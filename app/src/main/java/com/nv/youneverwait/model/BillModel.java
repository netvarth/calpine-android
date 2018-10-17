package com.nv.youneverwait.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sharmila on 1/10/18.
 */

public class BillModel {


    public int getGSTpercentage() {
        return GSTpercentage;
    }

    int GSTpercentage;


    public int getId() {
        return id;
    }

    public double getNetRate() {
        return netRate;
    }

    double netRate;

    public double getDiscountValue() {
        return discountValue;
    }

    public int getCouponValue() {
        return couponValue;
    }

    public double getTotalAmountPaid() {
        return totalAmountPaid;
    }

    double discountValue;
    int couponValue;
    double totalAmountPaid;
    int id;

    public String getGstNumber() {
        return gstNumber;
    }

    String gstNumber;

    public BillModel getCustomer() {
        return customer;
    }

    String serviceName;
    double  price;

    public String getServiceName() {
        return serviceName;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public ArrayList<BillModel> getService() {
        return service;
    }

    int quantity;
    @SerializedName("service")
    private ArrayList<BillModel> service;


    @SerializedName("customer")
    private BillModel customer;

    @SerializedName("userProfile")
    private BillModel userProfile;

    public BillModel getUserProfile() {
        return userProfile;
    }

    public String getFirstName() {
        return firstName;
    }

    String firstName;

    public String getCreatedDate() {
        return createdDate;
    }

    String createdDate;

}
