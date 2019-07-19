package com.jaldeeinc.jaldee.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.response.CheckInModel;
import java.util.List;

/**
 * Created by sharmila on 13/8/18.
 */


public class CheckInListAdapter extends RecyclerView.Adapter<CheckInListAdapter.MyViewHolder> {

    private List<CheckInModel> mCheckList;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_bussProfile,tv_place;
        public MyViewHolder(View view) {
            super(view);
            tv_bussProfile=(TextView)view.findViewById(R.id.txt_bussProfile);
            tv_place=(TextView)view.findViewById(R.id.txt_place);



        }
    }

    Activity activity;

    public CheckInListAdapter(List<CheckInModel> mCheckList, Context mContext, Activity mActivity) {
        this.mContext = mContext;
        this.mCheckList = mCheckList;
        this.activity = mActivity;

    }

    @Override
    public CheckInListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.checkinlist_row, parent, false);


        return new CheckInListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CheckInListAdapter.MyViewHolder myViewHolder, final int position) {
        final CheckInModel checkList = mCheckList.get(position);

        myViewHolder.tv_bussProfile.setText(checkList.getProvider().getBusinessName());

        if(checkList.getQueue()!=null) {
            myViewHolder.tv_place.setText(checkList.getQueue().getLocation().getPlace());
        }


    }


    @Override
    public int getItemCount() {
        return mCheckList.size();
    }
}