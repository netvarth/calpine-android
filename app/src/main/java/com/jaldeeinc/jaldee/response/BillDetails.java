package com.jaldeeinc.jaldee.response;

public class BillDetails {
    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    double amountPaid;

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    String billId;

    public String getBillPaymentStatus() {
        return billPaymentStatus;
    }

    public void setBillPaymentStatus(String billPaymentStatus) {
        this.billPaymentStatus = billPaymentStatus;
    }

    String billPaymentStatus;

    public String getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(String billStatus) {
        this.billStatus = billStatus;
    }

    String billStatus;

    public String getBillViewStatus() {
        return billViewStatus;
    }

    public void setBillViewStatus(String billViewStatus) {
        this.billViewStatus = billViewStatus;
    }

    String billViewStatus;
}
