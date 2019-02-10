package com.example.alphapav.book.util;


import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class SharedHelper {
    private Context mContext;
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String APP = "app";
    public static final String TEMPUSERNAME = "tempusername";
    public static final String LOGINTIME = "logintime";
    public SharedHelper() {
    }
    public SharedHelper(Context context)
    {
        mContext= context;
    }

    public void save(String key, String value){
        // get edit
        SharedPreferences sp = mContext.getSharedPreferences( APP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        //save
        editor.putString(key, value);
        editor.apply();
    }

    public String get(String key){
        // get key
        SharedPreferences sp = mContext.getSharedPreferences(APP, Context.MODE_PRIVATE);
        String value = sp.getString(key, "");
        if(value.equals(""))
            return null;
        return  value;
    }
}
