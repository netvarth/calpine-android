package com.jaldeeinc.jaldee.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.hbb20.CountryCodePicker;
import com.jaldeeinc.jaldee.Interface.ICpn;
import com.jaldeeinc.jaldee.Interface.IFamillyListSelected;
import com.jaldeeinc.jaldee.Interface.IFamilyMemberDetails;
import com.jaldeeinc.jaldee.Interface.IPaymentGateway;
import com.jaldeeinc.jaldee.Interface.IPaymentResponse;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.MultipleFamilyMemberAdapter;
import com.jaldeeinc.jaldee.adapter.PaymentModeAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CircleTransform;
import com.jaldeeinc.jaldee.custom.CustomTextViewLight;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.FamilyMemberDialog;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;
import com.jaldeeinc.jaldee.model.BookingModel;
import com.jaldeeinc.jaldee.model.FamilyArrayModel;
import com.jaldeeinc.jaldee.model.LabelPath;
import com.jaldeeinc.jaldee.model.MediaTypeAndExtention;
import com.jaldeeinc.jaldee.model.PincodeLocationsResponse;
import com.jaldeeinc.jaldee.model.QuestionnaireInput;
import com.jaldeeinc.jaldee.model.RazorpayModel;
import com.jaldeeinc.jaldee.model.ShoppingListModel;
import com.jaldeeinc.jaldee.payment.PaymentGateway;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.ActiveDonation;
import com.jaldeeinc.jaldee.response.AdvancePaymentDetails;
import com.jaldeeinc.jaldee.response.CoupnResponse;
import com.jaldeeinc.jaldee.response.PayMode;
import com.jaldeeinc.jaldee.response.PaymentModel;
import com.jaldeeinc.jaldee.response.ProfileModel;
import com.jaldeeinc.jaldee.response.Provider;
import com.jaldeeinc.jaldee.response.ProviderCouponResponse;
import com.jaldeeinc.jaldee.response.QuestionnaireUrls;
import com.jaldeeinc.jaldee.response.SearchTerminology;
import com.jaldeeinc.jaldee.response.SubmitQuestionnaire;
import com.jaldeeinc.jaldee.response.WalletCheckSumModel;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReconfirmationActivity extends AppCompatActivity implements IFamilyMemberDetails, IFamillyListSelected, PaymentResultWithDataListener, IPaymentResponse, IPaymentGateway, ICpn {

    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.toolbartitle)
    TextView toolbartitle;
    @BindView(R.id.tv_providerPhoneNumber)
    TextView tv_providerPhoneNumber;
    @BindView(R.id.tv_providerEmail)
    TextView tv_providerEmail;
    @BindView(R.id.ll_providerPhoneNumber)
    LinearLayout ll_providerPhoneNumber;
    @BindView(R.id.tv_locationName)
    TextView tvLocationName;
    @BindView(R.id.ll_providerEmail)
    LinearLayout ll_providerEmail;
    @BindView(R.id.tv_providerName)
    TextView tvProviderName;
    @BindView(R.id.tv_serviceName)
    TextView tvServiceName;
    @BindView(R.id.iv_serviceIcon)
    ImageView ivServiceIcon;
    @BindView(R.id.iv_teleService)
    ImageView iv_teleService;
    @BindView(R.id.tv_description)
    TextView tvDescription;
    @BindView(R.id.tv_cnsmrDetails_Heading)
    TextView tv_cnsmrDetails_Heading;
    @BindView(R.id.tv_customerName)
    TextView tvCustomerName;
    @BindView(R.id.tv_phoneNumber)
    TextView tvPhoneNumber;
    @BindView(R.id.tv_email)
    TextView tvEmail;
    @BindView(R.id.ll_preinfo)
    LinearLayout ll_preinfo;
    @BindView(R.id.tv_preInfoTitle)
    TextView tv_preInfoTitle;
    @BindView(R.id.tv_preInfo)
    TextView tv_preInfo;
    @BindView(R.id.gridLayout_booking)
    GridLayout gridLayout_booking;
    @BindView(R.id.ll_donationAmount)
    LinearLayout ll_donationAmount;
    @BindView(R.id.tv_donationAmount)
    TextView tv_donationAmount;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.ll_date)
    LinearLayout ll_date;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.ll_time)
    LinearLayout ll_time;
    @BindView(R.id.txtserviceamount)
    TextView txtserviceamount;
    @BindView(R.id.txtprepayamount)
    TextView txtprepayamount;
    @BindView(R.id.ll_prepay)
    LinearLayout ll_prepay;
    @BindView(R.id.ll_serviceamount)
    LinearLayout ll_serviceamount;
    @BindView(R.id.ll_fullfees_payd_advance)
    LinearLayout ll_fullfees_payd_advance;
    @BindView(R.id.ll_servicePrepay)
    LinearLayout ll_servicePrepay;
    @BindView(R.id.ll_payment_mode)
    LinearLayout ll_payment_mode;
    @BindView(R.id.ll_jCash)
    LinearLayout llJCash;
    @BindView(R.id.cb_jCash)
    CheckBox cbJCash;
    @BindView(R.id.tv_jCashHint)
    TextView tvJCashHint;
    @BindView(R.id.gv_payment_modes)
    GridView gv_payment_modes;
    @BindView(R.id.tv_buttonName)
    TextView tvButtonName;
    @BindView(R.id.ll_cancellation_policy)
    LinearLayout ll_cancellation_policy;
    @BindView(R.id.cv_cancellation_policy)
    CardView cv_cancellation_policy;
    @BindView(R.id.tv_payment_link)
    TextView tv_payment_link;
    @BindView(R.id.cv_submit)
    CardView cvSubmit;
    @BindView(R.id.iv_location_icon)
    ImageView iv_location_icon;
    @BindView(R.id.iv_prvdr_phone_icon)
    ImageView iv_prvdr_phone_icon;
    @BindView(R.id.iv_prvdr_email_icon)
    ImageView iv_prvdr_email_icon;
    @BindView(R.id.icon_text)
    TextView icon_text;
    @BindView(R.id.ll_coupon)
    LinearLayout ll_coupon;
    @BindView(R.id.ll_coupons)
    LinearLayout llCoupons;
    @BindView(R.id.cb_coupon)
    CheckBox cb_coupon;
    @BindView(R.id.rl_coupon)
    RelativeLayout rlCoupon;
    @BindView(R.id.tv_apply)
    TextView tvApply;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.ll_editDetails)
    LinearLayout llEditDetails;
    @BindView(R.id.tv_vitual_service_number)
    TextView tv_vitual_service_number;
    /*@BindView(R.id.tv_bookingAt)
    CustomTextViewMedium tv_bookingAt;*/

    @BindView(R.id.ll_virtualNumber)
    LinearLayout llVirtualNumber;
    @BindView(R.id.tv_vsHint)
    TextView tvVsHint;
    @BindView(R.id.virtual_NmbrCCPicker)
    CountryCodePicker virtual_NmbrCCPicker;
    @BindView(R.id.et_virtualNumber)
    EditText etVirtualNumber;
    @BindView(R.id.rl_editDetails)
    RelativeLayout rl_editDetails;
    @BindView(R.id.recycle_family)
    RecyclerView recycle_family;

    @BindView(R.id.tv_waitingInLine)
    CustomTextViewLight tvWaitingInLine;


    BookingModel bookingModel = new BookingModel();
    public Context mContext;
    public JSONObject jsonObject;
    public String value = null;
    public String prepayAmount = "";
    public String prePayRemainingAmount = "";
    public String selectedpaymentMode;
    public SearchTerminology mSearchTerminology;
    public ActiveCheckIn activeCheckin = new ActiveCheckIn();
    public ActiveAppointment activeAppointment = new ActiveAppointment();
    public ArrayList<FamilyArrayModel> MultiplefamilyList = new ArrayList<>();
    public ArrayList<LabelPath> imagePathList = new ArrayList<>();
    public ArrayList<LabelPath> serviceOptionImagePathList = new ArrayList<>();
    private IFamilyMemberDetails iFamilyMemberDetails;

    public ArrayList<ShoppingListModel> attachedImagePathList = new ArrayList<>();
    public ArrayList<PaymentModel> mPaymentData = new ArrayList<>();
    private ArrayList<PayMode> payModes = new ArrayList<>();
    AdvancePaymentDetails advancePaymentDetails = new AdvancePaymentDetails();

    public int familyMEmID;
    public int serviceId = 0;
    public boolean indiaPay = false;
    public boolean internationalPay = false;
    public boolean isInternational = false;
    public ActiveDonation activeDonation;
    public String encId;
    public String PAYMENT_LINK_FLAG;
    public IPaymentResponse iPaymentResponse;
    public IPaymentGateway iPaymentGateway;
    public BottomSheetDialog dialog;
    public String path;
    public Bitmap bitmap;
    public File file;
    public File f;
    private static final String IMAGE_DIRECTORY = "/Jaldee" + "";
    int consumerId;
    String paymentRequestId;
    String providerLogoUrl;
    Provider providerResponse;
    Integer providerUniqueId;
    ArrayList<CoupnResponse> s3couponList = new ArrayList<>();
    ArrayList<ProviderCouponResponse> providerCouponList = new ArrayList<>();

    String couponEntered;
    ArrayList<String> couponArraylist = new ArrayList<>();
    private ICpn iCpn;

    String serviceBookingType;
    private FamilyMemberDialog familyMemberDialog;
    String phoneNumber;
    String countryCode;
    String mFirstName, mLastName;
    private String emailId;
    ProfileModel profileModel = new ProfileModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconfirmation);
        ButterKnife.bind(ReconfirmationActivity.this);
        iPaymentResponse = this;
        iPaymentGateway = this;
        mContext = ReconfirmationActivity.this;
        iFamilyMemberDetails = this;

        consumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);
        Intent intent = getIntent();
        bookingModel = (BookingModel) intent.getSerializableExtra("data");
        providerUniqueId = bookingModel.getProviderUniqueId();
        providerResponse = bookingModel.getProviderResponse();
        providerLogoUrl = bookingModel.getProviderLogo();
        mSearchTerminology = bookingModel.getmSearchTerminology();
        serviceBookingType = bookingModel.getServiceBookingType();

        if (bookingModel.getProfileDetails() != null && !bookingModel.getProfileDetails().trim().isEmpty()) {
            Gson gson = new Gson();
            profileModel = gson.fromJson(bookingModel.getProfileDetails(), ProfileModel.class);
        }
        toolbartitle.setText("Confirm & pay");
        getQuestionnaireImages();
        getServiceOptionQnrImages();
        if (providerLogoUrl != null && !providerLogoUrl.trim().isEmpty()) {
            PicassoTrustAll.getInstance(mContext).load(providerLogoUrl).placeholder(R.drawable.service_avatar).error(R.drawable.service_avatar).transform(new CircleTransform()).fit().into(ivServiceIcon);
        }
        if (bookingModel != null) {
            mFirstName = bookingModel.getCustomerName().split("\\s+")[0];
            mLastName = bookingModel.getCustomerName().split("\\s+")[1];

            phoneNumber = bookingModel.getPhoneNumber();
            countryCode = bookingModel.getCountryCode();
            try {
                jsonObject = new JSONObject(bookingModel.getJsonObject());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // to set providerName
            if (bookingModel.getProviderName() != null) {
                if (bookingModel.getAccountBusinessName() != null) {
                    tvProviderName.setText(bookingModel.getProviderName() + ", " + bookingModel.getAccountBusinessName());
                } else {
                    tvProviderName.setText(bookingModel.getProviderName());
                }
            }
            if (bookingModel.getLocationName() != null) {
                tvLocationName.setText(bookingModel.getLocationName());
            }
            if (bookingModel.getProviderPhoneNumber() != null && !bookingModel.getProviderPhoneNumber().isEmpty()) {
                ll_providerPhoneNumber.setVisibility(View.VISIBLE);
                tv_providerPhoneNumber.setText(bookingModel.getProviderPhoneNumber());
            } else {
                ll_providerPhoneNumber.setVisibility(View.GONE);
            }
            if (bookingModel.getProviderEmail() != null && !bookingModel.getProviderEmail().isEmpty()) {
                ll_providerEmail.setVisibility(View.VISIBLE);
                tv_providerEmail.setText(bookingModel.getProviderEmail());
            } else {
                ll_providerEmail.setVisibility(View.GONE);
            }
            if (bookingModel.getDate() != null && !bookingModel.getDate().trim().isEmpty()) {
                ll_date.setVisibility(View.VISIBLE);
                tvDate.setVisibility(View.VISIBLE);
                tvDate.setText(Html.fromHtml(bookingModel.getDate()));
            } else {
                ll_date.setVisibility(View.GONE);
                tvDate.setVisibility(View.GONE);
            }
            if (bookingModel.getTime() != null && !bookingModel.getTime().trim().isEmpty()) {
                ll_time.setVisibility(View.VISIBLE);
                tvTime.setVisibility(View.VISIBLE);
                tvTime.setText(bookingModel.getTime());
            } else {
                ll_time.setVisibility(View.GONE);
                tvTime.setVisibility(View.GONE);
            }


            if (bookingModel.getFrom() != null) {
                if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT) || bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                    if (providerResponse != null) {
                        ApiJaldeegetS3Coupons(providerResponse.getCoupon());
                        ApiJaldeegetProviderCoupons(providerResponse.getProviderCoupon());
                    }
                    if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                        Appointment();
                    } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                        Checkin();
                    }
                } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.DONATION)) {
                    Donation();
                }
            }
        }
        cv_cancellation_policy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Dialog cancellationPolicyDialog = new Dialog(mContext);
                cancellationPolicyDialog.setContentView(R.layout.cancellation_policy_dialog);
                cancellationPolicyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cancellationPolicyDialog.setCancelable(false);
                DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                int width = (int) (metrics.widthPixels * 1);
                cancellationPolicyDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

                String cancelation_policy_2 = getResources().getString(R.string.cancelation_policy_2);
                String cancelation_policy_3 = getResources().getString(R.string.cancelation_policy_3);
                if (bookingModel.getFrom() != null) {
                    if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                        cancelation_policy_2 = cancelation_policy_2.replace("VARIABLE", "appointment");
                        cancelation_policy_3 = cancelation_policy_3.replace("VARIABLE", "appointment");
                    } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                        if (bookingModel.isToken) {
                            cancelation_policy_2 = cancelation_policy_2.replace("VARIABLE", "token");
                            cancelation_policy_3 = cancelation_policy_3.replace("VARIABLE", "token");
                        } else {
                            cancelation_policy_2 = cancelation_policy_2.replace("VARIABLE", "checkin");
                            cancelation_policy_3 = cancelation_policy_3.replace("VARIABLE", "checkin");
                        }
                    }
                }
                TextView setup_intro_bullet_first_text = cancellationPolicyDialog.findViewById(R.id.setup_intro_bullet_first_text);
                TextView setup_intro_bullet_second_text = cancellationPolicyDialog.findViewById(R.id.setup_intro_bullet_second_text);
                setup_intro_bullet_first_text.setText(cancelation_policy_2);
                setup_intro_bullet_second_text.setText(cancelation_policy_3);

                cancellationPolicyDialog.show();

                LinearLayout close = cancellationPolicyDialog.findViewById(R.id.ll_close);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancellationPolicyDialog.cancel();
                    }
                });
            }
        });
        cbJCash.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (cbJCash.isChecked()) {
                    if (bookingModel.getEligibleJcashAmt() >= bookingModel.getAmountRequiredNow()) {
                        tvButtonName.setText("Confirm ");
                        toolbartitle.setText("Confirm");
                        tv_payment_link.setVisibility(View.GONE);
                        gv_payment_modes.setVisibility(View.GONE);

                        txtprepayamount.setText("₹0");
                    } else {
                        tvButtonName.setText("Make Payment");
                        toolbartitle.setText("Confirm & pay");
                        tvJCashHint.setVisibility(View.VISIBLE);
                        if (indiaPay || internationalPay) {
                            gv_payment_modes.setVisibility(View.VISIBLE);
                            if (internationalPay && indiaPay) {
                                if (mPaymentData.get(0).getInternationalBankInfo().size() > 0 && mPaymentData.get(0).getIndiaBankInfo().size() > 0) {
                                    tv_payment_link.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            gv_payment_modes.setVisibility(View.GONE);
                            tv_payment_link.setVisibility(View.GONE);

                        }
                        txtprepayamount.setText("₹" + Config.getAmountNoOrTwoDecimalPoints(bookingModel.getAmountRequiredNow() - bookingModel.getEligibleJcashAmt()));
                    }
                } else {
                    tvButtonName.setText("Make Payment");
                    toolbartitle.setText("Confirm & pay");
                    if (indiaPay || internationalPay) {
                        gv_payment_modes.setVisibility(View.VISIBLE);
                        if (internationalPay && indiaPay) {
                            if (mPaymentData.get(0).getInternationalBankInfo().size() > 0 && mPaymentData.get(0).getIndiaBankInfo().size() > 0) {
                                tv_payment_link.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        gv_payment_modes.setVisibility(View.GONE);
                        tv_payment_link.setVisibility(View.GONE);

                    }
                    /*if (showForInternationalPayment) {
                        tv_international_payment_link.setVisibility(View.VISIBLE);
                    } else {
                        tv_international_payment_link.setVisibility(View.GONE);
                    }*/
                    tvJCashHint.setVisibility(View.GONE);
                    txtprepayamount.setText("₹" + Config.getAmountNoOrTwoDecimalPoints(bookingModel.getAmountRequiredNow()));
                }
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
        tv_payment_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PAYMENT_LINK_FLAG.isEmpty() && PAYMENT_LINK_FLAG != null) {
                    if (PAYMENT_LINK_FLAG.equalsIgnoreCase("INDIA")) {
                        tv_payment_link.setVisibility(View.VISIBLE);
                        tv_payment_link.setText("Indian Payment ? Click here");
                        payModes = mPaymentData.get(0).getInternationalBankInfo();
                        PAYMENT_LINK_FLAG = "INTERNATIONAL";
                        isInternational = true;
                    } else if (PAYMENT_LINK_FLAG.equalsIgnoreCase("INTERNATIONAL")) {
                        tv_payment_link.setVisibility(View.VISIBLE);
                        tv_payment_link.setText("Non Indian Payment ? Click here");
                        payModes = mPaymentData.get(0).getIndiaBankInfo();
                        PAYMENT_LINK_FLAG = "INDIA";
                        isInternational = false;
                    }
                    selectedpaymentMode = null;

                    gv_payment_modes.setVisibility(View.VISIBLE);
                    gv_payment_modes.setAdapter(new PaymentModeAdapter(mContext, payModes, iPaymentGateway));

                } else {
                    tv_payment_link.setVisibility(View.GONE);
                }
            }
        });
        cvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bookingModel != null) {
                    ApiSubmit(jsonObject, bookingModel.getAccountId());
                }
            }
        });
        cb_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cb_coupon.isChecked()) {
                    rlCoupon.setVisibility(View.VISIBLE);
                } else {
                    rlCoupon.setVisibility(View.GONE);
                }
            }
        });
        llCoupons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent iCoupons = new Intent(ReconfirmationActivity.this, CouponActivity.class);
                iCoupons.putExtra("uniqueID", String.valueOf(providerUniqueId));
                iCoupons.putExtra("accountId", String.valueOf(providerResponse.getBusinessProfile()));
                startActivity(iCoupons);
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

                    Toast.makeText(ReconfirmationActivity.this, "Coupon already added", Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(ReconfirmationActivity.this, couponEntered + " " + "Added", Toast.LENGTH_SHORT).show();


                } else {
                    if (couponEntered.equals("")) {
                        Toast.makeText(ReconfirmationActivity.this, "Enter a coupon", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ReconfirmationActivity.this, "Coupon Invalid", Toast.LENGTH_SHORT).show();
                    }
                }
                cpns(couponArraylist);
            }
        });
        llEditDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean virtualService;
                if (bookingModel.getServiceInfo().getServiceType() != null && bookingModel.getServiceInfo().getServiceType().equalsIgnoreCase("virtualService")) {
                    virtualService = true;
                } else {
                    virtualService = false;
                }
                familyMemberDialog = new FamilyMemberDialog(ReconfirmationActivity.this, familyMEmID, tvEmail.getText().toString(), phoneNumber, bookingModel.getServiceInfo().getIsPrePayment(), iFamilyMemberDetails, profileModel, false, 0, countryCode, virtualService, bookingModel.getAccountId());

                familyMemberDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                familyMemberDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                familyMemberDialog.show();
                DisplayMetrics metrics = ReconfirmationActivity.this.getResources().getDisplayMetrics();
                int width = (int) (metrics.widthPixels * 1);
                familyMemberDialog.setCancelable(false);
                familyMemberDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

            }
        });
    }

    private void Appointment() {
        familyMEmID = bookingModel.getFamilyEMIID();
        if (bookingModel.getServiceInfo() != null) {
            if (bookingModel.getServiceInfo().getServiceName() != null) {
                tvServiceName.setText(bookingModel.getServiceInfo().getServiceName());
                if (bookingModel.getServiceInfo().getServiceType() != null && bookingModel.getServiceInfo().getServiceType().equalsIgnoreCase("virtualService")) {
                    if (bookingModel.getServiceInfo().getCallingMode() != null) {
                        iv_teleService.setVisibility(View.VISIBLE);
                        if (bookingModel.getServiceInfo().getCallingMode().equalsIgnoreCase("Zoom")) {
                            iv_teleService.setImageResource(R.drawable.zoom);
                        } else if (bookingModel.getServiceInfo().getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                            iv_teleService.setImageResource(R.drawable.googlemeet);
                        } else if (bookingModel.getServiceInfo().getCallingMode().equalsIgnoreCase("WhatsApp")) {
                            if (bookingModel.getServiceInfo().getVirtualServiceType() != null && bookingModel.getServiceInfo().getVirtualServiceType().equalsIgnoreCase("videoService")) {
                                iv_teleService.setImageResource(R.drawable.whatsapp_videoicon);
                                if (bookingModel.getServiceBookingType() != null && bookingModel.getServiceBookingType().equalsIgnoreCase(Constants.SERVICE_BOOKING_TYPE_REQUEST)
                                        && bookingModel.getServiceInfo().isNoDateTime()) {
                                    llVirtualNumber.setVisibility(View.VISIBLE);
                                    tvVsHint.setText("WhatsApp number");
                                } else {
                                    if (bookingModel.getWhtsappCountryCode() != null && bookingModel.getWhtsappPhoneNumber() != null) {
                                        tv_vitual_service_number.setVisibility(View.VISIBLE);
                                        tv_vitual_service_number.setText("+" + bookingModel.getWhtsappCountryCode() + " " + bookingModel.getWhtsappPhoneNumber());
                                        tv_vitual_service_number.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsapp_videoicon_sized, 0, 0, 0);
                                    }
                                }
                            } else {
                                iv_teleService.setImageResource(R.drawable.whatsapp_icon);
                                if (bookingModel.getServiceBookingType() != null && bookingModel.getServiceBookingType().equalsIgnoreCase(Constants.SERVICE_BOOKING_TYPE_REQUEST)
                                        && bookingModel.getServiceInfo().isNoDateTime()) {
                                    llVirtualNumber.setVisibility(View.VISIBLE);
                                    tvVsHint.setText("WhatsApp number");
                                } else {
                                    if (bookingModel.getWhtsappCountryCode() != null && bookingModel.getWhtsappPhoneNumber() != null) {
                                        tv_vitual_service_number.setVisibility(View.VISIBLE);
                                        tv_vitual_service_number.setText("+" + bookingModel.getWhtsappCountryCode() + " " + bookingModel.getWhtsappPhoneNumber());
                                        tv_vitual_service_number.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized, 0, 0, 0);
                                    }
                                }
                            }
                        } else if (bookingModel.getServiceInfo().getCallingMode().equalsIgnoreCase("phone")) {
                            if (bookingModel.getServiceBookingType() != null && bookingModel.getServiceBookingType().equalsIgnoreCase(Constants.SERVICE_BOOKING_TYPE_REQUEST)
                                    && bookingModel.getServiceInfo().isNoDateTime()) {
                                llVirtualNumber.setVisibility(View.VISIBLE);
                                tvVsHint.setText("Contact number");
                            }
                            iv_teleService.setImageResource(R.drawable.phoneaudioicon);
                        } else if (bookingModel.getServiceInfo().getCallingMode().equalsIgnoreCase("VideoCall")) {
                            iv_teleService.setImageResource(R.drawable.ic_jaldeevideo);
                        } else {
                            iv_teleService.setVisibility(View.GONE);
                        }
                    } else {
                        iv_teleService.setVisibility(View.GONE);
                    }
                } else {
                    iv_teleService.setVisibility(View.GONE);
                }
            }
        }
        getAttachedImages();
        serviceId = bookingModel.getServiceInfo().getServiceId();
        tvWaitingInLine.setVisibility(View.GONE);
        gridLayout_booking.setVisibility(View.VISIBLE);
        ll_donationAmount.setVisibility(View.GONE);
        //tv_bookingAt.setText("APPOINTMENT AT : ");
        tv_cnsmrDetails_Heading.setText("Booking For");
        if (bookingModel.getCustomerName() != null && !bookingModel.getCustomerName().trim().isEmpty()) {
            String fName = bookingModel.getCustomerName();
            if (fName != null && !fName.trim().isEmpty()) {
                icon_text.setText(String.valueOf(fName.trim().charAt(0)));
            }
            tvCustomerName.setText(fName);
        }
        if (bookingModel.getServiceInfo().getIsPrePayment().equalsIgnoreCase("true")) {
            APIPayment(bookingModel.getAccountId());
        } else {
            updateUI();
        }
        if (bookingModel.getServiceInfo().getIsPrePayment().equalsIgnoreCase("true")) {
            txtprepayamount.setText("₹" + Config.getAmountNoOrTwoDecimalPoints(getFloatAsDouble(bookingModel.getAmountRequiredNow())));
            txtserviceamount.setText("₹" + Config.getAmountNoOrTwoDecimalPoints(getFloatAsDouble(bookingModel.getNetTotal())));
            ll_serviceamount.setVisibility(View.VISIBLE);
            if (bookingModel.getAmountRequiredNow() == bookingModel.getNetTotal()) {
                ll_fullfees_payd_advance.setVisibility(View.VISIBLE);
                ll_prepay.setVisibility(View.GONE);
            } else {
                ll_fullfees_payd_advance.setVisibility(View.GONE);
                ll_prepay.setVisibility(View.VISIBLE);
            }

        } else {
            ll_serviceamount.setVisibility(View.GONE);
            ll_prepay.setVisibility(View.GONE);
        }

        if (bookingModel.getServiceBookingType() != null && bookingModel.getServiceBookingType().equalsIgnoreCase(Constants.SERVICE_BOOKING_TYPE_REQUEST)
                && bookingModel.getServiceInfo().isNoDateTime()) {
            String cCode = bookingModel.getCountryCode().replace("+", "");
            virtual_NmbrCCPicker.setCountryForPhoneCode(Integer.parseInt(cCode));
            etVirtualNumber.setText(bookingModel.getPhoneNumber());
            if (bookingModel.getServiceInfo().isNoDateTime()) {
                rl_editDetails.setVisibility(View.VISIBLE);
            }
        }

    }

    private void Checkin() {
        familyMEmID = bookingModel.getFamilyEMIID();
        if (bookingModel.getCheckInInfo() != null) {
            if (bookingModel.getCheckInInfo().getName() != null) {
                tvServiceName.setText(bookingModel.getCheckInInfo().getName());
                if (bookingModel.getCheckInInfo().getServiceType() != null && bookingModel.getCheckInInfo().getServiceType().equalsIgnoreCase("virtualService")) {
                    if (bookingModel.getCheckInInfo().getVirtualCallingModes().get(0).getCallingMode() != null) {
                        iv_teleService.setVisibility(View.VISIBLE);
                        if (bookingModel.getCheckInInfo().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                            iv_teleService.setImageResource(R.drawable.zoom);
                        } else if (bookingModel.getCheckInInfo().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                            iv_teleService.setImageResource(R.drawable.googlemeet);
                        } else if (bookingModel.getCheckInInfo().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                            if (bookingModel.getCheckInInfo().getVirtualServiceType() != null && bookingModel.getCheckInInfo().getVirtualServiceType().equalsIgnoreCase("videoService")) {
                                iv_teleService.setImageResource(R.drawable.whatsapp_videoicon);
                                if (bookingModel.getWhtsappCountryCode() != null && bookingModel.getWhtsappPhoneNumber() != null) {
                                    tv_vitual_service_number.setVisibility(View.VISIBLE);
                                    tv_vitual_service_number.setText("+" + bookingModel.getWhtsappCountryCode() + " " + bookingModel.getWhtsappPhoneNumber());
                                    tv_vitual_service_number.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsapp_videoicon_sized, 0, 0, 0);
                                }
                            } else {
                                iv_teleService.setImageResource(R.drawable.whatsapp_icon);
                                if (bookingModel.getWhtsappCountryCode() != null && bookingModel.getWhtsappPhoneNumber() != null) {
                                    tv_vitual_service_number.setVisibility(View.VISIBLE);
                                    tv_vitual_service_number.setText("+" + bookingModel.getWhtsappCountryCode() + " " + bookingModel.getWhtsappPhoneNumber());
                                    tv_vitual_service_number.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized, 0, 0, 0);
                                }
                            }

                        } else if (bookingModel.getCheckInInfo().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                            iv_teleService.setImageResource(R.drawable.phoneaudioicon);
                            tvPhoneNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneaudioicon_sized, 0, 0, 0);
                        } else if (bookingModel.getCheckInInfo().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("VideoCall")) {
                            iv_teleService.setImageResource(R.drawable.ic_jaldeevideo);
                        } else {
                            iv_teleService.setVisibility(View.GONE);
                        }
                    } else {
                        iv_teleService.setVisibility(View.GONE);
                    }
                } else {
                    iv_teleService.setVisibility(View.GONE);
                }
            }
        }
        getAttachedImages();
        serviceId = bookingModel.getCheckInInfo().getId();

        gridLayout_booking.setVisibility(View.VISIBLE);
        ll_donationAmount.setVisibility(View.GONE);
        /*if (bookingModel.isToken()) {
            tv_bookingAt.setText("TOKEN AT : ");
        } else {
            tv_bookingAt.setText("CHECKIN AT : ");
        }*/
        tv_cnsmrDetails_Heading.setText("Booking For");
        if (bookingModel.getPeopleWaiting() != null) {
            tvWaitingInLine.setVisibility(View.VISIBLE);
            tvWaitingInLine.setText(bookingModel.getPeopleWaiting());
        }
        if (bookingModel.getMultipleFamilyMembers() != null && bookingModel.getMultipleFamilyMembers().size() > 0) {

            MultiplefamilyList = bookingModel.getMultipleFamilyMembers();
        } else {
            MultiplefamilyList = new ArrayList<>();
        }
        if (bookingModel.getCustomerName() != null && !bookingModel.getCustomerName().trim().equalsIgnoreCase("")) {
            String fName = bookingModel.getCustomerName();
            if (fName != null && !fName.trim().isEmpty()) {
                icon_text.setText(String.valueOf(fName.trim().charAt(0)));
            }
            tvCustomerName.setText(fName);
        } else if (MultiplefamilyList != null && MultiplefamilyList.size() > 0) {
            ArrayList<String> names = new ArrayList<>();
            for (FamilyArrayModel model : MultiplefamilyList) {
                String name = model.getFirstName() + " " + model.getLastName();
                names.add(name);
            }
            if (names != null && !names.get(0).trim().isEmpty()) {
                icon_text.setText(String.valueOf(names.get(0).trim().charAt(0)));
            }
            tvCustomerName.setText(TextUtils.join(", ", names));
        }
        if (bookingModel.getCheckInInfo().isPrePayment()) {
            APIPayment(bookingModel.getAccountId());
        } else {
            updateUI();
        }
        if (bookingModel.getCheckInInfo().isPrePayment()) {
            txtprepayamount.setText("₹" + Config.getAmountNoOrTwoDecimalPoints(getFloatAsDouble(bookingModel.getAmountRequiredNow())));
            txtserviceamount.setText("₹" + Config.getAmountNoOrTwoDecimalPoints(getFloatAsDouble(bookingModel.getNetTotal())));
            ll_serviceamount.setVisibility(View.VISIBLE);
            if (bookingModel.getAmountRequiredNow() == bookingModel.getNetTotal()) {
                ll_fullfees_payd_advance.setVisibility(View.VISIBLE);
                ll_prepay.setVisibility(View.GONE);
            } else {
                ll_fullfees_payd_advance.setVisibility(View.GONE);
                ll_prepay.setVisibility(View.VISIBLE);
            }
        } else {
            ll_serviceamount.setVisibility(View.GONE);
            ll_prepay.setVisibility(View.GONE);
        }


    }

    private void Donation() {
        familyMEmID = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);

        if (bookingModel.getDonationServiceInfo() != null) {
            if (bookingModel.getDonationServiceInfo().getName() != null) {
                tvServiceName.setText(bookingModel.getDonationServiceInfo().getName());
            }
        }
        serviceId = bookingModel.getDonationServiceInfo().getId();

        tvWaitingInLine.setVisibility(View.GONE);
        gridLayout_booking.setVisibility(View.GONE);
        ll_donationAmount.setVisibility(View.VISIBLE);
        tv_cnsmrDetails_Heading.setText("Payment By");
        if (bookingModel.getCustomerName() != null && !bookingModel.getCustomerName().trim().equalsIgnoreCase("")) {
            String fName = bookingModel.getCustomerName();
            if (fName != null && !fName.trim().isEmpty()) {
                icon_text.setText(String.valueOf(fName.trim().charAt(0)));
            }
            tvCustomerName.setText(fName);
        }
        if (bookingModel.getDonationAmount() != null && !bookingModel.getDonationAmount().trim().equalsIgnoreCase("")) {
            tv_donationAmount.setText("₹ " + Config.getMoneyFormat(Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(bookingModel.getDonationAmount()))));
        }
        APIPayment(bookingModel.getAccountId());
    }

    public void getQuestionnaireImages() {
        String imagesString = SharedPreference.getInstance(mContext).getStringValue(Constants.QIMAGES, "");
        if (imagesString != null && !imagesString.trim().equalsIgnoreCase("")) {
            Type labelPathType = new TypeToken<ArrayList<LabelPath>>() {
            }.getType();
            try {
                ArrayList<LabelPath> pathList = new Gson().fromJson(imagesString, labelPathType);
                imagePathList = pathList;
            } catch (JsonSyntaxException e) {
                imagePathList = new ArrayList<>();
                e.printStackTrace();
            }
        }
    }

    public void getServiceOptionQnrImages() {
        String imagesString = SharedPreference.getInstance(mContext).getStringValue(Constants.SERVICEOPTIONQIMAGES, "");
        if (imagesString != null && !imagesString.trim().equalsIgnoreCase("")) {
            Type labelPathType = new TypeToken<ArrayList<LabelPath>>() {
            }.getType();
            try {
                ArrayList<LabelPath> pathList = new Gson().fromJson(imagesString, labelPathType);
                serviceOptionImagePathList = pathList;
            } catch (JsonSyntaxException e) {
                serviceOptionImagePathList = new ArrayList<>();
                e.printStackTrace();
            }
        }
    }


    public void getAttachedImages() {
        if (bookingModel.getImagePathList() != null && bookingModel.getImagePathList().size() > 0) {
            attachedImagePathList = bookingModel.getImagePathList();
        } else {
            attachedImagePathList = new ArrayList<>();
        }
    }

    private void APIPayment(int accountID) {
        ApiInterface apiService =
                ApiClient.getClient(ReconfirmationActivity.this).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(ReconfirmationActivity.this, ReconfirmationActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Call<ArrayList<PaymentModel>> call = apiService.getPaymentModes(String.valueOf(accountID), serviceId, Constants.PURPOSE_PREPAYMENT);
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
                        indiaPay = mPaymentData.get(0).isIndiaPay();
                        internationalPay = mPaymentData.get(0).isInternationalPay();
                        if (indiaPay && mPaymentData.get(0).getIndiaBankInfo().size() > 0) {
                            selectedpaymentMode = null;
                            PAYMENT_LINK_FLAG = "INDIA";            /******** for set PAYMENT_LINK_FLAG in all cases(appt,checkin,donation) **/
                            tv_payment_link.setVisibility(View.VISIBLE);
                            if (mPaymentData != null && mPaymentData.get(0).getIndiaBankInfo().size() > 0) {
                                payModes = mPaymentData.get(0).getIndiaBankInfo();

                                gv_payment_modes.setVisibility(View.VISIBLE);
                                gv_payment_modes.setAdapter(new PaymentModeAdapter(mContext, payModes, iPaymentGateway));

                                if (internationalPay && mPaymentData.get(0).getInternationalBankInfo().size() > 0) {
                                    tv_payment_link.setText("Non Indian Payment ? Click here");
                                } else {
                                    tv_payment_link.setVisibility(View.GONE);
                                }
                                isInternational = false;
                            }
                        } else if (internationalPay && mPaymentData.get(0).getInternationalBankInfo().size() > 0) {
                            selectedpaymentMode = null;
                            PAYMENT_LINK_FLAG = "INTERNATIONAL";     /******** for set PAYMENT_LINK_FLAG in all cases(appt,checkin,donation) **/
                            tv_payment_link.setVisibility(View.VISIBLE);
                            if (mPaymentData != null && mPaymentData.get(0).getInternationalBankInfo().size() > 0) {
                                payModes = mPaymentData.get(0).getInternationalBankInfo();

                                gv_payment_modes.setVisibility(View.VISIBLE);
                                gv_payment_modes.setAdapter(new PaymentModeAdapter(mContext, payModes, iPaymentGateway));

                                if (indiaPay && mPaymentData.get(0).getIndiaBankInfo().size() > 0) {
                                    tv_payment_link.setText("Indian Payment ? Click here");
                                } else {
                                    tv_payment_link.setVisibility(View.GONE);
                                }
                                isInternational = true;
                            }
                        } else {
                            tv_payment_link.setVisibility(View.GONE);
                        }
                        updateUI();

                    } else {
                        Toast.makeText(ReconfirmationActivity.this, response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<PaymentModel>> call, Throwable t) {

            }
        });
    }

    public static Double getFloatAsDouble(Float fValue) {
        return Double.valueOf(fValue.toString());
    }

    public void updateUI() {
        boolean isPrePayment = false;
        Double eligibleJcashAmt = Double.valueOf(0);
        Glide.with(mContext).load(R.drawable.location_icon_1).into(iv_location_icon);
        Glide.with(mContext).load(R.drawable.phone_icon_1).into(iv_prvdr_phone_icon);
        Glide.with(mContext).load(R.drawable.email_icon_1).into(iv_prvdr_email_icon);
        if (bookingModel.getEmailId() != null && !bookingModel.getEmailId().trim().isEmpty()) {
            tvEmail.setText(bookingModel.getEmailId());
        }
        if (bookingModel.getPhoneNumber() != null && bookingModel.getCountryCode() != null) {
            tvPhoneNumber.setText(bookingModel.getCountryCode() + " " + bookingModel.getPhoneNumber());
        }
        if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT) || bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
            if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                if (bookingModel.getServiceInfo().getDescription() != null && !bookingModel.getServiceInfo().getDescription().trim().isEmpty()) {
                    tvDescription.setVisibility(View.VISIBLE);
                    tvDescription.setText(bookingModel.getServiceInfo().getDescription());
                } else {
                    tvDescription.setVisibility(View.GONE);
                }
                if (bookingModel.getServiceInfo().isPreInfoEnabled()) {  //  check if pre-info is available for the service

                    ll_preinfo.setVisibility(View.VISIBLE);
                    if (bookingModel.getServiceInfo().getPreInfoTitle() != null && !bookingModel.getServiceInfo().getPreInfoTitle().trim().isEmpty()) {
                        tv_preInfoTitle.setText(bookingModel.getServiceInfo().getPreInfoTitle());
                    } else {
                        ll_preinfo.setVisibility(View.GONE);
                    }
                    if (bookingModel.getServiceInfo().getPreInfoText() != null && !bookingModel.getServiceInfo().getPreInfoText().trim().isEmpty()) {
                        tv_preInfo.setText((Html.fromHtml(bookingModel.getServiceInfo().getPreInfoText())));
                    } else {
                        ll_preinfo.setVisibility(View.GONE);
                    }
                } else {
                    ll_preinfo.setVisibility(View.GONE);
                }
                if (bookingModel.getServiceInfo().getIsPrePayment().equalsIgnoreCase("true")) {
                    isPrePayment = true;
                }
                eligibleJcashAmt = bookingModel.getEligibleJcashAmt();
            } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                if (bookingModel.getCheckInInfo().getDescription() != null && !bookingModel.getCheckInInfo().getDescription().trim().isEmpty()) {
                    tvDescription.setVisibility(View.VISIBLE);
                    tvDescription.setText(bookingModel.getCheckInInfo().getDescription());
                } else {
                    tvDescription.setVisibility(View.GONE);
                }
                if (bookingModel.getCheckInInfo().isPreInfoEnabled()) { //  check if pre-info is available for the service
                    ll_preinfo.setVisibility(View.VISIBLE);
                    if (bookingModel.getCheckInInfo().getPreInfoTitle() != null && !bookingModel.getCheckInInfo().getPreInfoTitle().trim().isEmpty()) {
                        tv_preInfoTitle.setText(bookingModel.getCheckInInfo().getPreInfoTitle());
                    } else {
                        ll_preinfo.setVisibility(View.GONE);
                    }
                    if (bookingModel.getCheckInInfo().getPreInfoText() != null && !bookingModel.getCheckInInfo().getPreInfoText().trim().isEmpty()) {
                        tv_preInfo.setText(Html.fromHtml(bookingModel.getCheckInInfo().getPreInfoText()));
                    } else {
                        ll_preinfo.setVisibility(View.GONE);
                    }
                } else {
                    ll_preinfo.setVisibility(View.GONE);
                }
                isPrePayment = bookingModel.getCheckInInfo().isPrePayment();
                eligibleJcashAmt = bookingModel.getEligibleJcashAmt();
            }
            //if (bookingModel.getNetTotal() != 0) {
            //ll_servicePrepay.setVisibility(View.VISIBLE);
            if (isPrePayment) {
                if (indiaPay || internationalPay) {
                    ll_servicePrepay.setVisibility(View.VISIBLE);
                    ll_cancellation_policy.setVisibility(View.VISIBLE);
                    tvButtonName.setText("Make Payment");
                    toolbartitle.setText("Confirm & pay");
                    gv_payment_modes.setVisibility(View.VISIBLE);
                    if (internationalPay && indiaPay) {
                        if (mPaymentData.get(0).getInternationalBankInfo().size() > 0 && mPaymentData.get(0).getIndiaBankInfo().size() > 0) {
                            tv_payment_link.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tv_payment_link.setVisibility(View.GONE);
                    }

                } else {
                    tv_payment_link.setVisibility(View.GONE);
                    //ll_servicePrepay.setVisibility(View.GONE);
                    ll_cancellation_policy.setVisibility(View.GONE);
                    tvButtonName.setText("Confirm");
                    toolbartitle.setText("Confirm");
                    gv_payment_modes.setVisibility(View.GONE);
                }
                if (eligibleJcashAmt > 0) {
                    cbJCash.setChecked(true);
                    llJCash.setVisibility(View.VISIBLE);
                    cbJCash.setText("Use Jaldee cash balance : Rs " + Config.getAmountNoOrTwoDecimalPoints(eligibleJcashAmt));
                    if (bookingModel.getEligibleJcashAmt() >= bookingModel.getAmountRequiredNow()) {
                        txtprepayamount.setText("₹0");
                    } else {
                        txtprepayamount.setText("₹" + Config.getAmountNoOrTwoDecimalPoints(bookingModel.getAmountRequiredNow() - bookingModel.getEligibleJcashAmt()));
                    }
                    if (eligibleJcashAmt >= bookingModel.getAmountRequiredNow()) {
                        tvJCashHint.setVisibility(View.GONE);
                        tvButtonName.setText("Confirm");
                        toolbartitle.setText("Confirm");
                        tv_payment_link.setVisibility(View.GONE);
                        gv_payment_modes.setVisibility(View.GONE);
                    } else {
                        tvJCashHint.setVisibility(View.VISIBLE);
                        tvButtonName.setText("Make Payment");
                        toolbartitle.setText("Confirm & pay");
                        if (indiaPay || internationalPay) {
                            gv_payment_modes.setVisibility(View.VISIBLE);
                            if (internationalPay && indiaPay) {
                                if (mPaymentData.get(0).getInternationalBankInfo().size() > 0 && mPaymentData.get(0).getIndiaBankInfo().size() > 0) {
                                    tv_payment_link.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            gv_payment_modes.setVisibility(View.GONE);
                            tv_payment_link.setVisibility(View.GONE);

                        }
                    }
                } else if (eligibleJcashAmt == 0) {
                    cbJCash.setChecked(false);
                    llJCash.setVisibility(View.GONE);
                    tvButtonName.setText("Make Payment");
                    toolbartitle.setText("Confirm & pay");
                    if (indiaPay || internationalPay) {
                        gv_payment_modes.setVisibility(View.VISIBLE);
                        if (internationalPay && indiaPay) {
                            if (mPaymentData.get(0).getInternationalBankInfo().size() > 0 && mPaymentData.get(0).getIndiaBankInfo().size() > 0) {
                                tv_payment_link.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        gv_payment_modes.setVisibility(View.GONE);
                        tv_payment_link.setVisibility(View.GONE);

                    }
                } else {
                    cbJCash.setChecked(false);
                    llJCash.setVisibility(View.GONE);
                    if (indiaPay || internationalPay) {
                        gv_payment_modes.setVisibility(View.VISIBLE);
                        if (internationalPay && indiaPay) {
                            if (mPaymentData.get(0).getInternationalBankInfo().size() > 0 && mPaymentData.get(0).getIndiaBankInfo().size() > 0) {
                                tv_payment_link.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        gv_payment_modes.setVisibility(View.GONE);
                        tv_payment_link.setVisibility(View.GONE);
                        gv_payment_modes.setVisibility(View.GONE);
                    }
                }
            } else {
                ll_cancellation_policy.setVisibility(View.GONE);
                llJCash.setVisibility(View.GONE);
                tv_payment_link.setVisibility(View.GONE);
                gv_payment_modes.setVisibility(View.GONE);
                ll_payment_mode.setVisibility(View.GONE);
                ll_servicePrepay.setVisibility(View.GONE);
                txtprepayamount.setText("0");
                if (bookingModel.getServiceBookingType() != null && bookingModel.getServiceBookingType().equalsIgnoreCase(Constants.SERVICE_BOOKING_TYPE_REQUEST)) {
                    tvButtonName.setText("Send Request");
                } else {
                    tvButtonName.setText("Confirm");
                }
                toolbartitle.setText("Confirm");

                cbJCash.setChecked(false);

            }
        } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.DONATION)) {

            if (bookingModel.getDonationAmount() != null && !bookingModel.getDonationAmount().trim().equalsIgnoreCase("")) {
                if (bookingModel.getFrom().equalsIgnoreCase(Constants.DONATION)) {
                    if (bookingModel.getDonationServiceInfo().getDescription() != null && !bookingModel.getDonationServiceInfo().getDescription().trim().isEmpty()) {
                        tvDescription.setVisibility(View.VISIBLE);
                        tvDescription.setText(bookingModel.getDonationServiceInfo().getDescription());
                    } else {
                        tvDescription.setVisibility(View.GONE);
                    }
                    if (bookingModel.getDonationServiceInfo().isPreInfoEnabled()) { //  check if pre-info is available for the service
                        ll_preinfo.setVisibility(View.VISIBLE);
                        if (bookingModel.getDonationServiceInfo().getPreInfoTitle() != null && !bookingModel.getDonationServiceInfo().getPreInfoTitle().trim().isEmpty()) {
                            tv_preInfoTitle.setText(bookingModel.getDonationServiceInfo().getPreInfoTitle());
                        } else {
                            ll_preinfo.setVisibility(View.GONE);
                        }

                        if (bookingModel.getDonationServiceInfo().getPreInfoText() != null && !bookingModel.getDonationServiceInfo().getPreInfoText().trim().isEmpty()) {
                            tv_preInfo.setText(Html.fromHtml(bookingModel.getDonationServiceInfo().getPreInfoText()));
                        } else {
                            ll_preinfo.setVisibility(View.GONE);
                        }
                    } else {
                        ll_preinfo.setVisibility(View.GONE);
                    }
                }
                ll_coupon.setVisibility(View.GONE);
                ll_cancellation_policy.setVisibility(View.GONE);
                llJCash.setVisibility(View.GONE);
                ll_serviceamount.setVisibility(View.GONE);
                ll_prepay.setVisibility(View.GONE);
                tvButtonName.setText("Make Payment");
                toolbartitle.setText("Confirm & pay");
                cbJCash.setChecked(false);
                if (indiaPay || internationalPay) {
                    gv_payment_modes.setVisibility(View.VISIBLE);
                    if (internationalPay && indiaPay) {
                        if (mPaymentData.get(0).getInternationalBankInfo().size() > 0 && mPaymentData.get(0).getIndiaBankInfo().size() > 0) {
                            tv_payment_link.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    gv_payment_modes.setVisibility(View.GONE);
                    tv_payment_link.setVisibility(View.GONE);

                }
            }
        }
    }

    public void ApiSubmit(JSONObject jsonObject, int accountId) {
        if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT) || bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
            if ((bookingModel.getServiceInfo() != null && bookingModel.getServiceInfo().getIsPrePayment() != null && bookingModel.getServiceInfo().getIsPrePayment().equalsIgnoreCase("true")) || (bookingModel.getCheckInInfo() != null && bookingModel.getCheckInInfo().isPrePayment())) {

                final Dialog mDialog = Config.getProgressDialog(ReconfirmationActivity.this, "");
                mDialog.show();

                Log.i("QueueObj ", jsonObject.toString());
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
                ApiInterface apiService = ApiClient.getClient(ReconfirmationActivity.this).create(ApiInterface.class);
                Call<AdvancePaymentDetails> call = null;

                if (bookingModel.getFrom() != null) {
                    if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                        call = apiService.getAdvancePaymentDetails("appointment", String.valueOf(accountId), body);
                    } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                        call = apiService.getAdvancePaymentDetails("waitlist", String.valueOf(accountId), body);
                    }
                }
                call.enqueue(new Callback<AdvancePaymentDetails>() {
                    @Override
                    public void onResponse(Call<AdvancePaymentDetails> call, Response<AdvancePaymentDetails> response) {
                        try {
                            if (mDialog.isShowing())
                                Config.closeDialog(getParent(), mDialog);
                            if (response.code() == 200) {

                                advancePaymentDetails = response.body();
                                float amountReqiuredNow = advancePaymentDetails.getAmountRequiredNow();
                                float eligibleJcashAmt = (float) bookingModel.getEligibleJcashAmt();
                                prepayAmount = String.valueOf(amountReqiuredNow);

                                if ((amountReqiuredNow - eligibleJcashAmt) > 0) {
                                    if (selectedpaymentMode == null) {
                                        Toast.makeText(mContext, "Please select mode of payment", Toast.LENGTH_LONG).show();
                                    } else {
                                        ApiBooking(jsonObject, accountId);
                                    }
                                } else {
                                    ApiBooking(jsonObject, accountId);
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
            } else {
                ApiBooking(jsonObject, accountId);
            }
        } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.DONATION)) {
            ApiBooking(jsonObject, accountId);
        }

    }

    public void ApiBooking(JSONObject jsonObject, int accountId) {
        final Dialog mDialog = Config.getProgressDialog(ReconfirmationActivity.this, ReconfirmationActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        ApiInterface apiService = ApiClient.getClient(ReconfirmationActivity.this).create(ApiInterface.class);
        Call<ResponseBody> call = null;
        if (bookingModel.getFrom() != null) {
            if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT) && serviceBookingType != null &&
                    serviceBookingType.equalsIgnoreCase(Constants.SERVICE_BOOKING_TYPE_REQUEST)) {
                if (bookingModel.getServiceInfo().getServiceType().equalsIgnoreCase("virtualService")
                        && bookingModel.getServiceInfo().isNoDateTime()) {

                    JSONObject virtualService = new JSONObject();
                    JSONObject waitobj = new JSONObject();
                    JSONArray waitlistArray = new JSONArray();

                    try {
                        String countryVirtualCode = "";

                        if (virtual_NmbrCCPicker.getSelectedCountryCode() != null) {
                            countryVirtualCode = virtual_NmbrCCPicker.getSelectedCountryCode();
                        } else {
                            DynamicToast.make(ReconfirmationActivity.this, "Countrycode needed", AppCompatResources.getDrawable(
                                            ReconfirmationActivity.this, R.drawable.ic_info_black),
                                    ContextCompat.getColor(ReconfirmationActivity.this, R.color.white), ContextCompat.getColor(ReconfirmationActivity.this, R.color.green), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (etVirtualNumber.getText().toString().trim().length() >= 7) {
                            if (bookingModel.getServiceInfo().getCallingMode() != null && bookingModel.getServiceInfo().getCallingMode().equalsIgnoreCase("whatsApp")) {
                                virtualService.put("WhatsApp", countryVirtualCode + etVirtualNumber.getText());
                                //mWhtsappCountryCode = countryVirtualCode;
                                //mWhatsappNumber = etVirtualNumber.getText().toString();
                            } else if (bookingModel.getServiceInfo().getCallingMode() != null && bookingModel.getServiceInfo().getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                virtualService.put("GoogleMeet", bookingModel.getServiceInfo().getVirtualCallingValue());
                            } else if (bookingModel.getServiceInfo().getCallingMode() != null && bookingModel.getServiceInfo().getCallingMode().equalsIgnoreCase("Zoom")) {
                                virtualService.put("Zoom", bookingModel.getServiceInfo().getVirtualCallingValue());
                            } else if (bookingModel.getServiceInfo().getCallingMode() != null && bookingModel.getServiceInfo().getCallingMode().equalsIgnoreCase("Phone")) {
                                virtualService.put("Phone", countryVirtualCode + etVirtualNumber.getText());
                                phoneNumber = etVirtualNumber.getText().toString();
                            } else if (bookingModel.getServiceInfo().getCallingMode() != null && bookingModel.getServiceInfo().getCallingMode().equalsIgnoreCase("VideoCall")) {
                                virtualService.put("VideoCall", bookingModel.getServiceInfo().getVirtualCallingValue());
                            }
                        } else {

                            String modeOfCalling = "";
                            if (bookingModel.getServiceInfo().getCallingMode() != null && bookingModel.getServiceInfo().getCallingMode().equalsIgnoreCase("whatsApp")) {
                                if (etVirtualNumber.getText().toString().trim().equalsIgnoreCase("")) {
                                    modeOfCalling = "Enter WhatsApp number";
                                } else {
                                    modeOfCalling = "Invalid WhatsApp number";
                                }
                            } else if (etVirtualNumber.getText().toString().trim().length() < 7 || (virtual_NmbrCCPicker.getSelectedCountryCode().equalsIgnoreCase("91") && etVirtualNumber.getText().toString().trim().length() != 10)) {
                                modeOfCalling = "Invalid Contact number";
                            }
                            DynamicToast.make(ReconfirmationActivity.this, modeOfCalling, AppCompatResources.getDrawable(
                                            ReconfirmationActivity.this, R.drawable.ic_info_black),
                                    ContextCompat.getColor(ReconfirmationActivity.this, R.color.white), ContextCompat.getColor(ReconfirmationActivity.this, R.color.green), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        jsonObject.putOpt("virtualService", virtualService);
                        waitobj.put("id", 0);
                        waitobj.put("firstName", mFirstName);
                        waitobj.put("lastName", mLastName);
                        if (emailId != null && !emailId.equalsIgnoreCase("")) {
                            waitobj.put("email", emailId);
                        }
                        waitlistArray.put(waitobj);
                        jsonObject.putOpt("appmtFor", waitlistArray);
                        jsonObject.put("phoneNumber", phoneNumber);
                        jsonObject.put("countryCode", countryCode);
                    } catch (
                            JSONException e) {
                        e.printStackTrace();
                    }

                }
                RequestBody bod = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

                call = apiService.AppointmentRequest(String.valueOf(accountId), bod);

            } else {
                if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                    call = apiService.Appointment(String.valueOf(accountId), body);
                } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                    call = apiService.Checkin(String.valueOf(accountId), body);
                } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.DONATION)) {
                    call = apiService.Donation(String.valueOf(accountId), body);
                }
            }
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (mDialog.isShowing())
                            Config.closeDialog(getParent(), mDialog);

                        if (response.code() == 200) {
                            SharedPreference.getInstance(mContext).setValue("refreshcheckin", "true");
                            JSONObject reader = new JSONObject(response.body().string());
                            if (reader.has("parent_uuid")) {
                                value = reader.getString("parent_uuid");
                            }
                            if (reader.has("uid")) {
                                value = reader.getString("uid");
                            }
                            if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT) && serviceBookingType != null &&
                                    serviceBookingType.equalsIgnoreCase(Constants.SERVICE_BOOKING_TYPE_REQUEST)) {   // for appointment request type
                                getApptConfirmationDetails(bookingModel.getAccountId());
                            } else {
                                if (reader.has("_prepaymentAmount")) {
                                    if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT) || bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                                        if ((bookingModel.getServiceInfo() != null && bookingModel.getServiceInfo().getIsPrePayment().equalsIgnoreCase("true")) || bookingModel.getCheckInInfo().isPrePayment()) {
                                            prepayAmount = reader.getString("_prepaymentAmount");
                                        }
                                    }
                                }
                                if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT) || bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                                    if (cbJCash.isChecked()) {
                                        getPrePayRemainingAmntNeeded(prepayAmount, cbJCash.isChecked(), bookingModel.getAccountId());
                                    } else {
                                        if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                                            getApptConfirmationId(accountId, bookingModel.getMessage());
                                        } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                                            getCheckinConfirmationId(accountId, bookingModel.getMessage());
                                        }
                                    }
                                } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.DONATION)) {
                                    getDonationConfirmationId(accountId);
                                }
                            }
                        } else if (response.code() == 422) {

                            String errorString = response.errorBody().string();

                            Config.logV("Error String-----------" + errorString);
                            Map<String, String> tokens = new HashMap<String, String>();
                            tokens.put("Customer", Config.toTitleCase(mSearchTerminology.getCustomer()));
                            tokens.put("provider", mSearchTerminology.getProvider());
                            tokens.put("arrived", mSearchTerminology.getArrived());
                            tokens.put("waitlisted", mSearchTerminology.getWaitlisted());
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
                            Toast.makeText(ReconfirmationActivity.this, sb.toString(), Toast.LENGTH_LONG).show();
                        } else {
                            String responseerror = response.errorBody().string();
                            Config.logV("Response--error-------------------------" + responseerror);
                            if (response.code() != 419)
                                Toast.makeText(ReconfirmationActivity.this, responseerror, Toast.LENGTH_LONG).show();
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
    }

    private void getDonationConfirmationId(int userId) {

        final ApiInterface apiService =
                ApiClient.getClient(ReconfirmationActivity.this).create(ApiInterface.class);
        Call<ActiveDonation> call = apiService.getActiveDonationUUID(value, String.valueOf(userId));
        call.enqueue(new Callback<ActiveDonation>() {
            @Override
            public void onResponse(Call<ActiveDonation> call, Response<ActiveDonation> response) {
                try {
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        activeDonation = response.body();
                        if (activeDonation != null) {
                            encId = activeDonation.getDonationEncId();
                            String inputString = SharedPreference.getInstance(mContext).getStringValue(Constants.QUESTIONNAIRE, "");

                            if (inputString != null && !inputString.trim().equalsIgnoreCase("")) {

                                QuestionnaireInput input = new QuestionnaireInput();
                                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                input = gson.fromJson(inputString, QuestionnaireInput.class);
                                ApiDontionSubmitQuestionnnaire(input, activeDonation.getUid());
                            }
                            if (selectedpaymentMode != null) {
                                try {
                                    final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
                                    mDialog.show();
                                    new PaymentGateway(ReconfirmationActivity.this, ReconfirmationActivity.this, iPaymentResponse).ApiGenerateHash(bookingModel.getDonationAmount(), selectedpaymentMode, value, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_DONATIONPAYMENT, serviceId, isInternational, encId, familyMEmID, paymentRequestId);
                                    mDialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(mContext, "Please select mode of payment", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.i("mnbbnmmnbbnm", e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ActiveDonation> call, Throwable t) {
            }
        });

    }

    private void ApiSubmitServiceOptionQuestionnnaire(QuestionnaireInput input, String uid) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        MediaType type = MediaType.parse("*/*");
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        for (int i = 0; i < serviceOptionImagePathList.size(); i++) {
            file = new File(serviceOptionImagePathList.get(i).getPath());///////

            mBuilder.addFormDataPart("files", file.getName(), RequestBody.create(type, file));
        }

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(input);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);
        mBuilder.addFormDataPart("question", "blob", body);
        RequestBody requestBody = mBuilder.build();

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<SubmitQuestionnaire> call = null;
        if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
            call = apiService.submitAppointmentServiceOptionQnr(uid, requestBody, bookingModel.getAccountId());
        } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
            call = apiService.submitWaitListServiceOptionQnr(uid, requestBody, bookingModel.getAccountId());
        }
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        call.enqueue(new Callback<SubmitQuestionnaire>() {
            @Override
            public void onResponse(Call<SubmitQuestionnaire> call, Response<SubmitQuestionnaire> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    if (response.code() == 200) {

                        SubmitQuestionnaire result = response.body();

                        if (result != null && result.getUrls().size() > 0) {

                            for (QuestionnaireUrls url : result.getUrls()) {

                                for (LabelPath p : serviceOptionImagePathList) {

                                    if (url.getUrl().contains(p.getFileName())) {

                                        p.setUrl(url.getUrl());
                                    }
                                }
                            }

                            uploadFilesToS3(serviceOptionImagePathList, result);
                        } else {
                            if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                                getApptConfirmationDetails(bookingModel.getAccountId());
                            } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                                getCheckinConfirmationDetails(bookingModel.getAccountId());
                            }
                        }

                    } else {
                        if (response.code() == 422) {
                            Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                        }
                        if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) { // make confirm APPT/CHECKIN if any QNR uploading error
                            getApptConfirmationDetails(bookingModel.getAccountId());
                        } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                            getCheckinConfirmationDetails(bookingModel.getAccountId());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SubmitQuestionnaire> call, Throwable t) {
                // Log error here since request failed
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
                Config.logV("Fail---------------" + t.toString());
            }
        });

    }

    private void ApiSubmitQuestionnnaire(QuestionnaireInput input, String uid) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        MediaType type = MediaType.parse("*/*");
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        for (int i = 0; i < imagePathList.size(); i++) {
           /*
            try {
                bitmap = MediaStore.Images.Media.getBitmap(mContext.getApplicationContext().getContentResolver(), Uri.fromFile(new File(imagePathList.get(i).getPath())));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (bitmap != null) {
                path = saveImage(bitmap);
                file = new File(path);
            } else {
                file = new File(imagePathList.get(i).getPath());
            }*/
            file = new File(imagePathList.get(i).getPath());///////

            mBuilder.addFormDataPart("files", file.getName(), RequestBody.create(type, file));
        }

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(input);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);
        mBuilder.addFormDataPart("question", "blob", body);
        RequestBody requestBody = mBuilder.build();

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<SubmitQuestionnaire> call = null;
        if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
            call = apiService.submitAppointmentQuestionnaire(uid, requestBody, bookingModel.getAccountId());
        } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
            call = apiService.submitWaitListQuestionnaire(uid, requestBody, bookingModel.getAccountId());
        }

        call.enqueue(new Callback<SubmitQuestionnaire>() {
            @Override
            public void onResponse(Call<SubmitQuestionnaire> call, Response<SubmitQuestionnaire> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    if (response.code() == 200) {

                        SubmitQuestionnaire result = response.body();

                        if (result != null && result.getUrls().size() > 0) {

                            for (QuestionnaireUrls url : result.getUrls()) {

                                for (LabelPath p : imagePathList) {

                                    if (url.getUrl().contains(p.getFileName())) {

                                        p.setUrl(url.getUrl());
                                    }
                                }
                            }

                            uploadFilesToS3(imagePathList, result);
                        } else {
                            if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                                getApptConfirmationDetails(bookingModel.getAccountId());
                            } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                                getCheckinConfirmationDetails(bookingModel.getAccountId());
                            }
                        }

                    } else {
                        if (response.code() == 422) {
                            Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                        }
                        if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) { // make confirm APPT/CHECKIN if any QNR uploading error
                            getApptConfirmationDetails(bookingModel.getAccountId());
                        } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                            getCheckinConfirmationDetails(bookingModel.getAccountId());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SubmitQuestionnaire> call, Throwable t) {
                // Log error here since request failed
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
                Config.logV("Fail---------------" + t.toString());
            }
        });

    }

    private void ApiDontionSubmitQuestionnnaire(QuestionnaireInput input, String uid) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        MediaType type = MediaType.parse("*/*");
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        for (int i = 0; i < imagePathList.size(); i++) {

            /*try {
                bitmap = MediaStore.Images.Media.getBitmap(mContext.getApplicationContext().getContentResolver(), Uri.fromFile(new File(imagePathList.get(i).getPath())));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap != null) {
                path = saveImage(bitmap);
                file = new File(path);
            } else {
                file = new File(imagePathList.get(i).getPath());
            }*/
            file = new File(imagePathList.get(i).getPath());////////

            mBuilder.addFormDataPart("files", file.getName(), RequestBody.create(type, file));
        }

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(input);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);
        mBuilder.addFormDataPart("question", "blob", body);
        RequestBody requestBody = mBuilder.build();

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ResponseBody> call = apiService.submitDonationQuestionnaire(uid, requestBody, bookingModel.getAccountId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    if (response.code() == 200) {
                        imagePathList.clear();


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
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
                Config.logV("Fail---------------" + t.toString());
            }
        });
    }

    private void getApptConfirmationId(int userId, String txt_addnote) {

        final ApiInterface apiService =
                ApiClient.getClient(ReconfirmationActivity.this).create(ApiInterface.class);
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
                            encId = activeAppointment.getAppointmentEncId();

                            if (bookingModel.getServiceInfo().getIsPrePayment().equalsIgnoreCase("true")) {
                                if (cbJCash.isChecked() && Double.parseDouble(prePayRemainingAmount) <= 0) {
                                    isGateWayPaymentNeeded(value, prepayAmount, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_PREPAYMENT, true, false, false, false, "JCASH", txt_addnote);
                                    //Toast.makeText(mContext,"Pay amount by Cash",Toast.LENGTH_LONG).show();
                                } else if (prepayAmount != null && Float.parseFloat(prepayAmount) > 0) {
                                    if (selectedpaymentMode != null) {
                                        if (cbJCash.isChecked()) {
                                            new PaymentGateway(ReconfirmationActivity.this, ReconfirmationActivity.this, iPaymentResponse).ApiGenerateHashWallet(prepayAmount, selectedpaymentMode, value, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_PREPAYMENT, serviceId, isInternational, encId, true, paymentRequestId);
                                        } else {
                                            new PaymentGateway(ReconfirmationActivity.this, ReconfirmationActivity.this, iPaymentResponse).ApiGenerateHash(prepayAmount, selectedpaymentMode, value, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_PREPAYMENT, serviceId, isInternational, encId, familyMEmID, paymentRequestId);
                                        }
                                    } else {
                                        Toast.makeText(mContext, "Please select mode of payment", Toast.LENGTH_LONG).show();
                                    }

                                    /*if (true) {
                                        // if (isPaytm) {
                                        PaytmPayment payment = new PaytmPayment(ReconfirmationActivity.this, iPaymentResponse);
                                        if (cbJCash.isChecked()) {
                                            payment.ApiGenerateHashPaytm2(value, prepayAmount, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_PREPAYMENT, "checkin", true, false, false, true, encId, mContext, ReconfirmationActivity.this);
                                        } else {
                                            payment.ApiGenerateHashPaytm(value, prepayAmount, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_PREPAYMENT, ReconfirmationActivity.this, ReconfirmationActivity.this, "", familyMEmID, encId);
                                        }
                                    } else {
                                        if (cbJCash.isChecked()) {
                                            new PaymentGateway(ReconfirmationActivity.this, ReconfirmationActivity.this).ApiGenerateHash2(value, prepayAmount, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_PREPAYMENT, "checkin", true, false, true, false);
                                        } else {
                                            new PaymentGateway(ReconfirmationActivity.this, ReconfirmationActivity.this).ApiGenerateHash1(value, prepayAmount, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_PREPAYMENT, "checkin", familyMEmID, Constants.SOURCE_PAYMENT);
                                        }
                                    }*/
                                }
                            } else {
                                if (attachedImagePathList.size() > 0) {
                                    ApiCommunicate(value, String.valueOf(bookingModel.getAccountId()), txt_addnote, dialog);
                                }
                                String inputString = SharedPreference.getInstance(mContext).getStringValue(Constants.QUESTIONNAIRE, "");
                                String soInputString = SharedPreference.getInstance(mContext).getStringValue(Constants.SERVICEOPTIONQNR, "");
                                if ((inputString != null && !inputString.trim().equalsIgnoreCase("")) ||
                                        (soInputString != null && !soInputString.trim().equalsIgnoreCase(""))) {
                                    if (inputString != null && !inputString.trim().equalsIgnoreCase("")) {

                                        QuestionnaireInput input = new QuestionnaireInput();
                                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                        input = gson.fromJson(inputString, QuestionnaireInput.class);
                                        ApiSubmitQuestionnnaire(input, activeAppointment.getUid());
                                    }
                                    if (soInputString != null && !soInputString.trim().equalsIgnoreCase("")) {

                                        QuestionnaireInput input = new QuestionnaireInput();
                                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                        input = gson.fromJson(soInputString, QuestionnaireInput.class);
                                        ApiSubmitServiceOptionQuestionnnaire(input, activeAppointment.getUid());
                                    }
                                } else {
                                    getApptConfirmationDetails(bookingModel.getAccountId());

                                }

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

    private void getCheckinConfirmationId(int id, String txt_addnote) {

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
                        activeCheckin = response.body();

                        if (activeCheckin != null) {
                            encId = activeCheckin.getCheckinEncId();

                            if (bookingModel.getCheckInInfo().isPrePayment()) {
                                if (cbJCash.isChecked() && Double.parseDouble(prePayRemainingAmount) <= 0) {
                                    isGateWayPaymentNeeded(value, prepayAmount, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_PREPAYMENT, true, false, false, false, "JCASH", txt_addnote);
                                    //Toast.makeText(mContext,"Pay amount by Cash",Toast.LENGTH_LONG).show();
                                } else if (prepayAmount != null && Float.parseFloat(prepayAmount) > 0) {
                                    if (selectedpaymentMode != null) {
                                        if (cbJCash.isChecked()) {
                                            new PaymentGateway(ReconfirmationActivity.this, ReconfirmationActivity.this, iPaymentResponse).ApiGenerateHashWallet(prepayAmount, selectedpaymentMode, value, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_PREPAYMENT, serviceId, isInternational, encId, true, paymentRequestId);
                                        } else {
                                            new PaymentGateway(ReconfirmationActivity.this, ReconfirmationActivity.this, iPaymentResponse).ApiGenerateHash(prepayAmount, selectedpaymentMode, value, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_PREPAYMENT, serviceId, isInternational, encId, familyMEmID, paymentRequestId);
                                        }
                                    } else {
                                        Toast.makeText(mContext, "Please select mode of payment", Toast.LENGTH_LONG).show();
                                    }
                                }
                            } else {

                                if (attachedImagePathList.size() > 0) {
                                    ApiCommunicate(value, String.valueOf(bookingModel.getAccountId()), txt_addnote, dialog);
                                }

                                String inputString = SharedPreference.getInstance(mContext).getStringValue(Constants.QUESTIONNAIRE, "");
                                String soInputString = SharedPreference.getInstance(mContext).getStringValue(Constants.SERVICEOPTIONQNR, "");
                                if ((inputString != null && !inputString.trim().equalsIgnoreCase("")) ||
                                        (soInputString != null && !soInputString.trim().equalsIgnoreCase(""))) {
                                    if (inputString != null && !inputString.trim().equalsIgnoreCase("")) {

                                        QuestionnaireInput input = new QuestionnaireInput();
                                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                        input = gson.fromJson(inputString, QuestionnaireInput.class);
                                        ApiSubmitQuestionnnaire(input, activeCheckin.getYnwUuid());
                                    }
                                    if (soInputString != null && !soInputString.trim().equalsIgnoreCase("")) {

                                        QuestionnaireInput input = new QuestionnaireInput();
                                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                        input = gson.fromJson(soInputString, QuestionnaireInput.class);
                                        ApiSubmitServiceOptionQuestionnnaire(input, activeCheckin.getYnwUuid());
                                    }
                                } else {
                                    getCheckinConfirmationDetails(bookingModel.getAccountId());
                                }
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
                Log.i("mnbbnmmnbbnm", t.toString());
                t.printStackTrace();
            }
        });

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
            MediaScannerConnection.scanFile(ReconfirmationActivity.this,
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

    public void isGateWayPaymentNeeded(String ynwUUID, final String amount, String accountID, String purpose, boolean isJcashUsed, boolean isreditUsed, boolean isRazorPayPayment, boolean isPayTmPayment, String paymentMode, String txt_addnote) {

        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        //  String uniqueID = UUID.randomUUID().toString();
        SharedPreference.getInstance(mContext).setValue("prePayment", false);
        JSONObject jsonObj = new JSONObject();
        try {

            jsonObj.put("accountId", accountID);
            jsonObj.put("amountToPay", Float.valueOf(amount));
            jsonObj.put("isJcashUsed", isJcashUsed);
            jsonObj.put("isPayTmPayment", isPayTmPayment);
            jsonObj.put("isRazorPayPayment", isRazorPayPayment);
            jsonObj.put("isreditUsed", isreditUsed);
            jsonObj.put("paymentMode", paymentMode);
            jsonObj.put("paymentPurpose", purpose);
            jsonObj.put("uuid", ynwUUID);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<WalletCheckSumModel> call = apiService.generateHash2(body);
        call.enqueue(new Callback<WalletCheckSumModel>() {

            @Override
            public void onResponse(Call<WalletCheckSumModel> call, Response<WalletCheckSumModel> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(ReconfirmationActivity.this, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());


                    if (response.code() == 200) {

                        WalletCheckSumModel respnseWCSumModel = response.body();

                        if (respnseWCSumModel != null && !respnseWCSumModel.isGateWayPaymentNeeded()) {

                            if (attachedImagePathList.size() > 0) {
                                ApiCommunicate(value, accountID, txt_addnote, dialog);
                            }
                            if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                                getApptConfirmationDetails(Integer.parseInt(accountID));
                            } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                                getCheckinConfirmationDetails(Integer.parseInt(accountID));
                            }
                        }
                    } else {
                        String responseerror = response.errorBody().string();
                        Config.logV("Response--error-------------------------" + responseerror);
                        Toast.makeText(mContext, responseerror, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<WalletCheckSumModel> call, Throwable t) {

            }
        });
    }

    private void getPrePayRemainingAmntNeeded(String prepayAmount, boolean isJcashSelected, int id) {
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<String> call = apiService.getPrePayRemainingAmnt(isJcashSelected, false, prepayAmount);
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                Config.logV("URL------GET Prepay remaining amount---------" + response.raw().request().url().toString().trim());
                Config.logV("Response--code-------------------------" + response.code());
                if (response.code() == 200) {
                    prePayRemainingAmount = response.body();
                    if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                        getApptConfirmationId(bookingModel.getAccountId(), bookingModel.getMessage());
                    } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                        getCheckinConfirmationId(bookingModel.getAccountId(), bookingModel.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i("PrePayRemainingAmntNeed", t.toString());
                t.printStackTrace();
            }
        });
    }


    private void paymentFinished() {
        if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT) || bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {

            if (attachedImagePathList != null && attachedImagePathList.size() > 0) {
                ApiCommunicate(value, String.valueOf(bookingModel.getAccountId()), bookingModel.getMessage(), dialog);
            }
            String inputString = SharedPreference.getInstance(mContext).getStringValue(Constants.QUESTIONNAIRE, "");
            String soInputString = SharedPreference.getInstance(mContext).getStringValue(Constants.SERVICEOPTIONQNR, "");
            if ((inputString != null && !inputString.trim().equalsIgnoreCase("")) ||
                    (soInputString != null && !soInputString.trim().equalsIgnoreCase(""))) {
                if (inputString != null && !inputString.trim().equalsIgnoreCase("")) {

                    QuestionnaireInput input = new QuestionnaireInput();
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    input = gson.fromJson(inputString, QuestionnaireInput.class);
                    if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                        ApiSubmitQuestionnnaire(input, activeAppointment.getUid());
                    } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                        ApiSubmitQuestionnnaire(input, activeCheckin.getYnwUuid());
                    }
                }
                if (soInputString != null && !soInputString.trim().equalsIgnoreCase("")) {

                    QuestionnaireInput input = new QuestionnaireInput();
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    input = gson.fromJson(soInputString, QuestionnaireInput.class);
                    if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                        ApiSubmitServiceOptionQuestionnnaire(input, activeAppointment.getUid());
                    } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                        ApiSubmitServiceOptionQuestionnnaire(input, activeCheckin.getYnwUuid());
                    }
                }
            } else {
                if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                    getApptConfirmationDetails(bookingModel.getAccountId());
                } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                    getCheckinConfirmationDetails(bookingModel.getAccountId());
                }
            }
        } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.DONATION)) {
            paymentFinishedDonationDialog();
        }
    }

    private void paymentError() {
        try {
            AlertDialog alertDialog = new AlertDialog.Builder(ReconfirmationActivity.this).create();
            alertDialog.setTitle("Payment Failed");
            alertDialog.setMessage("Unable to process your request.Please try again after some time");
            alertDialog.setCancelable(false);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                           /* if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT) || bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN) || bookingModel.getFrom().equalsIgnoreCase(Constants.TOKEN)) {
                                Intent homeIntent = new Intent(ReconfirmationActivity.this, Home.class);
                                startActivity(homeIntent);
                            }
                            finish();*/

                        }
                    });
            alertDialog.show();
            alertDialog.getButton(alertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.colorAccent));

        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentError..", e);
        }
    }

    private void paymentFinishedDonationDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(R.layout.successful_donation)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what would happen when positive button is clicked
                        Intent myPaymentIntent = new Intent(ReconfirmationActivity.this, MyPaymentsActivity.class);
                        myPaymentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //myPaymentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        myPaymentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(myPaymentIntent);
                        finish();

                    }
                })
                .show();
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));

        CustomTextViewMedium tvProviderName = alertDialog.findViewById(R.id.tv_providerName);
        CustomTextViewMedium tvLocationName = alertDialog.findViewById(R.id.tv_locationName);
        CustomTextViewMedium tvDonorName = alertDialog.findViewById(R.id.tv_donorName);
        CustomTextViewMedium tvCause = alertDialog.findViewById(R.id.tv_cause);
        CustomTextViewMedium tvAmountPaid = alertDialog.findViewById(R.id.tv_amountPaid);
        CustomTextViewMedium tvPostInfoTitle = alertDialog.findViewById(R.id.tv_postInfoTitle);
        CustomTextViewLight tvPostInfoText = alertDialog.findViewById(R.id.tv_postInfoText);
        LinearLayout llPostInfo = alertDialog.findViewById(R.id.ll_postInfo);
        if (activeDonation != null) {
            //Locale indian = new Locale("en", "IN");
            //NumberFormat formatter = NumberFormat.getCurrencyInstance(indian);
            //String currency = formatter.format(Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(activeDonation.getDonationAmount())));
            String currency = Config.getMoneyFormat(Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(activeDonation.getDonationAmount())));
            if (activeDonation.getProviderAccount() != null) {
                tvProviderName.setText(activeDonation.getProviderAccount().getAsJsonObject().get("businessName").getAsString());
            }
            if (activeDonation.getLocation() != null) {
                tvLocationName.setText(activeDonation.getLocation().getAsJsonObject().get("place").getAsString());
            }
            if (activeDonation.getDonor() != null) {
                String firstName = "", lastName = "";
                if (activeDonation.getDonor().getAsJsonObject().get("firstName") != null) {
                    firstName = activeDonation.getDonor().getAsJsonObject().get("firstName").getAsString();
                }
                if (activeDonation.getDonor().getAsJsonObject().get("lastName") != null) {
                    lastName = activeDonation.getDonor().getAsJsonObject().get("lastName").getAsString();
                }
                tvDonorName.setText(firstName + " " + lastName);
            }
            if (activeDonation.getService() != null) {
                tvCause.setText(activeDonation.getService().getAsJsonObject().get("name").getAsString());
            }
            tvAmountPaid.setText("₹ " + currency);
            if (activeDonation.getService().getAsJsonObject().get("postInfoEnabled") != null && activeDonation.getService().getAsJsonObject().get("postInfoEnabled").getAsBoolean()) {
                if (activeDonation.getService().getAsJsonObject().get("postInfoTitle") != null && !activeDonation.getService().getAsJsonObject().get("postInfoTitle").getAsString().isEmpty()) {
                    tvPostInfoTitle.setText(activeDonation.getService().getAsJsonObject().get("postInfoTitle").getAsString());
                    tvPostInfoTitle.setVisibility(View.VISIBLE);
                    llPostInfo.setVisibility(View.VISIBLE);
                }
                if (activeDonation.getService().getAsJsonObject().get("postInfoText") != null && !activeDonation.getService().getAsJsonObject().get("postInfoText").getAsString().isEmpty()) {
                    tvPostInfoText.setText(activeDonation.getService().getAsJsonObject().get("postInfoText").getAsString());
                    tvPostInfoText.setVisibility(View.VISIBLE);
                    llPostInfo.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void ApiCommunicate(String id, String accountID, String message, final BottomSheetDialog dialog) {
        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        MediaTypeAndExtention type;
        JSONObject captions = new JSONObject();
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        mBuilder.addFormDataPart("message", message);
        for (int i = 0; i < attachedImagePathList.size(); i++) {

            type = Config.getFileType(attachedImagePathList.get(i).getImagePath());

            file = new File(attachedImagePathList.get(i).getImagePath());//////////

            mBuilder.addFormDataPart("attachments", file.getName(), RequestBody.create(type.getMediaTypeWithExtention(), file));
            try {
                captions.put(String.valueOf(i), attachedImagePathList.get(i).getCaption());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        RequestBody body1 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), String.valueOf(captions));
        mBuilder.addFormDataPart("captions", "blob", body1);

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
        Call<ResponseBody> call = null;
        if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
            call = apiService.appointmentSendAttachments(id, Integer.parseInt(accountID.split("-")[0]), requestBody);
        } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
            call = apiService.waitlistSendAttachments(id, Integer.parseInt(accountID.split("-")[0]), requestBody);
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        //Toast.makeText(mContext, "Message sent successfully", Toast.LENGTH_LONG).show();///////
                        attachedImagePathList.clear();
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

    private void uploadFilesToS3(ArrayList<LabelPath> filesList, SubmitQuestionnaire result) {

        try {
            ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
            final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
            mDialog.show();
            List<Observable<?>> requests = new ArrayList<>();
            for (LabelPath l : filesList) {
                if (l.getUrl() != null && !l.getUrl().trim().equalsIgnoreCase("")) {
                    RequestBody requestFile = RequestBody.create(MediaType.parse(l.getType()), new File(l.getPath()));
                    requests.add(apiService.uploadPreSignedS3File(l.getUrl(), requestFile));
                }
            }
            // Zip all requests with the Function, which will receive the results.
            Observable.zip(requests, new Function<Object[], Object>() {
                        @Override
                        public Object apply(Object[] objects) throws Exception {
                            // Objects[] is an array of combined results of completed requests
                            // do something with those results and emit new event
                            return objects;
                        }
                    })
                    // After all requests had been performed the next observer will receive the Object, returned from Function

                    .subscribe(
                            // Will be triggered if all requests will end successfully (4xx and 5xx also are successful requests too)
                            new Consumer<Object>() {
                                @Override
                                public void accept(Object object) throws Exception {
                                    //Do something on successful completion of all requests
                                    Log.e("ListOf Calls", "0");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Stuff that updates the UI
                                            try {
                                                if (mDialog.isShowing())
                                                    Config.closeDialog(getParent(), mDialog);
                                                if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                                                    ApiCheckStatus(activeAppointment.getUid(), bookingModel.getAccountId(), result);
                                                } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                                                    ApiCheckStatus(activeCheckin.getYnwUuid(), bookingModel.getAccountId(), result);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            },
                            // Will be triggered if any error during requests will happen
                            new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable e) throws Exception {
                                    Log.e("ListOf Calls", "1");

                                    //Do something on error completion of requests
                                }
                            }
                    );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ApiCheckStatus(String uid, int accountId, SubmitQuestionnaire result) throws JSONException {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        JSONObject uploadObj = new JSONObject();
        JSONArray uploadArray = new JSONArray();

        for (int i = 0; i < result.getUrls().size(); i++) {

            JSONObject urlObj = new JSONObject();

            urlObj.put("uid", result.getUrls().get(i).getUid());
            urlObj.put("labelName", result.getUrls().get(i).getLabelName());
            urlObj.put("url", result.getUrls().get(i).getUrl());
            urlObj.put("document", result.getUrls().get(i).getDocument());
            if (result.getUrls().get(i).getColumnId() != null && !result.getUrls().get(i).getColumnId().trim().equalsIgnoreCase("")) {
                urlObj.put("columnId", result.getUrls().get(i).getColumnId());
                urlObj.put("gridOrder", 1);
            }

            uploadArray.put(urlObj);

        }


        uploadObj.putOpt("urls", uploadArray);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), uploadObj.toString());
        Call<ResponseBody> call = null;
        if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
            call = apiService.checkAppointmentUploadStatus(uid, accountId, body);
        } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
            call = apiService.checkWaitlistUploadStatus(uid, accountId, body);
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    if (response.code() == 200) {
                        if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                            getApptConfirmationDetails(accountId);
                        } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                            getCheckinConfirmationDetails(accountId);
                        }


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
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
                Config.logV("Fail---------------" + t.toString());
            }
        });

    }

    private void getApptConfirmationDetails(int userId) {

        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Call<ActiveAppointment> call = apiService.getActiveAppointmentUUID(value, String.valueOf(userId));
                call.enqueue(new Callback<ActiveAppointment>() {
                    @Override
                    public void onResponse(Call<ActiveAppointment> call, Response<ActiveAppointment> response) {
                        try {
                            if (mDialog.isShowing())
                                Config.closeDialog(ReconfirmationActivity.this, mDialog);

                            Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                            Config.logV("Response--code-------------------------" + response.code());
                            if (response.code() == 200) {
                                activeAppointment = response.body();
                                imagePathList.clear();
                                SharedPreference.getInstance(mContext).setValue(Constants.QUESTIONNAIRE, "");
                                SharedPreference.getInstance(mContext).setValue(Constants.QIMAGES, "");
                                serviceOptionImagePathList.clear();
                                SharedPreference.getInstance(mContext).setValue(Constants.SERVICEOPTIONQNR, "");
                                SharedPreference.getInstance(mContext).setValue(Constants.SERVICEOPTIONQIMAGES, "");


                                if (activeAppointment != null) {
                                    String serviceDescription = "";
                                    if (bookingModel.getServiceInfo().getDescription() != null && !bookingModel.getServiceInfo().getDescription().trim().isEmpty()) {
                                        serviceDescription = bookingModel.getServiceInfo().getDescription();
                                    }
                                    encId = activeAppointment.getAppointmentEncId();

                                    Intent checkin = new Intent(ReconfirmationActivity.this, BookingConfirmation.class);
                                    if (bookingModel.getEmailId() != null && !bookingModel.getEmailId().equalsIgnoreCase("")) {
                                        checkin.putExtra("email", bookingModel.getEmailId());
                                    }
                                    checkin.putExtra("serviceDescription", serviceDescription);
                                    checkin.putExtra("terminology", mSearchTerminology.getProvider());
                                    checkin.putExtra("from", "");
                                    checkin.putExtra("typeOfService", Constants.APPOINTMENT);
                                    checkin.putExtra("waitlistPhonenumberCountryCode", bookingModel.getCountryCode());
                                    checkin.putExtra("waitlistPhonenumber", bookingModel.getPhoneNumber());
                                    checkin.putExtra("accountID", String.valueOf(bookingModel.getAccountId()));
                                    if (bookingModel.getServiceInfo().getLivetrack() != null && bookingModel.getServiceInfo().getLivetrack().equalsIgnoreCase("true")) {
                                        checkin.putExtra("livetrack", true);
                                    } else {
                                        checkin.putExtra("livetrack", false);
                                    }
                                    checkin.putExtra("uid", activeAppointment.getUid());
                                    checkin.putExtra("providerLogoUrl", providerLogoUrl);

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

                        if (mDialog.isShowing())
                            Config.closeDialog(ReconfirmationActivity.this, mDialog);

                    }
                });
            }
        }, 1000);


    }

    private void getCheckinConfirmationDetails(int id) {

        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Call<ActiveCheckIn> call = apiService.getActiveCheckInUUID(value, String.valueOf(id));
                call.enqueue(new Callback<ActiveCheckIn>() {
                    @Override
                    public void onResponse(Call<ActiveCheckIn> call, Response<ActiveCheckIn> response) {

                        if (mDialog.isShowing())
                            Config.closeDialog(getParent(), mDialog);
                        try {

                            if (response.code() == 200) {
                                ActiveCheckIn activeAppointment = response.body();
                                imagePathList.clear();
                                SharedPreference.getInstance(mContext).setValue(Constants.QUESTIONNAIRE, "");
                                SharedPreference.getInstance(mContext).setValue(Constants.QIMAGES, "");
                                serviceOptionImagePathList.clear();
                                SharedPreference.getInstance(mContext).setValue(Constants.SERVICEOPTIONQNR, "");
                                SharedPreference.getInstance(mContext).setValue(Constants.SERVICEOPTIONQIMAGES, "");

                                if (activeAppointment != null) {
                                    String serviceDescription = "";
                                    if (bookingModel.getCheckInInfo().getDescription() != null && !bookingModel.getCheckInInfo().getDescription().trim().isEmpty()) {
                                        serviceDescription = bookingModel.getCheckInInfo().getDescription();
                                    }
                                    encId = activeAppointment.getCheckinEncId();

                                    Intent checkin = new Intent(ReconfirmationActivity.this, BookingConfirmation.class);
                                    if (bookingModel.getEmailId() != null && !bookingModel.getEmailId().equalsIgnoreCase("")) {
                                        checkin.putExtra("email", bookingModel.getEmailId());
                                    }
                                    checkin.putExtra("serviceDescription", serviceDescription);
                                    checkin.putExtra("terminology", mSearchTerminology.getProvider());
                                    checkin.putExtra("from", "");
                                    checkin.putExtra("typeOfService", Constants.CHECKIN);
                                    checkin.putExtra("waitlistPhonenumberCountryCode", bookingModel.getCountryCode());
                                    checkin.putExtra("waitlistPhonenumber", bookingModel.getPhoneNumber());
                                    checkin.putExtra("livetrack", bookingModel.getCheckInInfo().isLivetrack());
                                    checkin.putExtra("accountID", String.valueOf(id));
                                    checkin.putExtra("uid", activeAppointment.getYnwUuid());
                                    checkin.putExtra("providerLogoUrl", providerLogoUrl);

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

                        if (mDialog.isShowing())
                            Config.closeDialog(getParent(), mDialog);
                    }
                });

            }
        }, 1000);
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        try {
            RazorpayModel razorpayModel = new RazorpayModel(paymentData);
            // new PaymentGateway(mContext, ReconfirmationActivity.this).sendPaymentStatus(razorpayModel, "SUCCESS");
            ApiCheckRazorpayPaymentStatus(razorpayModel);
        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        paymentError();
    }

    private void ApiCheckRazorpayPaymentStatus(RazorpayModel razorpayModel) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        JSONObject jsonObj = new JSONObject();
        try {

            // jsonObj.put("paymentId", razorpayModel.getRazorpay_payment_id());
            jsonObj.put("paymentId", razorpayModel.getRazorpay_payment_id());
            jsonObj.put("orderId", razorpayModel.getRazorpay_order_id());
            jsonObj.put("signature", razorpayModel.getRazorpay_signature());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call = apiService.checkRazorpayPaymentStatus(body, bookingModel.getAccountId());
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(ReconfirmationActivity.this, mDialog);
                    if (response.code() == 200) {
                        paymentFinished();
                    } else {
                        paymentError();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void ApiCheckPaytmPaymentStatus(String orderId) {
        /*try {
            TimeUnit.SECONDS.sleep(1);  // important delay for getting result from paytm
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        JSONObject jsonObj = new JSONObject();
        try {

            jsonObj.put("paymentId", orderId);
            jsonObj.put("orderId", orderId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call = apiService.checkPaytmPaymentStatus(body, bookingModel.getAccountId());
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(ReconfirmationActivity.this, mDialog);
                    if (response.code() == 200) {
                        paymentFinished();
                    } else {
                        paymentError();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String TAG = "PaytmPayment";
        Log.e(TAG, " result code " + resultCode);
        // -1 means successful  // 0 means failed
        // one error is - nativeSdkForMerchantMessage : networkError
        super.onActivityResult(requestCode, resultCode, data);
        String bundle = data.getStringExtra("response");//.getString("ORDERID");
        String orderId = "";
        try {
            JSONObject jsonObject = new JSONObject(bundle);
            orderId = jsonObject.getString("ORDERID");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (requestCode == 01 && data != null) {
            if (data.getStringExtra("response").contains("TXN_SUCCESS")) {
                sendPaymentResponse("TXN_SUCCESS", orderId);
                Toast.makeText(this, "Payment Successful", Toast.LENGTH_LONG).show();
            } else {
                sendPaymentResponse("TXN_FAILED", orderId);
            }
            Log.e(TAG, " data " + data.getStringExtra("nativeSdkForMerchantMessage"));
            Log.e(TAG, " data response - " + data.getStringExtra("response"));
            /*
            data response - {"BANKNAME":"WALLET","BANKTXNID":"1394221115",
            "CHECKSUMHASH":"7jRCFIk6eRmrep+IhnmQrlrL43KSCSXrmM+VHP5pH0ekXaaxjt3MEgd1N9mLtWyu4VwpWexHOILCTAhybOo5EVDmAEV33rg2VAS/p0PXdk\u003d",
            "CURRENCY":"INR","GATEWAYNAME":"WALLET","MID":"EAcP3138556","ORDERID":"100620202152",
            "PAYMENTMODE":"PPI","RESPCODE":"01","RESPMSG":"Txn Success","STATUS":"TXN_SUCCESS/TXN_FAILURE",
            "TXNAMOUNT":"2.00","TXNDATE":"2020-06-10 16:57:45.0","TXNID":"2020061011121280011018328631290118"}
             */
        } else {
            Log.e(TAG, " payment failed");
            Toast.makeText(this, "Payment Failed ", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void sendPaymentResponse(String paymentStatus, String orderid) {
        if (paymentStatus.equalsIgnoreCase("TXN_SUCCESS")) {
            ApiCheckPaytmPaymentStatus(orderid);
        } else {
            paymentError();
        }
    }

    @Override
    public void update() {

    }

    @Override
    public void setPaymentRequestId(String paymntRequestId) {
        paymentRequestId = paymntRequestId;
    }

    @Override
    public void selectedPaymentMode(String mode) {
        selectedpaymentMode = mode;
    }

    private void ApiJaldeegetS3Coupons(String s3Coupons) {

        try {

            if (s3Coupons != null) {
                s3couponList.clear();
                s3couponList = new Gson().fromJson(s3Coupons, new TypeToken<ArrayList<CoupnResponse>>() {
                }.getType());
                if (s3couponList.size() != 0 || (providerCouponList != null && providerCouponList.size() != 0)) {
                    llCoupons.setVisibility(View.VISIBLE);
                    ll_coupon.setVisibility(View.VISIBLE);
                } else {
                    llCoupons.setVisibility(View.GONE);
                    ll_coupon.setVisibility(View.GONE);
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
                    ll_coupon.setVisibility(View.VISIBLE);
                } else {
                    llCoupons.setVisibility(View.GONE);
                    ll_coupon.setVisibility(View.GONE);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void cpns(ArrayList<String> mcouponArraylist) {
        iCpn = (ICpn) this;
        couponArraylist = mcouponArraylist;
        //CouponApliedOrNotDetails c = new CouponApliedOrNotDetails();
        /*if (userMessage != null) {

            if (isUser) {
                getAdvancePaymentDetails(userMessage, userId);
            } else {
                getAdvancePaymentDetails(userMessage, providerId);
            }
        }*/
    }


    @Override
    public void sendFamilyMemberDetails(int fmemId, String firstName, String lastName, String phone, String email, String conCode) {
        mFirstName = firstName;
        mLastName = lastName;
        phoneNumber = phone;
        familyMEmID = fmemId;
        emailId = email;
        countryCode = conCode;
        tvPhoneNumber.setText(countryCode + " " + phoneNumber);
        String cCode = countryCode.replace("+", "");
        virtual_NmbrCCPicker.setCountryForPhoneCode(Integer.parseInt(cCode));
        //et_countryCode.setText(countryCode);

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
        tvCustomerName.setText(mFirstName + " " + mLastName);


    }

    @Override
    public void sendFamilyMemberDetails(int consumerId, String firstName, String lastName, String phone, String email, String countryCode, String whtsappCountryCode, String whatsappNumber, String telegramCountryCode, String telegramNumber, String age, JSONArray preferredLanguages, JSONObject selectedPincode, String gender) {

    }

    @Override
    public void refreshMultipleMEmList(ArrayList<FamilyArrayModel> familyList) {
        MultiplefamilyList.clear();
        MultiplefamilyList.addAll(familyList);

        recycle_family.setVisibility(View.VISIBLE);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ReconfirmationActivity.this);
        recycle_family.setLayoutManager(mLayoutManager);
        MultipleFamilyMemberAdapter mFamilyAdpater = new MultipleFamilyMemberAdapter(familyList, mContext, ReconfirmationActivity.this);
        recycle_family.setAdapter(mFamilyAdpater);
        mFamilyAdpater.notifyDataSetChanged();
        tvCustomerName.setVisibility(View.GONE);
    }

    @Override
    public void sendFamilyMbrPhoneAndEMail(String phone, String email, String countryCode) {

    }

    @Override
    public void closeActivity() {

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
}