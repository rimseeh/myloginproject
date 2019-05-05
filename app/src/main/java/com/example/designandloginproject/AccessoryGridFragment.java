package com.example.designandloginproject;

import android.os.Build;
import android.os.Bundle;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.designandloginproject.application.MyApplication;
import com.example.designandloginproject.models.Accessory;
import com.facebook.login.LoginManager;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AccessoryGridFragment extends Fragment {
    private static final String TAG = "AccessoryGridFragment";

    @BindView(R.id.accessory_swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.backdrop_logout_button)
    MaterialButton logoutButton;

    private DocumentSnapshot lastVisible;
    ArrayList<Accessory> accessories = new ArrayList<>();
    AccessoryCardRecyclerViewAdapter adapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment with the ProductGrid theme
        View view = inflater.inflate(R.layout.accessory_grid_fragment, container, false);
        ButterKnife.bind(this, view);
        // Set up the toolbar
        setUpToolbar(view);
        // Set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, RecyclerView.VERTICAL, false));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.findViewById(R.id.accessory_swipe_refresh_layout).setBackground(getContext().getDrawable(R.drawable.accessory_grid_background_shape));
        }

        swipeRefreshLayout.setRefreshing(true);
        Query query = FirebaseFirestore.getInstance().collection("accessory_images").limit(4);
        query.get().addOnCompleteListener(task -> {
            swipeRefreshLayout.setRefreshing(false);
            if (task.isSuccessful()) {
                accessories = new ArrayList<>();
                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Accessory accessory = document.toObject(Accessory.class);
                    assert accessory != null;
                    accessory.setId(document.getId());
                    accessories.add(accessory);
                }
                Log.d(TAG, "onComplete: " + accessories.toString());
                adapter = new AccessoryCardRecyclerViewAdapter(accessories, (MainActivity) getActivity());
                int largePadding = getResources().getDimensionPixelSize(R.dimen.product_grid_spacing);
                int smallPadding = getResources().getDimensionPixelSize(R.dimen.product_grid_spacing_small);
                recyclerView.addItemDecoration(new ProductGridItemDecoration(largePadding, smallPadding));
                Log.d(TAG, "onCreateView: " + accessories);
                recyclerView.setAdapter(adapter);
                lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
            } else {
                Toast.makeText(MyApplication.getAppContext(), task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
//        FirebaseDatabase.getInstance().getReference().child("images").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                swipeRefreshLayout.setRefreshing(false);
//                for (DataSnapshot document : dataSnapshot.getChildren()) {
//                    Accessory accessory = document.getValue(Accessory.class);
//                    assert accessory != null;
//                    accessory.setId(document.getKey());
//                    accessories.add(accessory);
//                }
//                adapter = new AccessoryCardRecyclerViewAdapter(accessories, (MainActivity) getActivity());
//                int largePadding = getResources().getDimensionPixelSize(R.dimen.product_grid_spacing);
//                int smallPadding = getResources().getDimensionPixelSize(R.dimen.product_grid_spacing_small);
//                recyclerView.addItemDecoration(new ProductGridItemDecoration(largePadding, smallPadding));
//                Log.d(TAG, "onCreateView: " + accessories);
//                recyclerView.setAdapter(adapter);
//
//            }


//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
        swipeRefreshLayout.setOnRefreshListener(() -> swipeRefreshLayout.setRefreshing(false));
        return view;
    }

    @OnClick(R.id.backdrop_logout_button)
    void onClick() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        LoginManager.getInstance().logOut();
        ((NavigationHost) getActivity()).navigateTo(new LoginFragment(), false); // Navigate to the grid Fragment
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }
        toolbar.setNavigationOnClickListener(new NavigationIconClickListener(
                getContext(),
                view.findViewById(R.id.accessory_swipe_refresh_layout),
                new AccelerateDecelerateInterpolator(),
                getContext().getResources().getDrawable(R.drawable.branded_menu), // Menu open icon
                getContext().getResources().getDrawable(R.drawable.close_menu)) // Menu close icon
        );
    }

}