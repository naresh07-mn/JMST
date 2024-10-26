package com.travel_track.solution.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BillingModel {

    @SerializedName("CompanyId")
    @Expose
    private Integer companyId;
    @SerializedName("BillPrgDutyDetail")
    @Expose
    private String billPrgDutyDetail;
    @SerializedName("BillPrgDutyRate")
    @Expose
    private String billPrgDutyRate;
    @SerializedName("BillPrgAmount")
    @Expose
    private String billPrgAmount;
    @SerializedName("TotalBillPrg")
    @Expose
    private String totalBillPrg;
    @SerializedName("TotalBillPrgText")
    @Expose
    private String totalBillPrgText;
    @SerializedName("TotalBillAmount")
    @Expose
    private String totalBillAmount;
    @SerializedName("billno")
    @Expose
    private String billno;
    @SerializedName("billdate")
    @Expose
    private String billdate;
    @SerializedName("guest")
    @Expose
    private String guest;
    @SerializedName("CompanyName")
    @Expose
    private String companyName;
    @SerializedName("custname")
    @Expose
    private String custname;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("DriverName")
    @Expose
    private String driverName;
    @SerializedName("carname")
    @Expose
    private String carname;
    @SerializedName("BilledHour")
    @Expose
    private String billedHour;
    @SerializedName("BilledKm")
    @Expose
    private String billedKm;
    @SerializedName("StartTime")
    @Expose
    private String startTime;
    @SerializedName("CloseTime")
    @Expose
    private String closeTime;

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getBillPrgDutyDetail() {
        return billPrgDutyDetail;
    }

    public void setBillPrgDutyDetail(String billPrgDutyDetail) {
        this.billPrgDutyDetail = billPrgDutyDetail;
    }

    public String getBillPrgDutyRate() {
        return billPrgDutyRate;
    }

    public void setBillPrgDutyRate(String billPrgDutyRate) {
        this.billPrgDutyRate = billPrgDutyRate;
    }

    public String getBillPrgAmount() {
        return billPrgAmount;
    }

    public void setBillPrgAmount(String billPrgAmount) {
        this.billPrgAmount = billPrgAmount;
    }

    public String getTotalBillPrg() {
        return totalBillPrg;
    }

    public void setTotalBillPrg(String totalBillPrg) {
        this.totalBillPrg = totalBillPrg;
    }

    public String getTotalBillPrgText() {
        return totalBillPrgText;
    }

    public void setTotalBillPrgText(String totalBillPrgText) {
        this.totalBillPrgText = totalBillPrgText;
    }

    public String getTotalBillAmount() {
        return totalBillAmount;
    }

    public void setTotalBillAmount(String totalBillAmount) {
        this.totalBillAmount = totalBillAmount;
    }

    public String getBillno() {
        return billno;
    }

    public void setBillno(String billno) {
        this.billno = billno;
    }

    public String getBilldate() {
        return billdate;
    }

    public void setBilldate(String billdate) {
        this.billdate = billdate;
    }

    public String getGuest() {
        return guest;
    }

    public void setGuest(String guest) {
        this.guest = guest;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCustname() {
        return custname;
    }

    public void setCustname(String custname) {
        this.custname = custname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getCarname() {
        return carname;
    }

    public void setCarname(String carname) {
        this.carname = carname;
    }

    public String getBilledHour() {
        return billedHour;
    }

    public void setBilledHour(String billedHour) {
        this.billedHour = billedHour;
    }

    public String getBilledKm() {
        return billedKm;
    }

    public void setBilledKm(String billedKm) {
        this.billedKm = billedKm;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

}