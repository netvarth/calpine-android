package com.jaldeeinc.jaldee.adapter;

import android.annotation.SuppressLint;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.chinodev.androidneomorphframelayout.NeomorphFrameLayout;
import com.jaldeeinc.jaldee.Interface.ISelectSlotInterface;
import com.jaldeeinc.jaldee.Interface.OnBottomReachedListener;
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
    private int selectedPosition = 0;
    OnBottomReachedListener onBottomReachedListener;


    public TimeSlotsAdapter(Context context, ArrayList timeSlots, ISelectSlotInterface iSelectSlotInterface, OnBottomReachedListener onBottomReachedListener) {
        this.context = context;
        this.timeSlots = timeSlots;
        this.iSelectSlotInterface = iSelectSlotInterface;
        this.onBottomReachedListener = onBottomReachedListener;

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

        if (timeSlots != null && timeSlots.size() > 0) {
            final AvailableSlotsData timeSlot = timeSlots.get(position);
            String convertedTime = timeSlots.get(position).getDisplayTime().replace("am", "AM").replace("pm", "PM");

            myViewHolder.tvTimeSlot.setText(convertedTime);

            if (position == timeSlots.size() - 1) {

                onBottomReachedListener.onBottomReached(position);

            }

            if (position == selectedPosition) {

                myViewHolder.flSlotBackground.setBackgroundResource(R.drawable.selected_slot);
                myViewHolder.tvTimeSlot.setTextColor(ContextCompat.getColor(context, R.color.white));
            } else {
                myViewHolder.flSlotBackground.setBackgroundResource(R.drawable.unselected_slot);
                myViewHolder.tvTimeSlot.setTextColor(ContextCompat.getColor(context, R.color.gray));
            }
            setAnimation(myViewHolder.flSlotBackground, position);

            myViewHolder.flSlotBackground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int currentPosition = myViewHolder.getLayoutPosition();
                    if (selectedPosition != currentPosition) {
                        // Temporarily save the last selected position
                        int lastSelectedPosition = selectedPosition;
                        // Save the new selected position
                        selectedPosition = currentPosition;
                        // update the previous selected row
                        notifyItemChanged(currentPosition);
                        notifyItemChanged(lastSelectedPosition);
                        // select the clicked row
                        myViewHolder.flSlotBackground.setBackgroundResource(R.drawable.selected_slot);
                        myViewHolder.tvTimeSlot.setTextColor(ContextCompat.getColor(context, R.color.white));
                        iSelectSlotInterface.sendSelectedTime(timeSlots.get(position).getDisplayTime(), timeSlots.get(position).getSlotTime(), timeSlots.get(position).getScheduleId());

                    }

                }
            });
        }

    }


    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

    public class TimeSlotsAdapterViewHolder extends RecyclerView.ViewHolder {

        CardView flSlotBackground;
        CustomTextViewSemiBold tvTimeSlot;


        public TimeSlotsAdapterViewHolder(View view) {
            super(view);

            tvTimeSlot = view.findViewById(R.id.tv_timeSlot);
            flSlotBackground = view.findViewById(R.id.fl_slotBackground);


        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > row_index) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            row_index = position;
        }
    }
}