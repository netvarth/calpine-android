package com.nv.youneverwait.callback;

/**
 * Created by sharmila on 25/9/18.
 */

public interface ActiveAdapterOnCallback {

    void onMethodActiveCallback(String value);
    void onMethodActiveBillIconCallback(String value,String provider);
    void onMethodMessageCallback(String ynwuuid,String accountID,String provider);

    void onMethodDelecteCheckinCallback(String ynwuuid,int accountID);
}
