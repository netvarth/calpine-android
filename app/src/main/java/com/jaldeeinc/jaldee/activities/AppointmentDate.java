package com.jaldeeinc.jaldee.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.SpecialisationAdapter;
import com.jaldeeinc.jaldee.adapter.TimeSlotsAdapter;
import com.jaldeeinc.jaldee.adapter.VirtualFieldAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by sharmila on 8/8/18.
 */

public class AppointmentDate<mAdapter> extends AppCompatActivity {
     ArrayList timeslot = new ArrayList();
     RecyclerView recycle_timeslots;
     TimeSlotsAdapter sAdapter;
     TextView tv_date_slot;
     CalendarView cv;
     Button btn_confirm;
     String selectedDate;
     TextView tv_title;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appoinmentdate);
        recycle_timeslots = (RecyclerView) findViewById(R.id.recycler_time_slot);
//        tv_date_slot = (TextView) findViewById(R.id.selected_date_time);
        cv = (CalendarView) findViewById(R.id.calendarView);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        tv_title = findViewById(R.id.toolbartitle);
        tv_title.setText("Time Slots");
        ImageView iBackPress = findViewById(R.id.backpress);
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                finish();
            }
        });

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



        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView arg0, int year, int month ,
                                            int date) {
                month= month + 1;
                Toast.makeText(getApplicationContext(),date+ "/"+ month  +"/"+year,Toast.LENGTH_LONG).show();
                selectedDate = date+ "/"+ month  +"/"+year;
                Appointment.timeslotdates(selectedDate);
            }
        });



    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent iAppointment = new Intent(this,Appointment.class);
        iAppointment.putExtra("selectedDate",selectedDate);
        startActivity(iAppointment);
    }
}
