package com.example.designandloginproject.recyclerCard;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.designandloginproject.R;
import com.example.designandloginproject.models.Accessory;
import com.example.designandloginproject.network.ImageRequester;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AccessoryCartCardRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_CART = 0;
    private static final int VIEW_TYPE_CART_FOOTER = 1;
    private List<Accessory> cartAccessories;
    private ImageRequester cartImageRequester;
    private double total = 0;

    public AccessoryCartCardRecyclerViewAdapter(List<Accessory> cartAccessories) {
        this.cartAccessories = cartAccessories;
        cartImageRequester = ImageRequester.getInstance();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView;
        RecyclerView.ViewHolder holder = null;
        if (viewType == VIEW_TYPE_CART) {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_card, parent, false);
            holder = new AccessoryCartCardViewHolder(layoutView);
        }
        else {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_footer_card, parent, false);
            holder = new AccessoryFooterCartCardViewHolder(layoutView);

        }
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof AccessoryCartCardViewHolder) {
            AccessoryCartCardViewHolder holder = (AccessoryCartCardViewHolder) viewHolder;
            Accessory accessory = cartAccessories.get(position);
            holder.titleTextView.setText(accessory.getTitle());
            holder.descriptionTextView.setText(accessory.getDescription());
            holder.priceTextView.setText(accessory.getPrice());
            cartImageRequester.setImageFromUrl(holder.networkImageView, accessory.getUrl());
            holder.removeImageButton.setOnClickListener(v -> {
                holder.removeImageButton.setRotation(90f);
                cartAccessories.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartAccessories.size() + 1);
            });

        }
        if (viewHolder instanceof AccessoryFooterCartCardViewHolder) {
                AccessoryFooterCartCardViewHolder holder = (AccessoryFooterCartCardViewHolder) viewHolder;
                this.total = 0;
                setTotal();
                holder.totalTextView.setText("" + this.total);
        }
    }

    @Override
    public int getItemCount() {
        return cartAccessories.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == cartAccessories.size()) {
            return VIEW_TYPE_CART_FOOTER;
        }
        return VIEW_TYPE_CART;
    }

    private void setTotal() {
        for (Accessory accessory : cartAccessories) {
            this.total = this.total + Double.parseDouble(accessory.getPrice());
        }
    }
}
