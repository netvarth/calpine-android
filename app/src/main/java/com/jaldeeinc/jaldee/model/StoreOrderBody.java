package com.jaldeeinc.jaldee.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jaldeeinc.jaldee.response.CatalogTimeSlot;

import java.io.Serializable;
import java.util.ArrayList;

public class StoreOrderBody implements Serializable {


    private boolean storePickup;
    private boolean homeDelivery;
    private String homeDeliveryAddress;

    @SerializedName("catalog")
    @Expose
    private CatalogBody catalogBody;

    @SerializedName("orderFor")
    @Expose
    private OrderForBody orderForBody;

    @SerializedName("timeSlot")
    @Expose
    private CatalogTimeSlot catalogTimeSlot;

    @SerializedName("orderItem")
    @Expose
    private ArrayList<OrderItem> orderItemsList;

    private String orderDate;
    private String countryCode;
    private String phoneNumber;
    private String email;
    private String orderNote;

    public StoreOrderBody(){

    }

    public StoreOrderBody(boolean storePickup, boolean homeDelivery, String homeDeliveryAddress, CatalogBody catalogBody, OrderForBody orderForBody, CatalogTimeSlot catalogTimeSlot, ArrayList<OrderItem> orderItemsList, String orderDate, String countryCode, String phoneNumber, String email) {
        this.storePickup = storePickup;
        this.homeDelivery = homeDelivery;
        this.homeDeliveryAddress = homeDeliveryAddress;
        this.catalogBody = catalogBody;
        this.orderForBody = orderForBody;
        this.catalogTimeSlot = catalogTimeSlot;
        this.orderItemsList = orderItemsList;
        this.orderDate = orderDate;
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public boolean isStorePickup() {
        return storePickup;
    }

    public void setStorePickup(boolean storePickup) {
        this.storePickup = storePickup;
    }

    public CatalogBody getCatalogBody() {
        return catalogBody;
    }

    public void setCatalogBody(CatalogBody catalogBody) {
        this.catalogBody = catalogBody;
    }

    public OrderForBody getOrderForBody() {
        return orderForBody;
    }

    public void setOrderForBody(OrderForBody orderForBody) {
        this.orderForBody = orderForBody;
    }

    public CatalogTimeSlot getCatalogTimeSlot() {
        return catalogTimeSlot;
    }

    public void setCatalogTimeSlot(CatalogTimeSlot catalogTimeSlot) {
        this.catalogTimeSlot = catalogTimeSlot;
    }

    public ArrayList<OrderItem> getOrderItemsList() {
        return orderItemsList;
    }

    public void setOrderItemsList(ArrayList<OrderItem> orderItemsList) {
        this.orderItemsList = orderItemsList;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isHomeDelivery() {
        return homeDelivery;
    }

    public void setHomeDelivery(boolean homeDelivery) {
        this.homeDelivery = homeDelivery;
    }

    public String getHomeDeliveryAddress() {
        return homeDeliveryAddress;
    }

    public void setHomeDeliveryAddress(String homeDeliveryAddress) {
        this.homeDeliveryAddress = homeDeliveryAddress;
    }

    public String getOrderNote() {
        return orderNote;
    }

    public void setOrderNote(String orderNote) {
        this.orderNote = orderNote;
    }
}
