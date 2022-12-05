package com.jaldeeinc.jaldee.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Interface.ITimeSlot;
import com.jaldeeinc.jaldee.Interface.OnBottomReachedListener;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.response.CatalogTimeSlot;

import java.util.ArrayList;


public class OrderTimeSlotAdapter extends RecyclerView.Adapter<OrderTimeSlotAdapter.MyViewHolder> {
    ArrayList<CatalogTimeSlot> schedulesList = new ArrayList<>();
    int row_index = -1;
    String selectTime = "";
    String selectTimeslot = "";
    private ITimeSlot iTimeSlot;
    View previousSelectedItem;
    private Context context;
    private int selectedPosition = 0;
    OnBottomReachedListener onBottomReachedListener;


    public OrderTimeSlotAdapter(Context context, ArrayList<CatalogTimeSlot> schedulesList, ITimeSlot iTimeSlot) {
        this.context = context;
        this.schedulesList = schedulesList;
        this.iTimeSlot = iTimeSlot;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout file
        View queueView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeslots, parent, false);
        MyViewHolder gvh = new MyViewHolder(queueView);
        return gvh;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, @SuppressLint("RecyclerView") final int position) {

        final CatalogTimeSlot schedule = schedulesList.get(position);

        String startTime = schedule.getStartTime();
        String endTime = schedule.getEndTime();
        String displayTime = startTime+" - "+endTime;

        myViewHolder.tvTimeSlot.setText(displayTime);


        if (position == selectedPosition) {

            myViewHolder.flSlotBackground.setBackgroundResource(R.drawable.selected_darkblue);
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
                    myViewHolder.flSlotBackground.setBackgroundResource(R.drawable.selected_darkblue);
                    myViewHolder.tvTimeSlot.setTextColor(ContextCompat.getColor(context, R.color.white));
                    iTimeSlot.sendSelectedTime(displayTime);

                }

            }
        });
    }


    @Override
    public int getItemCount() {
        return schedulesList == null ? 0 : schedulesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CardView flSlotBackground;
        CustomTextViewSemiBold tvTimeSlot;


        public MyViewHolder(View view) {
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