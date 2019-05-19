package com.example.designandloginproject.fragments;


import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.designandloginproject.NavigationHost;
import com.example.designandloginproject.R;
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

    private static final String TAG = "SignUpFragment";
    private static final Boolean ERROR = true;
    private String mGender;
    private boolean mDateSet = false;
    private String mDate;
    private DatePickerDialog.OnDateSetListener mOnDateSetListener;
    private FirebaseAuth mAuth;
    private User mUser;

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

    public SignUpFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
            mUser = new User(
                    Objects.requireNonNull(emailEditText.getText()).toString(),
                    Objects.requireNonNull(firstNameEditText.getText()).toString(),
                    Objects.requireNonNull(lastNameEditText.getText()).toString(),
                    Objects.requireNonNull(phoneEditText.getText()).toString(),
                    mGender,
                    citySpinner.getSelectedItem().toString()
            );
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(), Objects.requireNonNull(passwordEditText.getText()).toString())
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            //store additional fields
                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                                    .setValue(mUser).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Registration Successful", Toast.LENGTH_SHORT).show();
                                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            Toast.makeText(getActivity(), "Verification email sent to " + mUser.getEmail(), Toast.LENGTH_LONG).show();
                                            ((NavigationHost) Objects.requireNonNull(getActivity())).navigateTo(new LoginFragment(), false); // Navigate to the next Fragment
                                        } else {
                                            Log.e(TAG, "sendEmailVerification", task.getException());
                                            Toast.makeText(getActivity(), "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(getActivity(), Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Log.d(TAG, "onClick: " + Objects.requireNonNull(task.getException()).getMessage());
                            Toast.makeText(getActivity(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


        }
    }

    private Boolean isValidUser() {
        Boolean error = false;
        if (!User.isEmailValid(Objects.requireNonNull(emailEditText.getText()).toString())) {
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
        if (!mDateSet) {
            dateErrorTextView.setVisibility(View.VISIBLE);
            error = ERROR;
        } else {
            dateErrorTextView.setVisibility(View.INVISIBLE);
        }

        if (maleRadioButton.isChecked()) {
            mGender = "male";
            genderErrorTextView.setVisibility(View.INVISIBLE);
        } else if (femaleRadioButton.isChecked()) {
            mGender = "female";
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
            Objects.requireNonNull(datePickerDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });
        mOnDateSetListener = (view1, year, month, dayOfMonth) -> {
            month = month + 1;
            mDate = dayOfMonth + "/" + month + "/" + year;
            mDateSet = true;
            dateTextView.setText(mDate);
        };
    }


}
