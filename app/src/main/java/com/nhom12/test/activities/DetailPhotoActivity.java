package com.nhom12.test.activities;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nhom12.test.Fragment_Album;
import com.nhom12.test.Fragment_Album_Choose;
import com.nhom12.test.Fragment_Album_Photo;
import com.nhom12.test.Fragment_Favorite;
import com.nhom12.test.Fragment_Photo;
import com.nhom12.test.Fragment_Private;
import com.nhom12.test.MainActivity;
import com.nhom12.test.OnItemClickListener;
import com.nhom12.test.R;
import com.nhom12.test.adapter.GridAlbumAdapter;
import com.nhom12.test.database.AlbumDbHelper;
import com.nhom12.test.database.DatabaseSingleton;
import com.nhom12.test.structures.Album;
import com.nhom12.test.utils.OnSwipeTouchListener;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DetailPhotoActivity extends AppCompatActivity {
    ImageView detailImage;
    Toolbar mToolbar;
    TextView txtDate, txtTime;
    boolean flag = true;
    BottomNavigationView navigation;
    AlbumDbHelper albumDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_photo);

        //set db
        albumDbHelper = DatabaseSingleton.getInstance(this).getDbHelper();

        Intent intent = getIntent();
        String value = intent.getStringExtra("path");
        String imageDate = intent.getStringExtra("date");
        long imageId = intent.getLongExtra("id", 0); // dt
        Instant instant = Instant.ofEpochMilli(Long.parseLong(imageDate) * 1000);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

        int year = zonedDateTime.getYear();
        int month = zonedDateTime.getMonthValue();
        int day = zonedDateTime.getDayOfMonth();
        Date date = new Date(Long.parseLong(imageDate) * 1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String formattedTime = dateFormat.format(date);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_detail_photo);
        mToolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.move_to_album) {
                Fragment fragment = Fragment_Album_Choose.newInstance(imageId);
                getSupportFragmentManager().beginTransaction().replace(R.id.body_container_detail, fragment).commit();
                return true;
            } else
                return false;

        });

        txtDate = (TextView) findViewById(R.id.date);
        txtTime = (TextView) findViewById(R.id.time);
        txtDate.setText(String.valueOf(day) + " tháng " + String.valueOf(month) + ", năm " + String.valueOf(year));
        txtTime.setText(formattedTime);

        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int key = item.getItemId();
                if (key == R.id.favorite) {
                    Toast.makeText(DetailPhotoActivity.this, "click", Toast.LENGTH_SHORT).show();
                } else if (key == R.id.move_to_album) {
                    Fragment fragment = Fragment_Album_Choose.newInstance(imageId);
                    getSupportFragmentManager().beginTransaction().replace(R.id.body_container_detail, fragment).commit();
                }
                return true;
            }
        });

        detailImage = (ImageView) findViewById(R.id.detailImageView);
        Bitmap bitmap = resizeImage(value);
        detailImage.setImageBitmap(bitmap);


        navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(item -> {
            int key = item.getItemId();
            if (key == R.id.menu_photo) {

            }
            return true;
        });
        detailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    mToolbar.setVisibility(View.INVISIBLE);
                    navigation.setVisibility(View.INVISIBLE);
                    flag = false;
                } else {
                    mToolbar.setVisibility(View.VISIBLE);
                    navigation.setVisibility(View.VISIBLE);
                    flag = true;
                }
            }
        });
        detailImage.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeRight() {
                if (Fragment_Photo.index > 0) {
                    Fragment_Photo.index = Fragment_Photo.index - 1;
                    Fragment_Photo.result.moveToPosition(Fragment_Photo.index);
                    String path = Fragment_Photo.result.getString(1);
                    int dateColumnIndex = Fragment_Photo.result.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
                    String imageDate = Fragment_Photo.result.getString(dateColumnIndex);

                    Instant instant = Instant.ofEpochMilli(Long.parseLong(imageDate) * 1000);
                    ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
                    int year = zonedDateTime.getYear();
                    int month = zonedDateTime.getMonthValue();
                    int day = zonedDateTime.getDayOfMonth();
                    Date date = new Date(Long.parseLong(imageDate) * 1000);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                    String formattedTime = dateFormat.format(date);

                    txtDate.setText(String.valueOf(day) + " tháng " + String.valueOf(month) + ", năm " + String.valueOf(year));
                    txtTime.setText(formattedTime);
                    Bitmap bitmap = resizeImage(path);
                    detailImage.setImageBitmap(bitmap);
                }
            }

            public void onSwipeLeft() {
                if (Fragment_Photo.index < Fragment_Photo.result.getCount() - 1) {
                    Fragment_Photo.index = Fragment_Photo.index + 1;
                    Fragment_Photo.result.moveToPosition(Fragment_Photo.index);
                    String path = Fragment_Photo.result.getString(1);
                    int dateColumnIndex = Fragment_Photo.result.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
                    String imageDate = Fragment_Photo.result.getString(dateColumnIndex);

                    Instant instant = Instant.ofEpochMilli(Long.parseLong(imageDate) * 1000);
                    ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
                    int year = zonedDateTime.getYear();
                    int month = zonedDateTime.getMonthValue();
                    int day = zonedDateTime.getDayOfMonth();
                    Date date = new Date(Long.parseLong(imageDate) * 1000);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                    String formattedTime = dateFormat.format(date);

                    txtDate.setText(String.valueOf(day) + " tháng " + String.valueOf(month) + ", năm " + String.valueOf(year));
                    txtTime.setText(formattedTime);
                    Bitmap bitmap = resizeImage(path);
                    detailImage.setImageBitmap(bitmap);
                }
            }
        });

        navigation.setOnNavigationItemSelectedListener(item -> {
            int key = item.getItemId();

            if (key == R.id.menu_detail_edit) {
                Intent myIntent = new Intent(this, EditActivity.class);
                myIntent.putExtra("path", value);
                this.startActivity(myIntent);
            } else if(key == R.id.menu_detail_delete){
                displayDialogAndRemove(imageId);
            }

            return true;
        });

    }

    public Bitmap resizeImage(String imagePath) {
        Bitmap originalBitmap = BitmapFactory.decodeFile(imagePath);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        if (screenHeight <= originalBitmap.getHeight()) {
            int newHeight = screenHeight;
            int newWidth = (int) (originalBitmap.getWidth() * ((float) newHeight / originalBitmap.getHeight()));
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
            return resizedBitmap;
        } else {
            int newWidth = screenWidth;
            int newHeight = (int) (originalBitmap.getHeight() * ((float) newWidth / originalBitmap.getWidth()));
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
            return resizedBitmap;
        }
    }

    // Dialog for remove
    public void displayDialogAndRemove(long imageId){
        final Dialog dialog = new Dialog(DetailPhotoActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_check);
        Window window = dialog.getWindow();
        if(window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams windowAttributes= window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        dialog.setCancelable(true);
        Button btnSubmit = dialog.findViewById(R.id.btnSubmit_check);
        Button btnExit = dialog.findViewById(R.id.btnExit_check);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                albumDbHelper.moveImageToAlbum(imageId, "Remove");
                dialog.dismiss();
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

    public void onBackPressedExit() {
        finish(); // Kết thúc activity hiện tại
    }
}