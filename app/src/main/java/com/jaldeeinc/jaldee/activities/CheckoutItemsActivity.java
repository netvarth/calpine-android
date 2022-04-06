package com.jaldeeinc.jaldee.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jaldeeinc.jaldee.Interface.IAddressInterface;
import com.jaldeeinc.jaldee.Interface.ICpn;
import com.jaldeeinc.jaldee.Interface.IEditContact;
import com.jaldeeinc.jaldee.Interface.IPaymentGateway;
import com.jaldeeinc.jaldee.Interface.IPaymentResponse;
import com.jaldeeinc.jaldee.Interface.ISelectedTime;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.AddressAdapter;
import com.jaldeeinc.jaldee.adapter.CheckoutItemsAdapter;
import com.jaldeeinc.jaldee.adapter.CouponlistAdapter;
import com.jaldeeinc.jaldee.adapter.PaymentModeAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.AddressDialog;
import com.jaldeeinc.jaldee.custom.BorderImageView;
import com.jaldeeinc.jaldee.custom.CustomEditTextRegular;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewLight;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.EditContactDialog;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;
import com.jaldeeinc.jaldee.custom.SlotSelection;
import com.jaldeeinc.jaldee.custom.StoreDetailsDialog;
import com.jaldeeinc.jaldee.custom.SuccessDialog;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.Address;
import com.jaldeeinc.jaldee.model.CartItemModel;
import com.jaldeeinc.jaldee.model.LabelPath;
import com.jaldeeinc.jaldee.model.OrderItem;
import com.jaldeeinc.jaldee.model.QuestionnaireInput;
import com.jaldeeinc.jaldee.model.RazorpayModel;
import com.jaldeeinc.jaldee.payment.PaymentGateway;
import com.jaldeeinc.jaldee.response.ActiveOrders;
import com.jaldeeinc.jaldee.response.AdvancePaymentDetailsOrder;
import com.jaldeeinc.jaldee.response.Catalog;
import com.jaldeeinc.jaldee.response.CoupnResponse;
import com.jaldeeinc.jaldee.response.OrderResponse;
import com.jaldeeinc.jaldee.response.PayMode;
import com.jaldeeinc.jaldee.response.PaymentModel;
import com.jaldeeinc.jaldee.response.ProfileModel;
import com.jaldeeinc.jaldee.response.Provider;
import com.jaldeeinc.jaldee.response.ProviderCouponResponse;
import com.jaldeeinc.jaldee.response.QuestionnaireUrls;
import com.jaldeeinc.jaldee.response.Schedule;
import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.jaldeeinc.jaldee.response.StoreDetails;
import com.jaldeeinc.jaldee.response.SubmitQuestionnaire;
import com.jaldeeinc.jaldee.response.WalletCheckSumModel;
import com.jaldeeinc.jaldee.utils.DialogUtilsKt;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.omjoonkim.skeletonloadingview.SkeletonLoadingView;
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
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import kotlin.Unit;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutItemsActivity extends AppCompatActivity implements IAddressInterface, ISelectedTime, IPaymentResponse, PaymentResultWithDataListener, IEditContact, ICpn, IPaymentGateway {

    private Context mContext;
    private IAddressInterface iAddressInterface;
    ArrayList<Address> addressList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private AddressAdapter addressAdapter;
    boolean isVirtualItemsOnly = false;

    @BindView(R.id.ll_group)
    LinearLayout ll_group;

    @BindView(R.id.cb_jCash)
    CheckBox cbJCash;

    @BindView(R.id.ll_jCash)
    LinearLayout llJCash;

    @BindView(R.id.rv_items)
    RecyclerView rvItems;

    @BindView(R.id.iv_spImage)
    BorderImageView ivSpImage;

    @BindView(R.id.tv_spName)
    CustomTextViewSemiBold tvSpName;

    @BindView(R.id.tv_locationName)
    CustomTextViewMedium tvLocationName;

    @BindView(R.id.et_specialNotes)
    CustomEditTextRegular etSpecialNotes;

    @BindView(R.id.tv_name)
    CustomTextViewMedium tvName;

    @BindView(R.id.tv_number)
    CustomTextViewMedium tvMobileNumber;

    @BindView(R.id.tv_mailId)
    CustomTextViewMedium tvEmailId;

    @BindView(R.id.tv_address)
    CustomTextViewMedium tvDeliveryAddress;

    @BindView(R.id.tv_storeDetails)
    CustomTextViewMedium tvStoreDetails;

    @BindView(R.id.tv_phoneNumber)
    CustomTextViewMedium tvPhoneNumber;

    @BindView(R.id.tv_email)
    CustomTextViewMedium tvMailId;

    @BindView(R.id.tv_changeAddress)
    CustomTextViewSemiBold tvChangeAddress;

    @BindView(R.id.ll_storeDetails)
    LinearLayout llStoreDetails;

    @BindView(R.id.shimmer)
    SkeletonLoadingView shimmer;

    @BindView(R.id.rl_deliveryFee)
    RelativeLayout rlDeliveryFee;

    @BindView(R.id.rl_prepayment)
    RelativeLayout rlPrepayment;

    @BindView(R.id.rv_itemTotal)
    RelativeLayout rlItemTotal;

    @BindView(R.id.tv_itemsBill)
    CustomTextViewMedium tvItemsBill;

    @BindView(R.id.tv_deliveryBill)
    CustomTextViewMedium tvDeliveryBill;

    @BindView(R.id.tv_bill)
    CustomTextViewSemiBold tvBill;

    @BindView(R.id.tv_timeSlot)
    CustomTextViewSemiBold tvTimeSlot;

    @BindView(R.id.tv_changeTime)
    CustomTextViewSemiBold tvChangeTime;

    @BindView(R.id.rb_group)
    RadioGroup rbGroup;

    @BindView(R.id.rb_store)
    RadioButton rbStore;

    @BindView(R.id.rb_home)
    RadioButton rbHome;

    @BindView(R.id.ll_delivery)
    LinearLayout llDelivery;

    @BindView(R.id.ll_contactDetails)
    LinearLayout llContactDetails;

    @BindView(R.id.tv_contactNumber)
    CustomTextViewSemiBold tvContactNumber;

    @BindView(R.id.tv_advance)
    CustomTextViewSemiBold tvAdvance;

    @BindView(R.id.tv_contactEmail)
    CustomTextViewSemiBold tvContactEmail;

    @BindView(R.id.tv_changeContactInfo)
    CustomTextViewSemiBold tvChangeContactInfo;

    @BindView(R.id.tv_countryCode)
    CustomTextViewSemiBold tvCountryCode;

    @BindView(R.id.cv_placeOrder)
    CardView cvPlaceOrder;

    @BindView(R.id.ll_address)
    LinearLayout llAddress;

    @BindView(R.id.ll_addNew)
    LinearLayout llAddNew;

    @BindView(R.id.cv_back)
    CardView cvBack;

    @BindView(R.id.nested)
    ScrollView scrollView;

    @BindView(R.id.rl_coupon)
    RelativeLayout rlCoupon;

    @BindView(R.id.et_couponCode)
    CustomEditTextRegular etCouponCode;

    @BindView(R.id.tv_apply)
    CustomTextViewBold tvApply;

    @BindView(R.id.list)
    RecyclerView list;

    @BindView(R.id.rv_tax)
    RelativeLayout rlTax;

    @BindView(R.id.tv_totalTax)
    CustomTextViewMedium tvTotalTax;

    @BindView(R.id.rl_jdnDiscount)
    RelativeLayout rlJdnDiscount;

    @BindView(R.id.tv_jdnDiscount)
    CustomTextViewMedium tvJdnDiscount;

    @BindView(R.id.rl_jCoupon)
    RelativeLayout rlJCoupon;

    @BindView(R.id.tv_jCoupon)
    CustomTextViewMedium tvJCoupon;

    @BindView(R.id.tv_jCouponTxt)
    CustomTextViewMedium tvJCouponTxt;

    @BindView(R.id.rl_pCoupon)
    RelativeLayout rlPCoupon;

    @BindView(R.id.tv_pCoupon)
    CustomTextViewMedium tvPCoupon;

    @BindView(R.id.tv_pCouponTxt)
    CustomTextViewMedium tvPCouponTxt;

    @BindView(R.id.rl_totalDiscount)
    RelativeLayout rltotalDiscount;

    @BindView(R.id.tv_totalDiscount)
    CustomTextViewMedium tvTotalDiscount;

    @BindView(R.id.cv_coupon)
    CardView cvCoupon;

    @BindView(R.id.ll_advanceAmount)
    LinearLayout llAdvanceAmount;

    @BindView(R.id.tv_advanceAmount)
    CustomTextViewSemiBold tvAdvanceAmount;

    @BindView(R.id.tv_jCashHint)
    CustomTextViewMedium tvJCashHint;

    public String orderId;
    public boolean indiaPay = false;
    public boolean internationalPay = false;
    public String PAYMENT_LINK_FLAG;
    public ArrayList<PayMode> payModes = new ArrayList<>();
    public PaymentModeAdapter paymentModeAdapter;
    boolean isInternational = false;
    public String selectedpaymentMode;
    public IPaymentGateway iPaymentGateway;
    private boolean isStore = true;
    private DatabaseHandler db;
    private String selectedDate;
    private String selectedTime = "";
    private String ynwUUID = null;
    private String prepayAmount = "";
    private int providerId, catalogId, uniqueId;
    private AddressDialog addressDialog;
    private IPaymentResponse iPaymentResponse;
    private CheckoutItemsAdapter checkoutItemsAdapter;
    ArrayList<Catalog> catalogs = new ArrayList<>();
    private ArrayList<CartItemModel> cartItemsList = new ArrayList<>();
    private SearchViewDetail mBusinessDataList = new SearchViewDetail();
    private OrderResponse orderResponse = new OrderResponse();
    private ArrayList<Schedule> storePickupSchedulesList = new ArrayList<>();
    private ArrayList<Schedule> homeDeliverySchedulesList = new ArrayList<>();
    private SlotSelection slotSelection;
    private ISelectedTime iSelectedTime;
    private BottomSheetDialog dialog;
    private String phoneNumber = "", countryCode = "", email = "";
    private ActiveOrders activeOrders = new ActiveOrders();
    public ArrayList<LabelPath> imagePathList = new ArrayList<>();
    private SuccessDialog successDialog;
    ArrayList<PaymentModel> mPaymentData = new ArrayList<>();
    private ProfileModel profileDetails = new ProfileModel();
    private EditContactDialog editContactDialog;
    private IEditContact iEditContact;
    private String homeDeliveryEmail, homeDeliveryNumber;
    private StoreDetails storeInfo = new StoreDetails();
    private StoreDetailsDialog storeDetailsDialog;
    ArrayList<CoupnResponse> s3couponList = new ArrayList<>();
    ArrayList<ProviderCouponResponse> providerCouponList = new ArrayList<>();
    AdvancePaymentDetailsOrder advancePaymentDetailsOrder = new AdvancePaymentDetailsOrder();
    String couponEntered, prePayRemainingAmount = "";

    ArrayList<String> couponArraylist = new ArrayList<>();
    private CouponlistAdapter mAdapter;
    private Address selectedAddress = new Address();

    private ICpn iCpn;
    public String path;
    public Bitmap bitmap;
    public File file;
    public File f;
    private static final String IMAGE_DIRECTORY = "/Jaldee" + "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        ButterKnife.bind(CheckoutItemsActivity.this);
        mContext = CheckoutItemsActivity.this;
        iAddressInterface = (IAddressInterface) this;
        iSelectedTime = (ISelectedTime) this;
        iPaymentResponse = (IPaymentResponse) this;
        iEditContact = (IEditContact) this;
        db = new DatabaseHandler(mContext);
        iPaymentGateway = this;

        uniqueId = db.getUniqueId();
        catalogId = db.getCatalogId();
        providerId = db.getAccountId();
        getQuestionnaireImages();

        getCatalogDetails(providerId);
        getProviderDetails(uniqueId);
        // to fetch user addresses list

        Typeface font_style = Typeface.createFromAsset(mContext.getAssets(), "fonts/JosefinSans-SemiBold.ttf");
        rbHome.setTypeface(font_style);
        rbStore.setTypeface(font_style);

        cbJCash.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (cbJCash.isChecked()) {
                    if (advancePaymentDetailsOrder.getEligibleJcashAmt().get("jCashAmt").getAsDouble() >= advancePaymentDetailsOrder.getAdvanceAmount()) {
                        llAdvanceAmount.setVisibility(View.GONE);
                    } else {
                        double amnt = advancePaymentDetailsOrder.getEligibleJcashAmt().get("jCashAmt").getAsDouble() - advancePaymentDetailsOrder.getAdvanceAmount();
                        tvAdvanceAmount.setText("An advance of ₹\u00a0" + Config.getAmountNoOrTwoDecimalPoints(Math.abs(amnt)) + " required");
                        llAdvanceAmount.setVisibility(View.VISIBLE);
                        tvJCashHint.setVisibility(View.VISIBLE);
                    }
                } else {
                    if ((catalogs.get(0).getAdvanceAmount() != null && Double.parseDouble(catalogs.get(0).getAdvanceAmount()) > 0) || catalogs.get(0).getPaymentType().equalsIgnoreCase(Constants.FULLAMOUNT)) {
                        tvAdvanceAmount.setText("An advance of ₹\u00a0" + Config.getAmountNoOrTwoDecimalPoints(advancePaymentDetailsOrder.getAdvanceAmount()) + " required");
                        llAdvanceAmount.setVisibility(View.VISIBLE);
                    }
                    tvJCashHint.setVisibility(View.GONE);
                }
            }
        });
        tvChangeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddressList();
                showAddressDialog();

            }
        });

        llAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAddressDialog();

            }
        });

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        tvStoreDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getStoreDetails(providerId);

            }
        });

        cvCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent iCoupons = new Intent(CheckoutItemsActivity.this, CouponActivity.class);
                iCoupons.putExtra("uniqueID", String.valueOf(uniqueId));
                iCoupons.putExtra("accountId", String.valueOf(mBusinessDataList.getId()));
                startActivity(iCoupons);
            }
        });

        rbStore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isStore = true;
                    rbStore.setChecked(true);
                    rbHome.setChecked(false);
                    getStorePickupSchedules(catalogId, providerId);
                    updateBill();
                } else {
                    isStore = false;
                    rbHome.setChecked(true);
                    rbStore.setChecked(false);
                }
            }
        });

        rbHome.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isStore = false;
                    rbHome.setChecked(true);
                    rbStore.setChecked(false);
                    getHomeDeliverySchedules(catalogId, providerId);
                    getAddressList();
                    updateBill();
                } else {
                    isStore = true;
                    rbHome.setChecked(false);
                    rbStore.setChecked(true);
                }
            }
        });

        tvChangeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedDate != null && !selectedDate.trim().equalsIgnoreCase("")) {
                    if (isStore) {
                        slotSelection = new SlotSelection(mContext, storePickupSchedulesList, iSelectedTime, selectedDate);
                    } else {
                        slotSelection = new SlotSelection(mContext, homeDeliverySchedulesList, iSelectedTime, selectedDate);
                    }
                    slotSelection.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
                    slotSelection.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    slotSelection.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    slotSelection.show();
                    slotSelection.setCancelable(true);
                    DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                    int width = (int) (metrics.widthPixels * 1);
                    slotSelection.getWindow().setGravity(Gravity.BOTTOM);
                    slotSelection.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                }

            }
        });

        tvChangeContactInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editContactDialog = new EditContactDialog(mContext, iEditContact, phoneNumber, email, countryCode);
                editContactDialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
                editContactDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                editContactDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                editContactDialog.show();
                editContactDialog.setCancelable(true);
                DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                int width = (int) (metrics.widthPixels * 1);
                editContactDialog.getWindow().setGravity(Gravity.BOTTOM);
                editContactDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);


            }
        });

        tvContactEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editContactDialog = new EditContactDialog(mContext, iEditContact, phoneNumber, email, countryCode);
                editContactDialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
                editContactDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                editContactDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                editContactDialog.show();
                editContactDialog.setCancelable(true);
                DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                int width = (int) (metrics.widthPixels * 1);
                editContactDialog.getWindow().setGravity(Gravity.BOTTOM);
                editContactDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);


            }
        });

        cvPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                placeOrder(providerId);
            }
        });

        // to scroll edittext or text inside scrollview
        etSpecialNotes.setMovementMethod(new ScrollingMovementMethod());
        scrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                etSpecialNotes.getParent().requestDisallowInterceptTouchEvent(false);

                return false;
            }
        });

        etSpecialNotes.setOnTouchListener((v, event) -> {

            etSpecialNotes.getParent().requestDisallowInterceptTouchEvent(true);

            return false;
        });

        tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                couponEntered = etCouponCode.getEditableText().toString();
                boolean found = false;
                for (int index = 0; index < couponArraylist.size(); index++) {
                    if (couponArraylist.get(index).equals(couponEntered)) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    Toast.makeText(CheckoutItemsActivity.this, "Coupon already added", Toast.LENGTH_SHORT).show();
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
                    etCouponCode.setText("");
                    Toast.makeText(CheckoutItemsActivity.this, couponEntered + " " + "Added", Toast.LENGTH_SHORT).show();
                } else {
                    if (couponEntered.equals("")) {
                        Toast.makeText(CheckoutItemsActivity.this, "Enter a coupon", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CheckoutItemsActivity.this, "Coupon Invalid", Toast.LENGTH_SHORT).show();
                    }
                }
                cpns(couponArraylist);
            }
        });
        ApiGetProfileDetail();
        getCoupons(uniqueId);
    }

    boolean showPaytmWallet = false;
    boolean showPayU = false;
    boolean showForInternationalPayment = false;

    private void placeOrder(int accountId) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(CheckoutItemsActivity.this, CheckoutItemsActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        JSONObject inputObj = new JSONObject();
        JSONObject catalog = new JSONObject();
        JSONObject orderFor = new JSONObject();
        JSONObject timeSlot = new JSONObject();
        JSONArray itemsArray = new JSONArray();

        ArrayList<OrderItem> itemsList = new ArrayList<>();
        itemsList = db.getOrderItems();


        try {
            if (isVirtualItemsOnly) {
                if (phoneNumber != null && !phoneNumber.trim().equalsIgnoreCase("")) {
                    inputObj.put("phoneNumber", phoneNumber);
                } else {
                    showAlert("Please enter valid mobile number");
                    mDialog.dismiss();
                    return;
                }
                if (email != null && !email.trim().equalsIgnoreCase("")) { // to check email address
                    inputObj.put("email", email);
                } else {
                    showAlert("Please enter valid Email address");
                    mDialog.dismiss();
                    return;
                }
            } else {
                if (isStore) {
                    inputObj.put("storePickup", true);

                    if (phoneNumber != null && !phoneNumber.trim().equalsIgnoreCase("")) {
                        inputObj.put("phoneNumber", phoneNumber);
                    } else {
                        showAlert("Please enter valid mobile number");
                        mDialog.dismiss();
                        return;
                    }

                    if (email != null && !email.trim().equalsIgnoreCase("")) { // to check email address
                        inputObj.put("email", email);
                    } else {
                        showAlert("Please enter valid Email address");
                        mDialog.dismiss();
                        return;
                    }

                } else {
                    if (!tvDeliveryAddress.getText().toString().trim().equalsIgnoreCase("")) {  // to check delivery address
                        inputObj.put("homeDelivery", true);
                        JSONObject address = new JSONObject();
                        address.put("phoneNumber", selectedAddress.getPhoneNumber());
                        address.put("firstName", selectedAddress.getFirstName());
                        address.put("lastName", selectedAddress.getLastName());
                        address.put("email", selectedAddress.getEmail());
                        address.put("address", selectedAddress.getAddress());
                        address.put("city", selectedAddress.getCity());
                        address.put("postalCode", selectedAddress.getPostalCode());
                        address.put("landMark", selectedAddress.getLandMark());
                        address.put("countryCode", selectedAddress.getCountryCode());

                        inputObj.put("homeDeliveryAddress", address);

                        if (homeDeliveryNumber != null && !homeDeliveryNumber.trim().equalsIgnoreCase("")) {
                            inputObj.put("phoneNumber", homeDeliveryNumber);
                        } else {
                            showAlert("Please enter valid mobile number");
                            mDialog.dismiss();
                            return;
                        }

                        if (homeDeliveryEmail != null && !homeDeliveryEmail.trim().equalsIgnoreCase("")) {
                            inputObj.put("email", homeDeliveryEmail);
                        } else {
                            showAlert("Please enter valid Email address");
                            mDialog.dismiss();
                            return;
                        }
                    } else {
                        showAlert("Please add the delivery address to deliver");
                        mDialog.dismiss();
                        return;
                    }
                }
                if (selectedTime != null && !selectedTime.trim().equalsIgnoreCase("")) {
                    if (isStore) {
                        if (catalogs != null && catalogs.get(0).getNextAvailablePickUpDetails() != null && catalogs.get(0).getNextAvailablePickUpDetails().getTimeSlots() != null) {
                            timeSlot.put("sTime", catalogs.get(0).getNextAvailablePickUpDetails().getTimeSlots().get(0).getStartTime());
                            timeSlot.put("eTime", catalogs.get(0).getNextAvailablePickUpDetails().getTimeSlots().get(0).getEndTime());
                            inputObj.put("timeSlot", timeSlot);
                        }
                    } else {
                        if (catalogs != null && catalogs.get(0).getNextAvailableDeliveryDetails() != null && catalogs.get(0).getNextAvailableDeliveryDetails().getTimeSlots() != null) {
                            timeSlot.put("sTime", catalogs.get(0).getNextAvailableDeliveryDetails().getTimeSlots().get(0).getStartTime());
                            timeSlot.put("eTime", catalogs.get(0).getNextAvailableDeliveryDetails().getTimeSlots().get(0).getEndTime());
                            inputObj.put("timeSlot", timeSlot);
                        }
                    }
                } else {

                    showAlert("Please select a time slot");
                    mDialog.dismiss();
                    return;
                }
                inputObj.put("orderDate", selectedDate);
            }
            inputObj.put("countryCode", countryCode);
            inputObj.put("orderNote", etSpecialNotes.getText().toString());

            JSONArray couponList = new JSONArray();

            for (int i = 0; i < couponArraylist.size(); i++) {
                couponList.put(couponArraylist.get(i));
            }
            Log.i("kooooooo", couponList.toString());
            inputObj.put("coupons", couponList);
            Log.i("couponList", couponList.toString());
            if (itemsList != null && itemsList.size() > 0) {

                for (int i = 0; i < itemsList.size(); i++) {

                    JSONObject obj = new JSONObject();
                    obj.put("id", itemsList.get(i).getId());
                    obj.put("quantity", itemsList.get(i).getQuantity());
                    if (itemsList.get(i).getConsumerNote() != null) {
                        obj.put("consumerNote", itemsList.get(i).getConsumerNote());
                    } else {
                        obj.put("consumerNote", "");
                    }
                    obj.put("itemType", itemsList.get(i).getItemType());
                    itemsArray.put(obj);
                }
                inputObj.put("orderItem", itemsArray);
            }

            catalog.put("id", catalogId);
            inputObj.put("catalog", catalog);
            orderFor.put("id", 0);
            inputObj.put("orderFor", orderFor);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), inputObj.toString());
        mBuilder.addFormDataPart("order", "blob", body);
//        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), inputObj.toString());
        RequestBody requestBody = mBuilder.build();

        Call<ResponseBody> call = apiService.order(accountId, requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse
                    (Call<ResponseBody> call, Response<ResponseBody> response) {
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
                try {
                    Config.logV("URL--7777-------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------Setting-------------------" + response.code());
                    if (response.code() == 200) {

                        if (response.body() != null) {

                            JSONObject reader = new JSONObject(response.body().string());
                            Iterator iteratorObj = reader.keys();

                            while (iteratorObj.hasNext()) {
                                String getJsonObj = (String) iteratorObj.next();
                                System.out.println("KEY: " + "------>" + getJsonObj);
                                if (reader.getString(getJsonObj).trim().length() > 7) {
                                    ynwUUID = reader.getString(getJsonObj);
                                }
                                if (!catalogs.get(0).getPaymentType().equalsIgnoreCase(Constants.FULLAMOUNT)) {
                                    if (catalogs.get(0).getAdvanceAmount() != null && !catalogs.get(0).getAdvanceAmount().equalsIgnoreCase("0.0")) {
                                        if (reader.has("_prepaymentAmount")) {
                                            prepayAmount = reader.getString("_prepaymentAmount");
                                        } else {
                                            prepayAmount = String.valueOf(0);
                                        }
                                    }
                                } else {
                                    if (reader.has("_prepaymentAmount")) {
                                        prepayAmount = reader.getString("_prepaymentAmount");
                                    } else {
                                        prepayAmount = String.valueOf(0);
                                    }
                                }

                            }
                            if (cbJCash.isChecked()) {
                                getPrePayRemainingAmntNeeded(prepayAmount);
                            } else {
                                getOrderConfirmationId(ynwUUID, accountId);
                            }

                        }

                    } else {

                        if (response.code() == 422) {

                            String errorString = response.errorBody().string();

                            Toast.makeText(CheckoutItemsActivity.this, errorString, Toast.LENGTH_LONG).show();
                        } else {
                            String responseerror = response.errorBody().string();
                            Config.logV("Response--error-------------------------" + responseerror);
                            if (response.code() != 419)
                                Toast.makeText(CheckoutItemsActivity.this, responseerror, Toast.LENGTH_LONG).show();
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

    private void getPrePayRemainingAmntNeeded(String prepayAmount) {
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<String> call = apiService.getPrePayRemainingAmnt(cbJCash.isChecked(), false, prepayAmount);
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                Config.logV("URL------GET Prepay remaining amount---------" + response.raw().request().url().toString().trim());
                Config.logV("Response--code-------------------------" + response.code());
                if (response.code() == 200) {
                    prePayRemainingAmount = response.body();
                    getOrderConfirmationId(ynwUUID, providerId);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i("PrePayRemainingAmntNeed", t.toString());
                t.printStackTrace();
            }
        });
    }

    private void showAlert(String message) {
        //DialogUtilities c = new DialogUtilities();
        //c.displayAlert(mContext, "", message);
        DialogUtilsKt.showUIDialog(mContext, "", message, () -> {
            return Unit.INSTANCE;
        });
    }

    private void getOrderConfirmationId(String value, int acctId) {

        final ApiInterface apiService =
                ApiClient.getClient(CheckoutItemsActivity.this).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(CheckoutItemsActivity.this, CheckoutItemsActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ActiveOrders> call = apiService.getOrderDetails(value, acctId);
        call.enqueue(new Callback<ActiveOrders>() {
            @Override
            public void onResponse(Call<ActiveOrders> call, Response<ActiveOrders> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        activeOrders = response.body();
                        if (activeOrders != null) {
                            orderId = activeOrders.getOrderNumber();
                            if (catalogs.get(0).getPaymentType() != null && !catalogs.get(0).getPaymentType().equalsIgnoreCase(Constants.NONE)) {
                                if (cbJCash.isChecked() && Double.parseDouble(prePayRemainingAmount) <= 0) {
                                    isGateWayPaymentNeeded(value, prepayAmount, acctId, Constants.PURPOSE_PREPAYMENT, true, false, false, false, "JCASH");

                                    //Toast.makeText(mContext,"Pay amount by Cash",Toast.LENGTH_LONG).show();
                                } else if (prepayAmount != null && Float.parseFloat(prepayAmount) > 0) {
                                    try {
                                        ApiGetPaymentModes();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    String inputString = SharedPreference.getInstance(mContext).getStringValue(Constants.QUESTIONNAIRE, "");

                                    if (inputString != null && !inputString.trim().equalsIgnoreCase("")) {

                                        QuestionnaireInput input = new QuestionnaireInput();
                                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                        input = gson.fromJson(inputString, QuestionnaireInput.class);
                                        ApiSubmitQuestionnnaire(input, activeOrders.getUid());
                                    } else {
                                        getOrderConfirmationDetails(acctId);
                                    }
                                }
                            } else {
                                String inputString = SharedPreference.getInstance(mContext).getStringValue(Constants.QUESTIONNAIRE, "");

                                if (inputString != null && !inputString.trim().equalsIgnoreCase("")) {

                                    QuestionnaireInput input = new QuestionnaireInput();
                                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                    input = gson.fromJson(inputString, QuestionnaireInput.class);
                                    ApiSubmitQuestionnnaire(input, activeOrders.getUid());
                                } else {
                                    getOrderConfirmationDetails(acctId);
                                }

                            }
                        }

                    }

                } catch (
                        Exception e) {
                    Log.i("mnbbnmmnbbnm", e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ActiveOrders> call, Throwable t) {
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });

    }

    public void ApiGetPaymentModes() {

        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        Call<ArrayList<PaymentModel>> call = apiService.getPaymentModes(String.valueOf(providerId), 0, Constants.PURPOSE_BILLPAYMENT);

        call.enqueue(new Callback<ArrayList<PaymentModel>>() {
            @Override
            public void onResponse(Call<ArrayList<PaymentModel>> call, Response<ArrayList<PaymentModel>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {


                        mPaymentData = response.body();

                        dialog = new BottomSheetDialog(CheckoutItemsActivity.this);
                        dialog.setContentView(R.layout.prepayment);
                        dialog.setCancelable(false);
                        dialog.show();
                        LinearLayout ll_cancellation_policy = (LinearLayout) dialog.findViewById(R.id.ll_cancellation_policy);
                        ll_cancellation_policy.setVisibility(View.VISIBLE);
                        CardView cv_cancellation_policy = (CardView) dialog.findViewById(R.id.cv_cancellation_policy);
                        Button btn_pay = (Button) dialog.findViewById(R.id.btn_pay);
                        CustomTextViewBold tv_payment_link = (CustomTextViewBold) dialog.findViewById(R.id.tv_international_payment_link);
                        ImageView ivClose = dialog.findViewById(R.id.iv_close);
                        ivClose.setVisibility(View.VISIBLE);
                        GridView gv_payment_modes = (GridView) dialog.findViewById(R.id.gv_payment_modes);
                        TextView txtamt = (TextView) dialog.findViewById(R.id.txtamount);

                        TextView txtprepayment = (TextView) dialog.findViewById(R.id.txtprepayment);

                        txtprepayment.setText(R.string.ordr_prepay);
                        if (cbJCash.isChecked()) {
                            txtamt.setText("Rs." + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(prePayRemainingAmount)));
                        } else {
                            txtamt.setText("Rs." + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(prepayAmount)));
                        }
                        Typeface tyface1 = Typeface.createFromAsset(CheckoutItemsActivity.this.getAssets(),
                                "fonts/JosefinSans-SemiBold.ttf");
                        txtamt.setTypeface(tyface1);

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
                                cancelation_policy_2 = cancelation_policy_2.replace("VARIABLE", "order");
                                cancelation_policy_3 = cancelation_policy_3.replace("VARIABLE", "order");
                                CustomTextViewLight setup_intro_bullet_first_text = cancellationPolicyDialog.findViewById(R.id.setup_intro_bullet_first_text);
                                CustomTextViewLight setup_intro_bullet_second_text = cancellationPolicyDialog.findViewById(R.id.setup_intro_bullet_second_text);
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
                        ivClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                finish();
                            }
                        });
                        btn_pay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (selectedpaymentMode != null) {

                                    if (cbJCash.isChecked()) {
                                        new PaymentGateway(mContext, (Activity) mContext, iPaymentResponse).ApiGenerateHashWallet(prepayAmount, selectedpaymentMode, ynwUUID, String.valueOf(providerId), Constants.PURPOSE_PREPAYMENT, 0, isInternational, orderId, true);

                                    } else {
                                        new PaymentGateway(mContext, (Activity) mContext, iPaymentResponse).ApiGenerateHash(prepayAmount, selectedpaymentMode, ynwUUID, String.valueOf(providerId), Constants.PURPOSE_PREPAYMENT, 0, isInternational, orderId, 0);

                                    }
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(mContext, "Please select mode of payment", Toast.LENGTH_LONG).show();
                                }
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
                        indiaPay = mPaymentData.get(0).isIndiaPay();
                        internationalPay = mPaymentData.get(0).isInternationalPay();
                        if (indiaPay && mPaymentData.get(0).getIndiaBankInfo().size() > 0) {
                            selectedpaymentMode = null;
                            PAYMENT_LINK_FLAG = "INDIA";            /******** for set PAYMENT_LINK_FLAG **/
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
                            PAYMENT_LINK_FLAG = "INTERNATIONAL";     /******** for set PAYMENT_LINK_FLAG **/
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


                    } else {
                        if (response.code() != 419)
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

    public void isGateWayPaymentNeeded(String ynwUUID, final String amount, int accountID, String purpose, boolean isJcashUsed, boolean isreditUsed, boolean isRazorPayPayment, boolean isPayTmPayment, String paymentMode) {

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
                        Config.closeDialog(CheckoutItemsActivity.this, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());


                    if (response.code() == 200) {

                        WalletCheckSumModel respnseWCSumModel = response.body();

                        if (!respnseWCSumModel.isGateWayPaymentNeeded()) {

                            getOrderConfirmationDetails(accountID);

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

    private void getOrderConfirmationDetails(int acctId) {

        final ApiInterface apiService =
                ApiClient.getClient(CheckoutItemsActivity.this).create(ApiInterface.class);
        final Dialog dialog = Config.getProgressDialog(CheckoutItemsActivity.this, CheckoutItemsActivity.this.getResources().getString(R.string.dialog_log_in));
        dialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Call<ActiveOrders> call = apiService.getOrderDetails(ynwUUID, acctId);
                call.enqueue(new Callback<ActiveOrders>() {
                    @Override
                    public void onResponse(Call<ActiveOrders> call, Response<ActiveOrders> response) {
                        try {
                            if (dialog.isShowing())
                                Config.closeDialog(getParent(), dialog);
                            Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                            Config.logV("Response--code-------------------------" + response.code());
                            if (response.code() == 200) {
                                activeOrders = response.body();
                                imagePathList.clear();
                                SharedPreference.getInstance(mContext).setValue(Constants.QUESTIONNAIRE, "");
                                SharedPreference.getInstance(mContext).setValue(Constants.QIMAGES, "");
                                if (activeOrders != null) {

                                    successDialog = new SuccessDialog(mContext, activeOrders.getOrderNumber());
                                    successDialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
                                    successDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    successDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    successDialog.setCancelable(false);
                                    successDialog.show();
                                    DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                                    int width = (int) (metrics.widthPixels * 1);
                                    successDialog.getWindow().setGravity(Gravity.BOTTOM);
                                    successDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Gson gson = new Gson();
                                            String myJson = gson.toJson(activeOrders);
                                            successDialog.dismiss();
                                            db.DeleteCart();
                                            Intent checkIn = new Intent(CheckoutItemsActivity.this, OrderConfirmation.class);
                                            checkIn.putExtra("orderInfo", myJson);
                                            checkIn.putExtra("isVirtualItemsOnly", isVirtualItemsOnly);
                                            startActivity(checkIn);
                                        }
                                    }, 8000);


                                }

                            }
                        } catch (Exception e) {
                            Log.i("mnbbnmmnbbnm", e.toString());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ActiveOrders> call, Throwable t) {
                        if (dialog.isShowing())
                            Config.closeDialog(getParent(), dialog);
                    }
                });
            }
        }, 0);
    }

    private void getProviderDetails(int id) {
        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(CheckoutItemsActivity.this, CheckoutItemsActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<Provider> call = apiService.getProvider(id);
        call.enqueue(new Callback<Provider>() {
            @Override
            public void onResponse(Call<Provider> call, final Response<Provider> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(CheckoutItemsActivity.this, mDialog);

                    if (response.code() == 200) {

                        Provider provider = new Provider();

                        provider = response.body();

                        if (provider != null && provider.getBusinessProfile() != null) {

                            mBusinessDataList = provider.getBusinessProfile();

                            if (mBusinessDataList != null) {

                                tvSpName.setText(mBusinessDataList.getBusinessName());

                                tvLocationName.setText(mBusinessDataList.getBaseLocation().getPlace());

                                if (mBusinessDataList.getLogo() != null) {

                                    shimmer.setVisibility(View.VISIBLE);
                                    PicassoTrustAll.getInstance(CheckoutItemsActivity.this).load(mBusinessDataList.getLogo().getUrl()).into(ivSpImage, new com.squareup.picasso.Callback() {
                                        @Override
                                        public void onSuccess() {

                                            shimmer.setVisibility(View.GONE);
                                            ivSpImage.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onError() {

                                            shimmer.setVisibility(View.GONE);
                                            ivSpImage.setVisibility(View.VISIBLE);
                                            ivSpImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_noimage));
                                        }
                                    });
                                } else {
                                    shimmer.setVisibility(View.GONE);
                                    ivSpImage.setVisibility(View.VISIBLE);
                                    ivSpImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_noimage));
                                }
                            }
                        }

                    } else {

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
                    Config.closeDialog(CheckoutItemsActivity.this, mDialog);
            }
        });
    }


    private void ApiGetProfileDetail() {

        ApiInterface apiService = ApiClient.getClient(CheckoutItemsActivity.this).create(ApiInterface.class);

        final int consumerId = SharedPreference.getInstance(CheckoutItemsActivity.this).getIntValue("consumerId", 0);

        final Dialog mDialog = Config.getProgressDialog(CheckoutItemsActivity.this, CheckoutItemsActivity.this.getResources().getString(R.string.dialog_log_in));
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

                            if (profileDetails.getUserprofile().getEmail() != null) {
                                email = profileDetails.getUserprofile().getEmail();
                                tvContactEmail.setText(email);
                            } else {
                                tvContactEmail.setHint("Enter your Mail Id");
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


    private void getCatalogDetails(int providerId) {
        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(CheckoutItemsActivity.this, CheckoutItemsActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ArrayList<Catalog>> call = apiService.getListOfCatalogs(providerId);
        call.enqueue(new Callback<ArrayList<Catalog>>() {
            @Override
            public void onResponse(Call<ArrayList<Catalog>> call, Response<ArrayList<Catalog>> response) {
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
                try {
                    Config.logV("URL--7777-------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------Setting-------------------" + response.code());
                    if (response.code() == 200) {
                        catalogs.clear();
                        catalogs = response.body();
                        if (catalogs != null && catalogs.size() > 0) {

                            getProviderDetails(providerId, catalogs);
                            // to get payment modes
                            //APIPayment(String.valueOf(accountId));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Catalog>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });
    }


    private void getStorePickupSchedules(int catalogId, int providerId) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(CheckoutItemsActivity.this, CheckoutItemsActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ArrayList<Schedule>> call = apiService.getPickUpSchedule(catalogId, providerId);
        call.enqueue(new Callback<ArrayList<Schedule>>() {
            @Override
            public void onResponse(Call<ArrayList<Schedule>> call, Response<ArrayList<Schedule>> response) {
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
                try {
                    Config.logV("URL--7777-------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------Setting-------------------" + response.code());
                    if (response.code() == 200) {
                        storePickupSchedulesList.clear();
                        storePickupSchedulesList = response.body();

                        // set what is given in the catalog Api
                        if (catalogs != null && catalogs.get(0).getNextAvailablePickUpDetails() != null) {

                            selectedDate = catalogs.get(0).getNextAvailablePickUpDetails().getAvailableDate();
                            String date = getCustomDateString(selectedDate);
                            if (catalogs.get(0).getNextAvailablePickUpDetails().getTimeSlots() != null && catalogs.get(0).getNextAvailablePickUpDetails().getTimeSlots().size() > 0) {

                                String startTime = catalogs.get(0).getNextAvailablePickUpDetails().getTimeSlots().get(0).getStartTime();
                                String endTime = catalogs.get(0).getNextAvailablePickUpDetails().getTimeSlots().get(0).getEndTime();
                                tvTimeSlot.setText(date + ", " + startTime + "-" + endTime);
                                selectedTime = startTime + "-" + endTime;
                            } else {

                                tvTimeSlot.setText("Not available");

                            }

                            llDelivery.setVisibility(View.GONE);
                            rlDeliveryFee.setVisibility(View.GONE);
                            llContactDetails.setVisibility(View.VISIBLE);

                        } else if (storePickupSchedulesList != null && storePickupSchedulesList.size() > 0) { // if failed show what is given in Api

                            selectedDate = storePickupSchedulesList.get(0).getDate();
                            String date = getCustomDateString(storePickupSchedulesList.get(0).getDate());
                            if (storePickupSchedulesList.get(0).getCatalogTimeSlotList() != null) {
                                String startTime = storePickupSchedulesList.get(0).getCatalogTimeSlotList().get(0).getStartTime();
                                String endTime = storePickupSchedulesList.get(0).getCatalogTimeSlotList().get(0).getEndTime();
                                tvTimeSlot.setText(date + ", " + startTime + "-" + endTime);
                                selectedTime = startTime + "-" + endTime;
                            } else {
                                // tvTimeSlot.setText(storePickupSchedulesList.get(0).getReason());
                                tvTimeSlot.setText("No Store pickup");
                            }
                            llDelivery.setVisibility(View.GONE);
                            rlDeliveryFee.setVisibility(View.GONE);
                            llContactDetails.setVisibility(View.VISIBLE);

                        }

                    } else {

                        tvTimeSlot.setText("Not available");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Schedule>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });

    }


    private void getHomeDeliverySchedules(int catalogId, int providerId) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(CheckoutItemsActivity.this, CheckoutItemsActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ArrayList<Schedule>> call = apiService.getHomeDeliverySchedule(catalogId, providerId);
        call.enqueue(new Callback<ArrayList<Schedule>>() {
            @Override
            public void onResponse(Call<ArrayList<Schedule>> call, Response<ArrayList<Schedule>> response) {
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
                try {
                    Config.logV("URL--7777-------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------Setting-------------------" + response.code());
                    if (response.code() == 200) {
                        homeDeliverySchedulesList.clear();
                        homeDeliverySchedulesList = response.body();

                        if (!isStore) {

                            // set what is given in the catalog Api
                            if (catalogs != null && catalogs.get(0).getNextAvailableDeliveryDetails() != null) {
                                selectedDate = catalogs.get(0).getNextAvailableDeliveryDetails().getAvailableDate();
                                String date = getCustomDateString(selectedDate);
                                if (catalogs.get(0).getNextAvailableDeliveryDetails().getTimeSlots() != null && catalogs.get(0).getNextAvailableDeliveryDetails().getTimeSlots().size() > 0) {

                                    String startTime = catalogs.get(0).getNextAvailableDeliveryDetails().getTimeSlots().get(0).getStartTime();
                                    String endTime = catalogs.get(0).getNextAvailableDeliveryDetails().getTimeSlots().get(0).getEndTime();
                                    tvTimeSlot.setText(date + ", " + startTime + "-" + endTime);
                                    selectedTime = startTime + "-" + endTime;
                                } else {

                                    tvTimeSlot.setText("Not available");

                                }

                                llContactDetails.setVisibility(View.GONE);
                                llDelivery.setVisibility(View.VISIBLE);
                                rlDeliveryFee.setVisibility(View.VISIBLE);

                            } else if (homeDeliverySchedulesList != null && homeDeliverySchedulesList.size() > 0) { // if failed show what is given in Api

                                selectedDate = homeDeliverySchedulesList.get(0).getDate();
                                String date = getCustomDateString(homeDeliverySchedulesList.get(0).getDate());
                                if (homeDeliverySchedulesList.get(0).getCatalogTimeSlotList() != null) {
                                    String startTime = homeDeliverySchedulesList.get(0).getCatalogTimeSlotList().get(0).getStartTime();
                                    String endTime = homeDeliverySchedulesList.get(0).getCatalogTimeSlotList().get(0).getEndTime();
                                    tvTimeSlot.setText(date + ", " + startTime + "-" + endTime);
                                    selectedTime = startTime + "-" + endTime;
                                } else {
                                    // tvTimeSlot.setText(homeDeliverySchedulesList.get(0).getReason());
                                    tvTimeSlot.setText("No Home Delivery");
                                }
                                llContactDetails.setVisibility(View.GONE);
                                llDelivery.setVisibility(View.VISIBLE);
                                rlDeliveryFee.setVisibility(View.VISIBLE);
                            }


                        }
                    } else {

                        tvTimeSlot.setText("Not available");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Schedule>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });
    }


    private void getProviderDetails(int providerId, ArrayList<Catalog> catalogs) {
        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(CheckoutItemsActivity.this, CheckoutItemsActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<OrderResponse> call = apiService.getOrderEnabledStatus(providerId);
        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
                try {
                    Config.logV("URL--7777-------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------Setting-------------------" + response.code());
                    if (response.code() == 200) {
                        orderResponse = response.body();
                        if (orderResponse != null) {
                            updateMainUI(orderResponse, catalogs);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });
    }

    private void getStoreDetails(int providerId) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(CheckoutItemsActivity.this, CheckoutItemsActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<StoreDetails> call = apiService.getStoreDetails(providerId);
        call.enqueue(new Callback<StoreDetails>() {
            @Override
            public void onResponse(Call<StoreDetails> call, Response<StoreDetails> response) {
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
                try {
                    if (response.code() == 200) {

                        storeInfo = response.body();
                        if (storeInfo != null) {
                            storeDetailsDialog = new StoreDetailsDialog(mContext, storeInfo);
                            storeDetailsDialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
                            storeDetailsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            storeDetailsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            storeDetailsDialog.show();
                            storeDetailsDialog.setCancelable(true);
                            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                            int width = (int) (metrics.widthPixels * 1);
                            storeDetailsDialog.getWindow().setGravity(Gravity.BOTTOM);
                            storeDetailsDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<StoreDetails> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });
    }

    private void updateMainUI(OrderResponse orderResponse, ArrayList<Catalog> catalogs) {
        if (orderResponse.getStoreInfo() != null) {
            tvMailId.setText(orderResponse.getStoreInfo().getEmail());
            tvPhoneNumber.setText(orderResponse.getStoreInfo().getPrimaryCountryCode() + " " + orderResponse.getStoreInfo().getPhone());
        }
        countryCode = SharedPreference.getInstance(mContext).getStringValue("countryCode", "");
        phoneNumber = SharedPreference.getInstance(mContext).getStringValue("mobile", "");
        tvContactNumber.setText(phoneNumber);
        tvCountryCode.setText(countryCode);
        tvContactEmail.setText(email);

        updateBill();
        // to set total bill value
        // to fetch items in cart
        cartItemsList.clear();
        cartItemsList = db.getCartItems();
        if (cartItemsList != null && cartItemsList.size() > 0) {
            isVirtualItemsOnly = isVirtualItemsOnly(cartItemsList);

            linearLayoutManager = new LinearLayoutManager(mContext);
            rvItems.setLayoutManager(linearLayoutManager);
            checkoutItemsAdapter = new CheckoutItemsAdapter(cartItemsList, mContext, false);
            rvItems.setAdapter(checkoutItemsAdapter);
        }
        if (isVirtualItemsOnly) {
            ll_group.setVisibility(View.GONE);
            llDelivery.setVisibility(View.GONE);
            rlDeliveryFee.setVisibility(View.GONE);
            llContactDetails.setVisibility(View.VISIBLE);
        } else {
            ll_group.setVisibility(View.VISIBLE);
            if (catalogs.get(0).getPickUp() != null && catalogs.get(0).getPickUp().isOrderPickUp() && catalogs.get(0).getHomeDelivery() != null && catalogs.get(0).getHomeDelivery().isHomeDelivery()) {
                rbStore.setVisibility(View.VISIBLE);
                rbStore.setChecked(true);
                rbHome.setChecked(false);
                getStorePickupSchedules(catalogs.get(0).getCatLogId(), providerId);
                getHomeDeliverySchedules(catalogs.get(0).getCatLogId(), providerId);
            } else if (catalogs.get(0).getPickUp() != null && catalogs.get(0).getPickUp().isOrderPickUp() && catalogs.get(0).getHomeDelivery() == null) {
                rbHome.setVisibility(View.GONE);
                rbStore.setVisibility(View.VISIBLE);
                rbStore.setChecked(true);
                rbHome.setChecked(false);
                getStorePickupSchedules(catalogs.get(0).getCatLogId(), providerId);
            } else if (catalogs.get(0).getPickUp() == null && catalogs.get(0).getHomeDelivery() != null && catalogs.get(0).getHomeDelivery().isHomeDelivery()) {
                rbStore.setVisibility(View.GONE);
                rbHome.setVisibility(View.VISIBLE);
                rbHome.setChecked(true);
                rbStore.setChecked(false);
                getHomeDeliverySchedules(catalogs.get(0).getCatLogId(), providerId);
            }
        }
    }

    private boolean isVirtualItemsOnly(ArrayList<CartItemModel> cartItemsList) {
        boolean isContainsPhysicalItems = false;
        for (CartItemModel cartItem : cartItemsList) {
            if (cartItem.getItemType() == null || cartItem.getItemType().isEmpty() || cartItem.getItemType().equalsIgnoreCase("PHYSICAL")) {
                isContainsPhysicalItems = true;
            }
        }
        if (!isContainsPhysicalItems) {
            return true;
        } else {
            return false;
        }
    }

    private void updateBill() {
        try {
            /*double totalBill;
            boolean tax = false;
            if (db.getTaxAmount() >= 0.0) {
                tax = true;
            }
            if (catalogs != null && catalogs.size() > 0) {
                Catalog catalog = new Catalog();
                catalog = catalogs.get(0);
                if (catalog.getHomeDelivery() != null) {
                    if (catalog.getHomeDelivery().isHomeDelivery()) {
                        rlDeliveryFee.setVisibility(View.VISIBLE);
                        String deliveryCharge = Config.getAmountNoOrTwoDecimalPoints(catalog.getHomeDelivery().getDeliveryCharge());
                        tvDeliveryBill.setText("₹ " + deliveryCharge);
                    }
                }
                if (!isStore) {
                    if (catalogs.get(0).getHomeDelivery() != null && catalogs.get(0).getHomeDelivery().isHomeDelivery()) {
                        if (tax) {
                            totalBill = db.getCartDiscountedPrice() + catalogs.get(0).getHomeDelivery().getDeliveryCharge() + db.getTaxAmount();
                            tvBill.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(totalBill));
                        } else {
                            totalBill = db.getCartDiscountedPrice() + catalogs.get(0).getHomeDelivery().getDeliveryCharge();
                            tvBill.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(totalBill));
                        }
                    } else {
                        if (tax) {
                            totalBill = db.getCartDiscountedPrice() + db.getTaxAmount();
                            tvBill.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(totalBill));
                        } else {
                            totalBill = db.getCartDiscountedPrice();
                            tvBill.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(totalBill));
                        }
                    }
                } else {
                    if (tax) {
                        totalBill = db.getCartDiscountedPrice() + db.getTaxAmount();
                        tvBill.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(totalBill));
                    } else {
                        totalBill = db.getCartDiscountedPrice();
                        tvBill.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(totalBill));
                    }
                }
                getAdvancePaymentDetails();
            }*/
            getAdvancePaymentDetails();

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void getAddressList() {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(CheckoutItemsActivity.this, CheckoutItemsActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ArrayList<Address>> call = apiService.getDeliveryAddress();
        call.enqueue(new Callback<ArrayList<Address>>() {
            @Override
            public void onResponse(Call<ArrayList<Address>> call, Response<ArrayList<Address>> response) {
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
                try {
                    if (response.code() == 200) {
                        addressList = response.body();

                        if (addressList != null && addressList.size() > 0) {

                            updateUI(addressList.get(0));

                        } else {

                            tvChangeAddress.setVisibility(View.GONE);
                            llAddress.setVisibility(View.GONE);
                            llAddNew.setVisibility(View.VISIBLE);
                            showAddressDialog();


                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Address>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });
    }

    private void showAddressDialog() {

        addressDialog = new AddressDialog(mContext, addressList, iAddressInterface);
        addressDialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
        addressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addressDialog.show();
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 1);
        addressDialog.getWindow().setGravity(Gravity.BOTTOM);
        addressDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    private void updateUI(Address address) {

        if (address != null) {

            llAddNew.setVisibility(View.GONE);
            tvChangeAddress.setVisibility(View.VISIBLE);
            llAddress.setVisibility(View.VISIBLE);

            selectedAddress = address;
            homeDeliveryEmail = address.getEmail();
            homeDeliveryNumber = address.getPhoneNumber();
            tvName.setText(address.getFirstName() + " " + address.getLastName());
            tvEmailId.setText(address.getEmail());
            tvMobileNumber.setText(address.getPhoneNumber());
            String fullAddress = address.getAddress() + " \n" + address.getCity() + ", \n" + address.getLandMark() + ", " + address.getPostalCode();
            tvDeliveryAddress.setText(fullAddress);

        }
    }


    @Override
    public void onSelectAddress(Address address) {

        // update selected Address
        updateUI(address);
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
            finalDate = "Today, ";
        } else {
            Format f = new SimpleDateFormat("MMM dd");
            finalDate = f.format(selectedDate);
        }

        return finalDate;
    }


    public Schedule getSlotsByDate(ArrayList<Schedule> objList, String date) {
        for (Schedule obj : objList) {
            if (obj.getDate().equalsIgnoreCase(date)) {
                return obj;
            }
        }
        return null;
    }

    @Override
    public void sendTime(String newTime, String date, String displayDate) {

        try {

            selectedDate = date;
            String convertedDate = getCustomDateString(date);
            tvTimeSlot.setText(convertedDate + " " + newTime);
            selectedTime = newTime;

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void changeCheckBoxColor(CheckBox checkBox) {

        ColorStateList darkStateList = ContextCompat.getColorStateList(mContext, R.color.dark_blue);
        CompoundButtonCompat.setButtonTintList(checkBox, darkStateList);
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
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        try {
            RazorpayModel razorpayModel = new RazorpayModel(paymentData);
            //new PaymentGateway(mContext, CheckoutItemsActivity.this).sendPaymentStatus(razorpayModel, "SUCCESS");

            ApiCheckRazorpayPaymentStatus(razorpayModel);

        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentSuccess", e);
        }
    }

    private void ApiCheckRazorpayPaymentStatus(RazorpayModel razorpayModel) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        JSONObject jsonObj = new JSONObject();
        try {

            jsonObj.put("paymentId", razorpayModel.getRazorpay_payment_id());
            jsonObj.put("orderId", razorpayModel.getRazorpay_order_id());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call = apiService.checkRazorpayPaymentStatus(body, providerId);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(CheckoutItemsActivity.this, mDialog);

                    if (response.code() == 200) {

                        Toast.makeText(mContext, "Payment Successful", Toast.LENGTH_LONG).show();
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
        Call<ResponseBody> call = apiService.checkPaytmPaymentStatus(body, providerId);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(CheckoutItemsActivity.this, mDialog);

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
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        paymentError();
    }

    public void paymentFinished() {
        String inputString = SharedPreference.getInstance(mContext).getStringValue(Constants.QUESTIONNAIRE, "");
        if (inputString != null && !inputString.trim().equalsIgnoreCase("")) {
            QuestionnaireInput input = new QuestionnaireInput();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            input = gson.fromJson(inputString, QuestionnaireInput.class);
            ApiSubmitQuestionnnaire(input, ynwUUID);
        } else {
            getOrderConfirmationDetails(providerId);
        }
    }

    private void paymentError() {
        try {
            AlertDialog alertDialog = new AlertDialog.Builder(CheckoutItemsActivity.this).create();
            alertDialog.setTitle("Payment Failed");
            alertDialog.setMessage("Unable to process your request.Please try again after some time");
            alertDialog.setCancelable(false);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            /*Intent homeIntent = new Intent(CheckoutItemsActivity.this, Home.class);
                            startActivity(homeIntent);
                            finish();*/
                        }
                    });
            alertDialog.show();
            alertDialog.getButton(alertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.colorAccent));
        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentError..", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String TAG = "PaytmPayment";
        Log.e(TAG, " result code " + resultCode);
        // -1 means successful  // 0 means failed
        // one error is - nativeSdkForMerchantMessage : networkError
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 01 && data != null) {
            if (data.getStringExtra("response").contains("TXN_SUCCESS")) {
                sendPaymentResponse("TXN_SUCCESS", "");
                Toast.makeText(this, "Payment Successful", Toast.LENGTH_LONG).show();
            } else {
                sendPaymentResponse("TXN_FAILED", "");
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

    private void APIPayment(String accountID) {


        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ArrayList<PaymentModel>> call = apiService.getPaymentMod(accountID, Constants.PURPOSE_PREPAYMENT);

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
                        /*for (int i = 0; i < mPaymentData.size(); i++) {
                            if (mPaymentData.get(i).getDisplayname().equalsIgnoreCase("Wallet")) {
                                showPaytmWallet = true;
                            }

                            if (mPaymentData.get(i).getName().equalsIgnoreCase("CC") || mPaymentData.get(i).getName().equalsIgnoreCase("DC") || mPaymentData.get(i).getName().equalsIgnoreCase("NB")) {
                                showPayU = true;
                            }
                        }*/

                        String countryCode = SharedPreference.getInstance(mContext).getStringValue("countryCode", "").replace("+", "");
                        if (mPaymentData.get(0).isJaldeeBank()) {
                            if (!countryCode.equalsIgnoreCase("91"))  // for international number it shows Razorpay gateway only
                            {
                                showPaytmWallet = false;
                                showPayU = true;
                                showForInternationalPayment = false;
                            } else {
                                showPaytmWallet = true;
                                showPayU = false;
                                showForInternationalPayment = true;
                            }
                        } else {
                            if (mPaymentData.get(0).getPayGateways().contains("PAYTM")) {
                                showPaytmWallet = true;
                            }
                            if (mPaymentData.get(0).getPayGateways().contains("RAZORPAY")) {
                                showPayU = true;
                            }
                        }
                        if ((showPayU) || showPaytmWallet) {
                            Config.logV("URL----%%%%%---@@--");
                            Log.e("XXXXXXXXXXX", "Executed Advance Amount");
                            if (catalogs != null && catalogs.size() > 0) {
                                if (catalogs.get(0).getAdvanceAmount() != null && !catalogs.get(0).getAdvanceAmount().equalsIgnoreCase("0.0")) {
                                    rlPrepayment.setVisibility(View.VISIBLE);
                                    String amount = "₹" + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(catalogs.get(0).getAdvanceAmount()));
                                    tvAdvance.setText(amount);
                                    rlPrepayment.setVisibility(View.GONE);
                                    Log.e("XXXXXXXXXXX", "Executed Advance Amount");
                                } else {
                                    rlPrepayment.setVisibility(View.GONE);
                                    Log.e("HHHHHHHHHHHHH", "Hide Advance Amount");
                                }
                            }
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

    @Override
    public void onEdit(String cCode, String number, String mail) {

        countryCode = cCode;
        phoneNumber = number;
        email = mail;
        tvContactEmail.setText(email);
        tvContactNumber.setText(phoneNumber);
        tvCountryCode.setText(countryCode);
    }

    public static String getCustomDateString(String d) throws ParseException {

        String strCurrentDate = d;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = format.parse(strCurrentDate);
        format = new SimpleDateFormat("MMM dd, yyyy");
        String date = format.format(newDate);

        return date;
    }

    private void getCoupons(int uniqueID) {

        ApiInterface apiService =
                ApiClient.getClient(CheckoutItemsActivity.this).create(ApiInterface.class);

        Call<Provider> call = apiService.getCoupons(uniqueID);
        call.enqueue(new Callback<Provider>() {
            @Override
            public void onResponse(Call<Provider> call, Response<Provider> response) {

                try {

                    if (response.code() == 200) {


                        Provider providerResponse = new Provider();
                        providerResponse = response.body();

                        if (providerResponse != null) {
                            getS3Coupons(providerResponse.getCoupon());
                            getJaldeeProviderCoupons(providerResponse.getProviderCoupon());

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

            }
        });
    }

    private void getS3Coupons(String s3Cupons) {
        try {


            if (s3Cupons != null) {
                s3couponList.clear();
                s3couponList = new Gson().fromJson(s3Cupons, new TypeToken<ArrayList<CoupnResponse>>() {
                }.getType());

                if (s3couponList != null) {

                    if (s3couponList.size() != 0 || providerCouponList.size() != 0) {
                        rlCoupon.setVisibility(View.VISIBLE);
                        cvCoupon.setVisibility(View.VISIBLE);
                    } else {
                        rlCoupon.setVisibility(View.GONE);
                        cvCoupon.setVisibility(View.GONE);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void getJaldeeProviderCoupons(String providerCoupons) {

        try {

            if (providerCoupons != null) {

                providerCouponList.clear();
                providerCouponList = new Gson().fromJson(providerCoupons, new TypeToken<ArrayList<ProviderCouponResponse>>() {
                }.getType());

                if (providerCouponList != null) {

                    if (s3couponList.size() != 0 || providerCouponList.size() != 0) {
                        rlCoupon.setVisibility(View.VISIBLE);
                        cvCoupon.setVisibility(View.VISIBLE);
                    } else {
                        rlCoupon.setVisibility(View.GONE);
                        cvCoupon.setVisibility(View.GONE);
                    }

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
        getAdvancePaymentDetails();
    }

    public AdvancePaymentDetailsOrder getAdvancePaymentDetails() {
        final ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        JSONObject inputObj = new JSONObject();
        JSONObject catalog = new JSONObject();
        JSONArray couponList = new JSONArray();
        JSONArray orderItemsArray = new JSONArray();
        ArrayList<OrderItem> itemsList = new ArrayList<>();
        itemsList = db.getOrderItems();
        try {
            catalog.put("id", catalogId);
            if (itemsList != null && itemsList.size() > 0) {
                for (int i = 0; i < itemsList.size(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("id", itemsList.get(i).getId());
                    obj.put("quantity", itemsList.get(i).getQuantity());
                    if (itemsList.get(i).getConsumerNote() != null) {
                        obj.put("consumerNote", itemsList.get(i).getConsumerNote());
                    } else {
                        obj.put("consumerNote", "");
                    }
                    obj.put("itemType", itemsList.get(i).getItemType());

                    orderItemsArray.put(obj);
                }
            }
            for (int i = 0; i < couponArraylist.size(); i++) {
                couponList.put(couponArraylist.get(i));
            }
            if (isVirtualItemsOnly) {
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                inputObj.put("homeDelivery", false);
                inputObj.put("orderDate", date);
            } else {
                if (isStore) {
                    if (catalogs != null && catalogs.get(0).getNextAvailablePickUpDetails() != null) {
                        selectedDate = catalogs.get(0).getNextAvailablePickUpDetails().getAvailableDate();
                    } else if (storePickupSchedulesList != null && storePickupSchedulesList.size() > 0) {
                        selectedDate = storePickupSchedulesList.get(0).getDate();
                    }
                } else {
                    if (catalogs != null && catalogs.get(0).getNextAvailableDeliveryDetails() != null) {
                        selectedDate = catalogs.get(0).getNextAvailableDeliveryDetails().getAvailableDate();

                    } else if (homeDeliverySchedulesList != null && homeDeliverySchedulesList.size() > 0) {
                        selectedDate = homeDeliverySchedulesList.get(0).getDate();
                    }
                }
                inputObj.put("homeDelivery", rbHome.isChecked());
                inputObj.put("orderDate", selectedDate);
            }
            inputObj.put("coupons", couponList);
            inputObj.put("orderItem", orderItemsArray);
            inputObj.put("catalog", catalog);

        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), inputObj.toString());
        Call<AdvancePaymentDetailsOrder> call = apiService.getOrderAdvancePaymentDetails(String.valueOf(providerId), requestBody);
        call.enqueue(new Callback<AdvancePaymentDetailsOrder>() {
            @Override
            public void onResponse(Call<AdvancePaymentDetailsOrder> call, Response<AdvancePaymentDetailsOrder> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        advancePaymentDetailsOrder = response.body();
                        if (couponEntered != null) {
                            Config.logV("couponArraylist--code-------------------------" + couponArraylist);
                            list.setVisibility(View.VISIBLE);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CheckoutItemsActivity.this);
                            list.setLayoutManager(mLayoutManager);
                            mAdapter = new CouponlistAdapter(CheckoutItemsActivity.this, s3couponList, couponEntered, couponArraylist, advancePaymentDetailsOrder, iCpn);
                            list.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        }
                        if ((catalogs.get(0).getAdvanceAmount() != null && Double.parseDouble(catalogs.get(0).getAdvanceAmount()) > 0) || catalogs.get(0).getPaymentType().equalsIgnoreCase(Constants.FULLAMOUNT)) {
                            if (advancePaymentDetailsOrder.getEligibleJcashAmt().get("jCashAmt") != null && advancePaymentDetailsOrder.getEligibleJcashAmt().get("jCashAmt").getAsDouble() > 0) {
                                cbJCash.setChecked(true);
                                llJCash.setVisibility(View.VISIBLE);
                                cbJCash.setText("Use Jaldee cash balance : Rs " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(advancePaymentDetailsOrder.getEligibleJcashAmt().get("jCashAmt").getAsString())));
                                if (advancePaymentDetailsOrder.getEligibleJcashAmt().get("jCashAmt").getAsDouble() >= advancePaymentDetailsOrder.getAdvanceAmount()) {
                                    tvJCashHint.setVisibility(View.GONE);
                                    llAdvanceAmount.setVisibility(View.GONE);
                                } else {
                                    tvJCashHint.setVisibility(View.VISIBLE);
                                    double amnt = advancePaymentDetailsOrder.getEligibleJcashAmt().get("jCashAmt").getAsDouble() - advancePaymentDetailsOrder.getAdvanceAmount();
                                    tvAdvanceAmount.setText("An advance of ₹\u00a0" + Config.getAmountNoOrTwoDecimalPoints(Math.abs(amnt)) + " required");
                                    llAdvanceAmount.setVisibility(View.VISIBLE);
                                }
                            } else if (advancePaymentDetailsOrder.getEligibleJcashAmt().get("jCashAmt").getAsDouble() == 0) {
                                cbJCash.setChecked(false);
                                llJCash.setVisibility(View.GONE);
                                tvAdvanceAmount.setText("An advance of ₹\u00a0" + Config.getAmountNoOrTwoDecimalPoints(advancePaymentDetailsOrder.getAdvanceAmount()) + " required");
                                llAdvanceAmount.setVisibility(View.VISIBLE);
                            } else {
                                cbJCash.setChecked(false);
                                llJCash.setVisibility(View.GONE);
                                llAdvanceAmount.setVisibility(View.GONE);
                            }
                        } else {
                            cbJCash.setChecked(false);
                            llJCash.setVisibility(View.GONE);
                            llAdvanceAmount.setVisibility(View.GONE);
                        }

                        if (catalogs != null && catalogs.size() > 0) {
                            Catalog catalog = new Catalog();
                            catalog = catalogs.get(0);
                            if (catalog.getHomeDelivery() != null) {
                                if (catalog.getHomeDelivery().isHomeDelivery()) {
                                    rlDeliveryFee.setVisibility(View.VISIBLE);
                                    String deliveryCharge = Config.getAmountNoOrTwoDecimalPoints(advancePaymentDetailsOrder.getDeliveryCharge());
                                    tvDeliveryBill.setText("₹ " + deliveryCharge);
                                }
                            }
                            // to set items bill
                            String amount = "";
                            if (advancePaymentDetailsOrder.getItemTotal() >= 0) {
                                rlItemTotal.setVisibility(View.VISIBLE);
                                amount = Config.getAmountNoOrTwoDecimalPoints(advancePaymentDetailsOrder.getItemTotal());
                                tvItemsBill.setText("₹ " + amount);
                            } else {
                                rlItemTotal.setVisibility(View.GONE);
                            }
                            if (advancePaymentDetailsOrder.getTaxAmount() > 0.0) {
                                rlTax.setVisibility(View.VISIBLE);
                                String tax = Config.getAmountNoOrTwoDecimalPoints(advancePaymentDetailsOrder.getTaxAmount());
                                tvTotalTax.setText("₹ " + tax);
                            } else {
                                rlTax.setVisibility(View.GONE);
                            }
                            if (advancePaymentDetailsOrder.getJdnDiscount() > 0) {
                                rlJdnDiscount.setVisibility(View.VISIBLE);
                                String jdnDiscount = Config.getAmountNoOrTwoDecimalPoints(advancePaymentDetailsOrder.getJdnDiscount());
                                tvJdnDiscount.setText("₹ " + jdnDiscount);
                            } else {
                                rlJdnDiscount.setVisibility(View.GONE);
                            }
                            if (advancePaymentDetailsOrder.getTotalDiscount() > 0) {
                                rltotalDiscount.setVisibility(View.VISIBLE);
                                String totalDiscount = Config.getAmountNoOrTwoDecimalPoints(advancePaymentDetailsOrder.getTotalDiscount());
                                tvTotalDiscount.setText("₹ " + totalDiscount);
                            } else {
                                rltotalDiscount.setVisibility(View.GONE);
                            }
                            if (advancePaymentDetailsOrder.getNetTotal() > 0) {
                                tvBill.setVisibility(View.VISIBLE);
                                String totalBill = Config.getAmountNoOrTwoDecimalPoints(advancePaymentDetailsOrder.getNetTotal());
                                tvBill.setText("₹ " + totalBill);
                            } else {
                                tvBill.setVisibility(View.GONE);
                            }
                            if (advancePaymentDetailsOrder.getjCouponList() != null && advancePaymentDetailsOrder.getJaldeeCouponDiscount() > 0 && advancePaymentDetailsOrder.getjCouponList().size() > 0) {
                                rlJCoupon.setVisibility(View.VISIBLE);
                                JsonObject jCoupon = advancePaymentDetailsOrder.getjCouponList();
                                List<String> keyList = new ArrayList<>();
                                for (String name : jCoupon.keySet()) {
                                    keyList.add(name);
                                }
                                String jCouponText = "Coupon Discount (" + Arrays.toString(keyList.toArray()).replace("[", "").replace("]", "") + ")";
                                tvJCouponTxt.setText(jCouponText);
                                String jaldeeCouponDiscount = Config.getAmountNoOrTwoDecimalPoints(advancePaymentDetailsOrder.getJaldeeCouponDiscount());
                                tvJCoupon.setText("₹ " + jaldeeCouponDiscount);
                            } else {
                                rlJCoupon.setVisibility(View.GONE);
                            }
                            if (advancePaymentDetailsOrder.getProCouponList() != null && advancePaymentDetailsOrder.getProviderCouponDiscount() > 0 && advancePaymentDetailsOrder.getProCouponList().size() > 0) {
                                rlPCoupon.setVisibility(View.VISIBLE);
                                JsonObject pCoupon = advancePaymentDetailsOrder.getProCouponList();
                                List<String> keyList = new ArrayList<>();
                                for (String name : pCoupon.keySet()) {
                                    keyList.add(name);
                                }
                                String jCouponText = "Provider Discount (" + Arrays.toString(keyList.toArray()).replace("[", "").replace("]", "") + ")";
                                tvPCouponTxt.setText(jCouponText);
                                String providerCouponDiscount = Config.getAmountNoOrTwoDecimalPoints(advancePaymentDetailsOrder.getProviderCouponDiscount());
                                tvPCoupon.setText("₹ " + providerCouponDiscount);
                            } else {
                                rlPCoupon.setVisibility(View.GONE);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<AdvancePaymentDetailsOrder> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });
        return advancePaymentDetailsOrder;
    }

    @Override
    public void selectedPaymentMode(String mode) {
        selectedpaymentMode = mode;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
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

    private void ApiSubmitQuestionnnaire(QuestionnaireInput input, String uid) {

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
        call = apiService.submitOrderQuestionnaire(uid, requestBody, providerId);

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
                            getOrderConfirmationDetails(providerId);
                        }

                    } else {
                        if (response.code() == 422) {
                            Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                        }
                        getOrderConfirmationDetails(providerId);// make confirm Order if any QNR uploading error
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

                                                ApiCheckStatus(ynwUUID, providerId, result);

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
        call = apiService.checkOrderUploadStatus(uid, accountId, body);


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    if (response.code() == 200) {
                        getOrderConfirmationDetails(accountId);


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
}