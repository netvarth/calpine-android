package com.jaldeeinc.jaldee.activities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.response.ShareLocation;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Vivek on 12/9/18.
 */

public class CheckinShareLocation extends AppCompatActivity {

    Switch shareSwitch, automaticTrackSwitch;
    TextView drivingIcon, walkingIcon, bicycleIcon, trackLabel, modeLabel, checkinMessage,tv_title;
    Context mContext;
    Boolean locationStatus;
    String waitlistPhonenumber, travelMode, startTime, latValue, longValue, uuid, accountID, title;
    Drawable highlight, border;
    double latitudes, longitudes;
    LinearLayout Lterms, transportLayout;
    ShareLocation shareLocation;
    View view1,view2,view3;
    Button btn_send,btn_cancel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sharelocation);


        shareSwitch = findViewById(R.id.shareSwitch);
        checkinMessage = findViewById(R.id.checkinMessage);
        Lterms = findViewById(R.id.Lterms);
        transportLayout = findViewById(R.id.transportLayout);
        automaticTrackSwitch = findViewById(R.id.automaticTrackSwitch);
        trackLabel = findViewById(R.id.trackLabel);
        modeLabel = findViewById(R.id.modeLabel);
        drivingIcon = findViewById(R.id.drivingIcon);
        walkingIcon = findViewById(R.id.walkingIcon);
        bicycleIcon = findViewById(R.id.bicycleIcon);
        btn_send = findViewById(R.id.btn_send);
        btn_cancel = findViewById(R.id.btn_cancel);
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);

        tv_title = findViewById(R.id.toolbartitle);
        tv_title.setText("");

        highlight = getResources().getDrawable(R.drawable.highlight);
        border = getResources().getDrawable(R.drawable.border_image);
        latValue = SharedPreference.getInstance(CheckinShareLocation.this).getStringValue("latitudes", "");
        longValue = SharedPreference.getInstance(CheckinShareLocation.this).getStringValue("longitudes", "");
        latitudes = Double.parseDouble(latValue);
        longitudes = Double.parseDouble(longValue);
        shareLocation = new ShareLocation();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            waitlistPhonenumber = extras.getString("waitlistPhonenumber");
            uuid = extras.getString("uuid");
            accountID = extras.getString("accountID");
            title = extras.getString("title");
        }
        locationStatus = true;
        startTime = "ONEHOUR";
        travelMode = "DRIVING";

        checkinMessage.setText("Your check-in is successful !!");

//        ApiWaitlist();
        ApiShareLiveLocation();
        modeLabel.setText("Hello");

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiShareLiveLocation();
                finish();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    trackLabel.setVisibility(View.VISIBLE);
                    modeLabel.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.VISIBLE);
                    view3.setVisibility(View.VISIBLE);

                } else {
                    locationStatus = false;
                    Lterms.setVisibility(View.GONE);
                    transportLayout.setVisibility(View.GONE);
                    trackLabel.setVisibility(View.GONE);
                    modeLabel.setVisibility(View.GONE);
                    view1.setVisibility(View.GONE);
                    view2.setVisibility(View.GONE);
                    view3.setVisibility(View.GONE);
                }
            }
        });

        if (automaticTrackSwitch.isChecked()) {
            trackLabel.setText("In \"Automatic tracking\" Jaldee will start tracking 1 hour prior to your turn");
        } else {
            trackLabel.setText("If \"Automatic tracking\" is turned off you will have to enable tracking manually");
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

        drivingIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                travelMode = "DRIVING";
                ApiShareLiveLocation();
            }
        });

        walkingIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                travelMode = "WALKING";
                ApiShareLiveLocation();
            }
        });

        bicycleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                travelMode = "BICYCLING";
                ApiShareLiveLocation();
            }
        });
    }

    private void ApiShareLiveLocation() {

        final ApiInterface apiService = ApiClient.getClient(this).create(ApiInterface.class);
        final JSONObject jsonObj = new JSONObject();
        final JSONObject geoLoc = new JSONObject();
        try {
            geoLoc.put("latitude", latitudes);
            geoLoc.put("longitude", longitudes);
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
                        modeLabel.setText("From your current location, you are" + " " + response.body().getJaldeeDistanceTime().getJaldeeDistance().getDistance() + "Km" + " " + "away and will take around " + response.body().getJaldeeDistanceTime().getJaldeelTravelTime().getTravelTime().toString() + " " + response.body().getJaldeeDistanceTime().getJaldeelTravelTime().getTimeUnit() + " to reach");
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

//
//    private void ApiWaitlist() {
//
//        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
//        Call<List<ResponseBody>> cal;
//        cal = apiService.waitlist(uuid, accountID);
//        cal.enqueue(new Callback<List<ResponseBody>>() {
//            @Override
//            public void onResponse(Call<List<ResponseBody>> call, Response<List<ResponseBody>> response) {
//                try {
//                    if (response.code() == 200) {
//                        Log.i("asdsad", "asdas");
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<ResponseBody>> cal, Throwable t) {
//                // Log error here since request failed
//                Config.logV("Location-----###########@@@@@@-------Fail--------" + t.toString());
//            }
//        });
//    }

}
