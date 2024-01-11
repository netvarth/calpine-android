package com.jaldeeinc.customapp;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.NonNull;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = FirebaseMessagingService.class.getSimpleName();

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
            sendNotification(notificationType, title, body, type, bookingId, accountId, meetingLink, remoteMessage.getData());

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

        Log.e("$$$&#^$&#^&$&#&$*#$*&#$", s);

    }

    private void storeRegIdInPref(String token) {
        String sh = Constants.SHARED_PREF;
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }

    private void sendNotification(final String notificationType, final String title, final String text, final String type, final String id, String accountId, String meetingLink, Map<String, String> notificationData) {

        BasicNotification basicNotification = new BasicNotification();
        switch (notificationType) {
            case "NOTIFICATION":
                basicNotification = new BasicNotification();
                basicNotification.notify(getApplicationContext(), title, text, type, id, accountId, meetingLink, notificationData);
                break;
        }
    }
}