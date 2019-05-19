package com.example.designandloginproject.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.example.designandloginproject.network.ConnectivityReceiver;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * This is a Singleton class and triggered when the application starts
 */
public class MyApplication extends Application {
    private static MyApplication instance;
    private static Context appContext;
    private SharedPreferences mSharedPreferences; // this is the instance of the SharedPreference were it reference to the sharedPreference Object
    private static final String SHARED_PREFERENCE_NAME = "MySharedPref"; // sharedPreference name
    private static final int MODE = Activity.MODE_PRIVATE; // mode for the shared preference this can be Activity.MODE_WORLD_READABLE or Activity.MODE_WORLD_WRITABLE

    /**
     * @return Return the application context
     */
    public static MyApplication getInstance() {
        return instance;
    }

    /**
     * @return Returns the Application Context
     */
    public static Context getAppContext() {
        return appContext;
    }

    /**
     * Executed when the application starts
     */
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appContext = getApplicationContext();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        initSharedPreferences();
    }

    /**
     * Listener for the Internet connectivity BroadCast Receiver
     */
    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    public Bitmap loadBitmapFromView(View view, int width, int height) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        Log.e("width", "=" + width);
        Log.e("height", "=" + height);
        return returnedBitmap;
    }

    /**
     * Used to initialize SharedPreferences
     */
    public void initSharedPreferences() {
        mSharedPreferences = getSharedPreferences(SHARED_PREFERENCE_NAME, MODE);
    }

    /**
     * Used to get the SharedPref instance
     */
    private SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

    /**
     * Used to get boolean
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        if (getSharedPreferences() != null)
            return getSharedPreferences().getBoolean(key, defaultValue);
        else
            return false;
    }

    /**
     * Used to save boolean
     */
    public boolean saveBoolean(String key, boolean value) {
        return getSharedPreferences().edit().putBoolean(key, value).commit();
    }

    /*Check Network*/


    /**
     * Used to get String
     */
    public String getString(String key, String defaultValue) {
        if (getSharedPreferences() != null)
            return getSharedPreferences().getString(key, defaultValue);
        else
            return "";
    }

    /**
     * Used to save String
     */
    public boolean saveString(String key, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    /**
     * Used to get Int
     */
    public int getInt(String key, int defaultValue) {
        if (getSharedPreferences() != null)
            return getSharedPreferences().getInt(key, defaultValue);
        else
            return 0;
    }

    /**
     * Used to save Int
     */
    public boolean saveInt(String key, int value) {
        return getSharedPreferences().edit().putInt(key, value).commit();
    }

}
