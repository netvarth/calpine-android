package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.response.TeleServiceCheckIn;

public class MeetingInfo extends Dialog {

    TextView tvServiceName,tvTime,tvOK,tvPhoneNumber;
    String time = null;
    String serviceName,callingMode;
    TeleServiceCheckIn teleServiceCheckInResponse;
    Context context;
    String phoneNumber;
    String virtualServiceType;
    String countryCode,bookingType;
    ImageView ivClose;


    public MeetingInfo(@NonNull Context context, String checkInTime, String name, TeleServiceCheckIn teleServiceCheckInResponse, String callingMode, String waitlistPhoneNumber, String virtualServiceType, String countryCode, String bookingType) {
        super(context);
        this.context = context;
        this.time = checkInTime;
        this.serviceName = name;
        this.teleServiceCheckInResponse = teleServiceCheckInResponse;
        this.phoneNumber = waitlistPhoneNumber;
        this.callingMode = callingMode;
        this.virtualServiceType = virtualServiceType;
        this.countryCode = countryCode;
        this.bookingType = bookingType;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_box);

        tvServiceName = findViewById(R.id.tv_serviceName);
        tvTime = findViewById(R.id.tv_timeInfo);
        tvOK = findViewById(R.id.tv_ok);
        tvPhoneNumber = findViewById(R.id.tv_phoneNumber);
        ivClose = findViewById(R.id.iv_close);



        if (serviceName != null){
            String name = serviceName;
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
            tvServiceName.setText(name);

            try {
                if(callingMode!=null) {
                    if (callingMode.equalsIgnoreCase("Zoom")) {
                        tvServiceName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoom, 0, 0, 0);
                        tvServiceName.setCompoundDrawablePadding(15);
                    } else if (callingMode.equalsIgnoreCase("GoogleMeet")) {
                        tvServiceName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet, 0, 0, 0);
                        tvServiceName.setCompoundDrawablePadding(15);
                    } else if (callingMode.equalsIgnoreCase("WhatsApp")) {
                        if(virtualServiceType!=null && virtualServiceType.equalsIgnoreCase("videoService")){
                            tvServiceName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsapp_videoicon, 0, 0, 0);
                        }
                        else {
                            tvServiceName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsapp, 0, 0, 0);
                        }
                        tvServiceName.setCompoundDrawablePadding(15);
                    } else if (callingMode.equalsIgnoreCase("phone")) {
                        tvServiceName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneicon_sized, 0, 0, 0);
                        tvServiceName.setCompoundDrawablePadding(15);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (time != null){
            tvTime.setText(time);
        }

        if(phoneNumber!=null){
            tvPhoneNumber.setText("+" + phoneNumber);
        }

//        if(phoneNumber!=null&& countryCode!=null && bookingType.equalsIgnoreCase(Constants.CHECKIN)){
//            tvPhoneNumber.setText(countryCode + phoneNumber);
//        }
//        else if(phoneNumber!=null && bookingType.equalsIgnoreCase(Constants.APPOINTMENT)){
//            tvPhoneNumber.setText("+" + phoneNumber);
//        }

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        tvOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

    }
}
