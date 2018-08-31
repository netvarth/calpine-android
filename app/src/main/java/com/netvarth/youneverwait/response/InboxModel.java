package com.netvarth.youneverwait.response;


import com.google.gson.annotations.SerializedName;

/**
 * Created by sharmila on 14/8/18.
 */

public class InboxModel  {
    public String getMsg() {
        return msg;
    }

    String msg;

    public long getTimeStamp() {
        return timeStamp;
    }

    long timeStamp;

    public InboxModel getOwner() {
        return owner;
    }

    @SerializedName("owner")
    private InboxModel owner;

    public String getUserName() {
        return userName;
    }

    String userName;

    public int getId() {
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
}
