package com.nv.youneverwait.response;

import com.google.gson.annotations.SerializedName;
import com.nv.youneverwait.model.CommonFilterEnum;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by sharmila on 13/11/18.
 */

public class RefinedFilters {


    @SerializedName("commonFilters")
    private ArrayList<RefinedFilters> commonFilters;

    @SerializedName("refinedFilters")
    private ArrayList<RefinedFilters> refinedFilters;

    String name;
    String displayName;
    String  dataType;
    String cloudSearchIndex;

  // private String enumeratedConstants;

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

   /* public String getEnumeratedConstants() {
        return enumeratedConstants;
    }*/






}
