package com.nv.youneverwait.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nv.youneverwait.R;
import com.nv.youneverwait.common.Config;

import java.util.List;

/**
 * Created by sharmila on 31/8/18.
 */

public class ParkingTypesAdapter extends RecyclerView.Adapter<ParkingTypesAdapter.ParkingTypesAdapterViewHolder> {
    private List<ParkingModel> horizontaltypeList;
    Context context;


    public ParkingTypesAdapter(List<ParkingModel> horizontaltypeList, Context context) {
        this.horizontaltypeList = horizontaltypeList;
        this.context = context;
        Config.logV("List-------------" + horizontaltypeList.size());
    }

    @Override
    public ParkingTypesAdapter.ParkingTypesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout file
        View queueView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_typelist, parent, false);
        ParkingTypesAdapter.ParkingTypesAdapterViewHolder gvh = new ParkingTypesAdapter.ParkingTypesAdapterViewHolder(queueView);
        return gvh;
    }

    @Override
    public void onBindViewHolder(ParkingTypesAdapter.ParkingTypesAdapterViewHolder holder, final int position) {



        holder.tv_type.setText(horizontaltypeList.get(position).getTypename());

        if (horizontaltypeList.get(position).getId().equalsIgnoreCase("0"))
            holder.ic_type.setImageResource(R.drawable.icon_work);
        if (horizontaltypeList.get(position).getId().equalsIgnoreCase("1"))
            holder.ic_type.setImageResource(R.drawable.icon_parking);
        if (horizontaltypeList.get(position).getId().equalsIgnoreCase("2"))
            holder.ic_type.setImageResource(R.drawable.icon_24hours);
        if (horizontaltypeList.get(position).getId().equalsIgnoreCase("4"))
            holder.ic_type.setImageResource(R.drawable.icon_ambulance);
        if (horizontaltypeList.get(position).getId().equalsIgnoreCase("5"))
            holder.ic_type.setImageResource(R.drawable.icon_firstaid);
        if (horizontaltypeList.get(position).getId().equalsIgnoreCase("7"))
            holder.ic_type.setImageResource(R.drawable.icon_trauma);
        if (horizontaltypeList.get(position).getId().equalsIgnoreCase("6")||horizontaltypeList.get(position).getId().equalsIgnoreCase("3"))
            holder.ic_type.setImageResource(R.drawable.icon_emergency);


    }

    @Override
    public int getItemCount() {
        return horizontaltypeList.size();
    }

    public class ParkingTypesAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView tv_type;
        ImageView ic_type;


        public ParkingTypesAdapterViewHolder(View view) {
            super(view);
            tv_type = view.findViewById(R.id.txt_type);
            ic_type = view.findViewById(R.id.ic__image);


        }
    }
}
