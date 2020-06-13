package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.SerializedName;
import com.jaldeeinc.jaldee.model.LocationModel;
import com.jaldeeinc.jaldee.model.SocialMediaModel;

import java.util.ArrayList;

/**
 * Created by sharmila on 24/7/18.
 */

public class SearchViewDetail {

    String label;
    String resource;
    String instance;
    String permission;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;

    public String getLabel() {
        return label;
    }

    public String getResource() {
        return resource;
    }

    public String getInstance() {
        return instance;
    }

    public String getPermission() {
        return permission;
    }

    int id;
    String businessName;
    String displayName;
    String domain;
    String branchId;
    boolean onlinePresence;

    public boolean isOnlinePresence() {
        return onlinePresence;
    }

    public void setOnlinePresence(boolean onlinePresence) {
        this.onlinePresence = onlinePresence;
    }

    String accountType;
    String businessDesc;
    String experience;

    String url;

    String awardName;
    String awardIssuedBy;
    String awardMonth;

    public void setBaseLocation(LocationModel baseLocation) {
        this.baseLocation = baseLocation;
    }

    public LocationModel getBaseLocation() {
        return baseLocation;
    }

    LocationModel baseLocation;

    public ArrayList<SocialMediaModel> getSocialMedia() {
        return socialMedia;
    }

    public ArrayList getSpecialization() {
        return specialization;
    }

    ArrayList specialization;


    ArrayList<SocialMediaModel> socialMedia;

    public ArrayList<SearchViewDetail> getEmails() {
        return emails;
    }

    ArrayList<SearchViewDetail> emails;

    public ArrayList<SearchViewDetail> getPhoneNumbers() {
        return phoneNumbers;
    }

    ArrayList<SearchViewDetail> phoneNumbers;

    public String getAwardName() {
        return awardName;
    }

    public String getAwardIssuedBy() {
        return awardIssuedBy;
    }

    public String getAwardMonth() {
        return awardMonth;
    }

    public String getAwardYear() {
        return awardYear;
    }

    String awardYear;

    public String getVerifyLevel() {
        return verifyLevel;
    }

    String verifyLevel;

    public SearchViewDetail getLogo() {
        return logo;
    }

    public void setLogo(SearchViewDetail logo) {
        this.logo = logo;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    String longitude;


    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    String lattitude;



    public SearchViewDetail logo;

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

    String subDomain;

    public String getSubDomain() {
        return subDomain;
    }

    public SearchViewDetail getServiceSubSector() {
        return serviceSubSector;
    }

    SearchViewDetail serviceSubSector;

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


    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
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

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    String caption;


}
