package com.jaldeeinc.jaldee.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.enums.JCashRewrd;
import com.jaldeeinc.jaldee.response.JCashAvailable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class JCashListAdapter extends RecyclerView.Adapter<JCashListAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<JCashAvailable> listJCashAvailable;
    JsonObject jCashIssueInfo, jCashSpendRulesInfo;

    public JCashListAdapter(Context context, ArrayList<JCashAvailable> listJCashAvailable) {
        this.mContext = context;
        this.listJCashAvailable = listJCashAvailable;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CustomTextViewSemiBold tvRwrdEarned, tvRwrdSpent;
        CustomTextViewMedium tvRwrdExpiry, tvTAndc, tvJCashIssueDate, tvBookingNo, tvRewardName;

        public MyViewHolder(View view) {
            super(view);
            tvRewardName = (CustomTextViewMedium) view.findViewById(R.id.tv_rewardName);
            tvBookingNo = (CustomTextViewMedium) view.findViewById(R.id.tv_bookingNo);
            tvJCashIssueDate = (CustomTextViewMedium) view.findViewById(R.id.tv_jCashIssueDate);
            tvRwrdEarned = (CustomTextViewSemiBold) view.findViewById(R.id.tv_rwrd_earned);
            tvRwrdSpent = (CustomTextViewSemiBold) view.findViewById(R.id.tv_rwrd_spent);
            tvRwrdExpiry = (CustomTextViewMedium) view.findViewById(R.id.tv_rwrd_expiry);
            tvTAndc = (CustomTextViewMedium) view.findViewById(R.id.tv_t_and_c);

        }
    }

    @Override
    public JCashListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.jcash_awards_view, parent, false);

        return new JCashListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final JCashListAdapter.MyViewHolder myViewHolder, final int position) {
        final JCashAvailable jCashReward = listJCashAvailable.get(position);

        jCashIssueInfo = jCashReward.getjCashIssueInfo().getAsJsonObject();
        jCashSpendRulesInfo = jCashReward.getjCashSpendRulesInfo().getAsJsonObject();

        if (jCashIssueInfo != null) {
            String issuedDtStr = jCashIssueInfo.get("issuedDt").getAsString();
            DateTimeFormatter issuedDtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate issuedlocalDate = LocalDate.parse(issuedDtStr);
            myViewHolder.tvJCashIssueDate.setText(issuedlocalDate.format(issuedDtf));
        }
        if(jCashSpendRulesInfo != null){
            String expiryDtStr = jCashSpendRulesInfo.get("expiryDt").getAsString();
            DateTimeFormatter expiryDtf = DateTimeFormatter.ofPattern("MMM dd");
            LocalDate expirylocalDate = LocalDate.parse(expiryDtStr);
            myViewHolder.tvRwrdExpiry.setText("Expires on "+expirylocalDate.format(expiryDtf)+"th");
        }

        myViewHolder.tvRewardName.setText(jCashReward.getjCashOffer().get("name").getAsString());
        myViewHolder.tvRwrdEarned.setText(Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(jCashReward.getOriginalAmt())));
        if (jCashReward.getOriginalAmt() != null && jCashReward.getRemainingAmt() != null) {
            float spent = Float.parseFloat(jCashReward.getOriginalAmt()) - Float.parseFloat(jCashReward.getRemainingAmt());
            if (spent >= 0) {
                myViewHolder.tvRwrdSpent.setText(Config.getAmountNoOrTwoDecimalPoints(spent));
            }
        }

        myViewHolder.tvTAndc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("T & C")
                        .setMessage(jCashReward.getDisplayNote())
                        .setCancelable(true)
                        .setPositiveButton("close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                //Creating dialog box
                AlertDialog dialog  = builder.create();
                dialog.show();
            }
        });

        }

    @Override
    public int getItemCount() {
        return listJCashAvailable.size();
    }
}
