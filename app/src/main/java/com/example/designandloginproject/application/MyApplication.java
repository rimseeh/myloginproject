package com.example.designandloginproject.application;

import android.app.Application;
import android.content.Context;

import com.example.designandloginproject.network.ConnectivityReceiver;

import androidx.appcompat.app.AppCompatDelegate;

public class MyApplication extends Application {
    private static MyApplication instance;
    private static Context appContext;

    public static MyApplication getInstance() {
        return instance;
    }

    public static Context getAppContext() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        appContext = getApplicationContext();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
