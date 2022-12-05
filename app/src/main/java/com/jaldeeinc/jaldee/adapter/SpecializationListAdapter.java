package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.response.SearchViewDetail;

import java.util.ArrayList;

public class SpecializationListAdapter extends RecyclerView.Adapter<SpecializationListAdapter.MyViewHolder> {
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_specialization;
        LinearLayout specializationList;

        public MyViewHolder(View view) {
            super(view);
            tv_specialization = (TextView) view.findViewById(R.id.tv_specialization);
            specializationList = (LinearLayout) view.findViewById(R.id.specializationList);


        }
    }
    ArrayList<SearchViewDetail> mSpecializationList;
    String from,businessName;

    public SpecializationListAdapter(ArrayList<SearchViewDetail> mSpecializationList, Context mContext, String from, String businessName) {
        this.mContext = mContext;
        this.mSpecializationList = mSpecializationList;
        this.from = from;
        this.businessName = businessName;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.specializationlist_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int position) {
        final SearchViewDetail specializationList = mSpecializationList.get(position);
        myViewHolder.tv_specialization.setText(specializationList.getName());
    }

    @Override
    public int getItemCount() {
        return mSpecializationList.size();
    }
}