package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.jaldeeinc.jaldee.CustomSwipe.DiscreteScrollView;
import com.jaldeeinc.jaldee.CustomSwipe.transform.ScaleTransformer;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.OrdersAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.Contents;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.QRCodeEncoder;
import com.jaldeeinc.jaldee.response.ActiveOrders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderConfirmation extends AppCompatActivity {

    @BindView(R.id.rv_items)
    DiscreteScrollView rvItems;

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


    private Context mContext;
    private String orderUUid;
    private int accountId;
    private GridLayoutManager gridLayoutManager;
    private OrdersAdapter ordersAdapter;
    private ActiveOrders orderInfo = new ActiveOrders();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);
        ButterKnife.bind(OrderConfirmation.this);
        mContext = OrderConfirmation.this;


        Intent intent = getIntent();
        orderInfo = (ActiveOrders) intent.getSerializableExtra("orderInfo");

        if (orderInfo != null) {

            orderUUid = orderInfo.getUid();
            if (orderInfo.getProviderAccount() != null) {
                accountId = orderInfo.getProviderAccount().getId();
            }

        }

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
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
        final Dialog mDialog = Config.getProgressDialog(OrderConfirmation.this, OrderConfirmation.this.getResources().getString(R.string.dialog_log_in));
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

    private void updateUI(ActiveOrders orderInfo) {

        try {

            if (orderInfo != null) {

                if (orderInfo.getProviderAccount() != null && orderInfo.getProviderAccount().getBusinessName() != null) {
                    tvSpName.setText(orderInfo.getProviderAccount().getBusinessName());
                }

                tvOrderNO.setText(orderInfo.getOrderNumber());
                tvStatus.setText(orderInfo.getOrderStatus());
                tvDate.setText(getCalenderDateFormat(orderInfo.getOrderDate()));
                if (orderInfo.getTimeSlot() != null) {
                    tvTime.setText(orderInfo.getTimeSlot().getsTime() + " - " + orderInfo.getTimeSlot().geteTime());
                }

                if (orderInfo.isHomeDelivery()) {

                    tvDeliveryType.setText("Home Delivery");

                } else if (orderInfo.isStorePickup()) {

                    tvDeliveryType.setText("Store pickup");

                }

                if (orderInfo.getOrderFor() != null) {

                    tvConsumerName.setText(orderInfo.getOrderFor().getFirstName() + " " + orderInfo.getOrderFor().getLastName());
                }

                // to set items
//                gridLayoutManager = new GridLayoutManager(mContext, 2);
                ordersAdapter = new OrdersAdapter(orderInfo.getItemsList(), mContext, false);
//                rvItems.setLayoutManager(gridLayoutManager);
                rvItems.setAdapter(ordersAdapter);
                rvItems.setItemTransformer(new ScaleTransformer.Builder()
                        .setMinScale(0.8f)
                        .build());


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

                                Dialog settingsDialog = new Dialog(OrderConfirmation.this);
                                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                                settingsDialog.getWindow().getAttributes().windowAnimations = R.style.zoomInAndOut;
                                settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.image_layout
                                        , null));
                                ImageView imageView = settingsDialog.findViewById(R.id.iv_close);
                                ImageView ivQR = settingsDialog.findViewById(R.id.iv_Qr);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        settingsDialog.dismiss();
                                    }
                                });

                                ivQR.setImageBitmap(bitmap);
                                settingsDialog.show();
                            }
                        });


                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }

            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    public static String getCalenderDateFormat(String d) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = format.parse(d);
        format = new SimpleDateFormat("EEE, dd/MM/yyyy");
        String yourDate = format.format(date1);

        return yourDate;

    }
}