package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.response.JCashSpentDetails;

import java.text.ParseException;
import java.util.ArrayList;

public class JCashSpentLogAdapter extends RecyclerView.Adapter<JCashSpentLogAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<JCashSpentDetails> listJCashSpentDetails;

    public JCashSpentLogAdapter(Context mContext, ArrayList<JCashSpentDetails> listJCashSpentDetails) {
        this.mContext = mContext;
        this.listJCashSpentDetails = listJCashSpentDetails;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CustomTextViewMedium tv_spent_to, tv_amount_spent_or_refunded, tv_date;
        public LinearLayout ll_jcash_spent_list_raw;

        public MyViewHolder(View view) {
            super(view);
            ll_jcash_spent_list_raw = (LinearLayout) view.findViewById(R.id.ll_jcash_spent_list_raw);
            tv_spent_to = (CustomTextViewMedium) view.findViewById(R.id.tv_spent_to);
            tv_amount_spent_or_refunded = (CustomTextViewMedium) view.findViewById(R.id.tv_amount_spent_or_refunded);
            tv_date = (CustomTextViewMedium) view.findViewById(R.id.tv_date);
        }
    }

    @Override
    public JCashSpentLogAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.jcash_spentlog_list_raw, parent, false);

        return new JCashSpentLogAdapter.MyViewHolder(itemView);
    }

    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final JCashSpentLogAdapter.MyViewHolder myViewHolder, int position) {
        int rowPos = position;
        if (rowPos == 0) {
            // Header Cells. Main Headings appear here
            myViewHolder.tv_spent_to.setText("Spent to");
            myViewHolder.tv_amount_spent_or_refunded.setText("Amount Spent/Refunded");
            myViewHolder.tv_date.setText("Date");
        } else {
            JCashSpentDetails jCashSpentDetails = listJCashSpentDetails.get(rowPos - 1);
            String amount = Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(jCashSpentDetails.getAmount()));
            String date = "";
            try {
                date = Config.getCustomDateString(jCashSpentDetails.getCreatedDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // Content Cells. Content appear here

            myViewHolder.tv_spent_to.setText(jCashSpentDetails.getSpentToBizName());
            if (jCashSpentDetails.getjCashTxnType() != null && Constants.REFUNDED.equalsIgnoreCase(jCashSpentDetails.getjCashTxnType())) {
                myViewHolder.tv_amount_spent_or_refunded.setText("(+) ₹" + amount);
                myViewHolder.tv_amount_spent_or_refunded.setTextColor(mContext.getResources().getColor(R.color.green) );
            } else {
                myViewHolder.tv_amount_spent_or_refunded.setText("₹" + amount);
                myViewHolder.tv_amount_spent_or_refunded.setTextColor(Color.RED);
            }
            myViewHolder.tv_date.setText(date);
        }
        if (rowPos % 2 != 0) {
            //myViewHolder.ll_jcash_spent_list_raw.setBackground(mContext.getResources().getDrawable(R.color.spinnerbg));
            myViewHolder.ll_jcash_spent_list_raw.setBackgroundColor(Color.parseColor("#f9f9f9"));
        }
    }

    @Override
    public int getItemCount() {
        return listJCashSpentDetails.size() + 1;
    }
}
