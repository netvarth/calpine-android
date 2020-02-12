package com.jaldeeinc.jaldee.adapter;


import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.CheckinShareLocation;
import com.jaldeeinc.jaldee.service.LocationUpdatesService;
import com.jaldeeinc.jaldee.callback.HistoryAdapterCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTypefaceSpan;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.FavouriteModel;

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
public class ExpandableListAdapter extends BaseExpandableListAdapter implements LocationListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private Context mContext;
    private List<String> headerData; // header titles
    // Child data in format of header title, child title
    private HashMap<String, ArrayList<ActiveCheckIn>> child;
    Activity activity;
    HistoryAdapterCallback callback;
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


    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private CheckinShareLocation.MyReceiver myReceiver;

    // A reference to the service used to get location updates.
    private LocationUpdatesService mService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;


    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };


    public ExpandableListAdapter(ArrayList<FavouriteModel> mFavList, Context mContext, Activity mActivity, HistoryAdapterCallback callback, List<String> listDataHeader, HashMap<String, ArrayList<ActiveCheckIn>> listChildData, boolean mTodayFlag, boolean mFutureFlag, boolean mOldFlag, LocationManager locationManager) {
        this.mContext = mContext;
        this.headerData = listDataHeader;
        this.child = listChildData;
        this.activity = mActivity;
        this.callback = callback;

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
        // TODO Auto-generated method stub
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
                txtnocheckold.setText("No Check-ins for today");
            }
            if (groupPosition == 1) {
                txtnocheckold.setText("No Future Check-ins");
            }
            if (groupPosition == 2) {
                txtnocheckold.setText("No Past Check-ins");
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

        if (activelist.getJaldeeWaitlistDistanceTime() != null && activelist.getWaitlistStatus().equals("checkedIn")) {


            activelistLatest = activelist;
            if (activelist.getJaldeeWaitlistDistanceTime().getJaldeeDistanceTime() != null) {
                if (activelist.getJaldeeWaitlistDistanceTime().getJaldeeDistanceTime().getJaldeelTravelTime().getTravelMode().equals("WALKING")) {
                    tv_travelWalk.setVisibility(View.VISIBLE);
                    tv_travelCar.setVisibility(View.GONE);
                    trackinLabel.setText("I am walking");
                } else if (activelist.getJaldeeWaitlistDistanceTime().getJaldeeDistanceTime().getJaldeelTravelTime().getTravelMode().equals("DRIVING")) {
                    tv_travelCar.setVisibility(View.VISIBLE);
                    tv_travelWalk.setVisibility(View.GONE);
                    trackinLabel.setText("I am driving");
                }
            }

            if (activelist.getJaldeeWaitlistDistanceTime().getJaldeeDistanceTime() != null) {
                travelDetailsLayout.setVisibility(View.VISIBLE);
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
                                                        locationManager.removeUpdates(ExpandableListAdapter.this);
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
            locationManager.removeUpdates(ExpandableListAdapter.this);
        }


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


        icon_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onMethodDelecteCheckinCallback(activelist.getYnwUuid(), activelist.getId(), mTodayFlag, mFutureFlag, mOldFlag);
            }
        });
        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_businessname.setTypeface(tyface);

        tv_businessname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onMethodActiveCallback(activelist.getUniqueId());
            }
        });


        try {
            String geoUri = activelist.getGoogleMapUrl();
            if (activelist.getPlace() != null && geoUri != null && !geoUri.equalsIgnoreCase("")) {
                tv_place.setVisibility(View.VISIBLE);
                tv_place.setText(activelist.getPlace());

            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }

        icon_fav.setText("Favourite");
        icon_fav.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_favourite_line, 0, 0);

        for (
                int i = 0; i < FavList.size(); i++) {
            Config.logV("Fav List-----##&&&-----" + FavList.get(i).getId());


            if (FavList.get(i).getId() == activelist.getId()) {
                Config.logV("Fav Fav List--------%%%%--" + activelist.getId());
                icon_fav.setVisibility(View.VISIBLE);
                icon_fav.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_favourited, 0, 0);
                activelist.setFavFlag(true);
                icon_fav.setText("Favourite");
            }
        }

        icon_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activelist.isFavFlag()) {

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
                String geoUri = activelist.getGoogleMapUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                mContext.startActivity(intent);
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
        } else {
            tv_service.setVisibility(View.GONE);
        }

        if (activelist.getPaymentStatus().

                equalsIgnoreCase("FullyPaid")) {
            icon_bill.setText("Receipt");
        } else {
            icon_bill.setText("Bill");
        }

        Config.logV("Bill------------" + activelist.getWaitlistStatus());
        if (activelist.getBillViewStatus() != null) {
            if (activelist.getBillViewStatus().equalsIgnoreCase("Show")) {
                icon_bill.setVisibility(View.VISIBLE);
            } else {
                icon_bill.setVisibility(View.GONE);
            }
        } else {
            icon_bill.setVisibility(View.GONE);
        }


        icon_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onMethodBillIconCallback(activelist.getPaymentStatus(), activelist.getYnwUuid(), activelist.getBusinessName(), String.valueOf(activelist.getId()), String.valueOf(Config.toTitleCase(activelist.getFirstName()) + " " + Config.toTitleCase(activelist.getLastName())));
            }
        });


        Config.logV("Date------------" + activelist.getDate());


        //  tv_estTime.setVisibility(View.VISIBLE);

        if (activelist.getServiceTime() != null) {

            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            if (date.equalsIgnoreCase(activelist.getDate())) {

                Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                        "fonts/Montserrat_Bold.otf");
                String firstWord = "";
                firstWord = "Checked in for ";

                String secondWord = "Today" + "," + " " + activelist.getServiceTime();
                Spannable spannable = new SpannableString(firstWord + secondWord + " (" + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime() + " )");
                spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                        firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                if (activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                    tv_status.setVisibility(View.VISIBLE);
                    tv_status.setText(secondWord + " - Cancelled ");
                    tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                }

                if (!activelist.getWaitlistStatus().equalsIgnoreCase("done") && !activelist.getWaitlistStatus().equalsIgnoreCase("cancelled") && !header.equalsIgnoreCase("old")) {
                    tv_estTime.setVisibility(View.VISIBLE);
                    tv_estTime.setText(spannable);
//                    tv_queueTime.setVisibility(View.VISIBLE);
//                    tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
                } else {
                    tv_estTime.setVisibility(View.GONE);
//                    tv_queueTime.setVisibility(View.VISIBLE);
//                    tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
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

                String dtStart = outputDateStr;
                Date dateParse = null;
                SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    dateParse = format1.parse(dtStart);
                    System.out.println(dateParse);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                SimpleDateFormat format = new SimpleDateFormat("d");
                String date1 = format.format(dateParse);

                if (date1.endsWith("1") && !date1.endsWith("11"))
                    format = new SimpleDateFormat("EE, MMM d'st' yyyy");
                else if (date1.endsWith("2") && !date1.endsWith("12"))
                    format = new SimpleDateFormat("EE, MMM d'nd' yyyy");
                else if (date1.endsWith("3") && !date1.endsWith("13"))
                    format = new SimpleDateFormat("EE, MMM d'rd' yyyy");
                else
                    format = new SimpleDateFormat("EE, MMM d'th' yyyy");

                String yourDate = format.format(dateParse);
                String secondWord = yourDate + ", " + activelist.getServiceTime();


                Spannable spannable = new SpannableString(firstWord + secondWord + " (" + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime() + " )");
                spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                        firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                if (activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                    tv_status.setVisibility(View.VISIBLE);
                    tv_status.setText(secondWord + " - Cancelled ");
                    tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                    tv_estTime.setText(spannable);
                }
                if (!activelist.getWaitlistStatus().equalsIgnoreCase("done") && !activelist.getWaitlistStatus().equalsIgnoreCase("cancelled") && !header.equalsIgnoreCase("old")) {
                    tv_estTime.setVisibility(View.VISIBLE);
                    tv_estTime.setText(spannable);
//                    tv_queueTime.setVisibility(View.VISIBLE);
//                    tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
                } else {
                    tv_estTime.setVisibility(View.GONE);
//                    tv_queueTime.setVisibility(View.VISIBLE);
//                    tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
                }
            }


        } else {
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            if (date.equalsIgnoreCase(activelist.getDate())) {
                Config.logV("getAppxWaitingTime------------" + activelist.getAppxWaitingTime());
                if (activelist.getAppxWaitingTime() == 0) {
                    Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                            "fonts/Montserrat_Bold.otf");
                    String firstWord = "Est Time ";
                    String secondWord = "Now";
                    Spannable spannable = new SpannableString(firstWord + secondWord);
                    spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
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
                        String dtStart = outputDateStr;
                        Date dateParse = null;
                        SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
                        try {
                            dateParse = format1.parse(dtStart);
                            System.out.println(dateParse);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        SimpleDateFormat format = new SimpleDateFormat("d");
                        String date1 = format.format(dateParse);

                        if (date1.endsWith("1") && !date1.endsWith("11"))
                            format = new SimpleDateFormat("EE, MMM d'st' yyyy");
                        else if (date1.endsWith("2") && !date1.endsWith("12"))
                            format = new SimpleDateFormat("EE, MMM d'nd' yyyy");
                        else if (date1.endsWith("3") && !date1.endsWith("13"))
                            format = new SimpleDateFormat("EE, MMM d'rd' yyyy");
                        else
                            format = new SimpleDateFormat("EE, MMM d'th' yyyy");

                        String yourDate = format.format(dateParse);
                        tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                "fonts/Montserrat_Bold.otf");
                        secondWord = yourDate + ", " + timeFORAMT;
                        spannable = new SpannableString(firstWord + secondWord + " (" + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime() + " )");
                        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                                firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        tv_check_in.setVisibility(View.VISIBLE);
                        tv_check_in.setText(spannable);
                        tv_status.setVisibility(View.VISIBLE);
                        tv_status.setText("Cancelled at " + activelist.getStatusUpdatedTime());
                        tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                    }
                    if (!activelist.getWaitlistStatus().equalsIgnoreCase("done") && !activelist.getWaitlistStatus().equalsIgnoreCase("cancelled") && !header.equalsIgnoreCase("old")) {
                        tv_estTime.setVisibility(View.VISIBLE);
                        tv_estTime.setText(spannable);
//                        tv_queueTime.setVisibility(View.VISIBLE);
//                        tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
                    } else {
                        tv_estTime.setVisibility(View.GONE);
//                        tv_queueTime.setVisibility(View.VISIBLE);
//                        tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
                    }
                } else {
                    if (activelist.getAppxWaitingTime() == -1) {
                        tv_estTime.setVisibility(View.GONE);
//                        tv_queueTime.setVisibility(View.VISIBLE);
//                        tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
                        if (activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                            tv_status.setVisibility(View.VISIBLE);
                            tv_status.setText(" Cancelled ");
                            tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                        }
                    } else {
                        //tv_estTime.setVisibility(View.VISIBLE);
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
                        Spannable spannable = new SpannableString(firstWord + secondWord + " (" + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime() + " )");
                        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
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
                            String dtStart = outputDateStr;
                            Date dateParse = null;
                            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
                            try {
                                dateParse = format1.parse(dtStart);
                                System.out.println(dateParse);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            SimpleDateFormat format = new SimpleDateFormat("d");
                            String date1 = format.format(dateParse);

                            if (date1.endsWith("1") && !date1.endsWith("11"))
                                format = new SimpleDateFormat("EE, MMM d'st' yyyy");
                            else if (date1.endsWith("2") && !date1.endsWith("12"))
                                format = new SimpleDateFormat("EE, MMM d'nd' yyyy");
                            else if (date1.endsWith("3") && !date1.endsWith("13"))
                                format = new SimpleDateFormat("EE, MMM d'rd' yyyy");
                            else
                                format = new SimpleDateFormat("EE, MMM d'th' yyyy");

                            String yourDate = format.format(dateParse);
                            tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                    "fonts/Montserrat_Bold.otf");
                            secondWord = yourDate + ", " + timeFORAMT;
                            spannable = new SpannableString(firstWord + secondWord + " (" + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime() + " )");
                            spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                                    firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            tv_status.setVisibility(View.VISIBLE);
                            tv_status.setText("Cancelled at " + " " + activelist.getStatusUpdatedTime());
                            tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                        }
                        if (!activelist.getWaitlistStatus().equalsIgnoreCase("done") && !activelist.getWaitlistStatus().equalsIgnoreCase("cancelled") && !header.equalsIgnoreCase("old")) {
                            tv_estTime.setVisibility(View.VISIBLE);
                            tv_estTime.setText(spannable);
//                            tv_queueTime.setVisibility(View.VISIBLE);
//                            tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
                        } else {
                            tv_estTime.setVisibility(View.GONE);
//                            tv_queueTime.setVisibility(View.VISIBLE);
//                            tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
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
                    String dtStart = outputDateStr;
                    Date dateParse = null;
                    SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
                    try {
                        dateParse = format1.parse(dtStart);
                        System.out.println(dateParse);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat format = new SimpleDateFormat("d");
                    String date1 = format.format(dateParse);

                    if (date1.endsWith("1") && !date1.endsWith("11"))
                        format = new SimpleDateFormat("EE, MMM d'st' yyyy");
                    else if (date1.endsWith("2") && !date1.endsWith("12"))
                        format = new SimpleDateFormat("EE, MMM d'nd' yyyy");
                    else if (date1.endsWith("3") && !date1.endsWith("13"))
                        format = new SimpleDateFormat("EE, MMM d'rd' yyyy");
                    else
                        format = new SimpleDateFormat("EE, MMM d'th' yyyy");

                    String yourDate = format.format(dateParse);
                    String secondWord = activelist.getDate() + ", " + activelist.getQueueStartTime();
                    Spannable spannable = new SpannableString(firstWord + secondWord + " (" + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime() + " )");
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
                        dtStart = outputDateStr;
                        dateParse = null;
                        format1 = new SimpleDateFormat("dd-MM-yyyy");
                        try {
                            dateParse = format1.parse(dtStart);
                            System.out.println(dateParse);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        format = new SimpleDateFormat("d");
                        date1 = format.format(dateParse);

                        if (date1.endsWith("1") && !date1.endsWith("11"))
                            format = new SimpleDateFormat("EE, MMM d'st' yyyy");
                        else if (date1.endsWith("2") && !date1.endsWith("12"))
                            format = new SimpleDateFormat("EE, MMM d'nd' yyyy");
                        else if (date1.endsWith("3") && !date1.endsWith("13"))
                            format = new SimpleDateFormat("EE, MMM d'rd' yyyy");
                        else
                            format = new SimpleDateFormat("EE, MMM d'th' yyyy");

                        yourDate = format.format(dateParse);
                        tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                "fonts/Montserrat_Bold.otf");
                        secondWord = yourDate + ", " + timeFORAMT;
                        spannable = new SpannableString(firstWord + secondWord + " (" + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime() + " )");
                        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                                firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        tv_check_in.setVisibility(View.VISIBLE);
                        tv_check_in.setText(spannable);
                        tv_status.setVisibility(View.VISIBLE);
                        tv_status.setText("Cancelled at" + " " + activelist.getStatusUpdatedTime());
                        tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                    }
                    if (!activelist.getWaitlistStatus().equalsIgnoreCase("done") && !activelist.getWaitlistStatus().equalsIgnoreCase("cancelled") && !header.equalsIgnoreCase("old")) {
                        tv_estTime.setVisibility(View.VISIBLE);
                        tv_estTime.setText(spannable);
//                        tv_queueTime.setVisibility(View.VISIBLE);
//                        tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
                    } else {
                        tv_estTime.setVisibility(View.GONE);
//                        tv_queueTime.setVisibility(View.VISIBLE);
//                        tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
                    }
                } else {

                    if (activelist.getAppxWaitingTime() != -1) {
                        String sTime = null;
                        String firstWord = "";
                        try {
                            String startTime = "00:00";
                            String newtime;
                            int minutes = activelist.getAppxWaitingTime();
                            int h = minutes / 60 + Integer.parseInt(startTime.substring(0, 1));
                            int m = minutes % 60 + Integer.parseInt(startTime.substring(3, 4));

                            if (header.equalsIgnoreCase("future")) {
                                firstWord = "Checked in for ";
                            }

                            if (header.equalsIgnoreCase("today")) {

                                firstWord = "Est Wait Time ";
                            }
                            if (m > 0 && h > 0) {
                                newtime = h + " Hour :" + m + " Minutes";
                            } else if (h > 0 && m == 0) {
                                newtime = h + " Hour";
                            } else {
                                newtime = m + " Minutes";
                            }
                            sTime = newtime;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

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
                        String dtStart = outputDateStr;
                        Date dateParse = null;
                        SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
                        try {
                            dateParse = format1.parse(dtStart);
                            System.out.println(dateParse);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        SimpleDateFormat format = new SimpleDateFormat("d");
                        String date1 = format.format(dateParse);

                        if (date1.endsWith("1") && !date1.endsWith("11"))
                            format = new SimpleDateFormat("EE, MMM d'st' yyyy");
                        else if (date1.endsWith("2") && !date1.endsWith("12"))
                            format = new SimpleDateFormat("EE, MMM d'nd' yyyy");
                        else if (date1.endsWith("3") && !date1.endsWith("13"))
                            format = new SimpleDateFormat("EE, MMM d'rd' yyyy");
                        else
                            format = new SimpleDateFormat("EE, MMM d'th' yyyy");

                        String yourDate = format.format(dateParse);
                        String secondWord = yourDate + ", " + sTime;
                        Spannable spannable = new SpannableString(firstWord + secondWord + " (" + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime() + " )");
                        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
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
                            dtStart = outputDateStr;
                            dateParse = null;
                            format1 = new SimpleDateFormat("dd-MM-yyyy");
                            try {
                                dateParse = format1.parse(dtStart);
                                System.out.println(dateParse);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            format = new SimpleDateFormat("d");
                            date1 = format.format(dateParse);

                            if (date1.endsWith("1") && !date1.endsWith("11"))
                                format = new SimpleDateFormat("EE, MMM d'st' yyyy");
                            else if (date1.endsWith("2") && !date1.endsWith("12"))
                                format = new SimpleDateFormat("EE, MMM d'nd' yyyy");
                            else if (date1.endsWith("3") && !date1.endsWith("13"))
                                format = new SimpleDateFormat("EE, MMM d'rd' yyyy");
                            else
                                format = new SimpleDateFormat("EE, MMM d'th' yyyy");

                            yourDate = format.format(dateParse);
                            tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                    "fonts/Montserrat_Bold.otf");
                            secondWord = yourDate + ", " + timeFORAMT;
                            spannable = new SpannableString(firstWord + secondWord + " (" + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime() + " )");
                            spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                                    firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            tv_check_in.setVisibility(View.VISIBLE);
                            tv_check_in.setText(spannable);
                            tv_status.setVisibility(View.VISIBLE);
                            tv_status.setText("Cancelled at" + " " + activelist.getStatusUpdatedTime());
                            tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                        }
                        if (!activelist.getWaitlistStatus().equalsIgnoreCase("done") && !activelist.getWaitlistStatus().equalsIgnoreCase("cancelled") && !header.equalsIgnoreCase("old")) {
                            tv_estTime.setVisibility(View.VISIBLE);
                            tv_estTime.setText(spannable);
//                            tv_queueTime.setVisibility(View.VISIBLE);
//                            tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
                        } else {
                            tv_estTime.setVisibility(View.GONE);
//                            tv_queueTime.setVisibility(View.VISIBLE);
//                            tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
                        }
                    } else {
                        tv_estTime.setVisibility(View.GONE);
//                        tv_queueTime.setVisibility(View.VISIBLE);
//                        tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
                    }
                }

            }

        }
        layout_token.setVisibility(View.GONE);
        if (!header.equalsIgnoreCase("old") && !activelist.getWaitlistStatus().

                equalsIgnoreCase("started") && !activelist.getWaitlistStatus().

                equalsIgnoreCase("cancelled") && !(activelist.getWaitlistStatus().

                equalsIgnoreCase("done"))) {
            Typeface tyface2 = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/Montserrat_Bold.otf");
            if (activelist.getToken() != -1 && activelist.getToken() >= 0) {
                tv_token.setVisibility(View.VISIBLE);
                tv_time_queue.setVisibility(View.VISIBLE);
                layout_token.setVisibility(View.VISIBLE);
                String firstWord = "Token # ";
                Config.logV("Token------------" + activelist.getToken());
                String secondWord = String.valueOf(activelist.getToken());
                String queStart = String.valueOf(activelist.getQueue().getQueueStartTime());
                String queEnd = String.valueOf(activelist.getQueue().getQueueEndTime());
                String queueWindow = String.valueOf(activelist.getQueue().getName());
                Spannable spannable = new SpannableString(firstWord + secondWord);
                spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.sec_title_grey)),
                        0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface2), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                        firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv_token.setText(spannable);
                tv_time_queue.setText(queueWindow + " " + "[" + " " + queStart + " " + "-" + " " + queEnd + " " + "]");
            } else {
                tv_token.setVisibility(View.GONE);
                tv_time_queue.setVisibility(View.GONE);
            }
            if (String.valueOf(activelist.getPartySize()) != null) {
                if (activelist.getPartySize() > 1) {
                    layout_token.setVisibility(View.VISIBLE);
                    String partyWord = "Party Size ";
                    String ValueWord = String.valueOf(activelist.getPartySize());
                    Spannable spannable1 = new SpannableString(partyWord + ValueWord);
                    spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.sec_title_grey)),
                            0, partyWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_partysize.setVisibility(View.VISIBLE);
                    spannable1.setSpan(new CustomTypefaceSpan("sans-serif", tyface2), partyWord.length(), partyWord.length() + ValueWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                            partyWord.length(), partyWord.length() + ValueWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_partysize.setText(spannable1);
                } else {
                    tv_partysize.setVisibility(View.GONE);
                }
            } else {
                tv_partysize.setVisibility(View.GONE);
            }
        } else {
            layout_token.setVisibility(View.GONE);
            tv_estTime.setVisibility(View.GONE);
//            tv_queueTime.setVisibility(View.VISIBLE);
//            tv_queueTime.setText("Checked in for " + " " + activelist.getQueueStartTime() + " " + "-" + " " + activelist.getQueueEndTime());
        }
        if (activelist.getPersonsAhead() != -1 && !activelist.getWaitlistStatus().

                equalsIgnoreCase("cancelled") && !header.equalsIgnoreCase("old")) {
            layout_token.setVisibility(View.VISIBLE);
            tv_personahead.setVisibility(View.VISIBLE);
            String firstWord1 = "People ahead of you ";
            String secondWord1 = String.valueOf(activelist.getPersonsAhead());
            String nobody_ahead = "You are first in line ";
            String one_person_ahead = "1 person ahead of you ";

            if (activelist.getPersonsAhead() == 0) {

                Spannable spannable1 = new SpannableString(nobody_ahead);
                spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.sec_title_grey)),
                        0, nobody_ahead.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv_personahead.setText(spannable1);
            } else if (activelist.getPersonsAhead() == 1) {

                Spannable spannable1 = new SpannableString(one_person_ahead);
                spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.sec_title_grey)),
                        0, one_person_ahead.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv_personahead.setText(spannable1);
            } else {

                Spannable spannable1 = new SpannableString(secondWord1 + " " + firstWord1);
                spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.sec_title_grey)),
                        0, firstWord1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv_personahead.setText(spannable1);
            }
        } else {
            tv_personahead.setVisibility(View.INVISIBLE);
        }
        icon_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onMethodMessageCallback(activelist.getYnwUuid(), String.valueOf(activelist.getId()), activelist.getBusinessName());
            }
        });
        if (header.equalsIgnoreCase("old")) {
            tv_date.setVisibility(View.VISIBLE);
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
        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_status.setTypeface(tyface1);
        if (activelist.getWaitlistStatus().

                equalsIgnoreCase("done")) {
            tv_check_in.setVisibility(View.GONE);
            tv_status.setText("Complete");
            tv_status.setVisibility(View.VISIBLE);
            tv_status.setTextColor(mContext.getResources().getColor(R.color.green));
        }
        if (activelist.getWaitlistStatus().

                equalsIgnoreCase("arrived")) {
            tv_status.setText("Arrived");
            tv_status.setVisibility(View.VISIBLE);
            tv_status.setTextColor(mContext.getResources().getColor(R.color.arrived_green));
        }
        if (activelist.getWaitlistStatus().

                equalsIgnoreCase("checkedIn")) {
            tv_status.setVisibility(View.GONE);
            tv_check_in.setVisibility(View.GONE);
        }

        if (activelist.getWaitlistStatus().

                equalsIgnoreCase("started")) {
            tv_check_in.setVisibility(View.GONE);
            tv_status.setText("Started");
            tv_status.setVisibility(View.VISIBLE);
            tv_status.setTextColor(mContext.getResources().getColor(R.color.cyan));
        }

        if (activelist.getWaitlistStatus().

                equalsIgnoreCase("prepaymentPending")) {
            tv_status.setText("Prepayment Pending");
            tv_status.setVisibility(View.VISIBLE);
            tv_status.setTextColor(mContext.getResources().getColor(R.color.gray));
        }

        /*if(header.equalsIgnoreCase("old")) {*/
        if (activelist.getWaitlistStatus().

                equalsIgnoreCase("done")) {
            icon_rate.setVisibility(View.VISIBLE);
        } else {
            icon_rate.setVisibility(View.GONE);
        }

        icon_rate.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_star_line, 0, 0);
        if (activelist.getRating() != null) {
            if (Integer.parseInt(activelist.getRating().getStars()) > 0) {

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
        } else {

            if (activelist.getWaitlistStatus().equalsIgnoreCase("checkedIn") || activelist.getWaitlistStatus().equalsIgnoreCase("arrived")) {
                icon_cancel.setVisibility(View.VISIBLE);
            } else {
                icon_cancel.setVisibility(View.GONE);
            }
        }
        Config.logV("Header Title" + header + "Title" + activelist.getBusinessName() + "Group" + groupPosition);
        return view;
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
        // TODO Auto-generated method stub
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


    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1000;
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
