package com.jaldeeinc.jaldee.custom;

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
import android.view.Gravity;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.jaldeeinc.jaldee.Interface.ISendData;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.BillActivity;
import com.jaldeeinc.jaldee.activities.ChatActivity;
import com.jaldeeinc.jaldee.activities.CheckinShareLocation;
import com.jaldeeinc.jaldee.activities.CheckinShareLocationAppointment;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.activities.CustomQuestionnaire;
import com.jaldeeinc.jaldee.activities.RescheduleActivity;
import com.jaldeeinc.jaldee.activities.RescheduleCheckinActivity;
import com.jaldeeinc.jaldee.activities.UpdateQuestionnaire;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.model.Bookings;
import com.jaldeeinc.jaldee.model.LabelPath;
import com.jaldeeinc.jaldee.model.QuestionnaireResponseInput;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.AnswerLineResponse;
import com.jaldeeinc.jaldee.response.GetQuestion;
import com.jaldeeinc.jaldee.response.QuestionAnswers;
import com.jaldeeinc.jaldee.response.QuestionnaireResponse;
import com.jaldeeinc.jaldee.response.RatingResponse;
import com.jaldeeinc.jaldee.response.TeleServiceCheckIn;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
    private LinearLayout llReschedule, llMessages, llRating, llCancel, llTrackingOn, llInstructions, llCustomerNotes, llMeetingDetails, llBillDetails, llPrescription, llSendAttachments, llViewAttachments, llQuestionnaire;
    private CustomTextViewMedium tvTrackingText, tvBillText, tvCustomerNotes;
    private boolean isActive = false;
    private Bookings bookings = new Bookings();
    boolean firstTimeRating = false;
    private InstructionsDialog instructionsDialog;
    private CustomerNotes customerNotes;
    private ImageView ivLtIcon, ivMeetIcon, ivBillIcon;
    private TeleServiceCheckIn meetingDetails;
    private MeetingDetailsWindow meetingDetailsWindow;
    private MeetingInfo meetingInfo;
    private PrescriptionDialog prescriptionDialog;
    ArrayList<String> imagePathList = new ArrayList<>();
    ArrayList<String> imagePathLists = new ArrayList<>();
    Bitmap bitmap;
    File f, file;
    String path, from, from1 = "";
    private LinearLayout llNoHistory;
    private ImageView iv_attach;
    TextView tv_attach, tv_camera;
    private BottomSheetDialog dialog;
    CustomTextViewSemiBold tvErrorMessage;
    RecyclerView recycle_image_attachment;
    private int GALLERY = 1, CAMERA = 2;
    String[] fileExtsSupported = new String[]{"jpg", "jpeg", "png", "pdf"};
    private static final String IMAGE_DIRECTORY = "/Jaldee" + "";
    private Uri mImageUri;
    private ISendData iSendData;

    public ActionsDialog(@NonNull Context context, boolean isActive, Bookings bookings, ISendData iSendData) {
        super(context);
        this.mContext = context;
        this.isActive = isActive;
        this.bookings = bookings;
        this.iSendData = iSendData;
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

                // to show questionnaire option

                if (bookings.getAppointmentInfo().getQuestionnaire() != null && bookings.getAppointmentInfo().getQuestionnaire().getQuestionAnswers() != null && bookings.getAppointmentInfo().getQuestionnaire().getQuestionAnswers().size() > 0) {

                    llQuestionnaire.setVisibility(View.VISIBLE);
                } else {

                    hideView(llQuestionnaire);
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
                    if (bookings.getAppointmentInfo().getProviderAccount() != null) {
                        if (bookings.getAppointmentInfo().getProviderAccount().getServiceSector().getDomain().equalsIgnoreCase("healthCare")) {
                            tvCustomerNotes.setText("Patient Note");
                        } else if (bookings.getAppointmentInfo().getProviderAccount().getServiceSector().getDomain().equalsIgnoreCase("educationalInstitution")) {
                            tvCustomerNotes.setText("Student Note");
                        } else {
                            tvCustomerNotes.setText("Customer Notes");
                        }
                    }
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
                                ivMeetIcon.setImageResource(R.drawable.phoneicon_sized);
                            } else if (bookings.getAppointmentInfo().getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("VideoCall")) {
                                ivMeetIcon.setImageResource(R.drawable.ic_jaldeevideo);
                            }
                        } else {
                            hideView(llMeetingDetails);
                        }
                    } else {
                        hideView(llMeetingDetails);
                    }

                    // To show Bill details

                    if (bookings.getAppointmentInfo().getPaymentStatus().equalsIgnoreCase("FullyPaid") || bookings.getAppointmentInfo().getPaymentStatus().equalsIgnoreCase("Refund")) {
                        ivBillIcon.setVisibility(View.VISIBLE);
                        tvBillText.setText("Receipt");
                    } else {
                        ivBillIcon.setVisibility(View.VISIBLE);
                        tvBillText.setText("Pay Bill");
                    }

                    if (bookings.getAppointmentInfo().getBillViewStatus() != null && !bookings.getAppointmentInfo().getApptStatus().equalsIgnoreCase("cancelled")) {
                        if (bookings.getAppointmentInfo().getBillViewStatus().equalsIgnoreCase("Show")) {
                            ivBillIcon.setVisibility(View.VISIBLE);
                        } else {
                            ivBillIcon.setVisibility(View.GONE);
                            hideView(llBillDetails);
                        }

                    } else {
                        if (!bookings.getAppointmentInfo().getPaymentStatus().equalsIgnoreCase("NotPaid")) {
                            ivBillIcon.setVisibility(View.VISIBLE);
                        } else {
                            ivBillIcon.setVisibility(View.GONE);
                            hideView(llBillDetails);
                        }
                    }
                    /**26-3-21*/
                    if (bookings.getAppointmentInfo().getBillViewStatus() == null || bookings.getAppointmentInfo().getBillViewStatus().equalsIgnoreCase("NotShow") || bookings.getAppointmentInfo().getBillStatus().equals("Settled") || bookings.getAppointmentInfo().getApptStatus().equals("Rejected")) {
                        ivBillIcon.setVisibility(View.GONE);
                        hideView(llBillDetails);
                    }
                    /***/

                    ivBillIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent iBill = new Intent(context, BillActivity.class);
                            iBill.putExtra("ynwUUID", bookings.getAppointmentInfo().getUid());
                            iBill.putExtra("provider", bookings.getAppointmentInfo().getProviderAccount().getBusinessName());
                            iBill.putExtra("accountID", String.valueOf(bookings.getAppointmentInfo().getProviderAccount().getId()));
                            iBill.putExtra("payStatus", bookings.getAppointmentInfo().getPaymentStatus());
                            iBill.putExtra("purpose", Constants.PURPOSE_BILLPAYMENT);
                            iBill.putExtra("consumer", bookings.getAppointmentInfo().getAppmtFor().get(0).getFirstName() + " " + bookings.getAppointmentInfo().getAppmtFor().get(0).getLastName());
                            iBill.putExtra("uniqueId", bookings.getAppointmentInfo().getProviderAccount().getUniqueId());
                            iBill.putExtra("encId", bookings.getAppointmentInfo().getAppointmentEncId());
                            iBill.putExtra("bookingStatus", bookings.getAppointmentInfo().getApptStatus());
                            iBill.putExtra("location", bookings.getAppointmentInfo().getLocation().getPlace());

                            v.getContext().startActivity(iBill);
                        }
                    });

                    // To show prescription
                    if (bookings.getAppointmentInfo().isPrescShared()) {
                        llPrescription.setVisibility(View.VISIBLE);
                    } else {
                        hideView(llPrescription);
                    }

                    llPrescription.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            prescriptionDialog = new PrescriptionDialog(mContext, isActive, bookings);
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

                // to show Questionnaire option
                if (bookings.getCheckInInfo().getQuestionnaire() != null && bookings.getCheckInInfo().getQuestionnaire().getQuestionAnswers() != null && bookings.getCheckInInfo().getQuestionnaire().getQuestionAnswers().size() > 0) {

                    llQuestionnaire.setVisibility(View.VISIBLE);
                } else {

                    hideView(llQuestionnaire);
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
                        if (bookings.getCheckInInfo().getProviderAccount() != null) {
                            if (bookings.getCheckInInfo().getProviderAccount().getServiceSector().getDomain().equalsIgnoreCase("healthCare")) {
                                tvCustomerNotes.setText("Patient Note");
                            } else if (bookings.getCheckInInfo().getProviderAccount().getServiceSector().getDomain().equalsIgnoreCase("educationalInstitution")) {
                                tvCustomerNotes.setText("Student Note");
                            } else {
                                tvCustomerNotes.setText("Customer Notes");
                            }
                        }
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
                                ivMeetIcon.setImageResource(R.drawable.phoneicon_sized);
                            } else if (bookings.getCheckInInfo().getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("VideoCall")) {
                                ivMeetIcon.setImageResource(R.drawable.ic_jaldeevideo);
                            }
                        } else {
                            hideView(llMeetingDetails);
                        }
                    } else {
                        hideView(llMeetingDetails);
                    }
                }

                // To show Bill details

                if (bookings.getCheckInInfo().getPaymentStatus().equalsIgnoreCase("FullyPaid") || bookings.getCheckInInfo().getPaymentStatus().equalsIgnoreCase("Refund") && bookings.getCheckInInfo().getBillViewStatus() != null) {
                    ivBillIcon.setVisibility(View.VISIBLE);
                    tvBillText.setText("Receipt");
                } else {
                    ivBillIcon.setVisibility(View.VISIBLE);
                    tvBillText.setText("Pay Bill");
                }

                if (bookings.getCheckInInfo().getBillViewStatus() != null && !bookings.getCheckInInfo().getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                    if (bookings.getCheckInInfo().getBillViewStatus().equalsIgnoreCase("Show")) {
                        ivBillIcon.setVisibility(View.VISIBLE);
                    } else {
                        ivBillIcon.setVisibility(View.GONE);
                        hideView(llBillDetails);
                    }

                } else {
                    if (!bookings.getCheckInInfo().getPaymentStatus().equalsIgnoreCase("NotPaid")) {
                        ivBillIcon.setVisibility(View.VISIBLE);
                        if (bookings.getCheckInInfo().getPaymentStatus().equalsIgnoreCase("Refund")) {
                            ivBillIcon.setVisibility(View.GONE);
                            hideView(llBillDetails);
                        }
                    } else {
                        ivBillIcon.setVisibility(View.GONE);
                        hideView(llBillDetails);
                    }
                }
                /**26-3-21*/
                if (bookings.getCheckInInfo().getBillViewStatus() == null || bookings.getCheckInInfo().getBillViewStatus().equalsIgnoreCase("NotShow") || bookings.getCheckInInfo().getBillStatus().equals("Settled") || bookings.getCheckInInfo().getWaitlistStatus().equals("Rejected")) {
                    ivBillIcon.setVisibility(View.GONE);
                    hideView(llBillDetails);
                }
                /***/
                if (bookings.getCheckInInfo() != null && bookings.getCheckInInfo().getParentUuid() != null) {
                    hideView(llBillDetails);
                }


                ivBillIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent iBill = new Intent(context, BillActivity.class);
                        iBill.putExtra("ynwUUID", bookings.getCheckInInfo().getYnwUuid());
                        iBill.putExtra("provider", bookings.getCheckInInfo().getProviderAccount().getBusinessName());
                        iBill.putExtra("accountID", String.valueOf(bookings.getCheckInInfo().getProviderAccount().getId()));
                        iBill.putExtra("payStatus", bookings.getCheckInInfo().getPaymentStatus());
                        iBill.putExtra("purpose", Constants.PURPOSE_BILLPAYMENT);
                        iBill.putExtra("consumer", bookings.getCheckInInfo().getWaitlistingFor().get(0).getFirstName() + " " + bookings.getCheckInInfo().getWaitlistingFor().get(0).getLastName());
                        iBill.putExtra("uniqueId", bookings.getCheckInInfo().getProviderAccount().getUniqueId());
                        iBill.putExtra("encId", bookings.getCheckInInfo().getCheckinEncId());
                        iBill.putExtra("bookingStatus", bookings.getCheckInInfo().getWaitlistStatus());
                        iBill.putExtra("location", bookings.getCheckInInfo().getQueue().getLocation().getPlace());

                        v.getContext().startActivity(iBill);

                    }
                });

                // To Show Prescription
                if (bookings.getCheckInInfo().isPrescShared()) {
                    llPrescription.setVisibility(View.VISIBLE);
                } else {
                    hideView(llPrescription);
                }


                llPrescription.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        prescriptionDialog = new PrescriptionDialog(mContext, isActive, bookings);
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


            llSendAttachments.setVisibility(View.VISIBLE);
            if (bookings.isHasAttachment()) {
                llViewAttachments.setVisibility(View.VISIBLE);
            } else {
                llViewAttachments.setVisibility(View.GONE);
                hideView(llViewAttachments);
            }


        } else {

            hideView(llReschedule);
            hideView(llCancel);
            hideView(llTrackingOn);
            hideView(llInstructions);
            hideView(llCustomerNotes);
            hideView(llMeetingDetails);
            hideView(llQuestionnaire);
            if (bookings != null && bookings.getCheckInInfo() != null) {
                if (bookings.getCheckInInfo().isPrescShared()) {
                    llPrescription.setVisibility(View.VISIBLE);
                    llPrescription.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            prescriptionDialog = new PrescriptionDialog(mContext, isActive, bookings);
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
                } else {
                    hideView(llPrescription);
                }
            } else if (bookings != null && bookings.getAppointmentInfo() != null) {
                if (bookings.getAppointmentInfo().isPrescShared()) {
                    llPrescription.setVisibility(View.VISIBLE);
                    llPrescription.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            prescriptionDialog = new PrescriptionDialog(mContext, isActive, bookings);
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
                } else {
                    hideView(llPrescription);
                }
            } else {
                hideView(llPrescription);
            }
            llSendAttachments.setVisibility(View.VISIBLE);
            //hideView(llSendAttachments);
            if (bookings.isHasAttachment()) {
                llViewAttachments.setVisibility(View.VISIBLE);
            } else {
                llViewAttachments.setVisibility(View.GONE);
                hideView(llViewAttachments);
            }
            //hideView(llViewAttachments);
            llRating.setVisibility(View.VISIBLE);


            // To show Bill details
            if (bookings.getAppointmentInfo() != null) {

                if (bookings.getAppointmentInfo().getPaymentStatus().equalsIgnoreCase("FullyPaid") || bookings.getAppointmentInfo().getPaymentStatus().equalsIgnoreCase("Refund")) {
                    ivBillIcon.setVisibility(View.VISIBLE);
                    tvBillText.setText("Receipt");
                } else {
                    ivBillIcon.setVisibility(View.VISIBLE);
                    tvBillText.setText("Pay Bill");
                }

                if (bookings.getAppointmentInfo().getBillViewStatus() != null && !bookings.getAppointmentInfo().getApptStatus().equalsIgnoreCase("cancelled")) {
                    if (bookings.getAppointmentInfo().getBillViewStatus().equalsIgnoreCase("Show")) {
                        ivBillIcon.setVisibility(View.VISIBLE);
                    } else {
                        ivBillIcon.setVisibility(View.GONE);
                        hideView(llBillDetails);
                    }

                } else {
                    if (!bookings.getAppointmentInfo().getPaymentStatus().equalsIgnoreCase("NotPaid")) {
                        ivBillIcon.setVisibility(View.VISIBLE);
                    } else {
                        ivBillIcon.setVisibility(View.GONE);
                        hideView(llBillDetails);
                    }
                }
                /**26-3-21*/
                if (bookings.getAppointmentInfo().getBillViewStatus() == null || bookings.getAppointmentInfo().getBillViewStatus().equalsIgnoreCase("NotShow") || bookings.getAppointmentInfo().getBillStatus().equals("Settled") || bookings.getAppointmentInfo().getApptStatus().equals("Rejected")) {
                    ivBillIcon.setVisibility(View.GONE);
                    hideView(llBillDetails);
                }
                /***/

                ivBillIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent iBill = new Intent(context, BillActivity.class);
                        iBill.putExtra("ynwUUID", bookings.getAppointmentInfo().getUid());
                        iBill.putExtra("provider", bookings.getAppointmentInfo().getProviderAccount().getBusinessName());
                        iBill.putExtra("accountID", String.valueOf(bookings.getAppointmentInfo().getProviderAccount().getId()));
                        iBill.putExtra("payStatus", bookings.getAppointmentInfo().getPaymentStatus());
                        iBill.putExtra("purpose", Constants.PURPOSE_BILLPAYMENT);
                        iBill.putExtra("consumer", bookings.getAppointmentInfo().getAppmtFor().get(0).getFirstName() + " " + bookings.getAppointmentInfo().getAppmtFor().get(0).getLastName());
                        iBill.putExtra("uniqueId", bookings.getAppointmentInfo().getProviderAccount().getUniqueId());
                        iBill.putExtra("encId", bookings.getAppointmentInfo().getAppointmentEncId());
                        iBill.putExtra("bookingStatus", bookings.getAppointmentInfo().getApptStatus());
                        iBill.putExtra("location", bookings.getAppointmentInfo().getLocation().getPlace());

                        v.getContext().startActivity(iBill);
                    }
                });
            } else if (bookings.getCheckInInfo() != null) {
                if (bookings.getCheckInInfo().getPaymentStatus().equalsIgnoreCase("FullyPaid") || bookings.getCheckInInfo().getPaymentStatus().equalsIgnoreCase("Refund")) {
                    ivBillIcon.setVisibility(View.VISIBLE);
                    tvBillText.setText("Receipt");
                } else {
                    ivBillIcon.setVisibility(View.VISIBLE);
                    tvBillText.setText("Pay Bill");
                }

                if (bookings.getCheckInInfo().getBillViewStatus() != null && !bookings.getCheckInInfo().getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                    if (bookings.getCheckInInfo().getBillViewStatus().equalsIgnoreCase("Show")) {
                        ivBillIcon.setVisibility(View.VISIBLE);
                    } else {
                        ivBillIcon.setVisibility(View.GONE);
                        hideView(llBillDetails);
                    }

                } else {
                    if (!bookings.getCheckInInfo().getPaymentStatus().equalsIgnoreCase("NotPaid")) {
                        ivBillIcon.setVisibility(View.VISIBLE);
                    } else {
                        ivBillIcon.setVisibility(View.GONE);
                        hideView(llBillDetails);
                    }
                }
                /**26-3-21*/
                if (bookings.getCheckInInfo().getBillViewStatus() == null || bookings.getCheckInInfo().getBillViewStatus().equalsIgnoreCase("NotShow") || bookings.getCheckInInfo().getBillStatus().equals("Settled") || bookings.getCheckInInfo().getWaitlistStatus().equals("Rejected")) {
                    ivBillIcon.setVisibility(View.GONE);
                    hideView(llBillDetails);
                }
                /***/


                ivBillIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent iBill = new Intent(context, BillActivity.class);
                        iBill.putExtra("ynwUUID", bookings.getCheckInInfo().getYnwUuid());
                        iBill.putExtra("provider", bookings.getCheckInInfo().getProviderAccount().getBusinessName());
                        iBill.putExtra("accountID", String.valueOf(bookings.getCheckInInfo().getProviderAccount().getId()));
                        iBill.putExtra("payStatus", bookings.getCheckInInfo().getPaymentStatus());
                        iBill.putExtra("purpose", Constants.PURPOSE_BILLPAYMENT);
                        iBill.putExtra("consumer", bookings.getCheckInInfo().getWaitlistingFor().get(0).getFirstName() + " " + bookings.getCheckInInfo().getWaitlistingFor().get(0).getLastName());
                        iBill.putExtra("uniqueId", bookings.getCheckInInfo().getProviderAccount().getUniqueId());
                        iBill.putExtra("encId", bookings.getCheckInInfo().getCheckinEncId());
                        iBill.putExtra("bookingStatus", bookings.getCheckInInfo().getWaitlistStatus());
                        iBill.putExtra("location", bookings.getCheckInInfo().getQueue().getLocation().getPlace());

                        v.getContext().startActivity(iBill);

                    }
                });
            }

        }
        llSendAttachments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iSendData.sendAttachments(bookings);
                dismiss();

            }
        });
        llViewAttachments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iSendData.viewAttachments(bookings);
                dismiss();

            }
        });
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
                        intent.putExtra("from", Constants.APPOINTMENT);
                    } else if (bookings.getCheckInInfo() != null) {
                        intent.putExtra("uuid", bookings.getCheckInInfo().getYnwUuid());
                        intent.putExtra("accountId", bookings.getCheckInInfo().getProviderAccount().getId());
                        intent.putExtra("name", bookings.getCheckInInfo().getProviderAccount().getBusinessName());
                        intent.putExtra("from", Constants.CHECKIN);
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
                        intent.putExtra("uniqueId", bookings.getAppointmentInfo().getProviderAccount().getUniqueId());
                        intent.putExtra("ynwuuid", bookings.getAppointmentInfo().getUid());
                        intent.putExtra("providerId", bookings.getAppointmentInfo().getProviderAccount().getId());
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
                Typeface tyface1 = Typeface.createFromAsset(context.getAssets(),
                        "fonts/JosefinSans-SemiBold.ttf");
                btSend.setTypeface(tyface1);
                btCancel.setTypeface(tyface1);
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

                    ApiRatingApp(String.valueOf(bookings.getAppointmentInfo().getProviderAccount().getId()), bookings.getAppointmentInfo().getUid());

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

                    if (bookings.getAppointmentInfo() != null) {

                        apiGetMeetingDetails(bookings.getAppointmentInfo().getUid(), bookings.getAppointmentInfo().getService().getVirtualCallingModes().get(0).getCallingMode(), bookings.getAppointmentInfo().getProviderAccount().getId(), bookings.getAppointmentInfo());

                    } else if (bookings.getCheckInInfo() != null) {

                        apiGetMeetingDetails(bookings.getCheckInInfo().getYnwUuid(), bookings.getCheckInInfo().getService().getVirtualCallingModes().get(0).getCallingMode(), bookings.getCheckInInfo().getProviderAccount().getId(), bookings.getCheckInInfo());

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        llQuestionnaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bookings.getAppointmentInfo() != null) {

                    QuestionnaireResponseInput input = buildQuestionnaireInput(bookings.getAppointmentInfo().getQuestionnaire());
                    ArrayList<LabelPath> labelPaths = buildQuestionnaireLabelPaths(bookings.getAppointmentInfo().getQuestionnaire());

                    SharedPreference.getInstance(mContext).setValue(Constants.QUESTIONNAIRE, new Gson().toJson(input));
                    SharedPreference.getInstance(mContext).setValue(Constants.QIMAGES, new Gson().toJson(labelPaths));

                    Intent intent = new Intent(mContext, UpdateQuestionnaire.class);
                    intent.putExtra("serviceId", bookings.getAppointmentInfo().getService().getId());
                    intent.putExtra("accountId", bookings.getAppointmentInfo().getProviderAccount().getId());
                    intent.putExtra("uid", bookings.getAppointmentInfo().getUid());
                    intent.putExtra("isEdit", true);
                    intent.putExtra("from", Constants.BOOKING_APPOINTMENT);
                    if (bookings.getAppointmentInfo() != null && bookings.getAppointmentInfo().getApptStatus() != null) {
                        intent.putExtra("status", bookings.getAppointmentInfo().getApptStatus());
                    }
                    mContext.startActivity(intent);

                } else if (bookings.getCheckInInfo() != null) {

                    QuestionnaireResponseInput input = buildQuestionnaireInput(bookings.getCheckInInfo().getQuestionnaire());
                    ArrayList<LabelPath> labelPaths = buildQuestionnaireLabelPaths(bookings.getCheckInInfo().getQuestionnaire());

                    SharedPreference.getInstance(mContext).setValue(Constants.QUESTIONNAIRE, new Gson().toJson(input));
                    SharedPreference.getInstance(mContext).setValue(Constants.QIMAGES, new Gson().toJson(labelPaths));

                    Intent intent = new Intent(mContext, UpdateQuestionnaire.class);
                    intent.putExtra("serviceId", bookings.getCheckInInfo().getService().getId());
                    intent.putExtra("accountId", bookings.getCheckInInfo().getProviderAccount().getId());
                    intent.putExtra("uid", bookings.getCheckInInfo().getYnwUuid());
                    intent.putExtra("isEdit", true);
                    intent.putExtra("from", Constants.BOOKING_CHECKIN);
                    if (bookings.getCheckInInfo() != null && bookings.getCheckInInfo().getWaitlistStatus() != null) {
                        intent.putExtra("status", bookings.getCheckInInfo().getWaitlistStatus());
                    }
                    mContext.startActivity(intent);
                }

                dismiss();

            }
        });

    }

    private QuestionnaireResponseInput buildQuestionnaireInput(QuestionnaireResponse questionnaire) {

        QuestionnaireResponseInput responseInput = new QuestionnaireResponseInput();
        responseInput.setQuestionnaireId(questionnaire.getQuestionnaireId());
        ArrayList<AnswerLineResponse> answerLineResponse = new ArrayList<>();
        ArrayList<GetQuestion> questions = new ArrayList<>();

        for (QuestionAnswers qAnswers : questionnaire.getQuestionAnswers()) {

            answerLineResponse.add(qAnswers.getAnswerLine());
            questions.add(qAnswers.getGetQuestion());

        }

        responseInput.setAnswerLines(answerLineResponse);
        responseInput.setQuestions(questions);

        return responseInput;

    }

    private ArrayList<LabelPath> buildQuestionnaireLabelPaths(QuestionnaireResponse questionnaire) {

        ArrayList<LabelPath> labelPaths = new ArrayList<>();

        for (QuestionAnswers qAnswers : questionnaire.getQuestionAnswers()) {

            if (qAnswers.getGetQuestion().getFieldDataType().equalsIgnoreCase("fileUpload")) {

                JsonArray jsonArray = new JsonArray();
                jsonArray = qAnswers.getAnswerLine().getAnswer().get("fileUpload").getAsJsonArray();
                for (int i = 0; i < jsonArray.size(); i++) {

                    LabelPath path = new LabelPath();
                    path.setId(labelPaths.size());
                    path.setFileName(jsonArray.get(i).getAsJsonObject().get("caption").getAsString());
                    path.setLabelName(qAnswers.getAnswerLine().getLabelName());
                    path.setPath(jsonArray.get(i).getAsJsonObject().get("s3path").getAsString());
                    path.setType(jsonArray.get(i).getAsJsonObject().get("type").getAsString());

                    labelPaths.add(path);
                }

            }

        }

        return labelPaths;

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
        llBillDetails = findViewById(R.id.ll_billDetails);
        ivBillIcon = findViewById(R.id.iv_billIcon);
        tvBillText = findViewById(R.id.billLabel);
        llPrescription = findViewById(R.id.ll_prescriptionDetails);
        llSendAttachments = findViewById(R.id.ll_sendAttachments);
        llViewAttachments = findViewById(R.id.ll_viewAttachments);
        llQuestionnaire = findViewById(R.id.ll_questionnaire);
        tvCustomerNotes = findViewById(R.id.tv_customerNotes);

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
    public void showMeetingDetailsWindow(ActiveAppointment activeAppointment, String mode, TeleServiceCheckIn meetingDetails) {

        meetingDetailsWindow = new MeetingDetailsWindow(mContext, activeAppointment.getApptTime(), activeAppointment.getService().getName(), meetingDetails, activeAppointment.getService().getVirtualCallingModes().get(0).getCallingMode(), activeAppointment.getService().getVirtualCallingModes().get(0).getVirtualServiceType());
        meetingDetailsWindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
        meetingDetailsWindow.show();
        meetingDetailsWindow.setCancelable(false);
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 1);
        meetingDetailsWindow.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    // for Whatsspp and Phone
    public void showMeetingWindow(ActiveAppointment activeAppointment, String mode, TeleServiceCheckIn meetingDetails) {

        if (mode.equalsIgnoreCase("WhatsApp")) {
            meetingInfo = new MeetingInfo(mContext, activeAppointment.getApptTime(), activeAppointment.getService().getName(), meetingDetails, activeAppointment.getService().getVirtualCallingModes().get(0).getCallingMode(), activeAppointment.getVirtualService().getWhatsApp(), activeAppointment.getService().getVirtualCallingModes().get(0).getVirtualServiceType(), activeAppointment.getCountryCode(), Constants.APPOINTMENT);
        } else {
            meetingInfo = new MeetingInfo(mContext, activeAppointment.getApptTime(), activeAppointment.getService().getName(), meetingDetails, activeAppointment.getService().getVirtualCallingModes().get(0).getCallingMode(), activeAppointment.getVirtualService().getPhone(), "", activeAppointment.getCountryCode(), Constants.APPOINTMENT);
        }
        meetingInfo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        meetingInfo.show();
        meetingInfo.setCancelable(false);
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

                            } else if (mode.equalsIgnoreCase("VideoCall")) {

                                showCheckInMeetingDetailsWindow(info, mode, meetingDetails);

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

        meetingDetailsWindow = new MeetingDetailsWindow(mContext, activeCheckIn.getCheckInTime(), activeCheckIn.getService().getName(), meetingDetails, activeCheckIn.getService().getVirtualCallingModes().get(0).getCallingMode(), activeCheckIn.getService().getVirtualCallingModes().get(0).getVirtualServiceType());
        meetingDetailsWindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
        meetingDetailsWindow.show();
        meetingDetailsWindow.setCancelable(false);
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 1);
        meetingDetailsWindow.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    // for Whatsspp and Phone
    public void showCheckInMeetingWindow(ActiveCheckIn activeCheckIn, String mode, TeleServiceCheckIn meetingDetails) {

        if (mode.equalsIgnoreCase("WhatsApp")) {
            meetingInfo = new MeetingInfo(mContext, activeCheckIn.getCheckInTime(), activeCheckIn.getService().getName(), meetingDetails, activeCheckIn.getService().getVirtualCallingModes().get(0).getCallingMode(), activeCheckIn.getVirtualService().getWhatsApp(), activeCheckIn.getService().getVirtualCallingModes().get(0).getVirtualServiceType(), activeCheckIn.getCountryCode(), Constants.CHECKIN);
        } else {
            meetingInfo = new MeetingInfo(mContext, activeCheckIn.getCheckInTime(), activeCheckIn.getService().getName(), meetingDetails, activeCheckIn.getService().getVirtualCallingModes().get(0).getCallingMode(), activeCheckIn.getVirtualService().getPhone(), "", activeCheckIn.getCountryCode(), Constants.CHECKIN);
        }
        meetingInfo.setCancelable(false);
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
                            iSendData.cancel();

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
                            iSendData.cancel();

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


    BottomSheetDialog ratingDialog;
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
                        Config.closeDialog(getOwnerActivity(), mDialog);
                    Config.logV("URL----------Location-----###########@@@@@@-----" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code--------Message-----------------" + response.code());
                    if (response.code() == 200) {
                        final ArrayList<RatingResponse> mRatingDATA = response.body();
                        Config.logV("Response--code--------BottomSheetDialog-----------------" + response.code());
                        ratingDialog = new BottomSheetDialog(mContext);
                        ratingDialog.setContentView(R.layout.rating);
                        ratingDialog.setCancelable(true);
                        ratingDialog.show();
                        TextView tv_title = (TextView) ratingDialog.findViewById(R.id.txtratevisit);
                        final EditText edt_message = (EditText) ratingDialog.findViewById(R.id.edt_message);
                        final RatingBar rating = (RatingBar) ratingDialog.findViewById(R.id.rRatingBar);
                        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                                "fonts/Montserrat_Bold.otf");
                        tv_title.setTypeface(tyface);
                        final Button btn_close = (Button) ratingDialog.findViewById(R.id.btn_cancel);
                        final Button btn_rate = (Button) ratingDialog.findViewById(R.id.btn_send);
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
                                ApiPUTCheckInRating(Math.round(rate), UUID, comment, accountID, ratingDialog, firstTimeRating);

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
                                ratingDialog.dismiss();
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


    private void ApiRatingApp(final String accountID, final String UUID) {
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
                        ratingDialog = new BottomSheetDialog(mContext);
                        ratingDialog.setContentView(R.layout.rating);
                        ratingDialog.setCancelable(true);
                        ratingDialog.show();
                        TextView tv_title = (TextView) ratingDialog.findViewById(R.id.txtratevisit);
                        final EditText edt_message = (EditText) ratingDialog.findViewById(R.id.edt_message);
                        final RatingBar rating = (RatingBar) ratingDialog.findViewById(R.id.rRatingBar);
                        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                                "fonts/Montserrat_Bold.otf");
                        tv_title.setTypeface(tyface);
                        final Button btn_close = (Button) ratingDialog.findViewById(R.id.btn_cancel);
                        final Button btn_rate = (Button) ratingDialog.findViewById(R.id.btn_send);
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
                                ApiPUTRating(Math.round(rate), UUID, comment, accountID, ratingDialog, firstTimeRating);

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
                                ratingDialog.dismiss();
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
