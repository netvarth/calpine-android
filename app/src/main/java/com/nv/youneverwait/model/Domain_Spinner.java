package com.nv.youneverwait.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sharmila on 16/7/18.
 */

public class Domain_Spinner {
  /*  public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    String domain;
    @Override
    public String toString() {
        return domain;
    }

    public Domain_Spinner(String domainname) {
        this.domain = domainname;

    }*/

    String domain;



    public String getSector() {
        return domain;
    }

    public void setSector(String sector) {
        this.domain = sector;
    }



    public String getDomain() {
        return displayName;
    }

    public void setDomain(String domain) {
        this.displayName = domain;
    }


    @Override
    public String toString() {
        return displayName;
    }

    public Domain_Spinner(String domainname,String domainchk) {
        this.displayName = domainname;
        this.domain=domainchk;

    }
    public Domain_Spinner() {

    }

    public ArrayList<Domain_Spinner> getSubDomains() {
        return subDomains;
    }

    public void setSubDomains(ArrayList<Domain_Spinner> subDomains) {
        this.subDomains = subDomains;
    }

    @SerializedName("subDomains")
    ArrayList<Domain_Spinner> subDomains;

    public String getSubDomain() {
        return subDomain;
    }

    public void setSubDomain(String subDomain) {
        this.subDomain = subDomain;
    }

    String subDomain;


    public ArrayList<Domain_Spinner> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(ArrayList<Domain_Spinner> specializations) {
        this.specializations = specializations;
    }

    @SerializedName("specializations")
    ArrayList<Domain_Spinner> specializations;

    public String getName() {
        return displayName;
    }

    public void setName(String name) {
        this.displayName = name;
    }

    String displayName;


    public String getSubDomain_DisplayNAme() {
        return subDomain_DisplayNAme;
    }

    public void setSubDomain_DisplayNAme(String subDomain_DisplayNAme) {
        this.subDomain_DisplayNAme = subDomain_DisplayNAme;
    }

    String subDomain_DisplayNAme;

    String name;

    public String getSpecilicationANme() {
        return name;
    }

    public void setSpecilicationANme(String subDomain_DisplayNAme) {
        this.name = subDomain_DisplayNAme;
    }


}
