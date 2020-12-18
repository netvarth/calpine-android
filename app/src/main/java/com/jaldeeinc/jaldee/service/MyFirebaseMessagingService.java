//package com.jaldeeinc.jaldee.service;
//
//import android.content.Intent;
//import android.media.Ringtone;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//
//
//public class MyFirebaseMessagingService extends FirebaseMessagingService {
//    private static final String TAG = "NOTIFICATION DATA";
//    private static final String[] ACTION_TYPES = {};
//
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
//
//        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//        }
//
//        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//        }
//        try {
//            String title = remoteMessage.getData().get("title");
//            String body = remoteMessage.getData().get("message");
//            String type = remoteMessage.getData().get("status");
//            String id = remoteMessage.getData().get("meeting_id");
//            String response = remoteMessage.getData().get("user_status");
//            response = response == null ? "1" : response;
//            int userResponse =  Integer.parseInt(response);
//
//            String notificationType = "NOTIFICATION";
//
////            sendNotification(notificationType, title, body, type, requestId,userResponse);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    public void onNewToken(@NonNull String token) {
//
//    }
//
//    private void sendNotification(final String notificationType, final String title, final String text, final String type, final int id, int userResponse) {
//
//        BasicNotification basicNotification = new BasicNotification();
//        ActionNotification actionNotification = new ActionNotification();
//        switch (notificationType) {
//            case "NOTIFICATION":
//                basicNotification = new BasicNotification();
//                basicNotification.notify(getApplicationContext(), title, text, type, id,userResponse,false);
//
//                break;
//
//
//            case "CALL":
//
//                break;
//
//            case "ACTION":
//                basicNotification = new BasicNotification();
//                basicNotification.notify(getApplicationContext(), title, text, type, id);
//
//                break;
//
//            case "ACTION_NOTIFICATION":
//                actionNotification = new ActionNotification();
//                actionNotification.notify(getApplicationContext(), title, text, type, id);
//
//                break;
//        }
//        playSound();
//    }
//
//    private void playSound() {
//        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//        r.play();
//    }
//}