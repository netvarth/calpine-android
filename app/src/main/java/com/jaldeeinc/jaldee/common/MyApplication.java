package com.jaldeeinc.jaldee.common;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.activities.Home;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.FontOverride;
import com.jaldeeinc.jaldee.response.LoginResponse;
import com.jaldeeinc.jaldee.utils.LogUtil;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.RequestBody;

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
            Config.ApiSessionResetLogin(loginId, password,this);

        }
    }

    public  void ApiSessionResetLogin(String loginId, String password) {

        CompositeDisposable cDisposable = new CompositeDisposable();

        ApiInterface apiService =
                ApiClient.getClient(this).create(ApiInterface.class);


        SharedPreferences pref = this.getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        Config.logV("REGISTARION ID______RENEW________@@@@@@@___" + regId);
        LogUtil.writeLogTest("REG ID @@@@@@@@@@@" + regId);
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("loginId", loginId);
            jsonObj.put("password", password);
            jsonObj.put("mUniqueId", regId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Config.logV("JSON--------------" + jsonObj);
        Disposable d =
                apiService.login(body)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::processSuccess,
                                this::processFailure);
        cDisposable.add(d);


    }

    private void processFailure(Throwable throwable) {
        Config.logV("Fail---------------" + throwable.toString());

    }

    private void processSuccess(LoginResponse response) {

        try {

//            Config.logV("URL---------------" + response.raw().request().url().toString().trim());
//            Config.logV("Response-- LOGIN RESERT code-------------------------" + response.code());
//
//            LogUtil.writeLogTest("REG ID RESP CODE@@@@@@@@@@@" + response.code());
//            if (response.code() == 200) {
//                Config.logV("Response--code-------------------------" + response.body().getFirstName());
//
//
//                // get header value
//
//
//                List<String> cookiess = response.headers().values("Set-Cookie");
//                StringBuffer Cookie_header = new StringBuffer();
//
//                for (String key : cookiess) {
//                    String Cookiee = key.substring(0, key.indexOf(";"));
//                    Cookie_header.append(Cookiee + ';');
//                }
//
//                Config.logV("Response--Cookie config-------------------------" + cookiess);
//                if (!cookiess.isEmpty()) {
//
//                    SharedPreference.getInstance(context).getStringValue("PREF_COOKIES", "");
//
//                    SharedPreference.getInstance(context).setValue("PREF_COOKIES", String.valueOf(Cookie_header));
//                    Config.logV("Set Cookie sharedpref_config------------" + Cookie_header);
//
//                    LogUtil.writeLogTest("****Login Cookie****" + Cookie_header);
//
//                }
//
//
//                Headers headerList = response..headers();
//                String version = headerList.get("Version");
//                Config.logV("Header----------" + version);
//
//                SharedPreference.getInstance(context).setValue("Version", version);

            // Config.logV("Email------------------"+response.body().get);
            SharedPreference.getInstance(this).setValue("consumerId", response.getId());
            SharedPreference.getInstance(this).setValue("register", "success");
            SharedPreference.getInstance(this).setValue("firstname", response.getFirstName());
            SharedPreference.getInstance(this).setValue("lastname", response.getLastName());

            SharedPreference.getInstance(this).setValue("s3Url", response.getS3Url());

            SharedPreference.getInstance(this).setValue("mobile", response.getPrimaryPhoneNumber());
            Intent iReg = new Intent(this, Home.class);
            iReg.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            this.startActivity(iReg);
            // ((Activity)context).finish();
            Config.logV("App@@@ ForeBackGround Sucess");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}