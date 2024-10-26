package com.travel_track.solution.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class BookingModel implements Serializable {

    @SerializedName("CompanyId")
    @Expose
    private Integer companyId;
    @SerializedName("companyName")
    @Expose
    private String companyName;
    @SerializedName("BookingNo")
    @Expose
    private String bookingNo;
    @SerializedName("BKGNo")
    @Expose
    private String bKGNo;
    @SerializedName("DSNo")
    @Expose
    private String dSNo;
    @SerializedName("ClientCompanyName")
    @Expose
    private String clientCompanyName;
    @SerializedName("PickUpLocation")
    @Expose
    private String pickUpLocation;
    @SerializedName("GuestName")
    @Expose
    private String guestName;
    @SerializedName("GuestMobileNo")
    @Expose
    private String guestMobileNo;
    @SerializedName("VehicleType")
    @Expose
    private String vehicleType;
    @SerializedName("CarNo")
    @Expose
    private String carNo;
    @SerializedName("DropAddress")
    @Expose
    private String dropAddress;
    @SerializedName("PickUpDate")
    @Expose
    private String pickUpDate;
    @SerializedName("PickUpTime")
    @Expose
    private String pickUpTime;
    @SerializedName("CustomerName")
    @Expose
    private Object customerName;
    @SerializedName("StartMeter")
    @Expose
    private String startMeter;
    @SerializedName("BookingStatus")
    @Expose
    private String bookingStatus;
    @SerializedName("PickUpLatitude")
    @Expose
    private String pickUpLatitude;
    @SerializedName("DropLocationLatitude")
    @Expose
    private String dropLocationLatitude;
    @SerializedName("PickUpLongitude")
    @Expose
    private String pickUpLongitude;
    @SerializedName("DroplocationLongitude")
    @Expose
    private String droplocationLongitude;
    @SerializedName("EmployeeId")
    @Expose
    private String employeeId;
    @SerializedName("FileCode")
    @Expose
    private String fileCode;
    @SerializedName("PayMentMode")
    @Expose
    private String payMentMode;
    @SerializedName("DutyStartDate")
    @Expose
    private String dutyStartDate;
    @SerializedName("DutyCloseDate")
    @Expose
    private String dutyCloseDate;

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBookingNo() {
        return bookingNo;
    }

    public void setBookingNo(String bookingNo) {
        this.bookingNo = bookingNo;
    }

    public String getBKGNo() {
        return bKGNo;
    }

    public void setBKGNo(String bKGNo) {
        this.bKGNo = bKGNo;
    }

    public String getDSNo() {
        return dSNo;
    }

    public void setDSNo(String dSNo) {
        this.dSNo = dSNo;
    }

    public String getClientCompanyName() {
        return clientCompanyName;
    }

    public void setClientCompanyName(String clientCompanyName) {
        this.clientCompanyName = clientCompanyName;
    }

    public String getPickUpLocation() {
        return pickUpLocation;
    }

    public void setPickUpLocation(String pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestMobileNo() {
        return guestMobileNo;
    }

    public void setGuestMobileNo(String guestMobileNo) {
        this.guestMobileNo = guestMobileNo;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getDropAddress() {
        return dropAddress;
    }

    public void setDropAddress(String dropAddress) {
        this.dropAddress = dropAddress;
    }

    public String getPickUpDate() {
        return pickUpDate;
    }

    public void setPickUpDate(String pickUpDate) {
        this.pickUpDate = pickUpDate;
    }

    public String getPickUpTime() {
        return pickUpTime;
    }

    public void setPickUpTime(String pickUpTime) {
        this.pickUpTime = pickUpTime;
    }

    public Object getCustomerName() {
        return customerName;
    }

    public void setCustomerName(Object customerName) {
        this.customerName = customerName;
    }

    public String getStartMeter() {
        return startMeter;
    }

    public void setStartMeter(String startMeter) {
        this.startMeter = startMeter;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getPickUpLatitude() {
        return pickUpLatitude;
    }

    public void setPickUpLatitude(String pickUpLatitude) {
        this.pickUpLatitude = pickUpLatitude;
    }

    public String getDropLocationLatitude() {
        return dropLocationLatitude;
    }

    public void setDropLocationLatitude(String dropLocationLatitude) {
        this.dropLocationLatitude = dropLocationLatitude;
    }

    public String getPickUpLongitude() {
        return pickUpLongitude;
    }

    public void setPickUpLongitude(String pickUpLongitude) {
        this.pickUpLongitude = pickUpLongitude;
    }

    public String getDroplocationLongitude() {
        return droplocationLongitude;
    }

    public void setDroplocationLongitude(String droplocationLongitude) {
        this.droplocationLongitude = droplocationLongitude;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getFileCode() {
        return fileCode;
    }

    public void setFileCode(String fileCode) {
        this.fileCode = fileCode;
    }

    public String getPayMentMode() {
        return payMentMode;
    }

    public void setPayMentMode(String payMentMode) {
        this.payMentMode = payMentMode;
    }

    public String getDutyStartDate() {
        return dutyStartDate;
    }

    public void setDutyStartDate(String dutyStartDate) {
        this.dutyStartDate = dutyStartDate;
    }

    public String getDutyCloseDate() {
        return dutyCloseDate;
    }

    public void setDutyCloseDate(String dutyCloseDate) {
        this.dutyCloseDate = dutyCloseDate;
    }
}