package com.example.designandloginproject;

import androidx.fragment.app.Fragment;

/**
 * A host (typically an {@code Activity}} that can display fragments and knows how to respond to
 * navigation events.
 */
public interface NavigationHost {
    /**
     * Trigger a navigation to the specified fragment, optionally adding a transaction to the back
     * stack to make this navigation reversible.
     */
    void navigateTo(Fragment fragment, boolean addToBackStack);

    /**
     * Trigger a navigation to the specified fragment with animation,
     * optionally adding a transaction to the back
     * stack to make this navigation reversible.
     */
    void navigateToWithAnimation
    (Fragment fragment, boolean addToBackStack,
     int animationIn, int animationOut,int backAnimationIn,int backAnimationOut);

}
