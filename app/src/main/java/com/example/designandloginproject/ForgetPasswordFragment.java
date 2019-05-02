package com.example.designandloginproject;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.designandloginproject.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgetPasswordFragment extends Fragment {

    private static final String TAG = "ForgetPasswordFragment";

    @BindView(R.id.reset_button)
    MaterialButton buttonReset;
    @BindView(R.id.email_edit_text_reset)
    TextInputEditText editTextEmail;
    @BindView(R.id.email_text_input_reset)
    TextInputLayout emailInputLayout;

    String email=null;


    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    public ForgetPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forget_password, container, false);
        ButterKnife.bind(this, view);
        return view;


    }

    @OnClick(R.id.reset_button)
    public void onClick(){
        email = editTextEmail.getText().toString();
        if (User.isEmailValid(email)) {
            mAuth.fetchProvidersForEmail(email).addOnCompleteListener(task -> {
                if (task.getResult().getProviders().isEmpty()) {
                    Toast.makeText(getActivity(), "email not found", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(getActivity(), "Reset Password Sent to Email", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "onCreate: " + task1.getException().getMessage());
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
