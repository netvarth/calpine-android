package com.netvarth.youneverwait.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.adapter.PaginationAdapter;
import com.netvarth.youneverwait.common.Config;
import com.netvarth.youneverwait.connection.ApiClient;
import com.netvarth.youneverwait.connection.ApiInterface;
import com.netvarth.youneverwait.model.SearchListModel;
import com.netvarth.youneverwait.response.QueueList;
import com.netvarth.youneverwait.response.SearchAWsResponse;
import com.netvarth.youneverwait.utils.PaginationScrollListener;
import com.netvarth.youneverwait.utils.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 20/7/18.
 */

public class SearchDetailFragment extends RootFragment {

    Context mContext;
    String query, url;
    Toolbar toolbar;
    RecyclerView mRecySearchDetail;
    // SearchDetailAdapter adapter;
    List<SearchAWsResponse> mSearchResp = new ArrayList<>();

    List<QueueList> mQueueList = new ArrayList<>();


    List<SearchListModel> mSearchListModel = new ArrayList<>();


    ProgressBar progressBar;
    LinearLayoutManager linearLayoutManager;
    PaginationAdapter pageadapter;
    private static final int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = 0;
    private int currentPage = PAGE_START;

    int total_foundcount = 0;
    Fragment searchDetail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View row = inflater.inflate(R.layout.fragment_searchdetail, container, false);

        mContext = getActivity();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            query = bundle.getString("query", "");
            url = bundle.getString("url", "");
        }


        toolbar = (Toolbar) row.findViewById(R.id.toolbar);
        mRecySearchDetail = (RecyclerView) row.findViewById(R.id.SearchDetail);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(" Search ");





        progressBar = (ProgressBar) row.findViewById(R.id.main_progress);
      //  Config.logV("Pass Fragment" + searchDetail);

        pageadapter = new PaginationAdapter(getActivity());

        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecySearchDetail.setLayoutManager(linearLayoutManager);

        mRecySearchDetail.setItemAnimator(new DefaultItemAnimator());

        mRecySearchDetail.setAdapter(pageadapter);


        mRecySearchDetail.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {


                Config.logV("Load More-----------------------");
                isLoading = true;
                Config.logV("CURRENT PAGE***************" + currentPage);
                Config.logV("CURRENT PAGE**111*************" + TOTAL_PAGES);
                currentPage += 10;

                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage(query, url);
                    }
                }, 1000);

            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        //init service and load data

        ApiSEARCHAWSLoadFirstData(query, url);


        return row;
    }


    private void loadNextPage(String mQueryPass, String mPass) {
        Log.d("", "loadNextPage: " + currentPage);

        final ApiInterface apiService =
                ApiClient.getClientAWS(mContext).create(ApiInterface.class);

       /* final Dialog mDialog = Config.getProgressDialog(getActivity(), getActivity().getResources().getString(R.string.dialog_log_in));
        mDialog.show();*/

        Map<String, String> query = new HashMap<>();

        query.put("start", String.valueOf(currentPage));
        query.put("q", mQueryPass);


        Map<String, String> params = new HashMap<>();

        params.put("size", "10");
        params.put("q.parser", "structured");
        params.put("q.sort", "distance asc,title asc");

        params.put("expr.distance", mPass);


        Call<SearchAWsResponse> call = apiService.getSearchAWS(query, params);


        call.enqueue(new Callback<SearchAWsResponse>() {
            @Override
            public void onResponse(Call<SearchAWsResponse> call, Response<SearchAWsResponse> response) {
                try {
                    /*if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);*/

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());

                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {


                        Config.logV("Response--Body AWS-------------------------" + new Gson().toJson(response.body()));

                        Config.logV("Status" + response.body().getStatus().getRid());

                        Config.logV("Found" + response.body().getHits().getFound());
                        TOTAL_PAGES = response.body().getHits().getFound() / 10;
                        if (response.body().getHits().getFound() > 0) {




                            mSearchResp.clear();
                            ArrayList<String > ids=new ArrayList<>();
                            for (int i = 0; i < response.body().getHits().getHit().size(); i++) {
                                SearchAWsResponse search = new SearchAWsResponse();
                                search.setId(response.body().getHits().getHit().get(i).getId());
                                search.setLogo(response.body().getHits().getHit().get(i).getFields().getLogo());
                                search.setSub_sector_displayname(response.body().getHits().getHit().get(i).getFields().getSub_sector_displayname());
                                search.setTitle(response.body().getHits().getHit().get(i).getFields().getTitle());
                                search.setRating(response.body().getHits().getHit().get(i).getFields().getRating());
                                search.setPlace1(response.body().getHits().getHit().get(i).getFields().getPlace1());
                                search.setUnique_id(response.body().getHits().getHit().get(i).getFields().getUnique_id());

                                if(response.body().getHits().getHit().get(i).getFields().getQualification()!=null) {
                                    search.setQualification(response.body().getHits().getHit().get(i).getFields().getQualification());

                                }


                                if(response.body().getHits().getHit().get(i).getFields().getSpecialization_displayname()!=null) {
                                    search.setSpecialization_displayname(response.body().getHits().getHit().get(i).getFields().getSpecialization_displayname());

                                }
                                if (response.body().getHits().getHit().get(i).getFields().getShow_waiting_time() != null) {
                                    search.setShow_waiting_time(response.body().getHits().getHit().get(i).getFields().getShow_waiting_time());
                                }


                                // Config.logV("response.body().getHits().getHit().get(i).getFields().toString()"+response.body().getHits().getHit().get(i).getFields().toString());
                                //search.setFound(response.body().getHits().getFound());
                                if (response.body().getHits().getHit().get(i).getFields().getServices() != null) {
                                    search.setServices(response.body().getHits().getHit().get(i).getFields().getServices());
                                }

                                if (response.body().getHits().getHit().get(i).getFields().getBusiness_hours1() != null) {
                                    search.setBusiness_hours1(response.body().getHits().getHit().get(i).getFields().getBusiness_hours1());
                                }

                                if (response.body().getHits().getHit().get(i).getFields().getOnline_checkins() != null) {
                                    search.setOnline_checkins(response.body().getHits().getHit().get(i).getFields().getOnline_checkins());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getFuture_checkins() != null) {
                                    search.setFuture_checkins(response.body().getHits().getHit().get(i).getFields().getFuture_checkins());
                                }



                                //7 types

                                if (response.body().getHits().getHit().get(i).getFields().getParking_type_location1() != null) {
                                    search.setParking_type_location1(response.body().getHits().getHit().get(i).getFields().getParking_type_location1());
                                }

                                if (response.body().getHits().getHit().get(i).getFields().getAlways_open_location1() != null) {
                                    search.setAlways_open_location1(response.body().getHits().getHit().get(i).getFields().getAlways_open_location1());
                                }

                                if (response.body().getHits().getHit().get(i).getFields().getTraumacentre_location1() != null) {
                                    search.setTraumacentre_location1(response.body().getHits().getHit().get(i).getFields().getTraumacentre_location1());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getDentistemergencyservices_location1() != null) {
                                    search.setDentistemergencyservices_location1(response.body().getHits().getHit().get(i).getFields().getDentistemergencyservices_location1());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getDocambulance_location1() != null) {
                                    search.setDocambulance_location1(response.body().getHits().getHit().get(i).getFields().getDocambulance_location1());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getPhysiciansemergencyservices_location1() != null) {
                                    search.setPhysiciansemergencyservices_location1(response.body().getHits().getHit().get(i).getFields().getPhysiciansemergencyservices_location1());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getFirstaid_location1() != null) {
                                    search.setFirstaid_location1(response.body().getHits().getHit().get(i).getFields().getFirstaid_location1());
                                }


                                mSearchResp.add(search);

                                ids.add(response.body().getHits().getHit().get(i).getId());



                            }




                           /* Config.logV("TOTAL PAGES_--------------" + TOTAL_PAGES);
                            Config.logV("CURRENT PAGE**22222**555***********" + TOTAL_PAGES);
                            Config.logV("CURRENT PAGE**333*****5555********" + currentPage);
                            pageadapter.removeLoadingFooter();
                            isLoading = false;

                            List<SearchAWsResponse> results = mSearchResp;
                            pageadapter.addAll(results);

                            if (currentPage / 10 != TOTAL_PAGES) {
                                pageadapter.addLoadingFooter();
                            } else {
                                isLastPage = true;
                            }*/

                            ApiQueueList(ids,mSearchResp,"next");
                        }

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            @Override
            public void onFailure(Call<SearchAWsResponse> call, Throwable t) {
                t.printStackTrace();
                // TODO: 08/11/16 handle failure
            }
        });
    }


   /* */

    /**
     * Performs a Retrofit call to the top rated movies API.
     * Same API call for Pagination.
     * As {@link #currentPage} will be incremented automatically
     * by @{@link PaginationScrollListener} to load next page.
     *//*
    private Call<TopRatedMovies> callTopRatedMoviesApi() {
        return movieService.getTopRatedMovies(
                getString(R.string.my_api_key),
                "en_US",
                currentPage
        );
    }*/
    private void ApiSEARCHAWSLoadFirstData(String mQueryPass, String mPass) {


        final ApiInterface apiService =
                ApiClient.getClientAWS(mContext).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(getActivity(), getActivity().getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Map<String, String> query = new HashMap<>();

        query.put("start", "0");
        query.put("q", mQueryPass);


        Map<String, String> params = new HashMap<>();

        params.put("size", "10");
        params.put("q.parser", "structured");
        params.put("q.sort", "distance asc,title asc");
        params.put("expr.distance", mPass);

        Call<SearchAWsResponse> call = apiService.getSearchAWS(query, params);


        call.enqueue(new Callback<SearchAWsResponse>() {
            @Override
            public void onResponse(Call<SearchAWsResponse> call, Response<SearchAWsResponse> response) {

                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());

                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {


                        Config.logV("Response--Body AWS-------------------------" + new Gson().toJson(response.body()));

                        Config.logV("Status" + response.body().getStatus().getRid());

                        Config.logV("Found" + response.body().getHits().getFound());
                        total_foundcount = response.body().getHits().getFound();
                        TOTAL_PAGES = response.body().getHits().getFound() / 10;
                        if (response.body().getHits().getFound() > 0) {



                            mSearchResp.clear();
                            ArrayList<String > ids=new ArrayList<>();
                            for (int i = 0; i < response.body().getHits().getHit().size(); i++) {
                                SearchAWsResponse search = new SearchAWsResponse();
                                search.setId(response.body().getHits().getHit().get(i).getId());
                                search.setLogo(response.body().getHits().getHit().get(i).getFields().getLogo());
                                search.setSub_sector_displayname(response.body().getHits().getHit().get(i).getFields().getSub_sector_displayname());
                                search.setTitle(response.body().getHits().getHit().get(i).getFields().getTitle());
                                search.setRating(response.body().getHits().getHit().get(i).getFields().getRating());
                                search.setPlace1(response.body().getHits().getHit().get(i).getFields().getPlace1());
                                search.setUnique_id(response.body().getHits().getHit().get(i).getFields().getUnique_id());

                                if(response.body().getHits().getHit().get(i).getFields().getQualification()!=null) {
                                    search.setQualification(response.body().getHits().getHit().get(i).getFields().getQualification());

                                }

                                if(response.body().getHits().getHit().get(i).getFields().getSpecialization_displayname()!=null) {
                                    search.setSpecialization_displayname(response.body().getHits().getHit().get(i).getFields().getSpecialization_displayname());

                                }
                                if (response.body().getHits().getHit().get(i).getFields().getShow_waiting_time() != null) {
                                    search.setShow_waiting_time(response.body().getHits().getHit().get(i).getFields().getShow_waiting_time());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getOnline_checkins() != null) {
                                    search.setOnline_checkins(response.body().getHits().getHit().get(i).getFields().getOnline_checkins());
                                }


                                Config.logV("UNIUE ID________________" + response.body().getHits().getHit().get(i).getFields().getUnique_id());

                                Config.logV("Search Detail--------" + response.body().getHits().getHit().get(i).getFields().getServices());
                                if (response.body().getHits().getHit().get(i).getFields().getServices() != null) {
                                    search.setServices(response.body().getHits().getHit().get(i).getFields().getServices());
                                }


                                //7 types

                                if (response.body().getHits().getHit().get(i).getFields().getBusiness_hours1() != null) {
                                    search.setBusiness_hours1(response.body().getHits().getHit().get(i).getFields().getBusiness_hours1());
                                }



                                if (response.body().getHits().getHit().get(i).getFields().getFuture_checkins() != null) {
                                    search.setFuture_checkins(response.body().getHits().getHit().get(i).getFields().getFuture_checkins());
                                }


                                if (response.body().getHits().getHit().get(i).getFields().getParking_type_location1() != null) {
                                    search.setParking_type_location1(response.body().getHits().getHit().get(i).getFields().getParking_type_location1());
                                }

                                if (response.body().getHits().getHit().get(i).getFields().getAlways_open_location1() != null) {
                                    search.setAlways_open_location1(response.body().getHits().getHit().get(i).getFields().getAlways_open_location1());
                                }

                                if (response.body().getHits().getHit().get(i).getFields().getTraumacentre_location1() != null) {
                                    search.setTraumacentre_location1(response.body().getHits().getHit().get(i).getFields().getTraumacentre_location1());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getDentistemergencyservices_location1() != null) {
                                    search.setDentistemergencyservices_location1(response.body().getHits().getHit().get(i).getFields().getDentistemergencyservices_location1());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getDocambulance_location1() != null) {
                                    search.setDocambulance_location1(response.body().getHits().getHit().get(i).getFields().getDocambulance_location1());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getPhysiciansemergencyservices_location1() != null) {
                                    search.setPhysiciansemergencyservices_location1(response.body().getHits().getHit().get(i).getFields().getPhysiciansemergencyservices_location1());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getFirstaid_location1() != null) {
                                    search.setFirstaid_location1(response.body().getHits().getHit().get(i).getFields().getFirstaid_location1());
                                }

                                ids.add(response.body().getHits().getHit().get(i).getId());

                                mSearchResp.add(search);
                            }


                           /* List<SearchAWsResponse> results = mSearchResp;
                            progressBar.setVisibility(View.GONE);
                            pageadapter.addAll(results);

                            Config.logV("CURRENT PAGE**22222*************" + TOTAL_PAGES);
                            Config.logV("CURRENT PAGE**333*************" + currentPage);
                            if (TOTAL_PAGES > 0) {
                                pageadapter.addLoadingFooter();
                            } else {
                                isLastPage = true;
                            }*/

                            ApiQueueList(ids,mSearchResp,"first");


                         //   waitlist/queues/waitingTime/2-1%2C2-2%2C141-388%2C141-2563

                        }

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<SearchAWsResponse> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }

    private void ApiQueueList(ArrayList<String> queuelist, final List<SearchAWsResponse> mSearchRespPass, final String mCheck ){

        ApiInterface apiService =
                ApiClient.getClient(getActivity()).create(ApiInterface.class);

       /* final Dialog mDialog = Config.getProgressDialog(getActivity(), getActivity().getResources().getString(R.string.dialog_log_in));
        mDialog.show();*/
        StringBuilder csvBuilder = new StringBuilder();
        for(String data : queuelist){
            csvBuilder.append(data);
            csvBuilder.append(",");
        }
        String csv = csvBuilder.toString();
        System.out.println(csv);

        Call<List<QueueList>> call = apiService.getQueueCheckReponse(csv);

        call.enqueue(new Callback<List<QueueList>>() {
            @Override
            public void onResponse(Call<List<QueueList>> call, Response<List<QueueList>> response) {

                try {

                   /* if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);*/

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("code---------------" + response.code());
                    mQueueList.clear();
                    if (response.code() == 200) {
                        Config.logV("Sucess ----------"+response.body());



                        for(int i=0;i<response.body().size();i++){
                            QueueList que=new QueueList();
                            que.setId(response.body().get(i).getProvider().getId());

                            if(response.body().get(i).getNextAvailableQueue()!=null) {
                                que.setLocation(response.body().get(i).getNextAvailableQueue().getLocation());
                              //  Config.logV("Available Time----1111---"+response.body().get(i).getNextAvailableQueue().getAvailableDate());
                                que.setAvailableDate(response.body().get(i).getNextAvailableQueue().getAvailableDate());
                                    que.setOpenNow(response.body().get(i).getNextAvailableQueue().isOpenNow());
                                if(response.body().get(i).getNextAvailableQueue().getServiceTime()!=null) {
                                    que.setServiceTime(response.body().get(i).getNextAvailableQueue().getServiceTime());
                                }

                               que.setQueueWaitingTime(response.body().get(i).getNextAvailableQueue().getQueueWaitingTime());


                            }


                            mQueueList.add(que);
                        }

                        if(mCheck.equalsIgnoreCase("next")) {

                            Config.logV("TOTAL PAGES_--------------" + TOTAL_PAGES);
                            Config.logV("CURRENT PAGE**22222**555***********" + TOTAL_PAGES);
                            Config.logV("CURRENT PAGE**333*****5555********" + currentPage);
                            pageadapter.removeLoadingFooter();
                            isLoading = false;




                            mSearchListModel.clear();
                            for(int i=0;i<mSearchRespPass.size();i++) {
                                SearchListModel searchList=new SearchListModel();
                                searchList.setId(mSearchRespPass.get(i).getId());
                                searchList.setLogo(mSearchRespPass.get(i).getLogo());
                                searchList.setPlace1(mSearchRespPass.get(i).getPlace1());
                                searchList.setSector(mSearchRespPass.get(i).getSub_sector_displayname());
                                searchList.setTitle(mSearchRespPass.get(i).getTitle());
                                searchList.setRating(mSearchRespPass.get(i).getRating());
                                searchList.setUniqueid(mSearchRespPass.get(i).getUnique_id());
                                String spec="";
                                if(mSearchRespPass.get(i).getSpecialization_displayname()!=null) {
                                   for(int l=0;l<mSearchRespPass.get(i).getSpecialization_displayname().size();l++){
                                       if(!spec.equalsIgnoreCase("")) {
                                           spec = spec + ", " + mSearchRespPass.get(i).getSpecialization_displayname().get(l);
                                       }else{
                                           spec = spec + mSearchRespPass.get(i).getSpecialization_displayname().get(l);
                                       }
                                   }
                                   searchList.setSpecialization_displayname(spec);
                                }




                                String qualify="";
                                if(mSearchRespPass.get(i).getQualification()!=null) {
                                    for(int l=0;l<mSearchRespPass.get(i).getQualification().size();l++){
                                        qualify = qualify + ", " + mSearchRespPass.get(i).getQualification().get(l);

                                    }
                                    searchList.setQualification(qualify);
                                }



                                if (mSearchRespPass.get(i).getServices() != null) {
                                    searchList.setServices(mSearchRespPass.get(i).getServices());
                                }

                                if (mSearchRespPass.get(i).getBusiness_hours1() != null) {
                                    searchList.setBusiness_hours1(mSearchRespPass.get(i).getBusiness_hours1());
                                }

                                if (mSearchRespPass.get(i).getOnline_checkins() != null) {
                                    searchList.setOnline_checkins(mSearchRespPass.get(i).getOnline_checkins());
                                }

                                if (mSearchRespPass.get(i).getFuture_checkins() != null) {
                                    searchList.setFuture_checkins(mSearchRespPass.get(i).getFuture_checkins());
                                }

                                if (mSearchRespPass.get(i).getShow_waiting_time() != null) {
                                    searchList.setShow_waiting_time(mSearchRespPass.get(i).getShow_waiting_time());
                                }


                                //7types

                                if (mSearchRespPass.get(i).getParking_type_location1() != null) {
                                    searchList.setParking_type_location1(mSearchRespPass.get(i).getParking_type_location1());
                                }

                                if (mSearchRespPass.get(i).getAlways_open_location1() != null) {
                                    searchList.setAlways_open_location1(mSearchRespPass.get(i).getAlways_open_location1());
                                }

                                if (mSearchRespPass.get(i).getTraumacentre_location1() != null) {
                                    searchList.setTraumacentre_location1(mSearchRespPass.get(i).getTraumacentre_location1());
                                }
                                if (mSearchRespPass.get(i).getDentistemergencyservices_location1() != null) {
                                    searchList.setDentistemergencyservices_location1(mSearchRespPass.get(i).getDentistemergencyservices_location1());
                                }
                                if (mSearchRespPass.get(i).getDocambulance_location1() != null) {
                                    searchList.setDocambulance_location1(mSearchRespPass.get(i).getDocambulance_location1());
                                }
                                if (mSearchRespPass.get(i).getPhysiciansemergencyservices_location1() != null) {
                                    searchList.setPhysiciansemergencyservices_location1(mSearchRespPass.get(i).getPhysiciansemergencyservices_location1());
                                }
                                if (mSearchRespPass.get(i).getFirstaid_location1() != null) {
                                    searchList.setFirstaid_location1(mSearchRespPass.get(i).getFirstaid_location1());
                                }


                                for(int j=0;j<mQueueList.size();j++) {
                                    Config.logV("mQueueList.get(j).getLocation().getId()"+mQueueList.get(j).getLocation());
                                    if (mQueueList.get(j).getLocation() != null) {

                                        String json=new Gson().toJson(mQueueList.get(j).getLocation());
                                        JSONObject json1 =new JSONObject(json);

                                        String QID=json1.getString("id");


                                        String mID = mQueueList.get(j).getId() + "-" + mQueueList.get(j).getLocation().getId();
                                        Config.logV("QID----mmm-------------------"+mID+"compare-------"+mSearchRespPass.get(i).getId());
                                        if (mID.equalsIgnoreCase(mSearchRespPass.get(i).getId())) {
                                            if (mQueueList.get(j).getAvailableDate() != null) {

                                                searchList.setAvail_date(mQueueList.get(j).getAvailableDate());
                                            }
                                            if (mQueueList.get(j).getLocation() != null) {
                                                searchList.setmLoc(QID);
                                            }
                                            //searchList.setQId(mQueueList.get(j).getId());
                                            searchList.setQId(mID);
                                            searchList.setIsopen(mQueueList.get(i).isOpenNow());
                                            searchList.setQueueWaitingTime(mQueueList.get(i).getQueueWaitingTime());
                                            if(mQueueList.get(i).getServiceTime()!=null) {
                                                searchList.setServiceTime(mQueueList.get(i).getServiceTime());
                                            }

                                        }
                                    }
                                }




                                mSearchListModel.add(searchList);
                            }



                            List<SearchListModel> results = mSearchListModel;
                            pageadapter.addAll(results);

                            if (currentPage / 10 != TOTAL_PAGES) {
                                pageadapter.addLoadingFooter();
                            } else {
                                isLastPage = true;
                            }
                        }else{


                            mSearchListModel.clear();
                            for(int i=0;i<mSearchRespPass.size();i++) {
                                SearchListModel searchList=new SearchListModel();
                                searchList.setId(mSearchRespPass.get(i).getId());
                                searchList.setLogo(mSearchRespPass.get(i).getLogo());
                                searchList.setPlace1(mSearchRespPass.get(i).getPlace1());
                                searchList.setSector(mSearchRespPass.get(i).getSub_sector_displayname());
                                searchList.setTitle(mSearchRespPass.get(i).getTitle());
                                searchList.setRating(mSearchRespPass.get(i).getRating());
                                searchList.setUniqueid(mSearchRespPass.get(i).getUnique_id());

                                String spec="";
                                if(mSearchRespPass.get(i).getSpecialization_displayname()!=null) {
                                    for(int l=0;l<mSearchRespPass.get(i).getSpecialization_displayname().size();l++){
                                        if(!spec.equalsIgnoreCase("")) {
                                            spec = spec + ", " + mSearchRespPass.get(i).getSpecialization_displayname().get(l);
                                        }else{
                                            spec = spec + mSearchRespPass.get(i).getSpecialization_displayname().get(l);
                                        }
                                    }
                                    searchList.setSpecialization_displayname(spec);
                                }


                                String qualify="";
                                if(mSearchRespPass.get(i).getQualification()!=null) {
                                    for(int l=0;l<mSearchRespPass.get(i).getQualification().size();l++){
                                            qualify = qualify + ", " + mSearchRespPass.get(i).getQualification().get(l);

                                    }
                                    searchList.setQualification(qualify);
                                }



                                if (mSearchRespPass.get(i).getServices() != null) {
                                    searchList.setServices(mSearchRespPass.get(i).getServices());
                                }

                                if (mSearchRespPass.get(i).getBusiness_hours1() != null) {
                                    searchList.setBusiness_hours1(mSearchRespPass.get(i).getBusiness_hours1());
                                }

                                if (mSearchRespPass.get(i).getOnline_checkins() != null) {
                                    searchList.setOnline_checkins(mSearchRespPass.get(i).getOnline_checkins());
                                }

                                if (mSearchRespPass.get(i).getFuture_checkins() != null) {
                                    searchList.setFuture_checkins(mSearchRespPass.get(i).getFuture_checkins());
                                }

                                if (mSearchRespPass.get(i).getShow_waiting_time() != null) {
                                    searchList.setShow_waiting_time(mSearchRespPass.get(i).getShow_waiting_time());
                                }



                                if (mSearchRespPass.get(i).getParking_type_location1() != null) {
                                    searchList.setParking_type_location1(mSearchRespPass.get(i).getParking_type_location1());
                                }

                                if (mSearchRespPass.get(i).getAlways_open_location1() != null) {
                                    searchList.setAlways_open_location1(mSearchRespPass.get(i).getAlways_open_location1());
                                }

                                if (mSearchRespPass.get(i).getTraumacentre_location1() != null) {
                                    searchList.setTraumacentre_location1(mSearchRespPass.get(i).getTraumacentre_location1());
                                }
                                if (mSearchRespPass.get(i).getDentistemergencyservices_location1() != null) {
                                    searchList.setDentistemergencyservices_location1(mSearchRespPass.get(i).getDentistemergencyservices_location1());
                                }
                                if (mSearchRespPass.get(i).getDocambulance_location1() != null) {
                                    searchList.setDocambulance_location1(mSearchRespPass.get(i).getDocambulance_location1());
                                }
                                if (mSearchRespPass.get(i).getPhysiciansemergencyservices_location1() != null) {
                                    searchList.setPhysiciansemergencyservices_location1(mSearchRespPass.get(i).getPhysiciansemergencyservices_location1());
                                }
                                if (mSearchRespPass.get(i).getFirstaid_location1() != null) {
                                    searchList.setFirstaid_location1(mSearchRespPass.get(i).getFirstaid_location1());
                                }



                                for(int j=0;j<mQueueList.size();j++) {
                                    //Config.logV("mQueueList.get(j).getLocation().getId()"+mQueueList.get(j).getLocation());
                                    if (mQueueList.get(j).getLocation() != null) {

                                        String json=new Gson().toJson(mQueueList.get(j).getLocation());
                                        JSONObject json1 =new JSONObject(json);

                                        String QID=json1.getString("id");


                                        String mID = mQueueList.get(j).getId() + "-" + mQueueList.get(j).getLocation().getId();
                                        //Config.logV("QID----mmm-------------------"+mID+"compare-------"+mSearchRespPass.get(i).getId());
                                        if (mID.equalsIgnoreCase(mSearchRespPass.get(i).getId())) {
                                            if (mQueueList.get(j).getAvailableDate() != null) {

                                                searchList.setAvail_date(mQueueList.get(j).getAvailableDate());
                                            }
                                            if (mQueueList.get(j).getLocation() != null) {
                                                searchList.setmLoc(QID);
                                            }
                                            searchList.setQId(mID);
                                            searchList.setIsopen(mQueueList.get(i).isOpenNow());
                                            searchList.setQueueWaitingTime(mQueueList.get(i).getQueueWaitingTime());
                                            if(mQueueList.get(i).getServiceTime()!=null) {
                                                searchList.setServiceTime(mQueueList.get(i).getServiceTime());
                                            }
                                        }
                                    }
                                }




                                mSearchListModel.add(searchList);
                            }





                            List<SearchListModel> results = mSearchListModel;
                            progressBar.setVisibility(View.GONE);
                            pageadapter.addAll(results);

                            Config.logV("CURRENT PAGE**22222*************" + TOTAL_PAGES);
                            Config.logV("CURRENT PAGE**333*************" + currentPage);
                            if (TOTAL_PAGES > 0&&total_foundcount>10) {
                                pageadapter.addLoadingFooter();
                            } else {
                                isLastPage = true;
                            }
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<List<QueueList>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
               /* if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);
*/
            }
        });

    }

}
