package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AvailableSlotsData implements Serializable {

    @SerializedName("time")
    @Expose
    private String slotTime;

    @SerializedName("noOfAvailbleSlots")
    @Expose
    private int noOfAvailableSlots;

    @SerializedName("active")
    @Expose
    private boolean isActive;

    @SerializedName("capacity")
    @Expose
    private int capacity;

    private int scheduleId;
    private String displayTime;
    private int position;


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getSlotTime() {
        return slotTime;
    }

    public void setSlotTime(String slotTime) {
        this.slotTime = slotTime;
    }

    public int getNoOfAvailableSlots() {
        return noOfAvailableSlots;
    }

    public void setNoOfAvailableSlots(int noOfAvailableSlots) {
        this.noOfAvailableSlots = noOfAvailableSlots;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(String displayTime) {
        this.displayTime = displayTime;
    }
}
