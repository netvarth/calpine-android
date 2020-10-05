package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
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
import com.jaldeeinc.jaldee.Interface.ISlotInfo;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.CouponlistAdapter;
import com.jaldeeinc.jaldee.adapter.MultipleFamilyMemberAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.EmailEditWindow;
import com.jaldeeinc.jaldee.custom.MobileNumberDialog;
import com.jaldeeinc.jaldee.custom.SlotsDialog;
import com.jaldeeinc.jaldee.model.FamilyArrayModel;
import com.jaldeeinc.jaldee.model.RazorpayModel;
import com.jaldeeinc.jaldee.payment.PaymentGateway;
import com.jaldeeinc.jaldee.payment.PaytmPayment;
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
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentActivity extends AppCompatActivity implements PaymentResultWithDataListener, ISlotInfo, IMailSubmit, IPaymentResponse, IMobileSubmit {

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
    ActiveCheckIn activeAppointment = new ActiveCheckIn();
    static Activity mActivity;
    static Context mContext;
    String apiDate = "";
    private int userId;
    String sector, subsector;
    int maxPartysize;
    SearchViewDetail mBusinessDataList;
    ArrayList<PaymentModel> mPaymentData = new ArrayList<>();


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
                // ApiGenerateHash();
                if (tvNumber.length() < 10) {
                    Toast.makeText(AppointmentActivity.this, "Mobile number should have 10 digits" + "", Toast.LENGTH_SHORT).show();
                } else {

//                    if (enableparty) {
//                        if (Integer.parseInt(editpartysize.getText().toString()) > maxPartysize) {
//                            Toast.makeText(mContext, "Sorry, Max party size allowed is " + maxPartysize, Toast.LENGTH_LONG).show();
//                        } else {
//                            ApiAppointment(txt_message);
//                        }
//                    } else {

                    if (serviceInfo.getIsPrePayment().equalsIgnoreCase("true")) {
                        if (tvEmail.getText().toString() != null && tvEmail.getText().length() > 0) {

                            if (serviceInfo.isUser()) {
                                ApiAppointment("message", userId);
                            } else {
                                ApiAppointment("message", providerId);
                            }

                        } else {


                        }
                    } else {

                        if (serviceInfo.isUser()) {
                            ApiAppointment("message", userId);
                        } else {
                            ApiAppointment("message", providerId);
                        }
                    }
//                    }
                }
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
                DynamicToast.make(AppointmentActivity.this, "Virtual service number is invalid", AppCompatResources.getDrawable(
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

//                                            if (imagePathList.size() > 0) {
//                                                ApiCommunicateAppointment(value, String.valueOf(accountID), txt_addnote, dialog);
//                                            }
                                        }
                                    });

                                    btn_paytm.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            PaytmPayment payment = new PaytmPayment(AppointmentActivity.this, paymentResponse);
                                            payment.ApiGenerateHashPaytm(value, serviceInfo.getMinPrePaymentAmount(), String.valueOf(id), Constants.PURPOSE_PREPAYMENT, AppointmentActivity.this, AppointmentActivity.this, "", familyMEmID);
                                            //payment.generateCheckSum(sAmountPay);
                                            dialog.dismiss();

//                                            if (imagePathList.size() > 0) {
//                                                ApiCommunicateAppointment(value, String.valueOf(accountID), txt_addnote, dialog);
//                                            }
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                        } else {
                            //  txt_message ="Please find the attachment from Consumer with this message";
//                            if (imagePathList.size() > 0) {
//                                ApiCommunicateAppointment(value, String.valueOf(accountID), txt_addnote, dialog);
//                            }

                            if (serviceInfo.isUser()) {
                                getConfirmationDetails(userId);

                            } else {
                                getConfirmationDetails(providerId);

                            }
                        }


                        if (serviceInfo.getLivetrack().equalsIgnoreCase("true")) {
                            Intent checkinShareLocations = new Intent(AppointmentActivity.this, CheckinShareLocationAppointment.class);
                            checkinShareLocations.putExtra("waitlistPhonenumber", phoneNumber);
                            checkinShareLocations.putExtra("uuid", value);
                            checkinShareLocations.putExtra("accountID", locationId);
                            checkinShareLocations.putExtra("title", providerName);
                            checkinShareLocations.putExtra("terminology", mSearchTerminology.getWaitlist());
                            checkinShareLocations.putExtra("calcMode", calcMode);
                            checkinShareLocations.putExtra("queueStartTime", "");
                            checkinShareLocations.putExtra("queueEndTime", "");
                            checkinShareLocations.putExtra("from", "appt");
                            startActivity(checkinShareLocations);
                        }


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

    private void getConfirmationDetails(int userId) {

        final ApiInterface apiService =
                ApiClient.getClient(AppointmentActivity.this).create(ApiInterface.class);
        Call<ActiveCheckIn> call = apiService.getActiveAppointmentUUID(value, String.valueOf(userId));
        call.enqueue(new Callback<ActiveCheckIn>() {
            @Override
            public void onResponse(Call<ActiveCheckIn> call, Response<ActiveCheckIn> response) {
                try {
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        activeAppointment = response.body();
                        if (activeAppointment != null) {

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
            public void onFailure(Call<ActiveCheckIn> call, Throwable t) {
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
            tvTime.setText(displayTime);
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
            getConfirmationDetails(userId);

        } else {
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

            getConfirmationDetails(userId);

        } else {
            getConfirmationDetails(providerId);

        }
    }

    @Override
    public void mobileUpdated() {
        String phone = SharedPreference.getInstance(mContext).getStringValue("mobile", "");
        tvNumber.setText(phone);
    }
}