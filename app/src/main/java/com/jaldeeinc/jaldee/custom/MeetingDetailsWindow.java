package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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


        if (serviceName != null){
            tvServiceName.setText(serviceName);
        }

        if (time != null){
            tvTime.setText(time);
        }

        if (teleServiceCheckInResponse != null){
            tvLink.setText(teleServiceCheckInResponse.getJoiningUrl());
        }

        btJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(teleServiceCheckInResponse.getJoiningUrl()));
                context.startActivity(intent);
            }
        });

    }
}
