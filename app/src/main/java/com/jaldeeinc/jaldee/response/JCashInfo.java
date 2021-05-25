package com.jaldeeinc.jaldee.response;

import com.google.gson.JsonObject;

public class JCashInfo {

    public String totCashAwarded;
    public String totCashSpent;
    public String totCashAvailable;
    private JsonObject cashExpiry;
    private JsonObject cashSpend;

    public String getTotCashAwarded() {
        return totCashAwarded;
    }

    public String getTotCashSpent() {
        return totCashSpent;
    }

    public String getTotCashAvailable() {
        return totCashAvailable;
    }

    public JsonObject getCashExpiry() {
        return cashExpiry;
    }

    public JsonObject getCashSpend() {
        return cashSpend;
    }
}
