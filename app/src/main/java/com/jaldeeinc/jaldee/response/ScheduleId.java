package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ScheduleId {

    @SerializedName("availableSlots")
    ArrayList<ScheduleId> availableSlots;

    String time;
    String noOfAvailbleSlots;
    String active;

    public ArrayList<ScheduleId> getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(ArrayList<ScheduleId> availableSlots) {
        this.availableSlots = availableSlots;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNoOfAvailbleSlots() {
        return noOfAvailbleSlots;
    }

    public void setNoOfAvailbleSlots(String noOfAvailbleSlots) {
        this.noOfAvailbleSlots = noOfAvailbleSlots;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    String scheduleName;

    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }
}
