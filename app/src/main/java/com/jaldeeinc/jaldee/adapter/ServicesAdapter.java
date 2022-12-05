package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.text.Html;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.jaldeeinc.jaldee.Interface.ISelectedService;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.AppointmentServiceDialog;
import com.jaldeeinc.jaldee.custom.DonationServiceDialog;
import com.jaldeeinc.jaldee.custom.ServiceInfoDialog;
import com.jaldeeinc.jaldee.response.DepServiceInfo;
import com.jaldeeinc.jaldee.response.DepartmentInfo;
import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ServicesAdapter extends SectionRecyclerViewAdapter<DepartmentInfo, DepServiceInfo, Parent, Child> {

    Context context;
    ArrayList<DepartmentInfo> departmentInfoList;
    private boolean isLoading = false;
    public static final int VIEW_TYPE_HEADER = 0;
    private ISelectedService iSelectedService;
    private int lastPosition = -1;
    private ServiceInfoDialog serviceInfoDialog;
    private AppointmentServiceDialog appointmentServiceDialog;
    private DonationServiceDialog donationServiceDialog;
    private SearchViewDetail providerInfo;

    public ServicesAdapter(Context context, ArrayList<DepartmentInfo> sectionItemList, boolean isLoading, ISelectedService iSelectedService, SearchViewDetail mBusinessDataList) {
        super(context, sectionItemList);
        this.context = context;
        this.departmentInfoList = sectionItemList;
        this.isLoading = isLoading;
        this.iSelectedService = iSelectedService;
        this.providerInfo = mBusinessDataList;
    }

    @Override
    public Parent onCreateSectionViewHolder(ViewGroup sectionViewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.parent_layout, sectionViewGroup, false);
        return new Parent(view);
    }

    @Override
    public Child onCreateChildViewHolder(ViewGroup childViewGroup, int viewType) {

        if (!isLoading) {
            View view = LayoutInflater.from(context).inflate(R.layout.service_card, childViewGroup, false);
            return new Child(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.shimmer, childViewGroup, false);
            return new Child(view);
        }
    }

    @Override
    public void onBindSectionViewHolder(Parent sectionViewHolder, int sectionPosition, DepartmentInfo section) {

        String name = section.getDepartmentName();
        name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        sectionViewHolder.departmentName.setText(name);


    }

    @Override
    public void onBindChildViewHolder(Child viewHolder, int sectionPosition, int childPosition, DepServiceInfo child) {

        // to set name of service/provider
        String name = child.getName();
        if (name != null) {
            viewHolder.tvName.setText(name);
        }

        setAnimation(viewHolder.cvCard, childPosition);

        // to set Provider image
        if (child.getType().equalsIgnoreCase(Constants.PROVIDER) || child.getType().equalsIgnoreCase(Constants.ORDERS)) {
            viewHolder.cvImage.setVisibility(View.VISIBLE);
            viewHolder.llTime.setVisibility(View.GONE);
            viewHolder.llDonationRange.setVisibility(View.GONE);
            viewHolder.llEstwaitTime.setVisibility(View.GONE);
            viewHolder.tvServiceType.setVisibility(View.GONE);
            viewHolder.ivMore.setVisibility(View.GONE);
            if (child.getProviderImage() != null) {
                Glide.with(context).load(child.getProviderImage()).fitCenter().placeholder(R.drawable.icon_noimage).into(viewHolder.ivImage);

                //PicassoTrustAll.getInstance(context).load(child.getProviderImage()).fit().placeholder(R.drawable.icon_noimage).into(viewHolder.ivImage);
            } else {
                if (child.getType().equalsIgnoreCase(Constants.ORDERS)) {
                    Glide.with(context).load(R.drawable.ic_catalogue).into(viewHolder.ivImage);
                } else {
                    Glide.with(context).load(R.drawable.icon_noimage).into(viewHolder.ivImage);
                }
            }
        } else {
            viewHolder.llTime.setVisibility(View.VISIBLE);
            viewHolder.llDonationRange.setVisibility(View.VISIBLE);
            viewHolder.llEstwaitTime.setVisibility(View.VISIBLE);
            viewHolder.tvServiceType.setVisibility(View.VISIBLE);
            viewHolder.ivMore.setVisibility(View.VISIBLE);
            viewHolder.cvImage.setVisibility(View.GONE);
        }

        // to set next available time if available
        if (child.getNextAvailableDate() != null && child.getNextAvailableTime() != null) {

            if (child.getType() != null && child.getType().equalsIgnoreCase(Constants.APPOINTMENT)) {
                viewHolder.llDonationRange.setVisibility(View.GONE);
                viewHolder.tvPeopleAhead.setVisibility(View.GONE);
                viewHolder.llTime.setVisibility(View.VISIBLE);
                viewHolder.llEstwaitTime.setVisibility(View.GONE);
                String date = child.getNextAvailableDate();
                String time = child.getNextAvailableTime();
                viewHolder.tvNextAvailableTime.setText(convertDate(date) + "," + "  " + convertTime(time));
            } else {
                viewHolder.llTime.setVisibility(View.GONE);
            }
        }

        // to set people waiting in line if available

        if (child.getType() != null && child.getType().equalsIgnoreCase(Constants.CHECKIN)) {

            if (child.isAvailability()) {
                viewHolder.tvPeopleAhead.setVisibility(View.VISIBLE);
                viewHolder.llDonationRange.setVisibility(View.GONE);

                if (child.getPeopleInLine() != null) {
                    int number = child.getPeopleInLine();
                    if (number >= 0) {
                        String changedtext = "People waiting in line : " + "<b>" + child.getPeopleInLine() + "</b> ";
                        viewHolder.tvPeopleAhead.setText(Html.fromHtml(changedtext));
                    }
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


        if (child.getType().equalsIgnoreCase(Constants.CHECKIN)) {
            if (child.getNextAvailableDate() != null && child.getNextAvailableTime() != null || child.getEstTime() != null) {
                // to set est waitTime if available
                if (!child.getCalculationMode().equalsIgnoreCase("NoCalc")) {
                    viewHolder.llEstwaitTime.setVisibility(View.VISIBLE);
                    viewHolder.llDonationRange.setVisibility(View.GONE);
                    viewHolder.llTime.setVisibility(View.GONE);
                    String time = getWaitingTime(child.getNextAvailableDate(), child.getNextAvailableTime(), child.getEstTime());
                    viewHolder.tvEstWaitTime.setText(time.split("-")[1]);
                    viewHolder.tvTimeHint.setText(time.split("-")[0]);

                } else {
                    viewHolder.llEstwaitTime.setVisibility(View.GONE);
                    viewHolder.llTime.setVisibility(View.GONE);
                }
            } else {
                viewHolder.llEstwaitTime.setVisibility(View.GONE);
                viewHolder.llTime.setVisibility(View.GONE);
            }
        }

        // to set Donation range if available
        if (child.getType() != null && child.getType().equalsIgnoreCase(Constants.DONATION)) {

            if (child.getMinDonationAmount() != null && child.getMaxDonationAmount() != null) {

                if (!child.getMinDonationAmount().equalsIgnoreCase("") && !child.getMaxDonationAmount().equalsIgnoreCase("")) {
                    viewHolder.llDonationRange.setVisibility(View.VISIBLE);
                    viewHolder.llTime.setVisibility(View.GONE);
                    viewHolder.llEstwaitTime.setVisibility(View.GONE);
                    if (Double.parseDouble(child.getMaxDonationAmount()) == Double.parseDouble(child.getMinDonationAmount())) {
                        viewHolder.tvDontnAmount.setText("Donate ₹\u00A0" + getMoneyFormat(child.getMinDonationAmount()));
                    } else {
                        viewHolder.tvDontnAmount.setText("Donate ₹\u00A0" + getMoneyFormat(child.getMinDonationAmount()) + " or more");
                    }
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
        if (child.getType() != null && child.getType().equalsIgnoreCase(Constants.CHECKIN)) {

            if (child.getChecinServiceInfo() != null) {

                viewHolder.llDonationRange.setVisibility(View.GONE);
                if (child.isToken()) {
                    viewHolder.tvServiceType.setVisibility(View.VISIBLE);
                    viewHolder.tvServiceType.setText("Get Token");
                } else {
                    viewHolder.tvServiceType.setVisibility(View.VISIBLE);
                    viewHolder.tvServiceType.setText("Check-in");
                }
                viewHolder.tvServiceType.setTextColor(ContextCompat.getColor(context, R.color.checkin_theme));
            }

        } else if (child.getType() != null && child.getType().equalsIgnoreCase(Constants.APPOINTMENT)) {

            viewHolder.llDonationRange.setVisibility(View.GONE);
            viewHolder.tvServiceType.setVisibility(View.VISIBLE);
            if (child.getAppointmentServiceInfo().getServiceBookingType().equalsIgnoreCase(Constants.SERVICE_BOOKING_TYPE_REQUEST)) {
                viewHolder.tvServiceType.setText("Request");
            } else {
                viewHolder.tvServiceType.setText("Appointment");
            }
            viewHolder.tvServiceType.setTextColor(ContextCompat.getColor(context, R.color.appoint_theme));


        } else if (child.getType() != null && child.getType().equalsIgnoreCase(Constants.DONATION)) {

            viewHolder.tvServiceType.setVisibility(View.VISIBLE);
            viewHolder.tvServiceType.setText("Donate");
            viewHolder.tvServiceType.setTextColor(ContextCompat.getColor(context, R.color.donation_theme));

        } else if (child.getType() != null && child.getType().equalsIgnoreCase(Constants.PROVIDER)) {

            viewHolder.llDonationRange.setVisibility(View.GONE);
            viewHolder.tvServiceType.setVisibility(View.GONE);
            viewHolder.tvPeopleAhead.setVisibility(View.GONE);
            viewHolder.llEstwaitTime.setVisibility(View.GONE);
            viewHolder.llTime.setVisibility(View.GONE);

        }

        // to set teleservice icon
        if (child.getServiceMode().equalsIgnoreCase("virtualService")) {
            viewHolder.ivTeleService.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivTeleService.setVisibility(View.GONE);
        }

        if (child.getType() != null) {

            if (child.getType().equalsIgnoreCase(Constants.CHECKIN) || child.getType().equalsIgnoreCase(Constants.APPOINTMENT)) {

                if (child.getServiceMode() != null && child.getServiceMode().equalsIgnoreCase("virtualService")) {

                    if (child.getCallingMode() != null) {
                        viewHolder.ivTeleService.setVisibility(View.VISIBLE);
                        if (child.getCallingMode().equalsIgnoreCase("Zoom")) {

                            viewHolder.ivTeleService.setImageResource(R.drawable.zoomicon_sized);

                        } else if (child.getCallingMode().equalsIgnoreCase("GoogleMeet")) {

                            viewHolder.ivTeleService.setImageResource(R.drawable.googlemeet_sized);

                        } else if (child.getCallingMode().equalsIgnoreCase("WhatsApp")) {
                            if (child.getVirtualServiceType() != null && child.getVirtualServiceType().equalsIgnoreCase("videoService")) {
                                viewHolder.ivTeleService.setImageResource(R.drawable.whatsapp_videoicon);
                            } else {
                                viewHolder.ivTeleService.setImageResource(R.drawable.whatsapp_icon);
                            }

                        } else if (child.getCallingMode().equalsIgnoreCase("phone")) {

                            viewHolder.ivTeleService.setImageResource(R.drawable.phoneaudioicon);

                        } else if (child.getCallingMode().equalsIgnoreCase("VideoCall")) {

                            viewHolder.ivTeleService.setImageResource(R.drawable.ic_jaldeevideo);

                        }
                    }

                } else {

                    viewHolder.ivTeleService.setVisibility(View.GONE);
                }
            } else {
                viewHolder.ivTeleService.setVisibility(View.GONE);
            }
        }


        viewHolder.cvCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (child.isAvailability()) { // checking service availability - for provider availability is set as true by default

                    if (child.getType() != null) {

                        if (child.getType().equalsIgnoreCase(Constants.CHECKIN)) {

                            if (child.isOnline()) { // checking if the provider is online in order to let user book the service

                                if (child.getChecinServiceInfo() != null) {
                                    iSelectedService.onCheckInSelected(child.getChecinServiceInfo());
                                }
                            } else {
                                showProviderUnavailable();
                            }

                        } else if (child.getType().equalsIgnoreCase(Constants.APPOINTMENT)) {

                            if (child.isOnline()) {

                                if (child.getAppointmentServiceInfo() != null) {
                                    iSelectedService.onAppointmentSelected(child.getAppointmentServiceInfo());
                                }
                            } else {

                                showProviderUnavailable();
                            }
                        } else if (child.getType().equalsIgnoreCase(Constants.PROVIDER)) {

                            if (child.getProviderInfo() != null) {
                                iSelectedService.onProviderSelected(child.getProviderInfo());
                            }
                        }
                        if (child.getType().equalsIgnoreCase(Constants.ORDERS)) {

                            if (child.getCatalogInfo() != null) {
                                iSelectedService.onCatalogSelected(child.getCatalogInfo());
                            }
                        } else if (child.getType().equalsIgnoreCase(Constants.DONATION)) {

                            if (child.isOnline()) {

                                if (child.getDonationServiceInfo() != null) {
                                    iSelectedService.onDonationSelected(child.getDonationServiceInfo());
                                }
                            } else {

                                showProviderUnavailable();
                            }
                        }
                    }
                } else {

                    if (child.getType() != null) { // showing a message consumer when

                        if (child.getType().equalsIgnoreCase(Constants.CHECKIN) || child.getType().equalsIgnoreCase(Constants.APPOINTMENT)) {

                            DynamicToast.make(context, "Selected Service is not available at the moment",
                                    ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.green), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        viewHolder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (child.getType() != null) {

                    if (child.getType().equalsIgnoreCase(Constants.CHECKIN)) {

                        if (child.getChecinServiceInfo() != null) {
                            serviceInfoDialog = new ServiceInfoDialog(context, child.getChecinServiceInfo(), providerInfo);
                            serviceInfoDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                            serviceInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            serviceInfoDialog.show();
                            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                            int width = (int) (metrics.widthPixels * 1);
                            serviceInfoDialog.setCancelable(true);
                            serviceInfoDialog.getWindow().setGravity(Gravity.BOTTOM);
                            serviceInfoDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                        }
                    } else if (child.getType().equalsIgnoreCase(Constants.APPOINTMENT)) {

                        if (child.getAppointmentServiceInfo() != null) {
                            appointmentServiceDialog = new AppointmentServiceDialog(context, child.getAppointmentServiceInfo(), providerInfo);
                            appointmentServiceDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                            appointmentServiceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            appointmentServiceDialog.show();
                            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                            int width = (int) (metrics.widthPixels * 1);
                            appointmentServiceDialog.setCancelable(true);
                            appointmentServiceDialog.getWindow().setGravity(Gravity.BOTTOM);
                            appointmentServiceDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                        }
                    } else if (child.getType().equalsIgnoreCase(Constants.DONATION)) {

                        if (child.getDonationServiceInfo() != null) {
                            donationServiceDialog = new DonationServiceDialog(context, child.getDonationServiceInfo());
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
    }

    private void showProviderUnavailable() {

        DynamicToast.make(context, "Provider is offline at the moment",
                ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.green), Toast.LENGTH_SHORT).show();
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
//                Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
//                        "fonts/Montserrat_Bold.otf");
                secondWord = monthString + " " + day + ", " + nextAvailableTime;
//                String outputDateStr = outputFormat.format(datechange);
//                String yourDate = Config.getFormatedDate(outputDateStr);
//                secondWord = yourDate + ", " + queue.getServiceTime();
            } else {
                secondWord = "Today, " + nextAvailableTime;
            }
        } else {
            firstWord = "Estimated wait time";
            secondWord = Config.getTimeinHourMinutes(Integer.parseInt(estTime));
        }
        return firstWord + "-" + secondWord;
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