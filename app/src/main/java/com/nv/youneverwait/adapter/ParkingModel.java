package com.nv.youneverwait.adapter;

/**
 * Created by sharmila on 10/9/18.
 */

class ParkingModel {
    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    private String typename;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
}
