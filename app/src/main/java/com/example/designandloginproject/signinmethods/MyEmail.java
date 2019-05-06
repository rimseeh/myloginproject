package com.example.designandloginproject.signinmethods;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.designandloginproject.fragments.AccessoryGridFragment;
import com.example.designandloginproject.NavigationHost;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import androidx.annotation.NonNull;

public class MyEmail {
    private static final String TAG = "MyEmail";
    private static MyEmail ourInstance = null;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    FirebaseUser firebaseUser=null;
Activity activity;
    public static MyEmail getInstance(Activity activity) {
        if (ourInstance == null) {
            ourInstance = new MyEmail(activity);
        }
        return ourInstance;
    }

    private MyEmail(Activity activity) {
        this.activity=activity;
    }

    private Activity getActivity() {
        return activity;
    }

    public void signInWithEamil(ProgressBar progressBar, TextInputEditText emailEditText, TextInputEditText passwordEditText) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                firebaseUser = mAuth.getCurrentUser();
                                if (firebaseUser.isEmailVerified()) {
                                    Iterable<DataSnapshot> iterable = dataSnapshot.child("users").getChildren();
                                    for (DataSnapshot snapshot : iterable) {
                                        if (Objects.equals(snapshot.getKey(), mAuth.getUid())) {
                                            ((NavigationHost) getActivity()).navigateTo(new AccessoryGridFragment(), false); // Navigate to the grid Fragment
                                            break;
                                        }
                                    }
                                } else {
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
                        Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
