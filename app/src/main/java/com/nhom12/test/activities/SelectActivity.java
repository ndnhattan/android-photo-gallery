package com.nhom12.test.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nhom12.test.Fragment_Album_Choose;
import com.nhom12.test.Fragment_Photo;
import com.nhom12.test.R;
import com.nhom12.test.adapter.ListImageAdapter;
import com.nhom12.test.adapter.ListImageSelectAdapter;
import com.nhom12.test.database.AlbumDbHelper;
import com.nhom12.test.database.DatabaseSingleton;

import java.io.File;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

public class SelectActivity extends AppCompatActivity {
    Toolbar mToolbar;
    RecyclerView recyclerView;
    ArrayList<Cursor> rs = new ArrayList<>();
    AlbumDbHelper albumDbHelper;

    public static Cursor result;
    public static ArrayList<Integer> indexArr = new ArrayList<>();
    public static ArrayList<Integer> checkedArr = new ArrayList<>();
    ListImageSelectAdapter listImageAdapter;
    CheckBox checkBox;

    BottomNavigationView navigation;

    //handle remove checked
    long imageIdRemove;
    long albumIdRemove;
    long imageId, albumId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        Intent intent = getIntent();
        int index = intent.getIntExtra("index", -1);
        if(index >= 0){
            checkedArr.clear();
            checkedArr.add(index);
        }

        try {
            albumDbHelper = DatabaseSingleton.getInstance(this).getDbHelper();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        listImages();
        loadImages();
        listImageAdapter = new ListImageSelectAdapter(this, rs);
        recyclerView.setAdapter(listImageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_photo);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getSupportFragmentManager().findFragmentByTag("ft_album_move_multiple") != null){
                    Toast.makeText(SelectActivity.this, "OK", Toast.LENGTH_SHORT).show();
                    getSupportFragmentManager().popBackStack("ft_album_move_multiple", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                else
                    onBackPressed();
            }
        });



        //handle mToolbar
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int key = item.getItemId();
                if (key == R.id.move_to_album) {
                    Fragment fragment = Fragment_Album_Choose.newInstance(0, 0, false, true);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.body_container, fragment, "ft_album_move_multiple").addToBackStack("ft_album_move_multiple");
                    ft.commit();
                }
                return true;
            }
        });

        checkBox = (CheckBox) findViewById(R.id.toolbar_checkbox);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    checkedArr.clear();
                    for (int i = 0; i < result.getCount(); i++) {
                        checkedArr.add(i);
                    }
                    listImageAdapter.notifyDataSetChanged();
                }else {
                    checkedArr.clear();
                    listImageAdapter.notifyDataSetChanged();
                }
            }
        });

        // handle navigation
        navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(item -> {
            int key = item.getItemId();

            if (key == R.id.menu_detail_delete) {
                displayDialogAndRemove();
            }
            else if(key == R.id.menu_detail_hide){
                AlertDialog.Builder builder = new AlertDialog.Builder(SelectActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Do you want to hide these images?");
                builder.setPositiveButton("HIDE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        for(int i=0; i<checkedArr.size(); i++) {
                            result.moveToPosition(checkedArr.get(i));
                            imageId = result.getLong(0);
                            albumId = albumDbHelper.getAlbumIdByImageId(imageId);
                            albumDbHelper.moveImageToAlbum(imageId, albumId,3);
                        }
                        Intent intent = new Intent();
                        intent.putExtra("isUpdate", true);
                        setResult(RESULT_OK, intent);
                        finish();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
            else if(key == R.id.menu_detail_share){
                ArrayList<Uri> uris = new ArrayList<>();
                Intent intentShareMultiple = new Intent();
                intentShareMultiple.setAction(Intent.ACTION_SEND_MULTIPLE);
                intentShareMultiple.setType("image/*");
                for(int i=0; i<checkedArr.size(); i++) {
                    result.moveToPosition(checkedArr.get(i));
                    String imagePath = result.getString(1);
                    Drawable mDrawable = Drawable.createFromPath(imagePath);
                    Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();
                    String path = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "Image Description", null);

                    Uri uri = Uri.parse(path);
                    uris.add(uri);
                }
                intentShareMultiple.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                startActivity(Intent.createChooser(intentShareMultiple , "Share images"));
            }
            return true;
        });

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
                indexArr.add(position + rs.size() - 1);
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

        Cursor result = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder);
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

    public void displayDialogAndRemove() {
        final Dialog dialog = new Dialog(SelectActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_check);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        dialog.setCancelable(true);
        Button btnSubmit = dialog.findViewById(R.id.btnSubmit_check);
        Button btnExit = dialog.findViewById(R.id.btnExit_check);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0; i<checkedArr.size(); i++) {
                    result.moveToPosition(checkedArr.get(i));
                    imageIdRemove = result.getLong(0);
                    albumIdRemove = albumDbHelper.getAlbumIdByImageId(imageIdRemove);
                    albumDbHelper.moveImageToAlbum(imageIdRemove, albumIdRemove,2); // 2 la id mac dinh cua album remove
                    long firstImageIDAlbumCurrent = albumDbHelper.findFirstImageIDAlbum(albumIdRemove);
                    albumDbHelper.updateAlbumFirstImage(albumIdRemove,firstImageIDAlbumCurrent);
                }
                dialog.dismiss();

                Intent intent = new Intent();
                intent.putExtra("isUpdate", true);
                setResult(RESULT_OK, intent);
                finish();

            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}