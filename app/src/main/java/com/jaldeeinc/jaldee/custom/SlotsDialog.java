package com.jaldeeinc.jaldee.custom;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Interface.ISelectSlotInterface;
import com.jaldeeinc.jaldee.Interface.ISlotInfo;
import com.jaldeeinc.jaldee.Interface.OnBottomReachedListener;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.TimeSlotsAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.model.SelectedSlotDetail;
import com.jaldeeinc.jaldee.response.AvailableSlotsData;
import com.jaldeeinc.jaldee.response.SlotsData;
import com.jaldeeinc.jaldee.singlerowcalendar.DatePickerTimeline;
import com.jaldeeinc.jaldee.singlerowcalendar.OnDateSelectedListener;
import com.jaldeeinc.jaldee.utils.DateUtils1;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SlotsDialog extends LinearLayout implements ISelectSlotInterface, OnBottomReachedListener {

    private Context context;
    public String selectedCalanderDate, selectedDate;
    private RecyclerView rvSlots;
    private LinearLayout llNoSlots;
    final Calendar myCalendar = Calendar.getInstance();
    private int  serviceId, locationId, providerId;
    private ISlotInfo iSlotInfo;
    ArrayList<AvailableSlotsData> activeSlotsList = new ArrayList<>();
    ArrayList<AvailableSlotsData> availableSlots = new ArrayList<>();
    ArrayList<SlotsData> slotsData = new ArrayList<SlotsData>();
    TimeSlotsAdapter sAdapter;
    private LinearLayout llSeeMoreHint;
    private ISelectSlotInterface iSelectSlotInterface;
    private String defaultDate;
    private String displayTime = "", slotTime = "";
    private int scheduleId;
    private OnBottomReachedListener onBottomReachedListener;
    int maxBookingsAllowed;
    List<SelectedSlotDetail> selectedSlotDetails = new ArrayList<>();
    boolean showOnlyAvailableSlots;

    TextView tvSelectedCalendarDate;
    TextView tvSelectedCalendarDay;
    Button btn_calendar;
    Date sDate;
    DatePickerTimeline datePickerTimeline;
    Calendar calendarDate;

    public SlotsDialog(Context context, int serviceId, int locationId, ISlotInfo iSlotInfo, int providerId, String availableDate, int maxBookingsAllowed, boolean showOnlyAvailableSlots) {
        super(context);
        this.context = context;
        this.serviceId = serviceId;
        this.locationId = locationId;
        this.iSlotInfo = iSlotInfo;
        this.providerId = providerId;
        this.defaultDate = availableDate;
        this.maxBookingsAllowed = maxBookingsAllowed;
        this.showOnlyAvailableSlots = showOnlyAvailableSlots;
        initView();
    }

    public SlotsDialog(Context context, int serviceId, int locationId, ISlotInfo iSlotInfo, int providerId, String availableDate, boolean showOnlyAvailableSlots) {
        super(context);
        this.context = context;
        this.serviceId = serviceId;
        this.locationId = locationId;
        this.iSlotInfo = iSlotInfo;
        this.providerId = providerId;
        this.defaultDate = availableDate;
        this.showOnlyAvailableSlots = showOnlyAvailableSlots;
    }


    private void initView() {

        inflate(context, R.layout.slots_dialog, this);


        initializations();
        this.iSelectSlotInterface = this;
        this.onBottomReachedListener = this;

        getSlotsOnDate(serviceId, locationId, defaultDate, providerId);

        sDate = null;
        String dtStart = defaultDate;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            sDate = format.parse(dtStart);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);

        datePickerTimeline = findViewById(R.id.main_single_row_calendar);
        calendarDate = Calendar.getInstance();
        datePickerTimeline.setInitialDate(calendarDate.get(Calendar.YEAR), calendarDate.get(Calendar.MONTH), calendarDate.get(Calendar.DAY_OF_MONTH));
        calendarDate.setTime(sDate);
        datePickerTimeline.setActiveDate(calendarDate);
        datePickerTimeline.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int dayOfWeek) {
                //Do Something
                String Tag = "null";
                Log.d(Tag, "onDateSelected: " + day);
                Calendar mCal = Calendar.getInstance();
                mCal.set(Calendar.YEAR, year);
                mCal.set(Calendar.MONTH, month);
                mCal.set(Calendar.DAY_OF_MONTH, day);
                Date d = mCal.getTime();

                String nameOfMonth = DateUtils1.getMonthName(d);
                String yer = DateUtils1.getYear(d);

                tvSelectedCalendarDate.setText(nameOfMonth + ", " + yer);

                updateSelectedDate(mCal);

            }

            @Override
            public void onDisabledDateSelected(int year, int month, int day, int dayOfWeek, boolean isDisabled) {
                //Log.d(TAG, "onDisabledDateSelected: " + day);
            }
        });
        // click-actions
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Date d = myCalendar.getTime();

                String nameOfMonth = DateUtils1.getMonthName(d);
                String yer = DateUtils1.getYear(d);

                tvSelectedCalendarDate.setText(nameOfMonth + ", " + yer);

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String apiFormat = "yyyy-MM-dd"; // your format
                SimpleDateFormat apiSdf = new SimpleDateFormat(apiFormat);
                String pickedDate = apiSdf.format(myCalendar.getTime());
                try {
                    selectedDate = getCustomDateString(pickedDate);
                    Calendar calendarToday = Calendar.getInstance();
                    String sd1 = sdf.format(calendarToday.getTime());
                    Date d1 = sdf.parse(sd1);
                    String sd2 = sdf.format(myCalendar.getTime());
                    Date d2 = sdf.parse(sd2);
                    long difference_In_Time
                            = d2.getTime() - d1.getTime();
                    long difference_In_Days
                            = TimeUnit
                            .MILLISECONDS
                            .toDays(difference_In_Time)
                            % 365;
                    datePickerTimeline.setActiveDate(myCalendar);
                    if (difference_In_Time < 0) {   //for selected item shown center
                        if (difference_In_Days >= 3) {
                            datePickerTimeline.goPosition(difference_In_Days - 3);
                        } else {
                            datePickerTimeline.goPosition(difference_In_Days);
                        }
                    } else {
                        datePickerTimeline.goPosition(difference_In_Days + 3);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

//
                updateSelectedDate(myCalendar);
            }
        };
        btn_calendar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog da = new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
               /*DatePickerDialog da = new DatePickerDialog(context, R.style.Base_Theme_AppCompat_Light_Dialog, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));*/

                da.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                da.show();
                da.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                da.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            }
        });

    }

    private void initializations() {

        rvSlots = findViewById(R.id.rv_slots);
        llNoSlots = findViewById(R.id.ll_noSlots);
        llSeeMoreHint = findViewById(R.id.ll_seeMoreHint);

        tvSelectedCalendarDate = findViewById(R.id.tvSelectedCalendarDate);
        tvSelectedCalendarDay = findViewById(R.id.tvSelectedCalendarDay);
        btn_calendar = findViewById(R.id.btn_calendar);
    }

    private void updateSelectedDate(Calendar mCalendar) {


        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEE");
        String dayOfWeek = simpledateformat.format(mCalendar.getTime());
        String sMonth = "";
        if (mCalendar.get(Calendar.MONTH) < 9) {
            sMonth = "0" + String.valueOf(mCalendar.get(Calendar.MONTH) + 1);
        } else {
            sMonth = String.valueOf(mCalendar.get(Calendar.MONTH) + 1);
        }

        String mDate = dayOfWeek + ", " + mCalendar.get(Calendar.DAY_OF_MONTH) +
                "/" + (sMonth) +
                "/" + mCalendar.get(Calendar.YEAR);
        selectedCalanderDate = mDate;

        String apiFormat = "yyyy-MM-dd"; // your format
        SimpleDateFormat apiSdf = new SimpleDateFormat(apiFormat);
        String pickedDate = apiSdf.format(mCalendar.getTime());

        getSlotsOnDate(serviceId, locationId, pickedDate, providerId);


    }

    private void getSlotsOnDate(int serviceId, int mSpinnertext, String selectDate, int modifyAccountID) {

        ApiInterface apiService =
                ApiClient.getClient(context).create(ApiInterface.class);

        //final Dialog mDialog = Config.getProgressDialog(context, context.getResources().getString(R.string.dialog_log_in));
        // mDialog.show();
        Call<ArrayList<SlotsData>> call = apiService.getSlotsOnDate(selectDate, mSpinnertext, serviceId, modifyAccountID);

        call.enqueue(new Callback<ArrayList<SlotsData>>() {
            @Override
            public void onResponse(Call<ArrayList<SlotsData>> call, Response<ArrayList<SlotsData>> response) {
                try {
                    // if (mDialog.isShowing())
                    //Config.closeDialog(getOwnerActivity(), mDialog);
                    if (response.code() == 200) {

                        if (response.body() != null) {
                            slotsData = response.body();
                            activeSlotsList.clear();
                            availableSlots.clear();
                            for (int i = 0; i < slotsData.size(); i++) {
                                ArrayList<AvailableSlotsData> availableSlotsList = new ArrayList<>();
                                availableSlotsList = slotsData.get(i).getAvailableSlots();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                Timestamp timenow = new Timestamp(new Date().getTime());

                                for (int j = 0; j < availableSlotsList.size(); j++) {

                                    if (availableSlotsList.get(j).getNoOfAvailableSlots() != 0 && availableSlotsList.get(j).isActive()) {
                                        availableSlotsList.get(j).setScheduleId(slotsData.get(i).getScheduleId());
                                        String displayTime = getDisplayTime(availableSlotsList.get(j).getSlotTime());
                                        availableSlotsList.get(j).setDisplayTime(displayTime);
                                        activeSlotsList.add(availableSlotsList.get(j));
                                    }
                                    if (!showOnlyAvailableSlots) {
                                        Date date1 = sdf.parse(slotsData.get(i).getDate() + " " + availableSlotsList.get(j).getSlotTime().split("-")[0]);
                                        Timestamp slottime = new Timestamp(date1.getTime());
                                        int c1 = slottime.compareTo(timenow);
                                        if (c1 >= 0) {
                                            availableSlotsList.get(j).setScheduleId(slotsData.get(i).getScheduleId());
                                            String displayTime = getDisplayTime(availableSlotsList.get(j).getSlotTime());
                                            availableSlotsList.get(j).setDisplayTime(displayTime);
                                            availableSlots.add(availableSlotsList.get(j));
                                        }
                                    }
                                }
                            }
                            if (activeSlotsList != null) {
                                if (activeSlotsList.size() > 0) {

                                    rvSlots.setVisibility(View.VISIBLE);
                                    llNoSlots.setVisibility(View.GONE);
                                    if (activeSlotsList.size() > 15) {
                                        llSeeMoreHint.setVisibility(View.VISIBLE);
                                    } else {
                                        llSeeMoreHint.setVisibility(View.GONE);
                                    }
                                    scheduleId = activeSlotsList.get(0).getScheduleId();
                                    slotTime = activeSlotsList.get(0).getSlotTime();
                                    displayTime = activeSlotsList.get(0).getDisplayTime();
                                    selectedDate = getCustomDateString(slotsData.get(0).getDate());
                                    selectedCalanderDate = getCalenderDateFormat(slotsData.get(0).getDate());
                                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 3);
                                    rvSlots.setLayoutManager(mLayoutManager);
                                    if (showOnlyAvailableSlots) {
                                        AtomicInteger x = new AtomicInteger();
                                        activeSlotsList.stream()
                                                .filter(n -> n != null)
                                                .forEach(slot -> slot.setPosition(x.getAndIncrement()));  //imp-- set position to all slots
                                        if (activeSlotsList.size() > 0) {
                                            sAdapter = new TimeSlotsAdapter(context, activeSlotsList, iSelectSlotInterface, onBottomReachedListener, maxBookingsAllowed);
                                        } else {
                                            showNoSlots();
                                        }
                                    } else {
                                        AtomicInteger x = new AtomicInteger();
                                        availableSlots.stream()
                                                .filter(n -> n != null)
                                                .forEach(slot -> slot.setPosition(x.getAndIncrement()));   //imp-- set position to all slots
                                        if (availableSlots.size() > 0) {
                                            sAdapter = new TimeSlotsAdapter(context, availableSlots, iSelectSlotInterface, onBottomReachedListener, maxBookingsAllowed);
                                        } else {
                                            showNoSlots();
                                        }
                                    }
                                    rvSlots.setAdapter(sAdapter);
                                } else {

                                    showNoSlots();

                                }
                            } else {

                                showNoSlots();
                            }
                        } else {

                            showNoSlots();

                        }

                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<SlotsData>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
               /* if (mDialog.isShowing())
                    Config.closeDialog(getOwnerActivity(), mDialog);*/
            }
        });
    }

    public void showNoSlots() {

        rvSlots.setVisibility(View.GONE);
        llNoSlots.setVisibility(View.VISIBLE);
        llSeeMoreHint.setVisibility(View.GONE);
        iSlotInfo.sendSlotInfo(null);

    }

    private String getDisplayTime(String slotTime) {

        String displayTime = slotTime.split("-")[0];
        String sTime = "";

        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(displayTime);
            SimpleDateFormat time = new SimpleDateFormat("hh:mm aa");
            sTime = time.format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return sTime;
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

    public static String getCalenderDateFormat(String d) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = format.parse(d);
        format = new SimpleDateFormat("EEE, dd/MM/yyyy");
        String yourDate = format.format(date1);

        return yourDate;

    }

    @Override
    public void sendSelectedTime(String dspTime, String sTime, int schdId) {

        // assigning
        displayTime = dspTime;
        slotTime = sTime;
        scheduleId = schdId;
    }

    @Override
    public void sendSelectedTime(List<SelectedSlotDetail> selectedSlotDetails) {
        this.selectedSlotDetails = selectedSlotDetails;

        if (selectedSlotDetails.size() == 1) {
            // assigning
            displayTime = selectedSlotDetails.get(0).getDisplayTime();
            slotTime = selectedSlotDetails.get(0).getSlotTime();
            scheduleId = selectedSlotDetails.get(0).getScheduleId();
        } else {
            this.selectedSlotDetails = selectedSlotDetails;
        }
        for (SelectedSlotDetail s : selectedSlotDetails) {
            s.setDate(selectedDate);
            s.setCalendarDate(selectedCalanderDate);
        }
        iSlotInfo.sendSlotInfo(selectedSlotDetails);
    }


    @Override
    public void onBottomReached(int position) {

        //hide scroll hint when recyclerview reaches to last position

        llSeeMoreHint.setVisibility(View.GONE);
    }


}
