package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeedBack {

    @SerializedName("comments")
    @Expose
    private String comments;

    @SerializedName("date")
    @Expose
    private String date;

    public FeedBack(String comments, String date) {
        this.comments = comments;
        this.date = date;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
