package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;

import java.util.ArrayList;

public class LAmenitiesListingAdapter extends RecyclerView.Adapter<LAmenitiesListingAdapter.ViewHolder> {

    ArrayList<ParkingModel> listType = new ArrayList<>();
    public Context context;

    public LAmenitiesListingAdapter(ArrayList<ParkingModel> listType, Context context) {
        this.listType = listType;
        this.context = context;
    }

    @NonNull
    @Override
    public LAmenitiesListingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        return new LAmenitiesListingAdapter.ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final LAmenitiesListingAdapter.ViewHolder viewHolder, final int i) {

        Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                "fonts/Montserrat_Regular.otf");
       viewHolder.tvServiceName.setText(listType.get(i).getTypename());
       viewHolder.tvServiceName.setTypeface(tyface);

    }

    @Override
    public int getItemCount() {

        return  listType.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvServiceName;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tv_serviceName);

        }
    }
}
