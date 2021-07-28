package com.jaldeeinc.jaldee.response;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sharmila on 9/7/18.
 */

public class ProfileModel {
    private JsonObject whatsAppNum;
    private JsonObject telegramNum;
    private String whtsAppCountryCode;
    private String telgrmCountryCode;
    private String whtsAppNumber;
    private String telgrmNumber;
    private Integer pinCode;
    private String city;
    private String state;

    public String getCity() { return city; }

    public String getState() { return state; }

    public void setCity(String city) { this.city = city; }

    public void setState(String state) { this.state = state; }

    public Integer getPinCode() {
        return pinCode;
    }

    public void setPinCode(Integer pinCode) {
        this.pinCode = pinCode;
    }

    public void setWhtsAppNumber(String whtsAppNumber) {
        this.whtsAppNumber = whtsAppNumber;
    }

    public void setTelgrmNumber(String telgrmNumber) {
        this.telgrmNumber = telgrmNumber;
    }

    public String getWhtsAppNumber() {
        return whtsAppNumber;
    }

    public String getTelgrmNumber() {
        return telgrmNumber;
    }

    public JsonObject getWhatsAppNum() {
        return whatsAppNum;
    }

    public JsonObject getTelegramNum() {
        return telegramNum;
    }

    public String getWhtsAppCountryCode() {
        return whtsAppCountryCode;
    }

    public String getTelgrmCountryCode() {
        return telgrmCountryCode;
    }

    public void setWhatsAppNum(JsonObject whatsAppNum) {
        this.whatsAppNum = whatsAppNum;
    }

    public void setTelegramNum(JsonObject telegramNum) {
        this.telegramNum = telegramNum;
    }

    public void setWhtsAppCountryCode(String whtsAppCountryCode) {
        this.whtsAppCountryCode = whtsAppCountryCode;
    }

    public void setTelgrmCountryCode(String telgrmCountryCode) {
        this.telgrmCountryCode = telgrmCountryCode;
    }


    @SerializedName("userProfile")
    ProfileModel userprofile;

    public ProfileModel getUserprofile() {
        return userprofile;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    String gender,dob;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPrimaryMobileNo() {
        return primaryMobileNo;
    }

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return userName;
    }

    public boolean getEmailVerified() {
        return emailVerified;
    }

    public boolean getPhoneVerified() {
        return phoneVerified;
    }

    public int getId() {
        return id;
    }

    int id;

    public void setUserprofile(ProfileModel userprofile) {
        this.userprofile = userprofile;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPrimaryMobileNo(String primaryMobileNo) {
        this.primaryMobileNo = primaryMobileNo;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    String firstName;
    String lastName;
    String primaryMobileNo;
    String email;
    String userName;
    boolean emailVerified,phoneVerified;
    String countryCode;
    ArrayList<String> preferredLanguages;

    public ArrayList<String> getPreferredLanguages() {
        return preferredLanguages;
    }

    public void setPreferredLanguages(ArrayList<String> preferredLanguages) {
        this.preferredLanguages = preferredLanguages;
    }
}
