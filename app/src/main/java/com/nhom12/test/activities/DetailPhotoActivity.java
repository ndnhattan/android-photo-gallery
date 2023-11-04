package com.nhom12.test.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nhom12.test.Fragment_Album;
import com.nhom12.test.Fragment_Favorite;
import com.nhom12.test.Fragment_Photo;
import com.nhom12.test.Fragment_Private;
import com.nhom12.test.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_photo);

        Intent intent = getIntent();
        String value = intent.getStringExtra("path");
        String imageDate = intent.getStringExtra("date");
        Instant instant = Instant.ofEpochMilli(Long.parseLong(imageDate) * 1000);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

        int year = zonedDateTime.getYear();
        int month = zonedDateTime.getMonthValue();
        int day = zonedDateTime.getDayOfMonth();
        Date date = new Date(Long.parseLong(imageDate) * 1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String formattedTime = dateFormat.format(date);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_detail_photo);
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

        detailImage = (ImageView) findViewById(R.id.detailImageView);
        Bitmap bitmap = resizeImage(value);
        detailImage.setImageBitmap(bitmap);

        navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(item -> {
            int key = item.getItemId();
            if(key == R.id.menu_photo){

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
    }

    public Bitmap resizeImage(String imagePath) {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true; // Set this to true to calculate the dimensions only

        // Decode the image file to get its dimensions
        Bitmap originalBitmap = BitmapFactory.decodeFile(imagePath);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        Log.d("TAG", "resizeImage: " + screenHeight + screenWidth);

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
}