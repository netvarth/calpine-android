package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ServiceDetails implements Serializable {
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
    int id;

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



    public String getPreInfoTitle() {
        return preInfoTitle;
    }

    public void setPreInfoTitle(String preInfoTitle) {
        this.preInfoTitle = preInfoTitle;
    }

    public String getPreInfoText() {
        return preInfoText;
    }

    public void setPreInfoText(String preInfoText) {
        this.preInfoText = preInfoText;
    }



    public String getPostInfoTitle() {
        return postInfoTitle;
    }

    public void setPostInfoTitle(String postInfoTitle) {
        this.postInfoTitle = postInfoTitle;
    }

    public String getPostInfoText() {
        return postInfoText;
    }

    public void setPostInfoText(String postInfoText) {
        this.postInfoText = postInfoText;
    }

    boolean preInfoEnabled;
    String preInfoTitle;
    String preInfoText;
    boolean postInfoEnabled;

    public boolean isPreInfoEnabled() {
        return preInfoEnabled;
    }

    public void setPreInfoEnabled(boolean preInfoEnabled) {
        this.preInfoEnabled = preInfoEnabled;
    }

    public boolean isPostInfoEnabled() {
        return postInfoEnabled;
    }

    public void setPostInfoEnabled(boolean postInfoEnabled) {
        this.postInfoEnabled = postInfoEnabled;
    }

    String postInfoTitle;
    String postInfoText;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
