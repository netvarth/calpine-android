package com.nv.youneverwait.callback;

/**
 * Created by sharmila on 26/10/18.
 */

public interface HistoryAdapterCallback {

    void onMethodMessageCallback(String ynwuuid,String accountID,String provider);
    void onMethodBillIconCallback(String payStatus,String value,String provider,String accountID);

    void onMethodDelecteCheckinCallback(String ynwuuid,int accountID,boolean todayflag,boolean futflag,boolean oldflag);
    void onMethodActiveCallback(String value);
    void onMethodAddFavourite(int value,boolean todayflag,boolean futflag,boolean oldflag);
    void onMethodDeleteFavourite(int value,boolean todayflag,boolean futflag,boolean oldflag);
    void onMethodRating(String accountID,String UUID,boolean todayflag,boolean futflag,boolean oldflag);

}
