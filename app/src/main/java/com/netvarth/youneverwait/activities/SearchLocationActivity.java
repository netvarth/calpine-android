package com.netvarth.youneverwait.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.netvarth.youneverwait.Fragment.DashboardFragment;
import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.adapter.LocationSearchAdapter;
import com.netvarth.youneverwait.callback.AdapterCallback;
import com.netvarth.youneverwait.callback.LocationSearchCallback;
import com.netvarth.youneverwait.common.Config;
import com.netvarth.youneverwait.connection.ApiClient;
import com.netvarth.youneverwait.connection.ApiInterface;
import com.netvarth.youneverwait.response.LocationResponse;
import com.netvarth.youneverwait.utils.SharedPreference;

import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 3/8/18.
 */

public class SearchLocationActivity extends AppCompatActivity  implements LocationSearchCallback {

    Toolbar toolbar;
    RecyclerView mrRecylce_searchloc;
    SearchView mSearchView;
    SearchView.SearchAutoComplete searchSrcTextView;
    Activity mActivity;
    Context mContext;
    LocationSearchAdapter mSearchLocAdapter;
    ArrayList<LocationResponse> items=new ArrayList<>();
    LocationSearchCallback mCallback;
    TextView tv_currentloc;
    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView tv_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        tv_currentloc=(TextView)findViewById(R.id.tv_currentloc);
        tv_title.setText("Search Location");
        Typeface tyface1 = Typeface.createFromAsset(this.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface1);
        tv_currentloc.setTypeface(tyface1);
        mActivity=this;
        mContext=this;
        mCallback = (LocationSearchCallback) this;
        Typeface tyface = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Regular.otf");
        tv_title.setTypeface(tyface);

        tv_currentloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreference.getInstance(v.getContext()).setValue("current_loc","yes");
                DashboardFragment pfFragment = new DashboardFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                //transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                // Store the Fragment in stack
                transaction.addToBackStack(null);
                transaction.replace(R.id.mainlayout, pfFragment).commit();
                finish();
            }
        });

        mrRecylce_searchloc = (RecyclerView) findViewById(R.id.recylce_searchloc);
        //SEARCH
        mSearchView = (SearchView) findViewById(R.id.search_loc);
        searchSrcTextView = (SearchView.SearchAutoComplete) findViewById(android.support.v7.appcompat.R.id.search_src_text);
        SearchManager searchMng = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchMng.getSearchableInfo(getComponentName()));

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        mrRecylce_searchloc.setLayoutManager(mLayoutManager);
        mSearchLocAdapter = new LocationSearchAdapter(this, items,mCallback);
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
                        items=response.body();
                        mSearchLocAdapter = new LocationSearchAdapter(mContext, items,mCallback);
                        mrRecylce_searchloc.setAdapter(mSearchLocAdapter);
                        mSearchLocAdapter.notifyDataSetChanged();


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<LocationResponse>>call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());


            }
        });


    }

    @Override
    public void onMethodCallback(String value, Double lat, Double longtitude) {
        Config.logV("UpdateLocation 555----"+lat);
       if(  DashboardFragment.UpdateLocation(lat,longtitude)){
           SharedPreference.getInstance(this).setValue("current_loc","no");
           finish();
       }
    }
}
