package com.travel_track.solution.views.activities;

import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.travel_track.solution.R;
import com.travel_track.solution.apihandler.ApiHandler;
import com.travel_track.solution.apihandler.RestClient;
import com.travel_track.solution.data.PreferenceManager;
import com.travel_track.solution.model.BillingDetails;
import com.travel_track.solution.model.BillingModel;
import com.travel_track.solution.model.BookingDetails;
import com.travel_track.solution.model.BookingModel;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

import androidx.annotation.Nullable;
import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FareSummaryActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.collection_amount)@Nullable
    TextView collection_amount;

    @BindView(R.id.collection_date)@Nullable
    TextView collection_date;

    @BindView(R.id.bill_no)@Nullable
    TextView bill_no;
    @BindView(R.id.company_name)@Nullable
    TextView company_name;

    @BindView(R.id.thanks)@Nullable
    TextView txtThanks;
    @BindView(R.id.driver_name)@Nullable
    TextView driver_name;
    @BindView(R.id.paid_amount)@Nullable
    TextView paid_amount;
    @BindView(R.id.travel_start_address)@Nullable
    TextView travel_start_address;
    @BindView(R.id.travel_end_address)@Nullable
    TextView travel_end_address;
    @BindView(R.id.travel_start_time)@Nullable
    TextView travel_start_time;
    @BindView(R.id.travel_end_time)@Nullable
    TextView travel_end_time;

    @BindView(R.id.travel_distance)@Nullable
    TextView travel_distance;
    @BindView(R.id.travel_time)@Nullable
    TextView travel_time;
    @BindView(R.id.car_type)@Nullable
    TextView car_type;


    @BindView(R.id.base_fare_label)@Nullable
    TextView base_fare_label;
    @BindView(R.id.base_fare_value)@Nullable
    TextView base_fare_value;

    @BindView(R.id.additional_fare_label)@Nullable
    TextView additional_fare_label;
    @BindView(R.id.additional_fare_value)@Nullable
    TextView additional_fare_value;

    @BindView(R.id.total_fare_label)@Nullable
    TextView total_fare_label;
    @BindView(R.id.total_fare_value)@Nullable
    TextView total_fare_value;

    @BindView(R.id.image_3)@Nullable
    ImageView mapView;

    @BindView(R.id.submit)@Nullable
    Button feedback;


    BookingModel bookingDetails;
    ApiHandler.BillingDetailsApiService apiServices;
    SimpleDateFormat inputFormatter;
    SimpleDateFormat postFormatter;
    @Override
    void onBuildUserInterface() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View pageContent = inflater.inflate(R.layout.activity_fare_summary, null);
        setViewContents(pageContent);

        mapView.setImageBitmap(BitmapHolder.getInstance().getBitmap());

        inputFormatter = new SimpleDateFormat("dd mmm yyyy");
        postFormatter = new SimpleDateFormat("dd MMMM yyyy");
        if(getIntent().getExtras() != null) {
            bookingDetails = (BookingModel) getIntent().getSerializableExtra("booking_details");
        }
        feedback.setOnClickListener(this);
        requestForBillingDetails();
    }

    public void requestForBillingDetails(){
        if (bookingDetails == null) return;
         apiServices = RestClient.getClient().create(ApiHandler.BillingDetailsApiService.class);
         Call<BillingDetails> call = apiServices.billingSave(bookingDetails.getBookingNo(), bookingDetails.getCompanyId());
         call.enqueue(new Callback<BillingDetails>() {
             @Override
             public void onResponse(Call<BillingDetails> call, Response<BillingDetails> response) {
                 if(response.body().getData().size()>0){
                     BillingModel data = response.body().getData().get(0);
                     String amount = data.getTotalBillAmount();
                     collection_amount.setText(getString(R.string.currency_symbol)+""+amount);

                     /*Date billingDate = null;
                     try {
                         billingDate = inputFormatter.parse(data.getBilldate());
                     } catch (ParseException e) {
                         e.printStackTrace();
                     }*/
                     //collection_date.setText(String.format(getString(R.string.app_text_collection_date), data.getBilldate()));

                     String strBillNo = data.getBillno();
                     bill_no.setText("Bill #"+Html.fromHtml(strBillNo)+", Date:"+String.format(getString(R.string.app_text_collection_date), data.getBilldate()));

                     company_name.setText(data.getCompanyName());

                     txtThanks.setText(String.format(getString(R.string.thanks_to_travel_user), data.getGuest()));

                     if(userDetails.getName()!=null)
                     driver_name.setText(userDetails.getName());

                     travel_start_time.setText(bookingDetails.getPickUpTime());
                     DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                     if(data.getCloseTime()!=null) {
                         travel_end_time.setText("" + data.getCloseTime());
                     } else {
                         travel_end_time.setText("" + dateFormat.format(new Date()));
                     }
                     travel_start_address.setText(bookingDetails.getPickUpLocation());
                     travel_end_address.setText(bookingDetails.getDropAddress());

                     travel_distance.setText(data.getBilledKm() +" km");
                     travel_time.setText(data.getBilledHour() + " hours");
                     car_type.setText(data.getCarname());

                     base_fare_label.setText(data.getBillPrgDutyDetail());
                     base_fare_value.setText(data.getBillPrgAmount());

                     /*additional_fare_label.setText(data.getBillPrgDutyDetail());
                     additional_fare_value.setText(data.getBillPrgAmount());*/

                     total_fare_label.setText(data.getTotalBillPrgText());
                     total_fare_value.setText(data.getTotalBillPrg());

                     paid_amount.setText(getString(R.string.currency_symbol)+data.getTotalBillAmount());
                     ImageView imageView = findViewById(R.id.driver_image);
                     Glide.with(FareSummaryActivity.this).load(userDetails.getProfileUrl()).into(imageView);
                 } else {
                     if(response.body()!=null && response.body().getMessage()!=null && response.body().getMessage().get(0).getMessage()!=null) {
                         Snackbar.make(findViewById(android.R.id.content), response.body().getMessage().get(0).getMessage(), Snackbar.LENGTH_SHORT).show();
                     } else {
                         Snackbar.make(findViewById(android.R.id.content), getString(R.string.error), Snackbar.LENGTH_SHORT).show();
                     }
                 }
             }

             @Override
             public void onFailure(Call<BillingDetails> call, Throwable t) {
                 Snackbar.make(findViewById(android.R.id.content), getString(R.string.error), Snackbar.LENGTH_SHORT).show();
             }
         });
    }

    @Override
    public void onClick(View v) {
        Intent mIntent = new Intent(FareSummaryActivity.this, CustomerFeedbackActivity.class);
        mIntent.putExtra("booking_details", bookingDetails);
        startActivity(mIntent);
        finish();
    }

}
