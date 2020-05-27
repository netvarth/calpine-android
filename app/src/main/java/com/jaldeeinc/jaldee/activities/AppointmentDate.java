package com.jaldeeinc.jaldee.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.SpecialisationAdapter;
import com.jaldeeinc.jaldee.adapter.TimeSlotsAdapter;
import com.jaldeeinc.jaldee.adapter.VirtualFieldAdapter;

import java.util.ArrayList;

/**
 * Created by sharmila on 8/8/18.
 */

public class AppointmentDate<mAdapter> extends AppCompatActivity {
     ArrayList timeslot = new ArrayList();
     RecyclerView recycle_timeslots;
     TimeSlotsAdapter sAdapter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appoinmentdate);
        recycle_timeslots = (RecyclerView) findViewById(R.id.recycler_time_slot);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            timeslot = extras.getStringArrayList("timeslots");
        }
        Log.i("cvbbvcbvc",timeslot.toString());

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        recycle_timeslots.setLayoutManager(mLayoutManager);
        sAdapter = new TimeSlotsAdapter(timeslot);
        recycle_timeslots.setAdapter(sAdapter);
        sAdapter.notifyDataSetChanged();

    }

}
