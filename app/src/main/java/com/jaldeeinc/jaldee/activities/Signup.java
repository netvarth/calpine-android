package com.jaldeeinc.jaldee.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTypefaceSpan;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.jaldeeinc.jaldee.utils.TypefaceFont;

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
    TextInputEditText firstName, Lastname, email;
    TextInputLayout mInputFirst, mInputLast, mEmail;
    TextView tv_createaccc, tv_ynw, tv_needynw;
    Context mContext;
    Button btn_signup;
    TextView tv_terms;
    String detail, countryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            detail = extras.getString("detail_id");
            countryCode = extras.getString("countryCode");

        }

        mContext = this;
        tv_terms = findViewById(R.id.txt_terms);
        firstName = findViewById(R.id.firstname);
      //  firstName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        Lastname = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
      //  Lastname.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        mInputFirst = findViewById(R.id.text_input_layout_first);
        mInputLast = findViewById(R.id.text_input_layout_last);
        mEmail = findViewById(R.id.text_input_layout_email);
        tv_createaccc = findViewById(R.id.txtcreate_acc);
        tv_ynw = findViewById(R.id.txt_ynw);
        tv_needynw = findViewById(R.id.txt_needynw);
        btn_signup = findViewById(R.id.btn_signup);
        Typeface tyface_edittext = Typeface.createFromAsset(getAssets(),
                "fonts/JosefinSans-Bold.ttf");
        firstName.setTypeface(tyface_edittext);
        Lastname.setTypeface(tyface_edittext);
        email.setTypeface(tyface_edittext);
        btn_signup.setTypeface(tyface_edittext);

        Typeface tyface_edittext_hint = Typeface.createFromAsset(getAssets(),
                "fonts/JosefinSans-Regular.ttf");
        mInputFirst.setTypeface(tyface_edittext_hint);

        Typeface tyface_edittext_hintlast = Typeface.createFromAsset(getAssets(),
                "fonts/JosefinSans-Regular.ttf");
        mInputLast.setTypeface(tyface_edittext_hintlast);

        mEmail.setTypeface(tyface_edittext_hintlast);

        String firstWord = "Jaldee ";
        String secondWord = "Terms and Conditions";
        //  <font color='#00AEF2'><b>Terms and Conditions

        Spannable spannable = new SpannableString(firstWord + secondWord);

        Typeface tyface_edittext1 = Typeface.createFromAsset(getAssets(),
                "fonts/JosefinSans-Regular.ttf");
        Typeface tyface_edittext2 = Typeface.createFromAsset(getAssets(),
                "fonts/JosefinSans-Bold.ttf");

        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface_edittext1), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.title_grey)), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface_edittext2), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.dark_blue)), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv_terms.setText(spannable);

        if(!countryCode.equalsIgnoreCase("+91")){
            mEmail.setVisibility(View.VISIBLE);
            email.setVisibility(View.VISIBLE);
        }
        else{
            mEmail.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
        }

        tv_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iterm = new Intent(v.getContext(), TermsOfUse.class);
                mContext.startActivity(iterm);
            }
        });

    }

    public void BtnSignUp(View view) {
        if(!countryCode.equalsIgnoreCase("+91")){
            if (!firstName.getText().toString().equalsIgnoreCase("") && !Lastname.getText().toString().equalsIgnoreCase("") && !email.getText().toString().equalsIgnoreCase("")) {
                isEmailValid(email.getText().toString());
                ApiSIgnup(firstName.getText().toString(), Lastname.getText().toString());
            }
            else{

                if (firstName.getText().toString().equalsIgnoreCase("")) {
                    // firstName.setError("Please enter firstname");
                    Config.logV("Firstname-----------1111");
                    SpannableString s = new SpannableString("Please enter firstname");
                    Typeface tyface_edittext_hint = Typeface.createFromAsset(getAssets(),
                            "fonts/JosefinSans-Light.ttf");
                    s.setSpan(new TypefaceFont(tyface_edittext_hint), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mInputFirst.setError(s);
                }
                if (Lastname.getText().toString().equalsIgnoreCase("")) {
                    // Lastname.setError("Please enter lastname");
                    Config.logV("Lastname-----------1111");
                    SpannableString s = new SpannableString("Please enter lastname");
                    Typeface tyface_edittext_hint = Typeface.createFromAsset(getAssets(),
                            "fonts/JosefinSans-Light.ttf");
                    s.setSpan(new TypefaceFont(tyface_edittext_hint), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mInputLast.setError(s);
                }
                if (email.getText().toString().equalsIgnoreCase("")) {
                    SpannableString s = new SpannableString("Please enter email");
                    Typeface tyface_edittext_hint = Typeface.createFromAsset(getAssets(),
                            "fonts/JosefinSans-Light.ttf");
                    s.setSpan(new TypefaceFont(tyface_edittext_hint), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mEmail.setError(s);
                }

            }
        }
        else {
            if (!firstName.getText().toString().equalsIgnoreCase("") && !Lastname.getText().toString().equalsIgnoreCase("")) {
                ApiSIgnup(firstName.getText().toString(), Lastname.getText().toString());
            } else {
                if (firstName.getText().toString().equalsIgnoreCase("")) {
                    // firstName.setError("Please enter firstname");
                    Config.logV("Firstname-----------1111");
                    SpannableString s = new SpannableString("Please enter firstname");
                    Typeface tyface_edittext_hint = Typeface.createFromAsset(getAssets(),
                            "fonts/JosefinSans-Light.ttf");
                    s.setSpan(new TypefaceFont(tyface_edittext_hint), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mInputFirst.setError(s);
                }
                if (Lastname.getText().toString().equalsIgnoreCase("")) {
                    // Lastname.setError("Please enter lastname");
                    Config.logV("Lastname-----------1111");
                    SpannableString s = new SpannableString("Please enter lastname");
                    Typeface tyface_edittext_hint = Typeface.createFromAsset(getAssets(),
                            "fonts/JosefinSans-Light.ttf");
                    s.setSpan(new TypefaceFont(tyface_edittext_hint), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mInputLast.setError(s);
                }
            }
        }
    }

    public void ApiSIgnup(final String firstname, final String lastname) {

        ApiInterface apiService =
                ApiClient.getClient(this).create(ApiInterface.class);
        final String mobno = SharedPreference.getInstance(mContext).getStringValue("mobno", "");
              String countryCode = SharedPreference.getInstance(mContext).getStringValue("countryCode", "");

        JSONObject userProfile = new JSONObject();
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("firstName", firstname);
            jsonObj.put("lastName", lastname);
            jsonObj.put("primaryMobileNo", mobno);
            jsonObj.put("countryCode", countryCode);
            if(!countryCode.equalsIgnoreCase("+91")){
                jsonObj.put("email", email.getText().toString());
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

                            if(countryCode.equalsIgnoreCase("+91")){
                                Toast.makeText(mContext, "Otp has been sent to  " + countryCode + " " + mobno, Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(mContext, "Otp has been sent to  " + "your email", Toast.LENGTH_LONG).show();
                            }


                            SharedPreference.getInstance(mContext).setValue("firstName", firstname);
                            SharedPreference.getInstance(mContext).setValue("LastName", lastname);
                            Intent iReg = new Intent(mContext, VerifyOtp.class);
                            if(detail!=null){
                                iReg.putExtra("detail_id", (detail));
                                if(countryCode!=null) {
                                    iReg.putExtra("countryCode", countryCode);
                                    if(!countryCode.equalsIgnoreCase("+91")){
                                        iReg.putExtra("email", email.getText().toString());
                                        iReg.putExtra("isInternationalNumber", true);
                                    } else{
                                        iReg.putExtra("isInternationalNumber", false);
                                    }
                                }
                            }
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
    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches();
    }
}
