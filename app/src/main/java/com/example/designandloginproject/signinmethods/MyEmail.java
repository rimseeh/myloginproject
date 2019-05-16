package com.example.designandloginproject.signinmethods;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.designandloginproject.fragments.AccessoryGridFragment;
import com.example.designandloginproject.NavigationHost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * A Singleton class which allow the user to login using email verification.
 */
public class MyEmail {
    private static final String TAG = "MyEmail";
    @SuppressLint("StaticFieldLeak")
    private static MyEmail ourInstance = null;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mFirebaseUser = null;
    private Activity mActivity;

    public static MyEmail getInstance(Activity activity) {
        if (ourInstance == null) {
            ourInstance = new MyEmail(activity);
        }
        return ourInstance;
    }

    /**
     * Private constructor to make a single object from MyEmail class
     */
    private MyEmail(Activity mActivity) {
        this.mActivity = mActivity;
    }

    /**
     * Getter for the Main Activity
     */
    private Activity getActivity() {
        return mActivity;
    }

    /**
     * Signing in with email and password
     *
     * @param progressBar a progress bar is only for progressing
     * @param email       email to sign in with
     * @param password    password to sign in with
     */
    public void signInWithEmail(ProgressBar progressBar, String email, String password) {
        //setting progress bar to visible
        progressBar.setVisibility(View.VISIBLE);
        // using firebase authentication for signing in
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    // setting progress bar to gone visibility because data is retrieved from firebase
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail: success");
                        FirebaseDatabase.
                                getInstance().
                                getReference().
                                addValueEventListener(new ValueEventListener() {
                                    //check if email is verified to login successfully
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        mFirebaseUser = mAuth.getCurrentUser();
                                        assert mFirebaseUser != null;
                                        // email is verified
                                        if (mFirebaseUser.isEmailVerified()) {
                                            // Navigate to the Accessory Grid fragment
                                            ((NavigationHost) getActivity())
                                                    .navigateTo(new AccessoryGridFragment(), false);
                                        }
                                        // email is not verified and must be verified first
                                        else {
                                            Toast.makeText(getActivity(), "Verify Email First", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                                    }
                                });
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(getActivity(), Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
