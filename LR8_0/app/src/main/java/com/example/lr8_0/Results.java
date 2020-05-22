package com.example.lr8_0;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Results {
    @SerializedName("results")
    List<Adress> adressList;

    class Adress{
        @SerializedName("formatted_address")
        String name;
        @SerializedName("geometry")
        Geometry geometry;

        class Geometry {
            @SerializedName("location")
            Location location;

            class Location{
                @SerializedName("lat")
                Double lat;
                @SerializedName("lng")
                Double lng;
            }
        }
    }
}
