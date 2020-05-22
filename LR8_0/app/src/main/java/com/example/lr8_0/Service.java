package com.example.lr8_0;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Service {
    @GET("place/textsearch/json?")
    Call<Results> getAddress(@Query("query") String address,
                             @Query("key") String key);

    @GET("directions/json?")
    Call<Road> getWay(@Query("origin") String origin,
                         @Query("destination") String destination,
                         @Query("key") String key);
}