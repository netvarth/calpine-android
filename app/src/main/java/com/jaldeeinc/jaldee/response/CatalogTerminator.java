package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CatalogTerminator implements Serializable {


    @SerializedName("endDate")
    @Expose
    private String endDate;

    @SerializedName("noOfOccurance")
    @Expose
    private int noOfOccurance;

    public CatalogTerminator(){

    }

    public CatalogTerminator(String endDate, int noOfOccurance) {
        this.endDate = endDate;
        this.noOfOccurance = noOfOccurance;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getNoOfOccurance() {
        return noOfOccurance;
    }

    public void setNoOfOccurance(int noOfOccurance) {
        this.noOfOccurance = noOfOccurance;
    }
}
