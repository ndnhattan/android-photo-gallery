package com.nhom12.test.activities;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.OnLifecycleEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileSaveHelper implements LifecycleObserver {

    private final ContentResolver mContentResolver;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final MutableLiveData<FileMeta> fileCreatedResult = new MutableLiveData<>();
    private OnFileCreateResult resultListener;
    private final Observer<FileMeta> observer = fileMeta -> {
        if (resultListener != null) {
            resultListener.onFileCreateResult(
                    fileMeta.isCreated,
                    fileMeta.filePath,
                    fileMeta.error,
                    fileMeta.uri
            );
        }
    };

    public FileSaveHelper(ContentResolver contentResolver) {
        this.mContentResolver = contentResolver;
    }

    public FileSaveHelper(AppCompatActivity activity) {
        this(activity.getContentResolver());
        addObserver(activity);
    }

    private void addObserver(LifecycleOwner lifecycleOwner) {
        fileCreatedResult.observe(lifecycleOwner, observer);
        lifecycleOwner.getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void release() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    public void createFile(String fileNameToSave, OnFileCreateResult listener) {
        resultListener = listener;
        executor.submit(() -> {
            Cursor cursor = null;
            try {
                ContentValues newImageDetails = new ContentValues();
                Uri imageCollection = buildUriCollection(newImageDetails);
                Uri editedImageUri = getEditedImageUri(fileNameToSave, newImageDetails, imageCollection);

                cursor = mContentResolver.query(
                        editedImageUri,
                        new String[]{MediaStore.Images.Media.DATA},
                        null,
                        null,
                        null
                );
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String filePath = cursor.getString(columnIndex);

                updateResult(true, filePath, null, editedImageUri, newImageDetails);
            } catch (Exception ex) {
                ex.printStackTrace();
                updateResult(false, null, ex.getMessage(), null, null);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        });
    }

    @SuppressLint("InlinedApi")
    private Uri getEditedImageUri(String fileNameToSave, ContentValues newImageDetails, Uri imageCollection) throws IOException {
        newImageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, fileNameToSave);
        Uri editedImageUri = mContentResolver.insert(imageCollection, newImageDetails);
        if (editedImageUri != null) {
            mContentResolver.openOutputStream(editedImageUri).close();
        }
        return editedImageUri;
    }

    @SuppressLint("InlinedApi")
    private Uri buildUriCollection(ContentValues newImageDetails) {
        Uri imageCollection;
        if (isSdkHigherThan28()) {
            imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            newImageDetails.put(MediaStore.Images.Media.IS_PENDING, 1);
        } else {
            imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        return imageCollection;
    }

    @SuppressLint("InlinedApi")
    public void notifyThatFileIsNowPubliclyAvailable(ContentResolver contentResolver) {
        if (isSdkHigherThan28()) {
            executor.submit(() -> {
                FileMeta value = fileCreatedResult.getValue();
                if (value != null) {
                    if (value.imageDetails != null) {
                        value.imageDetails.clear();
                        value.imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0);
                        contentResolver.update(value.uri, value.imageDetails, null, null);
                    }
                }
            });
        }
    }

    private static class FileMeta {
        private final boolean isCreated;
        private final String filePath;
        private final Uri uri;
        private final String error;
        private final ContentValues imageDetails;

        public FileMeta(boolean isCreated, String filePath, Uri uri, String error, ContentValues imageDetails) {
            this.isCreated = isCreated;
            this.filePath = filePath;
            this.uri = uri;
            this.error = error;
            this.imageDetails = imageDetails;
        }
    }

    public interface OnFileCreateResult {
        void onFileCreateResult(boolean created, String filePath, String error, Uri uri);
    }

    private void updateResult(boolean result, String filePath, String error, Uri uri, ContentValues newImageDetails) {
        fileCreatedResult.postValue(new FileMeta(result, filePath, uri, error, newImageDetails));
    }

    public static boolean isSdkHigherThan28() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }

    public static String saveBitmap(Context context, Bitmap bitmap) {
        String savedImagePath = null;

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures");
        if (!storageDir.exists()) {
            File wallpaperDirectory = new File("/sdcard/Pictures/");
            wallpaperDirectory.mkdirs();
        }

        File imageFile;
        try {
            imageFile = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            savedImagePath = imageFile.getAbsolutePath();

            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return savedImagePath;
    }
}