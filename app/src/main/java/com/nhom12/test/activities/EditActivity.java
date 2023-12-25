//package com.nhom12.test.activities;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.fragment.app.Fragment;
//
//import android.content.ActivityNotFoundException;
//import android.content.ContentResolver;
//import android.content.Context;
//import android.content.ContextWrapper;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.ImageDecoder;
//import android.media.MediaScannerConnection;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.bumptech.glide.Glide;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.nhom12.test.Fragment_Album;
//import com.nhom12.test.Fragment_Favorite;
//import com.nhom12.test.Fragment_Photo;
//import com.nhom12.test.Fragment_Private;
//import com.nhom12.test.MainActivity;
//import com.nhom12.test.R;
//import com.squareup.picasso.Picasso;
//import com.theartofdev.edmodo.cropper.CropImage;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.Random;
//
//public class EditActivity extends AppCompatActivity {
//    final int PIC_CROP = 1;
//
//    ImageView editImage;
//    Toolbar mToolbar;
//    TextView txtDate, txtTime;
//    boolean flag = true;
//    BottomNavigationView navigation;
//    ArrayList<Uri> imageUri = new ArrayList<>();
//    int index = 0;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_edit);
//
//        Intent intent = getIntent();
//        String value = intent.getStringExtra("path");
//        imageUri.add(Uri.fromFile(new File(value)));
//
//        mToolbar = (Toolbar) findViewById(R.id.toolbar_edit);
//
//        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
//        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//
//        mToolbar.setOnMenuItemClickListener(item -> {
//            int id = item.getItemId();
//            if (id == R.id.save) {
//                ContentResolver contentResolver = getContentResolver();
//                try {
//                    ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, imageUri.get(index));
//                    Bitmap bitmap = ImageDecoder.decodeBitmap(source);
//                    String imagePath = createDirectoryAndSaveFile(bitmap, "gallery-" + new Timestamp(System.currentTimeMillis()) + ".jpeg");
//                    MediaScannerConnection.scanFile(this, new String[]{imagePath}, null, null);
//                    Intent i = new Intent(this, MainActivity.class);
//                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(i);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return true;
//            } else if (id == R.id.undo) {
//                if (index > 0) {
//                    index--;
//                    Glide.with(this).load(imageUri.get(index)).into(editImage);
//                }
//                return true;
//            } else if (id == R.id.redo) {
//                if (index < imageUri.size()-1) {
//                    index++;
//                    Glide.with(this).load(imageUri.get(index)).into(editImage);
//                }
//                return true;
//            }
//            return false;
//        });
//
//        editImage = (ImageView) findViewById(R.id.edit_ImageView);
//        Bitmap bitmap = resizeImage(value, 0, 0);
//        editImage.setImageBitmap(bitmap);
//
//        navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
//        navigation.setOnNavigationItemSelectedListener(item -> {
//            int key = item.getItemId();
//            if (key == R.id.menu_edit_crop) {
//                CropImage.activity(imageUri.get(index)).start(this);
//            }
//            return true;
//        });
//        editImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (flag) {
//                    mToolbar.setVisibility(View.INVISIBLE);
//                    navigation.setVisibility(View.INVISIBLE);
//                    flag = false;
//                } else {
//                    mToolbar.setVisibility(View.VISIBLE);
//                    navigation.setVisibility(View.VISIBLE);
//                    flag = true;
//                }
//            }
//        });
//    }
//
//    public Bitmap resizeImage(String imagePath, int targetWidth, int targetHeight) {
////        BitmapFactory.Options options = new BitmapFactory.Options();
////        options.inJustDecodeBounds = true; // Set this to true to calculate the dimensions only
//
//        // Decode the image file to get its dimensions
//        Bitmap originalBitmap = BitmapFactory.decodeFile(imagePath);
//
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int screenWidth = displayMetrics.widthPixels;
//        int screenHeight = displayMetrics.heightPixels;
//
//        if (screenHeight <= originalBitmap.getHeight()) {
//            int newHeight = screenHeight;
//            int newWidth = (int) (originalBitmap.getWidth() * ((float) newHeight / originalBitmap.getHeight()));
//            Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
//            return resizedBitmap;
//        } else {
//            int newWidth = screenWidth;
//            int newHeight = (int) (originalBitmap.getHeight() * ((float) newWidth / originalBitmap.getWidth()));
//            Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
//            return resizedBitmap;
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            if (data != null) {
//                CropImage.ActivityResult result = CropImage.getActivityResult(data);
//                Uri resultUri = result.getUri();
//                Glide.with(this).load(resultUri).into(editImage);
//                index++;
//                imageUri.add(index, resultUri);
//                imageUri = new ArrayList<>(imageUri.subList(0, index+1));
//
//            }
//        }
//    }
//
//    private String createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {
//
//        File direct = new File(Environment.getExternalStorageDirectory() + "/Pictures");
//
//        if (!direct.exists()) {
//            File wallpaperDirectory = new File("/sdcard/Pictures/");
//            wallpaperDirectory.mkdirs();
//        }
//
//        File file = new File("/sdcard/Pictures/", fileName);
//        if (file.exists()) {
//            file.delete();
//        }
//        try {
//            FileOutputStream out = new FileOutputStream(file);
//            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
//            out.flush();
//            out.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return file.getAbsolutePath();
//    }
//}


package com.nhom12.test.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.nhom12.test.MainActivity;
import com.nhom12.test.activities.base.BaseActivity;
import com.nhom12.test.activities.filters.FilterListener;
import com.nhom12.test.activities.filters.FilterViewAdapter;
import com.nhom12.test.activities.tools.EditingToolsAdapter;
import com.nhom12.test.activities.tools.ToolType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import ja.burhanrashid52.photoeditor.SaveSettings;
import ja.burhanrashid52.photoeditor.ViewType;

import com.nhom12.test.R;
import com.theartofdev.edmodo.cropper.CropImage;


public class EditActivity extends BaseActivity implements OnPhotoEditorListener,
        View.OnClickListener, PropertiesBSFragment.Properties,
        EmojiBSFragment.EmojiListener, StickerBSFragment.StickerListener,
        EditingToolsAdapter.OnItemSelected, FilterListener {

    private PhotoEditor mPhotoEditor;
    private PhotoEditorView mPhotoEditorView;
    private PropertiesBSFragment mPropertiesBSFragment;
    private EmojiBSFragment mEmojiBSFragment;
    private StickerBSFragment mStickerBSFragment;
    private TextView mTxtCurrentTool;
    private Typeface mWonderFont;
    private RecyclerView mRvTools;
    private RecyclerView mRvFilters;
    private EditingToolsAdapter mEditingToolsAdapter = new EditingToolsAdapter(this);
    private FilterViewAdapter mFilterViewAdapter = new FilterViewAdapter(this);
    private ConstraintLayout mRootView;
    private ConstraintSet mConstraintSet = new ConstraintSet();
    private boolean mIsFilterVisible = false;

    ////////
    ArrayList<Uri> imageUri = new ArrayList<>();

    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_edit);
        initViews();
        mWonderFont = Typeface.createFromAsset(getAssets(), "beyond_wonderland.ttf");
        mPropertiesBSFragment = new PropertiesBSFragment();
        mEmojiBSFragment = new EmojiBSFragment();
        mStickerBSFragment = new StickerBSFragment();
        mStickerBSFragment.setStickerListener(this);
        mEmojiBSFragment.setEmojiListener(this);
        mPropertiesBSFragment.setPropertiesChangeListener(this);
        LinearLayoutManager llmTools = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvTools.setLayoutManager(llmTools);
        mRvTools.setAdapter(mEditingToolsAdapter);
        LinearLayoutManager llmFilters = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvFilters.setLayoutManager(llmFilters);
        mRvFilters.setAdapter(mFilterViewAdapter);



        //////////////////////////////////////


        Intent intent = getIntent();
        String value = intent.getStringExtra("path");
        imageUri.add(Uri.fromFile(new File(value)));

        Bitmap bitmap = resizeImage(value, 0, 0);
        mPhotoEditorView.getSource().setImageBitmap(bitmap);




        ///////////////////////////////////////////////



        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true)
                .build();
        mPhotoEditor.setOnPhotoEditorListener(this);

    }

    /////////////////////////////////////

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




    /////////////////////////////////////////

    private void initViews() {
        ImageView imgUndo, imgRedo, imgGallery, imgSave, imgClose;
        mPhotoEditorView = findViewById(R.id.photoEditorView);
        mTxtCurrentTool = findViewById(R.id.txtCurrentTool);
        mRvTools = findViewById(R.id.rvConstraintTools);
        mRvFilters = findViewById(R.id.rvFilterView);
        mRootView = findViewById(R.id.rootView);

        imgUndo = findViewById(R.id.imgUndo);
        imgUndo.setOnClickListener(this);
        imgRedo = findViewById(R.id.imgRedo);
        imgRedo.setOnClickListener(this);
        imgGallery = findViewById(R.id.imgGallery);
        imgGallery.setOnClickListener(this);
        imgSave = findViewById(R.id.imgSave);
        imgSave.setOnClickListener(this);
        imgClose = findViewById(R.id.imgClose);
        imgClose.setOnClickListener(this);
    }

    @Override
    public void onEditTextChangeListener(View rootView, String text, int colorCode) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(this, text, colorCode);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int colorCode) {
                mPhotoEditor.editText(rootView, inputText, colorCode);
                mTxtCurrentTool.setText(R.string.label_text);
            }
        });
    }

    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onAddViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onRemoveViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onClick(View view) {
        int key = view.getId();

        if(key == R.id.imgUndo){
            mPhotoEditor.undo();
        } else if (key == R.id.imgRedo) {
            mPhotoEditor.redo();
        } else if (key == R.id.imgSave) {
            saveImage();
        } else if (key == R.id.imgClose) {
            onBackPressed();
        } else if (key == R.id.imgGallery) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST);
        } else
            return;
    }

    //@SuppressWarnings("MissingPermission")
//    private void saveImage() {
//        ContentResolver contentResolver = getContentResolver();
//        try {
//            ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, imageUri.get(index));
//            //ImageDecoder.Source source = ImageDecoder.createSource(file);
//
//
//            Bitmap bitmap = ImageDecoder.decodeBitmap(source);
//            String imagePath = createDirectoryAndSaveFile(bitmap, "gallery-" + new Timestamp(System.currentTimeMillis()) + ".jpeg");
//            MediaScannerConnection.scanFile(this, new String[]{imagePath}, null, null);
//            Intent i = new Intent(this, MainActivity.class);
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(i);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


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


    @SuppressWarnings("MissingPermission")
    private void saveImage() {
        if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showLoading("Saving...");
            File file = new File(
                    Environment.getExternalStorageDirectory().toString() +
                            File.separator + new Timestamp(System.currentTimeMillis()) + ".jpeg");
            try {
                file.createNewFile();
                SaveSettings saveSettings = new SaveSettings.Builder()
                        .setClearViewsEnabled(true)
                        .setTransparencyEnabled(true)
                        .build();
                mPhotoEditor.saveAsFile(file.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
                    @Override
                    public void onSuccess(@NonNull String imagePath) {
                        hideLoading();
                        showSnackbar("Image Saved Successfully");
                        mPhotoEditorView.getSource().setImageURI(Uri.fromFile(new File(imagePath)));
                        Log.e("SaveSuccess", "Save Success");
                        MediaScannerConnection.scanFile(EditActivity.this, new String[]{imagePath}, null, null);
                        Intent i = new Intent(EditActivity.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }

                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        hideLoading();
                        showSnackbar("Failed to save Image");
                        Log.e("SaveFailed", "Save Failed");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                hideLoading();
                showSnackbar(e.getMessage());
            }
        }
    }


//    @SuppressWarnings("MissingPermission")
//    private void saveImage() {
//        if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            showLoading("Saving...");
//            File file = new File(
//                    Environment.getExternalStorageDirectory().toString() +
//                            File.separator + System.currentTimeMillis() + ".png");
//            try {
//                file.createNewFile();
//                SaveSettings saveSettings = new SaveSettings.Builder()
//                        .setClearViewsEnabled(true)
//                        .setTransparencyEnabled(true)
//                        .build();
//                mPhotoEditor.saveAsFile(file.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
//                    @Override
//                    public void onSuccess(@NonNull String imagePath) {
//                        hideLoading();
//                        showSnackbar("Image Saved Successfully");
//                        mPhotoEditorView.getSource().setImageURI(Uri.fromFile(new File(imagePath)));
//                        Log.e("SaveSuccess", "Save Success");
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        hideLoading();
//                        showSnackbar("Failed to save Image");
//                        Log.e("SaveFailed", "Save Failed");
//                    }
//                });
//            } catch (IOException e) {
//                e.printStackTrace();
//                hideLoading();
//                showSnackbar(e.getMessage());
//            }
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_REQUEST:
                    try {
                        mPhotoEditor.clearAllViews();
                        Uri uri = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        mPhotoEditorView.getSource().setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    if (data != null) {
                        CropImage.ActivityResult result = CropImage.getActivityResult(data);
                        Uri resultUri = result.getUri();
                        //Glide.with(this).load(resultUri).into(mPhotoEditorView);
                        mPhotoEditorView.getSource().setImageURI(resultUri);
                        index++;
                        imageUri.add(index, resultUri);
                        imageUri = new ArrayList<>(imageUri.subList(0, index+1));

                    }
                    break;
            }
        }
    }

    @Override
    public void onColorChanged(int colorCode) {
        mPhotoEditor.setBrushColor(colorCode);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onOpacityChanged(int opacity) {
        mPhotoEditor.setOpacity(opacity);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onBrushSizeChanged(int brushSize) {
        mPhotoEditor.setBrushSize(brushSize);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onEmojiClick(String emojiUnicode) {
        mPhotoEditor.addEmoji(emojiUnicode);
        mTxtCurrentTool.setText(R.string.label_emoji);
    }

    @Override
    public void onStickerClick(Bitmap bitmap) {
        mPhotoEditor.addImage(bitmap);
        mTxtCurrentTool.setText(R.string.label_sticker);
    }

    @Override
    public void isPermissionGranted(boolean isGranted, String permission) {
        if (isGranted) {
            saveImage();
        }
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you want to exit without saving image ?");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveImage();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create().show();
    }

    @Override
    public void onFilterSelected(PhotoFilter photoFilter) {
        mPhotoEditor.setFilterEffect(photoFilter);
    }

    @Override
    public void onToolSelected(ToolType toolType) {
        switch (toolType) {
            case BRUSH:
                mPhotoEditor.setBrushDrawingMode(true);
                mTxtCurrentTool.setText(R.string.label_brush);
                mPropertiesBSFragment.show(getSupportFragmentManager(), mPropertiesBSFragment.getTag());
                break;
            case TEXT:
                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this);
                textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                    @Override
                    public void onDone(String inputText, int colorCode) {
                        mPhotoEditor.addText(inputText, colorCode);
                        mTxtCurrentTool.setText(R.string.label_text);
                    }
                });
                break;
            case ERASER:
                mPhotoEditor.brushEraser();
                mTxtCurrentTool.setText(R.string.label_eraser);
                break;
            case FILTER:
                mTxtCurrentTool.setText(R.string.label_filter);
                showFilter(true);
                break;
            case EMOJI:
                mEmojiBSFragment.show(getSupportFragmentManager(), mEmojiBSFragment.getTag());
                //Uri uri = mPhotoEditorView;
                break;
            case STICKER:
                mStickerBSFragment.show(getSupportFragmentManager(), mStickerBSFragment.getTag());
                break;
            case CROP:
                CropImage.activity(imageUri.get(index)).start(this);


//                Intent intent = new Intent(this, CropImage.class);
//
//                CropImage.activity().start(this);


                break;
        }
    }

    private void showFilter(boolean isVisible) {
        mIsFilterVisible = isVisible;
        mConstraintSet.clone(mRootView);
        if (isVisible) {
            mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.START);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
            mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.END);
        }
        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootView, changeBounds);
        mConstraintSet.applyTo(mRootView);
    }

    @Override
    public void onBackPressed() {
        if (mIsFilterVisible) {
            showFilter(false);
            mTxtCurrentTool.setText(R.string.app_name);
        } else if (!mPhotoEditor.isCacheEmpty()) {
            showSaveDialog();
        } else {
            super.onBackPressed();
        }
    }


    private static final String TAG = EditActivity.class.getSimpleName();
    private static final int PICK_REQUEST = 53;


    @Override
    public void onTouchSourceImage(@Nullable MotionEvent motionEvent) {

    }
}
