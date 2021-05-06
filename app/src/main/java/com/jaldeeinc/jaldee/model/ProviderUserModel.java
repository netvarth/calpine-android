package com.jaldeeinc.jaldee.model;

import com.jaldeeinc.jaldee.response.ProviderPicture;

import java.io.Serializable;

/**
 * Created by Mani on 20/7/2020.
 */

public class ProviderUserModel implements Serializable {
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

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    String firstName;
    String lastName;
    String mobileNo;
    int deptId;
    String email;
    int id;
    String businessName;
    ProviderPicture profilePicture;
    boolean queues;
    boolean schedules;
    boolean available;

    @Override
    public String toString() {
        return this.businessName == null ? this.firstName + " " + this.lastName : this.businessName; // Value to be displayed in the Spinner
    }


    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }



    public boolean isQueues() {
        return queues;
    }

    public void setQueues(boolean queues) {
        this.queues = queues;
    }

    public boolean isSchedules() {
        return schedules;
    }

    public void setSchedules(boolean schedules) {
        this.schedules = schedules;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public ProviderPicture getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(ProviderPicture profilePicture) {
        this.profilePicture = profilePicture;
    }
}
