package com.jaldeeinc.jaldee.model;

import java.io.Serializable;

public class Schedule implements Serializable {

    int id;
    String timeDuration;
    String parallelServing;
    boolean batchEnable;
    boolean todayAppt;
    boolean futureAppt;
    boolean instantSchedule;
    boolean openNow;

    public int getId() {
        return id;
    }

    public String getTimeDuration() {
        return timeDuration;
    }

    public String getParallelServing() {
        return parallelServing;
    }

    public boolean isBatchEnable() {
        return batchEnable;
    }

    public boolean isTodayAppt() {
        return todayAppt;
    }

    public boolean isFutureAppt() {
        return futureAppt;
    }

    public boolean isInstantSchedule() {
        return instantSchedule;
    }

    public boolean isOpenNow() {
        return openNow;
    }
}
