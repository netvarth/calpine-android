package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AppointmentSchedule {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    int id;

    public AppointmentSchedule getApptSchedule() {
        return apptSchedule;
    }

    public void setApptSchedule(AppointmentSchedule apptSchedule) {
        this.apptSchedule = apptSchedule;
    }

    @SerializedName("apptSchedule")
    AppointmentSchedule apptSchedule;

    public ArrayList<AppointmentSchedule> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(ArrayList<AppointmentSchedule> timeSlots) {
        this.timeSlots = timeSlots;
    }

    ArrayList <AppointmentSchedule> timeSlots;

    public String getsTime() {
        return sTime;
    }

    public void setsTime(String sTime) {
        this.sTime = sTime;
    }

    String sTime;

    public String geteTime() {
        return eTime;
    }

    public void seteTime(String eTime) {
        this.eTime = eTime;
    }

    String eTime;

}
