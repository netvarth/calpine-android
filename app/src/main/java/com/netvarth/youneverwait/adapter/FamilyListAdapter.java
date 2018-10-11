package com.netvarth.youneverwait.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.netvarth.youneverwait.Fragment.FamilyListFragment;
import com.netvarth.youneverwait.Fragment.FamilyMemberFragment;
import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.callback.FamilyAdapterCallback;
import com.netvarth.youneverwait.common.Config;
import com.netvarth.youneverwait.connection.ApiClient;
import com.netvarth.youneverwait.connection.ApiInterface;
import com.netvarth.youneverwait.model.FamilyArrayModel;
import com.netvarth.youneverwait.model.FamilyModel;
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
 * Created by sharmila on 11/7/18.
 */


public class FamilyListAdapter extends RecyclerView.Adapter<FamilyListAdapter.MyViewHolder> {

    private List<FamilyArrayModel> familyList;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, gender;
        ImageView edit, delete;
        LinearLayout lfamily;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
           // gender = (TextView) view.findViewById(R.id.gender);
            edit = (ImageView) view.findViewById(R.id.edit);
            delete = (ImageView) view.findViewById(R.id.delete);
            lfamily = (LinearLayout) view.findViewById(R.id.lfamily);

        }
    }

    Activity activity;
    Fragment mFragment;
    FamilyAdapterCallback callback;
    public FamilyListAdapter(List<FamilyArrayModel> mfamilyList, Context mContext, Activity mActivity, Fragment fragment, FamilyAdapterCallback callback) {
        this.mContext = mContext;
        this.familyList = mfamilyList;
        this.activity = mActivity;
        this.mFragment = fragment;
        this.callback=callback;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.familytlist_row, parent, false);

        return new FamilyListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, final int position) {
        final FamilyArrayModel familylist = familyList.get(position);
        Config.logV("Family -----------------");
        myViewHolder.name.setText(familylist.getUserProfile().getFirstName());
       // myViewHolder.gender.setText(familylist.getUserProfile().getGender());


        myViewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("familymember", "edit");

                bundle.putString("firstName", familylist.getUserProfile().getFirstName());
                bundle.putString("lastname", familylist.getUserProfile().getLastName());
                bundle.putString("user",familylist.getUser());


                if (familylist.getUserProfile().getDob() != null) {
                    if (!familylist.getUserProfile().getDob().equalsIgnoreCase("")) {
                        bundle.putString("dob", familylist.getUserProfile().getDob());
                        Config.logV("Dob-----------####---" + familylist.getUserProfile().getDob());
                    }
                }
                if (familylist.getUserProfile().getGender() != null) {
                    if (!familylist.getUserProfile().getGender().equalsIgnoreCase("")) {
                        bundle.putString("gender", familylist.getUserProfile().getGender());
                    }
                }

                if (familylist.getUserProfile().getPrimaryMobileNo() != null) {
                    if (!familylist.getUserProfile().getPrimaryMobileNo().equalsIgnoreCase("")) {
                        bundle.putString("mobile", familylist.getUserProfile().getPrimaryMobileNo());
                    }
                }
                callback.onMethodCallback(bundle);
                /*

                FamilyMemberFragment pfFragment = new FamilyMemberFragment();
                pfFragment.setArguments(bundle);
                FragmentTransaction transaction = mFragment.getChildFragmentManager().beginTransaction();
                // Store the Fragment in stack
                transaction.addToBackStack(null);
                transaction.replace(R.id.mainlayout, pfFragment).commit();*/

            }
        });
        myViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiDeleteFamilyMember(familylist.getUserProfile().getId(), position);
            }
        });
        myViewHolder.lfamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    private void ApiDeleteFamilyMember(int ID, final int pos) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);

        final int consumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);

        String mobno = SharedPreference.getInstance(mContext).getStringValue("mobno", "");
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Call<ResponseBody> call = apiService.getFamilyMEmberDelete(ID);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(activity, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {


                        if (response.body().string().equalsIgnoreCase("true")) {
                            familyList.remove(pos);
                            Toast.makeText(mContext, "Member deleted successfully ", Toast.LENGTH_LONG).show();
                            notifyDataSetChanged();
                        }
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
                    Config.closeDialog(activity, mDialog);

            }
        });


    }

    @Override
    public int getItemCount() {
        return familyList.size();
    }
}
