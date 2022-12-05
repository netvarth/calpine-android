package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Interface.ISelectedOrder;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.activities.ProviderDetailActivity;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.model.RlsdQnr;
import com.jaldeeinc.jaldee.response.ActiveOrders;
import com.jaldeeinc.jaldee.response.ItemDetails;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


public class TodayOrdersAdapter extends RecyclerView.Adapter<TodayOrdersAdapter.ViewHolder> {

    ArrayList<ActiveOrders> ordersList;
    private boolean isLoading = true;
    public Context context;
    private int lastPosition = -1;
    private ISelectedOrder iSelectedOrder;
    private boolean hideMoreInfo = false;
    private boolean isVirtualItemsOnly = false;
    List<RlsdQnr> fReleasedQNR;

    public TodayOrdersAdapter(ArrayList<ActiveOrders> ordersList, Context context, boolean isLoading, ISelectedOrder iSelectedOrder, boolean hideMoreInfo) {
        this.context = context;
        this.ordersList = ordersList;
        this.isLoading = isLoading;
        this.iSelectedOrder = iSelectedOrder;
        this.hideMoreInfo = hideMoreInfo;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (isLoading) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shimmer_booking, viewGroup, false);
            return new ViewHolder(v, true);

        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.orders_item, viewGroup, false);
            return new ViewHolder(v, false);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {

        if (!isLoading) {
            final ActiveOrders orders = ordersList.get(position);

            setAnimation(viewHolder.cvBooking, position);

            if (orders.getShoppingList() != null || orders.getTotalItemQuantity() <= 0) {

                viewHolder.rlQuantity.setVisibility(View.GONE);
            } else {
                viewHolder.rlQuantity.setVisibility(View.VISIBLE);
            }

            try {

                if (orders.getProviderAccount() != null && !orders.getProviderAccount().getBusinessName().equalsIgnoreCase("")) {

                    viewHolder.tvSpName.setText(orders.getProviderAccount().getBusinessName());

                } else {

//                    viewHolder.tvSpName.setText(orders.getProviderAccount().getBusinessName());

                }

                viewHolder.tvSpName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent orderIntent = new Intent(view.getContext(), ProviderDetailActivity.class);
                        if (orders.getProviderAccount() != null && orders.getProviderAccount().getUniqueId() != 0) {
                            orderIntent.putExtra("uniqueID", orders.getProviderAccount().getUniqueId());
                        }
                        orderIntent.putExtra("from", "order");
                        context.startActivity(orderIntent);
                    }
                });

                if (orders.getOrderNumber() != null && !orders.getOrderNumber().equalsIgnoreCase("")) {
                    viewHolder.tvServiceName.setText(orders.getOrderNumber());
                }

                if (orders.getTotalItemQuantity() != 0) {
                    viewHolder.tvQuantity.setText(String.valueOf(orders.getTotalItemQuantity()));
                }

                if (orders.getOrderDate() != null) {
                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    String time = "";
                    if (orders.getTimeSlot() != null && orders.getTimeSlot().getsTime() != null && orders.getTimeSlot().geteTime() != null) {
                        time = orders.getTimeSlot().getsTime() + " - " + orders.getTimeSlot().geteTime();
                    }
                    if (date.equalsIgnoreCase(orders.getOrderDate())) {
                        if (time != null && !time.equals("")) {
                            viewHolder.tvDateAndTime.setText("Today," + " " + time);
                        } else {
                            viewHolder.tvDateAndTime.setText("Today");
                        }
                    } else {
                        viewHolder.tvDateAndTime.setText(getCustomDateString(orders.getOrderDate()) + "," + " " + time);
                    }
                }

                if (orders.getItemsList() != null && orders.getItemsList().size() > 0) {   //to get the order contains virtual item only
                    isVirtualItemsOnly = isVirtualItemsOnly(orders.getItemsList());
                }
                // To set icon for delivery type
                if (!isVirtualItemsOnly) {
                    if (orders.isHomeDelivery()) {
                        viewHolder.ivBookingType.setImageResource(R.drawable.home_delivery);
                    } else if (orders.isStorePickup()) {
                        viewHolder.ivBookingType.setImageResource(R.drawable.instore_pickup);
                    }
                } else {
                    viewHolder.ivBookingType.setImageResource(R.drawable.order_virtual_item);
                }

                // to set status
                if (orders.getOrderStatus() != null && !orders.getUid().contains("h_")) {
                    if (orders.getReleasedQnr() != null && orders.getReleasedQnr().size() > 0) {
                        fReleasedQNR = orders.getReleasedQnr().stream()
                                .filter(p -> p.getStatus().equalsIgnoreCase("released")).collect(Collectors.toList());
                    }
                    if (fReleasedQNR != null && !fReleasedQNR.isEmpty() && fReleasedQNR.size() > 0) {
                        viewHolder.rl_qnr_info_needed.setVisibility(View.VISIBLE);
                        viewHolder.rlStatus.setVisibility(View.GONE);
                        Animation anim = new AlphaAnimation(0.0f, 1.0f);
                        anim.setDuration(800); //You can manage the blinking time with this parameter
                        anim.setStartOffset(20);
                        anim.setRepeatMode(Animation.REVERSE);
                        anim.setRepeatCount(Animation.INFINITE);
                        viewHolder.rl_qnr_info_needed.startAnimation(anim);
                    } else {
                        viewHolder.rl_qnr_info_needed.setVisibility(View.GONE);
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
                    }
                } else {
                    viewHolder.tvStatus.setVisibility(View.GONE);
                    viewHolder.rlStatus.setVisibility(View.GONE);
                }

                if (orders.getBill() == null) {
                    if (orders.getAdvanceAmountPaid() != 0.0) {
                        viewHolder.tvpayment.setVisibility(View.VISIBLE);
                        viewHolder.tvpayment.setText("PAID" + " " + "₹" + " " + Config.getAmountNoOrTwoDecimalPoints(orders.getAdvanceAmountPaid()));
                    } else {
                        viewHolder.tvpayment.setVisibility(View.GONE);
                    }
                } else {
                    if (orders.getBill().getAmountPaid() != 0) {
                        viewHolder.tvpayment.setVisibility(View.VISIBLE);
                        viewHolder.tvpayment.setText("PAID" + " " + "₹" + " " + Config.getAmountNoOrTwoDecimalPoints(orders.getBill().getAmountPaid()));
                    }
                }

                viewHolder.cvBooking.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        iSelectedOrder.onOrderClick(orders);
                    }
                });

                if (!hideMoreInfo) {

                    viewHolder.ivMore.setVisibility(View.VISIBLE);
                    viewHolder.ivMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            iSelectedOrder.onOptionsClick(orders);
                        }
                    });

                } else {

                    viewHolder.ivMore.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            ViewHolder skeletonViewHolder = (ViewHolder) viewHolder;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            skeletonViewHolder.itemView.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {

        return isLoading ? 10 : ordersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivBookingType, ivMore;
        CustomTextViewBold tvSpName;
        CustomTextViewSemiBold tvProviderName, ivOrderNo;
        CustomTextViewMedium tvStatus, tvServiceName, tvDateAndTime, tvpayment, tvQuantity;
        CardView cvBooking;
        RelativeLayout rl_qnr_info_needed;
        RelativeLayout rlStatus, rlQuantity;


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
                ivMore = itemView.findViewById(R.id.iv_more);
                rlStatus = itemView.findViewById(R.id.rl_status);
                tvpayment = itemView.findViewById(R.id.tv_payment);
                tvQuantity = itemView.findViewById(R.id.tv_quantityValue);
                rlQuantity = itemView.findViewById(R.id.rl_quantitiy);
                rl_qnr_info_needed = itemView.findViewById(R.id.rl_qnr_info_needed);

            }

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