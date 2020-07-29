package com.jaldeeinc.jaldee.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveTrackService extends Service{


    public LiveTrackService() {
        super();
    }

    Date date1, date2;
    private MyReceiver myReceiver;
    private LocationUpdatesService mService = null;
    private boolean mBound = false;
    Double dist;
    Double latStartOne;
    Double longStartOne;


//

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public LiveTrackService getService() {
            return LiveTrackService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    boolean terminateStatus = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.i("Rebind", intent.toString());
        mService.requestLocationUpdatess();
    }

    private final ServiceConnection mLTServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            Log.i("IBinderService", service.toString());
            Log.i("Components", name.toString());

            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            Log.i("onServiceConnected123", "Working");
            mService.requestLocationUpdatess();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("onServiceConnected123", "Remove");
            mService = null;
            mBound = false;
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        super.onStartCommand(intent, flags, startId);
        Log.i("onStartCommandIn", "mServiceNull");
        myReceiver = new MyReceiver();
        this.bindService(new Intent(this, LocationUpdatesService.class), mLTServiceConnection,
                Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        this.unbindService(mLTServiceConnection);
        super.onDestroy();
    }
    /**
     * Receiver for broadcasts sent by {@link LocationUpdatesService}.
     */
    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            Log.i("OnReceiveLocation", "Before Checking location");
            if (location != null) {

                if (latStartOne == null) {
                    latStartOne = location.getLatitude();
                }
                if (longStartOne == null) {
                    longStartOne = location.getLongitude();
                }

                if (Config.isOnline(getApplicationContext())) {


                    double theta = longStartOne - location.getLongitude();
                    dist = Math.sin(deg2rad(latStartOne))
                            * Math.sin(deg2rad(location.getLatitude()))
                            + Math.cos(deg2rad(latStartOne))
                            * Math.cos(deg2rad(location.getLatitude()))
                            * Math.cos(deg2rad(theta));
                    dist = Math.acos(dist);
                    dist = rad2deg(dist);
                    dist = dist * 60 * 1.1515 * 1000;

                    String distValue = String.format("%.2f", dist);
                    Log.i("GeoLatLong", String.valueOf(latStartOne + "" + longStartOne + "" + location.getLatitude() + "" + location.getLongitude()));
                    Log.i("GeoLatLong", distValue);
                    if (dist > 100) {
                        ApiTodayChekInList(location);
                        latStartOne = location.getLatitude();
                        longStartOne = location.getLongitude();
                    }
                }
            }
        }
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
    private void ApiTodayChekInList(final Location location) {
        final ApiInterface apiService =
                ApiClient.getClient(LiveTrackService.this).create(ApiInterface.class);
        Map<String, String> filter = new HashMap<String, String>();
        filter.put("waitlistStatus-neq", "failed,prepaymentPending");
        Call<ArrayList<ActiveCheckIn>> call = apiService.getActiveCheckIn(filter);
        call.enqueue(new Callback<ArrayList<ActiveCheckIn>>() {
            @Override
            public void onResponse(Call<ArrayList<ActiveCheckIn>> call, Response<ArrayList<ActiveCheckIn>> response) {
                try {
                    if (response.code() == 200) {

                        ArrayList<ActiveCheckIn> mCheckTodayFutureList = response.body();
                        Log.i("ApiTodayChekInListSize", Integer.toString(mCheckTodayFutureList.size()));
                        ArrayList<ActiveCheckIn> pollCheckinsList = new ArrayList<>();

                        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                        for (int i = 0; i < mCheckTodayFutureList.size(); i++) {


                            if (date.equalsIgnoreCase(mCheckTodayFutureList.get(i).getDate()) && mCheckTodayFutureList.get(i).getWaitlistStatus().equals("checkedIn") && mCheckTodayFutureList.get(i).getJaldeeStartTimeType() != null
                                    && (mCheckTodayFutureList.get(i).getJaldeeStartTimeType().equals("AFTERSTART") || mCheckTodayFutureList.get(i).getJaldeeStartTimeType().equals("ONEHOUR"))) {
                                pollCheckinsList.add(response.body().get(i));
                            }
                        }
                        Log.i("ApiTodayPollingListSize", Integer.toString(pollCheckinsList.size()));
                        if (pollCheckinsList.size() > 0) {
                            for (int i = 0; i < pollCheckinsList.size(); i++) {
                                final ActiveCheckIn activeCheckin = pollCheckinsList.get(i);
                                if (activeCheckin.getJaldeeWaitlistDistanceTime() != null) {
                                    if (activeCheckin.getJaldeeStartTimeType().equals("AFTERSTART")) {
                                        ApiInterface apiService = ApiClient.getClient(LiveTrackService.this).create(ApiInterface.class);
                                        final Call<ResponseBody> cal;
                                        cal = apiService.StatusTracking(activeCheckin.getYnwUuid(), activeCheckin.getId());
                                        cal.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                try {
                                                    if (response.code() == 200) {
                                                        if (response.body().string().equals("true")) {
                                                            updateLatLong(location, activeCheckin);
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
                                    } else if (activeCheckin.getJaldeeStartTimeType().equals("ONEHOUR")) {
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                        String currentDateandTime = simpleDateFormat.format(new Date());
                                        if (activeCheckin.getDate() != null && activeCheckin.getJaldeeWaitlistDistanceTime() != null) {
                                            String pollingTime = activeCheckin.getDate() + " " + activeCheckin.getJaldeeWaitlistDistanceTime().getPollingTime();
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
                                                    updateLatLong(location, activeCheckin);
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        } else {
                            Log.i("pollingElse", "pollingelse");
                           stopSelf();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ActiveCheckIn>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
            }
        });


    }

    private void updateLatLong(Location location, ActiveCheckIn activeCheckin) {
        Log.i("Locationcalled", "LocationCalled2");
        ApiInterface apiService = ApiClient.getClient(LiveTrackService.this).create(ApiInterface.class);
        JSONObject jsonObj = new JSONObject();
        try {
            Log.i("LatlongTest", String.valueOf(location.getLatitude()));
            Log.i("LatlongTest", String.valueOf(location.getLongitude()));
            jsonObj.put("latitude", location.getLatitude());
            jsonObj.put("longitude", location.getLongitude());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> callLivetrack;
        Log.i("ActiveCheckinInfo", new Gson().toJson(activeCheckin));
        callLivetrack = apiService.UpdateLatLong(activeCheckin.getYnwUuid(), activeCheckin.getProviderAccount().getId(), body);
        callLivetrack.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> callLivetrack, Response<ResponseBody> response) {
                try {
                    if (response.code() == 200) {
                        Log.i("MELVIN", "MELVIN");
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
}


