package com.example.designandloginproject.recyclerCard;


import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.example.designandloginproject.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

 class AccessoryCardViewHolder extends RecyclerView.ViewHolder {

     NetworkImageView accessoryImage;
     TextView accessoryTitle;
     TextView accessoryPrice;

     AccessoryCardViewHolder(@NonNull View itemView) {
        super(itemView);
        accessoryImage = itemView.findViewById(R.id.product_image);
        accessoryTitle = itemView.findViewById(R.id.accessory_cart_title);
        accessoryPrice = itemView.findViewById(R.id.accessory_cart_description);
    }
}
