package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ListProperties implements Serializable {

    @SerializedName("minAnswerable") // for serviceOptionQNR
    @Expose
    private int minAnswerable;

    @SerializedName("maxAnswerable") // for serviceOptionQNR
    @Expose
    private int maxAnswerable;

    @SerializedName("minAnswers")
    @Expose
    private int minAnswers;

    @SerializedName("maxAnswers")
    @Expose
    private int maxAnswers;

    @SerializedName("mandatory")
    @Expose
    private boolean mandatory;

    @SerializedName("values")
    @Expose
    private ArrayList<String> values;

    @SerializedName("quantity") // for serviceOptionQNR
    @Expose
    private boolean quantity;

    @SerializedName("varPrice") // for serviceOptionQNR
    @Expose
    private boolean varPrice;

    @SerializedName("price") // for serviceOptionQNR
    @Expose
    private String price;
    @SerializedName("basePrice")
    @Expose
    private String basePrice;

    public String getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(String basePrice) {
        this.basePrice = basePrice;
    }
/* @SerializedName("basePrice")
    @Expose
    private Map<String, Object> basePrice;

    public Map<String, Object> getBasePrice() {
        return basePrice;
    }
    public void setBasePrice(Map<String, Object> basePrice) {
        this.basePrice = basePrice;
    }*/
   /* @SerializedName("basePrice")
    @Expose
    private JsonObject basePrice;

    public JsonObject getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(JsonObject basePrice) {
        this.basePrice = basePrice;
    }*/


    public int getMinAnswerable() {
        return minAnswerable;
    }

    public void setMinAnswerable(int minAnswerable) {
        this.minAnswerable = minAnswerable;
    }

    public int getMaxAnswerable() {
        return maxAnswerable;
    }

    public void setMaxAnswerable(int maxAnswerable) {
        this.maxAnswerable = maxAnswerable;
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

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
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

    public ArrayList<String> getValues() {
        return values;
    }

    public void setValues(ArrayList<String> values) {
        this.values = values;
    }
}
