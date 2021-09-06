package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ListProperties implements Serializable {

    @SerializedName("minAnswers")
    @Expose
    private int minAnswers;

    @SerializedName("maxAnswers")
    @Expose
    private int maxAnswers;

    @SerializedName("values")
    @Expose
    private ArrayList<String> values;


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

    public ArrayList<String> getValues() {
        return values;
    }

    public void setValues(ArrayList<String> values) {
        this.values = values;
    }
}
