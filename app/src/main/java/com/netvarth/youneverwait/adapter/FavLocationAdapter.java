package com.netvarth.youneverwait.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.response.FavouriteModel;

import java.util.List;

/**
 * Created by sharmila on 22/8/18.
 */

public class FavLocationAdapter extends RecyclerView.Adapter<FavLocationAdapter.MyViewHolder> {

    private List<FavouriteModel> mFavList;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_provider,tv_loc,tv_date,tv_time;
        public MyViewHolder(View view) {
            super(view);
            tv_provider=(TextView)view.findViewById(R.id.txt_provider);
            tv_loc=(TextView)view.findViewById(R.id.txt_loc);
            tv_date=(TextView)view.findViewById(R.id.txt_date);
            tv_time=(TextView)view.findViewById(R.id.txt_time);


        }
    }

    Activity activity;

    public FavLocationAdapter(List<FavouriteModel> mFAVList, Context mContext) {
        this.mContext = mContext;
        this.mFavList = mFAVList;

    }

    @Override
    public FavLocationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favloclist_row, parent, false);


        return new FavLocationAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FavLocationAdapter.MyViewHolder myViewHolder, final int position) {
        final FavouriteModel favList = mFavList.get(position);

        myViewHolder.tv_loc.setText(favList.getLocations().get(position).getPlace());





    }


    @Override
    public int getItemCount() {
        return mFavList.size();
    }
}