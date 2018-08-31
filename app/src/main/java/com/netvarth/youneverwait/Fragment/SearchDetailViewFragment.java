package com.netvarth.youneverwait.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.activities.SwipeGalleryImage;
import com.netvarth.youneverwait.adapter.SearchLocationAdapter;
import com.netvarth.youneverwait.common.Config;
import com.netvarth.youneverwait.connection.ApiClient;
import com.netvarth.youneverwait.connection.ApiInterface;
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


public class SearchDetailViewFragment extends AppCompatActivity {

    Context mContext;
    Toolbar toolbar;
    SearchViewDetail mBusinessDataList = new SearchViewDetail();
    ArrayList<SearchViewDetail> mSearchGallery = new ArrayList<>();
    ArrayList<SearchLocation> mSearchLocList = new ArrayList<>();

    SearchSetting mSearchSettings = new SearchSetting();
    SearchTerminology mSearchTerminology = new SearchTerminology();

    ArrayList<QueueList> mSearchQueueList= new ArrayList<>();

    ArrayList<SearchService> mServicesList = new ArrayList<>();

    ArrayList<SearchCheckInMessage> mSearchmCheckMessageList = new ArrayList<>();



    TextView tv_busName, tv_domain, tv_desc, tv_exp;

    RecyclerView mRecyLocDetail;
    SearchLocationAdapter mSearchLocAdapter;
    ImageView mImgeProfile, mImgthumbProfile;

    int mProvoderId;
    ArrayList<String> ids = new ArrayList<>();
    String uniqueID;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchdetails);
        mContext = this;
        mRecyLocDetail = (RecyclerView) findViewById(R.id.mSearchLoc);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(" Search ");
        Bundle bundle = getIntent().getExtras();
         uniqueID = bundle.getString("uniqueID");
        Config.logV("UNIUE ID---------1111-------" + uniqueID);
        tv_busName = (TextView) findViewById(R.id.txtbus_name);
        tv_domain = (TextView) findViewById(R.id.txt_domain);
        mImgeProfile = (ImageView) findViewById(R.id.i_profile);
        mImgthumbProfile = (ImageView) findViewById(R.id.iThumb_profile);
        tv_exp = (TextView) findViewById(R.id.txt_expe);
        tv_desc = (TextView) findViewById(R.id.txt_bus_desc);

        ApiSearchViewDetail(uniqueID);
        ApiSearchGallery(uniqueID);
        ApiSearchViewLocation(uniqueID);

        ApiSearchViewTerminology(uniqueID);
    }


    public void UpdateGallery(final ArrayList<SearchViewDetail> mGallery){
        Picasso.with(this).load(mGallery.get(0).getUrl()).fit().into(mImgeProfile);

        if (mGallery.size() > 1) {
            mImgthumbProfile.setVisibility(View.VISIBLE);
            Picasso.with(this).load(mGallery.get(1).getUrl()).fit().into(mImgthumbProfile);
            mImgthumbProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
            mImgthumbProfile.setVisibility(View.GONE);
        }
    }
    public void UpdateMainUI(SearchViewDetail getBussinessData) {
        tv_busName.setText(getBussinessData.getBusinessName());

        tv_domain.setText(getBussinessData.getServiceSector().getDisplayName());

        if (getBussinessData.getDomainVirtualFields()!=null) {
            if (getBussinessData.getDomainVirtualFields().getExperience() != null) {

                tv_exp.setVisibility(View.VISIBLE);
                tv_exp.setText(getBussinessData.getDomainVirtualFields().getExperience());
            } else {
                tv_exp.setVisibility(View.GONE);
            }

        }else{
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
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        mBusinessDataList = response.body();
                        mProvoderId=response.body().getId();
                        UpdateMainUI(mBusinessDataList);


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
                    Config.closeDialog(getParent(), mDialog);

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
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

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
                    Config.closeDialog(getParent(), mDialog);

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
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    mSearchLocList.clear();

                    if (response.code() == 200) {

                        mSearchLocList = response.body();
                        for(int i=0;i<response.body().size();i++){
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
                    Config.closeDialog(getParent(), mDialog);

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
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL----------Location-----" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

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
                    Config.closeDialog(getParent(), mDialog);

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
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {


                        SearchService mService = new SearchService();
                        mService.setmAllService(response.body());
                        mService.setLocid(id);
                        mServicesList.add(mService);


                        Config.logV("mServicesList" + mServicesList.size());


                        count++;

                        if (count == mSearchLocList.size()) {


                            if(ids.size()>0){
                                ApiSearchViewID(mProvoderId,ids);
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
                    Config.closeDialog(getParent(), mDialog);

            }
        });


    }




    private void ApiSearchViewID( int mProviderid,ArrayList<String> ids) {


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
                        Config.closeDialog(getParent(), mDialog);

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
                    Config.closeDialog(getParent(), mDialog);

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
                        Config.closeDialog(getParent(), mDialog);

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
                    Config.closeDialog(getParent(), mDialog);

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
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        mSearchTerminology=response.body();
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
                    Config.closeDialog(getParent(), mDialog);

            }
        });


    }


}
