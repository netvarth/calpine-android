package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class HomeDelivery implements Serializable {

    @SerializedName("homeDelivery")
    @Expose
    private boolean homeDelivery;

    @SerializedName("deliverySchedule")
    @Expose
    private CatalogSchedule deliverySchedule;

    @SerializedName("deliveryOtpVerification")
    @Expose
    private boolean deliveryOtpVerification;

    @SerializedName("deliveryRadius")
    @Expose
    private String deliveryRadius;

    @SerializedName("scheduledHomeDeliveryAllowed")
    @Expose
    private boolean scheduledHomeDeliveryAllowed;

    @SerializedName("asapHomeDeliveryAllowed")
    @Expose
    private boolean asapHomeDeliveryAllowed;

    @SerializedName("deliveryCharge")
    @Expose
    private String deliveryCharge;

    public HomeDelivery(){

    }

    public HomeDelivery(boolean homeDelivery, CatalogSchedule deliverySchedule, boolean deliveryOtpVerification, String deliveryRadius, boolean scheduledHomeDeliveryAllowed, boolean asapHomeDeliveryAllowed, String deliveryCharge) {
        this.homeDelivery = homeDelivery;
        this.deliverySchedule = deliverySchedule;
        this.deliveryOtpVerification = deliveryOtpVerification;
        this.deliveryRadius = deliveryRadius;
        this.scheduledHomeDeliveryAllowed = scheduledHomeDeliveryAllowed;
        this.asapHomeDeliveryAllowed = asapHomeDeliveryAllowed;
        this.deliveryCharge = deliveryCharge;
    }


    public boolean isHomeDelivery() {
        return homeDelivery;
    }

    public void setHomeDelivery(boolean homeDelivery) {
        this.homeDelivery = homeDelivery;
    }

    public CatalogSchedule getDeliverySchedule() {
        return deliverySchedule;
    }

    public void setDeliverySchedule(CatalogSchedule deliverySchedule) {
        this.deliverySchedule = deliverySchedule;
    }

    public boolean isDeliveryOtpVerification() {
        return deliveryOtpVerification;
    }

    public void setDeliveryOtpVerification(boolean deliveryOtpVerification) {
        this.deliveryOtpVerification = deliveryOtpVerification;
    }

    public String getDeliveryRadius() {
        return deliveryRadius;
    }

    public void setDeliveryRadius(String deliveryRadius) {
        this.deliveryRadius = deliveryRadius;
    }

    public boolean isScheduledHomeDeliveryAllowed() {
        return scheduledHomeDeliveryAllowed;
    }

    public void setScheduledHomeDeliveryAllowed(boolean scheduledHomeDeliveryAllowed) {
        this.scheduledHomeDeliveryAllowed = scheduledHomeDeliveryAllowed;
    }

    public boolean isAsapHomeDeliveryAllowed() {
        return asapHomeDeliveryAllowed;
    }

    public void setAsapHomeDeliveryAllowed(boolean asapHomeDeliveryAllowed) {
        this.asapHomeDeliveryAllowed = asapHomeDeliveryAllowed;
    }

    public String getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(String deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }
}
