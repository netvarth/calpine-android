package com.jaldeeinc.jaldee.activities;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.Interface.IPaymentResponse;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.payment.PaymentGateway;
import com.jaldeeinc.jaldee.payment.PaytmPayment;
import com.jaldeeinc.jaldee.response.CheckSumModel;
import com.jaldeeinc.jaldee.response.PaymentModel;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by sharmila on 11/9/18.
 */

public class PaymentActivity extends AppCompatActivity implements IPaymentResponse {

    static Context mContext;
    static Activity mActivity;
    String ynwUUID, accountID;
    double amountDue;
    String purpose;
    boolean showPaytmWallet = false;
    boolean showPayU = false;
    Button btn_paytm;
    Button btn_payu;
    String displayNotes;
    int customerId;
    private IPaymentResponse paymentResponse;
    String encId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prepayment_layout);
        mContext = this;
        mActivity = this;
        paymentResponse = this;
        TextView tv_title = findViewById(R.id.toolbartitle);
        tv_title.setText("Payment");

        Typeface tyface = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface);
        ImageView iBackPress = findViewById(R.id.backpress);
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                finish();
            }
        });
        btn_paytm = findViewById(R.id.btn_paytm);
        btn_payu = findViewById(R.id.btn_payu);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ynwUUID = extras.getString("ynwUUID");
            accountID = extras.getString("accountID");
            amountDue = extras.getDouble("amountDue");
            purpose = extras.getString("purpose");
            customerId = extras.getInt("customerId");
            encId = extras.getString("encId");


        }
        APIPayment(accountID, ynwUUID, amountDue, purpose);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //   mTxvBuy.setEnabled(true);

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);
            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);

            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {

                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    showAlert("Payment Successful");
                    finish();
                } else if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.CANCELLED)) {
                    showAlert("Payment Cancelled");
                } else if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.FAILED)) {
                    showAlert("Payment Failed");
                }

            } else if (resultModel != null && resultModel.getError() != null) {
                Toast.makeText(this, "Error check log", Toast.LENGTH_SHORT).show();
            } else {
                // Toast.makeText(this, "Both objects are null", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_CANCELED) {
            showAlert("Payment Cancelled");
        }
    }

    private static Double convertStringToDouble(String str) {
        return Double.parseDouble(str);
    }

    private void showAlert(String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(msg);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void APIPayment(final String accountID, final String ynwUUID, final double amountDue, final String purpose) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        Call<ArrayList<PaymentModel>> call = apiService.getPaymentModes(accountID);

        call.enqueue(new Callback<ArrayList<PaymentModel>>() {
            @Override
            public void onResponse(Call<ArrayList<PaymentModel>> call, Response<ArrayList<PaymentModel>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(mActivity, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        ArrayList<PaymentModel> mPaymentData = new ArrayList<>();
                        mPaymentData = response.body();

                        for (int i = 0; i < mPaymentData.size(); i++) {
                            if (mPaymentData.get(i).getDisplayname().equalsIgnoreCase("Wallet")) {
                                showPaytmWallet = true;
                            }

                            if (mPaymentData.get(i).getName().equalsIgnoreCase("CC") || mPaymentData.get(i).getName().equalsIgnoreCase("DC") || mPaymentData.get(i).getName().equalsIgnoreCase("NB")) {
                                showPayU = true;

                            }
                        }

                        if (!showPaytmWallet && !showPayU) {

                        } else {
                            PaymentFunc(ynwUUID, accountID, amountDue, purpose);
                        }

                    } else {

                        if (response.code() != 419)
                            Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<PaymentModel>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(mActivity, mDialog);

            }
        });


    }

    private void PaymentFunc(final String ynwUUID, final String accountID, final double amountDue, final String purpose) {

        if (showPaytmWallet) {
            btn_paytm.setVisibility(View.VISIBLE);
        } else {
            btn_paytm.setVisibility(View.GONE);
        }
        if (showPayU) {
            btn_payu.setVisibility(View.VISIBLE);
        } else {
            btn_payu.setVisibility(View.GONE);
        }

        final EditText edt_message = (EditText) findViewById(R.id.edt_message);
        TextView txtamt = (TextView) findViewById(R.id.txtamount);
//        DecimalFormat format = new DecimalFormat("0.00");
        txtamt.setText("Rs." + Config.getAmountinTwoDecimalPoints(amountDue));
        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        txtamt.setTypeface(tyface1);
        btn_payu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new PaymentGateway(mContext, mActivity).ApiGenerateHash1(ynwUUID, String.valueOf(amountDue), accountID, purpose, "dashboard", customerId, Constants.SOURCE_PAYMENT);

            }
        });

        btn_paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PaytmPayment payment = new PaytmPayment(mContext, paymentResponse);
                payment.ApiGenerateHashPaytm(ynwUUID, String.valueOf(amountDue), accountID, purpose, mContext, mActivity, "home", customerId,encId);
            }
        });

    }

    public static void launchPaymentFlow(String amount, CheckSumModel checksumModel) {
        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();

        payUmoneyConfig.setDoneButtonText("Pay Rs. " + amount);
        String firstname = SharedPreference.getInstance(mContext).getStringValue("firstname", "");
        String lastname = SharedPreference.getInstance(mContext).getStringValue("lastname", "");
        String mobile = SharedPreference.getInstance(mContext).getStringValue("mobile", "");
        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
        builder.setAmount(convertStringToDouble(amount))
                .setTxnId(checksumModel.getTxnid())
                .setPhone(mobile)
                .setProductName(new Gson().toJson(checksumModel.getProductinfo()))
                .setFirstName(firstname)
                .setEmail(checksumModel.getEmail())
                .setsUrl(checksumModel.getSuccessUrl())
                .setfUrl(checksumModel.getFailureUrl())
                .setUdf1("")
                .setUdf2("")
                .setUdf3("")
                .setUdf4("")
                .setUdf5("")
                .setUdf6("")
                .setUdf7("")
                .setUdf8("")
                .setUdf9("")
                .setUdf10("")
                .setIsDebug(true)
                .setKey(checksumModel.getMerchantKey())
                .setMerchantId(checksumModel.getMerchantId());
        try {
            PayUmoneySdkInitializer.PaymentParam mPaymentParams = builder.build();
            if (checksumModel.getChecksum().isEmpty() || checksumModel.getChecksum().equals("")) {
            } else {


                mPaymentParams.setMerchantHash(checksumModel.getChecksum());
                Config.logV("Checksum id---22222222222222--------" + mPaymentParams);

                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, mActivity, R.style.PayUMoney, true);
            }
        } catch (Exception e) {
            Config.logV("e.getMessage()------" + e.getMessage());
            Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_LONG).show();

            // mTxvBuy.setEnabled(true);
        }
    }

    @Override
    public void sendPaymentResponse(String paymentStatus) {

        Toast.makeText(PaymentActivity.this, "Payment Successful", Toast.LENGTH_LONG).show();

    }
}
