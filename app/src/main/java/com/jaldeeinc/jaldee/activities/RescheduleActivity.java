package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.chinodev.androidneomorphframelayout.NeomorphFrameLayout;
import com.jaldeeinc.jaldee.Interface.ISelectSlotInterface;
import com.jaldeeinc.jaldee.Interface.ISlotInfo;
import com.jaldeeinc.jaldee.Interface.OnBottomReachedListener;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.TimeSlotsAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.SlotsDialog;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.AvailableSlotsData;
import com.jaldeeinc.jaldee.response.ProfileModel;
import com.jaldeeinc.jaldee.response.SlotsData;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RescheduleActivity extends AppCompatActivity implements ISlotInfo,ISelectSlotInterface,OnBottomReachedListener {


    @BindView(R.id.tv_spName)
    CustomTextViewBold tvSpName;

    @BindView(R.id.tv_locationName)
    CustomTextViewMedium tvLocationName;

    @BindView(R.id.tv_serviceName)
    CustomTextViewBold tvServiceName;

    @BindView(R.id.iv_teleService)
    ImageView ivteleService;

    @BindView(R.id.tv_number)
    CustomTextViewMedium tvNumber;

    @BindView(R.id.tv_email)
    CustomTextViewMedium tvEmail;

    @BindView(R.id.tv_consumerName)
    CustomTextViewBold tvConsumerName;

    @BindView(R.id.tv_date)
    CustomTextViewBold tvDate;

    @BindView(R.id.tv_time)
    CustomTextViewBold tvTime;

    @BindView(R.id.tv_changeTime)
    CustomTextViewBold tvChangeTime;

    @BindView(R.id.tv_actualTime)
    CustomTextViewBold tvActualTime;

    @BindView(R.id.cv_back)
    CardView cvBack;

    @BindView(R.id.cv_submit)
    CardView cvSubmit;

    @BindView(R.id.cv_cancel)
    CardView cvCancel;

    @BindView(R.id.iv_minus)
    ImageView ivMinus;

    @BindView(R.id.iv_add)
    ImageView ivPlus;

    @BindView(R.id.tv_calenderDate)
    CustomTextViewSemiBold tvCalenderDate;

    @BindView(R.id.tv_userName)
    CustomTextViewBold tv_userName;

    @BindView(R.id.providerlabel)
    CustomTextViewMedium tv_labelprovider;


    int scheduleId,serviceId,locationId,accountId;
    String slotTime,apiDate;
    private SlotsDialog slotsDialog;
    private RecyclerView rvSlots;
    private LinearLayout llNoSlots,llChangeTo;
    private NeomorphFrameLayout cvCalender;
    TimeSlotsAdapter sAdapter;
    private LinearLayout llSeeMoreHint;
    ArrayList<AvailableSlotsData> activeSlotsList = new ArrayList<>();
    ArrayList<SlotsData> slotsData = new ArrayList<SlotsData>();
    private ISelectSlotInterface iSelectSlotInterface;
    private OnBottomReachedListener onBottomReachedListener;
    private ISlotInfo iSlotInfo;
    private Context mContext;
    final Calendar myCalendar = Calendar.getInstance();
    ActiveAppointment appointmentInfo = new ActiveAppointment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reschedule);
        ButterKnife.bind(RescheduleActivity.this);
        mContext = this;
        iSlotInfo = this;
        this.iSelectSlotInterface = this;
        this.onBottomReachedListener = this;

        initializations();

        Intent i = getIntent();
        appointmentInfo = (ActiveAppointment) i.getSerializableExtra("appointmentInfo");

        if (appointmentInfo != null && appointmentInfo.getAppmtDate() != null) {

            Date sDate = null;
            String dtStart = appointmentInfo.getAppmtDate();
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
                ivMinus.setColorFilter(ContextCompat.getColor(RescheduleActivity.this, R.color.location_theme), android.graphics.PorterDuff.Mode.SRC_IN);

            } else {
                Config.logV("Date Disabled---------------");
                ivMinus.setEnabled(false);
                ivMinus.setColorFilter(ContextCompat.getColor(RescheduleActivity.this, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        }

        if (appointmentInfo != null) {

            String name = appointmentInfo.getProviderAccount().getBusinessName();
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();

            tvSpName.setText(name);

            if(appointmentInfo.getProvider()!=null){
                String username = "";
                if (appointmentInfo.getProvider().getBusinessName() != null){
                    username = appointmentInfo.getProvider().getBusinessName();
                }else {
                    username  = appointmentInfo.getProvider().getFirstName() + " " + appointmentInfo.getProvider().getLastName();
                }
                username = username.substring(0, 1).toUpperCase() + username.substring(1).toLowerCase();
                tv_userName.setText(username);
                tv_userName.setVisibility(View.VISIBLE);
                tvSpName.setTextSize(16);
            }


            try {
                if (appointmentInfo.getLocation().getGoogleMapUrl() != null) {
                    String geoUri = appointmentInfo.getLocation().getGoogleMapUrl();
                    if (geoUri != null) {

                        tvLocationName.setVisibility(View.VISIBLE);
                        tvLocationName.setText(appointmentInfo.getLocation().getPlace());
                    } else {
                        tvLocationName.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            tvLocationName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String geoUri = appointmentInfo.getLocation().getGoogleMapUrl();
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            // to set service name
            if (appointmentInfo.getService() != null) {

                if (appointmentInfo.getService().getName() != null) {
                    String sName = appointmentInfo.getService().getName();
                    sName = sName.substring(0, 1).toUpperCase() + sName.substring(1).toLowerCase();
                    tvServiceName.setText(sName);
                }

                try {
                    if (appointmentInfo.getService().getServiceType() != null) {
                        if (appointmentInfo.getService().getServiceType().equalsIgnoreCase("virtualService")) {
                            ivteleService.setVisibility(View.VISIBLE);
                            if (appointmentInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                ivteleService.setImageResource(R.drawable.zoom);
                            } else if (appointmentInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                ivteleService.setImageResource(R.drawable.googlemeet);
                            } else if (appointmentInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                ivteleService.setImageResource(R.drawable.whatsapp_icon);

                            } else if (appointmentInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                ivteleService.setImageResource(R.drawable.phone_icon);
                            }
                        } else {
                            ivteleService.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            if (appointmentInfo.getAppmtDate() != null && appointmentInfo.getService() != null && appointmentInfo.getLocation()!= null && appointmentInfo.getProviderAccount() != null) {

                apiDate = appointmentInfo.getAppmtDate();
                serviceId = appointmentInfo.getService().getId();
                locationId = appointmentInfo.getLocation().getId();
                accountId = appointmentInfo.getProviderAccount().getId();
                // api call to get slots on default date (appointment date)
                getSlotsOnDate(serviceId, locationId, apiDate, accountId);

            }

                if (appointmentInfo.getAppmtFor() != null) {
                String cName = Config.toTitleCase(appointmentInfo.getAppmtFor().get(0).getFirstName()) + " " + Config.toTitleCase(appointmentInfo.getAppmtFor().get(0).getLastName());
                tvConsumerName.setText(cName);
                if (appointmentInfo.getPhoneNumber() != null){
                    tvNumber.setVisibility(View.VISIBLE);
                    tvNumber.setText(appointmentInfo.getPhoneNumber());
                }
                else {
                    tvNumber.setVisibility(View.GONE);
                }
                if (appointmentInfo.getAppmtFor().get(0).getEmail() != null) {
                    tvEmail.setVisibility(View.VISIBLE);
                    tvEmail.setText(appointmentInfo.getAppmtFor().get(0).getEmail());
                }
                else {
                    tvEmail.setVisibility(View.GONE);
                }
            }

            if (appointmentInfo.getAppmtDate() != null && appointmentInfo.getAppmtTime() != null) {
                String oldDate = convertDate(appointmentInfo.getAppmtDate());
                String time = appointmentInfo.getAppmtTime().split("-")[0];
                String oldtime = convertTime(time);
                tvActualTime.setText(oldDate+", "+ oldtime);
            }

        }

        // click actions

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

                DatePickerDialog da = new DatePickerDialog(mContext, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                da.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                da.show();
            }
        });

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        tvChangeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (appointmentInfo.getAppmtDate() != null && appointmentInfo.getService() != null && appointmentInfo.getLocation()!= null && appointmentInfo.getProviderAccount() != null) {
                        slotsDialog = new SlotsDialog(RescheduleActivity.this, appointmentInfo.getService().getId(), appointmentInfo.getLocation().getId(), iSlotInfo, appointmentInfo.getProviderAccount().getId(), appointmentInfo.getAppmtDate());
                        slotsDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                        slotsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        slotsDialog.show();
                        DisplayMetrics metrics = RescheduleActivity.this.getResources().getDisplayMetrics();
                        int width = (int) (metrics.widthPixels * 1);
                        slotsDialog.setCancelable(false);
                        slotsDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        cvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
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
                apiDate = selecteddateParse.format(added_date);
                UpdateDAte(apiDate);
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
                apiDate = selecteddateParse.format(added_date);
                UpdateDAte(apiDate);
            }
        });


        cvSubmit.setClickable(false);
        cvSubmit.setEnabled(false);
        cvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                reScheduleAppointment(appointmentInfo.getProviderAccount().getId());
            }
        });
    }

    private void initializations() {

        rvSlots = findViewById(R.id.rv_slots);
        llSeeMoreHint = findViewById(R.id.ll_seeMoreHint);
        llNoSlots = findViewById(R.id.ll_noSlots);
        cvCalender = findViewById(R.id.fl_calender);
        llChangeTo = findViewById(R.id.ll_changeTo);
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
            getSlotsOnDate(serviceId, locationId, pickedDate, accountId);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        getSlotsOnDate(serviceId, locationId, sDate, accountId);

        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorow = cal.getTime();
        if (today.before(selecteddate)) {
            Config.logV("Date Enabled---------------");
            ivMinus.setEnabled(true);
            ivMinus.setColorFilter(ContextCompat.getColor(mContext, R.color.location_theme), android.graphics.PorterDuff.Mode.SRC_IN);

        } else {
            Config.logV("Date Disabled---------------");
            ivMinus.setEnabled(false);
            ivMinus.setColorFilter(ContextCompat.getColor(mContext, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        return "";
    }



    private void getSlotsOnDate(int serviceId, int mSpinnertext, String selectDate, int modifyAccountID) {

        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ArrayList<SlotsData>> call = apiService.getSlotsOnDate(selectDate, mSpinnertext, serviceId, modifyAccountID);

        call.enqueue(new Callback<ArrayList<SlotsData>>() {
            @Override
            public void onResponse(Call<ArrayList<SlotsData>> call, Response<ArrayList<SlotsData>> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(RescheduleActivity.this, mDialog);
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
                                    llChangeTo.setVisibility(View.VISIBLE);
                                    llNoSlots.setVisibility(View.GONE);
                                    tvDate.setVisibility(View.VISIBLE);
                                    tvTime.setVisibility(View.VISIBLE);
                                    cvSubmit.setClickable(true);
                                    cvSubmit.setEnabled(true);
                                    cvSubmit.setCardBackgroundColor(getResources().getColor(R.color.location_theme));
                                    if (activeSlotsList.size()>15){
                                        llSeeMoreHint.setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        llSeeMoreHint.setVisibility(View.GONE);
                                    }
                                    scheduleId = activeSlotsList.get(0).getScheduleId();
                                    slotTime = activeSlotsList.get(0).getSlotTime();
                                    tvTime.setText(activeSlotsList.get(0).getDisplayTime());
                                    tvDate.setText(getCustomDateString(slotsData.get(0).getDate()));
                                    tvCalenderDate.setText(getCalenderDateFormat(slotsData.get(0).getDate()));
                                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 3);
                                    rvSlots.setLayoutManager(mLayoutManager);
                                    sAdapter = new TimeSlotsAdapter(mContext, activeSlotsList, iSelectSlotInterface,onBottomReachedListener);
                                    rvSlots.setAdapter(sAdapter);
                                } else {

                                    rvSlots.setVisibility(View.GONE);
                                    llNoSlots.setVisibility(View.VISIBLE);
                                    llSeeMoreHint.setVisibility(View.GONE);
                                    tvDate.setVisibility(View.GONE);
                                    tvTime.setVisibility(View.GONE);
                                    llChangeTo.setVisibility(View.GONE);
                                    cvSubmit.setClickable(false);
                                    cvSubmit.setEnabled(false);
                                    cvSubmit.setCardBackgroundColor(getResources().getColor(R.color.inactive_text));

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
                    Config.closeDialog(RescheduleActivity.this, mDialog);
            }
        });
    }


    private void reScheduleAppointment(int id) {

        ApiInterface apiService =
                ApiClient.getClient(RescheduleActivity.this).create(ApiInterface.class);

        JSONObject body = new JSONObject();
        try {
            body.put("uid",appointmentInfo.getUid());
            body.put("time",slotTime);
            body.put("date",apiDate);
            body.put("schedule",scheduleId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Dialog mDialog = Config.getProgressDialog(RescheduleActivity.this, RescheduleActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), body.toString());
        Call<ResponseBody> call = apiService.reScheduleAppointment(id,requestBody);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {

                        Toast.makeText(RescheduleActivity.this,"Appointment rescheduled successfully",Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });


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


    public static String convertDate(String date) {

        String finalDate = "";
        Date selectedDate = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            selectedDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (DateUtils.isToday(selectedDate.getTime())) {
            finalDate = "Today";
        } else {
            Format f = new SimpleDateFormat("MMM dd");
            finalDate = f.format(selectedDate);
        }

        return finalDate;
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
    public void sendSlotInfo(String displayTime, String selectedSlot, int schdId, String displayDate, String date) {

        try {

            // getting data from dialog
            String convertedTime = displayTime.replace("am", "AM").replace("pm", "PM");
            tvTime.setText(convertedTime);
            tvDate.setText(displayDate);
            scheduleId = schdId;
            slotTime = selectedSlot;
            cvSubmit.setClickable(true);
            cvSubmit.setEnabled(true);
            cvSubmit.setCardBackgroundColor(getResources().getColor(R.color.location_theme));
            try {
                apiDate = getApiDateFormat(date);  // to convert selected date to api date format
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getApiDateFormat(String d) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("EEE, dd/MM/yyyy");
        Date date1 = format.parse(d);
        format = new SimpleDateFormat("yyyy-MM-dd");
        String yourDate = format.format(date1);

        return yourDate;

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


    @Override
    public void sendSelectedTime(String dspTime, String sTime, int schdId) {

        // assigning
        tvTime.setText(dspTime);
        slotTime = sTime;
        scheduleId = schdId;
        cvSubmit.setClickable(true);
        cvSubmit.setEnabled(true);
        cvSubmit.setCardBackgroundColor(getResources().getColor(R.color.location_theme));
    }

    @Override
    public void onBottomReached(int position) {

        //hide scroll hint when recyclerview reaches to last position
        llSeeMoreHint.setVisibility(View.GONE);
    }


}