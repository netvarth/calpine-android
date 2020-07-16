package com.jaldeeinc.jaldee.activities;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.widget.Toast;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.RecyclerAdapterLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class newSearchLocationActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private List<Object> viewItems = new ArrayList<>();

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);

        Toast.makeText(this, "Entered location class", Toast.LENGTH_SHORT).show();

        mRecyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        mRecyclerView.setHasFixedSize(true);


        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new RecyclerAdapterLocation(this, viewItems);
        mRecyclerView.setAdapter(mAdapter);
        addItemsFromJSON();


    }

    private void addItemsFromJSON() {

        try {

            String jsonDataString = readJSONDataFromFile();
            JSONArray jsonArray = new JSONArray(jsonDataString);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject itemObj = jsonArray.getJSONObject(i);

                String name = itemObj.getString("name");

                Locations locations = new Locations(name);
                viewItems.add(locations);


            }


        } catch (JSONException e) {
            Log.i("Error", "Error");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String readJSONDataFromFile() throws IOException {

        InputStream inputStream = null;
        StringBuilder builder = new StringBuilder();

        try {
            String jsonString = null;
            inputStream = getResources().openRawResource(R.raw.locationmin);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));

            while((jsonString = bufferedReader.readLine()) != null){
                builder.append(jsonString);
            }

        } finally {
if(inputStream != null){
    inputStream.close();
}
        }

        return new String(builder);

    }
}
