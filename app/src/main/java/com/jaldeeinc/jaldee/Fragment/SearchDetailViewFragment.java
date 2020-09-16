
package com.jaldeeinc.jaldee.Fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Home;
import com.jaldeeinc.jaldee.activities.SwipeGalleryImage;
import com.jaldeeinc.jaldee.adapter.ContactDetailAdapter;
import com.jaldeeinc.jaldee.adapter.DepartmentAdapter;
import com.jaldeeinc.jaldee.adapter.LocationCheckinAdapter;
import com.jaldeeinc.jaldee.adapter.SearchLocationAdapter;
import com.jaldeeinc.jaldee.adapter.SpecialisationAdapter;
import com.jaldeeinc.jaldee.adapter.UserDetailAdapter;
import com.jaldeeinc.jaldee.adapter.UsersAdapter;
import com.jaldeeinc.jaldee.adapter.VirtualFieldAdapter;
import com.jaldeeinc.jaldee.callback.AdapterCallback;
import com.jaldeeinc.jaldee.callback.ContactAdapterCallback;
import com.jaldeeinc.jaldee.callback.LocationCheckinCallback;
import com.jaldeeinc.jaldee.callback.SearchLocationAdpterCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CircleTransform;
import com.jaldeeinc.jaldee.custom.CustomTypefaceSpan;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;
import com.jaldeeinc.jaldee.custom.ResizableCustomView;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.ContactModel;
import com.jaldeeinc.jaldee.model.DepartmentModal;
import com.jaldeeinc.jaldee.model.DepartmentUserSearchModel;
import com.jaldeeinc.jaldee.model.ProviderUserModel;
import com.jaldeeinc.jaldee.model.SearchListModel;
import com.jaldeeinc.jaldee.model.SearchModel;
import com.jaldeeinc.jaldee.model.SocialMediaModel;
import com.jaldeeinc.jaldee.model.WorkingModel;
import com.jaldeeinc.jaldee.response.CoupnResponse;
import com.jaldeeinc.jaldee.response.FavouriteModel;
import com.jaldeeinc.jaldee.response.JdnResponse;
import com.jaldeeinc.jaldee.response.QueueList;
import com.jaldeeinc.jaldee.response.ScheduleList;
import com.jaldeeinc.jaldee.response.SearchAWsResponse;
import com.jaldeeinc.jaldee.response.SearchAppointmentDepartmentServices;
import com.jaldeeinc.jaldee.response.SearchCheckInMessage;
import com.jaldeeinc.jaldee.response.SearchDepartment;
import com.jaldeeinc.jaldee.response.SearchDepartmentServices;
import com.jaldeeinc.jaldee.response.SearchDonation;
import com.jaldeeinc.jaldee.response.SearchLocation;
import com.jaldeeinc.jaldee.response.SearchService;
import com.jaldeeinc.jaldee.response.SearchSetting;
import com.jaldeeinc.jaldee.response.SearchTerminology;
import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.jaldeeinc.jaldee.response.SearchVirtualFields;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.jaldeeinc.jaldee.widgets.CustomDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jaldeeinc.jaldee.connection.ApiClient.context;


/**
 * Created by sharmila on 24/7/18.
 */


public class SearchDetailViewFragment extends RootFragment implements SearchLocationAdpterCallback, LocationCheckinCallback, ContactAdapterCallback, DepartmentAdapter.OnItemClickListener, UsersAdapter.OnItemClickListener {
    Context mContext;
    ListView deptListview;
    SearchViewDetail mBusinessDataList;
    ArrayList<CoupnResponse> couponResponse = new ArrayList<>();
    ArrayList<SearchViewDetail> mSearchGallery;
    ArrayList<SearchLocation> mSearchLocList;
    ArrayList<SearchDepartment> mSearchDepartments;
    ArrayList<SearchDepartmentServices> mSearchDepartmentServices;
    String mbranchId, latitude, longitude, lat_long;
    boolean online_presence;
    boolean donationFundRaising;
    boolean virtualServices;
    Boolean firstCouponAvailable, couponAvailable;
    JdnResponse jdnList;
    String jdnDiscount, jdnMaxvalue;
    AdapterCallback mAdapterCallback;
    SearchSetting mSearchSettings;
    SearchAWsResponse mSearchAWSResponse;
    SearchTerminology mSearchTerminology;
    ArrayList<QueueList> mSearchQueueList;
    ArrayList<ScheduleList> mSearchScheduleList;
    ArrayList<SearchService> mServicesList;
    ArrayList<DepartmentModal> mDepartmentsList;
    ArrayList<SearchCheckInMessage> mSearchmCheckMessageList;
    ArrayList<SearchCheckInMessage> mSearchmCheckListShow = new ArrayList<>();
    ArrayList<String> departmentNameList = new ArrayList();
    ArrayList<String> departmentCodeList = new ArrayList();
    TextView tv_busName, tv_domain, tv_desc, tv_msg, txtMore;
    ImageView img_arrow;
    Boolean isExpandFlag = true;
    RecyclerView mRecyLocDetail, mRecycle_virtualfield, mRecycleDepartment, mrecycle_specialisation;
    SearchLocationAdapter mSearchLocAdapter;
    DepartmentAdapter mDepartmentAdapter;
    UsersAdapter usersAdapter;
    UserDetailAdapter userDetailAdapter;
    ImageView mImgeProfile, mImgthumbProfile, mImgthumbProfile2, mImgthumbProfile1;
    int mProviderId;
    ArrayList<String> ids;
    String uniqueID;
    String customUniqueID;
    String home, homeUniqueId, uniID;
    String claimable;
    TextView tv_ImageViewText, tv_Moredetails, tv_specializtion, tv_SocialMedia, tv_Gallery, tv_mImageViewTextnew, locationHeading;
    RatingBar rating;
    SearchLocationAdpterCallback mInterface;
    ContactAdapterCallback mInterfaceContact;
    LocationCheckinCallback callback;
    String location;
    TextView tv_fav;
    int department;
    boolean flag_more = false;
    ImageView ic_pin, ic_yout, ic_fac, ic_gplus, ic_twitt, ic_link, ic_jaldeeverifiedIcon;
    LinearLayout LsocialMedia;
    LinearLayout LSpecialization, LSpecialization_2;
    TextView tv_spec1, tv_spec2, tv_seeAll, tv_contact, tv_coupon, tv_first_ccoupon, specialSeeAll;
    ImageView tv_jdn;
    List<SearchDepartment> departmentList;
    private String departmentCode;
    private int TOTAL_PAGES = 0;
    List<SearchAWsResponse> mSearchResp = new ArrayList<>();
    List<SearchAWsResponse> mSearchRespDetail = new ArrayList<>();
    List<QueueList> mQueueList = new ArrayList<>();
    //    PaginationAdapter pageadapter;
    List<SearchListModel> mSearchListModel = new ArrayList<>();
    private boolean isLoading = false;
    private int PAGE_START = 0;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    int total_foundcount = 0;
    String terminology;
    String userTerminology;
    ArrayList<SearchDepartmentServices> mSearchDepartmentList;
    TextView departmentHeading;
    HashMap<String, List<SearchListModel>> departmentMap;
    LinearLayout L_layout;
    ArrayList<SearchDonation> LServicesList = new ArrayList<>();
    ArrayList<SearchAppointmentDepartmentServices> LaServicesList = new ArrayList<>();
    ArrayList<SearchDonation> gServiceList = new ArrayList<>();
    ArrayList<SearchAppointmentDepartmentServices> aServiceList = new ArrayList<>();
    boolean from_user = false;
    DepartmentUserSearchModel searchdetailList = new DepartmentUserSearchModel();
    SearchModel domainList = new SearchModel();
    ArrayList<ProviderUserModel> usersList = new ArrayList<ProviderUserModel>();
    private CardView cvUsers;
    private boolean throughLink = false;
    TextView tvNoRatings,tvterminology;
    private LinearLayout llMDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDepartmentAdapter = new DepartmentAdapter(this);
        usersAdapter = new UsersAdapter(this);
        View row = inflater.inflate(R.layout.searchdetails, container, false);
        mContext = getActivity();
        mRecyLocDetail = (RecyclerView) row.findViewById(R.id.mSearchLoc);
        mRecycleDepartment = (RecyclerView) row.findViewById(R.id.mDepartmentView);
        mRecycle_virtualfield = (RecyclerView) row.findViewById(R.id.mrecycle_virtualfield);
        mrecycle_specialisation = (RecyclerView) row.findViewById(R.id.mrecycle_specialisation);
        rating = (RatingBar) row.findViewById(R.id.mRatingBar);
        // tv_contactdetails = (TextView) row.findViewById(R.id.txt_contactdetails);
        tv_specializtion = (TextView) row.findViewById(R.id.txt_specializtion);
        LSpecialization = (LinearLayout) row.findViewById(R.id.LSpecialization);
        tv_Gallery = (TextView) row.findViewById(R.id.txtGallery);
        tv_SocialMedia = (TextView) row.findViewById(R.id.txtSocialMedia);
        txtMore = (TextView) row.findViewById(R.id.txtMore);
        img_arrow = (ImageView) row.findViewById(R.id.img_arrow);
        tv_contact = (TextView) row.findViewById(R.id.txtcontact);
        tv_jdn = (ImageView) row.findViewById(R.id.txtjdn);
        tv_coupon = (TextView) row.findViewById(R.id.txtcoupon);
        cvUsers = row.findViewById(R.id.cv_users);
        tvterminology = row.findViewById(R.id.sp_title);
        tv_first_ccoupon = (TextView) row.findViewById(R.id.txtFirstCoupon);
        specialSeeAll = (TextView) row.findViewById(R.id.specialSeeAll);
        departmentHeading = (TextView) row.findViewById(R.id.departmentHeading);
        tvNoRatings = row.findViewById(R.id.tv_noRR);
        llMDetails = row.findViewById(R.id.locationlayout);
        count = 0;
        mBusinessDataList = new SearchViewDetail();
        List<CoupnResponse> couponResponse;
        mSearchGallery = new ArrayList<>();
        mSearchLocList = new ArrayList<>();
        mSearchDepartments = new ArrayList<>();
        mSearchDepartmentServices = new ArrayList<>();
        mSearchSettings = new SearchSetting();
        mSearchAWSResponse = new SearchAWsResponse();
        mSearchTerminology = new SearchTerminology();
        mSearchQueueList = new ArrayList<>();
        mServicesList = new ArrayList<SearchService>();
        mSearchmCheckMessageList = new ArrayList<>();
        ids = new ArrayList<>();
        callback = (LocationCheckinCallback) this;
        mInterfaceContact = (ContactAdapterCallback) this;
        //isContact = false;
        Config.logV("Refresh @@@@@@@@@@@@@@@@@@");
        TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);
        tv_title.setVisibility(View.INVISIBLE);
        ImageView iBackPress = (ImageView) row.findViewById(R.id.backpress);
        initializations(row);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            home = bundle.getString("home");
            homeUniqueId = bundle.getString("homeUniqueId");
            from_user = bundle.getBoolean("user");
            searchdetailList = (DepartmentUserSearchModel) bundle.getSerializable("userdetail");
            if (home != null && home.equals("home")) {
                if (homeUniqueId != null) {
                    apiFetchIdFromDeepLink(homeUniqueId);
//                    uniqueID = homeUniqueId;
//                    Log.i("uniqueCutomIdSecond", uniqueID);
                }
            } else {
                uniqueID = bundle.getString("uniqueID");
            }
            claimable = "0";
            if (uniqueID != null) {
                initSearchView(uniqueID);

            }
        }
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (homeUniqueId == null) {
                    getFragmentManager().popBackStack();
                } else {
                    Intent intent = new Intent(mContext, Home.class);
                    startActivity(intent);
                }
                // what do you want here
            }
        });
        SharedPreference.getInstance(mContext).setValue("refreshcheckin", "false");
        mRecyLocDetail.setNestedScrollingEnabled(false);
        tv_busName = (TextView) row.findViewById(R.id.txtbus_name);
        tv_msg = (TextView) row.findViewById(R.id.txtmsg);
        tv_domain = (TextView) row.findViewById(R.id.txt_domain);
        mImgeProfile = (ImageView) row.findViewById(R.id.i_profile);
        mImgthumbProfile = (ImageView) row.findViewById(R.id.iThumb_profile);
        mImgthumbProfile2 = (ImageView) row.findViewById(R.id.iThumb_profile2);
        tv_ImageViewText = (TextView) row.findViewById(R.id.mImageViewText);
        tv_mImageViewTextnew = (TextView) row.findViewById(R.id.mImageViewTextnew);
        locationHeading = (TextView) row.findViewById(R.id.locationHeading);
        mImgthumbProfile1 = (ImageView) row.findViewById(R.id.iThumb_profile1);
        tv_fav = (TextView) row.findViewById(R.id.txtfav);
        tv_Moredetails = (TextView) row.findViewById(R.id.txtMoredetails);
        LsocialMedia = (LinearLayout) row.findViewById(R.id.LsocialMedia);
        LSpecialization_2 = (LinearLayout) row.findViewById(R.id.LSpecialization_2);
        tv_spec1 = (TextView) row.findViewById(R.id.txtspec1);
        tv_spec2 = (TextView) row.findViewById(R.id.txtspec2);
        tv_seeAll = (TextView) row.findViewById(R.id.txtSeeAll);
        ic_fac = (ImageView) row.findViewById(R.id.ic_fac);
        ic_gplus = (ImageView) row.findViewById(R.id.ic_gplus);
        ic_pin = (ImageView) row.findViewById(R.id.ic_pin);
        ic_link = (ImageView) row.findViewById(R.id.ic_link);
        ic_twitt = (ImageView) row.findViewById(R.id.ic_twitt);
        ic_yout = (ImageView) row.findViewById(R.id.ic_yout);
        ic_jaldeeverifiedIcon = (ImageView) row.findViewById(R.id.ic_jaldeeverifiedIcon);
        //  tv_exp = (TextView) row.findViewById(R.id.txt_expe);
        tv_desc = (TextView) row.findViewById(R.id.txt_bus_desc);
        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_busName.setTypeface(tyface);
        tv_ImageViewText.setTypeface(tyface);
        tv_mImageViewTextnew.setTypeface(tyface);
        L_layout = row.findViewById(R.id.layout_type);

        tv_Moredetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!flag_more) {
                    flag_more = true;
                    mRecycle_virtualfield.setVisibility(View.VISIBLE);
                    Config.logV("Domain Size@@@@@@@@@@@@@" + domainVirtual.size());
                    Config.logV("Subdomain Size@@@@@@@@@@@@@" + sub_domainVirtual.size());
                    tv_Moredetails.setText("See Less");
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                    mRecycle_virtualfield.setLayoutManager(mLayoutManager);
                    mAdapter = new VirtualFieldAdapter(domainVirtual, mContext, domainVirtual.size());
                    mRecycle_virtualfield.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                } else {
                    flag_more = false;
                    tv_Moredetails.setText("See All");
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
        llMDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecycle_virtualfield.getVisibility() != View.VISIBLE) {
                    mRecycle_virtualfield.setVisibility(View.VISIBLE);
                    int size = domainVirtual.size();
                    if (size > 2) {
                        tv_Moredetails.setVisibility(View.VISIBLE);
                    } else {
                        tv_Moredetails.setVisibility(View.GONE);
                    }
                    if (size > 0) {
                        mRecycle_virtualfield.setVisibility(View.VISIBLE);
                    } else {
                        mRecycle_virtualfield.setVisibility(View.GONE);
                    }
                } else {
                    mRecycle_virtualfield.setVisibility(View.GONE);
                    tv_Moredetails.setVisibility(View.GONE);
                }

            }
        });
        tv_jdn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (homeUniqueId == null) {
                    onMethodJdn(uniqueID);
                } else {
                    onMethodJdn(homeUniqueId);
                }
            }
        });
        tv_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (homeUniqueId == null) {
                    onMethodCoupn(uniqueID);
                } else {
                    onMethodCoupn(homeUniqueId);
                }
            }
        });
        tv_first_ccoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (homeUniqueId == null) {
                    onMethodFirstCoupn(uniqueID);
                } else {
                    onMethodFirstCoupn(homeUniqueId);
                }
            }
        });
        mInterface = (SearchLocationAdpterCallback) this;
        if (claimable != null && claimable.equals("1")) {
            tv_msg.setVisibility(View.GONE);
            tv_fav.setVisibility(View.GONE);
        }
//        else if({
//          L_layout.setVisibility(View.GONE);
//        }
//        else{
//            L_layout.setVisibility(View.VISIBLE);
//        }
        tv_msg.setEnabled(false);
        tv_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.logV("Provider iD--------------" + String.valueOf(mProviderId));
                final BottomSheetDialog dialog = new BottomSheetDialog(mContext, R.style.DialogStyle);
                dialog.setContentView(R.layout.reply);
                dialog.show();
                final Button btn_send = (Button) dialog.findViewById(R.id.btn_send);
                Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
                final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
                TextView txtsendmsg = (TextView) dialog.findViewById(R.id.txtsendmsg);
                txtsendmsg.setVisibility(View.VISIBLE);
                txtsendmsg.setText("Message to " + tv_busName.getText().toString());
                btn_send.setText("SEND");
                edt_message.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable arg0) {
                        if (edt_message.getText().toString().length() >= 1 && !edt_message.getText().toString().trim().isEmpty()) {
                            btn_send.setEnabled(true);
                            btn_send.setClickable(true);
                            btn_send.setBackground(mContext.getResources().getDrawable(R.color.blue));
                        } else {
                            btn_send.setEnabled(false);
                            btn_send.setClickable(false);
                            btn_send.setBackground(mContext.getResources().getDrawable(R.color.button_grey));
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });
                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String modifyAccountID = String.valueOf(mProviderId);
                        ApiCommunicate(modifyAccountID, edt_message.getText().toString(), dialog);
                    }
                });
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
        return row;
    }

    private void initializations(View row) {

        SharedPreference.getInstance(mContext).setValue("refreshcheckin", "false");
        mRecyLocDetail.setNestedScrollingEnabled(false);
        tv_busName = (TextView) row.findViewById(R.id.txtbus_name);
        tv_msg = (TextView) row.findViewById(R.id.txtmsg);
        tv_domain = (TextView) row.findViewById(R.id.txt_domain);
        mImgeProfile = (ImageView) row.findViewById(R.id.i_profile);
        mImgthumbProfile = (ImageView) row.findViewById(R.id.iThumb_profile);
        mImgthumbProfile2 = (ImageView) row.findViewById(R.id.iThumb_profile2);
        tv_ImageViewText = (TextView) row.findViewById(R.id.mImageViewText);
        tv_mImageViewTextnew = (TextView) row.findViewById(R.id.mImageViewTextnew);
        locationHeading = (TextView) row.findViewById(R.id.locationHeading);
        mImgthumbProfile1 = (ImageView) row.findViewById(R.id.iThumb_profile1);
        tv_fav = (TextView) row.findViewById(R.id.txtfav);
        tv_Moredetails = (TextView) row.findViewById(R.id.txtMoredetails);
        LsocialMedia = (LinearLayout) row.findViewById(R.id.LsocialMedia);
        LSpecialization_2 = (LinearLayout) row.findViewById(R.id.LSpecialization_2);
        tv_spec1 = (TextView) row.findViewById(R.id.txtspec1);
        tv_spec2 = (TextView) row.findViewById(R.id.txtspec2);
        tv_seeAll = (TextView) row.findViewById(R.id.txtSeeAll);
        ic_fac = (ImageView) row.findViewById(R.id.ic_fac);
        ic_gplus = (ImageView) row.findViewById(R.id.ic_gplus);
        ic_pin = (ImageView) row.findViewById(R.id.ic_pin);
        ic_link = (ImageView) row.findViewById(R.id.ic_link);
        ic_twitt = (ImageView) row.findViewById(R.id.ic_twitt);
        ic_yout = (ImageView) row.findViewById(R.id.ic_yout);
        ic_jaldeeverifiedIcon = (ImageView) row.findViewById(R.id.ic_jaldeeverifiedIcon);
        //  tv_exp = (TextView) row.findViewById(R.id.txt_expe);
        tv_desc = (TextView) row.findViewById(R.id.txt_bus_desc);
        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_busName.setTypeface(tyface);
        tv_ImageViewText.setTypeface(tyface);
        tv_mImageViewTextnew.setTypeface(tyface);
        L_layout = row.findViewById(R.id.layout_type);

    }


    private void apiFetchIdFromDeepLink(String customID) {
        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<String> call = apiService.getUniqueID(customID);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    if (response.code() == 200) {
                        homeUniqueId = response.body();
                        Log.i("sadf", homeUniqueId);
                        uniqueID = homeUniqueId;
                        throughLink = true;
                        initSearchView(uniqueID);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Load every details for the search details page
     *
     * @param uniqueID
     */
    private void initSearchView(String uniqueID) {
        apiJaldeeCoupon(uniqueID);
        apiJDN(uniqueID);
        apiSearchViewTerminology(uniqueID);
        apiVirtualFields(uniqueID);
        apiSettings_Details(uniqueID, mSearchResp);

//        apiSearchViewDetail(uniqueID, mSearchResp);
    }

    private void apiSettings_Details(final String muniqueID, final List<SearchAWsResponse> mSearchResp) {
        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));
        Call<SearchSetting> call = apiService.getSearchViewSetting(Integer.parseInt(muniqueID), sdf.format(currentTime));
        call.enqueue(new Callback<SearchSetting>() {
            @Override
            public void onResponse(Call<SearchSetting> call, Response<SearchSetting> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);
                    Config.logV("URL--7777-------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------Setting-------------------" + response.code());
                    if (response.code() == 200) {
                        mSearchSettings = response.body();
                        apiSearchViewDetail(muniqueID, mSearchResp);
                        if (mSearchSettings.isFilterByDept()) {
                            apiShowDepartmentsOrServices(muniqueID);
                        } else {
                            apiGetUsers(muniqueID);
                            ApiSearchViewLocation(muniqueID);
                        }
                        Config.logV("Location Adapter-----------------------");

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
                    Config.closeDialog(getActivity(), mDialog);
            }
        });
    }


    private void apiSearchViewDetail(final String muniqueID, final List<SearchAWsResponse> mSearchRespPass) {
        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));
        Call<SearchViewDetail> call = apiService.getSearchViewDetail(Integer.parseInt(muniqueID), sdf.format(currentTime));
        call.enqueue(new Callback<SearchViewDetail>() {
            @Override
            public void onResponse(Call<SearchViewDetail> call, final Response<SearchViewDetail> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);
                    Config.logV("URL-----1111----------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-----detail--------------------" + response.code());
                    if (response.code() == 200) {
                        mBusinessDataList = response.body();
                        if (homeUniqueId != null) {
                            ApiAddFavo(mBusinessDataList.getId());
                        }
                        Log.i("plplplp", mBusinessDataList.toString());
//                        Log.i("plplplp", new Gson().toJson(mBusinessDataList));
                        // mbranchId = mBusinessDataList.getBranchId();
                        online_presence = mBusinessDataList.isOnlinePresence();
                        donationFundRaising = mBusinessDataList.isDonationFundRaising();
                        virtualServices = mBusinessDataList.isVirtualServices();
                        lat_long = mBusinessDataList.getBaseLocation().getLattitude() + "," + mBusinessDataList.getBaseLocation().getLongitude();
//                        Config.logV("Provider------------" + new Gson().toJson(mBusinessDataList));
                        if (response.body().getId() != 0) {
                            mProviderId = response.body().getId();
                        }

                        if (from_user) {
                            UpdateMainUIForUser(searchdetailList);

                        } else {
                            UpdateMainUI(mBusinessDataList);
                        }

                        if (from_user) {
                            tv_mImageViewTextnew.setVisibility(View.GONE);
                            if (searchdetailList.getSearchViewDetail().getLogo() != null) {
                                PicassoTrustAll.getInstance(context).load(searchdetailList.getSearchViewDetail().getLogo().getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(mImgeProfile);
                                mImgeProfile.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ArrayList<String> mGalleryList = new ArrayList<>();
                                        if (searchdetailList.getSearchViewDetail().getLogo() != null) {
                                            mGalleryList.add(searchdetailList.getSearchViewDetail().getLogo().getUrl());
                                        }
                                        boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryList, v.getContext());
                                        if (mValue) {
                                            Intent intent = new Intent(mContext, SwipeGalleryImage.class);
                                            intent.putExtra("pos", 0);
                                            startActivity(intent);
                                        }
                                    }
                                });
                            }
                        } else {
                            apiSearchGallery(uniqueID);
                        }
//                        if(homeUniqueId==null){

//                        }else{
//                            ApiSearchGallery(homeUniqueId);
//                        }
                        ApiFavList(mSearchRespPass, claimable);
//                        if (mProviderId != 0) {
//                            APIServiceDepartments(mProviderId);
//                        }
//                        if(homeUniqueId==null){
//                            ApiSearchViewLocation(uniqueID);
////                            listProviders(uniqueID);
//                        }else{

//                            ApiSearchViewLocation(homeUniqueId);
//                            listProviders(homeUniqueId);
//                        }
//                        listDoctorsByDepartment();
//                        apiSearchViewSetting(uniqueID);
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
                    Config.closeDialog(getActivity(), mDialog);
            }
        });
    }

    /**
     * Api to load Jaldee Coupons available of a particular account
     *
     * @param uniqueID unique Id of the account
     */
    private void apiJaldeeCoupon(String uniqueID) {
        couponAvailable = false;
        firstCouponAvailable = false;
        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Call<ArrayList<CoupnResponse>> call = apiService.getCoupanList(Integer.parseInt(uniqueID), sdf.format(currentTime));
        call.enqueue(new Callback<ArrayList<CoupnResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<CoupnResponse>> call, Response<ArrayList<CoupnResponse>> response) {
                try {
                    if (response.code() == 200) {
                        couponResponse = response.body();
                        Log.i("couponRR", couponResponse.toString());
//                        Log.i("couponRR", new Gson().toJson(couponResponse));
                        if (couponResponse.size() > 0) {
                            for (int i = 0; i < couponResponse.size(); i++) {
                                if (couponResponse.get(i).isFirstCheckinOnly()) {
                                    firstCouponAvailable = true;
                                } else {
                                    couponAvailable = true;
                                }
                                if (firstCouponAvailable && couponAvailable) {
                                    break;
                                }
                            }
                        }
                        if (firstCouponAvailable) {
                            tv_first_ccoupon.setVisibility(View.VISIBLE);
                        } else {
                            tv_first_ccoupon.setVisibility(View.GONE);
                        }
                        if (couponAvailable) {
                            tv_coupon.setVisibility(View.VISIBLE);
                        } else {
                            tv_coupon.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<CoupnResponse>> call, Throwable t) {
            }
        });
    }

    /**
     * api to load JDN (Jaldee Discount Network) details of a particular account
     *
     * @param uniqueID unique id of the account
     */
    private void apiJDN(String uniqueID) {
        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));
        Call<JdnResponse> call = apiService.getJdnList(Integer.parseInt(uniqueID), sdf.format(currentTime));
        call.enqueue(new Callback<JdnResponse>() {
            @Override
            public void onResponse(Call<JdnResponse> call, Response<JdnResponse> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);
                    Config.logV("URL-----1111----------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-----detail--------------------" + response.code());
                    if (response.code() == 200) {
                        jdnList = response.body();
                        jdnDiscount = jdnList.getDiscPercentage();
                        jdnMaxvalue = jdnList.getDiscMax();
                        if (new Gson().toJson(jdnList).equals("{}")) {
                            tv_jdn.setVisibility(View.GONE);
                        } else {
                            tv_jdn.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JdnResponse> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);
            }
        });
    }


    private void apiShowDepartmentsOrServices(final String uniqueID) {
        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Call<ArrayList<SearchDepartmentServices>> call = apiService.getDepartmentServices(Integer.parseInt(uniqueID), sdf.format(currentTime));
        call.enqueue(new Callback<ArrayList<SearchDepartmentServices>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchDepartmentServices>> call, Response<ArrayList<SearchDepartmentServices>> response) {
                try {
                    Config.logV("URL--7777-------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------Setting-------------------" + response.code());
                    if (response.code() == 200) {
                        // Department Section Starts
//                        Log.i("DepartmentProviders", new Gson().toJson(response.body()));
                        mSearchDepartmentList = response.body();
                        ApiSearchViewLocation(uniqueID);
                        apiIntegrateWithUsers(uniqueID, response.body());
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

    private void apiIntegrateWithUsers(String uniqueID, final ArrayList<SearchDepartmentServices> deptServices) {
        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Call<ArrayList<SearchDepartmentServices>> call = apiService.getUserandDepartments(Integer.parseInt(uniqueID), sdf.format(currentTime));
        call.enqueue(new Callback<ArrayList<SearchDepartmentServices>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchDepartmentServices>> call, Response<ArrayList<SearchDepartmentServices>> response) {
                try {
                    ArrayList<SearchDepartmentServices> deptMergedList = new ArrayList<>();
                    Config.logV("URL--7777-------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------Setting-------------------" + response.code());
                    if (response.code() == 200) {
                        // Department Section Starts
//                        Log.i("DepartmentProviders", new Gson().toJson(response.body()));
                        ArrayList<SearchDepartmentServices> deptProviders = response.body();
                        if (deptServices != null && deptServices.size() > 0) {
                            for (int dsIndex = 0; dsIndex < deptServices.size(); dsIndex++) {
                                for (int dpIndex = 0; dpIndex < deptProviders.size(); dpIndex++) {
                                    if (deptServices.get(dsIndex).getDepartmentId().equalsIgnoreCase(deptProviders.get(dpIndex).getDepartmentId())) {
                                        SearchDepartmentServices merged_S_P = deptServices.get(dsIndex);
                                        merged_S_P.setUsers(deptProviders.get(dpIndex).getUsers());
                                        deptMergedList.add(merged_S_P);
                                        break;
                                    }
                                }
                            }
                        }
                        Log.i("deptMergedList", new Gson().toJson(deptMergedList));
                        if (deptMergedList.size() > 0) {

                            if (from_user) {
                                mRecycleDepartment.setVisibility(View.GONE);
                            } else {
                                mSearchDepartmentServices.clear();
                                String responses = new Gson().toJson(response.body());
                                Config.logV("Deapartnamesss---------------" + responses);
                                for (int i = 0; i < deptMergedList.size(); i++) {
                                    departmentNameList.add(deptMergedList.get(i).getDepartmentName());
                                    departmentCodeList.add(deptMergedList.get(i).getDepartmentCode());
                                }
                                mSearchDepartmentServices.addAll(deptMergedList);
                                if (mSearchDepartmentServices != null) {
                                    if (mSearchDepartmentServices.size() == 1) {
                                        departmentHeading.setVisibility(View.VISIBLE);
                                        departmentHeading.setText("Department (1)");
                                    } else if (mSearchDepartmentServices.size() > 1) {
                                        departmentHeading.setVisibility(View.VISIBLE);
                                        departmentHeading.setText("Departments " + "(" + mSearchDepartmentServices.size() + ")");
                                    }
                                } else {
                                    departmentHeading.setVisibility(View.GONE);
                                }
                                Log.i("departmentservice", new Gson().toJson(mSearchDepartmentServices));
                                Config.logV("DepartmEntqweCode --------------" + departmentCodeList);
                                Config.logV("DepartmEntqweName --------------" + departmentNameList);
                                Log.i("SizeofDepartments", String.valueOf(mSearchDepartmentServices.size()));
                                RecyclerView.LayoutManager mDepartmentLayout = new LinearLayoutManager(mContext);
                                mRecycleDepartment.setVisibility(View.VISIBLE);
                                mRecycleDepartment.setLayoutManager(mDepartmentLayout);
                                mDepartmentAdapter.setFields(deptMergedList, mBusinessDataList.getBusinessName(), userTerminology);
                                mRecycleDepartment.setAdapter(mDepartmentAdapter);
                                mDepartmentAdapter.notifyDataSetChanged();
                            }
                        }
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

    private void apiGetUsers(String muniqueID) {

        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Call<ArrayList<ProviderUserModel>> call = apiService.getUsers(Integer.parseInt(uniqueID), sdf.format(currentTime));
        call.enqueue(new Callback<ArrayList<ProviderUserModel>>() {
            @Override
            public void onResponse(Call<ArrayList<ProviderUserModel>> call, Response<ArrayList<ProviderUserModel>> response) {
                try {
                    Config.logV("URL--7777-------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------Setting-------------------" + response.code());
                    if (response.code() == 200) {
                        if (response.body() != null) {
                            usersList.clear();
                            usersList = response.body();
                            if (usersList.size() > 0) {
                                String name = userTerminology;
                                name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                                if (from_user) {
                                    mRecycleDepartment.setVisibility(View.GONE);
                                } else {
                                    if (usersList != null) {

                                        if (usersList.size() == 1) {
                                            departmentHeading.setVisibility(View.VISIBLE);
                                            if (userTerminology != null) {
                                                departmentHeading.setText(name + "(1)");
                                            }
                                        } else if (usersList.size() > 1) {
                                            departmentHeading.setVisibility(View.VISIBLE);
                                            if (userTerminology != null) {
                                                departmentHeading.setText(name + "(" + usersList.size() + ")");
                                            }
                                        }
                                    } else {
                                        departmentHeading.setVisibility(View.GONE);
                                    }

                                    tvterminology.setText("Our "+name +"s");

                                    cvUsers.setVisibility(View.VISIBLE);
                                    cvUsers.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            onMethodUsersClick(usersList, mBusinessDataList.getBusinessName(), userTerminology);
                                        }
                                    });
//                                    RecyclerView.LayoutManager mDepartmentLayout = new LinearLayoutManager(mContext);
//                                    mRecycleDepartment.setVisibility(View.GONE);
//                                    mRecycleDepartment.setLayoutManager(mDepartmentLayout);
//                                    usersAdapter.setFields(usersList,mBusinessDataList.getBusinessName());
//                                    mRecycleDepartment.setAdapter(usersAdapter);
//                                    usersAdapter.notifyDataSetChanged();
                                }
                            }
                        }
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


    private void ApiCommunicate(String accountID, String message, final BottomSheetDialog mBottomDialog) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("communicationMessage", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call = apiService.PostMessage(accountID, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        Toast.makeText(mContext, "Message sent successfully", Toast.LENGTH_LONG).show();
                        mBottomDialog.dismiss();
                    }
                    else if(response.code() == 403){
                        Toast.makeText(mContext,"Please complete the details of profile name,location and working hours to continue",Toast.LENGTH_LONG).show();
                    }
                    else if(response.code() == 404){
                        Toast.makeText(mContext, "The account doesn't exist", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                mBottomDialog.dismiss();
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);
            }
        });
    }

    private void ApiAppointmentServices(String muniqueID) {
        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));
        Call<ArrayList<SearchAppointmentDepartmentServices>> call = apiService.getAppointmentServices(Integer.parseInt(muniqueID), sdf.format(currentTime));
        call.enqueue(new Callback<ArrayList<SearchAppointmentDepartmentServices>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchAppointmentDepartmentServices>> call, Response<ArrayList<SearchAppointmentDepartmentServices>> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);
                    Config.logV("URL--7777-------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------Setting-------------------" + response.code());
                    if (response.code() == 200) {
                        LaServicesList.clear();
                        aServiceList.clear();
                        for (int i = 0; i < response.body().size(); i++) {
                            if (response.body().get(i).getServices() == null) {
                                SearchAppointmentDepartmentServices mService = new SearchAppointmentDepartmentServices();
                                mService.setName(response.body().get(i).getName());
                                mService.setDescription(response.body().get(i).getDescription());
                                mService.setTotalAmount(response.body().get(i).getTotalAmount());
                                mService.setServiceDuration(response.body().get(i).getServiceDuration());
                                mService.setTaxable(response.body().get(i).isTaxable());
                                mService.setServicegallery(response.body().get(i).getServicegallery());
                                mService.setPrePayment(response.body().get(i).isPrePayment());
                                mService.setMinPrePaymentAmount(response.body().get(i).getMinPrePaymentAmount());
                                mService.setUrl(response.body().get(i).getUrl());
                                mService.setThumbUrl(response.body().get(i).getThumbUrl());
                                mService.setServiceType(response.body().get(i).getServiceType());
                                mService.setVirtualCallingModes(response.body().get(i).getVirtualCallingModes());
                                mService.setCallingMode(response.body().get(i).getCallingMode());

                                LaServicesList.add(mService);
                            } else {
                                SearchAppointmentDepartmentServices mService = new SearchAppointmentDepartmentServices();
                                mService.setDepartmentName(response.body().get(i).getDepartmentName());
                                mService.setServices(response.body().get(i).getServices());
                                mService.setName(response.body().get(i).getName());
                                mService.setDepartmentId(response.body().get(i).getDepartmentId());
                                mService.setServiceType(response.body().get(i).getServiceType());
                                mService.setVirtualCallingModes(response.body().get(i).getVirtualCallingModes());
                                mService.setCallingMode(response.body().get(i).getCallingMode());
                                LaServicesList.add(mService);
                            }
                        }
                        aServiceList = response.body();
//                        for (int i = 0; i < response.body().size(); i++) {
//                            SearchAppointmentDepartmentServices mService = new SearchAppointmentDepartmentServices();
//                            mService.setDepartmentName();Name(response.body().get(i).getName());
//                            mService.setId(response.body().get(i).getId());
//                            mService.setLivetrack(response.body().get(i).getLivetrack());
//                            mService.setIsPrePayment(response.body().get(i).getIsPrePayment());
//                            mService.setTotalAmount(response.body().get(i).getTotalAmount());
//                            mService.setMinPrePaymentAmount(response.body().get(i).getMinPrePaymentAmount());
//                            mService.setServiceType(response.body().get(i).getServiceType());
//                            mService.setVirtualServiceType(response.body().get(i).getVirtualServiceType());
//                            mService.setVirtualCallingModes(response.body().get(i).getVirtualCallingModes());
//                            mService.setInstructions(response.body().get(i).getInstructions());
//                            mService.setCallingMode(response.body().get(i).getCallingMode());
//                            mService.setValue(response.body().get(i).getValue());
//                            LaServicesList.add(mService);
//                        }
//                        aServiceList.addAll(LaServicesList);
                        // Department Section Starts
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<SearchAppointmentDepartmentServices>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);
            }
        });
    }

    private void ApiDonationServices(final int id) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
//        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
//        mDialog.show();
        Call<ArrayList<SearchDonation>> call = apiService.getSearchDonation(mProviderId);
        call.enqueue(new Callback<ArrayList<SearchDonation>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchDonation>> call, Response<ArrayList<SearchDonation>> response) {
                try {
                    //  if (mDialog.isShowing())
                    //  Config.closeDialog(getParent(), mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        LServicesList.clear();
                        gServiceList.clear();
                        for (int i = 0; i < response.body().size(); i++) {
                            SearchDonation mService = new SearchDonation();
                            mService.setName(response.body().get(i).getName());
                            mService.setId(id);
                            mService.setId(response.body().get(i).getId());
                            mService.setLivetrack(response.body().get(i).getLivetrack());
                            mService.setPrePayment(response.body().get(i).isPrePayment());
                            mService.setTotalAmount(response.body().get(i).getTotalAmount());
                            mService.setMinPrePaymentAmount(response.body().get(i).getMinPrePaymentAmount());
                            mService.setServiceType(response.body().get(i).getServiceType());
                            mService.setMultiples(response.body().get(i).getMultiples());
                            mService.setMinDonationAmount(response.body().get(i).getMinDonationAmount());
                            mService.setMaxDonationAmount(response.body().get(i).getMaxDonationAmount());
                            mService.setServicegallery(response.body().get(i).getServicegallery());
                            LServicesList.add(mService);
                        }
                        gServiceList.addAll(LServicesList);
                        // Department Section Starts
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<SearchDonation>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                //  if (mDialog.isShowing())
                //  Config.closeDialog(getParent(), mDialog);
            }
        });
    }

    public void UpdateGallery(final ArrayList<SearchViewDetail> mGallery) {
        //  Picasso.with(this).load(mGallery.get(0).getUrl()).fit().into(mImgeProfile);
        Config.logV("Gallery--------------333-----" + mGallery.size());
        try {
            if (mGallery.size() > 0 || mBusinessDataList.getLogo() != null) {
                mImgeProfile.setOnClickListener(new View.OnClickListener() {
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
                            Intent intent = new Intent(mContext, SwipeGalleryImage.class);
                            intent.putExtra("pos", 0);
                            startActivity(intent);
                        }
                    }
                });
                mImgthumbProfile.setOnClickListener(new View.OnClickListener() {
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
                            Intent intent = new Intent(mContext, SwipeGalleryImage.class);
                            intent.putExtra("pos", 1);
                            startActivity(intent);
                        }
                    }
                });
                mImgthumbProfile1.setOnClickListener(new View.OnClickListener() {
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
                            Intent intent = new Intent(mContext, SwipeGalleryImage.class);
                            intent.putExtra("pos", 2);
                            startActivity(intent);
                        }
                    }
                });
            } /*else {
                tv_Gallery.setVisibility(View.GONE);
            }*/
            Config.logV("Bussiness logo @@@@@@@@@@" + mBusinessDataList.getLogo());
            if (mBusinessDataList.getLogo() != null) {
                PicassoTrustAll.getInstance(context).load(mBusinessDataList.getLogo().getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(mImgeProfile);
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


    /*  ArrayList<SearchViewDetail> emails = new ArrayList<>();
      ArrayList<SearchViewDetail> phoneNumber = new ArrayList<>();*/
    ArrayList<ContactModel> contactDetail = new ArrayList<>();
    boolean isContact = false;
    ArrayList<SocialMediaModel> socialMedia = new ArrayList<>();

    //  boolean expand =false;
    public void UpdateMainUI(final SearchViewDetail getBussinessData) {

        try {
            getBussinessData.getSpecialization().removeAll(Collections.singleton(null));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getBussinessData.getSpecialization() != null) {
            for (int i = 0; i < getBussinessData.getSpecialization().size(); i++) {
                if (getBussinessData.getSpecialization().get(i).equals("Not Applicabale") || getBussinessData.getSpecialization().get(i).equals("Not Applicable")) {
                    getBussinessData.getSpecialization().remove(i);
                }
            }

            if (getBussinessData.getSpecialization().size() > 0) {
                tv_specializtion.setVisibility(View.VISIBLE);
                mrecycle_specialisation.setVisibility(View.VISIBLE);
                specialSeeAll.setVisibility(View.VISIBLE);
                int size = getBussinessData.getSpecialization().size();
                if (size > 0) {
                    if (size == 1) {
                        mrecycle_specialisation.setVisibility(View.GONE);
                        specialSeeAll.setVisibility(View.GONE);
                        LSpecialization_2.setVisibility(View.VISIBLE);
                        tv_spec1.setVisibility(View.VISIBLE);
                        tv_spec2.setVisibility(View.GONE);
                        tv_seeAll.setVisibility(View.GONE);
                        if (getBussinessData.getSpecialization().get(0).toString() != null) {
                            tv_spec1.setText(getBussinessData.getSpecialization().get(0).toString());
                        }
                    } else if (size == 2) {
                        mrecycle_specialisation.setVisibility(View.GONE);
                        specialSeeAll.setVisibility(View.GONE);
                        LSpecialization_2.setVisibility(View.VISIBLE);
                        tv_spec1.setVisibility(View.VISIBLE);
                        tv_spec2.setVisibility(View.GONE);
                        tv_seeAll.setVisibility(View.GONE);
                        if (getBussinessData.getSpecialization().get(0).toString() != null) {
                            tv_spec1.setText(getBussinessData.getSpecialization().get(0).toString() + ", " + getBussinessData.getSpecialization().get(1).toString());
                        }
                    } else {
                        mrecycle_specialisation.setVisibility(View.GONE);
                        specialSeeAll.setVisibility(View.GONE);
                        LSpecialization_2.setVisibility(View.VISIBLE);
                        tv_spec1.setVisibility(View.VISIBLE);
                        String specialization = getBussinessData.getSpecialization().get(0).toString() + ", " + getBussinessData.getSpecialization().get(1).toString() + ", ";
                        int remainingCount = size - 2;
                        tv_seeAll.setText("+" + remainingCount + " " + "more");
                        String more = tv_seeAll.getText().toString();
                        final Spannable spannable1 = new SpannableString(specialization + " " + more);
                        spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.black)),
                                0, more.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        final Spannable seeAll = new SpannableString(spannable1);
                        ClickableSpan clickableSpan = new ClickableSpan() {
                            @Override
                            public void onClick(@NonNull View widget) {
                                LSpecialization_2.setVisibility(View.GONE);
                                mrecycle_specialisation.setVisibility(View.VISIBLE);
                                specialSeeAll.setVisibility(View.VISIBLE);
                                LSpecialization.removeAllViews();
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                                mrecycle_specialisation.setLayoutManager(mLayoutManager);
                                sAdapter = new SpecialisationAdapter(getBussinessData);
                                mrecycle_specialisation.setAdapter(sAdapter);
                                sAdapter.notifyDataSetChanged();
                                SpannableString spanStr = new SpannableString("See less");
                                spanStr.setSpan(new UnderlineSpan(), 0, spanStr.length(), 0);
                                specialSeeAll.setText(spanStr);
                                specialSeeAll.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        LSpecialization_2.setVisibility(View.VISIBLE);
                                        LSpecialization.setVisibility(View.GONE);
                                        tv_spec1.setVisibility(View.VISIBLE);
                                        tv_seeAll.setVisibility(View.VISIBLE);
                                        tv_spec1.setText(seeAll);
                                        mrecycle_specialisation.setVisibility(View.GONE);
                                        specialSeeAll.setVisibility(View.GONE);
                                    }
                                });
                                LinearLayout parent1 = new LinearLayout(mContext);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                parent1.setOrientation(LinearLayout.VERTICAL);
                                parent1.setLayoutParams(params);
                                for (int i = 0; i < getBussinessData.getSpecialization().size(); i++) {
                                    TextView dynaText = new TextView(mContext);
                                    dynaText.setText(getBussinessData.getSpecialization().get(i).toString());
                                    dynaText.getLineSpacingExtra();
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        dynaText.getJustificationMode();
                                    }
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        dynaText.getLetterSpacing();
                                    }
                                    dynaText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                                    dynaText.setTextColor(mContext.getResources().getColor(R.color.black));
                                    //  dynaText.setPadding(5, 5, 5, 5);
                                    dynaText.setMaxLines(1);
                                    dynaText.setLayoutParams(params);
                                    params.setMargins(0, 10, 0, 0);
                                    dynaText.setGravity(Gravity.LEFT);
                                    parent1.addView(dynaText);
                                }
                                TextView dynaText = new TextView(mContext);
                                Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                                        "fonts/Montserrat_Regular.otf");
                                dynaText.setTypeface(tyface);
                                dynaText.setText("See Less");
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    dynaText.getJustificationMode();
                                }
                                dynaText.getLineSpacingExtra();
                                dynaText.getLineSpacingExtra();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    dynaText.getLetterSpacing();
                                }
                                dynaText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                                dynaText.setTextColor(mContext.getResources().getColor(R.color.title_consu));
                                // dynaText.setPadding(5, 5, 5, 5);
                                dynaText.setMaxLines(1);
                                dynaText.setLayoutParams(params);
                                params.setMargins(0, 10, 0, 0);
                                dynaText.setGravity(Gravity.LEFT);
                                parent1.addView(dynaText);
                                dynaText.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        LSpecialization_2.setVisibility(View.VISIBLE);
                                        LSpecialization.setVisibility(View.GONE);
                                        tv_spec1.setVisibility(View.VISIBLE);
                                        tv_spec2.setVisibility(View.VISIBLE);
                                        tv_seeAll.setVisibility(View.VISIBLE);
                                        tv_spec1.setText(getBussinessData.getSpecialization().get(0).toString() + ", ");
                                        tv_spec2.setText(getBussinessData.getSpecialization().get(1).toString());
                                    }
                                });
                                LSpecialization.addView(parent1);
                            }
                        };
                        seeAll.setSpan(clickableSpan, specialization.length(), specialization.length() + more.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        seeAll.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.title_consu)),
                                specialization.length(), specialization.length() + more.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv_spec1.setText(seeAll);
                        tv_spec1.setMovementMethod(LinkMovementMethod.getInstance());
                    }
                }
            } else {
                tv_specializtion.setVisibility(View.GONE);
                LSpecialization.setVisibility(View.GONE);
                LSpecialization_2.setVisibility(View.GONE);
            }
        } else {
            tv_specializtion.setVisibility(View.GONE);
            LSpecialization.setVisibility(View.GONE);
            LSpecialization_2.setVisibility(View.GONE);
        }
        if (getBussinessData.getSocialMedia() != null) {
            if (getBussinessData.getSocialMedia().size() > 0) {
                LsocialMedia.setVisibility(View.GONE);
                for (int i = 0; i < getBussinessData.getSocialMedia().size(); i++) {
                    if (getBussinessData.getSocialMedia().get(i).getResource().equalsIgnoreCase("facebook")) {
//                        tv_SocialMedia.setVisibility(View.VISIBLE);
                        ic_fac.setVisibility(View.VISIBLE);
                        final int finalI = i;
                        ic_fac.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getBussinessData.getSocialMedia().get(finalI).getValue()));
                                    startActivity(myIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(mContext, "No application can handle this request."
                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    if (getBussinessData.getSocialMedia().get(i).getResource().equalsIgnoreCase("googleplus")) {
//                        tv_SocialMedia.setVisibility(View.VISIBLE);
                        ic_gplus.setVisibility(View.VISIBLE);
                        final int finalI3 = i;
                        ic_gplus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getBussinessData.getSocialMedia().get(finalI3).getValue()));
                                    startActivity(myIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(mContext, "No application can handle this request."
                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    if (getBussinessData.getSocialMedia().get(i).getResource().equalsIgnoreCase("twitter")) {
//                        tv_SocialMedia.setVisibility(View.VISIBLE);
                        ic_twitt.setVisibility(View.VISIBLE);
                        final int finalI1 = i;
                        ic_twitt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getBussinessData.getSocialMedia().get(finalI1).getValue()));
                                    startActivity(myIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(mContext, "No application can handle this request."
                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    if (getBussinessData.getSocialMedia().get(i).getResource().equalsIgnoreCase("linkedin")) {
//                        tv_SocialMedia.setVisibility(View.VISIBLE);
                        ic_link.setVisibility(View.VISIBLE);
                        final int finalI5 = i;
                        ic_link.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getBussinessData.getSocialMedia().get(finalI5).getValue()));
                                    startActivity(myIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(mContext, "No application can handle this request."
                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    if (getBussinessData.getSocialMedia().get(i).getResource().equalsIgnoreCase("pinterest")) {
//                        tv_SocialMedia.setVisibility(View.VISIBLE);
                        ic_pin.setVisibility(View.VISIBLE);
                        final int finalI4 = i;
                        ic_pin.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getBussinessData.getSocialMedia().get(finalI4).getValue()));
                                    startActivity(myIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(mContext, "No application can handle this request."
                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    if (getBussinessData.getSocialMedia().get(i).getResource().equalsIgnoreCase("youtube")) {
//                        tv_SocialMedia.setVisibility(View.VISIBLE);
                        ic_yout.setVisibility(View.VISIBLE);
                        final int finalI2 = i;
                        ic_yout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getBussinessData.getSocialMedia().get(finalI2).getValue()));
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
                LsocialMedia.setVisibility(View.GONE);
                tv_SocialMedia.setVisibility(View.GONE);
            }
        } else {
            LsocialMedia.setVisibility(View.GONE);
            tv_SocialMedia.setVisibility(View.GONE);
        }
        contactDetail.clear();
        if (getBussinessData.getPhoneNumbers().size() > 0) {
            for (int i = 0; i < getBussinessData.getPhoneNumbers().size(); i++) {
                Config.logV("Phone @@@@@@@@@@@@" + getBussinessData.getPhoneNumbers().get(i).getInstance());
                ContactModel contact = new ContactModel();
                contact.setInstance(getBussinessData.getPhoneNumbers().get(i).getInstance());
                contact.setResource(getBussinessData.getPhoneNumbers().get(i).getResource());
                contact.setLabel(getBussinessData.getPhoneNumbers().get(i).getLabel());
                contactDetail.add(contact);
            }
        }
        if (getBussinessData.getEmails().size() > 0) {
            for (int i = 0; i < getBussinessData.getEmails().size(); i++) {
                ContactModel contact = new ContactModel();
                contact.setInstance(getBussinessData.getEmails().get(i).getInstance());
                contact.setResource(getBussinessData.getEmails().get(i).getResource());
                contact.setLabel(getBussinessData.getEmails().get(i).getLabel());
                contactDetail.add(contact);
            }
        }

        if (getBussinessData.getPhoneNumbers().size() > 0 || getBussinessData.getEmails().size() > 0 && contactDetail.size() > 0) {
            tv_contact.setVisibility(View.VISIBLE);
            tv_contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isContact) {
                        Config.logV("Open");
                        isContact = true;
                        tv_contact.setText("Contact");
                        tv_contact.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.contact_selected, 0, 0);
                        BottomSheetContactDialog();
                    } else {
                        Config.logV("CLosed");
                    }
                }
            });
        } else {
            tv_contact.setVisibility(View.GONE);
        }

        if (getBussinessData.getVerifyLevel() != null) {
            if (!getBussinessData.getVerifyLevel().equalsIgnoreCase("NONE")) {
                ic_jaldeeverifiedIcon.setVisibility(View.VISIBLE);
                if (getBussinessData.getVerifyLevel().equalsIgnoreCase("BASIC_PLUS")) {
                    ic_jaldeeverifiedIcon.setImageResource(R.drawable.jaldee_basicplus);
                }
                if (getBussinessData.getVerifyLevel().equalsIgnoreCase("BASIC")) {
                    ic_jaldeeverifiedIcon.setImageResource(R.drawable.jaldee_basic);
                }
                if (getBussinessData.getVerifyLevel().equalsIgnoreCase("PREMIUM") || getBussinessData.getVerifyLevel().equalsIgnoreCase("ADVANCED")) {
                    ic_jaldeeverifiedIcon.setImageResource(R.drawable.jaldee_adv);
                }
            } else {
                ic_jaldeeverifiedIcon.setVisibility(View.GONE);
            }

            ic_jaldeeverifiedIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String ynw_verified = null;
                    if (getBussinessData.getVerifyLevel().equalsIgnoreCase("BASIC"))
                        ynw_verified = "2";
                    else if (getBussinessData.getVerifyLevel().equalsIgnoreCase("BASIC_PLUS"))
                        ynw_verified = "3";
                    else if (getBussinessData.getVerifyLevel().equalsIgnoreCase("PREMIUM") || getBussinessData.getVerifyLevel().equalsIgnoreCase("ADVANCED"))
                        ynw_verified = "4";
                    Config.logV("YNW VERIFIED@@@@@@@@@@@@" + ynw_verified);
                    CustomDialog cdd = new CustomDialog(mContext, ynw_verified, getBussinessData.getBusinessName());
                    cdd.setCanceledOnTouchOutside(true);
                    cdd.show();
                }
            });
        } else {
            ic_jaldeeverifiedIcon.setVisibility(View.GONE);
        }
        tv_msg.setEnabled(true);

        tv_busName.setText(getBussinessData.getBusinessName());

        try {

            int rate = Math.round(searchdetailList.getSearchViewDetail().getAvgRating());
            if (rate < 4) {
                rating.setVisibility(View.GONE);
            } else {
                rating.setVisibility(View.VISIBLE);
                rating.setRating(searchdetailList.getSearchViewDetail().getAvgRating());
            }
        } catch (Exception e) {

        }
        if (getBussinessData.getServiceSector().getDisplayName() != null && getBussinessData.getServiceSubSector().getDisplayName() != null) {
            if (getBussinessData.getServiceSector().getDisplayName().equalsIgnoreCase("Other / Miscellaneous")) {
                tv_domain.setVisibility(View.GONE);
            } else {
                tv_domain.setText(getBussinessData.getServiceSector().getDisplayName()); //+ " " + "(" + getBussinessData.getServiceSubSector().getDisplayName() + ")");
            }
        }
        if (getBussinessData.getBusinessDesc() != null) {
            tv_desc.setVisibility(View.VISIBLE);
            tv_desc.setText(getBussinessData.getBusinessDesc());
            tv_desc.post(new Runnable() {
                @Override
                public void run() {
                    int lineCount = tv_desc.getLineCount();
                    //Config.logV("No of line---------------" + lineCount + "Name" + inboxList.getUserName());
                    if (lineCount > 3) {
                        ResizableCustomView.doResizeTextView(mContext, tv_desc, 3, "..more", true);
                    } else {
                    }
                    // Use lineCount here
                }
            });
        } else {
            tv_desc.setVisibility(View.GONE);
        }
    }

    public void UpdateMainUIForUser(DepartmentUserSearchModel searchdetailList) {
        if (searchdetailList.getSearchViewDetail().getSpecialization() != null) {
            for (int i = 0; i < searchdetailList.getSearchViewDetail().getSpecialization().size(); i++) {
                if (searchdetailList.getSearchViewDetail().getSpecialization().get(i).equals("Not Applicabale") || searchdetailList.getSearchViewDetail().getSpecialization().get(i).equals("Not Applicable")) {
                    searchdetailList.getSearchViewDetail().getSpecialization().remove(i);
                }
            }

            if (searchdetailList.getSearchViewDetail().getSpecialization().size() > 0) {
                tv_specializtion.setVisibility(View.VISIBLE);
                mrecycle_specialisation.setVisibility(View.VISIBLE);
                specialSeeAll.setVisibility(View.VISIBLE);
                int size = searchdetailList.getSearchViewDetail().getSpecialization().size();
                if (size > 0) {
                    if (size == 1) {
                        mrecycle_specialisation.setVisibility(View.GONE);
                        specialSeeAll.setVisibility(View.GONE);
                        LSpecialization_2.setVisibility(View.VISIBLE);
                        tv_spec1.setVisibility(View.VISIBLE);
                        tv_spec2.setVisibility(View.GONE);
                        tv_seeAll.setVisibility(View.GONE);
                        if (searchdetailList.getSearchViewDetail().getSpecialization().get(0).toString() != null) {
                            tv_spec1.setText(searchdetailList.getSearchViewDetail().getSpecialization().get(0).toString());
                        }
                    } else if (size == 2) {
                        mrecycle_specialisation.setVisibility(View.GONE);
                        specialSeeAll.setVisibility(View.GONE);
                        LSpecialization_2.setVisibility(View.VISIBLE);
                        tv_spec1.setVisibility(View.VISIBLE);
                        tv_spec2.setVisibility(View.GONE);
                        tv_seeAll.setVisibility(View.GONE);
                        if (searchdetailList.getSearchViewDetail().getSpecialization().get(0).toString() != null) {
                            tv_spec1.setText(searchdetailList.getSearchViewDetail().getSpecialization().get(0).toString() + ", " + searchdetailList.getSearchViewDetail().getSpecialization().get(1).toString());
                        }
                    } else {
                        mrecycle_specialisation.setVisibility(View.GONE);
                        specialSeeAll.setVisibility(View.GONE);
                        LSpecialization_2.setVisibility(View.VISIBLE);
                        tv_spec1.setVisibility(View.VISIBLE);
                        String specialization = searchdetailList.getSearchViewDetail().getSpecialization().get(0).toString() + ", " + searchdetailList.getSearchViewDetail().getSpecialization().get(1).toString() + ", ";
                        int remainingCount = size - 2;
                        tv_seeAll.setText("+" + remainingCount + " " + "more");
                        String more = tv_seeAll.getText().toString();
                        final Spannable spannable1 = new SpannableString(specialization + " " + more);
                        spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.black)),
                                0, more.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        final Spannable seeAll = new SpannableString(spannable1);
                        ClickableSpan clickableSpan = new ClickableSpan() {
                            @Override
                            public void onClick(@NonNull View widget) {
                                LSpecialization_2.setVisibility(View.GONE);
                                mrecycle_specialisation.setVisibility(View.VISIBLE);
                                specialSeeAll.setVisibility(View.VISIBLE);
                                LSpecialization.removeAllViews();
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                                mrecycle_specialisation.setLayoutManager(mLayoutManager);
                                sAdapter = new SpecialisationAdapter(searchdetailList.getSearchViewDetail());
                                mrecycle_specialisation.setAdapter(sAdapter);
                                sAdapter.notifyDataSetChanged();
                                SpannableString spanStr = new SpannableString("See less");
                                spanStr.setSpan(new UnderlineSpan(), 0, spanStr.length(), 0);
                                specialSeeAll.setText(spanStr);
                                specialSeeAll.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        LSpecialization_2.setVisibility(View.VISIBLE);
                                        LSpecialization.setVisibility(View.GONE);
                                        tv_spec1.setVisibility(View.VISIBLE);
                                        tv_seeAll.setVisibility(View.VISIBLE);
                                        tv_spec1.setText(seeAll);
                                        mrecycle_specialisation.setVisibility(View.GONE);
                                        specialSeeAll.setVisibility(View.GONE);
                                    }
                                });
                                LinearLayout parent1 = new LinearLayout(mContext);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                parent1.setOrientation(LinearLayout.VERTICAL);
                                parent1.setLayoutParams(params);
                                for (int i = 0; i < searchdetailList.getSearchViewDetail().getSpecialization().size(); i++) {
                                    TextView dynaText = new TextView(mContext);
                                    dynaText.setText(searchdetailList.getSearchViewDetail().getSpecialization().get(i).toString());
                                    dynaText.getLineSpacingExtra();
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        dynaText.getJustificationMode();
                                    }
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        dynaText.getLetterSpacing();
                                    }
                                    dynaText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                                    dynaText.setTextColor(mContext.getResources().getColor(R.color.black));
                                    //  dynaText.setPadding(5, 5, 5, 5);
                                    dynaText.setMaxLines(1);
                                    dynaText.setLayoutParams(params);
                                    params.setMargins(0, 10, 0, 0);
                                    dynaText.setGravity(Gravity.LEFT);
                                    parent1.addView(dynaText);
                                }
                                TextView dynaText = new TextView(mContext);
                                Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                                        "fonts/Montserrat_Regular.otf");
                                dynaText.setTypeface(tyface);
                                dynaText.setText("See Less");
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    dynaText.getJustificationMode();
                                }
                                dynaText.getLineSpacingExtra();
                                dynaText.getLineSpacingExtra();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    dynaText.getLetterSpacing();
                                }
                                dynaText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                                dynaText.setTextColor(mContext.getResources().getColor(R.color.title_consu));
                                // dynaText.setPadding(5, 5, 5, 5);
                                dynaText.setMaxLines(1);
                                dynaText.setLayoutParams(params);
                                params.setMargins(0, 10, 0, 0);
                                dynaText.setGravity(Gravity.LEFT);
                                parent1.addView(dynaText);
                                dynaText.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        LSpecialization_2.setVisibility(View.VISIBLE);
                                        LSpecialization.setVisibility(View.GONE);
                                        tv_spec1.setVisibility(View.VISIBLE);
                                        tv_spec2.setVisibility(View.VISIBLE);
                                        tv_seeAll.setVisibility(View.VISIBLE);
                                        tv_spec1.setText(searchdetailList.getSearchViewDetail().getSpecialization().get(0).toString() + ", ");
                                        tv_spec2.setText(searchdetailList.getSearchViewDetail().getSpecialization().get(1).toString());
                                    }
                                });
                                LSpecialization.addView(parent1);
                            }
                        };
                        seeAll.setSpan(clickableSpan, specialization.length(), specialization.length() + more.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        seeAll.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.title_consu)),
                                specialization.length(), specialization.length() + more.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv_spec1.setText(seeAll);
                        tv_spec1.setMovementMethod(LinkMovementMethod.getInstance());
                    }
                }
            } else {
                tv_specializtion.setVisibility(View.GONE);
                LSpecialization.setVisibility(View.GONE);
                LSpecialization_2.setVisibility(View.GONE);
            }
        } else {
            tv_specializtion.setVisibility(View.GONE);
            LSpecialization.setVisibility(View.GONE);
            LSpecialization_2.setVisibility(View.GONE);
        }
        tv_busName.setText(this.searchdetailList.getSearchViewDetail().getBusinessName());
        DatabaseHandler db = new DatabaseHandler(context);
        domainList = db.getSubDomainsByFilter(searchdetailList.getSearchViewDetail().getServiceSector().getDisplayName(), searchdetailList.getSearchViewDetail().getUserSubdomain());

        if (domainList.getDisplayname().equalsIgnoreCase("Other / Miscellaneous")) {
            tv_domain.setVisibility(View.GONE);
        } else {
           // tv_domain.setText(domainList.getDisplayname());
        }

        if (searchdetailList.getSearchViewDetail().getSocialMedia() != null) {
            if (searchdetailList.getSearchViewDetail().getSocialMedia().size() > 0) {
                LsocialMedia.setVisibility(View.GONE);
                for (int i = 0; i < searchdetailList.getSearchViewDetail().getSocialMedia().size(); i++) {
                    if (searchdetailList.getSearchViewDetail().getSocialMedia().get(i).getResource().equalsIgnoreCase("facebook")) {

//                        tv_SocialMedia.setVisibility(View.VISIBLE);
                        ic_fac.setVisibility(View.VISIBLE);
                        final int finalI = i;
                        ic_fac.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchdetailList.getSearchViewDetail().getSocialMedia().get(finalI).getValue()));
                                    startActivity(myIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(mContext, "No application can handle this request."
                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    if (searchdetailList.getSearchViewDetail().getSocialMedia().get(i).getResource().equalsIgnoreCase("googleplus")) {
//                        tv_SocialMedia.setVisibility(View.VISIBLE);
                        ic_gplus.setVisibility(View.VISIBLE);
                        final int finalI3 = i;
                        ic_gplus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchdetailList.getSearchViewDetail().getSocialMedia().get(finalI3).getValue()));
                                    startActivity(myIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(mContext, "No application can handle this request."
                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    if (searchdetailList.getSearchViewDetail().getSocialMedia().get(i).getResource().equalsIgnoreCase("twitter")) {
//                        tv_SocialMedia.setVisibility(View.VISIBLE);
                        ic_twitt.setVisibility(View.VISIBLE);
                        final int finalI1 = i;
                        ic_twitt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchdetailList.getSearchViewDetail().getSocialMedia().get(finalI1).getValue()));
                                    startActivity(myIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(mContext, "No application can handle this request."
                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    if (searchdetailList.getSearchViewDetail().getSocialMedia().get(i).getResource().equalsIgnoreCase("linkedin")) {
//                        tv_SocialMedia.setVisibility(View.VISIBLE);
                        ic_link.setVisibility(View.VISIBLE);
                        final int finalI5 = i;
                        ic_link.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchdetailList.getSearchViewDetail().getSocialMedia().get(finalI5).getValue()));
                                    startActivity(myIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(mContext, "No application can handle this request."
                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    if (searchdetailList.getSearchViewDetail().getSocialMedia().get(i).getResource().equalsIgnoreCase("pinterest")) {
//                        tv_SocialMedia.setVisibility(View.VISIBLE);
                        ic_pin.setVisibility(View.VISIBLE);
                        final int finalI4 = i;
                        ic_pin.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchdetailList.getSearchViewDetail().getSocialMedia().get(finalI4).getValue()));
                                    startActivity(myIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(mContext, "No application can handle this request."
                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    if (searchdetailList.getSearchViewDetail().getSocialMedia().get(i).getResource().equalsIgnoreCase("youtube")) {
//                        tv_SocialMedia.setVisibility(View.VISIBLE);
                        ic_yout.setVisibility(View.VISIBLE);
                        final int finalI2 = i;
                        ic_yout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchdetailList.getSearchViewDetail().getSocialMedia().get(finalI2).getValue()));
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
                LsocialMedia.setVisibility(View.GONE);
                tv_SocialMedia.setVisibility(View.GONE);
            }
        } else {
            LsocialMedia.setVisibility(View.GONE);
            tv_SocialMedia.setVisibility(View.GONE);
        }
        contactDetail.clear();
        if (searchdetailList.getSearchViewDetail().getPhoneNumbers() != null && searchdetailList.getSearchViewDetail().getPhoneNumbers().size() > 0) {
            for (int i = 0; i < searchdetailList.getSearchViewDetail().getPhoneNumbers().size(); i++) {
                Config.logV("Phone @@@@@@@@@@@@" + searchdetailList.getSearchViewDetail().getPhoneNumbers().get(i).getInstance());
                ContactModel contact = new ContactModel();
                contact.setInstance(searchdetailList.getSearchViewDetail().getPhoneNumbers().get(i).getInstance());
                contact.setResource(searchdetailList.getSearchViewDetail().getPhoneNumbers().get(i).getResource());
                contact.setLabel(searchdetailList.getSearchViewDetail().getPhoneNumbers().get(i).getLabel());
                contactDetail.add(contact);
            }
        }
        if (searchdetailList.getSearchViewDetail().getEmails() != null && searchdetailList.getSearchViewDetail().getEmails().size() > 0) {
            for (int i = 0; i < searchdetailList.getSearchViewDetail().getEmails().size(); i++) {
                ContactModel contact = new ContactModel();
                contact.setInstance(searchdetailList.getSearchViewDetail().getEmails().get(i).getInstance());
                contact.setResource(searchdetailList.getSearchViewDetail().getEmails().get(i).getResource());
                contact.setLabel(searchdetailList.getSearchViewDetail().getEmails().get(i).getLabel());
                contactDetail.add(contact);
            }
        }

        if (searchdetailList.getSearchViewDetail().getPhoneNumbers() != null) {

            if (searchdetailList.getSearchViewDetail().getPhoneNumbers().size() > 0 || searchdetailList.getSearchViewDetail().getEmails().size() > 0 && contactDetail.size() > 0) {
                tv_contact.setVisibility(View.VISIBLE);
                tv_contact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isContact) {
                            Config.logV("Open");
                            isContact = true;
                            tv_contact.setText("Contact");
                            tv_contact.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.contact_selected, 0, 0);
                            BottomSheetContactDialog();
                        } else {
                            Config.logV("CLosed");
                        }
                    }
                });
            } else {
                tv_contact.setVisibility(View.GONE);
            }
        }

        if (searchdetailList.getSearchViewDetail().getVerifyLevel() != null) {
            if (!searchdetailList.getSearchViewDetail().getVerifyLevel().equalsIgnoreCase("NONE")) {
                ic_jaldeeverifiedIcon.setVisibility(View.VISIBLE);
                if (searchdetailList.getSearchViewDetail().getVerifyLevel().equalsIgnoreCase("BASIC_PLUS")) {
                    ic_jaldeeverifiedIcon.setImageResource(R.drawable.jaldee_basicplus);
                }
                if (searchdetailList.getSearchViewDetail().getVerifyLevel().equalsIgnoreCase("BASIC")) {
                    ic_jaldeeverifiedIcon.setImageResource(R.drawable.jaldee_basic);
                }
                if (searchdetailList.getSearchViewDetail().getVerifyLevel().equalsIgnoreCase("PREMIUM") || searchdetailList.getSearchViewDetail().getVerifyLevel().equalsIgnoreCase("ADVANCED")) {
                    ic_jaldeeverifiedIcon.setImageResource(R.drawable.jaldee_adv);
                }
            } else {
                ic_jaldeeverifiedIcon.setVisibility(View.GONE);
            }

            ic_jaldeeverifiedIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String ynw_verified = null;
                    if (searchdetailList.getSearchViewDetail().getVerifyLevel().equalsIgnoreCase("BASIC"))
                        ynw_verified = "2";
                    else if (searchdetailList.getSearchViewDetail().getVerifyLevel().equalsIgnoreCase("BASIC_PLUS"))
                        ynw_verified = "3";
                    else if (searchdetailList.getSearchViewDetail().getVerifyLevel().equalsIgnoreCase("PREMIUM") || searchdetailList.getSearchViewDetail().getVerifyLevel().equalsIgnoreCase("ADVANCED"))
                        ynw_verified = "4";
                    Config.logV("YNW VERIFIED@@@@@@@@@@@@" + ynw_verified);
                    CustomDialog cdd = new CustomDialog(mContext, ynw_verified, searchdetailList.getSearchViewDetail().getBusinessName());
                    cdd.setCanceledOnTouchOutside(true);
                    cdd.show();
                }
            });
        } else {
            ic_jaldeeverifiedIcon.setVisibility(View.GONE);
        }
        tv_msg.setEnabled(true);

        tv_busName.setText(searchdetailList.getSearchViewDetail().getBusinessName());

        try {

            int rate = Math.round(searchdetailList.getSearchViewDetail().getAvgRating());
            if (rate < 4) {
                rating.setVisibility(View.GONE);
            } else {
                rating.setVisibility(View.VISIBLE);
                rating.setRating(searchdetailList.getSearchViewDetail().getAvgRating());
            }
        } catch (Exception e) {

        }

        if (searchdetailList.getSearchViewDetail().getBusinessDesc() != null) {
            tv_desc.setVisibility(View.VISIBLE);
            tv_desc.setText(searchdetailList.getSearchViewDetail().getBusinessDesc());
            tv_desc.post(new Runnable() {
                @Override
                public void run() {
                    int lineCount = tv_desc.getLineCount();
                    //Config.logV("No of line---------------" + lineCount + "Name" + inboxList.getUserName());
                    if (lineCount > 3) {
                        ResizableCustomView.doResizeTextView(mContext, tv_desc, 3, "..more", true);
                    } else {
                    }
                    // Use lineCount here
                }
            });
        } else {
            tv_desc.setVisibility(View.GONE);
        }

    }


    private void apiSearchGallery(final String muniqueID) {
        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));
        Call<ArrayList<SearchViewDetail>> call = apiService.getSearchGallery(Integer.parseInt(muniqueID), sdf.format(currentTime));
        call.enqueue(new Callback<ArrayList<SearchViewDetail>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchViewDetail>> call, Response<ArrayList<SearchViewDetail>> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);
                    Config.logV("URL------100000---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-----gallery--------------------" + response.code());
                    if (response.code() == 200) {
                        mSearchGallery = response.body();
                        UpdateGallery(mSearchGallery);

                    } else {
                        tv_mImageViewTextnew.setVisibility(View.GONE);
                        if (mBusinessDataList.getLogo() != null) {
                            // Picasso.with(mContext).load(mBusinessDataList.getLogo().getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(mImgeProfile);
                            UpdateGallery(mSearchGallery);
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
                    Config.closeDialog(getActivity(), mDialog);
            }
        });
    }

    int count = 0;

    private void ApiSearchViewLocation(final String muniqueID) {
        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));
        Call<ArrayList<SearchLocation>> call = apiService.getSearchViewLoc(Integer.parseInt(muniqueID), sdf.format(currentTime));
        call.enqueue(new Callback<ArrayList<SearchLocation>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchLocation>> call, Response<ArrayList<SearchLocation>> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);
                    Config.logV("URL---3333------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code--Location-----------------------" + response.code());
                    mSearchLocList.clear();
                    if (response.code() == 200) {
                        mSearchLocList = response.body();
                        if (mSearchLocList != null) {
                            locationHeading.setVisibility(View.VISIBLE);
                            if (mSearchLocList.size() == 1) {
                                locationHeading.setText("Location (1)");
                                locationHeading.setVisibility(View.GONE);
                            } else if (mSearchLocList.size() > 1) {
                                locationHeading.setText("Location " + "(" + mSearchLocList.size() + ")");
                            }
                        } else {
                            locationHeading.setVisibility(View.GONE);
                        }
                        for (int i = 0; i < response.body().size(); i++) {
                            ids.add(String.valueOf(response.body().get(i).getId()));
                        }
                        for (int k = 0; k < mSearchLocList.size(); k++) {
                            Config.logV("Location-----###########" + mSearchLocList.get(k).getId());
                            ApiCheckInMessage(mSearchLocList.get(k).getId(), "notshow", "");
                        }
                        Config.logV("mSearchLocList " + mSearchLocList.size());
                        for (int k = 0; k < mSearchLocList.size(); k++) {
                            ApiSearchViewServiceID(mSearchLocList.get(k).getId());
                            ApiAppointmentServices(muniqueID);
                            ApiDonationServices(mProviderId);
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
                    Config.closeDialog(getActivity(), mDialog);
            }
        });
    }

    BottomSheetDialog bdialog;

    public void BottomSheetContactDialog() {
        bdialog = new BottomSheetDialog(mContext);
        bdialog.setContentView(R.layout.contact_list);
        bdialog.setCancelable(true);
        bdialog.show();
        RecyclerView contactlist = (RecyclerView) bdialog.findViewById(R.id.contactlist);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        TextView txtcontact = (TextView) bdialog.findViewById(R.id.txtcontact);
        txtcontact.setTypeface(tyface);
        Button btn_close = (Button) bdialog.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isContact = false;
                tv_contact.setText("Contact");
                tv_contact.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_contact, 0, 0);
                bdialog.dismiss();
            }
        });
        bdialog.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        //When you touch outside of dialog bounds,
                        //the dialog gets canceled and this method executes.
                        Config.logV("CANCEL @@@@@@@@@@@");
                        isContact = false;
                        tv_contact.setText("Contact");
                        tv_contact.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_contact, 0, 0);
                        bdialog.dismiss();
                    }
                }
        );
        contactlist.setLayoutManager(mLayoutManager);
        ContactDetailAdapter checkAdapter = new ContactDetailAdapter(contactDetail, mContext, getActivity(), mInterfaceContact);
        contactlist.setAdapter(checkAdapter);
        checkAdapter.notifyDataSetChanged();
    }

    BottomSheetDialog dialog;

    private void ApiCheckInMessage(final int mLocid, final String from, final String loc) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Map<String, String> query = new HashMap<>();
        query.put("location-eq", String.valueOf(mLocid));
        query.put("waitlistStatus-eq", "checkedIn,arrived");
        Call<ArrayList<SearchCheckInMessage>> call = apiService.getSearchCheckInMessage(query);
        Config.logV("Location-----###########@@@@@@" + query);
        call.enqueue(new Callback<ArrayList<SearchCheckInMessage>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchCheckInMessage>> call, Response<ArrayList<SearchCheckInMessage>> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);
                    Config.logV("URL-----4444-----Location-----###########@@@@@@-----" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code--------Message-----------------" + response.code());
                    if (response.code() == 200) {
                        mSearchmCheckListShow.clear();
                        if (response.body().size() > 0) {
                            SearchCheckInMessage mCheckMessage = new SearchCheckInMessage();
                            mCheckMessage.setmAllSearch_checkIn(response.body());
                            mCheckMessage.setLocid(mLocid);
                            mSearchmCheckMessageList.add(mCheckMessage);
                            if (from.equalsIgnoreCase("show")) {
                                dialog = new BottomSheetDialog(mContext);
                                dialog.setContentView(R.layout.checkin_loclist);
                                dialog.setCancelable(true);
                                dialog.show();
                                TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);
                                TextView tv_token = (TextView) dialog.findViewById(R.id.tv_token);
                                if (mSearchSettings.getCalculationMode().equalsIgnoreCase("NoCalc") && mSearchSettings.isShowTokenId()) {
                                    tv_token.setVisibility(View.VISIBLE);
                                } else {
                                    tv_token.setVisibility(View.GONE);
                                }
                                String firstWord = "Your " + terminology + " at ";
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
                                        refreshList();
                                    }
                                });
                                mSearchmCheckListShow = response.body();
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                                checkloclist.setLayoutManager(mLayoutManager);
                                LocationCheckinAdapter checkAdapter = new LocationCheckinAdapter(callback, String.valueOf(mProviderId), mSearchmCheckListShow, mContext, getActivity());
                                checkloclist.setAdapter(checkAdapter);
                                checkAdapter.notifyDataSetChanged();
                            }
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
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);
            }
        });
    }

    // Need to change
    private void ApiSearchViewServiceID(final int id) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ArrayList<SearchService>> call = apiService.getSearchService(id);
        call.enqueue(new Callback<ArrayList<SearchService>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchService>> call, Response<ArrayList<SearchService>> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);
                    Config.logV("URL---5555------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code----------Service---------------" + response.code());
                    if (response.code() == 200) {
                        SearchService mService = new SearchService();
                        mService.setmAllService(response.body());
                        mService.setLocid(id);
                        mServicesList.add(mService);
//                        ApiServicesGroupbyDepartment(mServicesList);
                        Config.logV("mServicesList @@@@" + response.body().size());
                        Config.logV("mServicesList" + mServicesList.size());
                        count++;
                        Config.logV("Count " + count);
                        if (count == mSearchLocList.size()) {
                            if (ids.size() > 0) {
                                ApiSearchScheduleViewID(mProviderId, ids);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<SearchService>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);
            }
        });
    }

    //
//    /**
//     * To show departments and its doctors/services
//     * @param uniqueID
//     * @param mProviders // Department Users List
//     */
//    private void apiDepartmentServices(final String uniqueID, final ArrayList<SearchDepartmentServices> mProviders) {
//        Log.i("apidepartment", "apidept1");
//        ApiInterface apiService =
//                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
//        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
//        mDialog.show();
//        Date currentTime = new Date();
//        final SimpleDateFormat sdf = new SimpleDateFormat(
//                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//        System.out.println("UTC time: " + sdf.format(currentTime));
//        Call<ArrayList<SearchDepartmentServices>> call = apiService.getDepartmentServices(Integer.parseInt(uniqueID), sdf.format(currentTime));
//        call.enqueue(new Callback<ArrayList<SearchDepartmentServices>>() {
//            @Override
//            public void onResponse(Call<ArrayList<SearchDepartmentServices>> call, Response<ArrayList<SearchDepartmentServices>> response) {
//                try {
//                    if (mDialog.isShowing())
//                        Config.closeDialog(getActivity(), mDialog);
//                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
//                    Config.logV("Response--code-------------------------" + response.code());
//                    if (response.code() == 200) {
//                        mSearchDepartmentServices.clear();
//                        String responses = new Gson().toJson(response.body());
//                        Config.logV("Deapartnamesss---------------" + responses);
//                        if (mProviders.size() > 0) {
//                            for (int i = 0; i < response.body().size(); i++) {
//                                departmentNameList.add(response.body().get(i).getDepartmentName());
//                                departmentCodeList.add(response.body().get(i).getDepartmentCode());
//                            }
//                            mSearchDepartmentServices.addAll(response.body());
//                            if (mSearchDepartmentServices != null) {
//                                if (mSearchDepartmentServices.size() == 1) {
//                                    departmentHeading.setVisibility(View.VISIBLE);
//                                    departmentHeading.setText("Department (1)");
//                                } else if (mSearchDepartmentServices.size() > 1) {
//                                    departmentHeading.setVisibility(View.VISIBLE);
//                                    departmentHeading.setText("Departments " + "(" + mSearchDepartmentServices.size() + ")");
//                                }
//                            } else {
//                                departmentHeading.setVisibility(View.GONE);
//                            }
//                            Log.i("departmentservice", new Gson().toJson(mSearchDepartmentServices));
//                            Config.logV("DepartmEntqweCode --------------" + departmentCodeList);
//                            Config.logV("DepartmEntqweName --------------" + departmentNameList);
//                            RecyclerView.LayoutManager mDepartmentLayout = new LinearLayoutManager(mContext);
//                            mRecycleDepartment.setLayoutManager(mDepartmentLayout);
//                            mDepartmentAdapter.setFields(mSearchDepartmentServices, departmentMap, mBusinessDataList.getBusinessName(), mServicesList.get(0).getmAllService(), department);
//                            mRecycleDepartment.setAdapter(mDepartmentAdapter);
//                            mDepartmentAdapter.notifyDataSetChanged();
//                        } else {
//
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ArrayList<SearchDepartmentServices>> call, Throwable t) {
//                // Log error here since request failed
//                Config.logV("Fail---------------" + t.toString());
//                if (mDialog.isShowing())
//                    Config.closeDialog(getActivity(), mDialog);
//            }
//        });
//    }
//    private void APIServiceDepartments(final int id) {
//        ApiInterface apiService =
//                ApiClient.getClient(mContext).create(ApiInterface.class);
//        Call<SearchDepartment> call = apiService.getDepartment(id);
//        call.enqueue(new Callback<SearchDepartment>() {
//            @Override
//            public void onResponse(Call<SearchDepartment> call, Response<SearchDepartment> response) {
//                try {
//                    Config.logV("URL---5555------------" + response.raw().request().url().toString().trim());
//                    Config.logV("Response--code----------Service---------------" + response.code());
//                    if (response.code() == 200) {
//                        String responses = new Gson().toJson(response.body());
//                        Config.logV("Deapartnamesss---------------" + responses);
//                        mSearchDepartments.addAll(response.body().getDepartments());
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            @Override
//            public void onFailure(Call<SearchDepartment> call, Throwable t) {
//                Config.logV("Fail---------------" + t.toString());
//            }
//        });
//    }
//    private void ApiDepartment(final int id) {
//        ApiInterface apiService =
//                ApiClient.getClient(mContext).create(ApiInterface.class);
//        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
//        mDialog.show();
//        Call<SearchDepartment> call = apiService.getDepartment(id);
//        call.enqueue(new Callback<SearchDepartment>() {
//            @Override
//            public void onResponse(Call<SearchDepartment> call, Response<SearchDepartment> response) {
//                try {
//                    if (mDialog.isShowing())
//                        Config.closeDialog(getActivity(), mDialog);
//                    Config.logV("URL---5555------------" + response.raw().request().url().toString().trim());
//                    Config.logV("Response--code----------Service---------------" + response.code());
//                    if (response.code() == 200) {
//                        mSearchDepartments.clear();
//                        String responses = new Gson().toJson(response.body());
//                        Config.logV("Deapartnamesss---------------" + responses);
//                        for (int i = 0; i < response.body().getDepartments().size(); i++) {
//                            departmentNameList.add(response.body().getDepartments().get(i).getDepartmentName());
//                            departmentCodeList.add(response.body().getDepartments().get(i).getDepartmentCode());
//                        }
//                        mSearchDepartments.addAll(response.body().getDepartments());
//                        Config.logV("DepartmEntqweCode --------------" + departmentCodeList);
//                        Config.logV("DepartmEntqweName --------------" + departmentNameList);
//                        RecyclerView.LayoutManager mDepartmentLayout = new LinearLayoutManager(mContext);
//                        mRecycleDepartment.setLayoutManager(mDepartmentLayout);
//                        mDepartmentAdapter.setFields(mSearchDepartmentServices, departmentMap, mBusinessDataList.getBusinessName(), mServicesList.get(0).getmAllService(), department);
//                        mRecycleDepartment.setAdapter(mDepartmentAdapter);
//                        mDepartmentAdapter.notifyDataSetChanged();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<SearchDepartment> call, Throwable t) {
//                Config.logV("Fail---------------" + t.toString());
//                if (mDialog.isShowing())
//                    Config.closeDialog(getActivity(), mDialog);
//            }
//        });
//    }
    private void ApiSearchViewID(int mProviderid, ArrayList<String> ids) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        String idPass = "";
        for (int i = 0; i < ids.size(); i++) {
            idPass += mProviderid + "-" + ids.get(i) + ",";
        }
        if (!idPass.equals("") && idPass != null) {
            Config.logV("IDS_--------------------" + idPass);
            Call<ArrayList<QueueList>> call = apiService.getSearchID(idPass);
            call.enqueue(new Callback<ArrayList<QueueList>>() {
                @Override
                public void onResponse(Call<ArrayList<QueueList>> call, Response<ArrayList<QueueList>> response) {
                    try {
                        if (mDialog.isShowing())
                            Config.closeDialog(getActivity(), mDialog);
                        Config.logV("URL---66666----SEARCH--------" + response.raw().request().url().toString().trim());
                        Config.logV("Response--code-----SearchViewID--------------------" + response.code());
                        Config.logV("Response--code-----SearchViewID12--------------------" + new Gson().toJson(response.body()));
                        if (response.code() == 200) {
                            mSearchQueueList = response.body();
//                            if(homeUniqueId==null){
//                                ApiSearchViewSetting(uniqueID);}
//                            else {
//                                ApiSearchViewSetting(homeUniqueId);
//                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                        mRecyLocDetail.setLayoutManager(mLayoutManager);
                        if (homeUniqueId == null) {
                            uniID = uniqueID;
                        } else {
                            uniID = homeUniqueId;
                        }

                        if (from_user) {

                            try {

                                if (searchdetailList != null) {
                                    ArrayList<DepartmentUserSearchModel> userDetails = new ArrayList<DepartmentUserSearchModel>();
                                    userDetails.add(searchdetailList);
                                    userDetailAdapter = new UserDetailAdapter(mContext, userDetails, mSearchAWSResponse, mSearchDepartmentList, terminology, mInterface, mSearchmCheckMessageList);
                                    mRecyLocDetail.setAdapter(userDetailAdapter);
                                    userDetailAdapter.notifyDataSetChanged();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {

                            try {

                                if (mBusinessDataList != null && mSearchSettings != null) {
                                    if (mBusinessDataList.getServiceSector() != null && mBusinessDataList.getServiceSubSector() != null) {
                                        mSearchLocAdapter = new SearchLocationAdapter(mBusinessDataList.getServiceSector().getDomain(), mBusinessDataList.getServiceSubSector().getSubDomain(), String.valueOf(mProviderId), uniID, mInterface, mBusinessDataList.getBusinessName(), mSearchSettings, mSearchLocList, mContext, mServicesList, mSearchQueueList, mSearchmCheckMessageList, mSearchSettings.getCalculationMode(), terminology, mSearchSettings.isShowTokenId(), mSearchDepartmentList, mSearchRespDetail, mSearchAWSResponse, mSearchScheduleList, online_presence, donationFundRaising, gServiceList, LaServicesList, virtualServices);
                                        mRecyLocDetail.setAdapter(mSearchLocAdapter);
                                        mSearchLocAdapter.notifyDataSetChanged();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    }
                }

                @Override
                public void onFailure(Call<ArrayList<QueueList>> call, Throwable t) {
                    // Log error here since request failed
                    Config.logV("Fail---------------" + t.toString());
                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);
                }
            });
        }
    }

    private void ApiSearchScheduleViewID(int mProviderid, final ArrayList<String> ids) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        String idPass = "";
        for (int i = 0; i < ids.size(); i++) {
            idPass += mProviderid + "-" + ids.get(i) + ",";
        }
        if (!idPass.equals("") && idPass != null) {
            Config.logV("IDS_--------------------" + idPass);
            Call<ArrayList<ScheduleList>> call = apiService.getSchedule(idPass);
            call.enqueue(new Callback<ArrayList<ScheduleList>>() {
                @Override
                public void onResponse(Call<ArrayList<ScheduleList>> call, Response<ArrayList<ScheduleList>> response) {
                    try {
                        if (mDialog.isShowing())
                            Config.closeDialog(getActivity(), mDialog);
                        Config.logV("URL---66666----SEARCH--------" + response.raw().request().url().toString().trim());
                        Config.logV("Response--code-----SearchViewID--------------------" + response.code());
                        Config.logV("Response--code-----SearchViewID12--------------------" + new Gson().toJson(response.body()));
                        if (response.code() == 200) {
                            mSearchScheduleList = response.body();
                            ApiSearchViewID(mProviderId, ids);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<ScheduleList>> call, Throwable t) {
                    // Log error here since request failed
                    Config.logV("Fail---------------" + t.toString());
                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);
                }
            });
        }
    }
//    private void apiFetchIdFromDeepLink(String customID) {
//        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
//        Call<ResponseBody> call = apiService.getUniqueID(customID);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                try {
//                    if (response.code() == 200) {
//                        homeUniqueId = response.body().string();
//                        Log.i("sadf",homeUniqueId);
//                        ApiJaldeeCoupan(homeUniqueId);
//                        ApiJDN(homeUniqueId);
//                        apiSearchViewTerminology(homeUniqueId);
//                        apiSearchViewDetail(homeUniqueId, mSearchResp);
////                        ApiSearchGallery(homeUniqueId);
//                        ApiSearchVirtualFields(homeUniqueId);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//            }
//        });
//    }

    VirtualFieldAdapter mAdapter;
    SpecialisationAdapter sAdapter;
    SearchVirtualFields resultData;
    ArrayList<SearchVirtualFields> domainVirtual = new ArrayList<>();
    ArrayList<SearchVirtualFields> sub_domainVirtual = new ArrayList<>();
    ArrayList<SearchVirtualFields> mergeResult = new ArrayList<>();

    private void apiVirtualFields(String muniqueID) {
        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));
        Call<SearchVirtualFields> call = apiService.getVirtualFields(Integer.parseInt(muniqueID), sdf.format(currentTime));
        call.enqueue(new Callback<SearchVirtualFields>() {
            @Override
            public void onResponse(Call<SearchVirtualFields> call, Response<SearchVirtualFields> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);
                    Config.logV("URL----VIRTUAL---8888--------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------VIRTUAL-------------------" + response.code());
                    if (response.code() == 200) {
                        resultData = response.body();
                        if (resultData != null) {
                            domainVirtual.clear();
                            domainVirtual = response.body().getDomain();
                            sub_domainVirtual.clear();
                            sub_domainVirtual = response.body().getSubdomain();
                            domainVirtual.addAll(sub_domainVirtual);

                            if (domainVirtual.size() > 0) {
                                llMDetails.setVisibility(View.VISIBLE);
                                txtMore.setVisibility(View.VISIBLE);
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
                                tv_Moredetails.setVisibility(View.GONE);
                                llMDetails.setVisibility(View.GONE);
                            }
                        } else {
                            tv_Moredetails.setVisibility(View.GONE);
                            mRecycle_virtualfield.setVisibility(View.GONE);
                            txtMore.setVisibility(View.GONE);
                        }
                    } else {
                        txtMore.setVisibility(View.GONE);
                        llMDetails.setVisibility(View.GONE);
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
                    Config.closeDialog(getActivity(), mDialog);
            }
        });
    }

    private void apiSearchViewTerminology(String muniqueID) {
        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));
        Call<SearchTerminology> call = apiService.getSearchViewTerminology(Integer.parseInt(muniqueID), sdf.format(currentTime));
        call.enqueue(new Callback<SearchTerminology>() {
            @Override
            public void onResponse(Call<SearchTerminology> call, Response<SearchTerminology> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);
                    Config.logV("URL-------9999--------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-----Terminl--------------------" + response.code());
                    if (response.code() == 200) {
                        mSearchTerminology = response.body();
                        terminology = mSearchTerminology.getWaitlist();
                        userTerminology = mSearchTerminology.getProvider();

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
                    Config.closeDialog(getActivity(), mDialog);
            }
        });
    }

    public void onMethodNewSearch(String uniqueID) {
        NewSearchFragment nsfFragment = new NewSearchFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("uniqueID", uniqueID);
        nsfFragment.setArguments(bundle);
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.mainlayout, nsfFragment).commit();
    }

    public void onMethodCoupn(String uniqueID) {
        CouponFragment cfFragment = new CouponFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("uniqueID", uniqueID);
        cfFragment.setArguments(bundle);
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.mainlayout, cfFragment).commit();
    }

    public void onMethodJdn(String uniqueid) {
        JdnFragment jdnFragment = new JdnFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("uniqueID", uniqueid);
        jdnFragment.setArguments(bundle);
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.mainlayout, jdnFragment).commit();
    }

    public void onMethodFirstCoupn(String uniqueid) {
        CouponFirstFragment cffFragment = new CouponFirstFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("uniqueID", uniqueid);
        cffFragment.setArguments(bundle);
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.mainlayout, cffFragment).commit();
    }

    public void onMethodDepartment(SearchDepartmentServices departmentCode, String businessName, String providerTerminology) {
        Log.i("qweqweq", "qweqweqwe");

        DeptFragment deptFragment = new DeptFragment(departmentCode, this, businessName, mBusinessDataList, firstCouponAvailable, couponAvailable, mSearchLocList, mSearchSettings, providerTerminology);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.mainlayout, deptFragment).commit();
    }

    private void onMethodUsersClick(ArrayList<ProviderUserModel> usersList, String businessName, String providerTerminology) {

        boolean fromDoctors = true;
        DeptFragment deptFragment = new DeptFragment(usersList, this, businessName, mBusinessDataList, firstCouponAvailable, couponAvailable, mSearchLocList, mSearchSettings, fromDoctors,providerTerminology);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.mainlayout, deptFragment).commit();

    }


    public void onMethodJaldeeLogo(String ynw_verified, String providername) {
        CustomDialog cdd = new CustomDialog(mContext, ynw_verified, providername);
        cdd.setCanceledOnTouchOutside(true);
        cdd.show();
    }

    public void onMethodOpenMap(String location) {
        String geoUri = "http://maps.google.com/maps?q=loc:" + location;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
        mContext.startActivity(intent);
    }


    public void onMethodMessage(String provider, final String accountID, String from) {

        final BottomSheetDialog dialog = new BottomSheetDialog(mContext, R.style.DialogStyle);
        dialog.setContentView(R.layout.reply);
        dialog.show();

        final Button btn_send = (Button) dialog.findViewById(R.id.btn_send);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
        TextView txtsendmsg = (TextView) dialog.findViewById(R.id.txtsendmsg);
        txtsendmsg.setVisibility(View.VISIBLE);
        txtsendmsg.setText("Message to " + provider);
        btn_send.setText("SEND");
        edt_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                if (edt_message.getText().toString().length() >= 1 && !edt_message.getText().toString().trim().isEmpty()) {
                    btn_send.setEnabled(true);
                    btn_send.setClickable(true);
                    btn_send.setBackground(mContext.getResources().getDrawable(R.color.blue));
                } else {
                    btn_send.setEnabled(false);
                    btn_send.setClickable(false);
                    btn_send.setBackground(mContext.getResources().getDrawable(R.color.button_grey));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String modifyAccountID;
                if (from.equalsIgnoreCase("dept")) {
                    modifyAccountID = accountID;
                } else {
                    modifyAccountID = accountID.substring(0, accountID.indexOf("-"));
                }
                ApiCommunicate(modifyAccountID, edt_message.getText().toString(), dialog);
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onMethodWorkingCallback(ArrayList<WorkingModel> workingModel, String value) {
        WorkingHourFragment pfFragment = new WorkingHourFragment();
        // Config.logV("Fragment context-----------" + mFragment);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("workinghrlist", workingModel);
        bundle.putString("title", value);
        bundle.putString("uniqueID", uniqueID);
        pfFragment.setArguments(bundle);
        // Store the Fragment in stack
        transaction.addToBackStack(null);
        transaction.add(R.id.mainlayout, pfFragment).commit();
    }

    public void onMethodSpecialization(ArrayList Specialization_displayname, String businessName) {
        SpecializationFragment specialFragment = new SpecializationFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("businessName", businessName);
        bundle.putStringArrayList("Specialization_displayname", Specialization_displayname);
        specialFragment.setArguments(bundle);

        // Store the Fragment in stack
        transaction.addToBackStack(null);
        transaction.replace(R.id.mainlayout, specialFragment).commit();
    }

    @Override
    public void onMethodServiceCallback(ArrayList<SearchService> searchService, String value) {
        ServiceListFragment pfFragment = new ServiceListFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("servicelist", searchService);
        bundle.putString("title", value);
        bundle.putString("from", "searchdetail");
        bundle.putString("uniqueID", uniqueID);
        pfFragment.setArguments(bundle);
        // Store the Fragment in stack
        transaction.addToBackStack(null);
        transaction.add(R.id.mainlayout, pfFragment).commit();
    }

    @Override
    public void onMethodServiceCallback(ArrayList<SearchService> searchService, String value, ArrayList<SearchDepartmentServices> mSearchDepartment) {
        ServiceListFragment pfFragment = new ServiceListFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("servicelist", searchService);
        bundle.putSerializable("departmentlist", mSearchDepartment);
        bundle.putString("title", value);
        bundle.putString("from", "searchdetail");
        bundle.putString("uniqueID", uniqueID);
        pfFragment.setArguments(bundle);
        // Store the Fragment in stack
        transaction.addToBackStack(null);
        transaction.add(R.id.mainlayout, pfFragment).commit();
    }

    @Override
    public void onMethodServiceCallbackAppointment(ArrayList<SearchAppointmentDepartmentServices> searchService, String value, ArrayList<SearchDepartmentServices> mSearchDepartment) {
        ServiceListAppointmentFragment pfFragment = new ServiceListAppointmentFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("servicelist", searchService);
        bundle.putSerializable("departmentlist", mSearchDepartment);
        bundle.putString("title", value);
        bundle.putString("from", "searchdetail");
        bundle.putString("uniqueID", uniqueID);
        pfFragment.setArguments(bundle);
        // Store the Fragment in stack
        transaction.addToBackStack(null);
        transaction.add(R.id.mainlayout, pfFragment).commit();
    }

    @Override
    public void onMethodServiceCallbackDonation(ArrayList<SearchDonation> searchService, String value) {
        ServiceListDonationFragment pfFragment = new ServiceListDonationFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("servicelist", searchService);
        bundle.putString("title", value);
        bundle.putString("from", "searchdetail");
        bundle.putString("uniqueID", uniqueID);
        pfFragment.setArguments(bundle);
        // Store the Fragment in stack
        transaction.addToBackStack(null);
        transaction.add(R.id.mainlayout, pfFragment).commit();
    }


    @Override
    public void onMethodCheckinCallback(int locID, String from, String location) {
        ApiCheckInMessage(locID, from, location);
    }

    @Override
    public void onUserAppointmentServices(List<SearchService> apptServices, String businessName, ArrayList<SearchDepartmentServices> mSearchDepartmentList) {

        ServiceListAppointmentFragment pfFragment = new ServiceListAppointmentFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("apptList", (Serializable) apptServices);
        bundle.putString("title", businessName);
        bundle.putString("from", "individualUsers");
        bundle.putString("uniqueID", uniqueID);
        pfFragment.setArguments(bundle);
        // Store the Fragment in stack
        transaction.addToBackStack(null);
        transaction.add(R.id.mainlayout, pfFragment).commit();
    }

    @Override
    public void onMethodCallback() {
        Toast.makeText(mContext, terminology + " cancelled successfully", Toast.LENGTH_LONG).show();
        dialog.dismiss();
        refreshList();
    }

    public void onMethodCallback(DepartmentUserSearchModel searchdetailList, boolean fromUser, String uniqueID) {
        Bundle bundle = new Bundle();
        SearchDetailViewFragment pfFragment = new SearchDetailViewFragment();
        bundle.putSerializable("userdetail", searchdetailList);
        bundle.putBoolean("user", fromUser);
        bundle.putString("uniqueID", uniqueID);
        pfFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        // Store the Fragment in stack
        transaction.addToBackStack(null);
        transaction.add(R.id.mainlayout, pfFragment).commit();

    }

    public void refreshList() {
        Config.logV("Refresh $$$$$@@@@@@@@@@@@@@@@@@");
        count = 0;
        isContact = false;
        SharedPreference.getInstance(mContext).setValue("refreshcheckin", "false");
        mBusinessDataList = new SearchViewDetail();
        mSearchGallery = new ArrayList<>();
//        mSearchLocList = new ArrayList<>();
//        mSearchSettings = new SearchSetting();
        mSearchTerminology = new SearchTerminology();
        mSearchQueueList = new ArrayList<>();
        mServicesList = new ArrayList<>();
        mServicesList.clear();
        mSearchmCheckMessageList = new ArrayList<>();
        ids = new ArrayList<>();
        contactDetail = new ArrayList<>();
        if (homeUniqueId == null) {
            apiSearchViewDetail(uniqueID, mSearchResp);
        } else {
            apiSearchViewDetail(homeUniqueId, mSearchResp);
        }
        //ApiSearchGallery(uniqueID);
        if (homeUniqueId == null) {
            apiSearchViewTerminology(uniqueID);
        } else {
            apiSearchViewTerminology(homeUniqueId);
        }
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
                        Config.closeDialog(getActivity(), mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        if (response.body().string().equalsIgnoreCase("true")) {
                            Toast.makeText(mContext, "Added to Favourites", Toast.LENGTH_LONG).show();
                            ApiFavList(mSearchResp, claimable);
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
                    Config.closeDialog(getActivity(), mDialog);
            }
        });
    }

    boolean favFlag = false;
    ArrayList<FavouriteModel> mFavList = new ArrayList<>();

    private void ApiFavList(final List<SearchAWsResponse> mSearchRespPass, final String claimable) {
        Config.logV("API Call");
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<ArrayList<FavouriteModel>> call = apiService.getFavourites();
        call.enqueue(new Callback<ArrayList<FavouriteModel>>() {
            @Override
            public void onResponse(Call<ArrayList<FavouriteModel>> call, Response<ArrayList<FavouriteModel>> response) {
                try {
                    tv_fav.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_favourite_line, 0, 0);
                    tv_fav.setText("Favourite");
                    Config.logV("URL-----22222----------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        mFavList.clear();
                        mFavList = response.body();
                        favFlag = false;
                        for (int i = 0; i < mFavList.size(); i++) {
                            Config.logV("Fav List-----##&&&-----" + mFavList.get(i).getId());
                            Config.logV("Fav Fav List--------%%%%--" + mBusinessDataList.getId());
                            if (mFavList.get(i).getId() == mBusinessDataList.getId()) {
                                favFlag = true;
                                tv_fav.setVisibility(View.VISIBLE);
                                tv_fav.setText("Favourite");
                                tv_fav.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_favourited, 0, 0);
                            }
                        }
                        tv_fav.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (favFlag) {
                                    ApiRemoveFavo(mBusinessDataList.getId());
                                    /*AlertDialog myQuittingDialogBox = new AlertDialog.Builder(mContext)
                                            //set message, title, and icon
                                            .setTitle("Delete")
                                            .setMessage("Do you want to remove " + mBusinessDataList.getBusinessName() + " from favourite list?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    //your deleting code
                                                    dialog.dismiss();
                                                    ApiRemoveFavo(mBusinessDataList.getId());
                                                }
                                            })
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .create();
                                    myQuittingDialogBox.show();*/
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
                        Config.closeDialog(getActivity(), mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        if (response.body().string().equalsIgnoreCase("true")) {
                            Toast.makeText(mContext, "Removed from favourites", Toast.LENGTH_LONG).show();
                            ApiFavList(mSearchResp, claimable);
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
                    Config.closeDialog(getActivity(), mDialog);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        String refresh = SharedPreference.getInstance(mContext).getStringValue("refreshcheckin", "");
        if (refresh.equalsIgnoreCase("true")) {
            refreshList();
        }
    }

    @Override
    public void onMethodContactCallback(String type, String value) {
        if (type.equalsIgnoreCase("Phoneno")) {
            callPhoneNumber(value);
        }
        if (type.equalsIgnoreCase("Email")) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", value, null));
            // emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Jaldee Feedback");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "");
            try {
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(mContext, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private final int CALL_REQUEST = 100;
    String phoneNumber;

    public void callPhoneNumber(String phNo) {
        try {
            phoneNumber = phNo;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST);
                    requestPermissions(new String[]{
                            Manifest.permission.CALL_PHONE}, CALL_REQUEST);
                    return;
                } else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phNo));
                    startActivity(callIntent);
                }
            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phNo));
                startActivity(callIntent);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //    private void listDoctorsByDepartment() {
//        final ApiInterface apiService =
//                ApiClient.getClientAWS(mContext).create(ApiInterface.class);
//        Map<String, String> query = new HashMap<>();
//        query.put("start", "0");
//        query.put("q", "(and " + "account_type:" + 1 + " branch_id:" + mbranchId + ")");
//        String mobile = SharedPreference.getInstance(mContext).getStringValue("mobile", "");
//        if (mobile.startsWith("55")) {
//            query.put("fq", "(and  test_account:1 )");
//        } else {
//            query.put("fq", "(and  (not test_account:1) )");
//        }
//        Map<String, String> params = new HashMap<>();
//        params.put("size", "10000");
//        params.put("q.parser", "structured");
//        params.put("sort", "claimable asc,ynw_verified_level desc, distance asc");
//        params.put("expr.distance", "haversin(" + lat_long + ", location1.latitude, location1.longitude)");
//        params.put("return", "_all_fields,distance");
//        Call<SearchAWsResponse> call = apiService.getSearchAWS(query, params);
//        call.enqueue(new Callback<SearchAWsResponse>() {
//            @Override
//            public void onResponse(Call<SearchAWsResponse> call, Response<SearchAWsResponse> response) {
//                try {
//                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
//                    Config.logV("Response--code-------------------------" + response.code());
//                    if (response.code() == 200) {
//                        Config.logV("Response--Body AWSmbbnm-------------------------" + new Gson().toJson(response.body()));
//                        Config.logV("Response--Body AWS-------------------------" + new Gson().toJson(response.body()));
//                        Config.logV("Status" + response.body().getStatus().getRid());
//                        Config.logV("Found" + response.body().getHits().getFound());
//                        TOTAL_PAGES = response.body().getHits().getFound() / 10;
//                        if (response.body().getHits().getFound() > 0) {
//                            mSearchResp.clear();
//                            ArrayList<String> ids = new ArrayList<>();
//                            for (int i = 0; i < response.body().getHits().getHit().size(); i++) {
//                                SearchAWsResponse search = new SearchAWsResponse();
//                                search.setId(response.body().getHits().getHit().get(i).getId());
//                                search.setLogo(response.body().getHits().getHit().get(i).getFields().getLogo());
//                                search.setSub_sector_displayname(response.body().getHits().getHit().get(i).getFields().getSub_sector_displayname());
//                                search.setTitle(response.body().getHits().getHit().get(i).getFields().getTitle());
//                                search.setRating(response.body().getHits().getHit().get(i).getFields().getRating());
//                                search.setPlace1(response.body().getHits().getHit().get(i).getFields().getPlace1());
//                                search.setUnique_id(response.body().getHits().getHit().get(i).getFields().getUnique_id());
//                                search.setClaimable(response.body().getHits().getHit().get(i).getFields().getClaimable());
//                                search.setCoupon_enabled(response.body().getHits().getHit().get(i).getFields().getCoupon_enabled());
//                                search.setOnline_profile(response.body().getHits().getHit().get(i).getFields().getOnline_profile());
//                                search.setDonation_status(response.body().getHits().getHit().get(i).getFields().getDonation_status());
//                                search.setAccountType(response.body().getHits().getHit().get(i).getFields().getAccountType());
//                                search.setBranch_name(response.body().getHits().getHit().get(i).getFields().getBranch_name());
//                                search.setToday_appt(response.body().getHits().getHit().get(i).getFields().getToday_appt());
//                                search.setFuture_appt(response.body().getHits().getHit().get(i).getFields().getFuture_appt());
//                                search.setLocation1(response.body().getHits().getHit().get(i).getFields().getLocation1());
//                                search.setLocation_id1(response.body().getHits().getHit().get(i).getFields().getLocation_id1());
//                                search.setSector(response.body().getHits().getHit().get(i).getFields().getSector());
//                                search.setSub_sector(response.body().getHits().getHit().get(i).getFields().getSub_sector());
//                                if (response.body().getHits().getHit().get(i).getFields().getYnw_verified() != null) {
//                                    search.setYnw_verified(response.body().getHits().getHit().get(i).getFields().getYnw_verified());
//                                }
//                                if (response.body().getHits().getHit().get(i).getFields().getYnw_verified_level() != null) {
//                                    search.setYnw_verified_level(response.body().getHits().getHit().get(i).getFields().getYnw_verified_level());
//                                }
//                                if (response.body().getHits().getHit().get(i).getFields().getQualification() != null) {
//                                    search.setQualification(response.body().getHits().getHit().get(i).getFields().getQualification());
//                                }
//                                if (response.body().getHits().getHit().get(i).getExprs() != null) {
//                                    search.setDistance(response.body().getHits().getHit().get(i).getExprs().getDistance());
//                                }
//                                if (response.body().getHits().getHit().get(i).getFields().getSpecialization_displayname() != null) {
//                                    search.setSpecialization_displayname(response.body().getHits().getHit().get(i).getFields().getSpecialization_displayname());
//                                }
//                                if (response.body().getHits().getHit().get(i).getFields().getShow_waiting_time() != null) {
//                                    search.setShow_waiting_time(response.body().getHits().getHit().get(i).getFields().getShow_waiting_time());
//                                }
//                                // Config.logV("response.body().getHits().getHit().get(i).getFields().toString()"+response.body().getHits().getHit().get(i).getFields().toString());
//                                //search.setFound(response.body().getHits().getFound());
//                                if (response.body().getHits().getHit().get(i).getFields().getServices() != null) {
//                                    search.setServices(response.body().getHits().getHit().get(i).getFields().getServices());
//                                }
//                                if (response.body().getHits().getHit().get(i).getFields().getAppt_services() != null) {
//                                    search.setAppt_services(response.body().getHits().getHit().get(i).getFields().getAppt_services());
//                                }
//                                if (response.body().getHits().getHit().get(i).getFields().getDonation_services() != null) {
//                                    search.setDonation_services(response.body().getHits().getHit().get(i).getFields().getDonation_services());
//                                }
//                                if (response.body().getHits().getHit().get(i).getFields().getBusiness_hours1() != null) {
//                                    search.setBusiness_hours1(response.body().getHits().getHit().get(i).getFields().getBusiness_hours1());
//                                }
//                                if (response.body().getHits().getHit().get(i).getFields().getTerminologies() != null) {
//                                    search.setTerminologies(response.body().getHits().getHit().get(i).getFields().getTerminologies());
//                                }
//                                if (response.body().getHits().getHit().get(i).getFields().getOnline_checkins() != null) {
//                                    search.setOnline_checkins(response.body().getHits().getHit().get(i).getFields().getOnline_checkins());
//                                }
//                                if (response.body().getHits().getHit().get(i).getFields().getFuture_checkins() != null) {
//                                    search.setFuture_checkins(response.body().getHits().getHit().get(i).getFields().getFuture_checkins());
//                                }
//                                if (response.body().getHits().getHit().get(i).getFields().getGallery_thumb_nails() != null) {
//                                    search.setGallery_thumb_nails(response.body().getHits().getHit().get(i).getFields().getGallery_thumb_nails());
//                                }
//                                //7 types
//                                if (response.body().getHits().getHit().get(i).getFields().getParking_type_location1() != null) {
//                                    Config.logV("PArking----111---------" + response.body().getHits().getHit().get(i).getFields().getParking_type_location1());
//                                    search.setParking_type_location1(response.body().getHits().getHit().get(i).getFields().getParking_type_location1());
//                                }
//                                if (response.body().getHits().getHit().get(i).getFields().getParking_location1() != null) {
//                                    Config.logV("Park-@@@@-------------------" + response.body().getHits().getHit().get(i).getFields().getParking_location1());
//                                    search.setParking_location1(response.body().getHits().getHit().get(i).getFields().getParking_location1());
//                                }
//                                if (response.body().getHits().getHit().get(i).getFields().getAlways_open_location1() != null) {
//                                    search.setAlways_open_location1(response.body().getHits().getHit().get(i).getFields().getAlways_open_location1());
//                                }
//                                if (response.body().getHits().getHit().get(i).getFields().getTraumacentre_location1() != null) {
//                                    search.setTraumacentre_location1(response.body().getHits().getHit().get(i).getFields().getTraumacentre_location1());
//                                }
//                                if (response.body().getHits().getHit().get(i).getFields().getDentistemergencyservices_location1() != null) {
//                                    search.setDentistemergencyservices_location1(response.body().getHits().getHit().get(i).getFields().getDentistemergencyservices_location1());
//                                }
//                                if (response.body().getHits().getHit().get(i).getFields().getDocambulance_location1() != null) {
//                                    search.setDocambulance_location1(response.body().getHits().getHit().get(i).getFields().getDocambulance_location1());
//                                }
//                                if (response.body().getHits().getHit().get(i).getFields().getPhysiciansemergencyservices_location1() != null) {
//                                    search.setPhysiciansemergencyservices_location1(response.body().getHits().getHit().get(i).getFields().getPhysiciansemergencyservices_location1());
//                                }
//                                if(response.body().getHits().getHit().get(i).getFields().getHosemergencyservices_location1() != null){
//                                    search.setHosemergencyservices_location1(response.body().getHits().getHit().get(i).getHosemergencyservices_location1());
//                                }
//                                if (response.body().getHits().getHit().get(i).getFields().getFirstaid_location1() != null) {
//                                    search.setFirstaid_location1(response.body().getHits().getHit().get(i).getFields().getFirstaid_location1());
//                                }
//                                if (response.body().getHits().getHit().get(i).getFields().getDepartment_code() != null) {
//                                    search.setDepartment_code(response.body().getHits().getHit().get(i).getFields().getDepartment_code());
//                                }
//                                if (response.body().getHits().getHit().get(i).getFields().getClaimable().equals("1")) {
//                                    tv_fav.setVisibility(View.GONE);
//                                }
//                                mSearchResp.add(search);
//                                Config.logV("mSearchResp" + new Gson().toJson(mSearchResp));
//                                ids.add(response.body().getHits().getHit().get(i).getId());
//                            }
//                            ApiQueueList(ids, mSearchResp, "next");
//                            if (mSearchResp.get(0).getClaimable().equals("1")) {
//                                tv_fav.setVisibility(View.GONE);
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            private void ApiQueueList(ArrayList<String> queuelist, final List<SearchAWsResponse> mSearchRespPass, final String mCheck) {
//                ApiInterface apiService =
//                        ApiClient.getClient(mContext).create(ApiInterface.class);
//                StringBuilder csvBuilder = new StringBuilder();
//                for (String data : queuelist) {
//                    csvBuilder.append(data);
//                    csvBuilder.append(",");
//                }
//                String csv = csvBuilder.toString();
//                System.out.println(csv);
//                if(csv!= " " && csv!= null) {
//                    Call<List<QueueList>> call = apiService.getQueueCheckReponse(csv);
//                    call.enqueue(new Callback<List<QueueList>>() {
//                        @Override
//                        public void onResponse(Call<List<QueueList>> call, Response<List<QueueList>> response) {
//                            try {
//                                Config.logV("URL---------------" + response.raw().request().url().toString().trim());
//                                Config.logV("code---------------" + response.code());
//                                mQueueList.clear();
//                                if (response.code() == 200) {
//                                    Config.logV("Sucess123 ----------" + response.body());
//                                    for (int i = 0; i < response.body().size(); i++) {
//                                        mQueueList.add(response.body().get(i));
//                                    }
//                                    if (mCheck.equalsIgnoreCase("next")) {
//                                        Config.logV("TOTAL PAGES_--------------" + TOTAL_PAGES);
//                                        Config.logV("CURRENT PAGE**22222**555***********" + TOTAL_PAGES);
//                                        // pageadapter.removeLoadingFooter();
//                                        isLoading = false;
//                                        mSearchListModel.clear();
//                                        for (int i = 0; i < mSearchRespPass.size(); i++) {
//                                            SearchListModel searchList = new SearchListModel();
//                                            searchList.setId(mSearchRespPass.get(i).getId());
//                                            searchList.setLogo(mSearchRespPass.get(i).getLogo());
//                                            searchList.setPlace1(mSearchRespPass.get(i).getPlace1());
//                                            searchList.setSector(mSearchRespPass.get(i).getSub_sector_displayname());
//                                            searchList.setTitle(mSearchRespPass.get(i).getTitle());
//                                            searchList.setRating(mSearchRespPass.get(i).getRating());
//                                            searchList.setUniqueid(mSearchRespPass.get(i).getUnique_id());
//                                            searchList.setClaimable(mSearchRespPass.get(i).getClaimable());
//                                            searchList.setCoupon_enabled(mSearchRespPass.get(i).getCoupon_enabled());
//                                            searchList.setAccountType(mSearchRespPass.get(i).getAccountType());
//                                            searchList.setBranch_name(mSearchRespPass.get(i).getBranch_name());
//                                            searchList.setSectorname(mSearchRespPass.get(i).getSector());
//                                            searchList.setSub_sector(mSearchRespPass.get(i).getSub_sector());
//                                            searchList.setToday_appt(mSearchRespPass.get(i).getToday_appt());
//                                            searchList.setOnline_profile(mSearchRespPass.get(i).getOnline_profile());
//                                            searchList.setDonation_status(mSearchRespPass.get(i).getDonation_status());
//                                            searchList.setFuture_appt(mSearchRespPass.get(i).getFuture_appt());
//                                            searchList.setLocation1(mSearchRespPass.get(i).getLocation1());
//                                            searchList.setLocation_id1(mSearchRespPass.get(i).getLocation_id1());
//                                            String qualify = "";
//                                            if (mSearchRespPass.get(i).getQualification() != null) {
//                                                for (int l = 0; l < mSearchRespPass.get(i).getQualification().size(); l++) {
//                                                    qualify = qualify + ", " + mSearchRespPass.get(i).getQualification().get(l);
//                                                }
//                                                searchList.setQualification(qualify);
//                                            }
//                                            if (mSearchRespPass.get(i).getDepartment_code() != null) {
//                                                searchList.setDepartment_code(mSearchRespPass.get(i).getDepartment_code());
//                                            }
//                                            if (mSearchRespPass.get(i).getYnw_verified() != null) {
//                                                searchList.setYnw_verified(Integer.parseInt(mSearchRespPass.get(i).getYnw_verified()));
//                                            }
//                                            if (mSearchRespPass.get(i).getDistance() != null) {
//                                                Config.logV("Distance @@@@@@@@@@@" + mSearchRespPass.get(i).getDistance());
//                                                searchList.setDistance(mSearchRespPass.get(i).getDistance());
//                                            }
//                                            if (mSearchRespPass.get(i).getYnw_verified_level() != null) {
//                                                searchList.setYnw_verified_level(mSearchRespPass.get(i).getYnw_verified_level());
//                                            }
//                                            if (mSearchRespPass.get(i).getServices() != null) {
//                                                searchList.setServices(mSearchRespPass.get(i).getServices());
//                                            }
//                                            if (mSearchRespPass.get(i).getAppt_services() != null) {
//                                                searchList.setAppt_services(mSearchRespPass.get(i).getAppt_services());
//                                            }
//                                            if(mSearchRespPass.get(i).getDonation_services()!=null){
//                                                searchList.setDonation_services(mSearchRespPass.get(i).getDonation_services());
//                                            }
//                                            if (mSearchRespPass.get(i).getBusiness_hours1() != null) {
//                                                searchList.setBusiness_hours1(mSearchRespPass.get(i).getBusiness_hours1());
//                                            }
//                                            if (mSearchRespPass.get(i).getTerminologies() != null) {
//                                                searchList.setTerminologies(mSearchRespPass.get(i).getTerminologies());
//                                            }
//                                            if (mSearchRespPass.get(i).getOnline_checkins() != null) {
//                                                searchList.setOnline_checkins(mSearchRespPass.get(i).getOnline_checkins());
//                                            }
//                                            if (mSearchRespPass.get(i).getFuture_checkins() != null) {
//                                                searchList.setFuture_checkins(mSearchRespPass.get(i).getFuture_checkins());
//                                            }
//                                            if (mSearchRespPass.get(i).getShow_waiting_time() != null) {
//                                                searchList.setShow_waiting_time(mSearchRespPass.get(i).getShow_waiting_time());
//                                            }
//                                            if (mSearchRespPass.get(i).getGallery_thumb_nails() != null) {
//                                                //   Config.logV("Gallery-@@@@---111-------5555---------" + mSearchRespPass.get(i).getGallery_thumb_nails());
//                                                searchList.setGallery_thumb_nails(mSearchRespPass.get(i).getGallery_thumb_nails());
//                                            }
//                                            //7types
//                                            if (mSearchRespPass.get(i).getParking_type_location1() != null) {
//                                                Config.logV("PArking-------------" + mSearchRespPass.get(i).getParking_type_location1());
//                                                searchList.setParking_type_location1(mSearchRespPass.get(i).getParking_type_location1());
//                                            }
//                                            if (mSearchRespPass.get(i).getParking_location1() != null) {
//                                                Config.logV("Park-@@@@-------------3333------" + mSearchRespPass.get(i).getParking_location1());
//                                                searchList.setParking_location1(mSearchRespPass.get(i).getParking_location1());
//                                            }
//                                            if (mSearchRespPass.get(i).getAlways_open_location1() != null) {
//                                                searchList.setAlways_open_location1(mSearchRespPass.get(i).getAlways_open_location1());
//                                            }
//
//                                            if (mSearchRespPass.get(i).getTraumacentre_location1() != null) {
//                                                searchList.setTraumacentre_location1(mSearchRespPass.get(i).getTraumacentre_location1());
//                                            }
//                                            if (mSearchRespPass.get(i).getDentistemergencyservices_location1() != null) {
//                                                searchList.setDentistemergencyservices_location1(mSearchRespPass.get(i).getDentistemergencyservices_location1());
//                                            }
//                                            if (mSearchRespPass.get(i).getDocambulance_location1() != null) {
//                                                searchList.setDocambulance_location1(mSearchRespPass.get(i).getDocambulance_location1());
//                                            }
//                                            if (mSearchRespPass.get(i).getPhysiciansemergencyservices_location1() != null) {
//                                                searchList.setPhysiciansemergencyservices_location1(mSearchRespPass.get(i).getPhysiciansemergencyservices_location1());
//                                            }
//                                            if(mSearchRespPass.get(i).getHosemergencyservices_location1() != null){
//                                                searchList.setHosemergencyservices_location1(mSearchRespPass.get(i).getHosemergencyservices_location1());
//                                            }
//                                            if (mSearchRespPass.get(i).getFirstaid_location1() != null) {
//                                                searchList.setFirstaid_location1(mSearchRespPass.get(i).getFirstaid_location1());
//                                            }
//                                            if (mSearchRespPass.get(i).getClaimable().equals("1")) {
//                                                tv_fav.setVisibility(View.GONE);
//                                            }
//                                            searchList.setQId(mSearchRespPass.get(i).getId());
//                                            if (mQueueList.get(i).getMessage() != null) {
//                                                searchList.setMessage(mQueueList.get(i).getMessage());
//                                            }
//                                            if (mQueueList.get(i).getNextAvailableQueue() != null) {
//                                                if (mQueueList.get(i).getNextAvailableQueue().getLocation() != null) {
//                                                    if (mQueueList.get(i).getNextAvailableQueue().getAvailableDate() != null) {
//                                                        searchList.setAvail_date(mQueueList.get(i).getNextAvailableQueue().getAvailableDate());
//                                                    }
//                                                    if (mQueueList.get(i).getNextAvailableQueue().getLocation() != null) {
//                                                        searchList.setmLoc(String.valueOf(mQueueList.get(i).getNextAvailableQueue().getLocation().getId()));
//                                                    }
//                                                    searchList.setIsopen(mQueueList.get(i).getNextAvailableQueue().isOpenNow());
//                                                    searchList.setPersonAhead(mQueueList.get(i).getNextAvailableQueue().getPersonAhead());
//                                                    searchList.setCalculationMode(mQueueList.get(i).getNextAvailableQueue().getCalculationMode());
//                                                    searchList.setQueueWaitingTime(mQueueList.get(i).getNextAvailableQueue().getQueueWaitingTime());
//                                                    searchList.setBranchSpCount(mQueueList.get(i).getBranchSpCount());
//                                                    searchList.setOnlineCheckIn(mQueueList.get(i).getNextAvailableQueue().isOnlineCheckIn());
//                                                    searchList.setWaitlistEnabled(mQueueList.get(i).getNextAvailableQueue().isWaitlistEnabled());
//                                                    searchList.setAvailableToday(mQueueList.get(i).getNextAvailableQueue().isAvailableToday());
//                                                    searchList.setShowToken(mQueueList.get(i).getNextAvailableQueue().isShowToken());
//                                                    if (mQueueList.get(i).getNextAvailableQueue().getServiceTime() != null) {
//                                                        searchList.setServiceTime(mQueueList.get(i).getNextAvailableQueue().getServiceTime());
//                                                    }
//                                                }
//                                            }
//                                            mSearchListModel.add(searchList);
//                                            Log.i("iopiop", new Gson().toJson(mSearchListModel));
//                                        }
//                                        Config.logV("Response--Sucess-------------------------" + new Gson().toJson(mSearchListModel));
//                                        List<SearchListModel> results = mSearchListModel;
//                                    } else {
//                                        mSearchListModel.clear();
//                                        for (int i = 0; i < mSearchRespPass.size(); i++) {
//                                            SearchListModel searchList = new SearchListModel();
//                                            searchList.setId(mSearchRespPass.get(i).getId());
//                                            searchList.setLogo(mSearchRespPass.get(i).getLogo());
//                                            searchList.setPlace1(mSearchRespPass.get(i).getPlace1());
//                                            searchList.setSector(mSearchRespPass.get(i).getSub_sector_displayname());
//                                            searchList.setTitle(mSearchRespPass.get(i).getTitle());
//                                            searchList.setRating(mSearchRespPass.get(i).getRating());
//                                            searchList.setUniqueid(mSearchRespPass.get(i).getUnique_id());
//                                            searchList.setLocation1(mSearchRespPass.get(i).getLocation1());
//                                            searchList.setLocation_id1(mSearchRespPass.get(i).getLocation_id1());
//                                            searchList.setSectorname(mSearchRespPass.get(i).getSector());
//                                            searchList.setSub_sector(mSearchRespPass.get(i).getSub_sector());
//                                            searchList.setClaimable(mSearchRespPass.get(i).getClaimable());
//                                            searchList.setAccountType(mSearchRespPass.get(i).getAccountType());
//                                            searchList.setBranch_name(mSearchRespPass.get(i).getBranch_name());
//                                            searchList.setCoupon_enabled(mSearchRespPass.get(i).getCoupon_enabled());
//                                            searchList.setToday_appt(mSearchRespPass.get(i).getToday_appt());
//                                            searchList.setOnline_profile(mSearchRespPass.get(i).getOnline_profile());
//                                            searchList.setDonation_status(mSearchRespPass.get(i).getDonation_status());
//                                            searchList.setFuture_appt(mSearchRespPass.get(i).getFuture_appt());
//                                            searchList.setSpecialization_displayname(mSearchRespPass.get(i).getSpecialization_displayname());
//                                            String qualify = "";
//                                            if (mSearchRespPass.get(i).getQualification() != null) {
//                                                for (int l = 0; l < mSearchRespPass.get(i).getQualification().size(); l++) {
//                                                    qualify = qualify + ", " + mSearchRespPass.get(i).getQualification().get(l);
//                                                }
//                                                searchList.setQualification(qualify);
//                                            }
//                                            if (mSearchRespPass.get(i).getYnw_verified() != null) {
//                                                searchList.setYnw_verified(Integer.parseInt(mSearchRespPass.get(i).getYnw_verified()));
//                                            }
//                                            if (mSearchRespPass.get(i).getDepartment_code() != null) {
//                                                searchList.setDepartment_code(mSearchRespPass.get(i).getDepartment_code());
//                                            }
//                                            if (mSearchRespPass.get(i).getDistance() != null) {
//                                                Config.logV("Distance @@@@@@@@@@@44444" + mSearchRespPass.get(i).getDistance());
//                                                searchList.setDistance(mSearchRespPass.get(i).getDistance());
//                                            }
//                                            if (mSearchRespPass.get(i).getYnw_verified_level() != null) {
//                                                searchList.setYnw_verified_level(mSearchRespPass.get(i).getYnw_verified_level());
//                                            }
//                                            if (mSearchRespPass.get(i).getServices() != null) {
//                                                searchList.setServices(mSearchRespPass.get(i).getServices());
//                                            }
//                                            if (mSearchRespPass.get(i).getAppt_services() != null) {
//                                                searchList.setAppt_services(mSearchRespPass.get(i).getAppt_services());
//                                            }
//                                            if (mSearchRespPass.get(i).getDonation_services() != null) {
//                                                searchList.setDonation_services(mSearchRespPass.get(i).getDonation_services());
//                                            }
//                                            if (mSearchRespPass.get(i).getBusiness_hours1() != null) {
//                                                searchList.setBusiness_hours1(mSearchRespPass.get(i).getBusiness_hours1());
//                                            }
//                                            if (mSearchRespPass.get(i).getTerminologies() != null) {
//                                                searchList.setTerminologies(mSearchRespPass.get(i).getTerminologies());
//                                            }
//                                            if (mSearchRespPass.get(i).getOnline_checkins() != null) {
//                                                searchList.setOnline_checkins(mSearchRespPass.get(i).getOnline_checkins());
//                                            }
//                                            if (mSearchRespPass.get(i).getFuture_checkins() != null) {
//                                                searchList.setFuture_checkins(mSearchRespPass.get(i).getFuture_checkins());
//                                            }
//                                            if (mSearchRespPass.get(i).getShow_waiting_time() != null) {
//                                                searchList.setShow_waiting_time(mSearchRespPass.get(i).getShow_waiting_time());
//                                            }
//                                            if (mSearchRespPass.get(i).getGallery_thumb_nails() != null) {
//                                                // Config.logV("Gallery ###########"+mSearchRespPass.get(i).getGallery_thumb_nails());
//                                                searchList.setGallery_thumb_nails(mSearchRespPass.get(i).getGallery_thumb_nails());
//                                            }
//                                            if (mSearchRespPass.get(i).getParking_type_location1() != null) {
//                                                searchList.setParking_type_location1(mSearchRespPass.get(i).getParking_type_location1());
//                                            }
//                                            if (mSearchRespPass.get(i).getParking_location1() != null) {
//                                                Config.logV("Park-@@@@----------4444-----" + mSearchRespPass.get(i).getParking_location1());
//                                                searchList.setParking_location1(mSearchRespPass.get(i).getParking_location1());
//                                            }
//                                            if (mSearchRespPass.get(i).getAlways_open_location1() != null) {
//                                                searchList.setAlways_open_location1(mSearchRespPass.get(i).getAlways_open_location1());
//                                            }
//                                            if (mSearchRespPass.get(i).getTraumacentre_location1() != null) {
//                                                searchList.setTraumacentre_location1(mSearchRespPass.get(i).getTraumacentre_location1());
//                                            }
//                                            if (mSearchRespPass.get(i).getDentistemergencyservices_location1() != null) {
//                                                searchList.setDentistemergencyservices_location1(mSearchRespPass.get(i).getDentistemergencyservices_location1());
//                                            }
//                                            if (mSearchRespPass.get(i).getDocambulance_location1() != null) {
//                                                searchList.setDocambulance_location1(mSearchRespPass.get(i).getDocambulance_location1());
//                                            }
//                                            if (mSearchRespPass.get(i).getPhysiciansemergencyservices_location1() != null) {
//                                                searchList.setPhysiciansemergencyservices_location1(mSearchRespPass.get(i).getPhysiciansemergencyservices_location1());
//                                            }
//                                            if(mSearchRespPass.get(i).getHosemergencyservices_location1() != null){
//                                                searchList.setHosemergencyservices_location1(mSearchRespPass.get(i).getHosemergencyservices_location1());
//                                            }
//                                            if (mSearchRespPass.get(i).getFirstaid_location1() != null) {
//                                                searchList.setFirstaid_location1(mSearchRespPass.get(i).getFirstaid_location1());
//                                            }
//                                            searchList.setQId(mSearchRespPass.get(i).getId());
//                                            if (mQueueList.get(i).getMessage() != null) {
//                                                searchList.setMessage(mQueueList.get(i).getMessage());
//                                            }
//                                            if (mQueueList.get(i).getNextAvailableQueue() != null) {
//                                                if (mQueueList.get(i).getNextAvailableQueue().getLocation() != null) {
//                                                    if (mQueueList.get(i).getNextAvailableQueue().getAvailableDate() != null) {
//                                                        searchList.setAvail_date(mQueueList.get(i).getNextAvailableQueue().getAvailableDate());
//                                                    }
//                                                    if (mQueueList.get(i).getNextAvailableQueue().getLocation() != null) {
//                                                        searchList.setmLoc(String.valueOf(mQueueList.get(i).getNextAvailableQueue().getLocation().getId()));
//                                                    }
//                                                    searchList.setIsopen(mQueueList.get(i).getNextAvailableQueue().isOpenNow());
//                                                    searchList.setPersonAhead(mQueueList.get(i).getNextAvailableQueue().getPersonAhead());
//                                                    searchList.setCalculationMode(mQueueList.get(i).getNextAvailableQueue().getCalculationMode());
//                                                    searchList.setQueueWaitingTime(mQueueList.get(i).getNextAvailableQueue().getQueueWaitingTime());
//                                                    searchList.setBranchSpCount(mQueueList.get(i).getBranchSpCount());
//                                                    searchList.setOnlineCheckIn(mQueueList.get(i).getNextAvailableQueue().isOnlineCheckIn());
//                                                    searchList.setOnlineCheckIn(mQueueList.get(i).getNextAvailableQueue().isOnlineCheckIn());
//                                                    searchList.setWaitlistEnabled(mQueueList.get(i).getNextAvailableQueue().isWaitlistEnabled());
//                                                    searchList.setShowToken(mQueueList.get(i).getNextAvailableQueue().isShowToken());
//                                                    if (mQueueList.get(i).getNextAvailableQueue().getServiceTime() != null) {
//                                                        searchList.setServiceTime(mQueueList.get(i).getNextAvailableQueue().getServiceTime());
//                                                    }
//                                                }
//                                            }
//                                            mSearchListModel.add(searchList);
//                                        }
//                                        final List<SearchListModel> results = mSearchListModel;
//                                    }
//                                    groupByDepartmentCode(mSearchListModel);
//                                    if (mProviderId != 0) {
//                                        ApiDepartment(mProviderId);
//                                    }
//                                    if(homeUniqueId==null){
//                                        ApiDepartmentServices(uniqueID);
//                                    }else{
//                                        ApiDepartmentServices(homeUniqueId);
//                                    }
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<List<QueueList>> call, Throwable t) {
//                            // Log error here since request failed
//                            Config.logV("Fail---------------" + t.toString());
//                        }
//                    });
//                }
//            }
//            @Override
//            public void onFailure(Call<SearchAWsResponse> call, Throwable t) {
//                t.printStackTrace();
//            }
//        });
//    }
//    private void listProviders(final String muniqueID) {
//        final ApiInterface apiService = ApiClient.getClientAWS(mContext).create(ApiInterface.class);
//        Map<String, String> query = new HashMap<>();
//        query.put("start", "0");
//        query.put("q", "(and " + "unique_id:" + muniqueID + ")");
//        String mobile = SharedPreference.getInstance(mContext).getStringValue("mobile", "");
//        if (mobile.startsWith("55")) {
//            query.put("fq", "(and  test_account:1 )");
//        } else {
//            query.put("fq", "(and  (not test_account:1) )");
//        }
//        Map<String, String> params = new HashMap<>();
//        params.put("size", "10000");
//        params.put("q.parser", "structured");
//        params.put("sort", "claimable asc,ynw_verified_level desc, distance asc");
//        params.put("expr.distance", "haversin(" + lat_long + ", location1.latitude, location1.longitude)");
//        params.put("return", "_all_fields,distance");
//        Call<SearchAWsResponse> call = apiService.getSearchAWS(query, params);
//        call.enqueue(new Callback<SearchAWsResponse>() {
//            @Override
//            public void onResponse(Call<SearchAWsResponse> call, Response<SearchAWsResponse> response) {
//                try {
//                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
//                    Config.logV("Response--code-------------------------" + response.code());
//                    if (response.code() == 200) {
//                        if (response.body().getHits().getFound() > 0) {
//                            mSearchResp.clear();
//                            ArrayList<String> ids = new ArrayList<>();
//                            for (int i = 0; i < response.body().getHits().getHit().size(); i++) {
//                                SearchAWsResponse search = new SearchAWsResponse();
//                                if (response.body().getHits().getHit().get(i).getFields().getFuture_checkins() != null) {
//                                    search.setFuture_checkins(response.body().getHits().getHit().get(i).getFields().getFuture_checkins());
//                                }
//                                mSearchResp.add(search);
//                            }
//                        }
//                        mSearchAWSResponse = response.body();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            @Override
//            public void onFailure(Call<SearchAWsResponse> call, Throwable t) {
//                t.printStackTrace();
//            }
//        });
//    }
//    void groupByDepartmentCode(List<SearchListModel> mSearchListModel) {
//        departmentMap = new HashMap<String, List<SearchListModel>>();
//        for (SearchListModel searchModel : mSearchListModel) {
//            String deptCode = searchModel.getDepartment_code();
//            if (departmentMap.containsKey(deptCode)) {
//                departmentMap.get(deptCode).add(searchModel);
//            } else {
//                ArrayList<SearchListModel> emptyList = new ArrayList<SearchListModel>();
//                emptyList.add(searchModel);
//                departmentMap.put(deptCode, emptyList);
//            }
//        }
//        Log.i("Groupby", new Gson().toJson(departmentMap));
//    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == CALL_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Config.logV("CALL GRANTED @@@@@@@@@@@@@@");
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(callIntent);
            } else {
                Toast.makeText(mContext, getResources().getString(R.string.call_permission_denied_message), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void departmentClicked(SearchDepartmentServices searchDepartment, String businessName, String providerTerminology) {
        onMethodDepartment(searchDepartment, businessName, providerTerminology);
    }

    @Override
    public void usersClick(ArrayList<ProviderUserModel> usersList, String businessName) {
        onMethodUsersClick(usersList, businessName, userTerminology);
    }

    @Override
    public boolean onBackPressed() {

        return super.onBackPressed();
    }
}
