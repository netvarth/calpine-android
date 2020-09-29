package com.jaldeeinc.jaldee.response;

import java.util.ArrayList;

public class DepartmentsAndServices {

    String departmentId;
    String departmentName;
    ArrayList<SearchService> checkInServices;
    ArrayList<SearchAppoinment> appointServices;
    ArrayList<SearchDonation> donationServices;

    public DepartmentsAndServices() {
    }

    public DepartmentsAndServices(String departmentId, String departmentName, ArrayList<SearchService> checkInServices, ArrayList<SearchAppoinment> appointServices, ArrayList<SearchDonation> donationServices) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.checkInServices = checkInServices;
        this.appointServices = appointServices;
        this.donationServices = donationServices;
    }



    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public ArrayList<SearchService> getCheckInServices() {
        return checkInServices;
    }

    public void setCheckInServices(ArrayList<SearchService> checkInServices) {
        this.checkInServices = checkInServices;
    }

    public ArrayList<SearchAppoinment> getAppointServices() {
        return appointServices;
    }

    public void setAppointServices(ArrayList<SearchAppoinment> appointServices) {
        this.appointServices = appointServices;
    }

    public ArrayList<SearchDonation> getDonationServices() {
        return donationServices;
    }

    public void setDonationServices(ArrayList<SearchDonation> donationServices) {
        this.donationServices = donationServices;
    }
}
