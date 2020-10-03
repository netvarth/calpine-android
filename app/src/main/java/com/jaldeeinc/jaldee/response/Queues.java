package com.jaldeeinc.jaldee.response;

public class Queues {

    private int queueId;
    private String startTime;
    private String endTime;
    private int peopleAhead;
    private int estWaitTime;

    public Queues(int queueId, String startTime, String endTime, int peopleAhead, int estWaitTime) {
        this.queueId = queueId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.peopleAhead = peopleAhead;
        this.estWaitTime = estWaitTime;
    }

    public Queues() {
    }

    public int getQueueId() {
        return queueId;
    }

    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getPeopleAhead() {
        return peopleAhead;
    }

    public void setPeopleAhead(int peopleAhead) {
        this.peopleAhead = peopleAhead;
    }

    public int getEstWaitTime() {
        return estWaitTime;
    }

    public void setEstWaitTime(int estWaitTime) {
        this.estWaitTime = estWaitTime;
    }
}
