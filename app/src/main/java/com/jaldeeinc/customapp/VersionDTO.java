package com.jaldeeinc.customapp;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VersionDTO {
    @SerializedName("version")
    @Expose
    private String version;

    @SerializedName("link")
    @Expose
    private String link;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("review")
    @Expose
    private boolean inReview;

    public String getMessage() {
        return message;
    }

    public boolean isInReview() {
        return inReview;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

}
