package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NumberProperties implements Serializable {

    @SerializedName("start")
    @Expose
    private int start;

    @SerializedName("end")
    @Expose
    private int end;

    @SerializedName("minAnswers")
    @Expose
    private int minAnswers;

    @SerializedName("maxAnswers")
    @Expose
    private int maxAnswers;


    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getMinAnswers() {
        return minAnswers;
    }

    public void setMinAnswers(int minAnswers) {
        this.minAnswers = minAnswers;
    }

    public int getMaxAnswers() {
        return maxAnswers;
    }

    public void setMaxAnswers(int maxAnswers) {
        this.maxAnswers = maxAnswers;
    }
}
