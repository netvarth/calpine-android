package com.jaldeeinc.jaldee.model;

import com.razorpay.PaymentData;

public class RazorpayModel {
    String razorpay_payment_id;
    String razorpay_order_id;
    String razorpay_signature;
    String status;
    String txnid;

    public RazorpayModel(PaymentData paymentData) {
        this.razorpay_order_id = paymentData.getOrderId();
        this.razorpay_payment_id = paymentData.getPaymentId();
        this.razorpay_signature =  paymentData.getSignature();
    }

    public String getRazorpay_payment_id() {
        return razorpay_payment_id;
    }

    public String getRazorpay_order_id() {
        return razorpay_order_id;
    }

    public String getRazorpay_signature() {
        return razorpay_signature;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTxnid() {
        return txnid;
    }

    public void setTxnid(String txnid) {
        this.txnid = txnid;
    }
}
