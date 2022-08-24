package com.jaldeeinc.jaldee.activities;

import static com.jaldeeinc.jaldee.activities.BookingDetails.convertTime;
import static com.jaldeeinc.jaldee.activities.BookingDetails.convertToTitleForm;
import static com.jaldeeinc.jaldee.activities.BookingDetails.getCustomDateString;
import static com.jaldeeinc.jaldee.connection.ApiClient.context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chinodev.androidneomorphframelayout.NeomorphFrameLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.Contents;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.InstructionsDialog;
import com.jaldeeinc.jaldee.custom.QRCodeEncoder;
import com.jaldeeinc.jaldee.model.BookingModel;
import com.jaldeeinc.jaldee.response.ActiveAppointment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MultipleAppointmentConfirmation extends AppCompatActivity {
    Context mContext;
    List<String> uids;
    String providerId;
    ActiveAppointment activeCheckInInfo = new ActiveAppointment();
    List<String> confirmIds = new ArrayList<>();
    List<String> times = new ArrayList<>();
    CardView cvBack;
    CustomTextViewBold tvProvider, tv_consumer, tvConfirmationNumber, tvDate, tvTime, tvBatchNo;
    CustomTextViewMedium tvProviderName, tv_location, tvServiceName;
    ImageView icon_service;
    private LinearLayout llBatchNo;
    NeomorphFrameLayout llMoreDetails;
    Button btnOk;
    LinearLayout llMessage, ll_add_to_calendar;
    private String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_appointment_confirmation);
        mContext = this;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            uids = extras.getStringArrayList("uids");
            providerId = extras.getString("accountID");
        }
        getConfirmationDetails(providerId, uids);

        initializations();
        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(MultipleAppointmentConfirmation.this, Home.class);
                startActivity(home);
                finish();
            }
        });
    }

    private void initializations() {
        cvBack = findViewById(R.id.cv_back);
        tvProvider = findViewById(R.id.tv_doctorName);
        tvProviderName = findViewById(R.id.tv_providerName);
        tv_location = findViewById(R.id.tv_locationName);
        tv_consumer = findViewById(R.id.tv_consumerName);
        tvServiceName = findViewById(R.id.tv_serviceName);
        icon_service = findViewById(R.id.iv_teleService);
        tvConfirmationNumber = findViewById(R.id.tv_confirmationNumber);
        tvDate = findViewById(R.id.tv_date);
        tvTime = findViewById(R.id.tv_time);
        llBatchNo = findViewById(R.id.ll_batch);
        tvBatchNo = findViewById(R.id.tv_batchNo);
        llMoreDetails = findViewById(R.id.ll_moreDetails);
        btnOk = findViewById(R.id.cv_ok);
        ll_add_to_calendar = findViewById(R.id.ll_add_to_calendar);
        llMessage = findViewById(R.id.ll_message);

    }

    private void getConfirmationDetails(String userId, List<String> uids) {

        final Dialog mDialog = Config.getProgressDialog(MultipleAppointmentConfirmation.this, MultipleAppointmentConfirmation.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        final ApiInterface apiService =
                ApiClient.getClient(MultipleAppointmentConfirmation.this).create(ApiInterface.class);
        Call<ActiveAppointment> call = apiService.getActiveAppointmentUUID(uids.get(0), userId);
        call.enqueue(new Callback<ActiveAppointment>() {
            @Override
            public void onResponse(Call<ActiveAppointment> call, Response<ActiveAppointment> response) {
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
                try {
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        activeCheckInInfo = response.body();
                        confirmIds.add(activeCheckInInfo.getAppointmentEncId());
                        times.add(convertTime(activeCheckInInfo.getAppmtTime().split("-")[0]));
                        uids.remove(0);
                        if (uids.size() > 0) {
                            getConfirmationDetails(userId, uids);
                        } else {
                            UpdateMainUI(activeCheckInInfo);
                        }
                    }
                } catch (Exception e) {
                    Log.i("mnbbnmmnbbnm", e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ActiveAppointment> call, Throwable t) {
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });
    }

    public void UpdateMainUI(ActiveAppointment activeCheckInInfo) {


        if (activeCheckInInfo != null) {

            if (activeCheckInInfo.getProvider() != null) {

                if (activeCheckInInfo.getProvider().getBusinessName() != null && !activeCheckInInfo.getProvider().getBusinessName().equalsIgnoreCase("")) {
                    tvProvider.setText(activeCheckInInfo.getProvider().getBusinessName());
                } else {
                    String name = activeCheckInInfo.getProvider().getFirstName() + " " + activeCheckInInfo.getProvider().getLastName();
                    tvProvider.setText(name);
                }
                tvProviderName.setVisibility(View.VISIBLE);
                tvProviderName.setText(activeCheckInInfo.getProviderAccount().getBusinessName());
                tvProviderName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {
                            Intent intent = new Intent(MultipleAppointmentConfirmation.this, ProviderDetailActivity.class);
                            intent.putExtra("uniqueID", activeCheckInInfo.getProviderAccount().getUniqueId());
                            intent.putExtra("locationId", activeCheckInInfo.getLocation().getId());
                            startActivity(intent);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                tvProviderName.setVisibility(View.INVISIBLE);
                tvProvider.setText(activeCheckInInfo.getProviderAccount().getBusinessName());

                tvProvider.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {
                            Intent intent = new Intent(MultipleAppointmentConfirmation.this, ProviderDetailActivity.class);
                            intent.putExtra("uniqueID", activeCheckInInfo.getProviderAccount().getUniqueId());
                            intent.putExtra("locationId", activeCheckInInfo.getLocation().getId());
                            startActivity(intent);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }


            // to set location
            if (activeCheckInInfo.getLocation() != null) {
                if (activeCheckInInfo.getLocation().getPlace() != null) {
                    tv_location.setText(activeCheckInInfo.getLocation().getPlace());
                    tv_location.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openMapView(activeCheckInInfo.getLocation().getLattitude(), activeCheckInInfo.getLocation().getLongitude(), activeCheckInInfo.getLocation().getPlace());
                        }
                    });
                }
            }

            // to set consumer name
            if (activeCheckInInfo.getAppmtFor() != null) {
                if (activeCheckInInfo.getAppmtFor().get(0).getUserName() != null) {
                    tv_consumer.setText(activeCheckInInfo.getAppmtFor().get(0).getUserName());
                } else {
                    tv_consumer.setText(activeCheckInInfo.getAppmtFor().get(0).getFirstName() + " " + activeCheckInInfo.getAppmtFor().get(0).getLastName());
                }
            }

            if (activeCheckInInfo.getService() != null) {
                tvServiceName.setText(activeCheckInInfo.getService().getName());

                if (activeCheckInInfo.getService().getServiceType() != null && activeCheckInInfo.getService().getServiceType().equalsIgnoreCase("virtualService")) {

                    if (activeCheckInInfo.getService().getVirtualCallingModes() != null) {
                        icon_service.setVisibility(View.VISIBLE);
                        if (activeCheckInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                            icon_service.setImageResource(R.drawable.zoom_meet);
                        } else if (activeCheckInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                            icon_service.setImageResource(R.drawable.google_meet);
                        } else if (activeCheckInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                            if (activeCheckInInfo.getService().getVirtualServiceType() != null && activeCheckInInfo.getService().getVirtualServiceType().equalsIgnoreCase("videoService")) {
                                icon_service.setImageResource(R.drawable.whatsapp_videoicon);
                            } else {
                                icon_service.setImageResource(R.drawable.whatsapp_icon);
                            }
                        } else if (activeCheckInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                            icon_service.setImageResource(R.drawable.phoneicon_sized);
                        } else if (activeCheckInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("VideoCall")) {
                            icon_service.setImageResource(R.drawable.ic_jaldeevideo);
                        }
                    } else {
                        icon_service.setVisibility(View.GONE);
                    }
                }

            }

            if (activeCheckInInfo.getAppointmentEncId() != null) {
                tvConfirmationNumber.setText(confirmIds
                        .stream()
                        .map(a -> String.valueOf(a.replaceAll(" ", "\u00a0")))
                        .collect(Collectors.joining(", ")));
            }

            // to set appointment date
            if (activeCheckInInfo.getAppmtDate() != null) {
                try {
                    tvDate.setText(getCustomDateString(activeCheckInInfo.getAppmtDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            // to set slot time
            if (activeCheckInInfo.getAppmtTime() != null) {
                tvTime.setText(times
                        .stream()
                        .map(a -> String.valueOf(a.replaceAll(" ", "\u00a0")))
                        .collect(Collectors.joining(", ")));
            }

            if (activeCheckInInfo.getBatchId() != null) {
                llBatchNo.setVisibility(View.VISIBLE);
                tvBatchNo.setText(activeCheckInInfo.getBatchId());
            } else {
                llBatchNo.setVisibility(View.GONE);
            }
            llMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        Intent intent = new Intent(MultipleAppointmentConfirmation.this, ChatActivity.class);
                        if (activeCheckInInfo.getUid().contains("h_")) {
                            uuid = activeCheckInInfo.getUid().replace("h_", "");
                            intent.putExtra("uuid", uuid);
                        } else {
                            intent.putExtra("uuid", activeCheckInInfo.getUid());
                        }
                        intent.putExtra("accountId", activeCheckInInfo.getProviderAccount().getId());
                        intent.putExtra("name", activeCheckInInfo.getProviderAccount().getBusinessName());
                        intent.putExtra("from", Constants.APPOINTMENT);
                        startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            ll_add_to_calendar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                    Date sTime = null;
                    Date eTime = null;
                    Calendar calSTime = Calendar.getInstance();
                    Calendar calETime = Calendar.getInstance();

                    try {
                        sTime = sdf1.parse(activeCheckInInfo.getAppmtDate() + " " + activeCheckInInfo.getAppmtTime().split("-")[0]);
                        eTime = sdf1.parse(activeCheckInInfo.getAppmtDate() + " " + activeCheckInInfo.getAppmtTime().split("-")[1]);
                        calSTime.setTime(sTime);
                        calETime.setTime(eTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                           /* ContentResolver cr = mContext.getContentResolver();
                            ContentValues cv = new ContentValues();
                            cv.put(CalendarContract.Events.TITLE, "jaldee booking - " + activeCheckInInfo.getAppointmentEncId());
                            cv.put(CalendarContract.Events.DESCRIPTION, "Service provider : " + activeCheckInInfo.getBusinessName() + "\u2028Location : " + activeCheckInInfo.getLocation().getPlace());
                            cv.put(CalendarContract.Events.EVENT_LOCATION, activeCheckInInfo.getLocation().getPlace());
                            cv.put(CalendarContract.Events.DTSTART, calSTime.getTimeInMillis());
                            cv.put(CalendarContract.Events.DTEND, calETime.getTimeInMillis());
                            cv.put(CalendarContract.Events.CALENDAR_ID, 3);
                            cv.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());
                            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, cv);*/


                    Intent intent = new Intent(Intent.ACTION_INSERT);
                    intent.setData(CalendarContract.Events.CONTENT_URI);
                    if (activeCheckInInfo.getProviderAccount().getBusinessName() != null && !activeCheckInInfo.getProviderAccount().getBusinessName().equalsIgnoreCase("")) {
                        intent.putExtra(CalendarContract.Events.TITLE, activeCheckInInfo.getProviderAccount().getBusinessName() +" - "+ activeCheckInInfo.getService().getName());//activeCheckInInfo.getCheckinEncId());
                    } else {
                        String name = activeCheckInInfo.getProvider().getFirstName() + " " + activeCheckInInfo.getProvider().getLastName();
                        intent.putExtra(CalendarContract.Events.TITLE, name +" - "+ activeCheckInInfo.getService().getName());// activeCheckInInfo.getCheckinEncId());
                    }
                    intent.putExtra(CalendarContract.Events.DESCRIPTION, "Time slots : " + times
                            .stream()
                            .map(a -> String.valueOf(a.replaceAll(" ", "\u00a0")))
                            .collect(Collectors.joining(", ")));
                    intent.putExtra(CalendarContract.Events.EVENT_LOCATION, activeCheckInInfo.getLocation().getPlace());
                    // intent.putExtra(CalendarContract.Events.DTSTART, calSTime.getTimeInMillis());
                    // intent.putExtra(CalendarContract.Events.DTEND, calETime.getTimeInMillis());
                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calSTime.getTimeInMillis());
                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calETime.getTimeInMillis());
                    intent.putExtra(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());
                    intent.putExtra(CalendarContract.Events.ALL_DAY, true);
                    //intent.putExtra(Intent.EXTRA_EMAIL, "test@yahoo.com, test2@yahoo.com, test3@yahoo.com");

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(MultipleAppointmentConfirmation.this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent home = new Intent(MultipleAppointmentConfirmation.this, Home.class);
                    startActivity(home);
                    finish();
                }
            });
        }
    }

    private void openMapView(String latitude, String longitude, String locationName) {
        Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + locationName);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        }
    }
}