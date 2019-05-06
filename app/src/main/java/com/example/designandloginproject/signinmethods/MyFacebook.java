package com.example.designandloginproject.signinmethods;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.example.designandloginproject.fragments.AccessoryGridFragment;
import com.example.designandloginproject.NavigationHost;
import com.example.designandloginproject.models.User;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MyFacebook {
    private static final String TAG = "MyFacebook";

    private static MyFacebook ourInstance = null;
    Activity activity = null;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser;

    public static MyFacebook getInstance(Activity activity) {
        if (ourInstance == null) {
            ourInstance = new MyFacebook(activity);
        }
        return ourInstance;
    }

    private MyFacebook(Activity activity) {
        this.activity=activity;

    }

    private Activity getActivity() {
        return activity;
    }

    public void handleFacebookAccessToken(AccessToken token, LoginButton facebookLoginButton) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithCredential:success");
                firebaseUser = mAuth.getCurrentUser();
                if (!firebaseUser.isEmailVerified()) {
                    firebaseUser.sendEmailVerification().addOnCompleteListener(task1 -> {
                        Toast.makeText(getActivity(), "Verification Email is sent to " + mAuth.getCurrentUser().getEmail(), Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                        LoginManager.getInstance().logOut();
                    });
                } else {
                    ((NavigationHost) getActivity()).navigateTo(new AccessoryGridFragment(), false); // Navigate to the grid Fragment
                    facebookLoginButton.setLogoutText("Logout");
                }
                Log.d(TAG, "handleFacebookAccessToken: " + mAuth.getUid());
                Profile profile = Profile.getCurrentProfile();
                User user = new User();
                user.setEmail(mAuth.getCurrentUser().getEmail());
                user.setFirstName(profile.getFirstName());
                user.setLastName(profile.getLastName());
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
                Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
