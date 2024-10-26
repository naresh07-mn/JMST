package com.travel_track.solution.utils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class DeviceUuidFactory {
	private static final String DEVICE_ID = "device_id";
	private static final String DEVICE_PREF = "device_pref";

	protected static UUID uuid;
	private Context mContext = null;

	public DeviceUuidFactory(Context context) {
		this.mContext = context;
		if (uuid == null) {
			synchronized (DeviceUuidFactory.class) {
				if (uuid == null) {
					final SharedPreferences prefs = context.getSharedPreferences(DEVICE_PREF, Activity.MODE_PRIVATE);
					final String id = prefs.getString(DEVICE_ID, null);
					if (id != null) {
						// Use the ids previously computed and stored in the prefs file
						uuid = UUID.fromString(id);
					} else {
						final String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
						// Use the Android ID unless it's broken, in which case fallback on deviceId, unless it's not available, then fallback on a random
						// number which we store to a prefs file
						if (!"9774d56d682e549c".equals(androidId)) {
							uuid = UUID.nameUUIDFromBytes((context.getPackageName()+androidId).getBytes(StandardCharsets.UTF_8));
						} else {
							@SuppressLint("MissingPermission")
							final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
							uuid = deviceId != null ? UUID.nameUUIDFromBytes((context.getPackageName()+deviceId).getBytes(StandardCharsets.UTF_8)) : UUID.randomUUID();
						}
						// Write the value out to the prefs file
						prefs.edit().putString(DEVICE_ID, uuid.toString()).commit();
					}
				}
			}
		}
	}

	public UUID getDeviceUuid() {
		return uuid;
	}
	public void setDeviceUuid(String uuid) {
		SharedPreferences prefs = mContext.getSharedPreferences(DEVICE_PREF, Activity.MODE_PRIVATE);
		prefs.edit().putString(DEVICE_ID, uuid).commit();
	}
}
