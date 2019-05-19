package com.example.designandloginproject.animation;

import android.view.View;

/**
 * Interface for cart animation
 * used to translate the accessory cart sheet towards the cart ImageView
 * in the bottom right corner
 */
public interface CartChangeListener {
    public void onCartChange( View viewCart, int[] position);

}
