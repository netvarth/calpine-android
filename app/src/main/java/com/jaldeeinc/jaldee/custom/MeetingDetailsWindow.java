package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.WebViewActivity;
import com.jaldeeinc.jaldee.response.TeleServiceCheckIn;
import com.jaldeeinc.jaldee.utils.SharedPreference;

public class MeetingDetailsWindow extends Dialog {

    TextView tvServiceName, tvTime, tvLink;
    CustomTextViewMedium hint;
    Button btJoin;
    String time = null;
    String serviceName, callingMode, meetingUrl = null, click_action = null;
    TeleServiceCheckIn teleServiceCheckInResponse;
    Context context;
    String virtualServiceType, countryCode;
    ImageView ivClose;

    public MeetingDetailsWindow(@NonNull Context context, String checkInTime, String name, TeleServiceCheckIn teleServiceCheckInResponse, String callingMode, String countryCode) {
        super(context);
        this.context = context;
        this.time = checkInTime;
        this.serviceName = name;
        this.teleServiceCheckInResponse = teleServiceCheckInResponse;
        this.callingMode = callingMode;
        this.countryCode = countryCode;
    }

    public MeetingDetailsWindow(@NonNull Context context, String meetingUrl, String click_action) {
        super(context);
        this.context = context;
        this.meetingUrl = meetingUrl;
        this.callingMode = "VideoCall";
        this.click_action = click_action;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_info);

        tvLink = findViewById(R.id.tv_meetingLink);
        tvServiceName = findViewById(R.id.tv_serviceName);
        tvTime = findViewById(R.id.tv_timeInfo);
        btJoin = findViewById(R.id.bt_join);
        ivClose = findViewById(R.id.iv_close);
        hint = findViewById(R.id.hint);

        Typeface tyface = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat_Bold.otf");
        btJoin.setTypeface(tyface);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        if (click_action != null && click_action.equalsIgnoreCase("INSTANT_VIDEO")) {
            hint.setVisibility(View.GONE);
            tvServiceName.setVisibility(View.GONE);
            tvLink.setText(meetingUrl);
        } else if (click_action == null) {
            if (serviceName != null) {
                String name = serviceName;
//            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                tvServiceName.setText(name);

                try {
                    if (callingMode != null) {

                        if (callingMode.equalsIgnoreCase("Zoom")) {
                            tvServiceName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized, 0, 0, 0);
                            tvServiceName.setCompoundDrawablePadding(15);
                        } else if (callingMode.equalsIgnoreCase("GoogleMeet")) {
                            tvServiceName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized, 0, 0, 0);
                            tvServiceName.setCompoundDrawablePadding(15);
                        } else if (callingMode.equalsIgnoreCase("WhatsApp")) {
                            if (virtualServiceType != null && virtualServiceType.equalsIgnoreCase("videoService")) {
                                tvServiceName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsapp_videoicon, 0, 0, 0);
                            } else {
                                tvServiceName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized, 0, 0, 0);
                            }
                            tvServiceName.setCompoundDrawablePadding(15);
                        } else if (callingMode.equalsIgnoreCase("phone")) {
                            tvServiceName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneicon_sized, 0, 0, 0);
                            tvServiceName.setCompoundDrawablePadding(15);
                        } else if (callingMode.equalsIgnoreCase("VideoCall")) {
                            tvServiceName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_jaldeevideo, 0, 0, 0);
                            tvServiceName.setCompoundDrawablePadding(15);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            if (time != null) {
                tvTime.setText(time);
            }

            if (teleServiceCheckInResponse != null) {

                if (teleServiceCheckInResponse.getJoiningUrl() != null) {

                    tvLink.setText(teleServiceCheckInResponse.getJoiningUrl());
                } else if (teleServiceCheckInResponse.getStartingUl() != null) {

                    tvLink.setText(teleServiceCheckInResponse.getStartingUl());
                } else {
                    btJoin.setVisibility(View.GONE);
                }
            }
        }
        tvLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tvLink != null && callingMode != null && callingMode.equalsIgnoreCase("VideoCall")) {
                    Intent intent = new Intent(context, WebViewActivity.class);
                    String pass = SharedPreference.getInstance(context).getStringValue("password", "");
                    intent.putExtra("URL", tvLink.getText().toString() + "?pwd=" + pass);
                    context.startActivity(intent);
                } else {

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    if (tvLink != null) {
                        intent.setData(Uri.parse(tvLink.getText().toString()));
                    }
                    context.startActivity(intent);
                }
            }
        });
        btJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvLink != null && callingMode != null && callingMode.equalsIgnoreCase("VideoCall")) {
                    Intent intent = new Intent(context, WebViewActivity.class);
                    String pass = SharedPreference.getInstance(context).getStringValue("password", "");
                    intent.putExtra("URL", tvLink.getText().toString() + "?pwd=" + pass);
                    context.startActivity(intent);
                } else {

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    if (tvLink != null) {
                        intent.setData(Uri.parse(tvLink.getText().toString()));
                    }
                    context.startActivity(intent);
                }
            }
        });

    }
}
