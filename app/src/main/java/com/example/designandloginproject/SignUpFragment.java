package com.example.designandloginproject;


import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

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

    TextInputLayout emailInputLayout;
    TextInputEditText emailEditText;
    TextInputLayout firstNameInputLayout;
    TextInputEditText firstNameEditText;
    TextInputLayout lastNameInputLayout;
    TextInputEditText lastNameEditText;
    TextInputLayout phoneInputLayout;
    TextInputEditText phoneEditText;
    TextInputLayout passwordInputLayout;
    TextInputEditText passwordEditText;
    TextInputLayout confirmPasswordInputLayout;
    TextInputEditText confirmPasswordEditText;

    TextView dateTextView;
    TextView dateErrorTextView;
    TextView genderErrorTextView;

    boolean checked;
    RadioButton maleRadioButton;
    RadioButton femaleRadioButton;

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
        dateTextView =view.findViewById(R.id.date_text_view_sign_up);
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
        return view;
    }




}
