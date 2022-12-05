package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PickUp implements Serializable {

    @SerializedName("orderPickUp")
    @Expose
    private boolean orderPickUp;

    @SerializedName("pickUpSchedule")
    @Expose
    private CatalogSchedule pickUpSchedule;

    @SerializedName("pickUpOtpVerification")
    @Expose
    private boolean pickUpOtpVerification;

    @SerializedName("pickUpScheduledAllowed")
    @Expose
    private boolean pickUpScheduledAllowed;

    @SerializedName("pickUpAsapAllowed")
    @Expose
    private boolean pickUpASAPAllowed;

    public PickUp(){

    }

    public PickUp(boolean orderPickUp, CatalogSchedule pickUpSchedule, boolean pickUpOtpVerification, boolean pickUpScheduledAllowed, boolean pickUpASAPAllowed) {
        this.orderPickUp = orderPickUp;
        this.pickUpSchedule = pickUpSchedule;
        this.pickUpOtpVerification = pickUpOtpVerification;
        this.pickUpScheduledAllowed = pickUpScheduledAllowed;
        this.pickUpASAPAllowed = pickUpASAPAllowed;
    }

    public boolean isOrderPickUp() {
        return orderPickUp;
    }

    public void setOrderPickUp(boolean orderPickUp) {
        this.orderPickUp = orderPickUp;
    }

    public CatalogSchedule getPickUpSchedule() {
        return pickUpSchedule;
    }

    public void setPickUpSchedule(CatalogSchedule pickUpSchedule) {
        this.pickUpSchedule = pickUpSchedule;
    }

    public boolean isPickUpOtpVerification() {
        return pickUpOtpVerification;
    }

    public void setPickUpOtpVerification(boolean pickUpOtpVerification) {
        this.pickUpOtpVerification = pickUpOtpVerification;
    }

    public boolean isPickUpScheduledAllowed() {
        return pickUpScheduledAllowed;
    }

    public void setPickUpScheduledAllowed(boolean pickUpScheduledAllowed) {
        this.pickUpScheduledAllowed = pickUpScheduledAllowed;
    }

    public boolean isPickUpASAPAllowed() {
        return pickUpASAPAllowed;
    }

    public void setPickUpASAPAllowed(boolean pickUpASAPAllowed) {
        this.pickUpASAPAllowed = pickUpASAPAllowed;
    }
}
