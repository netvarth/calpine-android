package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jaldeeinc.jaldee.R;

public class NotificationDialog extends Dialog {

    Context context;
    TextView tvTitle,tvOk;
    String notificationMessage;


    public NotificationDialog(@NonNull Context context, String message) {
        super(context);
        this.context = context;
        this.notificationMessage = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_message);
        tvTitle = findViewById(R.id.tv_message);
        tvOk = findViewById(R.id.tv_ok);
        tvTitle.setText(notificationMessage);
        Linkify.addLinks(tvTitle, Linkify.WEB_URLS);

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

    }
}