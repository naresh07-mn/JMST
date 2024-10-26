package com.travel_track.solution.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.travel_track.solution.model.LoginModel;

import static android.content.Context.MODE_PRIVATE;

import java.util.ArrayList;

public class PreferenceManager {
    private final static String SHARED_PREF_TRAVEL_TRACK = "travel_track";
    private final static String KEY_USER_INFO = "user_info";
    private final static String KEY_WAY_POINTS = "WAY_POINT";
    private final static String DUTY_START_LATLONG = "start_latlong";

    private static SharedPreferences sharedPreferences;

    private static PreferenceManager preferenceManager;

    public static PreferenceManager getInstance(Context ctx) {
        if (preferenceManager == null) {
            preferenceManager = new PreferenceManager();
        }
        sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_TRAVEL_TRACK, MODE_PRIVATE);
        return preferenceManager;
    }

    public void saveUserInfo(Object object) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String stringUserInfo = gson.toJson(object);
        editor.putString(KEY_USER_INFO, stringUserInfo);
        editor.commit();
    }

    public LoginModel getUserInfo() {
        Gson gson = new Gson();
        String stringData = sharedPreferences.getString(KEY_USER_INFO, "");
        LoginModel model = gson.fromJson(stringData, LoginModel.class);
        return model;
    }

    public void saveData(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void saveIntData(String key, Integer value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }


    public Integer getIntData(String key) {
        return sharedPreferences.getInt(key, 0);
    }


    public String getStingData(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void saveData(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public Boolean getBooleanData(String key) {
        return sharedPreferences.getBoolean(key, false);
    }


    public void removeValues(String... keys) {
        for (String key : keys) {
            sharedPreferences.edit().remove(key).apply();
        }
    }

    public void logout() {
        sharedPreferences.edit().remove(KEY_USER_INFO).apply();
        preferenceManager.removeValues("c_booking_id", "c_booking_address", "c_passenger_name",
                "c_pickup_lat", "c_pickup_long", "c_drop_lat", "c_drop_long", "is_trip_started");
    }
}
