package com.jaldeeinc.jaldee.activities;

import static com.jaldeeinc.jaldee.activities.BookingDetails.convertTime;
import static com.jaldeeinc.jaldee.activities.CheckInDetails.convertToTitleForm;
import static com.jaldeeinc.jaldee.activities.CheckInDetails.getCustomDateString;
import static com.jaldeeinc.jaldee.connection.ApiClient.context;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.Contents;
import com.jaldeeinc.jaldee.custom.QRCodeEncoder;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingConfirmation extends AppCompatActivity {

    Context mContext;
    boolean livetrack;
    ActiveCheckIn activeCheckInInfo = new ActiveCheckIn();
    ActiveAppointment activeAppointmentInfo = new ActiveAppointment();
    ImageView icon_service, ivQR, iv_location_icon, iv_prvdr_phone_icon, iv_prvdr_email_icon, iv_serviceIcon, iv_success_icon;
    CardView btnOk, cvShare;
    LinearLayout ll_postinfo, llPayment, ll_status, llBatchNo, llMessage, ll_add_to_calendar, ll_tokenWaitTime, ll_token, ll_time, ll_date;
    TextView tvProviderName, tvProviderName1, tvServiceName, tvServiceName1, tvProvider, tvProvider1, tvConfirmationNumber,
            tvLocation, tv_description, tv_cnsmrDetails_Heading, tv_phoneNumber, tv_email, tv_postInfoTitle, tv_postInfo, tvConsumerName,
            tvAmount, tvDate, tv_datehint, tvTime, tv_timeHint, tvTokenWaitTime, tvStatus, tvBatchNo, tv_token_number, icon_text;
    private String from, typeOfService, uuid, countryCode, phoneNumber, providerId, email, value, serviceDescription, terminology, providerLogoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_confirmation);
        mContext = this;
        initializations();

        Intent i = getIntent();
        serviceDescription = i.getStringExtra("serviceDescription");
        typeOfService = i.getStringExtra("typeOfService");
        terminology = i.getStringExtra("terminology");
        from = i.getStringExtra("from");
        countryCode = i.getStringExtra("waitlistPhonenumberCountryCode");
        phoneNumber = i.getStringExtra("waitlistPhonenumber");
        email = i.getStringExtra("email");
        livetrack = i.getBooleanExtra("livetrack", false);
        providerId = i.getStringExtra("accountID");
        value = i.getStringExtra("uid");
        providerLogoUrl = i.getStringExtra("providerLogoUrl");

        if (providerLogoUrl != null && !providerLogoUrl.trim().isEmpty()) {
            Glide.with(mContext).load(providerLogoUrl).placeholder(R.drawable.service_avatar).error(R.drawable.service_avatar).circleCrop().into(iv_serviceIcon);

           // PicassoTrustAll.getInstance(mContext).load(providerLogoUrl).placeholder(R.drawable.service_avatar).error(R.drawable.service_avatar).transform(new CircleTransform()).fit().into(iv_serviceIcon);
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

        if (typeOfService != null && !typeOfService.trim().isEmpty()) {
            if (typeOfService.equalsIgnoreCase(Constants.APPOINTMENT)) {
                activeAppointmentInfo = (ActiveAppointment) i.getSerializableExtra("BookingDetails");
                appointment();
            } else if (typeOfService.equalsIgnoreCase(Constants.CHECKIN)) {
                activeCheckInInfo = (ActiveCheckIn) i.getSerializableExtra("BookingDetails");
                checkin();
            } else if (typeOfService.equalsIgnoreCase(Constants.DONATION)) {
                donation();
            }
        }
    }

    private void checkin() {
        ll_token.setVisibility(View.VISIBLE);
        tv_datehint.setText("Date & Time-window");
        llBatchNo.setVisibility(View.GONE);
        tv_cnsmrDetails_Heading.setText("Booking For");
        if (value != null && providerId != null) {
            getCheckinConfirmationDetails(Integer.parseInt(providerId));
        }
    }

    private void appointment() {
        ll_token.setVisibility(View.GONE);
        tv_datehint.setText("Date");
        tv_timeHint.setText("Time");
        tv_cnsmrDetails_Heading.setText("Booking For");

        if (value != null && providerId != null) {
            getAppointmentConfirmationDetails(Integer.parseInt(providerId));
        }
    }

    private void donation() {

        ll_token.setVisibility(View.GONE);
        tv_cnsmrDetails_Heading.setText("Payment By");
    }

    private void initializations() {
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
        ll_time = findViewById(R.id.ll_time);
        ll_date = findViewById(R.id.ll_date);
        iv_success_icon = findViewById(R.id.iv_success_icon);

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void UpdateCheckinMainUI(ActiveCheckIn activeCheckInInfo) throws ParseException {


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
                            Intent intent = new Intent(BookingConfirmation.this, ProviderDetailActivity.class);
                            intent.putExtra("uniqueID", activeCheckInInfo.getProviderAccount().getUniqueId());
                            intent.putExtra("locationId", activeCheckInInfo.getQueue().getLocation().getId());
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
                            Intent intent = new Intent(BookingConfirmation.this, ProviderDetailActivity.class);
                            intent.putExtra("uniqueID", activeCheckInInfo.getProviderAccount().getUniqueId());
                            intent.putExtra("locationId", activeCheckInInfo.getQueue().getLocation().getId());
                            startActivity(intent);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            // to set location
            if (activeCheckInInfo.getQueue() != null && activeCheckInInfo.getQueue().getLocation() != null) {
                if (activeCheckInInfo.getQueue().getLocation().getPlace() != null) {
                    tvLocation.setText(activeCheckInInfo.getQueue().getLocation().getPlace());
                    tvLocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openMapView(activeCheckInInfo.getQueue().getLocation().getLattitude(), activeCheckInInfo.getQueue().getLocation().getLongitude(), activeCheckInInfo.getQueue().getLocation().getPlace());
                        }
                    });
                }
            }

            // to set consumer name
            if (activeCheckInInfo.getWaitlistingFor() != null) {
                String fName = activeCheckInInfo.getWaitlistingFor().get(0).getFirstName();
                String lName = activeCheckInInfo.getWaitlistingFor().get(0).getLastName();
                if (fName != null && !fName.trim().isEmpty()) {
                    icon_text.setText(String.valueOf(fName.trim().charAt(0)));
                } else if (lName != null && !lName.trim().isEmpty()) {
                    icon_text.setText(String.valueOf(lName.trim().charAt(0)));
                }
                tvConsumerName.setText(fName + " " + lName);
            }

            if (activeCheckInInfo.getCheckinEncId() != null) {
                //Encode with a QR Code image
                String statusUrl = Constants.URL + "status/" + activeCheckInInfo.getCheckinEncId();

                WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                Point point = new Point();
                display.getSize(point);
                int width = point.x;
                int height = point.y;
                int smallerDimension = width < height ? width : height;
                smallerDimension = smallerDimension * 3 / 4;

                QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(statusUrl,
                        null,
                        Contents.Type.TEXT,
                        BarcodeFormat.QR_CODE.toString(), smallerDimension);
                try {
                    Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
                    ivQR.setImageBitmap(bitmap);

                    ivQR.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Dialog settingsDialog = new Dialog(BookingConfirmation.this);
                            settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.image_layout, null));
                            LinearLayout ll_download_qr = settingsDialog.findViewById(R.id.ll_download_qr);

                            ImageView imageView = settingsDialog.findViewById(R.id.iv_close);
                            ImageView ivQR = settingsDialog.findViewById(R.id.iv_Qr);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    settingsDialog.dismiss();
                                }
                            });
                            ll_download_qr.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions((Activity) view.getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                        // this will request for permission when permission is not true
                                    } else {
                                        storeImage(bitmap);
                                    }
                                }
                            });
                            Glide.with(context).load(bitmap).into(ivQR);
                            settingsDialog.show();
                        }
                    });


                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }

            if (activeCheckInInfo.getService() != null) {

                /********************************/
                String sName = activeCheckInInfo.getService().getName();
                String append = "";
                if (from != null && activeCheckInInfo != null) {
                    if (from.equalsIgnoreCase(Constants.RESCHEDULE)) {
                        if (activeCheckInInfo.getShowToken() != null && activeCheckInInfo.getShowToken().equalsIgnoreCase("true")) {
                            append = " Rescheduled";
                        } else {
                            append = " Rescheduled";
                        }
                    } else {
                        append = " Booked";
                    }
                }
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
            if (activeCheckInInfo.getCheckinEncId() != null) {
                tvConfirmationNumber.setText(activeCheckInInfo.getCheckinEncId());
            }

            // to set status
            if (activeCheckInInfo.getWaitlistStatus() != null) {
                tvStatus.setVisibility(View.VISIBLE);
                if (activeCheckInInfo.getWaitlistStatus().equalsIgnoreCase("Cancelled")) {
                    tvStatus.setTextColor(mContext.getResources().getColor(R.color.red));

                } else {
                    tvStatus.setTextColor(mContext.getResources().getColor(R.color.blue5));
                }
                tvStatus.setText(convertToTitleForm(activeCheckInInfo.getWaitlistStatus()));
            } else {
                ll_status.setVisibility(View.GONE);
                tvStatus.setVisibility(View.GONE);
            }

            // to set paid info
            if (activeCheckInInfo.getAmountPaid() != 0) {
                llPayment.setVisibility(View.VISIBLE);
                tvAmount.setText("₹" + Config.getAmountNoOrTwoDecimalPoints(activeCheckInInfo.getAmountPaid()));
            } else {

                llPayment.setVisibility(View.GONE);
            }


            // to set appointment date
            if (activeCheckInInfo.getDate() != null && activeCheckInInfo.getQueue() != null) {
                try {
                    tvDate.setText(getCustomDateString(activeCheckInInfo.getDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String time = activeCheckInInfo.getQueue().getQueueStartTime() + " - " + activeCheckInInfo.getQueue().getQueueEndTime();
                tvTime.setText(time);
            }

            // hide instructions link when there are no post instructions
            if (activeCheckInInfo.getService() != null && activeCheckInInfo.getService().isPostInfoEnabled()
                    && ((activeCheckInInfo.getService().getPostInfoText() != null && !activeCheckInInfo.getService().getPostInfoText().trim().isEmpty())
                    || (activeCheckInInfo.getService().getPostInfoTitle() != null && !activeCheckInInfo.getService().getPostInfoTitle().trim().isEmpty()))) {
                ll_postinfo.setVisibility(View.VISIBLE);
                if (activeCheckInInfo.getService().getPostInfoTitle() != null && !activeCheckInInfo.getService().getPostInfoTitle().trim().isEmpty()) {
                    tv_postInfoTitle.setText(activeCheckInInfo.getService().getPostInfoTitle());
                } else {
                    ll_postinfo.setVisibility(View.GONE);
                }
                if (activeCheckInInfo.getService().getPostInfoText() != null && !activeCheckInInfo.getService().getPostInfoText().trim().isEmpty()) {
                    tv_postInfo.setText((Html.fromHtml(activeCheckInInfo.getService().getPostInfoText())));
                } else {
                    ll_postinfo.setVisibility(View.GONE);
                }
            } else {
                ll_postinfo.setVisibility(View.GONE);
            }


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date checkinDate = sdf.parse(activeCheckInInfo.getDate());
            Date today = sdf.parse(LocalDateTime.now().toString());
            // to set waitTime or token No with waitTime
            if (activeCheckInInfo.getShowToken() != null && activeCheckInInfo.getShowToken().equalsIgnoreCase("true")) {

                if (activeCheckInInfo.getCalculationMode() != null && !activeCheckInInfo.getCalculationMode().equalsIgnoreCase("NoCalc")) {
                    ll_token.setVisibility(View.VISIBLE);
                    tv_token_number.setText(String.valueOf(activeCheckInInfo.getToken()));
                    ll_tokenWaitTime.setVisibility(View.VISIBLE);
                    tvTokenWaitTime.setVisibility(View.VISIBLE);
                    if (checkinDate.after(today)) {  //future upcomming checkin/token service time
                        tvTokenWaitTime.setText("Starts at : " + (activeCheckInInfo.getServiceTime()));

                    } else {
                        tvTokenWaitTime.setText("Estimated waiting time : " + Config.getTimeinHourMinutes(activeCheckInInfo.getAppxWaitingTime()));
                    }

                } else {
                    ll_token.setVisibility(View.VISIBLE);
                    tv_token_number.setText(String.valueOf(activeCheckInInfo.getToken()));
                    tvTokenWaitTime.setVisibility(View.GONE);
                    ll_tokenWaitTime.setVisibility(View.GONE);
                }
            } else {
                ll_tokenWaitTime.setVisibility(View.VISIBLE);
                tvTokenWaitTime.setVisibility(View.VISIBLE);
                if (checkinDate.after(today)) {    //future upcomming checkin/token service time
                    tvTokenWaitTime.setText("Starts at : " + (activeCheckInInfo.getServiceTime()));
                } else {
                    tvTokenWaitTime.setText("Estimated waiting time : " + Config.getTimeinHourMinutes(activeCheckInInfo.getAppxWaitingTime()));
                }
            }


            cvShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (activeCheckInInfo != null && activeCheckInInfo.getCheckinEncId() != null) {
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("text/html");
                        String statusUrl = Constants.URL + "status/" + activeCheckInInfo.getCheckinEncId();
                        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Share your CheckIn/Token status link");
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, statusUrl);
                        startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    }
                }
            });

            llMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        Intent intent = new Intent(BookingConfirmation.this, ChatActivity.class);
                        if (activeCheckInInfo.getYnwUuid().contains("h_")) {
                            uuid = activeCheckInInfo.getYnwUuid().replace("h_", "");
                            intent.putExtra("uuid", uuid);
                        } else {
                            intent.putExtra("uuid", activeCheckInInfo.getYnwUuid());
                        }
                        intent.putExtra("accountId", activeCheckInInfo.getProviderAccount().getId());
                        intent.putExtra("name", activeCheckInInfo.getProviderAccount().getBusinessName());
                        intent.putExtra("from", Constants.CHECKIN);
                        startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            ll_add_to_calendar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
                    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date sTime = null;
                    Date eTime = null;
                    Calendar calSTime = Calendar.getInstance();
                    Calendar calETime = Calendar.getInstance();

                    try {
                        String d1 = sdf.format(df.parse(activeCheckInInfo.getDate() + " " + activeCheckInInfo.getQueue().getQueueStartTime()));
                        String d2 = sdf.format(df.parse(activeCheckInInfo.getDate() + " " + activeCheckInInfo.getQueue().getQueueEndTime()));
                        sTime = sdf.parse(d1);
                        eTime = sdf.parse(d2);
                        calSTime.setTime(sTime);
                        calETime.setTime(eTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(Intent.ACTION_INSERT);
                    intent.setData(CalendarContract.Events.CONTENT_URI);
                    if (activeCheckInInfo.getProviderAccount().getBusinessName() != null && !activeCheckInInfo.getProviderAccount().getBusinessName().equalsIgnoreCase("")) {
                        intent.putExtra(CalendarContract.Events.TITLE, activeCheckInInfo.getProviderAccount().getBusinessName() + " - " + activeCheckInInfo.getService().getName());//activeCheckInInfo.getCheckinEncId());
                        intent.putExtra(CalendarContract.Events.DESCRIPTION, "Service provider : " + activeCheckInInfo.getProviderAccount().getBusinessName() + "\nLocation : " + activeCheckInInfo.getQueue().getLocation().getPlace());
                    } else {
                        String name = activeCheckInInfo.getProvider().getFirstName() + " " + activeCheckInInfo.getProvider().getLastName();
                        intent.putExtra(CalendarContract.Events.TITLE, name + " - " + activeCheckInInfo.getService().getName());// activeCheckInInfo.getCheckinEncId());

                        intent.putExtra(CalendarContract.Events.DESCRIPTION, "Service provider : " + name + "\nLocation : " + activeCheckInInfo.getQueue().getLocation().getPlace());
                    }
                    intent.putExtra(CalendarContract.Events.EVENT_LOCATION, activeCheckInInfo.getQueue().getLocation().getPlace());
                    //intent.putExtra(CalendarContract.Events.DTSTART, calSTime.getTime());
                    //intent.putExtra(CalendarContract.Events.DTEND, calETime.getTime());
                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calSTime.getTimeInMillis());
                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calETime.getTimeInMillis());
                    intent.putExtra(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());
                    //intent.putExtra(CalendarContract.Events.ALL_DAY, false);
                    //intent.putExtra(Intent.EXTRA_EMAIL, "test@yahoo.com, test2@yahoo.com, test3@yahoo.com");

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(BookingConfirmation.this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (livetrack) {
                        Intent checkinShareLocations = new Intent(mContext, CheckinShareLocation.class);
                        checkinShareLocations.putExtra("waitlistPhonenumber", phoneNumber);
                        checkinShareLocations.putExtra("uuid", activeCheckInInfo.getYnwUuid());
                        checkinShareLocations.putExtra("accountID", providerId);
                        checkinShareLocations.putExtra("title", activeCheckInInfo.getProviderAccount().getBusinessName());
                        checkinShareLocations.putExtra("terminology", terminology);
                        checkinShareLocations.putExtra("calcMode", activeCheckInInfo.getCalculationMode());
                        checkinShareLocations.putExtra("queueStartTime", "");
                        checkinShareLocations.putExtra("queueEndTime", "");
                        checkinShareLocations.putExtra("from", Constants.CHECKIN);
                        startActivity(checkinShareLocations);
                    } else {
                        Intent home = new Intent(BookingConfirmation.this, Home.class);
                        startActivity(home);
                        finish();
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent home = new Intent(mContext, Home.class);
        startActivity(home);
        finish();
        super.onBackPressed();
    }

    private void getCheckinConfirmationDetails(int id) {
        final Dialog mDialog = Config.getProgressDialog(BookingConfirmation.this, BookingConfirmation.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<ActiveCheckIn> call = apiService.getActiveCheckInUUID(value, String.valueOf(id));
        call.enqueue(new Callback<ActiveCheckIn>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<ActiveCheckIn> call, Response<ActiveCheckIn> response) {
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
                try {
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        activeCheckInInfo = response.body();
                        UpdateCheckinMainUI(activeCheckInInfo);
                    }
                } catch (Exception e) {
                    Log.i("mnbbnmmnbbnm", e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ActiveCheckIn> call, Throwable t) {
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });
    }

    private void storeImage(Bitmap image) {
        File pictureFile = null;
        try {
            pictureFile = Config.createFile(context, "png", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (pictureFile == null) {
            //"Error creating media file, check storage permissions: "
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
            Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show();

           /* Uri uri = Uri.fromFile(pictureFile);
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String path = uri.getPath();
            String extension = path.substring(path.lastIndexOf("."));;
            String type = mime.getMimeTypeFromExtension(extension);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, type);*/

        } catch (FileNotFoundException e) {
            //Log.d(TAG, "File not found: " + e.getMessage());
            e.printStackTrace();

        } catch (IOException e) {
            //Log.d(TAG, "Error accessing file: " + e.getMessage());
            e.printStackTrace();

        }
    }

    //Appointment
    private void getAppointmentConfirmationDetails(int userId) {
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<ActiveAppointment> call = apiService.getActiveAppointmentUUID(value, String.valueOf(userId));
        call.enqueue(new Callback<ActiveAppointment>() {
            @Override
            public void onResponse(Call<ActiveAppointment> call, Response<ActiveAppointment> response) {
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
                try {
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        activeAppointmentInfo = response.body();
                        UpdateAppointmentMainUI(activeAppointmentInfo);

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

    public void UpdateAppointmentMainUI(ActiveAppointment activeAppointmentInfo) {

        if (activeAppointmentInfo != null) {

            if (activeAppointmentInfo.getProvider() != null) {

                if (activeAppointmentInfo.getProvider().getBusinessName() != null && !activeAppointmentInfo.getProvider().getBusinessName().equalsIgnoreCase("")) {
                    tvProvider.setText(activeAppointmentInfo.getProvider().getBusinessName());

                    tvProvider1.setText(activeAppointmentInfo.getProvider().getBusinessName());
                    tvProvider1.setVisibility(View.VISIBLE);
                } else {
                    String name = activeAppointmentInfo.getProvider().getFirstName() + " " + activeAppointmentInfo.getProvider().getLastName();
                    tvProvider.setText(name);

                    tvProvider1.setText(name);
                    tvProvider1.setVisibility(View.VISIBLE);
                }
                tvProviderName1.setText(activeAppointmentInfo.getProviderAccount().getBusinessName());
                tvProviderName.setVisibility(View.VISIBLE);
                tvProviderName.setText(activeAppointmentInfo.getProviderAccount().getBusinessName());
                tvProviderName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {
                            Intent intent = new Intent(mContext, ProviderDetailActivity.class);
                            intent.putExtra("uniqueID", activeAppointmentInfo.getProviderAccount().getUniqueId());
                            intent.putExtra("locationId", activeAppointmentInfo.getLocation().getId());
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
                tvProvider1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                tvProvider1.setText(activeAppointmentInfo.getProviderAccount().getBusinessName());
                tvProvider.setVisibility(View.VISIBLE);
                tvProvider.setText(activeAppointmentInfo.getProviderAccount().getBusinessName());
                tvProvider.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {
                            Intent intent = new Intent(mContext, ProviderDetailActivity.class);
                            intent.putExtra("uniqueID", activeAppointmentInfo.getProviderAccount().getUniqueId());
                            intent.putExtra("locationId", activeAppointmentInfo.getLocation().getId());
                            startActivity(intent);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            // to set location
            if (activeAppointmentInfo.getLocation() != null) {
                if (activeAppointmentInfo.getLocation().getPlace() != null) {
                    tvLocation.setText(activeAppointmentInfo.getLocation().getPlace());
                    tvLocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openMapView(activeAppointmentInfo.getLocation().getLattitude(), activeAppointmentInfo.getLocation().getLongitude(), activeAppointmentInfo.getLocation().getPlace());
                        }
                    });
                }
            }

            // to set consumer name
            if (activeAppointmentInfo.getAppmtFor() != null) {
                String fName, lName;
                if (activeAppointmentInfo.getAppmtFor().get(0).getUserName() != null) {
                    fName = activeAppointmentInfo.getAppmtFor().get(0).getUserName();
                    if (fName != null && !fName.trim().isEmpty()) {
                        icon_text.setText(String.valueOf(fName.trim().charAt(0)));
                    }
                    tvConsumerName.setText(fName);
                } else {
                    fName = activeAppointmentInfo.getAppmtFor().get(0).getFirstName();
                    lName = activeAppointmentInfo.getAppmtFor().get(0).getLastName();
                    if (fName != null && !fName.trim().isEmpty()) {
                        icon_text.setText(String.valueOf(fName.trim().charAt(0)));
                    } else if (lName != null && !lName.trim().isEmpty()) {
                        icon_text.setText(String.valueOf(lName.trim().charAt(0)));
                    }
                    tvConsumerName.setText(fName + " " + lName);
                }
            }

            if (activeAppointmentInfo.getAppointmentEncId() != null) {
                //Encode with a QR Code image
                String statusUrl = Constants.URL + "status/" + activeAppointmentInfo.getAppointmentEncId();

                WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                Point point = new Point();
                display.getSize(point);
                int width = point.x;
                int height = point.y;
                int smallerDimension = width < height ? width : height;
                smallerDimension = smallerDimension * 3 / 4;

                QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(statusUrl,
                        null,
                        Contents.Type.TEXT,
                        BarcodeFormat.QR_CODE.toString(), smallerDimension);
                try {
                    Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
                    ivQR.setImageBitmap(bitmap);

                    ivQR.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Dialog settingsDialog = new Dialog(mContext);
                            settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.image_layout, null));
                            LinearLayout ll_download_qr = settingsDialog.findViewById(R.id.ll_download_qr);

                            ImageView imageView = settingsDialog.findViewById(R.id.iv_close);
                            ImageView ivQR = settingsDialog.findViewById(R.id.iv_Qr);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    settingsDialog.dismiss();
                                }
                            });
                            ll_download_qr.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions((Activity) view.getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                        // this will request for permission when permission is not true
                                    } else {
                                        storeImage(bitmap);
                                    }
                                }
                            });
                            Glide.with(context).load(bitmap).into(ivQR);
                            settingsDialog.show();
                        }
                    });


                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
            if (activeAppointmentInfo.getApptStatus().equalsIgnoreCase(Constants.REQUESTED)) {
                Glide.with(mContext).load(R.drawable.icon_message_avatar).into(iv_success_icon);

            } else {
                Glide.with(mContext).load(R.drawable.icon_green_confirm_tick).into(iv_success_icon);

            }
            if (activeAppointmentInfo.getService() != null) {

                /********************************/
                String sName = activeAppointmentInfo.getService().getName();

                if (activeAppointmentInfo.getApptStatus().equalsIgnoreCase(Constants.REQUESTED)) {
                    tvServiceName.setText("Request Sent Successfully");
                    tvServiceName.setTextColor(mContext.getResources().getColor(R.color.green4));

                } else {
                    String append = "";
                    if (from != null && activeAppointmentInfo != null) {

                        if (from.equalsIgnoreCase(Constants.RESCHEDULE)) {
                            append = " Rescheduled";
                        } else {
                            append = " Booked";
                        }

                    }
                    tvServiceName.setText(sName + append);
                }
                tvServiceName1.setText(sName);

                /********************************/

                if (activeAppointmentInfo.getService().getServiceType() != null && activeAppointmentInfo.getService().getServiceType().equalsIgnoreCase("virtualService")) {

                    if (activeAppointmentInfo.getService().getVirtualCallingModes() != null) {
                        icon_service.setVisibility(View.VISIBLE);
                        if (activeAppointmentInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                            icon_service.setImageResource(R.drawable.zoom_meet);
                        } else if (activeAppointmentInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                            icon_service.setImageResource(R.drawable.google_meet);
                        } else if (activeAppointmentInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                            if (activeAppointmentInfo.getService().getVirtualServiceType() != null && activeAppointmentInfo.getService().getVirtualServiceType().equalsIgnoreCase("videoService")) {
                                icon_service.setImageResource(R.drawable.whatsapp_videoicon);
                            } else {
                                icon_service.setImageResource(R.drawable.whatsapp_icon);
                            }
                        } else if (activeAppointmentInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                            icon_service.setImageResource(R.drawable.phoneicon_sized);
                        } else if (activeAppointmentInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("VideoCall")) {
                            icon_service.setImageResource(R.drawable.ic_jaldeevideo);
                        }
                    } else {
                        icon_service.setVisibility(View.GONE);
                    }
                }
            }

            // to set confirmation number
            if (activeAppointmentInfo.getAppointmentEncId() != null) {
                tvConfirmationNumber.setText(activeAppointmentInfo.getAppointmentEncId());
            }

            // to set status
            if (activeAppointmentInfo.getApptStatus() != null) {
                tvStatus.setVisibility(View.VISIBLE);
                if (activeAppointmentInfo.getApptStatus().equalsIgnoreCase("Cancelled")) {
                    tvStatus.setTextColor(mContext.getResources().getColor(R.color.red));

                } else if (activeAppointmentInfo.getApptStatus().equalsIgnoreCase(Constants.REQUESTED)) {
                    tvStatus.setTextColor(mContext.getResources().getColor(R.color.orange));

                } else {
                    tvStatus.setTextColor(mContext.getResources().getColor(R.color.location_theme));
                }
                tvStatus.setText(BookingDetails.convertToTitleForm(activeAppointmentInfo.getApptStatus()));
            } else {
                ll_status.setVisibility(View.GONE);
                tvStatus.setVisibility(View.GONE);
            }
            if (activeAppointmentInfo.getApptStatus().equalsIgnoreCase(Constants.REQUESTED)) {  //visibility set as GONE for appointment requeste
                ll_add_to_calendar.setVisibility(View.GONE);
            }
            // to set paid info
            if (activeAppointmentInfo.getAmountPaid() != null && !activeAppointmentInfo.getAmountPaid().equalsIgnoreCase("0.0")) {
                llPayment.setVisibility(View.VISIBLE);
                tvAmount.setText("₹" + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(activeAppointmentInfo.getAmountPaid())));
            } else {

                llPayment.setVisibility(View.GONE);
            }

            // to set appointment date
            if (activeAppointmentInfo.getAppmtDate() != null && !activeAppointmentInfo.getAppmtDate().trim().isEmpty()) {
                try {
                    tvDate.setText(BookingDetails.getCustomDateString(activeAppointmentInfo.getAppmtDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                ll_date.setVisibility(View.GONE);
            }

            // hide instructions link when there are no post instructions
            if (activeAppointmentInfo.getService() != null && activeAppointmentInfo.getService().isPostInfoEnabled()
                    && ((activeAppointmentInfo.getService().getPostInfoText() != null && !activeAppointmentInfo.getService().getPostInfoText().trim().isEmpty())
                    || (activeAppointmentInfo.getService().getPostInfoTitle() != null && !activeAppointmentInfo.getService().getPostInfoTitle().trim().isEmpty()))) {
                ll_postinfo.setVisibility(View.VISIBLE);
                if (activeAppointmentInfo.getService().getPostInfoTitle() != null && !activeAppointmentInfo.getService().getPostInfoTitle().trim().isEmpty()) {
                    tv_postInfoTitle.setText(activeAppointmentInfo.getService().getPostInfoTitle());
                } else {
                    ll_postinfo.setVisibility(View.GONE);
                }
                if (activeAppointmentInfo.getService().getPostInfoText() != null && !activeAppointmentInfo.getService().getPostInfoText().trim().isEmpty()) {
                    tv_postInfo.setText((Html.fromHtml(activeAppointmentInfo.getService().getPostInfoText())));
                } else {
                    ll_postinfo.setVisibility(View.GONE);
                }
            } else {
                ll_postinfo.setVisibility(View.GONE);
            }


            // to set slot time
            if (activeAppointmentInfo.getAppmtTime() != null && !activeAppointmentInfo.getAppmtTime().trim().isEmpty()) {
                tvTime.setText(convertTime(activeAppointmentInfo.getAppmtTime().split("-")[0]));
            } else {
                ll_time.setVisibility(View.GONE);
            }

            if (activeAppointmentInfo.getBatchId() != null) {
                llBatchNo.setVisibility(View.VISIBLE);
                tvBatchNo.setText(activeAppointmentInfo.getBatchId());
            } else {
                llBatchNo.setVisibility(View.GONE);
            }

            cvShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (activeAppointmentInfo != null && activeAppointmentInfo.getAppointmentEncId() != null) {
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("text/html");
                        String statusUrl = Constants.URL + "status/" + activeAppointmentInfo.getAppointmentEncId();
                        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Share your Appointment status link");
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, statusUrl);
                        startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    }

                }
            });

            llMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        Intent intent = new Intent(mContext, ChatActivity.class);
                        if (activeAppointmentInfo.getUid().contains("h_")) {
                            uuid = activeAppointmentInfo.getUid().replace("h_", "");
                            intent.putExtra("uuid", uuid);
                        } else {
                            intent.putExtra("uuid", activeAppointmentInfo.getUid());
                        }
                        intent.putExtra("accountId", activeAppointmentInfo.getProviderAccount().getId());
                        intent.putExtra("name", activeAppointmentInfo.getProviderAccount().getBusinessName());
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
                    Date sTime = null;
                    Date eTime = null;
                    Calendar calSTime = Calendar.getInstance();
                    Calendar calETime = Calendar.getInstance();

                    try {
                        sTime = sdf.parse(activeAppointmentInfo.getAppmtDate() + " " + activeAppointmentInfo.getAppmtTime().split("-")[0]);
                        eTime = sdf.parse(activeAppointmentInfo.getAppmtDate() + " " + activeAppointmentInfo.getAppmtTime().split("-")[1]);
                        calSTime.setTime(sTime);
                        calETime.setTime(eTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(Intent.ACTION_INSERT);
                    intent.setData(CalendarContract.Events.CONTENT_URI);
                    if (activeAppointmentInfo.getProviderAccount().getBusinessName() != null && !activeAppointmentInfo.getProviderAccount().getBusinessName().equalsIgnoreCase("")) {
                        intent.putExtra(CalendarContract.Events.TITLE, activeAppointmentInfo.getProviderAccount().getBusinessName() + " - " + activeAppointmentInfo.getService().getName());//activeCheckInInfo.getCheckinEncId());
                        if (from.equalsIgnoreCase(Constants.RESCHEDULE)) {
                            intent.putExtra(CalendarContract.Events.DESCRIPTION, "Booking Rescheduled\n" + "Service provider : " + activeAppointmentInfo.getProviderAccount().getBusinessName() + "\nLocation : " + activeAppointmentInfo.getLocation().getPlace());
                        } else {
                            intent.putExtra(CalendarContract.Events.DESCRIPTION, "Service provider : " + activeAppointmentInfo.getProviderAccount().getBusinessName() + "\nLocation : " + activeAppointmentInfo.getLocation().getPlace());
                        }
                    } else {
                        String name = activeAppointmentInfo.getProvider().getFirstName() + " " + activeAppointmentInfo.getProvider().getLastName();
                        intent.putExtra(CalendarContract.Events.TITLE, name + " - " + activeAppointmentInfo.getService().getName());// activeCheckInInfo.getCheckinEncId());
                        if (from.equalsIgnoreCase(Constants.RESCHEDULE)) {
                            intent.putExtra(CalendarContract.Events.DESCRIPTION, "Booking Rescheduled\n" + "Service provider : " + name + "\nLocation : " + activeAppointmentInfo.getLocation().getPlace());
                        } else {
                            intent.putExtra(CalendarContract.Events.DESCRIPTION, "Service provider : " + name + "\nLocation : " + activeAppointmentInfo.getLocation().getPlace());
                        }
                    }
                    intent.putExtra(CalendarContract.Events.EVENT_LOCATION, activeAppointmentInfo.getLocation().getPlace());
                    //intent.putExtra(CalendarContract.Events.DTSTART, calSTime.getTime());
                    //intent.putExtra(CalendarContract.Events.DTEND, calETime.getTime());
                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calSTime.getTimeInMillis());
                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calETime.getTimeInMillis());
                    intent.putExtra(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());
                    //intent.putExtra(CalendarContract.Events.ALL_DAY, false);
                    //intent.putExtra(Intent.EXTRA_EMAIL, "test@yahoo.com, test2@yahoo.com, test3@yahoo.com");

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(mContext, "There is no app that support this action", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (livetrack) {
                        Intent checkinShareLocations = new Intent(mContext, CheckinShareLocationAppointment.class);
                        checkinShareLocations.putExtra("waitlistPhonenumber", phoneNumber);
                        checkinShareLocations.putExtra("uuid", activeAppointmentInfo.getUid());
                        checkinShareLocations.putExtra("accountID", providerId);
                        checkinShareLocations.putExtra("title", activeAppointmentInfo.getProviderAccount().getBusinessName());
                        checkinShareLocations.putExtra("terminology", terminology);
                        checkinShareLocations.putExtra("calcMode", "");
                        checkinShareLocations.putExtra("queueStartTime", "");
                        checkinShareLocations.putExtra("queueEndTime", "");
                        checkinShareLocations.putExtra("from", Constants.APPOINTMENT);
                        startActivity(checkinShareLocations);
                    } else {
                        Intent home;
                        if (activeAppointmentInfo.getApptStatus().equalsIgnoreCase(Constants.REQUESTED) || activeAppointmentInfo.getApptStatus().equalsIgnoreCase(Constants.REQUESTREJECTED)) {
                            home = new Intent(mContext, Home.class);
                            home.putExtra("isRequest", Constants.REQUEST);
                        } else {
                            home = new Intent(mContext, Home.class);
                        }
                        startActivity(home);
                        finish();
                    }
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