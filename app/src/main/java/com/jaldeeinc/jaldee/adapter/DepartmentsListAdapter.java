package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.model.SearchListModel;
import com.jaldeeinc.jaldee.response.SearchDepartment;
import com.jaldeeinc.jaldee.response.SearchViewDetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DepartmentsListAdapter extends RecyclerView.Adapter<DepartmentsListAdapter.MyViewHolder> {
    Context mContext;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_department;
        LinearLayout departmentList;

        public MyViewHolder(View view) {
            super(view);
            tv_department = (TextView) view.findViewById(R.id.tv_departmentList);
            departmentList = (LinearLayout) view.findViewById(R.id.departmentList);


        }
    }
    ArrayList<SearchDepartment> mDepartmentList;
    String from,businessName;
    HashMap<String, List<SearchListModel>> mdepartmentMap;

    public DepartmentsListAdapter(ArrayList<SearchDepartment> mDepartmentList, Context mContext,String from, String businessName) {
        this.mContext = mContext;
        this.mDepartmentList = mDepartmentList;
        this.from = from;
        this.businessName = businessName;

    }
    public void setFields(ArrayList<SearchDepartment> mDepartmentList, HashMap<String, List<SearchListModel>> departmentMap, String businessName) {
        this.mDepartmentList = mDepartmentList;
        this.mdepartmentMap = departmentMap;
        this.businessName = businessName;
    }

    @Override
    public DepartmentsListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.departmentlist_row, parent, false);

        return new DepartmentsListAdapter.MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final DepartmentsListAdapter.MyViewHolder myViewHolder, final int position) {
        final SearchDepartment departmentsList = mDepartmentList.get(position);
//        int count = 0;
//        count = mdepartmentMap.get(departmentsList.getDepartmentCode()).size();
        myViewHolder.tv_department.setText(departmentsList.getDepartmentName());
        }

    @Override
    public int getItemCount() {
        return mDepartmentList.size();
    }

}