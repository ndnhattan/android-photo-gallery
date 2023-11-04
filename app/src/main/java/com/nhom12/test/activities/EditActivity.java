package com.nhom12.test.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nhom12.test.R;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class EditActivity extends AppCompatActivity {

    ImageView editImage;
    Toolbar mToolbar;
    TextView txtDate, txtTime;
    boolean flag = true;
    BottomNavigationView navigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        String value = intent.getStringExtra("path");

        mToolbar = (Toolbar) findViewById(R.id.toolbar_edit);

        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        editImage = (ImageView) findViewById(R.id.edit_ImageView);
        Bitmap bitmap = resizeImage(value, 0, 0);
        editImage.setImageBitmap(bitmap);

        navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag){
                    mToolbar.setVisibility(View.INVISIBLE);
                    navigation.setVisibility(View.INVISIBLE);
                    flag = false;
                }
                else{
                    mToolbar.setVisibility(View.VISIBLE);
                    navigation.setVisibility(View.VISIBLE);
                    flag = true;
                }
            }
        });
    }

    public Bitmap resizeImage(String imagePath, int targetWidth, int targetHeight) {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true; // Set this to true to calculate the dimensions only

        // Decode the image file to get its dimensions
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
}
