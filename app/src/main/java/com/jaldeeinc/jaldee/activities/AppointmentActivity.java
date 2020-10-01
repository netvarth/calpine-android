package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.jaldeeinc.jaldee.Interface.ISlotInfo;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.TimeSlotsAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.LocationsDialog;
import com.jaldeeinc.jaldee.custom.SlotsDialog;
import com.jaldeeinc.jaldee.response.AvailableSlotsData;
import com.jaldeeinc.jaldee.response.ProfileModel;
import com.jaldeeinc.jaldee.response.SearchService;
import com.jaldeeinc.jaldee.response.SearchTerminology;
import com.jaldeeinc.jaldee.response.ServiceInfo;
import com.jaldeeinc.jaldee.response.SlotsData;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentActivity extends AppCompatActivity implements ISlotInfo {

    @BindView(R.id.tv_providerName)
    CustomTextViewSemiBold tvProviderName;

    @BindView(R.id.tv_serviceName)
    CustomTextViewBold tvServiceName;

    @BindView(R.id.tv_description)
    CustomTextViewMedium tvDescription;

    @BindView(R.id.tv_date)
    CustomTextViewBold tvDate;

    @BindView(R.id.tv_time)
    CustomTextViewBold tvTime;

    @BindView(R.id.tv_changeTime)
    CustomTextViewBold tvChangeTime;

    @BindView(R.id.ll_editDetails)
    LinearLayout llEditDetails;

    @BindView(R.id.tv_consumerName)
    CustomTextViewBold tvConsumerName;

    @BindView(R.id.tv_number)
    CustomTextViewMedium tvNumber;

    @BindView(R.id.tv_email)
    CustomTextViewMedium tvEmail;

    @BindView(R.id.cv_addNote)
    CardView cvAddNote;

    @BindView(R.id.cv_attachFile)
    CardView cvAttachFile;

    @BindView(R.id.cv_submit)
    CardView cvSubmit;

    @BindView(R.id.ll_appoint)
    LinearLayout llAppointment;

    @BindView(R.id.ll_checkIn)
    LinearLayout llCheckIn;

    @BindView(R.id.tv_checkInDate)
    CustomTextViewBold tvCheckInDate;

    @BindView(R.id.tv_peopleInLine)
    CustomTextViewBold tvPeopleInLine;

    @BindView(R.id.tv_hint)
    CustomTextViewSemiBold tvHint;

    String mFirstName, mLastName;
    int consumerID;
    private int uniqueId;
    private String providerName;
    private int providerId;
    private int locationId;
    private int serviceId;
    private String serviceName;
    private String phoneNumber;
    private String serviceDescription;
    private ServiceInfo serviceInfo = new ServiceInfo();
    SearchTerminology mSearchTerminology;
    ProfileModel profileDetails;
    ArrayList<SlotsData> slotsData = new ArrayList<SlotsData>();
    ArrayList<AvailableSlotsData> activeSlotsList = new ArrayList<>();
    private SlotsDialog slotsDialog;
    private ISlotInfo iSlotInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        ButterKnife.bind(AppointmentActivity.this);
        iSlotInfo = this;

        // getting necessary details from intent
        Intent intent = getIntent();
        uniqueId = intent.getIntExtra("uniqueID", 0);
        providerName = intent.getStringExtra("providerName");
        serviceInfo = (ServiceInfo) intent.getSerializableExtra("serviceInfo");
        locationId = intent.getIntExtra("locationId", locationId);
        providerId = intent.getIntExtra("providerId", 0);

        if (providerName != null) {
            tvProviderName.setText(providerName);
        }

        if (serviceInfo != null) {
            tvServiceName.setText(serviceInfo.getServiceName());
            tvDescription.setText(serviceInfo.getDescription());
            if (serviceInfo.getType().equalsIgnoreCase(Constants.APPOINTMENT)) {
                tvDate.setText(convertDate(serviceInfo.getAvailableDate()));
                if (serviceInfo.getTime() != null) {
                    tvTime.setText(convertTime(serviceInfo.getTime()));
                }
                llAppointment.setVisibility(View.VISIBLE);
                llCheckIn.setVisibility(View.GONE);
            } else if (serviceInfo.getType().equalsIgnoreCase(Constants.CHECKIN)) {
                llCheckIn.setVisibility(View.VISIBLE);
                llAppointment.setVisibility(View.GONE);
                String time = getWaitingTime(serviceInfo.getAvailableDate(), serviceInfo.getTime(), serviceInfo.getWaitingTime());
                tvCheckInDate.setText(time.split("-")[1]);
                tvHint.setText(time.split("-")[0]);

                if (serviceInfo.getPeopleWaitingInLine() >= 0) {
                    if (serviceInfo.getPeopleWaitingInLine() == 0) {
                        tvPeopleInLine.setText("Be the first in line");
                    } else if (serviceInfo.getPeopleWaitingInLine() == 1) {
                        tvPeopleInLine.setText(serviceInfo.getPeopleWaitingInLine() + "  person waiting in line");
                    } else {
                        tvPeopleInLine.setText(serviceInfo.getPeopleWaitingInLine() + "  people waiting in line");
                    }
                }
            }
        }

        mFirstName = SharedPreference.getInstance(AppointmentActivity.this).getStringValue("firstname", "");
        mLastName = SharedPreference.getInstance(AppointmentActivity.this).getStringValue("lastname", "");
        consumerID = SharedPreference.getInstance(AppointmentActivity.this).getIntValue("consumerId", 0);

        // api calls
        ApiSearchViewTerminology(uniqueId);
        ApiGetProfileDetail();

        // click actions

        llEditDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent familyIntent = new Intent(AppointmentActivity.this, CheckinFamilyMemberAppointment.class);
                startActivity(familyIntent);

            }
        });

        tvChangeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (serviceInfo.getAvailableDate() != null) {
                    slotsDialog = new SlotsDialog(AppointmentActivity.this, serviceInfo.getServiceId(), locationId, iSlotInfo, providerId, serviceInfo.getAvailableDate());
                    slotsDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                    slotsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    slotsDialog.show();
                    DisplayMetrics metrics = AppointmentActivity.this.getResources().getDisplayMetrics();
                    int width = (int) (metrics.widthPixels * 1);
                    slotsDialog.setCancelable(false);
                    slotsDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                }

            }
        });

    }


    private void ApiSearchViewTerminology(int muniqueID) {


        ApiInterface apiService =
                ApiClient.getClientS3Cloud(AppointmentActivity.this).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(AppointmentActivity.this, AppointmentActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));
        Call<SearchTerminology> call = apiService.getSearchViewTerminology(muniqueID, sdf.format(currentTime));

        call.enqueue(new Callback<SearchTerminology>() {
            @Override
            public void onResponse(Call<SearchTerminology> call, Response<SearchTerminology> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        mSearchTerminology = response.body();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<SearchTerminology> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);

            }
        });
    }

    private void ApiGetProfileDetail() {

        ApiInterface apiService =
                ApiClient.getClient(AppointmentActivity.this).create(ApiInterface.class);

        final int consumerId = SharedPreference.getInstance(AppointmentActivity.this).getIntValue("consumerId", 0);

        final Dialog mDialog = Config.getProgressDialog(AppointmentActivity.this, AppointmentActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ProfileModel> call = apiService.getProfileDetail(consumerId);

        call.enqueue(new Callback<ProfileModel>() {
            @Override
            public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        profileDetails = response.body();
                        if (profileDetails != null) {
                            tvNumber.setText(profileDetails.getUserprofile().getPrimaryMobileNo());
                            phoneNumber = tvNumber.getText().toString();
                            if (profileDetails.getUserprofile().getEmail() != null) {
                                tvEmail.setText(profileDetails.getUserprofile().getEmail());
                            } else {
                                tvEmail.setHint("Enter your Mail Id");
                            }
                        }
                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(Call<ProfileModel> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });


    }

//    private void getSlotsOnDate(int serviceId, int mSpinnertext, String selectDate, String modifyAccountID) {
//
//        ApiInterface apiService =
//                ApiClient.getClient(AppointmentActivity.this).create(ApiInterface.class);
//
//        final Dialog mDialog = Config.getProgressDialog(AppointmentActivity.this, AppointmentActivity.this.getResources().getString(R.string.dialog_log_in));
//        mDialog.show();
//        Call<ArrayList<SlotsData>> call = apiService.getSlotsOnDate(selectDate, serviceId, mSpinnertext, Integer.parseInt(modifyAccountID));
//
//        call.enqueue(new Callback<ArrayList<SlotsData>>() {
//            @Override
//            public void onResponse(Call<ArrayList<SlotsData>> call, Response<ArrayList<SlotsData>> response) {
//                try {
//                    if (mDialog.isShowing())
//                        Config.closeDialog(getParent(), mDialog);
//                    if (response.code() == 200) {
//
//                        if (response.body() != null) {
//                            slotsData = response.body();
//
//                            activeSlotsList.clear();
//                            for (int i = 0; i < slotsData.size(); i++) {
//                                ArrayList<AvailableSlotsData> availableSlotsList = new ArrayList<>();
//                                availableSlotsList = slotsData.get(i).getAvailableSlots();
//
//                                for (int j = 0; j < availableSlotsList.size(); j++) {
//                                    if (availableSlotsList.get(j).getNoOfAvailableSlots() != 0 && availableSlotsList.get(j).isActive()) {
//
//                                        availableSlotsList.get(j).setScheduleId(slotsData.get(i).getScheduleId());
//                                        String displayTime = getDisplayTime(availableSlotsList.get(j).getSlotTime());
//                                        availableSlotsList.get(j).setDisplayTime(displayTime);
//                                        activeSlotsList.add(availableSlotsList.get(j));
//                                    }
//                                }
//                            }
//
//                            if (activeSlotsList != null) {
//                                if (activeSlotsList.size() > 0) {
//
//                                    selectedShcdId = activeSlotsList.get(0).getScheduleId();
//                                    txtnocheckin.setVisibility(View.GONE);
//                                    earliestAvailable.setText("Earliest available\n" + activeSlotsList.get(0).getDisplayTime());
//                                    llCoupons.setVisibility(View.VISIBLE);
//                                    btn_checkin.setVisibility(View.VISIBLE);
//                                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(AppointmentActivity.this, 3);
//                                    rvSlots.setLayoutManager(mLayoutManager);
//                                    sAdapter = new TimeSlotsAdapter(activeSlotsList, iSelectSlotInterface);
//                                    rvSlots.setAdapter(sAdapter);
//                                } else {
//                                    cvSlots.setVisibility(View.GONE);
//                                    llCoupons.setVisibility(View.GONE);
//                                    btn_checkin.setVisibility(View.GONE);
//                                    txtnocheckin.setVisibility(View.VISIBLE);
//                                    txtnocheckin.setText("Appointment for this service is not available at the moment. Please try for a different date");
//                                    earliestAvailable.setText("Timeslots not available");
//                                }
//                            }
//                        }
//
//                    } else {
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ArrayList<SlotsData>> call, Throwable t) {
//                // Log error here since request failed
//                Config.logV("Fail---------------" + t.toString());
//                if (mDialog.isShowing())
//                    Config.closeDialog(getParent(), mDialog);
//            }
//        });
//    }


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
            firstWord = "Est wait time";
            secondWord = Config.getTimeinHourMinutes(Integer.parseInt(estTime));
        }
        // Spannable spannable = new SpannableString(firstWord + secondWord);
//        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),"fonts/Montserrat_Bold.otf");
//        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.title_grey)), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return firstWord + "-" + secondWord;
    }

    private String getDisplayTime(String slotTime) {

        String displayTime = slotTime.split("-")[0];
        String sTime = "";

        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(displayTime);
            SimpleDateFormat time = new SimpleDateFormat("K:mm aa");
            sTime = time.format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return sTime;
    }

    @Override
    public void sendSlotInfo(String time, String displayTime, int scheduleId) {

        tvTime.setText(displayTime);
    }
}