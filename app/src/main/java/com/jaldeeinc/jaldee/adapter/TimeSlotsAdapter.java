package com.jaldeeinc.jaldee.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.response.SearchViewDetail;

import java.util.ArrayList;

public class TimeSlotsAdapter extends RecyclerView.Adapter< TimeSlotsAdapter. TimeSlotsAdapterViewHolder> {
    ArrayList timeSlots = new ArrayList();


    public  TimeSlotsAdapter( ArrayList timeSlots) {
        this.timeSlots = timeSlots;

    }

    @Override
    public  TimeSlotsAdapter.TimeSlotsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout file
        View queueView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeslots, parent, false);
        TimeSlotsAdapterViewHolder gvh = new TimeSlotsAdapterViewHolder(queueView);
        return gvh;
    }

    @Override
    public void onBindViewHolder(final  TimeSlotsAdapterViewHolder myViewHolder, int position) {
        //SearchVirtualFields virtualList = virtualFieldList.get(position);
        final String timeSlot = timeSlots.get(position).toString();
        myViewHolder.mSpecial.setText(timeSlot);

    }


    @Override
    public int getItemCount() {
        return /*virtualFieldList.size();*/
                timeSlots.size();
    }

    public class  TimeSlotsAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView mSpecial;


        public  TimeSlotsAdapterViewHolder(View view) {
            super(view);
            mSpecial = view.findViewById(R.id.special);



        }
    }
}