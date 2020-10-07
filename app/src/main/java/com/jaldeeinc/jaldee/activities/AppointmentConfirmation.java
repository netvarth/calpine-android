package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppointmentConfirmation extends AppCompatActivity {

    ActiveAppointment activeCheckInInfo = new ActiveAppointment();
    private TextView tvProviderName, tvServiceName, tvTimeWindow, tvProvider, tvConfirmationNumber, tvStatusLink, tvPreInfoTitle, tvPreInfo, tvPostInfo, tvPostInfoTitle, tvTerm;
    private LinearLayout llProvider;
    CardView cvOk;
    private String terminology;
    ImageView icon_service;

    @Override
    public void onBackPressed() {
        Intent home = new Intent(AppointmentConfirmation.this, Home.class);
        startActivity(home);
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_confirmation);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            activeCheckInInfo = (ActiveAppointment) extras.getSerializable("BookingDetails");
            terminology = extras.getString("terminology");
        }

        initializations();


        if (activeCheckInInfo != null) {

            if (activeCheckInInfo.getProviderAccount() != null) {

                String name = activeCheckInInfo.getProviderAccount().getBusinessName();
                name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                tvProviderName.setText(name);
            }

            if (terminology != null) {

                String term = terminology;
                term = term.substring(0, 1).toUpperCase() + term.substring(1).toLowerCase();
                tvTerm.setText(term);
            }

            if (activeCheckInInfo.getService() != null) {
                if (activeCheckInInfo.getService().getName() != null) {

                    String name2 = activeCheckInInfo.getService().getName();
                    name2 = name2.substring(0, 1).toUpperCase() + name2.substring(1).toLowerCase();
                    tvServiceName.setText(name2);

                    try{
                        if(activeCheckInInfo.getService().getServiceType().equalsIgnoreCase("virtualService")){
                            icon_service.setVisibility(View.VISIBLE);
                            icon_service.setY(15);

                            if(activeCheckInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")){
                                icon_service.setImageResource(R.drawable.zoomicon_sized);
                            }
                            else if(activeCheckInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")){
                                icon_service.setImageResource(R.drawable.googlemeet_sized);
                            }
                            else if(activeCheckInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")){
                                icon_service.setImageResource(R.drawable.whatsappicon_sized);

                            }
                            else if(activeCheckInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")){
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
                }

            }

            if (activeCheckInInfo.getAppmtTime() != null) {

                // to format time
                String formattedTime = "";
                String time = activeCheckInInfo.getAppmtTime().split("-")[0];
                try {
                    final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    final Date dateObj = sdf.parse(time);
                    time = new SimpleDateFormat("hh:mm aa").format(dateObj);
                    formattedTime = time.replace("am", "AM").replace("pm", "PM");

                } catch (final ParseException e) {
                    e.printStackTrace();
                }

                // to format date
                String date;
                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
                Date newDate = null;
                try {
                    newDate = spf.parse(activeCheckInInfo.getAppmtDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                spf = new SimpleDateFormat("dd, MMM yyyy");
                date = spf.format(newDate);
                tvTimeWindow.setText(date + "  " + formattedTime);
            }


            if (activeCheckInInfo.getProvider() != null) {

                if (terminology != null) {

                    String term = terminology;
                    term = term.substring(0, 1).toUpperCase() + term.substring(1).toLowerCase();
                    tvTerm.setText(term+"  :  ");
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

            if (activeCheckInInfo.getAppointmentEncId() != null) {

                tvConfirmationNumber.setText(activeCheckInInfo.getAppointmentEncId());
                String statusUrl = Constants.URL +"status/"+activeCheckInInfo.getAppointmentEncId();
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

            if (activeCheckInInfo.getService() != null) {

                if (activeCheckInInfo.getService().isPostInfoEnabled()) {

                    tvPostInfoTitle.setText(activeCheckInInfo.getService().getPostInfoTitle());
                    tvPostInfo.setText(Html.fromHtml(activeCheckInInfo.getService().getPostInfoText()));
                }
            }

            cvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent home = new Intent(AppointmentConfirmation.this, Home.class);
                    startActivity(home);
                    finish();

                }
            });


        }

    }

    private void initializations() {

        tvProviderName = findViewById(R.id.tv_providerName);
        tvServiceName = findViewById(R.id.tv_serviceName);
        tvTimeWindow = findViewById(R.id.tv_timeWindow);
        tvProvider = findViewById(R.id.tv_provider);
        tvConfirmationNumber = findViewById(R.id.tv_confrmNo);
        tvStatusLink = findViewById(R.id.tv_currentStatusLink);
        tvPreInfo = findViewById(R.id.tv_preInfo);
        tvPreInfoTitle = findViewById(R.id.tv_preInfoTitle);
        tvPostInfo = findViewById(R.id.tv_postInfo);
        tvPostInfoTitle = findViewById(R.id.tv_postInfoTitle);
        llProvider = findViewById(R.id.ll_provider);
        cvOk = findViewById(R.id.cv_ok);
        tvTerm = findViewById(R.id.tv_term);
        icon_service = findViewById(R.id.serviceicon);

    }

}