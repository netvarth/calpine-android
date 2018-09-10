package com.netvarth.youneverwait.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.common.Config;

import java.util.ArrayList;

/**
 * Created by sharmila on 3/9/18.
 */

public class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.MyViewHolder> {
    Context mContext;
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_service;
        public MyViewHolder(View view) {
            super(view);
            tv_service = (TextView) view.findViewById(R.id.tv_service);

        }
    }
    ArrayList mServiceList;
    public ServiceListAdapter(ArrayList mServiceList, Context mContext) {
        this.mContext = mContext;
        this.mServiceList = mServiceList;
        Config.logV("ServiceList--------------"+mServiceList.size());

    }

    @Override
    public ServiceListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.servicelist_row, parent, false);

        return new ServiceListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ServiceListAdapter.MyViewHolder myViewHolder, final int position) {
        final String serviceList = mServiceList.get(position).toString();

        myViewHolder.tv_service.setText(serviceList);


    }

    @Override
    public int getItemCount() {
        return mServiceList.size();
    }
}