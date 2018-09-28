package com.netvarth.youneverwait.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.activities.SearchServiceActivity;
import com.netvarth.youneverwait.common.Config;
import com.netvarth.youneverwait.response.SearchService;

import java.util.ArrayList;

/**
 * Created by sharmila on 3/9/18.
 */

public class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.MyViewHolder> {
    Context mContext;
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_service;
        LinearLayout serviceList;
        public MyViewHolder(View view) {
            super(view);
            tv_service = (TextView) view.findViewById(R.id.tv_service);
            serviceList=(LinearLayout)view.findViewById(R.id.serviceList);

        }
    }
    ArrayList<SearchService> mServiceList;
    String from;
    String title;
    public ServiceListAdapter(ArrayList<SearchService> mServiceList, Context mContext,String from,String title) {
        this.mContext = mContext;
        this.mServiceList = mServiceList;
        Config.logV("ServiceList--------------"+mServiceList.size());
        this.from=from;
        this.title=title;

    }

    @Override
    public ServiceListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.servicelist_row, parent, false);

        return new ServiceListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ServiceListAdapter.MyViewHolder myViewHolder, final int position) {
        final SearchService serviceList = mServiceList.get(position);

        myViewHolder.tv_service.setText(serviceList.getName());
        myViewHolder.serviceList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(from.equalsIgnoreCase("searchdetail")) {
                    final String mServicename = serviceList.getName();
                    final String mServiceprice = serviceList.getTotalAmount();
                    final String mServicedesc = serviceList.getDescription();
                    final String mServiceduration = serviceList.getServiceDuration();
                    final ArrayList<SearchService> mServiceGallery = serviceList.getServicegallery();

                    Intent iService = new Intent(v.getContext(), SearchServiceActivity.class);
                    iService.putExtra("name", mServicename);
                    iService.putExtra("duration", mServiceduration);
                    iService.putExtra("price", mServiceprice);
                    iService.putExtra("desc", mServicedesc);
                    iService.putExtra("servicegallery", mServiceGallery);
                    iService.putExtra("title", title);
                    mContext.startActivity(iService);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mServiceList.size();
    }
}