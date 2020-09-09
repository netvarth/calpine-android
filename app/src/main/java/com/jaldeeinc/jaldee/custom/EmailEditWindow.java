package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.response.ProfileModel;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EmailEditWindow extends Dialog {
    Context context;
    EditText email;
    Button btnsave;
    DatabaseHandler db;
    ProfileModel profileDetails;

    public EmailEditWindow(Context mContext, ProfileModel profileDetails){
        super(mContext);
        this.context = mContext;
        this.profileDetails = profileDetails;
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_info);
        email = findViewById(R.id.email);
        btnsave = findViewById(R.id.btnSave);

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ApiEditProfileDetail();
            }
        });
    }


    private void ApiEditProfileDetail() {


        ApiInterface apiService =
                ApiClient.getClient(context).create(ApiInterface.class);

        final int consumerId = SharedPreference.getInstance(context).getIntValue("consumerId", 0);

//        final Dialog mDialog = Config.getProgressDialog(context, context.getResources().getString(R.string.dialog_log_in));
//        mDialog.show();
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("id", consumerId);
            jsonObj.put("firstName", profileDetails.getUserprofile().getFirstName());
            jsonObj.put("lastName", profileDetails.getUserprofile().getLastName());

            jsonObj.put("email",email.getText().toString());
           /* if(radiogender.equalsIgnoreCase("")){
                radiogender=getGender;
            }*/
            jsonObj.put("gender", profileDetails.getUserprofile().getGender());

          //  String selectedDate = txtdob.getText().toString();

//            String finalDate = "";
//            if (selectedDate != null && !selectedDate.equalsIgnoreCase("")) {
//                SimpleDateFormat dateFormat = new SimpleDateFormat(
//                        "dd-MM-yyyy");
//
//                Date myDate = null;
//                try {
//                    myDate = dateFormat.parse(selectedDate);
//
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//
//                SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
//                finalDate = timeFormat.format(myDate);
//            }
//
//            Config.logV("FINAL DATE @@@@@@@@@@@@@@"+finalDate);
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
                            //   Config.logV("PopBack---------------"+getFragmentManager().getBackStackEntryCount());

//                            SharedPreference.getInstance(context).setValue("firstname", txtfirstname.getText().toString());
//                            SharedPreference.getInstance(context).setValue("lastname", txtlastname.getText().toString());
                            SharedPreference.getInstance(context).setValue("email", email.getText().toString());

                            Toast.makeText(context, "Email has been updated successfully ", Toast.LENGTH_LONG).show();
                           // getFragmentManager().popBackStackImmediate();
                           /* ProfileFragment pfFragment = new ProfileFragment();
                            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                            transaction.addToBackStack(null);
                            transaction.replace(R.id.mainlayout, pfFragment).commit();*/
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
