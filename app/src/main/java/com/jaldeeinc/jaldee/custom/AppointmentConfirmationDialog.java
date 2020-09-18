package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;

public class AppointmentConfirmationDialog extends Dialog {

    private ActiveCheckIn activeAppInfo;
    Context context;

    public AppointmentConfirmationDialog(@NonNull Context context, ActiveCheckIn activeAppointment) {
        super(context);
        this.context = context;
        this.activeAppInfo= activeAppointment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation_appointment);


    }
}
