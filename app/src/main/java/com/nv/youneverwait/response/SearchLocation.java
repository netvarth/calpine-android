package com.nv.youneverwait.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sharmila on 30/7/18.
 */

public class SearchLocation {

    public String getGoogleMapUrl() {
        return googleMapUrl;
    }

    String googleMapUrl;
    //Location
    private int id;
    private String place;
    private String address;
    private String parkingType;
    private boolean open24hours;
    private boolean baseLocation;
    @SerializedName("locationVirtualFields")
    SearchLocation locationVirtualFields;
    private String traumacentre;
    private String physiciansemergencyservices;
    @SerializedName("bSchedule")
    SearchLocation bSchedule;
    @SerializedName("timespec")
    private ArrayList<SearchLocation> timespec;
    private ArrayList<SearchLocation> timeSlots;

    private String eTime;
    private String sTime;
    private ArrayList repeatIntervals;

    public boolean isLocationAmentOpen() {
        return isLocationAmentOpen;
    }

    public void setLocationAmentOpen(boolean locationAmentOpen) {
        isLocationAmentOpen = locationAmentOpen;
    }

    boolean isLocationAmentOpen=false;

    public String getDocambulance() {
        return docambulance;
    }

    public void setDocambulance(String docambulance) {
        this.docambulance = docambulance;
    }

    public String getFirstaid() {
        return firstaid;
    }

    public void setFirstaid(String firstaid) {
        this.firstaid = firstaid;
    }

    String docambulance;
    String firstaid;

    public ArrayList getRepeatIntervals() {
        return repeatIntervals;
    }

    public void setRepeatIntervals(ArrayList repeatIntervals) {
        this.repeatIntervals = repeatIntervals;
    }



    public ArrayList<SearchLocation> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(ArrayList<SearchLocation> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public String geteTime() {
        return eTime;
    }

    public void seteTime(String eTime) {
        this.eTime = eTime;
    }

    public String getsTime() {
        return sTime;
    }

    public void setsTime(String sTime) {
        this.sTime = sTime;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getParkingType() {
        return parkingType;
    }

    public void setParkingType(String parkingType) {
        this.parkingType = parkingType;
    }

    public boolean isOpen24hours() {
        return open24hours;
    }

    public void setOpen24hours(boolean open24hours) {
        this.open24hours = open24hours;
    }

    public boolean isBaseLocation() {
        return baseLocation;
    }

    public void setBaseLocation(boolean baseLocation) {
        this.baseLocation = baseLocation;
    }

    public SearchLocation getLocationVirtualFields() {
        return locationVirtualFields;
    }

    public void setLocationVirtualFields(SearchLocation locationVirtualFields) {
        this.locationVirtualFields = locationVirtualFields;
    }

    public String getTraumacentre() {
        return traumacentre;
    }

    public void setTraumacentre(String traumacentre) {
        this.traumacentre = traumacentre;
    }

    public String getPhysiciansemergencyservices() {
        return physiciansemergencyservices;
    }

    public void setPhysiciansemergencyservices(String physiciansemergencyservices) {
        this.physiciansemergencyservices = physiciansemergencyservices;
    }

    public SearchLocation getbSchedule() {
        return bSchedule;
    }

    public void setbSchedule(SearchLocation bSchedule) {
        this.bSchedule = bSchedule;
    }

    public ArrayList<SearchLocation> getTimespec() {
        return timespec;
    }

    public void setTimespec(ArrayList<SearchLocation> timespec) {
        this.timespec = timespec;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    private String service;

    public void setTypename(String typename) {
        this.typename = typename;
    }

    private String typename;

    public String getDentistemergencyservices() {
        return dentistemergencyservices;
    }

    public void setDentistemergencyservices(String dentistemergencyservices) {
        this.dentistemergencyservices = dentistemergencyservices;
    }

    String dentistemergencyservices;

    public boolean isExpandFlag() {
        return expandFlag;
    }

    public void setExpandFlag(boolean expandFlag) {
        this.expandFlag = expandFlag;
    }

    boolean expandFlag=false;
}
