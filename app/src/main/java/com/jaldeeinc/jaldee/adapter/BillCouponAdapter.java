package com.jaldeeinc.jaldee.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;

import java.util.ArrayList;
import java.util.Map;

public class BillCouponAdapter extends RecyclerView.Adapter<BillCouponAdapter.BillCouponViewHolder> {

    private Map<String, JsonObject> jCoupons;
    private ArrayList<String> keyList = new ArrayList<String>();
    private ArrayList<String> valueList = new ArrayList<String>();
    private ArrayList<String> systemNote = new ArrayList<String>();
    String couponNote;
    String couponNoteValue;





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
            this.systemNote.add(couponValues.get("systemNote").toString());
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
    public void onBindViewHolder(final BillCouponAdapter.BillCouponViewHolder holder, final int position) {
         String couponName = this.keyList.get(position);
         String couponValue = this.valueList.get(position);
         couponNote = this.systemNote.get(position);

         if(couponNote.contains("MINIMUM_BILL_AMT_REQUIRED")){
             couponNoteValue = "Minimum bill amount";
         }
         else if(couponNote.contains("COUPON_APPLIED")){
             couponNoteValue = "Coupon already applied";
         }else if(couponNote.contains("SELF_PAY_REQUIRED")){
             couponNoteValue = "Self pay required";
         }else if(couponNote.contains("NO_OTHER_COUPONS_ALLOWED")){
             couponNoteValue = "No other coupons allowed";
         }else if(couponNote.contains("EXCEEDS_APPLY_LIMIT")){
             couponNoteValue = "Exceeds apply limit";
         }else if(couponNote.contains("ONLY_WHEN_FITST_CHECKIN")){
             couponNoteValue = "Only for first check-in";
         }else if(couponNote.contains("ONLINE_CHECKIN_REQUIRED")){
             couponNoteValue = "Online check-in required";
         }else if(couponNote.contains("CANT_COMBINE_WITH_OTHER_COUPONES")){
             couponNoteValue = "Cannot combine with other coupons";
         }else {
             couponNoteValue = "";
         }


        Log.i("couponNote",couponNote);



        holder.txt_coupon_name.setText("( "+couponName+" )");
        couponValue = couponValue.replaceAll("^\"|\"$", "");
        Double jCouponValue = Double.parseDouble(couponValue);
        //Float f= Float.parseFloat(couponValue);
//        DecimalFormat format = new DecimalFormat("0.00");
        holder.txt_coupon_value.setText("(-)₹\u00a0"+(Config.getAmountinTwoDecimalPoints(jCouponValue)));

        if(jCouponValue > 0){
            holder.txt_coupon_value.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(holder.txt_coupon_value.getContext(), "black", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            holder.txt_coupon_value.setTextColor(holder.txt_coupon_value.getContext().getResources().getColor(R.color.red));


            Log.i("couponNoteValue" , couponNoteValue);

            holder.txt_coupon_value.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(holder.txt_coupon_value.getContext(),"Sorry!! This coupon is rejected because it doesn't meet the following requirements :-"+"  " + couponNoteValue, Toast.LENGTH_LONG).show();
                }
            });
        }

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
