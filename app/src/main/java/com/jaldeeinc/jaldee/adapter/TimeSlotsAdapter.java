package com.jaldeeinc.jaldee.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Appointment;
import com.jaldeeinc.jaldee.response.SearchViewDetail;

import java.util.ArrayList;

import static com.jaldeeinc.jaldee.connection.ApiClient.context;

public class TimeSlotsAdapter extends RecyclerView.Adapter< TimeSlotsAdapter. TimeSlotsAdapterViewHolder> {
    ArrayList timeSlots = new ArrayList();
    int selectedPosition = -1;


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

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final  TimeSlotsAdapterViewHolder myViewHolder, final int position) {
        //SearchVirtualFields virtualList = virtualFieldList.get(position);
        final String timeSlot = timeSlots.get(position).toString();
        myViewHolder.mSpecial.setText(timeSlot);
        if(selectedPosition==position){
            myViewHolder.itemView.setBackgroundColor(Color.parseColor("#000000"));}

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;
                notifyDataSetChanged();
                String selectTime = timeSlots.get(selectedPosition).toString();
                Toast.makeText(context, selectTime, Toast.LENGTH_SHORT).show();
                Appointment.timeslotdate(selectTime);
            }
        });

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