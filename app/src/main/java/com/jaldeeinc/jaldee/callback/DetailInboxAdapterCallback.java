package com.jaldeeinc.jaldee.callback;

import android.content.Intent;

import java.io.File;

/**
 * Created by sharmila on 26/9/18.
 */

public interface DetailInboxAdapterCallback {

    void onMethodCallback(String waitlistId,int accountID,long timestamp);
    //void onMethodCallbackAttach(String waitlistId, int accountID, long timestamp, int PICKFILE_RESULT_CODE, File attachment) ;

}
