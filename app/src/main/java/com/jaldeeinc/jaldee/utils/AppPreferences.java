package com.jaldeeinc.jaldee.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.jaldeeinc.jaldee.common.MyApplication.getContext;

public class AppPreferences {

    private Context context;

    public AppPreferences() {
    }

    public AppPreferences(Context context) {
        this.context = context;
    }

    public static SharedPreferences getAppSharedPreferences() {
        return getContext().sharedPreferences;
    }



    public static void setString(SharedPreferences sharedPreferences, String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void setBoolean(SharedPreferences sharedPreferences, String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void setInt(SharedPreferences sharedPreferences, String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void setString(String key, String value) {
        SharedPreferences.Editor editor = getAppSharedPreferences().edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getAppSharedPreferences().edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void setInt(String key, int value) {
        SharedPreferences.Editor editor = getAppSharedPreferences().edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static String getString(SharedPreferences sharedPreferences, String key) {
        return sharedPreferences.getString(key, null);
    }

    public static boolean getBoolean(SharedPreferences sharedPreferences, String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public static int getInt(SharedPreferences sharedPreferences, String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public static String getString(String key) {
        return getAppSharedPreferences().getString(key, null);
    }

    public static boolean getBoolean(String key) {
        return getAppSharedPreferences().getBoolean(key, false);
    }

    public static int getInt(String key) {
        return getAppSharedPreferences().getInt(key, 0);
    }


    public static void delete(SharedPreferences sharedPreferences, String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }

    public static void deleteSharedPreferences() {
        SharedPreferences.Editor editor = getAppSharedPreferences().edit();
        editor.clear().commit();
    }
}
