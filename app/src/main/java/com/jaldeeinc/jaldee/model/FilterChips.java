package com.jaldeeinc.jaldee.model;

import android.view.View;

import androidx.annotation.Nullable;

public class FilterChips {

    private String name;
    private String value;
    private String type;
    private View view;
    private boolean deletable = true;

    public FilterChips() {

    }

    public FilterChips(String value, String type) {
        this.value = value;
        this.type = type;
    }

    public FilterChips(String name, String value, String type, View view, boolean deletable) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.view = view;
        this.deletable = deletable;
    }


    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}
