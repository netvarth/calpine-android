package com.jaldeeinc.jaldee.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Appointment;
import com.jaldeeinc.jaldee.activities.CheckIn;
import com.jaldeeinc.jaldee.activities.Donation;
import com.jaldeeinc.jaldee.callback.SearchLocationAdpterCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.AppointmentServiceInfoDialog;
import com.jaldeeinc.jaldee.custom.CustomTypefaceSpan;
import com.jaldeeinc.jaldee.custom.DonationServiceDialog;
import com.jaldeeinc.jaldee.custom.LocationAmenitiesDialog;
import com.jaldeeinc.jaldee.custom.ServiceInfoDialog;

import com.jaldeeinc.jaldee.model.NextAvailableQModel;
import com.jaldeeinc.jaldee.model.WorkingModel;
import com.jaldeeinc.jaldee.response.QueueList;
import com.jaldeeinc.jaldee.response.ScheduleList;
import com.jaldeeinc.jaldee.response.SearchAWsResponse;
import com.jaldeeinc.jaldee.response.SearchAppointmentDepartmentServices;
import com.jaldeeinc.jaldee.response.SearchCheckInMessage;
import com.jaldeeinc.jaldee.response.SearchDepartmentServices;
import com.jaldeeinc.jaldee.response.SearchDonation;
import com.jaldeeinc.jaldee.response.SearchLocation;
import com.jaldeeinc.jaldee.response.SearchService;
import com.jaldeeinc.jaldee.response.SearchSetting;
import com.jaldeeinc.jaldee.response.SearchViewDetail;

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
    private List<SearchAWsResponse> mSearchRespDetail;
    static Context mContext;
    String secondWord, firstWord;
    ArrayList serviceNames = new ArrayList();
    ArrayList serviceDonationNames = new ArrayList();
    ServiceInfoDialog serviceInfoDialog;
    AppointmentServiceInfoDialog appServInfoDialog;
    ArrayList<SearchAppointmentDepartmentServices> appList = new ArrayList<>();
    LocationAmenitiesDialog locationAmenitiesDialog;
    DonationServiceDialog donationServiceDialog;
    private SearchViewDetail providerInfo;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_place, tv_working, tv_open, tv_waittime, txt_diffdate, txt_msg, txt_peopleahead;
        Button btn_checkin;
        LinearLayout mLSeriveLayout, mLayouthide, LexpandCheckin, Ldirectionlayout, LService_2, LWorkinHrs, LDepartment_2, LAppointment, LApp_Services, LDonation, LDont_Services;
        ImageView img_arrow;
        RecyclerView recycle_parking;
        RelativeLayout layout_exapnd;
        TextView txtdirection, tv_checkin;
        Button btn_checkin_expand;
        TextView txtwaittime_expand, txt_diffdate_expand, txtlocation_amentites, txtparkingSeeAll, txtservices, txtdayofweek;
        TextView txtservice1, txtservice2, txtSeeAll, txtwork1, txtworkSeeAll, txtworking;
        TextView txt_earliestAvailable, txt_apptservices, txt_dontservices, txtapptSeeAll, txtdntSeeAll;
        Button btn_appointments, btn_donations;
        TextView tvAppService1, tvAppService2, tvAppSeeAll, tvAvailDate;
        TextView tvDntService1, tvDntService2, tvDntSeeAll,tvDonationAmount;

        ArrayList<WorkingModel> workingModelArrayList = new ArrayList<>();
        String txtdataMon = "";
        String txtdataTue = "";
        String txtdataWed = "";
        String txtdataThu = "";
        String txtdataFri = "";
        String txtdataSat = "";
        String txtdataSun = "";

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
            txtdayofweek = (TextView) view.findViewById(R.id.txtdayofweek);
            txtwork1 = (TextView) view.findViewById(R.id.txtwork1);
            txtworkSeeAll = (TextView) view.findViewById(R.id.txtworkSeeAll);
            LWorkinHrs = (LinearLayout) view.findViewById(R.id.LWorkinHrs);
            txtworking = (TextView) view.findViewById(R.id.txtworking);
            LDepartment_2 = (LinearLayout) view.findViewById(R.id.LDepartment_2);
            txt_msg = (TextView) view.findViewById(R.id.txt_msg);
            txt_peopleahead = (TextView) view.findViewById(R.id.txt_PeopleAhead);
            LAppointment = view.findViewById(R.id.appoinmentLayouts);
            btn_appointments = view.findViewById(R.id.btnappointments);
            LApp_Services = view.findViewById(R.id.appointmentList);
            txt_apptservices = view.findViewById(R.id.txtapptservices);
            LDonation = view.findViewById(R.id.donationLayouts);
            btn_donations = view.findViewById(R.id.btndonations);
            LDont_Services = view.findViewById(R.id.donationList);
            txt_dontservices = view.findViewById(R.id.txtdontservices);
            txtapptSeeAll = view.findViewById(R.id.txtapptSeeAll);
            txtdntSeeAll = view.findViewById(R.id.txtdntSeeAll);
            tvAppService1 = view.findViewById(R.id.tv_appService1);
            tvAppService2 = view.findViewById(R.id.tv_appservice2);
            tvAppSeeAll = view.findViewById(R.id.tv_appSeeAll);
            tvAvailDate = view.findViewById(R.id.txtavaildate);
            tvDntService1 = view.findViewById(R.id.tv_dntService1);
            tvDntService2 = view.findViewById(R.id.tv_dntService2);
            tvDntSeeAll = view.findViewById(R.id.tv_dntSeeAll);
            tvDonationAmount = view.findViewById(R.id.tv_donationAmount);
        }
    }

    List<SearchService> mSearchServiceList;
    List<QueueList> mQueueList;
    SearchSetting mSearchSetting;
    List<ScheduleList> mScheduleList;

    SearchAWsResponse mSearchAwsResponse;
    String mTitle;
    private SearchLocationAdpterCallback adaptercallback;
    String mUniqueID, accountID;
    List<SearchCheckInMessage> mCheckInMessage;
    String sector, subsector;
    String calcMode;
    String terminology;
    boolean isShowTokenId;
    boolean online_presence;
    boolean donationFundRaising;
    boolean virtualServices;
    ArrayList<SearchDepartmentServices> mSearchDepartmentList;
    ArrayList<SearchDonation> gServicesList;
    ArrayList<SearchAppointmentDepartmentServices> aServicesList;


    public SearchLocationAdapter(String sector, String subsector, String accountID, String uniqueid, SearchLocationAdpterCallback callback, String title, SearchSetting searchSetting, List<SearchLocation> mSearchLocation, Context mContext, List<SearchService> SearchServiceList, List<QueueList> SearchQueueList, List<SearchCheckInMessage> checkInMessage, String mCalcMode, String terminology, boolean isShowTokenId, ArrayList<SearchDepartmentServices> mSearchDepartments, List<SearchAWsResponse> mSearchRespDetails, SearchAWsResponse mSearchAWSResponse, List<ScheduleList> SearchScheduleList, boolean online_presence, boolean donationFundRaising, ArrayList<SearchDonation> gServicesList, ArrayList<SearchAppointmentDepartmentServices> aServiceList, boolean virtualServices) {
        this.mContext = mContext;
        this.mSearchLocationList = mSearchLocation;
        this.mSearchRespDetail = mSearchRespDetails;
        this.mSearchServiceList = SearchServiceList;
        this.mQueueList = SearchQueueList;
        this.mSearchSetting = searchSetting;
        this.mSearchAwsResponse = mSearchAWSResponse;
        this.mTitle = title;
        this.adaptercallback = callback;
        mUniqueID = uniqueid;
        this.accountID = accountID;
        this.mCheckInMessage = checkInMessage;
        this.sector = sector;
        this.subsector = subsector;
        Config.logV("Search Service-----1111-----------" + mSearchServiceList.size());
        this.calcMode = mCalcMode;
        this.terminology = terminology;
        this.isShowTokenId = isShowTokenId;
        this.mSearchDepartmentList = mSearchDepartments;
        this.mScheduleList = SearchScheduleList;
        this.online_presence = online_presence;
        this.donationFundRaising = donationFundRaising;
        this.gServicesList = gServicesList;
        this.aServicesList = aServiceList;
        this.virtualServices = virtualServices;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.searchlocation_list, parent, false);
        return new MyViewHolder(itemView);
    }

    boolean mShowWaitTime = false;
    boolean mFlagCLick = false, mFlagCLick1 = false;
    static SimpleDateFormat inputParser = new SimpleDateFormat("HH:mm", Locale.US);
    private static Date dateCompareOne;

    private static Date parseDate(String date) {
        try {
            return inputParser.parse(date);
        } catch (ParseException e) {
            return new Date(0);
        }
    }

    ArrayList<ParkingModel> listType = new ArrayList<>();

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int position) {
        final SearchLocation searchLoclist = mSearchLocationList.get(position);
        for (int i = 0; i < mCheckInMessage.size(); i++) {
            if (searchLoclist.getId() == mCheckInMessage.get(i).getLocid()) {
                myViewHolder.tv_checkin.setVisibility(View.VISIBLE);
                //  myViewHolder.tv_checkin.setText("You have "+mCheckInMessage.get(i).getmAllSearch_checkIn().size()+" Check-In at this location");
                Config.logV("Locationttt-----kkkk###########@@@@@@" + searchLoclist.getId());
                Config.logV("Locationttt-----aaaa###########@@@@@@" + mCheckInMessage.get(i).getmAllSearch_checkIn().size());
                if (terminology != null) {
                    String firstWord = "You have ";
                    String secondWord = mCheckInMessage.get(i).getmAllSearch_checkIn().size() + " " + terminology;
                    String thirdword = " at this location";
                    Spannable spannable = new SpannableString(firstWord + secondWord + thirdword);
                    Typeface tyface_edittext2 = Typeface.createFromAsset(mContext.getAssets(),
                            "fonts/Montserrat_Bold.otf");
                    spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface_edittext2), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    myViewHolder.tv_checkin.setText(spannable);

                }
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
        myViewHolder.btn_appointments.setTypeface(tyface);
        myViewHolder.btn_donations.setTypeface(tyface);

        listType.clear();
        handleLocationAmenities(myViewHolder, searchLoclist);
        if (searchLoclist.getAddress() != null && searchLoclist.getAddress().contains(searchLoclist.getPlace())) {
            myViewHolder.tv_place.setText(searchLoclist.getPlace());
        } else {
            myViewHolder.tv_place.setText(searchLoclist.getAddress());
        }
        if (searchLoclist.getbSchedule() != null) {
            if (searchLoclist.getbSchedule().getTimespec().size() > 0) {
                myViewHolder.tv_working.setVisibility(View.VISIBLE);
            } else {
                myViewHolder.tv_working.setVisibility(View.GONE);
            }
        }
        if (mSearchLocationList.size() > 1) {
            if (position == 0) {
                myViewHolder.mLayouthide.setVisibility(View.VISIBLE);
                myViewHolder.img_arrow.setImageResource(R.drawable.icon_angle_up);
                searchLoclist.setExpandFlag(true);
                myViewHolder.LexpandCheckin.setVisibility(View.GONE);

            } else {
                myViewHolder.mLayouthide.setVisibility(View.GONE);
                myViewHolder.img_arrow.setImageResource(R.drawable.icon_angle_down);
                myViewHolder.LexpandCheckin.setVisibility(View.GONE);
            }
        } else {

            myViewHolder.img_arrow.setVisibility(View.INVISIBLE);
        }
        for (int i = 0; i < mQueueList.size(); i++) {
//            if (mQueueList.get(i).getNextAvailableQueue() != null) {
//                if (online_presence && mQueueList.get(i).getNextAvailableQueue().isWaitlistEnabled()) {
//                    if (mQueueList.get(i).getNextAvailableQueue().isOnlineCheckIn() || mSearchSetting.isFutureDateWaitlist()) {
//                        if (!mQueueList.get(i).getNextAvailableQueue().getCalculationMode().equalsIgnoreCase("NoCalc")) {
//                            myViewHolder.tv_waittime.setVisibility(View.VISIBLE);
//                            myViewHolder.txtwaittime_expand.setVisibility(View.VISIBLE);
//                        } else {
//                            if (mQueueList.get(i).isShowToken() && (mQueueList.get(i).getNextAvailableQueue().getCalculationMode().equalsIgnoreCase("NoCalc"))) {
//                                myViewHolder.tv_waittime.setVisibility(View.VISIBLE);
//                                myViewHolder.txtwaittime_expand.setVisibility(View.VISIBLE);
//                            } else {
//                                myViewHolder.tv_waittime.setVisibility(View.GONE);
//                                myViewHolder.txtwaittime_expand.setVisibility(View.GONE);
//                            }
//                        }
//                    } else {
//                        myViewHolder.tv_waittime.setVisibility(View.GONE);
//                        myViewHolder.txtwaittime_expand.setVisibility(View.GONE);
//                    }
//                }
//            }

            if (mQueueList.get(i).getNextAvailableQueue() != null) {
                if (online_presence && mQueueList.get(i).isWaitlistEnabled()) {
                    if (mQueueList.get(i).getNextAvailableQueue() != null && mQueueList.get(i).getNextAvailableQueue().getAvailableDate() != null && mSearchSetting.isFutureDateWaitlist()) {
//                        myViewHolder.txt_diffdate.setVisibility(View.VISIBLE);
//                        myViewHolder.txt_diffdate_expand.setVisibility(View.VISIBLE);
                    } else {
                        myViewHolder.txt_diffdate.setVisibility(View.GONE);
                        myViewHolder.txt_diffdate_expand.setVisibility(View.GONE);
                    }
                } else {
                    myViewHolder.txt_diffdate.setVisibility(View.GONE);
                    myViewHolder.txt_diffdate_expand.setVisibility(View.GONE);
                }
            } else {
                myViewHolder.txt_diffdate.setVisibility(View.GONE);
                myViewHolder.txt_diffdate_expand.setVisibility(View.GONE);
            }
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
                iCheckIn.putExtra("terminology", terminology);
                iCheckIn.putExtra("virtualservices", virtualServices);
                if (mQueueList.get(position).getNextAvailableQueue() != null) {
                    iCheckIn.putExtra("getAvail_date", mQueueList.get(position).getNextAvailableQueue().getAvailableDate());
                }
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
                iCheckIn.putExtra("terminology", terminology);
                iCheckIn.putExtra("virtualservices", virtualServices);
                if (mQueueList.get(position).getNextAvailableQueue() != null) {
                    iCheckIn.putExtra("getAvail_date", mQueueList.get(position).getNextAvailableQueue().getAvailableDate());
                }
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
                iCheckIn.putExtra("terminology", terminology);
                iCheckIn.putExtra("virtualservices", virtualServices);
                if (mQueueList.get(position).getNextAvailableQueue() != null) {
                    iCheckIn.putExtra("getAvail_date", mQueueList.get(position).getNextAvailableQueue().getAvailableDate());
                }
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
                iCheckIn.putExtra("terminology", terminology);
                iCheckIn.putExtra("virtualservices", virtualServices);
                if (mQueueList.get(position).getNextAvailableQueue() != null) {
                    iCheckIn.putExtra("getAvail_date", mQueueList.get(position).getNextAvailableQueue().getAvailableDate());
                }
                mContext.startActivity(iCheckIn);
            }
        });
        myViewHolder.btn_appointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iAppointment = new Intent(v.getContext(), Appointment.class);
                iAppointment.putExtra("serviceId", searchLoclist.getId());
                iAppointment.putExtra("uniqueID", mUniqueID);
                iAppointment.putExtra("accountID", accountID);
                iAppointment.putExtra("from", "searchdetail_checkin");
                iAppointment.putExtra("title", mTitle);
                iAppointment.putExtra("place", searchLoclist.getPlace());
                iAppointment.putExtra("googlemap", searchLoclist.getGoogleMapUrl());
                iAppointment.putExtra("sector", sector);
                iAppointment.putExtra("subsector", subsector);
                iAppointment.putExtra("terminology", terminology);
                if (mScheduleList.get(position).getAvailableSchedule() != null) {
                    iAppointment.putExtra("availableDate", mScheduleList.get(position).getAvailableSchedule().getAvailableDate());
                }
                iAppointment.putExtra("virtualservices", virtualServices);
                mContext.startActivity(iAppointment);
            }
        });

        myViewHolder.btn_donations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iDonation = new Intent(v.getContext(), Donation.class);
                iDonation.putExtra("serviceId", searchLoclist.getId());
                iDonation.putExtra("uniqueID", mUniqueID);
                iDonation.putExtra("accountID", accountID);
                iDonation.putExtra("from", "searchdetail_checkin");
                iDonation.putExtra("title", mTitle);
                iDonation.putExtra("place", searchLoclist.getPlace());
                iDonation.putExtra("googlemap", searchLoclist.getGoogleMapUrl());
                iDonation.putExtra("sector", sector);
                iDonation.putExtra("subsector", subsector);
                iDonation.putExtra("terminology", terminology);
                mContext.startActivity(iDonation);
            }
        });

        if (mSearchLocationList.size() > 1) {

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
                        myViewHolder.LexpandCheckin.setVisibility(View.GONE);

                    }
                }
            });
        }

        if (searchLoclist.getbSchedule() != null) {
            if (searchLoclist.getbSchedule().getTimespec().size() > 0) {
                myViewHolder.txtworking.setVisibility(View.GONE); // Management asked to hide working hours
                myViewHolder.LWorkinHrs.setVisibility(View.GONE);
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
                        myViewHolder.workingModelArrayList.add(work);
                        Config.logV("workingModelPassArrayList @@@@@@@@@@" + myViewHolder.workingModelArrayList.size());
                    }
                }
                myViewHolder.txtworkSeeAll.setVisibility(View.VISIBLE);
                myViewHolder.txtworkSeeAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Config.logV("workingModelPassArrayList" + myViewHolder.workingModelArrayList.size());
                        adaptercallback.onMethodWorkingCallback(myViewHolder.workingModelArrayList, mTitle);
                    }
                });
                for (int i = 0; i < myViewHolder.workingModelArrayList.size(); i++) {
                    if (myViewHolder.workingModelArrayList.get(i).getDay().equalsIgnoreCase("Monday")) {
                        if (myViewHolder.workingModelArrayList.get(i).getTime_value() != null) {
                            if (myViewHolder.txtdataMon.equalsIgnoreCase("")) {
                                myViewHolder.txtdataMon += myViewHolder.workingModelArrayList.get(i).getTime_value()/*+"\n"*/;
                            } else {
                                myViewHolder.txtdataMon += "\n" + myViewHolder.workingModelArrayList.get(i).getTime_value();
                            }
                        }
                    }
                    if (myViewHolder.workingModelArrayList.get(i).getDay().equalsIgnoreCase("Tuesday")) {
                        if (myViewHolder.workingModelArrayList.get(i).getTime_value() != null) {

                            if (myViewHolder.txtdataTue.equalsIgnoreCase("")) {
                                myViewHolder.txtdataTue += myViewHolder.workingModelArrayList.get(i).getTime_value()/*+"\n"*/;
                            } else {
                                myViewHolder.txtdataTue += "\n" + myViewHolder.workingModelArrayList.get(i).getTime_value();
                            }
                        }
                    }
                    if (myViewHolder.workingModelArrayList.get(i).getDay().equalsIgnoreCase("Wednesday")) {
                        if (myViewHolder.workingModelArrayList.get(i).getTime_value() != null) {
                            if (myViewHolder.txtdataWed.equalsIgnoreCase("")) {
                                myViewHolder.txtdataWed += myViewHolder.workingModelArrayList.get(i).getTime_value()/*+"\n"*/;
                            } else {
                                myViewHolder.txtdataWed += "\n" + myViewHolder.workingModelArrayList.get(i).getTime_value();
                            }
                        }
                        // txtdataWed += workingModelArrayList.get(i).getTime_value() + "\n";
                    }
                    if (myViewHolder.workingModelArrayList.get(i).getDay().equalsIgnoreCase("Thursday")) {
                        if (myViewHolder.workingModelArrayList.get(i).getTime_value() != null) {
                            if (myViewHolder.txtdataThu.equalsIgnoreCase("")) {
                                myViewHolder.txtdataThu += myViewHolder.workingModelArrayList.get(i).getTime_value()/*+"\n"*/;
                            } else {
                                myViewHolder.txtdataThu += "\n" + myViewHolder.workingModelArrayList.get(i).getTime_value();
                            }
                        }
                        // txtdataThu += workingModelArrayList.get(i).getTime_value() + "\n";
                    }
                    if (myViewHolder.workingModelArrayList.get(i).getDay().equalsIgnoreCase("Friday")) {
                        if (myViewHolder.workingModelArrayList.get(i).getTime_value() != null) {
                            if (myViewHolder.txtdataFri.equalsIgnoreCase("")) {
                                myViewHolder.txtdataFri += myViewHolder.workingModelArrayList.get(i).getTime_value()/*+"\n"*/;
                            } else {
                                myViewHolder.txtdataFri += "\n" + myViewHolder.workingModelArrayList.get(i).getTime_value();
                            }
                        }
                        //txtdataFri += workingModelArrayList.get(i).getTime_value() + "\n";
                    }
                    if (myViewHolder.workingModelArrayList.get(i).getDay().equalsIgnoreCase("Saturday")) {
                        if (myViewHolder.workingModelArrayList.get(i).getTime_value() != null) {
                            if (myViewHolder.txtdataSat.equalsIgnoreCase("")) {
                                myViewHolder.txtdataSat += myViewHolder.workingModelArrayList.get(i).getTime_value()/*+"\n"*/;
                            } else {
                                myViewHolder.txtdataSat += "\n" + myViewHolder.workingModelArrayList.get(i).getTime_value();
                            }
                            // txtdataSat += workingModelArrayList.get(i).getTime_value() + "\n";
                        }
                    }
                    if (myViewHolder.workingModelArrayList.get(i).getDay().equalsIgnoreCase("Sunday")) {
                        if (myViewHolder.workingModelArrayList.get(i).getTime_value() != null) {
                            if (myViewHolder.txtdataSun.equalsIgnoreCase("")) {
                                myViewHolder.txtdataSun += myViewHolder.workingModelArrayList.get(i).getTime_value()/*+"\n"*/;
                            } else {
                                myViewHolder.txtdataSun += "\n" + myViewHolder.workingModelArrayList.get(i).getTime_value();
                            }
                        }
                        //txtdataSun += workingModelArrayList.get(i).getTime_value() + "\n";
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                    Date d = new Date();
                    String dayOfTheWeek = sdf.format(d);
                    Config.logV("DAY OF WEEK ##################" + dayOfTheWeek);
                    if (dayOfTheWeek.equalsIgnoreCase("Monday")) {
                        myViewHolder.txtdayofweek.setVisibility(View.VISIBLE);
                        myViewHolder.txtdayofweek.setText("Monday - ");
                        if (!myViewHolder.txtdataMon.equalsIgnoreCase("")) {
                            myViewHolder.txtwork1.setVisibility(View.VISIBLE);
                            myViewHolder.txtwork1.setText(myViewHolder.txtdataMon);

                        } else {
                            myViewHolder.txtwork1.setVisibility(View.VISIBLE);
                            myViewHolder.txtwork1.setText("CLOSED");
                        }
                    }

                    if (dayOfTheWeek.equalsIgnoreCase("Tuesday")) {
                        myViewHolder.txtdayofweek.setVisibility(View.VISIBLE);
                        myViewHolder.txtdayofweek.setText("Tuesday - ");

                        if (!myViewHolder.txtdataTue.equalsIgnoreCase("")) {
                            myViewHolder.txtwork1.setVisibility(View.VISIBLE);
                            myViewHolder.txtwork1.setText(myViewHolder.txtdataTue);
                        } else {
                            myViewHolder.txtwork1.setVisibility(View.VISIBLE);
                            myViewHolder.txtwork1.setText("CLOSED");
                        }
                    }


                    if (dayOfTheWeek.equalsIgnoreCase("Wednesday")) {

                        myViewHolder.txtdayofweek.setVisibility(View.VISIBLE);
                        myViewHolder.txtdayofweek.setText("Wednesday - ");
                        if (!myViewHolder.txtdataWed.equalsIgnoreCase("")) {
                            myViewHolder.txtwork1.setVisibility(View.VISIBLE);
                            myViewHolder.txtwork1.setText(myViewHolder.txtdataWed);
                        } else {
                            myViewHolder.txtwork1.setVisibility(View.VISIBLE);
                            myViewHolder.txtwork1.setText("CLOSED");
                        }
                    }
                    if (dayOfTheWeek.equalsIgnoreCase("Thursday")) {

                        myViewHolder.txtdayofweek.setVisibility(View.VISIBLE);
                        myViewHolder.txtdayofweek.setText("Thursday - ");
                        if (!myViewHolder.txtdataThu.equalsIgnoreCase("")) {
                            myViewHolder.txtwork1.setVisibility(View.VISIBLE);
                            myViewHolder.txtwork1.setText(myViewHolder.txtdataThu);
                        } else {
                            myViewHolder.txtwork1.setVisibility(View.VISIBLE);
                            myViewHolder.txtwork1.setText("CLOSED");
                        }
                    }
                    if (dayOfTheWeek.equalsIgnoreCase("Friday")) {
                        myViewHolder.txtdayofweek.setVisibility(View.VISIBLE);
                        myViewHolder.txtdayofweek.setText("Friday - ");

                        if (!myViewHolder.txtdataFri.equalsIgnoreCase("")) {
                            myViewHolder.txtwork1.setVisibility(View.VISIBLE);
                            myViewHolder.txtwork1.setText(myViewHolder.txtdataFri);
                        } else {
                            myViewHolder.txtwork1.setVisibility(View.VISIBLE);
                            myViewHolder.txtwork1.setText("CLOSED");
                        }
                    }

                    if (dayOfTheWeek.equalsIgnoreCase("Saturday")) {

                        myViewHolder.txtdayofweek.setVisibility(View.VISIBLE);
                        myViewHolder.txtdayofweek.setText("Saturday - ");

                        if (!myViewHolder.txtdataSat.equalsIgnoreCase("")) {
                            myViewHolder.txtwork1.setVisibility(View.VISIBLE);
                            myViewHolder.txtwork1.setText(myViewHolder.txtdataSat);
                        } else {
                            myViewHolder.txtwork1.setVisibility(View.VISIBLE);
                            myViewHolder.txtwork1.setText("CLOSED");
                        }
                    }

                    if (dayOfTheWeek.equalsIgnoreCase("Sunday")) {

                        myViewHolder.txtdayofweek.setVisibility(View.VISIBLE);
                        myViewHolder.txtdayofweek.setText("Sunday - ");


                        if (!myViewHolder.txtdataSun.equalsIgnoreCase("")) {
                            myViewHolder.txtwork1.setVisibility(View.VISIBLE);
                            myViewHolder.txtwork1.setText(myViewHolder.txtdataSun);
                        } else {
                            myViewHolder.txtwork1.setVisibility(View.VISIBLE);
                            myViewHolder.txtwork1.setText("CLOSED");
                        }
                    }
                }
            } else {
                myViewHolder.txtworking.setVisibility(View.GONE);
                myViewHolder.LWorkinHrs.setVisibility(View.GONE);
            }
        }

        try {

            // Checkin Services

            ArrayList<SearchService> checkInServicesList = new ArrayList<>();
            for (int i = 0; i < mSearchServiceList.size(); i++) {

                if (searchLoclist.getId() == mSearchServiceList.get(i).getLocid()) {

                    checkInServicesList.addAll(mSearchServiceList.get(i).getmAllService());
                }
            }

            if (online_presence) {
                if (checkInServicesList.size() > 0) {

//                    myViewHolder.LService_2.setVisibility(View.VISIBLE);
//                    myViewHolder.txtservices.setVisibility(View.VISIBLE);
                    if (checkInServicesList.size() == 1) {

                        myViewHolder.txtservice1.setVisibility(View.VISIBLE);
                        myViewHolder.txtservice2.setVisibility(View.GONE);
                        myViewHolder.txtSeeAll.setVisibility(View.GONE);
                        String name = checkInServicesList.get(0).getName();
                        name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                        myViewHolder.txtservice1.setText(name);
                        try {
                            if (checkInServicesList.get(0).getServiceType().equalsIgnoreCase("virtualservice")) {
                                if (checkInServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                    myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized, 0, 0, 0);
                                    myViewHolder.txtservice1.setCompoundDrawablePadding(10);
                                } else if (checkInServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                    myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized, 0, 0, 0);
                                    myViewHolder.txtservice1.setCompoundDrawablePadding(10);
                                } else if (checkInServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                    myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized, 0, 0, 0);
                                    myViewHolder.txtservice1.setCompoundDrawablePadding(10);
                                } else if (checkInServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                    myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small, 0, 0, 0);
                                    myViewHolder.txtservice1.setCompoundDrawablePadding(10);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        myViewHolder.txtservice1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                serviceInfoDialog = new ServiceInfoDialog(v.getContext(), checkInServicesList.get(0), providerInfo);
                                serviceInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                serviceInfoDialog.show();
                                DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                                int width = (int) (metrics.widthPixels * 1);
                                serviceInfoDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                            }
                        });

//                Toast.makeText(mContext, "set single line", Toast.LENGTH_SHORT).show();
                    } else if (checkInServicesList.size() >= 2 && checkInServicesList.get(0).getName().length() <= 15 && checkInServicesList.get(1).getName().length() <= 15) {

                        if (checkInServicesList.size() == 2) {

                            myViewHolder.txtservice1.setVisibility(View.VISIBLE);
                            myViewHolder.txtservice2.setVisibility(View.VISIBLE);
                            myViewHolder.txtSeeAll.setVisibility(View.GONE);
                            String name1 = checkInServicesList.get(0).getName();
                            name1 = name1.substring(0, 1).toUpperCase() + name1.substring(1).toLowerCase();
                            myViewHolder.txtservice1.setText(name1 + ",");
                            try {
                                if (checkInServicesList.get(0).getServiceType().equalsIgnoreCase("virtualservice")) {
                                    if (checkInServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                        myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized, 0, 0, 0);
                                        myViewHolder.txtservice1.setCompoundDrawablePadding(10);
                                    } else if (checkInServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                        myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized, 0, 0, 0);
                                        myViewHolder.txtservice1.setCompoundDrawablePadding(10);
                                    } else if (checkInServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                        myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized, 0, 0, 0);
                                        myViewHolder.txtservice1.setCompoundDrawablePadding(10);
                                    } else if (checkInServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                        myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small, 0, 0, 0);
                                        myViewHolder.txtservice1.setCompoundDrawablePadding(10);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            String name2 = checkInServicesList.get(1).getName();
                            name2 = name2.substring(0, 1).toUpperCase() + name2.substring(1).toLowerCase();
                            myViewHolder.txtservice2.setText(name2);
                            try {
                                if (checkInServicesList.get(1).getServiceType().equalsIgnoreCase("virtualservice")) {
                                    if (checkInServicesList.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                        myViewHolder.txtservice2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized, 0, 0, 0);
                                        myViewHolder.txtservice2.setCompoundDrawablePadding(10);
                                    } else if (checkInServicesList.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                        myViewHolder.txtservice2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized, 0, 0, 0);
                                        myViewHolder.txtservice2.setCompoundDrawablePadding(10);
                                    } else if (checkInServicesList.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                        myViewHolder.txtservice2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized, 0, 0, 0);
                                        myViewHolder.txtservice2.setCompoundDrawablePadding(10);
                                    } else if (checkInServicesList.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                        myViewHolder.txtservice2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small, 0, 0, 0);
                                        myViewHolder.txtservice2.setCompoundDrawablePadding(10);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            myViewHolder.txtservice1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    serviceInfoDialog = new ServiceInfoDialog(v.getContext(), checkInServicesList.get(0), providerInfo);
                                    serviceInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    serviceInfoDialog.show();
                                    DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                                    int width = (int) (metrics.widthPixels * 1);
                                    serviceInfoDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                                }
                            });

                            myViewHolder.txtservice2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    serviceInfoDialog = new ServiceInfoDialog(v.getContext(), checkInServicesList.get(1), providerInfo);
                                    serviceInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    serviceInfoDialog.show();
                                    DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                                    int width = (int) (metrics.widthPixels * 1);
                                    serviceInfoDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                                }
                            });

                            // Toast.makeText(mContext, "set text with comma seperated without seemore", Toast.LENGTH_SHORT).show();
                        } else {
                            myViewHolder.txtservice1.setVisibility(View.VISIBLE);
                            myViewHolder.txtservice2.setVisibility(View.VISIBLE);
                            myViewHolder.txtSeeAll.setVisibility(View.VISIBLE);
                            String name1 = checkInServicesList.get(0).getName();
                            name1 = name1.substring(0, 1).toUpperCase() + name1.substring(1).toLowerCase();
                            myViewHolder.txtservice1.setText(name1 + ",");
                            try {
                                if (checkInServicesList.get(0).getServiceType().equalsIgnoreCase("virtualservice")) {
                                    if (checkInServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                        myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized, 0, 0, 0);
                                        myViewHolder.txtservice1.setCompoundDrawablePadding(10);
                                    } else if (checkInServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                        myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized, 0, 0, 0);
                                        myViewHolder.txtservice1.setCompoundDrawablePadding(10);
                                    } else if (checkInServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                        myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized, 0, 0, 0);
                                        myViewHolder.txtservice1.setCompoundDrawablePadding(10);
                                    } else if (checkInServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                        myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small, 0, 0, 0);
                                        myViewHolder.txtservice1.setCompoundDrawablePadding(10);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            String name2 = checkInServicesList.get(1).getName();
                            name2 = name2.substring(0, 1).toUpperCase() + name2.substring(1).toLowerCase();
                            myViewHolder.txtservice2.setText(name2 + ",");
                            try {
                                if (checkInServicesList.get(1).getServiceType().equalsIgnoreCase("virtualservice")) {
                                    if (checkInServicesList.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                        myViewHolder.txtservice2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized, 0, 0, 0);
                                        myViewHolder.txtservice2.setCompoundDrawablePadding(10);
                                    } else if (checkInServicesList.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                        myViewHolder.txtservice2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized, 0, 0, 0);
                                        myViewHolder.txtservice2.setCompoundDrawablePadding(10);
                                    } else if (checkInServicesList.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                        myViewHolder.txtservice2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized, 0, 0, 0);
                                        myViewHolder.txtservice2.setCompoundDrawablePadding(10);
                                    } else if (checkInServicesList.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                        myViewHolder.txtservice2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small, 0, 0, 0);
                                        myViewHolder.txtservice2.setCompoundDrawablePadding(10);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            myViewHolder.txtservice1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    serviceInfoDialog = new ServiceInfoDialog(v.getContext(), checkInServicesList.get(0), providerInfo);
                                    serviceInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    serviceInfoDialog.show();
                                    DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                                    int width = (int) (metrics.widthPixels * 1);
                                    serviceInfoDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                                }
                            });

                            myViewHolder.txtservice2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    serviceInfoDialog = new ServiceInfoDialog(v.getContext(), checkInServicesList.get(1), providerInfo);
                                    serviceInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    serviceInfoDialog.show();
                                    DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                                    int width = (int) (metrics.widthPixels * 1);
                                    serviceInfoDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                                }
                            });
                            myViewHolder.txtSeeAll.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    adaptercallback.onMethodServiceCallback(checkInServicesList, mTitle, mSearchDepartmentList);
                                }
                            });
                            //  Toast.makeText(mContext, "set text with comma seperated with seemore", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        for (int i = 0; i < checkInServicesList.size(); i++) {

                            if (i == 0) {

                                myViewHolder.txtservice1.setVisibility(View.VISIBLE);
                                myViewHolder.txtservice2.setVisibility(View.GONE);
                                String name1 = checkInServicesList.get(0).getName();
                                name1 = name1.substring(0, 1).toUpperCase() + name1.substring(1).toLowerCase();
                                myViewHolder.txtservice1.setText(name1 + ",");
                                try {
                                    if (checkInServicesList.get(0).getServiceType().equalsIgnoreCase("virtualservice")) {
                                        if (checkInServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                            myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized, 0, 0, 0);
                                            myViewHolder.txtservice1.setCompoundDrawablePadding(10);
                                        } else if (checkInServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                            myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized, 0, 0, 0);
                                        } else if (checkInServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                            myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized, 0, 0, 0);
                                            myViewHolder.txtservice1.setCompoundDrawablePadding(10);
                                        } else if (checkInServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                            myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small, 0, 0, 0);
                                            myViewHolder.txtservice1.setCompoundDrawablePadding(10);

                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                myViewHolder.txtservice1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        serviceInfoDialog = new ServiceInfoDialog(v.getContext(), checkInServicesList.get(0), providerInfo);
                                        serviceInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        serviceInfoDialog.show();
                                        DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                                        int width = (int) (metrics.widthPixels * 1);
                                        serviceInfoDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    }
                                });
                                myViewHolder.txtSeeAll.setVisibility(View.VISIBLE);
                                myViewHolder.txtSeeAll.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        adaptercallback.onMethodServiceCallback(checkInServicesList, mTitle, mSearchDepartmentList);
                                    }
                                });

                                // Toast.makeText(mContext, "set single line and see more", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (mScheduleList != null) {
                if (online_presence) {
                    if (mScheduleList.get(position).isApptEnabled()) {

                        if (mScheduleList.get(position).getAvailableSchedule() != null) {
                            myViewHolder.LAppointment.setVisibility(View.VISIBLE);
                            myViewHolder.tvAvailDate.setVisibility(View.VISIBLE);
                            myViewHolder.LApp_Services.setVisibility(View.VISIBLE);
                            myViewHolder.txt_apptservices.setVisibility(View.VISIBLE);
                            String avlDate = mScheduleList.get(position).getAvailableSchedule().getAvailableDate();
                            Date date = null;
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                date = format.parse(avlDate);
                                System.out.println(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String timeSlot = convertSlotTime(mScheduleList.get(position).getSlotsData().getAvailableSlots().get(0).getSlotTime().split("-")[0]);

                            if (date != null) {
                                if (DateUtils.isToday(date.getTime())) {
                                    myViewHolder.tvAvailDate.setText("Next Available time " + "\n" + "Today,  " + timeSlot);

                                } else {
                                    String availableDate = formatDate(mScheduleList.get(position).getAvailableSchedule().getAvailableDate());
                                    myViewHolder.tvAvailDate.setText("Next Available time " + "\n" + availableDate + ",  " + timeSlot);
                                }
                            }
                        }
                        else {

                            myViewHolder.LAppointment.setVisibility(View.VISIBLE);
                            myViewHolder.tvAvailDate.setVisibility(View.GONE);
                            myViewHolder.LApp_Services.setVisibility(View.GONE);
                            myViewHolder.txt_apptservices.setVisibility(View.GONE);
                        }

                    } else {
                        myViewHolder.LAppointment.setVisibility(View.GONE);
                        myViewHolder.LApp_Services.setVisibility(View.GONE);
                        myViewHolder.tvAvailDate.setVisibility(View.GONE);
                        myViewHolder.txt_apptservices.setVisibility(View.GONE);
                        myViewHolder.txtapptSeeAll.setVisibility(View.GONE);
                    }
                } else {
                    myViewHolder.LAppointment.setVisibility(View.GONE);
                    myViewHolder.LApp_Services.setVisibility(View.GONE);
                    myViewHolder.txt_apptservices.setVisibility(View.GONE);
                    myViewHolder.txtapptSeeAll.setVisibility(View.GONE);
                    myViewHolder.tvAvailDate.setVisibility(View.GONE);
                }

                ArrayList<SearchAppointmentDepartmentServices> apptServicesList = new ArrayList<>();
                for (int i = 0; i < aServicesList.size(); i++) {
                    if (aServicesList.get(i).getServices() != null) {
                        apptServicesList.addAll(aServicesList.get(i).getServices());
                    } else {

                        SearchAppointmentDepartmentServices objApptService = new SearchAppointmentDepartmentServices();
                        objApptService.setName(aServicesList.get(i).getName());
                        objApptService.setServiceDuration(aServicesList.get(i).getServiceDuration());
                        objApptService.setTotalAmount(aServicesList.get(i).getTotalAmount());
                        objApptService.setDescription(aServicesList.get(i).getDescription());
                        objApptService.setServicegallery(aServicesList.get(i).getServicegallery());
                        objApptService.setTaxable(aServicesList.get(i).isTaxable());
                        objApptService.setPrePayment(aServicesList.get(i).isPrePayment());
                        objApptService.setMinPrePaymentAmount(aServicesList.get(i).getMinPrePaymentAmount());
                        objApptService.setDepartmentName(aServicesList.get(i).getDepartmentName());
                        objApptService.setServiceType(aServicesList.get(i).getServiceType());
                        apptServicesList.add(objApptService);
                    }
                }

                if (apptServicesList.size() > 0) {
                    if (apptServicesList.size() == 1) {

                        myViewHolder.tvAppService1.setVisibility(View.VISIBLE);
                        myViewHolder.tvAppService2.setVisibility(View.GONE);
                        myViewHolder.tvAppSeeAll.setVisibility(View.GONE);
                        String name = apptServicesList.get(0).getName();
                        name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                        myViewHolder.tvAppService1.setText(name);
                        try {
                            if (apptServicesList.get(0).getServiceType() != null) {
                                if (apptServicesList.get(0).getServiceType().equalsIgnoreCase("virtualservice")) {
                                    if (apptServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                        myViewHolder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized, 0, 0, 0);
                                        myViewHolder.tvAppService1.setCompoundDrawablePadding(10);
                                    } else if (apptServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                        myViewHolder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized, 0, 0, 0);
                                        myViewHolder.tvAppService1.setCompoundDrawablePadding(10);
                                    } else if (apptServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                        myViewHolder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized, 0, 0, 0);
                                        myViewHolder.tvAppService1.setCompoundDrawablePadding(10);
                                    } else if (apptServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                        myViewHolder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small, 0, 0, 0);
                                        myViewHolder.tvAppService1.setCompoundDrawablePadding(10);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        myViewHolder.tvAppService1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                appServInfoDialog = new AppointmentServiceInfoDialog(v.getContext(), apptServicesList.get(0));
                                appServInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                appServInfoDialog.show();
                                DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                                int width = (int) (metrics.widthPixels * 1);
                                appServInfoDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

                            }
                        });


                    } else if (apptServicesList.size() >= 2 && apptServicesList.get(0).getName().length() <= 20 && apptServicesList.get(1).getName().length() <= 20) {

                        if (apptServicesList.size() == 2) {
                            myViewHolder.tvAppService1.setVisibility(View.VISIBLE);
                            myViewHolder.tvAppService2.setVisibility(View.VISIBLE);
                            myViewHolder.tvAppSeeAll.setVisibility(View.GONE);
                            String name1 = apptServicesList.get(0).getName();
                            name1 = name1.substring(0, 1).toUpperCase() + name1.substring(1).toLowerCase();
                            myViewHolder.tvAppService1.setText(name1 + ",");
                            try {
                                if (apptServicesList.get(0).getServiceType() != null) {
                                    if (apptServicesList.get(0).getServiceType().equalsIgnoreCase("virtualservice")) {
                                        if (apptServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                            myViewHolder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized, 0, 0, 0);
                                            myViewHolder.tvAppService1.setCompoundDrawablePadding(10);
                                        } else if (apptServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                            myViewHolder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized, 0, 0, 0);
                                            myViewHolder.tvAppService1.setCompoundDrawablePadding(10);
                                        } else if (apptServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                            myViewHolder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized, 0, 0, 0);
                                            myViewHolder.tvAppService1.setCompoundDrawablePadding(10);
                                        } else if (apptServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                            myViewHolder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small, 0, 0, 0);
                                            myViewHolder.tvAppService1.setCompoundDrawablePadding(10);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            String name2 = apptServicesList.get(1).getName();
                            name2 = name2.substring(0, 1).toUpperCase() + name2.substring(1).toLowerCase();
                            myViewHolder.tvAppService2.setText(name2);
                            try {
                                if (apptServicesList.get(1).getServiceType() != null) {
                                    if (apptServicesList.get(1).getServiceType().equalsIgnoreCase("virtualservice")) {
                                        if (apptServicesList.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                            myViewHolder.tvAppService2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized, 0, 0, 0);
                                            myViewHolder.tvAppService2.setCompoundDrawablePadding(10);
                                        } else if (apptServicesList.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                            myViewHolder.tvAppService2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized, 0, 0, 0);
                                            myViewHolder.tvAppService2.setCompoundDrawablePadding(10);
                                        } else if (apptServicesList.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                            myViewHolder.tvAppService2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized, 0, 0, 0);
                                            myViewHolder.tvAppService2.setCompoundDrawablePadding(10);
                                        } else if (apptServicesList.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                            myViewHolder.tvAppService2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small, 0, 0, 0);
                                            myViewHolder.tvAppService2.setCompoundDrawablePadding(10);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            myViewHolder.tvAppService1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    appServInfoDialog = new AppointmentServiceInfoDialog(v.getContext(), apptServicesList.get(0));
                                    appServInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    appServInfoDialog.show();
                                    DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                                    int width = (int) (metrics.widthPixels * 1);
                                    appServInfoDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                                }
                            });

                            myViewHolder.tvAppService2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    appServInfoDialog = new AppointmentServiceInfoDialog(v.getContext(), apptServicesList.get(1));
                                    appServInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    appServInfoDialog.show();
                                    DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                                    int width = (int) (metrics.widthPixels * 1);
                                    appServInfoDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

                                }
                            });

                        } else {
                            myViewHolder.tvAppService1.setVisibility(View.VISIBLE);
                            myViewHolder.tvAppService2.setVisibility(View.VISIBLE);
                            myViewHolder.tvAppSeeAll.setVisibility(View.VISIBLE);
                            String name1 = apptServicesList.get(0).getName();
                            name1 = name1.substring(0, 1).toUpperCase() + name1.substring(1).toLowerCase();
                            myViewHolder.tvAppService1.setText(name1 + ",");
                            try {
                                if (apptServicesList.get(0).getServiceType() != null) {

                                    if (apptServicesList.get(0).getServiceType().equalsIgnoreCase("virtualservice")) {
                                        if (apptServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                            myViewHolder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized, 0, 0, 0);
                                            myViewHolder.tvAppService1.setCompoundDrawablePadding(10);
                                        } else if (apptServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                            myViewHolder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized, 0, 0, 0);
                                            myViewHolder.tvAppService1.setCompoundDrawablePadding(10);
                                        } else if (apptServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                            myViewHolder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized, 0, 0, 0);
                                            myViewHolder.tvAppService1.setCompoundDrawablePadding(10);
                                        } else if (apptServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                            myViewHolder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small, 0, 0, 0);
                                            myViewHolder.tvAppService1.setCompoundDrawablePadding(10);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            String name2 = apptServicesList.get(1).getName();
                            name2 = name2.substring(0, 1).toUpperCase() + name2.substring(1).toLowerCase();
                            myViewHolder.tvAppService2.setText(name2 + ",");
                            myViewHolder.tvAppService2.setText(name2);
                            try {
                                if (apptServicesList.get(1).getServiceType() != null) {
                                    if (apptServicesList.get(1).getServiceType().equalsIgnoreCase("virtualservice")) {
                                        if (apptServicesList.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                            myViewHolder.tvAppService2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized, 0, 0, 0);
                                            myViewHolder.tvAppService2.setCompoundDrawablePadding(10);
                                        } else if (apptServicesList.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                            myViewHolder.tvAppService2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized, 0, 0, 0);
                                            myViewHolder.tvAppService2.setCompoundDrawablePadding(10);
                                        } else if (apptServicesList.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                            myViewHolder.tvAppService2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized, 0, 0, 0);
                                            myViewHolder.tvAppService2.setCompoundDrawablePadding(10);
                                        } else if (apptServicesList.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                            myViewHolder.tvAppService2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small, 0, 0, 0);
                                            myViewHolder.tvAppService2.setCompoundDrawablePadding(10);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            myViewHolder.tvAppService1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    appServInfoDialog = new AppointmentServiceInfoDialog(v.getContext(), apptServicesList.get(0));
                                    appServInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    appServInfoDialog.show();
                                    DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                                    int width = (int) (metrics.widthPixels * 1);
                                    appServInfoDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                                }
                            });

                            myViewHolder.tvAppService2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    appServInfoDialog = new AppointmentServiceInfoDialog(v.getContext(), apptServicesList.get(1));
                                    appServInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    appServInfoDialog.show();
                                    DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                                    int width = (int) (metrics.widthPixels * 1);
                                    appServInfoDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

                                }
                            });
                            myViewHolder.tvAppSeeAll.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    adaptercallback.onMethodServiceCallbackAppointment(apptServicesList, mTitle, mSearchDepartmentList);
                                }
                            });
                        }
                    } else {

                        for (int i = 0; i < apptServicesList.size(); i++) {

                            if (i == 0) {
                                myViewHolder.tvAppService1.setVisibility(View.VISIBLE);
                                myViewHolder.tvAppService2.setVisibility(View.GONE);
                                String name1 = apptServicesList.get(0).getName();
                                name1 = name1.substring(0, 1).toUpperCase() + name1.substring(1).toLowerCase();
                                myViewHolder.tvAppService1.setText(name1 + ",");
                                try {
                                    if (apptServicesList.get(0).getServiceType() != null) {
                                        if (apptServicesList.get(0).getServiceType().equalsIgnoreCase("virtualservice")) {
                                            if (apptServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                                myViewHolder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized, 0, 0, 0);
                                                myViewHolder.tvAppService1.setCompoundDrawablePadding(10);
                                            } else if (apptServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                                myViewHolder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized, 0, 0, 0);
                                                myViewHolder.tvAppService1.setCompoundDrawablePadding(10);
                                            } else if (apptServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                                myViewHolder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized, 0, 0, 0);
                                                myViewHolder.tvAppService1.setCompoundDrawablePadding(10);
                                            } else if (apptServicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                                myViewHolder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small, 0, 0, 0);
                                                myViewHolder.tvAppService1.setCompoundDrawablePadding(10);
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                myViewHolder.tvAppService1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        appServInfoDialog = new AppointmentServiceInfoDialog(v.getContext(), apptServicesList.get(0));
                                        appServInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        appServInfoDialog.show();
                                        DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                                        int width = (int) (metrics.widthPixels * 1);
                                        appServInfoDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

                                    }
                                });
                                myViewHolder.tvAppSeeAll.setVisibility(View.VISIBLE);
                                myViewHolder.tvAppSeeAll.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        adaptercallback.onMethodServiceCallbackAppointment(apptServicesList, mTitle, mSearchDepartmentList);

                                    }
                                });
//                                    Toast.makeText(mContext, "set single line and see more", Toast.LENGTH_SHORT).show();
                                break;
                            } else {

                                myViewHolder.txt_apptservices.setVisibility(View.GONE);
                                myViewHolder.LApp_Services.setVisibility(View.GONE);
                            }
                        }
                    }
//        for (int m = 0; m < aServicesList.size(); m++) {
//            if (aServicesList.get(m).getServices() != null) {
//                if (aServicesList.size() > 0) {
//                    myViewHolder.LApp_Services.removeAllViews();
//                    //  myViewHolder.LApp_Services.setVisibility(View.VISIBLE);
//                    int size = 0;
//                    if (aServicesList.size() == 1) {
//                        size = 1;
//                    } else {
//                        if (aServicesList.size() == 2)
//                            size = 2;
//                        else
//                            size = 3;
//                    }
//                    for (int i = 0; i < size; i++) {
//                        TextView dynaText = new TextView(mContext);
//                        tyface = Typeface.createFromAsset(mContext.getAssets(),
//                                "fonts/Montserrat_Regular.otf");
//                        dynaText.setTypeface(tyface);
//                        for (int j = 0; j < aServicesList.get(i).getServices().size(); j++) {
//                            dynaText.setText(aServicesList.get(i).getServices().get(j).getName() + " (" + aServicesList.get(i).getDepartmentName() + " )");
//                            dynaText.setTextSize(13);
//                            dynaText.setPadding(5, 0, 5, 0);
//                            dynaText.setTextColor(mContext.getResources().getColor(R.color.black));
//                            dynaText.setMaxLines(1);
//                        }
//                        if (size > 2) {
//                            dynaText.setEllipsize(TextUtils.TruncateAt.END);
//                            dynaText.setMaxEms(10);
//                        }
//                        final int finalI = i;
//                        int finalM = m;
//                        dynaText.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
////                                    for (int i = 0; i < aServicesList.size(); i++) {
////                                        for (int j = 0; j < aServicesList.get(i).getServices().size(); j++) {
////                                            if (aServicesList.get(i).getServices().get(j).getName().toLowerCase().equalsIgnoreCase(aServicesList.get(finalI).getServices().get(j).getName().toLowerCase())) {
////                                                Intent iService = new Intent(v.getContext(), SearchServiceActivity.class);
////                                                iService.putExtra("name", aServicesList.get(i).getServices().get(j).getName().toString());
////                                                iService.putExtra("duration", String.valueOf(aServicesList.get(i).getServices().get(j).getServiceDuration()));
////                                                iService.putExtra("price", String.valueOf(aServicesList.get(i).getServices().get(j).getTotalAmount()));
////                                                iService.putExtra("desc", aServicesList.get(i).getServices().get(j).getDescription());
////                                                iService.putExtra("servicegallery", aServicesList.get(i).getServices().get(j).getServicegallery());
////                                                iService.putExtra("taxable", aServicesList.get(i).getServices().get(j).isTaxable());
////                                                iService.putExtra("isPrePayment", aServicesList.get(i).getServices().get(j).isPrePayment());
////                                                iService.putExtra("MinPrePaymentAmount", String.valueOf(aServicesList.get(i).getServices().get(j).getMinPrePaymentAmount()));
////                                                iService.putExtra("from", "appt");
////                                                mContext.startActivity(iService);
////                                            }
////                                        }  //  ApiService(searchdetailList.getUniqueid(), serviceNames.get(finalI).toString(), searchdetailList.getTitle());
////                                    }
//                            }
//
//                        });
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                        params.setMargins(0, 0, 20, 0);
//
//                        dynaText.setLayoutParams(params);
//                        myViewHolder.LApp_Services.addView(dynaText);
//                    }
//                    if (size > 3) {
//                        TextView dynaText = new TextView(mContext);
//                        dynaText.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                // mAdapterCallback.onMethodServiceCallback(serviceNames, searchdetailList.getTitle(), searchdetailList.getUniqueid());
//                            }
//                        });
//                        dynaText.setGravity(Gravity.CENTER);
//                        dynaText.setTextColor(mContext.getResources().getColor(R.color.black));
//                        dynaText.setText(" ... ");
//                        myViewHolder.LApp_Services.addView(dynaText);
//                    }
//                } else {
//                    myViewHolder.LApp_Services.setVisibility(View.GONE);
//                    myViewHolder.txt_apptservices.setVisibility(View.GONE);
//                }
//            } else {
//                if (aServicesList.size() > 0) {
//                    myViewHolder.LApp_Services.removeAllViews();
//                    //  myViewHolder.LApp_Services.setVisibility(View.VISIBLE);
//                    int size = 0;
//                    if (aServicesList.size() == 1) {
//                        size = 1;
//                    } else {
//                        if (aServicesList.size() == 2)
//                            size = 2;
//                        else
//                            size = 3;
//                    }
//                    for (int i = 0; i < size; i++) {
//                        TextView dynaText = new TextView(mContext);
//                        tyface = Typeface.createFromAsset(mContext.getAssets(),
//                                "fonts/Montserrat_Regular.otf");
//                        dynaText.setTypeface(tyface);
//                        dynaText.setText(aServicesList.get(i).getName().toString());
//                        dynaText.setTextSize(13);
//                        dynaText.setPadding(5, 0, 5, 0);
//                        dynaText.setTextColor(mContext.getResources().getColor(R.color.black));
//                        // dynaText.setBackground(context.getResources().getDrawable(R.drawable.input_border_rounded_blue_bg));
//
//                        //  dynaText.setPaintFlags(dynaText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//
//                        dynaText.setMaxLines(1);
//                        if (size > 2) {
//                            dynaText.setEllipsize(TextUtils.TruncateAt.END);
//                            dynaText.setMaxEms(10);
//                        }
//                        final int finalI = i;
//                        dynaText.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                for (int i = 0; i < aServicesList.size(); i++) {
//                                    if (aServicesList.get(i).getName().toLowerCase().equalsIgnoreCase(aServicesList.get(finalI).getName().toLowerCase())) {
//                                        Intent iService = new Intent(v.getContext(), SearchServiceActivity.class);
//                                        iService.putExtra("name", aServicesList.get(i).getName().toString());
//                                        iService.putExtra("duration", String.valueOf(aServicesList.get(i).getServiceDuration()));
//                                        iService.putExtra("price", String.valueOf(aServicesList.get(i).getTotalAmount()));
//                                        iService.putExtra("desc", aServicesList.get(i).getDescription());
//                                        iService.putExtra("servicegallery", aServicesList.get(i).getServicegallery());
//                                        iService.putExtra("taxable", aServicesList.get(i).isTaxable());
//                                        iService.putExtra("isPrePayment", aServicesList.get(i).isPrePayment());
//                                        iService.putExtra("MinPrePaymentAmount", String.valueOf(aServicesList.get(i).getMinPrePaymentAmount()));
//                                        iService.putExtra("from", "appt");
//                                        mContext.startActivity(iService);
//                                    }
//                                }
//                                //  ApiService(searchdetailList.getUniqueid(), serviceNames.get(finalI).toString(), searchdetailList.getTitle());
//                            }
//                        });
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                        params.setMargins(0, 0, 20, 0);
//
//                        dynaText.setLayoutParams(params);
//                        myViewHolder.LApp_Services.addView(dynaText);
//                    }
//                    if (size > 3) {
//                        TextView dynaText = new TextView(mContext);
//                        dynaText.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                // mAdapterCallback.onMethodServiceCallback(serviceNames, searchdetailList.getTitle(), searchdetailList.getUniqueid());
//                            }
//                        });
//                        dynaText.setGravity(Gravity.CENTER);
//                        dynaText.setTextColor(mContext.getResources().getColor(R.color.black));
//                        dynaText.setText(" ... ");
//                        myViewHolder.LApp_Services.addView(dynaText);
//                    }
//                } else {
//                    myViewHolder.LApp_Services.setVisibility(View.GONE);
//                    myViewHolder.txt_apptservices.setVisibility(View.GONE);
//                }
//            }
//            if (aServicesList.size() > 3) {
//                final int finalI = m;
//                if (online_presence) {
//                    if (mScheduleList.get(position).isCheckinAllowed()) {
//                        myViewHolder.txtapptSeeAll.setVisibility(View.VISIBLE);
//                    }
//                }
//
//
//                myViewHolder.txtapptSeeAll.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        adaptercallback.onMethodServiceCallbackAppointment(aServicesList, mTitle, mSearchDepartmentList);
//                    }
//                });
//            } else {
//                myViewHolder.txtapptSeeAll.setVisibility(View.GONE);
//            }
//        }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            if (online_presence) {
                if (donationFundRaising) {

                    if (gServicesList.size() > 0) {
                        myViewHolder.tvDonationAmount.setText("Upto\n"+ "₹ "+ gServicesList.get(0).getMaxDonationAmount());
                        myViewHolder.LDonation.setVisibility(View.VISIBLE);
                        myViewHolder.tvDonationAmount.setVisibility(View.GONE);
                        myViewHolder.LDont_Services.setVisibility(View.VISIBLE);
                        myViewHolder.txt_dontservices.setVisibility(View.VISIBLE);

                        if (gServicesList.size() == 1) {

                            myViewHolder.tvDntService1.setVisibility(View.VISIBLE);
                            myViewHolder.tvDntService2.setVisibility(View.GONE);
                            myViewHolder.tvDntSeeAll.setVisibility(View.GONE);
                            String name = gServicesList.get(0).getName();
                            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                            myViewHolder.tvDntService1.setText(name);
                            myViewHolder.tvDntService1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    donationServiceDialog = new DonationServiceDialog(v.getContext(), gServicesList.get(0));
                                    donationServiceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    donationServiceDialog.show();
                                    DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                                    int width = (int) (metrics.widthPixels * 1);
                                    donationServiceDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

                                }
                            });

                        } else if (gServicesList.size() >= 2 && gServicesList.get(0).getName().length() <= 20 && gServicesList.get(1).getName().length() <= 20) {

                            if (gServicesList.size() == 2) {

                                myViewHolder.tvDntService1.setVisibility(View.VISIBLE);
                                myViewHolder.tvDntService2.setVisibility(View.VISIBLE);
                                myViewHolder.tvDntSeeAll.setVisibility(View.GONE);
                                String name1 = gServicesList.get(0).getName();
                                name1 = name1.substring(0, 1).toUpperCase() + name1.substring(1).toLowerCase();
                                myViewHolder.tvDntService1.setText(name1 + ",");
                                String name2 = gServicesList.get(1).getName();
                                name2 = name2.substring(0, 1).toUpperCase() + name2.substring(1).toLowerCase();
                                myViewHolder.tvDntService2.setText(name2);
                                myViewHolder.tvDntService1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        donationServiceDialog = new DonationServiceDialog(v.getContext(), gServicesList.get(0));
                                        donationServiceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        donationServiceDialog.show();
                                        DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                                        int width = (int) (metrics.widthPixels * 1);
                                        donationServiceDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    }
                                });

                                myViewHolder.tvDntService2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        donationServiceDialog = new DonationServiceDialog(v.getContext(), gServicesList.get(1));
                                        donationServiceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        donationServiceDialog.show();
                                        DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                                        int width = (int) (metrics.widthPixels * 1);
                                        donationServiceDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    }
                                });

                                // Toast.makeText(mContext, "set text with comma seperated without seemore", Toast.LENGTH_SHORT).show();

                            } else {
                                myViewHolder.tvDntService1.setVisibility(View.VISIBLE);
                                myViewHolder.tvDntService2.setVisibility(View.VISIBLE);
                                myViewHolder.tvDntSeeAll.setVisibility(View.VISIBLE);
                                String name1 = gServicesList.get(0).getName();
                                name1 = name1.substring(0, 1).toUpperCase() + name1.substring(1).toLowerCase();
                                myViewHolder.tvDntService1.setText(name1 + ",");
                                String name2 = gServicesList.get(1).getName();
                                name2 = name2.substring(0, 1).toUpperCase() + name2.substring(1).toLowerCase();
                                myViewHolder.tvDntService2.setText(name2 + ",");
                                myViewHolder.tvDntService1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        donationServiceDialog = new DonationServiceDialog(v.getContext(), gServicesList.get(0));
                                        donationServiceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        donationServiceDialog.show();
                                        DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                                        int width = (int) (metrics.widthPixels * 1);
                                        donationServiceDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    }
                                });

                                myViewHolder.tvDntService2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        donationServiceDialog = new DonationServiceDialog(v.getContext(), gServicesList.get(1));
                                        donationServiceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        donationServiceDialog.show();
                                        DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                                        int width = (int) (metrics.widthPixels * 1);
                                        donationServiceDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    }
                                });
                                myViewHolder.tvDntSeeAll.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        adaptercallback.onMethodServiceCallbackDonation(gServicesList, mTitle);
                                    }
                                });
                                //  Toast.makeText(mContext, "set text with comma seperated with seemore", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            for (int i = 0; i < gServicesList.size(); i++) {

                                if (i == 0) {

                                    myViewHolder.tvDntService1.setVisibility(View.VISIBLE);
                                    myViewHolder.tvDntService2.setVisibility(View.GONE);
                                    String name1 = gServicesList.get(0).getName();
                                    name1 = name1.substring(0, 1).toUpperCase() + name1.substring(1).toLowerCase();
                                    myViewHolder.tvDntService1.setText(name1 + ",");
                                    myViewHolder.tvDntService1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            donationServiceDialog = new DonationServiceDialog(v.getContext(), gServicesList.get(0));
                                            donationServiceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            donationServiceDialog.show();
                                            DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                                            int width = (int) (metrics.widthPixels * 1);
                                            donationServiceDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        }
                                    });
                                    myViewHolder.tvDntSeeAll.setVisibility(View.VISIBLE);
                                    myViewHolder.tvDntSeeAll.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            adaptercallback.onMethodServiceCallbackDonation(gServicesList, mTitle);
                                        }
                                    });

                                    // Toast.makeText(mContext, "set single line and see more", Toast.LENGTH_SHORT).show();
                                    break;
                                }

                            }

                        }
                    }

//                if (gServicesList.size() > 0) {
//                    myViewHolder.LDont_Services.removeAllViews();
//                    //  myViewHolder.LApp_Services.setVisibility(View.VISIBLE);
//                    int size = 0;
//                    if (gServicesList.size() == 1) {
//                        size = 1;
//                    } else {
//                        if (gServicesList.size() == 2)
//                            size = 2;
//                        else
//                            size = 3;
//                    }
//                    for (int i = 0; i < size; i++) {
//                        TextView dynaText = new TextView(mContext);
//                        tyface = Typeface.createFromAsset(mContext.getAssets(),
//                                "fonts/Montserrat_Regular.otf");
//                        dynaText.setTypeface(tyface);
//                        dynaText.setText(gServicesList.get(i).toString());
//                        dynaText.setTextSize(13);
//                        dynaText.setPadding(5, 0, 5, 0);
//                        dynaText.setTextColor(mContext.getResources().getColor(R.color.black));
//                        dynaText.setMaxLines(1);
//                        if (size > 2) {
//                            dynaText.setEllipsize(TextUtils.TruncateAt.END);
//                            dynaText.setMaxEms(10);
//                        }
//                        final int finalI = i;
//                        dynaText.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                //  ApiService(searchdetailList.getUniqueid(), serviceNames.get(finalI).toString(), searchdetailList.getTitle());
//                                for (int i = 0; i < gServicesList.size(); i++) {
//                                    if (gServicesList.get(i).toString().toLowerCase().equalsIgnoreCase(gServicesList.get(finalI).toString().toLowerCase())) {
//                                        Intent iService = new Intent(v.getContext(), SearchServiceActivity.class);
//                                        iService.putExtra("name", gServicesList.get(i).toString());
//                                        iService.putExtra("minamount", String.valueOf(gServicesList.get(i).getMinDonationAmount()));
//                                        iService.putExtra("maxamount", String.valueOf(gServicesList.get(i).getMaxDonationAmount()));
//                                        iService.putExtra("multiples", String.valueOf(gServicesList.get(i).getMultiples()));
//                                        iService.putExtra("servicegallery", gServicesList.get(i).getServicegallery());
//                                        iService.putExtra("from", "dnt");
//                                        mContext.startActivity(iService);
//                                    }
//                                }
//                            }
//                        });
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                        params.setMargins(0, 0, 20, 0);
//
//                        dynaText.setLayoutParams(params);
//                        myViewHolder.LDont_Services.addView(dynaText);
//                    }
//                    if (size > 3) {
//                        TextView dynaText = new TextView(mContext);
//                        dynaText.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                // adaptercallback.onMethodServiceCallbackDonation(serviceNames, mTitle);
//                            }
//                        });
//                        dynaText.setGravity(Gravity.CENTER);
//                        dynaText.setTextColor(mContext.getResources().getColor(R.color.black));
//                        dynaText.setText(" ... ");
//                        myViewHolder.LDont_Services.addView(dynaText);
//                    } else {
//                        myViewHolder.LDont_Services.setVisibility(View.VISIBLE);
//                        myViewHolder.txt_dontservices.setVisibility(View.VISIBLE);
//                    }
//                    if (gServicesList.size() > 3) {
//                        TextView dynaText = new TextView(mContext);
//                        myViewHolder.txtdntSeeAll.setVisibility(View.VISIBLE);
//                        myViewHolder.txtdntSeeAll.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                adaptercallback.onMethodServiceCallbackDonation(gServicesList, mTitle);
//                            }
//                        });
//                    } else {
//                        myViewHolder.txtdntSeeAll.setVisibility(View.GONE);
//                    }
//                }
                    else {
                        myViewHolder.LDonation.setVisibility(View.GONE);
                        myViewHolder.LDont_Services.setVisibility(View.GONE);
                        myViewHolder.txt_dontservices.setVisibility(View.GONE);
                        myViewHolder.txtdntSeeAll.setVisibility(View.GONE);
                    }

                } else {
                    myViewHolder.LDonation.setVisibility(View.GONE);
                    myViewHolder.LDont_Services.setVisibility(View.GONE);
                    myViewHolder.txt_dontservices.setVisibility(View.GONE);
                    myViewHolder.txtdntSeeAll.setVisibility(View.GONE);
                }
            } else {
                myViewHolder.LDonation.setVisibility(View.GONE);
                myViewHolder.LDont_Services.setVisibility(View.GONE);
                myViewHolder.txt_dontservices.setVisibility(View.GONE);
                myViewHolder.txtdntSeeAll.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c);
        System.out.println("Current time => " + formattedDate);
        if (mSearchSetting.getCalculationMode() != null) {
            if (!mSearchSetting.getCalculationMode().equalsIgnoreCase("NoCalc")) {
                mShowWaitTime = true;
            } else {
                for (int l = 0; l < mQueueList.size(); l++) {
                    if (mQueueList.get(l).getNextAvailableQueue() != null) {
                        if (mSearchSetting.getCalculationMode().equalsIgnoreCase("NoCalc") && mQueueList.get(l).getNextAvailableQueue().isShowToken()) {
                            mShowWaitTime = true;
                        } else {
                            mShowWaitTime = false;
                        }
                    }
                }
            }

            for (int i = 0; i < mQueueList.size(); i++) {
                if (mQueueList.get(i).getNextAvailableQueue() != null) {
                    Config.logV("1--" + searchLoclist.getId() + "  2--" + mQueueList.get(i).getNextAvailableQueue().getLocation().getId());
                    if (searchLoclist.getId() == mQueueList.get(i).getNextAvailableQueue().getLocation().getId()) {
                        //open Now
                        if (mQueueList.get(i).getNextAvailableQueue().isOpenNow()) {
                            myViewHolder.tv_open.setVisibility(View.GONE); // Management asked to hide open now
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
                        if (mSearchAwsResponse != null) {
                            if (mSearchAwsResponse.getHits() != null) {
                                if (mSearchAwsResponse.getHits().getHit() != null) {
                                    for (int k = 0; k < mSearchAwsResponse.getHits().getHit().size(); k++) {
                                        if (!mSearchAwsResponse.getHits().getHit().isEmpty()) {
                                            if (mSearchAwsResponse.getHits().getHit().get(k).getFields() != null && mSearchAwsResponse.getHits().getHit().get(k).getFields().getFuture_checkins() != null) {
                                                if (mSearchAwsResponse.getHits().getHit().get(k).getFields().getFuture_checkins().equals("1")) {
                                                    if (mQueueList.get(i).getNextAvailableQueue().isShowToken()) {
                                                        myViewHolder.txt_diffdate.setText("Do you want to Get Token for another day?");
                                                        myViewHolder.txt_diffdate_expand.setText("Do you want to Get Token for another day?");


                                                    } else {
                                                        myViewHolder.txt_diffdate.setText("Do you want to " + " " + terminology + " for another day?");
                                                        myViewHolder.txt_diffdate_expand.setText("Do you want to " + " " + terminology + " for another day?");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (online_presence && mQueueList.get(i).isWaitlistEnabled()) {
                            disableCheckinFeature(myViewHolder);
                            if (mQueueList.get(i).getNextAvailableQueue().isShowToken()) {
                                myViewHolder.btn_checkin.setText("GET TOKEN");
                                myViewHolder.btn_checkin_expand.setText("GET TOKEN");
                                myViewHolder.txtservices.setText("Token Services");
                            } else {
                                myViewHolder.btn_checkin.setText("Check-in".toUpperCase());
                                myViewHolder.txtservices.setText("CheckIn Services");
                                myViewHolder.btn_checkin_expand.setText("Check-in".toUpperCase());
                                myViewHolder.txtservices.setText("Check-in Services");
                            }
                            if (mQueueList.get(i).getNextAvailableQueue().getAvailableDate() != null) {
                                if (mQueueList.get(i).getNextAvailableQueue().isOnlineCheckIn() && mQueueList.get(i).getNextAvailableQueue().isAvailableToday() && formattedDate.equalsIgnoreCase(mQueueList.get(i).getNextAvailableQueue().getAvailableDate())) { //Today
                                    enableCheckinButton(myViewHolder);
                                    if (mQueueList.get(i).getNextAvailableQueue().isShowToken()) {
                                        myViewHolder.btn_checkin.setText("GET TOKEN");
                                        myViewHolder.btn_checkin_expand.setText("GET TOKEN");
                                        myViewHolder.txtservices.setText("Token Services");

                                        if (mQueueList.get(i).getNextAvailableQueue().getCalculationMode().equalsIgnoreCase("NoCalc")) { // NoCalc without show waiting time
                                            String message = Config.getPersonsAheadText(mQueueList.get(i).getNextAvailableQueue().getPersonAhead());
                                            myViewHolder.tv_waittime.setText(message);
                                            myViewHolder.txtwaittime_expand.setText(message);
                                            myViewHolder.tv_waittime.setVisibility(View.VISIBLE);
                                            myViewHolder.txtwaittime_expand.setVisibility(View.VISIBLE);
                                            myViewHolder.txt_peopleahead.setVisibility(View.GONE);
                                        } else { // Conventional (Token with Waiting time)
                                            myViewHolder.tv_waittime.setVisibility(View.VISIBLE);
                                            myViewHolder.txtwaittime_expand.setVisibility(View.VISIBLE);
                                            String spannable = getWaitingTime(mQueueList.get(i).getNextAvailableQueue());
                                            String waitTime = spannable.replace("\n","-");
                                            myViewHolder.txt_peopleahead.setText(waitTime);
                                            myViewHolder.txt_peopleahead.setVisibility(View.VISIBLE);
                                            String message = Config.getPersonsAheadText(mQueueList.get(i).getNextAvailableQueue().getPersonAhead());
                                            myViewHolder.tv_waittime.setText(message);
                                            myViewHolder.txtwaittime_expand.setText(message);
                                        }
                                    } else { // Conventional/Fixed
                                        myViewHolder.btn_checkin.setText("Check-in".toUpperCase());
                                        myViewHolder.btn_checkin_expand.setText("Check-in".toUpperCase());
                                        myViewHolder.txtservices.setText("Check-in Services");
                                        myViewHolder.tv_waittime.setVisibility(View.VISIBLE);
                                        myViewHolder.txtwaittime_expand.setVisibility(View.VISIBLE);
                                        String spannable = getWaitingTime(mQueueList.get(i).getNextAvailableQueue());
                                        myViewHolder.tv_waittime.setText(spannable);
                                        myViewHolder.txtwaittime_expand.setText(spannable);
                                        myViewHolder.txt_peopleahead.setVisibility(View.VISIBLE);
                                        String message = Config.getPersonsAheadText(mQueueList.get(i).getNextAvailableQueue().getPersonAhead());
                                        myViewHolder.txt_peopleahead.setText(message);
                                    }
                                } else {
                                    //  disableCheckinButton(myViewHolder);
                                    enableCheckinButton(myViewHolder);
                                }
                                if (date2 != null && date1.compareTo(date2) < 0) {
                                    if (mQueueList.get(i).getNextAvailableQueue().isShowToken()) {
                                        myViewHolder.btn_checkin.setText("GET TOKEN");
                                        myViewHolder.btn_checkin_expand.setText("GET TOKEN");
                                        myViewHolder.txtservices.setText("Token Services");

                                        if (mQueueList.get(i).getNextAvailableQueue().getCalculationMode().equalsIgnoreCase("NoCalc")) { // NoCalc without show waiting time
                                            String message = Config.getPersonsAheadText(mQueueList.get(i).getNextAvailableQueue().getPersonAhead());
                                            myViewHolder.tv_waittime.setText(message);
                                            myViewHolder.txtwaittime_expand.setText(message);
                                            myViewHolder.tv_waittime.setVisibility(View.VISIBLE);
                                            myViewHolder.txtwaittime_expand.setVisibility(View.VISIBLE);
                                            myViewHolder.txt_peopleahead.setVisibility(View.GONE);
                                        } else { // Conventional (Token with Waiting time)
                                            myViewHolder.tv_waittime.setVisibility(View.VISIBLE);
                                            myViewHolder.txtwaittime_expand.setVisibility(View.VISIBLE);
                                            String spannable = getWaitingTime(mQueueList.get(i).getNextAvailableQueue());
                                            String waitTime = spannable.replace("\n", "-");
                                            myViewHolder.txt_peopleahead.setText(waitTime);
                                            myViewHolder.txt_peopleahead.setVisibility(View.VISIBLE);
                                            String message = Config.getPersonsAheadText(mQueueList.get(i).getNextAvailableQueue().getPersonAhead());
                                            myViewHolder.tv_waittime.setText(message);
                                            myViewHolder.txtwaittime_expand.setText(message);
                                        }
                                    }
                                   else {
                                        myViewHolder.tv_waittime.setVisibility(View.VISIBLE);
                                        myViewHolder.txtwaittime_expand.setVisibility(View.VISIBLE);
                                        String spannable = getWaitingTime(mQueueList.get(i).getNextAvailableQueue());
                                        myViewHolder.tv_waittime.setText(spannable);
                                        myViewHolder.txtwaittime_expand.setText(spannable);
                                        myViewHolder.txt_peopleahead.setVisibility(View.VISIBLE);
                                        String message = Config.getPersonsAheadText(mQueueList.get(i).getNextAvailableQueue().getPersonAhead());
                                        myViewHolder.txt_peopleahead.setText(message);
                                    }

                                }
                                //Future Checkin
                                if (mSearchSetting.isFutureDateWaitlist() && mQueueList.get(i).getNextAvailableQueue().getAvailableDate() != null) {
//                                    myViewHolder.txt_diffdate.setVisibility(View.VISIBLE);
//                                    myViewHolder.txt_diffdate_expand.setVisibility(View.VISIBLE);
                                    if (mQueueList.get(i).getNextAvailableQueue().isShowToken()) {
                                        myViewHolder.txt_diffdate.setText("Do you want to Get Token for another day?");
                                        myViewHolder.txt_diffdate_expand.setText("Do you want to Get Token for another day?");
                                    } else {
                                        myViewHolder.txt_diffdate.setText("Do you want to" + " Check-in for another day?");
                                        myViewHolder.txt_diffdate_expand.setText("Do you want to Get Token for another day?");
                                    }
                                } else {
                                    myViewHolder.txt_diffdate.setVisibility(View.GONE);
                                    myViewHolder.txt_diffdate_expand.setVisibility(View.GONE);
                                }
                            }
                        } else {

//                            myViewHolder.LService_2.setVisibility(View.GONE);
//                            myViewHolder.txtservices.setVisibility(View.GONE);
                        }
                    }
                } else {

//                        myViewHolder.txtservices.setVisibility(View.GONE);
//                        myViewHolder.LService_2.setVisibility(View.GONE);
                }
            }

        }
    }


    public String convertSlotTime(String date) {
        final String OLD_FORMAT = "HH:mm";
        final String NEW_FORMAT = "hh:mm aa";

        String slotTime = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
            Date d = sdf.parse(date);
            sdf.applyPattern(NEW_FORMAT);
            slotTime = sdf.format(d);
        } catch (ParseException e) {
            // TODO: handle exception
        }
        String str = slotTime.replace("am", "AM").replace("pm","PM");
        return str;
    }


    private String formatDate(String availableDate) {

        String dtStart = availableDate;
        Date dateParse = null;
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dateParse = format1.parse(dtStart);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat df = new SimpleDateFormat("MMM dd");
        String nAvailDate = df.format(dateParse);

        return nAvailDate;
    }

    public static String getWaitingTime(NextAvailableQModel queue) {
        String firstWord = "";
        String secondWord = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date c = Calendar.getInstance().getTime();
        String formattedDate = df.format(c);
        System.out.println("Current time => " + formattedDate);
        Date date1 = null, date2 = null;
        try {
            date1 = df.parse(formattedDate);
            if (queue.getAvailableDate() != null)
                date2 = df.parse(queue.getAvailableDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String type = null;
        if (date2 != null && date1.compareTo(date2) < 0) {
            type = "future";
        }
        if (queue.getServiceTime() != null) {
            firstWord = "Next Available Time ";
            if (type != null) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date = null;
                try {
                    date = format.parse(queue.getAvailableDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String day = (String) DateFormat.format("dd", date);
                String monthString = (String) DateFormat.format("MMM", date);
//                Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
//                        "fonts/Montserrat_Bold.otf");
                secondWord = "\n " + monthString + " " + day + ", " + queue.getServiceTime();
//                String outputDateStr = outputFormat.format(datechange);
//                String yourDate = Config.getFormatedDate(outputDateStr);
//                secondWord = yourDate + ", " + queue.getServiceTime();
            } else {
                secondWord = "\n Today, " + queue.getServiceTime();
            }
        } else {
            firstWord = "Est wait time";
            secondWord = " \n " + Config.getTimeinHourMinutes(queue.getQueueWaitingTime());
        }
        // Spannable spannable = new SpannableString(firstWord + secondWord);
//        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),"fonts/Montserrat_Bold.otf");
//        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.title_grey)), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return firstWord + secondWord;
    }

    private void handleLocationAmenities(final MyViewHolder myViewHolder,
                                         final SearchLocation searchLoclist) {

        if (searchLoclist.getParkingType() != null) {
            if (searchLoclist.getParkingType().equalsIgnoreCase("free") || searchLoclist.getParkingType().equalsIgnoreCase("valet") || searchLoclist.getParkingType().equalsIgnoreCase("street") || searchLoclist.getParkingType().equalsIgnoreCase("privatelot") || searchLoclist.getParkingType().equalsIgnoreCase("paid")) {
                ParkingModel mType = new ParkingModel();
                mType.setId("1");
                mType.setTypename(Config.toTitleCase(searchLoclist.getParkingType()) + " Parking ");
                listType.add(mType);
            }
        }
        if (searchLoclist.getLocationVirtualFields() != null) {
            if (searchLoclist.getLocationVirtualFields().getDocambulance() != null) {
                if (searchLoclist.getLocationVirtualFields().getDocambulance().equalsIgnoreCase("true")) {
                    ParkingModel mType = new ParkingModel();
                    mType.setId("4");
                    mType.setTypename("Ambulance");
                    listType.add(mType);
                }
            }
            if (searchLoclist.getLocationVirtualFields().getFirstaid() != null) {
                if (searchLoclist.getLocationVirtualFields().getFirstaid().equalsIgnoreCase("true")) {
                    ParkingModel mType = new ParkingModel();
                    mType.setId("5");
                    mType.setTypename("First Aid");
                    listType.add(mType);
                }
            }
            if (searchLoclist.getLocationVirtualFields().getTraumacentre() != null) {
                if (searchLoclist.getLocationVirtualFields().getTraumacentre().equalsIgnoreCase("true")) {
                    ParkingModel mType = new ParkingModel();
                    mType.setId("7");
                    mType.setTypename("Trauma");
                    listType.add(mType);
                }
            }
            if (searchLoclist.getLocationVirtualFields().getPhysiciansemergencyservices() != null) {
                if (searchLoclist.getLocationVirtualFields().getPhysiciansemergencyservices().equalsIgnoreCase("true")) {
                    ParkingModel mType = new ParkingModel();
                    mType.setId("3");
                    mType.setTypename("Emergency");
                    listType.add(mType);
                }
            }
            if (searchLoclist.getLocationVirtualFields().getHosemergencyservices() != null) {
                if (searchLoclist.getLocationVirtualFields().getHosemergencyservices().equalsIgnoreCase("true")) {
                    ParkingModel mType = new ParkingModel();
                    mType.setId("3");
                    mType.setTypename("Emergency");
                    listType.add(mType);
                }
            }
            if (searchLoclist.getLocationVirtualFields().getDentistemergencyservices() != null) {
                if (searchLoclist.getLocationVirtualFields().getDentistemergencyservices().equalsIgnoreCase("true")) {
                    ParkingModel mType = new ParkingModel();
                    mType.setId("6");
                    mType.setTypename("Emergency");
                    listType.add(mType);
                }
            }
        }
        try {
            if (searchLoclist.isOpen24hours()) {
                ParkingModel mType = new ParkingModel();
                mType.setId("2");
                mType.setTypename("24 Hours");
                listType.add(mType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listType.size() > 0) {
            Config.logV("Location Ament---------------" + listType.size());

            myViewHolder.txtlocation_amentites.setVisibility(View.VISIBLE);
            myViewHolder.txtlocation_amentites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    locationAmenitiesDialog = new LocationAmenitiesDialog(mContext, listType);
                    locationAmenitiesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    locationAmenitiesDialog.show();
                    DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                    int width = (int) (metrics.widthPixels * 1);
                    locationAmenitiesDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

                }
            });

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
                    int size = listType.size();
                    if (size == 1) {
                        size = 1;
                    } else {
                        size = 2;
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
//            if (listType.size() > 2) {
//                myViewHolder.txtparkingSeeAll.setVisibility(View.VISIBLE);
//            } else {
//                myViewHolder.txtparkingSeeAll.setVisibility(View.GONE);
//            }
//            myViewHolder.txtlocation_amentites.setVisibility(View.VISIBLE);
//            myViewHolder.recycle_parking.setVisibility(View.VISIBLE);
//            int size = listType.size();
//            if (size == 1) {
//                size = 1;
//            } else {
//                size = 2;
//            }
//            ParkingTypesAdapter mParkTypeAdapter = new ParkingTypesAdapter(listType, mContext, size);
//            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
//            myViewHolder.recycle_parking.setLayoutManager(horizontalLayoutManager);
//            myViewHolder.recycle_parking.setAdapter(mParkTypeAdapter);
//        } else {
//            myViewHolder.txtparkingSeeAll.setVisibility(View.GONE);
//            myViewHolder.txtlocation_amentites.setVisibility(View.GONE);
//            myViewHolder.recycle_parking.setVisibility(View.GONE);
//        }
//        myViewHolder.txtparkingSeeAll.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!searchLoclist.isLocationAmentOpen()) {
//                    ParkingTypesAdapter mParkTypeAdapter = new ParkingTypesAdapter(listType, mContext, listType.size());
//                    LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
//                    myViewHolder.recycle_parking.setLayoutManager(horizontalLayoutManager);
//                    myViewHolder.recycle_parking.setAdapter(mParkTypeAdapter);
//                    myViewHolder.txtparkingSeeAll.setText("See Less");
//                    searchLoclist.setLocationAmentOpen(true);
//                } else {
//                    int size = listType.size();
//                    if (size == 1) {
//                        size = 1;
//                    } else {
//                        size = 2;
//                    }
//                    ParkingTypesAdapter mParkTypeAdapter = new ParkingTypesAdapter(listType, mContext, size);
//                    LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
//                    myViewHolder.recycle_parking.setLayoutManager(horizontalLayoutManager);
//                    myViewHolder.recycle_parking.setAdapter(mParkTypeAdapter);
//                    myViewHolder.txtparkingSeeAll.setText("See All");
//                    searchLoclist.setLocationAmentOpen(false);
//                }
//            }
//        });
    }

    public void disableCheckinButton(MyViewHolder myViewHolder) {
        myViewHolder.btn_checkin.setBackgroundColor(Color.parseColor("#cfcfcf"));
        myViewHolder.btn_checkin_expand.setBackgroundColor(Color.parseColor("#cfcfcf"));
        myViewHolder.btn_checkin.setTextColor(mContext.getResources().getColor(R.color.button_grey));
        myViewHolder.btn_checkin_expand.setTextColor(mContext.getResources().getColor(R.color.button_grey));
        myViewHolder.btn_checkin.setEnabled(false);
        myViewHolder.btn_checkin_expand.setEnabled(false);
        myViewHolder.btn_checkin.setVisibility(View.VISIBLE);
        myViewHolder.btn_checkin_expand.setVisibility(View.VISIBLE);
    }

    public void disableCheckinFeature(MyViewHolder myViewHolder) {
        disableCheckinButton(myViewHolder);
        myViewHolder.btn_checkin.setVisibility(View.GONE);
        myViewHolder.btn_checkin_expand.setVisibility(View.GONE);
//        myViewHolder.LService_2.setVisibility(View.VISIBLE);
//        myViewHolder.txtservices.setVisibility(View.VISIBLE);
    }

    public void enableCheckinButton(MyViewHolder myViewHolder) {
        myViewHolder.btn_checkin.setBackgroundColor(mContext.getResources().getColor(R.color.dark_blue));
        myViewHolder.btn_checkin.setTextColor(mContext.getResources().getColor(R.color.white));
        myViewHolder.btn_checkin.setEnabled(true);
        myViewHolder.btn_checkin.setVisibility(View.VISIBLE);
        myViewHolder.btn_checkin_expand.setBackgroundColor(mContext.getResources().getColor(R.color.green));
        myViewHolder.btn_checkin_expand.setTextColor(mContext.getResources().getColor(R.color.white));
        myViewHolder.btn_checkin_expand.setEnabled(true);
        myViewHolder.btn_checkin_expand.setVisibility(View.VISIBLE);
        myViewHolder.LService_2.setVisibility(View.VISIBLE);
        myViewHolder.txtservices.setVisibility(View.VISIBLE);

    }

    private String getDepartmentName(int department) {
        if (mSearchDepartmentList != null) {
            for (int i = 0; i < mSearchDepartmentList.size(); i++) {
                if (mSearchDepartmentList.get(i).getDepartmentId() == department) {
                    return mSearchDepartmentList.get(i).getDepartmentName();
                }
            }
        }
        return "";
    }


    @Override
    public int getItemCount() {
        return mSearchLocationList.size();
    }
}

