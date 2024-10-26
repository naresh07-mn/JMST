package com.travel_track.solution.views.activities;
// created by naresh bhadana
import android.graphics.Bitmap;

public class BitmapHolder {
          private Bitmap bitmap=null;
          private Bitmap mapUpdate=null;
          private String startDate=null;
          private String closeDSate=null;
          private String pickUpTime=null;
    private static final BitmapHolder instance=new BitmapHolder();


    public BitmapHolder(){


    }

    public static BitmapHolder getInstance(){
        return instance;
    }



    public Bitmap getUpdatedBitmap() {return mapUpdate;}
    public void setUpdatedBitmap(Bitmap mapUpdate) {
        this.mapUpdate = mapUpdate;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getPickUpTime() {
        return pickUpTime;
    }

    public void setPickUpTime(String pickUpTime) {
        this.pickUpTime = pickUpTime;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getCloseDSate() {
        return closeDSate;
    }

    public void setCloseDSate(String closeDSate) {
        this.closeDSate = closeDSate;
    }
}
