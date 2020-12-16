package com.jaldeeinc.jaldee.response;

public class RefundInfo {
    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    String amount;

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    String refundId;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    String paymentId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String status;

    public String getRefundMode() {
        return refundMode;
    }

    public void setRefundMode(String refundMode) {
        this.refundMode = refundMode;
    }

    String refundMode;
}
