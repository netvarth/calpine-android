package com.jaldeeinc.jaldee.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Appointment;
import com.jaldeeinc.jaldee.activities.CheckIn;
import com.jaldeeinc.jaldee.activities.Donation;
import com.jaldeeinc.jaldee.callback.ContactAdapterCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.CustomTypefaceSpan;
import com.jaldeeinc.jaldee.response.FavouriteModel;
import com.jaldeeinc.jaldee.response.QueueList;
import com.jaldeeinc.jaldee.response.ScheduleList;
import com.jaldeeinc.jaldee.response.SearchSetting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.jaldeeinc.jaldee.adapter.SearchLocationAdapter.getWaitingTime;

/**
 * Created by sharmila on 22/8/18.
 */

public class FavLocationAdapter extends RecyclerView.Adapter<FavLocationAdapter.MyViewHolder> {

    private List<QueueList> mQueueList;
    private List<ScheduleList> mScheduleList;
    private List<FavouriteModel> mFavList;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_loc,tv_date,tv_waittime,tv_Open,tv_qmessage;
        Button btn_checkin,btnappointments,btndonations;
        View divider;
        RecyclerView recycleview_contact;
        LinearLayout appoinmentLayouts, donationLayouts;
        public MyViewHolder(View view) {
            super(view);

            tv_loc=(TextView)view.findViewById(R.id.txt_loc);
            tv_date=(TextView)view.findViewById(R.id.txt_date);
            tv_waittime=(TextView)view.findViewById(R.id.txt_time);
            tv_Open=(TextView)view.findViewById(R.id.txtOpen);
            btn_checkin=(Button) view.findViewById(R.id.btn_checkin);
            btnappointments=(Button) view.findViewById(R.id.btnappointments);
            btndonations = (Button) view.findViewById(R.id.btndonations);
            divider=(View)view.findViewById(R.id.divider);
            recycleview_contact=(RecyclerView)view.findViewById(R.id.recycleview_contact);
            tv_qmessage = view.findViewById(R.id.qmessage);
            appoinmentLayouts = view.findViewById(R.id.appoinmentLayouts);
            donationLayouts = view.findViewById(R.id.donationLayouts);
        }
    }

    Activity activity;
    boolean mShowWaitTime = false;
    SearchSetting searchSetting;
    String uniqueId,title,terminologys;
    ContactAdapterCallback contactAdapterCallback;
    public FavLocationAdapter(List<QueueList> mQueueList, Context mContext, List<FavouriteModel> mFavList, SearchSetting mSearchSetting,String uniqueID,String title,String terminology, ContactAdapterCallback contactAdapterCallback,List<ScheduleList> mScheduleList ) {
        this.mContext = mContext;
        this.mQueueList = mQueueList;
        this.mFavList=mFavList;
        this.searchSetting=mSearchSetting;
        this.uniqueId=uniqueID;
        this.title=title;
        this.terminologys=terminology;
        this.contactAdapterCallback=contactAdapterCallback;
        this.mScheduleList = mScheduleList;

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
        final ScheduleList scheduleList = mScheduleList.get(position);




        if(position==mQueueList.size()-1){
            myViewHolder.divider.setVisibility(View.GONE);
        }else{
            myViewHolder.divider.setVisibility(View.VISIBLE);
        }

//        if (queueList.getMessage() != null) {
//            myViewHolder.tv_qmessage.setVisibility(View.VISIBLE);
//            myViewHolder.tv_qmessage.setText(queueList.getMessage());
//        } else {
//            myViewHolder.tv_qmessage.setVisibility(View.GONE);
//        }

        myViewHolder.btn_checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iCheckIn = new Intent(v.getContext(), CheckIn.class);
                if(queueList.getNextAvailableQueue()!=null){
                    iCheckIn.putExtra("serviceId", Integer.parseInt(String.valueOf(queueList.getNextAvailableQueue().getLocation().getId())));
                }
                iCheckIn.putExtra("uniqueID", uniqueId);
                iCheckIn.putExtra("accountID",queueList.getProvider().getId());
                iCheckIn.putExtra("from", "favourites");
                iCheckIn.putExtra("title", title);
                iCheckIn.putExtra("terminology",terminologys);
                iCheckIn.putExtra("place", myViewHolder.tv_loc.getText().toString());
                if(mQueueList.get(position).getNextAvailableQueue()!=null){
                    iCheckIn.putExtra("getAvail_date",mQueueList.get(position).getNextAvailableQueue().getAvailableDate());
                }
                if(queueList.getNextAvailableQueue()!=null){
                    iCheckIn.putExtra("getAvail_date",queueList.getNextAvailableQueue().getAvailableDate());}
                if(queueList.getNextAvailableQueue()!=null){
                    iCheckIn.putExtra("isshowtoken", queueList.getNextAvailableQueue().isShowToken());
                }


                mContext.startActivity(iCheckIn);
            }
        });
        myViewHolder.tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iCheckIn = new Intent(v.getContext(), CheckIn.class);
               if(queueList.getNextAvailableQueue()!=null){
                   iCheckIn.putExtra("serviceId", Integer.parseInt(String.valueOf(queueList.getNextAvailableQueue().getLocation().getId())));
               }
                iCheckIn.putExtra("uniqueID", uniqueId);
                iCheckIn.putExtra("accountID",queueList.getProvider().getId());
                iCheckIn.putExtra("from", "favourites_date");
                iCheckIn.putExtra("title", title);
                iCheckIn.putExtra("terminology",terminologys);
                iCheckIn.putExtra("place", myViewHolder.tv_loc.getText().toString());
                if(mQueueList.get(position).getNextAvailableQueue()!=null){
                    iCheckIn.putExtra("getAvail_date",mQueueList.get(position).getNextAvailableQueue().getAvailableDate());}
                if(queueList.getNextAvailableQueue()!=null){
                    iCheckIn.putExtra("isshowtoken", queueList.getNextAvailableQueue().isShowToken());
                }


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

            if (queueList.getNextAvailableQueue()!=null && queueList.getNextAvailableQueue().getAvailableDate() != null){
                date2 = df.parse(queueList.getNextAvailableQueue().getAvailableDate());
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        for(int i=0;i<mFavList.size();i++){
            if(mFavList.get(i).getId()==Integer.parseInt(queueList.getProvider().getId())) {


                if (mFavList.get(i).getLocations() != null) {
                    for (int j = 0; j < mFavList.get(i).getLocations().size(); j++) {
                        if (queueList.getNextAvailableQueue() != null) {
                            if (mFavList.get(i).getLocations().get(j).getId() == (queueList.getNextAvailableQueue().getLocation().getId())) {
                                Config.logV("Location--##################------------" + mFavList.get(i).getLocations().get(j).getPlace());
                                myViewHolder.tv_loc.setText(mFavList.get(i).getLocations().get(j).getPlace());
                            }
                        } else {
                            Config.logV("ELSE Location--##################------------" + mFavList.get(i).getLocations().get(j).getPlace());
                            myViewHolder.tv_loc.setText(mFavList.get(i).getLocations().get(j).getPlace());
                        }
                    }
                }

                if(queueList.getNextAvailableQueue()!=null && mFavList.get(i).getOnlinePresence()!=null) {
                    if (mFavList.get(i).getOnlinePresence().equals("true") && queueList.getNextAvailableQueue().isWaitlistEnabled()) {
                        disableCheckinFeature(myViewHolder);
                        if (queueList.getNextAvailableQueue().isShowToken()) {
                            myViewHolder.btn_checkin.setText("GET TOKEN");

                        } else {
                            myViewHolder.btn_checkin.setText("Check-in".toUpperCase());

                        }
                        if (queueList.getNextAvailableQueue().getAvailableDate() != null) {
                            if (queueList.getNextAvailableQueue().isOnlineCheckIn() && queueList.getNextAvailableQueue().isAvailableToday() && formattedDate.equalsIgnoreCase(queueList.getNextAvailableQueue().getAvailableDate())) { //Today
                                enableCheckinButton(myViewHolder);
                                if (queueList.getNextAvailableQueue().isShowToken()) {
                                    myViewHolder.btn_checkin.setText("GET TOKEN");


                                    if (queueList.getNextAvailableQueue().getCalculationMode().equalsIgnoreCase("NoCalc")) { // NoCalc without show waiting time
                                        String message = Config.getPersonsAheadText(queueList.getNextAvailableQueue().getPersonAhead());
                                        myViewHolder.tv_waittime.setText(message);
                                        myViewHolder.tv_waittime.setVisibility(View.VISIBLE);
                                        //  myViewHolder.txt_peopleahead.setVisibility(View.GONE);
                                    } else { // Conventional (Token with Waiting time)
                                        myViewHolder.tv_waittime.setVisibility(View.VISIBLE);
                                        String spannable = getWaitingTime(queueList.getNextAvailableQueue());
                                        myViewHolder.tv_waittime.setText(spannable);
                                        // myViewHolder.txt_peopleahead.setVisibility(View.VISIBLE);
                                        String message = Config.getPersonsAheadText(queueList.getNextAvailableQueue().getPersonAhead());
                                        // myViewHolder.txt_peopleahead.setText(message);
                                    }
                                } else { // Conventional/Fixed
                                    myViewHolder.btn_checkin.setText("Check-in".toUpperCase());
                                    myViewHolder.tv_waittime.setVisibility(View.VISIBLE);
                                    String spannable = getWaitingTime(queueList.getNextAvailableQueue());
                                    myViewHolder.tv_waittime.setText(spannable);
                                    //  myViewHolder.txt_peopleahead.setVisibility(View.VISIBLE);
                                    String message = Config.getPersonsAheadText(queueList.getNextAvailableQueue().getPersonAhead());
                                    //  myViewHolder.txt_peopleahead.setText(message);
                                }
                            } else {
                                //  disableCheckinButton(myViewHolder);
                                enableCheckinButton(myViewHolder);
                            }
                            if (date2 != null && date1.compareTo(date2) < 0) {
                                myViewHolder.tv_waittime.setVisibility(View.VISIBLE);
                                String spannable = getWaitingTime(queueList.getNextAvailableQueue());
                                myViewHolder.tv_waittime.setText(spannable);
                                //    myViewHolder.txt_peopleahead.setVisibility(View.VISIBLE);
                                String message = Config.getPersonsAheadText(queueList.getNextAvailableQueue().getPersonAhead());
                                //   myViewHolder.txt_peopleahead.setText(message);
                            }
                            //Future Checkin
                            if (searchSetting.isFutureDateWaitlist() && queueList.getNextAvailableQueue().getAvailableDate() != null) {
//                                    myViewHolder.txt_diffdate.setVisibility(View.VISIBLE);
//                                    myViewHolder.txt_diffdate_expand.setVisibility(View.VISIBLE);
                                if (queueList.getNextAvailableQueue().isShowToken()) {
                                    myViewHolder.tv_date.setText("Do you want to Get Token for another day?");
                                } else {
                                    myViewHolder.tv_date.setText("Do you want to" + " Check-in for another day?");

                                }
                            } else {
                                myViewHolder.tv_date.setVisibility(View.GONE);

                            }
                        }
                    }
                }





//                if (queueList.getNextAvailableQueue() != null){
//                    if (queueList.getNextAvailableQueue().isWaitlistEnabled()) {
//                        if (mFavList.get(i).getOnlinePresence() != null) {
//                            if (mFavList.get(i).getOnlinePresence().equals("true")) {
//                                if (queueList.getNextAvailableQueue() != null && queueList.getNextAvailableQueue().isAvailableToday()) {
//                                    if (queueList.getNextAvailableQueue().getAvailableDate() != null) {
//                                        if ((formattedDate.trim().equalsIgnoreCase(queueList.getNextAvailableQueue().getAvailableDate()))) {
//                                            if (mFavList.get(i).isOnlineCheckin()) {
//                                                myViewHolder.btn_checkin.setVisibility(View.VISIBLE);
//                                                myViewHolder.btn_checkin.setBackground(mContext.getResources().getDrawable(R.drawable.button_gradient_checkin));
//                                            } else {
//                                                myViewHolder.btn_checkin.setVisibility(View.GONE);
//                                            }
//                                        } else if (date1.compareTo(date2) < 0) {
//                                            myViewHolder.btn_checkin.setVisibility(View.VISIBLE);
//                                            // myViewHolder.btn_checkin.setBackgroundColor(Color.parseColor("#cfcfcf"));
////                                            myViewHolder.btn_checkin.setBackground(mContext.getResources().getDrawable(R.drawable.btn_checkin_grey));
////                                            myViewHolder.btn_checkin.setTextColor(mContext.getResources().getColor(R.color.button_grey));
////                                            myViewHolder.btn_checkin.setEnabled(false);
//                                        }
//                                    } else {
//                                        myViewHolder.btn_checkin.setVisibility(View.GONE);
//                                    }
//                                } else {
//                                    myViewHolder.btn_checkin.setVisibility(View.VISIBLE);
//                                }
//
//                            } else {
//                                myViewHolder.btn_checkin.setVisibility(View.GONE);
//                            }
//                        } else {
//                            myViewHolder.btn_checkin.setVisibility(View.GONE);
//                        }
//                    } else {
//                        myViewHolder.btn_checkin.setVisibility(View.GONE);
//                    }
//            }
//




                if(mScheduleList.size()>0){

                    if(mFavList.get(i).getOnlinePresence().equals("true")){
                        if(scheduleList.isCheckinAllowed()){
                            myViewHolder.appoinmentLayouts.setVisibility(View.VISIBLE);
                        }else{
                            myViewHolder.appoinmentLayouts.setVisibility(View.GONE);
                        }
                    }else{
                        myViewHolder.appoinmentLayouts.setVisibility(View.GONE);
                    }
                }else{
                    myViewHolder.appoinmentLayouts.setVisibility(View.GONE);
                }


                final int finalI1 = i;
                myViewHolder.btnappointments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent iAppoinment = new Intent(v.getContext(), Appointment.class);
                        if(scheduleList.getAvailableSchedule()!=null){
                            iAppoinment.putExtra("serviceId", Integer.parseInt(String.valueOf(scheduleList.getAvailableSchedule().getLocation().getId())));
                        }
                        iAppoinment.putExtra("uniqueID", uniqueId);
                        iAppoinment.putExtra("accountID",mScheduleList.get(position).getProvider().getId());
                        iAppoinment.putExtra("from", "favourites");
                        iAppoinment.putExtra("title", title);
                        iAppoinment.putExtra("terminology",terminologys);
                        iAppoinment.putExtra("place", myViewHolder.tv_loc.getText().toString());
                        mContext.startActivity(iAppoinment);



                    }
                });

               if(mFavList.get(i).getOnlinePresence().equals("true") && mFavList.get(i).getDonationServiceStatus().equals("true")){
                   myViewHolder.donationLayouts.setVisibility(View.VISIBLE);
               }
               else{
                   myViewHolder.donationLayouts.setVisibility(View.GONE);
               }


                final int finalI = i;
                myViewHolder.btndonations.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent iDonation = new Intent(v.getContext(), Donation.class);
                        iDonation.putExtra("serviceId", mFavList.get(finalI).getLocations().get(position).getId());
                        iDonation.putExtra("uniqueID", uniqueId);
                        iDonation.putExtra("accountID", String.valueOf(mFavList.get(finalI).getId()));
                        iDonation.putExtra("from", "favourites");
                        iDonation.putExtra("title", title);
                        iDonation.putExtra("terminology",terminologys);
                        iDonation.putExtra("place", myViewHolder.tv_loc.getText().toString());
                        mContext.startActivity(iDonation);



                    }
                });

                if(mFavList.get(i).isFutureCheckin()){
                   // myViewHolder.tv_date.setVisibility(View.VISIBLE);
                }else{
                    myViewHolder.tv_date.setVisibility(View.GONE);
                }


                if(mFavList.get(i).getPhoneNumbers()!=null){
                    if(mFavList.get(i).getPhoneNumbers().size()>0 && mFavList.get(i).isRevealPhoneNumber()){
                        myViewHolder.recycleview_contact.setVisibility(View.VISIBLE);
                        Fav_ContactAdapter checkAdapter = new Fav_ContactAdapter(mFavList.get(i).getPhoneNumbers(), mContext,contactAdapterCallback);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                        myViewHolder.recycleview_contact.setLayoutManager(mLayoutManager);
                        myViewHolder.recycleview_contact.setAdapter(checkAdapter);
                        checkAdapter.notifyDataSetChanged();
                    }else{
                        myViewHolder.recycleview_contact.setVisibility(View.GONE);
                    }
                }

            }
        }


        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        myViewHolder.tv_Open.setTypeface(tyface);
        if(queueList.getNextAvailableQueue()!=null && queueList.getNextAvailableQueue().isOpenNow()){
            myViewHolder.tv_Open.setVisibility(View.GONE); // Management asked to hide open now
            Config.logV("Open Now----------------");
        }else{
           // Config.logV("Open Now-------3333---------"+queueList.getNextAvailableQueue().isOpenNow());
            myViewHolder.tv_Open.setVisibility(View.GONE);
        }

        if(searchSetting!=null && searchSetting.getCalculationMode()!=null) {
            if (!searchSetting.getCalculationMode().equalsIgnoreCase("NoCalc") )  {
                mShowWaitTime = true;
            } else {
                if(queueList.getNextAvailableQueue()!=null){
                    if(searchSetting.getCalculationMode().equalsIgnoreCase("NoCalc") && queueList.getNextAvailableQueue().isShowToken()){
                        mShowWaitTime = true;
                    }else {
                        mShowWaitTime = false;
                    }
                }



            }
        }
//        //Estimate WaitTime
//        if (mShowWaitTime) {
//
//            if (queueList.getNextAvailableQueue()!=null &&queueList.getNextAvailableQueue().getAvailableDate() != null) {
//                myViewHolder.tv_waittime.setVisibility(View.VISIBLE);
//
//
//                if ((formattedDate.trim().equalsIgnoreCase(queueList.getNextAvailableQueue().getAvailableDate()))) {
//                    if (queueList.getNextAvailableQueue().getServiceTime() != null) {
//
//                        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
//                                "fonts/Montserrat_Bold.otf");
//
//                        String firstWord="Next Available Time ";
//                        /*String firstWord = null;
//                        Date dt = new Date();
//                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
//                        String currentTime = sdf.format(dt);
//                        Date datenow=parseDate(currentTime);
//
//                        dateCompareOne = parseDate(queueList.getNextAvailableQueue().getServiceTime());
//                        if ( datenow.after( dateCompareOne ) ) {
//                            firstWord = "Est Service Time ";
//                        }else {
//                            firstWord = "Est Wait Time ";
//
//                        }*/
//
//
//                        String secondWord="Today, "+queueList.getNextAvailableQueue().getServiceTime();
//                        Spannable spannable = new SpannableString(firstWord+secondWord);
//                        spannable.setSpan( new CustomTypefaceSpan("sans-serif",tyface1), firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.white)),
//                                firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                        myViewHolder.tv_waittime.setText(spannable);
//                    } else {
//
//                        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
//                                "fonts/Montserrat_Bold.otf");
//                        String firstWord="Est Wait Time ";
//                        String secondWord= Config.getTimeinHourMinutes(queueList.getNextAvailableQueue().getQueueWaitingTime());// + " Minutes";
//                        Spannable spannable = new SpannableString(firstWord+secondWord);
//                        spannable.setSpan( new CustomTypefaceSpan("sans-serif",tyface1), firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.white)),
//                                firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                        myViewHolder.tv_waittime.setText(spannable);
//                    }
//                }
//                if (date1.compareTo(date2) < 0) {
//                    try {
//                        // String mMonthName=getMonth(searchdetailList.getAvail_date());
//                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//                        Date date = format.parse(queueList.getNextAvailableQueue().getAvailableDate());
//                        String day = (String) DateFormat.format("dd", date);
//                        String monthString = (String) DateFormat.format("MMM", date);
//
//                        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
//                                "fonts/Montserrat_Bold.otf");
//                        //String firstWord="Next Wait Time ";
//                        String firstWord="Next Available Time ";
//                        String secondWord=  monthString + " " + day + ", " +queueList.getNextAvailableQueue().getServiceTime();
//                        Spannable spannable = new SpannableString(firstWord+secondWord);
//                        spannable.setSpan( new CustomTypefaceSpan("sans-serif",tyface1), firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.white)),
//                                firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                        myViewHolder.tv_waittime.setText(spannable);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//
//
//                }
//            } else {
//                myViewHolder.tv_waittime.setVisibility(View.GONE);
//            }
//         } else {
//            myViewHolder.tv_waittime.setVisibility(View.GONE);
//            myViewHolder.btn_checkin.setText("GET TOKEN");
//        }
//        if (queueList.getNextAvailableQueue()!=null && queueList.getNextAvailableQueue().getAvailableDate() != null) {
//            if ( queueList.getNextAvailableQueue().isShowToken()) {
//                myViewHolder.btn_checkin.setText("GET TOKEN");
//                myViewHolder.tv_date.setText("Do you want to Get Token for another day?");
//                if (queueList.getNextAvailableQueue().getPersonAhead() != -1) {
//                    Config.logV("personAheadtttt @@@@@@@@@@@6666@@@ ####" + queueList.getNextAvailableQueue().getPersonAhead());
//                    if (queueList.getNextAvailableQueue().getPersonAhead() == 0) {
//                        myViewHolder.tv_waittime.setVisibility(View.VISIBLE);
//                        myViewHolder.tv_waittime.setText(" Be the first in line");
//                    } else {
//                        myViewHolder.tv_waittime.setVisibility(View.VISIBLE);
//                        String firstWord=String.valueOf(queueList.getNextAvailableQueue().getPersonAhead());
//                        String secondWord=" People waiting in line";
//                        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
//                                "fonts/Montserrat_Bold.otf");
//                        Spannable spannable = new SpannableString(firstWord + secondWord);
//                        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), 0, firstWord.length() , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                        myViewHolder.tv_waittime.setText(spannable);
//                    }
//                }
//
//            } else {
//
//                if(queueList.getNextAvailableQueue().getCalculationMode().equalsIgnoreCase("NoCalc") && !queueList.getNextAvailableQueue().isShowToken()){
//                    myViewHolder.tv_waittime.setVisibility(View.GONE);
//                    if(terminologys.equals("order")){
//                        myViewHolder.btn_checkin.setText("ORDER");
//                        myViewHolder.tv_date.setText("Do you want to Order for another day?");
//                    }else{
//                        myViewHolder.btn_checkin.setText("CHECK-IN");
//                        myViewHolder.tv_date.setText("Do you want to Check-in for another day?");
//                    }
//                }else{
//                    if(terminologys!= null && terminologys.equals("order")){
//                        myViewHolder.btn_checkin.setText("ORDER");
//                        myViewHolder.tv_date.setText("Do you want to Order for another day?");
//                    }else{
//                        myViewHolder.btn_checkin.setText("CHECK-IN");
//                        myViewHolder.tv_date.setText("Do you want to Check-in for another day?");
//                    }
//
//
//                }
//            }
//        }


    }
    public void disableCheckinButton(FavLocationAdapter.MyViewHolder myViewHolder) {
        myViewHolder.btn_checkin.setBackgroundColor(Color.parseColor("#cfcfcf"));
        myViewHolder.btn_checkin.setTextColor(mContext.getResources().getColor(R.color.button_grey));
        myViewHolder.btn_checkin.setEnabled(false);
        myViewHolder.btn_checkin.setVisibility(View.VISIBLE);

    }
    public void disableCheckinFeature (FavLocationAdapter.MyViewHolder myViewHolder) {
        disableCheckinButton(myViewHolder);
        myViewHolder.btn_checkin.setVisibility(View.GONE);

    }
    public void enableCheckinButton(FavLocationAdapter.MyViewHolder myViewHolder) {
        myViewHolder.btn_checkin.setBackgroundColor(mContext.getResources().getColor(R.color.green));
        myViewHolder.btn_checkin.setTextColor(mContext.getResources().getColor(R.color.white));
        myViewHolder.btn_checkin.setEnabled(true);
        myViewHolder.btn_checkin.setVisibility(View.VISIBLE);

    }



    @Override
    public int getItemCount() {
        return mQueueList.size();
    }
}