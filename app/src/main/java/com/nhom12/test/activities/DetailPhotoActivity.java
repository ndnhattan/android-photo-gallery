package com.nhom12.test.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItem;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.nhom12.test.DataLocalManager;
import com.nhom12.test.R;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DetailPhotoActivity extends AppCompatActivity{
    ImageView detailImage;
    Toolbar mToolbar;
    TextView txtDate, txtTime;
    boolean flag = true;
    BottomNavigationView navigation;
  
    private List<String> listFavorImgPath;
    private int pos;
    AlbumDbHelper albumDbHelper;
    //FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_photo);

        //set db
        albumDbHelper = DatabaseSingleton.getInstance(this).getDbHelper();

        Intent intent = getIntent();
        String value = intent.getStringExtra("path");
        String imageDate = intent.getStringExtra("date");
        long imageId = intent.getLongExtra("id", 0); // dt
        long albumId = intent.getLongExtra("albumId", 0); // dt


        Instant instant = Instant.ofEpochMilli(Long.parseLong(imageDate) * 1000);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

        int year = zonedDateTime.getYear();
        int month = zonedDateTime.getMonthValue();
        int day = zonedDateTime.getDayOfMonth();
        Date date = new Date(Long.parseLong(imageDate) * 1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String formattedTime = dateFormat.format(date);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_detail_photo);
        MenuItem favoriteItem = mToolbar.getMenu().findItem(R.id.add_to_favor);
        if (albumDbHelper.checkAlbumImageExistsVer2(imageId, 1)) {
            isFavorite = true;
        }
        favoriteItem.setIcon(isFavorite ? R.drawable.icon_favorite_red : R.drawable.icon_favorite);
        mToolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.move_to_album) {
//                Fragment fragment = Fragment_Album_Choose.newInstance(imageId, albumId, false, true);
//                getSupportFragmentManager().beginTransaction().replace(R.id.body_container_detail, fragment).commit();
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
                if(getSupportFragmentManager().findFragmentByTag("ft_album_move") != null){
                    Toast.makeText(DetailPhotoActivity.this, "OK", Toast.LENGTH_SHORT).show();
                    getSupportFragmentManager().popBackStack("ft_album_move", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                else
                    onBackPressed();

            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int key = item.getItemId();
                if (key == R.id.move_to_album) {
                    Fragment fragment = Fragment_Album_Choose.newInstance(imageId, albumId, false, false);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.body_container_detail, fragment, "ft_album_move").addToBackStack("ft_album_move");
                    ft.commit();
                }
//                else if (key == R.id.copy_to_album){
//                    Fragment fragment = Fragment_Album_Choose.newInstance(imageId, albumId, false, false);
//                    ft.replace(R.id.body_container_detail, fragment).addToBackStack("ft_album_copy");
//                    ft.commit();
//                }
                if (key == R.id.add_to_favor) {
                    if (!albumDbHelper.checkAlbumImageExistsVer2(imageId, 1)) {
                        albumDbHelper.moveImageToAlbumFavor(imageId, albumId);
                        item.setIcon(R.drawable.icon_favorite_red);
                    } else {
                        albumDbHelper.removeImageFromAlbumFavor(imageId);
                        item.setIcon(R.drawable.icon_favorite);
                    }
                    Intent intent = new Intent();
                    intent.putExtra("isUpdate", true);
                    setResult(RESULT_OK, intent);
                }
                else if(key == R.id.btn_info){

                    Intent intent = new Intent();
                    intent.putExtra("isUpdate", true);
                    setResult(RESULT_OK, intent);
                    File imageFile = new File(value);
                    Uri targetUri = Uri.fromFile(imageFile);
                    if (targetUri != null) {
                        showInfo(targetUri, value);
                    }
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
        detailImage.setOnTouchListener(new OnSwipeTouchListener(this) {


            public void onClickUp() {
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
            public void onSwipeRight() {
                if (Fragment_Photo.index > 0) {
                    Fragment_Photo.index = Fragment_Photo.index - 1;
                    Fragment_Photo.result.moveToPosition(Fragment_Photo.index);
                    String path = Fragment_Photo.result.getString(1);
                    String imageDate = Fragment_Photo.result.getString(2);

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
                    String imageDate = Fragment_Photo.result.getString(2);

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
                Log.e("test", String.valueOf(Fragment_Photo.result.getPosition()));
                String path = Fragment_Photo.result.getString(1);
                Intent myIntent = new Intent(this, EditActivity.class);
                myIntent.putExtra("path", path);
                this.startActivity(myIntent);
            } else if (key == R.id.menu_detail_delete) {
                displayDialogAndRemove(imageId, albumId);
            } else if (key == R.id.menu_detail_share) {
                Drawable mDrawable = Drawable.createFromPath(value);
                Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "Image Description", null);

                Uri uri = Uri.parse(path);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(shareIntent, "Share Image"));
            } else if (key == R.id.menu_detail_hide) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailPhotoActivity.this);

                builder.setTitle("Confirm");
                if (!albumDbHelper.checkAlbumImageExistsVer2(imageId, 3)) {
                    builder.setMessage("Do you want to hide this image?");
                    builder.setPositiveButton("HIDE", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            albumDbHelper.moveImageToAlbum(imageId, albumId,3);

                            Toast.makeText(DetailPhotoActivity.this, "Image hidden", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.putExtra("isUpdate", true);
                            setResult(RESULT_OK, intent);
                            finish();
                            dialog.dismiss();
                        }
                    });
                }
                else{
                    builder.setMessage("Do you want to unhide this image?");
                    builder.setPositiveButton("UNHIDE", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            albumDbHelper.removeImageFromAlbumPrivate(imageId);

                            Toast.makeText(DetailPhotoActivity.this, "Unhide image successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.putExtra("isUpdate", true);
                            setResult(RESULT_OK, intent);
                            finish();
                            dialog.dismiss();
                        }
                    });
                }

                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
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
    public void displayDialogAndRemove(long imageId, long albumId) {
        final Dialog dialog = new Dialog(DetailPhotoActivity.this);
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
                albumDbHelper.moveImageToAlbum(imageId, albumId,2); // 2 la id mac dinh cua album remove
                long firstImageIDAlbumCurrent = albumDbHelper.findFirstImageIDAlbum(albumId);
                albumDbHelper.updateAlbumFirstImage(albumId,firstImageIDAlbumCurrent);
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

    private void showInfo(Uri imgUri, String value) {
        if (imgUri != null) {

            try {
                ExifInterface exifInterface = new ExifInterface(value);
                File file = new File(value);
                String imgName = file.getName();

                BottomSheetDialog infoBottomDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
                View infoDialogView = LayoutInflater.from(getApplicationContext())
                        .inflate(
                                R.layout.layout_info,
                                (LinearLayout) findViewById(R.id.infoContainer),
                                false
                        );
                TextView txtInfoProducer = (TextView) infoDialogView.findViewById(R.id.txtInfoProducer);
                TextView txtInfoReso = (TextView) infoDialogView.findViewById(R.id.txtInfoReso);
                TextView txtInfoModel = (TextView) infoDialogView.findViewById(R.id.txtInfoModel);


                TextView txtInfoTime = (TextView) infoDialogView.findViewById(R.id.txtInfoTime);
                TextView txtInfoName = (TextView) infoDialogView.findViewById(R.id.txtInfoName);

                txtInfoName.setText(imgName);
                txtInfoProducer.setText(exifInterface.getAttribute(ExifInterface.TAG_MAKE));
                //txtInfoSize.setText(exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH) + "x" + exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH));
                String length = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
                String width = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);

                int imageLength = Integer.parseInt(length);
                int imageWidth = Integer.parseInt(width);

                int resolution = imageLength * imageWidth;
                double megapixels = (double) resolution / 1000000.0;

                String megapixelsString = String.format("%.2f MP", megapixels);

                txtInfoReso.setText(megapixelsString);

                txtInfoModel.setText(exifInterface.getAttribute(ExifInterface.TAG_MODEL));

                txtInfoTime.setText(exifInterface.getAttribute(ExifInterface.TAG_DATETIME));

                infoBottomDialog.setContentView(infoDialogView);
                infoBottomDialog.show();


                //parcelFileDescriptor.close();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Something wrong:\n" + e.toString(),
                        Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Something wrong:\n" + e.toString(),
                        Toast.LENGTH_LONG).show();
            }


        } else {
            Toast.makeText(getApplicationContext(),
                    "image Uri == null",
                    Toast.LENGTH_LONG).show();
        }
    }
}