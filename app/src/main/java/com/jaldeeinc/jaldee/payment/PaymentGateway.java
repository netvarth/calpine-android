package com.jaldeeinc.jaldee.payment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.model.RazorpayModel;
import com.jaldeeinc.jaldee.response.CheckSumModel;
import com.razorpay.Checkout;

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
    static Activity mActivity;

    public PaymentGateway(Context mContext, Activity activity) {
        mCOntext = mContext;
        mActivity = activity;
    }
    public void ApiGenerateHash1(String ynwUUID, final String amount, String accountID, String purpose, final String from,int customerId, String source) {


        ApiInterface apiService =
                ApiClient.getClient(mCOntext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mCOntext, mCOntext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        //  String uniqueID = UUID.randomUUID().toString();
        JSONObject jsonObj = new JSONObject();
        try {

            jsonObj.put("amount", amount);
            jsonObj.put("paymentMode", "DC");
            jsonObj.put("uuid", ynwUUID);
            jsonObj.put("accountId", accountID);
            jsonObj.put("purpose", purpose);
            jsonObj.put("custId",customerId);
            jsonObj.put("source",source);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<CheckSumModel> call = apiService.generateHash(body);
        call.enqueue(new Callback<CheckSumModel>() {
            @Override
            public void onResponse(Call<CheckSumModel> call, Response<CheckSumModel> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(mActivity, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());


                    if (response.code() == 200) {

                        CheckSumModel response_data = response.body();
                        response_data.setAmount(Config.getAmountinTwoDecimalPoints(Double.parseDouble(response_data.getAmount())));

                        if (from.equalsIgnoreCase("checkin")) {


//                            Log.i("Response--Sucess----" , new Gson().toJson(response.body()));

                            Config.logV("Response--Sucess----------@@@@---------------" + response.body().getPaymentEnv());

                            //CheckIn.launchPaymentFlow(amount, response_data);

                            if (response_data.getPaymentGateway()!=null && response_data.getPaymentGateway().equals("RAZORPAY")) {
                                RazorpayPayment razorPayment = new RazorpayPayment(mCOntext, mActivity);
                                razorPayment.startPayment(response_data);
                            } else {
                                Intent iPayu=new Intent(mCOntext, PayUMoneyWebview.class);
                                iPayu.putExtra("responsedata",response_data);
                                iPayu.putExtra("amount",amount);
                                mCOntext.startActivity(iPayu);
                            }


                        } else if (from.equalsIgnoreCase("bill")) {
                            // CheckSumModel response_data = response.body();
//                            Config.logV("Response--Sucess-------------------------" + new Gson().toJson(response.body()));

                            //  BillActivity.launchPaymentFlow(amount, response_data);
                            if (response_data.getPaymentGateway()!=null && response_data.getPaymentGateway().equals("RAZORPAY")) {
                                RazorpayPayment razorPayment = new RazorpayPayment(mCOntext, mActivity);
                                razorPayment.startPayment(response_data);
                            } else {
                                Intent iPayu=new Intent(mCOntext, PayUMoneyWebview.class);
                                iPayu.putExtra("responsedata",response_data);
                                iPayu.putExtra("amount",amount);
                                mCOntext.startActivity(iPayu);
                            }
                        } else {
                            // CheckSumModel response_data = response.body();

                            // PaymentActivity.launchPaymentFlow(amount, response_data);
                            if (response_data.getPaymentGateway()!=null && response_data.getPaymentGateway().equals("RAZORPAY")) {
                                RazorpayPayment razorPayment = new RazorpayPayment(mCOntext, mActivity);
                                razorPayment.startPayment(response_data);
                            } else {
                                Intent iPayu=new Intent(mCOntext, PayUMoneyWebview.class);
                                iPayu.putExtra("responsedata",response_data);
                                iPayu.putExtra("amount",Config.getAmountinTwoDecimalPoints(Double.parseDouble(amount)));
                                mCOntext.startActivity(iPayu);
                            }
                        }


                    } else {
                        String responseerror = response.errorBody().string();
                        Config.logV("Response--error-------------------------" + responseerror);
                        Toast.makeText(mCOntext,responseerror,Toast.LENGTH_LONG).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<CheckSumModel> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(mActivity, mDialog);

            }
        });



    }

    public void sendPaymentStatus(RazorpayModel razorpayModel, String status) {
        RazorpayPayment razorPayment = new RazorpayPayment(mCOntext, mActivity);
        razorpayModel.setStatus(status);
        razorpayModel.setTxnid("");
        razorPayment.sendPaymentStatus(razorpayModel);
    }
    /*public void ApiGenerateHashTest(String ynwUUID, final String amount, String accountID, final String from) {


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
            jsonObj.put("accountId", accountID);

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


                        if (from.equalsIgnoreCase("checkin")) {

                            CheckSumModelTest response_data = response.body();
                            Config.logV("Response--Sucess-------------------------" + new Gson().toJson(response.body()));

                          //  CheckIn.launchPaymentFlow(amount, response_data);

                        } else if (from.equalsIgnoreCase("bill")) {
                            CheckSumModelTest response_data = response.body();
                            Config.logV("Response--Sucess-------------------------" + new Gson().toJson(response.body()));

                            BillActivity.launchPaymentFlow(amount, response_data);
                        }else{
                            CheckSumModelTest response_data = response.body();

                           // PaymentActivity.launchPaymentFlow(amount, response_data);
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


*/
}
