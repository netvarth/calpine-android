package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;

public class CheckInConfirmation extends AppCompatActivity {

    ActiveCheckIn activeCheckInInfo = new ActiveCheckIn();
    private TextView tvProviderName, tvServiceName, tvTokenNumber, tvTimeWindow, tvLocation, tvConfirmationNumber, tvPeopleAhead, tvEstWaitTime, tvStatusLink,tvPreInfoTitle,tvPreInfo,tvPostInfo,tvPostInfoTitle;
    private LinearLayout llwaitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_confirmation);

        Intent i = getIntent();
        activeCheckInInfo = (ActiveCheckIn) i.getSerializableExtra("BookingDetails");

        initializations();


        if (activeCheckInInfo != null) {

            if (activeCheckInInfo.getProviderAccount() != null) {

                tvProviderName.setText(activeCheckInInfo.getProviderAccount().getBusinessName());
            }

            if (activeCheckInInfo.getService() != null) {
                if (activeCheckInInfo.getService().getName() != null) {

                    tvServiceName.setText(activeCheckInInfo.getService().getName());
                }

            }

            if (activeCheckInInfo.getToken() != 0) {

                tvTokenNumber.setText("#"+String.valueOf(activeCheckInInfo.getToken()));
            }

            if (activeCheckInInfo.getCheckInTime() != null) {

                tvTimeWindow.setText(activeCheckInInfo.getDate() + "  " + activeCheckInInfo.getQueue().getQueueStartTime() + " - " + activeCheckInInfo.getQueue().getQueueEndTime());
            }

            if (activeCheckInInfo.getQueue() != null) {

                if (activeCheckInInfo.getQueue().getLocation().getPlace() != null) {

                    tvLocation.setText(activeCheckInInfo.getQueue().getLocation().getPlace());

                    tvLocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (activeCheckInInfo.getLocation().getGoogleMapUrl() != null) {
                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                        Uri.parse(activeCheckInInfo.getQueue().getLocation().getGoogleMapUrl()));
                                startActivity(intent);
                            }
                        }
                    });

                }
            }

            if (activeCheckInInfo.getCheckinEncId() != null) {

                tvConfirmationNumber.setText(activeCheckInInfo.getCheckinEncId());
                String statusUrl = Constants.URL + activeCheckInInfo.getCheckinEncId();
                tvStatusLink.setText(statusUrl);

                tvStatusLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse(statusUrl));
                        startActivity(intent);
                    }
                });
            }

            tvPeopleAhead.setText(String.valueOf(activeCheckInInfo.getPersonsAhead()));

            if (activeCheckInInfo.getCalculationMode() != null){

                if (!activeCheckInInfo.getCalculationMode().equalsIgnoreCase("NoCalc")){

                    llwaitTime.setVisibility(View.VISIBLE);
                }
                else {
                    llwaitTime.setVisibility(View.GONE);
                }
            }

            if (activeCheckInInfo.getAppxWaitingTime() != -1) {

                tvEstWaitTime.setText(String.valueOf(activeCheckInInfo.getAppxWaitingTime())+" "+"Minutes");
            }

            if (activeCheckInInfo.getService() != null){

                if (activeCheckInInfo.getService().isPreInfoEnabled()){

                    tvPreInfoTitle.setText(activeCheckInInfo.getService().getPreInfoTitle());
                    tvPreInfo.setText(Html.fromHtml(activeCheckInInfo.getService().getPreInfoText()));
                }

                if (activeCheckInInfo.getService().isPostInfoEnabled()){

                    tvPostInfoTitle.setText(activeCheckInInfo.getService().getPostInfoTitle());
                    tvPostInfo.setText(Html.fromHtml(activeCheckInInfo.getService().getPostInfoText()));
                }
            }


        }

    }


    private void initializations() {

        tvProviderName = findViewById(R.id.tv_providerName);
        tvServiceName = findViewById(R.id.tv_serviceName);
        tvTokenNumber = findViewById(R.id.tv_tokenNumber);
        tvTimeWindow = findViewById(R.id.tv_timeWindow);
        tvLocation = findViewById(R.id.tv_location);
        tvConfirmationNumber = findViewById(R.id.tv_confrmNo);
        tvPeopleAhead = findViewById(R.id.tv_peopleAhead);
        tvEstWaitTime = findViewById(R.id.tv_waitTime);
        tvStatusLink = findViewById(R.id.tv_currentStatusLink);
        tvPreInfo = findViewById(R.id.tv_preInfo);
        tvPreInfoTitle = findViewById(R.id.tv_preInfoTitle);
        tvPostInfo = findViewById(R.id.tv_postInfo);
        tvPostInfoTitle = findViewById(R.id.tv_postInfoTitle);
        llwaitTime = findViewById(R.id.ll_waitTime);
    }

    @Override
    public void onBackPressed() {
        Intent home = new Intent(CheckInConfirmation.this,Home.class);
        startActivity(home);
        super.onBackPressed();
    }

}