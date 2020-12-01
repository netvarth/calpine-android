package com.jaldeeinc.jaldee.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

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
import com.google.gson.JsonArray;
import com.jaldeeinc.jaldee.Interface.ISelectedPopularSearch;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.SearchLocationActivity;
import com.jaldeeinc.jaldee.activities.SearchResultsActivity;
import com.jaldeeinc.jaldee.adapter.PopularSearchAdapter;
import com.jaldeeinc.jaldee.adapter.SearchListAdpter;
import com.jaldeeinc.jaldee.adapter.SearchResultsAdapter;
import com.jaldeeinc.jaldee.callback.AdapterCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTypefaceSpan;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.Domain_Spinner;
import com.jaldeeinc.jaldee.model.FilterChips;
import com.jaldeeinc.jaldee.model.LanLong;
import com.jaldeeinc.jaldee.model.ListCell;
import com.jaldeeinc.jaldee.model.SearchListModel;
import com.jaldeeinc.jaldee.model.SearchModel;
import com.jaldeeinc.jaldee.model.WorkingModel;
import com.jaldeeinc.jaldee.response.QueueList;
import com.jaldeeinc.jaldee.response.ScheduleList;
import com.jaldeeinc.jaldee.response.SearchAWsResponse;
import com.jaldeeinc.jaldee.response.SearchService;
import com.jaldeeinc.jaldee.utils.EmptySubmitSearchView;
import com.jaldeeinc.jaldee.utils.PaginationScrollListener;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class HomeSearchFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener, ISelectedPopularSearch, AdapterCallback {


    private static final int REQUEST_GET_DATA_FROM_SOME_ACTIVITY = 1;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static Context mContext;
    ArrayList<Domain_Spinner> domainList = new ArrayList<>();
    Spinner mSpinnerDomain;
    EmptySubmitSearchView mSearchView;
    SearchView.SearchAutoComplete searchSrcTextView;
    SearchListAdpter listadapter;
    String mDomainSpinner;
    String spinnerTxtPass;
    DatabaseHandler db;
    static double latitude;
    static double longitude;
    ArrayList<SearchModel> mPopularSearchList = new ArrayList<>();
    ArrayList<SearchModel> mPopular_SubSearchList = new ArrayList<>();
    ArrayList<SearchModel> mPopular_AllSearchList = new ArrayList<>();
    ArrayList<SearchModel> mGLobalSearch = new ArrayList<>();
    ArrayList<SearchModel> mSubDomainSubSearch = new ArrayList<>();
    ArrayList<SearchModel> mSubDomain = new ArrayList<>();
    ArrayList<SearchModel> mSpecializationDomain = new ArrayList<>();
    ArrayList<SearchModel> mSpecializationDomainSearch = new ArrayList<>();
    ArrayList<ListCell> items;
    String mSearchtxt;
    String mPopularSearchtxt;
    static Fragment home;
    static String mtyp = null;
    String AWS_URL = "";
    String query1 = "";
    static String mlocName = "";
    ProgressBar progressBar;
    String subdomainquery, subdomainName;
    private boolean fromBusinessId = false;
    private boolean fromBusinessName = false;
    static CustomTextViewMedium tvLocation;
    LinearLayout llPopularSearch;
    private Location mylocation;
    private GoogleApiClient googleApiClient;
    public final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    static double distance;
    private RecyclerView rvPopularSearch;
    private GridLayoutManager gridLayoutManager;
    private PopularSearchAdapter popularSearchAdapter;
    boolean userIsInteracting = false;
    private ISelectedPopularSearch iSelectedPopularSearch;
    private RecyclerView rvNearByResults;
    private SearchResultsAdapter searchResultsAdapter;
    private int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = 0;
    private int currentPage = PAGE_START;
    int total_foundcount = 0;
    private LinearLayout llNoResults;
    private CustomTextViewMedium tvNoResults;
    AdapterCallback mInterface;
    List<QueueList> mQueueList = new ArrayList<>();
    String uniqueID;
    private LinearLayoutManager linearLayoutManager;
    List<SearchAWsResponse> mSearchResp = new ArrayList<>();
    ArrayList<ScheduleList> mScheduleList = new ArrayList<>();
    List<SearchListModel> mSearchListModel = new ArrayList<>();
    String passformula = "";
    int pastVisiblesItems, visibleItemCount, totalItemCount;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeSearchFragment() {
        // Required empty public constructor
    }


    public static HomeSearchFragment newInstance(String param1, String param2) {
        HomeSearchFragment fragment = new HomeSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View row = inflater.inflate(R.layout.fragment_home_search, container, false);
        mContext = getActivity();
        home = getParentFragment();
        iSelectedPopularSearch = (ISelectedPopularSearch) this;
        mInterface = (AdapterCallback) this;

        initializations(row);
        ApiAWSearchDomain();

        String s_currentLoc = SharedPreference.getInstance(getActivity()).getStringValue("current_loc", "");
        Config.logV("UpdateLocation noooooooooooooooooo" + s_currentLoc);
        if (s_currentLoc.equalsIgnoreCase("yes")) {

            setUpGClient();

        } else if (s_currentLoc.equalsIgnoreCase("no")) {

            try {

                latitude = Double.valueOf(SharedPreference.getInstance(mContext).getStringValue("lat", ""));
                longitude = Double.valueOf(SharedPreference.getInstance(mContext).getStringValue("longitu", ""));
                mlocName = SharedPreference.getInstance(mContext).getStringValue("locnme", "");
                mtyp = SharedPreference.getInstance(mContext).getStringValue("typ", "");

                UpdateLocation(latitude, longitude, mlocName, mtyp);
            } catch (NumberFormatException e) {

                latitude = 12.971599;
                longitude = 77.594563;
                e.printStackTrace();
            }


        } else {
            Config.logV("UpdateLocation banglore");
            latitude = 12.971599;
            longitude = 77.594563;
            try {
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                tvLocation.setVisibility(View.VISIBLE);
                tvLocation.setText(addresses.get(0).getLocality());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (tvLocation.getText().equals("")) {
            DefaultLocation();
        }

        isLastPage = false;
        isLoading = false;
        PAGE_START = 0;
        total_foundcount = 0;
        TOTAL_PAGES = 0;
        currentPage = PAGE_START;


        if (Config.isOnline(mContext)) {
            APiSearchList();
        } else {
            DatabaseHandler db = new DatabaseHandler(mContext);

            domainList.clear();
            domainList = db.getDomain();
            ArrayAdapter<Domain_Spinner> adapter = new ArrayAdapter<Domain_Spinner>(mContext, R.layout.spinner_item, domainList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinnerDomain.setAdapter(adapter);
            mSpinnerDomain.setPopupBackgroundResource(R.color.white);
        }


        progressBar = (ProgressBar) row.findViewById(R.id.main_progress);
        searchResultsAdapter = new SearchResultsAdapter(getActivity(), mContext, mInterface, uniqueID, mQueueList);
        searchResultsAdapter.clear();
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rvNearByResults.setLayoutManager(linearLayoutManager);
        rvNearByResults.setItemAnimator(new DefaultItemAnimator());
        rvNearByResults.setAdapter(searchResultsAdapter);


        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent iLoc = new Intent(mContext, SearchLocationActivity.class);
                iLoc.putExtra("from", "dashboard");
                startActivityForResult(iLoc, REQUEST_GET_DATA_FROM_SOME_ACTIVITY);

            }
        });

        ArrayAdapter<Domain_Spinner> adapter = new ArrayAdapter<Domain_Spinner>(mContext, R.layout.spinner_item, domainList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerDomain.setPopupBackgroundResource(R.color.white);

        mSpinnerDomain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                searchSrcTextView.setText("");
                mDomainSpinner = ((Domain_Spinner) mSpinnerDomain.getSelectedItem()).getDomain();

                spinnerTxtPass = ((Domain_Spinner) mSpinnerDomain.getSelectedItem()).getDisplayName();

                if (userIsInteracting) {
                    SpinnerItemCLick();
                }
                userIsInteracting = true;

                mPopularSearchList.clear();
                if (mDomainSpinner.equalsIgnoreCase("All")) {
                    db = new DatabaseHandler(mContext);
                    mPopular_AllSearchList = db.getPopularSearch("All");
                    mPopularSearchList = mPopular_AllSearchList;
                    Config.logV("getPopularSearch Search Size-------------" + mPopularSearchList.size());
                    gridLayoutManager = new GridLayoutManager(mContext, 2);
                    rvPopularSearch.setLayoutManager(gridLayoutManager);
                    popularSearchAdapter = new PopularSearchAdapter(mPopularSearchList, mContext, false, iSelectedPopularSearch);
                    rvPopularSearch.setAdapter(popularSearchAdapter);

                } else {
                    mPopular_SubSearchList.clear();
                    ArrayList<SearchModel> dbSubDomain = new ArrayList<>();
                    dbSubDomain.clear();
                    dbSubDomain = db.getPopularSearch(mDomainSpinner);
                    Config.logV("getPopularSearch Search Size-------------" + dbSubDomain.size());
                    for (int i = 0; i < dbSubDomain.size(); i++) {

                        if (dbSubDomain.get(i).getSector().toLowerCase().trim().equalsIgnoreCase(mDomainSpinner.toLowerCase().trim())) {


                            SearchModel search = new SearchModel();
                            search.setName(dbSubDomain.get(i).getName());
                            search.setQuery(dbSubDomain.get(i).getQuery());
                            search.setSector(dbSubDomain.get(i).getSector());
                            search.setDisplayname(dbSubDomain.get(i).getDisplayname());

                            mPopular_SubSearchList.add(search);

                        }

                    }

                    mPopularSearchList = mPopular_SubSearchList;
                    gridLayoutManager = new GridLayoutManager(mContext, 2);
                    rvPopularSearch.setLayoutManager(gridLayoutManager);
                    popularSearchAdapter = new PopularSearchAdapter(mPopularSearchList, mContext, false, iSelectedPopularSearch);
                    rvPopularSearch.setAdapter(popularSearchAdapter);

                }

                if (mDomainSpinner.equalsIgnoreCase("ALL")) {

                    items = new ArrayList<ListCell>();
                    for (int i = 0; i < mGLobalSearch.size(); i++) {

                        items.add(new ListCell(mGLobalSearch.get(i).getName(), "Suggested Search", mGLobalSearch.get(i).getQuery(), mGLobalSearch.get(i).getDisplayname()));
                    }


                    for (int i = 0; i < mSpecializationDomain.size(); i++) {

                        items.add(new ListCell(mSpecializationDomain.get(i).getName(), "Specializations", mSpecializationDomain.get(i).getSector(), mSpecializationDomain.get(i).getDisplayname()));
                    }

                    listadapter = new SearchListAdpter("search", getActivity(), items, latitude, longitude, getParentFragment(), mSearchView);
                    searchSrcTextView.setAdapter(listadapter);

                    mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {

                            QuerySubmitCLick(query);
                            return true;
                        }

                        @Override
                        public boolean onQueryTextChange(String query) {
                            // filter recycler view when text is changed

                            if (!query.equalsIgnoreCase("")) {

                                mSearchtxt = query;

                            } else {
//                                mSearchtxt = "";
                            }

                            ImageView searchIcon = (ImageView) mSearchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
                            searchIcon.setImageDrawable(null);
                            searchIcon.setVisibility(View.GONE);
                            if (query.length() > 1) {

                                //list.setAdapter(adapter);
                                for (int i = 0; i < items.size(); i++) {
                                    if (items.get(i).getCategory().equalsIgnoreCase("Business Name as") || items.get(i).getCategory().equalsIgnoreCase("Business Id as")) {
                                        items.remove(items.get(i));
                                    }
                                }
                                items.add(new ListCell(query, "Business Id as", mDomainSpinner, query));
                                items.add(new ListCell(query, "Business Name as", mDomainSpinner, query));

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

                            SearchModel search = new SearchModel();
                            search.setDisplayname(mSubDomain.get(i).getDisplayname());
                            search.setSector(mSubDomain.get(i).getSector());
                            search.setName(mSubDomain.get(i).getName());
                            mSubDomainSubSearch.add(search);
                        }
                    }
                    for (int i = 0; i < mSubDomainSubSearch.size(); i++) {

                        items.add(new ListCell(mSubDomainSubSearch.get(i).getName(), "Sub Domain", mSubDomainSubSearch.get(i).getSector(), mSubDomainSubSearch.get(i).getDisplayname()));
                    }

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

                            ImageView searchIcon = (ImageView) mSearchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
                            searchIcon.setImageDrawable(null);
                            searchIcon.setVisibility(View.GONE);

                            if (query.length() > 1) {

                                //list.setAdapter(adapter);
                                //searchSrcTextView.setThreshold(3);
                                for (int i = 0; i < items.size(); i++) {
                                    if (items.get(i).getCategory().equalsIgnoreCase("Business Name as") || items.get(i).getCategory().equalsIgnoreCase("Business Id as")) {
                                        items.remove(items.get(i));
                                    }
                                }
                                items.add(new ListCell(query, "Business Id as", mDomainSpinner, query));
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
        searchSrcTextView = (SearchView.SearchAutoComplete) row.findViewById(androidx.appcompat.R.id.search_src_text);
        SearchManager searchMng = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchMng.getSearchableInfo(getActivity().getComponentName()));
        searchSrcTextView.setDropDownHeight(450);

        ImageView searchClose = (ImageView) mSearchView.findViewById(androidx.appcompat.R.id.search_close_btn);
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
                "fonts/JosefinSans-MediumItalic.ttf");
        searchSrcTextView.setTypeface(tyface1);
        searchSrcTextView.setTextColor(mContext.getResources().getColor(R.color.title_grey));
        searchSrcTextView.setHintTextColor(mContext.getResources().getColor(R.color.title_grey_light));


        String searchHint = "Search for Doctors, Salons...";
        Spannable spannable = new SpannableString(searchHint);

        Typeface tyface_edittext1 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/JosefinSans-MediumItalic.ttf");


        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface_edittext1), 0, searchHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        searchSrcTextView.setHint(spannable);


        searchSrcTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String mSector = "";
                ListCell cell = listadapter.getItem(position);
                Config.logV("Popular Text__________@@@Dele111");
                mSearchView.setQuery("", false);

                if (mtyp == null) {
                    mtyp = "city";
                }
                LanLong Lanlong = getLocationNearBy(latitude, longitude, mtyp);
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
                    subdomainquery = "sub_sector:'" + cell.getName() + "'";
                    subdomainName = cell.getName();
                }

                if (cell.getCategory().equalsIgnoreCase("Suggested Search")) {
                    Config.logV("Query------------" + cell.getMsector());
                    String requiredString = cell.getMsector().substring(cell.getMsector().indexOf("]") + 1, cell.getMsector().indexOf(")"));
                    Config.logV("Second---------" + requiredString);
                    querycreate = requiredString;
                }

                if (cell.getCategory().equalsIgnoreCase("Business Name as")) {
                    fromBusinessId = false;
                    fromBusinessName = true;
                    String name = cell.getName();
                    if (name.contains("'")) {
                        Config.logV("Query@@@@@@@@@@@@%%%###DDDD%%%%%%%%-----------" + name);
                        name = cell.getName().replace("'", "%5C%27");
                    }
                    Config.logV("Query@@@@@@@@@@@@%%%%%%%%%%%-----------" + name);


                    if (mSector.equalsIgnoreCase("All")) {
                        querycreate = "title " + "'" + name + "'";
                    } else {
                        sub = "title " + "'" + name + "'";
                        querycreate = "sector:" + "'" + mSector + "'" + " " + sub;
                    }
                }

                if (cell.getCategory().equalsIgnoreCase("Business Id as")) {


                    String name = cell.getName();
                    if (name.contains("'")) {
                        Config.logV("Query@@@@@@@@@@@@%%%###DDDD%%%%%%%%-----------" + name);
                        name = cell.getName().replace("'", "%5C%27");
                    }
                    Config.logV("Query@@@@@@@@@@@@%%%%%%%%%%%-----------" + name);

                    querycreate = "(or custom_id:" + "'" + name + "' enc_uid:" + "'" + name + "')";
                    fromBusinessId = true;
                    fromBusinessName = false;

                }


                Config.logV("Query-----------" + querycreate);


                // String pass = "haversin(11.751416900900901,75.3701820990991, location1.latitude, location1.longitude)";


                String pass = "haversin(" + latitude + "," + longitude + ", location1.latitude, location1.longitude)";

                Bundle bundle = new Bundle();
                /*bundle.putString("query", "(and location1:['11.751416900900901,75.3701820990991','9.9496150990991,77.171983900900'] " + querycreate + ")");
                bundle.putString("url", pass);*/


                //VALID QUERY PASS
                bundle.putString("subdomainquery", subdomainquery);
                bundle.putString("subdomainName", subdomainName);
                if (fromBusinessId) {
                    bundle.putString("query", querycreate);
                } else if (fromBusinessName) {

                    String qparam = "(and location1:" + locationRange + "(or (prefix field=" + querycreate + ") (phrase field=" + querycreate + ")))";
                    bundle.putString("query", qparam);

                } else {

                    bundle.putString("query", "(and location1:" + locationRange + querycreate + ")");

                }
                bundle.putString("url", pass);

                if (Config.isOnline(mContext)) {
                    Intent intent = new Intent(mContext, SearchResultsActivity.class);
                    bundle.putString("subdomain_select", "true");
                    bundle.putString("locName", tvLocation.getText().toString());
                    bundle.putString("latitude", String.valueOf(latitude));
                    bundle.putString("longitude", String.valueOf(longitude));
                    bundle.putString("spinnervalue", spinnerTxtPass);
                    bundle.putString("selectedDomain", mDomainSpinner);
                    mSearchtxt = cell.getMdisplayname();
                    Config.logV("SEARCH TXT 99999" + mSearchtxt);
                    bundle.putString("searchtxt", mSearchtxt);
                    bundle.putString("typ", mtyp);
                    intent.putExtra("Details", bundle);
                    startActivity(intent);
                    searchSrcTextView.clearFocus();
                    mSearchView.clearFocus();
                }
            }
        });


        rvNearByResults.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {

                if (mtyp == null) {
                    mtyp = "city";
                }
                LanLong Lanlong = getLocationNearBy(latitude, longitude, mtyp);
                double upperLeftLat = Lanlong.getUpperLeftLat();
                double upperLeftLon = Lanlong.getUpperLeftLon();
                double lowerRightLat = Lanlong.getLowerRightLat();
                double lowerRightLon = Lanlong.getLowerRightLon();
                String locationRange = "['" + lowerRightLat + "," + lowerRightLon + "','" + upperLeftLat + "," + upperLeftLon + "']";

                String query = "(and location1:" + locationRange + " " + ")";
                String url = "haversin(" + latitude + "," + longitude + ", location1.latitude, location1.longitude)";
                String sort = "claimable asc,distance asc, ynw_verified_level desc";
                Config.logV("Load More-----------------------");
                isLoading = true;
                Config.logV("CURRENT PAGE***************" + currentPage);
                Config.logV("CURRENT PAGE**111*************" + TOTAL_PAGES);
                currentPage += 10;

                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Config.logV("loadNextPage--------------------" + query);

                        loadNextPage(query, url);
                    }
                }, 1000);

            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });


        return row;
    }

    private void initializations(View view) {

        mSpinnerDomain = (Spinner) view.findViewById(R.id.spinnerdomain);
        rvNearByResults = view.findViewById(R.id.rv_nearbyResults);
        tvLocation = view.findViewById(R.id.tv_location);
        llPopularSearch = view.findViewById(R.id.ll_popularSearch);
        rvPopularSearch = view.findViewById(R.id.rv_popularSearch);
        llNoResults = view.findViewById(R.id.ll_noResults);
        tvNoResults = view.findViewById(R.id.txtnosearchresult);
    }

    private void APiSearchList() {
        final ApiInterface apiService =
                ApiClient.getClient(getActivity()).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, getActivity().getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<SearchModel> call = apiService.getAllSearch();
        call.enqueue(new Callback<SearchModel>() {
            @Override
            public void onResponse(Call<SearchModel> call, Response<SearchModel> response) {
                try {
                    ArrayList<SearchModel> mPopularSearch = new ArrayList<>();

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

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
                                search.setSector("All");


                                mGLobalSearch.add(search);
                                // Config.logV("Query*****111********" + response.body().getGlobalSearchLabels().get(k).getQuery());
                            }
                        }

                        Config.logV("Globa lSearch Size-------------" + mGLobalSearch.size());


                        mSpecializationDomain.clear();
                        mSubDomain.clear();


                        JsonArray lstPopularSearchLabel = response.body().getPopularSearchLabels().get("all").getAsJsonObject().get("labels").getAsJsonArray();
                        for (int k = 0; k < lstPopularSearchLabel.size(); k++) {
                            search = new SearchModel();
                            search.setName(lstPopularSearchLabel.get(k).getAsJsonObject().get("name").getAsString());
                            search.setDisplayname(lstPopularSearchLabel.get(k).getAsJsonObject().get("displayname").getAsString());
                            search.setQuery(lstPopularSearchLabel.get(k).getAsJsonObject().get("query").getAsString());
                            search.setSector("All");
                            mPopularSearch.add(search);
                        }

//                            Log.i("All Search Labels", new Gson().toJson(mPopularSearch));

                        for (int i = 0; i < response.body().getSectorLevelLabels().size(); i++) {
                            int mSectorSize = response.body().getSectorLevelLabels().get(i).getSubSectorLevelLabels().size();
                            Log.i("Sector", response.body().getSectorLevelLabels().get(i).getName());
//                                Log.i("KeyString",new Gson().toJson(response.body().getPopularSearchLabels().get(response.body().getSectorLevelLabels().get(i).getName())));
                            if (response.body().getPopularSearchLabels().get(response.body().getSectorLevelLabels().get(i).getName()) == null)
                                continue;
                            lstPopularSearchLabel = response.body().getPopularSearchLabels().get(response.body().getSectorLevelLabels().get(i).getName()).getAsJsonObject().get("labels").getAsJsonArray();

                            for (int l = 0; l < lstPopularSearchLabel.size(); l++) {
                                search = new SearchModel();
                                search.setName(lstPopularSearchLabel.get(l).getAsJsonObject().get("name").getAsString());
                                search.setDisplayname(lstPopularSearchLabel.get(l).getAsJsonObject().get("displayname").getAsString());
                                search.setQuery(lstPopularSearchLabel.get(l).getAsJsonObject().get("query").getAsString());
                                search.setSector(response.body().getSectorLevelLabels().get(i).getName());
                                mPopularSearch.add(search);
                            }

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
                                for (int j = 0; j < getSubdomainSectorLevel.get(k).getSpecializationLabels().size(); j++) {
                                    search = new SearchModel();
                                    search.setName(getSubdomainSectorLevel.get(k).getSpecializationLabels().get(j).getName());
                                    search.setDisplayname(getSubdomainSectorLevel.get(k).getSpecializationLabels().get(j).getDisplayname());
                                    search.setQuery(getSubdomainSectorLevel.get(k).getSpecializationLabels().get(j).getQuery());
                                    search.setSector(response.body().getSectorLevelLabels().get(i).getName());
                                    mSpecializationDomain.add(search);
                                }
                            }
                        }


                        DatabaseHandler db = new DatabaseHandler(mContext);
                        db.DeletePopularSearch();

                        db.insertPopularSearchInfo(mPopularSearch);
                        db = new DatabaseHandler(mContext);
                        db.DeleteSubDomain();
                        db.insertSubDomainList(mSubDomain);

                        db = new DatabaseHandler(mContext);
                        db.DeleteSubDomain();
                        db.insertSubDomainList(mSubDomain);

                        if (Config.isOnline(mContext)) {
                            APiGetDomain();
                        }
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


    public static int dpToPx(int dp) {
        float density = mContext.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }


    private void APiGetDomain() {
        {


            final ApiInterface apiService =
                    ApiClient.getClient(getActivity()).create(ApiInterface.class);

            final Dialog mDialog = Config.getProgressDialog(mContext, getActivity().getResources().getString(R.string.dialog_log_in));
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

                                DatabaseHandler db = new DatabaseHandler(mContext);
                                db.DeleteDomain();
                                db.insertDomainInfo(domainList);
                                ArrayAdapter<Domain_Spinner> adapter = new ArrayAdapter<Domain_Spinner>(getActivity(), R.layout.spinner_item, domainList);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                mSpinnerDomain.setAdapter(adapter);
                                mSpinnerDomain.setPopupBackgroundResource(R.color.white);


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

                        if (mtyp == null) {
                            mtyp = "city";
                        }
                        LanLong Lanlong = getLocationNearBy(latitude, longitude, mtyp);
                        double upperLeftLat = Lanlong.getUpperLeftLat();
                        double upperLeftLon = Lanlong.getUpperLeftLon();
                        double lowerRightLat = Lanlong.getLowerRightLat();
                        double lowerRightLon = Lanlong.getLowerRightLon();
                        String locationRange = "['" + lowerRightLat + "," + lowerRightLon + "','" + upperLeftLat + "," + upperLeftLon + "']";

                        String query = "(and location1:" + locationRange + " " + ")";
                        String url = "haversin(" + latitude + "," + longitude + ", location1.latitude, location1.longitude)";
                        String sort = "claimable asc,distance asc, ynw_verified_level desc";

                        String s_currentLoc = SharedPreference.getInstance(getActivity()).getStringValue("current_loc", "");
                        if (!s_currentLoc.equalsIgnoreCase("yes")) {
                            ApiSEARCHAWSLoadFirstData(query, url, sort);
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
            }
        });


    }

    public void ApiSEARCHAWSLoadFirstData(String mQueryPass, String mPass, String sort) {

        Config.logV("zeo WWWW" + sort);

        final ApiInterface apiService =
                ApiClient.getClientAWS(mContext).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Map<String, String> query = new HashMap<>();

        query.put("start", "0");
        query.put("q", mQueryPass);

        String mobile = SharedPreference.getInstance(mContext).getStringValue("mobile", "");
        if (mobile.startsWith("55")) {
            query.put("fq", "(and  test_account:1 " + passformula + ")");
        } else {
            query.put("fq", "(and  " + passformula + " (not test_account:1  ) )");
        }


        Map<String, String> params = new HashMap<>();

        params.put("size", "10");
        params.put("q.parser", "structured");
        params.put("q.options", "");
        params.put("sort", sort);
        params.put("expr.distance", mPass);
        params.put("return", "_all_fields,distance");

        Call<SearchAWsResponse> call = apiService.getSearchAWS(query, params);


        call.enqueue(new Callback<SearchAWsResponse>() {
            @Override
            public void onResponse(Call<SearchAWsResponse> call, Response<SearchAWsResponse> response) {

                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL--------First-------" + response.raw().request().url().toString().trim());

                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {


                        Config.logV("Status" + response.body().getStatus().getRid());

                        Config.logV("Found @@@@@@@@@@@@@@@@@@" + response.body().getHits().getFound());
                        total_foundcount = response.body().getHits().getFound();
                        TOTAL_PAGES = response.body().getHits().getFound() / 10;
                        if (response.body().getHits().getFound() > 0) {

                            tvNoResults.setVisibility(View.GONE);
                            rvNearByResults.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                            llNoResults.setVisibility(View.GONE);
                            String firstWord = String.valueOf(total_foundcount);
                            String secondWord = "";


                            if (total_foundcount > 1) {
                                secondWord = " results found ";
                            } else {
                                secondWord = " result found ";
                            }
                            Spannable spannable = new SpannableString(firstWord + secondWord);
                            Typeface tyface2 = Typeface.createFromAsset(mContext.getAssets(),
                                    "fonts/Montserrat_Bold.otf");
                            spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface2), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                            tv_searchresult.setText(spannable);


                            mSearchResp.clear();
                            ArrayList<String> ids = new ArrayList<>();
                            for (int i = 0; i < response.body().getHits().getHit().size(); i++) {
                                SearchAWsResponse search = new SearchAWsResponse();
                                search.setId(response.body().getHits().getHit().get(i).getId());
                                search.setLogo(response.body().getHits().getHit().get(i).getFields().getLogo());
                                search.setSub_sector_displayname(response.body().getHits().getHit().get(i).getFields().getSub_sector_displayname());
                                search.setTitle(response.body().getHits().getHit().get(i).getFields().getTitle());
                                search.setRating(response.body().getHits().getHit().get(i).getFields().getRating());
                                search.setPlace1(response.body().getHits().getHit().get(i).getFields().getPlace1());
                                search.setUnique_id(response.body().getHits().getHit().get(i).getFields().getUnique_id());
                                search.setLocation1(response.body().getHits().getHit().get(i).getFields().getLocation1());
                                search.setLocation_id1(response.body().getHits().getHit().get(i).getFields().getLocation_id1());
                                search.setSector(response.body().getHits().getHit().get(i).getFields().getSector());
                                search.setSub_sector(response.body().getHits().getHit().get(i).getFields().getSub_sector());
                                search.setClaimable(response.body().getHits().getHit().get(i).getFields().getClaimable());
                                search.setFirst_checkin_coupon_count(response.body().getHits().getHit().get(i).getFields().getFirst_checkin_coupon_count());
                                search.setJdn(response.body().getHits().getHit().get(i).getFields().getJdn());
                                search.setCoupon_enabled(response.body().getHits().getHit().get(i).getFields().getCoupon_enabled());
                                search.setOnline_profile(response.body().getHits().getHit().get(i).getFields().getOnline_profile());
                                search.setDonation_status(response.body().getHits().getHit().get(i).getFields().getDonation_status());
                                search.setVirtual_service_status(response.body().getHits().getHit().get(i).getFields().getVirtual_service_status());
                                search.setAccountType(response.body().getHits().getHit().get(i).getFields().getAccountType());
                                search.setBranch_name(response.body().getHits().getHit().get(i).getFields().getBranch_name());
                                search.setToday_appt(response.body().getHits().getHit().get(i).getFields().getToday_appt());
                                search.setFuture_appt(response.body().getHits().getHit().get(i).getFields().getFuture_appt());

                                if (response.body().getHits().getHit().get(i).getFields().getYnw_verified() != null) {
                                    search.setYnw_verified(response.body().getHits().getHit().get(i).getFields().getYnw_verified());

                                }
                                if (response.body().getHits().getHit().get(i).getFields().getYnw_verified_level() != null) {
                                    search.setYnw_verified_level(response.body().getHits().getHit().get(i).getFields().getYnw_verified_level());

                                }


                                if (response.body().getHits().getHit().get(i).getExprs() != null) {
                                    //   Config.logV("Distance @@@@@@@@@@@"+response.body().getHits().getHit().get(i).getExprs().getDistance());
                                    search.setDistance(response.body().getHits().getHit().get(i).getExprs().getDistance());
                                }

                                if (response.body().getHits().getHit().get(i).getFields().getQualification() != null) {
                                    search.setQualification(response.body().getHits().getHit().get(i).getFields().getQualification());

                                }

                                if (response.body().getHits().getHit().get(i).getFields().getSpecialization_displayname() != null) {
                                    search.setSpecialization_displayname(response.body().getHits().getHit().get(i).getFields().getSpecialization_displayname());

                                }
                                if (response.body().getHits().getHit().get(i).getFields().getShow_waiting_time() != null) {
                                    search.setShow_waiting_time(response.body().getHits().getHit().get(i).getFields().getShow_waiting_time());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getOnline_checkins() != null) {
                                    search.setOnline_checkins(response.body().getHits().getHit().get(i).getFields().getOnline_checkins());
                                }


                                Config.logV("UNIUE ID________________" + response.body().getHits().getHit().get(i).getFields().getUnique_id());

                                Config.logV("Search Detail--------" + response.body().getHits().getHit().get(i).getFields().getServices());
                                if (response.body().getHits().getHit().get(i).getFields().getServices() != null) {
                                    search.setServices(response.body().getHits().getHit().get(i).getFields().getServices());
                                }

                                if (response.body().getHits().getHit().get(i).getFields().getAppt_services() != null) {
                                    search.setAppt_services(response.body().getHits().getHit().get(i).getFields().getAppt_services());
                                }

                                if (response.body().getHits().getHit().get(i).getFields().getDonation_services() != null) {
                                    search.setDonation_services(response.body().getHits().getHit().get(i).getFields().getDonation_services());
                                }

                                if (response.body().getHits().getHit().get(i).getFields().getDepartments() != null) {
                                    search.setDepartments(response.body().getHits().getHit().get(i).getFields().getDepartments());
                                }


                                //7 types

                                if (response.body().getHits().getHit().get(i).getFields().getBusiness_hours1() != null) {
                                    search.setBusiness_hours1(response.body().getHits().getHit().get(i).getFields().getBusiness_hours1());
                                }

                                if (response.body().getHits().getHit().get(i).getFields().getTerminologies() != null) {
                                    search.setTerminologies(response.body().getHits().getHit().get(i).getFields().getTerminologies());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getProviders() != null) {
                                    search.setProviders(response.body().getHits().getHit().get(i).getFields().getProviders());
                                }

                                if (response.body().getHits().getHit().get(i).getFields().getFuture_checkins() != null) {
                                    search.setFuture_checkins(response.body().getHits().getHit().get(i).getFields().getFuture_checkins());
                                }


                                if (response.body().getHits().getHit().get(i).getFields().getParking_type_location1() != null) {
                                    Config.logV("PArking----111---------" + response.body().getHits().getHit().get(i).getFields().getParking_type_location1());
                                    search.setParking_type_location1(response.body().getHits().getHit().get(i).getFields().getParking_type_location1());
                                }

                                if (response.body().getHits().getHit().get(i).getFields().getParking_location1() != null) {

                                    Config.logV("@@@@@--111----------------" + response.body().getHits().getHit().get(i).getFields().getParking_location1());
                                    search.setParking_location1(response.body().getHits().getHit().get(i).getFields().getParking_location1());
                                }


                                if (response.body().getHits().getHit().get(i).getFields().getGallery_thumb_nails() != null) {
                                    //Config.logV("Gallery-@@@@---111----------------" + response.body().getHits().getHit().get(i).getFields().getGallery_thumb_nails());
                                    search.setGallery_thumb_nails(response.body().getHits().getHit().get(i).getFields().getGallery_thumb_nails());
                                }


                                if (response.body().getHits().getHit().get(i).getFields().getAlways_open_location1() != null) {
                                    search.setAlways_open_location1(response.body().getHits().getHit().get(i).getFields().getAlways_open_location1());
                                }

                                if (response.body().getHits().getHit().get(i).getFields().getTraumacentre_location1() != null) {
                                    search.setTraumacentre_location1(response.body().getHits().getHit().get(i).getFields().getTraumacentre_location1());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getDentistemergencyservices_location1() != null) {
                                    search.setDentistemergencyservices_location1(response.body().getHits().getHit().get(i).getFields().getDentistemergencyservices_location1());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getDocambulance_location1() != null) {
                                    search.setDocambulance_location1(response.body().getHits().getHit().get(i).getFields().getDocambulance_location1());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getPhysiciansemergencyservices_location1() != null) {
                                    search.setPhysiciansemergencyservices_location1(response.body().getHits().getHit().get(i).getFields().getPhysiciansemergencyservices_location1());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getHosemergencyservices_location1() != null) {
                                    search.setHosemergencyservices_location1(response.body().getHits().getHit().get(i).getFields().getHosemergencyservices_location1());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getAltemergencyservices_location1() != null) {
                                    search.setAltemergencyservices_location1(response.body().getHits().getHit().get(i).getFields().getAltemergencyservices_location1());
                                }


                                if (response.body().getHits().getHit().get(i).getFields().getFirstaid_location1() != null) {
                                    search.setFirstaid_location1(response.body().getHits().getHit().get(i).getFields().getFirstaid_location1());
                                }

                                if (response.body().getHits().getHit().get(i).getFields().getCustom_id() != null) {
                                    search.setCustom_id(response.body().getHits().getHit().get(i).getFields().getCustom_id());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getEnc_uid() != null) {
                                    search.setEnc_uid(response.body().getHits().getHit().get(i).getFields().getEnc_uid());
                                }

                                if (response.body().getHits().getHit().get(i).getId() != null) {
                                    ids.add(response.body().getHits().getHit().get(i).getId());
                                }

                                mSearchResp.add(search);
                            }


                            ApiSheduleList(ids, mSearchResp, "first", mScheduleList);

                        } else {
                            Config.logV(" No Found @@@@@@@@@@@@@@@@@@");
                            llNoResults.setVisibility(View.VISIBLE);
                            tvNoResults.setVisibility(View.VISIBLE);
                            rvNearByResults.setVisibility(View.GONE);
                            tvNoResults.setText("No service providers nearby");
                            progressBar.setVisibility(View.GONE);
                        }

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<SearchAWsResponse> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }

    /**
     * Method to combine Cloud response with Estimated waiting time.
     *
     * @param queuelist       Estimated Wait time Details
     * @param mSearchRespPass Cloud response
     * @param mCheck          first/next
     */
    private void ApiQueueList(ArrayList<String> queuelist, final List<SearchAWsResponse> mSearchRespPass, final String mCheck, final ArrayList<ScheduleList> mScheduleList) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        Config.logV("QUEUELIST @@@@@@@@@@@@@@@@@@@@@@");
       /* final Dialog mDialog = Config.getProgressDialog(getActivity(), getActivity().getResources().getString(R.string.dialog_log_in));
        mDialog.show();*/
        StringBuilder csvBuilder = new StringBuilder();
        for (String data : queuelist) {
            csvBuilder.append(data);
            csvBuilder.append(",");
        }
        String csv = csvBuilder.toString();
        System.out.println(csv);
        if (csv != "" && csv != null) {
            Call<List<QueueList>> call = apiService.getQueueCheckReponse(csv);
            call.enqueue(new Callback<List<QueueList>>() {
                @Override
                public void onResponse(Call<List<QueueList>> call, Response<List<QueueList>> response) {
                    try {
                   /* if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);*/
                        Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                        Config.logV("code---------------" + response.code());
                        mQueueList.clear();
                        if (response.code() == 200) {
                            Config.logV("Sucess ----------" + response.body());
                            for (int i = 0; i < response.body().size(); i++) {
                                mQueueList.add(response.body().get(i));
                            }
                            if (mCheck.equalsIgnoreCase("next")) {
                                Config.logV("TOTAL PAGES_--------------" + TOTAL_PAGES);
                                Config.logV("CURRENT PAGE**22222**555***********" + TOTAL_PAGES);
                                Config.logV("CURRENT PAGE**333*****5555********" + currentPage);
                                searchResultsAdapter.removeLoadingFooter();
                                isLoading = false;
                                mSearchListModel.clear();
                                for (int i = 0; i < mSearchRespPass.size(); i++) {
                                    // Cloud Response Setting
                                    SearchListModel searchList = new SearchListModel();
                                    searchList.setId(mSearchRespPass.get(i).getId());
                                    searchList.setLogo(mSearchRespPass.get(i).getLogo());
                                    searchList.setPlace1(mSearchRespPass.get(i).getPlace1());
                                    searchList.setSector(mSearchRespPass.get(i).getSub_sector_displayname());
                                    searchList.setTitle(mSearchRespPass.get(i).getTitle());
                                    searchList.setRating(mSearchRespPass.get(i).getRating());
                                    searchList.setUniqueid(mSearchRespPass.get(i).getUnique_id());
                                    searchList.setClaimable(mSearchRespPass.get(i).getClaimable());
                                    searchList.setFirst_checkin_coupon_count(mSearchRespPass.get(i).getFirst_checkin_coupon_count());
                                    searchList.setJdn(mSearchRespPass.get(i).getJdn());
                                    searchList.setCoupon_enabled(mSearchRespPass.get(i).getCoupon_enabled());
                                    searchList.setAccountType(mSearchRespPass.get(i).getAccountType());
                                    searchList.setBranch_name(mSearchRespPass.get(i).getBranch_name());
                                    searchList.setSectorname(mSearchRespPass.get(i).getSector());
                                    searchList.setSub_sector(mSearchRespPass.get(i).getSub_sector());
                                    searchList.setToday_appt(mSearchRespPass.get(i).getToday_appt());
                                    searchList.setOnline_profile(mSearchRespPass.get(i).getOnline_profile());
                                    searchList.setDonation_status(mSearchRespPass.get(i).getDonation_status());
                                    searchList.setFuture_appt(mSearchRespPass.get(i).getFuture_appt());
                                    searchList.setLocation1(mSearchRespPass.get(i).getLocation1());
                                    searchList.setLocation_id1(mSearchRespPass.get(i).getLocation_id1());
                                    searchList.setSpecialization_displayname(mSearchRespPass.get(i).getSpecialization_displayname());
                                    searchList.setVirtual_service_status(mSearchRespPass.get(i).getVirtual_service_status());
                                    String qualify = "";
                                    if (mSearchRespPass.get(i).getQualification() != null) {
                                        for (int l = 0; l < mSearchRespPass.get(i).getQualification().size(); l++) {
                                            qualify = qualify + ", " + mSearchRespPass.get(i).getQualification().get(l);

                                        }
                                        searchList.setQualification(qualify);
                                    }
                                    if (mSearchRespPass.get(i).getYnw_verified() != null) {
                                        searchList.setYnw_verified(Integer.parseInt(mSearchRespPass.get(i).getYnw_verified()));
                                    }
                                    if (mSearchRespPass.get(i).getDistance() != null) {
                                        Config.logV("Distance @@@@@@@@@@@" + mSearchRespPass.get(i).getDistance());
                                        searchList.setDistance(mSearchRespPass.get(i).getDistance());
                                    }
                                    if (mSearchRespPass.get(i).getYnw_verified_level() != null) {
                                        searchList.setYnw_verified_level(mSearchRespPass.get(i).getYnw_verified_level());
                                    }
                                    if (mSearchRespPass.get(i).getServices() != null) {
                                        searchList.setServices(mSearchRespPass.get(i).getServices());
                                    }
                                    if (mSearchRespPass.get(i).getAppt_services() != null) {
                                        searchList.setAppt_services(mSearchRespPass.get(i).getAppt_services());
                                    }
                                    if (mSearchRespPass.get(i).getDonation_services() != null) {
                                        searchList.setDonation_services(mSearchRespPass.get(i).getDonation_services());
                                    }
                                    if (mSearchRespPass.get(i).getDepartments() != null) {
                                        searchList.setDepartments(mSearchRespPass.get(i).getDepartments());
                                    }
                                    if (mSearchRespPass.get(i).getBusiness_hours1() != null) {
                                        searchList.setBusiness_hours1(mSearchRespPass.get(i).getBusiness_hours1());
                                    }
                                    if (mSearchRespPass.get(i).getTerminologies() != null) {
                                        searchList.setTerminologies(mSearchRespPass.get(i).getTerminologies());
                                    }
                                    if (mSearchRespPass.get(i).getProviders() != null) {
                                        searchList.setProviders(mSearchRespPass.get(i).getProviders());
                                    }
                                    if (mSearchRespPass.get(i).getOnline_checkins() != null) {
                                        searchList.setOnline_checkins(mSearchRespPass.get(i).getOnline_checkins());
                                    }
                                    if (mSearchRespPass.get(i).getFuture_checkins() != null) {
                                        searchList.setFuture_checkins(mSearchRespPass.get(i).getFuture_checkins());
                                    }
                                    if (mSearchRespPass.get(i).getShow_waiting_time() != null) {
                                        searchList.setShow_waiting_time(mSearchRespPass.get(i).getShow_waiting_time());
                                    }
                                    if (mSearchRespPass.get(i).getGallery_thumb_nails() != null) {
                                        //   Config.logV("Gallery-@@@@---111-------5555---------" + mSearchRespPass.get(i).getGallery_thumb_nails());
                                        searchList.setGallery_thumb_nails(mSearchRespPass.get(i).getGallery_thumb_nails());
                                    }
                                    // Location Amenities --- Start
                                    if (mSearchRespPass.get(i).getParking_type_location1() != null) {
                                        Config.logV("PArking-------------" + mSearchRespPass.get(i).getParking_type_location1());
                                        searchList.setParking_type_location1(mSearchRespPass.get(i).getParking_type_location1());
                                    }
                                    if (mSearchRespPass.get(i).getParking_location1() != null) {
                                        Config.logV("Park-@@@@-------------3333------" + mSearchRespPass.get(i).getParking_location1());
                                        searchList.setParking_location1(mSearchRespPass.get(i).getParking_location1());
                                    }
                                    if (mSearchRespPass.get(i).getAlways_open_location1() != null) {
                                        searchList.setAlways_open_location1(mSearchRespPass.get(i).getAlways_open_location1());
                                    }
                                    if (mSearchRespPass.get(i).getTraumacentre_location1() != null) {
                                        searchList.setTraumacentre_location1(mSearchRespPass.get(i).getTraumacentre_location1());
                                    }
                                    if (mSearchRespPass.get(i).getDentistemergencyservices_location1() != null) {
                                        searchList.setDentistemergencyservices_location1(mSearchRespPass.get(i).getDentistemergencyservices_location1());
                                    }
                                    if (mSearchRespPass.get(i).getDocambulance_location1() != null) {
                                        searchList.setDocambulance_location1(mSearchRespPass.get(i).getDocambulance_location1());
                                    }
                                    if (mSearchRespPass.get(i).getPhysiciansemergencyservices_location1() != null) {
                                        searchList.setPhysiciansemergencyservices_location1(mSearchRespPass.get(i).getPhysiciansemergencyservices_location1());
                                    }
                                    if (mSearchRespPass.get(i).getHosemergencyservices_location1() != null) {
                                        searchList.setHosemergencyservices_location1(mSearchRespPass.get(i).getHosemergencyservices_location1());
                                    }
                                    if (mSearchRespPass.get(i).getFirstaid_location1() != null) {
                                        searchList.setFirstaid_location1(mSearchRespPass.get(i).getFirstaid_location1());
                                    }
                                    if (mSearchRespPass.get(i).getCustom_id() != null) {
                                        searchList.setCustom_id(mSearchRespPass.get(i).getCustom_id());
                                    }
                                    if (mSearchRespPass.get(i).getEnc_uid() != null) {
                                        searchList.setEnc_uid(mSearchRespPass.get(i).getEnc_uid());
                                    }

                                    searchList.setQId(mSearchRespPass.get(i).getId());
                                    if (mQueueList.get(i).getMessage() != null) {
                                        searchList.setMessage(mQueueList.get(i).getMessage());
                                        searchList.setWaitlistEnabled(mQueueList.get(i).isWaitlistEnabled());
                                    }
                                    if (mQueueList.get(i).getNextAvailableQueue() != null) {
                                        if (mQueueList.get(i).getNextAvailableQueue().getLocation() != null) {
                                            if (mQueueList.get(i).getNextAvailableQueue().getAvailableDate() != null) {
                                                searchList.setAvail_date(mQueueList.get(i).getNextAvailableQueue().getAvailableDate());
                                            }
                                            if (mQueueList.get(i).getNextAvailableQueue().getLocation() != null) {
                                                searchList.setmLoc(String.valueOf(mQueueList.get(i).getNextAvailableQueue().getLocation().getId()));
                                            }
                                            searchList.setIsopen(mQueueList.get(i).getNextAvailableQueue().isOpenNow());
                                            searchList.setPersonAhead(mQueueList.get(i).getNextAvailableQueue().getPersonAhead());
                                            searchList.setCalculationMode(mQueueList.get(i).getNextAvailableQueue().getCalculationMode());
                                            searchList.setQueueWaitingTime(mQueueList.get(i).getNextAvailableQueue().getQueueWaitingTime());
                                            searchList.setBranchSpCount(mQueueList.get(i).getBranchSpCount());
                                            searchList.setOnlineCheckIn(mQueueList.get(i).getNextAvailableQueue().isOnlineCheckIn());
                                            searchList.setWaitlistEnabled(mQueueList.get(i).isWaitlistEnabled());
                                            searchList.setAvailableToday(mQueueList.get(i).getNextAvailableQueue().isAvailableToday());
                                            searchList.setShowToken(mQueueList.get(i).getNextAvailableQueue().isShowToken());
                                            if (mQueueList.get(i).getNextAvailableQueue().getServiceTime() != null) {
                                                searchList.setServiceTime(mQueueList.get(i).getNextAvailableQueue().getServiceTime());
                                            }
                                        }
                                    }
                                    if (mScheduleList != null && mScheduleList.size() > 0) {
                                        if (mScheduleList.get(i).isApptEnabled()) {
                                            searchList.setCheckinAllowed(true);
                                            searchList.setApptEnabled(true);
                                        } else {
                                            searchList.setCheckinAllowed(false);
                                            searchList.setApptEnabled(false);
                                        }
                                        //  searchList.setCheckinAllowed(mScheduleList.get(i).isCheckinAllowed());
                                        if (mScheduleList.get(i).getAvailableSchedule() != null) {
                                            if (mScheduleList.get(i).getAvailableSchedule().getLocation() != null) {
                                                searchList.setaLoc(String.valueOf(mScheduleList.get(i).getAvailableSchedule().getLocation().getId()));
                                            }
                                            searchList.setAvailableDate(mScheduleList.get(i).getAvailableSchedule().getAvailableDate());
                                        }

                                        if (mScheduleList.get(i).getSlotsData() != null) {
                                            searchList.setAvailableTime(mScheduleList.get(i).getSlotsData().getAvailableSlots().get(0).getSlotTime());
                                        }
                                    }

                                    mSearchListModel.add(searchList);
//                                    Log.i("mSearchListModel12", new Gson().toJson(mSearchListModel));
                                }

                                List<SearchListModel> results = mSearchListModel;
                                searchResultsAdapter.addAll(results);
                                boolean isLoading = true;
                                searchResultsAdapter.notifyDataSetChanged();
                                if (currentPage / 10 != TOTAL_PAGES) {
                                    searchResultsAdapter.addLoadingFooter();
                                } else {
                                    isLastPage = true;
                                }
                            } else {
                                mSearchListModel.clear();
                                for (int i = 0; i < mSearchRespPass.size(); i++) {
                                    SearchListModel searchList = new SearchListModel();
                                    searchList.setId(mSearchRespPass.get(i).getId());
                                    searchList.setLogo(mSearchRespPass.get(i).getLogo());
                                    searchList.setPlace1(mSearchRespPass.get(i).getPlace1());
                                    searchList.setSector(mSearchRespPass.get(i).getSub_sector_displayname());
                                    searchList.setTitle(mSearchRespPass.get(i).getTitle());
                                    searchList.setRating(mSearchRespPass.get(i).getRating());
                                    searchList.setUniqueid(mSearchRespPass.get(i).getUnique_id());
                                    searchList.setLocation1(mSearchRespPass.get(i).getLocation1());
                                    searchList.setLocation_id1(mSearchRespPass.get(i).getLocation_id1());
                                    searchList.setSectorname(mSearchRespPass.get(i).getSector());
                                    searchList.setSub_sector(mSearchRespPass.get(i).getSub_sector());
                                    searchList.setClaimable(mSearchRespPass.get(i).getClaimable());
                                    searchList.setFirst_checkin_coupon_count(mSearchRespPass.get(i).getFirst_checkin_coupon_count());
                                    searchList.setJdn(mSearchRespPass.get(i).getJdn());
                                    searchList.setAccountType(mSearchRespPass.get(i).getAccountType());
                                    searchList.setBranch_name(mSearchRespPass.get(i).getBranch_name());
                                    searchList.setCoupon_enabled(mSearchRespPass.get(i).getCoupon_enabled());
                                    searchList.setToday_appt(mSearchRespPass.get(i).getToday_appt());
                                    searchList.setOnline_profile(mSearchRespPass.get(i).getOnline_profile());
                                    searchList.setDonation_status(mSearchRespPass.get(i).getDonation_status());
                                    searchList.setVirtual_service_status(mSearchRespPass.get(i).getVirtual_service_status());
                                    searchList.setFuture_appt(mSearchRespPass.get(i).getFuture_appt());
                                    searchList.setSpecialization_displayname(mSearchRespPass.get(i).getSpecialization_displayname());
                                    searchList.setVirtual_service_status(mSearchRespPass.get(i).getVirtual_service_status());
                                    String qualify = "";
                                    if (mSearchRespPass.get(i).getQualification() != null) {
                                        for (int l = 0; l < mSearchRespPass.get(i).getQualification().size(); l++) {
                                            qualify = qualify + ", " + mSearchRespPass.get(i).getQualification().get(l);
                                        }
                                        searchList.setQualification(qualify);
                                    }
                                    if (mSearchRespPass.get(i).getYnw_verified() != null) {
                                        searchList.setYnw_verified(Integer.parseInt(mSearchRespPass.get(i).getYnw_verified()));

                                    }
                                    if (mSearchRespPass.get(i).getDistance() != null) {
                                        Config.logV("Distance @@@@@@@@@@@44444" + mSearchRespPass.get(i).getDistance());
                                        searchList.setDistance(mSearchRespPass.get(i).getDistance());
                                    }
                                    if (mSearchRespPass.get(i).getYnw_verified_level() != null) {
                                        searchList.setYnw_verified_level(mSearchRespPass.get(i).getYnw_verified_level());
                                    }
                                    if (mSearchRespPass.get(i).getDepartments() != null) {
                                        searchList.setDepartments(mSearchRespPass.get(i).getDepartments());
                                    }
                                    if (mSearchRespPass.get(i).getServices() != null) {
                                        searchList.setServices(mSearchRespPass.get(i).getServices());
                                    }
                                    if (mSearchRespPass.get(i).getAppt_services() != null) {
                                        searchList.setAppt_services(mSearchRespPass.get(i).getAppt_services());
                                    }
                                    if (mSearchRespPass.get(i).getDonation_services() != null) {
                                        searchList.setDonation_services(mSearchRespPass.get(i).getDonation_services());
                                    }
                                    if (mSearchRespPass.get(i).getBusiness_hours1() != null) {
                                        searchList.setBusiness_hours1(mSearchRespPass.get(i).getBusiness_hours1());
                                    }
                                    if (mSearchRespPass.get(i).getTerminologies() != null) {
                                        searchList.setTerminologies(mSearchRespPass.get(i).getTerminologies());
                                    }
                                    if (mSearchRespPass.get(i).getProviders() != null) {
                                        searchList.setProviders(mSearchRespPass.get(i).getProviders());
                                    }
                                    if (mSearchRespPass.get(i).getOnline_checkins() != null) {
                                        searchList.setOnline_checkins(mSearchRespPass.get(i).getOnline_checkins());
                                    }
                                    if (mSearchRespPass.get(i).getFuture_checkins() != null) {
                                        searchList.setFuture_checkins(mSearchRespPass.get(i).getFuture_checkins());
                                    }
                                    if (mSearchRespPass.get(i).getShow_waiting_time() != null) {
                                        searchList.setShow_waiting_time(mSearchRespPass.get(i).getShow_waiting_time());
                                    }
                                    if (mSearchRespPass.get(i).getGallery_thumb_nails() != null) {
                                        // Config.logV("Gallery ###########"+mSearchRespPass.get(i).getGallery_thumb_nails());
                                        searchList.setGallery_thumb_nails(mSearchRespPass.get(i).getGallery_thumb_nails());
                                    }
                                    if (mSearchRespPass.get(i).getParking_type_location1() != null) {
                                        searchList.setParking_type_location1(mSearchRespPass.get(i).getParking_type_location1());
                                    }
                                    if (mSearchRespPass.get(i).getParking_location1() != null) {
                                        Config.logV("Park-@@@@----------4444-----" + mSearchRespPass.get(i).getParking_location1());
                                        searchList.setParking_location1(mSearchRespPass.get(i).getParking_location1());
                                    }

                                    if (mSearchRespPass.get(i).getAlways_open_location1() != null) {
                                        searchList.setAlways_open_location1(mSearchRespPass.get(i).getAlways_open_location1());
                                    }
                                    if (mSearchRespPass.get(i).getTraumacentre_location1() != null) {
                                        searchList.setTraumacentre_location1(mSearchRespPass.get(i).getTraumacentre_location1());
                                    }
                                    if (mSearchRespPass.get(i).getDentistemergencyservices_location1() != null) {
                                        searchList.setDentistemergencyservices_location1(mSearchRespPass.get(i).getDentistemergencyservices_location1());
                                    }
                                    if (mSearchRespPass.get(i).getDocambulance_location1() != null) {
                                        searchList.setDocambulance_location1(mSearchRespPass.get(i).getDocambulance_location1());
                                    }
                                    if (mSearchRespPass.get(i).getPhysiciansemergencyservices_location1() != null) {
                                        searchList.setPhysiciansemergencyservices_location1(mSearchRespPass.get(i).getPhysiciansemergencyservices_location1());
                                    }
                                    if (mSearchRespPass.get(i).getHosemergencyservices_location1() != null) {
                                        searchList.setHosemergencyservices_location1(mSearchRespPass.get(i).getHosemergencyservices_location1());
                                    }

                                    if (mSearchRespPass.get(i).getAltemergencyservices_location1() != null) {
                                        searchList.setAltemergencyservices_location1(mSearchRespPass.get(i).getAltemergencyservices_location1());
                                    }

                                    if (mSearchRespPass.get(i).getFirstaid_location1() != null) {
                                        searchList.setFirstaid_location1(mSearchRespPass.get(i).getFirstaid_location1());
                                    }
                                    if (mSearchRespPass.get(i).getCustom_id() != null) {
                                        searchList.setCustom_id(mSearchRespPass.get(i).getCustom_id());
                                    }
                                    if (mSearchRespPass.get(i).getEnc_uid() != null) {
                                        searchList.setEnc_uid(mSearchRespPass.get(i).getEnc_uid());
                                    }
//                                    Log.i("mSearchResponse", new Gson().toJson(mSearchRespPass));
                                    searchList.setQId(mSearchRespPass.get(i).getId());
                                    if (mQueueList.get(i).getMessage() != null) {
                                        searchList.setMessage(mQueueList.get(i).getMessage());
                                        searchList.setWaitlistEnabled(mQueueList.get(i).isWaitlistEnabled());
                                    }
                                    if (mQueueList.get(i).getNextAvailableQueue() != null) {
                                        if (mQueueList.get(i).getNextAvailableQueue().getLocation() != null) {
                                            if (mQueueList.get(i).getNextAvailableQueue().getAvailableDate() != null) {
                                                searchList.setAvail_date(mQueueList.get(i).getNextAvailableQueue().getAvailableDate());
                                            }
                                            if (mQueueList.get(i).getNextAvailableQueue().getLocation() != null) {
                                                searchList.setmLoc(String.valueOf(mQueueList.get(i).getNextAvailableQueue().getLocation().getId()));
                                            }
                                            searchList.setIsopen(mQueueList.get(i).getNextAvailableQueue().isOpenNow());
                                            searchList.setPersonAhead(mQueueList.get(i).getNextAvailableQueue().getPersonAhead());
                                            searchList.setCalculationMode(mQueueList.get(i).getNextAvailableQueue().getCalculationMode());
                                            searchList.setQueueWaitingTime(mQueueList.get(i).getNextAvailableQueue().getQueueWaitingTime());
                                            searchList.setBranchSpCount(mQueueList.get(i).getBranchSpCount());
                                            searchList.setOnlineCheckIn(mQueueList.get(i).getNextAvailableQueue().isOnlineCheckIn());
                                            searchList.setWaitlistEnabled(mQueueList.get(i).isWaitlistEnabled());
                                            searchList.setAvailableToday(mQueueList.get(i).getNextAvailableQueue().isAvailableToday());
                                            searchList.setShowToken(mQueueList.get(i).getNextAvailableQueue().isShowToken());
                                            if (mQueueList.get(i).getNextAvailableQueue().getServiceTime() != null) {
                                                searchList.setServiceTime(mQueueList.get(i).getNextAvailableQueue().getServiceTime());
                                            }
                                        }
                                    }
                                    if (mScheduleList != null && mScheduleList.size() > 0) {
                                        if (mScheduleList.get(i).isApptEnabled()) {
                                            searchList.setCheckinAllowed(true);
                                            searchList.setApptEnabled(true);
                                        } else {
                                            searchList.setCheckinAllowed(false);
                                            searchList.setApptEnabled(false);
                                        }
                                        //searchList.setCheckinAllowed(mScheduleList.get(i).isCheckinAllowed());
                                        if (mScheduleList.get(i).getAvailableSchedule() != null) {
                                            if (mScheduleList.get(i).getAvailableSchedule().getLocation() != null) {
                                                searchList.setaLoc(String.valueOf(mScheduleList.get(i).getAvailableSchedule().getLocation().getId()));
                                            }
                                            searchList.setAvailableDate(mScheduleList.get(i).getAvailableSchedule().getAvailableDate());
                                        }
                                        if (mScheduleList.get(i).getSlotsData() != null) {
                                            searchList.setAvailableTime(mScheduleList.get(i).getSlotsData().getAvailableSlots().get(0).getSlotTime());
                                        }
                                    }
                                    mSearchListModel.add(searchList);
                                }
                                final List<SearchListModel> results = mSearchListModel;
//                                Log.i("FinalResult", new Gson().toJson(results));
                                progressBar.setVisibility(View.GONE);
                                searchResultsAdapter.addAll(results);
                                searchResultsAdapter.notifyDataSetChanged();
                                Config.logV("QUEUELIST @@@@@@@@@@@@@@@@@@@@@@ RESUlt" + results.size());
                                Config.logV("Results@@@@@@@@@@@@@@@@@" + results.size());
                                Config.logV("CURRENT PAGE**22222*************" + TOTAL_PAGES);
                                Config.logV("CURRENT PAGE**333*************" + currentPage);
                                if (TOTAL_PAGES > 0 && total_foundcount > 10) {
                                    Config.logV("First ADD Footer");
                                    searchResultsAdapter.addLoadingFooter();
                                } else {
                                    isLastPage = true;
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<List<QueueList>> call, Throwable t) {
                    // Log error here since request failed
                    Config.logV("Fail---------------" + t.toString());
               /* if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);
*/
                }
            });
        }
    }

    private void ApiSheduleList(final ArrayList<String> queuelist, final List<SearchAWsResponse> mSearchRespPass, final String mCheck, final ArrayList<ScheduleList> mScheduleList) {
        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        StringBuilder csvBuilder = new StringBuilder();
        for (String data : queuelist) {
            csvBuilder.append(data);
            csvBuilder.append(",");
        }
        String csv = csvBuilder.toString();
        System.out.println(csv);
        if (csv != "" && csv != null) {
            Call<ArrayList<ScheduleList>> call = apiService.getScheduleCheckReponse(csv);
            call.enqueue(new Callback<ArrayList<ScheduleList>>() {
                @Override
                public void onResponse(Call<ArrayList<ScheduleList>> call, Response<ArrayList<ScheduleList>> response) {
                    try {
                        mScheduleList.clear();
//                        Log.i("SearchScheduleResp", new Gson().toJson(response.body()));
                        if (response.code() == 200) {
                            for (int i = 0; i < response.body().size(); i++) {
                                mScheduleList.add(i, response.body().get(i));
                            }
                            if (mCheck.equals("next")) {
                                ApiQueueList(queuelist, mSearchResp, "next", mScheduleList);
                            } else if (mCheck.equals("first")) {
                                ApiQueueList(queuelist, mSearchResp, "first", mScheduleList);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<ScheduleList>> call, Throwable t) {
                    // Log error here since request failed
                    Config.logV("Fail---------------" + t.toString());
               /* if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);
*/
                }
            });
        }
    }

    private void loadNextPage(String mQueryPass, String mPass) {
        Log.d("", "loadNextPage: " + currentPage);

        final ApiInterface apiService =
                ApiClient.getClientAWS(mContext).create(ApiInterface.class);

       /* final Dialog mDialog = Config.getProgressDialog(getActivity(), getActivity().getResources().getString(R.string.dialog_log_in));
        mDialog.show();*/

        Map<String, String> query = new HashMap<>();
        query.put("start", String.valueOf(currentPage));
        query.put("q", mQueryPass);
        String mobile = SharedPreference.getInstance(mContext).getStringValue("mobile", "");
        if (mobile.startsWith("55")) {
            query.put("fq", "(and  test_account:1 " + passformula + ")");
        } else {
            query.put("fq", "(and  " + passformula + "(not test_account:1 ) )");
        }
        Map<String, String> params = new HashMap<>();
        params.put("size", "10");
        params.put("q.parser", "structured");
        params.put("sort", "claimable asc, ynw_verified_level desc, distance asc");
        params.put("expr.distance", mPass);
        params.put("return", "_all_fields,distance");
        Call<SearchAWsResponse> call = apiService.getSearchAWS(query, params);
        call.enqueue(new Callback<SearchAWsResponse>() {
            @Override
            public void onResponse(Call<SearchAWsResponse> call, Response<SearchAWsResponse> response) {
                try {
                    /*if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);*/
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
//                        Config.logV("Response--Body AWS-------------------------" + new Gson().toJson(response.body()));
                        Config.logV("Status" + response.body().getStatus().getRid());
                        Config.logV("Found" + response.body().getHits().getFound());
                        TOTAL_PAGES = response.body().getHits().getFound() / 10;
                        if (response.body().getHits().getFound() > 0) {
                            mSearchResp.clear();
                            ArrayList<String> ids = new ArrayList<>();
                            for (int i = 0; i < response.body().getHits().getHit().size(); i++) {
                                SearchAWsResponse search = new SearchAWsResponse();
                                search.setId(response.body().getHits().getHit().get(i).getId());
                                search.setLogo(response.body().getHits().getHit().get(i).getFields().getLogo());
                                search.setSub_sector_displayname(response.body().getHits().getHit().get(i).getFields().getSub_sector_displayname());
                                search.setTitle(response.body().getHits().getHit().get(i).getFields().getTitle());
                                search.setRating(response.body().getHits().getHit().get(i).getFields().getRating());
                                search.setPlace1(response.body().getHits().getHit().get(i).getFields().getPlace1());
                                search.setUnique_id(response.body().getHits().getHit().get(i).getFields().getUnique_id());
                                search.setClaimable(response.body().getHits().getHit().get(i).getFields().getClaimable());
                                search.setFirst_checkin_coupon_count(response.body().getHits().getHit().get(i).getFields().getFirst_checkin_coupon_count());
                                search.setJdn(response.body().getHits().getHit().get(i).getFields().getJdn());
                                search.setCoupon_enabled(response.body().getHits().getHit().get(i).getFields().getCoupon_enabled());
                                search.setOnline_profile(response.body().getHits().getHit().get(i).getFields().getOnline_profile());
                                search.setDonation_status(response.body().getHits().getHit().get(i).getFields().getDonation_status());
                                search.setVirtual_service_status(response.body().getHits().getHit().get(i).getFields().getVirtual_service_status());
                                search.setAccountType(response.body().getHits().getHit().get(i).getFields().getAccountType());
                                search.setBranch_name(response.body().getHits().getHit().get(i).getFields().getBranch_name());
                                search.setToday_appt(response.body().getHits().getHit().get(i).getFields().getToday_appt());
                                search.setFuture_appt(response.body().getHits().getHit().get(i).getFields().getFuture_appt());
                                search.setLocation1(response.body().getHits().getHit().get(i).getFields().getLocation1());
                                search.setLocation_id1(response.body().getHits().getHit().get(i).getFields().getLocation_id1());
                                search.setSector(response.body().getHits().getHit().get(i).getFields().getSector());
                                search.setSub_sector(response.body().getHits().getHit().get(i).getFields().getSub_sector());
                                if (response.body().getHits().getHit().get(i).getFields().getYnw_verified() != null) {
                                    search.setYnw_verified(response.body().getHits().getHit().get(i).getFields().getYnw_verified());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getYnw_verified_level() != null) {
                                    search.setYnw_verified_level(response.body().getHits().getHit().get(i).getFields().getYnw_verified_level());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getQualification() != null) {
                                    search.setQualification(response.body().getHits().getHit().get(i).getFields().getQualification());
                                }
                                if (response.body().getHits().getHit().get(i).getExprs() != null) {
                                    search.setDistance(response.body().getHits().getHit().get(i).getExprs().getDistance());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getSpecialization_displayname() != null) {
                                    search.setSpecialization_displayname(response.body().getHits().getHit().get(i).getFields().getSpecialization_displayname());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getShow_waiting_time() != null) {
                                    search.setShow_waiting_time(response.body().getHits().getHit().get(i).getFields().getShow_waiting_time());
                                }
                                // Config.logV("response.body().getHits().getHit().get(i).getFields().toString()"+response.body().getHits().getHit().get(i).getFields().toString());
                                //search.setFound(response.body().getHits().getFound());
                                if (response.body().getHits().getHit().get(i).getFields().getServices() != null) {
                                    search.setServices(response.body().getHits().getHit().get(i).getFields().getServices());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getAppt_services() != null) {
                                    search.setAppt_services(response.body().getHits().getHit().get(i).getFields().getAppt_services());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getDonation_services() != null) {
                                    search.setDonation_services(response.body().getHits().getHit().get(i).getFields().getDonation_services());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getDepartments() != null) {
                                    search.setDepartments(response.body().getHits().getHit().get(i).getFields().getDepartments());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getBusiness_hours1() != null) {
                                    search.setBusiness_hours1(response.body().getHits().getHit().get(i).getFields().getBusiness_hours1());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getTerminologies() != null) {
                                    search.setTerminologies(response.body().getHits().getHit().get(i).getFields().getTerminologies());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getProviders() != null) {
                                    search.setProviders(response.body().getHits().getHit().get(i).getFields().getProviders());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getOnline_checkins() != null) {
                                    search.setOnline_checkins(response.body().getHits().getHit().get(i).getFields().getOnline_checkins());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getFuture_checkins() != null) {
                                    search.setFuture_checkins(response.body().getHits().getHit().get(i).getFields().getFuture_checkins());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getGallery_thumb_nails() != null) {
                                    search.setGallery_thumb_nails(response.body().getHits().getHit().get(i).getFields().getGallery_thumb_nails());
                                }
                                //7 types
                                if (response.body().getHits().getHit().get(i).getFields().getParking_type_location1() != null) {
                                    Config.logV("PArking----111---------" + response.body().getHits().getHit().get(i).getFields().getParking_type_location1());
                                    search.setParking_type_location1(response.body().getHits().getHit().get(i).getFields().getParking_type_location1());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getParking_location1() != null) {
                                    Config.logV("Park-@@@@-------------------" + response.body().getHits().getHit().get(i).getFields().getParking_location1());
                                    search.setParking_location1(response.body().getHits().getHit().get(i).getFields().getParking_location1());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getAlways_open_location1() != null) {
                                    search.setAlways_open_location1(response.body().getHits().getHit().get(i).getFields().getAlways_open_location1());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getTraumacentre_location1() != null) {
                                    search.setTraumacentre_location1(response.body().getHits().getHit().get(i).getFields().getTraumacentre_location1());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getDentistemergencyservices_location1() != null) {
                                    search.setDentistemergencyservices_location1(response.body().getHits().getHit().get(i).getFields().getDentistemergencyservices_location1());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getDocambulance_location1() != null) {
                                    search.setDocambulance_location1(response.body().getHits().getHit().get(i).getFields().getDocambulance_location1());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getPhysiciansemergencyservices_location1() != null) {
                                    search.setPhysiciansemergencyservices_location1(response.body().getHits().getHit().get(i).getFields().getPhysiciansemergencyservices_location1());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getHosemergencyservices_location1() != null) {
                                    search.setHosemergencyservices_location1(response.body().getHits().getHit().get(i).getFields().getHosemergencyservices_location1());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getAltemergencyservices_location1() != null) {
                                    search.setAltemergencyservices_location1(response.body().getHits().getHit().get(i).getFields().getAltemergencyservices_location1());
                                }

                                if (response.body().getHits().getHit().get(i).getFields().getFirstaid_location1() != null) {
                                    search.setFirstaid_location1(response.body().getHits().getHit().get(i).getFields().getFirstaid_location1());
                                }

                                if (response.body().getHits().getHit().get(i).getFields().getCustom_id() != null) {
                                    search.setCustom_id(response.body().getHits().getHit().get(i).getFields().getCustom_id());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getEnc_uid() != null) {
                                    search.setEnc_uid(response.body().getHits().getHit().get(i).getFields().getEnc_uid());
                                }

                                mSearchResp.add(search);
                                if (response.body().getHits().getHit().get(i).getId() != null) {
                                    ids.add(response.body().getHits().getHit().get(i).getId());
                                }
                            }
//                            Log.i("SearchList", new Gson().toJson(mSearchResp));
                            ApiSheduleList(ids, mSearchResp, "next", mScheduleList);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SearchAWsResponse> call, Throwable t) {
                t.printStackTrace();
                // TODO: 08/11/16 handle failure
            }
        });
    }


    public void QuerySubmitCLick(String query) {
        mSearchView.setQuery("", false);
        if (mtyp == null) {
            mtyp = "city";
        }
        LanLong Lanlong = getLocationNearBy(latitude, longitude, mtyp);
        double upperLeftLat = Lanlong.getUpperLeftLat();
        double upperLeftLon = Lanlong.getUpperLeftLon();
        double lowerRightLat = Lanlong.getLowerRightLat();
        double lowerRightLon = Lanlong.getLowerRightLon();
        String locationRange = "['" + lowerRightLat + "," + lowerRightLon + "','" + upperLeftLat + "," + upperLeftLon + "']";
        query1 = query;
        if (query1.contains(" ")) {
            Config.logV("Query@@@@@@@@@@@@%%%###DDDD%%%%%%%%-----------" + query);
            query1 = query.replace(" ", "__");
        }
        if (query1.contains("'")) {
            Config.logV("Query@@@@@@@@@@@@%%%###DDDD%%%%%%%%-----------" + query1);
            query1 = query1.replace("'", "%5C%27");
        }
        if (query1.contains(" ")) {
            Config.logV("Query@@@@@@@@@@@@%%%###DDDD%%%%%%%%-----------" + query1);
            query1 = query1.replace(" ", "%20");
        }
        if (query.contains("'")) {
            Config.logV("Query@@@@@@@@@@@@%%%###DDDD%%%%%%%%-----------" + query);
            query = query.replace("'", "%5C%27");
        }
        Config.logV("Query@@@@@@@@@@@@%%%%%%%%%%%-----------" + query);
        String querycreate = "";
        if (!mDomainSpinner.equalsIgnoreCase("All")) {
            if (query.equals("")) {
                querycreate = "sector :'" + mDomainSpinner + "'";
            } else {
                querycreate = "(or sub_sector_displayname: " + "'" + query + "' sub_sector: " + "'" + query + "' specialization: " + "'" + query + "' specialization_displayname: " + "'" + query + "' title: " + "'" + query + "' services: " + "'" + query + "' custom_id: " + "'" + query + "' enc_uid: " + "'" + query + "' qualification: " + "'" + query + "' adwords: " + "'" + query1 + "') sector :'" + mDomainSpinner + "'";
            }
        } else {
            if (!query.equals("")) {
                querycreate = "(or sub_sector_displayname: " + "'" + query + "' sub_sector: " + "'" + query + "' specialization: " + "'" + query + "' specialization_displayname: " + "'" + query + "' title: " + "'" + query + "' services: " + "'" + query + "' custom_id: " + "'" + query + "' enc_uid: " + "'" + query + "' qualification: " + "'" + query + "' adwords: " + "'" + query1 + "')";
            } else {
                querycreate = "";
            }
        }
        // String pass = "haversin(11.751416900900901,75.3701820990991, location1.latitude, location1.longitude)";
        String pass = "haversin(" + latitude + "," + longitude + ", location1.latitude, location1.longitude)";
        Bundle bundle = new Bundle();
      /*  bundle.putString("query", "(and location1:['11.751416900900901,75.3701820990991','9.9496150990991,77.171983900900'] " + querycreate + ")");
        bundle.putString("url", pass);*/
        //VALID QUERY PASS
        bundle.putString("query", "(and location1:" + locationRange + querycreate + ")");
        bundle.putString("url", pass);
        if (Config.isOnline(mContext)) {
            Intent intent = new Intent(mContext, SearchResultsActivity.class);
            bundle.putString("locName", tvLocation.getText().toString());
            bundle.putString("latitude", String.valueOf(latitude));
            bundle.putString("longitude", String.valueOf(longitude));
            bundle.putString("spinnervalue", spinnerTxtPass);
            bundle.putString("selectedDomain", mDomainSpinner);
            bundle.putString("typ", mtyp);
            Config.logV("SEARCH TXT 99999" + mSearchtxt);
            if (!query.equalsIgnoreCase("")) {
                bundle.putString("searchtxt", mSearchtxt);
            } else {
                bundle.putString("searchtxt", "");
            }
            bundle.putString("subdomain_select", "false");
            intent.putExtra("Details", bundle);
            startActivity(intent);
            searchSrcTextView.clearFocus();
            mSearchView.clearFocus();
        }
    }

    public void SpinnerItemCLick() {
        mSearchView.setQuery("", false);
        if (mtyp == null) {
            mtyp = "city";
        }
        LanLong Lanlong = getLocationNearBy(latitude, longitude, mtyp);
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
        String pass = "haversin(" + latitude + "," + longitude + ", location1.latitude, location1.longitude)";
        Bundle bundle = new Bundle();
        bundle.putString("query", "(and location1:" + locationRange + querycreate + ")");
        bundle.putString("url", pass);
        if (Config.isOnline(mContext)) {
            Intent intent = new Intent(mContext, SearchResultsActivity.class);
            bundle.putString("locName", tvLocation.getText().toString());
            bundle.putString("latitude", String.valueOf(latitude));
            bundle.putString("longitude", String.valueOf(longitude));
            bundle.putString("spinnervalue", spinnerTxtPass);
            bundle.putString("selectedDomain", mDomainSpinner);
            Config.logV("SEARCH TXT 99999" + mSearchtxt);
            bundle.putString("searchtxt", mSearchtxt);
            bundle.putString("typ", mtyp);
            bundle.putString("subdomain_select", "false");
            intent.putExtra("Details", bundle);
            startActivity(intent);
            searchSrcTextView.clearFocus();
            mSearchView.clearFocus();
        }
    }


    private synchronized void setUpGClient() {
        if (googleApiClient == null || !googleApiClient.isConnected()) {

            try {
                googleApiClient = new GoogleApiClient.Builder(getActivity())
                        .enableAutoManage(getActivity(), 0, this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
                //SharedPreference.getInstance(mContext).setValue("map_intial","true");
                googleApiClient.connect();
                Config.logV("Google Update Location SetUpFConnected---------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public static boolean UpdateLocation(Double mlatitude, Double mlongitude, String locNme, String typ) {
        Config.logV("UpdateLocation 3333333333----" + mlatitude + " " + mlongitude + "" + locNme);
        try {
            latitude = mlatitude;
            longitude = mlongitude;
            mlocName = locNme;
            mtyp = typ;
            SharedPreference.getInstance(mContext).setValue("lat", latitude);
            SharedPreference.getInstance(mContext).setValue("longitu", longitude);
            SharedPreference.getInstance(mContext).setValue("locnme", mlocName);
            SharedPreference.getInstance(mContext).setValue("typ", mtyp);
            SharedPreference.getInstance(mContext).setValue("locnme", mlocName);
            Config.logV("UpdateLocation 4444----" + mlocName);
            tvLocation.setVisibility(View.VISIBLE);
            if (mlocName.equalsIgnoreCase("")) {
                Config.logV("UpdateLocation banglore");
                latitude = 12.971599;
                longitude = 77.594563;
                mtyp = "state";
                try {
                    Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    tvLocation.setVisibility(View.VISIBLE);
                    tvLocation.setText(addresses.get(0).getLocality());
                    if (mtyp == null) {
                        mtyp = "city";
                    }
                    LanLong Lanlong = getLocationNearBy(latitude, longitude, mtyp);
                    double upperLeftLat = Lanlong.getUpperLeftLat();
                    double upperLeftLon = Lanlong.getUpperLeftLon();
                    double lowerRightLat = Lanlong.getLowerRightLat();
                    double lowerRightLon = Lanlong.getLowerRightLon();
                    String locationRange = "['" + lowerRightLat + "," + lowerRightLon + "','" + upperLeftLat + "," + upperLeftLon + "']";

                    String query = "(and location1:" + locationRange + " " + ")";
                    String url = "haversin(" + latitude + "," + longitude + ", location1.latitude, location1.longitude)";
                    String sort = "claimable asc,distance asc, ynw_verified_level desc";

//                    ApiSEARCHAWSLoadFirstData(query,url,sort);
                    //   Config.logV("Latitude-----11111--------"+addresses.get(0).getAddressLine(0));
                } catch (Exception e) {
                }
            } else {
                tvLocation.setText(mlocName);
            }

        } catch (Exception e) {
        }
        return true;
    }


    public void DefaultLocation() {
        Config.logV("Google DEFAULT LOCATION" + tvLocation.getText().toString());
        //if (mCurrentLoc.getText().toString().equalsIgnoreCase("Locating...")) {
        latitude = 12.971599;
        longitude = 77.594563;
        Config.logV("Not Google DEFAULT LOCATION @@@ YES");
        try {
            if (Config.isOnline(getActivity())) {
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                tvLocation.setVisibility(View.VISIBLE);
                tvLocation.setText(addresses.get(0).getLocality());
                Config.logV("Google DEFAULT LOCATION @@@" + addresses.get(0).getLocality());
            } else {
                tvLocation.setVisibility(View.VISIBLE);
                tvLocation.setText("Bengaluru");
            }
            //   Config.logV("Latitude-----11111--------"+addresses.get(0).getAddressLine(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*} else {
            Config.logV("Not Google DEFAULT LOCATION @@@");
        }*/
    }

    public static LanLong getLocationNearBy(double lant, double longt, String typ) {
        if (typ.equalsIgnoreCase("state")) {
            distance = 300;
        } else if (typ.equalsIgnoreCase("city")) {
            distance = 40;
        } else if (typ.equalsIgnoreCase("area")) {
            distance = 5;
        } else if (typ.equalsIgnoreCase("metro")) {
            distance = 10;
        } else if (typ.equalsIgnoreCase("capital")) {
            distance = 20;
        }
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


    private Location getMyLocation() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                Config.logV("Google api connected granted");
                int permissionLocation = ContextCompat.checkSelfPermission(mContext,
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
//Mani Changed getActivity() -> mContext

                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(mContext,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                                        latitude = mylocation.getLatitude();
                                        longitude = mylocation.getLongitude();
                                        SharedPreference.getInstance(mContext).setValue("lat", latitude);
                                        SharedPreference.getInstance(mContext).setValue("longitu", longitude);
                                        String s_currentLoc = SharedPreference.getInstance(getActivity()).getStringValue("current_loc", "");
                                        if (s_currentLoc.equalsIgnoreCase("yes")) {
                                            if (mtyp == null) {
                                                mtyp = "city";
                                            }
                                            LanLong Lanlong = getLocationNearBy(latitude, longitude, mtyp);
                                            double upperLeftLat = Lanlong.getUpperLeftLat();
                                            double upperLeftLon = Lanlong.getUpperLeftLon();
                                            double lowerRightLat = Lanlong.getLowerRightLat();
                                            double lowerRightLon = Lanlong.getLowerRightLon();
                                            String locationRange = "['" + lowerRightLat + "," + lowerRightLon + "','" + upperLeftLat + "," + upperLeftLon + "']";

                                            String query = "(and location1:" + locationRange + " " + ")";
                                            String url = "haversin(" + latitude + "," + longitude + ", location1.latitude, location1.longitude)";
                                            String sort = "claimable asc,distance asc, ynw_verified_level desc";
                                            searchResultsAdapter.clear();
                                            ApiSEARCHAWSLoadFirstData(query, url, sort);
                                        }

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

        return mylocation;
    }

    public void updateCurrentLocation() {

        setUpGClient();

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

    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;
        Config.logV("Google Update Location Changed Connected---------------------------" + location);
        if (mylocation != null) {
            latitude = mylocation.getLatitude();
            longitude = mylocation.getLongitude();
            SharedPreference.getInstance(mContext).setValue("latitudes", latitude);
            SharedPreference.getInstance(mContext).setValue("longitudes", longitude);
            Log.i("latlatlat", String.valueOf(latitude));
            Log.i("latlatlat", String.valueOf(longitude));
            Config.logV("Update Location Changed Connected---------------------11111------" + location.getLatitude());
            Config.logV("Latitude-------------" + latitude);

            try {
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                tvLocation.setVisibility(View.VISIBLE);
                String s_currentLoc = SharedPreference.getInstance(getActivity()).getStringValue("current_loc", "");
                if (s_currentLoc.equalsIgnoreCase("yes")) {
                    tvLocation.setText(addresses.get(0).getLocality());
                }

            } catch (Exception e) {

            }
            //Or Do whatever you want with your location
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
                        if (requestCode == REQUEST_GET_DATA_FROM_SOME_ACTIVITY) {
                            Bundle extras = data.getExtras();
                            if (mtyp == null) {
                                mtyp = "city";
                            }
                            LanLong Lanlong = getLocationNearBy(latitude, longitude, mtyp);
                            double upperLeftLat = Lanlong.getUpperLeftLat();
                            double upperLeftLon = Lanlong.getUpperLeftLon();
                            double lowerRightLat = Lanlong.getLowerRightLat();
                            double lowerRightLon = Lanlong.getLowerRightLon();
                            String locationRange = "['" + lowerRightLat + "," + lowerRightLon + "','" + upperLeftLat + "," + upperLeftLon + "']";

                            String query = "(and location1:" + locationRange + " " + ")";
                            String url = "haversin(" + latitude + "," + longitude + ", location1.latitude, location1.longitude)";
                            String sort = "claimable asc,distance asc, ynw_verified_level desc";
                            searchResultsAdapter.clear();
                            ApiSEARCHAWSLoadFirstData(query, url, sort);
                        } else {
                            getMyLocation();
                        }
                        break;
                    case RESULT_CANCELED:
                        Config.logV("GPS ON Google Cancelled");
                        //getActivity().finish();
                        if (requestCode == REQUEST_GET_DATA_FROM_SOME_ACTIVITY) {

                            updateCurrentLocation();

                        } else {
                            DefaultLocation();
                        }
                        break;
                }
                break;
        }
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


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //You can display a message here
    }

    public static Fragment getHomeFragment() {
        return home;
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


    public void FunPopularSearch(String sector, String category, String name) {
        String mSector;
        if (mtyp == null) {
            mtyp = "city";
        }
        LanLong Lanlong = getLocationNearBy(latitude, longitude, mtyp);
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
            subdomainquery = "sub_sector:'" + name + "'";
            subdomainName = name;
        }
        if (sector.indexOf("sub_sector_displayname") != -1) {
            querycreate = sector;
        } else {
            if (category.equalsIgnoreCase("Suggested Search")) {
                String requiredString = sector.substring(sector.indexOf("]") + 1, sector.indexOf(")&q"));
//                String requiredString = getQuery("(and [loc_details]",")&q.parser");
                Config.logV("Second---------" + requiredString);
                querycreate = requiredString;
                subdomainquery = "sub_sector:'" + name + "'";
                subdomainName = name;
            }
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
        String pass = "haversin(" + latitude + "," + longitude + ", location1.latitude, location1.longitude)";
        Bundle bundle = new Bundle();
        //VALID QUERY PASS
        if (sector.indexOf("sub_sector_displayname") != -1) { // if keyw
            String requiredString = sector.substring(sector.indexOf("]") + 1, sector.indexOf("))"));
            Config.logV("Second---------" + requiredString);
            querycreate = requiredString + ")";
        }
        bundle.putString("query", "(and location1:" + locationRange + querycreate + ")");
        bundle.putString("url", pass);
        Config.logV("Popular Text__________@@@Del111e");
        mSearchView.setQuery("", false);
        if (Config.isOnline(getActivity())) {
            Intent intent = new Intent(mContext, SearchResultsActivity.class);
            bundle.putString("locName", tvLocation.getText().toString());
            bundle.putString("latitude", String.valueOf(latitude));
            bundle.putString("longitude", String.valueOf(longitude));
            bundle.putString("spinnervalue", spinnerTxtPass);
            bundle.putString("selectedDomain", mDomainSpinner);
            bundle.putString("subdomain_select", "true");
            bundle.putString("subdomainquery", subdomainquery);
            bundle.putString("subdomainName", subdomainName);
            Config.logV("Popular Text_______$$$$_______" + mPopularSearchtxt);
            bundle.putString("searchtxt", mPopularSearchtxt);
            bundle.putString("typ", mtyp);
            intent.putExtra("Details", bundle);
            startActivity(intent);
            searchSrcTextView.clearFocus();
            mSearchView.clearFocus();
        }
    }

    private String getQuery(String s, String s1) {



        return null;
    }

    @Override
    public void selectedPopularSearch(SearchModel popularSearch) {

        FunPopularSearch(popularSearch.getQuery(), "Suggested Search", popularSearch.getName());

    }

    @Override
    public void onMethodCallback(String value, String claimable, String location_id1) {

    }

    @Override
    public void onMethodWorkingCallback(ArrayList<WorkingModel> workingModel, String value, String UniqueID) {

    }

    @Override
    public void onMethodServiceCallback(ArrayList services, String value, String uniqueID, ArrayList serviceIds) {

    }

    @Override
    public void onMethodServiceListCallback(String uniqueID, String value) {

    }

    @Override
    public void onMethodServiceCallbackUser(ArrayList<SearchService> services, String value, String uniqueID) {

    }

    @Override
    public void onMethodOpenMap(String location) {

    }

    @Override
    public void onMethodMessage(String provider, String accountID, String from) {

    }

    @Override
    public void onMethodCoupn(String uniqueID) {

    }

    @Override
    public void onMethodJaldeeLogo(String ynw_verified, String provider) {

    }

    @Override
    public void onMethodFilterRefined(String passformula, RecyclerView recyclepopup, String domainame) {

    }

    @Override
    public void onMethodSubDomainFilter(String passformula, RecyclerView recyclepopup, String subdomainame, String domainName, String displayNameSubdomain) {

    }

    @Override
    public void onMethodQuery(ArrayList<String> formula, ArrayList<String> key, ArrayList<FilterChips> filterChipsList) {

    }

    @Override
    public void onMethodFirstCoupn(String uniqueid) {

    }

    @Override
    public void onMethodJdn(String uniqueid) {

    }

    @Override
    public void onMethodSpecialization(ArrayList<String> Specialization_displayname, String title) {

    }

    @Override
    public void onMethodDepartmentList(ArrayList<String> Departments, String businessName) {

    }

    @Override
    public void onMethodForceUpdate() {

    }
}