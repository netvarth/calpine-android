package com.jaldeeinc.jaldee.activities;

import android.app.Dialog;
import android.content.Context;
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

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.SpecialisationAdapter;
import com.jaldeeinc.jaldee.adapter.TimeSlotsAdapter;
import com.jaldeeinc.jaldee.adapter.VirtualFieldAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.response.AppointmentSchedule;
import com.jaldeeinc.jaldee.response.ScheduleId;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jaldeeinc.jaldee.activities.Appointment.earliestAvailable;
import static com.jaldeeinc.jaldee.common.MyApplication.getContext;

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
     static int serviceId;
     static int mSpinnertext;
     static String accountId;
     static String schdId;
    ArrayList<AppointmentSchedule> schedResponse = new ArrayList<>();
    ArrayList<String> timeslots= new ArrayList<>();
    Date last_date =new Date();



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

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AppointmentDate.this, "Selected Date and Time Slot Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            timeslot = extras.getStringArrayList("timeslots");
            serviceId = extras.getInt("serviceId");
            mSpinnertext = extras.getInt("mSpinnertext");
            accountId = extras.getString("accountId");
            schdId = extras.getString("id");
        }


        if(!timeslot.isEmpty()){
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        recycle_timeslots.setLayoutManager(mLayoutManager);
        sAdapter = new TimeSlotsAdapter(timeslot);
        recycle_timeslots.setAdapter(sAdapter);
        sAdapter.notifyDataSetChanged();
        } else{
            Appointment.timeslotdate("Time Slots not available");
            Toast.makeText(this, "Time Slots not available", Toast.LENGTH_SHORT).show();

        }



        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView arg0, int year, int month ,
                                            int date) {
                month= month + 1;
                Toast.makeText(getApplicationContext(),date+ "/"+ month  +"/"+year,Toast.LENGTH_LONG).show();
                selectedDate = year+ "-"+ month  +"-"+date;
                SimpleDateFormat sdf = new SimpleDateFormat(
                        "yyyy-MM-dd");

                try {
                     last_date = new SimpleDateFormat("yyyy-MM-dd").parse(selectedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
              //  ApiSchedule(String.valueOf(serviceId), String.valueOf(mSpinnertext), sdf.format(last_date), accountId);
                ApiScheduleId(schdId, sdf.format(last_date), accountId);
                Appointment.timeslotdates(sdf.format(last_date));
            }
        });

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().getActualMinimum(Calendar.HOUR_OF_DAY));
        long date = calendar.getTime().getTime();
        cv.setMinDate(date);


    }

//    @Override
//    public void onBackPressed() {
//        Log.d("CDA", "onBackPressed Called");
//        Intent iAppointment = new Intent(this,Appointment.class);
//        iAppointment.putExtra("selectedDate",selectedDate);
//        startActivity(iAppointment);
//    }
//private void ApiSchedule(String serviceId, String spinnerText, final String mDate, final String accountIDs) {
//
//
//    ApiInterface apiService = ApiClient.getClient(getContext()).create(ApiInterface.class);
//
//
////    final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
//  //  mDialog.show();
//
//
//    Call<ArrayList<AppointmentSchedule>> call = apiService.getAppointmentSchedule(serviceId, spinnerText, mDate, accountIDs);
//
//    call.enqueue(new Callback<ArrayList<AppointmentSchedule>>() {
//        @Override
//        public void onResponse(Call<ArrayList<AppointmentSchedule>> call, Response<ArrayList<AppointmentSchedule>> response) {
//
//            try {
//
////                if (mDialog.isShowing())
////                    Config.closeDialog(getParent(), mDialog);
//
//
//                if (response.code() == 200) {
//                    schedResponse = response.body();
//
//                    Log.i("responseeee", new Gson().toJson(response.body()));
//                    Log.i("responseeee", String.valueOf(response.body().get(0).getId()));
//                    String id = String.valueOf(response.body().get(0).getId());
//                    ApiScheduleId(id, mDate, accountIDs);
//
//                }
//
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//
//        @Override
//        public void onFailure(Call<ArrayList<AppointmentSchedule>> call, Throwable t) {
//            // Log error here since request failed
//            Config.logV("Fail---------------" + t.toString());
////            if (mDialog.isShowing())
////                Config.closeDialog(getParent(), mDialog);
//
//        }
//    });
//
//
//}
    private void ApiScheduleId(String id, final String mDate, final String accountIDs) {


        ApiInterface apiService = ApiClient.getClient(getContext()).create(ApiInterface.class);


//        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
//        mDialog.show();


        Call<ScheduleId> call = apiService.getAppointmentScheduleId(id, mDate, accountIDs);

        call.enqueue(new Callback<ScheduleId>() {
            @Override
            public void onResponse(Call<ScheduleId> call, Response<ScheduleId> response) {

                try {

//                    if (mDialog.isShowing())
//                        Config.closeDialog(getParent(), mDialog);


                    if (response.code() == 200) {
                        timeslots.clear();
                        if(response.body().getAvailableSlots()!=null){
                        int availableslots = response.body().getAvailableSlots().size();

                            for (int i = 0; i < availableslots; i++) {
                                if (response.body().getAvailableSlots().get(i).getActive().equalsIgnoreCase("true") && !response.body().getAvailableSlots().get(i).getNoOfAvailbleSlots().equalsIgnoreCase("0")) {
                                    timeslots.add(response.body().getAvailableSlots().get(i).getTime());
                                }


                            }
                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
                            recycle_timeslots.setLayoutManager(mLayoutManager);
                            sAdapter = new TimeSlotsAdapter(timeslots);
                            recycle_timeslots.setAdapter(sAdapter);
                            sAdapter.notifyDataSetChanged();
                            recycle_timeslots.setAlpha(1);
                            if (timeslots != null) {
                                earliestAvailable.setText("Earliest available\n" + timeslots.get(0));
                            }

                            Log.i("timeslots", timeslots.toString());

                        }
                        else{
                            recycle_timeslots.setAlpha(0);
                            Toast.makeText(AppointmentDate.this, "Appointment for this service is not available at the moment. Please try for a different time or date", Toast.LENGTH_SHORT).show();
                        }
                    }



                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ScheduleId> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
//                if (mDialog.isShowing())
//                    Config.closeDialog(getParent(), mDialog);

            }
        });


    }

}
