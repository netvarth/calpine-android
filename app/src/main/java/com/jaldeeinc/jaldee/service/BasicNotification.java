package com.jaldeeinc.jaldee.service;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.BillActivity;
import com.jaldeeinc.jaldee.activities.BookingDetails;
import com.jaldeeinc.jaldee.activities.ChatActivity;
import com.jaldeeinc.jaldee.activities.CheckInDetails;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.activities.Home;
import com.jaldeeinc.jaldee.activities.OrderDetailActivity;
import com.jaldeeinc.jaldee.activities.PaymentDetail;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.response.ActiveAppointment;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BasicNotification {

    NotificationManager manager;

    /**
     * The unique identifier for this type of notification.
     */
    private static final String NOTIFICATION_TAG = "Basic";
    private static final String CHANNEL_ID = "Default";

    /**
     * Shows the notification, or updates a previously shown notification of
     * this type, with the given parameters.
     * <p>
     * TODO: Customize this method's arguments to present relevant content in
     * the notification.
     * <p>
     * TODO: Customize the contents of this method to tweak the behavior and
     * presentation of basic notifications. Make
     * sure to follow the
     * <a href="https://developer.android.com/design/patterns/notifications.html">
     * Notification design guidelines</a> when doing so.
     *
     * @see #cancel(Context)
     */
    public static void notify(final Context context, final String titleString, final String textString, String activityString, final String bookingId, String accountId, String meetingLinkString) {
        final Resources res = context.getResources();

        // This image is used as the notification's large icon (thumbnail).
        // TODO: Remove this if your notification has no relevant thumbnail.
        final Bitmap picture = BitmapFactory.decodeResource(res, R.mipmap.ynw_logo);


        final String ticker = titleString;
        final String title = titleString;
        final String text = textString;
        final String meetingLink = meetingLinkString;
        final PendingIntent pendingIntent;
        final Intent intent;
        int number = generateRandom();
        Log.e("###############", String.valueOf(number));


        switch (activityString) {

            case "Home":

                intent = new Intent(context, Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                pendingIntent = PendingIntent.getActivity(context, number, intent, 0);
                break;

            case "CONSUMER_WAITLIST":

                intent = new Intent(context, CheckInDetails.class);
                intent.putExtra("uuid", bookingId);
                intent.putExtra("account", accountId);
                intent.putExtra(Constants.PUSH_NOTIFICATION, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(context, number, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                break;

            case "CONSUMER_APPT":

                intent = new Intent(context, BookingDetails.class);
                intent.putExtra("uuid", bookingId);
                intent.putExtra("account", accountId);
                intent.putExtra(Constants.PUSH_NOTIFICATION, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(context, number, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                break;

            case "CONSUMER_ORDER":
            case "CONSUMER_ORDER_STATUS":

                intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("uuid", bookingId);
                intent.putExtra("account", accountId);
                intent.putExtra(Constants.PUSH_NOTIFICATION, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(context, number, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                break;

            case "CONSUMER_DONATION_SERVICE":
            case "PAYMENTFAIL":

                intent = new Intent(context, Home.class);
                intent.putExtra("uuid", bookingId);
                intent.putExtra("account", accountId);
                intent.putExtra("message", textString);
                intent.putExtra(Constants.PUSH_NOTIFICATION, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                pendingIntent = PendingIntent.getActivity(context, number, intent, PendingIntent.FLAG_ONE_SHOT);
                break;

            case "BILL":

                intent = new Intent(context, BillActivity.class);
                intent.putExtra("ynwUUID", bookingId);
                intent.putExtra("accountID", accountId);
                intent.putExtra(Constants.PUSH_NOTIFICATION, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(context, number, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                break;

            case "BILL_PAYMENT_SUCCESS":
            case "CONSUMER_SHARE_PRESCRIPTION":
            case "CONSUMER_SHARE_MEDICAL_RECODE":
            case "PRE_PAYMENT_SUCCESS":

                if (bookingId != null) {

                    if (bookingId.contains("_wl")) {
                        intent = new Intent(context, CheckInDetails.class);
                    } else if (bookingId.contains("_appt")) {
                        intent = new Intent(context, BookingDetails.class);
                    } else {
                        intent = new Intent(context, OrderDetailActivity.class);
                    }
                } else {
                    intent = new Intent(context, Home.class);
                }
                intent.putExtra("uuid", bookingId);
                intent.putExtra("account", accountId);
                intent.putExtra("click_action", activityString);
                intent.putExtra(Constants.PUSH_NOTIFICATION, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(context, number, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                break;

            case "MASSCOMMUNICATION":

                intent = new Intent(context, ChatActivity.class);
                intent.putExtra("uuid", bookingId);
                intent.putExtra(Constants.PUSH_NOTIFICATION, true);
                if (accountId != null) {
                    intent.putExtra("accountId", Integer.parseInt(accountId));
                }
                intent.putExtra("name", titleString);
                if (bookingId != null) {
                    if (bookingId.contains("_appt")) {
                        intent.putExtra("from", Constants.APPOINTMENT);
                    } else if (bookingId.contains("_odr")) {
                        intent.putExtra("from", Constants.ORDERS);
                    } else if (bookingId.contains("_wl")) {
                        intent.putExtra("from", Constants.CHECKIN);
                    } else if (bookingId.contains("_dtn")) {
                        intent.putExtra("from", Constants.DONATION);
                    } else {
                        intent.putExtra("from", Constants.PROVIDER);
                    }
                } else {
                    intent.putExtra("from", Constants.PROVIDER);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(context, number, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                break;
            case "INSTANT_VIDEO":

                intent = new Intent(context, Home.class);
                intent.putExtra("click_action", activityString);
                intent.putExtra("meetingLink", meetingLink);
                intent.putExtra(Constants.PUSH_NOTIFICATION, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(context, number, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                break;


            default:
                pendingIntent = null;
                break;

        }


        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)

                // Set appropriate defaults for the notification light, sound,
                // and vibration.
                .setDefaults(Notification.DEFAULT_ALL)

                // Set required fields, including the small icon, the
                // notification title, and text.
                .setSmallIcon(R.mipmap.ynw_logo)
                .setContentTitle(title)
                .setContentText(text)


                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(textString))

                // All fields below this line are optional.

                // Use a default priority (recognized on devices running Android
                // 4.1 or later)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

                // Provide a large icon, shown with the notification in the
                // notification drawer on devices running Android 3.0 or later.
                .setLargeIcon(picture)

                // Set ticker text (preview) information for this notification.
                .setTicker(ticker)

                // Show a number. This is useful when stacking notifications of
                // a single type.
                .setNumber(number)

                // If this notification relates to a past or upcoming event, you
                // should set the relevant time information using the setWhen
                // method below. If this call is omitted, the notification's
                // timestamp will by set to the time at which it was shown.
                // TODO: Call setWhen if this notification relates to a past or
                // upcoming event. The sole argument to this method should be
                // the notification timestamp in milliseconds.
                //.setWhen(...)

                // Set the pending intent to be initiated when the user touches
                // the notification.

                .setContentIntent(pendingIntent)

                // Automatically dismiss the notification when it is touched.
                .setAutoCancel(true);

        notify(context, builder.build(), number);
    }

    public static int generateRandom() {
        Random random = new Random();
        return random.nextInt(9999 - 1000) + 1000;
    }


    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification, int number) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            // notificationId is a unique int for each notification that you must define
            //notificationManager.notify(0, notification);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        "Default Channel", NotificationManager.IMPORTANCE_HIGH);
                channel.setLightColor(Color.GREEN);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                nm.createNotificationChannel(channel);
            }
            nm.notify(NOTIFICATION_TAG, number, notification);
            Log.e("###############", String.valueOf(number));
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
        }
    }


    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG, 0);
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context, int NotificationId) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG, NotificationId);
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }


}


