package com.example.designandloginproject.recyclerCard;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
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
import androidx.recyclerview.widget.RecyclerView;

/**
 * Recycler View Adapter for the Accessory fragment
 * this Recycler View Adapter has three different View Holders
 * the first is for the cart
 * the second is for calculating the total cost at the bottom
 * the third is empty card in the bottom
 */
public class AccessoryCardRecyclerViewAdapter extends RecyclerView.Adapter<AccessoryCardRecyclerViewAdapter.AccessoryCardViewHolder> {

    private List<Accessory> mAccessories;
    private ImageRequester mImageRequester;
    private Activity mActivity;
    private CartChangeListener mOnQuantityChange;

    /**
     * Constructor for the Recycler view adapter
     *
     * @param mAccessories      list that has all the accessories to display
     * @param mActivity         activity holding the recycle view
     * @param mOnQuantityChange listener to firebase
     */
    public AccessoryCardRecyclerViewAdapter(List<Accessory> mAccessories,
                                            Activity mActivity,
                                            CartChangeListener mOnQuantityChange) {
        this.mAccessories = mAccessories;
        this.mImageRequester = ImageRequester.getInstance();
        this.mActivity = mActivity;
        this.mOnQuantityChange = mOnQuantityChange;
    }
    /**
     * Used to initialize the view holder
     */
    @NonNull
    @Override
    public AccessoryCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.accessory_card, parent, false);
        return new AccessoryCardViewHolder(layoutView);
    }

    /**
     * Add the view to the recycler view
     *
     * @param viewHolder view holder that is added to the recycler view
     * @param position   the position of the array list to add the recycler view
     */
    @Override
    public void onBindViewHolder(@NonNull AccessoryCardViewHolder viewHolder, int position) {
        if (mAccessories != null && position < mAccessories.size()) {
            Accessory accessory = mAccessories.get(position);
            viewHolder.accessoryTitle.setText(accessory.getTitle());
            viewHolder.accessoryPrice.setText(accessory.getPrice());
            mImageRequester.setImageFromUrl(viewHolder.accessoryImage, accessory.getUrl());
            Zoomy.Builder builder = new Zoomy.Builder(mActivity).target(viewHolder.accessoryImage);
            builder.register();
            if (!accessory.isAddedToCart()) {
                viewHolder.addToCartImageButton.setOnClickListener(v -> addToCartFireBase(accessory, viewHolder));
            } else {
                viewHolder.addToCartImageButton.setImageResource(R.drawable.ic_shopping_cart);
            }
        }
    }

    /**
     * adding the selected accessory to the FireBase cart and preform animation
     *
     * @param accessory  accessory to be added
     * @param viewHolder view holder from where the accessory is added
     */
    private void addToCartFireBase(Accessory accessory, AccessoryCardViewHolder viewHolder) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String id = accessory.getId();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("cart/" + mAuth.getUid());
        databaseReference.child(id).setValue("true").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                addToCartAnimation(viewHolder, accessory);
            }
        });
    }

    /**
     * perform animation for the added accessory
     *
     * @param holder holder where the accessory is added
     * @param accessory to be animated
     */
    private void addToCartAnimation(AccessoryCardViewHolder holder, Accessory accessory) {
        int[] e = new int[2];
        holder.card.getLocationOnScreen(e);
        mOnQuantityChange.onCartChange(accessory.getId(), 1, Integer.parseInt(accessory.getPrice()), holder.accessoryImage, e);
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

    /**
     * Get the number of elements to be added to the recycler view
     */
    @Override
    public int getItemCount() {
        return mAccessories.size();
    }

    /**
     * View Holder class for the accessories
     * Binding each view in the card to the view from the layout
     */
    static class AccessoryCardViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView accessoryImage;
        TextView accessoryTitle;
        TextView accessoryPrice;
        MaterialCardView card;
        ImageButton addToCartImageButton;
        AnimatorSet animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(MyApplication.getAppContext(), R.animator.flip);

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
