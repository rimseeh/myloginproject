package com.example.designandloginproject;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.example.designandloginproject.application.MyApplication;
import com.example.designandloginproject.fragments.AccessoryGridFragment;
import com.example.designandloginproject.fragments.LoginFragment;
import com.example.designandloginproject.network.ConnectivityReceiver;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
/**
 * Main activity (the only activity) which displays all the fragments inside.
 */
public class MainActivity extends AppCompatActivity implements NavigationHost {

    //broadcast receiver for internet access availability
    private BroadcastReceiver mNetworkReceiver;
    //firebase authentication to check if user is logged in or not
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setting the application theme from the shared preferences
        if (MyApplication.getInstance().getInt("Mode", AppCompatDelegate.MODE_NIGHT_NO)
                == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.Theme);
        }

        //loading the main activity layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //registering a BroadCast receiver for no internet access
        mNetworkReceiver = new ConnectivityReceiver();
        registerNetworkBroadcast();

        // start from the Accessory fragment if the user is logged in
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new AccessoryGridFragment())
                    .commit();
        }
        //start from the login fragment if user is already logged in and the email is valid
        else if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
        }
    }

    /**
     * Trigger a navigation to the specified fragment, optionally adding a transaction to the back
     * stack to make this navigation reversible.
     */
    @Override
    public void navigateTo(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.container, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    /**
     * Trigger a navigation to the specified fragment with animation,
     * optionally adding a transaction to the back
     * stack to make this navigation reversible.
     */
    @Override
    public void navigateToWithAnimation
    (Fragment fragment, boolean addToBackStack,
     int animationIn, int animationOut, int backAnimationIn, int backAnimationOut) {

        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .setCustomAnimations(animationIn, animationOut, backAnimationIn, backAnimationOut)
                        .replace(R.id.container, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    /**
     * register a broadcast Receiver listening to the internet access.
     */
    private void registerNetworkBroadcast() {
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    /**
     * removing a broadcast Receiver.
     */
    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * calling the unregisterNetworkChanges to remove the network broadcast Receiver.
     */
    @Override
    protected void onDestroy() {
        unregisterNetworkChanges();
        super.onDestroy();
    }
}
