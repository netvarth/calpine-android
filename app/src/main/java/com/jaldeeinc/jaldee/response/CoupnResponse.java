package com.jaldeeinc.jaldee.response;

/**
 * Created by sharmila on 14/12/18.
 */

public class CoupnResponse {

    public String getJaldeeCouponCode() {
        return jaldeeCouponCode;
    }

    String jaldeeCouponCode;


    public String getCouponDescription() {
        return couponDescription;
    }

    String couponDescription;

    public String getConsumerTermsAndconditions() {
        return consumerTermsAndconditions;
    }

    String consumerTermsAndconditions;

    public String getDiscountValue() {
        return discountValue;
    }

    String discountValue;


    public String getCouponName() {
        return couponName;
    }

    String couponName;


    public long getStartdate() {
        return startDate;
    }

    long startDate;


    public long getEnddate() {
        return endDate;
    }

    long endDate;

    public boolean isFirstCheckinOnly() {
        return firstCheckinOnly;
    }

    public void setFirstCheckinOnly(boolean firstCheckinOnly) {
        this.firstCheckinOnly = firstCheckinOnly;
    }

    boolean firstCheckinOnly;

}
