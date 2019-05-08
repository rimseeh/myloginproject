package com.example.designandloginproject.recyclerCard;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.example.designandloginproject.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

 class AccessoryCartCardViewHolder extends RecyclerView.ViewHolder {
     TextView titleTextView;
     TextView descriptionTextView;
     TextView priceTextView;
     NetworkImageView networkImageView;
     ImageButton removeImageButton;
     AccessoryCartCardViewHolder(@NonNull View itemView) {
        super(itemView);
        titleTextView=itemView.findViewById(R.id.accessory_cart_title_text_view);
        descriptionTextView=itemView.findViewById(R.id.accessory_cart_description_text_view);
        priceTextView =itemView.findViewById(R.id.accessory_cart_money_text_view);
        networkImageView = itemView.findViewById(R.id.accessory_cart_network_image_view);
        removeImageButton = itemView.findViewById(R.id.remove_image_button);
    }
}
