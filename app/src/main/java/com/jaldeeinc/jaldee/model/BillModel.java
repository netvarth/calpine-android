package com.jaldeeinc.jaldee.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by sharmila on 1/10/18.
 */

public class BillModel {

    public Jdn getJdn() {
        return jdn;
    }

    public void setJdn(Jdn jdn) {
        this.jdn = jdn;
    }

    Jdn jdn;


    public BillDisplayNotes getDisplayNotes() {
        return displayNotes;
    }

    public void setDisplayNotes(BillDisplayNotes displayNotes) {
        this.displayNotes = displayNotes;
    }

    BillDisplayNotes displayNotes;

    public double getTaxableTotal() {
        return taxableTotal;
    }

    public double getTotalTaxAmount() {
        return totalTaxAmount;
    }

    public double getTaxPercentage() {
        return taxPercentage;
    }

    double taxableTotal;
    double totalTaxAmount;
    double taxPercentage;



    public double getNetTotal() {
        return netTotal;
    }

    double netTotal;
    public ArrayList<BillModel> getProviderCoupon() {
        return providerCoupon;
    }

    ArrayList<BillModel>providerCoupon;

    public String getName() {
        return name;
    }

    String name;
    public int getGSTpercentage() {
        return GSTpercentage;
    }

    int GSTpercentage;

    public String getCouponName() {
        return couponName;
    }

    String couponName;

    public String getDiscountName() {
        return discountName;
    }

    String discountName;

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

    public double getCouponValue() {
        return couponValue;
    }

    public double getTotalAmountPaid() {
        return totalAmountPaid;
    }

    public double getDiscValue() {
        return discValue;
    }

    double discValue;
    double discountValue;
    double couponValue;
    double totalAmountPaid;
    int id;

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    String billId;

    public String getItemName() {
        return itemName;
    }

    String  itemName;
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

    public ArrayList<BillModel> getItems() {
        return items;
    }

    public ArrayList<BillModel> getDiscount() {
        return discount;
    }



    private ArrayList<BillModel>  discount;


    private ArrayList<BillModel>  items;

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


    @SerializedName("jCoupon")
    private Map<String, JsonObject> jCoupon;

    public Map<String, JsonObject> getJCoupon() {
        return jCoupon;
    }



}
