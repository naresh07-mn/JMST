package com.travel_track.solution.apihandler;

import android.graphics.Bitmap;
import android.widget.LinearLayout;

import com.travel_track.solution.model.BillingDetails;
import com.travel_track.solution.model.BookingDetails;
import com.travel_track.solution.model.CarGroupDetails;
import com.travel_track.solution.model.CityDetails;
import com.travel_track.solution.model.FeedbackModel;
import com.travel_track.solution.model.GoogleDistanceMatrixApiResponse;
import com.travel_track.solution.model.LoginDetails;
import com.travel_track.solution.model.StartTrip;
import com.travel_track.solution.model.TourCodeDetails;
import com.travel_track.solution.model.UploadResponce;
import com.travel_track.solution.model.UploadSignatureResponce;
import com.travel_track.solution.viewmodel.ParkingDutySlipDetails;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public class ApiHandler {

    public interface RegisterDevice {
        @GET("/RegisterDevice.aspx?")
        Call<Void> registerDevice(@Query("deviceId") String deviceId,
                                  @Query("pushToken") String pushToken,
                                  @Query("param1") String param1,
                                  @Query("param2") String param2);
    }

    public interface LoginAPIService {
        @GET("/LoginService.aspx?")
        Call<LoginDetails> fetchLoginDetails(
                @Query("username") String username,
                @Query("password") String password,
                @Query("deviceId") String deviceId);
    }

    public interface GuestBookingDetailsAPIService {
        @GET("/GuestViewBooking.aspx?")
        Call<BookingDetails> GuestfetchBookingDetails(@Query("userId") Integer userid,
                                                      @Query("OrgId") Integer orgid,
                                                      @Query("type") String type,
                                                      @Query("CustomerId") String CustomerId);
    }

    public interface BookingDetailsAPIService {
        @GET("/DriverBookingView.aspx?")
        Call<BookingDetails> fetchBookingDetails(@Query("userid") String userid,
                                                 @Query("CompanyName") String CompanyName,
                                                 @Query("orgid") String orgid,
                                                 @Query("type") String type);
    }

    public interface UpdateStartMeterAPIService {
        @GET("/UpdateStartMeter.aspx?")
        Call<StartTrip> UpdateStartMeter(@Query("StartMeter") String startMeter,
                                         @Query("CloseTime") String closeTime,
                                         @Query("startGarageMeter") String startGarageMeter,
                                         @Query("startGarageTime") String startGarageTime,
                                         @Query("startLat") String startLat,
                                         @Query("startLong") String startLong,
                                         @Query("startAddress") String startAddress,
                                         @Query("CompanyName") String CompanyName,
                                         @Query("orgid") String orgId,
                                         @Query("BookingId") String bookingId,
                                         @Query("WaitingTime") String waitingTime);
    }


    public interface UploadImageAPIServise {
        @Multipart
        @POST("/api/Upload/JMST/PostUserImage")
        Call<UploadResponce> upload(
                @PartMap Map<String, RequestBody> map);
    }

    public interface UploadSignaturAPIServise {

        @GET("/updateSignImage.aspx?")
        Call<UploadSignatureResponce> upload(
                       @Query("OrgId") String OrgId,
                @Query("BookingId") String BookingId);
    }


    public interface UploadMapBookingService {
        @GET("uploadMapImage.aspx?")
        Call<UploadResponce> sendMap(
                @Query("OrgId") String OrgId,
                @Query("bookingId") String bookingId);
    }

    public interface ParkingDutySlipImageAPIServise {
        @GET("parkingtollimages.aspx?")
        Call<ParkingDutySlipDetails> sendDutySlip(
                @Query("DutySlipNo") String DutySlipNo,
                @Query("CompanyID") String CompanyID);
    }

    public interface CloseDutyAPIService {
        @GET("/DutyClose.aspx?")
        Call<StartTrip> DutyClose(
                @Query("CloseMeter") String CloseMeter,
                @Query("CloseDate") String CloseDate,
                @Query("CloseTime") String CloseTime,
                @Query("closeGarageMeter") String closeGarageMeter,
                @Query("closeGarageTime") String closeGarageTime,
                @Query("Parking") String Parking,
                @Query("DND") String DND,
                @Query("TollTax") String TollTax,
                @Query("GtoG") String GtoG,
                @Query("otherDesc") String otherDesc,
                @Query("otherAmount") String otherAmount,
                @Query("waitingTime") float waitingTime,
                @Query("CompanyName") String CompanyName,
                @Query("OrgId") String OrgId,
                @Query("BookingId") String BookingId,
                @Query("routeString") String routeString,
                @Query("Address") String Address,
                @Query("DropLat") Double DropLat,
                @Query("DropLong") Double DropLong,
                @Query("Remark") String Remark);
    }

    public interface UpdateFeedbackAPIService {
        @GET("/UpdateFeedBack.aspx?")
        Call<FeedbackModel> updateFeedback(@Query("FeedBackRating") String FeedBackRating,
                                           @Query("FeedBackSummary") String FeedBackSummary,
                                           @Query("CompanyName") String CompanyName,
                                           @Query("orgid") Integer orgid,
                                           @Query("BookingId") String BookingId,
                                           @Query("Signature") String Signature);// converted to String from LinearLayout.created by naresh bhadana
    }

    public interface BillingDetailsApiService {
        @GET("/BillingSave.aspx?")
        Call<BillingDetails> billingSave(@Query("BookingID") String BookingId,
                                         @Query("orgid") Integer orgid);
    }

    public interface GetCity {
        @GET("ClientCityGet.aspx?")
        Call<CityDetails> getCity(@Query("CustomerId") String CustomerId,
                                  @Query("Companyid") Integer Companyid);
    }

    public interface GetTourCode {
        @GET("TourCodeGet.aspx?")
        Call<TourCodeDetails> getTourCode(@Query("OrgId") Integer OrgId);
    }

    public interface GetCarGroup {
        @GET("CarGroupGet.aspx?")
        Call<CarGroupDetails> getCarGroup();
    }

    public interface DoGuestBooking {
        @GET("AddBooking.aspx")
        Call<ResponseBody> doGuestBooking(@QueryMap HashMap<String, String> options);
    }

    public interface DoGuestBookings {
        @GET("/AddBooking.aspx")
        Call<ResponseBody> doGuestBooking(@Query("CustomerId") String CustomerId,
                                          @Query("City") String City,
                                          @Query("PaymentMode") String PaymentMode,
                                          @Query("CarGroup") String CarGroup,
                                          @Query("StartDate") String StartDate,
                                          @Query("ReportingAddress") String ReportingAddress,
                                          @Query("ReporingTime") String ReporingTime,
                                          @Query("CloseDate") String CloseDate,
                                          @Query("CloseTime") String CloseTime,
                                          @Query("BookingType") String BookingType,
                                          @Query("BookingStatus") String BookingStatus,
                                          @Query("GuestName") String GuestName,
                                          @Query("GuestContactNo") String GuestContactNo,
                                          @Query("CompanyId") String CompanyId,
                                          @Query("TourCode") String TourCode,
                                          @Query("BookedBy") String BookedBy,
                                          @Query("BookedByContactNo") String BookedByContactNo,
                                          @Query("FileCode") String FileCode,
                                          @Query("EmployeeID") String EmployeeID,
                                          @Query("DropLocation") String DropLocation,
                                          @Query("PickUpLatitude") String PickUpLatitude,
                                          @Query("PickUpLongitude") String PickUpLongitude,
                                          @Query("DropLatitude") String DropLatitude,
                                          @Query("DropLongitude") String DropLongitude,
                                          @Query("BookerId") String BookerId);
    }

    public interface PostLocationTracking {
        @GET("/TrackLocationService.aspx?")
        Call<Void> postLocation(@Query("deviceId") String deviceId,
                                @Query("employeeId") String employeeId,
                                @Query("latitude") String latitude,
                                @Query("longitude") String logitude,
                                @Query("param1") String param1,
                                @Query("param2") String param2
        );
    }  //@Query("Mode") String mode,

    public interface DistanceDuration {
        @GET("json?")
        Call<GoogleDistanceMatrixApiResponse> getMatrixData(
                @Query("origins") String origins,
                @Query("destinations") String destinations,
                @Query("key") String key);
    }
}

