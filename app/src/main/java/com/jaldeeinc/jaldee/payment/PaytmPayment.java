package com.jaldeeinc.jaldee.payment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.response.PaytmChecksum;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 16/11/18.
 */

public class PaytmPayment {

    Context context;
    public PaytmPayment(Context mContext) {
        context = mContext;

    }

    public void generateCheckSum(String txtamt) {


        ApiInterface apiService =
                ApiClient.getTestClient(context).create(ApiInterface.class);


        // RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), params.toString());
        Call<ArrayList<PaytmChecksum>> call = apiService.getPaytmCheckSum(txtamt);


        //creating a call object from the apiService
        // Call<ArrayList<PaytmChecksum>> call = apiService.getChecksum1(paytm.getTxnAmount());

        //making the call to generate checksum
        call.enqueue(new Callback<ArrayList<PaytmChecksum>>() {
            @Override
            public void onResponse(Call<ArrayList<PaytmChecksum>> call, Response<ArrayList<PaytmChecksum>> response) {

                //once we get the checksum we will initiailize the payment.
                //the method is taking the checksum we got and the paytm object as the parameter


                System.out.println("responseCode@@@@@@@@@@@" + response.body().size());
                System.out.println("responseCode" + new Gson().toJson(response.body()));

                System.out.println("responseCode" + response.body().get(0).getINDUSTRY_TYPE_ID());
                System.out.println("responseCode" + response.body().get(0).getChecksum());

                if (response.body().get(0).getIs_success().equalsIgnoreCase("1")) {

                    try {

                        Map<String, String> map = new HashMap<>();


                        map.put("MID", response.body().get(0).getMID());
                        map.put("ORDER_ID", response.body().get(0).getORDER_ID());
                        map.put("CUST_ID", response.body().get(0).getCUST_ID());
                        map.put("INDUSTRY_TYPE_ID", response.body().get(0).getINDUSTRY_TYPE_ID());
                        map.put("CHANNEL_ID", response.body().get(0).getCHANNEL_ID());
                        map.put("TXN_AMOUNT", response.body().get(0).getTXN_AMOUNT());
                        map.put("WEBSITE", response.body().get(0).getWEBSITE());
                        map.put("CALLBACK_URL", response.body().get(0).getCALLBACK_URL());
                        // map.put("EMAIL", jsonObj.getString("EMAIL"));
                        // map.put("MOBILE_NO", jsonObj.getString("MOBILE_NO"));
                        map.put("CHECKSUMHASH", response.body().get(0).getChecksum());


                        //PaytmPay(map);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ArrayList<PaytmChecksum>> call, Throwable t) {

                System.out.println("responseCode Fail" + t.toString());
            }
        });
    }

    private static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            copy.writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
    public void ApiGenerateHashPaytm(String ynwUUID, String amount, String accountID, final Context mContext, final Activity mActivity, final String from) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        //  String uniqueID = UUID.randomUUID().toString();
        JSONObject jsonObj = new JSONObject();
        try {

            jsonObj.put("amount", amount);
            jsonObj.put("paymentMode", "PPI");
            jsonObj.put("uuid", ynwUUID);
            jsonObj.put("accountId", accountID);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<PaytmChecksum> call = apiService.generateHashPaytm(body);

        Config.logV("Request--body------Payment---Amount----------------" + amount);
        Config.logV("Request--body------Payment-------------------" + bodyToString(body));
        call.enqueue(new Callback<PaytmChecksum>() {
            @Override
            public void onResponse(Call<PaytmChecksum> call, Response<PaytmChecksum> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(mActivity, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());


                    if (response.code() == 200) {


                        PaytmChecksum response_data = response.body();
                        Config.logV("Response--Sucess----PAytm---------------------" + new Gson().toJson(response.body()));

                        try {

                            Map<String, String> map = new HashMap<>();


                            map.put("MID", response_data.getMID());
                            map.put("ORDER_ID", response_data.getORDER_ID());
                            map.put("CUST_ID", response_data.getCUST_ID());
                            map.put("INDUSTRY_TYPE_ID", response_data.getINDUSTRY_TYPE_ID());
                            map.put("CHANNEL_ID", response_data.getCHANNEL_ID());
                            map.put("TXN_AMOUNT", response_data.getTXN_AMOUNT());
                            map.put("WEBSITE", response_data.getWEBSITE());
                            Config.logV("Response--Sucess----PAytm-CALLBACK_URL--------------------" + response_data.getCALLBACK_URL());
                            map.put("CALLBACK_URL", response_data.getCALLBACK_URL());
                            map.put("CHECKSUMHASH", response_data.getChecksum());
                            PaytmPay(map, from, response_data.getPaymentEnv());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        String responseerror = response.errorBody().string();
                        Config.logV("Response--error-------------------------" + responseerror);
                        if (response.code() == 422) {
                            Toast.makeText(mContext, responseerror, Toast.LENGTH_SHORT).show();
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<PaytmChecksum> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(mActivity, mDialog);

            }
        });


    }

    public void PaytmPay(Map<String, String> paramMap, final String from, String paymentEnv) {
        PaytmPGService Service = null;
        // Service = PaytmPGService.getStagingService();
        if (paymentEnv.equalsIgnoreCase("production")) {
            Service = PaytmPGService.getProductionService();
        } else {
            Service = PaytmPGService.getStagingService();
        }
        PaytmOrder Order = new PaytmOrder((HashMap<String, String>) paramMap);
        Service.initialize(Order, null);
        Service.startPaymentTransaction(context, true, true, new PaytmPaymentTransactionCallback() {

            @Override
            public void someUIErrorOccurred(String inErrorMessage) {
                Log.d("LOG", "UI Error Occur.");
                Toast.makeText(context, " UI Error Occur. ", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTransactionResponse(Bundle inResponse) {
                Log.d("LOG", "Payment Transaction : " + inResponse);
                if(inResponse.toString().contains("TXN_SUCCESS")){
                    Toast.makeText(context, "Payment Successful", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(context, "Payment Failed ", Toast.LENGTH_LONG).show();
                }



                //Toast.makeText(context, "Payment Success", Toast.LENGTH_LONG).show();
                ((Activity) context).finish();
                /*if(!from.equalsIgnoreCase("home")) {
                    ((Activity) context).finish();
                }*/
            }

            @Override
            public void networkNotAvailable() {
                Log.d("LOG", "UI Error Occur.");
                Toast.makeText(context, " UI Error Occur. ", Toast.LENGTH_LONG).show();
            }

            @Override
            public void clientAuthenticationFailed(String inErrorMessage) {
                Log.d("LOG", "UI Error Occur.");
                Toast.makeText(context, " Severside Error " + inErrorMessage, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onErrorLoadingWebPage(int iniErrorCode,
                                              String inErrorMessage, String inFailingUrl) {
                Log.d("LOG", inErrorMessage);
            }

            @Override
            public void onBackPressedCancelTransaction() {
                Log.d("LOG", "Back");
// TODO Auto-generated method stub
            }

            @Override
            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
                Toast.makeText(context, "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
            }

        });

    }
}
