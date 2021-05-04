package com.jaldeeinc.jaldee.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserMessage {

    private int id;
    private String message;
    private String userName;
    private String senderName;
    private String timeStamp;
    @SerializedName("attachements")
    public List<FileAttachment> attachments;
    private String messageType;

    public UserMessage(){

    }

    public UserMessage(int id, String message, String userName, String senderName) {
        this.id = id;
        this.message = message;
        this.userName = userName;
        this.senderName = senderName;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public List<FileAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<FileAttachment> attachments) {
        this.attachments = attachments;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
