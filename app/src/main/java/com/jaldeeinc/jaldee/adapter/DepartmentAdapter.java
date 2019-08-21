package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.model.SearchListModel;
import com.jaldeeinc.jaldee.response.SearchDepartment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.MyViewHolder> {


    OnItemClickListener onItemClickListener;

    static Context mcontext;
    private List<SearchDepartment> mSearchDepartments;
    HashMap<String, List<SearchListModel>> mdepartmentMap;


    public DepartmentAdapter(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setFields(ArrayList<SearchDepartment> mSearchDepartments, HashMap<String, List<SearchListModel>> departmentMap) {
        this.mSearchDepartments = mSearchDepartments;
        this.mdepartmentMap = departmentMap;
    }

    public interface OnItemClickListener {
        void departmentClicked(SearchDepartment searchDepartment, List<SearchListModel> searchListModels);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        public TextView deptName;

        public MyViewHolder(View view) {
            super(view);

            deptName = (TextView) view.findViewById(R.id.deptName);

        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.departmentlist, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final DepartmentAdapter.MyViewHolder holder, int position) {
        final SearchDepartment searchDepartment = mSearchDepartments.get(position);


        Log.i("mdeptmap",new Gson().toJson(mdepartmentMap));
        Log.i("mdeptmap",new Gson().toJson(mdepartmentMap.get(searchDepartment.getDepartmentCode())));

        int count = 0;
        if( mdepartmentMap.get(searchDepartment.getDepartmentCode())!=null){
            count = mdepartmentMap.get(searchDepartment.getDepartmentCode()).size();
            holder.deptName.setText(searchDepartment.getDepartmentName() + " " + "(" + count + ")");}
        else{
            holder.deptName.setVisibility(View.GONE);
        }

        holder.deptName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("deptNameClick", searchDepartment.getDepartmentCode());
                Log.i("deptNameClick", searchDepartment.getDepartmentName());

                onItemClickListener.departmentClicked(searchDepartment,mdepartmentMap.get(searchDepartment.getDepartmentCode()));
            }
        });

    }


    @Override
    public int getItemCount() {
        return mSearchDepartments.size();
    }


}