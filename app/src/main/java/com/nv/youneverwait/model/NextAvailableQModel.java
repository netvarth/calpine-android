package com.nv.youneverwait.model;

public class NextAvailableQModel {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
    private String name;
    private LocationModel location;
    private int parallelServing;
    private int queueWaitingTime;
    private int queueSize;
    private int delay;
    private int personAhead;
    private int turnAroundTime;
    private String availableDate;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocationModel getLocation() {
        return location;
    }

    public void setLocation(LocationModel location) {
        this.location = location;
    }

    public int getParallelServing() {
        return parallelServing;
    }

    public void setParallelServing(int parallelServing) {
        this.parallelServing = parallelServing;
    }

    public int getQueueWaitingTime() {
        return queueWaitingTime;
    }

    public void setQueueWaitingTime(int queueWaitingTime) {
        this.queueWaitingTime = queueWaitingTime;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getPersonAhead() {
        return personAhead;
    }

    public void setPersonAhead(int personAhead) {
        this.personAhead = personAhead;
    }

    public int getTurnAroundTime() {
        return turnAroundTime;
    }

    public void setTurnAroundTime(int turnAroundTime) {
        this.turnAroundTime = turnAroundTime;
    }

    public String getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(String availableDate) {
        this.availableDate = availableDate;
    }

    public String getCalculationMode() {
        return calculationMode;
    }

    public void setCalculationMode(String calculationMode) {
        this.calculationMode = calculationMode;
    }

    public boolean isOnlineCheckIn() {
        return onlineCheckIn;
    }

    public void setOnlineCheckIn(boolean onlineCheckIn) {
        this.onlineCheckIn = onlineCheckIn;
    }

    public boolean isFutureWaitlist() {
        return futureWaitlist;
    }

    public void setFutureWaitlist(boolean futureWaitlist) {
        this.futureWaitlist = futureWaitlist;
    }

    public boolean isOpenNow() {
        return openNow;
    }

    public void setOpenNow(boolean openNow) {
        this.openNow = openNow;
    }

    public boolean isAvailableToday() {
        return isAvailableToday;
    }

    public void setAvailableToday(boolean availableToday) {
        isAvailableToday = availableToday;
    }

    public boolean isShowToken() {
        return showToken;
    }

    public void setShowToken(boolean showToken) {
        this.showToken = showToken;
    }

    private String calculationMode;
    private boolean onlineCheckIn;
    private boolean futureWaitlist;
    private boolean openNow;
    private boolean isAvailableToday;
    private boolean showToken;





}
