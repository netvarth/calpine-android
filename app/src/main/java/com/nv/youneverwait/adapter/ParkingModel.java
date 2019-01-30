package com.nv.youneverwait.adapter;

import com.nv.youneverwait.model.WorkingModel;

import java.util.ArrayList;

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

    public ArrayList<WorkingModel> getWorkingModelArrayList() {
        return workingModelArrayList;
    }

    public void setWorkingModelArrayList(ArrayList<WorkingModel> workingModelArrayList) {
        this.workingModelArrayList = workingModelArrayList;
    }

    ArrayList<WorkingModel> workingModelArrayList=new ArrayList<>();
}
