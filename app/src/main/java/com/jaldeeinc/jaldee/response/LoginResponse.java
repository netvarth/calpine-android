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



}
