package com.jaldeeinc.jaldee.response;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class AdvancePaymentDetails {
    private float netTotal;
    private float amountRequiredNow;
    private float jdnDiscount;
    private float couponDiscount;
    private float providerCouponDiscount;
    private float totalDiscount;
    private float netTaxAmount;
    //private String systemNote;
    private JsonObject jCouponList;
    private JsonObject proCouponList;
    private JsonObject eligibleJcashAmt;

    public JsonObject getEligibleJcashAmt() {
        return eligibleJcashAmt;
    }

    public JsonObject getjCouponList() {
        return jCouponList;
    }
    public void setjCouponList(JsonObject jCouponList) {
        this.jCouponList = jCouponList;
    }
    public JsonObject getProCouponList() {
        return proCouponList;
    }
    public void setProCouponList(JsonObject proCouponList) {
        this.proCouponList = proCouponList;
    }
    public float getNetTotal() {
        return netTotal;
    }
    public void setNetTotal(float netTotal) {
        this.netTotal = netTotal;
    }
    public float getAmountRequiredNow() {
        return amountRequiredNow;
    }
    public void setAmountRequiredNow(float amountRequiredNow) {
        this.amountRequiredNow = amountRequiredNow;
    }
    public float getJdnDiscount() {
        return jdnDiscount;
    }
    public void setJdnDiscount(float jdnDiscount) {
        this.jdnDiscount = jdnDiscount;
    }
    public float getCouponDiscount() {
        return couponDiscount;
    }
    public void setCouponDiscount(float couponDiscount) {
        this.couponDiscount = couponDiscount;
    }
    public float getTotalDiscount() {
        return totalDiscount;
    }
    public void setTotalDiscount(float totalDiscount) {
        this.totalDiscount = totalDiscount;
    }
    public float getNetTaxAmount() {
        return netTaxAmount;
    }
    public void setNetTaxAmount(float netTaxAmount) {
        this.netTaxAmount = netTaxAmount;
    }
    public float getProviderCouponDiscount() {
        return providerCouponDiscount;
    }
    public void setProviderCouponDiscount(float providerCouponDiscount) {
        this.providerCouponDiscount = providerCouponDiscount;
    }
}
