package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sharmila on 13/11/18.
 */

public class RefinedFilters {


    @SerializedName("commonFilters")
    private ArrayList<RefinedFilters> commonFilters;

    @SerializedName("refinedFilters")
    private ArrayList<RefinedFilters> refinedFilters;

    String name;

    public void setName(String name) {
        this.name = name;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    String displayName;

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    String  dataType;

    public void setCloudSearchIndex(String cloudSearchIndex) {
        this.cloudSearchIndex = cloudSearchIndex;
    }

    String cloudSearchIndex;

    public Object getEnumeratedConstants() {
        return enumeratedConstants;
    }

    public void setEnumeratedConstants(Object enumeratedConstants) {
        this.enumeratedConstants = enumeratedConstants;
    }

    private Object enumeratedConstants;

    public ArrayList<RefinedFilters> getCommonFilters() {
        return commonFilters;
    }

    public ArrayList<RefinedFilters> getRefinedFilters() {
        return refinedFilters;
    }


    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDataType() {
        return dataType;
    }

    public String getCloudSearchIndex() {
        return cloudSearchIndex;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    boolean isExpand=false;

    public ArrayList getItemName() {
        return itemName;
    }

    public void setItemName(ArrayList itemName) {
        this.itemName = itemName;
    }

    ArrayList itemName;

    public ArrayList getCloudIndexvalue() {
        return cloudIndexvalue;
    }

    public void setCloudIndexvalue(ArrayList cloudIndexvalue) {
        this.cloudIndexvalue = cloudIndexvalue;
    }

    ArrayList cloudIndexvalue;

    public ArrayList getPassName() {
        return passName;
    }

    public void setPassName(ArrayList passName) {
        this.passName = passName;
    }

    ArrayList passName;
}
