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

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinodev.androidneomorphframelayout.NeomorphFrameLayout;
import com.jaldeeinc.jaldee.Interface.ISelectSlotInterface;
import com.jaldeeinc.jaldee.Interface.ISlotInfo;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Appointment;
import com.jaldeeinc.jaldee.activities.AppointmentActivity;
import com.jaldeeinc.jaldee.adapter.TimeSlotsAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.response.AvailableSlotsData;
import com.jaldeeinc.jaldee.response.SlotsData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SlotsDialog extends Dialog implements ISelectSlotInterface {

    private Context context;
    private CustomTextViewBold tvDate;
    private CustomTextViewBold tvTime;
    private ImageView ivMinus, ivPlus, ivClose;
    private CustomTextViewSemiBold tvCalenderDate;
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
    private ISelectSlotInterface iSelectSlotInterface;
    private String defaultDate;


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

        getSlotsOnDate(serviceId, locationId, defaultDate, providerId);

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

    }

    private void updateSelectedDate(int year, int monthOfYear, int dayOfMonth) {

        try {


            SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
            Date date = new Date(year, monthOfYear, dayOfMonth - 1);
            String dayOfWeek = simpledateformat.format(date);

            String sMonth = "";
            if (monthOfYear < 10) {
                sMonth = "0" + String.valueOf(monthOfYear + 1);
            } else {
                sMonth = String.valueOf(monthOfYear + 1);
            }

            String mDate = dayOfWeek + "\n" + dayOfMonth +
                    "/" + (sMonth) +
                    "/" + year;
            tvCalenderDate.setText(mDate);

            String apiFormat = "yyyy-MM-dd"; // your format
            SimpleDateFormat apiSdf = new SimpleDateFormat(apiFormat);
            String pickedDate = apiSdf.format(myCalendar.getTime());
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
        Call<ArrayList<SlotsData>> call = apiService.getSlotsOnDate(selectDate, serviceId, mSpinnertext, modifyAccountID);

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
                                    activeScheduleId = activeSlotsList.get(0).getScheduleId();
                                    tvTime.setText(activeSlotsList.get(0).getDisplayTime());
                                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 3);
                                    rvSlots.setLayoutManager(mLayoutManager);
                                    sAdapter = new TimeSlotsAdapter(context, activeSlotsList, iSelectSlotInterface);
                                    rvSlots.setAdapter(sAdapter);
                                } else {

                                    rvSlots.setVisibility(View.GONE);
                                    llNoSlots.setVisibility(View.VISIBLE);
                                }
                            }
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

    private String getDisplayTime(String slotTime) {

        String displayTime = slotTime.split("-")[0];
        String sTime = "";

        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(displayTime);
            SimpleDateFormat time = new SimpleDateFormat("K:mm aa");
            sTime = time.format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return sTime;
    }


    @Override
    public void sendSelectedTime(String time, String displayTime, int scheduleId) {

        iSlotInfo.sendSlotInfo(time, displayTime, scheduleId);
    }
}
