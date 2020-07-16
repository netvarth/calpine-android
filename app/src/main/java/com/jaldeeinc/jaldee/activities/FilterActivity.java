package com.jaldeeinc.jaldee.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaldeeinc.jaldee.Fragment.SearchListFragment;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.FilterAdapter;
import com.jaldeeinc.jaldee.callback.FilterAdapterCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.model.LanLong;
import com.jaldeeinc.jaldee.response.RefinedFilters;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 7/6/19.
 */

public class FilterActivity extends AppCompatActivity implements FilterAdapterCallback {

    Activity mActivitity;
    RecyclerView recycle_filter;
    Context mContext;
    Button btn_apply;
    FilterAdapterCallback mCallback;
    String latitude, longitude, locName, spinnerTxtPass, mDomainSpinner, from;
    String passformula = "",query="";

    static SearchListFragment mFragmentInstance;

    public static void setFragmentInstance(SearchListFragment fragmentInstance) {
        mFragmentInstance = fragmentInstance;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_filter);
        mContext = this;
        TextView tv_title = (TextView) findViewById(R.id.toolbartitle);
        Typeface tyface = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface);
        mActivitity = this;
        mCallback = (FilterAdapterCallback) this;

        btn_apply = (Button) findViewById(R.id.btn_apply);

        tv_title.setText("Filter");
        if (Config.isOnline(this)) {
            ApiFilters();
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            latitude = extras.getString("lat", "");
            longitude = extras.getString("longt", "");
            locName = extras.getString("locName", "");
            spinnerTxtPass = extras.getString("spinnervalue", "");
            mDomainSpinner = extras.getString("sector", "");
            from = extras.getString("from", "");
            query= extras.getString("query", "");

        }

        ImageView iBackPress = (ImageView) findViewById(R.id.backpress);
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // perform whatever you want on back arrow click
                Config.logV("BackPress-----------");
                finish();
            }
        });

        recycle_filter = (RecyclerView) findViewById(R.id.recycle_filter);
        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (from != null) {
                    if (from.equalsIgnoreCase("searchlist")) {

                        String queryFormula = "";
                        int count = 0;
                        boolean match = false;
                        for (int i = 0; i < keyFormula.size(); i++) {


                            for (int j = 0; j < sFormula.size(); j++) {


                                if (sFormula.get(j).toString().contains(keyFormula.get(i).toString())) {


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


                        Config.logV("SortString FInal @@@@@@@@@@" + queryFormula);

                       /*

                        SearchListFragment frg = new SearchListFragment();


                        LanLong Lanlong = getLocationNearBy(Double.valueOf(latitude), Double.valueOf(longitude));
                        double upperLeftLat = Lanlong.getUpperLeftLat();
                        double upperLeftLon = Lanlong.getUpperLeftLon();
                        double lowerRightLat = Lanlong.getLowerRightLat();
                        double lowerRightLon = Lanlong.getLowerRightLon();
                        String locationRange = "['" + lowerRightLat + "," + lowerRightLon + "','" + upperLeftLat + "," + upperLeftLon + "']";

                        String querycreate = "";
                        if (!mDomainSpinner.equalsIgnoreCase("All"))
                            querycreate = "sector :'" + mDomainSpinner + "'";


                        String pass = "haversin(" + latitude + "," + longitude + ", location1.latitude, location1.longitude)";

                        Bundle bundle = new Bundle();


                        //VALID QUERY PASS
                        bundle.putString("query", "(and location1:" + locationRange + querycreate + ")");
                        bundle.putString("url", pass);


                        bundle.putString("locName", locName);
                        bundle.putString("latitude", String.valueOf(latitude));
                        bundle.putString("longitude", String.valueOf(longitude));
                        bundle.putString("spinnervalue", spinnerTxtPass);
                        bundle.putString("passformula", queryFormula);
                        bundle.putString("filter", "filter");
                        bundle.putStringArrayList("passFormulaArray", sFormula);

                        Config.logV("PassFormula @@@@@@@@@@@@@@" + queryFormula);

                        Config.logV("PassFormula @$$$$$$@@@@@@@@@@@@@@" + pass);

                        frg.setArguments(bundle);
                         FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.detach(frg);
                        ft.attach(frg);
                        ft.commit();*/

                       finish();
                        mFragmentInstance.MoreItemClick(queryFormula,query);
                        mFragmentInstance.SetPassFormula(sFormula);


                    } else {
                        callSearchList();
                    }
                } else {

                    callSearchList();


                }


            }
        });

    }

    public void callSearchList() {
        String queryFormula = "";
        int count = 0;
        boolean match = false;
        for (int i = 0; i < keyFormula.size(); i++) {


            for (int j = 0; j < sFormula.size(); j++) {


                if (sFormula.get(j).toString().contains(keyFormula.get(i).toString())) {


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


        Config.logV("SortString FInal @@@@@@@@@@" + queryFormula);


        QuerySubmitCLick(queryFormula, sFormula);
    }


    ArrayList<RefinedFilters> commonFilterList = new ArrayList<>();

    ArrayList<RefinedFilters> commonFilterSortList = new ArrayList<>();

    private void ApiFilters() {


        ApiInterface apiService =
                ApiClient.getClient(mActivitity).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(mContext, this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        Call<RefinedFilters> call = apiService.getFilters();


        call.enqueue(new Callback<RefinedFilters>() {
            @Override
            public void onResponse(Call<RefinedFilters> call, Response<RefinedFilters> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(mActivitity, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------Filters-------------------" + response.code());
                    if (response.code() == 200) {
                        Config.logV("Response----------------");

                        commonFilterList.clear();
                        commonFilterList = response.body().getCommonFilters();
                        Config.logV("Common Filters----------------" + commonFilterList.size());

                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivitity);
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
                        FilterAdapter mFavAdapter = new FilterAdapter(commonFilterSortList, mContext, mActivitity, mCallback);
                        recycle_filter.setAdapter(mFavAdapter);
                        mFavAdapter.notifyDataSetChanged();


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
                    Config.closeDialog(mActivitity, mDialog);

            }
        });

    }

    ArrayList<String> sFormula = new ArrayList<>();
    ArrayList<String> keyFormula = new ArrayList<>();

    @Override
    public void onMethodFilterCallback(ArrayList<String> value, ArrayList<String> key) {


        sFormula.clear();
        sFormula.addAll(value);

        keyFormula.clear();
        keyFormula.addAll(key);

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

    public void QuerySubmitCLick(String passformula, ArrayList<String> sFormula) {

       /* String passFormula = "";
        for (String s : passformula) {
            passFormula += " " + s + "";
        }*/

        Config.logV("passFormaulaFF @@@@@@@@@@@@@@@@" + passformula);

        LanLong Lanlong = getLocationNearBy(Double.valueOf(latitude), Double.valueOf(longitude));
        double upperLeftLat = Lanlong.getUpperLeftLat();
        double upperLeftLon = Lanlong.getUpperLeftLon();
        double lowerRightLat = Lanlong.getLowerRightLat();
        double lowerRightLon = Lanlong.getLowerRightLon();
        String locationRange = "['" + lowerRightLat + "," + lowerRightLon + "','" + upperLeftLat + "," + upperLeftLon + "']";

        String querycreate = "";
        if (!mDomainSpinner.equalsIgnoreCase("All"))
            querycreate = "sector :'" + mDomainSpinner + "'";


        String pass = "haversin(" + latitude + "," + longitude + ", location1.latitude, location1.longitude)";

        Bundle bundle = new Bundle();


        //VALID QUERY PASS
        bundle.putString("query", "(and location1:" + locationRange + querycreate + ")");
        bundle.putString("url", pass);
        SearchListFragment pfFragment = new SearchListFragment();

        bundle.putString("locName", locName);
        bundle.putString("latitude", String.valueOf(latitude));
        bundle.putString("longitude", String.valueOf(longitude));
        bundle.putString("spinnervalue", spinnerTxtPass);
        bundle.putString("passformula", passformula);
        bundle.putString("filter", "filter");
        bundle.putStringArrayList("passFormulaArray", sFormula);

        Config.logV("PassFormula @@@@@@@@@@@@@@" + passformula);
       /* if(!query.equalsIgnoreCase("")) {
            bundle.putString("searchtxt", mSearchtxt);
        }else{
            bundle.putString("searchtxt", "");
        }*/

        pfFragment.setArguments(bundle);


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);

        // Store the Fragment in stack
        // transaction.addToBackStack(null);
        transaction.replace(R.id.mainlayout, pfFragment).commit();
        //searchSrcTextView.clearFocus();
        //mSearchView.clearFocus();
    }


}
