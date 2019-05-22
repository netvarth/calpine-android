package com.nv.youneverwait.Fragment;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.Gson;
import com.nv.youneverwait.R;
import com.nv.youneverwait.activities.BillActivity;
import com.nv.youneverwait.activities.Home;
import com.nv.youneverwait.activities.PaymentActivity;
import com.nv.youneverwait.database.DatabaseHandler;
import com.nv.youneverwait.payment.PaymentGateway;
import com.nv.youneverwait.payment.PaytmPayment;
import com.nv.youneverwait.activities.SearchLocationActivity;
import com.nv.youneverwait.adapter.ActiveCheckInAdapter;
import com.nv.youneverwait.adapter.SearchListAdpter;
import com.nv.youneverwait.callback.ActiveAdapterOnCallback;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.custom.CustomTypefaceSpan;
import com.nv.youneverwait.model.CheckSumModelTest;
import com.nv.youneverwait.model.Domain_Spinner;
import com.nv.youneverwait.model.LanLong;
import com.nv.youneverwait.model.ListCell;
import com.nv.youneverwait.model.SearchModel;
import com.nv.youneverwait.response.ActiveCheckIn;
import com.nv.youneverwait.response.PaymentModel;
import com.nv.youneverwait.response.RefinedFilters;
import com.nv.youneverwait.utils.EmptySubmitSearchView;
import com.nv.youneverwait.utils.LogUtil;
import com.nv.youneverwait.utils.SharedPreference;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.crypto.Mac;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class    DashboardFragment extends RootFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener, ActiveAdapterOnCallback {

    public DashboardFragment() {
        // Required empty public constructor
    }

    static Context mContext;
    RecyclerView mRecycleActive;
    Fragment active;
    TextView txtUsername;
    Toolbar toolbar;
    static Fragment home;
    static TextView mCurrentLoc;
    Spinner mSpinnerDomain;
    String AWS_URL = "";


    ArrayList<Domain_Spinner> domainList = new ArrayList<>();
    ArrayList<SearchModel> mGLobalSearch = new ArrayList<>();
    ArrayList<SearchModel> mSubDomainSubSearch = new ArrayList<>();
    ArrayList<SearchModel> mSubDomain = new ArrayList<>();
    ArrayList<SearchModel> mSpecializationDomain = new ArrayList<>();
    ArrayList<SearchModel> mSpecializationDomainSearch = new ArrayList<>();


    ArrayList<SearchModel> mPopularSearchList = new ArrayList<>();

    ArrayList<SearchModel> mPopular_SubSearchList = new ArrayList<>();

    ArrayList<SearchModel> mPopular_AllSearchList = new ArrayList<>();


    EmptySubmitSearchView mSearchView;
    SearchView.SearchAutoComplete searchSrcTextView;
    String mDomainSpinner;
    SearchListAdpter listadapter;
    ArrayList<ListCell> items;

    private Location mylocation;
    private GoogleApiClient googleApiClient;
    public final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    static double latitude;
    static double longitude;
    TextView tv_activechkin, tv_popular;
    LinearLayout LpopularSearch, LActiveCheckin, LinearPopularSearch, LinearMorePopularSearch, LMore;
    TextView tv_More;
    boolean is_MoreClick = false;
    LinearLayout Lhome_mainlayout;
    FrameLayout mainlayout;

    String mSearchtxt;
    String mPopularSearchtxt;

    String spinnerTxtPass;
    ActiveAdapterOnCallback mInterface;
    static String mlocName;
    ImageView img_arrow;


    public void funPopulateSearchList(final ArrayList<SearchModel> mPopularSearchList) {
        if (mPopularSearchList.size() > 0) {

            is_MoreClick = false;
            LpopularSearch.setVisibility(View.VISIBLE);
            LinearPopularSearch.setVisibility(View.VISIBLE);
            LinearMorePopularSearch.setVisibility(View.GONE);
            LinearPopularSearch.removeAllViews();
            if (mPopularSearchList.size() > 6) {
                tv_More.setVisibility(View.VISIBLE);
                LMore.setVisibility(View.VISIBLE);
            } else {
                tv_More.setVisibility(View.GONE);
                LMore.setVisibility(View.GONE);
            }

            int k = 0;
            int rowsize = 0;

            if (mPopularSearchList.size() > 3) {
                rowsize = 2;
            } else {
                rowsize = 1;
            }
            for (int i = 0; i < rowsize; i++) {
                LinearLayout parent1 = new LinearLayout(mContext);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                //params.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                parent1.setOrientation(LinearLayout.HORIZONTAL);
                parent1.setLayoutParams(params);

                for (int j = 0; j < 3; j++) {
                    if (k >= mPopularSearchList.size()) {
                        break;
                    } else {
                        TextView dynaText = new TextView(mContext);
                        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                                "fonts/Montserrat_Regular.otf");
                        dynaText.setTypeface(tyface);
                        dynaText.setText(mPopularSearchList.get(k).getDisplayname());
                        dynaText.setBackground(getResources().getDrawable(R.drawable.rounded_popularsearch));

                        dynaText.setTextSize(12);
                        dynaText.setTextColor(getResources().getColor(R.color.dark_blue));
                        dynaText.setPadding(15, 10, 15, 10);
                        //dynaText.setEllipsize(TextUtils.TruncateAt.END);
                        dynaText.setMaxLines(1);
                        // dynaText.setMaxEms(8);
                        dynaText.setWidth(dpToPx(120));
                        dynaText.setGravity(Gravity.CENTER);

                        params.setMargins(12, 10, 12, 0);
                        dynaText.setLayoutParams(params);


                        //   dynaText.setTag("" + i);
                        final int finalK = k;
                        dynaText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                mPopularSearchtxt = mPopularSearchList.get(finalK).getDisplayname();
                                Config.logV("Popular Text__________@@@____" + mPopularSearchtxt);
                                FunPopularSearch(mPopularSearchList.get(finalK).getQuery(), "Suggested Search", mPopularSearchList.get(finalK).getName());
                            }
                        });
                        parent1.addView(dynaText);
                        k++;
                    }

                }
                LinearPopularSearch.addView(parent1);
            }

        }
    }

    TextView txt_sorry;
    boolean activeCheckin = false;
    ImageView ic_refinedFilter;
    static Activity mActivity;
    DatabaseHandler db;
    boolean userIsInteracting=false;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // do something when visible.
            userIsInteracting=false;
            Config.logV("Spinner ITEM NOT CLICKED @@@@@@@@@@@@@@@@"+userIsInteracting);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View row = inflater.inflate(R.layout.fragment_myhome, container, false);
        mRecycleActive = (RecyclerView) row.findViewById(R.id.recycleActive);
        Lhome_mainlayout = (LinearLayout) row.findViewById(R.id.homemainlayout);
        LMore = (LinearLayout) row.findViewById(R.id.LMore);
        img_arrow = (ImageView) row.findViewById(R.id.img_arrow);

        Home.doubleBackToExitPressedOnce = false;
        mActivity = getActivity();

        txt_sorry = (TextView) row.findViewById(R.id.txt_sorry);
        mainlayout = (FrameLayout) row.findViewById(R.id.mainlayout);
        mCurrentLoc = (TextView) row.findViewById(R.id.currentloc);

        Config.logV("OnCreateView-@@@@@@@@@------------------");

        ic_refinedFilter = (ImageView) row.findViewById(R.id.ic_refinedFilter);

        ic_refinedFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Config.isOnline(getActivity())) {
                    ApiFilters();
                }
            }
        });


        mInterface = (ActiveAdapterOnCallback) this;
        home = getParentFragment();
        //Location

        String s_currentLoc = SharedPreference.getInstance(getActivity()).getStringValue("current_loc", "");
        Config.logV("UpdateLocation noooooooooooooooooo" + s_currentLoc);
        if (!s_currentLoc.equalsIgnoreCase("no")) {
            setUpGClient();
            //Config.logV("UpdateLocation noooooooooooooooooo" + latitude);
        } else if (s_currentLoc.equalsIgnoreCase("no")) {

            latitude = Double.valueOf(SharedPreference.getInstance(mContext).getStringValue("lat", ""));
            longitude = Double.valueOf(SharedPreference.getInstance(mContext).getStringValue("longitu", ""));
            mlocName = SharedPreference.getInstance(mContext).getStringValue("locnme", "");
            UpdateLocation(latitude, longitude, mlocName);


        } else {
            Config.logV("UpdateLocation banglore");
            latitude = 12.971599;
            longitude = 77.594563;
            try {
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                mCurrentLoc.setVisibility(View.VISIBLE);
                mCurrentLoc.setText(addresses.get(0).getLocality());
                //   Config.logV("Latitude-----11111--------"+addresses.get(0).getAddressLine(0));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mCurrentLoc.getText().equals("")) {
            DefaultLocation();
        }

        mContext = getActivity();
        active = getParentFragment();
        //  SharedPreference.getInstance(mContext).setValue("fragmentonce",false);

        String mFirstNAme = SharedPreference.getInstance(mContext).getStringValue("firstname", "");
        String mLastNAme = SharedPreference.getInstance(mContext).getStringValue("lastname", "");
        txtUsername = (TextView) row.findViewById(R.id.username);
        //txtUsername.setText("Helloo " + mFirstNAme + " " + mLastNAme);


        mSpinnerDomain = (Spinner) row.findViewById(R.id.spinnerdomain);
        tv_activechkin = (TextView) row.findViewById(R.id.txt_activechkin);
        tv_popular = (TextView) row.findViewById(R.id.txt_popular);
        LpopularSearch = (LinearLayout) row.findViewById(R.id.LpopularSearch);
        LActiveCheckin = (LinearLayout) row.findViewById(R.id.LActiveCheckin);
        LinearPopularSearch = (LinearLayout) row.findViewById(R.id.LinearPopularSearch);
        LinearMorePopularSearch = (LinearLayout) row.findViewById(R.id.LinearMorePopularSearch);
        tv_More = (TextView) row.findViewById(R.id.txtMore);


        Typeface tyface = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Montserrat_Bold.otf");

        tv_activechkin.setTypeface(tyface);
        mCurrentLoc.setTypeface(tyface);
        tv_popular.setTypeface(tyface);


        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //APiGetDomain();
        APiSearchList();

        mCurrentLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent iLoc = new Intent(mContext, SearchLocationActivity.class);
                iLoc.putExtra("from", "dashboard");
                mContext.startActivity(iLoc);
            }
        });
        /////////////////////////////////////


        if (Config.isOnline(getActivity())) {
            Config.logV("Active Checkin------@@@@@@@@@@@-------------" + activeCheckin);
            if (!activeCheckin) {
                activeCheckin = true;
                ApiActiveCheckIn();

            }
            ApiAWSearchDomain();

        } else {
            MActiveList.clear();
            db = new DatabaseHandler(getActivity());
            MActiveList = db.getAllCheckinList();
            Config.logV("Mactivie" + MActiveList.size());
            if (MActiveList.size() > 0) {
                tv_activechkin.setText("Active Check-ins " + "(" + String.valueOf(MActiveList.size()) + ")");
                LActiveCheckin.setVisibility(View.VISIBLE);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                mRecycleActive.setLayoutManager(mLayoutManager);
                activeAdapter = new ActiveCheckInAdapter(MActiveList, mContext, getActivity(), active, mInterface);
                mRecycleActive.setAdapter(activeAdapter);
                activeAdapter.notifyDataSetChanged();
            } else {

                tv_activechkin.setText("Active Check-ins ");
                txt_sorry.setVisibility(View.VISIBLE);
                LActiveCheckin.setVisibility(View.VISIBLE);
            }

        }


        // ArrayAdapter<Domain_Spinner> adapter = new ArrayAdapter<Domain_Spinner>(getActivity(), android.R.layout.simple_spinner_dropdown_item, domainList);

        ArrayAdapter<Domain_Spinner> adapter = new ArrayAdapter<Domain_Spinner>(getActivity(), R.layout.spinner_item, domainList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


       // mSpinnerDomain.setAdapter(adapter);

        mSpinnerDomain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {


                searchSrcTextView.setText("");
                //  Spinnertext = parent.getSelectedItem().toString();
                mDomainSpinner = ((Domain_Spinner) mSpinnerDomain.getSelectedItem()).getDomain();

                spinnerTxtPass = ((Domain_Spinner) mSpinnerDomain.getSelectedItem()).getDisplayName();
                Config.logV("Selected-----------" + spinnerTxtPass);


                Config.logV("Spinner ITEM NOT CLICKED $$$$$$$$$$$$$$$$$$$$$$$$$$$$$"+userIsInteracting);
                if(userIsInteracting){
                    Config.logV("Spinner ITEM CLICKED $$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                    SpinnerItemCLick();
                }
                userIsInteracting=true;


                /////////test code///////////////////////////

                is_MoreClick=false;
                img_arrow.setImageResource(R.drawable.icon_down_arrow_blue);
                tv_More.setText("More");
                mPopularSearchList.clear();
                if (mDomainSpinner.equalsIgnoreCase("All")) {
                    mPopular_AllSearchList.addAll(mGLobalSearch);
                    mPopularSearchList = mPopular_AllSearchList;
                    funPopulateSearchList(mPopularSearchList);

                } else {
                    mPopular_SubSearchList.clear();
                    for (int i = 0; i < mSubDomain.size(); i++) {

                        if (mSubDomain.get(i).getSector().toLowerCase().trim().equalsIgnoreCase(mDomainSpinner.toLowerCase().trim())) {


                                SearchModel search = new SearchModel();
                                 search.setName(mSubDomain.get(i).getName());
                                search.setQuery(mSubDomain.get(i).getQuery());
                                search.setSector(mSubDomain.get(i).getSector());
                                search.setDisplayname(mSubDomain.get(i).getDisplayname());

                                mPopular_SubSearchList.add(search);

                        }

                    }

                    mPopularSearchList = mPopular_SubSearchList;
                    funPopulateSearchList(mPopularSearchList);

                }


                LMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (mPopularSearchList.size() > 0) {

                            if (is_MoreClick) {
                                is_MoreClick = false;
                                LpopularSearch.setVisibility(View.VISIBLE);
                                LinearPopularSearch.setVisibility(View.VISIBLE);
                                LinearMorePopularSearch.setVisibility(View.GONE);
                                tv_More.setVisibility(View.VISIBLE);
                                LMore.setVisibility(View.VISIBLE);
                                img_arrow.setImageResource(R.drawable.icon_down_arrow_blue);
                                tv_More.setText("More");
                            } else {
                                is_MoreClick = true;
                                tv_More.setText("Less");
                                img_arrow.setImageResource(R.drawable.icon_up_arrow_blue);
                                tv_More.setVisibility(View.VISIBLE);
                                LMore.setVisibility(View.VISIBLE);
                                LpopularSearch.setVisibility(View.VISIBLE);
                                LinearPopularSearch.setVisibility(View.GONE);
                                LinearMorePopularSearch.setVisibility(View.VISIBLE);
                                LinearMorePopularSearch.removeAllViews();

                                int k = 0;
                                int add_row = 0;
                                int rem = mPopularSearchList.size() % 3;
                                if (rem == 0) {

                                    add_row = 0;

                                } else {

                                    add_row = 1;
                                }
                                for (int i = 0; i < (mPopularSearchList.size() / 3) + add_row; i++) {
                                    LinearLayout parent = new LinearLayout(mContext);

                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    //params.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                    parent.setOrientation(LinearLayout.HORIZONTAL);
                                    parent.setLayoutParams(params);


                                    for (int j = 0; j < 3; j++) {

                                        if (k >= mPopularSearchList.size()) {
                                            break;
                                        } else {


                                            TextView dynaText = new TextView(mContext);
                                            Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                                                    "fonts/Montserrat_Regular.otf");
                                            dynaText.setTypeface(tyface);

                                            dynaText.setText(mPopularSearchList.get(k).getDisplayname());


                                            dynaText.setBackground(getResources().getDrawable(R.drawable.rounded_popularsearch));
                                            dynaText.setTextSize(12);
                                            dynaText.setTextColor(getResources().getColor(R.color.dark_blue));
                                            dynaText.setPadding(15, 10, 15, 10);
                                            // dynaText.setEllipsize(TextUtils.TruncateAt.END);
                                            dynaText.setMaxLines(1);
                                            //dynaText.setMaxEms(8);
                                            dynaText.setGravity(Gravity.CENTER);
                                            dynaText.setWidth(dpToPx(120));

                                            params.setMargins(15, 10, 15, 0);
                                            dynaText.setLayoutParams(params);

                                            //   dynaText.setTag("" + i);
                                            final int finalK = k;
                                            dynaText.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    mPopularSearchtxt = mPopularSearchList.get(finalK).getDisplayname();
                                                    Config.logV("Popular Text______________" + mPopularSearchtxt);
                                                    FunPopularSearch(mPopularSearchList.get(finalK).getQuery(), "Suggested Search", mPopularSearchList.get(finalK).getName());


                                                }
                                            });
                                            parent.addView(dynaText);

                                            k++;
                                        }

                                    }
                                    LinearMorePopularSearch.addView(parent);
                                }
                            }

                        }
                    }
                });


                ///////////////////////////////////////////////////////////////


                if (mDomainSpinner.equalsIgnoreCase("ALL")) {

                    /**********************************HEADER=SuGGESTED SEARCH*************************************/

                    Config.logV("mGLobalSearch" + mGLobalSearch.size());

                               /* ArrayList<ListCell>*/
                    items = new ArrayList<ListCell>();
                    for (int i = 0; i < mGLobalSearch.size(); i++) {

                        items.add(new ListCell(mGLobalSearch.get(i).getName(), "Suggested Search", mGLobalSearch.get(i).getQuery(), mGLobalSearch.get(i).getDisplayname()));
                    }


                    /**********************************HEADER+SPECIALIZATION**************************************/

                    //HEADER+SPECIALIZATION

                   // mSpecializationDomainSearch.clear();



                    for (int i = 0; i < mSpecializationDomain.size(); i++) {
                        // Config.logV("mSectorSubSearch.get(i).getName()" + mSectorSubSearch.get(i).getName());
                        items.add(new ListCell(mSpecializationDomain.get(i).getName(), "Specializations", mSpecializationDomain.get(i).getSector(), mSpecializationDomain.get(i).getDisplayname()));
                    }


                           /*     *******************************************************************/

                    listadapter = new SearchListAdpter("search", getActivity(), items, latitude, longitude, getParentFragment(), mSearchView);
                    searchSrcTextView.setAdapter(listadapter);

                    mSearchView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(mContext, "Searchbox clicked", Toast.LENGTH_SHORT).show();
                        }
                    });

                    mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {

                            Config.logV("Keyboard CLick @@@@@@@@@@@@@@@@@@");


                            QuerySubmitCLick(query);
                            return true;
                        }

                        @Override
                        public boolean onQueryTextChange(String query) {
                            // filter recycler view when text is changed

                            if (!query.equalsIgnoreCase("")) {

                                mSearchtxt = query;

                                Config.logV("SEARCH TXT--------------88252-" + mSearchtxt);
                            }


                            ImageView searchIcon = (ImageView) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
                            searchIcon.setImageDrawable(null);
                            searchIcon.setVisibility(View.GONE);
                            if (query.length() > 1) {

                                //list.setAdapter(adapter);
                                for (int i = 0; i < items.size(); i++) {
                                    if (items.get(i).getCategory().equalsIgnoreCase("Business Name as")) {
                                        items.remove(items.get(i));
                                    }
                                }

                                items.add(new ListCell(query, "Business Name as", mDomainSpinner, query));

                               /* searchSrcTextView.setAdapter(listadapter);*/
                                listadapter.notifyDataSetChanged();
                                listadapter.getFilter().filter(query);
                            }

                            return false;
                        }
                    });


                } else {



                    items = new ArrayList<ListCell>();
                    //HEADER+SUBDOMAIN
                    mSubDomainSubSearch.clear();

                    for (int i = 0; i < mSubDomain.size(); i++) {
                        if (mSubDomain.get(i).getSector().equalsIgnoreCase(mDomainSpinner)) {

                            SearchModel search=new SearchModel();
                            search.setDisplayname(mSubDomain.get(i).getDisplayname());
                            search.setSector(mSubDomain.get(i).getSector());
                            search.setName(mSubDomain.get(i).getName());
                            mSubDomainSubSearch.add(search);
                        }
                    }
                    for (int i = 0; i < mSubDomainSubSearch.size(); i++) {

                        items.add(new ListCell(mSubDomainSubSearch.get(i).getName(), "Sub Domain", mSubDomainSubSearch.get(i).getSector(), mSubDomainSubSearch.get(i).getDisplayname()));
                    }


                    /**********************************HEADER+SPECIALIZATION**************************************/

                    //HEADER+SPECIALIZATION
                    mSpecializationDomainSearch.clear();
                    for (int i = 0; i < mSpecializationDomain.size(); i++) {
                        if (mSpecializationDomain.get(i).getSector().equalsIgnoreCase(mDomainSpinner)) {


                                SearchModel domain = new SearchModel();
                                domain.setName(mSpecializationDomain.get(i).getName());
                                domain.setSector(mSpecializationDomain.get(i).getSector());
                                domain.setDisplayname(mSpecializationDomain.get(i).getDisplayname());

                                mSpecializationDomainSearch.add(domain);

                        }

                    }


                    for (int i = 0; i < mSpecializationDomainSearch.size(); i++) {
                        items.add(new ListCell(mSpecializationDomainSearch.get(i).getName(), "Specializations", mSpecializationDomainSearch.get(i).getSector(), mSpecializationDomainSearch.get(i).getDisplayname()));
                    }


                    listadapter = new SearchListAdpter("search", getActivity(), items, latitude, longitude, home, mSearchView);
                    searchSrcTextView.setAdapter(listadapter);

                    mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {

                            Config.logV("Keyboard CLick @@@@@@@@@@@@@@@@@@");
                            QuerySubmitCLick(query);
                            return true;
                        }

                        @Override
                        public boolean onQueryTextChange(String query) {
                            // filter recycler view when text is changed

                            if (!query.equalsIgnoreCase("")) {

                                mSearchtxt = query;

                                Config.logV("SEARCH TXT--------------88252-" + mSearchtxt);
                            }

                            ImageView searchIcon = (ImageView) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
                            searchIcon.setImageDrawable(null);
                            searchIcon.setVisibility(View.GONE);

                            if (query.length() > 1) {

                                //list.setAdapter(adapter);
                                //searchSrcTextView.setThreshold(3);
                                for (int i = 0; i < items.size(); i++) {
                                    if (items.get(i).getCategory().equalsIgnoreCase("Business Name as")) {
                                        items.remove(items.get(i));
                                    }
                                }

                                items.add(new ListCell(query, "Business Name as", mDomainSpinner, query));


                                // searchSrcTextView.setAdapter(listadapter);
                                listadapter.notifyDataSetChanged();
                                listadapter.getFilter().filter(query);
                            }

                            return false;
                        }
                    });


                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //SEARCH
        mSearchView = (EmptySubmitSearchView) row.findViewById(R.id.search);
        searchSrcTextView = (SearchView.SearchAutoComplete) row.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        SearchManager searchMng = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchMng.getSearchableInfo(getActivity().getComponentName()));
        searchSrcTextView.setDropDownHeight(450);

        ImageView searchClose = (ImageView) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        searchClose.setImageResource(R.drawable.icon_cancel);


        final View dropDownAnchor = mSearchView.findViewById(searchSrcTextView.getDropDownAnchor());


        if (dropDownAnchor != null) {
            dropDownAnchor.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {

                    // screen width
                    int screenWidthPixel = mContext.getResources().getDisplayMetrics().widthPixels;
                    searchSrcTextView.setDropDownWidth(screenWidthPixel - 30);
                    searchSrcTextView.setDropDownBackgroundResource(R.drawable.roundedrect_blur_bg);
                    searchSrcTextView.setDropDownVerticalOffset(20);


                }
            });
        }


        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        searchSrcTextView.setTypeface(tyface1);
        searchSrcTextView.setTextColor(mContext.getResources().getColor(R.color.title_grey));
        searchSrcTextView.setHintTextColor(mContext.getResources().getColor(R.color.title_grey_light));


        String searchHint = "Eg: Dentist, Restaurants, Car Wash...";
        Spannable spannable = new SpannableString(searchHint);

        Typeface tyface_edittext1 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Light.otf");


        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface_edittext1), 0, searchHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        searchSrcTextView.setHint(spannable);


        searchSrcTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String mSector = "";
                ListCell cell = listadapter.getItem(position);
                Config.logV("Popular Text__________@@@Dele111");
                mSearchView.setQuery("", false);

                LanLong Lanlong = getLocationNearBy(latitude, longitude);
                double upperLeftLat = Lanlong.getUpperLeftLat();
                double upperLeftLon = Lanlong.getUpperLeftLon();
                double lowerRightLat = Lanlong.getLowerRightLat();
                double lowerRightLon = Lanlong.getLowerRightLon();
                String locationRange = "['" + lowerRightLat + "," + lowerRightLon + "','" + upperLeftLat + "," + upperLeftLon + "']";
                String sub = "";
                String querycreate = "";
                mSector = cell.getMsector();
                Config.logV("Sector--------------" + mSector);

                if (cell.getCategory().equalsIgnoreCase("Specializations")) {
                    sub = "specialization:" + "'" + cell.getName() + "'";
                    querycreate = "sector:" + "'" + mSector + "'" + " " + sub;
                }
                if (cell.getCategory().equalsIgnoreCase("Sub Domain")) {

                    sub = "sub_sector:" + "'" + cell.getName() + "'";
                    querycreate = "sector:" + "'" + mSector + "'" + " " + sub;
                }

                if (cell.getCategory().equalsIgnoreCase("Suggested Search")) {
                    Config.logV("Query------------" + cell.getMsector());
                    String requiredString = cell.getMsector().substring(cell.getMsector().indexOf("]") + 1, cell.getMsector().indexOf(")"));
                    Config.logV("Second---------" + requiredString);
                    querycreate = requiredString;
                }

                if (cell.getCategory().equalsIgnoreCase("Business Name as")) {


                    if (mSector.equalsIgnoreCase("All")) {
                        querycreate = "title:" + "'" + cell.getName() + "'";
                    } else {
                        sub = "title:" + "'" + cell.getName() + "'";
                        querycreate = "sector:" + "'" + mSector + "'" + " " + sub;
                    }
                }


                Config.logV("Query-----------" + querycreate);


                // String pass = "haversin(11.751416900900901,75.3701820990991, location1.latitude, location1.longitude)";


                String pass = "haversin(" + latitude + "," + longitude + ", location1.latitude, location1.longitude)";

                Bundle bundle = new Bundle();
                /*bundle.putString("query", "(and location1:['11.751416900900901,75.3701820990991','9.9496150990991,77.171983900900'] " + querycreate + ")");
                bundle.putString("url", pass);*/


                //VALID QUERY PASS
                bundle.putString("query", "(and location1:" + locationRange + querycreate + ")");
                bundle.putString("url", pass);
                SearchListFragment pfFragment = new SearchListFragment();


                bundle.putString("locName", mCurrentLoc.getText().toString());

                bundle.putString("latitude", String.valueOf(latitude));
                bundle.putString("longitude", String.valueOf(longitude));
                bundle.putString("spinnervalue", spinnerTxtPass);

                mSearchtxt = cell.getMdisplayname();
                Config.logV("SEARCH TXT 99999" + mSearchtxt);
                bundle.putString("searchtxt", mSearchtxt);
                pfFragment.setArguments(bundle);


                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
                // Store the Fragment in stack
                transaction.addToBackStack(null);
                transaction.replace(R.id.mainlayout, pfFragment).commit();
                searchSrcTextView.clearFocus();
                mSearchView.clearFocus();


            }
        });


        return row;

    }


    private void APiSearchList() {
        {


            final ApiInterface apiService =
                    ApiClient.getClient(getActivity()).create(ApiInterface.class);

            final Dialog mDialog = Config.getProgressDialog(getActivity(), getActivity().getResources().getString(R.string.dialog_log_in));
            mDialog.show();

            Call<SearchModel> call = apiService.getAllSearch();


            call.enqueue(new Callback<SearchModel>() {
                @Override
                public void onResponse(Call<SearchModel> call, Response<SearchModel> response) {

                    try {

                        if (mDialog.isShowing())
                            Config.closeDialog(getActivity(), mDialog);

                        Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                        Config.logV("Response--code-------------------------" + response.code());
                        // Config.logV("Response--BODY------Search-------------------" + new Gson().toJson(response));
                        mGLobalSearch.clear();
                        if (response.code() == 200) {

                            SearchModel search = null;
                            for (int i = 0; i < response.body().getGlobalSearchLabels().size(); i++) {
                                int mGlobalSize = response.body().getGlobalSearchLabels().size();
                                mGLobalSearch.clear();
                                for (int k = 0; k < mGlobalSize; k++) {
                                    search = new SearchModel();
                                    search.setName(response.body().getGlobalSearchLabels().get(k).getName());
                                    search.setDisplayname(response.body().getGlobalSearchLabels().get(k).getDisplayname());
                                    search.setQuery(response.body().getGlobalSearchLabels().get(k).getQuery());
                                    mGLobalSearch.add(search);
                                   // Config.logV("Query*****111********" + response.body().getGlobalSearchLabels().get(k).getQuery());
                                }
                            }

                            Config.logV("Globa lSearch Size-------------" + mGLobalSearch.size());




                            mSpecializationDomain.clear();
                            mSubDomain.clear();

                            for (int i = 0; i < response.body().getSectorLevelLabels().size(); i++) {
                                int mSectorSize = response.body().getSectorLevelLabels().get(i).getSubSectorLevelLabels().size();




                                for (int k = 0; k < mSectorSize; k++) {
                                    ArrayList<SearchModel> getSubdomainSectorLevel = response.body().getSectorLevelLabels().get(i).getSubSectorLevelLabels();

                                    // Config.logV("Sector-------------" + sector);
                                    search = new SearchModel();
                                    search.setName(getSubdomainSectorLevel.get(k).getName());
                                    search.setDisplayname(getSubdomainSectorLevel.get(k).getDisplayname());
                                    search.setQuery(getSubdomainSectorLevel.get(k).getQuery());
                                    search.setSector(response.body().getSectorLevelLabels().get(i).getName());

                                    mSubDomain.add(search);
                                    //mSpecializationDomain.addAll(getSubdomainSectorLevel.get(k).getSpecializationLabels());
                                    for(int j=0;j<getSubdomainSectorLevel.get(k).getSpecializationLabels().size();j++){
                                        search = new SearchModel();
                                        search.setName(getSubdomainSectorLevel.get(k).getSpecializationLabels().get(j).getName());
                                        search.setDisplayname(getSubdomainSectorLevel.get(k).getSpecializationLabels().get(j).getDisplayname());
                                        search.setQuery(getSubdomainSectorLevel.get(k).getSpecializationLabels().get(j).getQuery());
                                        search.setSector(response.body().getSectorLevelLabels().get(i).getName());
                                        mSpecializationDomain.add(search);
                                    }



                                }
                            }
                           /* ArrayAdapter<Domain_Spinner> adapter = new ArrayAdapter<Domain_Spinner>(getActivity(), R.layout.spinner_item, domainList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            mSpinnerDomain.setAdapter(adapter);*/


                           APiGetDomain();
                            Config.logV("GLobal Search Size-------------" + mGLobalSearch.size());
                            Config.logV("SUBDOAMIN Search Size-------------" + mSubDomain.size());
                            Config.logV("SPECIALIZATION Search Size-------------" + mSpecializationDomain.size());




                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<SearchModel> call, Throwable t) {
                    // Log error here since request failed
                    Config.logV("Fail---------------" + t.toString());
                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                }
            });


        }
    }
    private void APiGetDomain() {
        {


            final ApiInterface apiService =
                    ApiClient.getClient(getActivity()).create(ApiInterface.class);

            final Dialog mDialog = Config.getProgressDialog(getActivity(), getActivity().getResources().getString(R.string.dialog_log_in));
            mDialog.show();

            Call<ArrayList<Domain_Spinner>> call = apiService.getAllDomains();


            call.enqueue(new Callback<ArrayList<Domain_Spinner>>() {
                @Override
                public void onResponse(Call<ArrayList<Domain_Spinner>> call, Response<ArrayList<Domain_Spinner>> response) {

                    try {

                        if (mDialog.isShowing())
                            Config.closeDialog(getActivity(), mDialog);

                        Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                        Config.logV("Response--code-------------------------" + response.code());


                        if (response.code() == 200) {

                            Config.logV("Response--Array size-------------------------" + response.body().size());

                            if (response.body().size() > 0) {
                                domainList.clear();
                                domainList.add(new Domain_Spinner("All", "All"));
                                // domainModel=new Domain_Spinner();
                                for (int i = 0; i < response.body().size(); i++) {

                                    domainList.add(new Domain_Spinner(response.body().get(i).getDisplayName(), response.body().get(i).getDomain()));


                                }
                                Config.logV("response.body()) SUBDOMAIN SIZE" + mSubDomain.size());
                                ArrayAdapter<Domain_Spinner> adapter = new ArrayAdapter<Domain_Spinner>(getActivity(), R.layout.spinner_item, domainList);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                mSpinnerDomain.setAdapter(adapter);

                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<ArrayList<Domain_Spinner>> call, Throwable t) {
                    // Log error here since request failed
                    Config.logV("Fail---------------" + t.toString());
                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                }
            });


        }
    }
    public static int dpToPx(int dp) {
        float density = mContext.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }




    List<ActiveCheckIn> MActiveList = new ArrayList<>();
    ActiveCheckInAdapter activeAdapter;


    private void ApiAWSearchDomain() {


        final ApiInterface apiService =
                ApiClient.getClient(getActivity()).create(ApiInterface.class);


        Call<ResponseBody> call = apiService.getSearchDomain();


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {


                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {


                        AWS_URL = response.body().string();
                        Config.logV("Response--AWS URL-------------------------" + AWS_URL);
                        SharedPreference.getInstance(mContext).setValue("AWS_URL", AWS_URL);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());


            }
        });


    }

    public void ApiActiveCheckIn() {

        Config.logV("Active Checkin------%%%%%%%%%%%%%%%%%%%%------------" + activeCheckin);
        final ApiInterface apiService =
                ApiClient.getClient(getActivity()).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(getActivity(), getActivity().getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Call<ArrayList<ActiveCheckIn>> call = apiService.getActiveCheckIn();


        call.enqueue(new Callback<ArrayList<ActiveCheckIn>>() {
            @Override
            public void onResponse(Call<ArrayList<ActiveCheckIn>> call, Response<ArrayList<ActiveCheckIn>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    activeCheckin = false;
                    if (response.code() == 200) {


                        Config.logV("Response--Array size--Active-----------------------" + response.body().size());

                        if (response.body().size() > 0) {
                            txt_sorry.setVisibility(View.GONE);
                            MActiveList.clear();
                            for (int i = 0; i < response.body().size(); i++) {

                                MActiveList = response.body();
                            }
                            Config.logV("MActiveList----------------------" + MActiveList.size());
                            if (MActiveList.size() > 0) {
                                db = new DatabaseHandler(mContext);
                                db.DeleteCheckin();
                                db.insertCheckinInfo(MActiveList);
                                MActiveList.clear();
                                MActiveList = db.getAllCheckinList();
                                Config.logV("MActiveList---DB-------------------" + MActiveList.size());
                                tv_activechkin.setText("Active Check-ins " + "(" + MActiveList.size() + ")");
                                LActiveCheckin.setVisibility(View.VISIBLE);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                                mRecycleActive.setLayoutManager(mLayoutManager);
                                activeAdapter = new ActiveCheckInAdapter(MActiveList, mContext, getActivity(), active, mInterface);
                                mRecycleActive.setAdapter(activeAdapter);
                                activeAdapter.notifyDataSetChanged();
                            } else {
                                LActiveCheckin.setVisibility(View.VISIBLE);
                            }
                        } else {
                            tv_activechkin.setText("Active Check-ins ");
                            txt_sorry.setVisibility(View.VISIBLE);
                            LActiveCheckin.setVisibility(View.VISIBLE);
                        }
                    } else {
                        // Toast.makeText(mContext,response.errorBody().string(),Toast.LENGTH_SHORT).show();

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<ActiveCheckIn>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }

    public LanLong getLocationNearBy(double lant, double longt) {

        double distance = 60;/*;DISTANCE_AREA: 5, // in Km*/

        double distInDegree = distance / 111;
        double upperLeftLat = lant - distInDegree;
        double upperLeftLon = longt + distInDegree;
        double lowerRightLat = lant + distInDegree;
        double lowerRightLon = longt - distInDegree;
        /*double locationRange = '[\'' + lowerRightLat + ',' + lowerRightLon + '\',\'' + upperLeftLat + ',' + upperLeftLon + '\']';
        double retarr = {'locationRange':locationRange, 'upperLeftLat':upperLeftLat, 'upperLeftLon':
        upperLeftLon, 'lowerRightLat':lowerRightLat, 'lowerRightLon':lowerRightLon};*/

        LanLong lan = new LanLong();
        lan.setUpperLeftLat(upperLeftLat);
        lan.setUpperLeftLon(upperLeftLon);
        lan.setLowerRightLat(lowerRightLat);
        lan.setLowerRightLon(lowerRightLon);

        return lan;

    }

    public void FunPopularSearch(String sector, String category, String name) {
        String mSector;
        LanLong Lanlong = getLocationNearBy(latitude, longitude);
        double upperLeftLat = Lanlong.getUpperLeftLat();
        double upperLeftLon = Lanlong.getUpperLeftLon();
        double lowerRightLat = Lanlong.getLowerRightLat();
        double lowerRightLon = Lanlong.getLowerRightLon();
        String locationRange = "['" + lowerRightLat + "," + lowerRightLon + "','" + upperLeftLat + "," + upperLeftLon + "']";
        String sub = "";
        String querycreate = "";
        mSector = sector;
        Config.logV("Sector--------------" + mSector);

        if (category.equalsIgnoreCase("Specializations")) {
            sub = "specialization:" + "'" + name + "'";
            querycreate = "sector:" + "'" + mSector + "'" + " " + sub;
        }
        if (category.equalsIgnoreCase("Sub Domain")) {

            sub = "sub_sector:" + "'" + name + "'";
            querycreate = "sector:" + "'" + mSector + "'" + " " + sub;
        }

        if (category.equalsIgnoreCase("Suggested Search")) {

            String requiredString = sector.substring(sector.indexOf("]") + 1, sector.indexOf(")"));
            Config.logV("Second---------" + requiredString);
            querycreate = requiredString;
        }

        if (category.equalsIgnoreCase("Business Name as")) {


            if (mSector.equalsIgnoreCase("All")) {
                querycreate = "title:" + "'" + name + "'";
            } else {
                sub = "title:" + "'" + name + "'";
                querycreate = "sector:" + "'" + mSector + "'" + " " + sub;
            }
        }


        Config.logV("Query-----------" + querycreate);


        // String pass = "haversin(11.751416900900901,75.3701820990991, location1.latitude, location1.longitude)";


        String pass = "haversin(" + latitude + "," + longitude + ", location1.latitude, location1.longitude)";

        Bundle bundle = new Bundle();
       /* bundle.putString("query", "(and location1:['11.751416900900901,75.3701820990991','9.9496150990991,77.171983900900'] " + querycreate + ")");
        bundle.putString("url", pass);*/


        //VALID QUERY PASS
        bundle.putString("query", "(and location1:" + locationRange + querycreate + ")");
        bundle.putString("url", pass);

        Config.logV("Popular Text__________@@@Del111e");
        mSearchView.setQuery("", false);
        SearchListFragment pfFragment = new SearchListFragment();

        bundle.putString("locName", mCurrentLoc.getText().toString());
        bundle.putString("latitude", String.valueOf(latitude));
        bundle.putString("longitude", String.valueOf(longitude));
        bundle.putString("spinnervalue", spinnerTxtPass);

        Config.logV("Popular Text_______$$$$_______" + mPopularSearchtxt);
        bundle.putString("searchtxt", mPopularSearchtxt);
        pfFragment.setArguments(bundle);


        FragmentTransaction transaction = this.getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
        // Store the Fragment in stack
        transaction.addToBackStack(null);
        transaction.replace(R.id.mainlayout, pfFragment).commit();
        searchSrcTextView.clearFocus();
        mSearchView.clearFocus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();
        String s_currentLoc = SharedPreference.getInstance(getActivity()).getStringValue("current_loc", "");
        Config.logV("Current Location---------------------------" + s_currentLoc);
        if (!s_currentLoc.equalsIgnoreCase("no")) {

            if (googleApiClient != null) {
                googleApiClient.connect();
                Config.logV("Google ONREUME");

            } else {
                setUpGClient();
            }

        }
        if (Config.isOnline(getActivity())) {
            Config.logV("Active Checkin------@@@@@@@@@@@--------##########-----" + activeCheckin);
            if (!activeCheckin) {
                activeCheckin = true;
                ApiActiveCheckIn();
            }

        } else {
            MActiveList.clear();
            db = new DatabaseHandler(getActivity());
            MActiveList = db.getAllCheckinList();
            Config.logV("Mactivie" + MActiveList.size());
            if (MActiveList.size() > 0) {
                tv_activechkin.setText("Active Check-ins " + "(" + String.valueOf(MActiveList.size()) + ")");
                LActiveCheckin.setVisibility(View.VISIBLE);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                mRecycleActive.setLayoutManager(mLayoutManager);
                activeAdapter = new ActiveCheckInAdapter(MActiveList, mContext, getActivity(), active, mInterface);
                mRecycleActive.setAdapter(activeAdapter);
                activeAdapter.notifyDataSetChanged();
            } else {

                tv_activechkin.setText("Active Check-ins ");
                txt_sorry.setVisibility(View.VISIBLE);
                LActiveCheckin.setVisibility(View.VISIBLE);
            }

        }


    }


    @Override
    public void onConnected(Bundle bundle) {
        checkPermissions();
        Config.logV("CONNECTED ");
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Do whatever you need
        //You can display a message here
    }




    /*@Override
    public void onPause() {
        super.onPause();
        String s_currentLoc=SharedPreference.getInstance(getActivity()).getStringValue("current_loc","");
        if(!s_currentLoc.equalsIgnoreCase("no")) {
            googleApiClient.stopAutoManage(getActivity());
            googleApiClient.disconnect();
        }
    }*/

    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        //SharedPreference.getInstance(mContext).setValue("map_intial","true");
        googleApiClient.connect();
        Config.logV("Google Update Location SetUpFConnected---------------------------");
    }

    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;
        Config.logV("Google Update Location Changed Connected---------------------------" + location);
        if (mylocation != null) {
            latitude = mylocation.getLatitude();
            longitude = mylocation.getLongitude();
            Config.logV("Update Location Changed Connected---------------------11111------" + location.getLatitude());
            Config.logV("Latitude-------------" + latitude);

            try {
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                mCurrentLoc.setVisibility(View.VISIBLE);
                mCurrentLoc.setText(addresses.get(0).getLocality());

                SearchListFragment.UpdateLocationSearch(String.valueOf(latitude), String.valueOf(longitude), addresses.get(0).getLocality());
                //   Config.logV("Latitude-----11111--------"+addresses.get(0).getAddressLine(0));
            } catch (Exception e) {

            }
            //Or Do whatever you want with your location
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //You can display a message here
    }

    private void getMyLocation() {
        if (googleApiClient != null) {

            if (googleApiClient.isConnected()) {

                Config.logV("Google api connected granted");
                int permissionLocation = ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {

                    Config.logV("Google api connected granted@2@@@");
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(0);        // 10 seconds, in milliseconds
                    locationRequest.setFastestInterval(0); // 1 second, in milliseconds

                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest, this);
                    //DefaultLocation();

                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(getActivity(),
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                    }

                                    Config.logV("Google apiClient LocationSettingsStatusCodes.SUCCESS");
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically

                                        Config.logV("Google Ask to turn on GPS automatically");
                                        /*status.startResolutionForResult(getActivity(),
                                                REQUEST_CHECK_SETTINGS_GPS);*/
                                        startIntentSenderForResult(status.getResolution().getIntentSender(), REQUEST_CHECK_SETTINGS_GPS, null, 0, 0, 0, null);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                        e.printStackTrace();
                                    }


                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    Config.logV("Google Location settings are not satisfied");
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
        Config.logV("GPS ON Google ##################");
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                Config.logV("GPS ON Google resultCode ##################" + resultCode);
                switch (resultCode) {

                    case RESULT_OK:
                        Config.logV("GPS ON Google");
                        getMyLocation();
                        break;
                    case RESULT_CANCELED:
                        Config.logV("GPS ON Google Cancelled");
                        //getActivity().finish();
                        DefaultLocation();
                        break;


                }

                break;
        }
       /* if (requestCode == REQUEST_CHECK_SETTINGS_GPS && resultCode == RESULT_OK && data != null) {
            Config.logV("GPS ON Google");
            getMyLocation();
        } else if (requestCode == REQUEST_CHECK_SETTINGS_GPS && resultCode == RESULT_CANCELED) {
            Config.logV("GPS ON Google Cancelled");
            //getActivity().finish();
            DefaultLocation();
        } else if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
            Config.logV("PayU Monry");

            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);
            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);

            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {

                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    showAlert("Payment Successful");

                } else if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.CANCELLED)) {
                    showAlert("Payment Cancelled");
                } else if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.FAILED)) {
                    showAlert("Payment Failed");
                }

            } else if (resultModel != null && resultModel.getError() != null) {
                Toast.makeText(getActivity(), "Error check log", Toast.LENGTH_SHORT).show();
            } else {
                // Toast.makeText(this, "Both objects are null", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_CANCELED) {
            showAlert("Payment Cancelled");
        }*/


    }

    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            Config.logV("Google Not Granted" + permissionLocation);
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
               /*requestPermissions(getActivity(),
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);*/
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ID_MULTIPLE_PERMISSIONS);

                Config.logV("GoogleNot Granted" + permissionLocation);
            }

        } else {
            getMyLocation();
        }
    }


    public void DefaultLocation() {
        Config.logV("Google DEFAULT LOCATION" + mCurrentLoc.getText().toString());
        //if (mCurrentLoc.getText().toString().equalsIgnoreCase("Locating...")) {
        latitude = 12.971599;
        longitude = 77.594563;
        Config.logV("Not Google DEFAULT LOCATION @@@ YES");
        try {
            if (Config.isOnline(getActivity())) {
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                mCurrentLoc.setVisibility(View.VISIBLE);
                mCurrentLoc.setText(addresses.get(0).getLocality());
                Config.logV("Google DEFAULT LOCATION @@@" + addresses.get(0).getLocality());
            } else {
                mCurrentLoc.setVisibility(View.VISIBLE);
                mCurrentLoc.setText("Bengaluru");
            }

            //   Config.logV("Latitude-----11111--------"+addresses.get(0).getAddressLine(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*} else {
            Config.logV("Not Google DEFAULT LOCATION @@@");
        }*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            Config.logV("Google Granted@@@@@@@%%%%%%%%%%%%%%%%%%%%%%%");
            getMyLocation();
        } else {
            DefaultLocation();
        }

    }


    public static Fragment getHomeFragment() {
        return home;
    }

    public void QuerySubmitCLick(String query) {

        mSearchView.setQuery("", false);

        LanLong Lanlong = getLocationNearBy(latitude, longitude);
        double upperLeftLat = Lanlong.getUpperLeftLat();
        double upperLeftLon = Lanlong.getUpperLeftLon();
        double lowerRightLat = Lanlong.getLowerRightLat();
        double lowerRightLon = Lanlong.getLowerRightLon();
        String locationRange = "['" + lowerRightLat + "," + lowerRightLon + "','" + upperLeftLat + "," + upperLeftLon + "']";

        String querycreate = "";
        if (!mDomainSpinner.equalsIgnoreCase("All")) {
            querycreate = "(phrase " + "'" + query + "') sector :'" + mDomainSpinner + "'";
        } else {
            querycreate = "(phrase " + "'" + query + "')";
        }

        //  Config.logV("Query-----------" + querycreate);


        // String pass = "haversin(11.751416900900901,75.3701820990991, location1.latitude, location1.longitude)";


        String pass = "haversin(" + latitude + "," + longitude + ", location1.latitude, location1.longitude)";

        Bundle bundle = new Bundle();


      /*  bundle.putString("query", "(and location1:['11.751416900900901,75.3701820990991','9.9496150990991,77.171983900900'] " + querycreate + ")");
        bundle.putString("url", pass);*/

        //VALID QUERY PASS
        bundle.putString("query", "(and location1:" + locationRange + querycreate + ")");
        bundle.putString("url", pass);
        SearchListFragment pfFragment = new SearchListFragment();

        bundle.putString("locName", mCurrentLoc.getText().toString());
        bundle.putString("latitude", String.valueOf(latitude));
        bundle.putString("longitude", String.valueOf(longitude));
        bundle.putString("spinnervalue", spinnerTxtPass);
        Config.logV("SEARCH TXT 99999" + mSearchtxt);
        if(!query.equalsIgnoreCase("")) {
            bundle.putString("searchtxt", mSearchtxt);
        }else{
            bundle.putString("searchtxt", "");
        }
        pfFragment.setArguments(bundle);


        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
        // Store the Fragment in stack
        transaction.addToBackStack(null);
        transaction.replace(R.id.mainlayout, pfFragment).commit();
        searchSrcTextView.clearFocus();
        mSearchView.clearFocus();
    }


    public void SpinnerItemCLick() {

        mSearchView.setQuery("", false);

        LanLong Lanlong = getLocationNearBy(latitude, longitude);
        double upperLeftLat = Lanlong.getUpperLeftLat();
        double upperLeftLon = Lanlong.getUpperLeftLon();
        double lowerRightLat = Lanlong.getLowerRightLat();
        double lowerRightLon = Lanlong.getLowerRightLon();
        String locationRange = "['" + lowerRightLat + "," + lowerRightLon + "','" + upperLeftLat + "," + upperLeftLon + "']";

        String querycreate = "";
        if (!mDomainSpinner.equalsIgnoreCase("All")) {
            querycreate = "sector :'" + mDomainSpinner + "'";
        } else {
            querycreate = " ";
        }

        //  Config.logV("Query-----------" + querycreate);


        // String pass = "haversin(11.751416900900901,75.3701820990991, location1.latitude, location1.longitude)";


        String pass = "haversin(" + latitude + "," + longitude + ", location1.latitude, location1.longitude)";

        Bundle bundle = new Bundle();


      /*  bundle.putString("query", "(and location1:['11.751416900900901,75.3701820990991','9.9496150990991,77.171983900900'] " + querycreate + ")");
        bundle.putString("url", pass);*/

        //VALID QUERY PASS
        bundle.putString("query", "(and location1:" + locationRange + querycreate + ")");
        bundle.putString("url", pass);
        SearchListFragment pfFragment = new SearchListFragment();

        bundle.putString("locName", mCurrentLoc.getText().toString());
        bundle.putString("latitude", String.valueOf(latitude));
        bundle.putString("longitude", String.valueOf(longitude));
        bundle.putString("spinnervalue", spinnerTxtPass);
        Config.logV("SEARCH TXT 99999" + mSearchtxt);
        bundle.putString("searchtxt", mSearchtxt);
        pfFragment.setArguments(bundle);


        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
        // Store the Fragment in stack
        transaction.addToBackStack(null);
        transaction.replace(R.id.mainlayout, pfFragment).commit();
        searchSrcTextView.clearFocus();
        mSearchView.clearFocus();
    }


    public static boolean UpdateLocation(Double mlatitude, Double mlongitude, String locNme) {
        Config.logV("UpdateLocation 3333333333----" + mlatitude + " " + mlongitude + "" + locNme);
        try {
            latitude = mlatitude;
            longitude = mlongitude;
            mlocName = locNme;


            SharedPreference.getInstance(mContext).setValue("lat", latitude);
            SharedPreference.getInstance(mContext).setValue("longitu", longitude);
            SharedPreference.getInstance(mContext).setValue("locnme", mlocName);


            SharedPreference.getInstance(mContext).setValue("locnme", mlocName);
            Config.logV("UpdateLocation 4444----" + mlocName);
            mCurrentLoc.setVisibility(View.VISIBLE);

            if (mlocName.equalsIgnoreCase("")) {
                Config.logV("UpdateLocation banglore");
                latitude = 12.971599;
                longitude = 77.594563;
                try {
                    Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    mCurrentLoc.setVisibility(View.VISIBLE);
                    mCurrentLoc.setText(addresses.get(0).getLocality());
                    //   Config.logV("Latitude-----11111--------"+addresses.get(0).getAddressLine(0));
                } catch (Exception e) {

                }
            } else {
                mCurrentLoc.setText(mlocName);
            }


            //  SharedPreference.getInstance(mContext).setValue("map_intial","false");
        } catch (Exception e) {

        }

        return true;
    }

    @Override
    public void onStop() {
        super.onStop();
        Config.logV("OnStop---------------------------------");
        String s_currentLoc = SharedPreference.getInstance(getActivity()).getStringValue("current_loc", "");
        if (!s_currentLoc.equalsIgnoreCase("no")) {
            if (googleApiClient != null) {
                googleApiClient.stopAutoManage(getActivity());
                googleApiClient.disconnect();
            }
        }
    }


    @Override
    public void onMethodActiveCallback(String value) {
        Bundle bundle = new Bundle();

        SearchDetailViewFragment pfFragment = new SearchDetailViewFragment();

        bundle.putString("uniqueID", value);
        pfFragment.setArguments(bundle);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        // Store the Fragment in stack
        transaction.addToBackStack(null);
        transaction.replace(R.id.mainlayout, pfFragment).commit();
    }

    @Override
    public void onMethodActiveBillIconCallback(String payStatus, String value, String provider, String accountID) {
        Intent iBill = new Intent(mContext, BillActivity.class);
        iBill.putExtra("ynwUUID", value);
        iBill.putExtra("provider", provider);
        iBill.putExtra("accountID", accountID);
        iBill.putExtra("payStatus", payStatus);
        startActivity(iBill);

    }




    @Override
    public void onMethodActivePayIconCallback(String payStatus, final String ynwUUID, String provider, final String accountID, final double amountDue) {
       // APIPayment(accountID, ynwUUID, amountDue);
        Intent i=new Intent(mContext, PaymentActivity.class);
        i.putExtra("ynwUUID",ynwUUID);
        i.putExtra("accountID",accountID);
        i.putExtra("amountDue",amountDue);
        startActivity(i);
    }

    /*public void PaymentFunc(final String ynwUUID, final String accountID, final double amountDue) {
        try {

            final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
            dialog.setContentView(R.layout.prepayment);
            dialog.show();

            Button btn_paytm = (Button) dialog.findViewById(R.id.btn_paytm);
            Button btn_payu = (Button) dialog.findViewById(R.id.btn_payu);
            if (showPaytmWallet) {
                btn_paytm.setVisibility(View.VISIBLE);
            } else {
                btn_paytm.setVisibility(View.GONE);
            }
            if (showPayU) {
                btn_payu.setVisibility(View.VISIBLE);
            } else {
                btn_payu.setVisibility(View.GONE);
            }

            final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
            TextView txtamt = (TextView) dialog.findViewById(R.id.txtamount);
            txtamt.setText("Rs." + String.valueOf(amountDue));
            Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/Montserrat_Bold.otf");
            txtamt.setTypeface(tyface1);
            btn_payu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new PaymentGateway(mContext, mActivity).ApiGenerateHashTest(ynwUUID, String.valueOf(amountDue), accountID, "dashboard");
                    dialog.dismiss();
                    // payment.ApiGenerateHash(ynwUUID, sAmountPay, accountID);
                       *//*
                        dialog.dismiss();*//*

                }
            });

            btn_paytm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PaytmPayment payment = new PaytmPayment(mContext);
                    // payment.generateCheckSum(sAmountPay);
                    payment.ApiGenerateHashPaytm(ynwUUID, String.valueOf(amountDue), accountID, mContext, mActivity, "home");
                    //  payment.ApiGenerateHashPaytm(ynwUUID, sAmountPay, accountID,mCOntext,mActivity);
                    dialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    ArrayList<RefinedFilters> commonFilterList = new ArrayList<>();

    private void ApiFilters() {


        ApiInterface apiService =
                ApiClient.getClient(getActivity()).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(getActivity(), getActivity().getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        Call<RefinedFilters> call = apiService.getFilters();


        call.enqueue(new Callback<RefinedFilters>() {
            @Override
            public void onResponse(Call<RefinedFilters> call, Response<RefinedFilters> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------Filters-------------------" + response.code());
                    if (response.code() == 200) {
                        Config.logV("Response----------------");

                        commonFilterList.clear();
                        commonFilterList = response.body().getCommonFilters();
                        Config.logV("Common Filters----------------" + commonFilterList.size());


                    } else {

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<RefinedFilters> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }
        });

    }


    /*public static void launchPaymentFlow(String amount, CheckSumModelTest checksumModel) {
        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();

        // payUmoneyConfig.setPayUmoneyActivityTitle("Buy" + getResources().getString(R.string.nike_power_run));
        payUmoneyConfig.setDoneButtonText("Pay Rs." + amount);


        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
        builder.setAmount(convertStringToDouble(amount))
                .setTxnId(checksumModel.getTxnId())
                .setPhone(checksumModel.getMobile())
                // .setProductName(checksumModel.getProductinfo().getPaymentParts().get(0).toString())
                .setProductName(checksumModel.getProductinfo())
                .setFirstName(checksumModel.getFirstName())
                .setEmail(checksumModel.getEmail())
                .setsUrl(checksumModel.getFirstName())
                .setfUrl(checksumModel.getFurl())
                .setUdf1("")
                .setUdf2("")
                .setUdf3("")
                .setUdf4("")
                .setUdf5("")
                .setUdf6("")
                .setUdf7("")
                .setUdf8("")
                .setUdf9("")
                .setUdf10("")
                .setIsDebug(true)
                .setKey(checksumModel.getKey())
                .setMerchantId(checksumModel.getMerchantID());

        try {
            PayUmoneySdkInitializer.PaymentParam mPaymentParams = builder.build();
            if (checksumModel.getChecksum().isEmpty() || checksumModel.getChecksum().equals("")) {
                //  Toast.makeText(mCOntext, "Could not generate hash", Toast.LENGTH_SHORT).show();
            } else {


                mPaymentParams.setMerchantHash(checksumModel.getChecksum());
                Config.logV("Checksum id---22222222222222--------" + mPaymentParams);

                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, mActivity, R.style.PayUMoney, true);
            }
        } catch (Exception e) {
            Config.logV("e.getMessage()------" + e.getMessage());
            Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_LONG).show();

            // mTxvBuy.setEnabled(true);
        }
    }*/







}
