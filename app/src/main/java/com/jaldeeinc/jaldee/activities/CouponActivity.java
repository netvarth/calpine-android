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

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.CouponAdapter;
import com.jaldeeinc.jaldee.adapter.CouponsAdapter;
import com.jaldeeinc.jaldee.adapter.ProviderCouponsAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.response.CoupnResponse;
import com.jaldeeinc.jaldee.response.ProviderCouponResponse;

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
    String uniqueid;
    private CouponsAdapter mAdapter;
    CustomTextViewSemiBold tvError;
    private ProviderCouponsAdapter providerCouponsAdapter;
    ArrayList<ProviderCouponResponse> providerCouponList = new ArrayList<>();


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
            ApiJaldeeCoupan(uniqueid);
            ApiJaldeegetProviderCoupons(uniqueid);

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
                ApiClient.getClientS3Cloud(CouponActivity.this).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(CouponActivity.this, CouponActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));


        Call<ArrayList<CoupnResponse>> call = apiService.getCoupanList(Integer.parseInt(uniqueID), sdf.format(currentTime));

        call.enqueue(new Callback<ArrayList<CoupnResponse>>() {


            @Override
            public void onResponse(@NonNull Call<ArrayList<CoupnResponse>> call, Response<ArrayList<CoupnResponse>> response) {
                coupanList = new ArrayList<>();
                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(CouponActivity.this, mDialog);

                    Config.logV("URL-response--------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        coupanList = response.body();
                        if (coupanList != null) {
                            tvError.setVisibility(View.GONE);
                            rvCoupons.setLayoutManager(new LinearLayoutManager(CouponActivity.this));
                            mAdapter = new CouponsAdapter(CouponActivity.this, coupanList);
                            rvCoupons.setAdapter(mAdapter);
                        } else {

                            tvError.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tvError.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<CoupnResponse>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(CouponActivity.this, mDialog);

            }
        });


    }

    private void ApiJaldeegetProviderCoupons(String uniqueID) {
        ApiInterface apiService =
                ApiClient.getClientS3Cloud(CouponActivity.this).create(ApiInterface.class);
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
                        if (response.body() != null) {
                            providerCouponList = response.body();
                            if (providerCouponList.size() > 0) {
                                tvError.setVisibility(View.GONE);
                                rvProviderCoupons.setLayoutManager(new LinearLayoutManager(CouponActivity.this));
                                providerCouponsAdapter = new ProviderCouponsAdapter(providerCouponList, CouponActivity.this, uniqueID);
                                rvProviderCoupons.setAdapter(providerCouponsAdapter);
                            } else {

                                tvError.setVisibility(View.VISIBLE);
                            }
                        } else {

                            tvError.setVisibility(View.VISIBLE);
                        }

                    } else {
                        tvError.setVisibility(View.VISIBLE);
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
}
