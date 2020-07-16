package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.response.JdnResponse;
import java.util.List;

public class JdnAdapter  extends ArrayAdapter<JdnResponse> {

    List<JdnResponse> jdnList;
    Context mContext;
    String startDate;
    String endDate;
    SimpleDateFormat dateformats = null;
    CardView cardView;
    public JdnAdapter(@NonNull Context context, int resource, List<JdnResponse> jdnList) {
        super(context, resource, jdnList);
        this.mContext = context;
        this.jdnList = jdnList;




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            dateformats = new SimpleDateFormat("MMM dd yyyy");
        }



        for(int i=0;i<jdnList.size();i++){


            Config.logV("DisplayNote",jdnList.get(i).getDisplayNote());
            Config.logV("DiscPercentage",jdnList.get(i).getDiscPercentage());
            Config.logV("DiscMax",jdnList.get(i).getDiscMax());
            Log.i("DiscPercentage",jdnList.get(i).getDiscPercentage());




//            Date startdate = new Date(jdnList.get(i).getStartdate());
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                startDate=dateformats.format(startdate);
//            }
//
//            Date enddate = new Date(coupanList.get(i).getEnddate());
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                endDate=dateformats.format(enddate);
//            }

        }

    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


//        View listItem = convertView;
//        if(listItem == null)
//            listItem = LayoutInflater.from(this.getContext()).inflate(R.layout.couponview,parent,false);
//
//
//        CoupnResponse coupnResponse = couponList.get(position);
//        CardView mCardview =(CardView) listItem.findViewById(R.id.card);
//
//
//        Date startdate = new Date(couponList.get(position).getStartdate());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            startDate=dateformats.format(startdate);
//        }
//
//        Date enddate = new Date(couponList.get(position).getEnddate());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            endDate=dateformats.format(enddate);
//        }
//
//        if(!couponList.get(position).isFirstCheckinOnly()){mCardview.setVisibility(View.GONE);}
//
//        TextView mcouponCode = (TextView) listItem.findViewById(R.id.couponCode);
//        mcouponCode.setText(couponList.get(position).getJaldeeCouponCode());
//
//
//        TextView mcouponDesc = (TextView) listItem.findViewById(R.id.couponDesc);
//        mcouponDesc.setText(coupnResponse.getCouponDescription());
//
//        TextView mcouponTerms = (TextView) listItem.findViewById(R.id.couponTerms);
//        mcouponTerms.setText(coupnResponse.getConsumerTermsAndconditions());
//
//        TextView mcouponDisc = (TextView) listItem.findViewById(R.id.couponDisc);
//        mcouponDisc.setText(coupnResponse.getDiscountValue());
//
//        TextView mcouponName = (TextView) listItem.findViewById(R.id.couponName);
//        mcouponName.setText(coupnResponse.getCouponName());
//
//
//        TextView mvalidity = (TextView) listItem.findViewById(R.id.validityvaluetext);
//        mvalidity.setText(startDate+"-"+endDate);
//
//
//
        return null;
    }

}
