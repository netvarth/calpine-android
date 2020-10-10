package com.jaldeeinc.jaldee.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jaldeeinc.jaldee.Interface.IMailSubmit;
import com.jaldeeinc.jaldee.Interface.IMobileSubmit;
import com.jaldeeinc.jaldee.Interface.IPaymentResponse;
import com.jaldeeinc.jaldee.Interface.ISendMessage;
import com.jaldeeinc.jaldee.Interface.ISlotInfo;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.CouponlistAdapter;
import com.jaldeeinc.jaldee.adapter.DetailFileImageAdapter;
import com.jaldeeinc.jaldee.adapter.MultipleFamilyMemberAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.AddNotes;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.EmailEditWindow;
import com.jaldeeinc.jaldee.custom.EnquiryDialog;
import com.jaldeeinc.jaldee.custom.MobileNumberDialog;
import com.jaldeeinc.jaldee.custom.SlotsDialog;
import com.jaldeeinc.jaldee.model.FamilyArrayModel;
import com.jaldeeinc.jaldee.model.RazorpayModel;
import com.jaldeeinc.jaldee.payment.PaymentGateway;
import com.jaldeeinc.jaldee.payment.PaytmPayment;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.AvailableSlotsData;
import com.jaldeeinc.jaldee.response.CoupnResponse;
import com.jaldeeinc.jaldee.response.PaymentModel;
import com.jaldeeinc.jaldee.response.ProfileModel;
import com.jaldeeinc.jaldee.response.SearchSetting;
import com.jaldeeinc.jaldee.response.SearchTerminology;
import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.jaldeeinc.jaldee.response.SectorCheckin;
import com.jaldeeinc.jaldee.response.ServiceInfo;
import com.jaldeeinc.jaldee.response.SlotsData;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentActivity extends AppCompatActivity implements PaymentResultWithDataListener, ISlotInfo, IMailSubmit, IPaymentResponse, IMobileSubmit, ISendMessage {

    @BindView(R.id.tv_providerName)
    CustomTextViewSemiBold tvProviderName;

    @BindView(R.id.tv_serviceName)
    CustomTextViewBold tvServiceName;

    @BindView(R.id.tv_description)
    CustomTextViewMedium tvDescription;

    @BindView(R.id.tv_date)
    CustomTextViewBold tvDate;

    @BindView(R.id.tv_time)
    CustomTextViewBold tvTime;

    @BindView(R.id.tv_changeTime)
    CustomTextViewBold tvChangeTime;

    @BindView(R.id.ll_editDetails)
    LinearLayout llEditDetails;

    static CustomTextViewBold tvConsumerName;

    @BindView(R.id.tv_number)
    CustomTextViewMedium tvNumber;

    @BindView(R.id.tv_email)
    CustomTextViewMedium tvEmail;

    @BindView(R.id.cv_addNote)
    CardView cvAddNote;

    @BindView(R.id.cv_attachFile)
    CardView cvAttachFile;

    @BindView(R.id.cv_submit)
    CardView cvSubmit;

    @BindView(R.id.ll_appoint)
    LinearLayout llAppointment;

    @BindView(R.id.ll_checkIn)
    LinearLayout llCheckIn;

    @BindView(R.id.tv_checkInDate)
    CustomTextViewBold tvCheckInDate;

    @BindView(R.id.tv_peopleInLine)
    CustomTextViewBold tvPeopleInLine;

    @BindView(R.id.tv_hint)
    CustomTextViewSemiBold tvHint;

    @BindView(R.id.tv_selectedDateHint)
    CustomTextViewSemiBold tvSelectedDateHint;

    @BindView(R.id.ll_virtualNumber)
    LinearLayout llVirtualNumber;

    @BindView(R.id.et_virtualNumber)
    EditText etVirtualNumber;

    @BindView(R.id.tv_applyCode)
    CustomTextViewSemiBold tvApplyCode;

    @BindView(R.id.rl_coupon)
    RelativeLayout rlCoupon;

    @BindView(R.id.et_code)
    EditText etCode;

    @BindView(R.id.tv_apply)
    CustomTextViewBold tvApply;

    @BindView(R.id.txtprepay)
    CustomTextViewMedium txtprepay;

    @BindView(R.id.txtprepayamount)
    CustomTextViewMedium txtprepayamount;

    @BindView(R.id.LservicePrepay)
    LinearLayout LservicePrepay;

    @BindView(R.id.cv_back)
    CardView cvBack;

    @BindView(R.id.iv_teleService)
    ImageView ivteleService;

    @BindView(R.id.ll_preinfo)
    LinearLayout llPreInfo;

    @BindView(R.id.tv_preInfoTitle)
    CustomTextViewSemiBold tvPreInfoTitle;

    @BindView(R.id.tv_preInfo)
    CustomTextViewMedium tvPreInfo;

    CustomTextViewSemiBold tvErrorMessage;
    String mFirstName, mLastName;
    int consumerID;
    private int uniqueId;
    private String providerName;
    private int providerId;
    private int locationId;
    private int serviceId;
    private String serviceName;
    private String phoneNumber;
    private String serviceDescription;
    private ServiceInfo serviceInfo = new ServiceInfo();
    SearchTerminology mSearchTerminology;
    ProfileModel profileDetails;
    ArrayList<SlotsData> slotsData = new ArrayList<SlotsData>();
    ArrayList<AvailableSlotsData> activeSlotsList = new ArrayList<>();
    private SlotsDialog slotsDialog;
    private ISlotInfo iSlotInfo;
    String uuid;
    String value = null;
    String couponEntered;
    private int scheduleId;
    private EmailEditWindow emailEditWindow;
    private MobileNumberDialog mobileNumberDialog;
    private IMailSubmit iMailSubmit;
    private IMobileSubmit iMobileSubmit;
    ArrayList<String> couponArraylist = new ArrayList<>();
    ArrayList<CoupnResponse> s3couponList = new ArrayList<>();
    RecyclerView list;
    private CouponlistAdapter mAdapter;
    static int familyMEmID;
    static RecyclerView recycle_family;
    String slotTime;
    BottomSheetDialog dialog;
    private IPaymentResponse paymentResponse;
    String calcMode;
    ActiveAppointment activeAppointment = new ActiveAppointment();
    static Activity mActivity;
    static Context mContext;
    String apiDate = "";
    private int userId;
    String sector, subsector;
    int maxPartysize;
    SearchViewDetail mBusinessDataList;
    ArrayList<PaymentModel> mPaymentData = new ArrayList<>();

    //files related
    private static final String IMAGE_DIRECTORY = "/Jaldee" +
            "";
    private int GALLERY = 1, CAMERA = 2;
    RecyclerView recycle_image_attachment;

    String[] imgExtsSupported = new String[]{"jpg", "jpeg", "png"};
    String[] fileExtsSupported = new String[]{"jpg", "jpeg", "png", "pdf"};
    ArrayList<String> imagePathList = new ArrayList<>();
    ArrayList<String> imagePathLists = new ArrayList<>();
    private Uri mImageUri;
    File f;
    String path;
    Bitmap bitmap;
    EditText edt_message;
    String txt_message = "";
    File file;
    TextView tv_attach, tv_camera;
    private ISendMessage iSendMessage;
    private AddNotes addNotes;
    private String userMessage = "";
    String appEncId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        ButterKnife.bind(AppointmentActivity.this);
        iSlotInfo = this;
        iMailSubmit = this;
        mActivity = this;
        mContext = this;
        iMobileSubmit = this;
        paymentResponse = this;
        iSendMessage = this;

        // getting necessary details from intent
        Intent intent = getIntent();
        uniqueId = intent.getIntExtra("uniqueID", 0);
        providerName = intent.getStringExtra("providerName");
        serviceInfo = (ServiceInfo) intent.getSerializableExtra("serviceInfo");
        locationId = intent.getIntExtra("locationId", 0);
        providerId = intent.getIntExtra("providerId", 0);
        userId = intent.getIntExtra("userId", 0);
        tvConsumerName = findViewById(R.id.tv_consumerName);
        list = findViewById(R.id.list);
        recycle_family = findViewById(R.id.recycle_family);

        int consumerId = SharedPreference.getInstance(AppointmentActivity.this).getIntValue("consumerId", 0);
        familyMEmID = consumerId;

        Typeface tyface1 = Typeface.createFromAsset(AppointmentActivity.this.getAssets(),
                "fonts/JosefinSans-SemiBold.ttf");
        etCode.setTypeface(tyface1);

        if (providerName != null) {
            tvProviderName.setText(providerName);
        }

        if (serviceInfo != null) {
            String name = serviceInfo.getServiceName();
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
            tvServiceName.setText(name);
            tvDescription.setText(serviceInfo.getDescription());
            try {
                if (serviceInfo.getType().equalsIgnoreCase(Constants.APPOINTMENT)) {
                    if (serviceInfo.getAvailableDate() != null) {
                        tvDate.setText(convertDate(serviceInfo.getAvailableDate()));
                    }
                    if (serviceInfo.getTime() != null) {
                        apiDate = serviceInfo.getAvailableDate();
                        scheduleId = serviceInfo.getScheduleId();
                        tvTime.setText(convertTime(serviceInfo.getTime()));
                        slotTime = serviceInfo.getTime();
                    }
                    llAppointment.setVisibility(View.VISIBLE);
                    llCheckIn.setVisibility(View.GONE);
                } else if (serviceInfo.getType().equalsIgnoreCase(Constants.CHECKIN)) {
                    llCheckIn.setVisibility(View.VISIBLE);
                    llAppointment.setVisibility(View.GONE);
                    String time = getWaitingTime(serviceInfo.getAvailableDate(), serviceInfo.getTime(), serviceInfo.getWaitingTime());
                    tvCheckInDate.setText(time.split("-")[1]);
                    tvHint.setText(time.split("-")[0]);

                    if (serviceInfo.getPeopleWaitingInLine() >= 0) {
                        if (serviceInfo.getPeopleWaitingInLine() == 0) {
                            tvPeopleInLine.setText("Be the first in line");
                        } else if (serviceInfo.getPeopleWaitingInLine() == 1) {
                            tvPeopleInLine.setText(serviceInfo.getPeopleWaitingInLine() + "  person waiting in line");
                        } else {
                            tvPeopleInLine.setText(serviceInfo.getPeopleWaitingInLine() + "  people waiting in line");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (serviceInfo.getServiceType() != null && serviceInfo.getServiceType().equalsIgnoreCase("virtualService")) {

                if (serviceInfo.getCallingMode() != null) {

                    if (serviceInfo.getCallingMode().equalsIgnoreCase("WhatsApp") || serviceInfo.getCallingMode().equalsIgnoreCase("Phone")) {

                        llVirtualNumber.setVisibility(View.VISIBLE);
                    } else {
                        llVirtualNumber.setVisibility(View.GONE);
                    }

                    ivteleService.setVisibility(View.VISIBLE);
                    if (serviceInfo.getCallingMode().equalsIgnoreCase("Zoom")) {

                        ivteleService.setImageResource(R.drawable.zoom);

                    } else if (serviceInfo.getCallingMode().equalsIgnoreCase("GoogleMeet")) {

                        ivteleService.setImageResource(R.drawable.googlemeet);

                    } else if (serviceInfo.getCallingMode().equalsIgnoreCase("WhatsApp")) {

                        ivteleService.setImageResource(R.drawable.whatsapp_icon);

                    } else if (serviceInfo.getCallingMode().equalsIgnoreCase("Phone")) {

                        ivteleService.setImageResource(R.drawable.phone_icon);

                    } else {
                        ivteleService.setVisibility(View.GONE);
                    }
                } else {
                    ivteleService.setVisibility(View.GONE);
                    llVirtualNumber.setVisibility(View.GONE);
                }
            } else {
                ivteleService.setVisibility(View.GONE);
                llVirtualNumber.setVisibility(View.GONE);
            }

            if (serviceInfo.getIsPrePayment().equalsIgnoreCase("true")) {

                if (serviceInfo.isUser()) {
                    APIPayment(String.valueOf(userId));
                } else {
                    APIPayment(String.valueOf(providerId));

                }
            } else {

                LservicePrepay.setVisibility(View.GONE);
            }

            if (serviceInfo.isPreInfoEnabled()) {  //  check if pre-info is available for the service

                llPreInfo.setVisibility(View.VISIBLE);
                if (serviceInfo.getPreInfoTitle() != null) {
                    tvPreInfoTitle.setText(serviceInfo.getPreInfoTitle());
                } else {
                    llPreInfo.setVisibility(View.GONE);
                }
                if (serviceInfo.getPreInfoText() != null) {
                    tvPreInfo.setText((Html.fromHtml(serviceInfo.getPreInfoText())));
                } else {
                    llPreInfo.setVisibility(View.GONE);
                }
            } else {
                llPreInfo.setVisibility(View.GONE);
            }
        }

        mFirstName = SharedPreference.getInstance(AppointmentActivity.this).getStringValue("firstname", "");
        mLastName = SharedPreference.getInstance(AppointmentActivity.this).getStringValue("lastname", "");
        consumerID = SharedPreference.getInstance(AppointmentActivity.this).getIntValue("consumerId", 0);

        // api calls
        ApiSearchViewTerminology(uniqueId);
        ApiGetProfileDetail();
        ApiSearchViewSetting(uniqueId);
        apiSearchViewDetail(uniqueId);
        ApiJaldeegetS3Coupons(uniqueId);

        // click actions

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        tvEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mailId = "";
                if (tvEmail.getText().toString() != null) {
                    mailId = tvEmail.getText().toString();
                }
                emailEditWindow = new EmailEditWindow(AppointmentActivity.this, profileDetails, iMailSubmit, mailId);
                emailEditWindow.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                emailEditWindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
                emailEditWindow.show();
                DisplayMetrics metrics = AppointmentActivity.this.getResources().getDisplayMetrics();
                int width = (int) (metrics.widthPixels * 1);
                emailEditWindow.getWindow().setGravity(Gravity.BOTTOM);
                emailEditWindow.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        });

        tvNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tvNumber.getText().toString() != null) {
                    mobileNumberDialog = new MobileNumberDialog(AppointmentActivity.this, profileDetails, iMobileSubmit, tvNumber.getText().toString());
                    mobileNumberDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                    mobileNumberDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    mobileNumberDialog.show();
                    DisplayMetrics metrics = AppointmentActivity.this.getResources().getDisplayMetrics();
                    int width = (int) (metrics.widthPixels * 1);
                    mobileNumberDialog.getWindow().setGravity(Gravity.BOTTOM);
                    mobileNumberDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                }
            }
        });

        llEditDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent familyIntent = new Intent(AppointmentActivity.this, CheckinFamilyMemberAppointment.class);
                familyIntent.putExtra("firstname", mFirstName);
                familyIntent.putExtra("lastname", mLastName);
                familyIntent.putExtra("consumerID", consumerID);
                familyIntent.putExtra("multiple", multiplemem);
                familyIntent.putExtra("memberID", familyMEmID);
                familyIntent.putExtra("update", 0);
                startActivity(familyIntent);

            }
        });

        tvChangeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (serviceInfo.getAvailableDate() != null) {
                        if (serviceInfo.isUser()) {
                            slotsDialog = new SlotsDialog(AppointmentActivity.this, serviceInfo.getServiceId(), locationId, iSlotInfo, userId, apiDate);

                        } else {
                            slotsDialog = new SlotsDialog(AppointmentActivity.this, serviceInfo.getServiceId(), locationId, iSlotInfo, providerId, apiDate);

                        }
                        slotsDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                        slotsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        slotsDialog.show();
                        DisplayMetrics metrics = AppointmentActivity.this.getResources().getDisplayMetrics();
                        int width = (int) (metrics.widthPixels * 1);
                        slotsDialog.setCancelable(false);
                        slotsDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        tvApplyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvApplyCode.setVisibility(View.GONE);
                rlCoupon.setVisibility(View.VISIBLE);
            }
        });


        tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                couponEntered = etCode.getEditableText().toString();

                boolean found = false;
                for (int index = 0; index < couponArraylist.size(); index++) {
                    if (couponArraylist.get(index).equals(couponEntered)) {
                        found = true;
                        break;
                    }
                }
                if (found) {

                    Toast.makeText(AppointmentActivity.this, "Coupon already added", Toast.LENGTH_SHORT).show();

                    return;
                }
                found = false;
                for (int i = 0; i < s3couponList.size(); i++) {
                    if (s3couponList.get(i).getJaldeeCouponCode().equals(couponEntered)) {
                        found = true;
                        break;
                    }
                }
                if (found) {

                    couponArraylist.add(couponEntered);

                    etCode.setText("");
                    Toast.makeText(AppointmentActivity.this, couponEntered + " " + "Added", Toast.LENGTH_SHORT).show();


                } else {
                    if (couponEntered.equals("")) {
                        Toast.makeText(AppointmentActivity.this, "Enter a coupon", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AppointmentActivity.this, "Coupon Invalid", Toast.LENGTH_SHORT).show();
                    }


                }
                Config.logV("couponArraylist--code-------------------------" + couponArraylist);
                list.setVisibility(View.VISIBLE);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AppointmentActivity.this);
                list.setLayoutManager(mLayoutManager);
                mAdapter = new CouponlistAdapter(AppointmentActivity.this, s3couponList, couponEntered, couponArraylist);
                list.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        });


        cvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tvNumber.length() < 10) {
                    Toast.makeText(AppointmentActivity.this, "Mobile number should have 10 digits" + "", Toast.LENGTH_SHORT).show();
                } else {
                    if (serviceInfo.getIsPrePayment().equalsIgnoreCase("true")) {  // check if selected service requires prepayment
                        if (tvEmail.getText().toString() != null && tvEmail.getText().length() > 0) { // if selected service requires prepayment..then Email is mandatory

                            if (serviceInfo.isNoteManidtory()) { // check if notes is mandatory for selected service

                                if (userMessage != null && !userMessage.equalsIgnoreCase("")) {

                                    if (serviceInfo.isUser()) {
                                        ApiAppointment(userMessage, userId);
                                    } else {
                                        ApiAppointment(userMessage, providerId);
                                    }
                                } else {

                                    DynamicToast.make(AppointmentActivity.this, serviceInfo.getConsumerNoteTitle() + "in Add Notes", AppCompatResources.getDrawable(
                                            AppointmentActivity.this, R.drawable.ic_info_black),
                                            ContextCompat.getColor(AppointmentActivity.this, R.color.white), ContextCompat.getColor(AppointmentActivity.this, R.color.green), Toast.LENGTH_SHORT).show();
                                }

                            } else {

                                if (serviceInfo.isUser()) {
                                    ApiAppointment(userMessage, userId);
                                } else {
                                    ApiAppointment(userMessage, providerId);
                                }
                            }
                        } else {

                            DynamicToast.make(AppointmentActivity.this, "Email id is mandatory", AppCompatResources.getDrawable(
                                    AppointmentActivity.this, R.drawable.ic_info_black),
                                    ContextCompat.getColor(AppointmentActivity.this, R.color.white), ContextCompat.getColor(AppointmentActivity.this, R.color.green), Toast.LENGTH_SHORT).show();

                        }
                    } else {

                        if (serviceInfo.isNoteManidtory()) {

                            if (userMessage != null && !userMessage.equalsIgnoreCase("")) {

                                if (serviceInfo.isUser()) {
                                    ApiAppointment(userMessage, userId);
                                } else {
                                    ApiAppointment(userMessage, providerId);
                                }
                            } else {

                                DynamicToast.make(AppointmentActivity.this, serviceInfo.getConsumerNoteTitle(), AppCompatResources.getDrawable(
                                        AppointmentActivity.this, R.drawable.ic_info_black),
                                        ContextCompat.getColor(AppointmentActivity.this, R.color.white), ContextCompat.getColor(AppointmentActivity.this, R.color.green), Toast.LENGTH_SHORT).show();
                            }

                        } else {

                            if (serviceInfo.isUser()) {
                                ApiAppointment(userMessage, userId);
                            } else {
                                ApiAppointment(userMessage, providerId);
                            }
                        }
                    }
                }
            }
        });

        //notes and files related
        cvAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addNotes = new AddNotes(mContext, providerName, iSendMessage, userMessage);
                addNotes.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                addNotes.requestWindowFeature(Window.FEATURE_NO_TITLE);
                addNotes.show();
                addNotes.setCancelable(false);
                DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                int width = (int) (metrics.widthPixels * 1);
                addNotes.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

            }
        });

        cvAttachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new BottomSheetDialog(mContext, R.style.DialogStyle);
                dialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                dialog.setContentView(R.layout.files_layout);
                dialog.show();

                final Button btn_send = dialog.findViewById(R.id.btn_send);
                Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
                btn_send.setText("Save");
                Typeface font_style = Typeface.createFromAsset(mContext.getAssets(), "fonts/JosefinSans-SemiBold.ttf");
                btn_cancel.setTypeface(font_style);
                btn_send.setTypeface(font_style);
                tvErrorMessage = dialog.findViewById(R.id.tv_errorMessage);
                tv_attach = dialog.findViewById(R.id.btn);
                tv_camera = dialog.findViewById(R.id.camera);
                recycle_image_attachment = dialog.findViewById(R.id.recycler_view_image);

                if (imagePathList != null && imagePathList.size() > 0) {
                    DetailFileImageAdapter mDetailFileAdapter = new DetailFileImageAdapter(imagePathList, mContext);
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(v.getContext(), 3);
                    recycle_image_attachment.setLayoutManager(mLayoutManager);
                    recycle_image_attachment.setAdapter(mDetailFileAdapter);
                    mDetailFileAdapter.notifyDataSetChanged();
                }


                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (imagePathList != null && imagePathList.size() > 0) {

                            tvErrorMessage.setVisibility(View.GONE);
                            imagePathLists = imagePathList;
                            dialog.dismiss();
                        } else {

                            tvErrorMessage.setVisibility(View.VISIBLE);
                        }

                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (imagePathList != null && imagePathLists != null) {
                            imagePathLists.clear();
                            imagePathList.clear();
                        }
                        dialog.dismiss();
                    }
                });


                requestMultiplePermissions();
                tv_attach.setVisibility(View.VISIBLE);
                tv_camera.setVisibility(View.VISIBLE);


                tv_attach.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (imagePathLists.size() > 0) {
                            DetailFileImageAdapter mDetailFileAdapter = new DetailFileImageAdapter(imagePathLists, mContext);
                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(v.getContext(), 3);
                            recycle_image_attachment.setLayoutManager(mLayoutManager);
                            recycle_image_attachment.setAdapter(mDetailFileAdapter);
                            mDetailFileAdapter.notifyDataSetChanged();
                        }
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if ((ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                                    requestPermissions(new String[]{
                                            Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY);

                                    return;
                                } else {
                                    Intent intent = new Intent();
                                    intent.setType("*/*");
                                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
                                }
                            } else {

                                Intent intent = new Intent();
                                intent.setType("*/*");
                                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                });

//
                tv_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


                                    requestPermissions(new String[]{
                                            Manifest.permission.CAMERA}, CAMERA);

                                    return;
                                } else {
                                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                    Intent cameraIntent = new Intent();
                                    cameraIntent.setType("image/*");
                                    cameraIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                    cameraIntent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(intent, CAMERA);
                                }
                            } else {

                                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                Intent cameraIntent = new Intent();
                                cameraIntent.setType("image/*");
                                cameraIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                cameraIntent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent, CAMERA);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                });

                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (imagePathList != null && imagePathList.size() > 0) {

                            tvErrorMessage.setVisibility(View.GONE);
                            imagePathLists = imagePathList;
                            dialog.dismiss();
                        } else {

                            tvErrorMessage.setVisibility(View.VISIBLE);
                        }
                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (imagePathList != null && imagePathLists != null) {
                            imagePathLists.clear();
                            imagePathList.clear();
                        }
                        dialog.dismiss();
                    }
                });
            }
        });

    }


    private void ApiSearchViewTerminology(int muniqueID) {


        ApiInterface apiService =
                ApiClient.getClientS3Cloud(AppointmentActivity.this).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(AppointmentActivity.this, AppointmentActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));
        Call<SearchTerminology> call = apiService.getSearchViewTerminology(muniqueID, sdf.format(currentTime));

        call.enqueue(new Callback<SearchTerminology>() {
            @Override
            public void onResponse(Call<SearchTerminology> call, Response<SearchTerminology> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        mSearchTerminology = response.body();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<SearchTerminology> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);

            }
        });
    }

    private void ApiGetProfileDetail() {

        ApiInterface apiService =
                ApiClient.getClient(AppointmentActivity.this).create(ApiInterface.class);

        final int consumerId = SharedPreference.getInstance(AppointmentActivity.this).getIntValue("consumerId", 0);

        final Dialog mDialog = Config.getProgressDialog(AppointmentActivity.this, AppointmentActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ProfileModel> call = apiService.getProfileDetail(consumerId);

        call.enqueue(new Callback<ProfileModel>() {
            @Override
            public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        profileDetails = response.body();
                        if (profileDetails != null) {
                            tvConsumerName.setText(profileDetails.getUserprofile().getFirstName() + " " + profileDetails.getUserprofile().getLastName());
                            tvNumber.setText(profileDetails.getUserprofile().getPrimaryMobileNo());
                            etVirtualNumber.setText(profileDetails.getUserprofile().getPrimaryMobileNo());
                            phoneNumber = tvNumber.getText().toString();
                            if (profileDetails.getUserprofile().getEmail() != null) {
                                tvEmail.setText(profileDetails.getUserprofile().getEmail());
                            } else {
                                tvEmail.setHint("Enter your Mail Id");
                            }
                        }
                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(Call<ProfileModel> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });


    }

    private void ApiSearchViewSetting(int muniqueID) {


        ApiInterface apiService =
                ApiClient.getClientS3Cloud(AppointmentActivity.this).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(AppointmentActivity.this, AppointmentActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));


        Call<SearchSetting> call = apiService.getSearchViewSetting(muniqueID, sdf.format(currentTime));

        call.enqueue(new Callback<SearchSetting>() {
            @Override
            public void onResponse(Call<SearchSetting> call, Response<SearchSetting> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        if (response.body().getCalculationMode() != null) {

                            calcMode = response.body().getCalculationMode();

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<SearchSetting> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });
    }

    private void apiSearchViewDetail(int id) {
        ApiInterface apiService = ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));
        Call<SearchViewDetail> call = apiService.getSearchViewDetail(id, sdf.format(currentTime));
        call.enqueue(new Callback<SearchViewDetail>() {
            @Override
            public void onResponse(Call<SearchViewDetail> call, final Response<SearchViewDetail> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(AppointmentActivity.this, mDialog);
                    Config.logV("URL-----1111----------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-----detail--------------------" + response.code());
                    if (response.code() == 200) {
                        mBusinessDataList = response.body();

                        if (mBusinessDataList != null) {

                            sector = mBusinessDataList.getServiceSector().getDomain();
                            subsector = mBusinessDataList.getServiceSubSector().getSubDomain();
                            APISector(sector, subsector);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SearchViewDetail> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(AppointmentActivity.this, mDialog);
            }
        });
    }


    SectorCheckin checkin_sector = null;
    int partySize = 0;
    boolean enableparty = false;
    boolean multiplemem = false;

    private void APISector(String sector, String subsector) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        Call<SectorCheckin> call = apiService.getSector(sector, subsector);

        call.enqueue(new Callback<SectorCheckin>() {
            @Override
            public void onResponse(Call<SectorCheckin> call, Response<SectorCheckin> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        checkin_sector = response.body();
                        maxPartysize = 0;
                        if (checkin_sector.isPartySize() && !checkin_sector.isPartySizeForCalculation()) {
                            enableparty = true;
                            maxPartysize = checkin_sector.getMaxPartySize();

                        } else {
                            enableparty = false;
                        }
                        if (checkin_sector.isPartySizeForCalculation()) {
                            multiplemem = true;

                        } else {
                            multiplemem = false;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SectorCheckin> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });
    }


    private void ApiJaldeegetS3Coupons(int uniqueID) {

        ApiInterface apiService =
                ApiClient.getClientS3Cloud(AppointmentActivity.this).create(ApiInterface.class);

        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));

        Call<ArrayList<CoupnResponse>> call = apiService.getCoupanList(uniqueID, sdf.format(currentTime));

        call.enqueue(new Callback<ArrayList<CoupnResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<CoupnResponse>> call, Response<ArrayList<CoupnResponse>> response) {

                try {

                    Config.logV("Response---------------------------" + response.body().toString());
                    Config.logV("URL-response--------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        s3couponList.clear();
                        s3couponList = response.body();
                        Log.i("CouponResponse", s3couponList.toString());

                        if (s3couponList.size() != 0) {
                            tvApplyCode.setVisibility(View.VISIBLE);
                        } else {
                            tvApplyCode.setVisibility(View.GONE);
                        }

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<CoupnResponse>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());

            }
        });
    }


    boolean showPaytmWallet = false;
    boolean showPayU = false;

    private void APIPayment(String accountID) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        Call<ArrayList<PaymentModel>> call = apiService.getPaymentModes(accountID);

        call.enqueue(new Callback<ArrayList<PaymentModel>>() {
            @Override
            public void onResponse(Call<ArrayList<PaymentModel>> call, Response<ArrayList<PaymentModel>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL----%%%%%-----------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        mPaymentData = response.body();

                        for (int i = 0; i < mPaymentData.size(); i++) {
                            if (mPaymentData.get(i).getDisplayname().equalsIgnoreCase("Wallet")) {
                                showPaytmWallet = true;
                            }

                            if (mPaymentData.get(i).getName().equalsIgnoreCase("CC") || mPaymentData.get(i).getName().equalsIgnoreCase("DC") || mPaymentData.get(i).getName().equalsIgnoreCase("NB")) {
                                showPayU = true;
                            }
                        }

                        if ((showPayU) || showPaytmWallet) {
                            Config.logV("URL----%%%%%---@@--");
                            LservicePrepay.setVisibility(View.VISIBLE);
                            Typeface tyface = Typeface.createFromAsset(getAssets(),
                                    "fonts/Montserrat_Bold.otf");
                            txtprepay.setTypeface(tyface);
                            txtprepayamount.setTypeface(tyface);
                            String firstWord = "Prepayment Amount: ";
                            String secondWord = "₹ " + Config.getAmountinTwoDecimalPoints(Double.parseDouble(serviceInfo.getMinPrePaymentAmount()));
                            Spannable spannable = new SpannableString(firstWord + secondWord);
                            spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.colorAccent)),
                                    firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            txtprepayamount.setText(spannable);
                        }

                    } else {
                        Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<PaymentModel>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);

            }
        });


    }


    private void ApiAppointment(final String txt_addnote, int id) {

        phoneNumber = tvNumber.getText().toString();
        uuid = UUID.randomUUID().toString();


        ApiInterface apiService =
                ApiClient.getClient(AppointmentActivity.this).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(AppointmentActivity.this, AppointmentActivity.this.getResources().getString(R.string.dialog_log_in));
        //  mDialog.show();

        JSONObject qjsonObj = new JSONObject();
        JSONObject queueobj = new JSONObject();
        JSONObject waitobj = new JSONObject();
        JSONObject waitobj1 = new JSONObject();
        JSONObject waitobj2 = new JSONObject();
        JSONObject waitobj3 = new JSONObject();
        JSONObject waitobj4 = new JSONObject();
        JSONObject sejsonobj = new JSONObject();
        JSONArray waitlistArray = new JSONArray();
        JSONObject sjsonobj = new JSONObject();
        JSONObject virtualService = new JSONObject();
        JSONObject pjsonobj = new JSONObject();


        try {

            queueobj.put("appmtDate", apiDate);
            sjsonobj.put("id", scheduleId);
            queueobj.put("consumerNote", txt_addnote);
            queueobj.put("phonenumber", phoneNumber);
            if (serviceInfo.isUser()) {
                pjsonobj.put("id", providerId);
            } else {
                pjsonobj.put("id", 0);
            }

            if (etVirtualNumber.getText().toString().trim().length() > 9) {
                if (serviceInfo.getCallingMode() != null && serviceInfo.getCallingMode().equalsIgnoreCase("whatsapp")) {
                    virtualService.put("WhatsApp", etVirtualNumber.getText());
                } else if (serviceInfo.getCallingMode() != null && serviceInfo.getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                    virtualService.put("GoogleMeet", serviceInfo.getVirtualCallingValue());
                } else if (serviceInfo.getCallingMode() != null && serviceInfo.getCallingMode().equalsIgnoreCase("Zoom")) {
                    virtualService.put("Zoom", serviceInfo.getVirtualCallingValue());
                } else if (serviceInfo.getCallingMode() != null && serviceInfo.getCallingMode().equalsIgnoreCase("Phone")) {
                    virtualService.put("Phone", etVirtualNumber.getText());
                }
            } else {
                DynamicToast.make(AppointmentActivity.this, "Invalid phone number", AppCompatResources.getDrawable(
                        AppointmentActivity.this, R.drawable.ic_info_black),
                        ContextCompat.getColor(AppointmentActivity.this, R.color.white), ContextCompat.getColor(AppointmentActivity.this, R.color.green), Toast.LENGTH_SHORT).show();
                return;
            }

            sejsonobj.put("id", serviceInfo.getServiceId());
            JSONArray couponList = new JSONArray();

            for (int i = 0; i < couponArraylist.size(); i++) {
                couponList.put(couponArraylist.get(i));
            }
            Log.i("kooooooo", couponList.toString());
            queueobj.put("coupons", couponList);
            Log.i("couponList", couponList.toString());

            if (familyMEmID == 0) {
                familyMEmID = consumerID;
            }

            if (MultiplefamilyList.size() > 0) {
                for (int i = 0; i < MultiplefamilyList.size(); i++) {

                    waitobj1.put("id", MultiplefamilyList.get(i).getId());


                    waitobj2.put("firstName", MultiplefamilyList.get(i).getFirstName());


                    waitobj3.put("lastName", MultiplefamilyList.get(i).getLastName());

                    waitobj4.put("apptTime", slotTime);


                    waitlistArray.put(waitobj1);
                    waitlistArray.put(waitobj2);
                    waitlistArray.put(waitobj3);
                    waitlistArray.put(waitobj4);
                }
            } else {
                if (familyMEmID == consumerID) {
                    familyMEmID = 0;
                }
                waitobj.put("id", familyMEmID);
                waitobj.put("firstName", mFirstName);
                waitobj.put("lastName", mLastName);

                waitobj.put("apptTime", slotTime);

            }
            waitlistArray.put(waitobj);


            queueobj.putOpt("service", sejsonobj);
            // queueobj.putOpt("queue", qjsonObj);
            queueobj.putOpt("appmtFor", waitlistArray);
            queueobj.putOpt("schedule", sjsonobj);
            queueobj.putOpt("provider", pjsonobj);

            if (serviceInfo.getServiceType().equalsIgnoreCase("virtualService")) {
                queueobj.putOpt("virtualService", virtualService);
            }

        } catch (
                JSONException e) {
            e.printStackTrace();
        }

        Log.i("QueueObj Checkin", queueobj.toString());
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queueobj.toString());
        Call<ResponseBody> call = apiService.Appointment(String.valueOf(id), body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    Config.logV("Response--code-------------------------" + response.body());

                    MultiplefamilyList.clear();
                    if (response.code() == 200) {


                        SharedPreference.getInstance(AppointmentActivity.this).setValue("refreshcheckin", "true");

                        JSONObject reader = new JSONObject(response.body().string());
                        Iterator iteratorObj = reader.keys();

                        while (iteratorObj.hasNext()) {
                            String getJsonObj = (String) iteratorObj.next();
                            System.out.println("KEY: " + "------>" + getJsonObj);
                            value = reader.getString(getJsonObj);


                        }
                  //      if (serviceInfo.getIsPrePayment().equalsIgnoreCase("true")) {
//                            if (!showPaytmWallet && !showPayU) {
//
//                                //Toast.makeText(mContext,"Pay amount by Cash",Toast.LENGTH_LONG).show();
//                            } else {
                                if (serviceInfo.isUser()) {
                                    getConfirmationId(userId,txt_addnote,id);
                                }
                                else{
                                    getConfirmationId(providerId,txt_addnote,id);
                                }
//                                try {
//                                    dialog = new BottomSheetDialog(AppointmentActivity.this);
//                                    dialog.setContentView(R.layout.prepayment);
//                                    dialog.setCancelable(false);
//                                    dialog.show();
//
//
//                                    Button btn_paytm = (Button) dialog.findViewById(R.id.btn_paytm);
//                                    Button btn_payu = (Button) dialog.findViewById(R.id.btn_payu);
//                                    ImageView ivClose = dialog.findViewById(R.id.iv_close);
//                                    ivClose.setVisibility(View.VISIBLE);
//                                    if (showPaytmWallet) {
//                                        btn_paytm.setVisibility(View.VISIBLE);
//                                    } else {
//                                        btn_paytm.setVisibility(View.GONE);
//                                    }
//                                    if (showPayU) {
//                                        btn_payu.setVisibility(View.VISIBLE);
//                                    } else {
//                                        btn_payu.setVisibility(View.GONE);
//                                    }
//                                    final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
//                                    TextView txtamt = (TextView) dialog.findViewById(R.id.txtamount);
//
//                                    TextView txtprepayment = (TextView) dialog.findViewById(R.id.txtprepayment);
//
//                                    txtprepayment.setText("Prepayment Amount ");
//
////                                    DecimalFormat format = new DecimalFormat("0.00");
//                                    txtamt.setText("Rs." + Config.getAmountinTwoDecimalPoints((Double.parseDouble(serviceInfo.getMinPrePaymentAmount()))));
//                                    Typeface tyface1 = Typeface.createFromAsset(AppointmentActivity.this.getAssets(),
//                                            "fonts/JosefinSans-SemiBold.ttf");
//                                    txtamt.setTypeface(tyface1);
//                                    ivClose.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                            finish();
//                                        }
//                                    });
//                                    btn_payu.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                            new PaymentGateway(AppointmentActivity.this, AppointmentActivity.this).ApiGenerateHash1(value, serviceInfo.getMinPrePaymentAmount(), String.valueOf(id), Constants.PURPOSE_PREPAYMENT, "checkin", familyMEmID, Constants.SOURCE_PAYMENT);
//                                            dialog.dismiss();
//
//                                        }
//                                    });
//
//                                    btn_paytm.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                            PaytmPayment payment = new PaytmPayment(AppointmentActivity.this, paymentResponse);
//                                            payment.ApiGenerateHashPaytm(value, serviceInfo.getMinPrePaymentAmount(), String.valueOf(id), Constants.PURPOSE_PREPAYMENT, AppointmentActivity.this, AppointmentActivity.this, "", familyMEmID,appEncId);
//                                            //payment.generateCheckSum(sAmountPay);
//                                            dialog.dismiss();
//
//                                        }
//                                    });
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
                           // }


                   //     } else {
                            //  txt_message ="Please find the attachment from Consumer with this message";

//                            if (serviceInfo.isUser()) {
//                                if (imagePathList.size() > 0) {
//                                    ApiCommunicateAppointment(value, String.valueOf(userId), txt_addnote, dialog);
//                                }
//                                if (!serviceInfo.getLivetrack().equalsIgnoreCase("true")) {
//                                    getConfirmationDetails(userId);
//                                }
//
//                            } else {
//
//                                if (imagePathList.size() > 0) {
//                                    ApiCommunicateAppointment(value, String.valueOf(providerId), txt_addnote, dialog);
//                                }
//                                if (!serviceInfo.getLivetrack().equalsIgnoreCase("true")) {
//                                    getConfirmationDetails(providerId);
//                                }
//
//                            }
                      //  }


//                        if (serviceInfo.getLivetrack().equalsIgnoreCase("true")) {
//                            Intent checkinShareLocations = new Intent(AppointmentActivity.this, CheckinShareLocationAppointment.class);
//                            checkinShareLocations.putExtra("waitlistPhonenumber", phoneNumber);
//                            checkinShareLocations.putExtra("uuid", value);
//                            if (serviceInfo.isUser()){
//                                checkinShareLocations.putExtra("accountID", String.valueOf(userId));
//                            }else {
//                                checkinShareLocations.putExtra("accountID", String.valueOf(providerId));
//                            }
//                            checkinShareLocations.putExtra("title", providerName);
//                            checkinShareLocations.putExtra("terminology", mSearchTerminology.getWaitlist());
//                            checkinShareLocations.putExtra("calcMode", calcMode);
//                            checkinShareLocations.putExtra("queueStartTime", "");
//                            checkinShareLocations.putExtra("queueEndTime", "");
//                            checkinShareLocations.putExtra("from", "appt");
//                            startActivity(checkinShareLocations);
//                        }

                    } else {
                        if (response.code() == 422) {

                            String errorString = response.errorBody().string();

                            Config.logV("Error String-----------" + errorString);
                            Map<String, String> tokens = new HashMap<String, String>();
                            tokens.put("Customer", Config.toTitleCase(mSearchTerminology.getCustomer()));
                            tokens.put("provider", mSearchTerminology.getProvider());
                            tokens.put("arrived", mSearchTerminology.getArrived());
                            tokens.put("waitlisted", mSearchTerminology.getWaitlist());

                            tokens.put("start", mSearchTerminology.getStart());
                            tokens.put("cancelled", mSearchTerminology.getCancelled());
                            tokens.put("done", mSearchTerminology.getDone());


                            StringBuffer sb = new StringBuffer();

                            Pattern p3 = Pattern.compile("\\[(.*?)\\]");

                            Matcher matcher = p3.matcher(errorString);
                            while (matcher.find()) {
                                System.out.println(matcher.group(1));
                                matcher.appendReplacement(sb, tokens.get(matcher.group(1)));
                            }
                            matcher.appendTail(sb);

                            System.out.println("SubString@@@@@@@@@@@@@" + sb.toString());


                            Toast.makeText(AppointmentActivity.this, sb.toString(), Toast.LENGTH_LONG).show();
                        } else {
                            String responseerror = response.errorBody().string();
                            Config.logV("Response--error-------------------------" + responseerror);
                            if (response.code() != 419)
                                Toast.makeText(AppointmentActivity.this, responseerror, Toast.LENGTH_LONG).show();
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
                    Config.closeDialog(getParent(), mDialog);

            }
        });


    }

    private void ApiCommunicateAppointment(String waitListId, String accountID, String message,
                                           final BottomSheetDialog dialog) {


        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        MediaType type = MediaType.parse("*/*");
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        mBuilder.addFormDataPart("message", message);
        for (int i = 0; i < imagePathList.size(); i++) {

            try {
                bitmap = MediaStore.Images.Media.getBitmap(mContext.getApplicationContext().getContentResolver(), Uri.fromFile(new File(imagePathList.get(i))));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap != null) {
                path = saveImage(bitmap);
                file = new File(path);
            }
//            else{
//                path = getRealFilePath(Uri.parse(imagePathList.get(0)));
//            }
            else {
                file = new File(imagePathList.get(i));
            }
            mBuilder.addFormDataPart("attachments", file.getName(), RequestBody.create(type, file));
        }
        RequestBody requestBody = mBuilder.build();


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());

        Call<ResponseBody> call = apiService.AppointmentMessage(waitListId, String.valueOf(accountID.split("-")[0]), requestBody);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {


                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
//                        Toast.makeText(mContext, "Message sent successfully", Toast.LENGTH_LONG).show();
                        imagePathList.clear();
                        dialog.dismiss();


                    } else {
                        if (response.code() == 422) {
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
            }
        });

    }

    private void getConfirmationDetails(int userId) {

        final ApiInterface apiService =
                ApiClient.getClient(AppointmentActivity.this).create(ApiInterface.class);
        Call<ActiveAppointment> call = apiService.getActiveAppointmentUUID(value, String.valueOf(userId));
        call.enqueue(new Callback<ActiveAppointment>() {
            @Override
            public void onResponse(Call<ActiveAppointment> call, Response<ActiveAppointment> response) {
                try {
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        activeAppointment = response.body();
                        if (activeAppointment != null) {
                            appEncId = activeAppointment.getAppointmentEncId();
                            Bundle b = new Bundle();
                            b.putSerializable("BookingDetails", activeAppointment);
                            b.putString("terminology", mSearchTerminology.getProvider());
                            Intent checkin = new Intent(AppointmentActivity.this, AppointmentConfirmation.class);
                            checkin.putExtras(b);
                            startActivity(checkin);
                        }

                    }
                } catch (Exception e) {
                    Log.i("mnbbnmmnbbnm", e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ActiveAppointment> call, Throwable t) {
            }
        });

    }

    private void getConfirmationId(int userId, String txt_addnote,int id) {

        final ApiInterface apiService =
                ApiClient.getClient(AppointmentActivity.this).create(ApiInterface.class);
        Call<ActiveAppointment> call = apiService.getActiveAppointmentUUID(value, String.valueOf(userId));
        call.enqueue(new Callback<ActiveAppointment>() {
            @Override
            public void onResponse(Call<ActiveAppointment> call, Response<ActiveAppointment> response) {
                try {
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        activeAppointment = response.body();
                        if (activeAppointment != null) {
                            appEncId = activeAppointment.getAppointmentEncId();
                            if (serviceInfo.getIsPrePayment().equalsIgnoreCase("true")) {
                                if (!showPaytmWallet && !showPayU) {

                                    //Toast.makeText(mContext,"Pay amount by Cash",Toast.LENGTH_LONG).show();
                                } else {
                                    try {
                                        dialog = new BottomSheetDialog(AppointmentActivity.this);
                                        dialog.setContentView(R.layout.prepayment);
                                        dialog.setCancelable(false);
                                        dialog.show();


                                        Button btn_paytm = (Button) dialog.findViewById(R.id.btn_paytm);
                                        Button btn_payu = (Button) dialog.findViewById(R.id.btn_payu);
                                        ImageView ivClose = dialog.findViewById(R.id.iv_close);
                                        ivClose.setVisibility(View.VISIBLE);
                                        if (showPaytmWallet) {
                                            btn_paytm.setVisibility(View.VISIBLE);
                                        } else {
                                            btn_paytm.setVisibility(View.GONE);
                                        }
                                        if (showPayU) {
                                            btn_payu.setVisibility(View.VISIBLE);
                                        } else {
                                            btn_payu.setVisibility(View.GONE);
                                        }
                                        final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
                                        TextView txtamt = (TextView) dialog.findViewById(R.id.txtamount);

                                        TextView txtprepayment = (TextView) dialog.findViewById(R.id.txtprepayment);

                                        txtprepayment.setText("Prepayment Amount ");

//                                    DecimalFormat format = new DecimalFormat("0.00");
                                        txtamt.setText("Rs." + Config.getAmountinTwoDecimalPoints((Double.parseDouble(serviceInfo.getMinPrePaymentAmount()))));
                                        Typeface tyface1 = Typeface.createFromAsset(AppointmentActivity.this.getAssets(),
                                                "fonts/JosefinSans-SemiBold.ttf");
                                        txtamt.setTypeface(tyface1);
                                        ivClose.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                finish();
                                            }
                                        });
                                        btn_payu.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                new PaymentGateway(AppointmentActivity.this, AppointmentActivity.this).ApiGenerateHash1(value, serviceInfo.getMinPrePaymentAmount(), String.valueOf(id), Constants.PURPOSE_PREPAYMENT, "checkin", familyMEmID, Constants.SOURCE_PAYMENT);
                                                dialog.dismiss();

                                            }
                                        });

                                        btn_paytm.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                PaytmPayment payment = new PaytmPayment(AppointmentActivity.this, paymentResponse);
                                                payment.ApiGenerateHashPaytm(value, serviceInfo.getMinPrePaymentAmount(), String.valueOf(id), Constants.PURPOSE_PREPAYMENT, AppointmentActivity.this, AppointmentActivity.this, "", familyMEmID,appEncId);
                                                //payment.generateCheckSum(sAmountPay);
                                                dialog.dismiss();

                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }


                            }
                            else {
                                //  txt_message ="Please find the attachment from Consumer with this message";


                                if (serviceInfo.isUser()) {
                                    if (imagePathList.size() > 0) {
                                        ApiCommunicateAppointment(value, String.valueOf(userId), txt_addnote, dialog);
                                    }
                                    if (!serviceInfo.getLivetrack().equalsIgnoreCase("true")) {
                                        getConfirmationDetails(userId);
                                    }

                                } else {

                                    if (imagePathList.size() > 0) {
                                        ApiCommunicateAppointment(value, String.valueOf(providerId), txt_addnote, dialog);
                                    }
                                    if (!serviceInfo.getLivetrack().equalsIgnoreCase("true")) {
                                        getConfirmationDetails(providerId);
                                    }

                                }
                            }
                            if (serviceInfo.getLivetrack().equalsIgnoreCase("true")) {
                                Intent checkinShareLocations = new Intent(AppointmentActivity.this, CheckinShareLocationAppointment.class);
                                checkinShareLocations.putExtra("waitlistPhonenumber", phoneNumber);
                                checkinShareLocations.putExtra("uuid", value);
                                if (serviceInfo.isUser()){
                                    checkinShareLocations.putExtra("accountID", String.valueOf(userId));
                                }else {
                                    checkinShareLocations.putExtra("accountID", String.valueOf(providerId));
                                }
                                checkinShareLocations.putExtra("title", providerName);
                                checkinShareLocations.putExtra("terminology", mSearchTerminology.getWaitlist());
                                checkinShareLocations.putExtra("calcMode", calcMode);
                                checkinShareLocations.putExtra("queueStartTime", "");
                                checkinShareLocations.putExtra("queueEndTime", "");
                                checkinShareLocations.putExtra("from", "appt");
                                startActivity(checkinShareLocations);
                            }

                        }

                    }
                } catch (Exception e) {
                    Log.i("mnbbnmmnbbnm", e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ActiveAppointment> call, Throwable t) {
            }
        });

    }


    public static String convertDate(String date) {

        String finalDate = "";
        Date selectedDate = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            selectedDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (DateUtils.isToday(selectedDate.getTime())) {
            finalDate = "Today";
        } else {
            Format f = new SimpleDateFormat("MMM dd");
            finalDate = f.format(selectedDate);
        }

        return finalDate;
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

    public static String getWaitingTime(String nextAvailableDate, String nextAvailableTime, String estTime) {
        String firstWord = "";
        String secondWord = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date c = Calendar.getInstance().getTime();
        String formattedDate = df.format(c);
        System.out.println("Current time => " + formattedDate);
        Date date1 = null, date2 = null;
        try {
            date1 = df.parse(formattedDate);
            if (nextAvailableDate != null)
                date2 = df.parse(nextAvailableDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String type = null;
        if (date2 != null && date1.compareTo(date2) < 0) {
            type = "future";
        }
        if (nextAvailableTime != null) {
            firstWord = "Next Available Time ";
            if (type != null) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date = null;
                try {
                    date = format.parse(nextAvailableDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String day = (String) DateFormat.format("dd", date);
                String monthString = (String) DateFormat.format("MMM", date);
//                Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
//                        "fonts/Montserrat_Bold.otf");
                secondWord = monthString + " " + day + ", " + nextAvailableTime;
//                String outputDateStr = outputFormat.format(datechange);
//                String yourDate = Config.getFormatedDate(outputDateStr);
//                secondWord = yourDate + ", " + queue.getServiceTime();
            } else {
                secondWord = "Today, " + nextAvailableTime;
            }
        } else {
            firstWord = "Est wait time";
            secondWord = Config.getTimeinHourMinutes(Integer.parseInt(estTime));
        }
        // Spannable spannable = new SpannableString(firstWord + secondWord);
//        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),"fonts/Montserrat_Bold.otf");
//        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.title_grey)), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return firstWord + "-" + secondWord;
    }

    private String getDisplayTime(String slotTime) {

        String displayTime = slotTime.split("-")[0];
        String sTime = "";

        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(displayTime);
            SimpleDateFormat time = new SimpleDateFormat("K:mm aa");
            sTime = time.format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return sTime;
    }

    @Override
    public void sendSlotInfo(String displayTime, String selectedSlot, int schdId, String displayDate, String date) {

        try {

            // getting data from dialog
            String convertedTime = displayTime.replace("am", "AM").replace("pm", "PM");
            tvTime.setText(convertedTime);
            tvDate.setText(displayDate);
            tvSelectedDateHint.setText("Selected Time slot");
            scheduleId = schdId;
            slotTime = selectedSlot;
            try {
                apiDate = getApiDateFormat(date);  // to convert selected date to api date format
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getApiDateFormat(String d) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("EEE, dd/MM/yyyy");
        Date date1 = format.parse(d);
        format = new SimpleDateFormat("yyyy-MM-dd");
        String yourDate = format.format(date1);

        return yourDate;

    }


    @Override
    public void mailUpdated() {
        String mail = SharedPreference.getInstance(mContext).getStringValue("email", "");
        tvEmail.setText(mail);

        //  ApiGetProfileDetail();

    }

    public static void refreshName(String name, int memID) {
        Config.logV("NAme----------" + name);
        if (name != null && !name.equalsIgnoreCase("")) {
            tvConsumerName.setText(name);
            familyMEmID = memID;
        }
    }

    static ArrayList<FamilyArrayModel> MultiplefamilyList = new ArrayList<>();

    public static void refreshMultipleMEmList(ArrayList<FamilyArrayModel> familyList) {
        MultiplefamilyList.clear();
        MultiplefamilyList.addAll(familyList);

        recycle_family.setVisibility(View.VISIBLE);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        recycle_family.setLayoutManager(mLayoutManager);
        MultipleFamilyMemberAdapter mFamilyAdpater = new MultipleFamilyMemberAdapter(familyList, mContext, mActivity);
        recycle_family.setAdapter(mFamilyAdpater);
        mFamilyAdpater.notifyDataSetChanged();
        tvConsumerName.setVisibility(View.GONE);
    }

    public void paymentFinished(RazorpayModel razorpayModel) {


        if (serviceInfo.isUser()) {
            if (imagePathList.size() > 0) {
                ApiCommunicateAppointment(value, String.valueOf(userId), txt_message, dialog);
            }
            getConfirmationDetails(userId);

        } else {
            if (imagePathList.size() > 0) {
                ApiCommunicateAppointment(value, String.valueOf(providerId), txt_message, dialog);
            }
            getConfirmationDetails(providerId);

        }
    }


    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {

        try {
            RazorpayModel razorpayModel = new RazorpayModel(paymentData);
            new PaymentGateway(this.mContext, mActivity).sendPaymentStatus(razorpayModel, "SUCCESS");
            Toast.makeText(this.mContext, "Payment Successful", Toast.LENGTH_LONG).show();
            paymentFinished(razorpayModel);
        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {

        try {
            AlertDialog alertDialog = new AlertDialog.Builder(AppointmentActivity.this).create();
            alertDialog.setTitle("Payment Failed");
            alertDialog.setMessage("Unable to process your request.Please try again after some time");
            alertDialog.setCancelable(false);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            Intent homeIntent = new Intent(AppointmentActivity.this, Home.class);
                            startActivity(homeIntent);
                            finish();

                        }
                    });
            alertDialog.show();
        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentError..", e);
        }
    }

    @Override
    public void sendPaymentResponse() {

        // Paytm
        if (serviceInfo.isUser()) {
            if (imagePathList.size() > 0) {
                ApiCommunicateAppointment(value, String.valueOf(userId), txt_message, dialog);
            }
            getConfirmationDetails(userId);

        } else {
            if (imagePathList.size() > 0) {
                ApiCommunicateAppointment(value, String.valueOf(providerId), txt_message, dialog);
            }
            getConfirmationDetails(providerId);

        }
    }

    @Override
    public void mobileUpdated() {
        String phone = SharedPreference.getInstance(mContext).getStringValue("mobile", "");
        tvNumber.setText(phone);
    }


    // files related

    public static String getFilePathFromURI(Context context, Uri contentUri, String extension) {
        //copy file and send new file path
        String fileName = getFileNameInfo(contentUri);
        if (!TextUtils.isEmpty(fileName)) {
            String ext = "";
            if (fileName.contains(".")) {
            } else {
                ext = "." + extension;
            }
            File wallpaperDirectoryFile = new File(
                    Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY + File.separator + fileName + ext);
            copy(context, contentUri, wallpaperDirectoryFile);
            return wallpaperDirectoryFile.getAbsolutePath();
        }
        return null;
    }

    protected static String getFileNameInfo(Uri uri) {
        if (uri == null) {
            return null;
        }
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            FileOutputStream outputStream = new FileOutputStream(dstFile);
            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRealPathFromURI(Uri contentURI, Activity context) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = context.managedQuery(contentURI, projection, null,
                null, null);
        if (cursor == null)
            return null;
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor.moveToFirst()) {
            String s = cursor.getString(column_index);
            // cursor.close();
            return s;
        }
        // cursor.close();
        return null;
    }


    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        if (myBitmap != null) {
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        }
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(mContext,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getPDFPath(Uri uri) {

        final String id = DocumentsContract.getDocumentId(uri);
        final Uri contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = mActivity.getApplicationContext().getContentResolver().query(contentUri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public String getFilePathFromURI(Uri contentUri, Context context) {
        //copy file and send new file path
        String fileName = getFileName(contentUri);
        if (!TextUtils.isEmpty(fileName)) {
            File copyFile = new File(context.getExternalCacheDir() + File.separator + fileName);
            //copy(context, contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }

    public String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    public String getRealFilePath(Uri uri) {
        String path = uri.getPath();
        String[] pathArray = path.split(":");
        String fileName = pathArray[pathArray.length - 1];
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName;
    }

    private void requestMultiplePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();fc
                            Toast.makeText(getApplicationContext(), "You Denied the Permission", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    public static float getImageSize(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            cursor.moveToFirst();
            float imageSize = cursor.getLong(sizeIndex);
            cursor.close();
            return imageSize / (1024f * 1024f); // returns size in bytes
        }
        return 0;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //   mTxvBuy.setEnabled(true);

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {


            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);
            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);

            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {

                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    showAlert("Payment Successful");
                    finish();
                } else if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.CANCELLED)) {
                    showAlert("Payment Cancelled");
                } else if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.FAILED)) {
                    showAlert("Payment Failed");
                }

            } else if (resultModel != null && resultModel.getError() != null) {
                Toast.makeText(this, "Error check log", Toast.LENGTH_SHORT).show();
            } else {
                //  Toast.makeText(this, "Both objects are null", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_CANCELED) {
            showAlert("Payment Cancelled");
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                try {
                    if (data.getData() != null) {
                        Uri uri = data.getData();
                        String orgFilePath = getRealPathFromURI(uri, this);
                        String filepath = "";//default fileName

                        String mimeType = this.mContext.getContentResolver().getType(uri);
                        String uriString = uri.toString();
                        String extension = "";
                        if (uriString.contains(".")) {
                            extension = uriString.substring(uriString.lastIndexOf(".") + 1);
                        }

                        if (mimeType != null) {
                            extension = mimeType.substring(mimeType.lastIndexOf("/") + 1);
                        }
                        if (Arrays.asList(fileExtsSupported).contains(extension)) {
                            if (orgFilePath == null) {
                                orgFilePath = getFilePathFromURI(mContext, uri, extension);
                            }
                        } else {
                            Toast.makeText(mContext, "File type not supported", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        imagePathList.add(orgFilePath);

                        DetailFileImageAdapter mDetailFileAdapter = new DetailFileImageAdapter(imagePathList, mContext);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
                        recycle_image_attachment.setLayoutManager(mLayoutManager);
                        recycle_image_attachment.setAdapter(mDetailFileAdapter);
                        mDetailFileAdapter.notifyDataSetChanged();

                    }
                    else if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri imageUri = item.getUri();
                            String orgFilePath = getRealPathFromURI(imageUri, this);
                            String filepath = "";//default fileName

                            String mimeType = mContext.getContentResolver().getType(imageUri);
                            String uriString = imageUri.toString();
                            String extension = "";
                            if (uriString.contains(".")) {
                                extension = uriString.substring(uriString.lastIndexOf(".") + 1);
                            }

                            if (mimeType != null) {
                                extension = mimeType.substring(mimeType.lastIndexOf("/") + 1);
                            }
                            if (Arrays.asList(fileExtsSupported).contains(extension)) {
                                if (orgFilePath == null) {
                                    orgFilePath = getFilePathFromURI(mContext, imageUri, extension);
                                }
                            } else {
                                Toast.makeText(mContext, "File type not supported", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            imagePathList.add(orgFilePath);
                        }
                        DetailFileImageAdapter mDetailFileAdapter = new DetailFileImageAdapter(imagePathList, mContext);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
                        recycle_image_attachment.setLayoutManager(mLayoutManager);
                        recycle_image_attachment.setAdapter(mDetailFileAdapter);
                        mDetailFileAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else if (requestCode == CAMERA) {
            if (data != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                //      imageview.setImageBitmap(bitmap);
                path = saveImage(bitmap);
                // imagePathList.add(bitmap.toString());
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//            String paths = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), bitmap, "Pic from camera", null);
                if (path != null) {
                    mImageUri = Uri.parse(path);
                    imagePathList.add(mImageUri.toString());
                }
                try {
                    bytes.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                DetailFileImageAdapter mDetailFileAdapter = new DetailFileImageAdapter(imagePathList, mContext);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
                recycle_image_attachment.setLayoutManager(mLayoutManager);
                recycle_image_attachment.setAdapter(mDetailFileAdapter);
                mDetailFileAdapter.notifyDataSetChanged();
            }
        }
    }

    private void showAlert(String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(msg);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }


    @Override
    public void getMessage(String message) {

        if (message != null) {
            userMessage = message;
        }

    }
}