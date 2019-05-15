package com.example.designandloginproject.animation;

import android.view.View;

public interface CartChangeListener {
    public void onCartChange(String menuid, int count, int price, View cardv, int[] position);

}
