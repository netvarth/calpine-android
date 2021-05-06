package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Provider {

    @SerializedName("virtualFields")
    @Expose
    private String virtualFields;

    @SerializedName("settings")
    @Expose
    private String settings;

    @SerializedName("terminologies")
    @Expose
    private String terminologies;

    @SerializedName("departmentProviders")
    @Expose
    private Object departmentProviders;

    @SerializedName("coupon")
    @Expose
    private String coupon;

    @SerializedName("businessProfile")
    @Expose
    private SearchViewDetail businessProfile;

    @SerializedName("providerCoupon")
    @Expose
    private String providerCoupon;

    @SerializedName("location")
    @Expose
    private String location;

    @SerializedName("donationServices")
    @Expose
    private String donationServices;

    @SerializedName("gallery")
    @Expose
    private ArrayList<SearchViewDetail> galleryList;

    @SerializedName("jaldeediscount")
    @Expose
    private String jaldeediscount;

    public String getVirtualFields() {
        return virtualFields;
    }

    public void setVirtualFields(String virtualFields) {
        this.virtualFields = virtualFields;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    public String getTerminologies() {
        return terminologies;
    }

    public void setTerminologies(String terminologies) {
        this.terminologies = terminologies;
    }


    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public SearchViewDetail getBusinessProfile() {
        return businessProfile;
    }

    public void setBusinessProfile(SearchViewDetail businessProfile) {
        this.businessProfile = businessProfile;
    }

    public String getProviderCoupon() {
        return providerCoupon;
    }

    public void setProviderCoupon(String providerCoupon) {
        this.providerCoupon = providerCoupon;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDonationServices() {
        return donationServices;
    }

    public void setDonationServices(String donationServices) {
        this.donationServices = donationServices;
    }

    public ArrayList<SearchViewDetail> getGalleryList() {
        return galleryList;
    }

    public void setGalleryList(ArrayList<SearchViewDetail> galleryList) {
        this.galleryList = galleryList;
    }

    public String getJaldeediscount() {
        return jaldeediscount;
    }

    public void setJaldeediscount(String jaldeediscount) {
        this.jaldeediscount = jaldeediscount;
    }

    public Object getDepartmentProviders() {
        return departmentProviders;
    }

    public void setDepartmentProviders(Object departmentProviders) {
        this.departmentProviders = departmentProviders;
    }
}
