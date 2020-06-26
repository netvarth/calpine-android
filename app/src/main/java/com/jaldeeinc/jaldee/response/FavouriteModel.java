package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.SerializedName;
import com.jaldeeinc.jaldee.model.ContactModel;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by sharmila on 22/8/18.
 */

public class FavouriteModel {
    String businessName;

    boolean revealPhoneNumber;
    boolean OnlineCheckin;
    boolean FutureCheckin;
    String businessDesc;
    String status;
    String onlinePresence;

    public String getDonationServiceStatus() {
        return donationServiceStatus;
    }

    public void setDonationServiceStatus(String donationServiceStatus) {
        this.donationServiceStatus = donationServiceStatus;
    }

    String donationServiceStatus;




    public String getOnlinePresence() {
        return onlinePresence;
    }

    public void setOnlinePresence(String onlinePresence) {
        this.onlinePresence = onlinePresence;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    int uniqueId;

    public boolean isExpandFlag() {
        return expandFlag;
    }

    public void setExpandFlag(boolean expandFlag) {
        this.expandFlag = expandFlag;
    }

    boolean expandFlag=false;

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public boolean isRevealPhoneNumber() {
        return revealPhoneNumber;
    }

    public void setRevealPhoneNumber(boolean revealPhoneNumber) {
        this.revealPhoneNumber = revealPhoneNumber;
    }

    public boolean isOnlineCheckin() {
        return OnlineCheckin;
    }

    public void setOnlineCheckin(boolean onlineCheckin) {
        OnlineCheckin = onlineCheckin;
    }

    public boolean isFutureCheckin() {
        return FutureCheckin;
    }

    public void setFutureCheckin(boolean futureCheckin) {
        FutureCheckin = futureCheckin;
    }

    public String getBusinessDesc() {
        return businessDesc;
    }

    public void setBusinessDesc(String businessDesc) {
        this.businessDesc = businessDesc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public ArrayList<FavouriteModel> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<FavouriteModel> locations) {
        this.locations = locations;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    @SerializedName("locations")
    private ArrayList<FavouriteModel> locations;

    String place;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    int id;


    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    String locationId;

    public int getLocId() {
        return id;
    }

    public ArrayList<ContactModel> getPhoneNumbers() {
        return phoneNumbers;
    }

    ArrayList<ContactModel> phoneNumbers;


}
