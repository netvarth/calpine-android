package com.nv.youneverwait.callback;

/**
 * Created by sharmila on 26/10/18.
 */

public interface HistoryAdapterCallback {

    void onMethodMessageCallback(String ynwuuid,String accountID,String provider);
    void onMethodBillIconCallback(String value,String provider);

    void onMethodDelecteCheckinCallback(String ynwuuid,int accountID);
    void onMethodActiveCallback(String value);
    void onMethodAddFavourite(int value);
    void onMethodDeleteFavourite(int value);
    void onMethodRating(String accountID,String UUID);

}
