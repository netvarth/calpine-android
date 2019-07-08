package com.nv.youneverwait.Fragment;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nv.youneverwait.R;
import com.nv.youneverwait.activities.FilterActivity;
import com.nv.youneverwait.activities.SearchLocationActivity;
import com.nv.youneverwait.adapter.FilterAdapter;
import com.nv.youneverwait.adapter.MoreFilterAdapter;
import com.nv.youneverwait.adapter.PaginationAdapter;
import com.nv.youneverwait.adapter.SearchListAdpter;
import com.nv.youneverwait.callback.AdapterCallback;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.custom.CustomTypefaceSpan;
import com.nv.youneverwait.model.CommonFilterEnum;
import com.nv.youneverwait.model.Domain_Spinner;
import com.nv.youneverwait.model.LanLong;
import com.nv.youneverwait.model.ListCell;
import com.nv.youneverwait.model.SearchListModel;
import com.nv.youneverwait.model.SearchModel;
import com.nv.youneverwait.model.WorkingModel;
import com.nv.youneverwait.response.QueueList;
import com.nv.youneverwait.response.RefinedFilters;
import com.nv.youneverwait.response.SearchAWsResponse;
import com.nv.youneverwait.response.SearchTerminology;
import com.nv.youneverwait.utils.EmptySubmitSearchView;
import com.nv.youneverwait.utils.PaginationScrollListener;
import com.nv.youneverwait.utils.SharedPreference;
import com.nv.youneverwait.widgets.CustomDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
    MoreFilterAdapter mMoreAdapter;
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

    String subdomainquery, subdomainName;
    boolean show_subdomain = false;

    public void refreshQuery() {
        getQuery_previous = "true";
    }

    static TextView txt_toolbarlocation;
    ImageView ibackpress;
    String s_LocName = "";
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


    String passformula = "";
    TextView txtrefinedsearch;

    ArrayList<RefinedFilters> commonFilterList = new ArrayList<>();

    ArrayList<RefinedFilters> commonFilterSortList = new ArrayList<>();

    ArrayList<RefinedFilters> commonRefinedFilterList = new ArrayList<>();

    ArrayList<RefinedFilters> commonRefinedFilterSortList = new ArrayList<>();

    ArrayList<RefinedFilters> commonsubDomainFilterList = new ArrayList<>();

    ArrayList<RefinedFilters> commonsubDomainFilterSortList = new ArrayList<>();

    ArrayList<RefinedFilters> otherFilterSortedFinalList = new ArrayList<>();

    String filter = "";
    ArrayList<String> passedFormulaArray = new ArrayList<>();
    View row;
    String subdomain_select;
    String spinnerDomainSelectedFilter = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        row = inflater.inflate(R.layout.fragment_searchdetail, container, false);

        txtrefinedsearch = (TextView) row.findViewById(R.id.txtrefinedsearch);
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
            passformula = bundle.getString("passformula", "");
            filter = bundle.getString("filter", "");
            subdomain_select = bundle.getString("subdomain_select", "");
            subdomainName = bundle.getString("subdomainName", "");
            subdomainquery = bundle.getString("subdomainquery", "");

            if (subdomain_select.equalsIgnoreCase("true")) {
                show_subdomain = true;
            }
            if (filter != null) {
                if (filter.equalsIgnoreCase("filter")) {
                    passedFormulaArray = bundle.getStringArrayList("passFormulaArray");
                    Config.logV("PASSED FORMULA ARRAY WWWW @@" + passedFormulaArray.size());
                    if (passedFormulaArray.size() > 0) {
                        txtrefinedsearch.setText("Refine Search (" + passedFormulaArray.size() + ") ");
                    } else {
                        txtrefinedsearch.setText("Refine Search ");
                    }
                }
            }
        }
        userIsInteracting = false;
        Config.logV("APi SUBDOMAIN SEARCHHH@@@@@@@@@@@@@@@@" + subdomainName);
        spinnerDomainSelectedFilter = mDomainSpinner;
        Config.logV("LATITUDE--------------------------------" + latitude + ", " + longitude);

        isLastPage = false;
        isLoading = false;
        PAGE_START = 0;
        total_foundcount = 0;
        TOTAL_PAGES = 0;
        currentPage = PAGE_START;


        Config.logV("QUERY @@@@@@@@@@@@@@" + query);
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

        ImageView ic_refinedFilter = (ImageView) row.findViewById(R.id.ic_refinedFilter);

        ic_refinedFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchListFragment currentFragment = ((SearchListFragment) getFragmentManager().findFragmentById(R.id.mainlayout));
                Config.logV("Current Fragment @RRR@@@@@@@@@@@@@@@" + currentFragment);
                if (currentFragment instanceof SearchListFragment) {
                    Config.logV("Current Fragment @@@@@@@@@@@@@@@@");
                }

                FilterActivity.setFragmentInstance(currentFragment);

                mSearchView.setQuery("", false);

                Intent ifilter = new Intent(mContext, FilterActivity.class);
                ifilter.putExtra("lat", String.valueOf(latitude));
                ifilter.putExtra("longt", String.valueOf(longitude));
                ifilter.putExtra("locName", s_LocName);
                ifilter.putExtra("spinnervalue", spinnerTxt);
                ifilter.putExtra("sector", mDomainSpinner);
                ifilter.putExtra("from", "searchlist");
                startActivity(ifilter);

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


                if (!filter.equalsIgnoreCase("")) {
                    Config.logV("BackPress------$$$$$$$$$$$$$$###-----");
                    getActivity().finish();
                } else {
                    getFragmentManager().popBackStack();
                }
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

        Config.logV("SearchList URL ONCREATEVIEW@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + query + "URL" + url);
        ApiSEARCHAWSLoadFirstData(query, url);


        APiSearchList();
        //SearchView******************************************************************


        txtrefinedsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = layoutInflater.inflate(R.layout.popup_filter, null);


                final RecyclerView recycle_morefilter = (RecyclerView) customView.findViewById(R.id.recycle_morefilter);
                TextView txtclear = (TextView) customView.findViewById(R.id.txtclear);

                if (searchSrcTextView.getText().toString().length() == 0) {
                    show_subdomain = false;
                }

                Config.logV("PASSED FORMULA ARRAY@@@@@@@@@@@@" + passedFormulaArray.size());
                if (mDomainSpinner.equalsIgnoreCase("All")) {
                    ApiFilters(recycle_morefilter, "Select", passedFormulaArray);
                } else {

                    Config.logV("ApiMoreRefinedFilters1111 @@@@@@@@@@@@@" + passformula);
                    ApiMoreRefinedFilters(recycle_morefilter, mDomainSpinner, "No", "", passedFormulaArray, show_subdomain);
                    if (show_subdomain) {
                        Config.logV("APi SUBDOMAIN @@@@@@@@@@@@@@@@@" + subdomainName + "RRRR" + mDomainSpinner);
                        ApiSubDomainRefinedFilters(recycle_morefilter, subdomainName, mDomainSpinner, subdomainquery);
                    }
                }
                int[] location = new int[2];
                txtrefinedsearch.getLocationOnScreen(location);
                //Rect loc=locateView(ic_refinedFilter);
                //instantiate popup window
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                int height = displayMetrics.heightPixels;

                customView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                int x = location[0] - (int) ((customView.getMeasuredWidth() - v.getWidth()) / 2);

                final PopupWindow popupWindow = new PopupWindow(customView, width - width / 3, height - (location[1] + 50));

                popupWindow.setAnimationStyle(R.style.MyAlertDialogStyle);
                //display the popup window

                //   Rect location1 = locateView(txtrefinedsearch);
                //  popupWindow.showAtLocation(txtrefinedsearch, Gravity.NO_GRAVITY, location[0] + width - width / 3, location[1] + 50);

                popupWindow.showAtLocation(txtrefinedsearch, Gravity.NO_GRAVITY, x, location[1] + 50);


                dimBehind(popupWindow);


                txtclear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        passformula = "";
                        passedFormulaArray.clear();
                        txtrefinedsearch.setText("Refine Search");
                        MoreItemClick(passformula);
                        if (mDomainSpinner.equalsIgnoreCase("All")) {
                            ApiFilters(recycle_morefilter, "Select", passedFormulaArray);
                        } else {
                            ApiMoreRefinedFilters(recycle_morefilter, mDomainSpinner, "No", "", passedFormulaArray, show_subdomain);
                        }
                    }
                });


            }
        });


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


                Config.logV("show_subdomain---------------" + show_subdomain);
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

                    show_subdomain = true;
                    subdomainquery = "sub_sector:'" + cell.getName() + "'";
                    subdomainName = cell.getName();
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

                    String name = cell.getName();
                    if (name.contains("'")) {
                        Config.logV("Query@@@@@@@@@@@@%%%###DDDD%%%%%%%%-----------" + name);
                        name = cell.getName().replace("'", "%5C%27");

                    }
                    Config.logV("Query@@@@@@@@@@@@%%%%%%%%%%%-----------" + name);


                    if (mSector.equalsIgnoreCase("All")) {
                        querycreate = "title:" + "'" + name + "'";
                    } else {
                        sub = "title:" + "'" + name + "'";
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


                Config.logV("PASSED FORMULA ARRAY 11111111");
                passformula = "";
                passedFormulaArray.clear();
                txtrefinedsearch.setText("Refine Search");

                ApiSEARCHAWSLoadFirstData(query, url);


            }
        });

        return row;
    }

    public LanLong getLocationNearBy(double lant, double longt) {

        double distance = 40;/*;DISTANCE_AREA: 5, // in Km*/

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

    private void dimBehind(PopupWindow popupWindow) {
        View container;
        if (popupWindow.getBackground() == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent();
            } else {
                container = popupWindow.getContentView();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent().getParent();
            } else {
                container = (View) popupWindow.getContentView().getParent();
            }
        }
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.5f;
        wm.updateViewLayout(container, p);
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
            query.put("fq", "(and  "+passformula + "(not test_account:1 ) )");
        }


        Map<String, String> params = new HashMap<>();

        params.put("size", "10");
        params.put("q.parser", "structured");
        params.put("sort", "ynw_verified_level desc, distance asc");

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
                                search.setClaimable(response.body().getHits().getHit().get(i).getFields().getClaimable());
                                search.setFirst_checkin_coupon_count(response.body().getHits().getHit().get(i).getFields().getFirst_checkin_coupon_count());
                                search.setCoupon_enabled(response.body().getHits().getHit().get(i).getFields().getCoupon_enabled());
                                search.setAccountType(response.body().getHits().getHit().get(i).getFields().getAccountType());
                                search.setBranch_name(response.body().getHits().getHit().get(i).getFields().getBranch_name());


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

                                if (response.body().getHits().getHit().get(i).getFields().getBusiness_hours1() != null) {
                                    search.setBusiness_hours1(response.body().getHits().getHit().get(i).getFields().getBusiness_hours1());
                                }
                                if (response.body().getHits().getHit().get(i).getFields().getTerminologies() != null) {
                                    search.setTerminologies(response.body().getHits().getHit().get(i).getFields().getTerminologies());
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


        Config.logV("PASS FORMULA @@@@@@@@@@@@@@@@@@@@" + passformula);
        final ApiInterface apiService =
                ApiClient.getClientAWS(mContext).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(getActivity(), getActivity().getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Map<String, String> query = new HashMap<>();

        query.put("start", "0");
        query.put("q", mQueryPass);

        String mobile = SharedPreference.getInstance(mContext).getStringValue("mobile", "");
        if (mobile.startsWith("55")) {
            query.put("fq", "(and  test_account:1 " + passformula + ")");
        } else {
            query.put("fq", "(and  "+ passformula +" (not test_account:1  ) )");
        }


        Map<String, String> params = new HashMap<>();

        params.put("size", "10");
        params.put("q.parser", "structured");
        params.put("sort", "ynw_verified_level desc, distance asc");
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
                                search.setClaimable(response.body().getHits().getHit().get(i).getFields().getClaimable());
                                search.setFirst_checkin_coupon_count(response.body().getHits().getHit().get(i).getFields().getFirst_checkin_coupon_count());
                                search.setCoupon_enabled(response.body().getHits().getHit().get(i).getFields().getCoupon_enabled());
                                search.setAccountType(response.body().getHits().getHit().get(i).getFields().getAccountType());
                                search.setBranch_name(response.body().getHits().getHit().get(i).getFields().getBranch_name());

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


                                //7 types

                                if (response.body().getHits().getHit().get(i).getFields().getBusiness_hours1() != null) {
                                    search.setBusiness_hours1(response.body().getHits().getHit().get(i).getFields().getBusiness_hours1());
                                }

                                if (response.body().getHits().getHit().get(i).getFields().getTerminologies() != null) {
                                    search.setTerminologies(response.body().getHits().getHit().get(i).getFields().getTerminologies());
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

    /**
     * Method to combine Cloud response with Estimated waiting time.
     *
     * @param queuelist       Estimated Wait time Details
     * @param mSearchRespPass Cloud response
     * @param mCheck          first/next
     */
    private void ApiQueueList(ArrayList<String> queuelist, final List<SearchAWsResponse> mSearchRespPass, final String mCheck) {

        ApiInterface apiService =
                ApiClient.getClient(getActivity()).create(ApiInterface.class);

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
                        Config.logV("Sucess345 ----------" + new Gson().toJson(response.body()));
                        for (int i = 0; i < response.body().size(); i++) {
                            mQueueList.add(response.body().get(i));
                        }
                        if (mCheck.equalsIgnoreCase("next")) {
                            Config.logV("TOTAL PAGES_--------------" + TOTAL_PAGES);
                            Config.logV("CURRENT PAGE**22222**555***********" + TOTAL_PAGES);
                            Config.logV("CURRENT PAGE**333*****5555********" + currentPage);
                            pageadapter.removeLoadingFooter();
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
                                searchList.setCoupon_enabled(mSearchRespPass.get(i).getCoupon_enabled());
                                searchList.setAccountType(mSearchRespPass.get(i).getAccountType());
                                searchList.setBranch_name(mSearchRespPass.get(i).getBranch_name());
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

                                if (mSearchRespPass.get(i).getBusiness_hours1() != null) {
                                    searchList.setBusiness_hours1(mSearchRespPass.get(i).getBusiness_hours1());
                                }

                                if (mSearchRespPass.get(i).getTerminologies() != null) {
                                    searchList.setTerminologies(mSearchRespPass.get(i).getTerminologies());
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
                                if (mSearchRespPass.get(i).getFirstaid_location1() != null) {
                                    searchList.setFirstaid_location1(mSearchRespPass.get(i).getFirstaid_location1());
                                }
                                // Location Amenities --- End

                                // Merging Estimated Waittime Details with Cloud Response

                                //As Instructed by Nitesh Order won't change

                                searchList.setQId(mSearchRespPass.get(i).getId());
                                if(mQueueList.get(i).getMessage()!=null){
                                    searchList.setMessage(mQueueList.get(i).getMessage());
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
                                        searchList.setAvailableToday(mQueueList.get(i).getNextAvailableQueue().isAvailableToday());
                                        if (mQueueList.get(i).getNextAvailableQueue().getServiceTime() != null) {
                                            searchList.setServiceTime(mQueueList.get(i).getNextAvailableQueue().getServiceTime());
                                        }
                                    }
                                }
//                                for (int j = 0; j < mQueueList.size(); j++) {
//
//                                    String mID = mQueueList.get(j).getProvider().getId() + "-"; // Set Account Id here to mID. If location is not null it will set below
//
//                                    if (mQueueList.get(j).getNextAvailableQueue() != null) {
//                                        if (mQueueList.get(j).getNextAvailableQueue().getLocation() != null) {
//                                            mID += mQueueList.get(j).getNextAvailableQueue().getLocation().getId();
//                                            Config.logV("QID----mmm-------------------" + mID + "compare-------" + mSearchRespPass.get(i).getId());
//                                            if (mID.equalsIgnoreCase(mSearchRespPass.get(i).getId())) {
//                                                if (mQueueList.get(j).getNextAvailableQueue().getAvailableDate() != null) {
//                                                    searchList.setAvail_date(mQueueList.get(j).getNextAvailableQueue().getAvailableDate());
//                                                }
//                                                searchList.setmLoc(String.valueOf(mQueueList.get(j).getNextAvailableQueue().getLocation().getId()));
//                                                searchList.setQId(mID);
//                                                searchList.setIsopen(mQueueList.get(i).getNextAvailableQueue().isOpenNow());
//                                                searchList.setPersonAhead(mQueueList.get(i).getNextAvailableQueue().getPersonAhead());
//                                                searchList.setCalculationMode(mQueueList.get(i).getNextAvailableQueue().getCalculationMode());
//                                                searchList.setBranchSpCount(mQueueList.get(j).getBranchSpCount());
//                                                searchList.setOnlineCheckIn(mQueueList.get(i).getNextAvailableQueue().isOnlineCheckIn());
//                                                searchList.setAvailableToday(mQueueList.get(i).getNextAvailableQueue().isAvailableToday());
//                                                searchList.setQueueWaitingTime(mQueueList.get(i).getNextAvailableQueue().getQueueWaitingTime());
//                                                if (mQueueList.get(i).getServiceTime() != null) {
//                                                    searchList.setServiceTime(mQueueList.get(i).getServiceTime());
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
                                mSearchListModel.add(searchList);
                                Log.i("mSearchListModel12",new Gson().toJson(mSearchListModel));
                            }
                            List<SearchListModel> results = mSearchListModel;

                            Config.logV("QUEUELIST @@@@@@@@@@@@@@@@@@@@@@ RESUlt" + results.size());


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
                                searchList.setClaimable(mSearchRespPass.get(i).getClaimable());
                                searchList.setFirst_checkin_coupon_count(mSearchRespPass.get(i).getFirst_checkin_coupon_count());
                                searchList.setAccountType(mSearchRespPass.get(i).getAccountType());
                                searchList.setBranch_name(mSearchRespPass.get(i).getBranch_name());
                                searchList.setCoupon_enabled(mSearchRespPass.get(i).getCoupon_enabled());

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

                                if (mSearchRespPass.get(i).getDistance() != null) {
                                    Config.logV("Distance @@@@@@@@@@@44444" + mSearchRespPass.get(i).getDistance());
                                    searchList.setDistance(mSearchRespPass.get(i).getDistance());
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

                                if (mSearchRespPass.get(i).getTerminologies() != null) {
                                    searchList.setTerminologies(mSearchRespPass.get(i).getTerminologies());
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

                                Log.i("mSearchResponse", new Gson().toJson(mSearchRespPass));

                                // As Instructed by Nitesh Order not Change

                                searchList.setQId(mSearchRespPass.get(i).getId());
                                if(mQueueList.get(i).getMessage()!=null){
                                    searchList.setMessage(mQueueList.get(i).getMessage());
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
                                        searchList.setAvailableToday(mQueueList.get(i).getNextAvailableQueue().isAvailableToday());
                                        if (mQueueList.get(i).getNextAvailableQueue().getServiceTime() != null) {
                                            searchList.setServiceTime(mQueueList.get(i).getNextAvailableQueue().getServiceTime());
                                        }
                                    }
                                }


                                mSearchListModel.add(searchList);
                            }


                            final List<SearchListModel> results = mSearchListModel;
                            progressBar.setVisibility(View.GONE);
                            pageadapter.addAll(results);
                            pageadapter.notifyDataSetChanged();


                            Config.logV("QUEUELIST @@@@@@@@@@@@@@@@@@@@@@ RESUlt" + results.size());

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
        transaction.add(R.id.mainlayout, pfFragment).commit();

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
        transaction.add(R.id.mainlayout, pfFragment).commit();
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
        transaction.add(R.id.mainlayout, pfFragment).commit();
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

        if (!querypass.equalsIgnoreCase("")) {

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
        }


        if (querycreate == null) {

            if (querypass.contains("'")) {
                Config.logV("Query@@@@@@@@@@@@%%%###DDDD%%%%%%%%-----------" + querypass);
                querypass = querypass.replace("'", "%5C%27");
            }
            Config.logV("Query@@@@@@@@@@@@%%%%%%%%%%%-----------" + querypass);

            if (!mDomainSpinner.equalsIgnoreCase("All")) {
                querycreate = "(or sub_sector_displayname: " + "'" + querypass + "' sub_sector: " + "'" + querypass + "' specialization: " + "'" + querypass + "' specialization_displayname: " + "'" + querypass + "' title: " + "'" + querypass + "' services: " + "'" + querypass + "' qualification: " + "'" + querypass + "' adwords: " + "'" + querypass + "') sector :'" + mDomainSpinner + "'";
            } else {

                Config.logV("Query @@@@@@@@@@@@@@" + querypass);
                querycreate = "(or sub_sector_displayname: " + "'" + querypass + "' sub_sector: " + "'" + querypass + "' specialization: " + "'" + querypass + "' specialization_displayname: " + "'" + querypass + "' title: " + "'" + querypass + "' services: " + "'" + querypass + "' qualification: " + "'" + querypass + "' adwords: " + "'" + querypass + "')";


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

        passformula = "";

        Config.logV("PASSED FORMULA ARRAY 1144444411");
        passedFormulaArray.clear();
        txtrefinedsearch.setText("Refine Search");

        ApiSEARCHAWSLoadFirstData(query, url);


    }


    public void SpinnerItemClick() {

        show_subdomain = false;
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


        Config.logV("PASSED FORMULA ARRAY 15555511");
        passformula = "";
        passedFormulaArray.clear();
        txtrefinedsearch.setText("Refine Search");

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
//
//    @Override
//    public void onMethodCoupn(String uniqueID) {
//
//      //  ApiJaldeeCoupan(uniqueID);
//
//    }

    @Override
    public void onMethodFirstCoupn(String uniqueid) {

        CouponFirstFragment cffFragment = new CouponFirstFragment();
        refreshQuery();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("uniqueID", uniqueid);
        cffFragment.setArguments(bundle);
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.mainlayout, cffFragment).commit();

    }

    @Override
    public void onMethodCoupn(String uniqueID) {
        CouponFragment cfFragment = new CouponFragment();

//        ApiJaldeeCoupan(uniqueID);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("uniqueID", uniqueID);
        cfFragment.setArguments(bundle);

        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.mainlayout, cfFragment).commit();

    }

    @Override
    public void onMethodJaldeeLogo(String ynw_verified, String providername) {
        CustomDialog cdd = new CustomDialog(mContext, ynw_verified, providername);
        cdd.setCanceledOnTouchOutside(true);
        cdd.show();

    }


    @Override
    public void onMethodFilterRefined(String passformula, RecyclerView recylcepopup, String domain) {
        Config.logV("ApiMoreRefinedFilters2222222 @@@@@@@@@@@@@" + passformula);
        spinnerDomainSelectedFilter = domain;
        //MoreItemClick(passformula);
        ArrayList<String> emptyList = new ArrayList<>();
        ApiMoreRefinedFilters(recylcepopup, domain, "yes", passformula, emptyList, show_subdomain);
    }

    @Override
    public void onMethodSubDomainFilter(String passformula, RecyclerView recyclepopup, String subdomainame, String DomainName) {
        Config.logV("SUBDOMAIN Selector@@@@@@@@@@@@@@@@@@@" + passformula);
        //  MoreItemClick("sector:'" + spinnerDomainSelectedFilter + "'" + "sub_sector:'" + subdomainame + "'");
        if(DomainName.equalsIgnoreCase("")) {
            ApiSubDomainRefinedFilters(recyclepopup, subdomainame, mDomainSpinner, passformula);
        }else{
            ApiSubDomainRefinedFilters(recyclepopup, subdomainame, DomainName, passformula);
        }
    }

    @Override
    public void onMethodQuery(ArrayList<String> sFormula, ArrayList<String> keyFormula) {

        for (String str : sFormula) {

            Config.logV("PRINT $$$$$@ ####@@@@@@@@@@@@@@" + str);
        }
        for (String str : keyFormula) {

            Config.logV("PRINT Key@ ####@@@@@@@@@@@@@@" + str);
        }

        if (sFormula.size() > 0) {
            txtrefinedsearch.setText("Refine Search (" + sFormula.size() + ") ");
        } else {
            txtrefinedsearch.setText("Refine Search ");
        }
        String queryFormula = "";
        int count = 0;
        boolean match = false;
        for (int i = 0; i < keyFormula.size(); i++) {


            for (int j = 0; j < sFormula.size(); j++) {


                String splitsFormula[] = sFormula.get(j).toString().split(":");
                //Config.logV("PRINT Key ##"+splitsFormula[0]);
                if (splitsFormula[0].equalsIgnoreCase(keyFormula.get(i).toString())) {

                    // Config.logV("PRINT Key TRUE @@@@@@@@@@@ ##"+splitsFormula[0]);
                    match = true;
                    count++;
                    if (count == 1)
                        queryFormula += "(" + sFormula.get(j).toString();
                    else
                        queryFormula += sFormula.get(j).toString();

                }


            }


            if (match) {
                if (count > 1) {
                    queryFormula += ")";
                    match = false;
                }
            }
            Config.logV("SortString@@@@@@@@@@" + queryFormula + "Count @@@@@" + count);

            if (count > 1) {
                queryFormula = queryFormula.replace("(" + keyFormula.get(i), "( or " + keyFormula.get(i));
                Config.logV("SortString ^^^^^^^^^^^^^^^@@@@@@@@@@" + queryFormula);
            } else {
                queryFormula = queryFormula.replace("(" + keyFormula.get(i), keyFormula.get(i));

            }

            count = 0;
        }


        MoreItemClick(queryFormula);
        Config.logV("PRINT VAL FORMULA@@" + queryFormula);

    }


    SearchTerminology mSearchTerminology = new SearchTerminology();

    private void ApiSearchViewTerminology(String accountID) {


        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(getActivity(), this.getResources().getString(R.string.dialog_log_in));
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


        final Dialog mDialog = Config.getProgressDialog(getActivity(), this.getResources().getString(R.string.dialog_log_in));
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void APiSearchList() {
        {


            final ApiInterface apiService =
                    ApiClient.getClient(getActivity()).create(ApiInterface.class);

            final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
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


    private void ApiFilters(final RecyclerView recycle_filter, final String domainSelect, final ArrayList<String> passedFormulaArray) {


        ApiInterface apiService =
                ApiClient.getClient(getActivity()).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(getActivity(), this.getResources().getString(R.string.dialog_log_in));
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

                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                        recycle_filter.setLayoutManager(mLayoutManager);

                        int listsize = commonFilterList.size();

                        boolean otherFlag = false;
                        commonFilterSortList.clear();
                        ArrayList booleanVariables = new ArrayList();
                        ArrayList booleanVariablesValue = new ArrayList();
                        ArrayList booleanVariablesName = new ArrayList();
                        booleanVariables.clear();
                        booleanVariablesValue.clear();
                        booleanVariablesName.clear();


                        ArrayList<Domain_Spinner> domainNewlist = new ArrayList<>();
                        domainNewlist.addAll(domainList);
                        for (int i = 0; i < domainNewlist.size(); i++) {
                            if (domainNewlist.get(i).getDomain().equalsIgnoreCase("All")) {
                                domainNewlist.remove(i);
                            }
                        }
                        Domain_Spinner domain = new Domain_Spinner("Select", "Select");
                        domainNewlist.add(0, domain);

                        RefinedFilters refined1 = new RefinedFilters();
                        refined1.setDisplayName("Select Service Domain");
                        refined1.setDataType("Spinner");
                        refined1.setExpand(false);
                        refined1.setName("domain");
                        refined1.setEnumeratedConstants(domainNewlist);
                        refined1.setCloudSearchIndex("domain");
                        commonFilterSortList.add(refined1);

                        for (int i = 0; i < commonFilterList.size(); i++) {


                            if (commonFilterList.get(i).getDataType().equalsIgnoreCase("Boolean")) {

                                booleanVariables.add(commonFilterList.get(i).getDisplayName());
                                booleanVariablesValue.add(commonFilterList.get(i).getCloudSearchIndex());
                                booleanVariablesName.add(commonFilterList.get(i).getName());
                                if (!otherFlag) {
                                    RefinedFilters refined = new RefinedFilters();
                                    refined.setDisplayName("Other Filter");
                                    refined.setDataType(commonFilterList.get(i).getDataType());
                                    otherFlag = true;
                                    refined.setItemName(booleanVariables);
                                    refined.setExpand(false);
                                    refined.setPassName(booleanVariablesName);
                                    refined.setCloudIndexvalue(booleanVariablesValue);
                                    commonFilterSortList.add(refined);
                                }


                            } else {
                                RefinedFilters refined = new RefinedFilters();
                                refined.setDisplayName(commonFilterList.get(i).getDisplayName());
                                refined.setDataType(commonFilterList.get(i).getDataType());
                                refined.setExpand(false);
                                refined.setName(commonFilterList.get(i).getName());
                                refined.setEnumeratedConstants(commonFilterList.get(i).getEnumeratedConstants());
                                refined.setCloudSearchIndex(commonFilterList.get(i).getCloudSearchIndex());
                                commonFilterSortList.add(refined);
                            }


                        }


                        Config.logV("Comon Filter size @@@@@@@@@" + commonFilterSortList.size());
                        mMoreAdapter = new MoreFilterAdapter(commonFilterSortList, mContext, getActivity(), mInterface, recycle_filter, "", domainSelect, "Select", passedFormulaArray);
                        recycle_filter.setAdapter(mMoreAdapter);
                        mMoreAdapter.notifyDataSetChanged();


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

    ArrayList<RefinedFilters> commonMFilterList = new ArrayList<RefinedFilters>();

    private void ApiMoreRefinedFilters(final RecyclerView recycle_filter, final String subdomain, final String showdomain, final String passformula, final ArrayList<String> passedFormulaArray, final boolean show_subdomain) {


        Config.logV("show_subdomain @@@@@@@@@@@@@@" + show_subdomain);
        ApiInterface apiService =
                ApiClient.getClient(getActivity()).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(getActivity(), this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        Call<RefinedFilters> call = apiService.getMoreFilters(subdomain);


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

                        commonRefinedFilterList.clear();
                        commonRefinedFilterSortList.clear();
                        commonRefinedFilterList = new ArrayList<>();
                        commonRefinedFilterList = response.body().getRefinedFilters();
                        Config.logV("commonRefinedFilterList Filters----------------" + commonRefinedFilterList.size());

                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                        recycle_filter.setLayoutManager(mLayoutManager);


                        commonMFilterList.clear();
                        commonMFilterList = response.body().getCommonFilters();
                        Config.logV("Common Filters----------------" + commonMFilterList.size());


                        boolean otherFlag = false;
                        ArrayList booleanVariables = new ArrayList();
                        ArrayList booleanVariablesValue = new ArrayList();
                        ArrayList booleanVariablesName = new ArrayList();
                        booleanVariables.clear();
                        booleanVariablesValue.clear();
                        booleanVariablesName.clear();


                        if (showdomain.equalsIgnoreCase("yes")) {
                            ArrayList<Domain_Spinner> domainNewlist = new ArrayList<>();
                            domainNewlist.addAll(domainList);
                            for (int i = 0; i < domainNewlist.size(); i++) {
                                if (domainNewlist.get(i).getDomain().equalsIgnoreCase("All")) {
                                    domainNewlist.remove(i);
                                }
                            }
                            Domain_Spinner domain = new Domain_Spinner("Select", "Select");
                            domainNewlist.add(0, domain);

                            RefinedFilters refined1 = new RefinedFilters();
                            refined1.setDisplayName("Select Service Domain");
                            refined1.setDataType("Spinner");
                            refined1.setExpand(false);
                            refined1.setName("domain");
                            refined1.setEnumeratedConstants(domainNewlist);
                            refined1.setCloudSearchIndex("domain");
                            commonRefinedFilterSortList.add(0, refined1);
                        }

                        if (!show_subdomain||mDomainSpinner.equalsIgnoreCase("All")) {

                            ArrayList<SearchModel> subdomainList = new ArrayList<>();
                            subdomainList.clear();
                            SearchModel search1 = new SearchModel();
                            search1.setDisplayname("Select");
                            search1.setSector("Select");
                            search1.setName("Select");
                            search1.setQuery("");
                            subdomainList.add(search1);

                            for (int i = 0; i < mSubDomain.size(); i++) {
                                if (mSubDomain.get(i).getSector().equalsIgnoreCase(subdomain)) {
                                    SearchModel search = new SearchModel();
                                    search.setDisplayname(mSubDomain.get(i).getDisplayname());
                                    search.setSector(mSubDomain.get(i).getSector());
                                    search.setName(mSubDomain.get(i).getName());
                                    search.setQuery(mSubDomain.get(i).getQuery());
                                    subdomainList.add(search);
                                }
                            }
                            RefinedFilters refined2 = new RefinedFilters();
                            refined2.setDisplayName("Select Service specialization or Occupation");
                            refined2.setDataType("Spinner_subdomain");
                            refined2.setExpand(false);
                            refined2.setName("subdomain");
                            refined2.setEnumeratedConstants(subdomainList);
                            refined2.setCloudSearchIndex("subdomain");
                            if (showdomain.equalsIgnoreCase("yes")) {
                                commonRefinedFilterSortList.add(1, refined2);
                            } else {
                                commonRefinedFilterSortList.add(0, refined2);
                            }


                        }


                        for (int i = 0; i < commonRefinedFilterList.size(); i++) {

                            RefinedFilters refined = new RefinedFilters();
                            refined.setDisplayName(commonRefinedFilterList.get(i).getDisplayName());
                            refined.setDataType(commonRefinedFilterList.get(i).getDataType());
                            refined.setExpand(false);
                            refined.setName(commonRefinedFilterList.get(i).getName());
                            refined.setEnumeratedConstants(commonRefinedFilterList.get(i).getEnumeratedConstants());
                            refined.setCloudSearchIndex(commonRefinedFilterList.get(i).getCloudSearchIndex());
                            commonRefinedFilterSortList.add(refined);
                        }

                        for (int i = 0; i < commonMFilterList.size(); i++) {


                           /* if (commonMFilterList.get(i).getDataType().equalsIgnoreCase("Boolean")) {

                                booleanVariables.add(commonMFilterList.get(i).getDisplayName());
                                booleanVariablesValue.add(commonMFilterList.get(i).getCloudSearchIndex());
                                booleanVariablesName.add(commonMFilterList.get(i).getName());
                                if (!otherFlag) {
                                    RefinedFilters refined = new RefinedFilters();
                                    refined.setDisplayName("Other Filter");
                                    refined.setDataType(commonMFilterList.get(i).getDataType());
                                    otherFlag = true;
                                    refined.setItemName(booleanVariables);
                                    refined.setExpand(false);
                                    refined.setPassName(booleanVariablesName);
                                    refined.setCloudIndexvalue(booleanVariablesValue);
                                    commonRefinedFilterSortList.add(refined);
                                }


                            } else {*/
                            RefinedFilters refined = new RefinedFilters();
                            refined.setDisplayName(commonMFilterList.get(i).getDisplayName());
                            refined.setDataType(commonMFilterList.get(i).getDataType());
                            refined.setExpand(false);
                            refined.setName(commonMFilterList.get(i).getName());
                            refined.setEnumeratedConstants(commonMFilterList.get(i).getEnumeratedConstants());
                            refined.setCloudSearchIndex(commonMFilterList.get(i).getCloudSearchIndex());
                            commonRefinedFilterSortList.add(refined);
                            // }


                        }


                        otherFilterSortedFinalList.clear();
                        for (int i = 0; i < commonRefinedFilterSortList.size(); i++) {

                            if (commonRefinedFilterSortList.get(i).getDataType().equalsIgnoreCase("Boolean")) {

                                booleanVariables.add(commonRefinedFilterSortList.get(i).getDisplayName());
                                booleanVariablesValue.add(commonRefinedFilterSortList.get(i).getCloudSearchIndex());
                                booleanVariablesName.add(commonRefinedFilterSortList.get(i).getName());
                                if (!otherFlag) {
                                    RefinedFilters refined = new RefinedFilters();
                                    refined.setDisplayName("Other Filter");
                                    refined.setDataType(commonRefinedFilterSortList.get(i).getDataType());
                                    otherFlag = true;
                                    refined.setItemName(booleanVariables);
                                    refined.setExpand(false);
                                    refined.setPassName(booleanVariablesName);
                                    refined.setCloudIndexvalue(booleanVariablesValue);
                                    otherFilterSortedFinalList.add(refined);
                                }


                            } else {
                                RefinedFilters refined = new RefinedFilters();
                                refined.setDisplayName(commonRefinedFilterSortList.get(i).getDisplayName());
                                refined.setDataType(commonRefinedFilterSortList.get(i).getDataType());
                                refined.setExpand(false);
                                refined.setName(commonRefinedFilterSortList.get(i).getName());
                                refined.setEnumeratedConstants(commonRefinedFilterSortList.get(i).getEnumeratedConstants());
                                refined.setCloudSearchIndex(commonRefinedFilterSortList.get(i).getCloudSearchIndex());
                                otherFilterSortedFinalList.add(refined);
                            }

                        }


                        Config.logV("Comon Filter size @@@@@@@@@" + otherFilterSortedFinalList.size());
                        mMoreAdapter = new MoreFilterAdapter(otherFilterSortedFinalList, mContext, getActivity(), mInterface, recycle_filter, passformula, subdomain, "Select", passedFormulaArray);
                        recycle_filter.setAdapter(mMoreAdapter);
                        mMoreAdapter.notifyDataSetChanged();


                        Config.logV("commonRefinedFilterList Filter size @@@@@@@@@" + otherFilterSortedFinalList.size());


                       /* commonFilterSortList.addAll(1,commonRefinedFilterSortList);
                        recycle_filter.setAdapter(mMoreAdapter);
                        mMoreAdapter.notifyDataSetChanged();*/


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


    private void ApiSubDomainRefinedFilters(final RecyclerView recycle_filter, final String subdomain, final String domain, final String passformula) {

        Config.logV("URL----------SUBDOMAIN--@@@@@@@@@@@@@@@@@@");
        ApiInterface apiService =
                ApiClient.getClient(getActivity()).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(getActivity(), this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        Call<RefinedFilters> call = apiService.getSubDomainMoreFilters(subdomain, domain);


        call.enqueue(new Callback<RefinedFilters>() {
            @Override
            public void onResponse(Call<RefinedFilters> call, Response<RefinedFilters> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL----------SUBDOMAIN-----" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------SUBDOMAIN-------------------" + response.code());
                    if (response.code() == 200) {
                        Config.logV("Response----------------");

                        commonsubDomainFilterList.clear();
                        commonsubDomainFilterSortList.clear();
                        commonsubDomainFilterList = new ArrayList<>();
                        commonsubDomainFilterList = response.body().getRefinedFilters();


                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                        recycle_filter.setLayoutManager(mLayoutManager);


                        for (int i = 0; i < commonsubDomainFilterList.size(); i++) {

                            RefinedFilters refined = new RefinedFilters();
                            refined.setDisplayName(commonsubDomainFilterList.get(i).getDisplayName());
                            refined.setDataType(commonsubDomainFilterList.get(i).getDataType());
                            refined.setExpand(false);
                            refined.setName(commonsubDomainFilterList.get(i).getName());
                            refined.setEnumeratedConstants(commonsubDomainFilterList.get(i).getEnumeratedConstants());
                            refined.setCloudSearchIndex(commonsubDomainFilterList.get(i).getCloudSearchIndex());
                            commonsubDomainFilterSortList.add(refined);
                        }







/*
                        Config.logV("YYYYComon Filter size @@@@@@@@@" + commonRefinedFilterSortList.size());

                        commonRefinedFilterSortList.addAll(commonsubDomainFilterSortList);

                        Config.logV("YYYYComon Filter sizeEEE @@@@@@@@@" + commonRefinedFilterSortList.size());



                        recycle_filter.setAdapter(mMoreAdapter);
                        mMoreAdapter.notifyDataSetChanged();*/

                        ArrayList<RefinedFilters> mergedList = new ArrayList<>();
                        mergedList.clear();
                        mergedList.addAll(commonRefinedFilterSortList);
                        mergedList.addAll(commonsubDomainFilterSortList);


                        boolean otherFlag = false;
                        ArrayList booleanVariables = new ArrayList();
                        ArrayList booleanVariablesValue = new ArrayList();
                        ArrayList booleanVariablesName = new ArrayList();
                        booleanVariables.clear();
                        booleanVariablesValue.clear();
                        booleanVariablesName.clear();


                        ArrayList<RefinedFilters> mergedOtherFilterList = new ArrayList<>();

                        mergedOtherFilterList.clear();
                        for (int i = 0; i < mergedList.size(); i++) {

                            if (mergedList.get(i).getDataType().equalsIgnoreCase("Boolean")) {

                                booleanVariables.add(mergedList.get(i).getDisplayName());
                                booleanVariablesValue.add(mergedList.get(i).getCloudSearchIndex());
                                booleanVariablesName.add(mergedList.get(i).getName());
                                if (!otherFlag) {
                                    RefinedFilters refined = new RefinedFilters();
                                    refined.setDisplayName("Other Filter");
                                    refined.setDataType(mergedList.get(i).getDataType());
                                    otherFlag = true;
                                    refined.setItemName(booleanVariables);
                                    refined.setExpand(false);
                                    refined.setPassName(booleanVariablesName);
                                    refined.setCloudIndexvalue(booleanVariablesValue);
                                    mergedOtherFilterList.add(refined);
                                }


                            } else {
                                RefinedFilters refined = new RefinedFilters();
                                refined.setDisplayName(mergedList.get(i).getDisplayName());
                                refined.setDataType(mergedList.get(i).getDataType());
                                refined.setExpand(false);
                                refined.setName(mergedList.get(i).getName());
                                refined.setEnumeratedConstants(mergedList.get(i).getEnumeratedConstants());
                                refined.setCloudSearchIndex(mergedList.get(i).getCloudSearchIndex());
                                mergedOtherFilterList.add(refined);
                            }

                        }


                        Config.logV("YYYYmergedList@@@@@@@@@" + mergedOtherFilterList.size());
                        ArrayList<String> emptyList = new ArrayList<>();
                        mMoreAdapter = new MoreFilterAdapter(mergedOtherFilterList, mContext, getActivity(), mInterface, recycle_filter, passformula, domain, subdomain, emptyList);
                        recycle_filter.setAdapter(mMoreAdapter);
                        mMoreAdapter.notifyDataSetChanged();


                    } else {

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<RefinedFilters> call, Throwable t) {
                // Log error here since request failed
                Config.logV("SUBDOMAIN Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }
        });

    }

    public void MoreItemClick(String pass_formula) {

        //  mSearchView.setQuery("", false);
        LanLong Lanlong = getLocationNearBy(Double.parseDouble(latitude), Double.parseDouble(longitude));
        double upperLeftLat = Lanlong.getUpperLeftLat();
        double upperLeftLon = Lanlong.getUpperLeftLon();
        double lowerRightLat = Lanlong.getLowerRightLat();
        double lowerRightLon = Lanlong.getLowerRightLon();
        String locationRange = "['" + lowerRightLat + "," + lowerRightLon + "','" + upperLeftLat + "," + upperLeftLon + "']";


        isLastPage = false;
        isLoading = false;
        PAGE_START = 0;
        total_foundcount = 0;
        TOTAL_PAGES = 0;
        currentPage = PAGE_START;
        pageadapter.clear();


        passformula = pass_formula;

     /*   final String query1 = "(and location1:['11.751416900900901,75.3701820990991','9.9496150990991,77.171983900900'] " + querycreate + ")";

        final String pass1 = "haversin(11.751416900900901,75.3701820990991, location1.latitude, location1.longitude)";*/


        final String query1 = "(and location1:" + locationRange + ")";

        final String pass1 = "haversin(" + latitude + "," + longitude + ", location1.latitude, location1.longitude)";

        query = query1;
        url = pass1;

        Config.logV("MAin PAge&&&&&&&&&&&&Item CLICK &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&7" + query);
        Config.logV("SearchList URL ITEM CLICK SPINNER 222@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

        ApiSEARCHAWSLoadFirstData(query, url);


    }

    public void SetPassFormula(ArrayList<String> mPassFormula) {
        passedFormulaArray = mPassFormula;
        if (mPassFormula.size() > 0) {
            txtrefinedsearch.setText("Refine Search (" + mPassFormula.size() + ") ");
        } else {
            txtrefinedsearch.setText("Refine Search ");
        }
    }

    public static Rect locateView(View v) {
        int[] loc_int = new int[2];
        if (v == null) return null;
        try {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe) {
            //Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + v.getWidth();
        location.bottom = location.top + v.getHeight();
        return location;
    }
}
