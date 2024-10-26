package com.travel_track.solution.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TourCodeModel {

    @SerializedName("tourcode")
    @Expose
    private String tourcode;
    @SerializedName("TourId")
    @Expose
    private String tourId;

    public String getTourcode() {
        return tourcode;
    }

    public void setTourcode(String tourcode) {
        this.tourcode = tourcode;
    }

    public String getTourId() {
        return tourId;
    }

    public void setTourId(String tourId) {
        this.tourId = tourId;
    }
    public String toString() {
        return (tourcode);
    }
}