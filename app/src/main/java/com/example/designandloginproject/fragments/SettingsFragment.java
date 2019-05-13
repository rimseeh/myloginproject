package com.example.designandloginproject.fragments;


import android.app.AppComponentFactory;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.designandloginproject.MainActivity;
import com.example.designandloginproject.NavigationHost;
import com.example.designandloginproject.R;
import com.example.designandloginproject.application.MyApplication;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    View view;
    SwitchMaterial switchMaterial;
    boolean checkedABoolean;
    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_settings, container, false);
        // Inflate the layout for this fragment
        switchMaterial=view.findViewById(R.id.switchMaterial_dark_theme);
        switchMaterial.setOnCheckedChangeListener(null);

        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            checkedABoolean=false;
            switchMaterial.setChecked(true);
        }
        else {
            checkedABoolean=false;
            switchMaterial.setChecked(false);
        }
        switchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                restartApp();
            }else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                restartApp();
            }
        });
        return view;
    }

    private void restartApp() {
        Intent intent =new Intent(MyApplication.getAppContext(), MainActivity.class);
        intent.putExtra("settingsFragment",true);
        getActivity().finish();
        startActivity(intent);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Fragment fragment =getActivity().getSupportFragmentManager().findFragmentByTag("Settings Fragment");
        if (fragment!=null){
            getActivity().getSupportFragmentManager().putFragment(outState,"Fragment",fragment);
        }
    }


}
