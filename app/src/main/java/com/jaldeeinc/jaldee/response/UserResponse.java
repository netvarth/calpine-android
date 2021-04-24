package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserResponse {

    @SerializedName("providerVirtualFields")
    @Expose
    private String virtualFields;

    @SerializedName("providerBusinessProfile")
    @Expose
    private SearchViewDetail businessProfile;

    public String getVirtualFields() {
        return virtualFields;
    }

    public void setVirtualFields(String virtualFields) {
        this.virtualFields = virtualFields;
    }

    public SearchViewDetail getBusinessProfile() {
        return businessProfile;
    }

    public void setBusinessProfile(SearchViewDetail businessProfile) {
        this.businessProfile = businessProfile;
    }
}
