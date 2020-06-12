package com.jaldeeinc.jaldee.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.jaldeeinc.jaldee.Fragment.CheckinsFragmentCopy;
import com.jaldeeinc.jaldee.Fragment.HomeTabFragment;
import com.jaldeeinc.jaldee.Fragment.SearchDetailViewFragment;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
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


    Intent mLiveTrackClient;
    private LiveTrackService liveTrackService;

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
             detail = extras.getString("detail_id", "");
             path = extras.getString("path", "");
            Log.i("detailsofDetail",detail);
            Log.i("detailsofDetail",path);
        }

        Config.logV("Home Screen@@@@@@@@@@@@@@@@@@@");
        mContext = this;
        liveTrackService = new LiveTrackService();
        mLiveTrackClient = new Intent(Home.this, liveTrackService.getClass());
        if (!isMyServiceRunning(liveTrackService.getClass())) {
            Log.i("OnCreateHome?", true + "");
            startService(mLiveTrackClient);
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
            mHomeTab = (HomeTabFragment) getSupportFragmentManager().getFragments().get(0);
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


        if(detail!=null){
            if(path.contains("status")){
//                Log.i("ghjghjgj","pathhh");
//                checkinsFragmentCopy = new CheckinsFragmentCopy();
//                Bundle bundle = new Bundle();
//                bundle.putString("homeUniqueId", detail);
//                checkinsFragmentCopy.setArguments(bundle);
//                final FragmentManager fragmentManager = getSupportFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.container, checkinsFragmentCopy).commit();
                mHomeTab = new HomeTabFragment();
                final FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, mHomeTab)
                        .commit();
            }else{
                Log.i("ghjghjgj","detaillll");
                searchDetailViewFragment = new SearchDetailViewFragment();
                Bundle bundle = new Bundle();
                bundle.putString("homeUniqueId", detail);
                bundle.putString("home", "home");
                searchDetailViewFragment.setArguments(bundle);
                final FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, searchDetailViewFragment).commit();
            }
        }else{
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

        String loginId = SharedPreference.getInstance(mContext).getStringValue("mobno", "");
        Config.logV("Push Notification Foreground @@@@@@@@@@@@@@@@@@@@@" + loginId);
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

    public static boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if(mHomeTab!=null){
            if (!mHomeTab.onBackPressed()) {
                Config.logV("Home Back Presss-------------");
                if (doubleBackToExitPressedOnce) {
//                    super.onBackPressed();
                    System.exit(0);
                }
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Press back button twice to exit from the application", Toast.LENGTH_SHORT).show();
            } else {
            }
        }else{
            if (doubleBackToExitPressedOnce) {
//                    super.onBackPressed();
                System.exit(0);
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press back button twice to exit from the application", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onDestroy() {
        Log.i("OnDestroyHome", true + "");
        stopService(mLiveTrackClient);

        super.onDestroy();
    }

    @Override
    protected void onStart() {
        Log.i("onStartHomeBefore", true + "");
        if (!isMyServiceRunning(liveTrackService.getClass())) {
            Log.i("OnStartHomeAter", true + "");
            startService(mLiveTrackClient);
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.i("onStopHome", true + "");
        super.onStop();
        if (isMyServiceRunning(liveTrackService.getClass())) {
            Log.i("OnStartHomeAter", true + "");
            stopService(mLiveTrackClient);
        } else {
            startService(mLiveTrackClient);
        }

    }
}


