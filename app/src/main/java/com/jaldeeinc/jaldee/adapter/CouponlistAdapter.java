package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.CheckIn;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.response.CoupnResponse;

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
        TextView mcouponCode,readTerms;
        ImageView delete;

        public MyViewHolder(View view) {
            super(view);
             mcouponCode = (TextView) view.findViewById(R.id.name);
             delete = (ImageView) view.findViewById(R.id.deletecoupon);
             readTerms =(TextView) view.findViewById(R.id.ReadTC);
        }
    }
    @Override
    public CouponlistAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.coupon_list_row, parent, false);

        return new CouponlistAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CouponlistAdapter.MyViewHolder myViewHolder, final int position) {
        final String  coupan = mcouponArraylist.get(position);

        Config.logV("Coupan NAme-------------------" + mcouponArraylist.get(position));
        myViewHolder.mcouponCode.setText(coupan);
        myViewHolder.readTerms.setText("Read T & C");
        myViewHolder.readTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<m3couponList.size();i++){
                if(coupan.equalsIgnoreCase(m3couponList.get(i).getJaldeeCouponCode())) {
                    Toast.makeText(mContext, m3couponList.get(i).getConsumerTermsAndconditions(), Toast.LENGTH_SHORT).show();
                }}}
        });

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
