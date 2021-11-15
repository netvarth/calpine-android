package com.jaldeeinc.jaldee.response;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class ActiveDonation implements Serializable {


    String donationEncId;
    JsonObject providerAccount;
    JsonObject location;
    JsonObject donor;
    JsonObject service;
    String donationAmount;
    String uid;
    private QuestionnaireResponse questionnaire;


    public String getUid() { return uid; }

    public String getDonationEncId() {
        return donationEncId;
    }

    public void setDonationEncId(String donationEncId) {
        this.donationEncId = donationEncId;
    }

    public JsonObject getProviderAccount() {
        return providerAccount;
    }

    public JsonObject getLocation() {
        return location;
    }

    public JsonObject getDonor() {
        return donor;
    }

    public JsonObject getService() {
        return service;
    }

    public String getDonationAmount() {
        return donationAmount;
    }

    public QuestionnaireResponse getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(QuestionnaireResponse questionnaire) {
        this.questionnaire = questionnaire;
    }
}
