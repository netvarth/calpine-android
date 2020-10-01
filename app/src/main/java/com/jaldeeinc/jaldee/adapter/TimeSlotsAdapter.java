package com.jaldeeinc.jaldee.adapter;

import android.annotation.SuppressLint;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinodev.androidneomorphframelayout.NeomorphFrameLayout;
import com.jaldeeinc.jaldee.Interface.ISelectSlotInterface;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Appointment;
import com.jaldeeinc.jaldee.activities.AppointmentDate;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.response.AvailableSlotsData;

import java.util.ArrayList;


public class TimeSlotsAdapter extends RecyclerView.Adapter<TimeSlotsAdapter.TimeSlotsAdapterViewHolder> {
    ArrayList<AvailableSlotsData> timeSlots = new ArrayList<>();
    int row_index = -1;
    String selectTime = "";
    String selectTimeslot = "";
    private ISelectSlotInterface iSelectSlotInterface;
    View previousSelectedItem;
    private Context context;


    public TimeSlotsAdapter(Context context, ArrayList timeSlots, ISelectSlotInterface iSelectSlotInterface) {
        this.context = context;
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
        myViewHolder.tvTimeSlot.setText(timeSlots.get(position).getDisplayTime());

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myViewHolder.flSlotBackground.setShadowOuter();
                row_index = position;
                notifyDataSetChanged();
                iSelectSlotInterface.sendSelectedTime(timeSlots.get(position).getDisplayTime(), timeSlots.get(position).getSlotTime(), timeSlots.get(position).getScheduleId());
            }
        });

        if (row_index == position) {
            myViewHolder.flSlotBackground.setShadowInner();
            myViewHolder.tvTimeSlot.setTextColor(ContextCompat.getColor(context, R.color.location_theme));
//            myViewHolder.itemView.setBackgroundResource(R.drawable.rounded_popularsearch_green);
        } else {
            myViewHolder.flSlotBackground.setShadowOuter();
//            myViewHolder.itemView.setBackgroundResource(R.drawable.rounded_popularsearch);
        }
//        AppointmentDate.timeslot(selectTime);
    }


    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

    public class TimeSlotsAdapterViewHolder extends RecyclerView.ViewHolder {

        NeomorphFrameLayout flSlotBackground;
        CustomTextViewSemiBold tvTimeSlot;


        public TimeSlotsAdapterViewHolder(View view) {
            super(view);

            tvTimeSlot = view.findViewById(R.id.tv_timeSlot);
            flSlotBackground = view.findViewById(R.id.fl_slotBackground);


        }
    }
}