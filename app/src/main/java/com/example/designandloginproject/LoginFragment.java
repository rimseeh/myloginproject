package com.example.designandloginproject;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.designandloginproject.sharedPreferences.MySharedPreferences;
import com.example.designandloginproject.application.MyApplication;
import com.example.designandloginproject.models.User;
import com.example.designandloginproject.signinmethods.MyGoogle;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;


public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private FirebaseAuth mAuth;
    private static final int GE_SIGN_IN = 9001;
    private static final int FB_SIGN_IN = 9001;
    View view;

    @BindView(R.id.sign_up_button_login)
    Button buttonSignUp;
    @BindView(R.id.login_button_login)
    Button buttonLogin;
    @BindView(R.id.textView_forget_password)
    TextView textViewForgetPass;
    @BindView(R.id.checkBox_login)
    CheckBox checkBox;

    @BindView(R.id.email_edit_text_login)
    TextInputEditText emailEditText;
    @BindView(R.id.password_edit_text_login)
    TextInputEditText passwordEditText;

    @BindView(R.id.email_text_input_login)
    TextInputLayout emailTextInputLayout;
    @BindView(R.id.password_text_input_login)
    TextInputLayout passwordTextInputLayout;
    @BindView(R.id.progressBar_login)
    ProgressBar progressBar;

    @BindView(R.id.sign_in_button_google)
    SignInButton googleSignInButton;

    @BindView(R.id.login_button)
    LoginButton facebookLoginButton;

    private FirebaseUser firebaseUser;
    private CallbackManager mCallbackManager = null;

    private boolean checkBoxBoolean;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        if (MySharedPreferences.getInstance(getActivity()).readString("email", null) != null) {
            emailEditText.setText(MySharedPreferences.getInstance(getActivity()).readString("email", null));
            passwordEditText.setText(MySharedPreferences.getInstance(getActivity()).readString("password", null));
        }
        mAuth = FirebaseAuth.getInstance();
//        if (mAuth.getCurrentUser() != null) {
//        }
        setFaceBookLoginButton();
        setGooglePlusButtonText(googleSignInButton, "Continue with gmail");
        return view;
    }

    private void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
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

    @OnCheckedChanged(R.id.checkBox_login)
    void onChecked(boolean checked) {
        checkBoxBoolean = checked;
    }


    @OnClick({R.id.sign_up_button_login, R.id.login_button_login, R.id.textView_forget_password,R.id.sign_in_button_google})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_up_button_login:
                ((NavigationHost) Objects.requireNonNull(getActivity())).navigateTo(new SignUpFragment(), true); // Navigate to the sign up Fragment
                break;
            case R.id.login_button_login:
                if (isUserValidate()) {
                    if (checkBoxBoolean) {
                        MySharedPreferences.getInstance(getActivity()).writeString("email", Objects.requireNonNull(emailEditText.getText()).toString());
                        MySharedPreferences.getInstance(getActivity()).writeString("password", Objects.requireNonNull(passwordEditText.getText()).toString());
                    }
                    userAuthentication();
                }
                break;
            case R.id.textView_forget_password:
                ((NavigationHost) Objects.requireNonNull(getActivity())).navigateTo(new ForgetPasswordFragment(), true); // Navigate to the grid Fragment
                break;

            case R.id.sign_in_button_google:
                setGoogleLoginButton();
                break;

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                MyGoogle.getInstance(getActivity(),view).firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void setFaceBookLoginButton() {
        mCallbackManager = CallbackManager.Factory.create();
        facebookLoginButton.setReadPermissions("email", "public_profile");
        facebookLoginButton.setLogoutText("Continue with Facebook");
        facebookLoginButton.setFragment(this);
        facebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
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
                onResume();
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithCredential:failure", task.getException());
                Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isUserValidate() {
        boolean validateBoolean = true;
        if (!User.isEmailValid(emailEditText.getText().toString())) {
            emailTextInputLayout.setError(getString(R.string.error_email));
            validateBoolean = false;
        } else {
            emailTextInputLayout.setError(null);
        }

        if (!Pattern.compile(getString(R.string.password_special_char)).matcher(passwordEditText.getText().toString()).matches()) {
            passwordTextInputLayout.setError(getString(R.string.error_password));
            validateBoolean = false;
        } else {
            passwordTextInputLayout.setError(null);
        }
        return validateBoolean;
    }

    private void userAuthentication() {
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

    private void setGoogleLoginButton() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(MyApplication.getAppContext(), gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GE_SIGN_IN);

    }

}
