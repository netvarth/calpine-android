package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchDepartment implements Serializable {

    String departmentName;

    String businessName;
    String isSelected = "";

    public String getDepartmentStatus() {
        return departmentStatus;
    }

    public void setDepartmentStatus(String departmentStatus) {
        this.departmentStatus = departmentStatus;
    }

    String departmentStatus;

    public boolean isFilterByDept() {
        return filterByDept;
    }

    public void setFilterByDept(boolean filterByDept) {
        this.filterByDept = filterByDept;
    }
    @SerializedName("filterByDept")
    private boolean filterByDept;



    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    String departmentCode;


    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public ArrayList<SearchDepartment> getDepartments() {
        return departments;
    }

    public void setDepartments(ArrayList<SearchDepartment> departments) {
        this.departments = departments;
    }

    @SerializedName("departments")
    ArrayList<SearchDepartment> departments;

    public ArrayList<Integer> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(ArrayList<Integer> serviceIds) {
        this.serviceIds = serviceIds;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    int departmentId;

    ArrayList<Integer> serviceIds;
    ArrayList<Integer> userIds;

    @Override
    public String toString() {
        return this.departmentName; // Value to be displayed in the Spinner
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }


    public String getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(String isSelected) {
        this.isSelected = isSelected;
    }

    public ArrayList<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(ArrayList<Integer> userIds) {
        this.userIds = userIds;
    }

}