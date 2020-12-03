package com.jaldeeinc.jaldee.activities;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.response.MyPayments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 12/9/18.
 */

public class PaymentDetail extends AppCompatActivity {

    Context mActivity;
    TextView provider,dateandtime,mode,paymentMode,paymentGateway,amount,status,refundable;
    LinearLayout providerLayout,dateandtimeLayout,modeLayout,paymentModeLayout,paymentGatewayLayout,amountLayout,statusLayout,refundableLayout;
    String id;
    TextView tv_title;
    Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paymentdetail);
        mActivity = this;

        provider = findViewById(R.id.provider);
        dateandtime = findViewById(R.id.dateandtime);
        mode = findViewById(R.id.mode);
        paymentMode = findViewById(R.id.paymentMode);
        paymentGateway = findViewById(R.id.paymentGateway);
        amount = findViewById(R.id.amount);
        status = findViewById(R.id.status);
        refundable = findViewById(R.id.refundable);

        providerLayout = findViewById(R.id.providerlayout);
        dateandtimeLayout = findViewById(R.id.dateandtimeLayout);
        modeLayout = findViewById(R.id.modeLayout);
        paymentModeLayout = findViewById(R.id.paymentModeLayout);
        paymentGatewayLayout = findViewById(R.id.paymentGatewayLayout);
        amountLayout = findViewById(R.id.amoutLayout);
        statusLayout = findViewById(R.id.statusLayout);
        refundableLayout = findViewById(R.id.refundableLayout);



        ImageView iBackPress = findViewById(R.id.backpress);
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                finish();
            }
        });

        tv_title = findViewById(R.id.toolbartitle);
        tv_title.setText("Payment Details");

        Typeface tyface = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString("myPaymentID", "");
        }


        ApiPayementDetail(id);


    }


    private void ApiPayementDetail(String id) {

        final ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<MyPayments> call = apiService.getMyPaymentsDetails(id);

        call.enqueue(new Callback<MyPayments>() {
            @Override
            public void onResponse(Call<MyPayments> call, Response<MyPayments> response) {
                try {
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        if(response.body().getAccountName()!=null){
                            provider.setText(response.body().getAccountName());
                            providerLayout.setVisibility(View.VISIBLE);
                        }else{
                            providerLayout.setVisibility(View.GONE);
                        }
                        if(response.body().getPaymentOn()!=null){
                            String date = formatDateandTime(response.body().getPaymentOn());
                            dateandtime.setText(date);
                            dateandtime.setVisibility(View.VISIBLE);
                        }else{
                            dateandtime.setVisibility(View.GONE);
                        }

                        if(response.body().getPaymentMode()!=null){
                            if (response.body().getPaymentModeName() != null) {
                                paymentModeLayout.setVisibility(View.VISIBLE);
                                paymentMode.setText(response.body().getPaymentModeName());
                            }
                            else {
                                paymentModeLayout.setVisibility(View.GONE);
                            }
                        }else{
                            paymentModeLayout.setVisibility(View.GONE);
                        }

                        if(response.body().getAcceptPaymentBy()!=null){
                            mode.setText(response.body().getAcceptPaymentBy());
                            mode.setVisibility(View.VISIBLE);
                        }else{
                            mode.setVisibility(View.GONE);
                        }

                        if(response.body().getPaymentGateway()!=null){
                            paymentGateway.setText(response.body().getPaymentGateway());
                            paymentGateway.setVisibility(View.VISIBLE);
                        }else{
                            paymentGateway.setVisibility(View.GONE);
                        }

                        if(response.body().getAmount()!=null){
                            amount.setText("₹ " + response.body().getAmount());
                            amount.setVisibility(View.VISIBLE);
                        }else{
                            amount.setVisibility(View.GONE);
                        }


                        if(response.body().getStatus()!=null){
                            status.setText(response.body().getStatus());
                            status.setVisibility(View.VISIBLE);
                        }else{
                            status.setVisibility(View.GONE);
                        }

                        if(response.body().getRefundableAmount()!=null){
                            refundable.setText(response.body().getRefundableAmount());
                            refundable.setVisibility(View.VISIBLE);
                        }else{
                            refundable.setVisibility(View.GONE);
                        }






                    } else {
                        if (response.code() != 419) {
                            Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<MyPayments> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());


            }
        });


    }

    public String formatDateandTime(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss aaa";
        String outputPattern = "dd-MMM-yyyy hh:mm:ss aaa";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
}
