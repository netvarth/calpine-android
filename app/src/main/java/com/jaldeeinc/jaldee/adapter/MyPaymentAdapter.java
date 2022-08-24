package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.PaymentDetail;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewRegularItalic;
import com.jaldeeinc.jaldee.response.MyPayments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyPaymentAdapter extends ArrayAdapter<MyPayments> {

    List<MyPayments> paymentsList;
    Context mContext;


    public MyPaymentAdapter(@NonNull Context context, int resource, List<MyPayments> paymentsList) {
        super(context, resource, paymentsList);

        this.mContext = context;
        this.paymentsList = paymentsList;

    }

    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(this.getContext()).inflate(R.layout.paymentview, parent, false);


        MyPayments myPayments = paymentsList.get(position);

        CustomTextViewBold accountName = listItem.findViewById(R.id.accountName);
        CustomTextViewBold amount = listItem.findViewById(R.id.amount);
        CustomTextViewMedium paymentOn = listItem.findViewById(R.id.paymentOn);
        CustomTextViewRegularItalic tvStatus = listItem.findViewById(R.id.tv_status);
        ImageView ivIcon = listItem.findViewById(R.id.iv_icon);
        CardView card = (CardView) listItem.findViewById(R.id.card);

        if (paymentsList.get(position).getAccountName() != null) {
            accountName.setText(convertToTitleForm(paymentsList.get(position).getAccountName()));
            accountName.setVisibility(View.VISIBLE);
        } else {
            accountName.setVisibility(View.INVISIBLE);
        }

        if (paymentsList.get(position).getAmount() != null) {
            amount.setText("₹\u00A0" + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(paymentsList.get(position).getAmount())));
            amount.setVisibility(View.VISIBLE);
        } else {
            amount.setVisibility(View.INVISIBLE);
        }

        if (paymentsList.get(position).getStatus() != null) {

            tvStatus.setVisibility(View.VISIBLE);
            tvStatus.setText(convertToTitleForm(paymentsList.get(position).getStatus()));
            if (paymentsList.get(position).getStatus().equalsIgnoreCase("Success")) {
                tvStatus.setTextColor(mContext.getResources().getColor(R.color.green));
            } else {
                tvStatus.setTextColor(mContext.getResources().getColor(R.color.red));
            }
        } else {
            tvStatus.setVisibility(View.GONE);
        }

        if (paymentsList.get(position).getTxnType() != null) {

            ivIcon.setVisibility(View.VISIBLE);
            if (paymentsList.get(position).getTxnType().equalsIgnoreCase("Waitlist")) {
                if(paymentsList.get(position).isShowTokenId()){
                    ivIcon.setImageResource(R.drawable.icon_token);
                }else {
                    ivIcon.setImageResource(R.drawable.icon_checkin);
                }
            } else if (paymentsList.get(position).getTxnType().equalsIgnoreCase("Appointment")) {
                ivIcon.setImageResource(R.drawable.appt_icon);
            } else if (paymentsList.get(position).getTxnType().equalsIgnoreCase("Donation")) {
                ivIcon.setImageResource(R.drawable.icon_donate);
                ivIcon.setColorFilter(getContext().getResources().getColor(R.color.location_theme));
            } else if (paymentsList.get(position).getTxnType().equalsIgnoreCase("Order")){
                ivIcon.setImageResource(R.drawable.order_icon);
            }
        } else {
            ivIcon.setVisibility(View.GONE);
        }

        if (paymentsList.get(position).getPaymentOn() != null) {

            String date = "";
            try {
                 date = getCustomDateString(paymentsList.get(position).getPaymentOn().split(" ")[0]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String time = convertTime(paymentsList.get(position).getPaymentOn().split(" ")[1]);

            paymentOn.setText(date+" "+ time);
            paymentOn.setVisibility(View.VISIBLE);
        } else {
            paymentOn.setVisibility(View.INVISIBLE);
        }

        accountName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent iPaymentDetail = new Intent(v.getContext(), PaymentDetail.class);
                iPaymentDetail.putExtra("myPaymentID", String.valueOf(paymentsList.get(position).getId()));
                iPaymentDetail.putExtra("txnType",paymentsList.get(position).getTxnType());
                iPaymentDetail.putExtra("uuid",paymentsList.get(position).getYnwUuid());
                mContext.startActivity(iPaymentDetail);
            }

        });

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent iPaymentDetail = new Intent(v.getContext(), PaymentDetail.class);
                iPaymentDetail.putExtra("myPaymentID", String.valueOf(paymentsList.get(position).getId()));
                iPaymentDetail.putExtra("txnType",paymentsList.get(position).getTxnType());
                iPaymentDetail.putExtra("uuid",paymentsList.get(position).getYnwUuid());
                mContext.startActivity(iPaymentDetail);
            }

        });


        return listItem;
    }

    public static String convertTime(String time) {

        String formattedTime = "";
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            final Date dateObj = sdf.parse(time);
            time = new SimpleDateFormat("hh:mm aa").format(dateObj);
            formattedTime = time.replace("am", "AM").replace("pm", "PM");

        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return formattedTime;
    }


    public static String convertToTitleForm(String name) {

        String convertName = name;
        convertName = convertName.substring(0, 1).toUpperCase() + convertName.substring(1).toLowerCase();
        return convertName;
    }

    public static String getCustomDateString(String d) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = format.parse(d);
        String date = format.format(date1);

        if (date.endsWith("1") && !date.endsWith("11"))
            format = new SimpleDateFormat("d'st' MMM, yyyy");

        else if (date.endsWith("2") && !date.endsWith("12"))
            format = new SimpleDateFormat("d'nd' MMM, yyyy");

        else if (date.endsWith("3") && !date.endsWith("13"))
            format = new SimpleDateFormat("d'rd' MMM, yyyy");

        else
            format = new SimpleDateFormat("d'th' MMM, yyyy");

        String yourDate = format.format(date1);

        return yourDate;
    }

}
