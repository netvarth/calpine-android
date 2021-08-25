package com.jaldeeinc.jaldee.response;

import java.io.Serializable;

/**
 * Created by sharmila on 16/11/18.
 */

public class PaytmChecksum implements Serializable {

    String check;
    String checksum;
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

    public String getTxnToken() { return txnToken; }

    public String getTXNResponse() { return response; }

    public String getCheck() { return check; }

    public String getPaymentEnv() { return paymentEnv; }

    public void setPaymentEnv(String paymentEnv) {
        this.paymentEnv = paymentEnv;
    }

    public String getChecksum() {
        return checksum;
    }

    public String getMID() {
        return MID;
    }

    public String getINDUSTRY_TYPE_ID() {
        return INDUSTRY_TYPE;
    }

    public String getIs_success() {
        return is_success;
    }

    public String getCALLBACK_URL() {
        return CALLBACK_URL;
    }

    public String getTXN_AMOUNT() {
        return TXN_AMOUNT;
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

    public String getORDER_ID() { return ORDER_ID; }

    public String getMOBILE_NO() { return MOBILE_NO; }

    public String getEMAIL() { return EMAIL; }

    public String getMERC_UNQ_REF() { return MERC_UNQ_REF; }

    public void setMERC_UNQ_REF(String MERC_UNQ_REF) { this.MERC_UNQ_REF = MERC_UNQ_REF; }

}
