package com.example.tuantu.week7_findplacenearby;

/**
 * Created by TUAN TU on 4/23/2016.
 */
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}

