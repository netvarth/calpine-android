package com.jaldeeinc.jaldee.custom;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinodev.androidneomorphframelayout.NeomorphFrameLayout;
import com.jaldeeinc.jaldee.Interface.ISelectQ;
import com.jaldeeinc.jaldee.Interface.ISelectSlotInterface;
import com.jaldeeinc.jaldee.Interface.ISelectedQueue;
import com.jaldeeinc.jaldee.Interface.ISlotInfo;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.QueuesAdapter;
import com.jaldeeinc.jaldee.adapter.TimeSlotsAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.response.AvailableSlotsData;
import com.jaldeeinc.jaldee.response.QueueTimeSlotModel;
import com.jaldeeinc.jaldee.response.Queues;
import com.jaldeeinc.jaldee.response.SlotsData;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckInSlotsDialog extends Dialog implements ISelectedQueue {

    private Context context;
    private CustomTextViewBold tvDate;
    private CustomTextViewBold tvTime, tvPeople, tvMinutes;
    private CustomTextViewMedium tvPeopleHint, tvMinutesHint;
    private CustomTextViewThin tvSlash;
    private ImageView ivMinus, ivPlus, ivClose;
    private static CustomTextViewSemiBold tvCalenderDate;
    private RecyclerView rvQueues;
    private CardView cvConfirm;
    private LinearLayout llNoSlots,llWaiting;
    private NeomorphFrameLayout cvCalender;
    final Calendar myCalendar = Calendar.getInstance();
    private int activeScheduleId, serviceId, locationId, providerId;
    private ISelectedQueue iSelectedQueue;
    static ArrayList<QueueTimeSlotModel> mQueueTimeSlotList = new ArrayList<>();
    ArrayList<SlotsData> slotsData = new ArrayList<SlotsData>();
    QueuesAdapter qAdapter;
    private ISelectQ iSelectQ;
    private String defaultDate;
    private String displayTime = "", slotTime = "";
    private int queueId;
    private LinearLayout llEstTime;
    private CustomTextViewMedium tvServiceTime;
    private String selectedDate = "";
    private QueueTimeSlotModel queueDetails = new QueueTimeSlotModel();


    public CheckInSlotsDialog(Context context, int serviceId, int locationId, ISelectQ iSelectQ, int providerId, String availableDate) {
        super(context);
        this.context = context;
        this.serviceId = serviceId;
        this.locationId = locationId;
        this.iSelectQ = iSelectQ;
        this.providerId = providerId;
        this.defaultDate = availableDate;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin_queue);

        initializations();
        this.iSelectedQueue = this;

        ApiQueueTimeSlot(String.valueOf(locationId), String.valueOf(serviceId), String.valueOf(providerId), defaultDate);

        // click-actions

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateSelectedDate(year, monthOfYear, dayOfMonth);
            }
        };

        cvCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog da = new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                da.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                da.show();
            }
        });


        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

        cvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (queueId > 0) {
                    iSelectQ.sendSelectedQueueInfo(displayTime, queueId, queueDetails,selectedDate);
                    dismiss();
                } else {

                    DynamicToast.make(context, "Please select a queue", AppCompatResources.getDrawable(
                            context, R.drawable.ic_info_black),
                            ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.arrived_green), Toast.LENGTH_SHORT).show();

                }
            }
        });

        ivPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    String dtStart = tvCalenderDate.getText().toString();
                    Config.logV("Date----------------" + dtStart);
                    Date date = null;
                    SimpleDateFormat format = new SimpleDateFormat("EEE, dd/MM/yyyy");
                    try {
                        date = format.parse(dtStart);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Date added_date = addDays(date, 1);
                    DateFormat dateFormat = new SimpleDateFormat("EEE, dd/MM/yyyy");
                    //to convert Date to String, use format method of SimpleDateFormat class.
                    String strDate = dateFormat.format(added_date);
                    tvCalenderDate.setText(strDate);
                    DateFormat selecteddateParse = new SimpleDateFormat("yyyy-MM-dd");
                    defaultDate = selecteddateParse.format(added_date);
                    UpdateDAte(defaultDate);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        ivMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    String dtStart = tvCalenderDate.getText().toString();
                    Config.logV("Date----------------" + dtStart);
                    Date date = null;
                    SimpleDateFormat format = new SimpleDateFormat("EEE, dd/MM/yyyy");
                    try {
                        date = format.parse(dtStart);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Date added_date = subtractDays(date, 1);
                    DateFormat dateFormat = new SimpleDateFormat("EEE, dd/MM/yyyy");
                    //to convert Date to String, use format method of SimpleDateFormat class.
                    String strDate = dateFormat.format(added_date);
                    tvCalenderDate.setText(strDate);
                    DateFormat selecteddateParse = new SimpleDateFormat("yyyy-MM-dd");
                    defaultDate = selecteddateParse.format(added_date);
                    UpdateDAte(defaultDate);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void ApiQueueTimeSlot(String locationId, String subSeriveID, String accountID, String mDate) {
        ApiInterface apiService =
                ApiClient.getClient(context).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(context, context.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ArrayList<QueueTimeSlotModel>> call = apiService.getQueueTimeSlot(locationId, subSeriveID, mDate, accountID);
        call.enqueue(new Callback<ArrayList<QueueTimeSlotModel>>() {
            @Override
            public void onResponse(Call<ArrayList<QueueTimeSlotModel>> call, Response<ArrayList<QueueTimeSlotModel>> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getOwnerActivity(), mDialog);
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
                                cvConfirm.setVisibility(View.VISIBLE);
                                queueId = mQueueTimeSlotList.get(0).getId();
                                queueDetails = mQueueTimeSlotList.get(0);
                                selectedDate = mQueueTimeSlotList.get(0).getEffectiveSchedule().getStartDate();
                                tvDate.setText(getCustomDateString(mQueueTimeSlotList.get(0).getEffectiveSchedule().getStartDate()));
                                tvTime.setText(mQueueTimeSlotList.get(0).getQueueSchedule().getTimeSlots().get(0).getsTime() + "-" + mQueueTimeSlotList.get(0).getQueueSchedule().getTimeSlots().get(0).geteTime());
                                displayTime = mQueueTimeSlotList.get(0).getQueueSchedule().getTimeSlots().get(0).getsTime() + "-" + mQueueTimeSlotList.get(0).getQueueSchedule().getTimeSlots().get(0).geteTime();
                                tvCalenderDate.setText(getCalenderDateFormat(mQueueTimeSlotList.get(0).getEffectiveSchedule().getStartDate()));

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

                                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 2);
                                rvQueues.setLayoutManager(mLayoutManager);
                                qAdapter = new QueuesAdapter(context, mQueueTimeSlotList, iSelectedQueue);
                                rvQueues.setAdapter(qAdapter);

                            } else {
                                rvQueues.setVisibility(View.GONE);
                                llNoSlots.setVisibility(View.VISIBLE);
                                llWaiting.setVisibility(View.GONE);
                                tvDate.setVisibility(View.GONE);
                                tvTime.setVisibility(View.GONE);
                                cvConfirm.setVisibility(View.GONE);
                            }
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
                if (mDialog.isShowing())
                    Config.closeDialog(getOwnerActivity(), mDialog);
            }
        });
    }

    public String UpdateDAte(String sDate) {
        Date selecteddate = null;
        String dtStart = tvCalenderDate.getText().toString();
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd/MM/yyyy");
        try {
            selecteddate = format.parse(dtStart);
            //  System.out.println(selecteddate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ApiQueueTimeSlot(String.valueOf(locationId), String.valueOf(serviceId), String.valueOf(providerId), sDate);

        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorow = cal.getTime();
        if (today.before(selecteddate)) {
            Config.logV("Date Enabled---------------");
            ivMinus.setEnabled(true);
            ivMinus.setColorFilter(ContextCompat.getColor(context, R.color.location_theme), android.graphics.PorterDuff.Mode.SRC_IN);

        } else {
            Config.logV("Date Disabled---------------");
            ivMinus.setEnabled(false);
            ivMinus.setColorFilter(ContextCompat.getColor(context, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        return "";
    }

    private void initializations() {

        tvDate = findViewById(R.id.tv_date);
        tvTime = findViewById(R.id.tv_time);
        ivMinus = findViewById(R.id.iv_minus);
        ivPlus = findViewById(R.id.iv_add);
        cvCalender = findViewById(R.id.fl_calender);
        tvCalenderDate = findViewById(R.id.tv_calenderDate);
        rvQueues = findViewById(R.id.rv_queues);
        cvConfirm = findViewById(R.id.cv_submit);
        llNoSlots = findViewById(R.id.ll_noSlots);
        ivClose = findViewById(R.id.iv_close);
        tvPeople = findViewById(R.id.tv_people);
        tvPeopleHint = findViewById(R.id.tv_peopleHint);
        tvMinutes = findViewById(R.id.tv_minutes);
        tvMinutesHint = findViewById(R.id.tv_minutesHint);
        tvServiceTime = findViewById(R.id.tv_serviceTime);
        llEstTime = findViewById(R.id.ll_estTime);
        tvSlash = findViewById(R.id.tv_slash);
        llWaiting = findViewById(R.id.ll_waiting);


    }

    private void updateSelectedDate(int year, int monthOfYear, int dayOfMonth) {

        try {


            SimpleDateFormat simpledateformat = new SimpleDateFormat("EEE");
            Date date = new Date(year, monthOfYear, dayOfMonth - 1);
            String dayOfWeek = simpledateformat.format(date);

            String sMonth = "";
            if (monthOfYear < 9) {
                sMonth = "0" + String.valueOf(monthOfYear + 1);
            } else {
                sMonth = String.valueOf(monthOfYear + 1);
            }

            String mDate = dayOfWeek + ", " + dayOfMonth +
                    "/" + (sMonth) +
                    "/" + year;
            tvCalenderDate.setText(mDate);

            String apiFormat = "yyyy-MM-dd"; // your format
            SimpleDateFormat apiSdf = new SimpleDateFormat(apiFormat);
            String pickedDate = apiSdf.format(myCalendar.getTime());
            tvDate.setText(getCustomDateString(pickedDate));
            UpdateDAte(mDate);
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
