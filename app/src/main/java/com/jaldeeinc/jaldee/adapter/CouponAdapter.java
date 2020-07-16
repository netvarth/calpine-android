package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.response.CoupnResponse;

import java.util.Date;
import java.util.List;

public class CouponAdapter  extends ArrayAdapter<CoupnResponse> {

    List<CoupnResponse> couponList;
    Context mContext;
    String startDate;
    String endDate;
    SimpleDateFormat dateformats = null;
    CardView cardView;
    public CouponAdapter(@NonNull Context context, int resource, List<CoupnResponse> coupanList) {
        super(context, resource, coupanList);

            this.mContext = context;
            this.couponList = coupanList;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            dateformats = new SimpleDateFormat("MMM dd yyyy");
        }
        for(int i=0;i<coupanList.size();i++){

            Config.logV("Coupan List------------------------" + couponList.get(i).getJaldeeCouponCode());
            Config.logV("CouponDesp",couponList.get(i).getCouponDescription());
            Config.logV("CouponTerms",couponList.get(i).getConsumerTermsAndconditions());
            Config.logV("CouponDiscount",couponList.get(i).getDiscountValue());
            Config.logV("CouponName",couponList.get(i).getCouponName());
            Config.logV("CouponStartdate",String.valueOf(coupanList.get(i).getStartdate()));
            Config.logV("CouponEndDate",String.valueOf(coupanList.get(i).getEnddate()));

            Date startdate = new Date(coupanList.get(i).getStartdate());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                startDate=dateformats.format(startdate);
            }

            Date enddate = new Date(coupanList.get(i).getEnddate());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                endDate=dateformats.format(enddate);
            }

        }

    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(this.getContext()).inflate(R.layout.couponview,parent,false);


        CoupnResponse coupnResponse = couponList.get(position);

        CardView mCardview =(CardView) listItem.findViewById(R.id.card);

        Date startdate = new Date(couponList.get(position).getStartdate());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            startDate=dateformats.format(startdate);
        }

        Date enddate = new Date(couponList.get(position).getEnddate());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            endDate=dateformats.format(enddate);
        }

        if(couponList.get(position).isFirstCheckinOnly()){mCardview.setVisibility(View.GONE);}

        TextView mcouponCode = (TextView) listItem.findViewById(R.id.couponCode);
        mcouponCode.setText(couponList.get(position).getJaldeeCouponCode());


        TextView mcouponDesc = (TextView) listItem.findViewById(R.id.couponDesc);
        mcouponDesc.setText(coupnResponse.getCouponDescription());

        TextView mcouponTerms = (TextView) listItem.findViewById(R.id.couponTerms);
        mcouponTerms.setText(coupnResponse.getConsumerTermsAndconditions());

        if(coupnResponse.getDiscountType().equals("AMOUNT")){
            TextView mcouponDisc = (TextView) listItem.findViewById(R.id.couponDisc);
            mcouponDisc.setText("₹"+" "+ Config.getAmountinTwoDecimalPoints(Double.parseDouble((coupnResponse.getDiscountValue()))));
        }else{
            TextView mcouponDisc = (TextView) listItem.findViewById(R.id.couponDisc);
            mcouponDisc.setText(coupnResponse.getDiscountValue()+"%");
        }



         TextView mcouponName = (TextView) listItem.findViewById(R.id.couponName);
        mcouponName.setText(coupnResponse.getCouponName());

        TextView mvalidity = (TextView) listItem.findViewById(R.id.validityvaluetext);
        if(startdate!=null && enddate!=null){
            mvalidity.setText(startDate+"-"+endDate);
        }else {
            mvalidity.setText("");
        }







        return listItem;
    }

}
