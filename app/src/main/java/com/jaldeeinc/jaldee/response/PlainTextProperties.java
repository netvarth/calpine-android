package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlainTextProperties {

    @SerializedName("minNoOfLetter")
    @Expose
    private int minNoOfLetter;

    @SerializedName("maxNoOfLetter")
    @Expose
    private int maxNoOfLetter;

    public int getMinNoOfLetter() {
        return minNoOfLetter;
    }

    public void setMinNoOfLetter(int minNoOfLetter) {
        this.minNoOfLetter = minNoOfLetter;
    }

    public int getMaxNoOfLetter() {
        return maxNoOfLetter;
    }

    public void setMaxNoOfLetter(int maxNoOfLetter) {
        this.maxNoOfLetter = maxNoOfLetter;
    }
}
