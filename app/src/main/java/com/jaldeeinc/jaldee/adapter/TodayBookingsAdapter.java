package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Interface.ISelectedBooking;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.model.Bookings;
import com.jaldeeinc.jaldee.response.SearchService;

import java.util.ArrayList;
import java.util.List;

public class TodayBookingsAdapter extends RecyclerView.Adapter<TodayBookingsAdapter.ViewHolder> {

    ArrayList<Bookings> bookingsList;
    private boolean isLoading = true;
    public Context context;


    public TodayBookingsAdapter(ArrayList<Bookings> bookingsList, Context context, boolean isLoading, ISelectedBooking iSelectedBooking) {
        this.context = context;
        this.bookingsList = bookingsList;
        this.isLoading = isLoading;
    }

    @NonNull
    @Override
    public TodayBookingsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (isLoading) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shimmer, viewGroup, false);
            return new ViewHolder(v, true);

        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bookings_item, viewGroup, false);
            return new TodayBookingsAdapter.ViewHolder(v,false );
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final TodayBookingsAdapter.ViewHolder viewHolder, final int position) {

        if (!isLoading) {
            final Bookings bookings = bookingsList.get(position);

            viewHolder.tvSpName.setText(bookings.getSpName());
            viewHolder.tvServiceName.setText(bookings.getServiceName());
            viewHolder.tvStatus.setText(bookings.getBookingStatus());
            viewHolder.tvDateAndTime.setText(bookings.getDate());

            // to set icon based on booking
            if (bookings.getBookingType() != null) {
                viewHolder.ivBookingType.setVisibility(View.VISIBLE);
                if (bookings.getBookingType().equalsIgnoreCase(Constants.APPOINTMENT)) {
                    viewHolder.ivBookingType.setImageResource(R.drawable.appt_icon);
                } else if (bookings.getBookingType().equalsIgnoreCase(Constants.CHECKIN)) {
                    viewHolder.ivBookingType.setImageResource(R.drawable.icon_checkin);
                } else if (bookings.getBookingType().equalsIgnoreCase(Constants.TOKEN)) {
                    viewHolder.ivBookingType.setImageResource(R.drawable.icon_token);
                }
                else {
                    viewHolder.ivBookingType.setVisibility(View.GONE);
                }
            }
            else {
                viewHolder.ivBookingType.setVisibility(View.GONE);
            }

            // to set status
            if (bookings.getBookingStatus() != null) {

                viewHolder.tvStatus.setVisibility(View.VISIBLE);
                if (bookings.getBookingStatus().equalsIgnoreCase(Constants.CONFIRMED)) {
                    viewHolder.tvStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.location_theme));
                }
                else if (bookings.getBookingStatus().equalsIgnoreCase(Constants.ARRIVED)){
                    viewHolder.tvStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.donation_theme));
                }
                else if (bookings.getBookingStatus().equalsIgnoreCase(Constants.COMPLETED)){
                    viewHolder.tvStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.appoint_theme));
                }
                else if (bookings.getBookingStatus().equalsIgnoreCase(Constants.CANCELLED)){
                    viewHolder.tvStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.cb_errorRed));
                }
            }else {
                viewHolder.tvStatus.setVisibility(View.GONE);
            }



        }else {
            TodayBookingsAdapter.ViewHolder skeletonViewHolder = (TodayBookingsAdapter.ViewHolder) viewHolder;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            skeletonViewHolder.itemView.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {

        return bookingsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivBookingType, ivServiceIcon;
        CustomTextViewBold tvSpName;
        CustomTextViewSemiBold tvProviderName;
        CustomTextViewMedium tvStatus, tvServiceName, tvDateAndTime;


        public ViewHolder(@NonNull View itemView, boolean isLoading) {
            super(itemView);

            if (!isLoading) {

                ivBookingType = itemView.findViewById(R.id.iv_bookingType);
                ivServiceIcon = itemView.findViewById(R.id.iv_serviceIcon);
                tvSpName = itemView.findViewById(R.id.tv_spName);
                tvProviderName = itemView.findViewById(R.id.tv_providerName);
                tvStatus = itemView.findViewById(R.id.tv_status);
                tvServiceName = itemView.findViewById(R.id.tv_serviceName);
                tvDateAndTime = itemView.findViewById(R.id.tv_dateAndTime);

            }

        }
    }
}