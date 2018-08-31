package com.netvarth.youneverwait.model;

/**
 * Created by sharmila on 19/7/18.
 */

public class LanLong {

    public double getUpperLeftLat() {
        return upperLeftLat;
    }

    public void setUpperLeftLat(double upperLeftLat) {
        this.upperLeftLat = upperLeftLat;
    }

    public double getUpperLeftLon() {
        return upperLeftLon;
    }

    public void setUpperLeftLon(double upperLeftLon) {
        this.upperLeftLon = upperLeftLon;
    }

    public double getLowerRightLat() {
        return lowerRightLat;
    }

    public void setLowerRightLat(double lowerRightLat) {
        this.lowerRightLat = lowerRightLat;
    }

    public double getLowerRightLon() {
        return lowerRightLon;
    }

    public void setLowerRightLon(double lowerRightLon) {
        this.lowerRightLon = lowerRightLon;
    }

    double upperLeftLat ;
    double upperLeftLon ;
    double lowerRightLat;
    double lowerRightLon;
}
