package com.jaldeeinc.jaldee.callback;

import android.content.Intent;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sharmila on 26/9/18.
 */

public interface DetailInboxAdapterCallback {

    void onMethodCallback(String waitlistId, String accountID, long timestamp);

}
