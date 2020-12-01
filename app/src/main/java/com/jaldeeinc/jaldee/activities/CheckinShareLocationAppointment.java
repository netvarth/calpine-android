package com.jaldeeinc.jaldee.activities;



import  android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.BuildConfig;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.ShareLocation;
import com.jaldeeinc.jaldee.service.LocationUpdatesService;

import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jaldeeinc.jaldee.Fragment.CheckinsFragmentCopy.REQUEST_ID_MULTIPLE_PERMISSIONS;


public class CheckinShareLocationAppointment extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = CheckinShareLocationAppointment.class.getSimpleName();

    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private MyReceiver myReceiver;

    // A reference to the service used to get location updates.
    private LocationUpdatesService mService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;


    Switch shareSwitch;
    TextView bicycleIcon, modeLabel, checkinMessage, tv_title,trackingText,shareText;
    static Context mContext;
    Boolean locationStatus;
    String waitlistPhonenumber, travelMode, startTime, uuid, accountID, title;
    Drawable highlight, border;
    double latitudes, longitudes;
    LinearLayout transportLayout, saveAndClose, Laboutus;
    ShareLocation shareLocation;
    View view1, view3;
    Button btn_send, btn_cancel;
    ActiveAppointment a;
    TextView drivingIcon, walkingIcon;
    boolean isCar = true;
    boolean isWalk = false;
    boolean firstCall = true;
    LocationManager locationManager;
    String latValues, longValues, terminology, calcMode,queueStartTime,queueEndTime;
    String jaldeeDistance,from;



    // UI elements.



    // Monitors the state of the connection to the service.
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myReceiver = new MyReceiver();
        setContentView(R.layout.sharelocation);


        shareSwitch = findViewById(R.id.shareSwitch);
        checkinMessage = findViewById(R.id.checkinMessage);
        trackingText = findViewById(R.id.trackingText);
        shareText = findViewById(R.id.shareText);

        transportLayout = findViewById(R.id.transportLayout);
        Laboutus = findViewById(R.id.Laboutus);
        saveAndClose = findViewById(R.id.saveAndClose);

        modeLabel = findViewById(R.id.modeLabel);
        drivingIcon = findViewById(R.id.drivingIcon);
        walkingIcon = findViewById(R.id.walkingIcon);

        btn_send = findViewById(R.id.btn_send);
        btn_cancel = findViewById(R.id.btn_cancel);
        view1 = findViewById(R.id.view1);

        view3 = findViewById(R.id.view3);
        highlight = getResources().getDrawable(R.drawable.highlight);
        border = getResources().getDrawable(R.drawable.border_image);
        shareLocation = new ShareLocation();
        travelMode ="DRIVING";
        locationStatus = true;
        startTime = "ONEHOUR";

        // Check that the user hasn't revoked permissions by going to Settings.
        if (Utilss.requestingLocationUpdates(CheckinShareLocationAppointment.this)) {
            if (!checkPermissions()) {
                requestPermissions();
            }
        }
        LocationManager service = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Check if enabled and if not send user to the GPS settings
        if (!enabled) {
            android.app.AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setMessage("To continue, turn on device location, which uses Google location service");

            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(CheckinShareLocationAppointment.this, "Please enable the location to track your ETA", Toast.LENGTH_SHORT).show();
                }
            });

            alertDialog.setPositiveButton("Turn On GPS", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });

            alertDialog.show();
        }
        if(!enabled){
            Toast.makeText(CheckinShareLocationAppointment.this, "Please enable the location to track your ETA", Toast.LENGTH_SHORT).show();
        }



        if(!isCar){
            drivingIcon.setBackgroundResource(R.drawable.icon_driving);
            walkingIcon.setBackgroundResource(R.drawable.icons_walking_green);
        }else{
            drivingIcon.setBackgroundResource(R.drawable.icons_driving_green);
            walkingIcon.setBackgroundResource(R.drawable.icon_walking);
        }
        drivingIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                travelMode = "DRIVING";
                if(!isCar){
                    v.setBackgroundResource(R.drawable.icon_driving);
                    walkingIcon.setBackgroundResource(R.drawable.icons_walking_green);
                    isCar = !isCar; // reverse
                    isWalk = false;

                }else{
                    v.setBackgroundResource(R.drawable.icons_driving_green);
                    walkingIcon.setBackgroundResource(R.drawable.icon_walking);
                }
                ApiUpdateTravelMode();
            }
        });
        walkingIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                travelMode = "WALKING";
                if(isWalk){
                    v.setBackgroundResource(R.drawable.icon_walking);
                    drivingIcon.setBackgroundResource(R.drawable.icons_driving_green);
                    isWalk = !isWalk; // reverse
                    isCar = false;
                }else{
                    v.setBackgroundResource(R.drawable.icons_walking_green);
                    drivingIcon.setBackgroundResource(R.drawable.icon_driving);
                }
                ApiUpdateTravelMode();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            waitlistPhonenumber = extras.getString("waitlistPhonenumber");
            uuid = extras.getString("uuid");
            accountID = extras.getString("accountID");
            title = extras.getString("title");
            terminology = extras.getString("terminology");
            calcMode = extras.getString("calcMode");
            queueStartTime = extras.getString("queueStartTime");
            queueEndTime = extras.getString("queueEndTime");
            jaldeeDistance = extras.getString("jaldeeDistance");
            from = extras.getString("from");
            Log.i("calcmode",calcMode);
        }
        locationStatus = true;


        modeLabel.setVisibility(View.VISIBLE);
        ApiActiveCheckIn();
        if(from!=null && from.equalsIgnoreCase("appt") ){
            checkinMessage.setText("Your appointment is successfull !!");
            checkinMessage.setVisibility(View.VISIBLE);
        }
        else{
            checkinMessage.setVisibility(View.GONE);
        }
        if (shareSwitch.isChecked()) {
            trackingText.setVisibility(View.GONE);
            shareText.setVisibility(View.GONE);

            transportLayout.setVisibility(View.VISIBLE);
            saveAndClose.setVisibility(View.VISIBLE);
            btn_send.setVisibility(View.VISIBLE);
            btn_cancel.setVisibility(View.VISIBLE);
        } else {
         //   trackingText.setVisibility(View.VISIBLE);
            shareText.setVisibility(View.VISIBLE);

            transportLayout.setVisibility(View.GONE);
          //  btn_send.setVisibility(View.GONE);
            btn_cancel.setVisibility(View.VISIBLE);
        }
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
         if(shareSwitch.isChecked()){
                UpdateShareLiveLocation();
                mService.removeLocationUpdates();
//             if (a.getApptStatus().equalsIgnoreCase("prepaymentPending")) {
//                 finish();
//             }
//             else {
//                 if(from!=null && from.equalsIgnoreCase("appt")) {
//                     Intent checkin = new Intent(CheckinShareLocationAppointment.this, AppointmentConfirmation.class);
//                     checkin.putExtra("BookingDetails", a);
//                     checkin.putExtra("terminology", terminology);
//                     startActivity(checkin);
//                 }
//                 else{
//                     finish();
//                 }
//             }
             Intent intent = new Intent(CheckinShareLocationAppointment.this,Home.class);
             startActivity(intent);
         }
         else{
             shareSwitch.setChecked(false);
             locationStatus = false;
             UpdateShareLiveLocation();
             Toast.makeText(CheckinShareLocationAppointment.this, "Live tracking has been disabled", Toast.LENGTH_SHORT).show();
             mService.removeLocationUpdates();
//             if (a.getApptStatus().equalsIgnoreCase("prepaymentPending")) {
//                 finish();
//             }
//             else {
//                 if(from!=null && from.equalsIgnoreCase("appt")) {
//                     Intent checkin = new Intent(CheckinShareLocationAppointment.this, AppointmentConfirmation.class);
//                     checkin.putExtra("BookingDetails", a);
//                     checkin.putExtra("terminology", terminology);
//                     startActivity(checkin);
//                 }
//                 else{
//                     finish();
//                 }
//             }

             Intent intent = new Intent(CheckinShareLocationAppointment.this,Home.class);
             startActivity(intent);
         }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shareSwitch.isChecked()){
                    UpdateShareLiveLocation();
                    mService.removeLocationUpdates();
//                    if (a.getApptStatus().equalsIgnoreCase("prepaymentPending")) {
//                        finish();
//                    }
//                    else {
//                        if (from != null && from.equalsIgnoreCase("appt")) {
//                            Intent checkin = new Intent(CheckinShareLocationAppointment.this, AppointmentConfirmation.class);
//                            checkin.putExtra("BookingDetails", a);
//                            checkin.putExtra("terminology", terminology);
//                            startActivity(checkin);
//                        }
//                        else{
//                            finish();
//                        }
//                    }
                    Intent intent = new Intent(CheckinShareLocationAppointment.this,Home.class);
                    startActivity(intent);

                }
                else{
                shareSwitch.setChecked(false);
                locationStatus = false;
                UpdateShareLiveLocation();
                Toast.makeText(CheckinShareLocationAppointment.this, "Live tracking has been disabled", Toast.LENGTH_SHORT).show();
                mService.removeLocationUpdates();
//                    if (a.getApptStatus().equalsIgnoreCase("prepaymentPending")) {
//                        finish();
//                    }
//                    else {
//                        if (from != null && from.equalsIgnoreCase("appt")) {
//                            Intent checkin = new Intent(CheckinShareLocationAppointment.this, AppointmentConfirmation.class);
//                            checkin.putExtra("BookingDetails", a);
//                            checkin.putExtra("terminology", terminology);
//                            startActivity(checkin);
//                        }
//                        else{
//                            finish();
//                        }
//                    }
                    Intent intent = new Intent(CheckinShareLocationAppointment.this,Home.class);
                    startActivity(intent);
                }
            }
        });
        if(jaldeeDistance!=null){
            shareSwitch.setChecked(true);
            UpdateShareLiveLocation();
//            trackingText.setVisibility(View.GONE);
//            shareText.setVisibility(View.GONE);
        }
        else{
            shareSwitch.setChecked(false);
        //    trackingText.setVisibility(View.VISIBLE);
            shareText.setVisibility(View.VISIBLE);
        }


        shareSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    locationStatus = true;
                    if (!checkPermissions()) {
                        requestPermissions();
                    } else {
                        mService.requestLocationUpdatess();
                    }

                } else {
                    locationStatus = false;
                    transportLayout.setVisibility(View.GONE);
                   // btn_send.setVisibility(View.GONE);
                    btn_cancel.setVisibility(View.VISIBLE);
                    view1.setVisibility(View.GONE);
                    view3.setVisibility(View.GONE);
                    UpdateShareLiveLocation();
                    mService.removeLocationUpdates();
                }
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);



        // Bind to the  service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection);
            mBound = false;
        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    /**
     * Returns the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
//        boolean shouldProvideRationale =
//                ActivityCompat.shouldShowRequestPermissionRationale(this,
//                        Manifest.permission.ACCESS_FINE_LOCATION);
//
//        // Provide an additional rationale to the user. This would happen if the user denied the
//        // request previously, but didn't check the "Don't ask again" checkbox.
//        if (shouldProvideRationale) {
//            Log.i(TAG, "Displaying permission rationale to provide additional context.");
//            Snackbar.make(
//                    findViewById(R.id.activity_main),
//                    R.string.permission_rationale,
//                    Snackbar.LENGTH_INDEFINITE)
//                    .setAction(R.string.ok, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            // Request permission
//                            ActivityCompat.requestPermissions(CheckinShareLocationAppointment.this,
//                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                                    REQUEST_PERMISSIONS_REQUEST_CODE);
//                        }
//                    })
//                    .show();
//        } else {
//            Log.i(TAG, "Requesting permission");
//            // Request permission. It's possible this can be auto answered if device policy
//            // sets the permission in a given state or the user denied the permission
//            // previously and checked "Never ask again".
//            ActivityCompat.requestPermissions(CheckinShareLocationAppointment.this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    REQUEST_PERMISSIONS_REQUEST_CODE);
//        }
        int permissionLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            Config.logV("Google Not Granted" + permissionLocation);
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
               /*requestPermissions(getActivity(),
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);*/
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ID_MULTIPLE_PERMISSIONS);
                }

                Config.logV("GoogleNot Granted" + permissionLocation);
            }
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                mService.requestLocationUpdatess();
            } else {
                // Permission denied.
                Snackbar.make(
                        findViewById(R.id.activity_main),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }



    public void ApiActiveCheckIn() {
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<ActiveAppointment> call = apiService.getActiveAppointmentUUID(uuid,accountID);
        call.enqueue(new Callback<ActiveAppointment>() {
            @Override
            public void onResponse(Call<ActiveAppointment> call, Response<ActiveAppointment> response) {
                try {
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        a = response.body();
                        Log.i("fghffghfgh", response.body().toString());
                        Log.i("fghffghfgh", new Gson().toJson(response.body()));
                        shareSwitch.setText("Track me and share my arrival time with "+ response.body().getProviderAccount().getBusinessName());

                       // trackingText.setText(response.body().getProviderAccount().getBusinessName() + " won't know where you are and you can miss your turn. So Jaldee recommends to turn on sharing");
                        shareText.setText("Jaldee will not show your exact location, it will only share your arrival time with "+response.body().getProviderAccount().getBusinessName());

                        if (calcMode.equalsIgnoreCase("NoCalc")) {
                            checkinMessage.setText("Your appointment for " + response.body().getService().getName() + /*"( " + queueStartTime + "-"+queueEndTime+ " )" +"*/" with "+ response.body().getProviderAccount().getBusinessName() +", "+response.body().getLocation().getPlace() + " is successful !!");
                        } else if (terminology.equalsIgnoreCase("Check-in")) {
                            checkinMessage.setText("Your appointment for " + response.body().getService().getName() +/*"( " + queueStartTime + "-"+queueEndTime+ " )" +*/ " with "+ response.body().getProviderAccount().getBusinessName() +", "+response.body().getLocation().getPlace() + " is successful !!");
                        } else {
                            checkinMessage.setText("Your appointment for " + response.body().getService().getName() +/*"( " + queueStartTime + "-"+queueEndTime+ " )" + */" with "+ response.body().getProviderAccount().getBusinessName() +", "+response.body().getLocation().getPlace() + " is successful !!");
                        }

                        checkinMessage.setText("Your appointment for " + response.body().getService().getName() + " with "+/* "( " + queueStartTime + "-"+queueEndTime+ " )" +*/ response.body().getProviderAccount().getBusinessName() +", "+response.body().getLocation().getPlace() + " is successful !!");

                        if (a.getApptStatus().equalsIgnoreCase("prepaymentPending")) {
                            Laboutus.setVisibility(View.GONE);
                            checkinMessage.setVisibility(View.GONE);
                        } else {
                            if(from!=null && from.equalsIgnoreCase("appt")){
                            Laboutus.setVisibility(View.VISIBLE);
                            checkinMessage.setVisibility(View.VISIBLE);}
                            else{
                                checkinMessage.setVisibility(View.GONE);
                            }

                        }



                    }
                } catch (Exception e) {
                    Log.i("mnbbnmmnbbnm",e.toString());
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ActiveAppointment> call, Throwable t) {
            }
        });
    }

    private void ApiShareLiveLocation() {
        final ApiInterface apiService = ApiClient.getClient(this).create(ApiInterface.class);
        final JSONObject jsonObj = new JSONObject();
        final JSONObject geoLoc = new JSONObject();
        try {
            geoLoc.put("latitude", latValues);
            geoLoc.put("longitude", longValues);
            jsonObj.put("jaldeeGeoLocation", geoLoc);
            jsonObj.put("travelMode", travelMode);
            jsonObj.put("waitlistPhonenumber", waitlistPhonenumber);
            jsonObj.put("jaldeeStartTimeMod", startTime);
            jsonObj.put("shareLocStatus", locationStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ShareLocation> call = apiService.ShareLiveLocationAppointment(uuid, accountID, body);
        call.enqueue(new Callback<ShareLocation>() {
            @Override
            public void onResponse(Call<ShareLocation> call, Response<ShareLocation> response) {
                try {
                    if (response.code() == 200) {

                        transportLayout.setVisibility(View.VISIBLE);
                        btn_send.setVisibility(View.VISIBLE);
                        btn_cancel.setVisibility(View.VISIBLE);
                        view3.setVisibility(View.VISIBLE);
                        firstCall = false;
                        if(response.body().getJaldeeDistanceTime()!= null) {

                            int hours = response.body().getJaldeeDistanceTime().getJaldeelTravelTime().getTravelTime() / 60; //since both are ints, you get an int
                            int minutes = response.body().getJaldeeDistanceTime().getJaldeelTravelTime().getTravelTime() % 60;

                            if (response.body().getJaldeeDistanceTime().getJaldeeDistance().getDistance() > 0) {


                                if (hours < 1) {
                                    modeLabel.setText(a.getProviderAccount().getBusinessName() + " is" + " " + response.body().getJaldeeDistanceTime().getJaldeeDistance().getDistance() + "Km" + " " + "away and will take around " + minutes + " mins" + " to reach");

                                } else {
                                    modeLabel.setText(a.getProviderAccount().getBusinessName() + " is" + " " + response.body().getJaldeeDistanceTime().getJaldeeDistance().getDistance() + "Km" + " " + "away and will take around " + hours + " hours " + minutes + " mins" + " to reach");
                                }
                            } else {
                                modeLabel.setText("You are close to " + a.getProviderAccount().getBusinessName());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ShareLocation> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Location-----###########@@@@@@-------Fail--------" + t.toString());
            }
        });
    }

    private void UpdateShareLiveLocation() {
        final ApiInterface apiService = ApiClient.getClient(this).create(ApiInterface.class);
        final JSONObject jsonObj = new JSONObject();
        final JSONObject geoLoc = new JSONObject();
        try {
            geoLoc.put("latitude", latValues);
            geoLoc.put("longitude", longValues);
            jsonObj.put("jaldeeGeoLocation", geoLoc);
            jsonObj.put("travelMode", travelMode);
            jsonObj.put("waitlistPhonenumber", waitlistPhonenumber);
            jsonObj.put("jaldeeStartTimeMod", startTime);
            jsonObj.put("shareLocStatus", locationStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ShareLocation> call = apiService.UpdateShareLiveLocationAppointment(uuid, accountID, body);
        call.enqueue(new Callback<ShareLocation>() {
            @Override
            public void onResponse(Call<ShareLocation> call, Response<ShareLocation> response) {
                try {
                    if (response.code() == 200) {

                        transportLayout.setVisibility(View.VISIBLE);
                        btn_send.setVisibility(View.VISIBLE);
                        btn_cancel.setVisibility(View.VISIBLE);
                        view3.setVisibility(View.VISIBLE);
                        if (response.body().getJaldeeDistanceTime() != null) {

                            int hours = response.body().getJaldeeDistanceTime().getJaldeelTravelTime().getTravelTime() / 60; //since both are ints, you get an int
                            int minutes = response.body().getJaldeeDistanceTime().getJaldeelTravelTime().getTravelTime() % 60;
                            if (response.body().getJaldeeDistanceTime().getJaldeeDistance().getDistance() > 0) {
                                if (hours < 1) {
                                    modeLabel.setText(a.getProviderAccount().getBusinessName() + " is" + " " + response.body().getJaldeeDistanceTime().getJaldeeDistance().getDistance() + "Km" + " " + "away and will take around " + minutes + " mins" + " to reach");
                                } else {
                                    modeLabel.setText(a.getProviderAccount().getBusinessName() + " is" + " " + response.body().getJaldeeDistanceTime().getJaldeeDistance().getDistance() + "Km" + " " + "away and will take around " + hours + " hours " + minutes + " mins" + " to reach");
                                }
                            } else {
                                modeLabel.setText("You are close to " + a.getProviderAccount().getBusinessName());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ShareLocation> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Location-----###########@@@@@@-------Fail--------" + t.toString());
            }
        });
    }

    private void ApiUpdateTravelMode() {
        if(!uuid.equals("") && !accountID.equals("")){
            ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put("travelMode", travelMode);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());

            Call<ShareLocation> call;

            call = apiService.PutTravelModesAppointment(uuid, Integer.parseInt(accountID), body);


            Config.logV("Request--BODY-------------------------" + new Gson().toJson(jsonObj.toString()));

            call.enqueue(new Callback<ShareLocation>() {
                @Override
                public void onResponse(Call<ShareLocation> call, Response<ShareLocation> response) {

                    try {
                        if (response.code() == 200) {
                            if (response.body().getJaldeeDistanceTime() != null) {
                                int hours = response.body().getJaldeeDistanceTime().getJaldeelTravelTime().getTravelTime() / 60; //since both are ints, you get an int
                                int minutes = response.body().getJaldeeDistanceTime().getJaldeelTravelTime().getTravelTime() % 60;

                                if (response.body().getJaldeeDistanceTime() != null && response.body().getJaldeeDistanceTime().getJaldeeDistance().getDistance() > 0) {


                                    if (hours < 1) {
                                        modeLabel.setText(a.getProviderAccount().getBusinessName() + " is" + " " + response.body().getJaldeeDistanceTime().getJaldeeDistance().getDistance() + "Km" + " " + "away and will take around " + minutes + " mins" + " to reach");
                                    } else {
                                        modeLabel.setText(a.getProviderAccount().getBusinessName() + " is" + " " + response.body().getJaldeeDistanceTime().getJaldeeDistance().getDistance() + "Km" + " " + "away and will take around " + hours + " hours " + minutes + " mins" + " to reach");

                                    }
                                } else {
                                    modeLabel.setText("You are close to " + a.getProviderAccount().getBusinessName());
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(Call<ShareLocation> call, Throwable t) {
                    // Log error here since request failed
                    Config.logV("Location-----###########@@@@@@-------Fail--------" + t.toString());
                }
            });
        }
    }

    /**
     * Receiver for broadcasts sent by {@link LocationUpdatesService}.
     */
    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            if (location != null) {
                latValues = String.valueOf(location.getLatitude());
                longValues = String.valueOf(location.getLongitude());
                if(firstCall){
                    ApiShareLiveLocation();

                } else {
                    UpdateShareLiveLocation();
                }
                mService.removeLocationUpdates();
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        // Update the buttons state depending on whether location updates are being requested.
    }
}


