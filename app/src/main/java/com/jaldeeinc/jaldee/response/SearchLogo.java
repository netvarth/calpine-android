package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchLogo {

    @SerializedName("")
    @Expose
    private HashMap<String, ArrayList<ProfilePicture>> result;

    public HashMap<String, ArrayList<ProfilePicture>> getResult() {
        return result;
    }

    public void setResult(HashMap<String, ArrayList<ProfilePicture>> result) {
        this.result = result;
    }
}
