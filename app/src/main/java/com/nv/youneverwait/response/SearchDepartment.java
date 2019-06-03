package com.nv.youneverwait.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchDepartment implements Serializable {

    ArrayList departmentName;


    public ArrayList getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(ArrayList departmentName) {
        this.departmentName = departmentName;
    }

    @SerializedName("departments")
    ArrayList departments;


}