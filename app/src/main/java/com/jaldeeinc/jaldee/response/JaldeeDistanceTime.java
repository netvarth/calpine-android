package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class JaldeeDistanceTime implements Serializable {


    public JaldeeDistance getJaldeeDistance() {
        return jaldeeDistance;
    }

    public void setJaldeeDistance(JaldeeDistance jaldeeDistance) {
        this.jaldeeDistance = jaldeeDistance;
    }

    @SerializedName("jaldeeDistance")
    private JaldeeDistance jaldeeDistance;


    public JaldeelTravelTime getJaldeelTravelTime() {
        return jaldeelTravelTime;
    }

    public void setJaldeelTravelTime(JaldeelTravelTime jaldeelTravelTime) {
        this.jaldeelTravelTime = jaldeelTravelTime;
    }

    @SerializedName("jaldeelTravelTime")
    private JaldeelTravelTime jaldeelTravelTime;




}
