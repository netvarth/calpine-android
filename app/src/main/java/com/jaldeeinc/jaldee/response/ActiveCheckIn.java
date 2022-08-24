package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.SerializedName;
import com.jaldeeinc.jaldee.model.FileAttachment;
import com.jaldeeinc.jaldee.model.RlsdQnr;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sharmila on 13/7/18.
 */

public class ActiveCheckIn implements Serializable {


    int token;
    String checkinEncId;
    String appointmentEncId;
    String date;
    @SerializedName("providerAccount")
    private ActiveCheckIn providerAccount;
    @SerializedName("serviceSector")
    private ActiveCheckIn serviceSector;
    int id;
    String domain;
    String businessName;
    String licensePkgID;
    String uniqueId;
    String corpId;
    String branchId;
    String userSubdomain;
    String profileId;
    @SerializedName("consumer")
    private ConsumerDetails consumer;
    String firstName;
    String lastName;
    boolean favourite;
    boolean phone_verified;
    boolean email_verified;
    int parent;
    //    int jaldeeConsumer;
    String videoCallButton;
    String videoCallMessage;
    boolean isRescheduled;
    private QuestionnaireResponse questionnaire;
    private QuestionnaireResponse serviceOption;
    String countryCode;

    public QuestionnaireResponse getServiceOption() {
        return serviceOption;
    }

    public String getAppointmentEncId() {
        return appointmentEncId;
    }

    public void setAppointmentEncId(String appointmentEncId) {
        this.appointmentEncId = appointmentEncId;
    }

    public ActiveCheckIn getServiceSector() {
        return serviceSector;
    }

    public String getDomain() {
        return domain;
    }

    public ConsumerDetails getConsumer() {
        return consumer;
    }

    public void setConsumer(ConsumerDetails consumer) {
        this.consumer = consumer;
    }

    public boolean isRescheduled() {
        return isRescheduled;
    }

    public String getVideoCallButton() {
        return videoCallButton;
    }

    public String getVideoCallMessage() {
        return videoCallMessage;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public ServiceDetails getService() {
        return service;
    }

    public void setService(ServiceDetails service) {
        this.service = service;
    }

    @SerializedName("service")
    private ServiceDetails service;

    public VirtualServiceDetails getVirtualService() {
        return virtualService;
    }

    public void setVirtualService(VirtualServiceDetails virtualService) {
        this.virtualService = virtualService;
    }

    @SerializedName("virtualService")
    private VirtualServiceDetails virtualService;


    public ProviderDetails getProvider() {
        return provider;
    }

    public void setProvider(ProviderDetails provider) {
        this.provider = provider;
    }

    @SerializedName("provider")
    private ProviderDetails provider;


    public ArrayList<ServiceDetails> getVirtualCallingModes() {
        return virtualCallingModes;
    }

    public void setVirtualCallingModes(ArrayList<ServiceDetails> virtualCallingModes) {
        this.virtualCallingModes = virtualCallingModes;
    }

    @SerializedName("virtualCallingModes")
    private ArrayList<ServiceDetails> virtualCallingModes;
    String name;
    int department;
    String deptName;
    String serviceType;
    int multiples;
    String livetrack;
    String waitlistStatus;
    String statusUpdatedTime;
    int partySize;
    String consumerNote;
    String recalculatedTime;
    int appxWaitingTime;
    String appmtDate;
    String appmtTime;
    ArrayList<RlsdQnr> releasedQnr;

    public void setReleasedQnr(ArrayList<RlsdQnr> releasedQnr) {
        this.releasedQnr = releasedQnr;
    }

    public ArrayList<RlsdQnr> getReleasedQnr() {
        return releasedQnr;
    }
    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }

    boolean hasAttachment;


    public int getAppxWaitingTime() {
        return appxWaitingTime;
    }

    public void setAppxWaitingTime(int appxWaitingTime) {
        this.appxWaitingTime = appxWaitingTime;
    }

    @SerializedName("queue")
    private ActiveCheckIn queue;

    @SerializedName("location")
    private LocationDetails location;
    String place;
    String address;
    String longitude;
    String lattitude;
    String googleMapUrl;
    String queueStartTime;
    String queueEndTime;
    int personAhead;
    String waitlistedBy;
    int personsAhead = -1;
    @SerializedName("waitlistingFor")
    private ArrayList<ActiveCheckIn> waitlistingFor;
    String phoneNo;
    String email;
    String ynwUuid;

    public RatingResponse getRating() {
        return rating;
    }

    public void setRating(RatingResponse rating) {
        this.rating = rating;
    }

    @SerializedName("rating")
    private RatingResponse rating;

    String paymentStatus;
    String billStatus;
    String calculationMode;
    String checkInTime;
    String serviceTime;
    boolean prescShared;
    String prescUrl;
    String prescShortUrl;

    public String getPrescShortUrl() {
        return prescShortUrl;
    }

    public String getPrescUrl() {
        return prescUrl;
    }

    public void setPrescUrl(String prescUrl) {
        this.prescUrl = prescUrl;
    }

    public boolean isPrescShared() {
        return prescShared;
    }

    public void setPrescShared(boolean prescShared) {
        this.prescShared = prescShared;
    }

    public String getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(String serviceTime) {
        this.serviceTime = serviceTime;
    }

    String billViewStatus;
    double amountPaid;
    double amountDue;
    int billId;
    String parentUuid;

    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    String batchName;

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    @SerializedName("jaldeeWaitlistDistanceTime")
    private JaldeeWaitlistDistanceTime jaldeeWaitlistDistanceTime;
    String jaldeeStartTimeType;
    String waitlistPhoneNumber;
    int account;
    boolean onlineRequest;
    boolean kioskRequest;
    boolean firstCheckIn;
    boolean active;
    int accessScope;
    String waitlistMode;
    boolean consumerStarted;
    boolean callingStatus;
    String showToken;

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public String getCheckinEncId() {
        return checkinEncId;
    }

    public void setCheckinEncId(String checkinEncId) {
        this.checkinEncId = checkinEncId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ActiveCheckIn getProviderAccount() {
        return providerAccount;
    }

    public void setProviderAccount(ActiveCheckIn providerAccount) {
        this.providerAccount = providerAccount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getLicensePkgID() {
        return licensePkgID;
    }

    public void setLicensePkgID(String licensePkgID) {
        this.licensePkgID = licensePkgID;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getUserSubdomain() {
        return userSubdomain;
    }

    public void setUserSubdomain(String userSubdomain) {
        this.userSubdomain = userSubdomain;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

//    public ActiveCheckIn getConsumer() {
//        return consumer;
//    }

//    public void setConsumer(ActiveCheckIn consumer) {
//        this.consumer = consumer;
//    }

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

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public boolean isPhone_verified() {
        return phone_verified;
    }

    public void setPhone_verified(boolean phone_verified) {
        this.phone_verified = phone_verified;
    }

    public boolean isEmail_verified() {
        return email_verified;
    }

    public void setEmail_verified(boolean email_verified) {
        this.email_verified = email_verified;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

//    public int getJaldeeConsumer() {
//        return jaldeeConsumer;
//    }
//
//    public void setJaldeeConsumer(int jaldeeConsumer) {
//        this.jaldeeConsumer = jaldeeConsumer;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDepartment() {
        return department;
    }

    public void setDepartment(int department) {
        this.department = department;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public int getMultiples() {
        return multiples;
    }

    public void setMultiples(int multiples) {
        this.multiples = multiples;
    }

    public String getLivetrack() {
        return livetrack;
    }

    public void setLivetrack(String livetrack) {
        this.livetrack = livetrack;
    }

    public String getWaitlistStatus() {
        return waitlistStatus;
    }

    public void setWaitlistStatus(String waitlistStatus) {
        this.waitlistStatus = waitlistStatus;
    }

    public String getStatusUpdatedTime() {
        return statusUpdatedTime;
    }

    public void setStatusUpdatedTime(String statusUpdatedTime) {
        this.statusUpdatedTime = statusUpdatedTime;
    }

    public int getPartySize() {
        return partySize;
    }

    public void setPartySize(int partySize) {
        this.partySize = partySize;
    }

    public String getConsumerNote() {
        return consumerNote;
    }

    public void setConsumerNote(String consumerNote) {
        this.consumerNote = consumerNote;
    }

    public String getRecalculatedTime() {
        return recalculatedTime;
    }

    public void setRecalculatedTime(String recalculatedTime) {
        this.recalculatedTime = recalculatedTime;
    }

    public ActiveCheckIn getQueue() {
        return queue;
    }

    public void setQueue(ActiveCheckIn queue) {
        this.queue = queue;
    }

    public LocationDetails getLocation() {
        return location;
    }

    public void setLocation(LocationDetails location) {
        this.location = location;
    }


    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getGoogleMapUrl() {
        return googleMapUrl;
    }

    public void setGoogleMapUrl(String googleMapUrl) {
        this.googleMapUrl = googleMapUrl;
    }

    public String getQueueStartTime() {
        return queueStartTime;
    }

    public void setQueueStartTime(String queueStartTime) {
        this.queueStartTime = queueStartTime;
    }

    public String getQueueEndTime() {
        return queueEndTime;
    }

    public void setQueueEndTime(String queueEndTime) {
        this.queueEndTime = queueEndTime;
    }

    public int getPersonAhead() {
        return personAhead;
    }

    public void setPersonAhead(int personAhead) {
        this.personAhead = personAhead;
    }

    public String getWaitlistedBy() {
        return waitlistedBy;
    }

    public void setWaitlistedBy(String waitlistedBy) {
        this.waitlistedBy = waitlistedBy;
    }

    public int getPersonsAhead() {
        return personsAhead;
    }

    public void setPersonsAhead(int personsAhead) {
        this.personsAhead = personsAhead;
    }

    public ArrayList<ActiveCheckIn> getWaitlistingFor() {
        return waitlistingFor;
    }

    public void setWaitlistingFor(ArrayList<ActiveCheckIn> waitlistingFor) {
        this.waitlistingFor = waitlistingFor;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getYnwUuid() {
        return ynwUuid;
    }

    public void setYnwUuid(String ynwUuid) {
        this.ynwUuid = ynwUuid;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(String billStatus) {
        this.billStatus = billStatus;
    }


    public String getCalculationMode() {
        return calculationMode;
    }

    public void setCalculationMode(String calculationMode) {
        this.calculationMode = calculationMode;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

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


    public String getBillViewStatus() {
        return billViewStatus;
    }

    public void setBillViewStatus(String billViewStatus) {
        this.billViewStatus = billViewStatus;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public JaldeeWaitlistDistanceTime getJaldeeWaitlistDistanceTime() {
        return jaldeeWaitlistDistanceTime;
    }

    public void setJaldeeWaitlistDistanceTime(JaldeeWaitlistDistanceTime jaldeeWaitlistDistanceTime) {
        this.jaldeeWaitlistDistanceTime = jaldeeWaitlistDistanceTime;
    }

    public String getJaldeeStartTimeType() {
        return jaldeeStartTimeType;
    }

    public void setJaldeeStartTimeType(String jaldeeStartTimeType) {
        this.jaldeeStartTimeType = jaldeeStartTimeType;
    }

    public String getWaitlistPhoneNumber() {
        return waitlistPhoneNumber;
    }

    public void setWaitlistPhoneNumber(String waitlistPhoneNumber) {
        this.waitlistPhoneNumber = waitlistPhoneNumber;
    }

    public int getAccount() {
        return account;
    }

    public void setAccount(int account) {
        this.account = account;
    }

    public boolean isOnlineRequest() {
        return onlineRequest;
    }

    public void setOnlineRequest(boolean onlineRequest) {
        this.onlineRequest = onlineRequest;
    }

    public boolean isKioskRequest() {
        return kioskRequest;
    }

    public void setKioskRequest(boolean kioskRequest) {
        this.kioskRequest = kioskRequest;
    }

    public boolean isFirstCheckIn() {
        return firstCheckIn;
    }

    public void setFirstCheckIn(boolean firstCheckIn) {
        this.firstCheckIn = firstCheckIn;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getAccessScope() {
        return accessScope;
    }

    public void setAccessScope(int accessScope) {
        this.accessScope = accessScope;
    }

    public String getWaitlistMode() {
        return waitlistMode;
    }

    public void setWaitlistMode(String waitlistMode) {
        this.waitlistMode = waitlistMode;
    }

    public boolean isConsumerStarted() {
        return consumerStarted;
    }

    public void setConsumerStarted(boolean consumerStarted) {
        this.consumerStarted = consumerStarted;
    }

    public boolean isCallingStatus() {
        return callingStatus;
    }

    public void setCallingStatus(boolean callingStatus) {
        this.callingStatus = callingStatus;
    }

    public String getShowToken() {
        return showToken;
    }

    public void setShowToken(String showToken) {
        this.showToken = showToken;
    }

    public String getAppmtDate() {
        return appmtDate;
    }

    public void setAppmtDate(String appmtDate) {
        this.appmtDate = appmtDate;
    }

    public String getAppmtTime() {
        return appmtTime;
    }

    public void setAppmtTime(String appmtTime) {
        this.appmtTime = appmtTime;
    }

    public void setVideoCallButton(String videoCallButton) {
        this.videoCallButton = videoCallButton;
    }

    public QuestionnaireResponse getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(QuestionnaireResponse questionnaire) {
        this.questionnaire = questionnaire;
    }

    ArrayList<FileAttachment> attchment;

    public ArrayList<FileAttachment> getAttchment() {
        return attchment;
    }

    @SerializedName("questionnaires")
    ArrayList<QuestionnaireResponse> questionnaires;

    public ArrayList<QuestionnaireResponse> getQuestionnaires() {
        return questionnaires;
    }

    public void setQuestionnaires(ArrayList<QuestionnaireResponse> questionnaires) {
        this.questionnaires = questionnaires;
    }
}