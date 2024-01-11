package com.jaldeeinc.customapp;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConfigResponseDTO {
    @SerializedName("homeUrl")
    @Expose
    private String homeUrl;

    @SerializedName("uniqueId")
    @Expose
    private String uniqueId;

    @SerializedName("providerId")
    @Expose
    private String providerId;

    @SerializedName("version")
    @Expose
    private String version;

    public String getHomeUrl() {
        return homeUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
