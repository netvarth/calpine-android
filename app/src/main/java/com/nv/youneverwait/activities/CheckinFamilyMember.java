package com.nv.youneverwait.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nv.youneverwait.R;
import com.nv.youneverwait.adapter.CheckIn_FamilyMemberListAdapter;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.model.FamilyArrayModel;

import java.util.ArrayList;
import java.util.List;

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
    TextView btn_add;
    String firstname, lastname;
    int update;
    int consumerID, memID;
    Button btn_changemem;
    TextView txt_toolbartitle;
    ImageView imgBackpress;
    boolean multiple;
    ArrayList<FamilyArrayModel> familyList = new ArrayList<>();
    ArrayList<FamilyArrayModel> checkList = new ArrayList<>();
    ArrayList<FamilyArrayModel> data = new ArrayList<>();
    ArrayList<FamilyArrayModel> LCheckList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin_family);
        mContext = this;
        mActivity = this;
        mRecycleFamily = (RecyclerView) findViewById(R.id.recycle_familyMember);

        btn_changemem = (Button) findViewById(R.id.btn_changemem);
        txt_toolbartitle = (TextView) findViewById(R.id.txt_toolbartitle);
        imgBackpress = (ImageView) findViewById(R.id.backpress);
        imgBackpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            multiple = extras.getBoolean("multiple");

            firstname = extras.getString("firstname");
            lastname = extras.getString("lastname");
            consumerID = extras.getInt("consumerID");
            update = extras.getInt("update");
            if (update == 1) {
                checkList = (ArrayList<FamilyArrayModel>) getIntent().getSerializableExtra("checklist");
            } else {
                memID = extras.getInt("memberID");
                Config.logV("memID" + memID);
            }

        }

        Config.logV("multiplemem--------111-------------" + multiple);
        btn_add = (TextView) findViewById(R.id.btn_addmember);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), AddMemberChekin.class);
                startActivity(i);
            }
        });

        btn_changemem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (multiple) {
                    familyList.clear();

                    data = mFamilyAdpater.onItemSelected();

                    Config.logV("Family------------" + data.size());
                    for (int i = 0; i < data.size(); i++) {

                        if (data.get(i).isCheck()) {
                            FamilyArrayModel family = new FamilyArrayModel();
                            family.setId(data.get(i).getId());
                            family.setFirstName(data.get(i).getFirstName());
                            family.setLastName(data.get(i).getLastName());
                            family.setCheck(true);
                            familyList.add(family);
                        }

                        if (i == data.size() - 1) {
                            Config.logV("family refresh-------@@@@---------" + familyList.size());
                            CheckIn.refreshMultipleMEmList(familyList);
                            finish();
                        }

                    }


                } else {
                    CheckIn.refreshName(s_changename, memberid);
                    finish();
                }


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


                            LuserProfileList.clear();
                            LCheckList.clear();
                            /*FamilyArrayModel family = new FamilyArrayModel();
                            family.setFirstName(firstname);
                            family.setLastName(lastname);
                            family.setId(consumerID);
                            LuserProfileList.add(family);*/


                            //  LuserProfileList.addAll(response.body());
                            for (int i = 0; i < response.body().size(); i++) {
                                FamilyArrayModel family1 = new FamilyArrayModel();
                                family1.setFirstName(response.body().get(i).getUserProfile().getFirstName());
                                family1.setLastName(response.body().get(i).getUserProfile().getLastName());
                                family1.setId(response.body().get(i).getUserProfile().getId());
                                LuserProfileList.add(family1);
                            }


                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                            mRecycleFamily.setLayoutManager(mLayoutManager);

                            Config.logV("CheckList@@@@@@@@@@@@@@@@@@@@" + checkList.size());
                            Config.logV("CheckList@@@@@@@@@@@@@@@@@@@@ LuserProfileList" + LuserProfileList.size());
                            if (update == 1) {

                                for (int j = 0; j < LuserProfileList.size(); j++) {
                                    FamilyArrayModel family1 = new FamilyArrayModel();
                                    family1.setFirstName(LuserProfileList.get(j).getFirstName());
                                    family1.setLastName(LuserProfileList.get(j).getLastName());
                                    family1.setId(LuserProfileList.get(j).getId());
                                    for (int i = 0; i < checkList.size(); i++) {

                                        if (checkList.get(i).getId() == LuserProfileList.get(j).getId()) {
                                            family1.setCheck(true);
                                            Config.logV("Family %%%%%%%%%%%%%%%" + LuserProfileList.get(j).getFirstName());
                                        }
                                    }
                                    LCheckList.add(family1);

                                }

                                Config.logV("Family @@@@"+LCheckList.size());

                                mFamilyAdpater = new CheckIn_FamilyMemberListAdapter(LCheckList, multiple, LCheckList, mContext, mActivity);
                            } else {
                                if (memID == 0) {
                                    memID = consumerID;
                                }
                                Config.logV("memID @@@@@" + memID);
                                mFamilyAdpater = new CheckIn_FamilyMemberListAdapter(update,memID, multiple, LuserProfileList, mContext, mActivity);
                            }

                            mRecycleFamily.setAdapter(mFamilyAdpater);
                            mFamilyAdpater.notifyDataSetChanged();

                        }

                    }


                } catch(
                        Exception e)

                {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure (Call < ArrayList < FamilyArrayModel >> call, Throwable t){
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(mActivity, mDialog);

            }
        });


    }

    static String s_changename;
    static int memberid;

    public static void changeMemberName(String name, int familyMemID) {
        s_changename = name;
        memberid = familyMemID;

    }


    @Override
    protected void onResume() {
        super.onResume();
        ApiListFamilyMember();
    }

    static FamilyArrayModel familyModelData;


}
