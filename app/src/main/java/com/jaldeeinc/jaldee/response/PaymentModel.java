package com.jaldeeinc.jaldee.response;

import java.util.ArrayList;

/**
 * Created by sharmila on 8/8/18.
 */

public class PaymentModel {
    boolean indiaPay;
    boolean internationalPay;
    ArrayList<PayMode> indiaBankInfo;
    ArrayList<PayMode> internationalBankInfo;
    boolean isJaldeeBank;
    String paymentGateways;
    String PayGateways;

    public String getPayGateways() {
        return PayGateways;
    }

    public String getPaymentGateways() {
        return paymentGateways;
    }

    public boolean isIndiaPay() {
        return indiaPay;
    }

    public boolean isInternationalPay() {
        return internationalPay;
    }

    public ArrayList<PayMode> getIndiaBankInfo() {
        return indiaBankInfo;
    }

    public ArrayList<PayMode> getInternationalBankInfo() {
        return internationalBankInfo;
    }

    public boolean isJaldeeBank() {
        return isJaldeeBank;
    }

}
