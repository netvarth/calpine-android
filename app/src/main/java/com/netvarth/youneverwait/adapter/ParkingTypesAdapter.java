package com.netvarth.youneverwait.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.model.SearchListModel;
import java.util.List;

/**
 * Created by sharmila on 31/8/18.
 */

public class ParkingTypesAdapter extends RecyclerView.Adapter<ParkingTypesAdapter.ParkingTypesAdapterViewHolder> {
    private List<SearchListModel> horizontaltypeList;
    Context context;


    public ParkingTypesAdapter(List<SearchListModel> horizontaltypeList, Context context) {
        this.horizontaltypeList = horizontaltypeList;
        this.context = context;
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
       // holder.ic_type.setBackgroundResource(R.drawable.icon_home);

        if(horizontaltypeList.get(position).getParking_type_location1()!=null)
        holder.tv_type.setText("Parking Type free");
        if(horizontaltypeList.get(position).getAlways_open_location1()!=null)
            holder.tv_type.setText("24 Hours Open");
        if(horizontaltypeList.get(position).getDentistemergencyservices_location1()!=null)
            holder.tv_type.setText("Emergency Services");
        if(horizontaltypeList.get(position).getDocambulance_location1()!=null)
            holder.tv_type.setText("Ambulance");
        if(horizontaltypeList.get(position).getFirstaid_location1()!=null)
            holder.tv_type.setText("First Aid");
        if(horizontaltypeList.get(position).getPhysiciansemergencyservices_location1()!=null)
            holder.tv_type.setText("Emergency Services");
        if(horizontaltypeList.get(position).getTraumacentre_location1()!=null)
            holder.tv_type.setText("Trauma Center");

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
