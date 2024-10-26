package com.travel_track.solution.push;

import android.util.Log;

//import com.google.firebase.iid.FirebaseInstanceIdService;
import com.travel_track.solution.apihandler.ApiHandler;
import com.travel_track.solution.apihandler.RestClient;
import com.travel_track.solution.utils.DeviceUuidFactory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
public class FirebaseBGPushService extends FirebaseInstanceIdService {

    private static final String TAG = "push";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
     //   Log.i(FirebaseBGPushService.this,refreshedToken);
    }

   */
/* private void sendRegistrationToServer(String refreshedToken) {
        ApiHandler.RegisterDevice apiService = RestClient.getClient().create(ApiHandler.RegisterDevice.class);
        Call<Void> call = apiService.registerDevice(new DeviceUuidFactory(getApplicationContext()).getDeviceUuid().toString(),
                refreshedToken,"","");
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

    }*//*

}
*/
