package com.nv.youneverwait.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sharmila on 16/7/18.
 */

public class SearchModel {


    public ArrayList<SearchModel> getGlobalSearchLabels() {
        return globalSearchLabels;
    }

    public void setGlobalSearchLabels(ArrayList<SearchModel> globalSearchLabels) {
        this.globalSearchLabels = globalSearchLabels;
    }

    @SerializedName("globalSearchLabels")
    ArrayList<SearchModel> globalSearchLabels;


    public ArrayList<SearchModel> getSectorLevelLabels() {
        return sectorLevelLabels;
    }

    public void setSectorLevelLabels(ArrayList<SearchModel> sectorLevelLabels) {
        this.sectorLevelLabels = sectorLevelLabels;
    }

    @SerializedName("sectorLevelLabels")
    ArrayList<SearchModel> sectorLevelLabels;


    public ArrayList<SearchModel> getSubSectorLevelLabels() {
        return subSectorLevelLabels;
    }

    @SerializedName("subSectorLevelLabels")
    ArrayList<SearchModel> subSectorLevelLabels;

    public ArrayList<SearchModel> getSpecializationLabels() {
        return specializationLabels;
    }

    @SerializedName("specializationLabels")
    ArrayList<SearchModel> specializationLabels;

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    String group;
    public void setName(String name) {
        this.name = name;
    }

    String name;
    String sector;

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }


    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    String query;

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    String displayname;

}
