package com.nhom12.test;

import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nhom12.test.adapter.ListAlbumImageAdapter;
import com.nhom12.test.adapter.ListImageAdapter;
import com.nhom12.test.database.AlbumDbHelper;
import com.nhom12.test.database.DatabaseSingleton;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Album_Photo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Album_Photo extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String albumName;

    //
    ArrayList<Cursor> rs = new ArrayList<>();
    MainActivity main;
    RecyclerView recyclerView;
    AlbumDbHelper albumDbHelper;

    public Fragment_Album_Photo() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Fragment_Album_Photo newInstance(String albumName) {
        Fragment_Album_Photo fragment = new Fragment_Album_Photo();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, albumName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            albumName = getArguments().getString(ARG_PARAM1);
        }
        setHasOptionsMenu(true);
        try {
            main = (MainActivity) getActivity();
            albumDbHelper = DatabaseSingleton.getInstance(main).getDbHelper();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment__album__photo, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        rs.clear();
        listImages(albumName); // tim nhung anh co cung album name luu vao rs
        recyclerView.setAdapter(new ListAlbumImageAdapter(main, rs));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(main);
        recyclerView.setLayoutManager(linearLayoutManager);

//        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar_photo);
//        mToolbar.setOnMenuItemClickListener(item -> {
//            int id = item.getItemId();
//
//            if (id == R.id.camera) {
//                Toast.makeText(getActivity(), "Camera", Toast.LENGTH_LONG).show();
//                return true;
//            } else if (id == R.id.search) {
//                Toast.makeText(getActivity(), "Search", Toast.LENGTH_LONG).show();
//                return true;
//            } else if(id == R.id.color){
//                Toast.makeText(getActivity(), "Color", Toast.LENGTH_LONG).show();
//                return true;
//            } else if(id == R.id.setting){
//                Toast.makeText(getActivity(), "Settings", Toast.LENGTH_LONG).show();
//                return true;
//            } else
//                return false;
//
//        });

        return rootView;
    }

    private void listImages(String albumName) {
        Cursor result = albumDbHelper.readImageByAlbum(albumName);
        int position = 0;
        int preyear = 0, premonth = 0, preday = 0;
        System.out.println(result.getCount());
        while (result.moveToNext()) {
            String imageDate = result.getString(2);
            Timestamp tms = new Timestamp(Long.parseLong(imageDate) * 1000);
            Date date = new Date(tms.getTime());
            int year = date.getYear() + 1900;
            int month = date.getMonth() + 1;
            int day = date.getDate();
            if (preyear != 0 &&
                    (preyear < year ||
                            (preyear == year && premonth < month) ||
                            (preyear == year && premonth == month && preday <= day))
            ) {
                continue;
            }
            preyear = year;
            premonth = month;
            preday = day;

            // Calculate the timestamp for the start of the selected month
            long startOfMonth = getStartOfMonthTimestamp(year, month, day);

            // Calculate the timestamp for the end of the selected month
            long endOfMonth = startOfMonth + 24 * 60 * 60;

            // Define the selection arguments
            Cursor monthImage = albumDbHelper.readImageByAlbumAndByDate(albumName, startOfMonth, endOfMonth);

            rs.add(monthImage);
            position += monthImage.getCount() - 1;
            result.moveToPosition(position);
        }
    }

    private static long getStartOfMonthTimestamp(int year, int month,int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1); // Month is 0-based
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis() / 1000; // Convert to seconds
    }
}