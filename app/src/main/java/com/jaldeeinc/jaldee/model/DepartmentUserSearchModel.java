package com.jaldeeinc.jaldee.model;

import com.jaldeeinc.jaldee.response.QueueList;
import com.jaldeeinc.jaldee.response.ScheduleList;
import com.jaldeeinc.jaldee.response.SearchLocation;
import com.jaldeeinc.jaldee.response.SearchService;
import com.jaldeeinc.jaldee.response.SearchSetting;
import com.jaldeeinc.jaldee.response.SearchViewDetail;

import java.io.Serializable;
import java.util.List;

public class DepartmentUserSearchModel implements Serializable {
    private QueueList queueList;
    private ScheduleList scheduleList;
    private SearchViewDetail searchViewDetail;
    private SearchViewDetail parentSearchViewDetail;
    private SearchLocation location;
    private List<SearchService> services;
    private SearchSetting settings;

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    String departmentId;

    public SearchViewDetail getParentSearchViewDetail() {
        return parentSearchViewDetail;
    }

    public void setParentSearchViewDetail(SearchViewDetail parentSearchViewDetail) {
        this.parentSearchViewDetail = parentSearchViewDetail;
    }

    public SearchSetting getSettings() {
        return settings;
    }

    public void setSettings(SearchSetting settings) {
        this.settings = settings;
    }

    public List<SearchService> getServices() {
        return services;
    }

    public void setServices(List<SearchService> services) {
        this.services = services;
    }

    public SearchLocation getLocation() {
        return location;
    }

    public void setLocation(SearchLocation location) {
        this.location = location;
    }

    public QueueList getQueueList() {
        return queueList;
    }

    public void setQueueList(QueueList queueList) {
        this.queueList = queueList;
    }

    public ScheduleList getScheduleList() {
        return scheduleList;
    }

    public void setScheduleList(ScheduleList scheduleList) {
        this.scheduleList = scheduleList;
    }

    public SearchViewDetail getSearchViewDetail() {
        return searchViewDetail;
    }

    public void setSearchViewDetail(SearchViewDetail searchViewDetail) {
        this.searchViewDetail = searchViewDetail;
    }
}
