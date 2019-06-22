package com.nv.youneverwait.response;

import com.google.gson.annotations.SerializedName;
import com.nv.youneverwait.model.NextAvailableQModel;

/**
 * Created by sharmila on 25/7/18.
 */

public class QueueList {

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    String message;


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


    public NextAvailableQModel getNextAvailableQueue() {
        return nextAvailableQueue;
    }

    public void setNextAvailableQueue(NextAvailableQModel nextAvailableQueue) {
        this.nextAvailableQueue = nextAvailableQueue;
    }
    @SerializedName("nextAvailableQueue")
    private NextAvailableQModel nextAvailableQueue;


    public int getBranchSpCount() {
        return branchSpCount;
    }

    public void setBranchSpCount(int branchSpCount) {
        this.branchSpCount = branchSpCount;
    }

    int branchSpCount;



}
