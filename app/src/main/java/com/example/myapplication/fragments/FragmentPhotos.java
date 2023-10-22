package com.example.myapplication.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.customAdapter.ImageAdapter;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FragmentPhotos extends Fragment {
    MainActivity main;
    RecyclerView recyclerView;
    ArrayList<Cursor> rs = new ArrayList<>();

    public static FragmentPhotos newInstance(String strArg) {
        FragmentPhotos fragment = new FragmentPhotos();
        Bundle args = new Bundle();
        args.putString("strArg1", strArg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            main = (MainActivity) getActivity();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout_photos = (LinearLayout) inflater.inflate(R.layout.layout_photos, null);

        recyclerView = (RecyclerView) layout_photos.findViewById(R.id.recyclerView);
        listImages();
        recyclerView.setAdapter(new ImageAdapter(main, rs));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(main);

        recyclerView.setLayoutManager(linearLayoutManager);

        return layout_photos;
    }

    private void listImages() {
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, // Path to the image file
                MediaStore.Images.Media.DISPLAY_NAME, // Name of the image file
                MediaStore.Images.Media.DATE_ADDED // Date when the image was added
        };
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC"; // Sort by date added in descending order

        Cursor result = main.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder);
        int position = 0;
        int preyear = 0, premonth = 0;
        while (result.moveToNext()) {
            int dateColumnIndex = result.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
            String imageDate = result.getString(dateColumnIndex);
            Timestamp tms = new Timestamp(Long.parseLong(imageDate) * 1000);
            Date date = new Date(tms.getTime());
            int year = date.getYear() + 1900;
            int month = date.getMonth() + 1;
            if (preyear != 0 && (preyear < year || (preyear == year && premonth <= month))) {
                continue;
            }
            preyear = year;
            premonth = month;

            String selection = MediaStore.Images.Media.DATE_ADDED + " >= ? AND " + MediaStore.Images.Media.DATE_ADDED + " < ?";

            // Calculate the timestamp for the start of the selected month
            long startOfMonth = getStartOfMonthTimestamp(year, month);

            // Calculate the timestamp for the end of the selected month
            long endOfMonth = getEndOfMonthTimestamp(year, month);

            // Define the selection arguments
            String[] selectionArgs = {String.valueOf(startOfMonth), String.valueOf(endOfMonth)};
            Cursor monthImage = main.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder);

            Log.d("ImageLoader", "Year: " + String.valueOf(year));
            Log.d("ImageLoader", "Month: " + String.valueOf(month));
            Log.d("ImageLoader", "Length: " + String.valueOf(monthImage.getCount()));


            rs.add(monthImage);
            position += monthImage.getCount() - 1;
            result.moveToPosition(position);
        }
    }

    private static long getStartOfMonthTimestamp(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1); // Month is 0-based
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis() / 1000; // Convert to seconds
    }

    // Helper method to calculate the timestamp for the end of the selected month
    private static long getEndOfMonthTimestamp(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month); // Month is 0-based
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis() / 1000; // Convert to seconds
    }
}