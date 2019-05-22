package com.nv.youneverwait.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nv.youneverwait.R;
import com.nv.youneverwait.adapter.CouponAdapter;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.response.CoupnResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CouponFragment extends RootFragment  {


    List<CoupnResponse> coupanList;
    ListView coupon_listview;
    String uniqueid;
    private CouponAdapter mAdapter;


    public CouponFragment() {
        // Required empty public constructor
    }

    Context mContext;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View row = inflater.inflate(R.layout.couponlist, container, false);

        coupon_listview = row.findViewById(R.id.coupon_inner_list);
        mContext = getActivity();
        Bundle bundle = this.getArguments();
        if (bundle != null) {

            uniqueid = bundle.getString("uniqueID", "");
            ApiJaldeeCoupan(uniqueid);

        }

        ImageView iBackPress= row.findViewById(R.id.backpress);
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                getFragmentManager().popBackStack();
            }
        });


        TextView tv_title = row.findViewById(R.id.toolbartitle);
        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface1);
        tv_title.setText(R.string.couponss);

        return row;
    }

    private void ApiJaldeeCoupan(String uniqueID) {

        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
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
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL-response--------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        coupanList = response.body();
                        mAdapter = new CouponAdapter(mContext,0, coupanList);
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
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }
}
