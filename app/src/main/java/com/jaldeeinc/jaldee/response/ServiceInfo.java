package com.jaldeeinc.jaldee.response;

import java.io.Serializable;

public class ServiceInfo implements Serializable {

    String serviceName;
    String description;
    String virtualServiceType;
    String callingMode;
    String minPrePaymentAmount;
    String isPrePayment;
    String serviceType;
    String livetrack;
    String preInfoTitle;
    String preInfoText;
    String postInfoTitle;
    String postInfoText;
    String consumerNoteTitle;
    String availableDate;
    String time;
    String waitingTime;
    String type;
    String virtualCallingValue;
    String calculationMode;
    String totalAmount;
    String serviceBookingType;

    int maxBookingsAllowed;
    int peopleWaitingInLine;
    int scheduleId;
    int multiples;
    int serviceId;

    boolean showOnlyAvailableSlots;
    boolean isDate;
    boolean isDateTime;
    boolean isNoDateTime;
    boolean isUser;
    boolean noteManidtory;
    boolean isToken;
    boolean postInfoEnabled;
    boolean preInfoEnabled;


    public ServiceInfo(){}

    public String getServiceBookingType() { return serviceBookingType; }

    public void setServiceBookingType(String serviceBookingType) { this.serviceBookingType = serviceBookingType; }

    public boolean isDate() { return isDate; }

    public void setIsDate(boolean isDate) { this.isDate = isDate; }

    public boolean isDateTime() { return isDateTime; }

    public void setIsDateTime(boolean isDateTime) { this.isDateTime = isDateTime; }

    public boolean isNoDateTime() { return isNoDateTime; }

    public void setIsNoDateTime(boolean isNoDateTime) { this.isNoDateTime = isNoDateTime; }

    public boolean isShowOnlyAvailableSlots() {
        return showOnlyAvailableSlots;
    }

    public void setShowOnlyAvailableSlots(boolean showOnlyAvailableSlots) { this.showOnlyAvailableSlots = showOnlyAvailableSlots; }

    public int getMaxBookingsAllowed() {
        return maxBookingsAllowed;
    }

    public void setMaxBookingsAllowed(int maxBookingsAllowed) { this.maxBookingsAllowed = maxBookingsAllowed; }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVirtualServiceType() {
        return virtualServiceType;
    }

    public void setVirtualServiceType(String virtualServiceType) { this.virtualServiceType = virtualServiceType; }

    public String getMinPrePaymentAmount() {
        return minPrePaymentAmount;
    }

    public void setMinPrePaymentAmount(String minPrePaymentAmount) { this.minPrePaymentAmount = minPrePaymentAmount; }

    public String getIsPrePayment() {
        return isPrePayment;
    }

    public void setIsPrePayment(String isPrePayment) {
        this.isPrePayment = isPrePayment;
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

    public void setPostInfoEnabled(boolean postInfoEnabled) { this.postInfoEnabled = postInfoEnabled; }

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

    public void setConsumerNoteTitle(String consumerNoteTitle) { this.consumerNoteTitle = consumerNoteTitle; }

    public String getCallingMode() {
        return callingMode;
    }

    public void setCallingMode(String callingMode) {
        this.callingMode = callingMode;
    }

    public String getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(String availableDate) {
        this.availableDate = availableDate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getPeopleWaitingInLine() {
        return peopleWaitingInLine;
    }

    public void setPeopleWaitingInLine(int peopleWaitingInLine) { this.peopleWaitingInLine = peopleWaitingInLine; }

    public String getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(String waitingTime) {
        this.waitingTime = waitingTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isToken() {
        return isToken;
    }

    public void setToken(boolean token) {
        isToken = token;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getVirtualCallingValue() {
        return virtualCallingValue;
    }

    public void setVirtualCallingValue(String virtualCallingValue) { this.virtualCallingValue = virtualCallingValue; }

    public String getCalculationMode() {
        return calculationMode;
    }

    public void setCalculationMode(String calculationMode) { this.calculationMode = calculationMode; }

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    public boolean isNoteManidtory() {
        return noteManidtory;
    }

    public void setNoteManidtory(boolean noteManidtory) {
        this.noteManidtory = noteManidtory;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
}
