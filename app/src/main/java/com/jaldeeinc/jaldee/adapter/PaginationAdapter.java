package com.jaldeeinc.jaldee.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.JsonArray;
import com.jaldeeinc.jaldee.activities.Appointment;
import com.jaldeeinc.jaldee.activities.Donation;
import com.jaldeeinc.jaldee.activities.SearchServiceActivity;
import com.jaldeeinc.jaldee.activities.SwipeGalleryImage;
import com.jaldeeinc.jaldee.callback.AdapterCallback;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.CheckIn;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CircleTransform;
import com.jaldeeinc.jaldee.custom.CustomTypefaceSpan;
import com.jaldeeinc.jaldee.model.SearchListModel;
import com.jaldeeinc.jaldee.model.WorkingModel;
import com.jaldeeinc.jaldee.response.QueueList;
import com.jaldeeinc.jaldee.response.QueueTimeSlotModel;
import com.jaldeeinc.jaldee.response.ScheduleList;
import com.jaldeeinc.jaldee.response.SearchService;

import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    static ArrayList<QueueTimeSlotModel> mQueueTimeSlotList = new ArrayList<>();
    static SimpleDateFormat inputParser = new SimpleDateFormat("HH:mm", Locale.US);
    private static Context context;
    private static Date dateCompareOne;
    ArrayList<WorkingModel> workingModelArrayList = new ArrayList<>();
    Fragment mFragment;
    String workingHrs = "";
    String termilogy;
    String countTerminology;
    SearchView mSearchView;
    Activity activity;
    private List<SearchListModel> searchResults;
    private boolean isLoadingAdded = false;
    private AdapterCallback mAdapterCallback;
    ArrayList<SearchViewDetail> mSpecializationList;
    SearchViewDetail mBusinessDataList;
    ArrayList<SearchViewDetail> mSearchGallery;
    String uniqueID;
    List<QueueList> mQueueList;
    ArrayList serviceNames = new ArrayList();
    ArrayList serviceNamesAppointments = new ArrayList();
    ArrayList serviceNamesDonations = new ArrayList();



    public PaginationAdapter(Activity activity, SearchView searchview, Context context, Fragment mFragment, AdapterCallback callback, String uniqueID, List<QueueList> mQueueList) {
        this.context = context;
        searchResults = new ArrayList<>();
        this.mFragment = mFragment;
        mSearchView = searchview;
        this.mAdapterCallback = callback;
        this.activity = activity;
        this.uniqueID = uniqueID;
        this.mQueueList = mQueueList;
    }

    private static Date parseDate(String date) {
        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    private static String getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMMM").format(cal.getTime());
        return monthName;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.searchlist_row, parent, false);
        viewHolder = new MyViewHolder(v1);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final SearchListModel searchdetailList = searchResults.get(position);




        switch (getItemViewType(position)) {
            case ITEM:
                final MyViewHolder myViewHolder = (MyViewHolder) holder;

                Config.logV("VERified-----" + searchdetailList.getYnw_verified() + "name" + searchdetailList.getTitle());
                Config.logV("VERified-@@@@----" + searchdetailList.getYnw_verified_level() + "name" + searchdetailList.getTitle());
                if (searchdetailList.getYnw_verified_level() != null) {
                    if (searchdetailList.getYnw_verified() == 1) {
                        if (searchdetailList.getYnw_verified_level().equalsIgnoreCase("2")) {
                            myViewHolder.ic_jaldeeverifiedIcon.setVisibility(View.VISIBLE);
                            myViewHolder.ic_jaldeeverifiedIcon.setImageResource(R.drawable.jaldee_basic);
                        }
                        if (searchdetailList.getYnw_verified_level().equalsIgnoreCase("3")) {
                            myViewHolder.ic_jaldeeverifiedIcon.setVisibility(View.VISIBLE);
                            myViewHolder.ic_jaldeeverifiedIcon.setImageResource(R.drawable.jaldee_basicplus);
                        }
                        if (searchdetailList.getYnw_verified_level().equalsIgnoreCase("4")) {
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
                        mAdapterCallback.onMethodJaldeeLogo(searchdetailList.getYnw_verified_level(), searchdetailList.getTitle());
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


                if(searchdetailList.getJdn()!=null){

                    if (searchdetailList.getJdn().equals("1")) {
                       myViewHolder.jdn_icon.setVisibility(View.VISIBLE);
                    }else{
                        myViewHolder.jdn_icon.setVisibility(View.GONE);
                    }
                }else{
                    myViewHolder.jdn_icon.setVisibility(View.GONE);
                }


                myViewHolder.jdn_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAdapterCallback.onMethodJdn(searchdetailList.getUniqueid());
                    }
                });


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
                        mAdapterCallback.onMethodFirstCoupn(searchdetailList.getUniqueid());
                    }
                });
                final TextView dynaText2 = new TextView(context);
                Typeface tyface_2 = Typeface.createFromAsset(context.getAssets(),
                        "fonts/Montserrat_Regular.otf");
                dynaText2.setTypeface(tyface_2);
                dynaText2.setText("Coupons");
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
                        mAdapterCallback.onMethodCoupn(searchdetailList.getUniqueid());
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
                        mAdapterCallback.onMethodMessage(searchdetailList.getTitle(), searchdetailList.getId(), "search");
                    }
                });
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
                                        Log.i("terminos", array_json);
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

                                mAdapterCallback.onMethodWorkingCallback(workingModelArrayList, searchdetailList.getTitle(), searchdetailList.getUniqueid());
                                //   Config.logV("Working-----------" + workingModelArrayList.size() + "tt" + searchdetailList.getTitle());
                            }
                        }
                    });
                }
                if (searchdetailList.getParking_type_location1() != null) {
                    if (searchdetailList.getParking_location1().equalsIgnoreCase("1")) {

                            TextView dynaText = new TextView(context);
                            Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                                    "fonts/Montserrat_Regular.otf");
                        if (searchdetailList.getParking_type_location1().equalsIgnoreCase("none")) {
                            dynaText.setVisibility(View.GONE);
                        } else {
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
                        dynaText.setMaxLines(2);
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

                if (searchdetailList.getHosemergencyservices_location1() != null) {
                    if (searchdetailList.getHosemergencyservices_location1().equalsIgnoreCase("1")) {
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
                myViewHolder.btnappointments.setTypeface(tyface_confm);
                myViewHolder.btndonations.setTypeface(tyface_confm);
                // myViewHolder.tv_Open.setTypeface(tyface_confm);

                myViewHolder.tv_location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAdapterCallback.onMethodOpenMap(searchdetailList.getLocation1());
                    }
                });
                myViewHolder.layout_row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSearchView.setQuery("", false);
                        mAdapterCallback.onMethodCallback(searchdetailList.getUniqueid(), searchdetailList.getClaimable());
                    }
                });

                if (searchdetailList.getBranchSpCount() > 0) {
                    if (searchdetailList.getBranchSpCount() > 1) {
                        myViewHolder.tv_count.setText(" " + countTerminology + "s" + " " + ":" + " " + searchdetailList.getBranchSpCount());
                        myViewHolder.tv_count.setVisibility(View.VISIBLE);
                    } else {
                        myViewHolder.tv_count.setText(searchdetailList.getBranchSpCount() + " " + countTerminology);
                        myViewHolder.tv_count.setVisibility(View.VISIBLE);
                    }

                } else {
                    myViewHolder.tv_count.setVisibility(View.GONE);
                }
                if (searchdetailList.getAccountType() != null) {
                    if (searchdetailList.getAccountType().equals("1")) {
                        myViewHolder.tv_branch_name.setText(searchdetailList.getBranch_name());
                        myViewHolder.tv_branch_name.setVisibility(View.VISIBLE);
                    } else if ((searchdetailList.getAccountType().equals("0"))) {
                        myViewHolder.tv_count.setVisibility(View.VISIBLE);
                    } else {
                        myViewHolder.tv_branch_name.setVisibility(View.GONE);
                    }
                } else {
                    myViewHolder.tv_branch_name.setVisibility(View.GONE);
                }

                if(searchdetailList.getClaimable()!=null){
                    if (searchdetailList.getClaimable().equals("1")) {
                        myViewHolder.tv_claimable.setVisibility(View.VISIBLE);
                        // myViewHolder.tv_useWeb.setVisibility(View.VISIBLE);
                        myViewHolder.L_layout_type.setVisibility(View.GONE);
                        myViewHolder.tv_qmessage.setVisibility(View.GONE);

                    } else {
                        myViewHolder.tv_claimable.setVisibility(View.INVISIBLE);
                        // myViewHolder.tv_useWeb.setVisibility(View.INVISIBLE);
                        myViewHolder.L_layout_type.setVisibility(View.VISIBLE);
                        myViewHolder.tv_qmessage.setVisibility(View.VISIBLE);
                    }
                }

                myViewHolder.tv_claimable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });


                if(searchdetailList.getClaimable()!=null){

                    if (searchdetailList.getClaimable().equals("0")) {
                        myViewHolder.vsep.setVisibility(View.GONE);
                    } else {
                        myViewHolder.vsep.setVisibility(View.VISIBLE);
                    }

                }


                if (searchdetailList.getQualification() != null) {
                    myViewHolder.tv_name.setText(searchdetailList.getTitle());
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
                    if (distance >= 1) {
                        myViewHolder.tv_location.setText(searchdetailList.getPlace1() + " ( " + String.format("%.2f", distance) + " km )");
                    } else {
                        myViewHolder.tv_location.setText(searchdetailList.getPlace1() + " (<1 km) ");
                    }
                } else {
                    myViewHolder.tv_location.setVisibility(View.GONE);
                }





//////////////////////////////////////////////////////////////////////////////////////////

                if(searchdetailList.getId()!=null && searchdetailList.getQId()!=null){
                    if (searchdetailList.getId().equalsIgnoreCase(searchdetailList.getQId())) {
                        Date c = Calendar.getInstance().getTime();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        String formattedDate = df.format(c);
                        System.out.println("Current time => " + formattedDate);
                        Date date1 = null, date2 = null;
                        try {
                            date1 = df.parse(formattedDate);
                            if (searchdetailList.getAvail_date() != null)
                                date2 = df.parse(searchdetailList.getAvail_date());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (searchdetailList.getMessage() != null && searchdetailList.getClaimable() == null) {
                            myViewHolder.tv_qmessage.setVisibility(View.VISIBLE);
                            myViewHolder.tv_qmessage.setText(searchdetailList.getMessage());
                        } else {
                            myViewHolder.tv_qmessage.setVisibility(View.GONE);
                        }


                        if(searchdetailList.getMessage() != null && searchdetailList.getClaimable()!=null){
                            if (searchdetailList.getClaimable().equals("0")) {
                                myViewHolder.tv_qmessage.setVisibility(View.VISIBLE);
                                myViewHolder.tv_qmessage.setText(searchdetailList.getMessage());
                                myViewHolder.tv_qmessage.setTextColor(context.getResources().getColor(R.color.red));
                                myViewHolder.tv_WaitTime.setVisibility(View.GONE);
                                myViewHolder.tv_peopleahead.setVisibility(View.GONE);
                                disableCheckinButton(myViewHolder,searchdetailList);

                            }
                        }


                        if (searchdetailList.getFuture_checkins() != null && searchdetailList.getFuture_checkins().equalsIgnoreCase("1")) {
                            if ( searchdetailList.isShowToken()) {
                                myViewHolder.tv_Futuredate.setText("Do you want to Get Token for another day?");
                            } else {
                                myViewHolder.tv_Futuredate.setText("Do you want to" + " " + termilogy + " for another day?");
                            }
                        }


                        if(searchdetailList.getOnline_profile()!=null){
                            if(searchdetailList.getOnline_profile().equals("1") && searchdetailList.isWaitlistEnabled()){
                                myViewHolder.L_checkin.setVisibility(View.VISIBLE);
                                myViewHolder.L_services.setVisibility(View.VISIBLE);

                                if(searchdetailList.getAvail_date()!=null){
                                    if(searchdetailList.isOnlineCheckIn() && searchdetailList.isAvailableToday() && formattedDate.equalsIgnoreCase(searchdetailList.getAvail_date())){
                                        myViewHolder.btncheckin.setVisibility(View.VISIBLE);
                                        myViewHolder.L_services.setVisibility(View.VISIBLE);
                                    }else{
                                        myViewHolder.btncheckin.setVisibility(View.GONE);
                                        myViewHolder.L_services.setVisibility(View.GONE);
                                    }
                                }
                            }else{
                                myViewHolder.L_checkin.setVisibility(View.GONE);
                                myViewHolder.L_services.setVisibility(View.GONE);
                            }
                        }else{
                            myViewHolder.L_checkin.setVisibility(View.GONE);
                            myViewHolder.L_services.setVisibility(View.GONE);
                        }




                        if(searchdetailList.getAvail_date()!=null){
                            if (formattedDate.equalsIgnoreCase(searchdetailList.getAvail_date())) { // if Today
                                myViewHolder.tv_WaitTime.setVisibility(View.INVISIBLE);
                                myViewHolder.tv_peopleahead.setVisibility(View.INVISIBLE);

                                if (searchdetailList.isOnlineCheckIn()) {
//                                    enableCheckinButton(myViewHolder);
                                } else {
                                    disableCheckinButton(myViewHolder, searchdetailList);
                                }
                                if (searchdetailList.getShow_waiting_time() != null) { // ML/Fixed
                                    if (searchdetailList.isShowToken()) {
                                        myViewHolder.btncheckin.setText("GET TOKEN");
                                    }else{
                                        myViewHolder.btncheckin.setText(termilogy.toUpperCase());
                                    }

                                    if (searchdetailList.getShow_waiting_time().equalsIgnoreCase("1")) {
                                        setCurrentDateCheckin(searchdetailList, myViewHolder);
                                    } else {
//                                        enableCheckinButton(myViewHolder);

                                        if (searchdetailList.isShowToken()) {
                                            myViewHolder.btncheckin.setText("GET TOKEN");
                                            myViewHolder.tv_peopleahead.setVisibility(View.VISIBLE);
                                            noCalcShowToken(searchdetailList, myViewHolder);
                                        } else {
                                            myViewHolder.tv_WaitTime.setVisibility(View.GONE);
                                            myViewHolder.tv_peopleahead.setVisibility(View.GONE);
                                        }


                                    }
                                }
                            } else if (date2 != null && date1.compareTo(date2) < 0) {   // For Future
                                disableCheckinButton(myViewHolder,searchdetailList);
                                // ML/Fixed
                                if (searchdetailList.getShow_waiting_time() != null) {
                                    myViewHolder.btncheckin.setText(termilogy.toUpperCase());
                                    // For ML/Fixed
                                    if (searchdetailList.getShow_waiting_time().equalsIgnoreCase("1")) {
                                    } else {

                                        if (searchdetailList.isShowToken()) {
                                            myViewHolder.btncheckin.setText("GET TOKEN");
                                            myViewHolder.tv_peopleahead.setVisibility(View.VISIBLE);
                                            noCalcShowToken(searchdetailList, myViewHolder);
                                        } else {
                                            myViewHolder.tv_WaitTime.setVisibility(View.GONE);
                                            myViewHolder.tv_peopleahead.setVisibility(View.GONE);
                                        }

                                    }
                                    setFutureDateCheckin(searchdetailList, myViewHolder,position);
                                }
                            } else {
                                disableCheckinButton(myViewHolder,searchdetailList);
                                myViewHolder.tv_WaitTime.setVisibility(View.GONE);
                                myViewHolder.tv_peopleahead.setVisibility(View.GONE);
                                myViewHolder.tv_Futuredate.setVisibility(View.GONE);
                            }
                        }

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
                        iCheckIn.putExtra("terminology", termilogy);
                        iCheckIn.putExtra("isshowtoken", searchdetailList.isShowToken());
                        iCheckIn.putExtra("getAvail_date", searchdetailList.getAvail_date());
                        context.startActivity(iCheckIn);
                    }
                });


                myViewHolder.btnappointments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent iAppoinment = new Intent(v.getContext(), Appointment.class);
                        iAppoinment.putExtra("serviceId", Integer.parseInt(searchdetailList.getaLoc()));
                        iAppoinment.putExtra("uniqueID", searchdetailList.getUniqueid());
                        iAppoinment.putExtra("accountID", searchdetailList.getId());
                        iAppoinment.putExtra("googlemap", searchdetailList.getLocation1());
                        iAppoinment.putExtra("from", "checkin");
                        iAppoinment.putExtra("title", searchdetailList.getTitle());
                        iAppoinment.putExtra("place", searchdetailList.getPlace1());
                        iAppoinment.putExtra("sector", searchdetailList.getSectorname());
                        iAppoinment.putExtra("subsector", searchdetailList.getSub_sector());
                        iAppoinment.putExtra("terminology", termilogy);
                        iAppoinment.putExtra("isshowtoken", searchdetailList.isShowToken());
                        iAppoinment.putExtra("getAvail_date", searchdetailList.getAvail_date());
                        context.startActivity(iAppoinment);
                    }
                });

                myViewHolder.btndonations.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent iAppoinment = new Intent(v.getContext(), Donation.class);
                        iAppoinment.putExtra("serviceId", Integer.parseInt(searchdetailList.getaLoc()));
                        iAppoinment.putExtra("uniqueID", searchdetailList.getUniqueid());
                        iAppoinment.putExtra("accountID", searchdetailList.getId());
                        iAppoinment.putExtra("googlemap", searchdetailList.getLocation1());
                        iAppoinment.putExtra("from", "checkin");
                        iAppoinment.putExtra("title", searchdetailList.getTitle());
                        iAppoinment.putExtra("place", searchdetailList.getPlace1());
                        iAppoinment.putExtra("sector", searchdetailList.getSectorname());
                        iAppoinment.putExtra("subsector", searchdetailList.getSub_sector());
                        iAppoinment.putExtra("terminology", termilogy);
                        iAppoinment.putExtra("isshowtoken", searchdetailList.isShowToken());
                        iAppoinment.putExtra("getAvail_date", searchdetailList.getAvail_date());
                        context.startActivity(iAppoinment);
                    }
                });
                /////////////////////////////////////////////////////////
                setSpecializations(myViewHolder, searchdetailList);
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
                myViewHolder.profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Config.logV("Unique Id List", searchdetailList.getUniqueid());
                        ApiSearchGallery(searchdetailList.getUniqueid(), searchdetailList);

                    }


                });
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

                if (searchdetailList.getDepartments() != null) {
                    if (searchdetailList.getDepartments().size() > 0) {
                        if (searchdetailList.getDepartments().size() == 1) {
                            myViewHolder.L_departments.setVisibility(View.VISIBLE);
                            myViewHolder.tv_dep1.setVisibility(View.VISIBLE);
                            myViewHolder.tv_dep1.setText(searchdetailList.getDepartments().get(0).toString());
//                            myViewHolder.tv_dep1.setBackground(context.getResources().getDrawable(R.color.department_search_results));
//                            Typeface tyface = Typeface.createFromAsset(context.getAssets(),
//                                   "fonts/Montserrat_Regular.otf");
//                            myViewHolder.tv_dep1.setTypeface(tyface);
                            myViewHolder.tv_dep1.setTextSize(13);
                            //  myViewHolder.tv_dep1.setTextColor(context.getResources().getColor(R.color.active_text));
                            myViewHolder.tv_dep2.setVisibility(View.GONE);
                            myViewHolder.tv_dep_more.setVisibility(View.GONE);
                            myViewHolder.tv_dep22.setVisibility(View.GONE);

                        } else {
                            myViewHolder.L_departments.setVisibility(View.VISIBLE);
                            myViewHolder.tv_dep1.setText(searchdetailList.getDepartments().get(0) + "   ");
                            myViewHolder.tv_dep1.setTextSize(13);
                            myViewHolder.tv_dep1.setVisibility(View.VISIBLE);
                            myViewHolder.tv_dep1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            //    myViewHolder.tv_spec1.setEllipsize(TextUtils.TruncateAt.END);
                            myViewHolder.tv_dep1.setMaxLines(1);
                            // myViewHolder.tv_dep1.setBackground(context.getResources().getDrawable(R.color.department_search_results));

                            if (searchdetailList.getDepartments().size() > 2) {
                                myViewHolder.tv_dep1.setMaxEms(5);
                                myViewHolder.tv_dep1.setEllipsize(TextUtils.TruncateAt.END);
                                myViewHolder.tv_dep22.setText(searchdetailList.getDepartments().get(1) + "  ");
                                // myViewHolder.tv_dep22.setBackground(context.getResources().getDrawable(R.color.department_search_results));
                                myViewHolder.tv_dep22.setTextSize(13);
                                myViewHolder.tv_dep22.setVisibility(View.VISIBLE);
                                myViewHolder.tv_dep22.setEllipsize(TextUtils.TruncateAt.END);
                                myViewHolder.tv_dep22.setMaxLines(1);
                                // myViewHolder.tv_dep22.setWidth(dpToPx(120));
                                myViewHolder.tv_dep22.setMaxEms(8);
                                myViewHolder.tv_dep2.setText(searchdetailList.getDepartments().get(2) + "  ");
                                //   myViewHolder.tv_dep2.setBackground(context.getResources().getDrawable(R.color.department_search_results));
                                myViewHolder.tv_dep2.setTextSize(13);
                                myViewHolder.tv_dep2.setVisibility(View.VISIBLE);
                                myViewHolder.tv_dep2.setEllipsize(TextUtils.TruncateAt.END);
                                myViewHolder.tv_dep2.setMaxLines(1);
                                //  myViewHolder.tv_dep2.setWidth(dpToPx(120));
                                myViewHolder.tv_dep2.setMaxEms(8);
                                myViewHolder.tv_dep_more.setText(" > ");
                                //   myViewHolder.tv_dep_more.setBackground(context.getResources().getDrawable(R.color.department_search_results));
                                myViewHolder.tv_dep_more.setTextSize(20);
                                myViewHolder.tv_dep_more.setVisibility(View.VISIBLE);

                            } else {
                                myViewHolder.tv_dep22.setText(searchdetailList.getDepartments().get(1).toString() + " ");
                                // myViewHolder.tv_dep22.setBackground(context.getResources().getDrawable(R.color.department_search_results));
                                myViewHolder.tv_dep22.setTextSize(13);
                                myViewHolder.tv_dep22.setVisibility(View.VISIBLE);
                                //    myViewHolder.tv_dep22.setEllipsize(TextUtils.TruncateAt.END);
                                myViewHolder.tv_dep22.setMaxLines(1);
                                //     myViewHolder.tv_dep22.setMaxEms(8);

                                myViewHolder.tv_dep2.setVisibility(View.GONE);
                                myViewHolder.tv_dep_more.setVisibility(View.GONE);

                            }
                            myViewHolder.tv_dep_more.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    mAdapterCallback.onMethodDepartmentList(searchdetailList.getDepartments(), searchdetailList.getTitle());

                                }
                            });

                        }
                    }
                    else {
                        searchdetailList.getDepartments().clear();
                        myViewHolder.L_departments.setVisibility(View.GONE);
                    }
                }
                else {
                  //  myViewHolder.L_departments.removeAllViews();
                    myViewHolder.L_departments.setVisibility(View.GONE);
                }


                serviceNames.clear();
                if (searchdetailList.getServices() != null) {
                    try {
                        String serviceName = searchdetailList.getServices().toString();
                        try {
                            JSONArray jsonArray = new JSONArray(serviceName);
                            String jsonArry = jsonArray.getString(0);
                            JSONArray jsonArray1 = new JSONArray(jsonArry);
                            for(int i =0;i<jsonArray1.length();i++){
                                JSONObject jsonObject = jsonArray1.getJSONObject(i);
                                String name = jsonObject.optString("name");
                                serviceNames.add(i,name);
                                Log.i("sar",serviceNames.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (serviceNames.size() > 0) {
                        myViewHolder.L_services.removeAllViews();
                        int size = 0;
                        if (serviceNames.size() == 1) {
                            size = 1;
                        } else {
                            if (serviceNames.size() == 2)
                                size = 2;
                            else
                                size = 3;
                        }
                        for (int i = 0; i < size; i++) {
                            TextView dynaText = new TextView(context);
                            Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                                    "fonts/Montserrat_Regular.otf");
                            dynaText.setTypeface(tyface);
                            dynaText.setText(serviceNames.get(i).toString());
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
                                    ApiService(searchdetailList.getUniqueid(), serviceNames.get(finalI).toString(), searchdetailList.getTitle());
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
                                    mAdapterCallback.onMethodServiceCallback(serviceNames, searchdetailList.getTitle(), searchdetailList.getUniqueid());
                                }
                            });
                            dynaText.setGravity(Gravity.CENTER);
                            dynaText.setTextColor(context.getResources().getColor(R.color.title_consu));
                            dynaText.setText(" ... ");
                            myViewHolder.L_services.addView(dynaText);
                        }
                    }

                }



                serviceNamesAppointments.clear();
                if (searchdetailList.getAppt_services() != null) {
                    try {
                        String serviceName = searchdetailList.getAppt_services().toString();
                        try {
                            JSONArray jsonArray = new JSONArray(serviceName);
                            String jsonArry = jsonArray.getString(0);
                            JSONArray jsonArray1 = new JSONArray(jsonArry);
                            for(int i =0;i<jsonArray1.length();i++){
                                JSONObject jsonObject = jsonArray1.getJSONObject(i);
                                String name = jsonObject.optString("name");
                                serviceNamesAppointments.add(i,name);
                                Log.i("sar",serviceNamesAppointments.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (serviceNamesAppointments.size() > 0) {
                        myViewHolder.L_appointments.removeAllViews();
                        int size = 0;
                        if (serviceNamesAppointments.size() == 1) {
                            size = 1;
                        } else {
                            if (serviceNamesAppointments.size() == 2)
                                size = 2;
                            else
                                size = 3;
                        }
                        for (int i = 0; i < size; i++) {
                            TextView dynaText = new TextView(context);
                            Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                                    "fonts/Montserrat_Regular.otf");
                            dynaText.setTypeface(tyface);
                            dynaText.setText(serviceNamesAppointments.get(i).toString());
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
                                    ApiService(searchdetailList.getUniqueid(), serviceNamesAppointments.get(finalI).toString(), searchdetailList.getTitle());
                                }
                            });
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(0, 0, 20, 0);

                            dynaText.setLayoutParams(params);
                            myViewHolder.L_appointments.addView(dynaText);
                        }
                        if (size > 3) {
                            TextView dynaText = new TextView(context);
                            dynaText.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mAdapterCallback.onMethodServiceCallback(serviceNamesAppointments, searchdetailList.getTitle(), searchdetailList.getUniqueid());
                                }
                            });
                            dynaText.setGravity(Gravity.CENTER);
                            dynaText.setTextColor(context.getResources().getColor(R.color.title_consu));
                            dynaText.setText(" ... ");
                            myViewHolder.L_appointments.addView(dynaText);
                        }
                    }

                }


                if (searchdetailList.getOnline_profile() != null) {
                        if (searchdetailList.isCheckinAllowed() && searchdetailList.getOnline_profile().equals("1")) {
                            myViewHolder.L_appoinment.setVisibility(View.VISIBLE);
                            myViewHolder.L_appointments.setVisibility(View.VISIBLE);
                        } else {
                            myViewHolder.L_appoinment.setVisibility(View.GONE);
                            myViewHolder.L_appointments.setVisibility(View.GONE);
                        }

                } else {
                    myViewHolder.L_appoinment.setVisibility(View.GONE);
                    myViewHolder.L_appointments.setVisibility(View.GONE);
                }


                if (searchdetailList.getDonation_services() != null) {
                    try {
                        String serviceName = searchdetailList.getDonation_services().toString();
                        try {
                            JSONArray jsonArray = new JSONArray(serviceName);
                            String jsonArry = jsonArray.getString(0);
                            JSONArray jsonArray1 = new JSONArray(jsonArry);
                            for(int i =0;i<jsonArray1.length();i++){
                                JSONObject jsonObject = jsonArray1.getJSONObject(i);
                                String name = jsonObject.optString("name");
                                serviceNamesDonations.add(i,name);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (serviceNamesDonations.size() > 0) {
                        myViewHolder.L_donations.removeAllViews();
                        int size = 0;
                        if (serviceNamesDonations.size() == 1) {
                            size = 1;
                        } else {
                            if (serviceNamesDonations.size() == 2)
                                size = 2;
                            else
                                size = 3;
                        }
                        for (int i = 0; i < size; i++) {
                            TextView dynaText = new TextView(context);
                            Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                                    "fonts/Montserrat_Regular.otf");
                            dynaText.setTypeface(tyface);
                            dynaText.setText(serviceNamesDonations.get(i).toString());
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
                                    ApiService(searchdetailList.getUniqueid(), serviceNamesDonations.get(finalI).toString(), searchdetailList.getTitle());
                                }
                            });
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(0, 0, 20, 0);

                            dynaText.setLayoutParams(params);
                            myViewHolder.L_donations.addView(dynaText);
                        }
                        if (size > 3) {
                            TextView dynaText = new TextView(context);
                            dynaText.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mAdapterCallback.onMethodServiceCallback(serviceNamesDonations, searchdetailList.getTitle(), searchdetailList.getUniqueid());
                                }
                            });
                            dynaText.setGravity(Gravity.CENTER);
                            dynaText.setTextColor(context.getResources().getColor(R.color.title_consu));
                            dynaText.setText(" ... ");
                            myViewHolder.L_donations.addView(dynaText);
                        }
                    }

                }
                if (searchdetailList.getOnline_profile() != null && searchdetailList.getDonation_status() != null) {
                    if (searchdetailList.getDonation_status().equals("1") && searchdetailList.getOnline_profile().equals("1")) {
                        myViewHolder.L_donation.setVisibility(View.VISIBLE);
                        myViewHolder.L_donations.setVisibility(View.VISIBLE);
                    } else {
                        myViewHolder.L_donation.setVisibility(View.GONE);
                        myViewHolder.L_donations.setVisibility(View.GONE);
                    }

                } else {
                    myViewHolder.L_donation.setVisibility(View.GONE);
                    myViewHolder.L_donations.setVisibility(View.GONE);
                }

                if (searchdetailList.getRating() != null) {
                    myViewHolder.rating.setRating(Float.valueOf(searchdetailList.getRating()));
                }
                break;
            case LOADING:
//                Do nothing
                final LoadingVH LHHolder = (LoadingVH) holder;
                break;
        }
    }

    public void setSpecializations(MyViewHolder myViewHolder, final SearchListModel searchdetailList) {
        if (searchdetailList.getSpecialization_displayname() != null) {
            final List<String> list_spec = searchdetailList.getSpecialization_displayname();

            if (list_spec.size() > 0) {
                if (list_spec.size() == 1) {
                    myViewHolder.L_specialization.setVisibility(View.VISIBLE);
                    myViewHolder.tv_spec1.setText(list_spec.get(0));
                    myViewHolder.tv_spec1.setTextSize(13);
                    myViewHolder.tv_spec1.setVisibility(View.VISIBLE);
                    myViewHolder.tv_spec2.setVisibility(View.GONE);
                    myViewHolder.tv_spec_more.setVisibility(View.GONE);
                    myViewHolder.tv_spec22.setVisibility(View.GONE);
                } else {
                    myViewHolder.L_specialization.setVisibility(View.VISIBLE);
                    myViewHolder.tv_spec1.setText(list_spec.get(0) + " , ");
                    myViewHolder.tv_spec1.setTextSize(13);
                    myViewHolder.tv_spec1.setVisibility(View.VISIBLE);
                    myViewHolder.tv_spec1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    //    myViewHolder.tv_spec1.setEllipsize(TextUtils.TruncateAt.END);
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
                        myViewHolder.tv_spec_more.setText(" > ");
                        myViewHolder.tv_spec_more.setTextSize(20);
                        myViewHolder.tv_spec_more.setVisibility(View.VISIBLE);
                    } else {
                        myViewHolder.tv_spec22.setText(list_spec.get(1));
                        myViewHolder.tv_spec22.setTextSize(13);
                        myViewHolder.tv_spec22.setVisibility(View.VISIBLE);
                        //    myViewHolder.tv_spec22.setEllipsize(TextUtils.TruncateAt.END);
                        myViewHolder.tv_spec22.setMaxLines(1);
                        //     myViewHolder.tv_spec22.setMaxEms(8);

                        myViewHolder.tv_spec2.setVisibility(View.GONE);
                        myViewHolder.tv_spec_more.setVisibility(View.GONE);

                    }

                }
                myViewHolder.tv_spec2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // mAdapterCallback.onMethodCallback(searchdetailList.getUniqueid());
                        mAdapterCallback.onMethodSpecialization(searchdetailList.getSpecialization_displayname(), searchdetailList.getTitle());

                    }
                });
                myViewHolder.tv_spec22.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // mAdapterCallback.onMethodCallback(searchdetailList.getUniqueid());
                        mAdapterCallback.onMethodSpecialization(searchdetailList.getSpecialization_displayname(), searchdetailList.getTitle());

                    }
                });

                myViewHolder.tv_spec_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // mAdapterCallback.onMethodCallback(searchdetailList.getUniqueid());
                        mAdapterCallback.onMethodSpecialization(searchdetailList.getSpecialization_displayname(), searchdetailList.getTitle());

                    }
                });
            }
        } else {
            myViewHolder.tv_spec1.setVisibility(View.GONE);
            myViewHolder.tv_spec2.setVisibility(View.GONE);
            myViewHolder.tv_spec_more.setVisibility(View.GONE);
            myViewHolder.tv_spec22.setVisibility(View.GONE);
            myViewHolder.L_specialization.setVisibility(View.GONE);
        }
    }

    public void disableCheckinButton(MyViewHolder myViewHolder, SearchListModel searchdetailList) {
        if(searchdetailList.isShowToken()){
            myViewHolder.btncheckin.setText("GET TOKEN");

        }
        else{
            myViewHolder.btncheckin.setText("CHECK-IN");
        }
        myViewHolder.btncheckin.setBackgroundColor(Color.parseColor("#cfcfcf"));
        myViewHolder.btncheckin.setTextColor(context.getResources().getColor(R.color.button_grey));
        myViewHolder.btncheckin.setEnabled(false);
        myViewHolder.btncheckin.setVisibility(View.VISIBLE);
    }

    public void enableCheckinButton(MyViewHolder myViewHolder) {
        myViewHolder.btncheckin.setBackgroundColor(context.getResources().getColor(R.color.green));
        myViewHolder.btncheckin.setTextColor(context.getResources().getColor(R.color.white));
        myViewHolder.btncheckin.setEnabled(true);
        myViewHolder.btncheckin.setVisibility(View.VISIBLE);
    }

    public void setFutureDateCheckin(final SearchListModel searchdetailList, MyViewHolder myViewHolder, int position) {
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
            if(searchdetailList.getCalculationMode()!=null){
                if(searchdetailList.getCalculationMode().equalsIgnoreCase("NoCalc")){
                    if(mQueueList!=null){
                        for(int i = 0;i<mQueueList.size();i++) {
                            if (mQueueList.get(i).getNextAvailableQueue() != null && mQueueList.get(i).getNextAvailableQueue().isOpenNow()) {
                                myViewHolder.tv_WaitTime.setVisibility(View.GONE);
                            } else {
                                if (searchdetailList.isShowToken()) {
                                    myViewHolder.tv_WaitTime.setVisibility(View.VISIBLE);
                                } else {
                                    myViewHolder.tv_WaitTime.setVisibility(View.GONE);
                                }

                            }

                            if(searchdetailList.getOnline_profile()!=null) {
                                if (searchdetailList.getOnline_profile().equals("1") && searchdetailList.isWaitlistEnabled()) {
                                    if (searchdetailList.getFuture_checkins() != null && mQueueList.get(i).getNextAvailableQueue()!=null) {
                                        if(mQueueList.get(i).getNextAvailableQueue().getAvailableDate()!=null){
                                            if (searchdetailList.getFuture_checkins().equalsIgnoreCase("1")) {
                                                myViewHolder.tv_Futuredate.setVisibility(View.VISIBLE);
                                                myViewHolder.L_services.setVisibility(View.VISIBLE);
                                                myViewHolder.tv_Futuredate.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent iCheckIn = new Intent(v.getContext(), CheckIn.class);
                                                        if (searchdetailList.getmLoc() != null) {
                                                            iCheckIn.putExtra("serviceId", Integer.parseInt(searchdetailList.getmLoc()));
                                                        }
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
                                                        iCheckIn.putExtra("terminology", termilogy);
                                                        iCheckIn.putExtra("isshowtoken", searchdetailList.isShowToken());
                                                        iCheckIn.putExtra("getAvail_date", searchdetailList.getAvail_date());
                                                        context.startActivity(iCheckIn);
                                                    }
                                                });
                                            } else {
                                                myViewHolder.tv_Futuredate.setVisibility(View.GONE);
                                                myViewHolder.L_services.setVisibility(View.GONE);
                                            }
                                        }else {
                                            myViewHolder.tv_Futuredate.setVisibility(View.GONE);
                                            myViewHolder.L_services.setVisibility(View.GONE);
                                        }
                                    } else {
                                        myViewHolder.tv_Futuredate.setVisibility(View.GONE);
                                        myViewHolder.L_services.setVisibility(View.GONE);
                                    }

                                }else {
                                    myViewHolder.tv_Futuredate.setVisibility(View.GONE);
                                    myViewHolder.L_services.setVisibility(View.GONE);
                                }
                            }else {
                                myViewHolder.tv_Futuredate.setVisibility(View.GONE);
                                myViewHolder.L_services.setVisibility(View.GONE);
                            }

                        }
                    }
                }
                else{
                    myViewHolder.tv_WaitTime.setVisibility(View.VISIBLE);
                }
            }

            myViewHolder.tv_peopleahead.setText(String.valueOf(searchdetailList.getPersonAhead()) + " People waiting in line");
            myViewHolder.tv_peopleahead.setVisibility(View.VISIBLE);
            disableCheckinButton(myViewHolder,searchdetailList);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setCurrentDateCheckin(SearchListModel searchdetailList, MyViewHolder myViewHolder) {

        if (searchdetailList.getServiceTime() != null) {
            Typeface tyface1 = Typeface.createFromAsset(context.getAssets(),
                    "fonts/Montserrat_Bold.otf");
            String firstWord = "Next Available Time ";
            String secondWord = "\nToday, " + searchdetailList.getServiceTime();
            myViewHolder.tv_WaitTime.setText(firstWord + secondWord);
            myViewHolder.tv_peopleahead.setText(String.valueOf(searchdetailList.getPersonAhead()) + " People waiting in line");
//            enableCheckinButton(myViewHolder);
        } else { // Est. Waiting Time
            Typeface tyface1 = Typeface.createFromAsset(context.getAssets(),
                    "fonts/Montserrat_Bold.otf");
            String firstWord = "Est Wait Time ";
            String secondWord = "\n" + Config.getTimeinHourMinutes(searchdetailList.getQueueWaitingTime());
            myViewHolder.tv_WaitTime.setText(firstWord + secondWord);
            myViewHolder.tv_peopleahead.setText(String.valueOf(searchdetailList.getPersonAhead()) + " People waiting in line");
//            enableCheckinButton(myViewHolder);
        }

        myViewHolder.tv_WaitTime.setVisibility(View.VISIBLE);
        myViewHolder.tv_peopleahead.setVisibility(View.VISIBLE);

    }

    public void noCalcShowToken(SearchListModel searchdetailList, MyViewHolder myViewHolder) {
        if (searchdetailList.getPersonAhead() != -1) {
            Config.logV("personAheadtttt @@@@@@@@@@@6666@@@ ####" + searchdetailList.getPersonAhead());
            if (searchdetailList.getPersonAhead() == 0) {
                myViewHolder.tv_WaitTime.setVisibility(View.VISIBLE);
              //  myViewHolder.tv_WaitTime.setText(" Be the first in line");
                String firstWord = "Next Available Time ";
                String secondWord = "\nToday, " + searchdetailList.getServiceTime();
                Spannable spannable = new SpannableString(firstWord + secondWord);
               // spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                myViewHolder.tv_WaitTime.setText(spannable);
                if(searchdetailList.getServiceTime()!= null){
                    myViewHolder.tv_WaitTime.setVisibility(View.VISIBLE);
                }
                else{
                    myViewHolder.tv_WaitTime.setVisibility(View.GONE);
                }
                myViewHolder.tv_peopleahead.setText(String.valueOf(searchdetailList.getPersonAhead()) + " People waiting in line");
                myViewHolder.tv_peopleahead.setVisibility(View.VISIBLE);
            } else {
                myViewHolder.tv_WaitTime.setVisibility(View.VISIBLE);
//                String firstWord = String.valueOf(searchdetailList.getPersonAhead());
//                String secondWord = " People waiting in line";
                String firstWord = "Next Available Time ";
                String secondWord = "\nToday, " + searchdetailList.getServiceTime();
                Typeface tyface1 = Typeface.createFromAsset(context.getAssets(),
                        "fonts/Montserrat_Bold.otf");
                Spannable spannable = new SpannableString(firstWord + secondWord);
                spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                myViewHolder.tv_WaitTime.setText(spannable);
                if(searchdetailList.getServiceTime()!=null){
                    myViewHolder.tv_WaitTime.setVisibility(View.VISIBLE);
                }
                else{
                    myViewHolder.tv_WaitTime.setVisibility(View.GONE);

                }

                myViewHolder.tv_peopleahead.setText(String.valueOf(searchdetailList.getPersonAhead()) + " People waiting in line");
                myViewHolder.tv_peopleahead.setVisibility(View.VISIBLE);
            }
        }
    }

    public int dpToPx(int dp) {
        float density = context.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    @Override
    public int getItemCount() {
        return searchResults == null ? 0 : searchResults.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == searchResults.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void add(SearchListModel r) {
        searchResults.add(r);
        notifyItemInserted(searchResults.size() - 1);
        Config.logV("List size----------------------" + searchResults.size());
    }

    public void addAll(List<SearchListModel> moveResults) {
        for (SearchListModel result : moveResults) {
            add(result);
        }
    }

    public void remove(SearchListModel r) {
        int position = searchResults.indexOf(r);
        if (position > -1) {
            searchResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new SearchListModel());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = searchResults.size() - 1;
        SearchListModel result = getItem(position);

        if (result != null) {
            searchResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public SearchListModel getItem(int position) {
        return searchResults.get(position);
    }

    /*
    View Holders
    _________________________________________________________________________________________________
     */
    private void ApiService(String uniqueID, final String serviceName, final String title) {
        ApiInterface apiService =
                ApiClient.getClientS3Cloud(context).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(context, context.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
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
                                iService.putExtra("department", service.get(i).getDepartment());
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


    /**
     * Main list's content ViewHolder
     */
    protected class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name, tv_location, tv_domain, tv_Futuredate, tv_WaitTime, tv_spec1, tv_spec2, tv_spec_more, tv_spec22, tv_count, tv_qmessage, tv_dept, tv_services, tv_dep1, tv_dep2, tv_dep22, tv_dep_more, tv_peopleahead;
        LinearLayout L_specialization, L_services, L_layout_type, L_checkin, L_departments,L_appoinment,L_appointments,L_donation,L_donations;
        View vsep;

        ImageView ic_jaldeeverifiedIcon;
        ImageView profile, profile1, profile2;
        RatingBar rating;
        TextView tv_claimable, tv_distance, tv_branch_name;

        Button btncheckin,btnappointments,btndonations;
        LinearLayout layout_row;
        TextView mImageViewText,tv_useWeb;
        LinearLayout layout_type;
        ImageView jdn_icon;


        public MyViewHolder(View view) {
            super(view);
            L_checkin = view.findViewById(R.id.checkinlayout);
            L_appoinment = view.findViewById(R.id.appoinmentLayouts);
            L_donation = view.findViewById(R.id.donationLayouts);
            ic_jaldeeverifiedIcon = view.findViewById(R.id.ic_jaldeeverifiedIcon);
            tv_name = view.findViewById(R.id.name);
            tv_count = view.findViewById(R.id.count_doctors);
            tv_qmessage = view.findViewById(R.id.qmessage);
            tv_claimable = view.findViewById(R.id.claimable);
            tv_branch_name = view.findViewById(R.id.branch_name);
            tv_location = view.findViewById(R.id.location);
            tv_domain = view.findViewById(R.id.domain);
            profile = view.findViewById(R.id.profile);
            rating = view.findViewById(R.id.mRatingBar);
            L_departments = view.findViewById(R.id.department);
            L_services = view.findViewById(R.id.service);
            L_appointments = view.findViewById(R.id.appointmentList);
            L_donations = view.findViewById(R.id.donationList);
            tv_distance = view.findViewById(R.id.distance);
            btncheckin = view.findViewById(R.id.btncheckin);

            btnappointments = view.findViewById(R.id.btnappointments);
            btndonations = view.findViewById(R.id.btndonations);
            tv_Futuredate = view.findViewById(R.id.txt_diffdate);
            tv_WaitTime = view.findViewById(R.id.txtWaitTime);
            L_specialization = view.findViewById(R.id.Lspec);
            L_layout_type = view.findViewById(R.id.layout_type);
            layout_row = view.findViewById(R.id.layout_row);
            tv_spec1 = view.findViewById(R.id.txtspec1);
            tv_spec2 = view.findViewById(R.id.txtspec2);
            tv_spec_more = view.findViewById(R.id.txtspec3);
            tv_spec22 = view.findViewById(R.id.txtspec22);
            mImageViewText = view.findViewById(R.id.mImageViewText);
            layout_type = view.findViewById(R.id.layout_type);
            tv_dept = view.findViewById(R.id.departments);
            tv_services = view.findViewById(R.id.services);
            tv_dep1 = view.findViewById(R.id.txtdep1);
            tv_dep2 = view.findViewById(R.id.txtdep2);
            tv_dep22 = view.findViewById(R.id.txtdep22);
            tv_dep_more = view.findViewById(R.id.txtdep_more);
            tv_useWeb = view.findViewById(R.id.useWeb);
            profile1 = view.findViewById(R.id.iprofile1);
            profile2 = view.findViewById(R.id.iprofile2);
            vsep = view.findViewById(R.id.separator);
            jdn_icon = view.findViewById(R.id.txtjdn);
            tv_peopleahead = view.findViewById(R.id.txt_peopleahead);


        }
    }

    ImageView profile1, profile2, profile3;


    public void UpdateGallery(final ArrayList<SearchViewDetail> mGallery, final SearchListModel searchdetailList) {
        //  Picasso.with(this).load(mGallery.get(0).getUrl()).fit().into(mImgeProfile);

        Config.logV("Gallery--------------333-----" + mGallery.size());

        try {

            if (mGallery.size() > 0 || searchdetailList.getLogo() != null) {
//                profile.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {

                Config.logV("Gallery------------------------------" + mGallery.size());

                ArrayList<String> mGalleryList = new ArrayList<>();


                if (searchdetailList.getLogo() != null) {

                    mGalleryList.add(searchdetailList.getLogo());
                }

                for (int i = 0; i < mGallery.size(); i++) {

                    mGalleryList.add(mGallery.get(i).getUrl());


                }
                mGallery.clear();


                boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryList, context);
                if (mValue) {

                    Intent intent = new Intent(context, SwipeGalleryImage.class);
                    intent.putExtra("pos", 0);
                    context.startActivity(intent);
                }


//                    }
//                });


                profile2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Config.logV("Gallery------------------------------" + mGallery.size());
                        ArrayList<String> mGalleryList = new ArrayList<>();


                        if (searchdetailList.getLogo() != null) {

                            mGalleryList.add(searchdetailList.getLogo());
                        }
                        for (int i = 0; i < mGallery.size(); i++) {
                            mGalleryList.add(mGallery.get(i).getUrl());
                        }


                        boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryList, v.getContext());
                        if (mValue) {

                            Intent intent = new Intent(context, SwipeGalleryImage.class);
                            intent.putExtra("pos", 1);
                            context.startActivity(intent);
                        }


                    }
                });


                profile3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Config.logV("Gallery------------------------------" + mGallery.size());
                        ArrayList<String> mGalleryList = new ArrayList<>();


                        if (searchdetailList.getLogo() != null) {

                            mGalleryList.add(searchdetailList.getLogo());
                        }
                        for (int i = 0; i < mGallery.size(); i++) {

                            mGalleryList.add(mGallery.get(i).getUrl());
                        }


                        boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryList, v.getContext());
                        if (mValue) {

                            Intent intent = new Intent(context, SwipeGalleryImage.class);
                            intent.putExtra("pos", 2);
                            context.startActivity(intent);
                        }


                    }
                });


            } /*else {
                tv_Gallery.setVisibility(View.GONE);
            }*/

            // Config.logV("Bussiness logo @@@@@@@@@@" + mBusinessDataList.getLogo());
            if (searchdetailList.getLogo() != null) {
                Picasso.with(context).load(searchdetailList.getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(profile1);

            } else {
                Picasso.with(context).load(mGallery.get(0).getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(profile1);

            }


//            if (mBusinessDataList.getLogo() != null) {
//                if (mGallery.size() > 0) {
//                    tv_mImageViewTextnew.setVisibility(View.VISIBLE);
//                    tv_mImageViewTextnew.setText(" +" + String.valueOf(mGallery.size()));
//                }
//
//            } else if (mBusinessDataList.getLogo() == null) {
//                if (mGallery.size() > 0) {
//                    tv_mImageViewTextnew.setVisibility(View.VISIBLE);
//                    tv_mImageViewTextnew.setText(" +" + String.valueOf(mGallery.size() - 1));
//                } else {
//                    tv_mImageViewTextnew.setVisibility(View.GONE);
//                }
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ApiSearchGallery(final String muniqueID, final SearchListModel searchdetailList) {


        ApiInterface apiService =
                ApiClient.getClientS3Cloud(context).create(ApiInterface.class);


        //final Dialog mDialog = Config.getProgressDialog(context, context.getResources().getString(R.string.dialog_log_in));
        //mDialog.show();

        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));


        Call<ArrayList<SearchViewDetail>> call = apiService.getSearchGallery(Integer.parseInt(muniqueID), sdf.format(currentTime));

        call.enqueue(new Callback<ArrayList<SearchViewDetail>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchViewDetail>> call, Response<ArrayList<SearchViewDetail>> response) {

                try {

                    // if (mDialog.isShowing())
                    // Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL------100000---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-----gallery--------------------" + response.code());

                    if (response.code() == 200) {

                        mSearchGallery = response.body();
                        UpdateGallery(mSearchGallery, searchdetailList);

                    } else {
                        if (searchdetailList.getLogo() != null) {
                            // Picasso.with(mContext).load(mBusinessDataList.getLogo().getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(mImgeProfile);
                            ArrayList<String> mGalleryList = new ArrayList<>();
                            mGalleryList.add(searchdetailList.getLogo());

                            boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryList, context);
                            if (mValue) {

                                Intent intent = new Intent(context, SwipeGalleryImage.class);
                                intent.putExtra("pos", 0);
                                context.startActivity(intent);
                            }
                            // UpdateGallery(mSearchGallery,searchdetailList);
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<SearchViewDetail>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                //if (mDialog.isShowing())
                //  Config.closeDialog(getActivity(), mDialog);

            }
        });


    }


    protected class LoadingVH extends RecyclerView.ViewHolder {
        ProgressBar loadmore_progress;

        public LoadingVH(View itemView) {
            super(itemView);
            loadmore_progress = itemView.findViewById(R.id.loadmore_progress);
        }
    }


}