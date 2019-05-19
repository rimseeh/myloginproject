package com.example.designandloginproject.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.designandloginproject.animation.CartChangeListener;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * {@link View.OnClickListener} used for the cart translation
 * This fragment is used to show all the Accessories in the firebase
 */

public class AccessoryGridFragment extends Fragment implements CartChangeListener {

    private static final String TAG = "AccessoryGridFragment";
    private static ArrayList<Accessory> mAccessories = new ArrayList<>();
    private AccessoryCardRecyclerViewAdapter adapter;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ArrayList<String> mCartKeys = new ArrayList<>();
    private int mActionbarheight = 0;
    private RecyclerView mRecyclerView;
    View view;

    /**
     * Binding each View with the view from the XML file of the fragment using ButterKnife
     */
    @BindView(R.id.accessory_swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.backdrop_logout_button)
    MaterialButton logoutButton;
    @BindView(R.id.shopping_cart_image_button)
    ImageButton cartImageView;
    @BindView(R.id.cart_linear_layout)
    LinearLayout cartLinearLayout;
    @BindView(R.id.bottomCartSheet)
    CutCornerView cutCornerViewCart;
    @BindView(R.id.backdrop_settings_button)
    MaterialButton settingsMaterialButton;
    @BindView(R.id.img_cpy)
    ImageView mDummyImgView;
    @BindView(R.id.accessory_constraint_layout)
    ConstraintLayout constraintLayout;

    /**
     * Executed when the fragment is started
     * Initialize the Recycler View to show the accessories
     * Get all accessories from the database and show them in the Recycler view
     */
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment with the ProductGrid theme
        view = inflater.inflate(R.layout.accessory_grid_fragment, container, false);

        ButterKnife.bind(this, view);
        // Set up the toolbar
        setUpToolbar(view);
        // Set up the RecyclerView
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, RecyclerView.VERTICAL, false));

        view.findViewById(R.id.accessory_swipe_refresh_layout).setBackground(Objects.requireNonNull(getContext()).getDrawable(R.drawable.accessory_grid_background_shape));

        swipeRefreshLayout.setRefreshing(true);
        Query query = FirebaseFirestore.getInstance().collection("accessory_images").limit(5);
        query.get().addOnCompleteListener(task -> {
            swipeRefreshLayout.setRefreshing(false);
            if (task.isSuccessful()) {
                mAccessories = new ArrayList<>();
                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Accessory accessory = document.toObject(Accessory.class);
                    assert accessory != null;
                    accessory.setId(document.getId());
                    mAccessories.add(accessory);
                }
                int largePadding = getResources().getDimensionPixelSize(R.dimen.accessory_grid_spacing);
                int smallPadding = getResources().getDimensionPixelSize(R.dimen.accessory_grid_spacing_small);
                mRecyclerView.addItemDecoration(new AccessoryGridItemDecoration(largePadding, smallPadding));
                getCartAccessoriesIds();
//                lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
            } else {
                Toast.makeText(MyApplication.getAppContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
//        setCartKeysAndCartAccessories();

        swipeRefreshLayout.setOnRefreshListener(() -> swipeRefreshLayout.setRefreshing(false));

        TypedValue tv = new TypedValue();
        if (getContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            mActionbarheight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        return view;
    }

    /**
     * Click listener for the views that are listed from the XML
     *
     * @param view This is the view that is clicked (triggered)
     */

    @OnClick({R.id.backdrop_logout_button, R.id.shopping_cart_image_button, R.id.backdrop_settings_button})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.backdrop_logout_button:
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                LoginManager.getInstance().logOut();
                ((NavigationHost) Objects.requireNonNull(getActivity())).navigateTo(new LoginFragment(), false); // Navigate to the grid Fragment
                break;
            case R.id.shopping_cart_image_button:
                ((NavigationHost) Objects.requireNonNull(getActivity())).navigateToWithAnimation(new CartFragment(), true, R.anim.enter_from_buttom_right, R.anim.fade_out, R.anim.fade_in, R.anim.exit_to_buttom_right); // Navigate to the grid Fragment
                break;
            case R.id.backdrop_settings_button:
                ((NavigationHost) Objects.requireNonNull(getActivity())).navigateTo(new SettingsFragment(), true); // Navigate to the grid Fragment
                break;

        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    /**
     * setting up the toolbar animation and translation
     *
     * @param view the view containing the toolbar view
     */
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
                Objects.requireNonNull(getContext()).getDrawable(R.drawable.branded_menu), // Menu open icon
                getContext().getDrawable(R.drawable.close_menu)) // Menu close icon
        );
    }

    /**
     * shaking the cart image button and making animation when adding the the accessory to the cart
     *
     * @param viewCart The card to me moved.
     * @param position The position of the selected card.
     */
    @Override
    public void onCartChange(View viewCart, int[] position) {
        Animation shake;
        shake = AnimationUtils.loadAnimation(MyApplication.getAppContext(), R.anim.shake);
        cartImageView.startAnimation(shake);

        if (viewCart != null) {
            Bitmap b = MyApplication.getInstance().loadBitmapFromView(viewCart, viewCart.getWidth(), viewCart.getHeight());
            animateView(b, position);
        }

    }

    /**
     * Move a View to the bottom corner
     *
     * @param b        The image to be viewed when moving
     * @param position The current position of the card to be moved.
     */
    private void animateView(Bitmap b, int[] position) {
        mDummyImgView.setImageBitmap(b);
        int[] u = new int[2];
        cartImageView.getLocationInWindow(u);
        mDummyImgView.setVisibility(View.VISIBLE);
        AnimatorSet animSetXY = new AnimatorSet();
        Log.e("Utuxe", "=" + u[0]);
        Log.e("Utuye", "=" + u[1]);
        ObjectAnimator y = ObjectAnimator.ofFloat(mDummyImgView, "translationY", position[1], u[1]);
        ObjectAnimator x = ObjectAnimator.ofFloat(mDummyImgView, "translationX", position[0], u[0]);
        ObjectAnimator sy = ObjectAnimator.ofFloat(mDummyImgView, "scaleY", 0.8f, 0.0f);
        ObjectAnimator sx = ObjectAnimator.ofFloat(mDummyImgView, "scaleX", 0.8f, 0.0f);
        animSetXY.playTogether(x, y, sx, sy);
        animSetXY.setDuration(650);
        animSetXY.start();
    }

    /**
     * Initialize mCartKeys ArrayList for all cart Ids in fire base
     */
    private void getCartAccessoriesIds() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("cart/" + mAuth.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        mCartKeys.add(snapshot.getKey());
                    }
                    for (Accessory accessory : mAccessories) {
                        if (mCartKeys.contains(accessory.getId())) {
                            accessory.setAddedToCart(true);
                        }
                    }
                    int i = 0;
                    for (Accessory cartAccessory : mAccessories) {
                        if (cartAccessory.isAddedToCart()) {
                            ImageView imageView = new ImageView(MyApplication.getAppContext());
                            imageView.setLayoutParams(new LinearLayout.LayoutParams(96, 96));
                            imageView.setPadding(10, 0, 0, 0);
                            cartLinearLayout.addView(imageView);
                            Glide.with(MyApplication.getAppContext()).load(Uri.parse(cartAccessory.getUrl())).apply(RequestOptions.circleCropTransform()).into(imageView);
                            i++;
                            if (i == 2) {
                                break;
                            }
                        }
                    }
                    adapter = new AccessoryCardRecyclerViewAdapter(mAccessories, getActivity(), AccessoryGridFragment.this);
                    Log.d(TAG, "onComplete: " + mAccessories.toString());
                    mRecyclerView.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }
}