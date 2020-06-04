package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sharmila on 19/7/18.
 */

public class SearchAWsResponse {

    String parking_type_location1;
    String firstaid_location1;
    String traumacentre_location1;
    String docambulance_location1;
    String always_open_location1;
    String physiciansemergencyservices_location1;
    String dentistemergencyservices_location1;
    String hosemergencyservices_location1;

    public String getDepartment_code() {
        return department_code;
    }

    public void setDepartment_code(String department_code) {
        this.department_code = department_code;
    }

    String department_code;

    public String getParking_location1() {
        return parking_location1;
    }

    public void setParking_location1(String parking_location1) {
        this.parking_location1 = parking_location1;
    }

    int coupon_enabled;

    public int getCoupon_enabled() {
        return coupon_enabled;
    }

    public void setCoupon_enabled(int coupon_enabled) {
        this.coupon_enabled = coupon_enabled;
    }


    String parking_location1;

    public String getSub_sector() {
        return sub_sector;
    }

    public void setSub_sector(String sub_sector) {
        this.sub_sector = sub_sector;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    String sub_sector;
    String sector;

    public String getYnw_verified() {
        return ynw_verified;
    }

    public String getYnw_verified_level() {
        return ynw_verified_level;
    }

    public void setYnw_verified_level(String ynw_verified_level) {
        this.ynw_verified_level = ynw_verified_level;
    }

    String ynw_verified_level;


    public String getAccountType() {
        return account_type;
    }

    public void setAccountType(String account_type) {
        this.account_type = account_type;
    }

    String account_type;


    public String getBranch_name() {
        return branch_name;
    }

    public void setBranch_name(String branch_name) {
        this.branch_name = branch_name;
    }

    String branch_name;


    public void setYnw_verified(String ynw_verified) {
        this.ynw_verified = ynw_verified;
    }

    String ynw_verified;

    public ArrayList getGallery_thumb_nails() {
        return gallery_thumb_nails;
    }

    public void setGallery_thumb_nails(ArrayList gallery_thumb_nails) {
        this.gallery_thumb_nails = gallery_thumb_nails;
    }

    ArrayList gallery_thumb_nails;

    public String getLocation1() {
        return location1;
    }

    public void setLocation1(String location1) {
        this.location1 = location1;
    }

    String location1;

    public SearchAWsResponse getExprs() {
        return exprs;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    String distance;

    public void setExprs(SearchAWsResponse exprs) {
        this.exprs = exprs;
    }

    SearchAWsResponse exprs;


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
    public String getHosemergencyservices_location1() {
        return hosemergencyservices_location1;
    }

    public void setHosemergencyservices_location1(String hosemergencyservices_location1) {
        this.hosemergencyservices_location1 = hosemergencyservices_location1;
    }
    public void setStatus(SearchAWsResponse status) {
        this.status = status;
    }

    public void setHits(SearchAWsResponse hits) {
        this.hits = hits;
    }

    public void setHit(ArrayList<SearchAWsResponse> hit) {
        this.hit = hit;
    }

    public void setFields(SearchAWsResponse fields) {
        this.fields = fields;
    }


    public ArrayList getQualification() {
        return qualification;
    }

    public void setQualification(ArrayList qualification) {
        this.qualification = qualification;
    }

    ArrayList qualification;

    public SearchAWsResponse getStatus() {
        return status;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    String unique_id;

    @SerializedName("status")
    private SearchAWsResponse status;

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    String rid;

    public SearchAWsResponse getHits() {
        return hits;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;

    public void setHit(SearchAWsResponse hit) {
        this.hits = hit;
    }

    @SerializedName("hits")
    private SearchAWsResponse hits;

    public int getFound() {
        return found;
    }

    public void setFound(int found) {
        this.found = found;
    }

    int found;

    public ArrayList<SearchAWsResponse> getHit() {
        return hit;
    }


    @SerializedName("hit")
    private ArrayList<SearchAWsResponse> hit;


    public SearchAWsResponse getFields() {
        return fields;
    }

    @SerializedName("fields")
    private SearchAWsResponse fields;


    public ArrayList getServices() {
        return services;
    }

    public void setServices(ArrayList services) {
        this.services = services;
    }

    @SerializedName("services")
    private ArrayList services;

    public ArrayList getDepartments() {
        return departments;
    }

    public void setDepartments(ArrayList departments) {
        this.departments = departments;
    }

    @SerializedName("departments")
    private ArrayList departments;

    public ArrayList getSpecialization_displayname() {
        return specialization_displayname;
    }

    public void setSpecialization_displayname(ArrayList specialization_displayname) {
        this.specialization_displayname = specialization_displayname;
    }

    ArrayList specialization_displayname;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    String title;


    public String getSub_sector_displayname() {
        return sub_sector_displayname;
    }

    public void setSub_sector_displayname(String sub_sector_displayname) {
        this.sub_sector_displayname = sub_sector_displayname;
    }

    String sub_sector_displayname;

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    String logo;

    public String getPlace1() {
        return place1;
    }

    public void setPlace1(String place1) {
        this.place1 = place1;
    }

    String place1;


    String claimable;

    public String getJdn() {
        return jdn;
    }

    public void setJdn(String jdn) {
        this.jdn = jdn;
    }

    String jdn;

    public String getClaimable() {
        return claimable;
    }

    public void setClaimable(String claimable) {
        this.claimable = claimable;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    String rating;

    public ArrayList getTerminologies() {
        return terminologies;
    }

    public void setTerminologies(ArrayList terminologies) {
        this.terminologies = terminologies;
    }

    ArrayList terminologies;

    public ArrayList getBusiness_hours1() {
        return business_hours1;
    }

    public void setBusiness_hours1(ArrayList business_hours1) {
        this.business_hours1 = business_hours1;
    }

    ArrayList business_hours1;

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

    String future_checkins;
    String online_checkins;

    public String getShow_waiting_time() {
        return show_waiting_time;
    }

    public void setShow_waiting_time(String show_waiting_time) {
        this.show_waiting_time = show_waiting_time;
    }

    String show_waiting_time;

    public String getFirst_checkin_coupon_count() {
        return first_checkin_coupon_count;
    }

    public void setFirst_checkin_coupon_count(String first_checkin_coupon_count) {
        this.first_checkin_coupon_count = first_checkin_coupon_count;
    }

    private String first_checkin_coupon_count;


}


