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



}
