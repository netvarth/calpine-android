package com.netvarth.youneverwait.Fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.adapter.FavouriteAdapter;
import com.netvarth.youneverwait.adapter.InboxAdapter;
import com.netvarth.youneverwait.adapter.SearchLocationAdapter;
import com.netvarth.youneverwait.common.Config;
import com.netvarth.youneverwait.connection.ApiClient;
import com.netvarth.youneverwait.connection.ApiInterface;
import com.netvarth.youneverwait.database.DatabaseHandler;
import com.netvarth.youneverwait.response.FavouriteModel;
import com.netvarth.youneverwait.response.InboxModel;
import com.netvarth.youneverwait.response.QueueList;
import com.netvarth.youneverwait.response.SearchSetting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteFragment extends RootFragment {


    public FavouriteFragment() {
        // Required empty public constructor
    }

    RecyclerView mrRecylce_fav;
    Context mContext;
    FavouriteAdapter mFavAdapter;
    ArrayList<FavouriteModel> mFavList=new ArrayList<>();
    Activity mActivity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Config.logV("Favorite Fragment---------------------");
        View row = inflater.inflate(R.layout.fragment_fav, container, false);
        mrRecylce_fav = (RecyclerView) row.findViewById(R.id.recylce_fav);
        mContext=getActivity();
        mActivity=getActivity();
       // ApiFavList();
        return row;
    }

    ArrayList<String> ids =new ArrayList<>();
    private void ApiFavList() {

        Config.logV("API Call");
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);

        Call<ArrayList<FavouriteModel>> call = apiService.getFavourites();


        call.enqueue(new Callback<ArrayList<FavouriteModel>>() {
            @Override
            public void onResponse(Call<ArrayList<FavouriteModel>> call, Response<ArrayList<FavouriteModel>> response) {

                try {


                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        mFavList = response.body();

                        for(int i=0;i<mFavList.size();i++){
                            for(int j=0;j<mFavList.get(i).getLocations().size();j++){
                                ids.add(mFavList.get(i).getId()+"-"+mFavList.get(i).getLocations().get(j).getLocId());
                            }
                        }


                        Config.logV("Ids------------"+ids.size());
                        for(int i=0;i<ids.size();i++){

                            Config.logV("Ids---1111---------"+ids.get(i));
                        }
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                        mrRecylce_fav.setLayoutManager(mLayoutManager);
                        mFavAdapter = new FavouriteAdapter(mFavList, mContext, mActivity);
                        mrRecylce_fav.setAdapter(mFavAdapter);
                        mFavAdapter.notifyDataSetChanged();

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<FavouriteModel>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());


            }
        });


    }


    /*private void ApiSearchViewID( int mProviderid,ArrayList<String> ids) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        String idPass="";
        for(int i=0;i<ids.size();i++){
            idPass+=mProviderid+"-"+ids.get(i)+",";
        }

        Config.logV("IDS_--------------------"+idPass);
        Call<ArrayList<QueueList>> call = apiService.getSearchID(idPass);

        call.enqueue(new Callback<ArrayList<QueueList>>() {
            @Override
            public void onResponse(Call<ArrayList<QueueList>> call, Response<ArrayList<QueueList>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(mActivity, mDialog);

                    Config.logV("URL-------SEARCH--------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        mSearchQueueList=response.body();
                        ApiSearchViewSetting(uniqueID);




                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<QueueList>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(mActivity, mDialog);

            }
        });


    }




    private void ApiSearchViewSetting(String muniqueID) {


        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));


        Call<SearchSetting> call = apiService.getSearchViewSetting(Integer.parseInt(muniqueID), sdf.format(currentTime));

        call.enqueue(new Callback<SearchSetting>() {
            @Override
            public void onResponse(Call<SearchSetting> call, Response<SearchSetting> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(mActivity, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        mSearchSettings=response.body();

                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                        mRecyLocDetail.setLayoutManager(mLayoutManager);
                        mSearchLocAdapter = new SearchLocationAdapter(mSearchSettings,mSearchLocList, mContext, mServicesList,mSearchQueueList);
                        mRecyLocDetail.setAdapter(mSearchLocAdapter);
                        mSearchLocAdapter.notifyDataSetChanged();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<SearchSetting> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(mActivity, mDialog);

            }
        });


    }*/
}
