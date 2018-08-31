package com.netvarth.youneverwait.response;

import java.util.ArrayList;

/**
 * Created by sharmila on 1/8/18.
 */

public class SearchCheckInMessage {

    int locid;
    ArrayList<SearchCheckInMessage> mAllSearch_checkIn;
    String waitlistStatus;
    String service;
    String name;
    String consumer;
    String userProfile;
    String firstName;
    String date;



    public String getWaitlistStatus() {
        return waitlistStatus;
    }

    public void setWaitlistStatus(String waitlistStatus) {
        this.waitlistStatus = waitlistStatus;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConsumer() {
        return consumer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }



    public int getLocid() {
        return locid;
    }

    public void setLocid(int locid) {
        this.locid = locid;
    }

    public ArrayList<SearchCheckInMessage> getmAllSearch_checkIn() {
        return mAllSearch_checkIn;
    }

    public void setmAllSearch_checkIn(ArrayList<SearchCheckInMessage> mAllSearch_checkIn) {
        this.mAllSearch_checkIn = mAllSearch_checkIn;
    }


}
