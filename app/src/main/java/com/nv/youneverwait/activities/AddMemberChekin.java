package com.nv.youneverwait.activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nv.youneverwait.R;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.utils.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 12/9/18.
 */

public class AddMemberChekin extends AppCompatActivity {

    Button btn_savemember;
    Context mActivity;
    EditText tv_firstName, tv_Lastname;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addmember_checkin);
        mActivity=this;
        tv_firstName = (EditText) findViewById(R.id.txt_firstname);
        tv_Lastname = (EditText) findViewById(R.id.txt_lastname);
        btn_savemember = (Button) findViewById(R.id.btn_savemember);

        TextView tv_title = (TextView) findViewById(R.id.toolbartitle);
        Typeface tyface = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface);
        tv_title.setText("Add Member");
        ImageView iBackPress=(ImageView)findViewById(R.id.backpress) ;
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                finish();
            }
        });
        btn_savemember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tv_firstName.getText().toString().isEmpty()&&!tv_Lastname.getText().toString().isEmpty()) {
                    ApiAddFamilyMember();
                }else{
                    Toast.makeText(mActivity,"Please enter name",Toast.LENGTH_SHORT).show();
                }
            }
        });


        Typeface btntyface = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        btn_savemember.setTypeface(btntyface);

    }

    private void ApiAddFamilyMember() {


        ApiInterface apiService =
                ApiClient.getClient(mActivity).create(ApiInterface.class);

        final int consumerId = SharedPreference.getInstance(mActivity).getIntValue("consumerId", 0);

        final Dialog mDialog = Config.getProgressDialog(mActivity, mActivity.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        JSONObject userProfile = new JSONObject();
        JSONObject jsonObj = new JSONObject();
        try {

            jsonObj.put("firstName", tv_firstName.getText().toString());
            jsonObj.put("lastName", tv_Lastname.getText().toString());
            userProfile.putOpt("userProfile", jsonObj);
            //  userProfile.put("parent",consumerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), userProfile.toString());
        Call<ResponseBody> call = apiService.AddFamilyMEmber(body);
        Config.logV("Request--BODY-------------------------" + new Gson().toJson(userProfile.toString()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    Config.logV("Request--BODY-------------------------" + new Gson().toJson(response.body()));
                    if (response.code() == 200) {

                        finish();


                    }else{
                        String responseerror = response.errorBody().string();
                        Config.logV("Response--error----11111---------------------" + responseerror);
                        Toast.makeText(mActivity,responseerror,Toast.LENGTH_SHORT).show();
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
}
