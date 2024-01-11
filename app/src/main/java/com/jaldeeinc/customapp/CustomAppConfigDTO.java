package com.jaldeeinc.customapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CustomAppConfigDTO {

    @SerializedName("intentUrls")
    @Expose
    private List<String> intentUrls;

    @SerializedName("exceptionUrls")
    @Expose
    private List<String> exceptionUrls;

    public List<String> getExceptionUrls() {
        return exceptionUrls;
    }

    public List<String> getIntentUrls() {
        return intentUrls;
    }

}
