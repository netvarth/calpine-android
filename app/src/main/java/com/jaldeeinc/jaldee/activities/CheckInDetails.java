package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewItalicSemiBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewLight;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewRegularItalic;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.CustomerNotes;
import com.jaldeeinc.jaldee.custom.InstructionsDialog;
import com.jaldeeinc.jaldee.model.Bookings;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.RatingResponse;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    LinearLayout llMoreDetails;

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

    @BindView(R.id.tv_instructions)
    CustomTextViewMedium tvInstructions;

    @BindView(R.id.tv_chat)
    CustomTextViewMedium tvChat;

    @BindView(R.id.tv_customerNotes)
    CustomTextViewMedium tvCustomerNotes;

    @BindView(R.id.tv_amountToPay)
    CustomTextViewRegularItalic tvAmountToPay;

    @BindView(R.id.tv_tokenWaitTime)
    CustomTextViewItalicSemiBold tvTokenWaitTime;

    @BindView(R.id.tv_hint)
    CustomTextViewLight tvHint;

    boolean firstTimeRating = false;

    private InstructionsDialog instructionsDialog;
    private CustomerNotes customerNotes;
    private Context mContext;
    private boolean isToken = true;
    private Bookings bookingInfo = new Bookings();
    private boolean isActive = true;
    private ActiveCheckIn activeCheckIn = new ActiveCheckIn();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_details);
        ButterKnife.bind(CheckInDetails.this);
        mContext = CheckInDetails.this;

        Intent i = getIntent();
        if (i != null) {
            bookingInfo = (Bookings) i.getSerializableExtra("bookingInfo");
            isActive = i.getBooleanExtra("isActive", true);

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
                intent.putExtra("checkinInfo", activeCheckIn);
                startActivity(intent);
            }
        });

        tvInstructions.setOnClickListener(new View.OnClickListener() {
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
                    intent.putExtra("uuid", activeCheckIn.getYnwUuid());
                    intent.putExtra("accountId", activeCheckIn.getProviderAccount().getId());
                    intent.putExtra("name", activeCheckIn.getProviderAccount().getBusinessName());
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
                if (isToken){
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

                tvViewMore.setVisibility(View.GONE);
                llMoreDetails.setVisibility(View.VISIBLE);
            }
        });

        llLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent intent = new Intent(mContext, CheckinShareLocationAppointment.class);
                    intent.putExtra("waitlistPhonenumber", activeCheckIn.getWaitlistingFor().get(0).getPhoneNo());
                    intent.putExtra("uuid", activeCheckIn.getYnwUuid());
                    intent.putExtra("accountID", String.valueOf(activeCheckIn.getProviderAccount().getId()));
                    intent.putExtra("title", activeCheckIn.getProviderAccount().getBusinessName());
                    intent.putExtra("terminology", "Check-in");
                    intent.putExtra("calcMode", "Check-in");
                    intent.putExtra("queueStartTime", activeCheckIn.getQueue().getQueueStartTime());
                    intent.putExtra("queueEndTime", activeCheckIn.getQueue().getQueueEndTime());
                    if (activeCheckIn.getJaldeeWaitlistDistanceTime() != null && activeCheckIn.getJaldeeWaitlistDistanceTime().getJaldeeDistanceTime() != null) {
                        intent.putExtra("jaldeeDistance", activeCheckIn.getJaldeeWaitlistDistanceTime().getJaldeeDistanceTime().getJaldeeDistance().getDistance());
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

        tvCustomerNotes.setOnClickListener(new View.OnClickListener() {
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

    }

    @Override
    protected void onResume() {

        // Api call
        if (bookingInfo != null && bookingInfo.getCheckInInfo() != null) {
            getBookingDetails(bookingInfo.getCheckInInfo().getYnwUuid(), bookingInfo.getCheckInInfo().getProviderAccount().getId());
        }
        super.onResume();
    }

    private void getBookingDetails(String uid, int id) {

        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<ActiveCheckIn> call = apiService.getActiveCheckInUUID(uid, String.valueOf(id));
        call.enqueue(new Callback<ActiveCheckIn>() {
            @Override
            public void onResponse(Call<ActiveCheckIn> call, Response<ActiveCheckIn> response) {
                try {
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        activeCheckIn = response.body();

                        if (activeCheckIn != null) {

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
            }
        });
    }

    private void updateUI(ActiveCheckIn checkInInfo) {

        try {

            if (checkInInfo != null) {
                if (checkInInfo.getProvider() != null) {

                    if (checkInInfo.getProvider().getBusinessName() != null && !checkInInfo.getProvider().getBusinessName().equalsIgnoreCase("")) {
                        tvDoctorName.setText(convertToTitleForm(checkInInfo.getProvider().getBusinessName()));
                    } else {
                        String name = checkInInfo.getProvider().getFirstName() + " " + checkInInfo.getProvider().getLastName();
                        tvDoctorName.setText(convertToTitleForm(name));
                    }
                    tvProviderName.setVisibility(View.VISIBLE);
                    tvProviderName.setText(convertToTitleForm(checkInInfo.getProviderAccount().getBusinessName()));
                } else {
                    tvProviderName.setVisibility(View.INVISIBLE);
                    tvDoctorName.setText(convertToTitleForm(checkInInfo.getProviderAccount().getBusinessName()));

                }

                tvViewMore.setVisibility(View.VISIBLE);
                llMoreDetails.setVisibility(View.GONE);

                if (checkInInfo.getService() != null) {
                    tvServiceName.setText(convertToTitleForm(checkInInfo.getService().getName()));

                    if (checkInInfo.getService().getServiceType() != null && checkInInfo.getService().getServiceType().equalsIgnoreCase("virtualService")) {

                        if (checkInInfo.getService().getVirtualCallingModes() != null) {
                            ivTeleService.setVisibility(View.VISIBLE);
                            if (checkInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                ivTeleService.setImageResource(R.drawable.zoom_meet);

                            } else if (checkInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                ivTeleService.setImageResource(R.drawable.google_meet);

                            } else if (checkInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                if (checkInInfo.getService().getVirtualServiceType() != null && checkInInfo.getService().getVirtualServiceType().equalsIgnoreCase("videoService")) {
                                    ivTeleService.setImageResource(R.drawable.whatsapp_videoicon);
                                } else {
                                    ivTeleService.setImageResource(R.drawable.whatsapp_icon);
                                }
                            } else if (checkInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                ivTeleService.setImageResource(R.drawable.phone_icon);
                            }
                        } else {
                            ivTeleService.setVisibility(View.GONE);
                        }
                    }

                }

                // to set confirmation number
                if (checkInInfo.getCheckinEncId() != null) {
                    tvConfirmationNumber.setText(checkInInfo.getCheckinEncId());
                }

                // to set status
                if (checkInInfo.getWaitlistStatus() != null) {
                    tvStatus.setVisibility(View.VISIBLE);
                    if (checkInInfo.getWaitlistStatus().equalsIgnoreCase("Cancelled")) {
                        tvStatus.setTextColor(mContext.getResources().getColor(R.color.red));
                        tvStatus.setText(convertToTitleForm(checkInInfo.getWaitlistStatus()));

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
                    tvDate.setText(date + "\n" + time);

                }

                // to set waitTime or token No with waitTime
                if (checkInInfo.getShowToken() != null && checkInInfo.getShowToken().equalsIgnoreCase("true")) {

                    isToken = true;
                    if (checkInInfo.getCalculationMode() != null && !checkInInfo.getCalculationMode().equalsIgnoreCase("NoCalc")) {

                        tvHint.setText("Token #");
                        tvTime.setText(String.valueOf(checkInInfo.getToken()));
                        tvTime.setGravity(Gravity.CENTER);
                        tvTokenWaitTime.setVisibility(View.VISIBLE);
                        if (checkInInfo.getAppxWaitingTime() == 1){
                            tvTokenWaitTime.setText("Est wait time : "+ checkInInfo.getAppxWaitingTime()+" Min");

                        }else {
                            tvTokenWaitTime.setText("Est wait time : "+ checkInInfo.getAppxWaitingTime()+" Mins");
                        }

                    } else {
                        tvHint.setText("Token #");
                        tvTime.setText(String.valueOf(checkInInfo.getToken()));
                        tvTime.setGravity(Gravity.CENTER);
                        tvTokenWaitTime.setVisibility(View.GONE);
                    }
                } else {

                    isToken = false;
                    tvHint.setText("Est wait time");
                    tvTime.setText(checkInInfo.getAppxWaitingTime() + " Minutes");
                }


                // to set location
                if (checkInInfo.getQueue()!= null && checkInInfo.getQueue().getLocation() != null) {

                    if (checkInInfo.getQueue().getLocation().getPlace() != null) {

                        tvLocationName.setText(checkInInfo.getQueue().getLocation().getPlace());

                        tvLocationName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                openMapView(checkInInfo.getQueue().getLocation().getLattitude(), checkInInfo.getQueue().getLocation().getLongitude(), checkInInfo.getLocation().getPlace());
                            }
                        });
                    }
                }

                if (isActive) {
                    llReschedule.setVisibility(View.VISIBLE);
                    llCancel.setVisibility(View.VISIBLE);

                    if (checkInInfo.getService() != null) {

                        if (checkInInfo.getService().getLivetrack().equalsIgnoreCase("true")) {
                            llLocation.setVisibility(View.VISIBLE);
                            if (checkInInfo.getJaldeeWaitlistDistanceTime() != null) {
                                Glide.with(CheckInDetails.this).load(R.drawable.address).into(ivLtIcon);
                            } else {
                                ivLtIcon.setImageResource(R.drawable.location_off);

                            }
                        } else {
                            llLocation.setVisibility(View.GONE);
                        }
                    }


                } else {
                    llReschedule.setVisibility(View.GONE);
                    llCancel.setVisibility(View.GONE);
                    llLocation.setVisibility(View.GONE);
                }

                // hide instructions link when there are no post instructions
                if (checkInInfo.getService() != null && checkInInfo.getService().isPostInfoEnabled()) {
                    tvInstructions.setVisibility(View.VISIBLE);
                } else {
                    tvInstructions.setVisibility(View.GONE);
                }

                // hide customerNotes when there is no notes from consumer
                if (checkInInfo.getConsumerNote() != null && !checkInInfo.getConsumerNote().equalsIgnoreCase("")) {
                    tvCustomerNotes.setVisibility(View.VISIBLE);
                } else {
                    tvCustomerNotes.setVisibility(View.GONE);
                }

                if (checkInInfo.getPaymentStatus().equalsIgnoreCase("FullyPaid") || checkInInfo.getPaymentStatus().equalsIgnoreCase("Refund")) {
                    String amount = "₹" + " " + convertAmountToDecimals(checkInInfo.getAmountDue());
                    tvAmountToPay.setText(amount);
                    cvBill.setVisibility(View.VISIBLE);
                    tvBillText.setText("Receipt");
                } else {
                    String amount = "₹" + " " + convertAmountToDecimals(checkInInfo.getAmountDue());
                    tvAmountToPay.setText(amount);
                    cvBill.setVisibility(View.VISIBLE);
                    tvBillText.setText("Pay bill");
                }

                if (checkInInfo.getBillViewStatus() != null && !checkInInfo.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                    if (checkInInfo.getBillViewStatus().equalsIgnoreCase("Show")) {
                        cvBill.setVisibility(View.VISIBLE);
                    } else {
                        cvBill.setVisibility(View.GONE);
                    }

                } else {
                    if (!checkInInfo.getPaymentStatus().equalsIgnoreCase("NotPaid")) {
                        cvBill.setVisibility(View.VISIBLE);
                    } else {
                        cvBill.setVisibility(View.GONE);
                    }
                }


                cvBill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent iBill = new Intent(CheckInDetails.this, BillActivity.class);
                        iBill.putExtra("ynwUUID", checkInInfo.getYnwUuid());
                        iBill.putExtra("provider", checkInInfo.getProviderAccount().getBusinessName());
                        iBill.putExtra("accountID", checkInInfo.getProviderAccount().getId());
                        iBill.putExtra("payStatus", checkInInfo.getPaymentStatus());
                        iBill.putExtra("purpose", Constants.PURPOSE_BILLPAYMENT);
                        iBill.putExtra("consumer", checkInInfo.getWaitlistingFor().get(0).getFirstName() + " " + checkInInfo.getWaitlistingFor().get(0).getLastName());
                        iBill.putExtra("uniqueId", checkInInfo.getProviderAccount().getUniqueId());
                        startActivity(iBill);

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
        query.put("account-eq", accountID);
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
                                if (edt_message.getText().toString().length() >= 1 && !edt_message.getText().toString().trim().isEmpty()) {
                                    btn_rate.setEnabled(true);
                                    btn_rate.setClickable(true);
                                    btn_rate.setBackground(mContext.getResources().getDrawable(R.drawable.roundedrect_blue));
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
                            if (isToken){
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


}