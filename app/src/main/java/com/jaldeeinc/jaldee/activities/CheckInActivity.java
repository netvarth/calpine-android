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
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hbb20.CountryCodePicker;
import com.jaldeeinc.jaldee.Interface.ICpn;
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
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.AddNotes;
import com.jaldeeinc.jaldee.custom.CheckInSlotsDialog;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.CustomToolTip;
import com.jaldeeinc.jaldee.custom.CustomerInformationDialog;
import com.jaldeeinc.jaldee.custom.EmailEditWindow;
import com.jaldeeinc.jaldee.custom.FamilyMemberDialog;
import com.jaldeeinc.jaldee.custom.MobileNumberDialog;
import com.jaldeeinc.jaldee.model.BookingModel;
import com.jaldeeinc.jaldee.model.FamilyArrayModel;
import com.jaldeeinc.jaldee.model.PincodeLocationsResponse;
import com.jaldeeinc.jaldee.model.RazorpayModel;
import com.jaldeeinc.jaldee.payment.PaymentGateway;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.AdvancePaymentDetails;
import com.jaldeeinc.jaldee.response.AvailableSlotsData;
import com.jaldeeinc.jaldee.response.CoupnResponse;
import com.jaldeeinc.jaldee.response.PaymentModel;
import com.jaldeeinc.jaldee.response.ProfileModel;
import com.jaldeeinc.jaldee.response.Provider;
import com.jaldeeinc.jaldee.response.ProviderCouponResponse;
import com.jaldeeinc.jaldee.response.Questionnaire;
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
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckInActivity extends AppCompatActivity implements ISelectQ, PaymentResultWithDataListener, IPaymentResponse, IMobileSubmit, IMailSubmit, ISendMessage, IFamilyMemberDetails, IFamillyListSelected, ICpn {

    @BindView(R.id.tv_providerName)
    CustomTextViewBold tvProviderName;

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

    @BindView(R.id.ll_preinfo)
    LinearLayout llPreInfo;

    @BindView(R.id.tv_preInfoTitle)
    CustomTextViewSemiBold tvPreInfoTitle;

    @BindView(R.id.tv_preInfo)
    CustomTextViewMedium tvPreInfo;

    @BindView(R.id.tv_term)
    CustomTextViewMedium tvTerm;

    @BindView(R.id.attach_file_size)
    CustomTextViewMedium tvAttachFileSize;

    @BindView(R.id.tv_addNote)
    CustomTextViewMedium tvAddNotes;

    @BindView(R.id.tv_vsHint)
    CustomTextViewMedium tvVsHint;

    @BindView(R.id.ll_coupons)
    LinearLayout llCoupons;

    @BindView(R.id.cv_back)
    CardView cvBack;

    static CustomTextViewBold tvConsumerName;
    CountryCodePicker virtual_NmbrCCPicker;
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
    private String phoneNumber, whtsappCountryCode, whatsappNumber;
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
    ArrayList<ProviderCouponResponse> providerCouponList = new ArrayList<>();
    AdvancePaymentDetails advancePaymentDetails = new AdvancePaymentDetails();

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
    static String totalServicePay;
    static ArrayList<QueueTimeSlotModel> mQueueTimeSlotList = new ArrayList<>();

    //files related
    private static final String IMAGE_DIRECTORY = "/Jaldee" + "";
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
    private CustomerInformationDialog customerInformationDialog;
    private IFamilyMemberDetails iFamilyMemberDetails;
    String emailId, prepayAmount = "", prePayRemainingAmount = "";
    private String countryCode, mWhtsappCountryCode, mWhatsappNumber, mTelegramCountryCode, mTelegramNumber, mAge;
    private JSONArray mPreferredLanguages = new JSONArray();
    private JSONObject mBookingLocation = new JSONObject();
    private Provider providerResponse = new Provider();
    private SearchSetting mSearchSettings;
    private String accountBusinessName;
    private String locationName;
    private ICpn iCpn;
    boolean virtualService;


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

        SharedPreference.getInstance(mContext).setValue(Constants.QUESTIONNAIRE, "");
        SharedPreference.getInstance(mContext).setValue(Constants.QIMAGES, "");

        // getting necessary details from intent
        Intent intent = getIntent();
        uniqueId = intent.getIntExtra("uniqueID", 0);
        providerName = intent.getStringExtra("providerName");
        accountBusinessName = intent.getStringExtra("accountBusinessName");
        locationName = intent.getStringExtra("locationName");
        checkInInfo = (SearchService) intent.getSerializableExtra("checkInInfo");
        locationId = intent.getIntExtra("locationId", 0);
        providerId = intent.getIntExtra("providerId", 0);
        userId = intent.getIntExtra("userId", 0);
        isUser = intent.getBooleanExtra("fromUser", false);
        sector = intent.getStringExtra("sector");
        tvConsumerName = findViewById(R.id.tv_consumerName);

        list = findViewById(R.id.list);
        recycle_family = findViewById(R.id.recycle_family);

        virtual_NmbrCCPicker = findViewById(R.id.virtual_NmbrCCPicker);
        MultiplefamilyList.clear();

        int consumerId = SharedPreference.getInstance(CheckInActivity.this).getIntValue("consumerId", 0);
        familyMEmID = consumerId;
        if (providerName != null) {
            tvProviderName.setText(providerName);
        }

        if (checkInInfo != null) {
            String name = checkInInfo.getName();
            tvServiceName.setText(name);
            tvDescription.setText(checkInInfo.getDescription());
            llCheckIn.setVisibility(View.VISIBLE);
            llAppointment.setVisibility(View.GONE);
            if (checkInInfo.getServiceType() != null && checkInInfo.getServiceType().equalsIgnoreCase("virtualService")) {
                virtualService = true;
            } else {
                virtualService = false;
            }

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
                    } else {
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

                        if (checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                            tvVsHint.setText("WhatsApp number");
                        } else {

                            tvVsHint.setText("Contact number");
                        }

                    } else {
                        llVirtualNumber.setVisibility(View.GONE);
                    }

                    ivteleService.setVisibility(View.VISIBLE);
                    if (checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {

                        ivteleService.setImageResource(R.drawable.zoom);

                    } else if (checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {

                        ivteleService.setImageResource(R.drawable.googlemeet);

                    } else if (checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                        if (checkInInfo.getVirtualServiceType() != null && checkInInfo.getVirtualServiceType().equalsIgnoreCase("videoService")) {
                            ivteleService.setImageResource(R.drawable.whatsapp_videoicon);
                        } else {
                            ivteleService.setImageResource(R.drawable.whatsapp_icon);
                        }

                    } else if (checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Phone")) {

                        ivteleService.setImageResource(R.drawable.phoneaudioicon);

                    } else if (checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("VideoCall")) {

                        ivteleService.setImageResource(R.drawable.ic_jaldeevideo);

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
                    getAdvancePaymentDetails(userMessage, userId);
                } else {
                    getAdvancePaymentDetails(userMessage, providerId);
                }
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


            if (checkInInfo.getConsumerNoteTitle() != null && !checkInInfo.getConsumerNoteTitle().equalsIgnoreCase("")) {
                tvAddNotes.setText(checkInInfo.getConsumerNoteTitle());
            } else {
                tvAddNotes.setText("Add Note");
            }
        }

        mFirstName = SharedPreference.getInstance(CheckInActivity.this).getStringValue("firstname", "");
        mLastName = SharedPreference.getInstance(CheckInActivity.this).getStringValue("lastname", "");
        consumerID = SharedPreference.getInstance(CheckInActivity.this).getIntValue("consumerId", 0);

        // api calls
        ApiGetProviderDetails(uniqueId);

        ApiGetProfileDetail();


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
                if (tvEmail.getText().toString().equalsIgnoreCase("")) {
                    familyMemberDialog = new FamilyMemberDialog(CheckInActivity.this, familyMEmID, tvEmail.getText().toString(), phoneNumber, checkInInfo.isPrePayment(), iFamilyMemberDetails, profileDetails, multiplemem, 0, countryCode, virtualService);
                    familyMemberDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                    familyMemberDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    familyMemberDialog.show();
                    DisplayMetrics metrics = CheckInActivity.this.getResources().getDisplayMetrics();
                    int width = (int) (metrics.widthPixels * 1);
                    familyMemberDialog.setCancelable(false);
                    familyMemberDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                }
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

                familyMemberDialog = new FamilyMemberDialog(CheckInActivity.this, familyMEmID, tvEmail.getText().toString(), phoneNumber, checkInInfo.isPrePayment(), iFamilyMemberDetails, profileDetails, multiplemem, 0, countryCode, virtualService);
                familyMemberDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                familyMemberDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                familyMemberDialog.show();
                DisplayMetrics metrics = CheckInActivity.this.getResources().getDisplayMetrics();
                int width = (int) (metrics.widthPixels * 1);
                familyMemberDialog.setCancelable(false);
                familyMemberDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
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
                for (int i = 0; i < providerCouponList.size(); i++) {
                    if (providerCouponList.get(i).getCouponCode().equals(couponEntered)) {
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
                cpns(couponArraylist);
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
                            tvAttachFileSize.setText("Attach File" + "(" + imagePathList.size() + ")");
                            dialog.dismiss();
                        } else {
                            tvAttachFileSize.setText("Attach File");
                            tvErrorMessage.setVisibility(View.VISIBLE);
                        }
                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (imagePathList.size() > 0) {
                            tvAttachFileSize.setText("Attach File" + "(" + imagePathList.size() + ")");
                        } else {
                            tvAttachFileSize.setText("Attach File");
                        }
                        dialog.dismiss();
                    }
                });
            }
        });

        llCoupons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent iCoupons = new Intent(CheckInActivity.this, CouponActivity.class);
                iCoupons.putExtra("uniqueID", String.valueOf(uniqueId));
                iCoupons.putExtra("accountId", String.valueOf(mBusinessDataList.getId()));
                startActivity(iCoupons);
            }
        });

    }


    private void ApiGetProviderDetails(int uniqueId) {

        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<Provider> call = apiService.getProviderDetails(uniqueId);
        call.enqueue(new Callback<Provider>() {
            @Override
            public void onResponse(Call<Provider> call, Response<Provider> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(CheckInActivity.this, mDialog);
                    if (response.code() == 200) {
                        providerResponse = response.body();
                        if (providerResponse != null) {

                            ApiSearchViewSetting(providerResponse.getSettings());

                            ApiSearchViewTerminology(providerResponse.getTerminologies());

                            apiSearchViewDetail(providerResponse.getBusinessProfile());

                            ApiJaldeegetS3Coupons(providerResponse.getCoupon());

                            ApiJaldeegetProviderCoupons(providerResponse.getProviderCoupon());

                        } else {

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Provider> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(CheckInActivity.this, mDialog);
            }
        });

    }


    private void ApiSearchViewTerminology(String termin) {

        try {

            if (termin != null) {
                mSearchTerminology = new Gson().fromJson(termin, SearchTerminology.class);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void apiSearchViewDetail(SearchViewDetail profile) {


        try {

            mBusinessDataList = profile;

            if (mBusinessDataList != null) {

                sector = mBusinessDataList.getServiceSector().getDomain();
                subsector = mBusinessDataList.getServiceSubSector().getSubDomain();
                APISector(sector, subsector);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

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
                        if (checkin_sector != null) {
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
                            countryCode = SharedPreference.getInstance(mContext).getStringValue("countryCode", "");
                            phoneNumber = profileDetails.getUserprofile().getPrimaryMobileNo();
                            tvNumber.setText(countryCode + " " + phoneNumber);
                            //  et_countryCode.setText(countryCode);
                            String cCode = countryCode.replace("+", "");
                            virtual_NmbrCCPicker.setCountryForPhoneCode(Integer.parseInt(cCode));
                            etVirtualNumber.setText(profileDetails.getUserprofile().getPrimaryMobileNo());


                            if (profileDetails.getUserprofile().getEmail() != null) {
                                emailId = profileDetails.getUserprofile().getEmail();
                                tvEmail.setText(profileDetails.getUserprofile().getEmail());
                            } else {
                                tvEmail.setHint("Enter your Mail Id");
                            }
                            if (checkInInfo.getServiceType() != null && checkInInfo.getServiceType().equalsIgnoreCase("virtualService")) {

                                customerInformationDialog = new CustomerInformationDialog(CheckInActivity.this, familyMEmID, tvEmail.getText().toString(), phoneNumber, checkInInfo.isPrePayment(), iFamilyMemberDetails, profileDetails, multiplemem, 0, countryCode, sector);
                                customerInformationDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                                customerInformationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                customerInformationDialog.show();
                                DisplayMetrics metrics = CheckInActivity.this.getResources().getDisplayMetrics();
                                int width = (int) (metrics.widthPixels * 1);
                                customerInformationDialog.setCancelable(false);
                                customerInformationDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                            }
                        }
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

    private void ApiSearchViewSetting(String setting) {

        try {
            if (setting != null) {
                mSearchSettings = new Gson().fromJson(setting, SearchSetting.class);
                isToken = mSearchSettings.isShowTokenId();

                if (!checkInInfo.isPrePayment()) {
                    if (isToken) {
                        tvTerm.setText("Token for");
                    } else {
                        tvTerm.setText("CheckIn for");
                    }
                } else {
                    if (isToken) {
                        tvTerm.setText("Token for");
                    } else {
                        tvTerm.setText("CheckIn for");
                    }
                }
                if (mSearchSettings.getCalculationMode() != null) {
                    calcMode = mSearchSettings.getCalculationMode();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ApiJaldeegetS3Coupons(String s3Coupons) {

        try {

            if (s3Coupons != null) {
                s3couponList.clear();
                s3couponList = new Gson().fromJson(s3Coupons, new TypeToken<ArrayList<CoupnResponse>>() {
                }.getType());
                if (s3couponList.size() != 0 || (providerCouponList != null && providerCouponList.size() != 0)) {
                    tvApplyCode.setVisibility(View.VISIBLE);
                    llCoupons.setVisibility(View.VISIBLE);
                } else {
                    tvApplyCode.setVisibility(View.GONE);
                    llCoupons.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ApiJaldeegetProviderCoupons(String providerCoupons) {

        try {

            if (providerCoupons != null) {
                providerCouponList.clear();
                providerCouponList = new Gson().fromJson(providerCoupons, new TypeToken<ArrayList<ProviderCouponResponse>>() {
                }.getType());

                if (s3couponList != null && s3couponList.size() != 0 || providerCouponList.size() != 0) {
                    tvApplyCode.setVisibility(View.VISIBLE);
                    llCoupons.setVisibility(View.VISIBLE);
                } else {
                    tvApplyCode.setVisibility(View.GONE);
                    llCoupons.setVisibility(View.GONE);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public AdvancePaymentDetails getAdvancePaymentDetails(final String txt_addnote, int id) {
        final ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        JSONObject qjsonObj = new JSONObject();
        JSONObject queueobj = new JSONObject();
        JSONObject waitobj = new JSONObject();
        JSONObject service = new JSONObject();
        JSONArray waitlistArray = new JSONArray();
        JSONObject consumerObj = new JSONObject();
        JSONObject providerObj = new JSONObject();

        try {
            consumerObj.put("id", consumerID);
            providerObj.put("id", providerId);

            qjsonObj.put("id", queueId);
            queueobj.put("date", apiDate);
            queueobj.put("consumerNote", txt_addnote);
            queueobj.put("waitlistPhoneNumber", phoneNumber);
            queueobj.put("countryCode", countryCode);

            JSONArray couponList = new JSONArray();

            for (int i = 0; i < couponArraylist.size(); i++) {

                couponList.put(couponArraylist.get(i));

            }
            queueobj.put("coupons", couponList);
            Log.i("couponList", couponList.toString());
            service.put("id", checkInInfo.getId());
            if (familyMEmID == 0) {
                familyMEmID = consumerID;
            }

            if (MultiplefamilyList.size() > 0) {
                for (int i = 0; i < MultiplefamilyList.size(); i++) {
                    JSONObject waitobj1 = new JSONObject();
                    if (familyMEmID == MultiplefamilyList.get(i).getId()) {
                        waitobj1.put("id", 0);
                    } else {
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
            queueobj.putOpt("consumer", consumerObj);
            queueobj.putOpt("provider", providerObj);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queueobj.toString());
        Call<AdvancePaymentDetails> call = apiService.getWlAdvancePaymentDetails(String.valueOf(id), requestBody);
        call.enqueue(new Callback<AdvancePaymentDetails>() {

            @Override
            public void onResponse(Call<AdvancePaymentDetails> call, Response<AdvancePaymentDetails> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        advancePaymentDetails = response.body();
                        if (couponEntered != null) {
                            Config.logV("couponArraylist--code-------------------------" + couponArraylist);
                            list.setVisibility(View.VISIBLE);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CheckInActivity.this);
                            list.setLayoutManager(mLayoutManager);
                            mAdapter = new CouponlistAdapter(CheckInActivity.this, s3couponList, couponEntered, couponArraylist, advancePaymentDetails, iCpn);
                            list.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<AdvancePaymentDetails> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });
        return advancePaymentDetails;
    }

    private void ApiCheckin(final String txt_addnote, int id) {

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        String number = etVirtualNumber.getText().toString();
        uuid = UUID.randomUUID().toString();
        //String virtual_code = virtual_NmbrCCPicker.getSelectedCountryCode();
        String countryVirtualCode = "";
        /*if (!virtual_code.equalsIgnoreCase("")) {
            countryVirtualCode = virtual_code.substring(1);*/
        if (virtual_NmbrCCPicker.getSelectedCountryCode() != null) {
            countryVirtualCode = virtual_NmbrCCPicker.getSelectedCountryCode();
        } else {
            DynamicToast.make(CheckInActivity.this, "Country code needed", AppCompatResources.getDrawable(
                    CheckInActivity.this, R.drawable.ic_info_black),
                    ContextCompat.getColor(CheckInActivity.this, R.color.white), ContextCompat.getColor(CheckInActivity.this, R.color.green), Toast.LENGTH_SHORT).show();
            mDialog.dismiss();
            return;
        }


        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);

        JSONObject qjsonObj = new JSONObject();
        JSONObject queueobj = new JSONObject();
        JSONObject waitobj = new JSONObject();
        JSONObject service = new JSONObject();
        JSONArray waitlistArray = new JSONArray();
        JSONObject virtualService = new JSONObject();
        JSONObject pjsonobj = new JSONObject();

        JSONObject jsonObj2 = new JSONObject();
        JSONObject jsonObj3 = new JSONObject();
        try {

            qjsonObj.put("id", queueId);
            queueobj.put("date", apiDate);
            queueobj.put("consumerNote", txt_addnote);
            queueobj.put("waitlistPhoneNumber", phoneNumber);
            queueobj.put("countryCode", countryCode);

            if (isUser) {
                pjsonobj.put("id", providerId);
            }

            if (etVirtualNumber.getText().toString().trim().length() > 7) {
                if (checkInInfo.getVirtualCallingModes() != null && checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("whatsApp")) {
                    virtualService.put("WhatsApp", countryVirtualCode + etVirtualNumber.getText());
                    mWhtsappCountryCode = countryVirtualCode;
                    mWhatsappNumber = etVirtualNumber.getText().toString();
                } else if (checkInInfo.getVirtualCallingModes() != null && checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                    virtualService.put("GoogleMeet", checkInInfo.getVirtualCallingModes().get(0).getValue());
                } else if (checkInInfo.getVirtualCallingModes() != null && checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                    virtualService.put("Zoom", checkInInfo.getVirtualCallingModes().get(0).getValue());
                } else if (checkInInfo.getVirtualCallingModes() != null && checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Phone")) {
                    virtualService.put("Phone", countryVirtualCode + etVirtualNumber.getText());
                    phoneNumber = etVirtualNumber.getText().toString();
                } else if (checkInInfo.getVirtualCallingModes() != null && checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("VideoCall")) {
                    virtualService.put("VideoCall", checkInInfo.getVirtualCallingModes().get(0).getValue());
                }
            } else {

                String modeOfCalling = "";
                if (checkInInfo.getVirtualCallingModes() != null && checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("whatsApp")) {
                    modeOfCalling = "Invalid WhatsApp number";
                } else {
                    modeOfCalling = "Invalid Contact number";
                }
                DynamicToast.make(CheckInActivity.this, modeOfCalling, AppCompatResources.getDrawable(
                        CheckInActivity.this, R.drawable.ic_info_black),
                        ContextCompat.getColor(CheckInActivity.this, R.color.white), ContextCompat.getColor(CheckInActivity.this, R.color.green), Toast.LENGTH_SHORT).show();
                mDialog.dismiss();
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
                    if (familyMEmID == MultiplefamilyList.get(i).getId()) {
                        waitobj1.put("id", 0);
                    } else {
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

                waitobj.put("firstName", mFirstName);
                waitobj.put("lastName", mLastName);
                if (mWhatsappNumber != null && !mWhatsappNumber.isEmpty()) {
                    jsonObj2.put("countryCode", mWhtsappCountryCode);
                    jsonObj2.put("number", mWhatsappNumber);
                    waitobj.putOpt("whatsAppNum", jsonObj2);
                }
                if (mTelegramNumber != null && !mTelegramNumber.isEmpty()) {
                    jsonObj3.put("countryCode", mTelegramCountryCode);
                    jsonObj3.put("number", mTelegramNumber);
                    waitobj.putOpt("telegramNum", jsonObj3);
                }
                if (mAge != null && !mAge.isEmpty()) {
                    waitobj.put("age", mAge);
                }
                if (mPreferredLanguages != null) {
                    waitobj.putOpt("preferredLanguage", mPreferredLanguages);
                }
                if (mBookingLocation != null) {
                    waitobj.putOpt("bookingLocation", mBookingLocation);
                }
                if (emailId != null && !emailId.equalsIgnoreCase("")) {
                    waitobj.put("email", emailId);
                }
                waitlistArray.put(waitobj);

                // et_countryCode.setText(countryCode);


            }

            queueobj.putOpt("service", service);
            queueobj.putOpt("queue", qjsonObj);
            queueobj.putOpt("waitlistingFor", waitlistArray);
            queueobj.putOpt("waitlistPhoneNumber", phoneNumber);
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

        if (mDialog.isShowing())
            Config.closeDialog(getParent(), mDialog);

        getQuestionnaire(checkInInfo.getId(), id, queueobj, txt_addnote);

    }

    private void getQuestionnaire(int serviceId, int accountId, JSONObject queueobj, String txt_addnote) {

        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<Questionnaire> call = apiService.getQuestions(serviceId, 0, accountId);
        call.enqueue(new Callback<Questionnaire>() {
            @Override
            public void onResponse(Call<Questionnaire> call, Response<Questionnaire> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        Questionnaire questionnaire = response.body();

                        BookingModel model = new BookingModel();
                        model.setJsonObject(queueobj.toString());
                        model.setImagesList(imagePathList);
                        model.setMessage(txt_addnote);
                        model.setAccountId(accountId);
                        model.setCheckInInfo(checkInInfo);
                        model.setmSearchTerminology(mSearchTerminology);
                        model.setFamilyEMIID(familyMEmID);
                        model.setPhoneNumber(phoneNumber);
                        model.setQuestionnaire(questionnaire);
                        model.setFrom(Constants.APPOINTMENT);
                        model.setProviderName(providerName);
                        model.setAccountBusinessName(accountBusinessName);
                        model.setLocationName(locationName);
                        model.setDate(tvCheckInDate.getText().toString());
                        model.setHint(tvHint.getText().toString());
                        model.setPeopleWaiting(tvPeopleInLine.getText().toString());
                        model.setCheckInOrToken(tvTerm.getText().toString());
                        model.setCustomerName(tvConsumerName.getText().toString());
                        model.setEmailId(tvEmail.getText().toString());
                        model.setCountryCode(countryCode);
                        model.setMultipleFamilyMembers(MultiplefamilyList);
                        model.setTotalAmount(totalAmountPay);
                        model.setTotalServicePay(totalServicePay);
                        // model.setJacshSelected(cbJCash.isChecked());
                        model.setAmountRequiredNow(advancePaymentDetails.getAmountRequiredNow());
                        if (advancePaymentDetails.getEligibleJcashAmt() != null) {
                            model.setEligibleJcashAmt(advancePaymentDetails.getEligibleJcashAmt().get("jCashAmt").getAsDouble());
                        }
                        if (questionnaire != null) {

                            if (questionnaire.getQuestionsList() != null) {

                                Intent intent = new Intent(CheckInActivity.this, CustomQuestionnaire.class);
                                intent.putExtra("data", model);
                                intent.putExtra("from", Constants.CHECKIN);
                                startActivity(intent);

                            } else {

                                Intent intent = new Intent(CheckInActivity.this, CheckInReconfirmation.class);
                                intent.putExtra("data", model);
                                startActivity(intent);
                            }
                        } else {

                            Intent intent = new Intent(CheckInActivity.this, CheckInReconfirmation.class);
                            intent.putExtra("data", model);
                            startActivity(intent);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Questionnaire> call, Throwable t) {
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
        Call<ResponseBody> call = apiService.waitlistSendAttachments(waitListId, Integer.parseInt(accountID.split("-")[0]), requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        //Toast.makeText(mContext, "Message sent successfully", Toast.LENGTH_LONG).show();///////
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
                            checkin.putExtra("waitlistPhonenumber", phoneNumber);
                            checkin.putExtra("livetrack", checkInInfo.isLivetrack());
                            if (isUser) {
                                checkin.putExtra("accountID", String.valueOf(userId));
                            } else {
                                checkin.putExtra("accountID", String.valueOf(providerId));
                            }
                            checkin.putExtra("confId", value);
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
                                tvCheckInDate.setText(startDate + "," + "\n" + queueTime);

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


    @Override
    public void refreshMultipleMEmList(ArrayList<FamilyArrayModel> familyList) {
        MultiplefamilyList.clear();
        MultiplefamilyList.addAll(familyList);
        recycle_family.setVisibility(View.VISIBLE);
        if (checkInInfo.getTotalAmount() != null && !checkInInfo.getTotalAmount().equalsIgnoreCase("0.0")) {
            totalServicePay = String.valueOf(Double.parseDouble(checkInInfo.getTotalAmount()) * MultiplefamilyList.size());

        }
        if (userMessage != null) {


            if (isUser) {
                getAdvancePaymentDetails(userMessage, userId);
            } else {
                getAdvancePaymentDetails(userMessage, providerId);
            }
        }
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recycle_family.setLayoutManager(mLayoutManager);
        MultipleFamilyMemberAdapter mFamilyAdpater = new MultipleFamilyMemberAdapter(familyList, mContext, mActivity);
        recycle_family.setAdapter(mFamilyAdpater);
        mFamilyAdpater.notifyDataSetChanged();
        tvConsumerName.setVisibility(View.GONE);
    }

    @Override
    public void sendFamilyMbrPhoneAndEMail(String phone, String email, String conCode) {
        phoneNumber = phone;
        emailId = email;
        countryCode = conCode;
        tvNumber.setText(countryCode + " " + phoneNumber);
        tvEmail.setText(emailId);
    }

    @Override
    public void closeActivity() {
        finish();
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
                tvCheckInDate.setText(startDate + "," + "\n" + queueTime);
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

                        if (imagePathList.size() > 0) {
                            tvErrorMessage.setVisibility(View.GONE);
                        } else {
                            tvErrorMessage.setVisibility(View.VISIBLE);
                        }

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

                            if (imagePathList.size() > 0) {
                                tvErrorMessage.setVisibility(View.GONE);
                            } else {
                                tvErrorMessage.setVisibility(View.VISIBLE);
                            }
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
            if (data != null && data.getExtras() != null) {
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

                    if (imagePathList.size() > 0) {
                        tvErrorMessage.setVisibility(View.GONE);
                    } else {
                        tvErrorMessage.setVisibility(View.VISIBLE);
                    }
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
    public void sendFamilyMemberDetails(int consumerId, String firstName, String lastName, String phone, String email, String conCode, String whtsappCountryCode, String whatsappNumber, String telegramCountryCode, String telegramNumber, String age, JSONArray preferredLanguages, JSONObject bookingLocation) {
        mFirstName = firstName;
        mLastName = lastName;
        phoneNumber = phone;
        familyMEmID = consumerId;
        emailId = email;
        countryCode = conCode;
        mWhtsappCountryCode = whtsappCountryCode;
        mWhatsappNumber = whatsappNumber;
        mTelegramCountryCode = telegramCountryCode;
        mTelegramNumber = telegramNumber;
        mAge = age;
        mPreferredLanguages = preferredLanguages;
        mBookingLocation = bookingLocation;
        tvNumber.setText(countryCode + " " + phoneNumber);
        if (checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
            String cCode = whtsappCountryCode.replace("+", "");
            virtual_NmbrCCPicker.setCountryForPhoneCode(Integer.parseInt(cCode));
            etVirtualNumber.setText(whatsappNumber);
        } else {
            String cCode = conCode.replace("+", "");
            virtual_NmbrCCPicker.setCountryForPhoneCode(Integer.parseInt(cCode));
            etVirtualNumber.setText(phone);
        }

        // et_countryCode.setText(countryCode);

        if (!emailId.equalsIgnoreCase("")) {
            tvEmail.setText(emailId);
        } else {
            tvEmail.setText("");
        }
        tvConsumerName.setText(mFirstName + " " + mLastName);
    }

    @Override
    public void sendFamilyMemberDetails(int consumerId, String firstName, String lastName, String phone, String email, String conCode) {
        mFirstName = firstName;
        mLastName = lastName;
        phoneNumber = phone;
        familyMEmID = consumerId;
        emailId = email;
        countryCode = conCode;
        tvNumber.setText(countryCode + " " + phoneNumber);
        String cCode = countryCode.replace("+", "");
        virtual_NmbrCCPicker.setCountryForPhoneCode(Integer.parseInt(cCode));
        // et_countryCode.setText(countryCode);

        if (!emailId.equalsIgnoreCase("")) {
            tvEmail.setText(emailId);
        } else {
            tvEmail.setText("");
        }
        tvConsumerName.setText(mFirstName + " " + mLastName);
    }

    @Override
    public void changeMemberName(String name, int id) {

    }

    @Override
    public void changeMemberName(String name, FamilyArrayModel familylist) {

    }

    @Override
    public void CheckedFamilyList(List<FamilyArrayModel> familyList) {

    }

    @Override
    public void SelectedPincodeLocation(PincodeLocationsResponse selectedPincodeLocation) {

    }

    @Override
    public void cpns(ArrayList<String> mcouponArraylist) {
        iCpn = (ICpn) this;
        couponArraylist = mcouponArraylist;
        //CouponApliedOrNotDetails c = new CouponApliedOrNotDetails();
        if (userMessage != null) {


            if (isUser) {
                getAdvancePaymentDetails(userMessage, userId);
            } else {
                getAdvancePaymentDetails(userMessage, providerId);
            }
        }
    }
}