package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jaldeeinc.jaldee.Interface.IAddressInterface;
import com.jaldeeinc.jaldee.Interface.ISelectedTime;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.AddressAdapter;
import com.jaldeeinc.jaldee.adapter.CheckoutItemsAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.AddressDialog;
import com.jaldeeinc.jaldee.custom.BorderImageView;
import com.jaldeeinc.jaldee.custom.CustomEditTextRegular;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;
import com.jaldeeinc.jaldee.custom.SlotSelection;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.Address;
import com.jaldeeinc.jaldee.model.CartItemModel;
import com.jaldeeinc.jaldee.model.CatalogBody;
import com.jaldeeinc.jaldee.model.OrderForBody;
import com.jaldeeinc.jaldee.model.OrderItem;
import com.jaldeeinc.jaldee.model.OrderItemBody;
import com.jaldeeinc.jaldee.model.StoreOrderBody;
import com.jaldeeinc.jaldee.response.Catalog;
import com.jaldeeinc.jaldee.response.CatalogTimeSlot;
import com.jaldeeinc.jaldee.response.OrderResponse;
import com.jaldeeinc.jaldee.response.Schedule;
import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.omjoonkim.skeletonloadingview.SkeletonLoadingView;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutItemsActivity extends AppCompatActivity implements IAddressInterface, ISelectedTime {

    private Context mContext;
    private IAddressInterface iAddressInterface;
    ArrayList<Address> addressList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private AddressAdapter addressAdapter;

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

    @BindView(R.id.tv_contactEmail)
    CustomTextViewSemiBold tvContactEmail;

    @BindView(R.id.tv_changeContactInfo)
    CustomTextViewSemiBold tvChangeContactInfo;

    @BindView(R.id.tv_countryCode)
    CustomTextViewSemiBold tvCountryCode;

    @BindView(R.id.cv_placeOrder)
    CardView cvPlaceOrder;

    private boolean isStore = true;
    private DatabaseHandler db;
    private String selectedDate;
    private String selectedTime = "";
    private int accountId, catalogId;
    private AddressDialog addressDialog;
    private CheckoutItemsAdapter checkoutItemsAdapter;
    ArrayList<Catalog> catalogs = new ArrayList<>();
    private ArrayList<CartItemModel> cartItemsList = new ArrayList<>();
    private SearchViewDetail mBusinessDataList = new SearchViewDetail();
    private OrderResponse orderResponse = new OrderResponse();
    private ArrayList<Schedule> storePickupSchedulesList = new ArrayList<>();
    private ArrayList<Schedule> homeDeliverySchedulesList = new ArrayList<>();
    private SlotSelection slotSelection;
    private ISelectedTime iSelectedTime;
    private String phoneNumber = "", countryCode = "", email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        ButterKnife.bind(CheckoutItemsActivity.this);
        mContext = CheckoutItemsActivity.this;
        iAddressInterface = (IAddressInterface) this;
        iSelectedTime = (ISelectedTime) this;
        db = new DatabaseHandler(mContext);

        Intent intent = getIntent();
        accountId = intent.getIntExtra("accountId", 0);
        mBusinessDataList = (SearchViewDetail) intent.getSerializableExtra("providerInfo");

        catalogId = db.getCatalogId();
        getCatalogDetails(accountId);
        // to fetch user addresses list
        getAddressList();

        tvChangeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        });

        tvStoreDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (llStoreDetails.getVisibility() != View.VISIBLE) {
                    llStoreDetails.setVisibility(View.VISIBLE);
                } else {

                    llStoreDetails.setVisibility(View.GONE);
                }
            }
        });

        rbStore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    isStore = true;
                    rbStore.setChecked(true);
                    rbHome.setChecked(false);
                    getStorePickupSchedules(catalogId, accountId);

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
                    getHomeDeliverySchedules(catalogId, accountId);
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
        });

        tvChangeContactInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        cvPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                placeOrder(accountId);
            }
        });


    }

    boolean showPaytmWallet = false;
    boolean showPayU = false;

    private void placeOrder(int accountId) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(CheckoutItemsActivity.this, CheckoutItemsActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        StoreOrderBody storeOrderBody = new StoreOrderBody();
        if (isStore) {
            storeOrderBody.setStorePickup(true);
        } else {
            storeOrderBody.setHomeDelivery(true);
            storeOrderBody.setHomeDeliveryAddress(tvDeliveryAddress.getText().toString());
        }
        // to send catalogId
        CatalogBody catalogBody = new CatalogBody(catalogId);
        storeOrderBody.setCatalogBody(catalogBody);

        // to send orderFor
        OrderForBody orderForBody = new OrderForBody(0);
        storeOrderBody.setOrderForBody(orderForBody);

        // to set timeSlot
        if (selectedTime != null) {
            CatalogTimeSlot catalogTimeSlot = new CatalogTimeSlot(selectedTime.split("-")[0], selectedTime.split("-")[1]);
            storeOrderBody.setCatalogTimeSlot(catalogTimeSlot);
        }

        // to set items
        storeOrderBody.setOrderItemsList(db.getOrderItems());
        storeOrderBody.setOrderDate(selectedDate);
        storeOrderBody.setCountryCode(countryCode);
        storeOrderBody.setPhoneNumber(phoneNumber);
        storeOrderBody.setEmail(email);
        if (!etSpecialNotes.getText().toString().trim().equalsIgnoreCase("")) {
            storeOrderBody.setOrderNote(etSpecialNotes.getText().toString());
        }

        Call<ResponseBody> call = apiService.order(accountId, storeOrderBody);
        call.enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse
                    (Call<okhttp3.ResponseBody> call, Response<ResponseBody> response) {
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
                try {
                    Config.logV("URL--7777-------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------Setting-------------------" + response.code());
                    if (response.code() == 200) {

                        if (response.body() != null) {

                            if (catalogs.get(0).getAdvanceAmount() != null && !catalogs.get(0).getAdvanceAmount().equalsIgnoreCase("0.0")) {


                            } else {

                                onOrderSuccess(response.body());

                            }

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

    private void onOrderSuccess(ResponseBody body) {

    }


    private void getCatalogDetails(int accountId) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(CheckoutItemsActivity.this, CheckoutItemsActivity.this.getResources().getString(R.string.dialog_log_in));
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

                            if (catalogs.get(0).getPickUp() != null && catalogs.get(0).getPickUp().isOrderPickUp() && catalogs.get(0).getHomeDelivery() != null && catalogs.get(0).getHomeDelivery().isHomeDelivery()) {

                                rbStore.setVisibility(View.VISIBLE);
                                rbStore.setChecked(true);
                                rbHome.setChecked(false);
                                getStorePickupSchedules(catalogs.get(0).getCatLogId(), accountId);
                                getHomeDeliverySchedules(catalogs.get(0).getCatLogId(), accountId);

                            } else if (catalogs.get(0).getPickUp() != null && catalogs.get(0).getPickUp().isOrderPickUp() && catalogs.get(0).getHomeDelivery() != null && !catalogs.get(0).getHomeDelivery().isHomeDelivery()) {

                                rbHome.setVisibility(View.GONE);
                                rbStore.setVisibility(View.VISIBLE);
                                rbStore.setChecked(true);
                                rbHome.setChecked(false);
                                getStorePickupSchedules(catalogs.get(0).getCatLogId(), accountId);

                            } else if (catalogs.get(0).getPickUp() != null && !catalogs.get(0).getPickUp().isOrderPickUp() && catalogs.get(0).getHomeDelivery() != null && !catalogs.get(0).getHomeDelivery().isHomeDelivery()) {

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
        final Dialog mDialog = Config.getProgressDialog(CheckoutItemsActivity.this, CheckoutItemsActivity.this.getResources().getString(R.string.dialog_log_in));
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
                        if (storePickupSchedulesList != null && storePickupSchedulesList.size() > 0) {

                            selectedDate = storePickupSchedulesList.get(0).getDate();
                            String date = convertDate(storePickupSchedulesList.get(0).getDate());
                            String startTime = convertTime(storePickupSchedulesList.get(0).getCatalogTimeSlotList().get(0).getStartTime());
                            String endTime = convertTime(storePickupSchedulesList.get(0).getCatalogTimeSlotList().get(0).getEndTime());
                            tvTimeSlot.setText(date + " " + startTime + "-" + endTime);
                            selectedTime = startTime + "-" + endTime;
                            llDelivery.setVisibility(View.GONE);
                            llContactDetails.setVisibility(View.VISIBLE);

                        }

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
        final Dialog mDialog = Config.getProgressDialog(CheckoutItemsActivity.this, CheckoutItemsActivity.this.getResources().getString(R.string.dialog_log_in));
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
                        if (homeDeliverySchedulesList != null && homeDeliverySchedulesList.size() > 0) {

                            if (!isStore) {
                                selectedDate = homeDeliverySchedulesList.get(0).getDate();
                                String date = convertDate(homeDeliverySchedulesList.get(0).getDate());
                                String startTime = convertTime(homeDeliverySchedulesList.get(0).getCatalogTimeSlotList().get(0).getStartTime());
                                String endTime = convertTime(homeDeliverySchedulesList.get(0).getCatalogTimeSlotList().get(0).getEndTime());
                                tvTimeSlot.setText(date + " " + startTime + "-" + endTime);
                                selectedTime = startTime + "-" + endTime;
                                llContactDetails.setVisibility(View.GONE);
                                llDelivery.setVisibility(View.VISIBLE);
                            }

                        }

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


    private void getProviderDetails(int accountId, ArrayList<Catalog> catalogs) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(CheckoutItemsActivity.this, CheckoutItemsActivity.this.getResources().getString(R.string.dialog_log_in));
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
            }
        }

        email = SharedPreference.getInstance(mContext).getStringValue("email", "");
        countryCode = SharedPreference.getInstance(mContext).getStringValue("countryCode", "");
        phoneNumber = SharedPreference.getInstance(mContext).getStringValue("mobile", "");
        tvContactNumber.setText(phoneNumber);
        tvCountryCode.setText(countryCode);
        tvContactEmail.setText(email);

        // to set items bill
        String amount = "";
        if (db.getCartPrice() == db.getCartDiscountedPrice()) {

            amount = convertAmountToDecimals(String.valueOf(db.getCartPrice()));
            tvItemsBill.setText("₹" + amount);

        } else {
            amount = convertAmountToDecimals(String.valueOf(db.getCartDiscountedPrice()));
            tvItemsBill.setText("₹" + amount);

        }

        if (catalogs != null && catalogs.size() > 0) {
            Catalog catalog = new Catalog();
            catalog = catalogs.get(0);
            if (catalog.getHomeDelivery() != null) {

                if (catalog.getHomeDelivery().isHomeDelivery()) {

                    rlDeliveryFee.setVisibility(View.VISIBLE);

                    String deliveryCharge = convertAmountToDecimals(String.valueOf(catalog.getHomeDelivery().getDeliveryCharge()));
                    tvDeliveryBill.setText("₹" + deliveryCharge);


                }
            }

            // to set total bill value
            try {

                if (catalog.getHomeDelivery() != null && catalog.getHomeDelivery().isHomeDelivery()) {

                    double totalBill = db.getCartDiscountedPrice() + catalog.getHomeDelivery().getDeliveryCharge();
                    tvBill.setText("₹" + convertAmountToDecimals(String.valueOf(totalBill)));

                } else {

                    double bill = db.getCartDiscountedPrice();
                    tvBill.setText("₹" + convertAmountToDecimals(String.valueOf(bill)));
                }

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }


        }

        // to fetch items in cart
        cartItemsList.clear();
        cartItemsList = db.getCartItems();
        if (cartItemsList != null && cartItemsList.size() > 0) {

            linearLayoutManager = new LinearLayoutManager(mContext);
            rvItems.setLayoutManager(linearLayoutManager);
            checkoutItemsAdapter = new CheckoutItemsAdapter(cartItemsList, mContext, false);
            rvItems.setAdapter(checkoutItemsAdapter);
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

    private void updateUI(Address address) {

        if (address != null) {

            tvName.setText(address.getFirstName() + " " + address.getLastName());
            tvEmailId.setText(address.getEmail());
            tvMobileNumber.setText(address.getPhoneNumber());
            String fullAddress = address.getLandMark() + "," + address.getAddress() + "," + address.getCity() + "," + address.getPostalCode();
            tvDeliveryAddress.setText(fullAddress);

        }
    }

    @Override
    public void onSelectAddress(Address address) {

        // update selected Address
        updateUI(address);
    }

    public static String convertAmountToDecimals(String price) {

        double a = Double.parseDouble(price);
        DecimalFormat decim = new DecimalFormat("0.00");
        Double price2 = Double.parseDouble(decim.format(a));
        String amount = decim.format(price2);
        return amount;

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

        selectedDate = date;
        tvTimeSlot.setText(displayDate + " " + newTime);
        selectedTime = newTime;

    }

    public void changeCheckBoxColor(CheckBox checkBox) {

        ColorStateList darkStateList = ContextCompat.getColorStateList(mContext, R.color.dark_blue);
        CompoundButtonCompat.setButtonTintList(checkBox, darkStateList);
    }
}