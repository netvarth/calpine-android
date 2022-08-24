package com.jaldeeinc.jaldee.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.jaldeeinc.jaldee.response.AccountProfile;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by sharmila on 1/10/18.
 */

public class BillModel {
    @SerializedName("serviceOptions")
    public ArrayList<BillModel> serviceOptions;

    @SerializedName("providerCoupon")
    private Map<String, JsonObject> providerCoupon;

    @SerializedName("service")
    private ArrayList<BillModel> service;

    @SerializedName("customer")
    private BillModel customer;

    @SerializedName("userProfile")
    private BillModel userProfile;

    @SerializedName("jCoupon")
    private Map<String, JsonObject> jCoupon;

    @SerializedName("accountProfile")
    private AccountProfile accountProfile;

    public Jdn jdn;
    public BillDisplayNotes displayNotes;
    public ArrayList<BillModel> items;
    public ArrayList<BillModel> discount;
    public String name;
    public String billId;
    public String itemName;
    public String firstName;
    public String gstNumber;
    public String couponName;
    public String displayNote;
    public String serviceName;
    public String createdDate;
    public String discountName;
    public String serviceOptionName;
    public int id;
    public int quantity;
    public int GSTpercentage;
    public int deliveryCharges;
    public long serviceOptionId;
    public float totalPrice;
    public double price;
    public double netRate;
    public double netTotal;
    public double amountDue;
    public double discValue;
    public double couponValue;
    public double taxableTotal;
    public double taxPercentage;
    public double discountValue;
    public double totalTaxAmount;
    public double refundedAmount;
    public double totalAmountPaid;
    public double totalAmountSaved;
    public double customerPaidCharges;

    public ArrayList<BillModel> getServiceOptions() {
        return serviceOptions;
    }

    public long getServiceOptionId() {
        return serviceOptionId;
    }

    public String getServiceOptionName() {
        return serviceOptionName;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public Jdn getJdn() {
        return jdn;
    }

    public void setJdn(Jdn jdn) {
        this.jdn = jdn;
    }

    public BillDisplayNotes getDisplayNotes() {
        return displayNotes;
    }

    public void setDisplayNotes(BillDisplayNotes displayNotes) {
        this.displayNotes = displayNotes;
    }

    public double getTaxableTotal() {
        return taxableTotal;
    }

    public double getTotalTaxAmount() {
        return totalTaxAmount;
    }

    public double getTaxPercentage() {
        return taxPercentage;
    }

    public double getCustomerPaidCharges() {
        return customerPaidCharges;
    }

    public double getNetTotal() {
        return netTotal;
    }

    public Map<String, JsonObject> getProviderCoupon() {
        return providerCoupon;
    }

    public String getName() {
        return name;
    }

    public int getGSTpercentage() {
        return GSTpercentage;
    }

    public String getCouponName() {
        return couponName;
    }

    public String getDiscountName() {
        return discountName;
    }

    public int getId() {
        return id;
    }

    public double getNetRate() {
        return netRate;
    }

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

    public String getDisplayNote() {
        return displayNote;
    }

    public void setDisplayNote(String displayNote) {
        this.displayNote = displayNote;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public BillModel getCustomer() {
        return customer;
    }

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

    public ArrayList<BillModel> getItems() {
        return items;
    }

    public ArrayList<BillModel> getDiscount() {
        return discount;
    }

    public BillModel getUserProfile() {
        return userProfile;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public Map<String, JsonObject> getJCoupon() {
        return jCoupon;
    }

    public int getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(int deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public double getRefundedAmount() {
        return refundedAmount;
    }

    public void setRefundedAmount(double refundedAmount) {
        this.refundedAmount = refundedAmount;
    }

    public double getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(double amountDue) {
        this.amountDue = amountDue;
    }

    public AccountProfile getAccountProfile() {
        return accountProfile;
    }

    public void setAccountProfile(AccountProfile accountProfile) {
        this.accountProfile = accountProfile;
    }

    public double getTotalAmountSaved() {
        return totalAmountSaved;
    }
}

