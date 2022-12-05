package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sharmila on 21/11/18.
 */

public class SearchVirtualFields {
    String name;
    String displayName;

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDataType() {
        return dataType;
    }

    public ArrayList getDomain() {
        return domain;
    }

    public ArrayList getSubdomain() {
        return subdomain;
    }

    String dataType;
    @SerializedName("domain")
    ArrayList domain;
    @SerializedName("subdomain")
    ArrayList subdomain;

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    String head;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    String result;
    String type;
}
