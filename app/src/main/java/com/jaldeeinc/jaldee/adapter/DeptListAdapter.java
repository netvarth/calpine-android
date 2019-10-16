package com.jaldeeinc.jaldee.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jaldeeinc.jaldee.Fragment.SearchDetailViewFragment;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Appointment;
import com.jaldeeinc.jaldee.activities.CheckIn;
import com.jaldeeinc.jaldee.activities.SearchServiceActivity;
import com.jaldeeinc.jaldee.callback.AdapterCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CircleTransform;
import com.jaldeeinc.jaldee.custom.CustomTypefaceSpan;
import com.jaldeeinc.jaldee.model.SearchListModel;
import com.jaldeeinc.jaldee.model.WorkingModel;
import com.jaldeeinc.jaldee.response.SearchService;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jaldeeinc.jaldee.connection.ApiClient.context;

public class DeptListAdapter extends RecyclerView.Adapter {
    List<SearchListModel> searchList;
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private AdapterCallback mAdapterCallback;
    Activity activity;
    ArrayList<WorkingModel> workingModelArrayList = new ArrayList<>();
    SearchDetailViewFragment searchDetailViewFragment;
    String termilogy;
    String countTerminology;

    public DeptListAdapter(FragmentActivity activity, List<SearchListModel> msearchList, SearchDetailViewFragment searchDetailViewFragment) {
        this.searchList = msearchList;
        this.searchDetailViewFragment = searchDetailViewFragment;
        this.activity = activity;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        public TextView deptName;
        public TextView tv_name, tv_location, tv_domain, tv_Futuredate, tv_WaitTime, tv_spec1, tv_spec2, tv_spec3, tv_spec22, tv_count;
        LinearLayout L_specialization, L_services, L_layout_type, L_checkin;

        ImageView ic_jaldeeverifiedIcon;
        ImageView profile;
        RatingBar rating;
        TextView tv_claimable, tv_distance, tv_branch_name;
        Button btncheckin, btnappointment;
        LinearLayout layout_row;
        TextView mImageViewText;
        LinearLayout layout_type;

        public MyViewHolder(View view) {
            super(view);

            deptName = (TextView) view.findViewById(R.id.deptName);
            L_checkin = view.findViewById(R.id.checkinlayout);
            ic_jaldeeverifiedIcon = view.findViewById(R.id.ic_jaldeeverifiedIcon);
            tv_name = view.findViewById(R.id.name);
            tv_count = view.findViewById(R.id.count_search);
            tv_claimable = view.findViewById(R.id.claimable);
            tv_branch_name = view.findViewById(R.id.branch_name);
            tv_location = view.findViewById(R.id.location);
            tv_domain = view.findViewById(R.id.domain);
            profile = view.findViewById(R.id.profile);
            rating = view.findViewById(R.id.mRatingBar);
            L_services = view.findViewById(R.id.service);
            tv_distance = view.findViewById(R.id.distance);
            btncheckin = view.findViewById(R.id.btncheckin);
            btnappointment = view.findViewById(R.id.btnappointment);
            tv_Futuredate = view.findViewById(R.id.txt_diffdate);
            tv_WaitTime = view.findViewById(R.id.txtWaitTime);
            L_specialization = view.findViewById(R.id.Lspec);
            L_layout_type = view.findViewById(R.id.layout_type);
            layout_row = view.findViewById(R.id.layout_row);
            tv_spec1 = view.findViewById(R.id.txtspec1);
            tv_spec2 = view.findViewById(R.id.txtspec2);
            tv_spec3 = view.findViewById(R.id.txtspec3);
            tv_spec22 = view.findViewById(R.id.txtspec22);
            mImageViewText = view.findViewById(R.id.mImageViewText);
            layout_type = view.findViewById(R.id.layout_type);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.department_search_list_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final SearchListModel searchdetailList = searchList.get(position);

        final DeptListAdapter.MyViewHolder myViewHolder = (DeptListAdapter.MyViewHolder) holder;

        Config.logV("VERified-----" + searchdetailList.getYnw_verified() + "name" + searchdetailList.getTitle());

        Config.logV("VERified-@@@@----" + searchdetailList.getYnw_verified_level() + "name" + searchdetailList.getTitle());
        if (searchdetailList.getYnw_verified_level() != null) {
            if (searchdetailList.getYnw_verified() == 1) {
                if (searchdetailList.getYnw_verified_level().equalsIgnoreCase("2")) {

                    myViewHolder.ic_jaldeeverifiedIcon.setVisibility(View.VISIBLE);
                    myViewHolder.ic_jaldeeverifiedIcon.setImageResource(R.drawable.jaldee_basic);
                    //myViewHolder.tv_ynw_verified.setText("Basic");
                }
                if (searchdetailList.getYnw_verified_level().equalsIgnoreCase("3")) {
                          /*  myViewHolder.tv_ynw_verified.setVisibility(View.VISIBLE);
                            myViewHolder.tv_ynw_verified.setText("Basic Plus");*/
                    myViewHolder.ic_jaldeeverifiedIcon.setVisibility(View.VISIBLE);
                    myViewHolder.ic_jaldeeverifiedIcon.setImageResource(R.drawable.jaldee_basicplus);
                }
                if (searchdetailList.getYnw_verified_level().equalsIgnoreCase("4")) {

                           /* myViewHolder.tv_ynw_verified.setVisibility(View.VISIBLE);
                            myViewHolder.tv_ynw_verified.setText("Premium");*/
                    myViewHolder.ic_jaldeeverifiedIcon.setVisibility(View.VISIBLE);
                    myViewHolder.ic_jaldeeverifiedIcon.setImageResource(R.drawable.jaldee_adv);
                }
            } else {
                myViewHolder.ic_jaldeeverifiedIcon.setVisibility(View.INVISIBLE);

            }
        } else {
            myViewHolder.ic_jaldeeverifiedIcon.setVisibility(View.INVISIBLE);
        }


        myViewHolder.ic_jaldeeverifiedIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDetailViewFragment.onMethodJaldeeLogo(searchdetailList.getYnw_verified_level(), searchdetailList.getTitle());
            }
        });
////////////////////////////7 types////////////////////////////////////////////
               /* ArrayList<ParkingModel> listType = new ArrayList<>();
                listType.clear();*/

        myViewHolder.layout_type.removeAllViews();
        LinearLayout parent = new LinearLayout(context);

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        parent.setOrientation(LinearLayout.HORIZONTAL);
        parent.setLayoutParams(params1);
        TextView firstCoupon = new TextView(context);
        Typeface tyface_3 = Typeface.createFromAsset(context.getAssets(),
                "fonts/Montserrat_Regular.otf");
        firstCoupon.setTypeface(tyface_3);
        firstCoupon.setText("SignUp Coupon");
        firstCoupon.setText(context.getResources().getString(R.string.first_coupon));
        firstCoupon.setTextSize(13);
        firstCoupon.setTextColor(context.getResources().getColor(R.color.title_grey));
        firstCoupon.setPadding(5, 5, 5, 5);
        firstCoupon.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icc_coupon, 0, 0);
        firstCoupon.setVisibility(View.GONE);
        //dynaText.setEllipsize(TextUtils.TruncateAt.END);
        firstCoupon.setMaxLines(2);
        firstCoupon.setLayoutParams(params1);
        params1.setMargins(10, 7, 10, 7);
        firstCoupon.setGravity(Gravity.CENTER);
        parent.addView(firstCoupon);


        if (searchdetailList.getFirst_checkin_coupon_count() != null && searchdetailList.getFirst_checkin_coupon_count().equals("1")) {
            firstCoupon.setVisibility(View.VISIBLE);
        }

        firstCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDetailViewFragment.
                        onMethodFirstCoupn(searchdetailList.getUniqueid());
            }
        });

        TextView dynaText2 = new TextView(context);
        Typeface tyface_2 = Typeface.createFromAsset(context.getAssets(),
                "fonts/Montserrat_Regular.otf");
        dynaText2.setTypeface(tyface_2);
        dynaText2.setText("Coupon");
        dynaText2.setTextSize(13);
        dynaText2.setTextColor(context.getResources().getColor(R.color.title_grey));
        dynaText2.setPadding(5, 5, 5, 5);
        dynaText2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icc_coupon, 0, 0);
        dynaText2.setVisibility(View.GONE);
        //dynaText.setEllipsize(TextUtils.TruncateAt.END);
        dynaText2.setMaxLines(1);
        dynaText2.setLayoutParams(params1);
        params1.setMargins(10, 7, 10, 7);
        dynaText2.setGravity(Gravity.LEFT);
        parent.addView(dynaText2);

        if (searchdetailList.getCoupon_enabled() > 0) {
            dynaText2.setVisibility(View.VISIBLE);
        }

        dynaText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                searchDetailViewFragment.onMethodCoupn(searchdetailList.getUniqueid());
            }
        });
        TextView dynaText1 = new TextView(context);
        Typeface tyface_5 = Typeface.createFromAsset(context.getAssets(),
                "fonts/Montserrat_Regular.otf");
        dynaText1.setTypeface(tyface_5);
        dynaText1.setText("Enquiry");
        dynaText1.setTextSize(13);
        dynaText1.setTextColor(context.getResources().getColor(R.color.title_grey));
        dynaText1.setPadding(5, 5, 5, 5);
        dynaText1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_message_gray, 0, 0);
        //dynaText.setEllipsize(TextUtils.TruncateAt.END);
        dynaText1.setMaxLines(1);

        dynaText1.setLayoutParams(params1);
        params1.setMargins(10, 7, 10, 7);
        dynaText1.setGravity(Gravity.LEFT);
        parent.addView(dynaText1);

        dynaText1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchDetailViewFragment.onMethodMessage(searchdetailList.getTitle(), searchdetailList.getId(), "search");

            }
        });


        if (searchdetailList.getBusiness_hours1() != null) {
            TextView dynaText = new TextView(context);
            Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                    "fonts/Montserrat_Regular.otf");
            dynaText.setTypeface(tyface);
            dynaText.setText(context.getResources().getString(R.string.working_hours));

            dynaText.setTextSize(13);
            dynaText.setTextColor(context.getResources().getColor(R.color.title_grey));
            dynaText.setPadding(5, 5, 5, 5);
            dynaText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_clock, 0, 0);
            //dynaText.setEllipsize(TextUtils.TruncateAt.END);
            dynaText.setMaxLines(2);


            dynaText.setLayoutParams(params1);
            params1.setMargins(10, 7, 10, 7);
            dynaText.setGravity(Gravity.CENTER);
            parent.addView(dynaText);
            dynaText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (searchdetailList.getBusiness_hours1() != null) {

                        if (searchdetailList.getBusiness_hours1() != null) {
                            try {
                                String array_json = searchdetailList.getBusiness_hours1().toString();


                                try {


                                    //Get the instance of JSONArray that contains JSONObjects
                                    JSONArray jsonArray = new JSONArray(array_json);
                                    String jsonarry = jsonArray.getString(0);
                                    JSONArray jsonArray1 = new JSONArray(jsonarry);

                                    //Iterate the jsonArray and print the info of JSONObjects

                                    workingModelArrayList.clear();

                                    for (int i = 0; i < jsonArray1.length(); i++) {
                                        JSONObject jsonObject = jsonArray1.getJSONObject(i);

                                        String id = jsonObject.optString("recurringType").toString();
                                        String repeatinterval = jsonObject.optString("repeatIntervals").toString();
                                        String timeslot = jsonObject.optString("timeSlots").toString();
                                        // String publish_date = jsonObject.optString("publish_date").toString();
                                        JSONArray jsonArray_time = new JSONArray(timeslot);
                                        JSONObject jsonObject_time = jsonArray_time.getJSONObject(0);
                                        String sTime = jsonObject_time.optString("sTime").toString();
                                        String eTime = jsonObject_time.optString("eTime").toString();


                                        JSONArray jsonArray_repeat = new JSONArray(repeatinterval);

                                        for (int k = 0; k < jsonArray_repeat.length(); k++) {
                                            String repeat = jsonArray_repeat.getString(k);

                                            WorkingModel work = new WorkingModel();
                                            if (repeat.equalsIgnoreCase("2")) {

                                                work.setDay("Monday");
                                                work.setTime_value(sTime + "-" + eTime);

                                            }
                                            if (repeat.equalsIgnoreCase("3")) {

                                                work.setDay("Tuesday");
                                                work.setTime_value(sTime + "-" + eTime);

                                            }
                                            if (repeat.equalsIgnoreCase("4")) {
                                                work.setDay("Wednesday");
                                                work.setTime_value(sTime + "-" + eTime);
                                            }
                                            if (repeat.equalsIgnoreCase("5")) {

                                                work.setDay("Thursday");
                                                work.setTime_value(sTime + "-" + eTime);
                                            }
                                            if (repeat.equalsIgnoreCase("6")) {

                                                work.setDay("Friday");
                                                work.setTime_value(sTime + "-" + eTime);

                                            }
                                            if (repeat.equalsIgnoreCase("7")) {

                                                work.setDay("Saturday");
                                                work.setTime_value(sTime + "-" + eTime);

                                            }
                                            if (repeat.equalsIgnoreCase("1")) {

                                                work.setDay("Sunday");
                                                work.setTime_value(sTime + "-" + eTime);

                                            }

                                            workingModelArrayList.add(work);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        searchDetailViewFragment.onMethodWorkingCallback(workingModelArrayList, searchdetailList.getUniqueid());
                        //   Config.logV("Working-----------" + workingModelArrayList.size() + "tt" + searchdetailList.getTitle());
                    }
                }
            });
        }
        if (searchdetailList.getTerminologies() != null) {
            try {
                String array_json = searchdetailList.getTerminologies().toString();
                Log.i("terminos", array_json);
                try {
                    //Get the instance of JSONArray that contains JSONObjects

                    JSONObject term = new JSONObject(searchdetailList.getTerminologies().get(0).toString());
                    termilogy = term.get("waitlist").toString();
                    countTerminology = term.get("provider").toString();
                    Log.i("waitlistAlternate", term.get("waitlist").toString());


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (searchdetailList.getParking_type_location1() != null) {
            if (searchdetailList.getParking_location1().equalsIgnoreCase("1")) {
                TextView dynaText = new TextView(context);
                Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                        "fonts/Montserrat_Regular.otf");
                dynaText.setTypeface(tyface);
                dynaText.setText(Config.toTitleCase(searchdetailList.getParking_type_location1()) + " Parking ");
                dynaText.setTextSize(13);
                dynaText.setTextColor(context.getResources().getColor(R.color.title_grey));
                dynaText.setPadding(5, 5, 5, 5);
                dynaText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_parking, 0, 0);
                dynaText.setMaxLines(1);
                params1.setMargins(10, 7, 10, 7);
                dynaText.setGravity(Gravity.LEFT);
                dynaText.setLayoutParams(params1);
                parent.addView(dynaText);
                dynaText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(activity, searchdetailList.getParking_type_location1() + " parking available", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        if (searchdetailList.getAlways_open_location1() != null) {
            if (searchdetailList.getAlways_open_location1().equalsIgnoreCase("1")) {
                TextView dynaText = new TextView(context);
                Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                        "fonts/Montserrat_Regular.otf");
                dynaText.setTypeface(tyface);
                dynaText.setText("24 Hours");
                dynaText.setTextSize(13);
                dynaText.setTextColor(context.getResources().getColor(R.color.title_grey));
                dynaText.setPadding(5, 5, 5, 5);
                dynaText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_24hours, 0, 0);
                dynaText.setMaxLines(1);
                params1.setMargins(10, 7, 10, 7);
                dynaText.setGravity(Gravity.LEFT);
                dynaText.setLayoutParams(params1);
                parent.addView(dynaText);

                dynaText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(activity, "Open 24 hours", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        if (searchdetailList.getDentistemergencyservices_location1() != null) {
            if (searchdetailList.getDentistemergencyservices_location1().equalsIgnoreCase("1")) {

                TextView dynaText = new TextView(context);
                Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                        "fonts/Montserrat_Regular.otf");
                dynaText.setTypeface(tyface);
                dynaText.setText("Emergency");
                dynaText.setTextSize(13);
                dynaText.setTextColor(context.getResources().getColor(R.color.title_grey));
                dynaText.setPadding(5, 5, 5, 5);
                dynaText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_emergency, 0, 0);
                dynaText.setMaxLines(1);

                dynaText.setLayoutParams(params1);
                params1.setMargins(10, 7, 10, 7);
                dynaText.setGravity(Gravity.LEFT);
                parent.addView(dynaText);

                dynaText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(activity, "Emergency services available", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        if (searchdetailList.getDocambulance_location1() != null) {
            if (searchdetailList.getDocambulance_location1().equalsIgnoreCase("1")) {

                TextView dynaText = new TextView(context);
                Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                        "fonts/Montserrat_Regular.otf");
                dynaText.setTypeface(tyface);
                dynaText.setText("Ambulance");
                dynaText.setTextSize(13);
                dynaText.setTextColor(context.getResources().getColor(R.color.title_grey));
                dynaText.setPadding(5, 5, 5, 5);
                dynaText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_ambulance, 0, 0);
                //dynaText.setEllipsize(TextUtils.TruncateAt.END);
                dynaText.setMaxLines(1);

                dynaText.setLayoutParams(params1);
                params1.setMargins(10, 7, 10, 7);
                dynaText.setGravity(Gravity.LEFT);
                parent.addView(dynaText);

                dynaText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(activity, "Ambulance services available", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        if (searchdetailList.getFirstaid_location1() != null) {
            if (searchdetailList.getFirstaid_location1().equalsIgnoreCase("1")) {
                TextView dynaText = new TextView(context);
                Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                        "fonts/Montserrat_Regular.otf");
                dynaText.setTypeface(tyface);
                dynaText.setText("First Aid");
                dynaText.setTextSize(13);
                dynaText.setTextColor(context.getResources().getColor(R.color.title_grey));
                dynaText.setPadding(5, 5, 5, 5);
                dynaText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_firstaid, 0, 0);
                //dynaText.setEllipsize(TextUtils.TruncateAt.END);
                dynaText.setMaxLines(1);
                dynaText.setLayoutParams(params1);
                params1.setMargins(10, 7, 10, 7);
                dynaText.setGravity(Gravity.LEFT);
                parent.addView(dynaText);

                dynaText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(activity, "First aid services available", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        if (searchdetailList.getPhysiciansemergencyservices_location1() != null) {
            if (searchdetailList.getPhysiciansemergencyservices_location1().equalsIgnoreCase("1")) {
                TextView dynaText = new TextView(context);
                Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                        "fonts/Montserrat_Regular.otf");
                dynaText.setTypeface(tyface);
                dynaText.setText("Emergency");
                dynaText.setTextSize(13);
                dynaText.setTextColor(context.getResources().getColor(R.color.title_grey));
                dynaText.setPadding(5, 5, 5, 5);
                dynaText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_emergency, 0, 0);
                //dynaText.setEllipsize(TextUtils.TruncateAt.END);
                dynaText.setMaxLines(1);
                dynaText.setLayoutParams(params1);
                params1.setMargins(10, 7, 10, 7);
                dynaText.setGravity(Gravity.LEFT);
                parent.addView(dynaText);

                dynaText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(activity, "Emergency services available", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        if (searchdetailList.getTraumacentre_location1() != null) {
            if (searchdetailList.getTraumacentre_location1().equalsIgnoreCase("1")) {

                TextView dynaText = new TextView(context);
                Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                        "fonts/Montserrat_Regular.otf");
                dynaText.setTypeface(tyface);
                dynaText.setText("Trauma");
                dynaText.setTextSize(13);
                dynaText.setTextColor(context.getResources().getColor(R.color.title_grey));
                dynaText.setPadding(5, 5, 5, 5);
                dynaText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_trauma, 0, 0);
                //dynaText.setEllipsize(TextUtils.TruncateAt.END);
                dynaText.setMaxLines(1);

                dynaText.setLayoutParams(params1);
                params1.setMargins(10, 7, 10, 7);
                dynaText.setGravity(Gravity.LEFT);
                parent.addView(dynaText);

                dynaText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(activity, "Trauma care available", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
        myViewHolder.layout_type.addView(parent);
        //////////////////////////////////////////////////////////////
        Typeface tyface_confm = Typeface.createFromAsset(context.getAssets(),
                "fonts/Montserrat_Bold.otf");
        myViewHolder.tv_name.setTypeface(tyface_confm);
        myViewHolder.btncheckin.setTypeface(tyface_confm);
        // myViewHolder.tv_Open.setTypeface(tyface_confm);

        myViewHolder.tv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDetailViewFragment.onMethodOpenMap(searchdetailList.getLocation1());
            }
        });
        myViewHolder.layout_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Config.logV("UNIUE ID----------------" + searchdetailList.getUniqueid());

                Config.logV("Popular Text__________@@@Dele");
                String unique_id = searchdetailList.getUniqueid();
                searchDetailViewFragment.onMethodCallback(searchdetailList.getUniqueid(),searchdetailList.getClaimable());
            }
        });

        if (searchdetailList.getAccountType() != null) {
            if (searchdetailList.getAccountType().equals("1")) {
                myViewHolder.tv_branch_name.setText(searchdetailList.getBranch_name());
                myViewHolder.tv_branch_name.setVisibility(View.VISIBLE);
                Log.i("qazqaz", String.valueOf(searchdetailList.getBranchSpCount()));
            } else if ((searchdetailList.getAccountType().equals("0"))) {
                myViewHolder.tv_count.setVisibility(View.VISIBLE);
                Log.i("qaz", String.valueOf(searchdetailList.getBranchSpCount()));
            }
        } else {
            myViewHolder.tv_branch_name.setVisibility(View.GONE);
        }
        Log.i("Claaaaim", String.valueOf(searchdetailList.getClaimable()));

        if (searchdetailList.getClaimable().equals("1")) {
            myViewHolder.tv_claimable.setVisibility(View.VISIBLE);
            myViewHolder.L_layout_type.setVisibility(View.GONE);
            myViewHolder.L_checkin.setVisibility(View.GONE);

        } else {
            myViewHolder.tv_claimable.setVisibility(View.INVISIBLE);
            myViewHolder.L_layout_type.setVisibility(View.VISIBLE);
            myViewHolder.L_checkin.setVisibility(View.VISIBLE);
        }
        myViewHolder.tv_claimable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, "Use jaldee.com in Web Browser to Claim your Business", Toast.LENGTH_SHORT).show();
            }
        });
        if (searchdetailList.getQualification() != null) {
            myViewHolder.tv_name.setText(searchdetailList.getTitle() + " " + searchdetailList.getQualification());
        } else {
            myViewHolder.tv_name.setText(searchdetailList.getTitle());
        }
        if (searchdetailList.getSector() != null) {
            myViewHolder.tv_domain.setVisibility(View.VISIBLE);
            myViewHolder.tv_domain.setText(searchdetailList.getSector());
        } else {
            myViewHolder.tv_domain.setVisibility(View.GONE);
        }
        if (searchdetailList.getPlace1() != null) {
            myViewHolder.tv_location.setVisibility(View.VISIBLE);
            // myViewHolder.tv_location.setText(searchdetailList.getPlace1());
            Config.logV("Place @@@@@@@@@@@@@@" + searchdetailList.getDistance());
            Double distance = Double.valueOf(searchdetailList.getDistance()) * 1.6;

            myViewHolder.tv_location.setText(searchdetailList.getPlace1() + " ( " + String.format("%.2f", distance) + " km )");
        } else {
            myViewHolder.tv_location.setVisibility(View.GONE);
        }
        if (searchdetailList.getFuture_checkins() != null) {
            if (searchdetailList.getFuture_checkins().equalsIgnoreCase("1")) {
                myViewHolder.tv_Futuredate.setVisibility(View.VISIBLE);
                myViewHolder.tv_Futuredate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent iCheckIn = new Intent(v.getContext(), CheckIn.class);
                        iCheckIn.putExtra("serviceId", Integer.parseInt(searchdetailList.getmLoc()));
                        iCheckIn.putExtra("uniqueID", searchdetailList.getUniqueid());
                        iCheckIn.putExtra("accountID", searchdetailList.getId());

                        iCheckIn.putExtra("googlemap", searchdetailList.getLocation1());
                        // iCheckIn.putExtra("waititme", myViewHolder.tv_WaitTime.getText().toString());
                        iCheckIn.putExtra("from", "future_date");
                        iCheckIn.putExtra("title", searchdetailList.getTitle());
                        iCheckIn.putExtra("place", searchdetailList.getPlace1());
                        Config.logV("sector%%%%%%-------------" + searchdetailList.getSectorname());
                        iCheckIn.putExtra("sector", searchdetailList.getSectorname());
                        iCheckIn.putExtra("subsector", searchdetailList.getSub_sector());
                        context.startActivity(iCheckIn);
                    }
                });
            } else {
                myViewHolder.tv_Futuredate.setVisibility(View.GONE);
            }
        } else {
            myViewHolder.tv_Futuredate.setVisibility(View.GONE);
        }
//////////////////////////////////////////////////////////////////////////////////////////

        if (searchdetailList.getId().equalsIgnoreCase(searchdetailList.getQId())) {
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = df.format(c);
            System.out.println("Current time => " + formattedDate);


            Date date1 = null, date2 = null;
            try {
                date1 = df.parse(formattedDate);
                if (searchdetailList.getAvail_date() != null )
                    date2 = df.parse(searchdetailList.getAvail_date());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (searchdetailList.getMessage() != null && searchdetailList.getClaimable() == null) {
//                myViewHolder.tv_qmessage.setVisibility(View.VISIBLE);
//                myViewHolder.tv_qmessage.setText(searchdetailList.getMessage());
            } else {
//                myViewHolder.tv_qmessage.setVisibility(View.GONE);
            }
            if (searchdetailList.getShow_waiting_time() != null) {

                // For ML/Fixed
                if (searchdetailList.getShow_waiting_time().equalsIgnoreCase("1")) {

                    if(searchdetailList.isOnlineCheckIn() != false) {
                        if (searchdetailList.getAvail_date() != null) {
                            myViewHolder.tv_WaitTime.setVisibility(View.VISIBLE);

                            // For Today's Checkin
                            if (formattedDate.equalsIgnoreCase(searchdetailList.getAvail_date())) {
                                // Next Available Timing
                                if (searchdetailList.getServiceTime() != null) {
                                    Typeface tyface1 = Typeface.createFromAsset(context.getAssets(),
                                            "fonts/Montserrat_Bold.otf");
                                    String firstWord = "Next Available Time ";
                                    String secondWord = "\nToday, " + searchdetailList.getServiceTime();
                                    myViewHolder.tv_WaitTime.setText(firstWord + secondWord);
                                } else { // Est. Waiting Time
                                    Typeface tyface1 = Typeface.createFromAsset(context.getAssets(),
                                            "fonts/Montserrat_Bold.otf");
                                    String firstWord = "Est Wait Time ";
                                    String secondWord = "\n" + Config.getTimeinHourMinutes(searchdetailList.getQueueWaitingTime());
                                    myViewHolder.tv_WaitTime.setText(firstWord + secondWord);
                                }

                            } else if (date1.compareTo(date2) < 0) {   // For Future
                                try {
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                    Date date = format.parse(searchdetailList.getAvail_date());
                                    String day = (String) DateFormat.format("dd", date);
                                    String monthString = (String) DateFormat.format("MMM", date);

                                    Typeface tyface1 = Typeface.createFromAsset(context.getAssets(),
                                            "fonts/Montserrat_Bold.otf");
                                    String firstWord = "Next Available Time ";
                                    String secondWord = "\n" + monthString + " " + day + ", " + searchdetailList.getServiceTime();
                                    myViewHolder.tv_WaitTime.setText(firstWord + secondWord);
                                    myViewHolder.btncheckin.setEnabled(false);
                                    myViewHolder.btncheckin.setBackgroundColor(Color.parseColor("#cfcfcf"));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                myViewHolder.tv_WaitTime.setEnabled(false);
                                myViewHolder.btncheckin.setEnabled(false);
                                myViewHolder.btncheckin.setBackgroundColor(Color.parseColor("#cfcfcf"));
                                myViewHolder.btncheckin.setTextColor(context.getResources().getColor(R.color.button_grey));
////                                 myViewHolder.btncheckin.setTextColor(context.getResources().getColor(R.color.button_grey));
                            }
                        } else { // Available Date is null
                            myViewHolder.btncheckin.setBackgroundColor(Color.parseColor("#cfcfcf"));
                            myViewHolder.btncheckin.setTextColor(context.getResources().getColor(R.color.button_grey));
                            myViewHolder.btncheckin.setEnabled(false);
                            myViewHolder.tv_WaitTime.setVisibility(View.GONE);
                            myViewHolder.tv_Futuredate.setVisibility(View.GONE);
                        }
                    } else {
                        myViewHolder.btncheckin.setBackgroundColor(Color.parseColor("#cfcfcf"));
                        myViewHolder.btncheckin.setTextColor(context.getResources().getColor(R.color.button_grey));
                        myViewHolder.btncheckin.setEnabled(false);
                        myViewHolder.tv_WaitTime.setVisibility(View.GONE);
                        myViewHolder.tv_Futuredate.setVisibility(View.GONE);
                    }
                } else { // No Waiting Time && Show/Hide Token
                    if (searchdetailList.getCalculationMode() != null) {
                        if (searchdetailList.getCalculationMode().equalsIgnoreCase("NoCalc") && searchdetailList.isShowToken()) {
                            myViewHolder.btncheckin.setText("GET TOKEN");
                            myViewHolder.tv_Futuredate.setText("Do you want to Get Token for another day?");
                            if (searchdetailList.getPersonAhead() != -1) {
                                Config.logV("personAheadtttt @@@@@@@@@@@6666@@@ ####" + searchdetailList.getPersonAhead());
                                if (searchdetailList.getPersonAhead() == 0) {
                                    myViewHolder.tv_WaitTime.setVisibility(View.VISIBLE);
                                    myViewHolder.tv_WaitTime.setText(" Be the first in line");
                                } else {
                                    myViewHolder.tv_WaitTime.setVisibility(View.VISIBLE);
                                    String firstWord = String.valueOf(searchdetailList.getPersonAhead());
                                    String secondWord = " People waiting in line";
                                    Typeface tyface1 = Typeface.createFromAsset(context.getAssets(),
                                            "fonts/Montserrat_Bold.otf");
                                    Spannable spannable = new SpannableString(firstWord + secondWord);
                                    spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    myViewHolder.tv_WaitTime.setText(spannable);
                                }
                            }
                        } else {
                            if (searchdetailList.getCalculationMode().equalsIgnoreCase("NoCalc") && !searchdetailList.isShowToken()) {
                                myViewHolder.tv_WaitTime.setVisibility(View.GONE);

                                if (termilogy.equals("order")) {
                                    myViewHolder.btncheckin.setText("ORDER");
                                    myViewHolder.tv_Futuredate.setText("Do you want to Order for another day?");
                                } else {
                                    myViewHolder.btncheckin.setText("CHECK-IN");
                                    myViewHolder.tv_Futuredate.setText("Do you want to Check-in for another day?");
                                }
                            } else {
                                if (termilogy.equals("order")) {
                                    myViewHolder.btncheckin.setText("ORDER");
                                    myViewHolder.tv_Futuredate.setText("Do you want to Order for another day?");
                                } else {
                                    myViewHolder.btncheckin.setText("CHECK-IN");
                                    myViewHolder.tv_Futuredate.setText("Do you want to Check-in for another day?");
                                }
                            }
                        }
                    } else {
                        myViewHolder.btncheckin.setBackgroundColor(Color.parseColor("#cfcfcf"));
                        myViewHolder.btncheckin.setTextColor(context.getResources().getColor(R.color.button_grey));
                        myViewHolder.btncheckin.setEnabled(false);
                        myViewHolder.tv_WaitTime.setVisibility(View.GONE);
                        myViewHolder.tv_Futuredate.setVisibility(View.GONE);
                    }
                }
            } else {
                myViewHolder.btncheckin.setBackgroundColor(Color.parseColor("#cfcfcf"));
                myViewHolder.btncheckin.setTextColor(context.getResources().getColor(R.color.button_grey));
                myViewHolder.btncheckin.setEnabled(false);
                myViewHolder.tv_WaitTime.setVisibility(View.GONE);
                myViewHolder.tv_Futuredate.setVisibility(View.GONE);
            }
        } else {

            myViewHolder.btncheckin.setBackgroundColor(Color.parseColor("#cfcfcf"));
            myViewHolder.btncheckin.setTextColor(context.getResources().getColor(R.color.button_grey));
            myViewHolder.btncheckin.setEnabled(false);
            myViewHolder.tv_WaitTime.setVisibility(View.GONE);
            myViewHolder.tv_Futuredate.setVisibility(View.GONE);

            if (searchdetailList.getMessage() != null) {
//                myViewHolder.tv_qmessage.setVisibility(View.VISIBLE);
//                myViewHolder.tv_qmessage.setText(searchdetailList.getMessage());
                myViewHolder.tv_WaitTime.setVisibility(View.GONE);
                myViewHolder.btncheckin.setBackgroundColor(Color.parseColor("#cfcfcf"));
                myViewHolder.btncheckin.setTextColor(context.getResources().getColor(R.color.button_grey));
                myViewHolder.btncheckin.setEnabled(false);
            } else {
//                myViewHolder.tv_qmessage.setVisibility(View.GONE);
            }


        }


        myViewHolder.btncheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iCheckIn = new Intent(v.getContext(), CheckIn.class);
                iCheckIn.putExtra("serviceId", Integer.parseInt(searchdetailList.getmLoc()));
                iCheckIn.putExtra("uniqueID", searchdetailList.getUniqueid());
                iCheckIn.putExtra("accountID", searchdetailList.getId());
                iCheckIn.putExtra("googlemap", searchdetailList.getLocation1());
                // iCheckIn.putExtra("waititme", myViewHolder.tv_WaitTime.getText().toString());
                iCheckIn.putExtra("from", "checkin");
                iCheckIn.putExtra("title", searchdetailList.getTitle());
                iCheckIn.putExtra("place", searchdetailList.getPlace1());
                Config.logV("sector%%%%%%-------------" + searchdetailList.getSectorname());
                iCheckIn.putExtra("sector", searchdetailList.getSectorname());
                iCheckIn.putExtra("subsector", searchdetailList.getSub_sector());
                context.startActivity(iCheckIn);
            }
        });
        myViewHolder.btnappointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iAppointment = new Intent(v.getContext(), Appointment.class);
                iAppointment.putExtra("serviceId", Integer.parseInt(searchdetailList.getmLoc()));
                iAppointment.putExtra("uniqueID", searchdetailList.getUniqueid());
                iAppointment.putExtra("accountID", searchdetailList.getId());
                iAppointment.putExtra("googlemap", searchdetailList.getLocation1());
                iAppointment.putExtra("from", "checkin");
                iAppointment.putExtra("title", searchdetailList.getTitle());
                iAppointment.putExtra("place", searchdetailList.getPlace1());
                iAppointment.putExtra("sector", searchdetailList.getSectorname());
                iAppointment.putExtra("subsector", searchdetailList.getSub_sector());
                context.startActivity(iAppointment);
            }
        });
        /////////////////////////////////////////////////////////
        if (searchdetailList.getSpecialization_displayname() != null) {
            final List<String> list_spec = searchdetailList.getSpecialization_displayname();

            if (list_spec.size() > 0) {
                if (list_spec.size() == 1) {

                    myViewHolder.L_specialization.setVisibility(View.VISIBLE);
                    myViewHolder.tv_spec1.setText(list_spec.get(0));
                    myViewHolder.tv_spec1.setTextSize(13);

                    myViewHolder.tv_spec1.setVisibility(View.VISIBLE);
                    myViewHolder.tv_spec2.setVisibility(View.GONE);
                    myViewHolder.tv_spec3.setVisibility(View.GONE);
                    myViewHolder.tv_spec22.setVisibility(View.GONE);
                } else {

                    myViewHolder.L_specialization.setVisibility(View.VISIBLE);
                    myViewHolder.tv_spec1.setText(list_spec.get(0) + " , ");
                    myViewHolder.tv_spec1.setTextSize(13);
                    myViewHolder.tv_spec1.setVisibility(View.VISIBLE);
                    myViewHolder.tv_spec1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    // myViewHolder.tv_spec1.setEllipsize(TextUtils.TruncateAt.END);
                    myViewHolder.tv_spec1.setMaxLines(1);

                    if (list_spec.size() > 2) {

                        myViewHolder.tv_spec1.setMaxEms(5);
                        myViewHolder.tv_spec1.setEllipsize(TextUtils.TruncateAt.END);

                        myViewHolder.tv_spec22.setText(list_spec.get(1) + " , ");
                        myViewHolder.tv_spec22.setTextSize(13);
                        myViewHolder.tv_spec22.setVisibility(View.VISIBLE);
                        myViewHolder.tv_spec22.setEllipsize(TextUtils.TruncateAt.END);
                        myViewHolder.tv_spec22.setMaxLines(1);
                        // myViewHolder.tv_spec22.setWidth(dpToPx(120));
                        myViewHolder.tv_spec22.setMaxEms(8);

                        myViewHolder.tv_spec2.setText(list_spec.get(2) + " , ");
                        myViewHolder.tv_spec2.setTextSize(13);
                        myViewHolder.tv_spec2.setVisibility(View.VISIBLE);
                        myViewHolder.tv_spec2.setEllipsize(TextUtils.TruncateAt.END);
                        myViewHolder.tv_spec2.setMaxLines(1);
                        //  myViewHolder.tv_spec2.setWidth(dpToPx(120));
                        myViewHolder.tv_spec2.setMaxEms(8);


                        myViewHolder.tv_spec3.setText(" ...");
                        myViewHolder.tv_spec3.setTextSize(13);
                        myViewHolder.tv_spec3.setVisibility(View.VISIBLE);
                    } else {
                        myViewHolder.tv_spec22.setText(list_spec.get(1));
                        myViewHolder.tv_spec22.setTextSize(13);
                        myViewHolder.tv_spec22.setVisibility(View.VISIBLE);
                        //    myViewHolder.tv_spec22.setEllipsize(TextUtils.TruncateAt.END);
                        myViewHolder.tv_spec22.setMaxLines(1);
                        // myViewHolder.tv_spec22.setMaxEms(8);

                        myViewHolder.tv_spec2.setVisibility(View.GONE);
                        myViewHolder.tv_spec3.setVisibility(View.GONE);

                    }

                }

                Config.logV("SpEc Open----------" + searchdetailList.isIs_SpecOpen() + "Prov" + searchdetailList.getTitle());


                myViewHolder.tv_spec3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAdapterCallback.onMethodCallback(searchdetailList.getUniqueid(),searchdetailList.getClaimable());
                    }
                });
            }
        } else {
            myViewHolder.tv_spec1.setVisibility(View.GONE);
            myViewHolder.tv_spec2.setVisibility(View.GONE);
            myViewHolder.tv_spec3.setVisibility(View.GONE);
            myViewHolder.tv_spec22.setVisibility(View.GONE);
            myViewHolder.L_specialization.setVisibility(View.GONE);
        }
        //  Picasso.with(context).load(searchdetailList.getLogo()).fit().into(myViewHolder.profile);
        Config.logV("LOGO @@@@" + searchdetailList.getLogo() + searchdetailList.getTitle());

        Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();
            }
        });
        builder.build().load(searchdetailList.getLogo()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(myViewHolder.profile);
        if (searchdetailList.getGallery_thumb_nails() != null) {
            if (searchdetailList.getGallery_thumb_nails().size() > 0) {
                myViewHolder.mImageViewText.setVisibility(View.VISIBLE);
                myViewHolder.mImageViewText.setText("+" + searchdetailList.getGallery_thumb_nails().size());
            } else {
                myViewHolder.mImageViewText.setVisibility(View.GONE);
            }
        } else {
            myViewHolder.mImageViewText.setVisibility(View.GONE);
        }


        if (searchdetailList.getServices() != null) {


            if (searchdetailList.getServices().size() > 0) {
                myViewHolder.L_services.removeAllViews();
                myViewHolder.L_services.setVisibility(View.VISIBLE);

                int size = 0;
                if (searchdetailList.getServices().size() == 1) {
                    size = 1;
                } else {
                    if (searchdetailList.getServices().size() == 2)
                        size = 2;
                    else
                        size = 3;
                }
                for (int i = 0; i < size; i++) {
                    TextView dynaText = new TextView(context);
                    Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                            "fonts/Montserrat_Regular.otf");
                    dynaText.setTypeface(tyface);
                    dynaText.setText(searchdetailList.getServices().get(i).toString());
                    dynaText.setTextSize(13);
                    dynaText.setPadding(5, 0, 5, 0);
                    dynaText.setTextColor(context.getResources().getColor(R.color.title_consu));
                    // dynaText.setBackground(context.getResources().getDrawable(R.drawable.input_border_rounded_blue_bg));

                    dynaText.setPaintFlags(dynaText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                    dynaText.setMaxLines(1);
                    if (size > 2) {
                        dynaText.setEllipsize(TextUtils.TruncateAt.END);
                        dynaText.setMaxEms(10);
                    }
                    final int finalI = i;
                    dynaText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ApiService(searchdetailList.getUniqueid(), searchdetailList.getServices().get(finalI).toString(), searchdetailList.getTitle());
                        }
                    });
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 20, 0);

                    dynaText.setLayoutParams(params);
                    myViewHolder.L_services.addView(dynaText);

                }

                if (size > 3) {

                    TextView dynaText = new TextView(context);
                    dynaText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            mAdapterCallback.onMethodServiceCallback(searchdetailList.getServices(), searchdetailList.getTitle(), searchdetailList.getUniqueid());
                        }
                    });
                    dynaText.setGravity(Gravity.CENTER);
                    dynaText.setTextColor(context.getResources().getColor(R.color.title_consu));
                    dynaText.setText(" ... ");
                    // dynaText.setBackground(context.getResources().getDrawable(R.drawable.icon_arrowright_blue));
                    myViewHolder.L_services.addView(dynaText);
                }
            } else {
                myViewHolder.L_services.setVisibility(View.GONE);

            }

        } else {
            myViewHolder.L_services.setVisibility(View.GONE);

        }


        if (searchdetailList.getRating() != null) {
            myViewHolder.rating.setRating(Float.valueOf(searchdetailList.getRating()));
        }


    }


    @Override
    public int getItemCount() {
        if (searchList != null) {
            return searchList.size();
        }
        return 0;

    }


    private void ApiService(String uniqueID, final String serviceName, final String title) {

        ApiInterface apiService =
                ApiClient.getClientS3Cloud(context).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(context, context.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));

        Call<ArrayList<SearchService>> call = apiService.getService(Integer.parseInt(uniqueID), sdf.format(currentTime));

        call.enqueue(new Callback<ArrayList<SearchService>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchService>> call, Response<ArrayList<SearchService>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(activity, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        SearchService service1 = null;
                        ArrayList<SearchService> service = new ArrayList<>();
                        service = response.body();
                        for (int i = 0; i < service.size(); i++) {
                            Config.logV("Response--serviceid-------------------------" + serviceName);

                            if (service.get(i).getName().toLowerCase().equalsIgnoreCase(serviceName.toLowerCase())) {
                                Intent iService = new Intent(context, SearchServiceActivity.class);
                                iService.putExtra("name", service.get(i).getName());
                                iService.putExtra("duration", service.get(i).getServiceDuration());
                                iService.putExtra("price", service.get(i).getTotalAmount());
                                iService.putExtra("desc", service.get(i).getDescription());
                                iService.putExtra("servicegallery", service.get(i).getServicegallery());
                                iService.putExtra("taxable", service.get(i).isTaxable());
                                iService.putExtra("title", title);
                                iService.putExtra("isPrePayment", service.get(i).isPrePayment());
                                iService.putExtra("MinPrePaymentAmount", service.get(i).getMinPrePaymentAmount());
                                context.startActivity(iService);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<SearchService>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(activity, mDialog);
            }
        });
    }


}
