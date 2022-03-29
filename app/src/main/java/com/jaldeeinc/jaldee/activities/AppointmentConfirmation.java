package com.jaldeeinc.jaldee.activities;

import static com.jaldeeinc.jaldee.activities.BookingDetails.convertTime;
import static com.jaldeeinc.jaldee.activities.BookingDetails.convertToTitleForm;
import static com.jaldeeinc.jaldee.activities.BookingDetails.getCustomDateString;
import static com.jaldeeinc.jaldee.connection.ApiClient.context;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

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
import com.jaldeeinc.jaldee.response.ActiveAppointment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentConfirmation extends AppCompatActivity {

    ActiveAppointment activeCheckInInfo = new ActiveAppointment();
    private LinearLayout llBatchNo;
    Button btnOk;
    private String terminology;
    ImageView icon_service, ivQR;
    private String from;
    CustomTextViewMedium tvProviderName, tvServiceName, tv_location;
    CustomTextViewBold tvProvider, tvConfirmationNumber, tv_consumer, tvStatus, tvDate, tvTime, tvBatchNo, tvAmount, tv_heading;
    Context mContext;
    LinearLayout llPayment;
    CardView cvBack, cvShare;
    NeomorphFrameLayout llMoreDetails;
    LinearLayout llMessage, llInstructions, ll_add_to_calendar;
    private String uuid;
    private InstructionsDialog instructionsDialog;
    private String phoneNumber, providerId, livetrack, value;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_confirmation);
        mContext = this;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            activeCheckInInfo = (ActiveAppointment) extras.getSerializable("BookingDetails");
            terminology = extras.getString("terminology");
            from = extras.getString("from");
            phoneNumber = extras.getString("waitlistPhonenumber");
            livetrack = extras.getString("livetrack");
            providerId = extras.getString("accountID");
            value = extras.getString("uid");
        }

        initializations();

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(AppointmentConfirmation.this, Home.class);
                startActivity(home);
                finish();
            }
        });

        if (value != null && providerId != null) {
            getConfirmationDetails(Integer.parseInt(providerId));
        } else {

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

    private void initializations() {

        tvProviderName = findViewById(R.id.tv_providerName);
        tvServiceName = findViewById(R.id.tv_serviceName);
        tvProvider = findViewById(R.id.tv_doctorName);
        tvConfirmationNumber = findViewById(R.id.tv_confirmationNumber);
        btnOk = findViewById(R.id.cv_ok);
        icon_service = findViewById(R.id.iv_teleService);
        tv_consumer = findViewById(R.id.tv_consumerName);
        tv_location = findViewById(R.id.tv_locationName);
        tv_heading = findViewById(R.id.tv_heading);
        tvBatchNo = findViewById(R.id.tv_batchNo);
        llBatchNo = findViewById(R.id.ll_batch);
        tvStatus = findViewById(R.id.tv_status);
        tvDate = findViewById(R.id.tv_date);
        tvTime = findViewById(R.id.tv_time);
        llPayment = findViewById(R.id.ll_payment);
        tvAmount = findViewById(R.id.tv_amount);
        cvBack = findViewById(R.id.cv_back);
        cvShare = findViewById(R.id.cv_share);
        ivQR = findViewById(R.id.iv_Qr);
        llMoreDetails = findViewById(R.id.ll_moreDetails);
        llMessage = findViewById(R.id.ll_message);
        llInstructions = findViewById(R.id.ll_instructions);
        ll_add_to_calendar = findViewById(R.id.ll_add_to_calendar);
    }

    private void getConfirmationDetails(int userId) {

        final Dialog mDialog = Config.getProgressDialog(AppointmentConfirmation.this, AppointmentConfirmation.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        final ApiInterface apiService =
                ApiClient.getClient(AppointmentConfirmation.this).create(ApiInterface.class);
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
                        activeCheckInInfo = response.body();
                        UpdateMainUI(activeCheckInInfo);

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

        if (from != null && activeCheckInInfo != null) {
            if (from.equalsIgnoreCase("Reschedule")) {
                tv_heading.setText("Appointment Rescheduled!");
            } else {
                tv_heading.setText("Booking Confirmed!");
            }
        }

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
                            Intent intent = new Intent(AppointmentConfirmation.this, ProviderDetailActivity.class);
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
                            Intent intent = new Intent(AppointmentConfirmation.this, ProviderDetailActivity.class);
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
                tvConfirmationNumber.setText(activeCheckInInfo.getAppointmentEncId());
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
                tvTime.setText(convertTime(activeCheckInInfo.getAppmtTime().split("-")[0]));
            }

            if (activeCheckInInfo.getBatchId() != null) {
                llBatchNo.setVisibility(View.VISIBLE);
                tvBatchNo.setText(activeCheckInInfo.getBatchId());
            } else {
                llBatchNo.setVisibility(View.GONE);
            }

            // to set status
            if (activeCheckInInfo.getApptStatus() != null) {
                tvStatus.setVisibility(View.VISIBLE);
                if (activeCheckInInfo.getApptStatus().equalsIgnoreCase("Cancelled")) {
                    tvStatus.setTextColor(mContext.getResources().getColor(R.color.red));

                } else {
                    tvStatus.setTextColor(mContext.getResources().getColor(R.color.location_theme));
                }
                tvStatus.setText(convertToTitleForm(activeCheckInInfo.getApptStatus()));
            } else {
                tvStatus.setVisibility(View.GONE);
            }


            // to set paid info
            if (activeCheckInInfo.getAmountPaid() != null && !activeCheckInInfo.getAmountPaid().equalsIgnoreCase("0.0")) {
                llPayment.setVisibility(View.VISIBLE);
                tvAmount.setText("₹" + " " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(activeCheckInInfo.getAmountPaid())) + " " + "PAID");
            } else {

                llPayment.setVisibility(View.GONE);
            }


            cvShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (activeCheckInInfo != null && activeCheckInInfo.getAppointmentEncId() != null) {
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/html");
                        String statusUrl = Constants.URL + "status/" + activeCheckInInfo.getAppointmentEncId();
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share your Appointment status link");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, statusUrl);
                        startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    }

                }
            });


            if (activeCheckInInfo.getAppointmentEncId() != null) {
                //Encode with a QR Code image
                String statusUrl = Constants.URL + "status/" + activeCheckInInfo.getAppointmentEncId();

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

                            Dialog settingsDialog = new Dialog(AppointmentConfirmation.this);
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

            // hide instructions link when there are no post instructions
            if (activeCheckInInfo.getService() != null && activeCheckInInfo.getService().isPostInfoEnabled()) {
                llInstructions.setVisibility(View.VISIBLE);
            } else {
                llInstructions.setVisibility(View.GONE);
            }

            llInstructions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {

                        if (activeCheckInInfo != null && activeCheckInInfo.getService() != null) {
                            instructionsDialog = new InstructionsDialog(mContext, activeCheckInInfo.getService().getPostInfoText(), activeCheckInInfo.getService().getPostInfoTitle());
                            instructionsDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                            instructionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            instructionsDialog.show();
                            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                            int width = (int) (metrics.widthPixels * 1);
                            instructionsDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            llMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        Intent intent = new Intent(AppointmentConfirmation.this, ChatActivity.class);
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
                    Date sTime = null;
                    Date eTime = null;
                    Calendar calSTime = Calendar.getInstance();
                    Calendar calETime = Calendar.getInstance();

                    try {
                        sTime = sdf.parse(activeCheckInInfo.getAppmtDate() + " " + activeCheckInInfo.getAppmtTime().split("-")[0]);
                        eTime = sdf.parse(activeCheckInInfo.getAppmtDate() + " " + activeCheckInInfo.getAppmtTime().split("-")[1]);
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
                        intent.putExtra(CalendarContract.Events.TITLE, "booking with - " + activeCheckInInfo.getProviderAccount().getBusinessName());//activeCheckInInfo.getCheckinEncId());
                        if (from.equalsIgnoreCase("Reschedule")) {
                            intent.putExtra(CalendarContract.Events.DESCRIPTION, "Booking Rescheduled\n" + "Service provider : " + activeCheckInInfo.getProviderAccount().getBusinessName() + "\nLocation : " + activeCheckInInfo.getLocation().getPlace());
                        } else {
                            intent.putExtra(CalendarContract.Events.DESCRIPTION, "Service provider : " + activeCheckInInfo.getProviderAccount().getBusinessName() + "\nLocation : " + activeCheckInInfo.getLocation().getPlace());
                        }
                    } else {
                        String name = activeCheckInInfo.getProvider().getFirstName() + " " + activeCheckInInfo.getProvider().getLastName();
                        intent.putExtra(CalendarContract.Events.TITLE, "booking with - " + name);// activeCheckInInfo.getCheckinEncId());
                        if (from.equalsIgnoreCase("Reschedule")) {
                            intent.putExtra(CalendarContract.Events.DESCRIPTION, "Booking Rescheduled\n" + "Service provider : " + name + "\nLocation : " + activeCheckInInfo.getLocation().getPlace());
                        } else {
                            intent.putExtra(CalendarContract.Events.DESCRIPTION, "Service provider : " + name + "\nLocation : " + activeCheckInInfo.getLocation().getPlace());
                        }
                    }
                    intent.putExtra(CalendarContract.Events.EVENT_LOCATION, activeCheckInInfo.getLocation().getPlace());
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
                        Toast.makeText(AppointmentConfirmation.this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (livetrack != null && livetrack.equalsIgnoreCase("true")) {
                        Intent checkinShareLocations = new Intent(AppointmentConfirmation.this, CheckinShareLocationAppointment.class);
                        checkinShareLocations.putExtra("waitlistPhonenumber", phoneNumber);
                        checkinShareLocations.putExtra("uuid", activeCheckInInfo.getUid());
                        checkinShareLocations.putExtra("accountID", providerId);
                        checkinShareLocations.putExtra("title", activeCheckInInfo.getProviderAccount().getBusinessName());
                        checkinShareLocations.putExtra("terminology", terminology);
                        checkinShareLocations.putExtra("calcMode", "");
                        checkinShareLocations.putExtra("queueStartTime", "");
                        checkinShareLocations.putExtra("queueEndTime", "");
                        checkinShareLocations.putExtra("from", "appt");
                        startActivity(checkinShareLocations);


                    } else {

                        Intent home = new Intent(AppointmentConfirmation.this, Home.class);
                        startActivity(home);
                        finish();
                    }

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent home = new Intent(AppointmentConfirmation.this, Home.class);
        startActivity(home);
        finish();
        super.onBackPressed();
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
}