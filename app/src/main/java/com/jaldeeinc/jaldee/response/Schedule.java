package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Schedule {

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("isAvailable")
    @Expose
    private boolean isAvailable;

    @SerializedName("reason")
    @Expose
    private String reason;

    @SerializedName("timeSlots")
    @Expose
    private ArrayList<CatalogTimeSlot> catalogTimeSlotList;

    public Schedule(){

    }

    public Schedule(String date, boolean isAvailable, String reason, ArrayList<CatalogTimeSlot> catalogTimeSlotList) {
        this.date = date;
        this.isAvailable = isAvailable;
        this.reason = reason;
        this.catalogTimeSlotList = catalogTimeSlotList;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ArrayList<CatalogTimeSlot> getCatalogTimeSlotList() {
        return catalogTimeSlotList;
    }

    public void setCatalogTimeSlotList(ArrayList<CatalogTimeSlot> catalogTimeSlotList) {
        this.catalogTimeSlotList = catalogTimeSlotList;
    }
}
