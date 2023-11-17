package com.nhom12.test.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nhom12.test.Fragment_Album;
import com.nhom12.test.Fragment_Favorite;
import com.nhom12.test.Fragment_Photo;
import com.nhom12.test.Fragment_Private;
import com.nhom12.test.MainActivity;
import com.nhom12.test.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Random;

public class EditActivity extends AppCompatActivity {
    final int PIC_CROP = 1;

    ImageView editImage;
    Toolbar mToolbar;
    TextView txtDate, txtTime;
    boolean flag = true;
    BottomNavigationView navigation;
    ArrayList<Uri> imageUri = new ArrayList<>();
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        String value = intent.getStringExtra("path");
        imageUri.add(Uri.fromFile(new File(value)));

        mToolbar = (Toolbar) findViewById(R.id.toolbar_edit);

        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mToolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.save) {
                ContentResolver contentResolver = getContentResolver();
                try {
                    ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, imageUri.get(index));
                    Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                    String imagePath = createDirectoryAndSaveFile(bitmap, "gallery-" + new Timestamp(System.currentTimeMillis()) + ".jpeg");
                    MediaScannerConnection.scanFile(this, new String[]{imagePath}, null, null);
                    Intent i = new Intent(this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            } else if (id == R.id.undo) {
                if (index > 0) {
                    index--;
                    Glide.with(this).load(imageUri.get(index)).into(editImage);
                }
                return true;
            } else if (id == R.id.redo) {
                if (index < imageUri.size()-1) {
                    index++;
                    Glide.with(this).load(imageUri.get(index)).into(editImage);
                }
                return true;
            }
            return false;
        });

        editImage = (ImageView) findViewById(R.id.edit_ImageView);
        Bitmap bitmap = resizeImage(value, 0, 0);
        editImage.setImageBitmap(bitmap);

        navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(item -> {
            int key = item.getItemId();
            if (key == R.id.menu_edit_crop) {
                CropImage.activity(imageUri.get(index)).start(this);
            }
            return true;
        });
        editImage.setOnClickListener(new View.OnClickListener() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (data != null) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                Glide.with(this).load(resultUri).into(editImage);
                index++;
                imageUri.add(index, resultUri);
                imageUri = new ArrayList<>(imageUri.subList(0, index+1));

            }
        }
    }

    private String createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

        File direct = new File(Environment.getExternalStorageDirectory() + "/Pictures");

        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/Pictures/");
            wallpaperDirectory.mkdirs();
        }

        File file = new File("/sdcard/Pictures/", fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }
}
