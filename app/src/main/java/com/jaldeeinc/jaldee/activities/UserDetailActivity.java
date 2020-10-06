package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.Interface.ISelectedProviderService;
import com.jaldeeinc.jaldee.Interface.ISelectedService;
import com.jaldeeinc.jaldee.Interface.ISendMessage;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.MainServicesAdapter;
import com.jaldeeinc.jaldee.adapter.ServicesAdapter;
import com.jaldeeinc.jaldee.adapter.SpecialisationAdapter;
import com.jaldeeinc.jaldee.adapter.UserServicesAdapter;
import com.jaldeeinc.jaldee.adapter.VirtualFieldAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CircleTransform;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.EnquiryDialog;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;
import com.jaldeeinc.jaldee.custom.ResizableCustomView;
import com.jaldeeinc.jaldee.model.ProviderUserModel;
import com.jaldeeinc.jaldee.response.DepServiceInfo;
import com.jaldeeinc.jaldee.response.DepartmentInfo;
import com.jaldeeinc.jaldee.response.ProfilePicture;
import com.jaldeeinc.jaldee.response.QueueList;
import com.jaldeeinc.jaldee.response.ScheduleList;
import com.jaldeeinc.jaldee.response.SearchAppoinment;
import com.jaldeeinc.jaldee.response.SearchDonation;
import com.jaldeeinc.jaldee.response.SearchLocation;
import com.jaldeeinc.jaldee.response.SearchService;
import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.jaldeeinc.jaldee.response.SearchVirtualFields;
import com.jaldeeinc.jaldee.response.ServiceInfo;
import com.jaldeeinc.jaldee.widgets.CustomDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jaldeeinc.jaldee.connection.ApiClient.context;

public class UserDetailActivity extends AppCompatActivity implements ISelectedProviderService, ISendMessage {

    @BindView(R.id.tv_spName)
    CustomTextViewBold tvSpName;

    @BindView(R.id.iv_verified)
    ImageView ivVerified;

    @BindView(R.id.tv_spSpecialization)
    CustomTextViewSemiBold tvSpeciality;

    @BindView(R.id.tv_locationName)
    CustomTextViewMedium tvLocationName;


    @BindView(R.id.ll_specializations)
    LinearLayout llSpecializations;

    @BindView(R.id.tv_spOne)
    CustomTextViewMedium tvSpOne;

    @BindView(R.id.tv_spTwo)
    CustomTextViewMedium tvSpTwo;

    @BindView(R.id.txtSeeAll)
    CustomTextViewMedium tvSeeAll;

    @BindView(R.id.iv_image)
    ImageView ivSpImage;

    @BindView(R.id.tv_viewGallery)
    CustomTextViewSemiBold tvViewGallery;

    @BindView(R.id.rb_ratingBar)
    RatingBar ratingBar;

    @BindView(R.id.tv_moreInfo)
    CustomTextViewMedium tvMoreInfo;

    @BindView(R.id.tv_about)
    CustomTextViewMedium tvAbout;

    @BindView(R.id.mImageViewTextnew)
    TextView tv_mImageViewTextnew;

    @BindView(R.id.txtMoredetails)
    CustomTextViewMedium tvMoreDetails;

    @BindView(R.id.ll_more)
    LinearLayout llMore;

    @BindView(R.id.cv_back)
    CardView cvBack;

    @BindView(R.id.cv_enquiry)
    CardView cvEnquiry;

    @BindView(R.id.ll_noSlots)
    LinearLayout llNoSlots;

    private Context mContext;
    private boolean onlinePresence;
    String claimable;
    private int uniqueId;
    private int providerId;
    private int locationId;
    private int userId;
    private String locationName;
    boolean flag_more = false;
    private boolean isToken;
    private RecyclerView rvServices, mRecycle_virtualfield;
    private GridLayoutManager gridLayoutManager;
    private UserServicesAdapter userServicesAdapter;
    ArrayList<DepServiceInfo> serviceInfoList = new ArrayList<DepServiceInfo>();
    private ISelectedProviderService iSelectedService;
    private SearchViewDetail providerDetails = new SearchViewDetail();
    private ProviderUserModel providerInfo = new ProviderUserModel();
    VirtualFieldAdapter mAdapter;
    SpecialisationAdapter sAdapter;
    SearchVirtualFields resultData;
    ArrayList<SearchVirtualFields> domainVirtual = new ArrayList<>();
    ArrayList<SearchVirtualFields> sub_domainVirtual = new ArrayList<>();
    private ISendMessage iSendMessage;
    private EnquiryDialog enquiryDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        ButterKnife.bind(UserDetailActivity.this);
        mContext = UserDetailActivity.this;
        iSelectedService = this;
        iSendMessage = this;

        Intent intent = getIntent();
        if (intent != null) {
            uniqueId = intent.getIntExtra("uniqueID", 0);
            claimable = intent.getStringExtra("claimable");
            claimable = "0";
            locationId = intent.getIntExtra("locationId", 0);
            providerInfo = (ProviderUserModel) intent.getSerializableExtra("providerInfo");
            locationName = intent.getStringExtra("locationName");
            isToken = intent.getBooleanExtra("isToken", false);
        }

        if (providerInfo != null) {
            providerId = providerInfo.getId();
            if (providerInfo.getProfilePicture() != null) {
                String url = extractUrl(providerInfo.getProfilePicture());
                PicassoTrustAll.getInstance(mContext).load(url).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(ivSpImage);

            } else {
                ivSpImage.setImageResource(R.drawable.icon_noimage);
            }

        }


        //more details recyclerView
        mRecycle_virtualfield = findViewById(R.id.mrecycle_virtualfield);

        rvServices = findViewById(R.id.rv_services);
        gridLayoutManager = new GridLayoutManager(this, 2);
        rvServices.setLayoutManager(gridLayoutManager);
        userServicesAdapter = new UserServicesAdapter(serviceInfoList, this, true, iSelectedService);
        rvServices.setAdapter(userServicesAdapter);

        // get provider details
        getProviderBusinessProfile(uniqueId, providerId, locationId);


        tvMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (llMore.getVisibility() != View.VISIBLE) {
                    llMore.setVisibility(View.VISIBLE);
                    int size = domainVirtual.size();
                    if (size > 2) {
                        tvMoreDetails.setVisibility(View.VISIBLE);
                    } else {
                        tvMoreDetails.setVisibility(View.GONE);
                    }
                    if (size > 0) {
                        llMore.setVisibility(View.VISIBLE);
                    } else {
                        llMore.setVisibility(View.GONE);
                    }
                } else {
                    llMore.setVisibility(View.GONE);
                    tvMoreDetails.setVisibility(View.GONE);
                }

            }
        });

        tvMoreDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!flag_more) {
                    flag_more = true;
                    mRecycle_virtualfield.setVisibility(View.VISIBLE);
                    Config.logV("Domain Size@@@@@@@@@@@@@" + domainVirtual.size());
                    Config.logV("Subdomain Size@@@@@@@@@@@@@" + sub_domainVirtual.size());
                    tvMoreDetails.setText("See Less");
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                    mRecycle_virtualfield.setLayoutManager(mLayoutManager);
                    mAdapter = new VirtualFieldAdapter(domainVirtual, mContext, domainVirtual.size());
                    mRecycle_virtualfield.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                } else {
                    flag_more = false;
                    tvMoreDetails.setText("See All");
                    int size = domainVirtual.size();
                    if (size == 1) {
                        size = 1;
                    } else {
                        size = 2;
                    }
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                    mRecycle_virtualfield.setLayoutManager(mLayoutManager);
                    mAdapter = new VirtualFieldAdapter(domainVirtual, mContext, size);
                    mRecycle_virtualfield.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        cvEnquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (providerDetails != null && providerId != 0) {
                    enquiryDialog = new EnquiryDialog(mContext, providerDetails.getBusinessName(), iSendMessage, providerDetails.getId());
                    enquiryDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                    enquiryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    enquiryDialog.show();
                    DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                    int width = (int) (metrics.widthPixels * 1);
                    enquiryDialog.setCancelable(false);
                    enquiryDialog.getWindow().setGravity(Gravity.BOTTOM);
                    enquiryDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                } else {
                    Toast.makeText(UserDetailActivity.this, "Please try after some time", Toast.LENGTH_SHORT).show();
                }
            }
        });

        apiVirtualFields(uniqueId, providerId);

    }

    private void apiVirtualFields(int muniqueID, int provId) {
        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));
        Call<SearchVirtualFields> call = apiService.getProviderVirtualFields(muniqueID, provId, sdf.format(currentTime));
        call.enqueue(new Callback<SearchVirtualFields>() {
            @Override
            public void onResponse(Call<SearchVirtualFields> call, Response<SearchVirtualFields> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(UserDetailActivity.this, mDialog);
                    Config.logV("URL----VIRTUAL---8888--------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------VIRTUAL-------------------" + response.code());
                    if (response.code() == 200) {
                        resultData = response.body();
                        if (resultData != null) {
                            domainVirtual.clear();
                            domainVirtual = resultData.getDomain();
                            sub_domainVirtual.clear();
                            sub_domainVirtual = resultData.getSubdomain();
                            domainVirtual.addAll(sub_domainVirtual);

                            if (domainVirtual.size() > 0) {
                                tvMoreInfo.setVisibility(View.VISIBLE);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                                mRecycle_virtualfield.setLayoutManager(mLayoutManager);
                                int size = domainVirtual.size();
                                if (size == 1) {
                                    size = 1;
                                } else {
                                    size = 2;
                                }
                                mAdapter = new VirtualFieldAdapter(domainVirtual, mContext, size);
                                mRecycle_virtualfield.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged();
                            } else {
                                tvMoreDetails.setVisibility(View.VISIBLE);
                                tvMoreInfo.setVisibility(View.GONE);
                            }
                        } else {
                            tvMoreDetails.setVisibility(View.GONE);
                            llMore.setVisibility(View.GONE);
                        }
                    } else {
                        llMore.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SearchVirtualFields> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(UserDetailActivity.this, mDialog);
            }
        });
    }


    private void getProviderBusinessProfile(int uniqueId, int providerId, int locId) {

        ApiInterface apiService = ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));
        Call<SearchViewDetail> call = apiService.getUserBusinessProfile(uniqueId, providerId, sdf.format(currentTime));
        call.enqueue(new Callback<SearchViewDetail>() {
            @Override
            public void onResponse(Call<SearchViewDetail> call, final Response<SearchViewDetail> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(UserDetailActivity.this, mDialog);
                    Config.logV("URL-----1111----------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-----detail--------------------" + response.code());
                    if (response.code() == 200) {
                        providerDetails = response.body();

                        if (providerDetails != null) {

                            userId = providerDetails.getId();
                            onlinePresence = providerDetails.isOnlinePresence();
                            UpdateMainUI(providerDetails);
                            apiGetProviders(uniqueId, providerId, locId,userId);

                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SearchViewDetail> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(UserDetailActivity.this, mDialog);
            }
        });

    }

    private void UpdateMainUI(SearchViewDetail mBusinessDataList) {


        if (mBusinessDataList != null) {

            // Service provider name
            if (mBusinessDataList.getBusinessName() != null) {
                String name = mBusinessDataList.getBusinessName();
                name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                tvSpName.setText(name);
            }

            if (mBusinessDataList.getVerifyLevel() != null) {
                if (!mBusinessDataList.getVerifyLevel().equalsIgnoreCase("NONE")) {
                    ivVerified.setVisibility(View.VISIBLE);
                    if (mBusinessDataList.getVerifyLevel().equalsIgnoreCase("BASIC_PLUS")) {
                        ivVerified.setImageResource(R.drawable.jaldee_basicplus);
                    }
                    if (mBusinessDataList.getVerifyLevel().equalsIgnoreCase("BASIC")) {
                        ivVerified.setImageResource(R.drawable.jaldee_basic);
                    }
                    if (mBusinessDataList.getVerifyLevel().equalsIgnoreCase("PREMIUM") || mBusinessDataList.getVerifyLevel().equalsIgnoreCase("ADVANCED")) {
                        ivVerified.setImageResource(R.drawable.jaldee_adv);
                    }
                } else {
                    ivVerified.setVisibility(View.GONE);
                }

                ivVerified.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String ynw_verified = null;
                        if (mBusinessDataList.getVerifyLevel().equalsIgnoreCase("BASIC"))
                            ynw_verified = "2";
                        else if (mBusinessDataList.getVerifyLevel().equalsIgnoreCase("BASIC_PLUS"))
                            ynw_verified = "3";
                        else if (mBusinessDataList.getVerifyLevel().equalsIgnoreCase("PREMIUM") || mBusinessDataList.getVerifyLevel().equalsIgnoreCase("ADVANCED"))
                            ynw_verified = "4";
                        Config.logV("YNW VERIFIED@@@@@@@@@@@@" + ynw_verified);
                        CustomDialog cdd = new CustomDialog(mContext, ynw_verified, mBusinessDataList.getBusinessName());
                        cdd.setCanceledOnTouchOutside(true);
                        cdd.show();
                    }
                });
            } else {
                ivVerified.setVisibility(View.GONE);
            }

            // rating
            try {
                int rate = Math.round(mBusinessDataList.getAvgRating());
                if (rate < 4) {
                    ratingBar.setVisibility(View.GONE);
                } else {
                    ratingBar.setVisibility(View.VISIBLE);
                    ratingBar.setRating(mBusinessDataList.getAvgRating());
                }

            } catch (Exception e) {

            }

            // sud domain name
            if (mBusinessDataList.getServiceSector().getDisplayName() != null && mBusinessDataList.getServiceSubSector().getDisplayName() != null) {
                if (mBusinessDataList.getServiceSector().getDisplayName().equalsIgnoreCase("Other / Miscellaneous")) {
                    tvSpeciality.setVisibility(View.GONE);
                } else {
                    // tv_domain.setText(getBussinessData.getServiceSector().getDisplayName()); //+ " " + "(" + getBussinessData.getServiceSubSector().getDisplayName() + ")");
                    tvSpeciality.setVisibility(View.VISIBLE);
                    tvSpeciality.setText(mBusinessDataList.getServiceSubSector().getDisplayName());
                }
            }

            // location
            if (locationName != null) {

                tvLocationName.setText(locationName);
            }

            // About
            if (mBusinessDataList.getBusinessDesc() != null) {
                tvAbout.setVisibility(View.VISIBLE);
                tvAbout.setText(mBusinessDataList.getBusinessDesc());
                tvAbout.post(new Runnable() {
                    @Override
                    public void run() {
                        int lineCount = tvAbout.getLineCount();
                        //Config.logV("No of line---------------" + lineCount + "Name" + inboxList.getUserName());
                        if (lineCount > 2) {
                            ResizableCustomView.doResizeTextView(UserDetailActivity.this, tvAbout, 2, "..more", true);
                        } else {
                        }
                        // Use lineCount here
                    }
                });
            } else {
                tvAbout.setVisibility(View.GONE);
            }


            try {
                mBusinessDataList.getSpecialization().removeAll(Collections.singleton(null));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mBusinessDataList.getSpecialization() != null) {
                for (int i = 0; i < mBusinessDataList.getSpecialization().size(); i++) {
                    if (mBusinessDataList.getSpecialization().get(i).equals("Not Applicabale") || mBusinessDataList.getSpecialization().get(i).equals("Not Applicable")) {
                        mBusinessDataList.getSpecialization().remove(i);
                    }
                }

                if (mBusinessDataList.getSpecialization().size() > 0) {

                    ArrayList<String> specNames = new ArrayList<>();
                    String name;
                    for (int i = 0; i < mBusinessDataList.getSpecialization().size(); i++) {
                        name = mBusinessDataList.getSpecialization().get(i).toString();
                        specNames.add(name);
                    }

                    StringBuilder builder = new StringBuilder();
                    for (String details : specNames) {
                        builder.append(details + ", ");
                    }
                    String finalContent = builder.toString();
                    finalContent = finalContent.replaceAll(", $", ".");

                    tvSpOne.setText(finalContent);

                    tvSpOne.post(new Runnable() {
                        @Override
                        public void run() {
                            int lineCount = tvSpOne.getLineCount();
                            //Config.logV("No of line---------------" + lineCount + "Name" + inboxList.getUserName());
                            if (lineCount > 3) {
                                ResizableCustomView.doResizeTextView(mContext, tvSpOne, 3, "View more", true);
                            } else {
                            }
                            // Use lineCount here
                        }
                    });
                }


            }
        }
    }

    private void apiGetProviders(int unqId, int provid, int locid, int userId) {

        try {

            ApiInterface apiService =
                    ApiClient.getClient(mContext).create(ApiInterface.class);
            Date currentTime = new Date();
            final SimpleDateFormat sdf = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));


            List<Observable<?>> requests = new ArrayList<>();

            // Make a collection of all requests you need to call at once, there can be any number of requests, not only 3. You can have 2 or 5, or 100.
            requests.add(apiService.getProviderCheckInSchedule(provid + "-" + locid));
            requests.add(apiService.getAppointmentSchedule(userId + "-" + locid+"-"+provid));
            requests.add(apiService.getCheckInServices(locid));
            requests.add(apiService.getAppointmentServices(locid));

            // Zip all requests with the Function, which will receive the results.
            Observable.zip(requests, new Function<Object[], Object>() {
                @Override
                public Object apply(Object[] objects) throws Exception {
                    // Objects[] is an array of combined results of completed requests
                    ArrayList<QueueList> queueList = (ArrayList<QueueList>) objects[0];
                    ArrayList<ScheduleList> schedulesList = (ArrayList<ScheduleList>) objects[1];
                    ArrayList<SearchService> checkInServList = (ArrayList<SearchService>) objects[2];
                    ArrayList<SearchAppoinment> apptServicesList = (ArrayList<SearchAppoinment>) objects[3];

                    queueList = queueList == null ? new ArrayList<QueueList>() : queueList;

                    schedulesList = schedulesList == null ? new ArrayList<ScheduleList>() : schedulesList;

                    checkInServList = checkInServList == null ? new ArrayList<SearchService>() : checkInServList;

                    apptServicesList = apptServicesList == null ? new ArrayList<SearchAppoinment>() : apptServicesList;

                    ArrayList<DepServiceInfo> services = new ArrayList<DepServiceInfo>();

                    serviceInfoList.clear();

                    if (queueList.get(0).isWaitlistEnabled()) {
                        for (SearchService checkInService : checkInServList) {

                            if (checkInService.getProvider() != null) {// adding only Sp level services

                                if (checkInService.getProvider().getId() == provid) { // add only particular provider services

                                    DepServiceInfo serviceInfo = new DepServiceInfo();
                                    serviceInfo.setDepartmentId(0);
                                    serviceInfo.setDepartmentName("");
                                    serviceInfo.setId(checkInService.getId());
                                    serviceInfo.setName(checkInService.getName());
                                    serviceInfo.setType(Constants.CHECKIN);
                                    serviceInfo.setToken(isToken);
                                    serviceInfo.setOnline(onlinePresence);
                                    if (checkInService.getCheckInServiceAvailability() != null) {
                                        serviceInfo.setAvailability(true);
                                        if (checkInService.getCheckInServiceAvailability().getQueueWaitingTime() != null) {
                                            serviceInfo.setEstTime(checkInService.getCheckInServiceAvailability().getQueueWaitingTime());
                                        }
                                        serviceInfo.setPeopleInLine(checkInService.getCheckInServiceAvailability().getPersonAhead());
                                        serviceInfo.setCalculationMode(checkInService.getCheckInServiceAvailability().getCalculationMode());
                                        serviceInfo.setNextAvailableDate(checkInService.getCheckInServiceAvailability().getAvailableDate());
                                        if (checkInService.getCheckInServiceAvailability().getServiceTime() != null) {
                                            serviceInfo.setNextAvailableTime(checkInService.getCheckInServiceAvailability().getServiceTime());
                                        }
                                    }
                                    serviceInfo.setServiceMode(checkInService.getServiceType());
                                    if (checkInService.getVirtualCallingModes() != null) {
                                        serviceInfo.setCallingMode(checkInService.getVirtualCallingModes().get(0).getCallingMode());
                                    }
                                    // adding all the info
                                    serviceInfo.setChecinServiceInfo(checkInService);
                                    services.add(serviceInfo);
                                }
                            }
                        }
                    }

                    if (schedulesList.get(0).isApptEnabled()) {
                        for (SearchAppoinment appt : apptServicesList) {

                            if (appt.getProvider() != null) {  // adding only Sp level services

                                if (appt.getProvider().getId() == provid) {  // add only particular provider services

                                    DepServiceInfo serviceInfo = new DepServiceInfo();
                                    serviceInfo.setDepartmentId(0);
                                    serviceInfo.setDepartmentName("");
                                    serviceInfo.setId(appt.getId());
                                    serviceInfo.setName(appt.getName());
                                    serviceInfo.setType(Constants.APPOINTMENT);
                                    serviceInfo.setEstTime("");
                                    serviceInfo.setPeopleInLine(0);
                                    serviceInfo.setOnline(onlinePresence);
                                    serviceInfo.setCalculationMode("");
                                    serviceInfo.setServiceMode(appt.getServiceType());
                                    if (appt.getVirtualCallingModes() != null) {
                                        serviceInfo.setCallingMode(appt.getVirtualCallingModes().get(0).getCallingMode());
                                    }
                                    if (appt.getAppointServiceAvailability() != null) {
                                        serviceInfo.setAvailability(true);
                                        serviceInfo.setNextAvailableDate(appt.getAppointServiceAvailability().getNextAvailableDate());
                                        serviceInfo.setNextAvailableTime(appt.getAppointServiceAvailability().getNextAvailable().split("-")[0]);

                                    }
                                    // adding all the info
                                    serviceInfo.setAppointmentServiceInfo(appt);
                                    services.add(serviceInfo);
                                }
                            }

                        }
                    }

                    serviceInfoList.addAll(services);
                    //do something with those results and emit new event
                    return serviceInfoList;
                }
            })
                    // After all requests had been performed the next observer will receive the Object, returned from Function

                    .subscribe(
                            // Will be triggered if all requests will end successfully (4xx and 5xx also are successful requests too)
                            new Consumer<Object>() {
                                @Override
                                public void accept(Object object) throws Exception {
                                    //Do something on successful completion of all requests
                                    ArrayList<DepServiceInfo> servicesInfoList = (ArrayList<DepServiceInfo>) object;

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            if (servicesInfoList.size() > 0) {

                                                llNoSlots.setVisibility(View.GONE);
                                                rvServices.setVisibility(View.VISIBLE);
                                                gridLayoutManager = new GridLayoutManager(UserDetailActivity.this, 2);
                                                rvServices.setLayoutManager(gridLayoutManager);
                                                userServicesAdapter = new UserServicesAdapter(servicesInfoList, UserDetailActivity.this, false, iSelectedService);
                                                rvServices.setAdapter(userServicesAdapter);
                                            } else {

                                                llNoSlots.setVisibility(View.VISIBLE);
                                                rvServices.setVisibility(View.GONE);
                                            }

                                        }
                                    });
                                }
                            },

                            // Will be triggered if any error during requests will happen
                            new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable e) throws Exception {
                                    Log.e("ListOf Calls", "1");

                                    //Do something on error completion of requests
                                }
                            }
                    );

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void openMapView(String latitude, String longitude, String locationName) {
        Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + locationName);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        }
    }

    public static String extractUrl(String jsonString) {
        Gson g = new Gson();
        ProfilePicture p = g.fromJson(jsonString, ProfilePicture.class);
        String url = p.getUrl();
        return url;
    }

    // service item click action
    @Override
    public void onCheckInSelected(SearchService checkinServiceInfo) {

        if (checkinServiceInfo != null) {
            Intent intent = new Intent(UserDetailActivity.this, CheckInActivity.class);
            intent.putExtra("uniqueID", uniqueId);
            intent.putExtra("providerName", tvSpName.getText().toString());
            intent.putExtra("providerId", providerId);
            intent.putExtra("locationId", locationId);
            intent.putExtra("checkInInfo", checkinServiceInfo);
            intent.putExtra("userId", userId);
            intent.putExtra("fromUser", true);
            startActivity(intent);
        }
    }

    @Override
    public void onAppointmentSelected(SearchAppoinment appointmentServiceInfo) {

        if (appointmentServiceInfo != null) {
            Intent intent = new Intent(UserDetailActivity.this, AppointmentActivity.class);
            intent.putExtra("uniqueID", uniqueId);
            intent.putExtra("providerName", tvSpName.getText().toString());
            intent.putExtra("locationId", locationId);
            intent.putExtra("providerId", providerId);
            intent.putExtra("userId", userId);
            intent.putExtra("fromUser", true);
            ServiceInfo serviceInfo = new ServiceInfo();
            serviceInfo.setServiceId(appointmentServiceInfo.getId());
            serviceInfo.setServiceName(appointmentServiceInfo.getName());
            serviceInfo.setDescription(appointmentServiceInfo.getDescription());
            serviceInfo.setType(Constants.APPOINTMENT);
            serviceInfo.setUser(true);
            if (appointmentServiceInfo.getConsumerNoteTitle() != null) {
                serviceInfo.setConsumerNoteTitle(appointmentServiceInfo.getConsumerNoteTitle());
            }
            serviceInfo.setIsPrePayment(appointmentServiceInfo.getIsPrePayment());
            serviceInfo.setLivetrack(appointmentServiceInfo.getLivetrack());
            if (appointmentServiceInfo.getMinPrePaymentAmount() != null) {
                serviceInfo.setMinPrePaymentAmount(appointmentServiceInfo.getMinPrePaymentAmount());
            }
            serviceInfo.setPreInfoEnabled(appointmentServiceInfo.isPreInfoEnabled());
            if (appointmentServiceInfo.getPreInfoText() != null) {
                serviceInfo.setPreInfoText(appointmentServiceInfo.getPreInfoText());
            }
            if (appointmentServiceInfo.getServiceType() != null) {
                serviceInfo.setServiceType(appointmentServiceInfo.getServiceType());
            }
            if (appointmentServiceInfo.getVirtualCallingModes() != null) {
                serviceInfo.setCallingMode(appointmentServiceInfo.getVirtualCallingModes().get(0).getCallingMode());
                serviceInfo.setVirtualCallingValue(appointmentServiceInfo.getVirtualCallingModes().get(0).getValue());
            }
            if (appointmentServiceInfo.getAppointServiceAvailability() != null) {
                serviceInfo.setScheduleId(appointmentServiceInfo.getAppointServiceAvailability().getId());
                serviceInfo.setAvailableDate(appointmentServiceInfo.getAppointServiceAvailability().getNextAvailableDate());
                serviceInfo.setTime(appointmentServiceInfo.getAppointServiceAvailability().getNextAvailable());
            }

            intent.putExtra("serviceInfo", serviceInfo);

            startActivity(intent);
        }

    }
}