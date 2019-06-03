package com.nv.youneverwait.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchDepartment implements Serializable {

    String departmentName;


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

}