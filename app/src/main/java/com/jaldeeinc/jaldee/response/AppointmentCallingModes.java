package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppointmentCallingModes {

    @SerializedName("callingMode")
    @Expose
    private String callingMode;

    public String getCallingMode() {
        return callingMode;
    }

    public void setCallingMode(String callingMode) {
        this.callingMode = callingMode;
    }
}
