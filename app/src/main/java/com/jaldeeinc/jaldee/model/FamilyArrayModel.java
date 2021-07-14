package com.jaldeeinc.jaldee.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sharmila on 11/7/18.
 */

public class FamilyArrayModel  implements Serializable{

    Integer pincode;
    private JsonObject whatsAppNum;
    private JsonObject telegramNum;
    private String whtsAppCountryCode;
    private String telgrmCountryCode;
    private String whtsAppNumber;
    private String telgrmNumber;
    private boolean isAddMember;
    private String email;

    public boolean isAddMember() {
        return isAddMember;
    }

    public void setAddMember(boolean addMember) {
        isAddMember = addMember;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    ArrayList<String> preferredLanguages;

    public ArrayList<String> getPreferredLanguages() {
        return preferredLanguages;
    }

    public void setPreferredLanguages(ArrayList<String> preferredLanguages) {
        this.preferredLanguages = preferredLanguages;
    }
    public Integer getPincode() {
        return pincode;
    }

    public void setPincode(Integer pincode) {
        this.pincode = pincode;
    }

    /*  public List<FamilyModel> getmArrayuserProfile() {
                return mArrayuserProfile;
            }

            private List<FamilyModel>  mArrayuserProfile;*/
  public FamilyArrayModel getUserProfile() {
      return userProfile;
  }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    String user;

    public void setUserProfile(FamilyArrayModel userProfile) {
        this.userProfile = userProfile;
    }

    @SerializedName("userProfile")
    private FamilyArrayModel userProfile;

    private JsonObject bookingLocation;

    public JsonObject getBookingLocation() {
        return bookingLocation;
    }

    public void setBookingLocation(JsonObject bookingLocation) {
        this.bookingLocation = bookingLocation;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPrimaryMobileNo() {
        return primaryMobileNo;
    }

    public String getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public int getId() {
        return id;
    }

    int id;
    String firstName;

    public void setId(int id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    String lastName;
    String primaryMobileNo;
    String dob;
    String gender;


    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    boolean check=false;
    public String toString() {
        return this.firstName + " " + lastName; // Value to be displayed in the Spinner
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
