package com.nv.youneverwait.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.nv.youneverwait.R;
import com.nv.youneverwait.response.FavouriteModel;
import java.util.List;


/**
 * Created by sharmila on 22/8/18.
 */

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.MyViewHolder> {

    private List<FavouriteModel> mFavList;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_provider;
        RecyclerView mrRecylce_fav;
        public MyViewHolder(View view) {
            super(view);
            tv_provider=(TextView)view.findViewById(R.id.txt_provider);
            mrRecylce_fav=(RecyclerView) view.findViewById(R.id.recylce_favloc);




        }
    }

    Activity activity;
    FavLocationAdapter mFavAdapter;
    public FavouriteAdapter(List<FavouriteModel> mFAVList, Context mContext, Activity mActivity) {
        this.mContext = mContext;
        this.mFavList = mFAVList;
        this.activity = mActivity;

    }

    @Override
    public FavouriteAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favlist_row, parent, false);


        return new FavouriteAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FavouriteAdapter.MyViewHolder myViewHolder, final int position) {
        final FavouriteModel favList = mFavList.get(position);

        myViewHolder.tv_provider.setText(favList.getBusinessName());

        /*RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        myViewHolder.mrRecylce_fav.setLayoutManager(mLayoutManager);
        mFavAdapter = new FavLocationAdapter(mFavList, mContext);
        myViewHolder.mrRecylce_fav.setAdapter(mFavAdapter);
        mFavAdapter.notifyDataSetChanged();*/



    }


    @Override
    public int getItemCount() {
        return mFavList.size();
    }


}