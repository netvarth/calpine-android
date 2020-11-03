package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

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
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.InboxAdapter;
import com.jaldeeinc.jaldee.adapter.JaldeeTabs;
import com.jaldeeinc.jaldee.adapter.MoreInfoTabs;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.ChatHistory;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.CustomerNotes;
import com.jaldeeinc.jaldee.custom.EnquiryDialog;
import com.jaldeeinc.jaldee.custom.InstructionsDialog;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.Bookings;
import com.jaldeeinc.jaldee.model.ProviderUserModel;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.InboxModel;
import com.jaldeeinc.jaldee.response.RatingResponse;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jaldeeinc.jaldee.connection.ApiClient.context;

public class BookingDetails extends AppCompatActivity {

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

    @BindView(R.id.tv_batchNo)
    CustomTextViewBold tvBatchNo;

    @BindView(R.id.tv_viewMore)
    CustomTextViewSemiBold tvViewMore;

    @BindView(R.id.cv_ok)
    CardView cvOk;

    @BindView(R.id.cv_back)
    CardView cvBack;

    @BindView(R.id.cv_share)
    CardView cvShare;

    @BindView(R.id.cv_enquiry)
    CardView cvEnquiry;

    @BindView(R.id.iv_fav)
    ImageView ivfav;

    @BindView(R.id.ll_payment)
    LinearLayout llPayment;

    @BindView(R.id.ll_cancel)
    LinearLayout llCancel;

    @BindView(R.id.ll_message)
    LinearLayout llMessage;

    @BindView(R.id.ll_reschedule)
    LinearLayout llReschedule;

    @BindView(R.id.ll_batch)
    LinearLayout llBatch;

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

    boolean firstTimeRating = false;
    TabLayout tabLayout;
    ViewPager viewPager;
    private Context mContext;
    private Bookings bookingInfo = new Bookings();
    private boolean isActive = true;
    private ActiveAppointment apptInfo = new ActiveAppointment();
    private InstructionsDialog instructionsDialog;
    private CustomerNotes customerNotes;
    private ChatHistory chatHistory;
    ArrayList<InboxModel> mInboxList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);
        ButterKnife.bind(BookingDetails.this);
        mContext = BookingDetails.this;

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

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

                Intent intent = new Intent(BookingDetails.this, RescheduleActivity.class);
                intent.putExtra("appointmentInfo", apptInfo);
                startActivity(intent);
            }
        });

        tvInstructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    if (apptInfo != null && apptInfo.getService() != null) {
                        instructionsDialog = new InstructionsDialog(mContext, apptInfo.getService().getPostInfoText(), apptInfo.getService().getPostInfoTitle());
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
                    Intent intent = new Intent(BookingDetails.this, ChatActivity.class);
                    intent.putExtra("uuid", apptInfo.getUid());
                    intent.putExtra("accountId", apptInfo.getProviderAccount().getId());
                    intent.putExtra("name", apptInfo.getProviderAccount().getBusinessName());
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
                txtsendmsg.setText("Do you want to cancel this Appointment?");
                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ApiDeleteAppointment(apptInfo.getUid(), String.valueOf(apptInfo.getProviderAccount().getId()), dialog);
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

        llLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent intent = new Intent(mContext, CheckinShareLocationAppointment.class);
                    intent.putExtra("waitlistPhonenumber", apptInfo.getConsumer().getUserProfile().getPrimaryMobileNo());
                    intent.putExtra("uuid", apptInfo.getUid());
                    intent.putExtra("accountID", String.valueOf(apptInfo.getProviderAccount().getId()));
                    intent.putExtra("title", apptInfo.getProviderAccount().getBusinessName());
                    intent.putExtra("terminology", "Check-in");
                    intent.putExtra("calcMode", "Check-in");
                    intent.putExtra("queueStartTime", apptInfo.getSchedule().getApptSchedule().getTimeSlots().get(0).getsTime());
                    intent.putExtra("queueEndTime", apptInfo.getSchedule().getApptSchedule().getTimeSlots().get(0).geteTime());
                    if (apptInfo.getJaldeeApptDistanceTime() != null && apptInfo.getJaldeeApptDistanceTime().getJaldeeDistanceTime() != null) {
                        intent.putExtra("jaldeeDistance", apptInfo.getJaldeeApptDistanceTime().getJaldeeDistanceTime().getJaldeeDistance().getDistance());
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

                ApiRating(String.valueOf(apptInfo.getProviderAccount().getId()), apptInfo.getUid());

            }
        });

        tvCustomerNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    if (apptInfo != null && apptInfo.getService() != null) {
                        customerNotes = new CustomerNotes(mContext, apptInfo.getService().getConsumerNoteTitle(), apptInfo.getConsumerNote());
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
        if (bookingInfo != null && bookingInfo.getAppointmentInfo() != null) {
            getAppointmentDetails(bookingInfo.getAppointmentInfo().getUid(), bookingInfo.getAppointmentInfo().getProviderAccount().getId());
        }
        super.onResume();
    }

//    private void ApiInboxList() {
//
//        Config.logV("API Call");
//        final ApiInterface apiService =
//                ApiClient.getClient(mContext).create(ApiInterface.class);
//
//        Call<ArrayList<InboxModel>> call = apiService.getMessage();
//        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
//        mDialog.show();
//        final HashMap<String, List<InboxModel>> messagesMap = new HashMap<String, List<InboxModel>>();
//
//
//        call.enqueue(new Callback<ArrayList<InboxModel>>() {
//            @Override
//            public void onResponse(Call<ArrayList<InboxModel>> call, Response<ArrayList<InboxModel>> response) {
//
//                try {
//
//                    if (mDialog.isShowing())
//                        Config.closeDialog(BookingDetails.this, mDialog);
//                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
//                    Config.logV("Response--code-------------------------" + response.code());
//
//                    if (response.code() == 200) {
//                        mInboxList.clear();
//                        mInboxList = response.body();
//                        if (mInboxList.size() > 0) {
//
//                            for (int i = 0; i < mInboxList.size(); i++) {
//
//                                int activeConsumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);
//
//
//                                String senderName = String.valueOf(mInboxList.get(i).getAccountName()).toLowerCase().trim();
//
//                                int senderId = mInboxList.get(i).getOwner().getId();
//                                String messageStatus = "in";
//
//                                if (senderId == activeConsumerId) {
//
//                                    senderId = mInboxList.get(i).getReceiver().getReceiverId();
//                                    senderName = String.valueOf(mInboxList.get(i).getReceiver().getReceiverName()).toLowerCase().trim();
//                                    //  Config.logV("SenderID--1111----------" + senderId);
//                                    messageStatus = "out";
//                                }
//
//                                String senderKey = senderId + "_" + senderName;
//                                Config.logV("SenderKEy----------"+senderKey);
//                                InboxModel inbox = new InboxModel();
//                                inbox.setTimeStamp(mInboxList.get(i).getTimeStamp());
//                                inbox.setUserName(senderName);
//                                inbox.setService(mInboxList.get(i).getService());
//                                inbox.setMsg(mInboxList.get(i).getMsg());
//                                inbox.setId(mInboxList.get(i).getOwner().getId());
//                                inbox.setWaitlistId(mInboxList.get(i).getWaitlistId());
//                                inbox.setMessageStatus(messageStatus);
//                                inbox.setAttachments(mInboxList.get(i).getAttachments());
//                                //Config.logV("AccountID--------------"+mInboxList.get(i).getAccountId());
//                                inbox.setUniqueID(mInboxList.get(i).getAccountId());
//                                inbox.setAccountName(mInboxList.get(i).getAccountName());
//                                // mDBInboxList.add(inbox);
//
//                                db.insertInboxModel(inbox);
//                            }
//                            mDBSORTInboxList = db.getAllInboxDetail();
//                            Collections.sort(mDBSORTInboxList, new Comparator<InboxModel>() {
//                                @Override
//                                public int compare(InboxModel r1, InboxModel r2) {
//                                    return new Long(r2.getTimeStamp()).compareTo(new Long(r1.getTimeStamp()));
//                                }
//                            });
//
//                            Config.logV("INBOX LIST------NEW----------" + mDBSORTInboxList.size());
//                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
//                            mrRecylce_inboxlist.setLayoutManager(mLayoutManager);
//                            mInboxAdapter = new InboxAdapter(mDBSORTInboxList, mContext, mActivity, mDBInboxList);
//                            mrRecylce_inboxlist.setAdapter(mInboxAdapter);
//                            mInboxAdapter.notifyDataSetChanged();
//                        } else {
//
//                            tv_noinbox.setVisibility(View.VISIBLE);
//                            mrRecylce_inboxlist.setVisibility(View.GONE);
//
//                        }
//
//
//                    }else{
//
//                    }
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<ArrayList<InboxModel>> call, Throwable t) {
//                // Log error here since request failed
//                Config.logV("Fail---------------" + t.toString());
//
//                if (mDialog.isShowing())
//                    Config.closeDialog(getActivity(), mDialog);
//            }
//        });
//
//
//    }


    public void getAppointmentDetails(String uid, int id) {
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<ActiveAppointment> call = apiService.getActiveAppointmentUUID(uid, String.valueOf(id));
        call.enqueue(new Callback<ActiveAppointment>() {
            @Override
            public void onResponse(Call<ActiveAppointment> call, Response<ActiveAppointment> response) {
                try {
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        apptInfo = response.body();
                        updateUI(apptInfo);
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ActiveAppointment> call, Throwable t) {
            }
        });
    }


    private void updateUI(ActiveAppointment appointmentInfo) {

        try {

            if (appointmentInfo != null) {
                if (appointmentInfo.getProvider() != null) {

                    if (appointmentInfo.getProvider().getBusinessName() != null && !appointmentInfo.getProvider().getBusinessName().equalsIgnoreCase("")) {
                        tvDoctorName.setText(convertToTitleForm(appointmentInfo.getProvider().getBusinessName()));
                    } else {
                        String name = appointmentInfo.getProvider().getFirstName() + " " + appointmentInfo.getProvider().getLastName();
                        tvDoctorName.setText(convertToTitleForm(name));
                    }
                    tvProviderName.setVisibility(View.VISIBLE);
                    tvProviderName.setText(convertToTitleForm(appointmentInfo.getProviderAccount().getBusinessName()));
                } else {
                    tvProviderName.setVisibility(View.INVISIBLE);
                    tvDoctorName.setText(convertToTitleForm(appointmentInfo.getProviderAccount().getBusinessName()));

                }

                if (appointmentInfo.getService() != null) {
                    tvServiceName.setText(convertToTitleForm(appointmentInfo.getService().getName()));


                    if (appointmentInfo.getService().getServiceType() != null && appointmentInfo.getService().getServiceType().equalsIgnoreCase("virtualService")) {

                        if (appointmentInfo.getService().getVirtualCallingModes() != null) {
                            ivTeleService.setVisibility(View.VISIBLE);
                            if (appointmentInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                ivTeleService.setImageResource(R.drawable.zoom_meet);

                            } else if (appointmentInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                ivTeleService.setImageResource(R.drawable.google_meet);

                            } else if (appointmentInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                if (appointmentInfo.getService().getVirtualServiceType() != null && appointmentInfo.getService().getVirtualServiceType().equalsIgnoreCase("videoService")) {
                                    ivTeleService.setImageResource(R.drawable.whatsapp_videoicon);
                                } else {
                                    ivTeleService.setImageResource(R.drawable.whatsapp_icon);
                                }
                            } else if (appointmentInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                ivTeleService.setImageResource(R.drawable.phone_icon);
                            }
                        } else {
                            ivTeleService.setVisibility(View.GONE);
                        }
                    }

                }

                // to set confirmation number
                if (appointmentInfo.getAppointmentEncId() != null) {
                    tvConfirmationNumber.setText(appointmentInfo.getAppointmentEncId());
                }

                // to set status
                if (appointmentInfo.getApptStatus() != null) {
                    tvStatus.setVisibility(View.VISIBLE);
                    if (appointmentInfo.getApptStatus().equalsIgnoreCase("Cancelled")) {
                        tvStatus.setTextColor(mContext.getResources().getColor(R.color.red));
                        tvStatus.setText(convertToTitleForm(appointmentInfo.getApptStatus()));

                    } else {
                        tvStatus.setTextColor(mContext.getResources().getColor(R.color.location_theme));
                        tvStatus.setText(convertToTitleForm(appointmentInfo.getApptStatus()));
                    }
                } else {
                    tvStatus.setVisibility(View.GONE);
                }


                // to set paid info
                if (appointmentInfo.getAmountPaid() != null && !appointmentInfo.getAmountPaid().equalsIgnoreCase("0.0")) {
                    llPayment.setVisibility(View.VISIBLE);
                    tvAmount.setText("₹" + " " + convertAmountToDecimals(appointmentInfo.getAmountPaid()) + " " + "PAID");
                } else {

                    llPayment.setVisibility(View.GONE);
                }

                // to set consumer name
                if (appointmentInfo.getAppmtFor() != null) {

                    if (appointmentInfo.getAppmtFor().get(0).getUserName() != null) {
                        tvConsumerName.setText(appointmentInfo.getAppmtFor().get(0).getUserName());
                    } else {
                        tvConsumerName.setText(appointmentInfo.getAppmtFor().get(0).getFirstName() + " " + appointmentInfo.getAppmtFor().get(0).getLastName());
                    }
                }

                // to set appointment date
                if (appointmentInfo.getAppmtDate() != null) {
                    tvDate.setText(getCustomDateString(appointmentInfo.getAppmtDate()));
                }

                // to set slot time
                if (appointmentInfo.getAppmtTime() != null) {

                    tvTime.setText(convertTime(appointmentInfo.getAppmtTime().split("-")[0]));
                }

                if (appointmentInfo.getBatchId() != null) {
                    llBatch.setVisibility(View.VISIBLE);
                    tvBatchNo.setText(appointmentInfo.getBatchId());
                } else {
                    llBatch.setVisibility(View.GONE);
                }

                // to set location
                if (appointmentInfo.getLocation() != null) {

                    if (appointmentInfo.getLocation().getPlace() != null) {

                        tvLocationName.setText(appointmentInfo.getLocation().getPlace());

                        tvLocationName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                openMapView(appointmentInfo.getLocation().getLattitude(), appointmentInfo.getLocation().getLongitude(), appointmentInfo.getLocation().getPlace());
                            }
                        });
                    }
                }

                if (isActive) {
                    llReschedule.setVisibility(View.VISIBLE);
                    llCancel.setVisibility(View.VISIBLE);

                    if (appointmentInfo.getService() != null) {

                        if (appointmentInfo.getService().getLivetrack().equalsIgnoreCase("true")) {
                            llLocation.setVisibility(View.VISIBLE);
                            if (appointmentInfo.getJaldeeApptDistanceTime() != null) {
                                Glide.with(BookingDetails.this).load(R.drawable.address).into(ivLtIcon);
                            } else {
                                ivLtIcon.setImageResource(R.drawable.location_off);
//                                ivLtIcon.setColorFilter(ContextCompat.getColor(context, R.color.light_gray));

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
                if (appointmentInfo.getService() != null && appointmentInfo.getService().isPostInfoEnabled()) {
                    tvInstructions.setVisibility(View.VISIBLE);
                } else {
                    tvInstructions.setVisibility(View.GONE);
                }

                // hide customerNotes when there is no notes from consumer
                if (appointmentInfo.getConsumerNote() != null && !appointmentInfo.getConsumerNote().equalsIgnoreCase("")) {
                    tvCustomerNotes.setVisibility(View.VISIBLE);
                } else {
                    tvCustomerNotes.setVisibility(View.GONE);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                        Config.closeDialog(BookingDetails.this, mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        if (response.body().string().equalsIgnoreCase("true")) {
                            DynamicToast.make(context, "Appointment cancelled successfully", AppCompatResources.getDrawable(
                                    context, R.drawable.ic_info_black),
                                    ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.green), Toast.LENGTH_SHORT).show();

                            dialog.dismiss();
                            isActive = false;
                            getAppointmentDetails(apptInfo.getUid(), apptInfo.getProviderAccount().getId());

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
                    Config.closeDialog(BookingDetails.this, mDialog);
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
                        Config.closeDialog(BookingDetails.this, mDialog);
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
                    Config.closeDialog(BookingDetails.this, mDialog);
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
//        Config.logV("Request--BODY-------------------------" + new Gson().toJson(jsonObj.toString()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Config.logV("URL-------Request---" + response.raw().request().url().toString().trim());
                    if (mDialog.isShowing())
                        Config.closeDialog(BookingDetails.this, mDialog);
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
                    Config.closeDialog(BookingDetails.this, mDialog);
            }
        });
    }


    public static String convertToTitleForm(String name) {
        String convertName = name;
        convertName = convertName.substring(0, 1).toUpperCase() + convertName.substring(1).toLowerCase();
        return convertName;
    }

    public static String convertAmountToDecimals(String price) {

        double a = Double.parseDouble(price);
        DecimalFormat decim = new DecimalFormat("0.00");
        Double price2 = Double.parseDouble(decim.format(a));
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