package com.example.tuantu.week7_findplacenearby;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class SlidingAdapter extends PagerAdapter {
    private static ArrayList<String> IMAGES;
    public static ImageButton direction = null;
    public static RatingBar ratingBar = null;
    public static TextView rateCount = null;
    public static TextView panelAddr = null;
    public static TextView time = null;
    public static int isFinding = 0;
    private LayoutInflater inflater;
    private Context context;
    public static TextView name = null;
    //static ImageView imageView = null;

    public SlidingAdapter(Context context,ArrayList<String> IMAGES) {
        this.context = context;
        this.IMAGES=IMAGES;
        inflater = LayoutInflater.from(context);




    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return IMAGES.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        // View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);

        View imageLayout = inflater.inflate(R.layout.sliding_panel, view, false);

        assert imageLayout != null;

        name = (TextView) imageLayout.findViewById(R.id.name);
        ratingBar = (RatingBar)imageLayout.findViewById(R.id.ratingBar2);
        rateCount = (TextView) imageLayout.findViewById(R.id.rateCount);
        panelAddr = (TextView)imageLayout.findViewById(R.id.textAddr1);
        time = (TextView)imageLayout.findViewById(R.id.textTime);
        direction = (ImageButton)imageLayout.findViewById(R.id.direction);

        if (MapsActivity.isShowPath == 0) {
            LinearLayout li = (LinearLayout) imageLayout.findViewById(R.id.sliding_panel);
            if (MapsActivity.startFlag == 1) {
                if (MapsActivity.placeType.equals("restaurant")) {
                    name.setText("Chào mừng!");
                    panelAddr.setText("Bắt đầu tìm kiếm địa điểm ăn uống, khách sạn yêu thích của bạn ngay bây giờ");
                }
                else if (MapsActivity.placeType.equals("hospital")) {
                    name.setText("Chào mừng!");
                    panelAddr.setText("Bắt đầu tìm kiếm Bệnh viện");
                }
                else if (MapsActivity.placeType.equals("atm")) {
                    name.setText("Chào mừng!");
                    panelAddr.setText("Bắt đầu tìm kiếm ATM");
                }
                else if (MapsActivity.placeType.equals("bus_station")) {
                    name.setText("Chào mừng!");
                    panelAddr.setText("Bắt đầu tìm kiếm trạm xe buýt");
                }
                rateCount.setVisibility(View.GONE);
                //panelAddr.setVisibility(View.GONE);
                ratingBar.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
                direction.setVisibility(View.GONE);
            }
            li.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MapsActivity.startFlag == 0 && !MapsActivity.id.equals("start")) {
                        Intent intent = new Intent(context, Place_Detail.class);
                        context.startActivity(intent);
                        Place_Detail.placeID = placeResult.result.get(Integer.parseInt(MapsActivity.id)).get("place_id");
                    }
                }
            });


            SlidingAdapter.direction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SlidingAdapter.ratingBar.setVisibility(View.GONE);
                    SlidingAdapter.time.setVisibility(View.GONE);
                    SlidingAdapter.direction.setVisibility(View.GONE);
                    SlidingAdapter.name.setText("Đang định tuyến...");
                    SlidingAdapter.panelAddr.setText("");

                    MapsActivity.panel.setBackgroundColor(Color.parseColor("#006600"));

                    String destination = placeResult.result.get(Integer.parseInt(MapsActivity.id)).get("lat") + "," + placeResult.result.get(Integer.parseInt(MapsActivity.id)).get("lng");
                    String origin = "";
                    try {
                        new DirectionFinder((MapsActivity) context, origin, destination).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    MapsActivity.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(placeResult.result.get(Integer.parseInt(MapsActivity.id)).get("lat")), Double.parseDouble(placeResult.result.get(Integer.parseInt(MapsActivity.id)).get("lng"))), 17));
                }
            });
        }

        else
        {
            if (position != 0) {
                name.setVisibility(View.GONE);
                rateCount.setVisibility(View.GONE);
                ratingBar.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
                direction.setVisibility(View.GONE);

                try {

                Double lat = 0d;
                Double lng = 0d;
                    if (position > 1) {
                        lat = Double.parseDouble(MapsActivity.stepDirection.getJSONObject(position - 2).getJSONObject("start_location").getString("lat"));
                        lng = Double.parseDouble(MapsActivity.stepDirection.getJSONObject(position - 2).getJSONObject("start_location").getString("lng"));
                        CameraPosition SYDNEY =
                                new CameraPosition.Builder().target(new LatLng(lat, lng))
                                        .zoom(18f)
                                        .bearing(60)
                                        .tilt(25)
                                        .build();
                        changeCamera(CameraUpdateFactory.newCameraPosition(SYDNEY), new GoogleMap.CancelableCallback() {
                            @Override
                            public void onFinish() {

                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                    }
                    panelAddr.setText(Html.fromHtml(MapsActivity.stepDirection.getJSONObject(position - 1).getString("html_instructions")));

                } catch (Exception e) {
                }
            }
            else
            {
                if (name.getVisibility() == View.GONE)name.setVisibility(View.VISIBLE);
                if (rateCount.getVisibility() == View.GONE) rateCount.setVisibility(View.VISIBLE);
                ratingBar.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
                direction.setVisibility(View.GONE);
                SlidingAdapter.name.setText(MapsActivity.distance);
                SlidingAdapter.panelAddr.setText("Thời gian đi ước tính là: " + MapsActivity.esTime);
                SlidingAdapter.rateCount.setText("Vuốt sang trái để bắt đầu");
            }

        }
        view.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
    private void changeCamera(CameraUpdate update, GoogleMap.CancelableCallback callback) {
        MapsActivity.mMap.animateCamera(update, 3, callback);
    }

}