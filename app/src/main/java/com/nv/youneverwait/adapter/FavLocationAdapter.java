package com.nv.youneverwait.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nv.youneverwait.R;
import com.nv.youneverwait.activities.CheckIn;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.custom.CustomTypefaceSpan;
import com.nv.youneverwait.response.FavouriteModel;
import com.nv.youneverwait.response.QueueList;
import com.nv.youneverwait.response.SearchSetting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by sharmila on 22/8/18.
 */

public class FavLocationAdapter extends RecyclerView.Adapter<FavLocationAdapter.MyViewHolder> {

    private List<QueueList> mQueueList;
    private List<FavouriteModel> mFavList;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_loc,tv_date,tv_waittime,tv_Open;
        Button btn_checkin;
        View divider;
        public MyViewHolder(View view) {
            super(view);

            tv_loc=(TextView)view.findViewById(R.id.txt_loc);
            tv_date=(TextView)view.findViewById(R.id.txt_date);
            tv_waittime=(TextView)view.findViewById(R.id.txt_time);
            tv_Open=(TextView)view.findViewById(R.id.txtOpen);
            btn_checkin=(Button) view.findViewById(R.id.btn_checkin);
            divider=(View)view.findViewById(R.id.divider);
        }
    }

    Activity activity;
    boolean mShowWaitTime = false;
    SearchSetting searchSetting;
    String uniqueId,title;
    public FavLocationAdapter(List<QueueList> mQueueList, Context mContext, List<FavouriteModel> mFavList, SearchSetting mSearchSetting,String uniqueID,String title) {
        this.mContext = mContext;
        this.mQueueList = mQueueList;
        this.mFavList=mFavList;
        this.searchSetting=mSearchSetting;
        this.uniqueId=uniqueID;
        this.title=title;

    }

    @Override
    public FavLocationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favloclist_row, parent, false);


        return new FavLocationAdapter.MyViewHolder(itemView);
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
    public void onBindViewHolder(final FavLocationAdapter.MyViewHolder myViewHolder, final int position) {
        final QueueList queueList = mQueueList.get(position);

        if(position==mQueueList.size()-1){
            myViewHolder.divider.setVisibility(View.GONE);
        }else{
            myViewHolder.divider.setVisibility(View.VISIBLE);
        }

        myViewHolder.btn_checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iCheckIn = new Intent(v.getContext(), CheckIn.class);
                iCheckIn.putExtra("serviceId", Integer.parseInt(queueList.getNextAvailableQueue().getLocation().getId()));
                iCheckIn.putExtra("uniqueID", uniqueId);
                iCheckIn.putExtra("accountID",queueList.getProvider().getId());
                iCheckIn.putExtra("from", "favourites");
                iCheckIn.putExtra("title", title);
                iCheckIn.putExtra("place", myViewHolder.tv_loc.getText().toString());
                //iCheckIn.putExtra("googlemap", searchLoclist.getGoogleMapUrl());

                mContext.startActivity(iCheckIn);
            }
        });
        myViewHolder.tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iCheckIn = new Intent(v.getContext(), CheckIn.class);
                iCheckIn.putExtra("serviceId", Integer.parseInt(queueList.getNextAvailableQueue().getLocation().getId()));
                iCheckIn.putExtra("uniqueID", uniqueId);
                iCheckIn.putExtra("accountID",queueList.getProvider().getId());
                iCheckIn.putExtra("from", "favourites_date");
                iCheckIn.putExtra("title", title);
                iCheckIn.putExtra("place", myViewHolder.tv_loc.getText().toString());
                //iCheckIn.putExtra("googlemap", searchLoclist.getGoogleMapUrl());

                mContext.startActivity(iCheckIn);
            }
        });

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c);
        System.out.println("Current time => " + formattedDate);


        Date date1 = null, date2 = null;
        try {
            date1 = df.parse(formattedDate);
            if (queueList.getNextAvailableQueue().getAvailableDate() != null)
                date2 = df.parse(queueList.getNextAvailableQueue().getAvailableDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for(int i=0;i<mFavList.size();i++){
            if(mFavList.get(i).getId()==Integer.parseInt(queueList.getProvider().getId())){


                if(mFavList.get(i).getLocations()!=null) {
                    for(int j=0;j<mFavList.get(i).getLocations().size();j++) {
                        if (mFavList.get(i).getLocations().get(j).getId() == Integer.parseInt(queueList.getNextAvailableQueue().getLocation().getId())) {
                            Config.logV("Location--##################------------" + mFavList.get(i).getLocations().get(j).getPlace());
                            myViewHolder.tv_loc.setText(mFavList.get(i).getLocations().get(j).getPlace());
                        }
                    }
                }
                if(mFavList.get(i).isOnlineCheckin()){
                    myViewHolder.btn_checkin.setVisibility(View.VISIBLE);
                    if (queueList.getNextAvailableQueue().getAvailableDate() != null) {

                        if ((formattedDate.trim().equalsIgnoreCase(queueList.getNextAvailableQueue().getAvailableDate()))) {

                            myViewHolder.btn_checkin.setVisibility(View.VISIBLE);
                            myViewHolder.btn_checkin.setBackground(mContext.getResources().getDrawable(R.drawable.button_gradient_checkin));

                        } else if (date1.compareTo(date2) < 0) {
                            myViewHolder.btn_checkin.setVisibility(View.VISIBLE);
                            // myViewHolder.btn_checkin.setBackgroundColor(Color.parseColor("#cfcfcf"));
                            myViewHolder.btn_checkin.setBackground(mContext.getResources().getDrawable(R.drawable.btn_checkin_grey));
                            myViewHolder.btn_checkin.setTextColor(mContext.getResources().getColor(R.color.button_grey));
                            myViewHolder.btn_checkin.setEnabled(false);

                        }
                    } else {

                        myViewHolder.btn_checkin.setVisibility(View.GONE);

                    }
                }else{
                    myViewHolder.btn_checkin.setVisibility(View.GONE);
                }

                if(mFavList.get(i).isFutureCheckin()){
                    myViewHolder.tv_date.setVisibility(View.VISIBLE);
                }else{
                    myViewHolder.tv_date.setVisibility(View.GONE);
                }



            }
        }


        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        myViewHolder.tv_Open.setTypeface(tyface);
        if(queueList.getNextAvailableQueue().isOpenNow()){
            myViewHolder.tv_Open.setVisibility(View.VISIBLE);
            Config.logV("Open Now----------------");
        }else{
            Config.logV("Open Now-------3333---------"+queueList.isOpenNow());
            myViewHolder.tv_Open.setVisibility(View.GONE);
        }

        if(searchSetting!=null) {
            if (!searchSetting.getCalculationMode().equalsIgnoreCase("NoCalc")) {
                mShowWaitTime = true;
            } else {
                mShowWaitTime = false;

            }
        }

        //Estimate WaitTime
        if (mShowWaitTime) {

            if (queueList.getNextAvailableQueue().getAvailableDate() != null) {
                myViewHolder.tv_waittime.setVisibility(View.VISIBLE);


                if ((formattedDate.trim().equalsIgnoreCase(queueList.getNextAvailableQueue().getAvailableDate()))) {
                    if (queueList.getNextAvailableQueue().getServiceTime() != null) {

                        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                "fonts/Montserrat_Bold.otf");

                        String firstWord="Est Service Time ";
                        /*String firstWord = null;
                        Date dt = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                        String currentTime = sdf.format(dt);
                        Date datenow=parseDate(currentTime);

                        dateCompareOne = parseDate(queueList.getNextAvailableQueue().getServiceTime());
                        if ( datenow.after( dateCompareOne ) ) {
                            firstWord = "Est Service Time ";
                        }else {
                            firstWord = "Est Wait Time ";

                        }*/


                        String secondWord="Today, "+queueList.getNextAvailableQueue().getServiceTime();
                        Spannable spannable = new SpannableString(firstWord+secondWord);
                        spannable.setSpan( new CustomTypefaceSpan("sans-serif",tyface1), firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                                firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        myViewHolder.tv_waittime.setText(spannable);
                    } else {

                        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                "fonts/Montserrat_Bold.otf");
                        String firstWord="Est Wait Time ";
                        String secondWord= queueList.getNextAvailableQueue().getQueueWaitingTime() + " Minutes";
                        Spannable spannable = new SpannableString(firstWord+secondWord);
                        spannable.setSpan( new CustomTypefaceSpan("sans-serif",tyface1), firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                                firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        myViewHolder.tv_waittime.setText(spannable);
                    }
                }
                if (date1.compareTo(date2) < 0) {
                    try {
                        // String mMonthName=getMonth(searchdetailList.getAvail_date());
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = format.parse(queueList.getNextAvailableQueue().getAvailableDate());
                        String day = (String) DateFormat.format("dd", date);
                        String monthString = (String) DateFormat.format("MMM", date);

                        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                "fonts/Montserrat_Bold.otf");
                        //String firstWord="Next Wait Time ";
                        String firstWord="Est Service Time ";
                        String secondWord=  monthString + " " + day + ", " +queueList.getNextAvailableQueue().getServiceTime();
                        Spannable spannable = new SpannableString(firstWord+secondWord);
                        spannable.setSpan( new CustomTypefaceSpan("sans-serif",tyface1), firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                                firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        myViewHolder.tv_waittime.setText(spannable);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                }
            } else {
                myViewHolder.tv_waittime.setVisibility(View.GONE);
            }
        } else {
            myViewHolder.tv_waittime.setVisibility(View.GONE);
        }


    }


    @Override
    public int getItemCount() {
        return mQueueList.size();
    }
}