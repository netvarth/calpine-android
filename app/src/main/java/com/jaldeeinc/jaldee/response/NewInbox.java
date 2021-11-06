package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NewInbox implements Serializable {

    @SerializedName("msg")
    @Expose
    private String latestMessage;

    @SerializedName("messageId")
    @Expose
    private int messageId;

    @SerializedName("timeStamp")
    @Expose
    private long timeStamp;

    @SerializedName("waitlistId")
    @Expose
    private String waitlistId;

    @SerializedName("accountId")
    @Expose
    private int accountId;

    @SerializedName("accountName")
    @Expose
    private String accountName;

    @SerializedName("unReadCount")
    @Expose
    private int unReadCount;


    public String getLatestMessage() {
        return latestMessage;
    }

    public void setLatestMessage(String latestMessage) {
        this.latestMessage = latestMessage;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getWaitlistId() {
        return waitlistId;
    }

    public void setWaitlistId(String waitlistId) {
        this.waitlistId = waitlistId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public int getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }
}
