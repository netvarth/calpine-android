package com.jaldeeinc.jaldee.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jaldeeinc.jaldee.Interface.ISelectedBooking;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.activities.UserDetailActivity;
import com.jaldeeinc.jaldee.adapter.TodayBookingsAdapter;
import com.jaldeeinc.jaldee.adapter.UserServicesAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.model.Bookings;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.DepServiceInfo;
import com.jaldeeinc.jaldee.response.QueueList;
import com.jaldeeinc.jaldee.response.ScheduleList;
import com.jaldeeinc.jaldee.response.SearchAppoinment;
import com.jaldeeinc.jaldee.response.SearchService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyBookings extends Fragment implements ISelectedBooking {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private Context mContext;
    private Activity mActivity;
    private RecyclerView rvTodays, rvUpcomings;
    private TodayBookingsAdapter todayBookingsAdapter;
    private LinearLayoutManager linearLayoutManager,futureLayoutManager;
    private ISelectedBooking iSelectedBooking;
    ArrayList<ActiveAppointment> mAppointmentTodayList = new ArrayList<>();
    ArrayList<ActiveAppointment> mAppointmentFutureList = new ArrayList<>();
    ArrayList<Bookings> bookingsList = new ArrayList<>();

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_my_bookings, container, false);
        iSelectedBooking = (ISelectedBooking) this;

        initializations(view);

        linearLayoutManager = new LinearLayoutManager(getContext());
        futureLayoutManager = new LinearLayoutManager(getContext());
        rvTodays.setLayoutManager(linearLayoutManager);
        todayBookingsAdapter = new TodayBookingsAdapter(bookingsList, getContext(), true, iSelectedBooking);
        rvTodays.setAdapter(todayBookingsAdapter);

        apiGetAllBookings();

        return view;
    }

    private void initializations(View view) {

        rvTodays = view.findViewById(R.id.rv_todays);
        rvUpcomings = view.findViewById(R.id.rv_upcoming);
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

                        if (activeAppointment.getService() != null) {
                            bookingInfo.setServiceName(activeAppointment.getService().getName());
                            if (activeAppointment.getService().getServiceType().equalsIgnoreCase("virtualService")) { //  check if it is a virtual service
                                bookingInfo.setVirtual(true);
                            }
                        }

                        if (activeAppointment.getAppmtDate() != null && activeAppointment.getAppmtTime() != null) { //  to set time and date

                            String date = getCustomDateString(activeAppointment.getAppmtDate());
                            String time = convertTime(activeAppointment.getAppmtTime().split("-")[0]);
                            bookingInfo.setDate(date + " " + time);
                            bookingInfo.setBookingOn(activeAppointment.getAppmtDate()); // to check if it is today's or future's
                        }

                        if (activeAppointment.getApptStatus() != null) { //  to set status
                            bookingInfo.setBookingStatus(activeAppointment.getApptStatus());
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

                        if (activeCheckIn.getService() != null) {
                            bookingInfo.setServiceName(activeCheckIn.getService().getName());
                            if (activeCheckIn.getService().getServiceType().equalsIgnoreCase("virtualService")) { //  check if it is a virtual service
                                bookingInfo.setVirtual(true);
                            }
                        }

                        if (activeCheckIn.getDate() != null && activeCheckIn.getQueue() != null) {

                            String date = getCustomDateString(activeCheckIn.getDate());
                            String time = activeCheckIn.getQueue().getQueueStartTime() + " - " + activeCheckIn.getQueue().getQueueEndTime();
                            bookingInfo.setDate(date + " " + time);
                            bookingInfo.setBookingOn(date);
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
                                                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                                    for (int i = 0; i < allBookings.size(); i++) {

                                                        if (date.equalsIgnoreCase(allBookings.get(i).getDate())){

                                                            todayBookings.add(allBookings.get(i));
                                                        }
                                                        else {
                                                            futureBookings.add(allBookings.get(i));
                                                        }
                                                    }

                                                    if (todayBookings.size() >0){

                                                        rvTodays.setVisibility(View.VISIBLE);
                                                        rvTodays.setLayoutManager(linearLayoutManager);
                                                        todayBookingsAdapter = new TodayBookingsAdapter(todayBookings, getContext(), false, iSelectedBooking);
                                                        rvTodays.setAdapter(todayBookingsAdapter);
                                                    }

                                                    if (futureBookings.size()>0){

                                                        rvUpcomings.setVisibility(View.VISIBLE);
                                                        rvUpcomings.setLayoutManager(futureLayoutManager);
                                                        todayBookingsAdapter = new TodayBookingsAdapter(futureBookings, getContext(), false, iSelectedBooking);
                                                        rvUpcomings.setAdapter(todayBookingsAdapter);
                                                    }

                                                } else {

                                                    // hide all
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
}