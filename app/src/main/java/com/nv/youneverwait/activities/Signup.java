package com.nv.youneverwait.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nv.youneverwait.R;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.custom.CustomTypefaceSpan;
import com.nv.youneverwait.utils.LogUtil;
import com.nv.youneverwait.utils.SharedPreference;
import com.nv.youneverwait.utils.TypefaceFont;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 2/7/18.
 */

public class Signup extends AppCompatActivity {
    TextInputEditText firstName, Lastname;
    TextInputLayout mInputFirst, mInputLast;
    TextView tv_createaccc, tv_ynw, tv_needynw;
    Context mContext;
    Button btn_signup;
    TextView tv_terms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        mContext = this;
        tv_terms = findViewById(R.id.txt_terms);
        firstName = findViewById(R.id.firstname);
        firstName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        Lastname = findViewById(R.id.lastname);
        Lastname.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        mInputFirst = findViewById(R.id.text_input_layout_first);
        mInputLast = findViewById(R.id.text_input_layout_last);
        tv_createaccc = findViewById(R.id.txtcreate_acc);
        tv_ynw = findViewById(R.id.txt_ynw);
        tv_needynw = findViewById(R.id.txt_needynw);
        btn_signup = findViewById(R.id.btn_signup);
        Typeface tyface = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_ynw.setTypeface(tyface);
        Typeface tyface_need = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Light.otf");
        tv_needynw.setTypeface(tyface_need);
        Typeface tyface_confm = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_createaccc.setTypeface(tyface_confm);
        Typeface tyface_edittext = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        firstName.setTypeface(tyface_edittext);
        Lastname.setTypeface(tyface_edittext);

        Typeface tyface_edittext_hint = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Light.otf");
        mInputFirst.setTypeface(tyface_edittext_hint);

        Typeface tyface_edittext_hintlast = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Light.otf");
        mInputLast.setTypeface(tyface_edittext_hintlast);

        Typeface tyface_btn = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Medium.otf");
        btn_signup.setTypeface(tyface_btn);

        String firstWord = "Jaldee ";
        String secondWord = "Terms and Conditions";
        //  <font color='#00AEF2'><b>Terms and Conditions

        Spannable spannable = new SpannableString(firstWord + secondWord);

        Typeface tyface_edittext1 = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Regular.otf");
        Typeface tyface_edittext2 = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");

        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface_edittext1), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.title_grey)), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface_edittext2), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.title_consu)), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv_terms.setText(spannable);

        tv_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iterm = new Intent(v.getContext(), TermsOfUse.class);
                mContext.startActivity(iterm);
            }
        });

    }

    public void BtnSignUp(View view) {
        if (!firstName.getText().toString().equalsIgnoreCase("") && !Lastname.getText().toString().equalsIgnoreCase("")) {

            ApiSIgnup(firstName.getText().toString(), Lastname.getText().toString());
        } else {
            if (firstName.getText().toString().equalsIgnoreCase("")) {
                // firstName.setError("Please enter firstname");
                Config.logV("Firstname-----------1111");
                SpannableString s = new SpannableString("Please enter firstname");
                Typeface tyface_edittext_hint = Typeface.createFromAsset(getAssets(),
                        "fonts/Montserrat_Light.otf");
                s.setSpan(new TypefaceFont(tyface_edittext_hint), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mInputFirst.setError(s);
            }
            if (Lastname.getText().toString().equalsIgnoreCase("")) {
                // Lastname.setError("Please enter lastname");
                Config.logV("Lastname-----------1111");
                SpannableString s = new SpannableString("Please enter lastname");
                Typeface tyface_edittext_hint = Typeface.createFromAsset(getAssets(),
                        "fonts/Montserrat_Light.otf");
                s.setSpan(new TypefaceFont(tyface_edittext_hint), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mInputLast.setError(s);
            }
        }
    }

    public void ApiSIgnup(final String firstname, final String lastname) {

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

                            Toast.makeText(mContext, "Otp has been send to  " + mobno, Toast.LENGTH_LONG).show();
                            SharedPreference.getInstance(mContext).setValue("firstName", firstname);
                            SharedPreference.getInstance(mContext).setValue("LastName", lastname);
                            Intent iReg = new Intent(mContext, VerifyOtp.class);
                            startActivity(iReg);
                            finish();

                            String cookie = "";
                            List<String> cookies = response.headers().values("Set-Cookie");


                            Config.logV("Response--Cookie-signup------------------------" + cookie);
                            if (!cookies.isEmpty()) {


                                StringBuffer Cookie_header = new StringBuffer();

                                for (String key : cookies) {
                                    String Cookiee = key.substring(0, key.indexOf(";"));
                                    Cookie_header.append(Cookiee + ';');

                                }

                                SharedPreference.getInstance(mContext).setValue("PREF_COOKIES", Cookie_header.toString());
                                Config.logV("Set Cookie sharedpref signup------------" + Cookie_header);

                            }
                        }

                    }

                    if(response.code() == 422){
                        if(response.code() == 422){
                            String errorString = response.errorBody().string();
                            Toast.makeText(Signup.this, errorString, Toast.LENGTH_SHORT).show();
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
}
