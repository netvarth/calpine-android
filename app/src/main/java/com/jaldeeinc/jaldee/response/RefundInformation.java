package com.jaldeeinc.jaldee.response;

public class RefundInformation {
    String amount;
    String accountId;
    String refId;
    String paymentRefId;
    String status;
    String refundMode;
    String providerRefundCommission;
    String consumerRefundCommission;
    String refundDate;

    public String getAmount() {
        return amount;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getRefId() {
        return refId;
    }

    public String getPaymentRefId() {
        return paymentRefId;
    }

    public String getStatus() {
        return status;
    }

    public String getRefundMode() {
        return refundMode;
    }

    public String getProviderRefundCommission() {
        return providerRefundCommission;
    }

    public String getConsumerRefundCommission() {
        return consumerRefundCommission;
    }

    public String getRefundDate() {
        return refundDate;
    }
}
