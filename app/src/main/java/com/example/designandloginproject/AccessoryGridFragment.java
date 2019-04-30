package com.example.designandloginproject;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.designandloginproject.models.Accessory;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class AccessoryGridFragment extends Fragment {
    private static final String TAG = "AccessoryGridFragment";
    private DocumentSnapshot lastVisible;
    ArrayList<Accessory> accessories = new ArrayList<>();
    AccessoryCardRecyclerViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment with the ProductGrid theme
        View view = inflater.inflate(R.layout.accessory_grid_fragment, container, false);

        // Set up the toolbar
        setUpToolbar(view);

        // Set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, RecyclerView.VERTICAL, false));


        FirebaseDatabase.getInstance().getReference().child("images").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot document : dataSnapshot.getChildren()) {
                    Accessory accessory = document.getValue(Accessory.class);
                    assert accessory != null;
                    accessory.setId(document.getKey());
                    accessories.add(accessory);
                }
                adapter = new AccessoryCardRecyclerViewAdapter(accessories);
                int largePadding = getResources().getDimensionPixelSize(R.dimen.product_grid_spacing);
                int smallPadding = getResources().getDimensionPixelSize(R.dimen.product_grid_spacing_small);
                recyclerView.addItemDecoration(new ProductGridItemDecoration(largePadding, smallPadding));
                Log.d(TAG, "onCreateView: " + accessories);
                recyclerView.setAdapter(adapter);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        query.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                accessories = new ArrayList<>();
//                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
//                    Accessory accessory = document.toObject(Accessory.class);
//                    assert accessory != null;
//                    accessory.setId(document.getId());
//                    accessories.add(accessory);
//                }
//                adapter = new AccessoryCardRecyclerViewAdapter(accessories);
//                int largePadding = getResources().getDimensionPixelSize(R.dimen.product_grid_spacing);
//                int smallPadding = getResources().getDimensionPixelSize(R.dimen.product_grid_spacing_small);
//                recyclerView.addItemDecoration(new ProductGridItemDecoration(largePadding, smallPadding));
//                Log.d(TAG, "onCreateView: "+accessories);
////                lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
//            }
//        });


        return view;
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

}