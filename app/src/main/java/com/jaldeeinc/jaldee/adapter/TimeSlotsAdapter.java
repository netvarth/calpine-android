package com.jaldeeinc.jaldee.adapter;

import android.annotation.SuppressLint;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.jaldeeinc.jaldee.Interface.ISelectSlotInterface;
import com.jaldeeinc.jaldee.Interface.OnBottomReachedListener;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.model.SelectedSlotDetail;
import com.jaldeeinc.jaldee.response.AvailableSlotsData;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    int maxBookingsAllowed;
    List<SelectedSlotDetail> selectedSlotDetails = new ArrayList<>();

    public TimeSlotsAdapter(Context context, ArrayList timeSlots, ISelectSlotInterface iSelectSlotInterface, OnBottomReachedListener onBottomReachedListener, int maxBookingsAllowed) {
        this.context = context;
        this.timeSlots = timeSlots;
        this.iSelectSlotInterface = iSelectSlotInterface;
        this.onBottomReachedListener = onBottomReachedListener;
        this.maxBookingsAllowed = maxBookingsAllowed;
        AvailableSlotsData asd = this.timeSlots.stream()
                .filter(p -> p.isActive() && p.getNoOfAvailableSlots() != 0)
                .findAny()
                .orElse(null);
        SelectedSlotDetail ssd = new SelectedSlotDetail();
        ssd.setDisplayTime(asd.getDisplayTime());
        ssd.setSlotTime(asd.getSlotTime());
        ssd.setScheduleId(asd.getScheduleId());
        ssd.setPosition(asd.getPosition());
        selectedSlotDetails.add(ssd);
    }

    public TimeSlotsAdapter(Context context, ArrayList timeSlots, ISelectSlotInterface iSelectSlotInterface, OnBottomReachedListener onBottomReachedListener) {
        this.context = context;
        this.timeSlots = timeSlots;
        this.iSelectSlotInterface = iSelectSlotInterface;
        this.onBottomReachedListener = onBottomReachedListener;
        AvailableSlotsData asd = this.timeSlots.stream()
                .filter(p -> p.isActive() && p.getNoOfAvailableSlots() != 0)
                .findAny()
                .orElse(null);
        SelectedSlotDetail ssd = new SelectedSlotDetail();
        ssd.setDisplayTime(asd.getDisplayTime());
        ssd.setSlotTime(asd.getSlotTime());
        ssd.setScheduleId(asd.getScheduleId());
        ssd.setPosition(asd.getPosition());
        selectedSlotDetails.add(ssd);
    }


    @Override
    public TimeSlotsAdapter.TimeSlotsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout file
        View queueView = LayoutInflater.from(parent.getContext()).inflate(R.layout.appt_time_slots, parent, false);
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

            // if (position == selectedPosition) {
            if (timeSlot.isActive()) {

                if (selectedSlotDetails.stream()
                        .anyMatch(p -> p.getPosition() == position)) {
                    myViewHolder.flSlotBackground.setBackgroundResource(R.drawable.selected_slot);
                    myViewHolder.tvTimeSlot.setTextColor(ContextCompat.getColor(context, R.color.white));

                    // if (maxBookingsAllowed > 1) {
                   /* SelectedSlotDetail ssd = new SelectedSlotDetail();
                    ssd.setDisplayTime(timeSlots.get(position).getDisplayTime());
                    ssd.setSlotTime(timeSlots.get(position).getSlotTime());
                    ssd.setScheduleId(timeSlots.get(position).getScheduleId());
                    ssd.setPosition(position);
                    selectedSlotDetails.add(ssd);*/
                    iSelectSlotInterface.sendSelectedTime(selectedSlotDetails);
                    //} else {
                    //   iSelectSlotInterface.sendSelectedTime(timeSlots.get(position).getDisplayTime(), timeSlots.get(position).getSlotTime(), timeSlots.get(position).getScheduleId());// default selected slot
                    // }

                } else {
                    myViewHolder.flSlotBackground.setBackgroundResource(R.drawable.unselected_slot_1);
                    myViewHolder.tvTimeSlot.setTextColor(ContextCompat.getColor(context, R.color.black7));
                }
            } else {
                myViewHolder.flSlotBackground.setBackgroundResource(R.drawable.unselected_slot);
                myViewHolder.tvTimeSlot.setTextColor(ContextCompat.getColor(context, R.color.gray));
            }
            setAnimation(myViewHolder.flSlotBackground, position);
            myViewHolder.flSlotBackground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (timeSlot.isActive()) {

                        int currentPosition = myViewHolder.getLayoutPosition();

                        if (maxBookingsAllowed > 1) {
                            if (selectedSlotDetails.stream()
                                    .anyMatch(p -> p.getPosition() == currentPosition)) {
                                if (selectedSlotDetails.size() > 1) {
                                    myViewHolder.flSlotBackground.setBackgroundResource(R.drawable.unselected_slot_1);
                                    myViewHolder.tvTimeSlot.setTextColor(ContextCompat.getColor(context, R.color.black7));
                                    selectedSlotDetails = selectedSlotDetails.stream()
                                            .filter(e -> !Objects.equals(e.getPosition(), currentPosition))
                                            .collect(Collectors.toList());
                                    iSelectSlotInterface.sendSelectedTime(selectedSlotDetails);
                                }
                            } else {
                                if (selectedSlotDetails.size() == maxBookingsAllowed) {
                                    DynamicToast.make(context, "limit reached",
                                            ContextCompat.getColor(context, R.color.black), ContextCompat.getColor(context, R.color.white), Toast.LENGTH_SHORT).show();

                                } else {
                                    myViewHolder.flSlotBackground.setBackgroundResource(R.drawable.selected_slot);
                                    myViewHolder.tvTimeSlot.setTextColor(ContextCompat.getColor(context, R.color.white));

                                    SelectedSlotDetail ssd = new SelectedSlotDetail();
                                    ssd.setDisplayTime(timeSlots.get(position).getDisplayTime());
                                    ssd.setSlotTime(timeSlots.get(position).getSlotTime());
                                    ssd.setScheduleId(timeSlots.get(position).getScheduleId());
                                    ssd.setPosition(position);
                                    selectedSlotDetails.add(ssd);
                                    iSelectSlotInterface.sendSelectedTime(selectedSlotDetails);
                                }
                            }

                        } else {
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
                                //iSelectSlotInterface.sendSelectedTime(timeSlots.get(position).getDisplayTime(), timeSlots.get(position).getSlotTime(), timeSlots.get(position).getScheduleId());
                                SelectedSlotDetail ssd = new SelectedSlotDetail();
                                ssd.setDisplayTime(timeSlots.get(position).getDisplayTime());
                                ssd.setSlotTime(timeSlots.get(position).getSlotTime());
                                ssd.setScheduleId(timeSlots.get(position).getScheduleId());
                                ssd.setPosition(position);
                                selectedSlotDetails.clear();
                                selectedSlotDetails.add(ssd);
                                iSelectSlotInterface.sendSelectedTime(selectedSlotDetails);

                            }
                        }
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
        TextView tvTimeSlot;


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