package com.example.designandloginproject.recyclerCard;


import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


import com.ablanco.zoomy.Zoomy;
import com.android.volley.toolbox.NetworkImageView;
import com.example.designandloginproject.R;
import com.example.designandloginproject.animation.CartChangeListener;
import com.example.designandloginproject.application.MyApplication;
import com.example.designandloginproject.models.Accessory;
import com.example.designandloginproject.network.ImageRequester;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter used to show a simple grid of products.
 */
public class AccessoryCardRecyclerViewAdapter extends RecyclerView.Adapter<AccessoryCardRecyclerViewAdapter.AccessoryCardViewHolder> {

    private List<Accessory> accessories;
    private ImageRequester imageRequester;
    private Activity activity;
    CartChangeListener onQuantityChange;

    public AccessoryCardRecyclerViewAdapter(List<Accessory> accessories, Activity activity,CartChangeListener onQuantityChange) {
        this.accessories = accessories;
        imageRequester = ImageRequester.getInstance();
        this.activity=activity;
        this.onQuantityChange = onQuantityChange;

    }

    @NonNull
    @Override
    public AccessoryCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.accessory_card, parent, false);
        return new AccessoryCardViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull AccessoryCardViewHolder holder, int position) {
        if (accessories != null && position < accessories.size()) {
            Accessory accessory = accessories.get(position);
            holder.accessoryTitle.setText(accessory.getTitle());
            holder.accessoryPrice.setText(accessory.getPrice());
            imageRequester.setImageFromUrl(holder.accessoryImage, accessory.getUrl());
            Zoomy.Builder builder = new Zoomy.Builder(activity).target(holder.accessoryImage);
            builder.register();
            if(!accessory.isAddedToCart()) {
                holder.addToCartImageButton.setOnClickListener(v -> {
                    addToCartFireBase(accessory,holder);
                });
            }else {
                holder.addToCartImageButton.setImageResource(R.drawable.ic_shopping_cart);
            }
        }
    }

    private void addToCartFireBase(Accessory accessory,AccessoryCardViewHolder holder) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String id = accessory.getId();
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference= firebaseDatabase.getReference("cart/"+mAuth.getUid());
        databaseReference.child(id).setValue("true").addOnCompleteListener( task -> {
            if (task.isSuccessful()){
                int e[] = new int[2];
                holder.card.getLocationOnScreen(e);
                onQuantityChange.onCartChange(accessory.getId(), 1, Integer.parseInt(accessory.getPrice()), holder.accessoryImage, e);
                Log.e("tuxe", "=" + e[0]);
                Log.e("tuye", "=" + e[1]);
                holder.animatorSet.setTarget(holder.addToCartImageButton);
                holder.animatorSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        holder.addToCartImageButton.setImageResource(R.drawable.ic_shopping_cart);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                holder.animatorSet.start();
                notifyDataSetChanged();
                accessory.setAddedToCart(true);
                holder.addToCartImageButton.setOnClickListener(null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return accessories.size();
    }

    static class AccessoryCardViewHolder extends RecyclerView.ViewHolder {

        NetworkImageView accessoryImage;
        TextView accessoryTitle;
        TextView accessoryPrice;
        MaterialCardView card;
        ImageButton addToCartImageButton;
        AnimatorSet animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(MyApplication.getAppContext(),R.animator.flip);


        AccessoryCardViewHolder(@NonNull View itemView) {
           super(itemView);
           accessoryImage = itemView.findViewById(R.id.add_to_cart);
           accessoryTitle = itemView.findViewById(R.id.accessory_cart_title);
           accessoryPrice = itemView.findViewById(R.id.accessory_cart_description);
           card = itemView.findViewById(R.id.accessory_card_view);
           addToCartImageButton = itemView.findViewById(R.id.add_to_cart_image_button);
       }
   }
}
