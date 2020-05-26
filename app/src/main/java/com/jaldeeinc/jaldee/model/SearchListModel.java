package com.jaldeeinc.jaldee.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sharmila on 25/7/18.
 */

public class SearchListModel implements Serializable {
    private boolean is_SpecOpen = false;
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
    private String claimable;

    public String getJdn() {
        return jdn;
    }

    public void setJdn(String jdn) {
        this.jdn = jdn;
    }

    private String jdn;
    private int coupon_enabled;
    String today_appt;
    String future_appt;
    private String department_code;
    ArrayList gallery_thumb_nails;
    String parking_location1;
    String sectorname;
    String account_type;
    String branch_name;
    String sub_sector;
    String location1;
    int ynw_verified;
    String distance;
    String ynw_verified_level;
    String physiciansemergencyservices_location1;
    String dentistemergencyservices_location1;
    String qualification;
    ArrayList specialization_displayname;
    String show_waiting_time;
    int queueWaitingTime;
    private String message;
    boolean showToken;
    boolean onlineCheckIn;
    boolean futureWaitlist;
    boolean isAvailableToday;
    int branchSpCount;
    private String serviceTime;
    int personAhead;
    String calculationMode;
    private String first_checkin_coupon_count;

    private String future_checkins;
    private String online_checkins;
    private ArrayList business_hours1;
    ArrayList terminologies;
    boolean isopen;
    private ArrayList services;
    private ArrayList Departments;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    String url;


    private String departmentName;

    public boolean isIs_SpecOpen() {
        return is_SpecOpen;
    }

    public void setIs_SpecOpen(boolean is_SpecOpen) {
        this.is_SpecOpen = is_SpecOpen;
    }

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

    public String getSectorname() {
        return sectorname;
    }

    public void setSectorname(String sectorname) {
        this.sectorname = sectorname;
    }

    public String getClaimable() {
        return claimable;
    }

    public void setClaimable(String claimable) {
        this.claimable = claimable;
    }

    public String getAccountType() {
        return account_type;
    }

    public void setAccountType(String account_type) {
        this.account_type = account_type;
    }

    public String getBranch_name() {
        return branch_name;
    }

    public void setBranch_name(String branch_name) {
        this.branch_name = branch_name;
    }

    public int getCoupon_enabled() {
        return coupon_enabled;
    }

    public void setCoupon_enabled(int coupon_enabled) {
        this.coupon_enabled = coupon_enabled;
    }

    public String getToday_appt() {
        return today_appt;
    }

    public void setToday_appt(String today_appt) {
        this.today_appt = today_appt;
    }

    public String getFuture_appt() {
        return future_appt;
    }

    public void setFuture_appt(String future_appt) {
        this.future_appt = future_appt;
    }

    public String getSub_sector() {
        return sub_sector;
    }

    public void setSub_sector(String sub_sector) {
        this.sub_sector = sub_sector;
    }

    public String getLocation1() {
        return location1;
    }

    public void setLocation1(String location1) {
        this.location1 = location1;
    }

    public int getYnw_verified() {
        return ynw_verified;
    }

    public void setYnw_verified(int ynw_verified) {
        this.ynw_verified = ynw_verified;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setYnw_verified_level(String ynw_verified_level) {
        this.ynw_verified_level = ynw_verified_level;
    }

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

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public ArrayList getSpecialization_displayname() {
        return specialization_displayname;
    }

    public void setSpecialization_displayname(ArrayList specialization_displaynames) {
        this.specialization_displayname = specialization_displaynames;
    }

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

    public int getBranchSpCount() {
        return branchSpCount;
    }

    public void setBranchSpCount(int branchSpCount) {
        this.branchSpCount = branchSpCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isOnlineCheckIn() {
        return onlineCheckIn;
    }

    public void setOnlineCheckIn(boolean onlineCheckIn) {
        this.onlineCheckIn = onlineCheckIn;
    }

    public boolean isFutureWaitlist() {
        return futureWaitlist;
    }

    public void setFutureWaitlist(boolean futureWaitlist) {
        this.futureWaitlist = futureWaitlist;
    }

    public boolean isAvailableToday() {
        return isAvailableToday;
    }

    public void setAvailableToday(boolean availableToday) {
        isAvailableToday = availableToday;
    }

    public boolean isShowToken() {
        return showToken;
    }

    public void setShowToken(boolean showToken) {
        this.showToken = showToken;
    }

    public int getPersonAhead() {
        return personAhead;
    }

    public void setPersonAhead(int personAhead) {
        this.personAhead = personAhead;
    }

    public String getCalculationMode() {
        return calculationMode;
    }

    public void setCalculationMode(String calculationMode) {
        this.calculationMode = calculationMode;
    }

    public ArrayList getServices() {
        return services;
    }

    public void setServices(ArrayList services) {
        this.services = services;
    }

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

    public String getFirst_checkin_coupon_count() {
        return first_checkin_coupon_count;
    }

    public void setFirst_checkin_coupon_count(String first_checkin_coupon_count) {
        this.first_checkin_coupon_count = first_checkin_coupon_count;
    }

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

    public ArrayList getTerminologies() {
        return terminologies;
    }

    public void setTerminologies(ArrayList terminologies) {
        this.terminologies = terminologies;
    }

    public boolean isIsopen() {
        return isopen;
    }

    public void setIsopen(boolean isopen) {
        this.isopen = isopen;
    }

    public String getDepartment_code() {
        return department_code;
    }

    public void setDepartment_code(String department_code) {
        this.department_code = department_code;
    }

    public ArrayList getDepartments() {
        return Departments;
    }

    public void setDepartments(ArrayList departments) {
        Departments = departments;
    }


    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
