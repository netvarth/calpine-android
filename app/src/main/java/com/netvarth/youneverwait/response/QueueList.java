package com.netvarth.youneverwait.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sharmila on 25/7/18.
 */

public class QueueList {


    int queueWaitingTime;

    public int getQueueWaitingTime() {
        return queueWaitingTime;
    }

    public void setQueueWaitingTime(int queueWaitingTime) {
        this.queueWaitingTime = queueWaitingTime;
    }

    public String getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(String serviceTime) {
        this.serviceTime = serviceTime;
    }

    String serviceTime;


    public QueueList getProvider() {
        return provider;
    }

    public void setProvider(QueueList provider) {
        this.provider = provider;
    }

    @SerializedName("provider")
    private QueueList provider;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;


    public QueueList getNextAvailableQueue() {
        return nextAvailableQueue;
    }

    public void setNextAvailableQueue(QueueList nextAvailableQueue) {
        this.nextAvailableQueue = nextAvailableQueue;
    }

    private QueueList nextAvailableQueue;

    public QueueList getLocation() {
        return location;
    }

    public void setLocation(QueueList location) {
        this.location = location;
    }

    private QueueList location;

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

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    int queueSize;
}
