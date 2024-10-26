package com.travel_track.solution.apihandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ImageProcessingHandler {

   public static final String BASE_URL = "  http://ttrack.travellerstrack.in";
    // public static final String BASE_URL = " http://cyberhawk.travellerstrack.in";
    private static Retrofit retrofit = null;

    public static Retrofit getClient(){
        if(retrofit == null){
            final GsonBuilder gsonBuilder = new GsonBuilder();
            final Gson gson = gsonBuilder.create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(new StringConverterFactory(GsonConverterFactory.create(gson)))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
