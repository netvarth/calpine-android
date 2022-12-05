package com.jaldeeinc.jaldee.service;

import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jaldeeinc.jaldee.common.Config;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = FirebaseMessagingService.class.getSimpleName();
    private static final String[] ACTION_TYPES = {"CONSUMER_WAITLIST", "BILL", "PAYMENTSUCCESS", "PAYMENTFAIL", "VIRTUAL_SERVICE",
            "CONSUMER_DONATION_SERVICE", "CONSUMER_SHARE_PRESCRIPTION", "CONSUMER_SHARE_MEDICAL_RECODE", "CONSUMER_APPT",
            "PROVIDER_APPT", "CONSUMER_ORDER_STATUS", "CONSUMER_ORDER", "MASSCOMMUNICATION"};

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        try {
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            String type = remoteMessage.getData().get("click_action");
            String bookingId = remoteMessage.getData().get("uuid");
            String accountId = remoteMessage.getData().get("account");
            String notificationType = "NOTIFICATION";
            String meetingLink = remoteMessage.getData().get("link");

            sendNotification(notificationType, title, body, type, bookingId, accountId, meetingLink);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onNewToken(@NonNull String s) {

        String refreshedToken = s;

        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // sending reg id to your server


        // Notify UI that registration has completed, so the progress indicator can be hidden.

        Config.logV("TOKEN REFRESH #################__________________" + refreshedToken);

        Log.e("$$$&#^$&#^&$&#&$*#$*&#$", s);
        Config.ApiUpdateToken(this);

    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }


    private void sendNotification(final String notificationType, final String title, final String text, final String type, final String id, String accountId, String meetingLink) {

        BasicNotification basicNotification = new BasicNotification();
//        ActionNotification actionNotification = new ActionNotification();
        switch (notificationType) {
            case "NOTIFICATION":
                basicNotification = new BasicNotification();
                basicNotification.notify(getApplicationContext(), title, text, type, id, accountId, meetingLink);

                break;

        }
//        playSound();
    }

    private void playSound() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
    }
}