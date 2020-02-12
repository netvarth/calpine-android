package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SearchDepartmentServices {
    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    String departmentName;

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    String departmentId;

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    String departmentCode;

    public String getDepartmentDescription() {
        return departmentDescription;
    }

    public void setDepartmentDescription(String departmentDescription) {
        this.departmentDescription = departmentDescription;
    }

    String departmentDescription;

    public ArrayList<SearchDepartmentServices> getServices() {
        return services;
    }

    public void setServices(ArrayList<SearchDepartmentServices> services) {
        this.services = services;
    }

    ArrayList<SearchDepartmentServices> services;

    public String getDepartmentStatus() {
        return departmentStatus;
    }

    public void setDepartmentStatus(String departmentStatus) {
        this.departmentStatus = departmentStatus;
    }

    String departmentStatus;

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    boolean isDefault;
}
