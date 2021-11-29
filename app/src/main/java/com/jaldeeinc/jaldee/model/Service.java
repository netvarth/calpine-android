package com.jaldeeinc.jaldee.model;

public class Service {

    String id;
    String serviceType;
    String multiples;
    String maxBookingsAllowed;
    String resoucesRequired;
    boolean livetrack;
    boolean consumerNoteMandatory;
    boolean preInfoEnabled;
    boolean postInfoEnabled;
    boolean serviceDurationEnabled;
    boolean prePaymentFixed;

    public String getId() {
        return id;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getMultiples() {
        return multiples;
    }

    public boolean isLivetrack() {
        return livetrack;
    }

    public boolean isConsumerNoteMandatory() {
        return consumerNoteMandatory;
    }

    public boolean isPreInfoEnabled() {
        return preInfoEnabled;
    }

    public boolean isPostInfoEnabled() {
        return postInfoEnabled;
    }

    public String getMaxBookingsAllowed() {
        return maxBookingsAllowed;
    }

    public String getResoucesRequired() {
        return resoucesRequired;
    }

    public boolean isServiceDurationEnabled() {
        return serviceDurationEnabled;
    }

    public boolean isPrePaymentFixed() {
        return prePaymentFixed;
    }
}
