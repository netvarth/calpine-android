package com.netvarth.youneverwait.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.activities.SearchServiceActivity;
import com.netvarth.youneverwait.common.Config;
import com.netvarth.youneverwait.response.QueueList;
import com.netvarth.youneverwait.response.SearchLocation;
import com.netvarth.youneverwait.response.SearchService;
import com.netvarth.youneverwait.response.SearchSetting;

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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_place, tv_working, tv_services, tv_open, tv_waittime;
        Button btn_checkin;
        LinearLayout mLSeriveLayout;

        public MyViewHolder(View view) {
            super(view);
            tv_place = (TextView) view.findViewById(R.id.txtLoc);
            tv_working = (TextView) view.findViewById(R.id.txtworking);
            tv_services = (TextView) view.findViewById(R.id.txtservices);
            btn_checkin = (Button) view.findViewById(R.id.btn_checkin);
            tv_open = (TextView) view.findViewById(R.id.txtopen);
            tv_waittime = (TextView) view.findViewById(R.id.txtwaittime);
            mLSeriveLayout = (LinearLayout) view.findViewById(R.id.lServicelayout);


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

    @Override
    public void onBindViewHolder(SearchLocationAdapter.MyViewHolder myViewHolder, final int position) {
        final SearchLocation searchLoclist = mSearchLocationList.get(position);
        Config.logV("Place-------------" + searchLoclist.getPlace());
        myViewHolder.tv_place.setText(searchLoclist.getPlace());
        Config.logV("---Place 3333----11---" + searchLoclist.getbSchedule().getTimespec().size());
        if (searchLoclist.getbSchedule() != null) {
            if (searchLoclist.getbSchedule().getTimespec().size() > 0) {
                String workingHrs = null;
                Config.logV("---Place 3333-------" + searchLoclist.getbSchedule().getTimespec().size());
                for (int k = 0; k < searchLoclist.getbSchedule().getTimespec().size(); k++) {

                    String data = "", date = "", day = null;
                    String sTime, eTime;
                    sTime = searchLoclist.getbSchedule().getTimespec().get(k).getTimeSlots().get(0).getsTime();
                    eTime = searchLoclist.getbSchedule().getTimespec().get(k).getTimeSlots().get(0).geteTime();
                    for (int j = 0; j < searchLoclist.getbSchedule().getTimespec().get(k).getRepeatIntervals().size(); j++) {
                        String repeat = searchLoclist.getbSchedule().getTimespec().get(k).getRepeatIntervals().get(j).toString();
                        if (repeat.equalsIgnoreCase("1")) {
                            day = "Su";

                        }
                        if (repeat.equalsIgnoreCase("2")) {
                            day = "M";

                        }
                        if (repeat.equalsIgnoreCase("3")) {
                            day = "T";

                        }
                        if (repeat.equalsIgnoreCase("4")) {
                            day = "W";

                        }
                        if (repeat.equalsIgnoreCase("5")) {
                            day = "T";

                        }
                        if (repeat.equalsIgnoreCase("6")) {
                            day = "F";

                        }
                        if (repeat.equalsIgnoreCase("7")) {
                            day = "Sa";

                        }


                        date += day + ", ";
                    }

                    data += date + " " + sTime + "-" + eTime + "\n ";


                    if (workingHrs != null) {
                        workingHrs += data + "\n";
                    } else {
                        workingHrs = data + "\n";
                    }

                    Config.logV("WOrking HRS------------" + workingHrs);


                }
                myViewHolder.tv_working.setText(workingHrs);

            }
        }

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
                    dynaText.setTextSize(15);
                    dynaText.setPadding(10, 10, 10, 10);
                    dynaText.setPaintFlags(dynaText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    dynaText.setTag("" + i);
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
                            myViewHolder.btn_checkin.setBackgroundColor(Color.parseColor("#3498db"));

                        } else if (date1.compareTo(date2) < 0) {
                            myViewHolder.btn_checkin.setVisibility(View.VISIBLE);
                            myViewHolder.btn_checkin.setBackgroundColor(Color.parseColor("#cfcfcf"));
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
                                    myViewHolder.tv_waittime.setText("Estimated Waiting Time \n" + "Today ," + mQueueList.get(i).getNextAvailableQueue().getServiceTime());
                                } else {
                                    myViewHolder.tv_waittime.setText("Estimated Waiting Time \n" + mQueueList.get(i).getNextAvailableQueue().getQueueWaitingTime() + " Minutes");
                                }
                            }
                            if (date1.compareTo(date2) < 0) {
                                try {
                                    // String mMonthName=getMonth(searchdetailList.getAvail_date());
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                    Date date = format.parse(mQueueList.get(i).getNextAvailableQueue().getAvailableDate());
                                    String day = (String) DateFormat.format("dd", date);
                                    String monthString = (String) DateFormat.format("MMM", date);
                                    myViewHolder.tv_waittime.setText("Next Waiting Time \n" + monthString + " " + day + ", " + mQueueList.get(i).getNextAvailableQueue().getServiceTime());
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
