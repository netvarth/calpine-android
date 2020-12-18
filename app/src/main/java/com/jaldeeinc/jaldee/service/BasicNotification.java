//package com.jaldeeinc.jaldee.service;
//
//import android.annotation.TargetApi;
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.Resources;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.net.Uri;
//import android.os.Build;
//
//import androidx.core.app.NotificationCompat;
//
//import com.jaldeeinc.jaldee.R;
//
//public class BasicNotification {
//
//    NotificationManager manager;
//
//    /**
//     * The unique identifier for this type of notification.
//     */
//    private static final String NOTIFICATION_TAG = "Basic";
//    private static final String CHANNEL_ID = "Default";
//
//    /**
//     * Shows the notification, or updates a previously shown notification of
//     * this type, with the given parameters.
//     * <p>
//     * TODO: Customize this method's arguments to present relevant content in
//     * the notification.
//     * <p>
//     * TODO: Customize the contents of this method to tweak the behavior and
//     * presentation of basic notifications. Make
//     * sure to follow the
//     * <a href="https://developer.android.com/design/patterns/notifications.html">
//     * Notification design guidelines</a> when doing so.
//     *
//     * @see #cancel(Context)
//     */
//    public static void notify(final Context context, final String titleString, final String textString, String activityString, final int number) {
//        final Resources res = context.getResources();
//
//        // This image is used as the notification's large icon (thumbnail).
//        // TODO: Remove this if your notification has no relevant thumbnail.
//        final Bitmap picture = BitmapFactory.decodeResource(res, R.mipmap.ynw_logo);
//
//
//        final String ticker = titleString;
//        final String title = titleString;
//        final String text = textString;
//        final PendingIntent pendingIntent;
//        final Intent intent;
//
//
////        switch (activityString) {
////
////            case "Home":
////
////                intent = new Intent(context, HomeActivity.class);
////                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
////                break;
////            case "MEETING_SCHEDULED":
////
////            case "NO_ATTENDEE":
////
////                intent = new Intent(context, MeetingAcceptance.class);
////                intent.putExtra("MEETINGID", number);
////                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
////                break;
////
////            case "MEETING_RESCHEDULED":
////
////                intent = new Intent(context, MeetingAcceptance.class);
////                intent.putExtra("MEETINGID", number);
////                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
////                break;
////
////            case "JOIN_MEETING":
////            case "SCHEDULE_REMINDER":
////            case "COHOST_ASSIGNED":
////
////                intent = new Intent(context, JoinMeeting.class);
////                intent.putExtra("MEETINGID", number);
////                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
////                break;
////
////            case "END_MEETING":
////            case "USER_DELETED":
////
////                intent = new Intent(context, HomeActivity.class);
////                intent.putExtra("MEETINGID", number);
////                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
////                break;
////            case "CANCEL_MEETING":
////            case "PDF_GENERATED":
////
////                intent = new Intent(context, HomeActivity.class);
////                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
////                break;
////            case "LOGIN":
////                intent = new Intent(context, LoginActivity.class);
////                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
////                break;
////
////            default:
////                pendingIntent = null;
////                break;
////
////        }
//
//
//        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
//
//                // Set appropriate defaults for the notification light, sound,
//                // and vibration.
//                .setDefaults(Notification.DEFAULT_ALL)
//
//                // Set required fields, including the small icon, the
//                // notification title, and text.
//                .setSmallIcon(R.mipmap.ynw_logo)
//                .setContentTitle(title)
//                .setContentText(text)
//
//
//                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText(textString))
//
//                // All fields below this line are optional.
//
//                // Use a default priority (recognized on devices running Android
//                // 4.1 or later)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//
//                // Provide a large icon, shown with the notification in the
//                // notification drawer on devices running Android 3.0 or later.
//                .setLargeIcon(picture)
//
//                // Set ticker text (preview) information for this notification.
//                .setTicker(ticker)
//
//                // Show a number. This is useful when stacking notifications of
//                // a single type.
//                //.setNumber(number)
//
//                // If this notification relates to a past or upcoming event, you
//                // should set the relevant time information using the setWhen
//                // method below. If this call is omitted, the notification's
//                // timestamp will by set to the time at which it was shown.
//                // TODO: Call setWhen if this notification relates to a past or
//                // upcoming event. The sole argument to this method should be
//                // the notification timestamp in milliseconds.
//                //.setWhen(...)
//
//                // Set the pending intent to be initiated when the user touches
//                // the notification.
//
////                .setContentIntent(pendingIntent)
//
//                // Automatically dismiss the notification when it is touched.
//                .setAutoCancel(true);
//
//        notify(context, builder.build());
//    }
//
//    public static void notify(final Context context, final String titleString, final String textString, String activityString, final int number, final int userResponse, boolean isOngoing) {
//        final Resources res = context.getResources();
//
//        // This image is used as the notification's large icon (thumbnail).
//        // TODO: Remove this if your notification has no relevant thumbnail.
//        final Bitmap picture = BitmapFactory.decodeResource(res, R.mipmap.ynw_logo);
//
//
//        final String ticker = titleString;
//        final String title = titleString;
//        final String text = textString;
//        final PendingIntent pendingIntent;
//        final Intent intent;
//
//
////        switch (activityString) {
////
////            case "PLAYSTORE":
////
////                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.elorce.logmeeting"));
////                pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
////                break;
////
////            case "Home":
////
////                intent = new Intent(context, HomeActivity.class);
////                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
////                break;
////            case "MEETING_SCHEDULED":
////
////            case "NO_ATTENDEE":
////
////                intent = new Intent(context, MeetingAcceptance.class);
////                intent.putExtra("MEETINGID", number);
////                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
////                break;
////
////            case "MEETING_RESCHEDULED":
////
////                if (userResponse == 0) {
////
////                    intent = new Intent(context, MeetingAcceptance.class);
////                    intent.putExtra("MEETINGID", number);
////                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                    pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
////                } else {
////                    intent = new Intent(context, JoinMeeting.class);
////                    intent.putExtra("MEETINGID", number);
////                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                    pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
////                }
////                break;
////
////            case "JOIN_MEETING":
////            case "COHOST_ASSIGNED":
////
////                intent = new Intent(context, JoinMeeting.class);
////                intent.putExtra("MEETINGID", number);
////                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
////                break;
////
////            case "SCHEDULE_REMINDER":
////
////                if (userResponse == 0) {
////
////                    intent = new Intent(context, MeetingAcceptance.class);
////                    intent.putExtra("MEETINGID", number);
////                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                    pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
////                    break;
////                } else {
////
////                    intent = new Intent(context, JoinMeeting.class);
////                    intent.putExtra("MEETINGID", number);
////                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                    pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
////                    break;
////                }
////
////            case "FREE_ATTENDEE":
////
////                intent = new Intent(context, MeetingAcceptance.class);
////                intent.putExtra("MEETINGID", number);
////                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
////                break;
////
////            case "END_MEETING":
////            case "USER_DELETED":
////
////                intent = new Intent(context, HomeActivity.class);
////                intent.putExtra("MEETINGID", number);
////                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
////                break;
////            case "CANCEL_MEETING":
////            case "PDF_GENERATED":
////
////                intent = new Intent(context, HomeActivity.class);
////                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
////                break;
////            case "LOGIN":
////                intent = new Intent(context, LoginActivity.class);
////                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
////                break;
////
////            default:
////                pendingIntent = null;
////                break;
////
////        }
//
//
//        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
//
//                // Set appropriate defaults for the notification light, sound,
//                // and vibration.
//                .setDefaults(Notification.DEFAULT_ALL)
//
//                // Set required fields, including the small icon, the
//                // notification title, and text.
//                .setSmallIcon(R.mipmap.ynw_logo)
//                .setContentTitle(title)
//                .setContentText(text)
//
//
//                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText(textString))
//
//                // All fields below this line are optional.
//
//                // Use a default priority (recognized on devices running Android
//                // 4.1 or later)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//
//                // Provide a large icon, shown with the notification in the
//                // notification drawer on devices running Android 3.0 or later.
//                .setLargeIcon(picture)
//
//                // Set ticker text (preview) information for this notification.
//                .setTicker(ticker)
//
//                // Show a number. This is useful when stacking notifications of
//                // a single type.
//                //.setNumber(number)
//
//                // If this notification relates to a past or upcoming event, you
//                // should set the relevant time information using the setWhen
//                // method below. If this call is omitted, the notification's
//                // timestamp will by set to the time at which it was shown.
//                // TODO: Call setWhen if this notification relates to a past or
//                // upcoming event. The sole argument to this method should be
//                // the notification timestamp in milliseconds.
//                //.setWhen(...)
//
//                // Set the pending intent to be initiated when the user touches
//                // the notification.
//
////                .setContentIntent(pendingIntent)
//                .setOngoing(isOngoing)
//
//
//                // Automatically dismiss the notification when it is touched.
//                .setAutoCancel(true);
//
//        notify(context, builder.build());
//    }
//
//
//    @TargetApi(Build.VERSION_CODES.ECLAIR)
//    private static void notify(final Context context, final Notification notification) {
//        final NotificationManager nm = (NotificationManager) context
//                .getSystemService(Context.NOTIFICATION_SERVICE);
//        //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
//            // notificationId is a unique int for each notification that you must define
//            //notificationManager.notify(0, notification);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
//                        "Default Channel", NotificationManager.IMPORTANCE_DEFAULT);
//                channel.setLightColor(Color.GREEN);
//                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//                nm.createNotificationChannel(channel);
//            }
//            nm.notify(NOTIFICATION_TAG, 0, notification);
//        } else {
//            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
//        }
//    }
//
//
//    @TargetApi(Build.VERSION_CODES.ECLAIR)
//    public static void cancel(final Context context) {
//        final NotificationManager nm = (NotificationManager) context
//                .getSystemService(Context.NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
//            nm.cancel(NOTIFICATION_TAG, 0);
//        } else {
//            nm.cancel(NOTIFICATION_TAG.hashCode());
//        }
//    }
//
//    @TargetApi(Build.VERSION_CODES.ECLAIR)
//    public static void cancel(final Context context, int NotificationId) {
//        final NotificationManager nm = (NotificationManager) context
//                .getSystemService(Context.NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
//            nm.cancel(NOTIFICATION_TAG, NotificationId);
//        } else {
//            nm.cancel(NOTIFICATION_TAG.hashCode());
//        }
//    }
//
//}
//
//
