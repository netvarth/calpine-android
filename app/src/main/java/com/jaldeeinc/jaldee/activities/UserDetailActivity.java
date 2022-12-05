package com.jaldeeinc.jaldee.activities;

import static com.jaldeeinc.jaldee.connection.ApiClient.context;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jaldeeinc.jaldee.Interface.ISelectedProviderService;
import com.jaldeeinc.jaldee.Interface.ISendMessage;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.SpecialisationAdapter;
import com.jaldeeinc.jaldee.adapter.UserServicesAdapter;
import com.jaldeeinc.jaldee.adapter.VirtualFieldAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.EnquiryDialog;
import com.jaldeeinc.jaldee.custom.ResizableCustomView;
import com.jaldeeinc.jaldee.model.BookingModel;
import com.jaldeeinc.jaldee.model.ProviderUserModel;
import com.jaldeeinc.jaldee.response.DepServiceInfo;
import com.jaldeeinc.jaldee.response.ProfileModel;
import com.jaldeeinc.jaldee.response.ProfilePicture;
import com.jaldeeinc.jaldee.response.Provider;
import com.jaldeeinc.jaldee.response.QueueList;
import com.jaldeeinc.jaldee.response.ScheduleList;
import com.jaldeeinc.jaldee.response.SearchAppoinment;
import com.jaldeeinc.jaldee.response.SearchService;
import com.jaldeeinc.jaldee.response.SearchTerminology;
import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.jaldeeinc.jaldee.response.SearchVirtualFields;
import com.jaldeeinc.jaldee.response.ServiceInfo;
import com.jaldeeinc.jaldee.response.UserResponse;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.jaldeeinc.jaldee.widgets.CustomDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetailActivity extends AppCompatActivity implements ISelectedProviderService, ISendMessage {

    @BindView(R.id.tv_spName)
    CustomTextViewBold tvSpName;

    @BindView(R.id.iv_verified)
    ImageView ivVerified;

    @BindView(R.id.tv_spSpecialization)
    CustomTextViewMedium tvSpeciality;

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

    @BindView(R.id.rb_ratingBar)
    RatingBar ratingBar;

    @BindView(R.id.tv_moreInfo)
    CustomTextViewMedium tvMoreInfo;

    @BindView(R.id.tv_about)
    CustomTextViewMedium tvAbout;

    @BindView(R.id.tv_subDomainHint)
    CustomTextViewMedium tvSubDomainHint;

    @BindView(R.id.mImageViewTextnew)
    TextView tv_mImageViewTextnew;

    @BindView(R.id.ll_more)
    LinearLayout llMore;

    @BindView(R.id.cv_back)
    CardView cvBack;

    @BindView(R.id.cv_enquiry)
    CardView cvEnquiry;

    @BindView(R.id.ll_noSlots)
    LinearLayout llNoSlots;

    @BindView(R.id.tv_providerName)
    CustomTextViewBold tvProviderName;

    private Context mContext;
    private boolean onlinePresence;
    String claimable;
    private int uniqueId;
    private int providerId;
    private int locationId;
    private int userId;
    private String locationName, providerName;
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
    private UserResponse userResponse = new UserResponse();
    String sector;
    private Provider providerResponse = new Provider();
    SearchTerminology mSearchTerminology;
    String terminology;
    String userTerminology;
    ProfileModel profileDetails;
    String consumerName;
    String countryCode;
    String phoneNumber;
    String emailId;
    String whatsappCountryCode;
    String whatsappNumber;
    String conmrFName;
    String conmrLName;
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
            providerName = intent.getStringExtra("providerName");
            sector = intent.getStringExtra("sector");
        }

        if (providerInfo != null) {
            providerId = providerInfo.getId();
            if (providerInfo.getProfilePicture() != null) {
                String url = providerInfo.getProfilePicture().getUrl();
                Glide.with(mContext).load(url).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).circleCrop().into(ivSpImage);

                //PicassoTrustAll.getInstance(mContext).load(url).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(ivSpImage);

            } else {
                ivSpImage.setImageResource(R.drawable.icon_noimage);
            }

        }

        if (providerName != null) {

            tvProviderName.setText(providerName);
        }


        //more details recyclerView
        mRecycle_virtualfield = findViewById(R.id.mrecycle_virtualfield);

        rvServices = findViewById(R.id.rv_services);
        gridLayoutManager = new GridLayoutManager(this, 2);
        rvServices.setLayoutManager(gridLayoutManager);
        userServicesAdapter = new UserServicesAdapter(serviceInfoList, this, true, iSelectedService, providerDetails);
        rvServices.setAdapter(userServicesAdapter);


        getUserDetails(uniqueId, providerId, locationId);
        ApiGetProfileDetail();
        ApiGetProviderDetails(uniqueId);

        tvMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (llMore.getVisibility() != View.VISIBLE) {
                    llMore.setVisibility(View.VISIBLE);
                    int size = domainVirtual.size();
//                    int subDomainSize = sub_domainVirtual.size();
                    if (size > 0) {
                        llMore.setVisibility(View.VISIBLE);
                    } else {
                        llMore.setVisibility(View.GONE);
                    }
                } else {
                    llMore.setVisibility(View.GONE);
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

                    Intent intent = new Intent(UserDetailActivity.this, ChatActivity.class);
                    intent.putExtra("from", Constants.PROVIDER);
                    intent.putExtra("accountId", providerDetails.getId());
                    intent.putExtra("name", providerDetails.getBusinessName());
                    startActivity(intent);

                    /*enquiryDialog = new EnquiryDialog(mContext, providerDetails.getBusinessName(), iSendMessage, providerDetails.getId(),providerId);
                    enquiryDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                    enquiryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    enquiryDialog.show();
                    DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                    int width = (int) (metrics.widthPixels * 1);
                    enquiryDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);*/
                } else {
                    Toast.makeText(UserDetailActivity.this, "Please try after some time", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void getUserDetails(int uniqueId, int providerId, int locId) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Call<UserResponse> call = apiService.getUserDetails(uniqueId, providerId);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, final Response<UserResponse> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(UserDetailActivity.this, mDialog);

                    if (response.code() == 200) {
                        userResponse = response.body();

                        if (userResponse != null) {

                            if (userResponse.getBusinessProfile() != null) {
                                providerDetails = userResponse.getBusinessProfile();

                                if (providerDetails != null) {

                                    userId = providerDetails.getId();
                                    onlinePresence = providerDetails.isOnlinePresence();
                                    onlinePresence = true;   //businessprofile .Json is not getting updated correctly, so we don't need to check this condition..that's why setting it as true.
                                    UpdateMainUI(providerDetails);
                                    apiGetProviders(uniqueId, providerId, locId, userId);

                                    apiVirtualFields(uniqueId, providerId);

                                }
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(UserDetailActivity.this, mDialog);
            }
        });
    }

    private void apiVirtualFields(int muniqueID, int provId) {

        try {

            if (userResponse.getVirtualFields() != null) {

                String vFields = userResponse.getVirtualFields();
                resultData = new Gson().fromJson(vFields, SearchVirtualFields.class);

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
                        mAdapter = new VirtualFieldAdapter(domainVirtual, mContext, 0);
                        mRecycle_virtualfield.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        tvMoreInfo.setVisibility(View.GONE);
                    }
                } else {
                    llMore.setVisibility(View.GONE);
                }
            } else {
                llMore.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void UpdateMainUI(SearchViewDetail mBusinessDataList) {


        if (mBusinessDataList != null) {

            // Service provider name
            if (mBusinessDataList.getBusinessName() != null) {
                String name = mBusinessDataList.getBusinessName();
//                name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
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
                    tvSubDomainHint.setVisibility(View.GONE);
                } else {
                    tvSpeciality.setVisibility(View.VISIBLE);
                    tvSubDomainHint.setVisibility(View.VISIBLE);
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
            requests.add(apiService.getAppointmentSchedule(userId + "-" + locid + "-" + provid));
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

                                        if (checkInService.getCheckInServiceAvailability() != null) {

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
                                                    if (checkInService.getCheckInServiceAvailability().getPersonAhead() != null) {
                                                        serviceInfo.setPeopleInLine(checkInService.getCheckInServiceAvailability().getPersonAhead());
                                                    }
                                                    serviceInfo.setCalculationMode(checkInService.getCheckInServiceAvailability().getCalculationMode());
                                                    serviceInfo.setNextAvailableDate(checkInService.getCheckInServiceAvailability().getAvailableDate());
                                                    if (checkInService.getCheckInServiceAvailability().getServiceTime() != null) {
                                                        serviceInfo.setNextAvailableTime(checkInService.getCheckInServiceAvailability().getServiceTime());
                                                    }
                                                }
                                                serviceInfo.setServiceMode(checkInService.getServiceType());
                                                if (checkInService.getVirtualCallingModes() != null) {
                                                    serviceInfo.setCallingMode(checkInService.getVirtualCallingModes().get(0).getCallingMode());
                                                    serviceInfo.setVirtualServiceType(checkInService.getVirtualServiceType());
                                                }
                                                // adding all the info
                                                serviceInfo.setChecinServiceInfo(checkInService);
                                                services.add(serviceInfo);
                                            }
                                        }
                                    }
                                }
                            }

                            if (schedulesList.get(0).isApptEnabled()) {
                                for (SearchAppoinment appt : apptServicesList) {

                                    if (appt.getProvider() != null) {  // adding only Sp level services

                                        if (appt.getAppointServiceAvailability() != null) {

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
                                                    serviceInfo.setVirtualServiceType(appt.getVirtualServiceType());
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
                                                userServicesAdapter = new UserServicesAdapter(servicesInfoList, UserDetailActivity.this, false, iSelectedService, providerDetails);
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
        Intent intent = null;
        if (checkinServiceInfo != null) {
            if (checkinServiceInfo.getServiceOptionIds() != null && checkinServiceInfo.getServiceOptionIds().size() > 0) {
                intent = new Intent(UserDetailActivity.this, ServiceOptionActivity.class);
            } else {
                intent = new Intent(UserDetailActivity.this, CheckInActivity.class);
            }
            intent.putExtra("uniqueID", uniqueId);
            intent.putExtra("providerName", tvSpName.getText().toString());
            intent.putExtra("accountBusinessName", tvProviderName.getText().toString());
            intent.putExtra("locationName", tvLocationName.getText().toString());
            intent.putExtra("providerId", providerId);
            intent.putExtra("locationId", locationId);
            intent.putExtra("checkInInfo", checkinServiceInfo);
            intent.putExtra("userId", userId);
            intent.putExtra("fromUser", true);
            intent.putExtra("sector", sector);
            if (providerInfo.getProfilePicture() != null && providerInfo.getProfilePicture().getUrl() != null && !providerInfo.getProfilePicture().getUrl().trim().isEmpty()) {
                String url = providerInfo.getProfilePicture().getUrl();
                url = url.replaceAll(" ", "%20");
                String finalUrl = url;

                intent.putExtra("providerLogo", finalUrl);
            }
            ServiceInfo serviceInfo = new ServiceInfo();
            serviceInfo.setServiceId(checkinServiceInfo.getId());
            serviceInfo.setServiceName(checkinServiceInfo.getName());
            serviceInfo.setDescription(checkinServiceInfo.getDescription());
            serviceInfo.setType(Constants.CHECKIN);

            intent.putExtra("serviceInfo", serviceInfo);

            startActivity(intent);
        }
    }

    @Override
    public void onAppointmentSelected(SearchAppoinment appointmentServiceInfo) {
        Intent intent = null;
        if (appointmentServiceInfo != null) {
            ServiceInfo serviceInfo = new ServiceInfo();
            serviceInfo.setServiceId(appointmentServiceInfo.getId());
            serviceInfo.setServiceName(appointmentServiceInfo.getName());
            serviceInfo.setDescription(appointmentServiceInfo.getDescription());
            serviceInfo.setType(Constants.APPOINTMENT);
            serviceInfo.setUser(true);
            serviceInfo.setNoteManidtory(appointmentServiceInfo.isConsumerNoteMandatory());
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
            if (appointmentServiceInfo.getPreInfoTitle() != null) {
                serviceInfo.setPreInfoTitle(appointmentServiceInfo.getPreInfoTitle());
            }
            if (appointmentServiceInfo.getServiceType() != null) {
                serviceInfo.setServiceType(appointmentServiceInfo.getServiceType());
            }
            if (appointmentServiceInfo.getVirtualCallingModes() != null) {
                serviceInfo.setCallingMode(appointmentServiceInfo.getVirtualCallingModes().get(0).getCallingMode());
                serviceInfo.setVirtualCallingValue(appointmentServiceInfo.getVirtualCallingModes().get(0).getValue());
                serviceInfo.setVirtualServiceType(appointmentServiceInfo.getVirtualServiceType());
            }
            if (appointmentServiceInfo.getAppointServiceAvailability() != null) {
                serviceInfo.setScheduleId(appointmentServiceInfo.getAppointServiceAvailability().getId());
                serviceInfo.setAvailableDate(appointmentServiceInfo.getAppointServiceAvailability().getNextAvailableDate());
                serviceInfo.setTime(appointmentServiceInfo.getAppointServiceAvailability().getNextAvailable());
            }

            if (appointmentServiceInfo.getTotalAmount() != null) {
                serviceInfo.setTotalAmount(appointmentServiceInfo.getTotalAmount());
            }
            if (appointmentServiceInfo.getMaxBookingsAllowed() > 0) {
                serviceInfo.setMaxBookingsAllowed(appointmentServiceInfo.getMaxBookingsAllowed());
            }
            serviceInfo.setShowOnlyAvailableSlots(appointmentServiceInfo.isShowOnlyAvailableSlots());

            /** booking Type "booking"/"request" **/
            serviceInfo.setServiceBookingType(appointmentServiceInfo.getServiceBookingType());
            serviceInfo.setIsDate(appointmentServiceInfo.isDate());
            serviceInfo.setIsDateTime(appointmentServiceInfo.isDateTime());
            serviceInfo.setIsNoDateTime(appointmentServiceInfo.isNoDateTime());
            /** booking Type "booking"/"request" **/

            if ((appointmentServiceInfo.getServiceBookingType().equalsIgnoreCase(Constants.SERVICE_BOOKING_TYPE_REQUEST) && appointmentServiceInfo.isNoDateTime())) {
                JSONObject queueobj = createJsonObjectForBooking(serviceInfo);

                BookingModel model = new BookingModel();
                model.setAppointmentType(Constants.SINGLE_APPOINTMENT);
                model.setProviderUniqueId(uniqueId);
                model.setProviderResponse(providerResponse);
                model.setJsonObject(queueobj.toString());
                model.setAccountId(userId);  //
                model.setServiceInfo(serviceInfo);
                model.setmSearchTerminology(mSearchTerminology);
                model.setFamilyEMIID(0);
                model.setCountryCode(countryCode);
                model.setPhoneNumber(phoneNumber);
                model.setFrom(Constants.APPOINTMENT);
                model.setProviderName(tvSpName.getText().toString());
                model.setLocationName(locationName);
                model.setCustomerName(consumerName);
                model.setEmailId(emailId);
                model.setWhtsappCountryCode(whatsappCountryCode);
                model.setWhtsappPhoneNumber(whatsappNumber);
                model.setAccountBusinessName(providerName);
                if (serviceInfo.getServiceBookingType() != null && !serviceInfo.getServiceBookingType().isEmpty()
                        && (serviceInfo.getServiceBookingType().equalsIgnoreCase(Constants.SERVICE_BOOKING_TYPE_BOOKING)
                        || serviceInfo.getServiceBookingType().equalsIgnoreCase(Constants.SERVICE_BOOKING_TYPE_REQUEST))) {
                    model.setServiceBookingType(serviceInfo.getServiceBookingType());
                }
                if (providerInfo.getProfilePicture() != null && providerInfo.getProfilePicture().getUrl() != null && !providerInfo.getProfilePicture().getUrl().trim().isEmpty()) {
                    String providerLogoUrl = providerInfo.getProfilePicture().getUrl();
                    providerLogoUrl = providerLogoUrl.replaceAll(" ", "%20");
                    model.setProviderLogo(providerLogoUrl);
                }
                String pCountryCode = providerResponse.getBusinessProfile().getCountryCode();
                String pPhNo = providerResponse.getBusinessProfile().getAccountLinkedPhNo();
                if ((pCountryCode != null) && (!pCountryCode.isEmpty()) && (pPhNo != null) && (!pPhNo.isEmpty())) {
                    model.setProviderPhoneNumber(pCountryCode + " " + pPhNo);
                }
                intent = new Intent(UserDetailActivity.this, ReconfirmationActivity.class);
                intent.putExtra("data", model);

            } else {
                if (appointmentServiceInfo.getServiceOptionIds() != null && appointmentServiceInfo.getServiceOptionIds().size() > 0) {
                    intent = new Intent(UserDetailActivity.this, ServiceOptionActivity.class);
                } else {
                    intent = new Intent(UserDetailActivity.this, AppointmentActivity.class);
                }
                intent.putExtra("uniqueID", uniqueId);
                intent.putExtra("providerName", tvSpName.getText().toString());
                intent.putExtra("accountBusinessName", tvProviderName.getText().toString());
                intent.putExtra("locationName", tvLocationName.getText().toString());
                intent.putExtra("locationId", locationId);
                intent.putExtra("providerId", providerId);
                intent.putExtra("userId", userId);
                intent.putExtra("fromUser", true);
                intent.putExtra("sector", sector);
                //intent.putExtra("consumerId", consumerId);
                intent.putExtra("familyMemId", 0);
                intent.putExtra("requestFor", Constants.APPOINTMENT);
                intent.putExtra("requestFrom", "");

                if (providerInfo.getProfilePicture() != null && providerInfo.getProfilePicture().getUrl() != null && !providerInfo.getProfilePicture().getUrl().trim().isEmpty()) {
                    String url = providerInfo.getProfilePicture().getUrl();
                    url = url.replaceAll(" ", "%20");
                    String finalUrl = url;

                    intent.putExtra("providerLogo", finalUrl);
                }

                intent.putExtra("serviceInfo", serviceInfo);
                intent.putExtra("sector", sector);
            }



            startActivity(intent);
        }

    }

    @Override
    public void getMessage(String valueOf) {
        // do nothing
    }

    private void ApiGetProviderDetails(int uniqueId) {

        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<Provider> call = apiService.getProviderDetails(uniqueId);
        call.enqueue(new Callback<Provider>() {
            @Override
            public void onResponse(Call<Provider> call, Response<Provider> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(UserDetailActivity.this, mDialog);
                    Config.logV("URL----VIRTUAL---8888--------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------VIRTUAL-------------------" + response.code());
                    if (response.code() == 200) {
                        providerResponse = response.body();
                        if (providerResponse != null) {

                            // apiVirtualFields(providerResponse.getVirtualFields());

                            // ApiSearchViewLocation(providerResponse.getLocation(), providerResponse.getBusinessProfile(), providerResponse);

                            apiSearchViewTerminology(providerResponse.getTerminologies());

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Provider> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(UserDetailActivity.this, mDialog);
            }
        });
    }

    private void apiSearchViewTerminology(String termin) {
        try {
            mSearchTerminology = new Gson().fromJson(termin, SearchTerminology.class);
            if (mSearchTerminology != null) {
                terminology = mSearchTerminology.getWaitlist();
                userTerminology = mSearchTerminology.getProvider();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void ApiGetProfileDetail() {
        int consumerID = SharedPreference.getInstance(UserDetailActivity.this).getIntValue("consumerId", 0);

        ApiInterface apiService =
                ApiClient.getClient(UserDetailActivity.this).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(UserDetailActivity.this, UserDetailActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ProfileModel> call = apiService.getProfileDetail(consumerID);

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
                            String conmrFName = profileDetails.getUserprofile().getFirstName();
                            String conmrLName = profileDetails.getUserprofile().getLastName();
                            consumerName = conmrFName + " " + conmrLName;
                            countryCode = SharedPreference.getInstance(mContext).getStringValue("countryCode", "");
                            phoneNumber = profileDetails.getUserprofile().getPrimaryMobileNo();
                            if (profileDetails.getUserprofile().getEmail() != null) {
                                emailId = profileDetails.getUserprofile().getEmail();
                            }

                            whatsappCountryCode = countryCode.replace("+", "");
                            whatsappNumber = profileDetails.getUserprofile().getPrimaryMobileNo();

                        }
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

    private JSONObject createJsonObjectForBooking(ServiceInfo serviceInfo) {
        JSONObject queueobj = new JSONObject();
        JSONObject waitobj = new JSONObject();

        JSONObject sejsonobj = new JSONObject();
        JSONArray waitlistArray = new JSONArray();
        JSONObject sjsonobj = new JSONObject();

        try {
            waitobj.put("id", 0);
            waitobj.put("firstName", conmrFName);
            waitobj.put("lastName", conmrLName);
            if (emailId != null && !emailId.equalsIgnoreCase("")) {
                waitobj.put("email", emailId);
            }
            waitlistArray.put(waitobj);

            sejsonobj.put("id", serviceInfo.getServiceId());
            sejsonobj.put("serviceType", serviceInfo.getServiceType());

            sjsonobj.put("id", serviceInfo.getScheduleId());

            queueobj.put("phoneNumber", phoneNumber);
            queueobj.put("countryCode", countryCode);

            queueobj.putOpt("service", sejsonobj);
            queueobj.putOpt("appmtFor", waitlistArray);
            queueobj.putOpt("schedule", sjsonobj);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return queueobj;
    }
}