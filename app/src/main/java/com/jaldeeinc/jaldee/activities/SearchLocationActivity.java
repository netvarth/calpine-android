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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    LocationResponse locationName = new LocationResponse();
    ArrayList<LocationResponse> arrayList = new ArrayList<>();
    ArrayList<LocationResponse> newArrayList = new ArrayList<>();
//    ArrayList<JSONObject> newArrayList = new ArrayList<>();



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
        mSearchLocAdapter = new LocationSearchAdapter(this, arrayList, mCallback);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mSearchLocAdapter.getFilter().filter(query);
                if(query.length()>0){
                    getJson(query);
                }
                return false;
            }
        });

    }

    public void getJson(String query) {
        Log.i("locoloco", "getJson");
        String json;
        String jsonMetro;
        arrayList.clear();


        try {
            InputStream is = getResources().openRawResource(R.raw.locationmin);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            JSONObject jsonObj = new JSONObject(json);



            JSONArray ja_states = jsonObj.getJSONArray("states");
            for (int i = 0; i < ja_states.length(); i++) {
                LocationResponse locationResponse = new LocationResponse();
                JSONObject jsonObj1 = ja_states.getJSONObject(i);
                locationResponse.setName(jsonObj1.getString("name"));
                locationResponse.setLatitude(jsonObj1.getDouble("latitude"));
                locationResponse.setLongitude(jsonObj1.getDouble("longitude"));
                locationResponse.setTyp("state");
                locationResponse.setRank("4");
                arrayList.add(locationResponse);

                JSONArray citys = jsonObj1.getJSONArray("cities");
                for (int j = 0; j < citys.length(); j++) {
                    locationResponse = new LocationResponse();
                    JSONObject json1 = citys.getJSONObject(j);
                    locationResponse.setName(json1.getString("name") + ", " + jsonObj1.getString("name"));
                    locationResponse.setLatitude(json1.getDouble("latitude"));
                    locationResponse.setLongitude(json1.getDouble("longitude"));
                    locationResponse.setTyp("city");
                    locationResponse.setRank("3");
                    arrayList.add(locationResponse);


                    JSONArray locations = json1.getJSONArray("locations");
                    for (int k = 0; k < locations.length(); k++) {
                        locationResponse = new LocationResponse();
                        JSONObject json12 = locations.getJSONObject(k);
                        locationResponse.setName(json12.getString("name") + ", " + json1.getString("name") + ", " + jsonObj1.getString("name"));
                        locationResponse.setLatitude(json12.getDouble("latitude"));
                        locationResponse.setLongitude(json12.getDouble("longitude"));
                        locationResponse.setTyp("area");
                        locationResponse.setRank("5");
                        arrayList.add(locationResponse);
                    }

                }
            }


            //
            InputStream isMetro = getResources().openRawResource(R.raw.metros);
            int sizeMetro = isMetro.available();
            byte[] bufferMetro = new byte[sizeMetro];
            isMetro.read(bufferMetro);
            isMetro.close();
            jsonMetro = new String(bufferMetro, "UTF-8");

            JSONObject jsonObjMetro = new JSONObject(jsonMetro);


            JSONArray ja_statesMetro = jsonObjMetro.getJSONArray("metros");
            for (int i = 0; i < ja_statesMetro.length(); i++) {
                LocationResponse locationResponseMetro = new LocationResponse();
                JSONObject jsonObj1Metro = ja_statesMetro.getJSONObject(i);
                locationResponseMetro.setName(jsonObj1Metro.getString("name") + " " + "( Metro )");
                locationResponseMetro.setLatitude(jsonObj1Metro.getDouble("latitude"));
                locationResponseMetro.setLongitude(jsonObj1Metro.getDouble("longitude"));
                locationResponseMetro.setTyp("metro");
                locationResponseMetro.setRank("1");
                arrayList.add(locationResponseMetro);
            }

            JSONArray ja_statesCapital = jsonObjMetro.getJSONArray("capitals");
            for (int i = 0; i < ja_statesCapital.length(); i++) {
                LocationResponse locationResponseMetro = new LocationResponse();
                JSONObject jsonObj1Metro = ja_statesCapital.getJSONObject(i);
                locationResponseMetro.setName(jsonObj1Metro.getString("name") + " " + "( Capital )");
                locationResponseMetro.setLatitude(jsonObj1Metro.getDouble("latitude"));
                locationResponseMetro.setLatitude(jsonObj1Metro.getDouble("longitude"));
                locationResponseMetro.setTyp("capital");
                locationResponseMetro.setRank("2");
                arrayList.add(locationResponseMetro);
            }




            newArrayList.clear();
            for(int i = 0; i<arrayList.size(); i ++){
                if(arrayList.get(i).getName().toLowerCase().startsWith(query.toLowerCase())){
                    newArrayList.add(arrayList.get(i));
                }
            }

            Collections.sort(newArrayList, new Comparator<LocationResponse>() {
                @Override
                public int compare(LocationResponse lhs, LocationResponse rhs) {
                    return lhs.getRank().compareTo(rhs.getRank());
                }
            });



            Log.i("locoLastStateDetail", newArrayList.toString());
            mSearchLocAdapter = new LocationSearchAdapter(mContext, newArrayList, mCallback);
            mrRecylce_searchloc.setAdapter(mSearchLocAdapter);
            mSearchLocAdapter.notifyDataSetChanged();

            Log.i("locoLastStateDetail", arrayList.toString());
            Log.i("locoLastStateDetail", newArrayList.toString());



        } catch (IOException | JSONException e) {
            Log.i("locoloco", "exception");
            Log.i("locoloco", e.toString());
            e.printStackTrace();
        }
        {

        }
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
                        Log.i("LocationName",new Gson().toJson(response.body()));
                        mSearchLocAdapter = new LocationSearchAdapter(mContext, (ArrayList<LocationResponse>) arrayList, mCallback);
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
