package com.jaldeeinc.jaldee.response;

public class WalletCheckSumModel {
    CheckSumModel response;
    boolean isJCashPaymentSucess;
    boolean isGateWayPaymentNeeded;

    public CheckSumModel getResponse() {
        return response;
    }

    public boolean isJCashPaymentSucess() {
        return isJCashPaymentSucess;
    }

    public boolean isGateWayPaymentNeeded() {
        return isGateWayPaymentNeeded;
    }
}
