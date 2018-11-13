package com.nv.youneverwait.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nv.youneverwait.R;
import com.nv.youneverwait.adapter.BillServiceAdapter;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.model.BillModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 1/10/18.
 */

public class BillActivity extends AppCompatActivity {

    Context mCOntext;
    Activity mActivity;

    String ynwUUID, mprovider;
    TextView tv_provider, tv_customer, tv_date, tv_gstn, tv_bill;
    BillModel mBillData;
    TextView tv_coupan, tv_discount, tv_amount, tv_paid, tv_totalamt;
    RecyclerView recycle_item;
    BillServiceAdapter billServiceAdapter;
    ArrayList<BillModel> serviceArrayList = new ArrayList<>();
    ArrayList<BillModel> itemArrayList = new ArrayList<>();
    ArrayList<BillModel> serviceItemArrayList = new ArrayList<>();

    Button btn_cancel, btn_pay;
TextView txtnetRate,txttotal;
LinearLayout discountlayout,paidlayout,coupanlayout,amountlayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bill);
        mCOntext = this;
        mActivity = this;

        discountlayout = (LinearLayout) findViewById(R.id.discountlayout);
        paidlayout = (LinearLayout) findViewById(R.id.paidlayout);
        coupanlayout = (LinearLayout) findViewById(R.id.coupanlayout);
        amountlayout = (LinearLayout) findViewById(R.id.amountlayout);


        tv_provider = (TextView) findViewById(R.id.provider);
        tv_customer = (TextView) findViewById(R.id.txtcustomer);
        tv_date = (TextView) findViewById(R.id.txtdate);
        tv_bill = (TextView) findViewById(R.id.txtbill);
        tv_gstn = (TextView) findViewById(R.id.txtgstn);

        tv_coupan = (TextView) findViewById(R.id.txtcoupan);
        tv_discount = (TextView) findViewById(R.id.txtdiscount);
        tv_amount = (TextView) findViewById(R.id.txtamt);
        tv_paid = (TextView) findViewById(R.id.amtpaid);
        tv_totalamt = (TextView) findViewById(R.id.totalamt);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_pay = (Button) findViewById(R.id.btn_pay);

        recycle_item = (RecyclerView) findViewById(R.id.recycle_item);

        TextView tv_title = (TextView)findViewById(R.id.toolbartitle);
        tv_title.setText("Bill");

        Typeface tyface = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface);

        txtnetRate=(TextView) findViewById(R.id.txtnetRate);
        txttotal=(TextView) findViewById(R.id.txttotal);

        tv_totalamt.setTypeface(tyface);
        txttotal.setTypeface(tyface);
        txtnetRate.setTypeface(tyface);


        ImageView iBackPress=(ImageView)findViewById(R.id.backpress) ;
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
        }

        ApiBill(ynwUUID);

        Typeface tyface1 = Typeface.createFromAsset(this.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_provider.setTypeface(tyface1);
        tv_provider.setText(mprovider);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    private void ApiBill(String ynwuuid) {


        final ApiInterface apiService =
                ApiClient.getClient(this).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(this, this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Call<BillModel> call = apiService.getBill(ynwuuid);

        Config.logV("Request--ynwuuid-------------------------" + ynwuuid);

        call.enqueue(new Callback<BillModel>() {
            @Override
            public void onResponse(Call<BillModel> call, Response<BillModel> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(mActivity, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        Config.logV("Response--Array size--Active-----------------------" + response.body().toString());
                        mBillData = response.body();

                        if (mBillData.getCustomer().getUserProfile() != null) {
                            tv_customer.setText(mBillData.getCustomer().getUserProfile().getFirstName());
                            tv_date.setText(mBillData.getCreatedDate());
                        }

                        if (mBillData.getGstNumber() != null) {
                            tv_gstn.setText(mBillData.getGstNumber());

                        }

                        tv_bill.setText(String.valueOf(mBillData.getId()));


                        if(mBillData.getDiscountValue()!=0){
                            discountlayout.setVisibility(View.VISIBLE);
                            tv_discount.setText("₹ " +String.valueOf(mBillData.getDiscountValue()) );
                        }else{

                            discountlayout.setVisibility(View.GONE);
                        }

                        if(mBillData.getCouponValue()!=0){
                            coupanlayout.setVisibility(View.VISIBLE);
                            tv_coupan.setText("₹ " + String.valueOf(mBillData.getCouponValue()));
                        }else{

                            coupanlayout.setVisibility(View.GONE);
                        }


                        if(mBillData.getNetRate()!=0){

                            amountlayout.setVisibility(View.VISIBLE);
                            tv_amount.setText("₹ " + String.valueOf(mBillData.getNetRate()));
                        }else{

                            amountlayout.setVisibility(View.GONE);
                        }

                        if(mBillData.getTotalAmountPaid()!=0){
                            paidlayout.setVisibility(View.VISIBLE);
                            tv_paid.setText("₹ " + String.valueOf(mBillData.getTotalAmountPaid()));
                        }else{

                            paidlayout.setVisibility(View.GONE);
                        }



                        double total=mBillData.getNetRate()-mBillData.getTotalAmountPaid();
                        tv_totalamt.setText("₹ " +String.valueOf(total));



                        serviceArrayList = response.body().getService();
                        itemArrayList=response.body().getItems();

                        serviceArrayList.addAll(itemArrayList);
                        Config.logV("Sevice ArrayList-------------------" + serviceArrayList.size());
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mCOntext);
                        recycle_item.setLayoutManager(mLayoutManager);
                        billServiceAdapter = new BillServiceAdapter(serviceArrayList, mCOntext);
                        recycle_item.setAdapter(billServiceAdapter);
                        billServiceAdapter.notifyDataSetChanged();


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
