package com.example.designandloginproject;


import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.designandloginproject.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */


public class SignUpFragment extends Fragment {

    private float x1,x2,y1,y2;
    private static final String TAG = "SignUpFragment";
    private static final Boolean ERROR = true;


    @BindView(R.id.layout)
    ScrollView layout;
    @BindView(R.id.email_text_input)
    TextInputLayout emailInputLayout;
    @BindView(R.id.email_edit_text_sign_up)
    TextInputEditText emailEditText;
    @BindView(R.id.first_name_text_input)
    TextInputLayout firstNameInputLayout;
    @BindView(R.id.first_name_edit_text_sign_up)
    TextInputEditText firstNameEditText;
    @BindView(R.id.last_name_text_input)
    TextInputLayout lastNameInputLayout;
    @BindView(R.id.last_name_edit_text_sign_up)
    TextInputEditText lastNameEditText;
    @BindView(R.id.phone_text_input)
    TextInputLayout phoneInputLayout;
    @BindView(R.id.phone_edit_text_sign_up)
    TextInputEditText phoneEditText;
    @BindView(R.id.password_text_input)
    TextInputLayout passwordInputLayout;
    @BindView(R.id.password_edit_text_sign_up)
    TextInputEditText passwordEditText;
    @BindView(R.id.confirm_password_text_input)
    TextInputLayout confirmPasswordInputLayout;
    @BindView(R.id.confirm_password_edit_text_sign_up)
    TextInputEditText confirmPasswordEditText;

    @BindView(R.id.date_text_view_sign_up)
    TextView dateTextView;

    @BindView(R.id.date_error_text_input)
    TextView dateErrorTextView;
    @BindView(R.id.gender_error_text_input)
    TextView genderErrorTextView;

    private String gender;
    private boolean dateSet = false;

    @BindView(R.id.male_radio_button_sign_up)
    RadioButton maleRadioButton;
    @BindView(R.id.female_radio_button_sign_up)
    RadioButton femaleRadioButton;

    @BindView(R.id.city_spinner_sign_up)
    Spinner citySpinner;

    @BindView(R.id.sign_up_button_sign_up)
    MaterialButton button;

    @BindView(R.id.sign_up_progress_bar)
    ProgressBar progressBar;

    private String date;
    private DatePickerDialog.OnDateSetListener mOnDateSetListener;

    private FirebaseAuth mAuth;

    User user;

    public SignUpFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        ButterKnife.bind(this, view);
        setUpCalender();
        mAuth = FirebaseAuth.getInstance();

        return view;
    }

    @OnClick(R.id.sign_up_button_sign_up)
    void onClick() {
        Log.d(TAG, "onClick: ");
        if (isValidUser()) {
            user = new User(
                    emailEditText.getText().toString(),
                    firstNameEditText.getText().toString(),
                    lastNameEditText.getText().toString(),
                    phoneEditText.getText().toString(),
                    gender,
                    citySpinner.getSelectedItem().toString()
            );
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            //store additional fields
                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                                    .setValue(user).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Registration Successful", Toast.LENGTH_SHORT).show();
                                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            Toast.makeText(getActivity(), "Verification email sent to " + user.getEmail(), Toast.LENGTH_LONG).show();
                                            ((NavigationHost) getActivity()).navigateTo(new LoginFragment(), false); // Navigate to the next Fragment
                                        } else {
                                            Log.e(TAG, "sendEmailVerification", task.getException());
                                            Toast.makeText(getActivity(), "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(getActivity(), task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Log.d(TAG, "onClick: " + task.getException().getMessage());
                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


        }
    }

    private Boolean isValidUser() {
        Boolean error = false;
        if (!User.isEmailValid(emailEditText.getText().toString())) {
            emailInputLayout.setError(getString(R.string.error_email));
            error = ERROR;
        } else {
            emailInputLayout.setError(null);
        }

        if (!Objects.requireNonNull(firstNameEditText.getText()).toString().matches(getString(R.string.name_special_char))) {
            firstNameInputLayout.setError(getString(R.string.error_first_name));
            error = ERROR;
        } else {
            firstNameInputLayout.setError(null);
        }
        if (!Objects.requireNonNull(lastNameEditText.getText()).toString().matches(getString(R.string.name_special_char))) {
            lastNameInputLayout.setError(getString(R.string.error_last_name));
            error = ERROR;
        } else {
            lastNameInputLayout.setError(null);
        }
        if (!Objects.requireNonNull(phoneEditText.getText()).toString().matches(getString(R.string.phone_special_char))) {
            phoneInputLayout.setError(getString(R.string.error_phone));
            error = ERROR;
        } else {
            phoneInputLayout.setError(null);
        }
        if (!dateSet) {
            dateErrorTextView.setVisibility(View.VISIBLE);
            error = ERROR;
        } else {
            dateErrorTextView.setVisibility(View.INVISIBLE);
        }

        if (maleRadioButton.isChecked()) {
            gender = "male";
            genderErrorTextView.setVisibility(View.INVISIBLE);
        } else if (femaleRadioButton.isChecked()) {
            gender = "female";
            genderErrorTextView.setVisibility(View.INVISIBLE);
        } else {
            genderErrorTextView.setVisibility(View.VISIBLE);
            error = ERROR;
        }

        if (!Pattern.compile(getString(R.string.password_special_char)).matcher(Objects.requireNonNull(passwordEditText.getText()).toString()).matches()) {
            passwordInputLayout.setError(getString(R.string.error_password));
            error = ERROR;
        } else {
            passwordInputLayout.setError(null);
        }

        if (!passwordEditText.getText().toString().equals(Objects.requireNonNull(confirmPasswordEditText.getText()).toString())) {
            confirmPasswordInputLayout.setError(getString(R.string.error_confirm_password));
            error = ERROR;
        } else {
            confirmPasswordInputLayout.setError(null);
        }
        return !error;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void setUpCalender() {

        dateTextView.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    Objects.requireNonNull(getActivity()),
                    R.style.DatePickerTheme,
                    mOnDateSetListener,
                    year, day, month);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });
        mOnDateSetListener = (view1, year, month, dayOfMonth) -> {
            month = month + 1;
            date = dayOfMonth + "/" + month + "/" + year;
            dateSet = true;
            dateTextView.setText(date);
        };
    }

    @OnTouch(R.id.layout)
    public boolean onTouchEvent(MotionEvent touchEvent){
        switch (touchEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1=touchEvent.getX();
                y1=touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2=touchEvent.getX();
                y2=touchEvent.getY();
                if(x1+150<x2){
                    ((NavigationHost) getActivity()).navigateTo(new LoginFragment(), false); // Navigate to the next Fragment
                }
                break;
        }
        return false;
    }

}
