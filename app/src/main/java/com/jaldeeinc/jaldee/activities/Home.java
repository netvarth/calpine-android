package com.jaldeeinc.jaldee.activities;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jaldeeinc.jaldee.Fragment.CheckinsFragmentCopy;
import com.jaldeeinc.jaldee.Fragment.HomeTabFragment;
import com.jaldeeinc.jaldee.Fragment.SearchDetailViewFragment;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.NotificationDialog;
import com.jaldeeinc.jaldee.service.LiveTrackService;
import com.jaldeeinc.jaldee.utils.SharedPreference;


/**
 * Created by sharmila on 3/7/18.
 */
public class Home extends AppCompatActivity {

    HomeTabFragment mHomeTab;
    SearchDetailViewFragment searchDetailViewFragment;
    CheckinsFragmentCopy checkinsFragmentCopy;
    Context mContext;
    String detail;
    String path;
    String message = null;
    String from = null;


    Intent mLiveTrackClient;
    private LiveTrackService liveTrackService = new LiveTrackService();
    private boolean fromPushNotification = false;
    NotificationDialog notificationDialog;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            detail = extras.getString("detail_id", "");
            path = extras.getString("path", "");
            message = extras.getString("message", "");
            fromPushNotification = extras.getBoolean(Constants.PUSH_NOTIFICATION, false);
            Log.i("detailsofDetail", detail);
            Log.i("detailsofDetail", path);
        }

        if (notificationDialog != null && notificationDialog.isShowing()) {
            notificationDialog.dismiss();
        }
        Config.logV("Home Screen@@@@@@@@@@@@@@@@@@@");
        mContext = this;
        try {
            mLiveTrackClient = new Intent(Home.this, liveTrackService.getClass());
            startService(mLiveTrackClient);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (savedInstanceState == null) {
            // withholding the previously created fragment from being created again
            // On orientation change, it will prevent fragment recreation
            // its necessary to reserving the fragment stack inside each tab
            initScreen();


            Config.logV("Init Screen@@@@@@@@@@@@@@@@@@@");
        } else {
            // restoring the previously created fragment
            // and getting the reference
            Config.logV("RESTORE@@@@@@@@@@@@@@@@@");
            try {
                mHomeTab = (HomeTabFragment) getSupportFragmentManager().getFragments().get(0);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        SharedPreferences pref = mContext.getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        Config.logV("REGISTARION ID___3333####___________@@@@@@@___" + regId);


        Bundle b = getIntent().getExtras();
        // add these lines of code to get data from notification
        if (b != null) {
            String from = b.getString("message");

            if (from != null && !from.equalsIgnoreCase("")) {
                Config.logV("Push Notification Background@@@@@@@@@@@@@@@@@@@@@");

                String loginId = SharedPreference.getInstance(mContext).getStringValue("mobno", "");
                if (!loginId.equalsIgnoreCase("")) {
                    mHomeTab = new HomeTabFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString("tab", "1");
                    mHomeTab.setArguments(bundle);

                    final FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, mHomeTab)
                            .commit();
                } else {
                    Intent iLogin = new Intent(this, Register.class);
                    startActivity(iLogin);
                    finish();
                }
            }

        }

    }


    private void initScreen() {
        // Creating the ViewPager container fragment once

        if (message != null && !message.equalsIgnoreCase("")) {

            notificationDialog = new NotificationDialog(mContext, message);
            notificationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            notificationDialog.show();
            notificationDialog.setCancelable(false);
            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            int width = (int) (metrics.widthPixels * 1);
            notificationDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

        }
        if (detail != null && !detail.equalsIgnoreCase("")) {
            if (path.contains("status")) {
                mHomeTab = new HomeTabFragment();
                final FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, mHomeTab)
                        .commit();
            } else {
                Log.i("ghjghjgj", "detaillll");
                searchDetailViewFragment = new SearchDetailViewFragment();
                Bundle bundle = new Bundle();
                bundle.putString("homeUniqueId", detail);
                bundle.putString("home", "home");
                searchDetailViewFragment.setArguments(bundle);
                final FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, searchDetailViewFragment).commit();
            }
        } else {
            mHomeTab = new HomeTabFragment();
            final FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, mHomeTab)
                    .commit();
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String account = intent.getStringExtra("account");
        if (account != null) {

            String action = intent.getStringExtra("click_action");
            String bookingId = intent.getStringExtra("uuid");
            String bodyMessage = intent.getStringExtra("body");
            String titleString = intent.getStringExtra("title");

            if (action != null) {
                switch (action) {

                    case "CONSUMER_WAITLIST":

                        intent = new Intent(Home.this, CheckInDetails.class);
                        intent.putExtra("uuid", bookingId);
                        intent.putExtra("account", account);
                        intent.putExtra(Constants.PUSH_NOTIFICATION, true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;

                    case "CONSUMER_APPT":

                        intent = new Intent(Home.this, BookingDetails.class);
                        intent.putExtra("uuid", bookingId);
                        intent.putExtra("account", account);
                        intent.putExtra(Constants.PUSH_NOTIFICATION, true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;

                    case "CONSUMER_ORDER":
                    case "CONSUMER_ORDER_STATUS":

                        intent = new Intent(Home.this, OrderDetailActivity.class);
                        intent.putExtra("uuid", bookingId);
                        intent.putExtra("account", account);
                        intent.putExtra(Constants.PUSH_NOTIFICATION, true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;

                    case "CONSUMER_DONATION_SERVICE":
                    case "PAYMENTFAIL":

                        intent = new Intent(Home.this, Home.class);
                        intent.putExtra("uuid", bookingId);
                        intent.putExtra("account", account);
                        intent.putExtra("message", bodyMessage);
                        intent.putExtra(Constants.PUSH_NOTIFICATION, true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;

                    case "BILL":

                        intent = new Intent(Home.this, BillActivity.class);
                        intent.putExtra("ynwUUID", bookingId);
                        intent.putExtra("accountID", account);
                        intent.putExtra(Constants.PUSH_NOTIFICATION, true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;

                    case "BILL_PAYMENT_SUCCESS":
                    case "CONSUMER_SHARE_PRESCRIPTION":
                    case "CONSUMER_SHARE_MEDICAL_RECODE":
                    case "PRE_PAYMENT_SUCCESS":

                        if (bookingId != null) {

                            if (bookingId.contains("_wl")) {
                                intent = new Intent(Home.this, CheckInDetails.class);
                            } else if (bookingId.contains("_appt")) {
                                intent = new Intent(Home.this, BookingDetails.class);
                            } else {
                                intent = new Intent(Home.this, OrderDetailActivity.class);
                            }
                        } else {
                            intent = new Intent(Home.this, Home.class);
                        }
                        intent.putExtra("uuid", bookingId);
                        intent.putExtra("account", account);
                        intent.putExtra(Constants.PUSH_NOTIFICATION, true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;

                    case "MASSCOMMUNICATION":

                        intent = new Intent(Home.this, ChatActivity.class);
                        intent.putExtra("uuid", bookingId);
                        intent.putExtra(Constants.PUSH_NOTIFICATION, true);
                        intent.putExtra("accountId", Integer.parseInt(account));
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
                        startActivity(intent);
                        break;

                    default:

                        break;

                }
            }

        } else {

            message = intent.getStringExtra("message");
            if (message != null && !message.equalsIgnoreCase("") && message.trim().length() > 15) {

                notificationDialog = new NotificationDialog(mContext, message);
                notificationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                notificationDialog.show();
                notificationDialog.setCancelable(false);
                DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                int width = (int) (metrics.widthPixels * 1);
                notificationDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

            }

            from = intent.getStringExtra("isOrder");   // to set orders tab by default after completion of placing order.
            String loginId = SharedPreference.getInstance(mContext).getStringValue("mobno", "");
            Config.logV("Push Notification Foreground @@@@@@@@@@@@@@@@@@@@@" + loginId);
            if (!loginId.equalsIgnoreCase("")) {
                mHomeTab = new HomeTabFragment();
                Bundle bundle = new Bundle();
                bundle.putString("tab", "1");
                if (from != null && !from.equalsIgnoreCase("")) {
                    if (from.equalsIgnoreCase("ORDER")) {

                        bundle.putInt("myJaldeeTab", 2);  // to set orders tab by default after completion of placing order.
                    }
                }
                if (message != null && !message.equalsIgnoreCase("")) {
                    bundle.putString("message", message);
                } else {
                    bundle.putString("message", intent.getStringExtra("message"));
                }
                mHomeTab.setArguments(bundle);
                final FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, mHomeTab)
                        .commit();

            } else {
                Intent iLogin = new Intent(this, Register.class);
                startActivity(iLogin);
                finish();
            }
        }

    }

    public static boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

        if (mHomeTab != null) {
            if (!mHomeTab.onBackPressed()) {
                Config.logV("Home Back Presss-------------");
                if (doubleBackToExitPressedOnce) {
                    System.exit(0);
                }
                doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Press back button twice to exit from the application", Toast.LENGTH_SHORT).show();
            } else {
            }
        } else {
            if (doubleBackToExitPressedOnce) {
                System.exit(0);
            }
            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press back button twice to exit from the application", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        Log.i("OnDestroyHome", true + "");
        super.onDestroy();
        stopService(mLiveTrackClient);

    }

    @Override
    protected void onStart() {
        Log.i("onStartHomeBefore", true + "");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.i("onStopHome", true + "");
        super.onStop();
        Log.i("onStopHomeAfter", true + "");

    }
}


