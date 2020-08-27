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

    public String getConsumerEmail() {
        return ConsumerEmail;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setPaymentGateway(String paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public void setConsumerPhoneumber(String consumerPhoneumber) {
        this.consumerPhoneumber = consumerPhoneumber;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    public void setRazorpayId(String razorpayId) {
        this.razorpayId = razorpayId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setConsumerEmail(String consumerEmail) {
        ConsumerEmail = consumerEmail;
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

    public void setPaymentEnv(String paymentEnv) {
        this.paymentEnv = paymentEnv;
    }

    String paymentEnv;

    public boolean isRetry() {
        return isRetry;
    }

    public void setRetry(boolean retry) {
        isRetry = retry;
    }
}
