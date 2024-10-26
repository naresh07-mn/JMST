package com.travel_track.solution.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CarGroupDetails {

    @SerializedName("message")
    @Expose
    private List<Message> message = null;
    @SerializedName("Data")
    @Expose
    private List<CarGroupModel> data = null;

    public List<Message> getMessage() {
        return message;
    }

    public void setMessage(List<Message> message) {
        this.message = message;
    }

    public List<CarGroupModel> getData() {
        return data;
    }

    public void setData(List<CarGroupModel> data) {
        this.data = data;
    }

}