package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.response.QueueTimeSlotModel;

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
    public void onBindViewHolder(final QueueTimeSlotViewHolder holder, final int position) {

        holder.txt_queuename.setText(horizontalQueueList.get(position).getName());
        holder.txt_queuetime.setText(horizontalQueueList.get(position).getQueueSchedule().getTimeSlots().get(0).getsTime() + "- " + horizontalQueueList.get(position).getQueueSchedule().getTimeSlots().get(0).geteTime());

        if (horizontalQueueList.size() > 0) {

            if (horizontalQueueList.size() == 1) {
                holder.ic_left.setVisibility(View.INVISIBLE);
                holder.ic_right.setVisibility(View.INVISIBLE);
                holder.ic_right.setEnabled(false);
                holder.ic_left.setEnabled(false);
            } else {
                if(position==0){
                    holder.ic_left.setVisibility(View.VISIBLE);
                    holder.ic_right.setVisibility(View.VISIBLE);
                    holder.ic_left.setImageResource(R.drawable.icon_left_angle_disabled);
                    holder.ic_right.setImageResource(R.drawable.icon_right_angle_active);
                    holder.ic_right.setEnabled(true);
                    holder.ic_left.setEnabled(false);
                }
                if(position==horizontalQueueList.size()-1){
                    holder.ic_left.setVisibility(View.VISIBLE);
                    holder.ic_right.setVisibility(View.VISIBLE);
                    holder.ic_right.setEnabled(false);
                    holder.ic_left.setEnabled(true);
                    holder.ic_left.setImageResource(R.drawable.icon_left_angle_active);
                    holder.ic_right.setImageResource(R.drawable.icon_right_angle_disabled);
                }
            }

        }

        holder.ic_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.txt_queuename.setText(horizontalQueueList.get(position).getName());
                holder.txt_queuetime.setText(horizontalQueueList.get(position).getQueueSchedule().getTimeSlots().get(0).getsTime() + "- " + horizontalQueueList.get(position).getQueueSchedule().getTimeSlots().get(0).geteTime());


                if (horizontalQueueList.size() > 0) {

                    if (horizontalQueueList.size() == 1) {
                        holder.ic_left.setVisibility(View.INVISIBLE);
                        holder.ic_right.setVisibility(View.INVISIBLE);
                        holder.ic_right.setEnabled(false);
                        holder.ic_left.setEnabled(false);
                    } else {
                        if(position==0){
                            holder.ic_left.setVisibility(View.VISIBLE);
                            holder.ic_right.setVisibility(View.VISIBLE);
                            holder.ic_left.setImageResource(R.drawable.icon_left_angle_disabled);
                            holder.ic_right.setImageResource(R.drawable.icon_right_angle_active);
                            holder.ic_right.setEnabled(true);
                            holder.ic_left.setEnabled(false);
                        }
                        if(position==horizontalQueueList.size()-1){
                            holder.ic_left.setVisibility(View.VISIBLE);
                            holder.ic_right.setVisibility(View.VISIBLE);
                            holder.ic_right.setEnabled(false);
                            holder.ic_left.setEnabled(true);
                            holder.ic_left.setImageResource(R.drawable.icon_left_angle_active);
                            holder.ic_right.setImageResource(R.drawable.icon_right_angle_disabled);
                        }
                    }

                }
            }
        });

        holder.ic_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                holder.txt_queuename.setText(horizontalQueueList.get(position).getName());
                holder.txt_queuetime.setText(horizontalQueueList.get(position).getQueueSchedule().getTimeSlots().get(0).getsTime() + "- " + horizontalQueueList.get(position).getQueueSchedule().getTimeSlots().get(0).geteTime());

                if (horizontalQueueList.size() > 0) {

                    if (horizontalQueueList.size() == 1) {
                        holder.ic_left.setVisibility(View.INVISIBLE);
                        holder.ic_right.setVisibility(View.INVISIBLE);
                        holder.ic_right.setEnabled(false);
                        holder.ic_left.setEnabled(false);
                    } else {
                        if(position==0){
                            holder.ic_left.setVisibility(View.VISIBLE);
                            holder.ic_right.setVisibility(View.VISIBLE);
                            holder.ic_left.setImageResource(R.drawable.icon_left_angle_disabled);
                            holder.ic_right.setImageResource(R.drawable.icon_right_angle_active);
                            holder.ic_right.setEnabled(true);
                            holder.ic_left.setEnabled(false);
                        }
                        if(position==horizontalQueueList.size()-1){
                            holder.ic_left.setVisibility(View.VISIBLE);
                            holder.ic_right.setVisibility(View.VISIBLE);
                            holder.ic_right.setEnabled(false);
                            holder.ic_left.setEnabled(true);
                            holder.ic_left.setImageResource(R.drawable.icon_left_angle_active);
                            holder.ic_right.setImageResource(R.drawable.icon_right_angle_disabled);
                        }
                    }

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return horizontalQueueList.size();
    }

    public class QueueTimeSlotViewHolder extends RecyclerView.ViewHolder {
        TextView txt_queuename, txt_queuetime;
        ImageView ic_right, ic_left;


        public QueueTimeSlotViewHolder(View view) {
            super(view);
            txt_queuename = view.findViewById(R.id.txt_queuename);
            txt_queuetime = view.findViewById(R.id.txt_queuetime);
            ic_right = view.findViewById(R.id.ic_right);
            ic_left = view.findViewById(R.id.ic_left);

        }
    }

}