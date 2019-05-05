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

import com.example.designandloginproject.SharedPreferences.MySharedPreferences;
import com.example.designandloginproject.models.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
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

    @BindView(R.id.login_button)
    LoginButton facebookLoginButton;

    FirebaseUser firebaseUser;
    CallbackManager callbackManager;
    User user;

    boolean checkBoxBoolean;
    boolean validateBoolean;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        if (MySharedPreferences.getInstance(getActivity()).readString("email", null) != null) {
            emailEditText.setText(MySharedPreferences.getInstance(getActivity()).readString("email", null));
            passwordEditText.setText(MySharedPreferences.getInstance(getActivity()).readString("password", null));
        }
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
        }

        callbackManager = CallbackManager.Factory.create();
        facebookLoginButton.setReadPermissions(Arrays.asList("email"));
        return view;
    }

    public void buttonLoginListener(View view) {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handelFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getActivity(), getString(R.string.facebook_user_cancel), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getActivity(), error.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handelFacebookToken(AccessToken accessToken) {
        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                    } else {
                        Toast.makeText(getActivity(), "cannot connect to FireBase : " + task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @OnCheckedChanged(R.id.checkBox_login)
    void onChecked(boolean checked) {
        checkBoxBoolean = checked;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.sign_up_button_login, R.id.login_button_login, R.id.textView_forget_password, R.id.login_button})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_up_button_login:
                ((NavigationHost) getActivity()).navigateTo(new SignUpFragment(), true); // Navigate to the sign up Fragment
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
                ((NavigationHost) getActivity()).navigateTo(new ForgetPasswordFragment(), true); // Navigate to the grid Fragment
                break;
            case R.id.login_button:
                buttonLoginListener(v);
                break;

        }

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
                                            validateBoolean = true;
                                            user = snapshot.getValue(User.class);
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
