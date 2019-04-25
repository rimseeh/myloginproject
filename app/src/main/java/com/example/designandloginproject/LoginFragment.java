package com.example.designandloginproject;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;

    @BindView(R.id.sign_up_button_login)
    Button buttonSignUp;


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this,view);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){

        }
        return view;
    }

    @OnClick(R.id.sign_up_button_login)
    void onClick(){
        ((NavigationHost) getActivity()).navigateTo(new SignUpFragment(), false); // Navigate to the next Fragment
    }







}
