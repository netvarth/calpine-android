package com.jaldeeinc.jaldee.model;

import java.io.Serializable;

public class OrderItem implements Serializable {

    private int id;
    private int quantity;

    public OrderItem(){

    }

    public OrderItem(int id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
