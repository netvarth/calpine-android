package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckInConfirmation extends AppCompatActivity {

    ActiveCheckIn activeCheckInInfo = new ActiveCheckIn();
    private TextView tvProviderName, tvServiceName, tvTokenNumber, tvTimeWindow, tvProvider, tvConfirmationNumber, tvPeopleAhead, tvEstWaitTime, tvStatusLink, tvPreInfoTitle, tvPreInfo, tvPostInfo, tvPostInfoTitle, tvTerm;
    private LinearLayout llwaitTime, llToken, llProvider;
    private CardView cvPeople, cvOk;
    String terminology;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_confirmation);

        Intent i = getIntent();
        activeCheckInInfo = (ActiveCheckIn) i.getSerializableExtra("BookingDetails");
        terminology = i.getStringExtra("terminology");

        initializations();


        if (activeCheckInInfo != null) {

            if (activeCheckInInfo.getProviderAccount() != null) {

                String name = activeCheckInInfo.getProviderAccount().getBusinessName();
                name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                tvProviderName.setText(name);
            }


            if (activeCheckInInfo.getService() != null) {
                if (activeCheckInInfo.getService().getName() != null) {

                    String name2 = activeCheckInInfo.getService().getName();
                    name2 = name2.substring(0, 1).toUpperCase() + name2.substring(1).toLowerCase();
                    tvServiceName.setText(name2);
                }

            }

            if (activeCheckInInfo.getShowToken().equalsIgnoreCase("true")) {
                if (activeCheckInInfo.getToken() != 0) {
                    llToken.setVisibility(View.VISIBLE);
                    tvTokenNumber.setText("#" + activeCheckInInfo.getToken());
                }
                else{
                    llToken.setVisibility(View.GONE);
                }

            } else {

                llToken.setVisibility(View.GONE);
            }

            if (activeCheckInInfo.getCheckInTime() != null) {

                // to format date
                String date;
                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
                Date newDate = null;
                try {
                    newDate = spf.parse(activeCheckInInfo.getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                spf = new SimpleDateFormat("dd, MMM yyyy");
                date = spf.format(newDate);

                tvTimeWindow.setText(date + "  " + activeCheckInInfo.getQueue().getQueueStartTime() + " - " + activeCheckInInfo.getQueue().getQueueEndTime());
            }

            if (activeCheckInInfo.getProvider() != null) {

                if (terminology != null) {

                    String term = terminology;
                    term = term.substring(0, 1).toUpperCase() + term.substring(1).toLowerCase();
                    tvTerm.setText(term + "  :  ");
                }

                if (activeCheckInInfo.getProvider().getFirstName() != null) {

                    llProvider.setVisibility(View.VISIBLE);
                    tvProvider.setText(activeCheckInInfo.getProvider().getFirstName() + " " + activeCheckInInfo.getProvider().getLastName());
                } else {
                    llProvider.setVisibility(View.GONE);
                }

            } else {
                llProvider.setVisibility(View.GONE);
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

            if (activeCheckInInfo.getPersonsAhead() >= 0) {
                cvPeople.setVisibility(View.VISIBLE);
                tvPeopleAhead.setText(String.valueOf(activeCheckInInfo.getPersonsAhead()));
            } else {

                cvPeople.setVisibility(View.GONE);

            }

            if (activeCheckInInfo.getCalculationMode() != null) {

                if (!activeCheckInInfo.getCalculationMode().equalsIgnoreCase("NoCalc")) {

                    llwaitTime.setVisibility(View.VISIBLE);
                } else {
                    llwaitTime.setVisibility(View.GONE);
                }
            }

            if (activeCheckInInfo.getAppxWaitingTime() != -1) {

                tvEstWaitTime.setText(activeCheckInInfo.getAppxWaitingTime() + " " + "Minutes");
            }

            if (activeCheckInInfo.getService() != null) {

                if (activeCheckInInfo.getService().isPreInfoEnabled()) {

                    tvPreInfo.setVisibility(View.GONE);
                    tvPreInfoTitle.setVisibility(View.GONE);
                    tvPreInfoTitle.setText(activeCheckInInfo.getService().getPreInfoTitle());
                    tvPreInfo.setText(Html.fromHtml(activeCheckInInfo.getService().getPreInfoText()));
                }

                if (activeCheckInInfo.getService().isPostInfoEnabled()) {

                    tvPostInfoTitle.setText(activeCheckInInfo.getService().getPostInfoTitle());
                    tvPostInfo.setText(Html.fromHtml(activeCheckInInfo.getService().getPostInfoText()));
                }
            }

            cvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent home = new Intent(CheckInConfirmation.this, Home.class);
                    startActivity(home);
                    finish();
                }
            });

        }

    }


    private void initializations() {

        tvProviderName = findViewById(R.id.tv_providerName);
        tvServiceName = findViewById(R.id.tv_serviceName);
        tvTokenNumber = findViewById(R.id.tv_tokenNumber);
        tvTimeWindow = findViewById(R.id.tv_timeWindow);
        tvProvider = findViewById(R.id.tv_provider);
        tvConfirmationNumber = findViewById(R.id.tv_confrmNo);
        tvPeopleAhead = findViewById(R.id.tv_peopleAhead);
        tvEstWaitTime = findViewById(R.id.tv_waitTime);
        tvStatusLink = findViewById(R.id.tv_currentStatusLink);
        tvPreInfo = findViewById(R.id.tv_preInfo);
        tvPreInfoTitle = findViewById(R.id.tv_preInfoTitle);
        tvPostInfo = findViewById(R.id.tv_postInfo);
        tvPostInfoTitle = findViewById(R.id.tv_postInfoTitle);
        llwaitTime = findViewById(R.id.ll_waitTime);
        llToken = findViewById(R.id.ll_token);
        cvPeople = findViewById(R.id.cv_people);
        llProvider = findViewById(R.id.ll_provider);
        cvOk = findViewById(R.id.cv_ok);
        tvTerm = findViewById(R.id.tv_term);
    }

    @Override
    public void onBackPressed() {
        Intent home = new Intent(CheckInConfirmation.this, Home.class);
        startActivity(home);
        finish();
        super.onBackPressed();
    }

}