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

    @SerializedName("mandatory")
    @Expose
    private boolean mandatory;

    @SerializedName("quantity") // for serviceOptionQNR
    @Expose
    private boolean quantity;

    @SerializedName("varPrice") // for serviceOptionQNR
    @Expose
    private boolean varPrice;

    @SerializedName("price") // for serviceOptionQNR
    @Expose
    private String price;

    @SerializedName("minAnswers")
    @Expose
    private int minAnswers;

    @SerializedName("maxAnswers")
    @Expose
    private int maxAnswers;

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isQuantity() {
        return quantity;
    }

    public void setQuantity(boolean quantity) {
        this.quantity = quantity;
    }

    public boolean isVarPrice() {
        return varPrice;
    }

    public void setVarPrice(boolean varPrice) {
        this.varPrice = varPrice;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

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
