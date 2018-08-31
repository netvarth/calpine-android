package com.netvarth.youneverwait.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sharmila on 11/7/18.
 */

public class FamilyModel {

    public FamilyModel getUserProfile() {
        return userProfile;
    }


    @SerializedName("userProfile")
    private FamilyModel userProfile;

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

    String firstName;
    String lastName;
    String primaryMobileNo;
    String dob;
    String gender;

}
