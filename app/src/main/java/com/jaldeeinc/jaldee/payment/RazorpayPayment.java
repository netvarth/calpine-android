package com.jaldeeinc.jaldee.payment;

import android.app.Activity;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.model.RazorpayModel;
import com.jaldeeinc.jaldee.response.CheckSumModel;
import com.razorpay.Checkout;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mani on 01/07/2020.
 */

public class RazorpayPayment{

    static Context mContext;
    static Activity mActivity;
    String paymentResponse;

    RazorpayPayment (Context mContext, Activity mActivity) {
        this.mContext = mContext;
        this.mActivity = mActivity;
    }
    public void startPayment(CheckSumModel order) {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
//        final Activity activity = this;
        final Checkout co = new Checkout();
        try {
            JSONObject options = new JSONObject();
            options.put("name", order.getProviderName());
            options.put("description", order.getDescription());
            //You can omit the image option to fetch the image from dashboard
//            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", order.getAmount());
            options.put("retry",order.isRetry());
            options.put("order_id", order.getOrderId());
            options.put("retry",order.isRetry());
            if (order.isRetry()){

            }else {
                options.put("timeout",540);
            }
//            options.put("key", order.getRazorpayId());
            JSONObject preFill = new JSONObject();
            preFill.put("email", order.getConsumerEmail());
            preFill.put("contact", order.getConsumerPhoneumber());
            preFill.put("name", order.getConsumerName());
            options.put("prefill", preFill);
            Log.i("RazorPay", new Gson().toJson(options));
            co.setKeyID(order.getRazorpayId());
            co.open(this.mActivity, options);
        } catch (Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
            Toast.makeText(this.mContext, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    public void sendPaymentStatus(RazorpayModel razorpayModel) {
        final ApiInterface apiService =
                ApiClient.getClient( this.mContext).create(ApiInterface.class);
        Log.i("razorpayModel", new Gson().toJson(razorpayModel));
        Map<String, String> razorMap = new HashMap<String, String>();
        razorMap.put("razorpay_payment_id", razorpayModel.getRazorpay_payment_id());
        razorMap.put("razorpay_order_id", razorpayModel.getRazorpay_order_id());
        razorMap.put("razorpay_signature", razorpayModel.getRazorpay_signature());
        razorMap.put("status", razorpayModel.getStatus());
        razorMap.put("txnid", razorpayModel.getTxnid());
//        Toast.makeText(mContext, "Payment Successful", Toast.LENGTH_SHORT).show();
//        Call<String> call = apiService.verifyRazorpayPayment(razorMap);
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//
//                try {
//
//                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
//                    Config.logV("Response--code-------------------------" + response.code());
//
//                    if (response.code() == 200) {
//                        Log.i("PaymentResponse",new Gson().toJson(response.body()));
//                    }
//                } catch (Exception e) {
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                Log.e("TAG", "Error in payment status updation", t);
//                Toast.makeText(mContext, "Error in payment: " + t.getMessage(), Toast.LENGTH_LONG);
//            }
//        });
//        Toast.makeText(mContext, "Payment Successful", Toast.LENGTH_SHORT).show();
       // Call<String> call = apiService.verifyRazorpayPayment(razorMap);
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//
//                try {
//
//                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
//                    Config.logV("Response--code-------------------------" + response.code());
//
//                    if (response.code() == 200) {
//                        Log.i("PaymentResponse",new Gson().toJson(response.body()));
//                    }
//                } catch (Exception e) {
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                Log.e("TAG", "Error in payment status updation", t);
//                Toast.makeText(mContext, "Error in payment: " + t.getMessage(), Toast.LENGTH_LONG);
//            }
//        });
    }
}