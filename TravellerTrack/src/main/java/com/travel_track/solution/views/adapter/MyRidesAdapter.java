package com.travel_track.solution.views.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.travel_track.solution.R;
import com.travel_track.solution.model.BookingModel;
import com.travel_track.solution.model.LoginModel;
import com.travel_track.solution.views.activities.BitmapHolder;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyRidesAdapter extends RecyclerView.Adapter <MyRidesAdapter.MyRidesHolder> {

    private final LayoutInflater inflater;
    Context context;

    List<BookingModel> arrayList = new ArrayList<>();

    public MyRidesAdapter(Context context, ArrayList<BookingModel> listItems) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setListItems(List<BookingModel> listItems){
        this.arrayList = listItems;
        notifyDataSetChanged();
    }

    @Override@NonNull
    public MyRidesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_my_rides_row, parent, false);
        return new MyRidesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRidesHolder holder, int position) {
        holder.name.setText("Name: " + arrayList.get(position).getGuestName());
        holder.booking_id.setText("Booking id: "+arrayList.get(position).getBookingNo());
        holder.address.setText(arrayList.get(position).getClientCompanyName());
        holder.pickup_date.setText("Pickup Date: " + arrayList.get(position).getPickUpDate());
        holder.car_no.setText("Car no: " + arrayList.get(position).getCarNo());
        holder.pickup_time.setText("Pickup Time: " + arrayList.get(position).getPickUpTime());
        BitmapHolder.getInstance().setStartDate(arrayList.get(position).getDutyStartDate());
        BitmapHolder.getInstance().setCloseDSate(arrayList.get(position).getDutyCloseDate());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    protected class MyRidesHolder extends RecyclerView.ViewHolder{
        TextView name, booking_id,car_no;
        TextView address;
        TextView pickup_date;
        TextView pickup_time;

        public MyRidesHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.guest_name);
            booking_id = itemView.findViewById(R.id.booking_id);
            car_no = itemView.findViewById(R.id.car_no);
            address = itemView.findViewById(R.id.address);
            pickup_date = itemView.findViewById(R.id.pickup_date);
            pickup_time = itemView.findViewById(R.id.pickup_time);
        }
    }

}
