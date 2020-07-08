package com.jaldeeinc.jaldee.response;

import java.util.ArrayList;

public class SearchAppointmentDepartmentServices {
    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    String departmentName;

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    int departmentId;

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    String departmentCode;

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

    public ArrayList<SearchAppointmentDepartmentServices> getServices() {
        return services;
    }

    public void setServices(ArrayList<SearchAppointmentDepartmentServices> services) {
        this.services = services;
    }

    ArrayList<SearchAppointmentDepartmentServices> services;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    int id;

    public int getServiceDuration() {
        return serviceDuration;
    }

    public void setServiceDuration(int serviceDuration) {
        this.serviceDuration = serviceDuration;
    }

    int serviceDuration;

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    double totalAmount;

}
