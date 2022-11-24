package com.jaldeeinc.jaldee.custom;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaldeeinc.jaldee.CustomSwipe.DiscreteScrollView;
import com.jaldeeinc.jaldee.CustomSwipe.transform.ScaleTransformer;
import com.jaldeeinc.jaldee.Interface.ISelectQ;
import com.jaldeeinc.jaldee.Interface.ISelectedQueue;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.QueuesAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.response.QueueTimeSlotModel;
import com.jaldeeinc.jaldee.singlerowcalendar.DatePickerTimeline;
import com.jaldeeinc.jaldee.singlerowcalendar.OnDateSelectedListener;
import com.jaldeeinc.jaldee.utils.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckInSlotsDialog extends LinearLayout implements ISelectedQueue {

    private Context context;
    public String selectedCalanderDate;

    private TextView tvDate;
    private TextView tvTime, tvPeople, tvMinutes;
    private TextView tvPeopleHint, tvMinutesHint;
    private TextView tvSlash;
    private DiscreteScrollView rvQueues;
    private LinearLayout llNoSlots, llWaiting;
    final Calendar myCalendar = Calendar.getInstance();
    private int serviceId, locationId, providerId;
    private ISelectedQueue iSelectedQueue;
    static ArrayList<QueueTimeSlotModel> mQueueTimeSlotList = new ArrayList<>();
    QueuesAdapter qAdapter;
    private ISelectQ iSelectQ;
    private String defaultDate;
    private String displayTime = "", slotTime = "";
    private int queueId;
    private LinearLayout llEstTime;
    private TextView tvServiceTime;
    private String selectedDate = "";
    private QueueTimeSlotModel queueDetails = new QueueTimeSlotModel();

    TextView tvSelectedCalendarDate;
    TextView tvSelectedCalendarDay;
    Button btn_calendar;

    Date sDate;
    DatePickerTimeline datePickerTimeline;
    Calendar calendarDate;


    public CheckInSlotsDialog(Context context, int serviceId, int locationId, ISelectQ iSelectQ, int providerId, String availableDate) {
        super(context);
        this.context = context;
        this.serviceId = serviceId;
        this.locationId = locationId;
        this.iSelectQ = iSelectQ;
        this.providerId = providerId;
        this.defaultDate = availableDate;
        initView();
    }

    private void initView() {

        inflate(context, R.layout.checkin_queue, this);

        initializations();
        this.iSelectedQueue = this;

        ApiQueueTimeSlot(String.valueOf(locationId), String.valueOf(serviceId), String.valueOf(providerId), defaultDate);

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

                String nameOfMonth = DateUtils.INSTANCE.getMonthName(d);
                String yer = DateUtils.INSTANCE.getYear(d);

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

                String nameOfMonth = DateUtils.INSTANCE.getMonthName(d);
                String yer = DateUtils.INSTANCE.getYear(d);

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
                    if (difference_In_Time < 0) {  /// for selected item shown center
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
                updateSelectedDate(myCalendar);
            }
        };

        btn_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog da = new DatePickerDialog(context, R.style.Base_Theme_AppCompat_Light_Dialog, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                da.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                da.show();
            }
        });

    }

    private void ApiQueueTimeSlot(String locationId, String subSeriveID, String accountID, String mDate) {
        ApiInterface apiService =
                ApiClient.getClient(context).create(ApiInterface.class);
     /*   final Dialog mDialog = Config.getProgressDialog(context, context.getResources().getString(R.string.dialog_log_in));
        mDialog.show();*/
        Call<ArrayList<QueueTimeSlotModel>> call = apiService.getQueueTimeSlot(locationId, subSeriveID, mDate, accountID);
        call.enqueue(new Callback<ArrayList<QueueTimeSlotModel>>() {
            @Override
            public void onResponse(Call<ArrayList<QueueTimeSlotModel>> call, Response<ArrayList<QueueTimeSlotModel>> response) {
                try {
                     /*   if (mDialog.isShowing())
                            Config.closeDialog(context, mDialog);*/
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    Config.logV("mQueueTimeSlotList--------11111-----------------" + response.code());
                    if (response.code() == 200) {
                        mQueueTimeSlotList = response.body();

                        if (mQueueTimeSlotList != null) {

                            if (mQueueTimeSlotList.size() > 0) {

                                rvQueues.setVisibility(View.VISIBLE);
                                llNoSlots.setVisibility(View.GONE);
                                llWaiting.setVisibility(View.VISIBLE);
                                tvDate.setVisibility(View.VISIBLE);
                                tvTime.setVisibility(View.VISIBLE);
                                queueId = mQueueTimeSlotList.get(0).getId();
                                queueDetails = mQueueTimeSlotList.get(0);
                                selectedDate = mQueueTimeSlotList.get(0).getEffectiveSchedule().getStartDate();
                                tvTime.setText(mQueueTimeSlotList.get(0).getQueueSchedule().getTimeSlots().get(0).getsTime() + "-" + mQueueTimeSlotList.get(0).getQueueSchedule().getTimeSlots().get(0).geteTime());
                                displayTime = mQueueTimeSlotList.get(0).getQueueSchedule().getTimeSlots().get(0).getsTime() + "-" + mQueueTimeSlotList.get(0).getQueueSchedule().getTimeSlots().get(0).geteTime();
                                selectedCalanderDate = getCalenderDateFormat(mQueueTimeSlotList.get(0).getEffectiveSchedule().getStartDate());

                                if (mQueueTimeSlotList.get(0).getQueueSize() >= 0) {

                                    if (mQueueTimeSlotList.get(0).getQueueSize() == 1) {
                                        tvPeople.setText(String.valueOf(mQueueTimeSlotList.get(0).getQueueSize()));
                                        tvPeopleHint.setText("Person");
                                    } else {
                                        tvPeople.setText(String.valueOf(mQueueTimeSlotList.get(0).getQueueSize()));
                                        tvPeopleHint.setText("People");
                                    }
                                }

                                if (!mQueueTimeSlotList.get(0).getCalculationMode().equalsIgnoreCase("NoCalc")) {

                                    tvSlash.setVisibility(View.VISIBLE);
                                    if (mQueueTimeSlotList.get(0).getServiceTime() != null) {

                                        llEstTime.setVisibility(View.GONE);
                                        tvServiceTime.setVisibility(View.VISIBLE);
                                        tvServiceTime.setText(mQueueTimeSlotList.get(0).getServiceTime());
                                    } else {

                                        llEstTime.setVisibility(View.VISIBLE);
                                        tvServiceTime.setVisibility(View.GONE);
                                        int waitingTime = mQueueTimeSlotList.get(0).getQueueWaitingTime();
                                        if (waitingTime >= 0) {
                                            if (waitingTime == 1) {
                                                tvMinutes.setText(String.valueOf(waitingTime));
                                                tvMinutesHint.setText("Minute");
                                            } else {
                                                tvMinutes.setText(String.valueOf(waitingTime));
                                                tvMinutesHint.setText("Minutes");
                                            }
                                        }
                                    }

                                } else {
                                    tvMinutes.setVisibility(View.GONE);
                                    tvSlash.setVisibility(View.GONE);
                                }

                                qAdapter = new QueuesAdapter(context, mQueueTimeSlotList, iSelectedQueue);
                                rvQueues.setAdapter(qAdapter);
                                rvQueues.setItemTransformer(new ScaleTransformer.Builder()
                                        .setMinScale(0.8f)
                                        .build());

                            } else {
                                showNoSlots();
                            }
                        } else {
                            showNoSlots();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<QueueTimeSlotModel>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                /*if (mDialog.isShowing())
                    Config.closeDialog(C, mDialog);*/
            }
        });
    }

    public void showNoSlots() {

        rvQueues.setVisibility(View.GONE);
        llNoSlots.setVisibility(View.VISIBLE);
        llWaiting.setVisibility(View.GONE);
        tvDate.setVisibility(View.GONE);
        tvTime.setVisibility(View.GONE);
        iSelectQ.sendSelectedQueueInfo(displayTime, queueId, null, selectedDate);
    }

    private void initializations() {

        tvDate = findViewById(R.id.tv_date);
        tvTime = findViewById(R.id.tv_time);
        rvQueues = findViewById(R.id.rv_queues);
        llNoSlots = findViewById(R.id.ll_noSlots);
        tvPeople = findViewById(R.id.tv_people);
        tvPeopleHint = findViewById(R.id.tv_peopleHint);
        tvMinutes = findViewById(R.id.tv_minutes);
        tvMinutesHint = findViewById(R.id.tv_minutesHint);
        tvServiceTime = findViewById(R.id.tv_serviceTime);
        llEstTime = findViewById(R.id.ll_estTime);
        tvSlash = findViewById(R.id.tv_slash);
        llWaiting = findViewById(R.id.ll_waiting);

        tvSelectedCalendarDate = findViewById(R.id.tvSelectedCalendarDate);
        tvSelectedCalendarDay = findViewById(R.id.tvSelectedCalendarDay);
        btn_calendar = findViewById(R.id.btn_calendar);
    }

    private void updateSelectedDate(Calendar mCalendar) {

        try {
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
            ApiQueueTimeSlot(String.valueOf(locationId), String.valueOf(serviceId), String.valueOf(providerId), pickedDate);


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

    public static String getCalenderDateFormat(String d) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = format.parse(d);
        format = new SimpleDateFormat("EEE, dd/MM/yyyy");
        String yourDate = format.format(date1);

        return yourDate;

    }


    public Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);

        return cal.getTime();
    }

    public Date subtractDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -days);

        return cal.getTime();
    }

    @Override
    public void sendSelectedQueue(String displayQueueTime, QueueTimeSlotModel queue, int id) {

        if (queue != null) {
            tvTime.setText(displayQueueTime);
            displayTime = displayQueueTime;
            queueId = id; // get QueueId from selected queue
            queueDetails = queue;  // get Queue details from adapter

            if (queue.getQueueSize() >= 0) {
                if (queue.getQueueSize() == 1) {
                    tvPeople.setText(String.valueOf(queue.getQueueSize()));
                    tvPeopleHint.setText("Person");
                } else {
                    tvPeople.setText(String.valueOf(queue.getQueueSize()));
                    tvPeopleHint.setText("People");
                }
            }

            if (!queue.getCalculationMode().equalsIgnoreCase("NoCalc")) {

                tvSlash.setVisibility(View.VISIBLE);
                if (queue.getServiceTime() != null) {

                    llEstTime.setVisibility(View.GONE);
                    tvServiceTime.setVisibility(View.VISIBLE);
                    tvServiceTime.setText(queue.getServiceTime());
                } else {

                    llEstTime.setVisibility(View.VISIBLE);
                    tvServiceTime.setVisibility(View.GONE);
                    int waitingTime = queue.getQueueWaitingTime();
                    if (waitingTime >= 0) {
                        if (waitingTime == 1) {
                            tvMinutes.setText(String.valueOf(waitingTime));
                            tvMinutesHint.setText("Minute");
                        } else {
                            tvMinutes.setText(String.valueOf(waitingTime));
                            tvMinutesHint.setText("Minutes");
                        }
                    }
                }

            } else {
                tvMinutes.setVisibility(View.GONE);
                tvSlash.setVisibility(View.GONE);
            }
            iSelectQ.sendSelectedQueueInfo(displayTime, queueId, queueDetails, selectedDate);
        }
    }

    public static String getWaitingTime(QueueTimeSlotModel queue) {
        String firstWord = "";
        String secondWord = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date c = Calendar.getInstance().getTime();
        String formattedDate = df.format(c);
        System.out.println("Current time => " + formattedDate);
        Date date1 = null, date2 = null;
        try {
            date1 = df.parse(formattedDate);
            if (queue.getEffectiveSchedule().getStartDate() != null)
                date2 = df.parse(queue.getEffectiveSchedule().getStartDate());
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
                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                String inputDateStr = mQueueTimeSlotList.get(0).getEffectiveSchedule().getStartDate();
                Date datechange = null;
                try {
                    datechange = inputFormat.parse(inputDateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String outputDateStr = outputFormat.format(datechange);
                String yourDate = Config.getFormatedDate(outputDateStr);
                secondWord = yourDate + ", " + queue.getServiceTime();
            } else {
                secondWord = "\nToday, " + queue.getServiceTime();
            }
        } else {
            firstWord = "Est wait time  ";
            secondWord = Config.getTimeinHourMinutes(queue.getQueueWaitingTime());
        }

        return firstWord + "-" + secondWord;

    }

}
