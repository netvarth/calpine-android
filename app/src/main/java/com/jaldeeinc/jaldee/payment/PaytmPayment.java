package com.jaldeeinc.jaldee.payment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jaldeeinc.jaldee.Interface.IPaymentResponse;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.response.PaytmChecksum;
import com.jaldeeinc.jaldee.response.WalletPaytmChecksum;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;

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

public class PaytmPayment extends AppCompatActivity {

    private static final String TAG = "PaytmPayment";
    Context context;
    private IPaymentResponse iPaymentResponse;
    Integer ActivityRequestCode = 01;

    public PaytmPayment(Context mContext, IPaymentResponse paymentResponse) {
        context = mContext;
        iPaymentResponse = paymentResponse;
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

    public void ApiGenerateHashPaytm(String ynwUUID, String amount, String accountID, String purpose, final Context mContext, final Activity mActivity, final String from, int customerId, String encId) {


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
            jsonObj.put("purpose", purpose);
            jsonObj.put("custId", customerId);


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
//                        Config.logV("Response--Sucess----PAytm---------------------" + new Gson().toJson(response.body()));

                        try {

                            Map<String, String> map = new HashMap<>();


                            map.put("MID", response_data.getMID());
                            map.put("ORDER_ID", response_data.getORDER_ID());
                            map.put("CUST_ID", response_data.getCUST_ID());
                            map.put("INDUSTRY_TYPE_ID", response_data.getINDUSTRY_TYPE_ID());
                            map.put("CHANNEL_ID", response_data.getCHANNEL_ID());
                            map.put("TXN_AMOUNT", Config.getAmountinTwoDecimalPoints(Double.parseDouble(response_data.getTXN_AMOUNT())));
                            map.put("WEBSITE", response_data.getWEBSITE());
                            Config.logV("Response--Sucess----PAytm-CALLBACK_URL--------------------" + response_data.getCALLBACK_URL());
                            map.put("CALLBACK_URL", response_data.getCALLBACK_URL());
                            map.put("CHECKSUMHASH", response_data.getChecksum());
                            map.put("MERC_UNQ_REF", accountID + "_" + encId);
                            map.put("txnToken", response_data.getTxnToken());
                            PaytmPay(map, response_data.getPaymentEnv(), purpose);

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

    public void ApiGenerateHashPaytm2(String ynwUUID, final String amount, String accountID, String purpose, final String from, boolean isJcashUsed, boolean isreditUsed, boolean isRazorPayPayment, boolean isPayTmPayment, String encId, final Context mContext, final Activity mActivity) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        //  String uniqueID = UUID.randomUUID().toString();
        JSONObject jsonObj = new JSONObject();
        try {

            jsonObj.put("accountId", accountID);
            jsonObj.put("amountToPay", Float.valueOf(amount));
            jsonObj.put("isJcashUsed", isJcashUsed);
            jsonObj.put("isPayTmPayment", isPayTmPayment);
            jsonObj.put("isRazorPayPayment", isRazorPayPayment);
            jsonObj.put("isreditUsed", isreditUsed);
            jsonObj.put("paymentMode", "PPI");
            jsonObj.put("paymentPurpose", purpose);
            jsonObj.put("uuid", ynwUUID);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<WalletPaytmChecksum> call = apiService.generateHashPaytm2(body);

        Config.logV("Request--body------Payment---Amount----------------" + amount);
        Config.logV("Request--body------Payment-------------------" + bodyToString(body));
        call.enqueue(new Callback<WalletPaytmChecksum>() {
            @Override
            public void onResponse(Call<WalletPaytmChecksum> call, Response<WalletPaytmChecksum> response) {
                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(mActivity, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        WalletPaytmChecksum respnseWPCSumModel = response.body();

                        if (respnseWPCSumModel.isGateWayPaymentNeeded()) {
                            PaytmChecksum response_data = respnseWPCSumModel.getResponse();

                            try {

                                Map<String, String> map = new HashMap<>();

                                map.put("MID", response_data.getMID());
                                map.put("ORDER_ID", response_data.getORDER_ID());
                                map.put("CUST_ID", response_data.getCUST_ID());
                                map.put("INDUSTRY_TYPE_ID", response_data.getINDUSTRY_TYPE_ID());
                                map.put("CHANNEL_ID", response_data.getCHANNEL_ID());
                                map.put("TXN_AMOUNT", Config.getAmountinTwoDecimalPoints(Double.parseDouble(response_data.getTXN_AMOUNT())));
                                map.put("WEBSITE", response_data.getWEBSITE());
                                Config.logV("Response--Sucess----PAytm-CALLBACK_URL--------------------" + response_data.getCALLBACK_URL());
                                map.put("CALLBACK_URL", response_data.getCALLBACK_URL());
                                map.put("CHECKSUMHASH", response_data.getChecksum());
                                map.put("MERC_UNQ_REF", accountID + "_" + encId);
                                map.put("txnToken", response_data.getTxnToken());
                                PaytmPay(map, response_data.getPaymentEnv(), purpose);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
            public void onFailure(Call<WalletPaytmChecksum> call, Throwable t) {

            }
        });
    }

    public void PaytmPay(Map<String, String> paramMap, String paymentEnv, String purpose) {
        String host = "";
        if (paymentEnv.equalsIgnoreCase("production")) {
            // for test mode use it
            host = "https://securegw.paytm.in/";

        } else {
            // for production mode use it
            host = "https://securegw-stage.paytm.in/";
        }

        PaytmOrder Order = new PaytmOrder(paramMap.get("ORDER_ID"), paramMap.get("MID"), paramMap.get("txnToken"), paramMap.get("TXN_AMOUNT"), paramMap.get("CALLBACK_URL"));
        try {
            TransactionManager transactionManager = new TransactionManager(Order, new PaytmPaymentTransactionCallback() {
                @Override
                public void onTransactionResponse(Bundle inResponse) {
                    Log.d("LOG", "Payment Transaction : " + inResponse);
                    if (inResponse.toString().contains("TXN_SUCCESS")) {

                        if (purpose.equalsIgnoreCase(Constants.PURPOSE_PREPAYMENT) || purpose.equalsIgnoreCase(Constants.DONATION) || purpose.equalsIgnoreCase(Constants.PURPOSE_BILLPAYMENT)) {
                            iPaymentResponse.sendPaymentResponse("TXN_SUCCESS", inResponse.getString("ORDERID"));
                        } else {
                            Toast.makeText(context, "Payment Successful", Toast.LENGTH_LONG).show();
                        }
                        //((Activity) context).finish();
                    } else {
                        Toast.makeText(context, "Payment Failed ", Toast.LENGTH_LONG).show();
                        iPaymentResponse.sendPaymentResponse("TXN_FAILED", inResponse.getString("ORDERID"));
                    }
                }

                @Override
                public void networkNotAvailable() {
                    Log.d("LOG", "UI Error Occur.");
                    Toast.makeText(context, " UI Error Occur. ", Toast.LENGTH_LONG).show();
                    iPaymentResponse.sendPaymentResponse("TXN_FAILED", "");
                }

                @Override
                public void onErrorProceed(String s) {
                    Log.d("LOG", "Error Occur.");
                    Toast.makeText(context, " Error Occur. ", Toast.LENGTH_LONG).show();
                    iPaymentResponse.sendPaymentResponse("TXN_FAILED", "");
                }

                @Override
                public void clientAuthenticationFailed(String inErrorMessage) {
                    Log.d("LOG", "UI Error Occur.");
                    Toast.makeText(context, " Severside Error " + inErrorMessage, Toast.LENGTH_LONG).show();
                    iPaymentResponse.sendPaymentResponse("TXN_FAILED", "");
                }

                @Override
                public void someUIErrorOccurred(String s) {
                    Log.d("LOG", "UI Error Occur.");
                    Toast.makeText(context, " UI Error Occur. ", Toast.LENGTH_LONG).show();
                    iPaymentResponse.sendPaymentResponse("TXN_FAILED", "");
                }

                @Override
                public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                    Log.d("LOG", inErrorMessage);
                    iPaymentResponse.sendPaymentResponse("TXN_FAILED", "");
                }

                @Override
                public void onBackPressedCancelTransaction() {
                    Log.d("LOG", "Back");
                    Toast.makeText(context, "Payment Cancelled ", Toast.LENGTH_LONG).show();
                    iPaymentResponse.sendPaymentResponse("TXN_FAILED", "");
                }

                @Override
                public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                    Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
                    Toast.makeText(context, "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
                    iPaymentResponse.sendPaymentResponse("TXN_FAILED", "");
                }
            });

            transactionManager.setShowPaymentUrl(host + "theia/api/v1/showPaymentPage");
            transactionManager.setAppInvokeEnabled(false);
            transactionManager.startTransaction((Activity) context, ActivityRequestCode);

        } catch (Exception ews) {
            ews.printStackTrace();
        }
    }

}
