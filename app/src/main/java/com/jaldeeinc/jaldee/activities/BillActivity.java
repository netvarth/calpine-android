package com.jaldeeinc.jaldee.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.BIllDiscountAdapter;
import com.jaldeeinc.jaldee.adapter.BillCouponAdapter;
import com.jaldeeinc.jaldee.adapter.BillDemandDisplayNotesAdapter;
import com.jaldeeinc.jaldee.adapter.BillServiceAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.model.BillModel;
import com.jaldeeinc.jaldee.model.RazorpayModel;
import com.jaldeeinc.jaldee.payment.PaymentGateway;
import com.jaldeeinc.jaldee.payment.PaytmPayment;
import com.jaldeeinc.jaldee.response.CheckSumModel;
import com.jaldeeinc.jaldee.response.CoupnResponse;
import com.jaldeeinc.jaldee.response.PaymentModel;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 1/10/18.
 */

public class BillActivity extends AppCompatActivity implements PaymentResultWithDataListener {

    static Context mCOntext;
    static Activity mActivity;

    String ynwUUID, mprovider;
    TextView tv_provider, tv_customer, tv_date, tv_gstn, tv_bill;
    EditText mbill_coupon_edit;
    BillModel mBillData;
    TextView tv_paid, tv_totalamt, tv_jaldeeCouponLabel, gstLabel;
    RecyclerView recycle_item, recycle_discount_total, coupon_added, recycle_display_notes;
    BillServiceAdapter billServiceAdapter;
    BillCouponAdapter billCouponAdapter;
    ArrayList<BillModel> serviceArrayList = new ArrayList<>();
    ArrayList<BillModel> itemArrayList = new ArrayList<>();
    ArrayList<BillModel> serviceItemArrayList = new ArrayList<>();


    ArrayList<BillModel> discountArrayList = new ArrayList<>();
    ArrayList<BillModel> coupanArrayList = new ArrayList<>();

    Button btn_pay, mbill_applybtn;
    TextView txtnetRate, txttotal, tv_amount, tv_grosstotal, tv_gross, txtaxval, txttax, billLabel, jdnLabel, jdnValue;
    LinearLayout paidlayout, amountlayout, taxlayout, couponCheckin, jcLayout, jdnLayout;
    String sAmountPay;
    String accountID;
    String payStatus, consumer;
    String coupon_entered;
    String purpose;
    String displayNotes;
    TextView tv_billnotes, tv_notes;
    int customerId;
    String uniqueId;
    ArrayList<CoupnResponse> s3couponList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bill);
        mCOntext = this;
        mActivity = this;

        mbill_applybtn = findViewById(R.id.bill_applybtn);
        mbill_coupon_edit = findViewById(R.id.bill_coupon_edit);
        paidlayout = findViewById(R.id.paidlayout);
        couponCheckin = findViewById(R.id.couponCheckin);
        amountlayout = findViewById(R.id.amountlayout);
        tv_grosstotal = findViewById(R.id.grosstotal);
        tv_provider = findViewById(R.id.provider);
        tv_customer = findViewById(R.id.txtcustomer);
        tv_date = findViewById(R.id.txtdate);
        tv_bill = findViewById(R.id.txtbill);
        billLabel = findViewById(R.id.billLabel);
        tv_gstn = findViewById(R.id.txtgstn);
        gstLabel = findViewById(R.id.gstLabel);
        tv_gross = findViewById(R.id.tv_gross);
        txttax = findViewById(R.id.tv_tax);
        txtaxval = findViewById(R.id.txtaxval);
        taxlayout = findViewById(R.id.taxlayout);
        tv_amount = findViewById(R.id.txtamt);
        tv_paid = findViewById(R.id.amtpaid);
        tv_totalamt = findViewById(R.id.totalamt);
        btn_pay = findViewById(R.id.btn_pay);
        recycle_item = findViewById(R.id.recycle_item);
        coupon_added = findViewById(R.id.coupon_added);
        tv_jaldeeCouponLabel = findViewById(R.id.jaldeeCouponLabel);
        recycle_discount_total = findViewById(R.id.recycle_discount_total);
        jcLayout = findViewById(R.id.jcLayout);
        TextView tv_title = findViewById(R.id.toolbartitle);
        tv_billnotes = findViewById(R.id.billnotes);
        tv_notes = findViewById(R.id.notes);
        Typeface tyface = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface);
        txtnetRate = findViewById(R.id.txtnetRate);
        jdnLabel = findViewById(R.id.jdnLabel);
        jdnLayout = findViewById(R.id.jdnLayout);
        jdnValue = findViewById(R.id.jdnValue);
        txttotal = findViewById(R.id.txttotal);
        recycle_display_notes = findViewById(R.id.recycle_display_notes_demand);

        tv_totalamt.setTypeface(tyface);
        txttotal.setTypeface(tyface);
        txtnetRate.setTypeface(tyface);


        ImageView iBackPress = findViewById(R.id.backpress);
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ynwUUID = extras.getString("ynwUUID");
            mprovider = extras.getString("provider");
            accountID = extras.getString("accountID");
            payStatus = extras.getString("payStatus");
            consumer = extras.getString("consumer");
            purpose = extras.getString("purpose");
            customerId = extras.getInt("customerId");
            uniqueId = extras.getString("uniqueId");
        }


        if (payStatus != null) {

            if (payStatus.equalsIgnoreCase("FullyPaid")) {
                tv_title.setText("Receipt");
                btn_pay.setVisibility(View.GONE);
                couponCheckin.setVisibility(View.GONE);
            } else {
                tv_title.setText("Bill");
                btn_pay.setVisibility(View.VISIBLE);
                couponCheckin.setVisibility(View.VISIBLE);
            }
        }
        ApiBill(ynwUUID);
        ApiJaldeegetS3Coupons(uniqueId);


        Typeface tyface1 = Typeface.createFromAsset(this.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_provider.setTypeface(tyface1);
        tv_provider.setText(Config.toTitleCase(mprovider));

       /* btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/
        APIPayment(accountID);

        mbill_applybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mbill_coupon_edit.getText().toString().equals("")) {
                    Toast.makeText(BillActivity.this, "Enter a coupon", Toast.LENGTH_SHORT).show();
                } else {
                    coupon_entered = mbill_coupon_edit.getText().toString();
                    boolean found = false;
                    for (int i = 0; i < s3couponList.size(); i++) {
                        if (s3couponList.get(i).getJaldeeCouponCode().equals(coupon_entered)) {
                            found = true;
                            break;
                        }
                    }
                    if(found) {
                        ApigetBill(coupon_entered, ynwUUID, accountID);
                    }
                    else{
                        Toast.makeText(BillActivity.this, "Coupon Invalid", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!showPaytmWallet && !showPayU) {

                } else {
                    try {
                        btn_pay.setVisibility(View.VISIBLE);
                        final BottomSheetDialog dialog = new BottomSheetDialog(mCOntext);
                        dialog.setContentView(R.layout.prepayment);
                        dialog.show();

                        Button btn_paytm = dialog.findViewById(R.id.btn_paytm);
                        Button btn_payu = dialog.findViewById(R.id.btn_payu);
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

                        final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
                        TextView txtamt = (TextView) dialog.findViewById(R.id.txtamount);
//                        DecimalFormat format = new DecimalFormat("0.00");
                        txtamt.setText("Rs." + Config.getAmountinTwoDecimalPoints(Double.parseDouble(sAmountPay)));
                        Typeface tyface1 = Typeface.createFromAsset(mCOntext.getAssets(),
                                "fonts/Montserrat_Bold.otf");
                        txtamt.setTypeface(tyface1);
                        btn_payu.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                new PaymentGateway(mCOntext, mActivity).ApiGenerateHash1(ynwUUID, sAmountPay, accountID, purpose, "bill",customerId,Constants.SOURCE_PAYMENT);

                                dialog.dismiss();
                            }
                        });

                        btn_paytm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                PaytmPayment payment = new PaytmPayment(mCOntext);
                                payment.ApiGenerateHashPaytm(ynwUUID, sAmountPay, accountID, purpose, mCOntext, mActivity, "",customerId);
                                dialog.dismiss();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }


    boolean showPaytmWallet = false;
    boolean showPayU = false;
    ArrayList<PaymentModel> mPaymentData = new ArrayList<>();

    private void APIPayment(String accountID) {


        ApiInterface apiService =
                ApiClient.getClient(mCOntext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mCOntext, mCOntext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        Call<ArrayList<PaymentModel>> call = apiService.getPaymentModes(accountID);

        call.enqueue(new Callback<ArrayList<PaymentModel>>() {
            @Override
            public void onResponse(Call<ArrayList<PaymentModel>> call, Response<ArrayList<PaymentModel>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

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
                            btn_pay.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        btn_pay.setVisibility(View.INVISIBLE);
                        if (response.code() != 419)
                            Toast.makeText(mCOntext, response.errorBody().string(), Toast.LENGTH_LONG).show();
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
                    Config.closeDialog(getParent(), mDialog);
            }
        });
    }

    // Dialog mDialog1 = null;

    public static void launchPaymentFlow(String amount, CheckSumModel checksumModel) {
        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();

        payUmoneyConfig.setDoneButtonText("Pay Rs." + amount);
        String firstname = SharedPreference.getInstance(mCOntext).getStringValue("firstname", "");
        String lastname = SharedPreference.getInstance(mCOntext).getStringValue("lastname", "");

        String mobile = SharedPreference.getInstance(mCOntext).getStringValue("mobile", "");


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
                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, mActivity, R.style.PayUMoney, true);
            }
        } catch (Exception e) {
            Config.logV("e.getMessage()------" + e.getMessage());
            Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //   mTxvBuy.setEnabled(true);

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

    BIllDiscountAdapter billDiscountAdapter;
    BillDemandDisplayNotesAdapter billDemandDisplayNotesAdapter;

    private void ApiBill(String ynwuuid) {


        final ApiInterface apiService =
                ApiClient.getClient(this).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(mCOntext, mCOntext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Call<BillModel> call = apiService.getBill(ynwuuid,accountID);

        Config.logV("Request--ynwuuid1-------------------------" + ynwuuid);

        call.enqueue(new Callback<BillModel>() {
            @Override
            public void onResponse(Call<BillModel> call, Response<BillModel> response) {

                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(mActivity, mDialog);

                    Config.logV("URL12---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code12-------------------------" + response.code());
//                    Config.logV("Response--code12-------------------------" + new Gson().toJson(response.body()));

                    if (response.code() == 200) {

                        mBillData = response.body();

                        String firstName = SharedPreference.getInstance(mCOntext).getStringValue("firstname", "");
                        String lastNme = SharedPreference.getInstance(mCOntext).getStringValue("lastname", "");
                        tv_customer.setText(consumer);
                        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
                        DateFormat targetFormat = new SimpleDateFormat(("dd-MM-yyyy hh:mm a"));
                        Date date = originalFormat.parse(mBillData.getCreatedDate());
                        String formattedDate = targetFormat.format(date);
                        tv_date.setText(formattedDate);



                        Typeface tyface = Typeface.createFromAsset(getAssets(),
                                "fonts/Montserrat_Bold.otf");
                        tv_gross.setTypeface(tyface);
                        tv_grosstotal.setTypeface(tyface);


                        if (mBillData.getNetTotal() != 0.0) {
                            tv_grosstotal.setVisibility(View.VISIBLE);
                            tv_grosstotal.setText("₹ " + Config.getAmountinTwoDecimalPoints(mBillData.getNetTotal()));

                        } else {
                            tv_grosstotal.setVisibility(View.GONE);
                        }


                        if (mBillData.getGstNumber() != null) {
                            tv_gstn.setText(mBillData.getGstNumber());
                        } else {
                            tv_gstn.setVisibility(View.INVISIBLE);
                            gstLabel.setVisibility(View.INVISIBLE);
                        }

                        if (String.valueOf(mBillData.getBillId()).equals("")) {
                            billLabel.setVisibility(View.GONE);
                            tv_bill.setVisibility(View.GONE);
                        } else {
                            tv_bill.setText(String.valueOf(mBillData.getBillId()));
                        }


                        if (mBillData.getNetRate() != 0) {

                            amountlayout.setVisibility(View.VISIBLE);
//                            DecimalFormat format = new DecimalFormat("0.00");
                            tv_amount.setText("₹ " + Config.getAmountinTwoDecimalPoints(mBillData.getNetRate()));
                        } else {

                            amountlayout.setVisibility(View.GONE);
                        }



                        if (mBillData.getTotalAmountPaid() != 0) {
                            paidlayout.setVisibility(View.VISIBLE);
//                            DecimalFormat format = new DecimalFormat("0.00");
                            tv_paid.setText("₹ " + Config.getAmountinTwoDecimalPoints(mBillData.getTotalAmountPaid()));
                        } else {

                            paidlayout.setVisibility(View.GONE);
                        }


                        double total = mBillData.getNetRate() - mBillData.getTotalAmountPaid();

                        if (total >= 0) {
                            txttotal.setVisibility(View.VISIBLE);
                            tv_totalamt.setVisibility(View.VISIBLE);
//                            DecimalFormat format = new DecimalFormat("0.00");
//                            tv_totalamt.setText("₹ " + String.valueOf(total));
                            tv_totalamt.setText("₹ " + Config.getAmountinTwoDecimalPoints(total));
                            txttotal.setText("Amount Due");
                        } else if(total < 0) {
                            tv_totalamt.setVisibility(View.VISIBLE);
                            txttotal.setVisibility(View.VISIBLE);
//                            DecimalFormat format = new DecimalFormat("0.00");
                            tv_totalamt.setText("₹ " + Config.getAmountinTwoDecimalPoints(Math.abs(total)));
                            txttotal.setText("Refund Amount");
                            btn_pay.setVisibility(View.INVISIBLE);
                            couponCheckin.setVisibility(View.INVISIBLE);
                        } else{
                            tv_totalamt.setVisibility(View.INVISIBLE);
                            txttotal.setVisibility(View.INVISIBLE);
//                            DecimalFormat format = new DecimalFormat("0.00");
                      //      tv_totalamt.setText("₹ " + Config.getAmountinTwoDecimalPoints(Math.abs(total)));
                          //
                            //
                            //  txttotal.setText("Refund Amount");
                            btn_pay.setVisibility(View.INVISIBLE);
                            couponCheckin.setVisibility(View.INVISIBLE);
                        }


//                        sAmountPay = String.valueOf(total);
                        sAmountPay = Config.getAmountinTwoDecimalPoints(total);
//                        Config.logV("Amount PAy@@@@@@@@@@@@@@@@@@@@@@@@" + sAmountPay);

                        if (total != 0.0 && total > 0) {
                            btn_pay.setVisibility(View.VISIBLE);
                        } else {
                            btn_pay.setVisibility(View.INVISIBLE);
                        }

                        if (mBillData.getTaxableTotal() > 0) {
                            taxlayout.setVisibility(View.VISIBLE);
                            txttax.setText("Tax " + String.valueOf(mBillData.getTaxPercentage()) + "% of " + "₹ " + Config.getAmountinTwoDecimalPoints(mBillData.getTaxableTotal()) + "\n" + "(CGST: " + String.valueOf(mBillData.getTaxPercentage() / 2) + " %" + ", SGST: " + String.valueOf(mBillData.getTaxPercentage() / 2) + " %)");
//                        //    txttax.setText("Tax " + String.valueOf(mBillData.getTaxPercentage()) + "% of " + "₹ " + String.valueOf(mBillData.getTaxableTotal()) + "\n" + "(CGST: " + String.valueOf(mBillData.getTaxPercentage() / 2) + " %" + ", SGST: " + String.valueOf(mBillData.getTaxPercentage() / 2) + " %)");


//                            txtaxval.setText("(+)₹ " + String.valueOf(mBillData.getTotalTaxAmount()));
                            txtaxval.setText("(+)₹ " + Config.getAmountinTwoDecimalPoints(mBillData.getTotalTaxAmount()));
                        } else {
                            taxlayout.setVisibility(View.GONE);
                        }


                        if (mBillData.getJCoupon() != null) {
                            if (mBillData.getJCoupon().size() > 0 && mBillData.getJCoupon().size() == 1) {
                                jcLayout.setVisibility(View.VISIBLE);
                                tv_jaldeeCouponLabel.setText("Jaldee Coupon");
                                tv_jaldeeCouponLabel.setVisibility(View.VISIBLE);
                            }
                            if (mBillData.getJCoupon().size() > 1) {
                                jcLayout.setVisibility(View.VISIBLE);
                                tv_jaldeeCouponLabel.setText("Jaldee Coupons");
                                tv_jaldeeCouponLabel.setVisibility(View.VISIBLE);
                            }
                            RecyclerView.LayoutManager cLayoutManager = new LinearLayoutManager(mCOntext);
                            coupon_added.setLayoutManager(cLayoutManager);
                            billCouponAdapter = new BillCouponAdapter(mBillData.getJCoupon());
                            coupon_added.setAdapter(billCouponAdapter);
                            billCouponAdapter.notifyDataSetChanged();
                        } else {
                            jcLayout.setVisibility(View.GONE);
                        }


                        if (mBillData.getJdn() != null) {
                            jdnLayout.setVisibility(View.VISIBLE);
                            jdnLabel.setText("JDN");
                            jdnLabel.setVisibility(View.VISIBLE);
                            jdnValue.setText(("(-)₹ " + Config.getAmountinTwoDecimalPoints(mBillData.getJdn().getDiscount())));
                            jdnValue.setVisibility(View.VISIBLE);
                        } else {
                            jdnLayout.setVisibility(View.GONE);
                            jdnLabel.setVisibility(View.GONE);
                            jdnValue.setVisibility(View.GONE);
                        }

                        try {

                            if (mBillData.getDisplayNotes()!= null && mBillData.getDisplayNotes().getDisplayNotes() != null) {
                                tv_billnotes.setVisibility(View.VISIBLE);
                                tv_notes.setVisibility(View.VISIBLE);
                                tv_notes.setText(mBillData.getDisplayNotes().getDisplayNotes());
                            } else {
                                tv_billnotes.setVisibility(View.GONE);
                                tv_notes.setVisibility(View.GONE);

                            }
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }


                        serviceArrayList = response.body().getService();
                        itemArrayList = response.body().getItems();

                        serviceArrayList.addAll(itemArrayList);
                        Config.logV("Sevice ArrayList-------------------" + serviceArrayList.size());
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mCOntext);
                        recycle_item.setLayoutManager(mLayoutManager);
                        billServiceAdapter = new BillServiceAdapter(serviceArrayList, mCOntext);
                        recycle_item.setAdapter(billServiceAdapter);
                        billServiceAdapter.notifyDataSetChanged();

                        discountArrayList.clear();
                        coupanArrayList.clear();
                        discountArrayList = response.body().getDiscount();
                        coupanArrayList = response.body().getProviderCoupon();

                        if (discountArrayList != null || coupanArrayList != null) {
                            discountArrayList.addAll(coupanArrayList);

                            RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(mCOntext);
                            recycle_discount_total.setLayoutManager(mLayoutManager1);

                            if (discountArrayList.size() > 0) {

                                recycle_discount_total.setVisibility(View.VISIBLE);
                                billDiscountAdapter = new BIllDiscountAdapter("totalbill", discountArrayList, mCOntext);
                            }
                            recycle_discount_total.setAdapter(billDiscountAdapter);
                            billDiscountAdapter.notifyDataSetChanged();
                        } else {
                            recycle_discount_total.setVisibility(View.GONE);
                        }
                        if(discountArrayList!=null){
                            RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(mCOntext);
                            recycle_display_notes.setLayoutManager(mLayoutManager1);
                            if (discountArrayList.size() > 0) {

                                recycle_display_notes.setVisibility(View.VISIBLE);
                                billDemandDisplayNotesAdapter = new BillDemandDisplayNotesAdapter( discountArrayList, mCOntext, mBillData);
                            }
                            recycle_display_notes.setAdapter(billDemandDisplayNotesAdapter);
                            billDemandDisplayNotesAdapter.notifyDataSetChanged();
                        } else {
                            recycle_display_notes.setVisibility(View.GONE);
                        }





                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<BillModel> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(mActivity, mDialog);

            }
        });

    }
    private void ApiJaldeegetS3Coupons(String uniqueID) {
        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mActivity).create(ApiInterface.class);
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));
        Call<ArrayList<CoupnResponse>> call = apiService.getCoupanList(Integer.parseInt(uniqueID), sdf.format(currentTime));
        call.enqueue(new Callback<ArrayList<CoupnResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<CoupnResponse>> call, Response<ArrayList<CoupnResponse>> response) {
                try {
                    Config.logV("Response---------------------------" + response.body().toString());
                    Config.logV("URL-response--------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        s3couponList.clear();
                        s3couponList = response.body();
                        Log.i("CouponResponse", s3couponList.toString());


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<CoupnResponse>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());

            }
        });
    }

    private void ApigetBill(final String couponss, String ynwuuid, String acccount) {


        final ApiInterface apiService =
                ApiClient.getClient(this).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(this, this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Call<BillModel> call = apiService.getBillCoupon(couponss, ynwuuid, acccount);

        Config.logV("Request--ynwuuidssasasa-------------------------" + ynwuuid);

        call.enqueue(new Callback<BillModel>() {
            @Override
            public void onResponse(Call<BillModel> call, Response<BillModel> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(mActivity, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
//                    Config.logV("zxczqw" + new Gson().toJson(response.body()));

                    if (response.code() == 200) {


                        finish();
                        startActivity(getIntent());

                        Toast.makeText(BillActivity.this, couponss + " " + "Coupon Added", Toast.LENGTH_SHORT).show();

                        Config.logV("Response--Array size--Activessssssssss-----------------------" + response.body().toString());
//                        Config.logV("zxczxczxzc" + new Gson().toJson(response.body()));
                    }

                    if (response.code() == 422) {
                        String errorString = response.errorBody().string();
                        Toast.makeText(BillActivity.this, errorString, Toast.LENGTH_SHORT).show();

                    }

                    if (response.code() == 409) {
                        String errorString = response.errorBody().string();
                        Toast.makeText(BillActivity.this, errorString, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<BillModel> call, Throwable t) {


                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(mActivity, mDialog);
            }
        });


    }
    public void paymentFinished(RazorpayModel razorpayModel) {
        Intent intent = new Intent(mCOntext, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("message", "razorpay");
        startActivity(intent);
        finish();
    }
    @Override
    public void onPaymentSuccess(String razorpayPaymentID, PaymentData paymentData) {
        Log.i("mani","here");
        try {
//            Log.i("Success1111",  new Gson().toJson(paymentData));
            RazorpayModel razorpayModel = new RazorpayModel(paymentData);
            new PaymentGateway(mCOntext, mActivity).sendPaymentStatus(razorpayModel, "SUCCESS");
            Toast.makeText(mCOntext, "Payment Successful. Payment Id:" + razorpayPaymentID, Toast.LENGTH_LONG).show();
            paymentFinished(razorpayModel);

        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int code, String response, PaymentData paymentData) {
        try {
//            Log.i("here.....", new Gson().toJson(paymentData));
            Toast.makeText(mCOntext, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentError..", e);
        }
    }
}
