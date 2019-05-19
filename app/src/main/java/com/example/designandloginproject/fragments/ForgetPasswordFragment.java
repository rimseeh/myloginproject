package com.example.designandloginproject.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.designandloginproject.R;
import com.example.designandloginproject.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgetPasswordFragment extends Fragment {

    private static final String TAG = "ForgetPasswordFragment";
    private String mEmail =null;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @BindView(R.id.reset_button)
    MaterialButton buttonReset;
    @BindView(R.id.email_edit_text_reset)
    TextInputEditText editTextEmail;
    @BindView(R.id.email_text_input_reset)
    TextInputLayout emailInputLayout;

    public ForgetPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forget_password, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.reset_button)
    public void onClick(){
        mEmail = Objects.requireNonNull(editTextEmail.getText()).toString();
        if (User.isEmailValid(mEmail)) {
            mAuth.fetchSignInMethodsForEmail(mEmail).addOnCompleteListener(task -> {
                if (Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getSignInMethods()).isEmpty()) {
                    Toast.makeText(getActivity(), "mEmail not found", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.sendPasswordResetEmail(mEmail).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(getActivity(), "Reset Password Sent to Email", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "onCreate: " + Objects.requireNonNull(task1.getException()).getMessage());
                            Toast.makeText(getActivity(), task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } else {
            emailInputLayout.setError(getString(R.string.error_email));
        }
    }
}
