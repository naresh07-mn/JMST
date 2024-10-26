package com.travel_track.solution.model;


import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StartTrip {

  @SerializedName("message")
  @Expose
  private List<Message> message = null;


  public StartTrip(List<Message> message) {
    this.message = message;
  }

  public List<Message> getMessage() {
    return message;
  }

  public void setError(List<Message> message) {
    this.message = message;
  }
}
