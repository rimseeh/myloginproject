package com.example.designandloginproject.recyclerCard;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.example.designandloginproject.R;
import com.example.designandloginproject.models.Accessory;
import com.example.designandloginproject.network.ImageRequester;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Recycler View Adapter for the cart fragment.
 * This Recycler View Adapter has three different View Holders.
 * The first is for the cart.
 * The second is for calculating the total cost at the bottom.
 * The third is empty card in the bottom.
 */
public class AccessoryCartCardRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_CART = 0;
    private static final int VIEW_TYPE_CART_FOOTER = 1;
    private static final int VIEW_TYPE_EMPTY_CART_FOOTER = 2;
    private List<Accessory> cartAccessories;
    private ImageRequester cartImageRequester;
    private double total = 0;

    /**
     * Constructor for the Recycler view adapter.
     *
     * @param cartAccessories List that has all the carts to display.
     */
    public AccessoryCartCardRecyclerViewAdapter(List<Accessory> cartAccessories) {
        this.cartAccessories = cartAccessories;
        cartImageRequester = ImageRequester.getInstance();
    }

    /**
     * Used to initialize the view holder to each holder separately.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView;
        RecyclerView.ViewHolder holder;
        if (viewType == VIEW_TYPE_CART) {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_card, parent, false);
            holder = new AccessoryCartCardViewHolder(layoutView);
        } else if (viewType == VIEW_TYPE_CART_FOOTER) {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_footer_card, parent, false);
            holder = new AccessoryFooterCartCardViewHolder(layoutView);
        } else {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_card, parent, false);
            holder = new AccessoryEmptyFooterCartCardViewHolder(layoutView);
        }
        return holder;
    }

    /**
     * Add the view to the recycler view
     *
     * @param viewHolder view holder that is added to the recycler view
     * @param position   The position of the array list to add the recycler view
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        // Add the accessory to the recycler view by changing the image, title, description, and price
        // add listener for the remove image button
        if (viewHolder instanceof AccessoryCartCardViewHolder) {
            AccessoryCartCardViewHolder holder = (AccessoryCartCardViewHolder) viewHolder;
            Accessory accessory = cartAccessories.get(position);
            holder.titleTextView.setText(accessory.getTitle());
            holder.descriptionTextView.setText(accessory.getDescription());
            holder.priceTextView.setText(accessory.getPrice() + "$");
            cartImageRequester.setImageFromUrl(holder.networkImageView, accessory.getUrl());
            holder.removeImageButton.setOnClickListener(v -> removeFromDataBaseAndRecyclerView(accessory, holder, position));
        }
        // Calculate the total and add it to the Recycler View
        if (viewHolder instanceof AccessoryFooterCartCardViewHolder) {
            AccessoryFooterCartCardViewHolder holder = (AccessoryFooterCartCardViewHolder) viewHolder;
            this.total = 0;
            setTotal();
            holder.totalTextView.setText("" + this.total + "$");
        }
    }

    /**
     * Remove the selected view from the recycler view and the data base
     *
     * @param accessory Accessory to be removed from the database and the recycler view.
     * @param holder    Accessory card view to be removed from the recycler view.
     * @param position  The position of the recycler view to be removed.
     */
    private void removeFromDataBaseAndRecyclerView(Accessory accessory, AccessoryCartCardViewHolder holder, int position) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("cart/" + mAuth.getUid());
        databaseReference.child(accessory.getId()).removeValue().addOnSuccessListener(task -> {
            holder.removeImageButton.setRotation(90f);
            cartAccessories.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartAccessories.size() + 2);
        });
    }

    /**
     * Get the number of elements to be added to the recycler view.
     */
    @Override
    public int getItemCount() {
        return cartAccessories.size() + 2;
    }

    /**
     * Get the type of the card to be added to the recycler view according to position.
     *
     * @param position Position of the card to be added to the recycler view.
     */
    @Override
    public int getItemViewType(int position) {
        if (position < cartAccessories.size()) {
            return VIEW_TYPE_CART;
        } else if (position == getItemCount() - 2) {
            return VIEW_TYPE_CART_FOOTER;
        }
        return VIEW_TYPE_EMPTY_CART_FOOTER;
    }

    /**
     * Calculate the total price of the accessories added to the Recycler View.
     */
    private void setTotal() {
        for (Accessory accessory : cartAccessories) {
            this.total = this.total + Double.parseDouble(accessory.getPrice());
        }
    }

    /**
     * View Holder class for the accessories.
     * Binding each view in the card to the view from the layout.
     */
    static class AccessoryCartCardViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        TextView priceTextView;
        NetworkImageView networkImageView;
        ImageButton removeImageButton;

        AccessoryCartCardViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.accessory_cart_title_text_view);
            descriptionTextView = itemView.findViewById(R.id.accessory_cart_description_text_view);
            priceTextView = itemView.findViewById(R.id.accessory_cart_money_text_view);
            networkImageView = itemView.findViewById(R.id.accessory_cart_network_image_view);
            removeImageButton = itemView.findViewById(R.id.remove_image_button);
        }
    }

    /**
     * View Holder class for total price.
     * Binding the price view in the card to the view from the layout.
     */
    static class AccessoryFooterCartCardViewHolder extends RecyclerView.ViewHolder {
        TextView totalTextView;

        AccessoryFooterCartCardViewHolder(@NonNull View itemView) {
            super(itemView);
            totalTextView = itemView.findViewById(R.id.total_cart_text_view);
        }
    }

    /**
     * View Holder empty to extend the recycler view.
     */
    static class AccessoryEmptyFooterCartCardViewHolder extends RecyclerView.ViewHolder {
        AccessoryEmptyFooterCartCardViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
