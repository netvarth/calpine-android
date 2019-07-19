package com.jaldeeinc.jaldee.model;

/**
 * Created by sharmila on 3/12/18.
 */

public class PaytmModel {


    String amount;
    String checksum;
    String email;
    String failureUrl;
    String merchantId;
    String merchantKey;
    String successUrl;
    String txnid;

    public String getAmount() {
        return amount;
    }

    public String getChecksum() {
        return checksum;
    }

    public String getEmail() {
        return email;
    }

    public String getFailureUrl() {
        return failureUrl;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getMerchantKey() {
        return merchantKey;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public String getTxnid() {
        return txnid;
    }


}
