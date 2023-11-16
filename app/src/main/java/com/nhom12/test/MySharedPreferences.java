package com.nhom12.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

public class MySharedPreferences {

    private static final String MY_SHARED_PREFERENCES = "TQT";
    private Context mContext;

    public MySharedPreferences(Context mContext) {
        this.mContext = mContext;
    }

    public void deleteListFavor (String key){
        SharedPreferences settings = mContext.getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        settings.edit().remove("PREF_IMG_FAVOR").commit();
    }

    public void putStringSet(String key, Set<String> values){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MY_SHARED_PREFERENCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(key,values);
        editor.apply();
    }
    public Set<String> getStringSet(String key){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MY_SHARED_PREFERENCES,Context.MODE_PRIVATE);
        Set<String> valueDefault = new HashSet<>();
        return sharedPreferences.getStringSet(key,valueDefault);
    }

}

