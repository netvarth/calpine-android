package com.jaldeeinc.jaldee.enums;
public enum CouponSystemNotes {

    MINIMUM_BILL_AMT_REQUIRED("A minimum bill amount is required to redeem this coupon"),
    ONLY_WHEN_FITST_CHECKIN_ON_PROVIDER("Coupon can be applied only for first check in with this provider"),
    COUPON_APPLIED("Coupon Sussessfully applied"),
    SELF_PAY_REQUIRED("Self pay required"),
    NO_OTHER_COUPONS_ALLOWED("No other coupons allowed"),
    EXCEEDS_APPLY_LIMIT("Exceeds apply limit"),
    ONLY_WHEN_FITST_CHECKIN("Only for first check-in"),
    ONLINE_CHECKIN_REQUIRED("Online check-in required"),
    CANT_COMBINE_WITH_OTHER_COUPONES("Can't combine with other coupons"),
    CONSUMER_CAN_NOT_APPLY_COUPON("Coupon cannot be applied"),
    PROVIDER_COUPON_NOT_APPLICABLE_SERVICE("Provider coupon not applicable for this service"),
    PROVIDER_COUPON_NOT_APPLICABLE_USER("Provider coupon not applicable for this user"),
    PROVIDER_COUPON_NOT_APPLICABLE_GROUP("Provider coupon not applicable for this group"),
    PROVIDER_COUPON_NOT_APPLICABLE_ITEM("Provider coupon not applicable for this item"),
    PROVIDER_COUPON_NOT_APPLICABLE_CATALOG("Provider coupon not applicable for this catalog"),
    PROVIDER_COUPON_NOT_APPLICABLE_LABEL("Provider coupon not applicable for this label"),
    PROVIDER_COUPON_NOT_APPLICABLE_BOOKING_MODE("Provider coupon not applicable for this booking mode"),
    PROVIDER_COUPON_NOT_APPLICABLE("Provider coupon not applicable on this day"),
    PROVIDER_COUPON_NOT_APPLICABLE_NOW("Provider coupon not applicable now"),
    JC_NOT_APPLICABLE_DAY("Jaldee Coupon not applicable on this day"),

    COUPON_CAN_NOT_APPLY("Coupon cannot be applied."),
    PROVIDER_COUPON_NOT_APPLICABLE_ORDER("Provider coupon not applicable for order"),
    PROVIDER_COUPON_NOT_APPLICABLE_WAITLIST("Provider coupon not applicable for waitlist"),
    PROVIDER_COUPON_NOT_APPLICABLE_APPOINTMENT("Provider coupon not applicable for appointment"),
    EXCEEDS_PRO_COUP_APPLY_LIMIT("Coupon reached limit of usage");
    //COUPON_EXPIRED_OR_PASSIVE(JALDEE_COUPON_EXPIRED_OR_PASSIVE),
    //PROVIDER_COUPON_NOT_APPLICABLE_COUPON_BASED_ON(COUPON_NOT_VALID);

    private String systemMessage;
    private CouponSystemNotes (final String restriction) {
        this.systemMessage = restriction;
    }


    public String getSystemMsg() {
        return systemMessage;
    }

}
