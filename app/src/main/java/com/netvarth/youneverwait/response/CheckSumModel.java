package com.netvarth.youneverwait.response;

/**
 * Created by sharmila on 3/10/18.
 */

public class CheckSumModel {
    String checksum;
    String successUrl;
    String failureUrl;
    String merchantId;
    String email;
    String merchantKey;
   /* String txnid;

    public String getTxnid() {
        return txnid;
    }

    public String getProductinfo() {
        return productinfo;
    }

    String productinfo;*/

    public String getChecksum() {
        return checksum;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public String getFailureUrl() {
        return failureUrl;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getEmail() {
        return email;
    }

    public String getMerchantKey() {
        return merchantKey;
    }
}
