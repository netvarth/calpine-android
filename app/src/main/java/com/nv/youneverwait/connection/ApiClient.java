package com.nv.youneverwait.connection;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nv.youneverwait.connection.rest.AddCookiesInterceptor;
import com.nv.youneverwait.connection.rest.ResponseInteceptor;
import com.nv.youneverwait.utils.SharedPreference;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sharmila on 2/7/18.
 */

public class ApiClient {

    public static final String BASE_URL ="https://test.jaldee.com/v1/rest/";
            //"https://alpha.jaldee.com/v1/rest/";
           // "http://alpha.jaldee.com/v1/rest/";
            //
            //"http://54.215.5.201:8181/v1/rest/";
            //"http://35.154.241.175/v1/rest/";



    private static Retrofit retrofit = null;
    private static Retrofit retrofitAWS = null;
    private static Retrofit retrofitCloud = null;

    private static Retrofit retrofitTest = null;
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


    public static Retrofit getTestClient(Context mContext) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        context = mContext;
        if (retrofitTest == null) {
            retrofitTest = new Retrofit.Builder()
                    .baseUrl("http://stage.bookmyconsult.com/test/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(OkttpClientTest(context))
                    .build();
        }
        return retrofitTest;
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


            //String url = "https://s3-us-west-1.amazonaws.com/ynwtest.youneverwait.com/";
            String url=SharedPreference.getInstance(mContext).getStringValue("s3Url", "")+"/";
            retrofitCloud = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(OkttpClient(context))
                    .build();
        }
        return retrofitCloud;
    }

    public static OkHttpClient OkttpClient(Context context) {


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        OkHttpClient okHttpClient = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(new AddCookiesInterceptor(context)); // VERY VERY IMPORTANT
        //  builder.addInterceptor(new ReceivedCookiesInterceptor(context));
        builder.addInterceptor(new ConnectivityInterceptor(context));

        builder.addInterceptor(new ResponseInteceptor(context));


        builder.addInterceptor(logging);


        builder.readTimeout(60, TimeUnit.SECONDS);
        builder.connectTimeout(60, TimeUnit.SECONDS);
        okHttpClient = builder.build();
       /* OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new ConnectivityInterceptor(context))
                .readTimeout(90, TimeUnit.SECONDS)
                .connectTimeout(90, TimeUnit.SECONDS)
                .build();*/

        return okHttpClient;
    }


    public static OkHttpClient OkttpClientTest(Context context) {

        OkHttpClient okHttpClient = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

       // builder.addInterceptor(new AddCookiesInterceptor(context)); // VERY VERY IMPORTANT
        //  builder.addInterceptor(new ReceivedCookiesInterceptor(context));
        builder.addInterceptor(new ConnectivityInterceptor(context));
        builder.readTimeout(60, TimeUnit.SECONDS);
        builder.connectTimeout(60, TimeUnit.SECONDS);
        okHttpClient = builder.build();
       /* OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new ConnectivityInterceptor(context))
                .readTimeout(90, TimeUnit.SECONDS)
                .connectTimeout(90, TimeUnit.SECONDS)
                .build();*/

        return okHttpClient;
    }
}
