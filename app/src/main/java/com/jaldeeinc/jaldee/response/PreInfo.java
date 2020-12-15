package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PreInfo implements Serializable {

    @SerializedName("preInfoEnabled")
    @Expose
    private boolean preInfoEnabled;

    @SerializedName("preInfoTitle")
    @Expose
    private String preInfoTitle;

    @SerializedName("preInfoText")
    @Expose
    private String preInfoText;

    public PreInfo(){

    }

    public PreInfo(boolean preInfoEnabled, String preInfoTitle, String preInfoText) {
        this.preInfoEnabled = preInfoEnabled;
        this.preInfoTitle = preInfoTitle;
        this.preInfoText = preInfoText;
    }

    public boolean isPreInfoEnabled() {
        return preInfoEnabled;
    }

    public void setPreInfoEnabled(boolean preInfoEnabled) {
        this.preInfoEnabled = preInfoEnabled;
    }

    public String getPreInfoTitle() {
        return preInfoTitle;
    }

    public void setPreInfoTitle(String preInfoTitle) {
        this.preInfoTitle = preInfoTitle;
    }

    public String getPreInfoText() {
        return preInfoText;
    }

    public void setPreInfoText(String preInfoText) {
        this.preInfoText = preInfoText;
    }
}
