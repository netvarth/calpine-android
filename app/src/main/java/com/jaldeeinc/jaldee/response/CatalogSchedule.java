package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class CatalogSchedule implements Serializable {

    @SerializedName("Weekly")
    @Expose
    private String Weekly;

    @SerializedName("startDate")
    @Expose
    private String startDate;

    @SerializedName("terminator")
    @Expose
    private CatalogTerminator catalogTerminator;

    @SerializedName("timeSlots")
    @Expose
    private ArrayList<CatalogTimeSlot> catLogTimeSlotsList;

    public CatalogSchedule(){

    }


    public CatalogSchedule(String weekly, String startDate, CatalogTerminator catLogTerminator, ArrayList<CatalogTimeSlot> catLogTimeSlotsList) {
        Weekly = weekly;
        this.startDate = startDate;
        this.catalogTerminator = catLogTerminator;
        this.catLogTimeSlotsList = catLogTimeSlotsList;
    }

    public String getWeekly() {
        return Weekly;
    }

    public void setWeekly(String weekly) {
        Weekly = weekly;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public CatalogTerminator getCatLogTerminator() {
        return catalogTerminator;
    }

    public void setCatLogTerminator(CatalogTerminator catLogTerminator) {
        this.catalogTerminator = catLogTerminator;
    }

    public ArrayList<CatalogTimeSlot> getCatLogTimeSlotsList() {
        return catLogTimeSlotsList;
    }

    public void setCatLogTimeSlotsList(ArrayList<CatalogTimeSlot> catLogTimeSlotsList) {
        this.catLogTimeSlotsList = catLogTimeSlotsList;
    }
}
