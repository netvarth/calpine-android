package com.netvarth.youneverwait.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
    TextView btn_add;
    String firstname,lastname;
    int consumerID;
    Button btn_changemem;
TextView txt_toolbartitle;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin_family);
        mContext = this;
        mActivity = this;
        mRecycleFamily = (RecyclerView) findViewById(R.id.recycle_familyMember);

        btn_changemem = (Button) findViewById(R.id.btn_changemem);
        txt_toolbartitle=(TextView) findViewById(R.id.txt_toolbartitle);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            firstname = extras.getString("firstname");
            lastname = extras.getString("lastname");
            consumerID = extras.getInt("consumerID");
        }


        btn_add = (TextView) findViewById(R.id.btn_addmember);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(v.getContext(),AddMemberChekin.class);
                startActivity(i);
            }
        });

        btn_changemem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckIn.refreshName(s_changename,memberid);
                finish();

            }
        });
        Typeface tyface = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        btn_changemem.setTypeface(tyface);
        txt_toolbartitle.setTypeface(tyface);

        ApiListFamilyMember();
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


                            /*FamilyArrayModel family = new FamilyArrayModel();
                            family.setFirstName(firstname);
                            family.setLastName(lastname);
                            family.setId(consumerID);
                            LuserProfileList.add(family);*/
                            LuserProfileList.clear();
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

    static String s_changename;
    static int memberid;
    public static void changeMemberName(String name ,int familyMemID) {
        s_changename= name;
       memberid=familyMemID;

    }

    @Override
    protected void onResume() {
        super.onResume();
        ApiListFamilyMember();
    }
}
