package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderResponse {

    @SerializedName("account")
    @Expose
    private int accountId;

    @SerializedName("enableOrder")
    @Expose
    private boolean orderEnabled;

    @SerializedName("storeContactInfo")
    @Expose
    private StoreInfo storeInfo;

    public OrderResponse(){

    }

    public OrderResponse(int accountId, boolean orderEnabled, StoreInfo storeInfo) {
        this.accountId = accountId;
        this.orderEnabled = orderEnabled;
        this.storeInfo = storeInfo;
    }


    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public boolean isOrderEnabled() {
        return orderEnabled;
    }

    public void setOrderEnabled(boolean orderEnabled) {
        this.orderEnabled = orderEnabled;
    }

    public StoreInfo getStoreInfo() {
        return storeInfo;
    }

    public void setStoreInfo(StoreInfo storeInfo) {
        this.storeInfo = storeInfo;
    }
}
