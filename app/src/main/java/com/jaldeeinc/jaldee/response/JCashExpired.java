package com.jaldeeinc.jaldee.response;

public class JCashExpired {
    String createdDate;
    String id;
    String jCashTxnType;
    String jCashTxnTypeStr;
    Double amount;
    JCash jCash;

    public String getCreatedDate() {
        return createdDate;
    }

    public String getId() {
        return id;
    }

    public String getjCashTxnType() {
        return jCashTxnType;
    }

    public String getjCashTxnTypeStr() {
        return jCashTxnTypeStr;
    }

    public Double getAmount() {
        return amount;
    }

    public JCash getjCash() {
        return jCash;
    }
}
