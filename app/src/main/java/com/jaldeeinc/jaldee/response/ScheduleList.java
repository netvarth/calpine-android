package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.SerializedName;
import com.jaldeeinc.jaldee.model.NextAvailableQModel;
import com.jaldeeinc.jaldee.model.NextAvailableSchedule;

import java.io.Serializable;

public class ScheduleList implements Serializable {
    public boolean isCheckinAllowed() {
        return isCheckinAllowed;
    }

    public void setCheckinAllowed(boolean checkinAllowed) {
        isCheckinAllowed = checkinAllowed;
    }

    boolean isCheckinAllowed;

    public NextAvailableSchedule getAvailableSchedule() {
        return availableSchedule;
    }

    public void setAvailableSchedule(NextAvailableSchedule availableSchedule) {
        this.availableSchedule = availableSchedule;
    }

    @SerializedName("availableSchedule")
    private NextAvailableSchedule availableSchedule;

    public String getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(String availableDate) {
        this.availableDate = availableDate;
    }

    String availableDate;

    public boolean isOpenNow() {
        return openNow;
    }

    public void setOpenNow(boolean openNow) {
        this.openNow = openNow;
    }

    boolean openNow;

    public ScheduleList getProvider() {
        return provider;
    }

    public void setProvider(ScheduleList provider) {
        this.provider = provider;
    }

    @SerializedName("provider")
    private ScheduleList provider;

    public SlotsData getSlotsData() {
        return slotsData;
    }

    public void setSlotsData(SlotsData slotsData) {
        this.slotsData = slotsData;
    }

    @SerializedName("availableSlots")
    private SlotsData slotsData;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;

    public int getBranchSpCount() {
        return branchSpCount;
    }

    public void setBranchSpCount(int branchSpCount) {
        this.branchSpCount = branchSpCount;
    }

    int branchSpCount;
}
