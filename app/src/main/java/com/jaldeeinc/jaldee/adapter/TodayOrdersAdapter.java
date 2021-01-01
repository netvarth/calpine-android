package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Interface.ISelectedBooking;
import com.jaldeeinc.jaldee.Interface.ISelectedOrder;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.BillActivity;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.activities.ProviderDetailActivity;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.response.ActiveOrders;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class TodayOrdersAdapter extends RecyclerView.Adapter<TodayOrdersAdapter.ViewHolder> {

    ArrayList<ActiveOrders> ordersList;
    private boolean isLoading = true;
    public Context context;
    private int lastPosition = -1;
    private ISelectedOrder iSelectedOrder;
    private boolean hideMoreInfo = false;


    public TodayOrdersAdapter(ArrayList<ActiveOrders> ordersList, Context context, boolean isLoading, ISelectedOrder iSelectedOrder, boolean hideMoreInfo) {
        this.context = context;
        this.ordersList = ordersList;
        this.isLoading = isLoading;
        this.iSelectedOrder = iSelectedOrder;
        this.hideMoreInfo = hideMoreInfo;
    }

    @NonNull
    @Override
    public TodayOrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (isLoading) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shimmer_booking, viewGroup, false);
            return new ViewHolder(v, true);

        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.orders_item, viewGroup, false);
            return new ViewHolder(v, false);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final TodayOrdersAdapter.ViewHolder viewHolder, final int position) {

        if (!isLoading) {
            final ActiveOrders orders = ordersList.get(position);

            setAnimation(viewHolder.cvBooking, position);

            try {

                if (orders.getProviderAccount() != null && !orders.getProviderAccount().getBusinessName().equalsIgnoreCase("")) {

                    viewHolder.tvSpName.setText(convertToTitleForm(orders.getProviderAccount().getBusinessName()));

                } else {

                    viewHolder.tvSpName.setText(convertToTitleForm(orders.getProviderAccount().getBusinessName()));

                }

                viewHolder.tvSpName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (orders.getUid().contains("h_")){
                            Intent orderIntent = new Intent(view.getContext(), ProviderDetailActivity.class);
                        if (orders.getProviderAccount() != null && orders.getProviderAccount().getUniqueId() != 0) {
                            orderIntent.putExtra("uniqueID", orders.getProviderAccount().getUniqueId());
                        }
                        orderIntent.putExtra("from", "order");
                        view.getContext().startActivity(orderIntent);
                    }
                    }
                });

                if (orders.getOrderNumber() != null && !orders.getOrderNumber().equalsIgnoreCase("")) {
                    viewHolder.tvServiceName.setText("#" + orders.getOrderNumber());
                }

                if (orders.getTotalItemQuantity() != 0) {
                    viewHolder.tvQuantity.setText(String.valueOf(orders.getTotalItemQuantity()));
                }



                if(orders.getOrderDate()!=null){
                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    String time ="";
                    if(orders.getTimeSlot()!=null && orders.getTimeSlot().getsTime()!=null && orders.getTimeSlot().geteTime()!=null){
                         time = orders.getTimeSlot().getsTime() + " - " + orders.getTimeSlot().geteTime();
                    }
                    if(date.equalsIgnoreCase(orders.getOrderDate())){
                        viewHolder.tvDateAndTime.setText("Today," + " " + time );
                    }
                    else {
                        viewHolder.tvDateAndTime.setText(getCustomDateString(orders.getOrderDate()) + "," + " " + time);
                    }
                }


                // To set icon for delivery type
                if (orders.isHomeDelivery()) {
                    viewHolder.ivBookingType.setImageResource(R.drawable.home_delivery);
                } else if (orders.isStorePickup()) {
                    viewHolder.ivBookingType.setImageResource(R.drawable.instore_pickup);
                }


                // to set status
                if (orders.getOrderStatus() != null && !orders.getUid().contains("h_")) {

                    viewHolder.tvStatus.setVisibility(View.VISIBLE);
                    viewHolder.rlStatus.setVisibility(View.VISIBLE);
                    if (orders.getOrderStatus().equalsIgnoreCase("Done")) {
                        viewHolder.tvStatus.setText("Completed");
                    } else {
                        viewHolder.tvStatus.setText(convertToTitleForm(orders.getOrderStatus()));
                    }
                    if (orders.getOrderStatus().equalsIgnoreCase(Constants.CONFIRMED)) {
                        viewHolder.rlStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.appoint_theme));
                    } else if (orders.getOrderStatus().equalsIgnoreCase(Constants.ARRIVED)) {
                        viewHolder.rlStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.appoint_theme));
                    } else if (orders.getOrderStatus().equalsIgnoreCase(Constants.COMPLETED)) {
                        viewHolder.rlStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.location_theme));
                    } else if (orders.getOrderStatus().equalsIgnoreCase(Constants.CANCELLED)) {
                        viewHolder.rlStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.cb_errorRed));
                    } else if (orders.getOrderStatus().equalsIgnoreCase(Constants.CHECKEDIN)) {
                        viewHolder.rlStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.appoint_theme));
                    } else {
                        viewHolder.rlStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.orange));
                    }
                } else {
                    viewHolder.tvStatus.setVisibility(View.GONE);
                    viewHolder.rlStatus.setVisibility(View.GONE);
                }


                if (orders.getBill() != null && orders.getBill().getAmountPaid() != 0.0) {
                    viewHolder.tvpayment.setVisibility(View.VISIBLE);
                    viewHolder.tvpayment.setText("PAID" + " " + "₹" + " " + convertAmountsToDecimals(orders.getBill().getAmountPaid()));
                }

                    if (orders.getBill() != null) {
                        if (orders.getBill().getBillPaymentStatus().equalsIgnoreCase("FullyPaid") || orders.getBill().getBillPaymentStatus().equalsIgnoreCase("Refund")) {
                            viewHolder.ivBill.setVisibility(View.VISIBLE);
                            viewHolder.tvBillText.setVisibility(View.VISIBLE);
                            viewHolder.tvBillText.setText("Receipt");
                        } else {
                            viewHolder.ivBill.setVisibility(View.VISIBLE);
                            viewHolder.tvBillText.setVisibility(View.GONE);
                            viewHolder.tvBillText.setText("Pay Bill");
                        }

                        if (orders.getBill().getBillViewStatus() != null ) {
                            if (orders.getBill().getBillViewStatus().equalsIgnoreCase("Show")) {
                                viewHolder.ivBill.setVisibility(View.VISIBLE);
                                viewHolder.tvBillText.setVisibility(View.VISIBLE);
                            } else {
                                viewHolder.ivBill.setVisibility(View.GONE);
                                viewHolder.tvBillText.setVisibility(View.GONE);
                            }

                        } else {
                            if (!orders.getBill().getBillPaymentStatus().equalsIgnoreCase("NotPaid")) {
                                viewHolder.ivBill.setVisibility(View.VISIBLE);
                                viewHolder.tvBillText.setVisibility(View.VISIBLE);
                            } else {
                                viewHolder.ivBill.setVisibility(View.GONE);
                                viewHolder.tvBillText.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        viewHolder.ivBill.setVisibility(View.GONE);
                        viewHolder.tvBillText.setVisibility(View.GONE);
                    }


                viewHolder.ivBill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent iBill = new Intent(v.getContext(), BillActivity.class);
                        iBill.putExtra("ynwUUID", orders.getUid());
                        iBill.putExtra("provider", orders.getProviderAccount().getBusinessName());
                        iBill.putExtra("accountID", String.valueOf(orders.getProviderAccount().getId()));
                        iBill.putExtra("payStatus", orders.getBill().getBillPaymentStatus());
                        iBill.putExtra("purpose", Constants.PURPOSE_BILLPAYMENT);
                        iBill.putExtra("consumer", orders.getOrderFor().getFirstName() + " " + orders.getOrderFor().getLastName());
                        iBill.putExtra("uniqueId", String.valueOf(orders.getProviderAccount().getUniqueId()));
                        v.getContext().startActivity(iBill);

                    }
                });


                viewHolder.cvBooking.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        iSelectedOrder.onOrderClick(orders);
                    }
                });



            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            TodayOrdersAdapter.ViewHolder skeletonViewHolder = (TodayOrdersAdapter.ViewHolder) viewHolder;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            skeletonViewHolder.itemView.setLayoutParams(params);
        }
    }

    public static String convertAmountsToDecimals(double price) {

        DecimalFormat decim = new DecimalFormat("0.00");
        Double price2 = Double.parseDouble(decim.format(price));
        String amount = decim.format(price2);
        return amount;

    }


    @Override
    public int getItemCount() {

        return isLoading ? 10 : ordersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivBookingType, ivBill;
        CustomTextViewBold tvSpName;
        CustomTextViewSemiBold tvProviderName, ivOrderNo;
        CustomTextViewMedium tvStatus, tvServiceName, tvDateAndTime, tvpayment, tvBillText, tvQuantity;
        CardView cvBooking;
        RelativeLayout rlStatus;


        public ViewHolder(@NonNull View itemView, boolean isLoading) {
            super(itemView);

            if (!isLoading) {

                ivBookingType = itemView.findViewById(R.id.iv_bookingType);
                ivOrderNo = itemView.findViewById(R.id.iv_serviceIcon);
                tvSpName = itemView.findViewById(R.id.tv_spName);
                tvProviderName = itemView.findViewById(R.id.tv_providerName);
                tvStatus = itemView.findViewById(R.id.tv_status);
                tvServiceName = itemView.findViewById(R.id.tv_serviceName);
                tvDateAndTime = itemView.findViewById(R.id.tv_dateAndTime);
                cvBooking = itemView.findViewById(R.id.cv_booking);
                ivBill = itemView.findViewById(R.id.iv_bill);
                rlStatus = itemView.findViewById(R.id.rl_status);
                tvpayment = itemView.findViewById(R.id.tv_payment);
                tvBillText = itemView.findViewById(R.id.billLabel);
                tvQuantity = itemView.findViewById(R.id.tv_quantityValue);

            }

        }
    }

    public static String convertToTitleForm(String name) {

        String convertName = name;
        convertName = convertName.substring(0, 1).toUpperCase() + convertName.substring(1).toLowerCase();
        return convertName;
    }


    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public static String getCustomDateString(String d) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = format.parse(d);
        String date = format.format(date1);

        if (date.endsWith("1") && !date.endsWith("11"))
            format = new SimpleDateFormat("d'st' MMM, yyyy");

        else if (date.endsWith("2") && !date.endsWith("12"))
            format = new SimpleDateFormat("d'nd' MMM, yyyy");

        else if (date.endsWith("3") && !date.endsWith("13"))
            format = new SimpleDateFormat("d'rd' MMM, yyyy");

        else
            format = new SimpleDateFormat("d'th' MMM, yyyy");

        String yourDate = format.format(date1);

        return yourDate;
    }
}
