package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Locations;

import java.util.List;

public class RecyclerAdapterLocation extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private  static  final int TYPE = 1;
    private final Context context;
    private final List<Object> listRecyclerItem ;

    public RecyclerAdapterLocation(Context context, List<Object> listRecyclerItem) {
        this.context = context;
        this.listRecyclerItem = listRecyclerItem;
    }

    public  class ItemViewHolder extends RecyclerView.ViewHolder{

        private TextView name;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        switch (i){
            case TYPE:
            default:
                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.newsearchlocationlistitem,viewGroup,false);
                return new ItemViewHolder(layoutView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int viewType = getItemViewType(i);

        switch (viewType){
            case TYPE:
            default:
                ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
                Locations location =  (Locations) listRecyclerItem.get(i);
                itemViewHolder.name.setText(location.getName());
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
