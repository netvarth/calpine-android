package com.nv.youneverwait.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nv.youneverwait.R;
import com.nv.youneverwait.activities.CheckIn;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.response.ActiveCheckIn;
import com.nv.youneverwait.response.CoupnResponse;

import org.json.JSONArray;

import java.util.ArrayList;

public class CouponlistAdapter extends RecyclerView.Adapter<CouponlistAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<CoupnResponse> m3couponList;
    String mcouponEntered;
    ArrayList<String> mcouponArraylist;


    public CouponlistAdapter( Context Context, ArrayList<CoupnResponse> s3couponList, String couponEntered, ArrayList<String> couponArraylist) {

        this.mContext = Context;
        this.m3couponList = s3couponList;
        this.mcouponEntered = couponEntered;
        this.mcouponArraylist = couponArraylist;


    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mcouponCode;
        ImageView delete;

        public MyViewHolder(View view) {
            super(view);
             mcouponCode = (TextView) view.findViewById(R.id.name);
             delete = (ImageView) view.findViewById(R.id.deletecoupon);
        }
    }
    @Override
    public CouponlistAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.coupon_list_row, parent, false);

        return new CouponlistAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CouponlistAdapter.MyViewHolder myViewHolder, final int position) {
        final String  coupan = mcouponArraylist.get(position);

        Config.logV("Coupan NAme-------------------" + mcouponArraylist.get(position));
        myViewHolder.mcouponCode.setText(coupan);

        myViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mcouponArraylist.remove(position);
                notifyDataSetChanged();

                CheckIn checkIn = new CheckIn();
                checkIn.setCouponList(mcouponArraylist);
            }
        });

    }
    @Override
    public int getItemCount() {
        return mcouponArraylist.size();
    }


}
