package com.jaldeeinc.jaldee.response;

import java.io.Serializable;

public class CheckInServiceAvailability implements Serializable {

    private String availableDate;
    private Integer personAhead;
    private String calculationMode;
    private boolean showToken;
    private int id;
    private String queueWaitingTime;
    private String serviceTime;

    public CheckInServiceAvailability() {
    }


    public String getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(String availableDate) {
        this.availableDate = availableDate;
    }

    public Integer getPersonAhead() {
        return personAhead;
    }

    public void setPersonAhead(Integer personAhead) {
        this.personAhead = personAhead;
    }

    public String getCalculationMode() {
        return calculationMode;
    }

    public void setCalculationMode(String calculationMode) {
        this.calculationMode = calculationMode;
    }

    public boolean isShowToken() {
        return showToken;
    }

    public void setShowToken(boolean showToken) {
        this.showToken = showToken;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQueueWaitingTime() {
        return queueWaitingTime;
    }

    public void setQueueWaitingTime(String queueWaitingTime) {
        this.queueWaitingTime = queueWaitingTime;
    }

    public String getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(String serviceTime) {
        this.serviceTime = serviceTime;
    }
}

