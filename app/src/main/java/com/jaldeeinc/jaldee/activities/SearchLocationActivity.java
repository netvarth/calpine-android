package com.jaldeeinc.jaldee.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaldeeinc.jaldee.Fragment.DashboardFragment;
import com.jaldeeinc.jaldee.Fragment.SearchListFragment;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.LocationSearchAdapter;
import com.jaldeeinc.jaldee.callback.LocationSearchCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.response.LocationResponse;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 3/8/18.
 */

public class SearchLocationActivity extends AppCompatActivity implements LocationSearchCallback {


    RecyclerView mrRecylce_searchloc;
    SearchView mSearchView;
    SearchView.SearchAutoComplete searchSrcTextView;
    Activity mActivity;
    Context mContext;
    LocationSearchAdapter mSearchLocAdapter;
    ArrayList<LocationResponse> items = new ArrayList<>();
    LocationSearchCallback mCallback;
    TextView tv_currentloc;
    String from;
    String sforceupdate = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);


        ImageView iBackPress = findViewById(R.id.backpress);
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                finish();
            }
        });
        TextView tv_title = findViewById(R.id.toolbartitle);


        tv_currentloc = findViewById(R.id.tv_currentloc);
        tv_title.setText("Search Location");
        Typeface tyface1 = Typeface.createFromAsset(this.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface1);


        tv_currentloc.setTypeface(tyface1);
        mActivity = this;
        mContext = this;
        mCallback = (LocationSearchCallback) this;


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            from = extras.getString("from");
            sforceupdate = extras.getString("forceupdate", "");

            if (sforceupdate != null) {
                if (sforceupdate.equalsIgnoreCase("true")) {

                    showForceUpdateDialog();
                }
            }

        }
        tv_currentloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreference.getInstance(v.getContext()).setValue("current_loc", "yes");
                finish();
            }
        });
        mrRecylce_searchloc = findViewById(R.id.recylce_searchloc);
        //SEARCH
        mSearchView = findViewById(R.id.search_loc);
        searchSrcTextView = findViewById(android.support.v7.appcompat.R.id.search_src_text);
        SearchManager searchMng = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchMng.getSearchableInfo(getComponentName()));

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        mrRecylce_searchloc.setLayoutManager(mLayoutManager);
        mSearchLocAdapter = new LocationSearchAdapter(this, items, mCallback);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mSearchLocAdapter.getFilter().filter(query);
                if (query.length() > 1) {
                    ApiLocationSearch(query);

                }

                return false;
            }
        });

    }

    private void ApiLocationSearch(String criteria) {


        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);

        Call<ArrayList<LocationResponse>> call = apiService.getSearchLocation(criteria);
        call.enqueue(new Callback<ArrayList<LocationResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<LocationResponse>> call, Response<ArrayList<LocationResponse>> response) {

                try {
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        items = response.body();
                        mSearchLocAdapter = new LocationSearchAdapter(mContext, items, mCallback);
                        mrRecylce_searchloc.setAdapter(mSearchLocAdapter);
                        mSearchLocAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<LocationResponse>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
            }
        });
    }

    @Override
    public void onMethodCallback(String value, Double lat, Double longtitude, String locName) {
        Config.logV("UpdateLocation 555----" + lat);
        if (from.equalsIgnoreCase("dashboard")) {
            if (DashboardFragment.UpdateLocation(lat, longtitude, locName)) {
                SharedPreference.getInstance(this).setValue("current_loc", "no");
                finish();
            }
        } else {
            if (DashboardFragment.UpdateLocation(lat, longtitude, locName)) {
                SharedPreference.getInstance(this).setValue("current_loc", "no");

            }

            if (SearchListFragment.UpdateLocationSearch(String.valueOf(lat), String.valueOf(longtitude), locName)) {
                finish();
            }
        }
    }
    public void showForceUpdateDialog() {

        android.app.AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Jaldee update required ");
        alertDialog.setMessage(" This version of Jaldee is no longer supported. Please update to the latest version.");
        alertDialog.setPositiveButton("UPDATE NOW", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                final String appPackageName = mContext.getPackageName();
                try {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        alertDialog.show();
    }
}
