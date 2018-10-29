package com.nv.youneverwait.response;

import com.google.gson.annotations.SerializedName;

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

    int id;




   // int locId;

    public int getLocId() {
        return id;
    }


}
