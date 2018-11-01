package com.nv.youneverwait.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nv.youneverwait.R;
import com.nv.youneverwait.activities.Home;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.model.FamilyArrayModel;
import com.nv.youneverwait.utils.SharedPreference;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 6/7/18.
 */

public class ProfileFragment extends RootFragment /*implements FragmentInterface*/ {


    Context mContext;
    LinearLayout mLprofile,mLchangepwd,mLchangeEmail,mLchangePhone,mLmember,mLogout,mLTerm,mLcontactus;

    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View row = inflater.inflate(R.layout.fragment_profile, container, false);
        mContext = getActivity();
        Config.logV("Profile-----------");

        Home.doubleBackToExitPressedOnce=false;
        TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);

        ImageView iBackPress=(ImageView)row.findViewById(R.id.backpress) ;
        iBackPress.setVisibility(View.GONE);

        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setText("My Profile");
        tv_title.setTypeface(tyface);


        mLprofile = (LinearLayout) row.findViewById(R.id.lprofile);
        mLchangepwd=(LinearLayout)row.findViewById(R.id.lchangepwd);
        mLchangeEmail=(LinearLayout)row.findViewById(R.id.lchangeemail);
        mLchangePhone=(LinearLayout)row.findViewById(R.id.lchangeph);
        mLmember=(LinearLayout)row.findViewById(R.id.lmember);
        mLogout=(LinearLayout)row.findViewById(R.id.llogout);
        mLTerm=(LinearLayout)row.findViewById(R.id.lterm);
        mLcontactus=(LinearLayout)row.findViewById(R.id.lcontactus);

        mLcontactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactusFragment contactFragment = new ContactusFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right);
                // Store the Fragment in stack
                transaction.addToBackStack(null);
                transaction.replace(R.id.mainlayout, contactFragment).commit();
            }
        });


        mLchangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeEmailFragment changeFragment = new ChangeEmailFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right);
                // Store the Fragment in stack
                transaction.addToBackStack(null);
                transaction.replace(R.id.mainlayout, changeFragment).commit();
            }
        });

        mLchangePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePhoneFragment changeFragment = new ChangePhoneFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right);
                // Store the Fragment in stack
                transaction.addToBackStack(null);
                transaction.replace(R.id.mainlayout, changeFragment).commit();
            }
        });

        mLchangepwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordFragment changeFragment = new ChangePasswordFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right);
                // Store the Fragment in stack
                transaction.addToBackStack(null);
                transaction.replace(R.id.mainlayout, changeFragment).commit();
            }
        });


        mLprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.logV("Update profile--------");

                EditProfileFragment pfFragment = new EditProfileFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right);
                // Store the Fragment in stack
                transaction.addToBackStack(null);
                transaction.replace(R.id.mainlayout, pfFragment).commit();

            }
        });

        mLmember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.logV("Update profile--------");
                ApiAddFamilyMember();


            }
        });

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogouFragment pfFragment = new LogouFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right);
                // Store the Fragment in stack
                transaction.addToBackStack(null);
                transaction.replace(R.id.mainlayout, pfFragment).commit();


            }
        });

        mLTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutUsFragment contactFragment = new AboutUsFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right);
                // Store the Fragment in stack
                transaction.addToBackStack(null);
                transaction.replace(R.id.mainlayout, contactFragment).commit();
            }
        });






        return row;
    }
    List<FamilyArrayModel> MuserProfileList=new ArrayList<>();
    FamilyArrayModel MuserProfileMode;
    private void ApiAddFamilyMember() {


        ApiInterface apiService =
                ApiClient.getClient(getActivity()).create(ApiInterface.class);

        final int consumerId = SharedPreference.getInstance(getActivity()).getIntValue("consumerId", 0);

        String mobno= SharedPreference.getInstance(mContext).getStringValue("mobno","");
        final Dialog mDialog = Config.getProgressDialog(getActivity(), getActivity().getResources().getString(R.string.dialog_log_in));
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


                            boolean check=FamilyListFragment.getFamilyList(MuserProfileList,mContext);
                            if(check){

                                Bundle bundle = new Bundle();
                                bundle.putString("refersh", "noupdate");
                                FamilyListFragment pfFragment = new FamilyListFragment();
                                pfFragment.setArguments(bundle);
                                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right); // Store the Fragment in stack
                                transaction.addToBackStack(null);
                                transaction.replace(R.id.mainlayout, pfFragment).commit();
                            }

                        }else {
                            Bundle bundle = new Bundle();
                            bundle.putString("familymember", "profile");


                            FamilyMemberFragment pfFragment = new FamilyMemberFragment();
                            pfFragment.setArguments(bundle);
                            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right);// Store the Fragment in stack
                            transaction.addToBackStack(null);
                            transaction.replace(R.id.mainlayout, pfFragment).commit();
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
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }
    @Override
    public void onDestroyView() {

        ViewGroup mContainer = (ViewGroup) getActivity().findViewById(R.id.mainlayout);
        mContainer.removeAllViews();
        super.onDestroyView();
    }

/*
    @Override
    public void fragmentBecameVisible() {

    }*/
}
