package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ItemDetails implements Serializable {

    @SerializedName("id")
    @Expose
    private int itemId;

    @SerializedName("name")
    @Expose
    private String itemName;

    @SerializedName("quantity")
    @Expose
    private int quantity;

    @SerializedName("price")
    @Expose
    private String price;

    @SerializedName("consumerNote")
    @Expose
    private String consumerNotes;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("totalPrice")
    @Expose
    private String totalPrice;

    @SerializedName("itemImages")
    @Expose
    private ArrayList<ItemImages> itemImagesList;

    public ItemDetails(){

    }

    public ItemDetails(int itemId, String itemName, int quantity, String price, String status, String totalPrice, ArrayList<ItemImages> itemImagesList) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
        this.totalPrice = totalPrice;
        this.itemImagesList = itemImagesList;
    }


    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public ArrayList<ItemImages> getItemImagesList() {
        return itemImagesList;
    }

    public void setItemImagesList(ArrayList<ItemImages> itemImagesList) {
        this.itemImagesList = itemImagesList;
    }

    public String getConsumerNotes() {
        return consumerNotes;
    }

    public void setConsumerNotes(String consumerNotes) {
        this.consumerNotes = consumerNotes;
    }
}
