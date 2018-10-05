package com.netvarth.youneverwait.adapter;

import android.app.Activity;
import android.app.Dialog;
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
import com.netvarth.youneverwait.connection.ApiClient;
import com.netvarth.youneverwait.connection.ApiInterface;
import com.netvarth.youneverwait.response.SearchService;
import com.netvarth.youneverwait.response.SearchSetting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                }else{

                  //  ApiService();
                }
            }
        });


    }
    private void ApiService(int uniqueID , final int serviceid) {


        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);


       /* final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();*/

        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));


        Call<ArrayList<SearchService>> call = apiService.getService(uniqueID, sdf.format(currentTime));

        call.enqueue(new Callback<ArrayList<SearchService>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchService>> call, Response<ArrayList<SearchService>> response) {

                try {

                  /*  if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);*/

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {


                        ArrayList<SearchService> service=new ArrayList<>();
                        service=response.body();
                        for(int i=0;i<service.size();i++){
                            if(service.get(i).getId()==serviceid){
                               // SearchService service1=service.get
                            }
                        }


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<SearchService>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                /*if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);*/

            }
        });


    }
    @Override
    public int getItemCount() {
        return mServiceList.size();
    }
}