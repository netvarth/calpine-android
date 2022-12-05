package com.jaldeeinc.jaldee.model;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class GridColumnAnswerLine implements Serializable {

    public String columnId;
    public JsonObject column;
    public float price;
    public int quantity;

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public JsonObject getColumn() {
        return column;
    }

    public void setColumn(JsonObject column) {
        this.column = column;
    }
}
