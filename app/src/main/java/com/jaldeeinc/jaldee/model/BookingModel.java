package com.jaldeeinc.jaldee.model;

import com.jaldeeinc.jaldee.response.Questionnaire;
import com.jaldeeinc.jaldee.response.SearchDonation;
import com.jaldeeinc.jaldee.response.SearchService;
import com.jaldeeinc.jaldee.response.SearchTerminology;
import com.jaldeeinc.jaldee.response.ServiceInfo;

import java.io.Serializable;
import java.util.ArrayList;

public class BookingModel implements Serializable {

    public String jsonObject;
    public ArrayList<ShoppingListModel> imagePathList;
    public ArrayList<String> imagesList;
    public String message;
    public int accountId;
    public ServiceInfo serviceInfo;
    public SearchService checkInInfo;
    private SearchDonation donationServiceInfo;
    SearchTerminology mSearchTerminology;
    public int familyEMIID;
    public String phoneNumber;
    public Questionnaire questionnaire;
    public String from;
    public ArrayList<String> questionnaireImages;
    public String providerName;
    public String accountBusinessName;
    public String locationName;
    public String date;
    public String time;
    public String customerName;
    public String emailId;
    public String countryCode;
    public String hint;
    public String peopleWaiting;
    public String checkInOrToken;
    public ArrayList<FamilyArrayModel> multipleFamilyMembers = new ArrayList<>();
    public String totalAmount;
    public String totalServicePay;
    public String donationAmount;
    public String whtsappCountryCode;
    public String whtsappPhoneNumber;
    public double eligibleJcashAmt;
    public float amountRequiredNow;
    public float netTotal;

    public void setImagePathList(ArrayList<ShoppingListModel> imagePathList) {
        this.imagePathList = imagePathList;
    }

    public ArrayList<ShoppingListModel> getImagePathList() {
        return imagePathList;
    }

    public void setWhtsappCountryCode(String whtsappCountryCode) {
        this.whtsappCountryCode = whtsappCountryCode;
    }

    public void setWhtsappPhoneNumber(String whtsappPhoneNumber) {
        this.whtsappPhoneNumber = whtsappPhoneNumber;
    }

    public String getWhtsappCountryCode() {
        return whtsappCountryCode;
    }

    public String getWhtsappPhoneNumber() {
        return whtsappPhoneNumber;
    }

    public void setEligibleJcashAmt(double eligibleJcashAmt) {
        this.eligibleJcashAmt = eligibleJcashAmt;
    }

    public void setAmountRequiredNow(float amountRequiredNow) {
        this.amountRequiredNow = amountRequiredNow;
    }

    public double getEligibleJcashAmt() {
        return eligibleJcashAmt;
    }

    public float getAmountRequiredNow() {
        return amountRequiredNow;
    }

    public void setDonationAmount(String donationAmount) {
        this.donationAmount = donationAmount;
    }

    public String getDonationAmount() {
        return donationAmount;
    }

    public SearchDonation getDonationServiceInfo() {
        return donationServiceInfo;
    }

    public void setDonationServiceInfo(SearchDonation donationServiceInfo) {
        this.donationServiceInfo = donationServiceInfo;
    }

    public ArrayList<String> getImagesList() {
        return imagesList;
    }

    public void setImagesList(ArrayList<String> imagesList) {
        this.imagesList = imagesList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public ServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    public void setServiceInfo(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    public SearchTerminology getmSearchTerminology() {
        return mSearchTerminology;
    }

    public void setmSearchTerminology(SearchTerminology mSearchTerminology) {
        this.mSearchTerminology = mSearchTerminology;
    }

    public int getFamilyEMIID() {
        return familyEMIID;
    }

    public void setFamilyEMIID(int familyEMIID) {
        this.familyEMIID = familyEMIID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(String jsonObject) {
        this.jsonObject = jsonObject;
    }

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public ArrayList<String> getQuestionnaireImages() {
        return questionnaireImages;
    }

    public void setQuestionnaireImages(ArrayList<String> questionnaireImages) {
        this.questionnaireImages = questionnaireImages;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getAccountBusinessName() {
        return accountBusinessName;
    }

    public void setAccountBusinessName(String accountBusinessName) {
        this.accountBusinessName = accountBusinessName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public SearchService getCheckInInfo() {
        return checkInInfo;
    }

    public void setCheckInInfo(SearchService checkInInfo) {
        this.checkInInfo = checkInInfo;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getPeopleWaiting() {
        return peopleWaiting;
    }

    public void setPeopleWaiting(String peopleWaiting) {
        this.peopleWaiting = peopleWaiting;
    }

    public String getCheckInOrToken() {
        return checkInOrToken;
    }

    public void setCheckInOrToken(String checkInOrToken) {
        this.checkInOrToken = checkInOrToken;
    }

    public ArrayList<FamilyArrayModel> getMultipleFamilyMembers() {
        return multipleFamilyMembers;
    }

    public void setMultipleFamilyMembers(ArrayList<FamilyArrayModel> multipleFamilyMembers) {
        this.multipleFamilyMembers = multipleFamilyMembers;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getTotalServicePay() {
        return totalServicePay;
    }

    public void setTotalServicePay(String totalServicePay) {
        this.totalServicePay = totalServicePay;
    }

    public float getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(float netTotal) {
        this.netTotal = netTotal;
    }
}
