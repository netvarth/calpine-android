package com.jaldeeinc.jaldee.payment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.jaldeeinc.jaldee.Interface.IPaymentResponse;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.response.CheckSumModel;
import com.jaldeeinc.jaldee.response.WalletCheckSumModel;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 19/11/18.
 */

public class PaymentGateway {

    Context mCOntext;
    static Activity mActivity;
    private IPaymentResponse iPaymentResponse;

    public PaymentGateway(Context mContext, Activity activity) {
        mCOntext = mContext;
        mActivity = activity;
    }

    public PaymentGateway(Context mContext, Activity activity, IPaymentResponse paymentResponse) {
        mCOntext = mContext;
        mActivity = activity;
        iPaymentResponse = paymentResponse;
    }

    public void ApiGenerateHash(final String amount, String paymentMode, String ynwUUID, String accountID, String purpose, int serviceId, boolean isInternational, String encId, int customerId, String paymentRequestId) {

        ApiInterface apiService =
                ApiClient.getClient(mCOntext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mCOntext, mCOntext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        //  String uniqueID = UUID.randomUUID().toString();
        SharedPreference.getInstance(mCOntext).setValue("prePayment", false);
        JSONObject jsonObj = new JSONObject();
        try {

            jsonObj.put("amount", amount);
            jsonObj.put("paymentMode", paymentMode);
            jsonObj.put("uuid", ynwUUID);
            jsonObj.put("accountId", accountID);
            jsonObj.put("purpose", purpose);
            jsonObj.put("serviceId", serviceId);
            jsonObj.put("isInternational", isInternational);
            if (customerId != 0) {
                jsonObj.put("custId", customerId);
            }
            jsonObj.put("source", Constants.SOURCE_PAYMENT);
            if(paymentRequestId != null && !paymentRequestId.trim().isEmpty() && !paymentRequestId.equalsIgnoreCase("0")){
                jsonObj.put("paymentRequestId", paymentRequestId);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<CheckSumModel> call = apiService.generateHash(body);

        Config.logV("Request--body------Payment---Amount----------------" + amount);
        Config.logV("Request--body------Payment-------------------" + bodyToString(body));
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
                        if (response_data.getPaymentGateway() != null) {
                            String paymentGateway = response_data.getPaymentGateway();
                            if (paymentGateway.equalsIgnoreCase("PAYTM")) {
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

                                    iPaymentResponse.setPaymentRequestId(response_data.getPaymentRequestId());

                                    PaytmPayment payment = new PaytmPayment(mCOntext, iPaymentResponse);

                                    payment.PaytmPay(map, response_data.getPaymentEnv(), purpose);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                response_data.setAmount(Config.getAmountinTwoDecimalPoints(Double.parseDouble(response_data.getAmount())));
                                response_data.setPaymentmode(paymentMode);
                                if (jsonObj.get("purpose").equals(Constants.PURPOSE_PREPAYMENT)) {
                                    response_data.setRetry(false);
                                    SharedPreference.getInstance(mCOntext).setValue("prePayment", true);
                                } else {
                                    response_data.setRetry(true);
                                }
                                if (paymentGateway.equalsIgnoreCase("RAZORPAY")) {
                                    RazorpayPayment razorPayment = new RazorpayPayment(mCOntext, mActivity);
                                    razorPayment.startPayment(response_data);
                                } else {
                                    Intent iPayu = new Intent(mCOntext, PayUMoneyWebview.class);
                                    iPayu.putExtra("responsedata", response_data);
                                    iPayu.putExtra("amount", amount);
                                    mCOntext.startActivity(iPayu);
                                }
                            }
                        }


                    } else {
                        String responseerror = response.errorBody().string();
                        Config.logV("Response--error----------------    ---------" + responseerror);
                        Toast.makeText(mCOntext, responseerror, Toast.LENGTH_LONG).show();
                        if (response.code() == 422) {
                            Toast.makeText(mCOntext, responseerror, Toast.LENGTH_SHORT).show();
                        }
                        iPaymentResponse.update();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<CheckSumModel> call, Throwable t) {

            }
        });
    }

    public void ApiGenerateHashWallet(final String amount, String paymentMode, String ynwUUID, String accountID, String purpose, int serviceId, boolean isInternational, String encId, boolean isJcashUsed, String paymentRequestId) {

        ApiInterface apiService = ApiClient.getClient(mCOntext).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(mCOntext, mCOntext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        //  String uniqueID = UUID.randomUUID().toString();
        SharedPreference.getInstance(mCOntext).setValue("prePayment", false);
        JSONObject jsonObj = new JSONObject();
        try {

            jsonObj.put("accountId", accountID);
            jsonObj.put("amountToPay", Float.valueOf(amount));
            jsonObj.put("isInternational", isInternational);
            jsonObj.put("isJcashUsed", isJcashUsed);
            jsonObj.put("isPayTmPayment", false);
            jsonObj.put("isRazorPayPayment", false);
            jsonObj.put("isreditUsed", false);
            jsonObj.put("paymentMode", paymentMode);
            jsonObj.put("paymentPurpose", purpose);
            jsonObj.put("serviceId", serviceId);
            jsonObj.put("uuid", ynwUUID);
            if(paymentRequestId != null && !paymentRequestId.trim().isEmpty() && !paymentRequestId.equalsIgnoreCase("0")){
                jsonObj.put("paymentRequestId", paymentRequestId);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<WalletCheckSumModel> call = apiService.generateHashWallet(body);

        Config.logV("Request--body------Payment---Amount----------------" + amount);
        Config.logV("Request--body------Payment-------------------" + bodyToString(body));
        call.enqueue(new Callback<WalletCheckSumModel>() {
            @Override
            public void onResponse(Call<WalletCheckSumModel> call, Response<WalletCheckSumModel> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(mActivity, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        WalletCheckSumModel respnseWCSumModel = response.body();

                        if (respnseWCSumModel.isGateWayPaymentNeeded()) {

                            CheckSumModel response_data = respnseWCSumModel.getResponse();
                            if (response_data.getPaymentGateway() != null) {
                                String paymentGateway = response_data.getPaymentGateway();
                                if (paymentGateway.equalsIgnoreCase("PAYTM")) {
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

                                        iPaymentResponse.setPaymentRequestId(response_data.getPaymentRequestId());

                                        PaytmPayment payment = new PaytmPayment(mCOntext, iPaymentResponse);
                                        payment.PaytmPay(map, response_data.getPaymentEnv(), purpose);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    response_data.setPaymentmode(paymentMode);
                                    response_data.setAmount(Config.getAmountinTwoDecimalPoints(Double.parseDouble(response_data.getAmount())));
                                    if (jsonObj.get("paymentPurpose").equals(Constants.PURPOSE_PREPAYMENT)) {
                                        response_data.setRetry(false);
                                        SharedPreference.getInstance(mCOntext).setValue("prePayment", true);
                                    } else {
                                        response_data.setRetry(true);
                                    }
                                    if (paymentGateway.equalsIgnoreCase("RAZORPAY")) {
                                        RazorpayPayment razorPayment = new RazorpayPayment(mCOntext, mActivity);
                                        razorPayment.startPayment(response_data);
                                    } else {
                                        Intent iPayu = new Intent(mCOntext, PayUMoneyWebview.class);
                                        iPayu.putExtra("responsedata", response_data);
                                        iPayu.putExtra("amount", amount);
                                        mCOntext.startActivity(iPayu);
                                    }
                                }
                            }
                        }
                    } else {
                        String responseerror = response.errorBody().string();
                        Config.logV("Response--error-------------------------" + responseerror);
                        if (response.code() == 422) {
                            Toast.makeText(mCOntext, responseerror, Toast.LENGTH_SHORT).show();
                        }
                        iPaymentResponse.update();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<WalletCheckSumModel> call, Throwable t) {

            }
        });
    }

    public void ApiGenerateHash1(String ynwUUID, final String amount, String accountID, String purpose, final String from, int customerId, String source) {


        ApiInterface apiService =
                ApiClient.getClient(mCOntext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mCOntext, mCOntext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        //  String uniqueID = UUID.randomUUID().toString();
        SharedPreference.getInstance(mCOntext).setValue("prePayment", false);
        JSONObject jsonObj = new JSONObject();
        try {

            jsonObj.put("amount", amount);
            jsonObj.put("paymentMode", "DC");
            jsonObj.put("uuid", ynwUUID);
            jsonObj.put("accountId", accountID);
            jsonObj.put("purpose", purpose);
            jsonObj.put("custId", customerId);
            jsonObj.put("source", source);


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
                        if (jsonObj.get("purpose").equals(Constants.PURPOSE_PREPAYMENT)) {
                            response_data.setRetry(false);
                            SharedPreference.getInstance(mCOntext).setValue("prePayment", true);
                        } else {
                            response_data.setRetry(true);
                        }

                        if (from.equalsIgnoreCase("checkin")) {


//                            Log.i("Response--Sucess----" , new Gson().toJson(response.body()));

                            Config.logV("Response--Sucess----------@@@@---------------" + response.body().getPaymentEnv());

                            //CheckIn.launchPaymentFlow(amount, response_data);

                            if (response_data.getPaymentGateway() != null && response_data.getPaymentGateway().equals("RAZORPAY")) {
                                RazorpayPayment razorPayment = new RazorpayPayment(mCOntext, mActivity);
                                razorPayment.startPayment(response_data);
                            } else {
                                Intent iPayu = new Intent(mCOntext, PayUMoneyWebview.class);
                                iPayu.putExtra("responsedata", response_data);
                                iPayu.putExtra("amount", amount);
                                mCOntext.startActivity(iPayu);
                            }


                        } else if (from.equalsIgnoreCase("bill")) {
                            // CheckSumModel response_data = response.body();
//                            Config.logV("Response--Sucess-------------------------" + new Gson().toJson(response.body()));

                            //  BillActivity.launchPaymentFlow(amount, response_data);
                            if (response_data.getPaymentGateway() != null && response_data.getPaymentGateway().equals("RAZORPAY")) {
                                RazorpayPayment razorPayment = new RazorpayPayment(mCOntext, mActivity);
                                razorPayment.startPayment(response_data);
                            } else {
                                Intent iPayu = new Intent(mCOntext, PayUMoneyWebview.class);
                                iPayu.putExtra("responsedata", response_data);
                                iPayu.putExtra("amount", amount);
                                mCOntext.startActivity(iPayu);
                            }
                        } else {
                            // CheckSumModel response_data = response.body();

                            // PaymentActivity.launchPaymentFlow(amount, response_data);
                            if (response_data.getPaymentGateway() != null && response_data.getPaymentGateway().equals("RAZORPAY")) {
                                RazorpayPayment razorPayment = new RazorpayPayment(mCOntext, mActivity);
                                razorPayment.startPayment(response_data);
                            } else {
                                Intent iPayu = new Intent(mCOntext, PayUMoneyWebview.class);
                                iPayu.putExtra("responsedata", response_data);
                                iPayu.putExtra("amount", Config.getAmountinTwoDecimalPoints(Double.parseDouble(amount)));
                                mCOntext.startActivity(iPayu);
                            }
                        }


                    } else {
                        String responseerror = response.errorBody().string();
                        Config.logV("Response--error-------------------------" + responseerror);
                        Toast.makeText(mCOntext, responseerror, Toast.LENGTH_LONG).show();
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

    public void ApiGenerateHash2(String ynwUUID, final String amount, String accountID, String purpose, final String from, boolean isJcashUsed, boolean isreditUsed, boolean isRazorPayPayment, boolean isPayTmPayment) {

        ApiInterface apiService =
                ApiClient.getClient(mCOntext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mCOntext, mCOntext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        //  String uniqueID = UUID.randomUUID().toString();
        SharedPreference.getInstance(mCOntext).setValue("prePayment", false);
        JSONObject jsonObj = new JSONObject();
        try {

            jsonObj.put("accountId", accountID);
            jsonObj.put("amountToPay", Float.valueOf(amount));
            jsonObj.put("isJcashUsed", isJcashUsed);
            jsonObj.put("isPayTmPayment", isPayTmPayment);
            jsonObj.put("isRazorPayPayment", isRazorPayPayment);
            jsonObj.put("isreditUsed", isreditUsed);
            jsonObj.put("paymentMode", "DC");
            jsonObj.put("paymentPurpose", purpose);
            jsonObj.put("uuid", ynwUUID);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<WalletCheckSumModel> call = apiService.generateHash2(body);
        call.enqueue(new Callback<WalletCheckSumModel>() {

            @Override
            public void onResponse(Call<WalletCheckSumModel> call, Response<WalletCheckSumModel> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(mActivity, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());


                    if (response.code() == 200) {

                        WalletCheckSumModel respnseWCSumModel = response.body();

                        if (respnseWCSumModel.isGateWayPaymentNeeded()) {

                            CheckSumModel response_data = respnseWCSumModel.getResponse();
                            response_data.setAmount(Config.getAmountinTwoDecimalPoints(Double.parseDouble(response_data.getAmount())));
                            if (jsonObj.get("paymentPurpose").equals(Constants.PURPOSE_PREPAYMENT)) {
                                response_data.setRetry(false);
                                SharedPreference.getInstance(mCOntext).setValue("prePayment", true);
                            } else {
                                response_data.setRetry(true);
                            }

                            if (from.equalsIgnoreCase("checkin")) {


//                            Log.i("Response--Sucess----" , new Gson().toJson(response.body()));

                                // Config.logV("Response--Sucess----------@@@@---------------" + response.body().getPaymentEnv());

                                //CheckIn.launchPaymentFlow(amount, response_data);

                                if (response_data.getPaymentGateway() != null && response_data.getPaymentGateway().equals("RAZORPAY")) {
                                    RazorpayPayment razorPayment = new RazorpayPayment(mCOntext, mActivity);
                                    razorPayment.startPayment(response_data);
                                } else {
                                    Intent iPayu = new Intent(mCOntext, PayUMoneyWebview.class);
                                    iPayu.putExtra("responsedata", response_data);
                                    iPayu.putExtra("amount", amount);
                                    mCOntext.startActivity(iPayu);
                                }


                            } else if (from.equalsIgnoreCase("bill")) {
                                // CheckSumModel response_data = response.body();
//                            Config.logV("Response--Sucess-------------------------" + new Gson().toJson(response.body()));

                                //  BillActivity.launchPaymentFlow(amount, response_data);
                                if (response_data.getPaymentGateway() != null && response_data.getPaymentGateway().equals("RAZORPAY")) {
                                    RazorpayPayment razorPayment = new RazorpayPayment(mCOntext, mActivity);
                                    razorPayment.startPayment(response_data);
                                } else {
                                    Intent iPayu = new Intent(mCOntext, PayUMoneyWebview.class);
                                    iPayu.putExtra("responsedata", response_data);
                                    iPayu.putExtra("amount", amount);
                                    mCOntext.startActivity(iPayu);
                                }
                            } else {
                                // CheckSumModel response_data = response.body();

                                // PaymentActivity.launchPaymentFlow(amount, response_data);
                                if (response_data.getPaymentGateway() != null && response_data.getPaymentGateway().equals("RAZORPAY")) {
                                    RazorpayPayment razorPayment = new RazorpayPayment(mCOntext, mActivity);
                                    razorPayment.startPayment(response_data);
                                } else {
                                    Intent iPayu = new Intent(mCOntext, PayUMoneyWebview.class);
                                    iPayu.putExtra("responsedata", response_data);
                                    iPayu.putExtra("amount", Config.getAmountinTwoDecimalPoints(Double.parseDouble(amount)));
                                    mCOntext.startActivity(iPayu);
                                }
                            }

                        }
                    } else {
                        String responseerror = response.errorBody().string();
                        Config.logV("Response--error-------------------------" + responseerror);
                        Toast.makeText(mCOntext, responseerror, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<WalletCheckSumModel> call, Throwable t) {

                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(mActivity, mDialog);
            }
        });
    }

  /*  public void sendPaymentStatus(RazorpayModel razorpayModel, String status) {
        RazorpayPayment razorPayment = new RazorpayPayment(mCOntext, mActivity);
        razorpayModel.setStatus(status);
        razorpayModel.setTxnid("");
        razorPayment.sendPaymentStatus(razorpayModel);
    }*/

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

}
