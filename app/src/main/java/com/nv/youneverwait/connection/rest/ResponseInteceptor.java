package com.nv.youneverwait.connection.rest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.nv.youneverwait.activities.Home;
import com.nv.youneverwait.activities.Register;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.database.DatabaseHandler;
import com.nv.youneverwait.response.LoginResponse;
import com.nv.youneverwait.utils.LogUtil;
import com.nv.youneverwait.utils.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by sharmila on 15/11/18.
 */

public class ResponseInteceptor implements Interceptor {

    private Context context;

    public ResponseInteceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {

        Request original = chain.request();

        Request request = original.newBuilder()
              //  .header("Authorization", token_type + " " + access_token)
                .method(original.method(), original.body())
                .url(original.url())
                .build();


        Response response =  chain.proceed(request);

        Headers headerList = response.headers();
        String versionheader = headerList.get("Version");
        Config.logV("Header-----Response-----" + versionheader);
       /* if(versionheader.equalsIgnoreCase("api-1.1.0,config-1.1.0")){
            String version="v2";
            Config.logV("Header-----Response--@@@@@---" + versionheader);

           SharedPreference.getInstance(context).setValue("BASE_URL","http://35.154.241.175/"+version+"/rest/");
        }
*/

        if (response.code() == 419){
            // Magic is here ( Handle the error as your way )
            Config.logV("RESPONSE @@@@@@@@@@@@@@@@@"+response.code());


            //SharedPreference.getInstance(context).clear();
            DatabaseHandler db=new DatabaseHandler(context);
            db.deleteDatabase();

            String loginId = SharedPreference.getInstance(context).getStringValue("mobno", "");
            String password = SharedPreference.getInstance(context).getStringValue("password", "");
            ApiLogin(loginId,password);



            return response;
        }
        return response;
    }
    public void ApiLogin(String loginId, String password) {

        ApiInterface apiService =
                ApiClient.getClient(context).create(ApiInterface.class);


        SharedPreferences pref = context.getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        Config.logV("REGISTARION ID______________@@@@@@@___"+regId);

        Config.logV("login ID______________@@@@@@@___"+loginId);
        Config.logV("password ID______________@@@@@@@___"+password);

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

        Call<LoginResponse> call = apiService.LoginResponse(body);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {

                try {



                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        Config.logV("Response--code-------------------------" + response.body().getFirstName());


                        // get header value
                        String cookie = response.headers().get("Set-Cookie");

                        Config.logV("Response--Cookie-------------------------" + cookie);
                        if (!cookie.isEmpty()) {

                            SharedPreference.getInstance(context).getStringValue("PREF_COOKIES", "");
                            String header = response.headers().get("Set-Cookie");
                            String Cookie_header = header.substring(0, header.indexOf(";"));

                            SharedPreference.getInstance(context).setValue("PREF_COOKIES", Cookie_header);
                            Config.logV("Set Cookie sharedpref------------" + Cookie_header);

                            LogUtil.writeLogTest("****Login Cookie****"+Cookie_header);

                        }


                        Headers headerList = response.headers();
                        String version = headerList.get("Version");
                        Config.logV("Header----------" + version);

                        SharedPreference.getInstance(context).setValue("Version", version);

                        // Config.logV("Email------------------"+response.body().get);
                        SharedPreference.getInstance(context).setValue("consumerId", response.body().getId());
                        SharedPreference.getInstance(context).setValue("register", "success");
                        SharedPreference.getInstance(context).setValue("firstname", response.body().getFirstName());
                        SharedPreference.getInstance(context).setValue("lastname", response.body().getLastName());

                        SharedPreference.getInstance(context).setValue("s3Url", response.body().getS3Url());

                        SharedPreference.getInstance(context).setValue("mobile", response.body().getPrimaryPhoneNumber());
                        Intent iReg = new Intent(context, Home.class);
                        iReg.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(iReg);
                        ((Activity)context).finish();


                    }else{
                        /*if(response.code()!=419)
                        Toast.makeText(context,response.errorBody().string(),Toast.LENGTH_LONG).show();*/
                        Config.logV("Response Error-----------"+response.errorBody().string());
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());


            }
        });

    }
}
