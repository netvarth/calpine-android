package com.jaldeeinc.jaldee.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.RefundDetailsListAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.payment.PaymentGateway;
import com.jaldeeinc.jaldee.payment.PaytmPayment;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.ActiveDonation;
import com.jaldeeinc.jaldee.response.ActiveOrders;
import com.jaldeeinc.jaldee.response.MyPayments;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 12/9/18.
 */

public class PaymentDetail extends AppCompatActivity {

    Context mActivity;
    TextView provider, dateandtime, mode, paymentMode, paymentGateway, amount, status;
    LinearLayout providerLayout, dateandtimeLayout, modeLayout, paymentModeLayout, paymentGatewayLayout, amountLayout, statusLayout, refundableLayout;
    String id;
    TextView tv_title;
    Context mContext;
    String uniqueID;
    ImageView iBackPress;
    RefundDetailsListAdapter refundDetailsListAdapter;
    RecyclerView recycle_refundlist;
    private ActiveCheckIn bookingDetail = new ActiveCheckIn();
    private ActiveAppointment apptDetail = new ActiveAppointment();
    private ActiveDonation donationDetail = new ActiveDonation();
    private ActiveOrders orderDetail = new ActiveOrders();
    private MyPayments myPayments = new MyPayments();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paymentdetail);
        mActivity = this;

        provider = findViewById(R.id.provider);
        dateandtime = findViewById(R.id.dateandtime);
        mode = findViewById(R.id.mode);
        paymentMode = findViewById(R.id.paymentMode);
        paymentGateway = findViewById(R.id.paymentGateway);
        amount = findViewById(R.id.amount);
        status = findViewById(R.id.status);
        recycle_refundlist = findViewById(R.id.recycle_refundlist);
        providerLayout = findViewById(R.id.providerlayout);
        dateandtimeLayout = findViewById(R.id.dateandtimeLayout);
        modeLayout = findViewById(R.id.modeLayout);
        paymentModeLayout = findViewById(R.id.paymentModeLayout);
        paymentGatewayLayout = findViewById(R.id.paymentGatewayLayout);
        amountLayout = findViewById(R.id.amoutLayout);
        statusLayout = findViewById(R.id.statusLayout);
        refundableLayout = findViewById(R.id.refundableLayout);
        iBackPress = findViewById(R.id.backpress);

        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                finish();
            }
        });

        Typeface tyface = Typeface.createFromAsset(getAssets(), "fonts/Montserrat_Bold.otf");
        tv_title = findViewById(R.id.toolbartitle);
        tv_title.setText("Payment Details");
        tv_title.setTypeface(tyface);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString("myPaymentID", "");
        }

        provider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (myPayments != null && myPayments.getAccountEncodedId() != null) {

                    getUniqueId(myPayments.getAccountEncodedId());
                }
            }
        });

        ApiPayementDetail(id);
    }

    private void getUniqueId(String customID) {
        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<String> call = apiService.getUniqueID(customID);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    if (response.code() == 200) {
                        uniqueID = response.body();
                        if (uniqueID != null) {
                            Intent intent = new Intent(PaymentDetail.this, ProviderDetailActivity.class);
                            intent.putExtra("uniqueID", uniqueID);
                            startActivity(intent);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ApiPayementDetail(String id) {

        final ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<MyPayments> call = apiService.getMyPaymentsDetails(id);

        call.enqueue(new Callback<MyPayments>() {
            @Override
            public void onResponse(Call<MyPayments> call, Response<MyPayments> response) {
                try {
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        if (response.body() != null) {

                            myPayments = response.body();
                            if (response.body().getAccountName() != null) {
                                provider.setText(response.body().getAccountName());
                                providerLayout.setVisibility(View.VISIBLE);
                            } else {
                                providerLayout.setVisibility(View.GONE);
                            }
                            if (response.body().getPaymentOn() != null) {
                                String date = "";
                                try {
                                    date = formatDate(response.body().getPaymentOn().split(" ")[0]);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                String time = convertTime(response.body().getPaymentOn().split(" ")[1]);
                                dateandtime.setText(date + " " + time);
                                dateandtime.setVisibility(View.VISIBLE);
                            } else {
                                dateandtime.setVisibility(View.GONE);
                            }

                            if (response.body().getPaymentMode() != null && (response.body().getStatus().equalsIgnoreCase("SUCCESS") || response.body().getStatus().equalsIgnoreCase("FAILED"))) {
                                if (response.body().getPaymentModeName() != null) {
                                    paymentModeLayout.setVisibility(View.VISIBLE);
                                    paymentMode.setText(response.body().getPaymentModeName());
                                } else {
                                    paymentModeLayout.setVisibility(View.GONE);
                                }
                            } else {
                                paymentModeLayout.setVisibility(View.GONE);
                            }

                            /*if (response.body().getAcceptPaymentBy() != null) {                                // mode
                                mode.setText(response.body().getAcceptPaymentBy());
                                mode.setVisibility(View.VISIBLE);
                            } else {
                                mode.setVisibility(View.GONE);
                            }*/

                            if (response.body().getPaymentGateway() != null) {                                // Payment GateWay
                                paymentGateway.setText(response.body().getPaymentGateway());
                                paymentGatewayLayout.setVisibility(View.VISIBLE);
                            } else {
                                paymentGatewayLayout.setVisibility(View.GONE);
                            }

                            /*if (response.body().getStatus() != null && response.body().getStatus().equals("FAILED")) { // Refund amount
                                refundableLayout.setVisibility(View.GONE);
                            } else {

                                if (response.body().getRefundableAmount() != null) {
                                    refundable.setText("₹ " + Config.getAmountinTwoDecimalPoints(Double.parseDouble(response.body().getRefundableAmount())));
                                    refundable.setVisibility(View.VISIBLE);
                                } else {
                                    refundable.setVisibility(View.GONE);
                                }
                            }*/
                            if (response.body().getAmount() != null) {
                                //  Locale indian = new Locale("en", "IN");
                                // NumberFormat formatter=NumberFormat.getCurrencyInstance(indian);
                                //String currency=formatter.format(Double.parseDouble(response.body().getAmount()));
                                amount.setText("₹ " + Config.getAmountinTwoDecimalPoints(Double.parseDouble(response.body().getAmount())));

                                amount.setVisibility(View.VISIBLE);
                            } else {
                                amount.setVisibility(View.GONE);
                            }


                            if (response.body().getStatus() != null) {
                                status.setText(response.body().getStatus());
                                status.setVisibility(View.VISIBLE);
                            } else {
                                status.setVisibility(View.GONE);
                            }
                            if (myPayments != null) {
                                if (myPayments.getYnwUuid().endsWith("_wl")) {
                                    getBookingDetails(myPayments.getYnwUuid(), myPayments.getAccountId());
                                }
                                if (myPayments.getYnwUuid().endsWith("_appt")) {
                                    getAppointmentDetails(myPayments.getYnwUuid(), myPayments.getAccountId());
                                }
                            }
                            if (response.body().getRefundDetails() != null && response.body().getRefundDetails().size() != 0) {

                                refundDetailsListAdapter = new RefundDetailsListAdapter(mContext, response.body().getRefundDetails());
                                RecyclerView.LayoutManager mRefundDetailsLayoutManager = new LinearLayoutManager(mContext);
                                recycle_refundlist.setLayoutManager(mRefundDetailsLayoutManager);
                                recycle_refundlist.setAdapter(refundDetailsListAdapter);
                                refundDetailsListAdapter.notifyDataSetChanged();
                                refundableLayout.setVisibility(View.VISIBLE);
                            }
                        }
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
            public void onFailure(Call<MyPayments> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());


            }
        });


    }

    private void getBookingDetails(String uid, int id) {

        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<ActiveCheckIn> call = apiService.getActiveCheckInUUID(uid, String.valueOf(id));
        call.enqueue(new Callback<ActiveCheckIn>() {
            @Override
            public void onResponse(Call<ActiveCheckIn> call, Response<ActiveCheckIn> response) {
                try {
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        bookingDetail = response.body();
                        if (bookingDetail.getProvider() != null) {

                            if (bookingDetail.getProvider().getBusinessName() != null && !bookingDetail.getProvider().getBusinessName().equalsIgnoreCase("")) {
                                provider.append(", " + bookingDetail.getProvider().getBusinessName());
                            } else {
                                String name = bookingDetail.getProvider().getFirstName() + " " + bookingDetail.getProvider().getLastName();
                                provider.append(", " + name);
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
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
            }
        });
    }

    public void getAppointmentDetails(String uid, int id) {
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<ActiveAppointment> call = apiService.getActiveAppointmentUUID(uid, String.valueOf(id));
        call.enqueue(new Callback<ActiveAppointment>() {
            @Override
            public void onResponse(Call<ActiveAppointment> call, Response<ActiveAppointment> response) {
                try {
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        apptDetail = response.body();
                        if (apptDetail.getProvider() != null) {

                            if (apptDetail.getProvider().getBusinessName() != null && !apptDetail.getProvider().getBusinessName().equalsIgnoreCase("")) {
                                provider.append(", " + apptDetail.getProvider().getBusinessName());
                            } else {
                                String name = apptDetail.getProvider().getFirstName() + " " + apptDetail.getProvider().getLastName();
                                provider.append(", " + name);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ActiveAppointment> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
            }
        });
    }


    private void getConfirmationId(String uid, int id) {

        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<ActiveDonation> call = apiService.getActiveDonationUUID(uid, String.valueOf(id));
        call.enqueue(new Callback<ActiveDonation>() {
            @Override
            public void onResponse(Call<ActiveDonation> call, Response<ActiveDonation> response) {
                try {
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        donationDetail = response.body();
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

    private void getOrderDetails(String orderUUid, int accountId) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<ActiveOrders> call = apiService.getOrderDetails(orderUUid, accountId);
        call.enqueue(new Callback<ActiveOrders>() {
            @Override
            public void onResponse(Call<ActiveOrders> call, Response<ActiveOrders> response) {
                try {
                    if (response.code() == 200) {
                        orderDetail = response.body();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ActiveOrders> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
            }
        });
    }

    public String formatDate(String date) throws ParseException {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "dd-MMM-yyyy";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date1 = inputFormat.parse(date);
        String date2 = outputFormat.format(date1);

        return date2;
    }

    public static String convertTime(String time) {

        String formattedTime = "";
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            final Date dateObj = sdf.parse(time);
            time = new SimpleDateFormat("hh:mm aa").format(dateObj);
            formattedTime = time.replace("am", "AM").replace("pm", "PM");

        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return formattedTime;
    }
}
