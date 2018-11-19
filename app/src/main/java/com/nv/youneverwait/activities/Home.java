package com.nv.youneverwait.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.nv.youneverwait.Fragment.HomeTabFragment;
import com.nv.youneverwait.R;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.utils.NotificationUtils;


/**
 * Created by sharmila on 3/7/18.
 */
public class Home extends AppCompatActivity {


    HomeTabFragment mHomeTab;
    Toolbar toolbar;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        Config.logV("Home Screen@@@@@@@@@@@@@@@@@@@");


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
        Config.logV("REGISTARION ID___3333####___________@@@@@@@___"+regId);


    }

    private void initScreen() {
        // Creating the ViewPager container fragment once
        mHomeTab = new HomeTabFragment();

        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, mHomeTab)
                .commit();
    }

    public static boolean  doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {

        if (!mHomeTab.onBackPressed()) {
            // container Fragment or its associates couldn't handle the back pressed task
            // delegating the task to super class
            Config.logV("Home Back Presss-------------");
          //  super.onBackPressed();
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press back button twice to exist from the application", Toast.LENGTH_SHORT).show();



        } else {
            // carousel handled the back pressed task
            // do not call super
        }

    }



}
