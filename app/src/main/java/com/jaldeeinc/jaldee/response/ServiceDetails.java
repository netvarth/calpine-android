package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ServiceDetails {
    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    String serviceType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;

    public String getLivetrack() {
        return livetrack;
    }

    public void setLivetrack(String livetrack) {
        this.livetrack = livetrack;
    }

    String livetrack;


    public ArrayList<ServiceDetails> getVirtualCallingModes() {
        return virtualCallingModes;
    }

    public void setVirtualCallingModes(ArrayList<ServiceDetails> virtualCallingModes) {
        this.virtualCallingModes = virtualCallingModes;
    }

    @SerializedName("virtualCallingModes")
    private ArrayList<ServiceDetails> virtualCallingModes;

    public String getCallingMode() {
        return callingMode;
    }

    public void setCallingMode(String callingMode) {
        this.callingMode = callingMode;
    }

    String callingMode;



}
