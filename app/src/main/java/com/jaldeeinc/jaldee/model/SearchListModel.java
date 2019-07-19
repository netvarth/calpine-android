package com.jaldeeinc.jaldee.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sharmila on 25/7/18.
 */

public class SearchListModel implements Serializable{

    public boolean isIs_SpecOpen() {
        return is_SpecOpen;
    }

    public void setIs_SpecOpen(boolean is_SpecOpen) {
        this.is_SpecOpen = is_SpecOpen;
    }

    private boolean is_SpecOpen=false;
    private String id;
    private String Logo;
    private String title;
    private String sector;
    private String place1;
    private String uniqueid;
    private String rating;
    private String QId;
    private String mLoc;
    private String avail_date;
    private String parking_type_location1;
    private String firstaid_location1;
    private String traumacentre_location1;
    private String docambulance_location1;
    private String always_open_location1;
    public String getParking_location1() {
        return parking_location1;
    }

    public void setParking_location1(String parking_location1) {
        this.parking_location1 = parking_location1;
    }

    public ArrayList getGallery_thumb_nails() {
        return gallery_thumb_nails;
    }

    public void setGallery_thumb_nails(ArrayList gallery_thumb_nails) {
        this.gallery_thumb_nails = gallery_thumb_nails;
    }

    ArrayList gallery_thumb_nails;
    String parking_location1;
    public String getSectorname() {
        return sectorname;
    }

    public void setSectorname(String sectorname) {
        this.sectorname = sectorname;
    }

    String sectorname;

    public String getSub_sector() {
        return sub_sector;
    }

    public void setSub_sector(String sub_sector) {
        this.sub_sector = sub_sector;
    }

    String sub_sector;

    public String getLocation1() {
        return location1;
    }

    public void setLocation1(String location1) {
        this.location1 = location1;
    }

    String location1;

    public int getYnw_verified() {
        return ynw_verified;
    }

    public void setYnw_verified(int ynw_verified) {
        this.ynw_verified = ynw_verified;
    }

    int ynw_verified;

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    String distance;

    public void setYnw_verified_level(String ynw_verified_level) {
        this.ynw_verified_level = ynw_verified_level;
    }

    String ynw_verified_level;

    public String getYnw_verified_level() {
        return ynw_verified_level;
    }

    public String getParking_type_location1() {
        return parking_type_location1;
    }

    public void setParking_type_location1(String parking_type_location1) {
        this.parking_type_location1 = parking_type_location1;
    }

    public String getFirstaid_location1() {
        return firstaid_location1;
    }

    public void setFirstaid_location1(String firstaid_location1) {
        this.firstaid_location1 = firstaid_location1;
    }

    public String getTraumacentre_location1() {
        return traumacentre_location1;
    }

    public void setTraumacentre_location1(String traumacentre_location1) {
        this.traumacentre_location1 = traumacentre_location1;
    }

    public String getDocambulance_location1() {
        return docambulance_location1;
    }

    public void setDocambulance_location1(String docambulance_location1) {
        this.docambulance_location1 = docambulance_location1;
    }

    public String getAlways_open_location1() {
        return always_open_location1;
    }

    public void setAlways_open_location1(String always_open_location1) {
        this.always_open_location1 = always_open_location1;
    }

    public String getPhysiciansemergencyservices_location1() {
        return physiciansemergencyservices_location1;
    }

    public void setPhysiciansemergencyservices_location1(String physiciansemergencyservices_location1) {
        this.physiciansemergencyservices_location1 = physiciansemergencyservices_location1;
    }

    public String getDentistemergencyservices_location1() {
        return dentistemergencyservices_location1;
    }

    public void setDentistemergencyservices_location1(String dentistemergencyservices_location1) {
        this.dentistemergencyservices_location1 = dentistemergencyservices_location1;
    }

    String physiciansemergencyservices_location1;
    String dentistemergencyservices_location1;



    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    String qualification;

    public String getSpecialization_displayname() {
        return specialization_displayname;
    }

    public void setSpecialization_displayname(String specialization_displayname) {
        this.specialization_displayname = specialization_displayname;
    }

    String specialization_displayname;

    public String getShow_waiting_time() {
        return show_waiting_time;
    }

    public void setShow_waiting_time(String show_waiting_time) {
        this.show_waiting_time = show_waiting_time;
    }

    public int getQueueWaitingTime() {
        return queueWaitingTime;
    }

    public void setQueueWaitingTime(int queueWaitingTime) {
        this.queueWaitingTime = queueWaitingTime;
    }

    public String getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(String serviceTime) {
        this.serviceTime = serviceTime;
    }

    String show_waiting_time;
    int queueWaitingTime;
    private String serviceTime;

    public int getPersonAhead() {
        return personAhead;
    }


    public String getCalculationMode() {
        return calculationMode;
    }

    public void setCalculationMode(String calculationMode) {
        this.calculationMode = calculationMode;
    }

    String calculationMode;

    public void setPersonAhead(int personAhead) {
        this.personAhead = personAhead;
    }

    int personAhead;

    public ArrayList getServices() {
        return services;
    }

    public void setServices(ArrayList services) {
        this.services = services;
    }

    private ArrayList services;

    public String getFuture_checkins() {
        return future_checkins;
    }

    public void setFuture_checkins(String future_checkins) {
        this.future_checkins = future_checkins;
    }

    public String getOnline_checkins() {
        return online_checkins;
    }

    public void setOnline_checkins(String online_checkins) {
        this.online_checkins = online_checkins;
    }

    private String future_checkins;
    private String online_checkins;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogo() {
        return Logo;
    }

    public void setLogo(String logo) {
        Logo = logo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getPlace1() {
        return place1;
    }

    public void setPlace1(String place1) {
        this.place1 = place1;
    }

    public String getUniqueid() {
        return uniqueid;
    }

    public void setUniqueid(String uniqueid) {
        this.uniqueid = uniqueid;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getQId() {
        return QId;
    }

    public void setQId(String QId) {
        this.QId = QId;
    }

    public String getmLoc() {
        return mLoc;
    }

    public void setmLoc(String mLoc) {
        this.mLoc = mLoc;
    }

    public String getAvail_date() {
        return avail_date;
    }

    public void setAvail_date(String avail_date) {
        this.avail_date = avail_date;
    }

    public ArrayList getBusiness_hours1() {
        return business_hours1;
    }

    public void setBusiness_hours1(ArrayList business_hours1) {
        this.business_hours1 = business_hours1;
    }

    private ArrayList business_hours1;

    public boolean isIsopen() {
        return isopen;
    }

    public void setIsopen(boolean isopen) {
        this.isopen = isopen;
    }

    boolean isopen;




}


