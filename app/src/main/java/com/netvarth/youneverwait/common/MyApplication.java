package com.netvarth.youneverwait.common;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.netvarth.youneverwait.custom.FontOverride;

/**
 * Created by sharmila on 2/7/18.
 */

public class MyApplication extends Application{

    private static MyApplication s_instance;
    @Override
    public void onCreate() {

        super.onCreate();
        FontOverride.setDefaultFont(this, "SERIF", "fonts/Montserrat_Regular.ttf");

        Config.logV("MyAPplication---------------------------");

    }



    public static Context getContext() {
        return s_instance;
    }

}