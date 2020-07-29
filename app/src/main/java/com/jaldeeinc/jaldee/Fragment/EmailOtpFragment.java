package com.jaldeeinc.jaldee.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 11/7/18.
 */

public class EmailOtpFragment extends RootFragment {
    Context mContext;
    TextView txtResend;
    TextInputEditText edtOtp;
    Button btn_verify;
    String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View row = inflater.inflate(R.layout.fragment_verifyotp, container, false);

        mContext = getActivity();



        txtResend = (TextView) row.findViewById(R.id.resendOtp);

        edtOtp = (TextInputEditText) row.findViewById(R.id.editotp);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            email = bundle.getString("email", "");
        }
        btn_verify = (Button) row.findViewById(R.id.btn_verify);
        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Config.isOnline(mContext)) {

                    if(!edtOtp.getText().toString().equals("")){
                        ApiOtpEmail(edtOtp.getText().toString(), email);
                    }else{
                        Toast.makeText(mContext, "Enter OTP", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);


        ImageView iBackPress=(ImageView)row.findViewById(R.id.backpress) ;
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                getFragmentManager().popBackStack();
            }
        });


        tv_title.setText("Enter Otp");
        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");

        tv_title.setTypeface(tyface);




        txtResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiChangeEmail(email);
            }
        });


        return row;
    }

    private void ApiChangeEmail(final String mEmail) {


        ApiInterface apiService =
                ApiClient.getClient(getActivity()).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Call<ResponseBody> call = apiService.ChangeEmail(mEmail);


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {

                        //  if(response.body().equals("true")) {

                        // }




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
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }


    private void ApiOtpEmail(String otp, final String email) {


        ApiInterface apiService =
                ApiClient.getClient(getActivity()).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("loginId", email);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call = apiService.ChngeEmailOtp(otp, body);
//        Config.logV("Request--BODY--Emil-----------------------" + new Gson().toJson(jsonObj.toString()));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        Config.logV("Response----------------");
                        if (response.body().string().equals("true")) {
                            Config.logV("Response------#################----------"+email);
                            SharedPreference.getInstance(mContext).setValue("mobile",email);
                            Toast.makeText(mContext,"Phone Number has been updated successfully ",Toast.LENGTH_LONG).show();
                            getFragmentManager().popBackStack();


                        }


                    }if(response.code() == 422){
                        String errorString = response.errorBody().string();
                        Toast.makeText(getContext(), errorString, Toast.LENGTH_SHORT).show();
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
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }
}
