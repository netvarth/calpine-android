package com.jaldeeinc.jaldee.activities;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.CouponAdapter;
import com.jaldeeinc.jaldee.adapter.CouponsAdapter;
import com.jaldeeinc.jaldee.adapter.ProviderCouponsAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.response.CoupnResponse;
import com.jaldeeinc.jaldee.response.Provider;
import com.jaldeeinc.jaldee.response.ProviderCouponResponse;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CouponActivity extends AppCompatActivity {

    RecyclerView rvProviderCoupons;
    List<CoupnResponse> coupanList;
    RecyclerView rvCoupons;
    String uniqueid, accountId;
    private CouponsAdapter mAdapter;
    CustomTextViewSemiBold tvError;
    private ProviderCouponsAdapter providerCouponsAdapter;
    ArrayList<ProviderCouponResponse> providerCouponList = new ArrayList<>();
    private Provider providerResponse = new Provider();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.couponlist);
        rvCoupons = findViewById(R.id.coupon_inner_list);
        rvProviderCoupons = findViewById(R.id.rv_providerCoupons);
        tvError = findViewById(R.id.tv_error);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            uniqueid = bundle.getString("uniqueID", "");
            accountId = bundle.getString("accountId", null);
            ApiJaldeeCoupan(uniqueid);
        }

        ImageView iBackPress = findViewById(R.id.backpress);
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                finish();
            }
        });


        TextView tv_title = findViewById(R.id.toolbartitle);
        Typeface tyface1 = Typeface.createFromAsset(CouponActivity.this.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface1);
        tv_title.setText(R.string.couponss);


    }

    private void ApiJaldeeCoupan(String uniqueID) {

        ApiInterface apiService =
                ApiClient.getClient(CouponActivity.this).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(CouponActivity.this, CouponActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<Provider> call = apiService.getCoupons(Integer.parseInt(uniqueID));
        call.enqueue(new Callback<Provider>() {
            @Override
            public void onResponse(@NonNull Call<Provider> call, Response<Provider> response) {
                coupanList = new ArrayList<>();
                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(CouponActivity.this, mDialog);

                    if (response.code() == 200) {
                        providerResponse = response.body();

                        if (providerResponse != null) {

                            if (providerResponse.getCoupon() != null) {
                                coupanList.clear();
                                coupanList = new Gson().fromJson(providerResponse.getCoupon(), new TypeToken<ArrayList<CoupnResponse>>() {
                                }.getType());

                                if (coupanList != null && coupanList.size() > 0) {
                                    ApiJaldeegetProviderCoupons(providerResponse.getProviderCoupon());
                                    tvError.setVisibility(View.GONE);
                                    rvCoupons.setLayoutManager(new LinearLayoutManager(CouponActivity.this));
                                    mAdapter = new CouponsAdapter(CouponActivity.this, coupanList);
                                    rvCoupons.setAdapter(mAdapter);
                                } else {
                                    tvError.setVisibility(View.VISIBLE);
                                    ApiJaldeegetProviderCoupons(providerResponse.getProviderCoupon());
                                }
                            } else {
                                tvError.setVisibility(View.VISIBLE);
                                ApiJaldeegetProviderCoupons(providerResponse.getProviderCoupon());
                            }

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(@NonNull Call<Provider> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(CouponActivity.this, mDialog);

            }
        });


    }

    private void ApiJaldeegetProviderCoupons(String providerCoupons) {

        try {

            providerCouponList.clear();
            providerCouponList = new Gson().fromJson(providerCoupons, new TypeToken<ArrayList<ProviderCouponResponse>>() {
            }.getType());
            if (providerCouponList != null) {
                if ((coupanList != null && coupanList.size() > 0)||providerCouponList.size() > 0) {
                    tvError.setVisibility(View.GONE);
                    rvProviderCoupons.setLayoutManager(new LinearLayoutManager(CouponActivity.this));
                    providerCouponsAdapter = new ProviderCouponsAdapter(providerCouponList, CouponActivity.this, accountId);
                    rvProviderCoupons.setAdapter(providerCouponsAdapter);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
