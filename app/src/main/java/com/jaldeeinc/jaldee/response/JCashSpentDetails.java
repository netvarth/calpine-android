package com.jaldeeinc.jaldee.response;

import com.google.gson.JsonObject;

public class JCashSpentDetails {
    public String createdDate;
    public int id;
    public String jCashTxnType;
    public String jCashTxnTypeStr;
    public String spentRef;
    public String amount;
    public JsonObject jCash;
    public String spentToAcc;
    public String spentToSpId;
    public String spentToBizName;

    public String getCreatedDate() {
        return createdDate;
    }

    public int getId() {
        return id;
    }

    public String getjCashTxnType() {
        return jCashTxnType;
    }

    public String getjCashTxnTypeStr() {
        return jCashTxnTypeStr;
    }

    public String getSpentRef() {
        return spentRef;
    }

    public String getAmount() {
        return amount;
    }

    public JsonObject getjCash() {
        return jCash;
    }

    public String getSpentToAcc() {
        return spentToAcc;
    }

    public String getSpentToSpId() {
        return spentToSpId;
    }

    public String getSpentToBizName() {
        return spentToBizName;
    }
}
