package com.jaldeeinc.jaldee.adapter;

import android.annotation.SuppressLint;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaldeeinc.jaldee.Interface.ISelectSlotInterface;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Appointment;
import com.jaldeeinc.jaldee.activities.AppointmentDate;
import com.jaldeeinc.jaldee.response.AvailableSlotsData;

import java.util.ArrayList;


public class TimeSlotsAdapter extends RecyclerView.Adapter<TimeSlotsAdapter.TimeSlotsAdapterViewHolder> {
    ArrayList<AvailableSlotsData> timeSlots = new ArrayList<>();
    int row_index = -1;
    String selectTime = "";
    String selectTimeslot = "";
    private ISelectSlotInterface iSelectSlotInterface;
    View previousSelectedItem;


    public TimeSlotsAdapter(ArrayList timeSlots, ISelectSlotInterface iSelectSlotInterface) {
        this.timeSlots = timeSlots;
        this.iSelectSlotInterface = iSelectSlotInterface;

    }

    @Override
    public TimeSlotsAdapter.TimeSlotsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout file
        View queueView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeslots, parent, false);
        TimeSlotsAdapterViewHolder gvh = new TimeSlotsAdapterViewHolder(queueView);
        return gvh;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final TimeSlotsAdapterViewHolder myViewHolder, @SuppressLint("RecyclerView") final int position) {
        final AvailableSlotsData timeSlot = timeSlots.get(position);
        myViewHolder.mSpecial.setText(timeSlots.get(position).getDisplayTime());

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myViewHolder.itemView.setBackgroundResource(R.drawable.rounded_popularsearch_green);

                row_index = position;
                notifyDataSetChanged();
//                if (previousSelectedItem!=null) {
//                    previousSelectedItem.setBackgroundResource(R.drawable.rounded_popularsearch);
//                }
//                previousSelectedItem=v;
//                v.setBackgroundResource(R.drawable.rounded_popularsearch_green);


                new Handler().postDelayed(() -> {

                    iSelectSlotInterface.sendSelectedTime(timeSlots.get(position).getDisplayTime(), timeSlots.get(position).getSlotTime(), timeSlots.get(position).getScheduleId());

                }, 200);


            }
        });

        if (row_index == position) {
            myViewHolder.itemView.setBackgroundResource(R.drawable.rounded_popularsearch_green);
        } else {
            myViewHolder.itemView.setBackgroundResource(R.drawable.rounded_popularsearch);
        }
//        AppointmentDate.timeslot(selectTime);
    }


    @Override
    public int getItemCount() {
        return /*virtualFieldList.size();*/
                timeSlots.size();
    }

    public class TimeSlotsAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView mSpecial;


        public TimeSlotsAdapterViewHolder(View view) {
            super(view);
            mSpecial = view.findViewById(R.id.special);


        }
    }
}