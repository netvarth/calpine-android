package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ActiveOrders implements Serializable {
    int accesScope;
    public int getAccesScope() {
        return accesScope;
    }

    public void setAccesScope(int accesScope) {
        this.accesScope = accesScope;
    }

    int account;
    public int getAccount() {
        return account;
    }

    public void setAccount(int account) {
        this.account = account;
    }

    boolean active;
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    double amountDue;
    public double getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(double amountDue) {
        this.amountDue = amountDue;
    }

    double cartAmount;
    public double getCartAmount() {
        return cartAmount;
    }

    public void setCartAmount(double cartAmount) {
        this.cartAmount = cartAmount;
    }

    double advanceAmountPaid;
    public double getAdvanceAmountPaid() {
        return advanceAmountPaid;
    }

    public void setAdvanceAmountPaid(double advanceAmountPaid) {
        this.advanceAmountPaid = advanceAmountPaid;
    }
    double advanceAmountToPay;
    public double getAdvanceAmountToPay() {
        return advanceAmountToPay;
    }

    public void setAdvanceAmountToPay(double advanceAmountToPay) {
        this.advanceAmountToPay = advanceAmountToPay;
    }


    @SerializedName("consumer")
    @Expose
    private ConsumerDetails consumer;

    public ConsumerDetails getConsumer() {
        return consumer;
    }

    public void setConsumer(ConsumerDetails consumer) {
        this.consumer = consumer;
    }

    String countryCode;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }



    String email;
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    String orderDate;
    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }


    public CustomerDetails getOrderFor() {
        return orderFor;
    }

    public void setOrderFor(CustomerDetails orderFor) {
        this.orderFor = orderFor;
    }

    @SerializedName("orderFor")
    @Expose
    private CustomerDetails orderFor;

    public ArrayList<OrderDetails> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(ArrayList<OrderDetails> orderDetails) {
        this.orderDetails = orderDetails;
    }

    private ArrayList<OrderDetails> orderDetails;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    String orderNumber;

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    String orderStatus;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    String phoneNumber;

    public ProviderDetails getProviderAccount() {
        return providerAccount;
    }

    public void setProviderAccount(ProviderDetails providerAccount) {
        this.providerAccount = providerAccount;
    }

    @SerializedName("providerAccount")
    @Expose
    private ProviderDetails providerAccount;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    String uid;

    public int getTotalItemQuantity() {
        return totalItemQuantity;
    }

    public void setTotalItemQuantity(int totalItemQuantity) {
        this.totalItemQuantity = totalItemQuantity;
    }

    int totalItemQuantity;

    public boolean isHomeDelivery() {
        return homeDelivery;
    }

    public void setHomeDelivery(boolean homeDelivery) {
        this.homeDelivery = homeDelivery;
    }

    boolean homeDelivery;

    public boolean isStorePickup() {
        return storePickup;
    }

    public void setStorePickup(boolean storePickup) {
        this.storePickup = storePickup;
    }

    boolean storePickup;

    public BillDetails getBill() {
        return bill;
    }

    public void setBill(BillDetails bill) {
        this.bill = bill;
    }

    private BillDetails bill;


    @SerializedName("timeSlot")
    @Expose
    private TimeSlot timeSlot;

    @SerializedName("orderItem")
    @Expose
    private ArrayList<ItemDetails> itemsList;

    @SerializedName("shoppingList")
    @Expose
    private ArrayList<ShoppingList> shoppingList;

    @SerializedName("orderNote")
    @Expose
    private String orderNote;

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public ArrayList<ItemDetails> getItemsList() {
        return itemsList;
    }

    public void setItemsList(ArrayList<ItemDetails> itemsList) {
        this.itemsList = itemsList;
    }

    public ArrayList<ShoppingList> getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(ArrayList<ShoppingList> shoppingList) {
        this.shoppingList = shoppingList;
    }

    public String getOrderNote() {
        return orderNote;
    }

    public void setOrderNote(String orderNote) {
        this.orderNote = orderNote;
    }
}
