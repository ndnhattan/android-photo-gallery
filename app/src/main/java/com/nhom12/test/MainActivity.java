package com.nhom12.test;

//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//
//import android.os.Bundle;
//
//import com.nhom12.test.databinding.ActivityMainBinding;
//
//
//public class MainActivity extends AppCompatActivity {
//
//    ActivityMainBinding binding;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        replaceFragment(new Fragment_Photo());
//        binding.bottomNavigation.setBackground(null);
//
//        binding.bottomNavigation.setOnItemSelectedListener(item -> {
//            switch (item.getItemId()){
//                    case 1000002:
//                        replaceFragment(new Fragment_Photo()) ;
//                        break;
//                    case 1000005:
//                        replaceFragment(new Fragment_Album()) ;
//                        break;
//                    case 1000000:
//                        replaceFragment(new Fragment_Love()) ;
//                        break;
//                    case 1000004:
//                        replaceFragment(new Fragment_Private()) ;
//                        break;
//                }
//                return true;
//        });
//    }
//
//    private void replaceFragment(Fragment fragment){
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.frame_layout, fragment);
//        fragmentTransaction.commit();
//    }
//
//}

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.Manifest;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},121);
        }

        //this line hide statusbar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        navigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.body_container, new Fragment_Photo()).commit();
        navigationView.setSelectedItemId(R.id.menu_photo);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 121 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
        }
    }
}