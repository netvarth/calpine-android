package com.jaldeeinc.jaldee.response;

import com.jaldeeinc.jaldee.model.ProviderUserModel;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchDepartmentServices implements Serializable{
    String departmentName;
    String departmentId;
    String departmentCode;
    String departmentDescription;
    String departmentStatus;
    boolean isDefault;

    ArrayList<SearchService > services;
    ArrayList<ProviderUserModel> users;

    public String getDepartmentName() {
        return departmentName;
    }
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
    public String getDepartmentId() {
        return departmentId;
    }
    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }
    public String getDepartmentCode() {
        return departmentCode;
    }
    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }
    public String getDepartmentDescription() {
        return departmentDescription;
    }
    public void setDepartmentDescription(String departmentDescription) {
        this.departmentDescription = departmentDescription;
    }
    public String getDepartmentStatus() {
        return departmentStatus;
    }
    public void setDepartmentStatus(String departmentStatus) {
        this.departmentStatus = departmentStatus;
    }
    public boolean isDefault() {
        return isDefault;
    }
    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
    public ArrayList<SearchService> getServices() {
        return services;
    }
    public void setServices(ArrayList<SearchService> services) {
        this.services = services;
    }

    public ArrayList<ProviderUserModel> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<ProviderUserModel> users) {
        this.users = users;
    }
}
