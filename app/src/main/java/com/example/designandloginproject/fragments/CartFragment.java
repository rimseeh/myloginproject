package com.example.designandloginproject.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.designandloginproject.R;
import com.example.designandloginproject.application.MyApplication;
import com.example.designandloginproject.models.Accessory;
import com.example.designandloginproject.recyclerCard.AccessoryCartCardRecyclerViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {

    private static final String TAG = "CartFragment";
    @BindView(R.id.shopping_cart_back_image_button)
    ImageButton backImageButton;
    View view;
    ArrayList<Accessory> cartAccessories;
    ArrayList<String> cartKeys;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @BindView(R.id.shopping_cart_accessory_swipe_refresh_layout)
    SwipeRefreshLayout cartSwipeRefreshLayout;
    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_cart, container, false);
        Log.d(TAG, "onCreateView: ");
        ButterKnife.bind(this,view);
        setCartKeysAndCartAccessories();

        cartSwipeRefreshLayout.setOnRefreshListener(() -> cartSwipeRefreshLayout.setRefreshing(false));

        return view;
    }

    private void setCartAccessories() {



    }

    private void setCartKeysAndCartAccessories() {
        cartSwipeRefreshLayout.setRefreshing(true);
        cartKeys =new ArrayList<String>();
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference= firebaseDatabase.getReference("cart/"+mAuth.getUid());
        cartAccessories = new ArrayList<>();
        Query query = FirebaseFirestore.getInstance().collection("accessory_images");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        cartKeys.add(snapshot.getKey());
                    }
                    Log.d(TAG, "onDataChange: "+cartKeys.toString());
                    query.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Log.d(TAG, "onDataChange: "+document.getId()    );
                                if(cartKeys.contains(document.getId())){
                                    cartAccessories.add(document.toObject(Accessory.class));
                                }
                            }
                            Log.d(TAG, "onDataChange:"+cartAccessories.toString());
                            setUpCartCardRecyclerViewAdapter();
                            cartSwipeRefreshLayout.setRefreshing(false);
                        } else {
                            Toast.makeText(MyApplication.getAppContext(), task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

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
        AccessoryCartCardRecyclerViewAdapter recyclerViewAdapter = new AccessoryCartCardRecyclerViewAdapter(cartAccessories);
        recyclerView.setAdapter(recyclerViewAdapter);

    }

}
