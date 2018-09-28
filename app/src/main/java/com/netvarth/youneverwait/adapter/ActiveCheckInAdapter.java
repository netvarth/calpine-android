package com.netvarth.youneverwait.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
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
        public TextView tv_businessname, tv_estTime,tv_service,tv_place;
        TextView icon_bill;
        LinearLayout lactive;

        public MyViewHolder(View view) {
            super(view);
            tv_businessname = (TextView) view.findViewById(R.id.txt_businessname);
            tv_estTime = (TextView) view.findViewById(R.id.txt_esttime);
            icon_bill = (TextView) view.findViewById(R.id.icon_bill);
            tv_service = (TextView) view.findViewById(R.id.txt_service);
            tv_place = (TextView) view.findViewById(R.id.txt_location);


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

        if(activelist.getService()!=null) {
            if (activelist.getService().getName() != null) {
                myViewHolder.tv_service.setVisibility(View.VISIBLE);
                myViewHolder.tv_service.setText(Html.fromHtml("<font color=\"#00AEF2\"><b>"+activelist.getService().getName() +"</font><b>" +" for "+activelist.getConsumer().getUserProfile().getFirstName()+" "+activelist.getConsumer().getUserProfile().getLastName()));
            } else {
                myViewHolder.tv_service.setVisibility(View.GONE);
            }
        }

        Config.logV("Bill------------" + activelist.getWaitlistStatus());
        if (activelist.getWaitlistStatus().equalsIgnoreCase("done")) {
            myViewHolder.icon_bill.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.icon_bill.setVisibility(View.GONE);
        }


        Config.logV("Date------------" + activelist.getDate());
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        if (date.equalsIgnoreCase(activelist.getDate())) {
            Config.logV("getAppxWaitingTime------------" + activelist.getAppxWaitingTime());
            if (activelist.getAppxWaitingTime() == 0) {
               // myViewHolder.tv_estTime.setText("Estimated Time Now");

                Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                        "fonts/Montserrat_Bold.otf");
                String firstWord="Est Time ";
                String secondWord="Now";
                Spannable spannable = new SpannableString(firstWord+secondWord);
                spannable.setSpan( new CustomTypefaceSpan("sans-serif",tyface1), firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                        firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                myViewHolder.tv_estTime.setText( spannable );


            } else {

                Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                        "fonts/Montserrat_Bold.otf");
                String firstWord="Est Wait Time ";
                String secondWord=activelist.getAppxWaitingTime() +" Mins ";
                Spannable spannable = new SpannableString(firstWord+secondWord);
                spannable.setSpan( new CustomTypefaceSpan("sans-serif",tyface1), firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                        firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                myViewHolder.tv_estTime.setText( spannable );


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
                Config.logV("millsss----"+millis);
            } catch (ParseException e) {
                e.printStackTrace();
            }



            long finalcheckin= appwaittime + millis;
            Config.logV("Check-in Time  " +millis);

            String timeformat= String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(finalcheckin),
                    TimeUnit.MILLISECONDS.toMinutes(finalcheckin) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(finalcheckin)) // The change is in this line
                   /* TimeUnit.MILLISECONDS.toSeconds(finalcheckin) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalcheckin))*/);

            String timeFORAMT=getDate(finalcheckin,"hh:mm a");

           /* myViewHolder.estTime.setText("Check-in Time  "+timeformat);*/
           // myViewHolder.tv_estTime.setText(Html.fromHtml("Check-in Time  "+ "<font color=\"#6065FF\"><b>"+timeFORAMT +"</b></font>" ));

            Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/Montserrat_Bold.otf");
            String firstWord="Check-in Time ";
            String secondWord=timeFORAMT;
            Spannable spannable = new SpannableString(firstWord+secondWord);
            spannable.setSpan( new CustomTypefaceSpan("sans-serif",tyface1), firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                    firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            myViewHolder.tv_estTime.setText( spannable );

        }
        


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
