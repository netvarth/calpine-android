package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sharmila on 9/7/18.
 */

public class ProfileModel {

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

    String firstName;
    String lastName;
    String primaryMobileNo;
    String email;
    String userName;
    boolean emailVerified,phoneVerified;

}
