package com.netvarth.youneverwait.connection;

import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.netvarth.youneverwait.common.Config;
import com.netvarth.youneverwait.connection.rest.AddCookiesInterceptor;
import com.netvarth.youneverwait.connection.rest.ItemTypeAdapterFactory;
import com.netvarth.youneverwait.connection.rest.ReceivedCookiesInterceptor;
import com.netvarth.youneverwait.utils.SharedPreference;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sharmila on 2/7/18.
 */

public class ApiClient {

    public static final String BASE_URL = "http://54.215.5.201:8181/v1/rest/";


    private static Retrofit retrofit = null;
    private static Retrofit retrofitAWS = null;
    private static Retrofit retrofitCloud = null;
    public static Context context;

    public static Retrofit getClient(Context mContext) {
       /* Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .create();*/
        context = mContext;
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(OkttpClient(context))
                    .build();
        }
        return retrofit;
    }


    public static Retrofit getClientAWS(Context mContext) {


        context = mContext;
        if (retrofitAWS == null) {

            String AWS_URL = SharedPreference.getInstance(mContext).getStringValue("AWS_URL", "");
            String url = AWS_URL.replace("\"", "") + "/";

            retrofitAWS = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(OkttpClient(context))
                    .build();
        }
        return retrofitAWS;
    }


    public static Retrofit getClientS3Cloud(Context mContext) {


        context = mContext;
        if (retrofitCloud == null) {


            String url = "https://s3-us-west-1.amazonaws.com/ynwtest.youneverwait.com/";

            retrofitCloud = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(OkttpClient(context))
                    .build();
        }
        return retrofitCloud;
    }

    public static OkHttpClient OkttpClient(Context context) {

        OkHttpClient okHttpClient = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(new AddCookiesInterceptor(context)); // VERY VERY IMPORTANT
        //  builder.addInterceptor(new ReceivedCookiesInterceptor(context));
        builder.addInterceptor(new ConnectivityInterceptor(context));
        builder.readTimeout(90, TimeUnit.SECONDS);
        builder.connectTimeout(90, TimeUnit.SECONDS);
        okHttpClient = builder.build();
       /* OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new ConnectivityInterceptor(context))
                .readTimeout(90, TimeUnit.SECONDS)
                .connectTimeout(90, TimeUnit.SECONDS)
                .build();*/

        return okHttpClient;
    }
}
