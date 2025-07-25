package com.nv.youneverwait.Fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nv.youneverwait.R;
import com.nv.youneverwait.activities.Home;
import com.nv.youneverwait.adapter.FavLocationAdapter;
import com.nv.youneverwait.adapter.FavouriteAdapter;
import com.nv.youneverwait.callback.FavAdapterOnCallback;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.database.DatabaseHandler;
import com.nv.youneverwait.response.FavouriteModel;
import com.nv.youneverwait.response.QueueList;
import com.nv.youneverwait.response.SearchSetting;
import com.nv.youneverwait.utils.LogUtil;
import com.nv.youneverwait.utils.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteFragment extends RootFragment implements FavAdapterOnCallback/*,FragmentInterface*/ {


    public FavouriteFragment() {
        // Required empty public constructor
    }

    RecyclerView mrRecylce_fav;
    Context mContext;
    FavouriteAdapter mFavAdapter;
    ArrayList<FavouriteModel> mFavList = new ArrayList<>();
    Activity mActivity;
    FavAdapterOnCallback callback;
    ArrayList<QueueList> mSearchQueueList = new ArrayList<>();

    ArrayList<FavouriteModel> mFavModelList = new ArrayList<>();
    TextView tv_nofav;
    DatabaseHandler db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View row = inflater.inflate(R.layout.fragment_fav, container, false);
        Home.doubleBackToExitPressedOnce = false;
        mContext = getActivity();
        mActivity = getActivity();
        Config.logV("Favorite Fragment---------------------");
        TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);

        ImageView iBackPress = (ImageView) row.findViewById(R.id.backpress);
        iBackPress.setVisibility(View.GONE);

        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setText("My Favourites");
        tv_title.setTypeface(tyface);
        callback = (FavAdapterOnCallback) this;
        tv_nofav = (TextView) row.findViewById(R.id.txt_nofav);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mrRecylce_fav = (RecyclerView) row.findViewById(R.id.recylce_fav);

        if (Config.isOnline(getActivity())) {
            ApiFavList();
        } else {
            mFavList.clear();
            db = new DatabaseHandler(getActivity());
            mFavList = db.getFavourites();
            Config.logV("mFavList 22@@@@@@@@@@@@@@@@@" + mFavList.size());
            if (mFavList.size() > 0) {

                mrRecylce_fav.setVisibility(View.VISIBLE);
                tv_nofav.setVisibility(View.GONE);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                mrRecylce_fav.setLayoutManager(mLayoutManager);
                mFavAdapter = new FavouriteAdapter(mFavList, mContext, mActivity, callback);
                mrRecylce_fav.setAdapter(mFavAdapter);
                mFavAdapter.notifyDataSetChanged();
            } else {
                mrRecylce_fav.setVisibility(View.GONE);
                tv_nofav.setVisibility(View.VISIBLE);
            }


        }


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
                        mFavModelList.addAll(response.body());
                        if (mFavList.size() > 0) {

                            db = new DatabaseHandler(mContext);
                            db.DeleteFav();

                            for (int i = 0; i < mFavList.size(); i++) {
                                FavouriteModel fav = new FavouriteModel();
                                fav.setRevealPhoneNumber(mFavList.get(i).isRevealPhoneNumber());
                                fav.setId(mFavList.get(i).getId());
                                fav.setUniqueId(mFavList.get(i).getUniqueId());
                                fav.setBusinessName(mFavList.get(i).getBusinessName());
                                Config.logV("Revel Phone--@@@------------"+mFavList.get(i).isRevealPhoneNumber()+"Title"+mFavList.get(i).getBusinessName());
                                String locid = "";
                                String place = "";
                                for (int j = 0; j < mFavList.get(i).getLocations().size(); j++) {
                                    if (j == 0) {
                                        locid += mFavList.get(i).getLocations().get(j).getLocId();
                                    } else {
                                        locid += "," + mFavList.get(i).getLocations().get(j).getLocId();
                                    }

                                    if (j == 0) {
                                        place += mFavList.get(i).getLocations().get(j).getPlace();
                                    } else {
                                        place += "," + mFavList.get(i).getLocations().get(j).getPlace();
                                    }
                                }
                                Config.logV("Loc ID @@@@@@@@@@@" + locid);
                                fav.setLocationId(locid);
                                fav.setPlace(place);
                                db.insertFavInfo(fav);
                            }

                            mFavList.clear();
                            mFavList = db.getFavourites();



                            mrRecylce_fav.setVisibility(View.VISIBLE);
                            tv_nofav.setVisibility(View.GONE);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                            mrRecylce_fav.setLayoutManager(mLayoutManager);
                            mFavAdapter = new FavouriteAdapter(mFavList, mContext, mActivity, callback);
                            mrRecylce_fav.setAdapter(mFavAdapter);
                            mFavAdapter.notifyDataSetChanged();
                        } else {
                            mrRecylce_fav.setVisibility(View.GONE);
                            tv_nofav.setVisibility(View.VISIBLE);
                        }

                    } else {

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

    int uniQueID;
    String mTitle;

    @Override
    public void onMethodViewCallback(int mProviderid, ArrayList<String> ids, RecyclerView rfavlocRecycleview, int uniqueID, String title) {

        uniQueID = uniqueID;
        mTitle = title;

        ApiSearchViewID(mProviderid, ids, rfavlocRecycleview);

    }

    @Override
    public void onMethodMessageCallback(String accountID, String message, BottomSheetDialog mBottomDialog) {
        ApiCommunicate(accountID, message, mBottomDialog);
    }

    @Override
    public void onMethodSearchDetailCallback(int uniqueiD) {
        Bundle bundle = new Bundle();

        SearchDetailViewFragment pfFragment = new SearchDetailViewFragment();

        bundle.putString("uniqueID", String.valueOf(uniqueiD));
        pfFragment.setArguments(bundle);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_out_right, R.anim.slide_in_left);
        // Store the Fragment in stack
        transaction.addToBackStack(null);
        transaction.replace(R.id.mainlayout, pfFragment).commit();
    }

    @Override
    public void onMethodPrivacy(int ProviderID, boolean revelPhoneNumber, BottomSheetDialog mBottomDialog) {
        ApiRevelPhoneNo(ProviderID, revelPhoneNumber, mBottomDialog);

    }

    @Override
    public void onMethodDeleteFavourite(int ProviderID) {
        ApiRemoveFavo(ProviderID);

    }

    SearchSetting mSearchSettings = null;

    private void ApiSearchViewSetting(final RecyclerView mrRecylce_favloc) {


        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));


        Call<SearchSetting> call = apiService.getSearchViewSetting(uniQueID, sdf.format(currentTime));

        call.enqueue(new Callback<SearchSetting>() {
            @Override
            public void onResponse(Call<SearchSetting> call, Response<SearchSetting> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------Setting-------------------" + response.code());

                    Config.logV("mFavModelList------------------" + mFavModelList);

                    if (response.code() == 200) {

                        mSearchSettings = response.body();


                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);

                        mrRecylce_favloc.setLayoutManager(mLayoutManager);
                        FavLocationAdapter mFavAdapter = new FavLocationAdapter(mSearchQueueList, mContext, mFavModelList, mSearchSettings, String.valueOf(uniQueID), mTitle);
                        mrRecylce_favloc.setAdapter(mFavAdapter);
                        mFavAdapter.notifyDataSetChanged();
                    } else {
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                        mrRecylce_favloc.setLayoutManager(mLayoutManager);
                        FavLocationAdapter mFavAdapter = new FavLocationAdapter(mSearchQueueList, mContext, mFavModelList, mSearchSettings, String.valueOf(uniQueID), mTitle);
                        mrRecylce_favloc.setAdapter(mFavAdapter);
                        mFavAdapter.notifyDataSetChanged();

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
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }

    private void ApiSearchViewID(int mProviderid, ArrayList<String> ids, final RecyclerView rfavlocRecycleview) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Config.logV("IDS SIZE @@@@@@@@@@@@@@@@@@@@"+ids.size());
        List<String> idList = Arrays.asList(ids.get(0).split(","));


        String idPass = "";
        for (int i = 0; i < idList.size(); i++) {

            idPass += mProviderid + "-" + idList.get(i) + ",";
        }

        Config.logV("IDS_--------------------" + idPass);
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

                        mSearchQueueList = response.body();
                        ApiSearchViewSetting(rfavlocRecycleview);

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

    private void ApiCommunicate(String accountID, String message, final BottomSheetDialog mBottomDialog) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("communicationMessage", message);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call = apiService.PostMessage(accountID, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    mBottomDialog.dismiss();
                    if (response.code() == 200) {

                        Toast.makeText(mContext, "Message send successfully", Toast.LENGTH_LONG).show();


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                mBottomDialog.dismiss();
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }


    private void ApiRevelPhoneNo(int providerID, boolean revelPhoneNo, final BottomSheetDialog mBottomDialog) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Call<ResponseBody> call = apiService.RevealPhoneNo(providerID, revelPhoneNo);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    mBottomDialog.dismiss();
                    if (response.code() == 200) {

                        Toast.makeText(mContext, "Manage Privacy changed successfully", Toast.LENGTH_LONG).show();


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                mBottomDialog.dismiss();
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }

    private void ApiRemoveFavo(int providerID) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Call<ResponseBody> call = apiService.DeleteFavourite(providerID);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        if (response.body().string().equalsIgnoreCase("true")) {
                            Toast.makeText(mContext,"Removed from favourites",Toast.LENGTH_LONG).show();
                            ApiFavList();
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
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }

    /*@Override
    public void fragmentBecameVisible() {


    }*/
}
