package com.nhom12.test;

import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.nhom12.test.adapter.ListAlbumImageAdapter;
import com.nhom12.test.adapter.ListFavorImageAdapter;
import com.nhom12.test.adapter.ListImageAdapter;
import com.nhom12.test.database.AlbumDbHelper;
import com.nhom12.test.database.DatabaseSingleton;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;


public class Fragment_Favorite extends Fragment {
<<<<<<< Updated upstream

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Favorite() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Love.
     */
    // TODO: Rename and change types and number of parameters

    Button favor;

=======
>>>>>>> Stashed changes
    private static final String ARG_PARAM1 = "par1";
    private long albumID = 1;
    ArrayList<Cursor> rs = new ArrayList<>();
    MainActivity main;
    RecyclerView recyclerView;
    AlbumDbHelper albumDbHelper;
    Toolbar mToolbar;
    ListFavorImageAdapter listFavorImageAdapter;
    public static ArrayList<Integer> indexArr = new ArrayList<>();

    public static Cursor result;
    

    public static Fragment_Favorite newInstance(long albumID) {
        Fragment_Favorite fragment = new Fragment_Favorite();
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
                listFavorImageAdapter.setData(rs);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment__favorite, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewFavor);
        rs.clear();
        listImages(1);
        listFavorImageAdapter = new ListFavorImageAdapter(main, rs, this);
        recyclerView.setAdapter(listFavorImageAdapter);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(main);
        recyclerView.setLayoutManager(linearLayoutManager);

        mToolbar = rootView.findViewById(R.id.toolbar_favorite);

        mToolbar.setOnMenuItemClickListener(item -> {

            int id = item.getItemId();

            if (id == R.id.multi_select) {
                Toast.makeText(getActivity(), "Choose", Toast.LENGTH_LONG).show();
                return true;
            } else if (id == R.id.search) {
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