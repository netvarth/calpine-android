package com.jaldeeinc.jaldee.model;

import com.jaldeeinc.jaldee.response.Questionnaire;

import java.io.Serializable;

public class CartItemModel implements Serializable {

    private int id;
    private int itemId;
    private int accountId;
    private int uniqueId;
    private int catalogId;
    private String itemName;
    private String imageUrl;
    private int quantity;
    private double itemPrice;
    private double price;
    private double serviceOptionPrice;
    private String instruction;
    private int maxQuantity;
    private double discount;
    private double discountedPrice;
    private String promotionalType;
    private int isPromotional;
    private int isExpired = 0;
    private int isTaxable = 0;
    private double tax;
    private String itemType;
    private String questionnaire;
    private String serviceOptioniput;
    private String serviceOptionAtachedImages;
    public CartItemModel(){

    }



    public CartItemModel(int itemId, double itemPrice, int maxQuantity, double discountedPrice) {
        this.itemId = itemId;
        this.itemPrice = itemPrice;
        this.maxQuantity = maxQuantity;
        this.discountedPrice = discountedPrice;
        this.isPromotional = 0;
    }

    public CartItemModel(int id, int itemId, int accountId, int uniqueId, int catalogId, String itemName, String imageUrl,
                         int quantity, double itemPrice, double price, String instruction, int maxQuantity, double discount,
                         double discountedPrice, String promotionalType, int isPromotional, int isExpired, double tax,
                         String itemType, String questionnaire, String serviceOptioniput, String serviceOptionAtachedImages) {
        this.id = id;
        this.itemId = itemId;
        this.accountId = accountId;
        this.uniqueId = uniqueId;
        this.catalogId = catalogId;
        this.itemName = itemName;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.itemPrice = itemPrice;
        this.price = price;
        this.instruction = instruction;
        this.maxQuantity = maxQuantity;
        this.discount = discount;
        this.discountedPrice = discountedPrice;
        this.promotionalType = promotionalType;
        this.isPromotional = isPromotional;
        this.isExpired = isExpired;
        this.tax = tax;
        this.itemType = itemType;
        this.questionnaire = questionnaire;
        this.serviceOptioniput = serviceOptioniput;
        this.serviceOptionAtachedImages = serviceOptionAtachedImages;
    }

    public double getServiceOptionPrice() {
        return serviceOptionPrice;
    }

    public void setServiceOptionPrice(double serviceOptionPrice) {
        this.serviceOptionPrice = serviceOptionPrice;
    }

    public String getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(String questionnaire) {
        this.questionnaire = questionnaire;
    }

    public String getServiceOptioniput() {
        return serviceOptioniput;
    }

    public void setServiceOptioniput(String serviceOptioniput) {
        this.serviceOptioniput = serviceOptioniput;
    }

    public String getServiceOptionAtachedImages() {
        return serviceOptionAtachedImages;
    }

    public void setServiceOptionAtachedImages(String serviceOptionAtachedImages) {
        this.serviceOptionAtachedImages = serviceOptionAtachedImages;
    }

    public String getItemType() { return itemType; }

    public void setItemType(String itemType) { this.itemType = itemType; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(int catalogId) {
        this.catalogId = catalogId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public String getPromotionalType() {
        return promotionalType;
    }

    public void setPromotionalType(String promotionalType) {
        this.promotionalType = promotionalType;
    }

    public int getIsPromotional() {
        return isPromotional;
    }

    public void setIsPromotional(int isPromotional) {
        this.isPromotional = isPromotional;
    }

    public int isExpired() {
        return isExpired;
    }

    public void setExpired(int expired) {
        isExpired = expired;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    public int getIsExpired() {
        return isExpired;
    }

    public void setIsExpired(int isExpired) {
        this.isExpired = isExpired;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public int getIsTaxable() {
        return isTaxable;
    }

    public void setIsTaxable(int isTaxable) {
        this.isTaxable = isTaxable;
    }
}
