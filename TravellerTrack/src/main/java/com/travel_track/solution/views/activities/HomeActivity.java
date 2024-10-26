package com.travel_track.solution.views.activities;

import android.Manifest;
import android.app.AlertDialog;

import com.travel_track.solution.LocationTracking.LocationUpdateEvent;
import com.travel_track.solution.LocationTracking.LocationUpdateService;
import com.travel_track.solution.R;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.travel_track.solution.apihandler.ApiHandler;
import com.travel_track.solution.apihandler.DirectionMatrixApiClient;
import com.travel_track.solution.apihandler.RestClient;
import com.travel_track.solution.data.PreferenceManager;
import com.travel_track.solution.interfaces.DialogClickListener;
import com.travel_track.solution.interfaces.DialogClickListenerWithInput;
import com.travel_track.solution.model.BookingModel;
import com.travel_track.solution.model.GoogleDistanceMatrixApiResponse;
import com.travel_track.solution.model.LoginModel;
import com.travel_track.solution.model.StartTrip;
import com.travel_track.solution.utils.CurrentLocation;
import com.travel_track.solution.utils.DataParser;
import com.travel_track.solution.views.HttpConnection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity implements OnMapReadyCallback, LocationListener, View.OnClickListener {

    @BindView(R.id.button1)
    @Nullable
    Button button1;

    @BindView(R.id.button2)
    @Nullable
    Button button2;

    @BindView(R.id.view_additional_info)
    @Nullable
    TextView view_additional_info;

    @BindView(R.id.tv_name)
    @Nullable
    TextView guest_name;

    @BindView(R.id.tv_booking_id)
    @Nullable
    TextView booking_id;

    @BindView(R.id.tv_drop_address)
    @Nullable
    TextView drop_address;

    @BindView(R.id.item_booking_details_layout)
    @Nullable
    RelativeLayout item_booking_details_layout;

    @BindView(R.id.navigation_button)
    @Nullable
    Button navigation_button;

    private static final LatLng LOWER_MANHATTAN = new LatLng(40.722543,
            -73.998585);
    private static final LatLng BROOKLYN_BRIDGE = new LatLng(40.7057, -73.9964);
    private static final LatLng WALL_STREET = new LatLng(40.7064, -74.0094);
    final String TAG = "PathGoogleMapActivity";
    private boolean doubleBackToExitPressedOnce = false;

    private final int distanceTravelled = 0;
    private final int FASTEST_INTERVAL = 20000; // use whatever suits you
    private final Location currentLocation = null;
    private final long locationUpdatedAt = Long.MIN_VALUE;
    private final Boolean keepScreenOn = false;
    SupportMapFragment mapFragment;
    private GoogleMap map;
    LatLng geoPoints = null;

    int requestForLocation = 1001;

    BookingModel bookingModel;

    ApiHandler.UpdateStartMeterAPIService apiServices;
    ApiHandler.DistanceDuration apiService;

    PreferenceManager preferenceManager;

    LoginModel userDetails;
    private String bookingType;
    private String bookingId = "";
    private String startMeterReading;
    private String pickupTime;
    private String pickupDate;
    private String dropAddress;
    private String pickupAddress;
    private String passengerName;
    private String pickup_lat;
    private String pickup_long;
    private String drop_lat;
    private String drop_long;
    private String companyId;
    public Bitmap msnapshot;
    private Timer timer;
    boolean trip_to_end = false;
    Long waitingTime = 0L;
    String mAddress = "";

    ArrayList<LatLng> latLngArrayList = new ArrayList<>();
    BookingModel bookingDetails;

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    void onBuildUserInterface() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View pageContent = inflater.inflate(R.layout.activity_homepage, null);
        setViewContents(pageContent);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        preferenceManager = PreferenceManager.getInstance(this);
        userDetails = preferenceManager.getUserInfo();
        bookingId = preferenceManager.getStingData("c_booking_id");
        companyId = preferenceManager.getStingData("c_company_id");
        dropAddress = preferenceManager.getStingData("c_drop_address");
        pickupAddress = preferenceManager.getStingData("c_pickup_address");
        passengerName = preferenceManager.getStingData("c_passenger_name");
        pickup_lat = preferenceManager.getStingData("c_pickup_lat");
        pickup_long = preferenceManager.getStingData("c_pickup_long");
        drop_lat = preferenceManager.getStingData("c_drop_lat");
        drop_long = preferenceManager.getStingData("c_drop_long");

        navigation_button.setVisibility(View.GONE);

        if (getIntent().getExtras() != null) {
            Bundle extra = getIntent().getExtras();
            bookingType = extra.getString("type");
            bookingModel = (BookingModel) getIntent().getSerializableExtra("booking_details");
            bookingDetails = (BookingModel) getIntent().getSerializableExtra("booking_details");
            Gson gson = new Gson();
            String json = gson.toJson(bookingModel);
            preferenceManager.saveData("booking_model", json);
        } else if (preferenceManager.getBooleanData("is_trip_started")) {
            Gson gson = new Gson();
            String json = preferenceManager.getStingData("booking_model");
            bookingModel = gson.fromJson(json, BookingModel.class);
            bookingType = "alert";
        }
        if (bookingModel != null) {
            bookingId = bookingModel.getBookingNo();
            preferenceManager.saveData("c_booking_id", "" + bookingId);
            companyId = String.valueOf(bookingModel.getCompanyId());
            preferenceManager.saveData("c_company_id", "" + companyId);
            dropAddress = bookingModel.getDropAddress();
            pickupAddress = bookingModel.getPickUpLocation();
            passengerName = bookingModel.getGuestName();

            pickup_lat = bookingModel.getPickUpLatitude();
            preferenceManager.saveData("c_pickup_address", "" + pickupAddress);
            preferenceManager.saveData("c_drop_address", "" + dropAddress);
            preferenceManager.saveData("c_pickup_lat", "" + pickup_lat);
            pickup_long = bookingModel.getPickUpLongitude();
            preferenceManager.saveData("c_pickup_long", "" + pickup_long);
            drop_lat = bookingModel.getDropLocationLatitude();
            preferenceManager.saveData("c_drop_lat", "" + drop_lat);
            drop_long = bookingModel.getDroplocationLongitude();
            preferenceManager.saveData("c_drop_long", "" + drop_long);
            pickupTime = bookingModel.getPickUpTime();
            pickupDate = bookingModel.getPickUpDate();
            startMeterReading = bookingModel.getStartMeter();
            if (!TextUtils.isEmpty(startMeterReading)) {
                Integer reading = 0;
                try {
                    reading = Integer.parseInt(startMeterReading);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (reading > 0) {
                    preferenceManager.saveData("start_meter_reading", startMeterReading);
                }
            }
        }

        booking_id.setText("Booking Id: " + bookingId);

        if (!TextUtils.isEmpty(pickupAddress)) {
            drop_address.setText("Address: " + pickupAddress);
            preferenceManager.saveData("c_pickup_address", "" + pickupAddress);
            preferenceManager.removeValues("c_drop_address");
        } else if (!TextUtils.isEmpty(dropAddress)) {
            drop_address.setText("Address: " + dropAddress);
            preferenceManager.saveData("c_drop_address", "" + dropAddress);
            preferenceManager.removeValues("c_pickup_address");
        }

        if (!TextUtils.isEmpty(passengerName)) {
            guest_name.setText("Name: " + passengerName);
            preferenceManager.saveData("c_passenger_name", "" + passengerName);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestForLocation);
        } else {
            mapFragment.getMapAsync(this);
        }
        // getDistanceMatrix("40.6655101%2C-73.89188969999998","40.659569%2C-73.933783%7C40.729029%2C-73.851524%7C40.6860072%2C-73.6334271%7C40.598566%2C-73.7527626");

        // getDistanceMatrix("40.6655101,-73.89188969999998","40.659569,-73.933783");
        //%7Cfor multiple destination
        //%2C for comma
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        navigation_button.setOnClickListener(this);

        if (preferenceManager.getBooleanData("is_trip_started")) {
            item_booking_details_layout.setVisibility(View.VISIBLE);
            button1.setText(getString(R.string.end_trip));
            button2.setVisibility(View.VISIBLE);
            navigation_button.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(bookingType)) {
            item_booking_details_layout.setVisibility(View.GONE);
        } else {
            if (bookingType.equalsIgnoreCase("alert")) {
                item_booking_details_layout.setVisibility(View.VISIBLE);
                navigation_button.setVisibility(View.VISIBLE);

            } else if (bookingType.equalsIgnoreCase("completed")) {
                item_booking_details_layout.setVisibility(View.VISIBLE);
                button1.setVisibility(View.GONE);
                button2.setVisibility(View.GONE);
            }
        }
        if (userTypeDriver) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //createBackgroundLocationTrackingJob();
            }
        }
        if (preferenceManager.getBooleanData("is_trip_started")) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            startdrawRout(true);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        startService(new Intent(this, LocationUpdateService.class));

    }


    @Override
    public void onLocationChanged(Location location) {

    }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }



    private void currentLOc(Boolean isFirst) {
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, HomeActivity.this);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location == null) {
            Toast.makeText(getApplicationContext(), "GPS signal not found", Toast.LENGTH_SHORT).show();
        }
        if (location != null) {


            if (isFirst) {
                preferenceManager.saveData("start_lat", String.valueOf(location.getLatitude()));
                preferenceManager.saveData("start_long", String.valueOf(location.getLongitude()));
                preferenceManager.saveData("firstStart_lat", String.valueOf(geoPoints.latitude));
                preferenceManager.saveData("firstStart_long", String.valueOf(geoPoints.longitude));
                preferenceManager.saveData("last_lat", String.valueOf(location.getLatitude()));
                preferenceManager.saveData("last_long", String.valueOf(location.getLongitude()));
            } else {
                preferenceManager.saveData("end_lat", String.valueOf(location.getLatitude()));
                preferenceManager.saveData("end_long", String.valueOf(location.getLongitude()));

            }
        }
    }

    public void onMessageEvent(LocationUpdateEvent event) {
//handle updates here
     Log.i("new_location", event.toString());
    // event.getLocation().latitude, event.getLocation().longitude;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v == button1 && button1.getText().toString().equalsIgnoreCase(getString(R.string.begin_trip))) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean gps_enabled = false;
            boolean network_enabled = false;
            try {
                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
            }
            try {
                network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
            }
            if (!gps_enabled && !network_enabled) {
                // notify user
                new AlertDialog.Builder(this)
                        .setMessage("gps_network_not_enabled")
                        .setPositiveButton("open_location_settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                HomeActivity.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            } else {

                showBlockingMessageWithInput(new DialogClickListenerWithInput() {
                                                 @Override
                                                 public void onButton1Click(String inputText, String inputText2) {

                                                     if (geoPoints != null) {
                                                         String mLat = String.valueOf(pickup_lat);
                                                         String mLang = String.valueOf(pickup_long);
                                                         mAddress = getAddress(new LatLng(geoPoints.latitude, geoPoints.longitude));
                                                         String dest = geoPoints.latitude + "," + geoPoints.longitude;
                                                         // falguni "22.631376,88.442426"
                                                         // krishna rent car="28.464747,77.193375"
                                                         getDistanceMatrix("28.575060,77.215710", dest, inputText, inputText2, mAddress);

                                                     }

                                                     preferenceManager.removeValues("distanceCovered");
                                                     getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                                     currentLOc(true);
                                                     startdrawRout(true);
                                                     preferenceManager.saveData("isStartTimer", true);
                                                 }

                                                 @Override
                                                 public void onButton2Click(String inputText, String inputText2) {
                                                 }
                                             }
                        , null, "METER READING", "Meter Reading", startMeterReading,
                        "Error", "Start Time", pickupTime, "Error",
                        "STORE METER READING", null);
            }

        } else if (v == button1 && button1.getText().toString().equalsIgnoreCase(getString(R.string.end_trip))) {
            BitmapHolder.getInstance().setPickUpTime(pickupTime);
            BitmapHolder.getInstance().setStartDate(pickupDate);


            currentLOc(false);
            if (preferenceManager.getStingData("firstStart_lat") != null || preferenceManager.getStingData("firstStart_long") != null
            ) {

                startdrawRout(false);


            }

            showBlockingMessage(new DialogClickListener() {
                @Override
                public void onButton1Click() {


                    preferenceManager.removeValues("c_booking_id", "c_passenger_name",
                            "c_pickup_lat", "c_pickup_long", "c_drop_lat", "c_drop_long", "is_trip_started",
                            "c_drop_address0", "c_pickup_address", "start_lat",
                            "firstStart_long", "start_long",
                            "tripStatus", "end_lat", "end_long", "last_lat", "last_long", "isStartTimer");
                    Geocoder gcd = new Geocoder(getBaseContext(),
                            Locale.getDefault());
                    List<Address> addresses;
                    try {
                        addresses = gcd.getFromLocation(geoPoints.latitude,
                                geoPoints.longitude, 1);
                        if (addresses.size() > 0) {
                            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            String locality = addresses.get(0).getLocality();
                            String subLocality = addresses.get(0).getSubLocality();
                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            String postalCode = addresses.get(0).getPostalCode();
                            String knownName = addresses.get(0).getFeatureName();
                            if (subLocality != null) {
                                mAddress = locality + "," + subLocality;
                            } else {
                                mAddress = locality;
                            }

                            mAddress = address;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(HomeActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    Intent mIntent = new Intent(HomeActivity.this, CalculateChargeActivity.class);
                    mIntent.putExtra("booking_details", bookingModel);
                    mIntent.putExtra("waitingTime", waitingTime);
                    mIntent.putExtra("mCurrentLat", geoPoints.latitude);
                    mIntent.putExtra("mCurrentLong", geoPoints.longitude);
                    mIntent.putExtra("mAddress", mAddress);
                    showProgress();
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideProgress();
                            map.snapshot(bitmap -> {
                                Bitmap msnapshot = bitmap; //.createScaledBitmap(bitmap, 140, 140, true);
                                BitmapHolder.getInstance().setBitmap(msnapshot);
                            });

                            startActivity(mIntent);
                            finish();
                        }
                    }, 7000);
                }

                @Override
                public void onButton2Click(){
                    String firstStart_long = preferenceManager.getStingData("firstStart_long");
                    String firstStart_lat = preferenceManager.getStingData("firstStart_lat");
                    if (firstStart_long!=null&&firstStart_lat!=null){
                        LatLng morig = new LatLng(Double.parseDouble(firstStart_lat),
                                Double.parseDouble(firstStart_long));
                        Markers(morig,new LatLng(geoPoints.latitude,geoPoints.longitude) );
                    }
                }
            }, "", "Are you sure, you want to end this trip?", "Yes", "No");

        } else if (v == button2) {

            Intent mIntent = new Intent(HomeActivity.this, CustomerFeedbackActivity.class);
            mIntent.putExtra("booking_details", bookingDetails);
            startActivity(mIntent);
            trip_to_end = true;
            button2.setVisibility(View.GONE);
            //finish();

        } else if (v == navigation_button) {
            try {

                Uri navigationUri = Uri.parse("http://maps.google.com/maps?saddr=" + geoPoints.latitude + "," + geoPoints.longitude +
                        "&daddr=" + pickup_lat + "," + pickup_long);
                if (preferenceManager.getBooleanData("is_trip_started")) {
                    navigationUri = Uri.parse("http://maps.google.com/maps?saddr=" + pickup_lat + "," + pickup_long + "&daddr=" + drop_lat + "," + drop_long);
                }


                Intent intent = new Intent(Intent.ACTION_VIEW, navigationUri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {

                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (preferenceManager.getBooleanData("is_trip_started")) {
            startdrawRout(true);

        }

    }

    private void startdrawRout(Boolean startTimer) {
        Timer timer = new Timer();
        int begin = 15000;
        int timeInterval = 90000;//1000 is for 1 sec
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //call the method
                if (!preferenceManager.getBooleanData("isStartTimer")) {//false
                    timer.cancel();
                } else {
                    Log.i("Method_called", "called");
                    if (!HomeActivity.this.isFinishing()){
                        runOnUiThread(() -> updateLOcation());
                    }


                }

            }
        }, begin, timeInterval);

    }

    private void updateLOcation() {

        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean gps_enabled = false;
            boolean network_enabled = false;
            try {
                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
            }
            try {
                network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
            }
            if (!gps_enabled && !network_enabled) {
                // notify user
                new AlertDialog.Builder(HomeActivity.this)
                        .setMessage("gps_network_not_enabled")
                        .setPositiveButton("open_location_settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                HomeActivity.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            } else {
                if (preferenceManager.getBooleanData("is_trip_started")) {
                    Location targetLocation = new Location("");//provider name is unnecessary
                    targetLocation.setLatitude(geoPoints.latitude);//your coords of course
                    targetLocation.setLongitude(geoPoints.longitude);
                    if (preferenceManager.getStingData("last_lat") != null && preferenceManager.getStingData("last_long") != null) {
                        GetDistanceFromLatLonInKm(preferenceManager.getStingData("last_lat"),
                                preferenceManager.getStingData("last_long"), targetLocation);

                    }
                }
            }


        } catch (IllegalArgumentException e) {
            e.printStackTrace();

        }


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        startdrawRout(true);
    }

    public void GetDistanceFromLatLonInKm(String startLat, String startLong, Location end) {
        if (startLat != null && startLong != null && end != null) {
            String key = getString(R.string.distance_matrix_api_key);
            String origin = startLat + "," + startLong;
            String dest = end.getLatitude() + "," + end.getLongitude();
            apiService = DirectionMatrixApiClient.getClient().create(ApiHandler.DistanceDuration.class);
            Call<GoogleDistanceMatrixApiResponse> call = apiService.getMatrixData(origin, dest, key);
            call.enqueue(new Callback<GoogleDistanceMatrixApiResponse>() {
                @Override
                public void onResponse(@NotNull Call<GoogleDistanceMatrixApiResponse> call, Response<GoogleDistanceMatrixApiResponse> response) {
                    if (response.isSuccessful() && response.code() == 200) {
                        if (response.body().getRows().size() > 0) {
                            Integer mDistance = response.body().getRows().get(0).getElements().get(0).getDistance().getValue();
                            if (mDistance != null && mDistance > 200) {

                                String firstStart_long = preferenceManager.getStingData("firstStart_long");
                                String firstStart_lat = preferenceManager.getStingData("firstStart_lat");
                                if (firstStart_long != null && firstStart_lat != null) {
                                    drawRoutePath(true, new LatLng(Double.parseDouble(firstStart_lat),
                                                    Double.parseDouble(firstStart_long)),
                                            new LatLng(Double.parseDouble(preferenceManager.getStingData("start_lat")),
                                                    Double.parseDouble(preferenceManager.getStingData("start_long"))),
                                            new LatLng(end.getLatitude(), end.getLongitude()), false);
                                    preferenceManager.saveIntData("distanceCovered", preferenceManager.getIntData("distanceCovered") + mDistance);
                                    preferenceManager.saveData("last_lat", String.valueOf(end.getLatitude()));
                                    preferenceManager.saveData("last_long", String.valueOf(end.getLongitude()));
                                    preferenceManager.saveData("start_lat", String.valueOf(end.getLatitude()));
                                    preferenceManager.saveData("start_long", String.valueOf(end.getLongitude()));


                                }
                                Log.i("Method_called", "Mydistance is =" + mDistance + "distance is =" + preferenceManager.getIntData("distanceCovered").toString());
                            }
                        }

                    }


                }

                @Override
                public void onFailure(@NotNull Call<GoogleDistanceMatrixApiResponse> call, @NotNull Throwable t) {

                }
            });

        }
    }

    private double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }

    private void startTrip(final String startMeter, final String closeTime, final String distance, final String duration, String currentAddress) {
        int mMeter = 0;
        int statGarageTime = 0;
        if (distance != null) {
            mMeter = Integer.parseInt(startMeter) - Integer.parseInt(distance) / 1000;
            statGarageTime = Integer.parseInt(duration) / 60;
        }

        showProgress();
        apiServices = RestClient.getClient().create(ApiHandler.UpdateStartMeterAPIService.class);
        Call<StartTrip> call = apiServices.UpdateStartMeter(startMeter, closeTime, Integer.toString(mMeter), Integer.toString(statGarageTime), Double.toString(geoPoints.latitude), Double.toString(geoPoints.longitude), currentAddress, userDetails.getCompanyName(), "" + companyId, "" + bookingId, "" + 0);
        call.enqueue(new Callback<StartTrip>() {
            @Override
            public void onResponse(Call<StartTrip> call, Response<StartTrip> response) {
                hideProgress();
                String responseCode = response.body().getMessage().get(0).getResultcode();
                String message = response.body().getMessage().get(0).getMessage();
                if (!TextUtils.isEmpty(responseCode) && responseCode.equalsIgnoreCase("0")) {

                    button1.setText(getString(R.string.end_trip));
                    button2.setVisibility(View.VISIBLE);
                    PreferenceManager.getInstance(HomeActivity.this).saveData("is_trip_started", true);

                    PreferenceManager.getInstance(HomeActivity.this).saveData("start_meter_reading", startMeter);
                    navigation_button.setVisibility(View.VISIBLE);
                    drop_address.setText("Address: " + dropAddress);
                    preferenceManager.saveData("c_drop_address", "" + dropAddress);
                    preferenceManager.removeValues("c_pickup_address");

                } else if (!TextUtils.isEmpty(responseCode) && responseCode.equalsIgnoreCase("1")) {
                    Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StartTrip> call, Throwable t) {
                hideProgress();
                Snackbar.make(findViewById(android.R.id.content), "Something went wrong.", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void getDistanceMatrix(String origin, String destination, String input1, String input2, String currentAddress) {

        apiService = DirectionMatrixApiClient.getClient().create(ApiHandler.DistanceDuration.class);
        Call<GoogleDistanceMatrixApiResponse> call = apiService.getMatrixData(origin, destination, getString(R.string.google_server_key));
        call.enqueue(new Callback<GoogleDistanceMatrixApiResponse>() {
            @Override
            public void onResponse(@NotNull Call<GoogleDistanceMatrixApiResponse> call, Response<GoogleDistanceMatrixApiResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        if (response.body().getRows().get(0).getElements().get(0).getDistance().getText() != null
                                && response.body().getRows().get(0).getElements().get(0).getDuration().getText() != null) {
                            startTrip(input1, input2, response.body().getRows().get(0).getElements().get(0).getDistance().getValue().toString(),
                                    response.body().getRows().get(0).getElements().get(0).getDuration().getValue().toString(), currentAddress);

                           /*
                            Log.i("APIdataoogle",response.body().getRows().get(0).getElements().toString());
                            Log.i("APIdataoogle",response.body().getRows().get(0).getElements().get(0).getDistance().getText());
                            Toast.makeText(HomeActivity.this, response.body().getStatus(), Toast.LENGTH_LONG).show();
                            Toast.makeText(HomeActivity.this, response.body().getRows().get(0).getElements().get(0).getDistance().getText(), Toast.LENGTH_LONG).show();
                            Toast.makeText(HomeActivity.this, response.body().getRows().get(0).getElements().get(0).getDuration().getText(), Toast.LENGTH_LONG).show();

*/

                        }
                    }

                }


            }

            @Override
            public void onFailure(@NotNull Call<GoogleDistanceMatrixApiResponse> call, @NotNull Throwable t) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == requestForLocation && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mapFragment.getMapAsync(this);
            if (userTypeDriver) {
              //  createBackgroundLocationTrackingJob();
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {


        map = googleMap;
        initializeMap();
    }

    private void initializeMap() {
        if (map == null) {
            return;
        }
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        map.setTrafficEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        if (map.getMyLocation() != null) {
            geoPoints = new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());
            displayCurrentLocation();
        } else {
            CurrentLocation.LocationResult result = new CurrentLocation.LocationResult() {
                @Override
                public void gotLocation(Location location) {
                    geoPoints = new LatLng(location.getLatitude(), location.getLongitude());
                    runOnUiThread(() -> displayCurrentLocation());
                }
            };
            CurrentLocation currentLocation = new CurrentLocation();
            currentLocation.getLocation(this, result);
        }
    }

    public void displayCurrentLocation() {
        map.moveCamera(CameraUpdateFactory.newLatLng(geoPoints));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(geoPoints, 15.0f));


    }


    private void Markers(LatLng origin, LatLng destination) {

        //  Toast.makeText(this, origin.toString(), Toast.LENGTH_SHORT).show();
        if (map != null) {
            MarkerOptions options = new MarkerOptions();

            //  options.position(StartingLocation);
            options.position(destination);


            map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    Context context = getApplicationContext(); //or getActivity(), YourActivity.this, etc.

                    LinearLayout info = new LinearLayout(context);
                    info.setOrientation(LinearLayout.VERTICAL);

                    TextView title = new TextView(context);
                    title.setTextColor(Color.BLACK);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setLines(2);
                    title.setText(marker.getTitle());

                    TextView snippet = new TextView(context);
                    snippet.setTextColor(Color.BLACK);
                    snippet.setText(marker.getSnippet());

                    info.addView(title);
                    info.addView(snippet);

                    return info;
                }
            });


                Marker marker2 = map.addMarker(new MarkerOptions().position(origin)
                        .title("Start")
                        .snippet(getAddress(origin))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_start))
                );
                marker2.showInfoWindow();
                Marker marker3 = map.addMarker(new MarkerOptions().position(destination)
                        .title("End")
                        .snippet(getAddress(destination))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_end))
                );
                marker3.showInfoWindow();
            }



        }


    private void addCurrentMarkers(LatLng BIGINaDRESS, LatLng origin, LatLng destination, int markerStart, int markerEnd) {

        //  Toast.makeText(this, origin.toString(), Toast.LENGTH_SHORT).show();
        if (map != null) {
            MarkerOptions options = new MarkerOptions();

            //  options.position(StartingLocation);
            options.position(destination);
            options.position(origin);
            options.position(BIGINaDRESS);

            map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    Context context = getApplicationContext(); //or getActivity(), YourActivity.this, etc.

                    LinearLayout info = new LinearLayout(context);
                    info.setOrientation(LinearLayout.VERTICAL);

                    TextView title = new TextView(context);
                    title.setTextColor(Color.BLACK);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setLines(2);
                    title.setText(marker.getTitle());

                    TextView snippet = new TextView(context);
                    snippet.setTextColor(Color.BLACK);
                    snippet.setText(marker.getSnippet());

                    info.addView(title);
                    info.addView(snippet);

                    return info;
                }
            });
         /*   if (Objects.equals(preferenceManager.getStingData("endMarker"), "yes")) {
                String firstStart_long = preferenceManager.getStingData("firstStart_long");
                String firstStart_lat = preferenceManager.getStingData("firstStart_lat");

                Marker marker2 = map.addMarker(new MarkerOptions().position(origin)
                        .title("Start")
                        .snippet(getAddress(new LatLng(Double.parseDouble(firstStart_lat),
                                Double.parseDouble(firstStart_long))))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_start))
                );
                marker2.showInfoWindow();
                Marker marker3 = map.addMarker(new MarkerOptions().position(destination)
                        .title("End")
                        .snippet(getAddress(destination))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_end))
                );
                marker3.showInfoWindow();
            }*/


        }
    }

    private String getAddress(LatLng source) {
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            double latitude = source.latitude;
            double longitude = source.longitude;
            Log.e("New_latitude", "inside latitude--" + latitude);
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();

                //  ro_gps_location.setText(state + " , " + city + " , " + country);
                //  ro_address.setText(address + " , " + knownName + " , " + postalCode);
            }
            return addresses.get(0).getLocality() + addresses.get(0).getSubLocality();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }

    }


    private void drawRoutePath(boolean isTripStarted, LatLng garage, LatLng origin, LatLng dest, Boolean clearMap) {

        if (origin == null || dest == null || map == null) return;
        if (clearMap) {
            map.clear();

        }

        String url = getUrl(garage, origin, dest);
        FetchUrl FetchUrl = new FetchUrl();
        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);
        //move map camera
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        //   builder.include(garage);

        builder.include(garage);
        builder.include(origin);
        builder.include(dest);
        if (preferenceManager.getStingData("firstStart_long") != null
                && preferenceManager.getStingData("firstStart_lat") != null) {
            String firstStart_long = preferenceManager.getStingData("firstStart_long");
            String firstStart_lat = preferenceManager.getStingData("firstStart_lat");
            builder.include(new LatLng(Double.parseDouble(firstStart_lat),
                    Double.parseDouble(firstStart_long)));
        }

        addCurrentMarkers(garage, origin, dest, 0, 1);
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 180);
        map.moveCamera(cu);

        // map.moveCamera(CameraUpdateFactory.newLatLngZoom(dest, 13));
        // map.animateCamera(CameraUpdateFactory.newLatLngZoom(geoPoints, 15.0f));

    }


    private void currentRoutePath(ArrayList<LatLng> mList) {

        LatLng origin = new LatLng(mList.get(0).latitude, mList.get(0).latitude);
        LatLng dest = new LatLng(mList.get(mList.size() - 1).latitude, mList.get(mList.size() - 1).latitude);
        if (origin == null || dest == null || map == null) return;
        String wayPoints = "";
        for (int j = 1; j < mList.size() - 1; ++j) {

            wayPoints = wayPoints + "+to:" + mList.get(j).latitude + "," + mList.get(j).longitude;
        }

        //map.clear();
        String url = getcURRENTrOUTUrl(wayPoints, origin, dest);
        FetchUrl FetchUrl = new FetchUrl();
        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);
        //move map camera
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        //   builder.include(garage);
        builder.include(origin);
        builder.include(dest);
        //addCurrentMarkers( origin, dest, 0, 1);
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 150);
        map.moveCamera(cu);

        // map.moveCamera(CameraUpdateFactory.newLatLngZoom(dest, 13));
        // map.animateCamera(CameraUpdateFactory.newLatLngZoom(geoPoints, 15.0f));

    }

    private String getcURRENTrOUTUrl(String wayPoints, LatLng origin, LatLng dest) {


        String OriDest = "origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=driving";
        String sensor = "sensor=false";
        String params = OriDest + "&%20" + wayPoints + "&" + "&" + mode + sensor + "&key=" + getString(R.string.google_server_key);
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
        return url;
        //  String parameters =   getString(R.string.google_server_key);
        // Output format

    }


    private String getUrl(LatLng garage, LatLng origin, LatLng dest) {
        // Origin of route
        //"28.521840,77.090270"
        String waypoints = "waypoints=optimize:true|"
                + origin.latitude + "," + origin.longitude;
        String OriDest = "origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=driving";
        String sensor = "sensor=false";
        String params = OriDest + "&%20" + waypoints + "&" + "&" + mode + sensor + "&key=" + getString(R.string.google_server_key);
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
        return url;
        //  String parameters =   getString(R.string.google_server_key);
        // Output format

    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            Log.d("downloadUrl", data);
            br.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0]);
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.geodesic(true);
                lineOptions.width(10);
                lineOptions.color(Color.BLUE);


                Log.d("onPostExecute", "onPostExecute lineoptions decoded");
                Log.d("onPostExecute", "onPostExecute lineoptions decoded");
            }
            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                map.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }

    }

    private void startWaitingTime() {
        startTimer();
    }

    private void updateWaitingTime() {
        if (timer != null) {
            timer.cancel();
        }
        view_additional_info.setText("");
        view_additional_info.setBackgroundColor(getResources().getColor(R.color.app_theme_color));
    }

    public void startTimer() {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    waitingTime = waitingTime + 1000;
                    long seconds = (waitingTime / 1000) % 60;
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(waitingTime);
                    view_additional_info.setText("Waiting Time: " + minutes + "." + seconds + " seconds");
                    if (waitingTime > 5 * 60 * 1000) {
                        view_additional_info.setBackgroundColor(getResources().getColor(R.color.Red));
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }


}
