package com.jaldeeinc.jaldee.activities;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.LocationSearchAdapterNew;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class SampleLocation extends AppCompatActivity {

    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<String> city_names = new ArrayList<>();
    JSONObject locationName = new JSONObject();
    SearchView mSearchView;
    LocationSearchAdapterNew mSearchLocAdapter;
    RecyclerView mrRecylce_searchloc;
    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);
        Log.i("locoloco", "oncreate");
        mSearchView = findViewById(R.id.search_loc);
        SearchManager searchMng = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchMng.getSearchableInfo(getComponentName()));
        TextView tv_title = findViewById(R.id.toolbartitle);
        mrRecylce_searchloc = findViewById(R.id.recylce_searchloc);
        tv_title.setText("Search Location");
        Typeface tyface1 = Typeface.createFromAsset(this.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface1);
        mContext = this;

        ImageView iBackPress = findViewById(R.id.backpress);
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                finish();
            }
        });
        getJson();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
//                mSearchLocAdapter.getFilter().filter(query);
                if (query.length() > 1) {
                    Toast.makeText(mContext, query, Toast.LENGTH_SHORT).show();
//                    ApiLocationSearch(query);
                }
                return false;
            }

            private void ApiLocationSearch(String query) {

                mSearchLocAdapter = new LocationSearchAdapterNew(mContext, arrayList);
                mrRecylce_searchloc.setAdapter(mSearchLocAdapter);
                mSearchLocAdapter.notifyDataSetChanged();

            }
        });


    }

    public void getJson() {
        Log.i("locoloco", "getJson");
        String json;

        try {

            Log.i("locoloco", "try");
            InputStream is = getResources().openRawResource(R.raw.locationmin);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            Log.i("locoloco", "tryover");
            json = new String(buffer, "UTF-8");
            Log.i("jsonValue", json);


            JSONObject jsonObj = new JSONObject(json);

            JSONArray ja_states = jsonObj.getJSONArray("states");
            for (int i = 0; i < ja_states.length(); i++) {
                JSONObject jsonObj1 = ja_states.getJSONObject(i);
                locationName.put("name", jsonObj1.getString("name"));
                locationName.put("latitude", jsonObj1.getString("latitude"));
                locationName.put("longitude", jsonObj1.getString("longitude"));
                arrayList.add(locationName.toString());

                JSONArray citys = jsonObj1.getJSONArray("cities");
                for (int j = 0; j < citys.length(); j++) {
                    JSONObject json1 = citys.getJSONObject(j);
                    locationName.put("name", json1.getString("name"));
                    locationName.put("latitude", json1.getString("latitude"));
                    locationName.put("longitude", json1.getString("longitude"));
                    arrayList.add(locationName.toString());


                    JSONArray locations = json1.getJSONArray("locations");
                    for (int k = 0; k < locations.length(); k++) {
                        JSONObject json12 = locations.getJSONObject(k);
                        locationName.put("name", json12.getString("name"));
                        locationName.put("latitude", json12.getString("latitude"));
                        locationName.put("longitude", json12.getString("longitude"));
                        arrayList.add(locationName.toString());
                    }

                }
            }

            Log.i("locoLastStateDetail", arrayList.toString());

        } catch (IOException | JSONException e) {
            Log.i("locoloco", "exception");
            Log.i("locoloco", e.toString());
            e.printStackTrace();
        }
        {

        }
    }


}
