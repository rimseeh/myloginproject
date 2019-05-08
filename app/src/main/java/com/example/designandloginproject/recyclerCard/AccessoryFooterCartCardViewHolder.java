package com.example.designandloginproject.recyclerCard;

import android.view.View;
import android.widget.TextView;

import com.example.designandloginproject.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class AccessoryFooterCartCardViewHolder extends RecyclerView.ViewHolder {
    TextView totalTextView;

    AccessoryFooterCartCardViewHolder(@NonNull View itemView) {
        super(itemView);
        totalTextView=itemView.findViewById(R.id.total_cart_text_view);
    }
}
