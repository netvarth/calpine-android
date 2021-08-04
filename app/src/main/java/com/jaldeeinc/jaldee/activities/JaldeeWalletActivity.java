package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.response.JCashInfo;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JaldeeWalletActivity extends AppCompatActivity {
    @BindView(R.id.cv_back)
    CardView cvBack;
    static Activity mActivity;
    static Context mContext;
    JCashInfo jCashInfo = new JCashInfo();
    CustomTextViewBold jCashBalance;
    LinearLayout ll_img_right_arrow;
    CardView card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jaldee_wallet);
        ButterKnife.bind(JaldeeWalletActivity.this);
        jCashBalance = findViewById(R.id.tv_jcash);
        ll_img_right_arrow = findViewById(R.id.ll_img_right_arrow);
        card = findViewById(R.id.card);
        Typeface tyface1 = Typeface.createFromAsset(JaldeeWalletActivity.this.getAssets(),
                "fonts/Montserrat_Bold.otf");
        mActivity = this;
        mContext = this;
        ApiGetJCashInfo();
        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        //llJCashCard.setOnClickListener(new View.OnClickListener() {
        card.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Config.logV("JaldeeCashActivity--------");

                Intent intent = new Intent(mActivity, JaldeeCashActivity.class);
                intent.putExtra("totCashAwarded", jCashInfo.getTotCashAwarded());
                intent.putExtra("totCashSpent", jCashInfo.getTotCashSpent());
                intent.putExtra("totCashAvailable", jCashInfo.getTotCashAvailable());
                startActivity(intent);

            }
        });

    }

    @Override
    public void onRestart() {

        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    private void ApiGetJCashInfo() {

        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<JCashInfo> call = apiService.getJCashInfo();
        call.enqueue(new Callback<JCashInfo>() {
            @Override
            public void onResponse(Call<JCashInfo> call, Response<JCashInfo> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(JaldeeWalletActivity.this, mDialog);
                    if (response.code() == 200) {
                        jCashInfo = response.body();
                        if (jCashInfo != null) {
                            if (jCashInfo.getTotCashAvailable() != null) {
                                jCashBalance.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(jCashInfo.totCashAvailable)));
                            }
                            if (jCashInfo.getTotCashAwarded() != null && Double.parseDouble(jCashInfo.getTotCashAwarded()) == 0) {
                                ll_img_right_arrow.setVisibility(View.GONE);
                                card.setOnClickListener(null);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JCashInfo> call, Throwable t) {

            }
        });
    }

}