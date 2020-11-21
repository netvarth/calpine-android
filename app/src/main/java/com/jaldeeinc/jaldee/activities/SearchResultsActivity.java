package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.chinodev.androidneomorphframelayout.NeomorphFrameLayout;
import com.jaldeeinc.jaldee.Fragment.DashboardFragment;
import com.jaldeeinc.jaldee.Fragment.HomeSearchFragment;
import com.jaldeeinc.jaldee.Fragment.SearchListFragment;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.MoreFilterAdapter;
import com.jaldeeinc.jaldee.adapter.PaginationAdapter;
import com.jaldeeinc.jaldee.adapter.SearchListAdpter;
import com.jaldeeinc.jaldee.adapter.SearchResultsAdapter;
import com.jaldeeinc.jaldee.callback.AdapterCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTextViewItalicSemiBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTypefaceSpan;
import com.jaldeeinc.jaldee.model.Domain_Spinner;
import com.jaldeeinc.jaldee.model.LanLong;
import com.jaldeeinc.jaldee.model.ListCell;
import com.jaldeeinc.jaldee.model.SearchListModel;
import com.jaldeeinc.jaldee.model.SearchModel;
import com.jaldeeinc.jaldee.model.WorkingModel;
import com.jaldeeinc.jaldee.response.QueueList;
import com.jaldeeinc.jaldee.response.RefinedFilters;
import com.jaldeeinc.jaldee.response.ScheduleList;
import com.jaldeeinc.jaldee.response.SearchAWsResponse;
import com.jaldeeinc.jaldee.response.SearchService;
import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.jaldeeinc.jaldee.utils.EmptySubmitSearchView;
import com.jaldeeinc.jaldee.utils.PaginationScrollListener;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultsActivity extends AppCompatActivity implements AdapterCallback {

    private Context mContext;
    String query, url;
    RecyclerView mRecySearchDetail;
    MoreFilterAdapter mMoreAdapter;
    List<SearchAWsResponse> mSearchResp = new ArrayList<>();
    List<QueueList> mQueueList = new ArrayList<>();
    ArrayList<ScheduleList> mScheduleList = new ArrayList<>();

    List<SearchListModel> mSearchListModel = new ArrayList<>();
    ProgressBar progressBar;
    LinearLayoutManager linearLayoutManager;
    PaginationAdapter pageadapter;
    SearchResultsAdapter searchResultsAdapter;
    ArrayList<SearchViewDetail> mSpecializationList;

    private int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private boolean fromBusinessId = false;
    private boolean fromBusinessname = false;
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
    SearchListAdpter listadapter;
    ArrayList<ListCell> items;
    AdapterCallback mInterface;
    static String latitude;
    static String longitude;
    static String mtyp;

    String searchTxt, spinnerTxt;
    String getQuery_previous = "";

    String subdomainquery, subdomainName;
    boolean show_subdomain = false;

    public void refreshQuery() {
        getQuery_previous = "true";
    }

    CardView ibackpress;
    String s_LocName = "";
    CustomTextViewMedium tv_nosearchresult, tv_searchresult;
    LinearLayout Lnosearchresult;
    boolean userIsInteracting;
    String sort = "";
    String uniqueID;

    String selected = "";
    String querypass1 = "";
    double distance;

    String passformula = "";
    CardView cvRefineSearch;

    ArrayList<RefinedFilters> commonFilterList = new ArrayList<>();

    ArrayList<RefinedFilters> commonFilterSortList = new ArrayList<>();

    ArrayList<RefinedFilters> commonRefinedFilterList = new ArrayList<>();

    ArrayList<RefinedFilters> commonRefinedFilterSortList = new ArrayList<>();

    ArrayList<RefinedFilters> commonsubDomainFilterList = new ArrayList<>();

    ArrayList<RefinedFilters> commonsubDomainFilterSortList = new ArrayList<>();

    ArrayList<RefinedFilters> otherFilterSortedFinalList = new ArrayList<>();

    String filter = "";
    ArrayList<String> passedFormulaArray = new ArrayList<>();
    String subdomain_select;
    String spinnerDomainSelectedFilter = "";

    ImageView ic_sort;
    CardView cvDistance,cvJaldeeVerified,cvBack;
    CustomTextViewItalicSemiBold tvDistance,tvJaldeeVerified;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);


        cvRefineSearch = findViewById(R.id.cv_refineSearch);
        cvDistance = findViewById(R.id.cv_distance);
        cvJaldeeVerified = findViewById(R.id.cv_jaldeeverified);
        tvDistance = findViewById(R.id.tv_distance);
        tvJaldeeVerified = findViewById(R.id.tv_jaldeeVerified);
        cvBack = findViewById(R.id.cv_back);
        mContext = SearchResultsActivity.this;
        mInterface = (AdapterCallback) this;


        sort = SharedPreference.getInstance(mContext).getStringValue("selected_sort_value", "");
        if (sort.equalsIgnoreCase("")) {
            sort = "claimable asc, ynw_verified_level desc, distance asc";
            SharedPreference.getInstance(mContext).setValue("selected_sort_value", sort);
            sort = SharedPreference.getInstance(mContext).getStringValue("selected_sort_value", "");
        }
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("Details");
        if (bundle != null) {
            if (!getQuery_previous.equalsIgnoreCase("true")) {
                query = bundle.getString("query", "");
                url = bundle.getString("url", "");
                spinnerTxt = bundle.getString("spinnervalue", "");
                searchTxt = bundle.getString("searchtxt", "");
            }
            latitude = bundle.getString("latitude", "");
            longitude = bundle.getString("longitude", "");
            mtyp = bundle.getString("typ", "");

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
//                        txtrefinedsearch.setText("Refine Search (" + passedFormulaArray.size() + ") ");
                    } else {
//                        txtrefinedsearch.setText("Refine Search ");
                    }
                }
            }
        }

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });


        Config.logV("APi SUBDOMAIN SEARCHHH@@@@@@@@@@@@@@@@" + subdomainName);
        Config.logV("LATITUDE--------------------------------" + latitude + ", " + longitude);

        isLastPage = false;
        isLoading = false;
        PAGE_START = 0;
        total_foundcount = 0;
        TOTAL_PAGES = 0;
        currentPage = PAGE_START;


        Config.logV("QUERY @@@@@@@@@@@@@@" + query);
        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");

        mRecySearchDetail = (RecyclerView) findViewById(R.id.SearchDetail);
        tv_searchresult = (CustomTextViewMedium)findViewById(R.id.searchresult);

        ic_sort = (ImageView)findViewById(R.id.ic_sort);

        Lnosearchresult = (LinearLayout)findViewById(R.id.Lnosearchresult);
        tv_nosearchresult = (CustomTextViewMedium)findViewById(R.id.txtnosearchresult);



        cvRefineSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SearchListFragment currentFragment = ((SearchListFragment) getFragmentManager().findFragmentById(R.id.mainlayout));
//                Config.logV("Current Fragment @RRR@@@@@@@@@@@@@@@" + currentFragment);
//                if (currentFragment instanceof SearchListFragment) {
//                    Config.logV("Current Fragment @@@@@@@@@@@@@@@@");
//                }
//
//                FilterActivity.setFragmentInstance(currentFragment);

                Intent ifilter = new Intent(mContext, FilterActivity.class);
                ifilter.putExtra("lat", String.valueOf(latitude));
                ifilter.putExtra("longt", String.valueOf(longitude));
                ifilter.putExtra("locName", s_LocName);
                ifilter.putExtra("spinnervalue", spinnerTxt);
//                ifilter.putExtra("sector", mDomainSpinner);
                ifilter.putExtra("from", "searchlist");
                ifilter.putExtra("query", query);
                startActivity(ifilter);

            }
        });

        searchDetail = new HomeSearchFragment().getHomeFragment();

        Config.logV("Fragment Context--43343---" + searchDetail);
        progressBar = (ProgressBar)findViewById(R.id.main_progress);
        searchResultsAdapter = new SearchResultsAdapter(SearchResultsActivity.this, mContext, mInterface, uniqueID, mQueueList);
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecySearchDetail.setLayoutManager(linearLayoutManager);
        mRecySearchDetail.setItemAnimator(new DefaultItemAnimator());
        mRecySearchDetail.setAdapter(searchResultsAdapter);


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


        Config.logV("SearchList URL ONCREATEVIEW@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + query + "URL" + url);
        ApiSEARCHAWSLoadFirstData(query, url, sort);


        APiSearchList();

        cvJaldeeVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cvJaldeeVerified.setCardBackgroundColor(mContext.getResources().getColor(R.color.location_theme));
                tvJaldeeVerified.setTextColor(mContext.getResources().getColor(R.color.white));
                tvDistance.setTextColor(mContext.getResources().getColor(R.color.title_color));
                cvDistance.setCardBackgroundColor(mContext.getResources().getColor(R.color.neomorph_background_color));
                float radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, mContext.getResources().getDisplayMetrics());
                cvJaldeeVerified.setRadius(radius);
                sort = "claimable asc, ynw_verified_level desc, distance asc";
                SharedPreference.getInstance(mContext).setValue("selected_sort", "jaldeeVerified");
                SharedPreference.getInstance(mContext).setValue("selected_sort_value", sort);
                SortBy(sort);
            }
        });

        cvDistance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cvDistance.setCardBackgroundColor(mContext.getResources().getColor(R.color.location_theme));
                        tvDistance.setTextColor(mContext.getResources().getColor(R.color.white));
                        cvJaldeeVerified.setCardBackgroundColor(mContext.getResources().getColor(R.color.neomorph_background_color));
                        tvJaldeeVerified.setTextColor(mContext.getResources().getColor(R.color.title_color));
                        float radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, mContext.getResources().getDisplayMetrics());
                        cvDistance.setRadius(radius);
                        sort = "claimable asc,distance asc, ynw_verified_level desc";
                        SharedPreference.getInstance(mContext).setValue("selected_sort", "distance");
                        SharedPreference.getInstance(mContext).setValue("selected_sort_value", sort);
                        SortBy(sort);
                    }
                });


//        SearchView******************************************************************


//        ic_sort.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View customView = layoutInflater.inflate(R.layout.custom_alert, null);
//
//                final TextView txtjaldeeVerified = (TextView) customView.findViewById(R.id.txtjaldeeVerified);
//                final TextView txtDistance = (TextView) customView.findViewById(R.id.txtDistance);
//                LinearLayout LcustomLayout = (LinearLayout) customView.findViewById(R.id.LcustomLayout);
//
//
//                String selected = SharedPreference.getInstance(mContext).getStringValue("selected_sort", "");
//                if (selected.equalsIgnoreCase("jaldeeVerified")) {
//                    txtjaldeeVerified.setBackgroundColor(mContext.getResources().getColor(R.color.view_border));
//                    txtDistance.setBackgroundColor(mContext.getResources().getColor(R.color.app_background));
//                }
//                if (selected.equalsIgnoreCase("distance")) {
//                    txtDistance.setBackgroundColor(mContext.getResources().getColor(R.color.view_border));
//                    txtjaldeeVerified.setBackgroundColor(mContext.getResources().getColor(R.color.app_background));
//                }
//
//
//                DisplayMetrics displayMetrics = new DisplayMetrics();
//                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//                int width = displayMetrics.widthPixels;
//
//                LcustomLayout.setMinimumWidth(width / 2);
//
//
//                int[] location = new int[2];
//                ic_sort.getLocationOnScreen(location);
//
//                int height = displayMetrics.heightPixels;
//
//                customView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//
//                int x = location[0] - (int) ((customView.getMeasuredWidth() - v.getWidth()) / 2);
//
//                final PopupWindow popupWindow = new PopupWindow(customView, width - width / 3, height / 3);
//
//                //  popupWindow.setAnimationStyle(R.style.MyAlertDialogStyle);
//                //display the popup window
//
//                popupWindow.showAtLocation(ic_sort, Gravity.NO_GRAVITY, x, location[1] + 50);
//                txtjaldeeVerified.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        txtjaldeeVerified.setBackgroundColor(mContext.getResources().getColor(R.color.view_border));
//                        txtDistance.setBackgroundColor(mContext.getResources().getColor(R.color.app_background));
//                        sort = "claimable asc, ynw_verified_level desc, distance asc";
//                        SharedPreference.getInstance(mContext).setValue("selected_sort", "jaldeeVerified");
//                        SharedPreference.getInstance(mContext).setValue("selected_sort_value", sort);
//                        SortBy(sort);
//                    }
//                });
//
//                txtDistance.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        txtDistance.setBackgroundColor(mContext.getResources().getColor(R.color.view_border));
//                        txtjaldeeVerified.setBackgroundColor(mContext.getResources().getColor(R.color.app_background));
//                        sort = "claimable asc,distance asc, ynw_verified_level desc";
//                        SharedPreference.getInstance(mContext).setValue("selected_sort", "distance");
//                        SharedPreference.getInstance(mContext).setValue("selected_sort_value", sort);
//                        SortBy(sort);
//                    }
//                });
//
//                dimBehind(popupWindow);
//
//
//            }
//        });


//        txtrefinedsearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View customView = layoutInflater.inflate(R.layout.popup_filter, null);
//
//
//                final RecyclerView recycle_morefilter = (RecyclerView) customView.findViewById(R.id.recycle_morefilter);
//                TextView txtclear = (TextView) customView.findViewById(R.id.txtclear);
//
//                if (searchSrcTextView.getText().toString().length() == 0) {
//                    show_subdomain = false;
//                }
//
//
//                if (passedFormulaArray.size() == 0) {
//
//                    if (mDomainSpinner != null && mDomainSpinner.equalsIgnoreCase("All")) {
//                        ApiFilters(recycle_morefilter, "Select", passedFormulaArray);
//
//                    } else {
//
//                        Config.logV("ApiMoreRefinedFilters1111 @@@@@@@@@@@@@" + passformula + "" + show_subdomain);
//                        ApiMoreRefinedFilters(recycle_morefilter, mDomainSpinner, "No", "", passedFormulaArray, show_subdomain);
//                        if (show_subdomain) {
//                            Config.logV("APi SUBDOMAIN @@@@@@@@@@@@@@@@@" + subdomainName + "RRRR" + mDomainSpinner);
//                            ArrayList<String> emptyList = new ArrayList<>();
//                            ApiSubDomainRefinedFilters(recycle_morefilter, subdomainName, mDomainSpinner, subdomainquery, emptyList, "");
//                        }
//                    }
//                } else {
//                    if (mDomainSpinner.equalsIgnoreCase("All")) {
//
//                        for (int i = 0; i < passedFormulaArray.size(); i++) {
//                            Config.logV("PRINT VAL FORMULA@@111HHHHH" + passedFormulaArray.get(i));
//
//                        }
//
//                        String domainSelect = "Select";
//                        for (int i = 0; i < passedFormulaArray.size(); i++) {
//                            String splitsFormula[] = passedFormulaArray.get(i).toString().split(":");
//                            if (splitsFormula[0].equalsIgnoreCase("sector")) {
//
//                                Config.logV("Sector @@@@@@@@@@@@@@@@@@@@@@@@" + splitsFormula[1]);
//                                domainSelect = splitsFormula[1].replace("'", "");
//                            }
//                        }
//
//                        String subdomain = "Select";
//                        for (int i = 0; i < passedFormulaArray.size(); i++) {
//                            String splitsFormula[] = passedFormulaArray.get(i).toString().split(":");
//                            if (splitsFormula[0].equalsIgnoreCase("sub_sector")) {
//
//                                Config.logV("subdomain @@@@@@@@@@@@@@@@@@@@@@@@" + splitsFormula[1]);
//                                subdomain = splitsFormula[1].replace("'", "");
//                            }
//                        }
//                        if (domainSelect.equalsIgnoreCase("Select")) {
//                            ApiFilters(recycle_morefilter, "Select", passedFormulaArray);
//                        } else {
//
//                            //ApiMoreRefinedFilters(recycle_morefilter, domainSelect, "No", "", passedFormulaArray, show_subdomain);
//
//                            if (subdomain.equalsIgnoreCase("Select")) {
//
//                                ApiMoreRefinedFilters(recycle_morefilter, domainSelect, "yes", "", passedFormulaArray, show_subdomain);
//                            } else {
//
//                                //ApiMoreRefinedFilters(recycle_morefilter, domainSelect, "yes", passformula, passedFormulaArray, show_subdomain);
//                                ApiSubDomainRefinedFilters(recycle_morefilter, subdomain, domainSelect, subdomainquery, passedFormulaArray, subdomainDisplayNamePass);
//                            }
//                        }
//
//
//                    } else {
//                        String subdomain = "Select";
//                        for (int i = 0; i < passedFormulaArray.size(); i++) {
//                            String splitsFormula[] = passedFormulaArray.get(i).toString().split(":");
//                            if (splitsFormula[0].equalsIgnoreCase("sub_sector")) {
//
//                                Config.logV("subdomain @@@@@@@@@@@@@@@@@@@@@@@@" + splitsFormula[1]);
//                                subdomain = splitsFormula[1].replace("'", "");
//                            }
//                        }
//                        if (subdomain.equalsIgnoreCase("Select") && subdomainName.equalsIgnoreCase("")) {
//
//                            ApiMoreRefinedFilters(recycle_morefilter, mDomainSpinner, "No", "", passedFormulaArray, show_subdomain);
//                        } else {
//                            if (subdomain.equalsIgnoreCase("Select")) {
//                                subdomain = subdomainName;
//                            }
//                            ApiSubDomainRefinedFilters(recycle_morefilter, subdomain, mDomainSpinner, subdomainquery, passedFormulaArray, subdomainDisplayNamePass);
//                        }
//
//                    }
//                }
//                int[] location = new int[2];
//                txtrefinedsearch.getLocationOnScreen(location);
//                //Rect loc=locateView(ic_refinedFilter);
//                //instantiate popup window
//                DisplayMetrics displayMetrics = new DisplayMetrics();
//                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//                int width = displayMetrics.widthPixels;
//                int height = displayMetrics.heightPixels;
//
//                customView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//
//                int x = location[0] - (int) ((customView.getMeasuredWidth() - v.getWidth()) / 2);
//
//                final PopupWindow popupWindow = new PopupWindow(customView, width - width / 3, height - (location[1] + 50));
//
//                popupWindow.setAnimationStyle(R.style.MyAlertDialogStyle);
//                //display the popup window
//
//                //   Rect location1 = locateView(txtrefinedsearch);
//                //  popupWindow.showAtLocation(txtrefinedsearch, Gravity.NO_GRAVITY, location[0] + width - width / 3, location[1] + 50);
//
//                popupWindow.showAtLocation(txtrefinedsearch, Gravity.NO_GRAVITY, x, location[1] + 50);
//
//
//                dimBehind(popupWindow);
//                Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
//                        "fonts/Montserrat_Bold.otf");
//                txtclear.setTypeface(tyface);
//
//
//                txtclear.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        passformula = "";
//                        passedFormulaArray.clear();
//                        txtrefinedsearch.setText("Refine Search");
//                        MoreItemClick(passformula, query);
//                        if (mDomainSpinner.equalsIgnoreCase("All")) {
//                            ApiFilters(recycle_morefilter, "Select", passedFormulaArray);
//                        } else {
//                            ApiMoreRefinedFilters(recycle_morefilter, mDomainSpinner, "No", "", passedFormulaArray, show_subdomain);
//                        }
//                    }
//                });
//
//
//            }
//
//        });
    }

    private void APiSearchList() {
        {
            final ApiInterface apiService =
                    ApiClient.getClient(mContext).create(ApiInterface.class);
            final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
            mDialog.show();
            Call<SearchModel> call = apiService.getAllSearch();
            call.enqueue(new Callback<SearchModel>() {
                @Override
                public void onResponse(Call<SearchModel> call, Response<SearchModel> response) {
                    try {
                        if (mDialog.isShowing())
                            Config.closeDialog(SearchResultsActivity.this, mDialog);

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
                        Config.closeDialog(SearchResultsActivity.this, mDialog);
                }
            });
        }
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
                                if(response.body().getHits().getHit().get(i).getFields().getAltemergencyservices_location1()!=null){
                                    search.setAltemergencyservices_location1(response.body().getHits().getHit().get(i).getFields().getAltemergencyservices_location1());
                                }

                                if (response.body().getHits().getHit().get(i).getFields().getFirstaid_location1() != null) {
                                    search.setFirstaid_location1(response.body().getHits().getHit().get(i).getFields().getFirstaid_location1());
                                }

                                if(response.body().getHits().getHit().get(i).getFields().getCustom_id() != null){
                                    search.setCustom_id(response.body().getHits().getHit().get(i).getFields().getCustom_id());
                                }
                                if(response.body().getHits().getHit().get(i).getFields().getEnc_uid() != null){
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

    public void ApiSEARCHAWSLoadFirstData(String mQueryPass, String mPass, String sort) {

        Config.logV("zeo WWWW" + sort);

        Config.logV("PASS FORMULA @@@@@@@@@@@@@@@@@@@@" + passformula);
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
        params.put("q.options","");
        params.put("sort", sort);
        params.put("expr.distance", mPass);
        params.put("return", "_all_fields,distance");

        Call<SearchAWsResponse> call = apiService.getSearchAWS(query, params);


        call.enqueue(new Callback<SearchAWsResponse>() {
            @Override
            public void onResponse(Call<SearchAWsResponse> call, Response<SearchAWsResponse> response) {

                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(SearchResultsActivity.this, mDialog);

                    Config.logV("URL--------First-------" + response.raw().request().url().toString().trim());

                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

//                        Config.logV("Response--Body AWS-------------------------" + new Gson().toJson(response.body()));

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
                                if(response.body().getHits().getHit().get(i).getFields().getAltemergencyservices_location1()!=null){
                                    search.setAltemergencyservices_location1(response.body().getHits().getHit().get(i).getFields().getAltemergencyservices_location1());
                                }


                                if (response.body().getHits().getHit().get(i).getFields().getFirstaid_location1() != null) {
                                    search.setFirstaid_location1(response.body().getHits().getHit().get(i).getFields().getFirstaid_location1());
                                }

                                if(response.body().getHits().getHit().get(i).getFields().getCustom_id() != null){
                                    search.setCustom_id(response.body().getHits().getHit().get(i).getFields().getCustom_id());
                                }
                                if(response.body().getHits().getHit().get(i).getFields().getEnc_uid() != null){
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
                    Config.closeDialog(SearchResultsActivity.this, mDialog);

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
                                    if(mSearchRespPass.get(i).getCustom_id() != null){
                                        searchList.setCustom_id(mSearchRespPass.get(i).getCustom_id());
                                    }
                                    if(mSearchRespPass.get(i).getEnc_uid() != null){
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
                                    if (mScheduleList != null && mScheduleList.size()>0) {
                                        if(mScheduleList.get(i).isApptEnabled()){
                                            searchList.setCheckinAllowed(true);
                                            searchList.setApptEnabled(true);
                                        }
                                        else{
                                            searchList.setCheckinAllowed(false);
                                            searchList.setApptEnabled(false);
                                        }
                                        //  searchList.setCheckinAllowed(mScheduleList.get(i).isCheckinAllowed());
                                        if(mScheduleList.get(i).getAvailableSchedule()!=null) {
                                            if (mScheduleList.get(i).getAvailableSchedule().getLocation() != null) {
                                                searchList.setaLoc(String.valueOf(mScheduleList.get(i).getAvailableSchedule().getLocation().getId()));
                                            }
                                            searchList.setAvailableDate(mScheduleList.get(i).getAvailableSchedule().getAvailableDate());
                                        }

                                        if (mScheduleList.get(i).getSlotsData()!= null) {
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

                                    if(mSearchRespPass.get(i).getAltemergencyservices_location1()!=null){
                                        searchList.setAltemergencyservices_location1(mSearchRespPass.get(i).getAltemergencyservices_location1());
                                    }

                                    if (mSearchRespPass.get(i).getFirstaid_location1() != null) {
                                        searchList.setFirstaid_location1(mSearchRespPass.get(i).getFirstaid_location1());
                                    }
                                    if(mSearchRespPass.get(i).getCustom_id() != null){
                                        searchList.setCustom_id(mSearchRespPass.get(i).getCustom_id());
                                    }
                                    if(mSearchRespPass.get(i).getEnc_uid() != null){
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
                                    if (mScheduleList != null && mScheduleList.size()>0 ) {
                                        if(mScheduleList.get(i).isApptEnabled()){
                                            searchList.setCheckinAllowed(true);
                                            searchList.setApptEnabled(true);
                                        }
                                        else{
                                            searchList.setCheckinAllowed(false);
                                            searchList.setApptEnabled(false);
                                        }
                                        //searchList.setCheckinAllowed(mScheduleList.get(i).isCheckinAllowed());
                                        if(mScheduleList.get(i).getAvailableSchedule()!=null) {
                                            if (mScheduleList.get(i).getAvailableSchedule().getLocation() != null) {
                                                searchList.setaLoc(String.valueOf(mScheduleList.get(i).getAvailableSchedule().getLocation().getId()));
                                            }
                                            searchList.setAvailableDate(mScheduleList.get(i).getAvailableSchedule().getAvailableDate());
                                        }
                                        if (mScheduleList.get(i).getSlotsData()!= null) {
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
                                mScheduleList.add(i,response.body().get(i));
                            }
                            if(mCheck.equals("next")){
                                ApiQueueList(queuelist, mSearchResp, "next", mScheduleList);
                            }else if(mCheck.equals("first")){
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

    private int getIndex(ArrayList<Domain_Spinner> spinner, String myString) {
        int index = 0;
        for (int i = 0; i < spinner.size(); i++) {
            if (spinner.get(i).getDisplayName().equals(myString)) {
                index = i;
            }
        }
        return index;
    }

    public void SortBy(String sort) {
        Config.logV("zeo" + sort);
        if(mtyp==null){
            mtyp = "city";
        }
        LanLong Lanlong = getLocationNearBy(Double.parseDouble(latitude), Double.parseDouble(longitude),mtyp);
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
        searchResultsAdapter.clear();
        // final String query1 = "(and location1:" + locationRange + ")";
        final String query1;
        if (query.equalsIgnoreCase("")) {
            query1 = "(and location1:" + locationRange + ")";
        } else {
            query1 = query;
        }
        final String pass1 = "haversin(" + latitude + "," + longitude + ", location1.latitude, location1.longitude)";
        query = query1;
        url = pass1;
        Config.logV("MAin PAge&&&&&&&&&&&&Item CLICK &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&7" + query);
        Config.logV("SearchList URL ITEM CLICK SPINNER 222@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        ApiSEARCHAWSLoadFirstData(query, url, sort);
    }

    public LanLong getLocationNearBy(double lant, double longt,String typ) {

        if(typ.equalsIgnoreCase("state")){
            distance = 300;
        }else if(typ.equalsIgnoreCase("city")){
            distance = 40;
        }else if(typ.equalsIgnoreCase("area")){
            distance = 5;
        }else if(typ.equalsIgnoreCase("metro")){
            distance = 10;
        }else if(typ.equalsIgnoreCase("capital")){
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
    public void onMethodQuery(ArrayList<String> formula, ArrayList<String> key) {

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
