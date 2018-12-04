package com.nv.youneverwait.response;

/**
 * Created by sharmila on 16/11/18.
 */

public class PaytmChecksum {
    public String getCheck() {
        return check;
    }

    String check;

    String checksum;
    String MID;

    public String getChecksum() {
        return checksum;
    }

    public String getMID() {
        return MID;
    }

    public String getINDUSTRY_TYPE_ID() {
        return INDUSTRY_TYPE;
    }

    String INDUSTRY_TYPE;
    String CALLBACK_URL;
    String TXN_AMOUNT;

    public String getIs_success() {
        return is_success;
    }

    String is_success;

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

    public String getORDER_ID() {
        return ORDER_ID;
    }

    String WEBSITE;
    String CHANNEL_ID;
    String CUST_ID;
    String ORDER_ID;

}
