package com.jaldeeinc.jaldee.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.CountDownTimer;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaldeeinc.jaldee.Fragment.SearchDetailViewFragment;
import com.jaldeeinc.jaldee.Interface.IActiveBookings;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.RescheduleActivity;
import com.jaldeeinc.jaldee.activities.RescheduleCheckinActivity;
import com.jaldeeinc.jaldee.callback.ActiveAdapterOnCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.CustomTypefaceSpan;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
        TextView icon_bill, tv_prepaid, tv_service,tv_makepay,tv_peopleahead, tv_token;
        LinearLayout layout_btnpay,layout_activeCheckin;
        Button btn_pay;
        ImageView icon_service;
        CardView cvCard, cvReschedule;
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
            layout_activeCheckin = (LinearLayout) view.findViewById(R.id.activecheckin);
            tv_token = (TextView) view.findViewById(R.id.txt_token);
            icon_service =(ImageView) view.findViewById(R.id.serviceicon);
            cvCard = view.findViewById(R.id.card);
            cvReschedule = view.findViewById(R.id.cv_reschedule);
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
            myViewHolder.cvReschedule.setVisibility(View.GONE);
        }
        Config.logV("activelist.getPersonsAhead()"+activelist.getPersonsAhead());
        if(activelist.getPersonsAhead()!=-1){
            myViewHolder.tv_peopleahead.setVisibility(View.VISIBLE);
            String firstWord1 = "People ahead of you ";
            String secondWord1 = String.valueOf(activelist.getPersonsAhead());
            if(secondWord1.equals("0")){
                String nobody_ahead = "You are first in line ";
                Spannable spannable1 = new SpannableString(nobody_ahead);
                spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.sec_title_grey)),
                        0, nobody_ahead.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
                if(activelist.getAppxWaitingTime() == 0){
                    myViewHolder.tv_peopleahead.setText(spannable1 );}
                else{
                    myViewHolder.tv_peopleahead.setText(spannable1);}
            }
            else {
                Spannable spannable1 = new SpannableString(secondWord1 +" "+ firstWord1);
                spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.sec_title_grey)),
                        0, firstWord1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if(activelist.getAppxWaitingTime() == 0){
                    myViewHolder.tv_peopleahead.setText(spannable1 );}
                else{
                    myViewHolder.tv_peopleahead.setText(spannable1); }
            }
        }else{
            myViewHolder.tv_peopleahead.setVisibility(View.GONE);
        }
        if (activelist.getWaitlistStatus().equalsIgnoreCase("arrived")) {
            if(activelist.getService()!=null && activelist.getService().getServiceType().equalsIgnoreCase("virtualService")){
                myViewHolder.tv_status.setVisibility(View.GONE);
            }
            else{
            myViewHolder.tv_status.setText("Arrived");
            myViewHolder.tv_status.setVisibility(View.VISIBLE);
            myViewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.arrived_green));
            }
            myViewHolder.cvReschedule.setVisibility(View.VISIBLE);
        }
        if (activelist.getWaitlistStatus().equalsIgnoreCase("checkedIn")) {
            myViewHolder.tv_status.setVisibility(View.GONE);
            myViewHolder.cvReschedule.setVisibility(View.VISIBLE);
        }
        if (activelist.getWaitlistStatus().equalsIgnoreCase("started")) {
            myViewHolder.tv_status.setText("Started");
            myViewHolder.tv_status.setVisibility(View.VISIBLE);
            myViewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.cyan));
            myViewHolder.cvReschedule.setVisibility(View.GONE);
        }
        if (activelist.getWaitlistStatus().equalsIgnoreCase("prepaymentPending")) {
            myViewHolder.tv_status.setText("Prepayment Pending");
            myViewHolder.tv_status.setVisibility(View.VISIBLE);
            myViewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.gray));
            myViewHolder.cvReschedule.setVisibility(View.GONE);
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

        if (activelist.getToken() != -1 && activelist.getToken() > 0) {
         myViewHolder.tv_token.setVisibility(View.VISIBLE);
            String firstWord = "Token # ";
            Config.logV("Token------------" + activelist.getToken());
            String secondWord = String.valueOf(activelist.getToken());
            Typeface tyface2 = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/Montserrat_Bold.otf");
            Spannable spannable = new SpannableString(firstWord + secondWord);
            spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.sec_title_grey)),
                    0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //  spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface2), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.black)),
                    firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            myViewHolder.tv_token.setText(spannable);


        } else {
            myViewHolder.tv_token.setVisibility(View.GONE);

        }





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
            try{
                if(activelist.getService().getServiceType().equalsIgnoreCase("virtualService")){
                    myViewHolder.icon_service.setVisibility(View.VISIBLE);
                    myViewHolder.icon_service.setY(6);

                    if(activelist.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")){
                        myViewHolder.icon_service.setImageResource(R.drawable.zoomicon_sized);
                    }
                    else if(activelist.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")){
                        myViewHolder.icon_service.setImageResource(R.drawable.googlemeet_sized);
                    }
                    else if(activelist.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")){
                        myViewHolder.icon_service.setImageResource(R.drawable.whatsappicon_sized);

                    }
                    else if(activelist.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")){
                        myViewHolder.icon_service.setImageResource(R.drawable.phoneiconsized_small);
                    }

                }
                else{
                    myViewHolder.icon_service.setVisibility(View.GONE);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }

        } else {
            myViewHolder.tv_service.setVisibility(View.GONE);
        }
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

        // click action for reSchedule
        myViewHolder.cvReschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent rescheduleIntent = new Intent(mContext, RescheduleCheckinActivity.class);
                rescheduleIntent.putExtra("ynwuuid",activeChekinList.get(position).getYnwUuid());
                rescheduleIntent.putExtra("uniqueId",activeChekinList.get(position).getUniqueId());
                if(activeChekinList.get(position).getProviderAccount()!=null) {
                    rescheduleIntent.putExtra("providerId", activeChekinList.get(position).getProviderAccount().getId());
                }
                else{
                    rescheduleIntent.putExtra("providerId", activeChekinList.get(position).getId());
                }
                mContext.startActivity(rescheduleIntent);
            }
        });

        myViewHolder.cvCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callback.onActiveBookingClick(activelist.getShowToken());
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
        if (activelist.getBillViewStatus() != null && !activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
            if (activelist.getBillViewStatus().equalsIgnoreCase("Show")) {
                myViewHolder.icon_bill.setVisibility(View.VISIBLE);
            } else {
                myViewHolder.icon_bill.setVisibility(View.GONE);
            }
        } else {
            if(!activelist.getPaymentStatus().equalsIgnoreCase("NotPaid")){
                myViewHolder.icon_bill.setVisibility(View.VISIBLE);
            }
            else{
                myViewHolder.icon_bill.setVisibility(View.GONE);
            }
        }
        myViewHolder.btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.logV("Button Pay@@@@@@@@@@@@@@@@@"+activelist.getWaitlistStatus());
                String consumer = Config.toTitleCase(activelist.getFirstName() )+ " " + Config.toTitleCase(activelist.getLastName());
                if (activelist.getWaitlistStatus().equalsIgnoreCase("prepaymentPending")) {
                    callback.onMethodActivePayIconCallback(activelist.getPaymentStatus(), activelist.getYnwUuid(), activelist.getBusinessName(), String.valueOf(activelist.getId()),activelist.getAmountDue(),activelist.getId(),activelist.getUniqueId(),activelist.getCheckinEncId());
                }else {
                    callback.onMethodActiveBillIconCallback(activelist.getPaymentStatus(), activelist.getYnwUuid(), activelist.getBusinessName(), String.valueOf(activelist.getId()),consumer,activelist.getId(),activelist.getUniqueId(),activelist.getCheckinEncId());
                }
            }
        });
        if(activelist.getPaymentStatus().equalsIgnoreCase("FullyPaid") || activelist.getPaymentStatus().equalsIgnoreCase("Refund")){
            myViewHolder.icon_bill.setText("Receipt");
        }else{
            myViewHolder.icon_bill.setText("Bill");
        }
        myViewHolder.icon_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String consumer = Config.toTitleCase(activelist.getFirstName() )+ " " + Config.toTitleCase(activelist.getLastName());
                callback.onMethodActiveBillIconCallback(activelist.getPaymentStatus(),activelist.getYnwUuid(), activelist.getBusinessName(), String.valueOf(activelist.getId()),consumer,activelist.getId(),activelist.getUniqueId(),activelist.getCheckinEncId());
            }
        });
        Config.logV("Date------------" + activelist.getDate());
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
            String yourDate = Config.getFormatedDate(outputDateStr);
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
            myViewHolder.cvReschedule.setVisibility(View.GONE);
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
                if (activelist.getWaitlistStatus().equalsIgnoreCase("checkedIn")) {
                    if (activelist.getShowToken().equalsIgnoreCase("true") && activelist.getCalculationMode().equalsIgnoreCase("NoCalc")){
                        myViewHolder.tv_estTime.setVisibility(View.GONE);
                    } else {
                        myViewHolder.tv_estTime.setVisibility(View.VISIBLE);
                        myViewHolder.tv_estTime.setText(spannable);
                    }
                } else {
                    myViewHolder.tv_estTime.setVisibility(View.GONE);
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
                String yourDate = Config.getFormatedDate(outputDateStr);
                //to convert Date to String, use format method of SimpleDateFormat class.
                String secondWord = yourDate + ", " + activelist.getServiceTime();
                Spannable spannable = new SpannableString(firstWord + secondWord);
                spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                        firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (activelist.getWaitlistStatus().equalsIgnoreCase("checkedIn")) {
                    if (activelist.getShowToken().equalsIgnoreCase("true") && activelist.getCalculationMode().equalsIgnoreCase("NoCalc")){
                        myViewHolder.tv_estTime.setVisibility(View.GONE);
                    } else {
                        myViewHolder.tv_estTime.setVisibility(View.VISIBLE);
                        myViewHolder.tv_estTime.setText(spannable);
                    }
                } else {
                    myViewHolder.tv_estTime.setVisibility(View.GONE);
                }
            }
        } else {
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            if (date.equalsIgnoreCase(activelist.getDate())) {
                Config.logV("getAppxWaitingTime------------" + activelist.getAppxWaitingTime());
                if(activelist.getWaitlistStatus().equalsIgnoreCase("done")){
                    myViewHolder.tv_estTime.setVisibility(View.GONE);
                }
                else if (activelist.getAppxWaitingTime() == 0) {
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
                        String yourDate = Config.getFormatedDate(outputDateStr);
                        //to convert Date to String, use format method of SimpleDateFormat class.
                        secondWord = yourDate + ", " + activelist.getQueueStartTime();
                        spannable = new SpannableString(firstWord + secondWord);
                        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                                firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        myViewHolder.tv_status.setVisibility(View.VISIBLE);
                        myViewHolder.tv_status.setText("Cancelled at" + " " + activelist.getStatusUpdatedTime());
                        myViewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                    }
                    else{
                      //  myViewHolder.tv_status.setVisibility(View.GONE);
                    }
                    if (activelist.getWaitlistStatus().equalsIgnoreCase("checkedIn")) {
                        if (activelist.getShowToken().equalsIgnoreCase("true") && activelist.getCalculationMode().equalsIgnoreCase("NoCalc")){
                            myViewHolder.tv_estTime.setVisibility(View.GONE);
                        } else {
                            myViewHolder.tv_estTime.setVisibility(View.VISIBLE);
                            myViewHolder.tv_estTime.setText(spannable);
                        }
                    } else {
                        myViewHolder.tv_estTime.setVisibility(View.GONE);
                    }
                } else {
                    if (activelist.getAppxWaitingTime() == -1) {
                        myViewHolder.tv_estTime.setVisibility(View.GONE);
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
                            String yourDate = Config.getFormatedDate(outputDateStr);
                            //to convert Date to String, use format method of SimpleDateFormat class.
                            String secondWord = yourDate + ", " + activelist.getQueueStartTime();
                            Spannable spannable = new SpannableString(firstWord + secondWord);
                            spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                                    firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            myViewHolder.tv_status.setVisibility(View.VISIBLE);
                            myViewHolder.tv_status.setText(" Cancelled at" + " "+activelist.getStatusUpdatedTime());
                            myViewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                        }
                        else{
                        //    myViewHolder.tv_status.setVisibility(View.GONE);
                        }
                    } else {
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
                            String yourDate = Config.getFormatedDate(outputDateStr);
                            //to convert Date to String, use format method of SimpleDateFormat class.
                            secondWord = yourDate + ", " + activelist.getQueueStartTime();
                            spannable = new SpannableString(firstWord + secondWord);
                            spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                                    firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            myViewHolder.tv_status.setVisibility(View.VISIBLE);
                            myViewHolder.tv_status.setText("Cancelled at" + " " + activelist.getStatusUpdatedTime());
                            myViewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                        }
                        else{
                         //   myViewHolder.tv_status.setVisibility(View.GONE);
                        }
                        if (activelist.getWaitlistStatus().equalsIgnoreCase("checkedIn")) {
                            if (activelist.getShowToken().equalsIgnoreCase("true") && activelist.getCalculationMode().equalsIgnoreCase("NoCalc")){
                                myViewHolder.tv_estTime.setVisibility(View.GONE);
                            } else {
                                myViewHolder.tv_estTime.setVisibility(View.VISIBLE);
                                myViewHolder.tv_estTime.setText(spannable);
                            }
                        } else {
                            myViewHolder.tv_estTime.setVisibility(View.GONE);
                        }
                    }
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
                    String yourDate = Config.getFormatedDate(outputDateStr);
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
                        yourDate = Config.getFormatedDate(outputDateStr);
                        //to convert Date to String, use format method of SimpleDateFormat class.
                        secondWord = yourDate + ", " + activelist.getQueueStartTime();
                        spannable = new SpannableString(firstWord + secondWord);
                        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                                firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        myViewHolder.tv_status.setVisibility(View.VISIBLE);
                        myViewHolder.tv_status.setText("Cancelled at" + " " + activelist.getStatusUpdatedTime());
                        myViewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                    }
                    else{
                    //    myViewHolder.tv_status.setVisibility(View.GONE);
                    }
                    if (activelist.getWaitlistStatus().equalsIgnoreCase("checkedIn")) {
                        myViewHolder.tv_estTime.setVisibility(View.VISIBLE);
                        myViewHolder.tv_estTime.setText(spannable);
                    } else {
                        myViewHolder.tv_estTime.setVisibility(View.GONE);
                    }
                } else {
                    if (activelist.getAppxWaitingTime() != -1) {
                        String sTime = Config.getTimeinHourMinutes(activelist.getAppxWaitingTime());;
                        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                "fonts/Montserrat_Bold.otf");
                        String firstWord = "";
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
                        String yourDate = Config.getFormatedDate(outputDateStr);
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
                            yourDate = Config.getFormatedDate(outputDateStr);
                            //to convert Date to String, use format method of SimpleDateFormat class.
                            secondWord = yourDate  + ", " + activelist.getQueueStartTime();
                            spannable = new SpannableString(firstWord + secondWord);
                            spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                                    firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            myViewHolder.tv_status.setVisibility(View.VISIBLE);
                            myViewHolder.tv_status.setText("Cancelled at" + " " + activelist.getStatusUpdatedTime());
                            myViewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                        }
                        else{
                         //   myViewHolder.tv_status.setVisibility(View.GONE);
                        }
                        if (activelist.getWaitlistStatus().equalsIgnoreCase("checkedIn")) {
                            myViewHolder.tv_estTime.setVisibility(View.VISIBLE);
                            myViewHolder.tv_estTime.setText(spannable);
                        } else {
                            myViewHolder.tv_estTime.setVisibility(View.GONE);
                        }
                    } else {
                        myViewHolder.tv_estTime.setVisibility(View.GONE);
                    }
                }
            }
        }
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