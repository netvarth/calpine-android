package com.jaldeeinc.jaldee.response;

import java.io.Serializable;

/**
 * Created by sharmila on 31/7/18.
 */

public class SearchTerminology implements Serializable {

    String customer;
    String provider;
    String waitlist;
    String waitlisted;
    String arrived;
    String start;
    String cancelled;
    String done;


    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getWaitlist() {
        return waitlist;
    }

    public void setWaitlist(String waitlist) {
        this.waitlist = waitlist;
    }

    public String getWaitlisted() {
        return waitlisted;
    }

    public void setWaitlisted(String waitlisted) {
        this.waitlisted = waitlisted;
    }

    public String getArrived() {
        return arrived;
    }

    public void setArrived(String arrived) {
        this.arrived = arrived;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getCancelled() {
        return cancelled;
    }

    public void setCancelled(String cancelled) {
        this.cancelled = cancelled;
    }

    public String getDone() {
        return done;
    }

    public void setDone(String done) {
        this.done = done;
    }


}
