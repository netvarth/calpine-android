package com.jaldeeinc.jaldee.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.activities.ProviderDetailActivity;
import com.jaldeeinc.jaldee.activities.SwipeGalleryImage;
import com.jaldeeinc.jaldee.callback.AdapterCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CircleTransform;
import com.jaldeeinc.jaldee.custom.CustomItalicTextViewNormal;
import com.jaldeeinc.jaldee.custom.CustomTextViewItalicSemiBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewRegularItalic;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;
import com.jaldeeinc.jaldee.model.SearchListModel;
import com.jaldeeinc.jaldee.model.WorkingModel;
import com.jaldeeinc.jaldee.response.QueueList;
import com.jaldeeinc.jaldee.response.QueueTimeSlotModel;
import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.jaldeeinc.jaldee.widgets.CustomDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
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

public class SearchResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
    ArrayList<Boolean> checkins_flag;
    ArrayList<Boolean> appts_flag;
    ArrayList<Boolean> dnts_flag;
    boolean isLoading;
    private int lastPosition = -1;
    final ArrayList serviceNamesDonations = new ArrayList();
    final ArrayList dntServiceIds = new ArrayList();
    final ArrayList serviceNamesDonationsMin = new ArrayList();
    final ArrayList serviceNamesDonationsMax = new ArrayList();
    final ArrayList serviceNamesDonationsMultiples = new ArrayList();

    public SearchResultsAdapter(Activity activity, Context context, AdapterCallback callback, String uniqueID, List<QueueList> mQueueList) {
        this.context = context;
        searchResults = new ArrayList<>();
        checkins_flag = new ArrayList<>();
        dnts_flag = new ArrayList<>();
        appts_flag = new ArrayList<>();
        this.mAdapterCallback = callback;
        this.activity = activity;
        this.uniqueID = uniqueID;
        this.mQueueList = mQueueList;
        this.isLoading = isLoading;
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
                viewHolder = new SearchResultsAdapter.LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.search_item, parent, false);
        viewHolder = new SearchResultsAdapter.MyViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final SearchListModel searchdetailList = searchResults.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                final SearchResultsAdapter.MyViewHolder myViewHolder = (SearchResultsAdapter.MyViewHolder) holder;

                setAnimation(myViewHolder.cvSearch, position);
                // to set provider name
                if (searchdetailList.getQualification() != null) {
                    myViewHolder.tvSpName.setText(searchdetailList.getTitle());
                } else {
                    myViewHolder.tvSpName.setText(searchdetailList.getTitle());
                }

                // to set locationName
                if (searchdetailList.getPlace1() != null) {
                    myViewHolder.tvLocationName.setVisibility(View.VISIBLE);
                    Double distance = Double.valueOf(searchdetailList.getDistance()) * 1.6;
                    if (distance >= 1) {
                        myViewHolder.tvLocationName.setText(searchdetailList.getPlace1() + " ( " + String.format("%.2f", distance) + " km )");
                    } else {
                        myViewHolder.tvLocationName.setText(searchdetailList.getPlace1() + " (<1 km) ");
                    }
                } else {
                    myViewHolder.tvLocationName.setVisibility(View.GONE);
                }

                // claim info
                showClaimInfo(myViewHolder, searchdetailList);

                // handle verification
                handleJaldeeVerification(myViewHolder, searchdetailList);

                // to set provider image
                if (searchdetailList.getLogo() != null) {
                    PicassoTrustAll.getInstance(context).load(searchdetailList.getLogo()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(myViewHolder.ivSpImage);

                } else {

                    PicassoTrustAll.getInstance(context).load(R.drawable.icon_noimage).fit().into(myViewHolder.ivSpImage);
                }

                // to set services
                if (searchdetailList.getMessage() == null) {
                    myViewHolder.tvInvalidAccount.setVisibility(View.GONE);
                    setServices(myViewHolder, searchdetailList);
                } else {
                    if (searchdetailList.getClaimable().equalsIgnoreCase("0")) {
                        if (!searchdetailList.isWaitlistEnabled() && !searchdetailList.isApptEnabled()) {
                            myViewHolder.llEstTime.setVisibility(View.GONE);
                            myViewHolder.llServices.setVisibility(View.GONE);
                            myViewHolder.tvInvalidAccount.setVisibility(View.VISIBLE);
                            myViewHolder.tvInvalidAccount.setText(searchdetailList.getMessage() + "..!");
                        } else {
                            myViewHolder.llEstTime.setVisibility(View.GONE);
                        }
                    } else {

                    }
                }

                // to set rating
                try {
                    String rating = searchdetailList.getRating();

                    if (rating != null) {
                        int rate = Math.round(Float.parseFloat(rating));
                        if (rate < 4) {
                            myViewHolder.ratingBar.setVisibility(View.GONE);
                        } else {
                            myViewHolder.ratingBar.setVisibility(View.VISIBLE);
                            myViewHolder.ratingBar.setRating(rate);
                        }
                    }
                } catch (Exception e) {

                }

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

                if (searchdetailList.getOnline_profile() != null && searchdetailList.getOnline_profile().equalsIgnoreCase("1") && searchdetailList.isWaitlistEnabled()) {

                    if (searchdetailList.getAvail_date() != null) {
                        myViewHolder.llEstTime.setVisibility(View.VISIBLE);
                        if (searchdetailList.isOnlineCheckIn()) {
                            if (searchdetailList.isShowToken()) {

                                if (formattedDate.equalsIgnoreCase(searchdetailList.getAvail_date())) {
                                    showWaitingTime(myViewHolder, searchdetailList, null);
                                } else {
                                    showWaitingTime(myViewHolder, searchdetailList, "future");
                                }

                            } else {

                                if (searchdetailList.getShow_waiting_time().equalsIgnoreCase("1")) { // Conventional
                                    if (formattedDate.equalsIgnoreCase(searchdetailList.getAvail_date())) {
                                        showWaitingTime(myViewHolder, searchdetailList, null);
                                    } else {
                                        showWaitingTime(myViewHolder, searchdetailList, "future");
                                    }

                                } else {

                                    myViewHolder.tvEstWaitTime.setText("Waiting in line");
                                    myViewHolder.tvTimeOrPeople.setText(String.valueOf(searchdetailList.getPersonAhead()));
                                    myViewHolder.tvTimeOrPeopleHint.setVisibility(View.VISIBLE);
                                    myViewHolder.tvTimeOrPeopleHint.setText("people");
                                }

                            }
                        }
                    }

                } else if (searchdetailList.getOnline_profile() != null && searchdetailList.getOnline_profile().equals("1") && searchdetailList.isApptEnabled()) {

                    myViewHolder.llEstTime.setVisibility(View.VISIBLE);
                    if (searchdetailList.getAvailableDate() != null) {

                        if (formattedDate.equalsIgnoreCase(searchdetailList.getAvailableDate())) {

                            myViewHolder.tvEstWaitTime.setText("Next available on");
                            String time = convertSlotTime(searchdetailList.getAvailableTime().split("-")[0]);
                            myViewHolder.tvTimeOrPeople.setText("Today " + "\n" + time);
                            myViewHolder.tvTimeOrPeopleHint.setVisibility(View.GONE);
                        } else {
                            myViewHolder.tvEstWaitTime.setText("Next available on");
                            String time = convertSlotTime(searchdetailList.getAvailableTime().split("-")[0]);
                            myViewHolder.tvTimeOrPeople.setText(convertDate(searchdetailList.getAvailableDate()) + " " + "\n" + time);
                            myViewHolder.tvTimeOrPeopleHint.setVisibility(View.GONE);

                        }
                    }

                } else {
                    myViewHolder.llEstTime.setVisibility(View.GONE);
                }

                myViewHolder.cvSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent iCheckIn = new Intent(context, ProviderDetailActivity.class);
                        if (searchdetailList.getmLoc() != null) {
                            iCheckIn.putExtra("serviceId", Integer.parseInt(searchdetailList.getmLoc()));
                        }
                        iCheckIn.putExtra("uniqueID", searchdetailList.getUniqueid());
                        iCheckIn.putExtra("accountID", searchdetailList.getId());
                        iCheckIn.putExtra("googlemap", searchdetailList.getLocation1());
                        iCheckIn.putExtra("from", "checkin");
                        iCheckIn.putExtra("title", searchdetailList.getTitle());
                        iCheckIn.putExtra("place", searchdetailList.getPlace1());
                        iCheckIn.putExtra("sector", searchdetailList.getSectorname());
                        iCheckIn.putExtra("subsector", searchdetailList.getSub_sector());
                        iCheckIn.putExtra("terminology", termilogy);
                        iCheckIn.putExtra("isshowtoken", searchdetailList.isShowToken());
                        iCheckIn.putExtra("getAvail_date", searchdetailList.getAvail_date());
                        iCheckIn.putExtra("virtualservice", searchdetailList.getVirtual_service_status());
                        context.startActivity(iCheckIn);
                    }
                });


                break;
            case LOADING:
//                Do nothing
                final LoadingVH LHHolder = (LoadingVH) holder;
                break;
        }
    }

    private void handleJaldeeVerification(MyViewHolder myViewHolder, final SearchListModel searchdetailList) {
        if (searchdetailList.getYnw_verified_level() != null) {
            if (searchdetailList.getYnw_verified() == 1) {
                if (searchdetailList.getYnw_verified_level().equalsIgnoreCase("2")) {
                    myViewHolder.ivJaldeeverified.setVisibility(View.VISIBLE);
                    myViewHolder.ivJaldeeverified.setImageResource(R.drawable.jaldee_basic);
                }
                if (searchdetailList.getYnw_verified_level().equalsIgnoreCase("3")) {
                    myViewHolder.ivJaldeeverified.setVisibility(View.VISIBLE);
                    myViewHolder.ivJaldeeverified.setImageResource(R.drawable.jaldee_basicplus);
                }
                if (searchdetailList.getYnw_verified_level().equalsIgnoreCase("4")) {
                    myViewHolder.ivJaldeeverified.setVisibility(View.VISIBLE);
                    myViewHolder.ivJaldeeverified.setImageResource(R.drawable.jaldee_adv);
                }
            } else {
                myViewHolder.ivJaldeeverified.setVisibility(View.INVISIBLE);
            }
        } else {
            myViewHolder.ivJaldeeverified.setVisibility(View.INVISIBLE);
        }
        myViewHolder.ivJaldeeverified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog cdd = new CustomDialog(context, searchdetailList.getYnw_verified_level(), searchdetailList.getTitle());
                cdd.setCanceledOnTouchOutside(true);
                cdd.show();
//                mAdapterCallback.onMethodJaldeeLogo(searchdetailList.getYnw_verified_level(), searchdetailList.getTitle());
            }
        });
    }


    private void setServices(MyViewHolder myViewHolder, SearchListModel searchdetailList) {

        if (searchdetailList.getServices() != null) {
            final ArrayList serviceNames = new ArrayList();
            final ArrayList serviceTypes = new ArrayList();
            final ArrayList serviceIds = new ArrayList();
            serviceNames.clear();
            try {
                try {
                    JSONArray jsonArray = new JSONArray(searchdetailList.getServices());
                    String jsonArry = jsonArray.getString(0);
                    JSONArray jsonArray1 = new JSONArray(jsonArry);
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        JSONObject jsonObject = jsonArray1.getJSONObject(i);
                        String name = jsonObject.optString("name");
                        String serviceType = jsonObject.optString("serviceType");
                        String serviceId = jsonObject.optString("id");

                        serviceNames.add(i, name);
                        serviceTypes.add(i, serviceType);
                        serviceIds.add(i, serviceId);
                        Log.i("sar", serviceNames.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (serviceNames.size() > 0) {

                myViewHolder.tvInvalidAccount.setVisibility(View.GONE);
                myViewHolder.llServices.setVisibility(View.VISIBLE);

                if (serviceNames.size() == 1) {

                    myViewHolder.tvServiceOne.setVisibility(View.VISIBLE);
                    myViewHolder.tvServiceTwo.setVisibility(View.GONE);
                    myViewHolder.tvServiceThree.setVisibility(View.GONE);
                    myViewHolder.tvServiceOne.setText(headerFormat(serviceNames.get(0).toString()));

                } else if (serviceNames.size() > 2 && serviceNames.get(0).toString().length() <= 15 && serviceNames.get(1).toString().length() <= 15) {

                    if (serviceNames.size() == 2) {

                        myViewHolder.tvServiceOne.setVisibility(View.VISIBLE);
                        myViewHolder.tvServiceTwo.setVisibility(View.VISIBLE);
                        myViewHolder.tvServiceThree.setVisibility(View.GONE);
                        String name1 = serviceNames.get(0).toString();
                        name1 = name1.substring(0, 1).toUpperCase() + name1.substring(1).toLowerCase();
                        myViewHolder.tvServiceOne.setText(name1 + ",");
                        myViewHolder.tvServiceTwo.setText(headerFormat(serviceNames.get(1).toString()));

                    } else {
                        myViewHolder.tvServiceOne.setVisibility(View.VISIBLE);
                        myViewHolder.tvServiceTwo.setVisibility(View.VISIBLE);
                        myViewHolder.tvServiceThree.setVisibility(View.VISIBLE);
                        String name1 = serviceNames.get(0).toString();
                        name1 = name1.substring(0, 1).toUpperCase() + name1.substring(1).toLowerCase();
                        myViewHolder.tvServiceOne.setText(name1 + ",");
                        String name2 = serviceNames.get(1).toString();
                        name2 = name2.substring(0, 1).toUpperCase() + name2.substring(1).toLowerCase();
                        myViewHolder.tvServiceTwo.setText(name2 + ",");
                        String name3 = serviceNames.get(2).toString();
                        name3 = name3.substring(0, 1).toUpperCase() + name3.substring(1).toLowerCase();
                        myViewHolder.tvServiceThree.setText(name3);
                    }
                } else {

                    for (int i = 0; i < serviceNames.size(); i++) {

                        if (i == 0) {

                            myViewHolder.tvServiceOne.setVisibility(View.VISIBLE);
                            myViewHolder.tvServiceTwo.setVisibility(View.GONE);
                            String name1 = serviceNames.get(0).toString();
                            name1 = name1.substring(0, 1).toUpperCase() + name1.substring(1).toLowerCase();
                            myViewHolder.tvServiceOne.setText(name1);
                            myViewHolder.tvServiceThree.setVisibility(View.GONE);

                            break;
                        }
                    }

                }
            } else {
                myViewHolder.tvInvalidAccount.setVisibility(View.GONE);
                myViewHolder.llServices.setVisibility(View.GONE);
            }
        }

    }

    public void showWaitingTime(MyViewHolder myViewHolder, SearchListModel searchdetailList, String type) {
        if (searchdetailList.getServiceTime() != null) {
            String firstWord = "Next Available on";
            String secondWord = "";
            if (type != null) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date = null;
                try {
                    date = format.parse(searchdetailList.getAvail_date());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String day = (String) DateFormat.format("dd", date);
                String monthString = (String) DateFormat.format("MMM", date);

                secondWord = monthString + " " + day + ", " + searchdetailList.getServiceTime();
            } else {
                secondWord = "Today," + "\n" + searchdetailList.getServiceTime();
            }
            Spannable spannable = new SpannableString(firstWord + secondWord);
            myViewHolder.tvEstWaitTime.setText(firstWord);
            myViewHolder.tvTimeOrPeople.setText(secondWord);
            myViewHolder.tvTimeOrPeopleHint.setVisibility(View.GONE);
        } else {

            String firstWord = "Est wait time";
            String secondWord = " \n " + Config.getTimeinHourMinutes(searchdetailList.getQueueWaitingTime());
            myViewHolder.tvEstWaitTime.setText(firstWord);
            myViewHolder.tvTimeOrPeople.setText(String.valueOf(searchdetailList.getQueueWaitingTime()));
            myViewHolder.tvTimeOrPeopleHint.setVisibility(View.VISIBLE);
            myViewHolder.tvTimeOrPeopleHint.setText("Mins");

        }

    }

    private void showClaimInfo(MyViewHolder myViewHolder, SearchListModel searchdetailList) {
        if (searchdetailList.getClaimable() != null) {
            if (searchdetailList.getClaimable().equals("1")) {
                myViewHolder.cvClaimNow.setVisibility(View.VISIBLE);
                myViewHolder.llEstTime.setVisibility(View.GONE);
                myViewHolder.llServices.setVisibility(View.GONE);
                myViewHolder.tvInvalidAccount.setVisibility(View.GONE);
            } else {
                myViewHolder.cvClaimNow.setVisibility(View.GONE);
                myViewHolder.llEstTime.setVisibility(View.VISIBLE);
                myViewHolder.tvInvalidAccount.setVisibility(View.GONE);
            }
        }

        myViewHolder.cvClaimNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(Constants.URL));
                context.startActivity(intent);

            }
        });

    }


    public static String convertDate(String date) {

        String finalDate = "";
        Date selectedDate = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            selectedDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (DateUtils.isToday(selectedDate.getTime())) {
            finalDate = "Today";
        } else {
            Format f = new SimpleDateFormat("MMM dd");
            finalDate = f.format(selectedDate);
        }

        return finalDate;
    }

    public static String convertTime(String time) {

        String formattedTime = "";
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            final Date dateObj = sdf.parse(time);
            time = new SimpleDateFormat("hh:mm aa").format(dateObj);
            formattedTime = time.replace("am", "AM").replace("pm", "PM");

        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return formattedTime;
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


    protected class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivSpImage, ivJaldeeverified;
        private CustomTextViewSemiBold tvSpName;
        private CustomTextViewItalicSemiBold tvTimeOrPeople, tvTimeOrPeopleHint;
        private CustomTextViewMedium tvSpecializations, tvLocationName, tvEstWaitTime, tvInvalidAccount;
        private RatingBar ratingBar;
        private LinearLayout llServices, llEstTime;
        private CustomItalicTextViewNormal tvServiceOne, tvServiceTwo, tvServiceThree;
        private CardView cvSearch, cvClaimNow;

        public MyViewHolder(View view) {
            super(view);

            ivSpImage = view.findViewById(R.id.iv_spImage);
            tvSpName = view.findViewById(R.id.tv_spName);
            tvSpecializations = view.findViewById(R.id.tv_specializations);
            tvLocationName = view.findViewById(R.id.tv_locationName);
            ivJaldeeverified = view.findViewById(R.id.iv_jaldeeVerified);
            tvTimeOrPeople = view.findViewById(R.id.tv_timeOrCount);
            tvTimeOrPeopleHint = view.findViewById(R.id.tv_timeOrCountHint);
            tvEstWaitTime = view.findViewById(R.id.tv_estWaitTime);
            ratingBar = view.findViewById(R.id.rb_ratingBar);
            llServices = view.findViewById(R.id.ll_services);
            tvServiceOne = view.findViewById(R.id.tv_serviceOne);
            tvServiceTwo = view.findViewById(R.id.tv_serviceTwo);
            tvServiceThree = view.findViewById(R.id.tv_serviceThree);
            cvSearch = view.findViewById(R.id.cv_search);
            llEstTime = view.findViewById(R.id.ll_estTime);
            tvInvalidAccount = view.findViewById(R.id.tv_invalidAccount);
            cvClaimNow = view.findViewById(R.id.cv_claimNow);

        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder {
        ProgressBar loadmore_progress;

        public LoadingVH(View itemView) {
            super(itemView);
            loadmore_progress = itemView.findViewById(R.id.loadmore_progress);
        }
    }

    public String convertSlotTime(String date) {
        final String OLD_FORMAT = "HH:mm";
        final String NEW_FORMAT = "hh:mm aa";

        String slotTime = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
            Date d = sdf.parse(date);
            sdf.applyPattern(NEW_FORMAT);
            slotTime = sdf.format(d);
        } catch (ParseException e) {
            // TODO: handle exception
        }
        String str = slotTime.replace("am", "AM").replace("pm", "PM");
        return str;
    }


    private String formatDate(String availableDate) {

        String dtStart = availableDate;
        Date dateParse = null;
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dateParse = format1.parse(dtStart);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat df = new SimpleDateFormat("MMM dd");
        String nAvailDate = df.format(dateParse);

        return nAvailDate;
    }


    public String headerFormat(String n) {

        String name = n;
        name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        return name;
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }


}
