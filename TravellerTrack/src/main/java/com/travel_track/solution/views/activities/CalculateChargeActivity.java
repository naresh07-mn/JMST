package com.travel_track.solution.views.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.text.Editable;
import android.text.PrecomputedText;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.travel_track.solution.BuildConfig;
import com.travel_track.solution.R;
import com.travel_track.solution.apihandler.ApiHandler;
import com.travel_track.solution.apihandler.DirectionMatrixApiClient;
import com.travel_track.solution.apihandler.ImageProcessingHandler;
import com.travel_track.solution.apihandler.RestClient;
import com.travel_track.solution.model.BookingModel;
import com.travel_track.solution.model.GoogleDistanceMatrixApiResponse;
import com.travel_track.solution.model.StartTrip;
import com.travel_track.solution.model.UploadResponce;
import com.travel_track.solution.utils.ImageResizer;
import com.travel_track.solution.viewmodel.ParkingDutySlipDetails;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.transform.Result;

import androidx.core.text.PrecomputedTextCompat;
import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalculateChargeActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.wait_time_value)
    @Nullable
    TextView wait_time_value;

    @BindView(R.id.start_trip_reading_value)
    @Nullable
    TextView start_trip_reading_value;

    @BindView(R.id.start_trip_time_value)
    @Nullable
    TextView start_trip_time_value;

    @BindView(R.id.end_trip_reading_value)
    @Nullable
    EditText end_trip_reading_value;

    @BindView(R.id.close_time_value)
    @Nullable
    EditText close_time;
    @BindView(R.id.close_date_value)
    @Nullable
    EditText close_date;

    @BindView(R.id.InputTypeCloseTime)
    @Nullable
    TextInputLayout inputtype_close_time;

    @BindView(R.id.InputTypeCloseDate)
    @Nullable
    TextInputLayout inputtype_close_date;

    @BindView(R.id.InputTypeEndTrip)
    @Nullable
    TextInputLayout InputTypeEndTrip;

    @BindView(R.id.distance_in_km)
    @Nullable
    EditText distance_in_km;

    @BindView(R.id.parking_charge)
    @Nullable
    EditText parking_charge;
    @BindView(R.id.remark)
    @Nullable
    EditText remark;
    @BindView(R.id.dnd_charge)
    @Nullable
    EditText dnd_charge;

    @BindView(R.id.toll_charge)
    @Nullable
    EditText toll_charge;

    @BindView(R.id.garage_to_garage_value)
    @Nullable
    EditText garage_to_garage_value;

    @BindView(R.id.other_charge_desc)
    @Nullable
    EditText other_charge_desc;

    @BindView(R.id.other_charge_amount)
    @Nullable
    EditText other_charge_amount;

    @BindView(R.id.submit)
    @Nullable
    Button submit;

    ApiHandler.CloseDutyAPIService apiServices;
    ApiHandler.DistanceDuration apiService;
    BookingModel bookingDetails;
    final Calendar myCalendar = Calendar.getInstance();
    String endDate;
    String startMeterReading;
    float waitingTime = 0;
    double mCurrentLat=0;
    double mCurrentLong=0;
    String mAddress="";
    ImageView imageView;
    Button pickImage, upload;
    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_PICK_PHOTO = 2;
    private static final int CAMERA_PIC_REQUEST = 1111;

    private static final String TAG = CalculateChargeActivity.class.getSimpleName();
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private Uri fileUri;
    private String mediaPath;
    private Date startDate;
    private Date closeDate;

    private Button btnCapturePicture;

    private String mImageFileLocation = "";
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";
    ProgressDialog pDialog;
    private String postPath;

    @Override
    void onBuildUserInterface() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View pageContent = inflater.inflate(R.layout.activity_calculate_charge, null);
        setViewContents(pageContent);

        garage_to_garage_value.setText("0");
        close_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CalculateChargeActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        if (getIntent().getExtras() != null) {
            Bundle extra = getIntent().getExtras();
            bookingDetails = (BookingModel) extra.getSerializable("booking_details");
            waitingTime = extra.getFloat("waitingTime");
            mCurrentLat = extra.getDouble("mCurrentLat");
            mCurrentLong = extra.getDouble("mCurrentLong");
            mAddress = extra.getString("mAddress");
        }

        startMeterReading = preferenceManager.getStingData("start_meter_reading");
        start_trip_reading_value.setText(startMeterReading);
        if (bookingDetails != null && bookingDetails.getPickUpTime() != null) {
            start_trip_time_value.setText(bookingDetails.getPickUpTime());
        }
        if (bookingDetails != null && BitmapHolder.getInstance().getCloseDSate() != null) {
            close_date.setText(BitmapHolder.getInstance().getCloseDSate());
        } else {
            String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            close_date.setText(date);
        }
        submit.setOnClickListener(this);
        imageView = findViewById(R.id.preview);
        pickImage = findViewById(R.id.pickImage);
        upload = findViewById(R.id.upload);
        pickImage.setOnClickListener(this);
        upload.setOnClickListener(this);
        initDialog();
        int distanceCovered=preferenceManager.getIntData("distanceCovered");
        if (distanceCovered<1000){
            Integer totalReading=Integer.valueOf(startMeterReading)+ 1;
            end_trip_reading_value.setText(totalReading.toString());
        }else {
            Integer totalReading=Integer.valueOf(startMeterReading)+
                    preferenceManager.getIntData("distanceCovered")/1000+1;
            end_trip_reading_value.setText(totalReading.toString());
        }

       // distance_in_km.setText("" + preferenceManager.getStingData("distanceCovered"));

       end_trip_reading_value.setOnFocusChangeListener((v, hasFocus) -> {
            String endTripReading = end_trip_reading_value.getText().toString();

            if (TextUtils.isEmpty(endTripReading)) return;
            int intStartReading = Integer.valueOf(startMeterReading);
            int intEndReading = Integer.valueOf(endTripReading);
            int distance = intEndReading - intStartReading;
            distance_in_km.setText("" + distance);
            if (distance < 0) {

                InputTypeEndTrip.setError("Enter valid meter reading.");
                distance_in_km.setTextColor(Color.RED);
                return;

            } else if (distance > 0) {
            }
            distance_in_km.setTextAppearance(CalculateChargeActivity.this, R.style.text_small);
            distance_in_km.setTextColor(Color.RED);

        });
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        close_time.setText(dateFormat.format(new Date()));
        close_time.addTextChangedListener(mTimeEntryWatcher);

    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "dd/MM/yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            endDate = sdf.format(myCalendar.getTime());
            close_date.setText(sdf.format(myCalendar.getTime()));

        }

    };

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.pickImage:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    upload.setEnabled(false);
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                } else {
                    upload.setEnabled(true);
                }

                new MaterialDialog.Builder(this)
                        .title(R.string.uploadImages)
                        .items(R.array.uploadImages)
                        .itemsIds(R.array.itemIds)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        startActivityForResult(galleryIntent, REQUEST_PICK_PHOTO);
                                        break;
                                    case 1:
                                        captureImage();

                                        break;
                                    case 2:
                                        imageView.setImageResource(R.drawable.ic_launcher);
                                        postPath = null;
                                        break;
                                }
                            }
                        })
                        .show();
                break;
            case R.id.upload:
                if (postPath == null || postPath.equals("")) {
                    Toast.makeText(this, "please select an image ", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    showpDialog();
                    Map<String, RequestBody> map = new HashMap<>();
//                    File file = new File(postPath);
                    File file = new File(getApplicationContext().getCacheDir(), "myImage.JPEG");
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Bitmap bmp = BitmapFactory.decodeFile(postPath);
                    Bitmap redusedBitmap = ImageResizer.reduceBitmapSize(bmp, 240000);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    redusedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, bos);
                    byte[] bitmapData = bos.toByteArray();
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file);

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
                    // Parsing any Media type file
                    RequestBody requestBody = RequestBody.create(MediaType.parse("*image/*"), file);
                    // Parsing any Media type file
                    map.put("file\"; filename=\"" + file.getName() + "\"", requestBody);
                    try {
                        uploadFile(map);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                break;
            case R.id.submit:
                String endTripReading = end_trip_reading_value.getText().toString();
               try {
                   String startdate = BitmapHolder.getInstance().getStartDate();
                   String startTime = BitmapHolder.getInstance().getPickUpTime();
                   String closedate = close_date.getText().toString();
                   String closeTime = close_time.getText().toString();
                   String[] startDateValue =startdate.split("/");
                   int startDay=Integer.parseInt(startDateValue[0]);
                   int startMonth=Integer.parseInt(startDateValue[1]);
                   int startYear=Integer.parseInt(startDateValue[2]);

                   String[] closeDateValue =closedate.split("/");
                   int closeDay=Integer.parseInt(closeDateValue[0]);
                   int closeMonth=Integer.parseInt(closeDateValue[1]);
                   int closeYear=Integer.parseInt(closeDateValue[2]);

                   String[] closeHr = closeTime.split(":");
                   String[] startHR = startTime.split(":");
                   int closeMinuts = Integer.parseInt(closeHr[0]) * 60 + Integer.parseInt(closeHr[1]);
                   int startMinuts = Integer.parseInt(startHR[0]) * 60 + Integer.parseInt(startHR[1]);

                   DateFormat formatter = new SimpleDateFormat("dd/mm/yyyy");
                   try {
                       startDate = formatter.parse(startdate);
                       closeDate = formatter.parse(closedate);
                   } catch (ParseException e) {
                       e.printStackTrace();
                   }

                   inputtype_close_time.setError(null);
                   inputtype_close_date.setError(null);
                   InputTypeEndTrip.setError(null);
                   if (TextUtils.isEmpty(endTripReading)) {
                       InputTypeEndTrip.setError("Enter valid meter reading.");
                       return;
                   }
                   int intStartReading = Integer.valueOf(startMeterReading);
                   int intEndReading = Integer.valueOf(endTripReading);
                   if (intStartReading >= intEndReading) {
                       InputTypeEndTrip.setError("Wrong input. End reading is less than start reading");
                       return;
                   }
                   if (TextUtils.isEmpty(close_time.getText())) {
                       inputtype_close_time.setError("Enter valid time for close duty.");
                       return;
                   }
                   if (closeDate.equals(startDate) && closeMinuts < startMinuts) {
                       inputtype_close_time.setError("Enter valid time for close duty.");
                       return;
                   }
                   if (TextUtils.isEmpty(close_date.getText())) {
                       inputtype_close_date.setError("Enter valid date for close duty.");
                       return;
                   }

                   if (closeYear<startYear) {
                       inputtype_close_date.setError("Enter valid date for close duty.");
                       return;
                   }
                   if (closeYear==startYear&&closeMonth<startMonth){
                       inputtype_close_date.setError("Enter valid date for close duty.");
                       return;
                   }
                   if (closeYear==startYear&&closeMonth==startMonth&&closeDay<startDay){
                       inputtype_close_date.setError("Enter valid date for close duty.");
                       return;
                   }


               }catch (NullPointerException exception){exception.printStackTrace();}
                //created by naresh bhadana on 12 january 2020
                Map<String, RequestBody> map2 = new HashMap<>();
                File f = new File(getApplicationContext().getCacheDir(), "myImage.JPEG");
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bitmap mBitmap = BitmapHolder.getInstance().getBitmap();
                if (mBitmap!=null){
                    Bitmap redusedImage2 = ImageResizer.reduceBitmapSize(mBitmap, 240000);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    redusedImage2.compress(Bitmap.CompressFormat.JPEG, 50, bos);
                    byte[] bitmapData = bos.toByteArray();
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(f);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();

                    }  try {
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
                showpDialog();
                call.enqueue(new Callback<UploadResponce>() {
                    @Override
                    public void onResponse(Call<UploadResponce> call, Response<UploadResponce> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                hidepDialog();
                                String dest = mCurrentLat + "," + mCurrentLong;
                                //falguni "22.631376,88.442426"
                              //  krishna rent car="28.464747,77.193375"
                                getDistanceMatrix("28.575060,77.215710", dest, endTripReading, "10 travel");

                                //    endTrip(endTripReading, "Asian travel house");


//                      Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            hidepDialog();
                            Toast.makeText(getApplicationContext(), "problem uploading image", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UploadResponce> call, Throwable t) {
                        hidepDialog();
                        Toast.makeText(CalculateChargeActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        Log.v("Response gotten is", t.getMessage());
                    }
                });


                break;

        }

    }

    public void getDistanceMatrix(String origin, String destination, String endMeterReading, String routString) {

        apiService = DirectionMatrixApiClient.getClient().create(ApiHandler.DistanceDuration.class);
        Call<GoogleDistanceMatrixApiResponse> call = apiService.getMatrixData(origin, destination, getString(R.string.google_server_key));
        call.enqueue(new Callback<GoogleDistanceMatrixApiResponse>() {
            @Override
            public void onResponse(@NotNull Call<GoogleDistanceMatrixApiResponse> call, Response<GoogleDistanceMatrixApiResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        if (response.body().getRows().get(0).getElements().get(0).getDistance().getText() != null
                                && response.body().getRows().get(0).getElements().get(0).getDuration() != null) {

                            endTrip(endMeterReading,routString,response.body().getRows().get(0).getElements().get(0).getDistance().getValue().toString(),
                                    response.body().getRows().get(0).getElements().get(0).getDuration().getValue().toString());
                            preferenceManager.removeValues("distanceCovered");
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

    private void endTrip(String endMeterReading, String routeString,final String distance,final String duration) {
        showProgress();
        int mMeter = 0;
        int closeGarageTime = 0;
        if (distance != null) {
            mMeter = Integer.parseInt(endMeterReading) + Integer.parseInt(distance) / 1000;
            closeGarageTime = Integer.parseInt(duration) / 60;
        }

        endDate = close_date.getText().toString();
        apiServices = RestClient.getClient().create(ApiHandler.CloseDutyAPIService.class);
        Call<StartTrip> call = apiServices.DutyClose(endMeterReading, endDate, close_time.getText().toString(),Integer.toString(mMeter),Integer.toString(closeGarageTime),
                parking_charge.getText().toString(),
                dnd_charge.getText().toString(), toll_charge.getText().toString(), garage_to_garage_value.getText().toString(), other_charge_desc.getText().toString(),
                other_charge_amount.getText().toString(), 0, bookingDetails.getCompanyName(), bookingDetails.getCompanyId().toString(),
                bookingDetails.getBookingNo(), routeString,mAddress,mCurrentLat,mCurrentLong,remark.getText().toString());

        call.enqueue(new Callback<StartTrip>() {
            @Override
            public void onResponse(Call<StartTrip> call, Response<StartTrip> response) {
                hideProgress();
                // String responseCode = response.body().getMessage().get(0).getResultcode();
                if (response.body().getMessage() != null && response.body().getMessage().get(0).getResultcode().equalsIgnoreCase("0"))
                {
                    preferenceManager.removeValues("distanceCovered");

                    //    Intent mIntent = new Intent(CalculateChargeActivity.this, FareSummaryActivity.class);
                    if (!TextUtils.isEmpty(bookingDetails.getPayMentMode())
                            && !bookingDetails.getPayMentMode().equalsIgnoreCase("credit")) {
                        Intent mIntent = new Intent(CalculateChargeActivity.this, FareSummaryActivity.class);
                        mIntent.putExtra("booking_details", bookingDetails);
                        startActivity(mIntent);
                        finish();
                    } else if (!TextUtils.isEmpty(bookingDetails.getPayMentMode())
                            && !bookingDetails.getPayMentMode().equalsIgnoreCase("cash")) {
                        Intent mIntent = new Intent(CalculateChargeActivity.this, CustomerFeedbackActivity.class);
                        mIntent.putExtra("booking_details", bookingDetails);
                        startActivity(mIntent);
                        finish();
                    }

                } else {
                    Snackbar.make(findViewById(android.R.id.content), "please try again", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StartTrip> call, Throwable t) {
                hideProgress();
                Toast.makeText(CalculateChargeActivity.this, "Some thing went wrong please try later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    boolean isValid = true;
    private final TextWatcher mTimeEntryWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String working = s.toString();
            isValid = true;
            if (working.length() == 2 && before == 0) {
                if (Integer.parseInt(working) < 1 || Integer.parseInt(working) > 23) {
                    isValid = false;
                } else {
                    working += ":";
                    close_time.setText(working);
                    close_time.setSelection(working.length());
                }
            } else if (working.length() == 5 && before == 0) {
                String enterMin = working.substring(3);
                if (Integer.parseInt(enterMin) >= 60) {
                    isValid = false;
                }
            } else if (working.length() != 5) {
                isValid = false;
            }
            if (!isValid) {
                inputtype_close_time.setError("Enter Valid Time");
            } else {
                inputtype_close_time.setError(null);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

    };

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO || requestCode == REQUEST_PICK_PHOTO) {
                if (data != null) {
                    // Get the Image from data
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    assert cursor != null;
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    mediaPath = cursor.getString(columnIndex);

                    //   Toast.makeText(this, mediaPath, Toast.LENGTH_SHORT).show();
                    // Set the Image in ImageView for Previewing the Media
                    Bitmap bitmap=BitmapFactory.decodeFile(mediaPath);
                    Bitmap redusedBitmap=ImageResizer.reduceBitmapSize(bitmap, 240000);
                    imageView.setImageBitmap(redusedBitmap);
                    //0 Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();

/*
                    // Set the Image in ImageView for Previewing the Media
                    imageView.setImageBitmap(BitmapFactory.decodeFile(mediaPath));*/
                    cursor.close();


                    postPath = mediaPath;
                }


            } else if (requestCode == CAMERA_PIC_REQUEST) {
                if (Build.VERSION.SDK_INT > 21) {

                    Glide.with(this).load(mImageFileLocation).into(imageView);
                    postPath = mImageFileLocation;

                } else {
                    Glide.with(this).load(fileUri).into(imageView);
                    postPath = fileUri.getPath();

                }

            }

        } else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, "Sorry, there was an error!", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Checking device has camera hardware or not
     */
    private boolean isDeviceSupportCamera() {
        // this device has a camera
        // no camera on this device
        return getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
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

    /**
     * Launching camera app to capture image
     */
    private void captureImage() {
        if (Build.VERSION.SDK_INT > 23) { //use this if Lollipop_Mr1 (API 22) or above
            Intent callCameraApplicationIntent = new Intent();
            callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

            // We give some instruction to the intent to save the image
            File photoFile = null;

            try {
                // If the createImageFile will be successful, the photo file will have the address of the file
                photoFile = createImageFile();
                // Here we call the function that will try to catch the exception made by the throw function
            } catch (IOException e) {
                Logger.getAnonymousLogger().info("Exception error in generating the file");
                e.printStackTrace();
            }
            // Here we add an extra file to the intent to put the address on to. For this purpose we use the FileProvider, declared in the AndroidManifest.
            Uri outputUri = FileProvider.getUriForFile(
                    this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    photoFile);
            callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);

            // The following is a new line with a trying attempt
            callCameraApplicationIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Logger.getAnonymousLogger().info("Calling the camera App by intent");

            // The following strings calls the camera app and wait for his file in return.
            startActivityForResult(callCameraApplicationIntent, CAMERA_PIC_REQUEST);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // start the image capture Intent
            startActivityForResult(intent, CAMERA_PIC_REQUEST);
        }


    }

    File createImageFile() throws IOException {
        Logger.getAnonymousLogger().info("Generating the image - method started");

        // Here we create a "non-collision file name", alternatively said, "an unique filename" using the "timeStamp" functionality
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmSS").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp;
        // Here we specify the environment location and the exact path where we want to save the so-created file
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/photo_saving_app");
        Logger.getAnonymousLogger().info("Storage directory set");

        // Then we create the storage directory if does not exists
        if (!storageDirectory.exists()) storageDirectory.mkdir();

        // Here we create the file using a prefix, a suffix and a directory
        File image = new File(storageDirectory, imageFileName + ".jpg");
        // File image = File.createTempFile(imageFileName, ".jpg", storageDirectory);

        // Here the location is saved into the string mImageFileLocation
        Logger.getAnonymousLogger().info("File name and path set");

        mImageFileLocation = image.getAbsolutePath();
        // fileUri = Uri.parse(mImageFileLocation);
        // The file is returned to the previous intent across the camera application
        return image;
    }


    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }


    /**
     * Receiving activity result method will be called after closing the camera
     * */

    /**
     * ------------ Helper Methods ----------------------
     * */

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    // Uploading Image/Video
    private void sendParkingDutySlip() {
        ApiHandler.ParkingDutySlipImageAPIServise getResponse = RestClient.getClient().create(ApiHandler.ParkingDutySlipImageAPIServise.class);
        Call<ParkingDutySlipDetails> call = getResponse.sendDutySlip(bookingDetails.getBookingNo(), bookingDetails.getCompanyId().toString());
        Log.i(String.valueOf(CalculateChargeActivity.this), bookingDetails.getBookingNo() + " and companyId" + bookingDetails.getCompanyId());
        call.enqueue(new Callback<ParkingDutySlipDetails>() {
            @Override
            public void onResponse(Call<ParkingDutySlipDetails> call, Response<ParkingDutySlipDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Toast.makeText(CalculateChargeActivity.this, "Data save succesfully", Toast.LENGTH_SHORT).show();

                } else {
                    //Toast.makeText(CalculateChargeActivity.this, "DutySlipNot Save", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ParkingDutySlipDetails> call, Throwable throwable) {

            }
        });

    }

    private void uploadFile(Map<String, RequestBody> map) throws IOException {
        // Map is used to multipart the file using okhttp3.RequestBody
        ApiHandler.UploadImageAPIServise getResponse = ImageProcessingHandler.getClient().create(ApiHandler.UploadImageAPIServise.class);
        Call<UploadResponce> call = getResponse.upload(map);
        call.enqueue(new Callback<UploadResponce>() {
            @Override
            public void onResponse(Call<UploadResponce> call, Response<UploadResponce> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        hidepDialog();
                        postPath = "";
                        imageView.setImageResource(R.drawable.ic_launcher);
                        sendParkingDutySlip();
//                        Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
                else {
                    hidepDialog();
                    Toast.makeText(getApplicationContext(), "problem uploading image", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UploadResponce> call, Throwable t) {
                hidepDialog();
                Toast.makeText(CalculateChargeActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.v("Response gotten is", t.getMessage());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                upload.setEnabled(true);

            }
        }
    }
}
