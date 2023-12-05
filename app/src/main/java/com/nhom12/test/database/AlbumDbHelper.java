package com.nhom12.test.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class AlbumDbHelper extends SQLiteOpenHelper {
    private Context context;
    public static final String DATABASE_NAME = "AlbumPhoto.db";
    public static final int DATABASE_VERSION = 1;

    // Table Album
    public static final String TABLE_ALBUMS = "album";
    public static final String ALBUM_ID = "album_ID";
    public static final String ALBUM_NAME = "album_name";
    public static final String ALBUM_FIRST_IMAGE_ID = "album_first_img_id";

    // Table Image
    public static final String TABLE_IMAGES = "image";
    public static final String IMAGE_ID = "image_id";
    public static final String IMAGE_PATH = "image_path";
    public static final String IMAGE_DATE = "image_date";

    // Table Album_Image
    public static final String TABLE_ALBUM_IMAGE = "album_image";
    public static final String ID = "image_id";
    public static final String AI_ALBUM_ID = "ai_album_id";
    public static final String AI_IMAGE_ID = "ai_image_id";

    public AlbumDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Toast.makeText(context, "Tao Database", Toast.LENGTH_SHORT).show();

        // image_id theo mediastore
        String queryCreateImage =
                "CREATE TABLE " + TABLE_IMAGES
                        + " ( " +  IMAGE_ID + " INTEGER PRIMARY KEY, "
                        + IMAGE_PATH + " TEXT NOT NULL, "
                        + IMAGE_DATE + " TEXT NOT NULL );";

        String queryCreateAlbum =
                "CREATE TABLE " + TABLE_ALBUMS
                        + " ( " + ALBUM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        +  ALBUM_NAME + " TEXT NOT NULL, "
                        + ALBUM_FIRST_IMAGE_ID  + " INTEGER ,"
                        + "FOREIGN KEY (" + ALBUM_FIRST_IMAGE_ID + ") REFERENCES " +
                        TABLE_IMAGES + " (" + IMAGE_ID + "));";

        String queryCreateAlbumImage =
                "CREATE TABLE " + TABLE_ALBUM_IMAGE
                        + " ( " +  "ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + AI_ALBUM_ID + " INTEGER, "
                        + AI_IMAGE_ID + " INTEGER, "
                        + "FOREIGN KEY (" + AI_ALBUM_ID + ") REFERENCES " +
                        TABLE_ALBUMS + " (" + ALBUM_ID + "), "
                        + "FOREIGN KEY (" + AI_IMAGE_ID + ") REFERENCES " +
                        TABLE_IMAGES + " (" + IMAGE_ID + "));";

        db.execSQL(queryCreateImage);
        db.execSQL(queryCreateAlbum);
        db.execSQL(queryCreateAlbumImage);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALBUMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALBUM_IMAGE);
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

    public boolean checkAlbumImageExists(long albumId ,long imageId){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ALBUM_IMAGE +
//                " WHERE " + AI_ALBUM_ID + " = ? AND " + AI_IMAGE_ID + " = ?";
                " WHERE " + AI_IMAGE_ID + " = ? ";
        String[] selectionArgs = {String.valueOf(imageId)};
        Cursor cursor = null;
        cursor = db.rawQuery(query, selectionArgs);

        boolean isExists = cursor.getCount() > 0;
        cursor.close();
        return isExists;
    }

    public boolean checkAlbumImageExistsFull(long albumId ,long imageId){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ALBUM_IMAGE +
                " WHERE " + AI_ALBUM_ID + " = ? AND " + AI_IMAGE_ID + " = ?"; // Lay ca 2 truong
//                " WHERE " + AI_IMAGE_ID + " = ? ";
        String[] selectionArgs = {String.valueOf(albumId),String.valueOf(imageId)};
        Cursor cursor = null;
        cursor = db.rawQuery(query, selectionArgs);

        boolean isExists = cursor.getCount() > 0;
        cursor.close();
        return isExists;
    }

    public void addAlbum(String albumName, long albumFirstImageID){
        if(!checkAlbumExists(albumName)){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(ALBUM_NAME, albumName);
            cv.put(ALBUM_FIRST_IMAGE_ID, albumFirstImageID);
            long result = db.insert(TABLE_ALBUMS, null, cv);
        }
    }

    public void addImage(long imageID, String imagePath, String imageDate){
        if(!checkImageExists(imageID)){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(IMAGE_ID, imageID);
            cv.put(IMAGE_PATH, imagePath);
            cv.put(IMAGE_DATE, imageDate);
            long result = db.insert(TABLE_IMAGES, null, cv);
        }
    }

    public void addAlbumImage(long albumID, long imageID){
        if(!checkAlbumImageExists(albumID, imageID)){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(AI_ALBUM_ID, albumID);
            cv.put(AI_IMAGE_ID, imageID);
            long result = db.insert(TABLE_ALBUM_IMAGE, null, cv);
        }
    }

    public long getAlbumIdByAlbumName(String albumName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + ALBUM_ID + " FROM " + TABLE_ALBUMS + " WHERE " + ALBUM_NAME + " = ?";
        String[] selectionArgs = {albumName};
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, selectionArgs);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getLong(0); // GET COLUMN ALBUM_ID
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return -1;
    }

    public String getImagePathByImageId(long imageId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + IMAGE_PATH + " FROM " + TABLE_IMAGES + " WHERE " + IMAGE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(imageId)};
        Cursor cursor = null;

        if (db != null) {
            cursor = db.rawQuery(query, selectionArgs);
            if(cursor != null && cursor.moveToFirst()){
                return cursor.getString(0);
            }
        }

        return "";
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

    public Cursor readImageByAlbumID(long albumID){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + TABLE_IMAGES + ".*, ?" +
                " FROM " + TABLE_IMAGES +
                " INNER JOIN " + TABLE_ALBUM_IMAGE +
                " ON " + TABLE_IMAGES + "." + IMAGE_ID + " = " + TABLE_ALBUM_IMAGE + "." + AI_IMAGE_ID +
                " WHERE " + TABLE_ALBUM_IMAGE + "." + AI_ALBUM_ID + " = ?" +
                " ORDER BY " + IMAGE_DATE + " DESC";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{String.valueOf(albumID), String.valueOf(albumID)});
        }

        return cursor;
    }

    public Cursor readImageByAlbumIDAndByDate(long albumID, long startOfMonth, long endOfMonth){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + TABLE_IMAGES + ".*, ?" +
                " FROM " + TABLE_IMAGES +
                " INNER JOIN " + TABLE_ALBUM_IMAGE +
                " ON " + TABLE_IMAGES + "." + IMAGE_ID + " = " + TABLE_ALBUM_IMAGE + "." + AI_IMAGE_ID +
                " WHERE " + TABLE_ALBUM_IMAGE + "." + AI_ALBUM_ID + " = ?" +
                " AND " + TABLE_IMAGES + "." + IMAGE_DATE + " >= ?" +
                " AND " + TABLE_IMAGES + "." + IMAGE_DATE + " < ?" +
                " ORDER BY " + IMAGE_DATE + " DESC";;

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{String.valueOf(albumID) ,String.valueOf(albumID), String.valueOf(startOfMonth), String.valueOf(endOfMonth)});
        }

        return cursor;
    }

    public void moveImageToAlbum(long imageID, long albumIDCurrent, long albumID){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(AI_ALBUM_ID, albumID);

        String selections = AI_IMAGE_ID + " = ? AND " + AI_ALBUM_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(imageID), String.valueOf(albumIDCurrent)};
//        if(!checkAlbumImageExistsFull(albumID, imageID)){
//            db.update(TABLE_ALBUM_IMAGE, cv, selections, selectionArgs);
//        }
        db.update(TABLE_ALBUM_IMAGE, cv, selections, selectionArgs);
    }

    public void copyImageToAlbum(long imageID, long newAlbumID){
        if(!checkAlbumImageExistsFull(newAlbumID, imageID)){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(AI_ALBUM_ID, newAlbumID);
            cv.put(AI_IMAGE_ID, imageID);
            long result = db.insert(TABLE_ALBUM_IMAGE, null, cv);
        }
    }

    public void updateAlbumFirstImage(long albumID, long imageFirstImageID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(ALBUM_FIRST_IMAGE_ID, imageFirstImageID);

        String selection = ALBUM_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(albumID)};

        if (db != null) {
            db.update(TABLE_ALBUMS, cv, selection, selectionArgs);
        }
    }

    public void deleteImageById(long imageId){
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = IMAGE_ID + " = ?";
        String selection2 = AI_IMAGE_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(imageId)};

        db.delete(TABLE_IMAGES, selection, selectionArgs);
        db.delete(TABLE_ALBUM_IMAGE, selection2, selectionArgs);
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

    public long getAlbumIdByImageId(long imageId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + AI_ALBUM_ID +
                " FROM " + TABLE_ALBUM_IMAGE +
                " WHERE " + AI_IMAGE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(imageId)};
        Cursor cursor = null;

        cursor = db.rawQuery(query, selectionArgs);
        if (cursor != null && cursor.moveToFirst()) {
            return cursor.getLong(0);
        }

        return -1; // Trả về -1 nếu không tìm thấy album_id cho image_id
    }

    public Cursor readAllImages(){
        String query = "SELECT * FROM " + TABLE_IMAGES +
        " ORDER BY " + IMAGE_DATE + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor readImageByDate(long startOfMonth, long endOfMonth){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + TABLE_IMAGES + ".*" +
                " FROM " + TABLE_IMAGES +
                " WHERE " + TABLE_IMAGES + "." + IMAGE_DATE + " >= ?" +
                " AND " + TABLE_IMAGES + "." + IMAGE_DATE + " < ?" +
                " ORDER BY " + IMAGE_DATE + " DESC";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{String.valueOf(startOfMonth), String.valueOf(endOfMonth)});
        }

        return cursor;
    }
    public long findFirstImageIDAlbum(long albumId) {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT " + TABLE_IMAGES + "." + IMAGE_ID +
                " FROM " + TABLE_ALBUM_IMAGE +
                " JOIN " + TABLE_IMAGES + " ON " + TABLE_ALBUM_IMAGE + "." + AI_IMAGE_ID + " = " + TABLE_IMAGES + "." + IMAGE_ID +
                " WHERE " + TABLE_ALBUM_IMAGE + "." + AI_ALBUM_ID + " = " + albumId +
                " ORDER BY " + TABLE_IMAGES + "." + IMAGE_DATE + " DESC LIMIT 1";

        Cursor cursor = db.rawQuery(query, null);

        int firstImageIdAlbum = -1;
        if (cursor.moveToFirst()) {
            firstImageIdAlbum = cursor.getInt(cursor.getColumnIndex(IMAGE_ID));
        }

        return firstImageIdAlbum;
    }


}
