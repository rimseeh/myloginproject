package com.example.designandloginproject;


import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */


public class SignUpFragment extends Fragment {


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

    Boolean checked = null;

    @BindView(R.id.male_radio_button_sign_up)
    RadioButton maleRadioButton;
    @BindView(R.id.female_radio_button_sign_up)
    RadioButton femaleRadioButton;

    @BindView(R.id.city_spinner_sign_up)
    Spinner citySpinner;


    private DatePickerDialog.OnDateSetListener mOnDateSetListener;


    public SignUpFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        ButterKnife.bind(this,view);
        setUpCalender();
        isValidUser();
        return view;
    }

    private void isValidUser() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void setUpCalender(){
        dateTextView.setOnClickListener(v -> {
            Calendar calendar =Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog =new DatePickerDialog(
                    getActivity(),
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    mOnDateSetListener,
                    year,day,month);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });
        mOnDateSetListener = (view1, year, month, dayOfMonth) -> {
            month=month+1;
            String date = dayOfMonth+"/"+month+"/"+year;
            dateTextView.setText(date);
        };
    }




}
