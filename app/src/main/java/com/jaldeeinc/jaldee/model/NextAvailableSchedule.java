package com.jaldeeinc.jaldee.model;

import java.io.Serializable;

public class NextAvailableSchedule implements Serializable {

    private LocationModel location;
    private String apptState;
    private String availableDate;
    private boolean batchEnable;
    private boolean futureAppt;
    private long id;
    private String name;
    private boolean openNow;
    private boolean todayAppt;

    public String getApptState() {
        return apptState;
    }

    public void setApptState(String apptState) {
        this.apptState = apptState;
    }

    public String getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(String availableDate) {
        this.availableDate = availableDate;
    }

    public boolean isBatchEnable() {
        return batchEnable;
    }

    public void setBatchEnable(boolean batchEnable) {
        this.batchEnable = batchEnable;
    }

    public boolean isFutureAppt() {
        return futureAppt;
    }

    public void setFutureAppt(boolean futureAppt) {
        this.futureAppt = futureAppt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOpenNow() {
        return openNow;
    }

    public void setOpenNow(boolean openNow) {
        this.openNow = openNow;
    }

    public boolean isTodayAppt() {
        return todayAppt;
    }

    public void setTodayAppt(boolean todayAppt) {
        this.todayAppt = todayAppt;
    }

    public LocationModel getLocation() {
        return location;
    }
    public void setLocation(LocationModel location) {
        this.location = location;
    }


}
