package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SlotsData {

    @SerializedName("availableSlots")
    @Expose
    private ArrayList<AvailableSlotsData> availableSlots;

    @SerializedName("unavailableSlots")
    @Expose
    private ArrayList<AvailableSlotsData> unAvailableSlots;

    @SerializedName("scheduleName")
    @Expose
    private String scheduleName;

    @SerializedName("scheduleId")
    @Expose
    private int scheduleId;

    @SerializedName("date")
    @Expose
    private String date;

    public SlotsData(){

    }

    public SlotsData(ArrayList<AvailableSlotsData> availableSlots, ArrayList<AvailableSlotsData> unAvailableSlots, String scheduleName, int scheduleId, String date) {
        this.availableSlots = availableSlots;
        this.unAvailableSlots = unAvailableSlots;
        this.scheduleName = scheduleName;
        this.scheduleId = scheduleId;
        this.date = date;
    }

    public ArrayList<AvailableSlotsData> getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(ArrayList<AvailableSlotsData> availableSlots) {
        this.availableSlots = availableSlots;
    }

    public ArrayList<AvailableSlotsData> getUnAvailableSlots() {
        return unAvailableSlots;
    }

    public void setUnAvailableSlots(ArrayList<AvailableSlotsData> unAvailableSlots) {
        this.unAvailableSlots = unAvailableSlots;
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
