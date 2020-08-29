package com.jaldeeinc.jaldee.activities;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.CouponFirstAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.response.CoupnResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CouponFirstActivity extends AppCompatActivity {

    List<CoupnResponse> coupanList;
    ListView coupon_listview;
    String uniqueid;
    private CouponFirstAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.couponlist);
        coupon_listview = findViewById(R.id.coupon_inner_list);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            uniqueid = bundle.getString("uniqueID", "");
            ApiJaldeeCoupan(uniqueid);

        }

        ImageView iBackPress= findViewById(R.id.backpress);
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                finish();
            }
        });


        TextView tv_title = findViewById(R.id.toolbartitle);
        Typeface tyface1 = Typeface.createFromAsset(CouponFirstActivity.this.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface1);
        tv_title.setText(R.string.firstcouponss);


    }

    private void ApiJaldeeCoupan(String uniqueID) {

        ApiInterface apiService =
                ApiClient.getClientS3Cloud(CouponFirstActivity.this).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(CouponFirstActivity.this, CouponFirstActivity.this.getResources().getString(R.string.dialog_log_in));
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
                        Config.closeDialog(CouponFirstActivity.this, mDialog);

                    Config.logV("URL-response--------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        coupanList = response.body();
                        mAdapter = new CouponFirstAdapter(CouponFirstActivity.this,0, coupanList);
                        coupon_listview.setAdapter(mAdapter);
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
                    Config.closeDialog(CouponFirstActivity.this, mDialog);

            }
        });


    }
}

