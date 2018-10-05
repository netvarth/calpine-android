package com.netvarth.youneverwait.connection.rest;

/**
 * Created by sharmila on 10/7/18.
 */

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.netvarth.youneverwait.common.Config;
import com.netvarth.youneverwait.utils.SharedPreference;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This interceptor put all the Cookies in Preferences in the Request.
 * Your implementation on how to get the Preferences may ary, but this will work 99% of the time.
 */
public class AddCookiesInterceptor implements Interceptor {
    public static final String PREF_COOKIES = "PREF_COOKIES";
    // We're storing our stuff in a database made just for cookies called PREF_COOKIES.
    // I reccomend you do this, and don't change this default value.
    private Context context;

    public AddCookiesInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

       /* HashSet<String> preferences = (HashSet<String>) PreferenceManager.getDefaultSharedPreferences(context).getStringSet(PREF_COOKIES, new HashSet<String>());

        for (String cookie : preferences) {
            builder.addHeader("Cookie", cookie);
            Config.logV("Request Header append Cookie sharedpref------------"+cookie);
        }*/
        String cookie=SharedPreference.getInstance(context).getStringValue("PREF_COOKIES","");
        if(!cookie.equalsIgnoreCase("")) {
            Config.logV("Add Header-----------------"+cookie);
            builder.addHeader("Cookie", cookie);
        }

        String version= SharedPreference.getInstance(context).getStringValue("Version","");
        if(!version.equalsIgnoreCase("")) {
            Config.logV("Add Header--Version---------------"+version);
            builder.addHeader("Version", version);
        }

        return chain.proceed(builder.build());
    }
}