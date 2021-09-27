package com.jaldeeinc.jaldee.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.BorderImageView;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewItalicSemiBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;
import com.jaldeeinc.jaldee.custom.ViewNotesDialog;
import com.jaldeeinc.jaldee.response.ItemDetails;
import com.jaldeeinc.jaldee.response.ProviderCouponResponse;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.omjoonkim.skeletonloadingview.SkeletonLoadingView;
import com.squareup.picasso.Callback;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

public class ProviderCouponsAdapter extends RecyclerView.Adapter<ProviderCouponsAdapter.ViewHolder> {

    ArrayList<ProviderCouponResponse> itemsList;
    public Context context;
    SimpleDateFormat dateformats = null;
    String startDate;
    String endDate;
    String checkedInProviders;
    String accountId = null;

    public ProviderCouponsAdapter(ArrayList<ProviderCouponResponse> itemsList, Context context, String accId) {
        this.context = context;
        this.itemsList = itemsList;
        this.accountId = accId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            dateformats = new SimpleDateFormat("MMM dd, yyyy");
        }

        checkedInProviders = SharedPreference.getInstance(context).getStringValue("checkedInProviders", null);


    }

    @NonNull
    @Override
    public ProviderCouponsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.couponview, viewGroup, false);
        return new ProviderCouponsAdapter.ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final ProviderCouponsAdapter.ViewHolder viewHolder, final int position) {

        final ProviderCouponResponse couponResponse = itemsList.get(position);

        if (couponResponse != null) {


            Date startdate = new Date(couponResponse.getCouponRules().getStartDate());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                startDate = dateformats.format(startdate);
            }

            Date enddate = new Date(couponResponse.getCouponRules().getEndDate());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                endDate = dateformats.format(enddate);
            }

            if (couponResponse.getName() != null) {
                viewHolder.tvCouponName.setText(couponResponse.getName());
            }

            if (couponResponse.getCouponCode() != null) {
                viewHolder.tvCode.setText(couponResponse.getCouponCode());
            }

            if (couponResponse.getDescription() != null) {
                viewHolder.tvDescription.setText(couponResponse.getDescription());
            }

            if (couponResponse.getCouponRules() != null && couponResponse.getCouponRules().getMinBillAmount() != null) {

                viewHolder.tvMinBill.setText("₹" + " " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble((couponResponse.getCouponRules().getMinBillAmount()))));

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

            if (couponResponse.getCouponRules() != null && couponResponse.getCouponRules().isFirstCheckinOnly()) {
                if (checkedInProviders != null && checkedInProviders.contains(accountId)) {

                    viewHolder.llCoupon.setVisibility(View.GONE);
                    viewHolder.llSignUpCoupon.setVisibility(View.GONE);
                } else {
                    viewHolder.llCoupon.setVisibility(View.VISIBLE);
                    viewHolder.llSignUpCoupon.setVisibility(View.VISIBLE);
                }
            } else {
                viewHolder.llCoupon.setVisibility(View.VISIBLE);
                viewHolder.llSignUpCoupon.setVisibility(View.GONE);
            }


            if (couponResponse.getCouponRules() != null && couponResponse.getCalculationType().equals("Percentage")) {
                int i = (int) couponResponse.getAmount();
                String amount = Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(couponResponse.getCouponRules().getMaxDiscountValue()));
                viewHolder.tvDiscount.setText(i + "%" + "(Upto " + "₹" + amount + " MAX)");
            } else {
                viewHolder.tvDiscount.setText("₹" + " " + Config.getAmountNoOrTwoDecimalPoints(couponResponse.getAmount()));

            }

            if (couponResponse.getTermsConditions() != null && !couponResponse.getTermsConditions().trim().equalsIgnoreCase("")) {
                viewHolder.llTerms.setVisibility(View.VISIBLE);
                viewHolder.tvTerms.setText(couponResponse.getTermsConditions());
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

        return itemsList.size();
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

}