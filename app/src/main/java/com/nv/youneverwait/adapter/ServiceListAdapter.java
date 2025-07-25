package com.nv.youneverwait.adapter;

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
import com.nv.youneverwait.R;
import com.nv.youneverwait.activities.SearchServiceActivity;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.response.SearchService;

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
    String uniqueID;
    Activity activity;
    public ServiceListAdapter(ArrayList<SearchService> mServiceList, Context mContext,String from,String title,String uniqueID,Activity mActivity) {
        this.mContext = mContext;
        this.mServiceList = mServiceList;
        Config.logV("ServiceList--------------"+mServiceList.size());
        this.from=from;
        this.title=title;
        this.uniqueID=uniqueID;
        activity=mActivity;

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
                    final boolean mTaxable = serviceList.isTaxable();
                    final ArrayList<SearchService> mServiceGallery = serviceList.getServicegallery();

                    final boolean isPrepayment = serviceList.isPrePayment();
                    final String minPrepayment = serviceList.getMinPrePaymentAmount();

                    Intent iService = new Intent(v.getContext(), SearchServiceActivity.class);
                    iService.putExtra("name", mServicename);
                    iService.putExtra("duration", mServiceduration);
                    iService.putExtra("price", mServiceprice);
                    iService.putExtra("desc", mServicedesc);
                    iService.putExtra("servicegallery", mServiceGallery);
                    iService.putExtra("title", title);
                    iService.putExtra("taxable", mTaxable);
                    iService.putExtra("isPrePayment", isPrepayment);
                    iService.putExtra("MinPrePaymentAmount",minPrepayment);
                    mContext.startActivity(iService);
                }else{

                    Config.logV("Service ID pass------------"+serviceList.getName());
                    ApiService(uniqueID,serviceList.getName(),title);
                }
            }
        });


    }
    private void ApiService(String uniqueID , final String serviceName, final String title) {


        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));


        Call<ArrayList<SearchService>> call = apiService.getService(Integer.parseInt(uniqueID), sdf.format(currentTime));

        call.enqueue(new Callback<ArrayList<SearchService>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchService>> call, Response<ArrayList<SearchService>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(activity, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        SearchService service1 = null;
                        ArrayList<SearchService> service=new ArrayList<>();
                        service=response.body();
                        for(int i=0;i<service.size();i++){
                            Config.logV("Response--serviceid-------------------------" + serviceName);

                            if(service.get(i).getName().toLowerCase().equalsIgnoreCase(serviceName.toLowerCase())){
                                Intent iService = new Intent(mContext, SearchServiceActivity.class);
                                iService.putExtra("name", service.get(i).getName());
                                iService.putExtra("duration", service.get(i).getServiceDuration());
                                iService.putExtra("price", service.get(i).getTotalAmount());
                                iService.putExtra("desc", service.get(i).getDescription());
                                iService.putExtra("servicegallery", service.get(i).getServicegallery());

                                iService.putExtra("isPrePayment", service.get(i).isPrePayment());
                                iService.putExtra("MinPrePaymentAmount", service.get(i).getMinPrePaymentAmount());
                                iService.putExtra("title", title);
                                mContext.startActivity(iService);

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
                if (mDialog.isShowing())
                    Config.closeDialog(activity, mDialog);

            }
        });


    }
    @Override
    public int getItemCount() {
        return mServiceList.size();
    }
}