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
import com.jaldeeinc.jaldee.callback.AdapterCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.model.DepartmentUserSearchModel;
import com.jaldeeinc.jaldee.model.ProviderUserModel;
import com.jaldeeinc.jaldee.model.SearchListModel;
import com.jaldeeinc.jaldee.model.WorkingModel;
import com.jaldeeinc.jaldee.response.QueueList;
import com.jaldeeinc.jaldee.response.ScheduleList;
import com.jaldeeinc.jaldee.response.SearchAWsResponse;
import com.jaldeeinc.jaldee.response.SearchAppointmentDepartmentServices;
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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressLint("ValidFragment")
public class DeptFragment extends RootFragment implements AdapterCallback {

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
    SearchViewDetail mBusinessDataListParent;
    List<SearchViewDetail> mBusinessDataLists = new ArrayList<SearchViewDetail>();
    ArrayList<QueueList> mQueueLists = new ArrayList<>();
    SearchSetting mSearchSettings;
    SearchLocation location;
    List<SearchAWsResponse> mSearchList = new ArrayList<>();
    String lat_long;
    List<DepartmentUserSearchModel> usersSearchList = new ArrayList<>();
    Dialog mDialog;
    DepartmentUserSearchModel userSearch;
    Boolean firstCouponAvailable, couponAvailable;
    Boolean fromDoctors = false;
    ArrayList<ProviderUserModel> usersList;
    ArrayList<SearchLocation> mSearchLocList;
    ArrayList<SearchAppointmentDepartmentServices> userAppointmentServices = new ArrayList<>();
    ArrayList<SearchService> individualUserAppointmentServices = new ArrayList<>();

    public DeptFragment() {
    }


    public DeptFragment(SearchDepartmentServices departmentServices, SearchDetailViewFragment searchDetailViewFragment, String businessName, SearchViewDetail mBusinessDataListParent, Boolean firstCouponAvailable, Boolean couponAvailable, ArrayList<SearchLocation> searchLocation, SearchSetting mSearchSettings) {
        this.departmentServices = departmentServices;
        this.searchDetailViewFragment = searchDetailViewFragment;
        this.businessName = businessName;
        this.mBusinessDataListParent = mBusinessDataListParent;
        this.couponAvailable = couponAvailable;
        this.firstCouponAvailable = firstCouponAvailable;
        this.mSearchLocList = searchLocation;
        this.mSearchSettings = mSearchSettings;

    }

    public DeptFragment(ArrayList<ProviderUserModel> usersList, SearchDetailViewFragment searchDetailViewFragment, String businessName, SearchViewDetail mBusinessDataList, Boolean firstCouponAvailable, Boolean couponAvailable, ArrayList<SearchLocation> searchLocation, SearchSetting mSearchSettings, Boolean fromDoctors) {
        this.usersList = usersList;
        this.searchDetailViewFragment = searchDetailViewFragment;
        this.businessName = businessName;
        mBusinessDataListParent = mBusinessDataList;
        this.firstCouponAvailable = firstCouponAvailable;
        this.couponAvailable = couponAvailable;
        this.mSearchLocList = searchLocation;
        this.mSearchSettings = mSearchSettings;
        this.fromDoctors = fromDoctors;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View row = inflater.inflate(R.layout.departmentview, container, false);
        mContext = getActivity();
        mdepartment_searchresult = (RecyclerView) row.findViewById(R.id.department_searchresult);
        mservice_searchresult = (RecyclerView) row.findViewById(R.id.service_searchresult);
        TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);
        tv_services = row.findViewById(R.id.txt_services);
        tv_departmentName = row.findViewById(R.id.txtdepartment);
        tv_departmentCode = row.findViewById(R.id.txtdeptCode);
        tv_doctors = row.findViewById(R.id.txt_doctors);
        LServices = row.findViewById(R.id.Lservice);
        tv_service = row.findViewById(R.id.txtservice);
//        mBusinessDataList = new SearchViewDetail();

        ImageView iBackPress = (ImageView) row.findViewById(R.id.backpress);
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                getFragmentManager().popBackStack();
            }
        });

        tv_title.setText(businessName);

        if (fromDoctors) {
            tv_departmentName.setVisibility(View.GONE);
            tv_departmentCode.setVisibility(View.GONE);
            tv_services.setVisibility(View.GONE);
            tv_doctors.setVisibility(View.GONE);
        } else {
            if (departmentServices != null) {
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
                        servicesListAdapter = new ServicesListAdapter(getActivity(), mServicesList, departmentServices);
                        mservice_searchresult.setAdapter(servicesListAdapter);
                        mservice_searchresult.setLayoutManager(linearLayoutManager);
                        servicesListAdapter.notifyDataSetChanged();
                    }

                });

            }
        }

        try {

            ArrayList<String> idsCheckin = new ArrayList<>();
            ArrayList<String> idsAppt = new ArrayList<>();
            if (mSearchLocList != null && mSearchLocList.size() > 0) {
                location = mSearchLocList.get(0);
            }

            if (fromDoctors) {
                for (int i = 0; i < usersList.size(); i++) {
                    idsCheckin.add(usersList.get(i).getId() + "-" + location.getId());
                    idsAppt.add(searchDetailViewFragment.mProviderId + "-" + location.getId() + "-" + usersList.get(i).getId());
                }
            } else {
                for (int i = 0; i < departmentServices.getUsers().size(); i++) {
                    idsCheckin.add(departmentServices.getUsers().get(i).getId() + "-" + location.getId());
                    idsAppt.add(searchDetailViewFragment.mProviderId + "-" + location.getId() + "-" + departmentServices.getUsers().get(i).getId());
                }
            }

            ApiLoadQsAndSchedulesList(idsCheckin, idsAppt, mBusinessDataLists);
            return row;
        } catch (Exception e) {

            return null;
        }
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
//                        Config.logV("Response--code-----SearchViewID12--------------------" + new Gson().toJson(response.body()));
                        if (response.code() == 200) {
                            ArrayList<ScheduleList> mSearchScheduleList = response.body();
                            mQueueLists.clear();
                            loadNextAvailableQs(idsAppt, idsCheckin, mSearchScheduleList, 0);
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
        Call<SearchViewDetail> call1 = apiService.getUserBusinessProfile(Integer.parseInt(searchDetailViewFragment.uniqueID), Integer.parseInt(userId), sdf.format(currentTime));
        call1.enqueue(new Callback<SearchViewDetail>() {
            @Override
            public void onResponse(Call<SearchViewDetail> call, Response<SearchViewDetail> response) {
                try {
                    Config.logV("URL--7777-------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------Setting-------------------" + response.code());
                    if (response.code() == 200) {
                        SearchViewDetail businessProfile = response.body();
                        mBusinessDataLists.add(businessProfile);
                        if ((sIndex + 1) < idAppts.size()) {
                            loadBusinessProfile(idAppts, mSearchScheduleList, mSearchQueueList, (sIndex + 1));
                        } else {
                            if (fromDoctors) {
                                loadIndividualUserAppointmentServices(idAppts,0);
                                loadIndividualUserServices(idAppts, mSearchScheduleList, mSearchQueueList, mBusinessDataLists, 0);

                            } else {
                                loadUserAppointmentServices(idAppts,0);
                                loadUserServices(idAppts, mSearchScheduleList, mSearchQueueList, mBusinessDataLists, 0);
                            }
                        }
                    }
//                    else if (response.code() == 404){
//
//                        loadBusinessProfile(idAppts, mSearchScheduleList, mSearchQueueList, (sIndex + 1));
//
//                    }
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

    private void loadUserServices(ArrayList<String> idAppts, ArrayList<ScheduleList> mSearchScheduleList, ArrayList<QueueList> mSearchQueueList, List<SearchViewDetail> mBusinessDataLists, int sIndex) {
        ApiInterface apiService =
                ApiClient.getClientS3Cloud(getActivity().getApplicationContext()).create(ApiInterface.class);
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));
        String accountId = idAppts.get(sIndex).split("-")[0];
        String userId = idAppts.get(sIndex).split("-")[2];
        Call<ArrayList<SearchDepartmentServices>> calluser = apiService.getDepartmentServices(Integer.parseInt(searchDetailViewFragment.uniqueID), Integer.parseInt(userId), sdf.format(currentTime));
        calluser.enqueue(new Callback<ArrayList<SearchDepartmentServices>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchDepartmentServices>> call, Response<ArrayList<SearchDepartmentServices>> response) {
                userSearch = new DepartmentUserSearchModel();
                QueueList queuelist = mSearchQueueList.get(sIndex);
                ScheduleList schedulelist = mSearchScheduleList.get(sIndex);
                SearchViewDetail businessProfile = mBusinessDataLists.get(sIndex);
                userSearch.setQueueList(queuelist);
                userSearch.setScheduleList(schedulelist);
                userSearch.setLocation(location);
                userSearch.setSettings(searchDetailViewFragment.mSearchSettings);
                userSearch.setSearchViewDetail(businessProfile);
                userSearch.setParentSearchViewDetail(mBusinessDataListParent);
                try {
                    Config.logV("URL--7777-------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------Setting-------------------" + response.code());
                    if (response.code() == 200) {
                        ArrayList<SearchDepartmentServices> serviceList = response.body();
                        if (serviceList.size() > 0) {
                            userSearch.setServices(serviceList.get(sIndex).getServices());
                            userSearch.setDepartmentId(serviceList.get(sIndex).getDepartmentId());
                        } else {
                            userSearch.setServices(null);
                        }
                        if(userAppointmentServices.size()>0){
                            userSearch.setAppointmentServices(userAppointmentServices.get(sIndex).getServices());
                        }
                        else{
                            userSearch.setAppointmentServices(null);
                        }
                        usersSearchList.add(userSearch);
                        if ((sIndex + 1) < idAppts.size()) {
                            loadUserAppointmentServices(idAppts,sIndex + 1);
                            loadUserServices(idAppts, mSearchScheduleList, mSearchQueueList, mBusinessDataLists, (sIndex + 1));
                        } else {
                            loadUsersList();
                        }
                    } else if (response.code() == 404) {


                    }
                } catch (Exception e) {
                    userSearch.setServices(null);
                    usersSearchList.add(userSearch);
                    if ((sIndex + 1) < idAppts.size()) {
                        loadUserAppointmentServices(idAppts,sIndex + 1);
                        loadUserServices(idAppts, mSearchScheduleList, mSearchQueueList, mBusinessDataLists, (sIndex + 1));
                    } else {
                        loadUsersList();
                    }
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<SearchDepartmentServices>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
            }
        });
    }


    private void loadUserAppointmentServices(ArrayList<String> idAppts,int sIndex) {
        ApiInterface apiService =
                ApiClient.getClientS3Cloud(getActivity().getApplicationContext()).create(ApiInterface.class);
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));
        String accountId = idAppts.get(sIndex).split("-")[0];
        String userId = idAppts.get(sIndex).split("-")[2];
        Call<ArrayList<SearchAppointmentDepartmentServices>> calluser = apiService.getAppointmentServices(Integer.parseInt(searchDetailViewFragment.uniqueID), Integer.parseInt(userId), sdf.format(currentTime));
        calluser.enqueue(new Callback<ArrayList<SearchAppointmentDepartmentServices>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchAppointmentDepartmentServices>> call, Response<ArrayList<SearchAppointmentDepartmentServices>> response) {

                try {
                    Config.logV("URL--7777-------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------Setting-------------------" + response.code());
                    if (response.code() == 200) {
                        userAppointmentServices = response.body();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<SearchAppointmentDepartmentServices>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
            }
        });
    }

    private void loadIndividualUserAppointmentServices(ArrayList<String> idAppts,int sIndex) {
        ApiInterface apiService =
                ApiClient.getClientS3Cloud(getActivity().getApplicationContext()).create(ApiInterface.class);
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));
        String accountId = idAppts.get(sIndex).split("-")[0];
        String userId = idAppts.get(sIndex).split("-")[2];
        Call<ArrayList<SearchService>> calluser = apiService.getUserAppointmentServices(Integer.parseInt(searchDetailViewFragment.uniqueID), Integer.parseInt(userId), sdf.format(currentTime));
        calluser.enqueue(new Callback<ArrayList<SearchService>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchService>> call, Response<ArrayList<SearchService>> response) {

                try {
                    Config.logV("URL--7777-------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------Setting-------------------" + response.code());
                    if (response.code() == 200) {
                       individualUserAppointmentServices = response.body();

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

    private void loadBusinessProfiles(ArrayList<QueueList> mSearchQueueList, ArrayList<ScheduleList> mSearchScheduleList, ArrayList<String> idAppts, ArrayList<String> idCheckins) {
        usersSearchList.clear();
        mDialog = Config.getProgressDialog(getActivity(), mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        if (idAppts.size() > 0) {
            loadBusinessProfile(idAppts, mSearchScheduleList, mSearchQueueList, 0);
        }
    }


    private void loadNextAvailableQs(final ArrayList<String> idAppts, ArrayList<String> idCheckins, final ArrayList<ScheduleList> mSearchScheduleList, int sIndex) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
//        String idPass = "";
//        for (int i = 0; i < idCheckins.size(); i++) {
//            idPass += idCheckins.get(i) + ",";
//        }
//        if (!idPass.equals("") && idPass != null) {
//            Config.logV("IDS_--------------------" + idPass);
//            Call<ArrayList<QueueList>> call = apiService.getProviderAvailableQTime(idPass);
        Call<ArrayList<QueueList>> call = apiService.getProviderAvailableQTime(idCheckins.get(sIndex));
        call.enqueue(new Callback<ArrayList<QueueList>>() {
            @Override
            public void onResponse(Call<ArrayList<QueueList>> call, Response<ArrayList<QueueList>> response) {
                final ArrayList<QueueList> mSearchQueueList;
                try {
                    Config.logV("URL---66666----SEARCH--------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-----SearchViewID--------------------" + response.code());
//                        Config.logV("Response--code-----SearchViewID12--------------------" + new Gson().toJson(response.body()));
                    if (response.code() == 200) {
                        mSearchQueueList = response.body();
                        mQueueLists.add(mSearchQueueList.get(0));
                        if ((sIndex + 1) < idCheckins.size()) {
                            loadNextAvailableQs(idAppts, idCheckins, mSearchScheduleList, (sIndex + 1));
                        } else {
                            loadBusinessProfiles(mQueueLists, mSearchScheduleList, idAppts, idCheckins);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<QueueList>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
            }
        });
//        }
    }

    private void loadUsersList() {
        if (mDialog.isShowing())
            Config.closeDialog(getActivity(), mDialog);
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        deptListAdapter = new DeptListAdapter(getActivity(), usersSearchList, searchDetailViewFragment, firstCouponAvailable, couponAvailable, this);
        mdepartment_searchresult.setAdapter(deptListAdapter);
        mdepartment_searchresult.setLayoutManager(linearLayoutManager);
        deptListAdapter.notifyDataSetChanged();
    }

    private void loadIndividualUserServices(ArrayList<String> idAppts, ArrayList<ScheduleList> mSearchScheduleList, ArrayList<QueueList> mSearchQueueList, List<SearchViewDetail> mBusinessDataLists, int sIndex) {
        ApiInterface apiService =
                ApiClient.getClientS3Cloud(getActivity().getApplicationContext()).create(ApiInterface.class);
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));
        String accountId = idAppts.get(sIndex).split("-")[0];
        String userId = idAppts.get(sIndex).split("-")[2];
        Call<ArrayList<SearchService>> calluser = apiService.getUserServices(Integer.parseInt(searchDetailViewFragment.uniqueID), Integer.parseInt(userId), sdf.format(currentTime));
        calluser.enqueue(new Callback<ArrayList<SearchService>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchService>> call, Response<ArrayList<SearchService>> response) {
                userSearch = new DepartmentUserSearchModel();
                QueueList queuelist = mSearchQueueList.get(sIndex);
                ScheduleList schedulelist = mSearchScheduleList.get(sIndex);
                SearchViewDetail businessProfile = mBusinessDataLists.get(sIndex);
                userSearch.setQueueList(queuelist);
                userSearch.setScheduleList(schedulelist);
                userSearch.setLocation(location);
                userSearch.setSettings(searchDetailViewFragment.mSearchSettings);
                userSearch.setSearchViewDetail(businessProfile);
                userSearch.setParentSearchViewDetail(mBusinessDataListParent);
                try {
                    Config.logV("URL--7777-------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------Setting-------------------" + response.code());
                    if (response.code() == 200) {
                        ArrayList<SearchService> serviceList = response.body();
                        if (serviceList.size() > 0) {
                            userSearch.setServices(serviceList);
                        } else {
                            userSearch.setServices(null);
                        }
                        if(individualUserAppointmentServices.size()>0){
                            userSearch.setUserAppointmentServices(individualUserAppointmentServices);
                        }
                        else{
                            userSearch.setUserAppointmentServices(null);
                        }

                        usersSearchList.add(userSearch);
                        if ((sIndex + 1) < idAppts.size()) {
                            loadIndividualUserAppointmentServices(idAppts,(sIndex+1));
                            loadIndividualUserServices(idAppts, mSearchScheduleList, mSearchQueueList, mBusinessDataLists, (sIndex + 1));
                        } else {
                            loadUsersList();
                        }
                    } else if (response.code() == 404) {


                    }
                } catch (Exception e) {
                    userSearch.setServices(null);
                    usersSearchList.add(userSearch);
                    if ((sIndex + 1) < idAppts.size()) {
                        loadIndividualUserServices(idAppts, mSearchScheduleList, mSearchQueueList, mBusinessDataLists, (sIndex + 1));
                        loadIndividualUserAppointmentServices(idAppts,(sIndex+1));
                    } else {
                        loadUsersList();
                    }
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

    @Override
    public void onMethodCallback(String value, String claimable) {

    }

    @Override
    public void onMethodWorkingCallback(ArrayList<WorkingModel> workingModel, String value, String UniqueID) {

    }

    @Override
    public void onMethodServiceCallback(ArrayList services, String value, String uniqueID) {

    }

    @Override
    public void onMethodOpenMap(String location) {

    }

    @Override
    public void onMethodMessage(String provider, String accountID, String from) {

    }

    @Override
    public void onMethodCoupn(String uniqueID) {

    }

    @Override
    public void onMethodJaldeeLogo(String ynw_verified, String provider) {

    }

    @Override
    public void onMethodFilterRefined(String passformula, RecyclerView recyclepopup, String domainame) {

    }

    @Override
    public void onMethodSubDomainFilter(String passformula, RecyclerView recyclepopup, String subdomainame, String domainName, String displayNameSubdomain) {

    }

    @Override
    public void onMethodQuery(ArrayList<String> formula, ArrayList<String> key) {

    }

    @Override
    public void onMethodFirstCoupn(String uniqueid) {

    }

    @Override
    public void onMethodJdn(String uniqueid) {

    }

    @Override
    public void onMethodSpecialization(ArrayList Specialization_displayname, String title) {

        SpecializationFragment specialFragment = new SpecializationFragment();
        Bundle bundle = new Bundle();
        bundle.putString("businessName", businessName);
        bundle.putStringArrayList("Specialization_displayname", Specialization_displayname);
        specialFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        // Store the Fragment in stack
        transaction.addToBackStack(null);
        transaction.replace(R.id.mainlayout, specialFragment).commit();
    }

    @Override
    public void onMethodDepartmentList(ArrayList<String> Departments, String businessName) {

    }

    @Override
    public void onMethodForceUpdate() {

    }
}
