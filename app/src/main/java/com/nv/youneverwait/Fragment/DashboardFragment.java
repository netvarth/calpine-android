package com.nv.youneverwait.Fragment;


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
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.nv.youneverwait.activities.SearchLocationActivity;
import com.nv.youneverwait.adapter.ActiveCheckInAdapter;
import com.nv.youneverwait.adapter.SearchListAdpter;
import com.nv.youneverwait.callback.ActiveAdapterOnCallback;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.custom.CustomTypefaceSpan;
import com.nv.youneverwait.model.Domain_Spinner;
import com.nv.youneverwait.model.LanLong;
import com.nv.youneverwait.model.ListCell;
import com.nv.youneverwait.model.SearchModel;
import com.nv.youneverwait.response.ActiveCheckIn;
import com.nv.youneverwait.utils.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends RootFragment implements GoogleApiClient.ConnectionCallbacks,
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
    ArrayList<SearchModel> mSectorSearch = new ArrayList<>();
    ArrayList<SearchModel> mSectorSubSearch = new ArrayList<>();
    ArrayList<Domain_Spinner> mSubDomainSubSearch = new ArrayList<>();
    ArrayList<Domain_Spinner> mSubDomain = new ArrayList<>();
    ArrayList<Domain_Spinner> mSpecializationDomain = new ArrayList<>();
    ArrayList<Domain_Spinner> mSpecializationDomainSearch = new ArrayList<>();


    ArrayList<SearchModel> mPopularSearchList = new ArrayList<>();

    ArrayList<SearchModel> mPopular_SubSearchList = new ArrayList<>();

    ArrayList<SearchModel> mPopular_AllSearchList = new ArrayList<>();


    SearchView mSearchView;
    SearchView.SearchAutoComplete searchSrcTextView;
    String mDomainSpinner;
    SearchListAdpter listadapter;
    ArrayList<ListCell> items;

    private Location mylocation;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    static double latitude;
    static double longitude;
    TextView tv_activechkin, tv_popular;
    LinearLayout LpopularSearch, LActiveCheckin, LinearPopularSearch, LinearMorePopularSearch;
    TextView tv_More;
    boolean is_MoreClick = false;
    LinearLayout Lhome_mainlayout;
    FrameLayout mainlayout;

    String mSearchtxt;
    String mPopularSearchtxt;

    String spinnerTxtPass;
    ActiveAdapterOnCallback mInterface;
    static String mlocName;
    public void funPopulateSearchList(final ArrayList<SearchModel> mPopularSearchList) {
        if (mPopularSearchList.size() > 0) {

            is_MoreClick = false;
            LpopularSearch.setVisibility(View.VISIBLE);
            LinearPopularSearch.setVisibility(View.VISIBLE);
            LinearMorePopularSearch.setVisibility(View.GONE);
            LinearPopularSearch.removeAllViews();
            if (mPopularSearchList.size() > 6) {
                tv_More.setVisibility(View.VISIBLE);
            } else {
                tv_More.setVisibility(View.GONE);
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
                        dynaText.setTextColor(getResources().getColor(R.color.black));
                        dynaText.setPadding(15, 10, 15, 10);
                        //dynaText.setEllipsize(TextUtils.TruncateAt.END);
                        dynaText.setMaxLines(1);
                       // dynaText.setMaxEms(8);
                        dynaText.setWidth(dpToPx(130));
                        dynaText.setGravity(Gravity.CENTER);

                        params.setMargins(12, 10, 12, 0);
                        dynaText.setLayoutParams(params);


                        //   dynaText.setTag("" + i);
                        final int finalK = k;
                        dynaText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                mPopularSearchtxt = mPopularSearchList.get(finalK).getDisplayname();
                                Config.logV("Popular Text__________@@@____"+mPopularSearchtxt);
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

    TextView txt_sorry, tv_logotext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View row = inflater.inflate(R.layout.fragment_myhome, container, false);
        mRecycleActive = (RecyclerView) row.findViewById(R.id.recycleActive);
        Lhome_mainlayout = (LinearLayout) row.findViewById(R.id.homemainlayout);
        tv_logotext = (TextView) row.findViewById(R.id.logotext);
        txt_sorry = (TextView) row.findViewById(R.id.txt_sorry);
        mainlayout = (FrameLayout) row.findViewById(R.id.mainlayout);
        mCurrentLoc = (TextView) row.findViewById(R.id.currentloc);
        if (Config.isOnline(getActivity())) {
            ApiActiveCheckIn();
            ApiAWSearchDomain();
        }

        mInterface = (ActiveAdapterOnCallback) this;
        home = getParentFragment();
        //Location

        String s_currentLoc = SharedPreference.getInstance(getActivity()).getStringValue("current_loc", "");
        Config.logV("UpdateLocation noooooooooooooooooo" + s_currentLoc);
        if (!s_currentLoc.equalsIgnoreCase("no")) {
            setUpGClient();
            //Config.logV("UpdateLocation noooooooooooooooooo" + latitude);
        } else if (s_currentLoc.equalsIgnoreCase("no")) {

            latitude=Double.valueOf(SharedPreference.getInstance(mContext).getStringValue("lat",""));
            longitude=Double.valueOf(SharedPreference.getInstance(mContext).getStringValue("longitu",""));
            mlocName=SharedPreference.getInstance(mContext).getStringValue("locnme","");
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

            }
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
        tv_logotext.setTypeface(tyface);
        tv_activechkin.setTypeface(tyface);
        mCurrentLoc.setTypeface(tyface);
        tv_popular.setTypeface(tyface);



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

        // ArrayAdapter<Domain_Spinner> adapter = new ArrayAdapter<Domain_Spinner>(getActivity(), android.R.layout.simple_spinner_dropdown_item, domainList);

        ArrayAdapter<Domain_Spinner> adapter = new ArrayAdapter<Domain_Spinner>(getActivity(), R.layout.spinner_item, domainList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        mSpinnerDomain.setAdapter(adapter);

        mSpinnerDomain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                searchSrcTextView.setText("");
                //  Spinnertext = parent.getSelectedItem().toString();
                mDomainSpinner = ((Domain_Spinner) mSpinnerDomain.getSelectedItem()).getSector();

                spinnerTxtPass = ((Domain_Spinner) mSpinnerDomain.getSelectedItem()).getDomain();
                Config.logV("Selected-----------" + spinnerTxtPass);
                /////////test code///////////////////////////


                mPopularSearchList.clear();
                if (mDomainSpinner.equalsIgnoreCase("All")) {
                    mPopular_AllSearchList.addAll(mGLobalSearch);
                    mPopularSearchList = mPopular_AllSearchList;
                    funPopulateSearchList(mPopularSearchList);

                } else {
                    mPopular_SubSearchList.clear();
                    for (int i = 0; i < mSectorSearch.size(); i++) {

                        if (mSectorSearch.get(i).getName().toLowerCase().trim().equalsIgnoreCase(mDomainSpinner.toLowerCase().trim())) {

                            for (int k = 0; k < mSectorSearch.get(i).getSectorLabels().size(); k++) {
                                SearchModel search = new SearchModel();
                                Config.logV("Sector Suggestion---1222-333--" + mSectorSearch.get(i).getSectorLabels().get(k).getName());

                                search.setName(mSectorSearch.get(i).getSectorLabels().get(k).getName());
                                search.setQuery(mSectorSearch.get(i).getSectorLabels().get(k).getQuery());
                                search.setSector(mSectorSearch.get(i).getSector());
                                search.setDisplayname(mSectorSearch.get(i).getSectorLabels().get(k).getDisplayname());
                                Config.logV("Display NAme**************" + mSectorSearch.get(i).getSectorLabels().get(k).getDisplayname());
                                mPopular_SubSearchList.add(search);
                            }
                        }

                    }

                    mPopularSearchList = mPopular_SubSearchList;
                    funPopulateSearchList(mPopularSearchList);

                }


                tv_More.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (mPopularSearchList.size() > 0) {

                            is_MoreClick = true;
                            tv_More.setVisibility(View.GONE);
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
                                        dynaText.setTextColor(getResources().getColor(R.color.black));
                                        dynaText.setPadding(15, 10, 15, 10);
                                       // dynaText.setEllipsize(TextUtils.TruncateAt.END);
                                        dynaText.setMaxLines(1);
                                        //dynaText.setMaxEms(8);
                                        dynaText.setGravity(Gravity.CENTER);
                                        dynaText.setWidth(dpToPx(130));

                                        params.setMargins(12, 10, 12, 0);
                                        dynaText.setLayoutParams(params);

                                        //   dynaText.setTag("" + i);
                                        final int finalK = k;
                                        dynaText.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                mPopularSearchtxt = mPopularSearchList.get(finalK).getDisplayname();
                                                Config.logV("Popular Text______________"+mPopularSearchtxt);
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
                });


                ///////////////////////////////////////////////////////////////


                if (mDomainSpinner.equalsIgnoreCase("ALL")) {

                    /**********************************HEADER=SuGGESTED SEARCH*************************************/

                    Config.logV("mGLobalSearch" + mGLobalSearch.size());

                               /* ArrayList<ListCell>*/
                    items = new ArrayList<ListCell>();
                   /* for (int i = 0; i < mGLobalSearch.size(); i++) {

                        items.add(new ListCell(mGLobalSearch.get(i).getName(), "Suggested Search", mGLobalSearch.get(i).getQuery(), mGLobalSearch.get(i).getDisplayname()));
                    }*/


                    /**********************************HEADER+SUBDOMAIN**************************************/
                    //HEADER+SUBDOMAIN
                    mSubDomainSubSearch.clear();
                    for (int i = 0; i < mSubDomain.size(); i++) {

                        Domain_Spinner domain = new Domain_Spinner();
                        // Config.logV("Sector Search-----1111------" + mSubDomain.get(i).getName());
                        domain.setSubDomain(mSubDomain.get(i).getSubDomain());
                        domain.setSector(mSubDomain.get(i).getSector());
                        domain.setSubDomain_DisplayNAme(mSubDomain.get(i).getSubDomain_DisplayNAme());
                        mSubDomainSubSearch.add(domain);

                    }


                    for (int i = 0; i < mSubDomainSubSearch.size(); i++) {
                        // Config.logV("mSectorSubSearch.get(i).getName()" + mSectorSubSearch.get(i).getName());
                        items.add(new ListCell(mSubDomainSubSearch.get(i).getSubDomain(), "Sub Domain", mSubDomainSubSearch.get(i).getSector(), mSubDomainSubSearch.get(i).getSubDomain_DisplayNAme()));
                    }


                    /**********************************HEADER+SPECIALIZATION**************************************/

                    //HEADER+SPECIALIZATION

                    // Config.logV("Specialization***************" + mSpecializationDomain.size());
                    mSpecializationDomainSearch.clear();
                    for (int i = 0; i < mSpecializationDomain.size(); i++) {
                        // Config.logV("mSpecializationDomain).getName()" + mSpecializationDomain.get(i).getSpecializations().size());
                        // Config.logV("Special Domain).getName()" + mSpecializationDomain.get(i).getDomain());

                        for (int j = 0; j < mSpecializationDomain.get(i).getSpecializations().size(); j++) {


                            //  Config.logV("mSpecializationDomain).getName()" + mSpecializationDomain.get(i).getSpecializations().get(j).getName());
                            Domain_Spinner domain = new Domain_Spinner();
                            // Config.logV("Sector Search-----1111------" + mSubDomain.get(i).getName());
                            domain.setName(mSpecializationDomain.get(i).getSpecializations().get(j).getName());
                            domain.setSector(mSpecializationDomain.get(i).getSector());
                            domain.setSpecilicationANme(mSpecializationDomain.get(i).getSpecializations().get(j).getSpecilicationANme());

                            mSpecializationDomainSearch.add(domain);
                        }

                        //items.add(new ListCell(mSectorSubSearch.get(i).getName(), "Suggested Search"));
                    }


                    for (int i = 0; i < mSpecializationDomainSearch.size(); i++) {
                        // Config.logV("mSectorSubSearch.get(i).getName()" + mSectorSubSearch.get(i).getName());
                        items.add(new ListCell(mSpecializationDomainSearch.get(i).getSpecilicationANme(), "Specializations", mSpecializationDomainSearch.get(i).getSector(), mSpecializationDomainSearch.get(i).getName()));
                    }


                           /*     *******************************************************************/

                    listadapter = new SearchListAdpter("search", getActivity(), items, latitude, longitude, getParentFragment(), mSearchView);
                    searchSrcTextView.setAdapter(listadapter);

                    mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {

                            Config.logV("Click-------------------------");


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

                    /**********************************HEADER=SuGGESTED SEARCH*************************************/
                    /*mSectorSubSearch.clear();
                    for (int i = 0; i < mSectorSearch.size(); i++) {

                        if (mSectorSearch.get(i).getName().toLowerCase().trim().equalsIgnoreCase(mDomainSpinner.toLowerCase().trim())) {

                            for (int k = 0; k < mSectorSearch.get(i).getSectorLabels().size(); k++) {
                                SearchModel search = new SearchModel();
                                Config.logV("Sector Suggestion---1222-333--" + mSectorSearch.get(i).getSectorLabels().get(k).getName());

                                search.setName(mSectorSearch.get(i).getSectorLabels().get(k).getName());
                                search.setQuery(mSectorSearch.get(i).getSectorLabels().get(k).getQuery());
                                search.setSector(mSectorSearch.get(i).getSector());
                                search.setDisplayname(mSectorSearch.get(i).getSectorLabels().get(k).getDisplayname());
                                Config.logV("Display NAme**************" + mSectorSearch.get(i).getSectorLabels().get(k).getDisplayname());
                                mSectorSubSearch.add(search);
                            }
                        }

                    }

                    Config.logV("mSectorSubSearch" + mSectorSubSearch.size());

                    //HEADER=SuGGESTED SEARCH
                    items = new ArrayList<ListCell>();
                    for (int i = 0; i < mSectorSubSearch.size(); i++) {
                        items.add(new ListCell(mSectorSubSearch.get(i).getName(), "Suggested Search", mSectorSubSearch.get(i).getQuery(), mSectorSubSearch.get(i).getDisplayname()));

                    }*/

                    /**********************************HEADER+SUBDOMAIN**************************************/

                    //HEADER+SUBDOMAIN
                    mSubDomainSubSearch.clear();
                    Config.logV("mSubDomain.size()------------" + mSubDomain.size());
                    for (int i = 0; i < mSubDomain.size(); i++) {


                        if (mSubDomain.get(i).getSector().equalsIgnoreCase(mDomainSpinner)) {

                            Domain_Spinner domain = new Domain_Spinner();
                            Config.logV("Sector Search----^^^^^-----" + mSubDomain.get(i).getName());
                            domain.setSubDomain(mSubDomain.get(i).getSubDomain());
                            domain.setSector(mSubDomain.get(i).getSector());
                            domain.setSubDomain_DisplayNAme(mSubDomain.get(i).getSubDomain_DisplayNAme());

                            mSubDomainSubSearch.add(domain);

                        }

                    }

                    for (int i = 0; i < mSubDomainSubSearch.size(); i++) {
                        Config.logV("Sector Search-----222-----" + mSubDomainSubSearch.get(i).getSubDomain());
                        // Config.logV("mSectorSubSearch.get(i).getName()" + mSectorSubSearch.get(i).getName());
                        items.add(new ListCell(mSubDomainSubSearch.get(i).getSubDomain(), "Sub Domain", mSubDomainSubSearch.get(i).getSector(), mSubDomainSubSearch.get(i).getSubDomain_DisplayNAme()));
                    }


                    /**********************************HEADER+SPECIALIZATION**************************************/

                    //HEADER+SPECIALIZATION
                    mSpecializationDomainSearch.clear();
                    for (int i = 0; i < mSpecializationDomain.size(); i++) {
                        if (mSpecializationDomain.get(i).getSector().equalsIgnoreCase(mDomainSpinner)) {
                            for (int j = 0; j < mSpecializationDomain.get(i).getSpecializations().size(); j++) {

                                Domain_Spinner domain = new Domain_Spinner();
                                domain.setName(mSpecializationDomain.get(i).getSpecializations().get(j).getName());
                                domain.setSector(mSpecializationDomain.get(i).getSector());
                                domain.setSpecilicationANme(mSpecializationDomain.get(i).getSpecializations().get(j).getSpecilicationANme());

                                mSpecializationDomainSearch.add(domain);
                            }
                        }

                    }


                    for (int i = 0; i < mSpecializationDomainSearch.size(); i++) {
                        items.add(new ListCell(mSpecializationDomainSearch.get(i).getSpecilicationANme(), "Specializations", mSpecializationDomainSearch.get(i).getSector(), mSpecializationDomainSearch.get(i).getName()));
                    }


                    listadapter = new SearchListAdpter("search", getActivity(), items, latitude, longitude, home, mSearchView);
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
        mSearchView = (SearchView) row.findViewById(R.id.search);
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
                Config.logV("SEARCH TXT 99999" + mSearchtxt);
                bundle.putString("searchtxt", mSearchtxt);
                pfFragment.setArguments(bundle);


                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
                // Store the Fragment in stack
                transaction.addToBackStack(null);
                transaction.replace(R.id.mainlayout, pfFragment).commit();


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


                            Config.logV("Response--BODY------SearchLabel-------------------" + response.body().getSearchLabels().size());

                            SearchModel search = null;
                            for (int i = 0; i < response.body().getSearchLabels().size(); i++) {
                                int mGlobalSize = response.body().getSearchLabels().get(0).getGlobalSearchLabels().size();
                                mGLobalSearch.clear();
                                for (int k = 0; k < mGlobalSize; k++) {
                                    search = new SearchModel();
                                    search.setName(response.body().getSearchLabels().get(0).getGlobalSearchLabels().get(k).getName());
                                    search.setDisplayname(response.body().getSearchLabels().get(0).getGlobalSearchLabels().get(k).getDisplayname());

                                    search.setQuery(response.body().getSearchLabels().get(0).getGlobalSearchLabels().get(k).getQuery());
                                    //  Config.logV("Response--BODY------GlobalSearch-------------------" + response.body().getSearchLabels().get(0).getGlobalSearchLabels().get(k).getName());
                                    mGLobalSearch.add(search);
                                    Config.logV("Query*****111********" + response.body().getSearchLabels().get(0).getGlobalSearchLabels().get(k).getQuery());
                                }
                            }

                            Config.logV("Globa lSearch Size-------------" + mGLobalSearch.size());

                            for (int i = 0; i < response.body().getSearchLabels().size(); i++) {
                                int mSectorSize = response.body().getSearchLabels().get(1).getSectorLevelLabels().size();
                                mSectorSearch.clear();
                                for (int k = 0; k < mSectorSize; k++) {
                                    ArrayList<SearchModel> getSectorLevel = response.body().getSearchLabels().get(1).getSectorLevelLabels();
                                    String sector = response.body().getSearchLabels().get(1).getSectorLevelLabels().get(k).getName();
                                    // Config.logV("Sector-------------" + sector);
                                    search = new SearchModel();
                                    search.setName(sector);
                                    search.setSectorLabels(getSectorLevel.get(k).getSectorLabels());
                                    search.setSector(getSectorLevel.get(k).getName());

                                    //  Config.logV("Sector Suggestion---1222---" + getSectorLevel.get(k).getSector());
                                    //  Config.logV("Sector Suggestion---122222444---" + getSectorLevel.get(k).getName());
                                    mSectorSearch.add(search);


                                }
                            }

                            Config.logV("Sector Search Size-------------" + mSectorSearch.size());

                            Config.logV("GLobal Search Size-------------" + mGLobalSearch.size());


                            Lhome_mainlayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (is_MoreClick) {
                                        is_MoreClick = false;
                                        LpopularSearch.setVisibility(View.VISIBLE);
                                        LinearPopularSearch.setVisibility(View.VISIBLE);
                                        LinearMorePopularSearch.setVisibility(View.GONE);
                                        tv_More.setVisibility(View.VISIBLE);
                                    }
                                }
                            });


                            mainlayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (is_MoreClick) {
                                        is_MoreClick = false;
                                        LpopularSearch.setVisibility(View.VISIBLE);
                                        LinearPopularSearch.setVisibility(View.VISIBLE);
                                        LinearMorePopularSearch.setVisibility(View.GONE);
                                        tv_More.setVisibility(View.VISIBLE);
                                    }
                                }
                            });


                            APiGetDomain();
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
                                mSubDomain.clear();
                                mSpecializationDomain.clear();
                                for (int i = 0; i < response.body().size(); i++) {

                                    domainList.add(new Domain_Spinner(response.body().get(i).getDomain(), response.body().get(i).getSector()));

                                    for (int k = 0; k < response.body().get(i).getSubDomains().size(); k++) {
                                        Domain_Spinner domain = new Domain_Spinner();
                                        domain.setDomain(response.body().get(i).getDomain());
                                        domain.setSubDomain(response.body().get(i).getSubDomains().get(k).getSubDomain());
                                        domain.setSector(response.body().get(i).getSector());
                                        domain.setSubDomain_DisplayNAme(response.body().get(i).getSubDomains().get(k).getName());
                                        mSubDomain.add(domain);


                                        Domain_Spinner spec = new Domain_Spinner();

                                        spec.setDomain(response.body().get(i).getDomain());
                                        spec.setSpecializations(response.body().get(i).getSubDomains().get(k).getSpecializations());

                                        spec.setSector(response.body().get(i).getSector());
                                        mSpecializationDomain.add(spec);
                                    }

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

    private void ApiActiveCheckIn() {


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

        double distance = 5;/*;DISTANCE_AREA: 5, // in Km*/

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

        Config.logV("Popular Text_______$$$$_______"+mPopularSearchtxt);
        bundle.putString("searchtxt", mPopularSearchtxt);
        pfFragment.setArguments(bundle);


        FragmentTransaction transaction = this.getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
        // Store the Fragment in stack
        transaction.addToBackStack(null);
        transaction.replace(R.id.mainlayout, pfFragment).commit();
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


            }else{
                setUpGClient();
            }

        }


    }


    @Override
    public void onConnected(Bundle bundle) {
        checkPermissions();
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
        Config.logV("Update Location SetUpFConnected---------------------------");
    }

    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;
        Config.logV("Update Location Changed Connected---------------------------" + location);
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
                SearchListFragment.UpdateLocationSearch(String.valueOf(latitude),String.valueOf(longitude),addresses.get(0).getLocality());
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
                int permissionLocation = ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000);        // 10 seconds, in milliseconds
                    locationRequest.setFastestInterval(1 * 1000); // 1 second, in milliseconds

                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest, this);
                    DefaultLocation();

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
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(getActivity(),
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
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
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getMyLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        getActivity().finish();
                        DefaultLocation();
                        break;
                }
                break;
        }
    }

    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(getActivity(),
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        } else {
            getMyLocation();
        }

    }

    public void DefaultLocation(){
        if(mCurrentLoc.getText().toString().equalsIgnoreCase("Locating...")){
            latitude = 12.971599;
            longitude = 77.594563;
            try {
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                mCurrentLoc.setVisibility(View.VISIBLE);
                mCurrentLoc.setText(addresses.get(0).getLocality());
                //   Config.logV("Latitude-----11111--------"+addresses.get(0).getAddressLine(0));
            } catch (Exception e) {

            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
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


        String querycreate = "(phrase " + "'" + query + "')";
        ;
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
    }



    public static boolean UpdateLocation(Double mlatitude, Double mlongitude, String locNme) {
        Config.logV("UpdateLocation 3333333333----" + mlatitude + " " + mlongitude+""+locNme);
        try {
            latitude = mlatitude;
            longitude = mlongitude;
            mlocName = locNme;


            SharedPreference.getInstance(mContext).setValue("lat",latitude);
            SharedPreference.getInstance(mContext).setValue("longitu",longitude);
            SharedPreference.getInstance(mContext).setValue("locnme",mlocName);


            SharedPreference.getInstance(mContext).setValue("locnme",mlocName);
            Config.logV("UpdateLocation 4444----" + mlocName);
            mCurrentLoc.setVisibility(View.VISIBLE);
            mCurrentLoc.setText(mlocName);


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
    public void onMethodActiveBillIconCallback(String value, String provider) {
        Intent iBill = new Intent(mContext, BillActivity.class);
        iBill.putExtra("ynwUUID", value);
        iBill.putExtra("provider", provider);
        startActivity(iBill);

    }




}
