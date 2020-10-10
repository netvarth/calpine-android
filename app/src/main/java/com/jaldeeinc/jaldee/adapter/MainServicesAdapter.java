package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Interface.ISelectedService;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.AppointmentServiceDialog;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.DonationServiceDialog;
import com.jaldeeinc.jaldee.custom.EnquiryDialog;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;
import com.jaldeeinc.jaldee.custom.ServiceInfoDialog;
import com.jaldeeinc.jaldee.response.DepServiceInfo;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.jaldeeinc.jaldee.connection.ApiClient.context;

public class MainServicesAdapter extends RecyclerView.Adapter<MainServicesAdapter.ViewHolder> {

    ArrayList<DepServiceInfo> servicesInfoList;
    public Context context;
    private boolean isLoading = true;
    private ISelectedService iSelectedService;
    private int lastPosition = -1;
    private ServiceInfoDialog serviceInfoDialog;
    private AppointmentServiceDialog appointmentServiceDialog;
    private DonationServiceDialog donationServiceDialog;


    public MainServicesAdapter(ArrayList<DepServiceInfo> servicesInfo, Context context, boolean isLoading, ISelectedService iSelectedService) {
        this.servicesInfoList = servicesInfo;
        this.context = context;
        this.isLoading = isLoading;
        this.iSelectedService = iSelectedService;
    }

    @NonNull
    @Override
    public MainServicesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


        if (isLoading) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shimmer, viewGroup, false);
            return new ViewHolder(v, true);

        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.service_card, viewGroup, false);
            return new ViewHolder(v, false);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MainServicesAdapter.ViewHolder viewHolder, final int position) {

        if (!isLoading) {
            final DepServiceInfo depServiceInfo = servicesInfoList.get(position);

            setAnimation(viewHolder.cvCard, position);

            // to set Provider image
            if (servicesInfoList.get(position).getType().equalsIgnoreCase(Constants.PROVIDER)) {
                viewHolder.cvImage.setVisibility(View.VISIBLE);
                viewHolder.llTime.setVisibility(View.GONE);
                viewHolder.llDonationRange.setVisibility(View.GONE);
                viewHolder.llEstwaitTime.setVisibility(View.GONE);
                viewHolder.tvServiceType.setVisibility(View.GONE);
                viewHolder.ivMore.setVisibility(View.GONE);
                if (servicesInfoList.get(position).getProviderImage() != null) {
                    PicassoTrustAll.getInstance(context).load(servicesInfoList.get(position).getProviderImage()).fit().placeholder(R.drawable.icon_noimage).into(viewHolder.ivImage);

                } else {
                    viewHolder.ivImage.setImageResource(R.drawable.icon_noimage);
                }
            } else {
                viewHolder.llTime.setVisibility(View.VISIBLE);
                viewHolder.llDonationRange.setVisibility(View.VISIBLE);
                viewHolder.llEstwaitTime.setVisibility(View.VISIBLE);
                viewHolder.tvServiceType.setVisibility(View.VISIBLE);
                viewHolder.ivMore.setVisibility(View.VISIBLE);
                viewHolder.cvImage.setVisibility(View.GONE);
            }

            // to set name of service/provider
            if (servicesInfoList.get(position).getName() != null) {

                String name = servicesInfoList.get(position).getName();
                name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                viewHolder.tvName.setText(name);
            }

            // to set next available time if available
            if (servicesInfoList.get(position).getNextAvailableDate() != null && servicesInfoList.get(position).getNextAvailableTime() != null) {

                if (servicesInfoList.get(position).getType() != null && servicesInfoList.get(position).getType().equalsIgnoreCase(Constants.APPOINTMENT)) {
                    viewHolder.llDonationRange.setVisibility(View.GONE);
                    viewHolder.tvPeopleAhead.setVisibility(View.GONE);
                    viewHolder.llTime.setVisibility(View.VISIBLE);
                    viewHolder.llEstwaitTime.setVisibility(View.GONE);
                    String date = servicesInfoList.get(position).getNextAvailableDate();
                    String time = servicesInfoList.get(position).getNextAvailableTime();
                    viewHolder.tvNextAvailableTime.setText(convertDate(date) + "," + "  " + convertTime(time));
                } else {

                    viewHolder.llTime.setVisibility(View.GONE);
                }
            } else {
                viewHolder.llDonationRange.setVisibility(View.GONE);
                viewHolder.tvPeopleAhead.setVisibility(View.GONE);
                viewHolder.llTime.setVisibility(View.GONE);
            }

            // to set people waiting in line if available
            if (servicesInfoList.get(position).getType() != null && servicesInfoList.get(position).getType().equalsIgnoreCase(Constants.CHECKIN)) {

                if (servicesInfoList.get(position).isAvailability()) {
                    viewHolder.tvPeopleAhead.setVisibility(View.VISIBLE);
                    viewHolder.llDonationRange.setVisibility(View.GONE);
                    int number = servicesInfoList.get(position).getPeopleInLine();
                    if (number >= 0) {
                        viewHolder.tvPeopleAhead.setText("People waiting in line : "+servicesInfoList.get(position).getPeopleInLine());
                    }
                } else {

                    viewHolder.llDonationRange.setVisibility(View.GONE);
                    viewHolder.tvPeopleAhead.setVisibility(View.GONE);
                    viewHolder.llEstwaitTime.setVisibility(View.GONE);
                }

            } else {
                viewHolder.llDonationRange.setVisibility(View.GONE);
                viewHolder.tvPeopleAhead.setVisibility(View.GONE);
                viewHolder.llEstwaitTime.setVisibility(View.GONE);
            }

            // to set est waitTime or next available time for checkin - if available

            if (servicesInfoList.get(position).getType().equalsIgnoreCase(Constants.CHECKIN)) {
                if (servicesInfoList.get(position).getNextAvailableDate() != null) {
                    viewHolder.llEstwaitTime.setVisibility(View.VISIBLE);
                    viewHolder.llDonationRange.setVisibility(View.GONE);
                    viewHolder.llTime.setVisibility(View.GONE);
                    String time = getWaitingTime(servicesInfoList.get(position).getNextAvailableDate(), servicesInfoList.get(position).getNextAvailableTime(), servicesInfoList.get(position).getEstTime());
                    viewHolder.tvEstWaitTime.setText(time.split("-")[1]);
                    viewHolder.tvTimeHint.setText(time.split("-")[0]);
                } else {
                    viewHolder.llEstwaitTime.setVisibility(View.GONE);
                    viewHolder.llTime.setVisibility(View.GONE);

                }
            }

            // to set Donation range if available
            if (servicesInfoList.get(position).getType() != null && servicesInfoList.get(position).getType().equalsIgnoreCase(Constants.DONATION)) {

                if (servicesInfoList.get(position).getMinDonationAmount() != null && servicesInfoList.get(position).getMaxDonationAmount() != null) {

                    if (!servicesInfoList.get(position).getMinDonationAmount().equalsIgnoreCase("") && !servicesInfoList.get(position).getMaxDonationAmount().equalsIgnoreCase("")) {
                        viewHolder.llDonationRange.setVisibility(View.VISIBLE);
                        viewHolder.llTime.setVisibility(View.GONE);
                        viewHolder.llEstwaitTime.setVisibility(View.GONE);
                        viewHolder.tvMinAmount.setText("₹" + getMoneyFormat(servicesInfoList.get(position).getMinDonationAmount()));
                        viewHolder.tvMaxAmount.setText("₹" + getMoneyFormat(servicesInfoList.get(position).getMaxDonationAmount()));
                    } else {
                        viewHolder.llDonationRange.setVisibility(View.GONE);
                    }
                } else {
                    viewHolder.llDonationRange.setVisibility(View.GONE);
                    viewHolder.llTime.setVisibility(View.GONE);
                    viewHolder.llEstwaitTime.setVisibility(View.GONE);
                    viewHolder.tvPeopleAhead.setVisibility(View.GONE);
                }
            }

            // to set Service type
            if (servicesInfoList.get(position).getType() != null && servicesInfoList.get(position).getType().equalsIgnoreCase(Constants.CHECKIN)) {

                viewHolder.llDonationRange.setVisibility(View.GONE);
                viewHolder.tvServiceType.setVisibility(View.VISIBLE);
                viewHolder.tvServiceType.setText("Check In");
                viewHolder.tvServiceType.setTextColor(ContextCompat.getColor(context, R.color.checkin_theme));

            } else if (servicesInfoList.get(position).getType() != null && servicesInfoList.get(position).getType().equalsIgnoreCase(Constants.APPOINTMENT)) {

                viewHolder.llDonationRange.setVisibility(View.GONE);
                viewHolder.tvServiceType.setText("Appointments");
                viewHolder.tvServiceType.setVisibility(View.VISIBLE);
                viewHolder.tvServiceType.setTextColor(ContextCompat.getColor(context, R.color.appoint_theme));


            } else if (servicesInfoList.get(position).getType() != null && servicesInfoList.get(position).getType().equalsIgnoreCase(Constants.DONATION)) {

                viewHolder.tvServiceType.setVisibility(View.VISIBLE);
                viewHolder.tvServiceType.setText("Donation");
                viewHolder.tvServiceType.setTextColor(ContextCompat.getColor(context, R.color.donation_theme));

            } else if (servicesInfoList.get(position).getType() != null && servicesInfoList.get(position).getType().equalsIgnoreCase(Constants.PROVIDER)) {

                viewHolder.llDonationRange.setVisibility(View.GONE);
                viewHolder.tvServiceType.setVisibility(View.GONE);
                viewHolder.tvPeopleAhead.setVisibility(View.GONE);
                viewHolder.llEstwaitTime.setVisibility(View.GONE);
                viewHolder.llTime.setVisibility(View.GONE);

            }

            // to set teleservice icon
            if (servicesInfoList.get(position).getServiceMode().equalsIgnoreCase("virtualService")) {
                viewHolder.ivTeleService.setVisibility(View.VISIBLE);
            } else {
                viewHolder.ivTeleService.setVisibility(View.GONE);
            }

            if (servicesInfoList.get(position).getType() != null) {

                if (servicesInfoList.get(position).getType().equalsIgnoreCase(Constants.CHECKIN) || servicesInfoList.get(position).getType().equalsIgnoreCase(Constants.APPOINTMENT)) {

                    if (servicesInfoList.get(position).getServiceMode() != null && servicesInfoList.get(position).getServiceMode().equalsIgnoreCase("virtualService")) {

                        if (servicesInfoList.get(position).getCallingMode() != null) {
                            viewHolder.ivTeleService.setVisibility(View.VISIBLE);
                            if (servicesInfoList.get(position).getCallingMode().equalsIgnoreCase("Zoom")) {

                                viewHolder.ivTeleService.setImageResource(R.drawable.zoom);

                            } else if (servicesInfoList.get(position).getCallingMode().equalsIgnoreCase("GoogleMeet")) {

                                viewHolder.ivTeleService.setImageResource(R.drawable.googlemeet);

                            } else if (servicesInfoList.get(position).getCallingMode().equalsIgnoreCase("WhatsApp")) {

                                viewHolder.ivTeleService.setImageResource(R.drawable.whatsapp_icon);

                            } else if (servicesInfoList.get(position).getCallingMode().equalsIgnoreCase("phone")) {

                                viewHolder.ivTeleService.setImageResource(R.drawable.phone_icon);

                            }
                        }

                    } else {

                        viewHolder.ivTeleService.setVisibility(View.GONE);
                    }
                } else {
                    viewHolder.ivTeleService.setVisibility(View.GONE);
                }
            }


            // click Actions

            viewHolder.cvCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (servicesInfoList.get(position).isAvailability()) {   // checking service availability - for provider availability is set as true by default

                        if (servicesInfoList.get(position).getType() != null) {
                            if (servicesInfoList.get(position).getType().equalsIgnoreCase(Constants.CHECKIN)) {

                                if (servicesInfoList.get(position).isOnline()) {  // checking if the provider is online in order to let user book the service

                                    if (servicesInfoList.get(position).getChecinServiceInfo() != null) {
                                        iSelectedService.onCheckInSelected(servicesInfoList.get(position).getChecinServiceInfo());
                                    }
                                } else {

                                    showProviderUnavailable();
                                }

                            } else if (servicesInfoList.get(position).getType().equalsIgnoreCase(Constants.APPOINTMENT)) {

                                if (servicesInfoList.get(position).isOnline()) {
                                    if (servicesInfoList.get(position).getAppointmentServiceInfo() != null) {
                                        iSelectedService.onAppointmentSelected(servicesInfoList.get(position).getAppointmentServiceInfo());
                                    }
                                } else {
                                    showProviderUnavailable();

                                }

                            } else if (servicesInfoList.get(position).getType().equalsIgnoreCase(Constants.PROVIDER)) {

                                if (servicesInfoList.get(position).getProviderInfo() != null) {
                                    iSelectedService.onProviderSelected(servicesInfoList.get(position).getProviderInfo());
                                }

                            } else if (servicesInfoList.get(position).getType().equalsIgnoreCase(Constants.DONATION)) {

                                if (servicesInfoList.get(position).isOnline()) {
                                    if (servicesInfoList.get(position).getDonationServiceInfo() != null) {
                                        iSelectedService.onDonationSelected(servicesInfoList.get(position).getDonationServiceInfo());
                                    }
                                } else {

                                    showProviderUnavailable();
                                }

                            }
                        }
                    } else {

                        if (servicesInfoList.get(position).getType() != null) {

                            if (servicesInfoList.get(position).getType().equalsIgnoreCase(Constants.CHECKIN) || servicesInfoList.get(position).getType().equalsIgnoreCase(Constants.APPOINTMENT)) {

                                DynamicToast.make(context, "Selected Service is not available at the moment", AppCompatResources.getDrawable(
                                        context, R.drawable.ic_info_black),
                                        ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.green), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });

            viewHolder.ivMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (servicesInfoList.get(position).getType() != null) {

                        if (servicesInfoList.get(position).getType().equalsIgnoreCase(Constants.CHECKIN)) {

                            if (servicesInfoList.get(position).getChecinServiceInfo() != null) {
                                serviceInfoDialog = new ServiceInfoDialog(context, servicesInfoList.get(position).getChecinServiceInfo());
                                serviceInfoDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                                serviceInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                serviceInfoDialog.show();
                                DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                                int width = (int) (metrics.widthPixels * 1);
                                serviceInfoDialog.setCancelable(true);
                                serviceInfoDialog.getWindow().setGravity(Gravity.BOTTOM);
                                serviceInfoDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                            }
                        } else if (servicesInfoList.get(position).getType().equalsIgnoreCase(Constants.APPOINTMENT)) {

                            if (servicesInfoList.get(position).getAppointmentServiceInfo() != null) {
                                appointmentServiceDialog = new AppointmentServiceDialog(context, servicesInfoList.get(position).getAppointmentServiceInfo());
                                appointmentServiceDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                                appointmentServiceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                appointmentServiceDialog.show();
                                DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                                int width = (int) (metrics.widthPixels * 1);
                                appointmentServiceDialog.setCancelable(true);
                                appointmentServiceDialog.getWindow().setGravity(Gravity.BOTTOM);
                                appointmentServiceDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                            }
                        } else if (servicesInfoList.get(position).getType().equalsIgnoreCase(Constants.DONATION)) {

                            if (servicesInfoList.get(position).getDonationServiceInfo() != null) {
                                donationServiceDialog = new DonationServiceDialog(context, servicesInfoList.get(position).getDonationServiceInfo());
                                donationServiceDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                                donationServiceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                donationServiceDialog.show();
                                DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                                int width = (int) (metrics.widthPixels * 1);
                                donationServiceDialog.setCancelable(true);
                                donationServiceDialog.getWindow().setGravity(Gravity.BOTTOM);
                                donationServiceDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                            }
                        }
                    }

                }
            });


        } else {

            MainServicesAdapter.ViewHolder skeletonViewHolder = (ViewHolder) viewHolder;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            skeletonViewHolder.itemView.setLayoutParams(params);

        }


    }

    private void showProviderUnavailable() {

        DynamicToast.make(context, "Provider is offline at the moment", AppCompatResources.getDrawable(
                context, R.drawable.ic_info_black),
                ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.green), Toast.LENGTH_SHORT).show();
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
            finalDate = "Today";
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


    @Override
    public int getItemCount() {

        return isLoading ? 10 : servicesInfoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private ImageView ivImage, ivTeleService, ivMore;
        private CardView cvImage, cvCard;
        private CustomTextViewBold tvName, tvEstWaitTime, tvMinAmount, tvMaxAmount, tvNextAvailableTime;
        private LinearLayout llTime, llEstwaitTime, llDonationRange;
        private CustomTextViewMedium tvNextAvailableText, tvPeopleAhead, tvTimeHint;
        private CustomTextViewSemiBold tvServiceType;
        private RelativeLayout rlCommonLayout;

        public ViewHolder(@NonNull View itemView, boolean isLoading) {

            super(itemView);

            if (!isLoading) {

                ivImage = itemView.findViewById(R.id.iv_image);
                ivTeleService = itemView.findViewById(R.id.iv_teleService);
                cvImage = itemView.findViewById(R.id.cv_image);
                tvName = itemView.findViewById(R.id.tv_serviceName);
                tvEstWaitTime = itemView.findViewById(R.id.tv_estWaitTime);
                tvMinAmount = itemView.findViewById(R.id.tv_minAmount);
                tvMaxAmount = itemView.findViewById(R.id.tv_maxAmount);
                ivMore = itemView.findViewById(R.id.iv_info);
                llTime = itemView.findViewById(R.id.ll_time);
                llEstwaitTime = itemView.findViewById(R.id.ll_estWaitTime);
                llDonationRange = itemView.findViewById(R.id.ll_donationRange);
                tvNextAvailableText = itemView.findViewById(R.id.tv_nextavailableText);
                tvNextAvailableTime = itemView.findViewById(R.id.tv_nextAvailableTime);
                tvPeopleAhead = itemView.findViewById(R.id.tv_peopleAhead);
                tvServiceType = itemView.findViewById(R.id.tv_serviceType);
                cvCard = itemView.findViewById(R.id.cv_card);
                tvTimeHint = itemView.findViewById(R.id.tv_timeHint);

            }
        }
    }

    public static String getWaitingTime(String nextAvailableDate, String nextAvailableTime, String estTime) {
        String firstWord = "";
        String secondWord = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date c = Calendar.getInstance().getTime();
        String formattedDate = df.format(c);
        System.out.println("Current time => " + formattedDate);
        Date date1 = null, date2 = null;
        try {
            date1 = df.parse(formattedDate);
            if (nextAvailableDate != null)
                date2 = df.parse(nextAvailableDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String type = null;
        if (date2 != null && date1.compareTo(date2) < 0) {
            type = "future";
        }
        if (nextAvailableTime != null) {
            firstWord = "Next Available Time ";
            if (type != null) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date = null;
                try {
                    date = format.parse(nextAvailableDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String day = (String) DateFormat.format("dd", date);
                String monthString = (String) DateFormat.format("MMM", date);
                secondWord = monthString + " " + day + ", " + nextAvailableTime;

            } else {
                secondWord = "Today, " + nextAvailableTime;
            }
        } else {
            firstWord = "Estimated wait time";
            secondWord = Config.getTimeinHourMinutes(Integer.parseInt(estTime));
        }
        return firstWord + "-" + secondWord;
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public static String getMoneyFormat(String number) {

        if (!number.isEmpty()) {
            double val = Double.parseDouble(number);
            return NumberFormat.getNumberInstance(Locale.US).format(val);
        } else {
            return "0";
        }
    }
}