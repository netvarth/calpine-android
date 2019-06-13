package com.nv.youneverwait.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.nv.youneverwait.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class BillCouponAdapter extends RecyclerView.Adapter<BillCouponAdapter.BillCouponViewHolder> {

    private Map<String, JsonObject> jCoupons;
    private ArrayList<String> keyList = new ArrayList<String>();
    private ArrayList<String> valueList = new ArrayList<String>();


    public BillCouponAdapter(Map<String, JsonObject> jCoupon) {
        this.jCoupons = jCoupon;
        Log.i("JCoupon", this.jCoupons.toString());
        Log.i("jCoupon KeySet", jCoupon.keySet().toString());
        for (String name : jCoupon.keySet()) {
            this.keyList.add(name);
            Log.i("jCoupon Key: " , name);
        }
        Log.i("jCoupon Key: " , jCoupon.values().toString());
        for(JsonObject couponValues: jCoupon.values()) {
            this.valueList.add(couponValues.get("value").toString());
            Log.i("Coupon Value", couponValues.get("value").toString());
            Log.i("Coupon Value", couponValues.get("systemNote").toString());
        }
    }

    @Override
    public BillCouponViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_jcoupon, parent, false);
        return new BillCouponViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BillCouponAdapter.BillCouponViewHolder holder, int position) {
        final String couponName = this.keyList.get(position);
        final String couponValue = this.valueList.get(position);
        holder.txt_coupon_name.setText(couponName);
        holder.txt_coupon_value.setText("(-) ₹"+couponValue);
        Log.i("JCoupon Name", couponName);
        Log.i("JCoupon Value", couponValue);

    }

    @Override
    public int getItemCount() {
        Log.i("jCoupon Key size: " , String.valueOf(keyList.size()));
        return this.keyList.size();
    }


    public class BillCouponViewHolder extends RecyclerView.ViewHolder {
        TextView txt_coupon_name, txt_coupon_value;

        public BillCouponViewHolder(View view) {
            super(view);

            if(jCoupons != null & jCoupons.size()>0)
            {
                txt_coupon_name = view.findViewById(R.id.coupon_name);
                txt_coupon_value = view.findViewById(R.id.coupon_value);
            }


        }
    }
}
