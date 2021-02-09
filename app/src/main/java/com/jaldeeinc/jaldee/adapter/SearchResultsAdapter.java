package com.jaldeeinc.jaldee.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.activities.ProviderDetailActivity;
import com.jaldeeinc.jaldee.callback.AdapterCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.CircleTransform;
import com.jaldeeinc.jaldee.custom.CustomDailogVerification;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewItalicSemiBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;
import com.jaldeeinc.jaldee.model.SearchListModel;
import com.jaldeeinc.jaldee.model.WorkingModel;
import com.jaldeeinc.jaldee.response.QueueList;
import com.jaldeeinc.jaldee.response.QueueTimeSlotModel;
import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;

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
        View v1 = inflater.inflate(R.layout.search_card, parent, false);
        viewHolder = new SearchResultsAdapter.MyViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final SearchListModel searchdetailList = searchResults.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                final SearchResultsAdapter.MyViewHolder myViewHolder = (SearchResultsAdapter.MyViewHolder) holder;

                setAnimation(myViewHolder.cvCard, position);
                // to set provider name
                if (searchdetailList.getQualification() != null) {
                    myViewHolder.tvSpName.setText(searchdetailList.getTitle());
                } else {
                    myViewHolder.tvSpName.setText(searchdetailList.getTitle());
                }

                if (searchdetailList.getJdn() != null && searchdetailList.getJdn().equalsIgnoreCase("1")){

                    myViewHolder.llJdn.setVisibility(View.VISIBLE);
                } else {

                    myViewHolder.llJdn.setVisibility(View.GONE);
                }

                myViewHolder.llJdn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mAdapterCallback.onMethodJdn(searchdetailList.getUniqueid());

                    }
                });

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

                // to set JDN icon
//                if (searchdetailList.getJdn() != null) {
//                    if (searchdetailList.getJdn().equals("1")) {
//                        myViewHolder.iv_jdnIcon.setVisibility(View.VISIBLE);
//                    } else {
//                        myViewHolder.iv_jdnIcon.setVisibility(View.GONE);
//                    }
//                } else {
//                    myViewHolder.iv_jdnIcon.setVisibility(View.GONE);
//                }
//                myViewHolder.iv_jdnIcon.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        mAdapterCallback.onMethodJdn(searchdetailList.getUniqueid());
//
//                    }
//                });

                // claim info

                showClaimInfo(myViewHolder, searchdetailList);

                // handle verification
                handleJaldeeVerification(myViewHolder, searchdetailList);

                // to set specializations
                setSpecializations(myViewHolder, searchdetailList);

                // to set provider image
                myViewHolder.ivSpImage.setImageDrawable(null); // to make sure images of previous position don't show up in new positions

                if (searchdetailList.getLogo() != null) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Your code to run in GUI thread here
                            myViewHolder.cvImage.setVisibility(View.VISIBLE);
                            myViewHolder.cvShimmer.setVisibility(View.VISIBLE);
                            String url = searchdetailList.getLogo();
                            if (url.contains(" ")) {
                                url = url.replaceAll(" ", "%20");
                            }
                            String finalUrl = url;

//                            if (!((Activity) context).isDestroyed()) {

                                Glide.with(myViewHolder.itemView.getContext())
                                        .load(finalUrl)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .apply(new RequestOptions().error(R.drawable.icon_noimage).circleCrop())
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                //on load failed
                                                myViewHolder.cvShimmer.setVisibility(View.GONE);
                                                myViewHolder.cvImage.setVisibility(View.VISIBLE);
                                                myViewHolder.ivSpImage.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_noimage));
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                //on load success
                                                myViewHolder.cvShimmer.setVisibility(View.GONE);
                                                myViewHolder.cvImage.setVisibility(View.VISIBLE);
                                                return false;
                                            }
                                        })
                                        .into(myViewHolder.ivSpImage);

//                            } else {
//
//                                myViewHolder.cvShimmer.setVisibility(View.GONE);
//                            }


//                            Glide.with(context) //1
//                                    .load(url)
//                                    .placeholder(R.drawable.icon_noimage)
//                                    .error(R.drawable.icon_noimage)
//                                    .skipMemoryCache(true) //2
//                                    .diskCacheStrategy(DiskCacheStrategy.NONE) //3
//                                    .transform(new CircleCrop()) //4
//                                    .into(myViewHolder.ivSpImage);

//                            PicassoTrustAll.getInstance(myViewHolder.ivSpImage.getContext()).cancelRequest(myViewHolder.ivSpImage);
//                            PicassoTrustAll.getInstance(context).load(url).placeholder(R.drawable.icon_noimage).networkPolicy(NetworkPolicy.NO_STORE)
//                                    .error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(myViewHolder.ivSpImage, new Callback() {
//                                @Override
//                                public void onSuccess() {
//
//                                }
//
//                                @Override
//                                public void onError() {
//
//                                    PicassoTrustAll.getInstance(context).load(finalUrl).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(myViewHolder.ivSpImage);
//                                }
//                            });

                        }
                    });
                } else {
                    myViewHolder.cvImage.setVisibility(View.GONE);
                    myViewHolder.cvShimmer.setVisibility(View.GONE);
                }

                // to set services
                if (searchdetailList.getMessage() == null) {
//                    setServices(myViewHolder, searchdetailList);
                } else {
                    if (searchdetailList.getClaimable().equalsIgnoreCase("0")) {
                        if (!searchdetailList.isWaitlistEnabled() && !searchdetailList.isApptEnabled()) {
                            myViewHolder.rlStatus.setVisibility(View.GONE);
                            myViewHolder.llServices.setVisibility(View.GONE);

                        } else {
                            myViewHolder.rlStatus.setVisibility(View.GONE);
                        }
                    } else {

                    }
                }

                // to set rating
                try {
                    if (searchdetailList.getRating() != null) {
                        String rating = searchdetailList.getRating();

                        if (rating != null) {
                            int rate = Math.round(Float.parseFloat(rating));
                            if (rate < 4) {
                                myViewHolder.ratingBar.setVisibility(View.GONE);
                            } else {
                                myViewHolder.ratingBar.setVisibility(View.VISIBLE);
                                myViewHolder.ratingBar.setRating(rate);
                            }
                        } else {
                            myViewHolder.ratingBar.setVisibility(View.GONE);
                        }
                    } else {
                        myViewHolder.ratingBar.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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

                if (searchdetailList.getOnline_profile() != null && searchdetailList.getOnline_profile().equalsIgnoreCase("1")) {

                    myViewHolder.rlStatus.setVisibility(View.VISIBLE);

                    String actionName = getActionType(searchdetailList);

                    if (actionName.equalsIgnoreCase("bookservice")) {

                        myViewHolder.cvAction.setVisibility(View.VISIBLE);
                        myViewHolder.tvText.setText("Book Service");
                        myViewHolder.llEstTime.setVisibility(View.GONE);
                        myViewHolder.llWaitingInLine.setVisibility(View.GONE);
                        setServices(myViewHolder, searchdetailList.getServices());

                    } else if (actionName.equalsIgnoreCase("waitlist")) {

                        myViewHolder.cvAction.setVisibility(View.VISIBLE);
                        if (searchdetailList.isShowToken()) {
                            myViewHolder.tvText.setText("Get Token");
                        } else {
                            myViewHolder.tvText.setText("Check-in");
                        }

                        setServices(myViewHolder, searchdetailList.getServices());

                        if (searchdetailList.getAvail_date() != null) {

                            // est wait time or Date
                            if (formattedDate.equalsIgnoreCase(searchdetailList.getAvail_date())) {
                                myViewHolder.llWaitingInLine.setVisibility(View.VISIBLE);
                                if (!searchdetailList.getCalculationMode().equalsIgnoreCase("NoCalc")) {
                                    myViewHolder.llEstTime.setVisibility(View.VISIBLE);
                                    showWaitingTime(myViewHolder, searchdetailList, null);
                                } else {
                                    myViewHolder.llEstTime.setVisibility(View.GONE);
                                }

                                // people waiting in Line
                                myViewHolder.tvWaitingCount.setText(String.valueOf(searchdetailList.getPersonAhead()));
                                if (searchdetailList.getPersonAhead() == 1) {
                                    myViewHolder.tvWaitingCountHint.setText("Person");
                                } else {
                                    myViewHolder.tvWaitingCountHint.setText("People");
                                }

                            } else {
                                myViewHolder.llEstTime.setVisibility(View.VISIBLE);
                                myViewHolder.llWaitingInLine.setVisibility(View.GONE);
                                showWaitingTime(myViewHolder, searchdetailList, "future");
                            }

                        } else {

                            myViewHolder.llEstTime.setVisibility(View.GONE);
                            myViewHolder.llWaitingInLine.setVisibility(View.GONE);

                        }
                    } else if (actionName.equalsIgnoreCase("appointment")) {

                        myViewHolder.cvAction.setVisibility(View.VISIBLE);
                        myViewHolder.tvText.setText("Appointment");
                        myViewHolder.llEstTime.setVisibility(View.VISIBLE);
                        myViewHolder.llWaitingInLine.setVisibility(View.GONE);

                        setServices(myViewHolder, searchdetailList.getAppt_services());

                        if (searchdetailList.getAvailableDate() != null) {

                            if (formattedDate.equalsIgnoreCase(searchdetailList.getAvailableDate())) {

                                myViewHolder.tvEstWaitTime.setText("Next available on");
                                String time = convertSlotTime(searchdetailList.getAvailableTime().split("-")[0]);
                                myViewHolder.tvTime.setText("Today, " + time);
                            } else {
                                myViewHolder.tvEstWaitTime.setText("Next available on");
                                String time = convertSlotTime(searchdetailList.getAvailableTime().split("-")[0]);
                                myViewHolder.tvTime.setText(convertDate(searchdetailList.getAvailableDate()) + " " + time);

                            }
                        } else {

                            myViewHolder.llEstTime.setVisibility(View.GONE);
                            myViewHolder.llWaitingInLine.setVisibility(View.GONE);
                        }
                    } else if (actionName.equalsIgnoreCase("donation")) {

                        myViewHolder.cvAction.setVisibility(View.VISIBLE);
                        myViewHolder.tvText.setText("Donate");
                        myViewHolder.llEstTime.setVisibility(View.GONE);
                        myViewHolder.llWaitingInLine.setVisibility(View.GONE);

                    } else if (actionName.equalsIgnoreCase("order")) {

                        myViewHolder.cvAction.setVisibility(View.VISIBLE);
                        myViewHolder.tvText.setText("Order");
                        myViewHolder.llEstTime.setVisibility(View.GONE);
                        myViewHolder.llWaitingInLine.setVisibility(View.GONE);
                    } else {

                        myViewHolder.rlStatus.setVisibility(View.GONE);
                    }

//                    if (searchdetailList.isWaitlistEnabled() && searchdetailList.isApptEnabled() && searchdetailList.getDonation_status() != null && searchdetailList.getDonation_status().equalsIgnoreCase("1")) {
//
//                        myViewHolder.cvAction.setVisibility(View.VISIBLE);
//                        myViewHolder.tvText.setText("Book Service");
//                        myViewHolder.llEstTime.setVisibility(View.GONE);
//                        myViewHolder.llWaitingInLine.setVisibility(View.GONE);
//                        setServices(myViewHolder, searchdetailList.getServices());
//
//                    } else if (searchdetailList.isWaitlistEnabled() && searchdetailList.isApptEnabled() && !(searchdetailList.getDonation_status() != null && searchdetailList.getDonation_status().equalsIgnoreCase("1"))) {
//
//                        myViewHolder.cvAction.setVisibility(View.VISIBLE);
//                        myViewHolder.tvText.setText("Book Service");
//                        myViewHolder.llEstTime.setVisibility(View.GONE);
//                        myViewHolder.llWaitingInLine.setVisibility(View.GONE);
//                        setServices(myViewHolder, searchdetailList.getServices());
//
//                    } else if (searchdetailList.isWaitlistEnabled() && searchdetailList.getDonation_status() != null && searchdetailList.getDonation_status().equalsIgnoreCase("1") && !searchdetailList.isApptEnabled()) {
//
//                        myViewHolder.cvAction.setVisibility(View.VISIBLE);
//                        myViewHolder.tvText.setText("Book Service");
//                        myViewHolder.llEstTime.setVisibility(View.GONE);
//                        myViewHolder.llWaitingInLine.setVisibility(View.GONE);
//                        setServices(myViewHolder, searchdetailList.getServices());
//
//                    } else if (searchdetailList.isApptEnabled() && searchdetailList.getDonation_status() != null && searchdetailList.getDonation_status().equalsIgnoreCase("1") && !searchdetailList.isWaitlistEnabled()) {
//
//                        myViewHolder.cvAction.setVisibility(View.VISIBLE);
//                        myViewHolder.tvText.setText("Book Service");
//                        myViewHolder.llEstTime.setVisibility(View.GONE);
//                        myViewHolder.llWaitingInLine.setVisibility(View.GONE);
//                        setServices(myViewHolder, searchdetailList.getAppt_services());
//
//                    } else if (searchdetailList.isWaitlistEnabled() && !searchdetailList.isApptEnabled() && !(searchdetailList.getDonation_status() != null && searchdetailList.getDonation_status().equalsIgnoreCase("1"))) {
//
//                        myViewHolder.cvAction.setVisibility(View.VISIBLE);
//                        if (searchdetailList.isShowToken()) {
//                            myViewHolder.tvText.setText("Get Token");
//                        } else {
//                            myViewHolder.tvText.setText("CheckIn");
//                        }
//
//                        setServices(myViewHolder, searchdetailList.getServices());
//
//                        if (searchdetailList.getAvail_date() != null) {
//
//                            // est wait time or Date
//                            if (formattedDate.equalsIgnoreCase(searchdetailList.getAvail_date())) {
//                                myViewHolder.llWaitingInLine.setVisibility(View.VISIBLE);
//                                if (!searchdetailList.getCalculationMode().equalsIgnoreCase("NoCalc")) {
//                                    myViewHolder.llEstTime.setVisibility(View.VISIBLE);
//                                    showWaitingTime(myViewHolder, searchdetailList, null);
//                                } else {
//                                    myViewHolder.llEstTime.setVisibility(View.GONE);
//                                }
//
//                                // people waiting in Line
//                                myViewHolder.tvWaitingCount.setText(String.valueOf(searchdetailList.getPersonAhead()));
//                                if (searchdetailList.getPersonAhead() == 1) {
//                                    myViewHolder.tvWaitingCountHint.setText("Person");
//                                } else {
//                                    myViewHolder.tvWaitingCountHint.setText("People");
//                                }
//
//                            } else {
//                                myViewHolder.llEstTime.setVisibility(View.VISIBLE);
//                                myViewHolder.llWaitingInLine.setVisibility(View.GONE);
//                                showWaitingTime(myViewHolder, searchdetailList, "future");
//                            }
//
//                        } else {
//
//                            myViewHolder.llEstTime.setVisibility(View.GONE);
//                            myViewHolder.llWaitingInLine.setVisibility(View.GONE);
//
//                        }
//
//                    } else if (searchdetailList.isApptEnabled() && !searchdetailList.isWaitlistEnabled() && !(searchdetailList.getDonation_status() != null && searchdetailList.getDonation_status().equalsIgnoreCase("1"))) {
//
//                        myViewHolder.cvAction.setVisibility(View.VISIBLE);
//                        myViewHolder.tvText.setText("Get Appointment");
//                        myViewHolder.llEstTime.setVisibility(View.VISIBLE);
//                        myViewHolder.llWaitingInLine.setVisibility(View.GONE);
//
//                        setServices(myViewHolder, searchdetailList.getAppt_services());
//
//                        if (searchdetailList.getAvailableDate() != null) {
//
//                            if (formattedDate.equalsIgnoreCase(searchdetailList.getAvailableDate())) {
//
//                                myViewHolder.tvEstWaitTime.setText("Next available on");
//                                String time = convertSlotTime(searchdetailList.getAvailableTime().split("-")[0]);
//                                myViewHolder.tvTime.setText("Today, " + time);
//                            } else {
//                                myViewHolder.tvEstWaitTime.setText("Next available on");
//                                String time = convertSlotTime(searchdetailList.getAvailableTime().split("-")[0]);
//                                myViewHolder.tvTime.setText(convertDate(searchdetailList.getAvailableDate()) + " " + time);
//
//                            }
//                        } else {
//
//                            myViewHolder.llEstTime.setVisibility(View.GONE);
//                            myViewHolder.llWaitingInLine.setVisibility(View.GONE);
//                        }
//
//                    } else if (searchdetailList.getDonation_status() != null && searchdetailList.getDonation_status().equalsIgnoreCase("1") && !searchdetailList.isWaitlistEnabled() && !searchdetailList.isApptEnabled()) {
//
//                        myViewHolder.cvAction.setVisibility(View.VISIBLE);
//                        myViewHolder.tvText.setText("Donate");
//                        myViewHolder.llEstTime.setVisibility(View.GONE);
//                        myViewHolder.llWaitingInLine.setVisibility(View.GONE);
//
//                    } else if (searchdetailList.getOrderEnabled() == 1 && !(searchdetailList.getDonation_status() != null && searchdetailList.getDonation_status().equalsIgnoreCase("1")) && !searchdetailList.isWaitlistEnabled() && !searchdetailList.isApptEnabled()) {
//
//                        myViewHolder.cvAction.setVisibility(View.VISIBLE);
//                        myViewHolder.tvText.setText("Order");
//                        myViewHolder.llEstTime.setVisibility(View.GONE);
//                        myViewHolder.llWaitingInLine.setVisibility(View.GONE);
//
//                    } else {
//
//                        myViewHolder.rlStatus.setVisibility(View.GONE);
//                    }

                } else {
                    myViewHolder.rlStatus.setVisibility(View.GONE);
                }


                myViewHolder.cvCard.setOnClickListener(new View.OnClickListener() {
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
                        iCheckIn.putExtra("locationId", searchdetailList.getLocation_id1());
                        if (searchdetailList.getOrderEnabled() == 1) {
                            iCheckIn.putExtra("isOrderEnabled", true);
                        } else {
                            iCheckIn.putExtra("isOrderEnabled", false);
                        }
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

    private String getActionType(SearchListModel searchList) {

        int actionsCount = 0;
        String actionName = "";

        if (searchList.isWaitlistEnabled()) {

            actionName = "waitlist";
            actionsCount = actionsCount + 1;
        }

        if (searchList.isApptEnabled()) {

            actionName = "appointment";
            actionsCount = actionsCount + 1;

        }

        if (searchList.getDonation_status() != null && searchList.getDonation_status().equalsIgnoreCase("1")) {

            actionName = "donation";
            actionsCount = actionsCount + 1;

        }

        if (searchList.getOrderEnabled() == 1) {

            actionName = "order";
            actionsCount = actionsCount + 1;

        }
        if (actionsCount > 1) {

            actionName = "bookservice";
        }

        return actionName;
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
                CustomDailogVerification cdd = new CustomDailogVerification(context, searchdetailList.getYnw_verified_level(), searchdetailList.getTitle());
                cdd.setCanceledOnTouchOutside(true);
                cdd.show();
//                mAdapterCallback.onMethodJaldeeLogo(searchdetailList.getYnw_verified_level(), searchdetailList.getTitle());
            }
        });
    }

    public void setSpecializations(SearchResultsAdapter.MyViewHolder myViewHolder, final SearchListModel searchdetailList) {
        if (searchdetailList.getSpecialization_displayname() != null) {
            final List<String> list_spec = searchdetailList.getSpecialization_displayname();

            if (list_spec.size() > 0) {

                myViewHolder.llSpecializations.setVisibility(View.VISIBLE);
                if (list_spec.size() == 1) {
                    myViewHolder.tvSpOne.setVisibility(View.VISIBLE);
                    myViewHolder.tvSpOne.setText(list_spec.get(0));
                    myViewHolder.tvSpTwo.setVisibility(View.GONE);
                } else if (list_spec.size() == 2) {
                    myViewHolder.tvSpOne.setVisibility(View.VISIBLE);
                    myViewHolder.tvSpOne.setText(list_spec.get(0) + ", ");
                    myViewHolder.tvSpTwo.setVisibility(View.VISIBLE);
                    myViewHolder.tvSpTwo.setText(list_spec.get(1));
                } else {
                    myViewHolder.tvSpOne.setVisibility(View.VISIBLE);
                    myViewHolder.tvSpOne.setText(list_spec.get(0) + ", ");
                    myViewHolder.tvSpTwo.setVisibility(View.VISIBLE);
                    myViewHolder.tvSpTwo.setText(list_spec.get(1));
                }

            } else {
                myViewHolder.llSpecializations.setVisibility(View.GONE);
            }
        } else {

            myViewHolder.llSpecializations.setVisibility(View.GONE);
        }
    }


    private void setServices(MyViewHolder myViewHolder, ArrayList servicesList) {

        if (servicesList != null) {
            final ArrayList serviceNames = new ArrayList();
            final ArrayList serviceTypes = new ArrayList();
            final ArrayList serviceIds = new ArrayList();
            serviceNames.clear();
            try {
                try {
                    JSONArray jsonArray = new JSONArray(servicesList);
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
                myViewHolder.llServices.setVisibility(View.GONE);
            }
        } else {

            myViewHolder.llServices.setVisibility(View.GONE);
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
            myViewHolder.tvTime.setText(secondWord);
        } else {

            String firstWord = "Est wait time";
            String secondWord = Config.getTimeinHourMinutes(searchdetailList.getQueueWaitingTime());
            myViewHolder.tvEstWaitTime.setText(firstWord);
            myViewHolder.tvTime.setText(secondWord);
        }

    }

    private void showClaimInfo(MyViewHolder myViewHolder, SearchListModel searchdetailList) {
        if (searchdetailList.getClaimable() != null) {
            if (searchdetailList.getClaimable().equals("1")) {
//                myViewHolder.cvCard.setCardBackgroundColor(context.getResources().getColor(R.color.unclaimed));
                myViewHolder.cvClaimNow.setVisibility(View.VISIBLE);
                myViewHolder.rlStatus.setVisibility(View.GONE);
                myViewHolder.llServices.setVisibility(View.GONE);
                myViewHolder.llLoader.setVisibility(View.VISIBLE);
            } else {
//                myViewHolder.cvCard.setCardBackgroundColor(context.getResources().getColor(R.color.white));
                myViewHolder.llLoader.setVisibility(View.GONE);
                myViewHolder.cvClaimNow.setVisibility(View.GONE);
                myViewHolder.rlStatus.setVisibility(View.VISIBLE);
            }
        }

        myViewHolder.cvClaimNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    boolean claimable = false;
                    if (searchdetailList.getClaimable() != null && searchdetailList.getClaimable().equalsIgnoreCase("1")) {
                        claimable = true;
                    }
                    String accountId = "0";
                    accountId = searchdetailList.getId().split("-")[0];
                    intent.setData(Uri.parse(Constants.URL + "business/signup?" + "claimable=" + claimable + "&accountId=" + accountId + "&sector=" + searchdetailList.getSectorname() + "&subSector=" + searchdetailList.getSub_sector()));
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

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

        for (SearchListModel searchResult : this.searchResults) {
            for (SearchListModel result : moveResults) {
                if (searchResult.getId().equalsIgnoreCase(result.getId())) {
                    moveResults.remove(result);
                }
            }
        }
        for (SearchListModel result : moveResults) {
            add(result);
        }
    }

    public void updateData(List<SearchListModel> results) {

        results = results == null ? new ArrayList<>() : results;
        this.searchResults = results;
        notifyDataSetChanged();
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

        private ImageView ivSpImage, ivJaldeeverified, iv_jdnIcon;
        private CustomTextViewBold tvSpName;
        private CustomTextViewItalicSemiBold tvTime, tvWaitingCount, tvWaitingCountHint;
        private CustomTextViewMedium tvLocationName, tvEstWaitTime, tvSpOne, tvSpTwo, tvWaitingInLine;
        private RatingBar ratingBar;
        private LinearLayout llServices, llSpecializations, llWaitingInLine, llEstTime, llLoader,llJdn;
        private RelativeLayout rlStatus;
        private CustomTextViewItalicSemiBold tvServiceOne, tvServiceTwo, tvServiceThree;
        private CustomTextViewSemiBold tvText;
        private CardView cvClaimNow, cvAction, cvCard, cvImage, cvShimmer;

        public MyViewHolder(View view) {
            super(view);

            ivSpImage = view.findViewById(R.id.iv_spImage);
            tvSpName = view.findViewById(R.id.tv_spName);
            tvLocationName = view.findViewById(R.id.tv_locationName);
            ivJaldeeverified = view.findViewById(R.id.iv_jaldeeVerified);
            tvTime = view.findViewById(R.id.tv_time);
            tvEstWaitTime = view.findViewById(R.id.tv_estWaitTime);
            ratingBar = view.findViewById(R.id.rb_ratingBar);
            llServices = view.findViewById(R.id.ll_services);
            tvServiceOne = view.findViewById(R.id.tv_serviceOne);
            tvServiceTwo = view.findViewById(R.id.tv_serviceTwo);
            tvServiceThree = view.findViewById(R.id.tv_serviceThree);
            rlStatus = view.findViewById(R.id.rl_status);
            cvClaimNow = view.findViewById(R.id.cv_claimNow);
            llSpecializations = view.findViewById(R.id.ll_specializations);
            tvSpOne = view.findViewById(R.id.tv_spOne);
            tvSpTwo = view.findViewById(R.id.tv_spTwo);
//            iv_jdnIcon = view.findViewById(R.id.txtjdn);
            llWaitingInLine = view.findViewById(R.id.ll_waitingInLine);
            tvWaitingCount = view.findViewById(R.id.tv_waitingCount);
            tvWaitingCountHint = view.findViewById(R.id.tv_waitingCountHint);
            tvWaitingInLine = view.findViewById(R.id.tv_waitingInLine);
            cvAction = view.findViewById(R.id.cv_Action);
            tvText = view.findViewById(R.id.tv_text);
            llEstTime = view.findViewById(R.id.ll_estTime);
            cvCard = view.findViewById(R.id.cv_card);
            cvImage = view.findViewById(R.id.cv_image);
            llLoader = view.findViewById(R.id.ll_loader);
            cvShimmer = view.findViewById(R.id.cv_shimmer);
            llJdn = view.findViewById(R.id.ll_jdn);

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
