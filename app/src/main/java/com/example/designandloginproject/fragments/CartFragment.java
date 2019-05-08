package com.example.designandloginproject.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.designandloginproject.R;
import com.example.designandloginproject.recyclerCard.AccessoryCartCardRecyclerViewAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {

    @BindView(R.id.shopping_cart_back_image_button)
    ImageButton backImageButton;
    View view;
    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_cart, container, false);

        ButterKnife.bind(this,view);
        setUpCartCardRecyclerViewAdapter();
        return view;
    }

    @OnClick(R.id.shopping_cart_back_image_button)
    void onClick(View view){
        switch (view.getId()){
            case R.id.shopping_cart_back_image_button:
                Objects.requireNonNull(getActivity()).onBackPressed();
                break;
        }
    }
    private void setUpCartCardRecyclerViewAdapter() {
        RecyclerView recyclerView = view.findViewById(R.id.shopping_cart_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AccessoryCartCardRecyclerViewAdapter recyclerViewAdapter = new AccessoryCartCardRecyclerViewAdapter(AccessoryGridFragment.accessories);
        recyclerView.setAdapter(recyclerViewAdapter);

    }


}
