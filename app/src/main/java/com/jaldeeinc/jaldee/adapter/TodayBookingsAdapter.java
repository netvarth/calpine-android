package com.jaldeeinc.jaldee.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Interface.ISelectedBooking;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.activities.ProviderDetailActivity;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.model.Bookings;
import com.jaldeeinc.jaldee.model.RlsdQnr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class TodayBookingsAdapter extends RecyclerView.Adapter<TodayBookingsAdapter.ViewHolder> {

    ArrayList<Bookings> bookingsList;
    private boolean isLoading = true;
    public Context context;
    private int lastPosition = -1;
    private ISelectedBooking iSelectedBooking;
    private boolean hideMoreInfo = false;
    List<RlsdQnr> fReleasedQNR;

    public TodayBookingsAdapter(ArrayList<Bookings> bookingsList, Context context, boolean isLoading, ISelectedBooking iSelectedBooking, boolean hideMoreInfo) {
        this.context = context;
        this.bookingsList = bookingsList;
        this.isLoading = isLoading;
        this.iSelectedBooking = iSelectedBooking;
        this.hideMoreInfo = hideMoreInfo;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (isLoading) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shimmer_booking, viewGroup, false);
            return new ViewHolder(v, true);

        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bookings_item, viewGroup, false);
            return new ViewHolder(v, false);
        }

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {

        if (!isLoading) {
            final Bookings bookings = bookingsList.get(position);

            setAnimation(viewHolder.cvBooking, position);

            if (hideMoreInfo) {
                viewHolder.ivMore.setVisibility(View.GONE);
            } else {
                viewHolder.ivMore.setVisibility(View.VISIBLE);
            }

            try {

                if (bookings.getProviderName() != null && !bookings.getProviderName().equalsIgnoreCase("")) {

                    viewHolder.tvSpName.setText(bookings.getProviderName());
                    viewHolder.tvProviderName.setVisibility(View.VISIBLE);
                    viewHolder.tvProviderName.setText(bookings.getSpName());
                    viewHolder.tvProviderName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            try {
                                Intent intent = new Intent(context, ProviderDetailActivity.class);
                                intent.putExtra("uniqueID", bookings.getUniqueId());
                                //intent.putExtra("locationId", bookings.getLocation().getId());
                                context.startActivity(intent);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    viewHolder.tvProviderName.setVisibility(View.GONE);
                    viewHolder.tvSpName.setText(bookings.getSpName());
                    viewHolder.tvSpName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            try {
                                Intent intent = new Intent(context, ProviderDetailActivity.class);
                                intent.putExtra("uniqueID", bookings.getUniqueId());
                                //intent.putExtra("locationId", bookings.getLocation().getId());
                                context.startActivity(intent);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                if (bookings.isRescheduled()) {
                    viewHolder.ivRescheduled.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.ivRescheduled.setVisibility(View.GONE);
                }

                if (bookings.getDeptName() != null) {

                    viewHolder.tvServiceName.setText(bookings.getServiceName() + " (" + bookings.getDeptName() + ")");
                } else {

                    viewHolder.tvServiceName.setText(bookings.getServiceName());

                }
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                if (bookings.getBookingType() != null) {
                    if (bookings.getBookingType().equalsIgnoreCase(Constants.APPOINTMENT)) {

                        if (bookings.getAppointmentInfo() != null) {

                            if (bookings.isVirtual() && bookings.getCallingType() != null) {
                                if ((bookings.getBookingStatus() != null && bookings.getBookingStatus().equalsIgnoreCase("cancelled")) || (bookings.getBookingStatus() != null && bookings.getBookingStatus().equalsIgnoreCase("done")) || (bookings.getBookingStatus() != null && bookings.getBookingStatus().equalsIgnoreCase("Completed"))) {
                                    viewHolder.tvDateAndTime.setVisibility(View.GONE);
                                } else if (bookings.getVideoCallMessage() != null && bookings.getVideoCallMessage().equals("Meeting session expired")) {
                                    viewHolder.tvDateAndTime.setText(bookings.getVideoCallMessage());
                                    viewHolder.tvDateAndTime.setTextColor(Color.RED);
                                } else {
                                    if (bookings.getCallingType().equalsIgnoreCase("Zoom")) {
                                        viewHolder.tvDateAndTime.setText(bookings.getVideoCallMessage());
                                    } else if (bookings.getCallingType().equalsIgnoreCase("GoogleMeet")) {
                                        viewHolder.tvDateAndTime.setText(bookings.getVideoCallMessage());

                                    } else if (bookings.getCallingType().equalsIgnoreCase("WhatsApp")) {
                                        if (bookings.isVideoService()) {
                                            viewHolder.tvDateAndTime.setText(bookings.getVideoCallMessage());
                                        } else {
                                            viewHolder.tvDateAndTime.setText(bookings.getVideoCallMessage());
                                        }
                                    } else if (bookings.getCallingType().equalsIgnoreCase("phone")) {
                                        viewHolder.tvDateAndTime.setText(bookings.getVideoCallMessage());
                                    } else if (bookings.getCallingType().equalsIgnoreCase("VideoCall")) {
                                        viewHolder.tvDateAndTime.setText(bookings.getVideoCallMessage());

                                    }
                                    if (bookings.getBookingOn() != null) {
                                        if (date.equalsIgnoreCase(bookings.getBookingOn())) {
                                            viewHolder.tvDateAndTime.setTextColor(0xFF28A745);
                                        } else {
                                            viewHolder.tvDateAndTime.setTextColor(0xFFDC3545);
                                        }
                                    }

                                }
                            } else {
                                if (bookings.getBookingOn() != null) {
                                    if (date.equalsIgnoreCase(bookings.getBookingOn())) {
                                        if (bookings.getAppointmentInfo().getAppmtTime() != null) {
                                            String time = convertTime(bookings.getAppointmentInfo().getAppmtTime().split("-")[0]);
                                            viewHolder.tvDateAndTime.setText("Today," + " " + time);
                                        } else {
                                            viewHolder.tvDateAndTime.setText("Today");
                                        }
                                    } else {
                                        viewHolder.tvDateAndTime.setText(bookings.getDate());
                                    }
                                } else {
                                    viewHolder.tvDateAndTime.setVisibility(View.GONE);
                                }
                                /**below code for REQUEST TYPE BOOKING**/
                                if ((bookings.getBookingStatus() != null) && (bookings.getBookingStatus().equalsIgnoreCase(Constants.REQUESTED) || bookings.getBookingStatus().equalsIgnoreCase(Constants.REQUESTREJECTED))
                                        && bookings.isNoDateTime()) {
                                    viewHolder.tvDateAndTime.setVisibility(View.GONE);
                                }
                            }

                            if (bookings.getAppointmentInfo().getAppmtFor() != null && bookings.getAppointmentInfo().getAppmtFor().size() > 0) {
                                if ((bookings.getBookingStatus() != null) && (bookings.getBookingStatus().equalsIgnoreCase(Constants.REQUESTED) || bookings.getBookingStatus().equalsIgnoreCase(Constants.REQUESTREJECTED))) {
                                    viewHolder.tvForHint.setText("Request For");
                                } else {
                                    viewHolder.tvForHint.setText("Appointment For");
                                }
                                viewHolder.llBookingFor.setVisibility(View.VISIBLE);
                                String name = bookings.getAppointmentInfo().getAppmtFor().get(0).getFirstName() + " " + bookings.getAppointmentInfo().getAppmtFor().get(0).getLastName();
                                viewHolder.tvBookingFor.setText(name);
                            } else {

                                viewHolder.llBookingFor.setVisibility(View.GONE);
                            }

                        }

                    } else if (bookings.getBookingType().equalsIgnoreCase(Constants.CHECKIN)) {
                        if (bookings.getBookingStatus() != null && !bookings.getBookingStatus().equalsIgnoreCase("Done") && !bookings.getBookingStatus().equalsIgnoreCase("started") && !bookings.getBookingStatus().equalsIgnoreCase(Constants.CANCELLED)) {
                            viewHolder.tvDateAndTime.setVisibility(View.VISIBLE);
                        } else {
                            if (bookings.getBookingStatus() == null && bookings.isVirtual()) {
                                viewHolder.tvDateAndTime.setVisibility(View.VISIBLE);
                            } else {
                                viewHolder.tvDateAndTime.setVisibility(View.GONE);
                            }
                        }

                        if (bookings.getCalculationMode() != null && !bookings.getCalculationMode().equalsIgnoreCase("NoCalc")) {

                            int waitTime = bookings.getWaitingTime();
                            if (waitTime == 0) {
                                viewHolder.tvDateAndTime.setText(R.string.now);
                            } else if (waitTime == 1) {
                                String minuteText = "In " + "<b>" + Config.getTimeinHourMinutes(waitTime) + "</b>";
                                viewHolder.tvDateAndTime.setText(Html.fromHtml(minuteText));
                            } else {
                                String minutesText = "In " + "<b>" + Config.getTimeinHourMinutes(waitTime) + "</b>";
                                viewHolder.tvDateAndTime.setText(Html.fromHtml(minutesText));
                            }
                        } else {
                            if (bookings.getServiceTime() != null) {
                                viewHolder.tvDateAndTime.setText("Today, " + bookings.getServiceTime());
                            }
                        }

                        if (bookings.getCheckInInfo() != null) {
                            if (bookings.isVirtual() && bookings.getCallingType() != null) {
                                if ((bookings.getBookingStatus() != null && bookings.getBookingStatus().equalsIgnoreCase("cancelled")) || (bookings.getBookingStatus() != null && bookings.getBookingStatus().equalsIgnoreCase("done")) || (bookings.getBookingStatus() != null && bookings.getBookingStatus().equalsIgnoreCase("Completed"))) {
                                    viewHolder.tvDateAndTime.setVisibility(View.GONE);
                                } else if (bookings.getVideoCallMessage() != null && bookings.getVideoCallMessage().equals("Meeting session expired")) {
                                    viewHolder.tvDateAndTime.setText(bookings.getVideoCallMessage());
                                    viewHolder.tvDateAndTime.setTextColor(Color.RED);
                                } else {
                                    if (bookings.getCallingType().equalsIgnoreCase("Zoom")) {
                                        viewHolder.tvDateAndTime.setText(bookings.getVideoCallMessage());

                                    } else if (bookings.getCallingType().equalsIgnoreCase("GoogleMeet")) {
                                        viewHolder.tvDateAndTime.setText(bookings.getVideoCallMessage());

                                    } else if (bookings.getCallingType().equalsIgnoreCase("WhatsApp")) {
                                        if (bookings.isVideoService()) {
                                            viewHolder.tvDateAndTime.setText(bookings.getVideoCallMessage());
                                        } else {
                                            viewHolder.tvDateAndTime.setText(bookings.getVideoCallMessage());
                                        }
                                    } else if (bookings.getCallingType().equalsIgnoreCase("phone")) {
                                        viewHolder.tvDateAndTime.setText(bookings.getVideoCallMessage());
                                    } else if (bookings.getCallingType().equalsIgnoreCase("VideoCall")) {
                                        viewHolder.tvDateAndTime.setText(bookings.getVideoCallMessage());
                                    }
                                    if (date.equalsIgnoreCase(bookings.getBookingOn())) {
                                        viewHolder.tvDateAndTime.setTextColor(0xFF28A745);
                                    } else {
                                        viewHolder.tvDateAndTime.setTextColor(0xFFDC3545);
                                    }
                                }
                            } else {
                                if (date.equalsIgnoreCase(bookings.getBookingOn())) {
                                    if (bookings.getCheckInInfo().getAppmtTime() != null) {
                                        String time = convertTime(bookings.getCheckInInfo().getCheckInTime().split("-")[0]);
                                        viewHolder.tvDateAndTime.setText("Today," + " " + time);
                                    }
                                } else {
                                    viewHolder.tvDateAndTime.setText(bookings.getDate());
                                }
                            }

                            if (bookings.getCheckInInfo().getWaitlistingFor() != null && bookings.getCheckInInfo().getWaitlistingFor().size() > 0) {

                                viewHolder.llBookingFor.setVisibility(View.VISIBLE);
                                viewHolder.tvForHint.setText("CheckIn For");
                                String name = bookings.getCheckInInfo().getWaitlistingFor().get(0).getFirstName() + " " + bookings.getCheckInInfo().getWaitlistingFor().get(0).getLastName();
                                viewHolder.tvBookingFor.setText(name);
                            } else {

                                viewHolder.llBookingFor.setVisibility(View.GONE);
                            }

                        }
                    } else if (bookings.getBookingType().equalsIgnoreCase(Constants.TOKEN)) {

                        String builder = "Token " + "<b>" + "#" + bookings.getTokenNo() + "</b>";
                        viewHolder.tokenNo.setText(Html.fromHtml(builder));
                        viewHolder.tokenNo.setVisibility(View.VISIBLE);

                        if (bookings.getCheckInInfo() != null) {

                            if (bookings.isVirtual() && bookings.getCallingType() != null) {

                                if ((bookings.getBookingStatus() != null && bookings.getBookingStatus().equalsIgnoreCase("cancelled")) || (bookings.getBookingStatus() != null && bookings.getBookingStatus().equalsIgnoreCase("done")) || (bookings.getBookingStatus() != null && bookings.getBookingStatus().equalsIgnoreCase("Completed"))) {
                                    //viewHolder.tvDateAndTime.setText("Cancelled");
                                    //viewHolder.tvDateAndTime.setTextColor(Color.RED);
                                    viewHolder.tvDateAndTime.setVisibility(View.GONE);
                                } else if (bookings.getVideoCallMessage() != null && bookings.getVideoCallMessage().equals("Meeting session expired")) {
                                    viewHolder.tvDateAndTime.setText(bookings.getVideoCallMessage());
                                    viewHolder.tvDateAndTime.setTextColor(Color.RED);
                                } else {
                                    if (bookings.getCallingType().equalsIgnoreCase("Zoom")) {
                                        viewHolder.tvDateAndTime.setText(bookings.getVideoCallMessage());

                                    } else if (bookings.getCallingType().equalsIgnoreCase("GoogleMeet")) {
                                        viewHolder.tvDateAndTime.setText(bookings.getVideoCallMessage());

                                    } else if (bookings.getCallingType().equalsIgnoreCase("WhatsApp")) {
                                        if (bookings.isVideoService()) {
                                            viewHolder.tvDateAndTime.setText(bookings.getVideoCallMessage());
                                        } else {
                                            //String time = convertTime(bookings.getAppointmentInfo().getAppmtTime().split("-")[0]);
                                            //viewHolder.tvDateAndTime.setText("Today," + " " + time);
                                            viewHolder.tvDateAndTime.setText(bookings.getVideoCallMessage());
                                        }
                                    } else if (bookings.getCallingType().equalsIgnoreCase("phone")) {
                                        //String time = convertTime(bookings.getAppointmentInfo().getAppmtTime().split("-")[0]);
                                        //viewHolder.tvDateAndTime.setText("Today," + " " + time);
                                        viewHolder.tvDateAndTime.setText(bookings.getVideoCallMessage());
                                    } else if (bookings.getCallingType().equalsIgnoreCase("VideoCall")) {
                                        viewHolder.tvDateAndTime.setText(bookings.getVideoCallMessage());

                                    }
                                    if (date.equalsIgnoreCase(bookings.getBookingOn())) {
                                        viewHolder.tvDateAndTime.setTextColor(0xFF28A745);
                                    } else {
                                        viewHolder.tvDateAndTime.setTextColor(0xFFDC3545);
                                    }
                                }
                            } else {
                                if (date.equalsIgnoreCase(bookings.getBookingOn())) {

                                    if (bookings.getCheckInInfo().getAppmtTime() != null) {
                                        String time = convertTime(bookings.getCheckInInfo().getCheckInTime().split("-")[0]);
                                        viewHolder.tvDateAndTime.setText("Today," + " " + time);
                                    }
                                } else {
                                    viewHolder.tvDateAndTime.setText(bookings.getDate());

                                }
                            }

                            if (bookings.getCheckInInfo().getWaitlistingFor() != null && bookings.getCheckInInfo().getWaitlistingFor().size() > 0) {

                                viewHolder.llBookingFor.setVisibility(View.VISIBLE);
                                viewHolder.tvForHint.setText("Token For");
                                String name = bookings.getCheckInInfo().getWaitlistingFor().get(0).getFirstName() + " " + bookings.getCheckInInfo().getWaitlistingFor().get(0).getLastName();
                                viewHolder.tvBookingFor.setText(name);
                            } else {

                                viewHolder.llBookingFor.setVisibility(View.GONE);
                            }
                        }
                    }
                }

                // to set icon based on booking
                if (bookings.getBookingType() != null) {
                    viewHolder.ivBookingType.setVisibility(View.VISIBLE);
                    if (bookings.getBookingType().equalsIgnoreCase(Constants.APPOINTMENT)) {
                        viewHolder.ivBookingType.setImageResource(R.drawable.icon_appt);
                    } else if (bookings.getBookingType().equalsIgnoreCase(Constants.CHECKIN)) {
                        viewHolder.ivBookingType.setImageResource(R.drawable.icon_checkin);
                    } else if (bookings.getBookingType().equalsIgnoreCase(Constants.TOKEN)) {
                        viewHolder.ivBookingType.setImageResource(R.drawable.icon_token);
                    } else {
                        viewHolder.ivBookingType.setVisibility(View.GONE);
                    }
                } else {
                    viewHolder.ivBookingType.setVisibility(View.GONE);
                }

                // to set status
                if (bookings.getBookingStatus() != null) {
                    if (bookings.getReleasedQnr() != null && bookings.getReleasedQnr().size() > 0) {
                        fReleasedQNR = bookings.getReleasedQnr().stream()
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
                        if (bookings.getBookingStatus().equalsIgnoreCase("Done")) {
                            viewHolder.tvStatus.setText("Completed");
                        } else {
                            if (bookings.getBookingStatus().equalsIgnoreCase(Constants.REQUESTREJECTED)) {
                                viewHolder.tvStatus.setText("Request Rejected");
                            } else {
                                viewHolder.tvStatus.setText(convertToTitleForm(bookings.getBookingStatus()));
                            }
                        }
                        if (bookings.getBookingStatus().equalsIgnoreCase(Constants.CONFIRMED)) {
                            viewHolder.rlStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.appoint_theme));
                        } else if (bookings.getBookingStatus().equalsIgnoreCase(Constants.ARRIVED)) {
                            viewHolder.rlStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.appoint_theme));
                        } else if (bookings.getBookingStatus().equalsIgnoreCase(Constants.COMPLETED)) {
                            viewHolder.rlStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.location_theme));
                        } else if (bookings.getBookingStatus().equalsIgnoreCase(Constants.CANCELLED)) {
                            viewHolder.rlStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.cb_errorRed));
                        } else if (bookings.getBookingStatus().equalsIgnoreCase(Constants.CHECKEDIN)) {
                            viewHolder.rlStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.appoint_theme));
                        } else if (bookings.getBookingStatus().equalsIgnoreCase(Constants.REQUESTED)) {
                            viewHolder.rlStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.orange));
                        } else if (bookings.getBookingStatus().equalsIgnoreCase(Constants.REQUESTREJECTED)) {
                            viewHolder.rlStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.cb_errorRed));
                        } else if (bookings.getBookingStatus().equalsIgnoreCase(Constants.DONE)) {
                            viewHolder.rlStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.location_theme));
                        } else {
                            viewHolder.rlStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.orange));
                        }
                    }
                } else {
                    viewHolder.tvStatus.setVisibility(View.GONE);
                    viewHolder.rlStatus.setVisibility(View.GONE);
                }


                if (bookings.isVirtual() && bookings.getCallingType() != null) {

                    viewHolder.ivServiceIcon.setVisibility(View.VISIBLE);
                    if (bookings.getCallingType().equalsIgnoreCase("Zoom")) {
                        viewHolder.ivServiceIcon.setImageResource(R.drawable.zoom_meet);

                    } else if (bookings.getCallingType().equalsIgnoreCase("GoogleMeet")) {
                        viewHolder.ivServiceIcon.setImageResource(R.drawable.google_meet);

                    } else if (bookings.getCallingType().equalsIgnoreCase("WhatsApp")) {
                        if (bookings.isVideoService()) {
                            viewHolder.ivServiceIcon.setImageResource(R.drawable.whatsapp_videoicon);
                        } else {
                            viewHolder.ivServiceIcon.setImageResource(R.drawable.whatsapp_icon);
                        }
                    } else if (bookings.getCallingType().equalsIgnoreCase("phone")) {
                        viewHolder.ivServiceIcon.setImageResource(R.drawable.phoneicon_sized);
                    } else if (bookings.getCallingType().equalsIgnoreCase("VideoCall")) {
                        viewHolder.ivServiceIcon.setImageResource(R.drawable.ic_jaldeevideo);

                    }
                } else {
                    viewHolder.ivServiceIcon.setVisibility(View.GONE);
                }

                if (bookings.getBookingType().equalsIgnoreCase(Constants.CHECKIN) || bookings.getBookingType().equalsIgnoreCase(Constants.TOKEN)) {
                    if (bookings.getCheckInInfo() != null && bookings.getCheckInInfo().getAmountPaid() != 0.0) {
                        viewHolder.tvpayment.setVisibility(View.VISIBLE);
                        viewHolder.tvpayment.setText("PAID" + " " + "₹" + " " + Config.getAmountNoOrTwoDecimalPoints(bookings.getCheckInInfo().getAmountPaid()));
                    }
                } else if (bookings.getBookingType().equalsIgnoreCase(Constants.APPOINTMENT)) {
                    if (bookings.getAppointmentInfo() != null && bookings.getAppointmentInfo().getAmountPaid() != null && !bookings.getAppointmentInfo().getAmountPaid().equalsIgnoreCase("0.0")) {
                        viewHolder.tvpayment.setVisibility(View.VISIBLE);
                        viewHolder.tvpayment.setText("PAID" + " " + "₹" + " " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(bookings.getAppointmentInfo().getAmountPaid())));
                    }
                }

                viewHolder.cvBooking.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        iSelectedBooking.sendBookingInfo(bookings);

                    }
                });

                viewHolder.ivMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        iSelectedBooking.sendSelectedBookingActions(bookings);

                    }
                });

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

        return isLoading ? 10 : bookingsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivBookingType, ivServiceIcon, ivMore, ivRescheduled;
        TextView tvStatus, tvProviderName, tvSpName, tvServiceName, tvForHint, tvBookingFor, tokenNo, tvDateAndTime, tvpayment;
        CardView cvBooking;
        RelativeLayout rl_qnr_info_needed;
        RelativeLayout rlStatus;
        LinearLayout llBookingFor;


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
                cvBooking = itemView.findViewById(R.id.cv_booking);
                ivMore = itemView.findViewById(R.id.iv_more);
                ivRescheduled = itemView.findViewById(R.id.iv_rescheduled);
                rlStatus = itemView.findViewById(R.id.rl_status);
                tvpayment = itemView.findViewById(R.id.tv_payment);
                tokenNo = itemView.findViewById(R.id.tv_tokenno);
                tvForHint = itemView.findViewById(R.id.tv_forHint);
                tvBookingFor = itemView.findViewById(R.id.tv_bookingFor);
                llBookingFor = itemView.findViewById(R.id.ll_bookingFor);
                rl_qnr_info_needed = itemView.findViewById(R.id.rl_qnr_info_needed);

            }

        }
    }

    public static String convertToTitleForm(String name) {

        String convertName = name;
        convertName = convertName.substring(0, 1).toUpperCase() + convertName.substring(1).toLowerCase();
        return convertName;
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

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}