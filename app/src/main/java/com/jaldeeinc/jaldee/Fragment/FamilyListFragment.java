package com.jaldeeinc.jaldee.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.FamilyListAdapter;
import com.jaldeeinc.jaldee.callback.FamilyAdapterCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.model.FamilyArrayModel;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 11/7/18.
 */

public class FamilyListFragment extends RootFragment implements FamilyAdapterCallback {
     Context mContext;

    static RecyclerView mRecyclefamilyMember;
    static FamilyListAdapter familyAdapter;
    static List<FamilyArrayModel> userProfileList=new ArrayList<>();
    TextView txt_add;
     Fragment Family;
     Activity mActivity;
     String ValueCheck;
    FamilyAdapterCallback mCallback;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View row = inflater.inflate(R.layout.fragment_listmember, container, false);

        mCallback=(FamilyAdapterCallback)this;
        mContext = getActivity();

        Family=getParentFragment();
        mActivity=getActivity();


        mRecyclefamilyMember=(RecyclerView)row.findViewById(R.id.familyMember);


        TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);

        ImageView iBackPress=(ImageView)row.findViewById(R.id.backpress) ;
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                getFragmentManager().popBackStack();
            }
        });

        tv_title.setText("Members");

        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            ValueCheck = bundle.getString("refersh", "");
            if(ValueCheck.equalsIgnoreCase("update")){
                ApiAddFamilyMember();
            }

        }



        txt_add=(TextView) row.findViewById(R.id.txt_add) ;
        txt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("familymember", "profile");


                FamilyMemberFragment pfFragment = new FamilyMemberFragment();
                pfFragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                // Store the Fragment in stack
                transaction.addToBackStack(null);
                transaction.replace(R.id.mainlayout, pfFragment).commit();
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclefamilyMember.setLayoutManager(mLayoutManager);
        familyAdapter = new FamilyListAdapter(userProfileList, mContext,getActivity(),Family,mCallback);
        mRecyclefamilyMember.setAdapter(familyAdapter);
        familyAdapter.notifyDataSetChanged();
        return row;

    }
    public static  boolean  getFamilyList(List<FamilyArrayModel> muserProfileList, Context mContext){

        userProfileList=muserProfileList;
       return true;
    }




    @Override
    public void onResume() {
        super.onResume();
        Config.logV("OnRESUME----------- LIST");
        ApiAddFamilyMember();

    }

     List<FamilyArrayModel> MuserProfileList=new ArrayList<>();
    FamilyArrayModel MuserProfileMode;
    public void ApiAddFamilyMember() {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);

        final int consumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);

        String mobno= SharedPreference.getInstance(mContext).getStringValue("mobno","");
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Call<ArrayList<FamilyArrayModel>> call = apiService.getFamilyList();

        call.enqueue(new Callback<ArrayList<FamilyArrayModel>>() {
            @Override
            public void onResponse(Call<ArrayList<FamilyArrayModel>> call, Response<ArrayList<FamilyArrayModel>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        Config.logV("Response--Array size-------------------------" +response.body().size());
                        Config.logV("Response--Array ----Family List---------------------" +new Gson().toJson(response.body()));

                        if(response.body().size()>0){
                            MuserProfileList.clear();
                            for(int i=0;i<response.body().size();i++){

                                FamilyArrayModel family=new FamilyArrayModel();
                                family.setUser(response.body().get(i).getUser());
                                family.setUserProfile(response.body().get(i).getUserProfile());
                                //MuserProfileMode=response.body().get(i).getUserProfile();
                                MuserProfileList.add(family);

                            }

                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                            mRecyclefamilyMember.setLayoutManager(mLayoutManager);
                            familyAdapter = new FamilyListAdapter(MuserProfileList, mContext,getActivity(),Family,mCallback);
                            mRecyclefamilyMember.setAdapter(familyAdapter);
                            familyAdapter.notifyDataSetChanged();


                        }

                    }else{

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
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }


    @Override
    public void onMethodCallback(Bundle bundle) {
        FamilyMemberFragment pfFragment = new FamilyMemberFragment();
        pfFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        // Store the Fragment in stack
        transaction.addToBackStack(null);
        transaction.replace(R.id.mainlayout, pfFragment).commit();
    }
}
