package com.jaldeeinc.jaldee.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.callback.ActiveAdapterOnCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.CustomTypefaceSpan;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.payment.PaymentGateway;
import com.jaldeeinc.jaldee.payment.PaytmPayment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by sharmila on 13/7/18.
 */

public class ActiveCheckInAdapter extends RecyclerView.Adapter<ActiveCheckInAdapter.MyViewHolder> {

    private List<ActiveCheckIn> activeChekinList;
    Context mContext;
    ActiveAdapterOnCallback callback;

    public static String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_businessname, tv_estTime, tv_place, tv_status, tv_check_in, tv_queueTime;
        TextView icon_bill, tv_prepaid, tv_service,tv_makepay,tv_peopleahead;
        LinearLayout layout_btnpay;
        Button btn_pay;


        public MyViewHolder(View view) {
            super(view);
            tv_businessname = (TextView) view.findViewById(R.id.txt_businessname);
            tv_estTime = (TextView) view.findViewById(R.id.txt_esttime);
            icon_bill = (TextView) view.findViewById(R.id.icon_bill);
            tv_place = (TextView) view.findViewById(R.id.txt_location);
            layout_btnpay = (LinearLayout) view.findViewById(R.id.layout_btnpay);
            btn_pay = (Button) view.findViewById(R.id.btn_pay);
            tv_prepaid = (TextView) view.findViewById(R.id.txtprepaid);
            tv_status = (TextView) view.findViewById(R.id.txt_status);
            tv_service = (TextView) view.findViewById(R.id.txt_service);
            tv_makepay= (TextView) view.findViewById(R.id.txtmakepay);
            tv_peopleahead= (TextView) view.findViewById(R.id.txt_peopleahead);
            tv_check_in = (TextView) view.findViewById(R.id.txt_check_in);
            tv_queueTime = (TextView) view.findViewById(R.id.txt_queuetime);
        }
    }

    Activity activity;
    Fragment mFragment;

    public ActiveCheckInAdapter(List<ActiveCheckIn> mactiveChekinList, Context mContext, Activity mActivity, Fragment fragment, ActiveAdapterOnCallback callback) {
        this.mContext = mContext;
        this.activeChekinList = mactiveChekinList;
        this.activity = mActivity;
        this.mFragment = fragment;
        this.callback = callback;

    }

    @Override
    public ActiveCheckInAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.active_checkin_newrow, parent, false);

        return new ActiveCheckInAdapter.MyViewHolder(itemView);
    }

    static SimpleDateFormat inputParser = new SimpleDateFormat("HH:mm", Locale.US);
    private static Date dateCompareOne;

    private static Date parseDate(String date) {

        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    @Override
    public void onBindViewHolder(final ActiveCheckInAdapter.MyViewHolder myViewHolder, final int position) {
        final ActiveCheckIn activelist = activeChekinList.get(position);

        Config.logV("Provider NAme-------------------" + activelist.getBusinessName());
        myViewHolder.tv_businessname.setText(toTitleCase(activelist.getBusinessName()));

        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        myViewHolder.tv_businessname.setTypeface(tyface);

        myViewHolder.tv_businessname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onMethodActiveCallback(activelist.getUniqueId());
            }
        });


        myViewHolder.tv_status.setVisibility(View.VISIBLE);
        Typeface tyfacestatus = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        myViewHolder.tv_status.setTypeface(tyfacestatus);

        myViewHolder.tv_status.setText(activelist.getWaitlistStatus());
        if (activelist.getWaitlistStatus().equalsIgnoreCase("done")) {
            myViewHolder.tv_status.setText("Completed");
            myViewHolder.tv_status.setVisibility(View.VISIBLE);
            myViewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.green));
        }

        Config.logV("activelist.getPersonsAhead()"+activelist.getPersonsAhead());
        if(activelist.getPersonsAhead()!=-1){
            myViewHolder.tv_peopleahead.setVisibility(View.VISIBLE);
            // myViewHolder.tv_peopleahead.setText("People ahead of you : "+activelist.getPersonsAhead());
            String firstWord1 = "People ahead of you ";
            String secondWord1 = String.valueOf(activelist.getPersonsAhead());


            if(secondWord1.equals("0")){
                String nobody_ahead = "You are first in line ";
                Spannable spannable1 = new SpannableString(nobody_ahead);
                spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.sec_title_grey)),
                        0, nobody_ahead.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                Typeface tyface3 = Typeface.createFromAsset(mContext.getAssets(),
//                        "fonts/Montserrat_Bold.otf");
//                spannable1.setSpan(new CustomTypefaceSpan("sans-serif", tyface3), nobody_ahead.length(), nobody_ahead.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
//                        nobody_ahead.length(), nobody_ahead.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if(activelist.getAppxWaitingTime() == 0){
                    myViewHolder.tv_peopleahead.setText(spannable1 );}
                else{
                    myViewHolder.tv_peopleahead.setText(spannable1);
                }

            }
            else if(secondWord1.equals("1")){
                String one_ahead = "1 person ahead of you  ";
                Spannable spannable1 = new SpannableString(one_ahead);
                spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.sec_title_grey)),
                        0, one_ahead.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                Typeface tyface3 = Typeface.createFromAsset(mContext.getAssets(),
//                        "fonts/Montserrat_Bold.otf");
//                spannable1.setSpan(new CustomTypefaceSpan("sans-serif", tyface3), one_ahead.length(), one_ahead.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
//                        one_ahead.length(), one_ahead.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if(activelist.getAppxWaitingTime() == 0){
                    myViewHolder.tv_peopleahead.setText(spannable1 );}
                else{
                    myViewHolder.tv_peopleahead.setText(spannable1);}
            }
            else {

                Spannable spannable1 = new SpannableString(secondWord1 +" "+ firstWord1);
                spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.sec_title_grey)),
                        0, firstWord1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                Typeface tyface3 = Typeface.createFromAsset(mContext.getAssets(),
//                        "fonts/Montserrat_Bold.otf");
//                spannable1.setSpan(new CustomTypefaceSpan("sans-serif", tyface3), firstWord1.length(), firstWord1.length() + secondWord1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
//                        firstWord1.length(), firstWord1.length() + secondWord1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if(activelist.getAppxWaitingTime() == 0){
                    myViewHolder.tv_peopleahead.setText(spannable1 );}
                else{
                    myViewHolder.tv_peopleahead.setText(spannable1); }

            }


        }else{
            myViewHolder.tv_peopleahead.setVisibility(View.GONE);
        }

        if (activelist.getWaitlistStatus().equalsIgnoreCase("arrived")) {
            myViewHolder.tv_status.setText("Arrived");
            myViewHolder.tv_status.setVisibility(View.VISIBLE);
            myViewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.arrived_green));
        }

        if (activelist.getWaitlistStatus().equalsIgnoreCase("checkedIn")) {
            myViewHolder.tv_status.setVisibility(View.GONE);
          /*  myViewHolder.tv_status.setText("Checked In");
            myViewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.violet));*/
        }

        /*if(activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
            myViewHolder.tv_status.setText("Cancelled");
            myViewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
        }*/
        if (activelist.getWaitlistStatus().equalsIgnoreCase("started")) {
            myViewHolder.tv_status.setText("Started");
            myViewHolder.tv_status.setVisibility(View.VISIBLE);
            myViewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.cyan));
        }

        if (activelist.getWaitlistStatus().equalsIgnoreCase("prepaymentPending")) {
            myViewHolder.tv_status.setText("Prepayment Pending");
            myViewHolder.tv_status.setVisibility(View.VISIBLE);
            myViewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.gray));
        }


        try {
            if (activelist.getGoogleMapUrl() != null) {
                String geoUri = activelist.getGoogleMapUrl();
                if (geoUri!=null) {

                    myViewHolder.tv_place.setVisibility(View.VISIBLE);
                    myViewHolder.tv_place.setText(activelist.getPlace());
                } else {
                    myViewHolder.tv_place.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        /* if (activelist.getService() != null) {*/
        if (activelist.getName() != null) {
            myViewHolder.tv_service.setVisibility(View.VISIBLE);

            Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/Montserrat_Bold.otf");
            String firstWord = activelist.getName();
            String secondWord = " for ";
            String thirdWord = Config.toTitleCase(activelist.getFirstName() )+ " " + Config.toTitleCase(activelist.getLastName());
            Spannable spannable = new SpannableString(firstWord + secondWord + thirdWord);
            spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length() + secondWord.length(), firstWord.length() + secondWord.length() + thirdWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            myViewHolder.tv_service.setText(spannable);
        } else {
            myViewHolder.tv_service.setVisibility(View.GONE);
        }
        //  }


        myViewHolder.tv_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.logV("googlemap url--------" + activelist.getGoogleMapUrl());
                String geoUri = activelist.getGoogleMapUrl();
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                    mContext.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        if(activelist.getCheckInTime()!=null){
            Log.i("checkinTimeActivelist",activelist.getCheckInTime());
        }

        Config.logV("Bill------------" + activelist.getWaitlistStatus());
        if (!(activelist.getPaymentStatus().equalsIgnoreCase("FullyPaid"))&&(activelist.getBillViewStatus()!=null) || activelist.getWaitlistStatus().equalsIgnoreCase("prepaymentPending")) {
            if(activelist.getAmountDue()!=0) {

                if (activelist.getBillViewStatus()!=null && activelist.getBillViewStatus().equalsIgnoreCase("Show") && activelist.getAmountDue() > 0 &&  !activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                    myViewHolder.btn_pay.setVisibility(View.VISIBLE);
                    myViewHolder.btn_pay.setText("PAY");
                    if(activelist.getAmountDue()>0) {
                        myViewHolder.tv_prepaid.setVisibility(View.VISIBLE);
                        myViewHolder.tv_prepaid.setText("Amount Due: ₹ " + Config.getAmountinTwoDecimalPoints(activelist.getAmountDue()));
                    }else{
                        myViewHolder.tv_prepaid.setVisibility(View.GONE);
                    }
                }
                else{
                    myViewHolder.btn_pay.setVisibility(View.GONE);
                }
            }
            if (activelist.getWaitlistStatus().equalsIgnoreCase("prepaymentPending")) {
                myViewHolder.btn_pay.setVisibility(View.VISIBLE);
                myViewHolder.tv_makepay.setVisibility(View.VISIBLE);
                myViewHolder.btn_pay.setText("PAY");

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                Date time = new Date();
                Log.i("shankar",simpleDateFormat.format(time));

                String checkinTime = activelist.getDate() + " " + activelist.getCheckInTime();

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
                try {
                    Date date2 = format.parse(checkinTime);
                    long diff = (time.getTime() - date2.getTime());
                    if(diff < 0){
                        int i = date2.getDate() - time.getDate();
                        Date date3 = subtractDays(date2,i);
                        long diff1 = (time.getTime() - date3.getTime());

                        final long diffMins = diff1/60000;

                        if (diffMins <= 15 ) {
                            new CountDownTimer(diff1, 60000) {

                                public void onTick(long millisUntilFinished) {
                                    long mins = 15 - diffMins;
                                    if(activelist.getParentUuid()!=null){
                                        myViewHolder.tv_makepay.setVisibility(View.GONE);
                                    }
                                    else {
                                        myViewHolder.tv_makepay.setText("");
                                        myViewHolder.tv_makepay.setVisibility(View.VISIBLE);
                                        mins--;
                                    }

                                }

                                public void onFinish() {
                                    myViewHolder.tv_makepay.setVisibility(View.GONE);
                                }
                            }.start();
                        }

                    }
                    final long diffMins = diff/60000;

                    if (diffMins <= 15 ) {
                        new CountDownTimer(diff, 60000) {

                            public void onTick(long millisUntilFinished) {
                                long mins = 15 - diffMins;
                                if(activelist.getParentUuid()!=null){
                                    myViewHolder.tv_makepay.setVisibility(View.GONE);
                                }
                                else{
                                myViewHolder.tv_makepay.setText("");
                                myViewHolder.tv_makepay.setVisibility(View.VISIBLE);
                                mins--;}

                            }

                            public void onFinish() {
                                myViewHolder.tv_makepay.setVisibility(View.GONE);
                            }
                        }.start();
                    }
                    else{
                        myViewHolder.tv_makepay.setVisibility(View.GONE);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // myViewHolder.tv_makepay.setText("Click PRE-PAY button in 15 minutes to complete your check-in");
                if(activelist.getAmountDue()>0) {
                    myViewHolder.tv_prepaid.setVisibility(View.VISIBLE);
                    myViewHolder.tv_prepaid.setText("Amount Due: ₹ " + Config.getAmountinTwoDecimalPoints(activelist.getAmountDue()));
                }else{
                    myViewHolder.tv_prepaid.setVisibility(View.GONE);
                }

            }


        } else {

            myViewHolder.btn_pay.setVisibility(View.GONE);
            myViewHolder.tv_makepay.setVisibility(View.GONE);
        }

        if ((activelist.getBillViewStatus()!=null) ) {
            if(activelist.getBillViewStatus().equalsIgnoreCase("Show") && !activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {

                myViewHolder.icon_bill.setVisibility(View.VISIBLE);
            }else{
                myViewHolder.icon_bill.setVisibility(View.GONE);
            }

        } else {
            myViewHolder.icon_bill.setVisibility(View.GONE);

        }

        myViewHolder.btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.logV("Button Pay@@@@@@@@@@@@@@@@@"+activelist.getWaitlistStatus());
                // callback.onMethodActivePayIconCallback(activelist.getYnwUuid());
                String consumer = Config.toTitleCase(activelist.getFirstName() )+ " " + Config.toTitleCase(activelist.getLastName());
                if (activelist.getWaitlistStatus().equalsIgnoreCase("prepaymentPending")) {
                    callback.onMethodActivePayIconCallback(activelist.getPaymentStatus(), activelist.getYnwUuid(), activelist.getBusinessName(), String.valueOf(activelist.getId()),activelist.getAmountDue());
                }else {
                    callback.onMethodActiveBillIconCallback(activelist.getPaymentStatus(), activelist.getYnwUuid(), activelist.getBusinessName(), String.valueOf(activelist.getId()),consumer);
                }
            }
        });



        if(activelist.getPaymentStatus().equalsIgnoreCase("FullyPaid")){
            myViewHolder.icon_bill.setText("Receipt");

        }else{
            myViewHolder.icon_bill.setText("Bill");
        }
        myViewHolder.icon_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String consumer = Config.toTitleCase(activelist.getFirstName() )+ " " + Config.toTitleCase(activelist.getLastName());
                callback.onMethodActiveBillIconCallback(activelist.getPaymentStatus(),activelist.getYnwUuid(), activelist.getBusinessName(), String.valueOf(activelist.getId()),consumer);
            }
        });


        Config.logV("Date------------" + activelist.getDate());


        //   myViewHolder.tv_estTime.setVisibility(View.VISIBLE);
       myViewHolder.tv_queueTime.setText( "Time Window" + " (" + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime() + " )");
        if(activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")){
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
            String inputDateStr = activelist.getDate();
            Date datechange = null;
            try {
                datechange = inputFormat.parse(inputDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String outputDateStr = outputFormat.format(datechange);


            Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/Montserrat_Bold.otf");
            String firstWord = "";
            firstWord = "Checked in for ";

            String dtStart = outputDateStr;
            Date dateParse = null;
            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
            try {
                dateParse = format1.parse(dtStart);
                System.out.println(dateParse);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat format = new SimpleDateFormat("d");
            String date1 = format.format(dateParse);

            if (date1.endsWith("1") && !date1.endsWith("11"))
                format = new SimpleDateFormat("EE, MMM d'st' yyyy");
            else if (date1.endsWith("2") && !date1.endsWith("12"))
                format = new SimpleDateFormat("EE, MMM d'nd' yyyy");
            else if (date1.endsWith("3") && !date1.endsWith("13"))
                format = new SimpleDateFormat("EE, MMM d'rd' yyyy");
            else
                format = new SimpleDateFormat("EE, MMM d'th' yyyy");

            String yourDate = format.format(dateParse);
            firstWord = "Checked in for ";
            String secondWord = yourDate + ","+'\n' + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime();
            Spannable spannable = new SpannableString(firstWord + secondWord );
            spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                    firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            myViewHolder.tv_check_in.setText(spannable);

            myViewHolder.tv_check_in.setVisibility(View.VISIBLE);
            myViewHolder.tv_queueTime.setVisibility(View.GONE);
            myViewHolder.tv_check_in.setVisibility(View.VISIBLE);
        }
        else{
            myViewHolder.tv_check_in.setVisibility(View.GONE);
            myViewHolder.tv_queueTime.setVisibility(View.VISIBLE);
        }

        if(activelist.getParentUuid()!= null){
            myViewHolder.tv_prepaid.setVisibility(View.GONE);
            myViewHolder.btn_pay.setVisibility(View.GONE);
            myViewHolder.tv_makepay.setVisibility(View.GONE);
        }

        if (activelist.getServiceTime() != null) {
            Config.logV("Provider cancelled------@@@@@---%%%%-"+activelist.getBusinessName()+"status "+activelist.getWaitlistStatus());
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            if (date.equalsIgnoreCase(activelist.getDate())) {

                Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                        "fonts/Montserrat_Bold.otf");
                String firstWord = "Checked in for ";
                String secondWord = "Today" + ","+ activelist.getServiceTime();
                Spannable spannable = new SpannableString(firstWord + secondWord );
                spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                        firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

//                if (activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
//                     secondWord = activelist.getStatusUpdatedTime();
//                    myViewHolder.tv_status.setVisibility(View.VISIBLE);
//                    myViewHolder.tv_status.setText(secondWord + " - Cancelled ");
//                    myViewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
//                }

                if (!activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                    myViewHolder.tv_estTime.setVisibility(View.VISIBLE);
                    myViewHolder.tv_estTime.setText(spannable);
//                    myViewHolder.tv_queueTime.setVisibility(View.VISIBLE);
//                    myViewHolder.tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
                } else {
                    myViewHolder.tv_estTime.setVisibility(View.GONE);
//                    myViewHolder.tv_queueTime.setVisibility(View.VISIBLE);
//                    myViewHolder.tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
                }


            } else {


                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                String inputDateStr = activelist.getDate();
                Date datechange = null;
                try {
                    datechange = inputFormat.parse(inputDateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String outputDateStr = outputFormat.format(datechange);


                Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                        "fonts/Montserrat_Bold.otf");
                String firstWord = "Checked in for ";
                // String strDate = outputDateStr + ", " + activelist.getServiceTime();

                String dtStart = outputDateStr;
                Date dateParse = null;
                SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    dateParse = format1.parse(dtStart);
                    System.out.println(dateParse);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                SimpleDateFormat format = new SimpleDateFormat("d");
                String date1 = format.format(dateParse);

                if (date1.endsWith("1") && !date1.endsWith("11"))
                    format = new SimpleDateFormat("EE, MMM d'st' yyyy");
                else if (date1.endsWith("2") && !date1.endsWith("12"))
                    format = new SimpleDateFormat("EE, MMM d'nd' yyyy");
                else if (date1.endsWith("3") && !date1.endsWith("13"))
                    format = new SimpleDateFormat("EE, MMM d'rd' yyyy");
                else
                    format = new SimpleDateFormat("EE, MMM d'th' yyyy");

                String yourDate = format.format(dateParse);

                //to convert Date to String, use format method of SimpleDateFormat class.
                String secondWord = yourDate + ", " + activelist.getServiceTime();
                //  String secondWord = "";



                Spannable spannable = new SpannableString(firstWord + secondWord);
                spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                        firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

//                if (activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
//
//                    if(activelist.getStatusUpdatedTime()!= null) {
//                        secondWord = activelist.getStatusUpdatedTime();
//                    }
//
//
//
//                    myViewHolder.tv_status.setVisibility(View.VISIBLE);
//                    myViewHolder.tv_status.setText("Cancelled  " + secondWord);
//                    myViewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
//                }


                if (!activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                    myViewHolder.tv_estTime.setVisibility(View.VISIBLE);
                    myViewHolder.tv_estTime.setText(spannable);
                    //  myViewHolder.tv_queueTime.setVisibility(View.VISIBLE);
                    //  myViewHolder.tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
                } else {
                    myViewHolder.tv_estTime.setVisibility(View.GONE);
                    //  myViewHolder.tv_queueTime.setVisibility(View.VISIBLE);
                    //  myViewHolder.tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
                }


                // myViewHolder.tv_estTime.setText(Html.fromHtml("Approx Wait Time " +"<font color=\"#6065FF\"><b>"+ activelist.getAppxWaitingTime() +" Mins "+"</font><b>")) ;

            }


        } else {



            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            if (date.equalsIgnoreCase(activelist.getDate())) {


                Config.logV("getAppxWaitingTime------------" + activelist.getAppxWaitingTime());
                if(activelist.getWaitlistStatus().equalsIgnoreCase("done")){
                    myViewHolder.tv_estTime.setVisibility(View.GONE);
                }
                else if (activelist.getAppxWaitingTime() == 0) {
//                    myViewHolder.tv_estTime.setText("Estimated Time Now");
//                    myViewHolder.tv_estTime.setVisibility(View.VISIBLE);
                    Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                            "fonts/Montserrat_Bold.otf");
                    String firstWord = "Est Time ";
                    String secondWord = "Now";
                    Spannable spannable = new SpannableString(firstWord + secondWord);
                    spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                            firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    if (activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                        String inputDateStr = activelist.getDate();
                        Date datechange = null;
                        try {
                            datechange = inputFormat.parse(inputDateStr);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String outputDateStr = outputFormat.format(datechange);


                        tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                "fonts/Montserrat_Bold.otf");
                        firstWord = "Checked in for ";
                        // String strDate = outputDateStr + ", " + activelist.getServiceTime();

                        String dtStart = outputDateStr;
                        Date dateParse = null;
                        SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
                        try {
                            dateParse = format1.parse(dtStart);
                            System.out.println(dateParse);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        SimpleDateFormat format = new SimpleDateFormat("d");
                        String date1 = format.format(dateParse);

                        if (date1.endsWith("1") && !date1.endsWith("11"))
                            format = new SimpleDateFormat("EE, MMM d'st' yyyy");
                        else if (date1.endsWith("2") && !date1.endsWith("12"))
                            format = new SimpleDateFormat("EE, MMM d'nd' yyyy");
                        else if (date1.endsWith("3") && !date1.endsWith("13"))
                            format = new SimpleDateFormat("EE, MMM d'rd' yyyy");
                        else
                            format = new SimpleDateFormat("EE, MMM d'th' yyyy");

                        String yourDate = format.format(dateParse);

                        //to convert Date to String, use format method of SimpleDateFormat class.
                        secondWord = yourDate + ", " + activelist.getQueueStartTime();
                        //  String secondWord = "";



                        spannable = new SpannableString(firstWord + secondWord);
                        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                                firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                      //  myViewHolder.tv_check_in.setVisibility(View.VISIBLE);
                      //  myViewHolder.tv_check_in.setText(spannable);
                        myViewHolder.tv_status.setVisibility(View.VISIBLE);
                        myViewHolder.tv_status.setText("Cancelled at" + " " + activelist.getStatusUpdatedTime());
                        myViewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                    }
                    else{
                     //   myViewHolder.tv_check_in.setVisibility(View.GONE);
                        myViewHolder.tv_status.setVisibility(View.GONE);
                    }


                    if (!activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                        myViewHolder.tv_estTime.setVisibility(View.VISIBLE);
                        myViewHolder.tv_estTime.setText(spannable);
//                        myViewHolder.tv_queueTime.setVisibility(View.VISIBLE);
//                        myViewHolder.tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
                    } else {
                        myViewHolder.tv_estTime.setVisibility(View.GONE);
//                        myViewHolder.tv_queueTime.setVisibility(View.VISIBLE);
//                        myViewHolder.tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
                    }


                } else {
                    if (activelist.getAppxWaitingTime() == -1) {
                        myViewHolder.tv_estTime.setVisibility(View.GONE);
//                        myViewHolder.tv_queueTime.setVisibility(View.VISIBLE);
//                        myViewHolder.tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
                        if (activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                            String inputDateStr = activelist.getDate();
                            Date datechange = null;
                            try {
                                datechange = inputFormat.parse(inputDateStr);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String outputDateStr = outputFormat.format(datechange);


                            Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                    "fonts/Montserrat_Bold.otf");
                            String firstWord = "Checked in for ";
                            // String strDate = outputDateStr + ", " + activelist.getServiceTime();

                            String dtStart = outputDateStr;
                            Date dateParse = null;
                            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
                            try {
                                dateParse = format1.parse(dtStart);
                                System.out.println(dateParse);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            SimpleDateFormat format = new SimpleDateFormat("d");
                            String date1 = format.format(dateParse);

                            if (date1.endsWith("1") && !date1.endsWith("11"))
                                format = new SimpleDateFormat("EE, MMM d'st' yyyy");
                            else if (date1.endsWith("2") && !date1.endsWith("12"))
                                format = new SimpleDateFormat("EE, MMM d'nd' yyyy");
                            else if (date1.endsWith("3") && !date1.endsWith("13"))
                                format = new SimpleDateFormat("EE, MMM d'rd' yyyy");
                            else
                                format = new SimpleDateFormat("EE, MMM d'th' yyyy");

                            String yourDate = format.format(dateParse);

                            //to convert Date to String, use format method of SimpleDateFormat class.
                            String secondWord = yourDate + ", " + activelist.getQueueStartTime();
                            //  String secondWord = "";



                            Spannable spannable = new SpannableString(firstWord + secondWord);
                            spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                                    firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        //    myViewHolder.tv_check_in.setVisibility(View.VISIBLE);
                        //    myViewHolder.tv_check_in.setText(spannable);
                            myViewHolder.tv_status.setVisibility(View.VISIBLE);
                            myViewHolder.tv_status.setText(" Cancelled at" + " "+activelist.getStatusUpdatedTime());
                            myViewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                        }
                        else{
                         //   myViewHolder.tv_check_in.setVisibility(View.GONE);
                            myViewHolder.tv_status.setVisibility(View.GONE);
                        }

                    } else {
                        // myViewHolder.tv_estTime.setVisibility(View.VISIBLE);
                        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                "fonts/Montserrat_Bold.otf");
                        String firstWord = "Est Wait Time ";
                        String secondWord = Config.getTimeinHourMinutes(activelist.getAppxWaitingTime());
                        Spannable spannable = new SpannableString(firstWord + secondWord);
                        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                                firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                        if (activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                            String inputDateStr = activelist.getDate();
                            Date datechange = null;
                            try {
                                datechange = inputFormat.parse(inputDateStr);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String outputDateStr = outputFormat.format(datechange);


                            tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                    "fonts/Montserrat_Bold.otf");
                            firstWord = "Checked in for ";
                            // String strDate = outputDateStr + ", " + activelist.getServiceTime();

                            String dtStart = outputDateStr;
                            Date dateParse = null;
                            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
                            try {
                                dateParse = format1.parse(dtStart);
                                System.out.println(dateParse);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            SimpleDateFormat format = new SimpleDateFormat("d");
                            String date1 = format.format(dateParse);

                            if (date1.endsWith("1") && !date1.endsWith("11"))
                                format = new SimpleDateFormat("EE, MMM d'st' yyyy");
                            else if (date1.endsWith("2") && !date1.endsWith("12"))
                                format = new SimpleDateFormat("EE, MMM d'nd' yyyy");
                            else if (date1.endsWith("3") && !date1.endsWith("13"))
                                format = new SimpleDateFormat("EE, MMM d'rd' yyyy");
                            else
                                format = new SimpleDateFormat("EE, MMM d'th' yyyy");

                            String yourDate = format.format(dateParse);

                            //to convert Date to String, use format method of SimpleDateFormat class.
                            secondWord = yourDate + ", " + activelist.getQueueStartTime();
                            //  String secondWord = "";



                            spannable = new SpannableString(firstWord + secondWord);
                            spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                                    firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                         //   myViewHolder.tv_check_in.setVisibility(View.VISIBLE);
                        //    myViewHolder.tv_check_in.setText(spannable);
                            myViewHolder.tv_status.setVisibility(View.VISIBLE);
                            myViewHolder.tv_status.setText("Cancelled at" + " " + activelist.getStatusUpdatedTime());
                            myViewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                        }
                        else{
                          //  myViewHolder.tv_check_in.setVisibility(View.GONE);
                            myViewHolder.tv_status.setVisibility(View.GONE);
                        }

                        if (!activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                            myViewHolder.tv_estTime.setVisibility(View.VISIBLE);
                            myViewHolder.tv_estTime.setText(spannable);
//                            myViewHolder.tv_queueTime.setVisibility(View.VISIBLE);
//                            myViewHolder.tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
                        } else {
                            myViewHolder.tv_estTime.setVisibility(View.GONE);
//                            myViewHolder.tv_queueTime.setVisibility(View.VISIBLE);
//                            myViewHolder.tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
                        }
                    }


                    // myViewHolder.tv_estTime.setText(Html.fromHtml("Approx Wait Time " +"<font color=\"#6065FF\"><b>"+ activelist.getAppxWaitingTime() +" Mins "+"</font><b>")) ;

                }
            } else {

                Config.logV("response.body().get(i).getQueue().getQueueStartTime()" + activelist.getQueueStartTime());
                //Calulate appxtime+questime
                Config.logV("Quueue Time----------------" + activelist.getQueueStartTime());
                Config.logV("App Time----------------" + activelist.getAppxWaitingTime());
                long appwaittime;
                if (activelist.getAppxWaitingTime() != -1) {
                    appwaittime = TimeUnit.MINUTES.toMillis(activelist.getAppxWaitingTime());
                } else {
                    appwaittime = 0;
                }

                if (activelist.getQueueStartTime() != null) {

                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                    Date Timeconvert = null;
                    long millis = 0;
                    try {
                        // sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                        Timeconvert = sdf.parse(activelist.getQueueStartTime());
                        millis = Timeconvert.getTime();
                        Config.logV("millsss----" + millis);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    long finalcheckin = appwaittime + millis;


                    String timeFORAMT = getDate(finalcheckin, "hh:mm a");

                    Config.logV("Provider cancelled------@@@@@----"+activelist.getBusinessName()+"status "+activelist.getWaitlistStatus());
                    Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                            "fonts/Montserrat_Bold.otf");
                    //String firstWord = "Est Wait Time ";



                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String inputDateStr = activelist.getDate();
                    Date datechange = null;
                    try {
                        datechange = inputFormat.parse(inputDateStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String outputDateStr = outputFormat.format(datechange);


                    String firstWord = "Checked in for ";
                    // String strDate = outputDateStr + ", " + activelist.getServiceTime();

                    String dtStart = outputDateStr;
                    Date dateParse = null;
                    SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
                    try {
                        dateParse = format1.parse(dtStart);
                        System.out.println(dateParse);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    SimpleDateFormat format = new SimpleDateFormat("d");
                    String date1 = format.format(dateParse);

                    if (date1.endsWith("1") && !date1.endsWith("11"))
                        format = new SimpleDateFormat("EE, MMM d'st' yyyy");
                    else if (date1.endsWith("2") && !date1.endsWith("12"))
                        format = new SimpleDateFormat("EE, MMM d'nd' yyyy");
                    else if (date1.endsWith("3") && !date1.endsWith("13"))
                        format = new SimpleDateFormat("EE, MMM d'rd' yyyy");
                    else
                        format = new SimpleDateFormat("EE, MMM d'th' yyyy");

                    String yourDate = format.format(dateParse);


                    String secondWord = yourDate +", "+timeFORAMT;
                    Spannable spannable = new SpannableString(firstWord + secondWord);
                    spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                            firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                    if (activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                        Config.logV("Provider cancelled----------"+activelist.getBusinessName());
                        inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                        inputDateStr = activelist.getDate();datechange = null;
                        try {
                            datechange = inputFormat.parse(inputDateStr);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        outputDateStr = outputFormat.format(datechange);


                        tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                "fonts/Montserrat_Bold.otf");
                        firstWord = "Checked in for ";
                        // String strDate = outputDateStr + ", " + activelist.getServiceTime();

                        dtStart = outputDateStr;
                        dateParse = null;
                        format1 = new SimpleDateFormat("dd-MM-yyyy");
                        try {
                            dateParse = format1.parse(dtStart);
                            System.out.println(dateParse);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        format = new SimpleDateFormat("d");
                        date1 = format.format(dateParse);

                        if (date1.endsWith("1") && !date1.endsWith("11"))
                            format = new SimpleDateFormat("EE, MMM d'st' yyyy");
                        else if (date1.endsWith("2") && !date1.endsWith("12"))
                            format = new SimpleDateFormat("EE, MMM d'nd' yyyy");
                        else if (date1.endsWith("3") && !date1.endsWith("13"))
                            format = new SimpleDateFormat("EE, MMM d'rd' yyyy");
                        else
                            format = new SimpleDateFormat("EE, MMM d'th' yyyy");

                        yourDate = format.format(dateParse);

                        //to convert Date to String, use format method of SimpleDateFormat class.
                        secondWord = yourDate + ", " + activelist.getQueueStartTime();
                        //  String secondWord = "";



                        spannable = new SpannableString(firstWord + secondWord);
                        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                                firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                   //     myViewHolder.tv_check_in.setVisibility(View.VISIBLE);
                   //     myViewHolder.tv_check_in.setText(spannable);
                        myViewHolder.tv_status.setVisibility(View.VISIBLE);
                        myViewHolder.tv_status.setText("Cancelled at" + " " + activelist.getStatusUpdatedTime());
                        myViewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                    }
                    else{
                     //   myViewHolder.tv_check_in.setVisibility(View.GONE);
                        myViewHolder.tv_status.setVisibility(View.GONE);
                    }


                    if (!activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                        myViewHolder.tv_estTime.setVisibility(View.VISIBLE);
                        myViewHolder.tv_estTime.setText(spannable);
//                        myViewHolder.tv_queueTime.setVisibility(View.VISIBLE);
//                        myViewHolder.tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
                    } else {
                        myViewHolder.tv_estTime.setVisibility(View.GONE);
//                        myViewHolder.tv_queueTime.setVisibility(View.VISIBLE);
//                        myViewHolder.tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
                    }
                } else {

                    if (activelist.getAppxWaitingTime() != -1) {
                        String sTime = null;
                        int h = 0;
                        try {
                            String startTime = "00:00";
                            String newtime;
                            int minutes = activelist.getAppxWaitingTime();
                            h = minutes / 60 + Integer.parseInt(startTime.substring(0, 1));
                            int m = minutes % 60 + Integer.parseInt(startTime.substring(3, 4));
                            if (m > 0 && h > 0) {
                                newtime = h + " Hour :" + m + " Minutes";
                            } else if (h > 0 && m == 0) {
                                newtime = h + " Hour";
                            } else {
                                newtime = m + " Minutes";
                            }
                            sTime = newtime;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                "fonts/Montserrat_Bold.otf");
                        String firstWord = "";
                       /* if (h > 0) {
                            firstWord = "Checked in for ";
                        } else {
                            firstWord = "Est Wait Time ";

                        }*/
                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                        String inputDateStr = activelist.getDate();
                        Date datechange = null;
                        try {
                            datechange = inputFormat.parse(inputDateStr);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String outputDateStr = outputFormat.format(datechange);



                        firstWord = "Checked in for ";
                        // String strDate = outputDateStr + ", " + activelist.getServiceTime();

                        String dtStart = outputDateStr;
                        Date dateParse = null;
                        SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
                        try {
                            dateParse = format1.parse(dtStart);
                            System.out.println(dateParse);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        SimpleDateFormat format = new SimpleDateFormat("d");
                        String date1 = format.format(dateParse);

                        if (date1.endsWith("1") && !date1.endsWith("11"))
                            format = new SimpleDateFormat("EE, MMM d'st' yyyy");
                        else if (date1.endsWith("2") && !date1.endsWith("12"))
                            format = new SimpleDateFormat("EE, MMM d'nd' yyyy");
                        else if (date1.endsWith("3") && !date1.endsWith("13"))
                            format = new SimpleDateFormat("EE, MMM d'rd' yyyy");
                        else
                            format = new SimpleDateFormat("EE, MMM d'th' yyyy");

                        String yourDate = format.format(dateParse);

                        String secondWord = yourDate +", "+sTime;
                        Spannable spannable = new SpannableString(firstWord + secondWord);
                        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                                firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        if (activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                            inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                            outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                            inputDateStr = activelist.getDate();
                            datechange = null;
                            try {
                                datechange = inputFormat.parse(inputDateStr);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            outputDateStr = outputFormat.format(datechange);


                            tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                    "fonts/Montserrat_Bold.otf");
                            firstWord = "Checked in for ";
                            // String strDate = outputDateStr + ", " + activelist.getServiceTime();

                            dtStart = outputDateStr;
                            dateParse = null;
                            format1 = new SimpleDateFormat("dd-MM-yyyy");
                            try {
                                dateParse = format1.parse(dtStart);
                                System.out.println(dateParse);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            format = new SimpleDateFormat("d");
                            date1 = format.format(dateParse);

                            if (date1.endsWith("1") && !date1.endsWith("11"))
                                format = new SimpleDateFormat("EE, MMM d'st' yyyy");
                            else if (date1.endsWith("2") && !date1.endsWith("12"))
                                format = new SimpleDateFormat("EE, MMM d'nd' yyyy");
                            else if (date1.endsWith("3") && !date1.endsWith("13"))
                                format = new SimpleDateFormat("EE, MMM d'rd' yyyy");
                            else
                                format = new SimpleDateFormat("EE, MMM d'th' yyyy");

                            yourDate = format.format(dateParse);

                            //to convert Date to String, use format method of SimpleDateFormat class.
                            secondWord = yourDate  + ", " + activelist.getQueueStartTime();
                            //  String secondWord = "";



                            spannable = new SpannableString(firstWord + secondWord);
                            spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                                    firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

//                            myViewHolder.tv_check_in.setVisibility(View.VISIBLE);
//                            myViewHolder.tv_check_in.setText(spannable);
                            myViewHolder.tv_status.setVisibility(View.VISIBLE);
                            myViewHolder.tv_status.setText("Cancelled at" + " " + activelist.getStatusUpdatedTime());
                            myViewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                        }
                        else{
                         //   myViewHolder.tv_check_in.setVisibility(View.GONE);
                            myViewHolder.tv_status.setVisibility(View.GONE);
                        }


                        if (!activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                            myViewHolder.tv_estTime.setVisibility(View.VISIBLE);
                            myViewHolder.tv_estTime.setText(spannable);
//                            myViewHolder.tv_queueTime.setVisibility(View.VISIBLE);
//                            myViewHolder.tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
                        } else {
                            myViewHolder.tv_estTime.setVisibility(View.GONE);
//                            myViewHolder.tv_queueTime.setVisibility(View.VISIBLE);
//                            myViewHolder.tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
                        }
                    } else {
                        myViewHolder.tv_estTime.setVisibility(View.GONE);
//                        myViewHolder.tv_queueTime.setVisibility(View.VISIBLE);
//                        myViewHolder.tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
                    }
                }

            }

        }
         /*else {
            myViewHolder.tv_estTime.setVisibility(View.GONE);
        }*/


    }

    public Date subtractDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -days);

        return cal.getTime();
    }


    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        // formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    public int getItemCount() {
        return activeChekinList.size();
    }
}