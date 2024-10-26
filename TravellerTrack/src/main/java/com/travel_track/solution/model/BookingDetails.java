package com.travel_track.solution.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BookingDetails {

    @SerializedName("error")
    @Expose
    private List<Message> error = null;
    @SerializedName("Data")
    @Expose
    private List<BookingModel> data = null;

    public List<Message> getError() {
        return error;
    }

    public void setError(List<Message> error) {
        this.error = error;
    }

    public List<BookingModel> getData() {
        return data;
    }

    public void setData(List<BookingModel> data) {
        this.data = data;
    }

}