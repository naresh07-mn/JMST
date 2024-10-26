package com.travel_track.solution.push;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.travel_track.solution.R;
import com.travel_track.solution.apihandler.ApiHandler;
import com.travel_track.solution.apihandler.RestClient;
import com.travel_track.solution.utils.AppUtils;
import com.travel_track.solution.utils.DeviceUuidFactory;
import com.travel_track.solution.views.activities.MyRidesActivity;

import java.util.Calendar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirebaseMessageing extends FirebaseMessagingService{

    private static final String TAG = "push";
    private static final String CHANNEL_ID = "channel_11110";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            handleNow(remoteMessage.getNotification());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void handleNow(RemoteMessage.Notification notification) {
        //Get the default name of application to display as Notification title
        String title = this.getString(R.string.app_name);
        String alert = notification.getBody();

        Intent launch = new Intent(AppUtils.NOTIFICATION_RECEIVED);
        launch.setPackage(getApplicationContext().getPackageName());
        launch.putExtra("title", title);
        launch.putExtra("message", alert);
        getApplicationContext().sendBroadcast(launch);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        int icon = R.drawable.ic_launcher;

        builder.setSmallIcon(icon).setContentTitle(title).setContentText(alert);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        Bitmap largeIconBitMap = null;
        try {
            largeIconBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.setLargeIcon(largeIconBitMap);

        int notificationId = (int) Calendar.getInstance().getTimeInMillis();

        Intent notificationIntent = new Intent(this, MyRidesActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(this, notificationId,  notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(true);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle(builder);
        bigTextStyle.bigText(title);

        Notification notificationBuilder = builder.build();
        notificationBuilder.priority = Notification.PRIORITY_HIGH;
        notificationBuilder.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE) ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, title, NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        manager.notify(notificationId, notificationBuilder);

    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
       String tokeni = FirebaseInstanceId.getInstance().getToken();

        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(tokeni);
    }
    private void sendRegistrationToServer(String refreshedToken) {
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

    }
    @TargetApi(26)
    private void createChannel(NotificationManager notificationManager, String title, String alert) {
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel mChannel = new NotificationChannel(title, title, importance);
        mChannel.setDescription(alert);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        notificationManager.createNotificationChannel(mChannel);
    }
}
