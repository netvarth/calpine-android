package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PostInfo implements Serializable {

    @SerializedName("postInfoEnabled")
    @Expose
    private boolean postInfoEnabled;

    @SerializedName("postInfoTitle")
    @Expose
    private String postInfoTitle;

    @SerializedName("postInfoText")
    @Expose
    private String postInfoText;

    public PostInfo(){

    }

    public PostInfo(boolean postInfoEnabled, String postInfoTitle, String postInfoText) {
        this.postInfoEnabled = postInfoEnabled;
        this.postInfoTitle = postInfoTitle;
        this.postInfoText = postInfoText;
    }

    public boolean isPostInfoEnabled() {
        return postInfoEnabled;
    }

    public void setPostInfoEnabled(boolean postInfoEnabled) {
        this.postInfoEnabled = postInfoEnabled;
    }

    public String getPostInfoTitle() {
        return postInfoTitle;
    }

    public void setPostInfoTitle(String postInfoTitle) {
        this.postInfoTitle = postInfoTitle;
    }

    public String getPostInfoText() {
        return postInfoText;
    }

    public void setPostInfoText(String postInfoText) {
        this.postInfoText = postInfoText;
    }
}
