package com.jaldeeinc.jaldee.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.ProviderDetailActivity;
import com.jaldeeinc.jaldee.activities.SearchServiceActivity;
import com.jaldeeinc.jaldee.adapter.ServiceListAdapter;
import com.jaldeeinc.jaldee.adapter.ServiceListDetailAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.response.SearchDepartment;
import com.jaldeeinc.jaldee.response.SearchDepartmentServices;
import com.jaldeeinc.jaldee.response.SearchService;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Phani on 13/10/2020.
 */

public class ServiceListDetailsFragment extends RootFragment {

    public ServiceListDetailsFragment() {
        // Required empty public constructor
    }


    Context mContext;
    String title,uniqueid;
    ArrayList serviceList;
    ArrayList serviceIds;
    ArrayList<SearchService> serviceList_Detail;
    ArrayList<SearchService> listofserviceIds;
    TextView tv_subtitle;
    RecyclerView mrecycle_service;
    ServiceListDetailAdapter mAdapter;
    ArrayList<SearchDepartmentServices> departmentList;
    String from;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View row = inflater.inflate(R.layout.fragment_service_list_details, container, false);

        mContext = getActivity();
        Bundle bundle = this.getArguments();
        serviceList_Detail=new ArrayList<>();
        departmentList=new ArrayList<>();
        if (bundle != null) {
            title = bundle.getString("title", "");
            from = bundle.getString("from", "");
            uniqueid=bundle.getString("uniqueID", "");
            if (from.equalsIgnoreCase("searchdetail")) {
             //   serviceList_Detail=(ArrayList)getArguments().getSerializable("servicelist");
             //   departmentList = (ArrayList)getArguments().getSerializable("departmentlist");
                Config.logV("Service List-----11111----------"+serviceList_Detail.size());
            }else{
              //  serviceList = (ArrayList)getArguments().getSerializable("servicelist");
              //  serviceIds = (ArrayList)getArguments().getSerializable("serviceIds");
              //  departmentList = (ArrayList)getArguments().getSerializable("departmentlist");

                ApiService(uniqueid);

            }


        }
        mrecycle_service=(RecyclerView)row.findViewById(R.id.mrecycle_service) ;

        Config.logV("Service List---------------"+serviceList_Detail.size());

        TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);
        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface1);

        ImageView iBackPress=(ImageView)row.findViewById(R.id.backpress) ;

        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                getFragmentManager().popBackStack();
            }
        });

        tv_title.setText("Services");
        tv_subtitle=(TextView)row.findViewById(R.id.txttitle) ;
        tv_subtitle.setText(title);
        tv_subtitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//
//                SearchDetailViewFragment pfFragment = new SearchDetailViewFragment();
//
//                bundle.putString("uniqueID",uniqueid);
//                pfFragment.setArguments(bundle);
//
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                //transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_out_right, R.anim.slide_in_left);
//                // Store the Fragment in stack
//                transaction.addToBackStack(null);
//                transaction.replace(R.id.mainlayout, pfFragment).commit();
                Intent intent= new Intent(mContext, ProviderDetailActivity.class);
                intent.putExtra("uniqueID",uniqueid);
                startActivity(intent);
            }
        });


        return row;
    }

    private void ApiService(String uniqueID) {
        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Call<ArrayList<SearchService>> call = apiService.getService(Integer.parseInt(uniqueID), sdf.format(currentTime));
        call.enqueue(new Callback<ArrayList<SearchService>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchService>> call, Response<ArrayList<SearchService>> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        serviceList_Detail = response.body();
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                        mrecycle_service.setLayoutManager(mLayoutManager);
                        mAdapter = new ServiceListDetailAdapter(serviceList_Detail, mContext,from,title,uniqueid,getActivity());
                        mrecycle_service.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();

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


}
