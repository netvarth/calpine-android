package com.nv.youneverwait.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sharmila on 3/10/18.
 */

public class CheckSumModel implements Serializable {
    String checksum;
    String successUrl;
    String failureUrl;
    String merchantId;
    String email;
    String merchantKey;
    String amount;
    String txnid;


    public String getAmount() {
        return amount;
    }



    public String getTxnid() {
        return txnid;
    }

    public ArrayList getPaymentParts() {
        return paymentParts;
    }

    ArrayList paymentParts;

    public CheckSumModel getProductinfo() {
        return productinfo;
    }

    @SerializedName("productinfo")
   CheckSumModel productinfo;

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
