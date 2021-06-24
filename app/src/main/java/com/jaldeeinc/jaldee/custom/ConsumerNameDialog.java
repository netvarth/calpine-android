package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jaldeeinc.jaldee.Interface.IConsumerNameSubmit;
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


public class ConsumerNameDialog extends Dialog {
    Context context;
    EditText fName, lName;
    Button btnsave;
    DatabaseHandler db;
    ProfileModel profileDetails;
    CustomTextViewMedium tvFNameErrorMessage, tvLNameErrorMessage;
    private IConsumerNameSubmit iConsumerNameSubmit;
    String consumerName;
    String mFirstName;
    String mLastName;

    public ConsumerNameDialog(Context mContext, ProfileModel profileDetails, IConsumerNameSubmit iConsumerNameSubmit, String mFirstName, String mLastName) {
        super(mContext);
        this.context = mContext;
        this.profileDetails = profileDetails;
        this.iConsumerNameSubmit = iConsumerNameSubmit;
        this.consumerName = consumerName;
        this.mFirstName = mFirstName;
        this.mLastName = mLastName;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consumer_name);
        fName = findViewById(R.id.fname);
        lName = findViewById(R.id.lname);
        btnsave = findViewById(R.id.btnSave);
        tvFNameErrorMessage = findViewById(R.id.fname_error_mesg);
        tvLNameErrorMessage = findViewById(R.id.lname_error_mesg);

        Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                "fonts/JosefinSans-SemiBold.ttf");
        fName.setTypeface(tyface);
        lName.setTypeface(tyface);

        btnsave.setTypeface(tyface);
        if (mLastName != null) {
            fName.setText(mFirstName);
            if (mLastName != null) {
                lName.setText(mLastName);
            }
        }

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveName();
            }
        });
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches();
    }

    private void saveName() {

        if(fName.getText() == null || fName.getText().toString().equals("")){
            tvFNameErrorMessage.setVisibility(View.VISIBLE);
        }else if(lName.getText() == null || lName.getText().toString().equals("")){
            tvLNameErrorMessage.setVisibility(View.VISIBLE);
        }else {
            mFirstName = fName.getText().toString();
            mLastName = lName.getText().toString();
            Toast.makeText(context, "Donor name has been updated successfully ", Toast.LENGTH_LONG).show();
            //SharedPreference.getInstance(context).setValue("consumerName", fName.getText().toString());
            iConsumerNameSubmit.consumerNameUpdated(mFirstName, mLastName);
            dismiss();
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
                            SharedPreference.getInstance(context).setValue("email", fName.getText().toString());

                            Toast.makeText(context, "Email has been updated successfully ", Toast.LENGTH_LONG).show();
                            iConsumerNameSubmit.consumerNameUpdated(profileDetails.getUserprofile().getFirstName(), profileDetails.getUserprofile().getLastName());
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
