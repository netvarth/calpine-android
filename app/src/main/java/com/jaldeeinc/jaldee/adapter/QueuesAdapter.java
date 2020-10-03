package com.jaldeeinc.jaldee.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.chinodev.androidneomorphframelayout.NeomorphFrameLayout;
import com.jaldeeinc.jaldee.Interface.ISelectSlotInterface;
import com.jaldeeinc.jaldee.Interface.ISelectedQueue;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.response.AvailableSlotsData;
import com.jaldeeinc.jaldee.response.QueueTimeSlotModel;

import java.util.ArrayList;

public class QueuesAdapter extends RecyclerView.Adapter<QueuesAdapter.QueuesAdapterViewHolder> {
    ArrayList<QueueTimeSlotModel> queuesList = new ArrayList<>();
    int row_index = -1;
    String selectTime = "";
    String selectTimeslot = "";
    private ISelectedQueue iSelectedQueue;
    View previousSelectedItem;
    private Context context;


    public QueuesAdapter(Context context, ArrayList<QueueTimeSlotModel> queuesList, ISelectedQueue iSelectedQueue) {
        this.context = context;
        this.queuesList = queuesList;
        this.iSelectedQueue = iSelectedQueue;

    }

    @Override
    public QueuesAdapter.QueuesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout file
        View queueView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeslots, parent, false);
        QueuesAdapter.QueuesAdapterViewHolder gvh = new QueuesAdapter.QueuesAdapterViewHolder(queueView);
        return gvh;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final QueuesAdapter.QueuesAdapterViewHolder myViewHolder, @SuppressLint("RecyclerView") final int position) {
        final QueueTimeSlotModel queue = queuesList.get(position);
        String displayQueueTime = queuesList.get(position).getQueueSchedule().getTimeSlots().get(0).getsTime() + "-" + queuesList.get(position).getQueueSchedule().getTimeSlots().get(0).geteTime();
        myViewHolder.tvTimeSlot.setText(displayQueueTime);

        myViewHolder.flSlotBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myViewHolder.flSlotBackground.setShadowInner();
                row_index = position;
                notifyDataSetChanged();
                iSelectedQueue.sendSelectedQueue(displayQueueTime, queue, queuesList.get(position).getId());
            }
        });

        if (row_index == position) {
            myViewHolder.flSlotBackground.setShadowInner();
            myViewHolder.tvTimeSlot.setTextColor(ContextCompat.getColor(context, R.color.location_theme));
        } else {
            myViewHolder.flSlotBackground.setShadowOuter();
            myViewHolder.tvTimeSlot.setTextColor(ContextCompat.getColor(context, R.color.inactive_text));

        }

    }

    @Override
    public int getItemCount() {
        return queuesList.size();
    }

    public class QueuesAdapterViewHolder extends RecyclerView.ViewHolder {

        NeomorphFrameLayout flSlotBackground;
        CustomTextViewSemiBold tvTimeSlot;


        public QueuesAdapterViewHolder(View view) {
            super(view);

            tvTimeSlot = view.findViewById(R.id.tv_timeSlot);
            flSlotBackground = view.findViewById(R.id.fl_slotBackground);


        }
    }
}