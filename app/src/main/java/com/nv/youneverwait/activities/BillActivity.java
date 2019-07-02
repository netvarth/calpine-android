package com.nv.youneverwait.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nv.youneverwait.R;
import com.nv.youneverwait.adapter.BIllDiscountAdapter;
import com.nv.youneverwait.adapter.BillCouponAdapter;
import com.nv.youneverwait.adapter.BillServiceAdapter;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.model.BillModel;
import com.nv.youneverwait.model.CheckSumModelTest;
import com.nv.youneverwait.payment.PayUMoneyWebview;
import com.nv.youneverwait.payment.PaymentGateway;
import com.nv.youneverwait.payment.PaytmPayment;
import com.nv.youneverwait.response.CheckSumModel;
import com.nv.youneverwait.response.PaymentModel;
import com.nv.youneverwait.utils.SharedPreference;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 1/10/18.
 */

public class BillActivity extends AppCompatActivity {

    static Context mCOntext;
    static Activity mActivity;

    String ynwUUID, mprovider;
    TextView tv_provider, tv_customer, tv_date, tv_gstn, tv_bill;
    EditText mbill_coupon_edit;
    BillModel mBillData;
    TextView tv_paid, tv_totalamt;
    RecyclerView recycle_item, recycle_discount_total, coupon_added;
    BillServiceAdapter billServiceAdapter;
    BillCouponAdapter billCouponAdapter;
    ArrayList<BillModel> serviceArrayList = new ArrayList<>();
    ArrayList<BillModel> itemArrayList = new ArrayList<>();
    ArrayList<BillModel> serviceItemArrayList = new ArrayList<>();


    ArrayList<BillModel> discountArrayList = new ArrayList<>();
    ArrayList<BillModel> coupanArrayList = new ArrayList<>();

    Button btn_pay, mbill_applybtn;
    TextView txtnetRate, txttotal, tv_amount, tv_grosstotal, tv_gross, txtaxval, txttax;
    LinearLayout paidlayout, amountlayout, taxlayout, couponCheckin;
    String sAmountPay;
    String accountID;
    String payStatus, consumer;
    String coupon_entered;

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
        tv_gstn = findViewById(R.id.txtgstn);
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
        recycle_discount_total = findViewById(R.id.recycle_discount_total);
        TextView tv_title = findViewById(R.id.toolbartitle);
        Typeface tyface = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface);
        txtnetRate = findViewById(R.id.txtnetRate);
        txttotal = findViewById(R.id.txttotal);

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
                if(mbill_coupon_edit.getText().toString().equals("")){
                    Toast.makeText(BillActivity.this, "Enter a coupon", Toast.LENGTH_SHORT).show();
                }else {
                    coupon_entered = mbill_coupon_edit.getText().toString();
                    ApigetBill(coupon_entered, ynwUUID, accountID);
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
                        txtamt.setText("Rs." + sAmountPay);
                        Typeface tyface1 = Typeface.createFromAsset(mCOntext.getAssets(),
                                "fonts/Montserrat_Bold.otf");
                        txtamt.setTypeface(tyface1);
                        btn_payu.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                new PaymentGateway(mCOntext, mActivity).ApiGenerateHash1(ynwUUID, sAmountPay, accountID, "bill");

                                dialog.dismiss();
                            }
                        });

                        btn_paytm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                PaytmPayment payment = new PaytmPayment(mCOntext);
                                payment.ApiGenerateHashPaytm(ynwUUID, sAmountPay, accountID, mCOntext, mActivity, "");
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


        Call<ArrayList<PaymentModel>> call = apiService.getPayment(accountID);

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

    private void ApiBill(String ynwuuid) {


        final ApiInterface apiService =
                ApiClient.getClient(this).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(this, this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Call<BillModel> call = apiService.getBill(ynwuuid);

        Config.logV("Request--ynwuuid1-------------------------" + ynwuuid);

        call.enqueue(new Callback<BillModel>() {
            @Override
            public void onResponse(Call<BillModel> call, Response<BillModel> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(mActivity, mDialog);

                    Config.logV("URL12---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code12-------------------------" + response.code());
                    Config.logV("Response--code12-------------------------" + new Gson().toJson(response.body()));

                    if (response.code() == 200) {

                        Config.logV("Response--Array size--Active-----------------------" + new Gson().toJson(response.body().toString()));
                        mBillData = response.body();

                        String firstName = SharedPreference.getInstance(mCOntext).getStringValue("firstname", "");
                        String lastNme = SharedPreference.getInstance(mCOntext).getStringValue("lastname", "");
                        tv_customer.setText(consumer);
                        // tv_date.setText(mBillData.getCreatedDate());
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
                            tv_grosstotal.setText("₹ " + String.valueOf(mBillData.getNetTotal()));

                        } else {
                            tv_grosstotal.setVisibility(View.GONE);
                        }


                        if (mBillData.getGstNumber() != null) {
                            tv_gstn.setText(mBillData.getGstNumber());
                        }

                        tv_bill.setText(String.valueOf(mBillData.getId()));


                        if (mBillData.getNetRate() != 0) {

                            amountlayout.setVisibility(View.VISIBLE);
                            tv_amount.setText("₹ " + String.valueOf(mBillData.getNetRate()));
                        } else {

                            amountlayout.setVisibility(View.GONE);
                        }

                        if (mBillData.getTotalAmountPaid() != 0) {
                            paidlayout.setVisibility(View.VISIBLE);
                            tv_paid.setText("₹ " + String.valueOf(mBillData.getTotalAmountPaid()));
                        } else {

                            paidlayout.setVisibility(View.GONE);
                        }


                        double total = mBillData.getNetRate() - mBillData.getTotalAmountPaid();

                        if (total != 0 && total > 0) {
                            txttotal.setVisibility(View.VISIBLE);
                            tv_totalamt.setVisibility(View.VISIBLE);
                            tv_totalamt.setText("₹ " + String.valueOf(total));
                            txttotal.setText("Amount Due");
                        } else {
                            tv_totalamt.setVisibility(View.VISIBLE);
                            txttotal.setVisibility(View.VISIBLE);
                            tv_totalamt.setText("₹ " + Math.abs(total));
                            txttotal.setText("Refundable amount");
                            btn_pay.setVisibility(View.INVISIBLE);
                            couponCheckin.setVisibility(View.INVISIBLE);
                        }


                        sAmountPay = String.valueOf(total);
                        Config.logV("Amount PAy@@@@@@@@@@@@@@@@@@@@@@@@" + sAmountPay);

                        if (total != 0.0 && total > 0) {
                            btn_pay.setVisibility(View.VISIBLE);
                        } else {
                            btn_pay.setVisibility(View.INVISIBLE);
                        }

                        if (mBillData.getTaxableTotal() > 0) {
                            taxlayout.setVisibility(View.VISIBLE);
                            txttax.setText("Tax " + String.valueOf(mBillData.getTaxPercentage()) + "% of " + "₹ " + String.valueOf(mBillData.getTaxableTotal()) + "\n" + "(CGST: " + String.valueOf(mBillData.getTaxPercentage() / 2) + " %" + ", SGST: " + String.valueOf(mBillData.getTaxPercentage() / 2) + " %)");

                            txtaxval.setText("(+)₹ " + String.valueOf(mBillData.getTotalTaxAmount()));
                        } else {
                            taxlayout.setVisibility(View.GONE);
                        }


                        if (mBillData.getJCoupon() != null) {
                            RecyclerView.LayoutManager cLayoutManager = new LinearLayoutManager(mCOntext);
                            coupon_added.setLayoutManager(cLayoutManager);
                            billCouponAdapter = new BillCouponAdapter(mBillData.getJCoupon());
                            coupon_added.setAdapter(billCouponAdapter);
                            billCouponAdapter.notifyDataSetChanged();
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
                    Config.logV("zxczqw" + new Gson().toJson(response.body()));

                    if (response.code() == 200) {


                        finish();
                        startActivity(getIntent());

                        Toast.makeText(BillActivity.this, couponss+" "+"Coupon Added", Toast.LENGTH_SHORT).show();

                        Config.logV("Response--Array size--Activessssssssss-----------------------" + response.body().toString());
                        Config.logV("zxczxczxzc" + new Gson().toJson(response.body()));
                    }

                    if(response.code() == 422){
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


}
