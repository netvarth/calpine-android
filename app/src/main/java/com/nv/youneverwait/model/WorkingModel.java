package com.nv.youneverwait.model;

import java.io.Serializable;

/**
 * Created by sharmila on 3/9/18.
 */

public class WorkingModel implements Serializable{

    String day;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime_value() {
        return time_value;
    }

    public void setTime_value(String time_value) {
        this.time_value = time_value;
    }

    String time_value;
}
