package com.nv.youneverwait.custom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.nv.youneverwait.R;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.utils.SharedPreference;
import com.nv.youneverwait.utils.TypefaceFont;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 30/7/18.
 */

public class CustomPopUpWindow {
    TextInputEditText edtEmail;
    TextInputLayout txt_InputEmail;

    public void showpopupWindowEmail(final Context context, final Activity mActivity) {

        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_email);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        edtEmail = (TextInputEditText) dialog.findViewById(R.id.edtEmail);
        txt_InputEmail = (TextInputLayout) dialog.findViewById(R.id.text_input_layout_email);

        Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                "fonts/Montserrat_Bold.otf");
        edtEmail.setTypeface(tyface);


        // edtEmail.addTextChangedListener(new MyTextWatcher(edtEmail));
        Button dialogButton = (Button) dialog.findViewById(R.id.btnEmailsend);

        Typeface tyface_btn = Typeface.createFromAsset(context.getAssets(),
                "fonts/Montserrat_Medium.otf");
        dialogButton.setTypeface(tyface_btn);

        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = SharedPreference.getInstance(context).getStringValue("firstName", "");
                String lastNme = SharedPreference.getInstance(context).getStringValue("LastName", "");
                if (!edtEmail.getText().toString().equalsIgnoreCase("") && isValidEmail(edtEmail.getText().toString())) {
                    ApiResendOTp(firstName, lastNme, edtEmail.getText().toString(), "email", context, mActivity);
                    dialog.dismiss();
                }else{

                        SpannableString s = new SpannableString("InValid Email ID");
                        Typeface tyface_edittext_hint = Typeface.createFromAsset(context.getAssets(),
                                "fonts/Montserrat_Light.otf");
                        s.setSpan(new TypefaceFont(tyface_edittext_hint), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        txt_InputEmail.setError(s);


                }

            }
        });

        dialog.show();


    }

    /*private class MyTextWatcher implements TextWatcher {

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
                case R.id.edtEmail:
                    isValidEmail(edtEmail.getText().toString());
                    break;

            }
        }
    }*/

    public  boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


    public void ApiResendOTp(final String firstname, final String lastname, String email, String check, final Context mContext, final Activity mActivity) {

        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        String mobno = SharedPreference.getInstance(mContext).getStringValue("mobno", "");

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
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ResponseBody> call = apiService.getSignUpResponse(body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(mActivity, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response---------------------------" + response.code());
                    if (response.code() == 200) {
                        if (response.body().string().equalsIgnoreCase("true")) {

                            Toast.makeText(mContext, "OTP resend to your email", Toast.LENGTH_LONG).show();


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
                    Config.closeDialog(mActivity, mDialog);

            }
        });

    }
}
