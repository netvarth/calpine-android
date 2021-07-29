package com.jaldeeinc.jaldee.response;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class AdvancePaymentDetailsOrder {
    private float netTotal;
    private float itemTotal;
    private float advanceAmount;
    private float jdnDiscount;
    private float jaldeeCouponDiscount;
    private float providerCouponDiscount;
    private float totalDiscount;
    private float taxAmount;
    private float deliveryCharge;
    private JsonObject proCouponList;
    private JsonObject jCouponList;
    private JsonObject eligibleJcashAmt;
    private JsonArray orderItems;

    public float getNetTotal() {
        return netTotal;
    }

    public float getItemTotal() {
        return itemTotal;
    }

    public float getAdvanceAmount() {
        return advanceAmount;
    }

    public float getJdnDiscount() {
        return jdnDiscount;
    }

    public float getJaldeeCouponDiscount() {
        return jaldeeCouponDiscount;
    }

    public float getProviderCouponDiscount() {
        return providerCouponDiscount;
    }

    public float getTotalDiscount() {
        return totalDiscount;
    }

    public float getTaxAmount() {
        return taxAmount;
    }

    public float getDeliveryCharge() {
        return deliveryCharge;
    }

    public JsonObject getProCouponList() {
        return proCouponList;
    }

    public JsonObject getjCouponList() {
        return jCouponList;
    }

    public JsonObject getEligibleJcashAmt() {
        return eligibleJcashAmt;
    }

    public JsonArray getOrderItems() {
        return orderItems;
    }
}
