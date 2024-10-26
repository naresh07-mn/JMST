package com.travel_track.solution.viewmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ParkingDutySlipDetails {


    @SerializedName("data")
    @Expose
    private ParkingDutyModel data;

    public ParkingDutyModel getData() {
        return data;
    }

    public void setData(ParkingDutyModel data) {
        this.data = data;
    }

}
