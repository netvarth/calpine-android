package com.jaldeeinc.jaldee.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.DeptListAdapter;
import com.jaldeeinc.jaldee.adapter.ServicesListAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.model.DepartmentUserSearchModel;
import com.jaldeeinc.jaldee.model.SearchListModel;
import com.jaldeeinc.jaldee.response.QueueList;
import com.jaldeeinc.jaldee.response.ScheduleList;
import com.jaldeeinc.jaldee.response.SearchAWsResponse;
import com.jaldeeinc.jaldee.response.SearchDepartment;
import com.jaldeeinc.jaldee.response.SearchDepartmentServices;
import com.jaldeeinc.jaldee.response.SearchLocation;
import com.jaldeeinc.jaldee.response.SearchService;
import com.jaldeeinc.jaldee.response.SearchSetting;
import com.jaldeeinc.jaldee.response.SearchViewDetail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressLint("ValidFragment")
public class DeptFragment extends RootFragment {

    Context mContext;
    SearchDepartment departments;
    SearchDepartmentServices departmentServices;
    List<SearchListModel> msearchList;
    List<SearchService> mSearchDepartmentServices;
    RecyclerView mdepartment_searchresult;
    RecyclerView mservice_searchresult;
    private DeptListAdapter deptListAdapter;
    private ServicesListAdapter servicesListAdapter;
    LinearLayoutManager linearLayoutManager;
    SearchDetailViewFragment searchDetailViewFragment;
    TextView tv_services, tv_departmentName, tv_departmentCode, tv_doctors, tv_providerName;
    String businessName, uniqueID;
    SearchListModel searchdetailList;
    ArrayList<SearchService> mServicesList;
    int department;
    LinearLayout LServices;
    TextView tv_service;
    boolean online_presence;
    boolean donationFundRaising;
    SearchViewDetail mBusinessDataList;
    List<SearchViewDetail> mBusinessDataLists = new ArrayList<SearchViewDetail>();
    SearchSetting mSearchSettings;
    SearchLocation location;
    List<SearchAWsResponse> mSearchList = new ArrayList<>();
    String lat_long;
    List<DepartmentUserSearchModel> usersSearchList = new ArrayList<>();
    Dialog mDialog;
    DepartmentUserSearchModel userSearch;
    public DeptFragment(SearchDepartmentServices departmentServices, SearchDetailViewFragment searchDetailViewFragment, String businessName ) {
        this.departmentServices = departmentServices;
        this.searchDetailViewFragment = searchDetailViewFragment;
        this.businessName = businessName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View row = inflater.inflate(R.layout.departmentview, container, false);
        mdepartment_searchresult = (RecyclerView) row.findViewById(R.id.department_searchresult);
        mservice_searchresult = (RecyclerView) row.findViewById(R.id.service_searchresult);
        TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);
        tv_services = row.findViewById(R.id.txt_services);
        tv_departmentName = row.findViewById(R.id.txtdepartment);
        tv_departmentCode = row.findViewById(R.id.txtdeptCode);
        tv_doctors = row.findViewById(R.id.txt_doctors);
        LServices = row.findViewById(R.id.Lservice);
        tv_service = row.findViewById(R.id.txtservice);
        mBusinessDataList = new SearchViewDetail();

        ImageView iBackPress = (ImageView) row.findViewById(R.id.backpress);
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                getFragmentManager().popBackStack();
            }
        });
        tv_title.setText(this.businessName);
        tv_departmentCode.setText(departmentServices.getDepartmentCode());
        tv_departmentName.setText(departmentServices.getDepartmentName());
        if (departmentServices.getUsers().size() > 0) {
            tv_doctors.setVisibility(View.VISIBLE);
            tv_doctors.setText("Doctors : " + departmentServices.getUsers().size());
            tv_services.setVisibility(View.GONE);
        } else {
            tv_doctors.setVisibility(View.GONE);
            tv_services.setVisibility(View.VISIBLE);
            tv_services.setText("Services >");
        }
        tv_services.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                mServicesList = departmentServices.getServices();
                servicesListAdapter = new ServicesListAdapter(getActivity(),mServicesList,departmentServices);
                mservice_searchresult.setAdapter(servicesListAdapter);
                mservice_searchresult.setLayoutManager(linearLayoutManager);
                servicesListAdapter.notifyDataSetChanged();
            }

        });

        ArrayList<String> idsCheckin = new ArrayList<>();
        ArrayList<String> idsAppt = new ArrayList<>();
        location = searchDetailViewFragment.mSearchLocList.get(0);
        mSearchSettings = searchDetailViewFragment.mSearchSettings;
        for (int i = 0; i < departmentServices.getUsers().size(); i++) {
            idsCheckin.add(departmentServices.getUsers().get(i).getId()+"-"+location.getId());
            idsAppt.add(searchDetailViewFragment.uniqueID+"-"+location.getId()+"-"+departmentServices.getUsers().get(i).getId());
        }
        ApiLoadQsAndSchedulesList(idsCheckin, idsAppt, mBusinessDataLists);;
        return row;
    }
    private void ApiLoadQsAndSchedulesList(final ArrayList<String> idsCheckin, final ArrayList<String> idsAppt, List<SearchViewDetail> mBusinessDataLists) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
//        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
//        mDialog.show();
        String idPass = "";
        for (int i = 0; i < idsAppt.size(); i++) {
            idPass += idsAppt.get(i) + ",";
        }
        if (!idPass.equals("") && idPass != null) {
            Config.logV("IDS_--------------------" + idPass);
            Call<ArrayList<ScheduleList>> call = apiService.getSchedule(idPass);
            call.enqueue(new Callback<ArrayList<ScheduleList>>() {
                @Override
                public void onResponse(Call<ArrayList<ScheduleList>> call, Response<ArrayList<ScheduleList>> response) {
                    try {
//                        if (mDialog.isShowing())
//                            Config.closeDialog(getActivity(), mDialog);
                        Config.logV("URL---66666----SEARCH--------" + response.raw().request().url().toString().trim());
                        Config.logV("Response--code-----SearchViewID--------------------" + response.code());
                        Config.logV("Response--code-----SearchViewID12--------------------" + new Gson().toJson(response.body()));
                        if (response.code() == 200) {
                            ArrayList<ScheduleList> mSearchScheduleList = response.body();
                            loadNextAvailableQs(idsAppt, idsCheckin, mSearchScheduleList);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<ScheduleList>> call, Throwable t) {
                    // Log error here since request failed
                    Config.logV("Fail---------------" + t.toString());
                }
            });
        }
    }
    private void loadBusinessProfile(ArrayList<String> idAppts, ArrayList<ScheduleList> mSearchScheduleList, ArrayList<QueueList> mSearchQueueList, int sIndex) {
        ApiInterface apiService =
                ApiClient.getClientS3Cloud(getActivity().getApplicationContext()).create(ApiInterface.class);
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));
        String accountId = idAppts.get(sIndex).split("-")[0];
        String userId = idAppts.get(sIndex).split("-")[2];
        Call<SearchViewDetail> call1 = apiService.getUserBusinessProfile(Integer.parseInt(accountId), Integer.parseInt(userId), sdf.format(currentTime));
        call1.enqueue(new Callback<SearchViewDetail>() {
            @Override
            public void onResponse(Call<SearchViewDetail> call, Response<SearchViewDetail> response) {
                try {
                    Config.logV("URL--7777-------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------Setting-------------------" + response.code());
                    if (response.code() == 200) {
                        SearchViewDetail businessProfile = response.body();
                        userSearch = new DepartmentUserSearchModel();
                        QueueList queuelist = mSearchQueueList.get(sIndex);
                        ScheduleList schedulelist = mSearchScheduleList.get(sIndex);
                        userSearch.setQueueList(queuelist);
                        userSearch.setScheduleList(schedulelist);
                        userSearch.setLocation(location);
                        userSearch.setSearchViewDetail(businessProfile);
                        usersSearchList.add(userSearch);
                        Log.i("ddddass", new Gson().toJson(usersSearchList));
                        if ((sIndex+1) < idAppts.size()) {
                            loadBusinessProfile(idAppts, mSearchScheduleList, mSearchQueueList, (sIndex+1));
                        } else {
                            loadUsersList();
                        }
                    }
                } catch (Exception e) {
                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SearchViewDetail> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
            }
        });
    }
    private void loadBusinessProfiles(ArrayList<QueueList> mSearchQueueList, ArrayList<ScheduleList> mSearchScheduleList, ArrayList<String> idAppts, ArrayList<String> idCheckins) {
        usersSearchList.clear();
        mDialog = Config.getProgressDialog(getActivity(), getActivity().getResources().getString(R.string.dialog_log_in));
        mDialog.show();
//        List<Observable> observableList = new ArrayList<>();
        if(idAppts.size() > 0) {
            loadBusinessProfile(idAppts, mSearchScheduleList, mSearchQueueList, 0);
        }
        //int count = 0;
        //for (int sIndex = 0; sIndex < idAppts.size(); sIndex++) {

//    }
    }
    private void loadNextAvailableQs(final ArrayList<String> idAppts, ArrayList<String> idCheckins, final ArrayList<ScheduleList> mSearchScheduleList) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        String idPass = "";
        for (int i = 0; i < idCheckins.size(); i++) {
            idPass += idCheckins.get(i) + ",";
        }
        if (!idPass.equals("") && idPass != null) {
            Config.logV("IDS_--------------------" + idPass);
            Call<ArrayList<QueueList>> call = apiService.getSearchID(idPass);
            call.enqueue(new Callback<ArrayList<QueueList>>() {
                @Override
                public void onResponse(Call<ArrayList<QueueList>> call, Response<ArrayList<QueueList>> response) {
                    final ArrayList<QueueList> mSearchQueueList;
                    try {
                        Config.logV("URL---66666----SEARCH--------" + response.raw().request().url().toString().trim());
                        Config.logV("Response--code-----SearchViewID--------------------" + response.code());
                        Config.logV("Response--code-----SearchViewID12--------------------" + new Gson().toJson(response.body()));
                        if (response.code() == 200) {
                            mSearchQueueList = response.body();
                            loadBusinessProfiles(mSearchQueueList, mSearchScheduleList, idAppts, idCheckins);
//Log.i("Account_UserId", accountId+"_"+userId);
//                                Observable<SearchViewDetail> observable = apiService.getUserBusinessProfiles(Integer.parseInt(accountId), Integer.parseInt(userId), sdf.format(currentTime));
//                                observableList.add(observable);
//                            }
//                            Log.i("Account_UserIdfff", observableList.toString());
//                            List<SearchViewDetail> result = new ArrayList<>();
//                            Observable<List<SourceObject>> source = ...

//                            Observable.fromIterable(observableList.iterator())
//                                    .flatMap(list -> observableList.iterator().next())
//                                    .subscribeOn(Schedulers.newThread())
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .subscribe(
//                                        result1 -> {
//                                            Log.i("ddddddd" , new Gson().toJson(result1));
//                                            // mBusinessDataLists.add((SearchViewDetail) result1);
//                                            loadUsersList(idAppts, idCheckins, mBusinessDataLists);
//
//                                        }
//                                    );

//Observable.zip(observableList, new ArrayList<SearchViewDetail>)
//        .subscribeOn(Schedulers.io())
//        .observeOn(AndroidSchedulers.mainThread())
//        .subscribe();

//                            Observable<ArrayList<SearchViewDetail>> testDataObservable = Observable.zip(SearchViewDetail, new Func2<ArrayList<SearchViewDetail>, ArrayList<SearchViewDetail>>() {
//                                @Override
//                                public List<SearchViewDetail> call(SearchViewDetail businessList) {
//                                    mBusinessDataLists.add(businessList);
//                                    // process data from response responseOne & responseTwo
//                                    return mBusinessDataLists;
//                                }
//                            })
//                                    .subscribeOn(Schedulers.io())
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .subscribe(new Subscriber<ArrayList<SearchViewDetail>>() {
//
//                                        @Override
//                                        public void onSubscribe(Subscription s) {
//
//                                        }
//
//                                        @Override
//                                        public void onNext(ArrayList<SearchViewDetail> testDataList) {
//
//                                        }
//
//                                        @Override
//                                        public void onCompleted() {
//                                            Log.d(TAG, "onCompleted" );
//                                            // you can show alert here or do something when completed
//                                        }
//
//                                        @Override
//                                        public void onError(Throwable t) {
//                                            Log.d(TAG, "onError Throwable: " + t.toString() );
//                                        }
//
//                                        @Override
//                                        public void onComplete() {
//
//                                        }
//                                    });
//                                    .subscribe(getObserver());
//                            Observable.zip(observableList, (i)>)
//                                    .subscribeOn(Schedulers.io())
//                                    // Be notified on the main thread
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .subscribe(getObserver());
//                                });
//

//                                    Call<SearchViewDetail> call1 = apiService.getUserBusinessProfile(Integer.parseInt(accountId), Integer.parseInt(userId), sdf.format(currentTime));
//                                    call1.enqueue(new Callback<SearchViewDetail>() {
//                                    @Override
//                                    public void onResponse(Call<SearchViewDetail> call, Response<SearchViewDetail> response) {
//                                        try {
//                                            Config.logV("URL--7777-------------" + response.raw().request().url().toString().trim());
//                                            Config.logV("Response--code------Setting-------------------" + response.code());
//                                            if (response.code() == 200) {
//                                                SearchViewDetail businessProfile = response.body();
//                                                userSearch.setSearchViewDetail(businessProfile);
//                                                usersSearchList.add(userSearch);
//                                            }
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onFailure(Call<SearchViewDetail> call, Throwable t) {
//                                        // Log error here since request failed
//                                        Config.logV("Fail---------------" + t.toString());
//                                    }
//                                });
//                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<QueueList>> call, Throwable t) {
                    // Log error here since request failed
                    Config.logV("Fail---------------" + t.toString());
//                    if (mDialog.isShowing())
//                        Config.closeDialog(getActivity(), mDialog);
                }
            });
        }
    }

    private void loadUsersList() {
        if (mDialog.isShowing())
            Config.closeDialog(getActivity(), mDialog);
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        deptListAdapter = new DeptListAdapter(getActivity(), usersSearchList, searchDetailViewFragment);
        mdepartment_searchresult.setAdapter(deptListAdapter);
        mdepartment_searchresult.setLayoutManager(linearLayoutManager);
        deptListAdapter.notifyDataSetChanged();
    }

    private void ApiSearchViewServiceID(final int department) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<ArrayList<SearchService>> call = apiService.getSearchService(departments.getDepartmentId());
        call.enqueue(new Callback<ArrayList<SearchService>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchService>> call, Response<ArrayList<SearchService>> response) {
                try {


                    Config.logV("URL---5555------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code----------Service---------------" + response.code());
                    if (response.code() == 200) {
                        SearchService mService = new SearchService();
                        mService.setmAllService(response.body());
                        mService.setDepartment(department);
                        mServicesList.add(mService);
//                        ApiServicesGroupbyDepartment(mServicesList);
                        Config.logV("mServicesList @@@@" + response.body().size());
                        Config.logV("mServicesList" + mServicesList.size());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<SearchService>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());

            }
        });

    }
}
