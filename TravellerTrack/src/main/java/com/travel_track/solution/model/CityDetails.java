package com.travel_track.solution.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CityDetails {

    @SerializedName("message")
    @Expose
    private List<Message> message = null;
    @SerializedName("Data")
    @Expose
    private List<CityModel> data = null;

    public List<Message> getMessage() {
        return message;
    }

    public void setMessage(List<Message> message) {
        this.message = message;
    }

    public List<CityModel> getData() {
        return data;
    }

    public void setData(List<CityModel> data) {
        this.data = data;
    }

}