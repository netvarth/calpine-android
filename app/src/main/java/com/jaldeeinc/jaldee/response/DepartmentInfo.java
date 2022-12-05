package com.jaldeeinc.jaldee.response;


import com.jaldeeinc.jaldee.adapter.Section;

import java.util.ArrayList;

public class DepartmentInfo implements Section<DepServiceInfo> {

    ArrayList<DepServiceInfo> deptServicesList;
    String departmentName;

    public DepartmentInfo(ArrayList<DepServiceInfo> deptServicesList, String departmentName) {
        this.deptServicesList = deptServicesList;
        this.departmentName = departmentName;
    }

    public DepartmentInfo() {
    }

    public void setDeptServicesList(ArrayList<DepServiceInfo> deptServicesList) {
        this.deptServicesList = deptServicesList;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    @Override
    public ArrayList<DepServiceInfo> getChildItems() {
        return deptServicesList;
    }
}

