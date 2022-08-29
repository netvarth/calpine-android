package com.jaldeeinc.jaldee.model;

import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;

public class ProviderConsumerFamilyMemberModel implements Serializable {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String phoneNo;
    private String status;
    private boolean favourite;
    private boolean phone_verified;
    private boolean email_verified;
    private int jaldeeConsumer;
    private String countryCode;
    private int jaldeeId;
    private JsonObject bookingLocation;
    private ArrayList<String> preferredLanguages;
    private JsonObject age;
    private JsonObject whatsAppNum;
    private JsonObject telegramNum;
    private int totalCheckedInCount;
    private boolean isSignUpCustomer;
    private JaldeeConsumerDetails jaldeeConsumerDetails;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public int getJaldeeConsumer() {
        return jaldeeConsumer;
    }

    public void setJaldeeConsumer(int jaldeeConsumer) {
        this.jaldeeConsumer = jaldeeConsumer;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public int getJaldeeId() {
        return jaldeeId;
    }

    public void setJaldeeId(int jaldeeId) {
        this.jaldeeId = jaldeeId;
    }

    public JsonObject getBookingLocation() {
        return bookingLocation;
    }

    public void setBookingLocation(JsonObject bookingLocation) {
        this.bookingLocation = bookingLocation;
    }

    public ArrayList<String> getPreferredLanguages() {
        return preferredLanguages;
    }

    public void setPreferredLanguages(ArrayList<String> preferredLanguages) {
        this.preferredLanguages = preferredLanguages;
    }

    public JsonObject getAge() {
        return age;
    }

    public void setAge(JsonObject age) {
        this.age = age;
    }

    public JsonObject getWhatsAppNum() {
        return whatsAppNum;
    }

    public void setWhatsAppNum(JsonObject whatsAppNum) {
        this.whatsAppNum = whatsAppNum;
    }

    public JsonObject getTelegramNum() {
        return telegramNum;
    }

    public void setTelegramNum(JsonObject telegramNum) {
        this.telegramNum = telegramNum;
    }

    public int getTotalCheckedInCount() {
        return totalCheckedInCount;
    }

    public void setTotalCheckedInCount(int totalCheckedInCount) {
        this.totalCheckedInCount = totalCheckedInCount;
    }

    public boolean isSignUpCustomer() {
        return isSignUpCustomer;
    }

    public void setSignUpCustomer(boolean signUpCustomer) {
        isSignUpCustomer = signUpCustomer;
    }

    public JaldeeConsumerDetails getJaldeeConsumerDetails() {
        return jaldeeConsumerDetails;
    }

    public void setJaldeeConsumerDetails(JaldeeConsumerDetails jaldeeConsumerDetails) {
        this.jaldeeConsumerDetails = jaldeeConsumerDetails;
    }
}
