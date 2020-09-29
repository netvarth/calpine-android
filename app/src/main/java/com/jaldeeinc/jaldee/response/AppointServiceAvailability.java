package com.jaldeeinc.jaldee.response;

public class AppointServiceAvailability {

    private String nextAvailableDate;
    private String nextAvailable;
    private int id;

    public AppointServiceAvailability(String nextAvailableDate, String nextAvailable, int id) {
        this.nextAvailableDate = nextAvailableDate;
        this.nextAvailable = nextAvailable;
        this.id = id;
    }

    public AppointServiceAvailability() {
    }

    public String getNextAvailableDate() {
        return nextAvailableDate;
    }

    public void setNextAvailableDate(String nextAvailableDate) {
        this.nextAvailableDate = nextAvailableDate;
    }

    public String getNextAvailable() {
        return nextAvailable;
    }

    public void setNextAvailable(String nextAvailable) {
        this.nextAvailable = nextAvailable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
