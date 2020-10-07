package com.jaldeeinc.jaldee.response;

import java.io.Serializable;

public class JaldeeDistance implements Serializable {

Integer distance;
String unit;

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
