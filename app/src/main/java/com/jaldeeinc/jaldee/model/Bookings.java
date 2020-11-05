package com.jaldeeinc.jaldee.model;

import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;

import java.io.Serializable;

public class Bookings implements Serializable {

    private String bookingId;
    private String bookingType;
    private String bookingOn;
    private String spName;
    private String providerName;
    private String serviceName;
    private boolean isVirtual;
    private String callingType;
    private boolean videoService;
    private String date;
    private String bookingStatus;
    private String serviceTime;
    private int waitingTime;
    private String calculationMode;
    private int tokenNo;
    private ActiveAppointment appointmentInfo;
    private ActiveCheckIn checkInInfo;

    public Bookings(){

    }

    public Bookings(String bookingId, String bookingType, String bookingOn, String spName, String providerName, String serviceName, boolean isVirtual, String callingType, String date, ActiveAppointment appointmentInfo, ActiveCheckIn checkInInfo) {
        this.bookingId = bookingId;
        this.bookingType = bookingType;
        this.bookingOn = bookingOn;
        this.spName = spName;
        this.providerName = providerName;
        this.serviceName = serviceName;
        this.isVirtual = isVirtual;
        this.callingType = callingType;
        this.date = date;
        this.appointmentInfo = appointmentInfo;
        this.checkInInfo = checkInInfo;
    }


    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public String getBookingOn() {
        return bookingOn;
    }

    public void setBookingOn(String bookingOn) {
        this.bookingOn = bookingOn;
    }

    public String getSpName() {
        return spName;
    }

    public void setSpName(String spName) {
        this.spName = spName;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public boolean isVirtual() {
        return isVirtual;
    }

    public void setVirtual(boolean virtual) {
        isVirtual = virtual;
    }

    public String getCallingType() {
        return callingType;
    }

    public void setCallingType(String callingType) {
        this.callingType = callingType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ActiveAppointment getAppointmentInfo() {
        return appointmentInfo;
    }

    public void setAppointmentInfo(ActiveAppointment appointmentInfo) {
        this.appointmentInfo = appointmentInfo;
    }

    public ActiveCheckIn getCheckInInfo() {
        return checkInInfo;
    }

    public void setCheckInInfo(ActiveCheckIn checkInInfo) {
        this.checkInInfo = checkInInfo;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public boolean isVideoService() {
        return videoService;
    }

    public void setVideoService(boolean videoService) {
        this.videoService = videoService;
    }

    public String getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(String serviceTime) {
        this.serviceTime = serviceTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public String getCalculationMode() {
        return calculationMode;
    }

    public void setCalculationMode(String calculationMode) {
        this.calculationMode = calculationMode;
    }

    public int getTokenNo() {
        return tokenNo;
    }

    public void setTokenNo(int tokenNo) {
        this.tokenNo = tokenNo;
    }
}
