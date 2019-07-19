package com.jaldeeinc.jaldee.response;

/**
 * Created by sharmila on 31/7/18.
 */

public class SearchSetting {
    //Settings

    boolean onlineCheckIns;
    boolean futureDateWaitlist;
    String calculationMode;

    public boolean isOnlineCheckIns() {
        return onlineCheckIns;
    }

    public void setOnlineCheckIns(boolean onlineCheckIns) {
        this.onlineCheckIns = onlineCheckIns;
    }

    public boolean isFutureDateWaitlist() {
        return futureDateWaitlist;
    }

    public void setFutureDateWaitlist(boolean futureDateWaitlist) {
        this.futureDateWaitlist = futureDateWaitlist;
    }

    public String getCalculationMode() {
        return calculationMode;
    }

    public void setCalculationMode(String calculationMode) {
        this.calculationMode = calculationMode;
    }

}
