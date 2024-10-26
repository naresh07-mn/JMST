package com.travel_track.solution.LocationTracking;

import android.location.Location;


public class LocationUpdateEvent {
    private LocationDTO location;

    public LocationUpdateEvent(LocationDTO locationUpdate) {
        this.location = locationUpdate;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }


    public static class LocationDTO {
        public double latitude;
        public double longitude;
        public double speed;

        @Override
        public String toString() {
            return "lat:"+latitude +"- lng:"+longitude + "- speed:"+speed;
        }
    }
}
