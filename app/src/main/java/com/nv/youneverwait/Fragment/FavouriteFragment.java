package com.nv.youneverwait.Fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nv.youneverwait.R;
import com.nv.youneverwait.adapter.FavLocationAdapter;
import com.nv.youneverwait.adapter.FavouriteAdapter;
import com.nv.youneverwait.callback.FavAdapterOnCallback;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.response.FavouriteModel;
import com.nv.youneverwait.response.QueueList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteFragment extends RootFragment implements  FavAdapterOnCallback{


    public FavouriteFragment() {
        // Required empty public constructor
    }

    RecyclerView mrRecylce_fav;
    Context mContext;
    FavouriteAdapter mFavAdapter;
    ArrayList<FavouriteModel> mFavList=new ArrayList<>();
    Activity mActivity;
    FavAdapterOnCallback callback;
    ArrayList<QueueList> mSearchQueueList=new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View row = inflater.inflate(R.layout.fragment_fav, container, false);

        mContext=getActivity();
        mActivity=getActivity();
        Config.logV("Favorite Fragment---------------------");
        TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);

        ImageView iBackPress = (ImageView) row.findViewById(R.id.backpress);
        iBackPress.setVisibility(View.GONE);

        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setText("My Favorites");
        tv_title.setTypeface(tyface);
        callback=(FavAdapterOnCallback)this;


        mrRecylce_fav = (RecyclerView) row.findViewById(R.id.recylce_fav);

        ApiFavList();
        return row;
    }


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


                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                        mrRecylce_fav.setLayoutManager(mLayoutManager);
                        mFavAdapter = new FavouriteAdapter(mFavList, mContext, mActivity,callback);
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

    @Override
    public void onMethodViewCallback(int mProviderid, ArrayList<Integer> ids, final RecyclerView mrRecylce_favloc) {
        ApiSearchViewID(mProviderid,ids,mrRecylce_favloc);

    }


    private void ApiSearchViewID(int mProviderid, ArrayList<Integer> ids, final RecyclerView mrRecylce_favloc) {


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

                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                        mrRecylce_favloc.setLayoutManager(mLayoutManager);
                        FavLocationAdapter mFavAdapter = new FavLocationAdapter(mSearchQueueList, mContext);
                        mrRecylce_favloc.setAdapter(mFavAdapter);
                        mFavAdapter.notifyDataSetChanged();

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





}
