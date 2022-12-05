package com.jaldeeinc.jaldee.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.response.CoupnResponse;

import java.util.Date;
import java.util.List;

public class CouponAdapter extends ArrayAdapter<CoupnResponse> {

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
            dateformats = new SimpleDateFormat("MMM dd, yyyy");
        }
        for (int i = 0; i < coupanList.size(); i++) {

            Config.logV("Coupan List------------------------" + couponList.get(i).getJaldeeCouponCode());
            Config.logV("CouponDesp", couponList.get(i).getCouponDescription());
            Config.logV("CouponTerms", couponList.get(i).getConsumerTermsAndconditions());
            Config.logV("CouponDiscount", couponList.get(i).getDiscountValue());
            Config.logV("CouponName", couponList.get(i).getCouponName());
            Config.logV("CouponStartdate", String.valueOf(coupanList.get(i).getStartdate()));
            Config.logV("CouponEndDate", String.valueOf(coupanList.get(i).getEnddate()));

            Date startdate = new Date(coupanList.get(i).getStartdate());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                startDate = dateformats.format(startdate);
            }

            Date enddate = new Date(coupanList.get(i).getEnddate());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                endDate = dateformats.format(enddate);
            }

        }

    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(this.getContext()).inflate(R.layout.couponview, parent, false);


        CoupnResponse coupnResponse = couponList.get(position);

        CardView mCardview = (CardView) listItem.findViewById(R.id.card);

        Date startdate = new Date(couponList.get(position).getStartdate());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            startDate = dateformats.format(startdate);
        }

        Date enddate = new Date(couponList.get(position).getEnddate());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            endDate = dateformats.format(enddate);
        }

        TextView mcouponCode = (TextView) listItem.findViewById(R.id.couponCode);
        mcouponCode.setText(couponList.get(position).getJaldeeCouponCode());


        TextView mcouponDesc = (TextView) listItem.findViewById(R.id.couponDesc);
        mcouponDesc.setText(coupnResponse.getCouponDescription());

        TextView mcouponTerms = (TextView) listItem.findViewById(R.id.couponTerms);
        mcouponTerms.setText(coupnResponse.getConsumerTermsAndconditions());

        if (coupnResponse.getDiscountType().equals("AMOUNT")) {
            TextView mcouponDisc = (TextView) listItem.findViewById(R.id.couponDisc);
            mcouponDisc.setText("₹" + " " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble((coupnResponse.getDiscountValue()))));
        } else {
            TextView mcouponDisc = (TextView) listItem.findViewById(R.id.couponDisc);
            mcouponDisc.setText(coupnResponse.getDiscountValue() + "%");
        }

        ImageView ivCopy = listItem.findViewById(R.id.iv_copy);
        ivCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation animFadein = AnimationUtils.loadAnimation(mContext, R.anim.cb_fade_in);

                ivCopy.startAnimation(animFadein);

                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied", mcouponCode.getText().toString());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(mContext, "Coupon Code Copied", Toast.LENGTH_SHORT).show();
            }
        });

        LinearLayout llSignUpCoupon = listItem.findViewById(R.id.ll_signUpCoupon);

        if (coupnResponse.isFirstCheckinOnly()) {
            llSignUpCoupon.setVisibility(View.VISIBLE);
        } else {
            llSignUpCoupon.setVisibility(View.GONE);
        }

        CustomTextViewSemiBold tvMinBill = listItem.findViewById(R.id.tv_minBill);

        if (coupnResponse.getMinBillAmount() != null){

            tvMinBill.setText("₹" + " " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble((coupnResponse.getMinBillAmount()))));
        }


        TextView mcouponName = (TextView) listItem.findViewById(R.id.couponName);
        mcouponName.setText(coupnResponse.getCouponName());

        TextView mvalidity = (TextView) listItem.findViewById(R.id.validityvaluetext);
        if (startDate != null && endDate != null) {
            mvalidity.setText(startDate + "-" + endDate);
        } else {
            mvalidity.setText("");
        }


        return listItem;
    }

}
