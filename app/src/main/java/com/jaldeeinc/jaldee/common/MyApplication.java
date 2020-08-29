package com.jaldeeinc.jaldee.common;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.custom.FontOverride;
import com.jaldeeinc.jaldee.utils.SharedPreference;

/**
 * Created by sharmila on 2/7/18.
 */

public class MyApplication extends Application implements AppLifeCycleHandler.AppLifeCycleCallback{

    private static MyApplication s_instance;
    public SharedPreferences sharedPreferences;
    @Override
    public void onCreate() {

        super.onCreate();
        FontOverride.setDefaultFont(this, "SERIF", "fonts/Montserrat_Regular.otf");
        sharedPreferences = getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);

        Config.logV("APP APplication---------------------------");

        AppLifeCycleHandler appLifeCycleHandler = new AppLifeCycleHandler(this);
        registerActivityLifecycleCallbacks(appLifeCycleHandler);
        registerComponentCallbacks(appLifeCycleHandler);

    }



    public static MyApplication getContext() {
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