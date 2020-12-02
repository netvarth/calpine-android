package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jaldeeinc.jaldee.Fragment.HomeSearchFragment;
import com.jaldeeinc.jaldee.Fragment.JdnFragment;
import com.jaldeeinc.jaldee.Interface.IClearFilter;
import com.jaldeeinc.jaldee.Interface.IFilterOptions;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.FilterChipsAdapter;
import com.jaldeeinc.jaldee.adapter.MoreFilterAdapter;
import com.jaldeeinc.jaldee.adapter.PaginationAdapter;
import com.jaldeeinc.jaldee.adapter.SearchFiltersAdapter;
import com.jaldeeinc.jaldee.adapter.SearchListAdpter;
import com.jaldeeinc.jaldee.adapter.SearchResultsAdapter;
import com.jaldeeinc.jaldee.adapter.SubDomainAdapter;
import com.jaldeeinc.jaldee.callback.AdapterCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTextViewItalicSemiBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.CustomTypefaceSpan;

import com.jaldeeinc.jaldee.model.Domain_Spinner;
import com.jaldeeinc.jaldee.model.FilterChips;
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
import com.jaldeeinc.jaldee.utils.PaginationScrollListener;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultsActivity extends AppCompatActivity implements AdapterCallback, IClearFilter {

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
    String searchQuery = "";
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

    CardView cvSort;
    private String selectedDomain = "";
    CardView cvDistance, cvJaldeeVerified, cvBack;
    CustomTextViewItalicSemiBold tvDistance, tvJaldeeVerified;
    //    private Filters filtersDialog;
    private IFilterOptions iFilterOptions;
    private RecyclerView rvFilters,rvAppliedFilters;
    RecyclerView rvChips;
    private CustomTextViewMedium tvApplyFilters;
    private FilterChipsAdapter filterChipsAdapter;
    private SearchFiltersAdapter searchFiltersAdapter;
    private IClearFilter iClearFilter;
    private ArrayList<String> passList = new ArrayList<>();
    private ArrayList<String> keyList = new ArrayList<>();
    ArrayList<FilterChips> filterChipsList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);


        cvRefineSearch = findViewById(R.id.cv_refineSearch);
        rvAppliedFilters = findViewById(R.id.rv_appliedFilters);
        cvBack = findViewById(R.id.cv_back);
        tvApplyFilters = findViewById(R.id.tv_applyFilter);
        mContext = SearchResultsActivity.this;
        mInterface = (AdapterCallback) this;
        iClearFilter = (IClearFilter) this;


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
            selectedDomain = bundle.getString("selectedDomain", "");

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
        tv_searchresult = (CustomTextViewMedium) findViewById(R.id.searchresult);

        cvSort = (CardView) findViewById(R.id.cv_sort);

        Lnosearchresult = (LinearLayout) findViewById(R.id.Lnosearchresult);
        tv_nosearchresult = (CustomTextViewMedium) findViewById(R.id.txtnosearchresult);


        tvApplyFilters.setVisibility(View.GONE);
        filterChipsAdapter = new FilterChipsAdapter(mContext, filterChipsList, iClearFilter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.HORIZONTAL, false);
        rvAppliedFilters.setLayoutManager(gridLayoutManager);
        rvAppliedFilters.setAdapter(filterChipsAdapter);

        cvRefineSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomSheetDialog dialog = new BottomSheetDialog(SearchResultsActivity.this, R.style.TransparentDialog);
                dialog.setContentView(R.layout.filter);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);

                final RecyclerView recycle_morefilter = (RecyclerView) dialog.findViewById(R.id.rv_domains);
                rvChips = dialog.findViewById(R.id.rv_chips);
                CustomTextViewMedium tvClear = (CustomTextViewMedium) dialog.findViewById(R.id.tv_clear);
                CustomTextViewSemiBold tvApply = dialog.findViewById(R.id.tv_apply);
                ImageView ivClose = dialog.findViewById(R.id.iv_close);

                if (passedFormulaArray.size() == 0) {

                    if (selectedDomain != null && selectedDomain.equalsIgnoreCase("All")) {
                        ApiFilters(recycle_morefilter, "Select", passedFormulaArray);

                    } else {

                        Config.logV("ApiMoreRefinedFilters1111 @@@@@@@@@@@@@" + passformula + "" + show_subdomain);
                        ApiMoreRefinedFilters(recycle_morefilter, selectedDomain, "No", "", passedFormulaArray, show_subdomain);
                        if (show_subdomain) {

                            ArrayList<String> emptyList = new ArrayList<>();
                            ApiSubDomainRefinedFilters(recycle_morefilter, subdomainName, selectedDomain, subdomainquery, emptyList, "");
                        }
                    }
                } else {
                    if (selectedDomain.equalsIgnoreCase("All")) {

                        for (int i = 0; i < passedFormulaArray.size(); i++) {
                            Config.logV("PRINT VAL FORMULA@@111HHHHH" + passedFormulaArray.get(i));

                        }

                        String domainSelect = "Select";
                        for (int i = 0; i < passedFormulaArray.size(); i++) {
                            String splitsFormula[] = passedFormulaArray.get(i).toString().split(":");
                            if (splitsFormula[0].equalsIgnoreCase("sector")) {

                                Config.logV("Sector @@@@@@@@@@@@@@@@@@@@@@@@" + splitsFormula[1]);
                                domainSelect = splitsFormula[1].replace("'", "");
                            }
                        }

                        String subdomain = "Select";
                        for (int i = 0; i < passedFormulaArray.size(); i++) {
                            String splitsFormula[] = passedFormulaArray.get(i).toString().split(":");
                            if (splitsFormula[0].equalsIgnoreCase("sub_sector")) {

                                Config.logV("subdomain @@@@@@@@@@@@@@@@@@@@@@@@" + splitsFormula[1]);
                                subdomain = splitsFormula[1].replace("'", "");
                            }
                        }
                        if (domainSelect.equalsIgnoreCase("Select")) {
                            ApiFilters(recycle_morefilter, "Select", passedFormulaArray);
                        } else {

                            //ApiMoreRefinedFilters(recycle_morefilter, domainSelect, "No", "", passedFormulaArray, show_subdomain);

                            if (subdomain.equalsIgnoreCase("Select")) {

                                ApiMoreRefinedFilters(recycle_morefilter, domainSelect, "yes", "", passedFormulaArray, show_subdomain);
                            } else {

                                //ApiMoreRefinedFilters(recycle_morefilter, domainSelect, "yes", passformula, passedFormulaArray, show_subdomain);
                                //temporary
                                ApiSubDomainRefinedFilters(recycle_morefilter, subdomain, domainSelect, subdomainquery, passedFormulaArray, "");
                            }
                        }


                    } else {
                        String subdomain = "Select";
                        for (int i = 0; i < passedFormulaArray.size(); i++) {
                            String splitsFormula[] = passedFormulaArray.get(i).toString().split(":");
                            if (splitsFormula[0].equalsIgnoreCase("sub_sector")) {

                                Config.logV("subdomain @@@@@@@@@@@@@@@@@@@@@@@@" + splitsFormula[1]);
                                subdomain = splitsFormula[1].replace("'", "");
                            }
                        }
                        if (subdomain.equalsIgnoreCase("Select") && subdomainName.equalsIgnoreCase("")) {

                            ApiMoreRefinedFilters(recycle_morefilter, selectedDomain, "No", "", passedFormulaArray, show_subdomain);
                        } else {
                            if (subdomain.equalsIgnoreCase("Select")) {
                                subdomain = subdomainName;
                            }
                            //temporary
                            ApiSubDomainRefinedFilters(recycle_morefilter, subdomain, selectedDomain, subdomainquery, passedFormulaArray, "");
                        }

                    }
                }
                dialog.show();

                ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

//                        passformula = "";
//                        passedFormulaArray.clear();
                        dialog.dismiss();

                    }
                });

                tvApply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ApiSEARCHAWSLoadFirstData(searchQuery, url, sort);
                        tvApplyFilters.setVisibility(View.VISIBLE);
                        filterChipsAdapter = new FilterChipsAdapter(mContext, filterChipsList, iClearFilter);
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.HORIZONTAL, false);
                        rvAppliedFilters.setLayoutManager(gridLayoutManager);
                        rvAppliedFilters.setAdapter(filterChipsAdapter);
                        dialog.dismiss();
                    }
                });


                tvClear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        rvAppliedFilters.removeAllViewsInLayout();
                        filterChipsList.clear();
                        passformula = "";
                        passedFormulaArray.clear();
//                                txtrefinedsearch.setText("Refine Search");
                        MoreItemClick(passformula, query);
                        if (selectedDomain.equalsIgnoreCase("All")) {
                            ApiFilters(recycle_morefilter, "Select", passedFormulaArray);
                        } else {
                            ApiMoreRefinedFilters(recycle_morefilter, selectedDomain, "No", "", passedFormulaArray, show_subdomain);
                        }
                    }
                });

            }
        });

        searchDetail = new HomeSearchFragment().getHomeFragment();

        Config.logV("Fragment Context--43343---" + searchDetail);
        progressBar = (ProgressBar) findViewById(R.id.main_progress);
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

        filterChipsAdapter = new FilterChipsAdapter(mContext, filterChipsList, iClearFilter);

        cvSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater = (LayoutInflater) ((Activity) mContext).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = layoutInflater.inflate(R.layout.custom_alert, null);

                final CustomTextViewSemiBold txtjaldeeVerified = (CustomTextViewSemiBold) customView.findViewById(R.id.txtjaldeeVerified);
                final CustomTextViewSemiBold txtDistance = (CustomTextViewSemiBold) customView.findViewById(R.id.txtDistance);
                LinearLayout LcustomLayout = (LinearLayout) customView.findViewById(R.id.LcustomLayout);


                String selected = SharedPreference.getInstance(mContext).getStringValue("selected_sort", "");
                if (selected.equalsIgnoreCase("jaldeeVerified")) {
                    txtjaldeeVerified.setBackgroundColor(mContext.getResources().getColor(R.color.view_border));
                    txtDistance.setBackgroundColor(mContext.getResources().getColor(R.color.app_background));
                }
                if (selected.equalsIgnoreCase("distance")) {
                    txtDistance.setBackgroundColor(mContext.getResources().getColor(R.color.view_border));
                    txtjaldeeVerified.setBackgroundColor(mContext.getResources().getColor(R.color.app_background));
                }


                DisplayMetrics displayMetrics = new DisplayMetrics();
                ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;

                LcustomLayout.setMinimumWidth(width / 2);


                int[] location = new int[2];
                cvSort.getLocationOnScreen(location);

                int height = displayMetrics.heightPixels;

                customView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                int x = location[0] - (int) ((customView.getMeasuredWidth() - v.getWidth()) / 2);

                final PopupWindow popupWindow = new PopupWindow(customView, width - width / 3, height / 3);

                //  popupWindow.setAnimationStyle(R.style.MyAlertDialogStyle);
                //display the popup window

                popupWindow.showAtLocation(cvSort, Gravity.NO_GRAVITY, x, location[1] + 50);
                txtjaldeeVerified.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        txtjaldeeVerified.setBackgroundColor(mContext.getResources().getColor(R.color.view_border));
                        txtDistance.setBackgroundColor(mContext.getResources().getColor(R.color.app_background));
                        sort = "claimable asc, ynw_verified_level desc, distance asc";
                        SharedPreference.getInstance(mContext).setValue("selected_sort", "jaldeeVerified");
                        SharedPreference.getInstance(mContext).setValue("selected_sort_value", sort);
                        SortBy(sort);
                    }
                });

                txtDistance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        txtDistance.setBackgroundColor(mContext.getResources().getColor(R.color.view_border));
                        txtjaldeeVerified.setBackgroundColor(mContext.getResources().getColor(R.color.app_background));
                        sort = "claimable asc,distance asc, ynw_verified_level desc";
                        SharedPreference.getInstance(mContext).setValue("selected_sort", "distance");
                        SharedPreference.getInstance(mContext).setValue("selected_sort_value", sort);
                        SortBy(sort);
                    }
                });

                dimBehind(popupWindow);


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

    public void ApiSEARCHAWSLoadFirstData(String mQueryPass, String mPass, String sort) {

        Config.logV("zeo WWWW" + sort);

        Config.logV("PASS FORMULA @@@@@@@@@@@@@@@@@@@@" + passformula);
        final ApiInterface apiService =
                ApiClient.getClientAWS(mContext).create(ApiInterface.class);

        searchResultsAdapter.clear();
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
                            Lnosearchresult.setVisibility(View.VISIBLE);
                            tv_nosearchresult.setVisibility(View.VISIBLE);
                            mRecySearchDetail.setVisibility(View.GONE);
                            tv_nosearchresult.setText("No search result found");
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
        if (mtyp == null) {
            mtyp = "city";
        }
        LanLong Lanlong = getLocationNearBy(Double.parseDouble(latitude), Double.parseDouble(longitude), mtyp);
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

    public LanLong getLocationNearBy(double lant, double longt, String typ) {

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

    private void ApiFilters(final RecyclerView recycle_filter, final String domainSelect, final ArrayList<String> passedFormulaArray) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(SearchResultsActivity.this, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<RefinedFilters> call = apiService.getFilters();
        call.enqueue(new Callback<RefinedFilters>() {
            @Override
            public void onResponse(Call<RefinedFilters> call, Response<RefinedFilters> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(SearchResultsActivity.this, mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------Filters-------------------" + response.code());
                    if (response.code() == 200) {
                        Config.logV("Response----------------");
                        commonFilterList.clear();
                        commonFilterList = response.body().getCommonFilters();
                        Config.logV("Common Filters----------------" + commonFilterList.size());
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SearchResultsActivity.this);
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
//                        filterChipsAdapter = new FilterChipsAdapter(mContext, filterChipsList, iClearFilter);
//                        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.HORIZONTAL, false);
//                        rvChips.setLayoutManager(gridLayoutManager);
//                        rvChips.setAdapter(filterChipsAdapter);

                        searchFiltersAdapter = new SearchFiltersAdapter(commonFilterSortList, mContext, SearchResultsActivity.this, mInterface, recycle_filter, "", domainSelect, "Select", passedFormulaArray, "");
                        recycle_filter.setAdapter(searchFiltersAdapter);
                        searchFiltersAdapter.notifyDataSetChanged();
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
                    Config.closeDialog(SearchResultsActivity.this, mDialog);

            }
        });
    }

    ArrayList<RefinedFilters> commonMFilterList = new ArrayList<RefinedFilters>();

    private void ApiMoreRefinedFilters(final RecyclerView recycle_filter, final String subdomain, final String showdomain, final String passformula, final ArrayList<String> passedFormulaArray, final boolean show_subdomain) {
        Config.logV("show_subdomain @@@@@@@@@@@@@@" + show_subdomain);
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(SearchResultsActivity.this, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<RefinedFilters> call = apiService.getMoreFilters(subdomain);
        call.enqueue(new Callback<RefinedFilters>() {
            @Override
            public void onResponse(Call<RefinedFilters> call, Response<RefinedFilters> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(SearchResultsActivity.this, mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------Filters-------------------" + response.code());
                    if (response.code() == 200) {
                        Config.logV("Response----------------");
                        commonRefinedFilterList.clear();
                        commonRefinedFilterSortList.clear();
                        commonRefinedFilterList = new ArrayList<>();
                        commonRefinedFilterList = response.body().getRefinedFilters();
                        Config.logV("commonRefinedFilterList Filters----------------" + commonRefinedFilterList.size());
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SearchResultsActivity.this);
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
                        if (!show_subdomain || selectedDomain.equalsIgnoreCase("All")) {
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
                            RefinedFilters refined = new RefinedFilters();
                            refined.setDisplayName(commonMFilterList.get(i).getDisplayName());
                            refined.setDataType(commonMFilterList.get(i).getDataType());
                            refined.setExpand(false);
                            refined.setName(commonMFilterList.get(i).getName());
                            refined.setEnumeratedConstants(commonMFilterList.get(i).getEnumeratedConstants());
                            refined.setCloudSearchIndex(commonMFilterList.get(i).getCloudSearchIndex());
                            commonRefinedFilterSortList.add(refined);
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

                        searchFiltersAdapter = new SearchFiltersAdapter(otherFilterSortedFinalList, mContext, SearchResultsActivity.this, mInterface, recycle_filter, passformula, subdomain, "Select", passedFormulaArray, "");
                        recycle_filter.setAdapter(searchFiltersAdapter);
                        searchFiltersAdapter.setSelectedDomain(subdomain);
                        searchFiltersAdapter.notifyDataSetChanged();
                        Config.logV("commonRefinedFilterList Filter size @@@@@@@@@" + otherFilterSortedFinalList.size());


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
                    Config.closeDialog(SearchResultsActivity.this, mDialog);
            }
        });
    }

    private void ApiSubDomainRefinedFilters(final RecyclerView recycle_filter, final String subdomain, final String domain, final String passformula, final ArrayList<String> passFormulaArray, final String subdomainDisplayName) {
        Config.logV("URL----------SUBDOMAIN--@@@@@@@@@@@@@@@@@@");
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(SearchResultsActivity.this, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<RefinedFilters> call = apiService.getSubDomainMoreFilters(subdomain, domain);
        call.enqueue(new Callback<RefinedFilters>() {
            @Override
            public void onResponse(Call<RefinedFilters> call, Response<RefinedFilters> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(SearchResultsActivity.this, mDialog);
                    Config.logV("URL----------SUBDOMAIN-----" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------SUBDOMAIN-------------------" + response.code());
                    if (response.code() == 200) {
                        Config.logV("Response----------------");
                        commonsubDomainFilterList.clear();
                        commonsubDomainFilterSortList.clear();
                        commonsubDomainFilterList = new ArrayList<>();
                        commonsubDomainFilterList = response.body().getRefinedFilters();
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SearchResultsActivity.this);
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
                        if (passFormulaArray.size() == 0) {
                            ArrayList<String> emptyList = new ArrayList<>();
                            searchFiltersAdapter = new SearchFiltersAdapter(mergedOtherFilterList, mContext, SearchResultsActivity.this, mInterface, recycle_filter, passformula, domain, subdomain, emptyList, subdomainDisplayName);
                            recycle_filter.setAdapter(searchFiltersAdapter);
                            searchFiltersAdapter.setDomainAndSubDomain(domain, subdomain);
                            searchFiltersAdapter.notifyDataSetChanged();
                        } else {
                            searchFiltersAdapter = new SearchFiltersAdapter(mergedOtherFilterList, mContext, SearchResultsActivity.this, mInterface, recycle_filter, passformula, domain, subdomain, passFormulaArray, subdomainDisplayName);
                            recycle_filter.setAdapter(searchFiltersAdapter);
                            searchFiltersAdapter.setDomainAndSubDomain(domain, subdomain);
                            searchFiltersAdapter.notifyDataSetChanged();
                        }
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
                    Config.closeDialog(SearchResultsActivity.this, mDialog);
            }
        });
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

        Config.logV("ApiMoreRefinedFilters2222222 @@@@@@@@@@@@@" + passformula);
        spinnerDomainSelectedFilter = domainame;
        ArrayList<String> emptyList = new ArrayList<>();
        ApiMoreRefinedFilters(recyclepopup, domainame, "yes", passformula, emptyList, show_subdomain);
    }

    String subdomainDisplayNamePass = "";

    @Override
    public void onMethodSubDomainFilter(String passformula, RecyclerView recyclepopup, String subdomainame, String DomainName, String subdomainDisplayName) {

        subdomainDisplayNamePass = subdomainDisplayName;
        ArrayList<String> emptyList = new ArrayList<>();
        if (DomainName.equalsIgnoreCase("")) {
            ApiSubDomainRefinedFilters(recyclepopup, subdomainame, selectedDomain, passformula, emptyList, subdomainDisplayName);
        } else {
            ApiSubDomainRefinedFilters(recyclepopup, subdomainame, DomainName, passformula, emptyList, subdomainDisplayName);
        }
    }

    @Override
    public void onMethodQuery(ArrayList<String> sFormula, ArrayList<String> keyFormula, ArrayList<FilterChips> chipsList) {

        passList = sFormula;
        keyList = keyFormula;
        filterChipsList = chipsList;
        filterChipsAdapter = new FilterChipsAdapter(mContext, filterChipsList, iClearFilter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.HORIZONTAL, false);
        rvChips.setLayoutManager(gridLayoutManager);
        rvChips.setAdapter(filterChipsAdapter);

        Set<String> set = new HashSet<>(sFormula);
        sFormula.clear();
        sFormula.addAll(set);

        for (String str : sFormula) {

            Config.logV("PRINT SSSSSSFFFF@ ####@@@@@@@@@@@@@@" + str);
        }

        for (String str : keyFormula) {

            Config.logV("PRINT Key@ ####@@@@@@@@@@@@@@" + str);
        }

        Config.logV("PASSED FORMULA ARRAY WWWW GGGG@@" + sFormula.size());
        ArrayList<String> sFormulaModified = new ArrayList<>();
        if (sFormula.size() > 0) {
            if (!spinnerTxt.equalsIgnoreCase("All")) {
                for (int i = 0; i < sFormula.size(); i++) {
                    if (sFormula.get(i).contains(selectedDomain)) {
                        sFormula.remove(i);
                        sFormulaModified = sFormula;
//                        txtrefinedsearch.setText("Refine Search (" + sFormulaModified.size() + ") ");
                    } else {
//                        txtrefinedsearch.setText("Refine Search (" + sFormula.size() + ") ");
                    }
                }
            } else {
//                txtrefinedsearch.setText("Refine Search (" + sFormula.size() + ") ");
            }

        } else {
//            txtrefinedsearch.setText("Refine Search ");
        }
        String queryFormula = "";
        int count = 0;
        boolean match = false;
        for (int i = 0; i < keyFormula.size(); i++) {


            for (int j = 0; j < sFormula.size(); j++) {


                String splitsFormula[] = sFormula.get(j).toString().split(":");
                //Config.logV("PRINT Key ##"+splitsFormula[0]);
                splitsFormula[0] = splitsFormula[0].replace("not", "").trim();
                Config.logV("PRINT EEEE" + splitsFormula[0]);
                Config.logV("PRINT EEEE DDDD" + keyFormula.get(i).toString());
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


        passedFormulaArray.clear();
        passedFormulaArray.addAll(sFormula);

        /*if(!domian.equalsIgnoreCase("")){
             mDomainSpinner=domian;
        }

        if(!subdomain.equalsIgnoreCase("")){
            subdomainName=subdomain;
        }*/


      /*  refinedSelectedItems.clear();
        refinedSelectedItems.addAll(sFormula);*/
        MoreItemClick(queryFormula, query);
        Config.logV("PRINT VAL FORMULA@@" + queryFormula);

    }

    public void MoreItemClick(String pass_formula, String query) {
        //  mSearchView.setQuery("", false);
        if (mtyp == null) {
            mtyp = "city";
        }
        LanLong Lanlong = getLocationNearBy(Double.parseDouble(latitude), Double.parseDouble(longitude), mtyp);
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
//        searchResultsAdapter.clear();
        passformula = pass_formula;
     /*   final String query1 = "(and location1:['11.751416900900901,75.3701820990991','9.9496150990991,77.171983900900'] " + querycreate + ")";
        final String pass1 = "haversin(11.751416900900901,75.3701820990991, location1.latitude, location1.longitude)";*/
        // final String query1 = "(and location1:" + locationRange + ")";
        final String query1;
        if (query.equalsIgnoreCase("") || passformula.contains("sector") || passformula.contains("sub_sector")) {
            query1 = "(and location1:" + locationRange + ")";
        } else {
            query1 = query;
        }
        final String pass1 = "haversin(" + latitude + "," + longitude + ", location1.latitude, location1.longitude)";
        query = query1;
        url = pass1;
        searchQuery = query;
        Config.logV("MAin PAge&&&&&&&&&&&&Item CLICK &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&7" + query);
        Config.logV("SearchList URL ITEM CLICK SPINNER 222@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//        ApiSEARCHAWSLoadFirstData(searchQuery, url, sort);
    }


    @Override
    public void onMethodFirstCoupn(String uniqueid) {

    }

    @Override
    public void onMethodJdn(String uniqueid) {

        Intent jdnIntent = new Intent(SearchResultsActivity.this, JdnActivity.class);
        jdnIntent.putExtra("uniqueID", uniqueid);
        startActivity(jdnIntent);
 
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

    @Override
    public void clearSelectedFilter(FilterChips filterChips, int position) {

        searchFiltersAdapter.onChipRemoved(filterChips.getView());
        ApiSEARCHAWSLoadFirstData(searchQuery, url, sort);
    }
}
