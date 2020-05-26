package com.jaldeeinc.jaldee.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jaldeeinc.jaldee.R;

import java.util.ArrayList;

/**
 * Created by sharmila on 8/8/18.
 */

public class AppointmentDate extends AppCompatActivity {
     ArrayList timeslot = new ArrayList();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appoinmentdate);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            timeslot = extras.getStringArrayList("timeslots");
        }
        Log.i("cvbbvcbvc",timeslot.toString());



    }
}
