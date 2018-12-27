package com.nv.youneverwait.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sharmila on 13/7/18.
 */

public class ActiveCheckIn {

    public String getPaymentStatus() {
        return paymentStatus;
    }

    String paymentStatus;
    public int getPartySize() {
        return partySize;
    }

    int partySize;
    public boolean isFavourite() {
        return favourite;
    }

    boolean favourite;
    public int getPersonsAhead() {
        return personsAhead;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    int id;

    double amountPaid;

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public double getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(double amountDue) {
        this.amountDue = amountDue;
    }

    double amountDue;

    int personsAhead=-1;
    public String getYnwUuid() {
        return ynwUuid;
    }

    public void setYnwUuid(String ynwUuid) {
        this.ynwUuid = ynwUuid;
    }

    String ynwUuid;

    public String getGoogleMapUrl() {
        return googleMapUrl;
    }

    String googleMapUrl;

    public String getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(String billStatus) {
        this.billStatus = billStatus;
    }

    String billStatus;

    public String getBillViewStatus() {
        return billViewStatus;
    }

    public void setBillViewStatus(String billViewStatus) {
        this.billViewStatus = billViewStatus;
    }

    String billViewStatus;

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setPartySize(int partySize) {
        this.partySize = partySize;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public void setPersonsAhead(int personsAhead) {
        this.personsAhead = personsAhead;
    }

    public void setGoogleMapUrl(String googleMapUrl) {
        this.googleMapUrl = googleMapUrl;
    }

    public void setProvider(ActiveCheckIn provider) {
        this.provider = provider;
    }

    public void setWaitlistingFor(ArrayList<ActiveCheckIn> waitlistingFor) {
        this.waitlistingFor = waitlistingFor;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public void setQueue(ActiveCheckIn queue) {
        Queue = queue;
    }

    @SerializedName("consumer")

    private ActiveCheckIn consumer;

    public ActiveCheckIn getConsumer() {
        return consumer;
    }

    public void setConsumer(ActiveCheckIn consumer) {
        this.consumer = consumer;
    }

    public ActiveCheckIn getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(ActiveCheckIn userProfile) {
        this.userProfile = userProfile;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @SerializedName("userProfile")
    private ActiveCheckIn userProfile;

    String firstName;
    String lastName;

    public ActiveCheckIn getProvider() {
        return provider;
    }

    @SerializedName("provider")
    private ActiveCheckIn provider;

    public ArrayList<ActiveCheckIn> getWaitlistingFor() {
        return waitlistingFor;
    }

    @SerializedName("waitlistingFor")
    private ArrayList<ActiveCheckIn> waitlistingFor;


    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    String uniqueId;

    public ActiveCheckIn getLocation() {
        return location;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    String place;

    public String getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(String serviceTime) {
        this.serviceTime = serviceTime;
    }

    String serviceTime;
    public void setLocation(ActiveCheckIn location) {
        this.location = location;
    }

    @SerializedName("location")
    private ActiveCheckIn location;


    public ActiveCheckIn getService() {
        return service;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    int token;

    public void setService(ActiveCheckIn service) {
        this.service = service;
    }

    @SerializedName("service")
    private ActiveCheckIn service;

    public String getBusinessName() {
        return businessName;
    }

    String businessName;

    public String getDate() {
        return date;
    }

    String date;

    public int getAppxWaitingTime() {
        return appxWaitingTime;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAppxWaitingTime(int appxWaitingTime) {
        this.appxWaitingTime = appxWaitingTime;
    }

    int appxWaitingTime=-1;


    public ActiveCheckIn getQueue() {
        return Queue;
    }

    @SerializedName("queue")
    private ActiveCheckIn Queue;

    public String getQueueStartTime() {
        return queueStartTime;
    }

    public void setQueueStartTime(String queueStartTime) {
        this.queueStartTime = queueStartTime;
    }

    String queueStartTime;

    public String getWaitlistStatus() {
        return waitlistStatus;
    }

    public void setWaitlistStatus(String waitlistStatus) {
        this.waitlistStatus = waitlistStatus;
    }

    String waitlistStatus;

    public boolean isFavFlag() {
        return favFlag;
    }

    public void setFavFlag(boolean favFlag) {
        this.favFlag = favFlag;
    }

    boolean favFlag=false;


}
