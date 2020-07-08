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
import android.widget.LinearLayout;
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
     TextView  tv_noavail_slot;
     CalendarView cv;
     Button btn_confirm;
     String selectedDate;
     TextView tv_title;
     static int serviceId;
     static int mSpinnertext;
     static String accountId;
     static String schdId;
    ArrayList<AppointmentSchedule> schedResponse = new ArrayList<>();
    static ArrayList<String> timeslots= new ArrayList<>();
    Date last_date =new Date();
    static String selectDate;
    static int i = 0;
    static TextView tv_queue;
    static TextView tv_queuetime;
    static LinearLayout queuelayout;
    static TextView tv_waittime;
    static TextView txtnocheckin;
    String id = " ";
    static ImageView ic_left, ic_right;




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
        tv_noavail_slot =findViewById(R.id.noavailableslot);
        tv_queue = findViewById(R.id.txt_queue);
        tv_queuetime = findViewById(R.id.txt_queuetime);
        queuelayout = findViewById(R.id.queuelayout);
        ic_left = findViewById(R.id.ic_left);
        ic_right = findViewById(R.id.ic_right);
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
            selectDate = extras.getString("selectDate");
        }
        try {
            cv.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(selectDate).getTime(), true, true);
            ApiSchedule(String.valueOf(serviceId), String.valueOf(mSpinnertext), selectDate, accountId);
           // ApiScheduleId(schdId, selectDate, accountId);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(!timeslot.isEmpty()){
            tv_noavail_slot.setVisibility(View.GONE);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        recycle_timeslots.setLayoutManager(mLayoutManager);
        sAdapter = new TimeSlotsAdapter(timeslot);
        recycle_timeslots.setAdapter(sAdapter);
        sAdapter.notifyDataSetChanged();
        } else{
            tv_noavail_slot.setVisibility(View.VISIBLE);
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
                ApiSchedule(String.valueOf(serviceId), String.valueOf(mSpinnertext), sdf.format(last_date), accountId);
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
private void ApiSchedule(String serviceId, String spinnerText, final String mDate, final String accountIDs) {


    ApiInterface apiService = ApiClient.getClient(getContext()).create(ApiInterface.class);


//    final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
  //  mDialog.show();


    Call<ArrayList<AppointmentSchedule>> call = apiService.getAppointmentSchedule(serviceId, spinnerText, mDate, accountIDs);

    call.enqueue(new Callback<ArrayList<AppointmentSchedule>>() {
        @Override
        public void onResponse(Call<ArrayList<AppointmentSchedule>> call, Response<ArrayList<AppointmentSchedule>> response) {

            try {

//                if (mDialog.isShowing())
//                    Config.closeDialog(getParent(), mDialog);


                if (response.code() == 200) {
                    schedResponse = response.body();
                  if(schedResponse.size()!=0) {

                      if (schedResponse.size() > 0) {
                          i = 0;
                      }

                      if (schedResponse.size() == 1) {
                          tv_queue.setText("Time window");
                      } else {
                          tv_queue.setText("Choose the time window");
                      }

                      if (schedResponse.size() == 1) {
                          tv_queue.setText("Time window");
                      } else {
                          tv_queue.setText("Choose the time window");
                      }

                      if (schedResponse.size() > 0) {

//                            tv_queuename.setVisibility(View.GONE);
                          tv_queuetime.setVisibility(View.VISIBLE);
                          tv_queue.setVisibility(View.VISIBLE);
                          queuelayout.setVisibility(View.VISIBLE);
                          //  tv_waittime.setVisibility(View.VISIBLE);
                          //   txtnocheckin.setVisibility(View.GONE);
                          if (schedResponse.get(i).getId() != 0) {
                              id = String.valueOf(schedResponse.get(i).getId());
                          }


//                            tv_queuename.setText(mQueueTimeSlotList.get(0).getName());
                          tv_queuetime.setText(schedResponse.get(0).getApptSchedule().getTimeSlots().get(0).getsTime() + "- " + schedResponse.get(0).getApptSchedule().getTimeSlots().get(0).geteTime());


                          if (schedResponse.get(i).getId() != 0) {
                              id = String.valueOf(schedResponse.get(i).getId());
                              ApiScheduleId(id, mDate, accountIDs);
                          }


                      } else {


                          tv_queue.setVisibility(View.GONE);
                          queuelayout.setVisibility(View.GONE);
//                            tv_queuename.setVisibility(View.GONE);
                          tv_queuetime.setVisibility(View.GONE);
                          //  tv_waittime.setVisibility(View.GONE);
                          //   txtnocheckin.setVisibility(View.VISIBLE);
                          //    txtnocheckin.setText("Appointment "+ " this service is not available at the moment. Please try for a different time or date");
                      }


                      if (schedResponse.size() > 1) {

                          ic_right.setVisibility(View.VISIBLE);
                          ic_left.setVisibility(View.VISIBLE);
                          ic_right.setImageResource(R.drawable.icon_right_angle_active);
                          ic_right.setEnabled(true);


                      } else {
                          ic_right.setVisibility(View.INVISIBLE);
                          ic_left.setVisibility(View.INVISIBLE);
                      }

                      if (i > 0) {
                          ic_left.setEnabled(true);
                          ic_left.setImageResource(R.drawable.icon_left_angle_active);
                      } else {
                          ic_left.setEnabled(false);
                          ic_left.setImageResource(R.drawable.icon_left_angle_disabled);
                      }


                      ic_left.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {

                              i--;
                              Config.logV("Left Click------------------**" + i);
                              if (i >= 0) {

//                                    tv_queuename.setText(mQueueTimeSlotList.get(i).getName());
                                  tv_queuetime.setText(schedResponse.get(i).getApptSchedule().getTimeSlots().get(0).getsTime() + "- " + schedResponse.get(i).getApptSchedule().getTimeSlots().get(0).geteTime());


                                  if (schedResponse.get(i).getId() != 0) {
                                      id = String.valueOf(schedResponse.get(i).getId());
                                      ApiScheduleId(id, mDate, accountIDs);

                                  }

                              }


                              if (i < schedResponse.size()) {
                                  ic_right.setEnabled(true);
                                  ic_right.setImageResource(R.drawable.icon_right_angle_active);
                              } else {
                                  ic_right.setEnabled(false);
                                  ic_right.setImageResource(R.drawable.icon_right_angle_disabled);
                              }

                              if (i <= 0) {
                                  ic_left.setEnabled(false);
                                  ic_left.setImageResource(R.drawable.icon_left_angle_disabled);
                              } else {

                                  ic_left.setEnabled(true);
                                  ic_left.setImageResource(R.drawable.icon_left_angle_active);
                              }

                          }
                      });

                      ic_right.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {

                              if (i < 0) {
                                  i = 0;
                              }
                              i++;
                              Config.logV("Right Click----1111--------------" + i);
                              if (i < schedResponse.size()) {

//                                    tv_queuename.setText(mQueueTimeSlotList.get(i).getName());
                                  tv_queuetime.setText(schedResponse.get(i).getApptSchedule().getTimeSlots().get(0).getsTime() + "- " + schedResponse.get(i).getApptSchedule().getTimeSlots().get(0).geteTime());


                                  if (schedResponse.get(i).getId() != 0) {
                                      id = String.valueOf(schedResponse.get(i).getId());
                                      ApiScheduleId(id, mDate, accountIDs);

                                  }

                              }

                              if (i >= 0) {
                                  ic_left.setEnabled(true);
                                  ic_left.setImageResource(R.drawable.icon_left_angle_active);
                              } else {
                                  ic_left.setEnabled(false);
                                  ic_left.setImageResource(R.drawable.icon_left_angle_disabled);
                              }


                              if (i == schedResponse.size() - 1) {

                                  ic_right.setEnabled(false);
                                  ic_right.setImageResource(R.drawable.icon_right_angle_disabled);
                              } else {
                                  ic_right.setEnabled(true);
                                  ic_right.setImageResource(R.drawable.icon_right_angle_active);
                              }

                          }
                      });

                  }
                  else{
                      tv_queuetime.setVisibility(View.GONE);
                      tv_queue.setVisibility(View.GONE);
                      queuelayout.setVisibility(View.GONE);
                      tv_noavail_slot.setVisibility(View.VISIBLE);
                      recycle_timeslots.setAlpha(0);
                      Toast.makeText(AppointmentDate.this, "Appointment for this service is not available at the moment. Please try for a different time or date", Toast.LENGTH_SHORT).show();
                  }

                    }



            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onFailure(Call<ArrayList<AppointmentSchedule>> call, Throwable t) {
            // Log error here since request failed
            Config.logV("Fail---------------" + t.toString());
//            if (mDialog.isShowing())
//                Config.closeDialog(getParent(), mDialog);

        }
    });


}
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
                            if(timeslots.size()==0){
                                tv_noavail_slot.setVisibility(View.VISIBLE);
                            }
                            else{
                            tv_noavail_slot.setVisibility(View.GONE);}
                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
                            recycle_timeslots.setLayoutManager(mLayoutManager);
                            sAdapter = new TimeSlotsAdapter(timeslots);
                            recycle_timeslots.setAdapter(sAdapter);
                            sAdapter.notifyDataSetChanged();
                            recycle_timeslots.setAlpha(1);
                            if (timeslots != null) {
                                earliestAvailable.setText("Earliest available\n" + timeslots.get(0));
                            }



                        }
                        else{
                            tv_noavail_slot.setVisibility(View.VISIBLE);
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
    public static void timeslot(String timeSlot) {
        if(timeSlot.equalsIgnoreCase("") && timeslots.size()!=0){
            earliestAvailable.setText("Timeslot not picked");
        }
    }


    }
