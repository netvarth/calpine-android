package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.model.SearchListModel;

import com.jaldeeinc.jaldee.response.SearchDepartmentServices;
import com.jaldeeinc.jaldee.response.SearchService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.MyViewHolder> {
    OnItemClickListener onItemClickListener;
    static Context mcontext;

    private List<SearchDepartmentServices> mSearchDepartmentServices;
    HashMap<String, List<SearchListModel>> mdepartmentMap;
    String businessName;
    ArrayList<SearchService> mServicesList;
    int department;
    public DepartmentAdapter(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setFields(ArrayList<SearchDepartmentServices> mSearchDepartmentServices, String businessName) {
        this.mSearchDepartmentServices = mSearchDepartmentServices;
        this.businessName = businessName;

    }

    public interface OnItemClickListener {
        void departmentClicked(SearchDepartmentServices searchDepartment, String businessName);
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
    public void onBindViewHolder(@NonNull final DepartmentAdapter.MyViewHolder holder, final int position) {
        final SearchDepartmentServices searchDepartmentServices = mSearchDepartmentServices.get(position);
        int count = mSearchDepartmentServices.get(position).getUsers().size();
        if(count == 0){
            holder.deptName.setText(searchDepartmentServices.getDepartmentName() + " " + "(" + "No Doctors" + ")" );
        }
        else if(count == 1) {
            holder.deptName.setText(searchDepartmentServices.getDepartmentName() + " " + "(" + count + " Doctor" + ")");
        }
        else{
            holder.deptName.setText(searchDepartmentServices.getDepartmentName() + " " + "(" + count + " Doctors" + ")");
        }
        if (searchDepartmentServices.getDepartmentName() == null) {
        holder.deptName.setVisibility(View.GONE);
        }
        holder.deptName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (searchDepartmentServices.getDepartmentName() != null) {
                onItemClickListener.departmentClicked(searchDepartmentServices, businessName );
            }
            }
        });
    }
    @Override
    public int getItemCount() {
        return mSearchDepartmentServices.size();
    }
}