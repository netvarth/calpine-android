package com.netvarth.youneverwait.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.callback.ActiveAdapterOnCallback;
import com.netvarth.youneverwait.common.Config;
import com.netvarth.youneverwait.custom.CustomTypefaceSpan;
import com.netvarth.youneverwait.response.ActiveCheckIn;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by sharmila on 13/7/18.
 */

public class ActiveCheckInAdapter extends RecyclerView.Adapter<ActiveCheckInAdapter.MyViewHolder> {

    private List<ActiveCheckIn> activeChekinList;
    Context mContext;
    ActiveAdapterOnCallback callback;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_businessname, tv_estTime,tv_service,tv_place,tv_personahead,tv_token,tv_waitsatus,icon_message,icon_cancel;
        TextView icon_bill;
        LinearLayout lactive;
        ImageView img_fav;
        public MyViewHolder(View view) {
            super(view);
            tv_businessname = (TextView) view.findViewById(R.id.txt_businessname);
            tv_estTime = (TextView) view.findViewById(R.id.txt_esttime);
            icon_bill = (TextView) view.findViewById(R.id.icon_bill);
            tv_service = (TextView) view.findViewById(R.id.txt_service);
            tv_place = (TextView) view.findViewById(R.id.txt_location);
            tv_personahead=(TextView)view.findViewById(R.id.txt_personahead);
            tv_token=(TextView)view.findViewById(R.id.txt_token);
            tv_waitsatus=(TextView)view.findViewById(R.id.txt_waitsatus);
            img_fav=(ImageView)view.findViewById(R.id.img_fav);
            icon_message= (TextView) view.findViewById(R.id.icon_message);
            icon_cancel=(TextView) view.findViewById(R.id.icon_cancel);

        }
    }

    Activity activity;
    Fragment mFragment;

    public ActiveCheckInAdapter(List<ActiveCheckIn> mactiveChekinList, Context mContext, Activity mActivity, Fragment fragment,ActiveAdapterOnCallback callback) {
        this.mContext = mContext;
        this.activeChekinList = mactiveChekinList;
        this.activity = mActivity;
        this.mFragment = fragment;
        this.callback=callback;

    }

    @Override
    public ActiveCheckInAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activechecklist_row, parent, false);

        return new ActiveCheckInAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ActiveCheckInAdapter.MyViewHolder myViewHolder, final int position) {
        final ActiveCheckIn activelist = activeChekinList.get(position);
        myViewHolder.tv_businessname.setText(activelist.getProvider().getBusinessName());

        if(activelist.getWaitlistStatus().equalsIgnoreCase("checkedIn")||activelist.getWaitlistStatus().equalsIgnoreCase("arrived")){
            myViewHolder.icon_cancel.setVisibility(View.VISIBLE);
        }else{
            myViewHolder.icon_cancel.setVisibility(View.GONE);
        }

        if(activelist.getWaitlistStatus()!=null) {
            if (activelist.getWaitlistStatus().equalsIgnoreCase("cancelled") || activelist.getWaitlistStatus().equalsIgnoreCase("done") || activelist.getWaitlistStatus().equalsIgnoreCase("started")) {
                myViewHolder.tv_waitsatus.setVisibility(View.VISIBLE);
                Typeface tyface4 = Typeface.createFromAsset(mContext.getAssets(),
                        "fonts/Montserrat_Bold.otf");
                myViewHolder.tv_waitsatus.setTypeface(tyface4);
                myViewHolder.tv_waitsatus.setText(activelist.getWaitlistStatus());
            } else {
                Config.logV("activelist.getWaitlistStatus()"+activelist.getWaitlistStatus());
                myViewHolder.tv_waitsatus.setVisibility(View.GONE);
            }
        }





        myViewHolder.icon_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onMethodDelecteCheckinCallback(activelist.getYnwUuid(),activelist.getProvider().getId());
            }
        });
        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        myViewHolder.tv_businessname.setTypeface(tyface);

        myViewHolder.tv_businessname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               callback.onMethodActiveCallback(activelist.getProvider().getUniqueId());
            }
        });

        if(activelist.getQueue()!=null){
            if(activelist.getQueue().getLocation().getPlace()!=null){
                myViewHolder.tv_place.setVisibility(View.VISIBLE);
                myViewHolder.tv_place.setText(activelist.getQueue().getLocation().getPlace());
            }else{
                myViewHolder.tv_place.setVisibility(View.GONE);
            }
        }


        if(activelist.getConsumer().isFavourite()){

            myViewHolder.img_fav.setVisibility(View.VISIBLE);
            myViewHolder.img_fav.setImageResource(R.drawable.icon_favourited);
        }else{
            myViewHolder.img_fav.setVisibility(View.GONE);
        }
        myViewHolder.tv_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.logV("googlemap url--------"+activelist.getQueue().getLocation().getGoogleMapUrl());
                String geoUri = activelist.getQueue().getLocation().getGoogleMapUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                mContext.startActivity(intent);
            }
        });
        if(activelist.getService()!=null) {
            if (activelist.getService().getName() != null) {
                myViewHolder.tv_service.setVisibility(View.VISIBLE);

                Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                        "fonts/Montserrat_Bold.otf");
                String firstWord=activelist.getService().getName();
                String secondWord=" for "+activelist.getConsumer().getUserProfile().getFirstName()+" "+activelist.getConsumer().getUserProfile().getLastName();
                Spannable spannable = new SpannableString(firstWord+secondWord);
                spannable.setSpan( new CustomTypefaceSpan("sans-serif",tyface1), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                myViewHolder.tv_service.setText( spannable );
            } else {
                myViewHolder.tv_service.setVisibility(View.GONE);
            }
        }

        Config.logV("Bill------------" + activelist.getWaitlistStatus());
        if (activelist.getBillStatus()!=null) {
            myViewHolder.icon_bill.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.icon_bill.setVisibility(View.GONE);
        }


        myViewHolder.icon_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onMethodActiveBillIconCallback(activelist.getYnwUuid(),activelist.getProvider().getBusinessName());
            }
        });


        Config.logV("Date------------" + activelist.getDate());

        if(!activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
            myViewHolder.tv_estTime.setVisibility(View.VISIBLE);
            if (activelist.getServiceTime() != null) {

                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                if (date.equalsIgnoreCase(activelist.getDate())) {

                    Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                            "fonts/Montserrat_Bold.otf");
                    String firstWord = "Est Wait Time \n";
                    String secondWord ="Today,"+ activelist.getServiceTime();
                    Spannable spannable = new SpannableString(firstWord + secondWord);
                    spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                            firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    myViewHolder.tv_estTime.setText(spannable);


                } else {

                    Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                            "fonts/Montserrat_Bold.otf");
                    String firstWord = "Est Wait Time \n" ;
                    String secondWord =  activelist.getDate() + ", "+activelist.getServiceTime();
                    Spannable spannable = new SpannableString(firstWord + secondWord);
                    spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                            firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    myViewHolder.tv_estTime.setText(spannable);


                    // myViewHolder.tv_estTime.setText(Html.fromHtml("Approx Wait Time " +"<font color=\"#6065FF\"><b>"+ activelist.getAppxWaitingTime() +" Mins "+"</font><b>")) ;

                }


            } else {
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                if (date.equalsIgnoreCase(activelist.getDate())) {
                    Config.logV("getAppxWaitingTime------------" + activelist.getAppxWaitingTime());
                    if (activelist.getAppxWaitingTime() == 0) {
                        // myViewHolder.tv_estTime.setText("Estimated Time Now");

                        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                "fonts/Montserrat_Bold.otf");
                        String firstWord = "Est Time ";
                        String secondWord = "Now";
                        Spannable spannable = new SpannableString(firstWord + secondWord);
                        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                                firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        myViewHolder.tv_estTime.setText(spannable);


                    } else {

                        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                "fonts/Montserrat_Bold.otf");
                        String firstWord = "Est Wait Time \n";
                        String secondWord = activelist.getAppxWaitingTime() + " Mins ";
                        Spannable spannable = new SpannableString(firstWord + secondWord);
                        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                                firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        myViewHolder.tv_estTime.setText(spannable);


                        // myViewHolder.tv_estTime.setText(Html.fromHtml("Approx Wait Time " +"<font color=\"#6065FF\"><b>"+ activelist.getAppxWaitingTime() +" Mins "+"</font><b>")) ;

                    }
                } else {

                    Config.logV("response.body().get(i).getQueue().getQueueStartTime()" + activelist.getQueue().getQueueStartTime());
                    //Calulate appxtime+questime
                    Config.logV("Quueue Time----------------" + activelist.getQueue().getQueueStartTime());
                    Config.logV("App Time----------------" + activelist.getAppxWaitingTime());
                    long appwaittime = TimeUnit.MINUTES.toMillis(activelist.getAppxWaitingTime());


                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                    Date Timeconvert = null;
                    long millis = 0;
                    try {
                        // sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                        Timeconvert = sdf.parse(activelist.getQueue().getQueueStartTime());
                        millis = Timeconvert.getTime();
                        Config.logV("millsss----" + millis);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    long finalcheckin = appwaittime + millis;
                    Config.logV("Check-in Time  " + millis);

                    String timeFORAMT = getDate(finalcheckin, "hh:mm a");

                    Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                            "fonts/Montserrat_Bold.otf");
                    String firstWord = "Check-in Time \n";
                    String secondWord = timeFORAMT;
                    Spannable spannable = new SpannableString(firstWord + secondWord);
                    spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                            firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    myViewHolder.tv_estTime.setText(spannable);

                }

            }
        }else{
            myViewHolder.tv_estTime.setVisibility(View.GONE);
        }
        

        String firstWord="Token ";
        String secondWord=String.valueOf(activelist.getToken());
        Spannable spannable = new SpannableString(firstWord+secondWord);
        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.sec_title_grey)),
                0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Typeface tyface2 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        spannable.setSpan( new CustomTypefaceSpan("sans-serif",tyface2), firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        myViewHolder.tv_token.setText( spannable );



        if(activelist.getPersonsAhead()!=-1) {
            myViewHolder.tv_personahead.setVisibility(View.VISIBLE);
            String firstWord1 = "Persons Ahead ";
            String secondWord1 = String.valueOf(activelist.getPersonsAhead());
            Spannable spannable1 = new SpannableString(firstWord1 + secondWord1);
            spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.sec_title_grey)),
                    0, firstWord1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            Typeface tyface3 = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/Montserrat_Bold.otf");
            spannable1.setSpan(new CustomTypefaceSpan("sans-serif", tyface3), firstWord1.length(), firstWord1.length() + secondWord1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            myViewHolder.tv_personahead.setText(spannable1);
        }else{
            myViewHolder.tv_personahead.setVisibility(View.INVISIBLE);
        }


        myViewHolder.icon_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onMethodMessageCallback(activelist.getYnwUuid(),String.valueOf(activelist.getProvider().getId()),activelist.getProvider().getBusinessName());

            }
        });


        }



    public static String getDate(long milliSeconds, String dateFormat)
    {
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
