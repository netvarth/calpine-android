package com.jaldeeinc.jaldee.custom;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinodev.androidneomorphframelayout.NeomorphFrameLayout;
import com.jaldeeinc.jaldee.Interface.ISelectedTime;
import com.jaldeeinc.jaldee.Interface.ITimeSlot;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.OrderTimeSlotAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.response.CatalogTimeSlot;
import com.jaldeeinc.jaldee.response.Schedule;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SlotSelection extends Dialog implements ITimeSlot {

    private Context context;
    private CustomTextViewBold tvDate;
    private CustomTextViewBold tvTime;
    private CustomTextViewMedium tvReason;
    private ImageView ivMinus, ivPlus, ivClose;
    private static CustomTextViewSemiBold tvCalenderDate;
    private RecyclerView rvSlots;
    private CardView cvConfirm;
    private LinearLayout llNoSlots;
    private NeomorphFrameLayout cvCalender;
    final Calendar myCalendar = Calendar.getInstance();
    private ArrayList<Schedule> schedulesList = new ArrayList<>();
    private ArrayList<CatalogTimeSlot> timeSlots = new ArrayList<>();
    private ISelectedTime iSelectedTime;
    private Schedule schedule = new Schedule();
    private OrderTimeSlotAdapter orderTimeSlotAdapter;
    private ITimeSlot iTimeSlot;
    private String selectedDate = "";
    private String selectedTime = "";


    public SlotSelection(@NonNull Context context, ArrayList<Schedule> schedulesList, ISelectedTime iSelectedTime, String selectedDate) {
        super(context);
        this.context = context;
        this.schedulesList = schedulesList;
        this.iSelectedTime = iSelectedTime;
        this.selectedDate = selectedDate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slot_selection);
        iTimeSlot = (ITimeSlot) this;

        initializations();


        getUpdatedData(schedulesList, selectedDate);


        Date sDate = null;
        String dtStart = selectedDate;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            sDate = format.parse(dtStart);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorow = cal.getTime();
        if (today.before(sDate)) {
            Config.logV("Date Enabled---------------");
            ivMinus.setEnabled(true);
            ivMinus.setColorFilter(ContextCompat.getColor(context, R.color.location_theme), android.graphics.PorterDuff.Mode.SRC_IN);

        } else {
            Config.logV("Date Disabled---------------");
            ivMinus.setEnabled(false);
            ivMinus.setColorFilter(ContextCompat.getColor(context, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
        }

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

        cvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedTime != null && !selectedTime.trim().equalsIgnoreCase("")) {
                    iSelectedTime.sendTime(selectedTime, selectedDate, tvCalenderDate.getText().toString());
                    dismiss();
                } else {

                    DynamicToast.make(context, "Please select a time slot", AppCompatResources.getDrawable(
                            context, R.drawable.ic_info_black),
                            ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.green), Toast.LENGTH_SHORT).show();

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
                    selectedDate = selecteddateParse.format(added_date);
                    UpdateDAte(selectedDate);
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
                    selectedDate = selecteddateParse.format(added_date);
                    UpdateDAte(selectedDate);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void getUpdatedData(ArrayList<Schedule> schedulesList, String sDate) {

        try {
            tvCalenderDate.setText(getCalenderDateFormat(sDate));
            schedule = getSlotsByDate(schedulesList, sDate);
            timeSlots = new ArrayList<>();
            timeSlots = schedule.getCatalogTimeSlotList();
            selectedDate = schedule.getDate();
            if (timeSlots != null && timeSlots.size() > 0) {
                String startTime = timeSlots.get(0).getStartTime();
                String endTime = timeSlots.get(0).getEndTime();
                selectedTime = startTime + " - " + endTime;
                llNoSlots.setVisibility(View.GONE);
                rvSlots.setVisibility(View.VISIBLE);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 2);
                rvSlots.setLayoutManager(mLayoutManager);
                orderTimeSlotAdapter = new OrderTimeSlotAdapter(context, timeSlots, iTimeSlot);
                rvSlots.setAdapter(orderTimeSlotAdapter);
            }  else {
                rvSlots.setVisibility(View.GONE);
                llNoSlots.setVisibility(View.VISIBLE);
                tvReason.setText(schedule.getReason());

            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


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
            tvDate.setText((pickedDate));
            UpdateDAte(mDate);
            getUpdatedData(schedulesList, pickedDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initializations() {

        tvDate = findViewById(R.id.tv_date);
        tvTime = findViewById(R.id.tv_time);
        ivMinus = findViewById(R.id.iv_minus);
        ivPlus = findViewById(R.id.iv_add);
        cvCalender = findViewById(R.id.fl_calender);
        tvCalenderDate = findViewById(R.id.tv_calenderDate);
        rvSlots = findViewById(R.id.rv_slots);
        cvConfirm = findViewById(R.id.cv_submit);
        llNoSlots = findViewById(R.id.ll_noSlots);
        tvReason = findViewById(R.id.tv_reason);
    }

    public Schedule getSlotsByDate(ArrayList<Schedule> objList, String date) {
        for (Schedule obj : objList) {
            if (obj.getDate().equalsIgnoreCase(date)) {
                return obj;
            }
        }
        return null;
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
        getUpdatedData(schedulesList, sDate);

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
    public void sendSelectedTime(String displayTime) {

        selectedTime = displayTime;
    }
}
