package com.brighterbrain.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sandeep on 1/12/15.
 */
public class MySharedPreferences {



    private Context mcontext;
    private SharedPreferences sharedpreferences;

    public MySharedPreferences(Context context) {
        this.mcontext = context;
        sharedpreferences = context.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public void storeSharedValue(String key, String value)

    {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.apply();

    }

    public void storeSharedValue(String key, Boolean value)

    {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();

    }

    public String getSharedValue(String key) {
        return sharedpreferences.getString(key, "");
    }
    public Boolean getBooleanSharedValue(String key) {
        return sharedpreferences.getBoolean(key, false);
    }

    public String getAppLocationAddress(String key) {
        return sharedpreferences.getString(key, null);
    }

    public void clearSharedValue(String key) {

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove(key);
        editor.apply();

    }

    public void clearAllValues() {

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();

    }

    public boolean hasValue(String key) {

        return sharedpreferences.contains(key);
    }
}
