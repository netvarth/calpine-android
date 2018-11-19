package com.nv.youneverwait.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nv.youneverwait.R;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.model.CheckSumModelTest;
import com.nv.youneverwait.response.CheckSumModel;
import com.nv.youneverwait.utils.SharedPreference;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 19/11/18.
 */

public class PaymentGateway {

    Context mCOntext;
    Activity mActivity;
    public PaymentGateway(Context mContext, Activity activity){
        mCOntext=mContext;
        mActivity=activity;
    }

    public void ApiGenerateHashTest(String ynwUUID, final String amount, String accountID, final String from) {


        ApiInterface apiService =
                ApiClient.getTestClient(mCOntext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mCOntext, mCOntext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        //  String uniqueID = UUID.randomUUID().toString();
        JSONObject jsonObj = new JSONObject();
        try {

            jsonObj.put("amount", amount);
            jsonObj.put("paymentMode", "DC");
            jsonObj.put("uuid", ynwUUID);

        } catch (JSONException e) {
            e.printStackTrace();
        }


      //  RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<CheckSumModelTest> call = apiService.getPayUCheckSum(amount);

        call.enqueue(new Callback<CheckSumModelTest>() {
            @Override
            public void onResponse(Call<CheckSumModelTest> call, Response<CheckSumModelTest> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(mActivity, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());


                    if (response.code() == 200) {


                        if(from.equalsIgnoreCase("checkin")){

                            CheckSumModelTest response_data = response.body();
                            Config.logV("Response--Sucess-------------------------" + new Gson().toJson(response.body()));

                             CheckIn.launchPaymentFlow(amount, response_data);

                        }else {
                            CheckSumModelTest response_data = response.body();
                            Config.logV("Response--Sucess-------------------------" + new Gson().toJson(response.body()));

                            BillActivity.launchPaymentFlow(amount, response_data);
                        }


                    } else {
                        String responseerror = response.errorBody().string();
                        Config.logV("Response--error-------------------------" + responseerror);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<CheckSumModelTest> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(mActivity, mDialog);

            }
        });


    }



}
