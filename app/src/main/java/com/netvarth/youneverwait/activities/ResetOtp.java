package com.netvarth.youneverwait.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.common.Config;
import com.netvarth.youneverwait.connection.ApiClient;
import com.netvarth.youneverwait.connection.ApiInterface;
import com.netvarth.youneverwait.custom.CustomTypefaceSpan;
import com.netvarth.youneverwait.service.SmsBroadcastReceiver;
import com.netvarth.youneverwait.service.SmsHelper;
import com.netvarth.youneverwait.service.SmsListener;
import com.netvarth.youneverwait.utils.SharedPreference;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final int SMS_PERMISSION_CODE = 0;

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
        tv_title.setText("Forgot Password");

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

        if (!hasReadSmsPermission()) {
            showRequestPermissionsInfoAlertDialog();
        }


       /* if (checkAndRequestPermissions()) {
            // carry on the normal flow, as the case of  permissions  granted.
        }*/
        editotp = (TextInputEditText) findViewById(R.id.editotp);

        otpverify = (Button) findViewById(R.id.btn_verify);
        otpverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiForgotResetOtp(editotp.getText().toString());
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


        SmsBroadcastReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String otp) {

                editotp.setText(otp);
                if(otp.length()>0){
                    ApiForgotResetOtp(editotp.getText().toString());
                }

            }
        });



    }



    /**
     * Optional informative alert dialog to explain the user why the app needs the Read/Send SMS permission
     */
    private void showRequestPermissionsInfoAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.permission_alert_dialog_title);
        builder.setMessage(R.string.permission_dialog_message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                requestReadAndSendSmsPermission();
            }
        });
        builder.show();
    }

    /**
     * Runtime permission shenanigans
     */
    private boolean hasReadSmsPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestReadAndSendSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {
            Log.d("", "shouldShowRequestPermissionRationale(), no permission requested");
            return;
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS},
                SMS_PERMISSION_CODE);
    }

    private void ApiResendOtp() {


        ApiInterface apiService =
                ApiClient.getClient(this).create(ApiInterface.class);

        String loginId = SharedPreference.getInstance(mContext).getStringValue("mobno", "");

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

                        Toast.makeText(mContext,"Otp Resend ",Toast.LENGTH_LONG).show();
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

                        }


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
