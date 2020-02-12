package com.jaldeeinc.jaldee.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.SearchServiceActivity;
import com.jaldeeinc.jaldee.adapter.DeptListAdapter;
import com.jaldeeinc.jaldee.adapter.ServicesListAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.model.DepartmentModal;
import com.jaldeeinc.jaldee.model.SearchListModel;
import com.jaldeeinc.jaldee.response.SearchDepartment;
import com.jaldeeinc.jaldee.response.SearchDepartmentServices;
import com.jaldeeinc.jaldee.response.SearchService;
import com.jaldeeinc.jaldee.response.SearchViewDetail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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


    public DeptFragment(SearchDepartmentServices departmentServices, List<SearchListModel> searchList, SearchDetailViewFragment searchDetailViewFragment, String businessName, ArrayList<SearchService> mServicesList,int department ) {
        this.msearchList = searchList;
        this.departmentServices = departmentServices;
        this.searchDetailViewFragment = searchDetailViewFragment;
        this.businessName = businessName;
        this.mServicesList = mServicesList;
        this.department = department;


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
      //  tv_providerName = row.findViewById(R.id.providerName);
        LServices = row.findViewById(R.id.Lservice);
        tv_service = row.findViewById(R.id.txtservice);

        ImageView iBackPress = (ImageView) row.findViewById(R.id.backpress);
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                getFragmentManager().popBackStack();
            }
        });

        int count = 0;
        if (msearchList != null) {
            count = msearchList.size();
        }
        tv_title.setText(this.businessName);
        tv_departmentCode.setText(departmentServices.getDepartmentCode());
        tv_departmentName.setText(departmentServices.getDepartmentName());
        tv_doctors.setText("Doctors : " + count);
      //  tv_providerName.setText(departmentServices.getDepartmentName() + " " + "(" + count + ")");
        tv_services.setText("Services >");
        tv_services.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ApiSearchViewServiceID(departments.getDepartmentId());
//                if(mServicesList != null){
//                    if(mServicesList.size() > 0){
//                        LServices.setVisibility(View.VISIBLE);
//                        for(int i = 0;i < mServicesList.size(); i++){
//                            tv_service.setVisibility(View.VISIBLE);
//                            tv_service.setText(mServicesList.get(i).getName());
//                        }
//                    }
//                }
                linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                servicesListAdapter = new ServicesListAdapter(getActivity(),mServicesList,departmentServices);
                mservice_searchresult.setAdapter(servicesListAdapter);
                mservice_searchresult.setLayoutManager(linearLayoutManager);
                servicesListAdapter.notifyDataSetChanged();







//                if(mServicesList!= null) {
//                   if (mServicesList.size() > 0) {
//                       LServices.removeAllViews();
//                        LServices.setVisibility(View.VISIBLE);
//                        int size = mServicesList.size();
//                       for (int i = 0; i < size; i++) {
//                           //TextView tv_service1 = new TextView(mContext);
//                           //Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
//                            //        "fonts/Montserrat_Regular.otf");
//                           // tv_service.setTypeface(tyface);
//                           tv_service.setText(mServicesList.get(i).getName());
//                         //  tv_service.setBackground(mContext.getResources().getDrawable(R.color.department_search_results));
//                            tv_service.setTextSize(12);
////                            tv_service.setTextColor(mContext.getResources().getColor(R.color.active_text));
//                           tv_service.setPadding(15, 10, 15, 10);
//                            //dynaText.setEllipsize(TextUtils.TruncateAt.END);
//                            tv_service.setMaxLines(1);
//                            // dynaText.setMaxEms(8);
//                           tv_service.setGravity(Gravity.CENTER);
////                           LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
////                           params.setMargins(0, 0, 20, 0);
////                          tv_service.setLayoutParams(params);
//                           if(tv_service.getParent() != null) {
//                               ((ViewGroup)tv_service.getParent()).removeView(tv_service);
//                           }
//                          LServices.addView(tv_service);
//                        }
//                   }else{
//                        mServicesList.clear();
//                        LServices.setVisibility(View.GONE);
//                   }
//
//               } else {
//                   LServices.removeAllViews();
//                   LServices.setVisibility(View.GONE);
//               }


            }

        });



        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        deptListAdapter = new DeptListAdapter(getActivity(), msearchList, searchDetailViewFragment);
        mdepartment_searchresult.setAdapter(deptListAdapter);
        mdepartment_searchresult.setLayoutManager(linearLayoutManager);
        deptListAdapter.notifyDataSetChanged();

        if (count > 0) {
            tv_services.setVisibility(View.GONE);
        } else {
            tv_services.setVisibility(View.VISIBLE);

        }


        return row;


    }
    //    private void ApiService(String uniqueID, final String serviceName, final String title) {
//        ApiInterface apiService =
//                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
//
//
//        Date currentTime = new Date();
//        final SimpleDateFormat sdf = new SimpleDateFormat(
//                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//        Call<ArrayList<SearchService>> call = apiService.getService(Integer.parseInt(uniqueID), sdf.format(currentTime));
//        call.enqueue(new Callback<ArrayList<SearchService>>() {
//            @Override
//            public void onResponse(Call<ArrayList<SearchService>> call, Response<ArrayList<SearchService>> response) {
//                try {
//
//                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
//                    Config.logV("Response--code-------------------------" + response.code());
//
//                    if (response.code() == 200) {
//
//                        SearchService service1 = null;
//                        ArrayList<SearchService> service = new ArrayList<>();
//                        service = response.body();
//                        for (int i = 0; i < service.size(); i++) {
//                            Config.logV("Response--serviceid-------------------------" + serviceName);
//                            if (service.get(i).getName().toLowerCase().equalsIgnoreCase(serviceName.toLowerCase())) {
//                                Intent iService = new Intent(mContext, SearchServiceActivity.class);
//                                iService.putExtra("name", service.get(i).getName());
//                                iService.putExtra("duration", service.get(i).getServiceDuration());
//                                iService.putExtra("price", service.get(i).getTotalAmount());
//                                iService.putExtra("desc", service.get(i).getDescription());
//                                iService.putExtra("servicegallery", service.get(i).getServicegallery());
//                                iService.putExtra("taxable", service.get(i).isTaxable());
//                                iService.putExtra("title", title);
//                                iService.putExtra("isPrePayment", service.get(i).isPrePayment());
//                                iService.putExtra("MinPrePaymentAmount", service.get(i).getMinPrePaymentAmount());
//                                iService.putExtra("department", service.get(i).getDepartment());
//                                mContext.startActivity(iService);
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ArrayList<SearchService>> call, Throwable t) {
//                // Log error here since request failed
//                Config.logV("Fail---------------" + t.toString());
//
//            }
//        });
//    }
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
