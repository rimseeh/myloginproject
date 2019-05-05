package com.example.designandloginproject.signinmethods;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.example.designandloginproject.AccessoryGridFragment;
import com.example.designandloginproject.NavigationHost;
import com.example.designandloginproject.R;
import com.example.designandloginproject.application.MyApplication;
import com.example.designandloginproject.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class MyGoogle {
    private static final String TAG = "MyGoogle";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Activity activity=null;
    FirebaseUser firebaseUser=null;
    private static MyGoogle ourInstance = null;
    private View view=null;

    public static MyGoogle getInstance(Activity activity,View view) {
        if (ourInstance == null) {
            ourInstance = new MyGoogle(activity, view);

        }

        return ourInstance;
    }

    private MyGoogle(Activity activity,View view) {
        this.activity=activity;
        this.view=view;
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener( activity, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        firebaseUser = mAuth.getCurrentUser();
                        ((NavigationHost) activity).navigateTo(new AccessoryGridFragment(), false); // Navigate to the grid Fragment
                        User user = new User();
                        user.setEmail(firebaseUser.getEmail());
                        ArrayList<String> strings = getFirstAndLastNameGoogle(acct);
                        user.setFirstName(strings.get(0));
                        user.setLastName(strings.get(1));
                        user.setPhone(firebaseUser.getPhoneNumber());
                        FirebaseDatabase.getInstance().getReference("users")
                                .child(mAuth.getCurrentUser().getUid())
                                .setValue(user).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Log.d(TAG, "handleFacebookAccessToken: ");
                            } else {
                                Log.e(TAG, "handleFacebookAccessToken: " + task1.getException().getMessage(), task1.getException());
                            }
                        });
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Snackbar.make(getView().findViewById(R.id.container), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                    }

                    // ...
                });
    }

    public View getView() {
        return view;
    }

    private ArrayList<String> getFirstAndLastNameGoogle(GoogleSignInAccount acct) {
        ArrayList<String> strings = new ArrayList<>();
        String fullname = acct.getDisplayName();
        String[] parts = fullname.split("\\s+");
        Log.d("Length-->", "" + parts.length);
        String firstname = null;
        String lastname = null;
        if (parts.length == 2) {
            firstname = parts[0];
            lastname = parts[1];
            Log.d("First-->", "" + firstname);
            Log.d("Last-->", "" + lastname);
        } else if (parts.length == 3) {
            firstname = parts[0];
            lastname = parts[2];
            Log.d("First-->", "" + firstname);
            Log.d("Last-->", "" + lastname);

        }
        strings.add(firstname);
        strings.add(lastname);
        return strings;
    }
}
