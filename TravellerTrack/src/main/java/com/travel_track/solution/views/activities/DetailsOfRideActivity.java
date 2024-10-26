package com.travel_track.solution.views.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.travel_track.solution.R;
import com.travel_track.solution.model.BookingModel;

import androidx.annotation.Nullable;
import butterknife.BindView;

public class DetailsOfRideActivity extends BaseActivity {

    @BindView(R.id.travel_track_button)
    @Nullable
    ImageView travel_track_button;

    @BindView(R.id.booking_id_value)
    @Nullable
    TextView booking_id_value;

    @BindView(R.id.duty_slip_no_value)
    @Nullable
    TextView duty_slip_id_value;

    @BindView(R.id.corporate_name_value)
    @Nullable
    TextView corporate_name_value;

    @BindView(R.id.pickup_location_value)
    @Nullable
    TextView pickup_location_value;

    @BindView(R.id.passenger_name_value)
    @Nullable
    TextView passenger_name_value;

    @BindView(R.id.mobile_number_value)
    @Nullable
    TextView mobile_number_value;

    @BindView(R.id.type_of_service_value)
    @Nullable
    TextView type_of_service_value;

    @BindView(R.id.drop_location_value)
    @Nullable
    TextView drop_location_value;

    @BindView(R.id.pickup_date_time_value)
    @Nullable
    TextView pickup_date_time_value;

    @BindView(R.id.booking_status_value)
    @Nullable
    TextView booking_status_value;

    @BindView(R.id.employee_id_value)
    @Nullable
    TextView employee_id_value;

    @BindView(R.id.file_code_value)
    @Nullable
    TextView file_code_value;
    @BindView(R.id.car_number)
    @Nullable
    TextView car_number;

    @BindView(R.id.payment_mode_value)
    @Nullable
    TextView payment_mode_value;


    private String bookingId;
    private String dutySlipNo;
    private String pickupAddress;
    private String corporateName;
    private String passengerName;
    private String mobileNo;
    private String typeOfService;
    private String dropAddress;
    private String pickupDateTime;
    private String bookingStatus;
    private String carNumber;
    private String employeeId;
    private String fileCode;
    private String paymentMethod;

    BookingModel bookingModel;
    String bookingType = "completed";

    @Override
    void onBuildUserInterface() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View pageContent = inflater.inflate(R.layout.activity_my_rides_description, null);
        setViewContents(pageContent);
        Bundle extra = getIntent().getExtras();
        bookingType = extra.getString("type");
        travel_track_button.setVisibility(View.GONE);
        if (bookingType.equalsIgnoreCase("alert")) {
            if (userTypeDriver) {
                travel_track_button.setVisibility(View.VISIBLE);
            }
        }

        travel_track_button.setOnClickListener(v -> {
            Intent mIntent = new Intent(DetailsOfRideActivity.this, HomeActivity.class);
            mIntent.putExtra("type", bookingType);
            mIntent.putExtra("booking_details", bookingModel);
            startActivity(mIntent);
        });
        if (!TextUtils.isEmpty(mobile_number_value.getText().toString()))
            mobile_number_value.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mobile_number_value.getText().toString()));
                startActivity(intent);
            });
        requestForDetails();
    }

    private void requestForDetails() {
        if (getIntent().getExtras() != null) {
            Intent extra = getIntent();
            bookingModel = (BookingModel) extra.getSerializableExtra("booking_details");
            if (bookingModel != null) {
                bookingId = bookingModel.getBKGNo();
                dutySlipNo = bookingModel.getDSNo();
                carNumber = bookingModel.getCarNo();
                pickupAddress = bookingModel.getPickUpLocation();
                corporateName = bookingModel.getClientCompanyName();
                passengerName = bookingModel.getGuestName();
                mobileNo = bookingModel.getGuestMobileNo();
                typeOfService = bookingModel.getVehicleType();
                dropAddress = bookingModel.getDropAddress();
                pickupDateTime = bookingModel.getPickUpDate() + " " + bookingModel.getPickUpTime();
                bookingStatus = bookingModel.getBookingStatus();
                employeeId = bookingModel.getEmployeeId();
                fileCode = bookingModel.getFileCode();
                paymentMethod = bookingModel.getPayMentMode();
            }

            if (!TextUtils.isEmpty(bookingId))
                booking_id_value.setText(bookingId);
            if (!TextUtils.isEmpty(dutySlipNo))
                duty_slip_id_value.setText(dutySlipNo);
            if (!TextUtils.isEmpty(corporateName))
                corporate_name_value.setText(corporateName);
            if (!TextUtils.isEmpty(carNumber))
                car_number.setText(carNumber);
            if (!TextUtils.isEmpty(pickupAddress))
                pickup_location_value.setText(pickupAddress);
            if (!TextUtils.isEmpty(passengerName))
                passenger_name_value.setText(passengerName);
            if (!TextUtils.isEmpty(mobileNo))
                mobile_number_value.setText(mobileNo);
            if (!TextUtils.isEmpty(typeOfService))
                type_of_service_value.setText(typeOfService);
            if (!TextUtils.isEmpty(dropAddress))
                drop_location_value.setText(dropAddress);
            if (!TextUtils.isEmpty(pickupDateTime))
                pickup_date_time_value.setText(pickupDateTime);
            if (!TextUtils.isEmpty(bookingStatus))
                booking_status_value.setText(bookingStatus);
            if (!TextUtils.isEmpty(employeeId))
                employee_id_value.setText(employeeId);
            if (!TextUtils.isEmpty(fileCode))
                file_code_value.setText(fileCode);
            if (!TextUtils.isEmpty(paymentMethod))
                payment_mode_value.setText(paymentMethod);
        }
    }
}
