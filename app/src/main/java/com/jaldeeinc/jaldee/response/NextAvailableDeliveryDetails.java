package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class NextAvailableDeliveryDetails implements Serializable {

    @SerializedName("availableDate")
    @Expose
    private String availableDate;

    @SerializedName("timeSlots")
    @Expose
    private ArrayList<CatalogTimeSlot> timeSlots;

    public NextAvailableDeliveryDetails(){

    }

    public NextAvailableDeliveryDetails(String availableDate, ArrayList<CatalogTimeSlot> timeSlots) {
        this.availableDate = availableDate;
        this.timeSlots = timeSlots;
    }

    public String getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(String availableDate) {
        this.availableDate = availableDate;
    }

    public ArrayList<CatalogTimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(ArrayList<CatalogTimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }
}
