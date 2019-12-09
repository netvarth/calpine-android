package com.jaldeeinc.jaldee.response;

public class ShareLocation {

    public String getPollingTime() {
        return pollingTime;
    }

    public void setPollingTime(String pollingTime) {
        this.pollingTime = pollingTime;
    }

    public JaldeeDistanceTime getJaldeeDistanceTime() {
        return jaldeeDistanceTime;
    }

    public void setJaldeeDistanceTime(JaldeeDistanceTime jaldeeDistanceTime) {
        this.jaldeeDistanceTime = jaldeeDistanceTime;
    }

    String pollingTime;

    JaldeeDistanceTime jaldeeDistanceTime;
}
