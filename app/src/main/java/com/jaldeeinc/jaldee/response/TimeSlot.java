package com.jaldeeinc.jaldee.response;

import java.io.Serializable;

public class TimeSlot implements Serializable {

    private String sTime;
    private String eTime;

    public TimeSlot(){

    }

    public TimeSlot(String sTime, String eTime) {
        this.sTime = sTime;
        this.eTime = eTime;
    }

    public String getsTime() {
        return sTime;
    }

    public void setsTime(String sTime) {
        this.sTime = sTime;
    }

    public String geteTime() {
        return eTime;
    }

    public void seteTime(String eTime) {
        this.eTime = eTime;
    }
}
