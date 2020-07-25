package com.jaldeeinc.jaldee.model;

import com.jaldeeinc.jaldee.response.QueueList;
import com.jaldeeinc.jaldee.response.ScheduleList;
import com.jaldeeinc.jaldee.response.SearchLocation;
import com.jaldeeinc.jaldee.response.SearchViewDetail;

public class DepartmentUserSearchModel {
    private QueueList queueList;
    private ScheduleList scheduleList;
    private SearchViewDetail searchViewDetail;
    private SearchLocation location;

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
