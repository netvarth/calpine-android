package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sharmila on 13/7/18.
 */

public class ActiveAppointment implements Serializable {

    String appointmentEncId;
    String uid;
    @SerializedName("consumer")
    private ActiveAppointment consumer;
    int id;
    @SerializedName("userProfile")
    private ActiveAppointment userProfile;
    String firstName;
    String lastName;
    String primaryMobileNo;
    String dob;
    String gender;
    String email;
    String emailVerified;
    String phoneVerified;
    @SerializedName("createdBy")
    private ActiveAppointment createdBy;
    String userName;
    String phone_verified;
    String email_verified;
    boolean favourite;
    String SignedUp;
    @SerializedName("service")
    private ServiceDetails service;
    String name;
    String description;
    String serviceDuration;
    String notificationType;
    String notification;
    String isPrePayment;
    String totalAmount;
    String bType;
    String status;
    String taxable;
    String serviceType;
    String multiples;
    String livetrack;
    @SerializedName("schedule")
    private ActiveAppointment schedule;
    @SerializedName("apptSchedule")
    private ActiveAppointment apptSchedule;
    String recurringType;
    String startDate;
    @SerializedName("terminator")
    private ActiveAppointment terminator;
    String endDate;
    String noOfOccurance;
    @SerializedName("timeSlots")
    private ArrayList<ActiveAppointment> timeSlots;
    String sTime;
    String eTime;
    String timeDuration;
    String parallelServing;
    String batchEnable;
    String todayAppt;
    String futureAppt;
    String instantSchedule;
    String openNow;
    String account;
    String apptStatus;
    @SerializedName("appmtFor")
    private ArrayList<ActiveAppointment> appmtFor;
    String apptTime;
    String appmtDate;
    String appmtTime;
    String consumerNote;
    String apptBy;
    @SerializedName("providerConsumer")
    private ActiveAppointment providerConsumer;
    String parent;
    String jaldeeConsumer;
    String paymentStatus;
    String paymentDue;
    String isFirstAppmt;
    String onlineRequest;
    String firstCheckIn;
    @SerializedName("label")
    private ActiveAppointment label;
    @SerializedName("providerAccount")
    private ActiveAppointment providerAccount;
    String businessName;
    String licensePkgID;
    String uniqueId;
    String corpId;
    String branchId;
    String userSubdomain;
    String profileId;
    @SerializedName("location")
    private ActiveAppointment location;
    String place;
    String address;
    String longitude;
    String lattitude;
    String googleMapUrl;
    String appointmentMode;
    @SerializedName("virtualService")
    private ActiveAppointment virtualService;
    String WhatsApp;
    String phoneNumber;
    String accessScope;
    @SerializedName("jaldeeApptDistanceTime")
    private ActiveAppointment jaldeeApptDistanceTime;
    String pollingTime;
    @SerializedName("jaldeeDistanceTime")
    private ActiveAppointment jaldeeDistanceTime;
    @SerializedName("jaldeeDistance")
    private ActiveAppointment jaldeeDistance;
    String distance;
    String unit;
    @SerializedName("jaldeelTravelTime")
    private ActiveAppointment jaldeelTravelTime;
    String travelMode;
    String travelTime;
    String timeUnit;
    String jaldeeStartTimeType;
    String consumerStarted;
    String callingStatus;
    String billStatus;
    String billViewStatus;
    String billId;
    String amountPaid;
    double amountDue;
    String batchId;
    String phoneNo;
    String consumerNoteTitle;

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    String Phone;

    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    String parentUuid;

    public double getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(double amountDue) {
        this.amountDue = amountDue;
    }

    String statusUpdatedTime;
    String apptTakenTime;
    String batchName;

    public ActiveAppointment getRating() {
        return rating;
    }

    public void setRating(ActiveAppointment rating) {
        this.rating = rating;
    }

    @SerializedName("rating")
    private ActiveAppointment rating;

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }

    String stars;

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getAppointmentEncId() {
        return appointmentEncId;
    }

    public void setAppointmentEncId(String appointmentEncId) {
        this.appointmentEncId = appointmentEncId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ActiveAppointment getConsumer() {
        return consumer;
    }

    public void setConsumer(ActiveAppointment consumer) {
        this.consumer = consumer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ActiveAppointment getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(ActiveAppointment userProfile) {
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

    public String getPrimaryMobileNo() {
        return primaryMobileNo;
    }

    public void setPrimaryMobileNo(String primaryMobileNo) {
        this.primaryMobileNo = primaryMobileNo;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(String emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(String phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public ActiveAppointment getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(ActiveAppointment createdBy) {
        this.createdBy = createdBy;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone_verified() {
        return phone_verified;
    }

    public void setPhone_verified(String phone_verified) {
        this.phone_verified = phone_verified;
    }

    public String getEmail_verified() {
        return email_verified;
    }

    public void setEmail_verified(String email_verified) {
        this.email_verified = email_verified;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public String getSignedUp() {
        return SignedUp;
    }

    public void setSignedUp(String signedUp) {
        SignedUp = signedUp;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceDuration() {
        return serviceDuration;
    }

    public void setServiceDuration(String serviceDuration) {
        this.serviceDuration = serviceDuration;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getIsPrePayment() {
        return isPrePayment;
    }

    public void setIsPrePayment(String isPrePayment) {
        this.isPrePayment = isPrePayment;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getbType() {
        return bType;
    }

    public void setbType(String bType) {
        this.bType = bType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTaxable() {
        return taxable;
    }

    public void setTaxable(String taxable) {
        this.taxable = taxable;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getMultiples() {
        return multiples;
    }

    public void setMultiples(String multiples) {
        this.multiples = multiples;
    }

    public String getLivetrack() {
        return livetrack;
    }

    public void setLivetrack(String livetrack) {
        this.livetrack = livetrack;
    }

    public ActiveAppointment getSchedule() {
        return schedule;
    }

    public void setSchedule(ActiveAppointment schedule) {
        this.schedule = schedule;
    }

    public ActiveAppointment getApptSchedule() {
        return apptSchedule;
    }

    public void setApptSchedule(ActiveAppointment apptSchedule) {
        this.apptSchedule = apptSchedule;
    }

    public String getRecurringType() {
        return recurringType;
    }

    public void setRecurringType(String recurringType) {
        this.recurringType = recurringType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public ActiveAppointment getTerminator() {
        return terminator;
    }

    public void setTerminator(ActiveAppointment terminator) {
        this.terminator = terminator;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getNoOfOccurance() {
        return noOfOccurance;
    }

    public void setNoOfOccurance(String noOfOccurance) {
        this.noOfOccurance = noOfOccurance;
    }

    public ArrayList<ActiveAppointment> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(ArrayList<ActiveAppointment> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public String getsTime() {
        return sTime;
    }

    public void setsTime(String sTime) {
        this.sTime = sTime;
    }

    public String geteTime() {
        return eTime;
    }

    public void seteTime(String eTime) {
        this.eTime = eTime;
    }

    public String getTimeDuration() {
        return timeDuration;
    }

    public void setTimeDuration(String timeDuration) {
        this.timeDuration = timeDuration;
    }

    public String getParallelServing() {
        return parallelServing;
    }

    public void setParallelServing(String parallelServing) {
        this.parallelServing = parallelServing;
    }

    public String getBatchEnable() {
        return batchEnable;
    }

    public void setBatchEnable(String batchEnable) {
        this.batchEnable = batchEnable;
    }

    public String getTodayAppt() {
        return todayAppt;
    }

    public void setTodayAppt(String todayAppt) {
        this.todayAppt = todayAppt;
    }

    public String getFutureAppt() {
        return futureAppt;
    }

    public void setFutureAppt(String futureAppt) {
        this.futureAppt = futureAppt;
    }

    public String getInstantSchedule() {
        return instantSchedule;
    }

    public void setInstantSchedule(String instantSchedule) {
        this.instantSchedule = instantSchedule;
    }

    public String getOpenNow() {
        return openNow;
    }

    public void setOpenNow(String openNow) {
        this.openNow = openNow;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getApptStatus() {
        return apptStatus;
    }

    public void setApptStatus(String apptStatus) {
        this.apptStatus = apptStatus;
    }

    public ArrayList<ActiveAppointment> getAppmtFor() {
        return appmtFor;
    }

    public void setAppmtFor(ArrayList<ActiveAppointment> appmtFor) {
        this.appmtFor = appmtFor;
    }

    public String getApptTime() {
        return apptTime;
    }

    public void setApptTime(String apptTime) {
        this.apptTime = apptTime;
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

    public String getConsumerNote() {
        return consumerNote;
    }

    public void setConsumerNote(String consumerNote) {
        this.consumerNote = consumerNote;
    }

    public String getApptBy() {
        return apptBy;
    }

    public void setApptBy(String apptBy) {
        this.apptBy = apptBy;
    }

    public ActiveAppointment getProviderConsumer() {
        return providerConsumer;
    }

    public void setProviderConsumer(ActiveAppointment providerConsumer) {
        this.providerConsumer = providerConsumer;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getJaldeeConsumer() {
        return jaldeeConsumer;
    }

    public void setJaldeeConsumer(String jaldeeConsumer) {
        this.jaldeeConsumer = jaldeeConsumer;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentDue() {
        return paymentDue;
    }

    public void setPaymentDue(String paymentDue) {
        this.paymentDue = paymentDue;
    }

    public String getIsFirstAppmt() {
        return isFirstAppmt;
    }

    public void setIsFirstAppmt(String isFirstAppmt) {
        this.isFirstAppmt = isFirstAppmt;
    }

    public String getOnlineRequest() {
        return onlineRequest;
    }

    public void setOnlineRequest(String onlineRequest) {
        this.onlineRequest = onlineRequest;
    }

    public String getFirstCheckIn() {
        return firstCheckIn;
    }

    public void setFirstCheckIn(String firstCheckIn) {
        this.firstCheckIn = firstCheckIn;
    }

    public ActiveAppointment getLabel() {
        return label;
    }

    public void setLabel(ActiveAppointment label) {
        this.label = label;
    }

    public ActiveAppointment getProviderAccount() {
        return providerAccount;
    }

    public void setProviderAccount(ActiveAppointment providerAccount) {
        this.providerAccount = providerAccount;
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

    public ActiveAppointment getLocation() {
        return location;
    }

    public void setLocation(ActiveAppointment location) {
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

    public String getAppointmentMode() {
        return appointmentMode;
    }

    public void setAppointmentMode(String appointmentMode) {
        this.appointmentMode = appointmentMode;
    }

    public ActiveAppointment getVirtualService() {
        return virtualService;
    }

    public void setVirtualService(ActiveAppointment virtualService) {
        this.virtualService = virtualService;
    }

    public String getWhatsApp() {
        return WhatsApp;
    }

    public void setWhatsApp(String whatsApp) {
        WhatsApp = whatsApp;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAccessScope() {
        return accessScope;
    }

    public void setAccessScope(String accessScope) {
        this.accessScope = accessScope;
    }

    public ActiveAppointment getJaldeeApptDistanceTime() {
        return jaldeeApptDistanceTime;
    }

    public void setJaldeeApptDistanceTime(ActiveAppointment jaldeeApptDistanceTime) {
        this.jaldeeApptDistanceTime = jaldeeApptDistanceTime;
    }

    public String getPollingTime() {
        return pollingTime;
    }

    public void setPollingTime(String pollingTime) {
        this.pollingTime = pollingTime;
    }

    public ActiveAppointment getJaldeeDistanceTime() {
        return jaldeeDistanceTime;
    }

    public void setJaldeeDistanceTime(ActiveAppointment jaldeeDistanceTime) {
        this.jaldeeDistanceTime = jaldeeDistanceTime;
    }

    public ActiveAppointment getJaldeeDistance() {
        return jaldeeDistance;
    }

    public void setJaldeeDistance(ActiveAppointment jaldeeDistance) {
        this.jaldeeDistance = jaldeeDistance;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public ActiveAppointment getJaldeelTravelTime() {
        return jaldeelTravelTime;
    }

    public void setJaldeelTravelTime(ActiveAppointment jaldeelTravelTime) {
        this.jaldeelTravelTime = jaldeelTravelTime;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }

    public String getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(String travelTime) {
        this.travelTime = travelTime;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public String getJaldeeStartTimeType() {
        return jaldeeStartTimeType;
    }

    public void setJaldeeStartTimeType(String jaldeeStartTimeType) {
        this.jaldeeStartTimeType = jaldeeStartTimeType;
    }

    public String getConsumerStarted() {
        return consumerStarted;
    }

    public void setConsumerStarted(String consumerStarted) {
        this.consumerStarted = consumerStarted;
    }

    public String getCallingStatus() {
        return callingStatus;
    }

    public void setCallingStatus(String callingStatus) {
        this.callingStatus = callingStatus;
    }

    public String getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(String billStatus) {
        this.billStatus = billStatus;
    }

    public String getBillViewStatus() {
        return billViewStatus;
    }

    public void setBillViewStatus(String billViewStatus) {
        this.billViewStatus = billViewStatus;
    }


    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(String amountPaid) {
        this.amountPaid = amountPaid;
    }



    public String getStatusUpdatedTime() {
        return statusUpdatedTime;
    }

    public void setStatusUpdatedTime(String statusUpdatedTime) {
        this.statusUpdatedTime = statusUpdatedTime;
    }

    public String getApptTakenTime() {
        return apptTakenTime;
    }

    public void setApptTakenTime(String apptTakenTime) {
        this.apptTakenTime = apptTakenTime;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }


    public ArrayList<AppointmentCallingModes> getVirtualCallingModes() {
        return virtualCallingModes;
    }

    public void setVirtualCallingModes(ArrayList<AppointmentCallingModes> virtualCallingModes) {
        this.virtualCallingModes = virtualCallingModes;
    }

    @SerializedName("virtualCallingModes")
    private ArrayList<AppointmentCallingModes> virtualCallingModes;

    public ProviderDetails getProvider() {
        return provider;
    }

    public void setProvider(ProviderDetails provider) {
        this.provider = provider;
    }

    @SerializedName("provider")
    private ProviderDetails provider;


    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getConsumerNoteTitle() {
        return consumerNoteTitle;
    }

    public void setConsumerNoteTitle(String consumerNoteTitle) {
        this.consumerNoteTitle = consumerNoteTitle;
    }
}