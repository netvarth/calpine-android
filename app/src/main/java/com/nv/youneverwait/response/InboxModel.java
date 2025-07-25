package com.nv.youneverwait.response;


import com.google.gson.annotations.SerializedName;

/**
 * Created by sharmila on 14/8/18.
 */

public class InboxModel {

    public String getAccountId() {
        return accountId;
    }

    String accountId;
    public String getMsg() {
        return msg;
    }


    public int getLineCount() {
        return lineCount;
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }

    int lineCount=0;
    public String getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
    }

    String messageStatus;
    String msg;

    public String getWaitlistId() {
        return waitlistId;
    }

    public void setWaitlistId(String waitlistId) {
        this.waitlistId = waitlistId;
    }

    String waitlistId;

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    String uniqueID;

    public long getTimeStamp() {
        return timeStamp;
    }

    long timeStamp;

    public InboxModel getOwner() {
        return owner;
    }

    @SerializedName("owner")
    private InboxModel owner;

    public InboxModel getReceiver() {
        return receiver;
    }

    public void setReceiver(InboxModel receiver) {
        this.receiver = receiver;
    }

    @SerializedName("receiver")
    private InboxModel receiver;

    public String getUserName() {
        return userName;
    }
    String userName;

    String RuserName;


    public String getReceiverName() {
        return userName;
    }

    public void setRecevierName(String userName) {
        this.userName = userName;
    }


    public int getId() {
        return id;
    }

    int Rid;

    public void setReceiverId(int id) {
        this.id = id;
    }


    public int getReceiverId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    int id;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setOwner(InboxModel owner) {
        this.owner = owner;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    String service;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    String key;

    public boolean isIs_see() {
        return is_see;
    }

    public void setIs_see(boolean is_see) {
        this.is_see = is_see;
    }

    boolean is_see;
}
