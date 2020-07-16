package com.jaldeeinc.jaldee.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTypefaceSpan;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 3/7/18.
 */

public class ResetOtp extends AppCompatActivity {

    TextInputEditText editotp;
    Context mContext;
    Button otpverify;
    TextView resendOtp;
    TextInputLayout txt_input_layout_otp;
    ImageView img_indicator;
    TextView txt_enterotp, txtproceed, txt_ynw;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resetotp);

        ImageView iBackPress=(ImageView)findViewById(R.id.backpress) ;
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                finish();
            }
        });
        TextView tv_title = (TextView)findViewById(R.id.title);
        tv_title.setText("Forgot password");

        Typeface tyface = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface);




        img_indicator = (ImageView) findViewById(R.id.img_indicator);
        img_indicator.setVisibility(View.GONE);
        txtproceed = (TextView) findViewById(R.id.txtproceed);


        Typeface tyface_p = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Light.otf");
        txtproceed.setTypeface(tyface_p);


        mContext = this;



       /* if (checkAndRequestPermissions()) {
            // carry on the normal flow, as the case of  permissions  granted.
        }*/
        editotp = (TextInputEditText) findViewById(R.id.editotp);

        otpverify = (Button) findViewById(R.id.btn_verify);
        otpverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editotp.getText().toString() != null && !editotp.getText().toString().equalsIgnoreCase("")) {
                    ApiForgotResetOtp(editotp.getText().toString());
                }else{
                    Toast.makeText(mContext,"Please enter a valid otp",Toast.LENGTH_SHORT).show();
                }


            }
        });
        resendOtp = (TextView) findViewById(R.id.resendOtp);
        resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiResendOtp();
            }
        });
        txt_input_layout_otp = (TextInputLayout) findViewById(R.id.text_input_layout_pwd);
        Typeface tyface_edittext = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        editotp.setTypeface(tyface_edittext);

        Typeface tyface_edittext_hint = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Light.otf");
        txt_input_layout_otp.setTypeface(tyface_edittext_hint);

        Typeface tyface_btn = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Medium.otf");
        otpverify.setTypeface(tyface_btn);


        String firstWord = "Please enter ";
        String secondWord = "OTP";

        txt_enterotp = (TextView) findViewById(R.id.txt_enterotp);
        Spannable spannable = new SpannableString(firstWord + secondWord);

        Typeface tyface_edittext1 = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Light.otf");
        Typeface tyface_edittext2 = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");

        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface_edittext1), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface_edittext2), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        txt_enterotp.setText(spannable);


        /*SmsBroadcastReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String otp) {

                editotp.setText(otp);
                if(otp.length()>0){
                    ApiForgotResetOtp(editotp.getText().toString());
                }else{
                    Toast.makeText(mContext,"Please enter a valid otp",Toast.LENGTH_SHORT).show();
                }

            }
        });*/



    }


    private void ApiResendOtp() {


        ApiInterface apiService =
                ApiClient.getClient(this).create(ApiInterface.class);

        final String loginId = SharedPreference.getInstance(mContext).getStringValue("mobno", "");

        final Dialog mDialog = Config.getProgressDialog(this, this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ResponseBody> call = apiService.ForgotPwdResponse(loginId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {

                        Toast.makeText(mContext,"Otp has been resent to "+loginId,Toast.LENGTH_LONG).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);

            }
        });


    }

    public void ApiForgotResetOtp(String otp) {

        ApiInterface apiService =
                ApiClient.getClient(this).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(this, this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<String> call = apiService.ForgotResetOtp(otp);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("Response---------------------------" + response.code());
                    if (response.code() == 200) {
                        Config.logV("Response123---------------------------");
                        if (response.body().equalsIgnoreCase("true")) {
                            Intent iPass = new Intent(mContext, Password.class);
                            iPass.putExtra("otp", editotp.getText().toString());
                            iPass.putExtra("from", "login");
                            startActivity(iPass);
                            finish();

                        }else{
                            Toast.makeText(mContext,response.errorBody().string(),Toast.LENGTH_SHORT).show();
                        }


                    }else{
                        Toast.makeText(mContext,response.errorBody().string(),Toast.LENGTH_SHORT).show();
                    }





                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);

            }
        });

    }

    @Override
    protected void onResume() {
       // LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();

    }
    @Override
    public void onPause() {
        super.onPause();
       // LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

    }
}
