package com.jaldeeinc.jaldee.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.JCashAvailableListAdapter;
import com.jaldeeinc.jaldee.adapter.JCashExpiredListAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.JCashSpentLogDialog;
import com.jaldeeinc.jaldee.response.JCash;
import com.jaldeeinc.jaldee.response.JCashExpired;
import com.jaldeeinc.jaldee.response.JCashSpentDetails;

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

    @BindView(R.id.ll_totCashSpentLog)
    LinearLayout ll_totCashSpentLog;

    @BindView(R.id.list)
    RecyclerView list;

    @BindView(R.id.ll_jCash_expired_list_hint)
    LinearLayout ll_jCash_expired_list_hint;

    @BindView(R.id.iv_expired_jCash_list_hint)
    ImageView iv_expired_jCash_list_hint;

    @BindView(R.id.rv_expired_jCash_list)
    RecyclerView rv_expired_jCash_list;

    private JCashAvailableListAdapter jCashAvailableListAdapter;
    private JCashExpiredListAdapter jCashExpiredListAdapter;
    ArrayList<JCash> jCashAvailable = new ArrayList<JCash>();
    ArrayList<JCashExpired> jCashExpired = new ArrayList<JCashExpired>();
    ArrayList<JCashSpentDetails> listJCashSpentDetails = new ArrayList<JCashSpentDetails>();
    static Activity mActivity;
    static Context mContext;
    private JCashSpentLogDialog jCashSpentLogDialog;
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

        tvJcash.setText("₹\u00a0"+Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(totCashAvailable)));
        tvTotCashAwarded.setText(Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(totCashAwarded)));
        tvTotCashSpent.setText(Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(totCashSpent)));

        ApiGetJCashAvailable();
        ApiGetExpiredJCash();
        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        ll_jCash_expired_list_hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rv_expired_jCash_list.getVisibility() == View.VISIBLE) {
                    rv_expired_jCash_list.setVisibility(View.GONE);
                    iv_expired_jCash_list_hint.setImageResource(R.drawable.icon_down_arrow_blue);
                } else if (rv_expired_jCash_list.getVisibility() == View.GONE) {
                    rv_expired_jCash_list.setVisibility(View.VISIBLE);
                    iv_expired_jCash_list_hint.setImageResource(R.drawable.icon_up_arrow_blue);
                }
            }
        });
    }

    private void ApiGetJCashAvailable() {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ArrayList<JCash>> call = apiService.getJCashAvailable();
        call.enqueue(new Callback<ArrayList<JCash>>() {
            @Override
            public void onResponse(Call<ArrayList<JCash>> call, Response<ArrayList<JCash>> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(JaldeeCashActivity.this, mDialog);
                    if (response.code() == 200) {
                        jCashAvailable = response.body();
                        if(jCashAvailable != null && !jCashAvailable.isEmpty() && jCashAvailable.size() > 0) {
                            Config.logV("Jaldee Cash list--code-------------------------" + jCashAvailable);
                            list.setVisibility(View.VISIBLE);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(JaldeeCashActivity.this);
                            list.setLayoutManager(mLayoutManager);
                            jCashAvailableListAdapter = new JCashAvailableListAdapter(JaldeeCashActivity.this, jCashAvailable);
                            list.setAdapter(jCashAvailableListAdapter);
                            jCashAvailableListAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ArrayList<JCash>> call, Throwable t) {
            }
        });
    }

    private void ApiGetExpiredJCash() {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ArrayList<JCashExpired>> call = apiService.getJCashExpired();
        call.enqueue(new Callback<ArrayList<JCashExpired>>() {
            @Override
            public void onResponse(Call<ArrayList<JCashExpired>> call, Response<ArrayList<JCashExpired>> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(JaldeeCashActivity.this, mDialog);
                    if (response.code() == 200) {
                        jCashExpired = response.body();
                        if(jCashExpired != null && !jCashExpired.isEmpty() && jCashExpired.size() > 0){
                            ll_jCash_expired_list_hint.setVisibility(View.VISIBLE);
                            iv_expired_jCash_list_hint.setImageResource(R.drawable.icon_down_arrow_blue);
                            Config.logV("Jaldee Cash Expired list--code-------------------------" + jCashExpired);
                            rv_expired_jCash_list.setVisibility(View.VISIBLE);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(JaldeeCashActivity.this);
                            rv_expired_jCash_list.setLayoutManager(mLayoutManager);
                            jCashExpiredListAdapter = new JCashExpiredListAdapter(JaldeeCashActivity.this, jCashExpired);
                            rv_expired_jCash_list.setAdapter(jCashExpiredListAdapter);
                            rv_expired_jCash_list.setVisibility(View.GONE);
                            jCashExpiredListAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ArrayList<JCashExpired>> call, Throwable t) {
            }
        });
    }


    private void ApiGetAllJcashSpentDetails() {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, "");
        mDialog.show();

        Call<ArrayList<JCashSpentDetails>> call = apiService.getAllJCashSpentDetails();
        call.enqueue(new Callback<ArrayList<JCashSpentDetails>>() {
            @Override
            public void onResponse(Call<ArrayList<JCashSpentDetails>> call, Response<ArrayList<JCashSpentDetails>> response) {
                try {
                    if (mDialog.isShowing()) {
                        Config.closeDialog((Activity) mContext, mDialog);
                    }
                    if (response.code() == 200) {
                        listJCashSpentDetails = response.body();
                        Config.logV("Jaldee Cash Spent details--code-------------------------" + listJCashSpentDetails);
                        jCashSpentLogDialog = new JCashSpentLogDialog(mContext, listJCashSpentDetails);
                        jCashSpentLogDialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogStyle_Default;
                        jCashSpentLogDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        jCashSpentLogDialog.show();
                        DisplayMetrics metrics = JaldeeCashActivity.this.getResources().getDisplayMetrics();
                        int width = (int) (metrics.widthPixels * 1);
                        jCashSpentLogDialog.setCancelable(false);
                        jCashSpentLogDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<JCashSpentDetails>> call, Throwable t) {

            }
        });

    }
}