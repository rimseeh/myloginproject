package com.example.designandloginproject.fragments;

import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.designandloginproject.recyclerCard.AccessoryCardRecyclerViewAdapter;
import com.example.designandloginproject.recyclerCard.AccessoryGridItemDecoration;
import com.example.designandloginproject.NavigationHost;
import com.example.designandloginproject.animation.NavigationIconClickListener;
import com.example.designandloginproject.R;
import com.example.designandloginproject.application.MyApplication;
import com.example.designandloginproject.models.Accessory;
import com.facebook.login.LoginManager;
import com.github.florent37.shapeofview.shapes.CutCornerView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
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

    @BindView(R.id.shopping_cart_image_button)
    ImageButton cartImageView;

    @BindView(R.id.bottomCartSheet)
    CutCornerView cutCornerViewCart;
    static ArrayList<Accessory> accessories = new ArrayList<>();
    private AccessoryCardRecyclerViewAdapter adapter;
    View view;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment with the ProductGrid theme
        view = inflater.inflate(R.layout.accessory_grid_fragment, container, false);
        ButterKnife.bind(this, view);
        // Set up the toolbar
        setUpToolbar(view);
        // Set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, RecyclerView.VERTICAL, false));

        view.findViewById(R.id.accessory_swipe_refresh_layout).setBackground(getContext().getDrawable(R.drawable.accessory_grid_background_shape));

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
                adapter = new AccessoryCardRecyclerViewAdapter(accessories,getActivity());
                int largePadding = getResources().getDimensionPixelSize(R.dimen.product_grid_spacing);
                int smallPadding = getResources().getDimensionPixelSize(R.dimen.product_grid_spacing_small);
                recyclerView.addItemDecoration(new AccessoryGridItemDecoration(largePadding, smallPadding));
                Log.d(TAG, "onCreateView: " + accessories);
                recyclerView.setAdapter(adapter);
//                lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
            } else {
                Toast.makeText(MyApplication.getAppContext(), task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> swipeRefreshLayout.setRefreshing(false));
        return view;
    }

    @OnClick({R.id.backdrop_logout_button, R.id.shopping_cart_image_button})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.backdrop_logout_button:
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                LoginManager.getInstance().logOut();
                ((NavigationHost) Objects.requireNonNull(getActivity())).navigateTo(new LoginFragment(), false); // Navigate to the grid Fragment
                break;
            case R.id.shopping_cart_image_button:
                ((NavigationHost) Objects.requireNonNull(getActivity())).navigateToWithAnimation(new CartFragment(), true,R.anim.enter_from_buttom_right,R.anim.fade_out,R.anim.fade_in,R.anim.exit_to_buttom_right); // Navigate to the grid Fragment
                break;
        }

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