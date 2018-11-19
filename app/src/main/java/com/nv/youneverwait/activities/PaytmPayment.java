package com.nv.youneverwait.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import com.google.gson.Gson;
import com.nv.youneverwait.R;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.model.TestModel;
import com.nv.youneverwait.response.PaytmChecksum;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sharmila on 16/11/18.
 */

public class PaytmPayment {

    Context context;
    public PaytmPayment(Context mContext){
        context=mContext;
    }
    public void generateCheckSum() {


        ApiInterface apiService =
                ApiClient.getTestClient(context).create(ApiInterface.class);


        // RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), params.toString());
        Call<ArrayList<PaytmChecksum>>  call = apiService.getPaytmCheckSum("10");



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
                        map.put("WEBSITE",response.body().get(0).getWEBSITE());
                        map.put("CALLBACK_URL", response.body().get(0).getCALLBACK_URL());
                        // map.put("EMAIL", jsonObj.getString("EMAIL"));
                        // map.put("MOBILE_NO", jsonObj.getString("MOBILE_NO"));
                        map.put("CHECKSUMHASH", response.body().get(0).getChecksum());
                        PaytmPay(map);



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

    public void PaytmPay(Map<String, String> paramMap) {
        PaytmPGService Service = null;
        Service = PaytmPGService.getStagingService();
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
                Toast.makeText(context, "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
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
