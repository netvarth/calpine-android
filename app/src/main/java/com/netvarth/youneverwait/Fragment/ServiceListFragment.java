package com.netvarth.youneverwait.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.adapter.CheckIn_FamilyMemberListAdapter;
import com.netvarth.youneverwait.adapter.ServiceListAdapter;
import com.netvarth.youneverwait.common.Config;
import com.netvarth.youneverwait.response.SearchService;


import java.util.ArrayList;

/**
 * Created by sharmila on 3/9/18.
 */

public class ServiceListFragment extends RootFragment {

    public ServiceListFragment() {
        // Required empty public constructor
    }


    Context mContext;
    Toolbar toolbar;
    String title,uniqueid;
    ArrayList serviceList;
    ArrayList<SearchService> serviceList_Detail;
    TextView tv_subtitle;
    RecyclerView mrecycle_service;
    ServiceListAdapter mAdapter;
    String from;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View row = inflater.inflate(R.layout.servicelist, container, false);

        mContext = getActivity();
        Bundle bundle = this.getArguments();
        serviceList_Detail=new ArrayList<>();
        if (bundle != null) {
            title = bundle.getString("title", "");
            from = bundle.getString("from", "");
            if (from.equalsIgnoreCase("searchdetail")) {
                serviceList_Detail=(ArrayList)getArguments().getSerializable("servicelist");
                Config.logV("Service List-----11111----------"+serviceList_Detail.size());
            }else{
                serviceList = (ArrayList)getArguments().getSerializable("servicelist");
                for(int i=0;i<serviceList.size();i++){
                    SearchService data=new SearchService();
                    data.setName(serviceList.get(i).toString());
                    serviceList_Detail.add(data);
                }
            }

            uniqueid=bundle.getString("uniqueID", "");
        }
        mrecycle_service=(RecyclerView)row.findViewById(R.id.mrecycle_service) ;

        Config.logV("Service List---------------"+serviceList_Detail.size());

        toolbar = (Toolbar) row.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);


        TextView tv_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
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
        mAdapter = new ServiceListAdapter(serviceList_Detail, mContext,from,title);
        mrecycle_service.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                getFragmentManager().popBackStack();

            }
        });
        return row;
    }


}
