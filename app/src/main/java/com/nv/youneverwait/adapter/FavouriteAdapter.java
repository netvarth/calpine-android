package com.nv.youneverwait.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.nv.youneverwait.R;
import com.nv.youneverwait.callback.FavAdapterOnCallback;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.response.FavouriteModel;

import java.util.ArrayList;
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
    FavAdapterOnCallback callback;
    ArrayList<Integer> ids =new ArrayList<>();

    public FavouriteAdapter(List<FavouriteModel> mFAVList, Context mContext, Activity mActivity, FavAdapterOnCallback callback) {
        this.mContext = mContext;
        this.mFavList = mFAVList;
        this.activity = mActivity;
        this.callback=callback;

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
        myViewHolder.tv_provider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!favList.isExpandFlag()) {
                    favList.setExpandFlag(true);
                    myViewHolder.mrRecylce_fav.setVisibility(View.VISIBLE);
                    ids.clear();
                    for(int i=0;i<favList.getLocations().size();i++){
                        ids.add(favList.getLocations().get(i).getLocId());
                    }


                    Config.logV("Ids------------"+ids.size());
                    for(int i=0;i<ids.size();i++){

                        Config.logV("Ids---1111---------"+ids.get(i));
                    }
                    callback.onMethodViewCallback(favList.getId(),ids,myViewHolder.mrRecylce_fav);
                    myViewHolder.tv_provider.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.icon_up_light,0);
                    myViewHolder.tv_provider.setBackground(mContext.getResources().getDrawable(R.drawable.input_border_top));
                }else{
                    favList.setExpandFlag(false);
                    myViewHolder.mrRecylce_fav.setVisibility(View.GONE);
                    myViewHolder.tv_provider.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.icon_down_light,0);
                    myViewHolder.tv_provider.setBackground(mContext.getResources().getDrawable(R.drawable.input_background_opaque_round));

                }
            }


        });





    }


    @Override
    public int getItemCount() {
        return mFavList.size();
    }


}