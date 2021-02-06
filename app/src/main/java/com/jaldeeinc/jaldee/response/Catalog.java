package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Catalog implements Serializable {

    @SerializedName("id")
    @Expose
    private int catLogId;

    @SerializedName("catalogName")
    @Expose
    private String catLogName;

    @SerializedName("catalogDesc")
    @Expose
    private String catalogDescription;

    @SerializedName("catalogSchedule")
    @Expose
    private CatalogSchedule catalogSchedule;

    @SerializedName("expired")
    @Expose
    private boolean expired;

    @SerializedName("catalogStatus")
    @Expose
    private String catalogStatus;

    @SerializedName("orderType")
    @Expose
    private String orderType;

    @SerializedName("pickUp")
    @Expose
    private PickUp pickUp;

    @SerializedName("homeDelivery")
    @Expose
    private HomeDelivery homeDelivery;

    @SerializedName("showPrice")
    @Expose
    private boolean showPrice;

    @SerializedName("paymentType")
    @Expose
    private String paymentType;

    @SerializedName("advanceAmount")
    @Expose
    private String advanceAmount;

    @SerializedName("showContactInfo")
    @Expose
    private boolean showContactInfo;

    @SerializedName("preInfo")
    @Expose
    private PreInfo preInfo;

    @SerializedName("postInfo")
    @Expose
    private PostInfo postInfo;

    @SerializedName("catalogItem")
    @Expose
    private ArrayList<CatalogItem> catalogItemsList;

    @SerializedName("catalogImages")
    @Expose
    private ArrayList<ItemImages> catalogImagesList;

    @SerializedName("minNumberItem")
    @Expose
    private int minNumberItem;

    @SerializedName("maxNumberItem")
    @Expose
    private int maxNumberItem;

    @SerializedName("autoConfirm")
    @Expose
    private boolean autoConfirm;

    @SerializedName("cancellationPolicy")
    @Expose
    private String cancellationPolicy;

    @SerializedName("nextAvailablePickUpDetails")
    @Expose
    private NextAvailablePickUpDetails nextAvailablePickUpDetails;

    @SerializedName("nextAvailableDeliveryDetails")
    @Expose
    private NextAvailableDeliveryDetails nextAvailableDeliveryDetails;

    @SerializedName("taxPercentage")
    @Expose
    private double taxPercentage;

    public Catalog(){

    }

    public Catalog(int catLogId, String catLogName, String catalogDescription, CatalogSchedule catalogSchedule, boolean expired, String catalogStatus, String orderType, PickUp pickUp, HomeDelivery homeDelivery, boolean showPrice, String paymentType, String advanceAmount, boolean showContactInfo, PreInfo preInfo, PostInfo postInfo, ArrayList<CatalogItem> catalogItemsList, int minNumberItem, int maxNumberItem, boolean autoConfirm, String cancellationPolicy, NextAvailablePickUpDetails nextAvailablePickUpDetails, NextAvailableDeliveryDetails nextAvailableDeliveryDetails) {
        this.catLogId = catLogId;
        this.catLogName = catLogName;
        this.catalogDescription = catalogDescription;
        this.catalogSchedule = catalogSchedule;
        this.expired = expired;
        this.catalogStatus = catalogStatus;
        this.orderType = orderType;
        this.pickUp = pickUp;
        this.homeDelivery = homeDelivery;
        this.showPrice = showPrice;
        this.paymentType = paymentType;
        this.advanceAmount = advanceAmount;
        this.showContactInfo = showContactInfo;
        this.preInfo = preInfo;
        this.postInfo = postInfo;
        this.catalogItemsList = catalogItemsList;
        this.minNumberItem = minNumberItem;
        this.maxNumberItem = maxNumberItem;
        this.autoConfirm = autoConfirm;
        this.cancellationPolicy = cancellationPolicy;
        this.nextAvailablePickUpDetails = nextAvailablePickUpDetails;
        this.nextAvailableDeliveryDetails = nextAvailableDeliveryDetails;
    }


    public int getCatLogId() {
        return catLogId;
    }

    public void setCatLogId(int catLogId) {
        this.catLogId = catLogId;
    }

    public String getCatLogName() {
        return catLogName;
    }

    public void setCatLogName(String catLogName) {
        this.catLogName = catLogName;
    }

    public String getCatalogDescription() {
        return catalogDescription;
    }

    public void setCatalogDescription(String catalogDescription) {
        this.catalogDescription = catalogDescription;
    }

    public CatalogSchedule getCatalogSchedule() {
        return catalogSchedule;
    }

    public void setCatalogSchedule(CatalogSchedule catalogSchedule) {
        this.catalogSchedule = catalogSchedule;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public String getCatalogStatus() {
        return catalogStatus;
    }

    public void setCatalogStatus(String catalogStatus) {
        this.catalogStatus = catalogStatus;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public PickUp getPickUp() {
        return pickUp;
    }

    public void setPickUp(PickUp pickUp) {
        this.pickUp = pickUp;
    }

    public HomeDelivery getHomeDelivery() {
        return homeDelivery;
    }

    public void setHomeDelivery(HomeDelivery homeDelivery) {
        this.homeDelivery = homeDelivery;
    }

    public boolean isShowPrice() {
        return showPrice;
    }

    public void setShowPrice(boolean showPrice) {
        this.showPrice = showPrice;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getAdvanceAmount() {
        return advanceAmount;
    }

    public void setAdvanceAmount(String advanceAmount) {
        this.advanceAmount = advanceAmount;
    }

    public boolean isShowContactInfo() {
        return showContactInfo;
    }

    public void setShowContactInfo(boolean showContactInfo) {
        this.showContactInfo = showContactInfo;
    }

    public PreInfo getPreInfo() {
        return preInfo;
    }

    public void setPreInfo(PreInfo preInfo) {
        this.preInfo = preInfo;
    }

    public PostInfo getPostInfo() {
        return postInfo;
    }

    public void setPostInfo(PostInfo postInfo) {
        this.postInfo = postInfo;
    }

    public ArrayList<CatalogItem> getCatalogItemsList() {
        return catalogItemsList;
    }

    public void setCatalogItemsList(ArrayList<CatalogItem> catalogItemsList) {
        this.catalogItemsList = catalogItemsList;
    }

    public int getMinNumberItem() {
        return minNumberItem;
    }

    public void setMinNumberItem(int minNumberItem) {
        this.minNumberItem = minNumberItem;
    }

    public int getMaxNumberItem() {
        return maxNumberItem;
    }

    public void setMaxNumberItem(int maxNumberItem) {
        this.maxNumberItem = maxNumberItem;
    }

    public boolean isAutoConfirm() {
        return autoConfirm;
    }

    public void setAutoConfirm(boolean autoConfirm) {
        this.autoConfirm = autoConfirm;
    }

    public String getCancellationPolicy() {
        return cancellationPolicy;
    }

    public void setCancellationPolicy(String cancellationPolicy) {
        this.cancellationPolicy = cancellationPolicy;
    }

    public NextAvailablePickUpDetails getNextAvailablePickUpDetails() {
        return nextAvailablePickUpDetails;
    }

    public void setNextAvailablePickUpDetails(NextAvailablePickUpDetails nextAvailablePickUpDetails) {
        this.nextAvailablePickUpDetails = nextAvailablePickUpDetails;
    }

    public NextAvailableDeliveryDetails getNextAvailableDeliveryDetails() {
        return nextAvailableDeliveryDetails;
    }

    public void setNextAvailableDeliveryDetails(NextAvailableDeliveryDetails nextAvailableDeliveryDetails) {
        this.nextAvailableDeliveryDetails = nextAvailableDeliveryDetails;
    }

    public ArrayList<ItemImages> getCatalogImagesList() {
        return catalogImagesList;
    }

    public void setCatalogImagesList(ArrayList<ItemImages> catalogImagesList) {
        this.catalogImagesList = catalogImagesList;
    }

    public double getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(double taxPercentage) {
        this.taxPercentage = taxPercentage;
    }
}
