package com.jaldeeinc.jaldee.model;

import com.jaldeeinc.jaldee.adapter.Section;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.DepServiceInfo;

import java.util.ArrayList;
import java.util.List;

public class MyBookingsInfo implements Section<ActiveAppointment> {

    ArrayList<ActiveAppointment> deptServicesList;
    String departmentName;

    public MyBookingsInfo(){

    }

    public MyBookingsInfo(ArrayList<ActiveAppointment> deptServicesList, String departmentName) {
        this.deptServicesList = deptServicesList;
        this.departmentName = departmentName;
    }


    @Override
    public List<ActiveAppointment> getChildItems() {
        return null;
    }

    public ArrayList<ActiveAppointment> getDeptServicesList() {
        return deptServicesList;
    }

    public void setDeptServicesList(ArrayList<ActiveAppointment> deptServicesList) {
        this.deptServicesList = deptServicesList;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
