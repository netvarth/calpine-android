package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.response.TeleServiceCheckIn;

public class MeetingInfo extends Dialog {

    TextView tvServiceName,tvTime,tvOK,tvPhoneNumber;
    String time = null;
    String serviceName;
    TeleServiceCheckIn teleServiceCheckInResponse;
    Context context;
    String phoneNumber;


    public MeetingInfo(@NonNull Context context, String checkInTime, String name, TeleServiceCheckIn teleServiceCheckInResponse, String callingMode, String waitlistPhoneNumber) {
        super(context);
        this.context = context;
        this.context =context;
        this.time = checkInTime;
        this.serviceName = name;
        this.teleServiceCheckInResponse = teleServiceCheckInResponse;
        this.phoneNumber = waitlistPhoneNumber;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_box);

        tvServiceName = findViewById(R.id.tv_serviceName);
        tvTime = findViewById(R.id.tv_timeInfo);
        tvOK = findViewById(R.id.tv_ok);
        tvPhoneNumber = findViewById(R.id.tv_phoneNumber);

        if (serviceName != null){
            String name = serviceName;
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
            tvServiceName.setText(name);
        }

        if (time != null){
            tvTime.setText(time);
        }

        if (phoneNumber != null){

            tvPhoneNumber.setText(phoneNumber);
        }


        tvOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

    }
}
