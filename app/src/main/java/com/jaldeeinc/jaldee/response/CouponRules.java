package com.jaldeeinc.jaldee.response;

import java.io.Serializable;

public class CouponRules implements Serializable {

    private String minBillAmount;
    private String maxDiscountValue;
    private int maxProviderUseLimit;
    private boolean firstCheckinOnly;
    private long startDate;
    private long endDate;
    private String termsConditions;



    public int getMaxProviderUseLimit() {
        return maxProviderUseLimit;
    }

    public void setMaxProviderUseLimit(int maxProviderUseLimit) {
        this.maxProviderUseLimit = maxProviderUseLimit;
    }

    public boolean isFirstCheckinOnly() {
        return firstCheckinOnly;
    }

    public void setFirstCheckinOnly(boolean firstCheckinOnly) {
        this.firstCheckinOnly = firstCheckinOnly;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public String getMinBillAmount() {
        return minBillAmount;
    }

    public void setMinBillAmount(String minBillAmount) {
        this.minBillAmount = minBillAmount;
    }

    public String getMaxDiscountValue() {
        return maxDiscountValue;
    }

    public void setMaxDiscountValue(String maxDiscountValue) {
        this.maxDiscountValue = maxDiscountValue;
    }

    public String getTermsConditions() {
        return termsConditions;
    }

    public void setTermsConditions(String termsConditions) {
        this.termsConditions = termsConditions;
    }
}
