package com.example.designandloginproject.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.designandloginproject.R;
import com.example.designandloginproject.application.MyApplication;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";
    View view;
    @BindView(R.id.switchMaterial_dark_theme)
    SwitchMaterial switchMaterial;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_settings, container, false);
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: ");
        ButterKnife.bind(this,view);
        switchMaterial.setOnCheckedChangeListener(null);
        if(MyApplication.getInstance().getInt("Mode",AppCompatDelegate.MODE_NIGHT_NO)==AppCompatDelegate.MODE_NIGHT_YES){
            switchMaterial.setChecked(true);
        }
        else {
            switchMaterial.setChecked(false);
        }
        switchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SettingsFragment fragment= getFragment();
            if(fragment!=null) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    Objects.requireNonNull(getActivity()).setTheme(R.style.DarkTheme);
                    MyApplication.getInstance().saveInt("Mode",AppCompatDelegate.MODE_NIGHT_YES);

                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    Objects.requireNonNull(getActivity()).setTheme(R.style.Theme);
                    MyApplication.getInstance().saveInt("Mode",AppCompatDelegate.MODE_NIGHT_NO);
                }
                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
            }else {
                Toast.makeText(MyApplication.getAppContext(), "Error while changing theme", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Fragment fragment = Objects.requireNonNull(getActivity()).getSupportFragmentManager().findFragmentByTag("Settings Fragment");
        if (fragment!=null){
            getActivity().getSupportFragmentManager().putFragment(outState,"Fragment",fragment);
        }
    }

    SettingsFragment getFragment(){
        assert getFragmentManager() != null;
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
