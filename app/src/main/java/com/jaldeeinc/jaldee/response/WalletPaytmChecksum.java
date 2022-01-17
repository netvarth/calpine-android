package com.jaldeeinc.jaldee.response;

public class WalletPaytmChecksum {

    PaytmChecksum response;
    boolean isJCashPaymentSucess;
    boolean isGateWayPaymentNeeded;

    public PaytmChecksum getResponse() {
        return response;
    }

    public boolean isJCashPaymentSucess() {
        return isJCashPaymentSucess;
    }

    public boolean isGateWayPaymentNeeded() {
        return isGateWayPaymentNeeded;
    }
}
