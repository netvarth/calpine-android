package com.jaldeeinc.jaldee.callback;

/**
 * Created by sharmila on 25/9/18.
 */

public interface ActiveAdapterOnCallback {

    void onMethodActiveCallback(String value);
    void onMethodActiveBillIconCallback(String payStatus,String value,String provider,String accountID,String consumer);
    void onMethodActivePayIconCallback(String payStatus, String value, String provider, String accountID,double amountDue) ;


}
