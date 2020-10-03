package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jaldeeinc.jaldee.Interface.IMailSubmit;
import com.jaldeeinc.jaldee.R;
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
    EditText phone;
    Button btnsave;
    DatabaseHandler db;
    ProfileModel profileDetails;
    TextView tvErrorMessage;
    private IMailSubmit iMailSubmit;
    String phoneNumber;

    public MobileNumberDialog(Context mContext, ProfileModel profileDetails, IMailSubmit iMailSubmit, String number) {
        super(mContext);
        this.context = mContext;
        this.profileDetails = profileDetails;
        this.iMailSubmit = iMailSubmit;
        this.phoneNumber = number;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobile_number);
        phone = findViewById(R.id.et_phoneNumber);
        btnsave = findViewById(R.id.btnSave);
        tvErrorMessage = findViewById(R.id.error_mesg);

        if (phoneNumber != null) {

            phone.setText(phoneNumber);

        }

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkMail();
            }
        });

    }


    private void checkMail() {

        String phoneNumber = phone.getText().toString();

        if (phoneNumber.trim().length() > 9) {
            Toast.makeText(context, "Mobile number has been updated successfully ", Toast.LENGTH_LONG).show();
            SharedPreference.getInstance(context).setValue("mobile", phone.getText().toString());
            iMailSubmit.mailUpdated();
            dismiss();
          //  ApiEditProfileDetail();

        } else {

            tvErrorMessage.setVisibility(View.VISIBLE);
            tvErrorMessage.setText("This field is required");
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

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call = apiService.getEditProfileDetail(body);
//        Config.logV("Request--BODY-------------------------" + new Gson().toJson(jsonObj.toString()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

//                    if (mDialog.isShowing())
//                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        if (response.body().string().equalsIgnoreCase("true")) {
                            SharedPreference.getInstance(context).setValue("mobile", phone.getText().toString());

                            Toast.makeText(context, "Mobile number has been updated successfully ", Toast.LENGTH_LONG).show();
                            iMailSubmit.mailUpdated();
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
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
//                if (mDialog.isShowing())
//                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }

}
