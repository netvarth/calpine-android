package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.BookingDetails;
import com.jaldeeinc.jaldee.activities.ChatActivity;
import com.jaldeeinc.jaldee.activities.CheckInDetails;
import com.jaldeeinc.jaldee.activities.CheckinShareLocation;
import com.jaldeeinc.jaldee.activities.CheckinShareLocationAppointment;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.activities.RescheduleActivity;
import com.jaldeeinc.jaldee.activities.RescheduleCheckinActivity;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.model.Bookings;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.RatingResponse;
import com.jaldeeinc.jaldee.response.TeleServiceCheckIn;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jaldeeinc.jaldee.connection.ApiClient.context;

public class ActionsDialog extends Dialog {

    private Context mContext;
    private LinearLayout llReschedule, llMessages, llRating, llCancel, llTrackingOn, llInstructions, llCustomerNotes, llMeetingDetails;
    private CustomTextViewMedium tvTrackingText;
    private boolean isActive = false;
    private Bookings bookings = new Bookings();
    boolean firstTimeRating = false;
    private InstructionsDialog instructionsDialog;
    private CustomerNotes customerNotes;
    private ImageView ivLtIcon, ivMeetIcon;
    private TeleServiceCheckIn meetingDetails;
    private MeetingDetailsWindow meetingDetailsWindow;
    private MeetingInfo meetingInfo;

    public ActionsDialog(@NonNull Context context, boolean isActive, Bookings bookings) {
        super(context);
        this.mContext = context;
        this.isActive = isActive;
        this.bookings = bookings;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actions_layout);

        initializations();

        llMessages.setVisibility(View.VISIBLE);
        if (isActive) {

            // to show reschedule and cancel option based on appt/checkin with "status"
            if (bookings.getAppointmentInfo() != null) {

                // about reschedule option
                if (bookings.getAppointmentInfo().getApptStatus().equalsIgnoreCase("Confirmed") || bookings.getAppointmentInfo().getApptStatus().equalsIgnoreCase("Arrived")) {
                    llReschedule.setVisibility(View.VISIBLE);
                } else {
                    hideView(llReschedule);
                }

                // about cancel option
                if (bookings.getAppointmentInfo().getApptStatus().equalsIgnoreCase("Confirmed") || bookings.getAppointmentInfo().getApptStatus().equalsIgnoreCase("Arrived") || bookings.getAppointmentInfo().getApptStatus().equalsIgnoreCase("prepaymentPending")) {
                    llCancel.setVisibility(View.VISIBLE);
                } else {

                    hideView(llCancel);
                }

                // about liveTracking option
                if (bookings.getAppointmentInfo().getService() != null) {
                    if (bookings.getAppointmentInfo().getService().getLivetrack().equalsIgnoreCase("true")) {
                        llTrackingOn.setVisibility(View.VISIBLE);
                        if (bookings.getAppointmentInfo().getJaldeeApptDistanceTime() != null) {
                            tvTrackingText.setText("   Tracking On   ");
                            Glide.with(mContext).load(R.drawable.new_location).into(ivLtIcon);
                        } else {
                            tvTrackingText.setText("   Tracking Off   ");
                            ivLtIcon.setImageResource(R.drawable.location_off);
                        }
                    } else {
                        hideView(llTrackingOn);
                    }
                }

                // hide instructions link when there are no post instructions
                if (bookings.getAppointmentInfo().getService() != null && bookings.getAppointmentInfo().getService().isPostInfoEnabled()) {
                    llInstructions.setVisibility(View.VISIBLE);
                } else {
                    hideView(llInstructions);
                }

                // hide customerNotes when there is no notes from consumer
                if (bookings.getAppointmentInfo().getConsumerNote() != null && !bookings.getAppointmentInfo().getConsumerNote().equalsIgnoreCase("")) {
                    llCustomerNotes.setVisibility(View.VISIBLE);
                } else {
                    hideView(llCustomerNotes);
                }


                // To show meetingDetails
                if (bookings.getAppointmentInfo().getService() != null) {
                    if (bookings.getAppointmentInfo().getService().getServiceType() != null && bookings.getAppointmentInfo().getService().getServiceType().equalsIgnoreCase("virtualService")) {

                        llMeetingDetails.setVisibility(View.VISIBLE);
                        if (bookings.getAppointmentInfo().getService().getVirtualCallingModes() != null) {
                            if (bookings.getAppointmentInfo().getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                ivMeetIcon.setImageResource(R.drawable.zoom_meet);
                            } else if (bookings.getAppointmentInfo().getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                ivMeetIcon.setImageResource(R.drawable.google_meet);
                            } else if (bookings.getAppointmentInfo().getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                if (bookings.getAppointmentInfo().getService().getVirtualServiceType() != null && bookings.getAppointmentInfo().getService().getVirtualServiceType().equalsIgnoreCase("videoService")) {
                                    ivMeetIcon.setImageResource(R.drawable.whatsapp_videoicon);
                                } else {
                                    ivMeetIcon.setImageResource(R.drawable.whatsapp_icon);
                                }
                            } else if (bookings.getAppointmentInfo().getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                ivMeetIcon.setImageResource(R.drawable.phone_icon);
                            }
                        } else {
                            llMeetingDetails.setVisibility(View.GONE);
                        }
                    } else {

                        llMeetingDetails.setVisibility(View.GONE);
                    }
                }



            } else if (bookings.getCheckInInfo() != null) {

                // about reschedule option
                if (bookings.getCheckInInfo().getWaitlistStatus().equalsIgnoreCase("Checkedin") || bookings.getCheckInInfo().getWaitlistStatus().equalsIgnoreCase("Arrived")) {
                    llReschedule.setVisibility(View.VISIBLE);
                } else {
                    hideView(llReschedule);
                }

                // about cancel option
                if (bookings.getCheckInInfo().getWaitlistStatus().equalsIgnoreCase("Checkedin") || bookings.getCheckInInfo().getWaitlistStatus().equalsIgnoreCase("Arrived") || bookings.getCheckInInfo().getWaitlistStatus().equalsIgnoreCase("prepaymentPending")) {
                    llCancel.setVisibility(View.VISIBLE);
                } else {

                    hideView(llCancel);
                }

                // about liveTracking option
                if (bookings.getCheckInInfo().getService() != null) {
                    if (bookings.getCheckInInfo().getService().getLivetrack().equalsIgnoreCase("true")) {
                        llTrackingOn.setVisibility(View.VISIBLE);
                        if (bookings.getCheckInInfo().getJaldeeWaitlistDistanceTime() != null) {
                            tvTrackingText.setText("   Tracking On   ");
                            Glide.with(mContext).load(R.drawable.new_location).into(ivLtIcon);
                        } else {
                            tvTrackingText.setText("   Tracking Off   ");
                            ivLtIcon.setImageResource(R.drawable.location_off);

                        }
                    } else {
                        hideView(llTrackingOn);
                    }
                }


                // hide instructions link when there are no post instructions
                if (bookings.getCheckInInfo().getService() != null && bookings.getCheckInInfo().getService().isPostInfoEnabled()) {
                    llInstructions.setVisibility(View.VISIBLE);
                } else {
                    hideView(llInstructions);
                }

                // hide customerNotes when there is no notes from consumer
                if (bookings.getCheckInInfo().getConsumerNote() != null && !bookings.getCheckInInfo().getConsumerNote().equalsIgnoreCase("")) {
                    if (isActive) {
                        llCustomerNotes.setVisibility(View.VISIBLE);
                    }
                } else {
                    hideView(llCustomerNotes);
                }

                // show meetingDetails
                if (bookings.getCheckInInfo().getService() != null) {
                    if (bookings.getCheckInInfo().getService().getServiceType() != null && bookings.getCheckInInfo().getService().getServiceType().equalsIgnoreCase("virtualService")) {
                        llMeetingDetails.setVisibility(View.VISIBLE);
                        if (bookings.getCheckInInfo().getService().getVirtualCallingModes() != null) {
                            if (bookings.getCheckInInfo().getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                ivMeetIcon.setImageResource(R.drawable.zoom_meet);
                            } else if (bookings.getCheckInInfo().getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                ivMeetIcon.setImageResource(R.drawable.google_meet);
                            } else if (bookings.getCheckInInfo().getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                if (bookings.getCheckInInfo().getService().getVirtualServiceType() != null && bookings.getCheckInInfo().getService().getVirtualServiceType().equalsIgnoreCase("videoService")) {
                                    ivMeetIcon.setImageResource(R.drawable.whatsapp_videoicon);
                                } else {
                                    ivMeetIcon.setImageResource(R.drawable.whatsapp_icon);
                                }
                            } else if (bookings.getCheckInInfo().getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                ivMeetIcon.setImageResource(R.drawable.phone_icon);
                            }
                        } else {
                            llMeetingDetails.setVisibility(View.GONE);
                        }
                    } else {
                        llMeetingDetails.setVisibility(View.GONE);
                    }
                }
            }

        } else {

            hideView(llReschedule);
            hideView(llCancel);
            hideView(llTrackingOn);
            hideView(llInstructions);
            hideView(llCustomerNotes);
            hideView(llMeetingDetails);
            llRating.setVisibility(View.VISIBLE);

        }

        if (bookings.getAppointmentInfo() != null) {
            if (bookings.getAppointmentInfo().getApptStatus() != null) {
                if (bookings.getAppointmentInfo().getApptStatus().equalsIgnoreCase("Completed")) {
                    llRating.setVisibility(View.VISIBLE);
                } else {
                    hideView(llRating);
                }
            }
        } else if (bookings.getCheckInInfo() != null) {

            if (bookings.getCheckInInfo().getWaitlistStatus() != null) {

                if (bookings.getCheckInInfo().getWaitlistStatus().equalsIgnoreCase("done")) {
                    llRating.setVisibility(View.VISIBLE);
                } else {
                    hideView(llRating);
                }
            }
        }

        llMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    if (bookings.getAppointmentInfo() != null) {
                        intent.putExtra("uuid", bookings.getAppointmentInfo().getUid());
                        intent.putExtra("accountId", bookings.getAppointmentInfo().getProviderAccount().getId());
                        intent.putExtra("name", bookings.getAppointmentInfo().getProviderAccount().getBusinessName());
                    } else if (bookings.getCheckInInfo() != null) {
                        intent.putExtra("uuid", bookings.getCheckInInfo().getYnwUuid());
                        intent.putExtra("accountId", bookings.getCheckInInfo().getProviderAccount().getId());
                        intent.putExtra("name", bookings.getCheckInInfo().getProviderAccount().getBusinessName());
                    }
                    mContext.startActivity(intent);
                    dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        llReschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (bookings.getAppointmentInfo() != null) {
                        Intent intent = new Intent(mContext, RescheduleActivity.class);
                        intent.putExtra("appointmentInfo", bookings.getAppointmentInfo());
                        mContext.startActivity(intent);
                    } else if (bookings.getCheckInInfo() != null) {

                        Intent intent = new Intent(mContext, RescheduleCheckinActivity.class);
                        intent.putExtra("uniqueId", bookings.getCheckInInfo().getProviderAccount().getUniqueId());
                        intent.putExtra("ynwuuid", bookings.getCheckInInfo().getYnwUuid());
                        intent.putExtra("providerId", bookings.getCheckInInfo().getProviderAccount().getId());
                        mContext.startActivity(intent);
                    }

                    dismiss();
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
                Button btSend = (Button) dialog.findViewById(R.id.btn_send);
                Button btCancel = (Button) dialog.findViewById(R.id.btn_cancel);
                final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
                TextView txtsendmsg = (TextView) dialog.findViewById(R.id.txtsendmsg);
                if (bookings.getAppointmentInfo() != null) {
                    txtsendmsg.setText("Do you want to cancel this Appointment?");
                } else if (bookings.getCheckInInfo() != null) {
                    if (bookings.getBookingType().equalsIgnoreCase(Constants.CHECKIN)) {
                        txtsendmsg.setText("Do you want to cancel this CheckIn?");
                    } else {
                        txtsendmsg.setText("Do you want to cancel this Token?");
                    }
                }
                btSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (bookings.getAppointmentInfo() != null) {

                            ApiDeleteAppointment(bookings.getAppointmentInfo().getUid(), String.valueOf(bookings.getAppointmentInfo().getProviderAccount().getId()), dialog);

                        } else if (bookings.getCheckInInfo() != null) {

                            ApiDeleteCheckIn(bookings.getCheckInInfo().getYnwUuid(), String.valueOf(bookings.getCheckInInfo().getProviderAccount().getId()), dialog);

                        }
                    }
                });
                btCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        llRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bookings.getAppointmentInfo() != null) {

                    ApiRating(String.valueOf(bookings.getAppointmentInfo().getProviderAccount().getId()), bookings.getAppointmentInfo().getUid());

                } else if (bookings.getCheckInInfo() != null) {

                    ApiRating(String.valueOf(bookings.getCheckInInfo().getProviderAccount().getId()), bookings.getCheckInInfo().getYnwUuid());

                }
            }
        });

        llTrackingOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (bookings.getAppointmentInfo() != null) {
                        Intent intent = new Intent(mContext, CheckinShareLocationAppointment.class);
                        intent.putExtra("waitlistPhonenumber", bookings.getAppointmentInfo().getConsumer().getUserProfile().getPrimaryMobileNo());
                        intent.putExtra("uuid", bookings.getAppointmentInfo().getUid());
                        intent.putExtra("accountID", String.valueOf(bookings.getAppointmentInfo().getProviderAccount().getId()));
                        intent.putExtra("title", bookings.getAppointmentInfo().getProviderAccount().getBusinessName());
                        intent.putExtra("terminology", "Check-in");
                        intent.putExtra("calcMode", "Check-in");
                        intent.putExtra("queueStartTime", bookings.getAppointmentInfo().getSchedule().getApptSchedule().getTimeSlots().get(0).getsTime());
                        intent.putExtra("queueEndTime", bookings.getAppointmentInfo().getSchedule().getApptSchedule().getTimeSlots().get(0).geteTime());
                        if (bookings.getAppointmentInfo().getJaldeeApptDistanceTime() != null && bookings.getAppointmentInfo().getJaldeeApptDistanceTime().getJaldeeDistanceTime() != null) {
                            intent.putExtra("jaldeeDistance", bookings.getAppointmentInfo().getJaldeeApptDistanceTime().getJaldeeDistanceTime().getJaldeeDistance().getDistance());
                        }
                        mContext.startActivity(intent);
                    } else if (bookings.getCheckInInfo() != null) {

                        Intent intent = new Intent(mContext, CheckinShareLocation.class);
                        intent.putExtra("waitlistPhonenumber", bookings.getCheckInInfo().getWaitlistingFor().get(0).getPhoneNo());
                        intent.putExtra("uuid", bookings.getCheckInInfo().getYnwUuid());
                        intent.putExtra("accountID", String.valueOf(bookings.getCheckInInfo().getProviderAccount().getId()));
                        intent.putExtra("title", bookings.getCheckInInfo().getProviderAccount().getBusinessName());
                        intent.putExtra("terminology", "Check-in");
                        intent.putExtra("calcMode", "Check-in");
                        intent.putExtra("queueStartTime", bookings.getCheckInInfo().getQueue().getQueueStartTime());
                        intent.putExtra("queueEndTime", bookings.getCheckInInfo().getQueue().getQueueEndTime());
                        if (bookings.getCheckInInfo().getJaldeeWaitlistDistanceTime() != null && bookings.getCheckInInfo().getJaldeeWaitlistDistanceTime().getJaldeeDistanceTime() != null) {
                            intent.putExtra("jaldeeDistance", String.valueOf(bookings.getCheckInInfo().getJaldeeWaitlistDistanceTime().getJaldeeDistanceTime().getJaldeeDistance().getDistance()));
                        }
                        mContext.startActivity(intent);
                    }
                    dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        llInstructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    if (bookings.getAppointmentInfo() != null) {
                        if (bookings.getAppointmentInfo().getService() != null) {
                            instructionsDialog = new InstructionsDialog(mContext, bookings.getAppointmentInfo().getService().getPostInfoText(), bookings.getAppointmentInfo().getService().getPostInfoTitle());
                            instructionsDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                            instructionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            instructionsDialog.show();
                            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                            int width = (int) (metrics.widthPixels * 1);
                            instructionsDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                        }
                    } else if (bookings.getCheckInInfo() != null) {
                        if (bookings.getCheckInInfo().getService() != null) {
                            instructionsDialog = new InstructionsDialog(mContext, bookings.getCheckInInfo().getService().getPostInfoText(), bookings.getCheckInInfo().getService().getPostInfoTitle());
                            instructionsDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                            instructionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            instructionsDialog.show();
                            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                            int width = (int) (metrics.widthPixels * 1);
                            instructionsDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                        }
                    }

                    dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        llCustomerNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    if (bookings.getAppointmentInfo() != null) {

                        if (bookings.getAppointmentInfo().getService() != null) {
                            customerNotes = new CustomerNotes(mContext, bookings.getAppointmentInfo().getService().getConsumerNoteTitle(), bookings.getAppointmentInfo().getConsumerNote());
                            customerNotes.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                            customerNotes.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            customerNotes.show();
                            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                            int width = (int) (metrics.widthPixels * 1);
                            customerNotes.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                        }

                    } else if (bookings.getCheckInInfo() != null) {

                        if (bookings.getCheckInInfo().getService() != null) {
                            customerNotes = new CustomerNotes(mContext, bookings.getCheckInInfo().getService().getConsumerNoteTitle(), bookings.getCheckInInfo().getConsumerNote());
                            customerNotes.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                            customerNotes.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            customerNotes.show();
                            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                            int width = (int) (metrics.widthPixels * 1);
                            customerNotes.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                        }

                    }

                    dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        llMeetingDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    if (bookings.getAppointmentInfo() != null){

                        apiGetMeetingDetails(bookings.getAppointmentInfo().getUid(), bookings.getAppointmentInfo().getService().getVirtualCallingModes().get(0).getCallingMode(), bookings.getAppointmentInfo().getProviderAccount().getId(), bookings.getAppointmentInfo());

                    } else  if (bookings.getCheckInInfo() != null){

                        apiGetMeetingDetails(bookings.getCheckInInfo().getYnwUuid(), bookings.getCheckInInfo().getService().getVirtualCallingModes().get(0).getCallingMode(), bookings.getCheckInInfo().getProviderAccount().getId(), bookings.getCheckInInfo());

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void initializations() {

        llRating = findViewById(R.id.ll_rating);
        llReschedule = findViewById(R.id.ll_reschedule);
        llMessages = findViewById(R.id.ll_message);
        llCancel = findViewById(R.id.ll_cancel);
        llTrackingOn = findViewById(R.id.ll_location);
        llInstructions = findViewById(R.id.ll_instructions);
        llCustomerNotes = findViewById(R.id.ll_customerNotes);
        llMeetingDetails = findViewById(R.id.ll_meetingDetails);
        tvTrackingText = findViewById(R.id.tv_trackingText);
        ivLtIcon = findViewById(R.id.iv_ltIcon);
        ivMeetIcon = findViewById(R.id.iv_meetIcon);
    }

    private void apiGetMeetingDetails(String uuid, String mode, int accountID, ActiveAppointment info) {

        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);

        Call<TeleServiceCheckIn> call = apiService.getMeetingDetailsAppointment(uuid, mode, accountID);

        call.enqueue(new Callback<TeleServiceCheckIn>() {
            @Override
            public void onResponse(Call<TeleServiceCheckIn> call, Response<TeleServiceCheckIn> response) {

                try {
                    dismiss();
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
    public void showMeetingDetailsWindow(ActiveAppointment activeAppointment, String mode, TeleServiceCheckIn meetingDetails) {

        meetingDetailsWindow = new MeetingDetailsWindow(mContext, activeAppointment.getApptTime(), activeAppointment.getService().getName(), meetingDetails, activeAppointment.getService().getVirtualCallingModes().get(0).getCallingMode());
        meetingDetailsWindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
        meetingDetailsWindow.show();
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 1);
        meetingDetailsWindow.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    // for Whatsspp and Phone
    public void showMeetingWindow(ActiveAppointment activeAppointment, String mode, TeleServiceCheckIn meetingDetails) {

        if (mode.equalsIgnoreCase("WhatsApp")) {
            meetingInfo = new MeetingInfo(mContext, activeAppointment.getApptTime(), activeAppointment.getService().getName(), meetingDetails, activeAppointment.getService().getVirtualCallingModes().get(0).getCallingMode(), activeAppointment.getVirtualService().getWhatsApp());
        } else {
            meetingInfo = new MeetingInfo(mContext, activeAppointment.getApptTime(), activeAppointment.getService().getName(), meetingDetails, activeAppointment.getService().getVirtualCallingModes().get(0).getCallingMode(), activeAppointment.getVirtualService().getPhoneNo());
        }
        meetingInfo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        meetingInfo.show();
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 1);
        meetingInfo.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }


    private void apiGetMeetingDetails(String uuid, String mode, int accountID, ActiveCheckIn info) {

        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);

        Call<TeleServiceCheckIn> call = apiService.getMeetingDetails(uuid, mode, accountID);

        call.enqueue(new Callback<TeleServiceCheckIn>() {
            @Override
            public void onResponse(Call<TeleServiceCheckIn> call, Response<TeleServiceCheckIn> response) {

                try {
                    dismiss();
                    if (response.code() == 200) {

                        meetingDetails = response.body();
                        if (meetingDetails != null) {

                            if (mode.equalsIgnoreCase("GoogleMeet")) {

                                showCheckInMeetingDetailsWindow(info, mode, meetingDetails);
                            } else if (mode.equalsIgnoreCase("Zoom")) {

                                showCheckInMeetingDetailsWindow(info, mode, meetingDetails);

                            } else if (mode.equalsIgnoreCase("WhatsApp")) {

                                showCheckInMeetingWindow(info, mode, meetingDetails);

                            } else if (mode.equalsIgnoreCase("Phone")) {

                                showCheckInMeetingWindow(info, mode, meetingDetails);

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
    public void showCheckInMeetingDetailsWindow(ActiveCheckIn activeCheckIn, String mode, TeleServiceCheckIn meetingDetails) {

        meetingDetailsWindow = new MeetingDetailsWindow(mContext, activeCheckIn.getCheckInTime(), activeCheckIn.getService().getName(), meetingDetails, activeCheckIn.getService().getVirtualCallingModes().get(0).getCallingMode());
        meetingDetailsWindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
        meetingDetailsWindow.show();
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 1);
        meetingDetailsWindow.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    // for Whatsspp and Phone
    public void showCheckInMeetingWindow(ActiveCheckIn activeCheckIn, String mode, TeleServiceCheckIn meetingDetails) {

        if (mode.equalsIgnoreCase("WhatsApp")) {
            meetingInfo = new MeetingInfo(mContext, activeCheckIn.getCheckInTime(), activeCheckIn.getService().getName(), meetingDetails, activeCheckIn.getService().getVirtualCallingModes().get(0).getCallingMode(), activeCheckIn.getVirtualService().getWhatsApp());
        } else {
            meetingInfo = new MeetingInfo(mContext, activeCheckIn.getCheckInTime(), activeCheckIn.getService().getName(), meetingDetails, activeCheckIn.getService().getVirtualCallingModes().get(0).getCallingMode(), activeCheckIn.getVirtualService().getPhone());
        }
        meetingInfo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        meetingInfo.show();
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 1);
        meetingInfo.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }


    private void ApiDeleteAppointment(String ynwuuid, String accountID, final BottomSheetDialog dialog) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ResponseBody> call = apiService.deleteAppointment(ynwuuid, String.valueOf(accountID));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getOwnerActivity(), mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        if (response.body().string().equalsIgnoreCase("true")) {
                            DynamicToast.make(context, "Appointment cancelled successfully", AppCompatResources.getDrawable(
                                    context, R.drawable.ic_info_black),
                                    ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.green), Toast.LENGTH_SHORT).show();

                            dialog.dismiss();
                            dismiss();

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
                    Config.closeDialog(getOwnerActivity(), mDialog);
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
                        Config.closeDialog(getOwnerActivity(), mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        if (response.body().string().equalsIgnoreCase("true")) {

                            String mesg = "";
                            if (bookings.getBookingType().equalsIgnoreCase(Constants.TOKEN)) {
                                mesg = "Token cancelled successfully";
                            } else {
                                mesg = "CheckIn cancelled successfully";
                            }
                            DynamicToast.make(context, mesg, AppCompatResources.getDrawable(
                                    context, R.drawable.ic_info_black),
                                    ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.green), Toast.LENGTH_SHORT).show();

                            dialog.dismiss();
                            dismiss();

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
                    Config.closeDialog(getOwnerActivity(), mDialog);
            }
        });
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
        Call<ArrayList<RatingResponse>> call = apiService.getRatingApp(query);
        Config.logV("Location-----###########@@@@@@" + query);
        call.enqueue(new Callback<ArrayList<RatingResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<RatingResponse>> call, final Response<ArrayList<RatingResponse>> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getOwnerActivity(), mDialog);
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
                    Config.closeDialog(getOwnerActivity(), mDialog);
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
            call = apiService.PostRatingApp(accountID, body);
        } else {
            call = apiService.PutRatingApp(accountID, body);
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Config.logV("URL-------Request---" + response.raw().request().url().toString().trim());
                    if (mDialog.isShowing())
                        Config.closeDialog(getOwnerActivity(), mDialog);
                    dialog.dismiss();
                    Config.logV("Put Rating#########################" + response.code());
                    if (response.code() == 200) {
                        if (response.body().string().equalsIgnoreCase("true")) {
                            DynamicToast.make(context, "Rated successfully", AppCompatResources.getDrawable(
                                    context, R.drawable.icon_tickmark),
                                    ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.green), Toast.LENGTH_SHORT).show();
                            dismiss();
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
                    Config.closeDialog(getOwnerActivity(), mDialog);
            }
        });
    }

    private void ApiCheckInRating(final String accountID, final String UUID) {
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
                        Config.closeDialog(getOwnerActivity(), mDialog);
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
                    Config.closeDialog(getOwnerActivity(), mDialog);
            }
        });
    }

    private void ApiPUTCheckInRating(final int stars, final String UUID, String feedback, String accountID, final BottomSheetDialog dialog, boolean firstTimerate) {
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
                        Config.closeDialog(getOwnerActivity(), mDialog);
                    dialog.dismiss();
                    Config.logV("Put Rating#########################" + response.code());
                    if (response.code() == 200) {
                        if (response.body().string().equalsIgnoreCase("true")) {
                            DynamicToast.make(context, "Rated successfully", AppCompatResources.getDrawable(
                                    context, R.drawable.icon_tickmark),
                                    ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.green), Toast.LENGTH_SHORT).show();
                            dismiss();
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
                    Config.closeDialog(getOwnerActivity(), mDialog);
            }
        });
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
}
