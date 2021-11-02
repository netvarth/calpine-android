package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sharmila on 30/7/18.
 */

public class SearchService implements Serializable {

    ArrayList<SearchService> servicegallery;
    ArrayList<SearchService> mAllService;
    ArrayList<SearchService> virtualCallingModes;
    String serviceDuration;
    String notification;
    String minPrePaymentAmount;
    String totalAmount;
    String status;
    String name;
    String description;
    String url;
    String serviceType;
    String virtualServiceType;
    String instructions;
    String callingMode;
    String value;
    String preInfoTitle;
    String preInfoText;
    String postInfoTitle;
    String postInfoText;
    String consumerNoteTitle;
    String paymentDescription;
    boolean preInfoEnabled;
    boolean postInfoEnabled;
    boolean consumerNoteMandatory;
    boolean livetrack;
    boolean taxable;
    boolean isPrePayment;
    boolean serviceDurationEnabled;
    int id;
    int department;
    int locid;

    @SerializedName("provider")
    private SearchService provider;

    @SerializedName("serviceAvailability")
    private CheckInServiceAvailability checkInServiceAvailability;

    public boolean isLivetrack() {
        return livetrack;
    }

    public void setLivetrack(boolean livetrack) {
        this.livetrack = livetrack;
    }

    public int getDepartment() {
        return department;
    }

    public void setDepartment(int department) {
        this.department = department;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public void setServiceDuration(String serviceDuration) { this.serviceDuration = serviceDuration; }

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

    public void setMinPrePaymentAmount(String minPrePaymentAmount) { this.minPrePaymentAmount = minPrePaymentAmount; }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
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

    public void setServicegallery(ArrayList<SearchService> servicegallery) { this.servicegallery = servicegallery; }

    public ArrayList<SearchService> getmAllService() {
        return mAllService;
    }

    public void setmAllService(ArrayList<SearchService> mAllService) { this.mAllService = mAllService; }

    @Override
    public String toString() {
        return this.name; // Value to be displayed in the Spinner
    }

    public boolean isTaxable() {
        return taxable;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getVirtualServiceType() {
        return virtualServiceType;
    }

    public void setVirtualServiceType(String virtualServiceType) { this.virtualServiceType = virtualServiceType; }

    public ArrayList<SearchService> getVirtualCallingModes() {
        return virtualCallingModes;
    }

    public void setVirtualCallingModes(ArrayList<SearchService> virtualCallingModes) { this.virtualCallingModes = virtualCallingModes; }

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

    public String getPostInfoTitle() {
        return postInfoTitle;
    }

    public String getPostInfoText() {
        return postInfoText;
    }

    public String getConsumerNoteTitle() {
        return consumerNoteTitle;
    }

    public void setConsumerNoteTitle(String consumerNoteTitle) { this.consumerNoteTitle = consumerNoteTitle; }

    public CheckInServiceAvailability getCheckInServiceAvailability() { return checkInServiceAvailability; }

    public boolean isConsumerNoteMandatory() {
        return consumerNoteMandatory;
    }

    public void setTaxable(boolean taxable) {
        this.taxable = taxable;
    }

    public String getPaymentDescription() { return paymentDescription; }

    public boolean isServiceDurationEnabled() { return serviceDurationEnabled; }
}
