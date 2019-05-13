package com.example.designandloginproject.fragments;


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

import com.example.designandloginproject.NavigationHost;
import com.example.designandloginproject.R;
import com.example.designandloginproject.application.MyApplication;
import com.example.designandloginproject.models.User;
import com.example.designandloginproject.sharedPreferences.MySharedPreferences;
import com.example.designandloginproject.signinmethods.MyEmail;
import com.example.designandloginproject.signinmethods.MyFacebook;
import com.example.designandloginproject.signinmethods.MyGoogle;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.NotNull;


import java.util.Objects;
import java.util.regex.Pattern;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;


public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private FirebaseAuth mAuth= FirebaseAuth.getInstance();
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
    MaterialCheckBox checkBox;

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
        setFaceBookLoginButton();
        MyGoogle.setGooglePlusButtonText(googleSignInButton, "Continue with gmail");
        return view;
    }

    @OnCheckedChanged(R.id.checkBox_login)
    void onChecked(boolean checked) {
        checkBoxBoolean = checked;
    }

    @OnClick({R.id.sign_up_button_login, R.id.login_button_login, R.id.textView_forget_password, R.id.sign_in_button_google})
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
                    MyEmail.getInstance(getActivity()).signInWithEamil(progressBar, emailEditText, passwordEditText);
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
                Log.d(TAG, "onActivityResult: ");
                GoogleSignInAccount account = task.getResult(ApiException.class);
                MyGoogle.getInstance(getActivity(), view).firebaseAuthWithGoogle(account);
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
                MyFacebook.getInstance(getActivity()).handleFacebookAccessToken(loginResult.getAccessToken(), facebookLoginButton);
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

    private boolean isUserValidate() {
        boolean validateBoolean = true;
        if (!User.isEmailValid(Objects.requireNonNull(emailEditText.getText()).toString())) {
            emailTextInputLayout.setError(getString(R.string.error_email));
            validateBoolean = false;
        } else {
            emailTextInputLayout.setError(null);
        }

        if (!Pattern.compile(getString(R.string.password_special_char)).matcher(Objects.requireNonNull(passwordEditText.getText()).toString()).matches()) {
            passwordTextInputLayout.setError(getString(R.string.error_password));
            validateBoolean = false;
        } else {
            passwordTextInputLayout.setError(null);
        }
        return validateBoolean;
    }

    private void setGoogleLoginButton() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(MyApplication.getAppContext(), gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        Log.d(TAG, "setGoogleLoginButton: ");
        startActivityForResult(signInIntent, GE_SIGN_IN);

    }

}
