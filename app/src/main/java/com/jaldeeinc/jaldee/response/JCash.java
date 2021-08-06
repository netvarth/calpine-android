package com.jaldeeinc.jaldee.response;

import com.google.gson.JsonObject;

public class JCash {
    private String id;
    private JsonObject consumer;
    private String type;
    private String originalAmt;
    private String remainingAmt;
    private JsonObject jCashIssueInfo;
    private JsonObject jCashSpendRulesInfo;
    private String displayNote;
    private JsonObject jCashOffer;
    private String triggerWhen;
    private String triggerWhenStr;

    public String getId() {
        return id;
    }

    public JsonObject getConsumer() {
        return consumer;
    }

    public String getType() {
        return type;
    }

    public String getOriginalAmt() {
        return originalAmt;
    }

    public String getRemainingAmt() {
        return remainingAmt;
    }

    public JsonObject getjCashIssueInfo() {
        return jCashIssueInfo;
    }

    public JsonObject getjCashSpendRulesInfo() {
        return jCashSpendRulesInfo;
    }

    public String getDisplayNote() {
        return displayNote;
    }

    public JsonObject getjCashOffer() {
        return jCashOffer;
    }

    public String getTriggerWhen() {
        return triggerWhen;
    }

    public String getTriggerWhenStr() {
        return triggerWhenStr;
    }
}
