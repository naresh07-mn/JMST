package com.travel_track.solution.viewmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ParkingDutyModel {


    @SerializedName("resultcode")
    @Expose
    private String resultcode;
    @SerializedName("message")
    @Expose
    private String message;

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
