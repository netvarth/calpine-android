package com.jaldeeinc.jaldee.response;

import java.util.ArrayList;

public class RefundDetails {
    public ArrayList<RefundInfo> getRefundDetails() {
        return refundDetails;
    }

    public void setRefundDetails(ArrayList<RefundInfo> refundDetails) {
        this.refundDetails = refundDetails;
    }

    private ArrayList<RefundInfo> refundDetails;
}
