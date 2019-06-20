package com.nv.youneverwait.response;

import com.nv.youneverwait.model.SearchListModel;

import java.util.List;

public class SearchDepartmentResult {
    private String departmentName;

    public String getDepartmentStatus() {
        return departmentStatus;
    }

    public void setDepartmentStatus(String departmentStatus) {
        this.departmentStatus = departmentStatus;
    }

    private String departmentStatus;

    public int getDepartmentCount() {
        return departmentCount;
    }

    public void setDepartmentCount(int departmentCount) {
        this.departmentCount = departmentCount;
    }

    private int departmentCount;
    private List<SearchListModel> mSearchListModel;

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public List<SearchListModel> getmSearchListModel() {
        return mSearchListModel;
    }

    public void setmSearchListModel(List<SearchListModel> mSearchListModel) {
        this.mSearchListModel = mSearchListModel;
    }
}

