package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sharmila on 1/8/18.
 */

public class SearchCheckInMessage {

    public String getYnwUuid() {
        return ynwUuid;
    }

    String ynwUuid;
    int locid;
    ArrayList<SearchCheckInMessage> mAllSearch_checkIn;
    String waitlistStatus;
    ServiceDetails service;
    String name;
    SearchCheckInMessage consumer;
    //String userProfile;
    String firstName;
    String date;

    public String getQueueStartTime() {
        return queueStartTime;
    }

    String queueStartTime;
    public ActiveCheckIn getQueue() {
        return Queue;
    }

    @SerializedName("queue")
    private ActiveCheckIn Queue;

    public int getAppxWaitingTime() {
        return appxWaitingTime;
    }

    int appxWaitingTime;

    public String getServiceTime() {
        return serviceTime;
    }

    String serviceTime;

    public ArrayList<SearchCheckInMessage> getWaitlistingFor() {
        return waitlistingFor;
    }

    ArrayList<SearchCheckInMessage> waitlistingFor;


    public String getWaitlistStatus() {
        return waitlistStatus;
    }

    public void setWaitlistStatus(String waitlistStatus) {
        this.waitlistStatus = waitlistStatus;
    }

    public ServiceDetails getService() {
        return service;
    }

    public void setService(ServiceDetails service) {
        this.service = service;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getFirstName() {
        return firstName;
    }

    public String getCalculationMode() {
        return calculationMode;
    }

    public void setCalculationMode(String calculationMode) {
        this.calculationMode = calculationMode;
    }

    String calculationMode;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    String token;



}
