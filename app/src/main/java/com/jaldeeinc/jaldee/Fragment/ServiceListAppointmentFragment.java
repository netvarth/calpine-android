package com.jaldeeinc.jaldee.Fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.ServiceListAppointmentAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.response.SearchAppointmentDepartmentServices;
import com.jaldeeinc.jaldee.response.SearchDepartment;


import java.util.ArrayList;

import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by sharmila on 3/9/18.
 */

public class ServiceListAppointmentFragment extends RootFragment {

    public ServiceListAppointmentFragment() {
        // Required empty public constructor
    }


    Context mContext;
    String title,uniqueid;
    ArrayList serviceList;
    ArrayList<SearchAppointmentDepartmentServices> serviceList_Detail;
    TextView tv_subtitle;
    RecyclerView mrecycle_service;
    ServiceListAppointmentAdapter mAdapter;
    ArrayList<SearchDepartment> departmentList;
    String from;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View row = inflater.inflate(R.layout.servicelist, container, false);

        mContext = getActivity();
        Bundle bundle = this.getArguments();
        serviceList_Detail=new ArrayList<>();
        departmentList=new ArrayList<>();
        if (bundle != null) {
            title = bundle.getString("title", "");
            from = bundle.getString("from", "");
            if (from.equalsIgnoreCase("searchdetail")) {
                serviceList_Detail=(ArrayList)getArguments().getSerializable("servicelist");
                departmentList = (ArrayList)getArguments().getSerializable("departmentlist");
                Config.logV("Service List-----11111----------"+serviceList_Detail.size());
            }else{
                serviceList = (ArrayList)getArguments().getSerializable("servicelist");
                departmentList = (ArrayList)getArguments().getSerializable("departmentlist");
                for(int i=0;i<serviceList.size();i++){
                    SearchAppointmentDepartmentServices data = new SearchAppointmentDepartmentServices();
                    data.setName(serviceList.get(i).toString());
                    serviceList_Detail.add(data);
                }
            }

            uniqueid=bundle.getString("uniqueID", "");
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
                Bundle bundle = new Bundle();

                SearchDetailViewFragment pfFragment = new SearchDetailViewFragment();

                bundle.putString("uniqueID",uniqueid);
                pfFragment.setArguments(bundle);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                //transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_out_right, R.anim.slide_in_left);
                // Store the Fragment in stack
                transaction.addToBackStack(null);
                transaction.replace(R.id.mainlayout, pfFragment).commit();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mrecycle_service.setLayoutManager(mLayoutManager);
        mAdapter = new ServiceListAppointmentAdapter(serviceList_Detail, mContext,from,title,uniqueid,getActivity(), departmentList);
        mrecycle_service.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        return row;
    }


}

