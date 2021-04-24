package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.response.JdnResponse;
import com.jaldeeinc.jaldee.response.Provider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JdnActivity extends AppCompatActivity {
    JdnResponse jdnList;
    String jdnDiscount, jdnMaxvalue, jdnNote;
    String uniqueid;

    TextView discount, maxvalue, note;
    Context mContext;
    private Provider providerResponse = new Provider();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jdn);
        discount = findViewById(R.id.jdn_value);
        maxvalue = findViewById(R.id.jdn_maximum_value);
        note = findViewById(R.id.jdn_note);

        mContext = this;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            uniqueid = bundle.getString("uniqueID", "");
            ApiJDN(uniqueid);

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
        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface1);
        tv_title.setText(R.string.jdnDetails);


    }

    private void ApiJDN(String uniqueID) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Call<Provider> call = apiService.getJdn(Integer.parseInt(uniqueID));
        call.enqueue(new Callback<Provider>() {
            @Override
            public void onResponse(Call<Provider> call, Response<Provider> response) {
                try {

                    if (mDialog.isShowing())
                        Config.closeDialog((Activity) mContext, mDialog);

                    Config.logV("URL-----1111----------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-----detail--------------------" + response.code());

                    if (response.code() == 200) {
                        providerResponse = response.body();
//                        jdnDiscount = jdnList.getDiscPercentage();
//                        jdnMaxvalue = jdnList.getDiscMax();
//                        jdnNote = jdnList.getDisplayNote();

                        jdnList = new Gson().fromJson(providerResponse.getJaldeediscount(),JdnResponse.class);

                        if (jdnList != null) {
                            if (jdnList.getDiscMax() != null && jdnList.getDiscPercentage() != null) {
//                            discount.setText(jdnList.getDiscPercentage() + "%");
//                            maxvalue.setText("₹" + jdnList.getDiscMax());
                                discount.setText("You will get a discount of " + Config.getAmountNoDecimalPoints(Double.parseDouble(jdnList.getDiscPercentage())) + "%" + " " + "(" + "upto" + " " + "₹" + " " + Config.getAmountinTwoDecimalPoints(Double.parseDouble(jdnList.getDiscMax())) + ")" + " " + " for every visit.");
                            }
                            if (jdnList.getDisplayNote() != null) {
                                note.setText(jdnList.getDisplayNote());
                            }
                            if (jdnList.getDisplayNote() == null) {
                                note.setVisibility(View.GONE);
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<Provider> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog((Activity) mContext, mDialog);

            }
        });


    }


}