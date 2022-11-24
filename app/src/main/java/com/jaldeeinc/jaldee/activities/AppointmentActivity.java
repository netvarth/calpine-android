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
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.jaldeeinc.jaldee.Interface.ISendMessage;
import com.jaldeeinc.jaldee.Interface.ISlotInfo;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.CouponlistAdapter;
import com.jaldeeinc.jaldee.adapter.ImagePreviewAdapter;
import com.jaldeeinc.jaldee.adapter.MultipleFamilyMemberAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.AddNotes;
import com.jaldeeinc.jaldee.custom.CustomNotes;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.CustomToolTip;
import com.jaldeeinc.jaldee.custom.FamilyMemberDialog;
import com.jaldeeinc.jaldee.custom.SlotsDialog;
import com.jaldeeinc.jaldee.model.BookingModel;
import com.jaldeeinc.jaldee.model.FamilyArrayModel;
import com.jaldeeinc.jaldee.model.PincodeLocationsResponse;
import com.jaldeeinc.jaldee.model.SelectedSlotDetail;
import com.jaldeeinc.jaldee.model.ShoppingListModel;
import com.jaldeeinc.jaldee.response.AdvancePaymentDetails;
import com.jaldeeinc.jaldee.response.CoupnResponse;
import com.jaldeeinc.jaldee.response.ProfileModel;
import com.jaldeeinc.jaldee.response.Provider;
import com.jaldeeinc.jaldee.response.ProviderCouponResponse;
import com.jaldeeinc.jaldee.response.Questionnaire;
import com.jaldeeinc.jaldee.response.Questions;
import com.jaldeeinc.jaldee.response.SearchSetting;
import com.jaldeeinc.jaldee.response.SearchTerminology;
import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.jaldeeinc.jaldee.response.SectorCheckin;
import com.jaldeeinc.jaldee.response.ServiceInfo;
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
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentActivity extends AppCompatActivity implements ISlotInfo, IMailSubmit, IMobileSubmit, ISendMessage, IFamilyMemberDetails, IFamillyListSelected, ICpn, IDeleteImagesInterface, ISaveNotes {

    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.toolbartitle)
    TextView toolbartitle;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_selectedDateHint)
    TextView tvSelectedDateHint;
    @BindView(R.id.tv_checkInDate)
    TextView tvCheckInDate;
    @BindView(R.id.tv_peopleInLine)
    TextView tvPeopleInLine;
    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.ll_appoint)
    LinearLayout llAppointment;
    @BindView(R.id.ll_checkIn)
    LinearLayout llCheckIn;
    @BindView(R.id.iv_teleService)
    ImageView ivteleService;
    @BindView(R.id.ll_slots)
    LinearLayout ll_slots;
    @BindView(R.id.ll_calendar)
    LinearLayout ll_calendar;
    @BindView(R.id.calendar)
    DatePicker calendar;
    @BindView(R.id.tvSelectedCalendarDate)
    TextView tvSelectedCalendarDate;
    @BindView(R.id.ll_editDetails)
    LinearLayout llEditDetails;
    @BindView(R.id.tv_consumerName)
    TextView tvConsumerName;
    @BindView(R.id.recycle_family)
    RecyclerView recycle_family;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.tv_email)
    TextView tvEmail;
    @BindView(R.id.tv_serviceName)
    TextView tvServiceName;
    @BindView(R.id.tv_description)
    TextView tvDescription;
    @BindView(R.id.ll_preinfo)
    LinearLayout llPreInfo;
    @BindView(R.id.tv_preInfoTitle)
    CustomTextViewSemiBold tvPreInfoTitle;
    @BindView(R.id.tv_preInfo)
    CustomTextViewMedium tvPreInfo;
    @BindView(R.id.tv_buttonName)
    TextView tvButtonName;
    @BindView(R.id.ll_onetime_qnr_Layout)
    LinearLayout ll_onetime_qnr_Layout;
    @BindView(R.id.tv_term)
    TextView tvTerm;
    @BindView(R.id.cv_submit)
    CardView cvSubmit;
    @BindView(R.id.tv_vsHint)
    TextView tvVsHint;
    @BindView(R.id.et_virtualNumber)
    EditText etVirtualNumber;
    @BindView(R.id.cv_addNote)
    CardView cvAddNote;
    @BindView(R.id.cv_attachFile)
    CardView cvAttachFile;
    @BindView(R.id.ll_virtualNumber)
    LinearLayout llVirtualNumber;
    @BindView(R.id.tv_addNote)
    TextView tvAddNotes;
    @BindView(R.id.attach_file_size)
    TextView tvAttachFileSize;
    @BindView(R.id.virtual_NmbrCCPicker)
    CountryCodePicker virtual_NmbrCCPicker;
    /*@BindView(R.id.icon_text)
    TextView icon_text;*/


    @BindView(R.id.rl_coupon)
    RelativeLayout rlCoupon;

    @BindView(R.id.et_code)
    EditText etCode;

    @BindView(R.id.tv_apply)
    TextView tvApply;

    @BindView(R.id.ll_coupons)
    LinearLayout llCoupons;

    TextView tvErrorMessage;

    String mFirstName, mLastName;
    int consumerID;
    private int uniqueId;
    private String providerName;
    private int providerId;
    private int locationId;
    private String phoneNumber;
    private String emailId;
    private String mGender;
    private ServiceInfo serviceInfo = new ServiceInfo();
    SearchTerminology mSearchTerminology;
    ProfileModel profileDetails;
    private ISlotInfo iSlotInfo;
    String uuid;
    String couponEntered;
    private int scheduleId;
    private IMailSubmit iMailSubmit;
    private IMobileSubmit iMobileSubmit;
    ArrayList<String> couponArraylist = new ArrayList<>();
    ArrayList<CoupnResponse> s3couponList = new ArrayList<>();
    ArrayList<ProviderCouponResponse> providerCouponList = new ArrayList<>();
    AdvancePaymentDetails advancePaymentDetails = new AdvancePaymentDetails();

    RecyclerView list;
    private CouponlistAdapter mAdapter;
    static int familyMEmID;
    String slotTime;
    BottomSheetDialog dialog;
    String calcMode;
    static Activity mActivity;
    static Context mContext;
    String apiDate = "";
    private int userId;
    String sector, subsector;
    SearchViewDetail mBusinessDataList;

    //files related
    private int GALLERY = 1, CAMERA = 2;
    RecyclerView recycle_image_attachment;

    ArrayList<ShoppingListModel> imagePathList = new ArrayList<>();
    ImagePreviewAdapter imagePreviewAdapter;

    private Uri mImageUri;
    TextView tv_attach, tv_camera;
    private ISendMessage iSendMessage;
    private AddNotes addNotes;
    private String userMessage = "";
    private FamilyMemberDialog familyMemberDialog;
    private IFamilyMemberDetails iFamilyMemberDetails;
    private String prepayAmount = "", prePayRemainingAmount = "";
    private String countryCode, mWhtsappCountryCode, mWhatsappNumber, mTelegramCountryCode, mTelegramNumber, mAge;
    private JSONArray mPreferredLanguages = new JSONArray();
    private JSONObject mBookingLocation = new JSONObject();
    private Provider providerResponse = new Provider();
    private SearchSetting mSearchSettings;
    private String accountBusinessName;
    private String locationName;
    private ICpn iCpn;
    boolean virtualService;
    private IDeleteImagesInterface iDeleteImagesInterface;
    private CustomNotes customNotes;
    private ISaveNotes iSaveNotes;
    boolean showOnlyAvailableSlots;
    List<SelectedSlotDetail> selectedSlotDetails = new ArrayList<>();
    float sQnrPrice;
    private int consumerId;
    String type;
    static TextView icon_text;
    String providerLogoUrl;

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
        iSendMessage = this;
        iFamilyMemberDetails = this;
        iDeleteImagesInterface = (IDeleteImagesInterface) this;
        iSaveNotes = this;

        mFirstName = SharedPreference.getInstance(AppointmentActivity.this).getStringValue("firstname", "");
        mLastName = SharedPreference.getInstance(AppointmentActivity.this).getStringValue("lastname", "");
        consumerID = SharedPreference.getInstance(AppointmentActivity.this).getIntValue("consumerId", 0);

        SharedPreference.getInstance(mContext).setValue(Constants.QUESTIONNAIRE, "");
        SharedPreference.getInstance(mContext).setValue(Constants.QIMAGES, "");

        // getting necessary details from intent
        Intent intent = getIntent();
        uniqueId = intent.getIntExtra("uniqueID", 0);
        providerName = intent.getStringExtra("providerName");
        accountBusinessName = intent.getStringExtra("accountBusinessName");
        locationName = intent.getStringExtra("locationName");
        serviceInfo = (ServiceInfo) intent.getSerializableExtra("serviceInfo");
        locationId = intent.getIntExtra("locationId", 0);
        providerId = intent.getIntExtra("providerId", 0);
        userId = intent.getIntExtra("userId", 0);
        sector = intent.getStringExtra("sector");
        consumerId = intent.getIntExtra("consumerId", 0);
        providerLogoUrl = intent.getStringExtra("providerLogo");
        //////////////temp serviceoptionprice////////////
        sQnrPrice = intent.getFloatExtra("sQnrPrice", 0);
        //////////////temp serviceoptionprice////////////

        type = serviceInfo.getType();
        list = findViewById(R.id.list);
        icon_text = findViewById(R.id.icon_text);
       // virtual_NmbrCCPicker = findViewById(R.id.virtual_NmbrCCPicker);

        familyMEmID = consumerID;

        Typeface tyface1 = Typeface.createFromAsset(AppointmentActivity.this.getAssets(),
                "fonts/JosefinSans-SemiBold.ttf");
        etCode.setTypeface(tyface1);

        /*if (providerName != null) {
            tvProviderName.setText(providerName);
        }
*/
        toolbartitle.setText("Date and Info");
        if (serviceInfo.isUser()) {
            ApiGetOneTimeQNR(0, consumerID, userId, null);
        } else {
            ApiGetOneTimeQNR(0, consumerID, providerId, null);
        }

        try {
            if (serviceInfo.getServiceBookingType().equalsIgnoreCase(Constants.SERVICE_BOOKING_TYPE_BOOKING)
                    || (serviceInfo.getServiceBookingType().equalsIgnoreCase(Constants.SERVICE_BOOKING_TYPE_REQUEST) && serviceInfo.isDateTime())) {
                if (serviceInfo.getAvailableDate() != null) {
                    int maxAvailableSlots;
                    if (serviceInfo.getIsPrePayment().equalsIgnoreCase("true")) {
                        maxAvailableSlots = 1;
                    } else {
                        maxAvailableSlots = serviceInfo.getMaxBookingsAllowed();
                    }
                    if (serviceInfo.getTime() != null) {
                        apiDate = serviceInfo.getAvailableDate();
                        scheduleId = serviceInfo.getScheduleId();
                        tvTime.setText(convertTime(serviceInfo.getTime()));
                        slotTime = serviceInfo.getTime();
                    }
                    ll_slots.setVisibility(View.VISIBLE);
                    ll_calendar.setVisibility(View.GONE);
                    if (serviceInfo.isUser()) {
                        SlotsDialog sl;
                        sl = new SlotsDialog(AppointmentActivity.this, serviceInfo.getServiceId(), locationId, iSlotInfo, userId, apiDate, maxAvailableSlots, showOnlyAvailableSlots);
                        ll_slots.addView(sl);

                    } else {
                        SlotsDialog sl;
                        sl = new SlotsDialog(AppointmentActivity.this, serviceInfo.getServiceId(), locationId, iSlotInfo, providerId, apiDate, maxAvailableSlots, showOnlyAvailableSlots);
                        ll_slots.addView(sl);

                    }
                }
            } else if (serviceInfo.getServiceBookingType().equalsIgnoreCase(Constants.SERVICE_BOOKING_TYPE_REQUEST) && serviceInfo.isDate()) {
                apiDate = serviceInfo.getAvailableDate();
                Calendar cal = Calendar.getInstance();
                try {
                    SimpleDateFormat formatter;
                    Date date;
                    formatter = new SimpleDateFormat("yyyy-MM-dd");
                    date = (Date) formatter.parse(apiDate);
                    cal = Calendar.getInstance();
                    cal.setTime(date);
                } catch (ParseException e) {
                    System.out.println("Exception :" + e);
                }
                ll_slots.setVisibility(View.GONE);
                ll_calendar.setVisibility(View.VISIBLE);
                calendar.setMinDate(Calendar.getInstance().getTimeInMillis());
                calendar.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                tvTime.setText("");
                tvTime.setVisibility(View.GONE);
                tvSelectedDateHint.setText("Selected Date");
                scheduleId = serviceInfo.getScheduleId();

                // Add Listener in calendar
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    calendar
                            .setOnDateChangedListener(
                                    new DatePicker
                                            .OnDateChangedListener() {
                                        @Override
                                        public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {


                                            // Store the value of date with
                                            // format in String type Variable
                                            // Add 1 in month because month
                                            // index is start with 0
                                            String date = i + "-" + (i1 + 1) + "-" + i2;

                                            // set this date in TextView for Display
                                            tvTime.setText("");
                                            tvTime.setVisibility(View.GONE);
                                            try {
                                                tvDate.setText(Config.getCustomDateString(date));
                                                apiDate = date;  // to convert selected date to api date format

                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            tvSelectedDateHint.setText("Selected Date");
                                        }
                                    });
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (serviceInfo != null) {
            showOnlyAvailableSlots = serviceInfo.isShowOnlyAvailableSlots();
            if (serviceInfo.getIsPrePayment().equalsIgnoreCase("true")) {
                tvButtonName.setText("Next");
            } else {
                tvButtonName.setText("Next");
            }

            if (serviceInfo.getConsumerNoteTitle() != null && !serviceInfo.getConsumerNoteTitle().equalsIgnoreCase("")) {
                tvAddNotes.setText(serviceInfo.getConsumerNoteTitle());
            } else {
                tvAddNotes.setText("Add Note");
            }

            String name = serviceInfo.getServiceName();
            tvServiceName.setText(name);
            if (serviceInfo.getDescription() != null && !serviceInfo.getDescription().trim().isEmpty()) {
                tvDescription.setVisibility(View.VISIBLE);
                tvDescription.setText(serviceInfo.getDescription());
            }
            if (serviceInfo.getServiceType() != null && serviceInfo.getServiceType().equalsIgnoreCase("virtualService")) {
                virtualService = true;
            } else {
                virtualService = false;
            }
            try {
                if (serviceInfo.getType().equalsIgnoreCase(Constants.APPOINTMENT)) {
                    if (serviceInfo.getAvailableDate() != null) {
                        tvDate.setText(Config.getCustomDateString(serviceInfo.getAvailableDate()));
                    }
                    llAppointment.setVisibility(View.VISIBLE);
                    llCheckIn.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (serviceInfo.getServiceType() != null && serviceInfo.getServiceType().equalsIgnoreCase("virtualService")) {

                if (serviceInfo.getCallingMode() != null) {

                    if (serviceInfo.getCallingMode().equalsIgnoreCase("WhatsApp") || serviceInfo.getCallingMode().equalsIgnoreCase("Phone")) {

                        llVirtualNumber.setVisibility(View.VISIBLE);

                        if (serviceInfo.getCallingMode().equalsIgnoreCase("WhatsApp")) {
                            tvVsHint.setText("WhatsApp number");
                        } else {

                            tvVsHint.setText("Contact number");
                        }
                    } else {
                        llVirtualNumber.setVisibility(View.GONE);
                    }

                    ivteleService.setVisibility(View.VISIBLE);
                    if (serviceInfo.getCallingMode().equalsIgnoreCase("Zoom")) {

                        ivteleService.setImageResource(R.drawable.zoom);

                    } else if (serviceInfo.getCallingMode().equalsIgnoreCase("GoogleMeet")) {

                        ivteleService.setImageResource(R.drawable.googlemeet);

                    } else if (serviceInfo.getCallingMode().equalsIgnoreCase("WhatsApp")) {
                        if (serviceInfo.getVirtualServiceType() != null && serviceInfo.getVirtualServiceType().equalsIgnoreCase("videoService")) {
                            ivteleService.setImageResource(R.drawable.whatsapp_videoicon);
                        } else {
                            ivteleService.setImageResource(R.drawable.whatsapp_icon);
                        }

                    } else if (serviceInfo.getCallingMode().equalsIgnoreCase("VideoCall")) {

                        ivteleService.setImageResource(R.drawable.ic_jaldeevideo);

                    }
                } else {
                    llVirtualNumber.setVisibility(View.GONE);
                }
            } else {
                llVirtualNumber.setVisibility(View.GONE);
            }

            if (serviceInfo.getIsPrePayment().equalsIgnoreCase("true")) {

                if (serviceInfo.isUser()) {
                    getAdvancePaymentDetails(userMessage, userId);
                } else {
                    getAdvancePaymentDetails(userMessage, providerId);
                }
            }

            /*if (serviceInfo.isPreInfoEnabled()) {  //  check if pre-info is available for the service

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
            }*/
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
                    if (serviceInfo.isUser()) {
                        familyMemberDialog = new FamilyMemberDialog(AppointmentActivity.this, familyMEmID, tvEmail.getText().toString(), phoneNumber, serviceInfo.getIsPrePayment(), iFamilyMemberDetails, profileDetails, multiplemem, 0, countryCode, virtualService, userId);
                    } else {
                        familyMemberDialog = new FamilyMemberDialog(AppointmentActivity.this, familyMEmID, tvEmail.getText().toString(), phoneNumber, serviceInfo.getIsPrePayment(), iFamilyMemberDetails, profileDetails, multiplemem, 0, countryCode, virtualService, providerId);
                    }
                    familyMemberDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                    familyMemberDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    familyMemberDialog.show();
                    DisplayMetrics metrics = AppointmentActivity.this.getResources().getDisplayMetrics();
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
//                    mobileNumberDialog = new MobileNumberDialog(AppointmentActivity.this, profileDetails, iMobileSubmit, tvNumber.getText().toString());
//                    mobileNumberDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
//                    mobileNumberDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                    mobileNumberDialog.show();
//                    DisplayMetrics metrics = AppointmentActivity.this.getResources().getDisplayMetrics();
//                    int width = (int) (metrics.widthPixels * 1);
//                    mobileNumberDialog.getWindow().setGravity(Gravity.BOTTOM);
//                    mobileNumberDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
//                }
            }
        });

        llEditDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serviceInfo.isUser()) {
                    familyMemberDialog = new FamilyMemberDialog(AppointmentActivity.this, familyMEmID, tvEmail.getText().toString(), phoneNumber, serviceInfo.getIsPrePayment(), iFamilyMemberDetails, profileDetails, multiplemem, 0, countryCode, virtualService, userId);
                } else {
                    familyMemberDialog = new FamilyMemberDialog(AppointmentActivity.this, familyMEmID, tvEmail.getText().toString(), phoneNumber, serviceInfo.getIsPrePayment(), iFamilyMemberDetails, profileDetails, multiplemem, 0, countryCode, virtualService, providerId);
                }
                familyMemberDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                familyMemberDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                familyMemberDialog.show();
                DisplayMetrics metrics = AppointmentActivity.this.getResources().getDisplayMetrics();
                int width = (int) (metrics.widthPixels * 1);
                familyMemberDialog.setCancelable(false);
                familyMemberDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

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
                for (int i = 0; i < providerCouponList.size(); i++) {
                    if (providerCouponList.get(i).getCouponCode().equals(couponEntered)) {
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
                cpns(couponArraylist);
            }
        });

        cvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((slotTime != null && !slotTime.trim().isEmpty()) || (serviceInfo.getServiceBookingType().equalsIgnoreCase(Constants.SERVICE_BOOKING_TYPE_REQUEST) && serviceInfo.isDate())) {
                    if (tvNumber.length() < 10) {
                        Toast.makeText(AppointmentActivity.this, "Mobile number should have 10 digits" + "", Toast.LENGTH_SHORT).show();
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
                            if (selectedSlotDetails != null && !selectedSlotDetails.isEmpty() && selectedSlotDetails.size() > 1) {
                                if (serviceInfo.isNoteManidtory()) {

                                    if (userMessage != null && !userMessage.trim().equalsIgnoreCase("")) {

                                        if (serviceInfo.isUser()) {
                                            ApiMultipleAppointment(userMessage, userId);
                                        } else {
                                            ApiMultipleAppointment(userMessage, providerId);
                                        }
                                    } else {

                                        DynamicToast.make(AppointmentActivity.this, serviceInfo.getConsumerNoteTitle(), AppCompatResources.getDrawable(
                                                        AppointmentActivity.this, R.drawable.ic_info_black),
                                                ContextCompat.getColor(AppointmentActivity.this, R.color.white), ContextCompat.getColor(AppointmentActivity.this, R.color.green), Toast.LENGTH_SHORT).show();
                                    }
                                } else {

                                    if (serviceInfo.isUser()) {
                                        ApiMultipleAppointment(userMessage, userId);
                                    } else {
                                        ApiMultipleAppointment(userMessage, providerId);
                                    }
                                }
                            } else {
                                if (serviceInfo.getIsPrePayment().equalsIgnoreCase("true")) {  // check if selected service requires prepayment
                                    if (tvEmail.getText().toString() != null && tvEmail.getText().length() > 0) { // if selected service requires prepayment..then Email is mandatory

                                        if (serviceInfo.isNoteManidtory()) { // check if notes is mandatory for selected service

                                            if (userMessage != null && !userMessage.trim().equalsIgnoreCase("")) {

                                                if (serviceInfo.isUser()) {
                                                    ApiAppointment(userMessage, userId);
                                                } else {
                                                    ApiAppointment(userMessage, providerId);
                                                }
                                            } else {
                                                DynamicToast.make(AppointmentActivity.this, "Please provide " + serviceInfo.getConsumerNoteTitle(), AppCompatResources.getDrawable(
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

                                        if (userMessage != null && !userMessage.trim().equalsIgnoreCase("")) {

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
                    }
                } else {
                    DynamicToast.make(AppointmentActivity.this, "Please select Date and Time", AppCompatResources.getDrawable(
                                    AppointmentActivity.this, R.drawable.ic_info_black),
                            ContextCompat.getColor(AppointmentActivity.this, R.color.black), ContextCompat.getColor(AppointmentActivity.this, R.color.white), Toast.LENGTH_SHORT).show();

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

                                    requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA);
                                    return;
                                } else {
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    Intent cameraIntent = new Intent();
                                    cameraIntent.setType("image/*");
                                    cameraIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                    cameraIntent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(intent, CAMERA);
                                }
                            } else {

                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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

                Intent iCoupons = new Intent(AppointmentActivity.this, CouponActivity.class);
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
                        Config.closeDialog(AppointmentActivity.this, mDialog);
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
                    Config.closeDialog(AppointmentActivity.this, mDialog);
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

    private void ApiGetProfileDetail() {

        ApiInterface apiService =
                ApiClient.getClient(AppointmentActivity.this).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(AppointmentActivity.this, AppointmentActivity.this.getResources().getString(R.string.dialog_log_in));
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
                                tvEmail.setText(emailId);
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

    private void ApiSearchViewSetting(String setting) {

        try {

            if (setting != null) {
                mSearchSettings = new Gson().fromJson(setting, SearchSetting.class);

                calcMode = mSearchSettings.getCalculationMode();

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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    SectorCheckin checkin_sector = null;
    int partySize = 0;
    boolean enableparty = false;
    boolean multiplemem = false;

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

    private void ApiMultipleAppointment(final String txt_addnote, int id) {

        ArrayList<BookingModel> bookingModels = new ArrayList<>();
        uuid = UUID.randomUUID().toString();
        String countryVirtualCode = "";
        if (virtual_NmbrCCPicker.getSelectedCountryCode() != null) {
            countryVirtualCode = virtual_NmbrCCPicker.getSelectedCountryCode();
        } else {
            DynamicToast.make(AppointmentActivity.this, "Countrycode needed", AppCompatResources.getDrawable(
                            AppointmentActivity.this, R.drawable.ic_info_black),
                    ContextCompat.getColor(AppointmentActivity.this, R.color.white), ContextCompat.getColor(AppointmentActivity.this, R.color.green), Toast.LENGTH_SHORT).show();
            return;
        }
        for (SelectedSlotDetail s : selectedSlotDetails) {

            // getting data from dialog
            String convertedTime = s.getDisplayTime().replace("am", "AM").replace("pm", "PM").replaceAll(" ", "\u00a0");
            tvSelectedDateHint.setText("Selected Time slot");
            scheduleId = s.getScheduleId();
            slotTime = s.getSlotTime();
            try {
                apiDate = getApiDateFormat(s.getCalendarDate());  // to convert selected date to api date format
            } catch (ParseException e) {
                e.printStackTrace();
            }
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

            JSONObject jsonObj2 = new JSONObject();
            JSONObject jsonObj3 = new JSONObject();
            try {

                queueobj.put("appmtDate", apiDate);
                sjsonobj.put("id", scheduleId);
                queueobj.put("consumerNote", txt_addnote);
                queueobj.put("phoneNumber", phoneNumber);
                queueobj.put("countryCode", countryCode);
                if (serviceInfo.isUser()) {
                    pjsonobj.put("id", providerId);
                } else {
                    pjsonobj.put("id", 0);
                }


                if (etVirtualNumber.getText().toString().trim().length() >= 7) {
                    if (serviceInfo.getCallingMode() != null && serviceInfo.getCallingMode().equalsIgnoreCase("whatsApp")) {
                        virtualService.put("WhatsApp", countryVirtualCode + etVirtualNumber.getText());
                        mWhtsappCountryCode = countryVirtualCode;
                        mWhatsappNumber = etVirtualNumber.getText().toString();
                    } else if (serviceInfo.getCallingMode() != null && serviceInfo.getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                        virtualService.put("GoogleMeet", serviceInfo.getVirtualCallingValue());
                    } else if (serviceInfo.getCallingMode() != null && serviceInfo.getCallingMode().equalsIgnoreCase("Zoom")) {
                        virtualService.put("Zoom", serviceInfo.getVirtualCallingValue());
                    } else if (serviceInfo.getCallingMode() != null && serviceInfo.getCallingMode().equalsIgnoreCase("Phone")) {
                        virtualService.put("Phone", countryVirtualCode + etVirtualNumber.getText());
                        phoneNumber = etVirtualNumber.getText().toString();
                    } else if (serviceInfo.getCallingMode() != null && serviceInfo.getCallingMode().equalsIgnoreCase("VideoCall")) {
                        virtualService.put("VideoCall", serviceInfo.getVirtualCallingValue());
                    }
                } else {

                    String modeOfCalling = "";
                    if (serviceInfo.getCallingMode() != null && serviceInfo.getCallingMode().equalsIgnoreCase("whatsApp")) {
                        if (etVirtualNumber.getText().toString().trim().equalsIgnoreCase("")) {
                            modeOfCalling = "Enter WhatsApp number";
                        } else {
                            modeOfCalling = "Invalid WhatsApp number";
                        }
                    } else if (etVirtualNumber.getText().toString().trim().length() < 7 || (virtual_NmbrCCPicker.getSelectedCountryCode().equalsIgnoreCase("91") && etVirtualNumber.getText().toString().trim().length() != 10)) {
                        modeOfCalling = "Invalid Contact number";
                    }
                    DynamicToast.make(AppointmentActivity.this, modeOfCalling, AppCompatResources.getDrawable(
                                    AppointmentActivity.this, R.drawable.ic_info_black),
                            ContextCompat.getColor(AppointmentActivity.this, R.color.white), ContextCompat.getColor(AppointmentActivity.this, R.color.green), Toast.LENGTH_SHORT).show();
                    return;
                }

                sejsonobj.put("id", serviceInfo.getServiceId());
                sejsonobj.put("serviceType", serviceInfo.getServiceType());

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
                    if (mGender != null && !mGender.isEmpty()) {
                        waitobj.put("gender", mGender);
                    }
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
            BookingModel model = new BookingModel();
            model.setAppointmentType(Constants.MULTIPLE_APPOINTMENT);
            model.setProviderUniqueId(uniqueId);
            model.setProviderResponse(providerResponse);
            model.setJsonObject(queueobj.toString());
            model.setImagePathList(imagePathList);
            model.setMessage(txt_addnote);
            if (serviceInfo.isUser()) {
                model.setAccountId(userId);
            } else {
                model.setAccountId(providerId);
            }
            model.setServiceInfo(serviceInfo);
            model.setmSearchTerminology(mSearchTerminology);
            model.setFamilyEMIID(familyMEmID);
            model.setPhoneNumber(phoneNumber);
            //model.setQuestionnaire(questionnaire);
            model.setFrom(Constants.APPOINTMENT);
            model.setProviderName(providerName);
            model.setAccountBusinessName(accountBusinessName);
            model.setLocationName(locationName);
            model.setDate(s.getDate());
            model.setTime(convertedTime);
            model.setCustomerName(tvConsumerName.getText().toString());
            model.setEmailId(tvEmail.getText().toString());
            model.setCountryCode(countryCode);
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
            //model.setJacshSelected(cbJCash.isChecked());
            if (advancePaymentDetails != null) {
                model.setAmountRequiredNow(advancePaymentDetails.getAmountRequiredNow());
                model.setNetTotal(advancePaymentDetails.getNetTotal() + sQnrPrice);
            }
            if (advancePaymentDetails != null && advancePaymentDetails.getEligibleJcashAmt() != null) {
                model.setEligibleJcashAmt(advancePaymentDetails.getEligibleJcashAmt().get("jCashAmt").getAsDouble());
            }
            bookingModels.add(model);
        }
        if (serviceInfo.isUser()) {

            getMltpleApptQuestionnaire(serviceInfo.getServiceId(), userId, txt_addnote, bookingModels);
        } else {
            getMltpleApptQuestionnaire(serviceInfo.getServiceId(), providerId, txt_addnote, bookingModels);
        }
    }

    private void ApiAppointment(final String txt_addnote, int id) {

        final Dialog mDialog = Config.getProgressDialog(AppointmentActivity.this, AppointmentActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        String number = tvNumber.getText().toString();
        uuid = UUID.randomUUID().toString();
        //String virtual_code = et_countryCode.getText().toString();
        String countryVirtualCode = "";
        /*if (!virtual_code.equalsIgnoreCase("")) {
            countryVirtualCode = virtual_code.substring(1);*/
        if (virtual_NmbrCCPicker.getSelectedCountryCode() != null) {
            countryVirtualCode = virtual_NmbrCCPicker.getSelectedCountryCode();
        } else {
            DynamicToast.make(AppointmentActivity.this, "Countrycode needed", AppCompatResources.getDrawable(
                            AppointmentActivity.this, R.drawable.ic_info_black),
                    ContextCompat.getColor(AppointmentActivity.this, R.color.white), ContextCompat.getColor(AppointmentActivity.this, R.color.green), Toast.LENGTH_SHORT).show();
            mDialog.dismiss();
            return;
        }

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

        JSONObject jsonObj2 = new JSONObject();
        JSONObject jsonObj3 = new JSONObject();

        try {

            queueobj.put("appmtDate", apiDate);
            sjsonobj.put("id", scheduleId);
            queueobj.put("consumerNote", txt_addnote);
            queueobj.put("phoneNumber", phoneNumber);
            queueobj.put("countryCode", countryCode);
            if (serviceInfo.isUser()) {
                pjsonobj.put("id", providerId);
            } else {
                pjsonobj.put("id", 0);
            }


            if (etVirtualNumber.getText().toString().trim().length() >= 7) {
                if (serviceInfo.getCallingMode() != null && serviceInfo.getCallingMode().equalsIgnoreCase("whatsApp")) {
                    virtualService.put("WhatsApp", countryVirtualCode + etVirtualNumber.getText());
                    mWhtsappCountryCode = countryVirtualCode;
                    mWhatsappNumber = etVirtualNumber.getText().toString();
                } else if (serviceInfo.getCallingMode() != null && serviceInfo.getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                    virtualService.put("GoogleMeet", serviceInfo.getVirtualCallingValue());
                } else if (serviceInfo.getCallingMode() != null && serviceInfo.getCallingMode().equalsIgnoreCase("Zoom")) {
                    virtualService.put("Zoom", serviceInfo.getVirtualCallingValue());
                } else if (serviceInfo.getCallingMode() != null && serviceInfo.getCallingMode().equalsIgnoreCase("Phone")) {
                    virtualService.put("Phone", countryVirtualCode + etVirtualNumber.getText());
                    phoneNumber = etVirtualNumber.getText().toString();
                } else if (serviceInfo.getCallingMode() != null && serviceInfo.getCallingMode().equalsIgnoreCase("VideoCall")) {
                    virtualService.put("VideoCall", serviceInfo.getVirtualCallingValue());
                }
            } else {

                String modeOfCalling = "";
                if (serviceInfo.getCallingMode() != null && serviceInfo.getCallingMode().equalsIgnoreCase("whatsApp")) {
                    if (etVirtualNumber.getText().toString().trim().equalsIgnoreCase("")) {
                        modeOfCalling = "Enter WhatsApp number";
                    } else {
                        modeOfCalling = "Invalid WhatsApp number";
                    }
                } else if (etVirtualNumber.getText().toString().trim().length() < 7 || (virtual_NmbrCCPicker.getSelectedCountryCode().equalsIgnoreCase("91") && etVirtualNumber.getText().toString().trim().length() != 10)) {
                    modeOfCalling = "Invalid Contact number";
                }
                DynamicToast.make(AppointmentActivity.this, modeOfCalling, AppCompatResources.getDrawable(
                                AppointmentActivity.this, R.drawable.ic_info_black),
                        ContextCompat.getColor(AppointmentActivity.this, R.color.white), ContextCompat.getColor(AppointmentActivity.this, R.color.green), Toast.LENGTH_SHORT).show();
                mDialog.dismiss();
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
                if (mGender != null && !mGender.isEmpty()) {
                    waitobj.put("gender", mGender);
                }
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

        if (mDialog.isShowing())
            Config.closeDialog(getParent(), mDialog);

        if (serviceInfo.isUser()) {

            getQuestionnaire(serviceInfo.getServiceId(), userId, queueobj, txt_addnote);
        } else {
            getQuestionnaire(serviceInfo.getServiceId(), providerId, queueobj, txt_addnote);
        }

        MultiplefamilyList.clear();
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
                        model.setAppointmentType(Constants.SINGLE_APPOINTMENT);
                        model.setProviderUniqueId(uniqueId);
                        model.setProviderResponse(providerResponse);
                        model.setJsonObject(queueobj.toString());
                        model.setImagePathList(imagePathList);
                        model.setMessage(txt_addnote);
                        model.setAccountId(accountId);
                        model.setServiceInfo(serviceInfo);
                        model.setmSearchTerminology(mSearchTerminology);
                        model.setFamilyEMIID(familyMEmID);
                        model.setCountryCode(countryCode);
                        model.setPhoneNumber(phoneNumber);
                        model.setQuestionnaire(questionnaire);
                        model.setFrom(Constants.APPOINTMENT);
                        model.setProviderName(providerName);
                        model.setAccountBusinessName(accountBusinessName);
                        model.setLocationName(locationName);
                        model.setDate(tvDate.getText().toString());
                        model.setTime(tvTime.getText().toString());
                        model.setCustomerName(tvConsumerName.getText().toString());
                        model.setEmailId(tvEmail.getText().toString());
                        model.setWhtsappCountryCode(mWhtsappCountryCode);
                        model.setWhtsappPhoneNumber(mWhatsappNumber);
                        if (serviceInfo.getServiceBookingType() != null && !serviceInfo.getServiceBookingType().isEmpty()
                                && (serviceInfo.getServiceBookingType().equalsIgnoreCase(Constants.SERVICE_BOOKING_TYPE_BOOKING)
                                || serviceInfo.getServiceBookingType().equalsIgnoreCase(Constants.SERVICE_BOOKING_TYPE_REQUEST))) {
                            model.setServiceBookingType(serviceInfo.getServiceBookingType());
                        }
                        if (providerLogoUrl != null && !providerLogoUrl.trim().isEmpty()) {
                            model.setProviderLogo(providerLogoUrl);
                        }
                        String pCountryCode = providerResponse.getBusinessProfile().getCountryCode();
                        String pPhNo = providerResponse.getBusinessProfile().getAccountLinkedPhNo();
                        if ((pCountryCode != null) && (!pCountryCode.isEmpty()) && (pPhNo != null) && (!pPhNo.isEmpty())) {
                            model.setProviderPhoneNumber(pCountryCode + " " + pPhNo);
                        }
                        //model.setJacshSelected(cbJCash.isChecked());
                        if (advancePaymentDetails != null) {
                            model.setAmountRequiredNow(advancePaymentDetails.getAmountRequiredNow());
                            model.setNetTotal(advancePaymentDetails.getNetTotal() + sQnrPrice);
                        }
                        if (advancePaymentDetails != null && advancePaymentDetails.getEligibleJcashAmt() != null) {
                            model.setEligibleJcashAmt(advancePaymentDetails.getEligibleJcashAmt().get("jCashAmt").getAsDouble());
                        }
                        if (questionnaire != null) {

                            if (questionnaire.getQuestionsList() != null) {

                                Intent intent = new Intent(AppointmentActivity.this, CustomQuestionnaire.class);
                                intent.putExtra("data", model);
                                intent.putExtra("from", Constants.APPOINTMENT);
                                startActivity(intent);

                            } else {
                                Intent intent = new Intent(AppointmentActivity.this, ReconfirmationActivity.class);
                                intent.putExtra("data", model);
                                startActivity(intent);
                            }
                        } else {

                            Intent intent = new Intent(AppointmentActivity.this, ReconfirmationActivity.class);
                            intent.putExtra("data", model);
                            startActivity(intent);
                        }
                        //}
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

    private void getMltpleApptQuestionnaire(int serviceId, int accountId, String txt_addnote, ArrayList<BookingModel> bookingModels) {
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
                        if (questionnaire != null) {
                            bookingModels.get(bookingModels.size() - 1).setQuestionnaire(questionnaire);
                            if (questionnaire.getQuestionsList() != null) {

                                Intent intent = new Intent(AppointmentActivity.this, CustomQuestionnaire.class);
                                intent.putExtra("datas", bookingModels);
                                intent.putExtra("from", Constants.APPOINTMENT);
                                startActivity(intent);

                            } else {
                                Intent intent = new Intent(AppointmentActivity.this, ReconfirmationMultipleApptActivity.class);
                                intent.putExtra("datas", bookingModels);
                                startActivity(intent);
                            }
                        } else {
                            Intent intent = new Intent(AppointmentActivity.this, ReconfirmationMultipleApptActivity.class);
                            intent.putExtra("datas", bookingModels);
                            startActivity(intent);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Questionnaire> call, Throwable t) {

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

                secondWord = monthString + " " + day + ", " + nextAvailableTime;

            } else {
                secondWord = "Today, " + nextAvailableTime;
            }
        } else {
            firstWord = "Est wait time";
            secondWord = Config.getTimeinHourMinutes(Integer.parseInt(estTime));
        }
        return firstWord + "-" + secondWord;
    }

    @Override
    public void sendSlotInfo(List<SelectedSlotDetail> selectedSlotDetails) {
        if (selectedSlotDetails == null) {
            llAppointment.setVisibility(View.GONE);
            slotTime = null;
        } else {
            llAppointment.setVisibility(View.VISIBLE);
            if (selectedSlotDetails.size() == 1) {
                try {
                    this.selectedSlotDetails = selectedSlotDetails;

                    // getting data from dialog
                    String convertedTime = selectedSlotDetails.get(0).getDisplayTime().replace("am", "AM").replace("pm", "PM");
                    tvTime.setText(convertedTime);
                    tvDate.setText(selectedSlotDetails.get(0).getDate());
                    tvSelectedDateHint.setText("Selected Time slot");
                    scheduleId = selectedSlotDetails.get(0).getScheduleId();
                    slotTime = selectedSlotDetails.get(0).getSlotTime();
                    try {
                        apiDate = getApiDateFormat(selectedSlotDetails.get(0).getCalendarDate());  // to convert selected date to api date format
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (serviceInfo.isUser()) {
                        getAdvancePaymentDetails(userMessage, userId);
                    } else {
                        getAdvancePaymentDetails(userMessage, providerId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                String time = selectedSlotDetails
                        .stream()
                        .map(a -> String.valueOf(a.getDisplayTime().replaceAll(" ", "\u00a0")))
                        .collect(Collectors.joining(", "));
                String convertedTime = time.replaceAll("am", "AM").replaceAll("pm", "PM");
                if (selectedSlotDetails.size() > 2) {
                    tvTime.setTextSize(20);
                }
                tvTime.setText(convertedTime);
                tvDate.setText(selectedSlotDetails.get(0).getDate());
                tvSelectedDateHint.setText("Selected Time slot");
                this.selectedSlotDetails = selectedSlotDetails;
            }
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
        emailId = SharedPreference.getInstance(mContext).getStringValue("email", "");
        tvEmail.setText(emailId);
        //  ApiGetProfileDetail();
    }

    @Override
    public void mailUpdated(String emailId) {

    }


    static ArrayList<FamilyArrayModel> MultiplefamilyList = new ArrayList<>();


    @Override
    public void refreshMultipleMEmList(ArrayList<FamilyArrayModel> familyList) {
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

    @Override
    public void mobileUpdated() {
        phoneNumber = SharedPreference.getInstance(mContext).getStringValue("mobile", "");
        tvNumber.setText(phoneNumber);
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


    @Override
    public void getMessage(String message) {

        if (message != null) {
            userMessage = message;
        }

    }

    private void showToolTip() {

        CustomToolTip tipWindow = new CustomToolTip(AppointmentActivity.this, CustomToolTip.DRAW_TOP, "Please add notes");
        tipWindow.showToolTip(cvAddNote, CustomToolTip.DRAW_ARROW_DEFAULT_CENTER, false);
    }

    @Override
    public void sendFamilyMemberDetails(int fmemId, String firstName, String lastName, String phone, String email, String conCode, String whtsappCountryCode, String whatsappNumber, String telegramCountryCode, String telegramNumber, String age, JSONArray preferredLanguages, JSONObject bookingLocation, String gender) {

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
        //et_countryCode.setText(countryCode);
        if (serviceInfo.isUser()) {
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
        MultiplefamilyList.addAll(familyList);

    }

    @Override
    public void SelectedPincodeLocation(PincodeLocationsResponse selectedPincodeLocation) {

    }

    @Override
    public void cpns(ArrayList<String> mcouponArraylist) {
        iCpn = (ICpn) this;
        couponArraylist = mcouponArraylist;
        if (userMessage != null) {
            if (serviceInfo.isUser()) {
                getAdvancePaymentDetails(userMessage, userId);
            } else {
                getAdvancePaymentDetails(userMessage, providerId);
            }
        }
    }

    public AdvancePaymentDetails getAdvancePaymentDetails(final String txt_addnote, int id) {
        final Dialog mDialog = Config.getProgressDialog(AppointmentActivity.this, AppointmentActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        String number = tvNumber.getText().toString();
        uuid = UUID.randomUUID().toString();

        ApiInterface apiService = ApiClient.getClient(AppointmentActivity.this).create(ApiInterface.class);

        JSONObject queueobj = new JSONObject();
        JSONObject waitobj = new JSONObject();
        JSONObject waitobj1 = new JSONObject();
        JSONObject sejsonobj = new JSONObject();
        JSONArray waitlistArray = new JSONArray();
        JSONObject sjsonobj = new JSONObject();
        JSONObject virtualService = new JSONObject();
        JSONObject pjsonobj = new JSONObject();


        try {

            queueobj.put("appmtDate", apiDate);
            sjsonobj.put("id", scheduleId);
            queueobj.put("consumerNote", txt_addnote);
            queueobj.put("phoneNumber", phoneNumber);
            queueobj.put("countryCode", countryCode);
            if (serviceInfo.isUser()) {
                pjsonobj.put("id", providerId);
            } else {
                pjsonobj.put("id", 0);
            }


            sejsonobj.put("id", serviceInfo.getServiceId());
            sejsonobj.put("serviceType", serviceInfo.getServiceType());

            JSONArray couponList = new JSONArray();

            for (int i = 0; i < couponArraylist.size(); i++) {
                couponList.put(couponArraylist.get(i));
            }
            queueobj.put("coupons", couponList);
            Log.i("couponList", couponList.toString());

            if (familyMEmID == 0) {
                familyMEmID = consumerID;
            }

            if (MultiplefamilyList.size() > 0) {
                for (int i = 0; i < MultiplefamilyList.size(); i++) {

                    waitobj1.put("id", MultiplefamilyList.get(i).getId());
                    waitobj1.put("firstName", MultiplefamilyList.get(i).getFirstName());
                    waitobj1.put("lastName", MultiplefamilyList.get(i).getLastName());
                    waitobj1.put("apptTime", slotTime);

                    waitlistArray.put(waitobj1);
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

            queueobj.putOpt("schedule", sjsonobj);
            queueobj.putOpt("service", sejsonobj);
            queueobj.putOpt("appmtFor", waitlistArray);

            queueobj.putOpt("provider", pjsonobj);

            if (serviceInfo.getServiceType().equalsIgnoreCase("virtualService")) {
                queueobj.putOpt("virtualService", virtualService);
            }

        } catch (
                JSONException e) {
            e.printStackTrace();
        }

        Log.i("QueueObj Appointment", queueobj.toString());
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queueobj.toString());
        //Call<AdvancePaymentDetails> call = apiService.getApptAdvancePaymentDetails(String.valueOf(id), body);
        Call<AdvancePaymentDetails> call = apiService.getAdvancePaymentDetails("appointment", String.valueOf(id), body);

        call.enqueue(new Callback<AdvancePaymentDetails>() {
            @Override
            public void onResponse(Call<AdvancePaymentDetails> call, Response<AdvancePaymentDetails> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    Config.logV("Response--code-------------------------" + response.body());

                    MultiplefamilyList.clear();
                    if (response.code() == 200) {
                        advancePaymentDetails = response.body();
                        if (couponEntered != null) {
                            Config.logV("couponArraylist--code-------------------------" + couponArraylist);
                            list.setVisibility(View.VISIBLE);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AppointmentActivity.this);
                            list.setLayoutManager(mLayoutManager);
                            mAdapter = new CouponlistAdapter(AppointmentActivity.this, s3couponList, couponEntered, couponArraylist, advancePaymentDetails, iCpn);
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
    /*private void ApiEditProfileDetail() {


        ApiInterface apiService =
                ApiClient.getClient(context).create(ApiInterface.class);

        final int consumerId = SharedPreference.getInstance(context).getIntValue("consumerId", 0);
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("id", consumerId);
            jsonObj.put("firstName", profileDetails.getUserprofile().getFirstName());
            jsonObj.put("lastName", profileDetails.getUserprofile().getLastName());

            jsonObj.put("email", et_email.getText().toString());
            if (gender != null) {
                jsonObj.put("gender", gender);
            }


            jsonObj.put("dob", profileDetails.getUserprofile().getDob());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call = apiService.getEditProfileDetail(body);
//        Config.logV("Request--BODY-------------------------" + new Gson().toJson(jsonObj.toString()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

//                    if (mDialog.isShowing())
//                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        if (response.body().string().equalsIgnoreCase("true")) {
                            SharedPreference.getInstance(context).setValue("email", et_email.getText().toString());
                            if (!multiple) {
                                ApiGetOneTimeQNR();
                            } else {
                                Toast.makeText(context, "Details saved successfully ", Toast.LENGTH_LONG).show();
                                iFamilyMemberDetails.sendFamilyMemberDetails(memId, firstName, lastName, phone, email, countryCode);
                                iFamillyListSelected.CheckedFamilyList(checkedfamilyList);
                                dismiss();
                            }


                        }

                    } else {

                        Toast.makeText(context, response.errorBody().string(), Toast.LENGTH_LONG).show();

                        Config.logV("Error" + response.errorBody().string());

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
//                if (mDialog.isShowing())
//                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }*/
}