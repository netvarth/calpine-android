package com.jaldeeinc.jaldee.response;

/**
 * Created by sharmila on 3/7/18.
 */

public class LoginResponse {

    int id;
    String userName;
    String userType;
    String accStatus;
    String firstName;
    String lastName;
    String primaryPhoneNumber;
    String s3Url;
    String isProvider;
    String checkedInProviders;

    public void setId(int id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setAccStatus(String accStatus) {
        this.accStatus = accStatus;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPrimaryPhoneNumber(String primaryPhoneNumber) {
        this.primaryPhoneNumber = primaryPhoneNumber;
    }

    public void setS3Url(String s3Url) {
        this.s3Url = s3Url;
    }

    public void setIsProvider(String isProvider) {
        this.isProvider = isProvider;
    }

    public boolean isFirstCheckIn() {
        return firstCheckIn;
    }

    public void setFirstCheckIn(boolean firstCheckIn) {
        this.firstCheckIn = firstCheckIn;
    }

    boolean firstCheckIn;

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserType() {
        return userType;
    }

    public String getAccStatus() {
        return accStatus;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPrimaryPhoneNumber() {
        return primaryPhoneNumber;
    }

    public String getS3Url() {
        return s3Url;
    }

    public String getIsProvider() {
        return isProvider;
    }


    public String getCheckedInProviders() {
        return checkedInProviders;
    }

    public void setCheckedInProviders(String checkedInProviders) {
        this.checkedInProviders = checkedInProviders;
    }
}
