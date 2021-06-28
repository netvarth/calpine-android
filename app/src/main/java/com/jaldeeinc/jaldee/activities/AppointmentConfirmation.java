package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.text.ParseException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jaldeeinc.jaldee.activities.BookingDetails.convertAmountToDecimals;
import static com.jaldeeinc.jaldee.activities.BookingDetails.convertTime;
import static com.jaldeeinc.jaldee.activities.BookingDetails.convertToTitleForm;
import static com.jaldeeinc.jaldee.activities.BookingDetails.getCustomDateString;
import static com.jaldeeinc.jaldee.connection.ApiClient.context;

public class AppointmentConfirmation extends AppCompatActivity {

    ActiveAppointment activeCheckInInfo = new ActiveAppointment();
    private LinearLayout llBatchNo;
    Button btnOk,btnOk1;
    private String terminology;
    ImageView icon_service,ivQR;
    private String from;
    CustomTextViewSemiBold tvViewMore,tvViewLess;
    CustomTextViewMedium tvProviderName,tvServiceName,tv_location;
    CustomTextViewBold tvProvider,tvConfirmationNumber,tv_consumer,tvStatus,tvDate,tvTime,tvBatchNo,tvAmount,tv_heading;
    Context mContext;
    LinearLayout llPayment;
    CardView cvBack,cvShare;
    NeomorphFrameLayout llMoreDetails;
    LinearLayout llMessage, llInstructions, llViewMore, llViewLess;
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
            value = extras.getString("confId");
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

        if(value!=null && providerId!=null){
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
        btnOk1 = findViewById(R.id.cv_ok1);
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
        tvViewMore = findViewById(R.id.tv_viewMore);
        tvViewLess = findViewById(R.id.tv_viewLess);
        llViewMore = findViewById(R.id.ll_viewMore);
        llViewLess = findViewById(R.id.ll_viewLess);

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
                        UpdateMainUI();

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

    public void UpdateMainUI() {

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
                        }
                        else if (activeCheckInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("VideoCall")) {
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
                tvAmount.setText("₹" + " " + convertAmountToDecimals(activeCheckInInfo.getAmountPaid()) + " " + "PAID");
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
                QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(statusUrl,
                        null,
                        Contents.Type.TEXT,
                        BarcodeFormat.QR_CODE.toString(), 175);
                try {
                    Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
                    ivQR.setImageBitmap(bitmap);

                    ivQR.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Dialog settingsDialog = new Dialog(AppointmentConfirmation.this);
                            settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                            settingsDialog.getWindow().getAttributes().windowAnimations = R.style.zoomInAndOut;
                            settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.image_layout
                                    , null));
                            ImageView imageView = settingsDialog.findViewById(R.id.iv_close);
                            ImageView ivQR = settingsDialog.findViewById(R.id.iv_Qr);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    settingsDialog.dismiss();
                                }
                            });

                            ivQR.setImageBitmap(bitmap);
                            settingsDialog.show();
                        }
                    });


                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }

            tvViewMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    llViewMore.setVisibility(View.GONE);
                    llMoreDetails.setVisibility(View.VISIBLE);
                    llViewLess.setVisibility(View.VISIBLE);
                }
            });

            tvViewLess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    llViewLess.setVisibility(View.GONE);
                    llMoreDetails.setVisibility(View.GONE);
                    llViewMore.setVisibility(View.VISIBLE);
                }
            });


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


            btnOk1.setOnClickListener(new View.OnClickListener() {
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

}