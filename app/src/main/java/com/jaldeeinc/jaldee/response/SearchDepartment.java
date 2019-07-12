package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchDepartment implements Serializable {

    String departmentName;

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

    @Override
    public String toString() {
        return this.departmentName; // Value to be displayed in the Spinner
    }

}