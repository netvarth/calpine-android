package com.jaldeeinc.jaldee.response;

/**
 * Created by sharmila on 15/10/18.
 */

public class SectorCheckin {

    public boolean isPartySize() {
        return partySize;
    }

    public boolean isPartySizeForCalculation() {
        return partySizeForCalculation;
    }

    public int getMaxPartySize() {
        return maxPartySize;
    }

    boolean partySize;
    boolean partySizeForCalculation;
    int maxPartySize;
}
