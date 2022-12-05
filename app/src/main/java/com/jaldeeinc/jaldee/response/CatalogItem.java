package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CatalogItem implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("catalogId")
    @Expose
    private int catalogId;

    @SerializedName("minQuantity")
    @Expose
    private int minQuantity;

    @SerializedName("maxQuantity")
    @Expose
    private int maxQuantity;

    @SerializedName("item")
    @Expose
    private Item items;

    public CatalogItem() {

    }

    public CatalogItem(int id, int catalogId, Item items) {
        this.id = id;
        this.catalogId = catalogId;
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(int catalogId) {
        this.catalogId = catalogId;
    }

    public Item getItems() {
        return items;
    }

    public void setItems(Item items) {
        this.items = items;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(int minQuantity) {
        this.minQuantity = minQuantity;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }
}
