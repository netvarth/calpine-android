package com.jaldeeinc.jaldee.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class PincodeLocationsResponse implements Serializable {
    boolean isCheck;
    String Name;
    String State;
    Integer Pincode;
    @SerializedName("PostOffice")
    ArrayList<PincodeLocationsResponse> PostOffice;

    public ArrayList<PincodeLocationsResponse> getPostOffice() {
        return PostOffice;
    }

    public void setPostOffice(ArrayList<PincodeLocationsResponse> postOffice) {
        PostOffice = postOffice;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public String getName() {
        return Name;
    }

    public String getState() {
        return State;
    }

    public Integer getPincode() {
        return Pincode;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setState(String state) {
        State = state;
    }

    public void setPincode(Integer pincode) {
        Pincode = pincode;
    }
}
