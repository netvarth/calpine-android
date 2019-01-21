package com.nv.youneverwait.callback;

import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by sharmila on 25/9/18.
 */

public interface FavAdapterOnCallback {

    void onMethodViewCallback(int value, ArrayList<String> ids,  RecyclerView mrRecylce_favloc,int uniqueID,String title);

    void onMethodMessageCallback(String accountID, String message,  BottomSheetDialog mBottomDialog);

    void onMethodSearchDetailCallback(int uniqueiD);

    void onMethodPrivacy(int ProviderID,boolean revelPhoneNumber, BottomSheetDialog mBottomDialog);

    void onMethodDeleteFavourite(int ProviderID);
}
