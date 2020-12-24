package com.jaldeeinc.jaldee.Fragment;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Home;
import com.jaldeeinc.jaldee.adapter.MyPaymentAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.response.MyPayments;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.core.content.ContextCompat.getSystemService;



public class Tab2Fragment extends RootFragment  {

    List<MyPayments> paymentsList;
    ListView payments_listview;
    String uniqueid;
    private MyPaymentAdapter mAdapter;
    LinearLayout ll_nopayments;
    Context mContext;
    Dialog mDialog;


    public Tab2Fragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View row = inflater.inflate(R.layout.fragment_tab2, container, false);
        payments_listview = row.findViewById(R.id.payment_inner_list);
        ll_nopayments = row.findViewById(R.id.ll_noPayments);

        mContext = getActivity();
        Home.doubleBackToExitPressedOnce = false;
        mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        try {
            if (Config.isOnline(mContext)) {
                ApiPayments();
            } else {
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);
        ImageView iBackPress = (ImageView) row.findViewById(R.id.backpress);
        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface1);
        iBackPress.setVisibility(View.GONE);
        tv_title.setText("My Payments");
        Typeface tyface = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface);
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });


        return row;
    }


    private void ApiPayments() {

        final ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<ArrayList<MyPayments>> call = apiService.getMyPayments();

        call.enqueue(new Callback<ArrayList<MyPayments>>() {
            @Override
            public void onResponse(Call<ArrayList<MyPayments>> call, Response<ArrayList<MyPayments>> response) {
                try {
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        paymentsList = response.body();
                        if(paymentsList.size()>0) {
                            ll_nopayments.setVisibility(View.GONE);
                            payments_listview.setVisibility(View.VISIBLE);
                            mAdapter = new MyPaymentAdapter(mContext, 0, paymentsList);
                            payments_listview.setAdapter(mAdapter);
                        }
                        else{
                            ll_nopayments.setVisibility(View.VISIBLE);
                            payments_listview.setVisibility(View.GONE);
                        }


//                        Log.i("paymentsRes",new Gson().toJson(response.body()));


                    } else {
                        if (response.code() != 419) {
                            Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }

            @Override
            public void onFailure(Call<ArrayList<MyPayments>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());


            }
        });


    }

}