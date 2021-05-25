package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.CouponlistAdapter;
import com.jaldeeinc.jaldee.adapter.JCashListAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.response.ActiveOrders;
import com.jaldeeinc.jaldee.response.JCashAvailable;
import com.jaldeeinc.jaldee.response.JCashInfo;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JaldeeCashActivity extends AppCompatActivity {
    @BindView(R.id.cv_back)
    CardView cvBack;
    @BindView(R.id.tv_jcash)
    CustomTextViewBold tvJcash;
    @BindView(R.id.tv_totCashAwarded)
    CustomTextViewSemiBold tvTotCashAwarded;
    @BindView(R.id.tv_totCashSpent)
    CustomTextViewSemiBold tvTotCashSpent;
    RecyclerView list;
    private JCashListAdapter mAdapter;
    ArrayList<JCashAvailable> listJCashAvailable = new ArrayList<JCashAvailable>();
    static Activity mActivity;
    static Context mContext;

    public String totCashAwarded;
    public String totCashSpent;
    public String totCashAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jaldee_cash);
        ButterKnife.bind(JaldeeCashActivity.this);

        mActivity = this;
        mContext = this;

        Intent intent = getIntent();
        totCashAwarded = intent.getStringExtra("totCashAwarded");
        totCashSpent = intent.getStringExtra("totCashSpent");
        totCashAvailable = intent.getStringExtra("totCashAvailable");

        list = findViewById(R.id.list);

        tvJcash.setText(Config.getAmountinTwoDecimalPoints(Double.parseDouble(totCashAvailable)));
        tvTotCashAwarded.setText(Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(totCashAwarded)));
        tvTotCashSpent.setText(Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(totCashSpent)));

        ApiGetJCashAvailable();
        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }

    private void ApiGetJCashAvailable() {

        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ArrayList<JCashAvailable>> call = apiService.getJCashAvailable();
        call.enqueue(new Callback<ArrayList<JCashAvailable>>() {
            @Override
            public void onResponse(Call<ArrayList<JCashAvailable>> call, Response<ArrayList<JCashAvailable>> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(JaldeeCashActivity.this, mDialog);
                    if (response.code() == 200) {
                        listJCashAvailable = response.body();
                        Config.logV("Jaldee Cash list--code-------------------------" + listJCashAvailable);
                        list.setVisibility(View.VISIBLE);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(JaldeeCashActivity.this);
                        list.setLayoutManager(mLayoutManager);
                        mAdapter = new JCashListAdapter(JaldeeCashActivity.this, listJCashAvailable);
                        list.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<JCashAvailable>> call, Throwable t) {


            }
        });
    }
}