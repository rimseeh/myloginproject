package com.example.designandloginproject;


import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AccessoryCardViewHolder extends RecyclerView.ViewHolder {

    public NetworkImageView productImage;
    public TextView productTitle;
    public TextView productPrice;

    public AccessoryCardViewHolder(@NonNull View itemView) {
        super(itemView);
        productImage = itemView.findViewById(R.id.product_image);
        productTitle = itemView.findViewById(R.id.product_title);
        productPrice = itemView.findViewById(R.id.product_price);
    }
}
