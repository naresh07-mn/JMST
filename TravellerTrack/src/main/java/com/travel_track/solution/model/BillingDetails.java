package com.travel_track.solution.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BillingDetails {

    @SerializedName("message")
    @Expose
    private List<Message> message = null;
    @SerializedName("Data")
    @Expose
    private List<BillingModel> data = null;

    public List<Message> getMessage() {
        return message;
    }

    public void setMessage(List<Message> message) {
        this.message = message;
    }

    public List<BillingModel> getData() {
        return data;
    }

    public void setData(List<BillingModel> data) {
        this.data = data;
    }

}