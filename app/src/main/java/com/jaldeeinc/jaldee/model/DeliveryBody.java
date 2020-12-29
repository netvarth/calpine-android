package com.jaldeeinc.jaldee.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jaldeeinc.jaldee.response.CatalogTimeSlot;
import com.jaldeeinc.jaldee.response.TimeSlot;

import java.io.Serializable;
import java.util.ArrayList;

public class DeliveryBody implements Serializable {

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
    private TimeSlot timeSlot;

    @SerializedName("orderItem")
    @Expose
    private ArrayList<OrderItem> orderItemsList;

    private String orderDate;
    private String countryCode;
    private String phoneNumber;
    private String email;
    private String orderNote;

    public DeliveryBody(){

    }

    public DeliveryBody(boolean homeDelivery, String homeDeliveryAddress, CatalogBody catalogBody, OrderForBody orderForBody, TimeSlot timeSlot, ArrayList<OrderItem> orderItemsList, String orderDate, String countryCode, String phoneNumber, String email, String orderNote) {
        this.homeDelivery = homeDelivery;
        this.homeDeliveryAddress = homeDeliveryAddress;
        this.catalogBody = catalogBody;
        this.orderForBody = orderForBody;
        this.timeSlot = timeSlot;
        this.orderItemsList = orderItemsList;
        this.orderDate = orderDate;
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.orderNote = orderNote;
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

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
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

    public String getOrderNote() {
        return orderNote;
    }

    public void setOrderNote(String orderNote) {
        this.orderNote = orderNote;
    }
}
