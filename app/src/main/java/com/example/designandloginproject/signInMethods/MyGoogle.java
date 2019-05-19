package com.example.designandloginproject.signInMethods;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.designandloginproject.fragments.AccessoryGridFragment;
import com.example.designandloginproject.NavigationHost;
import com.example.designandloginproject.R;
import com.example.designandloginproject.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class MyGoogle {
    private static final String TAG = "MyGoogle";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Activity mActivity;
    private FirebaseUser mFirebaseUser;
    @SuppressLint("StaticFieldLeak")
    private static MyGoogle ourInstance = null;
    private View mView;

    public static MyGoogle getInstance(Activity activity, View view) {
        if (ourInstance == null) {
            ourInstance = new MyGoogle(activity, view);
        }
        return ourInstance;
    }

    /**
     * Private constructor to make a single object from MyGoogle class.
     */
    private MyGoogle(Activity activity, View view) {
        this.mActivity = activity;
        this.mView = view;
    }

    /**
     * Getter for the Main Activity.
     */
    private Activity getActivity() {
        return mActivity;
    }

    private View getView() {
        return mView;
    }

    /**
     * Signing in with google verification using google account and adding the account to the firebase database.
     *
     * @param account google account to sign in with.
     */
    public void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mActivity, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        mFirebaseUser = mAuth.getCurrentUser();
                        ((NavigationHost) getActivity()).navigateTo(new AccessoryGridFragment(), false); // Navigate to the grid Fragment
                        User user = new User();
                        user.setEmail(mFirebaseUser.getEmail());
                        ArrayList<String> strings = getFirstAndLastNameGoogle(account);
                        user.setFirstName(strings.get(0));
                        user.setLastName(strings.get(1));
                        user.setPhone(mFirebaseUser.getPhoneNumber());
                        //save the user to the firebase database
                        FirebaseDatabase.getInstance().getReference("users")
                                .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                                .setValue(user).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Log.d(TAG, "handleFacebookAccessToken: ");
                            } else {
                                Log.e(TAG, "handleFacebookAccessToken: " + Objects.requireNonNull(task1.getException()).getMessage(), task1.getException());
                            }
                        });
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Snackbar.make(getView().findViewById(R.id.container), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * getting the first and last name from google account.
     *
     * @param acct google account to get the first and last name.
     * @return array list that has two nodes (first is user name and second last name).
     */
    private ArrayList<String> getFirstAndLastNameGoogle(GoogleSignInAccount acct) {
        ArrayList<String> strings = new ArrayList<>();
        String fullName = acct.getDisplayName();
        assert fullName != null;
        String[] parts = fullName.split("\\s+");
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

    /**
     * Find the TextView that is inside of the SignInButton and set its text.
     *
     * @param signInButton this is google sign in button.
     * @param buttonText   the text to set the google text button.
     */
    public static void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        //
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                tv.setPadding(0, 0, 10, 0);
                return;
            }
        }
    }
}
