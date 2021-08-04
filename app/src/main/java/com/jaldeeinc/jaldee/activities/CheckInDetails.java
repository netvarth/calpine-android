package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chinodev.androidneomorphframelayout.NeomorphFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
import com.jaldeeinc.jaldee.custom.CustomTextViewRegularItalic;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.CustomerNotes;
import com.jaldeeinc.jaldee.custom.InstructionsDialog;
import com.jaldeeinc.jaldee.custom.MeetingDetailsWindow;
import com.jaldeeinc.jaldee.custom.MeetingInfo;
import com.jaldeeinc.jaldee.custom.PrescriptionDialog;
import com.jaldeeinc.jaldee.custom.QRCodeEncoder;
import com.jaldeeinc.jaldee.model.Bookings;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.RatingResponse;
import com.jaldeeinc.jaldee.response.TeleServiceCheckIn;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jaldeeinc.jaldee.connection.ApiClient.context;

public class CheckInDetails extends AppCompatActivity {

    @BindView(R.id.tv_providerName)
    CustomTextViewMedium tvProviderName;

    @BindView(R.id.tv_doctorName)
    CustomTextViewBold tvDoctorName;

    @BindView(R.id.tv_serviceName)
    CustomTextViewMedium tvServiceName;

    @BindView(R.id.iv_teleService)
    ImageView ivTeleService;

    @BindView(R.id.tv_locationName)
    CustomTextViewMedium tvLocationName;

    @BindView(R.id.tv_confirmationNumber)
    CustomTextViewBold tvConfirmationNumber;

    @BindView(R.id.tv_status)
    CustomTextViewBold tvStatus;

    @BindView(R.id.tv_amount)
    CustomTextViewBold tvAmount;

    @BindView(R.id.tv_consumerName)
    CustomTextViewBold tvConsumerName;

    @BindView(R.id.tv_date)
    CustomTextViewBold tvDate;

    @BindView(R.id.tv_time)
    CustomTextViewBold tvTime;


    @BindView(R.id.tv_viewMore)
    CustomTextViewSemiBold tvViewMore;

    @BindView(R.id.tv_billText)
    CustomTextViewSemiBold tvBillText;

    @BindView(R.id.tv_bill_receiptText)
    CustomTextViewSemiBold tvBillReceiptText;

    @BindView(R.id.cv_back)
    CardView cvBack;

    @BindView(R.id.cv_share)
    CardView cvShare;

    @BindView(R.id.cv_bill)
    CardView cvBill;

    @BindView(R.id.cv_enquiry)
    CardView cvEnquiry;

    @BindView(R.id.ll_payment)
    LinearLayout llPayment;

    @BindView(R.id.ll_cancel)
    LinearLayout llCancel;

    @BindView(R.id.ll_moreDetails)
    NeomorphFrameLayout llMoreDetails;

    @BindView(R.id.ll_message)
    LinearLayout llMessage;

    @BindView(R.id.ll_reschedule)
    LinearLayout llReschedule;

    @BindView(R.id.ll_location)
    LinearLayout llLocation;

    @BindView(R.id.ll_rating)
    LinearLayout llRating;

    @BindView(R.id.iv_ltIcon)
    ImageView ivLtIcon;

    @BindView(R.id.ll_customerNotes)
    LinearLayout llCustomerNotes;

    @BindView(R.id.tv_customerNotes)
    CustomTextViewMedium tvCustomerNotes;

    @BindView(R.id.ll_instructions)
    LinearLayout llInstructions;

    @BindView(R.id.tv_trackingText)
    CustomTextViewMedium tvTrackingText;

    @BindView(R.id.tv_amountToPay)
    CustomTextViewRegularItalic tvAmountToPay;

    @BindView(R.id.tv_tokenWaitTime)
    CustomTextViewItalicSemiBold tvTokenWaitTime;

    @BindView(R.id.tv_hint)
    CustomTextViewLight tvHint;

    @BindView(R.id.tv_queueTime)
    CustomTextViewSemiBold tvQueueTime;

    @BindView(R.id.tv_title)
    CustomTextViewSemiBold tvTitle;

    @BindView(R.id.cv_meetingDetails)
    NeomorphFrameLayout cvMeetingDetails;

    @BindView(R.id.iv_meetingIcon)
    ImageView ivMeetingIcon;

    @BindView(R.id.iv_Qr)
    ImageView ivQR;

    @BindView(R.id.ll_prescription)
    LinearLayout llPrescription;

    @BindView(R.id.ll_viewMore)
    LinearLayout llMore;

    @BindView(R.id.tv_phoneNumber)
    CustomTextViewBold tvPhoneNumber;

    @BindView(R.id.ll_phoneNumber)
    LinearLayout llPhoneNumber;

    boolean firstTimeRating = false;
    boolean isTvViewMore = false;

    private InstructionsDialog instructionsDialog;
    private CustomerNotes customerNotes;
    private Context mContext;
    private boolean isToken = true;
    private Bookings bookingInfo = new Bookings();
    private boolean isActive = true;
    private ActiveCheckIn activeCheckIn = new ActiveCheckIn();
    private TeleServiceCheckIn meetingDetails;
    private MeetingDetailsWindow meetingDetailsWindow;
    private MeetingInfo meetingInfo;
    private String uuid;
    private PrescriptionDialog prescriptionDialog;
    private Bookings bookings = new Bookings();
    String ynwUUid, accountId, countryCode;
    private boolean fromPushNotification = false;
    String uid;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_details);
        ButterKnife.bind(CheckInDetails.this);
        mContext = CheckInDetails.this;

        Intent i = getIntent();
        if (i != null) {
            bookingInfo = (Bookings) i.getSerializableExtra("bookingInfo");
            uid = i.getStringExtra("uid");
            id = i.getIntExtra("accountId", 0);
            isActive = i.getBooleanExtra("isActive", true);
            ynwUUid = i.getStringExtra("uuid");
            accountId = i.getStringExtra("account");
            fromPushNotification = i.getBooleanExtra(Constants.PUSH_NOTIFICATION, false);
        }

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        llReschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CheckInDetails.this, RescheduleCheckinActivity.class);
                intent.putExtra("uniqueId", activeCheckIn.getProviderAccount().getUniqueId());
                intent.putExtra("ynwuuid", activeCheckIn.getYnwUuid());
                intent.putExtra("providerId", activeCheckIn.getProviderAccount().getId());
                startActivity(intent);
            }
        });

        llInstructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    if (activeCheckIn != null && activeCheckIn.getService() != null) {
                        instructionsDialog = new InstructionsDialog(mContext, activeCheckIn.getService().getPostInfoText(), activeCheckIn.getService().getPostInfoTitle());
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
                    Intent intent = new Intent(CheckInDetails.this, ChatActivity.class);

                    if (activeCheckIn.getYnwUuid().contains("h_")) {
                        uuid = activeCheckIn.getYnwUuid().replace("h_", "");
                        intent.putExtra("uuid", uuid);
                    } else {
                        intent.putExtra("uuid", activeCheckIn.getYnwUuid());
                    }
                    intent.putExtra("accountId", activeCheckIn.getProviderAccount().getId());
                    intent.putExtra("name", activeCheckIn.getProviderAccount().getBusinessName());
                    intent.putExtra("from", Constants.CHECKIN);
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        llCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
                dialog.setContentView(R.layout.cancelcheckin);
                dialog.show();
                Button btn_send = (Button) dialog.findViewById(R.id.btn_send);
                Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
                final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
                TextView txtsendmsg = (TextView) dialog.findViewById(R.id.txtsendmsg);
                String mesg = "";
                if (isToken) {
                    mesg = "Do you want to cancel this Token ?";
                } else {
                    mesg = "Do you want to cancel this CheckIn ?";
                }
                txtsendmsg.setText(mesg);
                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ApiDeleteCheckIn(activeCheckIn.getYnwUuid(), String.valueOf(activeCheckIn.getProviderAccount().getId()), dialog);
                    }
                });
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });

        tvViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (llMoreDetails.getVisibility() != View.VISIBLE) {

                    llMoreDetails.setVisibility(View.VISIBLE);
                    tvViewMore.setText("View Less");
                    isTvViewMore = true;

                } else {

                    llMoreDetails.setVisibility(View.GONE);
                    tvViewMore.setText("View More");
                    isTvViewMore = false;
                }
            }
        });

        llLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent intent = new Intent(mContext, CheckinShareLocation.class);
                    intent.putExtra("waitlistPhonenumber", activeCheckIn.getWaitlistingFor().get(0).getPhoneNo());
                    intent.putExtra("uuid", activeCheckIn.getYnwUuid());
                    intent.putExtra("accountID", String.valueOf(activeCheckIn.getProviderAccount().getId()));
                    intent.putExtra("title", activeCheckIn.getProviderAccount().getBusinessName());
                    intent.putExtra("terminology", "Check-in");
                    intent.putExtra("calcMode", "Check-in");
                    intent.putExtra("queueStartTime", activeCheckIn.getQueue().getQueueStartTime());
                    intent.putExtra("queueEndTime", activeCheckIn.getQueue().getQueueEndTime());
                    if (activeCheckIn.getJaldeeWaitlistDistanceTime() != null && activeCheckIn.getJaldeeWaitlistDistanceTime().getJaldeeDistanceTime() != null) {
                        intent.putExtra("jaldeeDistance", String.valueOf(activeCheckIn.getJaldeeWaitlistDistanceTime().getJaldeeDistanceTime().getJaldeeDistance().getDistance()));
                    }
                    mContext.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        llRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ApiRating(String.valueOf(activeCheckIn.getProviderAccount().getId()), activeCheckIn.getYnwUuid());

            }
        });

        llCustomerNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    if (activeCheckIn != null && activeCheckIn.getService() != null) {
                        customerNotes = new CustomerNotes(mContext, activeCheckIn.getService().getConsumerNoteTitle(), activeCheckIn.getConsumerNote());
                        customerNotes.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                        customerNotes.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        customerNotes.show();
                        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                        int width = (int) (metrics.widthPixels * 1);
                        customerNotes.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        cvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (activeCheckIn != null && activeCheckIn.getCheckinEncId() != null) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/html");
                    String statusUrl = Constants.URL + "status/" + activeCheckIn.getCheckinEncId();
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share your CheckIn/Token status link");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, statusUrl);
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }

            }
        });

    }

    @Override
    protected void onResume() {

        try {

            // Api call
            if (uid != null) {
                getBookingDetails(uid, id);
            } else {

                // this gets called when activity is launched from push notification
                if (ynwUUid != null) {
                    getBookingDetails(ynwUUid, Integer.parseInt(accountId));
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    private void getBookingDetails(String uid, int id) {

        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(CheckInDetails.this, CheckInDetails.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ActiveCheckIn> call = apiService.getActiveCheckInUUID(uid, String.valueOf(id));
        call.enqueue(new Callback<ActiveCheckIn>() {
            @Override
            public void onResponse(Call<ActiveCheckIn> call, Response<ActiveCheckIn> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        activeCheckIn = response.body();

                        if (activeCheckIn != null) {
                            if (!activeCheckIn.getWaitlistStatus().equalsIgnoreCase("Cancelled") && !activeCheckIn.getWaitlistStatus().equalsIgnoreCase("done")) {

                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    Date strDate = sdf.parse(activeCheckIn.getDate());
                                    Date date = null;
                                    date = getCurrentDate();
                                    if (date.compareTo(strDate) == 0) {
                                        isActive = true;
                                    } else if (date.compareTo(strDate) == 1) {
                                        isActive = false;
                                    } else if (date.compareTo(strDate) == -1) {
                                        isActive = true;
                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                isActive = false;
                            }
                            updateUI(activeCheckIn);
                        }

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

    private void updateUI(ActiveCheckIn checkInInfo) {

        try {

            if (checkInInfo != null) {
                if (checkInInfo.getProvider() != null) {

                    if (checkInInfo.getProvider().getBusinessName() != null && !checkInInfo.getProvider().getBusinessName().equalsIgnoreCase("")) {
                        tvDoctorName.setText(checkInInfo.getProvider().getBusinessName());
                    } else {
                        String name = checkInInfo.getProvider().getFirstName() + " " + checkInInfo.getProvider().getLastName();
                        tvDoctorName.setText(name);
                    }
                    tvProviderName.setVisibility(View.VISIBLE);
                    tvProviderName.setText(checkInInfo.getProviderAccount().getBusinessName());
                    tvProviderName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            try {
                                Intent intent = new Intent(CheckInDetails.this, ProviderDetailActivity.class);
                                intent.putExtra("uniqueID", checkInInfo.getProviderAccount().getUniqueId());
                                intent.putExtra("locationId", checkInInfo.getQueue().getLocation().getId());
                                startActivity(intent);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    tvProviderName.setVisibility(View.INVISIBLE);
                    tvDoctorName.setText(checkInInfo.getProviderAccount().getBusinessName());
                    tvDoctorName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            try {
                                Intent intent = new Intent(CheckInDetails.this, ProviderDetailActivity.class);
                                intent.putExtra("uniqueID", checkInInfo.getProviderAccount().getUniqueId());
                                intent.putExtra("locationId", checkInInfo.getQueue().getLocation().getId());
                                startActivity(intent);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                if (checkInInfo.getCheckinEncId() != null) {
                    //Encode with a QR Code image
                    String statusUrl = Constants.URL + "status/" + checkInInfo.getCheckinEncId();
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

                                Dialog settingsDialog = new Dialog(CheckInDetails.this);
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

                tvViewMore.setVisibility(View.VISIBLE);
                if (isTvViewMore) {
                    llMoreDetails.setVisibility(View.VISIBLE);
                } else {
                    llMoreDetails.setVisibility(View.GONE);
                }

                if (checkInInfo.getService() != null) {
                    tvServiceName.setText(checkInInfo.getService().getName());

                    if (checkInInfo.getService().getServiceType() != null && checkInInfo.getService().getServiceType().equalsIgnoreCase("virtualService")) {

                        if (isActive) {
                            if (checkInInfo.getWaitlistStatus() != null && checkInInfo.getWaitlistStatus().equalsIgnoreCase("done")) {
                                cvMeetingDetails.setVisibility(View.GONE);
                            } else {
                                cvMeetingDetails.setVisibility(View.VISIBLE);
                            }
                        } else {
                            cvMeetingDetails.setVisibility(View.GONE);
                        }
                        if (checkInInfo.getService().getVirtualCallingModes() != null) {
                            ivTeleService.setVisibility(View.VISIBLE);
                            if (checkInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                ivTeleService.setImageResource(R.drawable.zoom_meet);
                                ivMeetingIcon.setImageResource(R.drawable.zoom_meet);
                            } else if (checkInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                ivTeleService.setImageResource(R.drawable.google_meet);
                                ivMeetingIcon.setImageResource(R.drawable.google_meet);
                            } else if (checkInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                if (checkInInfo.getService().getVirtualServiceType() != null && checkInInfo.getService().getVirtualServiceType().equalsIgnoreCase("videoService")) {
                                    ivTeleService.setImageResource(R.drawable.whatsapp_videoicon);
                                    ivMeetingIcon.setImageResource(R.drawable.whatsapp_videoicon);
                                } else {
                                    ivTeleService.setImageResource(R.drawable.whatsapp_icon);
                                    ivMeetingIcon.setImageResource(R.drawable.whatsapp_icon);
                                }
                            } else if (checkInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                ivTeleService.setImageResource(R.drawable.phoneicon_sized);
                                ivMeetingIcon.setImageResource(R.drawable.phoneicon_sized);
                            } else if (checkInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("VideoCall")) {
                                ivTeleService.setImageResource(R.drawable.ic_jaldeevideo);
                                ivMeetingIcon.setImageResource(R.drawable.ic_jaldeevideo);
                            }
                        } else {
                            ivTeleService.setVisibility(View.GONE);
                        }
                    } else {
                        cvMeetingDetails.setVisibility(View.GONE);
                    }
                }

                // to set confirmation number
                if (checkInInfo.getCheckinEncId() != null) {
                    tvConfirmationNumber.setText(checkInInfo.getCheckinEncId());
                }
                // to set Phone number
                if (checkInInfo.getWaitlistPhoneNumber() != null && !checkInInfo.getWaitlistPhoneNumber().isEmpty()) {
                    llPhoneNumber.setVisibility(View.VISIBLE);
                    countryCode = checkInInfo.getCountryCode();
                    tvPhoneNumber.setText(countryCode + "\u00a0" + checkInInfo.getWaitlistPhoneNumber());
                } else {
                    hideView(llPhoneNumber);
                }
                // to set status
                if (checkInInfo.getWaitlistStatus() != null) {

                    if (checkInInfo.getWaitlistStatus().equalsIgnoreCase("done")) {
                        llRating.setVisibility(View.VISIBLE);
                    } else {
                        hideView(llRating);
                    }
                    tvStatus.setVisibility(View.VISIBLE);
                    if (checkInInfo.getWaitlistStatus().equalsIgnoreCase("Cancelled")) {
                        tvStatus.setTextColor(mContext.getResources().getColor(R.color.red));
                        tvStatus.setText(convertToTitleForm(checkInInfo.getWaitlistStatus()));

                    } else if (checkInInfo.getWaitlistStatus().equalsIgnoreCase("done")) {
                        tvStatus.setText("Completed");
                        tvStatus.setTextColor(mContext.getResources().getColor(R.color.location_theme));
                    } else {
                        tvStatus.setTextColor(mContext.getResources().getColor(R.color.location_theme));
                        tvStatus.setText(convertToTitleForm(checkInInfo.getWaitlistStatus()));
                    }
                } else {
                    tvStatus.setVisibility(View.GONE);
                }


                // to set paid info
                if (checkInInfo.getAmountPaid() != 0) {
                    llPayment.setVisibility(View.VISIBLE);
                    tvAmount.setText("₹" + " " + convertAmountToDecimals(checkInInfo.getAmountPaid()) + " " + "PAID");
                } else {

                    llPayment.setVisibility(View.GONE);
                }

                // to set consumer name
                if (checkInInfo.getWaitlistingFor() != null) {
                    tvConsumerName.setText(checkInInfo.getWaitlistingFor().get(0).getFirstName() + " " + checkInInfo.getWaitlistingFor().get(0).getLastName());

                }

                // to set appointment date
                if (checkInInfo.getDate() != null && checkInInfo.getQueue() != null) {
                    String date = getCustomDateString(checkInInfo.getDate());
                    String time = checkInInfo.getQueue().getQueueStartTime() + " - " + checkInInfo.getQueue().getQueueEndTime();
                    tvDate.setText(date);
                    tvQueueTime.setText(time);

                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date checkinDate = sdf.parse(checkInInfo.getDate());
                Date today = sdf.parse(LocalDateTime.now().toString());
                // to set waitTime or token No with waitTime
                if (checkInInfo.getShowToken() != null && checkInInfo.getShowToken().equalsIgnoreCase("true")) {
                    tvTitle.setText("Token Details");
                    isToken = true;
                    if (checkInInfo.getCalculationMode() != null && !checkInInfo.getCalculationMode().equalsIgnoreCase("NoCalc")) {

                        tvHint.setText("Token #");
                        tvTime.setText(String.valueOf(checkInInfo.getToken()));
                        tvTime.setGravity(Gravity.CENTER_HORIZONTAL);
                        if (!checkInInfo.getWaitlistStatus().equalsIgnoreCase("Cancelled") && !checkInInfo.getWaitlistStatus().equalsIgnoreCase("done") && !checkInInfo.getWaitlistStatus().equalsIgnoreCase("started")) {
                            tvTokenWaitTime.setVisibility(View.VISIBLE);
                            if (checkinDate.after(today)) {   //future upcomming checkin/token service time
                                tvTokenWaitTime.setText("Starts at : " + (checkInInfo.getServiceTime()));

                            } else {
                                tvTokenWaitTime.setText("Est wait time : " + Config.getTimeinHourMinutes(checkInInfo.getAppxWaitingTime()));
                            }
                        } else {
                            tvTokenWaitTime.setVisibility(View.GONE);
                        }

                    } else {
                        tvHint.setText("Token #");
                        tvTime.setText(String.valueOf(checkInInfo.getToken()));
                        tvTime.setGravity(Gravity.CENTER_HORIZONTAL);
                        tvTokenWaitTime.setVisibility(View.GONE);
                    }
                } else {
                    tvTitle.setText("CheckIn Details");
                    isToken = false;
                    if (checkinDate.after(today)) {    //future upcomming checkin/token service time
                        tvHint.setText("Starts at");
                        tvTime.setText(checkInInfo.getServiceTime());
                    } else {
                        tvHint.setText("Est wait time");
                        tvTime.setText(Config.getTimeinHourMinutes(checkInInfo.getAppxWaitingTime()));
                    }
                    if (!checkInInfo.getWaitlistStatus().equalsIgnoreCase("Cancelled") && !checkInInfo.getWaitlistStatus().equalsIgnoreCase("done") && !checkInInfo.getWaitlistStatus().equalsIgnoreCase("started")) {
                        tvTime.setVisibility(View.VISIBLE);
                        tvHint.setVisibility(View.VISIBLE);
                    } else {
                        tvTime.setVisibility(View.GONE);
                        tvHint.setVisibility(View.GONE);
                    }
                }


                // to set location
                if (checkInInfo.getQueue() != null && checkInInfo.getQueue().getLocation() != null) {

                    if (checkInInfo.getQueue().getLocation().getPlace() != null) {

                        tvLocationName.setText(checkInInfo.getQueue().getLocation().getPlace());

                        tvLocationName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                openMapView(checkInInfo.getQueue().getLocation().getLattitude(), checkInInfo.getQueue().getLocation().getLongitude(), checkInInfo.getQueue().getLocation().getPlace());
                            }
                        });
                    }
                }

                if (isActive) {

                    cvShare.setVisibility(View.VISIBLE);
                    if (checkInInfo.getWaitlistStatus() != null) {
                        if (checkInInfo.getWaitlistStatus().equalsIgnoreCase("Checkedin") || checkInInfo.getWaitlistStatus().equalsIgnoreCase("Arrived")) {
                            llReschedule.setVisibility(View.VISIBLE);
                        } else {
                            hideView(llReschedule);
                        }

                        if (checkInInfo.getWaitlistStatus().equalsIgnoreCase("Checkedin") || checkInInfo.getWaitlistStatus().equalsIgnoreCase("Arrived") || checkInInfo.getWaitlistStatus().equalsIgnoreCase("prepaymentPending")) {
                            llCancel.setVisibility(View.VISIBLE);
                        } else {

                            hideView(llCancel);
                        }
                    }


                    if (checkInInfo.getService() != null) {

                        if (checkInInfo.getService().getLivetrack().equalsIgnoreCase("true")) {
                            llLocation.setVisibility(View.VISIBLE);
                            if (checkInInfo.getJaldeeWaitlistDistanceTime() != null) {
                                tvTrackingText.setText("   Tracking On   ");
                                Glide.with(CheckInDetails.this).load(R.drawable.new_location).into(ivLtIcon);
                            } else {
                                tvTrackingText.setText("   Tracking Off   ");
                                ivLtIcon.setImageResource(R.drawable.location_off);

                            }
                        } else {
                            hideView(llLocation);
                        }
                    }


                } else {
                    cvShare.setVisibility(View.GONE);
                    hideView(llReschedule);
                    hideView(llCancel);
                    hideView(llLocation);
                    if (checkInInfo.isPrescShared()) {
                        llPrescription.setVisibility(View.VISIBLE);
                    } else {
                        hideView(llPrescription);
                    }
                }

                // hide instructions link when there are no post instructions
                if (checkInInfo.getService() != null && checkInInfo.getService().isPostInfoEnabled()) {
                    if (isActive) {
                        llInstructions.setVisibility(View.VISIBLE);
                    }
                } else {

                    hideView(llInstructions);
                }

                // hide customerNotes when there is no notes from consumer
                if (checkInInfo.getConsumerNote() != null && !checkInInfo.getConsumerNote().equalsIgnoreCase("")) {
                    if (isActive) {
                        llCustomerNotes.setVisibility(View.VISIBLE);
                        if (checkInInfo.getProviderAccount() != null) {
                            if (checkInInfo.getProviderAccount().getServiceSector().getDomain().equalsIgnoreCase("healthCare")) {
                                tvCustomerNotes.setText("Patient Note");
                            } else if (checkInInfo.getProviderAccount().getServiceSector().getDomain().equalsIgnoreCase("educationalInstitution")) {
                                tvCustomerNotes.setText("Student Note");
                            } else {
                                tvCustomerNotes.setText("Customer Notes");
                            }
                        }
                    }
                } else {
                    hideView(llCustomerNotes);
                }

                if (checkInInfo.getPaymentStatus().equalsIgnoreCase("FullyPaid") || checkInInfo.getPaymentStatus().equalsIgnoreCase("Refund")) {
                    String amount = "₹" + " " + convertAmountToDecimals(checkInInfo.getAmountDue());
                    tvAmountToPay.setText(amount);
                    tvAmountToPay.setVisibility(View.GONE);
                    cvBill.setVisibility(View.VISIBLE);
                    tvBillText.setVisibility(View.GONE);
                    tvBillReceiptText.setVisibility(View.VISIBLE);
                    tvBillReceiptText.setText("Receipt");
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) cvBill.getLayoutParams();
                    lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                    cvBill.setLayoutParams(lp);
                } else {
                    String amount = "₹" + " " + convertAmountToDecimals(checkInInfo.getAmountDue());
                    if (checkInInfo.getWaitlistStatus().equalsIgnoreCase("Cancelled")) {
                        tvAmountToPay.setVisibility(View.GONE);
                    } else {
                        tvAmountToPay.setText(amount);
                        tvAmountToPay.setVisibility(View.VISIBLE);
                    }
                    cvBill.setVisibility(View.VISIBLE);
                    tvBillText.setText("Bill");
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) cvBill.getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_END);
                    cvBill.setLayoutParams(lp);
                }

                if (checkInInfo.getBillViewStatus() != null && !checkInInfo.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                    if (checkInInfo.getBillViewStatus().equalsIgnoreCase("Show")) {
                        cvBill.setVisibility(View.VISIBLE);
                    } else {
                        cvBill.setVisibility(View.GONE);
                    }

                } else {
                    /**26-3-21*/
                    /**/
                    if (!checkInInfo.getPaymentStatus().equalsIgnoreCase("NotPaid")) {
                        cvBill.setVisibility(View.VISIBLE);
                        if (checkInInfo.getPaymentStatus().equalsIgnoreCase("Refund")) {
                            cvBill.setVisibility(View.GONE);
                        }
                    } else {
                        cvBill.setVisibility(View.GONE);
                    }/**/
                    // cvBill.setVisibility(View.GONE);
                    /***/
                }
                /**26-3-21*/
                if (checkInInfo.getBillViewStatus() == null || checkInInfo.getBillViewStatus().equalsIgnoreCase("NotShow") || checkInInfo.getWaitlistStatus().equals("Rejected")) {
                    cvBill.setVisibility(View.GONE);
                }
                if (checkInInfo.getWaitlistStatus().equalsIgnoreCase("Cancelled"))
                    cvBill.setVisibility(View.GONE);
                /***/
                if (checkInInfo.getParentUuid() != null) {
                    cvBill.setVisibility(View.GONE);
                }


                cvBill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent iBill = new Intent(CheckInDetails.this, BillActivity.class);
                        iBill.putExtra("ynwUUID", checkInInfo.getYnwUuid());
                        iBill.putExtra("provider", checkInInfo.getProviderAccount().getBusinessName());
                        iBill.putExtra("accountID", String.valueOf(checkInInfo.getProviderAccount().getId()));
                        iBill.putExtra("payStatus", checkInInfo.getPaymentStatus());
                        iBill.putExtra("purpose", Constants.PURPOSE_BILLPAYMENT);
                        iBill.putExtra("consumer", checkInInfo.getWaitlistingFor().get(0).getFirstName() + " " + checkInInfo.getWaitlistingFor().get(0).getLastName());
                        iBill.putExtra("uniqueId", checkInInfo.getProviderAccount().getUniqueId());
                        iBill.putExtra("encId", checkInInfo.getCheckinEncId());
                        iBill.putExtra("bookingStatus", checkInInfo.getWaitlistStatus());
                        iBill.putExtra("location", checkInInfo.getQueue().getLocation().getPlace());

                        startActivity(iBill);

                    }
                });

                cvMeetingDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        apiGetMeetingDetails(checkInInfo.getYnwUuid(), checkInInfo.getService().getVirtualCallingModes().get(0).getCallingMode(), checkInInfo.getProviderAccount().getId(), checkInInfo);

                    }
                });

                if (checkInInfo.isPrescShared()) {
                    llPrescription.setVisibility(View.VISIBLE);
                } else {
                    hideView(llPrescription);
                }

                llPrescription.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        prescriptionDialog = new PrescriptionDialog(mContext, isActive, checkInInfo, "checkin");
                        prescriptionDialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
                        prescriptionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        prescriptionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        prescriptionDialog.show();
                        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                        int width = (int) (metrics.widthPixels * 1);
                        prescriptionDialog.getWindow().setGravity(Gravity.BOTTOM);
                        prescriptionDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    BottomSheetDialog dialog;
    float rate = 0;
    String comment = "";

    private void ApiRating(final String accountID, final String UUID) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Map<String, String> query = new HashMap<>();
        query.put("account", accountID);
        query.put("uId-eq", UUID);
        Call<ArrayList<RatingResponse>> call = apiService.getRating(query);
        Config.logV("Location-----###########@@@@@@" + query);
        call.enqueue(new Callback<ArrayList<RatingResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<RatingResponse>> call, final Response<ArrayList<RatingResponse>> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(CheckInDetails.this, mDialog);
                    Config.logV("URL----------Location-----###########@@@@@@-----" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code--------Message-----------------" + response.code());
                    if (response.code() == 200) {
                        final ArrayList<RatingResponse> mRatingDATA = response.body();
                        Config.logV("Response--code--------BottomSheetDialog-----------------" + response.code());
                        dialog = new BottomSheetDialog(mContext);
                        dialog.setContentView(R.layout.rating);
                        dialog.setCancelable(true);
                        dialog.show();
                        TextView tv_title = (TextView) dialog.findViewById(R.id.txtratevisit);
                        final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
                        final RatingBar rating = (RatingBar) dialog.findViewById(R.id.rRatingBar);
                        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                                "fonts/Montserrat_Bold.otf");
                        tv_title.setTypeface(tyface);
                        final Button btn_close = (Button) dialog.findViewById(R.id.btn_cancel);
                        final Button btn_rate = (Button) dialog.findViewById(R.id.btn_send);
                        btn_rate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                rate = rating.getRating();
                                comment = edt_message.getText().toString();
                                if (response.body().size() == 0) {
                                    firstTimeRating = true;
                                } else {
                                    firstTimeRating = false;
                                }
                                ApiPUTRating(Math.round(rate), UUID, comment, accountID, dialog, firstTimeRating);

                            }
                        });


                        edt_message.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void afterTextChanged(Editable arg0) {
                                if (edt_message.getText().toString().length() >= 1 && !edt_message.getText().toString().trim().isEmpty() && rating != null && rating.getRating() != 0) {
                                    btn_rate.setEnabled(true);
                                    btn_rate.setClickable(true);
                                    btn_rate.setBackground(mContext.getResources().getDrawable(R.drawable.curved_save));
                                } else {
                                    btn_rate.setEnabled(false);
                                    btn_rate.setClickable(false);
                                    btn_rate.setBackground(mContext.getResources().getDrawable(R.drawable.btn_checkin_grey));
                                }
                            }

                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }
                        });

                        if (rating != null) {
                            rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                @Override
                                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                                    if (edt_message.getText().toString().length() >= 1 && !edt_message.getText().toString().trim().isEmpty() && rating.getRating() != 0) {
                                        btn_rate.setEnabled(true);
                                        btn_rate.setClickable(true);
                                        btn_rate.setBackground(mContext.getResources().getDrawable(R.drawable.curved_save));
                                    } else {
                                        btn_rate.setEnabled(false);
                                        btn_rate.setClickable(false);
                                        btn_rate.setBackground(mContext.getResources().getDrawable(R.drawable.btn_checkin_grey));
                                    }
                                }
                            });
                        }

                        btn_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        if (response.body().size() > 0) {
                            if (mRatingDATA.get(0).getStars() != 0) {
                                rating.setRating(Float.valueOf(mRatingDATA.get(0).getStars()));
                            }
                            if (mRatingDATA.get(0).getFeedback() != null) {
                                Config.logV("Comments---------" + mRatingDATA.get(0).getFeedback().get(mRatingDATA.get(0).getFeedback().size() - 1).getComments());
                                edt_message.setText(mRatingDATA.get(0).getFeedback().get(mRatingDATA.get(0).getFeedback().size() - 1).getComments());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<RatingResponse>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Location-----###########@@@@@@-------Fail--------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(CheckInDetails.this, mDialog);
            }
        });
    }

    private void apiGetMeetingDetails(String uuid, String mode, int accountID, ActiveCheckIn info) {

        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);

        Call<TeleServiceCheckIn> call = apiService.getMeetingDetails(uuid, mode, accountID);

        call.enqueue(new Callback<TeleServiceCheckIn>() {
            @Override
            public void onResponse(Call<TeleServiceCheckIn> call, Response<TeleServiceCheckIn> response) {

                try {
                    if (response.code() == 200) {

                        meetingDetails = response.body();
                        if (meetingDetails != null) {

                            if (mode.equalsIgnoreCase("GoogleMeet")) {

                                showMeetingDetailsWindow(info, mode, meetingDetails);
                            } else if (mode.equalsIgnoreCase("Zoom")) {

                                showMeetingDetailsWindow(info, mode, meetingDetails);

                            } else if (mode.equalsIgnoreCase("WhatsApp")) {

                                showMeetingWindow(info, mode, meetingDetails);

                            } else if (mode.equalsIgnoreCase("Phone")) {

                                showMeetingWindow(info, mode, meetingDetails);

                            } else if (mode.equalsIgnoreCase("VideoCall")) {

                                showMeetingDetailsWindow(info, mode, meetingDetails);

                            }
                        }
                    }
                } catch (
                        Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<TeleServiceCheckIn> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());

            }
        });
    }

    // for zoom and GMeet
    public void showMeetingDetailsWindow(ActiveCheckIn activeCheckIn, String mode, TeleServiceCheckIn meetingDetails) {

        meetingDetailsWindow = new MeetingDetailsWindow(mContext, activeCheckIn.getCheckInTime(), activeCheckIn.getService().getName(), meetingDetails, activeCheckIn.getService().getVirtualCallingModes().get(0).getCallingMode(), activeCheckIn.getService().getVirtualCallingModes().get(0).getVirtualServiceType());
        meetingDetailsWindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
        meetingDetailsWindow.show();
        meetingDetailsWindow.setCancelable(false);
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 1);
        meetingDetailsWindow.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    // for Whatsspp and Phone
    public void showMeetingWindow(ActiveCheckIn activeCheckIn, String mode, TeleServiceCheckIn meetingDetails) {

        if (mode.equalsIgnoreCase("WhatsApp")) {
            meetingInfo = new MeetingInfo(mContext, activeCheckIn.getCheckInTime(), activeCheckIn.getService().getName(), meetingDetails, activeCheckIn.getService().getVirtualCallingModes().get(0).getCallingMode(), activeCheckIn.getVirtualService().getWhatsApp(), activeCheckIn.getService().getVirtualServiceType(), activeCheckIn.getCountryCode(), Constants.CHECKIN);
        } else {
            meetingInfo = new MeetingInfo(mContext, activeCheckIn.getCheckInTime(), activeCheckIn.getService().getName(), meetingDetails, activeCheckIn.getService().getVirtualCallingModes().get(0).getCallingMode(), activeCheckIn.getVirtualService().getPhone(), "", activeCheckIn.getCountryCode(), Constants.CHECKIN);
        }
        meetingInfo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        meetingInfo.show();
        meetingInfo.setCancelable(false);
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 1);
        meetingInfo.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void ApiPUTRating(final int stars, final String UUID, String feedback, String accountID, final BottomSheetDialog dialog, boolean firstTimerate) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("uuid", UUID);
            jsonObj.put("stars", String.valueOf(stars));
            jsonObj.put("feedback", feedback);
            Config.logV("Feedback--------------" + feedback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call;
        if (firstTimerate) {
            call = apiService.PostRating(accountID, body);
        } else {
            call = apiService.PutRating(accountID, body);
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Config.logV("URL-------Request---" + response.raw().request().url().toString().trim());
                    if (mDialog.isShowing())
                        Config.closeDialog(CheckInDetails.this, mDialog);
                    dialog.dismiss();
                    Config.logV("Put Rating#########################" + response.code());
                    if (response.code() == 200) {
                        if (response.body().string().equalsIgnoreCase("true")) {
                            DynamicToast.make(context, "Rated successfully", AppCompatResources.getDrawable(
                                    context, R.drawable.icon_tickmark),
                                    ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.green), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Location-----###########@@@@@@-------Fail--------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(CheckInDetails.this, mDialog);
            }
        });
    }


    private void ApiDeleteCheckIn(String ynwuuid, String accountID, final BottomSheetDialog dialog) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ResponseBody> call = apiService.deleteActiveCheckIn(ynwuuid, String.valueOf(accountID));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(CheckInDetails.this, mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        if (response.body().string().equalsIgnoreCase("true")) {

                            String mesg = "";
                            if (isToken) {
                                mesg = "Token cancelled successfully";
                            } else {
                                mesg = "CheckIn cancelled successfully";
                            }
                            DynamicToast.make(context, mesg, AppCompatResources.getDrawable(
                                    context, R.drawable.ic_info_black),
                                    ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.green), Toast.LENGTH_SHORT).show();

                            dialog.dismiss();
                            isActive = false;
                            getBookingDetails(activeCheckIn.getYnwUuid(), activeCheckIn.getProviderAccount().getId());

                        }
                    } else {
                        if (response.code() != 419) {
                            Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                        }
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
                    Config.closeDialog(CheckInDetails.this, mDialog);
            }
        });
    }


    public static String convertToTitleForm(String name) {
        String convertName = name;
        convertName = convertName.substring(0, 1).toUpperCase() + convertName.substring(1).toLowerCase();
        return convertName;
    }

    public static String convertAmountToDecimals(double price) {

        DecimalFormat decim = new DecimalFormat("0.00");
        Double price2 = Double.parseDouble(decim.format(price));
        String amount = decim.format(price2);
        return amount;

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

    private void openMapView(String latitude, String longitude, String locationName) {
        Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + locationName);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        }
    }

    private void hideView(View view) {
        GridLayout gridLayout = (GridLayout) view.getParent();
        if (gridLayout != null) {
            for (int i = 0; i < gridLayout.getChildCount(); i++) {
                if (view == gridLayout.getChildAt(i)) {
                    gridLayout.removeViewAt(i);
                    break;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (fromPushNotification) {
            Intent intent = new Intent(CheckInDetails.this, Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            super.onBackPressed();
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    public Date getCurrentDate() {

        Date date = new Date();
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            ;
            String d = formatter.format(date);
            date = (Date) formatter.parse(d);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }
}