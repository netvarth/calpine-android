package com.jaldeeinc.jaldee.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;

import com.hbb20.CountryCodePicker;
import com.jaldeeinc.jaldee.custom.CustomTypefaceSpan;
import com.jaldeeinc.jaldee.response.ProfileModel;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.utils.SharedPreference;

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
    TextView txtmobile;
    CountryCodePicker cCodePicker;
    String countryCode = "";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View row = inflater.inflate(R.layout.changephone, container, false);

        mContext = getActivity();

        ApiGetProfileDetail();
        TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);
        txtmobile=(TextView) row.findViewById(R.id.textmobile);
        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface1);

        ImageView iBackPress=(ImageView)row.findViewById(R.id.backpress) ;

        cCodePicker = row.findViewById(R.id.ccp);

        cCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {

                countryCode = cCodePicker.getSelectedCountryCodeWithPlus();
            }
        });

        countryCode = cCodePicker.getSelectedCountryCodeWithPlus();

        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                getFragmentManager().popBackStack();
            }
        });



       // setupUI(row.findViewById(R.id.mainlayout));

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
                        ApiChangePhone(edtPhone.getText().toString(), countryCode);
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

    private void ApiGetProfileDetail() {


        ApiInterface apiService =
                ApiClient.getClient(getActivity()).create(ApiInterface.class);

        final int consumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ProfileModel> call = apiService.getProfileDetail(consumerId);

        call.enqueue(new Callback<ProfileModel>() {
            @Override
            public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {

                        Config.logV("Response--mob-------------------------" + response.body().getUserprofile().getPrimaryMobileNo());

                        SharedPreference.getInstance(mContext).setValue("mobile", response.body().getUserprofile().getPrimaryMobileNo());
                       // SharedPreference.getInstance(mContext).setValue("countryCode","");

                        String mobile = SharedPreference.getInstance(mContext).getStringValue("mobile", "");
                        String countryCode = SharedPreference.getInstance(mContext).getStringValue("countryCode", "");


                        String firstWord ="Your Current Mobile # ";
                        String secondWord = countryCode + " " +  mobile;
                        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                "fonts/Montserrat_Bold.otf");
                        Spannable spannable = new SpannableString(firstWord + secondWord);
                        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length() , firstWord.length() + secondWord.length() , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        txtmobile.setText(spannable);



                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ProfileModel> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


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
    private void ApiChangePhone(final String mPhone, String countryCode) {


        ApiInterface apiService =
                ApiClient.getClient(getActivity()).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
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
                            bundle.putString("countryCode", countryCode);
                            emailFragment.setArguments(bundle);
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.addToBackStack(null);
                            transaction.replace(R.id.mainlayout, emailFragment).commit();

                        edtPhone.setText("");
                            //}


                    }else{
                        Toast.makeText(mContext,response.errorBody().string(),Toast.LENGTH_LONG).show();
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

    /*public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof TextInputEditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(getActivity());
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }*/


}
