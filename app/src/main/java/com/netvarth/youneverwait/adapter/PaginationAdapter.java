package com.netvarth.youneverwait.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
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


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.netvarth.youneverwait.Fragment.SearchDetailFragment;
import com.netvarth.youneverwait.Fragment.SearchDetailViewFragment;
import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.activities.CheckIn;
import com.netvarth.youneverwait.activities.MessageActivity;
import com.netvarth.youneverwait.activities.SearchServiceActivity;
import com.netvarth.youneverwait.common.Config;
import com.netvarth.youneverwait.custom.CircleTransform;
import com.netvarth.youneverwait.custom.CustomTypefaceSpan;
import com.netvarth.youneverwait.model.SearchListModel;
import com.netvarth.youneverwait.response.SearchAWsResponse;
import com.netvarth.youneverwait.response.SearchService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;


    private List<SearchListModel> searchResults;
    private Context context;

    private boolean isLoadingAdded = false;


    public PaginationAdapter(Context context) {
        this.context = context;
        searchResults = new ArrayList<>();

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
        View v1 = inflater.inflate(R.layout.searchdetail_row, parent, false);
        viewHolder = new MyViewHolder(v1);
        return viewHolder;
    }
    ParkingTypesAdapter mParkTypeAdapter;
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final SearchListModel searchdetailList = searchResults.get(position);


        switch (getItemViewType(position)) {
            case ITEM:
                final MyViewHolder myViewHolder = (MyViewHolder) holder;


////////////////////////////7 types////////////////////////////////////////////
            /*    ArrayList<SearchListModel> listType=new ArrayList<>();
                SearchListModel mType=new SearchListModel();
                if(searchdetailList.getParking_type_location1()!=null)
                mType.setParking_type_location1(searchdetailList.getParking_type_location1());
                listType.add(mType);

                if(searchdetailList.getAlways_open_location1()!=null)
                mType.setAlways_open_location1(searchdetailList.getAlways_open_location1());
                listType.add(mType);

                if(searchdetailList.getDentistemergencyservices_location1()!=null)
                mType.setDentistemergencyservices_location1(searchdetailList.getDentistemergencyservices_location1());
                listType.add(mType);

                if(searchdetailList.getDocambulance_location1()!=null)
                mType.setDocambulance_location1(searchdetailList.getDocambulance_location1());
                listType.add(mType);

                if(searchdetailList.getFirstaid_location1()!=null)
                mType.setFirstaid_location1(searchdetailList.getFirstaid_location1());
                listType.add(mType);

                if(searchdetailList.getPhysiciansemergencyservices_location1()!=null)
                mType.setPhysiciansemergencyservices_location1(searchdetailList.getPhysiciansemergencyservices_location1());
                listType.add(mType);

                if(searchdetailList.getTraumacentre_location1()!=null)
                mType.setTraumacentre_location1(searchdetailList.getTraumacentre_location1());
                listType.add(mType);

                if(listType.size()>0) {
                    mParkTypeAdapter = new ParkingTypesAdapter(listType, context);
                    LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                    myViewHolder.mRecycleTypes.setLayoutManager(horizontalLayoutManager);
                    myViewHolder.mRecycleTypes.setAdapter(mParkTypeAdapter);
                }*/


                //////////////////////////////////////////////////////////////

                Typeface tyface_confm = Typeface.createFromAsset(context.getAssets(),
                        "fonts/Montserrat_Bold.otf");
                myViewHolder.tv_name.setTypeface(tyface_confm);
                myViewHolder.btncheckin.setTypeface(tyface_confm);
                myViewHolder.tv_Open.setTypeface(tyface_confm);

               /* Typeface tyface_date = Typeface.createFromAsset(context.getAssets(),
                        "fonts/Montserrat_Light.otf");
                myViewHolder.tv_Futuredate.setTypeface(tyface_date);*/


                if(searchdetailList.getQualification()!=null){
                    myViewHolder.tv_name.setText(searchdetailList.getTitle()+" "+searchdetailList.getQualification());
                }else {
                    myViewHolder.tv_name.setText(searchdetailList.getTitle());
                }
                myViewHolder.tv_domain.setText(searchdetailList.getSector());

                myViewHolder.tv_location.setText(searchdetailList.getPlace1());


                if (searchdetailList.getFuture_checkins() != null) {
                    if (searchdetailList.getFuture_checkins().equalsIgnoreCase("1")) {
                        myViewHolder.tv_Futuredate.setVisibility(View.VISIBLE);
                        myViewHolder.tv_Futuredate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent iCheckIn=new Intent(v.getContext(), CheckIn.class);
                                iCheckIn.putExtra("serviceId",Integer.parseInt(searchdetailList.getmLoc()));
                                iCheckIn.putExtra("uniqueID", searchdetailList.getUniqueid());
                                iCheckIn.putExtra("accountID",searchdetailList.getId());
                                iCheckIn.putExtra("waititme", myViewHolder.tv_WaitTime.getText().toString());
                                iCheckIn.putExtra("from", "future_date");
                                Config.logV("Account ID-------------"+searchdetailList.getId());
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

                Config.logV("ID1111" + searchdetailList.getId() + "QID----" + searchdetailList.getQId());

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


                    if (searchdetailList.getAvail_date() != null && searchdetailList.getOnline_checkins() != null&&searchdetailList.getServices()!=null) {

                        Config.logV("Title-------"+searchdetailList.getTitle());
                        Config.logV("Title---111----"+searchdetailList.getAvail_date());

                        Config.logV("Title---111---222-"+formattedDate+""+searchdetailList.getAvail_date()+"online"+searchdetailList.getOnline_checkins());
                        if ((formattedDate.trim().equalsIgnoreCase(searchdetailList.getAvail_date().trim()) && (searchdetailList.getOnline_checkins().equalsIgnoreCase("1")))) {
                            Config.logV("Title------333-"+searchdetailList.getTitle());
                            myViewHolder.btncheckin.setVisibility(View.VISIBLE);
                         //   myViewHolder.btncheckin.setBackgroundColor(Color.parseColor("#3498db"));

                        } else if (searchdetailList.getOnline_checkins().equalsIgnoreCase("1") && date1.compareTo(date2) < 0) {
                            myViewHolder.btncheckin.setVisibility(View.VISIBLE);
                           // myViewHolder.btncheckin.setBackgroundColor(Color.parseColor("#cfcfcf"));
                            myViewHolder.btncheckin.setEnabled(false);
                            Config.logV("Title------444-"+searchdetailList.getTitle());
                        }
                    } else {
                        /*if (formattedDate.equalsIgnoreCase(searchdetailList.getAvail_date())&&searchdetailList.getServices()!=null) {
                            myViewHolder.btncheckin.setVisibility(View.VISIBLE);
                        } else {*/
                            myViewHolder.btncheckin.setVisibility(View.GONE);
                        //}
                    }

                    if(searchdetailList.getShow_waiting_time()!=null){

                        if(searchdetailList.getShow_waiting_time().equalsIgnoreCase("1")) {
                            if (searchdetailList.getAvail_date() != null) {
                                myViewHolder.tv_WaitTime.setVisibility(View.VISIBLE);


                                if (formattedDate.equalsIgnoreCase(searchdetailList.getAvail_date())) {
                                    if( searchdetailList.getServiceTime()!=null){

                                           // myViewHolder.tv_WaitTime.setText("Est Wait Time " + "Today ,"+searchdetailList.getServiceTime() );


                                        Typeface tyface1 = Typeface.createFromAsset(context.getAssets(),
                                                "fonts/Montserrat_Bold.otf");
                                        String firstWord="Est Wait Time ";
                                        String secondWord="Today ,"+searchdetailList.getServiceTime();
                                        Spannable spannable = new SpannableString(firstWord+secondWord);
                                        spannable.setSpan( new CustomTypefaceSpan("sans-serif",tyface1), firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        spannable.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.violet)),
                                                firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                        myViewHolder.tv_WaitTime.setText( spannable );

                                    }else {

                                        if(searchdetailList.getQueueWaitingTime()>=60){
                                            int hours = searchdetailList.getQueueWaitingTime()/ 60; //since both are ints, you get an int
                                            int minutes = searchdetailList.getQueueWaitingTime() % 60;
                                            Config.logV("TIME*****************"+ hours+" "+minutes);
                                            String mtime=hours+" hour"+" "+minutes+" minute";
                                            //myViewHolder.tv_WaitTime.setText("Est Wait Time " + mtime );

                                            Typeface tyface1 = Typeface.createFromAsset(context.getAssets(),
                                                    "fonts/Montserrat_Bold.otf");
                                            String firstWord="Est Wait Time ";
                                            String secondWord=mtime;
                                            Spannable spannable = new SpannableString(firstWord+secondWord);
                                            spannable.setSpan( new CustomTypefaceSpan("sans-serif",tyface1), firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            spannable.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.violet)),
                                                    firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                            myViewHolder.tv_WaitTime.setText( spannable );


                                        }else {
                                            //myViewHolder.tv_WaitTime.setText("Est Wait Time " + searchdetailList.getQueueWaitingTime() + " Minutes");


                                            Typeface tyface1 = Typeface.createFromAsset(context.getAssets(),
                                                    "fonts/Montserrat_Bold.otf");
                                            String firstWord="Est Wait Time ";
                                            String secondWord=searchdetailList.getQueueWaitingTime()+" Mins";
                                            Spannable spannable = new SpannableString(firstWord+secondWord);
                                            spannable.setSpan( new CustomTypefaceSpan("sans-serif",tyface1), firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            spannable.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.violet)),
                                                    firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                            myViewHolder.tv_WaitTime.setText( spannable );
                                        }
                                    }
                                }
                                if (date1.compareTo(date2) < 0) {
                                    try {
                                       // String mMonthName=getMonth(searchdetailList.getAvail_date());
                                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                        Date date = format.parse(searchdetailList.getAvail_date());
                                        String day          = (String) DateFormat.format("dd", date);
                                        String monthString  = (String) DateFormat.format("MMM",  date);
                                      //  myViewHolder.tv_WaitTime.setText("Next Wait Time" + monthString+" "+day+ ", " + searchdetailList.getServiceTime());

                                        Typeface tyface1 = Typeface.createFromAsset(context.getAssets(),
                                                "fonts/Montserrat_Bold.otf");
                                        String firstWord="Next Wait Time ";
                                        String secondWord=monthString+" "+day+ ", " + searchdetailList.getServiceTime();
                                        Spannable spannable = new SpannableString(firstWord+secondWord);
                                        spannable.setSpan( new CustomTypefaceSpan("sans-serif",tyface1), firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        spannable.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.violet)),
                                                firstWord.length(), firstWord.length()+secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                        myViewHolder.tv_WaitTime.setText( spannable );

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }


                                }
                            }
                        }else{
                            myViewHolder.tv_WaitTime.setVisibility(View.GONE);
                        }
                    }else{
                        myViewHolder.tv_WaitTime.setVisibility(View.GONE);
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
                        Intent iCheckIn=new Intent(v.getContext(), CheckIn.class);
                        iCheckIn.putExtra("serviceId",Integer.parseInt(searchdetailList.getmLoc()));
                        iCheckIn.putExtra("uniqueID", searchdetailList.getUniqueid());
                        iCheckIn.putExtra("accountID",searchdetailList.getId());
                        iCheckIn.putExtra("waititme", myViewHolder.tv_WaitTime.getText().toString());
                        iCheckIn.putExtra("from", "checkin");
                        Config.logV("Account ID-------------"+searchdetailList.getId());
                        context.startActivity(iCheckIn);
                    }
                });
                /////////////////////////////////////////////////////////

                if(searchdetailList.getSpecialization_displayname()!=null){

                   // myViewHolder.tv_specialization.setText(searchdetailList.getSpecialization_displayname());
                    List<String> list_spec = new ArrayList<String>(Arrays.asList(searchdetailList.getSpecialization_displayname().split(",")));

                    if(list_spec.size()>0) {
                        myViewHolder.L_specialization.removeAllViews();
                        myViewHolder.L_specialization.setVisibility(View.VISIBLE);
                        int size = 0;
                        if (list_spec.size() == 1) {
                            size = 1;
                        } else {
                            size = 2;
                        }
                        for (int i = 0; i < size; i++) {
                            TextView dynaText = new TextView(context);
                            dynaText.setText(list_spec.get(i));
                            dynaText.setTextSize(13);
                            dynaText.setPadding(0, 0, 10, 0);
                            dynaText.setTextColor(context.getResources().getColor(R.color.title_grey));
                            dynaText.setEllipsize(TextUtils.TruncateAt.END);
                            dynaText.setMaxLines(1);
                            dynaText.setMaxEms(8);
                            myViewHolder.L_specialization.addView(dynaText);
                        }
                    }else{
                        myViewHolder.L_specialization.setVisibility(View.GONE);
                    }
                }else{
                    myViewHolder.L_specialization.setVisibility(View.GONE);
                }
              //  Picasso.with(context).load(searchdetailList.getLogo()).fit().into(myViewHolder.profile);
                Picasso.with(context).load(searchdetailList.getLogo()).transform(new CircleTransform()).fit().into(myViewHolder.profile);
                if (searchdetailList.getServices() != null) {


                    if(searchdetailList.getServices().size()>0) {
                        myViewHolder.L_services.removeAllViews();
                        myViewHolder.L_services.setVisibility(View.VISIBLE);
                        int size=0;
                        if(searchdetailList.getServices().size()==1) {
                            size = 1;
                        }else {
                            size=2;
                        }
                        for (int i = 0; i < size; i++) {
                            TextView dynaText = new TextView(context);
                            dynaText.setText(searchdetailList.getServices().get(i).toString());
                            dynaText.setTextSize(13);
                            dynaText.setPadding(0, 0, 10, 0);
                            dynaText.setTextColor(context.getResources().getColor(R.color.title_consu));
                            dynaText.setEllipsize(TextUtils.TruncateAt.END);
                            dynaText.setMaxLines(1);
                            dynaText.setMaxEms(8);
                            myViewHolder.L_services.addView(dynaText);

                        }
                    }else{
                        myViewHolder.L_services.setVisibility(View.GONE);
                    }

                } else {
                    myViewHolder.L_services.setVisibility(View.GONE);
                }


                if (searchdetailList.getBusiness_hours1() != null) {
                    String workingHrs = "";
                    try {
                        //  Config.logV("json"+  new Gson().toJson(searchdetailList.getBusiness_hours1() ));
                        String array_json = searchdetailList.getBusiness_hours1().toString();
                        Config.logV("Array---------" + array_json);
                        String data = "";

                        try {


                            //Get the instance of JSONArray that contains JSONObjects
                            JSONArray jsonArray = new JSONArray(array_json);
                            String jsonarry = jsonArray.getString(0);
                            JSONArray jsonArray1 = new JSONArray(jsonarry);

                            //Iterate the jsonArray and print the info of JSONObjects
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
                                String day = "";
                                String date = "";


                                int count = 0;
                                for (int k = 0; k < jsonArray_repeat.length(); k++) {
                                    String repeat = jsonArray_repeat.getString(k);
                                    if (repeat.equalsIgnoreCase("1")) {
                                        day = "Su";
                                        count++;

                                    }
                                    if (repeat.equalsIgnoreCase("2")) {
                                        day = "M";
                                        count++;
                                    }
                                    if (repeat.equalsIgnoreCase("3")) {
                                        day = "T";
                                        count++;
                                    }
                                    if (repeat.equalsIgnoreCase("4")) {
                                        day = "W";
                                        count++;
                                    }
                                    if (repeat.equalsIgnoreCase("5")) {
                                        day = "T";
                                        count++;
                                    }
                                    if (repeat.equalsIgnoreCase("6")) {
                                        day = "F";
                                        count++;
                                    }
                                    if (repeat.equalsIgnoreCase("7")) {
                                        day = "Sa";
                                        count++;
                                    }



                                    date += day + ", ";
                                }


                                // data += " timeslot= "+ sTime+"-"+eTime +"\" \\n Repeat= "+date +"\"  \n\n ";
                                data += date + " " + sTime + "-" + eTime + "\n ";
                                ;

                                workingHrs = data;


                            }
                            Config.logV("Data---------------" + data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    myViewHolder.tv_Workinghrs.setVisibility(View.VISIBLE);
                   // myViewHolder.tv_Workinghrs.setText(workingHrs);


                } else {
                    myViewHolder.tv_Workinghrs.setVisibility(View.GONE);
                }


                myViewHolder.tv_communicate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent iCommunicate=new Intent(v.getContext(),MessageActivity.class);
                        iCommunicate.putExtra("accountID",searchdetailList.getId());
                        iCommunicate.putExtra("provider", searchdetailList.getTitle());
                        context.startActivity(iCommunicate);

                    }
                });


                //    myViewHolder.rating.setRating(Float.valueOf(searchdetailList.getRating()));

                myViewHolder.mLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Config.logV("UNIUE ID----------------" + searchdetailList.getUniqueid());
                        Intent i = new Intent(context, SearchDetailViewFragment.class);
                        i.putExtra("uniqueID", searchdetailList.getUniqueid());
                        context.startActivity(i);
                    }
                });


                break;



            case LOADING:
//                Do nothing
                final LoadingVH LHHolder = (LoadingVH) holder;


                break;
        }

    }
    public  int dpToPx(int dp) {
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

    private static String getMonth(String date) throws ParseException{
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
        public TextView tv_name, tv_location, tv_domain, tv_Date, tv_Futuredate, tv_Workinghrs,tv_Open,tv_WaitTime;
        LinearLayout L_specialization,L_services;
        TextView tv_communicate;
        ImageView profile;
        RatingBar rating;
        RelativeLayout mLayout;
        Button btncheckin;
        RecyclerView mRecycleTypes;

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
            tv_Workinghrs = (TextView) view.findViewById(R.id.txtWorkinghrs);
            tv_Open= (TextView) view.findViewById(R.id.txtOpen);
            tv_WaitTime=(TextView) view.findViewById(R.id.txtWaitTime);
            L_specialization=(LinearLayout) view.findViewById(R.id.specialization);
            tv_communicate=(TextView) view.findViewById(R.id.txtcommunicate);
            mRecycleTypes=(RecyclerView) view.findViewById(R.id.mRecycleTypes);
        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder {
        ProgressBar loadmore_progress;

        public LoadingVH(View itemView) {
            super(itemView);
            loadmore_progress = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);
        }
    }


}
