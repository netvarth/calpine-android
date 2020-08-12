package com.jaldeeinc.jaldee.model;

import java.io.Serializable;

/**
 * Created by sharmila on 13/12/18.
 */

public class SocialMediaModel implements Serializable {

    String resource;

    public String getResource() {
        return resource;
    }

    public String getValue() {
        return value;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public void setValue(String value) {
        this.value = value;
    }

    String value;
}
