package com.travel_track.solution.views.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.travel_track.solution.R;
import com.travel_track.solution.apihandler.ApiHandler;
import com.travel_track.solution.apihandler.RestClient;
import com.travel_track.solution.interfaces.DialogClickListener;
import com.travel_track.solution.interfaces.OnAddressResponse;
import com.travel_track.solution.model.CarGroupDetails;
import com.travel_track.solution.model.CarGroupModel;
import com.travel_track.solution.model.CityDetails;
import com.travel_track.solution.model.CityModel;
import com.travel_track.solution.model.TourCodeDetails;
import com.travel_track.solution.model.TourCodeModel;
import com.travel_track.solution.utils.AppUtils;
import com.travel_track.solution.views.adapter.PlacesAutoCompleteAdapter;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.widget.AppCompatSpinner;
import butterknife.BindView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.QueryMap;

public class UserTripBookingActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.button1)
    @Nullable
    Button button1;
    @BindView(R.id.customer_name)
    @Nullable
    EditText customer_name;
    @BindView(R.id.ddl_city)
    @Nullable
    AppCompatSpinner ddl_city;
    @BindView(R.id.ddl_payment_mode)
    @Nullable
    AppCompatSpinner ddl_payment_mode;
    @BindView(R.id.guest_name)
    @Nullable
    EditText guest_name;
    @BindView(R.id.mobile_no)
    @Nullable
    EditText mobile_no;
    @BindView(R.id.employee_id)
    @Nullable
    EditText employee_id;
    @BindView(R.id.file_code)
    @Nullable
    EditText file_code;
    @BindView(R.id.booking_type)
    @Nullable
    EditText booking_type;
    @BindView(R.id.ddl_tour_code)
    @Nullable
    AppCompatSpinner ddl_tour_code;
    @BindView(R.id.reporting_address)
    @Nullable
    AutoCompleteTextView reporting_address;
    @BindView(R.id.destination_address)
    @Nullable
    AutoCompleteTextView destination_address;
    @BindView(R.id.start_date)
    @Nullable
    EditText start_date;
    @BindView(R.id.start_time)
    @Nullable
    EditText start_time;
    @BindView(R.id.close_date)
    @Nullable
    EditText close_date;
    @BindView(R.id.close_time)
    @Nullable
    EditText close_time;
    @BindView(R.id.ddl_car_group)
    @Nullable
    AppCompatSpinner ddl_car_group;

    private List<CityModel> cityList = new ArrayList<>();
    private final List<String> paymentMode = new ArrayList<>();
    private List<TourCodeModel> tourCode = new ArrayList<>();
    private List<CarGroupModel> carGroup = new ArrayList<>();


    private ApiHandler.GetCity getCityApiService;
    private ApiHandler.GetTourCode getTourCodeApiService;
    private ApiHandler.GetCarGroup getCarGroupApiService;

    private ApiHandler.DoGuestBooking doGuestBooking;

    private ArrayAdapter cityAdapter;
    private ArrayAdapter tourCodeAdapter;
    private ArrayAdapter carGroupAdapter;

    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    void onBuildUserInterface() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View pageContent = inflater.inflate(R.layout.activity_user_booking, null);
        setViewContents(pageContent);
        if (userDetails.getCustomerName() != null) {
            customer_name.setText(userDetails.getCustomerName());
        }
        paymentMode.add("Cash");
        paymentMode.add("Credit");
        booking_type.setText("L");
        loadPaymentMode();
        loadApiData();
        loadUIData();
        button1.setOnClickListener(this);
    }

    private void loadApiData() {
        getCityApiService = RestClient.getClient().create(ApiHandler.GetCity.class);
        Call<CityDetails> cityCall = getCityApiService.getCity(userDetails.getCustomerID(), userDetails.getCompanyId());
        cityCall.enqueue(new Callback<CityDetails>() {
            @Override
            public void onResponse(Call<CityDetails> call, Response<CityDetails> response) {
                if (cityList == null) {
                    cityList = new ArrayList<>();
                }
                cityList.clear();
                if (response != null && response.body() != null && response.body().getData() != null) {
                    cityList = response.body().getData();
                    loadCity();
                }
            }

            @Override
            public void onFailure(Call<CityDetails> call, Throwable t) {

            }
        });

        getTourCodeApiService = RestClient.getClient().create(ApiHandler.GetTourCode.class);
        Call<TourCodeDetails> tourCodeCall = getTourCodeApiService.getTourCode(userDetails.getCompanyId());
        tourCodeCall.enqueue(new Callback<TourCodeDetails>() {
            @Override
            public void onResponse(Call<TourCodeDetails> call, Response<TourCodeDetails> response) {
                if (tourCode == null) {
                    tourCode = new ArrayList<>();
                }
                tourCode.clear();
                if (response != null && response.body() != null && response.body().getData() != null) {
                    tourCode = response.body().getData();
                    loadTourCode();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });

        getCarGroupApiService = RestClient.getClient().create(ApiHandler.GetCarGroup.class);
        Call<CarGroupDetails> carGroupCall = getCarGroupApiService.getCarGroup();
        carGroupCall.enqueue(new Callback<CarGroupDetails>() {
            @Override
            public void onResponse(Call<CarGroupDetails> call, Response<CarGroupDetails> response) {
                if (carGroup == null) {
                    carGroup = new ArrayList<>();
                }
                carGroup.clear();
                if (response != null && response.body() != null && response.body().getData() != null) {
                    carGroup = response.body().getData();
                    loadCarGroup();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    private void loadUIData() {
        start_date.setKeyListener(null);
        start_date.setOnClickListener(onClickListener);
        close_date.setKeyListener(null);
        close_date.setOnClickListener(onClickListener);

        start_time.setKeyListener(null);
        start_time.setOnClickListener(onClickListener);
        close_time.setKeyListener(null);
        close_time.setOnClickListener(onClickListener);

        reporting_address.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_item));
        destination_address.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_item));
    }

    View.OnClickListener onClickListener = v -> {
        if (v == start_date || v == close_date) {
            setDate((EditText) v);
        } else if (v == start_time || v == close_time) {
            setTime((EditText) v);
        }
    };

    private void setTime(EditText editText) {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> editText.setText(pad(hourOfDay) + ":" + pad(minute)), mHour, mMinute, true);
        timePickerDialog.show();
    }

    private void setDate(EditText editText) {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> editText.setText(pad(dayOfMonth) + "-" +pad((monthOfYear + 1)) + "-" + year), mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void loadCity() {
        cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityList);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ddl_city.setAdapter(cityAdapter);
        ddl_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void loadTourCode() {
        tourCodeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tourCode);
        tourCodeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ddl_tour_code.setAdapter(tourCodeAdapter);
        ddl_tour_code.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void loadCarGroup() {
        carGroupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, carGroup);
        carGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ddl_car_group.setAdapter(carGroupAdapter);
        ddl_car_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void loadPaymentMode() {
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentMode);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ddl_payment_mode.setAdapter(adapter);
        ddl_payment_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        button1.setEnabled(false);
        if (TextUtils.isEmpty(reporting_address.getText().toString())
                || TextUtils.isEmpty(booking_type.getText().toString())
                || TextUtils.isEmpty(destination_address.getText().toString())) {
            Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_field_required), 4000).show();
            button1.setEnabled(true);
            return;
        } else if (!booking_type.getText().toString().matches(".*[LOPTN].*")) {
            Snackbar.make(findViewById(android.R.id.content), getString(R.string.wrong_input), 3000).show();
            button1.setEnabled(true);
            return;
        }
        showProgress();
        AppUtils.getLocationFromAddress(this, reporting_address.getText().toString(), new OnAddressResponse() {
                    @Override
                    public void onResponse(LatLng source) {
                        AppUtils.getLocationFromAddress(UserTripBookingActivity.this, destination_address.getText().toString(), new OnAddressResponse() {
                            @Override
                            public void onResponse(LatLng destination) {
                                if (source != null && destination != null) {
                                    sendRequest(source, destination);
                                } else {
                                    button1.setEnabled(true);
                                    hideProgress();
                                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.incorrect_address), 3000).show();
                                }
                            }
                        });
                    }
                });
                /*AppUtils.getLocationFromAddress(this, reporting_address.getText().toString(), source -> AppUtils.getLocationFromAddress(UserTripBookingActivity.this, destination_address.getText().toString(), destination -> {
                    if (source != null && destination != null) {
                        submitForm(source, destination);
                    } else {
                        button1.setEnabled(true);
                        hideProgress();
                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.incorrect_address), 3000).show();
                        return;
                    }
                }));*/
    }

    private void sendRequest(LatLng source, LatLng dest) {

        double sourceLat = 0, sourceLong = 0, destLat = 0, destLong = 0;
        if (source != null) {
            sourceLat = source.latitude;
            sourceLong = source.longitude;
        }
        if (dest != null) {
            destLat =  dest.latitude;
            destLong= dest.longitude;
        }
        doGuestBooking = RestClient.getClient().create(ApiHandler.DoGuestBooking.class);
        /*Call<ResponseBody> call = doGuestBooking.doGuestBooking(""+userDetails.getCustomerID(), ""+cityList.get(ddl_city.getSelectedItemPosition()).getId(),
                ""+paymentMode.get(ddl_payment_mode.getSelectedItemPosition()), ""+carGroup.get(ddl_car_group.getSelectedItemPosition()).getCarID(),
                ""+start_date.getText().toString(), ""+reporting_address.getText().toString(),
                ""+start_time.getText().toString(), ""+close_date.getText().toString(), ""+close_time.getText().toString(),
                ""+booking_type.getText().toString(), "confirm", ""+guest_name.getText().toString(),
                ""+mobile_no.getText().toString(), ""+userDetails.getCompanyId(),
                ""+tourCode.get(ddl_tour_code.getSelectedItemPosition()).getTourId(), ""+userDetails.getFirstName(),
                ""+userDetails.getContactNo(), ""+file_code.getText().toString(), ""+employee_id.getText().toString(),
                ""+destination_address.getText().toString(), ""+sourceLat, ""+sourceLong, ""+destLat,
                ""+destLong, ""+userDetails.getUserid());*/
        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("CustomerId",""+userDetails.getCustomerID());
        queryMap.put("City",""+cityList.get(ddl_city.getSelectedItemPosition()).getId());
        queryMap.put("PaymentMode",""+paymentMode.get(ddl_payment_mode.getSelectedItemPosition()));
        queryMap.put("CarGroup",""+carGroup.get(ddl_car_group.getSelectedItemPosition()).getCarID());
        queryMap.put("StartDate",""+start_date.getText().toString());
        queryMap.put("ReportingAddress",""+reporting_address.getText().toString());
        queryMap.put("ReporingTime",""+start_time.getText().toString());
        queryMap.put("CloseDate",""+close_date.getText().toString());
        queryMap.put("CloseTime",""+close_time.getText().toString());
        queryMap.put("BookingType",""+booking_type.getText().toString());
        queryMap.put("BookingStatus","confirm");
        queryMap.put("GuestName",""+guest_name.getText().toString());
        queryMap.put("GuestContactNo",""+userDetails.getContactNo());
        queryMap.put("CompanyId",""+userDetails.getCompanyId());
        queryMap.put("TourCode",""+tourCode.get(ddl_tour_code.getSelectedItemPosition()).getTourId());
        queryMap.put("BookedBy",""+userDetails.getFirstName());
        queryMap.put("BookedByContactNo",""+userDetails.getContactNo());
        queryMap.put("FileCode",""+file_code.getText().toString());
        queryMap.put("EmployeeID",""+employee_id.getText().toString());
        queryMap.put("DropLocation",""+destination_address.getText().toString());
        queryMap.put("PickUpLatitude",""+sourceLat);
        queryMap.put("PickUpLongitude",""+sourceLong);
        queryMap.put("DropLatitude",""+destLat);
        queryMap.put("DropLongitude",""+destLong);
        queryMap.put("BookerId",""+userDetails.getUserid());

        Call<ResponseBody> call = doGuestBooking.doGuestBooking(queryMap);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                button1.setEnabled(true);
                hideProgress();
                if (response.body() == null) {
                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.error), 1000).show();
                } else {
                    showBlockingMessage(new DialogClickListener() {
                        @Override
                        public void onButton1Click() {
                            finish();
                        }

                        @Override
                        public void onButton2Click() {

                        }
                    }, null, getString(R.string.booking_added), getString(R.string.text_ok), null);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                button1.setEnabled(true);
                hideProgress();
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.error), 1000).show();
            }
        });
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + c;
    }
}
