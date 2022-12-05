package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.response.RefundInformation;

import java.util.ArrayList;

public class RefundDetailsListAdapter extends RecyclerView.Adapter<RefundDetailsListAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<RefundInformation> listRefundDetails;

    public RefundDetailsListAdapter(Context mContext, ArrayList<RefundInformation> listRefundDetails) {
        this.mContext = mContext;
        this.listRefundDetails = listRefundDetails;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CustomTextViewMedium tv_refund_status, tv_refund_mode, tv_refund_amount;
        public LinearLayout ll_refund_details_list_raw;

        public MyViewHolder(View view) {
            super(view);
            ll_refund_details_list_raw = (LinearLayout) view.findViewById(R.id.ll_refund_details_list_raw);
            tv_refund_status = (CustomTextViewMedium) view.findViewById(R.id.tv_refund_status);
            tv_refund_mode = (CustomTextViewMedium) view.findViewById(R.id.tv_refund_mode);
            tv_refund_amount = (CustomTextViewMedium) view.findViewById(R.id.tv_refund_amount);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.refund_details_list_raw, parent, false);

        return new MyViewHolder(itemView);
    }

    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, int position) {
        int rowPos = position;
        if (rowPos == 0) {
            // Header Cells. Main Headings appear here
            myViewHolder.tv_refund_status.setText("Status");
            myViewHolder.tv_refund_mode.setText("Mode");
            myViewHolder.tv_refund_amount.setText("Amount");
        } else {
            RefundInformation refundInformation = listRefundDetails.get(rowPos - 1);
            // Content Cells. Content appear here
            if (refundInformation.getStatus() != null) {
                myViewHolder.tv_refund_status.setText(refundInformation.getStatus());
            }
            if (refundInformation.getRefundMode() != null) {
                myViewHolder.tv_refund_mode.setText(refundInformation.getRefundMode());
            }
            if (refundInformation.getAmount() != null) {
                myViewHolder.tv_refund_amount.setText("₹\u00a0" + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(refundInformation.getAmount())));
            }
        }
        if (rowPos % 2 == 0) {
            myViewHolder.ll_refund_details_list_raw.setBackgroundColor(Color.parseColor("#dbd9d9"));
        }
    }

    @Override
    public int getItemCount() {
        return listRefundDetails.size() + 1;
    }
}
