package com.jaldeeinc.jaldee.response;

/**
 * Created by sharmila on 31/7/18.
 */

public class SearchSetting {
    //Settings

    boolean onlineCheckIns;
    boolean futureDateWaitlist;
    boolean filterByDept;
    boolean showTokenId;
    String calculationMode;

    public boolean isFilterByDept() {
        return filterByDept;
    }
    public void setFilterByDept(boolean filterByDept) {
        this.filterByDept = filterByDept;
    }
    public boolean isShowTokenId() {
        return showTokenId;
    }
    public void setShowTokenId(boolean showTokenId) {
        this.showTokenId = showTokenId;
    }
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
