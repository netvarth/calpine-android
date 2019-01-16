package com.nv.youneverwait.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.nv.youneverwait.R;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.response.CoupnResponse;
import java.util.List;

public class CouponAdapter  extends ArrayAdapter<CoupnResponse> {

    List<CoupnResponse> couponList;
    Context mContext;

    public CouponAdapter(@NonNull Context context, int resource, List<CoupnResponse> coupanList) {
        super(context, resource, coupanList);

            this.mContext = context;
            this.couponList = coupanList;

        for(int i=0;i<coupanList.size();i++){

            Config.logV("Coupan List------------------------" + couponList.get(i).getJaldeeCouponCode());
            Config.logV("CouponDesp",couponList.get(i).getCouponDescription());
            Config.logV("CouponTerms",couponList.get(i).getConsumerTermsAndconditions());
            Config.logV("CouponDiscount",couponList.get(i).getDiscountValue());
            Config.logV("CouponName",couponList.get(i).getCouponName());

        }

    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(this.getContext()).inflate(R.layout.couponview,parent,false);


        CoupnResponse coupnResponse = couponList.get(position);

        TextView mcouponCode = (TextView) listItem.findViewById(R.id.couponCode);
        mcouponCode.setText(couponList.get(position).getJaldeeCouponCode());


        TextView mcouponDesc = (TextView) listItem.findViewById(R.id.couponDesc);
        mcouponDesc.setText(coupnResponse.getCouponDescription());

        TextView mcouponTerms = (TextView) listItem.findViewById(R.id.couponTerms);
        mcouponTerms.setText(coupnResponse.getConsumerTermsAndconditions());

        TextView mcouponDisc = (TextView) listItem.findViewById(R.id.couponDisc);
        mcouponDisc.setText(coupnResponse.getDiscountValue());

         TextView mcouponName = (TextView) listItem.findViewById(R.id.couponName);
        mcouponName.setText(coupnResponse.getCouponName());


        return listItem;
    }

}
