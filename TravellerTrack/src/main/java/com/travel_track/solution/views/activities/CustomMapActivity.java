package com.travel_track.solution.views.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.google.firebase.firestore.GeoPoint;
import com.travel_track.solution.R;
import com.travel_track.solution.apihandler.ApiHandler;
import com.travel_track.solution.apihandler.ImageProcessingHandler;
import com.travel_track.solution.apihandler.RestClient;
import com.travel_track.solution.data.PreferenceManager;
import com.travel_track.solution.model.LoginModel;
import com.travel_track.solution.model.UploadResponce;
import com.travel_track.solution.utils.CurrentLocation;
import com.travel_track.solution.utils.DataParser;
import com.travel_track.solution.utils.ImageResizer;
import com.travel_track.solution.views.adapter.PlacesAutoCompleteAdapter;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomMapActivity extends BaseActivity implements OnMapReadyCallback, LocationListener, View.OnClickListener {
    @BindView(R.id.upload_map)
    @Nullable
    Button upload;

    @BindView(R.id.signature_map)
    @Nullable
    Button draw_route;

    @BindView(R.id.start_addres_value)
    @Nullable
    AutoCompleteTextView start_addres_value;

    @BindView(R.id.close_address)
    @Nullable
    AutoCompleteTextView close_address;

    @BindView(R.id.booking_id_value)
    @Nullable
    EditText booking_id;

    @BindView(R.id.end_marker)
    @Nullable
    CheckBox endMarker;
    @BindView(R.id.clear_map)
    @Nullable
    Button clearMarker;
    SupportMapFragment mapFragment;
    private GoogleMap map;
    LatLng geoPoints = null;
    int requestForLocation = 1001;
    PreferenceManager pre;
    LoginModel userDetails = null;
    PreferenceManager preferenceManager;
    ProgressDialog pDialog;
    private String postPath;
    String orgId = "";

    private Boolean myMarker;
    private Boolean myMarkerStart = true;
    private String myCloseAddress;
    private LatLng fiestOrigin;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    void onBuildUserInterface() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View pageContent = inflater.inflate(R.layout.activity_custom_map, null);
        setViewContents(pageContent);
        initDialog();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestForLocation);
        } else {
            mapFragment.getMapAsync(this);
        }
        preferenceManager = PreferenceManager.getInstance(this);
        userDetails = preferenceManager.getUserInfo();
        orgId = preferenceManager.getStingData("c_company_id");

        start_addres_value.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_item));
        close_address.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_item));

        draw_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showpDialog();
                myMarker = endMarker.isChecked();
                if (start_addres_value.getText().toString().trim().isEmpty()) {
                    Toast.makeText(CustomMapActivity.this, "Please enter start Address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (close_address.getText().toString().trim().isEmpty()) {
                    Toast.makeText(CustomMapActivity.this, "Please enter close Address", Toast.LENGTH_SHORT).show();
                    return;
                }
                LatLng origin = getLocationFromAddress(CustomMapActivity.this, start_addres_value.getText().toString().trim());
                LatLng dest = getLocationFromAddress(CustomMapActivity.this, close_address.getText().toString().trim());
                drawRoutePath(origin, dest, myMarkerStart, myMarker);
                start_addres_value.setEnabled(false);
                start_addres_value.setText(close_address.getText().toString().trim());
                fiestOrigin = origin;
                close_address.setText("");
                myMarkerStart = false;


              /*  LatLng origin = getLocationFromAddress(CustomMapActivity.this, start_addres_value.getText().toString().trim());
                LatLng dest = getLocationFromAddress(CustomMapActivity.this, close_address.getText().toString().trim());
                drawRoutePath(origin, dest, false, myMarker);
                close_address.setText("");*/


            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bookingID = booking_id.getText().toString().trim();
                if (!bookingID.isEmpty()) {
                    showpDialog();
                    map.snapshot(bitmap -> {
                        Bitmap msnapshot = bitmap; //.createScaledBitmap(bitmap, 140, 140, true);
                        BitmapHolder.getInstance().setUpdatedBitmap(msnapshot);
                        Log.i("custom_map", "taking snapshot");
                        try {
                            sendMapbookingId(bookingID);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    Toast.makeText(CustomMapActivity.this, "Please Enter booking id", Toast.LENGTH_SHORT).show();
                }

            }
        });
        clearMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (map != null) {
                    map.clear();
                    start_addres_value.setEnabled(true);

                    start_addres_value.setText("");
                    close_address.setText("");


                }
            }
        });
    }

    public GeoPoint getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        GeoPoint p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new GeoPoint(location.getLatitude() * 1E6,
                    location.getLongitude() * 1E6);

            return p1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }


    private void sendMapbookingId(String bookingId) throws IOException {
        Log.i("custom_map", "Calling function");
        uploadFile(bookingId);

    }

    private void uploadFile(String bookingId) throws IOException {
        Map<String, RequestBody> map2 = new HashMap<>();
        File f = new File(getApplicationContext().getCacheDir(), "updatedImage.JPEG");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap mBitmap = BitmapHolder.getInstance().getUpdatedBitmap();
        if (mBitmap != null) {
            Bitmap redusedImage2 = ImageResizer.reduceBitmapSize(mBitmap, 240000);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            redusedImage2.compress(Bitmap.CompressFormat.JPEG, 50, bos);
            byte[] bitmapData = bos.toByteArray();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);

            } catch (FileNotFoundException e) {
                e.printStackTrace();

            }
            try {
                fos.write(bitmapData);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Parsing any Media type file
        RequestBody requestBody = RequestBody.create(MediaType.parse("*image/*"), f);
        map2.put("file\"; filename=\"" + f.getName() + "\"", requestBody);
        ApiHandler.UploadImageAPIServise getResponse = ImageProcessingHandler.getClient().create(ApiHandler.UploadImageAPIServise.class);
        Call<UploadResponce> call = getResponse.upload(map2);
        Log.i("custom_map", "Calling function" + map + userDetails.getCompanyId().toString() + bookingId);
        call.enqueue(new Callback<UploadResponce>() {
            @Override
            public void onResponse(Call<UploadResponce> call, Response<UploadResponce> response) {
                Log.i("custom_map", "responsse code = " + response.code());

                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getMessage() != null) {
                        hidepDialog();
                        sendBookingId(bookingId);
                        Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    hidepDialog();
                    Toast.makeText(getApplicationContext(), "problem uploading image", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UploadResponce> call, Throwable t) {
                hidepDialog();
                Toast.makeText(CustomMapActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.v("Response gotten is", t.getMessage());
            }
        });
    }


    private void sendBookingId(String bookingId) {
        // Map is used to multipart the file using okhttp3.RequestBody
        ApiHandler.UploadMapBookingService getResponse = RestClient.getClient().create(ApiHandler.UploadMapBookingService.class);
        Call<UploadResponce> call = getResponse.sendMap(userDetails.getCompanyId().toString(), bookingId);
        Log.i("custom_map", "Calling function" + map + userDetails.getCompanyId().toString() + bookingId);
        call.enqueue(new Callback<UploadResponce>() {
            @Override
            public void onResponse(Call<UploadResponce> call, Response<UploadResponce> response) {
                Log.i("custom_map", "responsse code = " + response.code());

                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getMessage() != null) {
                        hidepDialog();
                        map.clear();
                    }
                } else {
                    hidepDialog();
                }
            }

            @Override
            public void onFailure(Call<UploadResponce> call, Throwable t) {
                hidepDialog();
                Toast.makeText(CustomMapActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.v("Response gotten is", t.getMessage());
            }
        });
    }


    @Override
    public void onLocationChanged(Location location) {
     /*   boolean updateLocationandReport = false;
        if(currentLocation == null){
            currentLocation = location;
            locationUpdatedAt = System.currentTimeMillis();
            updateLocationandReport = true;
        } else {
            long secondsElapsed = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - locationUpdatedAt);
            if (secondsElapsed >= TimeUnit.MILLISECONDS.toSeconds(FASTEST_INTERVAL)){
                // check location accuracy here
                currentLocation = location;
                locationUpdatedAt = System.currentTimeMillis();
                updateLocationandReport = true;
            }
        }
        if(updateLocationandReport){

            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (preferenceManager.getBooleanData("is_trip_started")) {
*//*
                        if (latLngArrayList.size()>1){
                        preferenceManager.saveLocation("latLangList",latLngArrayList);
                        currentRoutePath(latLngArrayList);}
*//*

                        drawRoutePath(true, new LatLng(Double.parseDouble(preferenceManager.getStingData("firstStart_lat")),
                                        Double.parseDouble(preferenceManager.getStingData("firstStart_long"))),
                                new LatLng(Double.parseDouble(preferenceManager.getStingData("start_lat")),
                                        Double.parseDouble(preferenceManager.getStingData("start_long"))),
                                new LatLng(location.getLatitude(), location.getLongitude()), true);
                    }
                    preferenceManager.saveData("start_lat", String.valueOf(location.getLatitude()));
                    preferenceManager.saveData("start_long", String.valueOf(location.getLongitude()));
                }
            }, 6000);
        }*/


    }

    private void drawRoutePath(LatLng origin, LatLng dest, boolean startMarker, boolean EndMarker) {

        if (origin == null || dest == null || map == null) return;

        String url = getUrl(origin, origin, dest);
        FetchUrl FetchUrl = new FetchUrl();
        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);
        //move map camera
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        //   builder.include(garage);
        builder.include(origin);
        builder.include(dest);
        if (fiestOrigin != null) {
            builder.include(fiestOrigin);
        }
        addCurrentMarkers(origin, dest, startMarker, EndMarker);
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 180);
        map.moveCamera(cu);


        // map.moveCamera(CameraUpdateFactory.newLatLngZoom(dest, 13));
        // map.animateCamera(CameraUpdateFactory.newLatLngZoom(geoPoints, 15.0f));

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
                lineOptions.width(15);
                lineOptions.color(Color.BLACK);


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

    private void addCurrentMarkers(LatLng origin, LatLng destination, boolean markerStart, boolean markerEnd) {

        //  Toast.makeText(this, origin.toString(), Toast.LENGTH_SHORT).show();
        if (map != null) {
            MarkerOptions options = new MarkerOptions();

            options.position(origin);
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

            if (markerStart) {
                Marker marker2 = map.addMarker(new MarkerOptions().position(origin)
                        .title("Start")
                        .snippet(getAddress(origin))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_start))
                );
                // marker2.showInfoWindow();

            }
            if (markerEnd) {
                Marker marker3 = map.addMarker(new MarkerOptions().position(destination)
                        .title("End")
                        .snippet(getAddress(destination))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_end))
                );
                //  marker3.showInfoWindow();
            }


            hideProgress();

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

            }
            return addresses.get(0).getLocality() + addresses.get(0).getSubLocality();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
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

    @Override
    public void onClick(View view) {

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
        if (preferenceManager.getStingData("start_lat") == null && preferenceManager.getStingData("start_long") == null) {
            preferenceManager.saveData("start_lat", String.valueOf(geoPoints.latitude));
            preferenceManager.saveData("start_long", String.valueOf(geoPoints.longitude));

        }

    }

    protected void initDialog() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.msg_loading));
        pDialog.setCancelable(true);
    }

    protected void showpDialog() {

        if (!pDialog.isShowing()) pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing()) pDialog.dismiss();
    }
}