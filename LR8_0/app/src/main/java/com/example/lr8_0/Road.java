package com.example.lr8_0;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Road {
    @SerializedName("routes")
    List<Way> routes;
    @SerializedName("status")
    String status;
    String getPoints() {
        return this.routes.get(0).overviewPolyline.points;
    }

    class Way{
        @SerializedName("overview_polyline")
        OverviewPolyline overviewPolyline;
    }

    class OverviewPolyline{
        String points;
    }
}
