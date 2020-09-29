package com.jaldeeinc.jaldee.response;

import com.jaldeeinc.jaldee.model.NextAvailableQModel;
import com.jaldeeinc.jaldee.model.ProviderUserModel;

public class DepServiceInfo {

    String departmentName;
    int departmentId;
    String providerImage;
    int id;
    String Name;
    String estTime;
    int peopleInLine;
    String nextAvailableDate;
    String nextAvailableTime;
    String type;
    boolean isToken;
    String serviceMode;
    String callingMode;
    String calculationMode;
    String minDonationAmount;
    String maxDonationAmount;
    boolean availability = false;
    SearchService checinServiceInfo;
    SearchAppoinment appointmentServiceInfo;
    SearchDonation donationServiceInfo;
    ProviderUserModel providerInfo;

    public DepServiceInfo(){

    }

    public DepServiceInfo(String departmentName, int departmentId, String providerImage, int id, String name, String estTime, int peopleInLine, String nextAvailableDate, String nextAvailableTime, String type, String serviceMode, String callingMode, String calculationMode, String minDonationAmount, String maxDonationAmount, boolean availability, SearchService checinServiceInfo, SearchAppoinment appointmentServiceInfo, SearchDonation donationServiceInfo, ProviderUserModel providerInfo) {
        this.departmentName = departmentName;
        this.departmentId = departmentId;
        this.providerImage = providerImage;
        this.id = id;
        Name = name;
        this.estTime = estTime;
        this.peopleInLine = peopleInLine;
        this.nextAvailableDate = nextAvailableDate;
        this.nextAvailableTime = nextAvailableTime;
        this.type = type;
        this.serviceMode = serviceMode;
        this.callingMode = callingMode;
        this.calculationMode = calculationMode;
        this.minDonationAmount = minDonationAmount;
        this.maxDonationAmount = maxDonationAmount;
        this.availability = availability;
        this.checinServiceInfo = checinServiceInfo;
        this.appointmentServiceInfo = appointmentServiceInfo;
        this.donationServiceInfo = donationServiceInfo;
        this.providerInfo = providerInfo;
    }


    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getProviderImage() {
        return providerImage;
    }

    public void setProviderImage(String providerImage) {
        this.providerImage = providerImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEstTime() {
        return estTime;
    }

    public void setEstTime(String estTime) {
        this.estTime = estTime;
    }

    public int getPeopleInLine() {
        return peopleInLine;
    }

    public void setPeopleInLine(int peopleInLine) {
        this.peopleInLine = peopleInLine;
    }

    public String getNextAvailableDate() {
        return nextAvailableDate;
    }

    public void setNextAvailableDate(String nextAvailableDate) {
        this.nextAvailableDate = nextAvailableDate;
    }

    public String getNextAvailableTime() {
        return nextAvailableTime;
    }

    public void setNextAvailableTime(String nextAvailableTime) {
        this.nextAvailableTime = nextAvailableTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getServiceMode() {
        return serviceMode;
    }

    public void setServiceMode(String serviceMode) {
        this.serviceMode = serviceMode;
    }

    public String getCallingMode() {
        return callingMode;
    }

    public void setCallingMode(String callingMode) {
        this.callingMode = callingMode;
    }

    public String getCalculationMode() {
        return calculationMode;
    }

    public void setCalculationMode(String calculationMode) {
        this.calculationMode = calculationMode;
    }

    public String getMinDonationAmount() {
        return minDonationAmount;
    }

    public void setMinDonationAmount(String minDonationAmount) {
        this.minDonationAmount = minDonationAmount;
    }

    public String getMaxDonationAmount() {
        return maxDonationAmount;
    }

    public void setMaxDonationAmount(String maxDonationAmount) {
        this.maxDonationAmount = maxDonationAmount;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public SearchService getChecinServiceInfo() {
        return checinServiceInfo;
    }

    public void setChecinServiceInfo(SearchService checinServiceInfo) {
        this.checinServiceInfo = checinServiceInfo;
    }

    public SearchAppoinment getAppointmentServiceInfo() {
        return appointmentServiceInfo;
    }

    public void setAppointmentServiceInfo(SearchAppoinment appointmentServiceInfo) {
        this.appointmentServiceInfo = appointmentServiceInfo;
    }

    public SearchDonation getDonationServiceInfo() {
        return donationServiceInfo;
    }

    public void setDonationServiceInfo(SearchDonation donationServiceInfo) {
        this.donationServiceInfo = donationServiceInfo;
    }

    public ProviderUserModel getProviderInfo() {
        return providerInfo;
    }

    public void setProviderInfo(ProviderUserModel providerInfo) {
        this.providerInfo = providerInfo;
    }

    public boolean isToken() {
        return isToken;
    }

    public void setToken(boolean token) {
        isToken = token;
    }
}
