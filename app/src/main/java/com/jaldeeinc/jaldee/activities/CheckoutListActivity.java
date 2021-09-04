package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jaldeeinc.jaldee.Interface.IAddressInterface;
import com.jaldeeinc.jaldee.Interface.ICpn;
import com.jaldeeinc.jaldee.Interface.IDeleteImagesInterface;
import com.jaldeeinc.jaldee.Interface.IEditContact;
import com.jaldeeinc.jaldee.Interface.IPaymentResponse;
import com.jaldeeinc.jaldee.Interface.ISelectedTime;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.AddressAdapter;
import com.jaldeeinc.jaldee.adapter.CouponlistAdapter;
import com.jaldeeinc.jaldee.adapter.ImagePreviewAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.AddressDialog;
import com.jaldeeinc.jaldee.custom.BorderImageView;
import com.jaldeeinc.jaldee.custom.CustomEditTextRegular;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.EditContactDialog;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;
import com.jaldeeinc.jaldee.custom.SlotSelection;
import com.jaldeeinc.jaldee.custom.StoreDetailsDialog;
import com.jaldeeinc.jaldee.custom.SuccessDialog;
import com.jaldeeinc.jaldee.model.Address;
import com.jaldeeinc.jaldee.model.CartItemModel;
import com.jaldeeinc.jaldee.model.RazorpayModel;
import com.jaldeeinc.jaldee.model.ShoppingListModel;
import com.jaldeeinc.jaldee.payment.PaymentGateway;
import com.jaldeeinc.jaldee.payment.PaytmPayment;
import com.jaldeeinc.jaldee.response.ActiveOrders;
import com.jaldeeinc.jaldee.response.Catalog;
import com.jaldeeinc.jaldee.response.CoupnResponse;
import com.jaldeeinc.jaldee.response.OrderResponse;
import com.jaldeeinc.jaldee.response.PaymentModel;
import com.jaldeeinc.jaldee.response.ProfileModel;
import com.jaldeeinc.jaldee.response.Provider;
import com.jaldeeinc.jaldee.response.ProviderCouponResponse;
import com.jaldeeinc.jaldee.response.Schedule;
import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.jaldeeinc.jaldee.response.StoreDetails;
import com.jaldeeinc.jaldee.response.WalletCheckSumModel;
import com.jaldeeinc.jaldee.response.WalletEligibleJCash;
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
import java.text.DecimalFormat;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import kotlin.Unit;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutListActivity extends AppCompatActivity implements IAddressInterface, ISelectedTime, IPaymentResponse, PaymentResultWithDataListener, IEditContact, IDeleteImagesInterface, ICpn {


    private Context mContext;
    private IAddressInterface iAddressInterface;
    ArrayList<Address> addressList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private AddressAdapter addressAdapter;
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

    @BindView(R.id.tv_deliveryBill)
    CustomTextViewMedium tvDeliveryBill;

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

    @BindView(R.id.ll_billDetails)
    LinearLayout llBillDetails;

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

    @BindView(R.id.ll_advanceAmount)
    LinearLayout llAdvanceAmount;

    @BindView(R.id.tv_advanceAmount)
    CustomTextViewSemiBold tvAdvanceAmount;

    @BindView(R.id.tv_jCashHint)
    CustomTextViewMedium tvJCashHint;

    private boolean isStore = true;
    private String selectedDate;
    private String selectedTime = "";
    private String value = null;
    private String prepayAmount = "";
    private int accountId, catalogId, uniqueId;
    private AddressDialog addressDialog;
    private IPaymentResponse paymentResponse;
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
    private SuccessDialog successDialog;
    ArrayList<PaymentModel> mPaymentData = new ArrayList<>();
    private ProfileModel profileDetails = new ProfileModel();
    private EditContactDialog editContactDialog;
    private IEditContact iEditContact;
    private String homeDeliveryEmail, homeDeliveryNumber;
    private StoreDetails storeInfo = new StoreDetails();
    private StoreDetailsDialog storeDetailsDialog;
    ArrayList<ShoppingListModel> imagePathList = new ArrayList<>();
    String path, couponEntered, prePayRemainingAmount = "";
    Bitmap bitmap;
    File f, file;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private IDeleteImagesInterface iDeleteImagesInterface;
    ArrayList<CoupnResponse> s3couponList = new ArrayList<>();
    ArrayList<ProviderCouponResponse> providerCouponList = new ArrayList<>();

    ArrayList<String> couponArraylist = new ArrayList<>();
    private CouponlistAdapter mAdapter;
    private Address selectedAddress = new Address();
    WalletEligibleJCash walletEligibleJCash = new WalletEligibleJCash();

    private ICpn iCpn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_list);
        ButterKnife.bind(CheckoutListActivity.this);
        mContext = CheckoutListActivity.this;
        iAddressInterface = (IAddressInterface) this;
        iSelectedTime = (ISelectedTime) this;
        paymentResponse = (IPaymentResponse) this;
        iEditContact = (IEditContact) this;
        iDeleteImagesInterface = (IDeleteImagesInterface) this;

        Intent intent = getIntent();
        imagePathList = (ArrayList<ShoppingListModel>) intent.getSerializableExtra("IMAGESLIST");
        uniqueId = intent.getIntExtra("uniqueId", 0);
        accountId = intent.getIntExtra("accountId", 0);
        catalogId = intent.getIntExtra("catalogId", 0);

        getCatalogDetails(accountId);
        getProviderDetails(uniqueId);
        // to fetch user addresses list

        Typeface font_style = Typeface.createFromAsset(mContext.getAssets(), "fonts/JosefinSans-SemiBold.ttf");
        rbHome.setTypeface(font_style);
        rbStore.setTypeface(font_style);

        cbJCash.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (cbJCash.isChecked()) {
                    if (walletEligibleJCash.getjCashAmt() >= Float.parseFloat(catalogs.get(0).getAdvanceAmount())) {
                        llAdvanceAmount.setVisibility(View.GONE);
                    } else {
                        double amnt = walletEligibleJCash.getjCashAmt() - Float.parseFloat(catalogs.get(0).getAdvanceAmount());
                        tvAdvanceAmount.setText("An advance of ₹\u00a0" + Config.getAmountinTwoDecimalPoints(Math.abs(amnt)) + " required");
                        llAdvanceAmount.setVisibility(View.VISIBLE);
                        tvJCashHint.setVisibility(View.VISIBLE);
                    }
                } else {
                    if ((catalogs.get(0).getAdvanceAmount() != null && Double.parseDouble(catalogs.get(0).getAdvanceAmount()) > 0) || catalogs.get(0).getPaymentType().equalsIgnoreCase(Constants.FULLAMOUNT)) {
                        tvAdvanceAmount.setText("An advance of ₹\u00a0" + Config.getAmountinTwoDecimalPoints(Double.parseDouble(catalogs.get(0).getAdvanceAmount())) + " required");
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

                getStoreDetails(accountId);

            }
        });

        rbStore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    isStore = true;
                    rbStore.setChecked(true);
                    rbHome.setChecked(false);
                    llBillDetails.setVisibility(View.GONE);
                    getStorePickupSchedules(catalogId, accountId);
                    showDeliveryCharge();

                } else {
                    isStore = false;
                    rbHome.setChecked(true);
                    rbStore.setChecked(false);
                    llBillDetails.setVisibility(View.VISIBLE);

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
                    llBillDetails.setVisibility(View.VISIBLE);
                    getHomeDeliverySchedules(catalogId, accountId);
                    getAddressList();
                    showDeliveryCharge();
                } else {
                    isStore = true;
                    rbHome.setChecked(false);
                    rbStore.setChecked(true);
                    llBillDetails.setVisibility(View.GONE);
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

                placeOrder(accountId);

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

                    Toast.makeText(CheckoutListActivity.this, "Coupon already added", Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(CheckoutListActivity.this, couponEntered + " " + "Added", Toast.LENGTH_SHORT).show();


                } else {
                    if (couponEntered.equals("")) {
                        Toast.makeText(CheckoutListActivity.this, "Enter a coupon", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CheckoutListActivity.this, "Coupon Invalid", Toast.LENGTH_SHORT).show();
                    }


                }
                cpns(couponArraylist);

            }
        });

        ApiGetProfileDetail();
        getCoupons(uniqueId);


    }

    private void placeOrder(int accountId) {

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        MediaType type = MediaType.parse("*/*");
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        for (int i = 0; i < imagePathList.size(); i++) {

            try {
                bitmap = MediaStore.Images.Media.getBitmap(mContext.getApplicationContext().getContentResolver(), Uri.fromFile(new File(imagePathList.get(i).getImagePath())));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap != null) {
                path = saveImage(bitmap);
                file = new File(path);
            } else {
                file = new File(imagePathList.get(i).getImagePath());
            }
            mBuilder.addFormDataPart("attachments", file.getName(), RequestBody.create(type, file));
        }

        JSONObject jsonObj = new JSONObject();
        JSONObject inputBody = new JSONObject();
        Map<String, String> query = new HashMap<>();
        String json = "";
        try {

            for (int i = 0; i < imagePathList.size(); i++) {

                query.put(String.valueOf(i), imagePathList.get(i).getCaption());
//                jsonObj.put("0", "prescription");

            }
            Gson gson = new GsonBuilder().create();
            json = gson.toJson(query);

            JSONObject catalog = new JSONObject();
            catalog.put("id", catalogId);
            inputBody.put("catalog", catalog);

            JSONObject orderFor = new JSONObject();
            orderFor.put("id", 0);
            inputBody.put("orderFor", orderFor);

            inputBody.put("orderDate", selectedDate);
            inputBody.put("countryCode", countryCode);
            inputBody.put("orderNote", etSpecialNotes.getText().toString());

            JSONArray couponList = new JSONArray();

            for (int i = 0; i < couponArraylist.size(); i++) {
                couponList.put(couponArraylist.get(i));
            }
            Log.i("kooooooo", couponList.toString());
            inputBody.put("coupons", couponList);
            Log.i("couponList", couponList.toString());

            if (isStore) {
                inputBody.put("storePickup", true);

                if (phoneNumber != null && !phoneNumber.trim().equalsIgnoreCase("")) {
                    inputBody.put("phoneNumber", phoneNumber);
                } else {
                    showAlert("Please enter valid mobile number");
                    mDialog.dismiss();
                    return;
                }

                if (email != null && !email.trim().equalsIgnoreCase("")) { // to check email address
                    inputBody.put("email", email);
                } else {
                    showAlert("Please enter valid Email address");
                    mDialog.dismiss();
                    return;
                }

            } else {
                if (!tvDeliveryAddress.getText().toString().trim().equalsIgnoreCase("")) {  // to check delivery address
                    inputBody.put("homeDelivery", true);
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

                    inputBody.put("homeDeliveryAddress", address);
                    if (homeDeliveryNumber != null && !homeDeliveryNumber.trim().equalsIgnoreCase("")) {
                        inputBody.put("phoneNumber", homeDeliveryNumber);
                    } else {
                        showAlert("Please enter valid mobile number");
                        mDialog.dismiss();
                        return;
                    }

                    if (homeDeliveryEmail != null && !homeDeliveryEmail.trim().equalsIgnoreCase("")) {
                        inputBody.put("email", homeDeliveryEmail);
                    } else {
                        showAlert("Please enter valid Email address");
                        mDialog.dismiss();
                        return;
                    }

                } else {

                    showAlert("Please select an address to deliver");
                    mDialog.dismiss();
                    return;
                }

            }
            JSONObject timeSlot = new JSONObject();
            if (selectedTime != null && !selectedTime.trim().equalsIgnoreCase("")) {
                if (isStore) {
                    if (catalogs != null && catalogs.get(0).getNextAvailablePickUpDetails() != null && catalogs.get(0).getNextAvailablePickUpDetails().getTimeSlots() != null) {
                        timeSlot.put("sTime", catalogs.get(0).getNextAvailablePickUpDetails().getTimeSlots().get(0).getStartTime());
                        timeSlot.put("eTime", catalogs.get(0).getNextAvailablePickUpDetails().getTimeSlots().get(0).getEndTime());
                        inputBody.put("timeSlot", timeSlot);
                    }
                } else {

                    if (catalogs != null && catalogs.get(0).getNextAvailableDeliveryDetails() != null && catalogs.get(0).getNextAvailableDeliveryDetails().getTimeSlots() != null) {
                        timeSlot.put("sTime", catalogs.get(0).getNextAvailableDeliveryDetails().getTimeSlots().get(0).getStartTime());
                        timeSlot.put("eTime", catalogs.get(0).getNextAvailableDeliveryDetails().getTimeSlots().get(0).getEndTime());
                        inputBody.put("timeSlot", timeSlot);
                    }
                }
            } else {

                showAlert("Please select a time slot");
                mDialog.dismiss();
                return;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);
        mBuilder.addFormDataPart("captions", "blob", body);
        RequestBody body1 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), inputBody.toString());
        mBuilder.addFormDataPart("order", "blob", body1);
        RequestBody requestBody = mBuilder.build();
        Call<ResponseBody> call = apiService.orderList(accountId, requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(CheckoutListActivity.this, mDialog);

                    if (response.code() == 200) {

                        if (response.body() != null) {

                            JSONObject reader = new JSONObject(response.body().string());
                            Iterator iteratorObj = reader.keys();

                            while (iteratorObj.hasNext()) {
                                String getJsonObj = (String) iteratorObj.next();
                                System.out.println("KEY: " + "------>" + getJsonObj);
                                if (reader.getString(getJsonObj).trim().length() > 7) {
                                    value = reader.getString(getJsonObj);
                                }
                                if (!catalogs.get(0).getPaymentType().equalsIgnoreCase(Constants.FULLAMOUNT)) {
                                    if (catalogs.get(0).getAdvanceAmount() != null && !catalogs.get(0).getAdvanceAmount().equalsIgnoreCase("0.0")) {
                                        prepayAmount = reader.getString("_prepaymentAmount");
                                    }
                                } else {
                                    prepayAmount = reader.getString("_prepaymentAmount");
                                }

                            }
                            if (cbJCash.isChecked()) {
                                getPrePayRemainingAmntNeeded(prepayAmount);
                            } else {
                                getConfirmationId(value, accountId);
                            }

                        }

                    } else {

                        if (response.code() == 422) {

                            String errorString = response.errorBody().string();

                            Toast.makeText(CheckoutListActivity.this, errorString, Toast.LENGTH_LONG).show();
                        } else {
                            String responseerror = response.errorBody().string();
                            Config.logV("Response--error-------------------------" + responseerror);
                            if (response.code() != 419)
                                Toast.makeText(CheckoutListActivity.this, responseerror, Toast.LENGTH_LONG).show();
                        }
                    }

                } catch (
                        Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(CheckoutListActivity.this, mDialog);
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
                    getConfirmationId(value, accountId);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i("PrePayRemainingAmntNeed", t.toString());
                t.printStackTrace();
            }
        });
    }

    private void getConfirmationId(String value, int acctId) {

        final ApiInterface apiService =
                ApiClient.getClient(CheckoutListActivity.this).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(CheckoutListActivity.this, CheckoutListActivity.this.getResources().getString(R.string.dialog_log_in));
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
                            String orderId = activeOrders.getOrderNumber();
                            if (catalogs.get(0).getAdvanceAmount() != null && !catalogs.get(0).getAdvanceAmount().equalsIgnoreCase("0.0")) {
                                if (cbJCash.isChecked() && Double.parseDouble(prePayRemainingAmount) <= 0) {
                                    isGateWayPaymentNeeded(value, prepayAmount, acctId, Constants.PURPOSE_PREPAYMENT, true, false, false, false, "JCASH");

                                    //Toast.makeText(mContext,"Pay amount by Cash",Toast.LENGTH_LONG).show();
                                } else {
                                    try {
                                        dialog = new BottomSheetDialog(CheckoutListActivity.this);
                                        dialog.setContentView(R.layout.prepayment);
                                        dialog.setCancelable(false);
                                        dialog.show();

                                        Button btn_paytm = (Button) dialog.findViewById(R.id.btn_paytm);
                                        Button btn_payu = (Button) dialog.findViewById(R.id.btn_payu);
                                        ImageView ivClose = dialog.findViewById(R.id.iv_close);
                                        ivClose.setVisibility(View.VISIBLE);
                                        /*if (showPaytmWallet) {
                                            btn_paytm.setVisibility(View.VISIBLE);
                                        } else {
                                            btn_paytm.setVisibility(View.GONE);
                                        }
                                        if (showPayU) {
                                            btn_payu.setVisibility(View.VISIBLE);
                                        } else {
                                            btn_payu.setVisibility(View.GONE);
                                        }*/
                                        if (showPaytmWallet && showPayU) {
                                            btn_paytm.setVisibility(View.VISIBLE);
                                            btn_payu.setVisibility(View.VISIBLE);
                                            btn_payu.setText("Credit Card/Debit Card/Net Banking");
                                            btn_paytm.setText("Paytm");
                                        } else if (showPayU && !showPaytmWallet) {
                                            btn_payu.setVisibility(View.VISIBLE);
                                            btn_paytm.setVisibility(View.GONE);
                                            btn_payu.setText("CC/DC/UPI");
                                        } else if (!showPayU && showPaytmWallet) {
                                            btn_payu.setVisibility(View.GONE);
                                            btn_paytm.setVisibility(View.VISIBLE);
                                            btn_paytm.setText("CC/DC/UPI");
                                        }
                                        final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
                                        TextView txtamt = (TextView) dialog.findViewById(R.id.txtamount);

                                        TextView txtprepayment = (TextView) dialog.findViewById(R.id.txtprepayment);

                                        txtprepayment.setText(R.string.serve_prepay);
                                        if (cbJCash.isChecked()) {
                                            txtamt.setText("Rs." + Config.getAmountinTwoDecimalPoints(Double.parseDouble(prePayRemainingAmount)));
                                        } else {
                                            txtamt.setText("Rs." + Config.getAmountinTwoDecimalPoints((Double.parseDouble(prepayAmount))));
                                        }
                                        Typeface tyface1 = Typeface.createFromAsset(CheckoutListActivity.this.getAssets(),
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
                                                if (cbJCash.isChecked()) {
                                                    new PaymentGateway(CheckoutListActivity.this, CheckoutListActivity.this).ApiGenerateHash2(value, prepayAmount, String.valueOf(acctId), Constants.PURPOSE_PREPAYMENT, "checkin", true, false, true, false);
                                                } else {
                                                    new PaymentGateway(CheckoutListActivity.this, CheckoutListActivity.this).ApiGenerateHash1(value, prepayAmount, String.valueOf(acctId), Constants.PURPOSE_PREPAYMENT, "checkin", 0, Constants.SOURCE_PAYMENT);
                                                }
                                                dialog.dismiss();

                                            }
                                        });

                                        btn_paytm.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                PaytmPayment payment = new PaytmPayment(CheckoutListActivity.this, paymentResponse);
                                                if (cbJCash.isChecked()) {
                                                    payment.ApiGenerateHashPaytm2(value, prepayAmount, String.valueOf(acctId), Constants.PURPOSE_PREPAYMENT, "checkin", true, false, false, true, orderId, CheckoutListActivity.this, CheckoutListActivity.this);
                                                } else {
                                                    payment.ApiGenerateHashPaytm(value, prepayAmount, String.valueOf(acctId), Constants.PURPOSE_PREPAYMENT, CheckoutListActivity.this, CheckoutListActivity.this, "", 0, orderId);
                                                }
                                                //payment.generateCheckSum(sAmountPay);
                                                dialog.dismiss();

                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }


                            } else {

                                onOrderSuccess(acctId);

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
                        Config.closeDialog(CheckoutListActivity.this, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());


                    if (response.code() == 200) {

                        WalletCheckSumModel respnseWCSumModel = response.body();

                        if (!respnseWCSumModel.isGateWayPaymentNeeded()) {

                            onOrderSuccess(accountID);

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

    private void getCatalogDetails(int accountId) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(CheckoutListActivity.this, CheckoutListActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ArrayList<Catalog>> call = apiService.getListOfCatalogs(accountId);
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
                            getProviderDetails(accountId, catalogs);

                            // to get payment modes
                            APIPayment(String.valueOf(accountId));

                            if (catalogs.get(0).getPickUp() != null && catalogs.get(0).getPickUp().isOrderPickUp() && catalogs.get(0).getHomeDelivery() != null && catalogs.get(0).getHomeDelivery().isHomeDelivery()) {

                                rbStore.setVisibility(View.VISIBLE);
                                rbStore.setChecked(true);
                                rbHome.setChecked(false);
                                getStorePickupSchedules(catalogs.get(0).getCatLogId(), accountId);
                                getHomeDeliverySchedules(catalogs.get(0).getCatLogId(), accountId);

                            } else if (catalogs.get(0).getPickUp() != null && catalogs.get(0).getPickUp().isOrderPickUp() && catalogs.get(0).getHomeDelivery() == null) {

                                rbHome.setVisibility(View.GONE);
                                rbStore.setVisibility(View.VISIBLE);
                                rbStore.setChecked(true);
                                rbHome.setChecked(false);
                                getStorePickupSchedules(catalogs.get(0).getCatLogId(), accountId);

                            } else if (catalogs.get(0).getPickUp() == null && catalogs.get(0).getHomeDelivery() != null && catalogs.get(0).getHomeDelivery().isHomeDelivery()) {

                                rbStore.setVisibility(View.GONE);
                                rbHome.setVisibility(View.VISIBLE);
                                rbHome.setChecked(true);
                                rbStore.setChecked(false);
                                getHomeDeliverySchedules(catalogs.get(0).getCatLogId(), accountId);

                            }
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


    private void getStorePickupSchedules(int catalogId, int accountId) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(CheckoutListActivity.this, CheckoutListActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ArrayList<Schedule>> call = apiService.getPickUpSchedule(catalogId, accountId);
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
                                tvTimeSlot.setText(storePickupSchedulesList.get(0).getReason());
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


    private void getHomeDeliverySchedules(int catalogId, int accountId) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(CheckoutListActivity.this, CheckoutListActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ArrayList<Schedule>> call = apiService.getHomeDeliverySchedule(catalogId, accountId);
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
                                    tvTimeSlot.setText(homeDeliverySchedulesList.get(0).getReason());
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

    private void ApiGetProfileDetail() {

        ApiInterface apiService = ApiClient.getClient(CheckoutListActivity.this).create(ApiInterface.class);

        final int consumerId = SharedPreference.getInstance(CheckoutListActivity.this).getIntValue("consumerId", 0);

        final Dialog mDialog = Config.getProgressDialog(CheckoutListActivity.this, CheckoutListActivity.this.getResources().getString(R.string.dialog_log_in));
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


    private void getProviderDetails(int id) {
        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(CheckoutListActivity.this, CheckoutListActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<Provider> call = apiService.getProvider(id);
        call.enqueue(new Callback<Provider>() {
            @Override
            public void onResponse(Call<Provider> call, final Response<Provider> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(CheckoutListActivity.this, mDialog);
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
                                    PicassoTrustAll.getInstance(CheckoutListActivity.this).load(mBusinessDataList.getLogo().getUrl()).into(ivSpImage, new com.squareup.picasso.Callback() {
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
                    Config.closeDialog(CheckoutListActivity.this, mDialog);
            }
        });
    }


    private void getProviderDetails(int accountId, ArrayList<Catalog> catalogs) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(CheckoutListActivity.this, CheckoutListActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<OrderResponse> call = apiService.getOrderEnabledStatus(accountId);
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
        if (catalogs.get(0).getAdvanceAmount() != null && !catalogs.get(0).getAdvanceAmount().isEmpty() && Float.parseFloat(catalogs.get(0).getAdvanceAmount()) > 0) {
            tvAdvanceAmount.setText("An advance of ₹\u00a0" + Config.getAmountinTwoDecimalPoints(Double.parseDouble(catalogs.get(0).getAdvanceAmount())) + " required");
            llAdvanceAmount.setVisibility(View.VISIBLE);
        }
        updateImages();

        showDeliveryCharge();
        APIGetWalletEligibleJCash(catalogs);
    }

    private void showDeliveryCharge() {

        if (catalogs != null && catalogs.size() > 0) {
            Catalog catalog = new Catalog();
            catalog = catalogs.get(0);
            if (catalog.getHomeDelivery() != null) {

                if (catalog.getHomeDelivery().isHomeDelivery()) {

                    rlDeliveryFee.setVisibility(View.VISIBLE);

                    String deliveryCharge = convertAmountToDecimals(String.valueOf(catalog.getHomeDelivery().getDeliveryCharge()));
                    tvDeliveryBill.setText("₹ " + deliveryCharge);


                } else {
                    rlDeliveryFee.setVisibility(View.GONE);
                }
            } else {
                rlDeliveryFee.setVisibility(View.GONE);
            }
        }

    }

    private void updateImages() {

        if (imagePathList != null && imagePathList.size() > 0) {
            ImagePreviewAdapter mDetailFileAdapter = new ImagePreviewAdapter(imagePathList, mContext, false, iDeleteImagesInterface);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(CheckoutListActivity.this, 2);
            rvItems.setLayoutManager(mLayoutManager);
            rvItems.setAdapter(mDetailFileAdapter);
            mDetailFileAdapter.notifyDataSetChanged();
        }
    }


    private void getStoreDetails(int accountId) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(CheckoutListActivity.this, CheckoutListActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<StoreDetails> call = apiService.getStoreDetails(accountId);
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

    boolean showPaytmWallet = false;
    boolean showPayU = false;

    private void APIPayment(String accountID) {


        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ArrayList<PaymentModel>> call = apiService.getPaymentModes(accountID, Constants.PURPOSE_PREPAYMENT);

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

                        if (mPaymentData.get(0).getPayGateways().contains("PAYTM")) {
                            showPaytmWallet = true;
                        }

                        if (mPaymentData.get(0).getPayGateways().contains("RAZORPAY")) {
                            showPayU = true;
                        }
                        if ((showPayU) || showPaytmWallet) {
                            Config.logV("URL----%%%%%---@@--");
                            Log.e("XXXXXXXXXXX", "Executed Advance Amount");
                            if (catalogs != null && catalogs.size() > 0) {

                                if (catalogs.get(0).getAdvanceAmount() != null && !catalogs.get(0).getAdvanceAmount().equalsIgnoreCase("0.0")) {
                                    rlPrepayment.setVisibility(View.VISIBLE);
                                    String amount = "₹" + Config.getAmountinTwoDecimalPoints(Double.parseDouble(catalogs.get(0).getAdvanceAmount()));
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

    private void getAddressList() {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(CheckoutListActivity.this, CheckoutListActivity.this.getResources().getString(R.string.dialog_log_in));
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


    public static String convertAmountToDecimals(String price) {

        double a = Double.parseDouble(price);
        DecimalFormat decim = new DecimalFormat("0.00");
        Double price2 = Double.parseDouble(decim.format(a));
        String amount = decim.format(price2);
        return amount;

    }

    public static String getCustomDateString(String d) throws ParseException {

        String strCurrentDate = d;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = format.parse(strCurrentDate);
        format = new SimpleDateFormat("MMM dd, yyyy");
        String date = format.format(newDate);

        return date;
    }

    @Override
    public void onSelectAddress(Address address) {
        // update selected Address
        updateUI(address);
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

    @Override
    public void sendPaymentResponse(String paymentStatus) {
        if (paymentStatus.equalsIgnoreCase("TXN_SUCCESS")) {
            paymentFinished();
        } else {
            paymentError();
        }
    }

    private void onOrderSuccess(int acctId) {
        final ApiInterface apiService =
                ApiClient.getClient(CheckoutListActivity.this).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(CheckoutListActivity.this, CheckoutListActivity.this.getResources().getString(R.string.dialog_log_in));
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
                                    successDialog.dismiss();
                                    Intent checkIn = new Intent(CheckoutListActivity.this, OrderConfirmation.class);
                                    checkIn.putExtra("orderInfo", activeOrders);
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
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        try {
            RazorpayModel razorpayModel = new RazorpayModel(paymentData);
            new PaymentGateway(mContext, CheckoutListActivity.this).sendPaymentStatus(razorpayModel, "SUCCESS");
            Toast.makeText(mContext, "Payment Successful", Toast.LENGTH_LONG).show();
            paymentFinished();
        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        paymentError();
    }

    public void paymentFinished() {
        onOrderSuccess(accountId);
    }

    private void paymentError() {
        try {
            AlertDialog alertDialog = new AlertDialog.Builder(CheckoutListActivity.this).create();
            alertDialog.setTitle("Payment Failed");
            alertDialog.setMessage("Unable to process your request.Please try again after some time");
            alertDialog.setCancelable(false);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            Intent homeIntent = new Intent(CheckoutListActivity.this, Home.class);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String TAG = "PaytmPayment";
        Log.e(TAG, " result code " + resultCode);
        // -1 means successful  // 0 means failed
        // one error is - nativeSdkForMerchantMessage : networkError
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 01 && data != null) {
            if (data.getStringExtra("response").contains("TXN_SUCCESS")) {
                sendPaymentResponse("TXN_SUCCESS");
                Toast.makeText(this, "Payment Successful", Toast.LENGTH_LONG).show();
            } else {
                sendPaymentResponse("TXN_FAILED");
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


    private void showAlert(String message) {

        DialogUtilsKt.showUIDialog(mContext, "", message, () -> {
            return Unit.INSTANCE;
        });
    }

    @Override
    public void delete(int position, String imagePath) {

    }

    @Override
    public void addedNotes(int position) {

    }

    private void getCoupons(int uniqueID) {

        ApiInterface apiService =
                ApiClient.getClient(CheckoutListActivity.this).create(ApiInterface.class);

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
                        // cvCoupon.setVisibility(View.VISIBLE);
                    } else {
                        rlCoupon.setVisibility(View.GONE);
                        // cvCoupon.setVisibility(View.GONE);
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
                        // cvCoupon.setVisibility(View.VISIBLE);
                    } else {
                        rlCoupon.setVisibility(View.GONE);
                        //  cvCoupon.setVisibility(View.GONE);
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
        //CouponApliedOrNotDetails c = new CouponApliedOrNotDetails();
        Config.logV("couponArraylist--code-------------------------" + couponArraylist);
        list.setVisibility(View.VISIBLE);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CheckoutListActivity.this);
        list.setLayoutManager(mLayoutManager);
        mAdapter = new CouponlistAdapter(CheckoutListActivity.this, s3couponList, couponEntered, couponArraylist, iCpn);
        list.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        /*if (userMessage != null) {


            if (isUser) {
                getAdvancePaymentDetails(userMessage, userId);
            } else {
                getAdvancePaymentDetails(userMessage, providerId);
            }
        }*/
       /* Config.logV("couponArraylist--code-------------------------" + couponArraylist);
        list.setVisibility(View.VISIBLE);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CheckInActivity.this);
        list.setLayoutManager(mLayoutManager);
        mAdapter = new CouponlistAdapter(CheckInActivity.this, s3couponList, couponEntered, couponArraylist, getCoupnAppliedOrNotDetails(userMessage, providerId), iCpn);
        list.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();*/
    }


    private void APIGetWalletEligibleJCash(ArrayList<Catalog> catalogs) {
        final ApiInterface apiService =
                ApiClient.getClient(this).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(this, this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<WalletEligibleJCash> call = apiService.getWalletEligibleJCash();
        Config.logV("Request--GetWalletEligibleJCash-------------------------");
        call.enqueue(new Callback<WalletEligibleJCash>() {
            @Override
            public void onResponse(Call<WalletEligibleJCash> call, Response<WalletEligibleJCash> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(CheckoutListActivity.this, mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        walletEligibleJCash = response.body();
                        if (walletEligibleJCash != null) {
                            if (catalogs.get(0).getAdvanceAmount() != null && !catalogs.get(0).getAdvanceAmount().isEmpty() && Float.parseFloat(catalogs.get(0).getAdvanceAmount()) > 0) {
                                if (walletEligibleJCash.getjCashAmt() > 0) {
                                    cbJCash.setChecked(true);
                                    llJCash.setVisibility(View.VISIBLE);
                                    cbJCash.setText("Use Jaldee cash balance : Rs " + Config.getAmountNoOrTwoDecimalPoints(walletEligibleJCash.getjCashAmt()));
                                    if (walletEligibleJCash.getjCashAmt() >= Float.parseFloat(catalogs.get(0).getAdvanceAmount())) {
                                        tvJCashHint.setVisibility(View.GONE);
                                        llAdvanceAmount.setVisibility(View.GONE);
                                    } else {
                                        tvJCashHint.setVisibility(View.VISIBLE);
                                        double amnt = walletEligibleJCash.getjCashAmt() - Float.parseFloat(catalogs.get(0).getAdvanceAmount());
                                        tvAdvanceAmount.setText("An advance of ₹\u00a0" + Config.getAmountinTwoDecimalPoints(Math.abs(amnt)) + " required");
                                        llAdvanceAmount.setVisibility(View.VISIBLE);
                                    }
                                } else if (walletEligibleJCash.getjCashAmt() == 0) {
                                    cbJCash.setChecked(false);
                                    llJCash.setVisibility(View.GONE);
                                    tvAdvanceAmount.setText("An advance of ₹\u00a0" + Config.getAmountinTwoDecimalPoints(Double.parseDouble(catalogs.get(0).getAdvanceAmount())) + " required");
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
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<WalletEligibleJCash> call, Throwable t) {

            }
        });
    }
}