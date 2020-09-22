package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sharmila on 30/7/18.
 */

public class SearchService implements Serializable {


    String serviceDuration;
    String notificationType;
    String notification;
    boolean isPrePayment;
    String minPrePaymentAmount;
    String totalAmount;
    String bType;
    String status;
    ArrayList<SearchService> servicegallery;
    String name;
    int locid;
    String description;
    ArrayList<SearchService> mAllService;
    String url;
    String thumbUrl;
    int department;
    String serviceType;
    String virtualServiceType;
    ArrayList<SearchService> virtualCallingModes;
    String instructions;
    String callingMode;
    String value;
    boolean preInfoEnabled;
    String preInfoTitle;
    String preInfoText;
    boolean postInfoEnabled;
    String postInfoTitle;
    String postInfoText;
    String consumerNoteTitle;
    @SerializedName("provider")
    private SearchService provider;

    public boolean isLivetrack() {
        return livetrack;
    }

    public void setLivetrack(boolean livetrack) {
        this.livetrack = livetrack;
    }

    boolean livetrack;

    public int getDepartment() {
        return department;
    }

    public void setDepartment(int department) {
        this.department = department;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    int id;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLocid() {
        return locid;
    }

    public void setLocid(int locid) {
        this.locid = locid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceDuration() {
        return serviceDuration;
    }

    public void setServiceDuration(String serviceDuration) {
        this.serviceDuration = serviceDuration;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public boolean isPrePayment() {
        return isPrePayment;
    }

    public void setPrePayment(boolean prePayment) {
        isPrePayment = prePayment;
    }

    public String getMinPrePaymentAmount() {
        return minPrePaymentAmount;
    }

    public void setMinPrePaymentAmount(String minPrePaymentAmount) {
        this.minPrePaymentAmount = minPrePaymentAmount;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getbType() {
        return bType;
    }

    public void setbType(String bType) {
        this.bType = bType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<SearchService> getServicegallery() {
        return servicegallery;
    }

    public void setServicegallery(ArrayList<SearchService> servicegallery) {
        this.servicegallery = servicegallery;
    }

    public ArrayList<SearchService> getmAllService() {
        return mAllService;
    }

    public void setmAllService(ArrayList<SearchService> mAllService) {
        this.mAllService = mAllService;
    }
    @Override
    public String toString() {
        return this.name; // Value to be displayed in the Spinner
    }

    public boolean isTaxable() {
        return taxable;
    }

    boolean taxable;

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }


    public String getVirtualServiceType() {
        return virtualServiceType;
    }

    public void setVirtualServiceType(String virtualServiceType) {
        this.virtualServiceType = virtualServiceType;
    }
    public ArrayList<SearchService> getVirtualCallingModes() {
        return virtualCallingModes;
    }

    public void setVirtualCallingModes(ArrayList<SearchService> virtualCallingModes) {
        this.virtualCallingModes = virtualCallingModes;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    public String getCallingMode() {
        return callingMode;
    }

    public void setCallingMode(String callingMode) {
        this.callingMode = callingMode;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    public SearchService getProvider() {
        return provider;
    }

    public void setProvider(SearchService provider) {
        this.provider = provider;
    }

    public boolean isPreInfoEnabled() {
        return preInfoEnabled;
    }

    public void setPreInfoEnabled(boolean preInfoEnabled) {
        this.preInfoEnabled = preInfoEnabled;
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

    public boolean isPostInfoEnabled() {
        return postInfoEnabled;
    }

    public void setPostInfoEnabled(boolean postInfoEnabled) {
        this.postInfoEnabled = postInfoEnabled;
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

    public String getConsumerNoteTitle() {
        return consumerNoteTitle;
    }

    public void setConsumerNoteTitle(String consumerNoteTitle) {
        this.consumerNoteTitle = consumerNoteTitle;
    }
}
