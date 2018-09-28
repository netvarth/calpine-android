package com.netvarth.youneverwait.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.activities.SwipeGalleryImage;
import com.netvarth.youneverwait.adapter.SearchLocationAdapter;
import com.netvarth.youneverwait.callback.SearchLocationAdpterCallback;
import com.netvarth.youneverwait.common.Config;
import com.netvarth.youneverwait.connection.ApiClient;
import com.netvarth.youneverwait.connection.ApiInterface;
import com.netvarth.youneverwait.custom.CircleTransform;
import com.netvarth.youneverwait.model.WorkingModel;
import com.netvarth.youneverwait.response.QueueList;
import com.netvarth.youneverwait.response.SearchCheckInMessage;
import com.netvarth.youneverwait.response.SearchLocation;
import com.netvarth.youneverwait.response.SearchService;
import com.netvarth.youneverwait.response.SearchSetting;
import com.netvarth.youneverwait.response.SearchTerminology;
import com.netvarth.youneverwait.response.SearchViewDetail;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by sharmila on 24/7/18.
 */


public class SearchDetailViewFragment extends RootFragment implements SearchLocationAdpterCallback {

    Context mContext;
    Toolbar toolbar;
    SearchViewDetail mBusinessDataList;
    ArrayList<SearchViewDetail> mSearchGallery;
    ArrayList<SearchLocation> mSearchLocList;

    SearchSetting mSearchSettings;
    SearchTerminology mSearchTerminology;

    ArrayList<QueueList> mSearchQueueList;

    ArrayList<SearchService> mServicesList;

    ArrayList<SearchCheckInMessage> mSearchmCheckMessageList;


    TextView tv_busName, tv_domain, tv_desc, tv_exp;

    RecyclerView mRecyLocDetail;
    SearchLocationAdapter mSearchLocAdapter;
    ImageView mImgeProfile, mImgthumbProfile, mImgthumbProfile2, mImgthumbProfile1;

    int mProvoderId;
    ArrayList<String> ids;
    String uniqueID;

    TextView tv_ImageViewText;
    RatingBar rating;
    SearchLocationAdpterCallback mInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View row = inflater.inflate(R.layout.searchdetails, container, false);

        mContext = getActivity();
        mRecyLocDetail = (RecyclerView) row.findViewById(R.id.mSearchLoc);
        rating = (RatingBar) row.findViewById(R.id.mRatingBar);

        count = 0;
        mBusinessDataList = new SearchViewDetail();
        mSearchGallery = new ArrayList<>();
        mSearchLocList = new ArrayList<>();
        mSearchSettings = new SearchSetting();
        mSearchTerminology = new SearchTerminology();
        mSearchQueueList = new ArrayList<>();
        mServicesList = new ArrayList<>();
        mSearchmCheckMessageList = new ArrayList<>();
        ids = new ArrayList<>();


        toolbar = (Toolbar) row.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(" Search ");


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // perform whatever you want on back arrow click
                Config.logV("BackPress-----------");
                getFragmentManager().popBackStack();
            }
        });

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            uniqueID = bundle.getString("uniqueID");

        }

        mRecyLocDetail.setNestedScrollingEnabled(false);

        Config.logV("UNIUE ID---------1111-------" + uniqueID);
        tv_busName = (TextView) row.findViewById(R.id.txtbus_name);
        tv_domain = (TextView) row.findViewById(R.id.txt_domain);
        mImgeProfile = (ImageView) row.findViewById(R.id.i_profile);
        mImgthumbProfile = (ImageView) row.findViewById(R.id.iThumb_profile);
        mImgthumbProfile2 = (ImageView) row.findViewById(R.id.iThumb_profile2);
        tv_ImageViewText = (TextView) row.findViewById(R.id.mImageViewText);
        mImgthumbProfile1 = (ImageView) row.findViewById(R.id.iThumb_profile1);

        tv_exp = (TextView) row.findViewById(R.id.txt_expe);
        tv_desc = (TextView) row.findViewById(R.id.txt_bus_desc);

        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_busName.setTypeface(tyface);
        tv_ImageViewText.setTypeface(tyface);


        ApiSearchViewDetail(uniqueID);
        ApiSearchGallery(uniqueID);

        ApiSearchViewTerminology(uniqueID);
        mInterface = (SearchLocationAdpterCallback) this;

        return row;
    }


    public void UpdateGallery(final ArrayList<SearchViewDetail> mGallery) {
        //  Picasso.with(this).load(mGallery.get(0).getUrl()).fit().into(mImgeProfile);

        Config.logV("Gallery--------------333-----" + mGallery.size());
        Picasso.with(mContext).load(mGallery.get(0).getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(mImgeProfile);

        if (mGallery.size() > 1) {
            mImgthumbProfile.setVisibility(View.VISIBLE);
            // Picasso.with(this).load(mGallery.get(1).getUrl()).fit().into(mImgthumbProfile);

            Picasso.with(mContext).load(mGallery.get(1).getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(mImgthumbProfile);


            if (mGallery.size() == 3) {
                mImgthumbProfile1.setVisibility(View.VISIBLE);
                Config.logV("Gallery--------");
                Picasso.with(mContext).load(mGallery.get(2).getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(mImgthumbProfile1);
            } else {
                mImgthumbProfile1.setVisibility(View.GONE);
            }

            if (mGallery.size() > 3) {

                mImgthumbProfile1.setVisibility(View.VISIBLE);
                Picasso.with(mContext).load(mGallery.get(2).getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(mImgthumbProfile1);
                mImgthumbProfile2.setVisibility(View.VISIBLE);
                Picasso.with(mContext).load(mGallery.get(3).getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(mImgthumbProfile2);
                tv_ImageViewText.setVisibility(View.VISIBLE);
                tv_ImageViewText.setText(" +" + String.valueOf(mGallery.size() - 3));
                Config.logV("Galeery--------------11111-----------" + mGallery.size());
                mImgthumbProfile2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Config.logV("Gallery------------------------------" + mGallery.size());
                        ArrayList<String> mGalleryList = new ArrayList<>();
                        for (int i = 1; i < mGallery.size(); i++) {
                        /*SearchViewDetail data = new SearchViewDetail();
                        data.setUrl(mGallery.get(i).getUrl());*/
                            mGalleryList.add(mGallery.get(i).getUrl());
                        }


                        boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryList, v.getContext());
                        if (mValue) {

                            Intent intent = new Intent(mContext, SwipeGalleryImage.class);
                            startActivity(intent);
                        }


                    }
                });
            } else {
                mImgthumbProfile2.setVisibility(View.GONE);
                tv_ImageViewText.setVisibility(View.GONE);
                // mImgthumbProfile1.setVisibility(View.GONE);
            }


        } else {
            mImgthumbProfile.setVisibility(View.GONE);
        }
    }

    public void UpdateMainUI(SearchViewDetail getBussinessData) {


        if (getBussinessData.getVerifyLevel() != null) {
            if (!getBussinessData.getVerifyLevel().equalsIgnoreCase("NONE")) {
                tv_busName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_verified, 0);

            } else {

                tv_busName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }

        }else{
            tv_busName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        tv_busName.setText(getBussinessData.getBusinessName());
        rating.setRating(getBussinessData.getAvgRating());


        tv_domain.setText(getBussinessData.getServiceSector().getDisplayName());

        if (getBussinessData.getDomainVirtualFields() != null) {
            if (getBussinessData.getDomainVirtualFields().getExperience() != null) {

                tv_exp.setVisibility(View.VISIBLE);
                tv_exp.setText(getBussinessData.getDomainVirtualFields().getExperience());
            } else {
                tv_exp.setVisibility(View.GONE);
            }

        } else {
            tv_exp.setVisibility(View.GONE);
        }

        if (getBussinessData.getBusinessDesc() != null) {
            tv_desc.setVisibility(View.VISIBLE);
            tv_desc.setText(getBussinessData.getBusinessDesc());
        } else {
            tv_desc.setVisibility(View.GONE);
        }


    }

    private void ApiSearchViewDetail(final String muniqueID) {


        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));


        Call<SearchViewDetail> call = apiService.getSearchViewDetail(Integer.parseInt(muniqueID), sdf.format(currentTime));

        call.enqueue(new Callback<SearchViewDetail>() {
            @Override
            public void onResponse(Call<SearchViewDetail> call, Response<SearchViewDetail> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-----detail--------------------" + response.code());

                    if (response.code() == 200) {

                        mBusinessDataList = response.body();
                        mProvoderId = response.body().getId();
                        UpdateMainUI(mBusinessDataList);
                        ApiSearchViewLocation(uniqueID);

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<SearchViewDetail> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }

    private void ApiSearchGallery(final String muniqueID) {


        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));


        Call<ArrayList<SearchViewDetail>> call = apiService.getSearchGallery(Integer.parseInt(muniqueID), sdf.format(currentTime));

        call.enqueue(new Callback<ArrayList<SearchViewDetail>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchViewDetail>> call, Response<ArrayList<SearchViewDetail>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-----gallery--------------------" + response.code());

                    if (response.code() == 200) {

                        mSearchGallery = response.body();
                        UpdateGallery(mSearchGallery);

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<SearchViewDetail>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }


    int count = 0;

    private void ApiSearchViewLocation(final String muniqueID) {


        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));


        Call<ArrayList<SearchLocation>> call = apiService.getSearchViewLoc(Integer.parseInt(muniqueID), sdf.format(currentTime));

        call.enqueue(new Callback<ArrayList<SearchLocation>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchLocation>> call, Response<ArrayList<SearchLocation>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code--Location-----------------------" + response.code());
                    mSearchLocList.clear();

                    if (response.code() == 200) {

                        mSearchLocList = response.body();
                        for (int i = 0; i < response.body().size(); i++) {
                            ids.add(String.valueOf(response.body().get(i).getId()));
                        }
                        /*for(int i=0;i<response.body().size();i++) {
                            SearchLocation searchloc = new SearchLocation();
                            searchloc.setId(response.body().get(i).getId());
                            searchloc.setPlace(response.body().get(i).getPlace());
                            searchloc.setParkingType(response.body().get(i).getParkingType());
                            searchloc.setBaseLocation(response.body().get(i).isBaseLocation());
                            searchloc.setOpen24hours(response.body().get(i).isOpen24hours());
                            searchloc.setTraumacentre(response.body().get(i).getLocationVirtualFields().getTraumacentre());
                            searchloc.setPhysiciansemergencyservices(response.body().get(i).getLocationVirtualFields().getPhysiciansemergencyservices());
                            searchloc.setTimespec(response.body().get(i).getbSchedule().getTimespec());
                            mSearchLocList.add(searchloc);
                            ids.add(String.valueOf(response.body().get(i).getId()));
                        }*/


                        for (int k = 0; k < mSearchLocList.size(); k++) {

                            ApiCheckInMessage(mSearchLocList.get(k).getId());

                        }


                        for (int k = 0; k < mSearchLocList.size(); k++) {

                            ApiSearchViewServiceID(mSearchLocList.get(k).getId());

                        }


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<SearchLocation>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }

    private void ApiCheckInMessage(final int mLocid) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Map<String, String> query = new HashMap<>();

        query.put("location-eq", String.valueOf(mLocid));
        query.put("waitlistStatus-eq", "checkedIn,arrived");


        Call<ArrayList<SearchCheckInMessage>> call = apiService.getSearchCheckInMessage(query);

        call.enqueue(new Callback<ArrayList<SearchCheckInMessage>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchCheckInMessage>> call, Response<ArrayList<SearchCheckInMessage>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL----------Message-----" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code--------Message-----------------" + response.code());

                    if (response.code() == 200) {

                        SearchCheckInMessage mCheckMessage = new SearchCheckInMessage();
                        mCheckMessage.setmAllSearch_checkIn(response.body());
                        mCheckMessage.setLocid(mLocid);
                        mSearchmCheckMessageList.add(mCheckMessage);


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<SearchCheckInMessage>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }


    private void ApiSearchViewServiceID(final int id) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        Call<ArrayList<SearchService>> call = apiService.getSearchService(id);

        call.enqueue(new Callback<ArrayList<SearchService>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchService>> call, Response<ArrayList<SearchService>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code----------Service---------------" + response.code());

                    if (response.code() == 200) {


                        SearchService mService = new SearchService();
                        mService.setmAllService(response.body());
                        mService.setLocid(id);
                        mServicesList.add(mService);


                        Config.logV("mServicesList" + mServicesList.size());

                        Config.logV("Count " + count);
                        count++;

                        if (count == mSearchLocList.size()) {


                            if (ids.size() > 0) {
                                ApiSearchViewID(mProvoderId, ids);
                            }


                        }


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<SearchService>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }


    private void ApiSearchViewID(int mProviderid, ArrayList<String> ids) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        String idPass = "";
        for (int i = 0; i < ids.size(); i++) {
            idPass += mProviderid + "-" + ids.get(i) + ",";
        }

        Config.logV("IDS_--------------------" + idPass);
        Call<ArrayList<QueueList>> call = apiService.getSearchID(idPass);

        call.enqueue(new Callback<ArrayList<QueueList>>() {
            @Override
            public void onResponse(Call<ArrayList<QueueList>> call, Response<ArrayList<QueueList>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL-------SEARCH--------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-----SearchViewID--------------------" + response.code());

                    if (response.code() == 200) {

                        mSearchQueueList = response.body();
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
                    Config.closeDialog(getActivity(), mDialog);

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
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------Setting-------------------" + response.code());

                    if (response.code() == 200) {

                        mSearchSettings = response.body();

                        Config.logV("Location Adapter-----------------------");

                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                        mRecyLocDetail.setLayoutManager(mLayoutManager);
                        mSearchLocAdapter = new SearchLocationAdapter(String.valueOf(mProvoderId), uniqueID, mInterface, mBusinessDataList.getBusinessName(), mSearchSettings, mSearchLocList, mContext, mServicesList, mSearchQueueList);
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
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }


    private void ApiSearchViewTerminology(String muniqueID) {


        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));


        Call<SearchTerminology> call = apiService.getSearchViewTerminology(Integer.parseInt(muniqueID), sdf.format(currentTime));

        call.enqueue(new Callback<SearchTerminology>() {
            @Override
            public void onResponse(Call<SearchTerminology> call, Response<SearchTerminology> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-----Terminl--------------------" + response.code());

                    if (response.code() == 200) {

                        mSearchTerminology = response.body();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<SearchTerminology> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }


    @Override
    public void onMethodWorkingCallback(ArrayList<WorkingModel> workingModel, String value) {
        WorkingHourFragment pfFragment = new WorkingHourFragment();
        // Config.logV("Fragment context-----------" + mFragment);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("workinghrlist", workingModel);
        bundle.putString("title", value);
        bundle.putString("uniqueID", uniqueID);
        pfFragment.setArguments(bundle);
        // Store the Fragment in stack
        transaction.addToBackStack(null);
        transaction.replace(R.id.mainlayout, pfFragment).commit();
    }

    @Override
    public void onMethodServiceCallback(ArrayList<SearchService> searchService, String value) {
        ServiceListFragment pfFragment = new ServiceListFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("servicelist", searchService);
        bundle.putString("title", value);
        bundle.putString("from", "searchdetail");
        bundle.putString("uniqueID", uniqueID);
        pfFragment.setArguments(bundle);
        // Store the Fragment in stack
        transaction.addToBackStack(null);
        transaction.replace(R.id.mainlayout, pfFragment).commit();
    }


}
