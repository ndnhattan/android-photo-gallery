package com.nhom12.test;


import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom12.test.adapter.ListImageAdapter;
import com.nhom12.test.database.AlbumDbHelper;
import com.nhom12.test.database.DatabaseSingleton;
import com.nhom12.test.structures.Album;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Fragment_Photo extends Fragment {
    Toolbar mToolbar;
    MainActivity main;
    RecyclerView recyclerView;
    ArrayList<Cursor> rs = new ArrayList<>();
    AlbumDbHelper albumDbHelper;

    public static Cursor result;
    public static ArrayList<Integer> indexArr = new ArrayList<>();
    public static int index;
    ListImageAdapter listImageAdapter;

    public static Fragment_Photo newInstance(String strArg) {
        Fragment_Photo fragment = new Fragment_Photo();
        Bundle args = new Bundle();
        args.putString("strArg1", strArg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                Toast.makeText(main, "Refresh", Toast.LENGTH_SHORT).show();
                rs.clear();
                loadImages();
                listImageAdapter.setData(rs);
            }
        }
    }

    private void loadImages() {
        result = albumDbHelper.readAllImages();
        int position = 0;
        int preyear = 0, premonth = 0, preday = 0;
        if (result != null) {
            result.moveToPosition(-1);
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
                Cursor monthImage = albumDbHelper.readImageByDate(startOfMonth, endOfMonth);

                rs.add(monthImage);
                indexArr.add(position);
                position += monthImage.getCount() - 1;
                result.moveToPosition(position);
            }
        }
    }

    private void listImages() {

        String[] projection = {
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA, // Path to the image file
                MediaStore.Images.Media.DATE_ADDED
        };
        String sortOrder = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " ASC, " + MediaStore.Images.Media.DATE_ADDED + " DESC"; // Sort by date added in descending order

        Cursor result = main.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder);
        String currentAlbumName = null;
        result.moveToPosition(-1);
        // Add album favorite
        albumDbHelper.addAlbum("Favorite", -1); // id 1
        // Add album remove
        albumDbHelper.addAlbum("Remove", -1); // id 2
        // Add album private
        albumDbHelper.addAlbum("Private", -1);
        while (result.moveToNext()) {
            int bucketNameColIndex = result.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            String bucketName = result.getString(bucketNameColIndex);
            int idColIndex = result.getColumnIndex(MediaStore.Images.Media._ID);
            long id = result.getLong(idColIndex);
            int imagesDataColIndex = result.getColumnIndex(MediaStore.Images.Media.DATA);
            String imagesData = result.getString(imagesDataColIndex);
            int dateColumnIndex = result.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
            String imageDate = result.getString(dateColumnIndex);

            if (currentAlbumName == null || !currentAlbumName.equals(bucketName)) {
                currentAlbumName = bucketName;
                albumDbHelper.addAlbum(bucketName, id);
            }
            long albumID = albumDbHelper.getAlbumIdByAlbumName(bucketName);
            albumDbHelper.addImage(id, imagesData, imageDate);
            albumDbHelper.addAlbumImage(albumID, id);
        }
    }

    private static long getStartOfMonthTimestamp(int year, int month, int day) {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment__photo, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        listImages();
        loadImages();
        listImageAdapter = new ListImageAdapter(main, rs, this);
        recyclerView.setAdapter(listImageAdapter);
//        recyclerView.setAdapter(new ListImageAdapter(main, rs, this));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(main);
        recyclerView.setLayoutManager(linearLayoutManager);

        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar_photo);
        mToolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.camera) {
                Toast.makeText(getActivity(), "Camera", Toast.LENGTH_LONG).show();
                return true;
            } else if (id == R.id.search) {
                Toast.makeText(getActivity(), "Search", Toast.LENGTH_LONG).show();
                return true;
            } else if (id == R.id.color) {
                Toast.makeText(getActivity(), "Color", Toast.LENGTH_LONG).show();
                return true;
            } else if (id == R.id.setting) {
                Toast.makeText(getActivity(), "Settings", Toast.LENGTH_LONG).show();
                return true;
            } else
                return false;

        });

        return rootView;
    }

}
    