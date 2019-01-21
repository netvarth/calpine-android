package com.nv.youneverwait.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nv.youneverwait.R;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.response.LoginResponse;
import com.nv.youneverwait.utils.LogUtil;
import com.nv.youneverwait.utils.SharedPreference;
import com.nv.youneverwait.utils.TypefaceFont;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Headers;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 3/7/18.
 */

public class Login extends AppCompatActivity {

    TextInputEditText edtpassword_login;
    Context mContext;
    // private static final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$" ;
    private static final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[0-9]).{8,}$";
    private Pattern pattern;
    private Matcher matcher;
    TextInputLayout txt_InputPwd;
    TextView tv_account;
    Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mContext = this;
        txt_InputPwd = (TextInputLayout) findViewById(R.id.text_input_layout_pwd);
        edtpassword_login = (TextInputEditText) findViewById(R.id.edtpassword_login);

        edtpassword_login.addTextChangedListener(new MyTextWatcher(edtpassword_login));
        pattern = Pattern.compile(PASSWORD_PATTERN);

        tv_account = (TextView) findViewById(R.id.txt_account);
        btn_login = (Button) findViewById(R.id.btn_login);


        TextView tv_ynw = (TextView) findViewById(R.id.txtynw);
        Typeface tyface = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_ynw.setTypeface(tyface);


        TextView txtpwd_login = (TextView) findViewById(R.id.txtpwd_login);
        Typeface tyface1 = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Light.otf");
        txtpwd_login.setTypeface(tyface1);


        Typeface tyface_confm = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_account.setTypeface(tyface_confm);

        edtpassword_login.setTransformationMethod(new PasswordTransformationMethod());


        Typeface tyface_btn = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Medium.otf");
        btn_login.setTypeface(tyface_btn);

        Typeface tyface_edittext = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        edtpassword_login.setTypeface(tyface_edittext);


    }

    public boolean validatePwd(String password) {

        matcher = pattern.matcher(password);
        Config.logV("Pass------------" + matcher.matches());
        return matcher.matches();

    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.editpassword:
                    validatePassword();
                    break;


            }
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validatePassword() {
        Config.logV("Password--------" + edtpassword_login.getText().toString());
        if (!validatePwd(edtpassword_login.getText().toString()) || edtpassword_login.getText().toString().isEmpty()) {
            //txt_InputPwd.setError(getString(R.string.err_pwd_valid));

            SpannableString s = new SpannableString(getString(R.string.err_pwd_valid));
            Typeface tyface_edittext_hint = Typeface.createFromAsset(getAssets(),
                    "fonts/Montserrat_Light.otf");
            s.setSpan(new TypefaceFont(tyface_edittext_hint), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            txt_InputPwd.setError(s);

            requestFocus(edtpassword_login);
            return false;
        } else {
            txt_InputPwd.setError(null);
        }

        return true;
    }

    public void ApiLogin(String loginId, String password) {

        ApiInterface apiService =
                ApiClient.getClient(this).create(ApiInterface.class);
      /*  String androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);*/

        SharedPreference.getInstance(mContext).setValue("password", password);
        SharedPreferences pref = mContext.getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        Config.logV("REGISTARION ID______________@@@@@@@___"+regId);
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("loginId", loginId);
            jsonObj.put("password", password);
            jsonObj.put("mUniqueId", regId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Config.logV("JSON--------------" + jsonObj);
        final Dialog mDialog = Config.getProgressDialog(this, this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<LoginResponse> call = apiService.LoginResponse(body);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        Config.logV("Response--code-------------------------" + response.body().getFirstName());
                        /*Headers headers = response.headers();
                        // get header value
                        String cookie = response.headers().get("Set-Cookie");

                        Config.logV("Response--Cookie-------------------------" + cookie);
                        try {

                            String Cookie_header = cookie.substring(0, cookie.indexOf(";"));
                            Config.logV("Response--Cookie--Header-----------------------" + Cookie_header);

                            SharedPreference.getInstance(mContext).setValue("cookie",Cookie_header);
                        }catch (Exception e){
                            e.printStackTrace();
                        }*/

                        // get header value
                        String cookie = response.headers().get("Set-Cookie");

                        Config.logV("Response--Cookie-------------------------" + cookie);
                        if (!cookie.isEmpty()) {

                            SharedPreference.getInstance(mContext).getStringValue("PREF_COOKIES", "");
                            String header = response.headers().get("Set-Cookie");
                            String Cookie_header = header.substring(0, header.indexOf(";"));

                            SharedPreference.getInstance(mContext).setValue("PREF_COOKIES", Cookie_header);
                            Config.logV("Set Cookie sharedpref------------" + Cookie_header);

                            LogUtil.writeLogTest("****Login Cookie****"+Cookie_header);

                        }


                        Headers headerList = response.headers();
                        String version = headerList.get("Version");
                        Config.logV("Header----------" + version);

                      //  SharedPreference.getInstance(mContext).setValue("Version", version);

                        // Config.logV("Email------------------"+response.body().get);
                        SharedPreference.getInstance(mContext).setValue("consumerId", response.body().getId());
                        SharedPreference.getInstance(mContext).setValue("register", "success");
                        SharedPreference.getInstance(mContext).setValue("firstname", response.body().getFirstName());
                        SharedPreference.getInstance(mContext).setValue("lastname", response.body().getLastName());

                        SharedPreference.getInstance(mContext).setValue("s3Url", response.body().getS3Url());

                        SharedPreference.getInstance(mContext).setValue("mobile", response.body().getPrimaryPhoneNumber());
                        Intent iReg = new Intent(mContext, Home.class);
                        iReg.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(iReg);
                        finish();


                    }else{
                        if(response.code()!=419)
                        Toast.makeText(mContext,response.errorBody().string(),Toast.LENGTH_LONG).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);

            }
        });

    }

    public void BtnLogin(View view) {
        String loginId = SharedPreference.getInstance(mContext).getStringValue("mobno", "");
        if (validatePassword()) {
            ApiLogin(loginId, edtpassword_login.getText().toString());
        }
    }

    public void ForgotPwd(View view) {
        ApiForgotPwd();
    }

    private void ApiForgotPwd() {


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
                        Toast.makeText(mContext,"Otp has been send to "+loginId,Toast.LENGTH_LONG).show();
                        Intent iReg = new Intent(mContext, ResetOtp.class);
                        startActivity(iReg);


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
}
