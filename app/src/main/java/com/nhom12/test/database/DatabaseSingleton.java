package com.nhom12.test.database;

import android.content.Context;

public class DatabaseSingleton {
    private static DatabaseSingleton instance;
    private AlbumDbHelper dbHelper;

    private DatabaseSingleton(Context context) {
        dbHelper = new AlbumDbHelper(context);
    }

    public static synchronized DatabaseSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseSingleton(context.getApplicationContext());
        }
        return instance;
    }

    public AlbumDbHelper getDbHelper() {
        return dbHelper;
    }
}
