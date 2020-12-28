package com.jaldeeinc.jaldee.model;

import java.io.Serializable;

public class OrderItemBody implements Serializable {

    private int id;
    private int quantity;
    private String consumerNote;

    public OrderItemBody(){

    }

    public OrderItemBody(int id, int quantity, String consumerNote) {
        this.id = id;
        this.quantity = quantity;
        this.consumerNote = consumerNote;
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

    public String getConsumerNote() {
        return consumerNote;
    }

    public void setConsumerNote(String consumerNote) {
        this.consumerNote = consumerNote;
    }
}
