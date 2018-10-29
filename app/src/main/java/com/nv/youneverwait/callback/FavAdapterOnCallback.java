package com.nv.youneverwait.callback;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by sharmila on 25/9/18.
 */

public interface FavAdapterOnCallback {

    void onMethodViewCallback(int value, ArrayList<Integer> ids,  RecyclerView mrRecylce_favloc);


}
