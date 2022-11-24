package com.jaldeeinc.jaldee.activities;

import static com.jaldeeinc.jaldee.activities.BookingDetails.convertTime;
import static com.jaldeeinc.jaldee.activities.BookingDetails.getCustomDateString;
import static com.jaldeeinc.jaldee.connection.ApiClient.context;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CircleTransform;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;
import com.jaldeeinc.jaldee.response.ActiveAppointment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    boolean livetrack;
    List<String> uids;
    ActiveAppointment activeCheckInInfo = new ActiveAppointment();
    List<String> confirmIds = new ArrayList<>();
    List<String> times = new ArrayList<>();
    CardView cvShare, btnOk;
    private String uuid, typeOfService, serviceDescription, countryCode, phoneNumber, email, providerLogoUrl, providerId;
    TextView tv_email, tv_phoneNumber, tv_description, tv_datehint, tv_timeHint, tv_cnsmrDetails_Heading, tvProviderName1, tvProviderName,
            tvServiceName, tvProvider, tvConfirmationNumber, tvDate, tvTime, tvBatchNo, tvServiceName1, tvProvider1,
            tvConsumerName, tvLocation, tvStatus, tvAmount, tv_postInfoTitle, tv_postInfo, icon_text, tvTokenWaitTime, tv_token_number;
    ImageView iv_location_icon, iv_prvdr_phone_icon, iv_prvdr_email_icon, ivQR, iv_serviceIcon, icon_service;
    LinearLayout ll_token, ll_status, llPayment, ll_postinfo, ll_tokenWaitTime, llBatchNo, llMessage, ll_add_to_calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_confirmation);
        mContext = this;
        initializations();

        Intent i = getIntent();
        serviceDescription = i.getStringExtra("serviceDescription");
        typeOfService = i.getStringExtra("typeOfService");
        //terminology = i.getStringExtra("terminology");
        //from = i.getStringExtra("from");
        countryCode = i.getStringExtra("waitlistPhonenumberCountryCode");
        phoneNumber = i.getStringExtra("waitlistPhonenumber");
        email = i.getStringExtra("email");
        livetrack = i.getBooleanExtra("livetrack", false);
        providerId = i.getStringExtra("accountID");
        uids = i.getStringArrayListExtra("uids");
        providerLogoUrl = i.getStringExtra("providerLogoUrl");
        if (providerLogoUrl != null && !providerLogoUrl.trim().isEmpty()) {
            PicassoTrustAll.getInstance(mContext).load(providerLogoUrl).placeholder(R.drawable.service_avatar).error(R.drawable.service_avatar).transform(new CircleTransform()).fit().into(iv_serviceIcon);
        }

        if (email != null && !email.trim().isEmpty()) {
            tv_email.setText(email);
        }
        if (phoneNumber != null && countryCode != null) {
            tv_phoneNumber.setText(countryCode + " " + phoneNumber);
        }
        if (serviceDescription != null && !serviceDescription.trim().isEmpty()) {
            tv_description.setVisibility(View.VISIBLE);
            tv_description.setText(serviceDescription);
        }
        Glide.with(mContext).load(R.drawable.location_icon_1).into(iv_location_icon);
        Glide.with(mContext).load(R.drawable.phone_icon_1).into(iv_prvdr_phone_icon);
        Glide.with(mContext).load(R.drawable.email_icon_1).into(iv_prvdr_email_icon);
        ll_token.setVisibility(View.GONE);
        tv_datehint.setText("Date");
        tv_timeHint.setText("Time");
        tv_cnsmrDetails_Heading.setText("Booking For");
        getConfirmationDetails(providerId, uids);

    }

    private void initializations() {
        /*cvBack = findViewById(R.id.cv_back);
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
        llMessage = findViewById(R.id.ll_message);*/

        tvProviderName = findViewById(R.id.tv_providerName);
        tvProviderName1 = findViewById(R.id.tv_providerName1);
        tvServiceName = findViewById(R.id.tv_serviceName);
        tvServiceName1 = findViewById(R.id.tv_serviceName1);
        tvProvider = findViewById(R.id.tv_doctorName);
        tvProvider1 = findViewById(R.id.tv_doctorName1);
        tvConfirmationNumber = findViewById(R.id.tv_confirmationNumber);
        btnOk = findViewById(R.id.cv_ok);
        icon_service = findViewById(R.id.iv_teleService);
        tvConsumerName = findViewById(R.id.tv_consumerName);
        tvLocation = findViewById(R.id.tv_locationName);
        tvStatus = findViewById(R.id.tv_status);
        ll_status = findViewById(R.id.ll_status);
        tvDate = findViewById(R.id.tv_date);
        tvTime = findViewById(R.id.tv_time);
        llPayment = findViewById(R.id.ll_payment);
        tvAmount = findViewById(R.id.tv_amount);
        cvShare = findViewById(R.id.cv_share);
        ivQR = findViewById(R.id.iv_Qr);
        llMessage = findViewById(R.id.ll_message);
        ll_add_to_calendar = findViewById(R.id.ll_add_to_calendar);
        tv_datehint = findViewById(R.id.tv_datehint);
        tv_description = findViewById(R.id.tv_description);
        tv_cnsmrDetails_Heading = findViewById(R.id.tv_cnsmrDetails_Heading);
        tv_phoneNumber = findViewById(R.id.tv_phoneNumber);
        tv_email = findViewById(R.id.tv_email);
        ll_postinfo = findViewById(R.id.ll_postinfo);
        tv_postInfoTitle = findViewById(R.id.tv_postInfoTitle);
        tv_postInfo = findViewById(R.id.tv_postInfo);
        iv_location_icon = findViewById(R.id.iv_location_icon);
        iv_prvdr_phone_icon = findViewById(R.id.iv_prvdr_phone_icon);
        iv_prvdr_email_icon = findViewById(R.id.iv_prvdr_email_icon);
        icon_text = findViewById(R.id.icon_text);
        iv_serviceIcon = findViewById(R.id.iv_serviceIcon);
        //checkin
        tv_timeHint = findViewById(R.id.tv_timehint);
        ll_tokenWaitTime = findViewById(R.id.ll_tokenWaitTime);
        tvTokenWaitTime = findViewById(R.id.tv_tokenWaitTime);
        ll_token = findViewById(R.id.ll_token);
        tv_token_number = findViewById(R.id.tv_token_number);

        //appt
        tvBatchNo = findViewById(R.id.tv_batchNo);
        llBatchNo = findViewById(R.id.ll_batch);

    }

    @Override
    public void onBackPressed() {
        Intent home = new Intent(mContext, Home.class);
        startActivity(home);
        finish();
        super.onBackPressed();
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

                    tvProvider1.setText(activeCheckInInfo.getProvider().getBusinessName());
                    tvProvider1.setVisibility(View.VISIBLE);
                } else {
                    String name = activeCheckInInfo.getProvider().getFirstName() + " " + activeCheckInInfo.getProvider().getLastName();
                    tvProvider.setText(name);

                    tvProvider1.setText(name);
                    tvProvider1.setVisibility(View.VISIBLE);
                }
                tvProviderName1.setText(activeCheckInInfo.getProviderAccount().getBusinessName());
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
                tvProviderName.setVisibility(View.GONE);
                tvProviderName1.setVisibility(View.GONE);
                tvProvider1.setVisibility(View.VISIBLE);
                tvProvider1.setText(activeCheckInInfo.getProviderAccount().getBusinessName());
                tvProvider1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                tvProvider.setVisibility(View.VISIBLE);
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
                    tvLocation.setText(activeCheckInInfo.getLocation().getPlace());
                    tvLocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openMapView(activeCheckInInfo.getLocation().getLattitude(), activeCheckInInfo.getLocation().getLongitude(), activeCheckInInfo.getLocation().getPlace());
                        }
                    });
                }
            }

            // to set consumer name
            if (activeCheckInInfo.getAppmtFor() != null) {
                String fName, lName;
                if (activeCheckInInfo.getAppmtFor().get(0).getUserName() != null) {
                    fName = activeCheckInInfo.getAppmtFor().get(0).getUserName();
                    if (fName != null && !fName.trim().isEmpty()) {
                        icon_text.setText(String.valueOf(fName.trim().charAt(0)));
                    }
                    tvConsumerName.setText(activeCheckInInfo.getAppmtFor().get(0).getUserName());
                } else {
                    fName = activeCheckInInfo.getAppmtFor().get(0).getFirstName();
                    lName = activeCheckInInfo.getAppmtFor().get(0).getLastName();
                    if (fName != null && !fName.trim().isEmpty()) {
                        icon_text.setText(String.valueOf(fName.trim().charAt(0)));
                    } else if (lName != null && !lName.trim().isEmpty()) {
                        icon_text.setText(String.valueOf(lName.trim().charAt(0)));
                    }
                    tvConsumerName.setText(fName + " " + lName);
                }
            }

            if (activeCheckInInfo.getService() != null) {
                ivQR.setVisibility(View.GONE);
                /********************************/
                String sName = activeCheckInInfo.getService().getName();
                String append = " Booked";

                tvServiceName.setText(sName + append);
                tvServiceName1.setText(sName);
                /********************************/


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

            // to set confirmation number
            if (activeCheckInInfo.getAppointmentEncId() != null) {
                tvConfirmationNumber.setText(confirmIds
                        .stream()
                        .map(a -> String.valueOf(a.replaceAll(" ", "\n")))
                        .collect(Collectors.joining(", ")));
            }

            // to set status
            ll_status.setVisibility(View.GONE);
            tvStatus.setVisibility(View.GONE);

            // to set paid info
            llPayment.setVisibility(View.GONE);

            // to set appointment date
            if (activeCheckInInfo.getAppmtDate() != null) {
                try {
                    tvDate.setText(getCustomDateString(activeCheckInInfo.getAppmtDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            // hide instructions link when there are no post instructions
            ll_postinfo.setVisibility(View.GONE);


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

            cvShare.setVisibility(View.GONE);

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
                        intent.putExtra(CalendarContract.Events.TITLE, activeCheckInInfo.getProviderAccount().getBusinessName() + " - " + activeCheckInInfo.getService().getName());//activeCheckInInfo.getCheckinEncId());
                    } else {
                        String name = activeCheckInInfo.getProvider().getFirstName() + " " + activeCheckInInfo.getProvider().getLastName();
                        intent.putExtra(CalendarContract.Events.TITLE, name + " - " + activeCheckInInfo.getService().getName());// activeCheckInInfo.getCheckinEncId());
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