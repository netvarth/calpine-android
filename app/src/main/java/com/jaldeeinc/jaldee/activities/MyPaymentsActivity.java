package com.jaldeeinc.jaldee.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.MyPaymentAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.response.MyPayments;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPaymentsActivity extends AppCompatActivity {
    List<MyPayments> paymentsList;
    ListView payments_listview;
    String uniqueid;
    private MyPaymentAdapter mAdapter;
    LinearLayout ll_nopayments;
    Context mContext;
    Dialog mDialog;
    CardView cvBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_payment);
        initializations();


        mContext = this;
        Home.doubleBackToExitPressedOnce = false;
        mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();



        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

    }

    @Override
    public void onResume() {
        try {
            if (Config.isOnline(mContext)) {
                ApiPayments();
            } else {
                if (mDialog.isShowing())
                    Config.closeDialog((Activity) mContext, mDialog);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    private void initializations() {

        payments_listview = findViewById(R.id.payment_inner_list);
        ll_nopayments = findViewById(R.id.ll_noPayments);
        cvBack = findViewById(R.id.cv_back);

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
                        if (paymentsList.size() > 0) {
                            ll_nopayments.setVisibility(View.GONE);
                            payments_listview.setVisibility(View.VISIBLE);
                            mAdapter = new MyPaymentAdapter(mContext, 0, paymentsList);
                            payments_listview.setAdapter(mAdapter);
                        } else {
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
                    Config.closeDialog((Activity) mContext, mDialog);

            }

            @Override
            public void onFailure(Call<ArrayList<MyPayments>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());


            }
        });


    }
}
