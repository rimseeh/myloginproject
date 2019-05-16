package com.example.designandloginproject.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.designandloginproject.MainActivity;
import com.example.designandloginproject.R;
import com.example.designandloginproject.network.ConnectivityReceiver;
import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class NetworkErrorFragment extends Fragment {

    @BindView(R.id.tryAgainButton)
    MaterialButton TryAgainButton;
    @BindView(R.id.swipdownToRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    public NetworkErrorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_network_error, container, false);
        ButterKnife.bind(this, view);

        swipeRefreshLayout.setOnRefreshListener(this::isConnectedToNetwork);
        return view;
    }

    @OnClick(R.id.tryAgainButton)
    void onClick() {
       isConnectedToNetwork();

    }

    private void isConnectedToNetwork(){
        swipeRefreshLayout.setRefreshing(true);
        if(ConnectivityReceiver.isConnected()){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            Objects.requireNonNull(getActivity()).startActivity(intent);
            getActivity().finish();
        }
        swipeRefreshLayout.setRefreshing(false);
    }


}
