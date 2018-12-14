package com.nv.youneverwait.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.nv.youneverwait.activities.SearchServiceActivity;
import com.nv.youneverwait.callback.AdapterCallback;
import com.nv.youneverwait.R;
import com.nv.youneverwait.activities.CheckIn;
import com.nv.youneverwait.activities.MessageActivity;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.custom.CircleTransform;
import com.nv.youneverwait.custom.CustomTypefaceSpan;
import com.nv.youneverwait.model.SearchListModel;
import com.nv.youneverwait.model.WorkingModel;
import com.nv.youneverwait.response.SearchService;
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


public class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;


    private List<SearchListModel> searchResults;
    private Context context;
    ArrayList<WorkingModel> workingModelArrayList = new ArrayList<>();
    private boolean isLoadingAdded = false;
    Fragment mFragment;
    String workingHrs = "";
    SearchView mSearchView;
    private AdapterCallback mAdapterCallback;
    Activity activity;

    public PaginationAdapter(Activity activity, SearchView searchview, Context context, Fragment mFragment, AdapterCallback callback) {
        this.context = context;
        searchResults = new ArrayList<>();
        this.mFragment = mFragment;
        mSearchView = searchview;
        Config.logV("Fragment Context 1" + mFragment);
        this.mAdapterCallback = callback;
        this.activity = activity;

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

    ParkingTypesAdapter mParkTypeAdapter;
    static SimpleDateFormat inputParser = new SimpleDateFormat("HH:mm", Locale.US);
    private static Date dateCompareOne;

    private static Date parseDate(String date) {

        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final SearchListModel searchdetailList = searchResults.get(position);


        switch (getItemViewType(position)) {
            case ITEM:
                final MyViewHolder myViewHolder = (MyViewHolder) holder;


                Config.logV("VERified-----" + searchdetailList.getYnw_verified() + "name" + searchdetailList.getTitle());
                if (searchdetailList.getYnw_verified() == 1) {
                    myViewHolder.tv_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_verified, 0);
                } else {
                    myViewHolder.tv_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }


////////////////////////////7 types////////////////////////////////////////////
                ArrayList<ParkingModel> listType = new ArrayList<>();
                listType.clear();

                if (searchdetailList.getParking_type_location1() != null) {
                    if (searchdetailList.getParking_location1().equalsIgnoreCase("1")) {
                        ParkingModel mType = new ParkingModel();
                        // mType.setTypeicon(R.drawable.icon_24hours);
                        Config.logV("Park-@@@@--------11111-----------" + searchdetailList.getTitle());
                        mType.setId("1");
                        mType.setTypename("Parking " + searchdetailList.getParking_type_location1());
                        listType.add(mType);
                    }
                }

                if (searchdetailList.getAlways_open_location1() != null) {
                    if (searchdetailList.getAlways_open_location1().equalsIgnoreCase("1")) {
                        ParkingModel mType = new ParkingModel();
                        //mType.setTypeicon(R.drawable.);
                        mType.setId("2");
                        mType.setTypename("24 Hours");
                        listType.add(mType);
                    }
                }


                if (searchdetailList.getDentistemergencyservices_location1() != null) {
                    if (searchdetailList.getDentistemergencyservices_location1().equalsIgnoreCase("1")) {
                        ParkingModel mType = new ParkingModel();
                        //mType.setTypeicon(R.drawable.);
                        mType.setId("3");
                        mType.setTypename("Emergency");
                        listType.add(mType);
                    }
                }


                if (searchdetailList.getDocambulance_location1() != null) {
                    if (searchdetailList.getDocambulance_location1().equalsIgnoreCase("1")) {
                        ParkingModel mType = new ParkingModel();
                        //mType.setTypeicon(R.drawable.);
                        mType.setId("4");
                        mType.setTypename("Ambulance");
                        listType.add(mType);
                    }
                }


                if (searchdetailList.getFirstaid_location1() != null) {
                    if (searchdetailList.getFirstaid_location1().equalsIgnoreCase("1")) {
                        ParkingModel mType = new ParkingModel();
                        //mType.setTypeicon(R.drawable.);
                        mType.setId("5");
                        mType.setTypename("First Aid");
                        listType.add(mType);
                    }
                }


                if (searchdetailList.getPhysiciansemergencyservices_location1() != null) {
                    if (searchdetailList.getPhysiciansemergencyservices_location1().equalsIgnoreCase("1")) {
                        ParkingModel mType = new ParkingModel();
                        //mType.setTypeicon(R.drawable.);
                        mType.setId("6");
                        mType.setTypename("Emergency");
                        listType.add(mType);
                    }
                }


                if (searchdetailList.getTraumacentre_location1() != null) {
                    if (searchdetailList.getTraumacentre_location1().equalsIgnoreCase("1")) {
                        ParkingModel mType = new ParkingModel();
                        //mType.setTypeicon(R.drawable.);
                        mType.setId("7");
                        mType.setTypename("Trauma");
                        listType.add(mType);
                    }
                }


                if (listType.size() > 0) {
                    myViewHolder.mRecycleTypes.setVisibility(View.VISIBLE);
                    Config.logV("Park-@@@@-------------6666------" + listType.size());
                    mParkTypeAdapter = new ParkingTypesAdapter(listType, context);
                    LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                    myViewHolder.mRecycleTypes.setLayoutManager(horizontalLayoutManager);
                    myViewHolder.mRecycleTypes.setAdapter(mParkTypeAdapter);
                } else {
                    myViewHolder.mRecycleTypes.setVisibility(View.GONE);
                }


                //////////////////////////////////////////////////////////////

                Typeface tyface_confm = Typeface.createFromAsset(context.getAssets(),
                        "fonts/Montserrat_Bold.otf");
                myViewHolder.tv_name.setTypeface(tyface_confm);
                myViewHolder.btncheckin.setTypeface(tyface_confm);
                myViewHolder.tv_Open.setTypeface(tyface_confm);

                myViewHolder.tv_location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAdapterCallback.onMethodOpenMap(searchdetailList.getLocation1());
                    }
                });


                myViewHolder.tv_domain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAdapterCallback.onMethodCoupn(searchdetailList.getUniqueid());
                    }
                });

               /* Typeface tyface_date = Typeface.createFromAsset(context.getAssets(),
                        "fonts/Montserrat_Light.otf");
                myViewHolder.tv_Futuredate.setTypeface(tyface_date);*/


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
                    myViewHolder.tv_location.setText(searchdetailList.getPlace1());
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

                //  Config.logV("ID1111" + searchdetailList.getId() + "QID----" + searchdetailList.getQId());

                if (searchdetailList.getId().equalsIgnoreCase(searchdetailList.getQId())) {

                   /* if (searchdetailList.getAvail_date() != null) {
                        myViewHolder.tv_Date.setText(searchdetailList.getAvail_date());
                    }*/
                    Date c = Calendar.getInstance().getTime();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String formattedDate = df.format(c);
                    System.out.println("Current time => " + formattedDate);
                    Config.logV("print" + searchdetailList.getAvail_date() + "" + searchdetailList.getOnline_checkins());


                    Date date1 = null, date2 = null;
                    try {
                        date1 = df.parse(formattedDate);
                        if (searchdetailList.getAvail_date() != null)
                            date2 = df.parse(searchdetailList.getAvail_date());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    if (searchdetailList.getAvail_date() != null && searchdetailList.getOnline_checkins() != null && searchdetailList.getServices() != null) {

                        Config.logV("Title-------" + searchdetailList.getTitle());
                        Config.logV("Title---111----" + searchdetailList.getAvail_date());

                        Config.logV("Title---111---222-" + formattedDate + "" + searchdetailList.getAvail_date() + "online" + searchdetailList.getOnline_checkins());

                        if (searchdetailList.getOnline_checkins() != null) {
                            if (searchdetailList.getOnline_checkins().equalsIgnoreCase("1")) {

                                if ((formattedDate.trim().equalsIgnoreCase(searchdetailList.getAvail_date().trim()) && (searchdetailList.getOnline_checkins().equalsIgnoreCase("1")))) {
                                    Config.logV("Title------333-" + searchdetailList.getTitle());
                                    myViewHolder.btncheckin.setVisibility(View.VISIBLE);
                                    myViewHolder.btncheckin.setBackground(context.getResources().getDrawable(R.drawable.button_gradient_checkin));
                                    myViewHolder.btncheckin.setTextColor(context.getResources().getColor(R.color.app_background));
                                    //   myViewHolder.btncheckin.setBackgroundColor(Color.parseColor("#3498db"));

                                } else if (searchdetailList.getOnline_checkins().equalsIgnoreCase("1") && date1.compareTo(date2) < 0) {
                                    myViewHolder.btncheckin.setVisibility(View.VISIBLE);
                                    // myViewHolder.btncheckin.setBackgroundColor(Color.parseColor("#cfcfcf"));
                                    myViewHolder.btncheckin.setBackground(context.getResources().getDrawable(R.drawable.btn_checkin_grey));
                                    myViewHolder.btncheckin.setTextColor(context.getResources().getColor(R.color.button_grey));
                                    myViewHolder.btncheckin.setEnabled(false);
                                    Config.logV("Title------444-" + searchdetailList.getTitle());
                                }

                            } else {
                                myViewHolder.btncheckin.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        /*if (formattedDate.equalsIgnoreCase(searchdetailList.getAvail_date())&&searchdetailList.getServices()!=null) {
                            myViewHolder.btncheckin.setVisibility(View.VISIBLE);
                        } else {*/
                        myViewHolder.btncheckin.setVisibility(View.GONE);
                        //}
                    }

                    if (searchdetailList.getShow_waiting_time() != null) {

                        if (searchdetailList.getShow_waiting_time().equalsIgnoreCase("1")) {
                            if (searchdetailList.getAvail_date() != null) {
                                myViewHolder.tv_WaitTime.setVisibility(View.VISIBLE);


                                if (formattedDate.equalsIgnoreCase(searchdetailList.getAvail_date())) {
                                    if (searchdetailList.getServiceTime() != null) {

                                        // myViewHolder.tv_WaitTime.setText("Est Wait Time " + "Today ,"+searchdetailList.getServiceTime() );


                                        Typeface tyface1 = Typeface.createFromAsset(context.getAssets(),
                                                "fonts/Montserrat_Bold.otf");
                                       // String firstWord = "Est Service Time ";
                                        String firstWord = "Checked in for ";
                                        /*String firstWord = null;
                                        Date dt = new Date();
                                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                                        String currentTime = sdf.format(dt);
                                        Date datenow=parseDate(currentTime);

                                        dateCompareOne = parseDate(searchdetailList.getServiceTime());
                                        if ( datenow.after( dateCompareOne ) ) {
                                            firstWord = "Est Service Time ";
                                        }else {
                                            firstWord = "Est Wait Time ";

                                        }*/

                                        String secondWord = "\nToday, " + searchdetailList.getServiceTime();
                                        Spannable spannable = new SpannableString(firstWord + secondWord);
                                        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        spannable.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.violet)),
                                                firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                        myViewHolder.tv_WaitTime.setText(spannable);

                                    } else {

                                        if (searchdetailList.getQueueWaitingTime() >= 60) {
                                            int hours = searchdetailList.getQueueWaitingTime() / 60; //since both are ints, you get an int
                                            int minutes = searchdetailList.getQueueWaitingTime() % 60;
                                            Config.logV("TIME*****************" + hours + " " + minutes);
                                            String mtime = hours + " hour" + " " + minutes + " minute";
                                            //myViewHolder.tv_WaitTime.setText("Est Wait Time " + mtime );


                                            Typeface tyface1 = Typeface.createFromAsset(context.getAssets(),
                                                    "fonts/Montserrat_Bold.otf");
                                            //String firstWord = "Est Service Time ";
                                            String firstWord="";
                                            if (hours > 0) {
                                                firstWord = "Checked in for ";
                                            } else {
                                                firstWord = "Est Wait Time ";

                                            }
                                            /*String firstWord = null;
                                            Date dt = new Date();
                                            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                                            String currentTime = sdf.format(dt);
                                            Date datenow=parseDate(currentTime);

                                            dateCompareOne = parseDate(mtime);
                                            if ( datenow.after( dateCompareOne ) ) {
                                                firstWord = "Est Service Time ";
                                            }else {
                                                firstWord = "Est Wait Time ";

                                            }*/

                                            String secondWord = "\n" + mtime;
                                            Spannable spannable = new SpannableString(firstWord + secondWord);
                                            spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            spannable.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.violet)),
                                                    firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                            myViewHolder.tv_WaitTime.setText(spannable);


                                        } else {
                                            //myViewHolder.tv_WaitTime.setText("Est Wait Time " + searchdetailList.getQueueWaitingTime() + " Minutes");


                                            Typeface tyface1 = Typeface.createFromAsset(context.getAssets(),
                                                    "fonts/Montserrat_Bold.otf");
                                            String firstWord = "Est Wait Time ";
                                            String secondWord = "\n" + searchdetailList.getQueueWaitingTime() + " Mins";
                                            Spannable spannable = new SpannableString(firstWord + secondWord);
                                            spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            spannable.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.violet)),
                                                    firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                            myViewHolder.tv_WaitTime.setText(spannable);
                                        }
                                    }
                                }
                                if (date1.compareTo(date2) < 0) {
                                    try {
                                        // String mMonthName=getMonth(searchdetailList.getAvail_date());
                                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                        Date date = format.parse(searchdetailList.getAvail_date());
                                        String day = (String) DateFormat.format("dd", date);
                                        String monthString = (String) DateFormat.format("MMM", date);
                                        //  myViewHolder.tv_WaitTime.setText("Next Wait Time" + monthString+" "+day+ ", " + searchdetailList.getServiceTime());

                                        Typeface tyface1 = Typeface.createFromAsset(context.getAssets(),
                                                "fonts/Montserrat_Bold.otf");
                                        String firstWord = "Next Available Time ";
                                        String secondWord = "\n" + monthString + " " + day + ", " + searchdetailList.getServiceTime();
                                        Spannable spannable = new SpannableString(firstWord + secondWord);
                                        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        spannable.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.violet)),
                                                firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                        myViewHolder.tv_WaitTime.setText(spannable);

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }


                                }
                            }
                        } else {
                            myViewHolder.tv_WaitTime.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        myViewHolder.tv_WaitTime.setVisibility(View.INVISIBLE);
                    }
                    //Open Tag

                    if (searchdetailList.isIsopen()) {
                        myViewHolder.tv_Open.setVisibility(View.VISIBLE);
                    } else {
                        myViewHolder.tv_Open.setVisibility(View.GONE);
                    }


                } else {
                    myViewHolder.btncheckin.setVisibility(View.GONE);
                    myViewHolder.tv_Futuredate.setVisibility(View.GONE);
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
                /////////////////////////////////////////////////////////

                if (searchdetailList.getSpecialization_displayname() != null) {

                    // myViewHolder.tv_specialization.setText(searchdetailList.getSpecialization_displayname());
                    final List<String> list_spec = new ArrayList<String>(Arrays.asList(searchdetailList.getSpecialization_displayname().split(",")));

                    Config.logV("SPECIALIZTION SIZE @@@@@@@@@@@@@@@" + list_spec.size());
                    if (list_spec.size() > 0) {
                        if (list_spec.size() == 1) {

                            myViewHolder.L_specialization.setVisibility(View.GONE);
                            myViewHolder.tv_spec.setText(list_spec.get(0));
                            myViewHolder.tv_spec.setVisibility(View.VISIBLE);
                            myViewHolder.tv_spec.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        } else {


                            myViewHolder.tv_spec.setText(list_spec.get(0));
                            myViewHolder.tv_spec.setVisibility(View.VISIBLE);
                            myViewHolder.tv_spec.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_arrowright_gray, 0);


                            myViewHolder.L_specialization.removeAllViews();
                            myViewHolder.L_specialization.setVisibility(View.GONE);


                            myViewHolder.tv_spec.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myViewHolder.L_specialization.removeAllViews();
                                    myViewHolder.L_specialization.setOrientation(LinearLayout.VERTICAL);
                                    myViewHolder.L_specialization.setVisibility(View.VISIBLE);
                                    myViewHolder.tv_spec.setVisibility(View.GONE);

                                    for (int i = 0; i < list_spec.size(); i++) {

                                        TextView dynaText = new TextView(context);
                                        Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                                                "fonts/Montserrat_Regular.otf");
                                        dynaText.setTypeface(tyface);
                                        dynaText.setText(list_spec.get(i));
                                        dynaText.setTextSize(12);
                                        dynaText.setTextColor(context.getResources().getColor(R.color.black));
                                        dynaText.setEllipsize(TextUtils.TruncateAt.END);
                                        dynaText.setMaxLines(1);
                                        myViewHolder.L_specialization.addView(dynaText);
                                    }

                                }
                            });
                        }


                    } else {
                        myViewHolder.L_specialization.setVisibility(View.GONE);

                    }
                } else {
                    myViewHolder.L_specialization.setVisibility(View.GONE);

                }
                //  Picasso.with(context).load(searchdetailList.getLogo()).fit().into(myViewHolder.profile);
                Picasso.with(context).load(searchdetailList.getLogo()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(myViewHolder.profile);
                if (searchdetailList.getServices() != null) {


                    if (searchdetailList.getServices().size() > 0) {
                        myViewHolder.L_services.removeAllViews();
                        myViewHolder.L_services.setVisibility(View.VISIBLE);

                        int size = 0;
                        if (searchdetailList.getServices().size() == 1) {
                            size = 1;
                        } else {
                            size = 2;
                        }
                        for (int i = 0; i < size; i++) {
                            TextView dynaText = new TextView(context);
                            Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                                    "fonts/Montserrat_Regular.otf");
                            dynaText.setTypeface(tyface);
                            dynaText.setText(searchdetailList.getServices().get(i).toString());
                            dynaText.setTextSize(12);
                            //  dynaText.setPadding(0, 0, 12, 0);
                            dynaText.setTextColor(context.getResources().getColor(R.color.title_consu));
                            dynaText.setBackground(context.getResources().getDrawable(R.drawable.input_border_rounded_blue_bg));
                            dynaText.setEllipsize(TextUtils.TruncateAt.END);
                            dynaText.setMaxLines(1);
                            dynaText.setMaxEms(5);
                            final int finalI = i;
                            dynaText.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ApiService(searchdetailList.getUniqueid(), searchdetailList.getServices().get(finalI).toString(), searchdetailList.getTitle());
                                }
                            });
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(0, 0, 7, 0);
                            dynaText.setLayoutParams(params);
                            myViewHolder.L_services.addView(dynaText);

                        }

                        if (size > 1) {

                            TextView dynaText = new TextView(context);
                            dynaText.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    mAdapterCallback.onMethodServiceCallback(searchdetailList.getServices(), searchdetailList.getTitle(), searchdetailList.getUniqueid());
                                /*ServiceListFragment pfFragment = new ServiceListFragment();
                                FragmentTransaction transaction = mFragment.getFragmentManager().beginTransaction();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("servicelist", searchdetailList.getServices());
                                bundle.putString("title",searchdetailList.getTitle());
                                pfFragment.setArguments(bundle);
                                // Store the Fragment in stack
                                transaction.addToBackStack(null);
                                transaction.replace(R.id.mainlayout, pfFragment).commit();*/
                                }
                            });
                            dynaText.setGravity(Gravity.CENTER);
                            dynaText.setBackground(context.getResources().getDrawable(R.drawable.icon_arrowright_blue));
                            myViewHolder.L_services.addView(dynaText);
                        }
                    } else {
                        myViewHolder.L_services.setVisibility(View.GONE);

                    }

                } else {
                    myViewHolder.L_services.setVisibility(View.GONE);

                }


                if (searchdetailList.getBusiness_hours1() != null) {
                    myViewHolder.tv_Workinghrs.setVisibility(View.VISIBLE);
                    // myViewHolder.tv_Workinghrs.setText(workingHrs);
                } else {
                    myViewHolder.tv_Workinghrs.setVisibility(View.GONE);
                }


                myViewHolder.tv_Workinghrs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

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

                        mAdapterCallback.onMethodWorkingCallback(workingModelArrayList, searchdetailList.getTitle(), searchdetailList.getUniqueid());


                    }
                });


                myViewHolder.tv_communicate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mAdapterCallback.onMethodMessage(searchdetailList.getTitle(), searchdetailList.getId(), "search");

                      /*  Intent iCommunicate = new Intent(v.getContext(), MessageActivity.class);
                        iCommunicate.putExtra("accountID", searchdetailList.getId());
                        iCommunicate.putExtra("provider", searchdetailList.getTitle());
                        iCommunicate.putExtra("from", "search");
                        context.startActivity(iCommunicate);*/

                    }
                });


                if (searchdetailList.getRating() != null) {

                      /* String[] separated = searchdetailList.getRating().split(".");
                       String rating=separated[0]; // this will contain "Fruit"
                       String step=separated[1];*/


                    myViewHolder.rating.setRating(Float.valueOf(searchdetailList.getRating()));
                    // myViewHolder.rating.setStepSize(Float.valueOf("0."+step));
                }

                myViewHolder.layout_row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Config.logV("UNIUE ID----------------" + searchdetailList.getUniqueid());

                        Config.logV("Popular Text__________@@@Dele");
                        mSearchView.setQuery("", false);

                        mAdapterCallback.onMethodCallback(searchdetailList.getUniqueid());

                        /*Bundle bundle = new Bundle();

                        SearchDetailViewFragment pfFragment = new SearchDetailViewFragment();

                        bundle.putString("uniqueID",searchdetailList.getUniqueid());
                        pfFragment.setArguments(bundle);

                        FragmentTransaction transaction = mFragment.getFragmentManager().beginTransaction();
                        // Store the Fragment in stack
                        transaction.addToBackStack(null);
                        transaction.replace(R.id.mainlayout, pfFragment).commit();*/

                    }
                });


                break;


            case LOADING:
//                Do nothing
                final LoadingVH LHHolder = (LoadingVH) holder;


                break;
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


    /*
   Helpers
   _________________________________________________________________________________________________
    */

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

    private static String getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMMM").format(cal.getTime());
        return monthName;
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    protected class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name, tv_location, tv_domain, tv_Date, tv_Futuredate, tv_Open, tv_WaitTime,tv_spec;
        LinearLayout L_specialization, L_services;

        ImageView profile;
        RatingBar rating;
        RelativeLayout mLayout;
        Button btncheckin;
        RecyclerView mRecycleTypes;
        ImageView tv_Workinghrs, tv_communicate;
        LinearLayout layout_row;

        public MyViewHolder(View view) {
            super(view);
            tv_name = (TextView) view.findViewById(R.id.name);
            tv_location = (TextView) view.findViewById(R.id.location);
            tv_domain = (TextView) view.findViewById(R.id.domain);
            profile = (ImageView) view.findViewById(R.id.profile);
            rating = (RatingBar) view.findViewById(R.id.mRatingBar);
            L_services = (LinearLayout) view.findViewById(R.id.service);
            mLayout = (RelativeLayout) view.findViewById(R.id.mLayout);
            //tv_Date = (TextView) view.findViewById(R.id.mDate);
            btncheckin = (Button) view.findViewById(R.id.btncheckin);
            tv_Futuredate = (TextView) view.findViewById(R.id.txt_diffdate);
            tv_Workinghrs = (ImageView) view.findViewById(R.id.txtWorkinghrs);
            tv_Open = (TextView) view.findViewById(R.id.txtOpen);
            tv_WaitTime = (TextView) view.findViewById(R.id.txtWaitTime);
            L_specialization = (LinearLayout) view.findViewById(R.id.specialization);
            tv_communicate = (ImageView) view.findViewById(R.id.txtcommunicate);
            mRecycleTypes = (RecyclerView) view.findViewById(R.id.mRecycleTypes);
            layout_row = (LinearLayout) view.findViewById(R.id.layout_row);
            tv_spec=(TextView)view.findViewById(R.id.txtspec);

        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder {
        ProgressBar loadmore_progress;

        public LoadingVH(View itemView) {
            super(itemView);
            loadmore_progress = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);
        }
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
