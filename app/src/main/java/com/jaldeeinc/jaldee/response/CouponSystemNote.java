package com.jaldeeinc.jaldee.response;

import java.util.ArrayList;

public class CouponSystemNote {
    float value;
    ArrayList<String> systemNote = new ArrayList<String>();

    public CouponSystemNote() {
        super();
    }
    public CouponSystemNote(float value, ArrayList<String> systemNote) {
        this.value = value;
        this.systemNote = systemNote;
    }

    public float getValue() {
        return value;
    }

    public ArrayList<String> getSystemNote() {
        return systemNote;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public void setSystemNote(ArrayList<String> systemNote) {
        this.systemNote = systemNote;
    }
}
