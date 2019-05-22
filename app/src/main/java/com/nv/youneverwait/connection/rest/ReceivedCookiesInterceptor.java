package com.nv.youneverwait.connection.rest;

/**
 * Created by sharmila on 10/7/18.
 */

import android.content.Context;
import android.util.Log;

import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.utils.SharedPreference;

import java.io.IOException;
import java.util.List;

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
//            String header = originalResponse.headers().get("Set-Cookie");


            List<String> cookiess = originalResponse.headers().values("Set-Cookie");
            StringBuffer Cookie_header = new StringBuffer();

            for(String key : cookiess){
                String Cookiee = key.substring(0, key.indexOf(";"));
                Cookie_header.append(Cookiee +';');
            }



           SharedPreference.getInstance(context).setValue("PREF_COOKIES",Cookie_header.toString());
            Config.logV("Set Cookie sharedpref received------------"+Cookie_header);

        }

        return originalResponse;
    }
}