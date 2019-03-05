package com.nv.youneverwait.service;



import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.utils.SharedPreference;


public class FirebaseInstanceIDService extends FirebaseService {
    private static final String TAG = FirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.

        Config.logV("TOKEN REFRESH____EEEEEE______@@@@@@________"+refreshedToken);

       Config.ApiUpdateToken(this);

        /*String loginId = SharedPreference.getInstance(this).getStringValue("mobno", "");
        String password = SharedPreference.getInstance(this).getStringValue("password", "");
        if(!loginId.equalsIgnoreCase("")&&!password.equalsIgnoreCase("")) {
            Config.ApiSessionResetLogin(loginId,password,this);
        }*/
    }



    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }

}