package com.nhom12.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.Manifest;

import com.google.android.material.bottomnavigation.BottomNavigationView;
public class MainActivity extends AppCompatActivity implements MainCallBacks {

    BottomNavigationView navigationView;
    Fragment fragmentAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},121);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},122);
        }

        //this line hide statusbar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        navigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.body_container, new Fragment_Photo()).commit();
        navigationView.setSelectedItemId(R.id.menu_photo);

        navigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            int key = item.getItemId();
            if(key == R.id.menu_photo){
                fragment = new Fragment_Photo();
                // Add fragment photos
            }else if(key == R.id.menu_album){
                fragment = new Fragment_Album();
            }else if(key == R.id.menu_favorite){
                fragment = new Fragment_Favorite();
            }else
                fragment = new Fragment_Private();

            getSupportFragmentManager().beginTransaction().replace(R.id.body_container, fragment).commit();

            return true;
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 121 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
        }
        if (requestCode == 122 && grantResults[1]==PackageManager.PERMISSION_GRANTED) {
        }
    }

    @Override
    public void onMsgFromFragToMain(String sender, Fragment frmValue) {
        if(sender.equals("PHOTO")){
            fragmentAlbum = frmValue;
        }else if (sender.equals("ALBUM")){
            getSupportFragmentManager().beginTransaction().replace(R.id.body_container, fragmentAlbum).commit();
        }
    }
}