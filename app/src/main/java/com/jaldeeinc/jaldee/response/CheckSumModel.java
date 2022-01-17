package com.jaldeeinc.jaldee.response;

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
    String paymentGateway;
    String providerName;
    String consumerPhoneumber;
    String consumerName;
    String razorpayId;
    String description;
    String orderId;
    String ConsumerEmail;
    boolean isRetry = false;

    String check;
    String MID;
    String paymentEnv;
    String INDUSTRY_TYPE;
    String CALLBACK_URL;
    String TXN_AMOUNT;
    String response;
    String is_success;
    String WEBSITE;
    String CHANNEL_ID;
    String CUST_ID;
    String ORDER_ID;
    String MOBILE_NO;
    String EMAIL;
    String MERC_UNQ_REF;
    String txnToken;

    String paymentmode;

    public String getPaymentmode() {
        return paymentmode;
    }

    public void setPaymentmode(String paymentmode) {
        this.paymentmode = paymentmode;
    }

    public String getConsumerEmail() {
        return ConsumerEmail;
    }

    public String getOrderId() {
        return orderId;
    }


    public String getRazorpayId() {
        return razorpayId;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public String getDescription() {
        return description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
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

    public String getConsumerPhoneumber() {
        return consumerPhoneumber;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getPaymentGateway() {
        return paymentGateway;
    }

    public String getMerchantKey() {
        return merchantKey;
    }

    public String getPaymentEnv() {
        return paymentEnv;
    }

    public boolean isRetry() {
        return isRetry;
    }

    public void setRetry(boolean retry) {
        isRetry = retry;
    }


    public String getCheck() {
        return check;
    }

    public String getMID() {
        return MID;
    }

    public String getINDUSTRY_TYPE_ID() {
        return INDUSTRY_TYPE;
    }

    public String getCALLBACK_URL() {
        return CALLBACK_URL;
    }

    public String getTXN_AMOUNT() {
        return TXN_AMOUNT;
    }

    public String getResponse() {
        return response;
    }

    public String getIs_success() {
        return is_success;
    }

    public String getWEBSITE() {
        return WEBSITE;
    }

    public String getCHANNEL_ID() {
        return CHANNEL_ID;
    }

    public String getCUST_ID() {
        return CUST_ID;
    }

    public String getORDER_ID() {
        return ORDER_ID;
    }

    public String getMOBILE_NO() {
        return MOBILE_NO;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public String getMERC_UNQ_REF() {
        return MERC_UNQ_REF;
    }

    public String getTxnToken() {
        return txnToken;
    }

}
