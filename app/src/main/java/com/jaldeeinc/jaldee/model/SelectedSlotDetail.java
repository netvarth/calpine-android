package com.jaldeeinc.jaldee.model;

public class SelectedSlotDetail {
    String displayTime;
    String slotTime;
    int scheduleId;
    int position;
    String date;
    String calendarDate;

    public String getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(String displayTime) {
        this.displayTime = displayTime;
    }

    public String getSlotTime() {
        return slotTime;
    }

    public void setSlotTime(String slotTime) {
        this.slotTime = slotTime;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCalendarDate() {
        return calendarDate;
    }

    public void setCalendarDate(String calendarDate) {
        this.calendarDate = calendarDate;
    }
}
