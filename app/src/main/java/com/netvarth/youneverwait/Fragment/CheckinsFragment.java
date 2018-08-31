package com.netvarth.youneverwait.Fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.adapter.CheckInListAdapter;
import com.netvarth.youneverwait.adapter.CheckIn_FamilyMemberListAdapter;
import com.netvarth.youneverwait.adapter.LocationSearchAdapter;
import com.netvarth.youneverwait.common.Config;
import com.netvarth.youneverwait.connection.ApiClient;
import com.netvarth.youneverwait.connection.ApiInterface;
import com.netvarth.youneverwait.response.CheckInModel;
import com.netvarth.youneverwait.response.LocationResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class CheckinsFragment extends RootFragment {


    public CheckinsFragment() {
        // Required empty public constructor

    }


    Context mContext;
    Activity mActivity;
    RecyclerView mrRecylce_checklist;
    CheckInListAdapter mCheckAdapter;
    ArrayList<CheckInModel> mCheckList=new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View row = inflater.inflate(R.layout.fragment_checkins, container, false);
        mContext = getActivity();
        mActivity = getActivity();

        Config.logV("CheckIn--------------------------");
        mrRecylce_checklist = (RecyclerView) row.findViewById(R.id.recylce_checkin);
       //  ApiChekInList();
        return row;
    }



    private void ApiChekInList() {

        Config.logV("API Call");
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);

        Map<String, String> query = new HashMap<>();

        query.put("from", "0");
        query.put("count", "10");
        Call<ArrayList<CheckInModel>> call = apiService.getCheckInList(query);


        call.enqueue(new Callback<ArrayList<CheckInModel>>() {
            @Override
            public void onResponse(Call<ArrayList<CheckInModel>> call, Response<ArrayList<CheckInModel>> response) {

                try {


                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        mCheckList=response.body();

                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                        mrRecylce_checklist.setLayoutManager(mLayoutManager);
                        mCheckAdapter = new CheckInListAdapter(mCheckList, mContext, mActivity);
                        mrRecylce_checklist.setAdapter(mCheckAdapter);
                        mCheckAdapter.notifyDataSetChanged();

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<CheckInModel>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());


            }
        });


    }


    @Override
    public void onResume() {
        super.onResume();

    }
}
