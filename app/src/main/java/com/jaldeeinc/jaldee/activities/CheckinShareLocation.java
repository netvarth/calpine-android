package com.jaldeeinc.jaldee.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.ShareLocation;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Vivek on 12/9/18.
 */

public class CheckinShareLocation extends AppCompatActivity implements LocationListener {

    Switch shareSwitch, automaticTrackSwitch;
    TextView bicycleIcon, trackLabel, modeLabel, checkinMessage, tv_title;
    static Context mContext;
    Boolean locationStatus;
    String waitlistPhonenumber, travelMode, startTime, latValue, longValue, uuid, accountID, title;
    Drawable highlight, border;
    double latitudes, longitudes;
    LinearLayout Lterms, transportLayout, saveAndClose;
    ShareLocation shareLocation;
    View view1, view2, view3;
    Button btn_send, btn_cancel;
    ActiveCheckIn a;
    TextView drivingIcon, walkingIcon;
    boolean isCar = true;
    boolean isWalk = false;
    boolean firstCall = true;
    LocationManager locationManager;
    String latValues,longValues;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sharelocation);

        shareSwitch = findViewById(R.id.shareSwitch);
        checkinMessage = findViewById(R.id.checkinMessage);
        Lterms = findViewById(R.id.Lterms);
        transportLayout = findViewById(R.id.transportLayout);
        saveAndClose = findViewById(R.id.saveAndClose);
        automaticTrackSwitch = findViewById(R.id.automaticTrackSwitch);
        trackLabel = findViewById(R.id.trackLabel);
        modeLabel = findViewById(R.id.modeLabel);
        drivingIcon = findViewById(R.id.drivingIcon);
        walkingIcon = findViewById(R.id.walkingIcon);

        btn_send = findViewById(R.id.btn_send);
        btn_cancel = findViewById(R.id.btn_cancel);
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
        highlight = getResources().getDrawable(R.drawable.highlight);
        border = getResources().getDrawable(R.drawable.border_image);
        latValue = SharedPreference.getInstance(CheckinShareLocation.this).getStringValue("latitudes", "");
        longValue = SharedPreference.getInstance(CheckinShareLocation.this).getStringValue("longitudes", "");
        latitudes = Double.parseDouble(latValue);
        longitudes = Double.parseDouble(longValue);
        shareLocation = new ShareLocation();
        travelMode ="DRIVING";
        locationStatus = true;
        startTime = "ONEHOUR";
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


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


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            waitlistPhonenumber = extras.getString("waitlistPhonenumber");
            uuid = extras.getString("uuid");
            accountID = extras.getString("accountID");
            title = extras.getString("title");
        }
        ApiActiveCheckIn();
        if(shareSwitch.isChecked()){
            Lterms.setVisibility(View.VISIBLE);
            transportLayout.setVisibility(View.VISIBLE);
            saveAndClose.setVisibility(View.VISIBLE);
            btn_send.setVisibility(View.VISIBLE);
            btn_cancel.setVisibility(View.VISIBLE);
        }else{
            Lterms.setVisibility(View.GONE);
            transportLayout.setVisibility(View.GONE);
            btn_send.setVisibility(View.GONE);
            btn_cancel.setVisibility(View.VISIBLE);
        }
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(automaticTrackSwitch.isChecked()){
                    Toast.makeText(CheckinShareLocation.this, "Automatic live tracking enabled", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(CheckinShareLocation.this, "Automatic live tracking enabled", Toast.LENGTH_SHORT).show();
                }
                UpdateShareLiveLocation();
                locationManager.removeUpdates(CheckinShareLocation.this);
                finish();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareSwitch.setChecked(false);
                locationStatus = false;
                UpdateShareLiveLocation();
                Toast.makeText(CheckinShareLocation.this, "Live tracking has been disabled", Toast.LENGTH_SHORT).show();
                locationManager.removeUpdates(CheckinShareLocation.this);
                finish();
            }
        });
        shareSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    locationStatus = true;
                    Lterms.setVisibility(View.VISIBLE);
                    transportLayout.setVisibility(View.VISIBLE);
                    btn_send.setVisibility(View.VISIBLE);
                    btn_cancel.setVisibility(View.VISIBLE);
                    trackLabel.setVisibility(View.VISIBLE);
                    modeLabel.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.VISIBLE);
                    view3.setVisibility(View.VISIBLE);
                    if(firstCall){
                        ApiShareLiveLocation();

                    } else {
                        UpdateShareLiveLocation();
                    }
                } else {
                    locationStatus = false;
                    Lterms.setVisibility(View.GONE);
                    transportLayout.setVisibility(View.GONE);
                    btn_send.setVisibility(View.GONE);
                    btn_cancel.setVisibility(View.VISIBLE);
                    trackLabel.setVisibility(View.GONE);
                    modeLabel.setVisibility(View.GONE);
                    view1.setVisibility(View.GONE);
                    view2.setVisibility(View.GONE);
                    view3.setVisibility(View.GONE);
                    UpdateShareLiveLocation();
                    locationManager.removeUpdates(CheckinShareLocation.this);
                }
            }
        });
        if (automaticTrackSwitch.isChecked()) {
            trackLabel.setText("In \"Automatic tracking\" Jaldee will start tracking 1 hour prior to your turn");
        } else {
            trackLabel.setText("If \"Automatic tracking\" is turned off, you will have to enable tracking manually from your check-in");
        }
        automaticTrackSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startTime = "ONEHOUR";
                    trackLabel.setText("In \"Automatic tracking\" Jaldee will start tracking 1 hour prior to your turn");
                } else {
                    startTime = "AFTERSTART";
                    trackLabel.setText("If \"Automatic tracking\" is turned off you will have to enable tracking manually");
                }
            }
        });
    }

    public void ApiActiveCheckIn() {
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<ActiveCheckIn> call = apiService.getActiveCheckInUUID(uuid,accountID);
        call.enqueue(new Callback<ActiveCheckIn>() {
            @Override
            public void onResponse(Call<ActiveCheckIn> call, Response<ActiveCheckIn> response) {
                try {
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        a = response.body();
                        Log.i("fghffghfgh",response.body().toString());
                        checkinMessage.setText("Your check-in for "+response.body().getService().getName()+" is successful !!");
                        shareSwitch.setText("Allow "+response.body().getProvider().getBusinessName()+" to track your ETA");
                    }
                } catch (Exception e) {
                    Log.i("mnbbnmmnbbnm",e.toString());
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ActiveCheckIn> call, Throwable t) {
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
        Call<ShareLocation> call = apiService.ShareLiveLocation(uuid, accountID, body);
        call.enqueue(new Callback<ShareLocation>() {
            @Override
            public void onResponse(Call<ShareLocation> call, Response<ShareLocation> response) {
                try {
                    if (response.code() == 200) {
                        firstCall = false;
                        if(response.body().getJaldeeDistanceTime()!= null) {

                            int hours = response.body().getJaldeeDistanceTime().getJaldeelTravelTime().getTravelTime() / 60; //since both are ints, you get an int
                            int minutes = response.body().getJaldeeDistanceTime().getJaldeelTravelTime().getTravelTime() % 60;

                            if (response.body().getJaldeeDistanceTime().getJaldeeDistance().getDistance() > 0) {


                                if (hours < 1) {
                                    modeLabel.setText("From your current location, you are" + " " + response.body().getJaldeeDistanceTime().getJaldeeDistance().getDistance() + "Km" + " " + "away and will take around " + minutes + " mins" + " to reach");

                                } else {
                                    modeLabel.setText("From your current location, you are" + " " + response.body().getJaldeeDistanceTime().getJaldeeDistance().getDistance() + "Km" + " " + "away and will take around " + hours + " hours " + minutes + " mins" + " to reach");
                                }
                            } else {
                                modeLabel.setText("You are close to " + a.getProvider().getBusinessName());
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
        Call<ShareLocation> call = apiService.UpdateShareLiveLocation(uuid, accountID, body);
        call.enqueue(new Callback<ShareLocation>() {
            @Override
            public void onResponse(Call<ShareLocation> call, Response<ShareLocation> response) {
                try {
                    if (response.code() == 200) {
                        if (response.body().getJaldeeDistanceTime() != null) {

                            int hours = response.body().getJaldeeDistanceTime().getJaldeelTravelTime().getTravelTime() / 60; //since both are ints, you get an int
                            int minutes = response.body().getJaldeeDistanceTime().getJaldeelTravelTime().getTravelTime() % 60;
                            if (response.body().getJaldeeDistanceTime().getJaldeeDistance().getDistance() > 0) {
                                if (hours < 1) {
                                    modeLabel.setText("From your current location, you are" + " " + response.body().getJaldeeDistanceTime().getJaldeeDistance().getDistance() + "Km" + " " + "away and will take around " + minutes + " mins" + " to reach");
                                } else {
                                    modeLabel.setText("From your current location, you are" + " " + response.body().getJaldeeDistanceTime().getJaldeeDistance().getDistance() + "Km" + " " + "away and will take around " + hours + " hours " + minutes + " mins" + " to reach");
                                }
                            } else {
                                modeLabel.setText("You are close to " + a.getProvider().getBusinessName());
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

            call = apiService.PutTravelModes(uuid, Integer.parseInt(accountID), body);


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
                                        modeLabel.setText("From your current location, you are" + " " + response.body().getJaldeeDistanceTime().getJaldeeDistance().getDistance() + "Km" + " " + "away and will take around " + minutes + " mins" + " to reach");
                                    } else {
                                        modeLabel.setText("From your current location, you are" + " " + response.body().getJaldeeDistanceTime().getJaldeeDistance().getDistance() + "Km" + " " + "away and will take around " + hours + " hours " + minutes + " mins" + " to reach");

                                    }
                                } else {
                                    modeLabel.setText("You are close to " + a.getProvider().getBusinessName());
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

    @Override
    public void onLocationChanged(Location location) {
        Log.i("LatitudeCurrent",String.valueOf(location.getLatitude()));
        Log.i("LongitudeCurrent",String.valueOf(location.getLongitude()));
        latValues = String.valueOf(location.getLatitude());
        longValues = String.valueOf(location.getLongitude());
        locationManager.removeUpdates(CheckinShareLocation.this);

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
}