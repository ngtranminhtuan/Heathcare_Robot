package com.example.tuantu.week7_findplacenearby;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tuantu.week7_findplacenearby.DirectionFinder;
import com.example.tuantu.week7_findplacenearby.DirectionFinderListener;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListAdapter extends ArrayAdapter<String>  implements DirectionFinderListener {
    public static int flag = 0;
    private int resource;

    static Context c = null;
    static Context c1 = null;

    static ProgressDialog progressDialog;
    static TextView distance;
    static int number = 0;
   ArrayList<String> dt = new ArrayList<String>();
   // public static List<HashMap<String, String>> result;

    public ListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ListAdapter(Context context, int resource, ArrayList<String> items) {
        super(context, resource, items);
        this.resource = resource;
        c = context;
        c1 = this.getContext();

    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(resource, null);
        }
        Location locationA = new Location("point A");
        locationA.setLatitude(MapsActivity.latitude);
        locationA.setLongitude(MapsActivity.longitude);

        Location locationB = new Location("point B");
        if (flag == 0) {
            locationB.setLatitude(Double.parseDouble(placeResult.result.get(position).get("lat")));
            locationB.setLongitude(Double.parseDouble(placeResult.result.get(position).get("lng")));


            TextView placeName = (TextView) v.findViewById(R.id.placeName);
            TextView placeAddr = (TextView) v.findViewById(R.id.placeAddr);
            ImageButton btnFindPath = (ImageButton) v.findViewById(R.id.btnFindPath);
            distance = (TextView) v.findViewById(R.id.distance);
            placeName.setText(placeResult.result.get(position).get("place_name"));
            if (MapsActivity.searchFlag == 1)
                placeAddr.setText(placeResult.result.get(position).get("address"));
            else placeAddr.setText(placeResult.result.get(position).get("vicinity"));
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.CEILING);
            distance.setText((df.format(locationA.distanceTo(locationB) / 1000)) + " Km");

            btnFindPath.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SlidingAdapter.ratingBar.setVisibility(View.GONE);
                    SlidingAdapter.time.setVisibility(View.GONE);
                    SlidingAdapter.direction.setVisibility(View.GONE);
                    //MapsActivity.name.setText("Đang định tuyến...");
                    SlidingAdapter.panelAddr.setText("");
                    // MapsActivity.li = (LinearLayout)findViewById(R.id.showDetails);
                    MapsActivity.panel.setBackgroundColor(Color.parseColor("#006600"));
                    String destination = placeResult.result.get(position).get("lat") + "," + placeResult.result.get(position).get("lng");
                    String origin = "";
                    try {
                        new DirectionFinder(ListAdapter.this, origin, destination).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(c, Place_Detail.class);
                    c.startActivity(intent);
                    Place_Detail.placeID = placeResult.result.get(position).get("place_id");
                }
            });
        }else
        {
            locationB.setLatitude(Double.parseDouble(Life.result.get(position).get("lat")));
            locationB.setLongitude(Double.parseDouble(Life.result.get(position).get("lng")));


            TextView placeName = (TextView) v.findViewById(R.id.placeName);
            TextView placeAddr = (TextView) v.findViewById(R.id.placeAddr);
            ImageButton btnFindPath = (ImageButton) v.findViewById(R.id.btnFindPath);
            distance = (TextView) v.findViewById(R.id.distance);
            placeName.setText(Life.result.get(position).get("place_name"));
            if (Life.result.get(position).get("address") != "")
                placeAddr.setText(Life.result.get(position).get("address"));
            else placeAddr.setText(Life.result.get(position).get("vicinity"));
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.CEILING);
            distance.setText((df.format(locationA.distanceTo(locationB) / 1000)) + " Km");

            btnFindPath.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SlidingAdapter.ratingBar.setVisibility(View.GONE);
                    SlidingAdapter.time.setVisibility(View.GONE);
                    SlidingAdapter.direction.setVisibility(View.GONE);
                    //MapsActivity.name.setText("Đang định tuyến...");
                    SlidingAdapter.panelAddr.setText("");
                    // MapsActivity.li = (LinearLayout)findViewById(R.id.showDetails);
                    MapsActivity.panel.setBackgroundColor(Color.parseColor("#006600"));
                    String destination = Life.result.get(position).get("lat") + "," + Life.result.get(position).get("lng");
                    String origin = "";
                    try {
                        new DirectionFinder(ListAdapter.this, origin, destination).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(c, Place_Detail.class);
                    c.startActivity(intent);
                    Place_Detail.placeID = Life.result.get(position).get("place_id");
                }
            });
        }
        return v;
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(c, "Please wait.",
                "Finding direction..!", true);

        if (MapsActivity.originMarkers != null) {
            for (Marker marker : MapsActivity.originMarkers) {
                marker.remove();
            }
        }

        if (MapsActivity.destinationMarkers != null) {
            for (Marker marker : MapsActivity.destinationMarkers) {
                marker.remove();
            }
        }

        if (MapsActivity.polylinePaths != null) {
            for (Polyline polyline:MapsActivity.polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        MapsActivity.polylinePaths = new ArrayList<>();
        MapsActivity.originMarkers = new ArrayList<>();
        MapsActivity.destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            MapsActivity.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            SlidingAdapter.name.setText(route.distance.text);
            SlidingAdapter.panelAddr.setText("Thời gian đi ước tính là: " + route.duration.text);
            SlidingAdapter.rateCount.setText("Nhấn vào để xem chi tiết điểm đến");
            MapsActivity.slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

            //((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            //((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            MapsActivity.originMarkers.add(MapsActivity.mMap.addMarker(new MarkerOptions()
                    .title(route.startAddress)
                    .position(route.startLocation)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.naviconmini))
                    .snippet("start")));
            /*destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));*/

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            MapsActivity.isShowPath = 1;
            ArrayList<String> temp = new ArrayList<String>();
            for (int i = 0; i <= MapsActivity.stepDirection.length(); i++)
            {
                temp.add("");
            }
            MapsActivity.distance = route.distance.text;
            MapsActivity.esTime = route.duration.text;

            MapsActivity.mPager.setAdapter(new SlidingAdapter(c, temp));
            MapsActivity.polylinePaths.add(MapsActivity.mMap.addPolyline(polylineOptions));
            Toast.makeText(c, "Đã định tuyến xong, nhấn Back để xem", Toast.LENGTH_LONG).show();


        }
    }
    public static float round(float value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (float) tmp / factor;
    }
}
