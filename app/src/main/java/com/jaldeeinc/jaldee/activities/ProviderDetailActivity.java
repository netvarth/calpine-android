package com.jaldeeinc.jaldee.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.jaldeeinc.jaldee.BuildConfig;
import com.jaldeeinc.jaldee.Interface.ISelectedService;
import com.jaldeeinc.jaldee.Interface.ISendMessage;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.EmailsAdapter;
import com.jaldeeinc.jaldee.adapter.LocationCheckinAdapter;
import com.jaldeeinc.jaldee.adapter.MainServicesAdapter;
import com.jaldeeinc.jaldee.adapter.ParkingModel;
import com.jaldeeinc.jaldee.adapter.PhoneNumbersAdapter;
import com.jaldeeinc.jaldee.adapter.SectionRecyclerViewAdapter;
import com.jaldeeinc.jaldee.adapter.ServicesAdapter;
import com.jaldeeinc.jaldee.adapter.SpecialisationAdapter;
import com.jaldeeinc.jaldee.adapter.VirtualFieldAdapter;
import com.jaldeeinc.jaldee.callback.LocationCheckinCallback;
import com.jaldeeinc.jaldee.callback.SearchLocationAdpterCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.Contents;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.CustomTypefaceSpan;
import com.jaldeeinc.jaldee.custom.EnquiryDialog;
import com.jaldeeinc.jaldee.custom.IGetSelectedLocation;
import com.jaldeeinc.jaldee.custom.LocationAmenitiesDialog;
import com.jaldeeinc.jaldee.custom.LocationsDialog;
import com.jaldeeinc.jaldee.custom.QRCodeEncoder;
import com.jaldeeinc.jaldee.custom.ResizableCustomView;
import com.jaldeeinc.jaldee.model.ProviderUserModel;
import com.jaldeeinc.jaldee.response.Catalog;
import com.jaldeeinc.jaldee.response.DepServiceInfo;
import com.jaldeeinc.jaldee.response.DepartmentInfo;
import com.jaldeeinc.jaldee.response.FavouriteModel;
import com.jaldeeinc.jaldee.response.OrderResponse;
import com.jaldeeinc.jaldee.response.ProfilePicture;
import com.jaldeeinc.jaldee.response.Provider;
import com.jaldeeinc.jaldee.response.QueueList;
import com.jaldeeinc.jaldee.response.ScheduleList;
import com.jaldeeinc.jaldee.response.SearchAWsResponse;
import com.jaldeeinc.jaldee.response.SearchAppoinment;
import com.jaldeeinc.jaldee.response.SearchAppointmentDepartmentServices;
import com.jaldeeinc.jaldee.response.SearchCheckInMessage;
import com.jaldeeinc.jaldee.response.SearchDepartmentServices;
import com.jaldeeinc.jaldee.response.SearchDonation;
import com.jaldeeinc.jaldee.response.SearchLocation;
import com.jaldeeinc.jaldee.response.SearchService;
import com.jaldeeinc.jaldee.response.SearchSetting;
import com.jaldeeinc.jaldee.response.SearchTerminology;
import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.jaldeeinc.jaldee.response.SearchVirtualFields;
import com.jaldeeinc.jaldee.response.ServiceInfo;
import com.jaldeeinc.jaldee.widgets.CustomDialog;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jaldeeinc.jaldee.connection.ApiClient.context;

public class ProviderDetailActivity extends AppCompatActivity implements IGetSelectedLocation, ISelectedService, ISendMessage {

    @BindView(R.id.tv_spLanguagesKnown)
    CustomTextViewMedium tvSpLanguagesKnown;

    @BindView(R.id.tv_languagesKnownHint)
    CustomTextViewMedium tvLanguagesKnownHint;

    @BindView(R.id.tv_spName)
    CustomTextViewBold tvSpName;

    @BindView(R.id.iv_verified)
    ImageView ivVerified;

    @BindView(R.id.tv_spSpecialization)
    CustomTextViewMedium tvSpeciality;

    @BindView(R.id.tv_locationName)
    CustomTextViewMedium tvLocationName;

    @BindView(R.id.tv_specializations)
    CustomTextViewMedium tvSpecializations;

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

    @BindView(R.id.cv_coupon)
    CardView cvCoupon;

    @BindView(R.id.iv_fav)
    ImageView ivfav;

    @BindView(R.id.iv_share)
    ImageView ivShare;

    @BindView(R.id.ll_noSlots)
    LinearLayout llNoSlots;

    @BindView(R.id.checkinsList)
    CustomTextViewMedium tv_checkinsList;

    @BindView(R.id.tv_contactDetails)
    CustomTextViewMedium tvContactDetails;

    @BindView(R.id.rv_phoneNumbers)
    RecyclerView rvPhoneNumbers;

    @BindView(R.id.rv_emails)
    RecyclerView rvEmails;

    @BindView(R.id.ll_phone)
    LinearLayout llPhone;

    @BindView(R.id.ll_email)
    LinearLayout llEmail;

    @BindView(R.id.ll_socialMedia)
    LinearLayout llSocialMedia;

    @BindView(R.id.iv_facebook)
    ImageView ivFacebook;

    @BindView(R.id.iv_youtube)
    ImageView ivYoutube;

    @BindView(R.id.iv_gPlus)
    ImageView ivGooglePlus;

    @BindView(R.id.iv_twitter)
    ImageView ivTwitter;

    @BindView(R.id.iv_linkedIn)
    ImageView ivLinkedIn;

    @BindView(R.id.iv_pintrest)
    ImageView ivPintrest;

    @BindView(R.id.iv_bizyglobe)
    ImageView ivBizyglobe;

    @BindView(R.id.iv_website)
    ImageView ivWebsite;

    @BindView(R.id.iv_instagram)
    ImageView ivInstagram;

    @BindView(R.id.tv_socialMedia)
    CustomTextViewMedium tvSocialMedia;

    @BindView(R.id.tv_aboutUsHint)
    CustomTextViewMedium tvAboutUsHint;

    @BindView(R.id.tv_providerName)
    CustomTextViewBold tvProviderName;

    @BindView(R.id.ll_amenities)
    LinearLayout llAmenities;

    @BindView(R.id.cv_qr)
    CardView cvQr;


    String claimable;
    private int uniqueId;
    private int providerId;
    private int locationId;
    String accountType = "";
    boolean isToken;
    private String sharingId = "";
    private boolean onlinePresence = false;
    private boolean orderEnabled = false;
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
    String mFrom;
    boolean isCheckInEnabled = false;
    boolean isApptEnabled = false;
    ArrayList<SearchCheckInMessage> mSearchmCheckListShow = new ArrayList<>();
    ArrayList<SearchCheckInMessage> mSearchmCheckMessageList = new ArrayList<>();
    String location;
    LocationCheckinCallback callback;
    SearchTerminology mSearchTerminology;
    String terminology;
    String userTerminology;
    private SearchLocationAdpterCallback adaptercallback;
    private String location_Id;
    private String place;
    private OrderResponse orderResponse = new OrderResponse();
    private LocationAmenitiesDialog locationAmenitiesDialog;
    private Provider providerResponse = new Provider();
    ArrayList<SearchDonation> donationServices = new ArrayList<>();

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
            mFrom = intent.getStringExtra("from");
            if (mFrom != null && mFrom.equalsIgnoreCase("fav")) {
                uniqueId = intent.getIntExtra("uniqueID", 0);
            } else if (mFrom != null && mFrom.equalsIgnoreCase("order")) {
                uniqueId = intent.getIntExtra("uniqueID", 0);
            } else {
                uniqueId = Integer.parseInt(intent.getStringExtra("uniqueID"));
            }
            claimable = intent.getStringExtra("claimable");
            claimable = "0";
            orderEnabled = intent.getBooleanExtra("isOrderEnabled", false);
            if (mFrom != null && mFrom.equalsIgnoreCase("checkin")) {
                location_Id = intent.getStringExtra("locationId");
                place = intent.getStringExtra("place");
            } else {
                locationId = intent.getIntExtra("locationId", 0);
            }
        }

        //more details recyclerView
        mRecycle_virtualfield = findViewById(R.id.mrecycle_virtualfield);

        rvServices = findViewById(R.id.rv_services);
        gridLayoutManager = new GridLayoutManager(this, 2);
        rvServices.setLayoutManager(gridLayoutManager);
        mainServicesAdapter = new MainServicesAdapter(serviceInfoList, this, true, iSelectedService, mBusinessDataList);
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
                    tvMoreInfo.setText("less info");
//                    int size = domainVirtual.size();
//                    if (size > 0) {
//                        llMore.setVisibility(View.VISIBLE);
//                    } else {
//                        llMore.setVisibility(View.GONE);
//                    }
                } else {
                    llMore.setVisibility(View.GONE);
                    tvMoreInfo.setText("more info");

                }
            }
        });


        cvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sharingId != null) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/html");
                    String shareBody = Constants.URL + sharingId;
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "ServiceProvider details");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
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

                if (mBusinessDataList != null) {

                    Intent intent = new Intent(ProviderDetailActivity.this, ChatActivity.class);
                    intent.putExtra("from", Constants.PROVIDER);
                    intent.putExtra("accountId", mBusinessDataList.getId());
                    intent.putExtra("name", mBusinessDataList.getBusinessName());
                    startActivity(intent);

                } else {

                    Toast.makeText(ProviderDetailActivity.this, "Please try after some time", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cvCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent iCoupons = new Intent(v.getContext(), CouponActivity.class);
                iCoupons.putExtra("uniqueID", String.valueOf(uniqueId));
                iCoupons.putExtra("accountId", String.valueOf(mBusinessDataList.getId()));
                startActivity(iCoupons);

            }
        });
        cvQr.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (sharingId != null) {
                    //Encode with a QR Code image
                    String shareBody = Constants.URL + sharingId;
                    //Find screen size
                    WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                    Display display = manager.getDefaultDisplay();
                    Point point = new Point();
                    display.getSize(point);
                    int width = point.x;
                    int height = point.y;
                    int smallerDimension = width < height ? width : height;
                    smallerDimension = smallerDimension * 3 / 4;
                    QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(shareBody,
                            null,
                            Contents.Type.TEXT,
                            BarcodeFormat.QR_CODE.toString(), smallerDimension);
                    try {
                        Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();

                        Dialog settingsDialog = new Dialog(ProviderDetailActivity.this);
                        settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.qr_code_view, null));
                        ImageView ivQR = settingsDialog.findViewById(R.id.iv_Qr);
                        CustomTextViewSemiBold tvProvidername = settingsDialog.findViewById(R.id.tv_providerName);
                        tvProvidername.setText(mBusinessDataList.getBusinessName());
                        CustomTextViewMedium tvProviderSpecialization = settingsDialog.findViewById(R.id.tv_provider_specialization);
                        tvProviderSpecialization.setText(mBusinessDataList.getServiceSubSector().getDisplayName());
                        RelativeLayout rlClose = settingsDialog.findViewById(R.id.rl_close);
                        FrameLayout qr_card = settingsDialog.findViewById(R.id.qr_card);
                        ImageView avatar = settingsDialog.findViewById(R.id.profile_picture);
                        ImageView ivShare = settingsDialog.findViewById(R.id.iv_share);
                        ivShare.setVisibility(View.GONE);
                        //LinearLayout ll_qr = settingsDialog.findViewById(R.id.ll_qr);
                        //RelativeLayout rl_close = settingsDialog.findViewById(R.id.rl_close);

                        if (mBusinessDataList.getLogo() != null) {
                            Glide.with(ProviderDetailActivity.this)
                                    .load(mBusinessDataList.getLogo().getUrl())
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .apply(new RequestOptions().error(R.drawable.icon_noimage).circleCrop())
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            //on load failed
                                            avatar.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_noimage));
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            return false;
                                        }
                                    })
                                    .into(avatar);
                        }
                        ivShare.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // save bitmap to cache directory
                                try {
                                    ivShare.setImageResource(R.drawable.jaldeeblue);
                                    Bitmap bitmap1 = getBitmapFromView(qr_card);
                                    ivShare.setImageResource(R.drawable.icon_share);

                                    File cachePath = new File(context.getCacheDir(), "images");
                                    cachePath.mkdirs(); // don't forget to make the directory
                                    FileOutputStream stream = new FileOutputStream(new File(cachePath, "image.png")); // overwrites this image every time
                                    bitmap1.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    stream.close();

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                File imagePath = new File(context.getCacheDir(), "images");
                                File newFile = new File(imagePath, "image.png");
                                Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", newFile);

                                if (contentUri != null) {

                                    Intent shareIntent = new Intent();
                                    shareIntent.setAction(Intent.ACTION_SEND);
                                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                                    shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                                    String shareBody = Constants.URL + sharingId;
                                    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "ServiceProvider details");
                                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                                    startActivity(Intent.createChooser(shareIntent, "share via"));

                                }
                            }
                        });
                        rlClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                settingsDialog.dismiss();
                            }
                        });

                        ivQR.setImageBitmap(bitmap);
                        settingsDialog.show();


                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        ApiGetProviderDetails(uniqueId);

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
                        Config.closeDialog(ProviderDetailActivity.this, mDialog);
                    Config.logV("URL----VIRTUAL---8888--------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------VIRTUAL-------------------" + response.code());
                    if (response.code() == 200) {
                        providerResponse = response.body();
                        if (providerResponse != null) {

                            apiVirtualFields(providerResponse.getVirtualFields());

                            ApiSearchViewLocation(providerResponse.getLocation(), providerResponse.getBusinessProfile(), providerResponse);

                            apiSearchViewTerminology(providerResponse.getTerminologies());

                        } else {

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
                    Config.closeDialog(ProviderDetailActivity.this, mDialog);
            }
        });

    }

    private void apiVirtualFields(String virtFields) {

        if (virtFields != null) {

            String vFields = virtFields;
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
            tvMoreInfo.setVisibility(View.GONE);
            updateContactInfo();
        }

    }

    private void ApiSearchViewLocation(String loc, SearchViewDetail businessProfile, Provider providerResponse) {

        try {

            mSearchLocList = new Gson().fromJson(loc, new TypeToken<ArrayList<SearchLocation>>() {
            }.getType());

            if (mSearchLocList != null) {

                if (mSearchLocList.size() > 1) {
                    rlLocation.setVisibility(View.VISIBLE);
                    tvLocation.setText(mSearchLocList.get(0).getAddress());
                    tvChangeLocation.setVisibility(View.VISIBLE);

                } else {
                    rlLocation.setVisibility(View.GONE);
                }

                if (mFrom != null && mFrom.equalsIgnoreCase("checkin")) {
                    locationId = Integer.parseInt(location_Id);
                    location = place;
                    for (int i = 0; i < mSearchLocList.size(); i++) {
                        if (locationId == mSearchLocList.get(i).getId()) {
                            tvLocation.setText(mSearchLocList.get(i).getAddress());
                        }
                    }

                } else {
                    locationId = mSearchLocList.get(0).getId();
                    location = mSearchLocList.get(0).getPlace();
                }


                apiSearchViewDetail(businessProfile, providerResponse);

            } else {
                rlLocation.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void apiSearchViewDetail(SearchViewDetail businessProf, Provider providerResponse) {

        try {

            mBusinessDataList = businessProf;

            if (mBusinessDataList != null) {
                accountType = mBusinessDataList.getAccountType();

                if (mBusinessDataList.getCustomId() != null) {
                    sharingId = mBusinessDataList.getCustomId();
                } else if (mBusinessDataList.getAccEncUid() != null) {
                    sharingId = mBusinessDataList.getAccEncUid();
                } else if (mBusinessDataList.getAccEncUid() != null) {
                    sharingId = mBusinessDataList.getAccEncUid();
                } else {
                    sharingId = mBusinessDataList.getUniqueId();
                }

                if (mBusinessDataList.getId() != 0) {
                    providerId = mBusinessDataList.getId();
                }
                onlinePresence = mBusinessDataList.isOnlinePresence();
                onlinePresence = true;   //businessprofile .Json is not getting updated correctly, so we don't need to check this condition..that's why setting it as true.
                UpdateMainUI(mBusinessDataList);
                apiSearchGallery(providerResponse.getGalleryList());
                apiSettings_Details(uniqueId, providerId, locationId, location);
                // check if the provider is a favourite
                ApiFavList();


            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    ArrayList<ParkingModel> listType = new ArrayList<>();

    private void handleLocationAmenities(SearchLocation searchLoclist) {

        listType = new ArrayList<>();
        if (searchLoclist.getParkingType() != null) {
            if (searchLoclist.getParkingType().equalsIgnoreCase("free") || searchLoclist.getParkingType().equalsIgnoreCase("valet") || searchLoclist.getParkingType().equalsIgnoreCase("street") || searchLoclist.getParkingType().equalsIgnoreCase("privatelot") || searchLoclist.getParkingType().equalsIgnoreCase("paid")) {
                ParkingModel mType = new ParkingModel();
                mType.setId("1");
                mType.setTypename(Config.toTitleCase(searchLoclist.getParkingType()) + " Parking ");
                listType.add(mType);
            }
        }
        if (searchLoclist.getLocationVirtualFields() != null) {
            if (searchLoclist.getLocationVirtualFields().getDocambulance() != null) {
                if (searchLoclist.getLocationVirtualFields().getDocambulance().equalsIgnoreCase("true")) {
                    ParkingModel mType = new ParkingModel();
                    mType.setId("4");
                    mType.setTypename("Ambulance");
                    listType.add(mType);
                }
            }
            if (searchLoclist.getLocationVirtualFields().getFirstaid() != null) {
                if (searchLoclist.getLocationVirtualFields().getFirstaid().equalsIgnoreCase("true")) {
                    ParkingModel mType = new ParkingModel();
                    mType.setId("5");
                    mType.setTypename("First Aid");
                    listType.add(mType);
                }
            }
            if (searchLoclist.getLocationVirtualFields().getTraumacentre() != null) {
                if (searchLoclist.getLocationVirtualFields().getTraumacentre().equalsIgnoreCase("true")) {
                    ParkingModel mType = new ParkingModel();
                    mType.setId("7");
                    mType.setTypename("Trauma");
                    listType.add(mType);
                }
            }
            if (searchLoclist.getLocationVirtualFields().getPhysiciansemergencyservices() != null) {
                if (searchLoclist.getLocationVirtualFields().getPhysiciansemergencyservices().equalsIgnoreCase("true")) {
                    ParkingModel mType = new ParkingModel();
                    mType.setId("3");
                    mType.setTypename("Emergency");
                    listType.add(mType);
                }
            }
            if (searchLoclist.getLocationVirtualFields().getHosemergencyservices() != null) {
                if (searchLoclist.getLocationVirtualFields().getHosemergencyservices().equalsIgnoreCase("true")) {
                    ParkingModel mType = new ParkingModel();
                    mType.setId("3");
                    mType.setTypename("Emergency");
                    listType.add(mType);
                }
            }
            if (searchLoclist.getLocationVirtualFields().getDentistemergencyservices() != null) {
                if (searchLoclist.getLocationVirtualFields().getDentistemergencyservices().equalsIgnoreCase("true")) {
                    ParkingModel mType = new ParkingModel();
                    mType.setId("6");
                    mType.setTypename("Emergency");
                    listType.add(mType);
                }
            }
        }
        try {
            if (searchLoclist.isOpen24hours()) {
                ParkingModel mType = new ParkingModel();
                mType.setId("2");
                mType.setTypename("24 Hours");
                listType.add(mType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listType.size() > 0) {
            Config.logV("Location Ament---------------" + listType.size());

            llAmenities.setVisibility(View.VISIBLE);
            llAmenities.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    locationAmenitiesDialog = new LocationAmenitiesDialog(mContext, listType);
                    locationAmenitiesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    locationAmenitiesDialog.show();
                    DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                    int width = (int) (metrics.widthPixels * 1);
                    locationAmenitiesDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

                }
            });

        } else {
            llAmenities.setVisibility(View.GONE);
        }


    }

    private void apiSearchGallery(ArrayList<SearchViewDetail> mSearchGallery) {

        try {

            if (mSearchGallery != null) {
                UpdateGallery(mSearchGallery);
            } else {
                tv_mImageViewTextnew.setVisibility(View.GONE);
                if (mBusinessDataList.getLogo() != null) {
                    Glide.with(ProviderDetailActivity.this)
                            .load(mBusinessDataList.getLogo().getUrl())
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .apply(new RequestOptions().error(R.drawable.icon_noimage).circleCrop())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    //on load failed
                                    ivSpImage.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_noimage));
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    //on load success

                                    return false;
                                }
                            })
                            .into(ivSpImage);
//                            PicassoTrustAll.getInstance(mContext).load(mBusinessDataList.getLogo().getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(ivSpImage);
                    ivSpImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ArrayList<String> mGalleryList = new ArrayList<>();
                            if (mBusinessDataList.getLogo() != null) {
                                mGalleryList.add(mBusinessDataList.getLogo().getUrl());
                            }

                            boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryList, view.getContext());
                            if (mValue) {
                                Intent intent = new Intent(ProviderDetailActivity.this, SwipeGalleryImage.class);
                                intent.putExtra("pos", 0);
                                startActivity(intent);
                            }
                        }
                    });

                } else {
                    tv_mImageViewTextnew.setVisibility(View.GONE);
                }

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
                tvSpName.setText(name);
            }

            if (mBusinessDataList.getBusinessUserName() != null) {

                tvProviderName.setVisibility(View.VISIBLE);
                tvProviderName.setText(mBusinessDataList.getBusinessUserName());
            } else {
                tvProviderName.setVisibility(View.GONE);
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

                    //tvLocationName.setText(mBusinessDataList.getBaseLocation().getPlace());
                    tvLocationName.setText(location);

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
                    tvSubDomainHint.setVisibility(View.GONE);
                } else {
                    tvSubDomainHint.setVisibility(View.VISIBLE);
                    tvSpeciality.setVisibility(View.VISIBLE);
                    tvSpeciality.setText(mBusinessDataList.getServiceSubSector().getDisplayName());
                }
            }

            // Languages Known
            if (mBusinessDataList.getLanguagesSpoken() != null && !mBusinessDataList.getLanguagesSpoken().isEmpty()) {
                tvSpLanguagesKnown.setVisibility(View.VISIBLE);
                tvLanguagesKnownHint.setVisibility(View.VISIBLE);
                ArrayList<String> language = new ArrayList<String>();
                for (String value : mBusinessDataList.getLanguagesSpoken()) {
                    language.add(value.substring(0, 1).toUpperCase() + value.substring(1));
                }
                tvSpLanguagesKnown.setText(language.toString().replace("[", "").replace("]", ""));
            } else {
                tvSpLanguagesKnown.setVisibility(View.GONE);
                tvLanguagesKnownHint.setVisibility(View.GONE);
            }

            // About

            if (mBusinessDataList.getBusinessDesc() != null) {
                tvAboutUsHint.setVisibility(View.VISIBLE);
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
                tvAboutUsHint.setVisibility(View.GONE);
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

                    tvSpecializations.setVisibility(View.VISIBLE);
                    tvSpOne.setVisibility(View.VISIBLE);

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
                } else {

                    if (mBusinessDataList.getServiceSubSector() != null && mBusinessDataList.getServiceSubSector().getDisplayName() != null) {
                        if (!mBusinessDataList.getServiceSubSector().getDisplayName().equalsIgnoreCase("Miscellaneous")) {
                            tvSpecializations.setVisibility(View.VISIBLE);
                            tvSpOne.setVisibility(View.VISIBLE);
                            tvSpOne.setText(mBusinessDataList.getServiceSubSector().getDisplayName());
                        } else {
                            tvSpecializations.setVisibility(View.GONE);
                            tvSpOne.setVisibility(View.GONE);
                        }
                    }
                }

            } else {

                if (mBusinessDataList.getServiceSubSector() != null && mBusinessDataList.getServiceSubSector().getDisplayName() != null) {
                    if (!mBusinessDataList.getServiceSubSector().getDisplayName().equalsIgnoreCase("Miscellaneous")) {
                        tvSpecializations.setVisibility(View.VISIBLE);
                        tvSpOne.setVisibility(View.VISIBLE);
                        tvSpOne.setText(mBusinessDataList.getServiceSubSector().getDisplayName());
                    } else {
                        tvSpecializations.setVisibility(View.GONE);
                        tvSpOne.setVisibility(View.GONE);
                    }
                }
            }

            updateSocialMedia();
            updateContactInfo();

        }

    }

    private void updateSocialMedia() {

        if (mBusinessDataList.getSocialMedia() != null) {
            if (mBusinessDataList.getSocialMedia().size() > 0) {
                llSocialMedia.setVisibility(View.VISIBLE);
                tvSocialMedia.setVisibility(View.VISIBLE);
                for (int i = 0; i < mBusinessDataList.getSocialMedia().size(); i++) {
                    if (mBusinessDataList.getSocialMedia().get(i).getResource().equalsIgnoreCase("facebook")) {
//                        tv_SocialMedia.setVisibility(View.VISIBLE);
                        ivFacebook.setVisibility(View.VISIBLE);
                        final int finalI = i;
                        ivFacebook.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mBusinessDataList.getSocialMedia().get(finalI).getValue()));
                                    startActivity(myIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(mContext, "No application can handle this request."
                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    if (mBusinessDataList.getSocialMedia().get(i).getResource().equalsIgnoreCase("googleplus")) {
//                        tv_SocialMedia.setVisibility(View.VISIBLE);
                        ivGooglePlus.setVisibility(View.VISIBLE);
                        final int finalI3 = i;
                        ivGooglePlus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mBusinessDataList.getSocialMedia().get(finalI3).getValue()));
                                    startActivity(myIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(mContext, "No application can handle this request."
                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    if (mBusinessDataList.getSocialMedia().get(i).getResource().equalsIgnoreCase("twitter")) {
//                        tv_SocialMedia.setVisibility(View.VISIBLE);
                        ivTwitter.setVisibility(View.VISIBLE);
                        final int finalI1 = i;
                        ivTwitter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mBusinessDataList.getSocialMedia().get(finalI1).getValue()));
                                    startActivity(myIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(mContext, "No application can handle this request."
                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    if (mBusinessDataList.getSocialMedia().get(i).getResource().equalsIgnoreCase("linkedin")) {
//                        tv_SocialMedia.setVisibility(View.VISIBLE);
                        ivLinkedIn.setVisibility(View.VISIBLE);
                        final int finalI5 = i;
                        ivLinkedIn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mBusinessDataList.getSocialMedia().get(finalI5).getValue()));
                                    startActivity(myIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(mContext, "No application can handle this request."
                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    if (mBusinessDataList.getSocialMedia().get(i).getResource().equalsIgnoreCase("pinterest")) {
//                        tv_SocialMedia.setVisibility(View.VISIBLE);
                        ivPintrest.setVisibility(View.VISIBLE);
                        final int finalI4 = i;
                        ivPintrest.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mBusinessDataList.getSocialMedia().get(finalI4).getValue()));
                                    startActivity(myIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(mContext, "No application can handle this request."
                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    if (mBusinessDataList.getSocialMedia().get(i).getResource().equalsIgnoreCase("youtube")) {
//                        tv_SocialMedia.setVisibility(View.VISIBLE);
                        ivYoutube.setVisibility(View.VISIBLE);
                        final int finalI2 = i;
                        ivYoutube.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mBusinessDataList.getSocialMedia().get(finalI2).getValue()));
                                    startActivity(myIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(mContext, "No application can handle this request."
                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    if (mBusinessDataList.getSocialMedia().get(i).getResource().equalsIgnoreCase("instagram")) {
//                        tv_SocialMedia.setVisibility(View.VISIBLE);
                        ivInstagram.setVisibility(View.VISIBLE);
                        final int finalI4 = i;
                        ivInstagram.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mBusinessDataList.getSocialMedia().get(finalI4).getValue()));
                                    startActivity(myIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(mContext, "No application can handle this request."
                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    if (mBusinessDataList.getSocialMedia().get(i).getResource().equalsIgnoreCase("bizyGlobe")) {
//                        tv_SocialMedia.setVisibility(View.VISIBLE);
                        ivBizyglobe.setVisibility(View.VISIBLE);
                        final int finalI4 = i;
                        ivBizyglobe.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mBusinessDataList.getSocialMedia().get(finalI4).getValue()));
                                    startActivity(myIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(mContext, "No application can handle this request."
                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    if (mBusinessDataList.getSocialMedia().get(i).getResource().equalsIgnoreCase("website")) {
//                        tv_SocialMedia.setVisibility(View.VISIBLE);
                        ivWebsite.setVisibility(View.VISIBLE);
                        final int finalI4 = i;
                        ivWebsite.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mBusinessDataList.getSocialMedia().get(finalI4).getValue()));
                                    startActivity(myIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(mContext, "No application can handle this request."
                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            } else {
                llSocialMedia.setVisibility(View.GONE);
                tvSocialMedia.setVisibility(View.GONE);
            }
        } else {
            llSocialMedia.setVisibility(View.GONE);
            tvSocialMedia.setVisibility(View.GONE);
        }
    }

    public void updateContactInfo() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProviderDetailActivity.this);
        PhoneNumbersAdapter phoneNumbersAdapter;
        EmailsAdapter emailsAdapter;

        if (mBusinessDataList.getPhoneNumbers() != null || mBusinessDataList.getEmails() != null) {
            tvMoreInfo.setVisibility(View.VISIBLE);

            if (mBusinessDataList.getPhoneNumbers() != null && mBusinessDataList.getPhoneNumbers().size() > 0) {
                llPhone.setVisibility(View.VISIBLE);
                rvPhoneNumbers.setVisibility(View.VISIBLE);
                tvContactDetails.setVisibility(View.VISIBLE);
                rvPhoneNumbers.setLayoutManager(linearLayoutManager);
                phoneNumbersAdapter = new PhoneNumbersAdapter(mBusinessDataList.getPhoneNumbers(), ProviderDetailActivity.this);
                rvPhoneNumbers.setAdapter(phoneNumbersAdapter);

            } else {
                llPhone.setVisibility(View.GONE);
                rvPhoneNumbers.setVisibility(View.GONE);
            }

            if (mBusinessDataList.getEmails() != null && mBusinessDataList.getEmails().size() > 0) {

                llEmail.setVisibility(View.VISIBLE);
                tvContactDetails.setVisibility(View.VISIBLE);
                rvEmails.setVisibility(View.VISIBLE);
                rvEmails.setLayoutManager(new LinearLayoutManager(this));
                emailsAdapter = new EmailsAdapter(mBusinessDataList.getEmails(), ProviderDetailActivity.this);
                rvEmails.setAdapter(emailsAdapter);

            } else {
                llEmail.setVisibility(View.GONE);
                rvEmails.setVisibility(View.GONE);
            }
        } else {

            tvMoreInfo.setVisibility(View.GONE);
            tvContactDetails.setVisibility(View.GONE);
        }
    }

    public void UpdateGallery(final ArrayList<SearchViewDetail> mGallery) {
        //  Picasso.with(this).load(mGallery.get(0).getUrl()).fit().into(mImgeProfile);
        Config.logV("Gallery--------------333-----" + mGallery.size());
        try {
            if (mGallery.size() > 0 || mBusinessDataList.getLogo() != null) {
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
            } else {

            }
            Config.logV("Bussiness logo @@@@@@@@@@" + mBusinessDataList.getLogo());
            if (mBusinessDataList.getLogo() != null) {
                String url = mBusinessDataList.getLogo().getUrl();
                url = url.replaceAll(" ", "%20");
                String finalUrl = url;

                Glide.with(ProviderDetailActivity.this)
                        .load(finalUrl)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .apply(new RequestOptions().error(R.drawable.icon_noimage).circleCrop())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                //on load failed
                                ivSpImage.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_noimage));
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                //on load success

                                return false;
                            }
                        })
                        .into(ivSpImage);

            } else {
                //Toast.makeText(mContext, "There is no Profile Pic", Toast.LENGTH_SHORT).show();
                // Picasso.with(mContext).load(mGallery.get(0).getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(mImgeProfile);
                ivSpImage.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_noimage));

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

    private void apiSettings_Details(int uniqueId, int mProviderId, final int mlocationId, String location) {

        try {

            if (providerResponse.getSettings() != null) {

                String settings = providerResponse.getSettings();
                mSearchSettings = new Gson().fromJson(settings, SearchSetting.class);
                rvServices.setVisibility(View.VISIBLE);
                if (mSearchSettings != null) {

                    if (mSearchLocList != null && mSearchLocList.size() > 0) {
                        for (int i = 0; i < mSearchLocList.size(); i++) {
                            if (mlocationId == mSearchLocList.get(i).getId()) {
                                handleLocationAmenities(mSearchLocList.get(i));
                            }
                        }
                    }

                    // to check if the provider has order enabled, if it is order enabled
                    checkOrderEnabled(mSearchSettings, uniqueId, mProviderId, mlocationId);
                }

            } else {

                rvServices.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void checkOrderEnabled(SearchSetting mSearchSettings, int uniqueId, int mProviderId, int mlocationId) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<OrderResponse> call = apiService.getOrderEnabledStatus(mProviderId);
        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                try {

                    Config.logV("URL--7777-------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------Setting-------------------" + response.code());
                    if (response.code() == 200) {

                        orderResponse = response.body();

                        if (orderResponse != null) {
                            orderEnabled = orderResponse.isOrderEnabled();
                        }

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

                            // to parse donation services

                            if (providerResponse.getDonationServices() != null) {
                                donationServices = new Gson().fromJson(providerResponse.getDonationServices(), new TypeToken<ArrayList<SearchDonation>>() {
                                }.getType());
                            }
                            getOnlyServices(uniqueId, mlocationId, mProviderId);
                        }
                        ApiCheckInMessage(mlocationId, location);

                    } else {

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

                            // to parse donation services
                            if (providerResponse.getDonationServices() != null) {
                                donationServices = new Gson().fromJson(providerResponse.getDonationServices(), new TypeToken<ArrayList<SearchDonation>>() {
                                }.getType());
                            }
                            getOnlyServices(uniqueId, mlocationId, mProviderId);
                        }
                        ApiCheckInMessage(mlocationId, location);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
            }
        });

    }


    private void getProviderWithDepartments(int uniqueId, int mProviderId, int mlocationId) {


        try {
            if (providerResponse.getDepartmentProviders() != null) {

                String json = new Gson().toJson(providerResponse.getDepartmentProviders());
                ArrayList<SearchDepartmentServices> outputList = new Gson().fromJson(json, new TypeToken<ArrayList<SearchDepartmentServices>>() {
                }.getType());
                departmentProviders = outputList;

            }

            if (providerResponse.getDonationServices() != null) {
                donationServices = new Gson().fromJson(providerResponse.getDonationServices(), new TypeToken<ArrayList<SearchDonation>>() {
                }.getType());
            }

            if (departmentProviders != null) {

                getDepartmentsWithCheckInServices(uniqueId, mProviderId, mlocationId);

            } else {

                getDepartmentsWithCheckInServices(uniqueId, mProviderId, mlocationId);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getProvidersNew(int uniqueId, int mProviderId, int mlocationId) {


        try {

            if (providerResponse.getDepartmentProviders() != null) {
                String json = new Gson().toJson(providerResponse.getDepartmentProviders());
                ArrayList<ProviderUserModel> outputList = new Gson().fromJson(json, new TypeToken<ArrayList<ProviderUserModel>>() {
                }.getType());
                providersList = outputList;
            }

            if (providerResponse.getDonationServices() != null) {
                donationServices = new Gson().fromJson(providerResponse.getDonationServices(), new TypeToken<ArrayList<SearchDonation>>() {
                }.getType());
            }

            if (providersList != null) {

                apiGetProviders(uniqueId, mProviderId, mlocationId);

            } else {

                apiGetProviders(uniqueId, mProviderId, mlocationId);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getOnlyServices(int unqId, int locid, int provid) {


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
            if (orderEnabled) {  // if order manager is enabled then making an additional api call to get catalogs

                requests.add(apiService.getCheckInsSchedule(provid + "-" + locid));
                requests.add(apiService.getAppointmentSchedule(provid + "-" + locid));
                requests.add(apiService.getCheckInServices(locid));
                requests.add(apiService.getAppointmentServices(locid));
                requests.add(apiService.getCatalog(provid));

            } else {
                requests.add(apiService.getCheckInsSchedule(provid + "-" + locid));
                requests.add(apiService.getAppointmentSchedule(provid + "-" + locid));
                requests.add(apiService.getCheckInServices(locid));
                requests.add(apiService.getAppointmentServices(locid));
            }

            // Zip all requests with the Function, which will receive the results.
            Observable.zip(requests, new Function<Object[], Object>() {
                @Override
                public Object apply(Object[] objects) throws Exception {
                    // Objects[] is an array of combined results of completed requests
                    ArrayList<QueueList> queueList = new ArrayList<>();
                    ArrayList<ScheduleList> schedulesList = new ArrayList<>();
                    ArrayList<SearchService> checkInServList = new ArrayList<>();
                    ArrayList<SearchAppoinment> apptServicesList = new ArrayList<>();
                    ArrayList<Catalog> catalogs = new ArrayList<>();
                    if (orderEnabled) {

                        queueList = (ArrayList<QueueList>) objects[0];
                        schedulesList = (ArrayList<ScheduleList>) objects[1];
                        checkInServList = (ArrayList<SearchService>) objects[2];
                        apptServicesList = (ArrayList<SearchAppoinment>) objects[3];
                        catalogs = (ArrayList<Catalog>) objects[4];

                    } else {
                        queueList = (ArrayList<QueueList>) objects[0];
                        schedulesList = (ArrayList<ScheduleList>) objects[1];
                        checkInServList = (ArrayList<SearchService>) objects[2];
                        apptServicesList = (ArrayList<SearchAppoinment>) objects[3];
                    }

                    queueList = queueList == null ? new ArrayList<QueueList>() : queueList;

                    schedulesList = schedulesList == null ? new ArrayList<ScheduleList>() : schedulesList;

                    checkInServList = checkInServList == null ? new ArrayList<SearchService>() : checkInServList;

                    apptServicesList = apptServicesList == null ? new ArrayList<SearchAppoinment>() : apptServicesList;

                    donationServices = donationServices == null ? new ArrayList<SearchDonation>() : donationServices;

                    catalogs = catalogs == null ? new ArrayList<Catalog>() : catalogs;

                    serviceInfoList.clear();
                    DepartmentInfo departmentInfo = new DepartmentInfo();
                    ArrayList<DepServiceInfo> services = new ArrayList<DepServiceInfo>();

                    if (queueList.get(0).isWaitlistEnabled()) {
                        for (SearchService checkInService : checkInServList) {

                            if (checkInService.getCheckInServiceAvailability() != null) {

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
                                    serviceInfo.setVirtualServiceType(checkInService.getVirtualServiceType());
                                }
                                // adding all the info
                                serviceInfo.setChecinServiceInfo(checkInService);
                                services.add(serviceInfo);
                            }

                        }
                    }

                    if (schedulesList.get(0).isApptEnabled()) {
                        for (SearchAppoinment appt : apptServicesList) {
                            if (appt.getAppointServiceAvailability() != null) {
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
                                serviceInfo.setServiceMode(appt.getServiceType());
                                if (appt.getVirtualCallingModes() != null) {
                                    serviceInfo.setCallingMode(appt.getVirtualCallingModes().get(0).getCallingMode());
                                    serviceInfo.setVirtualServiceType(appt.getVirtualServiceType());
                                }
                                if (appt.getAppointServiceAvailability() != null) {
                                    serviceInfo.setAvailability(true);
                                    serviceInfo.setNextAvailableDate(appt.getAppointServiceAvailability().getNextAvailableDate());
                                    if (appt.getAppointServiceAvailability().getNextAvailable() != null) {
                                        serviceInfo.setNextAvailableTime(appt.getAppointServiceAvailability().getNextAvailable().split("-")[0]);
                                    }

                                }
                                // adding all the info
                                serviceInfo.setAppointmentServiceInfo(appt);
                                services.add(serviceInfo);
                            }

                        }
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

                    if (onlinePresence) {

                        if (catalogs.size() > 0) {

                            for (Catalog catalog : catalogs) {

                                DepServiceInfo serviceInfo = new DepServiceInfo();
                                if (catalog.getCatLogName() != null) {
                                    serviceInfo.setName(catalog.getCatLogName());
                                }
                                if (catalog.getCatalogImagesList() != null && catalog.getCatalogImagesList().size() > 0) {
                                    serviceInfo.setProviderImage(catalog.getCatalogImagesList().get(0).getUrl());
                                }
                                serviceInfo.setType(Constants.ORDERS);
                                serviceInfo.setEstTime("");
                                serviceInfo.setPeopleInLine(0);
                                serviceInfo.setCalculationMode("");
                                serviceInfo.setServiceMode("");
                                serviceInfo.setAvailability(true);
                                serviceInfo.setCallingMode("");
                                serviceInfo.setNextAvailableTime("");
                                serviceInfo.setCatalogInfo(catalog); // adding all the catalog info
                                services.add(serviceInfo);
                                break; // if more than one catalog,in this line break the loop and add only one catalog to diaplay(current system only support one catalog)(****   catalog commented on 3 locations   ***)
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
                                    Log.e("ListOf Calls", "0");
                                    ArrayList<DepServiceInfo> servicesInfoList = (ArrayList<DepServiceInfo>) object;

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            if (servicesInfoList.size() > 0) {
                                                llNoSlots.setVisibility(View.GONE);
                                                rvServices.setVisibility(View.VISIBLE);
                                                gridLayoutManager = new GridLayoutManager(ProviderDetailActivity.this, 2);
                                                rvServices.setLayoutManager(gridLayoutManager);
                                                mainServicesAdapter = new MainServicesAdapter(servicesInfoList, ProviderDetailActivity.this, false, iSelectedService, mBusinessDataList);
                                                rvServices.setAdapter(mainServicesAdapter);
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

        } catch (
                Exception e) {
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
            if (orderEnabled) { // if order manager is enabled then making an additional api call to get catalogs

                requests.add(apiService.getCheckInsSchedule(provid + "-" + locid));
                requests.add(apiService.getAppointmentSchedule(provid + "-" + locid));
                requests.add(apiService.getCheckInServices(locid));
                requests.add(apiService.getAppointmentServices(locid));
                requests.add(apiService.getCatalog(provid));

            } else {
                requests.add(apiService.getCheckInsSchedule(provid + "-" + locid));
                requests.add(apiService.getAppointmentSchedule(provid + "-" + locid));
                requests.add(apiService.getCheckInServices(locid));
                requests.add(apiService.getAppointmentServices(locid));
            }

            // Zip all requests with the Function, which will receive the results.
            Observable.zip(requests, new Function<Object[], Object>() {
                @Override
                public Object apply(Object[] objects) throws Exception {
                    // Objects[] is an array of combined results of completed requests
                    ArrayList<QueueList> queueList = new ArrayList<>();
                    ArrayList<ScheduleList> schedulesList = new ArrayList<>();
                    ArrayList<SearchService> checkInServList = new ArrayList<>();
                    ArrayList<SearchAppoinment> apptServicesList = new ArrayList<>();
                    ArrayList<Catalog> catalogs = new ArrayList<>();

                    if (orderEnabled) {

                        queueList = (ArrayList<QueueList>) objects[0];
                        schedulesList = (ArrayList<ScheduleList>) objects[1];
                        checkInServList = (ArrayList<SearchService>) objects[2];
                        apptServicesList = (ArrayList<SearchAppoinment>) objects[3];
                        catalogs = (ArrayList<Catalog>) objects[4];

                    } else {

                        queueList = (ArrayList<QueueList>) objects[0];
                        schedulesList = (ArrayList<ScheduleList>) objects[1];
                        checkInServList = (ArrayList<SearchService>) objects[2];
                        apptServicesList = (ArrayList<SearchAppoinment>) objects[3];
                    }

                    queueList = queueList == null ? new ArrayList<QueueList>() : queueList;

                    schedulesList = schedulesList == null ? new ArrayList<ScheduleList>() : schedulesList;

                    providersList = providersList == null ? new ArrayList<ProviderUserModel>() : providersList;

                    checkInServList = checkInServList == null ? new ArrayList<SearchService>() : checkInServList;

                    apptServicesList = apptServicesList == null ? new ArrayList<SearchAppoinment>() : apptServicesList;

                    donationServices = donationServices == null ? new ArrayList<SearchDonation>() : donationServices;

                    catalogs = catalogs == null ? new ArrayList<Catalog>() : catalogs;

                    serviceInfoList.clear();
                    DepartmentInfo departmentInfo = new DepartmentInfo();
                    ArrayList<DepServiceInfo> services = new ArrayList<DepServiceInfo>();

                    if (queueList.get(0).isWaitlistEnabled()) {
                        for (SearchService checkInService : checkInServList) {

                            if (checkInService.getProvider() == null) {  // adding only Sp level services

                                if (checkInService.getCheckInServiceAvailability() != null) {

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
                                        serviceInfo.setVirtualServiceType(checkInService.getVirtualServiceType());
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

                            if (appt.getProvider() == null) {  // adding only Sp level services
                                if (appt.getAppointServiceAvailability() != null) {

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

                    // for providers
                    for (ProviderUserModel provider : providersList) {

                        DepServiceInfo serviceInfo = new DepServiceInfo();
                        serviceInfo.setDepartmentId(provider.getDeptId());
                        serviceInfo.setDepartmentName("");
                        serviceInfo.setId(provider.getId());
                        if (provider.getBusinessName() != null) {
                            serviceInfo.setName(provider.getBusinessName());
                        } else {
                            serviceInfo.setName(provider.getFirstName() + " " + provider.getLastName());
                        }
                        serviceInfo.setType(Constants.PROVIDER);
                        serviceInfo.setEstTime("");
                        serviceInfo.setPeopleInLine(0);
                        serviceInfo.setCalculationMode("");
                        serviceInfo.setServiceMode("");
                        serviceInfo.setAvailability(true);
                        serviceInfo.setCallingMode("");
                        serviceInfo.setNextAvailableTime("");
                        if (provider.getProfilePicture() != null) {
                            String url = provider.getProfilePicture().getUrl();
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

                    if (onlinePresence) {

                        if (catalogs.size() > 0) {

                            for (Catalog catalog : catalogs) {

                                DepServiceInfo serviceInfo = new DepServiceInfo();
                                if (catalog.getCatLogName() != null) {
                                    serviceInfo.setName(catalog.getCatLogName());
                                }
                                if (catalog.getCatalogImagesList() != null && catalog.getCatalogImagesList().size() > 0) {
                                    serviceInfo.setProviderImage(catalog.getCatalogImagesList().get(0).getUrl());
                                }
                                serviceInfo.setType(Constants.ORDERS);
                                serviceInfo.setEstTime("");
                                serviceInfo.setPeopleInLine(0);
                                serviceInfo.setCalculationMode("");
                                serviceInfo.setServiceMode("");
                                serviceInfo.setAvailability(true);
                                serviceInfo.setCallingMode("");
                                serviceInfo.setNextAvailableTime("");
                                serviceInfo.setCatalogInfo(catalog); // adding all the catalog info
                                services.add(serviceInfo);
                                break; // if more than one catalog,in this line break the loop and add only one catalog to diaplay(current system only support one catalog)(****   catalog commented on 3 locations   ***)

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
                                    Log.e("ListOf Calls", "0");
                                    ArrayList<DepServiceInfo> servicesInfoList = (ArrayList<DepServiceInfo>) object;

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            if (servicesInfoList.size() > 0) {
                                                llNoSlots.setVisibility(View.GONE);
                                                rvServices.setVisibility(View.VISIBLE);
                                                gridLayoutManager = new GridLayoutManager(ProviderDetailActivity.this, 2);
                                                rvServices.setLayoutManager(gridLayoutManager);
                                                mainServicesAdapter = new MainServicesAdapter(servicesInfoList, ProviderDetailActivity.this, false, iSelectedService, mBusinessDataList);
                                                rvServices.setAdapter(mainServicesAdapter);
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
            if (orderEnabled) {

                requests.add(apiService.getCheckInsSchedule(provId + "-" + locid));
                requests.add(apiService.getAppointmentSchedule(provId + "-" + locid));
                requests.add(apiService.getCheckInServices(locid));
                requests.add(apiService.getAppointmentServices(locid));
                requests.add(apiService.getCatalog(provId));

            } else {
                requests.add(apiService.getCheckInsSchedule(provId + "-" + locid));
                requests.add(apiService.getAppointmentSchedule(provId + "-" + locid));
                requests.add(apiService.getCheckInServices(locid));
                requests.add(apiService.getAppointmentServices(locid));
            }

            // Zip all requests with the Function, which will receive the results.
            Observable.zip(requests, new Function<Object[], Object>() {
                @Override
                public Object apply(Object[] objects) throws Exception {
                    // Objects[] is an array of combined results of completed requests
                    ArrayList<QueueList> queueList = new ArrayList<>();
                    ArrayList<ScheduleList> schedulesList = new ArrayList<>();
                    ArrayList<SearchService> checkInServicesList = new ArrayList<>();
                    ArrayList<SearchAppoinment> apptServicesList = new ArrayList<>();
                    ArrayList<Catalog> catalogs = new ArrayList<>();

                    if (orderEnabled) {

                        queueList = (ArrayList<QueueList>) objects[0];
                        schedulesList = (ArrayList<ScheduleList>) objects[1];
                        checkInServicesList = (ArrayList<SearchService>) objects[2];
                        apptServicesList = (ArrayList<SearchAppoinment>) objects[3];
                        catalogs = (ArrayList<Catalog>) objects[4];

                    } else {

                        queueList = (ArrayList<QueueList>) objects[0];
                        schedulesList = (ArrayList<ScheduleList>) objects[1];
                        checkInServicesList = (ArrayList<SearchService>) objects[2];
                        apptServicesList = (ArrayList<SearchAppoinment>) objects[3];
                    }

                    checkInServicesList = checkInServicesList == null ? new ArrayList<SearchService>() : checkInServicesList;

                    departmentProviders = departmentProviders == null ? new ArrayList<SearchDepartmentServices>() : departmentProviders;

                    queueList = queueList == null ? new ArrayList<QueueList>() : queueList;

                    schedulesList = schedulesList == null ? new ArrayList<ScheduleList>() : schedulesList;

                    donationServices = donationServices == null ? new ArrayList<SearchDonation>() : donationServices;

                    departmentsList.clear();
                    for (SearchDepartmentServices department : departmentProviders) {

                        DepartmentInfo departmentInfo = new DepartmentInfo();
                        ArrayList<DepServiceInfo> services = new ArrayList<DepServiceInfo>();

                        if (queueList.get(0).isWaitlistEnabled()) {

                            for (SearchService checkInService : checkInServicesList) {

                                if (department.getDepartmentId() == checkInService.getDepartment()) {

                                    if (checkInService.getProvider() == null) {  // adding only Sp level services

                                        if (checkInService.getCheckInServiceAvailability() != null) {

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
                                                serviceInfo.setVirtualServiceType(checkInService.getVirtualServiceType());
                                            }
                                            // adding all the info
                                            services.add(serviceInfo);
                                        }
                                    }
                                }
                            }
                        }

                        // adding appointment services only when appointment is enabled

                        if (schedulesList.get(0).isApptEnabled()) {
                            for (SearchAppoinment appt : apptServicesList) {

                                if (department.getDepartmentId() == appt.getDepartment()) {

                                    if (appt.getProvider() == null) {  // adding only Sp level services

                                        if (appt.getAppointServiceAvailability() != null) {

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

                        // for providers
                        for (SearchDepartmentServices provider : departmentProviders) {

                            if (department.getDepartmentId() == provider.getDepartmentId()) {

                                for (ProviderUserModel user : provider.getUsers()) {

                                    DepServiceInfo serviceInfo = new DepServiceInfo();
                                    serviceInfo.setDepartmentId(provider.getDepartmentId());
                                    serviceInfo.setDepartmentName(provider.getDepartmentName());
                                    serviceInfo.setId(user.getId());
                                    if (user.getBusinessName() != null) {
                                        serviceInfo.setName(user.getBusinessName());
                                    } else {
                                        serviceInfo.setName(user.getFirstName() + " " + user.getLastName());
                                    }
                                    serviceInfo.setType(Constants.PROVIDER);
                                    serviceInfo.setEstTime("");
                                    serviceInfo.setPeopleInLine(0);
                                    serviceInfo.setCalculationMode("");
                                    serviceInfo.setServiceMode("");
                                    serviceInfo.setAvailability(true);
                                    serviceInfo.setCallingMode("");
                                    serviceInfo.setNextAvailableTime("");
                                    if (user.getProfilePicture() != null) {
                                        String url = user.getProfilePicture().getUrl();
                                        serviceInfo.setProviderImage(url);
                                    }
                                    // adding all the info
                                    serviceInfo.setProviderInfo(user);
                                    services.add(serviceInfo);
                                }

                            }
                        }

                        if (services.size() > 0) {
                            departmentInfo.setDeptServicesList(services);
                            departmentInfo.setDepartmentName(department.getDepartmentName());

                            departmentsList.add(departmentInfo);
                        }
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

                        if (donationServicesList.size() > 0) {
                            donationDepartment.setDeptServicesList(donationServicesList);
                            departmentsList.add(donationDepartment);
                        }
                    }

                    if (onlinePresence) {

                        if (catalogs.size() > 0) {
                            DepartmentInfo catalogDepartment = new DepartmentInfo();
                            ArrayList<DepServiceInfo> catalogsList = new ArrayList<DepServiceInfo>();
                            catalogDepartment.setDepartmentName("Catalog");

                            for (Catalog catalog : catalogs) {

                                DepServiceInfo serviceInfo = new DepServiceInfo();
                                if (catalog.getCatLogName() != null) {
                                    serviceInfo.setName(catalog.getCatLogName());
                                }
                                if (catalog.getCatalogImagesList() != null && catalog.getCatalogImagesList().size() > 0) {
                                    serviceInfo.setProviderImage(catalog.getCatalogImagesList().get(0).getUrl());
                                }
                                serviceInfo.setType(Constants.ORDERS);
                                serviceInfo.setEstTime("");
                                serviceInfo.setPeopleInLine(0);
                                serviceInfo.setCalculationMode("");
                                serviceInfo.setServiceMode("");
                                serviceInfo.setAvailability(true);
                                serviceInfo.setCallingMode("");
                                serviceInfo.setNextAvailableTime("");
                                serviceInfo.setCatalogInfo(catalog); // adding all the catalog info
                                catalogsList.add(serviceInfo);
                                break; // if more than one catalog,in this line break the loop and add only one catalog to diaplay(current system only support one catalog)(****   catalog commented on 3 locations   ***)

                            }

                            if (catalogsList.size() > 0) {
                                catalogDepartment.setDeptServicesList(catalogsList);
                                departmentsList.add(catalogDepartment);
                            }

                        }
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

                                            if (departmentsDataList.size() > 0) {
                                                llNoSlots.setVisibility(View.GONE);
                                                rvServices.setVisibility(View.VISIBLE);
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
                                                servicesAdapter = new ServicesAdapter(ProviderDetailActivity.this, departmentsDataList, false, iSelectedService, mBusinessDataList);
                                                rvServices.setAdapter(servicesAdapter);
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

        } catch (
                Exception e) {
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
                        favFlag = false;
                        if (mFavList != null && mFavList.size() > 0) {
                            for (int i = 0; i < mFavList.size(); i++) {
                                Config.logV("Fav List-----##&&&-----" + mFavList.get(i).getId());
                                Config.logV("Fav Fav List--------%%%%--" + mBusinessDataList.getId());
                                if (mFavList.get(i).getId() == mBusinessDataList.getId()) {
                                    favFlag = true;
                                    ivfav.setVisibility(View.VISIBLE);
                                    ivfav.setImageResource(R.drawable.new_favourite);
                                }
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

    BottomSheetDialog dialog;

    private void ApiCheckInMessage(final int mLocid, final String loc) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
//        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
//        mDialog.show();
        Map<String, String> query = new HashMap<>();
        query.put("location-eq", String.valueOf(mLocid));
        query.put("waitlistStatus-eq", "checkedIn,arrived");
        Call<ArrayList<SearchCheckInMessage>> call = apiService.getSearchCheckInMessage(query);
        Config.logV("Location-----###########@@@@@@" + query);
        call.enqueue(new Callback<ArrayList<SearchCheckInMessage>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchCheckInMessage>> call, Response<ArrayList<SearchCheckInMessage>> response) {
                try {
//                    if (mDialog.isShowing())
                    //  Config.closeDialog(this, mDialog);
                    Config.logV("URL-----4444-----Location-----###########@@@@@@-----" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code--------Message-----------------" + response.code());
                    if (response.code() == 200) {
                        mSearchmCheckListShow.clear();
                        mSearchmCheckMessageList.clear();
                        if (response.body().size() > 0) {
                            SearchCheckInMessage mCheckMessage = new SearchCheckInMessage();
                            mCheckMessage.setmAllSearch_checkIn(response.body());
                            mCheckMessage.setLocid(mLocid);
                            mSearchmCheckMessageList.add(mCheckMessage);
                            for (int i = 0; i < mSearchmCheckMessageList.size(); i++) {
                                if (mSearchLocList.get(i).getId() == mSearchmCheckMessageList.get(i).getLocid()) {
                                    if (mSearchmCheckMessageList.get(i).getmAllSearch_checkIn().size() > 0) {
                                        tv_checkinsList.setVisibility(View.VISIBLE);
                                        tv_checkinsList.setVisibility(View.GONE); // remove this line when you want to show bookings

                                    }
                                    //  myViewHolder.tv_checkin.setText("You have "+mCheckInMessage.get(i).getmAllSearch_checkIn().size()+" Check-In at this location");

                                    if (terminology != null) {
                                        String firstWord = "You have ";
                                        String secondWord = "";
                                        if (mSearchmCheckMessageList.get(i).getmAllSearch_checkIn().size() > 1) {
                                            secondWord = mSearchmCheckMessageList.get(i).getmAllSearch_checkIn().size() + " " + "Bookings";
                                        } else {
                                            secondWord = mSearchmCheckMessageList.get(i).getmAllSearch_checkIn().size() + " " + "Booking";
                                        }
                                        String thirdword = " at this location";
                                        Spannable spannable = new SpannableString(firstWord + secondWord + thirdword);
                                        Typeface tyface_edittext2 = Typeface.createFromAsset(mContext.getAssets(),
                                                "fonts/Montserrat_Bold.otf");
                                        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface_edittext2), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        tv_checkinsList.setText(spannable);

                                    }
                                }
                            }
                            tv_checkinsList.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog = new BottomSheetDialog(mContext);
                                    dialog.setContentView(R.layout.checkin_loclist);
                                    dialog.setCancelable(true);
                                    dialog.show();
                                    TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);
                                    TextView tv_token = (TextView) dialog.findViewById(R.id.tv_token);
                                    String firstWord = "";
                                    if (mSearchSettings.isShowTokenId()) {
                                        firstWord = "Your " + "Token" + " at ";
                                        tv_token.setVisibility(View.VISIBLE);
                                    } else {
                                        firstWord = "Your " + terminology + " at ";
                                        tv_token.setVisibility(View.GONE);
                                    }

                                    String secondWord = loc;
                                    location = loc;
                                    Spannable spannable = new SpannableString(firstWord + secondWord);
                                    Typeface tyface_edittext2 = Typeface.createFromAsset(mContext.getAssets(),
                                            "fonts/Montserrat_Bold.otf");
                                    spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface_edittext2), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    tv_title.setText(spannable);
                                    RecyclerView checkloclist = (RecyclerView) dialog.findViewById(R.id.checkloclist);
                                    Button btn_close = (Button) dialog.findViewById(R.id.btn_close);
                                    btn_close.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                            // refreshList();
                                        }
                                    });
                                    mSearchmCheckListShow = response.body();
                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                                    checkloclist.setLayoutManager(mLayoutManager);
                                    LocationCheckinAdapter checkAdapter = new LocationCheckinAdapter(callback, String.valueOf(providerId), mSearchmCheckListShow, mContext, ProviderDetailActivity.this);
                                    checkloclist.setAdapter(checkAdapter);
                                    checkAdapter.notifyDataSetChanged();
                                }
                            });


                        } else {
                            tv_checkinsList.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<SearchCheckInMessage>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Location-----###########@@@@@@-------Fail--------" + t.toString());
//                if (mDialog.isShowing())
//                    Config.closeDialog(getActivity(), mDialog);
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


    private void openMapView(String latitude, String longitude, String locationName) {
        String geoUri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + " (" + locationName + ")";

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
        mapIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        if (mapIntent.resolveActivity(mContext.getPackageManager()) != null) {
            mContext.startActivity(mapIntent);
        }
    }

    public static String extractUrl(String jsonString) {
        Gson g = new Gson();
        ProfilePicture p = g.fromJson(jsonString, ProfilePicture.class);
        String url = p.getUrl();
        return url;
    }


    // Click actions of selected items in grid

    @Override
    public void onCheckInSelected(SearchService checkinServiceInfo) {

        if (checkinServiceInfo != null) {
            Intent intent = new Intent(ProviderDetailActivity.this, CheckInActivity.class);
            intent.putExtra("uniqueID", uniqueId);
            intent.putExtra("providerName", tvSpName.getText().toString());
            intent.putExtra("locationName", tvLocationName.getText().toString());
            intent.putExtra("providerId", providerId);
            intent.putExtra("locationId", locationId);
            intent.putExtra("checkInInfo", checkinServiceInfo);
            intent.putExtra("fromUser", false);
            intent.putExtra("sector", mBusinessDataList.getServiceSector().getDomain());

            startActivity(intent);
        }
    }

    @Override
    public void onAppointmentSelected(SearchAppoinment appointmentServiceInfo) {

        if (appointmentServiceInfo != null) {
            Intent intent = new Intent(ProviderDetailActivity.this, AppointmentActivity.class);
            intent.putExtra("uniqueID", uniqueId);
            intent.putExtra("providerName", tvSpName.getText().toString());
            intent.putExtra("locationName", tvLocationName.getText().toString());
            intent.putExtra("locationId", locationId);
            intent.putExtra("providerId", providerId);
            intent.putExtra("fromUser", false);
            intent.putExtra("sector", mBusinessDataList.getServiceSector().getDomain());
            ServiceInfo serviceInfo = new ServiceInfo();
            serviceInfo.setServiceId(appointmentServiceInfo.getId());
            serviceInfo.setServiceName(appointmentServiceInfo.getName());
            serviceInfo.setDescription(appointmentServiceInfo.getDescription());
            serviceInfo.setType(Constants.APPOINTMENT);
            serviceInfo.setUser(false);
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

            intent.putExtra("serviceInfo", serviceInfo);
            intent.putExtra("sector", mBusinessDataList.getServiceSector().getDomain());

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
        providerIntent.putExtra("providerName", tvSpName.getText().toString());
        providerIntent.putExtra("sector", mBusinessDataList.getServiceSector());

        startActivity(providerIntent);

    }

    @Override
    public void onDonationSelected(SearchDonation donationServiceInfo) {

        Intent intent = new Intent(ProviderDetailActivity.this, DonationActivity.class);
        intent.putExtra("uniqueID", uniqueId);
        intent.putExtra("locationId", locationId);
        intent.putExtra("providerName", tvSpName.getText().toString());
        intent.putExtra("providerId", providerId);
        intent.putExtra("donationInfo", donationServiceInfo);
        intent.putExtra("locationName", tvLocationName.getText().toString());
        startActivity(intent);
    }

    @Override
    public void onCatalogSelected(Catalog catalogInfo) {

        Intent catalogIntent = new Intent(ProviderDetailActivity.this, ItemsActivity.class);
        if (mBusinessDataList != null && mBusinessDataList.getBusinessName() != null) {
            catalogIntent.putExtra("providerInfo", mBusinessDataList);
        }
        catalogIntent.putExtra("catalogInfo", catalogInfo);
        catalogIntent.putExtra("accountId", providerId);
        catalogIntent.putExtra("uniqueId", uniqueId);
        startActivity(catalogIntent);

    }

    @Override
    public void sendAddress(String address, int id, String place) {
        locationId = id;
        location = place;
        tvLocation.setText(address);
        tvLocationName.setText(location);
        //   ApiCheckInMessage(locationId,place);
        apiSettings_Details(uniqueId, providerId, locationId, location);

    }

    @Override
    public void getMessage(String valueOf) {
        // do nothing
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }
}