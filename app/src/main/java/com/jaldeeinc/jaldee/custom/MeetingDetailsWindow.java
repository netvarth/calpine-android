package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.response.TeleServiceCheckIn;

public class MeetingDetailsWindow extends Dialog {

    TextView tvServiceName,tvTime,tvLink;
    Button btJoin;
    String time = null;
    String serviceName;
    TeleServiceCheckIn teleServiceCheckInResponse;
    Context context;

    public MeetingDetailsWindow(@NonNull Context context, String checkInTime, String name, TeleServiceCheckIn teleServiceCheckInResponse, String callingMode) {
        super(context);
        this.context =context;
        this.time = checkInTime;
        this.serviceName = name;
        this.teleServiceCheckInResponse = teleServiceCheckInResponse;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_info);

        tvLink = findViewById(R.id.tv_meetingLink);
        tvServiceName = findViewById(R.id.tv_serviceName);
        tvTime = findViewById(R.id.tv_timeInfo);
        btJoin = findViewById(R.id.bt_join);

        Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                "fonts/Montserrat_Bold.otf");
        btJoin.setTypeface(tyface);

        if (serviceName != null){
            String name = serviceName;
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
            tvServiceName.setText(name);
        }

        if (time != null){
            tvTime.setText(time);
        }

        if (teleServiceCheckInResponse != null){

            if (teleServiceCheckInResponse.getJoiningUrl() != null){

                tvLink.setText(teleServiceCheckInResponse.getJoiningUrl());
            }
            else if (teleServiceCheckInResponse.getStartingUl() != null){

                tvLink.setText(teleServiceCheckInResponse.getStartingUl());
            }
            else{
                btJoin.setVisibility(View.GONE);
            }

            tvLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(tvLink.getText().toString()));
                    context.startActivity(intent);
                }
            });
        }

        btJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(tvLink.getText().toString()));
                context.startActivity(intent);
            }
        });

    }
}
