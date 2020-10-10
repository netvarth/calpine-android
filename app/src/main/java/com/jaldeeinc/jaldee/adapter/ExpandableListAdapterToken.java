package com.jaldeeinc.jaldee.adapter;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.CheckinShareLocation;
import com.jaldeeinc.jaldee.activities.ProviderDetailActivity;
import com.jaldeeinc.jaldee.callback.ActiveAdapterOnCallback;
import com.jaldeeinc.jaldee.custom.MeetingDetailsWindow;
import com.jaldeeinc.jaldee.custom.MeetingInfo;
import com.jaldeeinc.jaldee.response.SearchAWsResponse;
import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.jaldeeinc.jaldee.callback.HistoryAdapterCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTypefaceSpan;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.FavouriteModel;
import com.jaldeeinc.jaldee.response.TeleServiceCheckIn;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by sharmila on 14/1/19.
 */
public class ExpandableListAdapterToken extends BaseExpandableListAdapter implements LocationListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private Context mContext;
    private List<String> headerData; // header titles
    // Child data in format of header title, child title
    private HashMap<String, ArrayList<ActiveCheckIn>> child;
    Activity activity;
    HistoryAdapterCallback callback;
    ActiveAdapterOnCallback callbacks;
    String header;
    ArrayList<FavouriteModel> FavList;
    boolean mTodayFlag = false, mOldFlag = false, mFutureFlag = false;
    Date date1, date2;
    String qwe, asd, pollingTime;
    Date qwee, asdd;
    String latValue, longValue;
    LocationManager locationManager;
    ActiveCheckIn activelistLatest;
    boolean liveTrackSwitchLatest;
    double dist;
    long mins;
    List<SearchAWsResponse> mSearchResp = new ArrayList<>();
    SearchViewDetail mBusinessDataList;
    String latitude,longitude;
    boolean isChecked = true;
    private TeleServiceCheckIn teleServiceCheckInResponse;
    private TeleServiceCheckIn gMeetResponse;
    private TeleServiceCheckIn zoomResponse;
    private TeleServiceCheckIn whatsappResponse;
    private TeleServiceCheckIn phoneresponse;
    private MeetingDetailsWindow meetingDetailsWindow;
    private MeetingInfo meetingInfo;


//    // Used in checking for runtime permissions.
//    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
//
//    // The BroadcastReceiver used to listen from broadcasts from the service.
//    private CheckinShareLocation.MyReceiver myReceiver;
//
//    // A reference to the service used to get location updates.
//    private LocationUpdatesService mService = null;
//
//    // Tracks the bound state of the service.
//    private boolean mBound = false;
//
//
//    private final ServiceConnection mServiceConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
//            mService = binder.getService();
//            mBound = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            mService = null;
//            mBound = false;
//        }
//    };


    public ExpandableListAdapterToken(ArrayList<FavouriteModel> mFavList, Context mContext, Activity mActivity, HistoryAdapterCallback callback, List<String> listDataHeader, HashMap<String, ArrayList<ActiveCheckIn>> listChildData, boolean mTodayFlag, boolean mFutureFlag, boolean mOldFlag, LocationManager locationManager, ActiveAdapterOnCallback callbacks) {
        this.mContext = mContext;
        this.headerData = listDataHeader;
        this.child = listChildData;
        this.activity = mActivity;
        this.callback = callback;
        this.callbacks = callbacks;


        this.FavList = mFavList;
        this.mFutureFlag = mFutureFlag;
        this.mTodayFlag = mTodayFlag;
        this.mOldFlag = mOldFlag;
        this.locationManager = locationManager;

    }


    @Override
    public int getGroupCount() {
        // Get header size
        return this.headerData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // return children count

        return this.child.get(this.headerData.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        // Get header position
        return this.headerData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // This will return the child
        return this.child.get(this.headerData.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        // Getting header title
        String headerTitle = (String) getGroup(groupPosition);
        // header = headerTitle.toLowerCase();


        // Inflating header layout and setting text
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.header, parent, false);
        }

        TextView txtnocheckold = (TextView) convertView.findViewById(R.id.txtnocheckold);

        Config.logV("No Child" + getChildrenCount(groupPosition));
        if (getChildrenCount(groupPosition) == 0) {
            if (groupPosition == 0) {
                txtnocheckold.setText("No Tokens for today");
            }
            if (groupPosition == 1) {
                txtnocheckold.setText("No Future Tokens");
            }
            if (groupPosition == 2) {
                txtnocheckold.setText("No Past Tokens");
            }
            txtnocheckold.setVisibility(View.VISIBLE);
        } else {
            txtnocheckold.setVisibility(View.GONE);
        }

        TextView header_text = (TextView) convertView.findViewById(R.id.header);
        if (getChildrenCount(groupPosition) > 0) {
            header_text.setText(headerTitle + " ( " + getChildrenCount(groupPosition) + " )");
        } else {
            header_text.setText(headerTitle);
        }
        // If group is expanded then change the text into bold and change the
        // icon
        if (isExpanded) {
            //header_text.setTypeface(null, Typeface.BOLD);
            Config.logV("Open@@@@" + groupPosition);
            if (groupPosition == 0) {
                mTodayFlag = true;
            }
            if (groupPosition == 1) {
                mFutureFlag = true;
            }
            if (groupPosition == 2) {
                mOldFlag = true;
            }

            if (getChildrenCount(groupPosition) > 0) {
                header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_up_light, 0);
            } else {
                header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
            header_text.setBackground(mContext.getResources().getDrawable(R.drawable.input_border_top));
        } else {
            // If group is not expanded then change the text back into normal
            // and change the icon
            if (getChildrenCount(groupPosition) == 0) {
                Config.logV("Open@@@@ FFFF" + groupPosition);
                if (groupPosition == 0) {
                    header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    header_text.setBackground(mContext.getResources().getDrawable(R.drawable.input_border_top));
                    mTodayFlag = true;
                }
                if (groupPosition == 1) {
                    header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    header_text.setBackground(mContext.getResources().getDrawable(R.drawable.input_border_top));
                    mFutureFlag = true;
                }
                if (groupPosition == 2) {
                    header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    header_text.setBackground(mContext.getResources().getDrawable(R.drawable.input_border_top));
                    mOldFlag = true;
                }
            } else {
                //header_text.setTypeface(null, Typeface.NORMAL);
                header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_down_light, 0);
                header_text.setBackground(mContext.getResources().getDrawable(R.drawable.input_background_opaque_round));
                Config.logV("Close@@@@" + groupPosition);
                if (groupPosition == 0) {
                    mTodayFlag = false;
                }
                if (groupPosition == 1) {
                    mFutureFlag = false;
                }
                if (groupPosition == 2) {
                    mOldFlag = false;
                }
            }
        }

        return convertView;
    }


    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        // Getting child text
        final ActiveCheckIn activelist = (ActiveCheckIn) getChild(groupPosition, childPosition);

        if (groupPosition == 0)
            header = "today";
        if (groupPosition == 1)
            header = "future";
        if (groupPosition == 2)
            header = "old";


        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.child_checkinhistory_layout_copy, parent, false);
        }

        TextView tv_businessname = (TextView) view.findViewById(R.id.txt_businessname);
        TextView tv_estTime = (TextView) view.findViewById(R.id.txt_esttime);
        TextView icon_bill = (TextView) view.findViewById(R.id.icon_bill);
        TextView tv_service = (TextView) view.findViewById(R.id.txt_service);
        TextView tv_place = (TextView) view.findViewById(R.id.txt_location);
        TextView tv_personahead = (TextView) view.findViewById(R.id.txt_personahead);
        TextView tv_token = (TextView) view.findViewById(R.id.txt_token);
        TextView tv_time_queue = (TextView) view.findViewById(R.id.time_queue);
        TextView icon_fav = (TextView) view.findViewById(R.id.icon_fav);
        TextView icon_message = (TextView) view.findViewById(R.id.icon_message);
        TextView icon_cancel = (TextView) view.findViewById(R.id.icon_cancel);
        TextView icon_rate = (TextView) view.findViewById(R.id.icon_rate);
        LinearLayout layout_token = (LinearLayout) view.findViewById(R.id.layout_token);
        TextView tv_status = (TextView) view.findViewById(R.id.txt_status);
        Button btn_pay = (Button) view.findViewById(R.id.btn_pay);
        TextView tv_prepaid = (TextView) view.findViewById(R.id.txt_prepaid);
        final TextView tv_makepay = (TextView) view.findViewById(R.id.txtmakepay);
        final LinearLayout paymentLayout = (LinearLayout) view.findViewById(R.id.paymentLayout);
        TextView tv_date = (TextView) view.findViewById(R.id.txt_date);
        TextView tv_partysize = (TextView) view.findViewById(R.id.txt_partysizevalue);
        TextView tv_check_in = (TextView) view.findViewById(R.id.txt_check_in_list);
        final TextView trackinLabel = (TextView) view.findViewById(R.id.trackinLabel);
        final TextView tv_travelmode = (TextView) view.findViewById(R.id.txt_travelmode_value);
        final TextView tv_travelCar = (TextView) view.findViewById(R.id.travelCar);
        final TextView tv_travelWalk = (TextView) view.findViewById(R.id.traveWalf);
        final TextView tv_travelmddeEdit = (TextView) view.findViewById(R.id.travelmddeEdit);
        final LinearLayout travelDetailsLayout = view.findViewById(R.id.travelDetailsLayout);
        final Switch liveTrackSwitch = (Switch) view.findViewById(R.id.switch1);
        TextView tv_queueTime = (TextView) view.findViewById(R.id.txt_queuetime);
        TextView tv_batchName = (TextView) view.findViewById(R.id.txt_batchName);
        TextView tv_statusSmall = (TextView) view.findViewById(R.id.txt_status_small);
        LinearLayout layout_dateCheckin = (LinearLayout) view.findViewById(R.id.dateCheckin);
        LinearLayout layout_amountDue = (LinearLayout) view.findViewById(R.id.amountDue);
        LinearLayout layout_partySize = (LinearLayout) view.findViewById(R.id.partySize);
        TextView tv_enable_loc = (TextView) view.findViewById(R.id.notEnableLoc);
        TextView tv_recom_loc = (TextView) view.findViewById(R.id.recomEnableLoc);
        TextView tv_recom_liveloc = (TextView) view.findViewById(R.id.recomShareLiveLoc);
        LinearLayout liveTrackLayout = (LinearLayout) view.findViewById(R.id.liveTrackLayout);
        LinearLayout layouttoken = (LinearLayout) view.findViewById(R.id.checkin);
        CardView cvGmeetDetails = view.findViewById(R.id.cv_GmeetDetails);
        CardView cvZoomDetails = view.findViewById(R.id.cv_ZoommeetDetails);
        CardView cvWhatsppDetails = view.findViewById(R.id.cv_whatsppDetails);
        CardView cvPhoneDetails = view.findViewById(R.id.cv_phoneMeetDetails);
        ImageView icon_service = view.findViewById(R.id.serviceicon);
        LinearLayout llprovider = view.findViewById(R.id.ll_providerName);
        TextView tvProviderName = view.findViewById(R.id.tv_providerName);
        cvGmeetDetails.setVisibility(View.GONE);
        cvZoomDetails.setVisibility(View.GONE);
        cvWhatsppDetails.setVisibility(View.GONE);
        cvPhoneDetails.setVisibility(View.GONE);

//        if(activelist.getWaitlistStatus().equalsIgnoreCase("failed")){
//            layouttoken.setVisibility(View.GONE);
//        }
//        else{
//            layouttoken.setVisibility(View.VISIBLE);
//        }


        if (activelist.getJaldeeWaitlistDistanceTime() != null && activelist.getWaitlistStatus().equals("checkedIn") && header.equals("today")) {


            activelistLatest = activelist;
//            if (activelist.getJaldeeWaitlistDistanceTime().getJaldeeDistanceTime() != null) {
//                if (activelist.getJaldeeWaitlistDistanceTime().getJaldeeDistanceTime().getJaldeelTravelTime().getTravelMode().equals("WALKING")) {
//                    tv_travelWalk.setVisibility(View.VISIBLE);
//                    tv_travelCar.setVisibility(View.GONE);
//                    trackinLabel.setText("I am walking");
//                } else if (activelist.getJaldeeWaitlistDistanceTime().getJaldeeDistanceTime().getJaldeelTravelTime().getTravelMode().equals("DRIVING")) {
//                    tv_travelCar.setVisibility(View.VISIBLE);
//                    tv_travelWalk.setVisibility(View.GONE);
//                    trackinLabel.setText("I am driving");
//                }
//            }

            if (activelist.getJaldeeWaitlistDistanceTime().getJaldeeDistanceTime() != null) {
//                travelDetailsLayout.setVisibility(View.VISIBLE);
                if (activelist.getJaldeeStartTimeType() != null) {
                    if (activelist.getJaldeeStartTimeType().equals("AFTERSTART") && header.equals("today")) {
                        liveTrackSwitch.setVisibility(View.VISIBLE);
                        trackinLabel.setText("Enable live tracking");
                        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
                        Call<ResponseBody> cal;
                        cal = apiService.StatusTracking(activelist.getYnwUuid(), activelist.getId());
                        cal.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                    if (response.code() == 200) {
                                        if (response.body().string().equals("true")) {
                                            liveTrackSwitch.setChecked(true);
//                                        enableTrack("general");
                                        } else {
                                            liveTrackSwitch.setChecked(false);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> cal, Throwable t) {
                                // Log error here since request failed
                                Config.logV("Location-----###########@@@@@@-------Fail--------" + t.toString());
                            }
                        });

                        // Call When toggle the live track switch
                        liveTrackSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
                                    Call<ResponseBody> call;
                                    call = apiService.StartTracking(activelist.getYnwUuid(), activelist.getId());
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                            try {
                                                if (response.code() == 200) {
                                                    if (response.body().string().equals("true")) {
                                                        activelistLatest = activelist;
//                                                    enableTrack("manual");
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            // Log error here since request failed
                                            Config.logV("Location-----###########@@@@@@-------Fail--------" + t.toString());
                                        }
                                    });
                                } else {
                                    ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
                                    Call<ResponseBody> call;
                                    call = apiService.StopTracking(activelist.getYnwUuid(), activelist.getId());
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                            try {
                                                if (response.code() == 200) {
                                                    if (response.body().string().equals("false")) {
                                                        liveTrackSwitch.setChecked(false);
                                                        locationManager.removeUpdates(ExpandableListAdapterToken.this);
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            // Log error here since request failed
                                            Config.logV("Location-----###########@@@@@@-------Fail--------" + t.toString());

                                        }
                                    });
                                }
                            }
                        });
                    } else if (activelist.getJaldeeStartTimeType().equals("ONEHOUR") && header.equals("today")) {
                        liveTrackSwitch.setVisibility(View.GONE);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String currentDateandTime = simpleDateFormat.format(new Date());
                        if (activelist.getDate() != null && activelist.getJaldeeWaitlistDistanceTime() != null) {
                            pollingTime = activelist.getDate() + " " + activelist.getJaldeeWaitlistDistanceTime().getPollingTime();

                            try {
                                date1 = simpleDateFormat.parse(currentDateandTime);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            try {
                                date2 = simpleDateFormat.parse(pollingTime);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (date1 != null && date2 != null) {
                                if (!date1.before(date2)) {
                                    activelistLatest = new ActiveCheckIn();
                                    activelistLatest = activelist;
                                    liveTrackSwitchLatest = liveTrackSwitch.isChecked();
//                                enableTrack("automatic");
                                }
                            }
                        }
                    }
                }
            } else {
                travelDetailsLayout.setVisibility(View.GONE);
            }
        } else {
            travelDetailsLayout.setVisibility(View.GONE);
            locationManager.removeUpdates(ExpandableListAdapterToken.this);
        }




        LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Boolean live = true;

        Log.i("dsfgdfg",String.valueOf(activelist.getLivetrack()));

        latitude = SharedPreference.getInstance(mContext).getStringValue("latitudes", "");
        longitude = SharedPreference.getInstance(mContext).getStringValue("longitudes", "");
        if (header.equals("today") && activelist.getLivetrack()!=null && activelist.getLivetrack().equals("true") || header.equals("future") && activelist.getLivetrack()!=null && activelist.getLivetrack().equals("true")) {
            if(!latitude.equals("") && !longitude.equals("")){
                distance(Double.parseDouble(activelist.getLattitude()),Double.parseDouble(activelist.getLongitude()),Double.parseDouble(latitude),Double.parseDouble(longitude));
            }else{
                distance(Double.parseDouble(activelist.getLattitude()),Double.parseDouble(activelist.getLongitude()),12.971599,77.594563);
            }



            String text = "Jaldee recommends you always";
            String text3 = " share your arrival time";
            String text4 = " with " + activelist.getBusinessName();

            String text2 = text + text3 + text4;

            Spannable spannable = new SpannableString(text2);

            spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.title_consu)), (text.length()), (text.length() + text3.length()) , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


            String text5 =  "You are ";
            String text6 =  "sharing your arrival time ";
            String text7 =  "with "+activelist.getBusinessName();

            String text8 = text5 + text6 + text7;

            Spannable spannables = new SpannableString(text8);

            spannables.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.title_consu)), (text5.length()), (text5.length() + text6.length()) , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);



            liveTrackLayout.setVisibility(View.VISIBLE);
            if (!gps_enabled && !network_enabled) {
                tv_enable_loc.setText("Oops!!, You are NOT sharing your arrival time with " + activelist.getBusinessName());
                tv_recom_loc.setText("Jaldee recommends you to enable location");
                tv_recom_loc.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                tv_recom_liveloc.setVisibility(View.GONE);
                tv_enable_loc.setVisibility(View.VISIBLE);
                tv_recom_loc.setVisibility(View.VISIBLE);
            }

            else if (activelist.getJaldeeWaitlistDistanceTime() != null && activelist.getJaldeeWaitlistDistanceTime().getJaldeeDistanceTime() != null) {

                tv_recom_liveloc.setText(spannables, TextView.BufferType.SPANNABLE);
                tv_recom_loc.setVisibility(View.GONE);
                tv_enable_loc.setVisibility(View.GONE);
                tv_recom_liveloc.setVisibility(View.VISIBLE);
            }

            else {
                tv_recom_loc.setText("Oops!!, You are NOT sharing your arrival time with " + activelist.getBusinessName());
                tv_recom_liveloc.setText(spannable, TextView.BufferType.SPANNABLE);
                tv_recom_liveloc.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                tv_recom_liveloc.setVisibility(View.VISIBLE);
                tv_recom_loc.setVisibility(View.VISIBLE);
                tv_enable_loc.setVisibility(View.GONE);
            }

        } else {
            liveTrackLayout.setVisibility(View.GONE);
        }



//        tv_icon_refresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, CheckinShareLocation.class);
//                intent.putExtra("waitlistPhonenumber", activelist.getPrimaryMobileNo());
//                intent.putExtra("uuid", activelist.getYnwUuid());
//                intent.putExtra("accountID", String.valueOf(activelist.getId()));
//                intent.putExtra("title", activelist.getBusinessName());
//                intent.putExtra("terminology", "Check-in");
//                intent.putExtra("calcMode", activelist.getCalculationMode());
//                intent.putExtra("queueStartTime",activelist.getQueueStartTime());
//                intent.putExtra("queueEndTime",activelist.getQueueEndTime());
//                if(activelist.getJaldeeWaitlistDistanceTime()!=null && activelist.getJaldeeWaitlistDistanceTime().getJaldeeDistanceTime()!=null){
//                intent.putExtra("jaldeeDistance",activelist.getJaldeeWaitlistDistanceTime().getJaldeeDistanceTime().getJaldeeDistance().toString());}
//                mContext.startActivity(intent);
//            }
//        });

        tv_recom_liveloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CheckinShareLocation.class);
                intent.putExtra("waitlistPhonenumber", activelist.getPhoneNo());
                intent.putExtra("uuid", activelist.getYnwUuid());
                intent.putExtra("accountID", String.valueOf(activelist.getId()));
                intent.putExtra("title", activelist.getBusinessName());
                intent.putExtra("terminology", "Check-in");
                intent.putExtra("calcMode", activelist.getCalculationMode());
                intent.putExtra("queueStartTime",activelist.getQueueStartTime());
                intent.putExtra("queueEndTime",activelist.getQueueEndTime());
                if(activelist.getJaldeeWaitlistDistanceTime()!=null && activelist.getJaldeeWaitlistDistanceTime().getJaldeeDistanceTime()!=null){
                    intent.putExtra("jaldeeDistance",activelist.getJaldeeWaitlistDistanceTime().getJaldeeDistanceTime().getJaldeeDistance().toString());}
                mContext.startActivity(intent);
            }
        });
        tv_travelmddeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_travelmddeEdit.setVisibility(View.GONE);
                tv_travelCar.setVisibility(View.VISIBLE);
                tv_travelWalk.setVisibility(View.VISIBLE);

            }
        });

        tv_travelCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_travelCar.setVisibility(View.VISIBLE);
                tv_travelWalk.setVisibility(View.GONE);
                tv_travelmddeEdit.setVisibility(View.VISIBLE);
                tv_travelmode.setText("DRIVING");
                if (activelist.getJaldeeStartTimeType() != null) {
                    if (activelist.getJaldeeStartTimeType().equals("AFTERSTART")) {
                        trackinLabel.setText("Enable live tracking");
                    } else {
                        trackinLabel.setText("I am driving");
                    }
                    ApiUpdateTravelMode();
                }
            }

            private void ApiUpdateTravelMode() {
                ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
                JSONObject jsonObj = new JSONObject();
                try {
                    jsonObj.put("travelMode", tv_travelmode.getText());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());

                Call<ResponseBody> call;
                call = apiService.PutTravelMode(activelist.getYnwUuid(), activelist.getId(), body);


                Config.logV("Request--BODY-------------------------" + new Gson().toJson(jsonObj.toString()));

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        try {
                            if (response.code() == 200) {

                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // Log error here since request failed
                        Config.logV("Location-----###########@@@@@@-------Fail--------" + t.toString());


                    }
                });

            }

        });

        tv_travelWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_travelCar.setVisibility(View.GONE);
                tv_travelWalk.setVisibility(View.VISIBLE);
                tv_travelmddeEdit.setVisibility(View.VISIBLE);
                tv_travelmode.setText("WALKING");
                if (activelist.getJaldeeStartTimeType() != null) {
                    if (activelist.getJaldeeStartTimeType().equals("AFTERSTART")) {
                        trackinLabel.setText("Enable live tracking");
                    } else {
                        trackinLabel.setText("I am walking");
                    }

                    ApiUpdateTravelMode();
                }
            }
            private void ApiUpdateTravelMode() {


                ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
                JSONObject jsonObj = new JSONObject();
                try {
                    jsonObj.put("travelMode", tv_travelmode.getText());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());

                Call<ResponseBody> call;
                call = apiService.PutTravelMode(activelist.getYnwUuid(), activelist.getId(), body);


                Config.logV("Request--BODY-------------------------" + new Gson().toJson(jsonObj.toString()));

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        try {
                            if (response.code() == 200) {

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // Log error here since request failed
                        Config.logV("Location-----###########@@@@@@-------Fail--------" + t.toString());
                    }
                });
            }
        });


        tv_businessname.setText(Config.toTitleCase(activelist.getBusinessName()));
        if (activelist.getProvider() != null) {
            llprovider.setVisibility(View.VISIBLE);
            tvProviderName.setText(activelist.getProvider().getFirstName()+" "+ activelist.getProvider().getLastName());
        }
        else {
            llprovider.setVisibility(View.GONE);
        }

        icon_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onMethodDelecteCheckinCallback(activelist.getYnwUuid(), activelist.getId(), mTodayFlag, mFutureFlag, mOldFlag,"checkin");
            }
        });
        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_businessname.setTypeface(tyface);

        tv_businessname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailIntent = new Intent(mContext, ProviderDetailActivity.class);
                detailIntent.putExtra("uniqueID",activelist.getUniqueId());
                mContext.startActivity(detailIntent);
              //  callback.onMethodActiveCallback(activelist.getUniqueId());
            }
        });


        try {
            if(activelist.getGoogleMapUrl()!=null){
                String geoUri = activelist.getGoogleMapUrl();
                if (activelist.getPlace() != null && geoUri != null && !geoUri.equalsIgnoreCase("")) {
                    tv_place.setVisibility(View.VISIBLE);
                    tv_place.setText(activelist.getPlace());
                }
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
        icon_fav.setText("Favourite");
        icon_fav.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_favourite_line, 0, 0);
        for (int i = 0; i < FavList.size(); i++) {
            Config.logV("Fav List-----##&&&-----" + FavList.get(i).getId());
            if (FavList.get(i).getId() == activelist.getId()) {
                Config.logV("Fav Fav List--------%%%%--" + activelist.getId());
                icon_fav.setVisibility(View.VISIBLE);
                icon_fav.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_favourited, 0, 0);
                activelist.getConsumer().setFavourite(true);
                icon_fav.setText("Favourite");
            }
        }
        icon_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (activelist.getConsumer().isFavourite()) {
                Config.logV("Fav" + activelist.getId());
                callback.onMethodDeleteFavourite(activelist.getId(), mTodayFlag, mFutureFlag, mOldFlag);
            } else {
                Config.logV("Fav Addd" + activelist.getId());
                callback.onMethodAddFavourite(activelist.getId(), mTodayFlag, mFutureFlag, mOldFlag);
            }
            }
        });
        tv_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Config.logV("googlemap url--------" + activelist.getGoogleMapUrl());
            if(activelist.getGoogleMapUrl()!=null){
                String geoUri = activelist.getGoogleMapUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                mContext.startActivity(intent);}
            }
        });
        tv_service.setVisibility(View.GONE);
        if (activelist.getName() != null) {
            tv_service.setVisibility(View.VISIBLE);
            Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/Montserrat_Bold.otf");
            String firstWord = activelist.getName();
            String secondWord = " for ";
            String thirdWord = Config.toTitleCase(activelist.getFirstName()) + " " + Config.toTitleCase(activelist.getLastName());
            Spannable spannable = new SpannableString(firstWord + secondWord + thirdWord);
            spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length() + secondWord.length(), firstWord.length() + secondWord.length() + thirdWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_service.setText(spannable);
            try{
                if(activelist.getService().getServiceType().equalsIgnoreCase("virtualService")){
                    icon_service.setVisibility(View.VISIBLE);
                    icon_service.setY(6);

                    if(activelist.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")){
                        icon_service.setImageResource(R.drawable.zoomicon_sized);
                    }
                    else if(activelist.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")){
                        icon_service.setImageResource(R.drawable.googlemeet_sized);
                    }
                    else if(activelist.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")){
                        icon_service.setImageResource(R.drawable.whatsappicon_sized);

                    }
                    else if(activelist.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")){
                        icon_service.setImageResource(R.drawable.phoneiconsized_small);
                    }

                }
                else{
                    icon_service.setVisibility(View.GONE);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        } else {
            tv_service.setVisibility(View.GONE);
        }
        if (activelist.getPaymentStatus().
                equalsIgnoreCase("FullyPaid") || activelist.getPaymentStatus().equalsIgnoreCase("Refund")) {
            icon_bill.setText("Receipt");
        } else {
            icon_bill.setText("Bill");
        }
        Config.logV("Bill------------" + activelist.getWaitlistStatus());
        if (activelist.getBillViewStatus() != null && !activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
            if (activelist.getBillViewStatus().equalsIgnoreCase("Show")) {
                icon_bill.setVisibility(View.VISIBLE);
            } else {
                icon_bill.setVisibility(View.GONE);
            }
        } else {
            if(!activelist.getPaymentStatus().equalsIgnoreCase("NotPaid")){
                icon_bill.setVisibility(View.VISIBLE);
            }
            else{
                icon_bill.setVisibility(View.GONE);
            }
        }
        icon_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            callback.onMethodBillIconCallback(activelist.getPaymentStatus(), activelist.getYnwUuid(), activelist.getBusinessName(), String.valueOf(activelist.getId()), String.valueOf(Config.toTitleCase(activelist.getFirstName()) + " " + Config.toTitleCase(activelist.getLastName())),activelist.getConsumer().getId(),activelist.getUniqueId(),activelist.getCheckinEncId());
            }
        });
        Config.logV("Date------------" + activelist.getDate());
        //  tv_estTime.setVisibility(View.VISIBLE);
        Typeface tyface3 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        String Word1 = "Time Window";
        String Word2 = activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime();
        Spannable spannable2 = new SpannableString(Word1 + '\n' + Word2);
//        spannable2.setSpan(new CustomTypefaceSpan("sans-serif", tyface3), Word1.length(), Word1.length() + Word2.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable2.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.black)),
                Word1.length(), Word1.length() + Word2.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_queueTime.setText(spannable2);
        if (activelist.getToken() != -1 && activelist.getToken() > 0) {
            layout_partySize.setVisibility(View.VISIBLE);
            tv_token.setVisibility(View.VISIBLE);
            String firstWord = "Token # ";
            Config.logV("Token------------" + activelist.getToken());
            String secondWord = String.valueOf(activelist.getToken());
            Typeface tyface2 = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/Montserrat_Bold.otf");
            Spannable spannable = new SpannableString(firstWord + secondWord);
            spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.sec_title_grey)),
                    0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //  spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface2), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.black)),
                    firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_token.setText(spannable);

            tv_batchName.setVisibility(View.GONE);
        } else {
            tv_token.setVisibility(View.GONE);
            tv_batchName.setVisibility(View.GONE);
        }

//        if(activelist.getService()!=null) {
//            if (activelist.getService().getServiceType().equalsIgnoreCase("virtualService")) {
//                Toast.makeText(mContext,"You took an appointment for virtual service",Toast.LENGTH_SHORT).show();
//            }
//        }



        if (activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
            String inputDateStr = activelist.getDate();
            Date datechange = null;
            try {
                datechange = inputFormat.parse(inputDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String outputDateStr = outputFormat.format(datechange);
            Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/Montserrat_Bold.otf");
            String firstWord = "";
            firstWord = "Checked in for ";
            String yourDate = Config.getFormatedDate(outputDateStr);
            String secondWord = yourDate + "," + '\n' + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime();
            Spannable spannable = new SpannableString(firstWord + '\n' + secondWord);
            spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.black)),
                    firstWord.length(), firstWord.length() + secondWord.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_check_in.setText(spannable);
            tv_check_in.setVisibility(View.VISIBLE);
            layout_dateCheckin.setVisibility(View.VISIBLE);
            tv_queueTime.setVisibility(View.GONE);
            liveTrackLayout.setVisibility(View.GONE);
        } else {
            tv_check_in.setVisibility(View.GONE);
            tv_queueTime.setVisibility(View.VISIBLE);
        }
        if (activelist.getParentUuid() != null) {
            paymentLayout.setVisibility(View.GONE);
            tv_prepaid.setVisibility(View.GONE);
            btn_pay.setVisibility(View.GONE);
            tv_makepay.setVisibility(View.GONE);
        }
        if (tv_date.toString().equalsIgnoreCase("") && tv_check_in.toString().equalsIgnoreCase("")) {
            layout_dateCheckin.setVisibility(View.GONE);
        }
        if (tv_prepaid.toString().equalsIgnoreCase("")) {
            layout_amountDue.setVisibility(View.GONE);
        }
        if (tv_token.toString().equalsIgnoreCase("") && tv_partysize.toString().equalsIgnoreCase("")) {
            layout_partySize.setVisibility(View.GONE);
        }
        if (activelist.getServiceTime() != null) {
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            if (date.equalsIgnoreCase(activelist.getDate())) {
                Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                        "fonts/Montserrat_Bold.otf");
                String firstWord = "";
                firstWord = "Checked in for ";
                String secondWord = "Today" + "," + " " + activelist.getServiceTime();
                Spannable spannable = new SpannableString(firstWord + '\n' + secondWord);
                //   spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.black)),
                        firstWord.length(), firstWord.length() + secondWord.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                    tv_status.setVisibility(View.VISIBLE);
                    tv_statusSmall.setVisibility(View.VISIBLE);
                    tv_status.setText(secondWord + "Cancelled at " + activelist.getStatusUpdatedTime());
                    tv_statusSmall.setText("Cancelled ");
                    tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                    tv_statusSmall.setTextColor(mContext.getResources().getColor(R.color.red));
                }
                if (!activelist.getWaitlistStatus().equalsIgnoreCase("done") && !activelist.getWaitlistStatus().equalsIgnoreCase("cancelled") && !header.equalsIgnoreCase("old")) {
                    if (activelist.getShowToken().equalsIgnoreCase("true") && activelist.getCalculationMode().equalsIgnoreCase("NoCalc")){
                        tv_estTime.setVisibility(View.GONE);
                    } else {
                        tv_estTime.setVisibility(View.VISIBLE);
                        tv_estTime.setText(spannable);
                    }
                } else {
                    tv_estTime.setVisibility(View.GONE);
                }
            } else {
                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                String inputDateStr = activelist.getDate();
                Date datechange = null;
                try {
                    datechange = inputFormat.parse(inputDateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String outputDateStr = outputFormat.format(datechange);
                Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                        "fonts/Montserrat_Bold.otf");
                String firstWord = "";
                firstWord = "Checked in for ";
                String yourDate = Config.getFormatedDate(outputDateStr);
                String secondWord = yourDate + ", " + activelist.getServiceTime();
                Spannable spannable = new SpannableString(firstWord + '\n' + secondWord);
                //  spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.black)),
                        firstWord.length(), firstWord.length() + secondWord.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                    tv_status.setVisibility(View.VISIBLE);
                    tv_statusSmall.setVisibility(View.VISIBLE);
                    tv_status.setText(secondWord + "Cancelled at " + activelist.getStatusUpdatedTime());
                    tv_statusSmall.setText("Cancelled ");
                    tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                    tv_statusSmall.setTextColor(mContext.getResources().getColor(R.color.red));
                    //tv_estTime.setText(spannable);
                }
                if (!activelist.getWaitlistStatus().equalsIgnoreCase("done") && !activelist.getWaitlistStatus().equalsIgnoreCase("cancelled") && !header.equalsIgnoreCase("old")) {
                    if (activelist.getShowToken().equalsIgnoreCase("true") && activelist.getCalculationMode().equalsIgnoreCase("NoCalc")){
                        tv_estTime.setVisibility(View.GONE);
                    } else {
                        tv_estTime.setVisibility(View.VISIBLE);
                        tv_estTime.setText(spannable);
                    }
                } else {
                    tv_estTime.setVisibility(View.GONE);
                }
            }
        } else {
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            if (date.equalsIgnoreCase(activelist.getDate())) {
                Config.logV("getAppxWaitingTime------------" + activelist.getAppxWaitingTime());
                if (activelist.getAppxWaitingTime() == 0) {
                    Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                            "fonts/Montserrat_Bold.otf");
                    String firstWord = "Est Wait Time ";
                    String secondWord = "Now";
                    Spannable spannable = new SpannableString(firstWord + secondWord);
                    //  spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.black)),
                            firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                        secondWord = "Today";
                        long appwaittime;
                        if (activelist.getAppxWaitingTime() != -1) {
                            appwaittime = TimeUnit.MINUTES.toMillis(activelist.getAppxWaitingTime());
                        } else {
                            appwaittime = 0;
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                        Date Timeconvert = null;
                        long millis = 0;
                        try {
                            // sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                            Timeconvert = sdf.parse(activelist.getQueueStartTime());
                            millis = Timeconvert.getTime();
                            Config.logV("millsss----" + millis);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        long finalcheckin = appwaittime + millis;
                        String timeFORAMT = getDate(finalcheckin, "hh:mm a");
                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                        String inputDateStr = activelist.getDate();
                        Date datechange = null;
                        try {
                            datechange = inputFormat.parse(inputDateStr);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String outputDateStr = outputFormat.format(datechange);
                        firstWord = "Checked in for ";
                        String yourDate = Config.getFormatedDate(outputDateStr);
                        tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                "fonts/Montserrat_Bold.otf");
                        secondWord = yourDate + ", " + timeFORAMT;
                        spannable = new SpannableString(firstWord + '\n' + secondWord);
                        //  spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.black)),
                                firstWord.length(), firstWord.length() + secondWord.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv_status.setVisibility(View.VISIBLE);
                        tv_statusSmall.setVisibility(View.VISIBLE);
                        tv_status.setText("Cancelled at " + activelist.getStatusUpdatedTime());
                        tv_statusSmall.setText("Cancelled ");
                        tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                        tv_statusSmall.setTextColor(mContext.getResources().getColor(R.color.red));
                    } else {
                        //  tv_check_in.setVisibility(View.GONE);
                        tv_status.setVisibility(View.GONE);
                        tv_statusSmall.setVisibility(View.GONE);
                    }
                    if (!activelist.getWaitlistStatus().equalsIgnoreCase("done") && !activelist.getWaitlistStatus().equalsIgnoreCase("cancelled") && !header.equalsIgnoreCase("old")) {
                        if (activelist.getShowToken().equalsIgnoreCase("true") && activelist.getCalculationMode().equalsIgnoreCase("NoCalc")){
                            tv_estTime.setVisibility(View.GONE);
                        } else {
                            tv_estTime.setVisibility(View.VISIBLE);
                            tv_estTime.setText(spannable);
                        }
                    } else {
                        tv_estTime.setVisibility(View.GONE);
                    }
                } else {
                    if (activelist.getAppxWaitingTime() == -1) {
                        tv_estTime.setVisibility(View.GONE);
                        if (activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                            tv_status.setVisibility(View.VISIBLE);
                            tv_statusSmall.setVisibility(View.VISIBLE);
                            tv_status.setText("Cancelled at " + activelist.getStatusUpdatedTime());
                            tv_statusSmall.setText("Cancelled ");
                            tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                            tv_statusSmall.setTextColor(mContext.getResources().getColor(R.color.red));
                        }
                    } else {
                        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                "fonts/Montserrat_Bold.otf");
                        String firstWord = "";
                        if (header.equalsIgnoreCase("future")) {
                            firstWord = "Checked in for ";
                        }
                        if (header.equalsIgnoreCase("today")) {
                            firstWord = "Est Wait Time ";
                        }
                        String secondWord = Config.getTimeinHourMinutes(activelist.getAppxWaitingTime());
                        Spannable spannable = new SpannableString(firstWord + secondWord);
                        //  spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.black)),
                                firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        if (activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                            long appwaittime;
                            if (activelist.getAppxWaitingTime() != -1) {
                                appwaittime = TimeUnit.MINUTES.toMillis(activelist.getAppxWaitingTime());
                            } else {
                                appwaittime = 0;
                            }
                            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                            Date Timeconvert = null;
                            long millis = 0;
                            try {
                                // sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                                Timeconvert = sdf.parse(activelist.getQueueStartTime());
                                millis = Timeconvert.getTime();
                                Config.logV("millsss----" + millis);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            long finalcheckin = appwaittime + millis;
                            String timeFORAMT = getDate(finalcheckin, "hh:mm a");
                            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                            String inputDateStr = activelist.getDate();
                            Date datechange = null;
                            try {
                                datechange = inputFormat.parse(inputDateStr);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String outputDateStr = outputFormat.format(datechange);
                            firstWord = "Checked in for ";
                            String yourDate = Config.getFormatedDate(outputDateStr);
                            tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                    "fonts/Montserrat_Bold.otf");
                            secondWord = yourDate + ", " + timeFORAMT;
                            spannable = new SpannableString(firstWord + '\n' + secondWord);
                            spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.black)),
                                    firstWord.length(), firstWord.length() + secondWord.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            tv_status.setVisibility(View.VISIBLE);
                            tv_statusSmall.setVisibility(View.VISIBLE);
                            tv_status.setText("Cancelled at " + " " + activelist.getStatusUpdatedTime());
                            tv_statusSmall.setText("Cancelled ");
                            tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                            tv_statusSmall.setTextColor(mContext.getResources().getColor(R.color.red));
                        }
                        if (!activelist.getWaitlistStatus().equalsIgnoreCase("done") && !activelist.getWaitlistStatus().equalsIgnoreCase("cancelled") && !header.equalsIgnoreCase("old")) {
                            if (activelist.getShowToken().equalsIgnoreCase("true") && activelist.getCalculationMode().equalsIgnoreCase("NoCalc")){
                                tv_estTime.setVisibility(View.GONE);
                            } else {
                                tv_estTime.setVisibility(View.VISIBLE);
                                tv_estTime.setText(spannable);
                            }

                        } else {
                            tv_estTime.setVisibility(View.GONE);
                        }
                    }
                }
            } else {
                Config.logV("response.body().get(i).getQueue().getQueueStartTime()" + activelist.getQueueStartTime());
                Config.logV("Quueue Time----------------" + activelist.getQueueStartTime());
                Config.logV("App Time----------------" + activelist.getAppxWaitingTime());
                long appwaittime;
                if (activelist.getAppxWaitingTime() != -1) {
                    appwaittime = TimeUnit.MINUTES.toMillis(activelist.getAppxWaitingTime());
                } else {
                    appwaittime = 0;
                }
                if (activelist.getQueueStartTime() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                    Date Timeconvert = null;
                    long millis = 0;
                    try {
                        // sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                        Timeconvert = sdf.parse(activelist.getQueueStartTime());
                        millis = Timeconvert.getTime();
                        Config.logV("millsss----" + millis);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long finalcheckin = appwaittime + millis;
                    String timeFORAMT = getDate(finalcheckin, "hh:mm a");
                    Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                            "fonts/Montserrat_Bold.otf");
                    String firstWord = "";
                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String inputDateStr = activelist.getDate();
                    Date datechange = null;
                    try {
                        datechange = inputFormat.parse(inputDateStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String outputDateStr = outputFormat.format(datechange);
                    firstWord = "Checked in for ";
                    String yourDate = Config.getFormatedDate(outputDateStr);
                    String secondWord = yourDate + ", " + activelist.getQueueStartTime();
                    Spannable spannable = new SpannableString(firstWord + secondWord);
                    spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                            firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    if (activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {

                        if (activelist.getAppxWaitingTime() != -1) {
                            appwaittime = TimeUnit.MINUTES.toMillis(activelist.getAppxWaitingTime());
                        } else {
                            appwaittime = 0;
                        }
                        sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                        Timeconvert = null;
                        millis = 0;
                        try {
                            Timeconvert = sdf.parse(activelist.getQueueStartTime());
                            millis = Timeconvert.getTime();
                            Config.logV("millsss----" + millis);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        finalcheckin = appwaittime + millis;
                        timeFORAMT = getDate(finalcheckin, "hh:mm a");
                        inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                        inputDateStr = activelist.getDate();
                        datechange = null;
                        try {
                            datechange = inputFormat.parse(inputDateStr);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        outputDateStr = outputFormat.format(datechange);
                        firstWord = "Checked in for ";
                        yourDate = Config.getFormatedDate(outputDateStr);
                        tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                "fonts/Montserrat_Bold.otf");
                        secondWord = yourDate + ", " + timeFORAMT;
                        spannable = new SpannableString(firstWord + '\n' + secondWord);
                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.black)),
                                firstWord.length(), firstWord.length() + secondWord.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv_status.setVisibility(View.VISIBLE);
                        tv_statusSmall.setVisibility(View.VISIBLE);
                        tv_status.setText("Cancelled at" + " " + activelist.getStatusUpdatedTime());
                        tv_statusSmall.setText("Cancelled ");
                        tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                        tv_statusSmall.setTextColor(mContext.getResources().getColor(R.color.red));
                    } else {
                        tv_status.setVisibility(View.GONE);
                        tv_statusSmall.setVisibility(View.GONE);
                    }
                    if (!activelist.getWaitlistStatus().equalsIgnoreCase("done") && !activelist.getWaitlistStatus().equalsIgnoreCase("cancelled") && !header.equalsIgnoreCase("old")) {
                        tv_estTime.setVisibility(View.VISIBLE);
                        tv_estTime.setText(spannable);
                    } else {
                        tv_estTime.setVisibility(View.GONE);
                    }
                } else {
                    if (activelist.getAppxWaitingTime() != -1) {
                        String sTime = null;
                        String firstWord = "";
                        sTime = Config.getTimeinHourMinutes(activelist.getAppxWaitingTime());
                        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                "fonts/Montserrat_Bold.otf");
                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                        String inputDateStr = activelist.getDate();
                        Date datechange = null;
                        try {
                            datechange = inputFormat.parse(inputDateStr);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String outputDateStr = outputFormat.format(datechange);
                        String yourDate = Config.getFormatedDate(outputDateStr);
                        String secondWord = yourDate + ", " + sTime;
                        Spannable spannable = new SpannableString(firstWord + secondWord);
                        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.black)),
                                firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        if (activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                            if (activelist.getAppxWaitingTime() != -1) {
                                appwaittime = TimeUnit.MINUTES.toMillis(activelist.getAppxWaitingTime());
                            } else {
                                appwaittime = 0;
                            }
                            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                            Date Timeconvert = null;
                            long millis = 0;
                            try {
                                // sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                                Timeconvert = sdf.parse(activelist.getQueueStartTime());
                                millis = Timeconvert.getTime();
                                Config.logV("millsss----" + millis);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            long finalcheckin = appwaittime + millis;
                            String timeFORAMT = getDate(finalcheckin, "hh:mm a");
                            inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                            outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                            inputDateStr = activelist.getDate();
                            datechange = null;
                            try {
                                datechange = inputFormat.parse(inputDateStr);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            outputDateStr = outputFormat.format(datechange);
                            firstWord = "Checked in for ";
                            yourDate = Config.getFormatedDate(outputDateStr);
                            tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                    "fonts/Montserrat_Bold.otf");
                            secondWord = yourDate + ", " + timeFORAMT;
                            spannable = new SpannableString(firstWord + '\n' + secondWord);
                            spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.black)),
                                    firstWord.length(), firstWord.length() + secondWord.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            tv_status.setVisibility(View.VISIBLE);
                            tv_statusSmall.setVisibility(View.VISIBLE);
                            tv_status.setText("Cancelled at" + " " + activelist.getStatusUpdatedTime());
                            tv_statusSmall.setText("Cancelled");
                            tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                            tv_statusSmall.setTextColor(mContext.getResources().getColor(R.color.red));
                        } else {
                            tv_status.setVisibility(View.GONE);
                            tv_statusSmall.setVisibility(View.GONE);
                        }
                        if (!activelist.getWaitlistStatus().equalsIgnoreCase("done") && !activelist.getWaitlistStatus().equalsIgnoreCase("cancelled") && !header.equalsIgnoreCase("old")) {
                            tv_estTime.setVisibility(View.VISIBLE);
                            tv_estTime.setText(spannable);
                        } else {
                            tv_estTime.setVisibility(View.GONE);
                        }
                    } else {
                        tv_estTime.setVisibility(View.GONE);
                    }
                }
            }
        }
        layout_partySize.setVisibility(View.GONE);
        if (!header.equalsIgnoreCase("old") && !activelist.getWaitlistStatus().
                equalsIgnoreCase("started") && !activelist.getWaitlistStatus().
                equalsIgnoreCase("cancelled") && !(activelist.getWaitlistStatus().
                equalsIgnoreCase("done"))) {
            Typeface tyface2 = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/Montserrat_Bold.otf");
            if (String.valueOf(activelist.getPartySize()) != null) {
                if (activelist.getPartySize() > 1) {
                    layout_partySize.setVisibility(View.VISIBLE);
                    String partyWord = "Party Size ";
                    String ValueWord = String.valueOf(activelist.getPartySize());
                    Spannable spannable1 = new SpannableString(partyWord + ValueWord);
                    spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.sec_title_grey)),
                            0, partyWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_partysize.setVisibility(View.VISIBLE);
                    // spannable1.setSpan(new CustomTypefaceSpan("sans-serif", tyface2), partyWord.length(), partyWord.length() + ValueWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.black)),
                            partyWord.length(), partyWord.length() + ValueWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_partysize.setText(spannable1);
                } else {
                    tv_partysize.setVisibility(View.GONE);
                }
            } else {
                tv_partysize.setVisibility(View.GONE);
            }
        } else {
            layout_partySize.setVisibility(View.GONE);
            tv_estTime.setVisibility(View.GONE);
        }
        if (activelist.getPersonsAhead() != -1 && !activelist.getWaitlistStatus().
                equalsIgnoreCase("cancelled") && !header.equalsIgnoreCase("old")) {
            layout_partySize.setVisibility(View.VISIBLE);
            tv_personahead.setVisibility(View.VISIBLE);
            String firstWord1 = "People ahead of you ";
            String secondWord1 = String.valueOf(activelist.getPersonsAhead());
            String nobody_ahead = "You are first in line ";
            String one_person_ahead = "1 person ahead of you ";
            if (activelist.getPersonsAhead() == 0) {
                Spannable spannable1 = new SpannableString(nobody_ahead);
                spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.black)),
                        0, nobody_ahead.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (activelist.getAppxWaitingTime() == 0) {
                    tv_personahead.setText(spannable1);
                } else {
                    tv_personahead.setText(spannable1);
                }
            } else if (activelist.getPersonsAhead() == 1) {
                if (activelist.getAppxWaitingTime() == 0) {
                    Spannable spannable1 = new SpannableString(one_person_ahead);
                    spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.black)),
                            0, one_person_ahead.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_personahead.setText(spannable1);
                } else {
                    Spannable spannable1 = new SpannableString(one_person_ahead);
                    spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.black)),
                            0, one_person_ahead.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_personahead.setText(spannable1);
                }
            } else {
                Spannable spannable1 = new SpannableString(secondWord1 + " " + firstWord1);
                spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.black)),
                        0, firstWord1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (activelist.getAppxWaitingTime() == 0) {
                    tv_personahead.setText(spannable1);
                } else {
                    tv_personahead.setText(spannable1);
                }
            }
        } else {
            tv_personahead.setVisibility(View.GONE);
        }
        icon_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            callback.onMethodMessageCallback(activelist.getYnwUuid(), String.valueOf(activelist.getId()), activelist.getBusinessName(),"checkin");
            }
        });
        if (header.equalsIgnoreCase("old")) {
            tv_place.setVisibility(View.VISIBLE);
            tv_place.setText(activelist.getPlace());
            tv_date.setVisibility(View.VISIBLE);
            layout_dateCheckin.setVisibility(View.VISIBLE);
            try {
                String mDate = Config.ChangeDateFormat(activelist.getDate());
                if (mDate != null)
                    tv_date.setText(mDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            tv_date.setVisibility(View.GONE);
        }
        tv_status.setVisibility(View.VISIBLE);
        tv_statusSmall.setVisibility(View.VISIBLE);
        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        //  tv_status.setTypeface(tyface1);
        if (activelist.getWaitlistStatus().equalsIgnoreCase("done")) {
            tv_check_in.setVisibility(View.GONE);
            tv_status.setText("Completed");
            tv_statusSmall.setText("Completed");
            tv_status.setVisibility(View.GONE);
            tv_statusSmall.setVisibility(View.VISIBLE);
            tv_status.setTextColor(mContext.getResources().getColor(R.color.green));
            tv_statusSmall.setTextColor(mContext.getResources().getColor(R.color.green));
        }
        if (activelist.getWaitlistStatus().equalsIgnoreCase("arrived")) {
            if(activelist.getService().getServiceType().equalsIgnoreCase("virtualService")){
                tv_statusSmall.setText("");
            }
            else if(activelist.getService().getServiceType().equalsIgnoreCase("physicalService")){
                tv_statusSmall.setText("Arrived");

            }
            else {
                tv_statusSmall.setText("Arrived");
            }
            tv_status.setText("Arrived");
            tv_status.setVisibility(View.GONE);
            tv_statusSmall.setVisibility(View.VISIBLE);
            tv_status.setTextColor(mContext.getResources().getColor(R.color.arrived_green));
            tv_statusSmall.setTextColor(mContext.getResources().getColor(R.color.arrived_green));
        }
        if (activelist.getWaitlistStatus().equalsIgnoreCase("checkedIn")) {
            tv_status.setText("Checked-in");
            tv_statusSmall.setText("Checked-in");
            tv_status.setVisibility(View.GONE);
            tv_statusSmall.setVisibility(View.VISIBLE);
            tv_status.setTextColor(mContext.getResources().getColor(R.color.purple));
            tv_statusSmall.setTextColor(mContext.getResources().getColor(R.color.purple));
            tv_check_in.setVisibility(View.GONE);
        }
        if (activelist.getWaitlistStatus().equalsIgnoreCase("started")) {
            tv_check_in.setVisibility(View.GONE);
            tv_status.setText("Started");
            tv_statusSmall.setText("Started");
            tv_status.setVisibility(View.GONE);
            tv_statusSmall.setVisibility(View.VISIBLE);
            tv_status.setTextColor(mContext.getResources().getColor(R.color.cyan));
            tv_statusSmall.setTextColor(mContext.getResources().getColor(R.color.cyan));
        }
        if (activelist.getWaitlistStatus().equalsIgnoreCase("prepaymentPending")) {
            if (activelist.getParentUuid() != null) {
                tv_status.setVisibility(View.GONE);
                tv_statusSmall.setVisibility(View.GONE);
            } else {
                tv_status.setText("Prepayment Pending");
                tv_statusSmall.setText("Prepayment Pending");
                tv_status.setVisibility(View.GONE);
                tv_statusSmall.setVisibility(View.VISIBLE);
                tv_status.setTextColor(mContext.getResources().getColor(R.color.gray));
                tv_statusSmall.setTextColor(mContext.getResources().getColor(R.color.gray));
            }
        }
        if (!(activelist.getPaymentStatus().equalsIgnoreCase("FullyPaid")) && (activelist.getBillViewStatus() != null) || activelist.getWaitlistStatus().equalsIgnoreCase("prepaymentPending")) {
            if (activelist.getAmountDue() != 0) {
                if (activelist.getBillViewStatus() != null && activelist.getBillViewStatus().equalsIgnoreCase("Show") && activelist.getAmountDue() > 0 && !activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                    btn_pay.setVisibility(View.VISIBLE);
                    btn_pay.setText("PAY");
                    if (activelist.getAmountDue() > 0) {
                        tv_prepaid.setVisibility(View.VISIBLE);
                        tv_prepaid.setText("Amount Due: ₹ " + Config.getAmountinTwoDecimalPoints(activelist.getAmountDue()));
                        tv_makepay.setVisibility(View.VISIBLE);
                    } else {
                        tv_prepaid.setVisibility(View.GONE);
                        tv_makepay.setVisibility(View.GONE);
                    }
                } else {
                    btn_pay.setVisibility(View.GONE);
                }
            }
            if (activelist.getWaitlistStatus().equalsIgnoreCase("prepaymentPending")) {
                btn_pay.setVisibility(View.VISIBLE);
                tv_makepay.setVisibility(View.VISIBLE);
                paymentLayout.setVisibility(View.VISIBLE);
                btn_pay.setText("PAY");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                Date time = new Date();
                Log.i("shankar", simpleDateFormat.format(time));
                String checkinTime = activelist.getDate() + " " + activelist.getCheckInTime();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
                try {
                    Date date2 = format.parse(checkinTime);
                    long diff = (time.getTime() - date2.getTime());
                    if (diff < 0) {
                        Date date3 = subtractDays(date2, 1);
                        long diff1 = (time.getTime() - date3.getTime());
                        final long diffMins = diff1 / 60000;
                        if (diffMins <= 15) {
                            new CountDownTimer(diff1, 60000) {
                                public void onTick(long millisUntilFinished) {
                                    long mins = 15 - diffMins;
                                    if (activelist.getParentUuid() != null) {
                                        tv_makepay.setVisibility(View.GONE);
                                    } else {
                                        tv_makepay.setText("");
                                        mins--;
                                    }
                                }
                                public void onFinish() {
                                    tv_makepay.setVisibility(View.GONE);
                                }
                            }.start();
                        }
                    }
                    final long diffMins = diff / 60000;
                    if (diffMins <= 15) {
                        new CountDownTimer(diff, 60000) {
                            public void onTick(long millisUntilFinished) {
                                long mins = 15 - diffMins;
                                if (activelist.getParentUuid() != null) {
                                    tv_makepay.setVisibility(View.GONE);
                                } else {
                                    tv_makepay.setText("");
                                    mins--;
                                }
                            }

                            public void onFinish() {
                                tv_makepay.setVisibility(View.GONE);
                            }
                        }.start();
                    } else {
                        tv_makepay.setVisibility(View.GONE);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // myViewHolder.tv_makepay.setText("Click PRE-PAY button in 15 minutes to complete your check-in");
                if (activelist.getAmountDue() > 0) {
                    if (activelist.getParentUuid() != null) {
                        tv_prepaid.setVisibility(View.GONE);
                        tv_makepay.setVisibility(View.GONE);
                        btn_pay.setVisibility(View.GONE);
                    } else {
                        tv_prepaid.setVisibility(View.VISIBLE);
                        tv_makepay.setVisibility(View.VISIBLE);
                        tv_prepaid.setText("Amount Due: ₹ " + Config.getAmountinTwoDecimalPoints(activelist.getAmountDue()));
                        tv_makepay.setText("");
                    }
                } else {
                    tv_prepaid.setVisibility(View.GONE);
                    tv_makepay.setVisibility(View.GONE);
                }
            } else {
                btn_pay.setVisibility(View.GONE);
                tv_makepay.setVisibility(View.GONE);
                paymentLayout.setVisibility(View.GONE);
            }
        } else {
            btn_pay.setVisibility(View.GONE);
            tv_makepay.setVisibility(View.GONE);
            tv_prepaid.setVisibility(View.GONE);
            paymentLayout.setVisibility(View.GONE);
        }
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Config.logV("Button Pay@@@@@@@@@@@@@@@@@" + activelist.getWaitlistStatus());
            // callback.onMethodActivePayIconCallback(activelist.getYnwUuid());
            String consumer = Config.toTitleCase(activelist.getFirstName()) + " " + Config.toTitleCase(activelist.getLastName());
            if (activelist.getWaitlistStatus().equalsIgnoreCase("prepaymentPending")) {
                callbacks.onMethodActivePayIconCallback(activelist.getPaymentStatus(), activelist.getYnwUuid(), activelist.getBusinessName(), String.valueOf(activelist.getId()), activelist.getAmountDue(),activelist.getConsumer().getId(),activelist.getUniqueId(),activelist.getCheckinEncId());
            } else {
                callbacks.onMethodActiveBillIconCallback(activelist.getPaymentStatus(), activelist.getYnwUuid(), activelist.getBusinessName(), String.valueOf(activelist.getId()), consumer,activelist.getConsumer().getId(),activelist.getUniqueId(),activelist.getCheckinEncId());
            }
            }
        });
        /*if(header.equalsIgnoreCase("old")) {*/
        if (activelist.getWaitlistStatus().equalsIgnoreCase("done")) {
            icon_rate.setVisibility(View.VISIBLE);
        } else {
            icon_rate.setVisibility(View.GONE);
        }
        icon_rate.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_star_line, 0, 0);
        if (activelist.getRating() != null) {
            if (activelist.getRating().getStars() > 0) {
                icon_rate.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.star_full, 0, 0);
            } else {
                icon_rate.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_star_line, 0, 0);
            }
        }
        icon_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            callback.onMethodRating(String.valueOf(activelist.getId()), activelist.getYnwUuid(), mTodayFlag, mFutureFlag, mOldFlag);
            }
        });

        if (header.equalsIgnoreCase("old")) {
            travelDetailsLayout.setVisibility(View.GONE);
            icon_cancel.setVisibility(View.GONE);
            cvGmeetDetails.setVisibility(View.GONE);
            cvZoomDetails.setVisibility(View.GONE);
            cvWhatsppDetails.setVisibility(View.GONE);
            cvPhoneDetails.setVisibility(View.GONE);
        } else {
            if (activelist.getWaitlistStatus().equalsIgnoreCase("checkedIn") || activelist.getWaitlistStatus().equalsIgnoreCase("arrived") || activelist.getWaitlistStatus().equalsIgnoreCase("prepaymentPending")) {
                icon_cancel.setVisibility(View.VISIBLE);
            } else {
                icon_cancel.setVisibility(View.GONE);
            }
        }
        Config.logV("Header Title" + header + "Title" + activelist.getBusinessName() + "Group" + groupPosition);


        try {

            if (header.equalsIgnoreCase("today")) {
                if (activelist.getWaitlistStatus().equalsIgnoreCase("checkedIn") || activelist.getWaitlistStatus().equalsIgnoreCase("arrived") || activelist.getWaitlistStatus().equalsIgnoreCase("started")) {
                    if (activelist.getService() != null) {
                        if (activelist.getService().getServiceType().equalsIgnoreCase("virtualService")) {
                            apiGetMeetingDetails(activelist.getYnwUuid(), activelist.getService().getVirtualCallingModes().get(0).getCallingMode(), activelist.getId());

                            if (activelist.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                cvGmeetDetails.setVisibility(View.VISIBLE);
                                cvZoomDetails.setVisibility(View.GONE);
                                cvWhatsppDetails.setVisibility(View.GONE);
                                cvPhoneDetails.setVisibility(View.GONE);
                                cvGmeetDetails.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        meetingDetailsWindow = new MeetingDetailsWindow(mContext, activelist.getCheckInTime(), activelist.getService().getName(), gMeetResponse, activelist.getService().getVirtualCallingModes().get(0).getCallingMode());
                                        meetingDetailsWindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        meetingDetailsWindow.show();
                                        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                                        int width = (int) (metrics.widthPixels * 1);
                                        meetingDetailsWindow.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    }
                                });
                            } else if (activelist.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                cvZoomDetails.setVisibility(View.VISIBLE);
                                cvGmeetDetails.setVisibility(View.GONE);
                                cvWhatsppDetails.setVisibility(View.GONE);
                                cvPhoneDetails.setVisibility(View.GONE);
                                cvZoomDetails.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        meetingDetailsWindow = new MeetingDetailsWindow(mContext, activelist.getCheckInTime(), activelist.getService().getName(), zoomResponse, activelist.getService().getVirtualCallingModes().get(0).getCallingMode());
                                        meetingDetailsWindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        meetingDetailsWindow.show();
                                        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                                        int width = (int) (metrics.widthPixels * 1);
                                        meetingDetailsWindow.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    }
                                });

                            } else if (activelist.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                cvWhatsppDetails.setVisibility(View.VISIBLE);
                                cvGmeetDetails.setVisibility(View.GONE);
                                cvZoomDetails.setVisibility(View.GONE);
                                cvPhoneDetails.setVisibility(View.GONE);
                                cvWhatsppDetails.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        meetingInfo = new MeetingInfo(mContext, activelist.getCheckInTime(), activelist.getService().getName(), whatsappResponse, activelist.getService().getVirtualCallingModes().get(0).getCallingMode(),activelist.getVirtualService().getWhatsApp());
                                        meetingInfo.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        meetingInfo.show();
                                        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                                        int width = (int) (metrics.widthPixels * 1);
                                        meetingInfo.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    }
                                });

                            } else if (activelist.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Phone")) {
                                cvPhoneDetails.setVisibility(View.VISIBLE);
                                cvGmeetDetails.setVisibility(View.GONE);
                                cvZoomDetails.setVisibility(View.GONE);
                                cvWhatsppDetails.setVisibility(View.GONE);
                                cvPhoneDetails.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        meetingInfo = new MeetingInfo(mContext, activelist.getCheckInTime(), activelist.getService().getName(), phoneresponse, activelist.getService().getVirtualCallingModes().get(0).getCallingMode(), activelist.getVirtualService().getPhone());
                                        meetingInfo.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        meetingInfo.show();
                                        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                                        int width = (int) (metrics.widthPixels * 1);
                                        meetingInfo.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    }
                                });
                            }
                            else {
                                cvGmeetDetails.setVisibility(View.GONE);
                                cvZoomDetails.setVisibility(View.GONE);
                                cvWhatsppDetails.setVisibility(View.GONE);
                                cvPhoneDetails.setVisibility(View.GONE);
                            }
                        } else {

                            cvGmeetDetails.setVisibility(View.GONE);
                            cvZoomDetails.setVisibility(View.GONE);
                            cvWhatsppDetails.setVisibility(View.GONE);
                            cvPhoneDetails.setVisibility(View.GONE);
                        }

                    }

                }

            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    public Date subtractDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -days);

        return cal.getTime();
    }
//    private void enableTrack(String source) {
//        int a = 0;
//        if(a==1){
//            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
//        }else{
//            Log.i("KOoooi",source);
//        }
//
//
//
//    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        // formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        distance(location.getLatitude(), location.getLongitude(), 10.5276, 76.2144);
        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        JSONObject jsonObj = new JSONObject();
        try {
            Toast.makeText(mContext, "Live", Toast.LENGTH_SHORT).show();
            Log.i("LatlongTest", String.valueOf(location.getLatitude()));
            Log.i("LatlongTest", String.valueOf(location.getLongitude()));
            jsonObj.put("latitude", location.getLatitude());
            jsonObj.put("longitude", location.getLongitude());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> callLivetrack;
        callLivetrack = apiService.UpdateLatLong(activelistLatest.getYnwUuid(), activelistLatest.getId(), body);
        callLivetrack.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> callLivetrack, Response<ResponseBody> response) {
                try {
                    if (response.code() == 200) {
                        Log.i("MELVINS", "UpdateLatLong");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> callLivetrack, Throwable t) {
                // Log error here since request failed
                Config.logV("Location-----###########@@@@@@-------Fail--------" + t.toString());
            }
        });
    }

    private void apiGetMeetingDetails(String uuid, String mode, int accountID) {

        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);

//        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
//        mDialog.show();

        Call<TeleServiceCheckIn> call = apiService.getMeetingDetails(uuid, mode, accountID);

        call.enqueue(new Callback<TeleServiceCheckIn>() {
            @Override
            public void onResponse(Call<TeleServiceCheckIn> call, Response<TeleServiceCheckIn> response) {

                try {

//                    mDialog.dismiss();
                    if (response.code() == 200) {

                        teleServiceCheckInResponse = response.body();
                        if (teleServiceCheckInResponse != null) {

                            if (mode.equalsIgnoreCase("GoogleMeet")){

                                gMeetResponse = teleServiceCheckInResponse;
                            }
                            else if (mode.equalsIgnoreCase("Zoom")){

                                zoomResponse = teleServiceCheckInResponse;
                            }
                            else if (mode.equalsIgnoreCase("WhatsApp")){

                                whatsappResponse = teleServiceCheckInResponse;
                            }
                            else if (mode.equalsIgnoreCase("Phone")){

                                phoneresponse = teleServiceCheckInResponse;
                            }

                        }
                    }
                } catch (
                        Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<TeleServiceCheckIn> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
//                 if (mDialog.isShowing())
//                 Config.closeDialog(get, mDialog);
            }
        });
    }



    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        Log.i("DistanceVivek", String.valueOf(dist));
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}
