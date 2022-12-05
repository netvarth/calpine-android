package com.jaldeeinc.jaldee.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.AppointmentServiceInfoDialog;
import com.jaldeeinc.jaldee.custom.UserAppServicesDialog;
import com.jaldeeinc.jaldee.response.SearchService;

import java.util.ArrayList;
import java.util.List;

public class UserAppointmentsListAdapter extends RecyclerView.Adapter<UserAppointmentsListAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<String> servicesList = new ArrayList<>();
    AppointmentServiceInfoDialog appointmentServiceInfoDialog;
    UserAppServicesDialog userAppServicesDialog;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_service;
        LinearLayout serviceList;

        public MyViewHolder(View view) {
            super(view);
            tv_service = (TextView) view.findViewById(R.id.tv_service);
            serviceList = (LinearLayout) view.findViewById(R.id.serviceList);

        }
    }

    List<SearchService> mServiceList;
    String from;
    String title;
    String uniqueID;
    Activity activity;


    public UserAppointmentsListAdapter(List<SearchService> mServiceList, Context mContext, String from, String title, String uniqueID, Activity mActivity) {
        this.mContext = mContext;
        this.mServiceList = mServiceList;
        Config.logV("ServiceList--------------" + mServiceList.size());
        this.from = from;
        this.title = title;
        this.uniqueID = uniqueID;
        activity = mActivity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.servicelist_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int position) {
        final SearchService serviceList = mServiceList.get(position);


        myViewHolder.tv_service.setText(serviceList.getName());


        SearchService appointmentServices = serviceList;

        myViewHolder.serviceList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                userAppServicesDialog = new UserAppServicesDialog(v.getContext(), appointmentServices);
                userAppServicesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                userAppServicesDialog.show();
                DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                int width = (int) (metrics.widthPixels * 1);
                userAppServicesDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

            }
        });


    }


    @Override
    public int getItemCount() {
        return mServiceList.size();
    }
}
