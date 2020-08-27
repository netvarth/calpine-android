package com.jaldeeinc.jaldee.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTypefaceSpan;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.jaldeeinc.jaldee.utils.TypefaceFont;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 2/7/18.
 */

public class Register extends AppCompatActivity {

    Context mContext;
    TextInputEditText mEdtMobno;
    TextInputLayout txt_InputMob;
    Button btn_reg_submit;
    TextView tv_terms, tv_provider, tv_download;
    String sforceupdate = "";
    String detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        mEdtMobno = findViewById(R.id.editmobno);
        txt_InputMob = findViewById(R.id.text_input_layout);
        TextView tv_welcome = findViewById(R.id.txtwelcome);
        tv_terms = findViewById(R.id.txt_terms);
        tv_provider = findViewById(R.id.txtProvider);
        tv_download = findViewById(R.id.txt_download);
        btn_reg_submit = findViewById(R.id.reg_submit);
        Typeface tyface = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Light.otf");
        tv_welcome.setTypeface(tyface);
        TextView tv_ynw = findViewById(R.id.txtynw);
        Typeface tyface_ynw = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_ynw.setTypeface(tyface_ynw);

        mContext = this;
        SharedPreference.getInstance(mContext).removeKey("PREF_COOKIES");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sforceupdate = extras.getString("forceupdate", "");
            detail = extras.getString("detail_id", "");
        }

        if(detail!=null){
            Log.i("detailsofVivek",detail);
        }

        if (sforceupdate != null) {
            if (sforceupdate.equalsIgnoreCase("true")) {

                showForceUpdateDialog();
            }
        }

        Typeface tyface_edittext = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        mEdtMobno.setTypeface(tyface_edittext);
        Typeface tyface_edittext_hint = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Light.otf");
        txt_InputMob.setTypeface(tyface_edittext_hint);

        Typeface tyface_btn = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Medium.otf");
        btn_reg_submit.setTypeface(tyface_btn);
        tv_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iterm = new Intent(v.getContext(), TermsOfUse.class);
                mContext.startActivity(iterm);
            }
        });
        String firstWord = "Jaldee ";
        String secondWord = "Terms and Conditions";
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
        String text1 = "Are you a ";
        String text2 = "Service Provider? ";
        Spannable spannable_txt = new SpannableString(text1 + text2);
        spannable_txt.setSpan(new CustomTypefaceSpan("sans-serif", tyface_edittext1), 0, text1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable_txt.setSpan(new CustomTypefaceSpan("sans-serif", tyface_edittext2), text1.length(), text1.length() + text2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_provider.setText(spannable_txt);
        String text_1 = "Download the ";
        String text_2 = "Jaldee for Business App ";
        //  <font color='#00AEF2'><b>Terms and Conditions
        Spannable spannable_txt1 = new SpannableString(text_1 + text_2);
        spannable_txt1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.title_consu)), text_1.length(), text_1.length() + text_2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable_txt1.setSpan(new CustomTypefaceSpan("sans-serif", tyface_edittext1), 0, text_1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable_txt1.setSpan(new CustomTypefaceSpan("sans-serif", tyface_edittext2), text_1.length(), text_1.length() + text_2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_download.setText(spannable_txt1);
        tv_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.jaldeeinc.jaldeebusiness"));
                startActivity(intent);
            }
        });
    }

    private boolean validatePhone() {
        if (mEdtMobno.getText().toString().trim().isEmpty() || mEdtMobno.getText().toString().length() > 10 || mEdtMobno.getText().toString().length() < 10) {

            SpannableString s = new SpannableString(getString(R.string.err_msg_phone));
            Typeface tyface_edittext_hint = Typeface.createFromAsset(getAssets(),
                    "fonts/Montserrat_Light.otf");
            s.setSpan(new TypefaceFont(tyface_edittext_hint), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            txt_InputMob.setErrorEnabled(true);
            txt_InputMob.setError(s);
            //txt_InputMob.setError(getString(R.string.err_msg_phone));
            requestFocus(mEdtMobno);
            return false;
        } else {
            txt_InputMob.setError(null);
            txt_InputMob.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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
                case R.id.editmobno:
                    validatePhone();
                    break;

            }
        }
    }

    public void ApiMobileExist(String mobileno) {

        ApiInterface apiService =
                ApiClient.getClient(this).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(this, this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ResponseBody> call = apiService.chkNewUser(mobileno);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("Response---------------------------" + response.code());
                    if (response.code() == 200) {
                        Config.logV("Response123---------------------------");
                        if (response.body().string().equalsIgnoreCase("false")) {
                            SharedPreference.getInstance(mContext).setValue("mobno", mEdtMobno.getText().toString());
                            Intent iReg = new Intent(mContext, Signup.class);
                            if(detail!=null){
                                iReg.putExtra("detail_id", (detail));
                            }
                            startActivity(iReg);
                            //  finish();

                        } else {
                            SharedPreference.getInstance(mContext).setValue("mobno", mEdtMobno.getText().toString());
                            Intent iReg = new Intent(mContext, Login.class);
                            if(detail!=null){
                                iReg.putExtra("detail_id", (detail));
                            }
                            startActivity(iReg);
                            // finish();
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

    public void BtnSubmit(View view) {
        String mobno = mEdtMobno.getText().toString();


        if (validatePhone()) {
            ApiMobileExist(mobno);
        }

    }

    public void showForceUpdateDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Jaldee update required ");
        alertDialog.setMessage(" This version of Jaldee is no longer supported. Please update to the latest version.");
        alertDialog.setPositiveButton("UPDATE NOW", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                final String appPackageName = mContext.getPackageName();
                try {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreference.getInstance(mContext).removeKey("PREF_COOKIES");
    }
}
