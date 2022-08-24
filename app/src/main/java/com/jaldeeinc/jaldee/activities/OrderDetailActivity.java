package com.jaldeeinc.jaldee.activities;

import static com.jaldeeinc.jaldee.connection.ApiClient.context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chinodev.androidneomorphframelayout.NeomorphFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.jaldeeinc.jaldee.CustomSwipe.DiscreteScrollView;
import com.jaldeeinc.jaldee.CustomSwipe.transform.ScaleTransformer;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.OrderListImagesAdapter;
import com.jaldeeinc.jaldee.adapter.OrdersAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.Contents;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewRegularItalic;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.QRCodeEncoder;
import com.jaldeeinc.jaldee.custom.StoreDetailsDialog;
import com.jaldeeinc.jaldee.model.LabelPath;
import com.jaldeeinc.jaldee.model.QuestionnairInpt;
import com.jaldeeinc.jaldee.model.QuestionnaireResponseInput;
import com.jaldeeinc.jaldee.model.RlsdQnr;
import com.jaldeeinc.jaldee.response.ActiveOrders;
import com.jaldeeinc.jaldee.response.AnswerLineResponse;
import com.jaldeeinc.jaldee.response.GetQuestion;
import com.jaldeeinc.jaldee.response.ItemDetails;
import com.jaldeeinc.jaldee.response.QuestionAnswers;
import com.jaldeeinc.jaldee.response.QuestionnaireResponse;
import com.jaldeeinc.jaldee.response.RatingResponse;
import com.jaldeeinc.jaldee.response.StoreDetails;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {

    @BindView(R.id.ll_moreDetails)
    NeomorphFrameLayout llMoreDetails;

    @BindView(R.id.ll_message)
    LinearLayout llMessage;

    @BindView(R.id.ll_rating)
    LinearLayout llRating;

    @BindView(R.id.ll_cancel)
    LinearLayout llCancel;

    @BindView(R.id.cv_bill)
    CardView cvBill;

    @BindView(R.id.tv_amountToPay)
    CustomTextViewRegularItalic tvAmountToPay;

    @BindView(R.id.tv_billText)
    CustomTextViewSemiBold tvBillText;

    @BindView(R.id.tv_bill_receiptText)
    CustomTextViewSemiBold tvBillReceiptText;

    @BindView(R.id.ll_ordered_items)
    LinearLayout ll_ordered_items;

    @BindView(R.id.rv_items1)
    DiscreteScrollView rvItems1;

    @BindView(R.id.rv_items2)
    DiscreteScrollView rvItems2;

    @BindView(R.id.cv_back)
    CardView cvBack;

    @BindView(R.id.ll_confirm)
    LinearLayout llConfirm;

    @BindView(R.id.tv_spName)
    CustomTextViewBold tvSpName;

    @BindView(R.id.ll_storeDetails)
    LinearLayout llStoreDetails;

    @BindView(R.id.tv_locationName)
    CustomTextViewMedium tvLocationName;

    @BindView(R.id.iv_Qr)
    ImageView ivQRCode;

    @BindView(R.id.tv_confirmationNumber)
    CustomTextViewBold tvOrderNO;

    @BindView(R.id.tv_status)
    CustomTextViewBold tvStatus;

    @BindView(R.id.tv_deliveryType)
    CustomTextViewBold tvDeliveryType;

    @BindView(R.id.tv_consumerName)
    CustomTextViewBold tvConsumerName;

    @BindView(R.id.tv_date)
    CustomTextViewBold tvDate;

    @BindView(R.id.tv_time)
    CustomTextViewBold tvTime;

    @BindView(R.id.ll_notes)
    LinearLayout llNotes;

    @BindView(R.id.tv_notes)
    CustomTextViewMedium tvNotes;

    @BindView(R.id.nested)
    ScrollView scrollView;

    @BindView(R.id.ll_delivery_type)
    LinearLayout ll_delivery_type;

    @BindView(R.id.ll_time)
    LinearLayout ll_time;

    @BindView(R.id.ll_questionnaire)
    LinearLayout llQuestionnaire;

    @BindView(R.id.ll_service_option_qnr)
    LinearLayout ll_service_option_qnr;

    private Context mContext;
    private String orderUUid;
    private int accountId;
    private GridLayoutManager gridLayoutManager;
    private OrdersAdapter ordersAdapter;
    private ActiveOrders orderInfo = new ActiveOrders();
    private StoreDetails storeInfo = new StoreDetails();
    private StoreDetailsDialog storeDetailsDialog;
    private boolean fromPushNotification = false;
    private boolean isVirtualItemsOnly = false;
    private boolean isActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(OrderDetailActivity.this);
        mContext = OrderDetailActivity.this;

        Intent intent = getIntent();
        //orderInfo = (ActiveOrders) intent.getSerializableExtra("orderInfo");
        orderUUid = intent.getStringExtra("uuid");
        String aId = intent.getStringExtra("account");
        if (aId != null && !aId.isEmpty() && !aId.equalsIgnoreCase("")) {
            accountId = Integer.parseInt(intent.getStringExtra("account"));
        } else {
            accountId = 0;
        }
        isActive = intent.getBooleanExtra("isActive", false);

        fromPushNotification = intent.getBooleanExtra(Constants.PUSH_NOTIFICATION, false);

        //if (account != null) {
        //accountId = Integer.parseInt(account);
        // }

        llCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
                dialog.setContentView(R.layout.cancelcheckin);
                dialog.show();
                Button btn_send = (Button) dialog.findViewById(R.id.btn_send);
                Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
                final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
                TextView txtsendmsg = (TextView) dialog.findViewById(R.id.txtsendmsg);
                txtsendmsg.setText("Do you want to cancel this Order?");

                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //api call
                        if (orderInfo != null && orderInfo.getUid() != null && orderInfo.getProviderAccount() != null) {
                            cancelOrder(orderInfo, dialog);
                        }
                    }
                });
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
        llMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (orderInfo != null) {
                        Intent intent = new Intent(mContext, ChatActivity.class);

                        if (orderInfo.getUid().contains("h_")) {
                            String uuid = orderInfo.getUid().replace("h_", "");
                            intent.putExtra("uuid", uuid);
                        } else {
                            intent.putExtra("uuid", orderInfo.getUid());
                        }
                        intent.putExtra("accountId", orderInfo.getProviderAccount().getId());
                        intent.putExtra("name", orderInfo.getProviderAccount().getBusinessName());
                        intent.putExtra("from", Constants.ORDERS);
                        mContext.startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        llRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ApiRating(String.valueOf(orderInfo.getProviderAccount().getId()), orderInfo.getUid());
            }
        });
        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        llStoreDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getStoreDetails(accountId);
            }
        });

        scrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                tvNotes.getParent().requestDisallowInterceptTouchEvent(false);

                return false;
            }
        });

        tvNotes.setMovementMethod(new ScrollingMovementMethod());
        tvNotes.setOnTouchListener((v, event) -> {

            tvNotes.getParent().requestDisallowInterceptTouchEvent(true);

            return false;
        });
        ll_service_option_qnr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (orderInfo != null) {
                    if (orderInfo.getItemsList() != null && orderInfo.getItemsList().size() > 0) {

                        Intent intent = new Intent(mContext, ShowCartServiceOption.class);
                        //intent.putExtra("serviceId", orderInfo.getCatalog().getCatLogId());
                        // intent.putExtra("accountId", orderInfo.getProviderAccount().getId());
                        // intent.putExtra("uid", orderInfo.getUid());
                        intent.putExtra("orderInfo", new Gson().toJson(orderInfo));
                        intent.putExtra("from", Constants.ORDERS);

                        mContext.startActivity(intent);

                    }
                }
            }
        });
        llQuestionnaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (orderInfo != null) {
                    if (orderInfo.getReleasedQnr() != null) {
                        if (orderInfo.getReleasedQnr().size() == 1 && orderInfo.getReleasedQnr().get(0).getStatus().equalsIgnoreCase("submitted")) {

                            QuestionnaireResponseInput input = buildQuestionnaireInput(orderInfo.getQuestionnaire());
                            ArrayList<LabelPath> labelPaths = buildQuestionnaireLabelPaths(orderInfo.getQuestionnaire());

                            SharedPreference.getInstance(mContext).setValue(Constants.QUESTIONNAIRE, new Gson().toJson(input));
                            SharedPreference.getInstance(mContext).setValue(Constants.QIMAGES, new Gson().toJson(labelPaths));

                            Intent intent = new Intent(mContext, UpdateQuestionnaire.class);
                            intent.putExtra("serviceId", orderInfo.getCatalog().getCatLogId());
                            intent.putExtra("accountId", orderInfo.getProviderAccount().getId());
                            intent.putExtra("uid", orderInfo.getUid());
                            intent.putExtra("isEdit", true);
                            intent.putExtra("from", Constants.ORDERS);
                            if (orderInfo != null && orderInfo.getOrderStatus() != null) {
                                intent.putExtra("status", orderInfo.getOrderStatus());
                            }
                            mContext.startActivity(intent);
                        } else {
                            Gson gson = new Gson();
                            String myJson = gson.toJson(orderInfo);

                            Intent intent = new Intent(mContext, ReleasedQNRActivity.class);
                            intent.putExtra("bookingInfo", myJson);
                            intent.putExtra("from", Constants.ORDERS);
                            mContext.startActivity(intent);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        getOrderDetails(orderUUid, accountId);

    }

    private void getOrderDetails(String orderUUid, int accountId) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(OrderDetailActivity.this, OrderDetailActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ActiveOrders> call = apiService.getOrderDetails(orderUUid, accountId);
        call.enqueue(new Callback<ActiveOrders>() {
            @Override
            public void onResponse(Call<ActiveOrders> call, Response<ActiveOrders> response) {
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
                try {
                    if (response.code() == 200) {

                        orderInfo = response.body();
                        if (orderInfo != null) {
                            if (orderInfo.getReleasedQnr() != null) {
                                List<RlsdQnr> fReleasedQNR = orderInfo.getReleasedQnr().stream()
                                        .filter(p -> !p.getStatus().equalsIgnoreCase("unReleased")).collect(Collectors.toList());
                                orderInfo.getReleasedQnr().clear();
                                orderInfo.setReleasedQnr((ArrayList<RlsdQnr>) fReleasedQNR); // remove releasedqnr response and add rlsdqnr with remove "unReleased" status
                            }
                            updateUI(orderInfo);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ActiveOrders> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });
    }


    private void getStoreDetails(int accountId) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(OrderDetailActivity.this, OrderDetailActivity.this.getResources().getString(R.string.dialog_log_in));
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

    private void updateUI(ActiveOrders orderInfo) {

        try {

            if (orderInfo != null) {
                if (isActive) {
                    if (orderInfo.getOrderStatus().equalsIgnoreCase("Order Received") || orderInfo.getOrderStatus().equalsIgnoreCase("Order Acknowledged") || orderInfo.getOrderStatus().equalsIgnoreCase("Order Confirmed")) {

                        llCancel.setVisibility(View.VISIBLE);

                    } else {
                        hideView(llCancel);
                        llCancel.setVisibility(View.GONE);
                    }
                    // to show Questionnaire option
                    if (orderInfo.getReleasedQnr() != null && orderInfo.getQuestionnaire() != null && orderInfo.getQuestionnaire().getQuestionAnswers() != null && orderInfo.getQuestionnaire().getQuestionAnswers().size() > 0) {
                        llQuestionnaire.setVisibility(View.VISIBLE);
                    } else if (orderInfo.getReleasedQnr() != null && orderInfo.getQuestionnaire() != null && orderInfo.getReleasedQnr().size() > 0) {
                        llQuestionnaire.setVisibility(View.VISIBLE);
                    } else {
                        hideView(llQuestionnaire);
                    }
                } else {
                    llCancel.setVisibility(View.GONE);
                    hideView(llCancel);
                    llRating.setVisibility(View.VISIBLE);
                }
                // To show ServiceOption details
                if(orderInfo.getItemsList() != null && orderInfo.getItemsList().size() > 0){
                    ArrayList<ItemDetails> itemsDetails = orderInfo.getItemsList();
                    for(ItemDetails i : itemsDetails){
                        if(i.getSrvAnswers() != null ){
                            QuestionnairInpt answerLine;
                            JsonObject jsonObject = i.getSrvAnswers();
                            Gson gson = new Gson();
                            answerLine = gson.fromJson(jsonObject, QuestionnairInpt.class);
                            if(answerLine.getAnswerLines().size() > 0){
                                ll_service_option_qnr.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
                // To show Bill details
                if (orderInfo.getBill() != null) {
                    if (orderInfo.getBill().getBillPaymentStatus().equalsIgnoreCase("FullyPaid") || orderInfo.getBill().getBillPaymentStatus().equalsIgnoreCase("Refund")) {
                        String amount = "₹" + " " + Config.getAmountNoOrTwoDecimalPoints(orderInfo.getAmountDue());
                        tvAmountToPay.setText(amount);
                        tvAmountToPay.setVisibility(View.GONE);
                        cvBill.setVisibility(View.VISIBLE);
                        tvBillText.setVisibility(View.GONE);
                        tvBillReceiptText.setVisibility(View.VISIBLE);
                        tvBillReceiptText.setText("Receipt");
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) cvBill.getLayoutParams();
                        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                        cvBill.setLayoutParams(lp);
                    } else {
                        //tvBill.setText("Pay Bill");

                        String amount = "₹" + " " + Config.getAmountNoOrTwoDecimalPoints(orderInfo.getAmountDue());
                        if (orderInfo.getOrderStatus().equalsIgnoreCase("Cancelled")) {
                            tvAmountToPay.setVisibility(View.GONE);
                        } else {
                            tvAmountToPay.setText(amount);
                            tvAmountToPay.setVisibility(View.VISIBLE);
                        }
                        if (orderInfo.getBill().getBillStatus() != null && orderInfo.getBill().getBillId() != null && orderInfo.getBill().getBillId().equalsIgnoreCase("0")) {
                            cvBill.setVisibility(View.VISIBLE);
                        } else {
                            cvBill.setVisibility(View.GONE);
                        }
                        cvBill.setVisibility(View.VISIBLE);
                        tvBillText.setText("Bill");
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) cvBill.getLayoutParams();
                        lp.addRule(RelativeLayout.ALIGN_PARENT_END);
                        cvBill.setLayoutParams(lp);
                    }
                    if (!orderInfo.getUid().contains("h_")) {
                        if (orderInfo.getBill().getBillViewStatus() != null && !orderInfo.getOrderStatus().equalsIgnoreCase("Cancelled")) {
                            if (orderInfo.getBill().getBillViewStatus().equalsIgnoreCase("Show")) {
                                cvBill.setVisibility(View.VISIBLE);
                            } else {
                                cvBill.setVisibility(View.GONE);
                            }

                        } else {
                            if (!orderInfo.getBill().getBillPaymentStatus().equalsIgnoreCase("NotPaid")) {
                                cvBill.setVisibility(View.VISIBLE);
                            } else {
                                cvBill.setVisibility(View.GONE);
                            }
                        }
                        /**26-3-21*/
                        if (orderInfo.getBill().getBillViewStatus() == null || orderInfo.getBill().getBillViewStatus().equalsIgnoreCase("NotShow") || orderInfo.getBill().getBillStatus().equals("Settled") || orderInfo.getOrderStatus().equals("Rejected") || orderInfo.getOrderStatus().equals("Cancelled")) {
                            cvBill.setVisibility(View.GONE);
                        }
                        /***/
                    }
                    cvBill.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            try {
                                if (orderInfo != null) {
                                    Intent iBill = new Intent(mContext, BillActivity.class);
                                    iBill.putExtra("ynwUUID", orderInfo.getUid());
                                    iBill.putExtra("provider", orderInfo.getProviderAccount().getBusinessName());
                                    iBill.putExtra("accountID", String.valueOf(orderInfo.getProviderAccount().getId()));
                                    if (orderInfo.getBill() != null) {
                                        iBill.putExtra("payStatus", orderInfo.getBill().getBillPaymentStatus());
                                    }
                                    iBill.putExtra("purpose", Constants.PURPOSE_BILLPAYMENT);
                                    iBill.putExtra("consumer", orderInfo.getOrderFor().getFirstName() + " " + orderInfo.getOrderFor().getLastName());
                                    iBill.putExtra("uniqueId", String.valueOf(orderInfo.getProviderAccount().getUniqueId()));
                                    iBill.putExtra("encId", orderInfo.getOrderNumber());
                                    iBill.putExtra("bookingStatus", orderInfo.getOrderStatus());
                                    startActivity(iBill);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {

                    cvBill.setVisibility(View.GONE);
                }
                if (orderInfo.getItemsList() != null && orderInfo.getItemsList().size() > 0) {
                    isVirtualItemsOnly = isVirtualItemsOnly(orderInfo.getItemsList());
                }
                if (orderInfo.getProviderAccount() != null && orderInfo.getProviderAccount().getBusinessName() != null) {
                    tvSpName.setText(orderInfo.getProviderAccount().getBusinessName());
                }

                tvSpName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent orderIntent = new Intent(view.getContext(), ProviderDetailActivity.class);
                        if (orderInfo.getProviderAccount() != null && orderInfo.getProviderAccount().getUniqueId() != 0) {
                            orderIntent.putExtra("uniqueID", orderInfo.getProviderAccount().getUniqueId());
                        }
                        orderIntent.putExtra("from", "order");
                        view.getContext().startActivity(orderIntent);
                    }
                });

                tvOrderNO.setText(orderInfo.getOrderNumber());

                if (orderInfo.getOrderStatus() != null) {

                    tvStatus.setText(convertToTitleForm(orderInfo.getOrderStatus()));

                    if (orderInfo.getOrderStatus().equalsIgnoreCase("Cancelled")) {
                        tvStatus.setTextColor(mContext.getResources().getColor(R.color.red));
                    } else {
                        tvStatus.setTextColor(mContext.getResources().getColor(R.color.location_theme));
                    }
                }
                tvDate.setText(Config.getCustomDateString(orderInfo.getOrderDate()));
                //if(!isVirtualItemsOnly) {
                if (true) {

                    if (orderInfo.getTimeSlot() != null) {
                        String sTime = orderInfo.getTimeSlot().getsTime();
                        String eTime = orderInfo.getTimeSlot().geteTime();
                        sTime = sTime.replaceAll(" ", "\u00A0");
                        eTime = eTime.replaceAll(" ", "\u00A0");
                        tvTime.setText(sTime + " - " + eTime);
                        ll_time.setVisibility(View.VISIBLE);
                    } else {
                        ll_time.setVisibility(View.GONE);
                    }

                    if (orderInfo.isHomeDelivery()) {

                        tvDeliveryType.setText("Home Delivery");
                        ll_delivery_type.setVisibility(View.VISIBLE);

                    } else if (orderInfo.isStorePickup()) {

                        tvDeliveryType.setText("Store pickup");
                        ll_delivery_type.setVisibility(View.VISIBLE);

                    } else {

                        ll_delivery_type.setVisibility(View.GONE);

                    }
                } else {
                    ll_time.setVisibility(View.GONE);
                    ll_delivery_type.setVisibility(View.GONE);
                }

                if (orderInfo.getOrderFor() != null) {

                    tvConsumerName.setText(orderInfo.getOrderFor().getFirstName() + " " + orderInfo.getOrderFor().getLastName());
                }

                // to set items
//                gridLayoutManager = new GridLayoutManager(mContext, 2);
                if (orderInfo.getShoppingList() != null && orderInfo.getShoppingList().size() > 0) {

                    OrderListImagesAdapter imagePreviewAdapter = new OrderListImagesAdapter(orderInfo.getShoppingList(), mContext, false);
                    rvItems1.setAdapter(imagePreviewAdapter);
                    rvItems1.setItemTransformer(new ScaleTransformer.Builder()
                            .setMinScale(0.8f)
                            .build());
                    ll_ordered_items.setVisibility(View.VISIBLE);
                }
                if (orderInfo.getItemsList() != null && orderInfo.getItemsList().size() > 0) {
                    ordersAdapter = new OrdersAdapter(orderInfo.getItemsList(), mContext, false);
//                rvItems.setLayoutManager(gridLayoutManager);
                    rvItems2.setAdapter(ordersAdapter);
                    rvItems2.setItemTransformer(new ScaleTransformer.Builder()
                            .setMinScale(0.8f)
                            .build());
                    ll_ordered_items.setVisibility(View.VISIBLE);
                }

                if (orderInfo.getOrderNumber() != null) {
                    //Encode with a QR Code image
                    String statusUrl = Constants.URL + "status/" + orderInfo.getOrderNumber();
                    QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(statusUrl,
                            null,
                            Contents.Type.TEXT,
                            BarcodeFormat.QR_CODE.toString(), 175);
                    try {
                        Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
                        ivQRCode.setImageBitmap(bitmap);

                        ivQRCode.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Dialog settingsDialog = new Dialog(OrderDetailActivity.this);
                                settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.image_layout, null));
                                LinearLayout ll_download_qr = settingsDialog.findViewById(R.id.ll_download_qr);

                                ImageView imageView = settingsDialog.findViewById(R.id.iv_close);
                                ImageView ivQR = settingsDialog.findViewById(R.id.iv_Qr);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        settingsDialog.dismiss();
                                    }
                                });
                                ll_download_qr.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions((Activity) view.getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                            // this will request for permission when permission is not true
                                        } else {
                                            storeImage(bitmap);
                                        }
                                    }
                                });
                                Glide.with(context).load(bitmap).into(ivQR);
                                settingsDialog.show();
                            }
                        });


                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }

                if (orderInfo.getOrderNote() != null && !orderInfo.getOrderNote().trim().equalsIgnoreCase("")) {
                    llNotes.setVisibility(View.VISIBLE);
                    tvNotes.setText(orderInfo.getOrderNote());
                } else {
                    llNotes.setVisibility(View.GONE);
                }

            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    private boolean isVirtualItemsOnly(ArrayList<ItemDetails> itemsList) {
        boolean isContainsPhysicalItems = false;
        for (ItemDetails item : itemsList) {
            if (item.getItemType() == null || item.getItemType().isEmpty() || item.getItemType().equalsIgnoreCase("PHYSICAL")) {
                isContainsPhysicalItems = true;
            }
        }
        if (!isContainsPhysicalItems) {
            return true;
        } else {
            return false;
        }
    }

    public static String convertToTitleForm(String name) {
        String convertName = name;
        convertName = convertName.substring(0, 1).toUpperCase() + convertName.substring(1).toLowerCase();
        return convertName;
    }


    @Override
    public void onBackPressed() {

        if (fromPushNotification) {
            Intent intent = new Intent(OrderDetailActivity.this, Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            super.onBackPressed();
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void storeImage(Bitmap image) {
        File pictureFile = null;
        try {
            pictureFile = Config.createFile(context, "png", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (pictureFile == null) {
            //"Error creating media file, check storage permissions: "
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
            Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show();

           /* Uri uri = Uri.fromFile(pictureFile);
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String path = uri.getPath();
            String extension = path.substring(path.lastIndexOf("."));;
            String type = mime.getMimeTypeFromExtension(extension);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, type);*/

        } catch (FileNotFoundException e) {
            //Log.d(TAG, "File not found: " + e.getMessage());
            e.printStackTrace();

        } catch (IOException e) {
            //Log.d(TAG, "Error accessing file: " + e.getMessage());
            e.printStackTrace();

        }
    }

    private void hideView(View view) {
        GridLayout gridLayout = (GridLayout) view.getParent();
        if (gridLayout != null) {
            for (int i = 0; i < gridLayout.getChildCount(); i++) {
                if (view == gridLayout.getChildAt(i)) {
                    gridLayout.removeViewAt(i);
                    break;
                }
            }
        }
    }

    private void cancelOrder(ActiveOrders orderInfo, BottomSheetDialog dialog) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ResponseBody> call = apiService.cancelOrder(orderInfo.getUid(), orderInfo.getProviderAccount().getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(OrderDetailActivity.this, mDialog);
                    if (response.code() == 200) {

                        /*DynamicToast.make(mContext, "Order cancelled successfully",
                                ContextCompat.getColor(mContext, R.color.white), ContextCompat.getColor(mContext, R.color.appoint_theme), Toast.LENGTH_SHORT).show();
                        */
                        DynamicToast.make(context, "Order cancelled successfully", AppCompatResources.getDrawable(
                                context, R.drawable.ic_info_black),
                                ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.green), Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                        getOrderDetails(orderUUid, accountId);

                    } else {
                        if (response.code() != 419) {
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
                if (mDialog.isShowing())
                    Config.closeDialog(OrderDetailActivity.this, mDialog);

            }
        });

    }

    BottomSheetDialog dialog;
    float rate = 0;
    String comment = "";
    boolean firstTimeRating = false;

    private void ApiRating(final String accountID, final String UUID) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Map<String, String> query = new HashMap<>();
        query.put("account-eq", accountID);
        query.put("uId-eq", UUID);
        Call<ArrayList<RatingResponse>> call = apiService.getOrderRating(query);
        Config.logV("Location-----###########@@@@@@" + query);
        call.enqueue(new Callback<ArrayList<RatingResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<RatingResponse>> call, final Response<ArrayList<RatingResponse>> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(OrderDetailActivity.this, mDialog);
                    Config.logV("URL----------Location-----###########@@@@@@-----" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code--------Message-----------------" + response.code());
                    if (response.code() == 200) {
                        final ArrayList<RatingResponse> mRatingDATA = response.body();
                        Config.logV("Response--code--------BottomSheetDialog-----------------" + response.code());
                        dialog = new BottomSheetDialog(mContext);
                        dialog.setContentView(R.layout.rating);
                        dialog.setCancelable(true);
                        dialog.show();
                        TextView tv_title = (TextView) dialog.findViewById(R.id.txtratevisit);
                        final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
                        final RatingBar rating = (RatingBar) dialog.findViewById(R.id.rRatingBar);
                        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                                "fonts/Montserrat_Bold.otf");
                        tv_title.setTypeface(tyface);
                        final Button btn_close = (Button) dialog.findViewById(R.id.btn_cancel);
                        final Button btn_rate = (Button) dialog.findViewById(R.id.btn_send);
                        btn_rate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                rate = rating.getRating();
                                comment = edt_message.getText().toString();
                                if (response.body().size() == 0) {
                                    firstTimeRating = true;
                                } else {
                                    firstTimeRating = false;
                                }
                                ApiPUTRating(Math.round(rate), UUID, comment, accountID, dialog, firstTimeRating);

                            }
                        });
                        edt_message.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void afterTextChanged(Editable arg0) {
                                if (edt_message.getText().toString().length() >= 1 && !edt_message.getText().toString().trim().isEmpty() && rating != null && rating.getRating() != 0) {
                                    btn_rate.setEnabled(true);
                                    btn_rate.setClickable(true);
                                    btn_rate.setBackground(mContext.getResources().getDrawable(R.drawable.curved_save));
                                } else {
                                    btn_rate.setEnabled(false);
                                    btn_rate.setClickable(false);
                                    btn_rate.setBackground(mContext.getResources().getDrawable(R.drawable.btn_checkin_grey));
                                }
                            }

                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }
                        });

                        if (rating != null) {
                            rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                @Override
                                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                                    if (edt_message.getText().toString().length() >= 1 && !edt_message.getText().toString().trim().isEmpty() && rating.getRating() != 0) {
                                        btn_rate.setEnabled(true);
                                        btn_rate.setClickable(true);
                                        btn_rate.setBackground(mContext.getResources().getDrawable(R.drawable.curved_save));
                                    } else {
                                        btn_rate.setEnabled(false);
                                        btn_rate.setClickable(false);
                                        btn_rate.setBackground(mContext.getResources().getDrawable(R.drawable.btn_checkin_grey));
                                    }
                                }
                            });
                        }


                        btn_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        if (response.body().size() > 0) {
                            if (mRatingDATA.get(0).getStars() != 0) {
                                rating.setRating(Float.valueOf(mRatingDATA.get(0).getStars()));
                            }
                            if (mRatingDATA.get(0).getFeedback() != null) {
                                Config.logV("Comments---------" + mRatingDATA.get(0).getFeedback().get(mRatingDATA.get(0).getFeedback().size() - 1).getComments());
                                edt_message.setText(mRatingDATA.get(0).getFeedback().get(mRatingDATA.get(0).getFeedback().size() - 1).getComments());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<RatingResponse>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Location-----###########@@@@@@-------Fail--------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(OrderDetailActivity.this, mDialog);
            }
        });
    }

    private void ApiPUTRating(final int stars, final String UUID, String feedback, String accountID, final BottomSheetDialog dialog, boolean firstTimerate) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("uId", UUID);
            jsonObj.put("stars", String.valueOf(stars));
            jsonObj.put("feedback", feedback);
            Config.logV("Feedback--------------" + feedback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call;
        if (firstTimerate) {
            call = apiService.putOrderRating(accountID, body);
        } else {
            call = apiService.updateOrderRating(accountID, body);
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Config.logV("URL-------Request---" + response.raw().request().url().toString().trim());
                    if (mDialog.isShowing())
                        Config.closeDialog(OrderDetailActivity.this, mDialog);
                    dialog.dismiss();
                    Config.logV("Put Rating#########################" + response.code());
                    if (response.code() == 200) {
                        if (response.body().string().equalsIgnoreCase("true")) {
                            DynamicToast.make(context, "Rated successfully", AppCompatResources.getDrawable(
                                    context, R.drawable.icon_tickmark),
                                    ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.green), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Location-----###########@@@@@@-------Fail--------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(OrderDetailActivity.this, mDialog);
            }
        });
    }

    private QuestionnaireResponseInput buildQuestionnaireInput(QuestionnaireResponse questionnaire) {

        QuestionnaireResponseInput responseInput = new QuestionnaireResponseInput();
        responseInput.setQuestionnaireId(questionnaire.getQuestionnaireId());
        ArrayList<AnswerLineResponse> answerLineResponse = new ArrayList<>();
        ArrayList<GetQuestion> questions = new ArrayList<>();

        for (QuestionAnswers qAnswers : questionnaire.getQuestionAnswers()) {

            answerLineResponse.add(qAnswers.getAnswerLine());
            questions.add(qAnswers.getGetQuestion());

        }

        responseInput.setAnswerLines(answerLineResponse);
        responseInput.setQuestions(questions);

        return responseInput;

    }

    private ArrayList<LabelPath> buildQuestionnaireLabelPaths(QuestionnaireResponse questionnaire) {

        ArrayList<LabelPath> labelPaths = new ArrayList<>();

        for (QuestionAnswers qAnswers : questionnaire.getQuestionAnswers()) {

            if (qAnswers.getGetQuestion().getFieldDataType().equalsIgnoreCase("fileUpload")) {

                JsonArray jsonArray = new JsonArray();
                jsonArray = qAnswers.getAnswerLine().getAnswer().get("fileUpload").getAsJsonArray();
                for (int i = 0; i < jsonArray.size(); i++) {

                    LabelPath path = new LabelPath();
                    path.setId(labelPaths.size());
                    path.setFileName(jsonArray.get(i).getAsJsonObject().get("caption").getAsString());
                    path.setLabelName(qAnswers.getAnswerLine().getLabelName());
                    path.setPath(jsonArray.get(i).getAsJsonObject().get("s3path").getAsString());
                    path.setType(jsonArray.get(i).getAsJsonObject().get("type").getAsString());

                    labelPaths.add(path);
                }

            }

        }

        return labelPaths;

    }
}