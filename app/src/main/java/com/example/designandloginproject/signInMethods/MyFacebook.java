package com.example.designandloginproject.signInMethods;

import android.annotation.SuppressLint;
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

import java.util.Objects;

/**
 * A Singleton class which allow the user to login using facebook verification.
 */
public class MyFacebook {
    private static final String TAG = "MyFacebook";
    @SuppressLint("StaticFieldLeak")
    private static MyFacebook ourInstance = null;
    private Activity mActivity;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mFirebaseUser;

    public static MyFacebook getInstance(Activity activity) {
        if (ourInstance == null) {
            ourInstance = new MyFacebook(activity);
        }
        return ourInstance;
    }

    /**
     * Private constructor to make a single object from MyFacebook class.
     */
    private MyFacebook(Activity mActivity) {
        this.mActivity = mActivity;
    }

    /**
     * Getter for the Main Activity.
     */
    private Activity getActivity() {
        return mActivity;
    }

    /**
     * Signing in with facebook verification using facebook login button.
     *
     * @param token               token is for facebook verification.
     * @param facebookLoginButton facebook login button.
     */
    public void handleFacebookAccessToken(AccessToken token, LoginButton facebookLoginButton) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        //using firebase authentication to sign in using facebook
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithCredential:success");
                mFirebaseUser = mAuth.getCurrentUser();
                if (mFirebaseUser != null) {
                    if (!mFirebaseUser.isEmailVerified()) {
                        mFirebaseUser.sendEmailVerification().addOnCompleteListener(task1 -> {
                            Log.d(TAG, "handleFacebookAccessToken: verification Email sent to " + mAuth.getCurrentUser().getEmail());
                            Toast.makeText(getActivity(), "Verification Email is sent to "
                                    + mAuth.getCurrentUser().getEmail(), Toast.LENGTH_LONG)
                                    .show();
                            //logout from firebase authentication
                            mAuth.signOut();
                            //logout from facebook authentication
                            LoginManager.getInstance().logOut();
                        });
                    } else {
                        // Navigate to the accessory grid Fragment
                        ((NavigationHost) getActivity()).navigateTo(new AccessoryGridFragment(), false);
                        facebookLoginButton.setLogoutText("Logout");
                    }
                } else {
                    Log.e(TAG, "handleFacebookAccessToken: error in user authentication");
                }
                Log.d(TAG, "handleFacebookAccessToken: " + mAuth.getUid());
                Profile profile = Profile.getCurrentProfile();
                User user = new User();
                user.setEmail(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
                user.setFirstName(profile.getFirstName());
                user.setLastName(profile.getLastName());
                //save the user to the user to the firebase database
                FirebaseDatabase.getInstance().getReference("users")
                        .child(mAuth.getCurrentUser().getUid())
                        .setValue(user).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Log.d(TAG, "handleFacebookAccessToken: successfull saved to firebase");
                    } else {
                        Log.e(TAG, "handleFacebookAccessToken: " +
                                Objects.requireNonNull(task1.getException()).getMessage(), task1.getException());
                    }
                });
            } else {
                // If sign in fails, display a message to the user.
                Log.e(TAG, "signInWithCredential: failure", task.getException());
                Toast.makeText(getActivity(), "Authentication failed. " +
                        Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
