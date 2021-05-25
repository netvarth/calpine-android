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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.Interface.IPaymentResponse;
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
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.ActiveOrders;
import com.jaldeeinc.jaldee.response.CheckSumModel;
import com.jaldeeinc.jaldee.response.CoupnResponse;
import com.jaldeeinc.jaldee.response.PaymentModel;
import com.jaldeeinc.jaldee.response.ProviderCouponResponse;
import com.jaldeeinc.jaldee.response.RefundDetails;
import com.jaldeeinc.jaldee.response.WalletCheckSumModel;
import com.jaldeeinc.jaldee.response.WalletEligibleJCash;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 1/10/18.
 */

public class BillActivity extends AppCompatActivity implements PaymentResultWithDataListener, IPaymentResponse {

    static Context mCOntext;
    static Activity mActivity;

    String ynwUUID, mprovider;
    TextView tv_provider, tv_customer, tv_date, tv_gstn, tv_bill;
    EditText mbill_coupon_edit;
    BillModel mBillData;
    TextView tv_paid, tv_totalamt, tv_jaldeeCouponLabel, tv_providerCouponLabel, gstLabel, tv_refundamount;
    RecyclerView recycle_item, recycle_discount_total, coupon_added, proCoupon_added, recycle_display_notes;
    BillServiceAdapter billServiceAdapter;
    BillCouponAdapter billCouponAdapter, billProCouponAdapter;
    ArrayList<BillModel> serviceArrayList = new ArrayList<>();
    ArrayList<BillModel> itemArrayList = new ArrayList<>();
    ArrayList<BillModel> serviceItemArrayList = new ArrayList<>();


    ArrayList<BillModel> discountArrayList = new ArrayList<>();
    ArrayList<BillModel> coupanArrayList = new ArrayList<>();

    Button btn_pay, mbill_applybtn;
    TextView txtnetRate, txttotal, tv_amount, tv_grosstotal, tv_gross, txtaxval, txttax, billLabel, jdnLabel, jdnValue, txtrefund, txtdelivery, tv_deliveryCharge, tv_amount_Saved;
    LinearLayout paidlayout, amountlayout, taxlayout, couponCheckin, jcLayout, pcLayout, jdnLayout, refundLayout, deliveryLayout, llproviderlayout, ll_amount_Saved, llJCash;
    CheckBox cbJCash;
    String sAmountPay;
    String accountID;
    String payStatus, consumer;
    String coupon_entered;
    String purpose;
    String displayNotes;
    String payRemainingAmount = "";
    TextView tv_billnotes, tv_notes, tvProviderName;
    int customerId;
    String uniqueId;
    double total, totalRefund = 0.0;
    ;
    ArrayList<CoupnResponse> s3couponList = new ArrayList<>();
    ArrayList<ProviderCouponResponse> providerCouponList = new ArrayList<>();
    WalletEligibleJCash walletEligibleJCash = new WalletEligibleJCash();
    private IPaymentResponse paymentResponse;
    String encId;
    String bookingStatus;
    TextView tv_title;
    private boolean fromPushNotification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bill);
        mCOntext = this;
        mActivity = this;
        paymentResponse = this;
        mbill_applybtn = findViewById(R.id.bill_applybtn);
        mbill_coupon_edit = findViewById(R.id.bill_coupon_edit);
        paidlayout = findViewById(R.id.paidlayout);
        couponCheckin = findViewById(R.id.couponCheckin);
        llJCash = findViewById(R.id.ll_jCash);
        cbJCash = findViewById(R.id.cb_jCash);
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
        proCoupon_added = findViewById(R.id.proCoupon_added);
        tv_jaldeeCouponLabel = findViewById(R.id.jaldeeCouponLabel);
        tv_providerCouponLabel = findViewById(R.id.providerCouponLabel);
        recycle_discount_total = findViewById(R.id.recycle_discount_total);
        jcLayout = findViewById(R.id.jcLayout);
        pcLayout = findViewById(R.id.pcLayout);
        tv_title = findViewById(R.id.toolbartitle);
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
        txtrefund = findViewById(R.id.txtrefund);
        tv_refundamount = findViewById(R.id.refundamt);
        refundLayout = findViewById(R.id.refundlayout);
        deliveryLayout = findViewById(R.id.deliveryLayout);
        txtdelivery = findViewById(R.id.tv_deliveryCharges);
        tv_deliveryCharge = findViewById(R.id.grossDelivery);
        recycle_display_notes = findViewById(R.id.recycle_display_notes_demand);
        llproviderlayout = findViewById(R.id.ll_providerLayout);
        tvProviderName = findViewById(R.id.tv_providerName);
        ll_amount_Saved = findViewById(R.id.ll_amount_Saved);
        tv_amount_Saved = findViewById(R.id.tv_amount_Saved);

        tv_amount_Saved.setTypeface(tyface);
        tv_totalamt.setTypeface(tyface);
        txttotal.setTypeface(tyface);
        txtnetRate.setTypeface(tyface);
        boolean iscouponCheckin = false;


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
            encId = extras.getString("encId");
            bookingStatus = extras.getString("bookingStatus");
            fromPushNotification = extras.getBoolean(Constants.PUSH_NOTIFICATION, false);
        }

        if (encId == null && ynwUUID != null) {   // if encId is null then  the activity is launched from notification, so checking for required values and getting them via API calls

            if (ynwUUID.contains("_appt")) {
                getAppointmentDetails(ynwUUID, accountID);
            } else if (ynwUUID.contains("_wl")) {
                getCheckInDetails(ynwUUID, accountID);
            } else if (ynwUUID.contains("_odr")) {
                getOrderDetails(ynwUUID, Integer.parseInt(accountID));
            }
        } else {

            // if launched directly from APP
            UpdateUI();
        }


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
                    for (int i = 0; i < providerCouponList.size(); i++) {
                        if (providerCouponList.get(i).getCouponCode().equals(coupon_entered)) {
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        ApigetBill(coupon_entered, ynwUUID, accountID);
                    } else {
                        Toast.makeText(BillActivity.this, "Coupon Invalid", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPrePayRemainingAmntNeeded(sAmountPay);
            }
        });


    }

    private void getPrePayRemainingAmntNeeded(String amount) {
        final ApiInterface apiService =
                ApiClient.getClient(BillActivity.this).create(ApiInterface.class);
        Call<String> call = apiService.getPrePayRemainingAmnt(cbJCash.isChecked(), false, amount);
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                Config.logV("URL------GET Prepay remaining amount---------" + response.raw().request().url().toString().trim());
                Config.logV("Response--code-------------------------" + response.code());
                if (response.code() == 200) {
                    payRemainingAmount = response.body();
                    Payment();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i("PrePayRemainingAmntNeed", t.toString());
                t.printStackTrace();
            }
        });
    }

    private void Payment() {

        if (cbJCash.isChecked() && Double.parseDouble(payRemainingAmount) <= 0) {
            isGateWayPaymentNeeded(ynwUUID, sAmountPay, accountID, Constants.PURPOSE_PREPAYMENT, true, false, false, false, "JCASH");

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
                if (cbJCash.isChecked()) {
                    txtamt.setText("Rs." + Config.getAmountinTwoDecimalPoints(Double.parseDouble(payRemainingAmount)));

                } else {
                    txtamt.setText("Rs." + Config.getAmountinTwoDecimalPoints(Double.parseDouble(sAmountPay)));
                }
                Typeface tyface1 = Typeface.createFromAsset(mCOntext.getAssets(),
                        "fonts/Montserrat_Bold.otf");
                txtamt.setTypeface(tyface1);
                btn_payu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (cbJCash.isChecked()) {
                            new PaymentGateway(mCOntext, mActivity).ApiGenerateHash2(ynwUUID, sAmountPay, accountID, Constants.PURPOSE_PREPAYMENT, "bill", true, false, true, false);

                        } else {
                            new PaymentGateway(mCOntext, mActivity).ApiGenerateHash1(ynwUUID, sAmountPay, accountID, purpose, "bill", customerId, Constants.SOURCE_PAYMENT);
                        }
                        dialog.dismiss();
                    }
                });

                btn_paytm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        PaytmPayment payment = new PaytmPayment(mCOntext, paymentResponse);
                        if (cbJCash.isChecked()) {
                            payment.ApiGenerateHashPaytm2(ynwUUID, sAmountPay, accountID, Constants.PURPOSE_PREPAYMENT, "", true, false, false, true, encId, mCOntext, mActivity);

                        } else {
                            payment.ApiGenerateHashPaytm(ynwUUID, sAmountPay, accountID, purpose, mCOntext, mActivity, "", customerId, encId);
                        }
                        dialog.dismiss();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void isGateWayPaymentNeeded(String ynwUUID, final String amount, String accountID, String purpose, boolean isJcashUsed, boolean isreditUsed, boolean isRazorPayPayment, boolean isPayTmPayment, String paymentMode) {

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
            jsonObj.put("paymentMode", paymentMode);
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

                        if (!respnseWCSumModel.isGateWayPaymentNeeded()) {
                            finish();
                            startActivity(getIntent());

                            Toast.makeText(BillActivity.this, "Make Bill Payment", Toast.LENGTH_SHORT).show();

                            Config.logV("Response--Array size--Activessssssssss-----------------------" + response.body().toString());
//                        Config.logV("zxczxczxzc" + new Gson().toJson(response.body()));

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

            }
        });
    }
    private void UpdateUI() {

        if (payStatus != null) {

            if (payStatus.equalsIgnoreCase("FullyPaid") || payStatus.equalsIgnoreCase("FullyRefunded")) {
                tv_title.setText("Receipt");
                btn_pay.setVisibility(View.GONE);
                couponCheckin.setVisibility(View.GONE);
                llJCash.setVisibility(View.GONE);
                cbJCash.setChecked(false);
            } else {
                tv_title.setText("Bill");
                btn_pay.setVisibility(View.VISIBLE);
                couponCheckin.setVisibility(View.VISIBLE);
                llJCash.setVisibility(View.VISIBLE);
                cbJCash.setChecked(true);

                ApiJaldeegetS3Coupons(uniqueId);
                ApiJaldeegetProviderCoupons(uniqueId);/////////////////
                APIGetWalletEligibleJCash();

            }
        }
        ApiBill(ynwUUID);


        Typeface tyface1 = Typeface.createFromAsset(this.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_provider.setTypeface(tyface1);
        tv_provider.setText(Config.toTitleCase(mprovider));


        APIPayment(accountID);


    }

    public void getAppointmentDetails(String uid, String id) {
        final ApiInterface apiService =
                ApiClient.getClient(BillActivity.this).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(BillActivity.this, BillActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ActiveAppointment> call = apiService.getActiveAppointmentUUID(uid, id);
        call.enqueue(new Callback<ActiveAppointment>() {
            @Override
            public void onResponse(Call<ActiveAppointment> call, Response<ActiveAppointment> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        ActiveAppointment apptInfo = response.body();

                        if (apptInfo != null) {

                            try {
                                mprovider = apptInfo.getProviderAccount().getBusinessName();
                                payStatus = apptInfo.getPaymentStatus();
                                consumer = apptInfo.getAppmtFor().get(0).getFirstName() + " " + apptInfo.getAppmtFor().get(0).getLastName();
                                purpose = Constants.PURPOSE_BILLPAYMENT;
                                uniqueId = apptInfo.getProviderAccount().getUniqueId();
                                encId = apptInfo.getAppointmentEncId();
                                bookingStatus = apptInfo.getApptStatus();

                                // update UI with the data from notification
                                UpdateUI();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ActiveAppointment> call, Throwable t) {
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });
    }

    private void getCheckInDetails(String uid, String id) {

        final ApiInterface apiService =
                ApiClient.getClient(BillActivity.this).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(BillActivity.this, BillActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ActiveCheckIn> call = apiService.getActiveCheckInUUID(uid, id);
        call.enqueue(new Callback<ActiveCheckIn>() {
            @Override
            public void onResponse(Call<ActiveCheckIn> call, Response<ActiveCheckIn> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        ActiveCheckIn activeCheckIn = response.body();

                        if (activeCheckIn != null) {

                            mprovider = activeCheckIn.getProviderAccount().getBusinessName();
                            payStatus = activeCheckIn.getPaymentStatus();
                            consumer = activeCheckIn.getWaitlistingFor().get(0).getFirstName() + " " + activeCheckIn.getWaitlistingFor().get(0).getLastName();
                            purpose = Constants.PURPOSE_BILLPAYMENT;
                            uniqueId = activeCheckIn.getProviderAccount().getUniqueId();
                            encId = activeCheckIn.getCheckinEncId();
                            bookingStatus = activeCheckIn.getWaitlistStatus();
                            // update UI with the data from notification
                            UpdateUI();
                        }

                    }
                } catch (Exception e) {
                    Log.i("mnbbnmmnbbnm", e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ActiveCheckIn> call, Throwable t) {
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });
    }

    private void getOrderDetails(String orderUUid, int accountId) {

        ApiInterface apiService = ApiClient.getClient(BillActivity.this).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(BillActivity.this, BillActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ActiveOrders> call = apiService.getOrderDetails(orderUUid, accountId);
        call.enqueue(new Callback<ActiveOrders>() {
            @Override
            public void onResponse(Call<ActiveOrders> call, Response<ActiveOrders> response) {
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
                try {
                    if (response.code() == 200) {

                        ActiveOrders orderInfo = response.body();
                        if (orderInfo != null) {

                            mprovider = orderInfo.getProviderAccount().getBusinessName();
                            if (orderInfo.getBill() != null) {
                                payStatus = orderInfo.getBill().getBillPaymentStatus();
                            }
                            consumer = orderInfo.getOrderFor().getFirstName() + " " + orderInfo.getOrderFor().getLastName();
                            purpose = Constants.PURPOSE_BILLPAYMENT;
                            uniqueId = String.valueOf(orderInfo.getProviderAccount().getUniqueId());
                            encId = orderInfo.getOrderNumber();
                            bookingStatus = orderInfo.getOrderStatus();
                            // update UI with the data from notification
                            UpdateUI();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ActiveOrders> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });
    }


    boolean showPaytmWallet = false;
    boolean showPayU = false;
    ArrayList<PaymentModel> mPaymentData = new ArrayList<>();
    ArrayList<RefundDetails> refundData = new ArrayList<>();

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


    private void APIRefundInfo(String ynwUUID) {


        ApiInterface apiService = ApiClient.getClient(mCOntext).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(mCOntext, mCOntext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ArrayList<RefundDetails>> call = apiService.getRefundDetails(ynwUUID);

        call.enqueue(new Callback<ArrayList<RefundDetails>>() {
            @Override
            public void onResponse(Call<ArrayList<RefundDetails>> call, Response<ArrayList<RefundDetails>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        refundData = response.body();
                        if (refundData != null) {
                            double totalRefund = 0.0;
                            for (int i = 0; i < refundData.size(); i++) {
                                if (refundData.get(i).getRefundDetails().size() > 0) {
                                    if (refundData.get(i).getRefundDetails().get(0).getStatus().equalsIgnoreCase("Processed") || refundData.get(i).getRefundDetails().get(0).getStatus().equalsIgnoreCase("Pending")) {
                                        refundLayout.setVisibility(View.VISIBLE);
                                        for (int j = 0; j < refundData.get(i).getRefundDetails().size(); j++) {
                                            totalRefund = totalRefund + Double.parseDouble(refundData.get(i).getRefundDetails().get(j).getAmount());
                                        }
                                        tv_refundamount.setText("₹\u00a0" + Config.getAmountinTwoDecimalPoints(totalRefund));
                                        if (total < 0) {
                                            double tRefund = Double.parseDouble(String.valueOf(totalRefund));
                                            tv_totalamt.setText("₹\u00a0" + Config.getAmountinTwoDecimalPoints(tRefund));
                                            btn_pay.setVisibility(View.GONE);
                                        } else if (total >= 0) {
                                            total = mBillData.getNetRate() - mBillData.getTotalAmountPaid() + Double.parseDouble(String.valueOf(totalRefund));
                                            tv_totalamt.setText("₹\u00a0" + Config.getAmountinTwoDecimalPoints(total));
                                            btn_pay.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        refundLayout.setVisibility(View.GONE);
                                    }
                                } else {
//                                    refundLayout.setVisibility(View.GONE);
                                }
                            }
                        }

                    } else {

                        if (response.code() != 419)
                            Toast.makeText(mCOntext, response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<RefundDetails>> call, Throwable t) {
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

        Call<BillModel> call = apiService.getBill(ynwuuid, accountID);

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
//                        APIRefundInfo(ynwUUID);

                        if (mBillData == null) {
                            mBillData = new BillModel();
                        }

                        if (mBillData.getAccountProfile() != null) {

                            tv_provider.setText(mBillData.getAccountProfile().getBusinessName());

                            if (mBillData.getAccountProfile().getProviderBusinessName() != null) {

                                llproviderlayout.setVisibility(View.VISIBLE);
                                tvProviderName.setText(mBillData.getAccountProfile().getProviderBusinessName());
                            } else {

                                llproviderlayout.setVisibility(View.GONE);
                            }
                        }

                        String firstName = SharedPreference.getInstance(mCOntext).getStringValue("firstname", "");
                        String lastNme = SharedPreference.getInstance(mCOntext).getStringValue("lastname", "");
                        tv_customer.setText(consumer);
                        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
                        DateFormat targetFormat = new SimpleDateFormat("hh:mm a");
                        Date date = originalFormat.parse(mBillData.getCreatedDate());
                        String formattedDate = targetFormat.format(date);
                        DateFormat dateTarget = new SimpleDateFormat("yyyy-MM-dd");
                        Date date1 = originalFormat.parse(mBillData.getCreatedDate());
                        String targetDate = dateTarget.format(date1);
                        tv_date.setText(Config.getCustomDateString(targetDate) + " - " + formattedDate);


                        Typeface tyface = Typeface.createFromAsset(getAssets(),
                                "fonts/Montserrat_Bold.otf");
                        tv_gross.setTypeface(tyface);
                        tv_grosstotal.setTypeface(tyface);


                        if (mBillData.getNetTotal() != 0.0) {
                            tv_grosstotal.setVisibility(View.VISIBLE);
                            tv_grosstotal.setText("₹\u00a0" + Config.getAmountinTwoDecimalPoints(mBillData.getNetTotal()));

                        } else {
                            tv_gross.setVisibility(View.GONE);
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
                            tv_amount.setText("₹\u00a0" + Config.getAmountinTwoDecimalPoints(mBillData.getNetRate()));
                        } else {

                            amountlayout.setVisibility(View.GONE);
                        }

                        if (mBillData.getDeliveryCharges() != 0) {
                            deliveryLayout.setVisibility(View.VISIBLE);
                            tv_deliveryCharge.setText("(" + "+" + ")" + "₹\u00a0" + mBillData.getDeliveryCharges());
                        } else {
                            deliveryLayout.setVisibility(View.GONE);
                        }


                        if (mBillData.getTotalAmountPaid() != 0) {
                            paidlayout.setVisibility(View.VISIBLE);
//                            DecimalFormat format = new DecimalFormat("0.00");
                            tv_paid.setText("₹\u00a0" + Config.getAmountinTwoDecimalPoints(mBillData.getTotalAmountPaid()));
                        } else {

                            paidlayout.setVisibility(View.GONE);
                        }

                        if (!bookingStatus.equals("Cancelled")) {
                            txttotal.setVisibility(View.VISIBLE);
                            tv_totalamt.setVisibility(View.VISIBLE);
                            tv_totalamt.setText("₹\u00a0" + Config.getAmountinTwoDecimalPoints(Math.abs(mBillData.getAmountDue())));
                            if (mBillData.getAmountDue() == 0) {
                                btn_pay.setVisibility(View.GONE);
                                couponCheckin.setVisibility(View.GONE);
                                llJCash.setVisibility(View.GONE);
                                cbJCash.setChecked(false);

                            } else if (mBillData.getAmountDue() < 0) {
                                txttotal.setText("Refund Amount");
                                btn_pay.setVisibility(View.GONE);
                                couponCheckin.setVisibility(View.GONE);
                                llJCash.setVisibility(View.GONE);
                                cbJCash.setChecked(false);

                            } else if (mBillData.getAmountDue() > 0) {
                                txttotal.setText("Amount Due");                         //negative amountDue is the refundamound ,there for use getAmountDue() as the refund amount
                                btn_pay.setVisibility(View.VISIBLE);
                                sAmountPay = Config.getAmountinTwoDecimalPoints(Math.abs(mBillData.getAmountDue())); //amount to pay
                                //couponCheckin.setVisibility(View.GONE);  // "couponCheckin" visibility VISIBLE setted at ApiJaldeegetProviderCoupons and ApiJaldeegetS3Coupons methods.
                            }
                        } else {
                            tv_totalamt.setVisibility(View.VISIBLE);
                            txttotal.setVisibility(View.VISIBLE);
                            tv_totalamt.setText("₹\u00a0" + Config.getAmountinTwoDecimalPoints(Math.abs(mBillData.getAmountDue())));  ////negative amountDue is the refundamound ,there for use getAmountDue() as the refund amount
                            if (mBillData.getAmountDue() < 0) {
                                txttotal.setText("Refund Amount");
                            }
                            if (mBillData.getAmountDue() > 0) {
                                txttotal.setText("Amount Due");
                            }
                            btn_pay.setVisibility(View.GONE);
                            couponCheckin.setVisibility(View.GONE);
                            llJCash.setVisibility(View.GONE);
                            cbJCash.setChecked(false);

                        }
                        /*total = mBillData.getNetRate() - mBillData.getTotalAmountPaid() + mBillData.getRefundedAmount();//1111111111111

                        if (total >= 0) {
                            txttotal.setVisibility(View.VISIBLE);
                            tv_totalamt.setVisibility(View.VISIBLE);
//                            DecimalFormat format = new DecimalFormat("0.00");
//                            tv_totalamt.setText("₹ " + String.valueOf(total));
                            tv_totalamt.setText("₹\u00a0" + Config.getAmountinTwoDecimalPoints(total));
                            txttotal.setText("Amount Due");
                        } else if (total < 0) {
                            tv_totalamt.setVisibility(View.VISIBLE);
                            txttotal.setVisibility(View.VISIBLE);
//                            DecimalFormat format = new DecimalFormat("0.00");
                            tv_totalamt.setText("₹\u00a0" + Config.getAmountinTwoDecimalPoints(Math.abs(total)));
                            txttotal.setText("Refund Amount");
                            btn_pay.setVisibility(View.INVISIBLE);
                            couponCheckin.setVisibility(View.INVISIBLE);
                        } else {
                            tv_totalamt.setVisibility(View.INVISIBLE);
                            txttotal.setVisibility(View.INVISIBLE);
//                            DecimalFormat format = new DecimalFormat("0.00");
                            //      tv_totalamt.setText("₹ " + Config.getAmountinTwoDecimalPoints(Math.abs(total)));
                            //
                            //
                            //  txttotal.setText("Refund Amount");
                            btn_pay.setVisibility(View.INVISIBLE);
                            couponCheckin.setVisibility(View.INVISIBLE);
                        }*/

                        //sAmountPay = Config.getAmountinTwoDecimalPoints(total);
//                        Config.logV("Amount PAy@@@@@@@@@@@@@@@@@@@@@@@@" + sAmountPay);

                        if (mBillData.getRefundedAmount() > 0) {

                            refundLayout.setVisibility(View.VISIBLE);
                            tv_refundamount.setText("₹\u00a0" + Config.getAmountinTwoDecimalPoints(mBillData.getRefundedAmount()));
                        } else {

                            refundLayout.setVisibility(View.GONE);
                        }

                       /* if (total != 0.0 && total > 0) {
                            if (payStatus.equalsIgnoreCase("FullyPaid") || payStatus.equalsIgnoreCase("FullyRefunded")) {2222222222222


                                if (mBillData.getAmountDue() > 0) {

                                    btn_pay.setVisibility(View.VISIBLE);
                                } else {
                                    btn_pay.setVisibility(View.GONE);
                                }
//                                btn_pay.setVisibility(View.GONE);
                            } else {
                                btn_pay.setVisibility(View.VISIBLE);
                            }

                        } else {
                            btn_pay.setVisibility(View.INVISIBLE);
                        }*/

                        if (mBillData.getTaxableTotal() > 0) {
                            taxlayout.setVisibility(View.VISIBLE);
                            txttax.setText("Tax " + String.valueOf(mBillData.getTaxPercentage()) + "% of " + "₹\u00a0" + Config.getAmountinTwoDecimalPoints(mBillData.getTaxableTotal()) + "\n" + "(CGST: " + String.valueOf(mBillData.getTaxPercentage() / 2) + " %" + ", SGST: " + String.valueOf(mBillData.getTaxPercentage() / 2) + " %)");
//                        //    txttax.setText("Tax " + String.valueOf(mBillData.getTaxPercentage()) + "% of " + "₹ " + String.valueOf(mBillData.getTaxableTotal()) + "\n" + "(CGST: " + String.valueOf(mBillData.getTaxPercentage() / 2) + " %" + ", SGST: " + String.valueOf(mBillData.getTaxPercentage() / 2) + " %)");


//                            txtaxval.setText("(+)₹ " + String.valueOf(mBillData.getTotalTaxAmount()));
                            txtaxval.setText("(+)₹\u00a0" + Config.getAmountinTwoDecimalPoints(mBillData.getTotalTaxAmount()));
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
                        if (mBillData.getProviderCoupon() != null) {
                            if (mBillData.getProviderCoupon().size() > 0 && mBillData.getProviderCoupon().size() == 1) {
                                pcLayout.setVisibility(View.VISIBLE);
                                tv_providerCouponLabel.setText("Provider Coupon");
                                tv_providerCouponLabel.setVisibility(View.VISIBLE);
                            }
                            if (mBillData.getProviderCoupon().size() > 1) {
                                pcLayout.setVisibility(View.VISIBLE);
                                tv_providerCouponLabel.setText("Provider Coupons");
                                tv_providerCouponLabel.setVisibility(View.VISIBLE);
                            }
                            RecyclerView.LayoutManager cLayoutManager = new LinearLayoutManager(mCOntext);
                            proCoupon_added.setLayoutManager(cLayoutManager);
                            billProCouponAdapter = new BillCouponAdapter(mBillData.getProviderCoupon());
                            proCoupon_added.setAdapter(billProCouponAdapter);
                            billProCouponAdapter.notifyDataSetChanged();
                        } else {
                            pcLayout.setVisibility(View.GONE);
                        }

                        if (mBillData.getJdn() != null) {
                            jdnLayout.setVisibility(View.VISIBLE);
                            jdnLabel.setText("JDN");
                            jdnLabel.setVisibility(View.VISIBLE);
                            jdnValue.setText(("(-)₹\u00a0" + Config.getAmountinTwoDecimalPoints(mBillData.getJdn().getDiscount())));
                            jdnValue.setVisibility(View.VISIBLE);
                        } else {
                            jdnLayout.setVisibility(View.GONE);
                            jdnLabel.setVisibility(View.GONE);
                            jdnValue.setVisibility(View.GONE);
                        }

                        try {

                            if (mBillData.getDisplayNotes() != null && mBillData.getDisplayNotes().getDisplayNotes() != null) {
                                tv_billnotes.setVisibility(View.VISIBLE);
                                tv_notes.setVisibility(View.VISIBLE);
                                tv_notes.setText(mBillData.getDisplayNotes().getDisplayNotes());
                            } else {
                                tv_billnotes.setVisibility(View.GONE);
                                tv_notes.setVisibility(View.GONE);

                            }
                        } catch (Exception e) {
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
                        /*coupanArrayList = response.body().getProviderCoupon();

                        if (discountArrayList != null || coupanArrayList != null) {*/
                        if (discountArrayList != null || coupanArrayList != null) {
                            discountArrayList.addAll(coupanArrayList);

                            RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(mCOntext);
                            recycle_discount_total.setLayoutManager(mLayoutManager1);

                            if (discountArrayList.size() > 0) {

                                recycle_discount_total.setVisibility(View.VISIBLE);
                                billDiscountAdapter = new BIllDiscountAdapter("totalbill", discountArrayList, mCOntext);
                                //////////////
                                recycle_discount_total.setAdapter(billDiscountAdapter);
                                billDiscountAdapter.notifyDataSetChanged();
                            }

                        } else {
                            recycle_discount_total.setVisibility(View.GONE);
                        }
                        if (discountArrayList != null) {
                            RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(mCOntext);
                            recycle_display_notes.setLayoutManager(mLayoutManager1);
                            if (discountArrayList.size() > 0) {

                                recycle_display_notes.setVisibility(View.VISIBLE);
                                billDemandDisplayNotesAdapter = new BillDemandDisplayNotesAdapter(discountArrayList, mCOntext, mBillData);
                                ////////////
                                recycle_display_notes.setAdapter(billDemandDisplayNotesAdapter);
                                billDemandDisplayNotesAdapter.notifyDataSetChanged();
                            }

                        } else {
                            recycle_display_notes.setVisibility(View.GONE);
                        }
                        if (mBillData.getTotalAmountSaved() > 0 && !bookingStatus.equals("Cancelled")) {
                            if (payStatus.equals("FullyPaid") && mBillData.getAmountDue() == 0) {
                                ll_amount_Saved.setVisibility(View.VISIBLE);
                                tv_amount_Saved.setText("You have saved ₹\u00a0" + Config.getAmountinTwoDecimalPoints(mBillData.getTotalAmountSaved()));
                            } else {
                                ll_amount_Saved.setVisibility(View.VISIBLE);
                                tv_amount_Saved.setText("You will save ₹\u00a0" + Config.getAmountinTwoDecimalPoints(mBillData.getTotalAmountSaved()));
                            }
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
                        if ((s3couponList.size() > 0 || providerCouponList.size() > 0) && !bookingStatus.equals("Cancelled") && mBillData.getAmountDue() > 0) { //bookingStatus.equals("Cancelled") is seted for if cancelled bookings not need to visible the couponCheckin
                            couponCheckin.setVisibility(View.VISIBLE);
                        } else {
                            couponCheckin.setVisibility(View.GONE);
                        }
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

    private void ApiJaldeegetProviderCoupons(String uniqueID) {
        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mActivity).create(ApiInterface.class);
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));
        Call<ArrayList<ProviderCouponResponse>> call = apiService.getProviderCoupanList(Integer.parseInt(uniqueID), sdf.format(currentTime));
        call.enqueue(new Callback<ArrayList<ProviderCouponResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<ProviderCouponResponse>> call, Response<ArrayList<ProviderCouponResponse>> response) {
                try {
                    if (response.code() == 200) {
                        providerCouponList.clear();
                        providerCouponList = response.body();
                        if ((s3couponList.size() > 0 || providerCouponList.size() > 0) && !bookingStatus.equals("Cancelled") && mBillData.getAmountDue() > 0) {//bookingStatus.equals("Cancelled") is seted for if cancelled bookings not need to visible the couponCheckin
                            couponCheckin.setVisibility(View.VISIBLE);
                        } else {
                            couponCheckin.setVisibility(View.GONE);
                        }
                        Log.i("ProviderCouponResponse", providerCouponList.toString());


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ProviderCouponResponse>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());

            }
        });
    }

    private void APIGetWalletEligibleJCash() {
        final ApiInterface apiService =
                ApiClient.getClient(this).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(this, this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<WalletEligibleJCash> call = apiService.getWalletEligibleJCash();

        Config.logV("Request--GetWalletEligibleJCash-------------------------");
        call.enqueue(new Callback<WalletEligibleJCash>() {
            @Override
            public void onResponse(Call<WalletEligibleJCash> call, Response<WalletEligibleJCash> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(mActivity, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
//                    Config.logV("zxczqw" + new Gson().toJson(response.body()));

                    if (response.code() == 200) {
                        walletEligibleJCash = response.body();
                        if (walletEligibleJCash != null) {
                            if (walletEligibleJCash.getjCashAmt() > 0) {
                                llJCash.setVisibility(View.VISIBLE);
                                cbJCash.setChecked(true);
                                cbJCash.append(String.valueOf(walletEligibleJCash.getjCashAmt()));
                            } else {
                                llJCash.setVisibility(View.GONE);
                                cbJCash.setChecked(false);

                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<WalletEligibleJCash> call, Throwable t) {

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
        if (ynwUUID.endsWith("_odr")) {
            redirectToOrderTab();  // for in order payment -- after payment redirect to myorder.java tablayout
        } else {
            Intent intent = new Intent(mCOntext, Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
        finish();
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID, PaymentData paymentData) {
        Log.i("mani", "here");
        try {
            RazorpayModel razorpayModel = new RazorpayModel(paymentData);
            new PaymentGateway(mCOntext, mActivity).sendPaymentStatus(razorpayModel, "SUCCESS");
            Toast.makeText(mCOntext, "Payment Successful", Toast.LENGTH_LONG).show();
            paymentFinished(razorpayModel);

        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int code, String response, PaymentData paymentData) {
        try {
            Toast.makeText(mCOntext, "Payment failed ", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentError..", e);
        }
    }

    @Override
    public void sendPaymentResponse() {

        //Paytm
        Toast.makeText(BillActivity.this, "Payment Successful", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBackPressed() {

        if (fromPushNotification) {
            Intent intent = new Intent(BillActivity.this, Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            super.onBackPressed();
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void redirectToOrderTab() {
        Intent homeIntent = new Intent(BillActivity.this, Home.class);
        homeIntent.putExtra("isOrder", "ORDER");
        startActivity(homeIntent);
    }
}
