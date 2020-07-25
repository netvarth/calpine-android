package com.jaldeeinc.jaldee.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.response.SearchDepartmentServices;
import com.jaldeeinc.jaldee.response.SearchService;

import java.util.List;

public class ServicesListAdapter extends RecyclerView.Adapter<ServicesListAdapter.MyViewHolder> {
        List<SearchService> mSearchDepartmentServicesList;
        Context mContext;
        SearchDepartmentServices searchDepartmentServices;
        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv_services;
            View vsep;
            LinearLayout servicesList;

            public MyViewHolder(View view) {
                super(view);
                tv_services = (TextView) view.findViewById(R.id.txtservices);
                servicesList=(LinearLayout)view.findViewById(R.id.servicesList);
                vsep = view.findViewById(R.id.vsep);
            }
        }

        public ServicesListAdapter(Context mContext, List<SearchService> mSearchDepartmentServicesList,SearchDepartmentServices departmentServices) {
            this.mContext = mContext;
            this.mSearchDepartmentServicesList = mSearchDepartmentServicesList;
            this.searchDepartmentServices = departmentServices;
        }

        @Override
        public com.jaldeeinc.jaldee.adapter.ServicesListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.serviceslist_row, parent, false);

            return new com.jaldeeinc.jaldee.adapter.ServicesListAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final com.jaldeeinc.jaldee.adapter.ServicesListAdapter.MyViewHolder myViewHolder, final int position) {
            final SearchService servicesList = mSearchDepartmentServicesList.get(position);
            String serviceName = servicesList.getName();
            if(servicesList.getDepartment() == Integer.parseInt(searchDepartmentServices.getDepartmentId())){
            myViewHolder.tv_services.setText(serviceName);
            myViewHolder.tv_services.setVisibility(View.VISIBLE);
            }
            else{
                myViewHolder.tv_services.setVisibility(View.GONE);
                myViewHolder.vsep.setVisibility(View.GONE);
            }
    }
    @Override
    public int getItemCount() {
        return mSearchDepartmentServicesList.size();
    }
}
