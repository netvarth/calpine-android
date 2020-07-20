package com.jaldeeinc.jaldee.model;

import com.jaldeeinc.jaldee.response.SearchService;

import java.util.ArrayList;

/**
 * Created by Mani on 12/08/19.
 */


public class DepartmentModal {
    String departmentName;
    String departmentCode;
    String departmentDescription;
    ArrayList<SearchService> services;
    ArrayList<ProviderUserModel> users;

    public ArrayList<ProviderUserModel> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<ProviderUserModel> users) {
        this.users = users;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
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

    public ArrayList<SearchService> getServices() {
        return services;
    }

    public void setServices(ArrayList<SearchService> services) {
        this.services = services;
    }
}
