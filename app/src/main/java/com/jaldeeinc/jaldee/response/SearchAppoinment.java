package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchAppoinment implements Serializable {

    ArrayList<SearchService> servicegallery;
    ArrayList<SearchAppoinment> virtualCallingModes;
    ArrayList<Integer> serviceOptionIds;
    String name;
    String description;
    String notification;
    String isPrePayment;
    String totalAmount;
    String status;
    String taxable;
    String serviceType;
    String livetrack;
    String virtualServiceType;
    String minPrePaymentAmount;
    String instructions;
    String callingMode;
    String value;
    String preInfoTitle;
    String preInfoText;
    String postInfoTitle;
    String postInfoText;
    String consumerNoteTitle;
    String paymentDescription;
    String serviceBookingType;
    boolean consumerNoteMandatory;
    boolean postInfoEnabled;
    boolean preInfoEnabled;
    boolean serviceDurationEnabled;
    boolean showOnlyAvailableSlots;
    boolean date;
    boolean dateTime;
    boolean noDateTime;
    int serviceDuration;
    int id;
    int multiples;
    int department;
    int maxBookingsAllowed;

    public String getServiceBookingType() {
        return serviceBookingType;
    }

    public void setServiceBookingType(String serviceBookingType) {
        this.serviceBookingType = serviceBookingType;
    }

    public boolean isDate() {
        return date;
    }

    public void setDate(boolean date) {
        this.date = date;
    }

    public boolean isDateTime() {
        return dateTime;
    }

    public void setDateTime(boolean dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isNoDateTime() {
        return noDateTime;
    }

    public void setNoDateTime(boolean noDateTime) {
        this.noDateTime = noDateTime;
    }

    @SerializedName("provider")
    private SearchAppoinment provider;

    @SerializedName("serviceAvailability")
    private AppointServiceAvailability appointServiceAvailability;

    public ArrayList<Integer> getServiceOptionIds() {
        return serviceOptionIds;
    }

    public void setServiceOptionIds(ArrayList<Integer> serviceOptionIds) {
        this.serviceOptionIds = serviceOptionIds;
    }

    public boolean isShowOnlyAvailableSlots() {
        return showOnlyAvailableSlots;
    }

    public int getMaxBookingsAllowed() {
        return maxBookingsAllowed;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getServiceDuration() {
        return serviceDuration;
    }

    public void setServiceDuration(int serviceDuration) {
        this.serviceDuration = serviceDuration;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getIsPrePayment() {
        return isPrePayment;
    }

    public void setIsPrePayment(String isPrePayment) {
        this.isPrePayment = isPrePayment;
    }

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

    public String getTaxable() {
        return taxable;
    }

    public void setTaxable(String taxable) {
        this.taxable = taxable;
    }

    public int getDepartment() {
        return department;
    }

    public void setDepartment(int department) {
        this.department = department;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public int getMultiples() {
        return multiples;
    }

    public void setMultiples(int multiples) {
        this.multiples = multiples;
    }

    public String getLivetrack() {
        return livetrack;
    }

    public void setLivetrack(String livetrack) {
        this.livetrack = livetrack;
    }

    public String getMinPrePaymentAmount() {
        return minPrePaymentAmount;
    }

    public void setMinPrePaymentAmount(String minPrePaymentAmount) {
        this.minPrePaymentAmount = minPrePaymentAmount;
    }

    public String getVirtualServiceType() {
        return virtualServiceType;
    }

    public void setVirtualServiceType(String virtualServiceType) {
        this.virtualServiceType = virtualServiceType;
    }

    public ArrayList<SearchAppoinment> getVirtualCallingModes() {
        return virtualCallingModes;
    }

    public void setVirtualCallingModes(ArrayList<SearchAppoinment> virtualCallingModes) {
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

    @Override
    public String toString() {
        return this.name; // Value to be displayed in the Spinner
    }

    public SearchAppoinment getProvider() {
        return provider;
    }

    public void setProvider(SearchAppoinment provider) {
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

    public void setConsumerNoteTitle(String consumerNoteTitle) {
        this.consumerNoteTitle = consumerNoteTitle;
    }

    public AppointServiceAvailability getAppointServiceAvailability() {
        return appointServiceAvailability;
    }

    public ArrayList<SearchService> getServicegallery() {
        return servicegallery;
    }

    public void setServicegallery(ArrayList<SearchService> servicegallery) {
        this.servicegallery = servicegallery;
    }

    public boolean isConsumerNoteMandatory() {
        return consumerNoteMandatory;
    }

    public String getPaymentDescription() {
        return paymentDescription;
    }

    public boolean isServiceDurationEnabled() {
        return serviceDurationEnabled;
    }

}
