package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.SerializedName;
import com.jaldeeinc.jaldee.model.BillModel;

public class ProviderCouponResponse {
    long id;
    String name;
    float amount;
    String description;
    String calculationType;
    String status;
    String couponCode;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getCalculationType() {
        return calculationType;
    }

    public String getStatus() {
        return status;
    }

    public String getCouponCode() {
        return couponCode;
    }
}
