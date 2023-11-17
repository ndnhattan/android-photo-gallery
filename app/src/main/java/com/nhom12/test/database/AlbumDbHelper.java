package com.nhom12.test.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AlbumDbHelper extends SQLiteOpenHelper {
    private Context context;
    public static final String DATABASE_NAME = "AlbumPhoto.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_ALBUMS = "album";
    public static final String ALBUM_NAME = "album_name";
    public static final String ALBUM_FIRST_IMAGE = "album_first_img";

    public static final String TABLE_IMAGES = "image";
    public static final String IMAGE_ID = "image_id";
    public static final String IMAGE_PATH = "image_path";
    public static final String IMAGE_DATE = "image_date";
    public static final String IMAGE_ALBUM_NAME = "image_album_name";

    public AlbumDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryCreateAlbum =
                "CREATE TABLE " + TABLE_ALBUMS
                        + " ( " +  ALBUM_NAME + " TEXT PRIMARY KEY, "
                        + ALBUM_FIRST_IMAGE  + " TEXT NOT NULL);";

        // image_id theo mediastore
        String queryCreateImage =
                "CREATE TABLE " + TABLE_IMAGES
                        + " ( " +  IMAGE_ID + " INTEGER PRIMARY KEY, "
                        + IMAGE_PATH + " TEXT NOT NULL, "
                        + IMAGE_DATE + " TEXT NOT NULL, "
                        + IMAGE_ALBUM_NAME + " TEXT, "
                        + "FOREIGN KEY (" + IMAGE_ALBUM_NAME + ") REFERENCES " +
                        TABLE_ALBUMS + " (" + ALBUM_NAME + "));";

        db.execSQL(queryCreateAlbum);
        db.execSQL(queryCreateImage);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALBUMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
        onCreate(db);
    }

    public boolean checkAlbumExists(String albumName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_ALBUMS,
                new String[]{ALBUM_NAME},
                ALBUM_NAME + " = ?",
                new String[]{albumName},
                null, null, null
        );

        boolean isExists = cursor.getCount() > 0;
        cursor.close();
        return isExists;
    }

    public boolean checkImageExists(long imageId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_IMAGES,
                new String[]{IMAGE_ID},
                IMAGE_ID + " = ?",
                new String[]{String.valueOf(imageId)},
                null, null, null
        );

        boolean isExists = cursor.getCount() > 0;
        cursor.close();
        return isExists;
    }

    public void addAlbum(String albumName, String albumFirstImage){
        if(!checkAlbumExists(albumName)){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(ALBUM_NAME, albumName);
            cv.put(ALBUM_FIRST_IMAGE, albumFirstImage);
            long result = db.insert(TABLE_ALBUMS, null, cv);
        }
    }

    public void addImage(long imageID, String imagePath, String imageDate, String imageAlbumName){
        if(!checkImageExists(imageID)){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(IMAGE_ID, imageID);
            cv.put(IMAGE_PATH, imagePath);
            cv.put(IMAGE_DATE, imageDate);
            cv.put(IMAGE_ALBUM_NAME, imageAlbumName);
            long result = db.insert(TABLE_IMAGES, null, cv);
        }
    }

    public Cursor readAllAlbum(){
        String query = "SELECT * FROM " + TABLE_ALBUMS;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor readImageByAlbum(String albumName){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_IMAGES +
                " WHERE " + IMAGE_ALBUM_NAME + " = ? "+
                " ORDER BY " + IMAGE_DATE + " DESC";;

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{albumName});
        }

        return cursor;
    }

    public Cursor readImageByAlbumAndByDate(String albumName, long startOfMonth, long endOfMonth){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_IMAGES +
                " WHERE " + IMAGE_ALBUM_NAME + " = ? "+
                " AND " + IMAGE_DATE + " >= ? AND " + IMAGE_DATE + " < ? " +
                " ORDER BY " + IMAGE_DATE + " DESC";;

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{albumName, String.valueOf(startOfMonth), String.valueOf(endOfMonth)});
        }

        return cursor;
    }

    public void moveImageToAlbum(long imageId, String newAlbumName){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(IMAGE_ALBUM_NAME, newAlbumName);

        String selections = IMAGE_ID + " = ? ";
        String[] selectionArgs = new String[]{String.valueOf(imageId)};
        db.update(TABLE_IMAGES, cv, selections, selectionArgs);
    }

    public void updateAlbumFirstImage(String albumName, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(ALBUM_FIRST_IMAGE, imagePath);

        String selection = ALBUM_NAME + " = ?";
        String[] selectionArgs = new String[]{albumName};

        if (db != null) {
            db.update(TABLE_ALBUMS, cv, selection, selectionArgs);
        }
    }

    public void deleteImageById(long imageId){
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = IMAGE_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(imageId)};

        db.delete(TABLE_IMAGES, selection, selectionArgs);
    }

    public Cursor getImageById(long imageId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_IMAGES + " WHERE " + IMAGE_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(imageId)};

        if (db != null) {
            return db.rawQuery(query, selectionArgs);
        }

        return null;
    }
}
