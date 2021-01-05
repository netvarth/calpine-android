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
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinodev.androidneomorphframelayout.NeomorphFrameLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.Contents;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewItalicSemiBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewLight;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.InstructionsDialog;
import com.jaldeeinc.jaldee.custom.QRCodeEncoder;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;

import java.text.ParseException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jaldeeinc.jaldee.activities.CheckInDetails.convertAmountToDecimals;
import static com.jaldeeinc.jaldee.activities.CheckInDetails.convertToTitleForm;
import static com.jaldeeinc.jaldee.activities.CheckInDetails.getCustomDateString;
import static com.jaldeeinc.jaldee.connection.ApiClient.context;

public class CheckInConfirmation extends AppCompatActivity {

    ActiveCheckIn activeCheckInInfo = new ActiveCheckIn();
    private TextView tvTimeWindow;
    String terminology;
    ImageView icon_service, ivQR;
    private String from;
    Button btnOk, btnOk1;
    CustomTextViewSemiBold tvViewMore, tvViewLess;
    CustomTextViewMedium tvProviderName, tvServiceName, tvLocation;
    CustomTextViewBold tvProvider, tvConfirmationNumber, tvConsumerName, tvStatus, tvDate, tvTime, tvBatchNo, tvAmount, tv_heading;
    Context mContext;
    LinearLayout llPayment, llBatchNo;
    CardView cvBack, cvShare;
    NeomorphFrameLayout llMoreDetails;
    LinearLayout llMessage, llInstructions, llViewMore, llViewLess;
    private String uuid;
    private InstructionsDialog instructionsDialog;
    CustomTextViewLight tvHint;
    CustomTextViewItalicSemiBold tvTokenWaitTime;
    private String phoneNumber, providerId;
    boolean livetrack;
    private String value;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin_confirmation);
        mContext = this;

        Intent i = getIntent();
        activeCheckInInfo = (ActiveCheckIn) i.getSerializableExtra("BookingDetails");
        terminology = i.getStringExtra("terminology");
        from = i.getStringExtra("from");
        phoneNumber = i.getStringExtra("waitlistPhonenumber");
        livetrack = i.getBooleanExtra("livetrack", false);
        providerId = i.getStringExtra("accountID");
        value = i.getStringExtra("confId");


        initializations();

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(CheckInConfirmation.this, Home.class);
                startActivity(home);
                finish();
            }
        });

        if (value != null && providerId != null) {
            getConfirmationDetails(Integer.parseInt(providerId));
        } else {
            UpdateMainUI();
        }


    }

    private void initializations() {
        tvProviderName = findViewById(R.id.tv_providerName);
        tvServiceName = findViewById(R.id.tv_serviceName);
        tvTimeWindow = findViewById(R.id.tv_queueTime);
        tvProvider = findViewById(R.id.tv_doctorName);
        tvConfirmationNumber = findViewById(R.id.tv_confirmationNumber);
        btnOk = findViewById(R.id.cv_ok);
        btnOk1 = findViewById(R.id.cv_ok1);
        icon_service = findViewById(R.id.iv_teleService);
        tvConsumerName = findViewById(R.id.tv_consumerName);
        tvLocation = findViewById(R.id.tv_locationName);
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
        tvHint = findViewById(R.id.tv_hint);
        tvTokenWaitTime = findViewById(R.id.tv_tokenWaitTime);
    }


    public void UpdateMainUI() {
        if (from != null && activeCheckInInfo != null) {
            if (from.equalsIgnoreCase("Reschedule")) {
                if (activeCheckInInfo.getShowToken() != null && activeCheckInInfo.getShowToken().equalsIgnoreCase("true")) {
                    tv_heading.setText("Token Rescheduled");
                } else {
                    tv_heading.setText("Checkin Rescheduled");
                }
            } else {
                tv_heading.setText("Booking Confirmed");
            }
        }


        if (activeCheckInInfo != null) {

            if (activeCheckInInfo.getProvider() != null) {

                if (activeCheckInInfo.getProvider().getBusinessName() != null && !activeCheckInInfo.getProvider().getBusinessName().equalsIgnoreCase("")) {
                    tvProvider.setText(convertToTitleForm(activeCheckInInfo.getProvider().getBusinessName()));
                } else {
                    String name = activeCheckInInfo.getProvider().getFirstName() + " " + activeCheckInInfo.getProvider().getLastName();
                    tvProvider.setText(convertToTitleForm(name));
                }
                tvProviderName.setVisibility(View.VISIBLE);
                tvProviderName.setText(convertToTitleForm(activeCheckInInfo.getProviderAccount().getBusinessName()));
                tvProviderName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {
                            Intent intent = new Intent(CheckInConfirmation.this, ProviderDetailActivity.class);
                            intent.putExtra("uniqueID", activeCheckInInfo.getProviderAccount().getUniqueId());
                            intent.putExtra("locationId", activeCheckInInfo.getQueue().getLocation().getId());
                            startActivity(intent);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                tvProviderName.setVisibility(View.INVISIBLE);
                tvProvider.setText(convertToTitleForm(activeCheckInInfo.getProviderAccount().getBusinessName()));
                tvProvider.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {
                            Intent intent = new Intent(CheckInConfirmation.this, ProviderDetailActivity.class);
                            intent.putExtra("uniqueID", activeCheckInInfo.getProviderAccount().getUniqueId());
                            intent.putExtra("locationId", activeCheckInInfo.getQueue().getLocation().getId());
                            startActivity(intent);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }


            if (activeCheckInInfo.getCheckinEncId() != null) {
                //Encode with a QR Code image
                String statusUrl = Constants.URL + "status/" + activeCheckInInfo.getCheckinEncId();
                QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(statusUrl,
                        null,
                        Contents.Type.TEXT,
                        BarcodeFormat.QR_CODE.toString(), 0);
                try {
                    Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
                    ivQR.setImageBitmap(bitmap);

                    ivQR.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Dialog settingsDialog = new Dialog(CheckInConfirmation.this);
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
                    tvStatus.setTextColor(mContext.getResources().getColor(R.color.location_theme));
                }
                tvStatus.setText(convertToTitleForm(activeCheckInInfo.getWaitlistStatus()));
            } else {
                tvStatus.setVisibility(View.GONE);
            }

            // to set paid info
            if (activeCheckInInfo.getAmountPaid() != 0) {
                llPayment.setVisibility(View.VISIBLE);
                tvAmount.setText("₹" + " " + convertAmountToDecimals(activeCheckInInfo.getAmountPaid()) + " " + "PAID");
            } else {

                llPayment.setVisibility(View.GONE);
            }

            // to set consumer name
            if (activeCheckInInfo.getWaitlistingFor() != null) {

                tvConsumerName.setText(activeCheckInInfo.getWaitlistingFor().get(0).getFirstName() + " " + activeCheckInInfo.getWaitlistingFor().get(0).getLastName());

            }

            // to set appointment date
            if (activeCheckInInfo.getDate() != null && activeCheckInInfo.getQueue() != null) {
                String date = null;
                try {
                    date = getCustomDateString(activeCheckInInfo.getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String time = activeCheckInInfo.getQueue().getQueueStartTime() + " - " + activeCheckInInfo.getQueue().getQueueEndTime();
                tvDate.setText(date);
                tvTimeWindow.setText(time);

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


            // hide instructions link when there are no post instructions
            if (activeCheckInInfo.getService() != null && activeCheckInInfo.getService().isPostInfoEnabled()) {
                llInstructions.setVisibility(View.VISIBLE);
            } else {
                llInstructions.setVisibility(View.GONE);
            }

            if (activeCheckInInfo.getService() != null) {
                if (activeCheckInInfo.getService().getName() != null) {
                    tvServiceName.setText(activeCheckInInfo.getService().getName());
                    try {
                        if (activeCheckInInfo.getService().getServiceType().equalsIgnoreCase("virtualService")) {
                            icon_service.setVisibility(View.VISIBLE);

                            if (activeCheckInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                icon_service.setImageResource(R.drawable.zoomicon_sized);
                            } else if (activeCheckInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                icon_service.setImageResource(R.drawable.googlemeet_sized);
                            } else if (activeCheckInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                if (activeCheckInInfo.getService().getVirtualServiceType() != null && activeCheckInInfo.getService().getVirtualServiceType().equalsIgnoreCase("videoService")) {
                                    icon_service.setImageResource(R.drawable.whatsapp_videoicon);
                                } else {
                                    icon_service.setImageResource(R.drawable.whatsappicon_sized);

                                }
                            } else if (activeCheckInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                icon_service.setImageResource(R.drawable.phoneiconsized_small);
                            }

                        } else {
                            icon_service.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


            // to set waitTime or token No with waitTime
            if (activeCheckInInfo.getShowToken() != null && activeCheckInInfo.getShowToken().equalsIgnoreCase("true")) {

                if (activeCheckInInfo.getCalculationMode() != null && !activeCheckInInfo.getCalculationMode().equalsIgnoreCase("NoCalc")) {
                    tvHint.setText("Token #");
                    tvTime.setText(String.valueOf(activeCheckInInfo.getToken()));
                    tvTime.setGravity(Gravity.CENTER_HORIZONTAL);
                    tvTokenWaitTime.setVisibility(View.VISIBLE);
                    if (activeCheckInInfo.getAppxWaitingTime() == 1) {
                        tvTokenWaitTime.setText("Est wait time : " + Config.getTimeinHourMinutes(activeCheckInInfo.getAppxWaitingTime()));

                    } else {
                        tvTokenWaitTime.setText("Est wait time : " + Config.getTimeinHourMinutes(activeCheckInInfo.getAppxWaitingTime()));
                    }

                } else {
                    tvHint.setText("Token #");
                    tvTime.setText(String.valueOf(activeCheckInInfo.getToken()));
                    tvTime.setGravity(Gravity.CENTER_HORIZONTAL);
                    tvTokenWaitTime.setVisibility(View.GONE);
                }
            } else {
                tvHint.setText("Est wait time");
                tvTime.setText(Config.getTimeinHourMinutes(activeCheckInInfo.getAppxWaitingTime()));
            }


            llMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        Intent intent = new Intent(CheckInConfirmation.this, ChatActivity.class);

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


            btnOk1.setOnClickListener(new View.OnClickListener() {
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
                        checkinShareLocations.putExtra("from", "checkin");
                        startActivity(checkinShareLocations);
                    } else {
                        Intent home = new Intent(CheckInConfirmation.this, Home.class);
                        startActivity(home);
                        finish();
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
                        checkinShareLocations.putExtra("from", "checkin");
                        startActivity(checkinShareLocations);
                    } else {
                        Intent home = new Intent(CheckInConfirmation.this, Home.class);
                        startActivity(home);
                        finish();
                    }
                }
            });


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

            cvShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (activeCheckInInfo != null && activeCheckInInfo.getCheckinEncId() != null) {
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/html");
                        String statusUrl = Constants.URL + "status/" + activeCheckInInfo.getCheckinEncId();
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share your CheckIn/Token status link");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, statusUrl);
                        startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    }

                }
            });

        }
    }


    @Override
    public void onBackPressed() {
        Intent home = new Intent(CheckInConfirmation.this, Home.class);
        startActivity(home);
        finish();
        super.onBackPressed();
    }

    private void openMapView(String latitude, String longitude, String locationName) {
        Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + locationName);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        }
    }

    private void getConfirmationDetails(int id) {
        final Dialog mDialog = Config.getProgressDialog(CheckInConfirmation.this, CheckInConfirmation.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<ActiveCheckIn> call = apiService.getActiveCheckInUUID(value, String.valueOf(id));
        call.enqueue(new Callback<ActiveCheckIn>() {
            @Override
            public void onResponse(Call<ActiveCheckIn> call, Response<ActiveCheckIn> response) {
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
            public void onFailure(Call<ActiveCheckIn> call, Throwable t) {
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });

    }

}