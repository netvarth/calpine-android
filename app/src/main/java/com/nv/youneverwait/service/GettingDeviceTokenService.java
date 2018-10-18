package com.nv.youneverwait.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.nv.youneverwait.common.Config;

/**
 * Created by sharmila on 17/10/18.
 */

public class GettingDeviceTokenService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String DeviceToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("DeviceToken ==> ",  DeviceToken);
        Config.logV("Device Token-------------------"+DeviceToken);
    }

}