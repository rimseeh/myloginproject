package com.example.designandloginproject.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.designandloginproject.MainActivity;
import com.example.designandloginproject.R;
import com.example.designandloginproject.application.MyApplication;
import com.example.designandloginproject.sharedPreferences.MySharedPreferences;
import com.google.android.material.switchmaterial.SwitchMaterial;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    View view;
    @BindView(R.id.switchMaterial_dark_theme)
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
        ButterKnife.bind(this,view);
        switchMaterial.setOnCheckedChangeListener(null);
        MySharedPreferences mySharedPreferences= MySharedPreferences.getInstance(MyApplication.getAppContext());
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            checkedABoolean=false;
            switchMaterial.setChecked(true);
        }
        else {
            checkedABoolean=false;
            switchMaterial.setChecked(false);
        }
        switchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SettingsFragment fragment= getFragment();
            if(fragment!=null) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    getActivity().setTheme(R.style.DarkTheme);
                    mySharedPreferences.writeInteger("Mode",AppCompatDelegate.MODE_NIGHT_YES);

                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    getActivity().setTheme(R.style.Theme);
                    mySharedPreferences.writeInteger("Mode",AppCompatDelegate.MODE_NIGHT_NO);
                }
                getFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
            }else {
                Toast.makeText(MyApplication.getAppContext(), "Error while changing theme", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void restartApp() {
        Intent intent =new Intent(MyApplication.getAppContext(), MainActivity.class);
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

    SettingsFragment getFragment(){
        for (Fragment fragment : getFragmentManager().getFragments()){
            if (fragment instanceof SettingsFragment)
                return (SettingsFragment)fragment;
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
