package com.jaldeeinc.jaldee.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Interface.ISelectedQueue;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
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
    private int selectedPosition = 0;


    public QueuesAdapter(Context context, ArrayList<QueueTimeSlotModel> queuesList, ISelectedQueue iSelectedQueue) {
        this.context = context;
        this.queuesList = queuesList;
        this.iSelectedQueue = iSelectedQueue;

    }

    @Override
    public QueuesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout file
        View queueView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeslots, parent, false);
        QueuesAdapterViewHolder gvh = new QueuesAdapterViewHolder(queueView);
        return gvh;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final QueuesAdapterViewHolder myViewHolder, @SuppressLint("RecyclerView") final int position) {
        final QueueTimeSlotModel queue = queuesList.get(position);
        String displayQueueTime = queuesList.get(position).getQueueSchedule().getTimeSlots().get(0).getsTime() + "-" + queuesList.get(position).getQueueSchedule().getTimeSlots().get(0).geteTime();
        myViewHolder.tvTimeSlot.setText(displayQueueTime);

        if (position == selectedPosition) {
            myViewHolder.flSlotBackground.setBackgroundResource(R.drawable.selected_slot);
            myViewHolder.tvTimeSlot.setTextColor(ContextCompat.getColor(context, R.color.white));
            iSelectedQueue.sendSelectedQueue(displayQueueTime, queue, queuesList.get(position).getId());
        } else {
            myViewHolder.flSlotBackground.setBackgroundResource(R.drawable.unselected_slot);
            myViewHolder.tvTimeSlot.setTextColor(ContextCompat.getColor(context, R.color.gray));
        }

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
                    iSelectedQueue.sendSelectedQueue(displayQueueTime, queue, queuesList.get(position).getId());

                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return queuesList.size();
    }

    public class QueuesAdapterViewHolder extends RecyclerView.ViewHolder {

        CardView flSlotBackground;
        CustomTextViewSemiBold tvTimeSlot;


        public QueuesAdapterViewHolder(View view) {
            super(view);

            tvTimeSlot = view.findViewById(R.id.tv_timeSlot);
            flSlotBackground = view.findViewById(R.id.fl_slotBackground);


        }
    }
}