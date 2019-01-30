package com.nv.youneverwait.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nv.youneverwait.R;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.database.DatabaseHandler;
import com.nv.youneverwait.response.ProfileModel;
import com.nv.youneverwait.utils.LogUtil;
import com.nv.youneverwait.utils.SharedPreference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 6/7/18.
 */

public class UpdateProfileFragment extends RootFragment {


    public UpdateProfileFragment() {
        // Required empty public constructor
    }


    Context mContext;

    DatabaseHandler db;
    FrameLayout mFrameLayout;
    TextView txtFirstName, txtLastName, txtEmail, txtPhoneNumber, txtGender, txtDob;
    Button btnedt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Config.logV("UPDATE FROFILEEEE 9999999");


        View row = inflater.inflate(R.layout.updateprofile, container, false);

        mContext = getActivity();

        TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);
        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface1);

        ImageView iBackPress=(ImageView)row.findViewById(R.id.backpress) ;
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                getFragmentManager().popBackStack();
            }
        });
        tv_title.setText("Profile");


        mFrameLayout = (FrameLayout) row.findViewById(R.id.fragment_mainLayout);
        mFrameLayout.setVisibility(View.GONE);
        txtEmail = (TextView) row.findViewById(R.id.txtemail);
        txtFirstName = (TextView) row.findViewById(R.id.txtfrstname);
        txtLastName = (TextView) row.findViewById(R.id.txtlastname);
        txtPhoneNumber = (TextView) row.findViewById(R.id.txtphno);
        txtEmail = (TextView) row.findViewById(R.id.txtemail);
        txtGender = (TextView) row.findViewById(R.id.txtgender);
        txtDob = (TextView) row.findViewById(R.id.txtdob);
        btnedt = (Button) row.findViewById(R.id.btnEdit);

        btnedt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfileFragment pfFragment = new EditProfileFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("firstname", txtFirstName.getText().toString());
                bundle.putString("lastname", txtLastName.getText().toString());
                bundle.putString("phone", txtPhoneNumber.getText().toString());
                bundle.putString("email", txtEmail.getText().toString());
                bundle.putString("dob", txtDob.getText().toString());
                bundle.putString("gender", txtGender.getText().toString());
                pfFragment.setArguments(bundle);
                // Store the Fragment in stack
                transaction.addToBackStack(null);
                transaction.replace(R.id.fragment_mainLayout, pfFragment).commit();

            }
        });


        db = new DatabaseHandler(mContext);
        if (Config.isOnline(mContext)) {

            ApiGetProfileDetail();
        } else {
            mFrameLayout.setVisibility(View.VISIBLE);
            int consumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);
            ProfileModel getProfile = db.getProfileDetail(consumerId);
            showProfileDetail(getProfile);
        }

        return row;
    }

    private void ApiGetProfileDetail() {


        ApiInterface apiService =
                ApiClient.getClient(getActivity()).create(ApiInterface.class);

        final int consumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);

        final Dialog mDialog = Config.getProgressDialog(getActivity(), getActivity().getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ProfileModel> call = apiService.getProfileDetail(consumerId);

        call.enqueue(new Callback<ProfileModel>() {
            @Override
            public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {

                        Config.logV("Response--BODY-------------------------" + new Gson().toJson(response));
                        Config.logV("Response--first-------------------------" + response.body().getUserprofile().getFirstName());
                        Config.logV("Response--name-------------------------" + response.body().getUserprofile().getLastName());
                        Config.logV("Response--dob-------------------------" + response.body().getUserprofile().getDob());
                        Config.logV("Response--mob-------------------------" + response.body().getUserprofile().getPrimaryMobileNo());
                        String db_user = SharedPreference.getInstance(mContext).getStringValue("userDb", "");
                        if (!db_user.equalsIgnoreCase("success")) {
                            db.insertUserInfo(response.body().getUserprofile());
                        } else {
                            db.updateUserInfo(response.body().getUserprofile());
                        }

                        mFrameLayout.setVisibility(View.VISIBLE);
                        ProfileModel getProfile = db.getProfileDetail(consumerId);
                        showProfileDetail(getProfile);
                        SharedPreference.getInstance(mContext).setValue("userDb", "success");
                        Config.logV("ProfileModel size-----------" + getProfile);

                    }else{

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ProfileModel> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }

    private void showProfileDetail(ProfileModel getProfile) {
        if (getProfile.getEmail() != null) {
            if (!getProfile.getEmail().equalsIgnoreCase("")) {
                txtEmail.setText(getProfile.getEmail());
            }
        }
        txtFirstName.setText(getProfile.getFirstName());
        txtLastName.setText(getProfile.getLastName());
        txtPhoneNumber.setText(getProfile.getPrimaryMobileNo());
        txtDob.setText(getProfile.getDob());
        txtGender.setText(getProfile.getGender());


    }



    @Override
    public void onResume() {
        super.onResume();
        Config.logV("On Resume update profile--------------");
        db = new DatabaseHandler(mContext);
        if (Config.isOnline(mContext)) {

            ApiGetProfileDetail();
        } else {
            mFrameLayout.setVisibility(View.VISIBLE);
            int consumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);
            ProfileModel getProfile = db.getProfileDetail(consumerId);
            showProfileDetail(getProfile);
        }
    }
}
