package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.jaldeeinc.jaldee.Interface.ISelectedService;
import com.jaldeeinc.jaldee.Interface.ISendMessage;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.MainServicesAdapter;
import com.jaldeeinc.jaldee.adapter.SectionRecyclerViewAdapter;
import com.jaldeeinc.jaldee.adapter.ServicesAdapter;
import com.jaldeeinc.jaldee.adapter.SpecialisationAdapter;
import com.jaldeeinc.jaldee.adapter.VirtualFieldAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CircleTransform;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.EnquiryDialog;
import com.jaldeeinc.jaldee.custom.IGetSelectedLocation;
import com.jaldeeinc.jaldee.custom.LocationsDialog;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;
import com.jaldeeinc.jaldee.custom.ResizableCustomView;
import com.jaldeeinc.jaldee.model.ProviderUserModel;
import com.jaldeeinc.jaldee.response.DepServiceInfo;
import com.jaldeeinc.jaldee.response.DepartmentInfo;
import com.jaldeeinc.jaldee.response.FavouriteModel;
import com.jaldeeinc.jaldee.response.ProfilePicture;
import com.jaldeeinc.jaldee.response.SearchAWsResponse;
import com.jaldeeinc.jaldee.response.SearchAppoinment;
import com.jaldeeinc.jaldee.response.SearchAppointmentDepartmentServices;
import com.jaldeeinc.jaldee.response.SearchDepartmentServices;
import com.jaldeeinc.jaldee.response.SearchDonation;
import com.jaldeeinc.jaldee.response.SearchLocation;
import com.jaldeeinc.jaldee.response.SearchService;
import com.jaldeeinc.jaldee.response.SearchSetting;
import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.jaldeeinc.jaldee.response.SearchVirtualFields;
import com.jaldeeinc.jaldee.response.ServiceInfo;
import com.jaldeeinc.jaldee.widgets.CustomDialog;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jaldeeinc.jaldee.connection.ApiClient.context;

public class ProviderDetailActivity extends AppCompatActivity implements IGetSelectedLocation, ISelectedService, ISendMessage {


    @BindView(R.id.tv_spName)
    CustomTextViewBold tvSpName;

    @BindView(R.id.iv_verified)
    ImageView ivVerified;

    @BindView(R.id.tv_spSpecialization)
    CustomTextViewSemiBold tvSpeciality;

    @BindView(R.id.tv_locationName)
    CustomTextViewMedium tvLocationName;

    @BindView(R.id.txtMoredetails)
    CustomTextViewMedium tvMoreDetails;

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

    @BindView(R.id.tv_location)
    CustomTextViewSemiBold tvLocation;

    @BindView(R.id.tv_changeLocation)
    CustomTextViewBold tvChangeLocation;

    @BindView(R.id.ll_location)
    LinearLayout llLocation;

    @BindView(R.id.rl_Location)
    RelativeLayout rlLocation;

    @BindView(R.id.ll_more)
    LinearLayout llMore;

    @BindView(R.id.cv_back)
    CardView cvBack;

    @BindView(R.id.cv_share)
    CardView cvShare;

    @BindView(R.id.cv_enquiry)
    CardView cvEnquiry;

    @BindView(R.id.cv_favourite)
    CardView cvFavourite;

    @BindView(R.id.iv_fav)
    ImageView ivfav;

    String claimable;
    private int uniqueId;
    private int providerId;
    private int locationId;
    String accountType = "";
    boolean flag_more = false;
    boolean isToken;
    private boolean onlinePresence = false;
    private List<SearchAWsResponse> mSearchRespPass;
    private ArrayList<SearchViewDetail> mSearchGallery;
    private SearchViewDetail mBusinessDataList = new SearchViewDetail();
    private Context mContext;
    private SearchSetting mSearchSettings;
    ArrayList<SearchDepartmentServices> mSearchDepartmentList;
    private ArrayList<SearchService> checkInServices;
    ArrayList<SearchDonation> dontServices = new ArrayList<>();
    ArrayList<SearchAppointmentDepartmentServices> LaServicesList = new ArrayList<>();
    ArrayList<SearchAppointmentDepartmentServices> apptServiceswithDeptList = new ArrayList<>();
    ArrayList<SearchDonation> dontTotalServices = new ArrayList<>();
    ArrayList<SearchAppointmentDepartmentServices> aServiceList = new ArrayList<>();
    ArrayList<SearchAppoinment> appointServices = new ArrayList<>();
    ArrayList<SearchAppoinment> appTotalServices = new ArrayList<>();
    private RecyclerView rvServices, mRecycle_virtualfield;
    private GridLayoutManager gridLayoutManager;
    private ServicesAdapter servicesAdapter;
    ArrayList<SearchLocation> mSearchLocList;
    private MainServicesAdapter mainServicesAdapter;
    ArrayList<DepartmentInfo> departmentsList = new ArrayList<DepartmentInfo>();
    ArrayList<DepServiceInfo> serviceInfoList = new ArrayList<DepServiceInfo>();
    private SectionRecyclerViewAdapter sectionRecyclerViewAdapter = null;
    private LocationsDialog locationsDialog;
    private IGetSelectedLocation iGetSelectedLocation;
    private ISelectedService iSelectedService;
    ArrayList<SearchDepartmentServices> departmentProviders = new ArrayList<SearchDepartmentServices>();
    ArrayList<ProviderUserModel> providersList = new ArrayList<ProviderUserModel>();
    VirtualFieldAdapter mAdapter;
    SpecialisationAdapter sAdapter;
    SearchVirtualFields resultData;
    ArrayList<SearchVirtualFields> domainVirtual = new ArrayList<>();
    ArrayList<SearchVirtualFields> sub_domainVirtual = new ArrayList<>();
    private ISendMessage iSendMessage;
    private EnquiryDialog enquiryDialog;
    boolean favFlag = false;
    ArrayList<FavouriteModel> mFavList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_detail);
        ButterKnife.bind(ProviderDetailActivity.this);
        mContext = ProviderDetailActivity.this;
        iGetSelectedLocation = this;
        iSelectedService = this;
        iSendMessage = this;


        Intent intent = getIntent();
        if (intent != null) {
            uniqueId = Integer.parseInt(intent.getStringExtra("uniqueID"));
            claimable = intent.getStringExtra("claimable");
            claimable = "0";
            locationId = intent.getIntExtra("locationId", 0);
        }

        //more details recyclerView
        mRecycle_virtualfield = findViewById(R.id.mrecycle_virtualfield);

        rvServices = findViewById(R.id.rv_services);
        gridLayoutManager = new GridLayoutManager(this, 2);
        rvServices.setLayoutManager(gridLayoutManager);
        mainServicesAdapter = new MainServicesAdapter(serviceInfoList, this, true, iSelectedService);
        rvServices.setAdapter(mainServicesAdapter);


        tvChangeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationsDialog = new LocationsDialog(mContext, mSearchLocList, iGetSelectedLocation);
                locationsDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                locationsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                locationsDialog.show();
                DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                int width = (int) (metrics.widthPixels * 1);
                locationsDialog.setCancelable(false);
                locationsDialog.getWindow().setGravity(Gravity.BOTTOM);
                locationsDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        });

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

        cvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cvEnquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mBusinessDataList != null) {
                    enquiryDialog = new EnquiryDialog(mContext, mBusinessDataList.getBusinessName(), iSendMessage, mBusinessDataList.getId());
                    enquiryDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                    enquiryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    enquiryDialog.show();
                    DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                    int width = (int) (metrics.widthPixels * 1);
                    enquiryDialog.setCancelable(false);
                    enquiryDialog.getWindow().setGravity(Gravity.BOTTOM);
                    enquiryDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                } else {

                    Toast.makeText(ProviderDetailActivity.this, "Please try after some time", Toast.LENGTH_SHORT).show();
                }
            }
        });


        apiVirtualFields(uniqueId);
        ApiSearchViewLocation(uniqueId);

    }

    private void apiVirtualFields(int muniqueID) {
        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));
        Call<SearchVirtualFields> call = apiService.getVirtualFields(muniqueID, sdf.format(currentTime));
        call.enqueue(new Callback<SearchVirtualFields>() {
            @Override
            public void onResponse(Call<SearchVirtualFields> call, Response<SearchVirtualFields> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(ProviderDetailActivity.this, mDialog);
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
                    Config.closeDialog(ProviderDetailActivity.this, mDialog);
            }
        });
    }


    private void ApiSearchViewLocation(int muniqueID) {
        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));
        Call<ArrayList<SearchLocation>> call = apiService.getSearchViewLoc(muniqueID, sdf.format(currentTime));
        call.enqueue(new Callback<ArrayList<SearchLocation>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchLocation>> call, Response<ArrayList<SearchLocation>> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(ProviderDetailActivity.this, mDialog);
                    Config.logV("URL---3333------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code--Location-----------------------" + response.code());
                    if (response.code() == 200) {
                        mSearchLocList = response.body();
                        if (mSearchLocList != null) {
                            if (mSearchLocList.size() > 1) {
                                rlLocation.setVisibility(View.VISIBLE);
                                tvLocation.setText(mSearchLocList.get(0).getAddress());
                                tvChangeLocation.setVisibility(View.VISIBLE);
                            } else {
                                rlLocation.setVisibility(View.GONE);
                            }
                            locationId = mSearchLocList.get(0).getId();
                            apiSearchViewDetail(uniqueId);

                        } else {
                            rlLocation.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<SearchLocation>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(ProviderDetailActivity.this, mDialog);
            }
        });
    }


    private void apiSearchViewDetail(int id) {
        ApiInterface apiService = ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(ProviderDetailActivity.this, ProviderDetailActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));
        Call<SearchViewDetail> call = apiService.getSearchViewDetail(id, sdf.format(currentTime));
        call.enqueue(new Callback<SearchViewDetail>() {
            @Override
            public void onResponse(Call<SearchViewDetail> call, final Response<SearchViewDetail> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(ProviderDetailActivity.this, mDialog);
                    Config.logV("URL-----1111----------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-----detail--------------------" + response.code());
                    if (response.code() == 200) {
                        mBusinessDataList = response.body();

                        if (mBusinessDataList != null) {
                            accountType = mBusinessDataList.getAccountType();

                            if (mBusinessDataList.getId() != 0) {
                                providerId = mBusinessDataList.getId();
                            }
                            onlinePresence = mBusinessDataList.isOnlinePresence();
                            UpdateMainUI(mBusinessDataList);
                            apiSearchGallery(id);
                            apiSettings_Details(id, providerId, locationId);
                            // check if the provider is a favourite
                            ApiFavList();

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
                    Config.closeDialog(ProviderDetailActivity.this, mDialog);
            }
        });
    }


    private void apiSearchGallery(int muniqueID) {
        ApiInterface apiService = ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(ProviderDetailActivity.this, ProviderDetailActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println(sdf.format(currentTime));
        Call<ArrayList<SearchViewDetail>> call = apiService.getSearchGallery(muniqueID, sdf.format(currentTime));
        call.enqueue(new Callback<ArrayList<SearchViewDetail>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchViewDetail>> call, Response<ArrayList<SearchViewDetail>> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(ProviderDetailActivity.this, mDialog);
                    Config.logV("URL------100000---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-----gallery--------------------" + response.code());
                    if (response.code() == 200) {
                        mSearchGallery = response.body();
                        if (mSearchGallery != null) {
                            UpdateGallery(mSearchGallery);
                        }

                    } else {
                        tv_mImageViewTextnew.setVisibility(View.GONE);
                        if (mBusinessDataList.getLogo() != null) {
                            // Picasso.with(mContext).load(mBusinessDataList.getLogo().getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(mImgeProfile);
                            if (mSearchGallery != null) {
                                UpdateGallery(mSearchGallery);
                            }
                        } else {
                            tv_mImageViewTextnew.setVisibility(View.GONE);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<SearchViewDetail>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(ProviderDetailActivity.this, mDialog);
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

            // verified
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

            // location
            if (mBusinessDataList.getBaseLocation() != null) {

                if (mBusinessDataList.getBaseLocation().getPlace() != null) {

                    tvLocationName.setText(mBusinessDataList.getBaseLocation().getPlace());

                    tvLocationName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            openMapView(mBusinessDataList.getBaseLocation().getLattitude(), mBusinessDataList.getBaseLocation().getLongitude(), mBusinessDataList.getBaseLocation().getPlace());
                        }
                    });
                }
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
                            ResizableCustomView.doResizeTextView(ProviderDetailActivity.this, tvAbout, 2, "..more", true);
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

    public void UpdateGallery(final ArrayList<SearchViewDetail> mGallery) {
        //  Picasso.with(this).load(mGallery.get(0).getUrl()).fit().into(mImgeProfile);
        Config.logV("Gallery--------------333-----" + mGallery.size());
        try {
            if (mGallery.size() > 0 || mBusinessDataList.getLogo() != null) {
                tvViewGallery.setVisibility(View.VISIBLE);
                ivSpImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Config.logV("Gallery------------------------------" + mGallery.size());
                        ArrayList<String> mGalleryList = new ArrayList<>();
                        if (mBusinessDataList.getLogo() != null) {
                            mGalleryList.add(mBusinessDataList.getLogo().getUrl());
                        }
                        for (int i = 0; i < mGallery.size(); i++) {
                            mGalleryList.add(mGallery.get(i).getUrl());
                        }
                        boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryList, v.getContext());
                        if (mValue) {
                            Intent intent = new Intent(ProviderDetailActivity.this, SwipeGalleryImage.class);
                            intent.putExtra("pos", 0);
                            startActivity(intent);
                        }
                    }
                });

                tvViewGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Config.logV("Gallery------------------------------" + mGallery.size());
                        ArrayList<String> mGalleryList = new ArrayList<>();
                        if (mBusinessDataList.getLogo() != null) {
                            mGalleryList.add(mBusinessDataList.getLogo().getUrl());
                        }
                        for (int i = 0; i < mGallery.size(); i++) {
                            mGalleryList.add(mGallery.get(i).getUrl());
                        }
                        boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryList, v.getContext());
                        if (mValue) {
                            Intent intent = new Intent(ProviderDetailActivity.this, SwipeGalleryImage.class);
                            intent.putExtra("pos", 0);
                            startActivity(intent);
                        }
                    }
                });

            } else {
                tvViewGallery.setVisibility(View.GONE);
            }
            Config.logV("Bussiness logo @@@@@@@@@@" + mBusinessDataList.getLogo());
            if (mBusinessDataList.getLogo() != null) {
                PicassoTrustAll.getInstance(context).load(mBusinessDataList.getLogo().getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(ivSpImage);
            } else {
                //Toast.makeText(mContext, "There is no Profile Pic", Toast.LENGTH_SHORT).show();
                // Picasso.with(mContext).load(mGallery.get(0).getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(mImgeProfile);
            }
            if (mBusinessDataList.getLogo() != null) {
                if (mGallery.size() > 0) {
                    tv_mImageViewTextnew.setVisibility(View.VISIBLE);
                    tv_mImageViewTextnew.setText(" +" + String.valueOf(mGallery.size()));
                }
            } else if (mBusinessDataList.getLogo() == null) {
                if (mGallery.size() > 0) {
                    tv_mImageViewTextnew.setVisibility(View.VISIBLE);
                    tv_mImageViewTextnew.setText(" +" + String.valueOf(mGallery.size()));
                } else {
                    tv_mImageViewTextnew.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void apiSettings_Details(int uniqueId, int mProviderId, final int mlocationId) {
        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));
        Call<SearchSetting> call = apiService.getSearchViewSetting(uniqueId, sdf.format(currentTime));
        call.enqueue(new Callback<SearchSetting>() {
            @Override
            public void onResponse(Call<SearchSetting> call, Response<SearchSetting> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(ProviderDetailActivity.this, mDialog);
                    Config.logV("URL--7777-------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------Setting-------------------" + response.code());
                    if (response.code() == 200) {
                        mSearchSettings = response.body();
                        if (mSearchSettings != null) {

                            isToken = mSearchSettings.isShowTokenId();
                            if (!accountType.equalsIgnoreCase("INDEPENDENT_SP")) {
                                if (mSearchSettings.isFilterByDept()) {
                                    // To check if there are any users created -  if no users or departments are created.. even in 404 we have to proceed to next step
                                    getProviderWithDepartments(uniqueId, mProviderId, mlocationId);
                                } else {
                                    //get only providers when there are no departments
                                    // To check if there are any users created -  if no users are created.. even in 404 we have to proceed to next step
                                    getProvidersNew(uniqueId, mProviderId, mlocationId);
                                }
                            } else {

                                getOnlyServices(uniqueId, mlocationId);
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SearchSetting> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(ProviderDetailActivity.this, mDialog);
            }
        });
    }

    private void getProviderWithDepartments(int uniqueId, int mProviderId, int mlocationId) {

        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));
        Call<ArrayList<SearchDepartmentServices>> call = apiService.getUserandDepartments(uniqueId, sdf.format(currentTime));
        call.enqueue(new Callback<ArrayList<SearchDepartmentServices>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchDepartmentServices>> call, Response<ArrayList<SearchDepartmentServices>> response) {
                try {

                    Config.logV("URL--7777-------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------Setting-------------------" + response.code());
                    if (response.code() == 200) {
                        departmentProviders.clear();
                        departmentProviders = response.body();

                        getDepartmentsWithCheckInServices(uniqueId, mProviderId, mlocationId);

                    } else if (response.code() == 404) {

                        getDepartmentsWithCheckInServices(uniqueId, mProviderId, mlocationId);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<SearchDepartmentServices>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
            }
        });
    }

    private void getProvidersNew(int uniqueId, int mProviderId, int mlocationId) {

        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));
        Call<ArrayList<ProviderUserModel>> call = apiService.getUsers(uniqueId, sdf.format(currentTime));
        call.enqueue(new Callback<ArrayList<ProviderUserModel>>() {
            @Override
            public void onResponse(Call<ArrayList<ProviderUserModel>> call, Response<ArrayList<ProviderUserModel>> response) {
                try {

                    Config.logV("URL--7777-------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------Setting-------------------" + response.code());
                    if (response.code() == 200) {

                        providersList = response.body();
                        apiGetProviders(uniqueId, mProviderId, mlocationId);

                    } else if (response.code() == 404) {

                        apiGetProviders(uniqueId, mProviderId, mlocationId);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ProviderUserModel>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
            }
        });
    }

    private void getOnlyServices(int unqId, int locid) {


        try {
            ApiInterface s3ApiService =
                    ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
            ApiInterface apiService =
                    ApiClient.getClient(mContext).create(ApiInterface.class);
            Date currentTime = new Date();
            final SimpleDateFormat sdf = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));


            List<Observable<?>> requests = new ArrayList<>();

            // Make a collection of all requests you need to call at once, there can be any number of requests, not only 3. You can have 2 or 5, or 100.
            requests.add(apiService.getCheckInServices(locid));
            requests.add(apiService.getAppointmentServices(locid));
            requests.add(s3ApiService.getDonationServices(unqId, sdf.format(currentTime)));

            // Zip all requests with the Function, which will receive the results.
            Observable.zip(requests, new Function<Object[], Object>() {
                @Override
                public Object apply(Object[] objects) throws Exception {
                    // Objects[] is an array of combined results of completed requests

                    ArrayList<SearchService> checkInServList = (ArrayList<SearchService>) objects[0];
                    ArrayList<SearchAppoinment> apptServicesList = (ArrayList<SearchAppoinment>) objects[1];
                    ArrayList<SearchDonation> donationServices = (ArrayList<SearchDonation>) objects[2];

                    checkInServList = checkInServList == null ? new ArrayList<SearchService>() : checkInServList;

                    apptServicesList = apptServicesList == null ? new ArrayList<SearchAppoinment>() : apptServicesList;

                    donationServices = donationServices == null ? new ArrayList<SearchDonation>() : donationServices;

                    serviceInfoList.clear();
                    DepartmentInfo departmentInfo = new DepartmentInfo();
                    ArrayList<DepServiceInfo> services = new ArrayList<DepServiceInfo>();

                    for (SearchService checkInService : checkInServList) {

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

                    for (SearchAppoinment appt : apptServicesList) {


                        DepServiceInfo serviceInfo = new DepServiceInfo();
                        serviceInfo.setDepartmentId(0);
                        serviceInfo.setDepartmentName("");
                        serviceInfo.setId(appt.getId());
                        serviceInfo.setName(appt.getName());
                        serviceInfo.setType(Constants.APPOINTMENT);
                        serviceInfo.setEstTime("");
                        serviceInfo.setPeopleInLine(0);
                        serviceInfo.setCalculationMode("");
                        serviceInfo.setOnline(onlinePresence);
                        serviceInfo.setAvailability(true);
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

                    // adding donations only when donations are accepted
                    if (onlinePresence && mBusinessDataList.isDonationFundRaising()) {
                        for (SearchDonation donation : donationServices) {

                            DepServiceInfo serviceInfo = new DepServiceInfo();
                            serviceInfo.setDepartmentId(0);
                            serviceInfo.setDepartmentName("");
                            serviceInfo.setId(donation.getId());
                            serviceInfo.setName(donation.getName());
                            serviceInfo.setType(Constants.DONATION);
                            serviceInfo.setEstTime("");
                            serviceInfo.setPeopleInLine(0);
                            serviceInfo.setCalculationMode("");
                            serviceInfo.setServiceMode("");
                            serviceInfo.setOnline(onlinePresence);
                            serviceInfo.setCallingMode("");
                            serviceInfo.setNextAvailableTime("");
                            serviceInfo.setAvailability(true);
                            serviceInfo.setProviderImage("");
                            if (donation.getMinDonationAmount() != null && donation.getMaxDonationAmount() != null) {
                                serviceInfo.setMinDonationAmount(donation.getMinDonationAmount());
                                serviceInfo.setMaxDonationAmount(donation.getMaxDonationAmount());
                            }
                            // adding all the info
                            serviceInfo.setDonationServiceInfo(donation);
                            services.add(serviceInfo);
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
                                    Log.e("ListOf Calls", "0");
                                    ArrayList<DepServiceInfo> servicesInfoList = (ArrayList<DepServiceInfo>) object;

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            gridLayoutManager = new GridLayoutManager(ProviderDetailActivity.this, 2);
                                            rvServices.setLayoutManager(gridLayoutManager);
                                            mainServicesAdapter = new MainServicesAdapter(servicesInfoList, ProviderDetailActivity.this, false, iSelectedService);
                                            rvServices.setAdapter(mainServicesAdapter);

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

    private void apiGetProviders(int unqId, int provid, int locid) {

        try {
            ApiInterface s3ApiService =
                    ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
            ApiInterface apiService =
                    ApiClient.getClient(mContext).create(ApiInterface.class);
            Date currentTime = new Date();
            final SimpleDateFormat sdf = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));


            List<Observable<?>> requests = new ArrayList<>();

            // Make a collection of all requests you need to call at once, there can be any number of requests, not only 3. You can have 2 or 5, or 100.
            requests.add(apiService.getCheckInServices(locid));
            requests.add(apiService.getAppointmentServices(locid));
            requests.add(s3ApiService.getDonationServices(unqId, sdf.format(currentTime)));

            // Zip all requests with the Function, which will receive the results.
            Observable.zip(requests, new Function<Object[], Object>() {
                @Override
                public Object apply(Object[] objects) throws Exception {
                    // Objects[] is an array of combined results of completed requests

                    ArrayList<SearchService> checkInServList = (ArrayList<SearchService>) objects[0];
                    ArrayList<SearchAppoinment> apptServicesList = (ArrayList<SearchAppoinment>) objects[1];
                    ArrayList<SearchDonation> donationServices = (ArrayList<SearchDonation>) objects[2];


                    providersList = providersList == null ? new ArrayList<ProviderUserModel>() : providersList;

                    checkInServList = checkInServList == null ? new ArrayList<SearchService>() : checkInServList;

                    apptServicesList = apptServicesList == null ? new ArrayList<SearchAppoinment>() : apptServicesList;

                    donationServices = donationServices == null ? new ArrayList<SearchDonation>() : donationServices;

                    serviceInfoList.clear();
                    DepartmentInfo departmentInfo = new DepartmentInfo();
                    ArrayList<DepServiceInfo> services = new ArrayList<DepServiceInfo>();

                    for (SearchService checkInService : checkInServList) {

                        if (checkInService.getProvider() == null) {  // adding only Sp level services

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

                    for (SearchAppoinment appt : apptServicesList) {

                        if (appt.getProvider() == null) {  // adding only Sp level services

                            DepServiceInfo serviceInfo = new DepServiceInfo();
                            serviceInfo.setDepartmentId(0);
                            serviceInfo.setDepartmentName("");
                            serviceInfo.setId(appt.getId());
                            serviceInfo.setName(appt.getName());
                            serviceInfo.setType(Constants.APPOINTMENT);
                            serviceInfo.setEstTime("");
                            serviceInfo.setOnline(onlinePresence);
                            serviceInfo.setPeopleInLine(0);
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


                    // for providers
                    for (ProviderUserModel provider : providersList) {

                        DepServiceInfo serviceInfo = new DepServiceInfo();
                        serviceInfo.setDepartmentId(Integer.parseInt(provider.getDeptId()));
                        serviceInfo.setDepartmentName("");
                        serviceInfo.setId(provider.getId());
                        serviceInfo.setName(provider.getBusinessName());
                        serviceInfo.setType(Constants.PROVIDER);
                        serviceInfo.setEstTime("");
                        serviceInfo.setPeopleInLine(0);
                        serviceInfo.setCalculationMode("");
                        serviceInfo.setServiceMode("");
                        serviceInfo.setAvailability(true);
                        serviceInfo.setCallingMode("");
                        serviceInfo.setNextAvailableTime("");
                        if (provider.getProfilePicture() != null) {
                            String url = extractUrl(provider.getProfilePicture());
                            serviceInfo.setProviderImage(url);
                        }
                        // adding all the info
                        serviceInfo.setProviderInfo(provider);
                        services.add(serviceInfo);

                    }

                    // adding donations only when donations are accepted
                    if (mBusinessDataList.isDonationFundRaising()) {
                        for (SearchDonation donation : donationServices) {

                            DepServiceInfo serviceInfo = new DepServiceInfo();
                            serviceInfo.setDepartmentId(0);
                            serviceInfo.setDepartmentName("");
                            serviceInfo.setId(donation.getId());
                            serviceInfo.setName(donation.getName());
                            serviceInfo.setType(Constants.DONATION);
                            serviceInfo.setEstTime("");
                            serviceInfo.setPeopleInLine(0);
                            serviceInfo.setCalculationMode("");
                            serviceInfo.setServiceMode("");
                            serviceInfo.setCallingMode("");
                            serviceInfo.setOnline(onlinePresence);
                            serviceInfo.setAvailability(true);
                            serviceInfo.setNextAvailableTime("");
                            serviceInfo.setProviderImage("");
                            if (donation.getMinDonationAmount() != null && donation.getMaxDonationAmount() != null) {
                                serviceInfo.setMinDonationAmount(donation.getMinDonationAmount());
                                serviceInfo.setMaxDonationAmount(donation.getMaxDonationAmount());
                            }
                            // adding all the info
                            serviceInfo.setDonationServiceInfo(donation);
                            services.add(serviceInfo);
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
                                    Log.e("ListOf Calls", "0");
                                    ArrayList<DepServiceInfo> servicesInfoList = (ArrayList<DepServiceInfo>) object;

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            gridLayoutManager = new GridLayoutManager(ProviderDetailActivity.this, 2);
                                            rvServices.setLayoutManager(gridLayoutManager);
                                            mainServicesAdapter = new MainServicesAdapter(servicesInfoList, ProviderDetailActivity.this, false, iSelectedService);
                                            rvServices.setAdapter(mainServicesAdapter);

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


    private void getDepartmentsWithCheckInServices(int unqId, int provId, int locid) {

        try {
            ApiInterface s3ApiService =
                    ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
            ApiInterface apiService =
                    ApiClient.getClient(mContext).create(ApiInterface.class);
            Date currentTime = new Date();
            final SimpleDateFormat sdf = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            List<Observable<?>> requests = new ArrayList<>();

            // Make a collection of all requests you need to call at once, there can be any number of requests, not only 3. You can have 2 or 5, or 100.
            requests.add(s3ApiService.getDeptCheckInServices(unqId, sdf.format(currentTime)));
            requests.add(apiService.getCheckInServices(locid));
            requests.add(apiService.getAppointmentServices(locid));
            requests.add(s3ApiService.getDonationServices(unqId, sdf.format(currentTime)));

            // Zip all requests with the Function, which will receive the results.
            Observable.zip(requests, new Function<Object[], Object>() {
                @Override
                public Object apply(Object[] objects) throws Exception {
                    // Objects[] is an array of combined results of completed requests

                    ArrayList<SearchDepartmentServices> deptCheckinsList = (ArrayList<SearchDepartmentServices>) objects[0];

                    ArrayList<SearchService> checkInServicesList = (ArrayList<SearchService>) objects[1];
                    ArrayList<SearchAppoinment> apptServicesList = (ArrayList<SearchAppoinment>) objects[2];

                    ArrayList<SearchDonation> donationServices = (ArrayList<SearchDonation>) objects[3];

                    deptCheckinsList = deptCheckinsList == null ? new ArrayList<SearchDepartmentServices>() : deptCheckinsList;

                    checkInServicesList = checkInServicesList == null ? new ArrayList<SearchService>() : checkInServicesList;

                    departmentProviders = departmentProviders == null ? new ArrayList<SearchDepartmentServices>() : departmentProviders;

                    donationServices = donationServices == null ? new ArrayList<SearchDonation>() : donationServices;

                    departmentsList.clear();
                    for (SearchDepartmentServices department : deptCheckinsList) {

                        DepartmentInfo departmentInfo = new DepartmentInfo();
                        ArrayList<DepServiceInfo> services = new ArrayList<DepServiceInfo>();

                        for (SearchService checkInService : checkInServicesList) {

                            if (Integer.parseInt(department.getDepartmentId()) == checkInService.getDepartment()) {

                                if (checkInService.getProvider() == null) {  // adding only Sp level services

                                    DepServiceInfo serviceInfo = new DepServiceInfo();
                                    serviceInfo.setDepartmentId(checkInService.getDepartment());
                                    serviceInfo.setDepartmentName(department.getDepartmentName());
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
                                    serviceInfo.setChecinServiceInfo(checkInService);
                                    if (checkInService.getVirtualCallingModes() != null) {
                                        serviceInfo.setCallingMode(checkInService.getVirtualCallingModes().get(0).getCallingMode());
                                    }
                                    // adding all the info
                                    services.add(serviceInfo);
                                }
                            }
                        }


                        // adding appointment services only when appointment is enabled

                        for (SearchAppoinment appt : apptServicesList) {

                            if (Integer.parseInt(department.getDepartmentId()) == appt.getDepartment()) {

                                if (appt.getProvider() == null) {  // adding only Sp level services

                                    DepServiceInfo serviceInfo = new DepServiceInfo();
                                    serviceInfo.setDepartmentId(appt.getDepartment());
                                    serviceInfo.setDepartmentName(department.getDepartmentName());
                                    serviceInfo.setId(appt.getId());
                                    serviceInfo.setName(appt.getName());
                                    serviceInfo.setType(Constants.APPOINTMENT);
                                    serviceInfo.setOnline(onlinePresence);
                                    serviceInfo.setEstTime("");
                                    serviceInfo.setPeopleInLine(0);
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


                        // for providers
                        for (SearchDepartmentServices provider : departmentProviders) {

                            if (department.getDepartmentId().equalsIgnoreCase(provider.getDepartmentId())) {

                                for (ProviderUserModel user : provider.getUsers()) {

                                    DepServiceInfo serviceInfo = new DepServiceInfo();
                                    serviceInfo.setDepartmentId(Integer.parseInt(provider.getDepartmentId()));
                                    serviceInfo.setDepartmentName(provider.getDepartmentName());
                                    serviceInfo.setId(user.getId());
                                    serviceInfo.setName(user.getBusinessName());
                                    serviceInfo.setType(Constants.PROVIDER);
                                    serviceInfo.setEstTime("");
                                    serviceInfo.setPeopleInLine(0);
                                    serviceInfo.setCalculationMode("");
                                    serviceInfo.setServiceMode("");
                                    serviceInfo.setAvailability(true);
                                    serviceInfo.setCallingMode("");
                                    serviceInfo.setNextAvailableTime("");
                                    if (user.getProfilePicture() != null) {
                                        String url = extractUrl(user.getProfilePicture());
                                        serviceInfo.setProviderImage(url);
                                    }
                                    // adding all the info
                                    serviceInfo.setProviderInfo(user);
                                    services.add(serviceInfo);
                                }

                            }
                        }

                        departmentInfo.setDeptServicesList(services);
                        departmentInfo.setDepartmentName(department.getDepartmentName());

                        departmentsList.add(departmentInfo);
                    }

                    // adding donations only when donations are accepted
                    if (mBusinessDataList.isDonationFundRaising()) {
                        // to add donation services
                        DepartmentInfo donationDepartment = new DepartmentInfo();
                        ArrayList<DepServiceInfo> donationServicesList = new ArrayList<DepServiceInfo>();
                        donationDepartment.setDepartmentName("Donations");

                        for (SearchDonation donation : donationServices) {

                            DepServiceInfo serviceInfo = new DepServiceInfo();
                            serviceInfo.setDepartmentId(0);
                            serviceInfo.setDepartmentName("");
                            serviceInfo.setId(donation.getId());
                            serviceInfo.setName(donation.getName());
                            serviceInfo.setType(Constants.DONATION);
                            serviceInfo.setEstTime("");
                            serviceInfo.setPeopleInLine(0);
                            serviceInfo.setCalculationMode("");
                            serviceInfo.setOnline(onlinePresence);
                            serviceInfo.setAvailability(true);
                            serviceInfo.setServiceMode("");
                            serviceInfo.setCallingMode("");
                            serviceInfo.setNextAvailableTime("");
                            if (donation.getMinDonationAmount() != null && donation.getMaxDonationAmount() != null) {
                                serviceInfo.setMinDonationAmount(donation.getMinDonationAmount());
                                serviceInfo.setMaxDonationAmount(donation.getMaxDonationAmount());
                            }
                            // adding all the info
                            serviceInfo.setDonationServiceInfo(donation);
                            donationServicesList.add(serviceInfo);
                        }

                        donationDepartment.setDeptServicesList(donationServicesList);
                        departmentsList.add(donationDepartment);
                    }

                    // do something with those results and emit new event
                    return departmentsList;
                }
            })
                    // After all requests had been performed the next observer will receive the Object, returned from Function

                    .subscribe(
                            // Will be triggered if all requests will end successfully (4xx and 5xx also are successful requests too)
                            new Consumer<Object>() {
                                @Override
                                public void accept(Object object) throws Exception {
                                    //Do something on successful completion of all requests
                                    Log.e("ListOf Calls", "0");
                                    ArrayList<DepartmentInfo> departmentsDataList = (ArrayList<DepartmentInfo>) object;

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Stuff that updates the UI
                                            GridLayoutManager glm = new GridLayoutManager(ProviderDetailActivity.this, 2);
                                            glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                                @Override
                                                public int getSpanSize(int position) {
                                                    switch (servicesAdapter.getItemViewType(position)) {
                                                        case 1:
                                                            return 2;
                                                        default:
                                                            return 1;
                                                    }
                                                }
                                            });
                                            rvServices.setLayoutManager(glm);
                                            servicesAdapter = new ServicesAdapter(ProviderDetailActivity.this, departmentsDataList, false, iSelectedService);
                                            rvServices.setAdapter(servicesAdapter);

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

    private void ApiFavList() {
        Config.logV("API Call");
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<ArrayList<FavouriteModel>> call = apiService.getFavourites();
        call.enqueue(new Callback<ArrayList<FavouriteModel>>() {
            @Override
            public void onResponse(Call<ArrayList<FavouriteModel>> call, Response<ArrayList<FavouriteModel>> response) {
                try {
                    ivfav.setImageResource(R.drawable.icon_favourite_line);
                    Config.logV("URL-----22222----------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        mFavList.clear();
                        mFavList = response.body();
                        if (mFavList != null && mFavList.size() > 0) {
                            favFlag = false;
                            for (int i = 0; i < mFavList.size(); i++) {
                                Config.logV("Fav List-----##&&&-----" + mFavList.get(i).getId());
                                Config.logV("Fav Fav List--------%%%%--" + mBusinessDataList.getId());
                                if (mFavList.get(i).getId() == mBusinessDataList.getId()) {
                                    favFlag = true;
                                    ivfav.setVisibility(View.VISIBLE);
                                    ivfav.setImageResource(R.drawable.icon_favourited);
                                }
                            }
                            ivfav.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (favFlag) {
                                        ApiRemoveFavo(mBusinessDataList.getId());

                                    } else {
                                        ApiAddFavo(mBusinessDataList.getId());
                                    }
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<FavouriteModel>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
            }
        });
    }

    private void ApiAddFavo(int providerID) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ResponseBody> call = apiService.AddFavourite(providerID);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(ProviderDetailActivity.this, mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        if (response.body().string().equalsIgnoreCase("true")) {

                            DynamicToast.make(context, "Added to Favourites", AppCompatResources.getDrawable(
                                    context, R.drawable.adt_ic_success),
                                    ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.green), Toast.LENGTH_SHORT).show();

                            ApiFavList();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(ProviderDetailActivity.this, mDialog);
            }
        });
    }


    private void ApiRemoveFavo(int providerID) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ResponseBody> call = apiService.DeleteFavourite(providerID);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(ProviderDetailActivity.this, mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        if (response.body().string().equalsIgnoreCase("true")) {
                            Toast.makeText(mContext, "Removed from favourites", Toast.LENGTH_LONG).show();
                            ApiFavList();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(ProviderDetailActivity.this, mDialog);
            }
        });
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

    @Override
    public void sendAddress(String address, int locId) {

        locationId = locId;
        tvLocation.setText(address);
        apiSettings_Details(uniqueId, providerId, locationId);
    }

    // Click actions of selected items in grid

    @Override
    public void onCheckInSelected(SearchService checinServiceInfo) {

        if (checinServiceInfo != null) {
            Intent intent = new Intent(ProviderDetailActivity.this, AppointmentActivity.class);
            intent.putExtra("uniqueID", uniqueId);
            intent.putExtra("providerName", tvSpName.getText().toString());
            intent.putExtra("locationId",locationId);
            ServiceInfo serviceInfo = new ServiceInfo();
            serviceInfo.setServiceId(checinServiceInfo.getId());
            serviceInfo.setServiceName(checinServiceInfo.getName());
            serviceInfo.setDescription(checinServiceInfo.getDescription());
            serviceInfo.setType(Constants.CHECKIN);
            if (checinServiceInfo.getConsumerNoteTitle() != null) {
                serviceInfo.setConsumerNoteTitle(checinServiceInfo.getConsumerNoteTitle());
            }
            serviceInfo.setIsPrePayment(String.valueOf(checinServiceInfo.isPrePayment()));
            serviceInfo.setLivetrack(String.valueOf(checinServiceInfo.isLivetrack()));
            if (checinServiceInfo.getMinPrePaymentAmount() != null) {
                serviceInfo.setMinPrePaymentAmount(checinServiceInfo.getMinPrePaymentAmount());
            }
            serviceInfo.setPreInfoEnabled(checinServiceInfo.isPreInfoEnabled());
            if (checinServiceInfo.getPreInfoText() != null) {
                serviceInfo.setPreInfoText(checinServiceInfo.getPreInfoText());
            }
            if (checinServiceInfo.getServiceType() != null) {
                serviceInfo.setServiceType(checinServiceInfo.getServiceType());
            }
            if (checinServiceInfo.getCallingMode() != null) {
                serviceInfo.setCallingMode(checinServiceInfo.getCallingMode());
            }
            if (checinServiceInfo.getCheckInServiceAvailability() != null) {
                serviceInfo.setToken(checinServiceInfo.getCheckInServiceAvailability().isShowToken());
                serviceInfo.setPeopleWaitingInLine(checinServiceInfo.getCheckInServiceAvailability().getPersonAhead());
                serviceInfo.setAvailableDate(checinServiceInfo.getCheckInServiceAvailability().getAvailableDate());
                if (checinServiceInfo.getCheckInServiceAvailability().getServiceTime() != null) {
                    serviceInfo.setTime(checinServiceInfo.getCheckInServiceAvailability().getServiceTime());
                }
                if (checinServiceInfo.getCheckInServiceAvailability().getQueueWaitingTime() != null) {
                    serviceInfo.setWaitingTime(checinServiceInfo.getCheckInServiceAvailability().getQueueWaitingTime());
                }
            }

            intent.putExtra("serviceInfo", serviceInfo);
            startActivity(intent);
        }
    }

    @Override
    public void onAppointmentSelected(SearchAppoinment appointmentServiceInfo) {

        if (appointmentServiceInfo != null) {
            Intent intent = new Intent(ProviderDetailActivity.this, AppointmentActivity.class);
            intent.putExtra("uniqueID", uniqueId);
            intent.putExtra("providerName", tvSpName.getText().toString());
            intent.putExtra("locationId",locationId);
            ServiceInfo serviceInfo = new ServiceInfo();
            serviceInfo.setServiceId(appointmentServiceInfo.getId());
            serviceInfo.setServiceName(appointmentServiceInfo.getName());
            serviceInfo.setDescription(appointmentServiceInfo.getDescription());
            serviceInfo.setType(Constants.APPOINTMENT);
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
            if (appointmentServiceInfo.getCallingMode() != null) {
                serviceInfo.setCallingMode(appointmentServiceInfo.getCallingMode());
            }
            if (appointmentServiceInfo.getAppointServiceAvailability() != null){

                serviceInfo.setAvailableDate(appointmentServiceInfo.getAppointServiceAvailability().getNextAvailableDate());
                serviceInfo.setTime(appointmentServiceInfo.getAppointServiceAvailability().getNextAvailable());
            }

            intent.putExtra("serviceInfo", serviceInfo);

            startActivity(intent);
        }
    }

    @Override
    public void onProviderSelected(ProviderUserModel providerInfo) {

        Intent providerIntent = new Intent(ProviderDetailActivity.this, UserDetailActivity.class);
        providerIntent.putExtra("uniqueID", uniqueId);
        providerIntent.putExtra("locationId", locationId);
        providerIntent.putExtra("providerInfo", providerInfo);
        providerIntent.putExtra("locationName", tvLocationName.getText().toString());
        providerIntent.putExtra("isToken", isToken);
        startActivity(providerIntent);

    }

    @Override
    public void onDonationSelected(SearchDonation donationServiceInfo) {

        Intent intent = new Intent(ProviderDetailActivity.this, AppointmentActivity.class);
        intent.putExtra("uniqueID", uniqueId);
        intent.putExtra("providerName", tvSpName.getText().toString());
        startActivity(intent);
    }
}