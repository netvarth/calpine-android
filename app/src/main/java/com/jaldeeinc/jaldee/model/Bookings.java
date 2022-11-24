package com.jaldeeinc.jaldee.model;

import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;

import java.io.Serializable;
import java.util.ArrayList;

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
    private String amountPaid;
    private boolean hasAttachment;
    private ActiveAppointment appointmentInfo;
    private ActiveCheckIn checkInInfo;
    String videoCallButton;
    String videoCallMessage;
    boolean isRescheduled;
    String deptName;
    boolean isDate;
    boolean isDateTime;
    boolean isNoDateTime;
    ArrayList<RlsdQnr> releasedQnr;
    String uniqueId;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public boolean isDate() {
        return isDate;
    }

    public void setIsDate(boolean date) {
        isDate = date;
    }

    public boolean isDateTime() {
        return isDateTime;
    }

    public void setIsDateTime(boolean dateTime) {
        isDateTime = dateTime;
    }

    public boolean isNoDateTime() {
        return isNoDateTime;
    }

    public void setIsNoDateTime(boolean noDateTime) {
        isNoDateTime = noDateTime;
    }

    public ArrayList<RlsdQnr> getReleasedQnr() {
        return releasedQnr;
    }

    public void setReleasedQnr(ArrayList<RlsdQnr> releasedQnr) {
        this.releasedQnr = releasedQnr;
    }

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

    public boolean isRescheduled() {
        return isRescheduled;
    }

    public void setRescheduled(boolean rescheduled) {
        isRescheduled = rescheduled;
    }

    public String getVideoCallButton() {
        return videoCallButton;
    }

    public String getVideoCallMessage() {
        return videoCallMessage;
    }

    public void setVideoCallButton(String videoCallButton) {
        this.videoCallButton = videoCallButton;
    }

    public void setVideoCallMessage(String videoCallMessage) {
        this.videoCallMessage = videoCallMessage;
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

    public String getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(String amountPaid) {
        this.amountPaid = amountPaid;
    }

    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
}
