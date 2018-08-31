package com.netvarth.youneverwait.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.adapter.CheckIn_FamilyMemberListAdapter;
import com.netvarth.youneverwait.common.Config;
import com.netvarth.youneverwait.connection.ApiClient;
import com.netvarth.youneverwait.connection.ApiInterface;
import com.netvarth.youneverwait.model.FamilyArrayModel;
import com.netvarth.youneverwait.utils.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 8/8/18.
 */

public class CheckinFamilyMember extends AppCompatActivity {

    Context mContext;
    Activity mActivity;
    List<FamilyArrayModel> LuserProfileList = new ArrayList<>();
    RecyclerView mRecycleFamily;
    CheckIn_FamilyMemberListAdapter mFamilyAdpater;
    Toolbar toolbar;
    Button btn_add, btn_savemember;
    LinearLayout mLinearAddMember;
    TextInputEditText tv_firstName, tv_Lastname;
    String firstname,lastname;
    int consumerID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin_family);
        mContext = this;
        mActivity = this;
        mRecycleFamily = (RecyclerView) findViewById(R.id.recycle_familyMember);
        tv_firstName = (TextInputEditText) findViewById(R.id.txt_firstname);
        tv_Lastname = (TextInputEditText) findViewById(R.id.txt_lastname);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            firstname = extras.getString("firstname");
            lastname = extras.getString("lastname");
            consumerID = extras.getInt("consumerID");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView tv_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        tv_title.setText("Family Member");

        Typeface tyface = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Regular.otf");
        tv_title.setTypeface(tyface);
        btn_add = (Button) findViewById(R.id.btn_addmember);
        btn_savemember = (Button) findViewById(R.id.btn_savemember);
        mLinearAddMember = (LinearLayout) findViewById(R.id.LAddMember);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLinearAddMember.setVisibility(View.VISIBLE);


            }
        });
        btn_savemember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiAddFamilyMember();
            }
        });
        ApiListFamilyMember();
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
                        Config.closeDialog(mActivity, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    Config.logV("Request--BODY-------------------------" + new Gson().toJson(response.body()));
                    if (response.code() == 200) {
                        mLinearAddMember.setVisibility(View.GONE);
                        tv_firstName.setText("");
                        tv_Lastname.setText("");

                        ApiListFamilyMember();
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

    private void ApiListFamilyMember() {


        ApiInterface apiService =
                ApiClient.getClient(mActivity).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(mActivity, mActivity.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Call<ArrayList<FamilyArrayModel>> call = apiService.getFamilyList();

        call.enqueue(new Callback<ArrayList<FamilyArrayModel>>() {
            @Override
            public void onResponse(Call<ArrayList<FamilyArrayModel>> call, Response<ArrayList<FamilyArrayModel>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(mActivity, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        Config.logV("Response--Array size-------------------------" + response.body().size());
                        Config.logV("Response--Array ----Family List---------------------" + new Gson().toJson(response.body()));

                        if (response.body().size() > 0) {


                            FamilyArrayModel family = new FamilyArrayModel();
                            family.setFirstName(firstname);
                            family.setLastName(lastname);
                            family.setId(consumerID);
                            LuserProfileList.add(family);
                            LuserProfileList .addAll(response.body());


                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                            mRecycleFamily.setLayoutManager(mLayoutManager);
                            mFamilyAdpater = new CheckIn_FamilyMemberListAdapter(LuserProfileList, mContext, mActivity);
                            mRecycleFamily.setAdapter(mFamilyAdpater);
                            mFamilyAdpater.notifyDataSetChanged();

                        }

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<FamilyArrayModel>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(mActivity, mDialog);

            }
        });


    }

}
