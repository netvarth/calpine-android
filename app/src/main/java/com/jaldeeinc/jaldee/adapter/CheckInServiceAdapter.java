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
import com.jaldeeinc.jaldee.response.SearchService;

import java.util.ArrayList;
import java.util.List;

public class CheckInServiceAdapter extends RecyclerView.Adapter<CheckInServiceAdapter.ViewHolder> {

    List<SearchService> mSearchServiceList;
    public Context context;

    public CheckInServiceAdapter(List<SearchService> mSearchServiceList, Context context) {
        this.mSearchServiceList = mSearchServiceList;
        this.context = context;
    }

    @NonNull
    @Override
    public CheckInServiceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        return new CheckInServiceAdapter.ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final CheckInServiceAdapter.ViewHolder viewHolder, final int i) {

        Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                "fonts/Montserrat_Regular.otf");
        viewHolder.tvServiceName.setText(mSearchServiceList.get(i).getName());
        viewHolder.tvServiceName.setTypeface(tyface);

    }

    @Override
    public int getItemCount() {

        return  mSearchServiceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvServiceName;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tv_serviceName);

        }
    }
}