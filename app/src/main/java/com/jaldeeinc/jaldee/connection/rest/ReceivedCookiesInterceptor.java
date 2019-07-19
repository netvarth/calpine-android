package com.jaldeeinc.jaldee.connection.rest;

/**
 * Created by sharmila on 10/7/18.
 */

import android.content.Context;

import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class ReceivedCookiesInterceptor implements Interceptor {
    private Context context;
    public ReceivedCookiesInterceptor(Context context) {
        this.context = context;
    } // AddCookiesInterceptor()
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        Config.logV("Response--1111------------"+originalResponse);

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            /*HashSet<String> cookies = (HashSet<String>) PreferenceManager.getDefaultSharedPreferences(context).getStringSet("PREF_COOKIES", new HashSet<String>());

            for (String header : originalResponse.headers("Set-Cookie")) {
                Config.logV("Set Cookie---1111---------"+header);
                String Cookie_header = header.substring(0, header.indexOf(";"));
                cookies.add(Cookie_header);
                Config.logV("Set Cookie------------"+Cookie_header);

            }

            SharedPreferences.Editor memes = PreferenceManager.getDefaultSharedPreferences(context).edit();
            memes.putStringSet("PREF_COOKIES", cookies).apply();
            memes.commit();
            Config.logV("Set Cookie sharedpref------------"+cookies);*/

            SharedPreference.getInstance(context).getStringValue("PREF_COOKIES","");
            String header = originalResponse.headers().get("Set-Cookie");
            String Cookie_header = header.substring(0, header.indexOf(";"));

           SharedPreference.getInstance(context).setValue("PREF_COOKIES",Cookie_header);
            Config.logV("Set Cookie sharedpref------------"+Cookie_header);

        }

        return originalResponse;
    }
}