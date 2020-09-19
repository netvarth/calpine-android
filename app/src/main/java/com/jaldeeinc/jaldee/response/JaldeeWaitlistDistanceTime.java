package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class JaldeeWaitlistDistanceTime implements Serializable {

    public String getPollingTime() {
        return pollingTime;
    }

    public void setPollingTime(String pollingTime) {
        this.pollingTime = pollingTime;
    }

    String pollingTime;

    public JaldeeDistanceTime getJaldeeDistanceTime() {
        return jaldeeDistanceTime;
    }

    public void setJaldeeDistanceTime(JaldeeDistanceTime jaldeeDistanceTime) {
        this.jaldeeDistanceTime = jaldeeDistanceTime;
    }

    @SerializedName("jaldeeDistanceTime")
    private JaldeeDistanceTime jaldeeDistanceTime;


}
