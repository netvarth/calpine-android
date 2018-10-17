package com.nv.youneverwait.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sharmila on 13/8/18.
 */

public class CheckInModel {

    @SerializedName("provider")
    private CheckInModel provider;

    public CheckInModel getProvider() {
        return provider;
    }

    public String getBusinessName() {
        return businessName;
    }

    String businessName;


    public CheckInModel getQueue() {
        return queue;
    }

    public CheckInModel getLocation() {
        return location;
    }

    public String getPlace() {
        return place;
    }

    @SerializedName("queue")
    private CheckInModel queue;

    @SerializedName("location")
    private CheckInModel location;

    String place;

}
