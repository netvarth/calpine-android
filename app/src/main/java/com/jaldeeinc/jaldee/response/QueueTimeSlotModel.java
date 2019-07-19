package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sharmila on 8/8/18.
 */

public class QueueTimeSlotModel {
    int id;
    String name;
    @SerializedName("queueSchedule")
    QueueTimeSlotModel queueSchedule;
    ArrayList <QueueTimeSlotModel> timeSlots;
    String sTime;
    String eTime;

    public String getStartDate() {
        return startDate;
    }

    String startDate;

    public QueueTimeSlotModel getEffectiveSchedule() {

        return effectiveSchedule;
    }

    QueueTimeSlotModel effectiveSchedule;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public QueueTimeSlotModel getQueueSchedule() {
        return queueSchedule;
    }

    public void setQueueSchedule(QueueTimeSlotModel queueSchedule) {
        this.queueSchedule = queueSchedule;
    }

    public ArrayList<QueueTimeSlotModel> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(ArrayList<QueueTimeSlotModel> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public String getsTime() {
        return sTime;
    }

    public void setsTime(String sTime) {
        this.sTime = sTime;
    }

    public String geteTime() {
        return eTime;
    }

    public void seteTime(String eTime) {
        this.eTime = eTime;
    }



    public String getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(String serviceTime) {
        this.serviceTime = serviceTime;
    }

    public int getQueueWaitingTime() {
        return queueWaitingTime;
    }

    public void setQueueWaitingTime(int queueWaitingTime) {
        this.queueWaitingTime = queueWaitingTime;
    }

    int queueWaitingTime;
    String serviceTime;

    public String getCalculationMode() {
        return calculationMode;
    }

    public void setCalculationMode(String calculationMode) {
        this.calculationMode = calculationMode;
    }

    String calculationMode;

    public int getQueueSize() {
        return queueSize;
    }

    int queueSize;
}
