package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StoreInfo {

    @SerializedName("firstName")
    @Expose
    private String firstName;

    @SerializedName("lastName")
    @Expose
    private String lastName;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("alternatePhone")
    @Expose
    private String alternatePhone;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("primCountryCode")
    @Expose
    private String primaryCountryCode;

    @SerializedName("secCountryCode")
    @Expose
    private String secondaryCountryCode;

    @SerializedName("whatsAppCountryCode")
    @Expose
    private String whatsAppCountryCode;

    public StoreInfo(){

    }

    public StoreInfo(String firstName, String lastName, String phone, String alternatePhone, String email, String address, String primaryCountryCode, String secondaryCountryCode, String whatsAppCountryCode) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.alternatePhone = alternatePhone;
        this.email = email;
        this.address = address;
        this.primaryCountryCode = primaryCountryCode;
        this.secondaryCountryCode = secondaryCountryCode;
        this.whatsAppCountryCode = whatsAppCountryCode;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAlternatePhone() {
        return alternatePhone;
    }

    public void setAlternatePhone(String alternatePhone) {
        this.alternatePhone = alternatePhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrimaryCountryCode() {
        return primaryCountryCode;
    }

    public void setPrimaryCountryCode(String primaryCountryCode) {
        this.primaryCountryCode = primaryCountryCode;
    }

    public String getSecondaryCountryCode() {
        return secondaryCountryCode;
    }

    public void setSecondaryCountryCode(String secondaryCountryCode) {
        this.secondaryCountryCode = secondaryCountryCode;
    }

    public String getWhatsAppCountryCode() {
        return whatsAppCountryCode;
    }

    public void setWhatsAppCountryCode(String whatsAppCountryCode) {
        this.whatsAppCountryCode = whatsAppCountryCode;
    }
}
