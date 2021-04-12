package com.jaldeeinc.jaldee.custom;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinodev.androidneomorphframelayout.NeomorphFrameLayout;
import com.jaldeeinc.jaldee.Interface.ISelectSlotInterface;
import com.jaldeeinc.jaldee.Interface.ISlotInfo;
import com.jaldeeinc.jaldee.Interface.OnBottomReachedListener;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Appointment;
import com.jaldeeinc.jaldee.activities.AppointmentActivity;
import com.jaldeeinc.jaldee.adapter.TimeSlotsAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.response.AvailableSlotsData;
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

public class SlotsDialog extends Dialog implements ISelectSlotInterface,OnBottomReachedListener {

    private Context context;
    private CustomTextViewBold tvDate;
    private CustomTextViewBold tvTime;
    private ImageView ivMinus, ivPlus, ivClose;
    private static CustomTextViewSemiBold tvCalenderDate;
    private RecyclerView rvSlots;
    private CardView cvConfirm;
    private LinearLayout llNoSlots;
    private NeomorphFrameLayout cvCalender;
    final Calendar myCalendar = Calendar.getInstance();
    private int activeScheduleId, serviceId, locationId, providerId;
    private ISlotInfo iSlotInfo;
    ArrayList<AvailableSlotsData> activeSlotsList = new ArrayList<>();
    ArrayList<SlotsData> slotsData = new ArrayList<SlotsData>();
    TimeSlotsAdapter sAdapter;
    private LinearLayout llSeeMoreHint;
    private ISelectSlotInterface iSelectSlotInterface;
    private String defaultDate;
    private String displayTime = "", slotTime = "";
    private int scheduleId;
    private OnBottomReachedListener onBottomReachedListener;


    public SlotsDialog(Context context, int serviceId, int locationId, ISlotInfo iSlotInfo, int providerId, String availableDate) {
        super(context);
        this.context = context;
        this.serviceId = serviceId;
        this.locationId = locationId;
        this.iSlotInfo = iSlotInfo;
        this.providerId = providerId;
        this.defaultDate = availableDate;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slots_dialog);

        initializations();
        this.iSelectSlotInterface = this;
        this.onBottomReachedListener = this;

        getSlotsOnDate(serviceId, locationId, defaultDate, providerId);

        Date sDate = null;
        String dtStart = defaultDate;
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

                if (displayTime != null && !displayTime.trim().equalsIgnoreCase("")) {
                    iSlotInfo.sendSlotInfo(displayTime, slotTime, scheduleId, tvDate.getText().toString(), tvCalenderDate.getText().toString());
                    dismiss();
                }
                else {

                    DynamicToast.make(context, "Please select a time slot", AppCompatResources.getDrawable(
                            context, R.drawable.ic_info_black),
                            ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.green), Toast.LENGTH_SHORT).show();

                }
            }
        });

        ivPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        ivMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        getSlotsOnDate(serviceId, locationId, sDate, providerId);

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
        rvSlots = findViewById(R.id.rv_slots);
        cvConfirm = findViewById(R.id.cv_submit);
        llNoSlots = findViewById(R.id.ll_noSlots);
        ivClose = findViewById(R.id.iv_close);
        llSeeMoreHint = findViewById(R.id.ll_seeMoreHint);

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
            getSlotsOnDate(serviceId, locationId, pickedDate, providerId);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getSlotsOnDate(int serviceId, int mSpinnertext, String selectDate, int modifyAccountID) {

        ApiInterface apiService =
                ApiClient.getClient(context).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(context, context.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ArrayList<SlotsData>> call = apiService.getSlotsOnDate(selectDate, mSpinnertext, serviceId, modifyAccountID);

        call.enqueue(new Callback<ArrayList<SlotsData>>() {
            @Override
            public void onResponse(Call<ArrayList<SlotsData>> call, Response<ArrayList<SlotsData>> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getOwnerActivity(), mDialog);
                    if (response.code() == 200) {

                        if (response.body() != null) {
                            slotsData = response.body();

                            activeSlotsList.clear();
                            for (int i = 0; i < slotsData.size(); i++) {
                                ArrayList<AvailableSlotsData> availableSlotsList = new ArrayList<>();
                                availableSlotsList = slotsData.get(i).getAvailableSlots();

                                for (int j = 0; j < availableSlotsList.size(); j++) {
                                    if (availableSlotsList.get(j).getNoOfAvailableSlots() != 0 && availableSlotsList.get(j).isActive()) {

                                        availableSlotsList.get(j).setScheduleId(slotsData.get(i).getScheduleId());
                                        String displayTime = getDisplayTime(availableSlotsList.get(j).getSlotTime());
                                        availableSlotsList.get(j).setDisplayTime(displayTime);
                                        activeSlotsList.add(availableSlotsList.get(j));
                                    }
                                }
                            }

                            if (activeSlotsList != null) {
                                if (activeSlotsList.size() > 0) {

                                    rvSlots.setVisibility(View.VISIBLE);
                                    llNoSlots.setVisibility(View.GONE);
                                    tvDate.setVisibility(View.VISIBLE);
                                    tvTime.setVisibility(View.VISIBLE);
                                    cvConfirm.setVisibility(View.VISIBLE);
                                    if (activeSlotsList.size()>15){
                                        llSeeMoreHint.setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        llSeeMoreHint.setVisibility(View.GONE);
                                    }
                                    scheduleId = activeSlotsList.get(0).getScheduleId();
                                    slotTime = activeSlotsList.get(0).getSlotTime();
                                    tvTime.setText(activeSlotsList.get(0).getDisplayTime());
                                    displayTime = activeSlotsList.get(0).getDisplayTime();
                                    tvDate.setText(getCustomDateString(slotsData.get(0).getDate()));
                                    tvCalenderDate.setText(getCalenderDateFormat(slotsData.get(0).getDate()));
                                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 3);
                                    rvSlots.setLayoutManager(mLayoutManager);
                                    sAdapter = new TimeSlotsAdapter(context, activeSlotsList, iSelectSlotInterface,onBottomReachedListener);
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
                if (mDialog.isShowing())
                    Config.closeDialog(getOwnerActivity(), mDialog);
            }
        });
    }

    public void showNoSlots(){

        rvSlots.setVisibility(View.GONE);
        llNoSlots.setVisibility(View.VISIBLE);
        tvDate.setVisibility(View.GONE);
        tvTime.setVisibility(View.GONE);
        cvConfirm.setVisibility(View.GONE);
        llSeeMoreHint.setVisibility(View.GONE);


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
        tvTime.setText(dspTime);
        displayTime = dspTime;
        slotTime = sTime;
        scheduleId = schdId;
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
    public void onBottomReached(int position) {

        //hide scroll hint when recyclerview reaches to last position

        llSeeMoreHint.setVisibility(View.GONE);
    }
}
