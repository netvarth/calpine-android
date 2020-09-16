package com.jaldeeinc.jaldee.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.SearchServiceActivity;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.ServiceInfoDialog;
import com.jaldeeinc.jaldee.response.SearchDepartment;
import com.jaldeeinc.jaldee.response.SearchDepartmentServices;
import com.jaldeeinc.jaldee.response.SearchService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 3/9/18.
 */

public class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.MyViewHolder> {
    List<SearchDepartmentServices> mSearchDepartmentList;
    Context mContext;
    ServiceInfoDialog serviceInfoDialog;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_service;
        LinearLayout serviceList;

        public MyViewHolder(View view) {
            super(view);
            tv_service = (TextView) view.findViewById(R.id.tv_service);
            serviceList = (LinearLayout) view.findViewById(R.id.serviceList);

        }
    }

    ArrayList<SearchService> mServiceList;
    String from;
    String title;
    String uniqueID;
    Activity activity;

    public ServiceListAdapter(ArrayList<SearchService> mServiceList, Context mContext, String from, String title, String uniqueID, Activity mActivity, ArrayList<SearchDepartmentServices> departmentList) {
        this.mContext = mContext;
        this.mServiceList = mServiceList;
        Config.logV("ServiceList--------------" + mServiceList.size());
        this.from = from;
        this.title = title;
        this.uniqueID = uniqueID;
        activity = mActivity;
        this.mSearchDepartmentList = departmentList;
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
        String serviceName = serviceList.getName();
//        if (serviceList.getDepartment() != 0 && mSearchDepartmentList != null) {
//            String deptName = getDepartmentName(serviceList.getDepartment());
//            serviceName = serviceName.concat(" (").concat(deptName).concat(")");
//        }
        myViewHolder.tv_service.setText(serviceName);
        try{
        if(serviceList.getServiceType().equalsIgnoreCase("virtualservice")){
            if(serviceList.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")){
                myViewHolder.tv_service.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoom,0,0,0);
            }
            else if(serviceList.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")){
                myViewHolder.tv_service.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet,0,0,0);
            }
            else if(serviceList.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")){
                myViewHolder.tv_service.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsapp,0,0,0);
            }
            else if(serviceList.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")){
                myViewHolder.tv_service.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneicon_sized,0,0,0);
            }
        }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        myViewHolder.serviceList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (from.equalsIgnoreCase("searchdetail")) {
//                    final String mServicename = serviceList.getName();
//                    final String mServiceprice = serviceList.getTotalAmount();
//                    final String mServicedesc = serviceList.getDescription();
//                    final String mServiceduration = serviceList.getServiceDuration();
//                    final boolean mTaxable = serviceList.isTaxable();
//                    final ArrayList<SearchService> mServiceGallery = serviceList.getServicegallery();
//
//                    final boolean isPrepayment = serviceList.isPrePayment();
//                    final String minPrepayment = serviceList.getMinPrePaymentAmount();

                    serviceInfoDialog = new ServiceInfoDialog(v.getContext(), mServiceList.get(position));
                    serviceInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    serviceInfoDialog.show();
                    DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                    int width = (int) (metrics.widthPixels * 1);
                    serviceInfoDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

//                    Intent iService = new Intent(v.getContext(), SearchServiceActivity.class);
//                    iService.putExtra("name", mServicename);
//                    iService.putExtra("duration", mServiceduration);
//                    iService.putExtra("price", mServiceprice);
//                    iService.putExtra("desc", mServicedesc);
//                    iService.putExtra("servicegallery", mServiceGallery);
//                    iService.putExtra("title", title);
//                    iService.putExtra("taxable", mTaxable);
//                    iService.putExtra("isPrePayment", isPrepayment);
//                    iService.putExtra("MinPrePaymentAmount",minPrepayment);
//                    iService.putExtra("from", "chk");
//                    mContext.startActivity(iService);
                } else {

                    Config.logV("Service ID pass------------" + serviceList.getName());
                    ApiService(uniqueID, serviceList.getName(), title);
                }
            }
        });


    }

    private void ApiService(String uniqueID, final String serviceName, final String title) {


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
                        ArrayList<SearchService> service = new ArrayList<>();
                        service = response.body();
                        for (int i = 0; i < service.size(); i++) {
                            Config.logV("Response--serviceid-------------------------" + serviceName);

                            if (service.get(i).getName().toLowerCase().equalsIgnoreCase(serviceName.toLowerCase())) {
                                Intent iService = new Intent(mContext, SearchServiceActivity.class);
                                iService.putExtra("name", service.get(i).getName());
                                iService.putExtra("duration", service.get(i).getServiceDuration());
                                iService.putExtra("price", service.get(i).getTotalAmount());
                                iService.putExtra("desc", service.get(i).getDescription());
                                iService.putExtra("servicegallery", service.get(i).getServicegallery());
                                iService.putExtra("from","multiuser");
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

    public String getDepartmentName(int department) {
        Log.i("departments", new Gson().toJson(mSearchDepartmentList));
        for (int i = 0; i < mSearchDepartmentList.size(); i++) {
            if (Integer.parseInt(mSearchDepartmentList.get(i).getDepartmentId()) == department) {
                return mSearchDepartmentList.get(i).getDepartmentName();
            }
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return mServiceList.size();
    }
}