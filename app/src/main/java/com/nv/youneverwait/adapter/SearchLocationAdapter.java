package com.nv.youneverwait.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nv.youneverwait.callback.SearchLocationAdpterCallback;
import com.nv.youneverwait.R;
import com.nv.youneverwait.activities.CheckIn;
import com.nv.youneverwait.activities.SearchServiceActivity;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.custom.CustomTypefaceSpan;
import com.nv.youneverwait.model.WorkingModel;
import com.nv.youneverwait.response.QueueList;
import com.nv.youneverwait.response.SearchCheckInMessage;
import com.nv.youneverwait.response.SearchLocation;
import com.nv.youneverwait.response.SearchService;
import com.nv.youneverwait.response.SearchSetting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by sharmila on 30/7/18.
 */

public class SearchLocationAdapter extends RecyclerView.Adapter<SearchLocationAdapter.MyViewHolder> {

    private List<SearchLocation> mSearchLocationList;
    static Context mContext;
    ArrayList<WorkingModel> workingModelArrayList = new ArrayList<>();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_place, tv_working, tv_open, tv_waittime, txt_diffdate;
        Button btn_checkin;
        LinearLayout mLSeriveLayout, mLayouthide, LexpandCheckin, Ldirectionlayout,LService_2;
        ImageView img_arrow;
        RecyclerView recycle_parking;
        RelativeLayout layout_exapnd;
        TextView txtdirection, tv_checkin;
        Button btn_checkin_expand;
        TextView txtwaittime_expand, txt_diffdate_expand, txtlocation_amentites, txtparkingSeeAll, txtservices;
        TextView txtservice1,txtservice2,txtSeeAll;

        public MyViewHolder(View view) {
            super(view);
            tv_checkin = (TextView) view.findViewById(R.id.txtcheckin);
            tv_place = (TextView) view.findViewById(R.id.txtLoc);
            tv_working = (TextView) view.findViewById(R.id.txtworking);
            btn_checkin = (Button) view.findViewById(R.id.btn_checkin);
            tv_open = (TextView) view.findViewById(R.id.txtopen);
            tv_waittime = (TextView) view.findViewById(R.id.txtwaittime);
            mLSeriveLayout = (LinearLayout) view.findViewById(R.id.lServicelayout);
            img_arrow = (ImageView) view.findViewById(R.id.img_arrow);
            mLayouthide = (LinearLayout) view.findViewById(R.id.mLayouthide);
            recycle_parking = (RecyclerView) view.findViewById(R.id.recycle_parking);
            txt_diffdate = (TextView) view.findViewById(R.id.txt_diffdate);
            layout_exapnd = (RelativeLayout) view.findViewById(R.id.layout_exapnd);
            txtdirection = (TextView) view.findViewById(R.id.txtdirection);

            txtwaittime_expand = (TextView) view.findViewById(R.id.txtwaittime_expand);
            txt_diffdate_expand = (TextView) view.findViewById(R.id.txt_diffdate_expand);
            btn_checkin_expand = (Button) view.findViewById(R.id.btn_checkin_expand);
            LexpandCheckin = (LinearLayout) view.findViewById(R.id.LexpandCheckin);
            Ldirectionlayout = (LinearLayout) view.findViewById(R.id.Ldirectionlayout);
            txtlocation_amentites = (TextView) view.findViewById(R.id.txtlocation_amentites);
            txtparkingSeeAll = (TextView) view.findViewById(R.id.txtparkingSeeAll);
            txtservices = (TextView) view.findViewById(R.id.txtservices);
            LService_2 = (LinearLayout) view.findViewById(R.id.LService_2);
            txtservice1 = (TextView) view.findViewById(R.id.txtservice1);
            txtservice2 = (TextView) view.findViewById(R.id.txtservice2);
            txtSeeAll = (TextView) view.findViewById(R.id.txtSeeAll);
        }
    }

    List<SearchService> mSearchServiceList;
    List<QueueList> mQueueList;
    SearchSetting mSearchSetting;
    String mTitle;
    private SearchLocationAdpterCallback adaptercallback;
    String mUniqueID, accountID;
    List<SearchCheckInMessage> mCheckInMessage;
    String sector, subsector;

    public SearchLocationAdapter(String sector, String subsector, String accountID, String uniqueid, SearchLocationAdpterCallback callback, String title, SearchSetting searchSetting, List<SearchLocation> mSearchLocation, Context mContext, List<SearchService> SearchServiceList, List<QueueList> SearchQueueList, List<SearchCheckInMessage> checkInMessage) {
        this.mContext = mContext;
        this.mSearchLocationList = mSearchLocation;
        this.mSearchServiceList = SearchServiceList;
        this.mQueueList = SearchQueueList;
        this.mSearchSetting = searchSetting;
        this.mTitle = title;
        this.adaptercallback = callback;
        mUniqueID = uniqueid;
        this.accountID = accountID;
        this.mCheckInMessage = checkInMessage;
        this.sector = sector;
        this.subsector = subsector;
        Config.logV("Search Service-----1111-----------" + mSearchServiceList.size());


    }

    @Override
    public SearchLocationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.searchlocation_list, parent, false);

        return new SearchLocationAdapter.MyViewHolder(itemView);
    }

    boolean mShowWaitTime = false;
    boolean mFlagCLick = false, mFlagCLick1 = false;


    static SimpleDateFormat inputParser = new SimpleDateFormat("HH:mm", Locale.US);
    private static Date dateCompareOne;

    private static Date parseDate(String date) {

        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    ArrayList<ParkingModel> listType = new ArrayList<>();

    @Override
    public void onBindViewHolder(final SearchLocationAdapter.MyViewHolder myViewHolder, final int position) {
        final SearchLocation searchLoclist = mSearchLocationList.get(position);


        Typeface tyface3 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        myViewHolder.txtservices.setTypeface(tyface3);
        for (int i = 0; i < mCheckInMessage.size(); i++) {
            if (searchLoclist.getId() == mCheckInMessage.get(i).getLocid()) {

                myViewHolder.tv_checkin.setVisibility(View.VISIBLE);
                //  myViewHolder.tv_checkin.setText("You have "+mCheckInMessage.get(i).getmAllSearch_checkIn().size()+" Check-In at this location");
                Config.logV("Locationttt-----kkkk###########@@@@@@" + searchLoclist.getId());
                Config.logV("Locationttt-----aaaa###########@@@@@@" + mCheckInMessage.get(i).getmAllSearch_checkIn().size());


                String firstWord = "You have ";
                String secondWord = mCheckInMessage.get(i).getmAllSearch_checkIn().size() + " Check-In";
                String thirdword = " at this location";

                Spannable spannable = new SpannableString(firstWord + secondWord + thirdword);
                Typeface tyface_edittext2 = Typeface.createFromAsset(mContext.getAssets(),
                        "fonts/Montserrat_Bold.otf");
                spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface_edittext2), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                myViewHolder.tv_checkin.setText(spannable);

            } else {
                //myViewHolder.tv_checkin.setVisibility(View.GONE);
            }
        }

        myViewHolder.tv_checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adaptercallback.onMethodCheckinCallback(searchLoclist.getId(), "show", searchLoclist.getPlace());
            }
        });


        if (searchLoclist.getGoogleMapUrl() != null && !searchLoclist.getGoogleMapUrl().equalsIgnoreCase("")) {
            myViewHolder.Ldirectionlayout.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.Ldirectionlayout.setVisibility(View.GONE);
        }

        myViewHolder.txtdirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.logV("googlemap url--------" + searchLoclist.getGoogleMapUrl());
                String geoUri = searchLoclist.getGoogleMapUrl();
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                    mContext.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        myViewHolder.tv_place.setTypeface(tyface);
        myViewHolder.tv_open.setTypeface(tyface);
        myViewHolder.btn_checkin.setTypeface(tyface);


        if (searchLoclist.getParkingType() != null) {
            if (searchLoclist.getParkingType().equalsIgnoreCase("free") || searchLoclist.getParkingType().equalsIgnoreCase("none")) {
                ParkingModel mType = new ParkingModel();
                mType.setId("1");
                // mType.setTypeicon(R.drawable.icon_24hours);
                mType.setTypename("Parking " + searchLoclist.getParkingType());
                listType.add(mType);
            }
        }
        if (searchLoclist.getLocationVirtualFields().getDocambulance() != null) {
            if (searchLoclist.getLocationVirtualFields().getDocambulance().equalsIgnoreCase("true")) {
                ParkingModel mType = new ParkingModel();
                mType.setId("4");
                // mType.setTypeicon(R.drawable.icon_24hours);
                mType.setTypename("Ambulance");
                listType.add(mType);
            }
        }

        if (searchLoclist.getLocationVirtualFields().getFirstaid() != null) {

            if (searchLoclist.getLocationVirtualFields().getFirstaid().equalsIgnoreCase("true")) {
                ParkingModel mType = new ParkingModel();
                // mType.setTypeicon(R.drawable.icon_24hours);
                mType.setId("5");
                mType.setTypename("First Aid");
                listType.add(mType);
            }
        }

        try {
            if (searchLoclist.isOpen24hours()) {
                ParkingModel mType = new ParkingModel();
                // mType.setTypeicon(R.drawable.icon_24hours);
                mType.setId("2");
                mType.setTypename("24 Hours");
                listType.add(mType);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (searchLoclist.getLocationVirtualFields().getTraumacentre() != null) {

            if (searchLoclist.getLocationVirtualFields().getTraumacentre().equalsIgnoreCase("true")) {
                ParkingModel mType = new ParkingModel();
                // mType.setTypeicon(R.drawable.icon_24hours);
                mType.setId("7");
                mType.setTypename("Trauma");
                listType.add(mType);
            }
        }


        if (searchLoclist.getLocationVirtualFields().getPhysiciansemergencyservices() != null) {
            if (searchLoclist.getLocationVirtualFields().getPhysiciansemergencyservices().equalsIgnoreCase("true")) {
                ParkingModel mType = new ParkingModel();
                // mType.setTypeicon(R.drawable.icon_24hours);
                mType.setId("3");
                mType.setTypename("Emergency");
                listType.add(mType);
            }
        }

        if (searchLoclist.getLocationVirtualFields().getDentistemergencyservices() != null) {
            if (searchLoclist.getLocationVirtualFields().getDentistemergencyservices().equalsIgnoreCase("true")) {
                ParkingModel mType = new ParkingModel();
                // mType.setTypeicon(R.drawable.icon_24hours);
                mType.setId("6");
                mType.setTypename("Emergency");
                listType.add(mType);
            }
        }


        if (listType.size() > 0) {
            Typeface tyface2 = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/Montserrat_Bold.otf");

            myViewHolder.txtlocation_amentites.setTypeface(tyface2);
            Config.logV("Location Ament---------------" + listType.size());
            if (listType.size() > 2) {
                myViewHolder.txtparkingSeeAll.setVisibility(View.VISIBLE);
            } else {
                myViewHolder.txtparkingSeeAll.setVisibility(View.GONE);
            }
            myViewHolder.txtlocation_amentites.setVisibility(View.VISIBLE);
            myViewHolder.recycle_parking.setVisibility(View.VISIBLE);

            int size=listType.size();
            if(size==1){
                size=1;
            }else{
                size=2;
            }
            ParkingTypesAdapter mParkTypeAdapter = new ParkingTypesAdapter(listType, mContext, size);
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
            myViewHolder.recycle_parking.setLayoutManager(horizontalLayoutManager);
            myViewHolder.recycle_parking.setAdapter(mParkTypeAdapter);
        } else {
            myViewHolder.txtparkingSeeAll.setVisibility(View.GONE);
            myViewHolder.txtlocation_amentites.setVisibility(View.GONE);
            myViewHolder.recycle_parking.setVisibility(View.GONE);
        }

        myViewHolder.txtparkingSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!searchLoclist.isLocationAmentOpen()) {
                    ParkingTypesAdapter mParkTypeAdapter = new ParkingTypesAdapter(listType, mContext, listType.size());
                    LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                    myViewHolder.recycle_parking.setLayoutManager(horizontalLayoutManager);
                    myViewHolder.recycle_parking.setAdapter(mParkTypeAdapter);
                    myViewHolder.txtparkingSeeAll.setText("See Less");
                    searchLoclist.setLocationAmentOpen(true);

                } else {
                    int size=listType.size();
                    if(size==1){
                        size=1;
                    }else{
                        size=2;
                    }
                    ParkingTypesAdapter mParkTypeAdapter = new ParkingTypesAdapter(listType, mContext, size);
                    LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                    myViewHolder.recycle_parking.setLayoutManager(horizontalLayoutManager);
                    myViewHolder.recycle_parking.setAdapter(mParkTypeAdapter);
                    myViewHolder.txtparkingSeeAll.setText("See All");
                    searchLoclist.setLocationAmentOpen(false);
                }
            }
        });


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

        if (position == 0) {
            myViewHolder.mLayouthide.setVisibility(View.VISIBLE);
            myViewHolder.img_arrow.setImageResource(R.drawable.icon_angle_up);
            searchLoclist.setExpandFlag(true);
            myViewHolder.LexpandCheckin.setVisibility(View.GONE);

        } else {
            myViewHolder.mLayouthide.setVisibility(View.GONE);
            myViewHolder.img_arrow.setImageResource(R.drawable.icon_angle_down);
            myViewHolder.LexpandCheckin.setVisibility(View.VISIBLE);

        }


        if (mSearchSetting.isFutureDateWaitlist()) {
            myViewHolder.txt_diffdate.setVisibility(View.VISIBLE);
            myViewHolder.txt_diffdate_expand.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.txt_diffdate.setVisibility(View.GONE);
            myViewHolder.txt_diffdate_expand.setVisibility(View.GONE);
        }


        myViewHolder.txt_diffdate_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Config.logV("DETAIL !!!!!!!!!!!!!------"+);
                Intent iCheckIn = new Intent(v.getContext(), CheckIn.class);
                iCheckIn.putExtra("serviceId", searchLoclist.getId());
                iCheckIn.putExtra("uniqueID", mUniqueID);
                iCheckIn.putExtra("accountID", accountID);
                iCheckIn.putExtra("from", "searchdetail_future");
                iCheckIn.putExtra("title", mTitle);
                iCheckIn.putExtra("place", searchLoclist.getPlace());
                iCheckIn.putExtra("googlemap", searchLoclist.getGoogleMapUrl());

                iCheckIn.putExtra("sector", sector);
                iCheckIn.putExtra("subsector", subsector);
                mContext.startActivity(iCheckIn);
            }
        });


        myViewHolder.txt_diffdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Config.logV("DETAIL !!!!!!!!!!!!!------"+);
                Intent iCheckIn = new Intent(v.getContext(), CheckIn.class);
                iCheckIn.putExtra("serviceId", searchLoclist.getId());
                iCheckIn.putExtra("uniqueID", mUniqueID);
                iCheckIn.putExtra("accountID", accountID);
                iCheckIn.putExtra("from", "searchdetail_future");
                iCheckIn.putExtra("title", mTitle);
                iCheckIn.putExtra("place", searchLoclist.getPlace());
                iCheckIn.putExtra("googlemap", searchLoclist.getGoogleMapUrl());

                iCheckIn.putExtra("sector", sector);
                iCheckIn.putExtra("subsector", subsector);
                mContext.startActivity(iCheckIn);
            }
        });

        myViewHolder.btn_checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iCheckIn = new Intent(v.getContext(), CheckIn.class);
                iCheckIn.putExtra("serviceId", searchLoclist.getId());
                iCheckIn.putExtra("uniqueID", mUniqueID);
                iCheckIn.putExtra("accountID", accountID);
                iCheckIn.putExtra("from", "searchdetail_checkin");
                iCheckIn.putExtra("title", mTitle);
                iCheckIn.putExtra("place", searchLoclist.getPlace());
                iCheckIn.putExtra("googlemap", searchLoclist.getGoogleMapUrl());

                iCheckIn.putExtra("sector", sector);
                iCheckIn.putExtra("subsector", subsector);
                mContext.startActivity(iCheckIn);
            }
        });

        myViewHolder.btn_checkin_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iCheckIn = new Intent(v.getContext(), CheckIn.class);
                iCheckIn.putExtra("serviceId", searchLoclist.getId());
                iCheckIn.putExtra("uniqueID", mUniqueID);
                iCheckIn.putExtra("accountID", accountID);
                iCheckIn.putExtra("from", "searchdetail_checkin");
                iCheckIn.putExtra("title", mTitle);
                iCheckIn.putExtra("place", searchLoclist.getPlace());
                iCheckIn.putExtra("googlemap", searchLoclist.getGoogleMapUrl());

                iCheckIn.putExtra("sector", sector);
                iCheckIn.putExtra("subsector", subsector);
                mContext.startActivity(iCheckIn);
            }
        });


        myViewHolder.layout_exapnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchLoclist.isExpandFlag()) {
                    myViewHolder.mLayouthide.setVisibility(View.VISIBLE);
                    myViewHolder.img_arrow.setImageResource(R.drawable.icon_angle_up);
                    searchLoclist.setExpandFlag(true);
                    myViewHolder.LexpandCheckin.setVisibility(View.GONE);

                } else {
                    myViewHolder.mLayouthide.setVisibility(View.GONE);
                    myViewHolder.img_arrow.setImageResource(R.drawable.icon_angle_down);
                    searchLoclist.setExpandFlag(false);
                    myViewHolder.LexpandCheckin.setVisibility(View.VISIBLE);

                }
            }
        });

        /*myViewHolder.layout_exapnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (position == 0) {

                    if (!mFlagCLick1) {
                        myViewHolder.mLayouthide.setVisibility(View.GONE);
                        myViewHolder.img_arrow.setImageResource(R.drawable.icon_angle_down);
                        mFlagCLick1 = true;
                    } else {
                        myViewHolder.mLayouthide.setVisibility(View.VISIBLE);
                        myViewHolder.img_arrow.setImageResource(R.drawable.icon_angle_up);
                        mFlagCLick1 = false;
                    }


                } else {
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
        });*/


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


                        adaptercallback.onMethodWorkingCallback(workingModelArrayList, mTitle);
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


                int size = mSearchServiceList.get(i).getmAllService().size();
                if (size == 2) {
                    size = 2;
                } else if (size == 1) {
                    size = 1;
                } else if (size > 2) {
                    size = 3;
                }

                if (size > 0) {



                        if (size == 1) {
                            myViewHolder.mLSeriveLayout.setVisibility(View.GONE);
                            myViewHolder.LService_2.setVisibility(View.VISIBLE);
                            myViewHolder. txtservice1.setVisibility(View.VISIBLE);
                            myViewHolder.txtservice2.setVisibility(View.GONE);
                            myViewHolder.txtSeeAll.setVisibility(View.GONE);
                            myViewHolder.txtservices.setVisibility(View.VISIBLE);
                            myViewHolder.txtservice1.setText(mSearchServiceList.get(i).getmAllService().get(0).getName());

                        } else {
                            myViewHolder.mLSeriveLayout.setVisibility(View.GONE);
                            myViewHolder.LService_2.setVisibility(View.VISIBLE);
                            myViewHolder.txtservices.setVisibility(View.VISIBLE);
                            myViewHolder.txtservice1.setVisibility(View.VISIBLE);
                            myViewHolder.txtservice2.setVisibility(View.VISIBLE);
                            myViewHolder.txtSeeAll.setVisibility(View.VISIBLE);
                            myViewHolder.txtservice1.setText(mSearchServiceList.get(i).getmAllService().get(0).getName());
                            myViewHolder.txtservice2.setText(mSearchServiceList.get(i).getmAllService().get(1).getName());

                            final int finalI1 = i;
                            myViewHolder.txtSeeAll.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myViewHolder.LService_2.setVisibility(View.GONE);
                                    myViewHolder.mLSeriveLayout.setVisibility(View.VISIBLE);
                                    myViewHolder.mLSeriveLayout.removeAllViews();
                                    LinearLayout parent1 = new LinearLayout(mContext);
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    parent1.setOrientation(LinearLayout.VERTICAL);
                                    parent1.setLayoutParams(params);
                                    int size = mSearchServiceList.get(finalI1).getmAllService().size();
                                    for (int j = 0; j < size; j++) {

                                        TextView dynaText = new TextView(mContext);
                                        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                                                "fonts/Montserrat_Regular.otf");
                                        dynaText.setTypeface(tyface);
                                        dynaText.setText(mSearchServiceList.get(finalI1).getmAllService().get(j).getName());


                                        dynaText.setTextSize(14);
                                        dynaText.setTextColor(mContext.getResources().getColor(R.color.title_grey));
                                        //  dynaText.setPadding(5, 5, 5, 5);
                                        dynaText.setMaxLines(1);
                                        dynaText.setLayoutParams(params);


                                        params.setMargins(0, 10, 0, 0);
                                        dynaText.setGravity(Gravity.LEFT);
                                        parent1.addView(dynaText);

                                    }
                                    TextView dynaText = new TextView(mContext);
                                    Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                                            "fonts/Montserrat_Regular.otf");
                                    dynaText.setTypeface(tyface);
                                    dynaText.setText("See Less");

                                    dynaText.setTextSize(14);
                                    dynaText.setTextColor(mContext.getResources().getColor(R.color.title_consu));
                                    // dynaText.setPadding(5, 5, 5, 5);
                                    dynaText.setMaxLines(1);
                                    dynaText.setLayoutParams(params);


                                    params.setMargins(0, 10, 0, 0);
                                    dynaText.setGravity(Gravity.LEFT);
                                    parent1.addView(dynaText);
                                    dynaText.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            myViewHolder.LService_2.setVisibility(View.VISIBLE);
                                            myViewHolder.mLSeriveLayout.setVisibility(View.GONE);
                                            myViewHolder.txtservice1.setVisibility(View.VISIBLE);
                                            myViewHolder.txtservice2.setVisibility(View.VISIBLE);
                                            myViewHolder.txtSeeAll.setVisibility(View.VISIBLE);
                                            myViewHolder.txtservice1.setText(mSearchServiceList.get(finalI1).getmAllService().get(0).getName());
                                            myViewHolder.txtservice2.setText(mSearchServiceList.get(finalI1).getmAllService().get(1).getName());

                                        }
                                    });

                                    myViewHolder.mLSeriveLayout.addView(parent1);
                                }
                            });


                      /*  TextView dynaText = new TextView(mContext);
                        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                "fonts/Montserrat_Regular.otf");
                        dynaText.setTypeface(tyface1);
                        dynaText.setText(mSearchServiceList.get(i).getmAllService().get(j).getName());
                        dynaText.setTextSize(13);
                        dynaText.setPadding(10, 10, 10, 10);
                        dynaText.setBackground(mContext.getResources().getDrawable(R.drawable.input_border_rounded_blue_bg));
                        dynaText.setTextColor(mContext.getResources().getColor(R.color.title_grey));
                        dynaText.setEllipsize(TextUtils.TruncateAt.END);
                        dynaText.setMaxLines(1);
                        dynaText.setMaxEms(6);

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, 0, 10, 0);
                        dynaText.setLayoutParams(params);

                        final String mServicename = mSearchServiceList.get(i).getmAllService().get(j).getName();
                        final String mServiceprice = mSearchServiceList.get(i).getmAllService().get(j).getTotalAmount();
                        final String mServicedesc = mSearchServiceList.get(i).getmAllService().get(j).getDescription();
                        final String mServiceduration = mSearchServiceList.get(i).getmAllService().get(j).getServiceDuration();
                        final boolean mTaxable = mSearchServiceList.get(i).getmAllService().get(j).isTaxable();
                        final ArrayList<SearchService> mServiceGallery = mSearchServiceList.get(i).getmAllService().get(j).getServicegallery();

                        final boolean isPrepayment = mSearchServiceList.get(i).getmAllService().get(j).isPrePayment();
                        final String minPrepayment = mSearchServiceList.get(i).getmAllService().get(j).getMinPrePaymentAmount();
                        dynaText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent iService = new Intent(v.getContext(), SearchServiceActivity.class);
                                iService.putExtra("name", mServicename);
                                iService.putExtra("duration", mServiceduration);
                                iService.putExtra("price", mServiceprice);
                                iService.putExtra("desc", mServicedesc);
                                iService.putExtra("servicegallery", mServiceGallery);
                                iService.putExtra("taxable", mTaxable);
                                iService.putExtra("title", mTitle);
                                iService.putExtra("isPrePayment", isPrepayment);
                                iService.putExtra("MinPrePaymentAmount", minPrepayment);
                                mContext.startActivity(iService);

                            }
                        });
                        myViewHolder.mLSeriveLayout.addView(dynaText);
                    }

                    if (size > 1) {

                        TextView dynaText = new TextView(mContext);
                        final int finalI = i;
                        dynaText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                           *//* ServiceListFragment pfFragment = new ServiceListFragment();
                            FragmentTransaction transaction = DashboardFragment.getHomeFragment().getFragmentManager().beginTransaction();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("servicelist", mSearchServiceList.get(finalI).getmAllService());
                            bundle.putString("title",mTitle);
                            pfFragment.setArguments(bundle);
                            // Store the Fragment in stack
                            transaction.addToBackStack(null);
                            transaction.replace(R.id.mainlayout, pfFragment).commit();*//*

                                adaptercallback.onMethodServiceCallback(mSearchServiceList.get(finalI).getmAllService(), mTitle);
                            }
                        });
                        dynaText.setGravity(Gravity.CENTER);
                        dynaText.setBackground(mContext.getResources().getDrawable(R.drawable.icon_arrowright_blue));
                        myViewHolder.mLSeriveLayout.addView(dynaText);
                    }*/
                        }
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


                    if (mSearchSetting.isOnlineCheckIns()) {
                        if (mQueueList.get(i).getNextAvailableQueue().getAvailableDate() != null) {

                            if ((formattedDate.trim().equalsIgnoreCase(mQueueList.get(i).getNextAvailableQueue().getAvailableDate()))) {

                                myViewHolder.btn_checkin.setVisibility(View.VISIBLE);
                                // myViewHolder.btn_checkin.setBackground(mContext.getResources().getDrawable(R.drawable.button_gradient_checkin));
                                myViewHolder.btn_checkin.setBackgroundColor(mContext.getResources().getColor(R.color.blue));
                                myViewHolder.btn_checkin.setTextColor(mContext.getResources().getColor(R.color.white));
                                myViewHolder.btn_checkin_expand.setVisibility(View.VISIBLE);


                            } else if (date1.compareTo(date2) < 0) {
                                myViewHolder.btn_checkin.setVisibility(View.VISIBLE);
                                // myViewHolder.btn_checkin.setBackgroundColor(Color.parseColor("#cfcfcf"));
                                //   myViewHolder.btn_checkin.setBackground(mContext.getResources().getDrawable(R.drawable.btn_checkin_grey));
                                myViewHolder.btn_checkin.setTextColor(mContext.getResources().getColor(R.color.button_grey));
                                myViewHolder.btn_checkin.setEnabled(false);

                                myViewHolder.btn_checkin.setBackgroundColor(Color.parseColor("#cfcfcf"));
                                myViewHolder.btn_checkin_expand.setVisibility(View.VISIBLE);
                                myViewHolder.btn_checkin_expand.setBackground(mContext.getResources().getDrawable(R.drawable.btn_checkin_grey));
                                myViewHolder.btn_checkin_expand.setTextColor(mContext.getResources().getColor(R.color.button_grey));
                                myViewHolder.btn_checkin_expand.setEnabled(false);


                            }
                        } else {

                            myViewHolder.btn_checkin.setVisibility(View.GONE);
                            myViewHolder.btn_checkin_expand.setVisibility(View.GONE);

                        }
                    } else {
                        myViewHolder.btn_checkin.setVisibility(View.GONE);
                        myViewHolder.btn_checkin_expand.setVisibility(View.GONE);
                    }


                    //Estimate WaitTime
                    if (mShowWaitTime) {

                        if (mQueueList.get(i).getNextAvailableQueue().getAvailableDate() != null) {
                            myViewHolder.tv_waittime.setVisibility(View.VISIBLE);
                            myViewHolder.txtwaittime_expand.setVisibility(View.VISIBLE);


                            if ((formattedDate.trim().equalsIgnoreCase(mQueueList.get(i).getNextAvailableQueue().getAvailableDate()))) {
                                if (mQueueList.get(i).getNextAvailableQueue().getServiceTime() != null) {

                                    Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                            "fonts/Montserrat_Bold.otf");

                                    /*String firstWord = null;
                                    Date dt = new Date();
                                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                                    String currentTime = sdf.format(dt);
                                    Date datenow=parseDate(currentTime);

                                    dateCompareOne = parseDate(mQueueList.get(i).getNextAvailableQueue().getServiceTime());
                                    if ( datenow.after( dateCompareOne ) ) {
                                        firstWord = "Est Service Time \n";
                                    }else {
                                        firstWord = "Est Wait Time \n";

                                    }*/

                                    String firstWord = "Checked in for ";
                                    //String firstWord = "Est Service Time \n";
                                    String secondWord = "Today, " + mQueueList.get(i).getNextAvailableQueue().getServiceTime();
                                    Spannable spannable = new SpannableString(firstWord + secondWord);
                                    spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    /*spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                                            firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/

                                    myViewHolder.tv_waittime.setText(spannable);
                                    myViewHolder.txtwaittime_expand.setText(spannable);
                                } else {

                                    Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                            "fonts/Montserrat_Bold.otf");
                                    String firstWord = "Est Wait Time ";
                                    String secondWord = mQueueList.get(i).getNextAvailableQueue().getQueueWaitingTime() + " Minutes";
                                    Spannable spannable = new SpannableString(firstWord + secondWord);
                                    spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                   /* spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                                            firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
*/
                                    myViewHolder.tv_waittime.setText(spannable);
                                    myViewHolder.txtwaittime_expand.setText(spannable);
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
                                    String firstWord = "Next Available Time \n";
                                    String secondWord = monthString + " " + day + ", " + mQueueList.get(i).getNextAvailableQueue().getServiceTime();
                                    Spannable spannable = new SpannableString(firstWord + secondWord);
                                    spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    /*spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                                            firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/

                                    myViewHolder.tv_waittime.setText(spannable);
                                    myViewHolder.txtwaittime_expand.setText(spannable);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }


                            }
                        } else {
                            myViewHolder.tv_waittime.setVisibility(View.INVISIBLE);
                            myViewHolder.txtwaittime_expand.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        myViewHolder.tv_waittime.setVisibility(View.INVISIBLE);
                        myViewHolder.txtwaittime_expand.setVisibility(View.INVISIBLE);
                    }


                }
            } else {

            }

        }


    }


    @Override
    public int getItemCount() {
        return mSearchLocationList.size();
    }
}
