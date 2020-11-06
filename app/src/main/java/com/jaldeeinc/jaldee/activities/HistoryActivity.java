package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jaldeeinc.jaldee.Interface.ISelectedBooking;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.TodayBookingsAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.Bookings;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;

public class HistoryActivity extends AppCompatActivity implements ISelectedBooking {

    RecyclerView rvHistory;
    CardView cvBack;
    LinearLayout llNoBookings;
    ArrayList<Bookings> bookingsList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    private ISelectedBooking iSelectedBooking;
    private TodayBookingsAdapter todayBookingsAdapter;
    List<ActiveCheckIn> allCheckInsOffline = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        iSelectedBooking = this;
        initializations();

        linearLayoutManager = new LinearLayoutManager(HistoryActivity.this);
        rvHistory.setLayoutManager(linearLayoutManager);
        todayBookingsAdapter = new TodayBookingsAdapter(bookingsList, HistoryActivity.this, true, iSelectedBooking);
        rvHistory.setAdapter(todayBookingsAdapter);

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        try {
            if (Config.isOnline(HistoryActivity.this)) {
                apiGetAllBookings();
            } else {
                setOfflineBookings();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void initializations() {

        cvBack = findViewById(R.id.cv_back);
        rvHistory = findViewById(R.id.rv_history);
        llNoBookings = findViewById(R.id.ll_noBookings);
    }

    private void apiGetAllBookings() {

        try {

            ApiInterface apiService =
                    ApiClient.getClient(HistoryActivity.this).create(ApiInterface.class);

            List<Observable<?>> requests = new ArrayList<>();
            Map<String, String> appointFilter = new HashMap<String, String>();
            Map<String, String> checkInFilter = new HashMap<String, String>();
            appointFilter.put("apptStatus-neq", "failed,prepaymentPending");
            checkInFilter.put("waitlistStatus-neq", "failed,prepaymentPending");

            // Make a collection of all requests you need to call at once, there can be any number of requests, not only 3. You can have 2 or 5, or 100.
            requests.add(apiService.getHistoryAppointments(appointFilter));
            requests.add(apiService.getHistoryCheckIns(checkInFilter));
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

                    DatabaseHandler db = new DatabaseHandler(HistoryActivity.this);
                    db.DeleteMyCheckin("old");
                    db.insertMyCheckinInfo(checkInList);

                    bookingsList.clear();


                    Set<String> dates = new HashSet<String>();
                    for (ActiveAppointment activeAppoint : appntList) {

                        dates.add(activeAppoint.getAppmtDate());

                    }

                    for (ActiveCheckIn aCheckIn : checkInList) {

                        dates.add(aCheckIn.getDate());

                    }

                    for (String distinctDate : dates) {

                        for (ActiveAppointment activeAppointment : appntList) {

                            if (activeAppointment.getAppmtDate().equalsIgnoreCase(distinctDate)) {

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

                                    // no need to show status of history bookings , so setting as null
                                    if (activeAppointment.getService().getServiceType().equalsIgnoreCase("virtualService") && activeAppointment.getApptStatus().equalsIgnoreCase(Constants.ARRIVED)) {
                                        bookingInfo.setBookingStatus(null);
                                    } else {
                                        bookingInfo.setBookingStatus(null);
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
                                        if (activeAppointment.getService().getVirtualCallingModes().size() > 0) {

                                            bookingInfo.setCallingType(activeAppointment.getService().getVirtualCallingModes().get(0).getCallingMode());
                                        }
                                    }
                                }

                                bookings.add(bookingInfo);
                            }
                        }

                        for (ActiveCheckIn activeCheckIn : checkInList) {

                            if (activeCheckIn.getDate().equalsIgnoreCase(distinctDate)) {

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

                                    // no need to show status of history bookings , so setting as null
                                    if (activeCheckIn.getService().getServiceType().equalsIgnoreCase("virtualService") && activeCheckIn.getWaitlistStatus().equalsIgnoreCase(Constants.ARRIVED)) {
                                        bookingInfo.setBookingStatus(null);
                                    } else {
                                        bookingInfo.setBookingStatus(null);
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
                                        if (activeCheckIn.getService().getVirtualCallingModes().size() > 0) {
                                            bookingInfo.setCallingType(activeCheckIn.getService().getVirtualCallingModes().get(0).getCallingMode());
                                        }
                                    }
                                }

                                bookings.add(bookingInfo);
                            }
                        }

                    }

                    bookingsList.addAll(bookings);

                    Collections.sort(bookingsList, new Comparator<Bookings>() {
                        @Override
                        public int compare(Bookings p, Bookings q) {
                            Date pDate = null, qDate = null;
                            try {
                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                pDate = df.parse(p.getBookingOn());
                                qDate = df.parse(q.getBookingOn());

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (pDate.before(qDate)) {
                                return -1;
                            } else if (pDate.after(qDate)) {
                                return 1;
                            } else {
                                return 0;
                            }
                        }
                    }.reversed());


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

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            if (allBookings != null && allBookings.size() > 0) {

                                                llNoBookings.setVisibility(View.GONE);
                                                rvHistory.setVisibility(View.VISIBLE);
                                                rvHistory.setLayoutManager(linearLayoutManager);
                                                todayBookingsAdapter = new TodayBookingsAdapter(allBookings, HistoryActivity.this, false, iSelectedBooking);
                                                rvHistory.setAdapter(todayBookingsAdapter);
                                            } else {

                                                rvHistory.setVisibility(View.GONE);
                                                llNoBookings.setVisibility(View.VISIBLE);
                                            }

                                        }
                                    });
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

        } catch (
                Exception e) {
            e.printStackTrace();
        }

    }


    private void setOfflineBookings() {

        try {

            allCheckInsOffline.clear();
            bookingsList.clear();
            DatabaseHandler db = new DatabaseHandler(HistoryActivity.this);
            allCheckInsOffline = db.getMyCheckinList("old");
            ArrayList<Bookings> bookings = new ArrayList<Bookings>();

            Set<String> dates = new HashSet<String>();
            for (ActiveCheckIn aCheckIn : allCheckInsOffline) {
                dates.add(aCheckIn.getDate());
            }
            for (String distinctDate : dates) {

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

            }

            bookingsList.addAll(bookings);

            Collections.sort(bookingsList, new Comparator<Bookings>() {
                @Override
                public int compare(Bookings p, Bookings q) {
                    Date pDate = null, qDate = null;
                    try {
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        pDate = df.parse(p.getBookingOn());
                        qDate = df.parse(q.getBookingOn());

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (pDate.before(qDate)) {
                        return -1;
                    } else if (pDate.after(qDate)) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }.reversed());

            setOfflineBookingsToAdapter(bookingsList);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void setOfflineBookingsToAdapter(ArrayList<Bookings> bookingsList) {

        if (bookingsList != null && bookingsList.size() > 0) {

            llNoBookings.setVisibility(View.GONE);
            rvHistory.setVisibility(View.VISIBLE);
            rvHistory.setLayoutManager(linearLayoutManager);
            todayBookingsAdapter = new TodayBookingsAdapter(bookingsList, HistoryActivity.this, false, iSelectedBooking);
            rvHistory.setAdapter(todayBookingsAdapter);
        } else {

            rvHistory.setVisibility(View.GONE);
            llNoBookings.setVisibility(View.VISIBLE);
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
    public void sendBookingInfo(Bookings bookings) {

        if (bookings != null) {

            if (bookings.getBookingType().equalsIgnoreCase(Constants.APPOINTMENT)) {

                Intent intent = new Intent(HistoryActivity.this, BookingDetails.class);
                intent.putExtra("bookingInfo", bookings);
                intent.putExtra("isActive", false);
                startActivity(intent);

            } else if (bookings.getBookingType().equalsIgnoreCase(Constants.CHECKIN) || bookings.getBookingType().equalsIgnoreCase(Constants.TOKEN)) {

                Intent intent = new Intent(HistoryActivity.this, CheckInDetails.class);
                intent.putExtra("bookingInfo", bookings);
                intent.putExtra("isActive", false);
                startActivity(intent);


            }
        }

    }

    @Override
    public void sendSelectedBookingActions(Bookings bookings) {

    }
}