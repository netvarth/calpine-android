//package com.jaldeeinc.jaldee.activities;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.cardview.widget.CardView;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//
//import com.google.android.material.bottomsheet.BottomSheetDialog;
//import com.jaldeeinc.jaldee.Interface.IMailSubmit;
//import com.jaldeeinc.jaldee.Interface.IPaymentResponse;
//import com.jaldeeinc.jaldee.Interface.ISlotInfo;
//import com.jaldeeinc.jaldee.R;
//import com.jaldeeinc.jaldee.adapter.CouponlistAdapter;
//import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
//import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
//import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
//import com.jaldeeinc.jaldee.custom.EmailEditWindow;
//import com.jaldeeinc.jaldee.custom.MobileNumberDialog;
//import com.jaldeeinc.jaldee.custom.SlotsDialog;
//import com.jaldeeinc.jaldee.response.ActiveCheckIn;
//import com.jaldeeinc.jaldee.response.AvailableSlotsData;
//import com.jaldeeinc.jaldee.response.CoupnResponse;
//import com.jaldeeinc.jaldee.response.PaymentModel;
//import com.jaldeeinc.jaldee.response.ProfileModel;
//import com.jaldeeinc.jaldee.response.SearchService;
//import com.jaldeeinc.jaldee.response.SearchTerminology;
//import com.jaldeeinc.jaldee.response.ServiceInfo;
//import com.jaldeeinc.jaldee.response.SlotsData;
//import com.jaldeeinc.jaldee.utils.SharedPreference;
//
//import java.util.ArrayList;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
//public class CheckInActivity extends AppCompatActivity {
//
//    @BindView(R.id.tv_providerName)
//    CustomTextViewSemiBold tvProviderName;
//
//    @BindView(R.id.tv_serviceName)
//    CustomTextViewBold tvServiceName;
//
//    @BindView(R.id.tv_description)
//    CustomTextViewMedium tvDescription;
//
//    @BindView(R.id.tv_date)
//    CustomTextViewBold tvDate;
//
//    @BindView(R.id.tv_time)
//    CustomTextViewBold tvTime;
//
//    @BindView(R.id.tv_changeTime)
//    CustomTextViewBold tvChangeTime;
//
//    @BindView(R.id.ll_editDetails)
//    LinearLayout llEditDetails;
//
//    static CustomTextViewBold tvConsumerName;
//
//    @BindView(R.id.tv_number)
//    CustomTextViewMedium tvNumber;
//
//    @BindView(R.id.tv_email)
//    CustomTextViewMedium tvEmail;
//
//    @BindView(R.id.cv_addNote)
//    CardView cvAddNote;
//
//    @BindView(R.id.cv_attachFile)
//    CardView cvAttachFile;
//
//    @BindView(R.id.cv_submit)
//    CardView cvSubmit;
//
//    @BindView(R.id.ll_appoint)
//    LinearLayout llAppointment;
//
//    @BindView(R.id.ll_checkIn)
//    LinearLayout llCheckIn;
//
//    @BindView(R.id.tv_checkInDate)
//    CustomTextViewBold tvCheckInDate;
//
//    @BindView(R.id.tv_peopleInLine)
//    CustomTextViewBold tvPeopleInLine;
//
//    @BindView(R.id.tv_hint)
//    CustomTextViewSemiBold tvHint;
//
//    @BindView(R.id.tv_selectedDateHint)
//    CustomTextViewSemiBold tvSelectedDateHint;
//
//    @BindView(R.id.ll_virtualNumber)
//    LinearLayout llVirtualNumber;
//
//    @BindView(R.id.et_virtualNumber)
//    EditText etVirtualNumber;
//
//    @BindView(R.id.tv_applyCode)
//    CustomTextViewSemiBold tvApplyCode;
//
//    @BindView(R.id.rl_coupon)
//    RelativeLayout rlCoupon;
//
//    @BindView(R.id.et_code)
//    EditText etCode;
//
//    @BindView(R.id.tv_apply)
//    CustomTextViewBold tvApply;
//
//    @BindView(R.id.txtprepay)
//    CustomTextViewMedium txtprepay;
//
//    @BindView(R.id.txtprepayamount)
//    CustomTextViewMedium txtprepayamount;
//
//    @BindView(R.id.LservicePrepay)
//    LinearLayout LservicePrepay;
//
//    @BindView(R.id.cv_back)
//    CardView cvBack;
//    static Activity mActivity;
//    static Context mContext;
//    String mFirstName, mLastName;
//    int consumerID;
//    private int uniqueId;
//    private String providerName;
//    private int providerId;
//    private int locationId;
//    private int serviceId;
//    private String serviceName;
//    private String phoneNumber;
//    private String serviceDescription;
//    private SearchService checkInInfo = new SearchService();
//    SearchTerminology mSearchTerminology;
//    ProfileModel profileDetails;
//    ArrayList<SlotsData> slotsData = new ArrayList<SlotsData>();
//    ArrayList<AvailableSlotsData> activeSlotsList = new ArrayList<>();
//    private SlotsDialog slotsDialog;
//    private ISlotInfo iSlotInfo;
//    String uuid;
//    String value = null;
//    String couponEntered;
//    private int scheduleId;
//    private EmailEditWindow emailEditWindow;
//    private MobileNumberDialog mobileNumberDialog;
//    private IMailSubmit iMailSubmit;
//    ArrayList<String> couponArraylist = new ArrayList<>();
//    ArrayList<CoupnResponse> s3couponList = new ArrayList<>();
//    RecyclerView list;
//    private CouponlistAdapter mAdapter;
//    static int familyMEmID;
//    static RecyclerView recycle_family;
//    String slotTime;
//    BottomSheetDialog dialog;
//    private IPaymentResponse paymentResponse;
//    String calcMode;
//    ActiveCheckIn activeAppointment = new ActiveCheckIn();
//    String apiDate = "";
//    private int userId;
//    ArrayList<PaymentModel> mPaymentData = new ArrayList<>();
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_check_in);
//        ButterKnife.bind(CheckInActivity.this);
//        mActivity = this;
//        mContext = this;
//        // getting necessary details from intent
//        Intent intent = getIntent();
//        uniqueId = intent.getIntExtra("uniqueID", 0);
//        providerName = intent.getStringExtra("providerName");
//        checkInInfo = (SearchService) intent.getSerializableExtra("checkInInfo");
//        locationId = intent.getIntExtra("locationId", 0);
//        providerId = intent.getIntExtra("providerId", 0);
//        userId = intent.getIntExtra("userId", 0);
//        tvConsumerName = findViewById(R.id.tv_consumerName);
//        list = findViewById(R.id.list);
//        recycle_family = findViewById(R.id.recycle_family);
//
//        if (checkInInfo != null) {
//            tvServiceName.setText(checkInInfo.getName());
//            tvDescription.setText(checkInInfo.getDescription());
//
//           if (serviceInfo.getType().equalsIgnoreCase(Constants.CHECKIN)) {
//               llCheckIn.setVisibility(View.VISIBLE);
//               llAppointment.setVisibility(View.GONE);
//               String time = getWaitingTime(serviceInfo.getAvailableDate(), serviceInfo.getTime(), serviceInfo.getWaitingTime());
//               tvCheckInDate.setText(time.split("-")[1]);
//               tvHint.setText(time.split("-")[0]);
//
//               if (serviceInfo.getPeopleWaitingInLine() >= 0) {
//                   if (serviceInfo.getPeopleWaitingInLine() == 0) {
//                       tvPeopleInLine.setText("Be the first in line");
//                   } else if (serviceInfo.getPeopleWaitingInLine() == 1) {
//                       tvPeopleInLine.setText(serviceInfo.getPeopleWaitingInLine() + "  person waiting in line");
//                   } else {
//                       tvPeopleInLine.setText(serviceInfo.getPeopleWaitingInLine() + "  people waiting in line");
//                   }
//               }
//           }
//
//
//            if (serviceInfo.getServiceType() != null && serviceInfo.getServiceType().equalsIgnoreCase("virtualService")) {
//
//                if (serviceInfo.getCallingMode() != null) {
//
//                    if (serviceInfo.getCallingMode().equalsIgnoreCase("WhatsApp") || serviceInfo.getCallingMode().equalsIgnoreCase("Phone")) {
//
//                        llVirtualNumber.setVisibility(View.VISIBLE);
//                    } else {
//                        llVirtualNumber.setVisibility(View.GONE);
//                    }
//                } else {
//                    llVirtualNumber.setVisibility(View.GONE);
//                }
//            } else {
//                llVirtualNumber.setVisibility(View.GONE);
//            }
//
//            if (serviceInfo.getIsPrePayment().equalsIgnoreCase("true")) {
//
//                if (serviceInfo.isUser()) {
//                    APIPayment(String.valueOf(userId));
//                } else {
//                    APIPayment(String.valueOf(providerId));
//
//                }
//            } else {
//
//                LservicePrepay.setVisibility(View.GONE);
//            }
//        }
//
//        mFirstName = SharedPreference.getInstance(CheckInActivity.this).getStringValue("firstname", "");
//        mLastName = SharedPreference.getInstance(CheckInActivity.this).getStringValue("lastname", "");
//        consumerID = SharedPreference.getInstance(CheckInActivity.this).getIntValue("consumerId", 0);
//
//    }
//}