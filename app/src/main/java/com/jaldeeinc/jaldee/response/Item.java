package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Item implements Serializable {

    @SerializedName("itemId")
    @Expose
    private int itemId;

    @SerializedName("displayName")
    @Expose
    private String displayName;

    @SerializedName("shortDesc")
    @Expose
    private String shortDescription;

    @SerializedName("itemDesc")
    @Expose
    private String itemDescription;

    @SerializedName("price")
    @Expose
    private double price;

    @SerializedName("taxable")
    @Expose
    private boolean taxable;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("adhoc")
    @Expose
    private boolean adhoc;

    @SerializedName("itemName")
    @Expose
    private String itemName;

    @SerializedName("isShowOnLandingpage")
    @Expose
    private boolean isShowOnLandingPage;

    @SerializedName("isStockAvailable")
    @Expose
    private boolean isStockAvailable;

    @SerializedName("promotionalPriceType")
    @Expose
    private String promotionalPriceType;

    @SerializedName("itemImages")
    @Expose
    private ArrayList<ItemImages> itemImagesList;

    @SerializedName("promotionalPrice")
    @Expose
    private double promotionalPrice;

    @SerializedName("showPromotionalPrice")
    @Expose
    private boolean showPromotionalPrice;

    @SerializedName("promotionLabelType")
    @Expose
    private String promotionLabelType;

    @SerializedName("promotionLabel")
    @Expose
    private String promotionLabel;

    @SerializedName("itemCode")
    @Expose
    private String itemCode;

    @SerializedName("itemType")
    @Expose
    private String itemType;

    private String displayImage ="";

    private double discountedPrice = 0;

    private int itemQuantity = 0; // not a response param

    public Item(){

    }

    public Item(int itemId, String displayName, String itemDescription, double price, boolean taxable, String status, boolean adhoc, String itemName, boolean isShowOnLandingPage, boolean isStockAvailable, String promotionalPriceType, ArrayList<ItemImages> itemImagesList, double promotionalPrcnt, boolean showPromotionalPrice, String itemCode, String itemType) {
        this.itemId = itemId;
        this.displayName = displayName;
        this.itemDescription = itemDescription;
        this.price = price;
        this.taxable = taxable;
        this.status = status;
        this.adhoc = adhoc;
        this.itemName = itemName;
        this.isShowOnLandingPage = isShowOnLandingPage;
        this.isStockAvailable = isStockAvailable;
        this.promotionalPriceType = promotionalPriceType;
        this.itemImagesList = itemImagesList;
        this.promotionalPrice = promotionalPrcnt;
        this.showPromotionalPrice = showPromotionalPrice;
        this.itemCode = itemCode;
        this.itemType = itemType;
    }

    public String getItemType() { return itemType; }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }

    public boolean isTaxable() {
        return taxable;
    }

    public void setTaxable(boolean taxable) {
        this.taxable = taxable;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isAdhoc() {
        return adhoc;
    }

    public void setAdhoc(boolean adhoc) {
        this.adhoc = adhoc;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public boolean isShowOnLandingPage() {
        return isShowOnLandingPage;
    }

    public void setShowOnLandingPage(boolean showOnLandingPage) {
        isShowOnLandingPage = showOnLandingPage;
    }

    public boolean isStockAvailable() {
        return isStockAvailable;
    }

    public void setStockAvailable(boolean stockAvailable) {
        isStockAvailable = stockAvailable;
    }

    public String getPromotionalPriceType() {
        return promotionalPriceType;
    }

    public void setPromotionalPriceType(String promotionalPriceType) {
        this.promotionalPriceType = promotionalPriceType;
    }

    public ArrayList<ItemImages> getItemImagesList() {
        return itemImagesList;
    }

    public void setItemImagesList(ArrayList<ItemImages> itemImagesList) {
        this.itemImagesList = itemImagesList;
    }

    public boolean isShowPromotionalPrice() {
        return showPromotionalPrice;
    }

    public void setShowPromotionalPrice(boolean showPromotionalPrice) {
        this.showPromotionalPrice = showPromotionalPrice;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public double getPromotionalPrice() {
        return promotionalPrice;
    }

    public void setPromotionalPrice(double promotionalPrice) {
        this.promotionalPrice = promotionalPrice;
    }

    public String getDisplayImage() {
        return displayImage;
    }

    public void setDisplayImage(String displayImage) {
        this.displayImage = displayImage;
    }

    public String getPromotionLabelType() {
        return promotionLabelType;
    }

    public void setPromotionLabelType(String promotionLabelType) {
        this.promotionLabelType = promotionLabelType;
    }

    public String getPromotionLabel() {
        return promotionLabel;
    }

    public void setPromotionLabel(String promotionLabel) {
        this.promotionLabel = promotionLabel;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }
}
