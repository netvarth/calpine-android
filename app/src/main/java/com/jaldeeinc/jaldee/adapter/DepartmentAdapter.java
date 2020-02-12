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

import com.jaldeeinc.jaldee.response.SearchDepartmentServices;
import com.jaldeeinc.jaldee.response.SearchService;
import com.jaldeeinc.jaldee.response.SearchViewDetail;

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

    public void setFields(ArrayList<SearchDepartmentServices> mSearchDepartmentServices, HashMap<String, List<SearchListModel>> departmentMap, String businessName,ArrayList<SearchService> mServicesList,int department) {
        this.mSearchDepartmentServices = mSearchDepartmentServices;
        this.mdepartmentMap = departmentMap;
        this.businessName = businessName;
        this.mServicesList = mServicesList;
        this.department = department;

    }

    public interface OnItemClickListener {
        void departmentClicked(SearchDepartmentServices searchDepartment, List<SearchListModel> searchListModels, String businessName, ArrayList<SearchService> mServicesList,int department);
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
        final SearchDepartmentServices searchDepartmentServices = mSearchDepartmentServices.get(position);


        Log.i("mdeptmap",new Gson().toJson(mdepartmentMap));
        Log.i("mdeptmap",new Gson().toJson(mdepartmentMap.get(searchDepartmentServices.getDepartmentCode())));

        int count = 0;
        if( mdepartmentMap.get(searchDepartmentServices.getDepartmentCode())!=null){
            count = mdepartmentMap.get(searchDepartmentServices.getDepartmentCode()).size();}
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
//                Log.i("deptNameClick", searchDepartment.getDepartmentCode());
//                Log.i("deptNameClick", searchDepartment.getDepartmentName());
                if (searchDepartmentServices.getDepartmentName() != null) {

                    onItemClickListener.departmentClicked(searchDepartmentServices, mdepartmentMap.get(searchDepartmentServices.getDepartmentCode()), businessName, mServicesList, department);
                }

            }
        });

    }


    @Override
    public int getItemCount() {
        return mSearchDepartmentServices.size();
    }


}