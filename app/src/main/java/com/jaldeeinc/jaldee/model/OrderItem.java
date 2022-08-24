package com.jaldeeinc.jaldee.model;

import java.io.Serializable;

public class OrderItem implements Serializable {

    private int id;
    private int quantity;
    private String consumerNote;
    private String itemType;
    private String serviceOptionInput;
    private String serviceOptionInputImages;

    public OrderItem(){

    }

    public OrderItem(int id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public OrderItem(int id, int quantity, String consumerNote, String itemType, String serviceOptionInput, String serviceOptionInputImages) {
        this.id = id;
        this.quantity = quantity;
        this.consumerNote = consumerNote;
        this.itemType = itemType;
        this.serviceOptionInput = serviceOptionInput;
        this.serviceOptionInputImages = serviceOptionInputImages;
    }

    public String getServiceOptionInput() {
        return serviceOptionInput;
    }

    public void setServiceOptionInput(String serviceOptionInput) {
        this.serviceOptionInput = serviceOptionInput;
    }

    public String getServiceOptionInputImages() {
        return serviceOptionInputImages;
    }

    public void setServiceOptionInputImages(String serviceOptionInputImages) {
        this.serviceOptionInputImages = serviceOptionInputImages;
    }

    public String getItemType() { return itemType; }

    public void setItemType(String itemType) { this.itemType = itemType; }

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
