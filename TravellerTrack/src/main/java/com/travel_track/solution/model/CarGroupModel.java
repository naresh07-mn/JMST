package com.travel_track.solution.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CarGroupModel {

    @SerializedName("CarGroup")
    @Expose
    private String carGroup;
    @SerializedName("CarID")
    @Expose
    private String carID;

    public String getCarGroup() {
        return carGroup;
    }

    public void setCarGroup(String carGroup) {
        this.carGroup = carGroup;
    }

    public String getCarID() {
        return carID;
    }

    public void setCarID(String carID) {
        this.carID = carID;
    }
    public String toString() {
        return (carGroup);
    }
}
