package com.nv.youneverwait.Fragment;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nv.youneverwait.R;
import com.nv.youneverwait.activities.SearchLocationActivity;
import com.nv.youneverwait.adapter.PaginationAdapter;
import com.nv.youneverwait.adapter.SearchListAdpter;
import com.nv.youneverwait.callback.AdapterCallback;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.custom.CustomTypefaceSpan;
import com.nv.youneverwait.model.Domain_Spinner;
import com.nv.youneverwait.model.LanLong;
import com.nv.youneverwait.model.ListCell;
import com.nv.youneverwait.model.SearchListModel;
import com.nv.youneverwait.model.SearchModel;
import com.nv.youneverwait.model.WorkingModel;
import com.nv.youneverwait.response.QueueList;
import com.nv.youneverwait.response.SearchAWsResponse;
import com.nv.youneverwait.response.SearchTerminology;
import com.nv.youneverwait.utils.EmptySubmitSearchView;
import com.nv.youneverwait.utils.PaginationScrollListener;
import com.nv.youneverwait.utils.SharedPreference;
import com.nv.youneverwait.widgets.CustomDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 20/7/18.
 */

public class SearchListFragment extends RootFragment implements AdapterCallback {

    static Context mContext;
    String query, url;
    RecyclerView mRecySearchDetail;
    // SearchDetailAdapter adapter;
    List<SearchAWsResponse> mSearchResp = new ArrayList<>();
    List<QueueList> mQueueList = new ArrayList<>();
    List<SearchListModel> mSearchListModel = new ArrayList<>();
    ProgressBar progressBar;
    LinearLayoutManager linearLayoutManager;
    PaginationAdapter pageadapter;
    private int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = 0;
    private int currentPage = PAGE_START;
    int total_foundcount = 0;
    Fragment searchDetail;
    ArrayList<Domain_Spinner> domainList = new ArrayList<>();
    ArrayList<SearchModel> mGLobalSearch = new ArrayList<>();
    ArrayList<SearchModel> mSubDomainSubSearch = new ArrayList<>();
    ArrayList<SearchModel> mSubDomain = new ArrayList<>();
    ArrayList<SearchModel> mSpecializationDomain = new ArrayList<>();
    ArrayList<SearchModel> mSpecializationDomainSearch = new ArrayList<>();
    Spinner mSpinnerDomain;
    EmptySubmitSearchView mSearchView;
    SearchView.SearchAutoComplete searchSrcTextView;
    String mDomainSpinner;
    SearchListAdpter listadapter;
    ArrayList<ListCell> items;
    AdapterCallback mInterface;
    static String latitude;
    static String longitude;
    String searchTxt, spinnerTxt;
    String getQuery_previous = "";

    public void refreshQuery() {
        getQuery_previous = "true";
    }

    static TextView txt_toolbarlocation;
    ImageView ibackpress;
    String s_LocName;
    TextView tv_nosearchresult, tv_searchresult;
    LinearLayout Lnosearchresult;
    boolean userIsInteracting;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // do something when visible.
            userIsInteracting = false;
            Config.logV(" Search List Spinner ITEM NOT CLICKED @@@@@@@@@@@@@@@@" + userIsInteracting);
        }
    }


    public static int dpToPx(int dp) {
        float density = mContext.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View row = inflater.inflate(R.layout.fragment_searchdetail, container, false);

        Config.logV(" Search List &&&&&&&&&&&&&&&&");
        mContext = getActivity();
        mInterface = (AdapterCallback) this;
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (!getQuery_previous.equalsIgnoreCase("true")) {
                query = bundle.getString("query", "");
                url = bundle.getString("url", "");
                spinnerTxt = bundle.getString("spinnervalue", "");
                searchTxt = bundle.getString("searchtxt", "");
            }
            latitude = bundle.getString("latitude", "");
            longitude = bundle.getString("longitude", "");

            s_LocName = bundle.getString("locName", "");
        }
        userIsInteracting = false;

        Config.logV("LATITUDE--------------------------------" + latitude + ", " + longitude);

        isLastPage = false;
        isLoading = false;
        PAGE_START = 0;
        total_foundcount = 0;
        TOTAL_PAGES = 0;
        currentPage = PAGE_START;


        Typeface tyface = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Montserrat_Bold.otf");


        ibackpress = (ImageView) row.findViewById(R.id.backpress);
        mRecySearchDetail = (RecyclerView) row.findViewById(R.id.SearchDetail);
        txt_toolbarlocation = (TextView) row.findViewById(R.id.txt_toolbarlocation);
        tv_searchresult = (TextView) row.findViewById(R.id.searchresult);

        Lnosearchresult = (LinearLayout) row.findViewById(R.id.Lnosearchresult);
        tv_nosearchresult = (TextView) row.findViewById(R.id.txtnosearchresult);

        txt_toolbarlocation.setTypeface(tyface);
        txt_toolbarlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iLoc = new Intent(mContext, SearchLocationActivity.class);
                iLoc.putExtra("from", "searchlist");
                mContext.startActivity(iLoc);
            }
        });


        try {

            Config.logV("LATITUDE------------@@@@--------------------" + latitude + ", " + longitude);

            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);

            // Config.logV("Latitude-----11111--------" + addresses.get(0).getLocality());
            if (!s_LocName.equalsIgnoreCase("")) {
                txt_toolbarlocation.setVisibility(View.VISIBLE);
                // txt_toolbarlocation.setText(addresses.get(0).getLocality());
                txt_toolbarlocation.setText(s_LocName);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }


        ibackpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // perform whatever you want on back arrow click
                Config.logV("BackPress-----------");
                getFragmentManager().popBackStack();
            }
        });


        searchDetail = new DashboardFragment().getHomeFragment();


        Config.logV("Fragment Context--43343---" + searchDetail);
        progressBar = (ProgressBar) row.findViewById(R.id.main_progress);
        //  Config.logV("Pass Fragment" + searchDetail);
        mSearchView = (EmptySubmitSearchView) row.findViewById(R.id.search);


        pageadapter = new PaginationAdapter(getActivity(), mSearchView, getActivity(), searchDetail, this);

        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecySearchDetail.setLayoutManager(linearLayoutManager);

        mRecySearchDetail.setItemAnimator(new DefaultItemAnimator());

        mRecySearchDetail.setAdapter(pageadapter);


        mRecySearchDetail.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {


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

        //init service and load data

        Config.logV("SearchList URL ONCREATEVIEW@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

        ApiSEARCHAWSLoadFirstData(query, url);


        APiSearchList();
        //SearchView******************************************************************


        mSpinnerDomain = (Spinner) row.findViewById(R.id.spinnerdomain);

        ArrayAdapter<Domain_Spinner> adapter = new ArrayAdapter<Domain_Spinner>(getActivity(), R.layout.spinner_item, domainList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        //    mSpinnerDomain.setAdapter(adapter);

        mSpinnerDomain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                searchSrcTextView.setText("");
                //tv_searchresult.setVisibility(View.GONE);
                mDomainSpinner = ((Domain_Spinner) mSpinnerDomain.getSelectedItem()).getDomain();

                spinnerTxt = ((Domain_Spinner) mSpinnerDomain.getSelectedItem()).getDisplayName();
                Config.logV("Selected---423333333333--------" + mDomainSpinner);


                boolean FirstRun = SharedPreference.getInstance(mContext).getBoolanValue("ALL_SELECTED", false);


                if (userIsInteracting) {
                    Config.logV("Spinner ITEM CLICKED $$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                    SpinnerItemClick();
                }
                userIsInteracting = true;


                if (FirstRun) {

                    int pos = getIndex(domainList, spinnerTxt);
                    mSpinnerDomain.setSelection(pos);
                    SharedPreference.getInstance(mContext).setValue("ALL_SELECTED", false);

                    mSearchView.setQuery(searchTxt, false);

                    Config.logV("Selected  FIRST RUN-----------------------" + searchTxt);
                    if (!searchTxt.equalsIgnoreCase("")) {
                        ImageView searchIcon = (ImageView) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
                        searchIcon.setImageDrawable(null);
                        searchIcon.setVisibility(View.GONE);
                    }
                } else {

                  /*  Config.logV("Selected NOT FIRST RUN-----------------------" + searchTxt);
                    isLastPage = false;
                    isLoading = false;
                    PAGE_START = 0;
                    total_foundcount = 0;
                    TOTAL_PAGES = 0;
                    currentPage = PAGE_START;
                    searchSrcTextView.setText("");
                    pageadapter.clear();
                    Lnosearchresult.setVisibility(View.VISIBLE);
                    tv_nosearchresult.setVisibility(View.VISIBLE);
                    tv_searchresult.setVisibility(View.GONE);
                    mRecySearchDetail.setVisibility(View.GONE);
                    tv_nosearchresult.setText("No search result found for this location");
                    progressBar.setVisibility(View.GONE);*/

                }


                if (mDomainSpinner.equalsIgnoreCase("ALL")) {

                    /**********************************HEADER=SuGGESTED SEARCH*************************************/

                    Config.logV("mGLobalSearch" + mGLobalSearch.size());

                               /* ArrayList<ListCell>*/
                    items = new ArrayList<ListCell>();
                    for (int i = 0; i < mGLobalSearch.size(); i++) {

                        items.add(new ListCell(mGLobalSearch.get(i).getName(), "Suggested Search", mGLobalSearch.get(i).getQuery(), mGLobalSearch.get(i).getDisplayname()));
                    }


                    for (int i = 0; i < mSpecializationDomain.size(); i++) {
                        // Config.logV("mSectorSubSearch.get(i).getName()" + mSectorSubSearch.get(i).getName());
                        items.add(new ListCell(mSpecializationDomain.get(i).getName(), "Specializations", mSpecializationDomain.get(i).getSector(), mSpecializationDomain.get(i).getDisplayname()));
                    }

                           /*     *******************************************************************/

                    listadapter = new SearchListAdpter("detail", getActivity(), items, Double.valueOf(latitude), Double.valueOf(longitude), DashboardFragment.getHomeFragment(), mSearchView);
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


                            /*boolean FirstRun=SharedPreference.getInstance(mContext).getBoolanValue("firsttime",false);
                            if(FirstRun){
                                mSearchView.setQuery(searchTxt, false);
                            }*/

                            if (!query.equalsIgnoreCase("")) {
                                searchTxt = query;

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

                            SearchModel search = new SearchModel();
                            search.setDisplayname(mSubDomain.get(i).getDisplayname());
                            search.setSector(mSubDomain.get(i).getSector());
                            search.setName(mSubDomain.get(i).getName());
                            search.setQuery(mSubDomain.get(i).getQuery());
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


                    listadapter = new SearchListAdpter("detail", getActivity(), items, Double.valueOf(latitude), Double.valueOf(longitude), DashboardFragment.getHomeFragment(), mSearchView);
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

                            /*boolean FirstRun=SharedPreference.getInstance(mContext).getBoolanValue("firsttime",false);
                            if(FirstRun){
                                Config.logV("Query-------------"+searchTxt);
                                mSearchView.setQuery(searchTxt, false);
                            }*/

                            if (!query.equalsIgnoreCase("")) {
                                searchTxt = query;

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


        ////////////////////////////SEARCH//////////////////////////////////////////////


        searchSrcTextView = (SearchView.SearchAutoComplete) row.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        SearchManager searchMng = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchMng.getSearchableInfo(getActivity().getComponentName()));
        searchSrcTextView.setDropDownHeight(450);



        /*searchSrcTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    Config.logV("QUEry @@@@@@@@@@@@@@@@@@@@@@");
                    searchSrcTextView.showDropDown();
                }
            }
        });
*/
      /* searchSrcTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.logV("On Clickkkkkkkkkkkkkkkkkkkkkkkkkk");
               *//* listadapter = new SearchListAdpter("detail", getActivity(), items, Double.valueOf(latitude), Double.valueOf(longitude), DashboardFragment.getHomeFragment(), mSearchView);
                searchSrcTextView.setAdapter(listadapter);
                listadapter.notifyDataSetChanged();
                listadapter.getFilter().filter("a");*//*
                Config.logV("Filter ########@@WWWW@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

                SearchListAdpter listadapter1 = new SearchListAdpter("detail", getActivity(), items, Double.valueOf(latitude), Double.valueOf(longitude), DashboardFragment.getHomeFragment(), mSearchView);
                searchSrcTextView.setAdapter(listadapter1);
                listadapter1.getFilter().filter(null);

                listadapter1.notifyDataSetChanged();

            }
        });*/


        ImageView searchClose = (ImageView) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        searchClose.setImageResource(R.drawable.icon_cancel);

        Config.logV("Search TEXT--------9644644--------" + searchTxt);


        // mSearchView.setQuery(searchTxt, false);
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


        Config.logV("SEARCH TXT-----------------------" + searchTxt);


        searchSrcTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListCell cell = listadapter.getItem(position);


                //  mSearchView.setQuery("", false);
                String mSector;
                LanLong Lanlong = getLocationNearBy(Double.parseDouble(latitude), Double.parseDouble(longitude));
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


                isLastPage = false;
                isLoading = false;
                PAGE_START = 0;
                total_foundcount = 0;
                TOTAL_PAGES = 0;
                currentPage = PAGE_START;


                searchTxt = cell.getMdisplayname();
                mSearchView.setQuery(searchTxt, false);


                pageadapter.clear();


                /*pageadapter = new PaginationAdapter(mSearchView,getActivity(), searchDetail,mInterface);
                //pageadapter.clear();

                linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                mRecySearchDetail.setLayoutManager(linearLayoutManager);

                mRecySearchDetail.setItemAnimator(new DefaultItemAnimator());

                mRecySearchDetail.setAdapter(pageadapter);*/


              /*  mRecySearchDetail.smoothScrollToPosition(0);*/


                Config.logV("Query-----------" + querycreate);

                // final String query1 = "(and location1:['11.751416900900901,75.3701820990991','9.9496150990991,77.171983900900'] " + querycreate + ")";

                final String query1 = "(and location1:" + locationRange + querycreate + ")";
                //  final String pass1 = "haversin(11.751416900900901,75.3701820990991, location1.latitude, location1.longitude)";
                final String pass1 = "haversin(" + latitude + "," + longitude + ", location1.latitude, location1.longitude)";

                query = query1;
                url = pass1;

                Config.logV("MAin PAge&&&&&&&&&&&&Item CLICK &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&7" + query);

                Config.logV("SearchList URLSPINNER ITEMCLICK@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

                ApiSEARCHAWSLoadFirstData(query, url);


            }
        });

        return row;
    }

    public LanLong getLocationNearBy(double lant, double longt) {

        double distance = 30;/*;DISTANCE_AREA: 5, // in Km*/

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
            query.put("fq", "(and  test_account:1 )");
        } else {
            query.put("fq", "(and  (not test_account:1) )");
        }


        Map<String, String> params = new HashMap<>();

        params.put("size", "10");
        params.put("q.parser", "structured");
        params.put("sort", "ynw_verified_level desc, distance asc");

        params.put("expr.distance", mPass);


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


                        Config.logV("Response--Body AWS-------------------------" + new Gson().toJson(response.body()));

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

                                search.setLocation1(response.body().getHits().getHit().get(i).getFields().getLocation1());

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

                                if (response.body().getHits().getHit().get(i).getFields().getBusiness_hours1() != null) {
                                    search.setBusiness_hours1(response.body().getHits().getHit().get(i).getFields().getBusiness_hours1());
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
                                if (response.body().getHits().getHit().get(i).getFields().getFirstaid_location1() != null) {
                                    search.setFirstaid_location1(response.body().getHits().getHit().get(i).getFields().getFirstaid_location1());
                                }


                                mSearchResp.add(search);

                                ids.add(response.body().getHits().getHit().get(i).getId());


                            }




                           /* Config.logV("TOTAL PAGES_--------------" + TOTAL_PAGES);
                            Config.logV("CURRENT PAGE**22222**555***********" + TOTAL_PAGES);
                            Config.logV("CURRENT PAGE**333*****5555********" + currentPage);
                            pageadapter.removeLoadingFooter();
                            isLoading = false;

                            List<SearchAWsResponse> results = mSearchResp;
                            pageadapter.addAll(results);

                            if (currentPage / 10 != TOTAL_PAGES) {
                                pageadapter.addLoadingFooter();
                            } else {
                                isLastPage = true;
                            }*/

                            ApiQueueList(ids, mSearchResp, "next");
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


   /* */

    /**
     * Performs a Retrofit call to the top rated movies API.
     * Same API call for Pagination.
     * As {@link #currentPage} will be incremented automatically
     * by @{@link PaginationScrollListener} to load next page.
     *//*
    private Call<TopRatedMovies> callTopRatedMoviesApi() {
        return movieService.getTopRatedMovies(
                getString(R.string.my_api_key),
                "en_US",
                currentPage
        );
    }*/
    public void ApiSEARCHAWSLoadFirstData(String mQueryPass, String mPass) {


        final ApiInterface apiService =
                ApiClient.getClientAWS(mContext).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(getActivity(), getActivity().getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Map<String, String> query = new HashMap<>();

        query.put("start", "0");
        query.put("q", mQueryPass);

        String mobile = SharedPreference.getInstance(mContext).getStringValue("mobile", "");
        if (mobile.startsWith("55")) {
            query.put("fq", "(and  test_account:1 )");
        } else {
            query.put("fq", "(and  (not test_account:1) )");
        }


        Map<String, String> params = new HashMap<>();

        params.put("size", "10");
        params.put("q.parser", "structured");
        params.put("sort", "ynw_verified_level desc, distance asc");
        params.put("expr.distance", mPass);

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


                        Config.logV("Response--Body AWS-------------------------" + new Gson().toJson(response.body()));

                        Config.logV("Status" + response.body().getStatus().getRid());

                        Config.logV("Found @@@@@@@@@@@@@@@@@@" + response.body().getHits().getFound());
                        total_foundcount = response.body().getHits().getFound();
                        TOTAL_PAGES = response.body().getHits().getFound() / 10;
                        if (response.body().getHits().getFound() > 0) {

                            tv_nosearchresult.setVisibility(View.GONE);
                            mRecySearchDetail.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                            Lnosearchresult.setVisibility(View.GONE);
                            tv_searchresult.setVisibility(View.VISIBLE);
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
                            tv_searchresult.setText(spannable);


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


                                //7 types

                                if (response.body().getHits().getHit().get(i).getFields().getBusiness_hours1() != null) {
                                    search.setBusiness_hours1(response.body().getHits().getHit().get(i).getFields().getBusiness_hours1());
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
                                if (response.body().getHits().getHit().get(i).getFields().getFirstaid_location1() != null) {
                                    search.setFirstaid_location1(response.body().getHits().getHit().get(i).getFields().getFirstaid_location1());
                                }

                                ids.add(response.body().getHits().getHit().get(i).getId());

                                mSearchResp.add(search);
                            }


                           /* List<SearchAWsResponse> results = mSearchResp;
                            progressBar.setVisibility(View.GONE);
                            pageadapter.addAll(results);

                            Config.logV("CURRENT PAGE**22222*************" + TOTAL_PAGES);
                            Config.logV("CURRENT PAGE**333*************" + currentPage);
                            if (TOTAL_PAGES > 0) {
                                pageadapter.addLoadingFooter();
                            } else {
                                isLastPage = true;
                            }*/

                            ApiQueueList(ids, mSearchResp, "first");


                            //   waitlist/queues/waitingTime/2-1%2C2-2%2C141-388%2C141-2563

                        } else {
                            Config.logV(" No Found @@@@@@@@@@@@@@@@@@");
                            Lnosearchresult.setVisibility(View.VISIBLE);
                            tv_nosearchresult.setVisibility(View.VISIBLE);
                            mRecySearchDetail.setVisibility(View.GONE);
                            tv_nosearchresult.setText("No search result found for this location");
                            progressBar.setVisibility(View.GONE);
                            tv_searchresult.setVisibility(View.GONE);


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

    private void ApiQueueList(ArrayList<String> queuelist, final List<SearchAWsResponse> mSearchRespPass, final String mCheck) {

        ApiInterface apiService =
                ApiClient.getClient(getActivity()).create(ApiInterface.class);

       /* final Dialog mDialog = Config.getProgressDialog(getActivity(), getActivity().getResources().getString(R.string.dialog_log_in));
        mDialog.show();*/
        StringBuilder csvBuilder = new StringBuilder();
        for (String data : queuelist) {
            csvBuilder.append(data);
            csvBuilder.append(",");
        }
        String csv = csvBuilder.toString();
        System.out.println(csv);

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
                            QueueList que = new QueueList();
                            que.setId(response.body().get(i).getProvider().getId());

                            if (response.body().get(i).getNextAvailableQueue() != null) {
                                que.setLocation(response.body().get(i).getNextAvailableQueue().getLocation());
                                //  Config.logV("Available Time----1111---"+response.body().get(i).getNextAvailableQueue().getAvailableDate());
                                que.setAvailableDate(response.body().get(i).getNextAvailableQueue().getAvailableDate());
                                que.setOpenNow(response.body().get(i).getNextAvailableQueue().isOpenNow());
                                if (response.body().get(i).getNextAvailableQueue().getServiceTime() != null) {
                                    que.setServiceTime(response.body().get(i).getNextAvailableQueue().getServiceTime());
                                }

                                que.setQueueWaitingTime(response.body().get(i).getNextAvailableQueue().getQueueWaitingTime());


                            }


                            mQueueList.add(que);
                        }

                        if (mCheck.equalsIgnoreCase("next")) {

                            Config.logV("TOTAL PAGES_--------------" + TOTAL_PAGES);
                            Config.logV("CURRENT PAGE**22222**555***********" + TOTAL_PAGES);
                            Config.logV("CURRENT PAGE**333*****5555********" + currentPage);
                            pageadapter.removeLoadingFooter();
                            isLoading = false;


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


                                searchList.setSectorname(mSearchRespPass.get(i).getSector());
                                searchList.setSub_sector(mSearchRespPass.get(i).getSub_sector());


                                searchList.setLocation1(mSearchRespPass.get(i).getLocation1());
                                String spec = "";
                                if (mSearchRespPass.get(i).getSpecialization_displayname() != null) {
                                    for (int l = 0; l < mSearchRespPass.get(i).getSpecialization_displayname().size(); l++) {
                                        if (!spec.equalsIgnoreCase("")) {
                                            spec = spec + ", " + mSearchRespPass.get(i).getSpecialization_displayname().get(l);
                                        } else {
                                            spec = spec + mSearchRespPass.get(i).getSpecialization_displayname().get(l);
                                        }
                                    }
                                    searchList.setSpecialization_displayname(spec);
                                }


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

                                if (mSearchRespPass.get(i).getYnw_verified_level() != null) {
                                    searchList.setYnw_verified_level(mSearchRespPass.get(i).getYnw_verified_level());

                                }


                                if (mSearchRespPass.get(i).getServices() != null) {
                                    searchList.setServices(mSearchRespPass.get(i).getServices());
                                }

                                if (mSearchRespPass.get(i).getBusiness_hours1() != null) {
                                    searchList.setBusiness_hours1(mSearchRespPass.get(i).getBusiness_hours1());
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

                                //7types

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
                                if (mSearchRespPass.get(i).getFirstaid_location1() != null) {
                                    searchList.setFirstaid_location1(mSearchRespPass.get(i).getFirstaid_location1());
                                }


                                for (int j = 0; j < mQueueList.size(); j++) {
                                    Config.logV("mQueueList.get(j).getLocation().getId()" + mQueueList.get(j).getLocation());
                                    if (mQueueList.get(j).getLocation() != null) {

                                        String json = new Gson().toJson(mQueueList.get(j).getLocation());
                                        JSONObject json1 = new JSONObject(json);

                                        String QID = json1.getString("id");


                                        String mID = mQueueList.get(j).getId() + "-" + mQueueList.get(j).getLocation().getId();
                                        Config.logV("QID----mmm-------------------" + mID + "compare-------" + mSearchRespPass.get(i).getId());
                                        if (mID.equalsIgnoreCase(mSearchRespPass.get(i).getId())) {
                                            if (mQueueList.get(j).getAvailableDate() != null) {

                                                searchList.setAvail_date(mQueueList.get(j).getAvailableDate());
                                            }
                                            if (mQueueList.get(j).getLocation() != null) {
                                                searchList.setmLoc(QID);
                                            }
                                            //searchList.setQId(mQueueList.get(j).getId());
                                            searchList.setQId(mID);
                                            searchList.setIsopen(mQueueList.get(i).isOpenNow());
                                            searchList.setQueueWaitingTime(mQueueList.get(i).getQueueWaitingTime());
                                            if (mQueueList.get(i).getServiceTime() != null) {
                                                searchList.setServiceTime(mQueueList.get(i).getServiceTime());
                                            }

                                        }
                                    }
                                }


                                mSearchListModel.add(searchList);
                            }


                            List<SearchListModel> results = mSearchListModel;
                            pageadapter.addAll(results);
                            pageadapter.notifyDataSetChanged();

                            if (currentPage / 10 != TOTAL_PAGES) {
                                pageadapter.addLoadingFooter();
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

                                searchList.setSectorname(mSearchRespPass.get(i).getSector());
                                searchList.setSub_sector(mSearchRespPass.get(i).getSub_sector());

                                String spec = "";
                                if (mSearchRespPass.get(i).getSpecialization_displayname() != null) {
                                    for (int l = 0; l < mSearchRespPass.get(i).getSpecialization_displayname().size(); l++) {
                                        if (!spec.equalsIgnoreCase("")) {
                                            spec = spec + ", " + mSearchRespPass.get(i).getSpecialization_displayname().get(l);
                                        } else {
                                            spec = spec + mSearchRespPass.get(i).getSpecialization_displayname().get(l);
                                        }
                                    }
                                    searchList.setSpecialization_displayname(spec);
                                }


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

                                if (mSearchRespPass.get(i).getYnw_verified_level() != null) {
                                    searchList.setYnw_verified_level(mSearchRespPass.get(i).getYnw_verified_level());

                                }


                                if (mSearchRespPass.get(i).getServices() != null) {
                                    searchList.setServices(mSearchRespPass.get(i).getServices());
                                }

                                if (mSearchRespPass.get(i).getBusiness_hours1() != null) {
                                    searchList.setBusiness_hours1(mSearchRespPass.get(i).getBusiness_hours1());
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
                                if (mSearchRespPass.get(i).getFirstaid_location1() != null) {
                                    searchList.setFirstaid_location1(mSearchRespPass.get(i).getFirstaid_location1());
                                }


                                for (int j = 0; j < mQueueList.size(); j++) {
                                    //Config.logV("mQueueList.get(j).getLocation().getId()"+mQueueList.get(j).getLocation());
                                    if (mQueueList.get(j).getLocation() != null) {

                                        String json = new Gson().toJson(mQueueList.get(j).getLocation());
                                        JSONObject json1 = new JSONObject(json);

                                        String QID = json1.getString("id");


                                        String mID = mQueueList.get(j).getId() + "-" + mQueueList.get(j).getLocation().getId();
                                        //Config.logV("QID----mmm-------------------"+mID+"compare-------"+mSearchRespPass.get(i).getId());
                                        if (mID.equalsIgnoreCase(mSearchRespPass.get(i).getId())) {
                                            if (mQueueList.get(j).getAvailableDate() != null) {

                                                searchList.setAvail_date(mQueueList.get(j).getAvailableDate());
                                            }
                                            if (mQueueList.get(j).getLocation() != null) {
                                                searchList.setmLoc(QID);
                                            }
                                            searchList.setQId(mID);
                                            searchList.setIsopen(mQueueList.get(i).isOpenNow());
                                            searchList.setQueueWaitingTime(mQueueList.get(i).getQueueWaitingTime());
                                            if (mQueueList.get(i).getServiceTime() != null) {
                                                searchList.setServiceTime(mQueueList.get(i).getServiceTime());
                                            }
                                        }
                                    }
                                }


                                mSearchListModel.add(searchList);
                            }


                            final List<SearchListModel> results = mSearchListModel;
                            progressBar.setVisibility(View.GONE);
                            pageadapter.addAll(results);
                            pageadapter.notifyDataSetChanged();


                            Config.logV("Results@@@@@@@@@@@@@@@@@" + results.size());
                            Config.logV("CURRENT PAGE**22222*************" + TOTAL_PAGES);
                            Config.logV("CURRENT PAGE**333*************" + currentPage);
                            if (TOTAL_PAGES > 0 && total_foundcount > 10) {
                                Config.logV("First ADD Footer");
                                pageadapter.addLoadingFooter();


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

    private int getIndex(ArrayList<Domain_Spinner> spinner, String myString) {

        int index = 0;

        for (int i = 0; i < spinner.size(); i++) {
            if (spinner.get(i).getDisplayName().equals(myString)) {
                index = i;
            }
        }
        return index;
    }

    @Override
    public void onMethodCallback(String value) {

        Bundle bundle = new Bundle();

        SearchDetailViewFragment pfFragment = new SearchDetailViewFragment();

        refreshQuery();
        bundle.putString("uniqueID", value);
        pfFragment.setArguments(bundle);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        // Store the Fragment in stack
        transaction.addToBackStack(null);
        transaction.replace(R.id.mainlayout, pfFragment).commit();

    }

    @Override
    public void onMethodWorkingCallback(ArrayList<WorkingModel> workingModelArrayList, String value, String uniqueid) {
        WorkingHourFragment pfFragment = new WorkingHourFragment();

        refreshQuery();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("workinghrlist", workingModelArrayList);
        bundle.putString("title", value);
        bundle.putString("uniqueID", uniqueid);
        pfFragment.setArguments(bundle);
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        // Store the Fragment in stack
        transaction.addToBackStack(null);
        transaction.replace(R.id.mainlayout, pfFragment).commit();
    }

    @Override
    public void onMethodServiceCallback(ArrayList services, String value, String uniqueid) {
        ServiceListFragment pfFragment = new ServiceListFragment();
        refreshQuery();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("servicelist", services);
        bundle.putString("title", value);
        bundle.putString("from", "searchlist");
        bundle.putString("uniqueID", uniqueid);
        pfFragment.setArguments(bundle);
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        // Store the Fragment in stack
        transaction.addToBackStack(null);
        transaction.replace(R.id.mainlayout, pfFragment).commit();
    }

    @Override
    public void onMethodOpenMap(String location) {


        String geoUri = "http://maps.google.com/maps?q=loc:" + location;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
        mContext.startActivity(intent);
    }


    public void QuerySubmitCLick(String querypass) {

        //  mSearchView.setQuery("", false);
        LanLong Lanlong = getLocationNearBy(Double.parseDouble(latitude), Double.parseDouble(longitude));
        double upperLeftLat = Lanlong.getUpperLeftLat();
        double upperLeftLon = Lanlong.getUpperLeftLon();
        double lowerRightLat = Lanlong.getLowerRightLat();
        double lowerRightLon = Lanlong.getLowerRightLon();
        String locationRange = "['" + lowerRightLat + "," + lowerRightLon + "','" + upperLeftLat + "," + upperLeftLon + "']";
        //  String querycreate = "(phrase " + "'" + query + "')";

        String querycreate = null;

        if (!mDomainSpinner.equalsIgnoreCase("All")) {

            for (int i = 0; i < mSubDomain.size(); i++) {
                if (mSubDomain.get(i).getDisplayname().toLowerCase().equalsIgnoreCase(querypass.toLowerCase())) {

                    Config.logV("Query------------" + mSubDomain.get(i).getQuery());
                    String requiredString = mSubDomain.get(i).getQuery().substring(mSubDomain.get(i).getQuery().indexOf("]") + 1, mSubDomain.get(i).getQuery().indexOf(")"));
                    Config.logV("Second----@@@@@@-----" + requiredString);
                    querycreate = requiredString;

                }
            }
            Config.logV("Query @@@@@@@@@@-----------" + querycreate);
        } else {
            for (int i = 0; i < mGLobalSearch.size(); i++) {
                if (mGLobalSearch.get(i).getDisplayname().toLowerCase().equalsIgnoreCase(querypass.toLowerCase())) {
                    Config.logV("Query-ALL-----------" + mGLobalSearch.get(i).getQuery());
                    String requiredString = mGLobalSearch.get(i).getQuery().substring(mGLobalSearch.get(i).getQuery().indexOf("]") + 1, mGLobalSearch.get(i).getQuery().indexOf(")"));
                    Config.logV("Second---All-@@@@@@-----" + requiredString);
                    querycreate = requiredString;
                }
            }
            Config.logV("Query  ALL @@@@@@@@@@-----------" + querycreate);
        }


        if (querycreate == null) {
            if (!mDomainSpinner.equalsIgnoreCase("All")) {
                querycreate = "(phrase " + "'" + querypass + "') sector :'" + mDomainSpinner + "'";
            } else {
                querycreate = "(phrase " + "'" + querypass + "')";
            }
        }


        isLastPage = false;
        isLoading = false;
        PAGE_START = 0;
        total_foundcount = 0;
        TOTAL_PAGES = 0;
        currentPage = PAGE_START;
        pageadapter.clear();


        Config.logV("Query-----------" + querycreate);

     /*   final String query1 = "(and location1:['11.751416900900901,75.3701820990991','9.9496150990991,77.171983900900'] " + querycreate + ")";

        final String pass1 = "haversin(11.751416900900901,75.3701820990991, location1.latitude, location1.longitude)";*/


        final String query1 = "(and location1:" + locationRange + querycreate + ")";

        final String pass1 = "haversin(" + latitude + "," + longitude + ", location1.latitude, location1.longitude)";

        query = query1;
        url = pass1;

        Config.logV("MAin PAge&&&&&&&&&&&&Item CLICK &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&7" + query);
        Config.logV("SearchList URL QUERYSUBMIT@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

        ApiSEARCHAWSLoadFirstData(query, url);


    }


    public void SpinnerItemClick() {

        //  mSearchView.setQuery("", false);
        LanLong Lanlong = getLocationNearBy(Double.parseDouble(latitude), Double.parseDouble(longitude));
        double upperLeftLat = Lanlong.getUpperLeftLat();
        double upperLeftLon = Lanlong.getUpperLeftLon();
        double lowerRightLat = Lanlong.getLowerRightLat();
        double lowerRightLon = Lanlong.getLowerRightLon();
        String locationRange = "['" + lowerRightLat + "," + lowerRightLon + "','" + upperLeftLat + "," + upperLeftLon + "']";
        //  String querycreate = "(phrase " + "'" + query + "')";

        /*String querycreate = null;

        if (!mDomainSpinner.equalsIgnoreCase("All")) {

            for (int i=0;i<mSubDomain.size();i++ ){
                if(mSubDomain.get(i).getDisplayname().toLowerCase().equalsIgnoreCase(querypass.toLowerCase())){

                    Config.logV("Query------------" + mSubDomain.get(i).getQuery());
                    String requiredString = mSubDomain.get(i).getQuery().substring(mSubDomain.get(i).getQuery().indexOf("]") + 1, mSubDomain.get(i).getQuery().indexOf(")"));
                    Config.logV("Second----@@@@@@-----" + requiredString);
                    querycreate = requiredString;

                }
            }
            Config.logV("Query @@@@@@@@@@-----------" + querycreate);
        }else{
            for (int i=0;i<mGLobalSearch.size();i++ ){
                if(mGLobalSearch.get(i).getDisplayname().toLowerCase().equalsIgnoreCase(querypass.toLowerCase())){
                    Config.logV("Query-ALL-----------" + mGLobalSearch.get(i).getQuery());
                    String requiredString = mGLobalSearch.get(i).getQuery().substring(mGLobalSearch.get(i).getQuery().indexOf("]") + 1, mGLobalSearch.get(i).getQuery().indexOf(")"));
                    Config.logV("Second---All-@@@@@@-----" + requiredString);
                    querycreate = requiredString;
                }
            }
            Config.logV("Query  ALL @@@@@@@@@@-----------" + querycreate);
        }*/


        /*if (querycreate==null) {
            if (!mDomainSpinner.equalsIgnoreCase("All")) {
                querycreate = "(phrase " + "'" + querypass + "') sector :'" + mDomainSpinner + "'";
            } else {
                querycreate = "(phrase " + "'" + querypass + "')";
            }
        }*/

        String querycreate = "";
        if (!mDomainSpinner.equalsIgnoreCase("All")) {
            querycreate = "sector :'" + mDomainSpinner + "'";
        } else {
            querycreate = " ";
        }


        isLastPage = false;
        isLoading = false;
        PAGE_START = 0;
        total_foundcount = 0;
        TOTAL_PAGES = 0;
        currentPage = PAGE_START;
        pageadapter.clear();


        Config.logV("Query-----------" + querycreate);

     /*   final String query1 = "(and location1:['11.751416900900901,75.3701820990991','9.9496150990991,77.171983900900'] " + querycreate + ")";

        final String pass1 = "haversin(11.751416900900901,75.3701820990991, location1.latitude, location1.longitude)";*/


        final String query1 = "(and location1:" + locationRange + querycreate + ")";

        final String pass1 = "haversin(" + latitude + "," + longitude + ", location1.latitude, location1.longitude)";

        query = query1;
        url = pass1;

        Config.logV("MAin PAge&&&&&&&&&&&&Item CLICK &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&7" + query);
        Config.logV("SearchList URL ITEM CLICK SPINNER 222@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

        ApiSEARCHAWSLoadFirstData(query, url);


    }


    public static boolean UpdateLocationSearch(String mlatitude, String mlongitude, String locNme) {
        Config.logV("UpdateLocation 3333333333----" + mlatitude + " " + mlongitude);
        try {
            latitude = mlatitude;
            longitude = mlongitude;

            // mlocName = locNme;
            txt_toolbarlocation.setVisibility(View.VISIBLE);
            txt_toolbarlocation.setText(locNme);


        } catch (Exception e) {

        }

        return true;
    }

    @Override
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
                    btn_send.setBackground(mContext.getResources().getDrawable(R.drawable.roundedrect_blue));
                } else {
                    btn_send.setEnabled(false);
                    btn_send.setClickable(false);
                    btn_send.setBackground(mContext.getResources().getDrawable(R.drawable.btn_checkin_grey));
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

                String modifyAccountID = accountID.substring(0, accountID.indexOf("-"));
                ApiCommunicate(modifyAccountID, edt_message.getText().toString(), dialog);
                // ApiSearchViewTerminology(modifyAccountID);
                //dialog.dismiss();

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
    public void onMethodCoupn(String uniqueID) {
        CouponFragment cfFragment = new CouponFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("uniqueID", uniqueID);
        cfFragment.setArguments(bundle);
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.mainlayout, cfFragment).commit();
    }

    @Override
    public void onMethodJaldeeLogo(String ynw_verified,String providername) {
        CustomDialog cdd=new CustomDialog(mContext,ynw_verified,providername);
        cdd.setCanceledOnTouchOutside(true);
        cdd.show();

    }


    SearchTerminology mSearchTerminology = new SearchTerminology();

    private void ApiSearchViewTerminology(String accountID) {


        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));


        Call<SearchTerminology> call = apiService.getSearchViewTerminology(Integer.parseInt(accountID), sdf.format(currentTime));

        call.enqueue(new Callback<SearchTerminology>() {
            @Override
            public void onResponse(Call<SearchTerminology> call, Response<SearchTerminology> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

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
                    Config.closeDialog(getActivity(), mDialog);

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

                        Toast.makeText(mContext, "Message send successfully", Toast.LENGTH_LONG).show();
                        mBottomDialog.dismiss();

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
                        Config.logV("Response--BODY------Domain-------------------" + new Gson().toJson(response));

                        if (response.code() == 200) {

                            Config.logV("Response--Array size-------------------------" + response.body().size());

                            if (response.body().size() > 0) {
                                domainList.clear();
                                domainList.add(new Domain_Spinner("All", "All"));
                                // domainModel=new Domain_Spinner();

                                for (int i = 0; i < response.body().size(); i++) {

                                    domainList.add(new Domain_Spinner(response.body().get(i).getDisplayName(), response.body().get(i).getDomain()));


                                }


                                ArrayAdapter<Domain_Spinner> adapter = new ArrayAdapter<Domain_Spinner>(getActivity(), R.layout.spinner_item, domainList);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                mSpinnerDomain.setAdapter(adapter);

                                int pos = getIndex(domainList, spinnerTxt);
                                Config.logV("Selected POSITION ####################" + pos);
                                mSpinnerDomain.setSelection(pos);

                                SharedPreference.getInstance(mContext).setValue("ALL_SELECTED", true);


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

}
