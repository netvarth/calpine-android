package com.netvarth.youneverwait.Fragment;

import android.content.Context;
import android.os.Bundle;
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
    String title;
    ArrayList serviceList;
    TextView tv_subtitle;
    RecyclerView mrecycle_service;
    ServiceListAdapter mAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View row = inflater.inflate(R.layout.servicelist, container, false);

        mContext = getActivity();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            title = bundle.getString("title", "");
            serviceList = (ArrayList)getArguments().getSerializable("servicelist");
        }
        mrecycle_service=(RecyclerView)row.findViewById(R.id.mrecycle_service) ;

        Config.logV("Service List---------------"+serviceList.size());

        toolbar = (Toolbar) row.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);


        TextView tv_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        tv_title.setText("Services");
        tv_subtitle=(TextView)row.findViewById(R.id.txttitle) ;
        tv_subtitle.setText(title);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mrecycle_service.setLayoutManager(mLayoutManager);
        mAdapter = new ServiceListAdapter(serviceList, mContext);
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
