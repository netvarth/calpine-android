package com.nv.youneverwait.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sharmila on 16/7/18.
 */

public class Domain_Spinner {

    String domain;



    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }


    @Override
    public String toString() {
        return displayName;
    }

    public Domain_Spinner(String displayname,String domain) {
        this.displayName = displayname;
        this.domain=domain;

    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    String displayName;

}
