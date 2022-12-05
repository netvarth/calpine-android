package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.JCashSpentLogDialog;
import com.jaldeeinc.jaldee.response.JCashExpired;
import com.jaldeeinc.jaldee.response.JCashSpentDetails;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;


public class JCashExpiredListAdapter extends RecyclerView.Adapter<JCashExpiredListAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<JCashExpired> jCashExpireds;
    JsonObject jCashIssueInfo, jCashSpendRulesInfo;
    JCashSpentLogDialog jCashSpentLogDialog;
    ArrayList<JCashSpentDetails> listJCashSpentDetails = new ArrayList<JCashSpentDetails>();

    public JCashExpiredListAdapter(Context context, ArrayList<JCashExpired> jCashExpireds) {
        this.mContext = context;
        this.jCashExpireds = jCashExpireds;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CustomTextViewSemiBold tvRwrdEarned, tvRwrdSpent;
        CustomTextViewMedium tvRwrdExpiry, tvTAndc, tvJCashIssueDate, tvBookingNo, tvRewardName;
        LinearLayout llJCashSpentLog;
        RelativeLayout rlLayout;

        public MyViewHolder(View view) {
            super(view);
            tvRewardName = (CustomTextViewMedium) view.findViewById(R.id.tv_rewardName);
            tvBookingNo = (CustomTextViewMedium) view.findViewById(R.id.tv_bookingNo);
            tvJCashIssueDate = (CustomTextViewMedium) view.findViewById(R.id.tv_jCashIssueDate);
            tvRwrdEarned = (CustomTextViewSemiBold) view.findViewById(R.id.tv_rwrd_earned);
            tvRwrdSpent = (CustomTextViewSemiBold) view.findViewById(R.id.tv_rwrd_spent);
            tvRwrdExpiry = (CustomTextViewMedium) view.findViewById(R.id.tv_rwrd_expiry);
            tvTAndc = (CustomTextViewMedium) view.findViewById(R.id.tv_t_and_c);
            llJCashSpentLog = (LinearLayout) view.findViewById(R.id.ll_jCashSpentLog);
            rlLayout = (RelativeLayout) view.findViewById(R.id.rl_layout);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.jcash_awards_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int position) {
        final JCashExpired jCashExpiredReward = jCashExpireds.get(position);

        myViewHolder.rlLayout.animate().alpha(0.5f);
        myViewHolder.tvTAndc.setVisibility(View.GONE);

        jCashIssueInfo = jCashExpiredReward.getjCash().getjCashIssueInfo().getAsJsonObject();
        jCashSpendRulesInfo = jCashExpiredReward.getjCash().getjCashSpendRulesInfo().getAsJsonObject();

        if (jCashIssueInfo != null) {
            String issuedDtStr = jCashIssueInfo.get("issuedDt").getAsString();
            DateTimeFormatter issuedDtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate issuedlocalDate = LocalDate.parse(issuedDtStr);
            myViewHolder.tvJCashIssueDate.setText(issuedlocalDate.format(issuedDtf));
        }
        if (jCashSpendRulesInfo != null) {
            try {
                String date = getCustomDateString(jCashSpendRulesInfo.get("expiryDt").getAsString());
                myViewHolder.tvRwrdExpiry.setText("Expired on " + date);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        myViewHolder.tvRewardName.setText(jCashExpiredReward.getjCash().getjCashOffer().get("name").getAsString());
        myViewHolder.tvRwrdEarned.setText(Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(jCashExpiredReward.getjCash().getOriginalAmt())));
        if (jCashExpiredReward.getjCash().getOriginalAmt() != null && jCashExpiredReward.getjCash().getRemainingAmt() != null) {
            float spentedAmount = Float.parseFloat(jCashExpiredReward.getjCash().getOriginalAmt()) - Float.parseFloat(jCashExpiredReward.getjCash().getRemainingAmt());
            if (spentedAmount >= 0) {
                myViewHolder.tvRwrdSpent.setText(Config.getAmountNoOrTwoDecimalPoints(spentedAmount));
            }
        }
        /*myViewHolder.tvTAndc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("T & C")
                        .setMessage(jCashExpiredReward.getjCash().getDisplayNote())
                        .setCancelable(true)
                        .setPositiveButton("close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                //Creating dialog box
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });*/
        /*myViewHolder.llJCashSpentLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float spentedAmount = 0;
                if (jCashExpiredReward.getjCash().getOriginalAmt() != null && jCashExpiredReward.getjCash().getRemainingAmt() != null) {
                    spentedAmount = Float.parseFloat(jCashExpiredReward.getjCash().getOriginalAmt()) - Float.parseFloat(jCashExpiredReward.getjCash().getRemainingAmt());
                }
                if (spentedAmount > 0) {
                    ApiGetJcashSpentDetails(Integer.parseInt(jCashExpiredReward.getId()));
                }
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return jCashExpireds.size();
    }

    /*private void ApiGetJcashSpentDetails(int jCashId) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, "");
        mDialog.show();

        Call<ArrayList<JCashSpentDetails>> call = apiService.getJCashSpentDetails(jCashId);
        call.enqueue(new Callback<ArrayList<JCashSpentDetails>>() {
            @Override
            public void onResponse(Call<ArrayList<JCashSpentDetails>> call, Response<ArrayList<JCashSpentDetails>> response) {
                try {
                    if (mDialog.isShowing()) {
                        Config.closeDialog((Activity) mContext, mDialog);
                    }
                    if (response.code() == 200) {
                        listJCashSpentDetails = response.body();
                        Config.logV("Jaldee Cash Spent details--code-------------------------" + listJCashSpentDetails);

                        jCashSpentLogDialog = new JCashSpentLogDialog(mContext, listJCashSpentDetails);
                        jCashSpentLogDialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogStyle_Default;
                        jCashSpentLogDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        jCashSpentLogDialog.show();
                        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                        int width = (int) (metrics.widthPixels * 1);
                        jCashSpentLogDialog.setCancelable(false);
                        jCashSpentLogDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<JCashSpentDetails>> call, Throwable t) {

            }
        });
    }*/
    public static String getCustomDateString(String d) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = format.parse(d);
        String date = format.format(date1);

        if (date.endsWith("1") && !date.endsWith("11"))
            format = new SimpleDateFormat("MMM d'st', yyyy");

        else if (date.endsWith("2") && !date.endsWith("12"))
            format = new SimpleDateFormat("MMM d'nd', yyyy");

        else if (date.endsWith("3") && !date.endsWith("13"))
            format = new SimpleDateFormat("MMM d'rd', yyyy");

        else
            format = new SimpleDateFormat("MMM d'th', yyyy");

        String yourDate = format.format(date1);

        return yourDate;
    }
}
