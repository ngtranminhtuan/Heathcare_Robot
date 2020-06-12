package com.example.tuantu.week7_findplacenearby;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, DirectionFinderListener, SensorEventListener {

    public static JSONArray stepDirection = null;
    public static String distance = "";
    public static String esTime = "";
    public static SlidingUpPanelLayout slidingLayout;
    public static int isShowPath = 0;
    LinearLayout details = null;
    public static LinearLayout panel = null;
    public static ViewPager mPager;
    public static String placeType = "restaurant";
    ImageButton btnShare = null;
    ImageButton compass = null;
    static Marker previous = null;
    static String id = "";
    ImageButton checkin = null;
    static int startFlag = 0;
    static int compassFlag = 0;
    ImageButton btnDetail = null;
    public static List<Marker> destinationMarkers = new ArrayList<>();
    public static List<Polyline> polylinePaths = new ArrayList<>();
    static ProgressDialog progressDialog;
    public static List<Marker> originMarkers = new ArrayList<>();
    private static SensorManager sm;
    ImageButton btnSave = null;
    ImageButton btnRemider = null;
    ImageButton btnComment = null;
    ImageButton topic = null;
    public  static  Context _context;

    //----------------------------------------
    public static GoogleMap mMap;
    public static int searchFlag = 0;
    public static int flag;

    public static double latitude = 10.7725616;
    public static double longitude = 106.6958022;
    private static final int CONTENT_VIEW_ID = 10101010;
    EditText placeText;
    private int PROXIMITY_RADIUS = 5000;
    public static final String GOOGLE_API_KEY = "AIzaSyBHfDnaWtEfXJVVp-okrCzU0kv7xGZzkVc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
_context = this;
        placeText = (EditText) findViewById(R.id.placeText);
        Button btnFind = (Button) findViewById(R.id.btnFind);
        ImageButton nav = (ImageButton) findViewById(R.id.nav);
        ImageButton res = (ImageButton) findViewById(R.id.btnResult);
        topic = (ImageButton) findViewById(R.id.btnTopic);

        TextView txtTemp = (TextView)findViewById(R.id.txtTemp);
        if (flag == 1)
        {
            topic.setVisibility(View.GONE);
            txtTemp.setVisibility(View.GONE);
        }
        else
        {
            topic.setVisibility(View.VISIBLE);
            txtTemp.setVisibility(View.VISIBLE);
        }
        //-------------------------------Testing slideup--------------------------------
        //Test listView

        ArrayList<String> tempArr = new ArrayList<String>();

        tempArr.add("");


//---------------------------------------------------------------------------------------
        //stopService(new Intent(this, PowerService.class));
        Setting.databseProcess();
        startService(new Intent(this, PowerService.class));

        panel = (LinearLayout)findViewById(R.id.showDetails);
        slidingLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        SlidingAdapter.name = (TextView)findViewById(R.id.name);
        details = (LinearLayout)findViewById(R.id.showDetails);

        btnDetail = (ImageButton)findViewById(R.id.imageButtonDetail);
        compass = (ImageButton)findViewById(R.id.btnCompass);
        checkin = (ImageButton)findViewById(R.id.imageButtonCheckin);
        btnShare = (ImageButton)findViewById(R.id.imageButtonShare);
        btnSave = (ImageButton)findViewById(R.id.imageButtonSave);
        btnComment = (ImageButton)findViewById(R.id.imageButtonComment);
        btnRemider = (ImageButton)findViewById(R.id.imageButtonReminder);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startFlag == 0 && !id.equals("start")) {
                    PlaceDb.PlaceDbHelper placeDbHelper = new PlaceDb.PlaceDbHelper(getApplicationContext());
                    SQLiteDatabase placeDb = placeDbHelper.getWritableDatabase();

                    ContentValues values1 = new ContentValues();
                    values1.put(PlaceDb.PlaceDes.NAME, placeResult.result.get(Integer.parseInt(id)).get("place_name"));
                    values1.put(PlaceDb.PlaceDes.ADDR, placeResult.result.get(Integer.parseInt(id)).get("address"));
                    values1.put(PlaceDb.PlaceDes.VICINITY, placeResult.result.get(Integer.parseInt(id)).get("vicinity"));
                    values1.put(PlaceDb.PlaceDes.LONGITUDE, placeResult.result.get(Integer.parseInt(id)).get("lng"));
                    values1.put(PlaceDb.PlaceDes.LATITUDE, placeResult.result.get(Integer.parseInt(id)).get("lat"));
                    values1.put(PlaceDb.PlaceDes.PLACEID, placeResult.result.get(Integer.parseInt(id)).get("place_id"));

                    placeDb.insert(
                            PlaceDb.PlaceDes.TABLE_NAME,
                            "",
                            values1);
                    Toast.makeText(getApplicationContext(), "Đã thêm vào danh sách yêu thích", Toast.LENGTH_LONG).show();
                }
            }
        });
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Chức năng đang được phát triển", Toast.LENGTH_LONG).show();
            }
        });
        btnRemider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startFlag == 0 && !id.equals("start")) {
                    Intent intent = new Intent(Intent.ACTION_EDIT);
                    intent.setType("vnd.android.cursor.item/event");
                    if (placeResult.result.get(Integer.parseInt(id)).get("address") != "")
                        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, placeResult.result.get(Integer.parseInt(id)).get("place_name") + " " + placeResult.result.get(Integer.parseInt(id)).get("address"));
                    else
                        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, placeResult.result.get(Integer.parseInt(id)).get("place_name") + " " + placeResult.result.get(Integer.parseInt(id)).get("vicinity"));
                    startActivity(intent);
                }
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startFlag == 0 && ! id.equals("start")) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = "Here is the share content body";
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Shared by Tuan Tu");
                    if (placeResult.result.get(Integer.parseInt(id)).get("place_name") != "")
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, placeResult.result.get(Integer.parseInt(id)).get("place_name"));
                    startActivity(Intent.createChooser(sharingIntent, "Chia sẻ qua"));
                }
            }
        });
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startFlag == 0 && ! id.equals("start")) {
                    Intent intent = new Intent(getApplicationContext(), Place_Detail.class);
                    startActivity(intent);
                    Place_Detail.placeID = placeResult.result.get(Integer.parseInt(id)).get("place_id");
                }
            }
        });

        btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startFlag == 0 && !id.equals("start")) {
                    Intent intent = new Intent(getApplicationContext(), Place_Detail.class);
                    startActivity(intent);
                    Place_Detail.placeID = placeResult.result.get(Integer.parseInt(id)).get("place_id");
                }
            }
        });
        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Uri number = Uri.parse("tel:" + phone_number.getText());
                Intent callIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(callIntent);
            }
        });
        try {

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        compass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (compassFlag == 0) {
                    sm = (SensorManager) getSystemService(SENSOR_SERVICE);
                    sm.registerListener(MapsActivity.this, sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_NORMAL);
                    compassFlag = 1;
                }
                else
                {
                    sm.unregisterListener(MapsActivity.this);
                    compassFlag = 0;
                }
            }
        });

            res.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (startFlag == 0) {
                        Intent intent = new Intent(getApplicationContext(), placeResult.class);
                        startActivity(intent);
                    }
                    else Toast.makeText(getApplicationContext(), "Không có địa điểm", Toast.LENGTH_LONG).show();
                }
            });

        nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkOnline() && isOnline()) {
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (loc == null) {
// fall back to network if GPS is not available
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                    if (loc != null) {
                        onDirectionFinderStart();
                        isShowPath = 0;
                        ArrayList<String> temp = new ArrayList<String>();
                        temp.add("");
                        mPager.setAdapter(new SlidingAdapter(MapsActivity.this, temp));

                        LinearLayout li = (LinearLayout) findViewById(R.id.showDetails);
                        li.setBackgroundColor(Color.parseColor("#1691bc"));
                        if (SlidingAdapter.rateCount.getVisibility() == View.GONE)
                            SlidingAdapter.rateCount.setVisibility(View.VISIBLE);
                        //if (panelAddr.getVisibility() == View.GONE) panelAddr.setVisibility(View.VISIBLE);
                        if (SlidingAdapter.ratingBar.getVisibility() == View.GONE)
                            SlidingAdapter.ratingBar.setVisibility(View.VISIBLE);
                        if (SlidingAdapter.time.getVisibility() == View.GONE) SlidingAdapter.time.setVisibility(View.VISIBLE);
                        if (SlidingAdapter.direction.getVisibility() == View.GONE)
                            SlidingAdapter.direction.setVisibility(View.VISIBLE);
                        if (SlidingAdapter.panelAddr.getVisibility() == View.GONE)
                            SlidingAdapter.panelAddr.setVisibility(View.VISIBLE);
                        if (SlidingAdapter.name.getVisibility() == View.GONE)
                            SlidingAdapter.name.setVisibility(View.VISIBLE);

                        startFlag = 0;
                        searchFlag = 0;

                        latitude = loc.getLatitude();
                        longitude = loc.getLongitude();

                        //-----------------------Place nearby------------------------
                        //String type = placeText.getText().toString();
                        // PlacesDisplayTask.isDetails = 0;
                        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                        googlePlacesUrl.append("location=" + latitude + "," + longitude);
                        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
                        googlePlacesUrl.append("&types=" + placeType);
                        googlePlacesUrl.append("&sensor=true");
                        googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);
                        LatLng latLng = new LatLng(latitude, longitude);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
                        GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
                        Object[] toPass = new Object[2];
                        toPass[0] = mMap;
                        toPass[1] = googlePlacesUrl.toString();
                        googlePlacesReadTask.execute(toPass);
                        //previous = null;
                        if (slidingLayout.isShown())
                            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

                    } else
                        Toast.makeText(getApplicationContext(), "Không tìm thấy địa điểm", Toast.LENGTH_LONG).show();

                } else
                    Toast.makeText(getApplicationContext(), "Không có kết nối mạng", Toast.LENGTH_LONG).show();
            }
        });

        btnFind.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isShowPath = 0;
                onDirectionFinderStart();
                ArrayList<String> temp = new ArrayList<String>();
                temp.add("");
                mPager.setAdapter(new SlidingAdapter(MapsActivity.this, temp));
                if (isNetworkOnline() && isOnline()) {
                    LinearLayout li = (LinearLayout) findViewById(R.id.showDetails);
                    li.setBackgroundColor(Color.parseColor("#1691bc"));
                    startFlag = 0;
                    if (SlidingAdapter.rateCount.getVisibility() == View.GONE)
                        SlidingAdapter.rateCount.setVisibility(View.VISIBLE);
                    //if (panelAddr.getVisibility() == View.GONE) panelAddr.setVisibility(View.VISIBLE);
                    if (SlidingAdapter.ratingBar.getVisibility() == View.GONE)
                        SlidingAdapter.ratingBar.setVisibility(View.VISIBLE);
                    if (SlidingAdapter.time.getVisibility() == View.GONE) SlidingAdapter.time.setVisibility(View.VISIBLE);
                    if (SlidingAdapter.direction.getVisibility() == View.GONE)
                        SlidingAdapter.direction.setVisibility(View.VISIBLE);
                    if (SlidingAdapter.panelAddr.getVisibility() == View.GONE)
                        SlidingAdapter.panelAddr.setVisibility(View.VISIBLE);
                    searchFlag = 1;

                    String type = placeText.getText().toString();
                    StringBuilder places = new StringBuilder(type);

                    for (int i = 0; i < places.length(); i++)
                        if (places.charAt(i) == ' ') places.setCharAt(i, '+');

                    PlacesDisplayTask.isDetails = 0;
                    StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json?");
                    googlePlacesUrl.append("query=" + places);
                    googlePlacesUrl.append("&location=" +  10.7725616 + "," + 106.6958022);
                    googlePlacesUrl.append("&radius=20000");
                    googlePlacesUrl.append("&language=vi");
                    googlePlacesUrl.append("&types=" + placeType);
                    googlePlacesUrl.append("&sensor=true");
                    googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);

                    GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
                    Object[] toPass = new Object[2];
                    toPass[0] = mMap;
                    toPass[1] = googlePlacesUrl.toString();
                    googlePlacesReadTask.execute(toPass);
                    previous = null;
                    if (slidingLayout.isShown())
                        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                }else Toast.makeText(getApplicationContext(), "Không có kết nối mạng", Toast.LENGTH_LONG).show();
            }

        });
        panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        topic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Topic.class);
                startActivity(intent);
            }
        });
        mPager = (ViewPager) findViewById(R.id.slidingPager);
        mPager.setAdapter(new SlidingAdapter(MapsActivity.this, tempArr));

        //-------------------------

       /* String[] projection2 = {
                PlaceDb.PlaceDes._ID,
                PlaceDb.PlaceDes.NAME,
                PlaceDb.PlaceDes.ADDR,
                PlaceDb.PlaceDes.LONGITUDE,
                PlaceDb.PlaceDes.LATITUDE,
                PlaceDb.PlaceDes.PLACEID};
        Cursor placeCursor = placeDb.query(
                PlaceDb.PlaceDes.TABLE_NAME,  // The table to query
                projection2,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );
        //If database not exists
        if (placeCursor.getCount() <= 0)
        {

        }*/
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng benthanh = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(benthanh, 15));
        startFlag = 1;
      /* */


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                    isShowPath = 0;
                    ArrayList<String> temp = new ArrayList<String>();
                    temp.add("");
                    mPager.setAdapter(new SlidingAdapter(MapsActivity.this, temp));

                    LinearLayout li = (LinearLayout) findViewById(R.id.showDetails);
                    li.setBackgroundColor(Color.parseColor("#1691bc"));
                    if (SlidingAdapter.rateCount.getVisibility() == View.GONE)
                        SlidingAdapter.rateCount.setVisibility(View.VISIBLE);
                if (SlidingAdapter.name.getVisibility() == View.GONE)
                    SlidingAdapter.name.setVisibility(View.VISIBLE);
                    if (SlidingAdapter.panelAddr.getVisibility() == View.GONE)
                        SlidingAdapter.panelAddr.setVisibility(View.VISIBLE);
                    if (SlidingAdapter.ratingBar.getVisibility() == View.GONE)
                        SlidingAdapter.ratingBar.setVisibility(View.VISIBLE);
                    if (SlidingAdapter.time.getVisibility() == View.GONE) SlidingAdapter.time.setVisibility(View.VISIBLE);
                    if (SlidingAdapter.direction.getVisibility() == View.GONE)
                        SlidingAdapter.direction.setVisibility(View.VISIBLE);
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                id = marker.getSnippet();
                try {
                    if (!id.equals("start") && isNetworkOnline() && isOnline()) {

                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }


                    if (id.equals("start"))
                    {
                        SlidingAdapter.name.setText("Vị trí hiện tại");
                        SlidingAdapter.panelAddr.setText(marker.getTitle());
                        SlidingAdapter.rateCount.setVisibility(View.GONE);
                        SlidingAdapter.ratingBar.setVisibility(View.GONE);
                        SlidingAdapter.direction.setVisibility(View.GONE);
                        SlidingAdapter.time.setVisibility(View.GONE);
                    }
                    else {
                        try {
                            if (previous != null)
                                previous.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        previous = marker;
                        SlidingAdapter.name.setText(placeResult.result.get(Integer.parseInt(id)).get("place_name"));
                        if (placeResult.result.get(Integer.parseInt(id)).get("rating") != "") {
                            SlidingAdapter.rateCount.setText(placeResult.result.get(Integer.parseInt(id)).get("rating"));
                            SlidingAdapter.ratingBar.setRating(Float.parseFloat(placeResult.result.get(Integer.parseInt(id)).get("rating")));
                        } else {
                            SlidingAdapter.rateCount.setText("");
                            SlidingAdapter.ratingBar.setRating(0);
                        }

                        if (MapsActivity.searchFlag == 1)
                            SlidingAdapter.panelAddr.setText(placeResult.result.get(Integer.parseInt(id)).get("address"));
                        else
                            SlidingAdapter.panelAddr.setText(placeResult.result.get(Integer.parseInt(id)).get("vicinity"));
                    }
                return true;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                isShowPath = 0;
                ArrayList<String> temp = new ArrayList<String>();
                temp.add("");
                mPager.setAdapter(new SlidingAdapter(MapsActivity.this, temp));

                if (SlidingAdapter.name.getVisibility() == View.GONE)
                    SlidingAdapter.name.setVisibility(View.VISIBLE);
                if (startFlag == 0) {
                    if (previous != null)
                        previous.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    previous = null;
                    onDirectionFinderStart();
                    if (slidingLayout.isShown())
                        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                }
            }
        });
    }
    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }



    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    private View.OnClickListener onHideListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hide sliding layout
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            }
        };
    }
    @Override
    public void onDirectionFinderStart() {


        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {

        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();



        for (Route route : routes) {
            MapsActivity.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            SlidingAdapter.name.setText(route.distance.text);
            SlidingAdapter.panelAddr.setText("Thời gian đi ước tính là: " + route.duration.text);
            SlidingAdapter.rateCount.setText("Vuốt sang trái để bắt đầu");
            distance = route.distance.text;
            esTime = route.duration.text;
            originMarkers.add(MapsActivity.mMap.addMarker(new MarkerOptions()
                    .title(route.startAddress)
                    .position(route.startLocation)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.naviconmini))
                    .snippet("start")));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(MapsActivity.mMap.addPolyline(polylineOptions));
        }
        isShowPath = 1;
        ArrayList<String> temp = new ArrayList<String>();
        for (int i = 0; i <= stepDirection.length(); i++)
        {
            temp.add("");
        }

        mPager.setAdapter(new SlidingAdapter(MapsActivity.this, temp));
    }
    public boolean isNetworkOnline() {
        boolean status=false;
        try{
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
                status= true;
            }else {
                netInfo = cm.getNetworkInfo(1);
                if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
                    status= true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return status;

    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//
//        if (slidingLayout.isShown()) slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
//        boolean ret = super.dispatchTouchEvent(event);
//        return ret;
//
//    }


    private void updateCamera(float bearing) {
        CameraPosition oldPos = mMap.getCameraPosition();

        CameraPosition pos = CameraPosition.builder(oldPos).bearing(bearing).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));

    }
private float[] mRotationMatrix = new float[16];
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(
                    mRotationMatrix , event.values);
            float[] orientation = new float[3];
            SensorManager.getOrientation(mRotationMatrix, orientation);
            float bearing = (float) Math.toDegrees(orientation[0]);
            updateCamera(bearing);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (compassFlag == 1)

            sm.registerListener(MapsActivity.this, sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_NORMAL);

    }


    public static final class PlaceDb {
        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ",";
        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + PlaceDes.TABLE_NAME + " (" +
                        PlaceDes._ID + " INTEGER PRIMARY KEY," +
                        PlaceDes.NAME + TEXT_TYPE + COMMA_SEP +
                        PlaceDes.ADDR + TEXT_TYPE + COMMA_SEP+
                        PlaceDes.VICINITY + TEXT_TYPE + COMMA_SEP +
                        PlaceDes.LONGITUDE + TEXT_TYPE + COMMA_SEP +
                        PlaceDes.LATITUDE + TEXT_TYPE + COMMA_SEP +
                        PlaceDes.PLACEID + TEXT_TYPE + ")";
        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + PlaceDes.TABLE_NAME;
        // To prevent someone from accidentally instantiating the contract class,
        // give it an empty constructor.

        public PlaceDb() {
        }

        /* Inner class that defines the table contents */
        public static abstract class PlaceDes implements BaseColumns {
            public static final String TABLE_NAME = "Place";
            public static final String NAME = "placename";
            public static final String ADDR = "address";
            public static final String VICINITY = "vicinity";
            public static final String LONGITUDE = "longitude";
            public static final String LATITUDE = "latitude";
            public static final String PLACEID = "placeid";

        }

        public static class PlaceDbHelper extends SQLiteOpenHelper {
            // If you change the database schema, you must increment the database version.
            public static final int DATABASE_VERSION = 1;
            public static final String DATABASE_NAME = "Place.db";

            public PlaceDbHelper(Context context) {
                super(context, DATABASE_NAME, null, DATABASE_VERSION);
            }

            public void onCreate(SQLiteDatabase db) {
                db.execSQL(SQL_CREATE_ENTRIES);
            }

            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                // This database is only a cache for online data, so its upgrade policy is
                // to simply to discard the data and start over
                db.execSQL(SQL_DELETE_ENTRIES);
                onCreate(db);
            }

            public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                onUpgrade(db, oldVersion, newVersion);
            }
        }
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //stopService(new Intent(this, PowerService.class));
    }

}
