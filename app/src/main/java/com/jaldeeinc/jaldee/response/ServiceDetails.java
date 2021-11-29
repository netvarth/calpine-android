package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ServiceDetails implements Serializable {

    int id;
    boolean priceDynamic;
    boolean preInfoEnabled;
    boolean postInfoEnabled;
    String serviceType;
    String livetrack;
    String callingMode;
    String postInfoTitle;
    String postInfoText;
    String preInfoTitle;
    String preInfoText;
    String virtualServiceType;
    String consumerNoteTitle;
    String name;
    String deptName;

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLivetrack() {
        return livetrack;
    }

    public void setLivetrack(String livetrack) {
        this.livetrack = livetrack;
    }

    public ArrayList<ServiceDetails> getVirtualCallingModes() {
        return virtualCallingModes;
    }

    public void setVirtualCallingModes(ArrayList<ServiceDetails> virtualCallingModes) { this.virtualCallingModes = virtualCallingModes; }

    @SerializedName("virtualCallingModes")
    private ArrayList<ServiceDetails> virtualCallingModes;

    public String getCallingMode() {
        return callingMode;
    }

    public void setCallingMode(String callingMode) {
        this.callingMode = callingMode;
    }

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

    public boolean isPreInfoEnabled() {
        return preInfoEnabled;
    }

    public void setPreInfoEnabled(boolean preInfoEnabled) {
        this.preInfoEnabled = preInfoEnabled;
    }

    public boolean isPostInfoEnabled() {
        return postInfoEnabled;
    }

    public void setPostInfoEnabled(boolean postInfoEnabled) { this.postInfoEnabled = postInfoEnabled; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVirtualServiceType() {
        return virtualServiceType;
    }

    public void setVirtualServiceType(String virtualServiceType) { this.virtualServiceType = virtualServiceType; }

    public String getConsumerNoteTitle() {
        return consumerNoteTitle;
    }

    public void setConsumerNoteTitle(String consumerNoteTitle) { this.consumerNoteTitle = consumerNoteTitle; }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public boolean isPriceDynamic() { return priceDynamic; }

}
