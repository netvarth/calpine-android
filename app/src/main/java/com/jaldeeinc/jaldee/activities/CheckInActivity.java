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
import android.os.Handler;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
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
import com.jaldeeinc.jaldee.Interface.IFamillyListSelected;
import com.jaldeeinc.jaldee.Interface.IFamilyMemberDetails;
import com.jaldeeinc.jaldee.Interface.IMailSubmit;
import com.jaldeeinc.jaldee.Interface.IMobileSubmit;
import com.jaldeeinc.jaldee.Interface.IPaymentResponse;
import com.jaldeeinc.jaldee.Interface.ISelectQ;
import com.jaldeeinc.jaldee.Interface.ISendMessage;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.CouponlistAdapter;
import com.jaldeeinc.jaldee.adapter.DetailFileImageAdapter;
import com.jaldeeinc.jaldee.adapter.MultipleFamilyMemberAdapter;
import com.jaldeeinc.jaldee.adapter.QueuesAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.AddNotes;
import com.jaldeeinc.jaldee.custom.CheckInSlotsDialog;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.CustomToolTip;
import com.jaldeeinc.jaldee.custom.EmailEditWindow;
import com.jaldeeinc.jaldee.custom.FamilyMemberDialog;
import com.jaldeeinc.jaldee.custom.MobileNumberDialog;
import com.jaldeeinc.jaldee.custom.MyLeadingMarginSpan2;
import com.jaldeeinc.jaldee.model.FamilyArrayModel;
import com.jaldeeinc.jaldee.model.RazorpayModel;
import com.jaldeeinc.jaldee.payment.PaymentGateway;
import com.jaldeeinc.jaldee.payment.PaytmPayment;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.AvailableSlotsData;
import com.jaldeeinc.jaldee.response.CoupnResponse;
import com.jaldeeinc.jaldee.response.PaymentModel;
import com.jaldeeinc.jaldee.response.ProfileModel;
import com.jaldeeinc.jaldee.response.QueueTimeSlotModel;
import com.jaldeeinc.jaldee.response.SearchService;
import com.jaldeeinc.jaldee.response.SearchSetting;
import com.jaldeeinc.jaldee.response.SearchTerminology;
import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.jaldeeinc.jaldee.response.SectorCheckin;
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

public class CheckInActivity extends AppCompatActivity implements ISelectQ, PaymentResultWithDataListener, IPaymentResponse, IMobileSubmit, IMailSubmit, ISendMessage, IFamilyMemberDetails, IFamillyListSelected {

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

    @BindView(R.id.iv_teleService)
    ImageView ivteleService;

    @BindView(R.id.et_code)
    EditText etCode;

    @BindView(R.id.tv_apply)
    CustomTextViewBold tvApply;

    @BindView(R.id.tv_buttonName)
    CustomTextViewBold tvButtonName;

    @BindView(R.id.txtprepay)
    CustomTextViewMedium txtprepay;

    @BindView(R.id.ll_preinfo)
    LinearLayout llPreInfo;

    @BindView(R.id.tv_preInfoTitle)
    CustomTextViewSemiBold tvPreInfoTitle;

    @BindView(R.id.tv_preInfo)
    CustomTextViewMedium tvPreInfo;

    @BindView(R.id.tv_term)
    CustomTextViewMedium tvTerm;

    static CustomTextViewMedium txtprepayamount;

    static LinearLayout LservicePrepay;

    @BindView(R.id.cv_back)
    CardView cvBack;
    CustomTextViewSemiBold tvErrorMessage;
    static Activity mActivity;
    static Context mContext;
    String mFirstName, mLastName;
    String sector, subsector;
    int consumerID;
    private int uniqueId;
    private String providerName;
    private int providerId;
    private int locationId;
    private int serviceId;
    private String serviceName;
    private String phoneNumber;
    private String serviceDescription;
    private static SearchService checkInInfo = new SearchService();
    SearchTerminology mSearchTerminology;
    ProfileModel profileDetails;
    ArrayList<SlotsData> slotsData = new ArrayList<SlotsData>();
    ArrayList<AvailableSlotsData> activeSlotsList = new ArrayList<>();
    private CheckInSlotsDialog slotsDialog;
    private ISelectQ iSelectQ;
    String uuid;
    String value = null;
    String couponEntered;
    private int queueId;
    SearchViewDetail mBusinessDataList;
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
    ActiveCheckIn activeAppointment = new ActiveCheckIn();
    String apiDate = "";
    private int userId;
    private boolean isUser;
    QueueTimeSlotModel queueDetails = new QueueTimeSlotModel();
    int maxPartysize;
    private boolean isToken = false;
    BottomSheetDialog dialogPayment;
    static ArrayList<FamilyArrayModel> MultiplefamilyList = new ArrayList<>();
    ArrayList<PaymentModel> mPaymentData = new ArrayList<>();
    static String totalAmountPay;
    static ArrayList<QueueTimeSlotModel> mQueueTimeSlotList = new ArrayList<>();

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
    String checkEncId;
    private FamilyMemberDialog familyMemberDialog;
    private IFamilyMemberDetails iFamilyMemberDetails;
    String emailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        ButterKnife.bind(CheckInActivity.this);
        mActivity = this;
        mContext = this;
        iSelectQ = this;
        iMailSubmit = this;
        iMobileSubmit = this;
        paymentResponse = this;
        iSendMessage = this;
        iFamilyMemberDetails = this;

        // getting necessary details from intent
        Intent intent = getIntent();
        uniqueId = intent.getIntExtra("uniqueID", 0);
        providerName = intent.getStringExtra("providerName");
        checkInInfo = (SearchService) intent.getSerializableExtra("checkInInfo");
        locationId = intent.getIntExtra("locationId", 0);
        providerId = intent.getIntExtra("providerId", 0);
        userId = intent.getIntExtra("userId", 0);
        isUser = intent.getBooleanExtra("fromUser", false);
        tvConsumerName = findViewById(R.id.tv_consumerName);
        txtprepayamount = findViewById(R.id.txtprepayamount);
        LservicePrepay = findViewById(R.id.LservicePrepay);
        list = findViewById(R.id.list);
        recycle_family = findViewById(R.id.recycle_family);

        MultiplefamilyList.clear();

        if (providerName != null) {
            tvProviderName.setText(providerName);
        }

        if (checkInInfo != null) {
            String name = checkInInfo.getName();
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
            tvServiceName.setText(name);
            tvDescription.setText(checkInInfo.getDescription());
            llCheckIn.setVisibility(View.VISIBLE);
            llAppointment.setVisibility(View.GONE);
            if (checkInInfo.getCheckInServiceAvailability() != null) {
                if (checkInInfo.getCheckInServiceAvailability().getCalculationMode() != null && !checkInInfo.getCheckInServiceAvailability().getCalculationMode().equalsIgnoreCase("NoCalc")) {
                    String time = getWaitingTime(checkInInfo.getCheckInServiceAvailability().getAvailableDate(), checkInInfo.getCheckInServiceAvailability().getServiceTime(), checkInInfo.getCheckInServiceAvailability().getQueueWaitingTime());
                    tvCheckInDate.setVisibility(View.VISIBLE);
                    tvCheckInDate.setTextSize(30);
                    tvHint.setVisibility(View.VISIBLE);
                    tvCheckInDate.setText(time.split("-")[1]);
                    tvHint.setText(time.split("-")[0]);

                } else {
                    tvHint.setVisibility(View.GONE); // else condition to show Queue time and date if calculation mode is NoCalc
                    if (isUser) {
                        ApiQueueTimeSlot(locationId, checkInInfo.getId(), userId, checkInInfo.getCheckInServiceAvailability().getAvailableDate());
                    }else {
                        ApiQueueTimeSlot(locationId, checkInInfo.getId(), providerId, checkInInfo.getCheckInServiceAvailability().getAvailableDate());
                    }
                }

                if (checkInInfo.getCheckInServiceAvailability().getPersonAhead() >= 0) {

                    String changedtext = "People waiting in line : " + "<b>" + checkInInfo.getCheckInServiceAvailability().getPersonAhead() + "</b> ";
                    tvPeopleInLine.setText(Html.fromHtml(changedtext));
                }

                if (checkInInfo.getCheckInServiceAvailability().getAvailableDate() != null) {
                    apiDate = checkInInfo.getCheckInServiceAvailability().getAvailableDate();
                    queueId = checkInInfo.getCheckInServiceAvailability().getId();
//                    if (checkInInfo.getCheckInServiceAvailability().getServiceTime() != null) {
//                        tvTime.setText(checkInInfo.getCheckInServiceAvailability().getServiceTime());
//                        slotTime = checkInInfo.getCheckInServiceAvailability().getServiceTime();
//                    }
                }
            }


            if (checkInInfo.getServiceType() != null && checkInInfo.getServiceType().equalsIgnoreCase("virtualService")) {

                if (checkInInfo.getVirtualCallingModes().get(0).getCallingMode() != null) {

                    if (checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp") || checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Phone")) {

                        llVirtualNumber.setVisibility(View.VISIBLE);
                    } else {
                        llVirtualNumber.setVisibility(View.GONE);
                    }

                    ivteleService.setVisibility(View.VISIBLE);
                    if (checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {

                        ivteleService.setImageResource(R.drawable.zoom);

                    } else if (checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {

                        ivteleService.setImageResource(R.drawable.googlemeet);

                    } else if (checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {

                        ivteleService.setImageResource(R.drawable.whatsapp_icon);

                    } else if (checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Phone")) {

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

            if (checkInInfo.isPrePayment()) {

                if (isUser) {
                    APIPayment(String.valueOf(userId));
                } else {
                    APIPayment(String.valueOf(providerId));

                }
            } else {

                LservicePrepay.setVisibility(View.GONE);
            }

            if (checkInInfo.isPreInfoEnabled()) { //  check if pre-info is available for the service
                llPreInfo.setVisibility(View.VISIBLE);
                if (checkInInfo.getPreInfoTitle() != null) {
                    tvPreInfoTitle.setText(checkInInfo.getPreInfoTitle());
                } else {
                    llPreInfo.setVisibility(View.GONE);
                }

                if (checkInInfo.getPreInfoText() != null) {
                    tvPreInfo.setText(Html.fromHtml(checkInInfo.getPreInfoText()));
                } else {
                    llPreInfo.setVisibility(View.GONE);
                }
            } else {

                llPreInfo.setVisibility(View.GONE);
            }
        }

        mFirstName = SharedPreference.getInstance(CheckInActivity.this).getStringValue("firstname", "");
        mLastName = SharedPreference.getInstance(CheckInActivity.this).getStringValue("lastname", "");
        consumerID = SharedPreference.getInstance(CheckInActivity.this).getIntValue("consumerId", 0);

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

//                String mailId = "";
//                if (tvEmail.getText().toString() != null) {
//                    mailId = tvEmail.getText().toString();
//                }
//                emailEditWindow = new EmailEditWindow(CheckInActivity.this, profileDetails, iMailSubmit, mailId);
//                emailEditWindow.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
//                emailEditWindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                emailEditWindow.show();
//                DisplayMetrics metrics = CheckInActivity.this.getResources().getDisplayMetrics();
//                int width = (int) (metrics.widthPixels * 1);
//                emailEditWindow.getWindow().setGravity(Gravity.CENTER);
//                emailEditWindow.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
           }
        });

        tvNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (tvNumber.getText().toString() != null) {
//                    mobileNumberDialog = new MobileNumberDialog(CheckInActivity.this, profileDetails, iMobileSubmit, tvNumber.getText().toString());
//                    mobileNumberDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
//                    mobileNumberDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                    mobileNumberDialog.show();
//                    DisplayMetrics metrics = CheckInActivity.this.getResources().getDisplayMetrics();
//                    int width = (int) (metrics.widthPixels * 1);
//                    mobileNumberDialog.getWindow().setGravity(Gravity.CENTER);
//                    mobileNumberDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
//                }
            }
        });

        llEditDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                familyMemberDialog = new FamilyMemberDialog(CheckInActivity.this,familyMEmID,tvEmail.getText().toString(),tvNumber.getText().toString(),checkInInfo.isPrePayment(),iFamilyMemberDetails,profileDetails,multiplemem,0);
                familyMemberDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                familyMemberDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                familyMemberDialog.show();
                DisplayMetrics metrics =CheckInActivity.this.getResources().getDisplayMetrics();
                int width = (int) (metrics.widthPixels * 1);
                familyMemberDialog.setCancelable(false);
                familyMemberDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

//                Intent familyIntent = new Intent(CheckInActivity.this, CheckinFamilyMember.class);
//                familyIntent.putExtra("firstname", mFirstName);
//                familyIntent.putExtra("lastname", mLastName);
//                familyIntent.putExtra("consumerID", consumerID);
//                familyIntent.putExtra("multiple", multiplemem);
//                familyIntent.putExtra("memberID", familyMEmID);
//                familyIntent.putExtra("update", 0);
//                startActivity(familyIntent);

            }
        });

        tvChangeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkInInfo.getCheckInServiceAvailability().getAvailableDate() != null) {
                    if (isUser) {
                        slotsDialog = new CheckInSlotsDialog(mContext, checkInInfo.getId(), locationId, iSelectQ, userId, apiDate);

                    } else {
                        slotsDialog = new CheckInSlotsDialog(mContext, checkInInfo.getId(), locationId, iSelectQ, providerId, apiDate);

                    }
                    slotsDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                    slotsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    slotsDialog.show();
                    DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                    int width = (int) (metrics.widthPixels * 1);
                    slotsDialog.setCancelable(false);
                    slotsDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                }

            }
        });

        cvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvNumber.length() < 10) {
                    Toast.makeText(CheckInActivity.this, "Mobile number should have 10 digits" + "", Toast.LENGTH_SHORT).show();
                } else {

                    if (checkInInfo.isPrePayment()) {  // check if selected service requires prepayment
                        if (tvEmail.getText().toString() != null && tvEmail.getText().length() > 0) { // if selected service requires prepayment..then Email is mandatory

                            if (checkInInfo.isConsumerNoteMandatory()) { // check if notes is mandatory for selected service

                                if (userMessage != null && !userMessage.trim().equalsIgnoreCase("")) {

                                    if (isUser) {
                                        ApiCheckin(userMessage, userId);
                                    } else {
                                        ApiCheckin(userMessage, providerId);
                                    }
                                } else {

                                    DynamicToast.make(CheckInActivity.this, checkInInfo.getConsumerNoteTitle(), AppCompatResources.getDrawable(
                                            CheckInActivity.this, R.drawable.ic_info_black),
                                            ContextCompat.getColor(CheckInActivity.this, R.color.white), ContextCompat.getColor(CheckInActivity.this, R.color.green), Toast.LENGTH_SHORT).show();
                                }

                            } else {

                                if (isUser) {
                                    ApiCheckin(userMessage, userId);
                                } else {
                                    ApiCheckin(userMessage, providerId);
                                }
                            }
                        } else {

                            DynamicToast.make(CheckInActivity.this, "Email id is mandatory", AppCompatResources.getDrawable(
                                    CheckInActivity.this, R.drawable.ic_info_black),
                                    ContextCompat.getColor(CheckInActivity.this, R.color.white), ContextCompat.getColor(CheckInActivity.this, R.color.green), Toast.LENGTH_SHORT).show();

                        }
                    } else {

                        if (checkInInfo.isConsumerNoteMandatory()) {

                            if (userMessage != null && !userMessage.trim().equalsIgnoreCase("")) {

                                if (isUser) {
                                    ApiCheckin(userMessage, userId);
                                } else {
                                    ApiCheckin(userMessage, providerId);
                                }
                            } else {

                                DynamicToast.make(CheckInActivity.this, checkInInfo.getConsumerNoteTitle(), AppCompatResources.getDrawable(
                                        CheckInActivity.this, R.drawable.ic_info_black),
                                        ContextCompat.getColor(CheckInActivity.this, R.color.white), ContextCompat.getColor(CheckInActivity.this, R.color.green), Toast.LENGTH_SHORT).show();

                            }

                        } else {

                            if (isUser) {
                                ApiCheckin(userMessage, userId);
                            } else {
                                ApiCheckin(userMessage, providerId);
                            }
                        }
                    }

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

                    Toast.makeText(CheckInActivity.this, "Coupon already added", Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(CheckInActivity.this, couponEntered + " " + "Added", Toast.LENGTH_SHORT).show();


                } else {
                    if (couponEntered.equals("")) {
                        Toast.makeText(CheckInActivity.this, "Enter a coupon", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CheckInActivity.this, "Coupon Invalid", Toast.LENGTH_SHORT).show();
                    }


                }
                Config.logV("couponArraylist--code-------------------------" + couponArraylist);
                list.setVisibility(View.VISIBLE);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CheckInActivity.this);
                list.setLayoutManager(mLayoutManager);
                mAdapter = new CouponlistAdapter(CheckInActivity.this, s3couponList, couponEntered, couponArraylist);
                list.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
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

                        dialog.dismiss();
                    }
                });
            }
        });

    }

    private void ApiSearchViewTerminology(int muniqueID) {


        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
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
                        Config.closeDialog(CheckInActivity.this, mDialog);
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
                    Config.closeDialog(CheckInActivity.this, mDialog);
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


    private void ApiGetProfileDetail() {

        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);

        final int consumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
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
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
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
                        if (response.body() != null) {
                            isToken = response.body().isShowTokenId();

                            if(!checkInInfo.isPrePayment()) {
                                if (isToken) {
                                    tvButtonName.setText("Confirm Token");
                                    tvTerm.setText("Token for");
                                } else {
                                    tvButtonName.setText("Confirm CheckIn");
                                    tvTerm.setText("CheckIn for");
                                }
                            }
                            else{
                                if (isToken) {
                                    tvButtonName.setText("Proceed to Payment");
                                    tvTerm.setText("Token for");
                                } else {
                                    tvButtonName.setText("Proceed to Payment");
                                    tvTerm.setText("CheckIn for");
                                }
                            }
                            if (response.body().getCalculationMode() != null) {
                                calcMode = response.body().getCalculationMode();
                            }
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

    private void ApiJaldeegetS3Coupons(int uniqueID) {

        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);

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
                            String firstWord = "";
                            String secondWord;
                            if (MultiplefamilyList.size() > 1) {
                                secondWord = "₹ " + Config.getAmountinTwoDecimalPoints(Double.parseDouble(totalAmountPay));
                            } else {
                                secondWord = "₹ " + Config.getAmountinTwoDecimalPoints(Double.parseDouble(checkInInfo.getMinPrePaymentAmount()));
                            }
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

    private void ApiCheckin(final String txt_addnote, int id) {

        phoneNumber = etVirtualNumber.getText().toString();
        uuid = UUID.randomUUID().toString();


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        JSONObject qjsonObj = new JSONObject();
        JSONObject queueobj = new JSONObject();
        JSONObject waitobj = new JSONObject();
        JSONObject service = new JSONObject();
        JSONArray waitlistArray = new JSONArray();
        JSONObject virtualService = new JSONObject();
        JSONObject pjsonobj = new JSONObject();
        try {

            qjsonObj.put("id", queueId);
            queueobj.put("date", apiDate);
            queueobj.put("consumerNote", txt_addnote);
            queueobj.put("waitlistPhonenumber", phoneNumber);
            if (isUser) {
                pjsonobj.put("id", providerId);
            }

            if (imagePathList != null && imagePathList.size() > 0) {

                if (userMessage != null && userMessage.trim().equalsIgnoreCase("")) {

                    mDialog.dismiss();
                    showToolTip();
                    return;
                }
            }

            if (etVirtualNumber.getText().toString().trim().length() > 9) {
                if (checkInInfo.getVirtualCallingModes() != null && checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("whatsApp")) {
                    virtualService.put("WhatsApp", etVirtualNumber.getText());
                } else if (checkInInfo.getVirtualCallingModes() != null &&  checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                    virtualService.put("GoogleMeet", checkInInfo.getVirtualCallingModes().get(0).getValue());
                } else if (checkInInfo.getVirtualCallingModes() != null && checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                    virtualService.put("Zoom", checkInInfo.getVirtualCallingModes().get(0).getValue());
                } else if (checkInInfo.getVirtualCallingModes()!= null &&  checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Phone")) {
                    virtualService.put("Phone", etVirtualNumber.getText());
                }
            } else {
                DynamicToast.make(CheckInActivity.this, "Invalid phone number", AppCompatResources.getDrawable(
                        CheckInActivity.this, R.drawable.ic_info_black),
                        ContextCompat.getColor(CheckInActivity.this, R.color.white), ContextCompat.getColor(CheckInActivity.this, R.color.green), Toast.LENGTH_SHORT).show();
                return;
            }

            JSONArray couponList = new JSONArray();

            for (int i = 0; i < couponArraylist.size(); i++) {

                couponList.put(couponArraylist.get(i));

            }

            Log.i("kooooooo", couponList.toString());

            queueobj.put("coupons", couponList);


            Log.i("couponList", couponList.toString());
            service.put("id", checkInInfo.getId());
            if (familyMEmID == 0) {
                familyMEmID = consumerID;
            }

            if (MultiplefamilyList.size() > 0) {
                for (int i = 0; i < MultiplefamilyList.size(); i++) {
                    JSONObject waitobj1 = new JSONObject();
                    if(familyMEmID==MultiplefamilyList.get(i).getId()){
                        waitobj1.put("id",0);
                    }
                    else {
                        waitobj1.put("id", MultiplefamilyList.get(i).getId());
                    }
                    waitobj1.put("firstName", MultiplefamilyList.get(i).getFirstName());
                    waitobj1.put("lastName", MultiplefamilyList.get(i).getLastName());
                    waitlistArray.put(waitobj1);
                }
            } else {
                if (familyMEmID == consumerID) {
                    familyMEmID = 0;
                }
                waitobj.put("id", familyMEmID);
                waitlistArray.put(waitobj);
            }

            queueobj.putOpt("service", service);
            queueobj.putOpt("queue", qjsonObj);
            queueobj.putOpt("waitlistingFor", waitlistArray);

            if (isUser) {
                queueobj.putOpt("provider", pjsonobj);
            }

            if (checkInInfo.getServiceType() != null && checkInInfo.getServiceType().equalsIgnoreCase("virtualService")) {
                queueobj.putOpt("virtualService", virtualService);
            }

        } catch (
                JSONException e) {
            e.printStackTrace();
        }

        Log.i("QueueObj Checkin", queueobj.toString());
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queueobj.toString());
        Call<ResponseBody> call = apiService.Checkin(String.valueOf(id), body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    Config.logV("Response--code-------------------------" + response.body());
                    if (response.code() == 200) {
                        if (!checkInInfo.isPrePayment()) {
                            MultiplefamilyList.clear();
                        }
                        SharedPreference.getInstance(mContext).setValue("refreshcheckin", "true");
                        JSONObject reader = new JSONObject(response.body().string());
                        Iterator iteratorObj = reader.keys();
                        while (iteratorObj.hasNext()) {
                            String getJsonObj = (String) iteratorObj.next();
                            System.out.println("KEY: " + "------>" + getJsonObj);
                            value = reader.getString(getJsonObj);
                            break;

                        }

                        if (isUser) {
                            getConfirmationId(userId, txt_addnote, id);
                        } else {
                            getConfirmationId(providerId, txt_addnote, id);
                        }

                        System.out.println("VALUE: " + "------>" + value);

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


                            Toast.makeText(mContext, sb.toString(), Toast.LENGTH_LONG).show();
                        } else {
                            String responseerror = response.errorBody().string();
                            Config.logV("Response--error-------------------------" + responseerror);
                            if (response.code() != 419)
                                Toast.makeText(mContext, responseerror, Toast.LENGTH_LONG).show();
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

    private void ApiCommunicateCheckin(String waitListId, String accountID, String message, final BottomSheetDialog dialog) {
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
            } else {
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
        Call<ResponseBody> call = apiService.WaitListMessage(waitListId, String.valueOf(accountID.split("-")[0]), requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        Toast.makeText(mContext, "Message sent successfully", Toast.LENGTH_LONG).show();
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


    private void getConfirmationDetails(int id) {

        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<ActiveCheckIn> call = apiService.getActiveCheckInUUID(value, String.valueOf(id));
        call.enqueue(new Callback<ActiveCheckIn>() {
            @Override
            public void onResponse(Call<ActiveCheckIn> call, Response<ActiveCheckIn> response) {
                try {
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        activeAppointment = response.body();

                        if (activeAppointment != null) {
                            checkEncId = activeAppointment.getCheckinEncId();

                            Intent checkin = new Intent(CheckInActivity.this, CheckInConfirmation.class);
                            checkin.putExtra("BookingDetails", activeAppointment);
                            checkin.putExtra("terminology", mSearchTerminology.getProvider());
                            startActivity(checkin);
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

    private void ApiQueueTimeSlot(int locationId, int subSeriveID, int accountID, String mDate) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ArrayList<QueueTimeSlotModel>> call = apiService.getQueueTimeSlot(String.valueOf(locationId), String.valueOf(subSeriveID), mDate, String.valueOf(accountID));
        call.enqueue(new Callback<ArrayList<QueueTimeSlotModel>>() {
            @Override
            public void onResponse(Call<ArrayList<QueueTimeSlotModel>> call, Response<ArrayList<QueueTimeSlotModel>> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(CheckInActivity.this, mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    Config.logV("mQueueTimeSlotList--------11111-----------------" + response.code());
                    if (response.code() == 200) {
                        mQueueTimeSlotList = response.body();

                        if (mQueueTimeSlotList != null) {

                            if (mQueueTimeSlotList.size() > 0) {

                                String startDate = convertDate(mQueueTimeSlotList.get(0).getEffectiveSchedule().getStartDate());
                                String queueTime = mQueueTimeSlotList.get(0).getQueueSchedule().getTimeSlots().get(0).getsTime() + "-" + mQueueTimeSlotList.get(0).getQueueSchedule().getTimeSlots().get(0).geteTime();
                                tvCheckInDate.setVisibility(View.VISIBLE);
                                tvCheckInDate.setTextSize(20);
                                tvCheckInDate.setText(startDate+","+"\n"+ queueTime);

                            } else {

                            }
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<QueueTimeSlotModel>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(CheckInActivity.this, mDialog);
            }
        });
    }


    private void getConfirmationId(int userId, String txt_addnote, int id) {

        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<ActiveCheckIn> call = apiService.getActiveCheckInUUID(value, String.valueOf(id));
        call.enqueue(new Callback<ActiveCheckIn>() {
            @Override
            public void onResponse(Call<ActiveCheckIn> call, Response<ActiveCheckIn> response) {
                try {
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        activeAppointment = response.body();

                        if (activeAppointment != null) {
                            checkEncId = activeAppointment.getCheckinEncId();
                            dialogPayment = new BottomSheetDialog(mContext);

                            if (checkInInfo.isPrePayment()) {
                                if (!showPaytmWallet && !showPayU) {

                                    //Toast.makeText(mContext,"Pay amount by Cash",Toast.LENGTH_LONG).show();
                                } else {

                                    try {

                                        dialogPayment.setContentView(R.layout.prepayment);
                                        dialogPayment.show();


                                        Button btn_paytm = (Button) dialogPayment.findViewById(R.id.btn_paytm);
                                        Button btn_payu = (Button) dialogPayment.findViewById(R.id.btn_payu);
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
                                        final EditText edt_message = (EditText) dialogPayment.findViewById(R.id.edt_message);
                                        TextView txtamt = (TextView) dialogPayment.findViewById(R.id.txtamount);

                                        TextView txtprepayment = (TextView) dialogPayment.findViewById(R.id.txtprepayment);

                                        txtprepayment.setText("Prepayment Amount ");

                                        if (MultiplefamilyList.size() > 1) {
                                            txtamt.setText("Rs." + Config.getAmountinTwoDecimalPoints((Double.parseDouble(totalAmountPay))));
                                        } else {
                                            txtamt.setText("Rs." + Config.getAmountinTwoDecimalPoints((Double.parseDouble(checkInInfo.getMinPrePaymentAmount()))));
                                        }


                                        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                                "fonts/JosefinSans-SemiBold.ttf");
                                        txtamt.setTypeface(tyface1);
                                        btn_payu.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (MultiplefamilyList.size() > 1) {
                                                    new PaymentGateway(mContext, mActivity).ApiGenerateHash1(value, totalAmountPay, String.valueOf(id), Constants.PURPOSE_PREPAYMENT, "checkin", familyMEmID, Constants.SOURCE_PAYMENT);
                                                } else {
                                                    new PaymentGateway(mContext, mActivity).ApiGenerateHash1(value, checkInInfo.getMinPrePaymentAmount(), String.valueOf(id), Constants.PURPOSE_PREPAYMENT, "checkin", familyMEmID, Constants.SOURCE_PAYMENT);
                                                }
                                                dialogPayment.dismiss();
                                            }
                                        });

                                        btn_paytm.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                PaytmPayment payment = new PaytmPayment(mContext, paymentResponse);
                                                if (MultiplefamilyList.size() > 0) {
                                                    payment.ApiGenerateHashPaytm(value, totalAmountPay, String.valueOf(id), Constants.PURPOSE_PREPAYMENT, mContext, mActivity, "", familyMEmID, checkEncId);
                                                } else {
                                                    payment.ApiGenerateHashPaytm(value, checkInInfo.getMinPrePaymentAmount(), String.valueOf(id), Constants.PURPOSE_PREPAYMENT, mContext, mActivity, "", familyMEmID, checkEncId);
                                                }
                                                dialogPayment.dismiss();

                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }


                            } else {

                                if (isUser) {
                                    if (imagePathList.size() > 0) {
                                        ApiCommunicateCheckin(value, String.valueOf(userId), txt_addnote, dialog);
                                    }
                                    if (!checkInInfo.isLivetrack()) {
                                        getConfirmationDetails(userId);
                                    }

                                } else {
                                    if (imagePathList.size() > 0) {
                                        ApiCommunicateCheckin(value, String.valueOf(providerId), txt_addnote, dialog);
                                    }
                                    if (!checkInInfo.isLivetrack()) {
                                        getConfirmationDetails(providerId);
                                    }

                                }
                            }

                            if (checkInInfo.isLivetrack()) {
                                Intent checkinShareLocations = new Intent(mContext, CheckinShareLocation.class);
                                checkinShareLocations.putExtra("waitlistPhonenumber", phoneNumber);
                                checkinShareLocations.putExtra("uuid", value);
                                if (isUser) {
                                    checkinShareLocations.putExtra("accountID", String.valueOf(userId));
                                } else {
                                    checkinShareLocations.putExtra("accountID", String.valueOf(providerId));
                                }
                                checkinShareLocations.putExtra("title", providerName);
                                checkinShareLocations.putExtra("terminology", mSearchTerminology.getWaitlist());
                                checkinShareLocations.putExtra("calcMode", calcMode);
                                checkinShareLocations.putExtra("queueStartTime", "");
                                checkinShareLocations.putExtra("queueEndTime", "");
                                checkinShareLocations.putExtra("from", "checkin");
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
            public void onFailure(Call<ActiveCheckIn> call, Throwable t) {
            }
        });

    }


    public static void refreshMultipleMEmList(ArrayList<FamilyArrayModel> familyList) {
        MultiplefamilyList.clear();
        MultiplefamilyList.addAll(familyList);
        recycle_family.setVisibility(View.VISIBLE);
        if (checkInInfo.isPrePayment()) {
            totalAmountPay = String.valueOf(Double.parseDouble(checkInInfo.getMinPrePaymentAmount()) * MultiplefamilyList.size());
            LservicePrepay.setVisibility(View.VISIBLE);
//        Typeface tyface = Typeface.createFromAsset(getAssets(),
//                "fonts/Montserrat_Bold.otf");
//        txtprepay.setTypeface(tyface);
//        txtprepayamount.setTypeface(tyface);
            String firstWord = "";
            String secondWord = "₹ " + Config.getAmountinTwoDecimalPoints(Double.parseDouble(totalAmountPay));
            Spannable spannable = new SpannableString(firstWord + secondWord);
            spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.colorAccent)),
                    firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            txtprepayamount.setText(spannable);
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recycle_family.setLayoutManager(mLayoutManager);
        MultipleFamilyMemberAdapter mFamilyAdpater = new MultipleFamilyMemberAdapter(familyList, mContext, mActivity);
        recycle_family.setAdapter(mFamilyAdpater);
        mFamilyAdpater.notifyDataSetChanged();
        tvConsumerName.setVisibility(View.GONE);
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
                secondWord = monthString + " " + day + ", " + nextAvailableTime;
            } else {
                secondWord = "Today, " + nextAvailableTime;
            }
        } else {
            firstWord = "Estimated wait time";
            secondWord = Config.getTimeinHourMinutes(Integer.parseInt(estTime));
        }

        return firstWord + "-" + secondWord;
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

    public static void refreshName(String name, int memID) {
        Config.logV("NAme----------" + name);
        if (name != null && !name.equalsIgnoreCase("")) {
            tvConsumerName.setText(name);
            familyMEmID = memID;
        }
    }

    @Override
    public void sendSelectedQueueInfo(String displayTime, int id, QueueTimeSlotModel queueDetails, String selectedDate) {

        try {
            if (checkInInfo.getCheckInServiceAvailability().getCalculationMode() != null && !checkInInfo.getCheckInServiceAvailability().getCalculationMode().equalsIgnoreCase("NoCalc")) {

                String time = getWaitingTime(selectedDate, queueDetails.getServiceTime(), String.valueOf(queueDetails.getQueueWaitingTime()));
                tvCheckInDate.setVisibility(View.VISIBLE);
                tvCheckInDate.setTextSize(30);
                tvHint.setVisibility(View.VISIBLE);
                tvCheckInDate.setText(time.split("-")[1]);
                tvHint.setText(time.split("-")[0]);
            } else {
                tvCheckInDate.setVisibility(View.VISIBLE); // else condition to show Queue time and date if calculation mode is NoCalc
                tvHint.setVisibility(View.GONE);
                String startDate = convertDate(queueDetails.getEffectiveSchedule().getStartDate());
                String queueTime = queueDetails.getQueueSchedule().getTimeSlots().get(0).getsTime() + "-" + queueDetails.getQueueSchedule().getTimeSlots().get(0).geteTime();
                tvCheckInDate.setTextSize(20);
                tvCheckInDate.setText(startDate+","+"\n"+ queueTime);
            }
            queueId = id;
            apiDate = selectedDate;
            if (queueDetails.getQueueSize() >= 0) {

                String changedtext = "People waiting in line : " + "<b>" + queueDetails.getQueueSize() + "</b> ";
                tvPeopleInLine.setText(Html.fromHtml(changedtext));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void paymentFinished(RazorpayModel razorpayModel) {

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something here
                if (isUser) {
                    if (imagePathList.size() > 0) {
                        ApiCommunicateCheckin(value, String.valueOf(userId), userMessage, dialog);
                    }
                    getConfirmationDetails(userId);

                } else {
                    if (imagePathList.size() > 0) {
                        ApiCommunicateCheckin(value, String.valueOf(providerId), userMessage, dialog);
                    }
                    getConfirmationDetails(providerId);

                }
            }
        }, 1000);
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {

        try {
            RazorpayModel razorpayModel = new RazorpayModel(paymentData);
            new PaymentGateway(mContext, mActivity).sendPaymentStatus(razorpayModel, "SUCCESS");
            Toast.makeText(mContext, "Payment Successful", Toast.LENGTH_LONG).show();
            paymentFinished(razorpayModel);
        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {

        try {
            AlertDialog alertDialog = new AlertDialog.Builder(CheckInActivity.this).create();
            alertDialog.setTitle("Payment Failed");
            alertDialog.setMessage("Unable to process your request.Please try again after some time");
            alertDialog.setCancelable(false);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            Intent homeIntent = new Intent(CheckInActivity.this, Home.class);
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

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something here
                if (isUser) {
                    if (imagePathList.size() > 0) {
                        ApiCommunicateCheckin(value, String.valueOf(userId), userMessage, dialog);
                    }
                    getConfirmationDetails(userId);

                } else {
                    if (imagePathList.size() > 0) {
                        ApiCommunicateCheckin(value, String.valueOf(providerId), userMessage, dialog);
                    }
                    getConfirmationDetails(providerId);

                }
            }
        }, 1000);

    }

    @Override
    public void mailUpdated() {
        String mail = SharedPreference.getInstance(mContext).getStringValue("email", "");
        tvEmail.setText(mail);
    }

    @Override
    public void mobileUpdated() {
        String phone = SharedPreference.getInstance(mContext).getStringValue("mobile", "");
        tvNumber.setText(phone);
    }

    @Override
    public void getMessage(String message) {

        if (message != null) {
            userMessage = message;
        }

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
//                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
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

                    } else if (data.getClipData() != null) {
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

    private void showToolTip() {

        CustomToolTip tipWindow = new CustomToolTip(CheckInActivity.this, CustomToolTip.DRAW_TOP, "Please add notes");
        tipWindow.showToolTip(cvAddNote, CustomToolTip.DRAW_ARROW_DEFAULT_CENTER, false);
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

    @Override
    public void sendFamilyMemberDetails(int consumerId, String firstName, String lastName, String phone, String email) {
        mFirstName = firstName;
        mLastName = lastName;
        phoneNumber = phone;
        familyMEmID = consumerId;
        emailId = email;
        tvNumber.setText(phoneNumber);
        if(!emailId.equalsIgnoreCase("")) {
            tvEmail.setText(emailId);
        }
        else{
            tvEmail.setText("");
        }
        tvConsumerName.setText(mFirstName);
    }

    @Override
    public void changeMemberName(String name, int id) {

    }

    @Override
    public void CheckedFamilyList(List<FamilyArrayModel> familyList) {
//        MultiplefamilyList.clear();
//        MultiplefamilyList.addAll(familyList);
//        recycle_family.setVisibility(View.VISIBLE);
//        if (checkInInfo.isPrePayment()) {
//            totalAmountPay = String.valueOf(Double.parseDouble(checkInInfo.getMinPrePaymentAmount()) * MultiplefamilyList.size());
//            LservicePrepay.setVisibility(View.VISIBLE);
////        Typeface tyface = Typeface.createFromAsset(getAssets(),
////                "fonts/Montserrat_Bold.otf");
////        txtprepay.setTypeface(tyface);
////        txtprepayamount.setTypeface(tyface);
//            String firstWord = "Prepayment Amount: ";
//            String secondWord = "₹ " + Config.getAmountinTwoDecimalPoints(Double.parseDouble(totalAmountPay));
//            Spannable spannable = new SpannableString(firstWord + secondWord);
//            spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.colorAccent)),
//                    firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            txtprepayamount.setText(spannable);
//        }
//
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
//        recycle_family.setLayoutManager(mLayoutManager);
//        MultipleFamilyMemberAdapter mFamilyAdpater = new MultipleFamilyMemberAdapter(MultiplefamilyList, mContext, mActivity);
//        recycle_family.setAdapter(mFamilyAdpater);
//        mFamilyAdpater.notifyDataSetChanged();
//        tvConsumerName.setVisibility(View.GONE);

    }
}