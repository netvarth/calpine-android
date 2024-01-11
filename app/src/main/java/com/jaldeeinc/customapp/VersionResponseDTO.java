package com.jaldeeinc.customapp;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VersionResponseDTO {
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("license")
    @Expose
    private String license;

    @SerializedName("appstore")
    @Expose
    private VersionDTO appstore;

    @SerializedName("playstore")
    @Expose
    private VersionDTO playstore;

    @SerializedName("cacheIndex")
    @Expose
    private Integer cacheIndex;

    public Integer getCacheIndex() {
        return cacheIndex;
    }

    public VersionDTO getPlaystore() {
        return playstore;
    }
}
