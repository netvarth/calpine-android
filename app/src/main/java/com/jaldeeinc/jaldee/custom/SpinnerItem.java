package com.jaldeeinc.jaldee.custom;

public class SpinnerItem {

    int id;
    String value;
    boolean selected = false;

    public SpinnerItem(){

    }

    public SpinnerItem(int id, String value, boolean selected) {
        this.id = id;
        this.value = value;
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
