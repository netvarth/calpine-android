package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.jaldeeinc.jaldee.Interface.IMailSubmit;
import com.jaldeeinc.jaldee.Interface.IMobileSubmit;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.DonationActivity;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.response.ProfileModel;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MobileNumberDialog extends Dialog {
    Context context;
    EditText phone, et_countryCode;
    Button btnsave;
    DatabaseHandler db;
    ProfileModel profileDetails;
    CustomTextViewMedium tvErrorMessage;
    private IMobileSubmit iMobileSubmit;
    String phoneNumber;
    CountryCodePicker cCodePicker;
    String countryCode = "";

    public MobileNumberDialog(Context mContext, ProfileModel profileDetails, IMobileSubmit iMobileSubmit, String number, String countryCode) {
        super(mContext);
        this.context = mContext;
        this.profileDetails = profileDetails;
        this.iMobileSubmit = iMobileSubmit;
        this.phoneNumber = number;
        this.countryCode = countryCode;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobile_number);
        phone = findViewById(R.id.et_phoneNumber);
        btnsave = findViewById(R.id.btnSave);
        tvErrorMessage = findViewById(R.id.error_mesg);
        et_countryCode = findViewById(R.id.et_Ccode);
        cCodePicker = findViewById(R.id.ccp);

        Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                "fonts/JosefinSans-SemiBold.ttf");

        phone.setTypeface(tyface);
        btnsave.setTypeface(tyface);

        if(countryCode!=null){
            et_countryCode.setText(countryCode);
        }

        et_countryCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                 et_countryCode.setVisibility(View.GONE);
                 cCodePicker.setVisibility(View.VISIBLE);
                 countryCode = cCodePicker.getSelectedCountryCodeWithPlus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        cCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {

                countryCode = cCodePicker.getSelectedCountryCodeWithPlus();
            }
        });



        if (phoneNumber != null) {

            phone.setText(phoneNumber);

        }

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                cCodePicker.setVisibility(View.VISIBLE);
                countryCode = cCodePicker.getSelectedCountryCodeWithPlus();
                et_countryCode.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });





        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkMail();
            }
        });

    }


    private void checkMail() {

        String phoneNumber =  phone.getText().toString();

        if (phoneNumber.trim().length() > 9) {
            Toast.makeText(context, "Mobile number has been updated successfully ", Toast.LENGTH_LONG).show();
            SharedPreference.getInstance(context).setValue("mobile", phoneNumber);
            SharedPreference.getInstance(context).setValue("cCodeDonation",countryCode);
            iMobileSubmit.mobileUpdated();
            dismiss();

        } else if (phoneNumber.trim().equalsIgnoreCase("")) {

            tvErrorMessage.setVisibility(View.VISIBLE);
            tvErrorMessage.setText("This field is required");
        }else {

            tvErrorMessage.setVisibility(View.VISIBLE);
            tvErrorMessage.setText("Invalid Mobile Number");
        }

    }


    private void ApiEditProfileDetail() {


        ApiInterface apiService =
                ApiClient.getClient(context).create(ApiInterface.class);

        final int consumerId = SharedPreference.getInstance(context).getIntValue("consumerId", 0);
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("id", consumerId);
            jsonObj.put("firstName", profileDetails.getUserprofile().getFirstName());
            jsonObj.put("lastName", profileDetails.getUserprofile().getLastName());
            jsonObj.put("email", profileDetails.getUserprofile().getEmail());
            jsonObj.put("phone",phone.getText().toString());
            jsonObj.put("gender", profileDetails.getUserprofile().getGender());
            jsonObj.put("dob", profileDetails.getUserprofile().getDob());
            jsonObj.put("countryCode", countryCode);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call = apiService.getEditProfileDetail(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        if (response.body().string().equalsIgnoreCase("true")) {
                            SharedPreference.getInstance(context).setValue("mobile", phone.getText().toString());

                            Toast.makeText(context, "Mobile number has been updated successfully ", Toast.LENGTH_LONG).show();
                            iMobileSubmit.mobileUpdated();
                            dismiss();


                        }

                    } else {

                        Toast.makeText(context, response.errorBody().string(), Toast.LENGTH_LONG).show();

                        Config.logV("Error" + response.errorBody().string());

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Config.logV("Fail---------------" + t.toString());
            }
        });


    }

}
