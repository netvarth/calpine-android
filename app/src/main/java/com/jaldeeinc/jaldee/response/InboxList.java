package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jaldeeinc.jaldee.model.FileAttachment;

import java.io.Serializable;
import java.util.List;

public class InboxList implements Serializable {

    @SerializedName("owner")
    @Expose
    private UserModel owner;
    private String msg;
    private String timeStamp;
    private String service;

    @SerializedName("receiver")
    @Expose
    private UserModel receiver;

    private String waitlistId;

    private String accountId;
    private String accountName;

    private String messageId;

    private boolean read;



    @SerializedName("attachements")
    public List<FileAttachment> attachments;

    public InboxList() {

    }

    public InboxList(UserModel owner, String msg, String timeStamp, String service, UserModel receiver, String waitlistId, String accountId, String messageId, boolean read) {
        this.owner = owner;
        this.msg = msg;
        this.timeStamp = timeStamp;
        this.service = service;
        this.receiver = receiver;
        this.waitlistId = waitlistId;
        this.accountId = accountId;
        this.messageId = messageId;
        this.read = read;
    }

    public UserModel getOwner() {
        return owner;
    }

    public void setOwner(UserModel owner) {
        this.owner = owner;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public UserModel getReceiver() {
        return receiver;
    }

    public void setReceiver(UserModel receiver) {
        this.receiver = receiver;
    }

    public String getWaitlistId() {
        return waitlistId;
    }

    public void setWaitlistId(String waitlistId) {
        this.waitlistId = waitlistId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public List<FileAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<FileAttachment> attachments) {
        this.attachments = attachments;
    }
}
