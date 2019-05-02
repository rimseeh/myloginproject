package com.example.designandloginproject.SharedPreferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {
    private static MySharedPreferences sOurInstance = null;
    private SharedPreferences mSharedPreferences= null; // this is the instance of the SharedPreference were it reference to the sharedPreference Object
    private static final  String SHARED_PREFERENCE_NAME="MySharedPref"; // sharedPreference name
    private static final int MODE = Activity.MODE_PRIVATE; // mode for the shared preference this can be Activity.MODE_WORLD_READABLE or Activity.MODE_WORLD_WRITABLE


    //Get Or Initialize myInstance sharedPreferences if its not initialized

    public static MySharedPreferences getInstance(Context context) {
        if (sOurInstance==null){
            sOurInstance=new MySharedPreferences();
            sOurInstance.mSharedPreferences= context.getSharedPreferences(SHARED_PREFERENCE_NAME,MODE);
        }
        return sOurInstance;
    }

    //Empty constructor its private so nobody can make an object of it
    private MySharedPreferences() {
    }

    //Write String to shared preference
    public  boolean writeString (String key, String value){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key,value);
        return editor.commit();
    }

    public  boolean writeBoolean (String key, boolean value){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key,value);
        return editor.commit();
    }

    public String readString (String key, String defaultValue){
        return mSharedPreferences.getString(key,defaultValue);
    }
    public boolean readBoolean (String key, Boolean defaultValue){
        return mSharedPreferences.getBoolean(key,defaultValue);
    }



}
