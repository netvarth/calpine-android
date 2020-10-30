package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.model.Bookings;
import com.jaldeeinc.jaldee.model.ProviderUserModel;
import com.jaldeeinc.jaldee.response.ActiveAppointment;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jaldeeinc.jaldee.connection.ApiClient.context;

public class BookingDetails extends AppCompatActivity {

    @BindView(R.id.tv_providerName)
    CustomTextViewMedium tvProviderName;

    @BindView(R.id.tv_doctorName)
    CustomTextViewBold tvDoctorName;

    @BindView(R.id.tv_serviceName)
    CustomTextViewMedium tvServiceName;

    @BindView(R.id.iv_teleService)
    ImageView ivTeleService;

    @BindView(R.id.tv_locationName)
    CustomTextViewMedium tvLocationName;

    @BindView(R.id.tv_confirmationNumber)
    CustomTextViewBold tvConfirmationNumber;

    @BindView(R.id.tv_status)
    CustomTextViewBold tvStatus;

    @BindView(R.id.tv_amount)
    CustomTextViewBold tvAmount;

    @BindView(R.id.tv_consumerName)
    CustomTextViewBold tvConsumerName;

    @BindView(R.id.tv_date)
    CustomTextViewBold tvDate;

    @BindView(R.id.tv_time)
    CustomTextViewBold tvTime;

    @BindView(R.id.tv_batchNo)
    CustomTextViewBold tvBatchNo;

    @BindView(R.id.tv_viewMore)
    CustomTextViewSemiBold tvViewMore;

    @BindView(R.id.cv_ok)
    CardView cvOk;

    @BindView(R.id.cv_back)
    CardView cvBack;

    @BindView(R.id.cv_share)
    CardView cvShare;

    @BindView(R.id.cv_enquiry)
    CardView cvEnquiry;

    @BindView(R.id.iv_fav)
    ImageView ivfav;

    @BindView(R.id.ll_payment)
    LinearLayout llPayment;

    @BindView(R.id.ll_cancel)
    LinearLayout llCancel;

    @BindView(R.id.ll_message)
    LinearLayout llMessage;

    @BindView(R.id.ll_reschedule)
    LinearLayout llReschedule;

    @BindView(R.id.ll_batch)
    LinearLayout llBatch;

    @BindView(R.id.ll_location)
    LinearLayout llLocation;

    @BindView(R.id.iv_ltIcon)
    ImageView ivLtIcon;

    private Context mContext;
    private Bookings bookingInfo = new Bookings();
    private boolean isActive = true;
    private ActiveAppointment apptInfo = new ActiveAppointment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);
        ButterKnife.bind(BookingDetails.this);
        mContext = BookingDetails.this;

        Intent i = getIntent();
        if (i != null) {
            bookingInfo = (Bookings) i.getSerializableExtra("bookingInfo");
            isActive = i.getBooleanExtra("isActive", true);

        }

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        llReschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(BookingDetails.this, RescheduleActivity.class);
                intent.putExtra("appointmentInfo", apptInfo);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume() {

        // Api call
        if (bookingInfo != null && bookingInfo.getAppointmentInfo() != null) {
            getAppointmentDetails(bookingInfo.getAppointmentInfo().getUid(), bookingInfo.getAppointmentInfo().getProviderAccount().getId());
        }
        super.onResume();
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
                        apptInfo = response.body();
                        updateUI(apptInfo);
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ActiveAppointment> call, Throwable t) {
            }
        });
    }


    private void updateUI(ActiveAppointment appointmentInfo) {

        try {

            if (appointmentInfo != null) {
                if (appointmentInfo.getProvider() != null) {

                    if (appointmentInfo.getProvider().getBusinessName() != null && !appointmentInfo.getProvider().getBusinessName().equalsIgnoreCase("")) {
                        tvDoctorName.setText(convertToTitleForm(appointmentInfo.getProvider().getBusinessName()));
                    } else {
                        String name = appointmentInfo.getProvider().getFirstName() + " " + appointmentInfo.getProvider().getLastName();
                        tvDoctorName.setText(convertToTitleForm(name));
                    }
                    tvProviderName.setVisibility(View.VISIBLE);
                    tvProviderName.setText(convertToTitleForm(appointmentInfo.getProviderAccount().getBusinessName()));
                } else {
                    tvProviderName.setVisibility(View.INVISIBLE);
                    tvDoctorName.setText(convertToTitleForm(appointmentInfo.getProviderAccount().getBusinessName()));

                }

                if (appointmentInfo.getService() != null) {
                    tvServiceName.setText(convertToTitleForm(appointmentInfo.getService().getName()));


                    if (appointmentInfo.getService().getServiceType() != null && appointmentInfo.getService().getServiceType().equalsIgnoreCase("virtualService")) {

                        if (appointmentInfo.getService().getVirtualCallingModes() != null) {
                            ivTeleService.setVisibility(View.VISIBLE);
                            if (appointmentInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                ivTeleService.setImageResource(R.drawable.zoom_meet);

                            } else if (appointmentInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                ivTeleService.setImageResource(R.drawable.google_meet);

                            } else if (appointmentInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                if (appointmentInfo.getService().getVirtualServiceType() != null && appointmentInfo.getService().getVirtualServiceType().equalsIgnoreCase("videoService")) {
                                    ivTeleService.setImageResource(R.drawable.whatsapp_videoicon);
                                } else {
                                    ivTeleService.setImageResource(R.drawable.whatsapp_icon);
                                }
                            } else if (appointmentInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                ivTeleService.setImageResource(R.drawable.phone_icon);
                            }
                        } else {
                            ivTeleService.setVisibility(View.GONE);
                        }
                    }

                }

                // to set confirmation number
                if (appointmentInfo.getAppointmentEncId() != null) {
                    tvConfirmationNumber.setText(appointmentInfo.getAppointmentEncId());
                }

                // to set status
                if (appointmentInfo.getApptStatus() != null) {
                    tvStatus.setVisibility(View.VISIBLE);
                    tvStatus.setText(convertToTitleForm(appointmentInfo.getApptStatus()));
                } else {
                    tvStatus.setVisibility(View.GONE);
                }


                // to set paid info
                if (appointmentInfo.getAmountPaid() != null && !appointmentInfo.getAmountPaid().equalsIgnoreCase("0.0")) {
                    llPayment.setVisibility(View.VISIBLE);
                    tvAmount.setText("₹" + " " + convertAmountToDecimals(appointmentInfo.getAmountPaid()) + " " + "PAID");
                } else {

                    llPayment.setVisibility(View.GONE);
                }

                // to set consumer name
                if (appointmentInfo.getAppmtFor() != null) {

                    if (appointmentInfo.getAppmtFor().get(0).getUserName() != null) {
                        tvConsumerName.setText(appointmentInfo.getAppmtFor().get(0).getUserName());
                    } else {
                        tvConsumerName.setText(appointmentInfo.getAppmtFor().get(0).getFirstName() + " " + appointmentInfo.getAppmtFor().get(0).getLastName());
                    }
                }

                // to set appointment date
                if (appointmentInfo.getAppmtDate() != null) {
                    tvDate.setText(getCustomDateString(appointmentInfo.getAppmtDate()));
                }

                // to set slot time
                if (appointmentInfo.getAppmtTime() != null) {

                    tvTime.setText(convertTime(appointmentInfo.getAppmtTime().split("-")[0]));
                }

                if (appointmentInfo.getBatchId() != null) {
                    llBatch.setVisibility(View.VISIBLE);
                    tvBatchNo.setText(appointmentInfo.getBatchId());
                } else {
                    llBatch.setVisibility(View.GONE);
                }

                // to set location
                if (appointmentInfo.getLocation() != null) {

                    if (appointmentInfo.getLocation().getPlace() != null) {

                        tvLocationName.setText(appointmentInfo.getLocation().getPlace());

                        tvLocationName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                openMapView(appointmentInfo.getLocation().getLattitude(), appointmentInfo.getLocation().getLongitude(), appointmentInfo.getLocation().getPlace());
                            }
                        });
                    }
                }

                if (isActive) {
                    llReschedule.setVisibility(View.VISIBLE);
                    llCancel.setVisibility(View.VISIBLE);

                    if (appointmentInfo.getService() != null) {

                        if (appointmentInfo.getService().getLivetrack().equalsIgnoreCase("true")) {
                            llLocation.setVisibility(View.VISIBLE);
                            if (appointmentInfo.getJaldeeApptDistanceTime() != null) {
                                Glide.with(BookingDetails.this).load(R.drawable.address).into(ivLtIcon);
                            } else {
                                ivLtIcon.setImageResource(R.drawable.location_off);
                                ivLtIcon.setColorFilter(ContextCompat.getColor(context, R.color.light_gray));

                            }
                        } else {
                            llLocation.setVisibility(View.GONE);
                        }
                    }


                } else {
                    llReschedule.setVisibility(View.GONE);
                    llCancel.setVisibility(View.GONE);
                    llLocation.setVisibility(View.GONE);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String convertToTitleForm(String name) {
        String convertName = name;
        convertName = convertName.substring(0, 1).toUpperCase() + convertName.substring(1).toLowerCase();
        return convertName;
    }

    public static String convertAmountToDecimals(String price) {

        double a = Double.parseDouble(price);
        DecimalFormat decim = new DecimalFormat("0.00");
        Double price2 = Double.parseDouble(decim.format(a));
        String amount = decim.format(price2);
        return amount;

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

    private void openMapView(String latitude, String longitude, String locationName) {
        Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + locationName);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        }
    }

}