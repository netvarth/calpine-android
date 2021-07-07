package com.jaldeeinc.jaldee.response;

import com.google.gson.JsonObject;

public class ActiveDonation {


    String donationEncId;
    JsonObject providerAccount;
    JsonObject location;
    JsonObject donor;
    JsonObject service;
    String donationAmount;
    String uid;

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
}
