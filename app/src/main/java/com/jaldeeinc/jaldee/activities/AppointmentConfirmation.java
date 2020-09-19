package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;

public class AppointmentConfirmation extends AppCompatActivity {

    ActiveCheckIn activeCheckInInfo = new ActiveCheckIn();
    private TextView tvProviderName, tvServiceName, tvTimeWindow, tvLocation, tvConfirmationNumber, tvStatusLink,tvPreInfoTitle,tvPreInfo,tvPostInfo,tvPostInfoTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_confirmation);
        Bundle extras = getIntent().getExtras();
        if (extras != null){

            activeCheckInInfo = (ActiveCheckIn) extras.getSerializable("BookingDetails");
        }

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

            if (activeCheckInInfo.getAppmtTime() != null) {

                tvTimeWindow.setText(activeCheckInInfo.getAppmtDate()+" " + activeCheckInInfo.getAppmtTime().split("-")[0]);
            }

            if (activeCheckInInfo.getLocation() != null) {

                if (activeCheckInInfo.getLocation().getPlace() != null) {

                    tvLocation.setText(activeCheckInInfo.getLocation().getPlace());

                    tvLocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (activeCheckInInfo.getLocation().getGoogleMapUrl() != null) {
                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                        Uri.parse(activeCheckInInfo.getLocation().getGoogleMapUrl()));
                                startActivity(intent);
                            }
                        }
                    });

                }
            }

            if (activeCheckInInfo.getAppointmentEncId() != null) {

                tvConfirmationNumber.setText(activeCheckInInfo.getAppointmentEncId());
                String statusUrl = Constants.URL + activeCheckInInfo.getAppointmentEncId();
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

            if (activeCheckInInfo.getService() != null){

                if (activeCheckInInfo.getService().isPreInfoEnabled()){

                    tvPreInfoTitle.setText(activeCheckInInfo.getService().getPreInfoTitle());
                    tvPreInfo.setText(Html.fromHtml(activeCheckInInfo.getService().getPreInfoText()));
                }
            }


        }

    }

    private void initializations() {

        tvProviderName = findViewById(R.id.tv_providerName);
        tvServiceName = findViewById(R.id.tv_serviceName);
        tvTimeWindow = findViewById(R.id.tv_timeWindow);
        tvLocation = findViewById(R.id.tv_location);
        tvConfirmationNumber = findViewById(R.id.tv_confrmNo);
        tvStatusLink = findViewById(R.id.tv_currentStatusLink);
        tvPreInfo = findViewById(R.id.tv_preInfo);
        tvPreInfoTitle = findViewById(R.id.tv_preInfoTitle);
        tvPostInfo = findViewById(R.id.tv_postInfo);
        tvPostInfoTitle = findViewById(R.id.tv_postInfoTitle);
    }

}