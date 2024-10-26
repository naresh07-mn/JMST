package com.travel_track.solution.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TourCodeDetails {

    @SerializedName("message")
    @Expose
    private List<Message> message = null;
    @SerializedName("Data")
    @Expose
    private List<TourCodeModel> data = null;

    public List<Message> getMessage() {
        return message;
    }

    public void setMessage(List<Message> message) {
        this.message = message;
    }

    public List<TourCodeModel> getData() {
        return data;
    }

    public void setData(List<TourCodeModel> data) {
        this.data = data;
    }
}