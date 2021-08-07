package com.jaldeeinc.jaldee.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.JCashSpentLogDialog;
import com.jaldeeinc.jaldee.response.JCash;
import com.jaldeeinc.jaldee.response.JCashSpentDetails;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JCashAvailableListAdapter extends RecyclerView.Adapter<JCashAvailableListAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<JCash> JCash;
    JsonObject jCashIssueInfo, jCashSpendRulesInfo;
    JCashSpentLogDialog jCashSpentLogDialog;
    ArrayList<JCashSpentDetails> listJCashSpentDetails = new ArrayList<JCashSpentDetails>();

    public JCashAvailableListAdapter(Context context, ArrayList<JCash> JCash) {
        this.mContext = context;
        this.JCash = JCash;
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
    public JCashAvailableListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.jcash_awards_view, parent, false);

        return new JCashAvailableListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final JCashAvailableListAdapter.MyViewHolder myViewHolder, final int position) {
        final JCash jCashReward = JCash.get(position);
        jCashIssueInfo = jCashReward.getjCashIssueInfo().getAsJsonObject();
        jCashSpendRulesInfo = jCashReward.getjCashSpendRulesInfo().getAsJsonObject();

        if (jCashIssueInfo != null) {
            String issuedDtStr = jCashIssueInfo.get("issuedDt").getAsString();
            DateTimeFormatter issuedDtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate issuedlocalDate = LocalDate.parse(issuedDtStr);
            myViewHolder.tvJCashIssueDate.setText(issuedlocalDate.format(issuedDtf));
        }
        if (jCashSpendRulesInfo != null) {
            try {
                String date = getCustomDateString(jCashSpendRulesInfo.get("expiryDt").getAsString());
                myViewHolder.tvRwrdExpiry.setText("Expires on " + date);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //myViewHolder.rlLayout.animate().alpha(0.5f);
        myViewHolder.tvRewardName.setText(jCashReward.getjCashOffer().get("name").getAsString());
        myViewHolder.tvRwrdEarned.setText(Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(jCashReward.getOriginalAmt())));
        if (jCashReward.getOriginalAmt() != null && jCashReward.getRemainingAmt() != null) {
            float spentedAmount = Float.parseFloat(jCashReward.getOriginalAmt()) - Float.parseFloat(jCashReward.getRemainingAmt());
            if (spentedAmount >= 0) {
                myViewHolder.tvRwrdSpent.setText(Config.getAmountNoOrTwoDecimalPoints(spentedAmount));
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
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        myViewHolder.llJCashSpentLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float spentedAmount = 0;
                if (jCashReward.getOriginalAmt() != null && jCashReward.getRemainingAmt() != null) {
                    spentedAmount = Float.parseFloat(jCashReward.getOriginalAmt()) - Float.parseFloat(jCashReward.getRemainingAmt());
                }
                if (spentedAmount > 0) {
                    ApiGetJcashSpentDetails(Integer.parseInt(jCashReward.getId()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return JCash.size();
    }

    private void ApiGetJcashSpentDetails(int jCashId) {

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
    }
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
