package com.nv.youneverwait.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nv.youneverwait.R;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.response.CoupnResponse;

import org.json.JSONArray;

import java.util.ArrayList;

public class CouponlistAdapter extends ArrayAdapter<CoupnResponse> {
    Context mContext;
    ArrayList<CoupnResponse> m3couponList;
    String mcouponEntered;
    ArrayList<String> mcouponArraylist;
    JSONArray mcouponList = new JSONArray();

    public CouponlistAdapter(@NonNull Context Context, int resource, ArrayList<CoupnResponse> s3couponList, String couponEntered, ArrayList<String> couponArraylist, JSONArray couponList) {
        super(Context, resource, s3couponList);


        this.mContext = Context;
        this.m3couponList = s3couponList;
        this.mcouponEntered = couponEntered;
        this.mcouponArraylist = couponArraylist;
        this.mcouponList = couponList;

    }


    @Override
    public int getCount()
    {
        return mcouponArraylist.size();
    }

    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(this.getContext()).inflate(R.layout.coupon_list_row,parent,false);

        Log.i("position",String.valueOf(position));
        Log.i("coupoooon",mcouponArraylist.get(position).toString());


        final TextView mcouponCode = (TextView) listItem.findViewById(R.id.name);
        mcouponCode.setText(mcouponArraylist.get(position));

        ImageView delete = (ImageView) listItem.findViewById(R.id.deletecoupon);



        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mcouponArraylist.remove(position);

            }
        });


//
//        TextView mcouponDesc = (TextView) listItem.findViewById(R.id.type);
//        mcouponDesc.setText(coupnResponse.getCouponDescription());

//        TextView mcouponTerms = (TextView) listItem.findViewById(R.id.couponTerms);
//        mcouponTerms.setText(coupnResponse.getConsumerTermsAndconditions());
//
//        TextView mcouponDisc = (TextView) listItem.findViewById(R.id.couponDisc);
//        mcouponDisc.setText(coupnResponse.getDiscountValue());
//
//        TextView mcouponName = (TextView) listItem.findViewById(R.id.couponName);
//        mcouponName.setText(coupnResponse.getCouponName());


        return listItem;
    }




}
