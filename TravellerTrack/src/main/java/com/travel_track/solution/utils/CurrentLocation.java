package com.travel_track.solution.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import java.util.Timer;
import java.util.TimerTask;

public class CurrentLocation {
	private Timer updateTimer;
	private LocationManager locationManager;
	LocationResult mLocationResult;
	private boolean isGpsEnabled = false;
	private boolean isNnetworkEnabled = false;
	Context context;

	public boolean getLocation(Context ctx, LocationResult locationResult) {
		mLocationResult = locationResult;
		context = ctx;
		if (locationManager == null) {
			locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
		}
		try {
			isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			isNnetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*Return false if no location provider is present.*/
		if (!isGpsEnabled && !isNnetworkEnabled) {
			return false;
		}
		if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
				ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			return false;
		}
		if (isGpsEnabled) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
		}
		if (isNnetworkEnabled) {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
		}
		updateTimer = new Timer();
		/*Timer to fetch last known location if unable to get current location*/
		updateTimer.schedule(new GetLastLocation(), 0);
		return true;
	}

	LocationListener locationListenerGps = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			updateTimer.cancel();
			locationManager.removeUpdates(this);
			locationManager.removeUpdates(locationListenerNetwork);
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	LocationListener locationListenerNetwork = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			updateTimer.cancel();
			locationManager.removeUpdates(this);
			locationManager.removeUpdates(locationListenerGps);
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	class GetLastLocation extends TimerTask {
		@Override
		public void run() {
			locationManager.removeUpdates(locationListenerGps);
			locationManager.removeUpdates(locationListenerNetwork);

			Location networkLocation = null, gpsLocation = null;
			if (isGpsEnabled) {
				if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
						ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
					// TODO: Consider calling
					return;
				}
				gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}
			if (isNnetworkEnabled){
				networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}
			// if there are both values use the latest one
			if (gpsLocation != null && networkLocation != null) {
				if (gpsLocation.getTime() > networkLocation.getTime()){
					mLocationResult.gotLocation(gpsLocation);
				} else {
					mLocationResult.gotLocation(networkLocation);
				}
				return;
			}

			if (gpsLocation != null) {
				mLocationResult.gotLocation(gpsLocation);
				return;
			}
			if (networkLocation != null) {
				mLocationResult.gotLocation(networkLocation);
			}
		}
	}

	public static abstract class LocationResult {
		public abstract void gotLocation(Location location);
	}
}
