package com.nhom12.test;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.nhom12.test.adapter.ListAlbumImageAdapter;
import com.nhom12.test.adapter.ListImageAdapter;
import com.nhom12.test.adapter.ListPrivateImageAdapter;
import com.nhom12.test.database.AlbumDbHelper;
import com.nhom12.test.database.DatabaseSingleton;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;


public class Fragment_Private_Access extends Fragment {

    private static final String ARG_PARAM1 = "par1";
    private long albumID = 3;
    ArrayList<Cursor> rs = new ArrayList<>();
    MainActivity main;
    RecyclerView recyclerView;
    AlbumDbHelper albumDbHelper;
    Toolbar mToolbar;
    ListPrivateImageAdapter listPrivateImageAdapter;

    public static ArrayList<Integer> indexArr = new ArrayList<>();

    public static Cursor result;
    public Fragment_Private_Access() {

    }

    public static Fragment_Private_Access newInstance(long albumID) {
        Fragment_Private_Access fragment = new Fragment_Private_Access();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, albumID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            albumID = getArguments().getLong(ARG_PARAM1);
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == main.RESULT_OK) {
            boolean isUpdate = data.getBooleanExtra("isUpdate", false);
            if(isUpdate){
                rs.clear();
                listImages(albumID);
                listPrivateImageAdapter.setData(rs);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment__private__access, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewPrivate);
        rs.clear();
        listImages(3);
        listPrivateImageAdapter = new ListPrivateImageAdapter(main, rs, this);
        recyclerView.setAdapter(listPrivateImageAdapter);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(main);
        recyclerView.setLayoutManager(linearLayoutManager);

        mToolbar = rootView.findViewById(R.id.toolbar_private_access);

        mToolbar.setOnMenuItemClickListener(item -> {

            int id = item.getItemId();

            if (id == R.id.multi_select) {
                Toast.makeText(getActivity(), "Choose", Toast.LENGTH_LONG).show();
                return true;
            } else if (id == R.id.search) {
                Toast.makeText(getActivity(), "Search", Toast.LENGTH_LONG).show();
                return true;
            } else if(id == R.id.change_password){
                Toast.makeText(getActivity(), "Change password", Toast.LENGTH_LONG).show();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.add(R.id.body_container, new Fragment_Change_Password());
                fr.addToBackStack(null);
                fr.commit();
                return true;
            } else
                return false;

        });
        return rootView;
    }
    private void listImages(long albumID) {
        result = albumDbHelper.readImageByAlbumID(albumID);
        int position = 0;
        int preyear = 0, premonth = 0, preday = 0;
        indexArr.clear();
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
            Cursor monthImage = albumDbHelper.readImageByAlbumIDAndByDate(albumID, startOfMonth, endOfMonth);

            rs.add(monthImage);
            indexArr.add(position + rs.size() -1 );
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