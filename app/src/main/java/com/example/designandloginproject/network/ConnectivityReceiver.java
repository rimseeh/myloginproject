package com.example.designandloginproject.network;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.designandloginproject.NavigationHost;
import com.example.designandloginproject.fragments.NetworkErrorFragment;
import com.example.designandloginproject.application.MyApplication;

public class ConnectivityReceiver extends BroadcastReceiver {
    private static final String TAG = "ConnectivityReceiver";
    public static ConnectivityReceiverListener connectivityReceiverListener;

    public ConnectivityReceiver() {
        super();
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {

        try
        {
            if (isConnected()) {
                //MainActivity.dialog(true);
                Log.e(TAG, "Online Connect Intenet ");

            } else {
                //MainActivity.dialog(false);
                ((NavigationHost) context).navigateTo(new NetworkErrorFragment(), false); // Navigate to the sign up Fragment
                Log.e(TAG, "Conectivity Failure !!! ");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static boolean isConnected() {
        try {
            ConnectivityManager cm = (ConnectivityManager) MyApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}
