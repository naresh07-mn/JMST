package com.travel_track.solution.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.maps.model.LatLng;
import com.travel_track.solution.interfaces.OnAddressResponse;

import java.util.List;
import java.util.logging.Handler;

public class AppUtils {

    public static final String NOTIFICATION_RECEIVED = "com.travel_track.solution.android.action.broadcast";

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    static android.os.Handler handler = new android.os.Handler();
    public static void getLocationFromAddress(Context context, String strAddress, OnAddressResponse response){
        new Thread(() -> {
            LatLng latLng = null;
            Geocoder coder = new Geocoder(context);
            List<Address> address;
            try {
                address = coder.getFromLocationName(strAddress,5);
                if (address==null) {
                    LatLng finalLatLng = latLng;
                    handler.post(() -> response.onResponse(finalLatLng));
                }
                Address location=address.get(0);
                location.getLatitude();
                location.getLongitude();
                latLng = new LatLng(location.getLatitude() * 1E6,
                        location.getLongitude() * 1E6);
                LatLng finalLatLng1 = latLng;
                handler.post(() -> response.onResponse(finalLatLng1));
            } catch (Exception e){
                handler.post(() -> response.onResponse(null));
            }
        }).start();
    }

}
