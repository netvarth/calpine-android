package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;

import java.util.List;

/**
 * Created by sharmila on 31/8/18.
 */

public class ParkingTypesAdapter extends RecyclerView.Adapter<ParkingTypesAdapter.ParkingTypesAdapterViewHolder> {
    private List<ParkingModel> horizontaltypeList;
    Context context;
    int msize;


    public ParkingTypesAdapter(List<ParkingModel> horizontaltypeList, Context context,int size) {
        this.horizontaltypeList = horizontaltypeList;
        this.context = context;
        this.msize=size;
        Config.logV("List-------------" + size);
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


        if(horizontaltypeList.size()>0){
        holder.tv_type.setText(horizontaltypeList.get(position).getTypename());}


    }

    @Override
    public int getItemCount() {
        return msize;
    }

    public class ParkingTypesAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView tv_type;
        ImageView ic_type;


        public ParkingTypesAdapterViewHolder(View view) {
            super(view);
            tv_type = view.findViewById(R.id.txt_type);
           // ic_type = view.findViewById(R.id.ic__image);


        }
    }
}
