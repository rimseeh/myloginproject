package com.example.designandloginproject;


//import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.os.Bundle;
//import android.util.Base64;
//import android.util.Log;

import com.example.designandloginproject.application.MyApplication;
import com.example.designandloginproject.fragments.AccessoryGridFragment;
import com.example.designandloginproject.fragments.LoginFragment;
import com.example.designandloginproject.network.ConnectivityReceiver;
import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.FirebaseFirestore;

//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.ImageFormat;
//import android.net.Uri;
//import android.os.Build;


public class MainActivity extends AppCompatActivity implements NavigationHost {
//    private FirebaseFirestore db;

//    private static final String TAG = "MainActivity";
    private BroadcastReceiver mNetworkReceiver;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyApplication myApplication = MyApplication.getInstance();
//        printKeyHash();
//        ArrayList<Accessory> accessories = (ArrayList<Accessory>) Accessory.initAccessoryEntryList(getResources());
//        Log.d(TAG, "onCreate: "+accessories.toString());
//        db = FirebaseFirestore.getInstance();
//        for(Accessory accessory: accessories) {
//            CollectionReference collectionReference = db.collection("accessory_images");
//            collectionReference.add(accessory)
//                    .addOnSuccessListener(documentReference -> Log.d(TAG, "onCreate: "+accessory.toString()))
//                    .addOnFailureListener(e -> Toast.makeText(MyApplication.getAppContext(), "model failed to be added", Toast.LENGTH_SHORT).show());
//        }

        mNetworkReceiver = new ConnectivityReceiver();
        registerNetworkBroadcast();
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new AccessoryGridFragment())
                    .commit();
        } else if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
        }
    }

//    private void printKeyHash() {
//        // Add code to print out the key hash
//        try {
//            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = getPackageManager().getPackageInfo("com.example.designandloginproject", PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            Log.e("KeyHash:", e.toString());
//        } catch (NoSuchAlgorithmException e) {
//            Log.e("KeyHash:", e.toString());
//        }
//    }


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

    @Override
    public void navigateToWithAnimation(Fragment fragment, boolean addToBackStack, int animationIn, int animationOut, int backAnimationIn, int backAnimationOut) {

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

    private void registerNetworkBroadcast() {
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
}
