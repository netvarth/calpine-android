package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jaldeeinc.jaldee.Interface.IMailSubmit;
import com.jaldeeinc.jaldee.Interface.IPaymentResponse;
import com.jaldeeinc.jaldee.Interface.ISelectQ;
import com.jaldeeinc.jaldee.Interface.ISlotInfo;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.CouponlistAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CheckInSlotsDialog;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.EmailEditWindow;
import com.jaldeeinc.jaldee.custom.MobileNumberDialog;
import com.jaldeeinc.jaldee.custom.SlotsDialog;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.AvailableSlotsData;
import com.jaldeeinc.jaldee.response.CoupnResponse;
import com.jaldeeinc.jaldee.response.PaymentModel;
import com.jaldeeinc.jaldee.response.ProfileModel;
import com.jaldeeinc.jaldee.response.SearchService;
import com.jaldeeinc.jaldee.response.SearchSetting;
import com.jaldeeinc.jaldee.response.SearchTerminology;
import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.jaldeeinc.jaldee.response.SectorCheckin;
import com.jaldeeinc.jaldee.response.ServiceInfo;
import com.jaldeeinc.jaldee.response.SlotsData;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckInActivity extends AppCompatActivity {

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
    private SearchService checkInInfo = new SearchService();
    SearchTerminology mSearchTerminology;
    ProfileModel profileDetails;
    ArrayList<SlotsData> slotsData = new ArrayList<SlotsData>();
    ArrayList<AvailableSlotsData> activeSlotsList = new ArrayList<>();
    private CheckInSlotsDialog slotsDialog;
    private ISelectQ iSelectQ;
    String uuid;
    String value = null;
    String couponEntered;
    private int scheduleId;
    SearchViewDetail mBusinessDataList;
    private EmailEditWindow emailEditWindow;
    private MobileNumberDialog mobileNumberDialog;
    private IMailSubmit iMailSubmit;
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
    int maxPartysize;
    ArrayList<PaymentModel> mPaymentData = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        ButterKnife.bind(CheckInActivity.this);
        mActivity = this;
        mContext = this;
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
        list = findViewById(R.id.list);
        recycle_family = findViewById(R.id.recycle_family);

        if (checkInInfo != null) {
            tvServiceName.setText(checkInInfo.getName());
            tvDescription.setText(checkInInfo.getDescription());
            llCheckIn.setVisibility(View.VISIBLE);
            llAppointment.setVisibility(View.GONE);
            if (checkInInfo.getCheckInServiceAvailability() != null) {
                String time = getWaitingTime(checkInInfo.getCheckInServiceAvailability().getAvailableDate(), checkInInfo.getCheckInServiceAvailability().getServiceTime(), checkInInfo.getCheckInServiceAvailability().getQueueWaitingTime());
                tvCheckInDate.setText(time.split("-")[1]);
                tvHint.setText(time.split("-")[0]);

                if (checkInInfo.getCheckInServiceAvailability().getPersonAhead() >= 0) {
                    if (checkInInfo.getCheckInServiceAvailability().getPersonAhead() == 0) {
                        tvPeopleInLine.setText("Be the first in line");
                    } else if (checkInInfo.getCheckInServiceAvailability().getPersonAhead() == 1) {
                        tvPeopleInLine.setText(checkInInfo.getCheckInServiceAvailability().getPersonAhead() + "  person waiting in line");
                    } else {
                        tvPeopleInLine.setText(checkInInfo.getCheckInServiceAvailability().getPersonAhead() + "  people waiting in line");
                    }
                }

                if (checkInInfo.getCheckInServiceAvailability().getAvailableDate() != null) {
                    apiDate = checkInInfo.getCheckInServiceAvailability().getAvailableDate();
                    scheduleId = checkInInfo.getCheckInServiceAvailability().getId();
                    if (checkInInfo.getCheckInServiceAvailability().getServiceTime() != null) {
                        tvTime.setText(checkInInfo.getCheckInServiceAvailability().getServiceTime());
                        slotTime = checkInInfo.getCheckInServiceAvailability().getServiceTime();
                    }
                }
            }


            if (checkInInfo.getServiceType() != null && checkInInfo.getServiceType().equalsIgnoreCase("virtualService")) {

                if (checkInInfo.getVirtualCallingModes().get(0).getCallingMode() != null) {

                    if (checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp") || checkInInfo.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Phone")) {

                        llVirtualNumber.setVisibility(View.VISIBLE);
                    } else {
                        llVirtualNumber.setVisibility(View.GONE);
                    }
                } else {
                    llVirtualNumber.setVisibility(View.GONE);
                }
            } else {
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

                String mailId = "";
                if (tvEmail.getText().toString() != null) {
                    mailId = tvEmail.getText().toString();
                }
                emailEditWindow = new EmailEditWindow(CheckInActivity.this, profileDetails, iMailSubmit, mailId);
                emailEditWindow.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                emailEditWindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
                emailEditWindow.show();
                DisplayMetrics metrics = CheckInActivity.this.getResources().getDisplayMetrics();
                int width = (int) (metrics.widthPixels * 1);
                emailEditWindow.getWindow().setGravity(Gravity.BOTTOM);
                emailEditWindow.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        });

        tvNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tvNumber.getText().toString() != null) {
                    mobileNumberDialog = new MobileNumberDialog(CheckInActivity.this, profileDetails, iMailSubmit, tvNumber.getText().toString());
                    mobileNumberDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                    mobileNumberDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    mobileNumberDialog.show();
                    DisplayMetrics metrics = CheckInActivity.this.getResources().getDisplayMetrics();
                    int width = (int) (metrics.widthPixels * 1);
                    mobileNumberDialog.getWindow().setGravity(Gravity.BOTTOM);
                    mobileNumberDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                }
            }
        });

        llEditDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent familyIntent = new Intent(CheckInActivity.this, CheckinFamilyMember.class);
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
                            String firstWord = "Prepayment Amount: ";
                            String secondWord = "₹ " + Config.getAmountinTwoDecimalPoints(Double.parseDouble(checkInInfo.getMinPrePaymentAmount()));
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

        return firstWord + "-" + secondWord;
    }


    public static void refreshName(String name, int memID) {
        Config.logV("NAme----------" + name);
        if (name != null && !name.equalsIgnoreCase("")) {
            tvConsumerName.setText(name);
            familyMEmID = memID;
        }
    }

}