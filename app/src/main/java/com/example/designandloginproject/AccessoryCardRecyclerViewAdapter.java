package com.example.designandloginproject;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.ablanco.zoomy.Zoomy;
import com.example.designandloginproject.models.Accessory;
import com.example.designandloginproject.network.ImageRequester;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter used to show a simple grid of products.
 */
public class AccessoryCardRecyclerViewAdapter extends RecyclerView.Adapter<AccessoryCardViewHolder> {

    private List<Accessory> accessories;
    private ImageRequester imageRequester;
    private Activity activity;

    AccessoryCardRecyclerViewAdapter(List<Accessory> accessories,Activity activity) {
        this.accessories = accessories;
        imageRequester = ImageRequester.getInstance();
        this.activity=activity;
    }

    @NonNull
    @Override
    public AccessoryCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.accessory_card, parent, false);
        return new AccessoryCardViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull AccessoryCardViewHolder holder, int position) {
        // TODO: Put ViewHolder binding code here in MDC-102
        if (accessories != null && position < accessories.size()) {
            Accessory accessory = accessories.get(position);
            holder.productTitle.setText(accessory.getTitle());
            holder.productPrice.setText(accessory.getPrice());
            imageRequester.setImageFromUrl(holder.productImage, accessory.getUrl());
            Zoomy.Builder builder = new Zoomy.Builder(activity).target(holder.productImage);
            builder.register();
        }
    }

    @Override
    public int getItemCount() {
        return accessories.size();
    }
}
