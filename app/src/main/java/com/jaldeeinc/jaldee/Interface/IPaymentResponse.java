package com.jaldeeinc.jaldee.Interface;

public interface IPaymentResponse {
    void sendPaymentResponse(String paymentStatus, String orderid);
    void update();
    void setPaymentRequestId(String paymentRequestId);
}
