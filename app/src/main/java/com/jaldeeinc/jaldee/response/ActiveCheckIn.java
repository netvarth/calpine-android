package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sharmila on 13/7/18.
 */

public class ActiveCheckIn {

    public String getJaldeeStartTimeType() {
        return jaldeeStartTimeType;
    }

    public void setJaldeeStartTimeType(String jaldeeStartTimeType) {
        this.jaldeeStartTimeType = jaldeeStartTimeType;
    }

    String jaldeeStartTimeType;

    public String getPaymentStatus() {
        return paymentStatus;
    }

    String paymentStatus;
    String checkInTime;

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

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

    public String getStars() {
        return stars;
    }

    public ActiveCheckIn getRating() {
        return rating;
    }

    String stars;

    public void setRating(ActiveCheckIn rating) {
        this.rating = rating;
    }

    ActiveCheckIn rating;


    public ActiveCheckIn getProviderAccount() {
        return providerAccount;
    }

    public void setProviderAccount(ActiveCheckIn providerAccount) {
        this.providerAccount = providerAccount;
    }

    @SerializedName("providerAccount")
    private ActiveCheckIn providerAccount;

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


    public String getLivetrack() {
        return livetrack;
    }

    public void setLivetrack(String livetrack) {
        this.livetrack = livetrack;
    }

    String livetrack;


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


    public String getQueueEndTime() {
        return queueEndTime;
    }

    public void setQueueEndTime(String queueEndTime) {
        this.queueEndTime = queueEndTime;
    }

    String queueEndTime;

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


    public void setStatusUpdatedTime(String statusUpdatedTime) {
        this.statusUpdatedTime = statusUpdatedTime;
    }

    String statusUpdatedTime;

    public String getStatusUpdatedTime() {
        return statusUpdatedTime;
    }

    public JaldeeWaitlistDistanceTime getJaldeeWaitlistDistanceTime() {
        return jaldeeWaitlistDistanceTime;
    }

    public void setJaldeeWaitlistDistanceTime(JaldeeWaitlistDistanceTime jaldeeWaitlistDistanceTime) {
        this.jaldeeWaitlistDistanceTime = jaldeeWaitlistDistanceTime;
    }

    @SerializedName("jaldeeWaitlistDistanceTime")
    private JaldeeWaitlistDistanceTime jaldeeWaitlistDistanceTime;

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    String batchName;

    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    String parentUuid;


    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    String lattitude;

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    String longitude;

    public String getPrimaryMobileNo() {
        return primaryMobileNo;
    }

    public void setPrimaryMobileNo(String primaryMobileNo) {
        this.primaryMobileNo = primaryMobileNo;
    }

    String primaryMobileNo;

    public String getCalculationMode() {
        return calculationMode;
    }

    public void setCalculationMode(String calculationMode) {
        this.calculationMode = calculationMode;
    }

    String calculationMode;
}