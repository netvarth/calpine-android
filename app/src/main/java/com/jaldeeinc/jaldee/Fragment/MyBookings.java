package com.jaldeeinc.jaldee.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.jaldeeinc.jaldee.Interface.ISelectedBooking;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.BookingDetails;
import com.jaldeeinc.jaldee.activities.CheckInDetails;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.activities.Home;
import com.jaldeeinc.jaldee.adapter.TodayBookingsAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.ActionsDialog;
import com.jaldeeinc.jaldee.custom.CustomTextViewItalicSemiBold;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.Bookings;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;


public class MyBookings extends RootFragment implements ISelectedBooking {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private Context mContext;
    private Activity mActivity;
    private CustomTextViewItalicSemiBold tvToday, tvUpcoming;
    private LinearLayout llNoBookingsForToday, llNoBookingsForFuture, llNoBookings, llBookings;
    private RecyclerView rvTodays, rvUpcomings;
    private TodayBookingsAdapter todayBookingsAdapter;
    private LinearLayoutManager linearLayoutManager, futureLayoutManager;
    private ISelectedBooking iSelectedBooking;
    ArrayList<ActiveAppointment> mAppointmentTodayList = new ArrayList<>();
    ArrayList<ActiveAppointment> mAppointmentFutureList = new ArrayList<>();
    ArrayList<Bookings> bookingsList = new ArrayList<>();
    List<ActiveCheckIn> allCheckInsOffline = new ArrayList<>();
    Animation slideUp, slideRight;
    private ActionsDialog actionsDialog;
    boolean hideMoreInfo = false;


    public MyBookings() {
        // Required empty public constructor
    }


    public static MyBookings newInstance(String param1, String param2) {
        MyBookings fragment = new MyBookings();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onResume() {

        try {
            if (Config.isOnline(mContext)) {
                apiGetAllBookings();
            } else {
                setOfflineBookings();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_my_bookings, container, false);
        mContext = getActivity();
        iSelectedBooking = (ISelectedBooking) this;
        Home.doubleBackToExitPressedOnce = false;
        initializations(view);

        linearLayoutManager = new LinearLayoutManager(getContext());
        futureLayoutManager = new LinearLayoutManager(getContext());
        rvTodays.setLayoutManager(linearLayoutManager);
        todayBookingsAdapter = new TodayBookingsAdapter(bookingsList, getContext(), true, iSelectedBooking, hideMoreInfo);
        rvTodays.setAdapter(todayBookingsAdapter);



        return view;
    }


    private void initializations(View view) {

        rvTodays = view.findViewById(R.id.rv_todays);
        rvUpcomings = view.findViewById(R.id.rv_upcoming);
        llBookings = view.findViewById(R.id.ll_bookings);
        llNoBookings = view.findViewById(R.id.ll_noBookings);
        llNoBookingsForFuture = view.findViewById(R.id.ll_noFutureBookings);
        llNoBookingsForToday = view.findViewById(R.id.ll_noTodayBookings);
        tvToday = view.findViewById(R.id.tv_today);
        tvUpcoming = view.findViewById(R.id.tv_upcoming);
        slideUp = AnimationUtils.loadAnimation(mContext, R.anim.slide_up_in);
        slideRight = AnimationUtils.loadAnimation(mContext, R.anim.slide_up_out);

    }


    private void apiGetAllBookings() {

        try {

            ApiInterface apiService =
                    ApiClient.getClient(getContext()).create(ApiInterface.class);

            List<Observable<?>> requests = new ArrayList<>();
            Map<String, String> appointFilter = new HashMap<String, String>();
            Map<String, String> checkInFilter = new HashMap<String, String>();
            appointFilter.put("apptStatus-neq", "failed,prepaymentPending");
            checkInFilter.put("waitlistStatus-neq", "failed,prepaymentPending");

            // Make a collection of all requests you need to call at once, there can be any number of requests, not only 3. You can have 2 or 5, or 100.
            requests.add(apiService.getAppointments(appointFilter));
            requests.add(apiService.getCheckIns(checkInFilter));
            // Zip all requests with the Function, which will receive the results.
            Observable.zip(requests, new Function<Object[], Object>() {
                @Override
                public Object apply(Object[] objects) throws Exception {
                    // Objects[] is an array of combined results of completed requests
                    ArrayList<ActiveAppointment> appntList = (ArrayList<ActiveAppointment>) objects[0];
                    ArrayList<ActiveCheckIn> checkInList = (ArrayList<ActiveCheckIn>) objects[1];

                    appntList = appntList == null ? new ArrayList<ActiveAppointment>() : appntList;

                    checkInList = checkInList == null ? new ArrayList<ActiveCheckIn>() : checkInList;

                    ArrayList<Bookings> bookings = new ArrayList<Bookings>();

                    DatabaseHandler db = new DatabaseHandler(mContext);
                    db.DeleteMyCheckin("today");
                    db.DeleteMyCheckin("future");
                    db.insertMyCheckinInfo(checkInList);

                    bookingsList.clear();

                    for (ActiveAppointment activeAppointment : appntList) {

                        Bookings bookingInfo = new Bookings();
                        bookingInfo.setBookingId(activeAppointment.getUid());
                        bookingInfo.setBookingType(Constants.APPOINTMENT);
                        bookingInfo.setAppointmentInfo(activeAppointment);
                        if (activeAppointment.getProviderAccount() != null) {
                            bookingInfo.setSpName(activeAppointment.getProviderAccount().getBusinessName());
                        }
                        if (activeAppointment.getProvider() != null) {  // to get businessName of firstName & lastName
                            if (activeAppointment.getProvider().getBusinessName() != null) {
                                bookingInfo.setProviderName(activeAppointment.getProvider().getBusinessName());
                            } else {
                                bookingInfo.setProviderName(activeAppointment.getProvider().getFirstName() + " " + activeAppointment.getProvider().getLastName());
                            }
                        }

                        if (activeAppointment.getService() != null && activeAppointment.getApptStatus() != null) {
                            bookingInfo.setServiceName(activeAppointment.getService().getName());
                            if (activeAppointment.getService().getServiceType().equalsIgnoreCase("virtualService")) { //  check if it is a virtual service
                                bookingInfo.setVirtual(true);
                            }

                            if (activeAppointment.getService().getServiceType().equalsIgnoreCase("virtualService") && activeAppointment.getApptStatus().equalsIgnoreCase(Constants.ARRIVED)) {
                                bookingInfo.setBookingStatus(null);
                            } else {
                                bookingInfo.setBookingStatus(activeAppointment.getApptStatus());
                            }
                        }

                        if (activeAppointment.getAppmtDate() != null && activeAppointment.getAppmtTime() != null) { //  to set time and date

                            String date = getCustomDateString(activeAppointment.getAppmtDate());
                            String time = convertTime(activeAppointment.getAppmtTime().split("-")[0]);
                            bookingInfo.setDate(date + " " + time);
                            bookingInfo.setBookingOn(activeAppointment.getAppmtDate()); // to check if it is today's or future's
                        }

                        if (activeAppointment.getService() != null) {

                            if (activeAppointment.getService().getVirtualServiceType() != null) {

                                if (activeAppointment.getService().getVirtualServiceType().equalsIgnoreCase("videoService")) {
                                    bookingInfo.setVideoService(true);
                                } else {
                                    bookingInfo.setVideoService(false);
                                }
                            }

                            if (activeAppointment.getService().getVirtualCallingModes() != null) {

                                bookingInfo.setCallingType(activeAppointment.getService().getVirtualCallingModes().get(0).getCallingMode());
                            }
                        }

                        bookings.add(bookingInfo);
                    }


                    for (ActiveCheckIn activeCheckIn : checkInList) {

                        Bookings bookingInfo = new Bookings();
                        bookingInfo.setBookingId(activeCheckIn.getYnwUuid());
                        if (activeCheckIn.getShowToken().equalsIgnoreCase("true")) {
                            bookingInfo.setBookingType(Constants.TOKEN);
                        } else {
                            bookingInfo.setBookingType(Constants.CHECKIN);
                        }
                        bookingInfo.setCheckInInfo(activeCheckIn);
                        bookingInfo.setWaitingTime(activeCheckIn.getAppxWaitingTime());
                        bookingInfo.setTokenNo(activeCheckIn.getToken());
                        if (activeCheckIn.getCalculationMode() != null) {
                            bookingInfo.setCalculationMode(activeCheckIn.getCalculationMode());
                        }
                        if (activeCheckIn.getServiceTime() != null) {
                            bookingInfo.setServiceTime(activeCheckIn.getServiceTime());
                        }
                        if (activeCheckIn.getProviderAccount() != null) {
                            bookingInfo.setSpName(activeCheckIn.getProviderAccount().getBusinessName());
                        }

                        if (activeCheckIn.getProvider() != null) {  // to get businessName of firstName & lastName
                            if (activeCheckIn.getProvider().getBusinessName() != null) {
                                bookingInfo.setProviderName(activeCheckIn.getProvider().getBusinessName());
                            } else {
                                bookingInfo.setProviderName(activeCheckIn.getProvider().getFirstName() + " " + activeCheckIn.getProvider().getLastName());
                            }
                        }

                        if (activeCheckIn.getService() != null && activeCheckIn.getWaitlistStatus() != null) {
                            bookingInfo.setServiceName(activeCheckIn.getService().getName());
                            if (activeCheckIn.getService().getServiceType().equalsIgnoreCase("virtualService")) { //  check if it is a virtual service
                                bookingInfo.setVirtual(true);
                            }

                            if (activeCheckIn.getService().getServiceType().equalsIgnoreCase("virtualService") && activeCheckIn.getWaitlistStatus().equalsIgnoreCase(Constants.ARRIVED)) {
                                bookingInfo.setBookingStatus(null);
                            } else {
                                bookingInfo.setBookingStatus(activeCheckIn.getWaitlistStatus());
                            }
                        }

                        if (activeCheckIn.getDate() != null && activeCheckIn.getQueue() != null) {

                            String date = getCustomDateString(activeCheckIn.getDate());
                            String time = activeCheckIn.getQueue().getQueueStartTime() + " - " + activeCheckIn.getQueue().getQueueEndTime();
                            bookingInfo.setDate(date + " " + time);
                            bookingInfo.setBookingOn(activeCheckIn.getDate());
                        }

                        if (activeCheckIn.getService() != null) {

                            if (activeCheckIn.getService().getVirtualServiceType() != null) {

                                if (activeCheckIn.getService().getVirtualServiceType().equalsIgnoreCase("videoService")) {
                                    bookingInfo.setVideoService(true);
                                } else {
                                    bookingInfo.setVideoService(false);
                                }
                            }

                            if (activeCheckIn.getService().getVirtualCallingModes() != null) {

                                bookingInfo.setCallingType(activeCheckIn.getService().getVirtualCallingModes().get(0).getCallingMode());
                            }
                        }

                        bookings.add(bookingInfo);
                    }

                    bookingsList.addAll(bookings);
                    //do something with those results and emit new event
                    return bookingsList;
                }
            })
                    // After all requests had been performed the next observer will receive the Object, returned from Function

                    .subscribe(
                            // Will be triggered if all requests will end successfully (4xx and 5xx also are successful requests too)
                            new Consumer<Object>() {
                                @Override
                                public void accept(Object object) throws Exception {
                                    //Do something on successful completion of all requests
                                    ArrayList<Bookings> allBookings = (ArrayList<Bookings>) object;

                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                ArrayList<Bookings> todayBookings = new ArrayList<>();
                                                ArrayList<Bookings> futureBookings = new ArrayList<>();

                                                if (allBookings != null && allBookings.size() > 0) {
                                                    llBookings.setVisibility(View.VISIBLE);
                                                    llNoBookings.setVisibility(View.GONE);
                                                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                                    for (int i = 0; i < allBookings.size(); i++) {

                                                        if (date.equalsIgnoreCase(allBookings.get(i).getBookingOn())) {

                                                            todayBookings.add(allBookings.get(i));
                                                        } else {
                                                            futureBookings.add(allBookings.get(i));
                                                        }
                                                    }

                                                    if (todayBookings.size() > 0) {

                                                        tvToday.setVisibility(View.VISIBLE);
                                                        llNoBookingsForToday.setVisibility(View.GONE);
                                                        rvTodays.setVisibility(View.VISIBLE);
                                                        rvTodays.setLayoutManager(linearLayoutManager);
                                                        todayBookingsAdapter = new TodayBookingsAdapter(todayBookings, getContext(), false, iSelectedBooking, hideMoreInfo);
                                                        rvTodays.setAdapter(todayBookingsAdapter);
                                                    } else {
                                                        tvToday.setVisibility(View.GONE);
                                                        rvTodays.setVisibility(View.GONE);
                                                        llNoBookingsForToday.setVisibility(View.GONE);
                                                    }

                                                    if (futureBookings.size() > 0) {

                                                        tvUpcoming.setVisibility(View.VISIBLE);
                                                        llNoBookingsForFuture.setVisibility(View.GONE);
                                                        rvUpcomings.setVisibility(View.VISIBLE);
                                                        rvUpcomings.setLayoutManager(futureLayoutManager);
                                                        todayBookingsAdapter = new TodayBookingsAdapter(futureBookings, getContext(), false, iSelectedBooking, hideMoreInfo);
                                                        rvUpcomings.setAdapter(todayBookingsAdapter);
                                                    } else {

                                                        tvUpcoming.setVisibility(View.GONE);
                                                        rvUpcomings.setVisibility(View.GONE);
                                                        llNoBookingsForFuture.setVisibility(View.GONE);
                                                    }

                                                } else {

                                                    // hide all
                                                    llNoBookings.setVisibility(View.VISIBLE);
                                                    llBookings.setVisibility(View.GONE);
                                                }

                                            }
                                        });
                                    }
                                }
                            },

                            // Will be triggered if any error during requests will happen
                            new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable e) throws Exception {
                                    Log.e("ListOf Calls", "1");

                                    //Do something on error completion of requests
                                }
                            }
                    );

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void setOfflineBookings() {

        try {

            allCheckInsOffline.clear();
            bookingsList.clear();
            DatabaseHandler db = new DatabaseHandler(mContext);
            List<ActiveCheckIn> todayCheckInsOffline = new ArrayList<>();
            List<ActiveCheckIn> futureCheckInsOffline = new ArrayList<>();
            todayCheckInsOffline = db.getMyCheckinList("today");
            futureCheckInsOffline = db.getMyCheckinList("future");
            allCheckInsOffline.addAll(todayCheckInsOffline);
            allCheckInsOffline.addAll(futureCheckInsOffline);
            ArrayList<Bookings> bookings = new ArrayList<Bookings>();
            for (ActiveCheckIn activeCheckIn : allCheckInsOffline) {

                Bookings bookingInfo = new Bookings();
                bookingInfo.setBookingId(activeCheckIn.getYnwUuid());
                if (activeCheckIn.getShowToken() != null && activeCheckIn.getShowToken().equalsIgnoreCase("true")) {
                    bookingInfo.setBookingType(Constants.TOKEN);
                } else {
                    bookingInfo.setBookingType(Constants.CHECKIN);
                }
                bookingInfo.setCheckInInfo(activeCheckIn);
                bookingInfo.setWaitingTime(activeCheckIn.getAppxWaitingTime());
                bookingInfo.setTokenNo(activeCheckIn.getToken());
                if (activeCheckIn.getCalculationMode() != null) {
                    bookingInfo.setCalculationMode(activeCheckIn.getCalculationMode());
                }
                if (activeCheckIn.getServiceTime() != null) {
                    bookingInfo.setServiceTime(activeCheckIn.getServiceTime());
                }
                if (activeCheckIn.getBusinessName() != null) {
                    bookingInfo.setSpName(activeCheckIn.getBusinessName());
                }

                if (activeCheckIn.getProvider() != null) {  // to get businessName of firstName & lastName
                    bookingInfo.setProviderName(activeCheckIn.getProvider().getFirstName() + " " + activeCheckIn.getProvider().getLastName());
                }

                if (activeCheckIn.getName() != null) {
                    bookingInfo.setServiceName(activeCheckIn.getName());
                }

                if (activeCheckIn.getService() != null && activeCheckIn.getWaitlistStatus() != null) {
                    if (activeCheckIn.getService().getServiceType().equalsIgnoreCase("virtualService")) { //  check if it is a virtual service
                        bookingInfo.setVirtual(true);
                    }

                    if (activeCheckIn.getService().getServiceType().equalsIgnoreCase("virtualService") && activeCheckIn.getWaitlistStatus().equalsIgnoreCase(Constants.ARRIVED)) {
                        bookingInfo.setBookingStatus(null);
                    } else {
                        bookingInfo.setBookingStatus(activeCheckIn.getWaitlistStatus());
                    }
                }

                if (activeCheckIn.getDate() != null && activeCheckIn.getQueueStartTime() != null && activeCheckIn.getQueueEndTime() != null) {

                    String date = getCustomDateString(activeCheckIn.getDate());
                    String time = activeCheckIn.getQueueStartTime() + " - " + activeCheckIn.getQueueEndTime();
                    bookingInfo.setDate(date + " " + time);
                    bookingInfo.setBookingOn(activeCheckIn.getDate());
                }

                if (activeCheckIn.getService() != null) {

                    if (activeCheckIn.getService().getVirtualCallingModes() != null) {

                        bookingInfo.setCallingType(activeCheckIn.getService().getVirtualCallingModes().get(0).getCallingMode());
                    }
                }

                bookings.add(bookingInfo);
            }

            bookingsList.addAll(bookings);

            setOfflineBookingsToAdapter(bookingsList);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void setOfflineBookingsToAdapter(ArrayList<Bookings> offlineBookingsList) {

        ArrayList<Bookings> todayBookings = new ArrayList<>();
        ArrayList<Bookings> futureBookings = new ArrayList<>();
        if (offlineBookingsList != null && offlineBookingsList.size() > 0) {
            llBookings.setVisibility(View.VISIBLE);
            llNoBookings.setVisibility(View.GONE);
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            for (int i = 0; i < offlineBookingsList.size(); i++) {

                if (date.equalsIgnoreCase(offlineBookingsList.get(i).getBookingOn())) {

                    todayBookings.add(offlineBookingsList.get(i));
                } else {
                    futureBookings.add(offlineBookingsList.get(i));
                }
            }

            if (todayBookings.size() > 0) {

                llNoBookingsForToday.setVisibility(View.GONE);
                rvTodays.setVisibility(View.VISIBLE);
                rvTodays.setLayoutManager(linearLayoutManager);
                todayBookingsAdapter = new TodayBookingsAdapter(todayBookings, getContext(), false, iSelectedBooking, hideMoreInfo);
                rvTodays.setAdapter(todayBookingsAdapter);
            } else {
                rvTodays.setVisibility(View.GONE);
                llNoBookingsForToday.setVisibility(View.GONE);
            }

            if (futureBookings.size() > 0) {

                llNoBookingsForFuture.setVisibility(View.GONE);
                rvUpcomings.setVisibility(View.VISIBLE);
                rvUpcomings.setLayoutManager(futureLayoutManager);
                todayBookingsAdapter = new TodayBookingsAdapter(futureBookings, getContext(), false, iSelectedBooking, hideMoreInfo);
                rvUpcomings.setAdapter(todayBookingsAdapter);
            } else {

                rvUpcomings.setVisibility(View.GONE);
                llNoBookingsForFuture.setVisibility(View.GONE);
            }

        } else {

            // hide all
            llNoBookings.setVisibility(View.VISIBLE);
            llBookings.setVisibility(View.GONE);

        }
    }


    public static String getCustomDateString(String d) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = format.parse(d);
        String date = format.format(date1);

        if (date.endsWith("1") && !date.endsWith("11"))
            format = new SimpleDateFormat("d'st' MMM, yyyy");

        else if (date.endsWith("2") && !date.endsWith("12"))
            format = new SimpleDateFormat("d'nd' MMM, yyyy");

        else if (date.endsWith("3") && !date.endsWith("13"))
            format = new SimpleDateFormat("d'rd' MMM, yyyy");

        else
            format = new SimpleDateFormat("d'th' MMM, yyyy");

        String yourDate = format.format(date1);

        return yourDate;
    }

    public static String convertTime(String time) {

        String formattedTime = "";
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            final Date dateObj = sdf.parse(time);
            time = new SimpleDateFormat("hh:mm aa").format(dateObj);
            formattedTime = time.replace("am", "AM").replace("pm", "PM");

        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return formattedTime;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void sendBookingInfo(Bookings bookings) {

        if (bookings != null) {

            if (bookings.getBookingType().equalsIgnoreCase(Constants.APPOINTMENT)) {

                Intent intent = new Intent(mContext, BookingDetails.class);
                intent.putExtra("bookingInfo", bookings);
                if (!bookings.getBookingStatus().equalsIgnoreCase("Cancelled") && !bookings.getBookingStatus().equalsIgnoreCase("Completed")) {
                    intent.putExtra("isActive", true);
                } else {
                    intent.putExtra("isActive", false);
                }
                startActivity(intent);
            } else if (bookings.getBookingType().equalsIgnoreCase(Constants.CHECKIN) || bookings.getBookingType().equalsIgnoreCase(Constants.TOKEN)) {

                Intent intent = new Intent(mContext, CheckInDetails.class);
                intent.putExtra("bookingInfo", bookings);
                if (!bookings.getBookingStatus().equalsIgnoreCase("Cancelled")) {
                    intent.putExtra("isActive", true);
                } else {
                    intent.putExtra("isActive", false);
                }
                startActivity(intent);

            }

        }

    }

    @Override
    public void sendSelectedBookingActions(Bookings bookings) {

        boolean isActive = false;
        if (bookings != null && bookings.getBookingStatus() != null){
            if (!bookings.getBookingStatus().equalsIgnoreCase("Cancelled") && !bookings.getBookingStatus().equalsIgnoreCase("done")){
                isActive = true;
            }
        }
        actionsDialog = new ActionsDialog(mContext,isActive,bookings);
        actionsDialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
        actionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actionsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        actionsDialog.show();
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 1);
        actionsDialog.getWindow().setGravity(Gravity.BOTTOM);
        actionsDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

    }
}