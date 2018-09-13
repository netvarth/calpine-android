package com.netvarth.youneverwait.adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.response.QueueTimeSlotModel;

import java.util.List;

/**
 * Created by sharmila on 8/8/18.
 */



public class QueueTimeSlotAdapter extends RecyclerView.Adapter<QueueTimeSlotAdapter.QueueTimeSlotViewHolder> {
    private List<QueueTimeSlotModel> horizontalQueueList;
    Context context;


    public QueueTimeSlotAdapter(List<QueueTimeSlotModel> horizontalQueueList, Context context) {
        this.horizontalQueueList = horizontalQueueList;
        this.context = context;
    }

    @Override
    public QueueTimeSlotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout file
        View queueView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_queuelist, parent, false);
        QueueTimeSlotViewHolder gvh = new QueueTimeSlotViewHolder(queueView);
        return gvh;
    }

    @Override
    public void onBindViewHolder(QueueTimeSlotViewHolder holder, final int position) {
          holder.tv_queue.setText(horizontalQueueList.get(position).getName()+" "+"[ "+horizontalQueueList.get(position).getQueueSchedule().getTimeSlots().get(0).getsTime()+"- "+horizontalQueueList.get(position).getQueueSchedule().getTimeSlots().get(0).geteTime());

    }

    @Override
    public int getItemCount() {
        return horizontalQueueList.size();
    }

    public class QueueTimeSlotViewHolder extends RecyclerView.ViewHolder {
        TextView tv_queue;


        public QueueTimeSlotViewHolder(View view) {
            super(view);
            tv_queue = view.findViewById(R.id.txt_queue);


        }
    }

}