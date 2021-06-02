package com.jaldeeinc.jaldee.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTypefaceSpan;
import com.jaldeeinc.jaldee.response.LoginResponse;
import com.jaldeeinc.jaldee.utils.LogUtil;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.jaldeeinc.jaldee.utils.TypefaceFont;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
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

public class Password extends AppCompatActivity {


    Context mContext;
    TextInputEditText mEdtpwd, mEdtconfirmPwd;
    String otp, from;

    //private static final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$" ;
    private static final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[0-9]).{8,}$";
    private Pattern pattern;
    private Matcher matcher;
    TextInputLayout txt_InputPwd, txt_Confirm_InputPwd;
    TextView txt_ynw, tv_password_title;
    Button btn_pwd_submit;
    ImageView img_indicator;
    String detail;
    String countryCode="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            otp = extras.getString("otp");
            from = extras.getString("from");
            detail = extras.getString("detail_id");
        }


        txt_ynw = (TextView) findViewById(R.id.txt_ynw);
        tv_password_title = (TextView) findViewById(R.id.txt_password_title);
        btn_pwd_submit = (Button) findViewById(R.id.pwd_submit);
        img_indicator = (ImageView) findViewById(R.id.img_indicator);

        Typeface tyface_btn = Typeface.createFromAsset(getAssets(),
                "fonts/JosefinSans-Bold.ttf");
        btn_pwd_submit.setTypeface(tyface_btn);

        countryCode = SharedPreference.getInstance(mContext).getStringValue("countryCode", "");




        if (from.equalsIgnoreCase("login")) {
            img_indicator.setVisibility(View.GONE);


            ImageView iBackPress = (ImageView) findViewById(R.id.backpress);
            iBackPress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // what do you want here
                    finish();
                }
            });
            TextView tv_title = (TextView) findViewById(R.id.title);
            tv_title.setText("Forgot password");






           /* String firstWord = "Change ";
            String secondWord = "Password";


            Spannable spannable = new SpannableString(firstWord+secondWord);

            Typeface tyface_edittext1 = Typeface.createFromAsset(getAssets(),
                    "fonts/Montserrat_Light.otf");
            Typeface tyface_edittext2 = Typeface.createFromAsset(getAssets(),
                    "fonts/Montserrat_Bold.otf");

            spannable.setSpan( new CustomTypefaceSpan("sans-serif",tyface_edittext1), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan( new CustomTypefaceSpan("sans-serif",tyface_edittext2), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);



            tv_password_title.setText( spannable );*/
            tv_password_title.setVisibility(View.INVISIBLE);

        } else {
            LinearLayout layout_toolbar = (LinearLayout) findViewById(R.id.layout_toolbar);
            layout_toolbar.setVisibility(View.GONE);
            txt_ynw.setVisibility(View.VISIBLE);

            Typeface tyface_edittext = Typeface.createFromAsset(getAssets(),
                    "fonts/JosefinSans-Bold.ttf");
            txt_ynw.setTypeface(tyface_edittext);
            //tv_password_title.setText("Create New Password");
            btn_pwd_submit.setText("Create Password");


            String firstWord = "Create New ";
            String secondWord = "Password";


            Spannable spannable = new SpannableString(firstWord + secondWord);

            Typeface tyface_edittext1 = Typeface.createFromAsset(getAssets(),
                    "fonts/JosefinSans-Light.ttf");
            Typeface tyface_edittext2 = Typeface.createFromAsset(getAssets(),
                    "fonts/JosefinSans-Bold.ttf");

            spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface_edittext1), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface_edittext2), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            tv_password_title.setVisibility(View.VISIBLE);
            tv_password_title.setText(spannable);

        }
        mContext = this;
        mEdtpwd = (TextInputEditText) findViewById(R.id.editpassword);
        mEdtconfirmPwd = (TextInputEditText) findViewById(R.id.editconfirm_password);
        txt_InputPwd = (TextInputLayout) findViewById(R.id.text_input_layout_pwd);
        txt_Confirm_InputPwd = (TextInputLayout) findViewById(R.id.text_input_layout_pwd_confirm);

        mEdtpwd.addTextChangedListener(new MyTextWatcher(mEdtpwd));
        mEdtconfirmPwd.addTextChangedListener(new MyTextWatcher(mEdtconfirmPwd));
   //     pattern = Pattern.compile(PASSWORD_PATTERN);

        mEdtpwd.setTransformationMethod(new PasswordTransformationMethod());
        mEdtconfirmPwd.setTransformationMethod(new PasswordTransformationMethod());


        Typeface tyface_edittext = Typeface.createFromAsset(getAssets(),
                "fonts/JosefinSans-Bold.ttf");
        mEdtpwd.setTypeface(tyface_edittext);
        mEdtconfirmPwd.setTypeface(tyface_edittext);

        Typeface tyface_edittext_hint = Typeface.createFromAsset(getAssets(),
                "fonts/JosefinSans-Light.ttf");
        txt_InputPwd.setTypeface(tyface_edittext_hint);
        txt_Confirm_InputPwd.setTypeface(tyface_edittext_hint);
        btn_pwd_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Btn_Pwdsubmit(v);
            }
        });

        mEdtpwd.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });

        mEdtconfirmPwd.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });


    }


//    /**
//     * Validate password with regular expression
//     *
//     * @param password password for validation
//     * @return true valid password, false invalid password
//     */
//    public boolean validatePwd(final String password) {
//
//        matcher = pattern.matcher(password);
//        return matcher.matches();
//
//    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            Config.logV("Text Change---- before");
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            Config.logV("Text Change---- After111");
            if (editable.length() > 0) {
                switch (view.getId()) {
                    case R.id.editpassword:
                        Config.logV("Text Change---- After");
                      //  validatePassword();
                        break;
                    case R.id.editconfirm_password:
                        validateConfirmPassword();
                        break;

                }
            }
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateConfirmPassword() {
        if (mEdtconfirmPwd.getText().toString().trim().isEmpty()) {
            //txt_Confirm_InputPwd.setError(getString(R.string.err_msg_password));

            SpannableString s = new SpannableString(getString(R.string.err_msg_password));
            Typeface tyface_edittext_hint = Typeface.createFromAsset(getAssets(),
                    "fonts/JosefinSans-Light.ttf");
            s.setSpan(new TypefaceFont(tyface_edittext_hint), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            txt_Confirm_InputPwd.setError(s);


            requestFocus(mEdtconfirmPwd);
            return false;
        } else {
            txt_Confirm_InputPwd.setError(null);
            txt_Confirm_InputPwd.setErrorEnabled(false);
        }

        return true;
    }

//    private boolean validatePassword() {
//        if (!validatePwd(mEdtpwd.getText().toString())) {
//
//            SpannableString s = new SpannableString(getString(R.string.err_pwd_valid));
//            Typeface tyface_edittext_hint = Typeface.createFromAsset(getAssets(),
//                    "fonts/Montserrat_Light.otf");
//            s.setSpan(new TypefaceFont(tyface_edittext_hint), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            txt_InputPwd.setError(s);
//
//            requestFocus(mEdtpwd);
//            return false;
//        } else {
//            txt_InputPwd.setError(null);
//            txt_InputPwd.setErrorEnabled(false);
//        }
//        return true;
//    }

    public void Btn_Pwdsubmit(View view) {

        if (from.equalsIgnoreCase("login")) {

         //   if (validatePassword() && validateConfirmPassword()) {
                if (mEdtpwd.getText().toString().equals(mEdtconfirmPwd.getText().toString())) {
                    ApiReSetPassword(otp, mEdtconfirmPwd.getText().toString());
                } else {
                    Toast.makeText(mContext, " Password mismatch", Toast.LENGTH_LONG).show();
                }
         //   }
        } else {


         //   if (validatePassword() && validateConfirmPassword()) {
                if (mEdtpwd.getText().toString().equals(mEdtconfirmPwd.getText().toString())) {
                    ApiSetPassword(otp, mEdtconfirmPwd.getText().toString());
                } else {
                    Toast.makeText(mContext, "Password mismatch", Toast.LENGTH_LONG).show();
                }
          //  }
        }
    }

    private void ApiReSetPassword(String otp, final String pwd) {
        {

            ApiInterface apiService =
                    ApiClient.getClient(this).create(ApiInterface.class);
            final String loginId = SharedPreference.getInstance(mContext).getStringValue("mobno", "");
            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put("loginId", loginId);
                jsonObj.put("password", pwd);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
            final Dialog mDialog = Config.getProgressDialog(this, this.getResources().getString(R.string.dialog_log_in));
            mDialog.show();
            Call<String> call = apiService.SetResetPassword(otp, body);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    try {

                        if (mDialog.isShowing())
                            Config.closeDialog(getParent(), mDialog);

                        Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                        Config.logV("code---------------" + response.code());
                        if (response.code() == 200) {
                            Config.logV("Sucess password----------" + response.body());

                            if (response.body().equalsIgnoreCase("true")) {
                                ApiLogin(loginId, pwd);
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
    }

    public void ApiSetPassword(String otp, final String pwd) {

        ApiInterface apiService =
                ApiClient.getClient(this).create(ApiInterface.class);
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("password", pwd);
            jsonObj.put("countryCode", countryCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        final Dialog mDialog = Config.getProgressDialog(this, this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ResponseBody> call = apiService.SetPassword(otp, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("code---------------" + response.code());
                    if (response.code() == 200) {
                        Config.logV("Sucess password----------");

                        if (response.body().string().equalsIgnoreCase("true")) {
                            String loginId = SharedPreference.getInstance(mContext).getStringValue("mobno", "");
                            ApiLogin(loginId, pwd);

                        }
                    }
                    else{
                        if(response.code() == 422){
                            Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_LONG).show();
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

    public void ApiLogin(String loginId, String password) {

        ApiInterface apiService =
                ApiClient.getClient(this).create(ApiInterface.class);
        SharedPreference.getInstance(mContext).setValue("password", password);

        SharedPreferences pref = mContext.getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        Config.logV("REGISTARION ID_________________" + regId);

        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("loginId", loginId);
            jsonObj.put("password", password);
            jsonObj.put("mUniqueId", regId);
            jsonObj.put("countryCode", countryCode);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Config.logV("JSON--------------" + jsonObj);
        final Dialog mDialog = Config.getProgressDialog(this, this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<LoginResponse> call = apiService.LoginResponse(getDeviceName(),body);

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

                        String cookiestored = SharedPreference.getInstance(mContext).getStringValue("PREF_COOKIES", "");

                        if (cookiestored.equalsIgnoreCase("")) {
                            // get header value

                            List<String> cookiess = response.headers().values("Set-Cookie");
                            StringBuffer Cookie_header = new StringBuffer();

                            for (String key : cookiess) {
                                String Cookiee = key.substring(0, key.indexOf(";"));
                                Cookie_header.append(Cookiee + ';');
                            }

                            SharedPreference.getInstance(mContext).setValue("PREF_COOKIES", Cookie_header.toString());
                            Config.logV("Set Cookie sharedpref password------------" + Cookie_header);
                            LogUtil.writeLogTest("*******Signup Cookie*****" + Cookie_header);

                        }

                        Headers headerList = response.headers();
                        String version = headerList.get("Version");
                        Config.logV("Header----------" + version);


                        SharedPreference.getInstance(mContext).setValue("firstname", response.body().getFirstName());
                        SharedPreference.getInstance(mContext).setValue("lastname", response.body().getLastName());
                        SharedPreference.getInstance(mContext).setValue("consumerId", response.body().getId());
                        SharedPreference.getInstance(mContext).setValue("register", "success");
                        SharedPreference.getInstance(mContext).setValue("mobile", response.body().getPrimaryPhoneNumber());
                        SharedPreference.getInstance(mContext).setValue("s3Url", response.body().getS3Url());
                        SharedPreference.getInstance(mContext).setValue("countryCode", countryCode);
                        SharedPreference.getInstance(mContext).setValue("firstBooking", response.body().isFirstCheckIn());


                        Intent iReg = new Intent(mContext, Home.class);
                        iReg.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        if(detail!=null){
                            iReg.putExtra("detail_id", (detail));
                        }
                        startActivity(iReg);
                        finish();


                    } else {
                        Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_SHORT).show();
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
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        }
        return manufacturer + " " + model;
    }
}
