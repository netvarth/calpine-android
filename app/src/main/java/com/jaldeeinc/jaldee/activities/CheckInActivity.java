package com.jaldeeinc.jaldee.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hbb20.CountryCodePicker;
import com.jaldeeinc.jaldee.Interface.ICpn;
import com.jaldeeinc.jaldee.Interface.IDeleteImagesInterface;
import com.jaldeeinc.jaldee.Interface.IFamillyListSelected;
import com.jaldeeinc.jaldee.Interface.IFamilyMemberDetails;
import com.jaldeeinc.jaldee.Interface.IMailSubmit;
import com.jaldeeinc.jaldee.Interface.IMobileSubmit;
import com.jaldeeinc.jaldee.Interface.ISaveNotes;
import com.jaldeeinc.jaldee.Interface.ISelectQ;
import com.jaldeeinc.jaldee.Interface.ISendMessage;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.CouponlistAdapter;
import com.jaldeeinc.jaldee.adapter.ImagePreviewAdapter;
import com.jaldeeinc.jaldee.adapter.MultipleFamilyMemberAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.AddNotes;
import com.jaldeeinc.jaldee.custom.CheckInSlotsDialog;
import com.jaldeeinc.jaldee.custom.CustomNotes;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.FamilyMemberDialog;
import com.jaldeeinc.jaldee.model.BookingModel;
import com.jaldeeinc.jaldee.model.FamilyArrayModel;
import com.jaldeeinc.jaldee.model.QuestionnairInpt;
import com.jaldeeinc.jaldee.model.ShoppingListModel;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.AdvancePaymentDetails;
import com.jaldeeinc.jaldee.response.CoupnResponse;
import com.jaldeeinc.jaldee.response.ProfileModel;
import com.jaldeeinc.jaldee.response.Provider;
import com.jaldeeinc.jaldee.response.ProviderCouponResponse;
import com.jaldeeinc.jaldee.response.Questionnaire;
import com.jaldeeinc.jaldee.response.Questions;
import com.jaldeeinc.jaldee.response.QueueTimeSlotModel;
import com.jaldeeinc.jaldee.response.SearchService;
import com.jaldeeinc.jaldee.response.SearchSetting;
import com.jaldeeinc.jaldee.response.SearchTerminology;
import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.jaldeeinc.jaldee.response.SectorCheckin;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckInActivity extends AppCompatActivity implements ISelectQ, IMobileSubmit, IMailSubmit,
        ISendMessage, IFamilyMemberDetails, IFamillyListSelected, ICpn, IDeleteImagesInterface, ISaveNotes {


    @BindView(R.id.iv_teleService)
    ImageView ivteleService;
    @BindView(R.id.tv_serviceName)
    TextView tvServiceName;
    @BindView(R.id.tv_description)
    TextView tvDescription;
    @BindView(R.id.ll_appoint)
    LinearLayout llAppointment;
    @BindView(R.id.ll_checkIn)
    LinearLayout llCheckIn;
    @BindView(R.id.ll_slots)
    LinearLayout ll_slots;
    @BindView(R.id.tv_term)
    TextView tvTerm;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.tv_email)
    TextView tvEmail;
    @BindView(R.id.ll_onetime_qnr_Layout)
    LinearLayout ll_onetime_qnr_Layout;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.ll_editDetails)
    LinearLayout llEditDetails;
    @BindView(R.id.cv_submit)
    CardView cvSubmit;
    @BindView(R.id.tv_checkInDate)
    TextView tvCheckInDate;
    @BindView(R.id.tv_peopleInLine)
    TextView tvPeopleInLine;
    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.tv_buttonName)
    TextView tvButtonName;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.toolbartitle)
    TextView toolbartitle;
    @BindView(R.id.tv_vsHint)
    TextView tvVsHint;
    @BindView(R.id.cv_addNote)
    CardView cvAddNote;
    @BindView(R.id.cv_attachFile)
    CardView cvAttachFile;
    @BindView(R.id.ll_virtualNumber)
    LinearLayout llVirtualNumber;
    @BindView(R.id.et_virtualNumber)
    EditText etVirtualNumber;
    @BindView(R.id.attach_file_size)
    TextView tvAttachFileSize;
    @BindView(R.id.tv_addNote)
    TextView tvAddNotes;
    /*@BindView(R.id.icon_text)
    static TextView icon_text;
    @BindView(R.id.lImage)
    ImageView lImage;*/


    @BindView(R.id.rl_coupon)
    RelativeLayout rlCoupon;

    @BindView(R.id.et_code)
    EditText etCode;

    @BindView(R.id.tv_apply)
    CustomTextViewBold tvApply;

    @BindView(R.id.ll_preinfo)
    LinearLayout llPreInfo;

    @BindView(R.id.tv_preInfoTitle)
    CustomTextViewSemiBold tvPreInfoTitle;

    @BindView(R.id.tv_preInfo)
    CustomTextViewMedium tvPreInfo;

    @BindView(R.id.ll_coupons)
    LinearLayout llCoupons;

    static int familyMEmID;
    static String totalAmountPay;
    static String totalServicePay;
    static TextView tvConsumerName;
    static Activity mActivity;
    static Context mContext;
    static RecyclerView recycle_family;
    static ArrayList<FamilyArrayModel> MultiplefamilyList = new ArrayList<>();
    static ArrayList<QueueTimeSlotModel> mQueueTimeSlotList = new ArrayList<>();

    private int providerId;
    private int locationId;
    private int uniqueId;
    private Integer queueId;
    private int userId;
    private int GALLERY = 1, CAMERA = 2;
    private boolean isUser;
    private boolean isToken = false;
    private static SearchService checkInInfo = new SearchService();
    private static final String IMAGE_DIRECTORY = "/Jaldee" + "";
    private String providerName;
    private String phoneNumber;
    private String countryCode;
    private String mWhtsappCountryCode;
    private String mWhatsappNumber;
    private String mTelegramCountryCode;
    private String mTelegramNumber;
    private String mAge;
    private String accountBusinessName;
    private String locationName;
    private CheckInSlotsDialog slotsDialog;
    private ISelectQ iSelectQ;
    private IMailSubmit iMailSubmit;
    private IMobileSubmit iMobileSubmit;
    private CouponlistAdapter mAdapter;
    private Uri mImageUri;
    private ISendMessage iSendMessage;
    private AddNotes addNotes;
    private String userMessage = "";
    private ICpn iCpn;
    private FamilyMemberDialog familyMemberDialog;
    private IFamilyMemberDetails iFamilyMemberDetails;
    private IDeleteImagesInterface iDeleteImagesInterface;
    private CustomNotes customNotes;
    private ISaveNotes iSaveNotes;
    private SearchSetting mSearchSettings;
    private JSONArray mPreferredLanguages = new JSONArray();
    private JSONObject mBookingLocation = new JSONObject();
    private Provider providerResponse = new Provider();

    boolean virtualService;

    int consumerID;
    int maxPartysize;

    String checkEncId;
    String emailId;
    String mFirstName;
    String mLastName;
    String sector;
    String subsector;
    String mGender;
    String uuid;
    String value = null;
    String couponEntered;
    String apiDate = "";
    String calcMode;
    String path;
    static String time = null;
    ArrayList<String> couponArraylist = new ArrayList<>();
    ArrayList<CoupnResponse> s3couponList = new ArrayList<>();
    ArrayList<ProviderCouponResponse> providerCouponList = new ArrayList<>();

    RecyclerView recycle_image_attachment;
    RecyclerView list;

    File f;
    File file;

    AdvancePaymentDetails advancePaymentDetails = new AdvancePaymentDetails();
    CountryCodePicker virtual_NmbrCCPicker;
    TextView tvErrorMessage;
    SearchTerminology mSearchTerminology;
    ProfileModel profileDetails;
    SearchViewDetail mBusinessDataList;
    BottomSheetDialog dialog;
    ActiveCheckIn activeAppointment = new ActiveCheckIn();
    ArrayList<ShoppingListModel> imagePathList = new ArrayList<>();
    ImagePreviewAdapter imagePreviewAdapter;
    Bitmap bitmap;
    TextView tv_attach, tv_camera;
    float sQnrPrice;
    static TextView icon_text;
    String providerLogoUrl;

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
        iSendMessage = this;
        iFamilyMemberDetails = this;
        iDeleteImagesInterface = (IDeleteImagesInterface) this;
        iSaveNotes = this;

        mFirstName = SharedPreference.getInstance(CheckInActivity.this).getStringValue("firstname", "");
        mLastName = SharedPreference.getInstance(CheckInActivity.this).getStringValue("lastname", "");
        consumerID = SharedPreference.getInstance(CheckInActivity.this).getIntValue("consumerId", 0);

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
        providerLogoUrl = intent.getStringExtra("providerLogo");
        tvConsumerName = findViewById(R.id.tv_consumerName);

        //////////////temp serviceoptionprice////////////
        sQnrPrice = intent.getFloatExtra("sQnrPrice", 0);
        //////////////temp serviceoptionprice////////////


        list = findViewById(R.id.list);
        recycle_family = findViewById(R.id.recycle_family);
        icon_text = findViewById(R.id.icon_text);

        virtual_NmbrCCPicker = findViewById(R.id.virtual_NmbrCCPicker);
        MultiplefamilyList.clear();

        familyMEmID = consumerID;

        toolbartitle.setText("Date and Info");
        if (isUser) {
            ApiGetOneTimeQNR(0, consumerID, userId, null);
        } else {
            ApiGetOneTimeQNR(0, consumerID, providerId, null);
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
                    //tvCheckInDate.setTextSize(30);
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

                if (checkInInfo.getCheckInServiceAvailability().getPersonAhead() != null && checkInInfo.getCheckInServiceAvailability().getPersonAhead() >= 0) {

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
            if (checkInInfo.getCheckInServiceAvailability().getAvailableDate() != null) {
                if (isUser) {
                    CheckInSlotsDialog csl;
                    csl = new CheckInSlotsDialog(mContext, checkInInfo.getId(), locationId, iSelectQ, userId, apiDate);
                    ll_slots.addView(csl);
                } else {
                    CheckInSlotsDialog csl;
                    csl = new CheckInSlotsDialog(mContext, checkInInfo.getId(), locationId, iSelectQ, providerId, apiDate);
                    ll_slots.addView(csl);
                }

            }

            if (checkInInfo.getServiceType() != null && checkInInfo.getServiceType().equalsIgnoreCase("virtualService")) {

                if (checkInInfo.getVirtualCallingModes().get(0).getCallingMode() != null) {

                    if (checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp") || checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Phone")) {

                        llVirtualNumber.setVisibility(View.VISIBLE);
                        // lImage.setVisibility(View.VISIBLE);

                        if (checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                            tvVsHint.setText("WhatsApp number");
                        } else {

                            tvVsHint.setText("Contact number");
                        }

                    } else {
                        llVirtualNumber.setVisibility(View.GONE);
                        //lImage.setVisibility(View.GONE);
                    }

                    ivteleService.setVisibility(View.VISIBLE);
                    if (checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {

                        ivteleService.setImageResource(R.drawable.zoomicon_sized);

                    } else if (checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {

                        ivteleService.setImageResource(R.drawable.googlemeet_sized);

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

                    }

                } else {
                    llVirtualNumber.setVisibility(View.GONE);
                }
            } else {
                llVirtualNumber.setVisibility(View.GONE);
            }

            if (checkInInfo.isPrePayment()) {

                if (isUser) {
                    getAdvancePaymentDetails(userMessage, userId);
                } else {
                    getAdvancePaymentDetails(userMessage, providerId);
                }
            }

            /*if (checkInInfo.isPreInfoEnabled()) { //  check if pre-info is available for the service
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
            }*/


            if (checkInInfo.getConsumerNoteTitle() != null && !checkInInfo.getConsumerNoteTitle().equalsIgnoreCase("")) {
                tvAddNotes.setText(checkInInfo.getConsumerNoteTitle());
            } else {
                tvAddNotes.setText("Add Note");
            }
        }


        // api calls
        ApiGetProviderDetails(uniqueId);

        ApiGetProfileDetail();


        // click actions
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        tvEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvEmail.getText().toString().equalsIgnoreCase("")) {
                    if (isUser) {
                        familyMemberDialog = new FamilyMemberDialog(CheckInActivity.this, familyMEmID, tvEmail.getText().toString(), phoneNumber, checkInInfo.isPrePayment(), iFamilyMemberDetails, profileDetails, multiplemem, 0, countryCode, virtualService, userId);
                    } else {
                        familyMemberDialog = new FamilyMemberDialog(CheckInActivity.this, familyMEmID, tvEmail.getText().toString(), phoneNumber, checkInInfo.isPrePayment(), iFamilyMemberDetails, profileDetails, multiplemem, 0, countryCode, virtualService, providerId);
                    }
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
                if (isUser) {
                    familyMemberDialog = new FamilyMemberDialog(CheckInActivity.this, familyMEmID, tvEmail.getText().toString(), phoneNumber, checkInInfo.isPrePayment(), iFamilyMemberDetails, profileDetails, multiplemem, 0, countryCode, virtualService, userId);
                } else {
                    familyMemberDialog = new FamilyMemberDialog(CheckInActivity.this, familyMEmID, tvEmail.getText().toString(), phoneNumber, checkInInfo.isPrePayment(), iFamilyMemberDetails, profileDetails, multiplemem, 0, countryCode, virtualService, providerId);
                }
                familyMemberDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                familyMemberDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                familyMemberDialog.show();
                DisplayMetrics metrics = CheckInActivity.this.getResources().getDisplayMetrics();
                int width = (int) (metrics.widthPixels * 1);
                familyMemberDialog.setCancelable(false);
                familyMemberDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        });

        cvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (queueId != null) {
                    if (tvNumber.length() < 10) {
                        Toast.makeText(CheckInActivity.this, "Mobile number should have 10 digits" + "", Toast.LENGTH_SHORT).show();
                    } else {
                        boolean isOneTimeQnrValid = false;
                        if (isOneTimeQnrAvailable) {
                            try {
                                isOneTimeQnrValid = oneTimeQuestionnaire.submitQuestionnaire();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if ((!isOneTimeQnrAvailable) || (isOneTimeQnrAvailable && isOneTimeQnrValid)) {

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
                                            DynamicToast.make(CheckInActivity.this, "Please provide " + checkInInfo.getConsumerNoteTitle(), AppCompatResources.getDrawable(
                                                            CheckInActivity.this, R.drawable.icon_info),
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
                                                    CheckInActivity.this, R.drawable.icon_info),
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
                                                        CheckInActivity.this, R.drawable.icon_info),
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
                } else {
                    DynamicToast.make(CheckInActivity.this, "Please select a Date and Time window", AppCompatResources.getDrawable(
                                    CheckInActivity.this, R.drawable.ic_info_black),
                            ContextCompat.getColor(CheckInActivity.this, R.color.black), ContextCompat.getColor(CheckInActivity.this, R.color.white), Toast.LENGTH_SHORT).show();

                }
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
                    imagePreviewAdapter = new ImagePreviewAdapter(imagePathList, mContext, true, iDeleteImagesInterface);
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 2);
                    recycle_image_attachment.setLayoutManager(mLayoutManager);
                    recycle_image_attachment.setAdapter(imagePreviewAdapter);
                    imagePreviewAdapter.notifyDataSetChanged();
                }


                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (imagePathList != null && imagePathList.size() > 0) {

                            tvErrorMessage.setVisibility(View.GONE);
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
                        if (imagePathList != null && imagePathList.size() > 0) {
                            imagePathList.clear();
                            if (imagePathList.size() == 0) {
                                tvAttachFileSize.setText("Attach File");
                            } else {
                                tvAttachFileSize.setText("Attach File" + "(" + imagePathList.size() + ")");
                            }
                        } else {
                            tvAttachFileSize.setText("Attach File");
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

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ProfileModel> call = apiService.getProfileDetail(consumerID);

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
                            String fName = profileDetails.getUserprofile().getFirstName();
                            String lName = profileDetails.getUserprofile().getLastName();
                            if (fName != null && !fName.trim().isEmpty()) {
                                icon_text.setText(String.valueOf(fName.trim().charAt(0)));
                            } else if (lName != null && !lName.trim().isEmpty()) {
                                icon_text.setText(String.valueOf(lName.trim().charAt(0)));
                            }
                            tvConsumerName.setText(fName + " " + lName);
                            countryCode = SharedPreference.getInstance(mContext).getStringValue("countryCode", "");
                            phoneNumber = profileDetails.getUserprofile().getPrimaryMobileNo();
                            tvNumber.setText(countryCode + " " + phoneNumber);
                            //et_countryCode.setText(countryCode);
                            String cCode = countryCode.replace("+", "");
                            virtual_NmbrCCPicker.setCountryForPhoneCode(Integer.parseInt(cCode));
                            etVirtualNumber.setText(profileDetails.getUserprofile().getPrimaryMobileNo());


                            if (profileDetails.getUserprofile().getEmail() != null) {
                                emailId = profileDetails.getUserprofile().getEmail();
                                tvEmail.setText(profileDetails.getUserprofile().getEmail());
                            } else {
                                tvEmail.setHint("Enter your Mail Id");
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
                    llCoupons.setVisibility(View.VISIBLE);
                } else {
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
                    llCoupons.setVisibility(View.VISIBLE);
                } else {
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
        // Call<AdvancePaymentDetails> call = apiService.getWlAdvancePaymentDetails(String.valueOf(id), requestBody);
        Call<AdvancePaymentDetails> call = apiService.getAdvancePaymentDetails("waitlist", String.valueOf(id), requestBody);

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
        String countryVirtualCode = "";
        if (virtual_NmbrCCPicker.getSelectedCountryCode() != null) {
            countryVirtualCode = virtual_NmbrCCPicker.getSelectedCountryCode();
        } else {
            DynamicToast.make(CheckInActivity.this, "Country code needed", AppCompatResources.getDrawable(
                            CheckInActivity.this, R.drawable.icon_info),
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

            if (etVirtualNumber.getText().toString().trim().length() >= 7) {
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
                    if (etVirtualNumber.getText().toString().trim().equalsIgnoreCase("")) {
                        modeOfCalling = "Enter WhatsApp number";
                    } else if (etVirtualNumber.getText().toString().trim().length() < 7 || (virtual_NmbrCCPicker.getSelectedCountryCode().equalsIgnoreCase("91") && etVirtualNumber.getText().toString().trim().length() != 10)) {
                        modeOfCalling = "Invalid WhatsApp number";
                    }
                } else {
                    modeOfCalling = "Invalid Contact number";
                }
                DynamicToast.make(CheckInActivity.this, modeOfCalling, AppCompatResources.getDrawable(
                                CheckInActivity.this, R.drawable.icon_info),
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
                    String soInputString = SharedPreference.getInstance(mContext).getStringValue(Constants.SERVICEOPTIONQNR, "");
                    if ((soInputString != null && !soInputString.trim().equalsIgnoreCase(""))) {
                        QuestionnairInpt answerLine;
                        Gson gson = new Gson();
                        answerLine = gson.fromJson(soInputString, QuestionnairInpt.class);
                        String jsonInString = new Gson().toJson(answerLine);
                        JSONObject mJSONObject = new JSONObject(jsonInString);
                        waitobj1.put("srvAnswers", mJSONObject);

                    }
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
                    /*JsonObject jsonAge = new JsonObject();
                    jsonAge.addProperty("year", mAge);
                    jsonAge.addProperty("month", 0);
                    waitobj.put("age", jsonAge);*/
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
                if (mGender != null && !mGender.isEmpty()) {
                    waitobj.put("gender", mGender);
                }
                String soInputString = SharedPreference.getInstance(mContext).getStringValue(Constants.SERVICEOPTIONQNR, "");
                if ((soInputString != null && !soInputString.trim().equalsIgnoreCase(""))) {
                    QuestionnairInpt answerLine;
                    Gson gson = new Gson();
                    answerLine = gson.fromJson(soInputString, QuestionnairInpt.class);
                    String jsonInString = new Gson().toJson(answerLine);
                    JSONObject mJSONObject = new JSONObject(jsonInString);
                    waitobj.put("srvAnswers", mJSONObject);

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
                        model.setProviderUniqueId(uniqueId);
                        model.setProviderResponse(providerResponse);
                        model.setJsonObject(queueobj.toString());
                        model.setImagePathList(imagePathList);
                        model.setMessage(txt_addnote);
                        model.setAccountId(accountId);
                        model.setCheckInInfo(checkInInfo);
                        model.setmSearchTerminology(mSearchTerminology);
                        model.setFamilyEMIID(familyMEmID);
                        model.setPhoneNumber(phoneNumber);
                        model.setQuestionnaire(questionnaire);
                        model.setFrom(Constants.CHECKIN);
                        model.setProviderName(providerName);
                        model.setAccountBusinessName(accountBusinessName);
                        model.setLocationName(locationName);
                        model.setDate(time);
                        model.setHint(tvHint.getText().toString());
                        model.setPeopleWaiting(tvPeopleInLine.getText().toString());
                        model.setToken(isToken);
                        model.setCustomerName(tvConsumerName.getText().toString());
                        model.setEmailId(tvEmail.getText().toString());
                        model.setCountryCode(countryCode);
                        model.setMultipleFamilyMembers(MultiplefamilyList);
                        model.setTotalAmount(totalAmountPay);
                        model.setTotalServicePay(totalServicePay);
                        model.setWhtsappCountryCode(mWhtsappCountryCode);
                        model.setWhtsappPhoneNumber(mWhatsappNumber);
                        if (providerLogoUrl != null && !providerLogoUrl.trim().isEmpty()) {
                            model.setProviderLogo(providerLogoUrl);
                        }
                        String pCountryCode = providerResponse.getBusinessProfile().getCountryCode();
                        String pPhNo = providerResponse.getBusinessProfile().getAccountLinkedPhNo();
                        if ((pCountryCode != null) && (!pCountryCode.isEmpty()) && (pPhNo != null) && (!pPhNo.isEmpty())) {
                            model.setProviderPhoneNumber(pCountryCode + " " + pPhNo);
                        }
                        // model.setJacshSelected(cbJCash.isChecked());
                        if (advancePaymentDetails != null) {
                            model.setAmountRequiredNow(advancePaymentDetails.getAmountRequiredNow());
                            model.setNetTotal(advancePaymentDetails.getNetTotal() + sQnrPrice);
                        }
                        if (advancePaymentDetails != null && advancePaymentDetails.getEligibleJcashAmt() != null) {
                            model.setEligibleJcashAmt(advancePaymentDetails.getEligibleJcashAmt().get("jCashAmt").getAsDouble());
                        }
                        if (questionnaire != null) {

                            if (questionnaire.getQuestionsList() != null) {

                                Intent intent = new Intent(CheckInActivity.this, CustomQuestionnaire.class);
                                intent.putExtra("data", model);
                                intent.putExtra("from", Constants.CHECKIN);
                                startActivity(intent);

                            } else {

                                Intent intent = new Intent(CheckInActivity.this, ReconfirmationActivity.class);
                                intent.putExtra("data", model);
                                startActivity(intent);
                            }
                        } else {

                            Intent intent = new Intent(CheckInActivity.this, ReconfirmationActivity.class);
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
                                //tvCheckInDate.setTextSize(20);
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
        if (checkInInfo.getMinPrePaymentAmount() != null && !checkInInfo.getMinPrePaymentAmount().equalsIgnoreCase("0.0")) {
            totalAmountPay = String.valueOf(Double.parseDouble(checkInInfo.getMinPrePaymentAmount()) * MultiplefamilyList.size());
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
        String type = "today";
        if (date2 != null && date1.compareTo(date2) < 0) {
            type = "future";
        }
        if (nextAvailableTime != null) {
            firstWord = "Next Available Time ";
            if (type.equalsIgnoreCase("future")) {
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
            time = secondWord;
        } else {
            firstWord = "Estimated wait time";
            secondWord = Config.getTimeinHourMinutes(Integer.parseInt(estTime));

            if (type.equalsIgnoreCase("future")) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date = null;
                try {
                    date = format.parse(nextAvailableDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String day = (String) DateFormat.format("dd", date);
                String monthString = (String) DateFormat.format("MMM", date);
                time = monthString + " " + day + ", " + nextAvailableTime;
            } else {
                time = "<font color=#2F3032>Today, </font> <font color=#FC6464><br>" + firstWord + " " + secondWord + "</font>";
            }
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
            if (name != null && !name.trim().isEmpty()) {
                icon_text.setText(String.valueOf(name.trim().charAt(0)));
            }
            tvConsumerName.setText(name);
            familyMEmID = memID;
        }
    }

    @Override
    public void sendSelectedQueueInfo(String displayTime, int id, QueueTimeSlotModel queueDetails, String selectedDate) {

        try {
            if (queueDetails == null) {
                llCheckIn.setVisibility(View.GONE);
                queueId = null;
            } else {
                llCheckIn.setVisibility(View.VISIBLE);
                llAppointment.setVisibility(View.GONE);
                if (checkInInfo.getCheckInServiceAvailability().getCalculationMode() != null && !checkInInfo.getCheckInServiceAvailability().getCalculationMode().equalsIgnoreCase("NoCalc")) {

                    String time = getWaitingTime(selectedDate, queueDetails.getServiceTime(), String.valueOf(queueDetails.getQueueWaitingTime()));
                    tvCheckInDate.setVisibility(View.VISIBLE);
                    //tvCheckInDate.setTextSize(30);
                    tvHint.setVisibility(View.VISIBLE);
                    tvCheckInDate.setText(time.split("-")[1]);
                    tvHint.setText(time.split("-")[0]);
                } else {
                    tvCheckInDate.setVisibility(View.VISIBLE); // else condition to show Queue time and date if calculation mode is NoCalc
                    tvHint.setVisibility(View.GONE);
                    String startDate = convertDate(queueDetails.getEffectiveSchedule().getStartDate());
                    String queueTime = queueDetails.getQueueSchedule().getTimeSlots().get(0).getsTime() + "-" + queueDetails.getQueueSchedule().getTimeSlots().get(0).geteTime();
                    //tvCheckInDate.setTextSize(20);
                    tvCheckInDate.setText(startDate + "," + "\n" + queueTime);
                }
                queueId = id;
                apiDate = selectedDate;
                if (queueDetails.getQueueSize() >= 0) {

                    String changedtext = "People waiting in line : " + "<b>" + queueDetails.getQueueSize() + "</b> ";
                    tvPeopleInLine.setText(Html.fromHtml(changedtext));

                }

                if (isUser) {
                    getAdvancePaymentDetails(userMessage, userId);
                } else {
                    getAdvancePaymentDetails(userMessage, providerId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void mailUpdated() {
        String mail = SharedPreference.getInstance(mContext).getStringValue("email", "");
        tvEmail.setText(mail);
    }

    @Override
    public void mailUpdated(String emailId) {

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

                        String mimeType = this.mContext.getContentResolver().getType(uri);
                        String uriString = uri.toString();
                        String extension = "";
                        if (uriString.contains(".")) {
                            extension = uriString.substring(uriString.lastIndexOf(".") + 1);
                        }

                        if (mimeType != null) {
                            extension = mimeType.substring(mimeType.lastIndexOf("/") + 1);
                        }
                        File photoFile = null;

                        try {
                            // Creating file
                            try {
                                photoFile = Config.createFile(mContext, extension, true);
                            } catch (IOException ex) {
                                Toast.makeText(mContext, "Error occurred while creating the file", Toast.LENGTH_SHORT).show();

                                // Log.d(TAG, "Error occurred while creating the file");
                            }

                            InputStream inputStream = mContext.getContentResolver().openInputStream(uri);
                            FileOutputStream fileOutputStream = new FileOutputStream(photoFile);
                            // Copying
                            Config.copyStream(inputStream, fileOutputStream);
                            fileOutputStream.close();
                            inputStream.close();
                        } catch (Exception e) {
                            Toast.makeText(mContext, "onActivityResult: " + e.toString(), Toast.LENGTH_SHORT).show();

                            //Log.d(TAG, "onActivityResult: " + e.toString());
                        }
                        String orgFilePath = photoFile.getAbsolutePath();
                        if (Arrays.asList(Constants.fileExtFormats).contains(extension)) {
                            if (orgFilePath == null) {
                                orgFilePath = Config.getFilePathFromURI(mContext, uri, extension);
                            }
                        } else {
                            Toast.makeText(mContext, "File type not supported", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ShoppingListModel model = new ShoppingListModel();
                        model.setImagePath(orgFilePath);
                        imagePathList.add(model);

                        if (imagePathList.size() > 0) {
                            tvErrorMessage.setVisibility(View.GONE);
                        } else {
                            tvErrorMessage.setVisibility(View.VISIBLE);
                        }

                        imagePreviewAdapter = new ImagePreviewAdapter(imagePathList, mContext, true, iDeleteImagesInterface);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 2);
                        recycle_image_attachment.setLayoutManager(mLayoutManager);
                        recycle_image_attachment.setAdapter(imagePreviewAdapter);
                        imagePreviewAdapter.notifyDataSetChanged();

                    } else if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            String mimeType = this.mContext.getContentResolver().getType(uri);
                            String uriString = uri.toString();
                            String extension = "";
                            if (uriString.contains(".")) {
                                extension = uriString.substring(uriString.lastIndexOf(".") + 1);
                            }

                            if (mimeType != null) {
                                extension = mimeType.substring(mimeType.lastIndexOf("/") + 1);
                            }
                            File photoFile = null;

                            try {
                                // Creating file
                                try {
                                    photoFile = Config.createFile(mContext, extension, true);
                                } catch (IOException ex) {
                                    Toast.makeText(mContext, "Error occurred while creating the file", Toast.LENGTH_SHORT).show();

                                    // Log.d(TAG, "Error occurred while creating the file");
                                }

                                InputStream inputStream = mContext.getContentResolver().openInputStream(uri);
                                FileOutputStream fileOutputStream = new FileOutputStream(photoFile);
                                // Copying
                                Config.copyStream(inputStream, fileOutputStream);
                                fileOutputStream.close();
                                inputStream.close();
                            } catch (Exception e) {
                                Toast.makeText(mContext, "onActivityResult: " + e.toString(), Toast.LENGTH_SHORT).show();

                                //Log.d(TAG, "onActivityResult: " + e.toString());
                            }
                            String orgFilePath = photoFile.getAbsolutePath();
                            if (Arrays.asList(Constants.fileExtFormats).contains(extension)) {

                                if (orgFilePath == null) {
                                    orgFilePath = Config.getFilePathFromURI(mContext, uri, extension);
                                }
                            } else {
                                Toast.makeText(mContext, "File type not supported", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            ShoppingListModel model = new ShoppingListModel();
                            model.setImagePath(orgFilePath);
                            imagePathList.add(model);

                            if (imagePathList.size() > 0) {
                                tvErrorMessage.setVisibility(View.GONE);
                            } else {
                                tvErrorMessage.setVisibility(View.VISIBLE);
                            }
                        }
                        imagePreviewAdapter = new ImagePreviewAdapter(imagePathList, mContext, true, iDeleteImagesInterface);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 2);
                        recycle_image_attachment.setLayoutManager(mLayoutManager);
                        recycle_image_attachment.setAdapter(imagePreviewAdapter);
                        imagePreviewAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else if (requestCode == CAMERA) {
            if (data != null && data.getExtras() != null) {
                File photoFile = null;/////////
                // ///////
                try {//////////
                    photoFile = Config.createFile(mContext, "png", true);//////////
                } catch (IOException e) {/////////////
                    e.printStackTrace();///////////
                }///////////
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");/////////
                try (FileOutputStream out = new FileOutputStream(photoFile)) {////////////
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance////////////
                    // PNG is a lossless format, the compression factor (100) is ignored/////////
                } catch (IOException e) {////////////
                    e.printStackTrace();///////////
                }////////
                String path = photoFile.getAbsolutePath();////////

                if (path != null) {
                    mImageUri = Uri.parse(path);
                    ShoppingListModel model = new ShoppingListModel();
                    model.setImagePath(mImageUri.toString());
                    imagePathList.add(model);
                    if (imagePathList.size() > 0) {
                        tvErrorMessage.setVisibility(View.GONE);
                    } else {
                        tvErrorMessage.setVisibility(View.VISIBLE);
                    }
                }
                imagePreviewAdapter = new ImagePreviewAdapter(imagePathList, mContext, true, iDeleteImagesInterface);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 2);
                recycle_image_attachment.setLayoutManager(mLayoutManager);
                recycle_image_attachment.setAdapter(imagePreviewAdapter);
                imagePreviewAdapter.notifyDataSetChanged();
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
    public void sendFamilyMemberDetails(int fmemId, String firstName, String lastName, String phone, String email, String conCode, String whtsappCountryCode, String whatsappNumber, String telegramCountryCode, String telegramNumber, String age, JSONArray preferredLanguages, JSONObject bookingLocation, String gender) {
        mFirstName = firstName;
        mLastName = lastName;
        phoneNumber = phone;
        familyMEmID = fmemId;
        emailId = email;
        countryCode = conCode;
        mWhtsappCountryCode = whtsappCountryCode;
        mWhatsappNumber = whatsappNumber;
        mTelegramCountryCode = telegramCountryCode;
        mTelegramNumber = telegramNumber;
        mAge = age;
        mPreferredLanguages = preferredLanguages;
        mBookingLocation = bookingLocation;
        mGender = gender;
        tvNumber.setText(countryCode + " " + phoneNumber);
        if (isUser) {
            ApiGetOneTimeQNR(familyMEmID, consumerID, userId, null);
        } else {
            ApiGetOneTimeQNR(familyMEmID, consumerID, providerId, null);
        }
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
        if (mFirstName != null && !mFirstName.trim().isEmpty()) {
            icon_text.setText(String.valueOf(mFirstName.trim().charAt(0)));
        } else if (mLastName != null && !mLastName.trim().isEmpty()) {
            icon_text.setText(String.valueOf(mLastName.trim().charAt(0)));
        }
        if (mFirstName != null && !mFirstName.trim().isEmpty()) {
            icon_text.setText(String.valueOf(mFirstName.trim().charAt(0)));
        } else if (mLastName != null && !mLastName.trim().isEmpty()) {
            icon_text.setText(String.valueOf(mLastName.trim().charAt(0)));
        }
        tvConsumerName.setText(mFirstName + " " + mLastName);
    }

    @Override
    public void sendFamilyMemberDetails(int fmemId, String firstName, String lastName, String phone, String email, String conCode) {
        mFirstName = firstName;
        mLastName = lastName;
        phoneNumber = phone;
        familyMEmID = fmemId;
        emailId = email;
        countryCode = conCode;
        tvNumber.setText(countryCode + " " + phoneNumber);
        String cCode = countryCode.replace("+", "");
        virtual_NmbrCCPicker.setCountryForPhoneCode(Integer.parseInt(cCode));
        // et_countryCode.setText(countryCode);
        if (isUser) {
            ApiGetOneTimeQNR(familyMEmID, consumerID, userId, null);
        } else {
            ApiGetOneTimeQNR(familyMEmID, consumerID, providerId, null);
        }
        if (!emailId.equalsIgnoreCase("")) {
            tvEmail.setText(emailId);
        } else {
            tvEmail.setText("");
        }
        if (mFirstName != null && !mFirstName.trim().isEmpty()) {
            icon_text.setText(String.valueOf(mFirstName.trim().charAt(0)));
        } else if (mLastName != null && !mLastName.trim().isEmpty()) {
            icon_text.setText(String.valueOf(mLastName.trim().charAt(0)));
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

    @Override
    public void delete(int position, String imagePath) {
        imagePathList.remove(position);
        imagePreviewAdapter.notifyDataSetChanged();

        if (imagePathList != null && imagePathList.size() > 0) {

            for (int i = 0; i < imagePathList.size(); i++) {

                if (imagePathList.get(i).getImagePath().equalsIgnoreCase(imagePath)) {

                    imagePathList.remove(i);
                }
            }
        }
    }

    @Override
    public void addedNotes(int position) {
        showNotesDialog(position);
    }

    private void showNotesDialog(int position) {

        customNotes = new CustomNotes(mContext, position, iSaveNotes, imagePathList.get(position).getCaption());
        customNotes.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
        customNotes.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customNotes.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customNotes.setCancelable(false);
        customNotes.show();
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 1);
        customNotes.getWindow().setGravity(Gravity.BOTTOM);
        customNotes.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void saveMessage(String caption, int position) {

        imagePathList.get(position).setCaption(caption);
        imagePreviewAdapter.notifyDataSetChanged();
    }

    Questionnaire questionnaire = new Questionnaire();
    OneTimeQuestionnaire oneTimeQuestionnaire = new OneTimeQuestionnaire();
    boolean isOneTimeQnrAvailable = false;

    private void ApiGetOneTimeQNR(int fMemId, int consumerId, int providerId, String isFrom) {

        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);

        Call<Questionnaire> call = apiService.getOneTimeQnr(fMemId, consumerId, providerId);
//        Config.logV("Request--BODY-------------------------" + new Gson().toJson(jsonObj.toString()));
        call.enqueue(new Callback<Questionnaire>() {
            @Override
            public void onResponse(Call<Questionnaire> call, Response<Questionnaire> response) {

                try {
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        questionnaire = response.body();
                        if (questionnaire != null && questionnaire.getQuestionsList() != null && questionnaire.getQuestionsList().size() > 0) {
                            for (Questions qns : questionnaire.getQuestionsList()) {
                                if (qns.getGetQuestions() != null && qns.getGetQuestions().size() > 0) {
                                    isOneTimeQnrAvailable = true;
                                }
                            }
                        }
                        if (isOneTimeQnrAvailable) {
                            ll_onetime_qnr_Layout.setVisibility(View.VISIBLE);
                            oneTimeQuestionnaire = new OneTimeQuestionnaire();
                            Bundle bundle = new Bundle();
                            bundle.putString("requestFor", Constants.APPOINTMENT);
                            bundle.putString("requestFrom", "");
                            bundle.putInt("consumerId", consumerId);
                            bundle.putInt("providerId", providerId);
                            bundle.putInt("familyMemId", fMemId);

                            oneTimeQuestionnaire.setArguments(bundle);
                            final FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.rl_onetime_qnr, oneTimeQuestionnaire)
                                    .commit();
                        } else {
                            ll_onetime_qnr_Layout.setVisibility(View.GONE);
                        }

                    } else {

                        Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_LONG).show();

                        Config.logV("Error" + response.errorBody().string());

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Questionnaire> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
//                if (mDialog.isShowing())
//                    Config.closeDialog(getActivity(), mDialog);
            }
        });
    }
}