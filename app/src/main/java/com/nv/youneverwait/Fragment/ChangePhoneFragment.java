package com.nv.youneverwait.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nv.youneverwait.R;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 11/7/18.
 */

public class ChangePhoneFragment extends RootFragment {
    Context mContext;

    TextInputEditText edtPhone;
    Button mDone;
    TextInputLayout text_input_phone;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View row = inflater.inflate(R.layout.changephone, container, false);

        mContext = getActivity();


        TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);
        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface1);

        ImageView iBackPress=(ImageView)row.findViewById(R.id.backpress) ;
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                getFragmentManager().popBackStack();
            }
        });



        tv_title.setText("Change Phone Number");

        edtPhone=(TextInputEditText) row.findViewById(R.id.edtPhone) ;
        text_input_phone=(TextInputLayout) row.findViewById(R.id.text_input_phone) ;
        edtPhone.addTextChangedListener(new MyTextWatcher(edtPhone));
        mDone=(Button)row.findViewById(R.id.btnsubmit) ;
        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Config.isOnline(mContext)){
                    if (validatePhone()) {
                        ApiChangePhone(edtPhone.getText().toString());
                    }

                }
            }
        });


        Typeface tyface_edittext = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        edtPhone.setTypeface(tyface_edittext);




        return row;
    }
    private boolean validatePhone() {
        if (edtPhone.getText().toString().trim().isEmpty()||edtPhone.getText().toString().length()>10||edtPhone.getText().toString().length()<10) {
            text_input_phone.setError(getString(R.string.err_msg_phone));
            requestFocus(edtPhone);
            return false;
        } else {
            text_input_phone.setError(null);
        }

        return true;
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
    private void ApiChangePhone(final String mPhone) {


        ApiInterface apiService =
                ApiClient.getClient(getActivity()).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(getActivity(), getActivity().getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Call<ResponseBody> call = apiService.ChangePhone(mPhone);


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
                      //  if(response.body().string().equalsIgnoreCase("true")) {

                            EmailOtpFragment emailFragment = new EmailOtpFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("email", mPhone);
                            emailFragment.setArguments(bundle);
                            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                            transaction.addToBackStack(null);
                            transaction.replace(R.id.fragment_mainLayout, emailFragment).commit();


                            //}


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
