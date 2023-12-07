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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

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

    private static final String ARG_PARAM1 = "par1";
    private long albumID = 1;
    ArrayList<Cursor> rs = new ArrayList<>();
    MainActivity main;
    RecyclerView recyclerView;
    AlbumDbHelper albumDbHelper;
    Toolbar mToolbar;
    ListAlbumImageAdapter listAlbumImageAdapter;
    

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
                listAlbumImageAdapter.setData(rs);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment__favorite, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewFavor);
        rs.clear();
        listImages(1);
        listAlbumImageAdapter = new ListAlbumImageAdapter(main, rs, this);
        recyclerView.setAdapter(listAlbumImageAdapter);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(main);
        recyclerView.setLayoutManager(linearLayoutManager);

        mToolbar = rootView.findViewById(R.id.toolbar_favorite);

        mToolbar.setOnMenuItemClickListener(item -> {

            int id = item.getItemId();

            if (id == R.id.multi_select) {
                Toast.makeText(getActivity(), "Choose", Toast.LENGTH_LONG).show();
                return true;
            } else if (id == R.id.search) {
                showDatePickerDialog(item);
                return true;
            } else
                return false;

        });



        return rootView;
    }
    private void listImages(long albumID) {
        Cursor result = albumDbHelper.readImageByAlbumID(albumID);
        int position = 0;
        int preyear = 0, premonth = 0, preday = 0;
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
    private void showDatePickerDialog(@NonNull MenuItem item) {
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(i, i1, i2);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String date = simpleDateFormat.format(calendar.getTime());
                showImageByDate(date);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void showImageByDate(String date) {
        rs.clear();

        // Chuyển đổi chuỗi ngày thành các thành phần riêng lẻ (năm, tháng, ngày)
        String[] dateComponents = date.split("-");
        if (dateComponents.length == 3) {
            int day = Integer.parseInt(dateComponents[0]);
            int month = Integer.parseInt(dateComponents[1]);
            int year = Integer.parseInt(dateComponents[2]);

            //Cursor imageCursor = albumDbHelper.readImageByAlbumIDAndByDate(1, date);
            //rs.add(imageCursor);
            Toast.makeText(getContext(), "Images for date: " + day + month + year, Toast.LENGTH_LONG).show();
            listAlbumImageAdapter = new ListAlbumImageAdapter(main, rs, getParentFragment());
            recyclerView.setAdapter(listAlbumImageAdapter);

            // Cấu hình LayoutManager
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(main);
            recyclerView.setLayoutManager(linearLayoutManager);
        } else {
            // Xử lý trường hợp không thành công khi chuyển đổi ngày
            Toast.makeText(getContext(), "Invalid date format", Toast.LENGTH_SHORT).show();
        }
    }


}