package com.nv.youneverwait.adapter;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nv.youneverwait.R;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.custom.CustomTypefaceSpan;
import com.nv.youneverwait.response.SearchAWsResponse;
import com.nv.youneverwait.response.SearchDepartment;
import com.nv.youneverwait.utils.SharedPreference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepartmentAdapter extends  RecyclerView.Adapter<DepartmentAdapter.MyViewHolder> {

    Context mcontext;

    private List<SearchDepartment> mSearchDepartments;
    String mbranchId;

    public DepartmentAdapter(ArrayList<SearchDepartment> mSearchDepartments, String mbranchId) {
        this.mSearchDepartments=mSearchDepartments;
        this.mbranchId = mbranchId;
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
    public void onBindViewHolder(@NonNull DepartmentAdapter.MyViewHolder holder, int position) {
        final SearchDepartment searchDepartment = mSearchDepartments.get(position);

        holder.deptName.setText(searchDepartment.getDepartmentName());
        holder.deptName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("deptNameClick",searchDepartment.getDepartmentCode());
                Log.i("deptNameClick",searchDepartment.getDepartmentName());
                Log.i("deptNameClick",mbranchId);
            }
        });

    }


    @Override
    public int getItemCount() {
        return mSearchDepartments.size();
    }


}
