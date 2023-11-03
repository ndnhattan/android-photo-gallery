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

        int newWidth = screenWidth;
        int newHeight = (int) (originalBitmap.getHeight() * ((float) newWidth / originalBitmap.getWidth()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);

//        // Calculate the inSampleSize (scaling factor)
//        options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight);
//
//        // Decode the image with the calculated inSampleSize
//        options.inJustDecodeBounds = false;
//        return BitmapFactory.decodeFile(imagePath, options);

        return  resizedBitmap;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than or equal to the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}
