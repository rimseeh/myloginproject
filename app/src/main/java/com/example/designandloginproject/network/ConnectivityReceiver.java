package com.example.designandloginproject.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.designandloginproject.MainActivity;
import com.example.designandloginproject.NavigationHost;
import com.example.designandloginproject.NetworkErrorFragment;
import com.example.designandloginproject.SignUpFragment;
import com.example.designandloginproject.application.MyApplication;

import androidx.fragment.app.FragmentTransaction;

public class ConnectivityReceiver extends BroadcastReceiver {

    public static ConnectivityReceiverListener connectivityReceiverListener;

    public ConnectivityReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        try
        {
            if (isConnected()) {
                //MainActivity.dialog(true);
                Log.e("keshav", "Online Connect Intenet ");

            } else {
                //MainActivity.dialog(false);
                ((NavigationHost) context).navigateTo(new NetworkErrorFragment(), false); // Navigate to the sign up Fragment
                Log.e("keshav", "Conectivity Failure !!! ");
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
