package com.netvarth.youneverwait.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sharmila on 24/7/18.
 */

public class SearchViewDetail {


    int id;
    String businessName;
    String displayName;
    String   domain;
    String businessDesc;
    String experience;

    String url;

    public float getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(float avgRating) {
        this.avgRating = avgRating;
    }

    float avgRating;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    String thumbUrl;


    @SerializedName("serviceSector")
    SearchViewDetail serviceSector;

    @SerializedName("domainVirtualFields")
    SearchViewDetail domainVirtualFields;

    @SerializedName("awardsrecognitions")
    ArrayList<SearchViewDetail> awardsrecognitions;

    public ArrayList<SearchViewDetail> getAwardsrecognitions() {
        return awardsrecognitions;
    }

    public void setAwardsrecognitions(ArrayList<SearchViewDetail> awardsrecognitions) {
        this.awardsrecognitions = awardsrecognitions;
    }
    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getBusinessDesc() {
        return businessDesc;
    }

    public void setBusinessDesc(String businessDesc) {
        this.businessDesc = businessDesc;
    }

    public SearchViewDetail getDomainVirtualFields() {
        return domainVirtualFields;
    }

    public void setDomainVirtualFields(SearchViewDetail domainVirtualFields) {
        this.domainVirtualFields = domainVirtualFields;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SearchViewDetail getServiceSector() {
        return serviceSector;
    }

    public void setServiceSector(SearchViewDetail serviceSector) {
        this.serviceSector = serviceSector;
    }


    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }


}
