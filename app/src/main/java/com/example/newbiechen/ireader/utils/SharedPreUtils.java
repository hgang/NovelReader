package com.example.newbiechen.ireader.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.newbiechen.ireader.App;

/**
 * Created by newbiechen on 17-4-16.
 */

public class SharedPreUtils {
    private static final String SHARED_NAME = "IReader_pref";
    private static SharedPreUtils sInstance;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private SharedPreUtils() {
        mPreferences = App.getContext()
                .getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreUtils getInstance() {
        if (sInstance == null) {
            synchronized (SharedPreUtils.class) {
                if (sInstance == null) {
                    sInstance = new SharedPreUtils();
                }
            }
        }
        return sInstance;
    }

    private SharedPreferences.Editor getEditor() {
        if (mEditor == null) {
            mEditor = mPreferences.edit();
        }
        return mEditor;
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public String getString(String key, String def) {
        return mPreferences.getString(key, def);
    }

    public void putString(String key, String value) {
        getEditor().putString(key, value);
        getEditor().apply();
    }

    public void putInt(String key, int value) {
        getEditor().putInt(key, value);
        getEditor().apply();
    }

    public void putBoolean(String key, boolean value) {
        getEditor().putBoolean(key, value);
        getEditor().apply();
    }

    public int getInt(String key, int def) {
        return mPreferences.getInt(key, def);
    }

    public boolean getBoolean(String key, boolean def) {
        return mPreferences.getBoolean(key, def);
    }
}
