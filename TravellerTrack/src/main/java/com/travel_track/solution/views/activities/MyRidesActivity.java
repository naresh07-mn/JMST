package com.travel_track.solution.views.activities;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.travel_track.solution.R;
import com.travel_track.solution.apihandler.ApiHandler;
import com.travel_track.solution.apihandler.RestClient;
import com.travel_track.solution.data.PreferenceManager;
import com.travel_track.solution.model.BookingDetails;
import com.travel_track.solution.model.BookingModel;
import com.travel_track.solution.model.LoginDetails;
import com.travel_track.solution.model.LoginModel;
import com.travel_track.solution.views.adapter.MyRidesAdapter;
import com.travel_track.solution.views.components.RecyclerItemClickListener;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRidesActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.my_rides_list)@Nullable
    RecyclerView myRidesLists;

    @BindView(R.id.current_rides)@Nullable
    TextView current_rides;

    @BindView(R.id.future_rides)@Nullable
    TextView future_rides;

    @BindView(R.id.pending_rides)@Nullable
    TextView pending_rides;

    @BindView(R.id.pending_rides_seprator)@Nullable
    View pending_rides_seprator;

    @BindView(R.id.completed_ride)@Nullable
    TextView completed_ride;


    ArrayList rideLists = new ArrayList();
    MyRidesAdapter adapter;

    ApiHandler.BookingDetailsAPIService bookingDetailsAPIService;
    ApiHandler.GuestBookingDetailsAPIService guestBookingDetailsAPIService;
    LoginModel loginModel;

    String requestTypeCurrent = "alert";
    String requestTypePending = "pending";
    String requestTypeFuture = "future";
    String requestTypeComplete = "complete";
    String type = "alert";
    List<BookingModel> listItems = new ArrayList<>();

    @Override
    void onBuildUserInterface() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View pageContent = inflater.inflate(R.layout.activity_my_rides, null);
        setViewContents(pageContent);
        adapter = new MyRidesAdapter(this, rideLists);
        myRidesLists.setLayoutManager(new LinearLayoutManager(this));
        myRidesLists.setAdapter(adapter);
        myRidesLists.addOnItemTouchListener(new RecyclerItemClickListener(this, (view, position) -> {
            if(listItems != null && listItems.size()>position) {
                Intent mIntent = new Intent(MyRidesActivity.this, DetailsOfRideActivity.class);
                mIntent.putExtra("booking_details", listItems.get(position));
                mIntent.putExtra("type", type);
                startActivity(mIntent);
            }
        }));
        loginModel = PreferenceManager.getInstance(this).getUserInfo();
        requestForBookingDetails(requestTypeCurrent);
        current_rides.setBackgroundColor(getResources().getColor(R.color.gray));
        current_rides.setOnClickListener(this);
        future_rides.setOnClickListener(this);
        pending_rides.setOnClickListener(this);
        completed_ride.setOnClickListener(this);
        if(userDetails!=null && userDetails.getLoginMode()!=null
                && !userDetails.getLoginMode().equalsIgnoreCase("Driver")) {
            pending_rides.setVisibility(View.VISIBLE);
            pending_rides_seprator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        handleRideFeatures(v);
    }

    private void handleRideFeatures(View v){
        current_rides.setBackgroundColor(getResources().getColor(R.color.app_color_black));
        pending_rides.setBackgroundColor(getResources().getColor(R.color.app_color_black));
        future_rides.setBackgroundColor(getResources().getColor(R.color.app_color_black));
        completed_ride.setBackgroundColor(getResources().getColor(R.color.app_color_black));
        if(v == current_rides){
            current_rides.setBackgroundColor(getResources().getColor(R.color.gray));
            requestForBookingDetails(requestTypeCurrent);
            type ="alert";
        } else if(v == pending_rides){
            pending_rides.setBackgroundColor(getResources().getColor(R.color.gray));
            requestForBookingDetails(requestTypePending);
            type ="pending";
        } else if(v == future_rides){
            future_rides.setBackgroundColor(getResources().getColor(R.color.gray));
            requestForBookingDetails(requestTypeFuture);
            type ="future";
        } else {
            completed_ride.setBackgroundColor(getResources().getColor(R.color.gray));
            requestForBookingDetails(requestTypeComplete);
            type ="completed";
        }
    }

    private void requestForBookingDetails(String requestType) {
        Call<BookingDetails> call;
        showProgress();
        if(userTypeDriver){
            bookingDetailsAPIService = RestClient.getClient().create(ApiHandler.BookingDetailsAPIService.class);
            call = bookingDetailsAPIService.fetchBookingDetails(""+loginModel.getUserid(),
                    loginModel.getCompanyName(), ""+loginModel.getCompanyId(), requestType);

        } else {
            String rideType = requestType;
            if(rideType.equalsIgnoreCase("alert")){
                rideType = "current";
            }
            guestBookingDetailsAPIService = RestClient.getClient().create(ApiHandler.GuestBookingDetailsAPIService.class);
            call = guestBookingDetailsAPIService.GuestfetchBookingDetails(loginModel.getUserid(),
                    loginModel.getCompanyId(), rideType, loginModel.getCustomerID());
        }
        call.enqueue(new Callback<BookingDetails>() {
            @Override
            public void onResponse(Call<BookingDetails> call, Response<BookingDetails> response) {
                if(listItems == null){
                    listItems = new ArrayList<>();
                }
                listItems.clear();
                if(response!=null && response.body()!=null && response.body().getData()!=null) {
                    listItems = response.body().getData();
             //      Toast.makeText(MyRidesActivity.this,listItems.get(1).getDutyStartDate(), Toast.LENGTH_SHORT).show();
                    Log.e(String.valueOf(MyRidesActivity.this),listItems.toString());
                }

                adapter.setListItems(listItems);
                Log.e(String.valueOf(MyRidesActivity.this),listItems.toString());
                hideProgress();
            }

            @Override
            public void onFailure(Call<BookingDetails> call, Throwable t) {
                //Log.e(TAG, "Got error : " + t.getLocalizedMessage());
                if(listItems == null){
                    listItems = new ArrayList<>();
                }
                listItems.clear();
                adapter.setListItems(listItems);
                hideProgress();
            }
        });
    }
}
