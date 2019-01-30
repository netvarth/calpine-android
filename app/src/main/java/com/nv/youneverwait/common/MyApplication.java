package com.nv.youneverwait.common;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.nv.youneverwait.activities.Home;
import com.nv.youneverwait.custom.FontOverride;
import com.nv.youneverwait.database.DatabaseHandler;
import com.nv.youneverwait.utils.NotificationUtils;
import com.nv.youneverwait.utils.SharedPreference;

/**
 * Created by sharmila on 2/7/18.
 */

public class MyApplication extends Application implements AppLifeCycleHandler.AppLifeCycleCallback{

    private static MyApplication s_instance;
    @Override
    public void onCreate() {

        super.onCreate();
        FontOverride.setDefaultFont(this, "SERIF", "fonts/Montserrat_Regular.otf");

        Config.logV("APP APplication---------------------------");

        AppLifeCycleHandler appLifeCycleHandler = new AppLifeCycleHandler(this);
        registerActivityLifecycleCallbacks(appLifeCycleHandler);
        registerComponentCallbacks(appLifeCycleHandler);

    }



    public static Context getContext() {
        return s_instance;
    }

    @Override
    public void onAppBackground() {

        Config.logV("App@@@@ BackGround");
    }

    @Override
    public void onAppForeground() {

        Config.logV("App@@@ ForeBackGround");

        String loginId = SharedPreference.getInstance(this).getStringValue("mobno", "");
        String password = SharedPreference.getInstance(this).getStringValue("password", "");
        if(!loginId.equalsIgnoreCase("")&&!password.equalsIgnoreCase("")) {
            Config.logV("App@@@ ForeBackGround Reset");
            Config.ApiSessionResetLogin(loginId, password, this);
        }
    }
}