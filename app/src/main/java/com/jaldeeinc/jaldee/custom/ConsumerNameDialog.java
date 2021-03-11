package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jaldeeinc.jaldee.Interface.IConsumerNameSubmit;
import com.jaldeeinc.jaldee.Interface.IMailSubmit;
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


public class ConsumerNameDialog extends Dialog {
    Context context;
    EditText name;
    Button btnsave;
    DatabaseHandler db;
    ProfileModel profileDetails;
    CustomTextViewMedium tvErrorMessage;
    private IConsumerNameSubmit iConsumerNameSubmit;
    String consumerName;

    public ConsumerNameDialog(Context mContext, ProfileModel profileDetails, IConsumerNameSubmit iConsumerNameSubmit, String consumerName) {
        super(mContext);
        this.context = mContext;
        this.profileDetails = profileDetails;
        this.iConsumerNameSubmit = iConsumerNameSubmit;
        this.consumerName = consumerName;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consumer_name);
        name = findViewById(R.id.name);
        btnsave = findViewById(R.id.btnSave);
        tvErrorMessage = findViewById(R.id.error_mesg);
        Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                "fonts/JosefinSans-SemiBold.ttf");
        name.setTypeface(tyface);
        btnsave.setTypeface(tyface);
        if (consumerName != null) {

            name.setText(consumerName);

        }

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkMail();
            }
        });
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches();
    }

    private void checkMail() {

        String cName = name.getText().toString();

        if (cName.trim().length() > 0) {
            Toast.makeText(context, "Donor name has been updated successfully ", Toast.LENGTH_LONG).show();
            SharedPreference.getInstance(context).setValue("consumerName", name.getText().toString());
            iConsumerNameSubmit.consumerNameUpdated();
            dismiss();
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
                            SharedPreference.getInstance(context).setValue("email", name.getText().toString());

                            Toast.makeText(context, "Email has been updated successfully ", Toast.LENGTH_LONG).show();
                            iConsumerNameSubmit.consumerNameUpdated();
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
