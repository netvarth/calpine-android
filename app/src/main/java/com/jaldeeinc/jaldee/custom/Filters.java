//package com.jaldeeinc.jaldee.custom;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.os.Bundle;
//import android.widget.GridLayout;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.jaldeeinc.jaldee.Interface.IFilterOptions;
//import com.jaldeeinc.jaldee.Interface.ISendMessage;
//import com.jaldeeinc.jaldee.R;
//import com.jaldeeinc.jaldee.adapter.DomainsAdapter;
//import com.jaldeeinc.jaldee.adapter.MoreFilterAdapter;
//import com.jaldeeinc.jaldee.adapter.SearchFiltersAdapter;
//import com.jaldeeinc.jaldee.adapter.UserServicesAdapter;
//import com.jaldeeinc.jaldee.callback.AdapterCallback;
//import com.jaldeeinc.jaldee.common.Config;
//import com.jaldeeinc.jaldee.connection.ApiClient;
//import com.jaldeeinc.jaldee.connection.ApiInterface;
//import com.jaldeeinc.jaldee.database.DatabaseHandler;
//import com.jaldeeinc.jaldee.model.Domain_Spinner;
//import com.jaldeeinc.jaldee.model.LanLong;
//import com.jaldeeinc.jaldee.model.SearchModel;
//import com.jaldeeinc.jaldee.response.RefinedFilters;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.Set;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class Filters extends Dialog implements IFilterOptions {
//
//    private Context context;
//    private RecyclerView rvFilters;
//    private SearchFiltersAdapter searchFiltersAdapter;
//    private LinearLayoutManager linearLayoutManager;
//    private IFilterOptions iFilterOptions;
//    ArrayList<Domain_Spinner> domainList = new ArrayList<>();
//    DatabaseHandler databaseHandler;
//    private boolean showSubDomain = false;
//    ArrayList<String> passedFormulaArray = new ArrayList<>();
//    private String selectedDomain = "";
//    private String subDomainName = "";
//    private String subDomainquery = "";
//    ArrayList<SearchModel> mSubDomain = new ArrayList<>();
//    ArrayList<RefinedFilters> commonFilterList = new ArrayList<>();
//
//    ArrayList<RefinedFilters> commonFilterSortList = new ArrayList<>();
//
//    ArrayList<RefinedFilters> commonRefinedFilterList = new ArrayList<>();
//
//    ArrayList<RefinedFilters> commonRefinedFilterSortList = new ArrayList<>();
//
//    ArrayList<RefinedFilters> commonsubDomainFilterList = new ArrayList<>();
//
//    ArrayList<RefinedFilters> commonsubDomainFilterSortList = new ArrayList<>();
//
//    ArrayList<RefinedFilters> otherFilterSortedFinalList = new ArrayList<>();
//
//    String sort = "";
//    String searchQuery = "";
//    String url = "";
//
//
//    public Filters(@NonNull Context context, String spinnerTxt, boolean show_subdomain, String subdomainName, String subdomainquery) {
//        super(context);
//        this.context = context;
//        this.selectedDomain = spinnerTxt;
//        this.showSubDomain = show_subdomain;
//        this.subDomainName = subdomainName;
//        this.subDomainquery = subdomainquery;
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.filter);
//
//        iFilterOptions = (IFilterOptions) this;
//
//      rvFilters = findViewById(R.id.rv_domains);
//
//        if (passedFormulaArray.size() == 0) {
//
//            if (selectedDomain != null && selectedDomain.equalsIgnoreCase("All")) {
//                ApiFilters( rvFilters,"Select", passedFormulaArray);
//
//            } else {
//
//                ApiMoreRefinedFilters(rvFilters, selectedDomain, "No", "", passedFormulaArray, showSubDomain);
//                if (showSubDomain) {
//                    ArrayList<String> emptyList = new ArrayList<>();
//                    ApiSubDomainRefinedFilters(rvFilters, subDomainName, selectedDomain, subDomainquery, emptyList, "");
//                }
//            }
//        } else {
//            if (selectedDomain.equalsIgnoreCase("All")) {
//
//                for (int i = 0; i < passedFormulaArray.size(); i++) {
//                    Config.logV("PRINT VAL FORMULA@@111HHHHH" + passedFormulaArray.get(i));
//
//                }
//
//                String domainSelect = "Select";
//                for (int i = 0; i < passedFormulaArray.size(); i++) {
//                    String splitsFormula[] = passedFormulaArray.get(i).toString().split(":");
//                    if (splitsFormula[0].equalsIgnoreCase("sector")) {
//
//                        Config.logV("Sector @@@@@@@@@@@@@@@@@@@@@@@@" + splitsFormula[1]);
//                        domainSelect = splitsFormula[1].replace("'", "");
//                    }
//                }
//
//                String subdomain = "Select";
//                for (int i = 0; i < passedFormulaArray.size(); i++) {
//                    String splitsFormula[] = passedFormulaArray.get(i).toString().split(":");
//                    if (splitsFormula[0].equalsIgnoreCase("sub_sector")) {
//
//                        Config.logV("subdomain @@@@@@@@@@@@@@@@@@@@@@@@" + splitsFormula[1]);
//                        subdomain = splitsFormula[1].replace("'", "");
//                    }
//                }
//                if (domainSelect.equalsIgnoreCase("Select")) {
//                    ApiFilters(rvFilters, "Select", passedFormulaArray);
//                } else {
//
//                    //ApiMoreRefinedFilters(recycle_morefilter, domainSelect, "No", "", passedFormulaArray, show_subdomain);
//
//                    if (subdomain.equalsIgnoreCase("Select")) {
//
//                        ApiMoreRefinedFilters(rvFilters, domainSelect, "yes", "", passedFormulaArray, showSubDomain);
//                    } else {
//
//                        //ApiMoreRefinedFilters(recycle_morefilter, domainSelect, "yes", passformula, passedFormulaArray, show_subdomain);
//                        // empty subDomain for now
//                        ApiSubDomainRefinedFilters(rvFilters, subdomain, domainSelect, subDomainquery, passedFormulaArray, "");
//                    }
//                }
//
//
//            } else {
//                String subdomain = "Select";
//                for (int i = 0; i < passedFormulaArray.size(); i++) {
//                    String splitsFormula[] = passedFormulaArray.get(i).toString().split(":");
//                    if (splitsFormula[0].equalsIgnoreCase("sub_sector")) {
//
//                        Config.logV("subdomain @@@@@@@@@@@@@@@@@@@@@@@@" + splitsFormula[1]);
//                        subdomain = splitsFormula[1].replace("'", "");
//                    }
//                }
//                if (subdomain.equalsIgnoreCase("Select") && subDomainName.equalsIgnoreCase("")) {
//
//                    ApiMoreRefinedFilters(rvFilters, selectedDomain, "No", "", passedFormulaArray, showSubDomain);
//                } else {
//                    if (subdomain.equalsIgnoreCase("Select")) {
//                        subdomain = subDomainName;
//                    }
//                    // empty for now
//                    ApiSubDomainRefinedFilters(rvFilters, subdomain, selectedDomain, subDomainquery, passedFormulaArray, "");
//                }
//
//            }
//        }
//
//
//    }
//
//    private void ApiFilters(final RecyclerView recycle_filter, final String domainSelect, final ArrayList<String> passedFormulaArray) {
//        ApiInterface apiService =
//                ApiClient.getClient(context).create(ApiInterface.class);
//        final Dialog mDialog = Config.getProgressDialog(context, context.getResources().getString(R.string.dialog_log_in));
//        mDialog.show();
//        Call<RefinedFilters> call = apiService.getFilters();
//        call.enqueue(new Callback<RefinedFilters>() {
//            @Override
//            public void onResponse(Call<RefinedFilters> call, Response<RefinedFilters> response) {
//                try {
//                    if (mDialog.isShowing())
//                        Config.closeDialog(getOwnerActivity(), mDialog);
//                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
//                    Config.logV("Response--code------Filters-------------------" + response.code());
//                    if (response.code() == 200) {
//                        Config.logV("Response----------------");
//                        commonFilterList.clear();
//                        commonFilterList = response.body().getCommonFilters();
//                        Config.logV("Common Filters----------------" + commonFilterList.size());
//                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getOwnerActivity());
//                        recycle_filter.setLayoutManager(mLayoutManager);
//                        int listsize = commonFilterList.size();
//                        boolean otherFlag = false;
//                        commonFilterSortList.clear();
//                        ArrayList booleanVariables = new ArrayList();
//                        ArrayList booleanVariablesValue = new ArrayList();
//                        ArrayList booleanVariablesName = new ArrayList();
//                        booleanVariables.clear();
//                        booleanVariablesValue.clear();
//                        booleanVariablesName.clear();
//                        ArrayList<Domain_Spinner> domainNewlist = new ArrayList<>();
//                        domainNewlist.addAll(domainList);
//                        for (int i = 0; i < domainNewlist.size(); i++) {
//                            if (domainNewlist.get(i).getDomain().equalsIgnoreCase("All")) {
//                                domainNewlist.remove(i);
//                            }
//                        }
//                        Domain_Spinner domain = new Domain_Spinner("Select", "Select");
//                        domainNewlist.add(0, domain);
//                        RefinedFilters refined1 = new RefinedFilters();
//                        refined1.setDisplayName("Select Service Domain");
//                        refined1.setDataType("Spinner");
//                        refined1.setExpand(false);
//                        refined1.setName("domain");
//                        refined1.setEnumeratedConstants(domainNewlist);
//                        refined1.setCloudSearchIndex("domain");
//                        commonFilterSortList.add(refined1);
//                        for (int i = 0; i < commonFilterList.size(); i++) {
//                            if (commonFilterList.get(i).getDataType().equalsIgnoreCase("Boolean")) {
//                                booleanVariables.add(commonFilterList.get(i).getDisplayName());
//                                booleanVariablesValue.add(commonFilterList.get(i).getCloudSearchIndex());
//                                booleanVariablesName.add(commonFilterList.get(i).getName());
//                                if (!otherFlag) {
//                                    RefinedFilters refined = new RefinedFilters();
//                                    refined.setDisplayName("Other Filter");
//                                    refined.setDataType(commonFilterList.get(i).getDataType());
//                                    otherFlag = true;
//                                    refined.setItemName(booleanVariables);
//                                    refined.setExpand(false);
//                                    refined.setPassName(booleanVariablesName);
//                                    refined.setCloudIndexvalue(booleanVariablesValue);
//                                    commonFilterSortList.add(refined);
//                                }
//                            } else {
//                                RefinedFilters refined = new RefinedFilters();
//                                refined.setDisplayName(commonFilterList.get(i).getDisplayName());
//                                refined.setDataType(commonFilterList.get(i).getDataType());
//                                refined.setExpand(false);
//                                refined.setName(commonFilterList.get(i).getName());
//                                refined.setEnumeratedConstants(commonFilterList.get(i).getEnumeratedConstants());
//                                refined.setCloudSearchIndex(commonFilterList.get(i).getCloudSearchIndex());
//                                commonFilterSortList.add(refined);
//                            }
//                        }
//                        Config.logV("Comon Filter size @@@@@@@@@" + commonFilterSortList.size());
//                        searchFiltersAdapter = new SearchFiltersAdapter(commonFilterSortList, context, getOwnerActivity(), , recycle_filter, "", domainSelect, "Select", passedFormulaArray, "");
//                        recycle_filter.setAdapter(searchFiltersAdapter);
//                        searchFiltersAdapter.notifyDataSetChanged();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<RefinedFilters> call, Throwable t) {
//                // Log error here since request failed
//                Config.logV("Fail---------------" + t.toString());
//                if (mDialog.isShowing())
//                    Config.closeDialog(getOwnerActivity(), mDialog);
//
//            }
//        });
//    }
//
//    ArrayList<RefinedFilters> commonMFilterList = new ArrayList<RefinedFilters>();
//    private void ApiMoreRefinedFilters(final RecyclerView recycle_filter, final String subdomain, final String showdomain, final String passformula, final ArrayList<String> passedFormulaArray, final boolean show_subdomain) {
//        Config.logV("show_subdomain @@@@@@@@@@@@@@" + show_subdomain);
//        ApiInterface apiService =
//                ApiClient.getClient(getContext()).create(ApiInterface.class);
//        final Dialog mDialog = Config.getProgressDialog(getContext(), context.getResources().getString(R.string.dialog_log_in));
//        mDialog.show();
//        Call<RefinedFilters> call = apiService.getMoreFilters(subdomain);
//        call.enqueue(new Callback<RefinedFilters>() {
//            @Override
//            public void onResponse(Call<RefinedFilters> call, Response<RefinedFilters> response) {
//                try {
//                    if (mDialog.isShowing())
//                        Config.closeDialog(getOwnerActivity(), mDialog);
//                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
//                    Config.logV("Response--code------Filters-------------------" + response.code());
//                    if (response.code() == 200) {
//                        Config.logV("Response----------------");
//                        commonRefinedFilterList.clear();
//                        commonRefinedFilterSortList.clear();
//                        commonRefinedFilterList = new ArrayList<>();
//                        commonRefinedFilterList = response.body().getRefinedFilters();
//                        Config.logV("commonRefinedFilterList Filters----------------" + commonRefinedFilterList.size());
//                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getOwnerActivity());
//                        recycle_filter.setLayoutManager(mLayoutManager);
//                        commonMFilterList.clear();
//                        commonMFilterList = response.body().getCommonFilters();
//                        Config.logV("Common Filters----------------" + commonMFilterList.size());
//                        boolean otherFlag = false;
//                        ArrayList booleanVariables = new ArrayList();
//                        ArrayList booleanVariablesValue = new ArrayList();
//                        ArrayList booleanVariablesName = new ArrayList();
//                        booleanVariables.clear();
//                        booleanVariablesValue.clear();
//                        booleanVariablesName.clear();
//                        if (showdomain.equalsIgnoreCase("yes")) {
//                            ArrayList<Domain_Spinner> domainNewlist = new ArrayList<>();
//                            domainNewlist.addAll(domainList);
//                            for (int i = 0; i < domainNewlist.size(); i++) {
//                                if (domainNewlist.get(i).getDomain().equalsIgnoreCase("All")) {
//                                    domainNewlist.remove(i);
//                                }
//                            }
//                            Domain_Spinner domain = new Domain_Spinner("Select", "Select");
//                            domainNewlist.add(0, domain);
//                            RefinedFilters refined1 = new RefinedFilters();
//                            refined1.setDisplayName("Select Service Domain");
//                            refined1.setDataType("Spinner");
//                            refined1.setExpand(false);
//                            refined1.setName("domain");
//                            refined1.setEnumeratedConstants(domainNewlist);
//                            refined1.setCloudSearchIndex("domain");
//                            commonRefinedFilterSortList.add(0, refined1);
//                        }
//                        if (!show_subdomain || selectedDomain.equalsIgnoreCase("All")) {
//                            ArrayList<SearchModel> subdomainList = new ArrayList<>();
//                            subdomainList.clear();
//                            SearchModel search1 = new SearchModel();
//                            search1.setDisplayname("Select");
//                            search1.setSector("Select");
//                            search1.setName("Select");
//                            search1.setQuery("");
//                            subdomainList.add(search1);
//                            for (int i = 0; i < mSubDomain.size(); i++) {
//                                if (mSubDomain.get(i).getSector().equalsIgnoreCase(subdomain)) {
//                                    SearchModel search = new SearchModel();
//                                    search.setDisplayname(mSubDomain.get(i).getDisplayname());
//                                    search.setSector(mSubDomain.get(i).getSector());
//                                    search.setName(mSubDomain.get(i).getName());
//                                    search.setQuery(mSubDomain.get(i).getQuery());
//                                    subdomainList.add(search);
//                                }
//                            }
//                            RefinedFilters refined2 = new RefinedFilters();
//                            refined2.setDisplayName("Select Service specialization or Occupation");
//                            refined2.setDataType("Spinner_subdomain");
//                            refined2.setExpand(false);
//                            refined2.setName("subdomain");
//                            refined2.setEnumeratedConstants(subdomainList);
//                            refined2.setCloudSearchIndex("subdomain");
//                            if (showdomain.equalsIgnoreCase("yes")) {
//                                commonRefinedFilterSortList.add(1, refined2);
//                            } else {
//                                commonRefinedFilterSortList.add(0, refined2);
//                            }
//                        }
//                        for (int i = 0; i < commonRefinedFilterList.size(); i++) {
//                            RefinedFilters refined = new RefinedFilters();
//                            refined.setDisplayName(commonRefinedFilterList.get(i).getDisplayName());
//                            refined.setDataType(commonRefinedFilterList.get(i).getDataType());
//                            refined.setExpand(false);
//                            refined.setName(commonRefinedFilterList.get(i).getName());
//                            refined.setEnumeratedConstants(commonRefinedFilterList.get(i).getEnumeratedConstants());
//                            refined.setCloudSearchIndex(commonRefinedFilterList.get(i).getCloudSearchIndex());
//                            commonRefinedFilterSortList.add(refined);
//                        }
//                        for (int i = 0; i < commonMFilterList.size(); i++) {
//                           /* if (commonMFilterList.get(i).getDataType().equalsIgnoreCase("Boolean")) {
//                                booleanVariables.add(commonMFilterList.get(i).getDisplayName());
//                                booleanVariablesValue.add(commonMFilterList.get(i).getCloudSearchIndex());
//                                booleanVariablesName.add(commonMFilterList.get(i).getName());
//                                if (!otherFlag) {
//                                    RefinedFilters refined = new RefinedFilters();
//                                    refined.setDisplayName("Other Filter");
//                                    refined.setDataType(commonMFilterList.get(i).getDataType());
//                                    otherFlag = true;
//                                    refined.setItemName(booleanVariables);
//                                    refined.setExpand(false);
//                                    refined.setPassName(booleanVariablesName);
//                                    refined.setCloudIndexvalue(booleanVariablesValue);
//                                    commonRefinedFilterSortList.add(refined);
//                                }
//                            } else {*/
//                            RefinedFilters refined = new RefinedFilters();
//                            refined.setDisplayName(commonMFilterList.get(i).getDisplayName());
//                            refined.setDataType(commonMFilterList.get(i).getDataType());
//                            refined.setExpand(false);
//                            refined.setName(commonMFilterList.get(i).getName());
//                            refined.setEnumeratedConstants(commonMFilterList.get(i).getEnumeratedConstants());
//                            refined.setCloudSearchIndex(commonMFilterList.get(i).getCloudSearchIndex());
//                            commonRefinedFilterSortList.add(refined);
//                            // }
//                        }
//                        otherFilterSortedFinalList.clear();
//                        for (int i = 0; i < commonRefinedFilterSortList.size(); i++) {
//                            if (commonRefinedFilterSortList.get(i).getDataType().equalsIgnoreCase("Boolean")) {
//                                booleanVariables.add(commonRefinedFilterSortList.get(i).getDisplayName());
//                                booleanVariablesValue.add(commonRefinedFilterSortList.get(i).getCloudSearchIndex());
//                                booleanVariablesName.add(commonRefinedFilterSortList.get(i).getName());
//                                if (!otherFlag) {
//                                    RefinedFilters refined = new RefinedFilters();
//                                    refined.setDisplayName("Other Filter");
//                                    refined.setDataType(commonRefinedFilterSortList.get(i).getDataType());
//                                    otherFlag = true;
//                                    refined.setItemName(booleanVariables);
//                                    refined.setExpand(false);
//                                    refined.setPassName(booleanVariablesName);
//                                    refined.setCloudIndexvalue(booleanVariablesValue);
//                                    otherFilterSortedFinalList.add(refined);
//                                }
//                            } else {
//                                RefinedFilters refined = new RefinedFilters();
//                                refined.setDisplayName(commonRefinedFilterSortList.get(i).getDisplayName());
//                                refined.setDataType(commonRefinedFilterSortList.get(i).getDataType());
//                                refined.setExpand(false);
//                                refined.setName(commonRefinedFilterSortList.get(i).getName());
//                                refined.setEnumeratedConstants(commonRefinedFilterSortList.get(i).getEnumeratedConstants());
//                                refined.setCloudSearchIndex(commonRefinedFilterSortList.get(i).getCloudSearchIndex());
//                                otherFilterSortedFinalList.add(refined);
//                            }
//                        }
//                        Config.logV("Comon Filter size @@@@@@@@@" + otherFilterSortedFinalList.size());
//                        searchFiltersAdapter = new SearchFiltersAdapter(otherFilterSortedFinalList, context, getOwnerActivity(), iFilterOptions, recycle_filter, passformula, subdomain, "Select", passedFormulaArray, "");
//                        recycle_filter.setAdapter(searchFiltersAdapter);
//                        searchFiltersAdapter.notifyDataSetChanged();
//                        Config.logV("commonRefinedFilterList Filter size @@@@@@@@@" + otherFilterSortedFinalList.size());
//
//                       /* commonFilterSortList.addAll(1,commonRefinedFilterSortList);
//                        recycle_filter.setAdapter(mMoreAdapter);
//                        mMoreAdapter.notifyDataSetChanged();*/
//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<RefinedFilters> call, Throwable t) {
//                // Log error here since request failed
//                Config.logV("Fail---------------" + t.toString());
//                if (mDialog.isShowing())
//                    Config.closeDialog(getOwnerActivity(), mDialog);
//            }
//        });
//    }
//    private void ApiSubDomainRefinedFilters(final RecyclerView recycle_filter, final String subdomain, final String domain, final String passformula, final ArrayList<String> passFormulaArray, final String subdomainDisplayName) {
//        Config.logV("URL----------SUBDOMAIN--@@@@@@@@@@@@@@@@@@");
//        ApiInterface apiService =
//                ApiClient.getClient(getOwnerActivity()).create(ApiInterface.class);
//        final Dialog mDialog = Config.getProgressDialog(getOwnerActivity(), context.getResources().getString(R.string.dialog_log_in));
//        mDialog.show();
//        Call<RefinedFilters> call = apiService.getSubDomainMoreFilters(subdomain, domain);
//        call.enqueue(new Callback<RefinedFilters>() {
//            @Override
//            public void onResponse(Call<RefinedFilters> call, Response<RefinedFilters> response) {
//                try {
//                    if (mDialog.isShowing())
//                        Config.closeDialog(getOwnerActivity(), mDialog);
//                    Config.logV("URL----------SUBDOMAIN-----" + response.raw().request().url().toString().trim());
//                    Config.logV("Response--code------SUBDOMAIN-------------------" + response.code());
//                    if (response.code() == 200) {
//                        Config.logV("Response----------------");
//                        commonsubDomainFilterList.clear();
//                        commonsubDomainFilterSortList.clear();
//                        commonsubDomainFilterList = new ArrayList<>();
//                        commonsubDomainFilterList = response.body().getRefinedFilters();
//                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getOwnerActivity());
//                        recycle_filter.setLayoutManager(mLayoutManager);
//                        for (int i = 0; i < commonsubDomainFilterList.size(); i++) {
//                            RefinedFilters refined = new RefinedFilters();
//                            refined.setDisplayName(commonsubDomainFilterList.get(i).getDisplayName());
//                            refined.setDataType(commonsubDomainFilterList.get(i).getDataType());
//                            refined.setExpand(false);
//                            refined.setName(commonsubDomainFilterList.get(i).getName());
//                            refined.setEnumeratedConstants(commonsubDomainFilterList.get(i).getEnumeratedConstants());
//                            refined.setCloudSearchIndex(commonsubDomainFilterList.get(i).getCloudSearchIndex());
//                            commonsubDomainFilterSortList.add(refined);
//                        }
//
//                        ArrayList<RefinedFilters> mergedList = new ArrayList<>();
//                        mergedList.clear();
//                        mergedList.addAll(commonRefinedFilterSortList);
//                        mergedList.addAll(commonsubDomainFilterSortList);
//                        boolean otherFlag = false;
//                        ArrayList booleanVariables = new ArrayList();
//                        ArrayList booleanVariablesValue = new ArrayList();
//                        ArrayList booleanVariablesName = new ArrayList();
//                        booleanVariables.clear();
//                        booleanVariablesValue.clear();
//                        booleanVariablesName.clear();
//                        ArrayList<RefinedFilters> mergedOtherFilterList = new ArrayList<>();
//                        mergedOtherFilterList.clear();
//                        for (int i = 0; i < mergedList.size(); i++) {
//                            if (mergedList.get(i).getDataType().equalsIgnoreCase("Boolean")) {
//                                booleanVariables.add(mergedList.get(i).getDisplayName());
//                                booleanVariablesValue.add(mergedList.get(i).getCloudSearchIndex());
//                                booleanVariablesName.add(mergedList.get(i).getName());
//                                if (!otherFlag) {
//                                    RefinedFilters refined = new RefinedFilters();
//                                    refined.setDisplayName("Other Filter");
//                                    refined.setDataType(mergedList.get(i).getDataType());
//                                    otherFlag = true;
//                                    refined.setItemName(booleanVariables);
//                                    refined.setExpand(false);
//                                    refined.setPassName(booleanVariablesName);
//                                    refined.setCloudIndexvalue(booleanVariablesValue);
//                                    mergedOtherFilterList.add(refined);
//                                }
//                            } else {
//                                RefinedFilters refined = new RefinedFilters();
//                                refined.setDisplayName(mergedList.get(i).getDisplayName());
//                                refined.setDataType(mergedList.get(i).getDataType());
//                                refined.setExpand(false);
//                                refined.setName(mergedList.get(i).getName());
//                                refined.setEnumeratedConstants(mergedList.get(i).getEnumeratedConstants());
//                                refined.setCloudSearchIndex(mergedList.get(i).getCloudSearchIndex());
//                                mergedOtherFilterList.add(refined);
//                            }
//                        }
//                        Config.logV("YYYYmergedList@@@@@@@@@" + mergedOtherFilterList.size());
//                        if (passFormulaArray.size() == 0) {
//                            ArrayList<String> emptyList = new ArrayList<>();
//                            searchFiltersAdapter = new SearchFiltersAdapter(mergedOtherFilterList, context, getOwnerActivity(), iFilterOptions, recycle_filter, passformula, domain, subdomain, emptyList, subdomainDisplayName);
//                            recycle_filter.setAdapter(searchFiltersAdapter);
//                            searchFiltersAdapter.notifyDataSetChanged();
//                        } else {
//                            searchFiltersAdapter = new SearchFiltersAdapter(mergedOtherFilterList, context, getOwnerActivity(), iFilterOptions, recycle_filter, passformula, domain, subdomain, passFormulaArray, subdomainDisplayName);
//                            recycle_filter.setAdapter(searchFiltersAdapter);
//                            searchFiltersAdapter.notifyDataSetChanged();
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<RefinedFilters> call, Throwable t) {
//                // Log error here since request failed
//                Config.logV("SUBDOMAIN Fail---------------" + t.toString());
//                if (mDialog.isShowing())
//                    Config.closeDialog(getOwnerActivity(), mDialog);
//            }
//        });
//    }
//
//
//    @Override
//    public void onDomainSelect(Domain_Spinner domain) {
//
//
//
//    }
//
//    @Override
//    public void onMethodFilterRefined(String passformula, RecyclerView recyclepopup, String domainame) {
//
//
//    }
//
//    @Override
//    public void onMethodQuery(ArrayList<String> sFormula, ArrayList<String> keyFormula) {
//
//
//        Set<String> set = new HashSet<>(sFormula);
//        sFormula.clear();
//        sFormula.addAll(set);
//
//        for (String str : sFormula) {
//
//            Config.logV("PRINT SSSSSSFFFF@ ####@@@@@@@@@@@@@@" + str);
//        }
//
//        for (String str : keyFormula) {
//
//            Config.logV("PRINT Key@ ####@@@@@@@@@@@@@@" + str);
//        }
//
//        Config.logV("PASSED FORMULA ARRAY WWWW GGGG@@" + sFormula.size());
//        ArrayList<String> sFormulaModified = new ArrayList<>();
//        if (sFormula.size() > 0) {
//            if (!selectedDomain.equalsIgnoreCase("All")) {
//                for (int i = 0; i < sFormula.size(); i++) {
//                    if (sFormula.get(i).contains(selectedDomain)) {
//                        sFormula.remove(i);
//                        sFormulaModified = sFormula;
////                        txtrefinedsearch.setText("Refine Search (" + sFormulaModified.size() + ") ");
//                    } else {
////                        txtrefinedsearch.setText("Refine Search (" + sFormula.size() + ") ");
//                    }
//                }
//            } else {
////                txtrefinedsearch.setText("Refine Search (" + sFormula.size() + ") ");
//            }
//
//        } else {
////            txtrefinedsearch.setText("Refine Search ");
//        }
//        String queryFormula = "";
//        int count = 0;
//        boolean match = false;
//        for (int i = 0; i < keyFormula.size(); i++) {
//
//
//            for (int j = 0; j < sFormula.size(); j++) {
//
//
//                String splitsFormula[] = sFormula.get(j).toString().split(":");
//                //Config.logV("PRINT Key ##"+splitsFormula[0]);
//                splitsFormula[0] = splitsFormula[0].replace("not", "").trim();
//                Config.logV("PRINT EEEE" + splitsFormula[0]);
//                Config.logV("PRINT EEEE DDDD" + keyFormula.get(i).toString());
//                if (splitsFormula[0].equalsIgnoreCase(keyFormula.get(i).toString())) {
//
//                    // Config.logV("PRINT Key TRUE @@@@@@@@@@@ ##"+splitsFormula[0]);
//                    match = true;
//                    count++;
//
//                    if (count == 1)
//                        queryFormula += "(" + sFormula.get(j).toString();
//                    else
//                        queryFormula += sFormula.get(j).toString();
//
//                }
//
//
//            }
//
//
//            if (match) {
//                if (count > 1) {
//                    queryFormula += ")";
//                    match = false;
//                }
//            }
//            Config.logV("SortString@@@@@@@@@@" + queryFormula + "Count @@@@@" + count);
//
//            if (count > 1) {
//                queryFormula = queryFormula.replace("(" + keyFormula.get(i), "( or " + keyFormula.get(i));
//                Config.logV("SortString ^^^^^^^^^^^^^^^@@@@@@@@@@" + queryFormula);
//            } else {
//                queryFormula = queryFormula.replace("(" + keyFormula.get(i), keyFormula.get(i));
//
//            }
//
//            count = 0;
//        }
//
//
//        passedFormulaArray.clear();
//        passedFormulaArray.addAll(sFormula);
//
//        /*if(!domian.equalsIgnoreCase("")){
//             mDomainSpinner=domian;
//        }
//
//        if(!subdomain.equalsIgnoreCase("")){
//            subdomainName=subdomain;
//        }*/
//
//
//      /*  refinedSelectedItems.clear();
//        refinedSelectedItems.addAll(sFormula);*/
////        MoreItemClick(queryFormula, query);
//        Config.logV("PRINT VAL FORMULA@@" + queryFormula);
//
//    }
//
//
//
//
//    @Override
//    public void onMethodSubDomainFilter(String query, RecyclerView recyclview_popup, String name, String domainSelect, String displayname) {
//
//    }
//}
