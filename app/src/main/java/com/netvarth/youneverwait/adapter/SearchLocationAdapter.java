package com.netvarth.youneverwait.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netvarth.youneverwait.Fragment.WorkingHourFragment;
import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.activities.SearchServiceActivity;
import com.netvarth.youneverwait.common.Config;
import com.netvarth.youneverwait.custom.CustomTypefaceSpan;
import com.netvarth.youneverwait.model.WorkingModel;
import com.netvarth.youneverwait.response.QueueList;
import com.netvarth.youneverwait.response.SearchLocation;
import com.netvarth.youneverwait.response.SearchService;
import com.netvarth.youneverwait.response.SearchSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by sharmila on 30/7/18.
 */

public class SearchLocationAdapter extends RecyclerView.Adapter<SearchLocationAdapter.MyViewHolder> {

    private List<SearchLocation> mSearchLocationList;
    Context mContext;
    ArrayList<WorkingModel> workingModelArrayList =new ArrayList<>();
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_place, tv_working, tv_open, tv_waittime;
        Button btn_checkin;
        LinearLayout mLSeriveLayout,mLayouthide;
        ImageView img_arrow;

        public MyViewHolder(View view) {
            super(view);
            tv_place = (TextView) view.findViewById(R.id.txtLoc);
            tv_working = (TextView) view.findViewById(R.id.txtworking);
            btn_checkin = (Button) view.findViewById(R.id.btn_checkin);
            tv_open = (TextView) view.findViewById(R.id.txtopen);
            tv_waittime = (TextView) view.findViewById(R.id.txtwaittime);
            mLSeriveLayout = (LinearLayout) view.findViewById(R.id.lServicelayout);
            img_arrow=(ImageView) view.findViewById(R.id.img_arrow);
            mLayouthide=(LinearLayout) view.findViewById(R.id.mLayouthide);


        }
    }

    List<SearchService> mSearchServiceList;
    List<QueueList> mQueueList;
    SearchSetting mSearchSetting;

    public SearchLocationAdapter(SearchSetting searchSetting, List<SearchLocation> mSearchLocation, Context mContext, List<SearchService> SearchServiceList, List<QueueList> SearchQueueList) {
        this.mContext = mContext;
        this.mSearchLocationList = mSearchLocation;
        this.mSearchServiceList = SearchServiceList;
        this.mQueueList = SearchQueueList;
        this.mSearchSetting = searchSetting;


    }

    @Override
    public SearchLocationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.searchlocation_list, parent, false);

        return new SearchLocationAdapter.MyViewHolder(itemView);
    }

    boolean mShowWaitTime = false;
     boolean mFlagCLick=false,mFlagCLick1=false;
    @Override
    public void onBindViewHolder(final SearchLocationAdapter.MyViewHolder myViewHolder, final int position) {
        final SearchLocation searchLoclist = mSearchLocationList.get(position);
        Config.logV("Place-------------" + searchLoclist.getPlace());
        myViewHolder.tv_place.setText(searchLoclist.getPlace());
        Config.logV("---Place 3333----11---" + searchLoclist.getbSchedule().getTimespec().size());
        if (searchLoclist.getbSchedule() != null) {
            if (searchLoclist.getbSchedule().getTimespec().size() > 0) {

                myViewHolder.tv_working.setVisibility(View.VISIBLE);
            } else {
                myViewHolder.tv_working.setVisibility(View.GONE);
            }
        }

        if(position==0){
            myViewHolder.mLayouthide.setVisibility(View.VISIBLE);
            myViewHolder.img_arrow.setImageResource(R.drawable.icon_angle_up);

        }else{
            myViewHolder.mLayouthide.setVisibility(View.GONE);
            myViewHolder.img_arrow.setImageResource(R.drawable.icon_angle_down);

        }



        myViewHolder.img_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(position==0){

                    if (!mFlagCLick1) {
                        myViewHolder.mLayouthide.setVisibility(View.GONE);
                        myViewHolder.img_arrow.setImageResource(R.drawable.icon_angle_down);
                        mFlagCLick1 = true;
                    } else {
                        myViewHolder.mLayouthide.setVisibility(View.VISIBLE);
                        myViewHolder.img_arrow.setImageResource(R.drawable.icon_angle_up);
                        mFlagCLick1 = false;
                    }


                }else {
                    if (!mFlagCLick) {
                        myViewHolder.mLayouthide.setVisibility(View.VISIBLE);
                        myViewHolder.img_arrow.setImageResource(R.drawable.icon_angle_up);
                        mFlagCLick = true;
                    } else {
                        myViewHolder.mLayouthide.setVisibility(View.GONE);
                        myViewHolder.img_arrow.setImageResource(R.drawable.icon_angle_down);
                        mFlagCLick = false;
                    }
                }
            }
        });


            myViewHolder.tv_working.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (searchLoclist.getbSchedule() != null) {
                        if (searchLoclist.getbSchedule().getTimespec().size() > 0) {
                            String workingHrs = null;
                            Config.logV("---Place 3333-------" + searchLoclist.getbSchedule().getTimespec().size());
                            for (int k = 0; k < searchLoclist.getbSchedule().getTimespec().size(); k++) {

                                String sTime, eTime;
                                sTime = searchLoclist.getbSchedule().getTimespec().get(k).getTimeSlots().get(0).getsTime();
                                eTime = searchLoclist.getbSchedule().getTimespec().get(k).getTimeSlots().get(0).geteTime();
                                for (int j = 0; j < searchLoclist.getbSchedule().getTimespec().get(k).getRepeatIntervals().size(); j++) {
                                    String repeat = searchLoclist.getbSchedule().getTimespec().get(k).getRepeatIntervals().get(j).toString();

                                    WorkingModel work = new WorkingModel();
                                    if (repeat.equalsIgnoreCase("2")) {

                                        work.setDay("Monday");
                                        work.setTime_value(sTime + "-" + eTime);

                                    }
                                    if (repeat.equalsIgnoreCase("3")) {

                                        work.setDay("Tuesday");
                                        work.setTime_value(sTime + "-" + eTime);

                                    }
                                    if (repeat.equalsIgnoreCase("4")) {
                                        work.setDay("Wednesday");
                                        work.setTime_value(sTime + "-" + eTime);
                                    }
                                    if (repeat.equalsIgnoreCase("5")) {

                                        work.setDay("Thursday");
                                        work.setTime_value(sTime + "-" + eTime);
                                    }
                                    if (repeat.equalsIgnoreCase("6")) {

                                        work.setDay("Friday");
                                        work.setTime_value(sTime + "-" + eTime);

                                    }
                                    if (repeat.equalsIgnoreCase("7")) {

                                        work.setDay("Saturday");
                                        work.setTime_value(sTime + "-" + eTime);

                                    }
                                    if (repeat.equalsIgnoreCase("1")) {

                                        work.setDay("Sunday");
                                        work.setTime_value(sTime + "-" + eTime);

                                    }

                                    workingModelArrayList.add(work);

                                }


                            }


                            /*WorkingHourFragment pfFragment = new WorkingHourFragment();
                            Config.logV("Fragment context-----------" + mFragment);
                            FragmentTransaction transaction = mFragment.getFragmentManager().beginTransaction();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("workinghrlist", workingModelArrayList);
                            bundle.putString("title", searchdetailList.getTitle());
                            pfFragment.setArguments(bundle);
                            // Store the Fragment in stack
                            transaction.addToBackStack(null);
                            transaction.replace(R.id.mainlayout, pfFragment).commit();*/
                        }
                    }
                }
            });


//Services------------

       /* for (int i = 0; i < mSearchServiceList.size(); i++) {
            Config.logV("1--" + searchLoclist.getId() + "  2--" + mSearchServiceList.get(i).getLocid());
            String services = "";
            if (searchLoclist.getId() == mSearchServiceList.get(i).getLocid()) {
                // services+=mSearchServiceList.get(i).getName();

                for (int j = 0; j < mSearchServiceList.get(i).getmAllService().size(); j++) {
                    Config.logV("Services-----112222 -----" + mSearchServiceList.get(i).getmAllService().get(j).getName());

                    services += mSearchServiceList.get(i).getmAllService().get(j).getName() + " , ";
                    Config.logV("Services-----112222 --33---" + services);
                }
                myViewHolder.tv_services.setText(services);

            }

        }*/


        for (int i = 0; i < mSearchServiceList.size(); i++) {
            Config.logV("1--" + searchLoclist.getId() + "  2--" + mSearchServiceList.get(i).getLocid());
            String services = "";
            if (searchLoclist.getId() == mSearchServiceList.get(i).getLocid()) {



                for (int j = 0; j < mSearchServiceList.get(i).getmAllService().size(); j++) {
                    TextView dynaText = new TextView(mContext);
                    dynaText.setText(mSearchServiceList.get(i).getmAllService().get(j).getName());
                    dynaText.setTextSize(13);
                    dynaText.setPadding(10, 10, 10, 10);
                    dynaText.setTextColor(mContext.getResources().getColor(R.color.title_consu));
                    dynaText.setEllipsize(TextUtils.TruncateAt.END);
                    dynaText.setMaxLines(1);
                    dynaText.setMaxEms(6);


                    final String mServicename = mSearchServiceList.get(i).getmAllService().get(j).getName();
                    final String mServiceprice = mSearchServiceList.get(i).getmAllService().get(j).getTotalAmount();
                    final String mServicedesc = mSearchServiceList.get(i).getmAllService().get(j).getDescription();
                    final String mServiceduration = mSearchServiceList.get(i).getmAllService().get(j).getServiceDuration();
                    final ArrayList<SearchService> mServiceGallery = mSearchServiceList.get(i).getmAllService().get(j).getServicegallery();

                    dynaText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent iService = new Intent(v.getContext(), SearchServiceActivity.class);
                            iService.putExtra("name", mServicename);
                            iService.putExtra("duration", mServiceduration);
                            iService.putExtra("price", mServiceprice);
                            iService.putExtra("desc", mServicedesc);
                            iService.putExtra("servicegallery", mServiceGallery);
                            mContext.startActivity(iService);

                        }
                    });
                    myViewHolder.mLSeriveLayout.addView(dynaText);
                }


            }
        }


//Queue---- for button check-in,waittime checking

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c);
        System.out.println("Current time => " + formattedDate);


        if (!mSearchSetting.getCalculationMode().equalsIgnoreCase("NoCalc")) {
            mShowWaitTime = true;
        } else {
            mShowWaitTime = false;

        }

        for (int i = 0; i < mQueueList.size(); i++) {


            if (mQueueList.get(i).getNextAvailableQueue() != null) {
                Config.logV("1--" + searchLoclist.getId() + "  2--" + mQueueList.get(i).getNextAvailableQueue().getLocation().getId());
                if (searchLoclist.getId() == Integer.parseInt(mQueueList.get(i).getNextAvailableQueue().getLocation().getId())) {

                    //open Now

                    if (mQueueList.get(i).getNextAvailableQueue().isOpenNow()) {

                        myViewHolder.tv_open.setVisibility(View.VISIBLE);
                    } else {

                        myViewHolder.tv_open.setVisibility(View.GONE);
                    }


                    //Check-In Button

                    Date date1 = null, date2 = null;
                    try {
                        date1 = df.parse(formattedDate);
                        if (mQueueList.get(i).getNextAvailableQueue().getAvailableDate() != null)
                            date2 = df.parse(mQueueList.get(i).getNextAvailableQueue().getAvailableDate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    if (mQueueList.get(i).getNextAvailableQueue().getAvailableDate() != null) {

                        if ((formattedDate.trim().equalsIgnoreCase(mQueueList.get(i).getNextAvailableQueue().getAvailableDate()))) {

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


                    //Estimate WaitTime
                    if (mShowWaitTime) {

                        if (mQueueList.get(i).getNextAvailableQueue().getAvailableDate() != null) {
                            myViewHolder.tv_waittime.setVisibility(View.VISIBLE);


                            if ((formattedDate.trim().equalsIgnoreCase(mQueueList.get(i).getNextAvailableQueue().getAvailableDate()))) {
                                if (mQueueList.get(i).getNextAvailableQueue().getServiceTime() != null) {

                                    Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                            "fonts/Montserrat_Bold.otf");
                                    String firstWord="Est Wait Time ";
                                    String secondWord="Today ,"+mQueueList.get(i).getNextAvailableQueue().getServiceTime();
                                    Spannable spannable = new SpannableString(firstWord+secondWord);
                                    spannable.setSpan( new CustomTypefaceSpan("sans-serif",tyface1), firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                                            firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                    myViewHolder.tv_waittime.setText(spannable);
                                } else {

                                    Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                            "fonts/Montserrat_Bold.otf");
                                    String firstWord="Est Wait Time ";
                                    String secondWord= mQueueList.get(i).getNextAvailableQueue().getQueueWaitingTime() + " Minutes";
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
                                    Date date = format.parse(mQueueList.get(i).getNextAvailableQueue().getAvailableDate());
                                    String day = (String) DateFormat.format("dd", date);
                                    String monthString = (String) DateFormat.format("MMM", date);

                                    Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                            "fonts/Montserrat_Bold.otf");
                                    String firstWord="Next Wait Time ";
                                    String secondWord=  monthString + " " + day + ", " + mQueueList.get(i).getNextAvailableQueue().getServiceTime();
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
            }

        }


    }


    @Override
    public int getItemCount() {
        return mSearchLocationList.size();
    }
}
