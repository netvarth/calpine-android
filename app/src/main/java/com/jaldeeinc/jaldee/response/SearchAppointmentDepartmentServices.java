package com.jaldeeinc.jaldee.response;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchAppointmentDepartmentServices implements Serializable {
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

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    String serviceType;

    public ArrayList<SearchAppointmentDepartmentServices> getVirtualCallingModes() {
        return virtualCallingModes;
    }

    public void setVirtualCallingModes(ArrayList<SearchAppointmentDepartmentServices> virtualCallingModes) {
        this.virtualCallingModes = virtualCallingModes;
    }

    ArrayList<SearchAppointmentDepartmentServices> virtualCallingModes;

    public String getCallingMode() {
        return callingMode;
    }

    public void setCallingMode(String callingMode) {
        this.callingMode = callingMode;
    }

    String callingMode;

    public String getVirtualServiceType() {
        return virtualServiceType;
    }

    public void setVirtualServiceType(String virtualServiceType) {
        this.virtualServiceType = virtualServiceType;
    }

    String virtualServiceType;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    String description;

    public boolean isTaxable() {
        return taxable;
    }

    public void setTaxable(boolean taxable) {
        this.taxable = taxable;
    }

    boolean taxable;

    public boolean isPrePayment() {
        return isPrePayment;
    }

    public void setPrePayment(boolean prePayment) {
        isPrePayment = prePayment;
    }

    boolean isPrePayment;

    public double getMinPrePaymentAmount() {
        return minPrePaymentAmount;
    }

    public void setMinPrePaymentAmount(double minPrePaymentAmount) {
        this.minPrePaymentAmount = minPrePaymentAmount;
    }

    double minPrePaymentAmount;

    public ArrayList<SearchAppointmentDepartmentServices> getServicegallery() {
        return servicegallery;
    }

    public void setServicegallery(ArrayList<SearchAppointmentDepartmentServices> servicegallery) {
        this.servicegallery = servicegallery;
    }

    ArrayList<SearchAppointmentDepartmentServices> servicegallery;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    String url;

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    String thumbUrl;

}
