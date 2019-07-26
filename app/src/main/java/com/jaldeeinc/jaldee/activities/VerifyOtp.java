package com.jaldeeinc.jaldee.activities;

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
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomPopUpWindow;
import com.jaldeeinc.jaldee.custom.CustomTypefaceSpan;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 3/7/18.
 */

public class VerifyOtp extends AppCompatActivity {

    TextInputEditText editotp;
    Context mContext;
    TextView txtResendOtp, txtResendEmail;

    EditText edtEmail;
    TextInputLayout txt_input_layout_otp;
    TextView txt_ynw;
    Button btn_verify;
    TextView txt_enterotp,txtproceed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verifyotp);

        btn_verify=(Button)findViewById(R.id.btn_verify) ;
        txt_enterotp=(TextView) findViewById(R.id.txt_enterotp);
        txtproceed=(TextView) findViewById(R.id.txtproceed);
   /*     TextView tv_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        tv_title.setGravity(Gravity.CENTER);
        tv_title.setText("YouNeverWait");*/


        txt_ynw= (TextView) findViewById(R.id.txt_ynw);
        txt_ynw.setVisibility(View.VISIBLE);

        Typeface tyface = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        txt_ynw.setTypeface(tyface);



        Typeface tyface_p = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Light.otf");
        txtproceed.setTypeface(tyface_p);

        mContext = this;

        editotp = (TextInputEditText) findViewById(R.id.editotp);
        txtResendOtp = (TextView) findViewById(R.id.resendOtp);
        txtResendEmail = (TextView) findViewById(R.id.txtResendOtpEmail);
        txt_input_layout_otp=(TextInputLayout)findViewById(R.id.text_input_layout_pwd);

        edtEmail = (EditText) findViewById(R.id.edtEmail);
      /*  btnSend = (Button) findViewById(R.id.btnEmailsend);*/



        Typeface tyface_edittext = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        editotp.setTypeface(tyface_edittext);

        Typeface tyface_edittext_hint = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Light.otf");
        txt_input_layout_otp.setTypeface(tyface_edittext_hint);



        txtResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = SharedPreference.getInstance(mContext).getStringValue("firstName", "");
                String lastNme = SharedPreference.getInstance(mContext).getStringValue("LastName", "");
                ApiResendOTp(firstName, lastNme, "", "");
            }
        });
        txtResendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new CustomPopUpWindow().showpopupWindowEmail(mContext,getParent());
            }
        });
        Typeface tyface_btn = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Medium.otf");
        btn_verify.setTypeface(tyface_btn);


       /* btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = SharedPreference.getInstance(mContext).getStringValue("firstName", "");
                String lastNme = SharedPreference.getInstance(mContext).getStringValue("LastName", "");
                if (!edtEmail.getText().toString().equalsIgnoreCase("")) {
                    ApiResendOTp(firstName, lastNme, edtEmail.getText().toString(), "email");
                }
            }
        });*/


        String firstWord = "Please enter ";
        String secondWord = "OTP";


        Spannable spannable = new SpannableString(firstWord+secondWord);

        Typeface tyface_edittext1 = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Light.otf");
        Typeface tyface_edittext2 = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");

        spannable.setSpan( new CustomTypefaceSpan("sans-serif",tyface_edittext1), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan( new CustomTypefaceSpan("sans-serif",tyface_edittext2), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        txt_enterotp.setText( spannable );

        /*SmsBroadcastReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String otp) {

                editotp.setText(otp);
                if(otp.length()>0){
                    if (editotp.getText().toString() != null && !editotp.getText().toString().equalsIgnoreCase("")) {
                        ApiVerifyOtp(editotp.getText().toString());
                    }
                }

            }
        });*/


    }


    public void ApiResendOTp(final String firstname, final String lastname, String email, String check) {

        ApiInterface apiService =
                ApiClient.getClient(this).create(ApiInterface.class);
        final String mobno = SharedPreference.getInstance(mContext).getStringValue("mobno", "");

        JSONObject userProfile = new JSONObject();
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("firstName", firstname);
            jsonObj.put("lastName", lastname);
            jsonObj.put("primaryMobileNo", mobno);
            jsonObj.put("countryCode", "+91");
            if (check.equalsIgnoreCase("email")) {
                jsonObj.put("email", email);
            }
            userProfile.putOpt("userProfile", jsonObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), userProfile.toString());
        Config.logV("JSON--------------" + userProfile);
        final Dialog mDialog = Config.getProgressDialog(this, this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ResponseBody> call = apiService.getSignUpResponse(body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response---------------------------" + response.code());
                    if (response.code() == 200) {
                        if (response.body().string().equalsIgnoreCase("true")) {

                          //  Toast.makeText(mContext, "OTP resend", Toast.LENGTH_LONG).show();

                            Toast.makeText(mContext,"Otp has been resend to  "+mobno,Toast.LENGTH_LONG).show();
                        }

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

    public void ApiVerifyOtp(String otp) {

        ApiInterface apiService =
                ApiClient.getClient(this).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(this, this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ResponseBody> call = apiService.OtpVerify(otp);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("Response---------------------------" + response.code());
                    if (response.code() == 200) {
                        Config.logV("Response123---------------------------");
                        if (response.body().string().equalsIgnoreCase("true")) {
                            Intent iPass = new Intent(mContext, Password.class);
                            iPass.putExtra("otp", editotp.getText().toString());
                            iPass.putExtra("from", "signup");
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
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);

            }
        });

    }

    public void BtnOtpVerify(View view) {
        if (editotp.getText().toString() != null && !editotp.getText().toString().equalsIgnoreCase("")) {
            ApiVerifyOtp(editotp.getText().toString());
        }else{
            Toast.makeText(mContext,"Please enter a valid otp",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }
}
