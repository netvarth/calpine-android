package com.nv.youneverwait.model;

import com.nv.youneverwait.adapter.ContactDetailAdapter;

/**
 * Created by sharmila on 3/12/18.
 */

public class ContactModel {

    String label;
    String resource;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    String instance;
    String permission;
}
