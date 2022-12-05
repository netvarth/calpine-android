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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.CouponActivity;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.response.CoupnResponse;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CouponsAdapter extends RecyclerView.Adapter<CouponsAdapter.ViewHolder> {

    List<CoupnResponse> couponList;
    public Context context;
    SimpleDateFormat dateformats = null;
    String startDate;
    String endDate;
    boolean isFirstForConsumer = false;

    public CouponsAdapter(CouponActivity context, List<CoupnResponse> itemsList) {
        this.context = context;
        this.couponList = itemsList;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            dateformats = new SimpleDateFormat("MMM dd, yyyy");
        }
        isFirstForConsumer = SharedPreference.getInstance(context).getBoolanValue("firstBooking", false);
        couponList = rmvCpnsNotSupportThisCnsmr(isFirstForConsumer, couponList);//this method for remove firstcheckinOnly copouns if the consumer not isFirstForConsumer is FALSE(not a first checkin)
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.couponview, viewGroup, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {

        final CoupnResponse couponResponse = couponList.get(position);
        if (couponResponse != null) {


            Date startdate = new Date(couponResponse.getStartdate());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                startDate = dateformats.format(startdate);
            }

            Date enddate = new Date(couponResponse.getEnddate());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                endDate = dateformats.format(enddate);
            }

            if (couponResponse.getCouponName() != null) {
                viewHolder.tvCouponName.setText(couponResponse.getCouponName());
            }

            if (couponResponse.getJaldeeCouponCode() != null) {
                viewHolder.tvCode.setText(couponResponse.getJaldeeCouponCode());
            }

            if (couponResponse.getCouponDescription() != null) {
                viewHolder.tvDescription.setText(couponResponse.getCouponDescription());
            }

            if (couponResponse.getMinBillAmount() != null) {

                viewHolder.tvMinBill.setText("₹" + " " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble((couponResponse.getMinBillAmount()))));
            }

            viewHolder.ivCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Animation animFadein = AnimationUtils.loadAnimation(context, R.anim.cb_fade_in);

                    viewHolder.ivCopy.startAnimation(animFadein);

                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Coupon Code", viewHolder.tvCode.getText().toString());
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(context, "Coupon Code Copied", Toast.LENGTH_SHORT).show();
                }
            });

            if (couponResponse.isFirstCheckinOnly()) {
                if (isFirstForConsumer) {

                    viewHolder.llCoupon.setVisibility(View.VISIBLE);
                    viewHolder.llSignUpCoupon.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.llCoupon.setVisibility(View.GONE);
                    viewHolder.llSignUpCoupon.setVisibility(View.GONE);
                }
            } else {
                viewHolder.llCoupon.setVisibility(View.VISIBLE);
                viewHolder.llSignUpCoupon.setVisibility(View.GONE);
            }

            if (couponResponse.getDiscountType().equals("AMOUNT")) {
                viewHolder.tvDiscount.setText("₹" + " " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble((couponResponse.getDiscountValue()))));
            } else {
                viewHolder.tvDiscount.setText(couponResponse.getDiscountValue() + "%");
            }


            if (couponResponse.getConsumerTermsAndconditions() != null && !couponResponse.getConsumerTermsAndconditions().trim().equalsIgnoreCase("")) {
                viewHolder.llTerms.setVisibility(View.VISIBLE);
                viewHolder.tvTerms.setText(couponResponse.getConsumerTermsAndconditions());
            } else {
                viewHolder.llTerms.setVisibility(View.GONE);
            }

            if (startDate != null && endDate != null) {
                viewHolder.tvValidity.setText(startDate + "-" + endDate);
            } else {
                viewHolder.tvValidity.setText("");
            }

        }

    }


    @Override
    public int getItemCount() {
        return couponList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CustomTextViewSemiBold tvCouponName, tvTerms, tvDiscount, tvValidity, tvMinBill;
        CustomTextViewMedium tvDescription;
        CustomTextViewBold tvCode;
        ImageView ivCopy;
        LinearLayout llSignUpCoupon, llTerms, llCoupon;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCouponName = itemView.findViewById(R.id.couponName);
            tvTerms = itemView.findViewById(R.id.couponTerms);
            tvDiscount = itemView.findViewById(R.id.couponDisc);
            tvValidity = itemView.findViewById(R.id.validityvaluetext);
            tvDescription = itemView.findViewById(R.id.couponDesc);
            tvCode = itemView.findViewById(R.id.couponCode);
            tvMinBill = itemView.findViewById(R.id.tv_minBill);
            ivCopy = itemView.findViewById(R.id.iv_copy);
            llSignUpCoupon = itemView.findViewById(R.id.ll_signUpCoupon);
            llTerms = itemView.findViewById(R.id.termlayout);
            llCoupon = itemView.findViewById(R.id.ll_coupon);

        }
    }

    private List<CoupnResponse> rmvCpnsNotSupportThisCnsmr(boolean isFirstForConsumer, List<CoupnResponse> couponList) {  //this method for remove firstcheckinOnly copouns if the consumer not isFirstForConsumer is FALSE(not a first checkin)
        CoupnResponse cr;
        ArrayList<CoupnResponse> ccpnlist = new ArrayList<CoupnResponse>();
        for (int i = 0; i < couponList.size(); i++) {
            cr = couponList.get(i);
            if (cr.isFirstCheckinOnly()) {
                if (isFirstForConsumer) {
                    ccpnlist.add(cr);
                    continue;
                }
            } else {
                ccpnlist.add(cr);
            }
        }
        return ccpnlist;
    }
}